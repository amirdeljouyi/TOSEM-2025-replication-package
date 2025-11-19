public class CorefMentionAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testSynchCorefMentionEntityMentionExactMatch() {
    Annotation annotation = new Annotation("President Joe Smith");
    List<CoreMap> sentences = new ArrayList<>();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    tokens.add(token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention mention = new Mention(0, 0, 3);
    mention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(token2);
    entityMentionTokens.add(token3);
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMentionMismatchDueToExtraToken() {
    Annotation annotation = new Annotation("President Joe Smith");
    List<CoreMap> sentences = new ArrayList<>();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("car");
    tokens.add(token4);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention mention = new Mention(0, 0, 4); 
    mention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(token2);
    entityMentionTokens.add(token3);
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    assertFalse(result);
  }
@Test
  public void testSynchCorefMentionEntityMentionMatchWithPossessive() {
    Annotation annotation = new Annotation("President Joe Smith");
    List<CoreMap> sentences = new ArrayList<>();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("'s");
    tokens.add(token4);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention mention = new Mention(0, 0, 4);
    mention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(token2);
    entityMentionTokens.add(token3);
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMentionNoOverlap() {
    Annotation annotation = new Annotation("President Joe Smith");
    List<CoreMap> sentences = new ArrayList<>();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    tokens.add(token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention mention = new Mention(0, 3, 3); 
    mention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    assertFalse(result);
  }
@Test
  public void testAnnotatorRequirementsIncludesExpected() {
    Properties props = PropertiesUtils.asProperties("coref.algorithm", "statistical");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotations.Annotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesExpected() {
    Properties props = new Properties();
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotations.Annotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testConstructorThrowsRuntimeExceptionOnBadClass() {
    Properties props = new Properties();
    props.setProperty("coref.dict.path", "/this/path/does/not/exist");
    
    new CorefMentionAnnotator(props);
  }
@Test
  public void testSynchCorefMentionEntityMentionNonPersonType() {
    Annotation annotation = new Annotation("Stanford University");
    List<CoreMap> sentences = new ArrayList<>();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("University");
    tokens.add(token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention mention = new Mention(0, 0, 2);
    mention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    List<CoreLabel> entityMentionTokens = new ArrayList<>();
    entityMentionTokens.add(token1);
    entityMentionTokens.add(token2);
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityMentionTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    assertTrue(result);
  } 
}