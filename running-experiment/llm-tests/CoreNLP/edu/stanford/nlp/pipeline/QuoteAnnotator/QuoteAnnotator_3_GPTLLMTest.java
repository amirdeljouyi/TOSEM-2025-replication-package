package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAnnotator_3_GPTLLMTest {

 @Test
  public void testAsciiDoubleQuoteDetection() {
    Properties props = new Properties();
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "She said, \"Hello world!\" with confidence.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "test-doc");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "She");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "She");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "said,");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "said,");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "\"Hello");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "\"Hello");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "world!\"");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "world!\"");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "with");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "with");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 27);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 31);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.TextAnnotation.class, "confidence.");
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "confidence.");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 32);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 43);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 43);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull("Expected non-null quotations list", quotes);
    assertEquals("Expected one quotation detected", 1, quotes.size());

    CoreMap quote = quotes.get(0);
    String quoteText = quote.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Hello world!", quoteText);
  }
@Test
  public void testQuoteAnnotatorDetectsUnclosedQuoteWhenConfigured() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "Here is an unclosed quote: \"Something went wrong.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc1");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Here");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Here");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "an");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "an");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "unclosed");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "unclosed");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "quote:");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "quote:");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.TextAnnotation.class, "\"Something");
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "\"Something");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 27);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 37);

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.TextAnnotation.class, "went");
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, "went");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 38);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 42);

    CoreLabel token8 = new CoreLabel();
    token8.set(CoreAnnotations.TextAnnotation.class, "wrong.");
    token8.set(CoreAnnotations.OriginalTextAnnotation.class, "wrong.");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 43);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 49);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7, token8);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 49);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Expected no closed quotes", quotes == null || quotes.isEmpty());

    List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull("Expected unclosed quotes to be present", unclosedQuotes);
    assertEquals("Expecting one unclosed quotation", 1, unclosedQuotes.size());

    CoreMap unclosed = unclosedQuotes.get(0);
    assertEquals("Something went wrong.", unclosed.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testQuoteIndexSetCorrectly() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "'First quote' 'Second quote'";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc2");

    CoreLabel t0 = new CoreLabel();
    t0.set(CoreAnnotations.TextAnnotation.class, "'First");
    t0.set(CoreAnnotations.OriginalTextAnnotation.class, "'First");
    t0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "quote'");
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "quote'");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "'Second");
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "'Second");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "quote'");
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, "quote'");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);

    List<CoreLabel> tokens = Arrays.asList(t0, t1, t2, t3);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());

    Integer idx0 = quotes.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class);
    Integer idx1 = quotes.get(1).get(CoreAnnotations.QuotationIndexAnnotation.class);
    assertNotNull(idx0);
    assertNotNull(idx1);
    assertNotEquals("Quote indices must be distinct", idx0, idx1);
    assertTrue("Indices should be 0 and 1", (idx0 == 0 && idx1 == 1) || (idx0 == 1 && idx1 == 0));
  }
@Test
  public void testNoQuotesDetectedWhenDisabled() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");
    props.setProperty("quote.asciiQuotes", "false");
    props.setProperty("quote.smartQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "He said 'this is not a quote' and walked away.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc3");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "He");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "said");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "'this");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "'this");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "is");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "not");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "not");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Expected no quotes when quotes are disabled", quotes == null || quotes.isEmpty());
  }
@Test
  public void testEmptyTextDocument() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "empty");

    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("No quotes expected in empty document", quotes == null || quotes.isEmpty());
  }
@Test
  public void testOnlyQuoteCharacters() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "\"\" '' `` ''";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "quotes-only");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "\"\" '' `` ''");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "\"\" '' `` ''");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Arrays.asList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testQuoteExceedsMaxLength() {
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "5");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "\"This quote is too long and should be ignored due to length.\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "long-quote");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Arrays.asList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Quote should be ignored due to maxLength", quotes == null || quotes.isEmpty());
  }
@Test
  public void testUnicodeQuotesParsedCorrectly() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "She said: “Smart quotes ‘should work’ fine.”";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "smart-quotes");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Arrays.asList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull("Quotes should not be null", quotes);
    assertEquals("Should contain 1 outer quote", 1, quotes.size());

    CoreMap outerQuote = quotes.get(0);
    List<CoreMap> embedded = outerQuote.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull("Should detect embedded quotes", embedded);
    assertEquals("One embedded quote expected", 1, embedded.size());

    assertEquals("should work", embedded.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testLatexBacktickQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "It is written: ``Thou shalt not pass.''";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "latex");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals("Should detect 1 latex quote", 1, quotes.size());

    CoreMap quote = quotes.get(0);
    assertEquals("Thou shalt not pass.", quote.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testUnclosedEmbeddedQuote() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "\"This is an outer quote with an 'unclosed inner quote.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "unclosed-embedded");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> regularQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);

    assertNotNull("Should extract regular quotes", regularQuotes);
    assertEquals(1, regularQuotes.size());
    assertNotNull("Should extract unclosed nested quote", unclosedQuotes);
    assertTrue(unclosedQuotes.size() > 0);
  }
