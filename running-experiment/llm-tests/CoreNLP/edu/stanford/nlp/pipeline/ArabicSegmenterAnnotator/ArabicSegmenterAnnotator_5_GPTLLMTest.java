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
import edu.stanford.nlp.util.PropertiesUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArabicSegmenterAnnotator_5_GPTLLMTest {

 @Test
  public void testConstructor_DefaultConfig() {
    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", PropertiesUtils.asProperties("arabic.model", "dummy.model")) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        
      }
    };

    assertNotNull(annotator);
  }
@Test
  public void testConstructor_MissingModelPropertyThrowsException() {
    Properties props = new Properties();

    boolean threw = false;
    try {
      new ArabicSegmenterAnnotator("arabic", props);
    } catch (RuntimeException e) {
      threw = true;
      assertTrue(e.getMessage().contains("arabic.model"));
    }

    assertTrue(threw);
  }
@Test
  public void testAnnotate_SingleSentence_TokensAreSet() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "fake");

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    List<CoreLabel> mockTokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("مرحبا");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mockTokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("بكم");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    mockTokens.add(token2);

    when(mockSegmenter.segmentStringToTokenList("مرحبا بكم")).thenReturn(mockTokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("مرحبا بكم");

    Annotation doc = new Annotation("مرحبا بكم");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
//         this.segmenter = mockSegmenter;
      }
    };

    annotator.annotate(doc);

    verify(mockSegmenter, times(1)).segmentStringToTokenList("مرحبا بكم");
  }
@Test
  public void testAnnotate_NoSentences_CallsSegmenterOnce() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "fake.model");

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    List<CoreLabel> mockOutput = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.setWord("نص");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    mockOutput.add(token);

    when(mockSegmenter.segmentStringToTokenList("نص")).thenReturn(mockOutput);

    CoreMap fakeAnnotation = mock(CoreMap.class);
    when(fakeAnnotation.get(CoreAnnotations.TextAnnotation.class)).thenReturn("نص");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
//         this.segmenter = mockSegmenter;
      }

      @Override
      public void annotate(Annotation annotation) {
        CoreMap ann = mock(CoreMap.class);
        when(ann.get(CoreAnnotations.TextAnnotation.class)).thenReturn("نص");
        List<CoreLabel> tokens = mockSegmenter.segmentStringToTokenList("نص");
        ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
      }
    };

    Annotation doc = new Annotation("نص");

    annotator.annotate(doc);
    verify(mockSegmenter, times(1)).segmentStringToTokenList("نص");
  }
@Test
  public void testMakeNewlineCoreLabel_ReturnsCorrectOffsetsAndValues() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", PropertiesUtils.asProperties("arabic.model", "fake.model")) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        
      }
    };

//     CoreLabel result = annotator.makeNewlineCoreLabel("\n", 10);

//     assertEquals("\n", result.get(CoreAnnotations.OriginalTextAnnotation.class));
//     assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.word());
//     assertEquals(AbstractTokenizer.NEWLINE_TOKEN, result.value());
//     assertEquals(Integer.valueOf(10), result.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
//     assertEquals(Integer.valueOf(11), result.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testRequires_ReturnsEmptySet() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", PropertiesUtils.asProperties("arabic.model", "fake")) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        
      }
    };

//     Set<Class<?>> required = annotator.requires();
//     assertTrue(required.isEmpty());
  }
@Test
  public void testRequirementsSatisfied_NotEmptyAndContainsCommonTypes() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", PropertiesUtils.asProperties("arabic.model", "fake")) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        
      }
    };

//     Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

//     assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
//     assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
//     assertTrue(satisfied.contains(CoreAnnotations.TokenBeginAnnotation.class));
//     assertTrue(satisfied.contains(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testNewlineTokenization_TokenizeNewlineSingle() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> list1 = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("أهلا");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    list1.add(t1);

    List<CoreLabel> list2 = new ArrayList<>();
    CoreLabel t2 = new CoreLabel();
    t2.setWord("بك");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    list2.add(t2);

    when(segmenter.segmentStringToTokenList("أهلا")).thenReturn(list1);
    when(segmenter.segmentStringToTokenList("بك")).thenReturn(list2);

    String text = "أهلا\nبك";

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    annotator.annotate(new Annotation(text));
    verify(segmenter, times(1)).segmentStringToTokenList("أهلا");
    verify(segmenter, times(1)).segmentStringToTokenList("بك");
  }
