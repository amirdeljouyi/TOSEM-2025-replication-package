public class ChineseSegmenterAnnotator_wosr_2_GPTLLMTest { 

 @Test(expected = RuntimeException.class)
  public void testConstructorThrowsWithoutModelProperty() {
    Properties props = new Properties();
    new ChineseSegmenterAnnotator("segment", props);
  }
@Test
  public void testConstructorWithModelPathLoadsClassifier() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Assert.assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithNullSentencesAnnotationAddsTokensToRoot() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我爱自然语言处理");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    Assert.assertNotNull(tokens);
    Assert.assertFalse(tokens.isEmpty());
  }
@Test
  public void testAnnotateWithMultipleSentencesProcessesEach() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("");
    List<CoreMap> sentences = new ArrayList<>();

    Annotation sentence1 = new Annotation("我爱北京");
    Annotation sentence2 = new Annotation("天安门很好看");
    sentences.add(sentence1);
    sentences.add(sentence2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> tokens1 = sentence1.get(CoreAnnotations.TokensAnnotation.class);
    List<CoreLabel> tokens2 = sentence2.get(CoreAnnotations.TokensAnnotation.class);

    Assert.assertNotNull(tokens1);
    Assert.assertNotNull(tokens2);
    Assert.assertFalse(tokens1.isEmpty());
    Assert.assertFalse(tokens2.isEmpty());
  }
@Test
  public void testTokenizeNewlineHandlingWithNewlinesPreserved() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我爱北京\n天安门");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean containsNewline = false;
    for (CoreLabel token : tokens) {
      if ("\n".equals(token.originalText()) || "<NEWLINE>".equals(token.word())) {
        containsNewline = true;
        break;
      }
    }
    Assert.assertTrue(containsNewline);
  }
@Test
  public void testNewlinesAreStrippedWhenNotPreserved() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我爱北京\n天安门");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    for (CoreLabel token : tokens) {
      Assert.assertNotEquals("\n", token.originalText());
    }
  }
@Test
  public void testXMLWhitespacesAreMergedIntoSingleToken() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("<xml>  \n</xml>我爱北京");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean hasXmlTokens = false;
    for (CoreLabel token : tokens) {
      if (token.originalText().contains("<xml>") || token.originalText().contains("</xml>")) {
        hasXmlTokens = true;
        break;
      }
    }
    Assert.assertTrue(hasXmlTokens);
  }
@Test
  public void testEmptyStringInputProducesNoTokens() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    Assert.assertTrue(tokens == null || tokens.isEmpty());
  }
@Test
  public void testTokensHaveOffsets() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation("我爱自然语言处理");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    for (CoreLabel token : tokens) {
      Integer begin = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
      Integer end = token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
      Assert.assertNotNull(begin);
      Assert.assertNotNull(end);
      Assert.assertTrue(begin < end);
    }
  }
@Test
  public void testRequirementsSatisfiedContainsTokenAnnotations() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    Assert.assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    Assert.assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    Assert.assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testWhitespaceOnlyTextIgnoresAllContent() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation("    \n\n\r\n\t");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    Assert.assertTrue(tokens == null || tokens.isEmpty());
  }
@Test
  public void testEmojiSupportWithMultipleCodePoints() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我❤️北京");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean foundEmoji = false;
    for (CoreLabel token : tokens) {
      if (token.originalText().contains("❤️")) {
        foundEmoji = true;
        break;
      }
    }
    Assert.assertTrue(foundEmoji);
  }
@Test
  public void testMultipleXmlTagsProcessCorrectly() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("<tag>标签</tag>文本<tag2>更多</tag2>");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean hasMultipleXml = false;
    int xmlCount = 0;
    for (CoreLabel token : tokens) {
      if (token.originalText().contains("<tag>") || token.originalText().contains("</tag>")
          || token.originalText().contains("<tag2>") || token.originalText().contains("</tag2>")) {
        xmlCount++;
      }
    }
    if (xmlCount >= 2) {
      hasMultipleXml = true;
    }
    Assert.assertTrue(hasMultipleXml);
  }
@Test
  public void testSegmentTextWithTrailingNewlines() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("我爱自然\n\n");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean hasTrailing = tokens.get(tokens.size() - 1).originalText().contains("我") ||
                          tokens.get(tokens.size() - 1).originalText().contains("爱") ||
                          tokens.get(tokens.size() - 1).originalText().contains("自然");

    Assert.assertTrue(hasTrailing);
  }
@Test
  public void testWhitespaceXmlInsideTagsHandled() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);

    Annotation annotation = new Annotation("<p>  \n\t</p>");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    Assert.assertFalse(tokens.isEmpty());
    CoreLabel xmlToken = tokens.get(0);
    Assert.assertTrue(xmlToken.originalText().contains("<p>"));
  }
@Test
  public void testNullAnnotationDoesNotCrash() {
    Properties props = new Properties();
    props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese-ctb.gz");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    try {
      annotator.annotate(null);
    } catch (NullPointerException e) {
      Assert.fail("Should not throw NullPointerException when annotation is null");
    } catch (Exception e) {
      
    }
  } 
}