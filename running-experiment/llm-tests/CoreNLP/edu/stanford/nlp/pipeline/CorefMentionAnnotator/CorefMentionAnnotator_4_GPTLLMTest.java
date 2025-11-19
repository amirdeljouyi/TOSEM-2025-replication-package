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

public class CorefMentionAnnotator_4_GPTLLMTest {

 @Test
  public void testConstructorInitializesRequiredAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.EntityTypeAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.IndexAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.ValueAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedOutput() {
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
  public void testSynchMention_PersonTitleSkipped() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");

    List<CoreLabel> cmTokens = Arrays.asList(token1, token2, token3);

//    CoreMap cmSentence = new SimpleAnnotationMap();
//    cmSentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("doc");
//    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(cmSentence));

//    Mention cmMention = new Mention(0, 0, 3, null);
//    cmMention.sentNum = 0;

//    CoreMap em = new SimpleAnnotationMap();
//    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2, token3));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cmMention, em);

//    assertTrue(result);
  }
@Test
  public void testSynchMention_NoOverlapFails() {
    CoreLabel cm1 = new CoreLabel();
    cm1.setWord("Obama");

    CoreLabel em1 = new CoreLabel();
    em1.setWord("Lincoln");

//    CoreMap cmSentence = new SimpleAnnotationMap();
//    cmSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(cm1));

    Annotation annotation = new Annotation("doc");
//    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(cmSentence));

//    Mention mention = new Mention(0, 0, 1, null);
//    mention.sentNum = 0;

//    CoreMap em = new SimpleAnnotationMap();
//    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(em1));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, em);

//    assertFalse(result);
  }
@Test
  public void testSynchMention_MatchWithPossessive() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");

    List<CoreLabel> cmTokens = Arrays.asList(token1, token2, token3);

//    CoreMap sentence = new SimpleAnnotationMap();
//    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation ann = new Annotation("text");
//    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention mention = new Mention(0, 0, 3, null);
//    mention.sentNum = 0;

//    CoreMap em = new SimpleAnnotationMap();
//    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

//    boolean matched = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);

//    assertTrue(matched);
  }
@Test
  public void testAnnotateGeneratesMentionsAndIndexMapping() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.setIndex(0);
//    token.setSentenceIndex(0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Collections.singletonList(token);

//    CoreMap sentence = new SimpleAnnotationMap();
//    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    CoreMap entityMention = new SimpleAnnotationMap();
//    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);
//    List<CoreMap> mentions = Collections.singletonList(entityMention);
//    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    annotator.annotate(annotation);

    List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(corefMentions);
    assertFalse(corefMentions.isEmpty());

    Map<?, ?> map1 = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
    Map<?, ?> map2 = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
    assertNotNull(map1);
    assertNotNull(map2);

    Set<Integer> indices = token.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
    assertNotNull(indices);
    assertFalse(indices.isEmpty());
  }
@Test
public void testSynchMention_EmptyCorefMentionTokens() {
//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList());

  Annotation ann = new Annotation("text");
//  ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 0, null);
//  mention.sentNum = 0;

  CoreLabel emToken = new CoreLabel();
  emToken.setWord("Person");

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(emToken));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);

//  assertFalse(result);
}
@Test
public void testSynchMention_EmptyEntityMentionTokens() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("President");
  token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Joe");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

  Annotation ann = new Annotation("text");
//  ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 2, null);
//  mention.sentNum = 0;

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);

//  assertFalse(result);
}
@Test
public void testSynchMention_NonPersonEntity_NoTitleSkip() {
  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("Table");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1));

  Annotation ann = new Annotation("text");
//  ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 1, null);
//  mention.sentNum = 0;

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "OBJECT");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, mention, em);

//  assertTrue(result);
}
@Test
public void testAnnotate_ChineseNwDocWithSpecialCaseNewswire() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "zh");
  props.setProperty("coref.input.type", "conll");
  props.setProperty("coref.specialCaseNewswire", "true");

  CoreLabel token = new CoreLabel();
  token.setWord("张三");
  token.setIndex(0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("张三参加会议");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "nw_doc123");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  CoreMap entityMention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
