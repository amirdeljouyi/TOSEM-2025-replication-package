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

public class MorphaAnnotator_4_GPTLLMTest {

 @Test
  public void testAnnotateSimpleVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("run", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateNoun() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "cars");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("car", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotatePhrasalVerb() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "pick_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("pick_up", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "unknown");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("unknown", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithoutSentencesThrowsException() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    annotator.annotate(annotation); 
  }
@Test
  public void testAnnotateMultipleTokens() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "pick_up");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "apples");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "was");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "delicious");
    token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("pick_up", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("apple", token2.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("be", token3.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("delicious", token4.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequiresAnnotations() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(4, required.size());
  }
@Test
  public void testSatisfiedAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotatePhrasalVerbWithInvalidParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "pick_xyz");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("pick_xyz", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testHandlesNullPOSTagGracefully() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "walking");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not throw exception if POS tag is null");
    }
  }
@Test
  public void testAnnotateWithPunctuation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, ",");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals(",", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotateTokenWithNullText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      
      
      assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));
    } catch (Exception e) {
      
      assertTrue(e instanceof NullPointerException || e instanceof IllegalArgumentException);
    }
  }
@Test
  public void testEmptyTokenListInSentence() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    
    annotator.annotate(annotation);
  }
@Test
  public void testSentenceWithoutTokenAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreMap sentence = new ArrayCoreMap(); 

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      
      assertTrue(e instanceof NullPointerException);
    }
  }
@Test
  public void testMultipleSentences() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "dogs");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "ran");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

    annotator.annotate(annotation);

    assertEquals("dog", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("run", token2.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithLowercasePOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "flies");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "nns"); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithMidUnderscoreAndWrongPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "fall_down"); 
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertEquals("fall_down", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithMoreThanOneUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "look_up_here"); 
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("look_up_here", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testEmptyAnnotationObject() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation(""); 

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException for missing sentence info");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unable to find words"));
    }
  }
@Test
  public void testPOSWithoutCorrespondingLemmatization() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "zqxzq");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "ZZZ"); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithNullPOSTagAndValidWord() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "skipping");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    try {
      annotator.annotate(annotation);
      assertEquals("skipping", token.get(CoreAnnotations.LemmaAnnotation.class));
    } catch (Exception e) {
      fail("Should handle null POS without throwing exception");
    }
  }
@Test
  public void testTokenWithEmptyPOSTagAndNullWord() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    try {
      annotator.annotate(annotation);
      assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));
    } catch (Exception e) {
      assertTrue(e instanceof NullPointerException);
    }
  }
@Test
  public void testEmptyStringWordAndPOSTag() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotationWithNullTokenList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, null); 

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to null token list");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testPOSVerbButNoUnderscoreInWord() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "look");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("look", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testVerbWithUnderscoreButNotAtSeparator() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "fall_down_again");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("fall_down_again", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAnnotationWithEmptySentenceList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    
    annotator.annotate(annotation);
  }
@Test
  public void testUnderscoreVerbWithNonParticleAsSuffix() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "write_book");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("write_book", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testMinimalValidAnnotationStructure() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Minimal");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "plays");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("play", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithSingleLetterParticle_NotRecognized() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "look_z");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("look_z", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithNonVBTag_Ignored() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "get_back");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");  

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("get_back", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbSplitMissingParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "_");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("_", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithWhitespacePOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "   ");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("running", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithWhitespaceText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "   ");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("   ", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testNullTokenObjectInList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(null);  

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to null token in list");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testPOSCaseSensitivityForVB() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "try_out");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "vb"); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("try_out", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testMalformedAnnotationWithNoSentencesKeyAtAll() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("input with no sentences");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException for missing SentencesAnnotation");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unable to find words"));
    }
  }
@Test
  public void testTwoWordPhrasalVerbWithValidParticleOnly() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("");

    
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "break_down");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertEquals("break_down", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithNullTextAndValidPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Null word");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to null text");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testAnnotateTokenWithPOSNullTextNull() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Null POS and text");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to null text and null POS");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testPhrasalVerbVBTagUpperCaseBaseForm() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("break_up test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Break_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("break_up", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenPOSNotStartingWithVBWordContainsUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("non VB underscore");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "eat_down");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN"); 

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("eat_down", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testUnexpectedSentenceAnnotationTypeInMap() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Invalid sentence annotation value");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, (List<CoreMap>) (Object) Arrays.asList("not a sentence object"));

    try {
      annotator.annotate(annotation);
      fail("Should throw ClassCastException or similar");
    } catch (ClassCastException e) {
      
    }
  }
