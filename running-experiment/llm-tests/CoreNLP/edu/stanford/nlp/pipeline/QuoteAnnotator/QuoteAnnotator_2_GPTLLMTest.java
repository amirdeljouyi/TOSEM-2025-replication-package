package edu.stanford.nlp.pipeline;


import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAnnotator_2_GPTLLMTest {

 @Test
  public void testAsciiDoubleQuotes() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "He said, \"Hello world!\" and walked away.";

    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("He");
    token1.setBeginPosition(0);
    token1.setEndPosition(2);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("said,");
    token2.setBeginPosition(3);
    token2.setEndPosition(8);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.setOriginalText("\"Hello");
    token3.setBeginPosition(9);
    token3.setEndPosition(15);
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreLabel token4 = new CoreLabel();
    token4.setOriginalText("world!\"");
    token4.setBeginPosition(16);
    token4.setEndPosition(23);
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);

    CoreLabel token5 = new CoreLabel();
    token5.setOriginalText("and");
    token5.setBeginPosition(24);
    token5.setEndPosition(27);
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 24);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

    CoreLabel token6 = new CoreLabel();
    token6.setOriginalText("walked");
    token6.setBeginPosition(28);
    token6.setEndPosition(34);
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 28);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 34);

    CoreLabel token7 = new CoreLabel();
    token7.setOriginalText("away.");
    token7.setBeginPosition(35);
    token7.setEndPosition(40);
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 35);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);

    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);

    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap quote = quotes.get(0);
    String quoteText = quote.get(CoreAnnotations.TextAnnotation.class);
    assertTrue(quoteText.contains("Hello world!"));
  }
@Test
  public void testSingleQuotesDisabledByDefault() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "She said, 'That is fine.'";

    CoreLabel t1 = new CoreLabel();
    t1.setOriginalText("She");
    t1.setBeginPosition(0);
    t1.setEndPosition(3);
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel t2 = new CoreLabel();
    t2.setOriginalText("said,");
    t2.setBeginPosition(4);
    t2.setEndPosition(9);
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel t3 = new CoreLabel();
    t3.setOriginalText("'That");
    t3.setBeginPosition(10);
    t3.setEndPosition(15);
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreLabel t4 = new CoreLabel();
    t4.setOriginalText("is");
    t4.setBeginPosition(16);
    t4.setEndPosition(18);
    t4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    t4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

    CoreLabel t5 = new CoreLabel();
    t5.setOriginalText("fine.'");
    t5.setBeginPosition(19);
    t5.setEndPosition(25);
    t5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
    t5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);

    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testReplaceUnicodeQuotes() {
    String input = "She said, “Hello”.";
    String expected = "She said, \"Hello\".";

    String result = QuoteAnnotator.replaceUnicode(input);
    assertEquals(expected, result);
  }
@Test
  public void testGetQuoteComparator() {
    CoreMap q1 = new Annotation("One");
    q1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);

    CoreMap q2 = new Annotation("Two");
    q2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(q2);
    quotes.add(q1);

    quotes.sort(QuoteAnnotator.getQuoteComparator());

    assertEquals(5, quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class).intValue());
    assertEquals(10, quotes.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class).intValue());
  }
@Test
  public void testRequirementsSatisfiedWhenAttributeQuotesTrue() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "true");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.QuotationsAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedWhenAttributeQuotesFalse() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.QuotationsAnnotation.class));
  }
