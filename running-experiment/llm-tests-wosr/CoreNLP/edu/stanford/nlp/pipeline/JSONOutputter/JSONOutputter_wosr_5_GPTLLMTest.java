public class JSONOutputter_wosr_5_GPTLLMTest { 

 @Test
  public void testDocLevelMetadata() throws IOException {
    Annotation annotation = new Annotation("Test document");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc123");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-04-01");
    annotation.set(CoreAnnotations.AuthorAnnotation.class, "Tester");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.includeText = true;
    new JSONOutputter().print(annotation, os, options);
    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"docId\": \"doc123\""));
    assertTrue(json.contains("\"docDate\": \"2024-04-01\""));
    assertTrue(json.contains("\"author\": \"Tester\""));
    assertTrue(json.contains("\"text\": \"Test document\""));
  }
@Test
  public void testTokenSerialization() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setOriginalText("Hello");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "UH");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation doc = new Annotation("Hello");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    new JSONOutputter().print(doc, os, options);
    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"word\": \"Hello\""));
    assertTrue(json.contains("\"pos\": \"UH\""));
    assertTrue(json.contains("\"ner\": \"O\""));
  }
@Test
  public void testSentimentSerialization() throws IOException {
    CoreMap sentence = new Annotation("Great!");
    Tree sentimentTree = Tree.valueOf("(2 (2 Great))");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Great!");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    new JSONOutputter().print(doc, os, options);

    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"sentiment\": \"Positive\""));
    assertTrue(json.contains("\"sentimentValue\""));
    assertTrue(json.contains("\"sentimentTree\""));
  }
@Test
  public void testOpenIETriples() throws IOException {
    CoreMap sentence = new Annotation("John loves Mary.");
    RelationTriple triple = new RelationTriple(
        Arrays.asList(new CoreLabel()), Arrays.asList(new CoreLabel()), Arrays.asList(new CoreLabel()),
        1.0, null, null);
    triple.setSubjectLemmas(Arrays.asList("John"));
    triple.setRelationLemmas(Arrays.asList("loves"));
    triple.setObjectLemmas(Arrays.asList("Mary"));

    Collection<RelationTriple> triples = new ArrayList<>();
    triples.add(triple);
    sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, triples);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("John loves Mary.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    new JSONOutputter().print(doc, os, options);

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"subject\""));
    assertTrue(json.contains("\"relation\""));
    assertTrue(json.contains("\"object\""));
  }
@Test
  public void testCorefChains() throws IOException {
    CorefChain.CorefMention mention1 = new CorefChain.CorefMention(1, 0, 0, 1, "John", true, 1, 0, 1, 0, null, null, null, null);
    List<CorefChain.CorefMention> mentions = new ArrayList<>();
    mentions.add(mention1);
    CorefChain chain = new CorefChain(1, mentions);

    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, chain);

    Annotation doc = new Annotation("John went to the store. He bought apples.");
    doc.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    new JSONOutputter().print(doc, os, options);

    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"corefs\""));
    assertTrue(json.contains("\"isRepresentativeMention\""));
  }
@Test
  public void testJsonWriterEscaping() {
    String result = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("quote", "\"double quotes\"");
      writer.set("newline", "Line\nBreak");
    });

    assertTrue(result.contains("\\\"double quotes\\\""));
    assertTrue(result.contains("Line\\nBreak"));
  }
@Test
  public void testManualJsonPrintStaticMethod() throws IOException {
    Annotation annotation = new Annotation("Simple text.");
    String json = JSONOutputter.jsonPrint(annotation);
    assertNotNull(json);
    assertTrue(json.contains("{"));
  }

  @Test
  public void testJsonPrintWithOptions() throws IOException {
    Annotation annotation = new Annotation("Hello NLP world.");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "test001");

    OutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.pretty = true;
    JSONOutputter.jsonPrint(annotation, os, options);
    String output = os.toString();

    assertTrue(output.contains("docId"));
    assertTrue(output.contains("test001"));
    assertTrue(output.contains("\n"));
  }
}
@Test
  public void testJsonPrintWithOptions() throws IOException {
    Annotation annotation = new Annotation("Hello NLP world.");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "test001");

    OutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.pretty = true;
    JSONOutputter.jsonPrint(annotation, os, options);
    String output = os.toString();

    assertTrue(output.contains("docId"));
    assertTrue(output.contains("test001"));
    assertTrue(output.contains("\n"));
  } 
}