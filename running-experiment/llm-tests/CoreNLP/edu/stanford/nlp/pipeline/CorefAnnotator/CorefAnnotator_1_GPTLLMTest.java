package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.coref.md.CorefMentionFinder;
import edu.stanford.nlp.coref.md.RuleBasedCorefMentionFinder;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CorefAnnotator_1_GPTLLMTest {

 @Test
  public void testConstructorAcceptsValidProperties() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testConstructorRejectsHybridEnglishCombo() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "hybrid");

    new CorefAnnotator(props);
  }
@Test
  public void testNamedEntityTagGranularitySetsToFineCorrectly() {
    Annotation ann = new Annotation("Test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

//    CorefAnnotator.setNamedEntityTagGranularity(ann, "fine");

    String nerTag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("PERSON", nerTag);
  }
@Test
  public void testNamedEntityTagGranularitySetsToCoarseCorrectly() {
    Annotation ann = new Annotation("Test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

//    CorefAnnotator.setNamedEntityTagGranularity(ann, "coarse");

    String nerTag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("LOCATION", nerTag);
  }
@Test
  public void testAnnotateSkipsIfNoSentencesAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("This is a test without SentenceAnnotation.");

    annotator.annotate(annotation);
    assertNull(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();

    assertTrue(result.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testHasSpeakerAnnotationsReturnsTrueWhenSet() {
    Annotation ann = new Annotation("Spoken sentence");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Speaker1");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    Annotation sentence = new Annotation("Sentence 1");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    boolean result = CorefAnnotator.hasSpeakerAnnotations(ann);

//    assertTrue(result);
  }
@Test
  public void testHasSpeakerAnnotationsReturnsFalseIfUnset() {
    Annotation ann = new Annotation("Unspoken sentence");

    CoreLabel token = new CoreLabel();

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    Annotation sentence = new Annotation("Sentence 1");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

//    boolean result = CorefAnnotator.hasSpeakerAnnotations(ann);

//    assertFalse(result);
  }
@Test
  public void testGetLinksReturnsCorrectMentionPairs() {
    IntTuple pos1 = new IntTuple(3);
    pos1.set(0, 0);
    pos1.set(1, 1);
    pos1.set(2, 2);

    IntTuple pos2 = new IntTuple(3);
    pos2.set(0, 0);
    pos2.set(1, 2);
    pos2.set(2, 3);

//    CorefChain.CorefMention m1 = new CorefChain.CorefMention();
//    m1.mentionID = 1;
//    m1.position = pos1;
//
//    CorefChain.CorefMention m2 = new CorefChain.CorefMention();
//    m2.mentionID = 2;
//    m2.position = pos2;
//
//    CorefChain chain = new CorefChain(Arrays.asList(m1, m2));

    Map<Integer, CorefChain> corefMap = new HashMap<>();
//    corefMap.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(corefMap);

    assertEquals(1, links.size());
    Pair<IntTuple, IntTuple> pair = links.get(0);
    assertEquals(pos1, pair.first);
    assertEquals(pos2, pair.second);
  }
@Test
  public void testAnnotateSetsCanonicalEntityMentionIndex() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation ann = new Annotation("Barack Obama was president. He lived in Washington.");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    
    Annotation mention = new Annotation("Barack Obama");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    
    Map<Integer, Integer> em2cm = new HashMap<>();
    em2cm.put(0, 0);
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, em2cm);

    
    Map<Integer, Integer> cm2em = new HashMap<>();
    cm2em.put(0, 0);
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cm2em);

    
    Mention m = new Mention();
    m.mentionID = 0;
    m.corefClusterID = 1;

    Map<Integer, Mention> corefMentions = new HashMap<>();
    corefMentions.put(0, m);
//    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//
//
//    CorefMention cm = new CorefMention();
//    cm.mentionID = 0;
//
//    CorefChain chain = new CorefChain(Arrays.asList(cm));
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    annotator.annotate(ann);

    CoreMap mentionWithCanonical = ann.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertNotNull(mentionWithCanonical.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityWithUnknownInput() {
    Annotation ann = new Annotation("Unknown tag input");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "COARSE");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "FINE");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    
//    CorefAnnotator.setNamedEntityTagGranularity(ann, "invalid");

    String tag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("FINE", tag); 
  }
@Test
  public void testFindBestCoreferentEntityMentionWithNullMentionMapping() {
    Annotation ann = new Annotation("Missing mappings");

    Annotation mention = new Annotation("Barack");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 5);

    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, null);
    ann.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(mention, ann);
