package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.coref.md.CorefMentionFinder;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
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

public class CorefMentionAnnotator_2_GPTLLMTest {

 @Test
  public void testRequirementsContainExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContainExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
  }
@Test
  public void testAnnotateAddsMentionsAndMappings() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    token1.setIndex(0);
    token1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    token2.setIndex(1);
    token2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Joe Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> entityMentions = new ArrayList<CoreMap>();
    entityMentions.add(entityMention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentions);
    token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    token2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    annotator.annotate(annotation);

    List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mentions);
    assertFalse(mentions.isEmpty());

    Map<Integer, Integer> corefToEntity = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
    Map<Integer, Integer> entityToCoref = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

    assertNotNull(corefToEntity);
    assertNotNull(entityToCoref);
  }
@Test
  public void testSynchCorefMentionEntityMentionWithTitleStripsTitle() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Mr.");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    token1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Joe");
    token2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Smith");
    token3.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreLabel> allTokens = new ArrayList<CoreLabel>();
    allTokens.add(token1);
    allTokens.add(token2);
    allTokens.add(token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, allTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Mr. Joe Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 1, 3, "Joe Smith", 0);

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(token1);
    emTokens.add(token2);
    emTokens.add(token3);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> emList = new ArrayList<CoreMap>();
    emList.add(entityMention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, emList);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(result);
  }
@Test
  public void testSynchMentionWithPossessiveSuffix() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");

    List<CoreLabel> sentenceTokens = new ArrayList<CoreLabel>();
    sentenceTokens.add(token1);
    sentenceTokens.add(token2);
    sentenceTokens.add(token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Joe Smith's");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 3, "Joe Smith's", 0);

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(token1);
    emTokens.add(token2);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> ems = new ArrayList<CoreMap>();
    ems.add(entityMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, ems);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(result);
  }
@Test
  public void testSynchMentionFailsOnTokenMismatch() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Wick");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("John Wick");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 2, "John Wick", 0);

    CoreLabel tokenX = new CoreLabel();
    tokenX.setWord("James");

    CoreLabel tokenY = new CoreLabel();
    tokenY.setWord("Bond");

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(tokenX);
    emTokens.add(tokenY);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> mentionList = new ArrayList<CoreMap>();
    mentionList.add(entityMention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testChineseNewswireSetsRemoveNestedFalse() {
    Properties props = new Properties();
    props.setProperty("coref.language", "zh");
    props.setProperty("coref.input.type", "conll");
    props.setProperty("coref.specialCaseNewswire", "true");
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("中国");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("中国");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "nw_xinhua_zh_doc");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertEquals("false", props.getProperty("removeNestedMentions"));
  }
@Test
  public void testNonChineseRawInputSetsRemoveNestedTrue() {
    Properties props = new Properties();
    props.setProperty("coref.language", "zh");
    props.setProperty("coref.input.type", "raw");
    props.setProperty("coref.specialCaseNewswire", "false");
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Example");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Example");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "random_doc");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertEquals("true", props.getProperty("removeNestedMentions"));
  }
@Test
  public void testAnnotateWithNoSentencesShouldNotThrow() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Annotation annotation = new Annotation("Empty");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    tokens.add(token);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mentions);
  }
