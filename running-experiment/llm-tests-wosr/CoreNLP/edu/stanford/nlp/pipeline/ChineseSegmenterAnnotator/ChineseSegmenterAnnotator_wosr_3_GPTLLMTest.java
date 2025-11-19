public class ChineseSegmenterAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testAnnotateWithSimpleChineseSentence() {
    String input = "我爱自然语言处理";
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy-model", "segment.verbose", "false");
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("我爱自然语言处理")).thenReturn(Arrays.asList("我", "爱", "自然", "语言", "处理"));

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        
        try {
          java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
          field.setAccessible(true);
          field.set(this, mockSegmenter);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Annotation annotation = new Annotation(input);
    CoreMap sentence = new Annotation(input);
    sentence.set(CoreAnnotations.TextAnnotation.class, input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("爱", tokens.get(1).word());
    assertEquals("自然", tokens.get(2).word());
    assertEquals("语言", tokens.get(3).word());
    assertEquals("处理", tokens.get(4).word());
  }
@Test
  public void testAnnotateWithNewlinesWhenTokenizeNewlineTrue() {
    String input = "\n我\n爱\n中文\n";
    Properties props = PropertiesUtils.asProperties(
        "segment.model", "dummy-model",
        "segment.verbose", "false",
        "segment.normalizeSpace", "false",
        StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always"
    );

    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("我")).thenReturn(Collections.singletonList("我"));
    when(mockSegmenter.segmentString("爱")).thenReturn(Collections.singletonList("爱"));
    when(mockSegmenter.segmentString("中文")).thenReturn(Collections.singletonList("中文"));

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        try {
          java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
          field.setAccessible(true);
          field.set(this, mockSegmenter);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Annotation annotation = new Annotation(input);
    CoreMap sentence = new Annotation(input);
    sentence.set(CoreAnnotations.TextAnnotation.class, input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(7, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals("我", tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("爱", tokens.get(3).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(4).word());
    assertEquals("中文", tokens.get(5).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(6).word());
  }
@Test
  public void testAnnotateWithoutSentencesAnnotationFallsBack() {
    String input = "中文处理测试";
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy-model");
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString(input)).thenReturn(Arrays.asList("中文", "处理", "测试"));

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        try {
          java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
          field.setAccessible(true);
          field.set(this, mockSegmenter);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("中文", tokens.get(0).word());
    assertEquals("处理", tokens.get(1).word());
    assertEquals("测试", tokens.get(2).word());
  }
@Test
  public void testXmlInlineCharactersAreSegmentedProperly() {
    String xmlText = "<seg>我 爱</seg>";
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy-model");
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("我 爱")).thenReturn(Arrays.asList("我", "爱"));

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {
      {
        try {
          java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
          field.setAccessible(true);
          field.set(this, mockSegmenter);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Annotation annotation = new Annotation(xmlText);
    CoreMap sentence = new Annotation(xmlText);
    sentence.set(CoreAnnotations.TextAnnotation.class, xmlText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("<seg>", tokens.get(0).originalText());
    assertEquals("我", tokens.get(1).word());
    assertEquals("爱</seg>", tokens.get(2).originalText());
  }
@Test
  public void testMakeXmlTokenNormalization() {
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", PropertiesUtils.asProperties(
        "segment.model", "dummy-model", "segment.normalizeSpace", "true")) {
      {
        try {
          java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
          field.setAccessible(true);
          field.set(this, mock(AbstractSequenceClassifier.class));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    CoreLabel token = annotator.makeXmlToken("test token", true, 0, 10);
    assertEquals("test\u00A0token", token.word());
    assertEquals(Integer.valueOf(0), token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(10), token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testMakeXmlTokenNewlineReplacement() {
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", PropertiesUtils.asProperties(
        "segment.model", "dummy-model", "segment.normalizeSpace", "true")) {
      {
        try {
          java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
          field.setAccessible(true);
          field.set(this, mock(AbstractSequenceClassifier.class));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    CoreLabel token = annotator.makeXmlToken("\n", false, 0, 1);
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token.word());
  }
@Test
  public void testAdvancePosThrowsRuntimeExceptionOnMismatch() {
    List<CoreLabel> chars = new ArrayList<>();
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.ChineseCharAnnotation.class, "你");
    chars.add(label);

    try {
      ChineseSegmenterAnnotator.advancePos(chars, 0, "错误");
      fail("Expected RuntimeException for unmatched segment");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Expected is '错误'"));
    }
  } 
}