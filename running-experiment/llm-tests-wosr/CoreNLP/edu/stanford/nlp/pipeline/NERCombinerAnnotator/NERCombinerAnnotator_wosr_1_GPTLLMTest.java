public class NERCombinerAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructorWithEmptyPropertiesShouldInitialize() throws IOException {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithRulesOnlyTrue() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.rulesOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithStatisticalOnlyTrue() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.statisticalOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithSUTimeEnabled() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.useSUTime", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testDefaultNERCombinerLegacyConstructor() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testBooleanNERCombinerLegacyConstructor() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(false);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerLegacyConstructorWithClassifiers() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(false, "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerWithVerboseAndThreadConfig() throws Exception {
    NERClassifierCombiner nerCombiner = new NERClassifierCombiner(new Properties());
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerCombiner, true, 2, 1000L);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerAnnotateSimpleAnnotation() throws Exception {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.LemmaAnnotation.class, "Stanford");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "Stanford");
    token.setIndex(1);
    token.setSentIndex(0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

    annotator.annotate(annotation);

    List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(resultTokens);
    assertEquals(1, resultTokens.size());
    assertNotNull(resultTokens.get(0).ner());
  }
@Test
  public void testAnnotationWithNERTokenizationMergesWithHyphen() throws Exception {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Chicago");
    token1.set(CoreAnnotations.AfterAnnotation.class, "");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.set(CoreAnnotations.AfterAnnotation.class, "");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Style");
    token3.set(CoreAnnotations.AfterAnnotation.class, " ");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    Annotation sentenceAnn = new Annotation("Chicago-Style");
    sentenceAnn.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<Annotation> sentences = Collections.singletonList(sentenceAnn);

    Annotation doc = new Annotation("Chicago-Style");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Annotation copy = NERCombinerAnnotator.annotationWithNERTokenization(doc);

    List<CoreLabel> copyTokens = copy.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(2, copyTokens.size());
    assertEquals("Chicago-", copyTokens.get(0).word().substring(0, 8));
    assertTrue(copyTokens.get(0).word().contains("Style"));
  }
@Test
  public void testTransferNERAnnotationsToAnnotationShouldTransfer() throws Exception {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Berlin");
    token1.setNER("LOCATION");
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("LOCATION", 0.9));
    List<CoreLabel> nerTokens = Collections.singletonList(token1);

    Annotation nerAnn = new Annotation("Berlin");
    nerAnn.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Berlin");
    List<CoreLabel> origTokens = Collections.singletonList(token2);

    Annotation origAnn = new Annotation("Berlin");
    origAnn.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    origAnn.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(
        new Annotation("Berlin") {{
          set(CoreAnnotations.TokensAnnotation.class, origTokens);
        }}
    ));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnn, origAnn);

    assertEquals("LOCATION", origTokens.get(0).ner());
    Map<String, Double> probs = origTokens.get(0).get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertNotNull(probs);
    assertTrue(probs.containsKey("LOCATION"));
    assertEquals(Double.valueOf(0.9), probs.get("LOCATION"));
  }
@Test
  public void testDoOneSentenceSetsNEROnLabel() throws Exception {
    NERClassifierCombiner nerCombiner = new NERClassifierCombiner(new Properties()) {
      @Override
      public List<CoreLabel> classifySentenceWithGlobalInformation(List<CoreLabel> tokens, Annotation document,
                                                                   CoreMap sentence) {
        for (CoreLabel token : tokens) {
          token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
        }
        return tokens;
      }
    };

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerCombiner, true);
    CoreLabel token = new CoreLabel();
    token.setWord("John");
    token.setSentIndex(0);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("John");
    CoreMap sentence = new Annotation("John");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.doOneSentence(ann, sentence);

    assertEquals("PERSON", tokens.get(0).ner());
  }
@Test
  public void testDoOneFailedSentenceSetsBackgroundNER() throws Exception {
    NERClassifierCombiner nerCombiner = new NERClassifierCombiner(new Properties()) {
      @Override
      public String backgroundSymbol() {
        return "O";
      }
    };

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerCombiner, false);
    CoreLabel token = new CoreLabel();
    token.setWord("Unknown");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Unknown");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("Unknown");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.doOneFailedSentence(ann, sentence);

    assertEquals("O", tokens.get(0).ner());
  }
@Test
  public void testRequirementsSatisfiedIncludesNERKeys() throws Exception {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
  }
@Test
  public void testRequiresIncludesTokenAnnotation() throws Exception {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
  } 
}