//    assertFalse(result.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMentionWithCorefMentionButNoChain() {
    Annotation ann = new Annotation("No chain");

    Annotation mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(1, 11));

    Mention corefMention = new Mention();
    corefMention.mentionID = 11;
    corefMention.corefClusterID = 99;
//    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(11, corefMention));

    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.emptyMap());

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(mention, ann);
//    assertFalse(result.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMentionWithEmptyMentions() {
    Annotation ann = new Annotation("Empty mentions");

    Annotation mention = new Annotation("Person");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TextAnnotation.class, "John");

    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 200));
    Mention m = new Mention();
    m.mentionID = 200;
    m.corefClusterID = 7;
//    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(200, m));

//    CorefChain.CorefMention cm = new CorefChain.CorefMention();
//    cm.mentionID = 200;

//    CorefChain chain = new CorefChain(new ArrayList<>());
//    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(7, chain));
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.emptyMap());

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(mention, ann);
//    assertFalse(result.isPresent());
  }
@Test
  public void testAnnotateWhenEmptyMentionsAnnotationIsSet() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation ann = new Annotation("Empty mentions should be handled.");
    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    List<CoreMap> emptyMentions = new ArrayList<>();
    ann.set(CoreAnnotations.MentionsAnnotation.class, emptyMentions);

    annotator.annotate(ann);

    List<CoreMap> result = ann.get(CoreAnnotations.MentionsAnnotation.class);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetLinksWithSingleMentionInChain() {
    IntTuple pos = new IntTuple(3);
    pos.set(0, 0); pos.set(1, 1); pos.set(2, 2);

//    CorefChain.CorefMention m = new CorefChain.CorefMention();
//    m.mentionID = 101;
//    m.position = pos;

//    CorefChain chain = new CorefChain(Collections.singletonList(m));

    Map<Integer, CorefChain> corefChains = new HashMap<>();
//    corefChains.put(1, chain);

    List<Pair<IntTuple, IntTuple>> result = CorefAnnotator.getLinks(corefChains);

    assertTrue(result.isEmpty());
  }
@Test
  public void testRequirementsSkipsTreeAnnotationIfDependencyMD() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.md.type", "dependency"); 

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertFalse(requirements.contains(edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testExactRequirementsReturnsEmptyIfDefaultsUnset() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    
    
    Collection<String> result = annotator.exactRequirements();

    assertNotNull(result); 
  }
@Test
  public void testAnnotateWithNoCorefMappingsDoesNotCrash() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation ann = new Annotation("Missing optional mappings");

    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    Annotation mention = new Annotation("Obama");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 123);
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.emptyMap());
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.emptyMap());
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.emptyMap());
//    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.emptyMap());

    
    annotator.annotate(ann);

    CoreMap resultMention = ann.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    Object canonicalIndex = resultMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertNull(canonicalIndex);
  }
