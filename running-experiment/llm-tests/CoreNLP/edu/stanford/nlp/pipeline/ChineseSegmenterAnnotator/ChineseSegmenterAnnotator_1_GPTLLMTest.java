package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.CoreMap;
import java.util.*;
import org.junit.Test;

public class ChineseSegmenterAnnotator_1_GPTLLMTest {

  @Test
  public void testAnnotateSimpleSentence() {
    String text = "我爱北京天安门";
    Annotation annotation = new Annotation(text);
    // CoreMap sentence = new TypesafeMap();
    // sentence.set(CoreAnnotations.TextAnnotation.class, text);
    List<CoreMap> sentences = new ArrayList<>();
    // sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    List<String> segmented = Arrays.asList("我", "爱", "北京", "天安门");
    when(classifier.segmentString("我爱北京天安门")).thenReturn(segmented);
    // ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg",
    // getMockProperties());
    // setClassifier(annotator, classifier);
    // annotator.annotate(annotation);
    CoreMap outSentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
    List<CoreLabel> tokens = outSentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("爱", tokens.get(1).word());
    assertEquals("北京", tokens.get(2).word());
    assertEquals("天安门", tokens.get(3).word());
  }

  @Test
  public void testNoSentenceAnnotation() {
    String text = "中国经济发展迅速";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    List<String> segmented = Arrays.asList("中国", "经济", "发展", "迅速");
    when(classifier.segmentString("中国经济发展迅速")).thenReturn(segmented);
    // ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg",
    // getMockProperties());
    // setClassifier(annotator, classifier);
    // annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("中国", tokens.get(0).word());
    assertEquals("经济", tokens.get(1).word());
    assertEquals("发展", tokens.get(2).word());
    assertEquals("迅速", tokens.get(3).word());
  }

