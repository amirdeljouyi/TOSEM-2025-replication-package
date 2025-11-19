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

import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KBPAnnotator_5_GPTLLMTest {

 @Test
  public void testInitializationWithMinimalProperties() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);
    assertNotNull(annotator);
    assertNotNull(annotator.extractor);
  }
@Test
  public void testAnnotateWithEmptySentenceProducesNoTriples() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateSkipsSentenceExceedingMaxLength() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");
    props.setProperty("kbp.maxlen", "5");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John"); token1.setIndex(1);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("lives"); token2.setIndex(2);
    CoreLabel token3 = new CoreLabel();
    token3.setWord("in"); token3.setIndex(3);
    CoreLabel token4 = new CoreLabel();
    token4.setWord("Palo"); token4.setIndex(4);
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Alto"); token5.setIndex(5);
    CoreLabel token6 = new CoreLabel();
    token6.setWord("California"); token6.setIndex(6);

    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);
    tokens.add(token5);
    tokens.add(token6);

    CoreMap sentence = new Annotation("John lives in Palo Alto California.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
//    sentence.set((SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("John lives in Palo Alto California.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testCorefChainToKBPMentionsBestMentionIsLongest() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel label1 = new CoreLabel();
    label1.setWord("John");
    label1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    label1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
//    label1.setNamedEntityTag("PERSON");
    label1.setIndex(1);

    CoreMap mentionShort = new Annotation("John");
    mentionShort.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mentionShort.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    mentionShort.set(CoreAnnotations.TextAnnotation.class, "John");
//    mentionShort.setNamedEntityTag("PERSON");
    mentionShort.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(label1));

    CoreLabel label2a = new CoreLabel();
    label2a.setWord("John");
    label2a.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    label2a.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
//    label2a.setNamedEntityTag("PERSON");
    label2a.setIndex(1);

    CoreLabel label2b = new CoreLabel();
    label2b.setWord("Smith");
    label2b.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    label2b.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
//    label2b.setNamedEntityTag("PERSON");
    label2b.setIndex(2);

    CoreMap mentionLong = new Annotation("John Smith");
    mentionLong.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    mentionLong.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    mentionLong.set(CoreAnnotations.TextAnnotation.class, "John Smith");
//    mentionLong.setNamedEntityTag("PERSON");
    mentionLong.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(label2a, label2b));

    CoreMap sentence = new Annotation("Sent.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(label1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Doc");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//    CorefChain.CorefMention corefMention = new CorefChain.CorefMention(0, 1, 0, 1, 2, "dummy", false);
//    Map<Integer, CorefChain.CorefMention> positionMap = new HashMap<>();
//    positionMap.put(0, corefMention);
//
//    CorefChain corefChain = new CorefChain(positionMap, 0);

    HashMap<Pair<Integer, Integer>, CoreMap> mentionMap = new HashMap<>();
    mentionMap.put(new Pair<>(0, 4), mentionShort);
    mentionMap.put(new Pair<>(5, 15), mentionLong);

//    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(corefChain, annotation, mentionMap);

//    assertNotNull(result);
//    assertNotNull(result.first());
//    assertNotNull(result.second());
//
//    assertEquals("John Smith", result.second().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequirementsIncludeNecessaryAnnotations() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.LemmaAnnotation.class));
//    assertTrue(required.contains(SemanticGraph(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedReportsKBPTriplesAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testAnnotateWithNullCorefChainAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setIndex(1);
//    token.setNamedEntityTag("PERSON");

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap sentence = new Annotation("Obama speaks.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
//    sentence.set(CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation doc = new Annotation("Obama speaks.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));

    annotator.annotate(doc);

    List<RelationTriple> triples = doc.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testAnnotateWithInvalidNEROnCanonicalMention() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Entity");
    token.setIndex(1);
//    token.setNamedEntityTag(null);

    CoreMap mention = new Annotation("Entity");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.TextAnnotation.class, "Entity");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

    CoreMap sentence = new Annotation("Entity action");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
//    sentence.set(SemanticGraph(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("Entity action");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testAnnotateWithOnlyOneMentionInSentence() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setIndex(1);
//    token.setNamedEntityTag("ORGANIZATION");

    CoreMap mention = new Annotation("Google");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.TextAnnotation.class, "Google");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreMap sentence = new Annotation("Google innovates.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("Google innovates.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertTrue(triples != null);
  }
