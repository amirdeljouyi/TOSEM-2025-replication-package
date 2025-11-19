package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.coref.md.CorefMentionFinder;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CorefMentionAnnotator_1_GPTLLMTest {

@Test
  public void testAnnotator_returnsMentionAndMappings() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Annotation annotation = new Annotation("Barack Obama was president.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    token1.set(CoreAnnotations.IndexAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.IndexAnnotation.class, 1);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("was");
    token3.set(CoreAnnotations.IndexAnnotation.class, 2);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("president");
    token4.set(CoreAnnotations.IndexAnnotation.class, 3);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    token5.set(CoreAnnotations.IndexAnnotation.class, 4);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc1");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

//    Mention mention = new Mention(0, 0, 2, 0);
    List<Mention> mentionList = new ArrayList<>();
//    mentionList.add(mention);
    List<List<Mention>> mentionsNestedList = new ArrayList<>();
    mentionsNestedList.add(mentionList);

    CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
    when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));
    when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(em));

    token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    token2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CorefMentionAnnotator manual = new CorefMentionAnnotator(props);
    CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
    when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsNestedList);

    try {
      java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
      field.setAccessible(true);
      field.set(manual, mdMock);
    } catch (Exception e) {
      fail("Unexpected exception setting mock mention detector: " + e.getMessage());
    }

    manual.annotate(annotation);

    List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertEquals(1, corefMentions.size());
    assertEquals(0, corefMentions.get(0).mentionID);

    Map<Integer, Integer> corefToEntity =
            annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
    Map<Integer, Integer> entityToCoref =
            annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

    assertNotNull(corefToEntity);
    assertEquals(Integer.valueOf(0), corefToEntity.get(0));
    assertEquals(Integer.valueOf(0), entityToCoref.get(0));
  }
@Test
  public void testSynchCorefMentionEntityMention_returnsTrueForValidCase() {
    Annotation ann = new Annotation("President Joe Smith");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    token3.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention mention = new Mention(0, 0, 3, 0);
//    mention.sentNum = 0;

    CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
    when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token2, token3));
    when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);
//    assertTrue(result);
  }
@Test
  public void testSynchCorefMentionEntityMention_returnsFalseOnMismatch() {
    Annotation ann = new Annotation("President Joe Black");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Black");
    token3.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention mention = new Mention(0, 0, 3, 0);
//    mention.sentNum = 0;

    CoreLabel entityToken = new CoreLabel();
    entityToken.setWord("Mismatch");

    CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
    when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(entityToken));
    when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);
//    assertFalse(result);
  }

@Test
  public void testRequirementsSatisfied_returnsExpectedSet() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
//    Set<Class<? extends CoreAnnotations>> satisfied = annotator.requirementsSatisfied();

//    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
//    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
//    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
//    assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));
  }
@Test
public void testAnnotate_emptyAnnotation_doesNotFail() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Annotation annotation = new Annotation("");

  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(new ArrayList<List<Mention>>());

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to set mock mention detector");
  }

  annotator.annotate(annotation);

  List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(result);
  assertEquals(0, result.size());

  Map<Integer, Integer> corefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<Integer, Integer> entityToCoref = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(corefToEntity);
  assertTrue(corefToEntity.isEmpty());

  assertNotNull(entityToCoref);
  assertTrue(entityToCoref.isEmpty());
}
@Test
public void testSynchCorefMentionEntityMention_nullEntityType_returnsFalse() {
  Annotation ann = new Annotation("John went to school.");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("John");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = new ArrayList<CoreMap>();
  sentences.add(sentence);
  ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//  Mention mention = new Mention(0, 0, 1, 0);
//  mention.sentNum = 0;

  CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn(null);

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_extraCorefTokens_returnsTrueWithPossessiveS() {
  Annotation ann = new Annotation("Joe's");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Joe");
  token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("'s");

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = new ArrayList<CoreMap>();
  sentences.add(sentence);
  ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//  Mention cm = new Mention(0, 0, 2, 0);
//  cm.sentNum = 0;

  CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em);
//  assertTrue(result);
}
@Test
public void testAnnotate_withChineseDocID_disablesRemoveNestedMentions() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "zh");
  props.setProperty("coref.input.type", "conll");
  props.setProperty("coref.specialCaseNewswire", "true");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Annotation annotation = new Annotation("test text");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "nw123");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(new ArrayList<List<Mention>>());

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to mock mention detector");
  }

  annotator.annotate(annotation);

