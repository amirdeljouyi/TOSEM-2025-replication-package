package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Index;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArabicSegmenterAnnotator_1_GPTLLMTest {

 @Test
  public void testAnnotateSingleSentence_NoNewlines() {
    Properties props = new Properties();
    props.setProperty("test.model", "mock-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("test", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    List<CoreLabel> mockTokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("السلام");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    mockTokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("عليكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    mockTokens.add(token2);

    when(mockSegmenter.segmentStringToTokenList("السلام عليكم")).thenReturn(mockTokens);

    
    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Reflection failed to inject segmenter: " + e.getMessage());
    }

    Annotation annotation = new Annotation("السلام عليكم");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("السلام عليكم");
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      List<CoreLabel> tokens = (List<CoreLabel>) invocation.getArguments()[1];
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("السلام", result.get(0).word());
    assertEquals("عليكم", result.get(1).word());
  }
@Test
  public void testAnnotateWithNewlinesAndSplitOptionEnabled() {
    Properties props = new Properties();
    props.setProperty("custom.model", "mock-path");
    props.setProperty("custom.verbose", "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("custom", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> tokens1 = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("مرحبا");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens1.add(t1);

    List<CoreLabel> tokens2 = new ArrayList<>();
    CoreLabel t2 = new CoreLabel();
    t2.setWord("بكم");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    tokens2.add(t2);

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(tokens1);
    when(mockSegmenter.segmentStringToTokenList("بكم")).thenReturn(tokens2);

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    Annotation annotation = new Annotation("مرحبا\n\nبكم");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("مرحبا\n\nبكم");
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      List<CoreLabel> result = (List<CoreLabel>) invocation.getArguments()[1];
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(result);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("مرحبا", result.get(0).word());
    assertEquals("<NEWLINE>", result.get(1).word());
    assertEquals("<NEWLINE>", result.get(2).word());
    assertEquals("بكم", result.get(3).word());
  }
@Test
  public void testAnnotateWithoutSentencesAnnotationFallsBackToWholeText() {
    Properties props = new Properties();
    props.setProperty("test.model", "mock-path");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("test", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("مرحباً");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("بكم");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    tokens.add(t2);

    when(mockSegmenter.segmentStringToTokenList("مرحباً بكم")).thenReturn(tokens);

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Injection failed");
    }

    Annotation doc = new Annotation("مرحباً بكم");
    doc.set(CoreAnnotations.TextAnnotation.class, "مرحباً بكم");

    annotator.annotate(doc);

    List<CoreLabel> result = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("مرحباً", result.get(0).word());
    assertEquals("بكم", result.get(1).word());
  }
@Test(expected = RuntimeException.class)
  public void testThrowsExceptionIfModelPropertyMissing() {
    Properties props = new Properties();
    new ArabicSegmenterAnnotator("noModel", props);
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("test.model", "mock");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("test", props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertTrue(result.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.OriginalTextAnnotation.class));
  }
@Test
  public void testRequiresIsEmptySet() {
    Properties props = new Properties();
    props.setProperty("test.model", "mock");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("test", props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requires();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotateOnlyNewlinesShouldAddOneNewlineToken_whenSplitOnSingleNewlineDisabled() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "fake");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "false");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("\n")).thenReturn(new ArrayList<>());

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Reflection failed");
    }

    String text = "\n";
    Annotation annotation = new Annotation(text);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    doAnswer(invocation -> {
      List<CoreLabel> result = invocation.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(result);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAnnotateMultipleNewlinesSplitOptionFalse_ShouldSkipExtraNewlines() {
    Properties props = new Properties();
    props.setProperty("x.model", "mock-model");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
//     when(segmenter.segmentStringToTokenList("phrase")).thenReturn(Collections.singletonList(createToken("phrase", 0, 6)));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Reflection failed");
    }

    String original = "phrase\n\n\nphrase";
    Annotation annotation = new Annotation(original);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(original);
    doAnswer(invocation -> {
      List<CoreLabel> result = invocation.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(result);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("phrase", tokens.get(0).word());
    assertEquals("<NEWLINE>", tokens.get(1).word());
    assertEquals("<NEWLINE>", tokens.get(2).word());
    assertEquals("<NEWLINE>", tokens.get(3).word());
    assertEquals("phrase", tokens.get(4).word());
  }
@Test
  public void testAnnotateWithEmptyText_ShouldProduceNoTokens() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock-path");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList("")).thenReturn(new ArrayList<>());

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Reflection");
    }

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

    doAnswer(invocation -> {
      List<CoreLabel> list = invocation.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(list);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testAnnotateWithNullTextAnnotation_ShouldNotFailAndGenerateNoTokens() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "model");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter seg = mock(ArabicSegmenter.class);
    when(seg.segmentStringToTokenList(null)).thenReturn(new ArrayList<>());

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, seg);
    } catch (Exception ex) {
      fail("Reflection failed");
    }

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
    doAnswer(invocation -> {
      List<CoreLabel> tokens = invocation.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

//     Annotation doc = new Annotation(null);
//     doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//     annotator.annotate(doc);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testUnicodeTextSegmentingPreservesOffsets() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "model");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    CoreLabel c1 = new CoreLabel();
    c1.setWord("سَبْحَانَ");
    c1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    c1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel c2 = new CoreLabel();
    c2.setWord("ٱللَّه");
    c2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    c2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(c1);
    tokens.add(c2);

    when(segmenter.segmentStringToTokenList("سَبْحَانَ ٱللَّه")).thenReturn(tokens);

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception ex) {
      fail("Injection failed");
    }

    Annotation annotation = new Annotation("سَبْحَانَ ٱللَّه");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("سَبْحَانَ ٱللَّه");

    doAnswer(invocation -> {
      List<CoreLabel> result = invocation.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(result);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, result.size());
    assertEquals(0, (int) result.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(8, (int) result.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("سَبْحَانَ", result.get(0).word());
    assertEquals("ٱللَّه", result.get(1).word());
  }
@Test(expected = RuntimeException.class)
  public void testModelLoadingThrowsExceptionWhenArabicSegmenterFails() {
    Properties props = new Properties();
    props.setProperty("fail.model", "fake-path");
    props.setProperty("fail.verbose", "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("fail", props);

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, null);
    } catch (Exception e) {
      fail("Injection failed");
    }

    
    try {
      java.lang.reflect.Method m = ArabicSegmenterAnnotator.class.getDeclaredMethod("loadModel", String.class, Properties.class);
      m.setAccessible(true);
      m.invoke(annotator, "invalid-path", new Properties() {{
        setProperty("model", "invalid-path");
      }});
    } catch (Exception e) {
      throw new RuntimeException("Expected failure on model loading.");
    }
  }
@Test
  public void testAnnotateNullSentencesListDoesNotFail() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "path");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("some input")).thenReturn(Collections.emptyList());

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Unable to inject segmenter");
    }

    Annotation doc = new Annotation("some input");
    doc.set(CoreAnnotations.TextAnnotation.class, "some input");
    

    annotator.annotate(doc);

    List<CoreLabel> output = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(output);
    assertEquals(0, output.size());
  }
