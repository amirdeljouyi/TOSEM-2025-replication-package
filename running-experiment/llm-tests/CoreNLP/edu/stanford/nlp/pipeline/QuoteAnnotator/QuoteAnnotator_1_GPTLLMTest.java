package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAnnotator_1_GPTLLMTest {

 @Test
  public void testBasicAsciiQuoteExtraction() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "John said, \"Hello there!\" Then he left.";

    CoreLabel token1 = new CoreLabel(); token1.setOriginalText("John"); token1.setBeginPosition(0); token1.setEndPosition(4);
    CoreLabel token2 = new CoreLabel(); token2.setOriginalText("said,"); token2.setBeginPosition(5); token2.setEndPosition(10);
    CoreLabel token3 = new CoreLabel(); token3.setOriginalText("\"Hello"); token3.setBeginPosition(11); token3.setEndPosition(18);
    CoreLabel token4 = new CoreLabel(); token4.setOriginalText("there!\""); token4.setBeginPosition(19); token4.setEndPosition(27);
    CoreLabel token5 = new CoreLabel(); token5.setOriginalText("Then"); token5.setBeginPosition(28); token5.setEndPosition(32);
    CoreLabel token6 = new CoreLabel(); token6.setOriginalText("he"); token6.setBeginPosition(33); token6.setEndPosition(35);
    CoreLabel token7 = new CoreLabel(); token7.setOriginalText("left."); token7.setBeginPosition(36); token7.setEndPosition(41);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
    assertTrue(quoteText.contains("Hello"));
  }
@Test
  public void testSingleQuotesDisabledByDefault() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Hello', he said.";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Hello',"); t1.setBeginPosition(0); t1.setEndPosition(8);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("he"); t2.setBeginPosition(9); t2.setEndPosition(11);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("said."); t3.setBeginPosition(12); t3.setEndPosition(17);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue(quotes == null || quotes.isEmpty());
  }
@Test
  public void testSingleQuotesEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Hi there', he said.";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Hi"); t1.setBeginPosition(0); t1.setEndPosition(3);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("there',"); t2.setBeginPosition(4); t2.setEndPosition(11);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("he"); t3.setBeginPosition(12); t3.setEndPosition(14);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("said."); t4.setBeginPosition(15); t4.setEndPosition(20);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Hi"));
  }
@Test
  public void testSmartQuotesEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "He said, “Yes.”";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("He"); t1.setBeginPosition(0); t1.setEndPosition(2);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("said,"); t2.setBeginPosition(3); t2.setEndPosition(8);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("“Yes.”"); t3.setBeginPosition(9); t3.setEndPosition(16);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Yes"));
  }
@Test
  public void testUnclosedQuotesExtractionEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "He said, \"This is unclosed.";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("He"); t1.setBeginPosition(0); t1.setEndPosition(2);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("said,"); t2.setBeginPosition(3); t2.setEndPosition(8);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("\"This"); t3.setBeginPosition(9); t3.setEndPosition(14);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("is"); t4.setBeginPosition(15); t4.setEndPosition(17);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("unclosed."); t5.setBeginPosition(18); t5.setEndPosition(28);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
    List<CoreMap> unclosedQuotes = document.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosedQuotes);
    assertEquals(1, unclosedQuotes.size());
  }
@Test
  public void testRequiresWithAttributionDisabled() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertFalse(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequiresWithAttributionEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testEmptyTextAnnotation() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "";
    List<CoreLabel> tokens = new ArrayList<>();

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNull(quotes);
  }
@Test
  public void testNoSentencesPresent() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Orphan quote'";
    CoreLabel token = new CoreLabel(); token.setOriginalText("'Orphan"); token.setBeginPosition(0); token.setEndPosition(7);
    CoreLabel token2 = new CoreLabel(); token2.setOriginalText("quote'"); token2.setBeginPosition(8); token2.setEndPosition(14);
    List<CoreLabel> tokens = Arrays.asList(token, token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty()); 
  }
