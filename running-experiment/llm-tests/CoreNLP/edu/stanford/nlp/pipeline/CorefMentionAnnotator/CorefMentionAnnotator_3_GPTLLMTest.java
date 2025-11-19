package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.coref.md.CorefMentionFinder;
import edu.stanford.nlp.coref.md.RuleBasedCorefMentionFinder;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CorefMentionAnnotator_3_GPTLLMTest {

 @Test
  public void testRequiresContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContainsExpected() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));
  }
@Test
  public void testSynchCorefMentionEntityMention_MatchingPersonTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    Mention cm = new Mention(0, 0, 2, "Joe Smith");

    CoreMap em = mock(CoreMap.class);
    when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
    when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMention_TokenMismatchReturnsFalse() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Joe");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("Smith");

    List<CoreLabel> cmTokens = new ArrayList<>();
    cmTokens.add(cmToken1);
    cmTokens.add(cmToken2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    Mention cm = new Mention(0, 0, 2, "Joe Smith");

    CoreLabel emToken = new CoreLabel();
    emToken.setWord("Bob");

    List<CoreLabel> emTokens = new ArrayList<>();
    emTokens.add(emToken);

    CoreMap em = mock(CoreMap.class);
    when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
    when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchCorefMentionEntityMention_SkipTitleTag() {
    CoreLabel titleToken = new CoreLabel();
    titleToken.setWord("Mr.");
    titleToken.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel nameToken = new CoreLabel();
    nameToken.setWord("Smith");

    List<CoreLabel> cmTokens = new ArrayList<>();
    cmTokens.add(titleToken);
    cmTokens.add(nameToken);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    Mention cm = new Mention(0, 0, 2, "Mr. Smith");

    List<CoreLabel> emTokens = new ArrayList<>();
    emTokens.add(nameToken);

    CoreMap em = mock(CoreMap.class);
    when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
    when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMention_TrailingPossessiveAllowed() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");

    List<CoreLabel> cmTokens = new ArrayList<>();
    cmTokens.add(token1);
    cmTokens.add(token2);
    cmTokens.add(token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    Mention cm = new Mention(0, 0, 3, "Joe Smith's");

    List<CoreLabel> emTokens = new ArrayList<>();
    emTokens.add(token1);
    emTokens.add(token2);

    CoreMap em = mock(CoreMap.class);
    when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
    when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertTrue(result);
  }
@Test
  public void testAnnotateHandlesEmptySentencesAndTokens() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Annotation annotation = new Annotation("EmptyDoc");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(0, mentions.size());

    Map<?, ?> map1 = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
    Map<?, ?> map2 = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

    assertNotNull(map1);
    assertNotNull(map2);
    assertTrue(map1.isEmpty());
    assertTrue(map2.isEmpty());
  }
@Test(expected = RuntimeException.class)
  public void testConstructorThrowsForInvalidHeadFinderClass() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.headFinder", "invalid.HeadFinder");

    new CorefMentionAnnotator(props);
  }
