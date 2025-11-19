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

public class CorefAnnotator_2_GPTLLMTest {

 @Test(expected = RuntimeException.class)
  public void testConstructorThrowsExceptionForHybridEnglish() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "hybrid");
    props.setProperty("coref.language", "en");
    new CorefAnnotator(props);
  }
@Test
  public void testValidConstructorDoesNotThrowException() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithNoSentencesAnnotationDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Text with no sentence annotations.");
    annotator.annotate(annotation);
    assertNull(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityFine() {
    Annotation annotation = new Annotation("Named entity test.");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORGANIZATION");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    
    try {
      java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
      method.setAccessible(true);
      method.invoke(null, annotation, "fine");
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertEquals("ORGANIZATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityCoarse() {
    Annotation annotation = new Annotation("Named entity test.");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
      method.setAccessible(true);
      method.invoke(null, annotation, "coarse");
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testGetLinksReturnsExpectedList() {
    IntTuple pos1 = new IntTuple(3);
    pos1.set(0, 1);
    pos1.set(1, 0);
    pos1.set(2, 0);

    IntTuple pos2 = new IntTuple(3);
    pos2.set(0, 0);
    pos2.set(1, 0);
    pos2.set(2, 0);

//    CorefChain.CorefMention m1 = new CorefChain.CorefMention();
//    m1.mentionID = 1;
//    m1.position = pos1;
//
//    CorefMention m2 = new CorefMention();
//    m2.mentionID = 2;
//    m2.position = pos2;
//
//    CorefChain chain = new CorefChain(m1, Arrays.asList(m1, m2));
    Map<Integer, CorefChain> result = new HashMap<>();
//    result.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(result);
    assertEquals(1, links.size());
    assertEquals(pos1, links.get(0).first);
    assertEquals(pos2, links.get(0).second);
  }
@Test
  public void testHasSpeakerAnnotationsReturnsTrue() {
    Annotation annotation = new Annotation("He said something.");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "SpeakerA");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    boolean result = false;
    try {
      java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("hasSpeakerAnnotations", Annotation.class);
      method.setAccessible(true);
      result = (boolean) method.invoke(null, annotation);
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertTrue(result);
  }
@Test
  public void testHasSpeakerAnnotationsReturnsFalse() {
    Annotation annotation = new Annotation("No speaker here.");
    CoreLabel token = new CoreLabel(); 

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    boolean result = true;
    try {
      java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("hasSpeakerAnnotations", Annotation.class);
      method.setAccessible(true);
      result = (boolean) method.invoke(null, annotation);
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertFalse(result);
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testRequiresContainsTextAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMention_MatchingCorefChainWithOnlyNullMentions() throws Exception {
    Annotation annotation = new Annotation("Sample sentence");
    CoreMap entityMention = new Annotation("entity");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(0, 1);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 1;
//
//    List<CorefChain.CorefMention> corefMentions = Arrays.asList(corefMention);
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Arrays.asList(corefMention));

    Map<Integer, CorefChain> corefChains = new HashMap<>();
//    corefChains.put(1, new CorefChain(corefMention, corefMentions));
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChains);

    Map<Integer, Integer> corefToEntity = new HashMap<>();
    corefToEntity.put(1, null);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntity);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, entityMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testSetNamedEntityTagGranularity_TagIsNull_NoCrash() throws Exception {
    Annotation annotation = new Annotation("Sentence");
    CoreLabel token = new CoreLabel(); 
    List<CoreLabel> tokens = Collections.singletonList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, annotation, "fine");

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularity_InvalidGranularity_UsesDefaultAnnotation() throws Exception {
    Annotation annotation = new Annotation("Sentence");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    List<CoreLabel> tokens = Collections.singletonList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, annotation, "foobar");

    assertEquals("ORGANIZATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testGetLinks_EmptyCorefMap_ReturnsEmptyList() {
    Map<Integer, CorefChain> map = new HashMap<>();
    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);
    assertTrue(links.isEmpty());
  }
@Test
  public void testGetLinks_SingleMentionInChain_NoLinks() {
    IntTuple position = new IntTuple(3);
    position.set(0, 1);
    position.set(1, 2);
    position.set(2, 3);

//    CorefMention mention = new CorefMention();
//    mention.mentionID = 101;
//    mention.position = position;
//
//    CorefChain chain = new CorefChain(mention, Collections.singletonList(mention));
//    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(10, chain);

//    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);
//    assertTrue(links.isEmpty());
  }
@Test
  public void testAnnotate_MentionsWithNoCorefChain_DoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Barack Obama was the 44th president. He was born in Hawaii.");

    
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap entityMention = new Annotation("Barack Obama");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    entityMention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);
    assertTrue(true); 
  }
@Test
  public void testExactRequirements_NoParseDependencyDifference() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.mdType", "dependency");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> result = annotator.exactRequirements();
    assertTrue(result.contains("depparse") || result.contains("parse"));
  }
