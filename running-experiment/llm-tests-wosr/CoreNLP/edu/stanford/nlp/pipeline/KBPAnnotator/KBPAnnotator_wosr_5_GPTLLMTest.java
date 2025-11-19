public class KBPAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testEmptyAnnotationProducesNoTriples() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    Annotation ann = new Annotation("John works at Google.");
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("John works at Google.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.IndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());
    sentences.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotator.annotate(ann);
    List<RelationTriple> triples =
        sentences.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    Assert.assertNotNull(triples);
    Assert.assertTrue(triples.isEmpty());
  }
@Test
  public void testCorefChainToKBPMentionsWithPerfectMatch() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    Annotation document = new Annotation("This is a test.");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.setIndex(1);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("John is great");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreMap kbpMention = new Annotation("John");
    kbpMention.set(CoreAnnotations.TextAnnotation.class, "John");
    kbpMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    kbpMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<Pair<Integer, Integer>, CoreMap> kbpMentions = new HashMap<>();
    kbpMentions.put(new Pair<>(0, 4), kbpMention);

    CorefChain.CorefMention mention = new CorefChain.CorefMention(1, 1, 0, 1, 2, "John", true);
    List<CorefChain.CorefMention> corefMentions = new ArrayList<>();
    corefMentions.add(mention);

    CorefChain chain = new CorefChain(1, corefMentions);
    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, document, kbpMentions);

    Assert.assertNotNull(result);
    Assert.assertEquals(1, result.first.size());
    Assert.assertEquals(kbpMention, result.first.get(0));
    Assert.assertEquals(kbpMention, result.second);
  }
@Test
  public void testConvertRelationNameToLatestReturnsUpdated() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    String oldRel = "org:dissolved";
    try {
      java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod("convertRelationNameToLatest", String.class);
      method.setAccessible(true);
      Object newRel = method.invoke(annotator, oldRel);
      Assert.assertEquals("org:date_dissolved", newRel);
    } catch (Exception e) {
      Assert.fail("Reflection failed: " + e.getMessage());
    }
  }
@Test
  public void testAcronymMatchingBetweenMentions() {
    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();

    CoreMap orgMention = new Annotation("American Civil Liberties Union");
    List<CoreLabel> orgTokens = new ArrayList<>();
    CoreLabel aclu1 = new CoreLabel();
    aclu1.setWord("American");
    CoreLabel aclu2 = new CoreLabel();
    aclu2.setWord("Civil");
    CoreLabel aclu3 = new CoreLabel();
    aclu3.setWord("Liberties");
    CoreLabel aclu4 = new CoreLabel();
    aclu4.setWord("Union");
    orgTokens.add(aclu1);
    orgTokens.add(aclu2);
    orgTokens.add(aclu3);
    orgTokens.add(aclu4);
    orgMention.set(CoreAnnotations.TokensAnnotation.class, orgTokens);
    orgMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    orgMention.set(CoreAnnotations.TextAnnotation.class, "American Civil Liberties Union");

    CoreMap acronym = new Annotation("ACLU");
    acronym.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    acronym.set(CoreAnnotations.TextAnnotation.class, "ACLU");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(orgMention);
    mentions.add(acronym);

    KBPAnnotator.acronymMatch(mentions, mentionsMap);

    Assert.assertTrue(mentionsMap.containsKey(acronym));
    Set<CoreMap> cluster = mentionsMap.get(acronym);
    Assert.assertTrue(cluster.contains(acronym));
    Assert.assertTrue(cluster.contains(orgMention));
  }
@Test
  public void testKbpIsPronominalMentionRecognized() {
    CoreLabel token = new CoreLabel();
    token.setWord("he");

    try {
      java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod("kbpIsPronominalMention", CoreLabel.class);
      method.setAccessible(true);
      boolean result = (Boolean) method.invoke(null, token);
      Assert.assertTrue(result);
    } catch (Exception e) {
      Assert.fail("Reflection failed: " + e.getMessage());
    }
  }
@Test
  public void testRequirementsSatisfiedIncludesKBPTriples() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    Assert.assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testRequiresIncludesTokensAnnotations() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
    Assert.assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    Assert.assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    Assert.assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testNoModelPathStillCreatesExtractor() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    Assert.assertNotNull(annotator.extractor);
  }
@Test
  public void testInvalidModelThrowsRuntimeIOException() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "invalid_model_path");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    boolean exceptionCaught = false;
    try {
      new KBPAnnotator("kbp", props);
    } catch (RuntimeException e) {
      exceptionCaught = true;
    }
    Assert.assertTrue(exceptionCaught);
  }
@Test
  public void testSpanishCorefSystemInitializedWithSpanishLocale() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "es");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    Assert.assertNotNull("Spanish coref system should be initialized", annotator.spanishCorefSystem);
  } 
}