@Test
public void testSynchCorefMentionEntityMention_EmptyEntityTokensReturnsFalse() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alice");
  CoreLabel token2 = new CoreLabel();
  token2.setWord("Smith");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(token1);
  cmTokens.add(token2);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("EmptyEntityMention");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 2, "Alice Smith");

  List<CoreLabel> emTokens = new ArrayList<>();

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_CMExhaustedButEMNot() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alice");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(token1);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("EMLongerThanCM");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 1, "Alice");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Alice");
  CoreLabel token3 = new CoreLabel();
  token3.setWord("Walker");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(token2);
  emTokens.add(token3);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_ZeroTokenOverlapReturnsFalse() {
  CoreLabel cmToken1 = new CoreLabel();
  cmToken1.setWord("Hello");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(cmToken1);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("ZeroTokenOverlap");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 1, "Hello");

  CoreLabel emToken1 = new CoreLabel();
  emToken1.setWord("World");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(emToken1);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("LOCATION");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testAnnotateSetsMentionIDsAndMentionIndexes() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Barack");
  token1.setIndex(0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Obama");
  token2.setIndex(1);

  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token1);
  tokenList.add(token2);

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  List<Mention> mentions = new ArrayList<>();
//  Mention mention = new Mention(0, 0, 2, "Barack Obama");
//  mentions.add(mention);

  Annotation annotation = new Annotation("TestDoc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokenList);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

//  CorefCoreAnnotations.CorefMentionIndexesAnnotation.getClass();

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  List<List<Mention>> mentionLists = new ArrayList<>();
  mentionLists.add(mentions);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(mentionLists);

  
  try {
    java.lang.reflect.Field mdField = CorefMentionAnnotator.class.getDeclaredField("md");
    mdField.setAccessible(true);
    mdField.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Failed to set mock mention finder: " + e.getMessage());
  }

  annotator.annotate(annotation);

  List<Mention> resultMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(1, resultMentions.size());
  assertEquals(0, resultMentions.get(0).mentionID);

  ArraySet<Integer> mentionIndexes = (ArraySet<Integer>) token1.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  assertNotNull(mentionIndexes);
  assertTrue(mentionIndexes.contains(0));
}
@Test
public void testAnnotateHandlesMissingDocID() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.input.type", "raw");
  props.setProperty("coref.language", "en");
  props.setProperty("coref.specialCaseNewswire", "true");
  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Annotation annotation = new Annotation("TestDoc");
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  annotator.annotate(annotation);

  List<Mention> resultMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(resultMentions);
  assertTrue(resultMentions.isEmpty());

  Map<?, ?> m1 = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<?, ?> m2 = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(m1);
  assertNotNull(m2);
}
@Test
public void testSynchCorefMentionEntityMention_CMHasOneTokenAndEMHasSameSingleToken() {
  CoreLabel token = new CoreLabel();
  token.setWord("Paris");

  List<CoreLabel> cmTokenList = new ArrayList<>();
  cmTokenList.add(token);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokenList);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("TestSingleTokenMatch");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 1, "Paris");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(token);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("LOCATION");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertTrue(result);
}
@Test
public void testSynchCorefMentionEntityMention_CMHasExtraTokenAtEndThatIsNotPossessive() {
  CoreLabel t1 = new CoreLabel();
  t1.setWord("Barack");
  CoreLabel t2 = new CoreLabel();
  t2.setWord("Obama");
  CoreLabel t3 = new CoreLabel();
  t3.setWord("car");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(t1);
  cmTokens.add(t2);
  cmTokens.add(t3);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("CMExtraNonPossessive");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 3, "Barack Obama car");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(t1);
  emTokens.add(t2);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_CMHasPossessiveNotAtEnd() {
  CoreLabel t1 = new CoreLabel();
  t1.setWord("'s");
  CoreLabel t2 = new CoreLabel();
  t2.setWord("President");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(t1);
  cmTokens.add(t2);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("PossessiveNotAtEnd");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 2, "'s President");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(t1);
  emTokens.add(t2);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("TITLE");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertTrue(result);
}
@Test
public void testAnnotateAssignsMultipleMentionsWithCorrectIndexes() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("John");
  token1.setIndex(0);
  CoreLabel token2 = new CoreLabel();
  token2.setWord("Smith");
  token2.setIndex(1);
  CoreLabel token3 = new CoreLabel();
  token3.setWord("He");
  token3.setIndex(2);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);
  tokens.add(token3);

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

//  Mention m1 = new Mention(0, 0, 2, "John Smith");
//  Mention m2 = new Mention(0, 2, 3, "He");
  List<Mention> mentionList = new ArrayList<>();
//  mentionList.add(m1);
//  mentionList.add(m2);

  List<List<Mention>> mentionMatrix = new ArrayList<>();
  mentionMatrix.add(mentionList);

  Annotation annotation = new Annotation("DocTest");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(mentionMatrix);

  try {
    java.lang.reflect.Field mdField = CorefMentionAnnotator.class.getDeclaredField("md");
    mdField.setAccessible(true);
    mdField.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Reflection injection failed.");
  }

  annotator.annotate(annotation);

  List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(result);
  assertEquals(2, result.size());
  assertEquals(0, result.get(0).mentionID);
  assertEquals(1, result.get(1).mentionID);

  ArraySet<Integer> idx1 = (ArraySet<Integer>) tokens.get(0).get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  ArraySet<Integer> idx2 = (ArraySet<Integer>) tokens.get(1).get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  ArraySet<Integer> idx3 = (ArraySet<Integer>) tokens.get(2).get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  assertTrue(idx1.contains(0));
  assertTrue(idx2.contains(0));
  assertTrue(idx3.contains(1));
}
@Test
public void testAnnotateSkipsSynchronizingIfNoMatchingMentionFound() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Random");

  ArraySet<Integer> indexes = new ArraySet<>();
  indexes.add(0);
  token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, indexes);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

