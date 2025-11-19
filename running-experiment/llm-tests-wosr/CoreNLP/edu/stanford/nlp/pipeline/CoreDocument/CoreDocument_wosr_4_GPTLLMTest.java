public class CoreDocument_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructorWithString() {
    CoreDocument doc = new CoreDocument("This is a test.");
    assertNotNull(doc.annotation());
    assertEquals("This is a test.", doc.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithAnnotation() {
    Annotation annotation = new Annotation("Document with annotation.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Document with annotation.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("Document with annotation.", doc.annotation().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDocIDIsSetCorrectly() {
    Annotation annotation = new Annotation("ID test.");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-123");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("doc-123", doc.docID());
  }
@Test
  public void testDocDateIsSetCorrectly() {
    Annotation annotation = new Annotation("Date test.");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2023-10-14");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("2023-10-14", doc.docDate());
  }
@Test
  public void testTextAnnotation() {
    Annotation annotation = new Annotation("Some text here.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Some text here.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals("Some text here.", doc.text());
  }
@Test
  public void testTokensAnnotation() {
    Annotation annotation = new Annotation("Token test.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Token");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("test");
    tokens.add(token1);
    tokens.add(token2);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreDocument doc = new CoreDocument(annotation);
    List<CoreLabel> resultTokens = doc.tokens();
    assertEquals(2, resultTokens.size());
    assertEquals("Token", resultTokens.get(0).word());
    assertEquals("test", resultTokens.get(1).word());
  }
@Test
  public void testEmptySentencesAnnotation() {
    Annotation annotation = new Annotation("Empty sentences.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.entityMentions());
    assertNull(doc.sentences());
    assertNull(doc.quotes());
  }
@Test
  public void testSentencesAndEntityMentions() {
    Annotation annotation = new Annotation("Sentences and mentions.");
    CoreMap sentenceMap = new Annotation("sentence one");
    CoreEntityMention em = new CoreEntityMention(sentenceMap);
    sentenceMap.set(CoreAnnotations.EntityMentionsAnnotation.class, Collections.singletonList(em));
    List<CoreMap> sentenceList = Collections.singletonList(sentenceMap);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNotNull(doc.sentences());
    assertEquals(1, doc.sentences().size());
    assertNotNull(doc.entityMentions());
    assertEquals(1, doc.entityMentions().size());
  }
@Test
  public void testSentencesWithNoEntityMentionsButGlobalMentionsAnnotationExists() {
    Annotation annotation = new Annotation("Mentions annotation.");
    CoreMap sentenceMap = new Annotation("Another sentence");
    sentenceMap.set(CoreAnnotations.EntityMentionsAnnotation.class, null);
    List<CoreMap> sentences = Collections.singletonList(sentenceMap);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNotNull(doc.entityMentions());
    assertEquals(0, doc.entityMentions().size());
  }
@Test
  public void testQuotesWithNullGatheredQuotes() {
    Annotation annotation = new Annotation("Quote test.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    assertNull(doc.quotes());
  }
@Test
  public void testQuotesAreWrappedCorrectly() {
    Annotation annotation = new Annotation("With quote.");
    CoreMap quoteMap = new Annotation("He said, 'hi'");
    List<CoreMap> fakeQuotes = Collections.singletonList(quoteMap);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    QuoteAnnotator.setQuotesForTesting(annotation, fakeQuotes);
    CoreDocument doc = new CoreDocument(annotation);
    doc.wrapAnnotations();
    List<CoreQuote> resultQuotes = doc.quotes();
    assertNotNull(resultQuotes);
    assertEquals(1, resultQuotes.size());
    assertEquals("He said, 'hi'", resultQuotes.get(0).quote().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testCorefChainsReturnNullWhenNotSet() {
    CoreDocument doc = new CoreDocument("No coref annotated.");
    assertNull(doc.corefChains());
  }
@Test
  public void testCorefChainsReturnPopulatedMap() {
    Annotation annotation = new Annotation("With coref.");
    Map<Integer, CorefChain> chainMap = new HashMap<>();
    CorefChain chain = new CorefChain(1, new ArrayList<>());
    chainMap.put(1, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);
    CoreDocument doc = new CoreDocument(annotation);
    assertNotNull(doc.corefChains());
    assertEquals(1, doc.corefChains().size());
    assertTrue(doc.corefChains().containsKey(1));
  }
@Test
  public void testToStringReturnsUnderlyingAnnotationToString() {
    Annotation annotation = new Annotation("Testing toString.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Testing toString.");
    CoreDocument doc = new CoreDocument(annotation);
    assertEquals(annotation.toString(), doc.toString());
  } 
}