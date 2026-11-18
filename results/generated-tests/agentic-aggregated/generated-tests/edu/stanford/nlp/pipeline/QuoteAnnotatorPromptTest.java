package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class QuoteAnnotatorPromptTest {

  @Test
  public void defaultConfigurationFindsDoubleButNotSingleQuotes() {
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("He said \"yes\" and 'no'.");

    assertEquals(1, result.first().size());
    assertEquals(new Pair<>(8, 13), result.first().get(0));
    assertTrue(result.second().isEmpty());
  }

  @Test
  public void namedConstructorReadsOnlyNamedPrefix() {
    Properties properties = new Properties();
    properties.setProperty("quote.singleQuotes", "false");
    properties.setProperty("dialog.singleQuotes", "true");
    properties.setProperty("dialog.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("dialog", properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("'alpha'");

    assertEquals(Collections.singletonList(new Pair<>(0, 7)), result.first());
  }

  @Test(expected = NumberFormatException.class)
  public void constructorRejectsNonNumericMaximumLength() {
    Properties properties = new Properties();
    properties.setProperty("quote.maxLength", "many");
    properties.setProperty("quote.attributeQuotes", "false");

    new QuoteAnnotator(properties);
  }

  @Test
  public void maximumLengthDropsLongQuoteAndContinuesScanning() {
    Properties properties = new Properties();
    properties.setProperty("quote.maxLength", "5");
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("\"123456789\" then \"ok\"");

    assertEquals(1, result.first().size());
    assertEquals(new Pair<>(17, 21), result.first().get(0));
  }

  @Test
  public void findsSeveralDirectedQuoteConventions() {
    Properties properties = new Properties();
    properties.setProperty("quote.singleQuotes", "true");
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("“a” «b» ‹c› 「d」 『e』 „f” ‚g’");

    assertEquals(7, result.first().size());
    assertTrue(result.first().contains(new Pair<>(0, 3)));
    assertTrue(result.first().contains(new Pair<>(4, 7)));
    assertTrue(result.first().contains(new Pair<>(8, 11)));
    assertTrue(result.first().contains(new Pair<>(12, 15)));
    assertTrue(result.first().contains(new Pair<>(16, 19)));
    assertTrue(result.first().contains(new Pair<>(20, 23)));
    assertTrue(result.first().contains(new Pair<>(24, 27)));
  }

  @Test
  public void findsLatexStyleAndBacktickQuotes() {
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("``double'' and `single'");

    assertEquals(2, result.first().size());
    assertTrue(result.first().contains(new Pair<>(0, 10)));
    assertTrue(result.first().contains(new Pair<>(15, 23)));
  }

  @Test
  public void apostrophesInsideWordsAreNotQuotes() {
    Properties properties = new Properties();
    properties.setProperty("quote.singleQuotes", "true");
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("Don't touch Jones' book.");

    assertTrue(result.first().isEmpty());
    assertTrue(result.second().isEmpty());
  }

  @Test
  public void doubleQuoteMustHaveAValidClosingBoundary() {
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("\"word\"suffix");

    assertTrue(result.first().isEmpty());
  }

  @Test
  public void extractsUnclosedQuoteAndStillFindsNestedClosedQuote() {
    Properties properties = new Properties();
    properties.setProperty("quote.extractUnclosedQuotes", "true");
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("prefix \"unclosed with ‘closed’ inside");

    assertEquals(Collections.singletonList(new Pair<>(22, 30)), result.first());
    assertEquals(Collections.singletonList(new Pair<>(7, 37)), result.second());
  }

  @Test
  public void leavesUnclosedCollectionEmptyWhenExtractionDisabled() {
    Properties properties = new Properties();
    properties.setProperty("quote.extractUnclosedQuotes", "false");
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("before \"this never closes");

    assertTrue(result.first().isEmpty());
    assertTrue(result.second().isEmpty());
  }

  @Test
  public void recursivelyFindsDifferentQuoteTypeInsideOuterQuote() {
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("\"outer ‘inner’ tail\"");

    assertEquals(2, result.first().size());
    assertTrue(result.first().contains(new Pair<>(0, 20)));
    assertTrue(result.first().contains(new Pair<>(7, 14)));
  }

  @Test
  public void embeddedSameDirectedQuotesCanBeEnabled() {
    Properties properties = new Properties();
    properties.setProperty("quote.allowEmbeddedSame", "true");
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("“outer “inner” tail”");

    assertEquals(2, result.first().size());
    assertTrue(result.first().contains(new Pair<>(0, 20)));
    assertTrue(result.first().contains(new Pair<>(7, 14)));
  }

  @Test
  public void recursiveQuotesAppliesCallerOffset() {
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.recursiveQuotes("\"xy\"", 40, null);

    assertEquals(Collections.singletonList(new Pair<>(40, 44)), result.first());
  }

  @Test
  public void recursiveQuotesHonorsPreviousQuoteKind() {
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.recursiveQuotes("\"same\" and ‘other’", 0, "\"");

    assertEquals(Collections.singletonList(new Pair<>(11, 18)), result.first());
  }

  @Test
  public void unicodeReplacementNormalizesDirectionalMarks() {
    String replaced = QuoteAnnotator.replaceUnicode("“double” ‘single’ «angle»");

    assertEquals("\"double\" 'single' \"angle\"", replaced);
  }

  @Test
  public void punctuationAndWhitespaceClassificationCoversUnicode() {
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct(" "));
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct("\n"));
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct("—"));
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct("。"));
    assertFalse(QuoteAnnotator.isWhitespaceOrPunct("A"));
    assertFalse(QuoteAnnotator.isWhitespaceOrPunct("7"));
    assertFalse(QuoteAnnotator.isWhitespaceOrPunct("ab"));
  }

  @Test
  public void singleQuoteRecognitionIsExact() {
    assertTrue(QuoteAnnotator.isSingleQuote("'"));
    assertFalse(QuoteAnnotator.isSingleQuote("’"));
    assertFalse(QuoteAnnotator.isSingleQuote(""));
    assertFalse(QuoteAnnotator.isSingleQuote("''"));
  }

  @Test
  public void makeQuotePopulatesOffsetsTokensSentencesAndDocumentId() {
    CoreLabel opening = CoreLabel.wordFromString("\"");
    opening.setBeginPosition(5);
    opening.setEndPosition(6);
    CoreLabel word = CoreLabel.wordFromString("hello");
    word.setBeginPosition(6);
    word.setEndPosition(11);
    CoreLabel closing = CoreLabel.wordFromString("\"");
    closing.setBeginPosition(11);
    closing.setEndPosition(12);
    List<CoreLabel> tokens = Arrays.asList(opening, word, closing);

    Annotation quote = QuoteAnnotator.makeQuote("\"hello\"", 5, 12, tokens, 3, 1, 2, "doc-7");

    assertEquals("\"hello\"", quote.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(5), quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(12), quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertSame(tokens, quote.get(CoreAnnotations.TokensAnnotation.class));
    assertEquals(Integer.valueOf(3), quote.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(5), quote.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(1), quote.get(CoreAnnotations.SentenceBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), quote.get(CoreAnnotations.SentenceEndAnnotation.class));
    assertEquals("doc-7", quote.get(CoreAnnotations.DocIDAnnotation.class));
  }

  @Test
  public void makeQuoteAcceptsNullTokensAndDocumentId() {
    Annotation quote = QuoteAnnotator.makeQuote("text", 2, 6, null, -1, -1, -1, null);

    assertFalse(quote.containsKey(CoreAnnotations.TokensAnnotation.class));
    assertFalse(quote.containsKey(CoreAnnotations.TokenBeginAnnotation.class));
    assertFalse(quote.containsKey(CoreAnnotations.TokenEndAnnotation.class));
    assertFalse(quote.containsKey(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(Integer.valueOf(-1), quote.get(CoreAnnotations.SentenceBeginAnnotation.class));
    assertEquals(Integer.valueOf(-1), quote.get(CoreAnnotations.SentenceEndAnnotation.class));
  }

  @Test
  public void quoteComparatorOrdersByCharacterStart() {
    Annotation later = QuoteAnnotator.makeQuote("later", 20, 25, null, -1, 0, 0, null);
    Annotation earlier = QuoteAnnotator.makeQuote("earlier", 2, 9, null, -1, 0, 0, null);
    Annotation tied = QuoteAnnotator.makeQuote("tied", 20, 24, null, -1, 0, 0, null);
    Comparator<CoreMap> comparator = QuoteAnnotator.getQuoteComparator();

    assertTrue(comparator.compare(earlier, later) < 0);
    assertTrue(comparator.compare(later, earlier) > 0);
    assertEquals(0, comparator.compare(later, tied));
  }

  @Test
  public void coreMapConversionSortsEmbedsAndIndexesQuotesAndTokens() {
    String text = "\"A ‘B’ C\"";
    CoreLabel outerOpen = CoreLabel.wordFromString("\"");
    outerOpen.setBeginPosition(0);
    outerOpen.setEndPosition(1);
    CoreLabel a = CoreLabel.wordFromString("A");
    a.setBeginPosition(1);
    a.setEndPosition(2);
    CoreLabel innerOpen = CoreLabel.wordFromString("‘");
    innerOpen.setBeginPosition(3);
    innerOpen.setEndPosition(4);
    CoreLabel b = CoreLabel.wordFromString("B");
    b.setBeginPosition(4);
    b.setEndPosition(5);
    CoreLabel innerClose = CoreLabel.wordFromString("’");
    innerClose.setBeginPosition(5);
    innerClose.setEndPosition(6);
    CoreLabel c = CoreLabel.wordFromString("C");
    c.setBeginPosition(7);
    c.setEndPosition(8);
    CoreLabel outerClose = CoreLabel.wordFromString("\"");
    outerClose.setBeginPosition(8);
    outerClose.setEndPosition(9);
    List<CoreLabel> tokens = Arrays.asList(outerOpen, a, innerOpen, b, innerClose, c, outerClose);
    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 4);
    List<Pair<Integer, Integer>> spans = Arrays.asList(new Pair<>(3, 6), new Pair<>(0, 9));

    List<CoreMap> quotes = QuoteAnnotator.getCoreMapQuotes(
        spans, tokens, Collections.singletonList(sentence), text, "doc", false);

    assertEquals(1, quotes.size());
    CoreMap outer = quotes.get(0);
    assertEquals("\"A ‘B’ C\"", outer.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(0), outer.get(CoreAnnotations.QuotationIndexAnnotation.class));
    assertEquals(Integer.valueOf(4), outer.get(CoreAnnotations.SentenceBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), outer.get(CoreAnnotations.SentenceEndAnnotation.class));
    assertEquals(1, outer.get(CoreAnnotations.QuotationsAnnotation.class).size());
    CoreMap inner = outer.get(CoreAnnotations.QuotationsAnnotation.class).get(0);
    assertEquals("‘B’", inner.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(1), inner.get(CoreAnnotations.QuotationIndexAnnotation.class));
    assertEquals(Integer.valueOf(2), inner.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), inner.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(1), b.get(CoreAnnotations.QuotationIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), a.get(CoreAnnotations.QuotationIndexAnnotation.class));
  }

  @Test
  public void coreMapConversionUsesUnclosedNestingKey() {
    String text = "\"outer ‘inner";
    CoreLabel outer = CoreLabel.wordFromString("\"");
    outer.setBeginPosition(0);
    outer.setEndPosition(1);
    CoreLabel outerWord = CoreLabel.wordFromString("outer");
    outerWord.setBeginPosition(1);
    outerWord.setEndPosition(6);
    CoreLabel inner = CoreLabel.wordFromString("‘");
    inner.setBeginPosition(7);
    inner.setEndPosition(8);
    CoreLabel innerWord = CoreLabel.wordFromString("inner");
    innerWord.setBeginPosition(8);
    innerWord.setEndPosition(13);
    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> quotes = QuoteAnnotator.getCoreMapQuotes(
        Arrays.asList(new Pair<>(7, 13), new Pair<>(0, 13)),
        Arrays.asList(outer, outerWord, inner, innerWord),
        Collections.singletonList(sentence), text, null, true);

    assertEquals(1, quotes.size());
    assertFalse(quotes.get(0).containsKey(CoreAnnotations.QuotationsAnnotation.class));
    assertEquals(1, quotes.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class).size());
    assertEquals(Integer.valueOf(0), quotes.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class));
    assertEquals(Integer.valueOf(1), quotes.get(0).get(CoreAnnotations.UnclosedQuotationsAnnotation.class)
        .get(0).get(CoreAnnotations.QuotationIndexAnnotation.class));
  }

  @Test
  public void coreMapConversionFiltersQuotesWithoutCoveredTokens() {
    CoreLabel token = CoreLabel.wordFromString("word");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    Annotation sentence = new Annotation("word     \"x\"");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> quotes = QuoteAnnotator.getCoreMapQuotes(
        Collections.singletonList(new Pair<>(9, 12)), Collections.singletonList(token),
        Collections.singletonList(sentence), "word     \"x\"", null, false);

    assertTrue(quotes.isEmpty());
  }

  @Test
  public void coreMapConversionFiltersQuotesOutsideAllSentences() {
    CoreLabel quoteToken = CoreLabel.wordFromString("\"late\"");
    quoteToken.setBeginPosition(10);
    quoteToken.setEndPosition(16);
    Annotation sentence = new Annotation("early");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> quotes = QuoteAnnotator.getCoreMapQuotes(
        Collections.singletonList(new Pair<>(10, 16)), Collections.singletonList(quoteToken),
        Collections.singletonList(sentence), "early     \"late\"", null, false);

    assertTrue(quotes.isEmpty());
  }

  @Test
  public void gatherQuotesReturnsAllDescendantsInSourceOrder() {
    Annotation root = QuoteAnnotator.makeQuote("root", 0, 30, null, -1, 0, 0, null);
    Annotation later = QuoteAnnotator.makeQuote("later", 20, 25, null, -1, 0, 0, null);
    Annotation earlier = QuoteAnnotator.makeQuote("earlier", 5, 15, null, -1, 0, 0, null);
    Annotation deepest = QuoteAnnotator.makeQuote("deepest", 8, 12, null, -1, 0, 0, null);
    earlier.set(CoreAnnotations.QuotationsAnnotation.class, Collections.singletonList(deepest));
    root.set(CoreAnnotations.QuotationsAnnotation.class, Arrays.asList(later, earlier));

    List<CoreMap> gathered = QuoteAnnotator.gatherQuotes(root);

    assertEquals(3, gathered.size());
    assertSame(earlier, gathered.get(0));
    assertSame(deepest, gathered.get(1));
    assertSame(later, gathered.get(2));
    assertTrue(QuoteAnnotator.gatherQuotes(deepest).isEmpty());
  }

  @Test
  public void xmlFreeTextPreservesTokensAndBlanksMarkupAndOtherGaps() {
    String text = "<p>Hello</p> <b>world</b>!";
    CoreLabel hello = CoreLabel.wordFromString("Hello");
    hello.setBeginPosition(3);
    hello.setEndPosition(8);
    CoreLabel world = CoreLabel.wordFromString("world");
    world.setBeginPosition(16);
    world.setEndPosition(21);
    CoreLabel bang = CoreLabel.wordFromString("!");
    bang.setBeginPosition(25);
    bang.setEndPosition(26);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(hello, world, bang));

    String cleaned = QuoteAnnotator.xmlFreeText(text, annotation);

    assertEquals("   Hello        world    !", cleaned);
    assertEquals(text.length(), cleaned.length());
  }

  @Test
  public void annotateCreatesQuoteWithDocumentTokenAndSentenceMetadata() {
    String text = "He said \"Hi.\"";
    CoreLabel he = CoreLabel.wordFromString("He");
    he.setBeginPosition(0);
    he.setEndPosition(2);
    CoreLabel said = CoreLabel.wordFromString("said");
    said.setBeginPosition(3);
    said.setEndPosition(7);
    CoreLabel open = CoreLabel.wordFromString("\"");
    open.setBeginPosition(8);
    open.setEndPosition(9);
    CoreLabel hi = CoreLabel.wordFromString("Hi");
    hi.setBeginPosition(9);
    hi.setEndPosition(11);
    CoreLabel period = CoreLabel.wordFromString(".");
    period.setBeginPosition(11);
    period.setEndPosition(12);
    CoreLabel close = CoreLabel.wordFromString("\"");
    close.setBeginPosition(12);
    close.setEndPosition(13);
    List<CoreLabel> tokens = Arrays.asList(he, said, open, hi, period, close);
    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.DocIDAnnotation.class, "story");
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");

    new QuoteAnnotator(properties).annotate(document);

    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertEquals(1, quotes.size());
    assertEquals("\"Hi.\"", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("story", quotes.get(0).get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(Integer.valueOf(2), quotes.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(5), quotes.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(0), quotes.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), open.get(CoreAnnotations.QuotationIndexAnnotation.class));
  }

  @Test
  public void annotateCanNormalizeMisdirectedUnicodeQuotes() {
    String text = "“first“ and ”second”";
    CoreLabel firstQuote = CoreLabel.wordFromString("“");
    firstQuote.setBeginPosition(0);
    firstQuote.setEndPosition(1);
    CoreLabel first = CoreLabel.wordFromString("first");
    first.setBeginPosition(1);
    first.setEndPosition(6);
    CoreLabel secondQuote = CoreLabel.wordFromString("“");
    secondQuote.setBeginPosition(6);
    secondQuote.setEndPosition(7);
    CoreLabel and = CoreLabel.wordFromString("and");
    and.setBeginPosition(8);
    and.setEndPosition(11);
    CoreLabel thirdQuote = CoreLabel.wordFromString("”");
    thirdQuote.setBeginPosition(12);
    thirdQuote.setEndPosition(13);
    CoreLabel second = CoreLabel.wordFromString("second");
    second.setBeginPosition(13);
    second.setEndPosition(19);
    CoreLabel fourthQuote = CoreLabel.wordFromString("”");
    fourthQuote.setBeginPosition(19);
    fourthQuote.setEndPosition(20);
    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(firstQuote, first, secondQuote, and, thirdQuote, second, fourthQuote));
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    Properties properties = new Properties();
    properties.setProperty("quote.asciiQuotes", "true");
    properties.setProperty("quote.smartQuotes", "true");
    properties.setProperty("quote.attributeQuotes", "false");

    new QuoteAnnotator(properties).annotate(document);

    List<CoreMap> quotes = document.get(CoreAnnotations.QuotationsAnnotation.class);
    assertEquals(2, quotes.size());
    assertEquals("“first“", quotes.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("”second”", quotes.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void annotateStoresUnclosedQuotesOnlyWhenConfigured() {
    String text = "She began \"unfinished words";
    CoreLabel she = CoreLabel.wordFromString("She");
    she.setBeginPosition(0);
    she.setEndPosition(3);
    CoreLabel began = CoreLabel.wordFromString("began");
    began.setBeginPosition(4);
    began.setEndPosition(9);
    CoreLabel quote = CoreLabel.wordFromString("\"");
    quote.setBeginPosition(10);
    quote.setEndPosition(11);
    CoreLabel unfinished = CoreLabel.wordFromString("unfinished");
    unfinished.setBeginPosition(11);
    unfinished.setEndPosition(21);
    CoreLabel words = CoreLabel.wordFromString("words");
    words.setBeginPosition(22);
    words.setEndPosition(27);
    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    Annotation document = new Annotation(text);
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(she, began, quote, unfinished, words));
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    Properties properties = new Properties();
    properties.setProperty("quote.extractUnclosedQuotes", "true");
    properties.setProperty("quote.attributeQuotes", "false");

    new QuoteAnnotator(properties).annotate(document);

    assertTrue(document.get(CoreAnnotations.QuotationsAnnotation.class).isEmpty());
    List<CoreMap> unclosed = document.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertEquals(1, unclosed.size());
    assertEquals("\"unfinished words", unclosed.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(0), unclosed.get(0).get(CoreAnnotations.QuotationIndexAnnotation.class));
  }

  @Test
  public void requirementsWithoutAttributionAreMinimalAndSatisfiedOutputIsQuote() {
    Properties properties = new Properties();
    properties.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator(properties);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    Set<Class<? extends CoreAnnotation>> expected = new HashSet<>(Arrays.asList(
        CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.TokensAnnotation.class,
        CoreAnnotations.SentencesAnnotation.class,
        CoreAnnotations.CharacterOffsetBeginAnnotation.class,
        CoreAnnotations.CharacterOffsetEndAnnotation.class,
        CoreAnnotations.IsNewlineAnnotation.class,
        CoreAnnotations.OriginalTextAnnotation.class));

    assertEquals(expected, required);
    assertEquals(Collections.singleton(CoreAnnotations.QuotationsAnnotation.class),
        annotator.requirementsSatisfied());
  }

}
