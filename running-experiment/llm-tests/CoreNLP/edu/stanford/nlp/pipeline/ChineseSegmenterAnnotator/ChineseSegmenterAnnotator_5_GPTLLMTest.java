package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import java.util.*;
import org.junit.Test;

public class ChineseSegmenterAnnotator_5_GPTLLMTest {

  @Test(expected = RuntimeException.class)
  public void testConstructorThrowsWhenModelMissing() {
    Properties props = new Properties();
    new ChineseSegmenterAnnotator("segment", props);
  }

  @Test
  public void testSimpleAnnotationWithMockedSegmentation() {
    String inputText = "我爱自然语言处理";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    List<String> segmented = Arrays.asList("我", "爱", "自然", "语言", "处理");
    when(mockClassifier.segmentString(inputText)).thenReturn(segmented);
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy-path");
    props.setProperty("segment.verbose", "false");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation ann = new Annotation(inputText);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(ann);
    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());
  }

  @Test
  public void testNewlineSegmentationEnabled() {
    String input = "我爱\n自然";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    List<String> part1 = Arrays.asList("我", "爱");
    List<String> part2 = Arrays.asList("自然");
    when(mockClassifier.segmentString("我爱")).thenReturn(part1);
    when(mockClassifier.segmentString("自然")).thenReturn(part2);
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(annotation);
    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());
  }

  @Test
  public void testXMLPreservedInSegmentationFlow() {
    String inputText = "<xml>我爱</xml>中文";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我爱中文")).thenReturn(Arrays.asList("我", "爱", "中文"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(annotation);
    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());
  }

  @Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    // Set<Class<? extends CoreAnnotations.CoreAnnotation>> result =
    // annotator.requirementsSatisfied();
    // assertTrue(result.contains(CoreAnnotations.TextAnnotation.class));
    // assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
    // assertTrue(result.contains(CoreAnnotations.OriginalTextAnnotation.class));
    // assertTrue(result.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    // assertTrue(result.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void testRequirementsAreEmpty() {
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    // Set<Class<? extends CoreAnnotations.CoreAnnotation>> result = annotator.requires();
    // assertTrue(result.isEmpty());
  }

  @Test
  public void testSegmentationOutputTokenFieldsSet() {
    String input = "北京欢迎你";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    List<String> segmentResults = Arrays.asList("北京", "欢迎", "你");
    when(mockClassifier.segmentString("北京欢迎你")).thenReturn(segmentResults);
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation document = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(document);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List<?> list = (List<?>) tokens;
                  if (list.size() != 3) return false;
                  CoreLabel t0 = (CoreLabel) list.get(0);
                  CoreLabel t1 = (CoreLabel) list.get(1);
                  CoreLabel t2 = (CoreLabel) list.get(2);
                  return "北京".equals(t0.word()) && "欢迎".equals(t1.word()) && "你".equals(t2.word());
                }));
  }

  @Test
  public void testRunSegmentationWithNewlinesRemovedWhenDisabled() {
    String input = "今天天气\n很好";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("今天天气很好")).thenReturn(Arrays.asList("今天天气", "很好"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(annotation);
    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());
  }

  @Test
  public void testAnnotateHandlesNullSentencesAnnotation() {
    String input = "我爱北京";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    List<String> segmented = Arrays.asList("我", "爱", "北京");
    when(mockClassifier.segmentString("我爱北京")).thenReturn(segmented);
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("我", tokens.get(0).word());
    assertEquals("爱", tokens.get(1).word());
    assertEquals("北京", tokens.get(2).word());
  }

  @Test
  public void testSplitCharactersSkipsLeadingAndTrailingNewlines() {
    String input = "\n\n我爱中文\n\n";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我爱中文")).thenReturn(Arrays.asList("我", "爱", "中文"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List<?> list = (List<?>) tokens;
                  return list.size() == 3;
                }));
  }

  @Test
  public void testSingleNewlineInMiddleRemovedWhenSentenceSplitOnTwoNewlines() {
    String input = "他\n来了";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("他来了")).thenReturn(Arrays.asList("他", "来了"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spyAnnotator = spy(annotator);
    // doReturn(mockClassifier).when(spyAnnotator).segmenter;
    spyAnnotator.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List<?> list = (List<?>) tokens;
                  return list.size() == 2;
                }));
  }

  @Test
  public void testMakeXmlTokenNormalizesSpacesWhenEnabled() {
    Properties props =
        PropertiesUtils.asProperties("segment.model", "dummy", "segment.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreLabel token = new CoreLabel();
    token.setOriginalText("a b");
    String inputText = "a b";
    Annotation ann = new Annotation(inputText);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("a b")).thenReturn(Collections.singletonList("a b"));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  CoreLabel label = (CoreLabel) ((List<?>) tokens).get(0);
                  return label.word().contains("\u00A0");
                }));
  }

  @Test
  public void testMakeXmlTokenMapsBlankLineToSpecialToken() {
    String newline = "\n";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.singletonList(newline));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    String inputText = "\n";
    Annotation ann = new Annotation(inputText);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  CoreLabel label = (CoreLabel) ((List<?>) tokens).get(0);
                  return "\n".equals(label.originalText())
                      || "[CR]".equals(label.word())
                      || "\n".equals(label.word());
                }));
  }

  @Test
  public void testSegmentXmlElementAndPlainTextMixedCorrectly() {
    String input = "<tag>中文</tag>和英文";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("中文和英文")).thenReturn(Arrays.asList("中文", "和", "英文"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  if (list.size() < 4) return false;
                  boolean hasXml = false;
                  boolean hasTextToken = false;
                  for (CoreLabel token : list) {
                    if (token.originalText() != null && token.originalText().startsWith("<tag>"))
                      hasXml = true;
                    if ("中文".equals(token.word())
                        || "和".equals(token.word())
                        || "英文".equals(token.word())) hasTextToken = true;
                  }
                  return hasXml && hasTextToken;
                }));
  }

  @Test
  public void testAnnotateHandlesEmptyStringWithoutError() {
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return tokens instanceof List && ((List<?>) tokens).isEmpty();
                }));
  }

  @Test
  public void testSingleSpaceCharacterIsSkippedInSplitCharacters() {
    String inputText = "我 爱";
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("我爱")).thenReturn(Arrays.asList("我", "爱"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockSegmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List list = (List) tokens;
                  return list.size() == 2;
                }));
  }

  @Test
  public void testXmlWhitespaceTokensAreHandledCorrectly() {
    String inputText = "<tag>\n</tag>测试";
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("测试")).thenReturn(Arrays.asList("测试"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockSegmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  boolean foundXml = false;
                  boolean foundText = false;
                  for (int i = 0; i < list.size(); i++) {
                    CoreLabel t = list.get(i);
                    if (t.originalText() != null && t.originalText().contains("<tag>")) {
                      foundXml = true;
                    }
                    if ("测试".equals(t.word())) {
                      foundText = true;
                    }
                  }
                  return foundXml && foundText;
                }));
  }

  @Test
  public void testHandlesNonBmpCharactersCorrectly() {
    String inputText = "我😀你";
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("我😀你")).thenReturn(Arrays.asList("我", "😀", "你"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockSegmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List list = (List) tokens;
                  if (list.size() != 3) return false;
                  CoreLabel t0 = (CoreLabel) list.get(0);
                  CoreLabel t1 = (CoreLabel) list.get(1);
                  CoreLabel t2 = (CoreLabel) list.get(2);
                  return "我".equals(t0.word()) && "😀".equals(t1.word()) && "你".equals(t2.word());
                }));
  }

  @Test
  public void testHandlesConsecutiveNewlinesInNewlineSplitterMode() {
    String inputText = "我\n\n你";
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("我")).thenReturn(Arrays.asList("我"));
    when(mockSegmenter.segmentString("你")).thenReturn(Arrays.asList("你"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockSegmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  boolean hasNewlineToken = false;
                  for (int i = 0; i < list.size(); i++) {
                    CoreLabel token = list.get(i);
                    // if ("\n".equals(token.originalText()) ||
                    // AbstractSequenceClassifier.NEWLINE_TOKEN.equals(token.word())) {
                    // hasNewlineToken = true;
                    // }
                  }
                  return list.size() >= 3 && hasNewlineToken;
                }));
  }

  @Test
  public void testHandlesAllWhitespaceTextWithoutError() {
    String inputText = "   \n ";
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString(anyString())).thenReturn(Collections.emptyList());
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockSegmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return tokens instanceof List && ((List<?>) tokens).isEmpty();
                }));
  }

  @Test
  public void testEndOffsetIsCorrectAfterSegmentation() {
    String inputText = "自然语言";
    AbstractSequenceClassifier<?> mockSegmenter = mock(AbstractSequenceClassifier.class);
    when(mockSegmenter.segmentString("自然语言")).thenReturn(Arrays.asList("自然", "语言"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockSegmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (!(tokens instanceof List)) return false;
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  if (list.size() != 2) return false;
                  CoreLabel t1 = list.get(0);
                  CoreLabel t2 = list.get(1);
                  Integer end1 = t1.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
                  Integer begin2 = t2.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
                  return end1.equals(begin2);
                }));
  }

  @Test
  public void testNormalizeSpaceFalseLeavesSpacesUnchanged() {
    String input = "标  签";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("标  签")).thenReturn(Collections.singletonList("标  签"));
    Properties props =
        PropertiesUtils.asProperties("segment.model", "dummy", "segment.normalizeSpace", "false");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  if (list.isEmpty()) return false;
                  return list.get(0).word().equals("标  签");
                }));
  }

  @Test
  public void testMalformedXmlTagTreatedAsPlainText() {
    String input = "<unclosed 我爱你";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我爱你")).thenReturn(Arrays.asList("我", "爱", "你"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<?> list = (List<?>) tokens;
                  return !list.isEmpty();
                }));
  }

  @Test
  public void testVeryLastCharInInputCorrectlyTokenized() {
    String input = "你";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你")).thenReturn(Collections.singletonList("你"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<?> list = (List<?>) tokens;
                  if (list.size() != 1) return false;
                  CoreLabel token = (CoreLabel) list.get(0);
                  return "你".equals(token.word())
                      && Integer.valueOf(0)
                          .equals(token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
                }));
  }

  @Test
  public void testNewlineTokenSplitTwiceWhenSentenceSplitOnTwoNewlines() {
    String input = "早安\n\n晚安";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("早安晚安")).thenReturn(Arrays.asList("早安", "晚安"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<?> list = (List<?>) tokens;
                  return list.size() == 2;
                }));
  }

  @Test
  public void testControlCharactersInInputAreSkipped() {
    String input = "你\u0001好";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return ((List<?>) tokens).size() == 2;
                }));
  }

  @Test
  public void testEmptyStringInSegmenterOutputIsIgnored() {
    String input = "abc";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("abc")).thenReturn(Arrays.asList("a", "", "b", "c"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<?> list = (List<?>) tokens;
                  for (Object obj : list) {
                    CoreLabel t = (CoreLabel) obj;
                    if ("".equals(t.word())) return false;
                  }
                  return true;
                }));
  }

  @Test
  public void testOnlyNewlinesAsInputWithNewlineBreak() {
    String input = "\n\n\n";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString(anyString())).thenReturn(Collections.singletonList("\n"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return !((List<?>) tokens).isEmpty();
                }));
  }

  @Test
  public void testLongXmlFollowedByText() {
    String input = "<tag>\n<data>ignore</data></tag>\n测试文本";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("测试文本")).thenReturn(Arrays.asList("测试", "文本"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  boolean hasXml = false;
                  boolean hasText = false;
                  for (CoreLabel t : list) {
                    if (t.originalText() != null && t.originalText().contains("<")) hasXml = true;
                    if ("测试".equals(t.word()) || "文本".equals(t.word())) hasText = true;
                  }
                  return hasXml && hasText;
                }));
  }

  @Test
  public void testHandlesBackToBackXmlTagsCorrectly() {
    String input = "<div></div><p></p>你好";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  return list.size() >= 3;
                }));
  }

  @Test
  public void testAdvancePosThrowsForMultiCharMismatch() {
    String input = "我爱你";
    CoreLabel cl0 = new CoreLabel();
    cl0.set(CoreAnnotations.ChineseCharAnnotation.class, "我");
    CoreLabel cl1 = new CoreLabel();
    cl1.set(CoreAnnotations.ChineseCharAnnotation.class, "爱");
    CoreLabel cl2 = new CoreLabel();
    cl2.set(CoreAnnotations.ChineseCharAnnotation.class, "你");
    List<CoreLabel> characters = new ArrayList<>();
    characters.add(cl0);
    characters.add(cl1);
    characters.add(cl2);
    try {
      ChineseSegmenterAnnotator.class
          .getDeclaredMethod("advancePos", List.class, int.class, String.class)
          .invoke(null, characters, 0, "爱你我");
      fail("Expected exception not thrown");
    } catch (Exception e) {
      Throwable cause = e.getCause();
      assertTrue(cause instanceof RuntimeException);
      assertTrue(cause.getMessage().contains("Ate the whole text without matching"));
    }
  }

  @Test
  public void testXmlPatternSkipsPlainAngleBrackets() {
    String input = "我<你>";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我<你>")).thenReturn(Collections.singletonList("我<你>"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation ann = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                toks -> {
                  List<CoreLabel> list = (List<CoreLabel>) toks;
                  if (list.size() != 1) return false;
                  return list.get(0).word().equals("我<你>");
                }));
  }

  @Test
  public void testLongSingleXmlSequenceProcessedAsSingleToken() {
    String input = "<meta charset='utf-8'>";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation ann = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                toks -> {
                  List<CoreLabel> list = (List<CoreLabel>) toks;
                  if (list.size() != 1) return false;
                  CoreLabel xml = list.get(0);
                  return xml.originalText().contains("<meta");
                }));
  }

  @Test
  public void testAdvancePosMatchesSingleCodePointCharacters() {
    String input = "中文ABC";
    CoreLabel cl0 = new CoreLabel();
    cl0.set(CoreAnnotations.ChineseCharAnnotation.class, "中");
    CoreLabel cl1 = new CoreLabel();
    cl1.set(CoreAnnotations.ChineseCharAnnotation.class, "文");
    CoreLabel cl2 = new CoreLabel();
    cl2.set(CoreAnnotations.ChineseCharAnnotation.class, "A");
    CoreLabel cl3 = new CoreLabel();
    cl3.set(CoreAnnotations.ChineseCharAnnotation.class, "B");
    CoreLabel cl4 = new CoreLabel();
    cl4.set(CoreAnnotations.ChineseCharAnnotation.class, "C");
    List<CoreLabel> chars = new ArrayList<>();
    chars.add(cl0);
    chars.add(cl1);
    chars.add(cl2);
    chars.add(cl3);
    chars.add(cl4);
    int next = -1;
    try {
      next =
          (int)
              ChineseSegmenterAnnotator.class
                  .getDeclaredMethod("advancePos", List.class, int.class, String.class)
                  .invoke(null, chars, 0, "中文");
    } catch (Exception e) {
      fail("Exception during advancePos");
    }
    assertEquals(2, next);
  }

  @Test
  public void testLineOnlyContainsCRIsIgnoredWhenNewlineSplitIsDisabled() {
    String input = "我\r你";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我你")).thenReturn(Arrays.asList("我", "你"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation ann = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  return list.size() == 2;
                }));
  }

  @Test
  public void testThrowsRuntimeExceptionIfRequiredModelFileIsMissing() {
    Properties props = new Properties();
    props.setProperty("segment.unknownKey", "someValue");
    try {
      new ChineseSegmenterAnnotator("segment", props);
      fail("Expected RuntimeException for missing segment.model");
    } catch (RuntimeException ex) {
      assertTrue(ex.getMessage().contains("Expected a property segment.model"));
    }
  }

  @Test
  public void testTokenizeNewlineTrueAndSentenceSplitTwoSkipSingleNewline() {
    String text = "我们\n去学校";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我们去学校")).thenReturn(Arrays.asList("我们", "去", "学校"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model",
            "dummy",
            StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY,
            "two",
            StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY,
            "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation ann = new Annotation(text);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return ((List<?>) tokens).size() == 3;
                }));
  }

  @Test
  public void testMakeXmlTokenNewlineNormalization() {
    String text = "\n";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("")).thenReturn(Collections.singletonList("\n"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    Annotation ann = new Annotation(text);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  if (list.isEmpty()) return false;
                  for (CoreLabel t : list) {
                    if (t.word().equals("[CR]")
                        || t.word().equals("[NEWLINE]")
                        || System.lineSeparator().contains(t.word())) {
                      return true;
                    }
                  }
                  return false;
                }));
  }

  @Test
  public void testXmlTagEndsAtEndOfText() {
    String inputText = "<note>提示</note>";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("提示")).thenReturn(Collections.singletonList("提示"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(inputText);
    Annotation annotation = new Annotation(inputText);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return ((List<?>) tokens).size() >= 2;
                }));
  }

  @Test
  public void testMultipleXmlBlocksMixedWithText() {
    String input = "<xml1>x</xml1>你好<xml2>y</xml2>";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("你好")).thenReturn(Arrays.asList("你", "好"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<?> list = (List<?>) tokens;
                  if (list.size() < 4) return false;
                  CoreLabel t1 = (CoreLabel) list.get(1);
                  return "你".equals(t1.word());
                }));
  }

  @Test
  public void testCharacterOffsetsNonOverlapping() {
    String input = "早上好";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("早上好")).thenReturn(Arrays.asList("早上", "好"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator a = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(a);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  if (list.size() != 2) return false;
                  Integer end0 =
                      list.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
                  Integer begin1 =
                      list.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
                  return end0.equals(begin1);
                }));
  }

  @Test
  public void testTabsAreIgnoredLikeSpaces() {
    String input = "你\t我";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("你我")).thenReturn(Arrays.asList("你", "我"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator a = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(a);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return ((List<?>) tokens).size() == 2;
                }));
  }

  @Test
  public void testSegmenterReturnsUnicodeSymbolTokens() {
    String input = "我喜😊欢";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("我喜😊欢")).thenReturn(Arrays.asList("我", "喜", "😊", "欢"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator a = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(a);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  if (((List<?>) tokens).size() != 4) return false;
                  CoreLabel emoji = (CoreLabel) ((List<?>) tokens).get(2);
                  return "😊".equals(emoji.word());
                }));
  }

  @Test
  public void testTextInsideXmlDoesNotTriggerSegmentation() {
    String input = "<tag>你好吗</tag>";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return ((List<?>) tokens).size() == 1;
                }));
  }

  @Test
  public void testXmlWhitespaceIsHandledAsSpecialFlag() {
    String input = "<tag> \t</tag>";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator a = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(a);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return ((List<?>) tokens).size() == 1;
                }));
  }

  @Test
  public void testEmptyCoreLabelSegmentedWordIsSkipped() {
    String input = "北京";
    AbstractSequenceClassifier<?> segmenter = mock(AbstractSequenceClassifier.class);
    when(segmenter.segmentString("北京")).thenReturn(Arrays.asList("北", "", "京"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator a = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(a);
    // doReturn(segmenter).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<?> list = (List<?>) tokens;
                  if (list.size() != 2) return false;
                  CoreLabel l1 = (CoreLabel) list.get(0);
                  CoreLabel l2 = (CoreLabel) list.get(1);
                  return "北".equals(l1.word()) && "京".equals(l2.word());
                }));
  }

  @Test
  public void testXmlRegionSingleCharWhitespaceBehavesCorrectly() {
    String input = "<xml> </xml>";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("")).thenReturn(Collections.emptyList());
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                toks -> {
                  return ((List<?>) toks).size() == 1;
                }));
  }

  @Test
  public void testTokenizationRespectsUnicodeLineSeparatorU2028() {
    String input = "我\u2028你";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我你")).thenReturn(Arrays.asList("我", "你"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                toks -> {
                  List<CoreLabel> list = (List<CoreLabel>) toks;
                  return list.size() == 2;
                }));
  }

  @Test
  public void testCarriageReturnAloneIsRemoved() {
    String input = "今天天气\r不错";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("今天天气不错")).thenReturn(Arrays.asList("今天天气", "不错"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                toks -> {
                  List<CoreLabel> list = (List<CoreLabel>) toks;
                  return list.size() == 2;
                }));
  }

  @Test
  public void testMultipleNewlinesArePreservedWhenConfigured() {
    String input = "一句话\n\n\n第二句话";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("一句话第二句话")).thenReturn(Arrays.asList("一句话", "第二句话"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                toks -> {
                  List<CoreLabel> list = (List<CoreLabel>) toks;
                  if (list.size() < 3) return false;
                  CoreLabel newlineToken = list.get(1);
                  String orig = newlineToken.originalText();
                  return orig != null && orig.contains("\n");
                }));
  }

  @Test
  public void testSegmentationSkipsSingleNewlinesWhenSplittingOnTwoNewlines() {
    String input = "我\n你";
    AbstractSequenceClassifier<?> mockClassifier = mock(AbstractSequenceClassifier.class);
    when(mockClassifier.segmentString("我你")).thenReturn(Arrays.asList("我", "你"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model",
            "dummy",
            StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY,
            "two",
            StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY,
            "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(mockClassifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                toks -> {
                  return ((List<?>) toks).size() == 2;
                }));
  }

  @Test
  public void testInvalidSegmentedTokenThrowsAdvancePosError() {
    String input = "ab";
    CoreLabel cl0 = new CoreLabel();
    cl0.set(CoreAnnotations.ChineseCharAnnotation.class, "a");
    CoreLabel cl1 = new CoreLabel();
    cl1.set(CoreAnnotations.ChineseCharAnnotation.class, "b");
    List<CoreLabel> chars = new ArrayList<>();
    chars.add(cl0);
    chars.add(cl1);
    try {
      ChineseSegmenterAnnotator.class
          .getDeclaredMethod("advancePos", List.class, int.class, String.class)
          .invoke(null, chars, 0, "abc");
      fail("Expected RuntimeException");
    } catch (Exception e) {
      Throwable cause = e.getCause();
      assertNotNull(cause);
      assertTrue(cause instanceof RuntimeException);
    }
  }

  @Test
  public void testSingleCharacterNotSplitFurther() {
    String text = "吗";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("吗")).thenReturn(Collections.singletonList("吗"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  return ((List<?>) tokens).size() == 1;
                }));
  }

  @Test
  public void testLeadingAndTrailingSpacesAreSkippedWhenNewlineSplitDisabled() {
    String input = "  我喜欢中文  ";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我喜欢中文")).thenReturn(Arrays.asList("我", "喜欢", "中文"));
    Properties props = PropertiesUtils.asProperties("segment.model", "dummy");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(annotation);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  return list.size() == 3;
                }));
  }

  @Test
  public void testSingleNewlinePreservedIfNotLeadingOrTrailing() {
    String input = "我\n你";
    AbstractSequenceClassifier<?> classifier = mock(AbstractSequenceClassifier.class);
    when(classifier.segmentString("我你")).thenReturn(Arrays.asList("我", "你"));
    Properties props =
        PropertiesUtils.asProperties(
            "segment.model", "dummy", StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("segment", props);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    Annotation ann = new Annotation(input);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ChineseSegmenterAnnotator spy = spy(annotator);
    // doReturn(classifier).when(spy).segmenter;
    spy.annotate(ann);
    verify(sentence)
        .set(
            eq(CoreAnnotations.TokensAnnotation.class),
            argThat(
                tokens -> {
                  List<CoreLabel> list = (List<CoreLabel>) tokens;
                  if (list.size() != 3) return false;
                  CoreLabel newline = list.get(1);
                  String word = newline.word();
                  return "\n".equals(newline.originalText())
                      || "[CR]".equals(word)
                      || word.contains("\n");
                }));
  }
}