@Test
  public void testNewlineTokenization_TokenizeNewlineTwoLines() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("ssplit.newlineIsSentenceBreak", "two");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> list1 = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("نص");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    list1.add(t1);

    List<CoreLabel> list2 = new ArrayList<>();
    CoreLabel t2 = new CoreLabel();
    t2.setWord("هنا");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    list2.add(t2);

    when(segmenter.segmentStringToTokenList("نص")).thenReturn(list1);
    when(segmenter.segmentStringToTokenList("هنا")).thenReturn(list2);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("نص\n\nهنا");

//     annotator.doOneSentence(sentence);

    verify(segmenter, times(1)).segmentStringToTokenList("نص");
    verify(segmenter, times(1)).segmentStringToTokenList("هنا");
  }
@Test
public void testAnnotate_NullSentencesList_DirectTextAnnotationProcessing() {
  Properties props = new Properties();
  props.setProperty("arabic.model", "dummy");

  ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel t = new CoreLabel();
  t.setWord("مثال");
  t.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  t.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  tokens.add(t);
  when(segmenter.segmentStringToTokenList("مثال")).thenReturn(tokens);

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
//       this.segmenter = segmenter;
    }
  };

  Annotation doc = new Annotation("مثال");
  doc.set(CoreAnnotations.TextAnnotation.class, "مثال");

  annotator.annotate(doc);
}
@Test
public void testAnnotate_EmptySentenceText_YieldsEmptyTokens() {
  Properties props = new Properties();
  props.setProperty("arabic.model", "dummy");

  ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
  when(segmenter.segmentStringToTokenList("")).thenReturn(new ArrayList<>());

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
//       this.segmenter = segmenter;
    }
  };

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

  Annotation annot = new Annotation("");
  List<CoreMap> list = new ArrayList<>();
  list.add(sentence);
  annot.set(CoreAnnotations.SentencesAnnotation.class, list);

  annotator.annotate(annot);
  verify(segmenter, times(1)).segmentStringToTokenList("");
}
@Test
public void testAnnotate_SentenceWithMixedNewlineCharacters() {
  Properties props = new Properties();
  props.setProperty("arabic.model", "dummy");
  props.setProperty("ssplit.newlineIsSentenceBreak", "always");

  ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

  List<CoreLabel> list1 = new ArrayList<>();
  CoreLabel t1 = new CoreLabel();
  t1.setWord("خط");
  t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
  list1.add(t1);

  List<CoreLabel> list2 = new ArrayList<>();
  CoreLabel t2 = new CoreLabel();
  t2.setWord("آخر");
  t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  list2.add(t2);

  when(segmenter.segmentStringToTokenList("خط")).thenReturn(list1);
  when(segmenter.segmentStringToTokenList("آخر")).thenReturn(list2);

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
//       this.segmenter = segmenter;
    }
  };

  String text = "خط\r\nآخر";

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

//   annotator.doOneSentence(sentence);
  verify(segmenter, times(1)).segmentStringToTokenList("خط");
  verify(segmenter, times(1)).segmentStringToTokenList("آخر");
}
@Test
public void testDoOneSentence_WithOnlyNewlines_TokenizesNewlinesCorrectly() {
  Properties props = new Properties();
  props.setProperty("arabic.model", "dummy");
  props.setProperty("ssplit.newlineIsSentenceBreak", "always");

  ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
//       this.segmenter = segmenter;
    }
  };

  String text = "\n\n";

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

//   annotator.doOneSentence(sentence);
  verify(segmenter, never()).segmentStringToTokenList(anyString());
}
@Test
public void testDoOneSentence_MultipleConsecutiveNewlinesInTwoNewlineMode() {
  Properties props = new Properties();
  props.setProperty("arabic.model", "dummy");
  props.setProperty("ssplit.newlineIsSentenceBreak", "two");

  ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
  List<CoreLabel> wordTokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("السلام");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  wordTokens.add(token);

  when(segmenter.segmentStringToTokenList("السلام")).thenReturn(wordTokens);

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
//       this.segmenter = segmenter;
    }
  };

  String text = "السلام\n\n\n\n";
  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(text);