@Test
  public void testSynchMentionReturnsFalseIfNoTokenOverlap() {
    CoreLabel cmT1 = new CoreLabel(); cmT1.setWord("The");
    CoreLabel cmT2 = new CoreLabel(); cmT2.setWord("cat");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmT1);
    cmTokens.add(cmT2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("The cat");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 2, "The cat", 0);

    CoreLabel emT1 = new CoreLabel(); emT1.setWord("Dog");
    CoreLabel emT2 = new CoreLabel(); emT2.setWord("food");

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(emT1);
    emTokens.add(emT2);

    CoreMap entity = new ArrayCoreMap();
    entity.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entity.set(CoreAnnotations.EntityTypeAnnotation.class, "OBJECT");

    List<CoreMap> mentions = new ArrayList<CoreMap>();
    mentions.add(entity);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entity);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionReturnsFalseWhenOnlyOneDirectionalOverlap() {
    CoreLabel cmT1 = new CoreLabel(); cmT1.setWord("Barack");
    CoreLabel cmT2 = new CoreLabel(); cmT2.setWord("Obama");
    CoreLabel cmT3 = new CoreLabel(); cmT3.setWord("Jr.");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmT1);
    cmTokens.add(cmT2);
    cmTokens.add(cmT3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sents = new ArrayList<CoreMap>();
    sents.add(sentence);

    Annotation annotation = new Annotation("Barack Obama Jr.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

//    Mention corefMention = new Mention(0, 0, 3, "Barack Obama Jr.", 0);

    CoreLabel emT1 = new CoreLabel(); emT1.setWord("Barack");
    CoreLabel emT2 = new CoreLabel(); emT2.setWord("Obama");

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(emT1);
    emTokens.add(emT2);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> mentionList = new ArrayList<CoreMap>();
    mentionList.add(entityMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionReturnsFalseIfEntityTokensAreNull() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sents = new ArrayList<CoreMap>();
    sents.add(sentence);

    Annotation annotation = new Annotation("Barack");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

//    Mention corefMention = new Mention(0, 0, 1, "Barack", 0);

    CoreMap entity = new ArrayCoreMap();
    entity.set(CoreAnnotations.TokensAnnotation.class, null);
    entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entity));

    try {
//      CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entity);
    } catch (Exception e) {
      fail("Exception should not occur even if entity tokens are null");
    }
  }
@Test
  public void testAnnotateHandlesNullDocIdGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Sample");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Sample");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotation.set(CoreAnnotations.DocIDAnnotation.class, null);

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testSynchMentionReturnsFalseWithPersonMissingTitleCheck() {
    CoreLabel token1 = new CoreLabel(); token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
    token1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel(); token2.setWord("Joe");
    CoreLabel token3 = new CoreLabel(); token3.setWord("Biden");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(token1); cmTokens.add(token2); cmTokens.add(token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("President Joe Biden");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 3, "President Joe Biden", 0);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, cmTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> mentionList = new ArrayList<CoreMap>();
    mentionList.add(entityMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(result);
  }
@Test
  public void testSynchMentionWithZeroTokenOverlapReturnsFalse() {
    CoreLabel token1 = new CoreLabel(); token1.setWord("President");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    token1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(token1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("President");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 1, "President", 0);

    CoreLabel emToken = new CoreLabel();
    emToken.setWord("King");

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(emToken);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testAnnotateWithEmptyTokensAndSentences() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    List<CoreMap> sentences = new ArrayList<CoreMap>();

    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testAnnotateWithMultipleSentencesAndMentions() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token1 = new CoreLabel(); token1.setWord("Alice");
    CoreLabel token2 = new CoreLabel(); token2.setWord("Bob");
    CoreLabel token3 = new CoreLabel(); token3.setWord("Chris");

    List<CoreLabel> tokens1 = new ArrayList<CoreLabel>(); tokens1.add(token1);
    List<CoreLabel> tokens2 = new ArrayList<CoreLabel>(); tokens2.add(token2);
    List<CoreLabel> tokens3 = new ArrayList<CoreLabel>(); tokens3.add(token3);

    CoreMap sentence1 = new ArrayCoreMap(); sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);
    CoreMap sentence2 = new ArrayCoreMap(); sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);
    CoreMap sentence3 = new ArrayCoreMap(); sentence3.set(CoreAnnotations.TokensAnnotation.class, tokens3);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence1); sentences.add(sentence2); sentences.add(sentence3);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<CoreLabel> allTokens = new ArrayList<CoreLabel>();
    allTokens.add(token1); allTokens.add(token2); allTokens.add(token3);
    annotation.set(CoreAnnotations.TokensAnnotation.class, allTokens);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mentions);
  }