@Test
  public void testSingleDirectedUnicodeQuote() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "Here is a quote: “This is it.”";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("Here"); t1.setBeginPosition(0); t1.setEndPosition(4);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("is"); t2.setBeginPosition(5); t2.setEndPosition(7);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("a"); t3.setBeginPosition(8); t3.setEndPosition(9);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("quote:"); t4.setBeginPosition(10); t4.setEndPosition(16);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("“This"); t5.setBeginPosition(17); t5.setEndPosition(22);
    CoreLabel t6 = new CoreLabel(); t6.setOriginalText("is"); t6.setBeginPosition(23); t6.setEndPosition(25);
    CoreLabel t7 = new CoreLabel(); t7.setOriginalText("it.”"); t7.setBeginPosition(26); t7.setEndPosition(30);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6, t7);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testQuoteAtEndOfText() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "And he said, 'goodbye'";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("And"); t1.setBeginPosition(0); t1.setEndPosition(3);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("he"); t2.setBeginPosition(4); t2.setEndPosition(6);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("said,"); t3.setBeginPosition(7); t3.setEndPosition(12);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("'goodbye'"); t4.setBeginPosition(13); t4.setEndPosition(22);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testOverlappingQuotesShouldNest() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"He said, 'Hello!' and smiled.\"";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("\"He"); t1.setBeginPosition(0); t1.setEndPosition(3);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("said,"); t2.setBeginPosition(4); t2.setEndPosition(9);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("'Hello!'"); t3.setBeginPosition(10); t3.setEndPosition(18);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("and"); t4.setBeginPosition(19); t4.setEndPosition(22);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("smiled.\""); t5.setBeginPosition(23); t5.setEndPosition(31);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    List<CoreMap> nested = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(nested);
    assertEquals(1, nested.size());
  }
@Test
  public void testQuoteTooLongIsExcluded() {
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "10");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"This quote is very long and should be excluded due to maxLength filter.\"";
    CoreLabel token = new CoreLabel(); token.setOriginalText(text); token.setBeginPosition(0); token.setEndPosition(text.length());
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue(quotes == null || quotes.isEmpty());
  }
@Test
  public void testMultipleNonNestedQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'First.' And then he said: 'Second.'";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'First.'"); t1.setBeginPosition(0); t1.setEndPosition(8);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("And"); t2.setBeginPosition(9); t2.setEndPosition(12);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("then"); t3.setBeginPosition(13); t3.setEndPosition(17);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("he"); t4.setBeginPosition(18); t4.setEndPosition(20);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("said:"); t5.setBeginPosition(21); t5.setEndPosition(26);
    CoreLabel t6 = new CoreLabel(); t6.setOriginalText("'Second.'"); t6.setBeginPosition(27); t6.setEndPosition(36);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testEmbeddedSameQuoteTypeAllowed() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'She said, 'Get out!''";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'She"); t1.setBeginPosition(0); t1.setEndPosition(4);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("said,"); t2.setBeginPosition(5); t2.setEndPosition(10);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("'Get"); t3.setBeginPosition(11); t3.setEndPosition(15);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("out!''"); t4.setBeginPosition(16); t4.setEndPosition(22);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation(text);
    ann.set(CoreAnnotations.TextAnnotation.class, text);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<CoreMap> quotes = ann.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    List<CoreMap> nested = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(nested);
    assertEquals(1, nested.size());
  }
@Test
  public void testUnmatchedQuoteWarningSuppressedWhenUnclosedExtractionDisabled() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"This is never closed.";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("\"This"); t1.setBeginPosition(0); t1.setEndPosition(5);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("is"); t2.setBeginPosition(6); t2.setEndPosition(8);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("never"); t3.setBeginPosition(9); t3.setEndPosition(14);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("closed."); t4.setBeginPosition(15); t4.setEndPosition(22);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation(text);
    ann.set(CoreAnnotations.TextAnnotation.class, text);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<CoreMap> quotes = ann.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNull(quotes); 
  }
@Test
  public void testLatexBacktickQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "He said, ``This is LaTeX style.''";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("He"); t1.setBeginPosition(0); t1.setEndPosition(2);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("said,"); t2.setBeginPosition(3); t2.setEndPosition(8);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("``This"); t3.setBeginPosition(9); t3.setEndPosition(15);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("is"); t4.setBeginPosition(16); t4.setEndPosition(18);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("LaTeX"); t5.setBeginPosition(19); t5.setEndPosition(24);
    CoreLabel t6 = new CoreLabel(); t6.setOriginalText("style.''"); t6.setBeginPosition(25); t6.setEndPosition(33);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation(text);
    ann.set(CoreAnnotations.TextAnnotation.class, text);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<CoreMap> quotes = ann.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
    assertTrue(quoteText.contains("This is LaTeX style."));
  }
