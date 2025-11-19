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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class POSTaggerAnnotator_1_GPTLLMTest {

 @Test
  public void testDefaultConstructor() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithVerbose() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(true);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithModelLocation() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("path/to/model", false);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithAllArguments() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("path/to/model", false, 100, 2);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithMaxentTagger() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithInvalidAnnotation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);
    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateWithValidAnnotation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("This is a test.");
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("a");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("test");
    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("This", "DT");
    TaggedWord taggedWord2 = new TaggedWord("is", "VBZ");
    TaggedWord taggedWord3 = new TaggedWord("a", "DT");
    TaggedWord taggedWord4 = new TaggedWord("test", "NN");
    TaggedWord taggedWord5 = new TaggedWord(".", ".");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4, taggedWord5);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("DT", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VBZ", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("DT", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(".", token5.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotationWithMaxSentenceLengthExceeded() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger, 4, 1);

    Annotation annotation = new Annotation("A very long sentence.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("very");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("long");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("sentence");
    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token5.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateHandlesOOMGracefully() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("OOM error test.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Test1");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Test2");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    when(mockTagger.tagSentence(any(), anyBoolean())).thenThrow(new OutOfMemoryError("Simulated OOM"));

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequiresAnnotations() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithNegativeMaxSentenceLength() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("path/to/model", false, -1, 2);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithZeroThreads() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("path/to/model", false, 100, 0);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithNullAnnotation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);
    annotator.annotate(null);
  }
@Test
  public void testAnnotateWithEmptySentenceList() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Empty test case.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);

    assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateWithSentenceWithoutTokens() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("No tokens case.");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNull(sentence.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testAnnotateWithSingleTokenSentence() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Single word.");
    
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord = new TaggedWord("Hello", "UH");
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(Collections.singletonList(taggedWord));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("UH", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithLongSentenceExactlyAtMaxLimit() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Max limit test.");

    List<CoreLabel> tokens = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord("word" + i);
      tokens.add(token);
    }

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<TaggedWord> taggedWords = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      taggedWords.add(new TaggedWord("word" + i, "NN"));
    }

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    for (int i = 0; i < 100; i++) {
      assertEquals("NN", tokens.get(i).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    }
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithSentenceWithoutSentenceAnnotation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Missing sentences annotation.");
    annotation.remove(CoreAnnotations.SentencesAnnotation.class);

    annotator.annotate(annotation);
  }
