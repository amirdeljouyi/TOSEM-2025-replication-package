package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoreDocument_4_GPTLLMTest {

 @Test
  public void testConstructorWithTextInitializesAnnotation() {
    String text = "John went to school.";
    CoreDocument document = new CoreDocument(text);

    Annotation annotation = document.annotation();
    assertNotNull(annotation);
    
    
  }
@Test
  public void testConstructorWithAnnotationStoresReference() {
    Annotation annotation = new Annotation("Test text");
    CoreDocument document = new CoreDocument(annotation);
    assertSame(annotation, document.annotation());
  }
@Test
  public void testAnnotationReturnsUnderlyingAnnotation() {
    Annotation annotation = new Annotation("Another text");
    CoreDocument document = new CoreDocument(annotation);
    assertSame(annotation, document.annotation());
  }
@Test
  public void testDocIDReturnsValueWhenSet() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.DocIDAnnotation.class)).thenReturn("doc123");

    CoreDocument document = new CoreDocument(annotation);
    String docId = document.docID();

    assertEquals("doc123", docId);
  }
@Test
  public void testDocIDReturnsNullWhenNotSet() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.DocIDAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    String docId = document.docID();

    assertNull(docId);
  }
@Test
  public void testDocDateReturnsValueWhenSet() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.DocDateAnnotation.class)).thenReturn("2024-03-24");

    CoreDocument document = new CoreDocument(annotation);
    String docDate = document.docDate();

    assertEquals("2024-03-24", docDate);
  }
@Test
  public void testDocDateReturnsNullWhenNotSet() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.DocDateAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    String docDate = document.docDate();

    assertNull(docDate);
  }
@Test
  public void testTextReturnsSetValue() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TextAnnotation.class)).thenReturn("This is a test.");

    CoreDocument document = new CoreDocument(annotation);
    String text = document.text();

    assertEquals("This is a test.", text);
  }
@Test
  public void testTokensReturnsTokenList() {
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();
    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    CoreDocument document = new CoreDocument(annotation);
    List<CoreLabel> result = document.tokens();

    assertEquals(2, result.size());
    assertSame(token1, result.get(0));
    assertSame(token2, result.get(1));
  }
@Test
  public void testCorefChainsReturnsMap() {
    CorefChain chain = mock(CorefChain.class);
    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(1, chain);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(chains);

    CoreDocument document = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = document.corefChains();

    assertEquals(1, result.size());
    assertSame(chain, result.get(1));
  }
@Test
  public void testEntityMentionsReturnsNullWhenNotWrapped() {
    Annotation annotation = new Annotation("No entities.");
    CoreDocument document = new CoreDocument(annotation);
    List<CoreEntityMention> mentions = document.entityMentions();
    assertNull(mentions);
  }
@Test
  public void testQuotesReturnsNullWhenNotWrapped() {
    Annotation annotation = new Annotation("No quotes.");
    CoreDocument document = new CoreDocument(annotation);
    List<CoreQuote> quotes = document.quotes();
    assertNull(quotes);
  }
@Test
  public void testToStringDelegatesToAnnotation() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.toString()).thenReturn("Annotation#toString");

    CoreDocument document = new CoreDocument(annotation);
    String result = document.toString();

    assertEquals("Annotation#toString", result);
  }
@Test
  public void testWrapAnnotationsWithNoSentences() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNull(document.sentences());
    assertNull(document.entityMentions());
    assertNull(document.quotes());
  }