@Test
  public void testWhitespaceOnlyBetweenQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"   \"";
    CoreLabel token = new CoreLabel(); token.setOriginalText("\"   \""); token.setBeginPosition(0); token.setEndPosition(5);
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation(text);
    ann.set(CoreAnnotations.TextAnnotation.class, text);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<CoreMap> quotes = ann.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testQuoteAtTextStart() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Start of text.' Ends here.";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Start"); t1.setBeginPosition(0); t1.setEndPosition(6);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("of"); t2.setBeginPosition(7); t2.setEndPosition(9);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("text.'"); t3.setBeginPosition(10); t3.setEndPosition(16);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("Ends"); t4.setBeginPosition(17); t4.setEndPosition(21);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("here."); t5.setBeginPosition(22); t5.setEndPosition(28);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation(text);
    ann.set(CoreAnnotations.TextAnnotation.class, text);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<CoreMap> quotes = ann.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
    assertTrue(quoteText.startsWith("'Start"));
  }
@Test
  public void testQuoteWithoutTokensAnnotation() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"Missing tokens case.\"";

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation(text);
    ann.set(CoreAnnotations.TextAnnotation.class, text);
    ann.set(CoreAnnotations.TokensAnnotation.class, null);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<CoreMap> quotes = ann.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNull(quotes);
  }
@Test
  public void testQuoteWithoutSentenceMetadata() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Text with no sentence info'";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Text"); t1.setBeginPosition(0); t1.setEndPosition(5);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("with"); t2.setBeginPosition(6); t2.setEndPosition(10);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("no"); t3.setBeginPosition(11); t3.setEndPosition(13);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("sentence"); t4.setBeginPosition(14); t4.setEndPosition(22);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("info'"); t5.setBeginPosition(23); t5.setEndPosition(28);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5);

    Annotation ann = new Annotation(text);
    ann.set(CoreAnnotations.TextAnnotation.class, text);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, null);

    annotator.annotate(ann);
    List<CoreMap> quotes = ann.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty()); 
  }
@Test
  public void testQuoteWithZeroLength() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"\"";
    CoreLabel token = new CoreLabel(); token.setOriginalText("\"\""); token.setBeginPosition(0); token.setEndPosition(2);
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testMultipleConsecutiveQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'First' 'Second' 'Third'";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'First'"); t1.setBeginPosition(0); t1.setEndPosition(7);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("'Second'"); t2.setBeginPosition(8); t2.setEndPosition(16);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("'Third'"); t3.setBeginPosition(17); t3.setEndPosition(24);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(3, quotes.size());
  }
@Test
  public void testNestedQuotesMixedTypes() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"She said, 'He replied, “Yes.”'\"";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("\"She"); t1.setBeginPosition(0); t1.setEndPosition(4);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("said,"); t2.setBeginPosition(5); t2.setEndPosition(10);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("'He"); t3.setBeginPosition(11); t3.setEndPosition(14);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("replied,"); t4.setBeginPosition(15); t4.setEndPosition(23);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("“Yes.”'\""); t5.setBeginPosition(24); t5.setEndPosition(33);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
    List<CoreMap> topQuotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(topQuotes);
    assertEquals(1, topQuotes.size());
    List<CoreMap> embedded1 = topQuotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(embedded1);
    assertEquals(1, embedded1.size());
    List<CoreMap> embedded2 = embedded1.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(embedded2);
    assertEquals(1, embedded2.size());
  }
@Test
  public void testMaxLengthEqualBoundary() {
    Properties props = new Properties();
    props.setProperty("quote.maxLength", "22"); 
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"This is maxLength quote\"";
    CoreLabel token = new CoreLabel(); token.setOriginalText(text); token.setBeginPosition(0); token.setEndPosition(text.length());
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testReplaceUnicodeQuotesConversion() {
    String input = "He said, “Hello” and ‘Goodbye’.";
    String output = QuoteAnnotator.replaceUnicode(input);
    assertNotNull(output);
    assertTrue(output.contains("Hello"));
    assertTrue(output.contains("\""));
    assertFalse(output.contains("“"));
    assertFalse(output.contains("”"));
    assertFalse(output.contains("‘"));
    assertFalse(output.contains("’"));
  }
@Test
  public void testGetQuoteComparatorOrdering() {
    CoreLabel token1 = new CoreLabel(); token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    CoreLabel token2 = new CoreLabel(); token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);

    Annotation q1 = new Annotation("First");
    q1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);

    Annotation q2 = new Annotation("Second");
    q2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(q1);
    quotes.add(q2);

    quotes.sort(QuoteAnnotator.getQuoteComparator());
    assertEquals(quotes.get(0), q2);
    assertEquals(quotes.get(1), q1);
  }