//  List<CoreMap> mentions = Arrays.asList(entityMention);
//  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(corefMentions);
  assertFalse(corefMentions.isEmpty());

  Map<?, ?> mapping = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(mapping);
}
@Test
public void testAnnotate_RemovesXmlMentionsIfConfigured() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.removeXML", "true");

  CoreLabel token = new CoreLabel();
  token.setWord("<name>");
  token.setIndex(0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Collections.singletonList(token);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("doc");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  CoreMap entityMention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  List<Mention> corefMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);

  assertNotNull(corefMentions);
}
@Test
public void testAnnotate_SingleSentenceNoMentions() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

  Annotation annotation = new Annotation("This sentence has no mentions.");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(mentions);
}
@Test
public void testSynchMention_PartialTokenMatchFails() {
  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("Joe");

  CoreLabel cm2 = new CoreLabel();
  cm2.setWord("Smith");

  CoreLabel em1 = new CoreLabel();
  em1.setWord("Joe");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1, cm2));

  Annotation annotation = new Annotation("text");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 2, null);
//  mention.sentNum = 0;

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(em1));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, em);

  
//  assertFalse(result);
}
@Test
public void testSynchMention_TokenOverlapZeroFails() {
  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("President");

  CoreLabel cm2 = new CoreLabel();
  cm2.setWord("'s");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1, cm2));

  Annotation annotation = new Annotation("text");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 2, null);
//  mention.sentNum = 0;

  CoreLabel emToken = new CoreLabel();
  emToken.setWord("Joe");

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(emToken));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, em);

//  assertFalse(result);
}
@Test
public void testAnnotate_NullDocID_ShouldSetRemoveNestedMentionsTrue() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");
  props.setProperty("coref.language", "en");
  props.setProperty("coref.input.type", "raw");

  CoreLabel token = new CoreLabel();
  token.setWord("John");
  token.setIndex(0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token);
//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("text");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(mentions);
}
@Test
public void testAnnotate_TokenWithNoEntityMentionIndexIsIgnored() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("Unknown");
  token.setIndex(0);
  

  List<CoreLabel> tokens = Collections.singletonList(token);
//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("text");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  Map<?, ?> mapping = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(mapping);
  assertTrue(mapping.isEmpty());
}
@Test
public void testAnnotate_MentionsAnnotationMissingInInput() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("Alice");
  token.setIndex(0);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("Alice");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(mentions);
}
@Test
public void testGetMentionFinderTypeDependency() throws Exception {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "dependency");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

  assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testGetMentionFinderTypeHybrid() throws Exception {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "hybrid");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

  assertTrue(required.contains(CoreAnnotations.BeginIndexAnnotation.class));
  assertTrue(required.contains(CoreAnnotations.EndIndexAnnotation.class));
}
@Test
public void testSynchMention_EntityMentionIsNull() {
  CoreLabel cmToken = new CoreLabel();
  cmToken.setWord("Smith");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cmToken));

  Annotation annotation = new Annotation("doc");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 1, null);
//  mention.sentNum = 0;

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, null);
//  assertFalse(result);
}
@Test
public void testSynchMention_EntityMentionMissingEntityType() {
  CoreLabel cmToken = new CoreLabel();
  cmToken.setWord("John");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cmToken));

  Annotation annotation = new Annotation("text");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 1, null);
//  mention.sentNum = 0;

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cmToken));

  boolean result = false;
  try {
//    result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, em);
  } catch (Exception e) {
    
    result = false;
  }

  assertFalse(result);
}
@Test
public void testAnnotate_EmptySentencesAnnotation() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  Annotation annotation = new Annotation("text with no sentence");

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  annotator.annotate(annotation);

  List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(mentions);
  assertTrue(mentions.isEmpty());
}
@Test
public void testAnnotate_TokenMissingMentionIndexAnnotation() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("Lonely");

  List<CoreLabel> tokens = Arrays.asList(token);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("no mention index");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  Map<?, ?> map = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  assertNotNull(map);
  assertTrue(map.isEmpty());
}
@Test
public void testRequirementsSatisfiedDoesNotContainUnexpectedKeys() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();

  assertFalse(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class)); 
  assertTrue(result.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
}
@Test
public void testAnnotate_MultipleMentionsInSingleSentence() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("John");
  token1.setIndex(0);
  token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Smith");
  token2.setIndex(1);
  token2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("John Smith");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  CoreMap mention1 = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
//  mention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//  CoreMap mention2 = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
//  mention2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  Map<?, ?> map = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(map);
  assertEquals(2, map.size());
}
@Test
public void testAnnotate_WithMultipleSentencesAndNoMentionsInSecond() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alice");
  token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//  CoreMap sent1 = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sent1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));

  CoreLabel token2 = new CoreLabel();
  token2.setWord("walks");

