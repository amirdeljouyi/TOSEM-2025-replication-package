package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoreDocument_5_GPTLLMTest {

 @Test
  public void testConstructorWithStringInitializesAnnotation() {
    String text = "This is a test.";
    CoreDocument coreDocument = new CoreDocument(text);

    assertNotNull(coreDocument.annotation());
    assertEquals(text, coreDocument.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotationInitializesCorrectly() {
    Annotation annotation = new Annotation("Some text");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc123");

    CoreDocument coreDocument = new CoreDocument(annotation);

    assertEquals("doc123", coreDocument.docID());
  }
@Test
  public void testTextReturnsCorrectValue() {
    String input = "Sample document text.";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);

    CoreDocument coreDocument = new CoreDocument(annotation);

    assertEquals(input, coreDocument.text());
  }
@Test
  public void testDocIDReturnsCorrectValue() {
    Annotation annotation = new Annotation("Doc text");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-001");

    CoreDocument coreDocument = new CoreDocument(annotation);

    assertEquals("doc-001", coreDocument.docID());
  }
@Test
  public void testDocDateReturnsCorrectValue() {
    Annotation annotation = new Annotation("Doc text");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2023-12-01");

    CoreDocument coreDocument = new CoreDocument(annotation);

    assertEquals("2023-12-01", coreDocument.docDate());
  }
@Test
  public void testTokensReturnsTokenList() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    Annotation annotation = new Annotation("Hello world");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreDocument coreDocument = new CoreDocument(annotation);

    assertEquals(2, coreDocument.tokens().size());
    assertEquals("Hello", coreDocument.tokens().get(0).word());
    assertEquals("world", coreDocument.tokens().get(1).word());
  }
@Test
  public void testCorefChainsReturnsCorrectMap() {
    CorefChain mockChain1 = mock(CorefChain.class);
    CorefChain mockChain2 = mock(CorefChain.class);

    Map<Integer, CorefChain> corefChains = new HashMap<>();
    corefChains.put(1, mockChain1);
    corefChains.put(2, mockChain2);

    Annotation annotation = new Annotation("John saw his brother.");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChains);

    CoreDocument coreDocument = new CoreDocument(annotation);

    Map<Integer, CorefChain> result = coreDocument.corefChains();
    assertEquals(2, result.size());
    assertSame(mockChain1, result.get(1));
    assertSame(mockChain2, result.get(2));
  }
@Test
  public void testToStringDelegatesToAnnotation() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.toString()).thenReturn("Mocked Annotation");

    CoreDocument coreDocument = new CoreDocument(annotation);

    assertEquals("Mocked Annotation", coreDocument.toString());
  }
@Test
  public void testWrapAnnotationsWithNoSentences() {
    Annotation annotation = new Annotation("Empty sentence list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    assertNull(coreDocument.sentences());
    assertNull(coreDocument.entityMentions());
    assertNull(coreDocument.quotes());
  }
@Test
  public void testWrapAnnotationsWithEmptySentenceList() {
    Annotation annotation = new Annotation("No sentence content");
    List<CoreMap> emptyList = new ArrayList<>();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, emptyList);

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    assertNotNull(coreDocument.sentences());
    assertEquals(0, coreDocument.sentences().size());
    assertNull(coreDocument.entityMentions());
  }
@Test
  public void testWrapAnnotationsWithNullEntityMentionsButHasGlobalMentions() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Document with global mentions");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>()); 

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    assertEquals(Collections.emptyList(), coreDocument.entityMentions());
  }
@Test
  public void testWrapAnnotationsWithQuoteList() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    CoreMap quote1 = mock(CoreMap.class);
    CoreMap quote2 = mock(CoreMap.class);
    List<CoreMap> mockQuotes = new ArrayList<>();
    mockQuotes.add(quote1);
    mockQuotes.add(quote2);

    Annotation annotation = new Annotation("Quoted text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testEntityMentionsBeforeWrappingReturnsNull() {
    Annotation annotation = new Annotation("Entity test case");

    CoreDocument document = new CoreDocument(annotation);

    assertNull(document.entityMentions());
  }
