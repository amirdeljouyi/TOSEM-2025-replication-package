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

public class WordsToSentencesAnnotator_5_GPTLLMTest {

  @Test
  public void testAnnotateThrowsNullPointerException() throws Throwable  {
    CoreLabel coreLabel0 = new CoreLabel();
    coreLabel0.setWord("one");
    CoreLabel coreLabel1 = new CoreLabel();
    coreLabel1.setWord("two");
    CoreLabel[] coreLabelArray0 = new CoreLabel[2];
    coreLabelArray0[0] = coreLabel0;
    coreLabelArray0[1] = coreLabel1;
    List<CoreLabel> list0 = (List<CoreLabel>)Arrays.asList(coreLabelArray0);
    Annotation annotation0 = new Annotation("one two");
    Class<CoreAnnotations.TokensAnnotation> class0 = CoreAnnotations.TokensAnnotation.class;
    annotation0.set(class0, list0);
    WordsToSentencesAnnotator wordsToSentencesAnnotator0 = new WordsToSentencesAnnotator();
    // Throws NullPointerException: null
    // Undeclared exception!
    wordsToSentencesAnnotator0.annotate(annotation0);
  }

 @Test
  public void testNonSplitterCreatesSingleSentence() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    String text = "Hello world.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setValue("Hello");
    token1.setOriginalText("Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world.");
    token2.setValue("world.");
    token2.setOriginalText("world.");
    token2.set(CoreAnnotations.TextAnnotation.class, "world.");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token2.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token2.set(CoreAnnotations.AfterAnnotation.class, " ");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertEquals("Hello world.", sentence.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testEmptyTokensShouldThrowException() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    Annotation document = new Annotation("");

    try {
      annotator.annotate(document);
      fail("Expected IllegalArgumentException when TokensAnnotation is missing");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unable to find words/tokens"));
    }
  }
@Test
  public void testNewlineSplitterCreatesMultipleSentences() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

    String text = "First.\nSecond.\n\nThird.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("First.");
    token1.setOriginalText("First.");
    token1.set(CoreAnnotations.TextAnnotation.class, "First.");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("\n");
    token2.setOriginalText("\n");
    token2.set(CoreAnnotations.TextAnnotation.class, "\n");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Second.");
    token3.setOriginalText("Second.");
    token3.set(CoreAnnotations.TextAnnotation.class, "Second.");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("\n");
    token4.setOriginalText("\n");
    token4.set(CoreAnnotations.TextAnnotation.class, "\n");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token5 = new CoreLabel();
    token5.setWord("\n");
    token5.setOriginalText("\n");
    token5.set(CoreAnnotations.TextAnnotation.class, "\n");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    token5.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token6 = new CoreLabel();
    token6.setWord("Third.");
    token6.setOriginalText("Third.");
    token6.set(CoreAnnotations.TextAnnotation.class, "Third.");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);
    token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(3, sentences.size());

    assertTrue(sentences.get(0).get(CoreAnnotations.TextAnnotation.class).contains("First"));
    assertTrue(sentences.get(1).get(CoreAnnotations.TextAnnotation.class).contains("Second"));
    assertTrue(sentences.get(2).get(CoreAnnotations.TextAnnotation.class).contains("Third"));
  }
@Test
  public void testDocIdIsPropagated() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    String text = "Single sentence.";
    String docId = "DOC_42";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Single");
    token1.setOriginalText("Single");
    token1.set(CoreAnnotations.TextAnnotation.class, "Single");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("sentence.");
    token2.setOriginalText("sentence.");
    token2.set(CoreAnnotations.TextAnnotation.class, "sentence.");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.DocIDAnnotation.class, docId);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertEquals(docId, sentence.get(CoreAnnotations.DocIDAnnotation.class));

    assertEquals(docId, sentence.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(docId, sentence.get(CoreAnnotations.TokensAnnotation.class).get(1).get(CoreAnnotations.DocIDAnnotation.class));
  }
