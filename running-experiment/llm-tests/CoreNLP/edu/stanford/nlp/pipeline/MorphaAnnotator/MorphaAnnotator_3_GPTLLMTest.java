package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class MorphaAnnotator_3_GPTLLMTest {

 @Test
  public void testAnnotateWithTwoTokens() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "dogs");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "running");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Sample text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma1 = token1.get(CoreAnnotations.LemmaAnnotation.class);
    String lemma2 = token2.get(CoreAnnotations.LemmaAnnotation.class);

    assertEquals("dog", lemma1);
    assertEquals("run", lemma2);
  }
@Test
  public void testAnnotateWithEmptyPOSTag() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "jumps");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Jumping dog");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("jump", lemma);
  }
@Test
  public void testAnnotateThrowsWhenNoSentencesKey() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Annotation annotation = new Annotation("Test without sentence");

    annotator.annotate(annotation);
  }
@Test
  public void testPhrasalVerbHandlingWithParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "look_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Look it up");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("look_up", lemma);
  }
@Test
  public void testPhrasalVerbButNotParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "go_overboard");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Dummy text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("go_overboard", lemma);
  }
@Test
  public void testSentenceWithOneVerbToken() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "flies");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Birds flies");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("fly", lemma);
  }
@Test
  public void testTwoSentencesSeparateTokens() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel tokenA = new CoreLabel();
    tokenA.set(CoreAnnotations.TextAnnotation.class, "cats");
    tokenA.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreLabel tokenB = new CoreLabel();
    tokenB.set(CoreAnnotations.TextAnnotation.class, "sit");
    tokenB.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBP");

    CoreLabel tokenC = new CoreLabel();
    tokenC.set(CoreAnnotations.TextAnnotation.class, "runs");
    tokenC.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");

    CoreMap sentence1 = new ArrayCoreMap();
    List<CoreLabel> list1 = new ArrayList<>();
    list1.add(tokenA);
    list1.add(tokenB);
    sentence1.set(CoreAnnotations.TokensAnnotation.class, list1);

    CoreMap sentence2 = new ArrayCoreMap();
    List<CoreLabel> list2 = new ArrayList<>();
    list2.add(tokenC);
    sentence2.set(CoreAnnotations.TokensAnnotation.class, list2);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence1);
    sentenceList.add(sentence2);

    Annotation annotation = new Annotation("Multiple sentences");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    assertEquals("cat", tokenA.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("sit", tokenB.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("run", tokenC.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContainsOnlyLemmaAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends CoreAnnotation>> satisfied =
        annotator.requirementsSatisfied();

    assertNotNull(satisfied);
    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequiredAnnotationsIncludeExpectedKeys() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends CoreAnnotation>> required =
        annotator.requires();

    assertNotNull(required);
    assertEquals(4, required.size());
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAnnotateMalformedWordNonVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "play_time");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Some text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("play_time", lemma);
  }
@Test
  public void testEmptyTextAndNoPOSTag() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("", lemma);
  }
@Test
  public void testTokenWithMissingTextAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("missing text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertNotNull(lemma);
  }
@Test
  public void testTokenWithMissingPOSAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "jumping");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("missing pos");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("jump", lemma);  
  }
@Test
  public void testSentenceWithEmptyTokenList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("empty tokens");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
  }
@Test
  public void testPhrasalVerbWithMultipleUnderscores() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_up_stairs");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("multi-underscore");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run_up_stairs", lemma);  
  }
@Test
  public void testPhrasalVerbPOSNotVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "step_back");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("not a verb");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("step_back", lemma);
  }
@Test
  public void testPhrasalVerbValidParticleEdgeWord() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "send_over");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBP");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("with valid particle at edge");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("send_over", lemma);
  }
@Test
  public void testPhrasalVerbEdgeCaseParticleNotInList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "walk_fast");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBP");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("invalid particle");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("walk_fast", lemma);
  }
@Test
  public void testTokenWithNullTextPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation((Annotation) null);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithTextButNullPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "driven");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertNotNull(lemma);
  }
@Test
  public void testAnnotationWithMultipleSentencesAndEmptyTokenLists() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    Annotation annotation = new Annotation("Multiple empty sentences");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    
  }
@Test
  public void testTokenWithNullPOSAndText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    Annotation annotation = new Annotation("Null token fields");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testNullTokenListInSentence() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, null);

    Annotation annotation = new Annotation("null token list");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    
    annotator.annotate(annotation);
  }
@Test
  public void testAnnotationWithMultipleSentencesOnlyOneHasTokens() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "cats");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, null); 

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

    annotator.annotate(annotation);

    assertEquals("cat", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbUnderscoreButParticleNotValid() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "stand_correct");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("stand_correct", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testPhrasalVerbUppercaseParticleMatch() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "push_UP");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBP");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("case sensitivity");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertEquals("push_UP", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbSplitWithEmptyBase() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "_down");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("underscore with empty base");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("_down", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testTextWithoutTokensButSentencesExist() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Annotation annotation = new Annotation("non-empty text");

    CoreMap sentence = new ArrayCoreMap();
    

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    
    annotator.annotate(annotation);
  }
@Test
  public void testSentenceWithEmptyTextAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, ""); 
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("", lemma); 
  }
@Test
  public void testLemmaAnnotationIsAddedEvenIfTextIsNull() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation((Annotation) null);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertNotNull(lemma); 
  }
@Test
  public void testPhrasalVerbReturnsNullWhenNoUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("run without particle");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run", lemma); 
  }
@Test
  public void testEmptyAnnotationObject() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Annotation annotation = new Annotation("");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException due to missing SentencesAnnotation");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unable to find words/tokens in"));
    }
  }
