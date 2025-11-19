public class CorefMentionAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testSynchMentionsExactMatchWithoutTitle() {
    Annotation annotation = new Annotation("President Joe Smith's statement");
    
    List<CoreLabel> sentenceTokens = new ArrayList<>();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");
    
    sentenceTokens.add(token1);
    sentenceTokens.add(token2);
    sentenceTokens.add(token3);
    
    CoreMap sentence = new TypesafeMap.SimpleCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    
    Mention cm = new Mention(0, 2, 0);
    cm.endIndex = 3;
    
    List<CoreLabel> emTokens = new ArrayList<>();
    emTokens.add(token1);
    emTokens.add(token2);
    
    CoreMap em = new TypesafeMap.SimpleCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    
    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    
    assertTrue(result);
  }
@Test
  public void testSynchMentionsMismatchTokens() {
    Annotation annotation = new Annotation("President Joe's car");
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    
    CoreLabel token2 = new CoreLabel();
    token2.setWord("'s");
    
    CoreLabel token3 = new CoreLabel();
    token3.setWord("car");
    
    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2, token3);
    
    CoreMap sentence = new TypesafeMap.SimpleCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    
    Mention cm = new Mention(0, 2, 0);
    cm.endIndex = 3;
    
    CoreLabel emToken = new CoreLabel();
    emToken.setWord("Joe");
    
    CoreMap em = new TypesafeMap.SimpleCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(emToken));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    
    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    
    assertFalse(result);
  }
@Test
  public void testSynchMentionsWithTitleIgnored() {
    Annotation annotation = new Annotation("President Joe Smith");
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    
    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2, token3);
    
    CoreMap sentence = new TypesafeMap.SimpleCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    
    Mention cm = new Mention(0, 0, 0);
    cm.startIndex = 0;
    cm.endIndex = 3;
    
    List<CoreLabel> emTokens = Arrays.asList(token2, token3);
    CoreMap em = new TypesafeMap.SimpleCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    
    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    
    assertTrue(result);
  }
@Test
  public void testSynchMentionsNoOverlap() {
    Annotation annotation = new Annotation("Random unrelated mention");
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    
    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2, token3);
    
    CoreMap sentence = new TypesafeMap.SimpleCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    
    Mention cm = new Mention(0, 0, 0);
    cm.startIndex = 0;
    cm.endIndex = 3;
    
    CoreLabel emToken = new CoreLabel();
    emToken.setWord("Obama");
    
    CoreMap em = new TypesafeMap.SimpleCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(emToken));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    
    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    
    assertFalse(result);
  }
@Test
  public void testSynchMentionsTrailingPossessiveAllowed() {
    Annotation annotation = new Annotation("Joe Smith's");
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");
    
    List<CoreLabel> sentenceTokens = Arrays.asList(token1, token2, token3);
    
    CoreMap sentence = new TypesafeMap.SimpleCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    
    Mention cm = new Mention(0, 0, 0);
    cm.startIndex = 0;
    cm.endIndex = 3;
    
    CoreMap em = new TypesafeMap.SimpleCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    
    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    
    assertTrue(result);
  }
@Test
  public void testSynchMentionsEmptyOverlap() {
    Annotation annotation = new Annotation("Some content here");
    
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("Some");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("content");
    
    tokens.add(t1);
    tokens.add(t2);
    
    CoreMap sentence = new TypesafeMap.SimpleCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    
    Mention cm = new Mention(0, 0, 0);
    cm.startIndex = 0;
    cm.endIndex = 1;
    
    CoreMap em = new TypesafeMap.SimpleCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    
    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    
    assertFalse(result);
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    
    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));
  }
@Test
  public void testRequiresNotNull() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "hybrid");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertNotNull(required);
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testConstructDependencyMentionFinder() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "dependency");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    
    assertNotNull(annotator.requires());
    assertTrue(annotator.requires().contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testConstructRuleMentionFinder() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.EndIndexAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.BeginIndexAnnotation.class));
  } 
}