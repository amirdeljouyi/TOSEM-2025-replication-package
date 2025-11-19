package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAnnotator_4_GPTLLMTest {

 @Test
  public void testDoubleQuoteIsDetected() {
    String text = "He said, \"Hello world.\"";

    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");

    Annotation annotation = new Annotation(text);
    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("He");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.setOriginalText(",");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token4 = new CoreLabel();
    token4.setOriginalText("\"");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel token5 = new CoreLabel();
    token5.setOriginalText("Hello");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreLabel token6 = new CoreLabel();
    token6.setOriginalText("world");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);

    CoreLabel token7 = new CoreLabel();
    token7.setOriginalText(".");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 21);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreLabel token8 = new CoreLabel();
    token8.setOriginalText("\"");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7, token8);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertEquals(1, quotes.size());
    String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
    assertEquals("\"Hello world.\"", quoteText);
  }
@Test
  public void testSingleQuotesIgnoredByDefault() {
    String text = "She said, 'Not a quote'.";

    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");

    Annotation annotation = new Annotation(text);

    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("She");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.setOriginalText(",");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token4 = new CoreLabel();
    token4.setOriginalText("'");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreLabel token5 = new CoreLabel();
    token5.setOriginalText("Not");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    CoreLabel token6 = new CoreLabel();
    token6.setOriginalText("a");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreLabel token7 = new CoreLabel();
    token7.setOriginalText("quote");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreLabel token8 = new CoreLabel();
    token8.setOriginalText("'");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);

    CoreLabel token9 = new CoreLabel();
    token9.setOriginalText(".");
    token9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    token9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7, token8, token9);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertEquals(0, quotes.size());
  }
@Test
  public void testUnicodeQuotePairDetected() {
    String text = "“Unicode quotes” work correctly.";

    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");

    Annotation annotation = new Annotation(text);
    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("“");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("Unicode");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.setOriginalText("quotes");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreLabel token4 = new CoreLabel();
    token4.setOriginalText("”");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreLabel token5 = new CoreLabel();
    token5.setOriginalText("work");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);

    CoreLabel token6 = new CoreLabel();
    token6.setOriginalText("correctly");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 31);

    CoreLabel token7 = new CoreLabel();
    token7.setOriginalText(".");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 31);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 32);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertEquals(1, quotes.size());
    assertEquals("“Unicode quotes”", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testUnclosedQuotesIfEnabled() {
    String text = "He said, \"This is unclosed.";

    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");

    Annotation annotation = new Annotation(text);
    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("He");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.setOriginalText(",");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token4 = new CoreLabel();
    token4.setOriginalText("\"");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel token5 = new CoreLabel();
    token5.setOriginalText("This");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    CoreLabel token6 = new CoreLabel();
    token6.setOriginalText("is");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    CoreLabel token7 = new CoreLabel();
    token7.setOriginalText("unclosed.");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    annotator.annotate(annotation);

    List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosedQuotes);
    assertEquals(1, unclosedQuotes.size());
    assertTrue(unclosedQuotes.get(0).get(CoreAnnotations.TextAnnotation.class).startsWith("\"This is"));
  }
