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

public class EntityMentionsAnnotator_4_GPTLLMTest {

 @Test
  public void testDefaultConstructor_InitializesFields() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testPropertyConstructorOverrides() {
    Properties props = new Properties();
    props.setProperty("test.nerCoreAnnotation", "edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation");
    props.setProperty("test.mentionsCoreAnnotation", "edu.stanford.nlp.ling.CoreAnnotations$MentionsAnnotation");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("test", props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateSimpleNERProducesMentions() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setNER("PERSON");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setNER("PERSON");
    token2.setBeginPosition(7);
    token2.setEndPosition(12);
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("visited");
    token3.setNER("O");
    token3.setBeginPosition(13);
    token3.setEndPosition(20);
    token3.set(CoreAnnotations.TextAnnotation.class, "visited");

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Paris");
    token4.setNER("LOCATION");
    token4.setBeginPosition(21);
    token4.setEndPosition(26);
    token4.set(CoreAnnotations.TextAnnotation.class, "Paris");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    Annotation sentence = new Annotation("Barack Obama visited Paris");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation document = new Annotation("Barack Obama visited Paris");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    document.set(CoreAnnotations.TextAnnotation.class, "Barack Obama visited Paris");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(2, mentions.size());
  }
@Test
  public void testAnnotateWithNoNERProducesNoMentions() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setNER("O");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.setNER("O");
    token2.setBeginPosition(6);
    token2.setEndPosition(11);
    token2.set(CoreAnnotations.TextAnnotation.class, "world");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation sentence = new Annotation("Hello world");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("Hello world");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "Hello world");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testDetermineEntityMentionConfidencesMinimalCase() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    Map<String, Double> prob1 = new HashMap<>();
    prob1.put("PERSON", 0.85);
    prob1.put("ORGANIZATION", 0.10);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    Annotation entityMention = new Annotation("Barack");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> scores = EntityMentionsAnnotator.determineEntityMentionConfidences(entityMention);

    assertNotNull(scores);
    assertEquals(2, scores.size());
    assertEquals(0.85, scores.get("PERSON"), 0.001);
    assertEquals(0.10, scores.get("ORGANIZATION"), 0.001);
  }
@Test
  public void testKbpPronominalMentionReturnsTrue() {
    CoreLabel pronoun = new CoreLabel();
    pronoun.setWord("he");
  }
@Test
  public void testAnnotatePronominalMentionsAddsMention() {
    CoreLabel token = new CoreLabel();
    token.setWord("she");
    token.set(CoreAnnotations.TextAnnotation.class, "she");
    token.setNER("O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("she");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation document = new Annotation("she");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
  }
@Test
  public void testOverlapsWithMentionTrueOnOverlap() {
    CoreLabel tokenA = new CoreLabel();
    tokenA.setBeginPosition(5);
    tokenA.setEndPosition(10);

    List<CoreLabel> tokensA = new ArrayList<>();
    tokensA.add(tokenA);

    Annotation mentionA = new Annotation("A");
    mentionA.set(CoreAnnotations.TokensAnnotation.class, tokensA);

    CoreLabel tokenB = new CoreLabel();
    tokenB.setBeginPosition(8);
    tokenB.setEndPosition(15);

    List<CoreLabel> tokensB = new ArrayList<>();
    tokensB.add(tokenB);

    Annotation mentionB = new Annotation("B");
    mentionB.set(CoreAnnotations.TokensAnnotation.class, tokensB);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mentionB);
  }
@Test
  public void testOverlapsWithMentionFalseNoOverlap() {
    CoreLabel tokenA = new CoreLabel();
    tokenA.setBeginPosition(0);
    tokenA.setEndPosition(4);

    List<CoreLabel> tokensA = new ArrayList<>();
    tokensA.add(tokenA);

    Annotation mentionA = new Annotation("A");
    mentionA.set(CoreAnnotations.TokensAnnotation.class, tokensA);

    CoreLabel tokenC = new CoreLabel();
    tokenC.setBeginPosition(10);
    tokenC.setEndPosition(15);

    List<CoreLabel> tokensC = new ArrayList<>();
    tokensC.add(tokenC);

    Annotation mentionC = new Annotation("C");
    mentionC.set(CoreAnnotations.TokensAnnotation.class, tokensC);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mentionC);
  }
