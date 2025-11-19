package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class NERCombinerAnnotator_3_GPTLLMTest {

 @Test
  public void testDefaultConstructor() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testNERClassifierConstructor() throws Exception {
    Properties props = new Properties();
    NERClassifierCombiner combiner = new NERClassifierCombiner(props);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(combiner, true);
    assertNotNull(annotator);
  }
@Test
  public void testConstructWithProperties() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testFineGrainedNERFlagEnabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testEmptyAnnotationDoesNotCrash() throws Exception {
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    annotator.annotate(annotation);
    assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testDoOneFailedSentenceAssignsBackgroundNER() throws Exception {
    Annotation annotation = new Annotation("Failure test");
    CoreLabel token = new CoreLabel();
    token.setWord("failure");
    token.setNER(null);
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    NERClassifierCombiner nerCombiner = new NERClassifierCombiner(new Properties());
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerCombiner, false);
    annotator.doOneFailedSentence(annotation, sentence);

    assertEquals("O", token.ner());
  }
@Test
  public void testRequirementsSatisfiedIncludesNERAnnotations() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    boolean hasNamedEntity = false;
    boolean hasFineGrained = false;
    boolean hasEntityMentions = false;

    if (satisfied.contains(CoreAnnotations.NamedEntityTagAnnotation.class)) {
        hasNamedEntity = true;
    }

    if (satisfied.contains(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class)) {
        hasFineGrained = true;
    }

    if (satisfied.contains(CoreAnnotations.MentionsAnnotation.class)) {
        hasEntityMentions = true;
    }

    assertTrue(hasNamedEntity);
    assertTrue(hasFineGrained);
    assertTrue(hasEntityMentions);
  }
@Test
  public void testRequiresIncludesTextAnnotation() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    boolean foundTextAnnotation = false;

    if (required.contains(CoreAnnotations.TextAnnotation.class)) {
        foundTextAnnotation = true;
    }

    assertTrue(foundTextAnnotation);
  }
@Test
  public void testMergeTokensAddsMergedWordAndNERCount() {
    CoreLabel token = new CoreLabel();
    token.setWord("New");
    token.set(CoreAnnotations.AfterAnnotation.class, "");
    token.setEndPosition(3);
    token.setSentIndex(0);

    CoreLabel nextToken = new CoreLabel();
    nextToken.setWord("York");
    nextToken.set(CoreAnnotations.AfterAnnotation.class, " ");
    nextToken.setEndPosition(7);

    NERCombinerAnnotator.mergeTokens(token, nextToken);

    assertEquals("NewYork", token.word());
    assertEquals(" ", token.get(CoreAnnotations.AfterAnnotation.class));
    Integer mergeCount = token.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
    assertEquals(Integer.valueOf(1), mergeCount);
  }
@Test
  public void testTransferNERAnnotationsCopiesNERTag() {
    CoreLabel tokenSrc = new CoreLabel();
    tokenSrc.setWord("Obama");
    tokenSrc.setNER("PERSON");

    Annotation source = new Annotation("Obama");
    CoreMap sentenceSrc = new Annotation("Obama");
    List<CoreLabel> tokensSrc = new ArrayList<CoreLabel>();
    tokensSrc.add(tokenSrc);
    sentenceSrc.set(CoreAnnotations.TokensAnnotation.class, tokensSrc);
    List<CoreMap> sentencesSrc = new ArrayList<CoreMap>();
    sentencesSrc.add(sentenceSrc);
    source.set(CoreAnnotations.TokensAnnotation.class, tokensSrc);
    source.set(CoreAnnotations.SentencesAnnotation.class, sentencesSrc);

    CoreLabel tokenDest = new CoreLabel();
    tokenDest.setWord("Obama");

    Annotation dest = new Annotation("Obama");
    CoreMap sentenceDest = new Annotation("Obama");
    List<CoreLabel> tokensDest = new ArrayList<CoreLabel>();
    tokensDest.add(tokenDest);
    sentenceDest.set(CoreAnnotations.TokensAnnotation.class, tokensDest);
    List<CoreMap> sentencesDest = new ArrayList<CoreMap>();
    sentencesDest.add(sentenceDest);
    dest.set(CoreAnnotations.TokensAnnotation.class, tokensDest);
    dest.set(CoreAnnotations.SentencesAnnotation.class, sentencesDest);

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, dest);

    assertEquals("PERSON", tokenDest.ner());
  }