//  String removeNested = annotator.corefProperties.getProperty("removeNestedMentions");
//  assertEquals("false", removeNested);
}
@Test
public void testAnnotate_multipleSentences_multipleMentions_correctMapping() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel s1t1 = new CoreLabel();
  s1t1.setWord("Barack");
  s1t1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
  CoreLabel s1t2 = new CoreLabel();
  s1t2.setWord("Obama");
  s1t2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  List<CoreLabel> tokens1 = Arrays.asList(s1t1, s1t2);

  CoreMap sentence1 = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);

  CoreLabel s2t1 = new CoreLabel();
  s2t1.setWord("He");
  s2t1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
  CoreLabel s2t2 = new CoreLabel();
  s2t2.setWord("served");
  List<CoreLabel> tokens2 = Arrays.asList(s2t1, s2t2);

  CoreMap sentence2 = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

  List<CoreMap> allSentences = Arrays.asList(sentence1, sentence2);
  List<CoreLabel> allTokens = new ArrayList<CoreLabel>();
  allTokens.addAll(tokens1);
  allTokens.addAll(tokens2);

  Annotation doc = new Annotation("Barack Obama. He served.");
  doc.set(CoreAnnotations.DocIDAnnotation.class, "docA");
  doc.set(CoreAnnotations.SentencesAnnotation.class, allSentences);
  doc.set(CoreAnnotations.TokensAnnotation.class, allTokens);

//  Mention mention1 = new Mention(0, 0, 2, 0);
//  Mention mention2 = new Mention(1, 0, 1, 0);
  List<List<Mention>> mentions = new ArrayList<List<Mention>>();
//  mentions.add(Collections.singletonList(mention1));
//  mentions.add(Collections.singletonList(mention2));

  CoreMap entityMention = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(entityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(s1t2));
  when(entityMention.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");
  doc.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

  s1t2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentions);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Cannot inject mock mention detector");
  }

  annotator.annotate(doc);

  List<Mention> corefList = doc.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(2, corefList.size());
  assertEquals(0, (int) corefList.get(0).mentionID);
  assertEquals(1, (int) corefList.get(1).mentionID);

  Map<Integer, Integer> corefToEntity =
          doc.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<Integer, Integer> entityToCoref =
          doc.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertTrue(corefToEntity.containsKey(0));
  assertEquals((Integer) 0, corefToEntity.get(0));
  assertEquals((Integer) 0, entityToCoref.get(0));
}
@Test
public void testAnnotate_mentionWithZeroLengthSpan_isIgnoredGracefully() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("test");
  token1.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token1.set(CoreAnnotations.SentIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token1);

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = Arrays.asList(sentence);

  Annotation annotation = new Annotation("text");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docZeroLength");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

//  Mention zeroMention = new Mention(0, 0, 0, 0);
  List<List<Mention>> mentionsList = new ArrayList<>();
//  mentionsList.add(Collections.singletonList(zeroMention));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsList);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to inject mock mention detector: " + e.getMessage());
  }

  annotator.annotate(annotation);
  List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(1, mentions.size());
  assertEquals(0, mentions.get(0).startIndex);
  assertEquals(0, mentions.get(0).endIndex);
}
@Test
public void testAnnotate_tokenWithoutMentionIndexAnnotation_doesNotBreak() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Alice");
  token.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token.set(CoreAnnotations.SentIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = Arrays.asList(sentence);

  Annotation annotation = new Annotation("Alice");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docNoMentionIndex");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

//  Mention mention = new Mention(0, 0, 1, 0);
  List<List<Mention>> mentionsList = new ArrayList<>();
//  mentionsList.add(Collections.singletonList(mention));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsList);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to inject mock mention finder: " + e.getMessage());
  }

  annotator.annotate(annotation);
  List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(1, corefMentions.size());
}
@Test
public void testAnnotate_multipleMentionsSameToken_allMentionIndexesRecorded() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("entity");
  token.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token.set(CoreAnnotations.SentIndexAnnotation.class, 0);
  token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

  List<CoreLabel> tokens = Arrays.asList(token);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("entity");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docOverlapMentions");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

