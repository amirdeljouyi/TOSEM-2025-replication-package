public class JSONOutputter_wosr_3_GPTLLMTest { 

 @Test
  public void testJsonOutputterWithEmptyAnnotation() throws IOException {
    Annotation annotation = new Annotation("");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.pretty = false;
    options.includeText = false;

    new JSONOutputter().print(annotation, outputStream, options);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("{"));
    assertTrue(result.contains("}"));
  }
@Test
  public void testJsonOutputterWithBasicDocumentFields() throws IOException {
    Annotation annotation = new Annotation("Test document text.");
    annotation.set(DocIDAnnotation.class, "DOC_001");
    annotation.set(DocDateAnnotation.class, "2024-05-05");
    annotation.set(DocSourceTypeAnnotation.class, "news");
    annotation.set(DocTypeAnnotation.class, "article");
    annotation.set(AuthorAnnotation.class, "John Doe");
    annotation.set(LocationAnnotation.class, "California");
    annotation.set(TextAnnotation.class, "Test document text.");

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.includeText = true;
    options.pretty = false;

    new JSONOutputter().print(annotation, outputStream, options);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("\"docId\":\"DOC_001\""));
    assertTrue(result.contains("\"docDate\":\"2024-05-05\""));
    assertTrue(result.contains("\"docSourceType\":\"news\""));
    assertTrue(result.contains("\"docType\":\"article\""));
    assertTrue(result.contains("\"author\":\"John Doe\""));
    assertTrue(result.contains("\"location\":\"California\""));
    assertTrue(result.contains("\"text\":\"Test document text.\""));
  }
@Test
  public void testTokenSerializationWhenNoSentence() throws IOException {
    CoreLabel token1 = new CoreLabel();
    token1.setIndex(1);
    token1.setWord("Hello");
    token1.setOriginalText("Hello");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);
    token1.setTag("UH");
    token1.setNER("O");

    CoreLabel token2 = new CoreLabel();
    token2.setIndex(2);
    token2.setWord("World");
    token2.setOriginalText("World");
    token2.setBeginPosition(6);
    token2.setEndPosition(11);
    token2.setTag("NN");
    token2.setNER("LOCATION");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    Annotation annotation = new Annotation("Hello World");
    annotation.set(TokensAnnotation.class, tokens);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.includeText = false;
    options.pretty = false;

    new JSONOutputter().print(annotation, outputStream, options);
    String result = outputStream.toString(StandardCharsets.UTF_8.name());

    assertTrue(result.contains("\"tokens\""));
    assertTrue(result.contains("\"word\":\"Hello\""));
    assertTrue(result.contains("\"ner\":\"O\""));
    assertTrue(result.contains("\"tag\":\"UH\""));
    assertTrue(result.contains("\"word\":\"World\""));
    assertTrue(result.contains("\"ner\":\"LOCATION\""));
    assertTrue(result.contains("\"tag\":\"NN\""));
  }
@Test
  public void testJsonPrintConvenienceMethod() throws IOException {
    Annotation empty = new Annotation("");
    String result = JSONOutputter.jsonPrint(empty);
    assertNotNull(result);
    assertTrue(result.startsWith("{"));
    assertTrue(result.endsWith("}"));
  }
@Test
  public void testJsonPrintWithCoreNLPCompatibility() throws IOException {
    Annotation annotation = new Annotation("Apple released a new product.");
    annotation.set(DocIDAnnotation.class, "DOC12345");

    AnnotationPipeline pipeline = new AnnotationPipeline();
    

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output, new StanfordCoreNLP(new Properties()));

    String json = output.toString(StandardCharsets.UTF_8.name());
    assertTrue(json.contains("\"docId\":\"DOC12345\""));
  }
@Test
  public void testJSONWriterObjectToJSONSupportsBasicStructure() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("key1", "value1");
      writer.set("key2", 42);
      writer.set("key3", true);
    });
    assertTrue(json.contains("\"key1\""));
    assertTrue(json.contains("\"value1\""));
    assertTrue(json.contains("\"key2\""));
    assertTrue(json.contains("42"));
    assertTrue(json.contains("\"key3\""));
    assertTrue(json.contains("true"));
  }
@Test
  public void testPrintHandlesNullAnnotationFieldsGracefully() throws IOException {
    Annotation annotation = new Annotation("Sample text.");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.Options options = new JSONOutputter.Options();
    options.includeText = true;
    options.pretty = false;

    new JSONOutputter().print(annotation, output, options);
    String json = output.toString(StandardCharsets.UTF_8.name());

    assertTrue(json.contains("\"text\":\"Sample text.\""));
  }
@Test
  public void testJsonWriterArraySerialization() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("numbers", new int[]{1, 2, 3});
    });
    assertTrue(json.contains("\"numbers\""));
    assertTrue(json.contains("[1,"));
    assertTrue(json.contains("2"));
    assertTrue(json.contains("3]"));
  }
@Test
  public void testJsonWriterWithNestedStructure() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("outer", (JSONOutputter.Writer) (key, value) -> {
        value = "value";
        writer.set("inner", value);
      });
    });
    assertTrue(json.contains("\"inner\""));
    assertTrue(json.contains("\"value\""));
  }
@Test
  public void testFlushDoesNotThrow() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(output);
    PrintWriter writer = new PrintWriter(printStream);
    JSONOutputter.Options options = new JSONOutputter.Options();
    JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);
    jsonWriter.flush();
  }
@Test
  public void testObjectHandlesNullValuesGracefully() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("field1", null);
      writer.set(null, "value2");
      writer.set("field3", "value3");
    });
    assertFalse(json.contains("field1"));
    assertFalse(json.contains("value2"));
    assertTrue(json.contains("field3"));
    assertTrue(json.contains("value3"));
  } 
}