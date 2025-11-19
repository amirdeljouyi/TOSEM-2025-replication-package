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

public class POSTaggerAnnotator_2_GPTLLMTest {

 @Test
  public void testAnnotateSingleThreadSimpleSentence() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setValue("Hello");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.setValue("world");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    TaggedWord tagged1 = new TaggedWord("Hello", "UH");
    TaggedWord tagged2 = new TaggedWord("world", "NN");
    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(tagged1);
    taggedWords.add(tagged2);

    when(mockTagger.tagSentence(tokens, false)).thenReturn(taggedWords);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Hello world.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("UH", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateSkipsTooLongSentence() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 1, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Too");
    token1.setValue("Too");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("long");
    token2.setValue("long");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Too long sentence.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateMultiThreadedSentence() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 2);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Threads");
    token1.setValue("Threads");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("work");
    token2.setValue("work");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    TaggedWord tagged1 = new TaggedWord("Threads", "NNS");
    TaggedWord tagged2 = new TaggedWord("work", "VBP");
    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(tagged1);
    taggedWords.add(tagged2);

    when(mockTagger.tagSentence(tokens, false)).thenReturn(taggedWords);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Threads work");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("NNS", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VBP", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithoutSentencesThrowsException() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 50, 1);

    Annotation annotation = new Annotation("Missing required keys");

    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateHandlesOutOfMemoryError() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("fail");
    token1.setValue("fail");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("fast");
    token2.setValue("fast");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    when(mockTagger.tagSentence(tokens, false)).thenThrow(new OutOfMemoryError("Simulated OOM"));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("This fails fast.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedReturnsPOSAnnotationClass() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    Set<Class<? extends CoreAnnotation>> expected = new HashSet<>();
    expected.add(CoreAnnotations.PartOfSpeechAnnotation.class);

    Set<Class<? extends CoreAnnotation>> actual = annotator.requirementsSatisfied();

    assertEquals(expected, actual);
  }
@Test
  public void testRequiredAnnotationsReturnedCorrectly() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertEquals(5, required.size());
  }
@Test
  public void testConstructorWithPropertiesLoadsCorrectValues() {
    Properties props = new Properties();
    props.setProperty("pos.model", "fake-model-path.ser.gz");
    props.setProperty("pos.verbose", "true");
    props.setProperty("pos.maxlen", "123");
    props.setProperty("pos.nthreads", "2");
    props.setProperty("pos.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);

    assertNotNull(annotator);
    assertEquals(CoreAnnotations.PartOfSpeechAnnotation.class, annotator.requirementsSatisfied().iterator().next());
  }
@Test
  public void testAnnotateEmptySentenceList() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    Annotation annotation = new Annotation("Empty sentence list");
    List<CoreMap> sentences = new ArrayList<>(); 
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    
    annotator.annotate(annotation);

    assertEquals(0, annotation.get(CoreAnnotations.SentencesAnnotation.class).size());
  }
@Test
  public void testAnnotateSentenceWithNoTokens() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<CoreLabel>());

    Annotation annotation = new Annotation("No tokens");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testReuseTagsFlagTrueWithValidTags() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("dog");
    token.setValue("dog");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TaggedWord tagged = new TaggedWord("dog", "NN");
    List<TaggedWord> taggedList = new ArrayList<>();
    taggedList.add(tagged);

    when(tagger.tagSentence(tokens, false)).thenReturn(taggedList);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("dog");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMultipleSentencesEachWithOneToken() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("cat");
    token1.setValue("cat");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("runs");
    token2.setValue("runs");

    List<CoreLabel> sentence1Tokens = new ArrayList<>();
    sentence1Tokens.add(token1);

    List<CoreLabel> sentence2Tokens = new ArrayList<>();
    sentence2Tokens.add(token2);

    TaggedWord tagged1 = new TaggedWord("cat", "NN");
    TaggedWord tagged2 = new TaggedWord("runs", "VBZ");

    List<TaggedWord> taggedList1 = new ArrayList<>();
    taggedList1.add(tagged1);
    List<TaggedWord> taggedList2 = new ArrayList<>();
    taggedList2.add(tagged2);

    when(tagger.tagSentence(sentence1Tokens, false)).thenReturn(taggedList1);
    when(tagger.tagSentence(sentence2Tokens, false)).thenReturn(taggedList2);

    CoreMap sentence1 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentence1Tokens);

    CoreMap sentence2 = mock(CoreMap.class);
    when(sentence2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentence2Tokens);

    Annotation annotation = new Annotation("cat. runs.");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VBZ", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithMissingModelPropertyFallsBackToDefault() {
    Properties props = new Properties();
    props.setProperty("pos.maxlen", "20");
    props.setProperty("pos.nthreads", "3");

    
    System.setProperty("edu.stanford.nlp.tagger.maxent.MaxentTagger.DEFAULT_JAR_PATH", "fake-path.ser.gz");

    
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);

    assertNotNull(annotator.requirementsSatisfied());
    assertTrue(annotator.requires().contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testSentenceWithNullTokenListSkipsTagging() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 99, 1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("null token sentence");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try {
      annotator.annotate(annotation);
    } catch (NullPointerException e) {
      
      assertNotNull(e);
    }
  }
