package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
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

public class MorphaAnnotator_5_GPTLLMTest {

 @Test
  public void testStandardVerbLemmatization() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run", lemma);
  }
@Test
  public void testNounLemmatization() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "dogs");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("dogs");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("dog", lemma);
  }
@Test
  public void testPhrasalVerbLemmatization() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "gave_up");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("gave up");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("give_up", lemma);
  }
@Test
  public void testUnknownPOSUsesStem() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "walking");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("walking");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    Morphology morphology = new Morphology();
    String expected = morphology.stem("walking");

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals(expected, lemma);
  }
@Test
  public void testMissingSentencesThrowsException() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Annotation annotation = new Annotation("Text with no sentences");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unable to find words/tokens"));
    }
  }
@Test
  public void testMultipleTokensMixedTags() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "ran");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "cats");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("mixed");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma1 = token1.get(CoreAnnotations.LemmaAnnotation.class);
    String lemma2 = token2.get(CoreAnnotations.LemmaAnnotation.class);

    assertEquals("run", lemma1);
    assertEquals("cat", lemma2);
  }
@Test
  public void testEmptyWord() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    Morphology morphology = new Morphology();
    String expected = morphology.lemma("", "NN");

    String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals(expected, actual);
  }
@Test
  public void testPhrasalVerbExtraUnderscore() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "give_up_now");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    Morphology morphology = new Morphology();
    String expected = morphology.lemma("give_up_now", "VBD");

    String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals(expected, actual);
  }
@Test
  public void testNullPOSTagProducesException() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to null POS tag");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testRequirementsSatisfied() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertEquals(1, result.size());
    assertTrue(result.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequiresIncludesAllDependencies() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requires();
    assertTrue(result.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.SentencesAnnotation.class));
    assertEquals(4, result.size());
  }
@Test
  public void testVerboseModeSafeExecution() {
    MorphaAnnotator annotator = new MorphaAnnotator(true);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "sat");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("sat");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("sit", lemma);
  }