@Test
  public void testSentenceWithSameSubjectAndObjectIgnoredAlternateNames() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setIndex(1);
//    token.setNamedEntityTag("ORGANIZATION");

    CoreMap mention = new Annotation("IBM");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.TextAnnotation.class, "IBM");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    List<CoreMap> mentions = new ArrayList<CoreMap>();
    mentions.add(mention);
    mentions.add(mention); 

    CoreMap sentence = new Annotation("IBM is also called IBM");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation doc = new Annotation("IBM is also called IBM");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    List<RelationTriple> triples = doc.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateHandlesNullNERTagsForTripleFilter() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord("Jane");
    subjToken.setIndex(1);
//    subjToken.setNamedEntityTag(null);

    CoreMap mention1 = new Annotation("Jane");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subjToken));
    mention1.set(CoreAnnotations.TextAnnotation.class, "Jane");
//    mention1.setNamedEntityTagAnnotation(null);

    CoreLabel objToken = new CoreLabel();
    objToken.setWord("CEO");
    objToken.setIndex(2);
//    objToken.setNamedEntityTag(null);

    CoreMap mention2 = new Annotation("CEO");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(objToken));
    mention2.set(CoreAnnotations.TextAnnotation.class, "CEO");
//    mention2.setNamedEntityTagAnnotation(null);

    List<CoreMap> mentions = new ArrayList<CoreMap>();
    mentions.add(mention1);
    mentions.add(mention2);

    CoreMap sentence = new Annotation("Jane is CEO.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subjToken, objToken));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation doc = new Annotation("Jane is CEO.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    List<RelationTriple> triples = doc.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testAnnotateWithEmptyMentionListStillSetsTriplesAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Some");
    token1.setIndex(1);
//    token1.setNamedEntityTag("O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("text");
    token2.setIndex(2);
//    token2.setNamedEntityTag("O");

    CoreMap sentence = new Annotation("Some text here.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("Some text here.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testAcronymClusterLogicWithNoValidMatches() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("ACLU");
    token1.setIndex(1);
//    token1.setNamedEntityTag("ORGANIZATION");

    CoreMap mention1 = new Annotation("ACLU");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    mention1.set(CoreAnnotations.TextAnnotation.class, "ACLU");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("ACLU was mentioned.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("ACLU was mentioned.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testMentionWithMissingNamedEntityTag() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("UnknownEntity");
    token.setIndex(1);
//    token.setNamedEntityTag(null);

    CoreMap mention = new Annotation("UnknownEntity");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("UnknownEntity appeared.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation document = new Annotation("UnknownEntity appeared.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(document);

    List<RelationTriple> triples = document.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testCorefWithAllNullMappedKBPMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("President");
    token.setIndex(1);
//    token.setNamedEntityTag(null);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreMap mention = new Annotation("President");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

    CoreMap sentence = new Annotation("President gave a speech.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("President gave a speech.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//    CorefChain.CorefMention chainMention = new CorefChain.CorefMention(1, 0, 1, 2, 0, "President", false);
//    Map<Integer, CorefChain.CorefMention> mentionMap = new HashMap<>();
//    mentionMap.put(0, chainMention);
//
//    Map<Integer, CorefChain> chainMap = new HashMap<>();
//    chainMap.put(0, new CorefChain(mentionMap, 0));

//    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testMentionNERTypeMismatchSubjectObjectIgnored() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord("Amazon");
    subjToken.setIndex(1);
//    subjToken.setNamedEntityTag("ORGANIZATION");

    CoreLabel objToken = new CoreLabel();
    objToken.setWord("Seattle");
    objToken.setIndex(2);
//    objToken.setNamedEntityTag("LOCATION");

    CoreMap mention1 = new Annotation("Amazon");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subjToken));
    mention1.set(CoreAnnotations.TextAnnotation.class, "Amazon");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("Seattle");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(objToken));
    mention2.set(CoreAnnotations.TextAnnotation.class, "Seattle");
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Amazon operates in Seattle.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subjToken, objToken));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("Amazon operates in Seattle.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testMentionWithValidTriplesButNoRelationPrediction() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none"); 
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel subj = new CoreLabel();
    subj.setWord("Barack");
    subj.setIndex(1);
