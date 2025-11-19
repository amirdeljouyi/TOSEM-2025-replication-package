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

public class ArabicSegmenterAnnotator_4_GPTLLMTest {

 @Test
  public void testAnnotateSingleSentenceWithoutNewlines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      {
//         this.segmenter = mockSegmenter;
      }
    };

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    String text = "هذه جملة عربية";
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(sentences);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("هذه");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("جملة");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("عربية");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    when(mockSegmenter.segmentStringToTokenList(text)).thenReturn(tokens);

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, tokens);
  }
@Test
  public void testAnnotateAnnotationWithoutSentences() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      {
//         this.segmenter = mockSegmenter;
      }
    };

    Annotation annotation = mock(Annotation.class);
    when(annotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
    when(annotation.get(CoreAnnotations.TextAnnotation.class)).thenReturn("العربية لغة عظيمة");

    CoreLabel token = new CoreLabel();
    token.setWord("العربية");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    when(mockSegmenter.segmentStringToTokenList("العربية لغة عظيمة")).thenReturn(tokens);

    annotator.annotate(annotation);

    verify(annotation).set(CoreAnnotations.TokensAnnotation.class, tokens);
  }
@Test(expected = RuntimeException.class)
  public void testConstructorMissingModelThrowsException() {
    Properties props = new Properties();
    new ArabicSegmenterAnnotator("arabic", props);
  }
@Test
  public void testConstructorWithVerboseAndModel() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "modelPath");
    props.setProperty("arabic.verbose", "true");
    props.setProperty("arabic.someOther", "data");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithNewlineToken() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      {
//         this.segmenter = mockSegmenter;
      }
    };

    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("أهلاً");
    tokenA.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tokenA.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("بكم");
    tokenB.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    tokenB.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    List<CoreLabel> tokens1 = new ArrayList<>();
    tokens1.add(tokenA);
    tokens1.add(tokenB);

    CoreLabel tokenC = new CoreLabel();
    tokenC.setWord("في");
    tokenC.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    tokenC.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel tokenD = new CoreLabel();
    tokenD.setWord("الصف");
    tokenD.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    tokenD.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    List<CoreLabel> tokens2 = new ArrayList<>();
    tokens2.add(tokenC);
    tokens2.add(tokenD);

    when(mockSegmenter.segmentStringToTokenList("أهلاً بكم")).thenReturn(tokens1);
    when(mockSegmenter.segmentStringToTokenList("في الصف")).thenReturn(tokens2);

    String text = "أهلاً بكم\nفي الصف";

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertNotNull(sentence);
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> output = annotator.requirementsSatisfied();

    assertTrue(output.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(output.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(output.contains(CoreAnnotations.OriginalTextAnnotation.class));
  }
@Test
  public void testRequiresReturnsEmptySet() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "model/path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> req = annotator.requires();

    assertNotNull(req);
    assertTrue(req.isEmpty());
  }
@Test
  public void testSentenceSplitOnTwoNewlines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      {
//         this.segmenter = mockSegmenter;
      }
    };

    String text = "مرحبا\n\nبكم";

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("مرحبا");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    List<CoreLabel> firstTokens = new ArrayList<>();
    firstTokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("بكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    List<CoreLabel> secondTokens = new ArrayList<>();
    secondTokens.add(token2);

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(firstTokens);
    when(mockSegmenter.segmentStringToTokenList("بكم")).thenReturn(secondTokens);

    annotator.annotate(annotation);

    assertNotNull(sentence);
  }
@Test
  public void testAnnotateEmptySentence() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  }
@Test
  public void testAnnotateSentenceWithOnlyNewlines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("\n\n");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testAnnotateEmptyTextNoSentences() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");

    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    annotator.annotate(annotation);

    assertEquals(Collections.emptyList(), annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testSegmenterThrowsCheckedException() throws Exception {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
//         throw new Exception("Simulated error");
      }
    };
  }
@Test(expected = RuntimeException.class)
  public void testSegmenterThrowsRuntimeExceptionDuringLoad() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", new Properties()) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        throw new RuntimeException("model loading failed");
      }
    };
  }