@Test
public void testEmptyTokenListInSentence() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreMap emptySentence = new ArrayCoreMap();
  List<CoreLabel> emptyTokens = new ArrayList<>();
  emptySentence.set(CoreAnnotations.TokensAnnotation.class, emptyTokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(emptySentence);

  Annotation annotation = new Annotation("Empty token list test");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  
  annotator.annotate(annotation);
}
@Test
public void testSentenceWithoutTokensAnnotation() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreMap sentence = new ArrayCoreMap(); 

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Sentence without tokens");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to missing TokensAnnotation");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testPhrasalVerbNonMatchingParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "jumped_sideways");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD"); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Phrasal test");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("jumped_sideways", "VBD");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testPhrasalVerbWithInvalidVerbTag() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "bring_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN"); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Not a verb");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("bring_up", "NN");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testTokenWithNullTextAnnotation() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, null); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Test null text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to null word text");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testMultipleUnderscoresNonPhrasalFormat() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_something_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD"); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Complex underscore");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("give_something_up", "VBD");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testEmptyAnnotationInput() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  Annotation annotation = new Annotation("");

  try {
    annotator.annotate(annotation);
    fail("Expected RuntimeException on empty annotation with no sentences");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Unable to find words/tokens"));
  }
}
@Test
public void testSingleTokenSentenceWithoutPOS() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "talking");
  

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Missing POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to null POS");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testPhrasalVerbWithUpperCaseParticleNotRecognized() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_UP");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Uppercase particle test");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morphology = new Morphology();
  String expected = morphology.lemma("give_UP", "VBD");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testTokenWithWhitespacePOSAndValidText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "walking");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "  "); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Whitespace POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.stem("walking");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testPhrasalVerbWithSingleLetterParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_x");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Invalid phrasal particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("give_x", "VB");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testSentenceWithNullCoreLabel() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(null); 

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Null token test");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to null token in sentence token list");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testSentenceWithDuplicateAnnotationsIgnored() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "running");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");
  token.set(CoreAnnotations.LemmaAnnotation.class, "WILL_BE_OVERWRITTEN");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Duplicate LemmaAnnotation");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run", actual);
}
@Test
public void testPhrasalVerbTagStartsWithVBWithoutUnderscore() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "run");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("No underscore");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("run", actual);
}
@Test
public void testPhrasalVerbWithTwoUnderscoresOnlyLastIsParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_everything_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Complex phrasal");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("give_everything_up", "VBD");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testSentenceWithEmptyCoreLabelObject() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Empty token");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.stem("");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testTokensAnnotationPresentWithEmptyList() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>()); 

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Empty tokens list");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation); 

  assertTrue(true); 
}
@Test
public void testVerifyingLemmaIsOverwrittenIfAlreadyPresent() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "sat");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
  token.set(CoreAnnotations.LemmaAnnotation.class, "will_be_overwritten");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Overwriting lemma");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  String expected = "sit";
  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);

  assertEquals(expected, actual);
}
@Test
public void testTwoWordPhrasalVerbWithParticleNotInList() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "fall_sideways");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD"); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Valid verb, invalid particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("fall_sideways", "VBD");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testPhrasalVerbTagIsEmptyButWordHasUnderscore() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ""); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Word has underscore, POS is empty");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.stem("give_up");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testPhrasalVerbParticleListIsMatchedCaseSensitiveFailure() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "take_Up"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Case-sensitive particle failure");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("take_Up", "VBD");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testTokenWithWhitespaceOnlyText() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "   ");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Whitespace text token");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("   ", "NN");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testTokenWithWhitespaceOnlyPOSTag() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "running");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "   "); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Whitespace POS tag");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("running", "   ");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testSentenceWithNullTokensAnnotationKeySetExplicitly() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, null); 

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Explicit null TokensAnnotation");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to null TokensAnnotation");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testTokenWithNonVerbTagContainingUnderscoreButNonPhrasal() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "bank_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN"); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Underscore but not verb");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("bank_up", "NN");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testAnnotationWithNullSentencesAnnotationExplicit() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  Annotation annotation = new Annotation("Explicit null sentences");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null); 

  try {
    annotator.annotate(annotation);
    fail("Expected RuntimeException due to null SentencesAnnotation");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Unable to find words/tokens"));
  }
}
@Test
public void testSentenceWithTokenMissingBothTextAndPOSTag() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Missing text and POS");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to unset fields");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testPhrasalVerbWithValidVerbAndMultiPartParticle() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_up_in"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, list);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Extra particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("give_up_in", "VB");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testConstructorWithVerboseFalseDoesNotThrowAndLemmatizesCorrectly() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "walking");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("verbose false");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("walk", actual);
}
@Test
public void testConstructorDefaultVerboseTrueStillProcessesInput() {
  MorphaAnnotator annotator = new MorphaAnnotator(); 

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "bought");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("default constructor");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("buy", actual);
}
@Test
public void testPhrasalVerbVerbIsEmptyString() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "_up"); 
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, list);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Missing verb component");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morpha = new Morphology();
  String expected = morpha.lemma("_up", "VB");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testPhrasalVerbWithOnlyOnePartAfterSplit() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "give_");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, list);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Phrasal missing particle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("give_", "VB");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testPartOfSpeechContainsUnderscoreIsStillProcessed() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "walking");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB_G"); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, list);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("POS contains underscore");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("walking", "VB_G");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testNonVBTagWithUnderscoreReturnsNormalLemma() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "catch_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN"); 

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, list);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("NN underscore");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  Morphology morphology = new Morphology();
  String expected = morphology.lemma("catch_up", "NN");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testPhrasalVerbWhereVerbContainsUnderscoreInvalid() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "look_out_up");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Verb contains multiple underscores");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  Morphology morph = new Morphology();
  String expected = morph.lemma("look_out_up", "VB");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testAnnotateWithEmptyAnnotationObject() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  Annotation annotation = new Annotation(""); 

  try {
    annotator.annotate(annotation);
    fail("Expected RuntimeException for missing SentencesAnnotation");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Unable to find words/tokens"));
  }
}
@Test
public void testMultipleTokensContainDifferentPhrasalVerbs() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "fall_down");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "get_up");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Multiple phrasal verbs");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  String lemma1 = token1.get(CoreAnnotations.LemmaAnnotation.class);
  String lemma2 = token2.get(CoreAnnotations.LemmaAnnotation.class);

  assertEquals("fall_down", lemma1);
  assertEquals("get_up", lemma2);
}
@Test
public void testSingleCharacterWordLemmatization() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "a");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Single character lemma");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  annotator.annotate(annotation);

  Morphology morphology = new Morphology();
  String expected = morphology.lemma("a", "DT");

  String actual = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals(expected, actual);
}
@Test
public void testMissingTextAnnotationKeyThrowsException() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreMap sentence = new ArrayCoreMap();
  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("Missing text annotation");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException due to missing text");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testMinimumValidSingleTokenLemmatization() {
  MorphaAnnotator annotator = new MorphaAnnotator(false);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "is");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");

  CoreMap sentence = new ArrayCoreMap();
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("Minimum valid input");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotator.annotate(annotation);

  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
  assertEquals("be", lemma);
} 
}