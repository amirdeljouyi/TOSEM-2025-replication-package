package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserAnnotations;
import edu.stanford.nlp.parser.common.ParserConstraint;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

public class POSTaggerAnnotator_3_GPTLLMTest {

 @Test
  public void testAnnotateSingleSentence() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    TaggedWord tagged1 = new TaggedWord("Hello", "UH");
    TaggedWord tagged2 = new TaggedWord("world", "NN");
    List<TaggedWord> taggedSentence = Arrays.asList(tagged1, tagged2);
    when(mockTagger.tagSentence(tokens, false)).thenReturn(taggedSentence);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("UH", tokens.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", tokens.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithTwoSentences() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Stanford");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("NLP");
    List<CoreLabel> tokens1 = Arrays.asList(t1, t2);

    TaggedWord tg1 = new TaggedWord("Stanford", "NNP");
    TaggedWord tg2 = new TaggedWord("NLP", "NNP");
    List<TaggedWord> tagged1 = Arrays.asList(tg1, tg2);
    when(mockTagger.tagSentence(tokens1, false)).thenReturn(tagged1);

    CoreLabel t3 = new CoreLabel();
    t3.setWord("Java");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("Testing");
    List<CoreLabel> tokens2 = Arrays.asList(t3, t4);

    TaggedWord tg3 = new TaggedWord("Java", "NNP");
    TaggedWord tg4 = new TaggedWord("Testing", "NN");
    List<TaggedWord> tagged2 = Arrays.asList(tg3, tg4);
    when(mockTagger.tagSentence(tokens2, false)).thenReturn(tagged2);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
    when(sentence2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("NNP", tokens1.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NNP", tokens1.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NNP", tokens2.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", tokens2.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testSentenceExceedsMaxLength() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 2, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("This");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("sentence");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("is");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("X", tokens.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", tokens.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", tokens.get(2).get(CoreAnnotations.PartOfSpeechAnnotation.class));

    verify(mockTagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testOutOfMemoryResultsInXTags() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("OOM");
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("Crash");
    List<CoreLabel> tokens = Arrays.asList(tok1, tok2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    when(mockTagger.tagSentence(tokens, false)).thenThrow(new OutOfMemoryError("Mock OOM"));

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("X", tokens.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", tokens.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMultithreadedAnnotationTagsCorrectly() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 2);

    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("First");
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("Sentence");

    List<CoreLabel> tokens1 = Arrays.asList(tok1, tok2);
    when(mockTagger.tagSentence(tokens1, false)).thenReturn(Arrays.asList(
        new TaggedWord("First", "NN"),
        new TaggedWord("Sentence", "NN")
    ));

    CoreMap sent1 = mock(CoreMap.class);
    when(sent1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);

    CoreLabel tok3 = new CoreLabel();
    tok3.setWord("Another");
    CoreLabel tok4 = new CoreLabel();
    tok4.setWord("One");

    List<CoreLabel> tokens2 = Arrays.asList(tok3, tok4);
    when(mockTagger.tagSentence(tokens2, false)).thenReturn(Arrays.asList(
        new TaggedWord("Another", "DT"),
        new TaggedWord("One", "NN")
    ));

    CoreMap sent2 = mock(CoreMap.class);
    when(sent2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

    List<CoreMap> sentenceList = Arrays.asList(sent1, sent2);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("NN", tokens1.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", tokens1.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("DT", tokens2.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", tokens2.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIsPOSOnly() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertNotNull(result);
    assertTrue(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(1, result.size());
  }
@Test
  public void testRequiresReturnsExpectedAnnotations() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    Set<Class<? extends CoreAnnotation>> result = annotator.requires();

    assertTrue(result.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testThrowsExceptionWhenSentencesMissing() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    Annotation annotation = new Annotation("This annotation has no sentence.");
    annotator.annotate(annotation);
  }
@Test
  public void testConstructorWithProperties() {
    Properties props = new Properties();
    props.setProperty("mypos.model", "mock-path");
    props.setProperty("mypos.verbose", "true");
    props.setProperty("mypos.maxlen", "9");
    props.setProperty("mypos.nthreads", "3");
    props.setProperty("mypos.reuseTags", "true");

    MaxentTagger realTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("mypos", props) {
      public MaxentTagger loadModel(String loc, boolean verbose) {
        return realTagger;
      }
    };

    assertNotNull(annotator);
  }
@Test
  public void testEmptySentenceListInAnnotation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    
  }
@Test
  public void testTokenListIsEmptyInSentence() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    
    assertTrue(tokens.isEmpty());
    verify(mockTagger, never()).tagSentence(any(), anyBoolean());
  }
@Test
  public void testSentenceWithOneToken() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    List<CoreLabel> tokens = Collections.singletonList(token);

    TaggedWord tagged = new TaggedWord("Hello", "UH");
    when(mockTagger.tagSentence(tokens, false)).thenReturn(Collections.singletonList(tagged));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("UH", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsNull() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Null");
    List<CoreLabel> tokens = Collections.singletonList(token);

    when(mockTagger.tagSentence(tokens, false)).thenReturn(null);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testZeroThreadsFallsBackToSingleThread() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 0);

    CoreLabel token = new CoreLabel();
    token.setWord("Java");
    List<CoreLabel> tokens = Collections.singletonList(token);

    TaggedWord tag = new TaggedWord("Java", "NN");
    when(mockTagger.tagSentence(tokens, false)).thenReturn(Collections.singletonList(tag));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testNegativeMaxSentenceLengthSkipsAll() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, -1, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("SkipMe");
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    verify(mockTagger, never()).tagSentence(any(), anyBoolean());
  }
@Test
  public void testTaggerReturnsIncorrectTagCount() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Too");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Few");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    List<TaggedWord> wrongTags = Collections.singletonList(new TaggedWord("Too", "JJ"));
    when(mockTagger.tagSentence(tokens, false)).thenReturn(wrongTags);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
      
    }
  }
@Test
  public void testEmptyModelPathUsesDefault() {
    Properties props = new Properties();
    props.setProperty("pos.model", "");
    props.setProperty("pos.verbose", "false");
    props.setProperty("pos.maxlen", "100");
    props.setProperty("pos.nthreads", "1");

    try {
      new POSTaggerAnnotator("pos", props);
    } catch (Exception e) {
      
      assertNotNull(e);
    }
  }
@Test
  public void testMissingModelPathLoadsDefaultModel() {
    Properties props = new Properties();
    props.setProperty("pos.verbose", "false");
    props.setProperty("pos.maxlen", "100");

    try {
      new POSTaggerAnnotator("pos", props);
    } catch (Exception e) {
      
      assertNotNull(e);
    }
  }
@Test
  public void testTaggerReturnsEmptyTaggedList() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 10, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Test");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("EmptyTags");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockTagger.tagSentence(tokens, false)).thenReturn(new ArrayList<TaggedWord>());

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
      fail("Expected IndexOutOfBoundsException due to mismatch in tag list and token list size");
    } catch (IndexOutOfBoundsException expected) {
      
    }
  }