@Test
  public void testSentencesBeforeWrappingReturnsNull() {
    Annotation annotation = new Annotation("Sentence check");

    CoreDocument document = new CoreDocument(annotation);

    assertNull(document.sentences());
  }
@Test
  public void testQuotesBeforeWrappingReturnsNull() {
    Annotation annotation = new Annotation("Quote test");

    CoreDocument document = new CoreDocument(annotation);

    assertNull(document.quotes());
  }
@Test
  public void testWrapAnnotationsWithSentencesButNoEntityMentionsOrMentionsAnnotation() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Text with no mentions");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNotNull(document.sentences());
    assertEquals(1, document.sentences().size()); 
    assertNull(document.entityMentions()); 
  }
@Test
  public void testWrapSentencesHandlesSingleSentence() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Single sentence.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNotNull(document.sentences());
    assertEquals(1, document.sentences().size());
  }
@Test
  public void testEntityMentionsReturnsEmptyListWhenWrappedWithEmptyMentionsInSentences() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Text with empty sentence-level entity mentions.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNotNull(document.entityMentions());
    assertEquals(0, document.entityMentions().size());
  }
@Test
  public void testSentencesListPreservesOrder() {
    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("Two sentences.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertEquals(2, document.sentences().size());
    assertSame(document.sentences().get(0).coreMap(), sentence1);
    assertSame(document.sentences().get(1).coreMap(), sentence2);
  }
@Test
  public void testCorefChainsReturnsNullWhenNotSet() {
    Annotation annotation = new Annotation("No coref info here.");
    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.corefChains());
  }
@Test
  public void testDocIDReturnsNullWhenNotSet() {
    Annotation annotation = new Annotation("Document without ID.");
    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.docID());
  }
@Test
  public void testDocDateReturnsNullWhenNotSet() {
    Annotation annotation = new Annotation("Document without date.");
    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.docDate());
  }
@Test
  public void testTextReturnsNullWhenNotSet() {
    Annotation annotation = new Annotation("Text should be null.");
    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.text());
  }
@Test
  public void testTokensReturnsNullWhenNotSet() {
    Annotation annotation = new Annotation("No tokens set.");
    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.tokens());
  }
@Test
  public void testQuotesReturnsEmptyListWhenGatheredQuotesEmpty() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Quote handling test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testWrapAnnotationsWithNullQuotes() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Test with quotes == null");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation))
            .thenReturn(null);

      CoreDocument coreDocument = new CoreDocument(annotation);
      coreDocument.wrapAnnotations();

      assertNull(coreDocument.quotes()); 
    }
  }
@Test
  public void testWrapAnnotationsWithNullAnnotationObject() {
    Annotation annotation = new Annotation("Test doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();
    assertNull(coreDocument.sentences());
    assertNull(coreDocument.entityMentions());
    assertNull(coreDocument.quotes());
  }
@Test
  public void testWrapAnnotationsWithSentencesNullEntityMentionsWithoutMentionsAnnotation() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Entity-less sentence.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNotNull(document.sentences());
    assertEquals(1, document.sentences().size());
    assertNull(document.entityMentions());
  }
@Test
  public void testEntityMentionsReturnsEmptyListWhenSentenceMentionListIsEmpty() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Empty mention test.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNotNull(document.entityMentions());
    assertTrue(document.entityMentions().isEmpty());
  }
@Test
  public void testWrapAnnotationsWithSentencesAndNullReturnedFromQuoteAnnotator() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Quote null test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();

      assertNull(doc.quotes());
    }
  }
@Test
  public void testWrapAnnotationsWithQuoteListEmpty() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Quote empty test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation))
            .thenReturn(Collections.emptyList());

      CoreDocument coreDocument = new CoreDocument(annotation);
      coreDocument.wrapAnnotations();

      List<CoreQuote> quotes = coreDocument.quotes();
      assertNotNull(quotes);
      assertEquals(0, quotes.size());
    }
  }
