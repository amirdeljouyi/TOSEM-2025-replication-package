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

public class MorphaAnnotator_2_GPTLLMTest {

 @Test
  public void testAnnotateWithSingleToken() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "playing");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Test sentence");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("play", lemma);
  }
@Test
  public void testAnnotateWithMultipleTokensAcrossSentences() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "dancing");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "feet");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));

    Annotation annotation = new Annotation("Test multi-sentence");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

    annotator.annotate(annotation);

    String lemma1 = token1.get(CoreAnnotations.LemmaAnnotation.class);
    String lemma2 = token2.get(CoreAnnotations.LemmaAnnotation.class);

    assertEquals("dance", lemma1);
    assertEquals("foot", lemma2);
  }
@Test
  public void testAnnotateWithEmptyPosTagFallsBackToStem() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "questioning");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Fallback to stem");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("question", lemma);
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateThrowsWhenSentencesAnnotationMissing() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Annotation annotation = new Annotation("Missing sentence key");

    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateHandlesEmptyTokenListInSentence() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    Annotation annotation = new Annotation("Empty token list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    
    assertTrue(true);
  }
@Test
  public void testRequirementsDeclaredCorrectly() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals(4, required.size());
  }
@Test
  public void testRequirementsSatisfiedCorrectly() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
    assertEquals(1, satisfied.size());
  }
@Test
  public void testAnnotatePhrasalVerbWithKnownParticle() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "come_back");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Phrasal verb test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("come_back", lemma);
  }
@Test
  public void testAnnotateWithInvalidPhrasalVerbFormat() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "look_forward_soon");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Invalid phrasal verb");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("look_forward_soon", lemma); 
  }
@Test
  public void testAnnotateWithNonPhrasalVerbUnderscoreWord() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "run_fast");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Non-phrasal with underscore");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run_fast", lemma);
  }
@Test
  public void testAnnotateWithNounUnderscoreIgnoredAsPhrasal() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "high_school");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Underscore noun");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("high_school", lemma);
  }
@Test
  public void testAnnotateWithEmptyTextField() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Empty word");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("", lemma);
  }