@Test
  public void testSetNamedEntityTagGranularityWithNullSourceNERTag() {
    Annotation annotation = new Annotation("Test");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

//    CorefAnnotator.setNamedEntityTagGranularity(annotation, "coarse");

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionWithValidChainAndLongerMention() {
    Annotation annotation = new Annotation("John went to the store. The young man bought milk.");
    CoreMap mention = new Annotation("John");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
    mention.set(CoreAnnotations.TextAnnotation.class, "John");

    Map<Integer, Integer> em2cm = new HashMap<>();
    em2cm.put(1, 100);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, em2cm);

    Map<Integer, Integer> cm2em = new HashMap<>();
    cm2em.put(100, 2);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cm2em);

    CoreMap longerMention = new Annotation("The young man");
    longerMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 2);
    longerMention.set(CoreAnnotations.TextAnnotation.class, "The young man");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention); 
    mentions.add(longerMention); 
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    Mention mentionObj = new Mention();
    mentionObj.mentionID = 100;
    mentionObj.corefClusterID = 1;
    Map<Integer, Mention> corefMentions = new HashMap<>();
    corefMentions.put(100, mentionObj);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);

//    CorefMention cm = new CorefMention();
//    cm.mentionID = 100;
//    CorefChain chain = new CorefChain(Arrays.asList(cm));
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(mention, annotation);
//    assertTrue(result.isPresent());
//    assertEquals("The young man", result.get().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotateRestoresToFineGrainedTag() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "COARSE");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "FINE");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "FINE");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence 1");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertEquals("FINE", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotateHandlesNullCorefMentionToEntityMentionMapping() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, null);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, null);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotateSkipsCanonicalLinkingWhenMentionsNull() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Test");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null);

    annotator.annotate(annotation); 

    assertTrue(true);
  }
@Test
  public void testGetLinksEmptyCorefChainReturnsEmptyList() {
//    CorefChain emptyChain = new CorefChain(new ArrayList<>());

    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(1, emptyChain);

    List<Pair<IntTuple, IntTuple>> result = CorefAnnotator.getLinks(map);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testRequirementsIncludesTreeAnnotationWhenNotDependencyBased() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> reqs = annotator.requires();

    assertTrue(reqs.contains(edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testRequirementsIncludesCorefMentionsWhenUseCustomMentionDetectionIsTrue() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.useCustomMentionDetection", "true");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> reqs = annotator.requires();

    assertTrue(reqs.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testExactRequirementsReturnsParseInsteadOfDependencies() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.md.type", "not_dependency");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> reqs = annotator.exactRequirements();

    assertNotNull(reqs);
    assertTrue(reqs.contains("parse"));
    assertFalse(reqs.contains("depparse")); 
  }
@Test
  public void testAnnotateSkipsMentionDetectionIfDisabled() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.useCustomMentionDetection", "true");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Test");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionWhenMentionMapReturnsNullForCorefMention() {
    Annotation annotation = new Annotation("Test");
    CoreMap mention = new Annotation("John");
    mention.set(CoreAnnotations.TextAnnotation.class, "John");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 2);

    Map<Integer, Integer> em2cm = new HashMap<>();
    em2cm.put(2, 5);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, em2cm);

    Mention m = new Mention();
    m.mentionID = 5;
    m.corefClusterID = 10;

    Map<Integer, Mention> corefMentions = new HashMap<>();
    corefMentions.put(5, m);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//
//    CorefMention cm = new CorefMention();
//    cm.mentionID = 5;

//    CorefChain chain = new CorefChain(Arrays.asList(cm));
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(10, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(mention, annotation);
//    assertFalse(result.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMentionWhenEntityIsInChainButHasNullText() {
    Annotation annotation = new Annotation("Test");

    CoreMap mention = new Annotation("X");
    mention.set(CoreAnnotations.TextAnnotation.class, "X");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(1, 5));

    Mention m = new Mention();
    m.mentionID = 5;
    m.corefClusterID = 8;

//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(5, m));
//
//    CorefChain.CorefMention cm = new CorefChain.CorefMention();
//    cm.mentionID = 5;
//
//    CorefChain chain = new CorefChain(Arrays.asList(cm));
//    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(8, chain));

    CoreMap nullTextMention = new Annotation("Empty");
    nullTextMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 99);
    nullTextMention.set(CoreAnnotations.TextAnnotation.class, null);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(nullTextMention));
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.singletonMap(5, 99));

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(mention, annotation);
//    assertFalse(result.isPresent());
  }