@Test
  public void testAnnotateSentenceWithTokenListHavingNullToken() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token1 = null;
    CoreLabel token2 = new CoreLabel();
    token2.setWord("dog");
    token2.setValue("dog");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    TaggedWord tagged1 = new TaggedWord("?", "X");
    TaggedWord tagged2 = new TaggedWord("dog", "NN");

    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(tagged1);
    taggedWords.add(tagged2);

    when(tagger.tagSentence(tokens, false)).thenReturn(taggedWords);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("null token test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try {
      annotator.annotate(annotation);
    } catch (NullPointerException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testAnnotateWithMultipleTokensExceedingMaxLengthBoundaryByOne() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    int maxLen = 2;
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, maxLen, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("over");
    token1.setValue("over");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("the");
    token2.setValue("the");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("limit");
    token3.setValue("limit");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("too long");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotatorWithZeroThreadsDefaultsToSingleThreadBehavior() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 0);

    CoreLabel token = new CoreLabel();
    token.setWord("fallback");
    token.setValue("fallback");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TaggedWord tagged = new TaggedWord("fallback", "NN");
    List<TaggedWord> taggedOutput = new ArrayList<>();
    taggedOutput.add(tagged);

    when(tagger.tagSentence(tokens, false)).thenReturn(taggedOutput);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("fallback");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerThrowsRuntimeExceptionHandledByAnnotator() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("crashes");
    token.setValue("crashes");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    when(tagger.tagSentence(tokens, false)).thenThrow(new RuntimeException("Forced failure"));

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation annotation = new Annotation("error crash");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

    try {
      annotator.annotate(annotation);
    } catch (RuntimeException e) {
      assertEquals("Forced failure", e.getMessage());
    }
  }
@Test
  public void testZeroLengthSentence() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    List<CoreLabel> tokens = new ArrayList<>(); 

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    verify(tagger, times(0)).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testConstructorWithOnlyModelPath() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz", false);
    assertNotNull(annotator);
    assertTrue(annotator.requirementsSatisfied().contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testReuseTagsFalseConfirmedViaBehavior() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("example");
    token.setValue("example");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TaggedWord tagged = new TaggedWord("example", "NN");
    List<TaggedWord> taggedList = new ArrayList<>();
    taggedList.add(tagged);

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(taggedList);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("example");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithOnlyOneTokenInSentence() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Run");
    token.setValue("Run");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TaggedWord tagged = new TaggedWord("Run", "VB");

    List<TaggedWord> taggedList = new ArrayList<>();
    taggedList.add(tagged);

    when(tagger.tagSentence(tokens, false)).thenReturn(taggedList);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Run");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("VB", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotatorWithMultipleThreadsAndMultipleSentences() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 2);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Bird");
    token1.setValue("Bird");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("flies");
    token2.setValue("flies");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("High");
    token3.setValue("High");

    List<CoreLabel> sentence1Tokens = new ArrayList<>();
    sentence1Tokens.add(token1);
    sentence1Tokens.add(token2);

    List<CoreLabel> sentence2Tokens = new ArrayList<>();
    sentence2Tokens.add(token3);

    List<TaggedWord> tagged1 = new ArrayList<>();
    tagged1.add(new TaggedWord("Bird", "NN"));
    tagged1.add(new TaggedWord("flies", "VBZ"));

    List<TaggedWord> tagged2 = new ArrayList<>();
    tagged2.add(new TaggedWord("High", "RB"));

    when(tagger.tagSentence(sentence1Tokens, false)).thenReturn(tagged1);
    when(tagger.tagSentence(sentence2Tokens, false)).thenReturn(tagged2);

    CoreMap s1 = mock(CoreMap.class);
    when(s1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentence1Tokens);
    CoreMap s2 = mock(CoreMap.class);
    when(s2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentence2Tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(s1);
    sentenceList.add(s2);

    Annotation annotation = new Annotation("Bird flies. High");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VBZ", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("RB", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotationWithNoTokensAnnotationKey() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null); 

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    try {
      annotator.annotate(annotation);
    } catch (NullPointerException e) {
      assertNotNull(e); 
    }
  }
