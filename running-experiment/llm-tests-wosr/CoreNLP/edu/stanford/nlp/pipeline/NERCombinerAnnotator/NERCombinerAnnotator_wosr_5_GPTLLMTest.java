public class NERCombinerAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testDefaultConstructor() throws IOException, ClassNotFoundException {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testPropertiesConstructorWithStatisticalOnly() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.statisticalOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testPropertiesConstructorWithRulesOnly() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.rulesOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testSpanishNumberAnnotatorInitialization() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotationTransferAfterNER() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setNER("PERSON");
    token.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.AfterAnnotation.class, "");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    tokens.add(token2);

    CoreMap sentence = new Annotation("Barack Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Barack Obama");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    String ner0 = tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class);
    String ner1 = tokens.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class);

    assertNotNull(ner0);
    assertNotNull(ner1);
  }
@Test
  public void testAnnotationWithEmptyText() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Annotation doc = new Annotation("");

    List<CoreMap> sentences = new ArrayList<>();
    Annotation emptySentence = new Annotation("");
    emptySentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentences.add(emptySentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(doc);

    assertTrue(doc.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testDoOneFailedSentenceAddsNER() throws IOException, ClassNotFoundException {
    NERClassifierCombiner nerCombiner = new NERClassifierCombiner(new Properties());
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerCombiner, false);

    CoreLabel token = new CoreLabel();
    token.setWord("unknown");
    token.setNER(null);

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap sentence = new Annotation("unknown");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.TextAnnotation.class, "unknown");

    Annotation doc = new Annotation("unknown");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.doOneFailedSentence(doc, sentence);

    assertNotNull(token.ner());
  }
@Test
  public void testNERTokenizationMergeHyphenatedTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Chicago");
    token1.set(CoreAnnotations.AfterAnnotation.class, "");
    token1.setSentIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.set(CoreAnnotations.AfterAnnotation.class, "");
    token2.setSentIndex(0);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("area");
    token3.set(CoreAnnotations.AfterAnnotation.class, " ");
    token3.setSentIndex(0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    CoreMap sentence = new Annotation("Chicago-area");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Chicago-area");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Annotation tokenized = NERCombinerAnnotator.annotationWithNERTokenization(doc);

    List<CoreLabel> mergedTokens = tokenized.get(CoreAnnotations.TokensAnnotation.class);

    assertFalse(mergedTokens.isEmpty());
  }
@Test
  public void testNullNERAnnotationsTransferredSafely() {
    Annotation original = new Annotation("Data");
    Annotation tokenized = new Annotation("Data");

    List<CoreLabel> origTokens = new ArrayList<>();
    CoreLabel tok = new CoreLabel();
    tok.setWord("Data");
    origTokens.add(tok);

    List<CoreLabel> nerTokens = new ArrayList<>();
    CoreLabel nTok = new CoreLabel();
    nTok.setWord("Data");
    nerTokens.add(nTok);

    CoreMap sentenceOrig = new Annotation("Data");
    sentenceOrig.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    CoreMap sentenceNER = new Annotation("Data");
    sentenceNER.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceOrig));
    original.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    tokenized.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceNER));
    tokenized.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(tokenized, original);

    assertEquals("Data", origTokens.get(0).word());
  }
@Test
  public void testRequirementsSatisfiedIncludesEntityMentionKeys() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.buildEntityMentions", "true");
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.EntityTypeAnnotation.class));
  }
@Test
  public void testRequirementsIncludeSUTimeWhenEnabled() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.useSUTime", "true");
    props.setProperty("sutime.binders", "0");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.LemmaAnnotation.class));
  } 
}