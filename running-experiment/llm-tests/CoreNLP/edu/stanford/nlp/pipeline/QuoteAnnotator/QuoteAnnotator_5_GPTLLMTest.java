package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAnnotator_5_GPTLLMTest {

 @Test
  public void testInitializationWithDefaultProperties() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    assertFalse(annotator.USE_SINGLE);
    assertEquals(-1, annotator.MAX_LENGTH);
    assertFalse(annotator.ASCII_QUOTES);
    assertFalse(annotator.ALLOW_EMBEDDED_SAME);
    assertFalse(annotator.SMART_QUOTES);
    assertFalse(annotator.EXTRACT_UNCLOSED);
    assertTrue(annotator.ATTRIBUTE_QUOTES);
    assertNotNull(annotator.quoteAttributionAnnotator);
  }
@Test
  public void testInitializationWithCustomProperties() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.maxLength", "50");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    assertTrue(annotator.USE_SINGLE);
    assertEquals(50, annotator.MAX_LENGTH);
    assertTrue(annotator.ASCII_QUOTES);
    assertTrue(annotator.ALLOW_EMBEDDED_SAME);
    assertTrue(annotator.SMART_QUOTES);
    assertTrue(annotator.EXTRACT_UNCLOSED);
    assertFalse(annotator.ATTRIBUTE_QUOTES);
    assertNull(annotator.quoteAttributionAnnotator);
  }
@Test
  public void testAsciiQuotesExtraction() {
    String text = "He said, \"Hello world.\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "said,");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    tokens.add(t2);

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, "\"Hello");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    tokens.add(t3);

    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.OriginalTextAnnotation.class, "world.\"");
    t4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    t4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    tokens.add(t4);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap quote = quotes.get(0);
    Integer begin = quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    Integer end = quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
    assertEquals("\"Hello world.\"", text.substring(begin, end));
  }
@Test
  public void testExtractUnclosedQuote() {
    String text = "\"This is unclosed text";
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "\"This");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    tokens.add(t2);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosedQuotes);
    assertEquals(1, unclosedQuotes.size());

    CoreMap quote = unclosedQuotes.get(0);
    Integer begin = quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    Integer end = quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
    assertEquals("\"This is unclosed text", text.substring(begin, end));
  }
@Test
  public void testUnicodeQuoteReplacement() {
    String input = "“Smart quotes” around text.";
    String converted = QuoteAnnotator.replaceUnicode(input);
    assertEquals("\"Smart quotes\" around text.", converted);
  }
@Test
  public void testEmptyInputAnnotation() {
    String text = "";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testSmartQuoteNestedQuoteExtraction() {
    String text = "He said, “She replied, ‘Indeed.’”";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap topQuote = quotes.get(0);
    List<CoreMap> embedded = topQuote.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(embedded);
    assertEquals(1, embedded.size());

    String embeddedText = text.substring(
        embedded.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        embedded.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertTrue(embeddedText.contains("Indeed"));
  }
@Test
  public void testQuoteIndexAssignments() {
    String text = "\"First quote.\" and then, \"Second quote.\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());

    Integer quoteIndex1 = quotes.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class);
    Integer quoteIndex2 = quotes.get(1).get(CoreAnnotations.QuotationIndexAnnotation.class);

    assertEquals((Integer) 0, quoteIndex1);
    assertEquals((Integer) 1, quoteIndex2);
  }
@Test
  public void testComparatorLogic() {
    CoreMap quote1 = new Annotation("Quote one");
    quote1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);

    CoreMap quote2 = new Annotation("Quote two");
    quote2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);

    int result = QuoteAnnotator.getQuoteComparator().compare(quote1, quote2);
    assertTrue(result < 0);
  }
