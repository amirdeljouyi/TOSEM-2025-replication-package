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

public class CorefAnnotator_3_GPTLLMTest {

 @Test(expected = RuntimeException.class)
  public void testConstructorThrowsOnHybridEnglishCombo() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "HYBRID");
    props.setProperty("coref.language", "en");

    new CorefAnnotator(props);
  }
@Test
  public void testSetNamedEntityTagGranularityToCoarseAndFine() throws Exception {
    Annotation annotation = new Annotation("");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PER");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);

    method.invoke(null, annotation, "coarse");
    assertEquals("PER", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));

    method.invoke(null, annotation, "fine");
    assertEquals("PERSON", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testHasSpeakerAnnotationsReturnsTrue() throws Exception {
    Annotation annotation = new Annotation("");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Alice");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "hasSpeakerAnnotations", Annotation.class);
    method.setAccessible(true);
    boolean result = (boolean) method.invoke(null, annotation);

    assertTrue(result);
  }
@Test
  public void testHasSpeakerAnnotationsReturnsFalse() throws Exception {
    Annotation annotation = new Annotation("");
    CoreLabel token = new CoreLabel();

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "hasSpeakerAnnotations", Annotation.class);
    method.setAccessible(true);
    boolean result = (boolean) method.invoke(null, annotation);

    assertFalse(result);
  }
@Test
  public void testGetLinksReturnsExpectedPairs() {
    CorefChain.CorefMention mention1 = mock(CorefChain.CorefMention.class);
    CorefChain.CorefMention mention2 = mock(CorefChain.CorefMention.class);
    CorefChain.CorefMention mention3 = mock(CorefChain.CorefMention.class);

//    mention1.position = new IntTuple(new int[]{0, 1});
//    mention2.position = new IntTuple(new int[]{0, 2});
//    mention3.position = new IntTuple(new int[]{0, 3});
//
//    List<CorefMention> mentions = new ArrayList<>();
//    mentions.add(mention1);
//    mentions.add(mention2);
//    mentions.add(mention3);

    CorefChain chain = mock(CorefChain.class);
//    when(chain.getMentionsInTextualOrder()).thenReturn(mentions);

    Map<Integer, CorefChain> result = new HashMap<>();
    result.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(result);
    assertEquals(3, links.size());
  }
@Test
  public void testRequirementsIncludeNecessaryAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesCorefAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateProcessesWithoutMentionsAnnotationSet() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("John saw Mary.");
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();
    CoreLabel token3 = new CoreLabel();

    token1.set(CoreAnnotations.TextAnnotation.class, "John");
    token2.set(CoreAnnotations.TextAnnotation.class, "saw");
    token3.set(CoreAnnotations.TextAnnotation.class, "Mary");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testExactRequirementsNonDependencyMode() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> reqs = annotator.exactRequirements();

    assertNotNull(reqs);
    assertFalse(reqs.isEmpty());
  }
@Test
  public void testAnnotateSkipsWhenNoSentences() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("No sentence annotation set");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertFalse(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionReturnsEmptyWhenNoCorefMapping() throws Exception {
    Annotation annotation = new Annotation("");

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(999);

    Map<Integer, Integer> emptyMapping = new HashMap<>();
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emptyMapping);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);

    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, entityMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMentionChoosesLongest() throws Exception {
    Annotation annotation = new Annotation("");

    CoreMap input = mock(CoreMap.class);
    when(input.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

    CoreMap longMention = mock(CoreMap.class);
    when(longMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("President Barack Obama");
    when(longMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(42);

    Map<Integer, Integer> emToCm = new HashMap<>();
    emToCm.put(0, 99);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCm);

    Mention mention = new Mention();
    mention.mentionID = 99;
    mention.corefClusterID = 123;
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Arrays.asList(mention));

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 77;
//
//    List<CorefMention> mentionsInChain = new ArrayList<>();
//    mentionsInChain.add(corefMention);

    CorefChain chain = mock(CorefChain.class);
//    when(chain.getMentionsInTextualOrder()).thenReturn(mentionsInChain);

    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(123, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    Map<Integer, Integer> cmToEm = new HashMap<>();
    cmToEm.put(77, 42);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cmToEm);

    List<CoreMap> entityMentions = Arrays.asList(longMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentions);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);

    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, input, annotation);

    assertTrue(result.isPresent());
    assertEquals(longMention, result.get());
  }