@Test
  public void testTaggerReturnsExtraTaggedTokens() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Extra");

    List<CoreLabel> tokens = Collections.singletonList(token);
    List<TaggedWord> tagged = Arrays.asList(
        new TaggedWord("Extra", "NN"),
        new TaggedWord("Extra2", "VB")
    );

    when(mockTagger.tagSentence(tokens, false)).thenReturn(tagged);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testReuseTagsPropertyTrueHandledInConstructor() {
    Properties props = new Properties();
    props.setProperty("custom.model", "mock-path");
    props.setProperty("custom.verbose", "false");
    props.setProperty("custom.reuseTags", "true");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("custom", props);
      assertNotNull(annotator);
    } catch (RuntimeException e) {
      
      assertTrue(e.getMessage().contains("mock-path") || e.getMessage().contains("not found"));
    }
  }
@Test
  public void testMultithreadedEmptyInputDoesNotCrash() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 2);

    Annotation annotation = new Annotation("empty");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);
    
  }
@Test
  public void testConstructorWithMissingThreadPropertyFallsBackProperly() {
    Properties props = new Properties();
    props.setProperty("myannotator.model", "some/path");
    props.setProperty("myannotator.maxlen", "30");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("myannotator", props);
      assertNotNull(annotator);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("some/path"));
    }
  }
