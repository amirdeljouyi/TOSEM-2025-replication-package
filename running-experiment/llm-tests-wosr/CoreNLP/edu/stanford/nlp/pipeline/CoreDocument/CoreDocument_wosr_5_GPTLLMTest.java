public class CoreDocument_wosr_5_GPTLLMTest { 

 @Test
  public void testConstructorWithText() {
    CoreDocument doc = new CoreDocument("This is a test.");
    assertNotNull(doc);
    Annotation annotation = doc.annotation();
    assertNotNull(annotation);
    assertEquals("This is a test.", annotation.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotation() {
    Annotation ann = new Annotation("Sample text");
    ann.set(CoreAnnotations.TextAnnotation.class, "Sample text");
    CoreDocument doc = new CoreDocument(ann);
    assertNotNull(doc);
    assertEquals("Sample text", doc.text());
  }
@Test
  public void testAnnotationAccess() {
    Annotation ann = new Annotation("Another test");
    CoreDocument doc = new CoreDocument(ann);
    assertSame(ann, doc.annotation());
  }
@Test
  public void testDocIDAnnotation() {
    Annotation ann = new Annotation("Document ID test");
    ann.set(CoreAnnotations.DocIDAnnotation.class, "test-doc-id");
    CoreDocument doc = new CoreDocument(ann);
    assertEquals("test-doc-id", doc.docID());
  }
@Test
  public void testDocDateAnnotation() {
    Annotation ann = new Annotation("Document Date test");
    ann.set(CoreAnnotations.DocDateAnnotation.class, "2024-01-01");
    CoreDocument doc = new CoreDocument(ann);
    assertEquals("2024-01-01", doc.docDate());
  }
@Test
  public void testTextAnnotation() {
    Annotation ann = new Annotation("Text content test");
    ann.set(CoreAnnotations.TextAnnotation.class, "Text content test");
    CoreDocument doc = new CoreDocument(ann);
    assertEquals("Text content test", doc.text());
  }
@Test
  public void testTokensAnnotation() {
    Annotation ann = new Annotation("Token test");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel label1 = new CoreLabel();
    label1.setWord("Token");
    tokens.add(label1);
    CoreLabel label2 = new CoreLabel();
    label2.setWord("test");
    tokens.add(label2);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument(ann);
    List<CoreLabel> returnedTokens = doc.tokens();
    assertNotNull(returnedTokens);
    assertEquals(2, returnedTokens.size());
    assertEquals("Token", returnedTokens.get(0).word());
    assertEquals("test", returnedTokens.get(1).word());
  }
@Test
  public void testSentencesAnnotationCreatesSentenceObjects() {
    CoreMap sentence1 = new Annotation("Sentence one");
    CoreMap sentence2 = new Annotation("Sentence two");

    List<CoreMap> sentenceMaps = new ArrayList<>();
    sentenceMaps.add(sentence1);
    sentenceMaps.add(sentence2);

    Annotation ann = new Annotation("Test sentences");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceMaps);

    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    List<CoreSentence> coreSentences = doc.sentences();
    assertNotNull(coreSentences);
    assertEquals(2, coreSentences.size());
  }
@Test
  public void testEntityMentionsFromSentences() {
    CoreMap sentenceMap = new Annotation("Test with entity");
    CoreEntityMention em1 = new CoreEntityMention();
    CoreEntityMention em2 = new CoreEntityMention();

    sentenceMap.set(CoreAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em1, em2));

    List<CoreMap> sentenceMaps = new ArrayList<>();
    sentenceMaps.add(sentenceMap);

    Annotation ann = new Annotation("Test");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceMaps);

    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertEquals(2, mentions.size());
  }
@Test
  public void testQuotesListWrapped() {
    CoreMap quoteMap = new Annotation("Quote text");
    List<CoreMap> quotesList = new ArrayList<>();
    quotesList.add(quoteMap);

    Annotation ann = new Annotation("Quoted doc");
    ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    QuoteAnnotator.setQuotes(ann, quotesList);

    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    List<CoreQuote> quotes = doc.quotes();
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
  }
@Test
  public void testEmptyEntityMentionsWhenNoMentionsKey() {
    CoreMap sentenceMap = new Annotation("Test without mentions");

    List<CoreMap> sentenceMaps = new ArrayList<>();
    sentenceMaps.add(sentenceMap);

    Annotation ann = new Annotation("Test doc");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceMaps);
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testWrapAnnotationsWithoutSentences() {
    Annotation ann = new Annotation("No sentences");
    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    assertNull(doc.sentences());
  }
@Test
  public void testCorefChainsAvailable() {
    Annotation ann = new Annotation("Coref test");

    CorefChain chain = new CorefChain(1, new ArrayList<>());
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, chain);

    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    CoreDocument doc = new CoreDocument(ann);
    Map<Integer, CorefChain> returnedMap = doc.corefChains();

    assertNotNull(returnedMap);
    assertEquals(1, returnedMap.size());
    assertSame(chain, returnedMap.get(1));
  }
@Test
  public void testToStringDelegatesToAnnotation() {
    Annotation ann = new Annotation("Test string");
    ann.set(CoreAnnotations.TextAnnotation.class, "Test string");
    CoreDocument doc = new CoreDocument(ann);
    assertEquals(doc.annotation().toString(), doc.toString());
  }
@Test
  public void testQuotesNullWhenQuoteAnnotatorReturnsNull() {
    Annotation ann = new Annotation("No quote content");
    ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    QuoteAnnotator.setQuotes(ann, null);

    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    assertNull(doc.quotes());
  }
@Test
  public void testEntityMentionsNullWhenNoSentencesAndMentionsKey() {
    Annotation ann = new Annotation("");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    assertNotNull(doc.entityMentions());
    assertTrue(doc.entityMentions().isEmpty());
  }
@Test
  public void testWrapAnnotationsWithEmptySentences() {
    Annotation ann = new Annotation("Empty sentence list");
    ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    List<CoreSentence> sentences = doc.sentences();
    assertNotNull(sentences);
    assertTrue(sentences.isEmpty());
  } 
}