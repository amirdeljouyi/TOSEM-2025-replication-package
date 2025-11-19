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
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CorefAnnotator_5_GPTLLMTest {

 @Test(expected = RuntimeException.class)
  public void testThrowsOnHybridEnglishConfig() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "hybrid");
    props.setProperty("coref.language", "en");
    new CorefAnnotator(props);
  }
@Test
  public void testAnnotateDoesNotThrowWithMinimalValidAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("John went home.");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.<CoreMap>asList(new Annotation("John went home.")));

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should not throw exception: " + e.getMessage());
    }
  }
@Test
  public void testGetLinksSingleChainSinglePair() {
    IntTuple tupleA = new IntTuple(2);
    tupleA.set(0, 0); tupleA.set(1, 1);

    IntTuple tupleB = new IntTuple(2);
    tupleB.set(0, 0); tupleB.set(1, 0);

//    CorefMention mentionA = new CorefMention(0, 0, 1, "mentionA", 1, 1);
//    CorefMention mentionB = new CorefMention(0, 0, 0, "mentionB", 1, 2);
//
//    mentionA.position = tupleA;
//    mentionB.position = tupleB;
//
//    List<CorefMention> mentionList = new ArrayList<>();
//    mentionList.add(mentionA);
//    mentionList.add(mentionB);

//    CorefChain chain = new CorefChain(1, mentionList);
    Map<Integer, CorefChain> chainMap = new HashMap<>();
//    chainMap.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chainMap);

    assertEquals(1, links.size());
    assertEquals(tupleA, links.get(0).first);
    assertEquals(tupleB, links.get(0).second);
  }
@Test
  public void testRequirementsContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesExpected() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateSkipsWhenSentencesMissing() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("No sentences set");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    

    annotator.annotate(annotation);

    assertFalse(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testCanonicalEntityMentionIndexSetWhenBestMatchExists() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention1 = new Annotation("John");
    mention1.set(CoreAnnotations.TextAnnotation.class, "John");
    mention1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("John Smith");
    mention2.set(CoreAnnotations.TextAnnotation.class, "John Smith");
    mention2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

//    Mention corefMention = new Mention(1, "John");
//    corefMention.mentionID = 100;
//    corefMention.corefClusterID = 1;

//    CorefChain.CorefMention cm1 = new CorefChain.CorefMention(0, 0, 0, "John", 1, 100);
//    CorefMention cm2 = new CorefMention(0, 0, 1, "John Smith", 1, 101);

//    CorefChain chain = new CorefChain(1, Arrays.asList(cm1, cm2));

    Map<Integer, Mention> corefMentionsAnnotation = new HashMap<>();
//    corefMentionsAnnotation.put(100, corefMention);

    Map<Integer, CorefChain> chainMap = new HashMap<>();
//    chainMap.put(1, chain);

    Map<Integer, Integer> emToCm = new HashMap<>();
    emToCm.put(0, 100); 

    Map<Integer, Integer> cmToEm = new HashMap<>();
    cmToEm.put(100, 0);
    cmToEm.put(101, 1);

    List<CoreMap> mentionList = Arrays.asList(mention1, mention2);

    Annotation annotation = new Annotation("sample");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCm);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cmToEm);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentionsAnnotation);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    Annotation sentAnn = new Annotation("sentence");
    sentAnn.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sentAnn);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    Integer resultIndex = mention1.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertNotNull(resultIndex);
    assertEquals(Integer.valueOf(1), resultIndex);
  }