@Test
  public void testSentencesReturnsEmptyListAfterWrapWithEmptyAnnotationSentences() {
    Annotation annotation = new Annotation("Empty sentence set");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    assertNotNull(coreDocument.sentences());
    assertEquals(0, coreDocument.sentences().size());
  }
@Test
  public void testQuotesUninitializedWhenNoWrapCalled() {
    Annotation annotation = new Annotation("Quote not wrapped");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.quotes());
  }
@Test
  public void testEntityMentionsUninitializedWhenNoWrapCalled() {
    Annotation annotation = new Annotation("Mentions not wrapped");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.entityMentions());
  }
@Test
  public void testSentencesUninitializedWhenNoWrapCalled() {
    Annotation annotation = new Annotation("Sentences not wrapped");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.sentences());
  }
@Test
  public void testCorefChainsReturnsEmptyMapReference() {
    Annotation annotation = new Annotation("Doc with no corefs");
    annotation.set(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    CoreDocument coreDocument = new CoreDocument(annotation);

    Map<Integer, CorefChain> chains = coreDocument.corefChains();
    assertNotNull(chains);
    assertTrue(chains.isEmpty());
  }
@Test
  public void testMultipleEntityMentionsFromMultipleSentences() {
    CoreEntityMention em1 = mock(CoreEntityMention.class);
    CoreEntityMention em2 = mock(CoreEntityMention.class);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("Multi sentence entity test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    assertNotNull(coreDocument.sentences());
    assertEquals(2, coreDocument.sentences().size());

    List<CoreEntityMention> mentions = coreDocument.entityMentions();
    assertNotNull(mentions);
    assertEquals(2, mentions.size());
    assertTrue(mentions.contains(em1));
    assertTrue(mentions.contains(em2));
  }
@Test
  public void testDocFieldsAllReturnNullWhenUnset() {
    Annotation annotation = new Annotation("Unset fields doc");

    CoreDocument doc = new CoreDocument(annotation);

    assertNull(doc.text());
    assertNull(doc.docID());
    assertNull(doc.docDate());
    assertNull(doc.tokens());
  }
@Test
  public void testEmptyDocumentTextInStringConstructor() {
    CoreDocument coreDocument = new CoreDocument("");
    assertNotNull(coreDocument.annotation());
    assertEquals("", coreDocument.annotation().get(CoreAnnotations.TextAnnotation.class));
    assertNull(coreDocument.docID());
    assertNull(coreDocument.tokens());
    assertNull(coreDocument.sentences());
  }
@Test
  public void testAnnotationConstructorWithoutAnyAnnotations() {
    Annotation annotation = new Annotation("No annotations set");
    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.docID());
    assertNull(document.docDate());
    assertNull(document.tokens());
    assertNull(document.entityMentions());
    assertNull(document.sentences());
    assertNull(document.quotes());
    assertNull(document.corefChains());
  }
@Test
  public void testWrapAnnotationsWithSentencesAndEmptyMentionsInSecondSentenceOnly() {
    CoreEntityMention mentionA = mock(CoreEntityMention.class);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    when(sentence2.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("One meaningful sentence, one empty.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    List<CoreEntityMention> mentions = coreDocument.entityMentions();
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertTrue(mentions.contains(mentionA));
  }
@Test
  public void testWrapAnnotationsCalledTwiceProducesSameResult() {
    CoreEntityMention mention = mock(CoreEntityMention.class);
    CoreMap sentence = mock(CoreMap.class);

    List<CoreMap> sentences = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("Repeat wrap");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    List<CoreEntityMention> firstMentions = coreDocument.entityMentions();
    List<CoreSentence> firstSentences = coreDocument.sentences();

    coreDocument.wrapAnnotations(); 

    assertEquals(firstMentions, coreDocument.entityMentions());
    assertEquals(firstSentences, coreDocument.sentences());
  }
@Test
  public void testToStringReturnsUnderlyingAnnotationToString() {
    Annotation annotation = new Annotation("Annotated string");
    CoreDocument coreDocument = new CoreDocument(annotation);
    assertEquals(annotation.toString(), coreDocument.toString());
  }
@Test
  public void testWrapSentencesWithSingleTokenSentence() {
    CoreMap sentence = mock(CoreMap.class);
    CoreEntityMention singleMention = mock(CoreEntityMention.class);

    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("One-word sentence.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNotNull(document.sentences());
    assertEquals(1, document.sentences().size());
    assertEquals(1, document.entityMentions().size());
  }
@Test
  public void testWrapAnnotationsWithMixedNullAndNonNullMentions() {
    CoreEntityMention nonNullMention = mock(CoreEntityMention.class);

    CoreMap sentence1 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);

    Annotation annotation = new Annotation("Mixed mentions");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument coreDocument = new CoreDocument(annotation);
    coreDocument.wrapAnnotations();

    List<CoreEntityMention> mentions = coreDocument.entityMentions();

    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertTrue(mentions.contains(nonNullMention));
  }
@Test
  public void testTokensListEmpty() {
    Annotation annotation = new Annotation("Tokenless doc");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    CoreDocument document = new CoreDocument(annotation);
    List<CoreLabel> result = document.tokens();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testcorefChainsExplicitlyNull() {
    Annotation annotation = new Annotation("No coref example");
    annotation.set(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);

    assertNull(doc.corefChains());
  }
@Test
  public void testDocDateAnnotationEmptyString() {
    Annotation annotation = new Annotation("Empty date");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "");

    CoreDocument doc = new CoreDocument(annotation);

    assertEquals("", doc.docDate());
  }
@Test
  public void testDocIDAnnotationEmptyString() {
    Annotation annotation = new Annotation("Empty ID");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "");

    CoreDocument doc = new CoreDocument(annotation);

    assertEquals("", doc.docID());
  }
@Test
  public void testQuotesListReturnedEmptyButNonNull() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Empty quotes test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation))
            .thenReturn(Collections.emptyList());

      CoreDocument document = new CoreDocument(annotation);
      document.wrapAnnotations();

      List<CoreQuote> quotes = document.quotes();
      assertNotNull(quotes);
      assertTrue(quotes.isEmpty());
    }
  }