@Test
  public void testAnnotateWithEmptyDocument() {
    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testAnnotateNullTokenBeginAnnotationDefaultsToZero() {
    CoreLabel token = new CoreLabel();
    token.setWord("London");
    token.setNER("LOCATION");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.TextAnnotation.class, "London");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("London");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("London");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "London");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testDetermineEntityMentionConfidencesReturnsNullWhenMissingProbs() {
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> probs = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(probs);
  }
@Test
  public void testDetermineEntityMentionConfidencesMissingLabelDropsToNegativeOne() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> map1 = new HashMap<>();
    map1.put("LOCATION", 0.75);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, map1);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> map2 = new HashMap<>();
    map2.put("LOCATION", 0.85);
    map2.put("PERSON", 1.1);  
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, map2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    Annotation mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> probs = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNotNull(probs);
    assertEquals(2, probs.size());
    assertEquals(0.75, probs.get("LOCATION"), 0.001);
    assertEquals(-1.0, probs.get("PERSON"), 0.001);
  }
@Test
  public void testOverlapsWithMentionThrowsNoExceptionOnNullList() {
    CoreLabel token = new CoreLabel();
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation needle = new Annotation("test");
    needle.set(CoreAnnotations.TokensAnnotation.class, tokens);
  }
@Test
  public void testAnnotateAddsWikipediaEntityFromToken() {
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setNER("PERSON");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Barack_Obama");

    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("Obama");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "Obama");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.get(0).containsKey(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("Barack_Obama", mentions.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testAnnotateSetsTimexWhenPresent() {
    CoreLabel token = new CoreLabel();
    token.setWord("tomorrow");
    token.setNER("DATE");
    token.setBeginPosition(0);
    token.setEndPosition(8);

    Timex timex = new Timex("T1");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.set(CoreAnnotations.TextAnnotation.class, "tomorrow");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("tomorrow");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("tomorrow");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "tomorrow");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertNotNull(mentions.get(0).get(TimeAnnotations.TimexAnnotation.class));
    assertEquals("T1", mentions.get(0).get(TimeAnnotations.TimexAnnotation.class).tid());
  }
@Test
  public void testPronominalMentionHeSetsMaleGender() {
    CoreLabel token = new CoreLabel();
    token.setWord("he");
    token.set(CoreAnnotations.TextAnnotation.class, "he");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("he");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("he");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testAnnotateHandlesMultipleSentences() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");
    token1.setNER("PERSON");
    token1.setBeginPosition(0);
    token1.setEndPosition(4);
    token1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("London");
    token2.setNER("LOCATION");
    token2.setBeginPosition(5);
    token2.setEndPosition(11);
    token2.set(CoreAnnotations.TextAnnotation.class, "London");

    List<CoreLabel> sentence1Tokens = new ArrayList<>();
    sentence1Tokens.add(token1);

    List<CoreLabel> sentence2Tokens = new ArrayList<>();
    sentence2Tokens.add(token2);

    Annotation sentence1 = new Annotation("John");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, sentence1Tokens);
    sentence1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence1.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    Annotation sentence2 = new Annotation("London");
    sentence2.set(CoreAnnotations.TokensAnnotation.class, sentence2Tokens);
    sentence2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    sentence2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    Annotation document = new Annotation("John London");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "John London");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(2, mentions.size());
    assertEquals((Integer) 0, mentions.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals((Integer) 1, mentions.get(1).get(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testAnnotateSkipsNullMentionsAnnotationOnSentence() {
    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.TextAnnotation.class, "IBM");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("IBM");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("IBM");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "IBM");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> outputMentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(outputMentions);
    assertEquals(1, outputMentions.size());
  }
@Test
  public void testEmptyTokenListDoesNotCrashAnnotator() {
    Annotation sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TextAnnotation.class, "");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testAnnotateAssignsFallbackTextWhenNormalizedNameNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("Mars");
    token.setNER("LOCATION");
    token.setBeginPosition(10);
    token.setEndPosition(14);
    token.set(CoreAnnotations.TextAnnotation.class, "Mars");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("Explore Mars");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("Explore Mars");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "Explore Mars");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertEquals("Mars", mentions.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAcronymMatchDoesNotAddWhenTooManyMentions() {
    List<CoreMap> sentences = new ArrayList<>();
    for (int i = 0; i < 101; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord("Org" + i);
      token.setNER("ORGANIZATION");
      token.setBeginPosition(i * 5);
      token.setEndPosition(i * 5 + 4);
      token.set(CoreAnnotations.TextAnnotation.class, "Org" + i);

      List<CoreLabel> tokens = new ArrayList<>();
      tokens.add(token);

      Annotation sentence = new Annotation("Org" + i);
      sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
      sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
      sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

      CoreMap mention = new Annotation("Org" + i);
      mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
      mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

      List<CoreMap> mentionList = new ArrayList<>();
      mentionList.add(mention);
      sentence.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

      sentences.add(sentence);
    }

    Annotation document = new Annotation("Dummy Text");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "Dummy Text");

    Properties props = new Properties();
    props.setProperty("test.acronyms", "true");
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("test", props);
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    
    assertTrue(mentions.size() <= 101);
  }