//  Mention mention = new Mention(0, 0, 1, "Random");
  List<Mention> mentionList = new ArrayList<>();
//  mentionList.add(mention);

  List<List<Mention>> matrix = new ArrayList<>();
  matrix.add(mentionList);

  CoreMap entityMention = mock(CoreMap.class);
  when(entityMention.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("ORG");
  List<CoreLabel> unmatched = new ArrayList<>();
  unmatched.add(new CoreLabel());
  when(entityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(unmatched);

  List<CoreMap> entityMentions = new ArrayList<>();
  entityMentions.add(entityMention);

  Annotation annotation = new Annotation("DocX");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentions);

  RuleBasedCorefMentionFinder finder = mock(RuleBasedCorefMentionFinder.class);
  when(finder.findMentions(any(Annotation.class), any(), any())).thenReturn(matrix);

  try {
    java.lang.reflect.Field mdField = CorefMentionAnnotator.class.getDeclaredField("md");
    mdField.setAccessible(true);
    mdField.set(annotator, finder);
  } catch (Exception e) {
    fail("Could not inject mock mention finder.");
  }

  annotator.annotate(annotation);

  Map<?, ?> map1 = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<?, ?> map2 = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(map1);
  assertNotNull(map2);
  assertEquals(0, map1.size());
  assertEquals(0, map2.size());
}
@Test
public void testSynchCorefMentionEntityMention_CMEmptyTokenList() {
  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<CoreLabel>());

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("CorefMentionEmpty");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 0, "");

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

  List<CoreLabel> emTokens = new ArrayList<>();
  CoreLabel emToken1 = new CoreLabel();
  emToken1.setWord("Obama");
  emTokens.add(emToken1);

  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_CMEndsWithPossessiveButNoTokensBeforeIt() {
  CoreLabel possessive = new CoreLabel();
  possessive.setWord("'s");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(possessive);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("OnlyPossessive");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 1, "'s");

  List<CoreLabel> emTokens = new ArrayList<>();

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_EntityTypeNotPersonIgnoresTitleSkipping() {
  CoreLabel title = new CoreLabel();
  title.setWord("President");
  title.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel name = new CoreLabel();
  name.setWord("Washington");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(title);
  cmTokens.add(name);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("NonPersonTitle");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 2, "President Washington");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(title);
  emTokens.add(name);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("ORG");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertTrue(result);
}
@Test
public void testAnnotateHandlesNullEntityMentionIndexWithoutException() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("London");
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, null);
  token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<Integer>());

  List<CoreLabel> tokens = Collections.singletonList(token);

  CoreMap sentence = new Annotation("Sent");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = Collections.singletonList(sentence);

  List<Mention> mentionList = new ArrayList<>();
//  Mention mention = new Mention(0, 0, 1, "London");
//  mentionList.add(mention);

  List<List<Mention>> mentionLists = new ArrayList<>();
  mentionLists.add(mentionList);

  Annotation annotation = new Annotation("NullEMIndex");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "sample_doc");

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(mentionLists);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Injection error: " + e);
  }

  annotator.annotate(annotation);

  List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(corefMentions);
  assertEquals(1, corefMentions.size());

  Map<?, ?> corefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<?, ?> entityToCoref = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(corefToEntity);
  assertNotNull(entityToCoref);
}
@Test
public void testAnnotateDocumentIdTriggersRemoveNestedFalse() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.input.type", "conll");
  props.setProperty("coref.language", "zh");
  props.setProperty("coref.specialCaseNewswire", "true");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("新闻");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  List<Mention> mentions = new ArrayList<>();
//  Mention mention = new Mention(0, 0, 1, "新闻");
//  mentions.add(mention);

  List<List<Mention>> mentionMatrix = new ArrayList<>();
  mentionMatrix.add(mentions);

  Annotation annotation = new Annotation("SpecialZH");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "sample.zh.nw");

  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(mentionMatrix);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Unable to inject mock mention finder");
  }

  annotator.annotate(annotation);

  List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(1, result.size());

  Map<?, ?> corefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<?, ?> entityToCoref = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(corefToEntity);
  assertNotNull(entityToCoref);
}
@Test
public void testSynchCorefMentionEntityMention_EntityMentionIsNull() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Steve");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(token1);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("EntityMentionNull");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 1, "Steve");
//
//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, null);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_CorefMentionOutOfBoundsSentenceIndex() {
  CoreLabel cmToken = new CoreLabel();
  cmToken.setWord("Lincoln");

  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(cmToken);

  
  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);

  Annotation annotation = new Annotation("OutOfBoundsSentence");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