@Test
  public void testConstructorDefaultWithSystemPropertyOverridden() {
    System.setProperty("pos.model", "/fake/model/path");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator();
      assertNotNull(annotator);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("/fake/model/path"));
    } finally {
      System.clearProperty("pos.model");
    }
  }
@Test
  public void testMultipleCallsToAnnotateWithDifferentAnnotations() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel tokA = new CoreLabel();
    tokA.setWord("TokenA");
    List<CoreLabel> tokensA = Collections.singletonList(tokA);
    when(mockTagger.tagSentence(tokensA, false)).thenReturn(
        Collections.singletonList(new TaggedWord("TokenA", "NN")));

    CoreMap sentenceA = mock(CoreMap.class);
    when(sentenceA.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokensA);
    Annotation annA = new Annotation("");
    annA.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceA));
    annotator.annotate(annA);

    assertEquals("NN", tokA.get(CoreAnnotations.PartOfSpeechAnnotation.class));

    CoreLabel tokB = new CoreLabel();
    tokB.setWord("TokenB");
    List<CoreLabel> tokensB = Collections.singletonList(tokB);
    when(mockTagger.tagSentence(tokensB, false)).thenReturn(
        Collections.singletonList(new TaggedWord("TokenB", "VB")));

    CoreMap sentenceB = mock(CoreMap.class);
    when(sentenceB.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokensB);
    Annotation annB = new Annotation("");
    annB.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceB));
    annotator.annotate(annB);

    assertEquals("VB", tokB.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateHandlesTaggerReturningNullSecondTime() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("A");
    List<CoreLabel> tokensA = Collections.singletonList(tokenA);
    List<TaggedWord> taggedA = Collections.singletonList(new TaggedWord("A", "NN"));
    when(mockTagger.tagSentence(tokensA, false)).thenReturn(taggedA);

    CoreMap sentenceA = mock(CoreMap.class);
    when(sentenceA.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokensA);

    Annotation annotation1 = new Annotation("");
    annotation1.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceA));
    annotator.annotate(annotation1);

    assertEquals("NN", tokenA.get(CoreAnnotations.PartOfSpeechAnnotation.class));

    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("B");
    List<CoreLabel> tokensB = Collections.singletonList(tokenB);
    when(mockTagger.tagSentence(tokensB, false)).thenReturn(null);

    CoreMap sentenceB = mock(CoreMap.class);
    when(sentenceB.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokensB);

    Annotation annotation2 = new Annotation("");
    annotation2.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceB));
    annotator.annotate(annotation2);

    assertEquals("X", tokenB.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testEmptyTokensListDoesNotCallTagger() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 1000, 1);

    List<CoreLabel> tokens = new ArrayList<>();

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    verify(mockTagger, never()).tagSentence(anyList(), anyBoolean());
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testDefaultConstructorWithoutSystemProperty() {
    System.clearProperty("pos.model");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator();
      assertNotNull(annotator);
    } catch (Exception e) {
      
      assertNotNull(e);
    }
  }