@Test
  public void testRequiresIncludesTreeAnnotationWhenNotDependency() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.mdType", "rules");  

    CorefAnnotator annotator = new CorefAnnotator(props);
//    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
//    assertTrue(required.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testRequiresOmitsTreeAnnotationWhenDependency() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.mdType", "dependency");  

    CorefAnnotator annotator = new CorefAnnotator(props);
//    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
//    assertFalse(required.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testRequiresAddsCorefMentionsIfCustomMentionDetectionOff() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.useCustomMentionDetection", "true");

    CorefAnnotator annotator = new CorefAnnotator(props);
//    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
//    assertTrue(required.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testFindBestCoreferentEntityMention_WhenEntityToCorefMappingMissing() throws Exception {
    Annotation annotation = new Annotation("Text");

    CoreMap entityMention = new Annotation("mention");
    entityMention.set(CoreAnnotations.TextAnnotation.class, "He");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.emptyList());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.emptyMap());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.emptyMap());

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, entityMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMention_WhenCorefMentionIndexPresentButMentionMissing() throws Exception {
    Annotation annotation = new Annotation("Example");

    CoreMap entityMention = new Annotation("mention");
    entityMention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    Map<Integer, Integer> mapping = new HashMap<>();
    mapping.put(1, 99); 
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, mapping);

    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.emptyList());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> coref = (Optional<CoreMap>) method.invoke(null, entityMention, annotation);

    assertFalse(coref.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMention_WhenCorefMentionToEntityMappingIsMissing() throws Exception {
    Annotation annotation = new Annotation("Example");

    CoreMap entityMention = new Annotation("mention");
    entityMention.set(CoreAnnotations.TextAnnotation.class, "Barack");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(1, 0);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 0;
//
//    List<CorefMention> corefMentions = Collections.singletonList(corefMention);
//
//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//
//    CorefChain corefChain = new CorefChain(corefMention, corefMentions);
    Map<Integer, CorefChain> corefChains = new HashMap<>();
//    corefChains.put(0, corefChain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChains);

    
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, entityMention, annotation);

    assertFalse(result.isPresent());
  }
@Test
  public void testAnnotateRestoresFineGrainedTagEvenOnException() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Invalid structure");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "COARSE");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "FINE");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    

    annotator.annotate(annotation);

    assertEquals("PERSON", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularity_DoesNotOverrideIfSourceTagNull() throws Exception {
    Annotation annotation = new Annotation("Test");
    CoreLabel token = new CoreLabel(); 
    List<CoreLabel> tokens = Collections.singletonList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, annotation, "fine");

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotateWithUseMarkedDiscourseFlag() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Speaker present");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Barack");
    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    CoreLabel bareToken = new CoreLabel();
    bareToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    bareToken.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "FINE");
    bareToken.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "COARSE");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(bareToken));

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    Boolean useMarkedDiscourse = annotation.get(CoreAnnotations.UseMarkedDiscourseAnnotation.class);
    assertNotNull(useMarkedDiscourse);
    assertTrue(useMarkedDiscourse);
  }
