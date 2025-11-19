package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Index;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArabicSegmenterAnnotator_2_GPTLLMTest {

 @Test
  public void testDefaultConstructor() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testVerboseConstructor() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator(true);
    assertNotNull(annotator);
  }
@Test
  public void testStringBooleanConstructor() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("dummy-model-path", false);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testMissingModelThrows() {
    Properties props = new Properties();
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  }
@Test
  public void testModelWithPropertyLoadsCorrectly() {
    Properties props = new Properties();
    props.setProperty("ar.model", "/path/to/model");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateSingleSentenceNoNewlines() {
    ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("هذا");
    token1.setValue("هذا");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("اختبار");
    token2.setValue("اختبار");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    Mockito.when(mockSegmenter.segmentStringToTokenList("هذا اختبار")).thenReturn(tokens);

    Properties props = new Properties();
    props.setProperty("ar.model", "/path/to/fake-model");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
    ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//     Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

    Annotation sentence = new Annotation("هذا اختبار");
    sentence.set(CoreAnnotations.TextAnnotation.class, "هذا اختبار");

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation document = new Annotation("هذا اختبار");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotatorSpy.annotate(document);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(2, result.size());
    assertEquals("هذا", result.get(0).word());
    assertEquals("اختبار", result.get(1).word());
  }
@Test
  public void testAnnotateWithNewlineSplittingEnabled() {
    ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("هذا");
    token1.setValue("هذا");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("اختبار");
    token2.setValue("اختبار");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    Mockito.when(mockSegmenter.segmentStringToTokenList("هذا")).thenReturn(Collections.singletonList(token1));
    Mockito.when(mockSegmenter.segmentStringToTokenList("اختبار")).thenReturn(Collections.singletonList(token2));

    Properties props = new Properties();
    props.setProperty("ar.model", "fake-model");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
    ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//     Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

    Annotation sentence = new Annotation("هذا\nاختبار");
    sentence.set(CoreAnnotations.TextAnnotation.class, "هذا\nاختبار");

    Annotation document = new Annotation("هذا\nاختبار");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotatorSpy.annotate(document);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, result.size());
    assertEquals("هذا", result.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(1).word());
    assertEquals("اختبار", result.get(2).word());
  }
@Test
  public void testAnnotateWithoutSentenceList() {
    ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

    CoreLabel token = new CoreLabel();
    token.setWord("اختبار");
    token.setValue("اختبار");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    Mockito.when(mockSegmenter.segmentStringToTokenList("اختبار")).thenReturn(Collections.singletonList(token));

    Properties props = new Properties();
    props.setProperty("ar.model", "fake-model");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
    ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//     Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

    Annotation annotation = new Annotation("اختبار");
    annotation.set(CoreAnnotations.TextAnnotation.class, "اختبار");

    annotatorSpy.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("اختبار", result.get(0).word());
  }
@Test
  public void testRequirementsSatisfiedContent() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("dummyModel", false);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();

    assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.OriginalTextAnnotation.class));
  }
@Test
  public void testRequiresReturnsEmptySet() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("dummyModelPath", false);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requires();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotateTwoNewlinesBehavior() {
    ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("هذه");
    token1.setValue("هذه");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("جملة");
    token2.setValue("جملة");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    Mockito.when(mockSegmenter.segmentStringToTokenList("هذه")).thenReturn(Collections.singletonList(token1));
    Mockito.when(mockSegmenter.segmentStringToTokenList("جملة")).thenReturn(Collections.singletonList(token2));

    Properties props = new Properties();
    props.setProperty("ar.model", "mock-model");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
    ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//     Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

    Annotation sentence = new Annotation("هذه\n\nجملة");
    sentence.set(CoreAnnotations.TextAnnotation.class, "هذه\n\nجملة");

    Annotation document = new Annotation("هذه\n\nجملة");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotatorSpy.annotate(document);

    List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, result.size());
    assertEquals("هذه", result.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(1).word());
    assertEquals("جملة", result.get(2).word());
  }