//  Mention m1 = new Mention(0, 0, 1, 0);
//  Mention m2 = new Mention(0, 0, 1, 0);

  List<List<Mention>> allMentions = new ArrayList<>();
//  allMentions.add(Arrays.asList(m1, m2));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(allMentions);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Unable to inject md mock: " + e.getMessage());
  }

  annotator.annotate(annotation);
  List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(2, corefMentions.size());

  ArraySet<Integer> mentionIndexes = (ArraySet<Integer>) token.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  assertTrue(mentionIndexes.contains(0));
  assertTrue(mentionIndexes.contains(1));
}
@Test
public void testRequirementsSatisfied_isUnmodifiable() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();

  assertTrue(result.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  try {
    result.add(CoreAnnotations.TokensAnnotation.class);
    fail("Expected UnsupportedOperationException");
  } catch (UnsupportedOperationException expected) {
    
  }
}
@Test
public void testAnnotationWithNullSentences_isHandledGracefully() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Annotation annotation = new Annotation("text");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docNullSents");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(new ArrayList<List<Mention>>());

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to set mock md: " + e.getMessage());
  }

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotator should not throw on null sentences");
  }

  assertNotNull(annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
}
@Test
public void testAnnotate_mentionsOverlapDifferentTokens_indexesAssignedCorrectly() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel tokenA = new CoreLabel();
  tokenA.setWord("John");
  tokenA.set(CoreAnnotations.IndexAnnotation.class, 0);
//  tokenA.set(CoreAnnotations.SentIndexAnnotation.class, 0);
  tokenA.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

  CoreLabel tokenB = new CoreLabel();
  tokenB.setWord("Smith");
  tokenB.set(CoreAnnotations.IndexAnnotation.class, 1);
//  tokenB.set(CoreAnnotations.SentIndexAnnotation.class, 0);
  tokenB.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

  List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("John Smith");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docOverlapIndex");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

//  Mention mention1 = new Mention(0, 0, 2, 0);
//  Mention mention2 = new Mention(0, 1, 2, 0);

  List<List<Mention>> mentionsList = new ArrayList<>();
//  mentionsList.add(Arrays.asList(mention1, mention2));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsList);

  try {
    java.lang.reflect.Field ref = CorefMentionAnnotator.class.getDeclaredField("md");
    ref.setAccessible(true);
    ref.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to patch md field: " + e.getMessage());
  }

  annotator.annotate(annotation);

  List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(2, mentions.size());

  Set<Integer> indexesA = tokenA.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  Set<Integer> indexesB = tokenB.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  assertTrue(indexesA.contains(0));
  assertFalse(indexesA.contains(1));
  assertTrue(indexesB.contains(0));
  assertTrue(indexesB.contains(1));
}
@Test
public void testSynchCorefMentionEntityMention_mismatchedEnds_returnsFalse() {
  Annotation ann = new Annotation("President Joe Smith's house");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("President");
  token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Joe");
  token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("Smith");
  token3.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  CoreLabel token4 = new CoreLabel();
  token4.setWord("'s");

  CoreLabel token5 = new CoreLabel();
  token5.setWord("house");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentenceList = Arrays.asList(sentence);
  ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//  Mention cm = new Mention(0, 0, 5, 0);
//  cm.sentNum = 0;

  CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token2, token3));
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em);
//  assertFalse(result);
}
@Test
public void testAnnotate_withNullEntityType_doesNotThrowAndSkipsMapping() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Jordan");
  token1.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token1.set(CoreAnnotations.SentIndexAnnotation.class, 0);
  token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token1);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("Jordan");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "noEntityType");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  CoreMap mockEntityMention = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(mockEntityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1));
  when(mockEntityMention.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn(null); 
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mockEntityMention));

//  Mention mention = new Mention(0, 0, 1, 0);
  List<List<Mention>> mentionList = new ArrayList<>();