@Test
  public void testWrapAnnotationsWithQuoteOnly() {
    CoreMap quoteMap = mock(CoreMap.class);
    List<CoreMap> quotesList = Collections.singletonList(quoteMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(quotesList);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    List<CoreQuote> quotes = document.quotes();
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testWrapAnnotationsWithMentionsAnnotationButNoSentences() {
    List<CoreMap> mentions = new ArrayList<>();

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
    when(annotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    List<CoreEntityMention> result = document.entityMentions();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testWrapAnnotationsWithSentencesAndEntityMentions() {
    CoreEntityMention mention = mock(CoreEntityMention.class);
    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(Collections.singletonList(mention));

    CoreMap map = mock(CoreMap.class);
    List<CoreMap> sentenceMaps = Collections.singletonList(map);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceMaps);
  }
@Test
  public void testWrapAnnotationsWithEntityMentionsNullReturnsEmptyList() {
    CoreSentence mockSentence = mock(CoreSentence.class);
    when(mockSentence.entityMentions()).thenReturn(null);

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> maps = new ArrayList<>();
    maps.add(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(maps);
  }
@Test
  public void testWrapAnnotationsWithNullQuoteListSkipsQuotes() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());

    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNull(quotes);
  }
@Test
  public void testWrapAnnotationsQuoteListEmptyResultsInEmptyQuotes() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> emptyQuotes = new ArrayList<>();
    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(emptyQuotes);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testBuildDocumentEntityMentionsListHandlesMultipleSentences() {
    CoreEntityMention mention1 = mock(CoreEntityMention.class);
    CoreEntityMention mention2 = mock(CoreEntityMention.class);

    CoreSentence sentence1 = mock(CoreSentence.class);
    CoreSentence sentence2 = mock(CoreSentence.class);
    when(sentence1.entityMentions()).thenReturn(Collections.singletonList(mention1));
    when(sentence2.entityMentions()).thenReturn(Collections.singletonList(mention2));

    CoreMap sentMap1 = mock(CoreMap.class);
    CoreMap sentMap2 = mock(CoreMap.class);
    List<CoreMap> sentenceMaps = Arrays.asList(sentMap1, sentMap2);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceMaps);
  }
@Test
  public void testSentencesReturnsNullWhenWrapNotCalled() {
    Annotation annotation = mock(Annotation.class);
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.sentences());
  }
@Test
  public void testTokensReturnsNullIfNotPresentInAnnotation() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.tokens());
  }
@Test
  public void testWrapAnnotationsWithEmptyEntityMentionsInSentences() {
    CoreSentence sentence1 = mock(CoreSentence.class);
    when(sentence1.entityMentions()).thenReturn(Collections.emptyList());

    CoreMap sentMap = mock(CoreMap.class);
    List<CoreMap> sentenceMaps = Collections.singletonList(sentMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceMaps);
  }
@Test
  public void testWrapAnnotationsDoesNotBuildEntitiesIfFirstSentenceIsNullOrEmpty() {
    CoreSentence sentence1 = mock(CoreSentence.class);
    when(sentence1.entityMentions()).thenReturn(null);

    CoreMap map = mock(CoreMap.class);
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.singletonList(map));
  }
@Test
  public void testQuoteListIsNullWhenGatherQuotesReturnsNull() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());

    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNull(quotes);
  }
@Test
  public void testWrapAnnotationsWithSentencesButFirstSentenceNull() {
    CoreSentence nullSentence = null;

    CoreMap mockCoreMap = mock(CoreMap.class);
    List<CoreMap> coreMapList = Collections.singletonList(mockCoreMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(coreMapList);
  }
@Test
  public void testWrapAnnotationsWithFirstSentenceEntityMentionsNullAndMentionsAnnotationAbsent() {
    CoreSentence mockSentence = mock(CoreSentence.class);
    when(mockSentence.entityMentions()).thenReturn(null);

    CoreMap mockCoreMap = mock(CoreMap.class);
    List<CoreMap> coreMapList = Collections.singletonList(mockCoreMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(coreMapList);
    when(annotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
  }
@Test
  public void testWrapAnnotationsWithFirstSentenceEntityMentionsNullButMentionsAnnotationPresent() {
    CoreSentence mockSentence = mock(CoreSentence.class);
    when(mockSentence.entityMentions()).thenReturn(null);

    CoreMap mockCoreMap = mock(CoreMap.class);
    List<CoreMap> coreMapList = Collections.singletonList(mockCoreMap);
    List<CoreMap> mentionsAnnotation = Collections.emptyList();

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(coreMapList);
    when(annotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentionsAnnotation);
  }
@Test
  public void testQuotesDoesNotThrowWhenQuoteAnnotatorReturnsEmpty() {
    List<CoreMap> emptyQuotes = Collections.emptyList();

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());

    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(emptyQuotes);
  }
@Test
  public void testWrapSentencesAssignsCoreSentencesWithNullEntityMentions() {
    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> coreMapSentences = Collections.singletonList(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(coreMapSentences);
  }
@Test
  public void testWrapAnnotationsSkipsEntityMentionsWhenSentencesIsEmpty() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
  }
@Test
  public void testToStringReturnsActualAnnotationToStringEvenIfEmpty() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.toString()).thenReturn("");

    CoreDocument document = new CoreDocument(annotation);
    String result = document.toString();

    assertEquals("", result);
  }