@Test
public void testAnnotateWithEmptyText() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "dummy");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation("");
  annotation.set(CoreAnnotations.TextAnnotation.class, "");

  annotatorSpy.annotate(annotation);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertEquals(0, tokens.size());
}
@Test
public void testAnnotateWithNullSentenceText() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList(null)).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "dummy");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  CoreMap sentence = new Annotation("ignored");
  sentence.set(CoreAnnotations.TextAnnotation.class, null);

  Annotation doc = new Annotation("ignored");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotatorSpy.annotate(doc);

  List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(result);
  assertTrue(result.isEmpty());
}
@Test
public void testAnnotateNewlineOnlyTextWithNewlineSplitEnabled() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "dummy");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation("\n");
  annotation.set(CoreAnnotations.TextAnnotation.class, "\n");

  Annotation doc = new Annotation("\n");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertEquals(1, tokens.size());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
}
@Test
public void testAnnotateOnlyNewlinesSentenceSplitOnTwo() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "/mock/path");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  String input = "\n\n\n";
  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  
  assertEquals(2, tokens.size());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
}
@Test
public void testPropertyFilteringOnlyAppliesPrefixedKeys() {
  Properties props = new Properties();
  props.setProperty("somethingIrrelevant", "true");
  props.setProperty("ar.model", "/mock/path");
  props.setProperty("ar.verbose", "true");
  props.setProperty("ar.batchSize", "20");
  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  assertNotNull(annotator); 
}
@Test(expected = RuntimeException.class)
public void testSegmenterThrowsException() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList("crash")).thenThrow(new RuntimeException("crash"));

  Properties props = new Properties();
  props.setProperty("ar.model", "dummy");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation anno = new Annotation("crash");
  anno.set(CoreAnnotations.TextAnnotation.class, "crash");

  annotatorSpy.annotate(anno); 
}
@Test
public void testSegmenterReturnsEmptyTokenList() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList("لا شيء")).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "dummy");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation("لا شيء");
  annotation.set(CoreAnnotations.TextAnnotation.class, "لا شيء");

  Annotation doc = new Annotation("لا شيء");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertTrue(tokens.isEmpty());
}
@Test
public void testTextWithMultipleIndividualNewlinesTokenizeNewlineTrue() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("سطر");
  token1.setValue("سطر");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("جديد");
  token2.setValue("جديد");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

  Mockito.when(mockSegmenter.segmentStringToTokenList("سطر")).thenReturn(Collections.singletonList(token1));
  Mockito.when(mockSegmenter.segmentStringToTokenList("جديد")).thenReturn(Collections.singletonList(token2));

  Properties props = new Properties();
  props.setProperty("ar.model", "mock-model");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  String text = "سطر\n\n\nجديد";
  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TextAnnotation.class, text);

  Annotation document = new Annotation(text);
  document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotatorSpy.annotate(document);

  List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(5, result.size());
  assertEquals("سطر", result.get(0).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(1).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(2).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(3).word());
  assertEquals("جديد", result.get(4).word());
}
@Test
public void testOnlyNewlinesWithSentenceSplitNever() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList("\n\n")).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "dummy");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation("\n\n");
  annotation.set(CoreAnnotations.TextAnnotation.class, "\n\n");

  Annotation document = new Annotation("\n\n");
  document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  annotatorSpy.annotate(document);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertEquals(0, tokens.size());
}
@Test
public void testNewlineFirstTokenIsSentinelThenText() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel tok = new CoreLabel();
  tok.setWord("كلمة");
  tok.setValue("كلمة");
  tok.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
  tok.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

  Mockito.when(mockSegmenter.segmentStringToTokenList("كلمة")).thenReturn(Collections.singletonList(tok));

  Properties props = new Properties();
  props.setProperty("ar.model", "mock");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  String input = "\nكلمة";
  Annotation sentence = new Annotation(input);
  sentence.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation document = new Annotation(input);
  document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotatorSpy.annotate(document);

  List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(2, tokens.size());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
  assertEquals("كلمة", tokens.get(1).word());
}
@Test
public void testSentenceSplitOnTwoSkipsExtraNewlinesAfterTwo() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel label = new CoreLabel();
  label.setWord("نص");
  label.setValue("نص");
  label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
  label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

  Mockito.when(mockSegmenter.segmentStringToTokenList("نص")).thenReturn(Collections.singletonList(label));

  Properties props = new Properties();
  props.setProperty("ar.model", "/mock/path");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  String text = "\n\n\nنص";
  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TextAnnotation.class, text);

  Annotation document = new Annotation(text);
  document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotatorSpy.annotate(document);

  List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertEquals(3, tokens.size());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
  assertEquals("نص", tokens.get(2).word());
}
@Test
public void testTokenOffsetsUpdatedCorrectlyWithPrefix() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel token = new CoreLabel();
  token.setWord("مرحبا");
  token.setValue("مرحبا");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  Mockito.when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(token));

  Properties props = new Properties();
  props.setProperty("ar.model", "model-path-here");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  String prefix = ">>> ";
  String text = prefix + "مرحبا";

  Annotation sentence = new Annotation(text);
  sentence.set(CoreAnnotations.TextAnnotation.class, text);

  Annotation document = new Annotation(text);
  document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Mockito.when(mockSegmenter.segmentStringToTokenList(">>> ")).thenReturn(Collections.emptyList());
  Mockito.when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(token));

  annotatorSpy.annotate(document);

  List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(1, result.size());
  int begin = result.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
  int end = result.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);

  assertTrue(begin >= 0);
  assertTrue(end > begin);
}
@Test
public void testLoadModelPropagatesIOExceptionAsRuntimeException() {
  Properties props = new Properties();
  props.setProperty("ar.model", "fake-model");

  
  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);

  try {
    ArabicSegmenterAnnotator brokenAnnotator = new ArabicSegmenterAnnotator("ar", new Properties() {
      @Override
      public String getProperty(String key) {
        if ("ar.model".equals(key)) {
          return "trigger-exception";
        }
        return super.getProperty(key);
      }
    });

    
    brokenAnnotator.annotate(new Annotation("text"));

    fail("Expected RuntimeException due to model loading failure");
  } catch (RuntimeException expected) {
    assertNotNull(expected.getMessage());
  }
}
@Test
public void testAnnotateWithNoTextAnnotationKeyPresent() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList(null)).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "dummy-path");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation sentence = new Annotation("content");
  

  Annotation doc = new Annotation("content");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotatorSpy.annotate(doc);

  List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(result);
  assertTrue(result.isEmpty());
}
@Test
public void testAnnotateWithNonMatchingNewlineRegex() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  String input = "line💥break";
  CoreLabel token = new CoreLabel();
  token.setWord("line💥break");
  token.setValue("line💥break");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

  Mockito.when(mockSegmenter.segmentStringToTokenList("line💥break")).thenReturn(Collections.singletonList(token));

  Properties props = new Properties();
  props.setProperty("ar.model", "x");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(1, result.size());
  assertEquals("line💥break", result.get(0).word());
}
@Test
public void testAnnotateWithOneNewlineOnlyAddsOneNewlineToken() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel token = new CoreLabel();
  token.setWord("مرحبا");
  token.setValue("مرحبا");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

  Mockito.when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(token));

  Properties props = new Properties();
  props.setProperty("ar.model", "x");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  String input = "\nمرحبا";
  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(2, result.size());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(0).word());
  assertEquals("مرحبا", result.get(1).word());
}
@Test
public void testAnnotatorWithEmptyPropertiesDefaultsToFalseBooleans() {
  Properties props = new Properties();
  props.setProperty("ar.model", "dummy");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  assertNotNull(annotator);

  String booleanStr = props.getProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY);
  assertTrue(booleanStr == null || booleanStr.equals("false"));
}
@Test
public void testMultiplePropertiesAreFilteredCorrectly() {
  Properties props = new Properties();
  props.setProperty("ar.model", "some/path");
  props.setProperty("ar.verbose", "true");
  props.setProperty("ar.batchSize", "128");
  props.setProperty("foo.debug", "true"); 

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  assertNotNull(annotator);
}
@Test
public void testAnnotateWithCarriageReturnNewlines() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("السلام");
  token1.setValue("السلام");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("عليكم");
  token2.setValue("عليكم");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

  Mockito.when(mockSegmenter.segmentStringToTokenList("السلام")).thenReturn(Collections.singletonList(token1));
  Mockito.when(mockSegmenter.segmentStringToTokenList("عليكم")).thenReturn(Collections.singletonList(token2));

  String input = "السلام\r\nعليكم";

  Properties props = new Properties();
  props.setProperty("ar.model", "path");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, result.size());
  assertEquals("السلام", result.get(0).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(1).word());
  assertEquals("عليكم", result.get(2).word());
}
@Test
public void testConstructorWithNonPrefixedIrrelevantPropertiesOnly() {
  Properties props = new Properties();
  props.setProperty("other.module", "true");
  props.setProperty("random.value", "123");

  try {
    new ArabicSegmenterAnnotator("ar", props);
    fail("Expected RuntimeException due to missing ar.model key");
  } catch (RuntimeException expected) {
    assertTrue(expected.getMessage().contains("Expected a property"));
  }
}
@Test
public void testTwoNewlinesFollowedByMoreNewlines() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel token = new CoreLabel();
  token.setWord("مرحبا");
  token.setValue("مرحبا");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

  Mockito.when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(token));
  Mockito.when(mockSegmenter.segmentStringToTokenList("\n")).thenReturn(Collections.emptyList());

  String input = "\n\n\n\nمرحبا";

  Properties props = new Properties();
  props.setProperty("ar.model", "model-path");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, tokens.size());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
  assertEquals("مرحبا", tokens.get(2).word());
}
@Test
public void testDoOneSentenceHandlesMultipleLinesWithOffsetAdjustment() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel label1 = new CoreLabel();
  label1.setWord("أهلا");
  label1.setValue("أهلا");
  label1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  label1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreLabel label2 = new CoreLabel();
  label2.setWord("عالم");
  label2.setValue("عالم");
  label2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  label2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  Mockito.when(mockSegmenter.segmentStringToTokenList("أهلا")).thenReturn(Collections.singletonList(label1));
  Mockito.when(mockSegmenter.segmentStringToTokenList("عالم")).thenReturn(Collections.singletonList(label2));

  String input = "أهلا\nعالم";

  Properties props = new Properties();
  props.setProperty("ar.model", "x");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  annotatorSpy.annotate(doc);

  List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, result.size());

  int adjustedBegin = result.get(2).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
  int adjustedEnd = result.get(2).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);

  assertEquals("عالم", result.get(2).word());
  assertTrue(adjustedBegin > 0);
  assertTrue(adjustedEnd > adjustedBegin);
}
@Test
public void testAnnotateHandlesNullSentencesAnnotationGracefully() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel label = new CoreLabel();
  label.setWord("كلمة");
  label.setValue("كلمة");
  label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  Mockito.when(mockSegmenter.segmentStringToTokenList("كلمة")).thenReturn(Collections.singletonList(label));

  Properties props = new Properties();
  props.setProperty("ar.model", "ar-model");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator annotatorSpy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(annotatorSpy).segmenter;

  Annotation annotation = new Annotation("كلمة");
  annotation.set(CoreAnnotations.TextAnnotation.class, "كلمة");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotatorSpy.annotate(annotation);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(1, tokens.size());
  assertEquals("كلمة", tokens.get(0).word());
}
@Test
public void testAnnotateWithTokenizeNewlineFalseSingleSegment() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel label = new CoreLabel();
  label.setWord("البيت");
  label.setValue("البيت");
  label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

  Mockito.when(mockSegmenter.segmentStringToTokenList("البيت")).thenReturn(Collections.singletonList(label));

  Properties props = new Properties();
  props.setProperty("ar.model", "model-path");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spyAnnotator = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spyAnnotator).segmenter;

  Annotation annotation = new Annotation("البيت");
  annotation.set(CoreAnnotations.TextAnnotation.class, "البيت");

  Annotation doc = new Annotation("البيت");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  spyAnnotator.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertEquals(1, tokens.size());
  assertEquals("البيت", tokens.get(0).word());
}
@Test
public void testAnnotateNewlineTokenOffsetPositionsAreCorrect() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel label1 = new CoreLabel();
  label1.setWord("كلمة");
  label1.setValue("كلمة");
  label1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  label1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreLabel label2 = new CoreLabel();
  label2.setWord("أخرى");
  label2.setValue("أخرى");
  label2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  label2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  Mockito.when(mockSegmenter.segmentStringToTokenList("كلمة")).thenReturn(Collections.singletonList(label1));
  Mockito.when(mockSegmenter.segmentStringToTokenList("أخرى")).thenReturn(Collections.singletonList(label2));

  String input = "كلمة\nأخرى";

  Properties props = new Properties();
  props.setProperty("ar.model", "x");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spyAnnotator = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spyAnnotator).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  spyAnnotator.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, tokens.size());

  CoreLabel newline = tokens.get(1);
  int begin = newline.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
  int end = newline.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
  assertEquals(5, begin);
  assertEquals(6, end);

  CoreLabel secondToken = tokens.get(2);
  int adjustedBegin = secondToken.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
  int adjustedEnd = secondToken.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
  assertTrue(adjustedBegin > 5);
  assertTrue(adjustedEnd > adjustedBegin);
}
@Test
public void testAnnotateNewlineOnlyStringWithNoSegmentTokens() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  Mockito.when(mockSegmenter.segmentStringToTokenList(Mockito.anyString())).thenReturn(Collections.emptyList());

  String input = "\n";

  Properties props = new Properties();
  props.setProperty("ar.model", "mock");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spyAnnotator = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spyAnnotator).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  spyAnnotator.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(1, tokens.size()); 
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
}
@Test
public void testSentenceSplitOnTwoSkipsSingleNewlines() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel label = new CoreLabel();
  label.setWord("مرحبا");
  label.setValue("مرحبا");
  label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  Mockito.when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(label));

  String input = "مرحبا\nمرحبا\nمرحبا\n\nمرحبا";

  Properties props = new Properties();
  props.setProperty("ar.model", "mock");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spyAnnotator = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spyAnnotator).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  spyAnnotator.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertTrue(tokens.stream().anyMatch(t -> AbstractTokenizer.NEWLINE_TOKEN.equals(t.word())));
  assertTrue(tokens.stream().anyMatch(t -> "مرحبا".equals(t.word())));
}
@Test
public void testEmptyInputReturnsEmptyTokens() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  Mockito.when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

  String input = "";

  Properties props = new Properties();
  props.setProperty("ar.model", "mock-path");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spyAnnotator = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spyAnnotator).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  spyAnnotator.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertTrue(tokens.isEmpty());
}
@Test
public void testAnnotateWithTabsInsteadOfNewlines() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("اختبار1");
  token1.setValue("اختبار1");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("اختبار2");
  token2.setValue("اختبار2");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

  Mockito.when(mockSegmenter.segmentStringToTokenList("اختبار1")).thenReturn(Collections.singletonList(token1));
  Mockito.when(mockSegmenter.segmentStringToTokenList("اختبار2")).thenReturn(Collections.singletonList(token2));

  String input = "اختبار1\tاختبار2";

  Properties props = new Properties();
  props.setProperty("ar.model", "mock");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spyAnnotator = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spyAnnotator).segmenter;

  Annotation annotation = new Annotation(input);
  annotation.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(annotation));

  spyAnnotator.annotate(doc);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(2, tokens.size());
  assertEquals("اختبار1", tokens.get(0).word());
  assertEquals("اختبار2", tokens.get(1).word());
}
@Test
public void testAnnotateWithDuplicateNewlinesNotCaptured() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);

  CoreLabel label = new CoreLabel();
  label.setWord("مرحبا");
  label.setValue("مرحبا");
  label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

  Mockito.when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(label));

  String input = "\n\n\n\nمرحبا";

  Properties props = new Properties();
  props.setProperty("ar.model", "mock-path");
  props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spy).segmenter;

  Annotation sentence = new Annotation(input);
  sentence.set(CoreAnnotations.TextAnnotation.class, input);

  Annotation doc = new Annotation(input);
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  spy.annotate(doc);

  List<CoreLabel> result = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, result.size());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(0).word());
  assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(1).word());
  assertEquals("مرحبا", result.get(2).word());
}
@Test
public void testAnnotateWithSegmenterReturningNullList() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList("كلمة")).thenReturn(null);

  Properties props = new Properties();
  props.setProperty("ar.model", "mock-model");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spy).segmenter;

  Annotation sentence = new Annotation("كلمة");
  sentence.set(CoreAnnotations.TextAnnotation.class, "كلمة");

  Annotation doc = new Annotation("كلمة");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  spy.annotate(doc);

  List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
  assertTrue(tokens == null || tokens.isEmpty());
}
@Test
public void testConstructorHandlesMixedCaseBooleanPropertyValues() {
  Properties props = new Properties();
  props.setProperty("ar.model", "x");
  props.setProperty("ar.verbose", "TrUe");
  props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "FaLsE");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  assertNotNull(annotator);
}
@Test
public void testConstructorDropSuffixKeyParsing() {
  Properties props = new Properties();
  props.setProperty("ar.model", "/mock/path");
  props.setProperty("ar.tokenizeNewline", "true");
  props.setProperty("ar.anotherProperty", "irrelevant");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  assertNotNull(annotator);
}
@Test
public void testAnnotationWithoutTokensOrText() {
  ArabicSegmenter mockSegmenter = Mockito.mock(ArabicSegmenter.class);
  Mockito.when(mockSegmenter.segmentStringToTokenList(null)).thenReturn(Collections.emptyList());

  Properties props = new Properties();
  props.setProperty("ar.model", "x");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
  ArabicSegmenterAnnotator spy = Mockito.spy(annotator);
//   Mockito.doReturn(mockSegmenter).when(spy).segmenter;

  Annotation annotation = new Annotation("");
  

  spy.annotate(annotation);

  List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertTrue(tokens == null || tokens.isEmpty());
} 
}
