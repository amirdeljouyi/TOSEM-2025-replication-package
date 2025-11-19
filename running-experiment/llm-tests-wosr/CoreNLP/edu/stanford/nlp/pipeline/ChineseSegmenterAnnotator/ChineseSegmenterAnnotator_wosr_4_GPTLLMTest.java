public class ChineseSegmenterAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructorWithValidMinimalProperties() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testThrowsRuntimeExceptionWithMissingModelProperty() {
    Properties props = new Properties();
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");
    new ChineseSegmenterAnnotator("segment", props);
  }
@Test
  public void testAnnotateOneSentenceNoNewlines() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation("我喜欢学习中文。");

    CoreMap mockSentence = new Annotation("我喜欢学习中文。");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(mockSentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());

    for (CoreLabel token : tokens) {
      assertNotNull(token.word());
      assertNotNull(token.value());
      assertNotNull(token.originalText());
    }
  }
@Test
  public void testAnnotateMultipleSentencesWithNewlines() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");
    props.setProperty("segment.verbose", "true");
    props.setProperty("segment.normalizeSpace", "true");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    String input = "我喜欢学习中文。\n你喜欢学习英文。\n\n他喜欢数学。";
    Annotation annotation = new Annotation(input);
    List<CoreMap> sentences = new ArrayList<>();

    CoreMap s1 = new Annotation("我喜欢学习中文。");
    CoreMap s2 = new Annotation("你喜欢学习英文。");
    CoreMap s3 = new Annotation("他喜欢数学。");

    sentences.add(s1);
    sentences.add(s2);
    sentences.add(s3);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> outputSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(outputSentences);
    assertEquals(3, outputSentences.size());

    for (CoreMap sentence : outputSentences) {
      List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
      assertFalse(tokens.isEmpty());

      for (CoreLabel token : tokens) {
        assertNotNull(token.word());
        assertNotNull(token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
        assertNotNull(token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
      }
    }
  }
@Test
  public void testSplitCharactersHandlesXMLCorrectly() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation sentence = new Annotation("<xml>我</xml>");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    sentence.set(CoreAnnotations.TextAnnotation.class, "<xml>我</xml>");

    sentence.set(SegmenterCoreAnnotations.CharactersAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(sentence);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());

    boolean xmlFound = false;
    for (CoreLabel token : tokens) {
      String word = token.word();
      if (word != null && word.contains("<xml>我</xml>")) {
        xmlFound = true;
      }
    }
    assertTrue(xmlFound);
  }
@Test
  public void testSentenceWithOnlyWhitespace() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation sentence = new Annotation("   \n\t  ");

    sentence.set(SegmenterCoreAnnotations.CharactersAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TextAnnotation.class, "   \n\t  ");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(sentence);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testEmptyAnnotationInput() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation("");

    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testTokenizeNewlinesEnabled() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");
    props.setProperty("nlp.pipeline.newlineIsSentenceBreak", "always");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我\n喜欢\n中文");
    annotation.set(CoreAnnotations.TextAnnotation.class, "我\n喜欢\n中文");
    annotation.set(SegmenterCoreAnnotations.CharactersAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());

    boolean newlineTokenFound = false;
    for (CoreLabel token : tokens) {
      if (token.word().equals(AbstractTokenizer.NEWLINE_TOKEN)) {
        newlineTokenFound = true;
      }
    }

    assertTrue(newlineTokenFound);
  }
@Test
  public void testTokenizeNewlineDisabledDefaultBehavior() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我\n喜欢学习中文。");
    annotation.set(CoreAnnotations.TextAnnotation.class, "我\n喜欢学习中文。");
    annotation.set(SegmenterCoreAnnotations.CharactersAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    for (CoreLabel token : tokens) {
      assertNotEquals(AbstractTokenizer.NEWLINE_TOKEN, token.word());
    }
  }
@Test
  public void testMakeXmlTokenWithSpaceNormalization() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
    props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
    props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese/");
    props.setProperty("segment.normalizeSpace", "true");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    CoreLabel token = annotator.makeXmlToken("hello world", true, 0, 11);
    assertEquals("hello\u00A0world", token.word());
  } 
}