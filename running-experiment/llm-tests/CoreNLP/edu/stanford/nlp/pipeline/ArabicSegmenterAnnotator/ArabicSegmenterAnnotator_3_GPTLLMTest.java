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

public class ArabicSegmenterAnnotator_3_GPTLLMTest {

 @Test
  public void testConstructorWithDefault() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithVerboseFlag() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator(true);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testMissingModelThrowsException() {
    Properties props = new Properties();
    new ArabicSegmenterAnnotator("arabic", props);
  }
@Test
  public void testRequirementsSatisfied() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator();
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requirementsSatisfied();

    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testRequiresIsEmpty() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator();
    assertTrue(annotator.requires().isEmpty());
  }
@Test
  public void testAnnotateSingleSentenceWithoutNewlines() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
    
    CoreLabel token = new CoreLabel();
    token.setWord("مرحبا");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    when(segmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(token));

    
//     annotator.segmenter = segmenter;

    Annotation annotation = new Annotation("مرحبا");
    annotation.set(CoreAnnotations.TextAnnotation.class, "مرحبا");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("مرحبا", result.get(0).word());
  }
@Test
  public void testAnnotateMultipleSentences() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("أهلا");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("بكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(segmenter.segmentStringToTokenList("أهلا")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("بكم")).thenReturn(Collections.singletonList(token2));

//     annotator.segmenter = segmenter;

    CoreMap sentence1 = new Annotation("أهلا");
    sentence1.set(CoreAnnotations.TextAnnotation.class, "أهلا");

    CoreMap sentence2 = new Annotation("بكم");
    sentence2.set(CoreAnnotations.TextAnnotation.class, "بكم");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(doc);

    List<CoreLabel> tokens1 = sentence1.get(CoreAnnotations.TokensAnnotation.class);
    List<CoreLabel> tokens2 = sentence2.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(1, tokens1.size());
    assertEquals("أهلا", tokens1.get(0).word());

    assertEquals(1, tokens2.size());
    assertEquals("بكم", tokens2.get(0).word());
  }
@Test
  public void testAnnotateWithNewlineTokenizationEnabled() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = segmenter;

    String text = "سلام\nعليكم";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("سلام");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("عليكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    when(segmenter.segmentStringToTokenList("سلام")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("عليكم")).thenReturn(Collections.singletonList(token2));

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    
    
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("custom.model", "/dummy/path");
    props.setProperty("custom.verbose", "false");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ArabicSegmenterAnnotator newlineAnnotator = new ArabicSegmenterAnnotator("custom", props);
//     newlineAnnotator.segmenter = segmenter;

    newlineAnnotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, result.size()); 

    assertEquals("سلام", result.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.get(1).word());
    assertEquals("عليكم", result.get(2).word());
  }
@Test
  public void testAnnotateWithDoubleNewlineHandling() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("x.model", "/dummy/path");
    props.setProperty("x.verbose", "false");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = segmenter;

    String text = "مرحباً\n\nبكم";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("مرحباً");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("بكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    when(segmenter.segmentStringToTokenList("مرحباً")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("بكم")).thenReturn(Collections.singletonList(token2));

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> output = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(4, output.size());
    assertEquals("مرحباً", output.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, output.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, output.get(2).word());
    assertEquals("بكم", output.get(3).word());
  }
@Test
  public void testAnnotateWithNullSentencesEntry() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = segmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("حسنا");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    when(segmenter.segmentStringToTokenList("حسنا")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("حسنا");
    annotation.set(CoreAnnotations.TextAnnotation.class, "حسنا");  

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("حسنا", result.get(0).word());
  }
@Test
  public void testAnnotateSingleNewlineRemovedWhenSplitOnTwoNewlines() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = segmenter;

    CoreLabel token1 = new CoreLabel();
    token1.setWord("مرحباً");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("بكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    when(segmenter.segmentStringToTokenList("مرحباً")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("بكم")).thenReturn(Collections.singletonList(token2));

    String text = "مرحباً\nبكم";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());

    assertEquals("مرحباً", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word()); 
    assertEquals("بكم", tokens.get(2).word());
  }
