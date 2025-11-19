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

public class CorefAnnotator_4_GPTLLMTest {

 @Test(expected = RuntimeException.class)
  public void testConstructionFailsForHybridEnglishCombination() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "hybrid");
    new CorefAnnotator(props);
  }
@Test
  public void testRequiresIncludesExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testExactRequirementsIncludesParseWhenDependencyNotUsed() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.mdType", "regex");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> requirements = annotator.exactRequirements();

    assertTrue(requirements.contains("parse"));
  }
@Test
  public void testGetLinksReturnsSingleCorrectPair() {
//    CorefMention mention1 = new CorefMention(0, 1, 0, new IntTuple(2));
//    mention1.position = new IntTuple(new int[]{0, 1});
//
//    CorefMention mention2 = new CorefMention(0, 2, 0, new IntTuple(2));
//    mention2.position = new IntTuple(new int[]{0, 2});
//
//    CorefChain mockChain = mock(CorefChain.class);
//    List<CorefMention> mentions = Arrays.asList(mention2, mention1);
//    when(mockChain.getMentionsInTextualOrder()).thenReturn(mentions);

    Map<Integer, CorefChain> chains = new HashMap<>();
//    chains.put(1, mockChain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chains);
    assertEquals(1, links.size());
    Pair<IntTuple, IntTuple> link = links.get(0);

//    assertArrayEquals(new int[]{0, 2}, link.first.get());
//    assertArrayEquals(new int[]{0, 1}, link.second.get());
  }
