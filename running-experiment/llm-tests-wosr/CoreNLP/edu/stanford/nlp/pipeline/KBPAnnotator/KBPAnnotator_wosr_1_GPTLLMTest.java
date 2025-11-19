public class KBPAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testKBPAnnotatorInitializationWithDefaultModel() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbpAnnotator = new KBPAnnotator(props);
    Assert.assertNotNull(kbpAnnotator.extractor);
    Assert.assertEquals(LanguageInfo.HumanLanguage.ENGLISH, kbpAnnotator.kbpLanguage);
  }
@Test
  public void testKBPAnnotatorSpanishCorefInitialization() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "es");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Assert.assertEquals(LanguageInfo.HumanLanguage.SPANISH, annotator.kbpLanguage);
    Assert.assertNotNull(annotator.spanishCorefSystem);
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    Assert.assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testRequires() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    Assert.assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    Assert.assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    Assert.assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testConvertRelationNameToLatest_knownKey() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    String converted = annotator.convertRelationNameToLatest("org:top_members/employees");
    Assert.assertEquals("org:top_members_employees", converted);
  }
@Test
  public void testConvertRelationNameToLatest_unknownKey() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    String relation = "per:age";
    String converted = annotator.convertRelationNameToLatest(relation);
    Assert.assertEquals(relation, converted);
  }
@Test
  public void testAnnotateAnnotationNoCorefNoMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("Barack Obama was born in Hawaii.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel label1 = new CoreLabel();
    label1.setWord("Barack");
    label1.setNER("PERSON");
    label1.setIndex(1);
    label1.setSentIndex(0);
    tokens.add(label1);

    CoreLabel label2 = new CoreLabel();
    label2.setWord("Obama");
    label2.setNER("PERSON");
    label2.setIndex(2);
    label2.setSentIndex(0);
    tokens.add(label2);

    CoreMap sentence = new Annotation("Barack Obama was born in Hawaii.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);
    List<RelationTriple> result = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);

    Assert.assertNotNull(result);
    Assert.assertTrue(result.isEmpty());
  }
@Test
  public void testCorefChainToKBPMentionsEmptyChain() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("Text");
    CoreMap sentence = new Annotation("Text");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<CorefMention> mentions = new ArrayList<>();
    Map<IntPair, CorefMention> mentionMap = new HashMap<>();
    CorefChain corefChain = new CorefChain(1, mentions, mentionMap);
    HashMap<Pair<Integer, Integer>, CoreMap> kbpMentions = new HashMap<>();

    Pair<List<CoreMap>, CoreMap> output = annotator.corefChainToKBPMentions(corefChain, annotation, kbpMentions);
    Assert.assertEquals(0, output.first.size());
    Assert.assertNull(output.second);
  }
@Test
  public void testAnnotatorAnnotateSkipsWhenNoNEROrMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("Text");

    CoreMap sentence = new Annotation("Text");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);
    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    Assert.assertNotNull(triples);
    Assert.assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotatorHandlesMaxLengthProperly() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.maxlen", "2");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("too long");

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setIndex(1);
    token1.setSentIndex(0);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    token2.setIndex(2);
    token2.setSentIndex(0);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Extra");
    token3.setIndex(3);
    token3.setSentIndex(0);
    tokens.add(token3);

    CoreMap sentence = new Annotation("Hello World Extra");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);

    Assert.assertTrue(triples.isEmpty());
  } 
}