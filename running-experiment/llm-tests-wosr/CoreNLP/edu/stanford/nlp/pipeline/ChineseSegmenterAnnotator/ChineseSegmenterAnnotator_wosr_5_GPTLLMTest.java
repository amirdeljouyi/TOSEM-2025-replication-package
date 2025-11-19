public class ChineseSegmenterAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testAnnotateSingleSentenceBasicSegmentation() {
    AbstractSequenceClassifier mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("今天天气不错")).thenReturn(Arrays.asList("今天", "天气", "不错"));

    Properties props = new Properties();
    props.setProperty("segment.model", "ignoreModel");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        this.segmenter = mockClassifier;
      }
    };

    Annotation doc = new Annotation("今天天气不错");
    CoreMap sentence = new Annotation("今天天气不错");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(doc);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("今天", tokens.get(0).word());
    assertEquals("天气", tokens.get(1).word());
    assertEquals("不错", tokens.get(2).word());
  }
@Test
  public void testAnnotateSingleSentenceWithNewlineAndTokenization() {
    AbstractSequenceClassifier mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    when(mockClassifier.segmentString("\n")).thenReturn(Collections.singletonList("\n"));

    Properties props = new Properties();
    props.setProperty("segment.model", "ignoreModel");
    props.setProperty("segment.verbose", "true");
    props.setProperty("segment.normalizeSpace", "false");
    props.setProperty("segment.sighanCorporaDict", "dummy");
    props.setProperty("segment.serDictionary", "dummy");
    props.setProperty("tokenizeNLs", "true");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        this.segmenter = mockClassifier;
        this.tokenizeNewline = true;
      }
    };

    Annotation sentence = new Annotation("你好\n");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    Annotation doc = new Annotation("你好\n");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size()); 
    assertEquals("你", tokens.get(0).word());
    assertEquals("好", tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
  }
@Test
  public void testAnnotateSentenceContainingXmlBoundary() {
    AbstractSequenceClassifier mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("文本")).thenReturn(Arrays.asList("文本"));

    Properties props = new Properties();
    props.setProperty("segment.model", "foo.model");
    props.setProperty("segment.serDictionary", "foo.dict");
    props.setProperty("segment.sighanCorporaDict", "foo.sighan");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        this.segmenter = mockClassifier;
      }
    };

    Annotation sentence = new Annotation("<doc>文本</doc>");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    Annotation doc = new Annotation("<doc>文本</doc>");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size()); 

    assertEquals("<doc>", tokens.get(0).originalText());
    assertEquals("文本", tokens.get(1).originalText());
    assertEquals("</doc>", tokens.get(2).originalText());
  }
@Test
  public void testWhitespaceOnlyTextYieldsNoTokens() {
    AbstractSequenceClassifier mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("  \n\r")).thenReturn(Collections.emptyList());

    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        this.segmenter = mockClassifier;
        this.tokenizeNewline = false;
      }
    };

    Annotation annotation = new Annotation("  \n\r");
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("  \n\r");
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testAnnotatorMultipleSentencesHandledSeparately() {
    AbstractSequenceClassifier mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我喜欢")).thenReturn(Arrays.asList("我", "喜欢"));
    when(mockClassifier.segmentString("编程")).thenReturn(Arrays.asList("编", "程"));

    Properties props = new Properties();
    props.setProperty("segment.model", "model");
    props.setProperty("segment.serDictionary", "dict");
    props.setProperty("segment.sighanCorporaDict", "sighan");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        this.segmenter = mockClassifier;
      }
    };

    CoreMap sentence1 = new Annotation("我喜欢");
    CoreMap sentence2 = new Annotation("编程");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    Annotation doc = new Annotation("我喜欢。编程。");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);

    List<CoreLabel> tokens1 = sentence1.get(CoreAnnotations.TokensAnnotation.class);
    List<CoreLabel> tokens2 = sentence2.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens1);
    assertNotNull(tokens2);
    assertEquals(2, tokens1.size());
    assertEquals("我", tokens1.get(0).word());
    assertEquals("喜欢", tokens1.get(1).word());

    assertEquals(2, tokens2.size());
    assertEquals("编", tokens2.get(0).word());
    assertEquals("程", tokens2.get(1).word());
  }
@Test
  public void testSegmenterWithEmptyContent() {
    AbstractSequenceClassifier mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());

    Properties props = new Properties();
    props.setProperty("segment.model", "test-model");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        this.segmenter = mockClassifier;
        this.tokenizeNewline = false;
      }
    };

    Annotation doc = new Annotation("");
    List<CoreMap> sentenceList = new ArrayList<>();
    CoreMap sentence = new Annotation("");
    sentenceList.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(doc);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testAdvancePositionThrowsIfMismatch() {
    List<CoreLabel> charList = new ArrayList<>();

    CoreLabel cl1 = new CoreLabel();
    cl1.set(CoreAnnotations.ChineseCharAnnotation.class, "我");
    CoreLabel cl2 = new CoreLabel();
    cl2.set(CoreAnnotations.ChineseCharAnnotation.class, "是");

    charList.add(cl1);
    charList.add(cl2);

    try {
      ChineseSegmenterAnnotator.advancePos(charList, 0, "错");
      fail("Expected RuntimeException for unmatched string");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Expected is '错'"));
    }
  } 
}