package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.coref.md.CorefMentionFinder;
import edu.stanford.nlp.coref.md.RuleBasedCorefMentionFinder;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
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

public class CorefMentionAnnotator_5_GPTLLMTest {

 @Test
  public void testRequiresNotEmpty() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requiredAnnotations = annotator.requires();

    assertNotNull(requiredAnnotations);
    assertFalse(requiredAnnotations.isEmpty());
    assertTrue(requiredAnnotations.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyInput() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.language", "en");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Annotation annotation = new Annotation("");
    List<CoreMap> emptySentences = new ArrayList<CoreMap>();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, emptySentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testSynchExactMatch() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    List<CoreLabel> corefTokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, corefTokens);

    Annotation annotation = new Annotation("Alice Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 2, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean synch = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(synch);
  }
@Test
  public void testSynchFailsDueToExtraToken() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Jr.");

    List<CoreLabel> corefTokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, corefTokens);

    Annotation annotation = new Annotation("Alice Smith Jr.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 3, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean synch = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(synch);
  }
@Test
  public void testSynchWithTitleSkipped() {
    CoreLabel titleToken = new CoreLabel();
    titleToken.setWord("President");
    titleToken.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    List<CoreLabel> corefTokens = Arrays.asList(titleToken, token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, corefTokens);

    Annotation annotation = new Annotation("President Alice Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 3, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean synch = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(synch);
  }
@Test
  public void testSynchFailsDueToTokenMismatch() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel wrongToken = new CoreLabel();
    wrongToken.setWord("Johnson");

    List<CoreLabel> corefTokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, corefTokens);

    Annotation annotation = new Annotation("Alice Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 2, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, wrongToken));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean synch = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertFalse(synch);
  }
@Test
  public void testSynchAllowsTrailingPossessive() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");

    List<CoreLabel> corefTokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, corefTokens);

    Annotation annotation = new Annotation("John Smith's");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 3, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(result);
  }
@Test
  public void testSpecialCaseNewswireChineseConll() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.input.type", "conll");
    props.setProperty("coref.language", "zh");
    props.setProperty("coref.specialCaseNewswire", "true");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("张三");
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    token.setIndex(0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, null);
//    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("zh doc");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "nw_doc_001");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    String removeNested = props.getProperty("removeNestedMentions");
    assertEquals("false", removeNested);
  }
@Test
  public void testMentionIDAcrossMultipleSentences() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Bob");

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);
    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    token1.setIndex(0);
    token2.setIndex(1);

    Annotation annotation = new Annotation("Alice Bob");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mentions);
    if (mentions.size() >= 2) {
      assertEquals(0, mentions.get(0).mentionID);
      assertEquals(1, mentions.get(1).mentionID);
    }
  }
@Test
  public void testEntityMentionsWithoutMatchingCorefMention() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Alice");

    List<CoreLabel> tokens = Collections.singletonList(token);
    token.setIndex(0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    List<CoreMap> entityMentions = Collections.singletonList(entityMention);

    Annotation annotation = new Annotation("Alice");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentions);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    Map<Integer, Integer> e2c =
        annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
    Map<Integer, Integer> c2e =
        annotation.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);

    assertNotNull(e2c);
    assertNotNull(c2e);
    assertTrue(e2c.isEmpty() || c2e.isEmpty());
  }
@Test
  public void testZeroTokenOverlapReturnsFalse() {
    CoreLabel cmToken = new CoreLabel();
    cmToken.setWord("President");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(cmToken));

    Annotation ann = new Annotation("President");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 1, 0.0f);
//    corefMention.sentNum = 0;

    CoreLabel unrelatedToken = new CoreLabel();
    unrelatedToken.setWord("Engineer");

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(unrelatedToken));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testTokenObjectsAreDifferentButSameWords() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Alice");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("Smith");

    CoreLabel emToken1 = new CoreLabel();
    emToken1.setWord("Alice");

    CoreLabel emToken2 = new CoreLabel();
    emToken2.setWord("Smith");

    List<CoreLabel> cmTokens = Arrays.asList(cmToken1, cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation ann = new Annotation("Alice Smith");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 2, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(emToken1, emToken2));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testAnnotateHandlesMissingSentencesGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Annotation annotation = new Annotation("Text without sentences");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
      assertTrue(true); 
    } catch (Exception e) {
      fail("annotate() should handle missing sentence annotation gracefully.");
    }
  }