@Test
  public void testAnnotateSetsCanonicalIndex() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Jane said she would arrive.");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Jane");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "said");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "she");
    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "would");
    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "arrive");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);
    tokens.add(token5);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    CoreMap janeMention = mock(CoreMap.class);
    CoreMap sheMention = mock(CoreMap.class);

    when(janeMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);
    when(janeMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Jane");

    when(sheMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(1);
    when(sheMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("she");

    List<CoreMap> mentions = Arrays.asList(janeMention, sheMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    Map<Integer, Integer> emToCm = new HashMap<>();
    emToCm.put(0, 10);
    emToCm.put(1, 11);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCm);

    Mention m0 = new Mention();
    m0.mentionID = 10;
    m0.corefClusterID = 1;

    Mention m1 = new Mention();
    m1.mentionID = 11;
    m1.corefClusterID = 1;

    List<Mention> corefMentions = Arrays.asList(m0, m1);
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);

//    CorefMention cm0 = new CorefMention();
//    cm0.mentionID = 10;
//
//    CorefMention cm1 = new CorefMention();
//    cm1.mentionID = 11;

    CorefChain chain = mock(CorefChain.class);
//    when(chain.getMentionsInTextualOrder()).thenReturn(Arrays.asList(cm0, cm1));
    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(1, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    Map<Integer, Integer> cmToEm = new HashMap<>();
    cmToEm.put(10, 0);
    cmToEm.put(11, 1);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cmToEm);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.annotate(annotation);

    verify(sheMention, atLeastOnce()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), eq(0));
  }
@Test
  public void testSetNamedEntityTagGranularityWithInvalidType() throws Exception {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);

    method.invoke(null, annotation, "invalidType");

    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotateWithNullMentionInEntityMentionToCorefMappingAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Sample text");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(5);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

    Map<Integer, Integer> emToCoref = new HashMap<>();
    emToCoref.put(5, null); 
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCoref);

    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testGetLinksReturnsEmptyListOnEmptyInput() {
    Map<Integer, CorefChain> input = new HashMap<>();
    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(input);
    assertTrue(links.isEmpty());
  }
@Test
  public void testGetLinksWithSingleMentionInChain() {
    CorefChain.CorefMention mention = mock(CorefChain.CorefMention.class);
//    mention.position = new IntTuple(new int[]{0, 1});

    CorefChain chain = mock(CorefChain.class);
    when(chain.getMentionsInTextualOrder()).thenReturn(Arrays.asList(mention));

    Map<Integer, CorefChain> input = new HashMap<>();
    input.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(input);
    assertTrue(links.isEmpty());
  }
@Test
  public void testRequirementsWhenCustomMentionDetectionIsTrue() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.useCustomMentionDetection", "true");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required =
        annotator.requires();

    assertTrue(required.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMentionWithMissingMentionInCorefMentionsList() throws Exception {
    Annotation annotation = new Annotation("");

    CoreMap inputMention = mock(CoreMap.class);
    when(inputMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(100);

    Map<Integer, Integer> emToCoref = new HashMap<>();
    emToCoref.put(100, 5);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCoref);

    
    List<Mention> corefMentions = new ArrayList<>();
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);

    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);

    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, inputMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMentionWithNoChainForMention() throws Exception {
    Annotation annotation = new Annotation("");

    CoreMap inputMention = mock(CoreMap.class);
    when(inputMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(8);

    Map<Integer, Integer> emToCoref = new HashMap<>();
    emToCoref.put(8, 2);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCoref);

    Mention mention = new Mention();
    mention.mentionID = 2;
    mention.corefClusterID = 10;

    List<Mention> corefMentions = new ArrayList<>();
    corefMentions.add(mention);

    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);

    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);

    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, inputMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testExactRequirementsParsePrefersParseOverDependencies() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> exactReqs = annotator.exactRequirements();

    assertNotNull(exactReqs);
    assertTrue(exactReqs.contains("parse")); 
  }
