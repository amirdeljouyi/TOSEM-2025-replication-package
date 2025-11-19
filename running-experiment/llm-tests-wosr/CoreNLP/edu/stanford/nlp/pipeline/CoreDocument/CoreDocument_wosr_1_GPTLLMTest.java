public class CoreDocument_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructorWithText() {
    String text = "This is a simple test.";
    CoreDocument doc = new CoreDocument(text);
    assertNotNull(doc.annotation());
    assertEquals("This is a simple test.", doc.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotation() {
    Annotation annotation = new Annotation("Testing annotated constructor.");
    CoreDocument doc = new CoreDocument(annotation);
    assertSame(annotation, doc.annotation());
  }
@Test
  public void testWrapAnnotationsWithNoSentences() {
    Annotation annotation = new Annotation("Text without sentence annotations.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Text without sentence annotations.");
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.sentences());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  }
@Test
  public void testWrapAnnotationsWithEmptySentencesList() {
    Annotation annotation = new Annotation("Empty sentence list.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Empty sentence list.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertTrue(doc.sentences().isEmpty());
    assertNull(doc.entityMentions());
    assertNull(doc.quotes());
  }
@Test
  public void testSentencesWrapping() {
    CoreMap sentenceMap = new Annotation("Sentence one.");
    List<CoreMap> sentenceList = Arrays.asList(sentenceMap);
    Annotation annotation = new Annotation("Doc with one sentence.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Doc with one sentence.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreSentence> sentences = doc.sentences();
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals(doc, sentences.get(0).document());
  }
@Test
  public void testEntityMentionsWithEmptyMentionsAnnotation() {
    CoreMap sentenceMap = new Annotation("Sentence without mentions.");
    sentenceMap.set(CoreAnnotations.TextAnnotation.class, "Sentence without mentions.");
    sentenceMap.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = Arrays.asList(sentenceMap);
    Annotation annotation = new Annotation("No mentions.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "No mentions.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNotNull(doc.entityMentions());
    assertTrue(doc.entityMentions().isEmpty());
  }
@Test
  public void testQuoteGatheringReturnsNull() {
    Annotation annotation = new Annotation("A doc without quotes.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "A doc without quotes.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument(annotation) {
      @Override
      public void wrapAnnotations() {
        super.wrapAnnotations();
        this.quotes = null;
      }
    };
    doc.wrapAnnotations();
    assertNull(doc.quotes());
  }
@Test
  public void testDocIDAnnotation() {
    Annotation annotation = new Annotation("Doc ID test.");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-12345");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("doc-12345", doc.docID());
  }
@Test
  public void testDocDateAnnotation() {
    Annotation annotation = new Annotation("Doc Date test.");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-03-31");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("2024-03-31", doc.docDate());
  }
@Test
  public void testTextAnnotation() {
    Annotation annotation = new Annotation("Test text annotation.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Test text annotation.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("Test text annotation.", doc.text());
  }
@Test
  public void testTokensAnnotation() {
    List<CoreLabel> labels = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    labels.add(token);

    Annotation annotation = new Annotation("Token annotation.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, labels);
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals(1, doc.tokens().size());
    assertEquals("Hello", doc.tokens().get(0).word());
  }
@Test
  public void testCorefChainsPresent() {
    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(1, new CorefChain(1, new ArrayList<>()));

    Annotation annotation = new Annotation("Coref test.");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);
    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> retrievedChains = doc.corefChains();
    assertNotNull(retrievedChains);
    assertTrue(retrievedChains.containsKey(1));
  }
@Test
  public void testToStringDelegatesToAnnotation() {
    Annotation annotation = new Annotation("This is a doc.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is a doc.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals(annotation.toString(), doc.toString());
  }
@Test
  public void testWrapAnnotationsWithEntityMentionsFromSentences() {
    List<CoreEntityMention> mentions = new ArrayList<>();
    CoreEntityMention mention = new CoreEntityMention();
    mentions.add(mention);

    CoreMap sentenceMap = new Annotation("Entity sentence.");
    sentenceMap.set(CoreAnnotations.TextAnnotation.class, "Entity sentence.");
    sentenceMap.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentenceMap.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentenceList = Arrays.asList(sentenceMap);
    Annotation annotation = new Annotation("Sentence with entity.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Sentence with entity.");

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.entityMentions());
  }
@Test
  public void testQuotesAreConstructed() {
    List<CoreMap> quoteMaps = new ArrayList<>();
    CoreMap quoteMap = new Annotation("A quote.");
    quoteMaps.add(quoteMap);

    Annotation annotation = new Annotation("Doc with quote.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Doc with quote.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(annotation) {
      @Override
      public void wrapAnnotations() {
        buildDocumentQuotesList();
        this.quotes = Arrays.asList(new CoreQuote(this, quoteMap));
      }
    };

    doc.wrapAnnotations();
    assertNotNull(doc.quotes());
    assertEquals(1, doc.quotes().size());
    assertEquals("A quote.", doc.quotes().get(0).quoteMap().get(CoreAnnotations.TextAnnotation.class));
  } 
}