@Test
public void testNullTextAnnotation() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  Annotation annotation = new Annotation((String) null);
  annotation.set(CoreAnnotations.TextAnnotation.class, null);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to null text");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testEmptyDocumentText() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "";
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
public void testImbalancedQuotesOnlyOpening() {
  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "This is an \"unfinished quote.";
  Annotation annotation = new Annotation(text);

  CoreLabel token = new CoreLabel();
  token.setOriginalText("This");
  token.setBeginPosition(0);
  token.setEndPosition(4);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);
  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(unclosed);
  assertEquals(1, unclosed.size());
}
@Test
public void testImbalancedQuotesOnlyClosing() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "This is bad\"";
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
public void testOverlapQuotesWithoutAllowEmbedded() {
  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "false");
  props.setProperty("quote.singleQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "\"He said, 'Hi there.'\"";
  Annotation annotation = new Annotation(text);

  CoreLabel token = new CoreLabel();
  token.setOriginalText("\"He");
  token.setBeginPosition(0);
  token.setEndPosition(3);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());
  List<CoreMap> nested = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNull(nested);
}
@Test
public void testInternationalQuotesWithSmartEnabled() {
  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "He said, 『I saw her.』";
  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(quoteText.contains("I saw her"));
}
@Test
public void testQuoteWithOnlyWhitespaceContent() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "\"   \"";
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
public void testSingleBacktickUnhandled() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "`Unfinished";

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
public void testExtremeMaxLengthRejectsValidQuote() {
  Properties props = new Properties();
  props.setProperty("quote.maxLength", "5");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"This is too long for a quote\"";

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
public void testNoTokensPresentInAnnotation() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "\"Quote without token data.\"";
  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(1, quotes.size());
  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Quote without token data"));
}
@Test
public void testSymmetricalAsciiQuotesWithNoWhitespaceBoundary() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "\"Quote_at_start\"followedbyword";
  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);

  assertEquals(1, quotes.size());
  String quoted = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(quoted.contains("Quote_at_start"));
}
@Test
public void testNestedDoubleQuotesWithAllowEmbeddedFalse() {
  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "false");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"Outer quote with nested \"inner quote\" inside.\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  CoreMap outer = quotes.get(0);
  List<CoreMap> embedded = outer.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNull(embedded);
}
@Test
public void testNestedDirectedQuotesWithAllowEmbeddedTrue() {
  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "true");
  props.setProperty("quote.smartQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "She said, “He replied, ‘Yes.’”";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  CoreMap outer = quotes.get(0);
  List<CoreMap> embedded = outer.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(embedded);
  assertEquals(1, embedded.size());

  String embeddedText = embedded.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(embeddedText.contains("Yes"));
}
@Test
public void testMultipleConsecutiveQuotes() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"First.\" \"Second.\" \"Third.\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(3, quotes.size());

  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("First"));
  assertTrue(quotes.get(1).get(CoreAnnotations.TextAnnotation.class).contains("Second"));
  assertTrue(quotes.get(2).get(CoreAnnotations.TextAnnotation.class).contains("Third"));
}
@Test
public void testGetQuotesRecursionWithDeepNesting() {
  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "true");
  props.setProperty("quote.singleQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"Level 1 'Level 2 `Level 3'`\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  List<CoreMap> level2 = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(level2);
  assertEquals(1, level2.size());

  List<CoreMap> level3 = level2.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(level3);
  assertEquals(1, level3.size());

  assertTrue(level3.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Level 3"));
}
@Test
public void testReplaceUnicodeWithMixedContent() {
  String input = "“Quoted” and ‘another’ and «more» and \"legacy\"";
  String expected = "\"Quoted\" and 'another' and \"more\" and \"legacy\"";

  String output = QuoteAnnotator.replaceUnicode(input);
  assertEquals(expected, output);
}
@Test
public void testUnicodeReplacedWithAsciiQuotesOptionOn() {
  Properties props = new Properties();
  props.setProperty("quote.asciiQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "He said, “Convert this.”";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(quoteText.contains("Convert this"));
}
@Test
public void testWhitespaceOnlyBetweenTokens() {
  String text = "Some <xml> here";
  Annotation annotation = new Annotation(text);

  CoreLabel t1 = new CoreLabel();
  t1.setOriginalText("Some");
  t1.setBeginPosition(0);
  t1.setEndPosition(4);
  t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreLabel t2 = new CoreLabel();
  t2.setOriginalText("here");
  t2.setBeginPosition(11);
  t2.setEndPosition(15);
  t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
  t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

  List<CoreLabel> tokens = Arrays.asList(t1, t2);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  String clean = QuoteAnnotator.xmlFreeText(text, annotation);

  assertEquals(text.length(), clean.length());
  assertTrue(clean.substring(4, 11).trim().isEmpty());
}
@Test
public void testQuoteIndexPropagationToTokens() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "He said, \"Yes!\"";

  CoreLabel t1 = new CoreLabel();
  t1.setOriginalText("He");
  t1.setBeginPosition(0);
  t1.setEndPosition(2);
  t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

  CoreLabel t2 = new CoreLabel();
  t2.setOriginalText("said,");
  t2.setBeginPosition(3);
  t2.setEndPosition(8);
  t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
  t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

  CoreLabel t3 = new CoreLabel();
  t3.setOriginalText("\"Yes!\"");
  t3.setBeginPosition(9);
  t3.setEndPosition(16);
  t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

  List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

  CoreMap sentence = new Annotation(text);
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentences = Collections.singletonList(sentence);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  Integer index = quotes.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class);

  for (CoreLabel token : tokens) {
    if (token.beginPosition() >= 9 && token.endPosition() <= 16) {
      Integer idx = token.get(CoreAnnotations.QuotationIndexAnnotation.class);
      assertEquals(index, idx);
    }
  }
}
@Test
public void testQuoteSpanningMultipleSentences() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "He began, \"This is the first. This is the second.\" Then stopped.";

  CoreMap s1 = new Annotation("He began, \"This is the first.");
  s1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  s1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);
  s1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreMap s2 = new Annotation(" This is the second.\" Then stopped.");
  s2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 33);
  s2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  s2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

  List<CoreMap> sentences = Arrays.asList(s1, s2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  CoreMap quote = quotes.get(0);
  int beginSent = quote.get(CoreAnnotations.SentenceBeginAnnotation.class);
  int endSent = quote.get(CoreAnnotations.SentenceEndAnnotation.class);
  assertEquals(0, beginSent);
  assertEquals(1, endSent);
}
@Test
public void testDuplicateQuotesDifferentDelimiters() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "“Hello.” \"Hello.\" 'Hello.'";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(3, quotes.size());
}
@Test
public void testEdgeQuoteWithoutTokens() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = " \"Just quoted.\" ";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());
}
@Test
public void testZeroWidthQuotePair() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "\"\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(0, quotes.size());
}
@Test
public void testMultipleNestedUnclosedQuotes() {
  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");
  props.setProperty("quote.singleQuotes", "true");
  props.setProperty("quote.allowEmbeddedSame", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "\"Outer 'Inner `Deepest\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  annotator.annotate(annotation);

  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(unclosed);
  assertEquals(1, unclosed.size());

  List<CoreMap> inner = unclosed.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(inner);
  assertEquals(1, inner.size());

  List<CoreMap> innerMost = inner.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(innerMost);
  assertEquals(1, innerMost.size());
}
@Test
public void testSetAnnotationsHandlesEmptyQuoteLists() {
  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "Nothing to quote.";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertTrue(quotes.isEmpty());

  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(unclosed);
  assertTrue(unclosed.isEmpty());
}
@Test
public void testQuoteWithOffsetMisalignedToken() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "Text with \"quote\" where token offset skips.";

  CoreLabel token = new CoreLabel();
  token.setOriginalText("\"quote\"");
  token.setBeginPosition(10);
  token.setEndPosition(17);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

  List<CoreLabel> tokens = Collections.singletonList(token);

  CoreMap sentence = new Annotation(text);
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = Collections.singletonList(sentence);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(1, quotes.size());
  String result = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(result.contains("quote"));
}
@Test
public void testMultipleSameTypeNestedQuotesWithEmbeddingDisabled() {
  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "false");
  props.setProperty("quote.singleQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "'A quote with 'embedded' single quotes' outside.";
  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(1, quotes.size()); 
  List<CoreMap> embedded = quotes.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertNull(embedded); 
}
@Test
public void testUnclosedQuoteTriggersRecursiveParsing() {
  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "He said, \"This has an 'inner quote that never ends.";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  annotator.annotate(annotation);

  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(unclosed);
  assertEquals(1, unclosed.size());
  List<CoreMap> nested = unclosed.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(nested);
  assertEquals(1, nested.size());
}
@Test
public void testMaxLengthQuoteJustAtBoundary() {
  Properties props = new Properties();
  props.setProperty("quote.maxLength", "8");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"1234567\""; 

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
public void testEmbeddedUnicodeQuoteWithValidWrapping() {
  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.allowEmbeddedSame", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "“Top level quote with 『an embedded Unicode quote』 inside.”";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc123");

  annotator.annotate(annotation);
  List<CoreMap> outerQuotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(outerQuotes);
  assertEquals(1, outerQuotes.size());

  CoreMap outer = outerQuotes.get(0);
  List<CoreMap> innerQuotes = outer.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(innerQuotes);
  assertEquals(1, innerQuotes.size());

  String innerText = innerQuotes.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(innerText.contains("an embedded Unicode quote"));
}
@Test
public void testQuoteComparatorHandlesSameOffset() {
  CoreMap q1 = new Annotation("Quote A");
  q1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  CoreMap q2 = new Annotation("Quote B");
  q2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);

  int result = QuoteAnnotator.getQuoteComparator().compare(q1, q2);
  assertEquals(0, result);
}
@Test
public void testQuoteAlignedToSentenceWithTrailingQuote() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "The sentence ends with a quote.\"";

  CoreMap sentence = new Annotation(text);
  sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  int endSent = quotes.get(0).get(CoreAnnotations.SentenceEndAnnotation.class);
  assertEquals(0, endSent);
}
@Test
public void testQuoteAnnotationWithoutDocId() {
  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "“No doc ID is present.”";

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
public void testUnbalancedSmartQuotesClosingOnly() {
  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "This is an orphan closing smart quote.”";

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
public void testQuoteSpanningMultipleInvalidSentences() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "“Spanning badly indexed sentences”";

  CoreMap s1 = new Annotation("“Spanning badly");
  s1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  s1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
  s1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreMap s2 = new Annotation(" indexed sentences”");
  s2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 999); 
  s2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9999);
  s2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

  List<CoreMap> sentences = Arrays.asList(s1, s2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  Integer endSent = quotes.get(0).get(CoreAnnotations.SentenceEndAnnotation.class);
  assertEquals(1, (int) endSent);
}
@Test
public void testSmartQuotesWithAsciiQuotesFailsOverToAscii() {
  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.asciiQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"Using ascii after smart fails\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertEquals(1, quotes.size());
  String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(quoteText.contains("Using ascii"));
}
@Test
public void testQuoteIncludesNonBMPUnicodeCharacters() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String emoji = new String(Character.toChars(0x1F600)); 
  String text = "\"Hello " + emoji + " world!\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());

  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains(emoji));
}
@Test
public void testUnclosedQuoteWithMalformedEmbeddedQuote() {
  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");
  props.setProperty("quote.allowEmbeddedSame", "false");
  props.setProperty("quote.singleQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"Outer starts but inner is 'never finished.";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);
  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);

  assertNotNull(unclosed);
  assertFalse(unclosed.isEmpty());
  List<CoreMap> nested = unclosed.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(nested);
  assertEquals(1, nested.size());
}
@Test
public void testQuoteIndexAnnotationInNestedQuotes() {
  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "true");
  props.setProperty("quote.singleQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"Outer 'Inner'\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);

  List<CoreMap> outer = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(outer);
  assertEquals(1, outer.size());

  Integer outerIndex = outer.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class);
  List<CoreMap> inner = outer.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  Integer innerIndex = inner.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class);

  assertNotEquals(outerIndex, innerIndex);
  assertTrue(outerIndex < innerIndex);
}
@Test
public void testNullNestedQuotesHandledGracefully() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  CoreMap quote = new Annotation("Fake Quote");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  quote.set(CoreAnnotations.QuotationsAnnotation.class, null);

  List<CoreMap> input = Collections.singletonList(quote);
  QuoteAnnotator.getQuoteComparator().compare(quote, quote); 

  
  List<CoreMap> embedded = QuoteAnnotator.gatherQuotes(quote);
  assertTrue(embedded.isEmpty());
}
@Test
public void testDirectedQuotePairMisaligned() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "“Hello ‘mismatch.”"; 

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
public void testOverlappingQuotesIgnoredWhenEmbeddedSameFalse() {
  Properties props = new Properties();
  props.setProperty("quote.allowEmbeddedSame", "false");
  props.setProperty("quote.singleQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "'Outer 'Inner' Overlap'";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);

  assertNotNull(quotes);
  assertEquals(1, quotes.size()); 
  CoreMap quote = quotes.get(0);
  List<CoreMap> embedded = quote.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNull(embedded); 
}
@Test
public void testAsciiQuotesOffPreventsAsciiParsing() {
  Properties props = new Properties();
  props.setProperty("quote.asciiQuotes", "false");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"ASCII quote with asciiQuotes set to false\"";

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
public void testEmptyStringInput() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

  String text = "";

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
public void testUnclosedAsciiQuoteIsCapturedProperlyWhenEnabled() {
  Properties props = new Properties();
  props.setProperty("quote.extractUnclosedQuotes", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "He said: \"Unclosed quote here.";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);
  List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
  assertNotNull(unclosed);
  assertEquals(1, unclosed.size());
  String extracted = unclosed.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(extracted.contains("Unclosed quote"));
}
@Test
public void testSmartQuotePairOutsideAsciiDisabled() {
  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.asciiQuotes", "false");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "“Smart start and end.”";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);

  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());
  String quoteText = quotes.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(quoteText.contains("Smart"));
}
@Test
public void testMultipleNestedUnicodeSmartQuotes() {
  Properties props = new Properties();
  props.setProperty("quote.smartQuotes", "true");
  props.setProperty("quote.allowEmbeddedSame", "true");

  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "“Level one 『Level two 「Level three」』.”";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);
  List<CoreMap> levelOne = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, levelOne.size());

  CoreMap quoteOne = levelOne.get(0);
  List<CoreMap> levelTwo = quoteOne.get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, levelTwo.size());

  List<CoreMap> levelThree = levelTwo.get(0).get(CoreAnnotations.QuotationsAnnotation.class);
  assertEquals(1, levelThree.size());

  String innerText = levelThree.get(0).get(CoreAnnotations.TextAnnotation.class);
  assertTrue(innerText.contains("Level three"));
}

@Test
public void testMultipleAdjacentQuotesWithoutTokens() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"Quote1\" \"Quote2\"";

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(2, quotes.size());
  assertTrue(quotes.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Quote1"));
  assertTrue(quotes.get(1).get(CoreAnnotations.TextAnnotation.class).contains("Quote2"));
}
@Test
public void testDiscardZeroLengthQuoteBetweenPair() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String text = "\"\"";

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
public void testQuoteWithMalformedSurrogatePairsIgnoredGracefully() {
  Properties props = new Properties();
  QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
  String malformed = "\"Hello \uD800 world!\""; 

  Annotation annotation = new Annotation(malformed);
  annotation.set(CoreAnnotations.TextAnnotation.class, malformed);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  annotator.annotate(annotation);
  List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
  assertNotNull(quotes);
  assertEquals(1, quotes.size());
} 
}