  @Test
  public void testXmlTagHandling() {
    String text = "<tag>美国</tag>";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("美国")).thenReturn(Arrays.asList("美国"));
    // ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg",
    // getMockProperties());
    // setClassifier(annotator, classifier);
    // annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("<tag>", tokens.get(0).originalText());
    assertEquals("美国", tokens.get(1).word());
  }

  @Test
  public void testTokenizingNewlinesEnabled() {
    String text = "今天天气\n很好";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("今天天气")).thenReturn(Arrays.asList("今天", "天气"));
    when(classifier.segmentString("很好")).thenReturn(Arrays.asList("很好"));
    // Properties props = getMockProperties();
    // props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    // ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    // setClassifier(annotator, classifier);
    // annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("今天", tokens.get(0).word());
    assertEquals("天气", tokens.get(1).word());
    // assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("很好", tokens.get(3).word());
  }

  @Test
  public void testTwoNewlinesSentenceSplit() {
    String text = "数学很好\n\n物理也不错";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("数学很好")).thenReturn(Arrays.asList("数学", "很好"));
    when(classifier.segmentString("物理也不错")).thenReturn(Arrays.asList("物理", "也", "不错"));
    // Properties props = getMockProperties();
    // props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    // ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    // setClassifier(annotator, classifier);
    // annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(6, tokens.size());
    assertEquals("数学", tokens.get(0).word());
    assertEquals("很好", tokens.get(1).word());
    // assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    // assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(3).word());
    assertEquals("物理", tokens.get(4).word());
    assertEquals("也", tokens.get(5).word());
  }

  @Test
  public void testEmptySegmenterResult() {
    String text = "数据异常";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(text)).thenReturn(Collections.emptyList());
    // ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg",
    // getMockProperties());
    // setClassifier(annotator, classifier);
    // annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }

  @Test(expected = RuntimeException.class)
  public void testMissingModelPathThrowsException() {
    Properties props = new Properties();
    props.setProperty("seg.verbose", "false");
    new ChineseSegmenterAnnotator("seg", props);
  }

  @Test
  public void testNewlineOnlyInput() {
    String text = "\n\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    props.setProperty("seg.verbose", "false");
    props.setProperty("segmenter.newlineIsSentenceBreak", "two");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      fail("Reflection failed to inject mock classifier.");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }

  @Test
  public void testAllXmlInput() {
    String text = "<note><to>张三</to></note>";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("张三")).thenReturn(Arrays.asList("张", "三"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      fail("Reflection failed to inject classifier.");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3);
    assertEquals("<note>", tokens.get(0).originalText());
    assertEquals("<to>", tokens.get(1).originalText());
    assertEquals("张", tokens.get(2).word());
  }

  @Test
  public void testSurrogatePairCharacter() {
    String text = "A🙂B";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("A🙂B")).thenReturn(Arrays.asList("A", "🙂", "B"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      fail("Reflection failed to inject classifier.");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("A", tokens.get(0).word());
    assertEquals("🙂", tokens.get(1).word());
    assertEquals("B", tokens.get(2).word());
  }

  @Test
  public void testUnicodeWhitespaceIgnored() {
    String text = "我\u2003爱北京";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我爱北京")).thenReturn(Arrays.asList("我", "爱", "北京"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      fail("Failed to inject mock classifier");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("爱", tokens.get(1).word());
    assertEquals("北京", tokens.get(2).word());
  }

  @Test
  public void testEmptyStringInput() {
    String text = "";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      fail("Failed to inject mock classifier");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }

  @Test(expected = RuntimeException.class)
  public void testAdvancePosThrowsOnMismatch() {
    String text = "你好";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你好")).thenReturn(Arrays.asList("你", "哈"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      fail("Failed to inject mock classifier");
    }
    annotator.annotate(annotation);
  }

  @Test
  public void testLeadingAndTrailingNewlinesPreservedWhenTokenizeNewlineTrue() {
    String text = "\n我爱\n北京\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我爱")).thenReturn(Arrays.asList("我", "爱"));
    when(classifier.segmentString("北京")).thenReturn(Arrays.asList("北京"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    props.setProperty("segmenter.newlineSplitter", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      fail("Failed injection");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("我", tokens.get(0).word());
    assertEquals("爱", tokens.get(1).word());
    // assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("北京", tokens.get(3).word());
    // assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(4).word());
  }

  @Test
  public void testXmlWhitespacePreservedAsSpaceToken() {
    String text = "<tag> \t</tag>";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      fail("Reflection failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0).originalText().contains("<tag>"));
  }

  @Test
  public void testSpaceNormalizationEnabled() {
    String text = "A B";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("AB")).thenReturn(Arrays.asList("A", "B"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    props.setProperty("seg.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      fail("Reflection error");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("A", tokens.get(0).word());
    assertEquals("B", tokens.get(1).word());
  }

  @Test
  public void testMultipleWhitespaceCharactersIgnoredForSegmentation() {
    String text = "你\t  \n好";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    props.setProperty("segmenter.newlineSplitter", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      fail("Injection failure");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("你", tokens.get(0).word());
    assertEquals("好", tokens.get(1).word());
  }

  @Test
  public void testNewlineSeparatorCompatibilityCrossPlatform() {
    String text = "天\r\n安\r门";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("天安门")).thenReturn(Arrays.asList("天", "安", "门"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      fail("Failed reflection setup");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("天", tokens.get(0).word());
    assertEquals("安", tokens.get(1).word());
    assertEquals("门", tokens.get(2).word());
  }

  @Test
  public void testTokenRightNextToClosingXmlTag() {
    String text = "<person>张三</person>是";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("张三是")).thenReturn(Arrays.asList("张", "三", "是"));
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      fail("Error injecting classifier");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 4);
    assertEquals("<person>", tokens.get(0).originalText());
    assertEquals("张", tokens.get(1).word());
    assertEquals("三", tokens.get(2).word());
    assertEquals("</person>", tokens.get(3).originalText());
  }

  @Test
  public void testSegmenterReturnsEmptyStringToken() {
    String text = "测试";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    List<String> invalidOutput = Arrays.asList("测", "", "试");
    when(classifier.segmentString("测试")).thenReturn(invalidOutput);
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "mock-dict");
    props.setProperty("seg.sighanCorporaDict", "mock-corpora");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      fail("Injection error");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("测", tokens.get(0).word());
    assertEquals("试", tokens.get(1).word());
  }

  @Test
  public void testCoreMapWithoutTextAnnotation() {
    // CoreMap sentence = new TypesafeMap();
    List<CoreMap> sentences = new ArrayList<>();
    // sentences.add(sentence);
    Annotation annotation = new Annotation("dummy");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(anyString())).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "fake");
    props.setProperty("seg.sighanCorporaDict", "fake");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject segmenter.");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNull(tokens);
  }

  @Test
  public void testAllControlCharactersIgnored() {
    String text = "\u0001\u0002\u0003\u0004";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "mock-model");
    props.setProperty("seg.serDictionary", "path");
    props.setProperty("seg.sighanCorporaDict", "path");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field segmenterField =
          ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      segmenterField.setAccessible(true);
      segmenterField.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException("Injection failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }

  @Test
  public void testLongTokenReturnedFromSegmenter() {
    String text = "我爱中";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我爱中")).thenReturn(Arrays.asList("我爱中"));
    Properties props = new Properties();
    props.setProperty("seg.model", "m");
    props.setProperty("seg.serDictionary", "x");
    props.setProperty("seg.sighanCorporaDict", "y");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("我爱中", tokens.get(0).word());
  }

  @Test
  public void testSingleChineseCharacter() {
    String text = "中";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("中")).thenReturn(Arrays.asList("中"));
    Properties props = new Properties();
    props.setProperty("seg.model", "a");
    props.setProperty("seg.serDictionary", "b");
    props.setProperty("seg.sighanCorporaDict", "c");
    props.setProperty("segmenter.newlineSplitter", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("中", tokens.get(0).word());
  }

  @Test
  public void testSegmenterReturnsMoreTokensThanChars() {
    String text = "动态";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("动态")).thenReturn(Arrays.asList("动", "态", "extra"));
    Properties props = new Properties();
    props.setProperty("seg.model", "a");
    props.setProperty("seg.serDictionary", "b");
    props.setProperty("seg.sighanCorporaDict", "c");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException("reflect failed");
    }
    try {
      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertEquals(3, tokens.size());
    } catch (Exception e) {
      fail("No exception expected when segmenter returns extra tokens: " + e.getMessage());
    }
  }

  @Test
  public void testNewlineReturnedAsLiteralToken() {
    String text = "你好\n世界";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    List<String> segmentResult = Arrays.asList("你", "好", "\n", "世界");
    when(classifier.segmentString(anyString())).thenReturn(segmentResult);
    Properties props = new Properties();
    props.setProperty("seg.model", "p");
    props.setProperty("seg.serDictionary", "q");
    props.setProperty("seg.sighanCorporaDict", "r");
    props.setProperty("segmenter.newlineSplitter", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException("failed set segmenter");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("你", tokens.get(0).word());
    assertEquals("好", tokens.get(1).word());
    // assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("世界", tokens.get(3).word());
  }

  @Test
  public void testWhitespaceOnlyText() {
    String text = "    ";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "m");
    props.setProperty("seg.serDictionary", "dict");
    props.setProperty("seg.sighanCorporaDict", "corp");
    ChineseSegmenterAnnotator segmenter = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(segmenter, classifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    segmenter.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testLoneHighSurrogate() {
    String text = "\uD83D";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "mo");
    props.setProperty("seg.serDictionary", "d");
    props.setProperty("seg.sighanCorporaDict", "c");
    ChineseSegmenterAnnotator segmenter = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(segmenter, classifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    segmenter.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testVeryLongInput() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5000; i++) {
      sb.append("中");
    }
    String text = sb.toString();
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    List<String> segments = new ArrayList<>();
    segments.add(text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(text)).thenReturn(segments);
    Properties props = new Properties();
    props.setProperty("seg.model", "x");
    props.setProperty("seg.serDictionary", "x");
    props.setProperty("seg.sighanCorporaDict", "x");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals(text, tokens.get(0).word());
  }

  @Test
  public void testSegmenterReturnsOverlappingTokens() {
    String text = "中国人民";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    List<String> segments = Arrays.asList("中国", "国人民");
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(text)).thenReturn(segments);
    Properties props = new Properties();
    props.setProperty("seg.model", "z");
    props.setProperty("seg.serDictionary", ".");
    props.setProperty("seg.sighanCorporaDict", ".");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
    } catch (Exception e) {
      assertTrue(e instanceof RuntimeException);
    }
  }

  @Test
  public void testSegmenterReturnsDuplicateTokens() {
    String text = "你好";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    List<String> segments = Arrays.asList("你", "好", "你", "好");
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(text)).thenReturn(segments);
    Properties props = new Properties();
    props.setProperty("seg.model", "zz");
    props.setProperty("seg.serDictionary", "z");
    props.setProperty("seg.sighanCorporaDict", "z");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    try {
      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
      assertTrue(tokens.size() >= 2);
    } catch (Exception e) {
      fail("Unexpected exception on duplicate tokens: " + e.getMessage());
    }
  }

  @Test
  public void testChineseEmojiDigitsMixed() {
    String text = "我😊123你";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    List<String> segments = Arrays.asList("我", "😊", "123", "你");
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(text)).thenReturn(segments);
    Properties props = new Properties();
    props.setProperty("seg.model", "m");
    props.setProperty("seg.serDictionary", "m");
    props.setProperty("seg.sighanCorporaDict", "m");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("😊", tokens.get(1).word());
    assertEquals("123", tokens.get(2).word());
    assertEquals("你", tokens.get(3).word());
  }

  @Test
  public void testSegmenterReturnsNull() {
    String text = "中文";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(text)).thenReturn(null);
    Properties props = new Properties();
    props.setProperty("seg.model", "wow");
    props.setProperty("seg.serDictionary", "x");
    props.setProperty("seg.sighanCorporaDict", "x");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException when segmenter returns null");
    } catch (Exception e) {
      assertTrue(e instanceof NullPointerException);
    }
  }

  @Test
  public void testEmptySentencesListProvided() {
    Annotation annotation = new Annotation("hello");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(anyString())).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "a");
    props.setProperty("seg.serDictionary", "b");
    props.setProperty("seg.sighanCorporaDict", "c");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNull(tokens);
  }

  @Test
  public void testSentenceListContainsNullEntries() {
    // CoreMap valid = new TypesafeMap();
    // valid.set(CoreAnnotations.TextAnnotation.class, "你好");
    List<CoreMap> list = new ArrayList<>();
    // list.add(valid);
    list.add(null);
    Annotation annotation = new Annotation("dummy");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, list);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("seg.model", "ab");
    props.setProperty("seg.serDictionary", "ab");
    props.setProperty("seg.sighanCorporaDict", "ab");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException("inject segmenter failed");
    }
    try {
      annotator.annotate(annotation);
      List<CoreMap> outList = annotation.get(CoreAnnotations.SentencesAnnotation.class);
      assertNotNull(outList);
    } catch (Exception ex) {
      fail("Should not throw on null sentence in list");
    }
  }

  @Test
  public void testNestedXMLPlusChineseText() {
    String text = "<root><tag>西湖美</tag>很漂亮</root>";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("西湖美很漂亮")).thenReturn(Arrays.asList("西湖", "美", "很", "漂亮"));
    Properties props = new Properties();
    props.setProperty("seg.model", "abc");
    props.setProperty("seg.serDictionary", "abc");
    props.setProperty("seg.sighanCorporaDict", "abc");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception x) {
      throw new RuntimeException(x);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 4);
  }

  @Test
  public void testMissingSighanDictionaryProperty() {
    String text = "我爱中国";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我爱中国")).thenReturn(Arrays.asList("我", "爱", "中国"));
    Properties props = new Properties();
    props.setProperty("seg.model", "dummyModel");
    props.setProperty("seg.serDictionary", "mockDict");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException("failed injection");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("中国", tokens.get(2).word());
  }

  @Test
  public void testNewlineInXmlTagIgnored() {
    String xmlNewline = "<tag>\n</tag>你好";
    Annotation annotation = new Annotation(xmlNewline);
    annotation.set(CoreAnnotations.TextAnnotation.class, xmlNewline);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = new Properties();
    props.setProperty("seg.model", "x");
    props.setProperty("seg.serDictionary", "x");
    props.setProperty("seg.sighanCorporaDict", "x");
    props.setProperty("segmenter.newlineSplitter", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception err) {
      throw new RuntimeException(err);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.stream().anyMatch(t -> "你".equals(t.word())));
    assertTrue(tokens.stream().anyMatch(t -> "好".equals(t.word())));
  }

  @Test
  public void testSurrogatePairEndsAtTokenBoundary() {
    String text = "你😊";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你😊")).thenReturn(Arrays.asList("你", "😊"));
    Properties props = new Properties();
    props.setProperty("seg.model", "y");
    props.setProperty("seg.serDictionary", "y");
    props.setProperty("seg.sighanCorporaDict", "y");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException("reflective access failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("你", tokens.get(0).word());
    assertEquals("😊", tokens.get(1).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) >= 1);
  }

  @Test
  public void testControlCharactersInsideXmlDoNotTriggerSegmentation() {
    String text = "<root>\u0003我\n</root>喜欢";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我喜欢")).thenReturn(Arrays.asList("我", "喜欢"));
    Properties props = new Properties();
    props.setProperty("seg.model", "zzz");
    props.setProperty("seg.serDictionary", ".");
    props.setProperty("seg.sighanCorporaDict", ".");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException("reflection failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.stream().anyMatch(t -> "喜欢".equals(t.word())));
  }

  @Test
  public void testInvalidXmlNotParsedAsTag() {
    String text = "<!@#??>中文</broken>>";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("中文")).thenReturn(Arrays.asList("中", "文"));
    Properties props = new Properties();
    props.setProperty("seg.model", "abc");
    props.setProperty("seg.serDictionary", "abc");
    props.setProperty("seg.sighanCorporaDict", "abc");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception err) {
      throw new RuntimeException("fail reflect");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.stream().anyMatch(t -> "中".equals(t.word())));
    assertTrue(tokens.stream().anyMatch(t -> "文".equals(t.word())));
  }

  @Test
  public void testNoCharacterOffsetInCoreLabelStillProcessed() {
    String text = "数据";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("数据")).thenReturn(Arrays.asList("数", "据"));
    Properties props = new Properties();
    props.setProperty("seg.model", "t");
    props.setProperty("seg.serDictionary", "dict");
    props.setProperty("seg.sighanCorporaDict", "dict");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception err) {
      throw new RuntimeException(err);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }

  @Test
  public void testAnnotationContainsMixedValidAndEmptySentences() {
    String text = "天气不错";
    Annotation annotation = new Annotation(text);
    // CoreMap sent1 = new TypesafeMap();
    // sent1.set(CoreAnnotations.TextAnnotation.class, "今天天气");
    // CoreMap sent2 = new TypesafeMap();
    // sent2.set(CoreAnnotations.TextAnnotation.class, "");
    List<CoreMap> sentences = new ArrayList<>();
    // sentences.add(sent1);
    // sentences.add(sent2);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("今天天气")).thenReturn(Arrays.asList("今天", "天气"));
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "model");
    props.setProperty("seg.serDictionary", "dict");
    props.setProperty("seg.sighanCorporaDict", "dict");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception err) {
      throw new RuntimeException(err);
    }
    annotator.annotate(annotation);
    List<CoreMap> storedSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    CoreMap updatedFirst = storedSentences.get(0);
    assertNotNull(updatedFirst.get(CoreAnnotations.TokensAnnotation.class));
    assertTrue(updatedFirst.get(CoreAnnotations.TokensAnnotation.class).size() >= 2);
    CoreMap updatedSecond = storedSentences.get(1);
    assertNotNull(updatedSecond.get(CoreAnnotations.TokensAnnotation.class));
    assertEquals(0, updatedSecond.get(CoreAnnotations.TokensAnnotation.class).size());
  }

  @Test(expected = RuntimeException.class)
  public void testMissingModelPropertyFails() {
    Properties props = new Properties();
    props.setProperty("seg.verbose", "true");
    new ChineseSegmenterAnnotator("seg", props);
  }

  @Test
  public void testNewlineSingleRemovedWhenTwoNewlinesSplitEnabled() {
    String text = "段落一\n段落二\n\n段落三";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString(anyString()))
        .thenAnswer(
            invocation -> {
              Object arg = invocation.getArguments()[0];
              return Collections.singletonList(arg.toString());
            });
    Properties props = new Properties();
    props.setProperty("seg.model", "model");
    props.setProperty("seg.serDictionary", "dict");
    props.setProperty("seg.sighanCorporaDict", "dict");
    props.setProperty("segmenter.newlineIsSentenceBreak", "two");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, mockClassifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    boolean hasDoubleNewline =
        tokens.stream().filter(t -> "\n".equals(t.originalText())).count() >= 2;
    assertTrue(hasDoubleNewline);
  }

  @Test
  public void testPreserveNewlineWhenTokenizeEnabled() {
    String text = "\n中\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("中")).thenReturn(Collections.singletonList("中"));
    Properties props = new Properties();
    props.setProperty("seg.model", "m");
    props.setProperty("seg.serDictionary", "m");
    props.setProperty("seg.sighanCorporaDict", "m");
    props.setProperty("segmenter.newlineSplitter", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException("reflection failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("中", tokens.get(0).word());
    // assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
  }

  @Test
  public void testSegmenterReturnsWholeInputAsSingleToken() {
    String text = "中华人民共和国";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("中华人民共和国")).thenReturn(Collections.singletonList("中华人民共和国"));
    Properties props = new Properties();
    props.setProperty("seg.model", "a");
    props.setProperty("seg.serDictionary", "b");
    props.setProperty("seg.sighanCorporaDict", "c");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("中华人民共和国", tokens.get(0).word());
  }

  @Test
  public void testSegmenterReturnsEmptyBetweenValidTokens() {
    String text = "中国";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("中国")).thenReturn(Arrays.asList("中", "", "国"));
    Properties props = new Properties();
    props.setProperty("seg.model", "t");
    props.setProperty("seg.serDictionary", "x");
    props.setProperty("seg.sighanCorporaDict", "x");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field field = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException("injection failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("中", tokens.get(0).word());
    assertEquals("国", tokens.get(1).word());
  }

  @Test
  public void testNewlineOnlyTextGetsNoTokensWhenNotTokenizingNewlines() {
    String text = "\n\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = new Properties();
    props.setProperty("seg.model", "m");
    props.setProperty("seg.serDictionary", "d");
    props.setProperty("seg.sighanCorporaDict", "d");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException("injection failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }

  @Test
  public void testNullChineseCharAnnotationHandled() {
    String text = "abc";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("abc")).thenReturn(Arrays.asList("a", "b", "c"));
    Properties props = new Properties();
    props.setProperty("seg.model", "d");
    props.setProperty("seg.serDictionary", "d");
    props.setProperty("seg.sighanCorporaDict", "d");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("seg", props);
    try {
      java.lang.reflect.Field f = ChineseSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, classifier);
    } catch (Exception ex) {
      throw new RuntimeException("injection failed");
    }
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("a", tokens.get(0).word());
  }
}
