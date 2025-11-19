public class QuoteAttributionAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructorWithValidProperties() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "edu/stanford/nlp/models/quoteattribution/example_characters.tsv");
    props.setProperty("booknlpCoref", "path/to/example/coref.txt");
    props.setProperty("modelPath", "edu/stanford/nlp/models/quoteattribution/quoteattribution_model.ser");
    props.setProperty("familyWordsFile", "edu/stanford/nlp/models/quoteattribution/family_words.txt");
    props.setProperty("animacyWordsFile", "edu/stanford/nlp/models/quoteattribution/animate.unigrams.txt");
    props.setProperty("genderNamesFile", "edu/stanford/nlp/models/quoteattribution/gender_filtered.txt");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testEntityMentionsToCharacterMapAddsPerson() {
    Annotation annotation = new Annotation("John walked.");
    CoreMap mention = new Annotation("John");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(new Properties());
    annotator.entityMentionsToCharacterMap(annotation);
    assertEquals(1, annotator.characterMap.size());
    assertTrue(annotator.characterMap.containsKey("John"));
  }
@Test
  public void testEntityMentionsToCharacterMapSkipsNonPerson() {
    Annotation annotation = new Annotation("Paris is beautiful.");
    CoreMap mention = new Annotation("Paris");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(new Properties());
    annotator.entityMentionsToCharacterMap(annotation);
    assertTrue(annotator.characterMap.isEmpty());
  }
@Test
  public void testAnnotateDoesNotThrowWithMinimalAnnotation() {
    Annotation annotation = new Annotation("“Hi,” said John.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hi");
    token.set(CoreAnnotations.IndexAnnotation.class, 0);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "UH");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token);
    annotation.set(CoreAnnotations.TextAnnotation.class, "“Hi,” said John.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.PartOfSpeechAnnotation.class, "UH");

    Properties props = new Properties();
    props.setProperty("charactersPath", "edu/stanford/nlp/models/quoteattribution/sample.tsv");
    props.setProperty("booknlpCoref", "sample/coref.txt");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    annotator.annotate(annotation);
    assertTrue(annotation.containsKey(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(new Properties());
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphIndexAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
  }
@Test
  public void testRequiresContainsCoreNLPAnnotations() {
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(new Properties());
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testBuildCharacterMapIsTrueWhenCharactersFileNull() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertTrue(annotator.buildCharacterMapPerAnnotation);
  }
@Test
  public void testUseCorefDefaultTrueUnlessOverridden() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertTrue(annotator.useCoref);

    Properties disabledProps = new Properties();
    disabledProps.setProperty("useCoref", "false");
    QuoteAttributionAnnotator disabledAnnotator = new QuoteAttributionAnnotator(disabledProps);
    assertFalse(disabledAnnotator.useCoref);
  }
@Test
  public void testQMMappingIncludesAllExpectedSieves() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation doc = new Annotation("“Hello,” said Alice.");
    Map<Integer, String> corefMap = new HashMap<>();
    Map<String, edu.stanford.nlp.quoteattribution.Sieves.QMSieves.QMSieve> qmsieves =
        annotator.getQMMapping(doc, corefMap);

    assertTrue(qmsieves.containsKey("tri"));
    assertTrue(qmsieves.containsKey("dep"));
    assertTrue(qmsieves.containsKey("onename"));
    assertTrue(qmsieves.containsKey("voc"));
    assertTrue(qmsieves.containsKey("paraend"));
    assertTrue(qmsieves.containsKey("conv"));
    assertTrue(qmsieves.containsKey("sup"));
    assertTrue(qmsieves.containsKey("loose"));
    assertTrue(qmsieves.containsKey("closest"));
  }
@Test
  public void testMSMappingIncludesAllExpectedSieves() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation doc = new Annotation("“Hello,” said Alice.");
    Map<Integer, String> corefMap = new HashMap<>();
    Map<String, edu.stanford.nlp.quoteattribution.Sieves.MSSieves.MSSieve> mssieves =
        annotator.getMSMapping(doc, corefMap);

    assertTrue(mssieves.containsKey("det"));
    assertTrue(mssieves.containsKey("top"));
    assertTrue(mssieves.containsKey("maj"));
    assertTrue(mssieves.containsKey("loose"));
  }
@Test
  public void testCanonicalMentionIsSetWhenSpeakerTokenMapped() {
    Annotation annotation = new Annotation("“Hello,” said Alice.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Alice");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    tokens.add(token);
    annotation.set(CoreAnnotations.TextAnnotation.class, "“Hello,” said Alice.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap mention = new Annotation("Alice");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 0);
    List<CoreLabel> mentionTokens = new ArrayList<>();
    CoreLabel mt = new CoreLabel();
    mt.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mentionTokens.add(mt);
    mention.set(CoreAnnotations.TokensAnnotation.class, mentionTokens);

    List<CoreMap> entityMentions = new ArrayList<>();
    entityMentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentions);

    CoreMap quote = new Annotation("“Hello,”");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    List<CoreMap> gatheredQuotes = new ArrayList<>();
    gatheredQuotes.add(quote);
    QuoteAnnotator mockQuoteAnnotator = new QuoteAnnotator() {
      public static List<CoreMap> gatherQuotes(Annotation ignored) {
        List<CoreMap> quotes = new ArrayList<>();
        CoreMap q = new Annotation("Mock Quote");
        q.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);
        quotes.add(q);
        return quotes;
      }
    };

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(new Properties());
    annotator.annotate(annotation);
    List<CoreMap> finalQuotes = QuoteAnnotator.gatherQuotes(annotation);
    for (CoreMap q : finalQuotes) {
      assertNotNull(q.get(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    }
  } 
}