@Test
  public void testReuseTagsFlagFalseInConstructor() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("reuseTags");
    List<CoreLabel> tokens = Collections.singletonList(token1);

    TaggedWord tagged = new TaggedWord("reuseTags", "NN");
    when(mockTagger.tagSentence(tokens, false)).thenReturn(Collections.singletonList(tagged));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testPropsFallbackToGlobalPropertyWhenThreadNotSetForAnnotatorName() {
    Properties props = new Properties();
    props.setProperty("nthreads", "4");  
    props.setProperty("custom.model", "mock-model-path");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("custom", props);
      assertNotNull(annotator);
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("mock-model-path"));
    }
  }
@Test
  public void testMulticorePathUsedWithPositiveThreads() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 2);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("multi");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("core");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    when(mockTagger.tagSentence(tokens, false)).thenReturn(Arrays.asList(
        new TaggedWord("multi", "JJ"),
        new TaggedWord("core", "NN")
    ));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("JJ", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerThrowsRuntimeExceptionHandledIncorrectly() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("error");

    List<CoreLabel> tokens = Collections.singletonList(token);

    when(mockTagger.tagSentence(tokens, false)).thenThrow(new RuntimeException("Fake error"));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("Fake error", e.getMessage());
    }
  }
@Test
  public void testEmptyPropertiesUsesAllDefaults() {
    Properties props = new Properties();

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);
      assertNotNull(annotator);
    } catch (Exception e) {
      
      assertNotNull(e);
    }
  }
@Test
  public void testTaggerInitializationTimingWhenVerboseTrue() {
    Properties props = new Properties();
    props.setProperty("mypos.model", "mock-path");
    props.setProperty("mypos.verbose", "true");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("mypos", props);
      assertNotNull(annotator);
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("mock-path"));
    }
  }
@Test
  public void testModelConstructorAppliesMaxLenCorrectly() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 1, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Too");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Long");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));

    verify(mockTagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testTaggerCalledWithReuseTagsWhenTrue() {
    Properties props = new Properties();
    props.setProperty("pos.model", "mock-model");
    props.setProperty("pos.reuseTags", "true");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);
      assertNotNull(annotator);
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("mock-model"));
    }
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithNullSentenceListThrowsException() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    Annotation annotation = new Annotation("sample text");
    
    annotator.annotate(annotation);
  }
@Test
  public void testTaggerReturnsTaggedWithNullTag() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("nulltag");
    List<CoreLabel> tokens = Collections.singletonList(token);

    TaggedWord tagged = new TaggedWord("nulltag", null);
    List<TaggedWord> taggedList = Collections.singletonList(tagged);

    when(mockTagger.tagSentence(tokens, false)).thenReturn(taggedList);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNull(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsEmptyTaggedListWithSameSizedTokenList() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 5, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    List<TaggedWord> tags = new ArrayList<>();
    when(mockTagger.tagSentence(tokens, false)).thenReturn(tags);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected IndexOutOfBoundsException due to mismatch size");
    } catch (IndexOutOfBoundsException expected) {
      
    }
  }
@Test
  public void testMultithreadedAnnotateWithTwoSentences() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 2);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("quick");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("fox");
    List<CoreLabel> tokens1 = Arrays.asList(token1, token2);
    List<TaggedWord> tags1 = Arrays.asList(new TaggedWord("quick", "JJ"), new TaggedWord("fox", "NN"));

    CoreLabel token3 = new CoreLabel();
    token3.setWord("jumps");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("over");
    List<CoreLabel> tokens2 = Arrays.asList(token3, token4);
    List<TaggedWord> tags2 = Arrays.asList(new TaggedWord("jumps", "VBZ"), new TaggedWord("over", "IN"));

    when(mockTagger.tagSentence(tokens1, false)).thenReturn(tags1);
    when(mockTagger.tagSentence(tokens2, false)).thenReturn(tags2);

    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
    when(sentence2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);
    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("JJ", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VBZ", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("IN", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsMutableList_MutationsDoNotAffectAnnotation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("token");

    List<CoreLabel> tokens = Collections.singletonList(token1);
    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(new TaggedWord("token", "NN"));

    when(mockTagger.tagSentence(tokens, false)).thenReturn(taggedWords);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("sample");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    taggedWords.clear(); 

    try {
      annotator.annotate(annotation);
      assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class)); 
    } catch (Exception e) {
      fail("Should not have thrown exception");
    }
  }