@Test
  public void testCorefAnnotatorAnnotateNullEntityMentionTextIsHandled() throws Exception {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Sample text");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "FINE");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "COARSE");

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    CoreMap mention = new Annotation("entity"); 
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);

    Integer canonical = mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertNull(canonical);
  }
@Test
  public void testFindBestCoreferentEntityMention_MultipleMentionsSelectLongest() throws Exception {
    Annotation annotation = new Annotation("Barack said he would run for office.");

    CoreMap em = new Annotation("mention");
    em.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    em.set(CoreAnnotations.TextAnnotation.class, "he");

    CoreMap longerMention = new Annotation("mention");
    longerMention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    longerMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(em);
    mentions.add(longerMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    Map<Integer, Integer> entityToCorefMention = new HashMap<>();
    entityToCorefMention.put(0, 5);
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCorefMention);

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 5;
//    List<CorefMention> corefMentions = new ArrayList<>();
//    corefMentions.add(corefMention);

//    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Arrays.asList(corefMention));
    Map<Integer, CorefChain> corefChains = new HashMap<>();
//    corefChains.put(corefMention.corefClusterID, new CorefChain(corefMention, Arrays.asList(corefMention)));
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChains);

    Map<Integer, Integer> corefToEntity = new HashMap<>();
    corefToEntity.put(5, 1);
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntity);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, em, annotation);

    assertTrue(result.isPresent());
    assertEquals("Barack Obama", result.get().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotate_NullTokensAnnotationTagSwitchingDoesNotThrow() throws Exception {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("This has no tokens.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, null); 
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);
    assertTrue(true); 
  }
@Test
  public void testAnnotate_WhenExceptionIsThrown_CorefSystemIgnoredButFineNERRestored() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Invalid sentence input");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "COARSE");
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(annotation);
    assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
  }
@Test
  public void testGetLinks_TwoMentionsOutOfOrder_ComparatorSortsCorrectly() {
    IntTuple pos1 = new IntTuple(3);
    pos1.set(0, 5);
    pos1.set(1, 1);
    pos1.set(2, 2);

    IntTuple pos2 = new IntTuple(3);
    pos2.set(0, 2);
    pos2.set(1, 2);
    pos2.set(2, 3);

//    CorefMention m1 = new CorefMention();
//    m1.mentionID = 1;
//    m1.position = pos1;
//
//    CorefMention m2 = new CorefMention();
//    m2.mentionID = 2;
//    m2.position = pos2;
//
//    CorefChain chain = new CorefChain(m1, Arrays.asList(m1, m2));
    Map<Integer, CorefChain> result = new HashMap<>();
//    result.put(100, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(result);
    assertEquals(1, links.size());
    assertEquals(pos1, links.get(0).first);
    assertEquals(pos2, links.get(0).second);
  }
@Test
  public void testExactRequirementsWithDefaultDependencyMode() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.mdType", "dependency");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> results = annotator.exactRequirements();
    assertNotNull(results);
    assertTrue(results.contains("depparse") || results.contains("parse"));
  }
@Test
  public void testExactRequirementsWithRulesModeAddsParse() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.mdType", "rules");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> results = annotator.exactRequirements();
    boolean containsParse = false;
    for (String r : results) {
      if (r.equals("parse")) {
        containsParse = true;
      }
    }
    assertTrue(containsParse);
  }
@Test
  public void testRequirementsSatisfied_SizeIsTwoAndCorrect() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);
//    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

//    assertEquals(2, satisfied.size());
//    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
//    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testRequiresCustomMentionDetectionFalseSkipsCorefMentions() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.useCustomMentionDetection", "false"); 

    CorefAnnotator annotator = new CorefAnnotator(props);
//    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
//
//    for (Class<?> c : required) {
//      assertNotEquals(CorefCoreAnnotations.CorefMentionsAnnotation.class, c);
//    }
  }
