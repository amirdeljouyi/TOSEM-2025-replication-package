public class JSONOutputter_wosr_1_GPTLLMTest { 

 @Test
  public void testEmptyAnnotationProducesValidJSON() throws IOException {
    Annotation annotation = new Annotation("");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, new Options());
    String json = out.toString("UTF-8").trim();
    assertTrue(json.startsWith("{") && json.endsWith("}"));
  }
@Test
  public void testTextFieldIncludedInOutputWhenOptionSet() throws IOException {
    Annotation annotation = new Annotation("Some test text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Some test text.");
    Options options = new Options();
    options.includeText = true;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, options);
    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"text\""));
    assertTrue(json.contains("Some test text."));
  }
@Test
  public void testNoTextFieldWhenIncludeTextOptionFalse() throws IOException {
    Annotation annotation = new Annotation("This is hidden text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is hidden text.");
    Options options = new Options();
    options.includeText = false;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, options);
    String json = out.toString("UTF-8");
    assertFalse(json.contains("hidden text"));
    assertFalse(json.contains("\"text\""));
  }
@Test
  public void testTokenSerialization() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("Stanford");
    token.setOriginalText("Stanford");
    token.setBeginPosition(0);
    token.setEndPosition(8);
    token.setTag("NNP");
    token.setNER("ORGANIZATION");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, new Options());
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"tokens\""));
    assertTrue(json.contains("\"word\""));
    assertTrue(json.contains("Stanford"));
    assertTrue(json.contains("\"ner\""));
    assertTrue(json.contains("ORGANIZATION"));
  }
@Test
  public void testSentenceSectionIncluded() throws IOException {
    Annotation sentence = new Annotation("The quick brown fox.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.LineNumberAnnotation.class, 1);
    sentence.set(CoreAnnotations.ParagraphIndexAnnotation.class, 1);
    sentence.set(CoreAnnotations.SpeakerAnnotation.class, "Narrator");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("The quick brown fox.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, new Options());
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"sentences\""));
    assertTrue(json.contains("\"index\""));
    assertTrue(json.contains("\"paragraph\""));
    assertTrue(json.contains("\"speaker\""));
    assertTrue(json.contains("Narrator"));
  }
@Test
  public void testCorefChainSerialization() throws IOException {
    CorefChain.CorefMention mention = new CorefChain.CorefMention(1, 0, 0, 1, 0, "He", "PRP", "NUMBER", "MALE", "ANIMATE", null);
    List<CorefChain.CorefMention> mentions = new ArrayList<>();
    mentions.add(mention);
    CorefChain chain = new CorefChain(1, mention, mentions);

    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(1, chain);

    Annotation annotation = new Annotation("He runs.");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, new Options());
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"corefs\""));
    assertTrue(json.contains("He"));
    assertTrue(json.contains("\"type\""));
    assertTrue(json.contains("MALE"));
  }
@Test
  public void testQuoteAnnotationsHandledGracefully() throws IOException {
    CoreMap quote = new Annotation("“Hello.”");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 0);
    quote.set(CoreAnnotations.TextAnnotation.class, "“Hello.”");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
    quote.set(CoreAnnotations.SpeakerAnnotation.class, "Unknown");

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);

    Annotation annotation = new Annotation("“Hello.”");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, new Options());
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"quotes\""));
    assertTrue(json.contains("“Hello.”"));
    assertTrue(json.contains("Unknown"));
  }
@Test
  public void testTimexInTokenAndMention() throws IOException {
    Timex timex = new Timex("t1", "DATE", "2024-04-01", null, null);
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("April");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.setNER("DATE");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("April");
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    mention.set(CoreAnnotations.TextAnnotation.class, "April");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("April is spring.");
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("April is spring.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, new Options());
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"timex\""));
    assertTrue(json.contains("2024-04-01"));
    assertTrue(json.contains("\"DATE\""));
  }
@Test
  public void testPrettyJSONOptionAppliesFormatting() throws IOException {
    Annotation annotation = new Annotation("x");
    annotation.set(CoreAnnotations.TextAnnotation.class, "x");
    Options options = new Options();
    options.pretty = true;
    options.includeText = true;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, options);
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\n")); 
    assertTrue(json.contains("text"));
  }
@Test
  public void testObjectToJsonWorksForSimpleMap() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("hello", "world");
      writer.set("number", 42);
      writer.set("bool", true);
    });

    assertTrue(json.contains("\"hello\":\"world\""));
    assertTrue(json.contains("\"number\":42"));
    assertTrue(json.contains("\"bool\":true"));
  } 
}