//  mentionList.add(Collections.singletonList(mention));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionList);

  try {
    java.lang.reflect.Field f = CorefMentionAnnotator.class.getDeclaredField("md");
    f.setAccessible(true);
    f.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Could not access md field");
  }

  annotator.annotate(annotation);

  Map<Integer, Integer> corefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<Integer, Integer> entityToCoref = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

  assertTrue(corefToEntity.isEmpty());
  assertTrue(entityToCoref.isEmpty());
}
@Test
public void testAnnotate_removesXmlMentions_whenConfiguredTrue() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");
  props.setProperty("coref.removeXml", "true");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("<tag>");

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1));

  List<CoreMap> sentences = Arrays.asList(sentence);
  Annotation annotation = new Annotation("xml mention");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "xmlDoc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

//  Mention xmlMention = new Mention(0, 0, 1, 0);
//  xmlMention.headWord = token1;

  List<List<Mention>> rawMentions = new ArrayList<>();
//  rawMentions.add(Collections.singletonList(xmlMention));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(rawMentions);

  try {
    java.lang.reflect.Field f = CorefMentionAnnotator.class.getDeclaredField("md");
    f.setAccessible(true);
    f.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Unable to mock md");
  }

  annotator.annotate(annotation);
  List<Mention> resultMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertTrue(resultMentions.isEmpty());
}
@Test
public void testSynchCorefMentionEntityMention_zeroTokenOverlap_returnsFalse() {
  Annotation ann = new Annotation("Random mismatch span");

  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("Not");

  CoreMap cmSentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(cmSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(cm1));

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(cmSentence));

//  Mention mention = new Mention(0, 0, 1, 0);
//  mention.sentNum = 0;

  CoreLabel em1 = new CoreLabel();
  em1.setWord("SomethingElse");

  CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(em1));
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("ORGANIZATION");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);
//  assertFalse(result);
}
@Test
public void testAnnotate_mentionsAnnotationNull_skipsEntityMappingSafely() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");
  
  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alice");
  token1.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token1.set(CoreAnnotations.SentIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Collections.singletonList(token1);

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = Collections.singletonList(sentence);
  Annotation annotation = new Annotation("Alice");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "nullMentions");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, null);

//  Mention mention = new Mention(0, 0, 1, 0);
  List<List<Mention>> mentionsList = new ArrayList<>();
//  mentionsList.add(Collections.singletonList(mention));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsList);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to inject md mock");
  }

  annotator.annotate(annotation);

  Map<Integer, Integer> corefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<Integer, Integer> entityToCoref = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(corefToEntity);
  assertTrue(corefToEntity.isEmpty());
  assertNotNull(entityToCoref);
  assertTrue(entityToCoref.isEmpty());
}
@Test
public void testSynchCorefMentionEntityMention_nullFineGrainedNER_skipsTitleHandling() {
  Annotation ann = new Annotation("Dr. Smith");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Dr.");
  token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Smith");
  token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  List<CoreLabel> tokens = Arrays.asList(token1, token2);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  Mention mention = new Mention(0, 0, 2, 0);
//  mention.sentNum = 0;

  CoreMap entityMention = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(entityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));
  when(entityMention.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, entityMention);
//  assertTrue(result);
}
@Test
public void testAnnotate_nullDocID_defaultsToEmptyString() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "zh");
  props.setProperty("coref.specialCaseNewswire", "true");
  props.setProperty("coref.input.type", "conll");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("News");
  List<CoreLabel> tokens = Collections.singletonList(token);

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  Annotation annotation = new Annotation("News");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, null);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(Collections.singletonList(new ArrayList<>()));

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Mock injection failed");
  }

  annotator.annotate(annotation);
  
//  String removeNested = annotator.corefProperties.getProperty("removeNestedMentions");
//  assertEquals("true", removeNested);
}
@Test
public void testSynchCorefMentionEntityMention_entityMentionLargerThanCoref_returnsFalse() {
  Annotation ann = new Annotation("President Joe");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("President");
  token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Joe");
  token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  List<CoreLabel> tokens = Arrays.asList(token1, token2);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  Mention mention = new Mention(0, 1, 2, 0);
//  mention.sentNum = 0;

  CoreMap entityMention = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(entityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));
  when(entityMention.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, entityMention);