@Test
  public void testCorefChainsReturnsNullWhenUnset() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    Map<Integer, CorefChain> chains = document.corefChains();

    assertNull(chains);
  }
@Test
  public void testEntityMentionsReturnsNonNullAfterManualAnnotationWrap() {
    Annotation annotation = new Annotation("Barack Obama was born in Hawaii.");

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations(); 

    List<CoreEntityMention> mentions = document.entityMentions();
    assertNull(mentions); 
  }
@Test
  public void testTextReturnsNullWhenTextAnnotationIsMissing() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    String result = document.text();

    assertNull(result);
  }
@Test
  public void testToStringReturnsNullWhenAnnotationToStringIsNull() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.toString()).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    String result = document.toString();

    assertNull(result);
  }
@Test
  public void testTokensReturnsNullWhenTokensAnnotationMissing() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    List<CoreLabel> tokens = document.tokens();

    assertNull(tokens);
  }
@Test
  public void testWrapAnnotationsHandlesNullSentenceInList() {
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(null);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceList);


  }
@Test
  public void testWrapAnnotationsHandlesEntityMentionThrowsException() {
    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenThrow(new RuntimeException("entity mention failed"));

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> coreMapList = Collections.singletonList(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(coreMapList);
  }
@Test
  public void testWrapAnnotationsSetsSentencesEvenIfEntityMentionsNull() {
    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(null);

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentences = Collections.singletonList(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentences);
  }
@Test
  public void testCorefChainsReturnsEmptyMapWhenEmptyResult() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(new HashMap<>());

    CoreDocument document = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = document.corefChains();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testWrapAnnotationsHandlesQuoteAnnotatorReturnsNullWithSentencesPresent() {
    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentences = Collections.singletonList(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentences);

    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

    CoreSentence dummySentence = mock(CoreSentence.class);
    when(dummySentence.entityMentions()).thenReturn(null);
  }
@Test
  public void testEntityMentionsHandlesEmptyListSafely() {
    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(Collections.emptyList());

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> coreMapList = Collections.singletonList(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(coreMapList);
  }
@Test
  public void testSentencesIsEmptyWhenAnnotationReturnsEmptyList() {
    List<CoreMap> sentenceMaps = new ArrayList<>();

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceMaps);
  }
@Test
  public void testWrapAnnotationsEntityMentionFirstSentenceEmptySecondNonEmpty() {
    CoreEntityMention mention = mock(CoreEntityMention.class);

    CoreSentence sentence1 = mock(CoreSentence.class);
    CoreSentence sentence2 = mock(CoreSentence.class);
    when(sentence1.entityMentions()).thenReturn(Collections.emptyList());
    when(sentence2.entityMentions()).thenReturn(Collections.singletonList(mention));

    CoreMap map1 = mock(CoreMap.class);
    List<CoreMap> mapList = Collections.singletonList(map1);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(mapList);
  }
@Test
  public void testWrapAnnotationsWithQuotesOnlyEmptyListReturned() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());

    List<CoreMap> emptyQuotes = Collections.emptyList();
    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(emptyQuotes);
  }
