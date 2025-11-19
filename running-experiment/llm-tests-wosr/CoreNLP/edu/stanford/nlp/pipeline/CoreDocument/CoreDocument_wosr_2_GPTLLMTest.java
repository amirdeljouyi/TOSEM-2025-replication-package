public class CoreDocument_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorWithStringInitializesAnnotation() {
    String text = "This is a test.";
    CoreDocument doc = new CoreDocument(text);
    assertNotNull(doc.annotation());
    assertEquals(text, doc.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotation() {
    Annotation annotation = new Annotation("Sample text.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals(annotation, doc.annotation());
  }
@Test
  public void testWrapAnnotationsWithSentencesAndEntityMentions() {
    Annotation annotation = new Annotation("Person A went to Paris.");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Person");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "A");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap sentence = new Annotation("Person A went to Paris.");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreEntityMention entityMention = new CoreEntityMention("Person A", "PERSON", sentence, 0, 2);
    sentence.set(CoreAnnotations.EntityMentionsAnnotation.class, Collections.singletonList(entityMention));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
    assertNotNull(doc.entityMentions());
    assertEquals(1, doc.entityMentions().size());
    assertEquals("Person A", doc.entityMentions().get(0).text());
  }
@Test
  public void testWrapAnnotationsWithOnlyMentionsAnnotation() {
    Annotation annotation = new Annotation("Some mentions.");
    CoreMap sentence = new Annotation("Some mentions.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNotNull(doc.entityMentions());
    assertTrue(doc.entityMentions().isEmpty());
  }
@Test
  public void testWrapAnnotationsWithNoSentences() {
    Annotation annotation = new Annotation("No sentences here.");
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.sentences());
  }
@Test
  public void testWrapAnnotationsWithQuotes() {
    Annotation annotation = new Annotation("She said, \"Hello\".");
    List<CoreMap> mockQuotes = new ArrayList<>();
    CoreMap quote = new Annotation("\"Hello\"");
    mockQuotes.add(quote);
    annotation.set(QuoteAnnotator.GatherQuotesAnnotation.class, mockQuotes);

    CoreMap sentence = new Annotation("She said, \"Hello\".");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();

    assertNotNull(doc.quotes());
    assertEquals(1, doc.quotes().size());
    assertEquals("\"Hello\"", doc.quotes().get(0).quoteText());
  }
@Test
  public void testAnnotationReturnsSameInstance() {
    Annotation annotation = new Annotation("Test doc.");
    CoreDocument doc = new CoreDocument(annotation);
    assertSame(annotation, doc.annotation());
  }
@Test
  public void testDocIDWhenAnnotationHasDocID() {
    Annotation annotation = new Annotation("Some doc.");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-123");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("doc-123", doc.docID());
  }
@Test
  public void testDocIDWhenAnnotationDoesNotHaveDocID() {
    Annotation annotation = new Annotation("No ID.");
    CoreDocument doc = new CoreDocument(annotation);
    assertNull(doc.docID());
  }
@Test
  public void testDocDateWhenSet() {
    Annotation annotation = new Annotation("Date doc.");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-01-01");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("2024-01-01", doc.docDate());
  }
@Test
  public void testTextReturnsCorrectValue() {
    Annotation annotation = new Annotation("Some custom text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Some custom text.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("Some custom text.", doc.text());
  }
@Test
  public void testTokensReturnsCorrectList() {
    Annotation annotation = new Annotation("Token text.");
    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("Token");
    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("text");
    List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreDocument doc = new CoreDocument(annotation);
    List<CoreLabel> result = doc.tokens();

    assertEquals(2, result.size());
    assertEquals("Token", result.get(0).word());
    assertEquals("text", result.get(1).word());
  }
@Test
  public void testCorefChainsReturnsMap() {
    Annotation annotation = new Annotation("Sample mention.");
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    CorefChain mockChain = new CorefChain(1, new ArrayList<>());
    corefMap.put(1, mockChain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    CoreDocument doc = new CoreDocument(annotation);
    Map<Integer, CorefChain> result = doc.corefChains();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertSame(mockChain, result.get(1));
  }
@Test
  public void testToStringReturnsAnnotationToString() {
    Annotation annotation = new Annotation("Some content here.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals(annotation.toString(), doc.toString());
  }
@Test
  public void testQuotesReturnsNullIfNotInitialized() {
    CoreDocument doc = new CoreDocument("This doc has no quotes.");
    assertNull(doc.quotes());
  }
@Test
  public void testEntityMentionsReturnsNullIfNotInitialized() {
    CoreDocument doc = new CoreDocument("Entities not processed.");
    assertNull(doc.entityMentions());
  }
@Test
  public void testSentencesReturnsNullIfNotInitialized() {
    CoreDocument doc = new CoreDocument("Sentence segmentation not done.");
    assertNull(doc.sentences());
  }
@Test
  public void testQuoteInitializationWithoutQuotesAnnotation() {
    Annotation annotation = new Annotation("No quotes here.");
    CoreMap sentence = new Annotation("No quotes here.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.quotes());
  } 
}