@Test
  public void testAnnotateSkipsExtraNewlinesBeyondTwo() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = segmenter;

    CoreLabel token1 = new CoreLabel();
    token1.setWord("مرحبا");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("بكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    when(segmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("بكم")).thenReturn(Collections.singletonList(token2));

    String text = "مرحبا\n\n\nبكم";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());

    assertEquals("مرحبا", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("بكم", tokens.get(3).word());
  }
@Test(expected = RuntimeException.class)
  public void testModelLoadThrowsWrappedException() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/fail/path", false) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        throw new IllegalStateException("Forced fail");
      }
    };

    Annotation annotation = new Annotation("اختبار");
    annotation.set(CoreAnnotations.TextAnnotation.class, "اختبار");

    annotator.annotate(annotation);
  }
@Test
  public void testTokenOffsetsAdjustedCorrectlyAfterNewline() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = segmenter;

    CoreLabel token1 = new CoreLabel();
    token1.setWord("abc");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("de");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    when(segmenter.segmentStringToTokenList("abc")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("de")).thenReturn(Collections.singletonList(token2));

    String text = "abc\nde";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, tokens.size());
    assertEquals("abc", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals("de", tokens.get(2).word());

    assertEquals(0, (int) tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(3, (int) tokens.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals(3, (int) tokens.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(4, (int) tokens.get(1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals(4, (int) tokens.get(2).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(6, (int) tokens.get(2).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testAnnotateEmptyTextAnnotation() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = segmenter;

    when(segmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testSentenceSplitOnTwoNewlinesFalseKeepsMultipleNewlines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = mockSegmenter;

    CoreLabel token1 = new CoreLabel();
    token1.setWord("abc");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(mockSegmenter.segmentStringToTokenList("abc")).thenReturn(Collections.singletonList(token1));

    String text = "abc\n\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("abc", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
  }
@Test
  public void testOnlyNewlineTextWithTokenizeNewlineEnabled() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = mockSegmenter;

    String text = "\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(1, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
  }
@Test
  public void testSegmenterReturnsNullShouldHandleGracefully() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = mockSegmenter;

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(null);

    Annotation annotation = new Annotation("مرحبا");
    annotation.set(CoreAnnotations.TextAnnotation.class, "مرحبا");

    try {
      annotator.annotate(annotation);
      List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNull(result); 
    } catch (Exception e) {
      fail("Should handle null from segmenter without exception");
    }
  }
@Test
  public void testSegmenterThrowsExceptionInAnnotate() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = mockSegmenter;

    when(mockSegmenter.segmentStringToTokenList("خطأ")).thenThrow(new RuntimeException("fake failure"));

    Annotation annotation = new Annotation("خطأ");
    annotation.set(CoreAnnotations.TextAnnotation.class, "خطأ");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException from segmenter");
    } catch (RuntimeException e) {
      assertEquals("fake failure", e.getMessage());
    }
  }
@Test
  public void testEmptyStringWithNewlinesEnabled() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = mockSegmenter;

    String text = "";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotationWithOnlyNonNewlineWhitespace() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = mockSegmenter;

    when(mockSegmenter.segmentStringToTokenList("   ")).thenReturn(Collections.emptyList());

    String text = "   ";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testAnnotationWithInterleavedEmptyAndNonEmptyLines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = mockSegmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("نص");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1); 
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);   

    when(mockSegmenter.segmentStringToTokenList("نص")).thenReturn(Collections.singletonList(token));

    String text = "\nنص\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());

    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals("نص", tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
  }
@Test
  public void testEmptyLinesOnlyWhenTokenizeNewlinesEnabled() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("seg.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("seg", props);
//     annotator.segmenter = mockSegmenter;

    String text = "\n\n";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
  }
@Test
  public void testMixedLanguageTextHandledBySegmenter() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = mockSegmenter;

    String arabic = "مرحبا";
    String latin = "hello";
    String combined = arabic + " " + latin;

    CoreLabel token1 = new CoreLabel();
    token1.setWord(arabic);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, arabic.length());

    CoreLabel token2 = new CoreLabel();
    token2.setWord(latin);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, arabic.length() + 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, combined.length());

    when(mockSegmenter.segmentStringToTokenList(combined)).thenReturn(Arrays.asList(token1, token2));

    Annotation annotation = new Annotation(combined);
    annotation.set(CoreAnnotations.TextAnnotation.class, combined);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("مرحبا", tokens.get(0).word());
    assertEquals("hello", tokens.get(1).word());
  }
