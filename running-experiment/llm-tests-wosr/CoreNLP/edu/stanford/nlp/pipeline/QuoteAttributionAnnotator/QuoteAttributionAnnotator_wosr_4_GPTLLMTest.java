public class QuoteAttributionAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructor_WithMinimalRequiredProperties() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("modelPath", "my_model.ser");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
    assertTrue(annotator.useCoref);
  }
@Test
  public void testConstructor_WithVerboseTrue() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("modelPath", "my_model.ser");
    props.setProperty("verbose", "true");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
    assertTrue(annotator.VERBOSE);
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphIndexAnnotation.class));
  }
@Test
  public void testRequiresIncludesCOREFWhenUseCorefTrue() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("useCoref", "true");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> requires = annotator.requires();

    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testRequiresDoesNotIncludeCOREFWhenUseCorefFalse() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("useCoref", "false");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> requires = annotator.requires();

    assertFalse(requires.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.IndexAnnotation.class));
  }
@Test
  public void testEntityMentionsToCharacterMapAddsOnlyPersonEntities() {
    CoreMap mention1 = new TypesafeMap.CoreMap();
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention1.set(CoreAnnotations.TextAnnotation.class, "John Doe");

    CoreMap mention2 = new TypesafeMap.CoreMap();
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Paris");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    annotator.entityMentionsToCharacterMap(annotation);

    assertNotNull(annotator);
    assertNotNull(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class).equals("PERSON"));
    assertEquals("John Doe", mention1.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotateRunsWithoutException_OnMinimalAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("modelPath", "edu/stanford/nlp/models/quoteattribution/quoteattribution_model.ser");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He said, \"Hello world.\"");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.IndexAnnotation.class, 0);
    token1.set(CoreAnnotations.TextAnnotation.class, "He");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.TextAnnotation.class, "He said, \"Hello world.\"");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotation should not throw exception, but got: " + e.getMessage());
    }
  }
@Test
  public void testGetQMMappingContainsExpectedSieves() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("A quote here");
    Map<Integer, String> corefMap = new HashMap<>();

    Map<String, QMSieve> qmMapping = annotator.getQMMapping(annotation, corefMap);
    assertTrue(qmMapping.containsKey("tri"));
    assertTrue(qmMapping.containsKey("dep"));
    assertTrue(qmMapping.containsKey("onename"));
    assertTrue(qmMapping.containsKey("sup"));
    assertTrue(qmMapping.containsKey("loose"));
  }
@Test
  public void testGetMSMappingContainsExpectedSieves() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Another quote");
    Map<Integer, String> corefMap = new HashMap<>();

    Map<String, MSSieve> msMapping = annotator.getMSMapping(annotation, corefMap);
    assertTrue(msMapping.containsKey("det"));
    assertTrue(msMapping.containsKey("top"));
    assertTrue(msMapping.containsKey("loose"));
    assertTrue(msMapping.containsKey("maj"));
  }
@Test
  public void testQuoteAnnotationWithCanonicalMentionAddsInfo() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.IndexAnnotation.class, 0);
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreMap entityMention = new TypesafeMap.CoreMap();
    entityMention.set(CoreAnnotations.TextAnnotation.class, "Mr. John");
    entityMention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 0);
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("He said something.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

    CoreMap quote = new TypesafeMap.CoreMap();
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    List<CoreMap> quotes = Arrays.asList(quote);
    QuoteAnnotator.setQuotes(annotation, quotes);

    try {
      annotator.annotate(annotation);
      assertNotNull(quote.get(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    } catch (Exception e) {
      fail("Exception during annotation: " + e.getMessage());
    }
  } 
}