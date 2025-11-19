package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KBPAnnotator_3_GPTLLMTest {

 @Test
  public void testBasicConstruction() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.verbose", "false");
    KBPAnnotator annotator = new KBPAnnotator(props);

    assertNotNull(annotator);
    assertNotNull(annotator.extractor);
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.verbose", "false");
    KBPAnnotator annotator = new KBPAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testRequirementsDefined() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.verbose", "false");
    KBPAnnotator annotator = new KBPAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testConvertRelationNameToLatest_knownMapping() throws Exception {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.verbose", "false");
    KBPAnnotator annotator = new KBPAnnotator(props);

    Method method = KBPAnnotator.class.getDeclaredMethod("convertRelationNameToLatest", String.class);
    method.setAccessible(true);

    Object result = method.invoke(annotator, "per:employee_of");

    assertEquals("per:employee_or_member_of", result);
  }
@Test
  public void testConvertRelationNameToLatest_passThrough() throws Exception {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.verbose", "false");
    KBPAnnotator annotator = new KBPAnnotator(props);

    Method method = KBPAnnotator.class.getDeclaredMethod("convertRelationNameToLatest", String.class);
    method.setAccessible(true);

    Object result = method.invoke(annotator, "per:spouse");

    assertEquals("per:spouse", result);
  }
@Test
  public void testIsPronominalMentionPositive() throws Exception {
    CoreLabel label = new CoreLabel();
    label.setWord("he");

    Method method = KBPAnnotator.class.getDeclaredMethod("kbpIsPronominalMention", CoreLabel.class);
    method.setAccessible(true);

    Object result = method.invoke(null, label);

    assertTrue((Boolean) result);
  }
@Test
  public void testIsPronominalMentionNegative() throws Exception {
    CoreLabel label = new CoreLabel();
    label.setWord("airport");

    Method method = KBPAnnotator.class.getDeclaredMethod("kbpIsPronominalMention", CoreLabel.class);
    method.setAccessible(true);

    Object result = method.invoke(null, label);

    assertFalse((Boolean) result);
  }
@Test
  public void testAnnotateEmptyAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation ann = new Annotation("No content");

    annotator.annotate(ann);

    assertNull(ann.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAnnotateSentenceWithNoTriples() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation ann = new Annotation("Barack Obama was born in Hawaii.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setNER("PERSON");
    token1.setIndex(1);
    token1.setSentIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setNER("PERSON");
    token2.setIndex(2);
    token2.setSentIndex(0);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Hawaii");
    token3.setNER("LOCATION");
    token3.setIndex(6);
    token3.setSentIndex(0);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new Annotation("Barack Obama was born in Hawaii.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    CoreMap mention1 = new Annotation("Barack Obama");
    mention1.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreMap mention2 = new Annotation("Hawaii");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Hawaii");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token3));
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);

    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    List<RelationTriple> triples = sentences.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test(expected = RuntimeIOException.class)
  public void testThrowsForInvalidModel() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "invalid/path/to/model");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    new KBPAnnotator(props);
  }
@Test
  public void testSpanishModeCreatesSpanishCorefSystem() {
    Properties props = new Properties();
    props.setProperty("kbp.language", "es");
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testMaxSentenceLength() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.maxlen", "1");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation ann = new Annotation("This is a long sentence.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setNER("O");
    token1.setIndex(1);
    token1.setSentIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setNER("O");
    token2.setIndex(2);
    token2.setSentIndex(0);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("This is a long sentence.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertEquals(0, triples.size());
  }
@Test
public void testRelationTripleAlternateNamesSelfRelationFilteredOut() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  Annotation ann = new Annotation("ACME is also known as ACME.");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("ACME");
  token1.setNER("ORGANIZATION");
  token1.setIndex(1);
  token1.setSentIndex(0);

  List<CoreLabel> tokens = Collections.singletonList(token1);

  CoreMap sentence = new Annotation("ACME is also known as ACME.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  CoreMap mention = new Annotation("ACME");
  mention.set(CoreAnnotations.TextAnnotation.class, "ACME");
  mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention, mention)); 

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class)
      .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);

  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testNullNamedEntityTagMentionIsRemovedFromAcronymMap() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  Annotation ann = new Annotation("Sample text.");

  CoreLabel token = new CoreLabel();
  token.setWord("NASA");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("NASA");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.TextAnnotation.class, "NASA");
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  

  CoreMap sentence = new Annotation("NASA");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  assertTrue(sentence.get(CoreAnnotations.KBPTriplesAnnotation.class).isEmpty());
}
@Test
public void testSpanishCorefSystemCanonicalMentionMapPropagates() {
  Properties props = new Properties();
  props.setProperty("kbp.language", "es");
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Madrid");
  token.setNER("LOCATION");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("Madrid");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.TextAnnotation.class, "Madrid");
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  CoreMap sentence = new Annotation("Madrid es la capital.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Madrid es la capital.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  assertTrue(sentence.get(CoreAnnotations.KBPTriplesAnnotation.class).isEmpty());
}
@Test
public void testAcronymEntityLinkPropagation() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel acronym = new CoreLabel();
  acronym.setWord("UNESCO");
  acronym.setNER("ORGANIZATION");
  acronym.setIndex(1);
  acronym.setSentIndex(0);

  CoreLabel fullNameToken1 = new CoreLabel();
  fullNameToken1.setWord("United");
  fullNameToken1.setNER("ORGANIZATION");
  fullNameToken1.setIndex(2);
  fullNameToken1.setSentIndex(0);

  CoreLabel fullNameToken2 = new CoreLabel();
  fullNameToken2.setWord("Nations");
  fullNameToken2.setNER("ORGANIZATION");
  fullNameToken2.setIndex(3);
  fullNameToken2.setSentIndex(0);

  CoreMap mentionAcronym = new Annotation("UNESCO");
  mentionAcronym.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(acronym));
  mentionAcronym.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mentionAcronym.set(CoreAnnotations.TextAnnotation.class, "UNESCO");
  mentionAcronym.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mentionAcronym.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  mentionAcronym.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  CoreMap mentionFull = new Annotation("United Nations");
  mentionFull.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(fullNameToken1, fullNameToken2));
  mentionFull.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mentionFull.set(CoreAnnotations.TextAnnotation.class, "United Nations");
  mentionFull.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  mentionFull.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
  mentionFull.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  mentionFull.set(CoreAnnotations.WikipediaEntityAnnotation.class, "United_Nations");

  CoreMap sentence = new Annotation("UNESCO and United Nations are involved.");
  List<CoreLabel> allTokens = Arrays.asList(acronym, fullNameToken1, fullNameToken2);
  sentence.set(CoreAnnotations.TokensAnnotation.class, allTokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mentionAcronym, mentionFull));
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("UNESCO and United Nations are involved.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<CoreLabel> tokens = mentionAcronym.get(CoreAnnotations.TokensAnnotation.class);
  String wikiLink = tokens.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class);

  assertEquals("United_Nations", wikiLink);
}
@Test
public void testAnnotateWithMaxSentenceLengthZeroSkipsAllSentences() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.maxlen", "0");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Obama");
  token.setNER("PERSON");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("Obama");
  mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreMap sentence = new Annotation("Obama visited Texas.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Obama visited Texas.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  
  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testNoRelationDueToNERTypeMismatch() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Paris");
  token1.setNER("LOCATION");
  token1.setIndex(1);
  token1.setSentIndex(0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Banana");
  token2.setNER("MISC");
  token2.setIndex(2);
  token2.setSentIndex(0);

  CoreMap mention1 = new Annotation("Paris");
  mention1.set(CoreAnnotations.TextAnnotation.class, "Paris");
  mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
  mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
  mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreMap mention2 = new Annotation("Banana");
  mention2.set(CoreAnnotations.TextAnnotation.class, "Banana");
  mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
  mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  CoreMap sentence = new Annotation("Paris and Banana.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Paris and Banana.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertTrue(triples.isEmpty());
}
@Test
public void testCorefChainWithSingleTokenMentionOutsideKBPMap() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  Annotation ann = new Annotation("Sample text.");
  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token = new CoreLabel();
  token.setWord("Bob");
  token.setIndex(1);
  token.setSentIndex(0);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
  tokens.add(token);

  CoreMap sentence = new Annotation("Bob");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  CorefChain.CorefMention corefMention = new CorefChain.CorefMention(0, 1, 1, 2, 1, "Bob", false);
//  CorefChain chain = new CorefChain(1, Collections.singletonList(corefMention));
  Map<Integer, CorefChain> chains = new HashMap<>();
//  chains.put(1, chain);
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testCorefChainWithMultipleKBPMentionsSameLength() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  Annotation ann = new Annotation("John met him.");
  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token1 = new CoreLabel();
  token1.setWord("John");
  token1.setNER("PERSON");
  token1.setIndex(1);
  token1.setSentIndex(0);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("him");
  token2.setNER("PERSON");
  token2.setIndex(3);
  token2.setSentIndex(0);
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  tokens.add(token1);
  tokens.add(token2);

  CoreMap mention1 = new Annotation("John");
  mention1.set(CoreAnnotations.TextAnnotation.class, "John");
  mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
  mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreMap mention2 = new Annotation("him");
  mention2.set(CoreAnnotations.TextAnnotation.class, "him");
  mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  List<CoreMap> mentions = Arrays.asList(mention1, mention2);

  CoreMap sentence = new Annotation("John met him.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  HashMap<Pair<Integer, Integer>, CoreMap> kbpMentions = new HashMap<>();
  kbpMentions.put(new Pair<>(0, 4), mention1);
  kbpMentions.put(new Pair<>(9, 12), mention2);

//  CorefChain.CorefMention cm1 = new CorefChain.CorefMention(0, 1, 1, 2, 1, "John", false);
//  CorefChain.CorefMention cm2 = new CorefChain.CorefMention(0, 3, 3, 4, 1, "him", false);
//  CorefChain chain = new CorefChain(1, Arrays.asList(cm1, cm2));
  Map<Integer, CorefChain> chains = new HashMap<>();
//  chains.put(1, chain);

  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

//  Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, ann, kbpMentions);

//  assertEquals(2, result.first.size());
//  assertTrue(result.first.contains(mention1));
//  assertTrue(result.first.contains(mention2));
//  assertNotNull(result.second);
//  assertEquals(mention1.get(CoreAnnotations.TextAnnotation.class).length(),
//               result.second.get(CoreAnnotations.TextAnnotation.class).length());
}
@Test
public void testCanonicalMentionFallbackWhenNoNER() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  Properties noNERProps = new Properties(props);

  KBPAnnotator annotator = new KBPAnnotator(noNERProps);

  Annotation ann = new Annotation("Example sentence.");
  CoreLabel token = new CoreLabel();
  token.setWord("XCorp");
  token.setIndex(1);
  token.setSentIndex(0);
  token.setNER(null);

  CoreMap mention = new Annotation("XCorp");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.TextAnnotation.class, "XCorp");
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  

  CoreMap sentence = new Annotation("XCorp reports earnings.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testNullGraphAnnotationHandledGracefully() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Obama");
  token.setNER("PERSON");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("Obama");
  mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreMap sentence = new Annotation("Obama went home.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  

  Annotation ann = new Annotation("Obama went home.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testRelationTripleConfidenceOverridesDuplicate() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreMap sentence = new Annotation("Sample input.");

  CoreLabel subj = new CoreLabel();
  subj.setWord("John");
  subj.setNER("PERSON");
  subj.setIndex(1);
  subj.setSentIndex(0);
  List<CoreLabel> subjTokens = Collections.singletonList(subj);

  CoreLabel obj = new CoreLabel();
  obj.setWord("Google");
  obj.setNER("ORGANIZATION");
  obj.setIndex(2);
  obj.setSentIndex(0);
  List<CoreLabel> objTokens = Collections.singletonList(obj);

  RelationTriple first = new RelationTriple.WithLink(
      subjTokens, subjTokens,
      Collections.singletonList(new CoreLabel(new Word("per:employee_of"))),
      objTokens, objTokens,
      0.5,
      new SemanticGraph(),
      null, null
  );

  RelationTriple duplicate = new RelationTriple.WithLink(
      subjTokens, subjTokens,
      Collections.singletonList(new CoreLabel(new Word("per:employee_of"))),
      objTokens, objTokens,
      0.9, 
      new SemanticGraph(),
      null, null
  );

  Map<String, RelationTriple> map = new HashMap<>();
  map.put("John\tper:employee_of\tGoogle", first);
  if (duplicate.confidence > first.confidence) {
    map.put("John\tper:employee_of\tGoogle", duplicate);
  }

  RelationTriple finalTriple = map.get("John\tper:employee_of\tGoogle");
  assertEquals(0.9, finalTriple.confidence, 0.001);
}
@Test
public void testAnnotateWithEmptyMentionListNoException() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Apple");
  token.setIndex(1);
  token.setSentIndex(0);
  token.setNER("ORGANIZATION");

  CoreMap sentence = new Annotation("Apple releases earnings.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation doc = new Annotation("Apple releases earnings.");
  doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(doc);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testAcronymMatchDoesNotTriggerWithoutProperNER() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  Annotation ann = new Annotation("NASA and National Aeronautics Space Administration are related.");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("NASA");
  token1.setNER(null);
  token1.setIndex(1);
  token1.setSentIndex(0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Administration");
  token2.setNER(null);
  token2.setIndex(7);
  token2.setSentIndex(0);

  CoreMap mention1 = new Annotation("NASA");
  mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
  mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention1.set(CoreAnnotations.TextAnnotation.class, "NASA");
  mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreMap mention2 = new Annotation("National Aeronautics Space Administration");
  mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention2.set(CoreAnnotations.TextAnnotation.class, "National Aeronautics Space Administration");
  mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 45);

  CoreMap sentence = new Annotation("NASA and National Aeronautics Space Administration are related.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testRelationTripleRejectedDueToSubjectEqualsObject() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("IBM");
  token.setNER("ORGANIZATION");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("IBM");
  mention.set(CoreAnnotations.TextAnnotation.class, "IBM");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

  CoreMap sentence = new Annotation("IBM is also IBM.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token, token));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention, mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("IBM is also IBM.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  for (RelationTriple triple : triples) {
    assertNotEquals("IBM", triple.subjectGloss());
    assertNotEquals(triple.subjectGloss(), triple.objectGloss());
  }
}
@Test
public void testTitlePersonMatcherDoesNotMatchPartialMentions() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel t1 = new CoreLabel();
  t1.setWord("President");
  t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  t1.setNER("TITLE");
  t1.setIndex(1);
  t1.setSentIndex(0);
  t1.setBeginPosition(0);
  t1.setEndPosition(9);

  CoreLabel t2 = new CoreLabel();
  t2.setWord("Lincoln");
  t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  t2.setNER("PERSON");
  t2.setIndex(2);
  t2.setSentIndex(0);
  t2.setBeginPosition(10);
  t2.setEndPosition(17);

  CoreMap mention = new Annotation("President Lincoln");
  mention.set(CoreAnnotations.TextAnnotation.class, "President Lincoln");
  mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION"); 
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

  CoreMap sentence = new Annotation("President Lincoln gave a speech.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("President Lincoln gave a speech.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testMentionPropagationWithoutEntityLink() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("FBI");
  token1.setNER("ORGANIZATION");
  token1.setIndex(1);
  token1.setSentIndex(0);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Federal");
  token2.setNER("ORGANIZATION");
  token2.setIndex(2);
  token2.setSentIndex(0);
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

  CoreMap mentionShort = new Annotation("FBI");
  mentionShort.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
  mentionShort.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mentionShort.set(CoreAnnotations.TextAnnotation.class, "FBI");
  mentionShort.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  mentionShort.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mentionShort.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

  CoreMap mentionLong = new Annotation("Federal");
  mentionLong.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  mentionLong.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mentionLong.set(CoreAnnotations.TextAnnotation.class, "Federal");
  mentionLong.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  mentionLong.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  mentionLong.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

  CoreMap sentence = new Annotation("FBI stands for Federal Bureau of Investigation.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mentionShort, mentionLong));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("FBI stands for Federal Bureau of Investigation.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testAnnotateWithNoNEROnCanonMention_EmptyEntityLinkPropagation() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("CEO");
  token.setNER(null);
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("CEO");
  mention.set(CoreAnnotations.TextAnnotation.class, "CEO");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

  CoreMap sentence = new Annotation("CEO announced...");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("CEO announced...");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testAnnotateWithWikipediaEntityPropagation() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("UN");
  token1.setNER("ORGANIZATION");
  token1.setIndex(1);
  token1.setSentIndex(0);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Nations");
  token2.setNER("ORGANIZATION");
  token2.setIndex(2);
  token2.setSentIndex(0);
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

  CoreMap mention1 = new Annotation("UN");
  mention1.set(CoreAnnotations.TextAnnotation.class, "UN");
  mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
  mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
  mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  mention1.set(CoreAnnotations.WikipediaEntityAnnotation.class, "United_Nations");

  CoreMap mention2 = new Annotation("Nations");
  mention2.set(CoreAnnotations.TextAnnotation.class, "Nations");
  mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
  mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  CoreMap sentence = new Annotation("UN is part of United Nations.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());
  
  Annotation ann = new Annotation("UN is part of United Nations.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  String propagated = token2.get(CoreAnnotations.WikipediaEntityAnnotation.class);
  assertEquals("United_Nations", propagated);
}
@Test
public void testAnnotateSkipsDueToNERTagTypeMismatch() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel subjTok = new CoreLabel();
  subjTok.setWord("Paris");
  subjTok.setNER("LOCATION");
  subjTok.setIndex(1);
  subjTok.setSentIndex(0);

  CoreLabel objTok = new CoreLabel();
  objTok.setWord("Jazz");
  objTok.setNER("MISC");
  objTok.setIndex(2);
  objTok.setSentIndex(0);

  CoreMap subjMention = new Annotation("Paris");
  subjMention.set(CoreAnnotations.TextAnnotation.class, "Paris");
  subjMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
  subjMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(subjTok));
  subjMention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  subjMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  subjMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreMap objMention = new Annotation("Jazz");
  objMention.set(CoreAnnotations.TextAnnotation.class, "Jazz");
  objMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
  objMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(objTok));
  objMention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  objMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  objMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

  CoreMap sentence = new Annotation("Paris hosts Jazz Festival.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subjTok, objTok));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(subjMention, objMention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Paris hosts Jazz Festival.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testAcronymLoopCutoffAfter1000Checks() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  List<CoreMap> mentions = new ArrayList<>();
  for (int i = 0; i < 1005; i++) {
    CoreLabel t = new CoreLabel();
    t.setWord("Entity" + i);
    t.setNER("ORGANIZATION");
    t.setIndex(i + 1);
    t.setSentIndex(0);
    CoreMap m = new Annotation("Entity" + i);
    m.set(CoreAnnotations.TextAnnotation.class, "Entity" + i);
    m.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    m.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t));
    mentions.add(m);
  }

  CoreLabel acronymTok = new CoreLabel();
  acronymTok.setWord("ABC");
  acronymTok.setNER("ORGANIZATION");
  acronymTok.setIndex(1006);
  acronymTok.setSentIndex(0);

  CoreMap acronymMention = new Annotation("ABC");
  acronymMention.set(CoreAnnotations.TextAnnotation.class, "ABC");
  acronymMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  acronymMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(acronymTok));
  mentions.add(acronymMention);

  Map<CoreMap, Set<CoreMap>> mentionMap = new HashMap<>();
  for (CoreMap m : mentions) {
    Set<CoreMap> cluster = new LinkedHashSet<>();
    cluster.add(m);
    mentionMap.put(m, cluster);
  }

  try {
    Method method = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
    method.setAccessible(true);
    method.invoke(null, mentions, mentionMap);
  } catch (Exception e) {
    fail("Invocation of acronymMatch failed: " + e.getMessage());
  }

  
  for (Set<CoreMap> cluster : mentionMap.values()) {
    assertTrue(cluster.size() <= 2); 
  }
}
@Test
public void testAnnotateDoesNotFailWithNullCorefChainAnnotation() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Mars");
  token.setNER("LOCATION");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("Mars");
  mention.set(CoreAnnotations.TextAnnotation.class, "Mars");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreMap sentence = new Annotation("Mars is red.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Mars is red.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, null); 

  annotator.annotate(ann);
  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testConvertRelationNameToLatestHandlesNullInputGracefully() throws Exception {
  Properties properties = new Properties();
  properties.setProperty("kbp.model", "none");
  properties.setProperty("kbp.tokensregex", "none");
  properties.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(properties);

  Method method = KBPAnnotator.class.getDeclaredMethod("convertRelationNameToLatest", String.class);
  method.setAccessible(true);
  Object result = method.invoke(annotator, new Object[] { null });

  assertNull(result);
}
@Test
public void testAnnotateWithEmptyTokenListInMention() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreMap mention = new Annotation("Empty");
  mention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.TextAnnotation.class, "Empty");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  CoreMap sentence = new Annotation("Test sentence.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Test sentence.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, null);

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testAnnotateSkipsSentenceDueToIndexOutOfBoundsOnMentionTokenIndex() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Obama");
  token1.setNER("PERSON");
  token1.setIndex(1);
  token1.setSentIndex(0);
  tokens.add(token1);

  CoreMap sentence = new Annotation("Obama leads.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());
  sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  Annotation ann = new Annotation("Obama leads.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
//
//  CorefChain.CorefMention corefMention = new CorefChain.CorefMention(0, 5, 5, 6, 0, "Obama", false);
//  CorefChain chain = new CorefChain(0, Collections.singletonList(corefMention));
  Map<Integer, CorefChain> chains = new HashMap<>();
//  chains.put(0, chain);
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

  
  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testAnnotateHandlesMentionWithNullNER() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("X");
  token.setIndex(1);
  token.setSentIndex(0);
  token.setNER(null);

  CoreMap mention = new Annotation("X");
  mention.set(CoreAnnotations.TextAnnotation.class, "X");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

  CoreMap sentence = new Annotation("X went.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("X went.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testPronounToPronounMentionRejectedInCoreference() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("he");
  token1.setNER("PERSON");
  token1.setIndex(1);
  token1.setSentIndex(0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("him");
  token2.setNER("PERSON");
  token2.setIndex(2);
  token2.setSentIndex(0);

  CoreMap mention1 = new Annotation("he");
  mention1.set(CoreAnnotations.TextAnnotation.class, "he");
  mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
  mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

  CoreMap mention2 = new Annotation("him");
  mention2.set(CoreAnnotations.TextAnnotation.class, "him");
  mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
  mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

  CoreMap sentence = new Annotation("he saw him.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("he saw him.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  CorefChain.CorefMention cm1 = new CorefChain.CorefMention(0, 1, 1, 2, 1, "he", false);
//  CorefChain.CorefMention cm2 = new CorefChain.CorefMention(0, 2, 2, 3, 1, "him", false);
//  CorefChain chain = new CorefChain(1, Arrays.asList(cm1, cm2));
  Map<Integer, CorefChain> map = new HashMap<>();
//  map.put(1, chain);
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testAnnotateSkipsRelationWhenSubjectNERIsAbsent() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel subjToken = new CoreLabel();
  subjToken.setWord("John");
  subjToken.setIndex(1);
  subjToken.setSentIndex(0);
  subjToken.setNER(null);

  CoreLabel objToken = new CoreLabel();
  objToken.setWord("IBM");
  objToken.setNER("ORGANIZATION");
  objToken.setIndex(2);
  objToken.setSentIndex(0);

  CoreMap subjMention = new Annotation("John");
  subjMention.set(CoreAnnotations.TextAnnotation.class, "John");
  subjMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
  subjMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(subjToken));
  subjMention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  subjMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  subjMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreMap objMention = new Annotation("IBM");
  objMention.set(CoreAnnotations.TextAnnotation.class, "IBM");
  objMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  objMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(objToken));
  objMention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  objMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  objMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

  CoreMap sentence = new Annotation("John works for IBM.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subjToken, objToken));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(subjMention, objMention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("John works for IBM.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testAnnotateSkipsLongSentenceWhenMaxLenIsSet() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.maxlen", "3"); 
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Barack");
  token1.setNER("PERSON");
  token1.setIndex(1);
  token1.setSentIndex(0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Obama");
  token2.setNER("PERSON");
  token2.setIndex(2);
  token2.setSentIndex(0);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("was");
  token3.setNER("O");
  token3.setIndex(3);
  token3.setSentIndex(0);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("born");
  token4.setNER("O");
  token4.setIndex(4);
  token4.setSentIndex(0);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

  CoreMap mention = new Annotation("Barack Obama");
  mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  CoreMap sentence = new Annotation("Barack Obama was born.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Barack Obama was born.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
  assertTrue(triples.isEmpty());
}
@Test
public void testAnnotateDoesNotFailWhenMentionToCanonicalMentionMapIsIncomplete() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.setWord("Jeff");
  token.setNER("PERSON");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("Jeff");
  mention.set(CoreAnnotations.TextAnnotation.class, "Jeff");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreMap sentence = new Annotation("Jeff spoke.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Jeff spoke.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
  assertNotNull(triples);
}
@Test
public void testRelationTripleGlossRejectsAlternateNamesSelfRef() throws Exception {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator(props);

  CoreLabel subjToken = new CoreLabel();
  subjToken.setWord("ACME");
  subjToken.setNER("ORGANIZATION");

  CoreLabel objToken = new CoreLabel();
  objToken.setWord("ACME");
  objToken.setNER("ORGANIZATION");

  RelationTriple triple = new RelationTriple.WithLink(
    Collections.singletonList(subjToken),
    Collections.singletonList(subjToken),
    Collections.singletonList(new CoreLabel(new Word("org:alternate_names"))),
    Collections.singletonList(objToken),
    Collections.singletonList(objToken),
    0.9,
    new SemanticGraph(),
    null,
    null
  );

  String subject = triple.subjectGloss();
  String relation = triple.relationGloss();
  String object = triple.objectGloss();

  boolean rejected = subject.equals(object) && relation.endsWith("alternate_names");

  assertTrue(rejected);
}
@Test
public void testAnnotateFailsGracefullyOnInterruptedThread() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  final KBPAnnotator annotator = new KBPAnnotator(props);

  Thread.currentThread().interrupt(); 

  CoreLabel token = new CoreLabel();
  token.setWord("Obama");
  token.setNER("PERSON");
  token.setIndex(1);
  token.setSentIndex(0);

  CoreMap mention = new Annotation("Obama");
  mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreMap sentence = new Annotation("Obama visited.");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

  Annotation ann = new Annotation("Obama visited.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  boolean runtimeInterrupted = false;
  try {
    annotator.annotate(ann);
  } catch (RuntimeInterruptedException e) {
    runtimeInterrupted = true;
  } finally {
    Thread.interrupted(); 
  }

  assertTrue(runtimeInterrupted);
} 
}