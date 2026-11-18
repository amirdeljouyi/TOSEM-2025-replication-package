package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CoreDocumentPromptTest {

  @Test
  public void stringConstructorCreatesTextOnlyDocument() {
    CoreDocument document = new CoreDocument("Stanford NLP");

    assertNotNull(document.annotation());
    assertEquals("Stanford NLP", document.text());
    assertEquals("Stanford NLP", document.toString());
    assertNull(document.docID());
    assertNull(document.docDate());
    assertNull(document.tokens());
    assertNull(document.sentences());
    assertNull(document.entityMentions());
    assertNull(document.corefChains());
    assertNull(document.quotes());
  }

  @Test
  public void stringConstructorAcceptsEmptyAndNullText() {
    CoreDocument emptyDocument = new CoreDocument("");
    CoreDocument nullDocument = new CoreDocument((String) null);

    assertEquals("", emptyDocument.text());
    assertEquals("", emptyDocument.toString());
    assertNull(nullDocument.text());
    assertNull(nullDocument.toString());
  }

  @Test
  public void annotationConstructorExposesMetadataTokensAndCoreferenceMap() {
    Annotation annotation = new Annotation("Ada writes.");
    CoreLabel ada = new CoreLabel();
    ada.setWord("Ada");
    CoreLabel writes = new CoreLabel();
    writes.setWord("writes");
    List<CoreLabel> tokens = new ArrayList<>(Arrays.asList(ada, writes));
    Map<Integer, CorefChain> chains = new HashMap<>();
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-17");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2025-03-12");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    CoreDocument document = new CoreDocument(annotation);

    assertSame(annotation, document.annotation());
    assertEquals("doc-17", document.docID());
    assertEquals("2025-03-12", document.docDate());
    assertSame(tokens, document.tokens());
    assertSame(chains, document.corefChains());
    assertEquals("Ada", document.tokens().get(0).word());
    assertEquals("writes", document.tokens().get(1).word());
  }

  @Test
  public void emptySentenceAnnotationProducesEmptyWrappedCollectionsWhenMentionsKeyExists() {
    Annotation annotation = new Annotation("No sentences");
    List<CoreMap> sentenceMaps = new ArrayList<>();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceMaps);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreDocument document = new CoreDocument(annotation);

    assertNotNull(document.sentences());
    assertTrue(document.sentences().isEmpty());
    assertNotNull(document.entityMentions());
    assertTrue(document.entityMentions().isEmpty());
    assertNotNull(document.quotes());
    assertTrue(document.quotes().isEmpty());
  }

  @Test
  public void emptySentenceAnnotationLeavesEntityMentionsUnsetWithoutDocumentMentionKey() {
    Annotation annotation = new Annotation("No sentences");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    CoreDocument document = new CoreDocument(annotation);

    assertNotNull(document.sentences());
    assertTrue(document.sentences().isEmpty());
    assertNull(document.entityMentions());
    assertNotNull(document.quotes());
    assertTrue(document.quotes().isEmpty());
  }

  @Test
  public void wrapsSentenceAndPreservesItsUnderlyingMap() {
    Annotation annotation = new Annotation("A short sentence.");
    ArrayCoreMap sentenceMap = new ArrayCoreMap();
    sentenceMap.set(CoreAnnotations.TextAnnotation.class, "A short sentence.");
    sentenceMap.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Collections.singletonList(sentenceMap)));

    CoreDocument document = new CoreDocument(annotation);

    assertEquals(1, document.sentences().size());
    assertSame(document, document.sentences().get(0).document());
    assertSame(sentenceMap, document.sentences().get(0).coreMap());
    assertEquals("A short sentence.", document.sentences().get(0).text());
    assertNull(document.sentences().get(0).entityMentions());
    assertNull(document.entityMentions());
  }

  @Test
  public void flattensEntityMentionsAcrossSentencesInDocumentOrder() {
    Annotation annotation = new Annotation("Alice met Bob. Paris welcomed them.");
    ArrayCoreMap aliceMention = new ArrayCoreMap();
    aliceMention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    ArrayCoreMap bobMention = new ArrayCoreMap();
    bobMention.set(CoreAnnotations.TextAnnotation.class, "Bob");
    ArrayCoreMap parisMention = new ArrayCoreMap();
    parisMention.set(CoreAnnotations.TextAnnotation.class, "Paris");
    ArrayCoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TextAnnotation.class, "Alice met Bob.");
    firstSentence.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Arrays.asList(aliceMention, bobMention)));
    ArrayCoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TextAnnotation.class, "Paris welcomed them.");
    secondSentence.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(parisMention)));
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Arrays.asList(firstSentence, secondSentence)));

    CoreDocument document = new CoreDocument(annotation);

    assertEquals(2, document.sentences().size());
    assertEquals(3, document.entityMentions().size());
    assertEquals("Alice", document.entityMentions().get(0).text());
    assertEquals("Bob", document.entityMentions().get(1).text());
    assertEquals("Paris", document.entityMentions().get(2).text());
    assertSame(document.sentences().get(0), document.entityMentions().get(0).sentence());
    assertSame(document.sentences().get(0), document.entityMentions().get(1).sentence());
    assertSame(document.sentences().get(1), document.entityMentions().get(2).sentence());
    assertSame(aliceMention, document.entityMentions().get(0).coreMap());
    assertSame(parisMention, document.entityMentions().get(2).coreMap());
  }

  @Test
  public void emptyFirstSentenceMentionListStillAllowsLaterMentionsToBeFlattened() {
    Annotation annotation = new Annotation("Nothing here. Grace arrived.");
    ArrayCoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TextAnnotation.class, "Nothing here.");
    firstSentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ArrayCoreMap graceMention = new ArrayCoreMap();
    graceMention.set(CoreAnnotations.TextAnnotation.class, "Grace");
    ArrayCoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TextAnnotation.class, "Grace arrived.");
    secondSentence.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(graceMention)));
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Arrays.asList(firstSentence, secondSentence)));

    CoreDocument document = new CoreDocument(annotation);

    assertNotNull(document.sentences().get(0).entityMentions());
    assertTrue(document.sentences().get(0).entityMentions().isEmpty());
    assertEquals(1, document.entityMentions().size());
    assertEquals("Grace", document.entityMentions().get(0).text());
  }

  @Test
  public void missingFirstSentenceMentionsUsesEmptyDocumentListWhenDocumentMentionsExist() {
    Annotation annotation = new Annotation("First. Later has a mention.");
    ArrayCoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TextAnnotation.class, "First.");
    ArrayCoreMap laterMention = new ArrayCoreMap();
    laterMention.set(CoreAnnotations.TextAnnotation.class, "Later");
    ArrayCoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TextAnnotation.class, "Later has a mention.");
    secondSentence.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(laterMention)));
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Arrays.asList(firstSentence, secondSentence)));
    annotation.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(laterMention)));

    CoreDocument document = new CoreDocument(annotation);

    assertNull(document.sentences().get(0).entityMentions());
    assertEquals(1, document.sentences().get(1).entityMentions().size());
    assertNotNull(document.entityMentions());
    assertTrue(document.entityMentions().isEmpty());
  }

  @Test
  public void wrapAnnotationsCanPopulateAStringConstructedDocumentAfterAnnotation() {
    CoreDocument document = new CoreDocument("Dynamic text");
    ArrayCoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "Dynamic");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "Dynamic text");
    sentence.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(mention)));
    document.annotation().set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Collections.singletonList(sentence)));

    document.wrapAnnotations();

    assertEquals(1, document.sentences().size());
    assertEquals("Dynamic text", document.sentences().get(0).text());
    assertEquals(1, document.entityMentions().size());
    assertEquals("Dynamic", document.entityMentions().get(0).text());
    assertNotNull(document.quotes());
    assertTrue(document.quotes().isEmpty());
  }

  @Test
  public void repeatedWrapReflectsReplacementSentenceAndMentionAnnotations() {
    Annotation annotation = new Annotation("Original then replacement");
    ArrayCoreMap originalMention = new ArrayCoreMap();
    originalMention.set(CoreAnnotations.TextAnnotation.class, "Original");
    ArrayCoreMap originalSentence = new ArrayCoreMap();
    originalSentence.set(CoreAnnotations.TextAnnotation.class, "Original");
    originalSentence.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(originalMention)));
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Collections.singletonList(originalSentence)));
    CoreDocument document = new CoreDocument(annotation);
    ArrayCoreMap replacementMention = new ArrayCoreMap();
    replacementMention.set(CoreAnnotations.TextAnnotation.class, "replacement");
    ArrayCoreMap replacementSentence = new ArrayCoreMap();
    replacementSentence.set(CoreAnnotations.TextAnnotation.class, "replacement");
    replacementSentence.set(CoreAnnotations.MentionsAnnotation.class,
        new ArrayList<>(Collections.singletonList(replacementMention)));
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Collections.singletonList(replacementSentence)));

    document.wrapAnnotations();

    assertEquals(1, document.sentences().size());
    assertEquals("replacement", document.sentences().get(0).text());
    assertEquals(1, document.entityMentions().size());
    assertEquals("replacement", document.entityMentions().get(0).text());
    assertSame(replacementSentence, document.sentences().get(0).coreMap());
  }

  @Test
  public void wrapsQuoteAndAssociatesItsSentence() {
    Annotation annotation = new Annotation("She said, \"hello\".");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "She said, \"hello\".");
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Collections.singletonList(sentence)));
    ArrayCoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.TextAnnotation.class, "hello");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class,
        new ArrayList<>(Collections.singletonList(quote)));

    CoreDocument document = new CoreDocument(annotation);

    assertEquals(1, document.quotes().size());
    assertEquals("hello", document.quotes().get(0).text());
    assertSame(document, document.quotes().get(0).document());
    assertSame(quote, document.quotes().get(0).coreMap());
    assertEquals(1, document.quotes().get(0).sentences().size());
    assertSame(document.sentences().get(0), document.quotes().get(0).sentences().get(0));
    assertEquals(new Pair<>(10, 15), document.quotes().get(0).quoteCharOffsets());
    assertFalse(document.quotes().get(0).speaker().isPresent());
  }

  @Test
  public void recursivelyGathersNestedQuotesAndSortsByCharacterOffset() {
    Annotation annotation = new Annotation("Outer quote containing inner quote");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "Outer quote containing inner quote");
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Collections.singletonList(sentence)));
    ArrayCoreMap innerQuote = new ArrayCoreMap();
    innerQuote.set(CoreAnnotations.TextAnnotation.class, "inner");
    innerQuote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    innerQuote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    innerQuote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    innerQuote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
    ArrayCoreMap outerQuote = new ArrayCoreMap();
    outerQuote.set(CoreAnnotations.TextAnnotation.class, "Outer quote containing inner quote");
    outerQuote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    outerQuote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 32);
    outerQuote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    outerQuote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
    outerQuote.set(CoreAnnotations.QuotationsAnnotation.class,
        new ArrayList<>(Collections.singletonList(innerQuote)));
    annotation.set(CoreAnnotations.QuotationsAnnotation.class,
        new ArrayList<>(Collections.singletonList(outerQuote)));

    CoreDocument document = new CoreDocument(annotation);

    assertEquals(2, document.quotes().size());
    assertEquals("Outer quote containing inner quote", document.quotes().get(0).text());
    assertEquals("inner", document.quotes().get(1).text());
    assertSame(outerQuote, document.quotes().get(0).coreMap());
    assertSame(innerQuote, document.quotes().get(1).coreMap());
  }

  @Test
  public void quotesAreNotWrappedUntilSentencesAreAvailable() {
    Annotation annotation = new Annotation("\"orphan quote\"");
    ArrayCoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.TextAnnotation.class, "orphan quote");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class,
        new ArrayList<>(Collections.singletonList(quote)));
    CoreDocument document = new CoreDocument(annotation);

    assertNull(document.sentences());
    assertNull(document.quotes());

    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "\"orphan quote\"");
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        new ArrayList<>(Collections.singletonList(sentence)));
    document.wrapAnnotations();

    assertEquals(1, document.sentences().size());
    assertEquals(1, document.quotes().size());
    assertEquals("orphan quote", document.quotes().get(0).text());
  }

  @Test
  public void annotationMutationsRemainVisibleThroughAccessorsAndToString() {
    Annotation annotation = new Annotation("before");
    CoreDocument document = new CoreDocument(annotation);

    annotation.set(CoreAnnotations.TextAnnotation.class, "after");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "changed-id");

    assertEquals("after", document.text());
    assertEquals("after", document.toString());
    assertEquals("changed-id", document.docID());
    assertSame(annotation, document.annotation());
  }

  @Test(expected = NullPointerException.class)
  public void nullAnnotationIsRejectedDuringWrapping() {
    new CoreDocument((Annotation) null);
  }
}