@Test
  public void testNestedQuotesSameTypeWithDisallowedEmbeddedSame() {
    String text = "\"Outer quote with \"inner quote\" not allowed.\"";
    Properties props = new Properties();
    props.setProperty("quote.allowEmbeddedSame", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());

    String quote1 = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    String quote2 = text.substring(
        quotes.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertTrue(quote1.contains("Outer"));
    assertTrue(quote2.contains("inner"));
  }
@Test
  public void testQuoteLongerThanMaxLengthIsIgnored() {
    String text = "\"This is a very long quotation that should be excluded based on maxLength property.\"";
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "10");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testTextWithOnlyOpeningQuoteAndNoExtractUnclosed() {
    String text = "\"This quote is never closed";
    Properties props = new Properties(); 
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(0, quotes.size());

    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNull(unclosed);
  }
@Test
  public void testDifferentUnicodeQuotePairsExtractedCorrectly() {
    String text = "Some text «quoted here» and then ‚another‘ one.";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(2, quotes.size());

    String first = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    String second = text.substring(
        quotes.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(first.contains("quoted"));
    assertTrue(second.contains("another"));
  }
@Test
  public void testMinimalQuoteOnlyCharacters() {
    String text = "\"\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap quote = quotes.get(0);
    assertEquals((Integer) 0, quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals((Integer) 2, quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testQuoteInsideTokenBoundary() {
    String text = "The file is named \"report.pdf\".";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.OriginalTextAnnotation.class, "The");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokens.add(tok1);

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.OriginalTextAnnotation.class, "file");
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    tokens.add(tok2);

    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    tokens.add(tok3);

    CoreLabel tok4 = new CoreLabel();
    tok4.set(CoreAnnotations.OriginalTextAnnotation.class, "named");
    tok4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    tok4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    tokens.add(tok4);

    CoreLabel tok5 = new CoreLabel();
    tok5.set(CoreAnnotations.OriginalTextAnnotation.class, "\"report.pdf\"");
    tok5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    tok5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
    tokens.add(tok5);

    CoreLabel tok6 = new CoreLabel();
    tok6.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    tok6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 30);
    tok6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 31);
    tokens.add(tok6);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    String quoteString = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals("\"report.pdf\"", quoteString);
  }
@Test
  public void testDisallowSingleQuotesWhenDisabled() {
    String text = "He said, 'Absolutely not.'";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testAllowSingleQuotesWhenEnabled() {
    String text = "He said, 'Absolutely not.'";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    String quoteString = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("'Absolutely not.'", quoteString);
  }
@Test
  public void testQuoteEndingWithTextNotPunctuation() {
    String text = "\"Quote text that ends strangely\"followingWord";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    String extracted = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(extracted.startsWith("\""));
    assertTrue(extracted.endsWith("\""));
    assertTrue(extracted.contains("strangely"));
  }
@Test
  public void testTextWithOnlyClosingQuote() {
    
    String text = "This sentence ends with a quote mark\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testMultipleConsecutiveQuotes() {
    
    String text = "\"\"\"Empty\"\"\"";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertTrue(quotes.size() >= 1);
  }
@Test
  public void testBacktickQuotes() {
    
    String text = "``This is a TeX-style quote.''";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quoted = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(quoted.contains("TeX-style"));
  }
@Test
  public void testWhitespaceOnlyInsideQuotes() {
    
    String text = "\"   \"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testQuoteWithSpecialCharactersInside() {
    
    String text = "\"!@#$%^&*()_+\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testQuotesWithPunctuationAround() {
    
    String text = "Wow! \"Amazing.\" Isn't it?";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quote = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(quote.contains("Amazing."));
  }
@Test
  public void testQuoteDetectionNearTextBoundaries() {
    
    String text = "\"Everything quoted.\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String q = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("\"Everything quoted.\"", q);
  }
@Test
  public void testUnclosedNestedQuoteWithUnclosedSupport() {
    
    String text = "\"Outer says 'inner is broken\" more text";
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosed);
    assertTrue(unclosed.size() >= 1);
  }
@Test
  public void testQuoteFollowedImmediatelyByQuote() {
    
    String text = "\"Hi\"\"Bye\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testQuoteSurroundedByWhitespace() {
    
    String text = "   \"Hello world\"   ";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quote = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("\"Hello world\"", quote);
  }
@Test
  public void testUnmatchedSmartQuoteAtEndIgnoredWithoutExtract() {
    
    String text = "Quote starts here “but never ends";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.extractUnclosedQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(0, quotes.size());

    assertNull(annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class));
  }
@Test
  public void testDirectedNestedQuotesAllowed() {
    
    String text = "She replied: “He said, ‘Indeed.’”";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    List<CoreMap> embedded = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(embedded);
    assertEquals(1, embedded.size());
  }
