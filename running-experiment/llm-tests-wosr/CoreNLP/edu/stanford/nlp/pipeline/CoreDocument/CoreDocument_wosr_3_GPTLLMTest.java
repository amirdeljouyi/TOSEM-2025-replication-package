public class CoreDocument_wosr_3_GPTLLMTest { 

 @Test
  public void testConstructorWithText_shouldInitializeAnnotation() {
    CoreDocument doc = new CoreDocument("Test text.");
    assertNotNull(doc.annotation());
    assertEquals("Test text.", doc.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotation_shouldWrapIfSentencesPresent() {
    CoreMap sentence = new Annotation("This is a test sentence.");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annot = new Annotation("This is a test sentence.");
    annot.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    CoreDocument doc = new CoreDocument(annot); 

    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
  }
@Test
  public void testWrapAnnotations_withEmptySentences_shouldDoNothing() {
    Annotation annot = new Annotation("No sentences.");
    annot.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(annot);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertTrue(doc.sentences().isEmpty());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotations_withMentionsAnnotationOnly_shouldInitializeEmptyEntityList() {
    Annotation annot = new Annotation("Document with coref only.");
    annot.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annot.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(annot);
    doc.wrapAnnotations();

    assertNotNull(doc.entityMentions());
    assertTrue(doc.entityMentions().isEmpty());
  }
@Test
  public void testWrapAnnotations_withQuoteAnnotatorQuotes_shouldBuildQuotesList() {
    List<CoreMap> fakeQuotes = new ArrayList<>();
    CoreMap quote1 = new Annotation("He said, 'Hello'");
    fakeQuotes.add(quote1);

    Annotation annot = new Annotation("He said, 'Hello'");
    annot.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>()); 
    QuoteAnnotator.setMockQuotes(fakeQuotes); 

    CoreDocument doc = new CoreDocument(annot);
    doc.wrapAnnotations();

    assertNotNull(doc.quotes());
    assertEquals(1, doc.quotes().size());
    assertEquals("He said, 'Hello'", doc.quotes().get(0).quoteText());
  }
@Test
  public void testTokens_shouldReturnCorrectTokenList() {
    Annotation annotation = new Annotation("Sample text.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Sample");
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("text");
    tokens.add(tok1);
    tokens.add(tok2);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreDocument doc = new CoreDocument(annotation);
    List<CoreLabel> resultTokens = doc.tokens();

    assertNotNull(resultTokens);
    assertEquals(2, resultTokens.size());
    assertEquals("Sample", resultTokens.get(0).word());
    assertEquals("text", resultTokens.get(1).word());
  }
@Test
  public void testDocId_shouldReturnSetDocId() {
    Annotation annotation = new Annotation("Some content");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-123");

    CoreDocument doc = new CoreDocument(annotation);

    assertEquals("doc-123", doc.docID());
  }
@Test
  public void testDocDate_shouldReturnSetDocDate() {
    Annotation annotation = new Annotation("Some content");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-05-01");

    CoreDocument doc = new CoreDocument(annotation);

    assertEquals("2024-05-01", doc.docDate());
  }
@Test
  public void testText_shouldReturnFullText() {
    Annotation annotation = new Annotation("Full document text");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Full document text");

    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("Full document text", doc.text());
  }
@Test
  public void testCorefChains_shouldReturnCorefMap() {
    Annotation annotation = new Annotation("Coref example.");
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, new CorefChain(new ArrayList<>())); 
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> retrieved = doc.corefChains();

    assertNotNull(retrieved);
    assertEquals(1, retrieved.size());
  }
@Test
  public void testToString_shouldMatchAnnotationToString() {
    Annotation annotation = new Annotation("Hello world.");
    CoreDocument doc = new CoreDocument(annotation);

    assertEquals(annotation.toString(), doc.toString());
  }
@Test
  public void testEntityMentions_shouldReturnFlattenedListFromSentences() {
    
    CoreEntityMention mention1 = new CoreEntityMention();
    List<CoreEntityMention> mentionList1 = new ArrayList<>();
    mentionList1.add(mention1);

    CoreMap sentenceMap1 = new Annotation("First sentence.");
    sentenceMap1.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    CoreSentence coreSentence1 = new CoreSentence(null, sentenceMap1);
    coreSentence1.setEntityMentions(mentionList1);

    
    CoreEntityMention mention2 = new CoreEntityMention();
    CoreEntityMention mention3 = new CoreEntityMention();
    List<CoreEntityMention> mentionList2 = new ArrayList<>();
    mentionList2.add(mention2);
    mentionList2.add(mention3);

    CoreMap sentenceMap2 = new Annotation("Second sentence.");
    sentenceMap2.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    CoreSentence coreSentence2 = new CoreSentence(null, sentenceMap2);
    coreSentence2.setEntityMentions(mentionList2);

    List<CoreMap> sentenceMapList = new ArrayList<>();
    sentenceMapList.add(sentenceMap1);
    sentenceMapList.add(sentenceMap2);

    Annotation annotation = new Annotation("Full doc.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceMapList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.sentences = new ArrayList<>();
    doc.sentences().add(coreSentence1);
    doc.sentences().add(coreSentence2);
    doc.wrapAnnotations();

    List<CoreEntityMention> mentions = doc.entityMentions();
    assertNotNull(mentions);
    assertEquals(3, mentions.size());
  }
@Test
  public void testSentences_shouldReturnListAfterWrapping() {
    CoreMap sentence1 = new Annotation("This is sentence one.");
    CoreMap sentence2 = new Annotation("This is sentence two.");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("Combined");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(2, doc.sentences().size());
    assertEquals("This is sentence one.", doc.sentences().get(0).toString());
  }
@Test
  public void testAnnotationGetter_shouldReturnSameAnnotation() {
    Annotation ann = new Annotation("Original text");
    CoreDocument doc = new CoreDocument(ann);
    assertSame(ann, doc.annotation());
  }
@Test
  public void testNullSentenceAnnotation_shouldNotThrowException() {
    Annotation ann = new Annotation("Test document");
    CoreDocument doc = new CoreDocument(ann);
    doc.wrapAnnotations();

    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  } 
}