@Test
  public void testAnnotateWithExceptionInCorefSystem() {
    Annotation annotation = new Annotation("");

    Properties props = new Properties() {
      @Override
      public String getProperty(String key) {
        if ("coref.language".equals(key)) return "en";
        if ("coref.algorithm".equals(key)) return "neural";
        return super.getProperty(key);
      }
    };

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreMap sentence = mock(CoreMap.class);
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    tokens.add(token);

    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, null);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, null);

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyCorefMentionsAndChains() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Sample");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Sample");
    tokens.add(t1);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotateWithoutEntityToCorefMappingAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Test");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(1);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Obama");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    
    annotator.annotate(annotation);

    
    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotateWithNullMentionsList() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Null mentions check");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "Barack");
    tokens.add(t);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotation.set(CoreAnnotations.MentionsAnnotation.class, null);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    
    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testGetLinksWithMultipleChains() {
//    CorefMention m1 = new CorefMention();
//    m1.position = new IntTuple(new int[]{0, 1});
//    CorefMention m2 = new CorefMention();
//    m2.position = new IntTuple(new int[]{0, 2});
//    CorefMention m3 = new CorefMention();
//    m3.position = new IntTuple(new int[]{0, 3});
//    CorefMention m4 = new CorefMention();
//    m4.position = new IntTuple(new int[]{0, 4});

    CorefChain chain1 = mock(CorefChain.class);
//    when(chain1.getMentionsInTextualOrder()).thenReturn(Arrays.asList(m1, m2));

    CorefChain chain2 = mock(CorefChain.class);
//    when(chain2.getMentionsInTextualOrder()).thenReturn(Arrays.asList(m3, m4));

    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(1, chain1);
    chains.put(2, chain2);

    List<edu.stanford.nlp.util.Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);

    
    assertEquals(2, links.size());
  }
@Test
  public void testAnnotateHandlesSentenceWithoutTokens() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Empty tokens sentence");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityWhenNERValuesAreNull() throws Exception {
    Annotation annotation = new Annotation("NER null");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, annotation, "fine");

    String tag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertNull(tag);
  }
@Test
  public void testFindBestCoreferentEntityMentionNullMentionListReturnsEmptyOptional() throws Exception {
    Annotation annotation = new Annotation("example");

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(7);

    Map<Integer, Integer> emToCoref = new HashMap<>();
    emToCoref.put(7, 1);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCoref);

    Mention m = new Mention();
    m.mentionID = 1;
    m.corefClusterID = 10;
    List<Mention> mentions = new ArrayList<>();
    mentions.add(m);
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentions);

//    CorefMention cm = new CorefMention();
//    cm.mentionID = 42;
//    List<CorefMention> corefList = new ArrayList<>();
//    corefList.add(cm);

    CorefChain chain = mock(CorefChain.class);
//    when(chain.getMentionsInTextualOrder()).thenReturn(corefList);
    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(10, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    Map<Integer, Integer> corefToMention = new HashMap<>();
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToMention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, null);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, entityMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMentionNoMatchingCorefMention() throws Exception {
    Annotation annotation = new Annotation("test");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(55);

    Map<Integer, Integer> emToCoref = new HashMap<>();
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCoref);
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, mention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testRequiresExcludesTreeRequirementWhenUsingDependencyMD() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.mdType", "dependency"); 
    props.setProperty("coref.useCustomMentionDetection", "false");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();

    boolean hasTree = false;
    for (Class<?> clazz : requirements) {
      if (clazz.getSimpleName().contains("TreeAnnotation")) {
        hasTree = true;
      }
    }

    assertFalse(hasTree);
  }
@Test
  public void testRequiresIncludesTreeRequirementWhenNotDependencyMD() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.mdType", "regex"); 
    props.setProperty("coref.useCustomMentionDetection", "false");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();

    boolean hasTree = false;
    for (Class<?> clazz : requirements) {
      if (clazz.getSimpleName().contains("TreeAnnotation")) {
        hasTree = true;
      }
    }

    assertTrue(hasTree);
  }
@Test
  public void testConstructorThrowsRuntimeExceptionWhenCorefSystemFailsToInitialize() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");

    
    props.setProperty("coref.algorithm", "HYBRID");

    boolean exceptionThrown = false;
    try {
      new CorefAnnotator(props);
    } catch (RuntimeException e) {
      exceptionThrown = true;
    }

    assertTrue(exceptionThrown);
  }