@Test
  public void testAnnotateWithNoMentionsAnnotationPresent() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("John walked home.");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation sent = new Annotation("John walked home.");
    sent.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));

    
    annotator.annotate(annotation);

    assertFalse(annotation.containsKey(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyMentionsList() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Empty mention input.");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation sent = new Annotation("sentence");
    sent.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>()); 

    annotator.annotate(annotation);

    assertTrue(annotation.get(CoreAnnotations.MentionsAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateWithUnmappedMentionToCorefMention() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap unlinkedMention = new Annotation("Barack");
    unlinkedMention.set(CoreAnnotations.TextAnnotation.class, "Barack");
    unlinkedMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    Map<Integer, Integer> emToCm = new HashMap<>(); 

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    tokens.add(token);

    List<CoreMap> sentences = new ArrayList<>();
    Annotation sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sentence);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(unlinkedMention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCm);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    
    assertNull(unlinkedMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateWithCorefChainButNoEntityMentionMapping() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

//    Mention corefMention = new Mention(1, "Obama");
//    corefMention.mentionID = 200;
//    corefMention.corefClusterID = 1;
//
//    CorefMention cm = new CorefMention(0, 0, 0, "Obama", 1, 200);
//    CorefChain chain = new CorefChain(1, Collections.singletonList(cm));

    Map<Integer, Mention> corefMentionsMap = new HashMap<>();
//    corefMentionsMap.put(200, corefMention);

    Map<Integer, CorefChain> chainMap = new HashMap<>();
//    chainMap.put(1, chain);

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    List<CoreMap> mentionList = new ArrayList<>();
    mentionList.add(mention);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    tokens.add(token);

    Annotation sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 200));
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentionsMap);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>()); 

    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testGetLinksWithEmptyChains() {
    Map<Integer, CorefChain> chains = new HashMap<>();
    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);
    assertNotNull(links);
    assertTrue(links.isEmpty());
  }
@Test
  public void testGetLinksWithSingleMentionChain() {
    IntTuple pos = new IntTuple(2);
    pos.set(0, 0);
    pos.set(1, 0);

//    CorefMention mention = new CorefMention(0, 0, 0, "single", 1, 1);
//    mention.position = pos;
//
//    CorefChain chain = new CorefChain(1, Collections.singletonList(mention));
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);

    
    assertNotNull(links);
    assertTrue(links.isEmpty());
  }
@Test
  public void testFineGrainedNERNullDoesNotOverwriteTag() {
    Annotation annotation = new Annotation("Test");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    try {
      java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
      method.setAccessible(true);
      method.invoke(null, annotation, "fine");
    } catch (Exception e) {
      fail("Reflection failed to invoke setNamedEntityTagGranularity: " + e.getMessage());
    }

    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testGetLinksWithMultipleChainsAndMultiplePairs() {
    IntTuple pos1 = new IntTuple(2);
    pos1.set(0, 0);
    pos1.set(1, 1);
    IntTuple pos2 = new IntTuple(2);
    pos2.set(0, 0);
    pos2.set(1, 0);
    IntTuple pos3 = new IntTuple(2);
    pos3.set(0, 1);
    pos3.set(1, 2);

//    CorefMention m1 = new CorefMention(0, 0, 1, "first", 1, 1);
//    m1.position = pos1;
//
//    CorefMention m2 = new CorefMention(0, 0, 0, "second", 1, 2);
//    m2.position = pos2;
//
//    CorefMention m3 = new CorefMention(0, 1, 2, "third", 2, 3);
//    m3.position = pos3;
//
//    CorefChain chain1 = new CorefChain(1, Arrays.asList(m1, m2));
//    CorefChain chain2 = new CorefChain(2, Collections.singletonList(m3));

    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain1);
//    chains.put(2, chain2);

    List<edu.stanford.nlp.util.Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);

    assertEquals(1, links.size());
    assertEquals(pos1, links.get(0).first);
    assertEquals(pos2, links.get(0).second);
  }
@Test
  public void testFindBestCoreferentReturnsEmptyWhenMappingMissing() {
    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    Map<Integer, Integer> emToCmMap = new HashMap<>();
    

    Map<Integer, Mention> corefMentionMap = new HashMap<>();
    Map<Integer, CorefChain> chains = new HashMap<>();
    Map<Integer, Integer> cmToEmMap = new HashMap<>();
    List<CoreMap> mentionsList = Collections.singletonList(mention);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    tokens.add(token);

    Annotation sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionsList);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCmMap);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentionMap);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cmToEmMap);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    annotator.annotate(annotation);

    Object val = mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertNull(val);
  }