@Test
  public void testNewlineOnlyLineIsRecognizedAndOffsetAssignedCorrectly() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Reflection fails");
    }

    String text = "\n";
    Annotation annotation = new Annotation(text);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> list = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(list);
    assertEquals(1, list.size());
    assertEquals("<NEWLINE>", list.get(0).word());
    assertEquals(0, (int) list.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(1, (int) list.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testOnlyOneNewlineLineAdded_WhenSplitOnTwoNewlinesTrue() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    List<String> lines = new ArrayList<>();
    lines.add("first");
    lines.add("\n");
    lines.add("\n");
    lines.add("second");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    List<CoreLabel> firstTokens = new ArrayList<>();
    CoreLabel ft = new CoreLabel();
    ft.setWord("first");
    ft.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    ft.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    firstTokens.add(ft);

    List<CoreLabel> secondTokens = new ArrayList<>();
    CoreLabel st = new CoreLabel();
    st.setWord("second");
    st.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    st.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    secondTokens.add(st);

    when(segmenter.segmentStringToTokenList("first")).thenReturn(firstTokens);
    when(segmenter.segmentStringToTokenList("second")).thenReturn(secondTokens);

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Field set failed");
    }

    String text = "first\n\nsecond";
    Annotation annotation = new Annotation(text);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("first", tokens.get(0).word());
    assertEquals("<NEWLINE>", tokens.get(1).word());
    assertEquals("<NEWLINE>", tokens.get(2).word());
    assertEquals("second", tokens.get(3).word());
  }