@Test
  public void testDocIDAndDocDateReturnNullExplicitly() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.DocIDAnnotation.class)).thenReturn(null);
    when(annotation.get(CoreAnnotations.DocDateAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.docID());
    assertNull(document.docDate());
  }
@Test
  public void testWrapSentencesHandlesNullCoreMapGracefully() {
    CoreMap nullMap = null;
    List<CoreMap> maps = Collections.singletonList(nullMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(maps);
  }
@Test
  public void testTextAnnotationReturnsEmptyString() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

    CoreDocument document = new CoreDocument(annotation);
    String text = document.text();
    assertEquals("", text);
  }
@Test
  public void testAnnotationReturnsNullSentenceAnnotation() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    document.wrapAnnotations();

    assertNull(document.sentences());
    assertNull(document.entityMentions());
  }
@Test
  public void testBuildEntityMentionsListWhenAllSentencesReturnEmptyEntities() {
    CoreSentence sentenceA = mock(CoreSentence.class);
    CoreSentence sentenceB = mock(CoreSentence.class);

    when(sentenceA.entityMentions()).thenReturn(Collections.emptyList());
    when(sentenceB.entityMentions()).thenReturn(Collections.emptyList());

    CoreMap mapA = mock(CoreMap.class);
    List<CoreMap> maps = Collections.singletonList(mapA);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(maps);
  }
@Test
  public void testCorefChainsReturnsNullCorefAnnotation() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    Map<Integer, CorefChain> map = document.corefChains();
    assertNull(map);
  }
@Test
  public void testTokensAnnotationReturnsEmptyList() {
    Annotation annotation = mock(Annotation.class);
    List<CoreLabel> empty = Collections.emptyList();
    when(annotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(empty);

    CoreDocument document = new CoreDocument(annotation);
    List<CoreLabel> tokens = document.tokens();

    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testAnnotationWithNullToStringHandlingGracefully() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.toString()).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    assertNull(document.toString());
  }
@Test
  public void testConstructorWithEmptyAnnotation() {
    Annotation annotation = new Annotation("");
    CoreDocument document = new CoreDocument(annotation);
    assertNotNull(document.annotation());
  }
@Test
  public void testSentenceCoreMapListContainsNullEntries() {
    Annotation annotation = mock(Annotation.class);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(null); 

    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentences);

    CoreDocument document = new CoreDocument(annotation);
    try {
      document.wrapAnnotations();
      assertNotNull(document.sentences());
    } catch (Exception e) {
      fail("Exception should not be thrown when wrapping sentences with null CoreMap");
    }
  }
@Test
  public void testWrapAnnotationsWithoutEntityMentionsAndMentionsAnnotation() {
    CoreSentence mockSentence = mock(CoreSentence.class);
    when(mockSentence.entityMentions()).thenReturn(null);

    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentenceMaps = Collections.singletonList(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceMaps);
    when(annotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
  }
@Test
  public void testWrapAnnotationsSentenceEntityMentionsReturnsEmptyButMentionsAnnotationSet() {
    CoreSentence mockSentence = mock(CoreSentence.class);
    when(mockSentence.entityMentions()).thenReturn(null);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.singletonList(mock(CoreMap.class)));
    when(annotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
  }
@Test
  public void testWrapAnnotationsHandlesNullQuotesGracefully() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.singletonList(mock(CoreMap.class)));

    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(null);
  }
@Test
  public void testWrapAnnotationsWithNullEntityMentionsNoMentionsAnnotationNoQuotes() {
    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(null);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.singletonList(mock(CoreMap.class)));
    when(annotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);
  }
@Test
  public void testTokensAnnotationReturnsNull() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    List<CoreLabel> result = document.tokens();
    assertNull(result);
  }
@Test
  public void testQuotesReturnsEmptyIfBuildQuotesCreatesEmpty() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.singletonList(mock(CoreMap.class)));

    List<CoreMap> emptyQuotes = Collections.emptyList();
    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(emptyQuotes);

    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(null);
  }
