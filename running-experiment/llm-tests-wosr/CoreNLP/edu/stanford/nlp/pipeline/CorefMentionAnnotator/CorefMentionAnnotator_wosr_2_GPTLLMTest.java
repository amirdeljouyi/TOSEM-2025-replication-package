public class CorefMentionAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorWithValidProperties() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    assertNotNull(annotator);
    Set<Class<? extends CoreAnnotations.Annotation>> annReqs = annotator.requires();
    assertTrue(annReqs.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(annReqs.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testRequiresNotEmpty() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotations.Annotation>> required = annotator.requires();
    assertFalse(required.isEmpty());
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedClasses() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotations.Annotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
  }
@Test
  public void testAnnotateWithSimpleMentionMapping() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("removeNestedMentions", "true");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Annotation annotation = new Annotation("Joe Smith went home.");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    token1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    token2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("went");
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("home");
    tokens.add(token4);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    tokens.add(token5);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Mention mention = new Mention(0, 0, 2, 0); 
    List<Mention> mentionList = Collections.singletonList(mention);
    sentence.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentionList);
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentionList);

    Map<Integer, Integer> entityMapping = new HashMap<>();
    entityMapping.put(0, 0);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens.subList(0, 2));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> entityMentionsList = new ArrayList<>();
    entityMentionsList.add(entityMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentionsList);

    token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    token2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    ArraySet<Integer> mentionIdxSet = new ArraySet<>();
    mentionIdxSet.add(0);
    token1.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, mentionIdxSet);
    token2.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, mentionIdxSet);

    annotator.annotate(annotation);

    Map<Integer, Integer> resultCorefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
    assertTrue(resultCorefToEntity.containsKey(0));
    assertEquals(Integer.valueOf(0), resultCorefToEntity.get(0));
  }
@Test
  public void testSynchCorefMentionEntityMention_TitleSkipped() {
    Annotation annotation = new Annotation("President Joe Smith");

    CoreLabel token0 = new CoreLabel();
    token0.setWord("President");
    token0.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention cm = new Mention(0, 0, 3, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, tokens.subList(1, 3));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMention_WithPossessive() {
    Annotation annotation = new Annotation("Joe Smith's");

    CoreLabel token0 = new CoreLabel();
    token0.setWord("Joe");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Smith");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("'s");

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention cm = new Mention(0, 0, 3, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, tokens.subList(0, 2));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMention_TokenMismatch() {
    Annotation annotation = new Annotation("Joe Smith and Mary");

    CoreLabel token0 = new CoreLabel();
    token0.setWord("Joe");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Smith");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("and");

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Mention cm = new Mention(0, 0, 2, 0);
    cm.sentNum = 0;

    CoreLabel mismatchToken = new CoreLabel();
    mismatchToken.setWord("Jane");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(mismatchToken));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    assertFalse(result);
  }
@Test
  public void testSynchCorefMentionEntityMention_EntityTokenLongerThanMention() {
    Annotation annotation = new Annotation("Joe");

    CoreLabel token0 = new CoreLabel();
    token0.setWord("Joe");

    List<CoreLabel> tokens = Collections.singletonList(token0);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    Mention cm = new Mention(0, 0, 1, 0);
    cm.sentNum = 0;

    CoreLabel tokenLong = new CoreLabel();
    tokenLong.setWord("Joe");
    CoreLabel tokenExtra = new CoreLabel();
    tokenExtra.setWord("Smith");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(tokenLong, tokenExtra));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    assertFalse(result);
  } 
}