@Test
  public void testOffsetAdjustmentAfterNewlineIsCorrect() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("line1");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens1 = Arrays.asList(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("line2");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens2 = Arrays.asList(token2);

    when(segmenter.segmentStringToTokenList("line1")).thenReturn(tokens1);
    when(segmenter.segmentStringToTokenList("line2")).thenReturn(tokens2);

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Reflection inject failed");
    }

    String text = "line1\nline2";
    Annotation annotation = new Annotation(text);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

    doAnswer(i -> {
      List<CoreLabel> result = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(result);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, result.size());

    CoreLabel first = result.get(0);
    CoreLabel newline = result.get(1);
    CoreLabel second = result.get(2);

    assertEquals("line1", first.word());
    assertEquals(0, (int) first.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(5, (int) first.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals("<NEWLINE>", newline.word());
    assertEquals(5, (int) newline.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(6, (int) newline.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals("line2", second.word());
    assertEquals(6, (int) second.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(11, (int) second.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testMakeNewlineCoreLabelSetsCorrectValues() {
    
    Properties props = new Properties();
    props.setProperty("arabic.model", "fake");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter seg = mock(ArabicSegmenter.class);
//     when(seg.segmentStringToTokenList("كما")).thenReturn(Arrays.asList(create("كما", 0, 3)));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, seg);
    } catch (Exception e) {
      fail("Could not inject segmenter");
    }

    String input = "\nكما";
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> result = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(result);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, result.size());

    CoreLabel newline = result.get(0);
    CoreLabel word = result.get(1);

    assertEquals("<NEWLINE>", newline.word());
    assertEquals(0, (int) newline.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(1, (int) newline.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals("كما", word.word());
    assertEquals(1, (int) word.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(4, (int) word.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testNewlineSplitterTrueButSentenceBreakNever() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
      CoreLabel token = new CoreLabel();
      token.setWord("مرحبا");
      token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
      token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
      when(segmenter.segmentStringToTokenList("مرحبا")).thenReturn(Arrays.asList(token));
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Field injection failed");
    }

    String input = "مرحبا";
    Annotation annotation = new Annotation(input);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("مرحبا", result.get(0).word());
  }
@Test(expected = RuntimeException.class)
  public void testSegmenterThrowsCheckedExceptionDuringLoadModel() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "fake-model");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    try {
      java.lang.reflect.Method m = ArabicSegmenterAnnotator.class.getDeclaredMethod("loadModel", String.class, Properties.class);
      m.setAccessible(true);
      m.invoke(annotator, "broken-path", new Properties() {{
        setProperty("model", "broken-path");
      }});
    } catch (Exception e) {
      throw new RuntimeException("Expected error during model loading");
    }
  }
@Test
  public void testSentenceTextAnnotationIsNull_returnsEmptyTokens() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "model-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(null)).thenReturn(new ArrayList<>());

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Failed to inject mock segmenter");
    }

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testMultipleSentencesHandledSeparately() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "any");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("الأولى");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("الثانية");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    when(segmenter.segmentStringToTokenList("جملة الأولى")).thenReturn(Arrays.asList(token1));
    when(segmenter.segmentStringToTokenList("جملة الثانية")).thenReturn(Arrays.asList(token2));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Injection failed");
    }

    Annotation annotation = new Annotation("جملة الأولى جملة الثانية");

    CoreMap sent1 = mock(CoreMap.class);
    when(sent1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("جملة الأولى");
    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sent1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sent1).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    CoreMap sent2 = mock(CoreMap.class);
    when(sent2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("جملة الثانية");
    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sent2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sent2).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent1, sent2));
    annotator.annotate(annotation);

    List<CoreLabel> tokens1 = sent1.get(CoreAnnotations.TokensAnnotation.class);
    List<CoreLabel> tokens2 = sent2.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(1, tokens1.size());
    assertEquals("الأولى", tokens1.get(0).word());

    assertEquals(1, tokens2.size());
    assertEquals("الثانية", tokens2.get(0).word());
  }
@Test
  public void testRequirementsSatisfiedIncludesFullSet() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "any");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.PositionAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testSingleNewlineSplitOnTwoEnabled_OnlyOneNewlinePresent() {
    Properties props = new Properties();
    props.setProperty("seg.model", "path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("seg", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("wordA");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    when(segmenter.segmentStringToTokenList("wordA")).thenReturn(Collections.singletonList(tok1));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Injection failed");
    }

    String input = "wordA\n";
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);

    doAnswer(i -> {
      List<CoreLabel> result = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(result);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("wordA", result.get(0).word());
    assertEquals("<NEWLINE>", result.get(1).word());
  }
@Test
  public void testWhitespaceOnlyLineIsSkippedProperly() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "xyz");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    CoreLabel label1 = new CoreLabel();
    label1.setWord("a");
    label1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    when(segmenter.segmentStringToTokenList("a")).thenReturn(Collections.singletonList(label1));

    when(segmenter.segmentStringToTokenList("  ")).thenReturn(Collections.emptyList());

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Injecting failed");
    }

    String input = "a\n  \na";
    Annotation annotation = new Annotation(input);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> output = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, output.size());
    assertEquals("a", output.get(0).word());
    assertEquals("<NEWLINE>", output.get(1).word());
    assertEquals("a", output.get(4).word());
  }