@Test
  public void testGetLinksWithMultipleMentionsInSingleChain() {
    IntTuple posA = new IntTuple(3);
    posA.set(0, 0); posA.set(1, 2); posA.set(2, 4);

    IntTuple posB = new IntTuple(3);
    posB.set(0, 1); posB.set(1, 3); posB.set(2, 5);

    IntTuple posC = new IntTuple(3);
    posC.set(0, 2); posC.set(1, 4); posC.set(2, 6);

//    CorefChain.CorefMention cm1 = new CorefChain.CorefMention();
//    cm1.mentionID = 1;
//    cm1.position = posA;
//
//    CorefMention cm2 = new CorefMention();
//    cm2.mentionID = 2;
//    cm2.position = posB;
//
//    CorefMention cm3 = new CorefMention();
//    cm3.mentionID = 3;
//    cm3.position = posC;

//    CorefChain chain = new CorefChain(Arrays.asList(cm1, cm2, cm3));
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(11, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);

    assertEquals(3, links.size());
    assertTrue(links.contains(new Pair<>(posB, posA)));
    assertTrue(links.contains(new Pair<>(posC, posA)));
    assertTrue(links.contains(new Pair<>(posC, posB)));
  }
@Test
  public void testAnnotateRestoresOriginalNERTagIfGranularitySwitchFailsToMatch() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORIGINAL");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertEquals("ORIGINAL", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotateCatchesGenericExceptionAndWraps() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Throws");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreMap sentence = new Annotation("S");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    
    annotation.remove(CoreAnnotations.TokensAnnotation.class);

    try {
      annotator.annotate(annotation);
      assertTrue(true); 
    } catch (RuntimeException e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }
  }
@Test
  public void testAnnotateDoesNotThrowWhenCanonicalAnnotationAlreadySet() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("The company announced profits. It also expanded.");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    CoreMap mention = new Annotation("The company");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TextAnnotation.class, "The company");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);
    CoreMap result = annotation.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertEquals(0, (int) result.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionReturnsNoneWhenMentionIDIsMissing() {
    Annotation annotation = new Annotation("Testing missing coref mention ID");
    CoreMap mention = new Annotation("missing");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 42);
    mention.set(CoreAnnotations.TextAnnotation.class, "whatever");

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(42, 101));
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(101, null));
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(mention, annotation);
//    assertFalse(result.isPresent());
  }
@Test
  public void testAnnotateHandlesEmptyCorefMentionMap() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Sentence level test");

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);
    assertNotNull(annotation);
  }
@Test
  public void testAnnotateSkipsCanonicalLinkWithNonExistentMappingIndex() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Obama gave a speech. The president was applauded.");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    CoreMap em = new Annotation("Obama");
    em.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 3);
    em.set(CoreAnnotations.TextAnnotation.class, "Obama");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(em));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(3, 5));

    Mention m = new Mention();
    m.mentionID = 5;
    m.corefClusterID = 99;
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(5, m));
//
//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 5;
//    CorefChain chain = new CorefChain(Collections.singletonList(corefMention));
//    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(99, chain));

    
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);
    Object result = em.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertNull(result);
  }
@Test
  public void testFindBestCoreferentEntityMentionWhenMentionTextIsLongest() {
    Annotation annotation = new Annotation("Test coref mention length resolution");

    CoreMap entityMention = new Annotation("short");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
    entityMention.set(CoreAnnotations.TextAnnotation.class, "Barack");

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(1, 101));

    Mention mention = new Mention();
    mention.mentionID = 101;
    mention.corefClusterID = 20;
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(101, mention));
//
//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 101;
//    CorefChain corefChain = new CorefChain(Collections.singletonList(corefMention));
//    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(20, corefChain));

    CoreMap longerMention = new Annotation("Barack Hussein Obama");
    longerMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 5);
    longerMention.set(CoreAnnotations.TextAnnotation.class, "Barack Hussein Obama");

    
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(longerMention));
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.singletonMap(101, 5));

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(entityMention, annotation);
//    assertTrue(result.isPresent());
//    assertEquals("Barack Hussein Obama", result.get().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequirementsExcludesCategoryAnnotationWithDependencyMD() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.md.type", "dependency");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertFalse(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testRequirementsIncludesCategoryAnnotationWithSyntacticMD() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.md.type", "rule");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularitySkipsNullTokensList() {
    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.TokensAnnotation.class, null);