@Test
  public void testIndexingAfterAnnotation() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    String text = "First Second";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.setOriginalText("First");
    token1.set(CoreAnnotations.TextAnnotation.class, "First");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Second");
    token2.setOriginalText("Second");
    token2.set(CoreAnnotations.TextAnnotation.class, "Second");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
    List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(Integer.valueOf(1), Integer.valueOf(sentenceTokens.get(0).index()));
    assertEquals(Integer.valueOf(2), Integer.valueOf(sentenceTokens.get(1).index()));
    assertEquals(0, sentenceTokens.get(0).sentIndex());
    assertEquals(0, sentenceTokens.get(1).sentIndex());
  }
@Test
public void testAnnotateWithEmptySentenceThrowsWhenCountLineNumbersFalse() {
  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(false, null, null, null, "false", null, null);

  String text = " ";
  CoreLabel token = new CoreLabel();
  token.setWord("");
  token.setOriginalText("");
  token.set(CoreAnnotations.TextAnnotation.class, "");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
  token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = new ArrayList<>();
  
  tokens.add(token);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  try {
    annotator.annotate(doc);
    fail("Expected IllegalStateException for empty sentence when countLineNumbers is false");
  } catch (IllegalStateException e) {
    assertTrue(e.getMessage().contains("unexpected empty sentence"));
  }
}
@Test
public void testBoundaryPropertiesUsedInConstructor() {
  Properties props = new Properties();
  props.setProperty("ssplit.boundaryTokenRegex", "[.?!]");
  props.setProperty("ssplit.boundaryFollowersRegex", "[\"')\\]]*");
  props.setProperty("ssplit.boundariesToDiscard", "<BR>");
  props.setProperty("ssplit.htmlBoundariesToDiscard", "div,br");
  props.setProperty("ssplit.tokenPatternsToDiscard", "...");
  props.setProperty("tokenize.whitespace", "true");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

  String text = "Hello.";
  CoreLabel token = new CoreLabel();
  token.setWord("Hello.");
  token.setOriginalText("Hello.");
  token.set(CoreAnnotations.TextAnnotation.class, "Hello.");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
  assertEquals("Hello.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testMultipleSentencesOnlyOneHasSectionStart() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "Sentence one. Sentence two.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Sentence");
  token1.set(CoreAnnotations.TextAnnotation.class, "Sentence");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

  Annotation section = new Annotation("Section");
  section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-01-01");

  token1.set(CoreAnnotations.SectionStartAnnotation.class, section);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("one.");
  token2.set(CoreAnnotations.TextAnnotation.class, "one.");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("Sentence");
  token3.set(CoreAnnotations.TextAnnotation.class, "Sentence");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("two.");
  token4.set(CoreAnnotations.TextAnnotation.class, "two.");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  CoreMap firstSentence = sentences.get(0);
  assertEquals("2024-01-01", firstSentence.get(CoreAnnotations.SectionDateAnnotation.class));

  CoreMap secondSentence = sentences.get(1);
  assertNull(secondSentence.get(CoreAnnotations.SectionDateAnnotation.class));
}
@Test
public void testMultipleSectionMatchesSentenceWithQuotesAndAuthor() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "\"Quoted sentence.\"";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("\"Quoted");
  token1.set(CoreAnnotations.TextAnnotation.class, "\"Quoted");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("sentence.\"");
  token2.set(CoreAnnotations.TextAnnotation.class, "sentence.\"");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

  Annotation quote = new Annotation(text);
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
  quote.set(CoreAnnotations.AuthorAnnotation.class, "Mark Twain");

  Annotation section = new Annotation(text);
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
  section.set(CoreAnnotations.QuotesAnnotation.class, Arrays.asList(quote));
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.SectionDateAnnotation.class, "1890-10-10");

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  doc.set(CoreAnnotations.SectionsAnnotation.class, Arrays.asList(section));

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  CoreMap sentence = sentences.get(0);

  assertEquals("1890-10-10", sentence.get(CoreAnnotations.SectionDateAnnotation.class));
  assertEquals("Mark Twain", sentence.get(CoreAnnotations.AuthorAnnotation.class));
  assertEquals(Boolean.TRUE, sentence.get(CoreAnnotations.QuotedAnnotation.class));
  assertEquals(0, sentence.get(CoreAnnotations.SectionIndexAnnotation.class).intValue());
}
@Test
public void testAnnotateSkipsNewlineTokensFromFinalOutput() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

  String text = "Line one.\nLine two.";
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Line");
  token1.setOriginalText("Line");
  token1.set(CoreAnnotations.TextAnnotation.class, "Line");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("one.");
  token2.setOriginalText("one.");
  token2.set(CoreAnnotations.TextAnnotation.class, "one.");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("\n");
  token3.setOriginalText("\n");
  token3.set(CoreAnnotations.TextAnnotation.class, "\n");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, true);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("Line");
  token4.setOriginalText("Line");
  token4.set(CoreAnnotations.TextAnnotation.class, "Line");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token5 = new CoreLabel();
  token5.setWord("two.");
  token5.setOriginalText("two.");
  token5.set(CoreAnnotations.TextAnnotation.class, "two.");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(doc);

  List<CoreLabel> finalTokens = doc.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(4, finalTokens.size());
  assertEquals("Line", finalTokens.get(0).word());
  assertEquals("two.", finalTokens.get(3).word());

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(2, sentences.size());
}
@Test
public void testTokensWithMissingCharacterOffsetsDoesNotThrow() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "Token with no offsets";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Token");
  token1.setOriginalText("Token");
  token1.set(CoreAnnotations.TextAnnotation.class, "Token");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("with");
  token2.setOriginalText("with");
  token2.set(CoreAnnotations.TextAnnotation.class, "with");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("no");
  token3.setOriginalText("no");
  token3.set(CoreAnnotations.TextAnnotation.class, "no");

  CoreLabel token4 = new CoreLabel();
  token4.setWord("offsets");
  token4.setOriginalText("offsets");
  token4.set(CoreAnnotations.TextAnnotation.class, "offsets");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  boolean exceptionThrown = false;
  try {
    annotator.annotate(doc); 
  } catch (Exception e) {
    exceptionThrown = true;
  }
  assertFalse(exceptionThrown);
}
@Test
public void testEmptyTextButNonEmptyTokensStillAnnotates() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "";

  CoreLabel token = new CoreLabel();
  token.setWord("Token");
  token.setOriginalText("Token");
  token.set(CoreAnnotations.TextAnnotation.class, "Token");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  List<CoreLabel> tokens = Arrays.asList(token);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sentences);
  assertEquals(1, sentences.size());
}
@Test
public void testBoundaryMultiTokenRegexCompilesProperly() {
  Properties props = new Properties();
  props.setProperty("ssplit.boundaryMultiTokenRegex", "\"[A-Z][^\\.]*\\.\"");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

  String text = "Quoted sentence.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("\"Quoted");
  token1.setOriginalText("\"Quoted");
  token1.set(CoreAnnotations.TextAnnotation.class, "\"Quoted");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("sentence.\"");
  token2.setOriginalText("sentence.\"");
  token2.set(CoreAnnotations.TextAnnotation.class, "sentence.\"");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
  doc.set(CoreAnnotations.TextAnnotation.class, text);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sentences);
  assertEquals(1, sentences.size());
  assertTrue(sentences.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Quoted"));
}
@Test
public void testAnnotateSkipsSectionMismatchDueToNoOverlap() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "Hello world!";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Hello");
  token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("world!");
  token2.set(CoreAnnotations.TextAnnotation.class, "world!");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  Annotation unmatchedSection = new Annotation("Unrelated section");
  unmatchedSection.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20); 
  unmatchedSection.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
  unmatchedSection.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  unmatchedSection.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
  unmatchedSection.set(CoreAnnotations.SectionDateAnnotation.class, "2099-01-01");

  List<CoreMap> sections = Arrays.asList(unmatchedSection);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
  doc.set(CoreAnnotations.SectionsAnnotation.class, sections);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
  CoreMap sentence = sentences.get(0);
  assertNull(sentence.get(CoreAnnotations.SectionDateAnnotation.class));
}
@Test
public void testMultipleQuotesInSectionMatchNestedQuoteAnnotation() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "He said, \"She replied, 'Indeed.'\"";

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "He");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "said,");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

  CoreLabel token3 = new CoreLabel();
  token3.set(CoreAnnotations.TextAnnotation.class, "\"She");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

  CoreLabel token4 = new CoreLabel();
  token4.set(CoreAnnotations.TextAnnotation.class, "replied,");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

  CoreLabel token5 = new CoreLabel();
  token5.set(CoreAnnotations.TextAnnotation.class, "'Indeed.'\"");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

  Annotation nestedQuote = new Annotation("'Indeed.'");
  nestedQuote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
  nestedQuote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);
  nestedQuote.set(CoreAnnotations.AuthorAnnotation.class, "Author B");

  Annotation outerQuote = new Annotation("\"She replied, 'Indeed.'\"");
  outerQuote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  outerQuote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);
  outerQuote.set(CoreAnnotations.AuthorAnnotation.class, "Author A");

  Annotation section = new Annotation(text);
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.QuotesAnnotation.class, Arrays.asList(outerQuote, nestedQuote));

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
  doc.set(CoreAnnotations.SectionsAnnotation.class, Arrays.asList(section));

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  CoreMap sentence = sentences.get(0);

  assertEquals("Author B", sentence.get(CoreAnnotations.AuthorAnnotation.class));
  assertEquals(Boolean.TRUE, sentence.get(CoreAnnotations.QuotedAnnotation.class));
}
@Test
public void testMultipleSectionSwitchingDuringAnnotation() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "Alpha Beta Gamma.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alpha");
  token1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Beta");
  token2.set(CoreAnnotations.TextAnnotation.class, "Beta");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("Gamma.");
  token3.set(CoreAnnotations.TextAnnotation.class, "Gamma.");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

  Annotation section1 = new Annotation("Section1");
  section1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  section1.set(CoreAnnotations.SectionDateAnnotation.class, "2000-01-01");
  section1.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
  section1.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  Annotation section2 = new Annotation("Section2");
  section2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
  section2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
  section2.set(CoreAnnotations.SectionDateAnnotation.class, "2020-12-31");
  section2.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
  section2.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  List<CoreMap> sections = Arrays.asList(section1, section2);
  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
  doc.set(CoreAnnotations.SectionsAnnotation.class, sections);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());

  CoreMap sentence = sentences.get(0);
  assertEquals("2020-12-31", sentence.get(CoreAnnotations.SectionDateAnnotation.class));
  assertEquals(1, sentence.get(CoreAnnotations.SectionIndexAnnotation.class).intValue());
}
@Test
public void testSectionStartAndEndOnSentenceTokensTransfersAnnotations() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "Start Middle End";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Start");
  token1.set(CoreAnnotations.TextAnnotation.class, "Start");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Middle");
  token2.set(CoreAnnotations.TextAnnotation.class, "Middle");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("End");
  token3.set(CoreAnnotations.TextAnnotation.class, "End");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

  Annotation section = new Annotation("Section");
  section.set(CoreAnnotations.SectionDateAnnotation.class, "1999-11-11");

  token1.set(CoreAnnotations.SectionStartAnnotation.class, section);
  token3.set(CoreAnnotations.SectionEndAnnotation.class, "end");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());

  CoreMap sentence = sentences.get(0);
  assertEquals("1999-11-11", sentence.get(CoreAnnotations.SectionDateAnnotation.class));
}
@Test
public void testMissingTextAnnotationDefaultsToSubstringWithoutNPE() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = null;

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Test");
  token1.set(CoreAnnotations.TextAnnotation.class, "Test");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  List<CoreLabel> tokens = Arrays.asList(token1);

  Annotation doc = new Annotation("");
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  boolean caughtException = false;

  try {
    annotator.annotate(doc);
  } catch (Exception e) {
    caughtException = true;
  }

  assertTrue("Expected failure due to null TextAnnotation", caughtException);
}
@Test
public void testSentenceWithOnlyNewlineTokensDoesNotCreateSentence() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

  String text = "\n";

  CoreLabel token = new CoreLabel();
  token.setWord("\n");
  token.setOriginalText("\n");
  token.set(CoreAnnotations.TextAnnotation.class, "\n");
  token.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
  token.set(CoreAnnotations.IsNewlineAnnotation.class, true);

  List<CoreLabel> tokens = Arrays.asList(token);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(0, sentences.size());
}
@Test
public void testQuoteDoesNotMatchAnythingDoesNotSetAuthor() {
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

  String text = "Out of quote sentence.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Out");
  token1.set(CoreAnnotations.TextAnnotation.class, "Out");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("of");
  token2.set(CoreAnnotations.TextAnnotation.class, "of");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("quote");
  token3.set(CoreAnnotations.TextAnnotation.class, "quote");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("sentence.");
  token4.set(CoreAnnotations.TextAnnotation.class, "sentence.");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

  Annotation unrelatedQuote = new Annotation("\"Unrelated text\"");
  unrelatedQuote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 100);
  unrelatedQuote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 115);
  unrelatedQuote.set(CoreAnnotations.AuthorAnnotation.class, "Unknown");

  Annotation section = new Annotation("Section");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);
  section.set(CoreAnnotations.QuotesAnnotation.class, Arrays.asList(unrelatedQuote));
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.SectionDateAnnotation.class, "2010-04-01");

  List<CoreMap> tokens = Arrays.asList(token1, token2, token3, token4);

  Annotation doc = new Annotation(text);
  doc.set(CoreAnnotations.TextAnnotation.class, text);
  doc.set(CoreAnnotations.SectionsAnnotation.class, Arrays.asList(section));

  annotator.annotate(doc);

  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());

  CoreMap sentence = sentences.get(0);
  assertNull(sentence.get(CoreAnnotations.AuthorAnnotation.class));
  assertNull(sentence.get(CoreAnnotations.QuotedAnnotation.class));
}
@Test
  public void testMultipleAnnotatorsWritesOnceAndLogsOnce() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    String text = "Repeat sentence test.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Repeat");
    token1.set(CoreAnnotations.TextAnnotation.class, "Repeat");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("sentence");
    token2.set(CoreAnnotations.TextAnnotation.class, "sentence");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("test.");
    token3.set(CoreAnnotations.TextAnnotation.class, "test.");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);
    annotator.annotate(doc); 

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testVerboseDoesNotAffectOutput() {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "true");
    props.setProperty("ssplit.verbose", "true");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    String text = "Verbose flag test.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Verbose");
    token1.set(CoreAnnotations.TextAnnotation.class, "Verbose");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("flag");
    token2.set(CoreAnnotations.TextAnnotation.class, "flag");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("test.");
    token3.set(CoreAnnotations.TextAnnotation.class, "test.");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("Verbose flag test.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testTokenDiscardPatternRemovesTokensFromSentence() {
    Properties props = new Properties();
    props.setProperty("ssplit.tokenPatternsToDiscard", "###");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    String text = "Hello ### World.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("###");
    token2.set(CoreAnnotations.TextAnnotation.class, "###");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "###");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("World.");
    token3.set(CoreAnnotations.TextAnnotation.class, "World.");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "World.");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());

    List<CoreLabel> sentenceTokens = sentences.get(0).get(CoreAnnotations.TokensAnnotation.class);
    
    assertEquals(3, sentenceTokens.size());
  }