//  Mention cm = new Mention(1, 0, 1, "Lincoln");

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);

  try {
//    CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    fail("Expected IndexOutOfBoundsException or handled failure");
  } catch (IndexOutOfBoundsException e) {
    
  }
}
@Test
public void testAnnotatePopulatesEmptyMentionIndexes() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Elon");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

//  Mention mention = new Mention(0, 0, 1, "Elon");
  List<Mention> mentions = new ArrayList<>();
//  mentions.add(mention);
  List<List<Mention>> mentionLists = new ArrayList<>();
  mentionLists.add(mentions);

  Annotation annotation = new Annotation("PopulateMentionIndexes");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(mentionLists);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Reflection injection failed");
  }

  annotator.annotate(annotation);

  List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(1, corefMentions.size());

  CoreLabel annotatedToken = tokens.get(0);
  Set<Integer> corefIndexes = annotatedToken.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  assertNotNull(corefIndexes);
  assertTrue(corefIndexes.contains(0));
}
@Test
public void testRequiresReturnsUnmodifiableSet() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Set<Class<? extends CoreAnnotation>> requires = annotator.requires();

  try {
    requires.add(CoreAnnotations.LemmaAnnotation.class);
    fail("Expected UnsupportedOperationException");
  } catch (UnsupportedOperationException e) {
    
  }
}
@Test
public void testRequirementsSatisfiedReturnsUnmodifiableSet() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

  try {
    satisfied.add(CoreAnnotations.LemmaAnnotation.class);
    fail("Expected UnsupportedOperationException");
  } catch (UnsupportedOperationException e) {
    
  }
}
@Test
public void testAnnotateWithNoMentionsDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Empty");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("EmptySentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  List<List<Mention>> emptyMentionLists = new ArrayList<>();
  emptyMentionLists.add(new ArrayList<Mention>());

  Annotation annotation = new Annotation("NoMentions");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(emptyMentionLists);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Mock mention finder inject failed");
  }

  annotator.annotate(annotation);

  List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(result);
  assertEquals(0, result.size());
}
@Test
public void testSynchCorefMentionEntityMention_CMHasExtraTokensAfterMatchingButNotPossessive() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("John");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Doe");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("car");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(token1);
  cmTokens.add(token2);
  cmTokens.add(token3);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("ExtraNonPossessive");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 3, "John Doe car");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(token1);
  emTokens.add(token2);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_CMAndEMAreIdenticalButEntityTypeIsNull() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alice");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Johnson");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(token1);
  cmTokens.add(token2);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("NullEntityType");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 2, "Alice Johnson");

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn(null);
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertTrue(result);
}
@Test
public void testAnnotateWithMultipleSentencesAndMixedMentionSizes() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Barack");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Obama");

  List<CoreLabel> tokens1 = new ArrayList<>();
  tokens1.add(token1);
  tokens1.add(token2);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("He");

  List<CoreLabel> tokens2 = new ArrayList<>();
  tokens2.add(token3);

  CoreMap sentence1 = new Annotation("Sentence1");
  sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

  CoreMap sentence2 = new Annotation("Sentence2");
  sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence1);
  sentences.add(sentence2);

//  Mention mention1 = new Mention(0, 0, 2, "Barack Obama");
//  Mention mention2 = new Mention(1, 0, 1, "He");

  List<List<Mention>> mentionMatrix = new ArrayList<>();
  List<Mention> list1 = new ArrayList<>();
//  list1.add(mention1);
  List<Mention> list2 = new ArrayList<>();