@Test
  public void testWrapAnnotationsWithNullSentencesAnnotationKey() {
    Annotation annotation = new Annotation("No sentence annotations");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsWithNullMentionsInSentencesButMentionsAnnotationSetGlobal() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("Mixed mentions and sentences");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertEquals(Collections.emptyList(), doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsWithEmptySentencesListTriggersQuoteBuildIfQuotesExist() {
    Annotation annotation = new Annotation("Only quotes, no sentences");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    List<CoreMap> quoteMaps = new ArrayList<>();
    quoteMaps.add(mock(CoreMap.class));

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(quoteMaps);

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();
      List<CoreQuote> quotes = doc.quotes();
      assertNotNull(quotes);
      assertEquals(1, quotes.size());
    }
  }
@Test
  public void testWrapAnnotationsQuoteAnnotatorReturnsNullIsHandled() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Quote null safety");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();

      assertNull(doc.quotes()); 
    }
  }
@Test
  public void testWrapSentencesCreatesCoreSentenceInstances() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Simple one sentence");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
    assertSame(sentence, doc.sentences().get(0).coreMap());
  }
@Test
  public void testBuildDocumentEntityMentionsEmptyWhenAllSentencesLackMentions() {
    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    when(sentence2.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> sentenceList = Arrays.asList(sentence1, sentence2);
    Annotation annotation = new Annotation("Sentences with no mentions");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(2, doc.sentences().size());
    assertNotNull(doc.entityMentions());
    assertTrue(doc.entityMentions().isEmpty());
  }
@Test
  public void testCorefChainsAnnotationAbsentReturnsNull() {
    Annotation annotation = new Annotation("Test missing coref chains");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.corefChains());
  }
@Test
  public void testCorefChainsAnnotationPresentReturnsCorrectMap() {
    CorefChain fakeChain = mock(CorefChain.class);
    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(42, fakeChain);

    Annotation annotation = new Annotation("Coref Data");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertSame(fakeChain, result.get(42));
  }
