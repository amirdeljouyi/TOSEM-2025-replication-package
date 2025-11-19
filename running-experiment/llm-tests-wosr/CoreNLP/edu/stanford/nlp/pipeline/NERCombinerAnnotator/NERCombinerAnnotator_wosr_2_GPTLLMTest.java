public class NERCombinerAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructor_DefaultModelsWithDefaults() throws IOException {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructor_WithRulesOnlyTrue() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.rulesOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructor_WithStatisticalOnlyTrue() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.statisticalOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructor_WithSpanishLanguage() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotationWorkflow_WithSimpleEnglishSentence() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.language", "en");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setValue("Barack");
    token.setOriginalText("Barack");
    token.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.LemmaAnnotation.class, "Barack");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Barack");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("Barack");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);
    assertEquals("PERSON", tokens.get(0).ner());
  }
@Test
  public void testAnnotationWithUseNERSpecificTokenizationFalse() throws IOException {
    NERClassifierCombiner combiner = new NERClassifierCombiner(new Properties());
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(combiner, false);
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("state");
    token1.setOriginalText("state");
    token1.set(CoreAnnotations.TextAnnotation.class, "state");
    token1.set(CoreAnnotations.BeforeAnnotation.class, "");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token1.set(CoreAnnotations.LemmaAnnotation.class, "state");
    token1.set(CoreAnnotations.IndexAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    CoreMap sentence = new Annotation("state");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("state");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);
    assertNotNull(tokens.get(0).ner());
  }
@Test
  public void testDoOneFailedSentence_SetsNERBackground() throws IOException {
    NERClassifierCombiner combiner = new NERClassifierCombiner(new Properties());
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(combiner, false);

    CoreLabel token = new CoreLabel();
    token.setWord("X");
    token.set(CoreAnnotations.TextAnnotation.class, "X");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("X");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("X");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.doOneFailedSentence(annotation, sentence);

    assertNotNull(tokens.get(0).ner());
    assertEquals(combiner.backgroundSymbol(), tokens.get(0).ner());
  }
@Test
  public void testTransferNERAnnotationsToAnnotation_WithMergedTokens() {
    CoreLabel originalToken1 = new CoreLabel();
    originalToken1.setWord("New");
    CoreLabel originalToken2 = new CoreLabel();
    originalToken2.setWord("York");
    List<CoreLabel> originalTokens = new ArrayList<>();
    originalTokens.add(originalToken1);
    originalTokens.add(originalToken2);

    CoreMap originalSentence = new Annotation("New York");
    originalSentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    Annotation originalAnnotation = new Annotation("New York");
    originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(originalSentence));

    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setWord("NewYork");
    mergedToken.setNER("LOCATION");
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);
    List<CoreLabel> nerTokens = new ArrayList<>();
    nerTokens.add(mergedToken);

    CoreMap nerSentence = new Annotation("New York");
    nerSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    Annotation nerAnnotation = new Annotation("New York");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(nerSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);

    assertEquals("LOCATION", originalTokens.get(0).ner());
    assertEquals("LOCATION", originalTokens.get(1).ner());
  }
@Test
  public void testRequirementsSatisfied_ContainsNERKeys() throws IOException {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotations>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
  }
@Test
  public void testRequires_ContainsProperDependencies() throws IOException {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotations>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  } 
}