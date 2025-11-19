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

public class ChineseSegmenterAnnotator_3_GPTLLMTest {

  @Test
  public void testSimpleSegmentationWithoutNewlines() {
    String text = "北京大学生前来应聘";
    List<String> segmentation = Arrays.asList("北京大学", "生", "前来", "应聘");
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("北京大学生前来应聘")).thenReturn(segmentation);
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-seg-model");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation document = new Annotation("北京大学生前来应聘");
    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TextAnnotation.class, text);
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotator.annotate(document);
    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    CoreMap annotatedSentence = sentences.get(0);
    List<CoreLabel> tokens = annotatedSentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("北京大学", tokens.get(0).word());
    assertEquals("生", tokens.get(1).word());
    assertEquals("前来", tokens.get(2).word());
    assertEquals("应聘", tokens.get(3).word());
  }

  @Test
  public void testTokenizeNewlinesPreservesNewlineAsToken() {
    String text = "北京大学\n前来应聘";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("北京大学")).thenReturn(Arrays.asList("北京大学"));
    when(mockClassifier.segmentString("前来应聘")).thenReturn(Arrays.asList("前来", "应聘"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy-model");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation document = new Annotation("北京大学\n前来应聘");
    document.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(document);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("北京大学", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals("前来", tokens.get(2).word());
    assertEquals("应聘", tokens.get(3).word());
  }

  @Test
  public void testSentenceSplittingOnTwoNewlines() {
    String text = "中山大学\n\n前来应聘";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中山大学前来应聘")).thenReturn(Arrays.asList("中山大学", "前来", "应聘"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation doc = new Annotation(text);
    doc.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(doc);
    List<CoreLabel> tokens = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, tokens.size());
    assertEquals("中山大学", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("前来", tokens.get(3).word());
    assertEquals("应聘", tokens.get(4).word());
  }

  @Test
  public void testNormalizedWhitespaceReplacedWithUnicodeSpace() {
    String xmlText = "<tag> a b </tag>";
    List<String> xmlSeg = Collections.singletonList(xmlText);
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(xmlText)).thenReturn(xmlSeg);
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty("segment.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation doc = new Annotation(xmlText);
    doc.set(CoreAnnotations.TextAnnotation.class, xmlText);
    annotator.annotate(doc);
    List<CoreLabel> tokens = doc.get(CoreAnnotations.TokensAnnotation.class);
    String expected = xmlText.replace(' ', '\u00A0');
    assertEquals(1, tokens.size());
    assertEquals(expected, tokens.get(0).word());
  }

  @Test(expected = RuntimeException.class)
  public void testMissingModelThrowsRuntimeException() {
    Properties props = new Properties();
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
  }

  @Test
  public void testRequirementsAndCapabilities() {
    AbstractSequenceClassifier<?> dummyClassifier = mock(AbstractSequenceClassifier.class);
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = dummyClassifier;
          }
        };
    assertNotNull(annotator.requirementsSatisfied());
    assertTrue(annotator.requires().isEmpty());
  }

  @Test
  public void testAnnotationWithNoSentencesTriggersFallbackToDocumentProcessing() {
    String input = "中文测试没有句子结构";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input))
        .thenReturn(Arrays.asList("中文", "测试", "没有", "句子", "结构"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, tokens.size());
    assertEquals("中文", tokens.get(0).word());
    assertEquals("测试", tokens.get(1).word());
    assertEquals("没有", tokens.get(2).word());
    assertEquals("句子", tokens.get(3).word());
    assertEquals("结构", tokens.get(4).word());
  }

  @Test
  public void testEmptyTextProducesNoTokens() {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.isEmpty());
  }

  @Test
  public void testOnlyNewlinesAreHandledCorrectlyWithTokenizeNewlineFalse() {
    String input = "\n\n";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "false");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testOnlyNewlinesWithTokenizeNewlineTrue() {
    String input = "\n\n";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
  }

  @Test
  public void testCRHandlingInAdvancePos() {
    String input = "测\r试";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("测试")).thenReturn(Arrays.asList("测", "试"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("测", tokens.get(0).word());
    assertEquals("试", tokens.get(1).word());
  }

  @Test
  public void testMalformedXmlLikeContentHandledAsCharacters() {
    String input = "<notClosedTag 中文内容";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input))
        .thenReturn(Collections.singletonList("<notClosedTag中文内容"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0).word().contains("中文"));
  }

  @Test
  public void testSegmentingEmojiCharacters() {
    String input = "我爱😊你";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我爱😊你")).thenReturn(Arrays.asList("我", "爱", "😊", "你"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("爱", tokens.get(1).word());
    assertEquals("😊", tokens.get(2).word());
    assertEquals("你", tokens.get(3).word());
  }

  @Test
  public void testMultipleSpacesCollapsedInXmlNormalization() {
    String text = "<xml>  test  </xml>";
    List<String> segments = Collections.singletonList(text);
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(text)).thenReturn(segments);
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty("segment.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0).word().contains("\u00A0"));
  }

  @Test
  public void testXmlBoundaryTagClosedRightAway() {
    String text = "<xml/>abc";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("abc")).thenReturn(Arrays.asList("a", "b", "c"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("<xml/>", tokens.get(0).originalText());
    assertEquals("a", tokens.get(1).word());
    assertEquals("b", tokens.get(2).word());
    assertEquals("c", tokens.get(3).word());
  }

  @Test
  public void testAdvancePosThrowsRuntimeWhenMismatchOccurs() {
    String input = "abc";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("abc")).thenReturn(Arrays.asList("abcd"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    boolean thrown = false;
    try {
      annotator.annotate(annotation);
    } catch (RuntimeException e) {
      thrown = true;
    }
    assertTrue(thrown);
  }

  @Test
  public void testNewlineAtStartAndEndIsStrippedWithNewlineTokenization() {
    String input = "\n中山大学\n";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中山大学")).thenReturn(Arrays.asList("中山", "大学"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("中山", tokens.get(0).word());
    assertEquals("大学", tokens.get(1).word());
  }

  @Test
  public void testAdvancePosWithNonBmpCharacters() {
    String input = "中\uD83D\uDE0A文";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中😊文")).thenReturn(Arrays.asList("中", "😊", "文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("中", tokens.get(0).word());
    assertEquals("😊", tokens.get(1).word());
    assertEquals("文", tokens.get(2).word());
  }

  @Test
  public void testMakeXmlTokenWhitespacePreservesSpaceWhenNormalizeFalse() {
    String xml = " <tag> ";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(xml)).thenReturn(Arrays.asList(xml));
    Properties props = new Properties();
    props.setProperty("segment.model", "x");
    props.setProperty("segment.normalizeSpace", "false");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(xml);
    annotation.set(CoreAnnotations.TextAnnotation.class, xml);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0).word().contains(" "));
    assertFalse(tokens.get(0).word().contains("\u00A0"));
  }

  @Test
  public void testWhitespaceInsideXmlPreservedAsWhitespaceAttribute() {
    String text = "<xml> \n </xml>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(text)).thenReturn(Collections.singletonList(text));
    Properties props = new Properties();
    props.setProperty("segment.model", "x");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    CoreLabel xmlToken = tokens.get(0);
    assertEquals(text, xmlToken.originalText());
  }

  @Test
  public void testInputOfOnlyWhitespaceYieldsEmptyTokens() {
    String input = "   ";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testSegmenterModelLoadFailureThrowsRuntimeException() {
    Properties props = new Properties();
    props.setProperty("segment.model", "nonexistent/path");
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    RuntimeException cause = new RuntimeException("Failed to load model");
    try {
      ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props) {

            // {
            // throw cause;
            // }
          };
      fail("Expected RuntimeException was not thrown.");
    } catch (RuntimeException e) {
      assertEquals("Failed to load model", e.getMessage());
    }
  }

  @Test
  public void testSingleCharacterOnlyText() {
    String text = "你";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你")).thenReturn(Collections.singletonList("你"));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("你", tokens.get(0).word());
  }

  @Test
  public void testSegmenterReturnsEmptyStringToken() {
    String text = "测试";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    List<String> segments = Arrays.asList("测", "", "试");
    when(mockClassifier.segmentString("测试")).thenReturn(segments);
    Properties props = new Properties();
    props.setProperty("segment.model", "abc");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("测", tokens.get(0).word());
    assertEquals("试", tokens.get(1).word());
  }

  @Test
  public void testMultipleNonConsecutiveNewlinesWithSplitTwoSetting() {
    String input = "a\nb\n\nc";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("abc")).thenReturn(Arrays.asList("a", "b", "c"));
    Properties props = new Properties();
    props.setProperty("segment.model", "abc");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("b", tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(3).word());
    assertEquals("c", tokens.get(4).word());
  }

  @Test
  public void testChineseTextInsideXmlTagIsPreservedAsXmlToken() {
    String input = "<tag>你好</tag>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input)).thenReturn(Collections.singletonList(input));
    Properties props = new Properties();
    props.setProperty("segment.model", "xyz");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("<tag>你好</tag>", tokens.get(0).originalText());
    assertEquals("<tag>你好</tag>", tokens.get(0).word());
  }

  @Test
  public void testWhitespaceOnlyXmlIsTaggedAsXmlWhitespace() {
    String input = "<tag> \n </tag>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input)).thenReturn(Collections.singletonList(input));
    Properties props = new Properties();
    props.setProperty("segment.model", "xyz");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("<tag> \n </tag>", tokens.get(0).word());
  }

  @Test
  public void testMultipleXmlTagsAndPlainTextMixed() {
    String input = "<x1/>你好<x2/>世界";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好世界")).thenReturn(Arrays.asList("你", "好", "世", "界"));
    Properties props = new Properties();
    props.setProperty("segment.model", "a");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(6, tokens.size());
    assertEquals("<x1/>", tokens.get(0).word());
    assertEquals("你", tokens.get(1).word());
    assertEquals("好", tokens.get(2).word());
    assertEquals("<x2/>", tokens.get(3).word());
    assertEquals("世", tokens.get(4).word());
    assertEquals("界", tokens.get(5).word());
  }

  @Test
  public void testMultipleConsecutiveEmptySegmenterTokensHandledSafely() {
    String input = "国家安全法";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    List<String> segmented = Arrays.asList("国家", "", "", "安全", "", "法");
    when(mockClassifier.segmentString("国家安全法")).thenReturn(segmented);
    Properties props = new Properties();
    props.setProperty("segment.model", "model-path");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("国家", tokens.get(0).word());
    assertEquals("安全", tokens.get(1).word());
    assertEquals("法", tokens.get(2).word());
  }

  @Test
  public void testNewlineInMiddleOfXmlTagIgnoredCorrectly() {
    String input = "<tag\nname>文本</tag>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input)).thenReturn(Collections.singletonList(input));
    Properties props = new Properties();
    props.setProperty("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, result.size());
    assertEquals(input, result.get(0).originalText());
  }

  @Test
  public void testUnicodeSurrogatePairsFollowedByPlainText() {
    String input = "\uD83D\uDE02后";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("😂后")).thenReturn(Arrays.asList("😂", "后"));
    Properties props = new Properties();
    props.setProperty("segment.model", "surrogate");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("😂", result.get(0).word());
    assertEquals("后", result.get(1).word());
  }

  @Test
  public void testOnlyLineSeparatorsWithNewlineTokenizationOff() {
    String input = "\n\r\n\r";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "model");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "false");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation doc = new Annotation(input);
    doc.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(doc);
    List<CoreLabel> tokens = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testOnlyLineSeparatorsWithNewlineTokenizationOn() {
    String input = "\n\r\n\r";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "model");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation doc = new Annotation(input);
    doc.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(doc);
    List<CoreLabel> tokens = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
  }

  @Test
  public void testOnlyXmlSpecialTagProcessedAndTokenizedAsXml() {
    String input = "<?xml?><root>内容</root>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("内容")).thenReturn(Arrays.asList("内", "容"));
    Properties props = new Properties();
    props.setProperty("segment.model", "model");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertTrue(tokens.get(0).word().startsWith("<?xml"));
    assertEquals("内", tokens.get(1).word());
    assertEquals("容", tokens.get(2).word());
  }

  @Test
  public void testWhitespaceAndControlCharsAreIgnoredUnlessTokenizingNewlines() {
    String input = "\t\n中文\r";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中文")).thenReturn(Arrays.asList("中", "文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "model");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "false");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("中", tokens.get(0).word());
    assertEquals("文", tokens.get(1).word());
  }

  @Test
  public void testAdvancePosMismatchCrLfReplacement() {
    String input = "测\r试";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("测试")).thenReturn(Arrays.asList("测试"));
    Properties props = new Properties();
    props.setProperty("segment.model", "model");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("测试", tokens.get(0).word());
  }

  @Test
  public void testSegmenterReturnsMultiCharacterTokenButCharsAreIndividualCodepoints() {
    String input = "你好";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Collections.singletonList("你好"));
    Properties props = new Properties();
    props.setProperty("segment.model", "unit");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("你好", tokens.get(0).word());
    assertEquals(0, (int) tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(2, (int) tokens.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void testOnlyOpeningXmlTagAtEndOfTextProperlyBuffered() {
    String input = "内容<tag";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("内容<tag")).thenReturn(Collections.singletonList("内容<tag"));
    Properties props = new Properties();
    props.setProperty("segment.model", "x");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("内容<tag", tokens.get(0).word());
  }

  @Test
  public void testRealisticXmlAttributeTagWithEqualSign() {
    String input = "<tag attr=\"value\">中文</tag>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中文")).thenReturn(Arrays.asList("中", "文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "model");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.size() >= 3);
    assertTrue(tokens.get(0).word().startsWith("<tag"));
    assertEquals("中", tokens.get(1).word());
    assertEquals("文", tokens.get(2).word());
  }

  @Test
  public void testXmlAndCharacterBoundaryAlignmentProducingFinalTagToken() {
    String input = "abc</x>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("abc")).thenReturn(Arrays.asList("a", "b", "c"));
    Properties props = new Properties();
    props.setProperty("segment.model", "align");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("b", tokens.get(1).word());
    assertEquals("c", tokens.get(2).word());
    assertEquals("</x>", tokens.get(3).word());
  }

  @Test
  public void testNonMatchedXmlTagsBufferedAtEndProduceToken() {
    String input = "abc<tag";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("abc")).thenReturn(Arrays.asList("a", "b", "c"));
    Properties props = new Properties();
    props.setProperty("segment.model", "edge");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("b", tokens.get(1).word());
    assertEquals("c", tokens.get(2).word());
    assertEquals("<tag", tokens.get(3).word());
  }

  @Test
  public void testMultipleIndependentXmlTokensInSingleText() {
    String input = "<A/><B/>中文";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中文")).thenReturn(Arrays.asList("中", "文"));
    Properties props = new Properties();
    props.setProperty("segment.model", "multi");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("<A/>", tokens.get(0).word());
    assertEquals("<B/>", tokens.get(1).word());
    assertEquals("中", tokens.get(2).word());
    assertEquals("文", tokens.get(3).word());
  }

  @Test
  public void testWhitespaceOnlyInsideXmlTag() {
    String input = "<tag> \t\n </tag>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input)).thenReturn(Collections.singletonList(input));
    Properties props = new Properties();
    props.setProperty("segment.model", "x");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0).word().contains(" "));
  }

  @Test
  public void testEmptyStringReturnsNoTokens() {
    String input = "";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "x");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testSegmenterReturnsEmojiOnlyTokens() {
    String input = "😊😉";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input)).thenReturn(Arrays.asList("😊", "😉"));
    Properties props = new Properties();
    props.setProperty("segment.model", "emoji");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("😊", result.get(0).word());
    assertEquals("😉", result.get(1).word());
  }

  @Test
  public void testOnlyNewlinesWithSentenceBreakingTwo() {
    String input = "\n\n\n";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("segment.model", "lengths");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation document = new Annotation(input);
    document.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(document);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
  }

  @Test
  public void testUnicodeSurrogatePairFollowedBySpecialCharacter() {
    String input = "\uD83D\uDE80!";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input)).thenReturn(Arrays.asList("\uD83D\uDE80", "!"));
    Properties props = new Properties();
    props.setProperty("segment.model", "surrogates");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("\uD83D\uDE80", result.get(0).word());
    assertEquals("!", result.get(1).word());
  }

  @Test
  public void testModelPropertyIsMissingThrowsException() {
    Properties props = new Properties();
    props.setProperty("segment.verbose", "true");
    try {
      new ChineseSegmenterAnnotator("segment", props);
      fail("Expected RuntimeException to be thrown for missing segment.model");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Expected a property segment.model"));
    }
  }

  @Test
  public void testXMLWhitespacePreservationWithNormalizeSpaceTrue() {
    String input = "<tag> a b </tag>";
    String normalized = "<tag>\u00A0a\u00A0b\u00A0</tag>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(input)).thenReturn(Collections.singletonList(input));
    Properties props = new Properties();
    props.setProperty("segment.model", "x");
    props.setProperty("segment.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(annotation);
    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, result.size());
    assertTrue(result.get(0).word().contains("\u00A0"));
  }

  @Test
  public void testRunSegmentationConsumesMalformedBackslashNewlines() {
    String input = "a\\nb";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("a\\nb")).thenReturn(Arrays.asList("a", "\\n", "b"));
    Properties props = new Properties();
    props.setProperty("segment.model", "slashes");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation doc = new Annotation(input);
    doc.set(CoreAnnotations.TextAnnotation.class, input);
    annotator.annotate(doc);
    List<CoreLabel> tokens = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("\\n", tokens.get(1).word());
    assertEquals("b", tokens.get(2).word());
  }

  @Test
  public void testAdvancePosFailsGracefullyOnBadTokenMismatch() {
    String input = "你好";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("你好")).thenReturn(Collections.singletonList("你好吗"));
    Properties props = new Properties();
    props.setProperty("segment.model", "mismatch");
    ChineseSegmenterAnnotator annotator =
        new ChineseSegmenterAnnotator("segment", props) {

          {
            // this.segmenter = mockClassifier;
          }
        };
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);
    boolean threwException = false;
    try {
      annotator.annotate(annotation);
    } catch (RuntimeException e) {
      threwException = true;
    }
    assertTrue(threwException);
  }
}