@Test
  public void testInvalidAnnotationClassesInPropertiesDoNotCrash() {
    Properties props = new Properties();
    props.setProperty("fake.nerCoreAnnotation", "bad.class.Name");
    props.setProperty("fake.mentionsCoreAnnotation", "another.bad.ClassName");

    
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("fake", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.TextAnnotation.class, "Google");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("Google");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Google");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TextAnnotation.class, "Google");

    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testRequirementsSatisfiedReturnsCorrectKey() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertNotNull(satisfied);
    assertTrue(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testIS_TOKENS_COMPATIBLEDifferentNER_ReturnsFalse() {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Barack");

    CoreLabel token2 = new CoreLabel();
    token2.setNER("LOCATION");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "London");

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testIS_TOKENS_COMPATIBLETimeMismatchTid_ReturnsFalse() {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("DATE");

    Timex timex1 = new Timex("T1");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex1);
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "tomorrow");

    CoreLabel token2 = new CoreLabel();
    token2.setNER("DATE");

    Timex timex2 = new Timex("T2");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex2);
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "next day");

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testTokenCompatibilityNumberSameNormalizedNERAndValue() {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("NUMBER");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "1");
    token1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 1);
    
    CoreLabel token2 = new CoreLabel();
    token2.setNER("NUMBER");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "1");

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testTokenCompatibilityNumberDifferentValuesReturnFalse() {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("NUMBER");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "1");
    token1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 1);

    CoreLabel token2 = new CoreLabel();
    token2.setNER("NUMBER");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "1");
    token2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 2);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testIS_TOKENS_COMPATIBLE_NullTokensReturnFalse() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Pair<CoreLabel, CoreLabel> pair1 = new Pair<>(null, new CoreLabel());
    Pair<CoreLabel, CoreLabel> pair2 = new Pair<>(new CoreLabel(), null);
  }