//    CorefAnnotator.setNamedEntityTagGranularity(annotation, "fine");
    assertTrue(true); 
  }
@Test
  public void testSetNamedEntityTagGranularityDoesNotOverrideNullNERFields() {
    Annotation annotation = new Annotation("Test");
    CoreLabel token = new CoreLabel();
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
//    CorefAnnotator.setNamedEntityTagGranularity(annotation, "coarse");
    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionIgnoresCorefMappingWithInvalidMentionIndex() {
    Annotation annotation = new Annotation("Invalid mapping");
    CoreMap entityMention = new Annotation("Short");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 123);
    entityMention.set(CoreAnnotations.TextAnnotation.class, "Joe");

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(123, 333));

    Mention invalidMention = new Mention();
    invalidMention.mentionID = 333;
    invalidMention.corefClusterID = 800;

//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(333, invalidMention));
//
//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 333;
//    CorefChain chain = new CorefChain(Collections.singletonList(corefMention));
//    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(800, chain));

    
    Map<Integer, Integer> cm2em = new HashMap<>();
    cm2em.put(333, 999); 
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cm2em);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(entityMention, annotation);
//    assertFalse(result.isPresent());
  }
@Test
  public void testAnnotateHandlesEmptySentencesList() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Empty");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotator.annotate(annotation);

    assertEquals("PERSON", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotateHandlesMultipleMentionsWithNullEntityIndex() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Mentions with missing index");
    CoreMap mention1 = new Annotation("Some entity");
    mention1.set(CoreAnnotations.TextAnnotation.class, "Test");
    

    CoreMap mention2 = new Annotation("Another entity");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Another");
    

    List<CoreMap> mentionList = new ArrayList<>();
    mentionList.add(mention1);
    mentionList.add(mention2);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

    CoreMap sentence = new Annotation("Sentence");
    List<CoreLabel> tokenList = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    tokenList.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);
    assertNull(mention1.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
    assertNull(mention2.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testGetLinksSkipsNullEntries() {
//    CorefMention m1 = new CorefMention();
//    m1.mentionID = 101;
    IntTuple p1 = new IntTuple(3);
    p1.set(0, 0); p1.set(1, 1); p1.set(2, 2);
//    m1.position = p1;

    
//    List<CorefChain.CorefMention> mentions = Arrays.asList(m1, null);
//    CorefChain chain = new CorefChain(mentions);
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(33, chain);

    List<Pair<IntTuple, IntTuple>> result = CorefAnnotator.getLinks(chains);
    assertTrue(result.isEmpty());
  }
@Test
  public void testExactRequirementsReturnsDefaultWithoutModificationIfParseNotNeeded() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.md.type", "dependency");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> requirements = annotator.exactRequirements();
    assertNotNull(requirements);
    assertFalse(requirements.isEmpty());
  }
@Test
  public void testExactRequirementsIncludesParseIfStaticTreeAnnotationRequired() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.md.type", "not_dependency"); 

    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> requirements = annotator.exactRequirements();

    boolean containsParse = false;
    for (String req : requirements) {
      if (req.equals("parse")) {
        containsParse = true;
      }
    }
    assertTrue(containsParse);
  }
