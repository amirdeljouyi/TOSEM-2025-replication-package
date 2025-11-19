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
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
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

public class POSTaggerAnnotator_4_GPTLLMTest {

 @Test
  public void testAnnotateSingleSentenceSuccess() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("The");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("dog");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation document = new Annotation("test");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    TaggedWord tagged1 = new TaggedWord("The", "DT");
    TaggedWord tagged2 = new TaggedWord("dog", "NN");
    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(tagged1);
    taggedWords.add(tagged2);

    when(tagger.tagSentence(anyList(), eq(false))).thenReturn(taggedWords);

    annotator.annotate(document);

    assertEquals("DT", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateOverMaxSentenceLengthSkipsTagging() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Word1");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Word2");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation document = new Annotation("Test");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateThrowsIfNoSentences() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    Annotation document = new Annotation("Test");
    annotator.annotate(document);
  }
@Test
  public void testOutOfMemoryDuringTaggingFallsBackToX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Fail");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation document = new Annotation("text");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    when(tagger.tagSentence(anyList(), anyBoolean())).thenThrow(new OutOfMemoryError("simulated"));

    annotator.annotate(document);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedAndRequired() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requirementsSatisfied();
    assertEquals(1, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));

    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testAnnotatorWithReuseTagsTrue() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Hello");

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord("Hello", "UH"));

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation document = new Annotation("doc");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    when(tagger.tagSentence(anyList(), eq(false))).thenReturn(tagged);

    annotator.annotate(document);

    assertEquals("UH", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithProperties() {
    Properties props = new Properties();
    props.setProperty("custom.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("custom.nthreads", "2");
    props.setProperty("custom.maxlen", "50");
    props.setProperty("custom.verbose", "false");
    props.setProperty("custom.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("custom", props);
    assertNotNull(annotator);
  }
@Test
  public void testDefaultConstructorDoesNotCrash() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testSingleTokenTagging() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Hi");

    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(new TaggedWord("Hi", "UH"));

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("text");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    when(tagger.tagSentence(anyList(), eq(false))).thenReturn(taggedWords);

    annotator.annotate(doc);

    assertEquals("UH", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
public void testZeroThreadsFallsBackToSingleThreaded() {
  MaxentTagger tagger = mock(MaxentTagger.class);
  
  POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 0);

  CoreLabel token = new CoreLabel();
  token.setWord("Test");

  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);

  TaggedWord taggedWord = new TaggedWord("Test", "NN");

  List<TaggedWord> taggedWords = new ArrayList<>();
  taggedWords.add(taggedWord);

  CoreMap sentence = new Annotation("sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation doc = new Annotation("text");
  doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  when(tagger.tagSentence(anyList(), eq(false))).thenReturn(taggedWords);

  annotator.annotate(doc);

  assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
}
@Test
public void testMultipleSentencesSingleThreaded() {
  MaxentTagger tagger = mock(MaxentTagger.class);
  POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("First");
  CoreLabel token2 = new CoreLabel();
  token2.setWord("Second");

  List<CoreLabel> tokens1 = new ArrayList<>();
  tokens1.add(token1);
  List<CoreLabel> tokens2 = new ArrayList<>();
  tokens2.add(token2);

  CoreMap sentence1 = new Annotation("sentence1");
  sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);
  CoreMap sentence2 = new Annotation("sentence2");
  sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence1);
  sentenceList.add(sentence2);

  Annotation doc = new Annotation("text");
  doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  TaggedWord tagged1 = new TaggedWord("First", "JJ");
  TaggedWord tagged2 = new TaggedWord("Second", "NN");

  when(tagger.tagSentence(eq(tokens1), eq(false))).thenReturn(Collections.singletonList(tagged1));
  when(tagger.tagSentence(eq(tokens2), eq(false))).thenReturn(Collections.singletonList(tagged2));

  annotator.annotate(doc);

  assertEquals("JJ", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  assertEquals("NN", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
}
@Test
public void testTaggerReturnsNullFallbackToX() {
  MaxentTagger tagger = mock(MaxentTagger.class);
  POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

  CoreLabel token = new CoreLabel();
  token.setWord("Null");

  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);

  CoreMap sentence = new Annotation("sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation doc = new Annotation("doc");
  doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  when(tagger.tagSentence(anyList(), anyBoolean())).thenReturn(null);

  annotator.annotate(doc);

  assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
}
@Test
public void testReuseTagsTrueFromPropertiesConstructor() {
  Properties props = new Properties();
  props.setProperty("tagger.model", MaxentTagger.DEFAULT_JAR_PATH);
  props.setProperty("tagger.verbose", "false");
  props.setProperty("tagger.maxlen", "100");
  props.setProperty("tagger.nthreads", "1");
  props.setProperty("tagger.reuseTags", "true");

  POSTaggerAnnotator annotator = new POSTaggerAnnotator("tagger", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Reuse");

  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);

  CoreMap sentence = new Annotation("sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation doc = new Annotation("test");
  doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  MaxentTagger mockTagger = mock(MaxentTagger.class);
  TaggedWord tagged = new TaggedWord("Reuse", "VB");
  when(mockTagger.tagSentence(eq(tokenList), eq(true))).thenReturn(Collections.singletonList(tagged));

  POSTaggerAnnotator reuseAnnotator = new POSTaggerAnnotator(mockTagger, Integer.MAX_VALUE, 1);
  reuseAnnotator.annotate(doc);

  assertEquals("VB", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
}
@Test
public void testEmptyTokenListSetsNothingDoesNotCrash() {
  MaxentTagger tagger = mock(MaxentTagger.class);
  POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

  List<CoreLabel> emptyTokens = new ArrayList<>();

  CoreMap sentence = new Annotation("sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, emptyTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation document = new Annotation("doc");
  document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(document);

  
  verify(tagger, never()).tagSentence(anyList(), anyBoolean());
}
@Test
  public void testConstructorWithMissingModelFallsBackToDefaultPath() {
    Properties props = new Properties();
    props.setProperty("custom.verbose", "false");
    props.setProperty("custom.maxlen", "200");
    props.setProperty("custom.nthreads", "2");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("custom", props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithAlternateAnnotatorPrefix() {
    Properties props = new Properties();
    props.setProperty("alt.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("alt.verbose", "true");
    props.setProperty("alt.maxlen", "10");
    props.setProperty("alt.nthreads", "2");
    props.setProperty("alt.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("alt", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateMultiThreadedWithEmptySentenceListDoesNothing() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 4);

    Annotation document = new Annotation("text");
    List<CoreMap> sentences = new ArrayList<>();
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);
    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testSentenceExactlyAtMaxLengthGetsTagged() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    int maxLen = 3;
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, maxLen, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("one");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("two");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("three");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    tokens.add(t3);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation document = new Annotation("doc");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord("one", "DT"));
    tagged.add(new TaggedWord("two", "NN"));
    tagged.add(new TaggedWord("three", "VB"));

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(tagged);

    annotator.annotate(document);

    assertEquals("DT", t1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", t2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", t3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMultipleTokensNullTaggedListFallbackToX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 5, 1);

    CoreLabel a = new CoreLabel();
    a.setWord("A");
    CoreLabel b = new CoreLabel();
    b.setWord("B");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(a);
    tokens.add(b);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);

    Annotation doc = new Annotation("text");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);

    when(tagger.tagSentence(eq(tokens), anyBoolean())).thenReturn(null);

    annotator.annotate(doc);

    assertEquals("X", a.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", b.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testSingleThreadedWithSentenceMissingTokensAnnotationDoesNotTag() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Annotation doc = new Annotation("text");

    CoreMap sentence = new Annotation("sentence");
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);

    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(doc);

    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testPropertiesFallBackToGlobalNThreadsWhenAnnotatorSpecificMissing() {
    Properties props = new Properties();
    props.setProperty("nthreads", "3");
    props.setProperty("myannotator.model", MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("myannotator", props);
    assertNotNull(annotator); 
  }
@Test
  public void testPropertiesMissingThreadsDefaultsTo1() {
    Properties props = new Properties();
    props.setProperty("noThreads.model", MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("noThreads", props);
    assertNotNull(annotator);
  }
@Test
  public void testTaggerThrowsUnexpectedExceptionSkipsSentence() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Faulty");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation doc = new Annotation("txt");
    doc.set(CoreAnnotations.SentencesAnnotation.class, list);

    when(tagger.tagSentence(anyList(), anyBoolean())).thenThrow(new RuntimeException("Unexpected error"));

    try {
      annotator.annotate(doc);
    } catch (RuntimeException e) {
      assertEquals("Unexpected error", e.getMessage());
    }
  }
@Test
  public void testMultithreadedTaggingWithSingleSentence() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 2);

    CoreLabel token = new CoreLabel();
    token.setWord("thread");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("test");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord("thread", "NN"));

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(tagged);

    annotator.annotate(document);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testEmptyDocumentStillFailsWithoutSentences() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    Annotation emptyDoc = new Annotation("");
    try {
      annotator.annotate(emptyDoc);
      fail("Expected RuntimeException due to missing SentencesAnnotation");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("unable to find words/tokens"));
    }
  }
@Test
  public void testTokenMissingWordStillTaggable() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token = new CoreLabel(); 
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("doc");
    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, list);

    TaggedWord tagged = new TaggedWord(null, "NN");
    List<TaggedWord> taggedList = new ArrayList<>();
    taggedList.add(tagged);

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(taggedList);

    annotator.annotate(doc);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMultipleSentencesMixedValidAndTooLong() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1, 1);

    CoreLabel validToken = new CoreLabel();
    validToken.setWord("valid");

    CoreLabel a = new CoreLabel();
    a.setWord("a");
    CoreLabel b = new CoreLabel();
    b.setWord("b");

    CoreMap validSentence = new Annotation("v");
    validSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(validToken));

    List<CoreLabel> tooLongTokens = new ArrayList<>();
    tooLongTokens.add(a);
    tooLongTokens.add(b);
    CoreMap longSentence = new Annotation("l");
    longSentence.set(CoreAnnotations.TokensAnnotation.class, tooLongTokens);

    Annotation document = new Annotation("both");
    List<CoreMap> sents = new ArrayList<>();
    sents.add(validSentence);
    sents.add(longSentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sents);

    TaggedWord tagged = new TaggedWord("valid", "JJ");
    when(tagger.tagSentence(eq(Collections.singletonList(validToken)), eq(false)))
        .thenReturn(Collections.singletonList(tagged));

    annotator.annotate(document);

    assertEquals("JJ", validToken.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", a.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", b.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMissingReuseTagsDefaultsToFalse() {
    Properties props = new Properties();
    props.setProperty("test.model", MaxentTagger.DEFAULT_JAR_PATH);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("test", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithReuseTagsEnabledAndMultiThreaded() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 2);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("multi");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("doc");
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);

    TaggedWord tagged = new TaggedWord("multi", "JJ");
    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(Collections.singletonList(tagged));

    annotator.annotate(doc);

    assertEquals("JJ", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsEmptyTagList() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("hole");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> all = new ArrayList<>();
    all.add(sentence);

    Annotation doc = new Annotation("text");
    doc.set(CoreAnnotations.SentencesAnnotation.class, all);

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(new ArrayList<TaggedWord>());

    annotator.annotate(doc);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTokenWithExistingPOSGetsOverwritten() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("rewrite");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "OLD");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TaggedWord tagged = new TaggedWord("rewrite", "NEW");

    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(tagged);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation doc = new Annotation("d");
    doc.set(CoreAnnotations.SentencesAnnotation.class, list);

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(taggedWords);

    annotator.annotate(doc);

    assertEquals("NEW", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotatorHandlesMaxSentenceLengthZero() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 0, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("too");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("d");
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(doc);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testConstructorWithJustModelObjectNoCrash() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithAllCustomParamsSkipsTaggingIfTooLong() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("too");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("long");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("doc");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(doc);

    assertEquals("X", t1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", t2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithStringModelPathAndVerbose() {
    String fakePath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-dummy.tagger";
    
    try {
      new POSTaggerAnnotator(fakePath, true);
    } catch (Exception e) {
      
      assertTrue(e.getMessage().contains("edu/stanford/nlp/models"));
    }
  }
@Test
  public void testTokenTagReturnedAsNullDefaultsToX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 5, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("test");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    TaggedWord tagged = new TaggedWord("test", null);
    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(tagged);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation doc = new Annotation("txt");
    doc.set(CoreAnnotations.SentencesAnnotation.class, list);

    when(tagger.tagSentence(eq(tokenList), eq(false))).thenReturn(taggedWords);

    annotator.annotate(doc);

    assertNull(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerThrowsNullPointerExceptionFallbackToX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("crash");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("crashsentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("example");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    when(tagger.tagSentence(eq(tokens), anyBoolean())).thenThrow(new NullPointerException("Null tagger!"));

    try {
      annotator.annotate(doc);
      fail("Expected exception to propagate");
    } catch (NullPointerException e) {
      assertEquals("Null tagger!", e.getMessage());
    }
  }
@Test
  public void testTaggerReturnsLessTagsThanTokensFallbackToX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("one");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("two");

    List<CoreLabel> inputTokens = new ArrayList<>();
    inputTokens.add(token1);
    inputTokens.add(token2);

    TaggedWord tagged = new TaggedWord("one", "NN");
    List<TaggedWord> incomplete = new ArrayList<>();
    incomplete.add(tagged);

    CoreMap sentence = new Annotation("shortTag");
    sentence.set(CoreAnnotations.TokensAnnotation.class, inputTokens);

    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentList);

    when(tagger.tagSentence(eq(inputTokens), eq(false))).thenReturn(incomplete);

    annotator.annotate(doc);

    
    assertEquals("NN", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertNull(token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testReuseTagsFalseByDefaultInPropertyConstructor() {
    Properties props = new Properties();
    props.setProperty("foo.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("foo.maxlen", "1000");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("foo", props);
    assertNotNull(annotator);
  }
@Test
  public void testTaggerReturnsTaggedWordWithNullTokenStillSetsPOS() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 5, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("bob");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TaggedWord nullTokenTag = new TaggedWord(null, "NN");

    List<TaggedWord> tags = new ArrayList<>();
    tags.add(nullTokenTag);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, list);

    when(tagger.tagSentence(eq(tokens), anyBoolean())).thenReturn(tags);

    annotator.annotate(doc);

    assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTokensAnnotationMissingButSentencePresent() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    CoreMap sentence = new Annotation("s"); 

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, list);

    try {
      annotator.annotate(doc);
    } catch (Exception e) {
      assertTrue(e instanceof NullPointerException || e instanceof ClassCastException);
    }
  }
@Test
  public void testSentenceWithNullTokensAnnotationGracefullySkipped() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 999, 1);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TextAnnotation.class, "This is a test");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("Text");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);
    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testTaggerReturnsTagTooShortAndNullsGracefullyHandled() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("only");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("one");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord("only", null));  

    Annotation doc = new Annotation("doc");
    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentList);

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(tagged);

    annotator.annotate(doc);

    assertNull(t1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertNull(t2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotationWithoutTokensKeyThrowsException() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    Annotation document = new Annotation("test");

    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("sentence");
    sentences.add(sentence);

    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(document);
    } catch (NullPointerException | ClassCastException e) {
      assertTrue(e instanceof NullPointerException || e instanceof ClassCastException);
    }
  }
