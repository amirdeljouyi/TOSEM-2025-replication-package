public class MorphaAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testAnnotateWithValidSingleTokenSentence() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = Arrays.asList(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run", lemma);
  }
@Test
  public void testAnnotateWithMissingPOS() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "cats");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = Arrays.asList(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("cat", lemma);
  }
@Test
  public void testAnnotateWithPhrasalVerb() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "took_off");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = Arrays.asList(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("take_off", lemma);
  }
@Test
  public void testAnnotateWithUnsupportedPhrasalVerbForm() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_magic");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = Arrays.asList(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("ran_magic", lemma);
  }
@Test
  public void testAnnotationWithoutSentencesThrowsException() {
    Annotation annotation = new Annotation("");
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException due to missing sentence annotations");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unable to find words/tokens"));
    }
  }
@Test
  public void testAnnotateWithMultipleTokens() {
    Annotation annotation = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "looked");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "at");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "IN");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = Arrays.asList(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("look", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("at", token2.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotatorRequirementsList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAnnotatorRequirementsSatisfied() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyTokenList() {
    Annotation annotation = new Annotation("");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);
    assertTrue(sentence.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateWithTokenWithEmptyPOSTag() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "leaves");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("leav", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateMultipleSentences() {
    Annotation annotation = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "walking");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "walked");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("walk", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("walk", token2.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithExtraUnderscoreIgnored() {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "take_off_now");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("took_off_now", lemma);
  } 
}