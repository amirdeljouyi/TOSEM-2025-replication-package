public class ChineseSegmenterAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testAnnotate_SimpleChineseSentence() {
    Properties props = PropertiesUtils.asProperties("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我爱自然语言处理");
    List<CoreMap> sentences = new ArrayList<>();
    Annotation sentenceAnno = new Annotation("我爱自然语言处理");
    sentenceAnno.set(CoreAnnotations.TextAnnotation.class, "我爱自然语言处理");
    sentences.add(sentenceAnno);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotator.annotate(annotation);

    List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(result);
    assertEquals(1, result.size());

    CoreMap resultSentence = result.get(0);
    List<CoreLabel> tokens = resultSentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 4); 

    StringBuilder sb = new StringBuilder();
    for (CoreLabel cl : tokens) {
      sb.append(cl.word());
    }
    assertEquals("我爱自然语言处理", sb.toString());
  }
@Test(expected = RuntimeException.class)
  public void testConstructor_MissingModelPropertyShouldThrowException() {
    Properties props = new Properties();
    new ChineseSegmenterAnnotator("segment", props);
  }
@Test
  public void testAnnotate_EmptyTextShouldProduceNoTokens() {
    Properties props = PropertiesUtils.asProperties("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testAnnotate_TextWithNewlinesAndTokenizeNewlinesTrue() {
    Properties props = PropertiesUtils.asProperties(
        "segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz",
        "segment.verbose", "false",
        StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true"
    );
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我爱\n自然语言\n处理");
    List<CoreMap> sentences = new ArrayList<>();
    Annotation sentenceAnno = new Annotation("我爱\n自然语言\n处理");
    sentenceAnno.set(CoreAnnotations.TextAnnotation.class, "我爱\n自然语言\n处理");
    sentences.add(sentenceAnno);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(result);

    CoreMap resultSentence = result.get(0);
    List<CoreLabel> tokens = resultSentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);

    boolean hasNewlineToken = false;
    for (CoreLabel token : tokens) {
      if ("<CR>".equals(token.word())) {
        hasNewlineToken = true;
        break;
      }
    }
    assertTrue(hasNewlineToken);
  }
@Test
  public void testAnnotate_WithXMLRegion() {
    Properties props = PropertiesUtils.asProperties("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("<tag>我爱</tag>自然语言");
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("<tag>我爱</tag>自然语言");
    sentence.set(CoreAnnotations.TextAnnotation.class, "<tag>我爱</tag>自然语言");
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentences.get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    boolean xmlTokenFound = false;
    for (CoreLabel token : tokens) {
      if (token.originalText().contains("<tag>") || token.originalText().contains("</tag>")) {
        xmlTokenFound = true;
        break;
      }
    }
    assertTrue(xmlTokenFound);
  }
@Test
  public void testSegmentWhitespaceNormalization() {
    Properties props = PropertiesUtils.asProperties(
        "segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz",
        "segment.normalizeSpace", "true"
    );
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我 爱 自然");
    annotation.set(CoreAnnotations.TextAnnotation.class, "我 爱 自然");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(annotation);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);

    boolean foundNonBreakingSpace = false;
    for (CoreLabel token : tokens) {
      if ("\u00A0".equals(token.word())) {
        foundNonBreakingSpace = true;
        break;
      }
    }
    assertTrue(foundNonBreakingSpace);
  }
@Test
  public void testRequirementsSatisfiedContainsKeyCoreAnnotations() {
    Properties props = PropertiesUtils.asProperties("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequiresReturnsEmptySet() {
    Properties props = PropertiesUtils.asProperties("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertNotNull(required);
    assertTrue(required.isEmpty());
  }
@Test
  public void testAnnotate_SingleCharacterChineseSentence() {
    Properties props = PropertiesUtils.asProperties("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("爱");
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("爱");
    sentence.set(CoreAnnotations.TextAnnotation.class, "爱");
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotator.annotate(annotation);

    List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(result);
    assertEquals(1, result.size());

    CoreMap resultSentence = result.get(0);
    List<CoreLabel> tokens = resultSentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("爱", tokens.get(0).word());
  } 
}