@Test
  public void testBeforeAfterNewlineHandlingRestoresWhitespace() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

    String text = "Line1\nLine2";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Line1");
    token1.setOriginalText("Line1");
    token1.set(CoreAnnotations.TextAnnotation.class, "Line1");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.BeforeAnnotation.class, "");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("\n");
    token2.setOriginalText("\n");
    token2.set(CoreAnnotations.TextAnnotation.class, "\n");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Line2");
    token3.setOriginalText("Line2");
    token3.set(CoreAnnotations.TextAnnotation.class, "Line2");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token3.set(CoreAnnotations.BeforeAnnotation.class, "");
    token3.set(CoreAnnotations.AfterAnnotation.class, "");
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    List<CoreLabel> cleanedTokens = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, cleanedTokens.size());
    assertEquals("Line1", cleanedTokens.get(0).word());
    assertEquals("\n", cleanedTokens.get(1).get(CoreAnnotations.BeforeAnnotation.class));
  }
@Test
  public void testNewlineSplitterWithMultipleNewlineTokensOnly() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n", "<NL>", "NEWLINE_TOKEN");

    String text = "X\n<NL>Y";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("X");
    token1.setOriginalText("X");
    token1.set(CoreAnnotations.TextAnnotation.class, "X");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("\n");
    token2.setOriginalText("\n");
    token2.set(CoreAnnotations.TextAnnotation.class, "\n");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("<NL>");
    token3.setOriginalText("<NL>");
    token3.set(CoreAnnotations.TextAnnotation.class, "<NL>");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Y");
    token4.setOriginalText("Y");
    token4.set(CoreAnnotations.TextAnnotation.class, "Y");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("X", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Y", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithCustomNewlineSplitterHandlesLineSeparatorLogic() {
    Properties props = new Properties();
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");
    props.setProperty("ssplit.boundaryTokenRegex", "[!?.]");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("custom.fakeKey", "fakeVal"); 

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence");
    token1.setOriginalText("Sentence");
    token1.set(CoreAnnotations.TextAnnotation.class, "Sentence");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("\n");
    token2.setOriginalText("\n");
    token2.set(CoreAnnotations.TextAnnotation.class, "\n");
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Next");
    token3.setOriginalText("Next");
    token3.set(CoreAnnotations.TextAnnotation.class, "Next");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    String text = "Sentence\nNext";
    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
  }