@Test
  public void testToStringHandlesUnderlyingAnnotationWithCustomToString() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.toString()).thenReturn("MockAnnotationString");

    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("MockAnnotationString", doc.toString());
  }
@Test
  public void testWrapAnnotationsWithMultipleEntityMentionsAccumulatesAll() {
    CoreEntityMention mention1 = mock(CoreEntityMention.class);
    CoreEntityMention mention2 = mock(CoreEntityMention.class);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("Entity tracking");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertEquals(2, mentions.size());
    assertTrue(mentions.contains(mention1));
    assertTrue(mentions.contains(mention2));
  }
@Test
  public void testAnnotationChainingPreservesInstance() {
    Annotation annotation = new Annotation("Text for chaining");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Text for chaining");

    CoreDocument doc = new CoreDocument(annotation);
    assertSame(annotation, doc.annotation());
  }
@Test
  public void testToStringWithEmptyAnnotation() {
    Annotation annotation = new Annotation("");
    CoreDocument doc = new CoreDocument(annotation);
    assertTrue(doc.toString().contains("Annotation"));
  }
@Test
  public void testWrapAnnotationsWithSentencesButFirstSentenceHasNullEntityMentions() {
    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
    CoreEntityMention mention = mock(CoreEntityMention.class);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("Mixed mention structure");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    List<CoreEntityMention> mentions = document.entityMentions();
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertTrue(mentions.contains(mention));
  }
@Test
  public void testWrapAnnotationsWithEmptyEntityMentionListOnFirstSentencePreventsEntityBuild() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Empty entity list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    List<CoreEntityMention> mentions = document.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testQuotesReturnsNullWhenWrapNotCalled() {
    Annotation annotation = new Annotation("Unwrapped annotation");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.quotes());
  }
@Test
  public void testEntityMentionsReturnsNullWhenWrapNotCalled() {
    Annotation annotation = new Annotation("Test without wrap");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.entityMentions());
  }
@Test
  public void testSentencesReturnsNullWhenWrapNotCalled() {
    Annotation annotation = new Annotation("Sentences not wrapped");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.sentences());
  }
@Test
  public void testWrapAnnotationsWithEmptyMentionsAnnotationAndEmptySentenceMentions() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Global mentions only");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.entityMentions());
    assertEquals(0, doc.entityMentions().size());
  }
@Test
  public void testQuotesReturnsEmptyListWhenQuoteAnnotatorReturnsEmptyList() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Empty quotes");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation))
            .thenReturn(Collections.emptyList());

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();

      List<CoreQuote> quotes = doc.quotes();
      assertNotNull(quotes);
      assertEquals(0, quotes.size());
    }
  }
@Test
  public void testQuotesReturnsNullWhenQuoteAnnotatorReturnsNull() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Null quote list return");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation))
            .thenReturn(null);

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();

      assertNull(doc.quotes());
    }
  }
@Test
  public void testTokensReturnsEmptyListWhenTokenListIsEmpty() {
    Annotation annotation = new Annotation("No tokens");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(annotation);
    List<CoreLabel> tokens = doc.tokens();

    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testTextAnnotationIsExplicitlyNull() {
    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.TextAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());
  }
@Test
  public void testDocIdAnnotationIsExplicitlyNull() {
    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docID());
  }
@Test
  public void testDocDateAnnotationIsExplicitlyNull() {
    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docDate());
  }
@Test
  public void testCorefChainsReturnsNullWhenAbsent() {
    Annotation annotation = new Annotation("Missing coref");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.corefChains());
  }
@Test
  public void testCorefChainsReturnsEmptyMap() {
    Map<Integer, CorefChain> emptyMap = new HashMap<>();
    Annotation annotation = new Annotation("Empty coref map");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, emptyMap);

    CoreDocument doc = new CoreDocument(annotation);
    assertNotNull(doc.corefChains());
    assertEquals(0, doc.corefChains().size());
  }
