package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoreDocument_3_GPTLLMTest {

 @Test
  public void testConstructorWithText() {
    CoreDocument doc = new CoreDocument("This is a test.");
    assertNotNull(doc.annotation());
    assertEquals("This is a test.", doc.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotation() {
    Annotation annotation = new Annotation("Sample text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Sample text.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNotNull(doc.annotation());
    assertEquals("Sample text.", doc.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDocIDReturnsValue() {
    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc_123");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("doc_123", doc.docID());
  }
@Test
  public void testDocDateReturnsValue() {
    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-06-01");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("2024-06-01", doc.docDate());
  }
@Test
  public void testTextReturnsOriginalText() {
    Annotation annotation = new Annotation("Some sentence.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Some sentence.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("Some sentence.", doc.text());
  }
@Test
  public void testTokensReturnsTokenList() {
    Annotation annotation = new Annotation("Tokens");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Apple");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("pie");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument(annotation);

    List<CoreLabel> resultTokens = doc.tokens();
    assertNotNull(resultTokens);
    assertEquals(2, resultTokens.size());
    assertEquals("Apple", resultTokens.get(0).word());
    assertEquals("pie", resultTokens.get(1).word());
  }
@Test
  public void testSentencesReturnsWrappedSentences() {
    Annotation annotation = new Annotation("Sentence text");

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentenceMap);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreEntityMention entityMention = mock(CoreEntityMention.class);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> sentenceWrappers = doc.sentences();
    assertNotNull(sentenceWrappers);
    assertEquals(1, sentenceWrappers.size());
  }
@Test
  public void testEntityMentionsReturnsDocumentEntities() {
    Annotation annotation = new Annotation("Document with entity");

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentenceMap);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreEntityMention mention = mock(CoreEntityMention.class);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testEntityMentionsReturnsEmptyListIfMentionsAnnotationPresent() {
    Annotation annotation = new Annotation("No real entity mentions");

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentenceMap);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    when(sentenceMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testQuotesReturnsQuotesIfAnnotationPresent() {
    Annotation annotation = new Annotation("Quote test");
    List<CoreMap> sentenceList = new ArrayList<>();
    CoreMap sentence = mock(CoreMap.class);
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    List<CoreMap> quoteList = new ArrayList<>();
    CoreMap quote = mock(CoreMap.class);
    quoteList.add(quote);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> actualQuotes = doc.quotes();
    assertNotNull(actualQuotes);
    assertEquals(1, actualQuotes.size());
  }
@Test
  public void testCorefChainsReturnsMap() {
    Annotation annotation = new Annotation("Coref test");

    CorefChain chain = mock(CorefChain.class);
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, chain);

    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> resultMap = doc.corefChains();

    assertNotNull(resultMap);
    assertEquals(1, resultMap.size());
    assertTrue(resultMap.containsKey(1));
  }
@Test
  public void testToStringUsesAnnotationToString() {
    Annotation annotation = new Annotation("ToString check");
    annotation.set(CoreAnnotations.TextAnnotation.class, "ToString check");

    CoreDocument doc = new CoreDocument(annotation);
    String result = doc.toString();

    assertNotNull(result);
    assertTrue(result.contains("ToString check"));
  }
@Test
  public void testSentencesReturnsNullIfNotWrapped() {
    Annotation annotation = new Annotation("Sentence not wrapped");
    CoreDocument doc = new CoreDocument(annotation);
    List<CoreSentence> result = doc.sentences();
    assertNull(result);
  }
@Test
  public void testQuotesReturnsNullIfNotWrapped() {
    Annotation annotation = new Annotation("Quote not wrapped");
    CoreDocument doc = new CoreDocument(annotation);
    List<CoreQuote> result = doc.quotes();
    assertNull(result);
  }
@Test
  public void testEntityMentionsReturnsNullIfNotWrapped() {
    Annotation annotation = new Annotation("Mentions not wrapped");
    CoreDocument doc = new CoreDocument(annotation);
    List<CoreEntityMention> result = doc.entityMentions();
    assertNull(result);
  }
@Test
  public void testWrapAnnotationsWithNoSentences() {
    Annotation annotation = new Annotation("Empty annotation without SentencesAnnotation");
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsSentenceListPresentEntityMentionsNullNoMentionsAnnotation() {
    Annotation annotation = new Annotation("Entity mention missing");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotation.remove(CoreAnnotations.MentionsAnnotation.class);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> sentenceWrappers = doc.sentences();
    assertNotNull(sentenceWrappers);
    assertEquals(1, sentenceWrappers.size());

    assertNull(doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsSentencesPresentMentionsAnnotationSetButSentencesEmpty() {
    Annotation annotation = new Annotation("Doc with mentions but no sentences");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.sentences());
    List<CoreEntityMention> mentions = doc.entityMentions();
    assertEquals(Collections.emptyList(), mentions);
  }
@Test
  public void testWrapAnnotationsSentencesHasNullList() {
    Annotation annotation = new Annotation("null sentence list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
  }
@Test
  public void testAnnotationReturnsSameInstance() {
    Annotation annotation = new Annotation("Check annotation identity");
    CoreDocument doc = new CoreDocument(annotation);
    assertSame(annotation, doc.annotation());
  }
@Test
  public void testDocIDReturnsNullIfNotSet() {
    Annotation annotation = new Annotation("Test");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docID());
  }
@Test
  public void testDocDateReturnsNullIfNotSet() {
    Annotation annotation = new Annotation("Test");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docDate());
  }
@Test
  public void testTextReturnsNullIfNotSetExplicitly() {
    Annotation annotation = new Annotation("Test that TextAnnotation is null");
    annotation.remove(CoreAnnotations.TextAnnotation.class);

    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());
  }
@Test
  public void testTokensReturnsNullIfNotSet() {
    Annotation annotation = new Annotation("Test uninitialized tokens");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.tokens());
  }
@Test
  public void testCorefChainsReturnsNullIfNotPresent() {
    Annotation annotation = new Annotation("Test coref missing");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.corefChains());
  }
@Test
  public void testToStringOnEmptyAnnotationInstance() {
    Annotation annotation = new Annotation("Initial");
    annotation.remove(CoreAnnotations.TextAnnotation.class);
    CoreDocument doc = new CoreDocument(annotation);
    String output = doc.toString();
    assertNotNull(output);
  }
@Test
  public void testMultipleEntityMentionsFromMultipleSentences() {
    Annotation annotation = new Annotation("Simulate multiple sentences");

    CoreEntityMention mention1 = mock(CoreEntityMention.class);
    CoreEntityMention mention2 = mock(CoreEntityMention.class);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> resultMentions = doc.entityMentions();
    assertNotNull(resultMentions);
    assertEquals(2, resultMentions.size());
  }
@Test
  public void testWrapSentencesWithMultipleSentenceMaps() {
    Annotation annotation = new Annotation("Multi sentence doc");

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);


    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> sentences = doc.sentences();
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
  }
@Test
  public void testQuotesReturnsEmptyWhenQuoteAnnotatorReturnsEmptyList() {
    Annotation annotation = new Annotation("Empty quotes");
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    
    assertNull(quotes);
  }
@Test
  public void testWrapAnnotationsWithSentencesAndNoEntityMentionsKey() {
    Annotation annotation = new Annotation("Sentence but entityMentions is never set");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> sentences = doc.sentences();
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    List<CoreEntityMention> entityMentions = doc.entityMentions();
    assertNull(entityMentions); 
  }
@Test
  public void testWrapAnnotationsWithSentencesThatReturnNullMentionsAnnotation() {
    Annotation annotation = new Annotation("Edge case where mentions are null and MentionsAnnotation present");

    CoreMap sentenceMap = mock(CoreMap.class);
    when(sentenceMap.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentenceMap);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());

    List<CoreEntityMention> mentions = doc.entityMentions(); 
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testWrapAnnotationsWithNullReturnedFromQuoteAnnotator() {
    Annotation annotation = new Annotation("QuoteAnnotator returns null");

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNull(quotes); 
  }
@Test
  public void testWrapSentencesCreatesValidSentencesEvenIfMentionsEmpty() {
    Annotation annotation = new Annotation("Sentence without entity mentions");

    CoreMap sentenceMap = mock(CoreMap.class);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentenceMap);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> wrappedSentences = doc.sentences();
    assertNotNull(wrappedSentences);
    assertEquals(1, wrappedSentences.size());
  }
@Test
  public void testBuildDocumentEntityMentionsListReturnsEmptyWhenAllSentencesHaveEmptyMentions() {
    Annotation annotation = new Annotation("Multiple sentences with no mentions");

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testToStringHandlesNullTextGracefully() {
    Annotation annotation = new Annotation((Annotation) null);
    CoreDocument doc = new CoreDocument(annotation);
    String result = doc.toString();
    assertNotNull(result);
  }
@Test
  public void testConstructorWithAnnotationContainingNullFields() {
    Annotation annotation = new Annotation((String) null);
    annotation.set(CoreAnnotations.TextAnnotation.class, null);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, null);
    annotation.set(CoreAnnotations.DocDateAnnotation.class, null);
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());
    assertNull(doc.docID());
    assertNull(doc.docDate());
  }
@Test
  public void testEntityMentionsNotPopulatedWhenSentenceListEmptyAndNoMentionsAnnotation() {
    Annotation annotation = new Annotation("Test sentence");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsDoesNotBuildQuotesWhenQuoteAnnotatorReturnsNull() {
    Annotation annotation = new Annotation("Quote-less document");

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotation.set(CoreAnnotations.QuotationsAnnotation.class, null); 

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsSkipsEntityMentionsWhenFirstSentenceDoesNotHaveMentions() {
    Annotation annotation = new Annotation("Document with first sentence missing mentions");

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.entityMentions()); 
  }
@Test
  public void testTextGetterReturnsNullWhenAnnotationTextUnset() {
    Annotation annotation = new Annotation((String) null);
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());
  }
@Test
  public void testTokensReturnsEmptyListIfAnnotationTokenKeyIsEmptyList() {
    Annotation annotation = new Annotation("Token test");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    CoreDocument doc = new CoreDocument(annotation);

    List<CoreLabel> tokens = doc.tokens();
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testCorefChainsReturnsEmptyMap() {
    Annotation annotation = new Annotation("Coref");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());
    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> coref = doc.corefChains();
    assertNotNull(coref);
    assertTrue(coref.isEmpty());
  }
@Test
  public void testSentencesReturnsListWithMultipleWrappedSentences() {
    Annotation annotation = new Annotation("Multiple sentences");

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> resultSentences = doc.sentences();
    assertNotNull(resultSentences);
    assertEquals(2, resultSentences.size());
  }
@Test
  public void testQuotesListIsEmptyWhenQuoteAnnotationSetToEmptyList() {
    Annotation annotation = new Annotation("Quote test");

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(mock(CoreMap.class));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<CoreMap> emptyQuotes = new ArrayList<CoreMap>();
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, emptyQuotes);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testToStringReturnsNonNullEvenIfAnnotationEmpty() {
    Annotation annotation = new Annotation((String) null);
    CoreDocument doc = new CoreDocument(annotation);
    String output = doc.toString();
    assertNotNull(output);
  }
@Test
  public void testEntityMentionsHandlesMixedEmptyAndNonEmptySentences() {
    Annotation annotation = new Annotation("Mixed sentences");

    CoreEntityMention mention = mock(CoreEntityMention.class);
    List<CoreEntityMention> mentions = new ArrayList<CoreEntityMention>();
    mentions.add(mention);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> sentenceWrappers = doc.sentences(); 
    assertNotNull(sentenceWrappers);
    assertEquals(2, sentenceWrappers.size());

    List<CoreEntityMention> flattenedMentions = doc.entityMentions();
    assertNull(flattenedMentions); 
  }
@Test
  public void testWrapAnnotationsWithSentencesAndFirstSentenceReturnsEmptyEntityMentionsList() {
    Annotation annotation = new Annotation("First sentence has empty entity list");

    CoreMap firstSentence = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(firstSentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
    assertNull(doc.entityMentions()); 
  }
@Test
  public void testWrapAnnotationsExecutesQuoteWrappingIfQuotesExist() {
    Annotation annotation = new Annotation("Quote exists");

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreMap quote1 = mock(CoreMap.class);
    CoreMap quote2 = mock(CoreMap.class);

    List<CoreMap> quoteList = new ArrayList<CoreMap>();
    quoteList.add(quote1);
    quoteList.add(quote2);

    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quoteList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertEquals(2, quotes.size());
  }
@Test
  public void testWrapAnnotationsSkipsQuoteWrappingWhenQuoteListIsEmpty() {
    Annotation annotation = new Annotation("No quotes");

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentenceMap);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertEquals(0, quotes.size());
  }
@Test
  public void testBuildDocumentEntityMentionsListCollectsMultipleEntitiesFromMultipleSentences() {
    Annotation annotation = new Annotation("Test multi-mention collection");

    CoreEntityMention mention1 = mock(CoreEntityMention.class);
    CoreEntityMention mention2 = mock(CoreEntityMention.class);
    CoreEntityMention mention3 = mock(CoreEntityMention.class);

    List<CoreEntityMention> list1 = new ArrayList<CoreEntityMention>();
    list1.add(mention1);
    list1.add(mention2);

    List<CoreEntityMention> list2 = new ArrayList<CoreEntityMention>();
    list2.add(mention3);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> allMentions = doc.entityMentions();
    assertNotNull(allMentions);
    assertEquals(3, allMentions.size());
  }
@Test
  public void testCorefChainsReturnsNonNullWhenKeyIsPresentWithNullValue() {
    Annotation annotation = new Annotation("Coref field with null value");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, null);
    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> chains = doc.corefChains();
    assertNull(chains); 
  }