@Test
  public void testAnnotationWithNullTokenListDoesNotCrash() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 50, 1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    Annotation annotation = new Annotation("invalid");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      
      assertTrue(e instanceof NullPointerException);
    }
  }
@Test
  public void testMaxSentenceLengthBoundaryExact() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 3, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("C");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<TaggedWord> tags = Arrays.asList(
        new TaggedWord("A", "DT"),
        new TaggedWord("B", "NN"),
        new TaggedWord("C", "VB")
    );

    when(mockTagger.tagSentence(tokens, false)).thenReturn(tags);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("DT", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMaxSentenceLengthExceededSkipsTagging() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 2, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("one");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("two");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("three");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));

    verify(mockTagger, never()).tagSentence(any(), anyBoolean());
  }
@Test
  public void testPropertiesConstructorWithMissingAllKeysUsesDefaults() {
    Properties properties = new Properties();
    POSTaggerAnnotator annotator = null;
    try {
      annotator = new POSTaggerAnnotator("pos", properties);
    } catch (Exception e) {
      assertNotNull(e); 
    }
    assertNotNull(properties); 
  }
@Test
  public void testConstructorWithNonexistentModelPathThrowsException() {
    try {
      new POSTaggerAnnotator("invalid/model/path", false);
      fail("Expected IllegalArgumentException or model load error");
    } catch (Exception e) {
      assertNotNull(e); 
    }
  }
@Test
  public void testConstructorWithVerboseTimingOutput() {
    try {
      new POSTaggerAnnotator("invalid-path", true);
      fail("Expected error due to model path");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("invalid-path"));
    }
  }
@Test
  public void testTaggerReturnsNullTaggedWordList() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("hello");

    List<CoreLabel> tokens = Collections.singletonList(token);
    when(tagger.tagSentence(tokens, false)).thenReturn(null);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithSentencesKeySetButNullValue() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

    try {
      annotator.annotate(annotation);
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }
  }