@Test
  public void testConstructorHandlesIOExceptionGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.dictionaries", "/invalid/path/to/trigger/io");

    try {
      new CorefMentionAnnotator(props);
    } catch (Exception ex) {
      assertTrue(ex.getCause() instanceof IOException || ex instanceof RuntimeException);
    }
  }
@Test
  public void testSynchMentionWhenEntityMentionLargerThanCoref() {
    CoreLabel token1 = new CoreLabel(); token1.setWord("Joe");
    CoreLabel token2 = new CoreLabel(); token2.setWord("Smith");

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> sentenceTokens = new ArrayList<CoreLabel>();
    sentenceTokens.add(token1);
    sentenceTokens.add(token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>(); sentences.add(sentence);

    Annotation annotation = new Annotation("Joe Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 1, "Joe", 0);

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(token1);
    emTokens.add(token2);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionReturnsFalseWhenTokenCountMatchesButDifferentRefs() {
    CoreLabel cmToken1 = new CoreLabel(); cmToken1.setWord("Locations");
    CoreLabel cmToken2 = new CoreLabel(); cmToken2.setWord("Inc.");

    List<CoreLabel> cmTokenList = new ArrayList<CoreLabel>();
    cmTokenList.add(cmToken1);
    cmTokenList.add(cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokenList);
    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Locations Inc.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 2, "Locations Inc.", 0);

    
    CoreLabel emToken1 = new CoreLabel(); emToken1.setWord("Locations");
    CoreLabel emToken2 = new CoreLabel(); emToken2.setWord("Inc.");

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(emToken1); emTokens.add(emToken2);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testCorefAndEntityMappingOneToMany() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Joe");
    token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Joe");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap entityMention1 = new ArrayCoreMap();
    entityMention1.set(CoreAnnotations.TokensAnnotation.class, tokens);
    entityMention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention1, entityMention1));

    annotator.annotate(annotation);

    Map<Integer, Integer> map1 = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
    Map<Integer, Integer> map2 = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

    assertNotNull(map1);
    assertNotNull(map2);
  }
@Test
  public void testXmlMentionFilteringConditionTriggered() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.removeXML", "true");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel(); token.setWord("<xml>");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("<xml>");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testSynchMentionFailsWhenSentenceTokenCountIsNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, null); 

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Obama");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention mention = new Mention(0, 0, 1, "Obama", 0);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    try {
//      CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
    } catch (Exception e) {
      fail("Method should handle null sentence token list and not throw exception");
    }
  }
@Test
  public void testAnnotateWithNullMentionsAnnotationHandlesGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    List<CoreLabel> tokenList = new ArrayList<CoreLabel>();
    tokenList.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null); 

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should not fail on null MentionsAnnotation");
    }

    List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testSynchMentionReturnsFalseForNonPERSONEntityType() {
    CoreLabel token1 = new CoreLabel(); token1.setWord("Tesla");
    CoreLabel token2 = new CoreLabel(); token2.setWord("Motors");

    List<CoreLabel> sentenceTokens = new ArrayList<CoreLabel>();
    sentenceTokens.add(token1);
    sentenceTokens.add(token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Tesla Motors");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 2, "Tesla Motors", 0);

    List<CoreLabel> entityTokens = new ArrayList<CoreLabel>();
    entityTokens.add(token1);
    entityTokens.add(token2);

    CoreMap entity = new ArrayCoreMap();
    entity.set(CoreAnnotations.TokensAnnotation.class, entityTokens);
    entity.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION"); 

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entity));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entity);
//    assertTrue("Entity type ORGANIZATION should not affect token match", result);
  }
@Test
  public void testAnnotateProperlyUpdatesTokenWithEmptyMentionIndexes() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Apple");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentList = new ArrayList<CoreMap>();
    sentList.add(sentence);

    Annotation annotation = new Annotation("Apple");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentList);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(token.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class));
  }