@Test
  public void testOneSentenceWithOnlyWhitespaceText() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);
//     annotator.segmenter = mockSegmenter;

    when(mockSegmenter.segmentStringToTokenList("   ")).thenReturn(Collections.emptyList());

    Annotation annotation = new Annotation("   ");
    annotation.set(CoreAnnotations.TextAnnotation.class, "   ");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testAnnotateWithoutSettingTextAnnotation() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false);

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
//     annotator.segmenter = mockSegmenter;

    Annotation annotation = new Annotation((String) null);
    

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException or controlled handling when TextAnnotation is missing");
    } catch (NullPointerException e) {
      
    } catch (Exception e) {
      fail("Unexpected exception type: " + e.getClass());
    }
  }
@Test
  public void testTokenOffsetsAdjustedCorrectlyWithLeadingNewlines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/dummy/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = mockSegmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("نص");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    when(mockSegmenter.segmentStringToTokenList("نص")).thenReturn(Collections.singletonList(token));

    String text = "\nنص";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(2, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals("نص", tokens.get(1).word());

    assertEquals(1, (int) tokens.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(3, (int) tokens.get(1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testModelLoadingWithAdditionalProperties() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arab.model", "/custom/path");
    props.setProperty("arab.verbose", "true");
    props.setProperty("arab.someModelOption", "xyz");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arab", props);

    
//     annotator.segmenter = mockSegmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("اختبار");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    when(mockSegmenter.segmentStringToTokenList("اختبار")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("اختبار");
    annotation.set(CoreAnnotations.TextAnnotation.class, "اختبار");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("اختبار", tokens.get(0).word());
  }
@Test
  public void testMultipleNewlineLinesIgnoredWithNoConfig() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/fake/path", false);
//     annotator.segmenter = mockSegmenter;

    String input = "\n\n\n";

    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);

    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
  }
@Test(expected = RuntimeException.class)
  public void testLoadModelHandlesCheckedException() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy/path", false) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        throw new IllegalArgumentException("Invalid config");
      }
    };

    
    Annotation annotation = new Annotation("نص");
    annotation.set(CoreAnnotations.TextAnnotation.class, "نص");
    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateSentenceListIsEmpty() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/a", false);
//     annotator.segmenter = segmenter;

    Annotation annotation = new Annotation("text doesn't matter");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>()); 

    annotator.annotate(annotation);

    
    assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
  }
@Test
  public void testSegmenterReturnsEmptyList() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/abc", false);
//     annotator.segmenter = mockSegmenter;

    when(mockSegmenter.segmentStringToTokenList("كلام فارغ")).thenReturn(Collections.emptyList());

    Annotation annotation = new Annotation("كلام فارغ");
    annotation.set(CoreAnnotations.TextAnnotation.class, "كلام فارغ");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testUnicodeNewlineLineSeparatorHandled() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = segmenter;

    String text = "السطر1\u2028السطر2";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("السطر1");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("السطر2");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    when(segmenter.segmentStringToTokenList("السطر1")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("السطر2")).thenReturn(Collections.singletonList(token2));

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> output = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, output.size());
    assertEquals("السطر1", output.get(0).word());
    assertEquals("\n", output.get(1).word()); 
    assertEquals("السطر2", output.get(2).word());
  }
@Test
  public void testMultipleTokensReturnedBySegmenter() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/any", false);
//     annotator.segmenter = segmenter;

    CoreLabel token1 = new CoreLabel();
    token1.setWord("واحد");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("اثنان");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    List<CoreLabel> tokenList = Arrays.asList(token1, token2);

    when(segmenter.segmentStringToTokenList("واحد اثنان")).thenReturn(tokenList);

    Annotation annotation = new Annotation("واحد اثنان");
    annotation.set(CoreAnnotations.TextAnnotation.class, "واحد اثنان");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("واحد", result.get(0).word());
    assertEquals("اثنان", result.get(1).word());
  }