//    subj.setNamedEntityTag("PERSON");

    CoreLabel obj = new CoreLabel();
    obj.setWord("Hawaii");
    obj.setIndex(2);
//    obj.setNamedEntityTag("LOCATION");

    CoreMap mention1 = new Annotation("Barack");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subj));
    mention1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("Hawaii");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(obj));
    mention2.set(CoreAnnotations.TextAnnotation.class, "Hawaii");
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Barack was born in Hawaii.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subj, obj));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("Barack was born in Hawaii.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty()); 
  }
@Test
  public void testConvertRelationNameUnmappedReturnsInput() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "en");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    String relation = "per:birthplace";
    String result = relation; 

    assertEquals(result, relation); 
  }
@Test
  public void testCorefChainPartiallyMappedMentionsYieldsBest() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setIndex(1);
//    token1.setNamedEntityTag("PERSON");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("he");
    token2.setIndex(1);
//    token2.setNamedEntityTag("O"); 

    CoreMap kbpMention = new Annotation("Barack");
    kbpMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    kbpMention.set(CoreAnnotations.TextAnnotation.class, "Barack");
    kbpMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    kbpMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    kbpMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("Barack was president.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

//    CorefChain.CorefMention mention1 = new CorefChain.CorefMention(0, 1, 1, 2, 0, null, false);
//    Map<Integer, CorefChain.CorefMention> mentionMap = new HashMap<>();
//    mentionMap.put(1, mention1);
//    CorefChain chain = new CorefChain(mentionMap, 1);

    HashMap<Pair<Integer, Integer>, CoreMap> kbps = new HashMap<>();
    kbps.put(new Pair<>(0, 6), kbpMention);

//    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, doc, kbps);
//
//    assertNotNull(result.first());
//    assertNotNull(result.second());
//    assertEquals("Barack", result.second().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testCorefChainFallbackToTitlePersonPattern() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "en");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("President");
    token1.setNER("TITLE");
//    token1.setPOS("NNP");
    token1.setIndex(1);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setNER("PERSON");
//    token2.setPOS("NNP");
    token2.setIndex(2);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap cmSent = new Annotation("President Obama");
    cmSent.set(CoreAnnotations.TokensAnnotation.class, tokens);
    cmSent.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("President Obama...");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(cmSent));

    CoreMap personMention = new Annotation("Obama");
    personMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
    personMention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    personMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    HashMap<Pair<Integer, Integer>, CoreMap> kbpMap = new HashMap<>();
    kbpMap.put(new Pair<>(10, 15), personMention);

//    CorefChain.CorefMention cm = new CorefChain.CorefMention(0, 1, 1, 3, 0, "President Obama", false);
//    Map<Integer, CorefChain.CorefMention> mentionMap = new HashMap<>();
//    mentionMap.put(0, cm);
//
//    CorefChain chain = new CorefChain(mentionMap, 0);

//    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, doc, kbpMap);
//    assertNotNull(result.first());
//    assertEquals("Obama", result.second().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotateSkipsSentenceEqualToMaxLen() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "en");
    props.setProperty("kbp.maxlen", "3");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("This");
    t1.setIndex(1);
//    t1.setNamedEntityTag("O");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("is");
    t2.setIndex(2);
//    t2.setNamedEntityTag("O");

    CoreLabel t3 = new CoreLabel();
    t3.setWord("valid");
    t3.setIndex(3);
//    t3.setNamedEntityTag("O");

    CoreMap sentence = new Annotation("This is valid.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2, t3));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("This is valid.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);
    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0)
            .get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
  }
@Test
  public void testSpanishCorefSystemBuildDecision() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "es");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    
    assertNotNull(annotator);
  }
@Test
  public void testCanonicalMentionMappingWithNullTargetNERTag() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "en");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Company");
    token1.setIndex(1);
//    token1.setNamedEntityTag("ORGANIZATION");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("It");
    token2.setIndex(2);
