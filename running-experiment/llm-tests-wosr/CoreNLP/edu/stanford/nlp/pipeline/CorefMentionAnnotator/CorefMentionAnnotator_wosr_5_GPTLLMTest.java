public class CorefMentionAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testSynchCorefMentionEntityMention_SameExactTokens() {
    Annotation ann = new Annotation("");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap cmSentence = new ArrayCoreMap();
    cmSentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

    Mention cm = new Mention(0, 0, 2, 0, 0, "Joe Smith", "Joe Smith", true, 0, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em));
  }
@Test
  public void testSynchCorefMentionEntityMention_PersonWithTitle() {
    Annotation ann = new Annotation("");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap cmSentence = new ArrayCoreMap();
    cmSentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

    Mention cm = new Mention(0, 0, 3, 0, 0, "President Joe Smith", "President Joe Smith", true, 0, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2, token3));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em));
  }
@Test
  public void testSynchCorefMentionEntityMention_TrailingPossessive() {
    Annotation ann = new Annotation("");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap cmSentence = new ArrayCoreMap();
    cmSentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

    Mention cm = new Mention(0, 0, 3, 0, 0, "Joe Smith's", "Joe Smith's", true, 0, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em));
  }
@Test
  public void testSynchCorefMentionEntityMention_TokenMismatch() {
    Annotation ann = new Annotation("");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Sam");

    List<CoreLabel> cmTokens = Arrays.asList(token1, token2);
    CoreMap cmSentence = new ArrayCoreMap();
    cmSentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

    Mention cm = new Mention(0, 0, 2, 0, 0, "Joe Smith", "Joe Smith", true, 0, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token3));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em));
  }
@Test
  public void testSynchCorefMentionEntityMention_NonPersonWithoutTitle() {
    Annotation ann = new Annotation("");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("University");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap cmSentence = new ArrayCoreMap();
    cmSentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

    Mention cm = new Mention(0, 0, 2, 0, 0, "Stanford University", "Stanford University", true, 0, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em));
  }
@Test
  public void testSynchCorefMentionEntityMention_ExcessTokenInEntityMention() {
    Annotation ann = new Annotation("");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    List<CoreLabel> cmTokens = Arrays.asList(token1, token2);
    CoreMap cmSentence = new ArrayCoreMap();
    cmSentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

    Mention cm = new Mention(0, 0, 2, 0, 0, "Joe Smith", "Joe Smith", true, 0, 0);
    cm.sentNum = 0;

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Jr.");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em));
  }
@Test
  public void testSynchCorefMentionEntityMention_EmptyTokenList() {
    Annotation ann = new Annotation("");
    CoreMap cmSentence = new ArrayCoreMap();
    cmSentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

    Mention cm = new Mention(0, 0, 0, 0, 0, "", "", true, 0, 0);
    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    em.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em));
  }
@Test
  public void testRequiresIncludesExpectedAnnotations() {
    Properties props = new Properties();
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesCorefMentions() {
    Properties props = new Properties();
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
  }
@Test(expected = RuntimeIOException.class)
  public void testConstructorThrowsIOExceptionAsExpected() {
    Properties props = new Properties();
    props.setProperty("coref.dictionaryPath", "invalid/path/should/fail");
    props.setProperty("coref.md.type", "dependency");

    
    new CorefMentionAnnotator(props);
  } 
}