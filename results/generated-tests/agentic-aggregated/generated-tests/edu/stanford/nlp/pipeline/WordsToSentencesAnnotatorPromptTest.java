package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WordsToSentencesAnnotatorPromptTest {

  @Test
  public void defaultAnnotatorSplitsSentencesAndAssignsDocumentMetadata() {
    Annotation document = new Annotation("One. Two!");
    document.set(CoreAnnotations.DocIDAnnotation.class, "doc-7");
    CoreLabel one = CoreLabel.wordFromString("One");
    one.setBeginPosition(0);
    one.setEndPosition(3);
    one.setIsNewline(false);
    CoreLabel period = CoreLabel.wordFromString(".");
    period.setBeginPosition(3);
    period.setEndPosition(4);
    period.setIsNewline(false);
    CoreLabel two = CoreLabel.wordFromString("Two");
    two.setBeginPosition(5);
    two.setEndPosition(8);
    two.setIsNewline(false);
    CoreLabel exclamation = CoreLabel.wordFromString("!");
    exclamation.setBeginPosition(8);
    exclamation.setEndPosition(9);
    exclamation.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class,
        new ArrayList<>(Arrays.asList(one, period, two, exclamation)));

    new WordsToSentencesAnnotator().annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("One.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Two!", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(0), sentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals(Integer.valueOf(1), sentences.get(1).get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), sentences.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), sentences.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(2), sentences.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), sentences.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals("doc-7", sentences.get(0).get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals("doc-7", sentences.get(1).get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(1, one.index());
    assertEquals(2, period.index());
    assertEquals(1, two.index());
    assertEquals(2, exclamation.index());
    assertEquals(0, one.sentIndex());
    assertEquals(1, two.sentIndex());
    assertEquals("doc-7", exclamation.docID());
  }

  @Test
  public void boundaryFollowerClosingQuoteStaysWithQuotedSentence() {
    Annotation document = new Annotation("Hello. \"Next!\"");
    CoreLabel hello = CoreLabel.wordFromString("Hello");
    hello.setBeginPosition(0);
    hello.setEndPosition(5);
    hello.setIsNewline(false);
    CoreLabel period = CoreLabel.wordFromString(".");
    period.setBeginPosition(5);
    period.setEndPosition(6);
    period.setIsNewline(false);
    CoreLabel openQuote = CoreLabel.wordFromString("\"");
    openQuote.setBeginPosition(7);
    openQuote.setEndPosition(8);
    openQuote.setIsNewline(false);
    CoreLabel next = CoreLabel.wordFromString("Next");
    next.setBeginPosition(8);
    next.setEndPosition(12);
    next.setIsNewline(false);
    CoreLabel bang = CoreLabel.wordFromString("!");
    bang.setBeginPosition(12);
    bang.setEndPosition(13);
    bang.setIsNewline(false);
    CoreLabel closeQuote = CoreLabel.wordFromString("\"");
    closeQuote.setBeginPosition(13);
    closeQuote.setEndPosition(14);
    closeQuote.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(hello, period, openQuote, next, bang, closeQuote));

    new WordsToSentencesAnnotator(false).annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("Hello.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("\"Next!\"", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(4, sentences.get(1).get(CoreAnnotations.TokensAnnotation.class).size());
    assertSame(openQuote, sentences.get(1).get(CoreAnnotations.TokensAnnotation.class).get(0));
    assertSame(closeQuote, sentences.get(1).get(CoreAnnotations.TokensAnnotation.class).get(3));
  }

  @Test
  public void nonSplitterTreatsBoundaryPunctuationAsOrdinaryTokens() {
    Annotation document = new Annotation("First. Second?");
    CoreLabel first = CoreLabel.wordFromString("First");
    first.setBeginPosition(0);
    first.setEndPosition(5);
    first.setIsNewline(false);
    CoreLabel period = CoreLabel.wordFromString(".");
    period.setBeginPosition(5);
    period.setEndPosition(6);
    period.setIsNewline(false);
    CoreLabel second = CoreLabel.wordFromString("Second");
    second.setBeginPosition(7);
    second.setEndPosition(13);
    second.setIsNewline(false);
    CoreLabel question = CoreLabel.wordFromString("?");
    question.setBeginPosition(13);
    question.setEndPosition(14);
    question.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(first, period, second, question));

    WordsToSentencesAnnotator.nonSplitter().annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("First. Second?", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(4, sentences.get(0).get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals(Integer.valueOf(0), sentences.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), sentences.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void propertiesCanSelectOneSentenceMode() {
    Properties properties = new Properties();
    properties.setProperty("ssplit.isOneSentence", "true");
    properties.setProperty("ssplit.boundaryTokenRegex", "!");
    Annotation document = new Annotation("A! B.");
    CoreLabel a = CoreLabel.wordFromString("A");
    a.setBeginPosition(0);
    a.setEndPosition(1);
    a.setIsNewline(false);
    CoreLabel bang = CoreLabel.wordFromString("!");
    bang.setBeginPosition(1);
    bang.setEndPosition(2);
    bang.setIsNewline(false);
    CoreLabel b = CoreLabel.wordFromString("B");
    b.setBeginPosition(3);
    b.setEndPosition(4);
    b.setIsNewline(false);
    CoreLabel period = CoreLabel.wordFromString(".");
    period.setBeginPosition(4);
    period.setEndPosition(5);
    period.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(a, bang, b, period));

    new WordsToSentencesAnnotator(properties).annotate(document);

    assertEquals(1, document.get(CoreAnnotations.SentencesAnnotation.class).size());
    assertEquals("A! B.", document.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void newlineSplitterCountsEmptyLinesAndRepairsWhitespaceAnnotations() {
    Annotation document = new Annotation("A\n\nB");
    CoreLabel a = CoreLabel.wordFromString("A");
    a.setBeginPosition(0);
    a.setEndPosition(1);
    a.setIsNewline(false);
    a.setAfter("old-after");
    CoreLabel newlineOne = CoreLabel.wordFromString("\n");
    newlineOne.setBeginPosition(1);
    newlineOne.setEndPosition(2);
    newlineOne.setIsNewline(true);
    newlineOne.setOriginalText("\n");
    CoreLabel newlineTwo = CoreLabel.wordFromString("\n");
    newlineTwo.setBeginPosition(2);
    newlineTwo.setEndPosition(3);
    newlineTwo.setIsNewline(true);
    newlineTwo.setOriginalText("\n");
    CoreLabel b = CoreLabel.wordFromString("B");
    b.setBeginPosition(3);
    b.setEndPosition(4);
    b.setIsNewline(false);
    b.setBefore("old-before");
    document.set(CoreAnnotations.TokensAnnotation.class,
        new ArrayList<>(Arrays.asList(a, newlineOne, newlineTwo, b)));

    WordsToSentencesAnnotator.newlineSplitter("\n").annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    List<CoreLabel> finalTokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("A", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("B", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(1), sentences.get(0).get(CoreAnnotations.LineNumberAnnotation.class));
    assertEquals(Integer.valueOf(3), sentences.get(1).get(CoreAnnotations.LineNumberAnnotation.class));
    assertEquals(2, finalTokens.size());
    assertSame(a, finalTokens.get(0));
    assertSame(b, finalTokens.get(1));
    assertEquals("\n", a.after());
    assertEquals("\n", b.before());
    assertEquals(Integer.valueOf(0), a.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), b.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), sentences.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void propertyBasedWhitespaceNewlineSplitterUsesLiteralNewline() {
    Properties properties = new Properties();
    properties.setProperty("ssplit.eolonly", "true");
    properties.setProperty("tokenize.whitespace", "true");
    Annotation document = new Annotation("Left\nRight");
    CoreLabel left = CoreLabel.wordFromString("Left");
    left.setBeginPosition(0);
    left.setEndPosition(4);
    left.setIsNewline(false);
    CoreLabel newline = CoreLabel.wordFromString("\n");
    newline.setBeginPosition(4);
    newline.setEndPosition(5);
    newline.setIsNewline(true);
    newline.setOriginalText("\n");
    CoreLabel right = CoreLabel.wordFromString("Right");
    right.setBeginPosition(5);
    right.setEndPosition(10);
    right.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(left, newline, right));

    new WordsToSentencesAnnotator(properties).annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals(Integer.valueOf(1), sentences.get(0).get(CoreAnnotations.LineNumberAnnotation.class));
    assertEquals(Integer.valueOf(2), sentences.get(1).get(CoreAnnotations.LineNumberAnnotation.class));
    assertEquals("Left", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Right", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(2, document.get(CoreAnnotations.TokensAnnotation.class).size());
  }

  @Test
  public void configurableBoundaryDiscardAndHtmlBreakInteractAcrossSentences() {
    Set<String> discardedBoundaries = new HashSet<>(Collections.singletonList("BREAK"));
    Set<String> htmlElements = new HashSet<>(Collections.singletonList("p"));
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(
        false, ";", discardedBoundaries, htmlElements, "always", null, null);
    Annotation document = new Annotation("A;B BREAK C<p>D");
    CoreLabel a = CoreLabel.wordFromString("A");
    a.setBeginPosition(0);
    a.setEndPosition(1);
    a.setIsNewline(false);
    CoreLabel semicolon = CoreLabel.wordFromString(";");
    semicolon.setBeginPosition(1);
    semicolon.setEndPosition(2);
    semicolon.setIsNewline(false);
    CoreLabel b = CoreLabel.wordFromString("B");
    b.setBeginPosition(2);
    b.setEndPosition(3);
    b.setIsNewline(false);
    CoreLabel breakToken = CoreLabel.wordFromString("BREAK");
    breakToken.setBeginPosition(4);
    breakToken.setEndPosition(9);
    breakToken.setIsNewline(false);
    CoreLabel c = CoreLabel.wordFromString("C");
    c.setBeginPosition(10);
    c.setEndPosition(11);
    c.setIsNewline(false);
    CoreLabel paragraph = CoreLabel.wordFromString("<p>");
    paragraph.setBeginPosition(11);
    paragraph.setEndPosition(14);
    paragraph.setIsNewline(false);
    CoreLabel d = CoreLabel.wordFromString("D");
    d.setBeginPosition(14);
    d.setEndPosition(15);
    d.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(a, semicolon, b, breakToken, c, paragraph, d));

    annotator.annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(4, sentences.size());
    assertEquals("A;", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("B", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("C", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("D", sentences.get(3).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(7, document.get(CoreAnnotations.TokensAnnotation.class).size());
    assertFalse(sentences.get(1).get(CoreAnnotations.TokensAnnotation.class).contains(breakToken));
    assertFalse(sentences.get(2).get(CoreAnnotations.TokensAnnotation.class).contains(paragraph));
  }

  @Test
  public void tokenPatternDiscardRemovesTokenFromSentenceButNotDocumentTokenStream() {
    Set<String> discardedPatterns = new HashSet<>(Collections.singletonList("NOISE"));
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(
        false, "[.]", null, null, "never", null, discardedPatterns);
    Annotation document = new Annotation("Keep NOISE.");
    CoreLabel keep = CoreLabel.wordFromString("Keep");
    keep.setBeginPosition(0);
    keep.setEndPosition(4);
    keep.setIsNewline(false);
    CoreLabel noise = CoreLabel.wordFromString("NOISE");
    noise.setBeginPosition(5);
    noise.setEndPosition(10);
    noise.setIsNewline(false);
    CoreLabel period = CoreLabel.wordFromString(".");
    period.setBeginPosition(10);
    period.setEndPosition(11);
    period.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(keep, noise, period));

    annotator.annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("Keep NOISE.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(2, sentences.get(0).get(CoreAnnotations.TokensAnnotation.class).size());
    assertSame(keep, sentences.get(0).get(CoreAnnotations.TokensAnnotation.class).get(0));
    assertSame(period, sentences.get(0).get(CoreAnnotations.TokensAnnotation.class).get(1));
    assertEquals(3, document.get(CoreAnnotations.TokensAnnotation.class).size());
  }

  @Test
  public void forcedSentenceEndOverridesLackOfPunctuation() {
    Annotation document = new Annotation("Alpha beta gamma");
    CoreLabel alpha = CoreLabel.wordFromString("Alpha");
    alpha.setBeginPosition(0);
    alpha.setEndPosition(5);
    alpha.setIsNewline(false);
    CoreLabel beta = CoreLabel.wordFromString("beta");
    beta.setBeginPosition(6);
    beta.setEndPosition(10);
    beta.setIsNewline(false);
    beta.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel gamma = CoreLabel.wordFromString("gamma");
    gamma.setBeginPosition(11);
    gamma.setEndPosition(16);
    gamma.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(alpha, beta, gamma));

    new WordsToSentencesAnnotator().annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("Alpha beta", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("gamma", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(0, beta.sentIndex());
    assertEquals(1, gamma.sentIndex());
  }

  @Test
  public void existingSentenceAnnotationCausesAnEarlyNoOp() {
    Annotation document = new Annotation("Untouched");
    CoreLabel token = CoreLabel.wordFromString("Untouched");
    token.setBeginPosition(0);
    token.setEndPosition(9);
    token.setIsNewline(false);
    token.setIndex(42);
    List<CoreLabel> tokens = new ArrayList<>(Collections.singletonList(token));
    List<CoreMap> existingSentences = new ArrayList<>();
    existingSentences.add(new Annotation("already split"));
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);
    document.set(CoreAnnotations.SentencesAnnotation.class, existingSentences);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(document);
    annotator.annotate(document);

    assertSame(existingSentences, document.get(CoreAnnotations.SentencesAnnotation.class));
    assertSame(tokens, document.get(CoreAnnotations.TokensAnnotation.class));
    assertEquals(42, token.index());
    assertEquals("already split", document.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void missingTokensProducesDescriptiveIllegalArgumentException() {
    Annotation document = new Annotation("No token annotation");

    try {
      new WordsToSentencesAnnotator().annotate(document);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException exception) {
      assertTrue(exception.getMessage().contains("unable to find words/tokens"));
      assertTrue(exception.getMessage().contains("No token annotation"));
    }
  }

  @Test
  public void emptyDocumentProducesEmptySentencesAndTokens() {
    Annotation document = new Annotation("");
    document.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    new WordsToSentencesAnnotator().annotate(document);

    assertTrue(document.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
    assertTrue(document.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }

  @Test
  public void nonSplitterRejectsItsUnexpectedEmptySentence() {
    Annotation document = new Annotation("");
    document.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    try {
      WordsToSentencesAnnotator.nonSplitter().annotate(document);
      fail("Expected the non-splitter's empty sentence to be rejected");
    } catch (IllegalStateException exception) {
      assertTrue(exception.getMessage().contains("unexpected empty sentence"));
    }
  }

  @Test
  public void propertyBasedMultiTokenBoundaryEndsAtLastMatchedToken() {
    Properties properties = new Properties();
    properties.setProperty("ssplit.boundaryTokenRegex", "[.]");
    properties.setProperty("ssplit.boundaryMultiTokenRegex", "[{word:and}] [{word:then}]");
    Annotation document = new Annotation("A and then B.");
    CoreLabel a = CoreLabel.wordFromString("A");
    a.setBeginPosition(0);
    a.setEndPosition(1);
    a.setIsNewline(false);
    CoreLabel and = CoreLabel.wordFromString("and");
    and.setBeginPosition(2);
    and.setEndPosition(5);
    and.setIsNewline(false);
    CoreLabel then = CoreLabel.wordFromString("then");
    then.setBeginPosition(6);
    then.setEndPosition(10);
    then.setIsNewline(false);
    CoreLabel b = CoreLabel.wordFromString("B");
    b.setBeginPosition(11);
    b.setEndPosition(12);
    b.setIsNewline(false);
    CoreLabel period = CoreLabel.wordFromString(".");
    period.setBeginPosition(12);
    period.setEndPosition(13);
    period.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(a, and, then, b, period));

    new WordsToSentencesAnnotator(properties).annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("A and then", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("B.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertSame(then, sentences.get(0).get(CoreAnnotations.TokensAnnotation.class).get(2));
  }

  @Test
  public void sectionCollectionReceivesSentencesAndQuoteMetadata() {
    Annotation document = new Annotation("Alpha. Beta.");
    CoreLabel alpha = CoreLabel.wordFromString("Alpha");
    alpha.setBeginPosition(0);
    alpha.setEndPosition(5);
    alpha.setIsNewline(false);
    CoreLabel firstPeriod = CoreLabel.wordFromString(".");
    firstPeriod.setBeginPosition(5);
    firstPeriod.setEndPosition(6);
    firstPeriod.setIsNewline(false);
    CoreLabel beta = CoreLabel.wordFromString("Beta");
    beta.setBeginPosition(7);
    beta.setEndPosition(11);
    beta.setIsNewline(false);
    CoreLabel secondPeriod = CoreLabel.wordFromString(".");
    secondPeriod.setBeginPosition(11);
    secondPeriod.setEndPosition(12);
    secondPeriod.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(alpha, firstPeriod, beta, secondPeriod));

    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "Alice");
    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2026-10-18");
    section.set(CoreAnnotations.QuotesAnnotation.class,
        new ArrayList<>(Collections.singletonList(quote)));
    List<CoreMap> sectionSentences = new ArrayList<>();
    section.set(CoreAnnotations.SentencesAnnotation.class, sectionSentences);
    document.set(CoreAnnotations.SectionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(section)));

    new WordsToSentencesAnnotator().annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals(2, sectionSentences.size());
    assertSame(sentences.get(0), sectionSentences.get(0));
    assertSame(sentences.get(1), sectionSentences.get(1));
    assertEquals(Boolean.TRUE, sentences.get(0).get(CoreAnnotations.QuotedAnnotation.class));
    assertEquals("Alice", sentences.get(0).get(CoreAnnotations.AuthorAnnotation.class));
    assertNull(sentences.get(1).get(CoreAnnotations.QuotedAnnotation.class));
    assertNull(sentences.get(1).get(CoreAnnotations.AuthorAnnotation.class));
    assertEquals("2026-10-18", sentences.get(0).get(CoreAnnotations.SectionDateAnnotation.class));
    assertEquals("2026-10-18", sentences.get(1).get(CoreAnnotations.SectionDateAnnotation.class));
    assertEquals(Integer.valueOf(0), sentences.get(0).get(CoreAnnotations.SectionIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), sentences.get(1).get(CoreAnnotations.SectionIndexAnnotation.class));
  }

  @Test
  public void tokenSectionStartMetadataIsCopiedUntilSectionEndToken() {
    Annotation document = new Annotation("First. Second.");
    CoreMap sectionMetadata = new ArrayCoreMap();
    sectionMetadata.set(CoreAnnotations.AuthorAnnotation.class, "Section Author");
    sectionMetadata.set(CoreAnnotations.SectionAnnotation.class, "intro");
    CoreLabel first = CoreLabel.wordFromString("First");
    first.setBeginPosition(0);
    first.setEndPosition(5);
    first.setIsNewline(false);
    first.set(CoreAnnotations.SectionStartAnnotation.class, sectionMetadata);
    CoreLabel firstPeriod = CoreLabel.wordFromString(".");
    firstPeriod.setBeginPosition(5);
    firstPeriod.setEndPosition(6);
    firstPeriod.setIsNewline(false);
    firstPeriod.set(CoreAnnotations.SectionEndAnnotation.class, "intro");
    CoreLabel second = CoreLabel.wordFromString("Second");
    second.setBeginPosition(7);
    second.setEndPosition(13);
    second.setIsNewline(false);
    CoreLabel secondPeriod = CoreLabel.wordFromString(".");
    secondPeriod.setBeginPosition(13);
    secondPeriod.setEndPosition(14);
    secondPeriod.setIsNewline(false);
    document.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(first, firstPeriod, second, secondPeriod));

    new WordsToSentencesAnnotator().annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals("Section Author", sentences.get(0).get(CoreAnnotations.AuthorAnnotation.class));
    assertEquals("intro", sentences.get(0).get(CoreAnnotations.SectionAnnotation.class));
    assertNull(sentences.get(1).get(CoreAnnotations.AuthorAnnotation.class));
    assertNull(sentences.get(1).get(CoreAnnotations.SectionAnnotation.class));
  }

  @Test
  public void requirementContractsContainExpectedKeysAndRequiredSetIsImmutable() {
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(9, required.size());
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.ValueAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.IsNewlineAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokenBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokenEndAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertEquals(2, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SentenceIndexAnnotation.class));
    try {
      required.clear();
      fail("Expected requirements to be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(9, required.size());
    }
  }

  @Test
  public void invalidNewlineBreakPropertyIsRejectedDuringConstruction() {
    Properties properties = new Properties();
    properties.setProperty("ssplit.newlineIsSentenceBreak", "occasionally");

    try {
      new WordsToSentencesAnnotator(properties);
      fail("Expected invalid newline policy to be rejected");
    } catch (IllegalArgumentException exception) {
      assertTrue(exception.getMessage().contains("Not a valid NewlineIsSentenceBreak name"));
      assertTrue(exception.getMessage().contains("occasionally"));
    }
  }
}