@Test
  public void testCanonicalMentionSkippedIfMappedMentionIsNull() {
    CoreMap mention = new Annotation("He");
    mention.set(CoreAnnotations.TextAnnotation.class, "He");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//    Mention corefMention = new Mention(0, "He");
//    corefMention.mentionID = 201;
//    corefMention.corefClusterID = 2;

//    CorefChain.CorefMention cm = new CorefChain.CorefMention(0, 0, 0, "He", 2, 201);
    

    Map<Integer, Integer> emToCmMap = new HashMap<>();
    emToCmMap.put(0, 201); 

    Map<Integer, Integer> cmToEmMap = new HashMap<>();
    cmToEmMap.put(201, null); 

    Map<Integer, Mention> corefMentionsMap = new HashMap<>();
//    corefMentionsMap.put(201, corefMention);

//    CorefChain chain = new CorefChain(2, Collections.singletonList(cm));
    Map<Integer, CorefChain> chainMap = new HashMap<>();
//    chainMap.put(2, chain);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCmMap);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cmToEmMap);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentionsMap);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateWithExceptionDuringCorefResolution() {
    Annotation annotation = new Annotation("text");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> tokens = Collections.singletonList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation("s1");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, null); 

    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    try {
      annotator.annotate(annotation); 
    } catch (Exception e) {
      fail("Annotator should not throw even if coref fails internally: " + e.getMessage());
    }
  }
@Test
  public void testAnnotateWithEmptyCorefMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "false");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("Alice");
    mention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Alice spoke.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>() {{
      put(0, 999); 
    }});
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testGetLinksWhenChainsHaveReverseOrderMentions() {
    IntTuple pos1 = new IntTuple(2);
    pos1.set(0, 1);
    pos1.set(1, 2);

    IntTuple pos2 = new IntTuple(2);
    pos2.set(0, 0);
    pos2.set(1, 1);

//    CorefMention m1 = new CorefMention(0, 1, 1, "first", 1, 1);
//    m1.position = pos1;
//
//    CorefMention m2 = new CorefMention(0, 0, 0, "second", 1, 2);
//    m2.position = pos2;
//
//    List<CorefMention> mentions = Arrays.asList(m1, m2);
//    CorefChain chain = new CorefChain(1, mentions);

    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);

    List<edu.stanford.nlp.util.Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);

    assertEquals(1, links.size());
    assertEquals(pos1, links.get(0).first);
    assertEquals(pos2, links.get(0).second);
  }
@Test
  public void testAnnotateWithNullSpeakerAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("Test Text");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertFalse(annotation.containsKey(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testCanonicalMappingNotSetWhenTextLengthsAreEqual() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention1 = new Annotation("Joe");
    mention1.set(CoreAnnotations.TextAnnotation.class, "Joe");
    mention1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("Dan");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Dan");
    mention2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    Map<Integer, Integer> emToCm = new HashMap<>();
    emToCm.put(0, 100);

//    Mention corefMention = new Mention(0, "Joe");
//    corefMention.mentionID = 100;
//    corefMention.corefClusterID = 1;
//
//    CorefMention cm1 = new CorefMention(0, 0, 0, "Joe", 1, 100);
//    CorefMention cm2 = new CorefMention(0, 0, 1, "Dan", 1, 101);

    Map<Integer, Mention> corefMentions = new HashMap<>();
//    corefMentions.put(100, corefMention);

    Map<Integer, Integer> cmToEm = new HashMap<>();
    cmToEm.put(100, 0);
    cmToEm.put(101, 1);

//    CorefChain chain = new CorefChain(1, Arrays.asList(cm1, cm2));

    Annotation annotation = new Annotation("Joe greeted Dan.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCm);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cmToEm);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(1, chain));

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    Integer result = mention1.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertNotNull(result);
    
    assertTrue(result == 0 || result == 1);
  }
@Test
  public void testAnnotateSkipsMappingIfMentionIndexNotSet() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("Unknown");
    

    Annotation annotation = new Annotation("Missing index");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateWithNullCorefMentionInMapping() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "false");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("Alice");
    mention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    Map<Integer, Integer> entityToCorefMap = new HashMap<>();
    entityToCorefMap.put(0, 123); 

    Map<Integer, Mention> corefMentions = new HashMap<>(); 

    Map<Integer, CorefChain> corefChains = new HashMap<>();

    Map<Integer, Integer> corefToEntityMap = new HashMap<>();

//    annotationSetup(annotation -> {
//      annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
//      annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCorefMap);
//      annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//      annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChains);
//      annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntityMap);
//
//      CoreLabel token = new CoreLabel();
//      token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
//      token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
//
//      annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
//
//      CoreMap sentence = new Annotation("sentence");
//      sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
//      annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
//
//      annotator.annotate(annotation);
//
//      assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
//    });
  }
