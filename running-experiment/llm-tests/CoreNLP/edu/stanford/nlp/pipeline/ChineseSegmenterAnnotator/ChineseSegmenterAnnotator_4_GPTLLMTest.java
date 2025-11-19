package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.CoreMap;
import java.util.*;
import org.junit.Test;

public class ChineseSegmenterAnnotator_4_GPTLLMTest {

  @Test(expected = RuntimeException.class)
  public void testConstructorThrowsWhenMissingModelProperty() {
    Properties props = new Properties();
    new ChineseSegmenterAnnotator("segment", props);
  }

  @Test
  public void testBasicSegmentationWithTextAnnotationOnly() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我喜欢学习中文")).thenReturn(Arrays.asList("我", "喜欢", "学习", "中文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我喜欢学习中文";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("喜欢", tokens.get(1).word());
    assertEquals("学习", tokens.get(2).word());
    assertEquals("中文", tokens.get(3).word());
  }

  @Test
  public void testSegmentationWithSentenceAnnotations() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我爱中国")).thenReturn(Arrays.asList("我", "爱", "中国"));
    when(mockClassifier.segmentString("你喜欢北京吗")).thenReturn(Arrays.asList("你", "喜欢", "北京", "吗"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    Annotation ann = new Annotation("我是文本");
    CoreMap s1 = new Annotation("我爱中国");
    s1.set(CoreAnnotations.TextAnnotation.class, "我爱中国");
    CoreMap s2 = new Annotation("你喜欢北京吗");
    s2.set(CoreAnnotations.TextAnnotation.class, "你喜欢北京吗");
    ann.set(CoreAnnotations.TextAnnotation.class, "我爱中国你喜欢北京吗");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(s1, s2));
    annotator.annotate(ann);
    List<CoreLabel> tokens1 = s1.get(CoreAnnotations.TokensAnnotation.class);
    List<CoreLabel> tokens2 = s2.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens1);
    assertEquals(3, tokens1.size());
    assertEquals("我", tokens1.get(0).word());
    assertEquals("中国", tokens1.get(2).word());
    assertNotNull(tokens2);
    assertEquals(4, tokens2.size());
    assertEquals("你", tokens2.get(0).word());
    assertEquals("吗", tokens2.get(3).word());
  }

  @Test
  public void testNewlineIsHandledWhenTokenizeNewlineTrue() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我")).thenReturn(Collections.singletonList("我"));
    when(mockClassifier.segmentString("喜欢")).thenReturn(Collections.singletonList("喜欢"));
    when(mockClassifier.segmentString("中文")).thenReturn(Collections.singletonList("中文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我\n喜欢\n中文";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals("喜欢", tokens.get(2).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(3).word());
    assertEquals("中文", tokens.get(4).word());
  }

  @Test
  public void testNormalizeSpacesWhenEnabled() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("foo bar")).thenReturn(Arrays.asList("foo", "bar"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty("segment.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag> foo bar </tag>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean foundNbsp = false;
    if (tokens != null && !tokens.isEmpty()) {
      String word1 = tokens.get(0).word();
      String word2 = tokens.get(tokens.size() - 1).word();
      if (word1 != null && word1.contains("\u00A0")) {
        foundNbsp = true;
      }
      if (word2 != null && word2.contains("\u00A0")) {
        foundNbsp = true;
      }
    }
    assertTrue(foundNbsp);
  }

  @Test
  public void testEmptyInputResultsInNoTokens() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testOnlyWhitespaceInputReturnsOneToken() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("   ")).thenReturn(Collections.singletonList(" "));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "   ";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
  }

  @Test
  public void testRequirementsSatisfiedSetIncludesExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    AbstractSequenceClassifier<?> dummyClassifier = mock(AbstractSequenceClassifier.class);
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return dummyClassifier;
          }
        };
    // Set<Class<? extends CoreAnnotations.CoreAnnotation<?>>> satisfied =
    // annotator.requirementsSatisfied();
    // assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    // assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    // assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    // assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void testRequiresReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    AbstractSequenceClassifier<?> dummyClassifier = mock(AbstractSequenceClassifier.class);
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return dummyClassifier;
          }
        };
    // Set<Class<? extends CoreAnnotations.CoreAnnotation<?>>> required = annotator.requires();
    // assertTrue(required.isEmpty());
  }

  @Test
  public void testMultipleConsecutiveNewlinesWithSentenceBreakPropertyTwo() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我")).thenReturn(Collections.singletonList("我"));
    when(mockClassifier.segmentString("喜")).thenReturn(Collections.singletonList("喜"));
    when(mockClassifier.segmentString("欢")).thenReturn(Collections.singletonList("欢"));
    when(mockClassifier.segmentString("中")).thenReturn(Collections.singletonList("中"));
    when(mockClassifier.segmentString("文")).thenReturn(Collections.singletonList("文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我\n\n喜\n\n欢中\n文";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("喜", tokens.get(1).word());
    assertEquals("欢", tokens.get(2).word());
    assertEquals("中", tokens.get(3).word());
    assertEquals("文", tokens.get(4).word());
  }

  @Test
  public void testOnlyNewlinesInputShouldProduceNoTokens() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "\n\n\n";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testSegmenterReturnsEmptyListProducesEmptyTokensAnnotation() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我喜欢")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-path");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我喜欢";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testSegmenterThrowsCheckedExceptionWrappedAsRuntimeException() {
    AbstractSequenceClassifier<?> faultyClassifier = mock(AbstractSequenceClassifier.class);
    doThrow(new RuntimeException("intentional")).when(faultyClassifier).segmentString("数据");
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return faultyClassifier;
          }
        };
    String input = "数据";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException not thrown");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("intentional"));
    }
  }

  @Test
  public void testMakeXmlTokenWithWhitespaceNormalizationEnabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-path");
    props.setProperty("segment.normalizeSpace", "true");
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(any())).thenReturn(Collections.singletonList(" "));
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag> </tag>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    boolean foundNormalized = false;
    if (!tokens.isEmpty()) {
      for (int i = 0; i < tokens.size(); i++) {
        String word = tokens.get(i).word();
        if (word != null && word.contains("\u00A0")) {
          foundNormalized = true;
        }
      }
    }
    assertTrue(foundNormalized);
  }

  @Test
  public void testEmptyXmlTagIsTreatedAsSingleToken() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<br/>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("<br/>", tokens.get(0).originalText());
  }

  @Test
  public void testTextContainingOnlyXMLContent() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<note><to>你</to><from>我</from></note>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
    assertTrue(
        tokens.get(0).originalText().contains("<note>")
            || tokens.get(0).originalText().contains("<to>"));
  }

  @Test
  public void testSingleEmojiCharacterSegmentation() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("\uD83C\uDF1F"))
        .thenReturn(Collections.singletonList("\uD83C\uDF1F"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "\uD83C\uDF1F";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals(input, tokens.get(0).word());
  }

  @Test
  public void testSurrogatePairSplitHandlingIsNotBroken() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我\uD83D\uDE80你"))
        .thenReturn(Arrays.asList("我", "\uD83D\uDE80", "你"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我\uD83D\uDE80你";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("\uD83D\uDE80", tokens.get(1).word());
    assertEquals("你", tokens.get(2).word());
  }

  @Test
  public void testTextEndsWithNewlineWhenTokenizeNewlineFalse() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我喜欢\n")).thenReturn(Arrays.asList("我", "喜欢"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "false");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我喜欢\n";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("喜欢", tokens.get(1).word());
  }

  @Test
  public void testTextStartsWithNewlineWhenTokenizeNewlineTrue() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我喜欢")).thenReturn(Arrays.asList("我", "喜欢"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "\n我喜欢";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals("我", tokens.get(1).word());
    assertEquals("喜欢", tokens.get(2).word());
  }

  @Test
  public void testTextWithIsolatedNewlineBetweenNonWhitespaceWhenSplittingOnTwoNewlines()
      throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我喜\n欢你")).thenReturn(Arrays.asList("我", "喜", "欢", "你"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我喜\n欢你";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("喜", tokens.get(1).word());
    assertEquals("欢", tokens.get(2).word());
    assertEquals("你", tokens.get(3).word());
  }

  @Test
  public void testUnknownCharacterReturnsValidToken() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("#特殊")).thenReturn(Arrays.asList("#", "特殊"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "#特殊";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("#", tokens.get(0).word());
    assertEquals("特殊", tokens.get(1).word());
  }

  @Test
  public void testMatchLongTextWithMultipleLinesAndTags() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我爱北京")).thenReturn(Arrays.asList("我", "爱", "北京"));
    when(mockClassifier.segmentString("你喜欢上海吗")).thenReturn(Arrays.asList("你", "喜欢", "上海", "吗"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<xml>\n我爱北京\n</xml>\n你喜欢上海吗\n";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 7);
    assertEquals("我", tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(3).word());
    assertEquals("你", tokens.get(tokens.size() - 4).word());
    assertEquals("吗", tokens.get(tokens.size() - 1).word());
  }

  @Test
  public void testOnlySingleXmlTagNoText() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(any())).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<div/>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("<div/>", tokens.get(0).originalText());
  }

  @Test
  public void testControlCharacterInputIsHandledGracefully() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("\u0001\u0002"))
        .thenReturn(Collections.singletonList("\u0001\u0002"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "\u0001\u0002";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("\u0001\u0002", tokens.get(0).word());
  }

  @Test
  public void testMixOfChineseEnglishWhitespace() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好 world")).thenReturn(Arrays.asList("你", "好", "world"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "你好 world";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("你", tokens.get(0).word());
    assertEquals("world", tokens.get(2).word());
  }

  @Test
  public void testTabCharactersAreIgnoredUnlessTokenizeNewlineTrue() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我\t你")).thenReturn(Arrays.asList("我", "你"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我\t你";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("你", tokens.get(1).word());
  }

  @Test
  public void testPropertiesPrefixFilteringLeavesOtherKeysUntouched() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("测试")).thenReturn(Arrays.asList("测", "试"));
    Properties props = new Properties();
    props.setProperty("segment.model", "model-path");
    props.setProperty("someOtherKey", "value");
    props.setProperty("segment.verbose", "true");
    props.setProperty("segment.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            assertTrue(modelProps.containsKey("verbose"));
            assertTrue(modelProps.containsKey("normalizeSpace"));
            assertFalse(modelProps.containsKey("someOtherKey"));
            return mockClassifier;
          }
        };
    String input = "测试";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("测", tokens.get(0).word());
    assertEquals("试", tokens.get(1).word());
  }

  @Test
  public void testAdvancePosThrowsWhenSegmentDoesNotMatchCharacters() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我X")).thenReturn(Arrays.asList("我X"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我X";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException due to mismatched advancePos");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Ate the whole text without matching"));
    }
  }

  @Test
  public void testSurrogatePairWithXMLDoesNotSplitImproperly() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    String emoji = "\uD83D\uDE0A";
    when(mockClassifier.segmentString(emoji)).thenReturn(Collections.singletonList(emoji));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag>" + emoji + "</tag>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.stream().anyMatch(token -> emoji.equals(token.word())));
  }

  @Test
  public void testSentenceWithoutTextAnnotationIsSkippedGracefully() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    CoreMap sentence = new Annotation("");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    Annotation ann = new Annotation("Missing");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    try {
      annotator.annotate(ann);
    } catch (Exception e) {
      fail("Annotator should skip sentence without TextAnnotation gracefully");
    }
  }

  @Test
  public void testNonChineseInputStillSegmented() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("Stanford NLP rocks!"))
        .thenReturn(Arrays.asList("Stanford", "NLP", "rocks!"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "Stanford NLP rocks!";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("Stanford", tokens.get(0).word());
    assertEquals("rocks!", tokens.get(2).word());
  }

  @Test
  public void testWhitespaceBetweenXmlTagsIgnoredUnlessTokenizeNewlineTrue() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "false");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<doc>\n\n</doc>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0).originalText().contains("<doc>"));
  }

  @Test
  public void testTrailingIsolatedWhitespaceIsSkipped() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "你好   ";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("你", tokens.get(0).word());
    assertEquals("好", tokens.get(1).word());
  }

  @Test
  public void testInputWithEmojiAdjacentToChineseText() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    String emoji = "\uD83D\uDE09";
    when(mockClassifier.segmentString("我" + emoji + "你"))
        .thenReturn(Arrays.asList("我", emoji, "你"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我" + emoji + "你";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals(emoji, tokens.get(1).word());
    assertEquals("你", tokens.get(2).word());
  }

  @Test
  public void testTokenizationWhenTrailingNewlineExistsWithSplitOnTwo() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("爱国")).thenReturn(Arrays.asList("爱", "国"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "爱国\n\n";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("爱", tokens.get(0).word());
    assertEquals("国", tokens.get(1).word());
  }

  @Test
  public void testWhitespaceOnlyInsideXmlRegion() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag>    </tag>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0).originalText().contains("<tag>"));
  }

  @Test
  public void testXmlWhitespacePreservedWhenTokenizeNewlineTrue() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag>\n</tag>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.get(0).originalText().contains("<tag>"));
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
  }

  @Test
  public void testTokenBeginAndEndAnnotationsAreSet() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中国")).thenReturn(Arrays.asList("中", "国"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "中国";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertNotNull(tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertNotNull(tokens.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(
        tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)
            <= tokens.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void testEmptyStringInsideXmlTag() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag></tag>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("<tag></tag>", tokens.get(0).originalText());
  }

  @Test
  public void testEmptySentenceAnnotationIsHandled() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TextAnnotation.class, "");
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }

  @Test
  public void testMakeXmlTokenWithOnlyNewline() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("\n")).thenReturn(Arrays.asList("\n"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "\n";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
  }

  @Test
  public void testMultipleXmlTagsInInput() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    Annotation annotation = new Annotation("<doc><title>你好</title></doc>");
    annotation.set(CoreAnnotations.TextAnnotation.class, "<doc><title>你好</title></doc>");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3);
    assertEquals("你", tokens.get(1).word());
    assertEquals("好", tokens.get(2).word());
  }

  @Test
  public void testXmlAtEndWithoutTrailingText() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("文本")).thenReturn(Arrays.asList("文", "本"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "文本<tag/>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("文", tokens.get(0).word());
    assertEquals("<tag/>", tokens.get(2).originalText());
  }

  @Test
  public void testXmlAtBeginningWithoutPrecedingText() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<note>你好";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("<note>", tokens.get(0).originalText());
    assertEquals("你", tokens.get(1).word());
  }

  @Test
  public void testMalformedXmlStillProcessedWithoutException() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("测试")).thenReturn(Arrays.asList("测", "试"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<b>测试";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("<b>", tokens.get(0).originalText());
    assertEquals("测", tokens.get(1).word());
  }

  @Test
  public void testMultipleConsecutiveXmlTags() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("text")).thenReturn(Collections.singletonList("text"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<a/><b/>text";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("<a/>", tokens.get(0).originalText());
    assertEquals("<b/>", tokens.get(1).originalText());
    assertEquals("text", tokens.get(2).word());
  }

  @Test
  public void testEmptyXmlNodeWithLeadingAndTrailingText() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("测试")).thenReturn(Arrays.asList("测", "试"));
    when(mockClassifier.segmentString("完成")).thenReturn(Arrays.asList("完", "成"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "测试<empty/>完成";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, tokens.size());
    assertEquals("测", tokens.get(0).word());
    assertEquals("<empty/>", tokens.get(2).originalText());
    assertEquals("完", tokens.get(3).word());
  }

  @Test
  public void testNormalizeSpaceReplacesOnlySpacesInsideXmlTokens() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("正常")).thenReturn(Arrays.asList("正常"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty("segment.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag> Hello World </tag>正常";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.get(0).word().contains("\u00A0"));
    assertEquals("正常", tokens.get(1).word());
  }

  @Test
  public void testSentenceSplitOnTwoPreservesOnlyDoubleNewlines() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("A\n\nB")).thenReturn(Arrays.asList("A", "\n\n", "B"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "A\n\nB";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 2);
    assertEquals("A", tokens.get(0).word());
    assertEquals("B", tokens.get(tokens.size() - 1).word());
  }

  @Test
  public void testWhitespaceOnlyBetweenChineseTokensIsIgnored() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我 你")).thenReturn(Arrays.asList("我", "你"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "我 你";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("你", tokens.get(1).word());
  }

  @Test
  public void testNonSpacingUnicodeCharactersAreSegmentedCorrectly() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中\u200B文")).thenReturn(Arrays.asList("中", "文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "中\u200B文";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("中", tokens.get(0).word());
    assertEquals("文", tokens.get(1).word());
  }

  @Test
  public void testXmlTagContainingAttributesProcessedCorrectly() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag type=\"greeting\">你好</tag>";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertTrue(tokens.get(0).originalText().contains("type=\"greeting\""));
    assertEquals("你", tokens.get(1).word());
    assertEquals("好", tokens.get(2).word());
  }

  @Test
  public void testSingleMultibyteCharacterInput() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    String emoji = "\uD83C\uDF3A";
    when(mockClassifier.segmentString(emoji)).thenReturn(Collections.singletonList(emoji));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    Annotation annotation = new Annotation(emoji);
    annotation.set(CoreAnnotations.TextAnnotation.class, emoji);
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals(emoji, tokens.get(0).word());
  }

  @Test
  public void testSegmenterReturnsEmptyStringToken() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Arrays.asList("你", "", "好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    Annotation annotation = new Annotation("你好");
    annotation.set(CoreAnnotations.TextAnnotation.class, "你好");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("你", tokens.get(0).word());
    assertEquals("好", tokens.get(1).word());
  }

  @Test
  public void testCharacterCombiningUnicodeSequenceSegmentation() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    String accented = "a\u0301";
    when(mockClassifier.segmentString(accented)).thenReturn(Collections.singletonList(accented));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    Annotation annotation = new Annotation(accented);
    annotation.set(CoreAnnotations.TextAnnotation.class, accented);
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals(accented, tokens.get(0).word());
  }

  @Test
  public void testOnlyControlCharactersInputNoTokens() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("\u0001\u0002")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    Annotation annotation = new Annotation("\u0001\u0002");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\u0001\u0002");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }

  @Test
  public void testSplitCharactersHandlesUnicodeLineSeparatorU2028() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你\u2028好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    Annotation annotation = new Annotation("你\u2028好");
    annotation.set(CoreAnnotations.TextAnnotation.class, "你\u2028好");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("你", tokens.get(0).word());
    assertEquals("好", tokens.get(1).word());
  }

  @Test
  public void testSentenceContainsOnlyNewlinesWithTwoNewlineMode() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    Annotation annotation = new Annotation("\n\n\n\n");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\n\n\n\n");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }

  @Test
  public void testTokenAfterXmlEntityWithWhitespace() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    String input = "<tag> </tag>你好";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("你", tokens.get(1).word());
    assertEquals("好", tokens.get(2).word());
  }

  @Test
  public void testEmptyAnnotationObjectIsHandledGracefully() throws Exception {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(null)).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    Annotation annotation = new Annotation((String) null);
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          // @Override
          protected AbstractSequenceClassifier<?> loadClassifier(
              String model, Properties modelProps) {
            return mockClassifier;
          }
        };
    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not throw on empty annotation");
    }
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens == null || tokens.isEmpty());
  }
}