@Test
  public void testCharacterOffsetAdjustedCorrectlyWithNewlines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "model");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    String text = "أهلا\nأصدقاء";
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("أهلا");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    List<CoreLabel> segment1 = new ArrayList<>();
    segment1.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("أصدقاء");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    List<CoreLabel> segment2 = new ArrayList<>();
    segment2.add(token2);

    when(mockSegmenter.segmentStringToTokenList("أهلا")).thenReturn(segment1);
    when(mockSegmenter.segmentStringToTokenList("أصدقاء")).thenReturn(segment2);

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testNewlineTokenizationSkipsExtraNewlinesInTwoNewlineMode() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "mockModel");
    props.setProperty("annotator.verbose", "false");
    props.setProperty("annotator.model", "mockModel");
    props.setProperty("annotator.option1", "value1");
    props.setProperty("annotator.option2", "value2");
    props.setProperty("annotator.tokenize.whitespace", "true");
    props.setProperty("stanford.corenlp.newlineIsSentenceBreak", "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("annotator", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    String text = "السطر الأول\n\nالسطر الثاني\n\n\n\nالسطر الثالث";

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    when(mockSegmenter.segmentStringToTokenList("السطر الأول")).thenReturn(Collections.emptyList());
    when(mockSegmenter.segmentStringToTokenList("السطر الثاني")).thenReturn(Collections.emptyList());
    when(mockSegmenter.segmentStringToTokenList("السطر الثالث")).thenReturn(Collections.emptyList());

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testAnnotateSentenceWithMixedNewlinesAndText() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "model");
    props.setProperty("stanford.corenlp.newlineIsSentenceBreak", "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    String text = "\nالسلام\n\nعليكم\n";
    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    when(mockSegmenter.segmentStringToTokenList("السلام")).thenReturn(Collections.emptyList());
    when(mockSegmenter.segmentStringToTokenList("عليكم")).thenReturn(Collections.emptyList());

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testStaticNewlinePatternMatchesCorrectly() {
    String newline1 = "\n";
    String newline2 = "\r";
    String newline3 = "\r\n";

    assertTrue("\n".matches("\\R"));
    assertTrue("\r\n".matches("\\R"));
    assertTrue("\r".matches("\\R"));
    assertFalse("notnewline".matches("\\R"));
  }
@Test
  public void testMultipleCoreLabelsPreserveOffsetsCorrectly() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "path");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    String input = "مرحبا\nبالعالم";
    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    CoreLabel label1 = new CoreLabel();
    label1.setWord("مرحبا");
    label1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel label2 = new CoreLabel();
    label2.setWord("بالعالم");
    label2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.singletonList(label1));
    when(mockSegmenter.segmentStringToTokenList("بالعالم")).thenReturn(Collections.singletonList(label2));

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
    assertEquals(Integer.valueOf(6), label2.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)); 
    assertEquals(Integer.valueOf(13), label2.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testPropertiesWithoutMatchingPrefixAreIgnored() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "path/to/model");
    props.setProperty("otherAnnotator.model", "shouldBeIgnored");
    props.setProperty("arabic.verbose", "true");
    props.setProperty("arabic.optionX", "XVal");
    props.setProperty("unrelated.key", "junk");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testModelLoadThrowsCheckedException() {
    Properties props = new Properties() {{
      setProperty("arabic.model", "invalidPath");
    }};

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        throw new RuntimeException("Simulated load failure");
      }
    };
  }
@Test
  public void testTokensAnnotationIsEmptyWhenSegmenterReturnsNull() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("نص عربي");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("نص عربي");
    when(mockSegmenter.segmentStringToTokenList("نص عربي")).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    
    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, null);
  }
@Test
  public void testSegmenterReturnsEmptyList_TokensStillSet() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("اختبار");
    when(mockSegmenter.segmentStringToTokenList("اختبار")).thenReturn(Collections.emptyList());

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  }
@Test
  public void testAnnotateWithNullTextOnSentence() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    when(mockSegmenter.segmentStringToTokenList(null)).thenReturn(Collections.emptyList());

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  }
@Test
  public void testTokenizeNewlineTrueWithoutTwoNewlineSplit() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    String text = "مرحبا\nبكم";
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.emptyList());
    when(mockSegmenter.segmentStringToTokenList("بكم")).thenReturn(Collections.emptyList());

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testVerboseLoggingFlagSetButNoError() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("arabic.verbose", "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    assertNotNull(annotator);
  }
@Test
  public void testModelPropertiesOverrideMainProps() {
    Properties props = new Properties();
    props.setProperty("seg.model", "model.from.props");
    props.setProperty("seg.verbose", "true");
    props.setProperty("seg.caseFeature", "false");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("seg", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotatorRequirementsSatisfiedIncludesExpectedTypes() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "xyz");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();

    assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.OriginalTextAnnotation.class));
  }
@Test
  public void testAnnotatorDoesNothingIfNoTextAndNoSentences() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "test");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation(""); 
    annotator.annotate(annotation); 
    assertNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testNewlineOnlyWithoutTokenizationOption() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("\n\n");

    when(mockSegmenter.segmentStringToTokenList("\n\n")).thenReturn(Collections.emptyList());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  }
