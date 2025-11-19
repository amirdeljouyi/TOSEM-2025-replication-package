public class NERCombinerAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testNERCombinerAnnotator_DefaultConstructor() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator();
    Assert.assertNotNull(annotator);
  }
@Test
  public void testNERCombinerAnnotator_WithCustomProperties() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Assert.assertNotNull(annotator);
  }
@Test
  public void testAnnotate_WithEnglishText() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("Barack Obama was born in Hawaii.");
    CoreLabel t1 = new CoreLabel();
    t1.setWord("Barack");
    t1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    t1.set(CoreAnnotations.AfterAnnotation.class, " ");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("Obama");
    t2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    t2.set(CoreAnnotations.AfterAnnotation.class, " ");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("was");
    t3.set(CoreAnnotations.TextAnnotation.class, "was");
    t3.set(CoreAnnotations.AfterAnnotation.class, " ");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("born");
    t4.set(CoreAnnotations.TextAnnotation.class, "born");
    t4.set(CoreAnnotations.AfterAnnotation.class, " ");
    CoreLabel t5 = new CoreLabel();
    t5.setWord("in");
    t5.set(CoreAnnotations.TextAnnotation.class, "in");
    t5.set(CoreAnnotations.AfterAnnotation.class, " ");
    CoreLabel t6 = new CoreLabel();
    t6.setWord("Hawaii");
    t6.set(CoreAnnotations.TextAnnotation.class, "Hawaii");
    t6.set(CoreAnnotations.AfterAnnotation.class, ".");

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6);

    Map<Class<? extends TypesafeMap.Key<?>>, Object> sentenceAnnotations = new HashMap<>();
    sentenceAnnotations.put(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreMap sentence = new Annotation("Barack Obama was born in Hawaii.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    for (CoreLabel token : tokens) {
      Assert.assertNotNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    }
  }
@Test
  public void testDoOneFailedSentence_AddsBackgroundTags() throws Exception {
    NERClassifierCombiner mockNER = new NERClassifierCombiner(new Properties()) {
      @Override
      public String backgroundSymbol() {
        return "O";
      }
    };
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNER, false);

    CoreLabel token = new CoreLabel();
    token.setWord("Example");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Example");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Annotation annotation = new Annotation("Example");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.doOneFailedSentence(annotation, sentence);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    if (ner == null) {
      ner = token.ner();
    }

    Assert.assertEquals("O", ner);
  }
@Test
  public void testTransferNERAnnotationsToAnnotation_MergedTokenCorrectness() {
    CoreLabel t1Orig = new CoreLabel();
    t1Orig.setWord("New");
    CoreLabel t2Orig = new CoreLabel();
    t2Orig.setWord("York");

    CoreMap sentenceOriginal = new Annotation("New York");
    sentenceOriginal.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1Orig, t2Orig));
    Annotation origAnnotation = new Annotation("New York");
    origAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentenceOriginal));
    origAnnotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1Orig, t2Orig));

    CoreLabel tMerged = new CoreLabel();
    tMerged.setWord("NewYork");
    tMerged.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    tMerged.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    CoreMap sentenceNerTok = new Annotation("New York");
    sentenceNerTok.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(tMerged));
    Annotation nerTokAnnotation = new Annotation("New York");
    nerTokAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentenceNerTok));
    nerTokAnnotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(tMerged));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerTokAnnotation, origAnnotation);

    Assert.assertEquals("CITY", t1Orig.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    Assert.assertEquals("CITY", t2Orig.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testMergeTokens_MergesFieldsCorrectly() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("New-");
    token1.setAfter("");
    token1.setEndPosition(3);
    token1.setSentIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("York");
    token2.setAfter(" ");
    token2.setEndPosition(7);

    NERCombinerAnnotator.mergeTokens(token1, token2);

    Assert.assertEquals("New-York", token1.word());
    Assert.assertEquals(" ", token1.after());
    Assert.assertEquals(7, token1.endPosition());
    Assert.assertEquals("New-York-0", token1.value());
    Assert.assertEquals((Integer)1, token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied_EntityMentionsDisabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotations>> expected = new HashSet<>();
    expected.add(CoreAnnotations.CoarseNamedEntityTagAnnotation.class);
    expected.add(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class);
    expected.add(CoreAnnotations.NamedEntityTagAnnotation.class);
    expected.add(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    expected.add(CoreAnnotations.NumericCompositeTypeAnnotation.class);
    expected.add(CoreAnnotations.NumericCompositeValueAnnotation.class);
    expected.add(CoreAnnotations.NumericTypeAnnotation.class);
    expected.add(CoreAnnotations.NumericValueAnnotation.class);
    expected.add(CoreAnnotations.DistSimAnnotation.class);
    expected.add(CoreAnnotations.NumerizedTokensAnnotation.class);
    expected.add(CoreAnnotations.AnswerAnnotation.class);
    expected.add(CoreAnnotations.ShapeAnnotation.class);
    expected.add(CoreAnnotations.ValueAnnotation.class);
    expected.add(TimeAnnotations.TimexAnnotation.class);
    expected.add(TimeExpression.Annotation.class);
    expected.add(TimeExpression.TimeIndexAnnotation.class);
    expected.add(TimeExpression.ChildrenAnnotation.class);
    expected.add(Tags.TagsAnnotation.class);

    Set<Class<? extends CoreAnnotation>> actual = annotator.requirementsSatisfied();

    for (Class<? extends CoreAnnotation> cls : expected) {
      Assert.assertTrue(actual.contains(cls));
    }

    Assert.assertFalse(actual.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testAfterIsEmptyPredicate_ReturnsTrueWhenExpected() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "");
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    Assert.assertTrue(result);
  }
@Test
  public void testAfterIsEmptyPredicate_ReturnsFalseWhenAfterNotEmpty() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    Assert.assertFalse(result);
  } 
}