@Test
  public void testEmptyTokenListDoesNotThrowWhenLineNumbersActive() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

    Annotation document = new Annotation("Empty");

    List<CoreLabel> tokens = new ArrayList<>(); 

    document.set(CoreAnnotations.TextAnnotation.class, "Empty");
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    boolean threw = false;
    try {
      annotator.annotate(document); 
    } catch (Exception e) {
      threw = true;
    }
    assertFalse(threw);
    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(0, sentences.size()); 
  }
@Test
  public void testOutOfOrderCharacterOffsetsDoesNotThrow() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    String text = "Out of sync.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Out");
    token1.set(CoreAnnotations.TextAnnotation.class, "Out");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("of");
    token2.set(CoreAnnotations.TextAnnotation.class, "of");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("sync.");
    token3.set(CoreAnnotations.TextAnnotation.class, "sync.");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    boolean threw = false;
    try {
      annotator.annotate(doc); 
    } catch (Exception e) {
      threw = true;
    }
    assertFalse(threw);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testNullSectionsAnnotationHandledGracefully() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    String text = "Just a test.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Just");
    token1.set(CoreAnnotations.TextAnnotation.class, "Just");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("a");
    token2.set(CoreAnnotations.TextAnnotation.class, "a");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("test.");
    token3.set(CoreAnnotations.TextAnnotation.class, "test.");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    

    boolean threw = false;
    try {
      annotator.annotate(document);
    } catch (Exception e) {
      threw = true;
    }

    assertFalse(threw);
    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testPropertiesConstructorWithoutRegexesDefaultsToStandardSplitter() {
    Properties props = new Properties();
    

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    String text = "Hello world!";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world!");
    token2.set(CoreAnnotations.TextAnnotation.class, "world!");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testMultiTokenRegexBoundarySplitting() {
    Properties props = new Properties();
    props.setProperty("ssplit.boundaryMultiTokenRegex", "\"[^\"]+\""); 
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    String text = "\"Hello world\" \"Another quoted sentence\"";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("\"Hello");
    token1.setOriginalText("\"Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "\"Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world\"");
    token2.setOriginalText("world\"");
    token2.set(CoreAnnotations.TextAnnotation.class, "world\"");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("\"Another");
    token3.setOriginalText("\"Another");
    token3.set(CoreAnnotations.TextAnnotation.class, "\"Another");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("quoted");
    token4.setOriginalText("quoted");
    token4.set(CoreAnnotations.TextAnnotation.class, "quoted");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 29);

    CoreLabel token5 = new CoreLabel();
    token5.setWord("sentence\"");
    token5.setOriginalText("sentence\"");
    token5.set(CoreAnnotations.TextAnnotation.class, "sentence\"");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 30);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 39);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertTrue(sentences.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Hello"));
    assertTrue(sentences.get(1).get(CoreAnnotations.TextAnnotation.class).contains("Another"));
  }
