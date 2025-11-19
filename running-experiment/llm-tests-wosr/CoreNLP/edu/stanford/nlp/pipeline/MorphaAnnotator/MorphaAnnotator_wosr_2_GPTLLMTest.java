public class MorphaAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testAnnotateSingleTokenSentence() {
    Annotation annotation = new Annotation("Input");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("run", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateMultipleTokenSentence() {
    Annotation annotation = new Annotation("The quick brown foxes");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "foxes");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "run");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBP");
    
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(true);
    annotator.annotate(annotation);

    assertEquals("fox", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("run", token2.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateMissingPOSTag() {
    Annotation annotation = new Annotation("walk");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "walk");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(true);
    annotator.annotate(annotation);

    assertEquals("walk", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateMissingSentenceAnnotationThrows() {
    Annotation annotation = new Annotation("Hello world");

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);
  }
@Test
  public void testPhrasalVerbLemmatization_valid() {
    Annotation annotation = new Annotation("looked_up");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "looked_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(true);
    annotator.annotate(annotation);

    assertEquals("look_up", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbLemmatization_invalidParticle() {
    Annotation annotation = new Annotation("gave_way");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "gave_way");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(true);
    annotator.annotate(annotation);

    assertEquals("give", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testNonVerbUnderscoreWordNoPhrasalVerb() {
    Annotation annotation = new Annotation("non_phrasal");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "non_phrasal");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("non_phrasal", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContainsLemmaAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator();
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequiresIncludesNecessaryAnnotations() {
    MorphaAnnotator annotator = new MorphaAnnotator();
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyTokenList() {
    Annotation annotation = new Annotation("");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertTrue(sentence.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateNullPOSTag() {
    Annotation annotation = new Annotation("run");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator();
    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      
    }
  } 
}