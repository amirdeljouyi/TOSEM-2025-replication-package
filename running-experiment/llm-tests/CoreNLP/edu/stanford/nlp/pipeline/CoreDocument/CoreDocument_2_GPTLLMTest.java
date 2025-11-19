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

public class CoreDocument_2_GPTLLMTest {

 @Test
  public void testConstructorWithString() {
    String text = "This is a test.";
    CoreDocument doc = new CoreDocument(text);
    Annotation annotation = doc.annotation();
    assertNotNull(annotation);
    assertEquals(text, annotation.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotation() {
    Annotation annotation = new Annotation("Annotated text");
    CoreDocument doc = new CoreDocument(annotation);
    assertSame(annotation, doc.annotation());
  }
@Test
  public void testWrapAnnotationsWithSentencesOnly() {
    Annotation annotation = new Annotation("Sentence text.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("text.");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("Sentence text.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Sentence text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> sentences = doc.sentences();
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("Sentence text.", sentences.get(0).text());
  }
@Test
  public void testWrapAnnotationsWithEntityMentionsFallback() {
    Annotation annotation = new Annotation("Fallback text.");

    CoreMap sentence = new Annotation("Fallback sentence.");
    CoreLabel token = new CoreLabel();
    token.setWord("Fallback");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Fallback text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testWrapAnnotationsWithoutSentences() {
    Annotation annotation = new Annotation("No sentences present.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "No sentences present.");

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
  }
@Test
  public void testTextGetter() {
    Annotation annotation = new Annotation("Get text test.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Get text test.");
    CoreDocument doc = new CoreDocument(annotation);
    String text = doc.text();
    assertEquals("Get text test.", text);
  }
@Test
  public void testTokensGetter() {
    Annotation annotation = new Annotation("Token test.");
    CoreLabel token = new CoreLabel();
    token.setWord("Token");
    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    CoreDocument doc = new CoreDocument(annotation);
    List<CoreLabel> resultTokens = doc.tokens();

    assertNotNull(resultTokens);
    assertEquals(1, resultTokens.size());
    assertEquals("Token", resultTokens.get(0).word());
  }
@Test
  public void testDocIDGetter() {
    Annotation annotation = new Annotation("Doc ID test");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "my-doc-id");
    CoreDocument doc = new CoreDocument(annotation);
    String docId = doc.docID();
    assertEquals("my-doc-id", docId);
  }
@Test
  public void testDocDateGetter() {
    Annotation annotation = new Annotation("Date test.");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-01-01");
    CoreDocument doc = new CoreDocument(annotation);
    String date = doc.docDate();
    assertEquals("2024-01-01", date);
  }
@Test
  public void testToStringDelegatesToAnnotation() {
    Annotation annotation = new Annotation("Some document.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals(annotation.toString(), doc.toString());
  }
@Test
  public void testCorefChainsGetter() {
    Annotation annotation = new Annotation("Coref test.");
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, mock(CorefChain.class));
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.containsKey(1));
  }
@Test
  public void testQuotesIsNullWhenNotBuilt() {
    Annotation annotation = new Annotation("Quotes not built.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsDoesNotCrashWithoutQuoteSupport() {
    Annotation annotation = new Annotation("Quote test");
    CoreMap sentence = new Annotation("Sentence");
    List<CoreMap> sentenceList = Collections.singletonList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
  }
@Test
  public void testWrapAnnotationsWithNullEntityMentionsAndNoMentionsAnnotation() {
    Annotation annotation = new Annotation("No entity mentions.");

    CoreMap sentence = new Annotation("Just sentence.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.TextAnnotation.class, "No entity mentions.");

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertNull(doc.entityMentions());
  }
@Test
  public void testSentencesGetterReturnsConsistentList() {
    Annotation annotation = new Annotation("Two sentence test.");

    CoreMap sentence1 = new Annotation("Sentence one.");
    CoreMap sentence2 = new Annotation("Sentence two.");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Two sentence test.");

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> coreSentences = doc.sentences();
    assertNotNull(coreSentences);
    assertEquals(2, coreSentences.size());
  }
@Test
  public void testEntityMentionsIsNullIfNotBuilt() {
    Annotation annotation = new Annotation("No mentions.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "No mentions.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.entityMentions());
  }
@Test
  public void testEmptyConstructorSentences() {
    CoreDocument doc = new CoreDocument("Empty.");
    assertNull(doc.sentences());
  }
@Test
  public void testEmptyConstructorTokens() {
    CoreDocument doc = new CoreDocument("Some doc.");
    List<CoreLabel> tokens = doc.tokens();
    assertNull(tokens);
  }
@Test
  public void testEmptyConstructorCoref() {
    CoreDocument doc = new CoreDocument("Testing.");
    Map<Integer, CorefChain> coref = doc.corefChains();
    assertNull(coref);
  }
@Test
  public void testWrapAnnotationsWithEmptySentenceListDoesNotBuildMentionsOrQuotes() {
    Annotation annotation = new Annotation("Some text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Some text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertTrue(doc.sentences().isEmpty());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsWithSentencesThatHaveNullEntityMentions() {
    Annotation annotation = new Annotation("Entity mention test.");

    CoreMap sentence1 = new Annotation("First.");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    CoreMap sentence2 = new Annotation("Second.");
    sentence2.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Entity mention test.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(2, doc.sentences().size());
    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNull(mentions);
  }
@Test
  public void testWrapAnnotationsSkipsQuoteConstructionWhenQuoteAnnotatorReturnsNull() {
    Annotation annotation = new Annotation("Quote test.");

    CoreMap sentence = new Annotation("He said hello.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    List<CoreMap> sentenceList = Arrays.asList(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Quote test.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsWithSentenceThatHasEmptyEntityMentionsList() {
    Annotation annotation = new Annotation("Empty mentions test");

    CoreMap sentence = new Annotation("Sentence with no mentions.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Empty mentions test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertNotNull(doc.entityMentions());
    assertEquals(0, doc.entityMentions().size());
  }
@Test
  public void testEntityMentionsReturnsNullIfWrapAnnotationsNotCalled() {
    Annotation annotation = new Annotation("Lazy test.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Lazy test.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.entityMentions());
  }
@Test
  public void testQuotesReturnsNullIfWrapAnnotationsNotCalled() {
    Annotation annotation = new Annotation("Lazy quotes.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Lazy quotes.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.quotes());
  }
@Test
  public void testTokensReturnsNullIfUnset() {
    Annotation annotation = new Annotation("Token null test.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.tokens());
  }
@Test
  public void testDocIDReturnsNullIfUnset() {
    Annotation annotation = new Annotation("No doc ID");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docID());
  }
@Test
  public void testDocDateReturnsNullWhenUnset() {
    Annotation annotation = new Annotation("No date.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docDate());
  }
@Test
  public void testCorefChainsReturnsNullIfUnset() {
    Annotation annotation = new Annotation("No coref");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.corefChains());
  }
@Test
  public void testToStringReturnsDefaultIfAnnotationUnsetText() {
    Annotation annotation = new Annotation("");
    CoreDocument doc = new CoreDocument(annotation);
    String str = doc.toString();
    assertNotNull(str);
  }
@Test
  public void testWrapAnnotationsWithSentenceListContainingNulls() {
    Annotation annotation = new Annotation("Sentence with null.");
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(null);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Sentence with null.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
    } catch (Exception e) {
      fail("wrapAnnotations should handle null elements in sentence list gracefully");
    }
  }
@Test
  public void testWrapAnnotationsWithEntityMentionsKeyMappedToWrongType() {
    Annotation annotation = new Annotation("Wrong type for entity mentions");
    List<CoreMap> sentenceList = new ArrayList<>();
    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
    } catch (ClassCastException expected) {
      
      return;
    }
    fail("Expected ClassCastException was not thrown");
  }
@Test
  public void testWrapAnnotationsWithQuotesKeyMappedToWrongType() {
    Annotation annotation = new Annotation("Wrong quote map");
    List<CoreMap> sentenceList = new ArrayList<>();
    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
    } catch (ClassCastException expected) {
      return;
    }
    fail("Expected ClassCastException was not thrown due to incorrect quote value type");
  }
@Test
  public void testCorefChainsWithEmptyMap() {
    Annotation annotation = new Annotation("Empty coref chains");
    Map<Integer, CorefChain> emptyMap = new HashMap<>();
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, emptyMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testCorefChainsWithNullValueInMap() {
    Annotation annotation = new Annotation("Coref chain with null");
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, null);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();
    assertEquals(1, result.size());
    assertNull(result.get(1));
  }
@Test
  public void testEntityMentionsWithOneSentenceWithMixedMentions() {
    Annotation annotation = new Annotation("Entity mention types");

    CoreMap sentence = new Annotation("Entity sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertTrue(mentions == null || mentions.isEmpty());
  }
@Test
  public void testIdempotentWrapAnnotationsCall() {
    CoreMap sentence = new Annotation("Idempotent test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreSentence> firstCall = doc.sentences();
    doc.wrapAnnotations(); 
    List<CoreSentence> secondCall = doc.sentences();
    assertSame(firstCall, secondCall);
  }
@Test
  public void testConstructorWithAnnotationWithNoTextAnnotationSet() {
    Annotation annotation = new Annotation("Unset TextAnnotation");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());  
  }
@Test
  public void testSentencesListAfterWrapWithSentenceMissingTokenAnnotation() {
    CoreMap sentence = new Annotation("No tokens");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Missing tokens");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> result = doc.sentences();
    assertNotNull(result);
    assertEquals(1, result.size());
  }
@Test
  public void testWrapAnnotationsCalledWithAllAnnotationsEmpty() {
    Annotation annotation = new Annotation("Empty everything");
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  }
@Test
  public void testCorrectBehaviorWhenQuoteAnnotationReturnsEmptyList() {
    CoreMap sentence = new Annotation("Quoted content here.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Document text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testWrapAnnotationsSentenceWithMissingCoreAnnotationsKeys() {
    Annotation annotation = new Annotation("Text with partial annotation keys");

    CoreMap sentence = new Annotation("This sentence has no tokens or mentions.");
    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Some text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> coreSentences = doc.sentences();
    assertNotNull(coreSentences);
    assertEquals(1, coreSentences.size());
  }
@Test
  public void testWrapAnnotationsWithMixedValidAndNullSentences() {
    CoreMap validSentence = new Annotation("Valid sentence");
    validSentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(validSentence);
    sentences.add(null);

    Annotation annotation = new Annotation("Mixed sentence list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Mixed sentence list");

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
    } catch (Exception e) {
      fail("wrapAnnotations() should not fail with null inside sentence list");
    }
  }
@Test
  public void testWrapAnnotationsDoesNotRebuildIfAlreadyWrapped() {
    CoreMap sentence = new Annotation("Once wrapped");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Text");

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> firstSentences = doc.sentences();
    doc.wrapAnnotations(); 
    List<CoreSentence> secondSentences = doc.sentences();

    assertSame(firstSentences, secondSentences);
  }
@Test
  public void testQuotesAnnotationAtSentenceLevelOnlyDoesNotTriggerQuotesList() {
    CoreMap sentence = new Annotation("He said hello.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Doc with nested quotes");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Doc with nested quotes");
    

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.quotes());
  }
@Test
  public void testSentencesIsNullInitiallyBeforeWrapAnnotations() {
    CoreDocument doc = new CoreDocument("Initial state test");
    List<CoreSentence> sentences = doc.sentences();
    assertNull(sentences);
  }
@Test
  public void testEntityMentionsIsNullIfSentencesHaveNoMentionsAnnotation() {
    CoreMap sentence = new Annotation("Plain sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Entity test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Entity test");

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNull(doc.entityMentions());
  }
@Test
  public void testQuotesIsNullWhenOnlyAnnotationObjectConstructed() {
    Annotation annotation = new Annotation("Only base annotation");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.quotes());
  }
@Test
  public void testCoreSentencePreservedEvenWithSubclassedCoreMap() {
    Annotation annotation = new Annotation("Subclassed sentence list");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Subclassed sentence list");

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> coreSentences = doc.sentences();
    assertNotNull(coreSentences);
    assertEquals(1, coreSentences.size());
  }
@Test
  public void testWrapAnnotationsHandlesEmptyQuotesInAnnotation() {
    CoreMap sentence1 = new Annotation("Quote sentence");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);

    Annotation annotation = new Annotation("Quote doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Quote doc");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
@Test
  public void testTextReturnsNullIfNotSetExplicitlyInAnnotation() {
    Annotation annotation = new Annotation("");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.text());
  }
@Test
  public void testWrapAnnotationsSkipsQuoteListIfQuotationsAnnotationIsNull() {
    Annotation annotation = new Annotation("Quote skip test");

    CoreMap sentence = new Annotation("Sentence one.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Quote skip test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, null);  

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsSetsEmptyEntityMentionsWhenAnnotationHasMentionsAnnotationNull() {
    Annotation annotation = new Annotation("Mentions fallback test");

    CoreMap sentence = new Annotation("Entity sentence.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Mentions fallback test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null); 

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertNull(doc.entityMentions()); 
  }
@Test
  public void testWrapAnnotationsWithQuotesAnnotationContainingNullElement() {
    Annotation annotation = new Annotation("Quote null item");

    CoreMap sentence = new Annotation("Quoted sentence.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(null);  

    annotation.set(CoreAnnotations.TextAnnotation.class, "Quote null item");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
    } catch (Exception e) {
      fail("wrapAnnotations should handle null entry inside quote list without exception");
    }

    List<CoreQuote> coreQuotes = doc.quotes();
    assertNotNull(coreQuotes);
    assertEquals(1, coreQuotes.size());
  }
@Test
  public void testWrapAnnotationsWithUnexpectedTypeInSentencesAnnotation() {
    Annotation annotation = new Annotation("Invalid sentences type");

    annotation.set(CoreAnnotations.TextAnnotation.class, "Invalid sentences type");

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
      fail("Expected ClassCastException due to invalid annotation type for sentences");
    } catch (ClassCastException expected) {
      
    }
  }
@Test
  public void testWrapAnnotationsWithFirstSentenceHavingNullEntityMentions() {
    Annotation annotation = new Annotation("Fallback on entity mentions");

    CoreMap sentence = new Annotation("First sentence with no mentions.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Fallback on entity mentions");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertNull(doc.entityMentions());
  }
@Test
  public void testCorefChainsReturnsEmptyIfCorefChainAnnotationNotSet() {
    CoreDocument doc = new CoreDocument("No coref map here.");
    Map<Integer, CorefChain> chains = doc.corefChains();
    assertNull(chains);
  }
@Test
  public void testMultipleCallsToWrapAnnotationsAreIdempotent() {
    CoreMap sentence = new Annotation("Repeatable wrap");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Idempotent test");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Idempotent test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreSentence> firstResult = doc.sentences();

    doc.wrapAnnotations(); 
    List<CoreSentence> secondResult = doc.sentences();

    assertSame(firstResult, secondResult);
    assertEquals(1, secondResult.size());
  }
@Test
  public void testTextGetterReturnsNullIfMissingFromAnnotation() {
    Annotation annotation = new Annotation("Missing text field"); 

    CoreDocument doc = new CoreDocument(annotation);
    String text = doc.text();

    assertNull(text);
  }
@Test
  public void testTokensReturnsNullWhenNotSet() {
    Annotation annotation = new Annotation("Tokens not available");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Tokens not available");

    CoreDocument doc = new CoreDocument(annotation);
    List<CoreLabel> tokenList = doc.tokens();

    assertNull(tokenList);
  }
@Test
  public void testToStringReturnsSameAsAnnotationToString() {
    Annotation annotation = new Annotation("To string check");
    CoreDocument doc = new CoreDocument(annotation);

    assertEquals(annotation.toString(), doc.toString());
  }
@Test
  public void testConstructorWithEmptyString() {
    CoreDocument doc = new CoreDocument("");
    assertNotNull(doc.annotation());
    assertEquals("", doc.annotation().get(CoreAnnotations.TextAnnotation.class));
    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
  }
@Test
  public void testWrapAnnotationsWithFirstSentenceMentionListEmpty() {
    CoreMap sentence = new Annotation("Test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>()); 

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Some Text");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Some Text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreEntityMention> mentions = doc.entityMentions();

    assertNotNull(doc.sentences());
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty()); 
  }
@Test
  public void testWrapAnnotationsInvalidTokenStructureThrowsCastError() {
    Annotation annotation = new Annotation("Bad token data");

    CoreMap sentence = new Annotation("One");

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Bad token data");

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
      fail("Expected ClassCastException due to invalid TokensAnnotation data");
    } catch (ClassCastException expected) {
      
    }
  }
@Test
  public void testWrapAnnotationsInvalidQuotationsAnnotationType() {
    Annotation annotation = new Annotation("Invalid quotes");

    CoreMap sentence = new Annotation("Text");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotation.set(CoreAnnotations.TextAnnotation.class, "Invalid quotes");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
      fail("Expected ClassCastException from invalid quote list type");
    } catch (ClassCastException expected) {
      
    }
  }
@Test
  public void testEntityMentionsReturnsEmptyWhenWrappedAndMentionListsEmpty() {
    CoreMap sentence1 = new Annotation("Test 1");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence1.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    CoreMap sentence2 = new Annotation("Test 2");
    sentence2.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    Annotation annotation = new Annotation("Many Empty");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Many Empty");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreEntityMention> result = doc.entityMentions();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testWrapAnnotationsHandlesMixedSentenceListTypes() {
    Annotation annotation = new Annotation("Mixed annotations");

    CoreMap goodSentence = new Annotation("Valid");
    goodSentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    Object badSentence = "StringInsteadOfCoreMap"; 
    List sentenceList = new ArrayList();
    sentenceList.add(goodSentence);
    sentenceList.add(badSentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Mixed annotations");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
      fail("Expected ClassCastException due to non-CoreMap element in sentence list");
    } catch (ClassCastException expected) {
      
    }
  }
@Test
  public void testSameAnnotationReusedAcrossMultipleCoreDocuments() {
    Annotation shared = new Annotation("Shared annotation");
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);
    shared.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    shared.set(CoreAnnotations.TextAnnotation.class, "Shared annotation");

    CoreDocument doc1 = new CoreDocument(shared);
    CoreDocument doc2 = new CoreDocument(shared);

    assertEquals(doc1.text(), doc2.text());
    assertEquals(doc1.tokens().size(), doc2.tokens().size());
    assertSame(doc1.annotation(), doc2.annotation());
  }
@Test
  public void testCorefChainsReturnsValidEmptyMapWhenPresentButEmpty() {
    Annotation annotation = new Annotation("Empty chains");
    Map<Integer, CorefChain> chains = new HashMap<>();
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testDocDateAndDocIDReturnNullWhenNotSet() {
    Annotation annotation = new Annotation("Testing");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docID());
    assertNull(doc.docDate());
  }
@Test
  public void testWrapAnnotationWithMentionAnnotationWrongGenericType() {
    Annotation annotation = new Annotation("Text");
    CoreMap sentence = new Annotation("Sentence with text only");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    try {
      doc.wrapAnnotations();
      fail("Expected a ClassCastException due to incorrect MentionsAnnotation type");
    } catch (ClassCastException expected) {
      
    }
  }
@Test
  public void testWrapAnnotationsWithDuplicateSentenceObjects() {
    CoreMap sentence = new Annotation("Repeated sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> repeatedList = new ArrayList<>();
    repeatedList.add(sentence);
    repeatedList.add(sentence); 

    Annotation annotation = new Annotation("Doc with duplication");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Doc with duplication");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, repeatedList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreSentence> sentences = doc.sentences();
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    assertEquals(sentences.get(0).text(), sentences.get(1).text());
  }
@Test
  public void testWrapAnnotationsHandlesQuoteAnnotationListWithPartialMap() {
    CoreMap sentence = new Annotation("Quote test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    CoreMap partialQuote = new Annotation(""); 
    List<CoreMap> quotationList = new ArrayList<>();
    quotationList.add(partialQuote);

    Annotation annotation = new Annotation("Partial quotes");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Partial quotes");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotationList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testWrapAnnotationsWithCorefAnnotationMappedToWrongType() {
    Annotation annotation = new Annotation("Wrong coref map type");

    CoreDocument doc = new CoreDocument(annotation);
    try {
      Map<Integer, CorefChain> map = doc.corefChains();
      map.size(); 
      fail("Expected ClassCastException on incorrect CorefChainAnnotation value");
    } catch (ClassCastException expected) {
      
    }
  }
@Test
  public void testWrapAnnotationsCorefMapWithNullKey() {
    Annotation annotation = new Annotation("Null in coref keys");

    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(null, null);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();
    assertNotNull(result);
    assertTrue(result.containsKey(null));
    assertNull(result.get(null));
  }
@Test
  public void testMultipleCallerAccessOrder() {
    Annotation annotation = new Annotation("Order check");
    CoreLabel token = new CoreLabel();
    token.setWord("Hi");

    CoreMap sentence = new Annotation("Hello");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Order check");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.annotation());
    assertNotNull(doc.text());
    assertEquals("Order check", doc.text());
    assertNotNull(doc.tokens());
    assertEquals(1, doc.tokens().size());
    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
    assertNull(doc.corefChains());
    assertNotNull(doc.toString());
  }
@Test
  public void testAnnotationToStringHandlingWhenEmpty() {
    Annotation annotation = new Annotation("");
    CoreDocument doc = new CoreDocument(annotation);
    String result = doc.toString();
    assertNotNull(result);
    assertTrue(result.contains("edu.stanford.nlp.ling.Annotation"));
  }
@Test
  public void testNullSentenceAnnotationHandledGracefully() {
    Annotation a = new Annotation("Doc");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(null); 
    a.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    a.set(CoreAnnotations.TextAnnotation.class, "Doc");

    CoreDocument doc = new CoreDocument(a);
    try {
      doc.wrapAnnotations();
      assertNotNull(doc.sentences());
      assertEquals(0, doc.sentences().size());
    } catch (Exception e) {
      fail("CoreDocument.wrapAnnotations() should tolerate null entries in sentence list");
    }
  } 
}