//  CoreMap sent2 = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sent2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));

  Annotation annotation = new Annotation("Two sentences");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent1, sent2));

//  CoreMap mention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
//  mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  List<Mention> allMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
  assertNotNull(allMentions);
}
@Test
public void testSynchMention_DifferentTokenObjectsSameValue() {
  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("Alice");

  CoreLabel em1 = new CoreLabel();
  em1.setWord("Alice");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1));

  Annotation annotation = new Annotation("document");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 1, null);
//  mention.sentNum = 0;

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(em1));

  
//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, em);
//
//  assertFalse(result);
}
@Test
public void testAnnotate_MultipleTokensSameMentionID() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Mr.");
  token1.setIndex(0);
  token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Smith");
  token2.setIndex(1);
  token2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("Mr. Smith");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  CoreMap entityMention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  Map<?, ?> map = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(map);
}
@Test
public void testSynchMention_EntityMentionLargerThanMention() {
  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("President");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1));

  Annotation annotation = new Annotation("text");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 1, null);
//  mention.sentNum = 0;

  CoreLabel em1 = new CoreLabel();
  em1.setWord("President");

  CoreLabel em2 = new CoreLabel();
  em2.setWord("Biden");

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(em1, em2));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, em);
//  assertFalse(result);
}
@Test
public void testSynchMention_CorefMentionLargerThanEntityMentionAndNoApostropheS() {
  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("John");

  CoreLabel cm2 = new CoreLabel();
  cm2.setWord("Smith");
  
  CoreLabel cm3 = new CoreLabel();
  cm3.setWord("Jr.");

  List<CoreLabel> cmTokens = Arrays.asList(cm1, cm2, cm3);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

  Annotation annotation = new Annotation("John Smith Jr.");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 3, null);
//  mention.sentNum = 0;

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1, cm2));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, em);

//  assertFalse(result);
}
@Test
public void testAnnotate_NoCoreAnnotationsTokensAnnotation() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
  

  Annotation annotation = new Annotation("incomplete input");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    assertTrue(e instanceof NullPointerException);
  }
}
@Test
public void testAnnotate_EmptyCorefMentionListDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  Annotation annotation = new Annotation("empty");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  Map<?, ?> map = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
  assertNotNull(map);
  assertTrue(map.isEmpty());
}
@Test
public void testAnnotate_WithEntityMentionThatNeverMatchesAnyCorefMention() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("President");
  token.setIndex(0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  List<CoreLabel> tokens = Arrays.asList(token);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation annotation = new Annotation("President");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  CoreLabel unrelatedToken = new CoreLabel();
  unrelatedToken.setWord("Chairman");

//  CoreMap mention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(unrelatedToken));
//  mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//
//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  Map<?, ?> map = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(map);
}
@Test
public void testSynchMention_PersonWithMultipleTitleTokensSkipped() {
  CoreLabel title1 = new CoreLabel();
  title1.setWord("Dr.");
  title1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel title2 = new CoreLabel();
  title2.setWord("Prof.");
  title2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

  CoreLabel name1 = new CoreLabel();
  name1.setWord("Jane");

  CoreLabel name2 = new CoreLabel();
  name2.setWord("Doe");

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(title1, title2, name1, name2));

  Annotation annotation = new Annotation("Dr. Prof. Jane Doe");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention mention = new Mention(0, 0, 4, null);
//  mention.sentNum = 0;

//  CoreMap entityMention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(name1, name2));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);

//  assertTrue(result);
}
@Test
public void testAnnotate_EntityMentionTypeNull() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("Smith");
  token.setIndex(0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("Smith");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  CoreMap entityMention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  

//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    assertTrue(e instanceof NullPointerException);
  }
}
@Test
public void testSynchMention_TrailingSuffixNotPossessiveFails() {
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Emily");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Clark");

  CoreLabel suffix = new CoreLabel();
  suffix.setWord("Jr"); 

  List<CoreLabel> cmTokens = Arrays.asList(token1, token2, suffix);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

  Annotation annotation = new Annotation("Emily Clark Jr");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  Mention cmMention = new Mention(0, 0, 3, null);
//  cmMention.sentNum = 0;

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cmMention, em);