@Test
  public void testHtmlBoundaryToDiscardDoesNotMarkBoundary() {
    Properties props = new Properties();
    props.setProperty("ssplit.htmlBoundariesToDiscard", "br,div");
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    String text = "First<br>Second";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.set(CoreAnnotations.TextAnnotation.class, "First");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("<br>");
    token2.set(CoreAnnotations.TextAnnotation.class, "<br>");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "<br>");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Second");
    token3.set(CoreAnnotations.TextAnnotation.class, "Second");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testBoundaryFollowersRegexStopsSplit() {
    Properties props = new Properties();
    props.setProperty("ssplit.boundaryTokenRegex", "[.!?]");
    props.setProperty("ssplit.boundaryFollowersRegex", "[\"]");
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    String text = "Hello.\" World.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello.");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello.");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("\"");
    token2.set(CoreAnnotations.TextAnnotation.class, "\"");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("World.");
    token3.set(CoreAnnotations.TextAnnotation.class, "World.");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("Hello.\"", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("World.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNewlineTokenModifiesPreviousTokenAfterText() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

    String text = "A\nB";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setOriginalText("A");
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("\n");
    token2.setOriginalText("\n");
    token2.set(CoreAnnotations.TextAnnotation.class, "\n");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("B");
    token3.setOriginalText("B");
    token3.set(CoreAnnotations.TextAnnotation.class, "B");
    token3.set(CoreAnnotations.BeforeAnnotation.class, "");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    
    CoreLabel modified = doc.get(CoreAnnotations.TokensAnnotation.class).get(0);
    assertEquals("\n", modified.get(CoreAnnotations.AfterAnnotation.class));

    
    CoreLabel second = doc.get(CoreAnnotations.TokensAnnotation.class).get(1);
    assertEquals("\n", second.get(CoreAnnotations.BeforeAnnotation.class));
  }
@Test
  public void testSentenceInsideMultipleOverlappingSectionsMatchesFirst() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    String text = "Nested section.";

    CoreLabel token = new CoreLabel();
    token.setWord("Nested");
    token.set(CoreAnnotations.TextAnnotation.class, "Nested");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("section.");
    token2.set(CoreAnnotations.TextAnnotation.class, "section.");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    Annotation section1 = new Annotation("Parent");
    section1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    section1.set(CoreAnnotations.SectionDateAnnotation.class, "2000-01-01");
    section1.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
    section1.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation section2 = new Annotation("Child");
    section2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    section2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    section2.set(CoreAnnotations.SectionDateAnnotation.class, "1999-01-01");
    section2.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
    section2.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    List<CoreLabel> tokens = Arrays.asList(token, token2);
    List<CoreMap> sections = Arrays.asList(section1, section2);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SectionsAnnotation.class, sections);

    annotator.annotate(doc);

    List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals("2000-01-01", sentences.get(0).get(CoreAnnotations.SectionDateAnnotation.class));
  } 
}