@Test
  public void testConstructorWithInvalidModelPath() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("invalid/path/to/model", false);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithPropertiesWithoutModelPath() {
    Properties props = new Properties();
    props.setProperty("pos.verbose", "true");
    props.setProperty("pos.maxlen", "200");
    props.setProperty("pos.nthreads", "2");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithMultipleSentences() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);
    
    Annotation annotation = new Annotation("Sentence one. Sentence two.");
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence"); 
    CoreLabel token2 = new CoreLabel();
    token2.setWord("one."); 
    
    List<CoreLabel> tokens1 = Arrays.asList(token1, token2);
    
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Sentence"); 
    CoreLabel token4 = new CoreLabel();
    token4.setWord("two."); 

    List<CoreLabel> tokens2 = Arrays.asList(token3, token4);
    
    CoreMap sentence1 = mock(CoreMap.class);
    CoreMap sentence2 = mock(CoreMap.class);

    when(sentence1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
    when(sentence2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

    TaggedWord taggedWord1 = new TaggedWord("Sentence", "NN");
    TaggedWord taggedWord2 = new TaggedWord("one.", "JJ");
    TaggedWord taggedWord3 = new TaggedWord("Sentence", "NN");
    TaggedWord taggedWord4 = new TaggedWord("two.", "JJ");

    List<TaggedWord> taggedWords1 = Arrays.asList(taggedWord1, taggedWord2);
    List<TaggedWord> taggedWords2 = Arrays.asList(taggedWord3, taggedWord4);

    when(mockTagger.tagSentence(tokens1, false)).thenReturn(taggedWords1);
    when(mockTagger.tagSentence(tokens2, false)).thenReturn(taggedWords2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithWhitespaceOnlyTokens() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("   ");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("   ");
    
    List<CoreLabel> tokens = Collections.singletonList(token1);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithAnnotationMissingTokensKey() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Test case for missing tokens key.");
    CoreMap sentence = mock(CoreMap.class);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateWithSentenceContainingSpecialCharacters() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Hello, world! @#$%^&*()");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello"); 
    CoreLabel token2 = new CoreLabel();
    token2.setWord(","); 
    CoreLabel token3 = new CoreLabel();
    token3.setWord("world"); 
    CoreLabel token4 = new CoreLabel();
    token4.setWord("!"); 
    CoreLabel token5 = new CoreLabel();
    token5.setWord("@#$%^&*()"); 

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("Hello", "UH");
    TaggedWord taggedWord2 = new TaggedWord(",", ",");
    TaggedWord taggedWord3 = new TaggedWord("world", "NN");
    TaggedWord taggedWord4 = new TaggedWord("!", ".");
    TaggedWord taggedWord5 = new TaggedWord("@#$%^&*()", "SYM");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4, taggedWord5);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("UH", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(",", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(".", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token5.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithNumericTokens() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("123 456.78");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("123"); 
    CoreLabel token2 = new CoreLabel();
    token2.setWord("456.78"); 

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("123", "CD");
    TaggedWord taggedWord2 = new TaggedWord("456.78", "CD");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("CD", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("CD", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithEmptyModelPath() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("", false);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithNullModelPath() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(null, false);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithOneCharacterTokens() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("A B C D.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("C");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("D.");
    
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("A", "NN");
    TaggedWord taggedWord2 = new TaggedWord("B", "NN");
    TaggedWord taggedWord3 = new TaggedWord("C", "NN");
    TaggedWord taggedWord4 = new TaggedWord("D.", "NN");
    
    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithMixedCaseTokens() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Mixed LOWER and UPPER case.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Mixed");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("LOWER");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("and");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("UPPER");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("case.");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("Mixed", "JJ");
    TaggedWord taggedWord2 = new TaggedWord("LOWER", "NN");
    TaggedWord taggedWord3 = new TaggedWord("and", "CC");
    TaggedWord taggedWord4 = new TaggedWord("UPPER", "NN");
    TaggedWord taggedWord5 = new TaggedWord("case.", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4, taggedWord5);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("JJ", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("CC", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token5.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithRepeatedWords() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Test test test.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Test");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("test");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("test.");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("Test", "NN");
    TaggedWord taggedWord2 = new TaggedWord("test", "NN");
    TaggedWord taggedWord3 = new TaggedWord("test.", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithNullSentenceList() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Null sentence list test.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateWithUnicodeCharacters() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("你好，世界！😊");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("你好");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("，");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("世界");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("！");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("😊");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("你好", "NN");
    TaggedWord taggedWord2 = new TaggedWord("，", ",");
    TaggedWord taggedWord3 = new TaggedWord("世界", "NN");
    TaggedWord taggedWord4 = new TaggedWord("！", ".");
    TaggedWord taggedWord5 = new TaggedWord("😊", "SYM");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4, taggedWord5);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(",", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(".", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token5.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithLargeMaxSentenceLength() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("path/to/model", false, Integer.MAX_VALUE, 2);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithMinimumValues() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("path/to/model", false, 1, 1);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithMultipleSpacesBetweenWords() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Word    with   extra spaces.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Word");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("with");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("extra");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("spaces.");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("Word", "NN");
    TaggedWord taggedWord2 = new TaggedWord("with", "IN");
    TaggedWord taggedWord3 = new TaggedWord("extra", "JJ");
    TaggedWord taggedWord4 = new TaggedWord("spaces.", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("IN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithLongContinuousTextWithoutSpaces() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("Thisisaverylongwordwithnospaces");

    CoreLabel token = new CoreLabel();
    token.setWord("Thisisaverylongwordwithnospaces");

    List<CoreLabel> tokens = Collections.singletonList(token);
    
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord = new TaggedWord("Thisisaverylongwordwithnospaces", "NN");
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(Collections.singletonList(taggedWord));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithOnlyPunctuation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("!!??...");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("!");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("?");
    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("!", ".");
    TaggedWord taggedWord2 = new TaggedWord("?", ".");
    TaggedWord taggedWord3 = new TaggedWord(".", ".");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals(".", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(".", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(".", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithEmojiAndSymbols() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("😀 ❤️ ✔️");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("😀");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("❤️");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("✔️");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("😀", "SYM");
    TaggedWord taggedWord2 = new TaggedWord("❤️", "SYM");
    TaggedWord taggedWord3 = new TaggedWord("✔️", "SYM");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("SYM", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithUncommonCharacters() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("© 𝒜 𝔘 𝕏");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("©");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("𝒜");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("𝔘");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("𝕏");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("©", "SYM");
    TaggedWord taggedWord2 = new TaggedWord("𝒜", "NN");
    TaggedWord taggedWord3 = new TaggedWord("𝔘", "NN");
    TaggedWord taggedWord4 = new TaggedWord("𝕏", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("SYM", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithNullTaggerModel() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator((String) null, false);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithMixedAlphaNumericTokens() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("abc123 def456 ghi789");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("abc123");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("def456");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("ghi789");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("abc123", "NN");
    TaggedWord taggedWord2 = new TaggedWord("def456", "NN");
    TaggedWord taggedWord3 = new TaggedWord("ghi789", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithNonStandardWhitespace() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("word\u00A0with\u00A0non-standard\u00A0space");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("word");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("with");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("non-standard");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("space");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("word", "NN");
    TaggedWord taggedWord2 = new TaggedWord("with", "IN");
    TaggedWord taggedWord3 = new TaggedWord("non-standard", "JJ");
    TaggedWord taggedWord4 = new TaggedWord("space", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("IN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithRepeatedPunctuation() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("???!!!...");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("???");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("!!!");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("...");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("???", ".");
    TaggedWord taggedWord2 = new TaggedWord("!!!", ".");
    TaggedWord taggedWord3 = new TaggedWord("...", ".");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals(".", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(".", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(".", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithUnicodeCurrencySymbols() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("$ € ¥");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("$");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("€");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("¥");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("$", "SYM");
    TaggedWord taggedWord2 = new TaggedWord("€", "SYM");
    TaggedWord taggedWord3 = new TaggedWord("¥", "SYM");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("SYM", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithInterleavedNumbersAndText() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("The 42 quick 3.14 brown foxes");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("The");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("42");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("quick");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("3.14");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("brown");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("foxes");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("The", "DT");
    TaggedWord taggedWord2 = new TaggedWord("42", "CD");
    TaggedWord taggedWord3 = new TaggedWord("quick", "JJ");
    TaggedWord taggedWord4 = new TaggedWord("3.14", "CD");
    TaggedWord taggedWord5 = new TaggedWord("brown", "JJ");
    TaggedWord taggedWord6 = new TaggedWord("foxes", "NNS");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4, taggedWord5, taggedWord6);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("DT", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("CD", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("CD", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", token5.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NNS", token6.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithOnlyWhitespace() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("    ");

    CoreLabel token = new CoreLabel();
    token.setWord("    ");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithMixOfLettersNumbersAndSymbols() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("abc123 @home #winning!");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("abc123");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("@home");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("#winning!");
    
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("abc123", "NN");
    TaggedWord taggedWord2 = new TaggedWord("@home", "SYM");
    TaggedWord taggedWord3 = new TaggedWord("#winning!", "SYM");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithUnicodeCharactersIncludingMathSymbols() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("∑ x² + ∞ = ℝ");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("∑");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("x²");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("+");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("∞");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("=");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("ℝ");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("∑", "SYM");
    TaggedWord taggedWord2 = new TaggedWord("x²", "NN");
    TaggedWord taggedWord3 = new TaggedWord("+", "SYM");
    TaggedWord taggedWord4 = new TaggedWord("∞", "SYM");
    TaggedWord taggedWord5 = new TaggedWord("=", "SYM");
    TaggedWord taggedWord6 = new TaggedWord("ℝ", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4, taggedWord5, taggedWord6);
    
    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("SYM", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("SYM", token5.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token6.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithForeignLanguageText() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("こんにちは 世界");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("こんにちは");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("世界");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("こんにちは", "NN");
    TaggedWord taggedWord2 = new TaggedWord("世界", "NN");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateWithMalformedSentenceStructure() {
    MaxentTagger mockTagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(mockTagger);

    Annotation annotation = new Annotation("!!! broken structure 123");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("!!!");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("broken");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("structure");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("123");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    TaggedWord taggedWord1 = new TaggedWord("!!!", ".");
    TaggedWord taggedWord2 = new TaggedWord("broken", "JJ");
    TaggedWord taggedWord3 = new TaggedWord("structure", "NN");
    TaggedWord taggedWord4 = new TaggedWord("123", "CD");

    List<TaggedWord> taggedWords = Arrays.asList(taggedWord1, taggedWord2, taggedWord3, taggedWord4);

    when(mockTagger.tagSentence(any(), anyBoolean())).thenReturn(taggedWords);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals(".", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("CD", token4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  } 
}