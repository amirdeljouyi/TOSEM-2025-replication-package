public class JSONOutputter_wosr_2_GPTLLMTest { 

 @Test
  public void testBasicDocumentFields() throws IOException {
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc1");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-05-01");
    annotation.set(CoreAnnotations.DocSourceTypeAnnotation.class, "news");
    annotation.set(CoreAnnotations.DocTypeAnnotation.class, "article");
    annotation.set(CoreAnnotations.AuthorAnnotation.class, "John Doe");
    annotation.set(CoreAnnotations.LocationAnnotation.class, "Stanford");

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"docId\":"));
    assertTrue(json.contains("\"docDate\":"));
    assertTrue(json.contains("\"docSourceType\":"));
    assertTrue(json.contains("\"docType\":"));
    assertTrue(json.contains("\"author\":"));
    assertTrue(json.contains("\"location\":"));
  }
@Test
  public void testIncludeTextFieldOption() throws IOException {
    Annotation annotation = new Annotation("Hello world.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello world.");

    JSONOutputter.Options options = new JSONOutputter.Options();
    options.includeText = true;

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output, options);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"text\":"));
    assertTrue(json.contains("Hello world."));
  }
@Test
  public void testEmptySentencesTokenFallback() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setWord("Hi");
    token.setOriginalText("Hi");
    token.setBeginPosition(0);
    token.setEndPosition(2);
    token.setIndex(1);

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Hi");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"tokens\":"));
    assertTrue(json.contains("\"word\": \"Hi\""));
  }
@Test
  public void testCorefChainOutput() throws IOException {
    List<CorefChain.CorefMention> mentions = new ArrayList<>();
    CorefChain.CorefMention mention = new CorefChain.CorefMention(
        1, 0, 1, 2, 1, "he", Dictionaries.MentionType.PRONOMINAL,
        Dictionaries.Number.SINGULAR, Dictionaries.Gender.MALE,
        Dictionaries.Animacy.ANIMATE, 1, new int[]{1,2,3});
    mentions.add(mention);

    CorefChain chain = new CorefChain(1, mentions);

    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(1, chain);

    Annotation annotation = new Annotation("");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"corefs\""));
    assertTrue(json.contains("\"mentionSpan\": \"he\"") || json.contains("\"text\": \"he\""));
  }
@Test
  public void testQuotesBasicFields() throws IOException {
    CoreMap quote = new Annotation("He said hello.");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 0);
    quote.set(CoreAnnotations.TextAnnotation.class, "Hello");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 1);
    quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "John");
    quote.set(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class, "John Smith");

    List<CoreMap> quotes = Collections.singletonList(quote);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"quotes\""));
    assertTrue(json.contains("\"text\": \"Hello\""));
    assertTrue(json.contains("\"speaker\": \"John\""));
    assertTrue(json.contains("\"canonicalSpeaker\": \"John Smith\""));
  }
@Test
  public void testTimexEntityMention() throws IOException {
    Timex timex = new Timex("t1", Timex.Type.TIME, "2024-05-01");
    timex.setRange(new Timex.Range("2024-05-01T00:00", "2024-05-01T23:59", "PT24H"));

    CoreLabel token = new CoreLabel();
    token.setWord("Today");
    token.setIndex(1);
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.set(CoreAnnotations.TextAnnotation.class, "Today");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> mentions = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Today is the day.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, mentions);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Today is the day.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"timex\""));
    assertTrue(json.contains("\"tid\": \"t1\""));
    assertTrue(json.contains("\"value\": \"2024-05-01\""));
    assertTrue(json.contains("\"range\""));
    assertTrue(json.contains("\"begin\": \"2024-05-01T00:00\""));
  }
@Test
  public void testSectionSentenceIndexes() throws IOException {
    CoreMap sentence = new Annotation("First sentence.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    CoreMap section = new Annotation("Section");
    section.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
    section.set(CoreAnnotations.AuthorAnnotation.class, "Author Name");
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-06-01");

    List<CoreMap> sections = Collections.singletonList(section);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"sections\""));
    assertTrue(json.contains("\"sentenceIndexes\""));
    assertTrue(json.contains("\"index\": 0"));
    assertTrue(json.contains("\"author\": \"Author Name\""));
  }
@Test
  public void testRelationTripleOpenIE() throws IOException {
    RelationTriple triple = new RelationTriple(
        Arrays.asList(new CoreLabel()), new CoreLabel(), Arrays.asList(new CoreLabel()),
        "Barack Obama", "was", "President", null);

    List<RelationTriple> triples = Collections.singletonList(triple);

    CoreMap sentence = new Annotation("Barack Obama was President.");
    sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, triples);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Barack Obama was President.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"openie\""));
    assertTrue(json.contains("\"subject\": \"Barack Obama\""));
    assertTrue(json.contains("\"relation\": \"was\""));
    assertTrue(json.contains("\"object\": \"President\""));
  }
@Test
  public void testSentimentFields() throws IOException {
    Tree sentimentTree = Tree.valueOf("(3 (2 good) (4 product))");

    CoreMap sentence = new Annotation("good product");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("good product");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, output);

    String json = output.toString("UTF-8");
    assertTrue(json.contains("\"sentimentValue\""));
    assertTrue(json.contains("\"sentiment\": \"Positive\"") || json.contains("\"sentiment\":"));
    assertTrue(json.contains("\"sentimentTree\""));
  } 
}