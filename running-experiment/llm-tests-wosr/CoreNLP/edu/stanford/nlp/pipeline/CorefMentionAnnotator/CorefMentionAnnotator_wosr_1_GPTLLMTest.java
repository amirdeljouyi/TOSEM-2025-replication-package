public class CorefMentionAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testRequiresShouldContainExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.EntityTypeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.IndexAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.ValueAnnotation.class));
    assertTrue(requirements.contains(CorefMentionAnnotatorTest.class.getPackage()
        .getAnnotation(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class)));
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertEquals(0, annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).size());
    assertNotNull(annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class));
    assertNotNull(annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class));
  }
@Test
  public void testSynchCorefMentionEntityMentionTrueMatch() {
    Annotation annotation = new Annotation("This is a test.");
    List<CoreMap> sentences = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");

    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(token2); 
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Mention mention = new Mention(0, 0, 2, 0, 0, 0, 0, null);

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMentionFalseMismatchDueToExtraTokens() {
    Annotation annotation = new Annotation("Mismatch test.");
    List<CoreMap> sentences = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");

    CoreLabel token4 = new CoreLabel();
    token4.setWord("car");

    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2, token3, token4);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(token2); 
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Mention mention = new Mention(0, 1, 4, 0, 0, 0, 0, null); 

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    assertFalse(result);
  }
@Test
  public void testSynchCorefMentionEntityMentionTokenMismatch() {
    Annotation annotation = new Annotation("Wrong match.");
    List<CoreMap> sentences = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Doe");

    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreLabel wrongToken = new CoreLabel();
    wrongToken.setWord("Jane");

    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(wrongToken);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Mention mention = new Mention(0, 0, 1, 0, 0, 0, 0, null);

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);

    assertFalse(result);
  }
@Test
  public void testSynchCorefMentionEntityMentionAllowTrailingPossessive() {
    Annotation annotation = new Annotation("Allow 's");
    List<CoreMap> sentences = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Smith");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("'s");

    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(token1);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Mention mention = new Mention(0, 0, 2, 0, 0, 0, 0, null);

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);

    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMentionNoTokensShouldReturnFalse() {
    Annotation annotation = new Annotation("Empty");
    List<CoreMap> sentences = new ArrayList<>();

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Mention mention = new Mention(0, 0, 0, 0, 0, 0, 0, null);

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);

    assertFalse(result);
  } 
}