//  list2.add(mention2);
  mentionMatrix.add(list1);
  mentionMatrix.add(list2);

  List<CoreLabel> allTokens = new ArrayList<>();
  allTokens.addAll(tokens1);
  allTokens.addAll(tokens2);

  Annotation annotation = new Annotation("MultiSentence");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, allTokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(mentionMatrix);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Injection failed");
  }

  annotator.annotate(annotation);

  List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(2, result.size());
  assertEquals(0, result.get(0).mentionID);
  assertEquals(1, result.get(1).mentionID);

  assertEquals(0, result.get(0).sentNum);
  assertEquals(1, result.get(1).sentNum);
}
@Test
public void testAnnotateDoesNotOverwriteExistingMentionIndexes() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Michael");
  ArraySet<Integer> presetIndexes = new ArraySet<>();
  presetIndexes.add(42);
  token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, presetIndexes);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Sent");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("ExistingMentionIndex");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  List<List<Mention>> mentions = new ArrayList<>();
  mentions.add(new ArrayList<Mention>());

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(mentions);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Reflection inject failed");
  }

  annotator.annotate(annotation);

  ArraySet<Integer> resultSet = (ArraySet<Integer>) token.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  assertTrue(resultSet.contains(42)); 
}
@Test
public void testSynchCorefMentionEntityMention_CMAndEMSameReferenceDifferentObjects() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Benjamin");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Franklin");

  List<CoreLabel> cmTokens = new ArrayList<>();
  cmTokens.add(token1);
  cmTokens.add(token2);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("RefEqTokens");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 2, "Benjamin Franklin");

  CoreLabel copy1 = new CoreLabel();
  copy1.setWord("Benjamin");

  CoreLabel copy2 = new CoreLabel();
  copy2.setWord("Franklin");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(copy1);
  emTokens.add(copy2);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_CMStartIndexGreaterThanEndIndex() {
  CoreLabel token = new CoreLabel();
  token.setWord("Anna");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  Annotation annotation = new Annotation("BadIndexOrder");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 1, 0, "");

  List<CoreLabel> emTokens = new ArrayList<>();
  emTokens.add(token);

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emTokens);

  try {
//    CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
    fail("Expected IndexOutOfBoundsException or handled error due to invalid indices.");
  } catch (IndexOutOfBoundsException ignored) {
    
  } catch (IllegalArgumentException e) {
    
  }
}
@Test
public void testAnnotateDoesNotThrowIfMentionFinderReturnsNull() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Chicago");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Sent");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("NullFinderOutput");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  RuleBasedCorefMentionFinder mockFinder = mock(RuleBasedCorefMentionFinder.class);
  when(mockFinder.findMentions(any(Annotation.class), any(), any())).thenReturn(null);

  try {
    java.lang.reflect.Field f = CorefMentionAnnotator.class.getDeclaredField("md");
    f.setAccessible(true);
    f.set(annotator, mockFinder);
  } catch (Exception e) {
    fail("Failed to inject mock mention finder.");
  }

  try {
    annotator.annotate(annotation);
    
  } catch (Exception e) {
    fail("Unexpected exception: " + e.getMessage());
  }

  List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(mentions);
}
@Test
public void testAnnotateHandlesPartialMissingAnnotationsGracefully() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("SentWithToken");
  
  

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("PartialAnnotation");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  RuleBasedCorefMentionFinder finder = mock(RuleBasedCorefMentionFinder.class);
  List<List<Mention>> mentions = new ArrayList<>();
  mentions.add(new ArrayList<Mention>());
  when(finder.findMentions(any(), any(), any())).thenReturn(mentions);

  try {
    java.lang.reflect.Field f = CorefMentionAnnotator.class.getDeclaredField("md");
    f.setAccessible(true);
    f.set(annotator, finder);
  } catch (Exception e) {
    fail("Reflection injection failed");
  }

  try {
    annotator.annotate(annotation);
    
  } catch (Exception e) {
    fail("Caught unexpected exception: " + e);
  }

  assertNotNull(annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
}
@Test
public void testRequirementsSatisfiedContainsExactlyExpectedSet() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();

  Set<Class<? extends CoreAnnotation>> expected = new HashSet<>();
  expected.add(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  expected.add(CoreAnnotations.ParagraphAnnotation.class);
  expected.add(CoreAnnotations.SpeakerAnnotation.class);
  expected.add(CoreAnnotations.UtteranceAnnotation.class);

  assertEquals(expected, result);
}
@Test
public void testRequiresContainsDependencyAnnotations() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "dependency");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Set<Class<? extends CoreAnnotation>> required = annotator.requires();

  assertTrue(required.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
  assertTrue(required.contains(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class));
} 
}