@Test
  public void testAnnotateWithNullEntityMentionMapping() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "false");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("Alice");
    mention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//    Mention corefMention = new Mention(1, "Alice");
//    corefMention.mentionID = 123;
//    corefMention.corefClusterID = 456;
//
//    CorefMention cm = new CorefMention(0, 0, 0, "Alice", 456, 123);
//
//    CorefChain chain = new CorefChain(456, Collections.singletonList(cm));

    Map<Integer, Mention> corefMentions = new HashMap<>();
//    corefMentions.put(123, corefMention);

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(0, 123);

    Map<Integer, Integer> corefToEntity = new HashMap<>();
    corefToEntity.put(123, null); 

    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(456, chain);
//
//    annotationSetup(annotation -> {
//      annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
//      annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);
//      annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//      annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);
//      annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntity);
//
//      CoreLabel token = new CoreLabel();
//      token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
//      token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
//
//      annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
//      CoreMap sentence = new Annotation("sentence");
//      sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
//      annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
//
//      annotator.annotate(annotation);
//
//      assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
//    });
  }
@Test
  public void testAnnotateHandlesMissingGranularityTags() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "false");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not fail when missing optional granularity fields");
    }
  }
@Test
  public void testGetLinksWithIdenticalMentionPositions() {
    IntTuple position = new IntTuple(2);
    position.set(0, 0);
    position.set(1, 1);

//    CorefMention m1 = new CorefMention(0, 0, 0, "A", 1, 1);
//    CorefMention m2 = new CorefMention(0, 0, 1, "B", 1, 2);
//    m1.position = position;
//    m2.position = position;
//
//    CorefChain chain = new CorefChain(1, Arrays.asList(m1, m2));
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);

    List<edu.stanford.nlp.util.Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);
    assertTrue("Mentions with same position shouldn't produce link", links.isEmpty());
  }
@Test
  public void testRequirementsSatisfiedOnlyReturnsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(2, satisfied.size());
    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateWithMissingEntityMentionIndexReturnsGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("She");
    
    mention.set(CoreAnnotations.TextAnnotation.class, "She");

    Annotation annotation = new Annotation("She left.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateHandlesEmptyEntityMentionMappingGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.useCustomMentionDetection", "false");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    CoreMap mention = new Annotation("John");
    mention.set(CoreAnnotations.TextAnnotation.class, "John");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("John went home.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateSkipsSpeakerAnnotationWhenNoTextTokens() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>()); 

    Annotation doc = new Annotation("text");
    doc.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>()); 
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    doc.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    doc.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    doc.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
//    doc.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    doc.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    annotator.annotate(doc);

    assertFalse(doc.containsKey(CoreAnnotations.UseMarkedDiscourseAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityWithUnknownGranularityFallsBackToDefault() {
    Annotation annotation = new Annotation("Test input");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      java.lang.reflect.Method m = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
      m.setAccessible(true);
      m.invoke(null, annotation, "invalid"); 
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertEquals("PERSON", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testGetLinksWithTwoMentionsOutOfOrderStillAddsOneLink() {
    IntTuple posA = new IntTuple(2);
    posA.set(0, 0);
    posA.set(1, 1);

    IntTuple posB = new IntTuple(2);
    posB.set(0, 2);
    posB.set(1, 3);

//    CorefMention mentionA = new CorefMention(0, 2, 1, "later", 1, 10);
//    CorefMention mentionB = new CorefMention(0, 0, 0, "earlier", 1, 11);
//
//    mentionA.position = posB;
//    mentionB.position = posA;
//
//    List<CorefMention> mentions = Arrays.asList(mentionA, mentionB);
//
//    CorefChain chain = new CorefChain(1, mentions);
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);

    List<edu.stanford.nlp.util.Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);

    assertEquals(1, links.size());
    assertEquals(posB, links.get(0).first);
    assertEquals(posA, links.get(0).second);
  }
@Test
  public void testCanonicalEntityMentionLongestTextWinsTieOnChain() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.useCustomMentionDetection", "false");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention1 = new Annotation("X");
    mention1.set(CoreAnnotations.TextAnnotation.class, "John");
    mention1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("Y");
    mention2.set(CoreAnnotations.TextAnnotation.class, "John Smith");
    mention2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

//    Mention corefMention = new Mention(1, "John");
//    corefMention.mentionID = 100;
//    corefMention.corefClusterID = 1;

//    CorefMention cm1 = new CorefMention(0, 0, 0, "John", 1, 100);
//    CorefMention cm2 = new CorefMention(0, 0, 1, "John Smith", 1, 101);

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(0, 100);

    Map<Integer, Integer> corefToEntity = new HashMap<>();
    corefToEntity.put(100, 0);
    corefToEntity.put(101, 1);

    Map<Integer, Mention> corefMentions = new HashMap<>();
//    corefMentions.put(100, corefMention);

//    CorefChain chain = new CorefChain(1, Arrays.asList(cm1, cm2));
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);

    Annotation annotation = new Annotation("text");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntity);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    Integer result = mention1.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertEquals(Integer.valueOf(1), result);
  }