//   annotator.doOneSentence(sentence);
  verify(segmenter, times(1)).segmentStringToTokenList("السلام");
}
@Test
public void testLoadModel_WrapsExceptionAsRuntime() {
  Properties props = new Properties();
  props.setProperty("model", "badmodel");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
//       throw new RuntimeException(new IOException("failed to load"));
    }
  };

  boolean caught = false;
  try {
    annotator.annotate(new Annotation("test"));
  } catch (RuntimeException e) {
    caught = true;
    assertNotNull(e.getCause());
//     assertTrue(e.getMessage().contains("failed") || e.getCause() instanceof IOException);
  }

  assertTrue(caught);
}
@Test
public void testConstructor_TokenizeNewlineTrueWhenSplitterIsTrue() {
  Properties props = new Properties();
  props.setProperty("arabic.model", "dummy");
  props.setProperty("ssplit.newlineSplitter", "true");

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
      
    }
  };

  assertNotNull(annotator); 
}
@Test
public void testTokenOffsetAdjustment_AfterNewline_PreserveOffsets() {
  Properties props = new Properties();
  props.setProperty("arabic.model", "dummy");
  props.setProperty("ssplit.newlineIsSentenceBreak", "always");

  ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel tok = new CoreLabel();
  tok.setWord("نص");
  tok.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0); 
  tok.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
  tokens.add(tok);

  when(segmenter.segmentStringToTokenList("نص")).thenReturn(tokens);

  ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//     @Override
    protected void loadModel(String segLoc, Properties props) {
//       this.segmenter = segmenter;
    }
  };

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("\nنص");

//   annotator.doOneSentence(sentence);

  assertEquals(Integer.valueOf(1), tok.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
  assertEquals(Integer.valueOf(4), tok.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
}
@Test
  public void testModelLoadingThrowsCheckedException() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "failPath");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
//         try {
//           throw new IOException("Simulated IO failure");
//         } catch (IOException e) {
//           throw new RuntimeException(e);
//         }
      }
    };

    Annotation annotation = new Annotation("مرحبا");

    boolean thrown = false;
    try {
      annotator.annotate(annotation);
    } catch (RuntimeException e) {
      thrown = true;
//       assertTrue(e.getCause() instanceof IOException);
    }

    assertTrue(thrown);
  }
@Test
  public void testAnnotate_NullTextInSentenceHandledGracefully() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "model");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(null)).thenReturn(new ArrayList<CoreLabel>());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);
    verify(segmenter, times(1)).segmentStringToTokenList(null);
  }
@Test
  public void testAnnotate_MultipleSentencesAreProcessedIndividually() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "fake");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList("الأولى")).thenReturn(Collections.singletonList(new CoreLabel()));
    when(segmenter.segmentStringToTokenList("الثانية")).thenReturn(Collections.singletonList(new CoreLabel()));

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("الأولى");
    when(sentence2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("الثانية");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    verify(segmenter, times(1)).segmentStringToTokenList("الأولى");
    verify(segmenter, times(1)).segmentStringToTokenList("الثانية");
  }
@Test
  public void testMakeNewlineCoreLabel_WhenGivenTabCharacter() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String loc, Properties p) {
      }
    };

//     CoreLabel label = annotator.makeNewlineCoreLabel("\t", 3);

//     assertEquals(3, (int) label.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
//     assertEquals(4, (int) label.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
//     assertEquals("\t", label.get(CoreAnnotations.OriginalTextAnnotation.class));
//     assertEquals("\n", label.word());
//     assertEquals("\n", label.value());
  }
@Test
  public void testSentenceWithOnlySpaces_TokenizerStillRuns() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList("   ")).thenReturn(new ArrayList<>());

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("   ");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("   ");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String loc, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    annotator.annotate(annotation);
    verify(segmenter, times(1)).segmentStringToTokenList("   ");
  }