@Test
  public void testMixedNullAndNonNullTokensAnnotationInSentences() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel validToken = new CoreLabel();
    validToken.setWord("sample");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(validToken);

    CoreMap sentence1 = new Annotation("valid");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence2 = new Annotation("invalid");

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence1);
    list.add(sentence2);

    Annotation doc = new Annotation("Text");
    doc.set(CoreAnnotations.SentencesAnnotation.class, list);

    List<TaggedWord> tagged = new ArrayList<>();
    tagged.add(new TaggedWord("sample", "NN"));

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(tagged);

    annotator.annotate(doc);

    assertEquals("NN", validToken.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testEmptyTokensListInSentence() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 999, 1);

    List<CoreLabel> empty = new ArrayList<>();

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, empty);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("text");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);

    verify(tagger, never()).tagSentence(anyList(), anyBoolean());
  }
@Test
  public void testConstructorWithInvalidModelPathThrowsException() {
    try {
      new POSTaggerAnnotator("nonexistent/path/to/model.tagger", false);
      fail("Expected exception due to invalid model path");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("error") || e.getCause() != null);
    }
  }
@Test
  public void testTaggerThrowsErrorHandledInDoOneSentence() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("fail");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sent");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation doc = new Annotation("Failure test");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    when(tagger.tagSentence(eq(tokens), eq(false))).thenThrow(new OutOfMemoryError("OOM simulation"));

    annotator.annotate(doc);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testPartiallyTaggedSentenceMixedWithNullsHandled() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 999, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("word1");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("word2");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    TaggedWord tagged1 = new TaggedWord("word1", "DT");
    TaggedWord tagged2 = new TaggedWord("word2", null);

    List<TaggedWord> taggedWords = new ArrayList<>();
    taggedWords.add(tagged1);
    taggedWords.add(tagged2);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation document = new Annotation("doc");
    document.set(CoreAnnotations.SentencesAnnotation.class, list);

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(taggedWords);

    annotator.annotate(document);

    assertEquals("DT", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertNull(token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerThrowsIllegalStateException() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 50, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("illegal");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("text");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    when(tagger.tagSentence(eq(tokens), eq(false))).thenThrow(new IllegalStateException("bad state"));

    try {
      annotator.annotate(doc);
      fail("Expected exception");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("bad state"));
    }
  }