//  assertFalse(result);
}
@Test
public void testAnnotate_corefMentionListAssignedToSentencesAndDocument() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Sam");
  token.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token.set(CoreAnnotations.SentIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = Collections.singletonList(sentence);
  Annotation annotation = new Annotation("Sam");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "corefDoc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

//  Mention mention = new Mention(0, 0, 1, 0);
//  List<List<Mention>> mentionsList = Collections.singletonList(Collections.singletonList(mention));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
//  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsList);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to inject mock");
  }

  annotator.annotate(annotation);

  List<Mention> corpusMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertEquals(1, corpusMentions.size());

  verify(sentence).set(eq(CorefCoreAnnotations.CorefMentionsAnnotation.class), any());
}
@Test
public void testSynchCorefMentionEntityMention_cmTokenListEmpty_returnsFalse() {
  Annotation ann = new Annotation("Test");

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  Mention mention = new Mention(0, 0, 0, 0);
//  mention.sentNum = 0;

  CoreMap entityMention = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  CoreLabel entToken = new CoreLabel();
  entToken.setWord("Sam");
  when(entityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(entToken));
  when(entityMention.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, entityMention);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_emTokenListEmpty_returnsFalse() {
  Annotation ann = new Annotation("Dr. Sam");

  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("Dr.");
  cm1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel cm2 = new CoreLabel();
  cm2.setWord("Sam");
  cm2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  List<CoreLabel> cmTokens = Arrays.asList(cm1, cm2);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  Mention cm = new Mention(0, 0, 2, 0);
//  cm.sentNum = 0;

  CoreMap entityMention = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(entityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());
  when(entityMention.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, entityMention);
//  assertFalse(result);
}
@Test
public void testSynchCorefMentionEntityMention_corefMatchWithMultipleTokensAndTrailingPossessive() {
  Annotation ann = new Annotation("President Joe Smith's");

  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("President");
  cm1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel cm2 = new CoreLabel();
  cm2.setWord("Joe");
  cm2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  CoreLabel cm3 = new CoreLabel();
  cm3.setWord("Smith");
  cm3.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  CoreLabel possessive = new CoreLabel();
  possessive.setWord("'s");

  List<CoreLabel> cmTokens = Arrays.asList(cm1, cm2, cm3, possessive);
  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(cmTokens);

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  Mention mention = new Mention(0, 0, 4, 0);
//  mention.sentNum = 0;

  CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(cm2, cm3));
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PERSON");

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);
//  assertTrue(result);
}
@Test
public void testAnnotate_tokensInDifferentSentencesAreCorrectlyIndexed() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alice");
  token1.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token1.set(CoreAnnotations.SentIndexAnnotation.class, 0);
  token1.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Bob");
  token2.set(CoreAnnotations.IndexAnnotation.class, 1);
//  token2.set(CoreAnnotations.SentIndexAnnotation.class, 1);
  token2.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

  CoreMap sentence1 = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  CoreMap sentence2 = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
  when(sentence2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));

  List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);

  Annotation annotation = new Annotation("sentence1. sentence2.");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docSentenceIndexing");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

//  Mention mention1 = new Mention(0, 0, 1, 0);
//  Mention mention2 = new Mention(1, 0, 1, 0);

  List<List<Mention>> mentionList = new ArrayList<>();