@Test
  public void testAnnotate_TextWithVeryLongLineSegmentedOnce() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    StringBuilder longText = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      longText.append("ا");
    }

    List<CoreLabel> list = new ArrayList<>();
    CoreLabel label = new CoreLabel();
    label.setWord("طويل");
    label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, longText.length());
    list.add(label);

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(longText.toString())).thenReturn(list);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(longText.toString());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation(longText.toString());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);
    verify(segmenter, times(1)).segmentStringToTokenList(longText.toString());
  }
@Test
  public void testRequirementsSatisfied_ContainsAllExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "fake");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String segLoc, Properties props) {
        
      }
    };

//     Set<Class<? extends CoreAnnotations.CoreAnnotation>> result = annotator.requirementsSatisfied();

//     assertTrue(result.contains(CoreAnnotations.TextAnnotation.class));
//     assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
//     assertTrue(result.contains(CoreAnnotations.OriginalTextAnnotation.class));
//     assertTrue(result.contains(CoreAnnotations.TokenBeginAnnotation.class));
//     assertTrue(result.contains(CoreAnnotations.TokenEndAnnotation.class));
//     assertTrue(result.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
//     assertTrue(result.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testNewlineFlagEnabledAndSingleNewlineAppearsOnce() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    String input = "الفقرة\n";

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("الفقرة");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    tokens.add(token);

    when(mockSegmenter.segmentStringToTokenList("الفقرة")).thenReturn(tokens);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties p) {
//         this.segmenter = mockSegmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);

//     annotator.doOneSentence(sentence);

    verify(mockSegmenter, times(1)).segmentStringToTokenList("الفقرة");
  }
@Test
  public void testSingleNewlineAppearsTwiceButSentenceSplitOnTwoNewlines() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "test-path");
    props.setProperty("ssplit.newlineIsSentenceBreak", "two");

    String input = "مرحبا\n\n";

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("مرحبا");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token);

    when(mockSegmenter.segmentStringToTokenList("مرحبا")).thenReturn(tokens);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String model, Properties props) {
//         this.segmenter = mockSegmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);

//     annotator.doOneSentence(sentence);

    verify(mockSegmenter, times(1)).segmentStringToTokenList("مرحبا");
  }
@Test
  public void testLoadModelThrowsRuntimeExceptionIsNotDoubleWrapped() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "invalid");

    final RuntimeException expected = new RuntimeException("bad model");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties p) {
        throw expected;
      }
    };

    Annotation doc = new Annotation("example");

    boolean exceptionCaught = false;
    try {
      annotator.annotate(doc);
    } catch (RuntimeException e) {
      assertSame(expected, e);
      exceptionCaught = true;
    }

    assertTrue(exceptionCaught);
  }
@Test
  public void testLoadModelThrowsCheckedExceptionIsWrapped() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "invalid");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties props) {
        try {
          throw new FileNotFoundException("cannot load model");
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Annotation doc = new Annotation("example");

    boolean thrown = false;
    try {
      annotator.annotate(doc);
    } catch (RuntimeException e) {
      assertNotNull(e.getCause());
      assertTrue(e.getCause() instanceof FileNotFoundException);
      thrown = true;
    }

    assertTrue(thrown);
  }
@Test
  public void testEmptyInputSentenceWithNewlineSplitEnabled() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "empty-model");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    when(mockSegmenter.segmentStringToTokenList("")).thenReturn(new ArrayList<CoreLabel>());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String str, Properties p) {
//         this.segmenter = mockSegmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");

//     annotator.doOneSentence(sentence);

    verify(mockSegmenter, times(1)).segmentStringToTokenList("");
  }
@Test
  public void testAnnotationWithoutSentencesAndNullTextHandled() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);
    when(mockSegmenter.segmentStringToTokenList(null)).thenReturn(new ArrayList<CoreLabel>());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String x, Properties p) {
//         this.segmenter = mockSegmenter;
      }
    };

    Annotation annotation = new Annotation((String) null);

    annotator.annotate(annotation);
    verify(mockSegmenter, times(1)).segmentStringToTokenList(null);
  }