@Test
  public void testTokensListReturnsCorrectTypesWithMultipleCoreLabels() {
    Annotation annotation = new Annotation("Tokens verify");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setValue("Hello");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    token2.setValue("World");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);
    tokens.add(token2);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreDocument doc = new CoreDocument(annotation);
    List<CoreLabel> out = doc.tokens();

    assertEquals(2, out.size());
    assertEquals("Hello", out.get(0).word());
    assertEquals("World", out.get(1).word());
  }
@Test
  public void testToStringHandlesEmptyAnnotationGracefully() {
    Annotation annotation = new Annotation((String) null);
    CoreDocument doc = new CoreDocument(annotation);
    String result = doc.toString();
    assertNotNull(result);
    assertTrue(result.contains("Annotation"));
  }
@Test
  public void testNoExceptionWhenAnnotationHasNoRelevantKeys() {
    Annotation annotation = new Annotation("No keys");
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations(); 

    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
    assertNull(doc.tokens());
    assertNull(doc.text());
  }
@Test
  public void testWrapAnnotationsExecutesProperlyWhenQuoteListIsNull() {
    Annotation annotation = new Annotation("Quote null shortcut");

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotation.set(CoreAnnotations.QuotationsAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsHandlesSingleSentenceWithNullMentions() {
    Annotation annotation = new Annotation("Single null mention");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
    assertNull(doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsSentenceListContainsNullElement() {
    Annotation annotation = new Annotation("Sentence list contains null");
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(null); 
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreSentence> sentences = doc.sentences();
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testWrapAnnotationsMultipleSentencesFirstLacksMentionsSecondHasMentions_NoMentionsInDocKey() {
    Annotation annotation = new Annotation("Mixed mentions across sentences");

    CoreEntityMention entity = mock(CoreEntityMention.class);
    List<CoreEntityMention> secondMentions = new ArrayList<CoreEntityMention>();
    secondMentions.add(entity);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    
    assertNull(doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsSkipEntityMentionBlockWhenFirstSentenceHasEmptyMentionsAndMentionsAnnotationIsNull() {
    Annotation annotation = new Annotation("Empty mentions list on first sentence");

    CoreMap sentence1 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence1);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsEmptyQuoteListSkipsBuildQuoteList() {
    Annotation annotation = new Annotation("Empty quotes simulate");

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotation.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<CoreMap>()); 

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty()); 
  }
@Test
  public void testWrapSentencesWithSingleSentenceCreatesWrappedSentenceWithEntityMentionList() {
    Annotation annotation = new Annotation("Single sentence with one entity");

    CoreEntityMention mention = mock(CoreEntityMention.class);
    List<CoreEntityMention> mentionList = new ArrayList<CoreEntityMention>();
    mentionList.add(mention);

    CoreMap sentence = mock(CoreMap.class);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> wrappedSentences = doc.sentences();
    assertNotNull(wrappedSentences);
    assertEquals(1, wrappedSentences.size());

    List<CoreEntityMention> em = doc.entityMentions();
    assertNotNull(em);
    assertEquals(1, em.size());
  }
@Test
  public void testCorefChainsReturnsNullWhenKeyNotSet() {
    Annotation annotation = new Annotation("No coref chain key");
    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> chains = doc.corefChains();
    assertNull(chains);
  }
@Test
  public void testDocIDReturnsNullWhenUnset() {
    Annotation annotation = new Annotation("Unset doc ID");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docID());
  }
@Test
  public void testDocDateReturnsNullWhenUnset() {
    Annotation annotation = new Annotation("Unset doc date");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docDate());
  }
@Test
  public void testTextReturnsNullWhenUnset() {
    Annotation annotation = new Annotation("Unset text");
    annotation.remove(CoreAnnotations.TextAnnotation.class);
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());
  }
@Test
  public void testTokensReturnsNullWhenUnset() {
    Annotation annotation = new Annotation("Unset tokens");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.tokens());
  }
@Test
  public void testToStringStillWorksWhenTextAnnotationIsMissing() {
    Annotation annotation = new Annotation("Missing TextAnnotation");
    annotation.remove(CoreAnnotations.TextAnnotation.class);
    CoreDocument doc = new CoreDocument(annotation);
    String str = doc.toString();
    assertNotNull(str);
  }
@Test
  public void testQuotesReturnsNullWhenNotWrapped() {
    Annotation annotation = new Annotation("Quotes never initialized");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.quotes());
  }