@Test
  public void testOverlappingQuotesAreNested() {
    
    String text = "\"Outer quote with 'inner quote' inside.\"";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> outerQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(outerQuotes);
    assertEquals(1, outerQuotes.size());

    CoreMap outer = outerQuotes.get(0);
    List<CoreMap> inner = outer.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(inner);
    assertEquals(1, inner.size());
  }
@Test
  public void testMultipleQuoteTypesSameTextPreferenceUnicode() {
    
    String text = "“Unicode quote” and \"ASCII quote\"";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.singleQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(2, quotes.size());

    String firstQuoteText = text.substring(
        quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(firstQuoteText.startsWith("“") || firstQuoteText.startsWith("\""));
  }
@Test
  public void testAsciiQuotesConversionAppliedWhenEnabled() {
    
    String text = "“Hello world” and ‘test’";
    Properties props = new Properties();
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testQuoteEndingMatchesMaxLengthBoundary() {
    
    String text = "\"1234567890\""; 
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "10"); 
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    
    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testInvalidUTFUnicodeQuotePairIgnored() {
    
    String text = "Some text «badly nested“ with no close";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testDeepNestedQuotesRecursiveLimit() {
    
    String text = "\"Level 1 quote 'Level 2 “Level 3 content” still level 2' back to 1\"";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> topLevelQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(topLevelQuotes);
    assertEquals(1, topLevelQuotes.size());

    List<CoreMap> level2 = topLevelQuotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(level2);
    assertEquals(1, level2.size());

    List<CoreMap> level3 = level2.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(level3);
    assertEquals(1, level3.size());

    String level3Text = text.substring(
        level3.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        level3.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertTrue(level3Text.contains("Level 3"));
  }
@Test
  public void testUnsupportedQuoteCharactersIgnored() {
    
    String text = "Here's a broken quote “and then we end with less-than<";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testOnlyOneTerminalQuoteCharacterWithoutMatch() {
    String text = "A sentence that ends with ‘";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true"); 
    props.setProperty("quote.extractUnclosedQuotes", "false");
    QuoteAnnotator quoteAnnotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    quoteAnnotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testEmptyStringInputYieldsNoQuotes() {
    String text = "";
    Properties props = new Properties();
    QuoteAnnotator quoteAnnotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    quoteAnnotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testWhitespaceOnlyDocumentInput() {
    String text = "      ";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    
    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testQuoteWithUnmatchedStartFallbackToRecursion() {
    String text = "\"A quote starts and then \"another one\" is found right after";
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);

    List<CoreMap> closedQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);

    assertNotNull(closedQuotes);
    assertEquals(1, closedQuotes.size()); 
    assertNotNull(unclosedQuotes);
    assertEquals(1, unclosedQuotes.size()); 
  }
@Test
  public void testQuoteExactlyAtStartAndEndOfString() {
    String text = "\"only quoted content\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap quote = quotes.get(0);
    int begin = quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    int end = quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
    assertEquals(0, begin);
    assertEquals(text.length(), end);
  }
@Test
  public void testRepeatedIdenticalQuotesWithoutEmbeddingAllowed() {
    String text = "‘A’ and ‘B’ are both simple quotes.";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testQuoteInsideWordBoundariesIgnoredWhenInvalid() {
    String text = "Don’t confuse quotes like O’Reilly with quotations.";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertTrue(quotes.isEmpty()); 
  }
@Test
  public void testUnicodeQuotePairFromCJKSymbols() {
    String text = "The phrase is enclosed in 「special brackets」.";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.asciiQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quoted = text.substring(
            quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
            quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("「special brackets」", quoted);
  }
@Test
  public void testSingleAsciiQuoteMarksIgnoredWhenSingleQuotesFalse() {
    String text = "He called me 'Bossman' back then.";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testQuoteAtEndOfStringWithPunctuation() {
    String text = "He said \"That is correct.\"";
    Properties props = new Properties();

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    
    String extracted = text.substring(
      quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
      quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals("\"That is correct.\"", extracted);
  }
@Test
  public void testQuoteBeforeFirstTokenPosition() {
    String text = "  \"Preamble quote\" followed by a sentence.";
    Properties props = new Properties();

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);
    
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    
    String matched = text.substring(
      quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
      quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(matched.contains("Preamble"));
  }
@Test
  public void testInnerQuoteWithoutClosingOuterQuoteAndUnclosedExtractionOn() {
    String text = "\"This starts with an outer quote and 'inner quote ends properly.";
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> closedQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);

    assertNotNull(closedQuotes);
    assertEquals(1, closedQuotes.size()); 
    
    assertNotNull(unclosedQuotes);
    assertFalse(unclosedQuotes.isEmpty()); 
  }
@Test
  public void testQuoteWithMultipleSameSmartQuotePairs() {
    String text = "“First” text “Second” text “Third”.";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(3, quotes.size());
  }
@Test
  public void testQuoteAcrossMultipleSentencesWithoutSentenceSplits() {
    String text = "He said, \"This spans a sentence. Then continues on.\" That's unusual.";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testUnclosedQuoteIsNestedInsideAnotherUnclosedQuote() {
    String text = "\"The outer ‘inner without end.";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.extractUnclosedQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);

    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosed);
    assertEquals(1, unclosed.size());
    
    List<CoreMap> nested = unclosed.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(nested);
    assertEquals(1, nested.size());
  }
@Test
  public void testQuoteParsingWithOffsetPaddingAndMissingTokenText() {
    String text = "  \n\n  \"Quoting after padding\"";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testQuoteWithOverlappedTokensShouldIncludeQuote() {
    String text = "Start quote here: \"Quick brown fox\".";
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    int start = quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    int end = quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
    String matched = text.substring(start, end);
    assertEquals("\"Quick brown fox\"", matched);
  }
@Test
  public void testQuotationInsideParentheses() {
    String text = "He said (\"I agree with that.\") during the meeting.";
    Properties props = new Properties();

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String extracted = text.substring(
      quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
      quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("\"I agree with that.\"", extracted);
  }
@Test
  public void testAdjacentDifferentQuoteTypesWithNoSpaces() {
    String text = "‘First’“Second”“Third”";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(3, quotes.size());
    String first = text.substring(
      quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
      quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(first.contains("First"));
  }
@Test
  public void testQuoteBeginningWithApostropheLikeWord() {
    String text = "He said, ''tis strange indeed.''";
    Properties props = new Properties();
    props.setProperty("quote.asciiQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quote = text.substring(quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                                  quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("''tis strange indeed.''", quote);
  }
@Test
  public void testQuoteWithMixedUnicodeAndAsciiMarks() {
    String text = "The sign read “Welcome to the “real” world.”";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> outer = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(outer);
    assertEquals(1, outer.size());

    List<CoreMap> inner = outer.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(inner);
    assertEquals(1, inner.size());

    String innerText = text.substring(inner.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                                      inner.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("“real”", innerText);
  }
@Test
  public void testQuoteWithSmartSingleAndAllowEmbeddedSameTrue() {
    String text = "‘Outer ‘inner’ outer’";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> outer = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(outer);
    assertEquals(1, outer.size());

    List<CoreMap> inner = outer.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(inner);
    assertEquals(1, inner.size());

    String embedded = text.substring(inner.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                                     inner.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("‘inner’", embedded);
  }
@Test
  public void testQuoteThatStartsAndEndsWithSameQuoteButWrongDirection() {
    String text = "“Mismatched ‘smart' quotes”";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> outer = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(outer);
    assertEquals(1, outer.size());

    List<CoreMap> embedded = outer.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(embedded);
    assertEquals(1, embedded.size());

    String embeddedText = text.substring(embedded.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                                         embedded.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("‘smart'", embeddedText);
  }
@Test
  public void testQuoteEndsInsideNewlineGap() {
    String text = "The quote is here:\n\"Ends with newline\n\" Follow up.";
    Properties props = new Properties();

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    String captured = text.substring(quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                                     quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(captured.contains("Ends with newline"));
  }
@Test
  public void testQuotationWithWhitespacePaddingIgnoredInLengthCheck() {
    String text = "\"    A short quote    \"";
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "10");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    
    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    String captured = text.substring(quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                                     quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(captured.contains("short quote"));
  }
@Test
  public void testQuoteBetweenTwoCommas() {
    String text = "By saying, \"yes\", he agreed.";
    Properties props = new Properties();

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    String quoted = text.substring(quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                                   quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(quoted.equals("\"yes\""));
  } 
}