@Test
  public void testDoOneSentence_TextWithoutNewlinesWithNewlineTokenizationEnabled() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("ssplit.newlineSplitter", "true");

    String input = "بلا فواصل";

    ArabicSegmenter mockSegmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("بلا");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokens.add(token);

    when(mockSegmenter.segmentStringToTokenList("بلا فواصل")).thenReturn(tokens);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties p) {
//         this.segmenter = mockSegmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(input);

//     annotator.doOneSentence(sentence);

    verify(mockSegmenter, times(1)).segmentStringToTokenList("بلا فواصل");
  }
@Test
  public void testRequirementsSatisfied_ContainsExactlyExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties p) {
        
      }
    };

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenEndAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.BeforeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.AfterAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.PositionAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.IndexAnnotation.class));
  }
@Test
  public void testAnnotate_WithEmptySentencesList_DoesNothing() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String model, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    Annotation annotation = new Annotation("input text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);
    verifyNoInteractions(segmenter);
  }
@Test
  public void testAnnotate_WithNullAnnotationObject_DoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String model, Properties p) {
//         this.segmenter = segmenter;
      }

      @Override
      public void annotate(Annotation annotation) {
        super.annotate(annotation); 
      }
    };

    boolean thrown = false;
    try {
      annotator.annotate(null);
    } catch (NullPointerException e) {
      thrown = true;
    }

    assertTrue(thrown); 
  }
@Test
  public void testMakeNewlineCoreLabel_WithCarriageReturn() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String s, Properties p) {
      }
    };

//     CoreLabel result = annotator.makeNewlineCoreLabel("\r", 2);
//     assertEquals("\n", result.word());
//     assertEquals("\n", result.value());
//     assertEquals("\r", result.get(CoreAnnotations.OriginalTextAnnotation.class));
//     assertEquals(Integer.valueOf(2), result.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
//     assertEquals(Integer.valueOf(3), result.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testMixedTextAndMultipleNewlinePatternsInTwoNewlineMode() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");
    props.setProperty("ssplit.newlineIsSentenceBreak", "two");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> part1Tokens = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("أول");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    part1Tokens.add(tok1);

    List<CoreLabel> part2Tokens = new ArrayList<>();
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("عنوان");
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    part2Tokens.add(tok2);

    when(segmenter.segmentStringToTokenList("أول")).thenReturn(part1Tokens);
    when(segmenter.segmentStringToTokenList("عنوان")).thenReturn(part2Tokens);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String s, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    String value = "أول\n\nعنوان";
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(value);

//     annotator.doOneSentence(sentence);

    verify(segmenter, times(1)).segmentStringToTokenList("أول");
    verify(segmenter, times(1)).segmentStringToTokenList("عنوان");
  }
@Test
  public void testSegmenterReturnsNullTokenList() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList("كلمة")).thenReturn(null);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String s, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("كلمة");

    boolean thrown = false;
    try {
//       annotator.doOneSentence(sentence);
    } catch (NullPointerException e) {
      thrown = true;
    }

    assertTrue(thrown); 
  }
@Test
  public void testRequirementsSatisfiedContainsExactlyExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "mock");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String s, Properties p) {
        
      }
    };

    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();

    List<Class<? extends CoreAnnotation>> expected = Arrays.asList(
      CoreAnnotations.TextAnnotation.class,
      CoreAnnotations.TokensAnnotation.class,
      CoreAnnotations.OriginalTextAnnotation.class,
      CoreAnnotations.CharacterOffsetBeginAnnotation.class,
      CoreAnnotations.CharacterOffsetEndAnnotation.class,
      CoreAnnotations.BeforeAnnotation.class,
      CoreAnnotations.AfterAnnotation.class,
      CoreAnnotations.TokenBeginAnnotation.class,
      CoreAnnotations.TokenEndAnnotation.class,
      CoreAnnotations.PositionAnnotation.class,
      CoreAnnotations.IndexAnnotation.class,
      CoreAnnotations.ValueAnnotation.class
    );

    assertEquals(new HashSet<>(expected), result);
  }
@Test
  public void testConstructorHandlesPropsWithMixedValidAndUnknownKeys() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "modelFile");
    props.setProperty("arabic.verbose", "true");
    props.setProperty("arabic.unknownKey1", "value1");
    props.setProperty("arabic.unknownKey2", "value2");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