@Test
  public void testRequiresIncludesTreeRequirementWhenNotDependencyMentionDetection() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "RULE"); 
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testAnnotateWithMissingCorefMentionToEntityMappingAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "false");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("Barack");
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//    Mention corefMention = new Mention(0, "Barack");
//    corefMention.mentionID = 123;
//    corefMention.corefClusterID = 1;
//
//    CorefMention cm = new CorefMention(0, 0, 0, "Barack", 1, 123);
//    CorefChain chain = new CorefChain(1, Arrays.asList(cm));

    Map<Integer, Mention> corefMentionMap = new HashMap<>();
//    corefMentionMap.put(123, corefMention);

    Map<Integer, Integer> entityMentionToCorefMention = new HashMap<>();
    entityMentionToCorefMention.put(0, 123);

    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, chain);

    Annotation annotation = new Annotation("Barack Obama");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityMentionToCorefMention);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentionMap);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);
    

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new Annotation("s");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionWithNoCandidatesHavingText() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "false");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("XYZ");
    mention.set(CoreAnnotations.TextAnnotation.class, "XYZ");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//    Mention corefMention = new Mention(1, "XYZ");
//    corefMention.mentionID = 200;
//    corefMention.corefClusterID = 1;
//
//    CorefMention cm1 = new CorefMention(0, 0, 0, "", 1, 200);

    CoreMap mappedEntity = new Annotation("");
    

    Map<Integer, Mention> corefMentions = new HashMap<>();
//    corefMentions.put(200, corefMention);

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(0, 200);

    Map<Integer, Integer> corefToEntity = new HashMap<>();
    corefToEntity.put(200, 1); 

    List<CoreMap> mentions = Arrays.asList(mention, mappedEntity);
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, new CorefChain(1, Arrays.asList(cm1)));

    Annotation annotation = new Annotation("test input");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntity);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateHandlesEmptyChainsGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.useCustomMentionDetection", "false");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap mention = new Annotation("Who");
    mention.set(CoreAnnotations.TextAnnotation.class, "Who");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

//    Mention corefMention = new Mention(0, "Who");
//    corefMention.mentionID = 101;
//    corefMention.corefClusterID = 5;

    Map<Integer, Mention> corefMentionMap = new HashMap<>();
//    corefMentionMap.put(101, corefMention);

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(0, 101);

    Map<Integer, Integer> corefToEntity = new HashMap<>();
    corefToEntity.put(101, 0);

    
//    CorefChain emptyChain = new CorefChain(5, new ArrayList<CorefChain.CorefMention>());
    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(5, emptyChain);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntity);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentionMap);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateHandlesNullMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Input without MentionsAnnotation");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertFalse(annotation.containsKey(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateSkipsCorefWhenSentencesAnnotationIsMissing() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Sentence missing test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    

    annotator.annotate(annotation);

    assertFalse(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  } 
}