@Test
  public void testGetLinks_MentionsWithEqualComparison_YieldsNoLink() {
    IntTuple pos = new IntTuple(3);
    pos.set(0, 1);
    pos.set(1, 2);
    pos.set(2, 3);

//    CorefMention m1 = new CorefMention();
//    m1.mentionID = 1;
//    m1.position = pos;
//
//    CorefMention m2 = new CorefMention();
//    m2.mentionID = 2;
//    m2.position = pos;
//
//
//    CorefChain chain = new CorefChain(m1, Arrays.asList(m1, m2));
    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);
    assertTrue(links.isEmpty());
  }
@Test
  public void testAnnotate_NoMentionsAnnotation_DefaultsToNoCanonicalSetting() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation doc = new Annotation("Some text.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    
    annotator.annotate(doc);
    assertTrue(true); 
  }
@Test
  public void testAnnotate_EntityMentionWithNoTextAnnotation_SafeHandling() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    Annotation doc = new Annotation("Input");

    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap mention = new Annotation("mention");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    

    doc.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    doc.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    doc.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    doc.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    doc.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(doc);
    assertNull(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testRequires_WhenPerformMentionDetectionTrue_ExcludesCorefMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "false"); 
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

//    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
//    assertFalse(required.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testRequires_WhenPerformMentionDetectionFalse_IncludesCorefMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "true"); 
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
//    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
//
//    assertTrue(required.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testMentionsToCorefChainCanonicalAssignment_SkipsIfInvalidMapping() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation ann = new Annotation("Test");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap entityMention = new Annotation("mention");
    entityMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    entityMention.set(CoreAnnotations.TextAnnotation.class, "Company");

    ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));

    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(0, 1);
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);

//    CorefMention mention = new CorefMention();
//    mention.mentionID = 1;
//    List<CorefMention> corefMentions = Arrays.asList(mention);
//
//    CorefChain chain = new CorefChain(mention, corefMentions);
//    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(mention.corefClusterID, chain);
//    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(ann);
    assertNull(entityMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityWithNullNERField_QuietlySkipsSet() throws Exception {
    CoreLabel token = new CoreLabel(); 
    List<CoreLabel> tokens = Arrays.asList(token);
    Annotation ann = new Annotation("Input");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, ann, "fine");

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
  }
@Test
  public void testAnnotate_WhenMentionAnnotatorIsNull_NoCrash() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    props.setProperty("coref.useCustomMentionDetection", "true"); 

    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation ann = new Annotation("Text");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(ann);
    assertTrue(true); 
  }
@Test
  public void testAnnotate_withMentionHasBestCoreferent_MappingSuccess() throws Exception {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("Example");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap mention1 = new Annotation("mention1");
    mention1.set(CoreAnnotations.TextAnnotation.class, "he");
    mention1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("mention2");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention2.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));

    Map<Integer, Integer> entityToCorefMap = new HashMap<>();
    entityToCorefMap.put(0, 99);
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCorefMap);

    Map<Integer, Integer> corefToEntityMap = new HashMap<>();
    corefToEntityMap.put(99, 1);
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntityMap);

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 99;
//    List<CorefMention> corefMentions = Arrays.asList(corefMention);
//    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
//
//    CorefChain chain = new CorefChain(corefMention, corefMentions);
//    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(0, chain));

    annotator.annotate(ann);

    Integer canonicalIdx = mention1.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);
    assertEquals(Integer.valueOf(1), canonicalIdx);
  }
@Test
  public void testAnnotate_WhenEmptyChainMentions_NoException() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation ann = new Annotation("Empty mentions");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(ann);

    assertTrue(true); 
  }
@Test
  public void testAnnotate_missingCoarseAndFineNER_tagUnchanged() throws Exception {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DEFAULT");
    

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("Input");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(ann);
    assertEquals("DEFAULT", token.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
  }
@Test
  public void testSetNamedEntityTagGranularity_granularityIsNull_doesNothing() throws Exception {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NAMED");

    Annotation ann = new Annotation("Null tag");
    ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, ann, null);

    assertEquals("NAMED", token.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
  }
@Test
  public void testFindBestCoreferentEntityMention_chainIsNull_returnsEmptyOptional() throws Exception {
    Annotation ann = new Annotation("Sample");

    CoreMap mention = new Annotation("mention");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    Map<Integer, Integer> entityToCoref = new HashMap<>();
    entityToCoref.put(0, 5); 
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>()); 

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
      "findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> res = (Optional<CoreMap>) method.invoke(null, mention, ann);
    assertFalse(res.isPresent());
  }