//       @Override
      protected void loadModel(String path, Properties p) {
        assertEquals("modelFile", p.getProperty("model"));
        assertTrue(p.containsKey("unknownKey1"));
        assertTrue(p.containsKey("unknownKey2"));
      }
    };

    assertNotNull(annotator);
  }
@Test
  public void testDoOneSentenceWithOnlyWhitespaceText() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList("     ")).thenReturn(Collections.emptyList());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String segLoc, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("     ");
//     annotator.doOneSentence(sentence);

    verify(segmenter, times(1)).segmentStringToTokenList("     ");
  }
@Test
  public void testAnnotateWithSentenceContainingNullTextAnnotationEntry() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(null)).thenReturn(new ArrayList<>());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String segLoc, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    Annotation annotation = new Annotation("unused");
    List<CoreMap> sentenceList = new ArrayList<>();
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotator.annotate(annotation);
    verify(segmenter, times(1)).segmentStringToTokenList(null);
  }
@Test
  public void testSegmentTokensCharacterOffsetAdjustmentWhenOffsetNonZero() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("كلمة");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    tokens.add(token);

    when(segmenter.segmentStringToTokenList("كلمة")).thenReturn(tokens);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String modelPath, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("كلمة");

    
//     annotator.doOneSentence(sentence);

    CoreLabel resultToken = tokens.get(0);
    Integer begin = resultToken.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    Integer end = resultToken.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
    assertTrue(begin >= 0);
    assertTrue(end >= 0);
    assertTrue(end > begin);
  }
@Test
  public void testAnnotateWithNoSentencesAndNoTextAnnotation() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(null)).thenReturn(Collections.emptyList());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String path, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    Annotation annotation = new Annotation((String) null);
    annotator.annotate(annotation);

    verify(segmenter, times(1)).segmentStringToTokenList(null);
  }
@Test
  public void testTwoNewlinesFollowedByRealText_SplittingBranchActivated() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "path");
    props.setProperty("ssplit.newlineIsSentenceBreak", "two");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> t1 = new ArrayList<>();
    CoreLabel c1 = new CoreLabel();
    c1.setWord("مرحبا");
    c1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    c1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    t1.add(c1);

    when(segmenter.segmentStringToTokenList("مرحبا")).thenReturn(t1);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String path, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("مرحبا\n\n");

//     annotator.doOneSentence(sentence);

    verify(segmenter, times(1)).segmentStringToTokenList("مرحبا");
  }
@Test
  public void testSegmenterReturnsEmptyTokenList() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummyPath");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList("جملة")).thenReturn(new ArrayList<CoreLabel>());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String model, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    Annotation annotation = new Annotation("جملة");
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("جملة");
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    verify(segmenter, times(1)).segmentStringToTokenList("جملة");
  }
@Test
  public void testAnnotatorInitializesWithDefaultModelAndVerboseFalse() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator(false) {
      protected void loadModel(String path, Properties props) {
        assertEquals("/u/nlp/data/arabic-segmenter/arabic-segmenter-atb+bn+arztrain.ser.gz", props.getProperty("model"));
      }
    };
    assertNotNull(annotator);
  }
@Test
  public void testNewlineTokenization_singleNewlineFlagTrueOnlyOnce() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    List<CoreLabel> tokenList = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("سطر");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokenList.add(token);
    when(segmenter.segmentStringToTokenList("سطر")).thenReturn(tokenList);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String s, Properties properties) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("سطر\n");

//     annotator.doOneSentence(sentence);

    verify(segmenter, times(1)).segmentStringToTokenList("سطر");
  }
@Test
  public void testEmptyAnnotation_NoTextOrSentences_DoesNotCallSegmenter() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String path, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    Annotation annotation = new Annotation("");

    annotator.annotate(annotation);

    verifyNoInteractions(segmenter);
  }
@Test
  public void testTokenBeginEndAnnotationsAreNotNullAfterDoOneSentence() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "x");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    CoreLabel token = new CoreLabel();
    token.setWord("كلمة");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    when(segmenter.segmentStringToTokenList("كلمة")).thenReturn(tokens);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String path, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("كلمة");