@Test
  public void testWhitespaceNewlineSentenceSinglePiece() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("y.model", "/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("y", props);
//     annotator.segmenter = segmenter;

    
    String text = "   \n";

    CoreLabel token = new CoreLabel();
    token.setWord("   ");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(segmenter.segmentStringToTokenList("   ")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("   ", tokens.get(0).word());
    assertEquals("\n", tokens.get(1).word());
  }
@Test
  public void testInvalidLineBreakCharactersHandledAsText() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("x.model", "/model/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = segmenter;

    
    String text = "سطر1\u000Bسطر2";

    CoreLabel token = new CoreLabel();
    token.setWord("سطر1\u000Bسطر2");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    when(segmenter.segmentStringToTokenList(text)).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("سطر1\u000Bسطر2", tokens.get(0).word());
  }
@Test
  public void testNewlineSplitterPropertyNeverDisablesTokenizeNewline() {
    Properties props = new Properties();
    props.setProperty("arab.model", "/path/to/model");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "false");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arab", props);
//     annotator.segmenter = mockSegmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("مرحبا");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("مرحبا");
    annotation.set(CoreAnnotations.TextAnnotation.class, "مرحبا");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("مرحبا", tokens.get(0).word());
  }
@Test
  public void testMultiTokenLineWithOffsetAfterNewline() {
    Properties props = new Properties();
    props.setProperty("seg.model", "/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("seg", props);
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
//     annotator.segmenter = segmenter;

    CoreLabel t1 = new CoreLabel();
    t1.setWord("a");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("b");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(segmenter.segmentStringToTokenList("x")).thenReturn(Collections.singletonList(new CoreLabel() {{
      setWord("x");
      set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
      set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    }}));

    when(segmenter.segmentStringToTokenList("a b")).thenReturn(Arrays.asList(t1, t2));

    Annotation annotation = new Annotation("x\na b");
    annotation.set(CoreAnnotations.TextAnnotation.class, "x\na b");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("x", tokens.get(0).word());
    assertEquals("\n", tokens.get(1).word());
    assertEquals("a", tokens.get(2).word());
    assertEquals("b", tokens.get(3).word());

    assertEquals(2, (int) tokens.get(2).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)); 
    assertEquals(3, (int) tokens.get(2).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testAnnotatorWithPropertyKeysThatShouldBeIgnored() {
    Properties props = new Properties();
    props.setProperty("x.model", "/xyz");
    props.setProperty("x.verbose", "false");
    props.setProperty("other.key", "ignored");
    props.setProperty("xx.model", "wrongmodel"); 

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("x", props);
//     annotator.segmenter = segmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("text");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    when(segmenter.segmentStringToTokenList("text")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.TextAnnotation.class, "text");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("text", result.get(0).word());
  }
@Test
  public void testMultiplePrefixedPropertiesHandledCorrectly() {
    Properties props = new Properties();
    props.setProperty("ar.model", "/model-ar");
    props.setProperty("ar.verbose", "true");
    props.setProperty("fr.model", "/model-fr");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("ar", props);
//     annotator.segmenter = segmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("اختبار");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    when(segmenter.segmentStringToTokenList("اختبار")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("اختبار");
    annotation.set(CoreAnnotations.TextAnnotation.class, "اختبار");

    annotator.annotate(annotation);

    List<CoreLabel> output = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(output);
    assertEquals(1, output.size());
    assertEquals("اختبار", output.get(0).word());
  }
@Test
  public void testSentenceSplitOnTwoNewlinesWithOneNewlineInput() {
    Properties props = new Properties();
    props.setProperty("path.model", "/m");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("path", props);
//     annotator.segmenter = segmenter;

    String input = "abc\ndef";
    CoreLabel token1 = new CoreLabel();
    token1.setWord("abc");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("def");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(segmenter.segmentStringToTokenList("abc")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("def")).thenReturn(Collections.singletonList(token2));

    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, result.size());
    assertEquals("abc", result.get(0).word());
    assertEquals("\n", result.get(1).word());
    assertEquals("def", result.get(2).word());
  }
@Test
  public void testWindowsStyleCarriageReturnNewlineHandledWhenTokenizeNewlineEnabled() {
    Properties props = new Properties();
    props.setProperty("arab.model", "/m");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arab", props);
//     annotator.segmenter = segmenter;

    String input = "سطر1\r\nسطر2";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("سطر1");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("سطر2");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    when(segmenter.segmentStringToTokenList("سطر1")).thenReturn(Collections.singletonList(token1));
    when(segmenter.segmentStringToTokenList("سطر2")).thenReturn(Collections.singletonList(token2));

    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("سطر1", tokens.get(0).word());
    assertEquals("\n", tokens.get(1).word()); 
    assertEquals("سطر2", tokens.get(2).word());
  }
@Test
  public void testSegmentStringToTokenListReturnsNullHandledSafely() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/some/model", false);
//     annotator.segmenter = segmenter;

    when(segmenter.segmentStringToTokenList("")).thenReturn(null);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");

    try {
      annotator.annotate(annotation);
      assertNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
    } catch (Exception e) {
      fail("Failure: should not throw on null token list");
    }
  }
