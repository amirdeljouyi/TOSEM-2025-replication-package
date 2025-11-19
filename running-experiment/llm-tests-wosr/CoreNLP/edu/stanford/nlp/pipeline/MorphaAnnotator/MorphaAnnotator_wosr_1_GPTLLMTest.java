public class MorphaAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testSimpleLemmaVerb() {
    Annotation annotation = new Annotation("");
    
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run", lemma);
  }
@Test
  public void testSimpleLemmaNoun() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "batteries");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("battery", lemma);
  }
@Test
  public void testEmptyPartOfSpeechUsesStem() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("running", lemma); 
  }
@Test
  public void testPhrasalVerbRecognized() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "go_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("go_up", lemma);
  }
@Test
  public void testPhrasalVerbNotRecognizedInvalidParticle() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "go_xyz");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("went_xyz", lemma); 
  }
@Test
  public void testNoSentencesKeyThrowsException() {
    Annotation annotation = new Annotation("");

    MorphaAnnotator annotator = new MorphaAnnotator(false);

    boolean exceptionThrown = false;
    try {
      annotator.annotate(annotation);
    } catch (RuntimeException e) {
      exceptionThrown = true;
      assertTrue(e.getMessage().contains("Unable to find words/tokens"));
    }

    assertTrue(exceptionThrown);
  }
@Test
  public void testMultipleTokensInSentence() {
    Annotation annotation = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "dogs");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "were");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "running");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("dog", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("be", token2.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("run", token3.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testMultipleSentencesProcessedSeparately() {
    Annotation annotation = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "cats");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "jumped");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("cat", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("jump", token2.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequiresFields() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requires = annotator.requires();

    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedField() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
  } 
}