@Test
  public void testPhrasalVerbWithEmptyParticleAfterUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "pull_");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("pull_", lemma); 
  }
@Test
  public void testPhrasalVerbWithOnlyUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "_");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("_ input");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("_", lemma); 
  }
@Test
  public void testTokenWithSpaceInText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run quickly");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("text with space");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("run quickly", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testNonVerbPhrasalCandidate() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "green_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("adjective phrasal");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("green_up", lemma); 
  }
@Test
  public void testSentenceWithTextAnnotationPresentOnly() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "some sentence text");

    Annotation annotation = new Annotation("content");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation); 

    
  }
@Test
  public void testTokenWithNumericText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "12345");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("number test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("12345", lemma); 
  }
@Test
  public void testTokenWithNullLemmaHandledGracefully() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "bouncing");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("bouncing ball");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("bounce", lemma);
  }
@Test
  public void testPhrasalVerbWithMultipleUnderscoresAndValidParticleAtEnd() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "go_all_out");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("multi underscore");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("go_all_out", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testPhrasalVerbWithExtraWhitespace() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_ up ");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("space in phrasal");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("run_ up ", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testWhitespaceAsTokenText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "   "); 
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("whitespace test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("   ", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testSpecialSymbolAsTextAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "@");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("symbolic term");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("@", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testBoundaryParticleRecognizedPhrasalVerb_first() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "move_abroad"); 
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("boundary test first");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("move_abroad", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testBoundaryParticleRecognizedPhrasalVerb_last() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "pick_up"); 
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("boundary test last");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("pick_up", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testInvalidPOSTagStructure() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "walk");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "!!"); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("invalid POS");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("walk", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testEmptyStringAsTextAndPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("empty string");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testSingleUnderscoreText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "_");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("underscore only");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("_", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testMixedTokensInSentence() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "running");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, null);
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

    Annotation annotation = new Annotation("mixed token states");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("run", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("", token2.get(CoreAnnotations.LemmaAnnotation.class));
    assertNotNull(token3.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testUnrecognizedMixedCasePhrasalVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Pull_Up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("mixed case phrasal");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("Pull_Up", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testPhrasalVerbPOSVariation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "pull_out");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBP"); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("POS variation VBP");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("pull_out", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testNullTextValueWithValidPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Test null text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertNotNull(lemma); 
  }
@Test
  public void testTokenWithNullPOSTagAndNonNullText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "computing");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Null POS case");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("comput", lemma); 
  }
@Test
  public void testTokenWithDoubleUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "get_out_now");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("double underscore");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("get_out_now", lemma); 
  }
@Test
  public void testPhrasalVerbWithUpperCaseParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "send_OUT");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Uppercase particle");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("send_OUT", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testSentenceWithNullTokenList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, null);

    Annotation annotation = new Annotation("Sentence with null token list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation); 
  }
@Test
  public void testMixedNullAndValidTokensInSentence() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel validToken = new CoreLabel();
    validToken.set(CoreAnnotations.TextAnnotation.class, "jumping");
    validToken.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreLabel nullPosToken = new CoreLabel();
    nullPosToken.set(CoreAnnotations.TextAnnotation.class, "flies");
    nullPosToken.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreLabel emptyToken = new CoreLabel();
    emptyToken.set(CoreAnnotations.TextAnnotation.class, null);
    emptyToken.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(validToken);
    tokens.add(nullPosToken);
    tokens.add(emptyToken);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Mixed token states");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("jump", validToken.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("fli", nullPosToken.get(CoreAnnotations.LemmaAnnotation.class)); 
    assertNotNull(emptyToken.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testNonVBPOSWithUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "over_the_top");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("underscore, non-VB POS");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("over_the_top", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testFallbackForUnhandledPOSTag() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "correctly");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "RB"); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("adverb fallback");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("correct", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testTokenTextWithEmojiCharacter() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "😊");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("emoji token");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("😊", token.get(CoreAnnotations.LemmaAnnotation.class)); 
  }
@Test
  public void testEmptySentencesList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Annotation annotation = new Annotation("empty sentence list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.<CoreMap>emptyList());

    annotator.annotate(annotation); 
  }
@Test
  public void testPhrasalVerbWithNumericParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_123");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Particle with number");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("run_123", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testInvalidTokenKeyTypeInSentence() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, (List) Arrays.asList("not a token", 123));

    Annotation annotation = new Annotation("Invalid token types");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected ClassCastException due to invalid token type");
    } catch (ClassCastException e) {
      assertTrue(e.getMessage().contains("cannot be cast"));
    }
  }
@Test
  public void testAnnotationWithNoTokensAnnotationKey() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "cats");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence = new ArrayCoreMap();

    Annotation annotation = new Annotation("missing TokensAnnotation");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    
    annotator.annotate(annotation);
  }
@Test
  public void testOnlyOnePartPOSTagInPhrasalVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "over");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Phrasal-like but invalid");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("over", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithEmptyTokenText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("Empty token text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithAlmostValidParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "carry_inside");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("Malformed particle (inside)");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("carry_inside", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testSentenceWithoutTokenKeyEntirely() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap(); 

    Annotation annotation = new Annotation("No token key");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation); 
  }
@Test
  public void testPhrasalVerbWithEmptyParticleButValidPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "turn_");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation annotation = new Annotation("Underscore with empty suffix");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("turn_", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testSentenceWithEmptyTokensArray() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    Annotation annotation = new Annotation("Sentence with empty token list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation); 
  }
@Test
  public void testCorrectlyCapitalizedKnownParticlePhrasalVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "open_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBP");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Valid phrasal verb");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("open_up", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPOSOnlySetButWordTextIsNull() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Missing text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));
  } 
}