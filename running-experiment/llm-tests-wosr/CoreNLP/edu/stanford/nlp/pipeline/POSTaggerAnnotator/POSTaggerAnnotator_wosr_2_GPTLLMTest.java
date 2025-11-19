public class POSTaggerAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorWithDefaultModel() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithVerboseFlag() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(true);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithModelPathVerboseMaxLengthThreads() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(MaxentTagger.DEFAULT_JAR_PATH, false, 100, 1);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithMaxentTagger() {
    MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithModelAndParams() {
    MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 200, 2);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithProperties() {
    Properties props = new Properties();
    props.setProperty("pos.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("pos.verbose", "true");
    props.setProperty("pos.maxlen", "123");
    props.setProperty("pos.nthreads", "2");
    props.setProperty("pos.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateSingleThreadedTagging() {
    MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setValue("This");
    token1.setWord("This");

    CoreLabel token2 = new CoreLabel();
    token2.setValue("is");
    token2.setWord("is");

    CoreLabel token3 = new CoreLabel();
    token3.setValue("test");
    token3.setWord("test");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);
    Annotation document = new Annotation("This is test.");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);

    for (CoreLabel token : tokens) {
      assertNotNull(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    }
  }
@Test
  public void testAnnotateMultiThreadedTagging() {
    MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 2);

    CoreLabel token1 = new CoreLabel();
    token1.setValue("This");
    token1.setWord("This");

    CoreLabel token2 = new CoreLabel();
    token2.setValue("works");
    token2.setWord("works");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);
    Annotation doc = new Annotation("This works.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);

    for (CoreLabel token : tokens) {
      assertNotNull(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    }
  }
@Test
  public void testAnnotateSkipsLongSentence() {
    int maxLen = 1;
    MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, maxLen, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setValue("This");
    token1.setWord("This");

    CoreLabel token2 = new CoreLabel();
    token2.setValue("is");
    token2.setWord("is");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);
    Annotation document = new Annotation("This is skipped.");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateThrowsOnMissingSentences() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(MaxentTagger.DEFAULT_JAR_PATH, false, 100, 1);
    Annotation document = new Annotation("No sentences");
    annotator.annotate(document);
  }
@Test
  public void testRequires() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(MaxentTagger.DEFAULT_JAR_PATH, false, 100, 1);
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(MaxentTagger.DEFAULT_JAR_PATH, false, 100, 1);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggingHandlesEmptyTokens() {
    MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);
    Annotation doc = new Annotation("Empty sentence.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testTaggingEdgeCaseSingleToken() {
    MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Hello");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);
    Annotation doc = new Annotation("Hello.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);

    String tag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
    assertNotNull(tag);
  } 
}