@Test
  public void testSetNamedEntityTagGranularitySwitchesFineGrainedIntoNull() throws Exception {
    Annotation annotation = new Annotation("test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, annotation, "fine");

    assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularitySkipsWhenSourceTagIsNull() throws Exception {
    Annotation annotation = new Annotation("test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, annotation, "fine");

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotateWithNoMentionDetectionAndMissingCorefMentionsDoesNotFail() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.useCustomMentionDetection", "true");

    CorefAnnotator corefAnnotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("John goes to school.");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    corefAnnotator.annotate(annotation);

    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotateHandlesExceptionDuringCorefSystemCallGracefully() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Exception in corefSystem");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("annotate() should catch and wrap exceptions internally, not propagate.");
    }

    assertTrue(annotation.containsKey(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityReturnsEmptyForNonexistentChain() throws Exception {
    Annotation annotation = new Annotation("test");
    CoreMap input = mock(CoreMap.class);

    when(input.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(100);

    Map<Integer, Integer> emToCoref = new HashMap<>();
    emToCoref.put(100, 1);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCoref);

    Mention mention = new Mention();
    mention.mentionID = 1;
    mention.corefClusterID = 123;

    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Arrays.asList(mention));

    
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
      "findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, input, annotation);
    
    assertFalse(result.isPresent());
  }
@Test
  public void testAnnotateHandlesNullSpeakerAnnotationSafely() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Speaker check");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    
    
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testExactRequirementsSwitchesToParseIfTreeAnnotationRequired() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.mdType", "regex");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> reqs = annotator.exactRequirements();
    
    assertTrue(reqs.contains("parse"));
    assertFalse(reqs.contains("depparse"));
  }
@Test
  public void testExactRequirementsKeepsDepsIfTreeNotRequired() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.mdType", "dependency");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> reqs = annotator.exactRequirements();

    assertTrue(reqs.contains("depparse"));
  }
@Test
  public void testFindBestCoreferentWhenCorefMentionMapsToNullEntity() throws Exception {
    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(5);
    when(entityMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Obama");

    Annotation annotation = new Annotation("Obama spoke.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(5, 1);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);

    Mention mention = new Mention();
    mention.mentionID = 1;
    mention.corefClusterID = 10;
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonList(mention));

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 99;
//
//    CorefCoreAnnotations.CorefChainAnnotation chainAnnotationKey = CorefCoreAnnotations.CorefChainAnnotation.class;
//
//    CorefMention[] mentionsArray = new CorefMention[]{corefMention};
//    CorefCoreAnnotations.CorefChainAnnotation.class.getClass();
//
//    CorefMap chainMentioned = mock(CorefMap.class);
//
//    List<CorefMention> corefMentions = new ArrayList<>();
//    corefMentions.add(corefMention);

    CorefCoreAnnotations.CorefChainAnnotation chainAnnotation = mock(CorefCoreAnnotations.CorefChainAnnotation.class);
    CorefCoreAnnotations.CorefChainAnnotation.class.getClass();

//    CorefMention mentionWithChain = new CorefMention();
//    mentionWithChain.mentionID = 99;
//    List<CorefMention> chainMentions = Collections.singletonList(mentionWithChain);

    edu.stanford.nlp.coref.data.CorefChain chain = mock(edu.stanford.nlp.coref.data.CorefChain.class);
//    when(chain.getMentionsInTextualOrder()).thenReturn(chainMentions);

    Map<Integer, edu.stanford.nlp.coref.data.CorefChain> chainMap = new HashMap<>();
    chainMap.put(10, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);

    Map<Integer, Integer> corefToEntityMap = new HashMap<>();
    corefToEntityMap.put(99, null); 
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntityMap);

    Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, entityMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testSetNamedEntityTagGranularityUsesDefaultForUnknownType() throws Exception {
    Annotation annotation = new Annotation("example");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCA");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "LOC");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);

    
    method.invoke(null, annotation, "unknown");

    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testHasSpeakerAnnotationsReturnsFalseIfTokensAreMissing() throws Exception {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    Method method = CorefAnnotator.class.getDeclaredMethod("hasSpeakerAnnotations", Annotation.class);
    method.setAccessible(true);

    Boolean result = (Boolean) method.invoke(null, annotation);

    assertFalse(result);
  }
@Test
  public void testHasSpeakerAnnotationsReturnsFalseIfSentenceListNull() throws Exception {
    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

    Method method = CorefAnnotator.class.getDeclaredMethod("hasSpeakerAnnotations", Annotation.class);
    method.setAccessible(true);
    Boolean result = (Boolean) method.invoke(null, annotation);

    assertFalse(result);
  }
@Test
  public void testAnnotateSkipsMentionProcessingWhenMentionAnnotatorIsNull() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.useCustomMentionDetection", "true");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("No mentions");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Example");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  } 
}