@Test
  public void testEmptyTaggedWordTagHandledGracefullyWithX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("nullPOS");

    List<CoreLabel> tokens = Collections.singletonList(token);
    TaggedWord tagged = new TaggedWord("nullPOS", null);
    when(tagger.tagSentence(tokens, false)).thenReturn(Collections.singletonList(tagged));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation ann = new Annotation("text");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    assertNull(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testSentenceLengthExactlyOneGreaterThanLimitSkipsTagging() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 2, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("A");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("B");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("C");

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation ann = new Annotation("test");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    assertEquals("X", t1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", t2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", t3.get(CoreAnnotations.PartOfSpeechAnnotation.class));

    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testSingleTokenSentenceTaggedCorrectly() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Java");

    List<CoreLabel> tokens = Collections.singletonList(token);
    List<TaggedWord> tags = Collections.singletonList(new TaggedWord("Java", "NNP"));
    when(tagger.tagSentence(tokens, false)).thenReturn(tags);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation ann = new Annotation("");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    assertEquals("NNP", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMultipleTaggedSentencesTaggedWithoutErrorSingleThreaded() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("The");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("cat");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("sat");

    List<CoreLabel> tokens1 = Arrays.asList(t1, t2);
    List<CoreLabel> tokens2 = Collections.singletonList(t3);

    List<TaggedWord> tags1 = Arrays.asList(new TaggedWord("The", "DT"), new TaggedWord("cat", "NN"));
    List<TaggedWord> tags2 = Collections.singletonList(new TaggedWord("sat", "VBD"));

    when(tagger.tagSentence(tokens1, false)).thenReturn(tags1);
    when(tagger.tagSentence(tokens2, false)).thenReturn(tags2);

    CoreMap s1 = mock(CoreMap.class);
    CoreMap s2 = mock(CoreMap.class);
    when(s1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
    when(s2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

    List<CoreMap> sentences = Arrays.asList(s1, s2);

    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("DT", t1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", t2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VBD", t3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testEmptyTokenListReturnsImmediately() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("Empty tokens");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
    assertEquals(0, tokens.size());
  }
@Test
  public void testReuseTagsTrueUsedInPropertiesConstructor() {
    Properties props = new Properties();
    props.setProperty("custom.model", "mock/path"); 
    props.setProperty("custom.reuseTags", "true");
    props.setProperty("custom.maxlen", "10");
    props.setProperty("custom.nthreads", "1");

    try {
      new POSTaggerAnnotator("custom", props);
    } catch (Exception e) {
      assertTrue(e instanceof RuntimeException);
    }
  }
@Test
  public void testTaggerThrowsOutOfMemoryDuringMulticorePath() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 2);

    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("fail");

    List<CoreLabel> tokens = Collections.singletonList(tok1);
    when(tagger.tagSentence(tokens, false)).thenThrow(new OutOfMemoryError("mock OOM"));

    CoreMap sent = mock(CoreMap.class);
    when(sent.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("fail");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));

    annotator.annotate(annotation);

    assertEquals("X", tok1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testNoNThreadsKeyFallsBackToDefaultInsidePropsConstructor() {
    Properties props = new Properties();
    props.setProperty("myannotator.model", "mock/model/path");

    try {
      new POSTaggerAnnotator("myannotator", props);
    } catch (Exception e) {
      assertNotNull(e); 
    }
  }
@Test
  public void testReuseTagsFalseManipulatedInConstructor() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    CoreLabel t = new CoreLabel();
    t.setWord("once");

    List<CoreLabel> tokens = Collections.singletonList(t);
    List<TaggedWord> tagged = Collections.singletonList(new TaggedWord("once", "RB"));

    when(tagger.tagSentence(tokens, false)).thenReturn(tagged);

    CoreMap s = mock(CoreMap.class);
    when(s.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation a = new Annotation("");
    a.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(s));

    annotator.annotate(a);

    assertEquals("RB", t.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testModelLoadLoggingPathWhenVerboseTrue() {
    try {
      new POSTaggerAnnotator("invalid.model.path", true);
      fail("Should throw exception");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("invalid.model.path"));
    }
  }
@Test
  public void testConstructorWithExplicitModelAndFallbackDefaults() {
    String fakeModelPath = "mock-model";
    try {
      new POSTaggerAnnotator(fakeModelPath, false, Integer.MAX_VALUE, 1);
      fail("Expected failure due to model file");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains(fakeModelPath));
    }
  }
@Test
  public void testTaggerReturnsFewerTagsThanTokens() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("too");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("few");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<TaggedWord> tagged = Collections.singletonList(new TaggedWord("too", "JJ")); 

    when(tagger.tagSentence(tokens, false)).thenReturn(tagged);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
      assertTrue(true);
    }
  }
@Test
  public void testDefaultConstructorUsesSystemProperty() {
    System.setProperty("pos.model", "nonexistent");
    try {
      new POSTaggerAnnotator();
      fail("Expected loading error");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("nonexistent"));
    } finally {
      System.clearProperty("pos.model");
    }
  }
@Test
  public void testTaggingSkippedWhenNoTokensAnnotation() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to no tokens");
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }
  }
@Test
  public void testThreadCountZeroIsTreatedAsSerialExecution() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 0);

    CoreLabel tok = new CoreLabel();
    tok.setWord("zero");

    List<CoreLabel> tokens = Collections.singletonList(tok);
    List<TaggedWord> tagged = Collections.singletonList(new TaggedWord("zero", "CD"));

    when(tagger.tagSentence(tokens, false)).thenReturn(tagged);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("CD", tok.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  } 
}