@Test
  public void testNewlinesAreSkippedWhenTokenizeNewlineFalse() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/some/model", false);
//     annotator.segmenter = segmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("abc");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(segmenter.segmentStringToTokenList("abc\ndef")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("abc\ndef");
    annotation.set(CoreAnnotations.TextAnnotation.class, "abc\ndef");

    annotator.annotate(annotation);
    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("abc", result.get(0).word());
  }
@Test
  public void testTextWithOnlyNewlineIsNotSegmentedWhenDisabled() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/model/path", false);
//     annotator.segmenter = segmenter;

    when(segmenter.segmentStringToTokenList("\n")).thenReturn(Collections.emptyList());

    Annotation annotation = new Annotation("\n");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\n");

    annotator.annotate(annotation);
    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testTwoIdenticalNewlinesWithSplitOnTwoKeepsOnlyTwo() {
    Properties props = new Properties();
    props.setProperty("arab.model", "/fake/path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arab", props);
//     annotator.segmenter = segmenter;

    String input = "\n\n\n";

    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(2, result.size()); 
    assertEquals("\n", result.get(0).word());
    assertEquals("\n", result.get(1).word());
  }
@Test
  public void testSingleLineNoNewlineStillOffsetsCorrectly() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/dummy", false);
//     annotator.segmenter = segmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("token");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    when(segmenter.segmentStringToTokenList("token")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("token");
    annotation.set(CoreAnnotations.TextAnnotation.class, "token");

    annotator.annotate(annotation);
    List<CoreLabel> output = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(1, output.size());
    assertEquals("token", output.get(0).word());
    assertEquals(0, (int) output.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(5, (int) output.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testTextWithLinesEndingEmptySegmentReturnsNoTokens() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("seg.model", "/void");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("seg", props);
//     annotator.segmenter = segmenter;

    when(segmenter.segmentStringToTokenList("x")).thenReturn(Collections.emptyList());
    when(segmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    String input = "x\n";
    Annotation annotation = new Annotation(input);
    annotation.set(CoreAnnotations.TextAnnotation.class, input);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("x", tokens.get(0).word());
    assertEquals("\n", tokens.get(1).word());
  }
@Test
  public void testLoadModelCombinesPropsCorrectly() {
    Properties props = new Properties();
    props.setProperty("my.model", "/custom/path");
    props.setProperty("my.verbose", "true");
    props.setProperty("my.window", "5");
    props.setProperty("my.ignored", null);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("my", props);
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
//     annotator.segmenter = segmenter;

    CoreLabel token = new CoreLabel();
    token.setWord("abc");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(segmenter.segmentStringToTokenList("abc")).thenReturn(Collections.singletonList(token));

    Annotation annotation = new Annotation("abc");
    annotation.set(CoreAnnotations.TextAnnotation.class, "abc");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("abc", result.get(0).word());
  }
@Test
  public void testTextContainingMultipleLogicalSentencesSingleString() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("/model/file", false);
//     annotator.segmenter = segmenter;

    String fullText = "هذه جملة. وهذه أخرى. وثالثة";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("هذه");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("جملة");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("أخرى");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);

    when(segmenter.segmentStringToTokenList(fullText)).thenReturn(Arrays.asList(token1, token2, token3));

    Annotation annotation = new Annotation(fullText);
    annotation.set(CoreAnnotations.TextAnnotation.class, fullText);

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("هذه", tokens.get(0).word());
    assertEquals("جملة", tokens.get(1).word());
    assertEquals("أخرى", tokens.get(2).word());
  } 
}