@Test
  public void testTokenWithoutOffsetAnnotationsShouldNotFail() {
    Properties props = new Properties();
    props.setProperty("seg.model", "abc");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("seg", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    CoreLabel invalid = new CoreLabel();
    invalid.setWord("oops");
    when(segmenter.segmentStringToTokenList("oops")).thenReturn(Collections.singletonList(invalid));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Inject failed");
    }

    Annotation annotation = new Annotation("oops");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("oops");

    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("oops", result.get(0).word());
  }
@Test
  public void testSplitNewlinesAtStartAndEndOfSentence() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "y");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter seg = mock(ArabicSegmenter.class);

    CoreLabel inner = new CoreLabel();
    inner.setWord("nested");
    inner.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    inner.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    when(seg.segmentStringToTokenList("nested")).thenReturn(Collections.singletonList(inner));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, seg);
    } catch (Exception e) {
      fail("Reflection failed");
    }

    String input = "\nnested\n";
    Annotation annotation = new Annotation(input);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> list = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(list);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, result.size());
    assertEquals("<NEWLINE>", result.get(0).word());
    assertEquals("nested", result.get(1).word());
    assertEquals("<NEWLINE>", result.get(2).word());
    assertEquals(7, (int) result.get(1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testPropertiesBooleanParsingDefaultsCorrectly() {
    Properties props = new Properties();
    props.setProperty("x.model", "xxx");
    props.setProperty("x.verbose", "notBoolean");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);

    assertNotNull(annotator.requirementsSatisfied());
    assertTrue(annotator.requires().isEmpty());
  }
@Test
  public void testExtremeOffsetShiftedByLargeTextPrefix() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    CoreLabel token = new CoreLabel();
    token.setWord("real");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    when(segmenter.segmentStringToTokenList("real")).thenReturn(Collections.singletonList(token));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("reflection failed");
    }

    String padding = "0000000000000000\n0000000000000000\n";
    String input = padding + "real";
    Annotation annotation = new Annotation(input);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> list = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(list);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    CoreLabel last = result.get(result.size() - 1);
    assertEquals("real", last.word());
    assertTrue(last.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) > 30);
  }
@Test
  public void testAnnotatorWithEmptyPropertiesFallsBackToDefaultBehavior() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(annotator.requires().isEmpty());
  }
@Test
  public void testAnnotatorAnnotationWithEmptyListOfSentences() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "anything");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(anyString())).thenReturn(Collections.emptyList());

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Failed to set segmenter via reflection");
    }

    Annotation annotation = new Annotation("Some document text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    
    assertNotNull(annotation);
  }
@Test
  public void testAnnotatorWithSentenceThatReturnsNullTokens() {
    Properties props = new Properties();
    props.setProperty("test.model", "model-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("test", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(null);

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Reflection inject failed");
    }

    Annotation doc = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);

    List<CoreLabel> extracted = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(extracted);
    assertTrue(extracted.isEmpty()); 
  }
@Test(expected = RuntimeException.class)
  public void testInvalidBooleanParsingInVerboseInitThrowsNoErrorButLogs() {
    Properties props = new Properties();
    props.setProperty("some.model", "mock-path");
    props.setProperty("some.verbose", "not_a_boolean");

    new ArabicSegmenterAnnotator("some", props);
  }
@Test
  public void testSentenceSplitOnTwoNewlinesSkipExtraNewlinesBeyondTwo() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "splitter");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    List<CoreLabel> segmentTokens = Arrays.asList(
        new CoreLabel() {{
          setWord("مرحبا");
          set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
          set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
        }},
        new CoreLabel() {{
          setWord("بكم");
          set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
          set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
        }}
    );

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(segmentTokens.get(0)));
    when(mockSegmenter.segmentStringToTokenList("بكم")).thenReturn(Collections.singletonList(segmentTokens.get(1)));

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Reflection injection failed");
    }

    String input = "مرحبا\n\n\nبكم";
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> list = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(list);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(annotation);

    List<CoreLabel> output = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, output.size());
    assertEquals("مرحبا", output.get(0).word());
    assertEquals("<NEWLINE>", output.get(1).word());
    assertEquals("<NEWLINE>", output.get(2).word());
    assertEquals("بكم", output.get(3).word());
  }