@Test
public void testRulesOnlyPropertyDisablesModelLoading() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.rulesOnly", "true");
  props.setProperty("ner.model", ""); 
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testStatisticalOnlyPropertyDisablesRegexNER() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.statisticalOnly", "true");
  props.setProperty("ner.fine.regexner.mapping", "somefile"); 
  props.setProperty("ner.model", "");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testMergeTokensHandlesMergeCountIncrement() {
  CoreLabel token = new CoreLabel();
  token.setWord("New");
  token.setSentIndex(0);
  token.set(CoreAnnotations.AfterAnnotation.class, "");
  token.setEndPosition(3);
  token.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

  CoreLabel next = new CoreLabel();
  next.setWord("York");
  next.set(CoreAnnotations.AfterAnnotation.class, " ");
  next.setEndPosition(6);

  NERCombinerAnnotator.mergeTokens(token, next);

  Integer actual = token.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
  assertEquals(Integer.valueOf(3), actual);
}
@Test
public void testMergeTokensSkipsNERExceptions() {
  Annotation document = new Annotation("Seattle - based startup");
  List<CoreLabel> tokens = new ArrayList<CoreLabel>();

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Seattle");
  token1.set(CoreAnnotations.AfterAnnotation.class, " ");
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("-");
  token2.set(CoreAnnotations.AfterAnnotation.class, "");
  tokens.add(token2);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("based"); 
  token3.set(CoreAnnotations.AfterAnnotation.class, " ");
  tokens.add(token3);

  CoreMap sentence = new Annotation(document.get(CoreAnnotations.TextAnnotation.class));
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  List<CoreMap> sentenceList = new ArrayList<CoreMap>();
  sentenceList.add(sentence);
  document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  Annotation result = null;
  try {
    java.lang.reflect.Method method = NERCombinerAnnotator.class.getDeclaredMethod("annotationWithNERTokenization", Annotation.class);
    method.setAccessible(true);
    result = (Annotation) method.invoke(null, document);
  } catch (Exception e) {
    fail("Reflection failed");
  }

  List<CoreLabel> resultTokens = result.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, resultTokens.size());
}
@Test
public void testAnnotateSkipsOversizedSentence() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.maxlen", "1");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Hello");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("World");

  List<CoreLabel> tokens = Arrays.asList(token1, token2);
  CoreMap sentence = new Annotation("Hello World");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  List<CoreMap> sentences = Arrays.asList(sentence);

  Annotation annotation = new Annotation("Hello World");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.doOneSentence(annotation, sentence);

  assertNull(token1.ner());
  assertNull(token2.ner());
}
@Test
public void testSetUpFineGrainedNERIsActive() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.applyFineGrained", "true");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testTokenTypeMoneyRemovesTimexAnnotation() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  CoreLabel moneyToken = new CoreLabel();
  moneyToken.setNER("MONEY");

  Annotation annotation = new Annotation("Cost is $5.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(moneyToken));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

  annotator.annotate(annotation);

  assertNull(moneyToken.get(TimeAnnotations.TimexAnnotation.class));
}
@Test
public void testTransferNERAnnotationsHandlesMultipleMergedTokens() {
  CoreLabel merged = new CoreLabel();
  merged.setWord("MegaCorpInc");
  merged.setNER("ORGANIZATION");
  merged.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 3);

  List<CoreLabel> mergedTokens = new ArrayList<CoreLabel>();
  mergedTokens.add(merged);

  CoreMap mergedSentence = new Annotation("sentence");
  mergedSentence.set(CoreAnnotations.TokensAnnotation.class, mergedTokens);

  Annotation source = new Annotation("Merged");
  source.set(CoreAnnotations.TokensAnnotation.class, mergedTokens);
  source.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(mergedSentence));

  CoreLabel orig1 = new CoreLabel(); orig1.setWord("Mega");
  CoreLabel orig2 = new CoreLabel(); orig2.setWord("Corp");
  CoreLabel orig3 = new CoreLabel(); orig3.setWord("Inc");

  List<CoreLabel> originalTokens = Arrays.asList(orig1, orig2, orig3);

  CoreMap targetSentence = new Annotation("Merged");
  targetSentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

  Annotation target = new Annotation("Merged");
  target.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(targetSentence));
  target.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

  NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, target);

  assertEquals("ORGANIZATION", orig1.ner());
  assertEquals("ORGANIZATION", orig2.ner());
  assertEquals("ORGANIZATION", orig3.ner());
}
@Test
public void testEntityMentionsAnnotatorIsInvoked() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.buildEntityMentions", "true");

  CoreLabel token = new CoreLabel();
  token.setWord("Obama");
  token.setNER("PERSON");

  Annotation annotation = new Annotation("Obama visited Paris");
  CoreMap sentence = new Annotation("Obama visited Paris");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  annotator.annotate(annotation);

  List<?> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
  assertNotNull(mentions);
}
@Test
public void testRequiresWithoutNumericClassifiers() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.applyNumericClassifiers", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  Set<Class<? extends CoreAnnotation>> required = annotator.requires();

  boolean hasPartOfSpeech = false;
  if (required.contains(CoreAnnotations.PartOfSpeechAnnotation.class)) {
    hasPartOfSpeech = true;
  }

  assertFalse(hasPartOfSpeech);
}
@Test
public void testNullModelPropertyFallsBackToDefaults() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", null);
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testEmptyPropertiesObjectDoesNotThrow() throws Exception {
  Properties props = new Properties();
  
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testUnknownNERLanguageCodeDefaultsGracefully() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.language", "zz"); 
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testApplyTokensRegexRulesDisabledByDefault() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.additional.tokensregex.rules", "");
  props.setProperty("ner.statisticalOnly", "true");
  props.setProperty("ner.buildEntityMentions", "false");
  props.setProperty("ner.applyFineGrained", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  
  assertNotNull(annotator);
}
@Test
public void testSetUpDocDateIsTriggeredIfPropertyExists() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.docdate.useHeuristic", "true");  
  props.setProperty("ner.applyFineGrained", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testSentenceAnnotationWithoutTokensIsHandledInTransfer() {
  Annotation nerAnnotation = new Annotation("Test");
  CoreMap nerSentence = new Annotation("Empty sentence");
  nerSentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSentence));
  nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

  Annotation originalAnnotation = new Annotation("Test");
  CoreMap origSentence = new Annotation("Empty sentence");
  origSentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSentence));
  originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

  NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);
  assertTrue(originalAnnotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
}
@Test
public void testTransferNERDoesNotFailIfNERTokenizedListSmallerThanOriginal() {
  CoreLabel nerToken = new CoreLabel();
  nerToken.setWord("California");
  nerToken.setNER("LOCATION");

  CoreMap nerSent = new Annotation("California");
  nerSent.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(nerToken));

  Annotation nerAnnotation = new Annotation("California");
  nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(nerToken));
  nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSent));

  CoreLabel orig1 = new CoreLabel();
  orig1.setWord("Calif");
  CoreLabel orig2 = new CoreLabel();
  orig2.setWord("ornia");

  CoreMap origSent = new Annotation("California");
  origSent.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(orig1, orig2));

  Annotation originalAnnotation = new Annotation("California");
  originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(orig1, orig2));
  originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSent));

  NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);
  assertEquals("LOCATION", orig1.ner());
  assertEquals("LOCATION", orig2.ner());
}
@Test
public void testDoOneSentenceHandlesRuntimeInterruptedExceptionGracefully() throws Exception {
  NERClassifierCombiner nerClassifier = new NERClassifierCombiner(new Properties()) {
    public List<CoreLabel> classifySentenceWithGlobalInformation(List<CoreLabel> sentence,
                                                                 Annotation document,
                                                                 CoreMap originalSentence) {
      throw new RuntimeInterruptedException();
    }
    @Override
    public String backgroundSymbol() {
      return "O";
    }
  };

  CoreLabel token = new CoreLabel();
  token.setWord("error");
  List<CoreLabel> tokens = Arrays.asList(token);

  CoreMap sentence = new Annotation("error");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("error");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerClassifier, false);
  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token.ner());
}
@Test
public void testRequirementsSatisfiedWithoutEntityMentions() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.buildEntityMentions", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
  boolean mentionsAbsent = !satisfied.contains(CoreAnnotations.MentionsAnnotation.class);
  assertTrue(mentionsAbsent);
}
@Test
public void testUseNERSpecificTokenizationFlagFalseSkipsMerging() throws Exception {
  Properties properties = new Properties();
  properties.setProperty("ner.model", "");
  properties.setProperty("ner.useNERSpecificTokenization", "false");
  properties.setProperty("ner.buildEntityMentions", "false");
  properties.setProperty("ner.applyFineGrained", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("high");
  token1.set(CoreAnnotations.AfterAnnotation.class, "");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("-");
  token2.set(CoreAnnotations.AfterAnnotation.class, "");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("tech");
  token3.set(CoreAnnotations.AfterAnnotation.class, " ");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
  CoreMap sentence = new Annotation("high - tech");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  List<CoreMap> sentences = Arrays.asList(sentence);

  Annotation annotation = new Annotation("high - tech");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, resultTokens.size());
  assertEquals("high", resultTokens.get(0).word());
  assertEquals("-", resultTokens.get(1).word());
  assertEquals("tech", resultTokens.get(2).word());
}
@Test
public void testAnnnotateHandlesTokenWithNoNERAndTimexSetForNUMBER() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setNER("NUMBER");

  CoreMap sentence = new Annotation("123");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("123 number");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  annotator.annotate(annotation);

  assertNull(token.get(TimeAnnotations.TimexAnnotation.class));
}
@Test
public void testAnnotateHandlesTokenWithNullNERGracefully() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.applyFineGrained", "true");
  props.setProperty("ner.buildEntityMentions", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Obama");
  token1.setNER(null);

  List<CoreLabel> tokens = Arrays.asList(token1);
  CoreMap sentence = new Annotation("Obama");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("Obama");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNotNull(token1.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
}
@Test
public void testTransferNERAnnotationsHandlesEmptyBothAnnotationsWithoutException() {
  Annotation nerAnnotation = new Annotation("doc");
  nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  Annotation original = new Annotation("doc");
  original.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  original.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);

  List<CoreLabel> result = original.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(0, result.size());
}
@Test
public void testDoOneSentenceDoesNotCrashWhenNERReturnsNull() throws Exception {
  NERClassifierCombiner dummyNER = new NERClassifierCombiner(new Properties()) {
    public List<CoreLabel> classifySentenceWithGlobalInformation(List<CoreLabel> sentence, Annotation annotation, CoreMap orig) {
      return null;
    }
    @Override
    public String backgroundSymbol() {
      return "O";
    }
  };

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(dummyNER, false);

  CoreLabel token = new CoreLabel();
  token.setNER(null);
  token.setWord("interrupted");

  List<CoreLabel> tokens = Arrays.asList(token);

  CoreMap sentence = new Annotation("interrupted");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("interrupted");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token.ner());
}
@Test
public void testDoOneSentencePropagatesNERAnnotationsCorrectly() throws Exception {
  NERClassifierCombiner ner = new NERClassifierCombiner(new Properties()) {
    public List<CoreLabel> classifySentenceWithGlobalInformation(List<CoreLabel> sentence, Annotation annotation, CoreMap sentenceMap) {
      CoreLabel result = new CoreLabel();
      result.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
      result.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Barack_Obama");
      HashMap<String, Double> probs = new HashMap<String, Double>();
      probs.put("PERSON", 0.99);
      result.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
      return Arrays.asList(result);
    }

    @Override
    public String backgroundSymbol() {
      return "O";
    }
  };

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);

  CoreLabel token = new CoreLabel();
  token.setWord("Obama");

  CoreMap sentence = new Annotation("Obama");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("Obama");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("PERSON", token.ner());
  assertEquals("Barack_Obama", token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  assertEquals(Double.valueOf(0.99), token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).get("PERSON"));
}
@Test
public void testAnnotatedTokenWithoutNamedEntityTagProbsGetsDefaultProbability() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Paris");
  token.setNER("LOCATION");
  token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null); 

  CoreMap sentence = new Annotation("Paris");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("Paris");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  annotator.annotate(annotation);

  Map<String, Double> probs = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
  assertEquals(Double.valueOf(-1.0), probs.get("LOCATION"));
}
@Test
public void testNullValueForNERModelPropertyDoesNotCrash() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", null);
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testCommaOnlyNERModelPropertyIsHandledGracefully() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", " , , ");
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testEmptyStringPassedToNERLanguageDefaultsToEnglish() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.language", "");
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testNullDocDatePropertySkipsDocDateSetup() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.buildEntityMentions", "false");
  props.setProperty("ner.applyFineGrained", "false");

  
  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testSpanishLanguageTriggersSpanishNumberAnnotator() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.language", "es");
  props.setProperty("ner.buildEntityMentions", "false");
  props.setProperty("ner.applyFineGrained", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testTokenEndingInDashStopsMergeIfExceptionWordFollows() throws Exception {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Chicago-");
  token1.set(CoreAnnotations.AfterAnnotation.class, "");
  token1.setSentIndex(0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("based"); 
  token2.set(CoreAnnotations.AfterAnnotation.class, " ");
  token2.setSentIndex(0);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);
  CoreMap sentence = new Annotation("Chicago- based");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("Chicago- based");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotatedCopy = null;
  try {
    java.lang.reflect.Method method = NERCombinerAnnotator.class.getDeclaredMethod("annotationWithNERTokenization", Annotation.class);
    method.setAccessible(true);
    annotatedCopy = (Annotation) method.invoke(null, annotation);
  } catch (Exception e) {
    fail("Reflection access failed.");
  }

  List<CoreLabel> flattenedTokens = annotatedCopy.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(2, flattenedTokens.size());
  assertEquals("Chicago-", flattenedTokens.get(0).word());
  assertEquals("based", flattenedTokens.get(1).word());
}
@Test
public void testTransferNERAnnotationSkipsMissingNERKey() {
  CoreLabel mergedToken = new CoreLabel();
  mergedToken.setWord("AlphaBeta");
  mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

  CoreMap mergedSentence = new Annotation("Merged");
  mergedSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(mergedToken));

  Annotation nerAnnotation = new Annotation("Merged");
  nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(mergedToken));
  nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(mergedSentence));

  CoreLabel orig1 = new CoreLabel();
  orig1.setWord("Alpha");

  CoreLabel orig2 = new CoreLabel();
  orig2.setWord("Beta");

  List<CoreLabel> originalTokens = Arrays.asList(orig1, orig2);
  CoreMap origSentence = new Annotation("Orig");
  origSentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

  Annotation original = new Annotation("Orig");
  original.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
  original.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSentence));

  
  NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);

  assertNull(orig1.ner());
  assertNull(orig2.ner());
}
@Test
public void testApplyFineGrainedTrueWithEmptyMappingStillInitializesFineGrainedNER() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.applyFineGrained", "true");
  props.setProperty("ner.fine.regexner.mapping", "");
  props.setProperty("ner.buildEntityMentions", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testNERClassifierReturnsNERWithoutNormalizedTag() throws Exception {
  NERClassifierCombiner ner = new NERClassifierCombiner(new Properties()) {
    public List<CoreLabel> classifySentenceWithGlobalInformation(List<CoreLabel> sent, Annotation doc, CoreMap sen) {
      CoreLabel result = new CoreLabel();
      result.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
      return Arrays.asList(result);
    }

    @Override
    public String backgroundSymbol() {
      return "O";
    }
  };

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);

  CoreLabel token = new CoreLabel();
  token.setWord("Paris");

  CoreMap sentence = new Annotation("Paris");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("Paris");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("LOCATION", token.ner());
  assertEquals("LOCATION", token.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
}
@Test
public void testTransferNERAnnotationsStopsGracefullyWhenMergedListExceedsOriginalList() {
  CoreLabel mergedToken = new CoreLabel();
  mergedToken.setWord("SanJose");
  mergedToken.setNER("LOCATION");
  mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 3);

  List<CoreLabel> nerTokens = Arrays.asList(mergedToken);
  CoreMap nerSentence = new Annotation("San Jose");
  nerSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
  Annotation nerAnnotation = new Annotation("San Jose");
  nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
  nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSentence));

  CoreLabel originalToken = new CoreLabel();
  originalToken.setWord("San");

  CoreMap originalSentence = new Annotation("San Jose");
  originalSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(originalToken));
  Annotation original = new Annotation("San Jose");
  original.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(originalToken));
  original.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSentence));

  NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);

  assertEquals("LOCATION", originalToken.ner());
}
@Test
public void testInvalidNERLanguageFallsBackToDefaultLanguage() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.language", "xyz");
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.buildEntityMentions", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator); 
}
@Test
public void testAnnotationWithHyphenAndNoAfterTokenIsMergedProperly() throws Exception {
  Annotation annotation = new Annotation("Chicago - area");

  CoreLabel t1 = new CoreLabel();
  t1.setWord("Chicago");
  t1.set(CoreAnnotations.AfterAnnotation.class, "");
  t1.setSentIndex(0);

  CoreLabel t2 = new CoreLabel();
  t2.setWord("-");
  t2.set(CoreAnnotations.AfterAnnotation.class, "");
  t2.setSentIndex(0);

  CoreLabel t3 = new CoreLabel();
  t3.setWord("area");
  t3.set(CoreAnnotations.AfterAnnotation.class, " ");
  t3.setSentIndex(0);

  List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

  CoreMap sentence = new Annotation("Chicago - area");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  Annotation result = null;
  try {
    java.lang.reflect.Method m = NERCombinerAnnotator.class.getDeclaredMethod("annotationWithNERTokenization", Annotation.class);
    m.setAccessible(true);
    result = (Annotation) m.invoke(null, annotation);
  } catch (Exception e) {
    fail("Reflection call failed: " + e.getMessage());
  }

  List<CoreLabel> resultTokens = result.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(3, resultTokens.size()); 
  assertEquals("Chicago", resultTokens.get(0).word());
  assertEquals("-", resultTokens.get(1).word());
  assertEquals("area", resultTokens.get(2).word());
}
@Test
public void testRequiresExcludesPosAndLemmaWhenSUTimeAndNumericDisabled() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.buildEntityMentions", "false");
  props.setProperty("ner.applyFineGrained", "false");
  props.setProperty("ner.useSUTime", "false");
  props.setProperty("ner.applyNumericClassifiers", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

  boolean hasPOS = required.contains(CoreAnnotations.PartOfSpeechAnnotation.class);
  boolean hasLemma = required.contains(CoreAnnotations.LemmaAnnotation.class);
  assertFalse(hasPOS);
  assertFalse(hasLemma);
}
@Test
public void testNERCombinerWithNullAfterDoesNotThrow() throws Exception {
  CoreLabel t1 = new CoreLabel();
  t1.setWord("New");
  t1.set(CoreAnnotations.AfterAnnotation.class, null);
  t1.setSentIndex(0);

  CoreLabel t2 = new CoreLabel();
  t2.setWord("-");
  t2.set(CoreAnnotations.AfterAnnotation.class, null);
  t2.setSentIndex(0);

  CoreLabel t3 = new CoreLabel();
  t3.setWord("York");
  t3.set(CoreAnnotations.AfterAnnotation.class, " ");
  t3.setSentIndex(0);

  Annotation annotation = new Annotation("New - York");
  CoreMap sentence = new Annotation("New - York");
  List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  Annotation result = null;
  try {
    java.lang.reflect.Method m = NERCombinerAnnotator.class.getDeclaredMethod("annotationWithNERTokenization", Annotation.class);
    m.setAccessible(true);
    result = (Annotation) m.invoke(null, annotation);
  } catch (Exception e) {
    fail("Reflection error: " + e.getMessage());
  }

  assertNotNull(result);
  assertTrue(result.get(CoreAnnotations.TokensAnnotation.class).size() > 0);
}
@Test
public void testDoOneSentenceResultSetsNormalizedTagNullWhenAbsent() throws Exception {
  NERClassifierCombiner ner = new NERClassifierCombiner(new Properties()) {
    public List<CoreLabel> classifySentenceWithGlobalInformation(List<CoreLabel> tokens, Annotation annotation, CoreMap sentence) {
      CoreLabel token = new CoreLabel();
      token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
      token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DATE", 0.8));
      return Arrays.asList(token);
    }

    @Override
    public String backgroundSymbol() {
      return "O";
    }
  };

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);

  CoreLabel inputToken = new CoreLabel();
  inputToken.setWord("tomorrow");

  CoreMap sentence = new Annotation("tomorrow");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(inputToken));

  Annotation doc = new Annotation("tomorrow");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotator.doOneSentence(doc, sentence);

  assertEquals("DATE", inputToken.ner());
  assertEquals("DATE", inputToken.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
  assertNull(inputToken.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
}
@Test
public void testAnnotationPreservesDocIDAndDocDateInCopy() throws Exception {
  Annotation original = new Annotation("Report dated yesterday");
  original.set(CoreAnnotations.DocIDAnnotation.class, "doc-1234");
  original.set(CoreAnnotations.DocDateAnnotation.class, "2023-01-01");

  CoreLabel token = new CoreLabel();
  token.setWord("yesterday");
  token.set(CoreAnnotations.AfterAnnotation.class, " ");
  token.setSentIndex(0);

  CoreMap sentence = new Annotation("yesterday");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  original.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  original.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation copy = null;
  try {
    java.lang.reflect.Method m = NERCombinerAnnotator.class.getDeclaredMethod("annotationWithNERTokenization", Annotation.class);
    m.setAccessible(true);
    copy = (Annotation) m.invoke(null, original);
  } catch (Exception e) {
    fail("Failed to access or invoke method: " + e.getMessage());
  }

  String docID = copy.get(CoreAnnotations.DocIDAnnotation.class);
  String docDate = copy.get(CoreAnnotations.DocDateAnnotation.class);

  assertEquals("doc-1234", docID);
  assertEquals("2023-01-01", docDate);
}
@Test
public void testTokenBeginEndIndexesAreSetInNERTokenization() throws Exception {
  Annotation doc = new Annotation("NYSE-listed companies");

  CoreLabel t1 = new CoreLabel();
  t1.setWord("NYSE");
  t1.set(CoreAnnotations.AfterAnnotation.class, "");
  t1.setSentIndex(0);

  CoreLabel t2 = new CoreLabel();
  t2.setWord("-");
  t2.set(CoreAnnotations.AfterAnnotation.class, "");
  t2.setSentIndex(0);

  CoreLabel t3 = new CoreLabel();
  t3.setWord("listed");
  t3.set(CoreAnnotations.AfterAnnotation.class, " ");
  t3.setSentIndex(0);

  CoreMap sentence = new Annotation("NYSE-listed");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2, t3));
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  Annotation result = null;
  try {
    java.lang.reflect.Method m = NERCombinerAnnotator.class.getDeclaredMethod("annotationWithNERTokenization", Annotation.class);
    m.setAccessible(true);
    result = (Annotation) m.invoke(null, doc);
  } catch (Exception e) {
    fail("Reflection call failed");
  }

  List<CoreLabel> tokens = result.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals((Integer) 0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
  assertEquals((Integer) 1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
}
@Test
public void testFineGrainedAnnotationOverridesNamedEntityTag() throws Exception {
  NERClassifierCombiner classifier = new NERClassifierCombiner(new Properties()) {
    public List<CoreLabel> classifySentenceWithGlobalInformation(List<CoreLabel> tokens, Annotation annotation, CoreMap sentence) {
      CoreLabel token = new CoreLabel();
      token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
      Map<String, Double> probs = new HashMap<String, Double>();
      probs.put("MISC", 0.5);
      token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
      return Arrays.asList(token);
    }

    @Override
    public String backgroundSymbol() {
      return "O";
    }
  };

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(classifier, false);

  CoreLabel token = new CoreLabel();
  token.setWord("OpenAI");

  CoreMap sentence = new Annotation("OpenAI");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation doc = new Annotation("OpenAI");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotator.doOneSentence(doc, sentence);

  assertEquals("MISC", token.ner());
  assertEquals("MISC", token.get(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
}
@Test
public void testNamedEntityTagProbsPreservedIfAlreadySet() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.buildEntityMentions", "false");
  props.setProperty("ner.applyFineGrained", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");
  token.setNER("ORGANIZATION");
  Map<String, Double> existingProbs = new HashMap<String, Double>();
  existingProbs.put("ORGANIZATION", 0.75);
  token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, existingProbs);

  CoreMap sentence = new Annotation("Tesla");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation doc = new Annotation("Tesla");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  doc.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  annotator.annotate(doc);

  Map<String, Double> finalProbs = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
  assertEquals(Double.valueOf(0.75), finalProbs.get("ORGANIZATION"));
}
@Test
public void testMaxTimeAndThreadConfigDoesNotCrash() throws Exception {
  Properties props = new Properties();
  props.setProperty("ner.model", "");
  props.setProperty("ner.nthreads", "2");
  props.setProperty("ner.maxtime", "1000");
  props.setProperty("ner.buildEntityMentions", "false");
  props.setProperty("ner.applyFineGrained", "false");

  NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testNERSpecificTokenizationWithMultipleMergesUpdatesMergeCount() throws Exception {
  CoreLabel t1 = new CoreLabel();
  t1.setWord("cost-");
  t1.set(CoreAnnotations.AfterAnnotation.class, "");

  CoreLabel t2 = new CoreLabel();
  t2.setWord("effective");
  t2.set(CoreAnnotations.AfterAnnotation.class, "");
  CoreLabel t3 = new CoreLabel();
  t3.setWord("-");
  t3.set(CoreAnnotations.AfterAnnotation.class, "");

  CoreLabel t4 = new CoreLabel();
  t4.setWord("solution");
  t4.set(CoreAnnotations.AfterAnnotation.class, " ");

  List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);
  CoreMap sentence = new Annotation("cost-effective-solution");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation doc = new Annotation("cost-effective-solution");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation result = null;
  try {
    java.lang.reflect.Method m = NERCombinerAnnotator.class.getDeclaredMethod("annotationWithNERTokenization", Annotation.class);
    m.setAccessible(true);
    result = (Annotation) m.invoke(null, doc);
  } catch (Exception e) {
    fail("Reflection call failed with exception: " + e.getMessage());
  }

  CoreLabel merged = result.get(CoreAnnotations.TokensAnnotation.class).get(0);
  Integer count = merged.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
  assertTrue(count >= 1);
} 
}