@Test
  public void testOnlyNewlineTokenizationWithThreeNewlines() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("\n\n\n");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testAnnotationWithTextAnnotationButNoSentences() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "any-model");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("النص هنا");
    annotation.set(CoreAnnotations.TextAnnotation.class, "النص هنا");

    CoreLabel label = new CoreLabel();
    label.setWord("النص");
    label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);

    when(mockSegmenter.segmentStringToTokenList("النص هنا")).thenReturn(tokens);

    annotator.annotate(annotation);

    List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(resultTokens);
    assertEquals(1, resultTokens.size());
    assertEquals("النص", resultTokens.get(0).word());
  }
@Test
  public void testModelLoadingFailurePropagatesCheckedException() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    try {
      new ArabicSegmenterAnnotator("arabic", props) {
//         @Override
        protected void loadModel(String segLoc, Properties props) {
          throw new RuntimeException("simulated error");
        }
      };
      fail("Expected RuntimeException not thrown");
    } catch (RuntimeException e) {
      assertEquals("simulated error", e.getMessage());
    }
  }
@Test
  public void testNewlineAfterEmptyLinePreservesOffsetUpdate() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("arabic.verbose", "false");
    props.setProperty("stanford.corenlp.newlineIsSentenceBreak", "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("مرحبا\n\nعالم");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreLabel label1 = new CoreLabel();
    label1.setWord("مرحبا");
    label1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel label2 = new CoreLabel();
    label2.setWord("عالم");
    label2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    List<CoreLabel> first = new ArrayList<>();
    first.add(label1);
    List<CoreLabel> second = new ArrayList<>();
    second.add(label2);

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(first);
    when(mockSegmenter.segmentStringToTokenList("عالم")).thenReturn(second);

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());

    assertEquals(Integer.valueOf(12), label2.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(16), label2.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testConstructorParsesOnlyMatchingPrefixProperties() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "final-model");
    props.setProperty("arabic.optionA", "valA");
    props.setProperty("unrelated.key", "other");
    props.setProperty("arabic.verbose", "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    assertNotNull(annotator);
  }
@Test
  public void testNewlinesTokenizationPreservesOrderOfTokens() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = mock(CoreMap.class);

    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("ألف\nباء\nجيم");
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreLabel label1 = new CoreLabel();
    label1.setWord("ألف");
    label1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel label2 = new CoreLabel();
    label2.setWord("باء");
    label2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel label3 = new CoreLabel();
    label3.setWord("جيم");
    label3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    when(mockSegmenter.segmentStringToTokenList("ألف")).thenReturn(Collections.singletonList(label1));
    when(mockSegmenter.segmentStringToTokenList("باء")).thenReturn(Collections.singletonList(label2));
    when(mockSegmenter.segmentStringToTokenList("جيم")).thenReturn(Collections.singletonList(label3));

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());

    assertEquals(Integer.valueOf(8), label3.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(11), label3.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testOriginalTextPreservedOnNewlineLabel() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "model");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("كلمة\n");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    CoreLabel label = new CoreLabel();
    label.setWord("كلمة");
    label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    when(mockSegmenter.segmentStringToTokenList("كلمة")).thenReturn(Collections.singletonList(label));

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
    assertEquals("كلمة", label.word());
  }
@Test
  public void testSetNullSentenceListDoesNotThrow() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("some text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
    annotation.set(CoreAnnotations.TextAnnotation.class, "some text");

    when(mockSegmenter.segmentStringToTokenList("some text")).thenReturn(new ArrayList<CoreLabel>());

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testSegmentStringToTokenListReturnsNullHandledGracefully() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("السلام عليكم")).thenReturn(null);

    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("السلام عليكم");

    Annotation annotation = new Annotation("");
    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

    annotator.annotate(annotation);
    
    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, null);
  }
@Test
  public void testWhitespaceOnlyInputWithTokenizeNewlineTrue() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    when(mockSegmenter.segmentStringToTokenList("   ")).thenReturn(new ArrayList<CoreLabel>());

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("annotator.model", "dummy");
    props.setProperty("annotator.verbose", "false");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("annotator", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("   ");

    Annotation annotation = new Annotation("");
    List<CoreMap> sentenceList = Collections.singletonList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testMultipleNewlinesWithSentenceSplitFalse() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("أهلاً\n\nوسهلاً")).thenReturn(new ArrayList<CoreLabel>());

    Properties props = new Properties();
    props.setProperty("arabic.model", "test");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "never");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("أهلاً\n\nوسهلاً");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testNewlinePreservedWhenTokenizeNewlineTrueAndBreakIsAlways() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("كلمة");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    when(mockSegmenter.segmentStringToTokenList("كلمة")).thenReturn(Collections.singletonList(token1));

    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("كلمة\n");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    
    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
  }