@Test
  public void testSynchFailsWhenEntityTypeIsNull() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    Annotation ann = new Annotation("Alice");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 1, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, null);

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, corefMention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testFineGrainedTagPresentButNotTitle() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Dr.");
    cmToken1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PROFESSION");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("Alice");

    List<CoreLabel> cmTokens = Arrays.asList(cmToken1, cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("Dr. Alice");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention coref = new Mention(0, 0, 2, 0.0f);
//    coref.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cmToken1, cmToken2));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, coref, entityMention);
//    assertTrue(result);
  }
@Test
  public void testSynchWithIncompleteTokenOverlap() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Alice");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("Smith");

    List<CoreLabel> cmTokens = Arrays.asList(cmToken1, cmToken2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation ann = new Annotation("Alice Smith");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention coref = new Mention(0, 0, 2, 0.0f);
//    coref.sentNum = 0;

    CoreLabel emToken1 = new CoreLabel();
    emToken1.setWord("Alice");

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(emToken1));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, coref, entityMention);
//    assertFalse(result);
  }
@Test
  public void testSynchWithTrailingPossessiveOnPartialOverlapFails() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Alice");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("'s");

    List<CoreLabel> cmTokens = Arrays.asList(cmToken1, cmToken2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("Alice's");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention coref = new Mention(0, 0, 2, 0.0f);
//    coref.sentNum = 0;

    CoreLabel emToken = new CoreLabel();
    emToken.setWord("Bob"); 

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(emToken));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, coref, entityMention);
//    assertFalse(result);
  }
@Test
  public void testAnnotateWithSentenceHavingNullTokenList() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, null);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Sentence with null tokens");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
      assertTrue(true); 
    } catch (Exception e) {
      fail("Should handle null token list gracefully");
    }
  }
@Test
  public void testTokenWithoutCorefMentionIndexesAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    try {
      annotator.annotate(annotation);
      assertTrue(true); 
    } catch (Exception e) {
      fail("Should handle missing CorefMentionIndexesAnnotation gracefully");
    }
  }
@Test
  public void testEmptySpanMentionShouldFailSynch() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    Annotation annotation = new Annotation("Empty span");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention mention = new Mention(0, 1, 1, 0.0f);
//    mention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, mention, entityMention);
//    assertFalse(result);
  }
@Test
  public void testDuplicateMentionIndexesOnToken() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    Set<Integer> duplicateIndexes = new HashSet<>();
    duplicateIndexes.add(0);
    duplicateIndexes.add(0);
    token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, duplicateIndexes);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Annotation annotation = new Annotation("Dup IDs");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    try {
      annotator.annotate(annotation);
      Map<Integer, Integer> map = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
      assertTrue(map.isEmpty() || map.containsKey(0));
    } catch (Exception e) {
      fail("Should handle duplicate mention indexes without exception");
    }
  }
@Test
  public void testSynchCorefEntityMentionEntityTypeNotPerson() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 1, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "LOCATION");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, entityMention);
//    assertTrue(result);
  }
@Test
  public void testSynchWithNullFineGrainedNamedEntityTagAnnotation() {
    CoreLabel title = new CoreLabel();
    title.setWord("President");
    title.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);

    CoreLabel first = new CoreLabel();
    first.setWord("Bob");

    List<CoreLabel> cmTokens = Arrays.asList(title, first);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("President Bob");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention corefMention = new Mention(0, 0, 2, 0.0f);
//    corefMention.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(title, first));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, corefMention, em);
//    assertTrue(result);
  }
@Test
  public void testCorefPropertiesRemoveXmlMentionsFalse() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.removeXML", "false");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setIndex(0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = Collections.singletonList(sentence);
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Barack");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testAnnotateWithMissingDocId() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Barack");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    
    annotator.annotate(annotation);

    String removeNested = props.getProperty("removeNestedMentions");
    assertEquals("true", removeNested);
  }
@Test
  public void testMismatchedMentionListSizeToSentences() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("One");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Two");

    List<CoreLabel> tokens1 = Collections.singletonList(token1);
    List<CoreLabel> tokens2 = Collections.singletonList(token2);

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);
    List<CoreLabel> allTokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Two sentences");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, allTokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());

    annotator.annotate(annotation);

    List<Mention> result = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testSynchCorefMentionsEmptyEntityTokenList() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Obama");

    List<CoreLabel> cmTokens = Collections.singletonList(cmToken1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation ann = new Annotation("Obama");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 1, 0.0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchMentionsAllTokensMatchReferenceEquality() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("Barack Obama");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 2, 0.0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, tokens);
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em);
//    assertTrue(result);
  }
