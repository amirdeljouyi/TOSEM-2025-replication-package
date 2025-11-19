public class KBPAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testKBPAnnotatorInitializeWithValidProperties() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    assertNotNull(annotator);
    assertNotNull(annotator.extractor);
    assertTrue(annotator.requirementsSatisfied().contains(CoreAnnotations.KBPTriplesAnnotation.class));
    assertTrue(annotator.requires().contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testAnnotateEmptyAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("John works at Google.");

    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("John works at Google.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());
    sentences.add(sentence);
    annotation.set(SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> processedSentences = annotation.get(SentencesAnnotation.class);
    assertEquals(1, processedSentences.size());

    List<RelationTriple> triples = processedSentences.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(0, triples.size());
  }
@Test
  public void testAnnotateWithNoNERMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("He went to the university.");

    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("He went to the university.");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("He");
    token1.setIndex(1);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("went");
    token2.setIndex(2);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token2);

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());
    sentences.add(sentence);
    annotation.set(SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> processedSentences = annotation.get(SentencesAnnotation.class);
    assertEquals(1, processedSentences.size());

    List<RelationTriple> triples = processedSentences.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(0, triples.size());
  }
@Test
  public void testAcronymResolutionWithNoMentionMatch() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("IBM is an acronym. International Business Machine is a full form.");

    List<CoreMap> sentences = new ArrayList<>();

    CoreMap sentence1 = new Annotation("IBM is an acronym.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("IBM");
    token1.setIndex(1);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreMap mention1 = new Annotation("IBM");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    mention1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    sentence1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    sentence1.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1));
    sentence1.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());
    sentences.add(sentence1);

    CoreMap sentence2 = new Annotation("International Business Machine is a full form.");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("International");
    token2.setIndex(1);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    sentence2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
    sentence2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence2.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());
    sentences.add(sentence2);

    annotation.set(SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> processedSentences = annotation.get(SentencesAnnotation.class);
    assertEquals(2, processedSentences.size());

    List<RelationTriple> triples1 = processedSentences.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples1);

    List<RelationTriple> triples2 = processedSentences.get(1).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples2);
  }
@Test
  public void testAnnotatorSatisfiesRequirements() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testAnnotatorDeclaredRequires() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("This is a simple sentence.");
    CoreMap sentence = new Annotation("This is a simple sentence.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);
    List<CoreMap> resultSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

    assertNotNull(resultSentences);
    assertEquals(1, resultSentences.size());
    List<RelationTriple> triples = resultSentences.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(0, triples.size());
  } 
}