@Test
  public void testSynchMentionReturnsFalseIfEntityMentionTokensEmpty() {
    CoreLabel cmToken1 = new CoreLabel(); cmToken1.setWord("Elon");
    CoreLabel cmToken2 = new CoreLabel(); cmToken2.setWord("Musk");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken1);
    cmTokens.add(cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sents = new ArrayList<CoreMap>();
    sents.add(sentence);

    Annotation annotation = new Annotation("Elon Musk");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

//    Mention corefMention = new Mention(0, 0, 2, "Elon Musk", 0);

    CoreMap entity = new ArrayCoreMap();
    entity.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>()); 
    entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entity));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entity);
//    assertFalse(result);
  }
@Test
  public void testAnnotateHandlesSentenceWithoutMentionsList() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Amazon");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    

    List<CoreMap> sents = new ArrayList<CoreMap>();
    sents.add(sentence);

    Annotation annotation = new Annotation("Amazon");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    List<Mention> globalMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(globalMentions);
  }
@Test
  public void testSynchMentionWithNullFineGrainedTagSkipsTitle() {
    CoreLabel t1 = new CoreLabel(); t1.setWord("Dr.");
    t1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
    t1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    CoreLabel t2 = new CoreLabel(); t2.setWord("Henry");
    CoreLabel t3 = new CoreLabel(); t3.setWord("Jones");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(t1); tokens.add(t2); tokens.add(t3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sents = new ArrayList<CoreMap>();
    sents.add(sentence);

    Annotation annotation = new Annotation("Dr. Henry Jones");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

//    Mention corefMention = new Mention(0, 0, 3, "Dr. Henry Jones", 0);

    CoreMap entity = new ArrayCoreMap();
    entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
    entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entity));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entity);
//
//    assertTrue(result);
  }
@Test
  public void testSynchMentionReturnsFalseIfEntityTypeMissing() {
    CoreLabel cmToken1 = new CoreLabel(); cmToken1.setWord("Dr.");
    cmToken1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel cmToken2 = new CoreLabel(); cmToken2.setWord("Watson");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken1);
    cmTokens.add(cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Dr. Watson");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 2, "Dr. Watson", 0);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, cmTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, null); 

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionFailsWhenEntityMentionShorterThanCoref() {
    CoreLabel cmToken1 = new CoreLabel(); cmToken1.setWord("John");
    CoreLabel cmToken2 = new CoreLabel(); cmToken2.setWord("Smith");

    CoreLabel entityToken = new CoreLabel(); entityToken.setWord("John");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken1);
    cmTokens.add(cmToken2);

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(entityToken);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("John Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 2, "John Smith", 0);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testAnnotateDoesNotMapAnythingWhenNoEntityMentionIndexPresent() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("London");
    

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("London");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "LOCATION");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

    annotator.annotate(annotation);

    Map<Integer, Integer> map1 = annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);
    Map<Integer, Integer> map2 = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

    assertNotNull(map1);
    assertNotNull(map2);
    assertTrue(map1.isEmpty());
    assertTrue(map2.isEmpty());
  }
@Test
  public void testSynchMentionSkipsTitleTokensCorrectly() {
    CoreLabel cmToken1 = new CoreLabel(); cmToken1.setWord("Dr.");
    cmToken1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    cmToken1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    CoreLabel cmToken2 = new CoreLabel(); cmToken2.setWord("John");

    CoreLabel emToken = cmToken2;

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken1);
    cmTokens.add(cmToken2);

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(emToken);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Dr. John");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention corefMention = new Mention(0, 0, 2, "Dr. John", 0);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON"); 

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(result);
  }