@Test
  public void testToStringReturnsDefaultAnnotationString() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.toString()).thenReturn("CoreDocument#AnnotationToString");

    CoreDocument document = new CoreDocument(annotation);
    String result = document.toString();
    assertEquals("CoreDocument#AnnotationToString", result);
  }
@Test
  public void testDocIDReturnsExplicitlySetValue() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.DocIDAnnotation.class)).thenReturn("unit-test-doc-id");

    CoreDocument document = new CoreDocument(annotation);
    String result = document.docID();
    assertEquals("unit-test-doc-id", result);
  }
@Test
  public void testDocDateReturnsExplicitlySetValue() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.DocDateAnnotation.class)).thenReturn("2024-06-01");

    CoreDocument document = new CoreDocument(annotation);
    String result = document.docDate();
    assertEquals("2024-06-01", result);
  }
@Test
  public void testWrapAnnotationsWithNonEmptySentenceButEntityMentionsReturnsNullAndMentionsAnnotationNull() {
    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(null);

    Annotation annotation = mock(Annotation.class);
    List<CoreMap> sentenceMaps = Collections.singletonList(mock(CoreMap.class));
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceMaps);
    when(annotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
  }
@Test
  public void testWrapAnnotationsSkipsQuoteExtractionWhenGatherQuotesReturnsNullAndTextExists() {
    CoreMap sentenceMap = mock(CoreMap.class);
    List<CoreMap> sentenceList = Collections.singletonList(sentenceMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Sample text.");
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceList);

    Mockito.mockStatic(QuoteAnnotator.class).when(() -> QuoteAnnotator.gatherQuotes(annotation)).thenReturn(null);

    CoreSentence sentence = mock(CoreSentence.class);
    when(sentence.entityMentions()).thenReturn(null);

  }
@Test
  public void testCorefChainsReturnsNonEmptyMap() {
    CorefChain mockChain = mock(CorefChain.class);
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, mockChain);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(corefMap);

    CoreDocument document = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = document.corefChains();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertSame(mockChain, result.get(1));
  }
@Test
  public void testWrapSentencesCreatesCoreSentenceInstancesWithNullEntityMentionsSilently() {
    CoreMap sentMap = mock(CoreMap.class);
    List<CoreMap> coreMapList = new ArrayList<>();
    coreMapList.add(sentMap);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(coreMapList);
  }
@Test
  public void testWrapAnnotationsWithPartialSentenceInitializationAndEntityMentionsExtractionFailsGracefully() {
    CoreSentence sentence1 = mock(CoreSentence.class);
    CoreSentence sentence2 = mock(CoreSentence.class);

    when(sentence1.entityMentions()).thenReturn(null);
    when(sentence2.entityMentions()).thenThrow(new RuntimeException("Unexpected error"));

    CoreMap map1 = mock(CoreMap.class);
    CoreMap map2 = mock(CoreMap.class);
    List<CoreMap> sentenceMaps = Arrays.asList(map1, map2);

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentenceMaps);
  }
@Test
  public void testConstructorWithEmptyStringAnnotationYieldsAnnotationWithNoText() {
    CoreDocument document = new CoreDocument("");
    String text = document.text(); 
    assertNull(text); 
  }
@Test
  public void testTokensReturnsNullWhenFieldAbsent() {
    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    CoreDocument document = new CoreDocument(annotation);
    List<CoreLabel> result = document.tokens();
    assertNull(result);
  }
@Test
  public void testEntityMentionsReturnsNullBeforeWrapAnnotationsCalled() {
    Annotation annotation = mock(Annotation.class);
    CoreDocument document = new CoreDocument(annotation);
    List<CoreEntityMention> mentions = document.entityMentions();
    assertNull(mentions);
  }
@Test
  public void testSentencesReturnsNullBeforeWrapAnnotationsCalled() {
    Annotation annotation = mock(Annotation.class);
    CoreDocument document = new CoreDocument(annotation);
    List<CoreSentence> result = document.sentences();
    assertNull(result);
  } 
}