@Test
public void testEmptyTextAnnotation() {
  String text = "";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(0, quotes.size());
}
@Test
public void testMaxLengthExceededQuoteIsDropped() {
  String text = "\"This is a very long quote that exceeds the threshold.\"";

  Properties props = new Properties();
  props.setProperty("quote.maxLength", "10");
  props.setProperty("quote.attributeQuotes", "false");

  Annotation annotation = new Annotation(text);
  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(0, quotes.size());
}
@Test
public void testQuoteWithOnlyWhitespaceIsIgnored() {
  String text = "\"   \"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  Annotation annotation = new Annotation(text);

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"   \"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testEmbeddedSameTypeQuoteDisallowed() {
  String text = "\"Outer \"inner\" still outer\"";

  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "false");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
}
@Test
public void testEmbeddedSameTypeQuoteAllowed() {
  String text = "\"Outer \"inner\" still outer\"";

  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertEquals(1, quotes.size());
  List<CoreMap> embedded = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(embedded);
  assertEquals(1, embedded.size());
}
@Test
public void testQuoteWithSpecialCharactersInside() {
  String text = "\"@#$%^&*!\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"@#$%^&*!\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testQuoteCrossesSentenceBoundary() {
  String text = "Before. \"Quote that spans. Multiple sentences.\" After.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence1 = new Annotation(text);
  sentence1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"Quote that spans. Multiple sentences.\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testNullTokensAnnotation() {
  String text = "Text with \"quote\".";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(0, quotes.size());
}
@Test
public void testNullSentencesAnnotation() {
  String text = "He said, \"Test quote.\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(0, quotes.size());
}
@Test
public void testQuoteWithoutMatchingClosingQuote_NoExtraction() {
  String text = "This is an unmatched opening quote: \"Open but not closed";

  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "false");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(0, quotes.size());

  assertNull(annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class));
}
@Test
public void testSameStartAndEndOffsetQuoteIgnored() {
  String text = "\"\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testLatexStyleQuotes() {
  String text = "This is a quote: ``Hello world''.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");
  props.setProperty("quote.smartQuotes", "true");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Hello world"));
}
@Test
public void testTripleQuoteWithMiddleEmbeddedQuote() {
  String text = "\"First level quote with 'embedded quote' inside\"";

  Properties props = new Properties();
  props.setProperty("quote.singleQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  List<CoreMap> embeddedQuotes = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(embeddedQuotes);
  assertEquals(1, embeddedQuotes.size());
  assertTrue(embeddedQuotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("embedded quote"));
}
@Test
public void testDirectedQuoteWithoutClosureExtractedIfEnabled() {
  String text = "He said: 『This one never closes.";

  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(0, quotes.size());

  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(unclosed);
  assertEquals(1, unclosed.size());
  assertTrue(unclosed.get(0).get(CoreAnnotations.TextAnnotation.class).contains("This one never closes"));
}
@Test
public void testAsciiQuoteOverridesUnicodeWhenAsciiEnabled() {
  String text = "Start “unicode” and then \"ascii\".";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.asciiQuotes", "true");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertTrue(quotes.size() >= 1);
}
@Test
public void testRecursiveQuotesWithMultipleEmbeddedQuotes() {
  String text = "\"Outer 'Inner 1' and 'Inner 2' end\"";

  Properties props = new Properties();
  props.setProperty("quote.singleQuotes", "true");
  props.setProperty("quote.allowEmbeddedSame", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());

  List<CoreMap> embedded = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(embedded);
  assertEquals(2, embedded.size());
}
@Test
public void testQuoteTokenMappingFailureDueToNoMatch() {
  String text = "\"Quote\" with no token overlap.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token1 = new CoreLabel();
  token1.setOriginalText("something");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 50);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 59);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 50);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 59);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(0, quotes.size());
}
@Test
public void testQuoteAnnotationWithNullDocId() {
  String text = "“Quote without doc ID.”";

  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, null);

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
}
@Test
public void testQuoteAnnotationWithOverlappingQuotesOfDifferentTypes() {
  String text = "He said: \"First 『nested』 done\".";

  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.allowEmbeddedSame", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> topQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, topQuotes.size());
  List<CoreMap> embedded = topQuotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(embedded);
  assertEquals(1, embedded.size());
  String embeddedText = embedded.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(embeddedText.contains("nested"));
}
@Test
public void testSingleQuoteAtBeginningOfTextIsParsedWhenEnabled() {
  String text = "'Starts with quote' and continues.";

  Properties props = new Properties();
  props.setProperty("quote.singleQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("'Starts with quote'", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testMixedQuoteStylesUnicodeAndAsciiSameContent() {
  String text = "A: \"Hello.\" B: “Hello.”";

  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.asciiQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc1");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertTrue(quotes.size() >= 1);
}
@Test
public void testQuoteAtEndWithoutClosureExtractUnclosedFalse() {
  String text = "Start of text and then a quote begins: \"Unclosed quote";

  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "false");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc2");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(0, quotes.size());
  assertNull(annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class));
}
@Test
public void testQuoteWithOneCharacterInside() {
  String text = "\"X\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc3");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"X\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testSmartQuotesPrefersUnicodeOverAsciiWhenCountMore() {
  String text = "“Unicode 1” 'no quote' “Unicode 2”";

  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.asciiQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");
  props.setProperty("quote.singleQuotes", "true");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc4");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(2, quotes.size());
}
@Test
public void testUnicodeQuoteInDifferentLanguageDirection() {
  String text = "他说：「这是中文引号。」";

  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc5");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("这是中文引号"));
}
@Test
public void testQuoteFollowedBySameQuoteCharacter() {
  String text = "\"Hello world\"\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc6");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"Hello world\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testMismatchQuotePairReturnsPartialQuoteWhenUnclosedEnabled() {
  String text = "Mismatch: “This is not closed...";

  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc7");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(0, quotes.size());

  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(unclosed);
  assertEquals(1, unclosed.size());
}
@Test
public void testDeeplyNestedQuotesTripleLayer() {
  String text = "\"Level1 'Level2 “Level3” end Level2' end Level1\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.singleQuotes", "true");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docDeepNest");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> topQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, topQuotes.size());

  List<CoreMap> level2 = topQuotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(level2);
  assertEquals(1, level2.size());

  List<CoreMap> level3 = level2.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(level3);
  assertEquals(1, level3.size());

  assertTrue(level3.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Level3"));
}
@Test
public void testQuoteTokenOffsetsMissingTokenEndAnnotation() {
  String text = "Example: \"Incomplete token fields\" proceeding.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText("Example:");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

  CoreLabel token2 = new CoreLabel();
  token2.setOriginalText("\"Incomplete");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

  CoreLabel token3 = new CoreLabel();
  token3.setOriginalText("token");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);

  CoreLabel token4 = new CoreLabel();
  token4.setOriginalText("fields\"");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 26);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);

  CoreLabel token5 = new CoreLabel();
  token5.setOriginalText("proceeding.");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 34);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 45);

  List<CoreLabel> tokens = Arrays.asList(token, token2, token3, token4, token5);

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docMissingField");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Incomplete"));
}
@Test
public void testQuoteSurroundedByRelaxedPunctuation() {
  String text = "Hi: \"Oops...\" that's it.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docPunct");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Oops"));
}
@Test
public void testQuoteStartsAndEndsAtTextEdges() {
  String text = "\"Boundary test\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText("\"Boundary test\"");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docEdgeQuote");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"Boundary test\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testSingleQuoteBehavesAsApostropheWhenDisabled() {
  String text = "Bill's dog is cute.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");
  props.setProperty("quote.singleQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docApostrophe");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(0, quotes.size());
}
@Test
public void testQuoteContainingOnlySpecialCharacters() {
  String text = "\"@$%&#*!\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docSpecials");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"@$%&#*!\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testMultipleTopLevelQuotesOfSameType() {
  String text = "\"First\" and then \"Second\" appear.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docTwoQuotes");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(2, quotes.size());
  assertEquals("\"First\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  assertEquals("\"Second\"", quotes.get(1).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testQuoteRejectedDueToShortLengthVsMaxLength() {
  String text = "\"ab\"";

  Properties props = new Properties();
  props.setProperty("quote.maxLength", "2");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docMaxLen");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(0, quotes.size());
}
@Test
public void testSmartQuotesMismatchedPairHandledWithFallback() {
  String text = "“Left quote only without right";

  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.extractUnclosedQuotes", "true");
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docSmartFall");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> closedQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(closedQuotes);
  assertEquals(0, closedQuotes.size());

  List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertEquals(1, unclosedQuotes.size());
  assertTrue(unclosedQuotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Left quote only"));
}
@Test
public void testEmptyQuoteNotFilteredIfLengthIsMet() {
  String text = "\"  \"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");
  props.setProperty("quote.maxLength", "10");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docEmptyQuote");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"  \"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testQuoteAfterIgnoredInvalidFirstQuoteCharacter() {
  String text = "Ignored @ then \"valid quote\" follows.";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docRecoverAfterSymbol");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, quotes.size());
  assertEquals("\"valid quote\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testBackToBackQuotesSameTypeHandledSeparately() {
  String text = "\"One\"\"Two\"";

  Properties props = new Properties();
  props.setProperty("quote.attributeQuotes", "false");
  props.setProperty("quote.allowEmbeddedSame", "false");

  CoreLabel token = new CoreLabel();
  token.setOriginalText(text);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docBackToBack");
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(2, quotes.size());
  assertEquals("\"One\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  assertEquals("\"Two\"", quotes.get(1).get(CoreAnnotations.TextAnnotation.class));
} 
}