@Test
public void testTokenWithoutTextAnnotation() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Missing TextAnnotation");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertNull(lemma); 
}
@Test
public void testTokenWithoutPOSTagAnnotation() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "flying");
  

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("No POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("fly", lemma); 
}
@Test
public void testPhrasalVerbWithInvalidParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "pull_random");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Invalid particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("pull", lemma); 
}
@Test
public void testPhrasalVerbWithNoParticleSection() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "jump_"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Trailing underscore");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("jump_", lemma); 
}
@Test
public void testPhrasalVerbWithMultipleUnderscores() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "get_up_fast");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Multiple underscores");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("get_up_fast", lemma); 
}
@Test
public void testDifferentPOSPrefixesIgnoredByPhrasalVerbLogic() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run_away");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN"); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Ignore non-VB pos");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run_away", lemma); 
}
@Test
public void testTokenWithNullPOSAndNullText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, null);
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Null text and POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertNull(lemma); 
}
@Test
public void testAnnotateMultipleTokensMixedValidInvalid() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel validToken = new CoreLabel();
  validToken.set(CoreAnnotations.TextAnnotation.class, "talking");
  validToken.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

  CoreLabel noPOS = new CoreLabel();
  noPOS.set(CoreAnnotations.TextAnnotation.class, "rocks");
  noPOS.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

  CoreLabel badText = new CoreLabel();
  badText.set(CoreAnnotations.TextAnnotation.class, null);
  badText.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(validToken, noPOS, badText));

  Annotation annotation = new Annotation("Mixed token list");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  assertEquals("talk", validToken.get(CoreAnnotations.LemmaAnnotation.class));
  assertEquals("rock", noPOS.get(CoreAnnotations.LemmaAnnotation.class));
  assertNull(badText.get(CoreAnnotations.LemmaAnnotation.class));
}
@Test
public void testEmptyAnnotationObjectWithNoKeys() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  Annotation annotation = new Annotation(""); 

  try {
    annotator.annotate(annotation);
    fail("Expected RuntimeException due to missing SentencesAnnotation");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Unable to find words/tokens"));
  }
}
@Test
public void testSentenceWithNullTokenList() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, null); 

  Annotation annotation = new Annotation("Null token list scenario");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.annotate(annotation);
    
    assertTrue(true); 
  } catch (Exception e) {
    
    assertTrue(e instanceof NullPointerException);
  }
}
@Test
public void testTokenWithOnlyWhitespaceText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "   "); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Whitespace token");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("", lemma); 
}
@Test
public void testOnlyParticlesAsTokenText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "down");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "RB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Particle word only");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("down", lemma); 
}
@Test
public void testPhrasalVerbWithEmptyParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run_");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Trailing underscore no particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run_", lemma); 
}
@Test
public void testSentencesAnnotationIsEmptyList() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  Annotation annotation = new Annotation("Empty SentencesAnnotation");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation); 

  assertTrue(true); 
}
@Test
public void testSentenceWithTokenMissingBothTextAndPOS() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel(); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Token missing data");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertNull(lemma); 
}
@Test
public void testVerbWithParticleThatIsPrefixOfValidOne() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "put_ar"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Bad particle prefix");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("put_ar", lemma); 
}
@Test
public void testPhrasalVerbVerbParticleLowercaseMismatch() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Come_Back"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Mixed case particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("come_back", lemma); 
}
@Test
public void testPhrasalVerbTagWithoutVBPrefix() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "take_out");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ"); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Non-VB tag with phrasal shape");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("take_out", lemma); 
}
@Test
public void testPhrasalVerbWithExactTwoPartStructureButNonParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "walk_tree"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Two-part underscore with invalid particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("walk", lemma); 
}
@Test
public void testPhrasalVerbMalformedWithThreeComponentsUnderscores() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "jump_up_down");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Malformed phrasal verb 3 parts");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("jump_up_down", lemma); 
}
@Test
public void testOnlyTextAnnotationSet_NoPOSAnnotation() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "covered"); 
  

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Only text no POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("cover", lemma); 
}
@Test
public void testOnlyPOSTagSet_NoTextAnnotation() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Only POS no text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertNull(lemma); 
}
@Test
public void testVerbWithUnderscoreButParticleInMiddle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "get_out_now");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Triple part with particle in middle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("get_out_now", lemma); 
}
@Test
public void testTokenWithUnderscoreAndEmptyPOS() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run_back");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ""); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Phrasal form with empty POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run", lemma); 
}
@Test
public void testEmptyCoreLabelObject() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel(); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Fully empty CoreLabel");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
}
@Test
public void testPhrasalVerbWithUppercaseParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "break_DOWN");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Uppercase particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("break_down", lemma);
}
@Test
public void testTokenWithNullTokenListEntry() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), null, new CoreLabel());

  tokens.get(0).set(CoreAnnotations.TextAnnotation.class, "cats");
  tokens.get(0).set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

  tokens.get(2).set(CoreAnnotations.TextAnnotation.class, "ran");
  tokens.get(2).set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("Null token inside list");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.annotate(annotation);
    assertEquals("cat", tokens.get(0).get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("run", tokens.get(2).get(CoreAnnotations.LemmaAnnotation.class));
  } catch (Exception e) {
    fail("Annotator should not throw exception with null token in list");
  }
}
@Test
public void testSentenceWithoutTokensAnnotation() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreMap sentence = new ArrayCoreMap(); 

  Annotation annotation = new Annotation("Missing TokensAnnotation");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.annotate(annotation);
    assertTrue(true); 
  } catch (Exception e) {
    fail("Should not throw exception when TokensAnnotation missing");
  }
}
@Test
public void testTokenWithNullTextAndNonEmptyTag() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, null);
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Null text with valid POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.annotate(annotation);
    assertNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
  } catch (Exception e) {
    fail("Should not throw exception when token text is null");
  }
}
@Test
public void testTokenWithNullTextAndEmptyTag() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, null);
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ""); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Null text and empty tag");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.annotate(annotation);
    assertNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
  } catch (Exception e) {
    fail("Should not throw exception with both text and tag as null/empty");
  }
}
@Test
public void testPhrasalVerbTagCaseSensitivity() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "fill_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "vb"); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Lowercase VB tag");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("fill", lemma);
}
@Test
public void testWhitespaceOnlyParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run_   ");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Whitespace particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run", lemma);
}
@Test
public void testEmptyParticleAsPhrasalVerb() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run_");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Empty particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run", lemma);
}
@Test
public void testPhrasalVerbWithWhitespaceInBaseVerb() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run fast_down");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Whitespace in base verb");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run fast_down", lemma);
}
@Test
public void testParticleCaseSensitivity_MixedCaseFails() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "pick_Up"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Particle case mismatch");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("pick", lemma);
}
@Test
public void testNullSentenceInSentenceList() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "ideas");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  List<CoreMap> sentenceList = Arrays.asList(sentence, null); 

  Annotation annotation = new Annotation("Null sentence in sentence list");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  try {
    annotator.annotate(annotation);
    assertEquals("idea", token.get(CoreAnnotations.LemmaAnnotation.class));
  } catch (Exception e) {
    fail("Annotator should handle null sentence safely");
  }
}
@Test
public void testTokenWithUnknownPOSKeepsOriginal() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "xyzzy"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "FOO"); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Unknown POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("xyzzy", lemma);
}
@Test
public void testTokenWithDigitPOS() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "1234");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD"); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Digit word POS CD");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("1234", lemma);
}
@Test
public void testPhrasalVerbWithVerbOnly_NoParticleSplit() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Verb only");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("give", lemma); 
}
@Test
public void testTokenWithTabWhitespaceText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "\t\t");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Tab whitespace token text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("", lemma);
}
@Test
public void testEmptyAnnotationObjectWithJustSentencesAnnotationKeySet() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  Annotation annotation = new Annotation("Annotation with empty sentence");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(new ArrayCoreMap())); 

  try {
    annotator.annotate(annotation);
    assertTrue(true); 
  } catch (Exception e) {
    fail("Annotator should safely skip incomplete sentence");
  }
}
@Test
public void testWhitespaceVerbPlusValidParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run fast_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Whitespace base with valid particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run fast_up", lemma); 
}
@Test
public void testTokenWithWhitespaceAroundTextAndValidPOS() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "  sleeping  ");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Whitespace padded text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("sleep", lemma); 
}
@Test
public void testMixedTokensOneMissingTextOneMissingPOS() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "cars");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "running");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, null); 

  CoreLabel token3 = new CoreLabel();
  token3.set(CoreAnnotations.TextAnnotation.class, null); 
  token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

  Annotation annotation = new Annotation("Mixed validity tokens");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  assertEquals("car", token1.get(CoreAnnotations.LemmaAnnotation.class));
  assertEquals("run", token2.get(CoreAnnotations.LemmaAnnotation.class));
  assertNull(token3.get(CoreAnnotations.LemmaAnnotation.class)); 
}
@Test
public void testTokenWithOnlyUnderscoreAsText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "_");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Underscore only text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("_", lemma); 
}
@Test
public void testTokenWithPOSNullAndEmptyText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Empty text and null POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("", lemma); 
}
@Test
public void testTokenWithValidPhrasalVerbDifferentTag() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "bring_back");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Valid phrasal VBD");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("bring_back", lemma); 
}
@Test
public void testTokenWithPhrasalVerbAndExtraUnderscores() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_up_now_please");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Complex phrasal with extra underscores");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("give_up_now_please", lemma); 
}
@Test
public void testTokenWithNullPOSAndUnderscoreText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "take_over");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Underscore text, no POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("take_over", lemma); 
}
@Test
public void testTokenWithNullTextAndEmptyPOS() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, null);
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ""); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Annotation annotation = new Annotation("Null text and empty POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);
  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertNull(lemma);
}
@Test
public void testTokenNullTokenObjectInSentence() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  List<CoreLabel> tokens = Arrays.asList(null);

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("Single null token in sentence");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  try {
    annotator.annotate(annotation);
    assertTrue(true);
  } catch (Exception e) {
    fail("Annotator should not throw if token is null");
  }
} 
}