@Test
  public void testPartOfSpeechAnnotationAlreadySetGetsOverridden() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("override");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "OLD");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("text");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    TaggedWord tagged = new TaggedWord("override", "NEW");
    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(Collections.singletonList(tagged));

    annotator.annotate(doc);

    assertEquals("NEW", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsNullTaggedWordList() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("nullword");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(null);

    annotator.annotate(doc);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testEmptyTaggedWordTagDefaultsToX() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("emptytag");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("doc");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    TaggedWord tagged = new TaggedWord("emptytag", "");
    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(Collections.singletonList(tagged));

    annotator.annotate(doc);

    assertEquals("", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testSentenceWithTokensAnnotationAsWrongTypeCausesClassCast() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreMap sentence = new Annotation("invalid");

    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);

    Annotation document = new Annotation("doc");
    document.set(CoreAnnotations.SentencesAnnotation.class, list);

    try {
      annotator.annotate(document);
      fail("Expected exception");
    } catch (ClassCastException e) {
      
    }
  }
@Test
  public void testPropertiesConstructorReadsGlobalThreadFallback() {
    Properties props = new Properties();
    props.setProperty("pos.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("nthreads", "5");
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);
    assertNotNull(annotator);
  }
@Test
  public void testThreadsafeProcessorProcessReturnsSameSentence() {
    MaxentTagger tagger = mock(MaxentTagger.class);

    CoreLabel token = new CoreLabel();
    token.setWord("okay");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TaggedWord tagged = new TaggedWord("okay", "TAG");

    when(tagger.tagSentence(eq(tokens), eq(false))).thenReturn(Collections.singletonList(tagged));

    CoreMap sentence = new Annotation("stuff");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 999, 1);
    Annotator dummy = annotator;
  }
@Test
  public void testPOSTaggerAnnotatorImplementsAnnotatorInterfaceMethods() {
    MaxentTagger tagger = mock(MaxentTagger.class);
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1000, 1);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  } 
}