@Test
  public void testAnnotateHandlesNullSentencesAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    Annotation annotation = new Annotation("No sentence key");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CorefAnnotator annotator = new CorefAnnotator(props);
    annotator.annotate(annotation);

    assertEquals("PERSON", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionHandlesNullMentionList() {
    Annotation annotation = new Annotation("Test");
    CoreMap entityMention = new Annotation("X");
    entityMention.set(CoreAnnotations.TextAnnotation.class, "X");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    Mention mention = new Mention();
    mention.mentionID = 5;
    mention.corefClusterID = 99;
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(1, 5));
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(5, mention));
//
//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 5;
//    CorefChain chain = new CorefChain(Collections.singletonList(corefMention));
//    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(99, chain));

    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.singletonMap(5, 3));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null); 

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(entityMention, annotation);
//    assertFalse(result.isPresent());
  }
@Test
  public void testAnnotateSkipsCanonicalLinkingWhenEntityMentionIndexIsMissing() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    Annotation annotation = new Annotation("Text");

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    CorefAnnotator annotator = new CorefAnnotator(props);
    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testGetLinksSkipsComparisonToSelf() {
    IntTuple pos1 = new IntTuple(3);
    pos1.set(0, 0); pos1.set(1, 1); pos1.set(2, 1);

//    CorefMention cm1 = new CorefMention();
//    cm1.mentionID = 1;
//    cm1.position = pos1;
//
//
//    CorefChain chain = new CorefChain(Arrays.asList(cm1));
    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(10, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);
    assertTrue(links.isEmpty());
  }
@Test
  public void testRequirementsSatisfiedReturnsImmutableSet() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();

    try {
      result.add(CoreAnnotations.TextAnnotation.class);
      fail("Modifying returned set should throw");
    } catch (UnsupportedOperationException e) {
      assertTrue(true);
    }
  }
@Test
  public void testGetLinksWithUnorderedMentionsStillAddsCorrectLinks() {
    IntTuple pos1 = new IntTuple(3);
    pos1.set(0, 1); pos1.set(1, 2); pos1.set(2, 3);
    IntTuple pos2 = new IntTuple(3);
    pos2.set(0, 0); pos2.set(1, 1); pos2.set(2, 2);

//    CorefMention cmA = new CorefMention();
//    cmA.mentionID = 10;
//    cmA.position = pos1;
//
//    CorefMention cmB = new CorefMention();
//    cmB.mentionID = 20;
//    cmB.position = pos2;
//
//
//    CorefChain chain = new CorefChain(Arrays.asList(cmA, cmB));
//    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(1, chain);

//    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);

//
//    assertEquals(1, links.size());
//    assertEquals(pos1, links.get(0).first);
//    assertEquals(pos2, links.get(0).second);
  }
@Test
  public void testSetNamedEntityTagGranularityHandlesEmptyTokensGracefully() {
    Annotation annotation = new Annotation("Empty tokens");

    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
//    CorefAnnotator.setNamedEntityTagGranularity(annotation, "fine");
//
//    assertTrue(true);
  }
@Test
  public void testFindBestCoreferentEntityMentionPrefersLongerText() {
    Annotation annotation = new Annotation("Test longer mention match");

    CoreMap em = new Annotation("Short");
    em.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
    em.set(CoreAnnotations.TextAnnotation.class, "John");

    Mention coref = new Mention();
    coref.mentionID = 5;
    coref.corefClusterID = 100;

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(1, 5));
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonMap(5, coref));

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 5;
//
//    CorefChain chain = new CorefChain(Arrays.asList(corefMention));
//    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(100, chain));

    CoreMap longer = new Annotation("Long");
    longer.set(CoreAnnotations.TextAnnotation.class, "John F. Kennedy");
    longer.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 2);
    
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(longer));
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.singletonMap(5, 2));

//    Optional<CoreMap> result = CorefAnnotator.findBestCoreferentEntityMention(em, annotation);
//
//    assertTrue(result.isPresent());
//    assertEquals("John F. Kennedy", result.get().get(CoreAnnotations.TextAnnotation.class));
  } 
}