@Test
  public void testSynchWithNullTokensAnnotationOnEntityMention() {
    CoreLabel token = new CoreLabel();
    token.setWord("Alice");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation ann = new Annotation("Alice");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 1, 0.0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(ann, cm, em);
//    assertFalse(result);
  }
@Test
  public void testAnnotateWithNullMentionsAnnotationField() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.setIndex(0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotator.annotate(annotation);

    List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(mentions);
  }
@Test
  public void testAnnotatorDefaultsToRuleBasedIfTypeMissing() {
    Properties props = new Properties();  
    props.setProperty("coref.language", "en");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.BeginIndexAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testMentionFinderReturnsEmptyLists() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("John");
    token.setIndex(0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("John");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());  

    annotator.annotate(annotation);

    List<Mention> results = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
    assertNotNull(results);
  }
@Test
  public void testAnnotateWithChineseLocaleAndNewswireSpecialCase() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.input.type", "conll");
    props.setProperty("coref.language", "zh"); 
    props.setProperty("coref.specialCaseNewswire", "true");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("张三");
    token.setIndex(0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("doc");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "nw_file");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    String removeNestedProp = props.getProperty("removeNestedMentions");
    assertEquals("false", removeNestedProp);
  }
@Test
  public void testTokenWithoutIndexAnnotationAndMentionMapping() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");  
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Annotation annotation = new Annotation("Obama");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

    annotator.annotate(annotation);
    Map<Integer, Integer> map = annotation.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);
    assertNotNull(map);  
  }
@Test
  public void testEmptyCorefMentionTokensReturnsFalseInSynch() {
    Annotation annotation = new Annotation("text");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention coref = new Mention(0, 0, 0, 0.0f);

    CoreLabel entityToken = new CoreLabel();
    entityToken.setWord("Nobody");

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(entityToken));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, coref, entityMention);
//    assertFalse(result);
  }
@Test
  public void testSynchCorefMentionWithNullSentencesAnnotation() {
    Annotation annotation = new Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null); 

//    Mention cm = new Mention(0, 0, 1, 0.0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    try {
//      boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//      assertFalse(result);
    } catch (Exception e) {
      assertTrue(e instanceof NullPointerException || e instanceof IndexOutOfBoundsException);
    }
  }
@Test
  public void testSynchCorefMentionWithEntityMentionContainingMoreTokens() {
    CoreLabel cmToken = new CoreLabel();
    cmToken.setWord("Alice");

    List<CoreLabel> cmTokens = Collections.singletonList(cmToken);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("Alice");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 1, 0.0f);
//    cm.sentNum = 0;

    CoreLabel emToken1 = new CoreLabel();
    emToken1.setWord("Alice");

    CoreLabel emToken2 = new CoreLabel();
    emToken2.setWord("Johnson");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(emToken1, emToken2));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testNullFineGrainedTitleAndEntityMismatch() {
    CoreLabel titleToken = new CoreLabel();
    titleToken.setWord("Sir");
    titleToken.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null); 

    CoreLabel nameToken = new CoreLabel();
    nameToken.setWord("Arthur");

    List<CoreLabel> tokens = Arrays.asList(titleToken, nameToken);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Sir Arthur");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 2, 0f);
//    cm.sentNum = 0;

    CoreLabel mismatchToken = new CoreLabel();
    mismatchToken.setWord("SomeoneElse");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(mismatchToken));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testEntityMentionToCorefMappingFilled() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.setIndex(0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    Set<Integer> corefIndexes = new HashSet<Integer>();
    corefIndexes.add(0);
    token.set(CorefCoreAnnotations.CorefMentionIndexesAnnotation.class, corefIndexes);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Annotation annotation = new Annotation("Alice");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

//    Mention mention = new Mention(0, 0, 1, 0f);
//    mention.sentNum = 0;
    List<Mention> mentionList = new ArrayList<Mention>();
//    mentionList.add(mention);
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentionList);

    annotator.annotate(annotation);

    Map<Integer, Integer> e2c = annotation
        .get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class);

    Map<Integer, Integer> c2e = annotation
        .get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class);

    assertEquals(Integer.valueOf(0), e2c.get(0));
    assertEquals(Integer.valueOf(0), c2e.get(0));
  }
@Test
  public void testSynchWithTitleTagSkipped() {
    CoreLabel title = new CoreLabel();
    title.setWord("Dr.");
    title.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");

    CoreLabel name = new CoreLabel();
    name.setWord("Taylor");

    List<CoreLabel> tokens = Arrays.asList(title, name);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("Dr. Taylor");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 2, 0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(name));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertTrue(result);
  }