@Test
  public void testXmlFreeTextStripsNonTokens() {
    String rawText = "<xml><tag>“Hello”</tag></xml>";
    CoreLabel token = new CoreLabel();
    token.setOriginalText("Hello");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation(rawText);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    String cleaned = QuoteAnnotator.xmlFreeText(rawText, annotation);
    assertEquals(rawText.length(), cleaned.length());
    assertTrue(cleaned.contains("Hello"));
    assertFalse(cleaned.contains("xml"));
    assertTrue(cleaned.replaceAll("[^\\s]", "").length() > 0);
  }
@Test
  public void testQuotationEmbeddedButEmpty() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Empty '' nested'";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Empty"); t1.setBeginPosition(0); t1.setEndPosition(6);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("''"); t2.setBeginPosition(7); t2.setEndPosition(9);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("nested'"); t3.setBeginPosition(10); t3.setEndPosition(17);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> topQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(topQuotes);
    assertEquals(1, topQuotes.size());
    List<CoreMap> embedded = topQuotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(embedded);
    assertEquals(1, embedded.size());
    assertEquals("''", embedded.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testOnlyOpeningQuotePresent() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Unclosed quote without matching end";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Unclosed"); t1.setBeginPosition(0); t1.setEndPosition(9);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("quote"); t2.setBeginPosition(10); t2.setEndPosition(15);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("without"); t3.setBeginPosition(16); t3.setEndPosition(23);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("matching"); t4.setBeginPosition(24); t4.setEndPosition(32);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("end"); t5.setBeginPosition(33); t5.setEndPosition(36);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> unclosedQuotes = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosedQuotes);
    assertEquals(1, unclosedQuotes.size());
    assertTrue(unclosedQuotes.get(0).get(CoreAnnotations.TextAnnotation.class).startsWith("'Unclosed"));
  }
@Test
  public void testMultipleLevelsOfNesting() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Level one 'Level two 'Level three'' end'";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Level"); t1.setBeginPosition(0); t1.setEndPosition(6);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("one"); t2.setBeginPosition(7); t2.setEndPosition(10);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("'Level"); t3.setBeginPosition(11); t3.setEndPosition(17);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("two"); t4.setBeginPosition(18); t4.setEndPosition(21);
    CoreLabel t5 = new CoreLabel(); t5.setOriginalText("'Level"); t5.setBeginPosition(22); t5.setEndPosition(28);
    CoreLabel t6 = new CoreLabel(); t6.setOriginalText("three''"); t6.setBeginPosition(29); t6.setEndPosition(36);
    CoreLabel t7 = new CoreLabel(); t7.setOriginalText("end'"); t7.setBeginPosition(37); t7.setEndPosition(41);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6, t7);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
    List<CoreMap> level1 = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(level1);
    assertEquals(1, level1.size());
    List<CoreMap> level2 = level1.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(level2);
    assertEquals(1, level2.size());
    List<CoreMap> level3 = level2.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(level3);
    assertEquals(1, level3.size());
  }
@Test
  public void testTextWithOnlyPunctuation() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = ".,?!;:“”‘’«»";
    CoreLabel token = new CoreLabel();
    token.setOriginalText(text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue(quotes == null || quotes.isEmpty());
  }
@Test
  public void testUnescapedLatexQuotesWithSmartQuotesDisabled() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "``Unescaped latex style quotes.''";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("``Unescaped"); t1.setBeginPosition(0); t1.setEndPosition(11);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("latex"); t2.setBeginPosition(12); t2.setEndPosition(17);
    CoreLabel t3 = new CoreLabel(); t3.setOriginalText("style"); t3.setBeginPosition(18); t3.setEndPosition(23);
    CoreLabel t4 = new CoreLabel(); t4.setOriginalText("quotes.''"); t4.setBeginPosition(24); t4.setEndPosition(33);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testUnicodeQuotesAsMalformed() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.asciiQuotes", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "He said “YES!” and walked away.";
    CoreLabel token = new CoreLabel();
    token.setOriginalText(text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);
    List<CoreMap> quotes = doc.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue(quotes == null || quotes.isEmpty());
  }
@Test
  public void testQuoteWithNonStandardUnicodeOpeningOnly() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "«Unclosed guillemet quote";
    CoreLabel token = new CoreLabel();
    token.setOriginalText(text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosed);
    assertEquals(1, unclosed.size());
    String unclosedText = unclosed.get(0).get(CoreAnnotations.TextAnnotation.class);
    assertTrue(unclosedText.contains("Unclosed"));
  }
@Test
  public void testQuoteWithMismatchedDirectedQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "‘Mismatched directed quote”";
    CoreLabel token = new CoreLabel();
    token.setOriginalText(text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertTrue(quotes == null || quotes.isEmpty());
    assertNotNull(unclosed);
    assertEquals(1, unclosed.size());
  }