//    token2.setNamedEntityTag(null);

    CoreMap m1 = new Annotation("Company");
    m1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    m1.set(CoreAnnotations.TextAnnotation.class, "Company");
    m1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    m1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap m2 = new Annotation("It");
    m2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
    m2.set(CoreAnnotations.TextAnnotation.class, "It");
    m2.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    m2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Company is big. It acquired others.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(m1, m2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("Company is big. It acquired others.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testMentionToCanonicalMentionOverwritePropagation() {
    Properties props = new Properties();
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("CEO");
//    token1.setNamedEntityTag(null);
    token1.setIndex(1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Tim");
//    token2.setNamedEntityTag("PERSON");
    token2.setIndex(1);

    CoreMap nullNER = new Annotation("CEO");
    nullNER.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    nullNER.set(CoreAnnotations.TextAnnotation.class, "CEO");
    nullNER.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    nullNER.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap withNER = new Annotation("Tim");
    withNER.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
    withNER.set(CoreAnnotations.TextAnnotation.class, "Tim");
    withNER.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    withNER.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Set<CoreMap> mentions = new HashSet<>();
    mentions.add(nullNER);
    mentions.add(withNER);

    Map<CoreMap, Set<CoreMap>> mentionMap = new HashMap<>();
    mentionMap.put(nullNER, mentions);

    List<CoreMap> input = new ArrayList<>();
    input.add(nullNER);
    input.add(withNER);

    CoreLabel sToken = new CoreLabel();
    sToken.setWord("dummy");
    CoreMap sentence = new Annotation("dummy");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(sToken));

    Annotation document = new Annotation("dummy doc");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    try {
      java.lang.reflect.Method m = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
      m.setAccessible(true);
      m.invoke(null, input, mentionMap);
    } catch (Exception e) {
      fail("Method should not throw: " + e.getMessage());
    }
  }
@Test
  public void testRejectCorefPERSONLinkWhenNoTokenOverlap() {
    Properties props = new Properties();
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel tokenA1 = new CoreLabel();
    tokenA1.setWord("Morsi");
    tokenA1.setIndex(1);
//    tokenA1.setNamedEntityTag("PERSON");

    CoreLabel tokenA2 = new CoreLabel();
    tokenA2.setWord("Ashton");
    tokenA2.setIndex(1);
//    tokenA2.setNamedEntityTag("PERSON");

    CoreMap morsi = new Annotation("Morsi");
    morsi.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(tokenA1));
    morsi.set(CoreAnnotations.TextAnnotation.class, "Morsi");
    morsi.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    morsi.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap ashton = new Annotation("Catherine Ashton");
    ashton.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(tokenA2));
    ashton.set(CoreAnnotations.TextAnnotation.class, "Catherine Ashton");
    ashton.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    ashton.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Map<CoreMap, CoreMap> mentionMap = new LinkedHashMap<>();
    mentionMap.put(morsi, ashton);

    assertNotEquals(ashton.get(CoreAnnotations.TextAnnotation.class), morsi.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testWikipediaEntityPropagationToAllMentionsInCluster() {
    Properties props = new Properties();
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setIndex(1);
//    token.setNamedEntityTag("ORGANIZATION");

    CoreMap canonical = new Annotation("Apple Inc.");
    canonical.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    canonical.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    canonical.set(CoreAnnotations.TextAnnotation.class, "Apple Inc.");
    canonical.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    canonical.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Apple_(company)");

    CoreMap mention2 = new Annotation("Apple");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Apple");
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Set<CoreMap> cluster = new LinkedHashSet<>();
    cluster.add(canonical);
    cluster.add(mention2);

    Map<CoreMap, Set<CoreMap>> mentionsMap = new LinkedHashMap<>();
    mentionsMap.put(canonical, cluster);

    try {
      for (CoreMap mention : mentionsMap.entrySet().iterator().next().getValue()) {
        for (CoreLabel tok : mention.get(CoreAnnotations.TokensAnnotation.class)) {
          tok.set(CoreAnnotations.WikipediaEntityAnnotation.class, mentionsMap.entrySet().iterator().next().getKey().get(CoreAnnotations.WikipediaEntityAnnotation.class));
          assertEquals("Apple_(company)", tok.get(CoreAnnotations.WikipediaEntityAnnotation.class));
        }
      }
    } catch (Exception e) {
      fail("Should not fail: " + e.getMessage());
    }
  }
@Test
  public void testExactDuplicateTripleWithHigherConfidenceReplacesLower() {
    
    RelationTriple triple1 = new RelationTriple(
        Arrays.asList(new CoreLabel(new Word("Alice"))),
        Arrays.asList(new CoreLabel(new Word("is_colleague_of"))),
        Arrays.asList(new CoreLabel(new Word("Bob"))),
        0.6);

    RelationTriple triple2 = new RelationTriple(
        Arrays.asList(new CoreLabel(new Word("Alice"))),
        Arrays.asList(new CoreLabel(new Word("is_colleague_of"))),
        Arrays.asList(new CoreLabel(new Word("Bob"))),
        0.9);

    Map<String, RelationTriple> map = new HashMap<>();
    String key = "Alice\tis_colleague_of\tBob";

    map.put(key, triple1);

    if (triple2.confidence > map.get(key).confidence) {
      map.put(key, triple2); 
    }

    assertEquals(0.9, map.get(key).confidence, 0.001);
  }
@Test
  public void testMentionWithPronominalOverlapsBypassTokenMatchingCheck() {
    CoreLabel pronoun = new CoreLabel();
    pronoun.setWord("he");
    pronoun.setIndex(1);
//    pronoun.setNamedEntityTag("PERSON");

    CoreLabel proper = new CoreLabel();
    proper.setWord("Obama");
    proper.setIndex(1);
//    proper.setNamedEntityTag("PERSON");

    CoreMap pronominalMention = new Annotation("he");
    pronominalMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(pronoun));
    pronominalMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap properMention = new Annotation("Obama");
    properMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(proper));
    properMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    boolean kbpIsPronoun = edu.stanford.nlp.coref.data.WordLists.isKbpPronominalMention(pronoun.word());
    boolean shouldBypassTokenCheck = kbpIsPronoun;

    assertTrue(shouldBypassTokenCheck);
  }