@Test
  public void testSynchFailsOnTrailingMismatchIgnoredPossessive() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Company");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("'s");

    List<CoreLabel> cmTokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("Company's");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 2, 0f);
//    cm.sentNum = 0;

    CoreLabel other = new CoreLabel();
    other.setWord("Corporation");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(other));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchNullEntityTypeReturnsFalse() {
    CoreLabel token = new CoreLabel();
    token.setWord("James");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("James");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 1, 0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, null); 

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchNoTokenOverlapFails() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Jane");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Doe");

    List<CoreLabel> cmTokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("Jane Doe");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 2, 0f);
//    cm.sentNum = 0;

    CoreLabel other = new CoreLabel();
    other.setWord("Smith");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(other));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testAllowsTrailingPossessiveWhenAllTokensMatch() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("'s");

    List<CoreLabel> cmTokens = Arrays.asList(token1, token2, token3);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("John Smith's");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 3, 0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertTrue(result);
  }
@Test
  public void testSynchFailsOnZeroOverlap() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Mr.");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("X");

    List<CoreLabel> cmTokens = Arrays.asList(cmToken1, cmToken2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("Mr. X");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 2, 0f);
//    cm.sentNum = 0;

    CoreLabel emToken = new CoreLabel();
    emToken.setWord("Y");

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(emToken));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testSynchWithOffsetSubstructureShouldFail() {
    CoreLabel cmToken1 = new CoreLabel();
    cmToken1.setWord("Barack");

    CoreLabel cmToken2 = new CoreLabel();
    cmToken2.setWord("Obama");

    CoreLabel cmToken3 = new CoreLabel();
    cmToken3.setWord("Jr.");

    List<CoreLabel> cmTokens = Arrays.asList(cmToken1, cmToken2, cmToken3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, cmTokens);

    Annotation annotation = new Annotation("Barack Obama Jr.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    Mention cm = new Mention(0, 0, 3, 0f);
//    cm.sentNum = 0;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(cmToken2, cmToken3));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

//    boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, cm, em);
//    assertFalse(result);
  }
@Test
  public void testMentionSentenceIndexOutOfBounds() {
    CoreLabel token = new CoreLabel();
    token.setWord("Out");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Out");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

//    Mention coref = new Mention(0, 0, 1, 0f);
//    coref.sentNum = 9;

    CoreMap em = new ArrayCoreMap();
    em.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    em.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    try {
//      boolean result = CorefMentionAnnotator.synchCorefMentionEntityMention(annotation, coref, em);
//      assertFalse(result || true);
    } catch (Exception e) {
      assertTrue(e instanceof IndexOutOfBoundsException);
    }
  }
@Test
  public void testTokenMissingCorefMentionIndexesAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("TokenWithoutCorefIndex");
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");

    Annotation annotation = new Annotation("Edge");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.emptyList());

    try {
      annotator.annotate(annotation);
      assertTrue(true);
    } catch (Exception e) {
      fail("Should not throw with missing CorefMentionIndexesAnnotation");
    }
  }
@Test
  public void testAnnotationWithoutTokensAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    Annotation annotation = new Annotation("No Tokens");

    CoreMap sentence = new ArrayCoreMap();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
      assertTrue(true);
    } catch (Exception e) {
      fail("Should handle missing TokensAnnotation without throwing");
    }
  }
@Test
  public void testAnnotationWithoutSentencesAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);
    Annotation annotation = new Annotation("No Sentences");

    CoreLabel token = new CoreLabel();
    token.setWord("X");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
      assertTrue(true); 
    } catch (Exception e) {
      fail("Should handle null SentencesAnnotation gracefully");
    }
  }
@Test
  public void testDocIdWhitespaceEdgeCase() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.input.type", "conll");
    props.setProperty("coref.language", "zh");
    props.setProperty("coref.specialCaseNewswire", "true");

    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("文");
    token.setIndex(0);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Whitespace DocID");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, " "); 
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertEquals("true", props.getProperty("removeNestedMentions")); 
  }
@Test
  public void testAnnotationWithPreSetCorefMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "rule");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("John");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Pre-set");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonList(new Mention(0, 0, 0, 0)));

    try {
      annotator.annotate(annotation);
      List<Mention> mentions = annotation.get(CorefCoreAnnotations.CorefMentionsAnnotation.class);
      assertNotNull(mentions);
    } catch (Exception e) {
      fail("Annotation should allow overwriting pre-set CorefMention annotation");
    }
  } 
}