@Test
  public void testMixedValidAndInvalidTokens() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Mixed input");

    CoreLabel nullToken = null;

    CoreLabel validToken = new CoreLabel();
    validToken.set(CoreAnnotations.TextAnnotation.class, "jumped");
    validToken.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(validToken);
    tokens.add(nullToken);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Should fail due to null token in list");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testEmptyCoreLabel_WithNoAnnotationsSet() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Empty CoreLabel");

    CoreLabel token = new CoreLabel(); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      assertEquals(null, token.get(CoreAnnotations.LemmaAnnotation.class));
    } catch (Exception e) {
      fail("Should not throw; empty token should result in null lemma");
    }
  }
@Test
  public void testUnderscoreVerbWrongLengthSplit() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("under_over_again test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "under_over_again");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("under_over_again", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithValidPOSAndEmptyText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Empty text");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testAddLemmaWithOnlyStemFallback() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Stem fallback");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "unprocessedterm");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ""); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String result = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertNotNull(result);
    assertTrue(result.length() > 0);
  }
@Test
  public void testUnderscoreTokenWithInvalidSplitLengthMoreThanTwoParts() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("More than two parts");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_out_fast");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("run_out_fast", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testUnderscoreTokenWithLeadingUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Leading underscore");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "_down");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("_down", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testUnderscoreTokenWithTrailingUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Trailing underscore");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "backup_");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("backup_", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbInvalidParticleNotInList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Invalid particle");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "hold_overx");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("hold_overx", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testEmptyTokensListIsHandledGracefully() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Empty token list");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    
    annotator.annotate(annotation);
  }
@Test
  public void testTokenTextWithDigitOnlyContent() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Numeric token");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "2023");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD"); 

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = Collections.singletonList(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("2023", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithSymbolPOSAndSymbolText() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Symbolic text");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "-");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ":");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = Collections.singletonList(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("-", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testSentenceWithNoTokensAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Missing TokensAnnotation");

    CoreMap sentence = new ArrayCoreMap(); 

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Should throw NullPointerException");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testTokenPOSWhitespaceOnly() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Whitespace POS");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "skip");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "    "); 

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = Collections.singletonList(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("skip", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTokenWithNullPartOfSpeechValue() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Token with null POS");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "walking");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      assertEquals(null, token.get(CoreAnnotations.LemmaAnnotation.class)); 
    } catch (Exception e) {
      fail("Should handle null POS without throwing exception");
    }
  }
@Test
  public void testAnnotationWithSentenceButNoKeysOnSentence() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("sentence present but no token list");

    CoreMap sentence = new ArrayCoreMap(); 

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to missing token list");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testPhrasalVerbWithParticleInUppercaseShouldNotMatchParticleList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("uppercase particle should not match");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "come_BACK");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertEquals("come_BACK", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testPhrasalVerbWithValidParticleShouldLowercaseToMatchList() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("lower-case normalization");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_UP");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertEquals("run_up", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testVeryLongWordWithVerbPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("long token test");

    String longWord = "internationalizationizationizationization_up"; 
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, longWord);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals(longWord, token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testWhitespaceOnlyTextAndValidPOS() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("whitespace word");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "   ");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertEquals("   ", token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testSentenceWithMixedCorrectAndNullToken() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("mixed token list");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "runs");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");

    CoreLabel token2 = null; 

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to null CoreLabel in token list");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testNonStandardPOSStillLemmatizes() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("unknown POS");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "booksq!");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "XYZ");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testSentenceWithEmptyCoreMapSubclassWithoutOverride() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Annotation annotation = new Annotation("Empty subclass CoreMap");

    CoreMap sentence = new ArrayCoreMap(); 

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    assertEquals("run", token.get(CoreAnnotations.LemmaAnnotation.class));
  } 
}