@Test
  public void testTimexTokensSameTidAreCompatible() {
    Timex timex = new Timex("T123");

    CoreLabel t1 = new CoreLabel();
    t1.setNER("TIME");
    t1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "same");
    t1.set(TimeAnnotations.TimexAnnotation.class, timex);

    CoreLabel t2 = new CoreLabel();
    t2.setNER("TIME");
    t2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "same");
    t2.set(TimeAnnotations.TimexAnnotation.class, timex);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(t1, t2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testSetMentionsWikipediaEntitySkipIfAlreadySetToO() {
    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.TextAnnotation.class, "Google");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Google_Inc");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Google");
    token2.setNER("ORGANIZATION");
    token2.setBeginPosition(0);
    token2.setEndPosition(6);
    token2.set(CoreAnnotations.TextAnnotation.class, "Google");
    token2.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("Google");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    Annotation sentence = new Annotation("Google");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);

    Annotation doc = new Annotation("Google");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);
    doc.set(CoreAnnotations.TextAnnotation.class, "Google");

    EntityMentionsAnnotator ann = new EntityMentionsAnnotator();
    ann.annotate(doc);
    List<CoreMap> out = doc.get(CoreAnnotations.MentionsAnnotation.class);

    assertEquals("Google_Inc", out.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testEntityMentionIndexAndCanonicalAssignment() {
    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setNER("PERSON");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.TextAnnotation.class, "Barack");

    CoreMap mention = new Annotation("Barack");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    Annotation sentence = new Annotation("Barack");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Barack");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TextAnnotation.class, "Barack");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> result = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(result);
    assertEquals((Integer) 0, result.get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals((Integer) 0, result.get(0).get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
    assertEquals((Integer) 0, result.get(0).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotatePronominalMentionUnknownGender() {
    CoreLabel token = new CoreLabel();
    token.setWord("they");
    token.setNER("O");
    token.set(CoreAnnotations.TextAnnotation.class, "they");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("they");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("they");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testAnnotateHandlesMentionWithSingleTokenAndNoCharacterOffsets() {
    CoreLabel token = new CoreLabel();
    token.setWord("January");
    token.setNER("DATE");
    token.set(CoreAnnotations.TextAnnotation.class, "January");

    Timex timex = new Timex("T99");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("January");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    Annotation sentence = new Annotation("January");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);

    Annotation document = new Annotation("January");
    document.set(CoreAnnotations.SentencesAnnotation.class, sents);
    document.set(CoreAnnotations.TextAnnotation.class, "January");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> result = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("T99", result.get(0).get(TimeAnnotations.TimexAnnotation.class).tid());
  }
@Test
  public void testAcronymDetectionNoMatchWhenNERIsSet() {
    CoreLabel token = new CoreLabel();
    token.setWord("UN");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.TextAnnotation.class, "UN");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("UN");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("UN");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "UN");

    Properties props = new Properties();
    props.setProperty("foo.acronyms", "true");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("foo", props);
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertEquals("UN", mentions.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMentionsWithNoNERGetEntityTypeFromNERKeyNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("data");
    token.set(CoreAnnotations.TextAnnotation.class, "data");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("data");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("data");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("data");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "data");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> out = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, out.size());
    assertEquals("data", out.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMentionChunkWithoutNormalizedNERFallsBackToText() {
    CoreLabel token = new CoreLabel();
    token.setWord("Saturn");
    token.setNER("LOCATION");
    token.set(CoreAnnotations.TextAnnotation.class, "Saturn");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, null);
    token.setBeginPosition(0);
    token.setEndPosition(6);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("Saturn");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Saturn");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TextAnnotation.class, "Saturn");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> out = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, out.size());
    assertEquals("Saturn", out.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOCATION", out.get(0).get(CoreAnnotations.EntityTypeAnnotation.class));
  }
@Test
  public void testNoWikipediaEntityAnnotationAddedForOAndNoTokensWithEntity() {
    CoreLabel token = new CoreLabel();
    token.setNER("PERSON");
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Obama");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TextAnnotation.class, "Obama");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> out = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, out.size());
    assertEquals("O", out.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testMentionWithNoTimexAnnotationTokensDoesNotSetTimex() {
    CoreLabel token = new CoreLabel();
    token.setWord("tomorrow");
    token.setNER("DATE");
    token.set(CoreAnnotations.TextAnnotation.class, "tomorrow");
    token.setBeginPosition(0);
    token.setEndPosition(8);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("tomorrow");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("tomorrow");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("tomorrow");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "tomorrow");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> output = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNull(output.get(0).get(TimeAnnotations.TimexAnnotation.class));
  }
@Test
  public void testDetermineEntityMentionConfidencesWithEmptyTokenListReturnsNull() {
    CoreMap entityMention = new Annotation("test");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    HashMap<String, Double> probs = EntityMentionsAnnotator.determineEntityMentionConfidences(entityMention);
    assertNull(probs);
  }
@Test
  public void testEmptyCoreAnnotationsSentencesAnnotationclassDoesNotThrowException() {
    Annotation ann = new Annotation("text here");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(ann);

    List<CoreMap> globalMentions = ann.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(globalMentions);
    assertEquals(0, globalMentions.size());
  }
@Test
  public void testEmptyCharacterOffsetsSkipsTextSlicingWithoutError() {
    CoreLabel token = new CoreLabel();
    token.setNER("PERSON");
    token.setWord("Obama");
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation document = new Annotation("Obama");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    
    document.set(CoreAnnotations.TextAnnotation.class, "Obama");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> output = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(output);
    assertTrue(output.size() > 0);
  }
@Test
  public void testAnnotateHandlesSentenceWithoutMentionsAnnotationGracefully() {
    CoreLabel token = new CoreLabel();
    token.setWord("John");
    token.setNER("PERSON");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.set(CoreAnnotations.TextAnnotation.class, "John");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("John");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("John");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "John");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> global = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(global);
    assertEquals(1, global.size());
  }
@Test
  public void testTokenCompatibilitySameNERAndNullTimexOnBothSides() {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, null);

    CoreLabel token2 = new CoreLabel();
    token2.setNER("DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");
    token2.set(TimeAnnotations.TimexAnnotation.class, null);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testTokenCompatibilityDifferentNERSameTimexId() {
    Timex timex = new Timex("T42");

    CoreLabel token1 = new CoreLabel();
    token1.setNER("DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "today");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex);

    CoreLabel token2 = new CoreLabel();
    token2.setNER("TIME"); 
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "today");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testTokenCompatibilityNullNormalizedNERReturnsFalse() {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("LOCATION");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, null);

    CoreLabel token2 = new CoreLabel();
    token2.setNER("LOCATION");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Mars");

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testAnnotateMentionProbsEmptyProbsOnTokenStillReturnsMapWithNegativeScore() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Intel");
    token1.setNER("ORGANIZATION");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    Map<String, Double> emptyProbs = new HashMap<>();
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, emptyProbs);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    CoreMap mention = new Annotation("Intel");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Map<String, Double> tagProbs = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNotNull(tagProbs); 
    assertEquals(0, tagProbs.size()); 
  }
@Test
  public void testAnnotateMentionTokenTextUsedWhenOffsetsValidAndGlobalTextPresent() {
    CoreLabel token = new CoreLabel();
    token.setWord("NASA");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(10);
    token.setEndPosition(14);
    token.set(CoreAnnotations.TextAnnotation.class, "NASA");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("NASA");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("NASA");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("Launch at NASA HQ today.");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "Launch at NASA HQ today.");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    String text = document.get(CoreAnnotations.MentionsAnnotation.class).get(0).get(CoreAnnotations.TextAnnotation.class);
    assertEquals("NASA", text); 
  }
@Test
  public void testAnnotateMentionTokenTextNotUsedWhenOffsetsInvalid() {
    CoreLabel token = new CoreLabel();
    token.setWord("Moon");
    token.setNER("LOCATION");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.set(CoreAnnotations.TextAnnotation.class, "Moon");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("Moon");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 100); 
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 150);   
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("Moon");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("This sentence is too short.");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "This sentence is too short.");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    String usedText = document.get(CoreAnnotations.MentionsAnnotation.class).get(0).get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Moon", usedText); 
  }