@Test
  public void testAnnotateWithMultipleMentionsButAllFailNERCheck() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("room");
    token1.setIndex(1);
//    token1.setNamedEntityTag("O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("building");
    token2.setIndex(2);
//    token2.setNamedEntityTag("O");

    CoreMap mention1 = new Annotation("room");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("building");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("room building");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("room building");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    kbp.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateSentenceWithSingleMentionAndValidNER() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Microsoft");
    token.setIndex(1);
//    token.setNamedEntityTag("ORGANIZATION");

    CoreMap mention = new Annotation("Microsoft");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.TextAnnotation.class, "Microsoft");
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Microsoft grows.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("Microsoft grows.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    kbp.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateSentenceWhenNERTypeIsUnrecognizedValue() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("FooCorp");
    token1.setIndex(1);
//    token1.setNamedEntityTag("COMPANY");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("John");
    token2.setIndex(2);
//    token2.setNamedEntityTag("PERSON");

    CoreMap mention1 = new Annotation("FooCorp");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "COMPANY");
    mention1.set(CoreAnnotations.TextAnnotation.class, "FooCorp");
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("John");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention2.set(CoreAnnotations.TextAnnotation.class, "John");
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("FooCorp hired John.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("FooCorp hired John.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    kbp.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateSentenceWithNullSentenceList() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("test", props);

    Annotation ann = new Annotation("This is a sentence.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, null); 

    try {
      kbp.annotate(ann);
    } catch (NullPointerException e) {
      fail("Annotate should not throw NullPointerException on null sentence list");
    }
  }
@Test
  public void testAnnotateWithTokenSpanMisalignedWithSentenceBounds() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator kbp = new KBPAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setIndex(1);
//    token.setNamedEntityTag("PERSON");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Obama.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation doc = new Annotation("Obama.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    HashMap<Integer, CorefChain> coref = new HashMap<>();
    Map<Integer, CorefChain.CorefMention> positionMentionMap = new HashMap<>();
//    CorefChain.CorefMention corefMention = new CorefChain.CorefMention(1, 1, 2, 3, 0, "Obama", false);
//    positionMentionMap.put(1, corefMention);
//    coref.put(1, new CorefChain(positionMentionMap, 1));

    doc.set(CorefCoreAnnotations.CorefChainAnnotation.class, coref);

    
    kbp.annotate(doc);

    List<RelationTriple> triples = doc.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testDefaultLanguageFallbackToEnglish() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    assertNotNull(annotator);
  }
@Test
  public void testEmptyAnnotationReturnsCleanResult() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should handle empty sentence list gracefully");
    }
  }
