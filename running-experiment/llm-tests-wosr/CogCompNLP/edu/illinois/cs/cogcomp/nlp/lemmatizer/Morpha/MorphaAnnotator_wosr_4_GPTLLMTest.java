public class MorphaAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testAnnotateWithSimpleNoun() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "dogs");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("dog", lemma);
  }
@Test
  public void testAnnotateWithSimpleVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
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

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run", lemma);
  }
@Test
  public void testAnnotateWithPhrasalVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "look_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("look_up", lemma);
  }
@Test
  public void testAnnotateWithNonPhrasalVerbUnderscoreButNotParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_fast");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run_fast", lemma);
  }
@Test
  public void testAnnotateWithEmptyPOSTag() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "fought");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("fought", lemma);
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateMissingSentencesAnnotationThrowsException() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);
  }
@Test
  public void testRequirements() {
    MorphaAnnotator annotator = new MorphaAnnotator();
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied() {
    MorphaAnnotator annotator = new MorphaAnnotator();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateMultipleTokens() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "cats");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "were");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("cat", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("be", token2.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateWithVerbNotStartingWithVB() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_out");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("run_out", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateSentenceWithNoTokens() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateWithVerbUnderscoreMultiPartShouldReturnNull() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "break_it_down");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("break_it_down", token.get(CoreAnnotations.LemmaAnnotation.class));
  } 
}