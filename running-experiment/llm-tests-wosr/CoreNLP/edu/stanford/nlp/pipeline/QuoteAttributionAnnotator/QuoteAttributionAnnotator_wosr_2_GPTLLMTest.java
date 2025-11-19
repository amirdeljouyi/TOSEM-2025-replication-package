public class QuoteAttributionAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorWithDefaultProperties() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "test/coref.txt");
    props.setProperty("charactersPath", "test/characters.txt");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithNoCorefPath() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test/characters.txt");
    props.setProperty("verbose", "true");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithNoCharactersFile() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "test/coref.txt");
    props.setProperty("verbose", "true");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotationRequiresNotNull() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "coref/path");
    props.setProperty("charactersPath", "characters/path");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();
    assertNotNull(requirements);
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotationRequirementsSatisfiedNotNull() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "coref/path");
    props.setProperty("charactersPath", "char/path");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertNotNull(satisfied);
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
  }
@Test
  public void testEntityMentionsToCharacterMapAddsPerson() {
    Annotation annotation = new Annotation("");
    List<CoreMap> mentions = new ArrayList<>();
    CoreMap personMention = new Annotation("John Doe");
    personMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mentions.add(personMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "test/path.coref");
    props.setProperty("charactersPath", "test/characters.txt");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    annotator.entityMentionsToCharacterMap(annotation);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateNoMentionsAnnotation() {
    Annotation annotation = new Annotation("This is a test.");
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "test/path.txt");
    props.setProperty("charactersPath", "test/char.txt");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    annotator.annotate(annotation);
    assertNotNull(annotation);
  }
@Test
  public void testAnnotateWithMentionsAnnotationEmpty() {
    Annotation annotation = new Annotation("Text");
    List<CoreMap> emptyMentions = new ArrayList<>();
    annotation.set(CoreAnnotations.MentionsAnnotation.class, emptyMentions);
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "coref/file");
    props.setProperty("charactersPath", "char/path");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    annotator.annotate(annotation);
    assertNotNull(annotation);
  }
@Test
  public void testQMMappingContainsExpectedSieves() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "test/path.txt");
    props.setProperty("charactersPath", "char.txt");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Text.");
    List<CoreMap> mentions = new ArrayList<>();
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    Map<Integer, String> corefMap = new HashMap<>();
    Map<String, edu.stanford.nlp.quoteattribution.Sieves.QMSieves.QMSieve> map = annotator.getQMMapping(annotation, corefMap);
    assertTrue(map.containsKey("tri"));
    assertTrue(map.containsKey("dep"));
    assertTrue(map.containsKey("onename"));
    assertTrue(map.containsKey("sup"));
    assertTrue(map.containsKey("loose"));
  }
@Test
  public void testMSMappingContainsExpectedSieves() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "test/coref.json");
    props.setProperty("charactersPath", "path/char.tsv");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation doc = new Annotation("Testing speaker sieves.");
    List<CoreMap> dummyMentions = new ArrayList<>();
    doc.set(CoreAnnotations.MentionsAnnotation.class, dummyMentions);
    Map<Integer, String> map = new HashMap<>();
    Map<String, edu.stanford.nlp.quoteattribution.Sieves.MSSieves.MSSieve> msMap = annotator.getMSMapping(doc, map);
    assertTrue(msMap.containsKey("det"));
    assertTrue(msMap.containsKey("loose"));
    assertTrue(msMap.containsKey("top"));
    assertTrue(msMap.containsKey("maj"));
  }
@Test
  public void testSupervisedSieveModelLoadedInQMMapping() {
    Properties props = new Properties();
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("booknlpCoref", "dummy/path");
    props.setProperty("charactersPath", "dummy/character.txt");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Load model test.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    Map<Integer, String> corefMap = new HashMap<>();
    Map<String, edu.stanford.nlp.quoteattribution.Sieves.QMSieves.QMSieve> sieveMap = annotator.getQMMapping(annotation, corefMap);
    assertNotNull(sieveMap.get("sup"));
  }
@Test
  public void testCanonicalMentionAnnotationAdded() {
    Annotation annotation = new Annotation("Sample text for annotation.");
    CoreLabel speakerToken = new CoreLabel();
    speakerToken.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(speakerToken);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    List<CoreLabel> canonicalTokens = Arrays.asList(token1, token2);

    CoreMap canonicalMention = new Annotation("Canonical Name");
    canonicalMention.set(CoreAnnotations.TextAnnotation.class, "Canonical Name");
    canonicalMention.set(CoreAnnotations.TokensAnnotation.class, canonicalTokens);

    CoreMap mention = new Annotation("Mention");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 1);

    List<CoreMap> mentions = Arrays.asList(mention, canonicalMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    CoreMap quoteMap = new Annotation("Quote");
    quoteMap.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);
    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quoteMap);
    annotation.set(CoreAnnotations.QuotesAnnotation.class, quotes);

    Properties props = new Properties();
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("charactersPath", "character.tsv");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    annotator.annotate(annotation);

    CoreMap updatedQuote = annotation.get(CoreAnnotations.QuotesAnnotation.class).get(0);
    assertEquals("Canonical Name", updatedQuote.get(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    assertEquals(Integer.valueOf(0), updatedQuote.get(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), updatedQuote.get(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class));
  } 
}