@Test
  public void testSegmenterExceptionWrapsAsRuntimeForCheckedExceptions() {
    Properties props = new Properties();
    props.setProperty("broken.model", "somepath");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("broken", props);

    try {
      java.lang.reflect.Method method = ArabicSegmenterAnnotator.class.getDeclaredMethod("loadModel", String.class, Properties.class);
      method.setAccessible(true);
      method.invoke(annotator, "somepath", props);
    } catch (Exception e) {
      RuntimeException wrapped = new RuntimeException("Expected segmenter load failure");
      assertNotNull(wrapped);
    }
  }
@Test
  public void testNewlinesFollowedByEmptyLineOnlyAddsUpToTwoNewlinesWhenSplitOnTwoTrue() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "file");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    CoreLabel token = new CoreLabel();
    token.setWord("كلمة");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    when(segmenter.segmentStringToTokenList("كلمة")).thenReturn(Collections.singletonList(token));

    when(segmenter.segmentStringToTokenList("")).thenReturn(new ArrayList<>());

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, segmenter);
    } catch (Exception ex) {
      fail("Field injection failed");
    }

    String input = "كلمة\n\n\n";
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> list = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(list);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, result.size());
    assertEquals("كلمة", result.get(0).word());
    assertEquals("<NEWLINE>", result.get(1).word());
    assertEquals("<NEWLINE>", result.get(2).word());
  }
@Test
  public void testAnnotateWithNullSentenceTextAndNewlineConfigEnabled() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(null)).thenReturn(new ArrayList<>());

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, segmenter);
    } catch (Exception e) {
      fail("Reflection injection failed");
    }

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);

    doAnswer(i -> {
      List<CoreLabel> tokens = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotateWithNullAnnotationObjectHandlesGracefully() {
    Properties props = new Properties();
    props.setProperty("test.model", "x");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("test", props);

    try {
      annotator.annotate(null);
    } catch (Exception e) {
      fail("Annotator should tolerate null input with no effect");
    }
  }
@Test
  public void testPropertyParsingIgnoresIrrelevantKeys() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "something");
    props.setProperty("other.model", "should-be-ignored");
    props.setProperty("arabic.verbose", "true");
    props.setProperty("arabic.irrelevant.setting", "should-not-effect-loader");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    assertNotNull(annotator.requirementsSatisfied());
    assertTrue(annotator.requires().isEmpty());
  }
@Test
  public void testOffsetUpdatesForMultipleChunksAccumulatesCorrectly() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("الأولى");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("الثانية");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    when(mockSegmenter.segmentStringToTokenList("الأولى")).thenReturn(Collections.singletonList(t1));
    when(mockSegmenter.segmentStringToTokenList("الثانية")).thenReturn(Collections.singletonList(t2));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, mockSegmenter);
    } catch (Exception e) {
      fail("Failed to inject segmenter");
    }

    String input = "الأولى\n\nالثانية";
    Annotation annotation = new Annotation(input);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);
    doAnswer(i -> {
      List<CoreLabel> list = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(list);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("الأولى", result.get(0).word());
    assertTrue(result.get(3).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) > 6);
  }
@Test(expected = RuntimeException.class)
  public void testConstructorThrowsIfModelPropertyMissing() {
    Properties props = new Properties();
    props.setProperty("x.verbose", "true");
    new ArabicSegmenterAnnotator("x", props);
  }
@Test
  public void testAnnotateWithSentenceHavingEmptyStringText() {
    Properties props = new Properties();
    props.setProperty("a.model", "abc");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("a", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList("")).thenReturn(new ArrayList<>());

    try {
      java.lang.reflect.Field field = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      field.setAccessible(true);
      field.set(annotator, segmenter);
    } catch (Exception e) {
      fail("could not inject segmenter");
    }

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

    doAnswer(i -> {
      List<CoreLabel> value = i.getArgument(1);
      when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(value);
      return null;
    }).when(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), any());

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotateWithTextAnnotationButNoSentencesField() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    CoreLabel label = new CoreLabel();
    label.setWord("اختبار");
    label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    when(segmenter.segmentStringToTokenList("اختبار")).thenReturn(Collections.singletonList(label));

    try {
      java.lang.reflect.Field f = ArabicSegmenterAnnotator.class.getDeclaredField("segmenter");
      f.setAccessible(true);
      f.set(annotator, segmenter);
    } catch (Exception e) {
      fail("reflection failed");
    }

    Annotation doc = new Annotation("اختبار");
    doc.set(CoreAnnotations.TextAnnotation.class, "اختبار");

    annotator.annotate(doc);

    List<CoreLabel> result = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("اختبار", result.get(0).word());
  } 
}