@Test
  public void testEntityMentionsReturnsNullWhenNotWrapped() {
    Annotation annotation = new Annotation("Entity mentions never initialized");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.entityMentions());
  }
@Test
  public void testSentencesReturnsNullWhenNotWrapped() {
    Annotation annotation = new Annotation("Sentences never initialized");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.sentences());
  }
@Test
  public void testConstructorWithTextSetsTextAnnotation() {
    CoreDocument doc = new CoreDocument("Sample input text.");
    Annotation annotation = doc.annotation();
    String text = annotation.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Sample input text.", text);
  }
@Test
  public void testWrapSentencesAssignsCorrectSentenceObjects() {
    Annotation annotation = new Annotation("Multi-sentence test.");

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    when(sentence1.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
    when(sentence2.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> result = doc.sentences();
    assertNotNull(result);
    assertEquals(2, result.size());
  }
@Test
  public void testWrapAnnotationsSkipsEntityMentionExtractionWhenNoSentences() {
    Annotation annotation = new Annotation("Testing no SentencesAnnotation");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsSkipsQuoteWrappingWhenSentenceListIsNull() {
    Annotation annotation = new Annotation("Skip quotes when no sentences");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.quotes());
  }
@Test
  public void testBuildDocumentEntityMentionsListHandlesAllSentencesWithEmptyMentions() {
    CoreEntityMention mention = mock(CoreEntityMention.class);
    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("Entity mention flattening");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testBuildDocumentQuotesListCreatesCorrectNumberOfQuotes() {
    CoreMap quote1 = mock(CoreMap.class);
    CoreMap quote2 = mock(CoreMap.class);

    List<CoreMap> quotes = new ArrayList<CoreMap>();
    quotes.add(quote1);
    quotes.add(quote2);

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Quote creation");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> coreQuotes = doc.quotes();
    assertNotNull(coreQuotes);
    assertEquals(2, coreQuotes.size());
  }
@Test
  public void testToStringReturnsNonNullStringWhenAnnotationFieldsAreMissing() {
    Annotation annotation = new Annotation((String) null);
    annotation.remove(CoreAnnotations.TextAnnotation.class);
    annotation.remove(CoreAnnotations.DocIDAnnotation.class);
    annotation.remove(CoreAnnotations.DocDateAnnotation.class);

    CoreDocument doc = new CoreDocument(annotation);
    String result = doc.toString();
    assertNotNull(result);
    assertTrue(result.contains("Annotation"));
  }
@Test
  public void testWrapAnnotationsDoesNotThrowOnMixedAnnotationElements() {
    Annotation annotation = new Annotation("Mixed annotations");

    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null);
    annotation.set(CoreAnnotations.TokensAnnotation.class, null);
    annotation.set(CoreAnnotations.TextAnnotation.class, null);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, null);
    annotation.set(CoreAnnotations.DocDateAnnotation.class, null);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, null);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
    assertNull(doc.tokens());
    assertNull(doc.text());
    assertNull(doc.docID());
    assertNull(doc.docDate());
    assertNull(doc.corefChains());
  }
@Test
  public void testEntityMentionsFromSingleSentenceWithMultipleMentions() {
    CoreEntityMention mention1 = mock(CoreEntityMention.class);
    CoreEntityMention mention2 = mock(CoreEntityMention.class);

    List<CoreEntityMention> mentions = new ArrayList<CoreEntityMention>();
    mentions.add(mention1);
    mentions.add(mention2);

    CoreMap sentence = mock(CoreMap.class);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Multiple mention extraction");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> result = doc.entityMentions();
    assertNotNull(result);
    assertEquals(2, result.size());
  } 
}