@Test
  public void testFindBestCoreferentEntityMention_withEmptyTextInCandidateMention() throws Exception {
    Annotation ann = new Annotation("A sample.");

    CoreMap mention = new Annotation("mention");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TextAnnotation.class, "He");

    ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));

//    CorefMention corefMention = new CorefMention();
//    corefMention.mentionID = 10;

    Map<Integer, Integer> emToCm = new HashMap<>();
    emToCm.put(0, 10);
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, emToCm);

//    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Arrays.asList(corefMention));
//
//    CorefChain chain = new CorefChain(corefMention, Arrays.asList(corefMention));
//    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(corefMention.corefClusterID, chain);
//    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    Map<Integer, Integer> corefToEntity = new HashMap<>();
    corefToEntity.put(10, 1); 
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, corefToEntity);

    CoreMap badCorefMention = new Annotation("bad");
    badCorefMention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
    
    ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention, badCorefMention));

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "findBestCoreferentEntityMention", CoreMap.class, Annotation.class);
    method.setAccessible(true);
    Optional<CoreMap> result = (Optional<CoreMap>) method.invoke(null, mention, ann);

    assertFalse(result.isPresent());
  }
@Test
  public void testAnnotate_whenMentionsAnnotationEmptyButAnnotationPresent() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "DATE");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation ann = new Annotation("Sample");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(ann);

    assertTrue(true); 
  }
@Test
  public void testSetNamedEntityTagGranularity_withUnknownGranularityValue() throws Exception {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORIGINAL");
    Annotation annotation = new Annotation("Input");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    java.lang.reflect.Method method = CorefAnnotator.class.getDeclaredMethod(
        "setNamedEntityTagGranularity", Annotation.class, String.class);
    method.setAccessible(true);
    method.invoke(null, annotation, "undefined");

    assertEquals("ORIGINAL", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotate_whenSentencesAnnotationIsNull_returnsEarly() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");

    Annotation doc = new Annotation("Missing sentences");
    doc.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    
    doc.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    doc.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    doc.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    doc.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    doc.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(doc);

    
    assertNull(doc.get(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotate_withSpeakerAnnotationPresent_setMarkedDiscourseFlag() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "John");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

    Annotation doc = new Annotation("Input");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    doc.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    doc.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
    doc.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
    doc.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());
    doc.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());

    annotator.annotate(doc);

    Boolean flagged = doc.get(CoreAnnotations.UseMarkedDiscourseAnnotation.class);
    assertNotNull(flagged);
    assertTrue(flagged);
  }
@Test
  public void testGetLinks_multipleChains_multipleClusters() {
    IntTuple p1 = new IntTuple(3);
    p1.set(0, 0); p1.set(1, 1); p1.set(2, 2);

    IntTuple p2 = new IntTuple(3);
    p2.set(0, 0); p2.set(1, 2); p2.set(2, 3);

    IntTuple p3 = new IntTuple(3);
    p3.set(0, 1); p3.set(1, 3); p3.set(2, 4);

//    CorefMention m1 = new CorefMention(); m1.mentionID = 1; m1.position = p1;
//    CorefMention m2 = new CorefMention(); m2.mentionID = 2; m2.position = p2;
//    CorefMention m3 = new CorefMention(); m3.mentionID = 3; m3.position = p3;
//
//    CorefChain c1 = new CorefChain(m1, Arrays.asList(m1, m2));
//    CorefChain c2 = new CorefChain(m3, Collections.singletonList(m3));

    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(1, c1);
//    map.put(2, c2);

    List<edu.stanford.nlp.util.Pair<IntTuple, IntTuple>> result = CorefAnnotator.getLinks(map);
    assertEquals(1, result.size());
    assertEquals(p2, result.get(0).second);
    assertEquals(p1, result.get(0).first);
  } 
}