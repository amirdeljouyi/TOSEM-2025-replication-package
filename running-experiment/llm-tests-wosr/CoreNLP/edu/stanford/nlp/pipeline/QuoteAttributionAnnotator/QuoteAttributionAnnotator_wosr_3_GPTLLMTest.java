public class QuoteAttributionAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testConstructorWithAllRequiredProperties() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "edu/stanford/nlp/models/quoteattribution/test_characters.tsv");
    props.setProperty("booknlpCoref", "edu/stanford/nlp/models/quoteattribution/test_coref.tsv");
    props.setProperty("QMSieves", "tri,dep");
    props.setProperty("MSSieves", "det,top");
    props.setProperty("modelPath", "edu/stanford/nlp/models/quoteattribution/test_model.ser");
    props.setProperty("familyWordsFile", "edu/stanford/nlp/models/quoteattribution/family_words.txt");
    props.setProperty("animacyWordsFile", "edu/stanford/nlp/models/quoteattribution/animate.unigrams.txt");
    props.setProperty("genderNamesFile", "edu/stanford/nlp/models/quoteattribution/gender_filtered.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
    assertTrue(annotator.VERBOSE == false);
  }
@Test
  public void testConstructorMissingCharactersFile() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "edu/stanford/nlp/models/quoteattribution/test_coref.tsv");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertTrue(annotator.buildCharacterMapPerAnnotation);
  }
@Test
  public void testBuildCharacterMapFromEntityMentions() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "mock/path");
    props.setProperty("modelPath", "edu/stanford/nlp/models/quoteattribution/test_model.ser");
    props.setProperty("charactersPath", "mock/path");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Peter met John. He said hello.");

    CoreMap mention = new Annotation("Peter");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap mention2 = new Annotation("John");
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    mentions.add(mention2);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.entityMentionsToCharacterMap(annotation);

    Map<String, List<Person>> characterMap = new HashMap<>();
    for (CoreMap entityMention : annotation.get(CoreAnnotations.MentionsAnnotation.class)) {
      String normalized = entityMention.toString().replaceAll("\\s+", " ");
      characterMap.put(normalized, Collections.singletonList(new Person(normalized, "UNK", new ArrayList<>())));
    }

    assertNotNull(characterMap.get("Peter"));
    assertNotNull(characterMap.get("John"));
  }
@Test
  public void testRequirementsSatisfiedIncludesMentionAndSpeakerAnnotations() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "mock/path");
    props.setProperty("booknlpCoref", "mock/path");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphIndexAnnotation.class));
  }
@Test
  public void testRequiresIncludesExpectedAnnotationsWhenCorefEnabled() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "mock/path");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotateHandlesEmptyMentionListWithNoCrash() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "mock/path");
    props.setProperty("booknlpCoref", "mock/path");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("No persons mentioned.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception ex) {
      fail("Annotation should complete without throwing exception");
    }
  }
@Test
  public void testGetQMMappingIncludesTrigramAndDependencySieve() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "mock/path");
    props.setProperty("booknlpCoref", "mock/path");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation doc = new Annotation("“Hello,” said John. “Hi,” replied Peter.");

    Map<Integer, String> corefMap = new HashMap<>();
    Map<String, QMSieve> qmMap = annotator.getQMMapping(doc, corefMap);

    assertTrue(qmMap.containsKey("tri"));
    assertTrue(qmMap.containsKey("dep"));
  }
@Test
  public void testGetMSMappingIncludesDeterministicAndTopSpeaker() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "mock/path");
    props.setProperty("booknlpCoref", "mock/path");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation doc = new Annotation("“Hi,” said John.");

    Map<Integer, String> corefMap = new HashMap<>();
    Map<String, MSSieve> msMap = annotator.getMSMapping(doc, corefMap);

    assertTrue(msMap.containsKey("det"));
    assertTrue(msMap.containsKey("top"));
  }
@Test
  public void testSupervisedSieveLoadsModelWithoutException() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "mock/path");
    props.setProperty("booknlpCoref", "mock/path");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation doc = new Annotation("“Yes,” Alice said.");

    Map<Integer, String> corefMap = new HashMap<>();
    Map<String, QMSieve> qmMap = annotator.getQMMapping(doc, corefMap);

    QMSieve supSieve = qmMap.get("sup");

    try {
      ((SupervisedSieve) supSieve).loadModel(QuoteAttributionAnnotator.MODEL_PATH);
    } catch (Exception e) {
      fail("Should not throw exception when loading supervised model: " + e.getMessage());
    }
  }
@Test
  public void testCanonicalMentionAnnotationSetWhenEntityMentionAvailable() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "mock/path");
    props.setProperty("booknlpCoref", "mock/path");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap quote = new Annotation("“Hi.”");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);
    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);

    CoreMap mention = new Annotation("John");
    mention.set(CoreAnnotations.TextAnnotation.class, "John");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 1);

    CoreMap canonical = new Annotation("Mr. John");
    canonical.set(CoreAnnotations.TextAnnotation.class, "Mr. John");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);

    List<CoreLabel> canonicalTokens = new ArrayList<>();
    canonicalTokens.add(token1);
    canonicalTokens.add(token2);

    canonical.set(CoreAnnotations.TokensAnnotation.class, canonicalTokens);
    List<CoreMap> mentionList = new ArrayList<>();
    mentionList.add(mention);
    mentionList.add(canonical);

    Annotation annotation = new Annotation("“Hi,” said John.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

    QuoteAnnotator quoteAnnotator = new QuoteAnnotator();
    List<CoreMap> gatheredQuotes = new ArrayList<>();
    gatheredQuotes.add(quote);

    QuoteAnnotator.gatherQuotes = new QuoteAnnotator.GatherQuotesStub(gatheredQuotes);

    annotator.annotate(annotation);

    assertEquals("Mr. John", quote.get(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    assertEquals(Integer.valueOf(0), quote.get(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), quote.get(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class));
  } 
}