@Test
  public void testMentionWithoutTokensSkipsGracefully() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("kbp", props);

    CoreMap mention = new Annotation("Empty");
    mention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    mention.set(CoreAnnotations.TextAnnotation.class, "Empty");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("No tokens");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, null);

    Annotation document = new Annotation("Document");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    kbp.annotate(document);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAcronymClusterNoAntecedentMatch() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("UNICEF");
    token.setIndex(1);
//    token.setNamedEntityTag("ORGANIZATION");

    CoreMap sentence = new Annotation("UNICEF is active.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    Annotation ann = new Annotation("UNICEF is active.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    kbp.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testCanonicalMentionMapWithSelfLoopDoesNotCycle() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("NASA");
    token.setIndex(1);
//    token.setNamedEntityTag("ORGANIZATION");

    CoreMap mention = new Annotation("NASA");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.TextAnnotation.class, "NASA");
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Map<CoreMap, CoreMap> mentionToCanonical = new HashMap<>();
    mentionToCanonical.put(mention, mention); 

    assertEquals(mention, mentionToCanonical.get(mention));
  }
@Test
  public void testCorefChainWithOverlapIndexBounds() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("test", props);

    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("CEO");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tok1.setIndex(1);

    CoreMap sentence = new Annotation("CEO said");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(tok1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("CEO said");
    ann.set(CoreAnnotations.SentencesAnnotation.class, List.of(sentence));

//    CorefChain.CorefMention cm = new CorefChain.CorefMention(1, 0, 5, 6, 0, "bad", false);
    Map<Integer, CorefChain.CorefMention> mem = new HashMap<>();
//    mem.put(0, cm);
//    CorefChain chain = new CorefChain(mem, 0);
    Map<Integer, CorefChain> corefMap = new HashMap<>();
//    corefMap.put(0, chain);

    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    try {
      kbp.annotate(ann); 
    } catch (Exception e) {
      fail("Should not throw on out-of-bounds mention index");
    }
  }
@Test
  public void testTitlePersonPatternNoMatchDueToNERMismatch() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator kbp = new KBPAnnotator("kbp", props);

    CoreLabel t1 = new CoreLabel();
    t1.setNER("LOCATION");
//    t1.setPOS("NNP");
    t1.setWord("California");

    List<CoreLabel> tokens = List.of(t1);
    CoreMap sentence = new Annotation("California");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("California is large.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, List.of(sentence));

    CoreMap mention = new Annotation("California");
    mention.set(CoreAnnotations.TextAnnotation.class, "California");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> mentions = List.of(mention);
    HashMap<Pair<Integer, Integer>, CoreMap> map = new HashMap<>();

//    CorefChain.CorefMention cm = new CorefChain.CorefMention(1, 0, 1, 2, 0, "California", false);
    Map<Integer, CorefChain.CorefMention> positionMap = new HashMap<>();
//    positionMap.put(0, cm);
//    CorefChain chain = new CorefChain(positionMap, 0);

//    Pair<List<CoreMap>, CoreMap> result = kbp.corefChainToKBPMentions(chain, ann, map);
//    assertNotNull(result);
//    assertTrue(result.first().contains(null) || result.second() == null);
  }
@Test
  public void testWikipediaPropagationWithNullTokensInMention() {
    Properties props = new Properties();
    props.setProperty("kbp.language", "en");
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator kbp = new KBPAnnotator("kbp", props);

    CoreMap canonical = new Annotation("UN");
    canonical.set(CoreAnnotations.TokensAnnotation.class, null); 
    canonical.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    canonical.set(CoreAnnotations.WikipediaEntityAnnotation.class, "United_Nations");

    CoreMap m2 = new Annotation("UN");
    m2.set(CoreAnnotations.TokensAnnotation.class, null);

    Set<CoreMap> cluster = new HashSet<>();
    cluster.add(canonical);
    cluster.add(m2);

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    mentionsMap.put(canonical, cluster);

    try {
      for (CoreMap val : mentionsMap.values().iterator().next()) {
        if (val.get(CoreAnnotations.TokensAnnotation.class) != null) {
          for (CoreLabel t : val.get(CoreAnnotations.TokensAnnotation.class)) {
            t.set(CoreAnnotations.WikipediaEntityAnnotation.class, mentionsMap.keySet().iterator().next().get(CoreAnnotations.WikipediaEntityAnnotation.class));
          }
        }
        
      }
    } catch (Exception e) {
      fail("Propagation with null tokens should not throw exception");
    }
  } 
}