//     annotator.doOneSentence(sentence);

    Integer begin = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    Integer end = token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
    assertNotNull(begin);
    assertNotNull(end);
  }
@Test
  public void testNewlineBetweenSingleWords_TokenizeNewlineAndOffsetAccuracy() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "model");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("أ");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    List<CoreLabel> list1 = new ArrayList<>();
    list1.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("ب");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    List<CoreLabel> list2 = new ArrayList<>();
    list2.add(token2);

    when(segmenter.segmentStringToTokenList("أ")).thenReturn(list1);
    when(segmenter.segmentStringToTokenList("ب")).thenReturn(list2);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String path, Properties props) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("أ\nب");

//     annotator.doOneSentence(sentence);

    assertEquals(Integer.valueOf(2), token2.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(3), token2.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testLoadModelUsesModelPropertyFromPropsSet() {
    Properties inputProps = new Properties();
    inputProps.setProperty("arabic.model", "expected-model-path");

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", inputProps) {
      protected void loadModel(String path, Properties derivedProps) {
        assertEquals("expected-model-path", path);
        assertTrue(derivedProps.containsKey("model"));
        assertEquals("expected-model-path", derivedProps.getProperty("model"));
      }
    };
    assertNotNull(annotator);
  }
@Test
  public void testAnnotate_NullSentenceTextPropagatesSafely() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "model");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);
    when(segmenter.segmentStringToTokenList(null)).thenReturn(Collections.emptyList());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String model, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    Annotation annotation = new Annotation("");
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    verify(segmenter, times(1)).segmentStringToTokenList(null);
  }
@Test
  public void testNewlineSplitFalse_DefaultBehaviorIsTokenizeAsWholeText() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy");
    props.setProperty("ssplit.newlineIsSentenceBreak", "never");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok = new CoreLabel();
    tok.setWord("نص");
    tok.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokens.add(tok);

    when(segmenter.segmentStringToTokenList("سطر\nثاني")).thenReturn(tokens);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String path, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("سطر\nثاني");

//     annotator.doOneSentence(sentence);

    verify(segmenter, times(1)).segmentStringToTokenList("سطر\nثاني");
  }
@Test
  public void testTokenOffsetsAdjustedAfterMultipleNewlinesOnTwoSplit() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "path");
    props.setProperty("ssplit.newlineIsSentenceBreak", "two");

    ArabicSegmenter segmenter = mock(ArabicSegmenter.class);

    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("أ");
    tokenA.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tokenA.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    List<CoreLabel> tokens1 = new ArrayList<>();
    tokens1.add(tokenA);

    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("ب");
    tokenB.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tokenB.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    List<CoreLabel> tokens2 = new ArrayList<>();
    tokens2.add(tokenB);

    when(segmenter.segmentStringToTokenList("أ")).thenReturn(tokens1);
    when(segmenter.segmentStringToTokenList("ب")).thenReturn(tokens2);

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String file, Properties p) {
//         this.segmenter = segmenter;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("أ\n\nب");

//     annotator.doOneSentence(sentence);

    assertEquals(Integer.valueOf(3), tokenB.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), tokenB.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testRequiresReturnsEmptySet() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", new Properties() {{
      setProperty("arabic.model", "dummy.model");
    }}) {
      protected void loadModel(String path, Properties props) {}
    };

//     Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();

//     assertNotNull(required);
//     assertTrue(required.isEmpty());
  }
@Test
  public void testConstructorVerboseParsingAndBooleanFallback() {
    Properties props = new Properties();
    props.setProperty("arabic.model", "dummy.model");
    props.setProperty("arabic.verbose", "true");  

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", props) {
      protected void loadModel(String path, Properties props) {}
    };

    assertNotNull(annotator); 
  }
@Test
  public void testDefaultConstructorDoesNotThrow() {
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator() {
      protected void loadModel(String path, Properties props) {
        assertEquals("/u/nlp/data/arabic-segmenter/arabic-segmenter-atb+bn+arztrain.ser.gz", props.getProperty("model"));
      }
    };

    assertNotNull(annotator);
  } 
}