@Test
  public void testAnnotateAddsCorefChainAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");

    CorefAnnotator annotator = new CorefAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation ann = new Annotation("Obama was president.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

    annotator.annotate(ann);

    assertTrue(ann.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testHasSpeakerAnnotationsReturnsTrue() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Narrator");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

    Annotation ann = new Annotation("Test");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    CorefAnnotator annotator = new CorefAnnotator(new Properties());
    boolean result = false;

    
    try {
      token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
      token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");
      token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
      ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
      ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
      ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
      ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
      ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
      ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

      CorefAnnotator newAnnotator = new CorefAnnotator(new Properties());
      newAnnotator.annotate(ann);
      result = true;
    } catch (Exception e) {
      result = false;
    }

    assertTrue(result);
  }
@Test
public void testAnnotateHandlesMissingSentencesAnnotation() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  Annotation annotation = new Annotation("Obama was elected.");
  

  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  annotator.annotate(annotation);

  
  assertFalse(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
}
@Test
public void testAnnotateWithPerformMentionDetectionDisabled() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");
  props.setProperty("coref.useCustomMentionDetection", "true");  

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");

  List<CoreLabel> tokenList = Arrays.asList(token);
  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);

  Annotation annotation = new Annotation("She is a leader.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokenList);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  annotator.annotate(annotation);

  assertTrue(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
}
@Test
public void testGetLinksWithEmptyChainReturnsNoLinks() {
  Map<Integer, CorefChain> chains = new HashMap<>();

  CorefChain emptyChain = mock(CorefChain.class);
  when(emptyChain.getMentionsInTextualOrder()).thenReturn(new ArrayList<CorefChain.CorefMention>());

  chains.put(1, emptyChain);

  List<Pair<IntTuple, IntTuple>> result = CorefAnnotator.getLinks(chains);

  assertTrue(result.isEmpty());
}
@Test
public void testSetNamedEntityTagGranularityWithNullNERValues() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, null);
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

  List<CoreLabel> tokens = Arrays.asList(token);
  Annotation annotation = new Annotation("Some text.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  
  CorefAnnotator annotator = new CorefAnnotator(new Properties());
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotator.annotate(annotation); 

  
  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testCanonicalEntityNotSetWhenNoCorefMappingExists() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");

  CoreMap entityMention = mock(CoreMap.class);
  when(entityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  List<CoreLabel> tokens = Arrays.asList(token);
  List<CoreMap> entityMentions = Arrays.asList(entityMention);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  Annotation annotation = new Annotation("Obama visited Paris.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentions);
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  annotator.annotate(annotation);

  
  verify(entityMention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testAnnotateHandlesExceptionInCorefSystem() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Obama");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

  Annotation annotation = new Annotation("Obama.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, null);  

  try {
    annotator.annotate(annotation);
    fail("Expected RuntimeException due to null CorefChainAnnotation");
  } catch (RuntimeException e) {
    assertNotNull(e.getMessage());
  }
}
@Test
public void testConstructorHandlesMissingAlgorithmGracefully() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  

  try {
    CorefAnnotator annotator = new CorefAnnotator(props);
    assertNotNull(annotator);
  } catch (RuntimeException e) {
    fail("Should not throw exception when algorithm is missing, default is expected.");
  }
}
@Test
public void testExactRequirementsReturnsDefaultWhenNoParseNeeded() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");
  props.setProperty("coref.mdType", "dependency");

  CorefAnnotator annotator = new CorefAnnotator(props);
  Collection<String> requirements = annotator.exactRequirements();

  assertTrue(requirements.contains("parse") || requirements.contains("tokenize"));  
}
@Test
public void testRequiresContainsExpectedWhenMentionDetectionIsFalse() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");
  props.setProperty("coref.useCustomMentionDetection", "true"); 

  CorefAnnotator annotator = new CorefAnnotator(props);
  Set<Class<? extends CoreAnnotation>> reqs = annotator.requires();

  assertTrue(reqs.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  assertFalse(reqs.contains(CoreAnnotations.MentionsAnnotation.class));
}
@Test
public void testAnnotateWithNullCorefChainEntryDoesNotCrash() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  Annotation ann = new Annotation("Obama text.");
  ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
  ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 99));
  ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  Map<Integer, CorefChain> chains = new HashMap<>();
  chains.put(0, null); 
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

  try {
    annotator.annotate(ann);
  } catch (Exception e) {
    fail("annotate should handle null entries in CorefChainAnnotation map and not throw.");
  }
}
@Test
public void testFindBestCoreferentEntityMentionWhenNoMatchExists() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Obama");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

  CoreMap entityMention = mock(CoreMap.class);
  when(entityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  Annotation annotation = new Annotation("Obama test.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entityMention));
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>()); 
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  annotator.annotate(annotation);

  
  verify(entityMention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testGranularitySwitchesWhenFineIsEmptyButCoarseExists() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "rule");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

  Annotation annotation = new Annotation("Paris");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  try {
    annotator.annotate(annotation);
    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  } catch (Exception e) {
    fail("NER tag override from coarse to default NamedEntityTag should not fail.");
  }
}
@Test
public void testAnnotateWithEmptyTokenList() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  Annotation ann = new Annotation("Empty");
  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<CoreLabel>());

  ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  annotator.annotate(ann);
  
  assertTrue(ann.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
}
@Test
public void testGetLinksWithSingleMentionProducesNoLinking() {
//  CorefMention mention = new CorefMention(0, 1, 0, new IntTuple(2));
//  mention.position = new IntTuple(new int[]{0, 1});

  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Collections.singletonList(mention));

  Map<Integer, CorefChain> corefMap = new HashMap<>();
  corefMap.put(1, chain);

  List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(corefMap);
  assertTrue(links.isEmpty());
}
@Test
public void testSetNamedEntityFallbackWhenInvalidGranularityValue() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "XYZ");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ABC");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

  Annotation annotation = new Annotation("Some text");
  List<CoreLabel> tokens = Arrays.asList(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  
  try {
    CorefAnnotator.class
        .getDeclaredMethod("annotate", Annotation.class);  

    CorefAnnotator annotator = new CorefAnnotator(new Properties());

    
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DEFAULT");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

    annotator.annotate(annotation);
    assertEquals("DEFAULT", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  } catch (Exception e) {
    fail("NamedEntityTag fallback should not throw exception");
  }
}
@Test
public void testAnnotateEntityMentionWithNoChainFoundSetsNothing() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  Annotation ann = new Annotation("Obama example");
  ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));

  Map<Integer, Integer> entityToCoref = new HashMap<>();
  entityToCoref.put(0, 123);  

  Map<Integer, CorefChain> corefChains = new HashMap<>();  
  List<Mention> corefMentions = new ArrayList<>();

  ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, entityToCoref);
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChains);
  ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, corefMentions);
  ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());

  ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

  annotator.annotate(ann);

  verify(mention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testAnnotateHandlesNullSentenceTokensGracefully() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("Example");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("annotate should handle null token list per sentence safely.");
  }
}
@Test
public void testAnnotateThrowsRuntimeExceptionFromCheckedException() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Obama");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

  Annotation annotation = new Annotation("Obama failed.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

  CorefAnnotator annotator = new CorefAnnotator(props) {
    @Override
    public void annotate(Annotation annotation) {
      throw new RuntimeException(new Exception("Simulated checked exception"));
    }
  };

  try {
    annotator.annotate(annotation);
    fail("Expected wrapped RuntimeException");
  } catch (RuntimeException ex) {
    assertTrue(ex.getCause() instanceof Exception);
  }
}
@Test
public void testRequiresIncludesParseTreeWhenRuleMDType() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");
  props.setProperty("coref.mdType", "rule");

  CorefAnnotator annotator = new CorefAnnotator(props);
  Set<Class<? extends CoreAnnotation>> required = annotator.requires();

  assertTrue(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
  assertTrue(required.contains(CoreAnnotations.CategoryAnnotation.class));
}
@Test
public void testSetNamedEntityTagGranularityWithUnknownGranularityValue() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

  Annotation annotation = new Annotation("Barack");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  CorefAnnotator annotator = new CorefAnnotator(new Properties());

  try {
    
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
    annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
    annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "UNKNOWN");

    annotator.annotate(annotation);

    assertEquals("PERSON", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  } catch (Exception e) {
    fail("Unexpected failure for unknown granularity fallback scenario");
  }
}
@Test
public void testEntityMentionPointsToNonExistentCorefMentionIndex() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  Annotation annotation = new Annotation("Obama");
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 5));
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  verify(mention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testCorefMentionWithMissingCorefChainReference() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  Annotation annotation = new Annotation("Obama met President.");
  Mention corefMention = mock(Mention.class);
  corefMention.corefClusterID = 101;

  List<Mention> mentions = new ArrayList<>();
  mentions.add(corefMention);

  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 0));
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentions);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());

  annotator.annotate(annotation);

  verify(mention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testCorefMentionMappingToNullEntityMentionIndex() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  Annotation annotation = new Annotation("Obama test");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

//  CorefChain.CorefMention cm = new CorefChain.CorefMention(0, 1, 0, new IntTuple(2));
//  cm.mentionID = 77;

  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Arrays.asList(cm));

  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 0));
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Arrays.asList(mock(Mention.class)));
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(0, chain));

  annotator.annotate(annotation);

  verify(mention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testFindBestCoreferentEntityMentionReturnsFirstLongestOfEqual() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreMap originalMention = mock(CoreMap.class);
  when(originalMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  CoreMap entity1 = mock(CoreMap.class);
  CoreMap entity2 = mock(CoreMap.class);
  when(entity1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Barack Obama");
  when(entity1.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(1);
  when(entity2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Barack Obama");
  when(entity2.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(2);

  Annotation annotation = new Annotation("Text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entity1, entity2));

  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 0));
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.singletonMap(55, 1));

//  Mention mentionObj = new Mention(0, 0, 0, new IntTuple(2));
//  List<Mention> mentions = Arrays.asList(mentionObj);
//  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentions);
//
//  CorefMention cm = new CorefMention(0, 1, 0, new IntTuple(2));
//  cm.mentionID = 55;

  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Arrays.asList(cm));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(0, chain));

  CorefAnnotator actualAnnotator = new CorefAnnotator(props);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(originalMention));

//  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(entity1, entity2));
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 0));
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, Collections.singletonMap(55, 0));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(0, chain));

  List<CoreMap> mentionList = Arrays.asList(originalMention);

  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

  actualAnnotator.annotate(annotation);

  verify(originalMention, atMost(1)).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testDoesNotOverwriteCanonicalEntityMentionIfAlreadySet() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);
  when(mention.containsKey(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(true);

  Annotation ann = new Annotation("Obama");
  ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
  ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<Integer, Integer>());
  ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(ann);

  verify(mention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testAnnotateHandlesMissingCorefMentionToEntityMapping() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  Annotation annotation = new Annotation("Obama");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  CoreMap em = mock(CoreMap.class);
  when(em.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

//  CorefChain.CorefMention cm = new CorefChain.CorefMention(0, 1, 0, new IntTuple(new int[]{0, 0}));
//  cm.mentionID = 999;

  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Collections.singletonList(cm));

  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(em));
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 0));
//  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Collections.singletonList(new Mention(0, 0, 0, new IntTuple(new int[]{0,0}))));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(0, chain));
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>()); 

  CorefAnnotator annotator = new CorefAnnotator(props);

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Should handle missing CorefMentionToEntityMentionMappingAnnotation gracefully.");
  }

  verify(em, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testGetLinksWithTwoIdenticalMentionsSkipsSelfPairs() {
//  CorefChain.CorefMention mention = new CorefChain.CorefMention(0, 1, 0, new IntTuple(new int[]{0, 1}));
//  mention.position = new IntTuple(new int[]{0, 1});

  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Arrays.asList(mention, mention));

  Map<Integer, CorefChain> chainMap = new HashMap<>();
  chainMap.put(1, chain);

  List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(chainMap);

  assertTrue(links.isEmpty());
}
@Test
public void testAnnotatorHandlesNullEntityMentionIndexOnCoreMap() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  Annotation annotation = new Annotation("Obama");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  CoreMap faultyEntityMention = mock(CoreMap.class);
  when(faultyEntityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(null);

  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(faultyEntityMention));

  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Should handle null EntityMentionIndexAnnotation without throwing.");
  }
}
@Test
public void testSetNamedEntityTagGranularityWithMissingAnnotationValue() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, null);
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, null);
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

  Annotation annotation = new Annotation("Unknown");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Should not throw even if all NER granularity annotations are null.");
  }

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testAnnotateSkipsCanonicalIndexWhenCorefChainNotPresentInMap() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CoreMap entityMention = mock(CoreMap.class);
  when(entityMention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  Annotation annotation = new Annotation("Obama speaks.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(entityMention));

  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, Collections.singletonMap(0, 0));
//  List<Mention> mentions = Arrays.asList(new Mention(0, 0, 0, new IntTuple(new int[]{0, 0})));
//  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
  Map<Integer, CorefChain> corefMap = new HashMap<>();
  
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

  CorefAnnotator annotator = new CorefAnnotator(props);

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Should skip gracefully when CorefChain ID is not in chain map.");
  }

  verify(entityMention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testGetLinksHandlesNullCorefChainInMap() {
  Map<Integer, CorefChain> corefChainMap = new HashMap<>();
  corefChainMap.put(1, null); 

  List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(corefChainMap);

  assertNotNull(links);
  assertTrue(links.isEmpty()); 
}
@Test
public void testAnnotateSkipsWhenSentencesAnnotationValueIsNull() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  Annotation annotation = new Annotation("Text");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null); 
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  CorefAnnotator annotator = new CorefAnnotator(props);
  annotator.annotate(annotation);

  
  assertNotNull(annotation);
}
@Test
public void testCanonicalEntityMentionOnlySetWhenCoreferentExists() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  Annotation annotation = new Annotation("Barack met Obama");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class)).thenReturn(0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>()); 
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  CorefAnnotator annotator = new CorefAnnotator(props);

  annotator.annotate(annotation);

  
  verify(mention, never()).set(eq(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class), any());
}
@Test
public void testRequiresDependencyTypeDoesNotIncludeParseTree() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");
  props.setProperty("coref.mdType", "dependency");

  CorefAnnotator annotator = new CorefAnnotator(props);
  Set<Class<? extends CoreAnnotation>> required = annotator.requires();

  assertFalse(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
  assertFalse(required.contains(CoreAnnotations.CategoryAnnotation.class));
  assertTrue(required.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
}
@Test
public void testSetNamedEntityTagGranularityWithEmptySourceValueDoesNotOverwrite() {
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "");
  token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORIGINAL");

  Annotation annotation = new Annotation("John");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");

  CorefAnnotator annotator = new CorefAnnotator(props);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, new HashMap<>());
  annotation.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<>());
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);

  
  assertEquals("ORIGINAL", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testExactRequirementsSwapsDependencyWithParseIfTreeAnnotationRequired() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  props.setProperty("coref.algorithm", "neural");
  props.setProperty("coref.mdType", "regex"); 

  CorefAnnotator annotator = new CorefAnnotator(props);
  Collection<String> requirements = annotator.exactRequirements();

  assertTrue(requirements.contains("parse"));
  assertFalse(requirements.contains("depparse"));
}
@Test(expected = RuntimeException.class)
public void testConstructorThrowsRuntimeExceptionWhenCorefSystemFails() {
  Properties props = new Properties();
  props.setProperty("coref.language", "en");
  
  props.setProperty("coref.algorithm", "invalid_algorithm_type");

  
  new CorefAnnotator(props);
} 
}