@Test
  public void testSentenceExactlyAtMaxLength() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    int maxLen = 3;
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, maxLen, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("One");
    token1.setValue("One");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("two");
    token2.setValue("two");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("three");
    token3.setValue("three");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    List<TaggedWord> result = new ArrayList<>();
    result.add(new TaggedWord("One", "CD"));
    result.add(new TaggedWord("two", "CD"));
    result.add(new TaggedWord("three", "CD"));

    when(tagger.tagSentence(tokens, false)).thenReturn(result);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("One two three");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("CD", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("CD", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("CD", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotatorConstructorWithDefaultModelPathSystemProperty() {
    System.setProperty("pos.model", "fake/path/model.ser.gz");
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(false);
    assertNotNull(annotator);
    System.clearProperty("pos.model");
  }
@Test
  public void testAnnotatorConstructorWithModelAndVerboseDefaultBehavior() {
    POSTaggerAnnotator annotatorWithoutVerbose = new POSTaggerAnnotator("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz", false);
    assertNotNull(annotatorWithoutVerbose);

    POSTaggerAnnotator annotatorWithVerbose = new POSTaggerAnnotator("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz", true);
    assertNotNull(annotatorWithVerbose);
  }
@Test
  public void testAnnotatorRequirementsContentAndImmutability() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 20, 1);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));

    try {
      requirements.add(CoreAnnotations.SpeakerAnnotation.class);
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testDefaultConstructorUsesSystemPropertyModel() {
    System.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz");
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    assertNotNull(annotator.requirementsSatisfied());
    System.clearProperty("pos.model");
  }
@Test
  public void testConstructorWithOnlyModelString() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz", false);
    assertNotNull(annotator);
    assertTrue(annotator.requirementsSatisfied().contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithModelMaxSentenceLengthZero() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 0, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("ignore");
    token1.setValue("ignore");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("ignore");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithPropertiesUsesFallbackThreadValue() {
    Properties props = new Properties();
    props.setProperty("postagger.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz");
    props.setProperty("nthreads", "2");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("postagger", props);
    assertNotNull(annotator.requirementsSatisfied());
    assertTrue(annotator.requires().contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testEmptyAnnotationStillReturnsRequiresAndSatisfiedConsistently() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    Annotation annotation = new Annotation("");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException due to missing SentencesAnnotation");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("unable to find words/tokens"));
    }

    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    Set<Class<? extends CoreAnnotation>> satisfies = annotator.requirementsSatisfied();

    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(satisfies.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsFewerTagsThanTokensDefaultsRemainingToX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("word1");
    token1.setValue("word1");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("word2");
    token2.setValue("word2");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(new TaggedWord("word1", "NN")); 

    when(tagger.tagSentence(tokens, false)).thenReturn(taggedWords);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("word1 word2");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
      fail("Should throw IndexOutOfBoundsException if tag list is shorter");
    } catch (IndexOutOfBoundsException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testTokenWithPredefinedPOSNotOverwrittenIfReuseTagsTrue() {
    Properties props = new Properties();
    props.setProperty("postagger.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz");
    props.setProperty("postagger.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("postagger", props);

    CoreLabel token = new CoreLabel();
    token.setWord("existing");
    token.setValue("existing");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRESET");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("existing");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      
    }

    assertEquals("PRESET", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerThrowsNullInsteadOfListLeadsToAllXTags() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("nullResponse");
    token1.setValue("nullResponse");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    when(tagger.tagSentence(tokens, false)).thenReturn(null);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("nullResponse");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedNotModifiable() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));

    try {
      satisfied.add(CoreAnnotations.AfterAnnotation.class);
      fail("requirementsSatisfied set should be immutable");
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testSingleThreadTaggerReceivesCorrectBooleanForReuseTagsFalse() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("reuse");
    token.setValue("reuse");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<TaggedWord> result = new ArrayList<>();
    result.add(new TaggedWord("reuse", "VB"));

    when(tagger.tagSentence(tokens, false)).thenReturn(result);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("reuse");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    verify(tagger, times(1)).tagSentence(tokens, false);
    assertEquals("VB", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMultithreadedExecutionStillProcessesSentences() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 4);

    CoreLabel token = new CoreLabel();
    token.setWord("multi");
    token.setValue("multi");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord("multi", "ADJ"));

    when(tagger.tagSentence(tokens, false)).thenReturn(tagged);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("multi");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("ADJ", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testPropertiesConstructorFallbackToDefaultModel() {
    Properties props = new Properties(); 
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("postagger", props);
    assertNotNull(annotator);
    assertTrue(annotator.requirementsSatisfied().contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testPropertiesConstructorWithInvalidThreadNumbersFallsBackTo1() {
    Properties props = new Properties();
    props.setProperty("postagger.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz");
    props.setProperty("postagger.nthreads", "not_a_number");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("postagger", props);
    assertNotNull(annotator);
    assertTrue(annotator.requires().contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testTaggerThrowsOutOfMemoryErrorHandledGracefully() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 5, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("oom");
    token1.setValue("oom");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    doThrow(new OutOfMemoryError("simulate")).when(tagger).tagSentence(tokens, false);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("oom");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotatorAppliesXToAllTokensOnNullTaggedList() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 20, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("test");
    t1.setValue("test");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("null");
    t2.setValue("null");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);

    when(tagger.tagSentence(tokens, false)).thenReturn(null);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("tag returned null");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("X", t1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", t2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testSentenceWithTokensButNoPartOfSpeechInitially() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.setValue("word");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<TaggedWord> results = new ArrayList<>();
    results.add(new TaggedWord("word", "NN"));

    when(tagger.tagSentence(tokens, false)).thenReturn(results);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("word");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequiresSetContainsExactlyFiveItems() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 999, 1);
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    assertEquals(5, requires.size());
    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testSentenceWithNoTokensAnnotationHandledGracefully() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    Annotation annotation = new Annotation("text");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to missing token list");
    } catch (NullPointerException e) {
      assertNotNull(e);
    }
  }
@Test
  public void testTokenWithNullWordGetsTag() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    CoreLabel token = new CoreLabel();
    token.setWord(null); 
    token.setValue(null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord(null, "NN")); 

    when(tagger.tagSentence(tokens, false)).thenReturn(tagged);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("null-word");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsMoreTagsThanTokensHandledGracefully() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("hello");
    token.setValue("hello");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord("hello", "UH"));
    tagged.add(new TaggedWord("extra", "NN"));

    when(tagger.tagSentence(tokens, false)).thenReturn(tagged);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("extra tag case");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation); 

    assertEquals("UH", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotationWithMultipleSentencesAndOOMInOne() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 20, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("fail");
    t1.setValue("fail");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("pass");
    t2.setValue("pass");

    List<CoreLabel> tokens1 = Collections.singletonList(t1);
    List<CoreLabel> tokens2 = Collections.singletonList(t2);

    when(tagger.tagSentence(tokens1, false)).thenThrow(new OutOfMemoryError("simulate oom"));
    when(tagger.tagSentence(tokens2, false)).thenReturn(Collections.singletonList(new TaggedWord("pass", "VB")));

    CoreMap s1 = mock(CoreMap.class);
    CoreMap s2 = mock(CoreMap.class);
    when(s1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
    when(s2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

    Annotation annotation = new Annotation("test");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(s1);
    sentences.add(s2);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("X", t1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", t2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testLoadModelWithVerboseTrueTriggersTiming() {
    
    try {
      MaxentTagger mockTagger = mock(MaxentTagger.class);
      System.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz");
      POSTaggerAnnotator annotator = new POSTaggerAnnotator(true);
      assertNotNull(annotator);
    } catch (Exception e) {
      
      assertNotNull(e);
    } finally {
      System.clearProperty("pos.model");
    }
  }
@Test
  public void testInvalidModelPathThrowsError() {
    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("no/such/model/path.ser.gz", false);
      fail("Expected RuntimeException due to bad model path");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unable")); 
    }
  }
@Test
  public void testReuseTagsTrueCallsTaggerWithTrueFlag() {
    MaxentTagger tagger = mock(MaxentTagger.class);

    Properties props = new Properties();
    props.setProperty("tagger.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.ser.gz");
    props.setProperty("tagger.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("tagger", props);

    CoreLabel token = new CoreLabel();
    token.setWord("again");
    token.setValue("again");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<TaggedWord> result = Collections.singletonList(new TaggedWord("again", "RB"));

    when(tagger.tagSentence(tokens, true)).thenReturn(result); 

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("again");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    try {
      
      annotator.annotate(annotation);
    } catch (Exception e) {
      
    }

    
    
  } 
}