@Test
  public void testWrapAnnotationsWithQuotesOnlyNoSentences() {
    Annotation annotation = new Annotation("Just quotes");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    CoreMap quote = mock(CoreMap.class);
    List<CoreMap> quoteList = Collections.singletonList(quote);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(quoteList);

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();

      List<CoreQuote> quotes = doc.quotes();
      assertNotNull(quotes);
      assertEquals(1, quotes.size());
    }
  }
@Test
  public void testWrapAnnotationsWithMissingSentenceThenSetLater() {
    Annotation annotation = new Annotation("Deferred sentence injection");
    CoreDocument doc = new CoreDocument(annotation);

    assertNull(doc.sentences());

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    doc.wrapAnnotations();
    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
  }
@Test
  public void testWrapAnnotationsCalledTwiceYieldsConsistentEntities() {
    CoreEntityMention mention = mock(CoreEntityMention.class);
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Twice called wrapAnnotations");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreEntityMention> before = doc.entityMentions();
    doc.wrapAnnotations();
    List<CoreEntityMention> after = doc.entityMentions();

    assertSame(before, after);
    assertEquals(1, before.size());
  }
@Test
  public void testWrapAnnotationsHandlesSentencesWithMixedNullMentionsAndNonEmpty() {
    CoreMap sentence1 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    CoreEntityMention mention = mock(CoreEntityMention.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = Arrays.asList(sentence1, sentence2);
    Annotation annotation = new Annotation("Mixed entity annotations");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> result = doc.entityMentions();
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(mention));
  }
@Test
  public void testEntityMentionsAbsentWhenAnnotationContainsOnlyGlobalEmptyMentions() {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("Global mentions only (empty)");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.entityMentions());
    assertEquals(0, doc.entityMentions().size());
  }
@Test
  public void testQuoteListNullAfterWrapWhenGatherQuotesReturnsNull() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("null quote gathering");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();

      assertNull(doc.quotes());
    }
  }
@Test
  public void testQuoteListEmptyAfterWrapWhenGatherQuotesReturnsEmpty() {
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("empty quote list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try (MockedStatic<QuoteAnnotator> mocked = Mockito.mockStatic(QuoteAnnotator.class)) {
      mocked.when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(Collections.emptyList());

      CoreDocument doc = new CoreDocument(annotation);
      doc.wrapAnnotations();

      List<CoreQuote> quotes = doc.quotes();
      assertNotNull(quotes);
      assertTrue(quotes.isEmpty());
    }
  }
@Test
  public void testDocIdReturnsEmptyStringIfExplicitlySet() {
    Annotation annotation = new Annotation("explicit empty doc id");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "");

    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("", doc.docID());
  }
@Test
  public void testTextReturnsNullIfTextAnnotationNotSet() {
    Annotation annotation = new Annotation("Text annotation not set");

    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());
  }
@Test
  public void testTokensReturnsNullIfTokensAnnotationAbsent() {
    Annotation annotation = new Annotation("no tokens annotation");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.tokens());
  }
@Test
  public void testToStringDelegatesToAnnotationToString() {
    Annotation annotation = new Annotation("delegate toString");
    CoreDocument doc = new CoreDocument(annotation);
    String result = doc.toString();
    assertNotNull(result);
    assertTrue(result.contains("delegate toString"));
  }
@Test
  public void testAnnotationReturnsUnderlyingObject() {
    Annotation annotation = new Annotation("Direct annotation");
    CoreDocument doc = new CoreDocument(annotation);
    assertSame(annotation, doc.annotation());
  }
@Test
  public void testCorefChainReturnsSetMap() {
    CorefChain chain = mock(CorefChain.class);
    Map<Integer, CorefChain> chainMap = new HashMap<>();
    chainMap.put(0, chain);

    Annotation annotation = new Annotation("coref presence check");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertSame(chain, result.get(0));
  }
@Test
  public void testCorefChainReturnsNullIfAnnotationNotSet() {
    Annotation annotation = new Annotation("missing coref");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.corefChains());
  } 
}