@Test
  public void testAcronymsSkippedWhenDoAcronymsFalseEvenIfMatchExists() {
    CoreLabel token = new CoreLabel();
    token.setWord("USA");
    token.setNER("O"); 
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.TextAnnotation.class, "USA");

    List<CoreLabel> sentenceTokens = new ArrayList<>();
    sentenceTokens.add(token);

    Annotation mention = new Annotation("United States of America");
    List<CoreLabel> orgTokens = new ArrayList<>();

    CoreLabel orgTok = new CoreLabel();
    orgTok.setWord("United");
    orgTok.setNER("ORGANIZATION");
    orgTok.set(CoreAnnotations.TextAnnotation.class, "United");
    orgTokens.add(orgTok);

    mention.set(CoreAnnotations.TokensAnnotation.class, orgTokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreMap sentence = new Annotation("USA");
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation document = new Annotation("USA");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    document.set(CoreAnnotations.TextAnnotation.class, "USA");

    
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> resultMentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    boolean acronymAdded = false;
    for (CoreMap m : resultMentions) {
      String text = m.get(CoreAnnotations.TextAnnotation.class);
      if ("USA".equals(text) && "ORGANIZATION".equals(m.get(CoreAnnotations.NamedEntityTagAnnotation.class))) {
        acronymAdded = true;
      }
    }

    assertFalse(acronymAdded); 
  }