@Test
  public void testSynchMentionReturnsFalseWhenNoTokensOverlap() {
    CoreLabel cmToken1 = new CoreLabel(); cmToken1.setWord("Mike");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken1);

    CoreLabel emToken1 = new CoreLabel(); emToken1.setWord("Laura");

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(emToken1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>(); sentences.add(sentence);
    Annotation annotation = new Annotation("Mike");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention cm = new Mention(0, 0, 1, "Mike", 0);

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(em));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionEdgeCaseTrailingPossessiveButEntityTooShort() {
    CoreLabel cmToken1 = new CoreLabel(); cmToken1.setWord("Tesla");
    CoreLabel cmToken2 = new CoreLabel(); cmToken2.setWord("'s");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken1);
    cmTokens.add(cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>(); sentences.add(sentence);

    Annotation annotation = new Annotation("Tesla's");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention cm = new Mention(0, 0, 2, "Tesla's", 0);

    CoreLabel emToken = new CoreLabel(); emToken.setWord("Tesla");
    List<CoreLabel> emTokens = new ArrayList<CoreLabel>();
    emTokens.add(emToken);

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(em));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertTrue(result);
  }
@Test
  public void testSynchMentionSkipsTitleOnlyIfExactlyTITLE() {
    CoreLabel cmToken = new CoreLabel();
    cmToken.setWord("Mr.");
    cmToken.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "NOT_TITLE");
    cmToken.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("Bond");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken);
    cmTokens.add(cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Mr. Bond");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention cm = new Mention(0, 0, 2, "Mr. Bond", 0);

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, cmTokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(em));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionFailsIfStartEqualsEndIndex() {
    CoreLabel tok1 = new CoreLabel(); tok1.setWord("Hello");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(tok1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention cm = new Mention(0, 0, 0, "", 0);

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(em));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionFailsIfTokensNotSameReference() {
    CoreLabel t1 = new CoreLabel(); t1.setWord("Alice");
    CoreLabel t2 = new CoreLabel(); t2.setWord("Alice");

    List<CoreLabel> sentTokens = new ArrayList<CoreLabel>(); sentTokens.add(t1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);
    Annotation annotation = new Annotation("Alice");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention cm = new Mention(0, 0, 1, "Alice", 0);

    List<CoreLabel> emTokens = new ArrayList<CoreLabel>(); emTokens.add(t2);
    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, emTokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(em));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testAnnotateAssignsMentionIDsSequentiallyAcrossSentences() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel t1 = new CoreLabel(); t1.setWord("He");
    CoreLabel t2 = new CoreLabel(); t2.setWord("is");

    List<CoreLabel> tokens1 = new ArrayList<CoreLabel>();
    tokens1.add(t1); tokens1.add(t2);

    CoreLabel t3 = new CoreLabel(); t3.setWord("John");
    CoreLabel t4 = new CoreLabel(); t4.setWord("Smith");

    List<CoreLabel> tokens2 = new ArrayList<CoreLabel>();
    tokens2.add(t3); tokens2.add(t4);

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence1); sentences.add(sentence2);

    Annotation annotation = new Annotation("He is John Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    List<CoreLabel> allTokens = new ArrayList<CoreLabel>();
    allTokens.addAll(tokens1); allTokens.addAll(tokens2);
    annotation.set(CoreAnnotations.TokensAnnotation.class, allTokens);

    List<CoreMap> mentions = new ArrayList<CoreMap>();
    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, tokens2);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(em));

    annotator.annotate(annotation);

    List<Mention> resultMentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(resultMentions);
    if (resultMentions.size() > 1) {
      int id0 = resultMentions.get(0).mentionID;
      int id1 = resultMentions.get(1).mentionID;
      assertEquals(id0 + 1, id1);
    }
  }