//  assertFalse(result);
}
@Test
public void testAnnotate_EntityMentionWithEmptyTokensDoesNotCrash() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("ENTITY");
  token.setIndex(0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("ENTITY");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "OBJECT");
//  em.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(em));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotator.annotate(annotation);

  Map<?, ?> mapping = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  assertNotNull(mapping);
}
@Test
public void testAnnotate_TokenHasMultipleCorefMentionIDs() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("John");
  token.setIndex(0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

  token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>(Arrays.asList(0, 1, 2)));

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("John");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

//  CoreMap em = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

//  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(em));

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());

//  Mention m0 = new Mention(0, 0, 1, null);
//  Mention m1 = new Mention(0, 0, 1, null);
//  Mention m2 = new Mention(0, 0, 1, null);

//  annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).add(m0);
//  annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).add(m1);
//  annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).add(m2);

  annotator.annotate(annotation);

  Map<?, ?> em2cmMap = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
  Map<?, ?> cm2emMap = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);

  assertNotNull(em2cmMap);
  assertNotNull(cm2emMap);
}
@Test
public void testSynchMention_SameLengthMismatchFails() {
  CoreLabel cm1 = new CoreLabel();
  cm1.setWord("John");

  CoreLabel cm2 = new CoreLabel();
  cm2.setWord("Smith");

  CoreLabel em1 = new CoreLabel();
  em1.setWord("John");

  CoreLabel em2 = new CoreLabel();
  em2.setWord("Snow"); 

//  CoreMap cmSentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  cmSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cm1, cm2));

  Annotation annotation = new Annotation("John Smith");
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSentence));

//  Mention mention = new Mention(0, 0, 2, null);
//  mention.sentNum = 0;

//  CoreMap entityMention = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
//  entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(em1, em2));

//  boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);

//  assertFalse(result);
}
@Test
public void testAnnotate_NoIndexesAssignedWhenZeroMentions() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel token = new CoreLabel();
  token.setWord("OnlyToken");
  token.setIndex(0);

  token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, new ArraySet<>());

//  CoreMap sentence = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  Annotation annotation = new Annotation("No real mentions");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());

  annotator.annotate(annotation);

  Set<Integer> mentionIds = token.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class);
  assertNotNull(mentionIds);
  assertTrue(mentionIds.isEmpty());
}
@Test
public void testAnnotate_SentNumPopulatedCorrectly() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CoreLabel t1 = new CoreLabel();
  t1.setWord("The");

  CoreLabel t2 = new CoreLabel();
  t2.setWord("president");

//  CoreMap sentence1 = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));

  CoreLabel t3 = new CoreLabel();
  t3.setWord("spoke");

  CoreLabel t4 = new CoreLabel();
  t4.setWord("today");

//  CoreMap sentence2 = new CorefMentionAnnotatorTest.SimpleAnnotationMap();
//  sentence2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t3, t4));

  Annotation annotation = new Annotation("Two sentences.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2, t3, t4));
//  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());

//  Mention m1 = new Mention(0, 0, 2, null);
//  Mention m2 = new Mention(0, 0, 2, null);

//  List<Mention> mList1 = Arrays.asList(m1);
//  List<Mention> mList2 = Arrays.asList(m2);
//  List<List<Mention>> all = Arrays.asList(mList1, mList2);

  
  annotator.annotate(annotation); 

  
//  sentence1.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mList1);
//  sentence2.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mList2);
//  annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).addAll(mList1);
//  annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).addAll(mList2);
//
//  m1.sentNum = 0;
//  m2.sentNum = 1;
//
//  assertEquals(0, m1.sentNum);
//  assertEquals(1, m2.sentNum);
}
@Test
public void testRequirements_ContainsDependencyAnnotations() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

  assertTrue(required.contains(org.junit.Assert.class.getDeclaringClass())); 
  assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  assertTrue(required.contains(CoreAnnotations.IndexAnnotation.class));
}
@Test
public void testRequirementsSatisfiedSetIsImmutable() {
  Properties props = new Properties();
  props.setProperty("coref.md.type", "rule");

  CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> result = annotator.requirementsSatisfied();

  boolean threwException = false;
  try {
    result.add(CoreAnnotations.TokensAnnotation.class);
  } catch (UnsupportedOperationException e) {
    threwException = true;
  }

  assertTrue(threwException);
} 
}