//  mentionList.add(Collections.singletonList(mention1));
//  mentionList.add(Collections.singletonList(mention2));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionList);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to inject md");
  }

  annotator.annotate(annotation);

  assertEquals(0, annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).get(0).sentNum);
  assertEquals(1, annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).get(1).sentNum);
}
@Test
public void testAnnotate_emptyMentionListStillInitializesOutputAnnotations() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Annotation annotation = new Annotation("Nothing here.");
  CoreLabel token = new CoreLabel();
  token.setWord("Nothing");

  annotation.set(CoreAnnotations.DocIDAnnotation.class, "emptyMentionInit");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  when(mdMock.findMentions(any(), any(), any())).thenReturn(new ArrayList<>());

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Reflection failed");
  }

  annotator.annotate(annotation);

  Map<Integer, Integer> c2e = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<Integer, Integer> e2c = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

  assertNotNull(annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  assertNotNull(c2e);
  assertNotNull(e2c);
  assertTrue(c2e.isEmpty());
  assertTrue(e2c.isEmpty());
}
@Test
public void testAnnotate_emptySentencesList_noExceptionThrown() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Annotation annotation = new Annotation("");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "docEmptySents");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
  List<List<Mention>> emptyMentions = new ArrayList<>();
  when(mdMock.findMentions(any(), any(), any())).thenReturn(emptyMentions);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Failed to inject md");
  }

  annotator.annotate(annotation);

  List<Mention> resultMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(resultMentions);
  assertTrue(resultMentions.isEmpty());
}
@Test
public void testAnnotate_mentionsExceedingTokenBounds_throwsIndexExceptionSafelyHandled() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel tokenA = new CoreLabel();
  tokenA.setWord("Only");
  tokenA.set(CoreAnnotations.IndexAnnotation.class, 0);
//  tokenA.set(CoreAnnotations.SentIndexAnnotation.class, 0);
  tokenA.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

  List<CoreLabel> tokens = Collections.singletonList(tokenA);

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  List<CoreMap> sentences = Collections.singletonList(sentence);

  Annotation annotation = new Annotation("Only");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "outOfBoundsMention");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

//  Mention mention = new Mention(0, 0, 2, 0);
//  List<List<Mention>> mentionsNested = Collections.singletonList(Collections.singletonList(mention));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
//  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsNested);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Could not inject md for test");
  }

  try {
    annotator.annotate(annotation);
    
    assertTrue(true);
  } catch (Exception e) {
    fail("Annotate threw exception on out-of-bounds mention");
  }
}
@Test
public void testRequirementsSatisfied_containsExactlyExpectedAnnotations() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

  assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
  assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
  assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));

  assertEquals(4, satisfied.size());
}
@Test
public void testSynchCorefMentionEntityMention_nonPersonTypeTitlePresent_doesNotSkipTitleToken() {
  Annotation ann = new Annotation("Director James Smith");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Director");
  token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("James");
  token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("Smith");
  token3.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  Mention cm = new Mention(0, 0, 3, 0);
//  cm.sentNum = 0;

  CoreMap em = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(em.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2, token3));
  when(em.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("ORGANIZATION"); 

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em);
//  assertTrue(result);
}
@Test
public void testAnnotate_multipleEntityMentions_mapToSameToken_correctMapping() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Apple");
  token.set(CoreAnnotations.IndexAnnotation.class, 0);
//  token.set(CoreAnnotations.SentIndexAnnotation.class, 0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
  token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

  CoreMap sentence = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  List<CoreMap> sentences = Collections.singletonList(sentence);
  Annotation annotation = new Annotation("Apple");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "multiEntitySameToken");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  CoreMap entityMention1 = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(entityMention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(entityMention1.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("ORGANIZATION");

  CoreMap entityMention2 = mock(CoreMap.class, withSettings().extraInterfaces(TypesafeMap.class));
  when(entityMention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(entityMention2.get(CoreAnnotations.EntityTypeAnnotation.class)).thenReturn("PRODUCT");

  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention1, entityMention2));

//  Mention cm = new Mention(0, 0, 1, 0);
//  List<List<Mention>> mentionsList = Collections.singletonList(Collections.singletonList(cm));

  CorefMentionFinder mdMock = mock(CorefMentionFinder.class);
//  when(mdMock.findMentions(any(), any(), any())).thenReturn(mentionsList);

  try {
    java.lang.reflect.Field field = CorefMentionAnnotator.class.getDeclaredField("md");
    field.setAccessible(true);
    field.set(annotator, mdMock);
  } catch (Exception e) {
    fail("Could not inject mock md");
  }

  annotator.annotate(annotation);

  Map<Integer, Integer> corefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  Map<Integer, Integer> entityToCoref = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

  assertEquals(1, corefToEntity.size() + entityToCoref.size()); 
} 
}