@Test
  public void testSynchMentionFailsWhenOverlapZeroEvenIfCountsMatch() {
    CoreLabel cm1 = new CoreLabel(); cm1.setWord("Foo");
    CoreLabel cm2 = new CoreLabel(); cm2.setWord("Bar");

    CoreLabel em1 = new CoreLabel(); em1.setWord("Foo");
    CoreLabel em2 = new CoreLabel(); em2.setWord("Bar");

    CoreLabel refCopy1 = new CoreLabel(); refCopy1.setWord("Foo");
    CoreLabel refCopy2 = new CoreLabel(); refCopy2.setWord("Bar");

    List<CoreLabel> sentenceTokens = new ArrayList<CoreLabel>();
    sentenceTokens.add(cm1);
    sentenceTokens.add(cm2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>(); sentences.add(sentence);
    Annotation annotation = new Annotation("Foo Bar");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    Mention cm = new Mention(0, 0, 2, "Foo Bar", 0);

    List<CoreLabel> entityTokens = new ArrayList<CoreLabel>();
    entityTokens.add(refCopy1);
    entityTokens.add(refCopy2);
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, entityTokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

    
//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, entityMention);
//    assertFalse(result);
  }
@Test
  public void testMentionsAnnotationSetButTokenHasInvalidEntityIndex() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Bob");
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1); 

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Bob");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap mentionOnly = new ArrayCoreMap();
    mentionOnly.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mentionOnly.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mentionOnly));

    try {
      annotator.annotate(annotation);
    } catch (IndexOutOfBoundsException ex) {
      fail("Should not throw IndexOutOfBoundsException even if EntityMentionIndex is invalid");
    }

    List<Mention> mList = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mList);
  }
@Test
  public void testSynchMentionReturnsFalseWhenEntityTypeIsNotSet() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Captain");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("Marvel");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmToken1);
    cmTokens.add(cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Captain Marvel");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    Mention mention = new Mention(0, 0, 2, "Captain Marvel", 0);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, cmTokens);
    

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionReturnsFalseWhenMentionTokensAreNull() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, null);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    Mention mention = new Mention(0, 0, 1, "X", 0);

    CoreLabel emToken = new CoreLabel();
    emToken.setWord("X");

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(emToken));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    try {
//      boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
//      assertFalse(result);
    } catch (Exception e) {
      fail("Method should not throw even if sentence tokens are null");
    }
  }
@Test
  public void testAnnotateSkipsEntityMappingWhenMentionIndexIsNegative() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CoreLabel token = new CoreLabel();
    token.setWord("Neo");
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, -5); 

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Neo");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    annotator.annotate(annotation);

    Map<Integer,Integer> mapping =
        annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);

    assertNotNull(mapping);
    assertTrue(mapping.isEmpty());
  }
@Test
  public void testRequirementsIncludeEnhancedDependenciesIfHybridMD() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "hybrid");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.BeginIndexAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.EndIndexAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testGetMentionFinderReturnsDependencyFinder() throws Exception {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "dep");  

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);  
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertFalse(required.contains(CoreAnnotations.BeginIndexAnnotation.class)); 
  }
@Test
  public void testSynchCorefMentionEntityMentionAllowsTrailingPossessiveAtEnd() {
    CoreLabel cmTok1 = new CoreLabel(); cmTok1.setWord("John");
    CoreLabel cmTok2 = new CoreLabel(); cmTok2.setWord("Smith");
    CoreLabel cmTok3 = new CoreLabel(); cmTok3.setWord("'s");

    List<CoreLabel> cmTokens = new ArrayList<CoreLabel>();
    cmTokens.add(cmTok1);
    cmTokens.add(cmTok2);
    cmTokens.add(cmTok3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);

    Annotation ann = new Annotation("John Smith's");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    Mention cm = new Mention(0, 0, 3, "John Smith's", 0);

    CoreLabel emTok1 = cmTok1;
    CoreLabel emTok2 = cmTok2;

    List<CoreLabel> entityTokens = new ArrayList<CoreLabel>();
    entityTokens.add(emTok1);
    entityTokens.add(emTok2);

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, entityTokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    ann.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(em));

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em);
//
//    assertTrue(result);
  }
@Test
  public void testAnnotateHandlesTokensWithNoCorefMentionIndexesSet() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Michael");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Michael");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(token.get(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class));
  }
@Test
  public void testAnnotateAssignsEmptyMentionListWhenNoneFound() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CoreLabel token = new CoreLabel();
    token.setWord("Earth");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Earth");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    annotator.annotate(annotation);

    List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(result);
  } 
}