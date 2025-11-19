public class NERCombinerAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructorWithDefaultProperties() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithSpanishLanguage() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNEROnSimpleEnglishSentence() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.IndexAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token2.set(CoreAnnotations.AfterAnnotation.class, "");
    token2.set(CoreAnnotations.IndexAnnotation.class, 1);

    List<CoreLabel> tokens = Arrays.asList(token, token2);
    CoreMap sentence = new Annotation("Barack Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Barack Obama");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("PERSON", resultTokens.get(0).ner());
    assertEquals("PERSON", resultTokens.get(1).ner());
  }
@Test
  public void testNEROnEmptyAnnotation() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation emptyAnnotation = new Annotation("");
    emptyAnnotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    emptyAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(emptyAnnotation);

    assertTrue(emptyAnnotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testDoOneFailedSentenceAddsBackgroundNER() throws Exception {
    NERClassifierCombiner combiner = new NERClassifierCombiner(new Properties()) {
      @Override
      public String backgroundSymbol() {
        return "O";
      }
    };

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(combiner, false);

    CoreLabel token = new CoreLabel();
    token.setWord("Unknown");
    token.setNER(null);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation("Unknown");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Unknown");
    annotator.doOneFailedSentence(annotation, sentence);

    assertEquals("O", tokens.get(0).ner());
  }
@Test
  public void testTransferNERAnnotationsFromMergedToken() {
    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setWord("preexisting");
    mergedToken.setNER("DISEASE");
    mergedToken.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DISEASE", 0.95));
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    CoreMap nerSentence = new Annotation("pre existing");
    nerSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(mergedToken));
    nerSentence.set(CoreAnnotations.TextAnnotation.class, "pre existing");
    List<CoreMap> nerSentences = Collections.singletonList(nerSentence);
    Annotation nerAnnotation = new Annotation("pre existing");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(mergedToken));
    nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, nerSentences);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("pre");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("existing");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap origSentence = new Annotation("pre existing");
    origSentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    origSentence.set(CoreAnnotations.TextAnnotation.class, "pre existing");
    Annotation originalAnnotation = new Annotation("pre existing");
    originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(origSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);

    assertEquals("DISEASE", token1.ner());
    assertEquals("DISEASE", token2.ner());
  }
@Test
  public void testAfterIsEmptyTrueWhenEmptyAfterAnnotation() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "");
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    assertTrue(result);
  }
@Test
  public void testAfterIsEmptyFalseWhenAfterAnnotationIsSpace() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    assertFalse(result);
  } 
}