@Test
  public void testParagraphIndexUnchangedWithoutAttribution() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "Paragraph test: \"Only testing quotes, not attribution\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "paragraph");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertNull("No paragraph index set if attribution is disabled", quotes.get(0).get(CoreAnnotations.ParagraphIndexAnnotation.class));
  }
@Test
  public void testMisnestedQuotesShouldNotEmbed() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.allowEmbeddedSame", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "'Unmatched \"nested quote' still ends here\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "misnested");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertNull("Embedded quotes should not exist due to invalid nesting", quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class));
  }
@Test
  public void testUnmatchedClosingQuoteShouldNotCrash() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.extractUnclosedQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "This should not \"crash due to trailing end quote'.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "unmatched-closing");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testSmartQuotesChoosesUnicodeOverAscii() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "Here is “a smart ‘inner’ quote” preferred.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "smart-vs-ascii");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull("Unicode quote should have been used", quotes);
    assertEquals(1, quotes.size());

    List<CoreMap> embedded = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(embedded);
    assertEquals("Should detect inner quote", 1, embedded.size());
  }
@Test
  public void testDuplicateInnerQuotesFlattenedCorrectly() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "\"This has 'one', 'two', and 'three' quotes\".";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "multi-nested");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> topLevel = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(topLevel);
    assertEquals(1, topLevel.size());

    List<CoreMap> inner = topLevel.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(inner);
    assertEquals(3, inner.size());
  }
@Test
  public void testTextWithNonQuoteSymbols() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "Symbols like ©®™±≈«» should not be misinterpreted.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "symbols");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("No actual quotes means none should be detected", quotes == null || quotes.isEmpty());
  }
@Test
  public void testDuplicateQuoteBoundariesRemovedFromTopLevel() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "'Outer 'Inner still in outer' Done'";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "duplicate-nested");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("Outer 'Inner still in outer' Done", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testFallbackFromUnicodeToAsciiQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "\"Simple ASCII quote fallback\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "ascii-fallback");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("Simple ASCII quote fallback", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testQuoteStartsAtDocumentStart() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "\"Beginning quote right at start of text.\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "start-quote");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull("Should find one quote", quotes);
    assertEquals(1, quotes.size());
    assertEquals("Beginning quote right at start of text.", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testQuoteEndsAtDocumentEnd() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "This ends with a quote: 'end of the line.'";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "end-quote");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "This ends with a quote: 'end of the line.'");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "This ends with a quote: 'end of the line.'");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("end of the line.", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testIdenticalStartEndQuotesNotNested() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.allowEmbeddedSame", "false");
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "'This is 'not' nested'";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "non-nested");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("This is 'not' nested", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testEmptyQuoteContentIgnored() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "He said \"\" and walked out.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "empty-quote");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Quote with empty content should not be returned", quotes == null || quotes.isEmpty());
  }
@Test
  public void testQuoteDetectionWithMismatchedUnicodePair() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "Mismatched quote usage “This is not closed properly.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "mismatch-unicode");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Quote is unclosed and should be discarded", quotes == null || quotes.isEmpty());
  }
@Test
  public void testWhitespaceBetweenUnicodeQuotePairs() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "Whitespace “   padded quote   ” inside.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "unicode-space-padded");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("   padded quote   ", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testInvalidMixedQuoteTypesIgnored() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "\"This quote doesn't end with a matching quote mark.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "invalid-single");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Invalid closing quote should prevent detection", quotes == null || quotes.isEmpty());
  }
@Test
  public void testInternationalDirectedQuotesGuillemets() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "The headline was: «Breaking News».";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "guillemets");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("Breaking News", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAllowEmbeddedSameQuotesEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "'He said, 'I know.''";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "embedded-same-allowed");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> topLevel = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(topLevel);
    assertEquals(1, topLevel.size());

    CoreMap outerQuote = topLevel.get(0);
    assertTrue(outerQuote.containsKey(CoreAnnotations.QuotationsAnnotation.class));
    List<CoreMap> embedded = outerQuote.get(CoreAnnotations.QuotationsAnnotation.class);
    assertEquals(1, embedded.size());
    assertEquals("I know.", embedded.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testQuoteFollowedByPunctuation() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "He called it 'weird'.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "quote-punctuation");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("weird", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testXmlFreeTextWithoutTokenOffsets() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "Before <xml>This is a test quote</xml> 'Hello world' tag.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "xml-non-offset");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 33);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 38);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "world");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "world");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 39);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 44);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("Hello world", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testEmptyTextAndTokenContent() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "no-content");

    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNull(quotes);
  }