@Test
  public void testQuoteSpanningMultipleSentences() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'This is sentence one. This is sentence two.'";
    CoreLabel token = new CoreLabel();
    token.setOriginalText(text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence1 = new Annotation(text);
    sentence1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);
    sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation sentence2 = new Annotation(text);
    sentence2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 29);
    sentence2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);
    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    CoreMap q = quotes.get(0);
    assertEquals(Integer.valueOf(0), q.get(CoreAnnotations.SentenceBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), q.get(CoreAnnotations.SentenceEndAnnotation.class));
  }
@Test
  public void testQuoteAnnotationIndexingAppliedToTokens() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Index test.'";
    CoreLabel t1 = new CoreLabel(); t1.setOriginalText("'Index"); t1.setBeginPosition(0); t1.setEndPosition(6);
    CoreLabel t2 = new CoreLabel(); t2.setOriginalText("test.'"); t2.setBeginPosition(7); t2.setEndPosition(13);
    List<CoreLabel> tokens = Arrays.asList(t1, t2);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);
    List<CoreMap> quotes = doc.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    Integer quoteIndex = quotes.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class);
    CoreLabel tok0 = tokens.get(0);
    CoreLabel tok1 = tokens.get(1);
    assertEquals(quoteIndex, tok0.get(CoreAnnotations.QuotationIndexAnnotation.class));
    assertEquals(quoteIndex, tok1.get(CoreAnnotations.QuotationIndexAnnotation.class));
  }
@Test
  public void testRejectedQuoteWithNoTokenMatches() {
    Properties props = new Properties();
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.smartQuotes", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"Wrong offset\"";

    
    CoreLabel token = new CoreLabel();
    token.setOriginalText("Extra");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 100);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 105);

    Annotation sentence = new Annotation("Extra");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 100);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 105);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);
    List<CoreMap> quotes = doc.get(CoreAnnotations.QuotationsAnnotation.class);
    assertTrue(quotes == null || quotes.isEmpty());
  }
@Test
  public void testXmlFreeTextPreservesLengthWithOnlyWhitespaceTokens() {
    String raw = "<tag>  \n  </tag>";
    CoreLabel token = new CoreLabel();
    token.setOriginalText(" ");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation ann = new Annotation(raw);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    String out = QuoteAnnotator.xmlFreeText(raw, ann);
    assertEquals(raw.length(), out.length());
    assertTrue(out.chars().allMatch(c -> Character.isWhitespace(c)));
  }
@Test
  public void testQuoteWithSameOpeningClosingUnicodeAllowedNested() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "„Outer „Inner” Outer continues.”";
    CoreLabel token = new CoreLabel();
    token.setOriginalText(text);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    doc.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);
    List<CoreMap> outerQuotes = doc.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(outerQuotes);
    assertEquals(1, outerQuotes.size());
    List<CoreMap> innerQuotes = outerQuotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(innerQuotes);
    assertEquals(1, innerQuotes.size());
    assertTrue(innerQuotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Inner"));
  }
@Test
  public void testQuoteWithOnlySpecialCharactersInside() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'!@#$%^&*()'";
    CoreLabel token = new CoreLabel();
    token.setOriginalText("!@#$%^&*()");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("@"));
  }
@Test
  public void testQuoteWithOnlyNewlinesInside() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'\n\n'";
    CoreLabel token = new CoreLabel();
    token.setOriginalText("\n\n");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("\n"));
  }
@Test
  public void testAsciiQuotesWithSmartQuotesAndAsciiQuotesBothTrue() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"Combining smart and ascii.\"";
    CoreLabel token = new CoreLabel();
    token.setOriginalText("Combining smart and ascii.");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testQuoteFollowedImmediatelyByAnotherQuote() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "'Quote1''Quote2'";
    CoreLabel token = new CoreLabel();
    token.setOriginalText("Quote1");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("Quote2");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    List<CoreLabel> tokens = Arrays.asList(token, token2);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testQuoteWithoutAnyValidContentBetweenDelimiters() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    props.setProperty("quote.maxLength", "5");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "\"\"";
    CoreLabel token = new CoreLabel();
    token.setOriginalText("");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testSmartQuotesChoosesUnicodeWhenMoreUnicodeQuotesThanAscii() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(props);

    String text = "“Unicode one” and ‘Unicode two’ and \"Ascii three\"";
    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("Unicode one");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("Unicode two");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 29);

    CoreLabel token3 = new CoreLabel();
    token3.setOriginalText("Ascii three");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 37);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 49);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.size() >= 2);
    assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Unicode"));
  } 
}