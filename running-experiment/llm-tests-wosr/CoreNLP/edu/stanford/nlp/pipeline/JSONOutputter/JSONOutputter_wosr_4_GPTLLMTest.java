public class JSONOutputter_wosr_4_GPTLLMTest { 

 @Test
  public void testJsonPrint_withNullFields() throws IOException {
    Annotation annotation = new Annotation("");
    
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.includeText = true;
    options.pretty = true;

    JSONOutputter.jsonPrint(annotation, os, options);
    String output = os.toString();

    assertNotNull(output);
    assertTrue(output.contains("\"text\": \"\""));
  }
@Test
  public void testJsonPrint_withDocumentMetadata() throws IOException {
    Annotation annotation = new Annotation("Stanford CoreNLP");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc123");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-01-01");
    annotation.set(CoreAnnotations.DocSourceTypeAnnotation.class, "newswire");
    annotation.set(CoreAnnotations.DocTypeAnnotation.class, "article");
    annotation.set(CoreAnnotations.AuthorAnnotation.class, "test-author");
    annotation.set(CoreAnnotations.LocationAnnotation.class, "Palo Alto");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.includeText = true;

    JSONOutputter.jsonPrint(annotation, os, options);
    String output = os.toString();

    assertTrue(output.contains("\"docId\": \"doc123\""));
    assertTrue(output.contains("\"docDate\": \"2024-01-01\""));
    assertTrue(output.contains("\"docSourceType\": \"newswire\""));
    assertTrue(output.contains("\"docType\": \"article\""));
    assertTrue(output.contains("\"author\": \"test-author\""));
    assertTrue(output.contains("\"location\": \"Palo Alto\""));
    assertTrue(output.contains("\"text\": \"Stanford CoreNLP\""));
  }
@Test
  public void testJsonPrint_withSingleToken() throws IOException {
    Annotation annotation = new Annotation("test");
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setOriginalText("test");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.setTag("NN");
    token.setNER("O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.pretty = true;

    JSONOutputter.jsonPrint(annotation, os, options);
    String json = os.toString();

    assertTrue(json.contains("\"word\": \"test\""));
    assertTrue(json.contains("\"index\": 1"));
    assertTrue(json.contains("\"pos\": \"NN\""));
    assertTrue(json.contains("\"ner\": \"O\""));
  }
@Test
  public void testJsonPrint_withSingleQuote() throws IOException {
    Annotation annotation = new Annotation("sample");
    CoreMap quote = new Annotation("Hello");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.TextAnnotation.class, "Hello");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 1);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.pretty = true;

    JSONOutputter.jsonPrint(annotation, os, options);
    String json = os.toString();

    assertTrue(json.contains("\"text\": \"Hello\""));
    assertTrue(json.contains("\"beginIndex\": 0"));
    assertTrue(json.contains("\"endIndex\": 5"));
  }
@Test
  public void testJsonPrint_withTimexOnEntityMention() throws IOException {
    Annotation annotation = new Annotation("March 2020");
    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "March 2020");
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    Timex timex = new Timex("t1", "DATE", "2020-03");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("dummy sentence");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    JSONOutputter.jsonPrint(annotation, os, options);

    String json = os.toString();
    assertTrue(json.contains("\"value\": \"2020-03\""));
    assertTrue(json.contains("\"type\": \"DATE\""));
    assertTrue(json.contains("\"tid\": \"t1\""));
  }
@Test
  public void testJsonPrint_withCorefChain() throws IOException {
    Annotation annotation = new Annotation("test text");

    CorefChain.CorefMention mention1 = new CorefChain.CorefMention(1, 1, 1, 1, 1, "he", true, null);
    CorefChain.CorefMention mention2 = new CorefChain.CorefMention(1, 2, 2, 2, 2, "John", false, null);

    List<CorefChain.CorefMention> mentions = Arrays.asList(mention1, mention2);
    CorefChain corefChain = new CorefChain(mentions, 1, Collections.emptyMap());

    Map<Integer, CorefChain> chainsMap = new HashMap<>();
    chainsMap.put(1, corefChain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainsMap);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();

    JSONOutputter.jsonPrint(annotation, os, options);
    String json = os.toString();

    assertTrue(json.contains("\"text\": \"he\"") || json.contains("\"text\": \"John\""));
    assertTrue(json.contains("\"corefs\""));
  }
@Test
  public void testJSONPrinterObjectToJSON_basicStructure() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("name", "NLP");
      writer.set("version", 4);
    });

    assertTrue(json.contains("\"name\": \"NLP\""));
    assertTrue(json.contains("\"version\": 4"));
  }
@Test
  public void testJsonPrint_handlesEmptyAnnotation() throws IOException {
    Annotation annotation = new Annotation("");
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    JSONOutputter.jsonPrint(annotation, os);
    String json = os.toString();

    assertNotNull(json);
    assertTrue(json.startsWith("{"));
    assertTrue(json.endsWith("}"));
  } 
}