@Test
  public void testDeeplyNestedQuotesThreeLevels() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "'Level 1 says: 'Level 2 says: 'Level 3'' end.''";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "deep-nest");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> top = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(top);
    assertEquals(1, top.size());

    List<CoreMap> level2 = top.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(level2);
    assertEquals(1, level2.size());

    List<CoreMap> level3 = level2.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(level3);
    assertEquals(1, level3.size());

    assertEquals("Level 3", level3.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testZeroLengthQuoteNotIncluded() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "This is a '' zero-length quote.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "zero-length");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Zero-length quote should be ignored", quotes == null || quotes.isEmpty());
  }
@Test
  public void testOnlyOpeningQuotePresentWithoutUnclosedExtraction() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "He began to say, \"But never finished.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "no-unclosed");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "He");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "began");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "began");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "to");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "to");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "say,");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "say,");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "\"But");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "\"But");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.TextAnnotation.class, "never");
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "never");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.TextAnnotation.class, "finished.");
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, "finished.");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 29);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 38);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 38);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("No quote should be created for unclosed quotes when disabled", quotes == null || quotes.isEmpty());
  }
@Test
  public void testUnclosedQuoteWithNestedAlsoUnclosed() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "\"Start 'Nested still open\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "deep-unclosed");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Collections.singletonList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosed);
    assertEquals(1, unclosed.size());

    List<CoreMap> nestedUnclosed = unclosed.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(nestedUnclosed);
    assertEquals(1, nestedUnclosed.size());
    assertEquals("Nested still open", nestedUnclosed.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testQuoteAtEndWithoutWhitespaceOrPunctuation() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "Here it is: \"FinalQuote\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "no-trailing-punct");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Collections.singletonList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("FinalQuote", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testTextWithNoDocIDAnnotation() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "He said 'missing doc id'";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("missing doc id", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertNull("DocID should be null in quote if not set in main annotation", quotes.get(0).get(CoreAnnotations.DocIDAnnotation.class));
  }
@Test
  public void testQuoteWithNoTokensFallsThrough() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "This input has no tokens";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "no-tokens");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNull("No quotes expected when tokens/sentences missing", quotes);
  }
@Test
  public void testSortedQuoteOrderingByOffset() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "He said 'B', then 'A'.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "order-offset");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());
    int offset0 = quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    int offset1 = quotes.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    assertTrue("Quotes should be ordered by offset", offset0 < offset1);
  }
@Test
  public void testMaxLengthFilterExcludesQuote() {
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "10");
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "He said: \"this quote is too long to be valid\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "max-length");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Quote should be excluded due to maxLength", quotes == null || quotes.isEmpty());
  }
@Test
  public void testUnicodeDirectedQuoteUnmatchedPair() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "Starting quote “ but the pair is never closed...";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "bad-smart");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertTrue("Unmatched smart quote should not be returned", quotes == null || quotes.isEmpty());
  }
@Test
  public void testNegativeMaxLengthTreatsQuotesNormally() {
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "-1");
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    String text = "He said \"a quote that should be valid\"";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "neg-maxlen");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertEquals("a quote that should be valid", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDirectedQuotePairWithEmptyContentIgnored() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "Using empty pair: “”.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "smart-empty");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue("Empty smart quote should be discarded", quotes == null || quotes.isEmpty());
  }
@Test
  public void testRecursiveQuotesWithTooShortEmbeddedQuote() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "'Outer \"a\" text'";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "short-nested");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, text);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    List<CoreMap> embedded = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(embedded);
    assertEquals("Short but valid embedded quote", 1, embedded.size());
    assertEquals("a", embedded.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testWhitespaceOnlyBetweenTokensInXmlFreeText() {
    String docText = "Hi     <tag>Skip Me</tag>   There";

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hi");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "There");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 31);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 36);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    Annotation annotation = new Annotation(docText);
    annotation.set(CoreAnnotations.TextAnnotation.class, docText);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    String cleaned = QuoteAnnotator.xmlFreeText(docText, annotation);

    assertEquals("Whitespace should match original length", docText.length(), cleaned.length());
    assertEquals("Hi     ", cleaned.substring(0, 7));
    assertEquals("     ", cleaned.substring(26, 31)); 
    assertEquals("There", cleaned.substring(31, 36));
  } 
}