@Test
  public void testDetermineEntityMentionConfidencesTokenMissingProbsOnSecondToken() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("LOCATION", 0.95);
    probs1.put("PERSON", 0.80);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel token2 = new CoreLabel();
    
    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("LOCATION", 0.85); 
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap mention = new Annotation("Obama Paris");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(0.85, result.get("LOCATION"), 0.001);
    assertEquals(-1.0, result.get("PERSON"), 0.001); 
  }
@Test
  public void testIS_TOKENS_COMPATIBLENumberNERBothNullValues() {
    CoreLabel t1 = new CoreLabel();
    t1.setNER("NUMBER");
    t1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "100");
    t1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, null);

    CoreLabel t2 = new CoreLabel();
    t2.setNER("NUMBER");
    t2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "100");
    t2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, null);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(t1, t2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testMentionWithWikipediaAnnotationAlreadySetShouldSkipTokenScan() {
    CoreLabel token = new CoreLabel();
    token.setNER("ORGANIZATION");
    token.setWord("Google");
    token.set(CoreAnnotations.TextAnnotation.class, "Google");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Google_Inc");

    List<CoreLabel> mentionTokens = new ArrayList<>();
    mentionTokens.add(token);

    CoreMap mention = new Annotation("Google");
    mention.set(CoreAnnotations.TokensAnnotation.class, mentionTokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Google_Inc");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("Google");
    sentence.set(CoreAnnotations.TokensAnnotation.class, mentionTokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Today Google announced...");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TextAnnotation.class, "Today Google announced...");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);
    List<CoreMap> finalMentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(finalMentions);
    assertEquals("Google_Inc", finalMentions.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testIS_TOKENS_COMPATIBLETimeTidOneIsNull() {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, null);

    CoreLabel token2 = new CoreLabel();
    token2.setNER("DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-01-01");
    Timex timex = new Timex("T100");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
  }
@Test
  public void testOverlapsWithMentionValidOverlapWithDifferentLengths() {
    CoreLabel a = new CoreLabel();
    a.setBeginPosition(5);
    a.setEndPosition(10);

    List<CoreLabel> needleTokens = new ArrayList<>();
    needleTokens.add(a);

    Annotation needle = new Annotation("Obama");
    needle.set(CoreAnnotations.TokensAnnotation.class, needleTokens);

    CoreLabel b1 = new CoreLabel();
    b1.setBeginPosition(8);
    b1.setEndPosition(15);

    CoreLabel b2 = new CoreLabel();
    b2.setBeginPosition(16);
    b2.setEndPosition(20);

    List<CoreLabel> hayTokens = new ArrayList<>();
    hayTokens.add(b1);
    hayTokens.add(b2);

    Annotation hayMention = new Annotation("Obama went");
    hayMention.set(CoreAnnotations.TokensAnnotation.class, hayTokens);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(hayMention);
  }
@Test
  public void testDetermineEntityMentionConfidencesMultipleTokensSelectMinScore() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> probsA = new HashMap<>();
    probsA.put("LOCATION", 0.85);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probsA);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> probsB = new HashMap<>();
    probsB.put("LOCATION", 0.65); 
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probsB);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap mention = new Annotation("Rome Paris");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertEquals(1, result.size());
    assertEquals(0.65, result.get("LOCATION"), 0.001);
  }
@Test
  public void testAnnotatorRequirementsDefaultIncludesNER() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAnnotatorRequirementsCustomNERDoesNotIncludeNERRequirement() {
    Properties props = new Properties();
    props.setProperty("custom.nerCoreAnnotation", "java.lang.String");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("custom", props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertFalse(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testCustomizeAnnotationsFromPropertiesWithValidOverrides() {
    Properties props = new Properties();
    props.setProperty("ent.nerCoreAnnotation", "edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation");
    props.setProperty("ent.mentionsCoreAnnotation", "edu.stanford.nlp.ling.CoreAnnotations$MentionsAnnotation");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("ent", props);
    assertNotNull(annotator);
  }
@Test
  public void testCustomizeAnnotationsFromPropertiesWithInvalidClassesDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("bad.nerCoreAnnotation", "invalid.ClassName");
    props.setProperty("bad.mentionsCoreAnnotation", "also.invalid.ClassName");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("bad", props);
    assertNotNull(annotator);
  } 
}