@Test
  public void testOffsetShiftedCorrectlyWhenMultipleSegmentsBeforeNewline() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("أهلاً");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("وسهلاً");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    when(mockSegmenter.segmentStringToTokenList("أهلاً")).thenReturn(Collections.singletonList(t1));
    when(mockSegmenter.segmentStringToTokenList("وسهلاً")).thenReturn(Collections.singletonList(t2));

    Properties props = new Properties();
    props.setProperty("arabic.model", "mockModel");
    props.setProperty(StanfordCoreNLP.NEWLINE_SPLITTER_PROPERTY, "true");
    props.setProperty(StanfordCoreNLP.NEWLINE_IS_SENTENCE_BREAK_PROPERTY, "always");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("أهلاً\nوسهلاً");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), anyList());
    assertEquals(Integer.valueOf(6), t2.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(11), t2.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testAnnotatorConstructorWithMinimalProperties() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "just-a-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props);
    assertNotNull(annotator);
  }
@Test
  public void testDoOneSentenceHandlesEmptyCoreMapContent() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    Properties props = new Properties();
    props.setProperty("arabic.model", "basic");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  }
@Test
  public void testEmptyStringWithNoNewlineTreatsAsSingleSegment() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(Collections.emptyList());

    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-model");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    Annotation annotation = new Annotation("");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  }
@Test
  public void testTwoNewlineSplitAddsOnlyTwoNewlineTokens() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("واحد");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("اثنان");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    when(mockSegmenter.segmentStringToTokenList("واحد")).thenReturn(Collections.singletonList(token1));
    when(mockSegmenter.segmentStringToTokenList("اثنان")).thenReturn(Collections.singletonList(token2));

    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty("stanford.corenlp.newlineIsSentenceBreak", "two");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("واحد\n\nاثنان\n\n\n");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), argThat(tokens -> {
      int count = 0;
      for (Object obj : tokens) {
        CoreLabel label = (CoreLabel) obj;
        if ("\n".equals(label.word())) {
          count++;
        }
      }
      return count == 2;
    }));
  }
@Test
  public void testThrowsRuntimeExceptionWhenModelPathNotFound() {
    Properties props = new Properties();
    props.setProperty("someOtherKey", "value");

    try {
      new ArabicSegmenterAnnotator("missing", props);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Expected a property missing.model"));
    }
  }
@Test
  public void testNullOutputFromSegmenterHandledWithoutCrash() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("abc")).thenReturn(null);

    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("abc");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, null);
  }
@Test
  public void testAnnotationWithoutTextOrSentencesDoesNothing() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "stub");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mock(ArabicSegmenter.class);
    }};

    Annotation annotation = new Annotation("");
    

    annotator.annotate(annotation);
    assertNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testMultiplePropertiesParsedCorrectlyInConstructor() {
    Properties props = new Properties();
    props.setProperty("myAnno.model", "path/to/model");
    props.setProperty("myAnno.verbose", "true");
    props.setProperty("myAnno.option1", "123");
    props.setProperty("myAnno.option2", "xyz");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("myAnno", props);
    assertNotNull(annotator);
  }
@Test
  public void testNonTokenTextDoesNotCrashAnnotator() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList("\n\n")).thenReturn(Collections.emptyList());

    Properties props = new Properties();
    props.setProperty("arabic.model", "x");
    props.setProperty("stanford.corenlp.newlineIsSentenceBreak", "never");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("\n\n");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  }
@Test
  public void testNewlineSplitRespectsSingleNewlineLimitWithAlwaysMode() {
    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy-model");
    props.setProperty("stanford.corenlp.newlineIsSentenceBreak", "always");

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(Collections.emptyList());
    when(mockSegmenter.segmentStringToTokenList("عالم")).thenReturn(Collections.emptyList());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {{
//       this.segmenter = mockSegmenter;
    }};

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("مرحبا\nعالم");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(sentence).set(eq(CoreAnnotations.TokensAnnotation.class), argThat(tokens -> {
      int count = 0;
      for (Object obj : tokens) {
        CoreLabel label = (CoreLabel) obj;
        if ("\n".equals(label.word())) {
          count++;
        }
      }
      return count == 1;
    }));
  } 
}
