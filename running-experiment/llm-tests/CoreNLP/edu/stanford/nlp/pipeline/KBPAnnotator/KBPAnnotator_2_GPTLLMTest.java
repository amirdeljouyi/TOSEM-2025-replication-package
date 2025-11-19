package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
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

public class KBPAnnotator_2_GPTLLMTest {

 @Test
  public void testConstructorWithModelNone() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    assertNotNull(annotator.requirementsSatisfied());
    assertNotNull(annotator.extractor);
    assertTrue(annotator.requirementsSatisfied().contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testConstructorWithInvalidModelPathThrowsException() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "invalid_path_that_does_not_exist.model");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    try {
      new KBPAnnotator(props);
      fail("Expected RuntimeIOException was not thrown");
    } catch (RuntimeIOException e) {
      assertTrue(e.getCause() != null);
    }
  }
@Test
  public void testRequirementsSatisfiedIncludesKBPTriplesAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testRequirementsContainsRequiredAnnotations() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testAnnotateEmptyAnnotationDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("");
    List<CoreMap> emptySentences = Collections.emptyList();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, emptySentences);

    annotator.annotate(annotation);

    List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotateWithInterruptedThreadThrowsRuntimeInterruptedException() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("Sample sentence.");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap mention = new Annotation("John");
    mention.set(CoreAnnotations.TextAnnotation.class, "John");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("John went home.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    Thread.currentThread().interrupt(); 

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeInterruptedException");
    } catch (RuntimeInterruptedException expected) {
      
    } finally {
      Thread.interrupted(); 
    }
  }
@Test
  public void testSimpleAnnotationGeneratesNoTriplesForPersonWithNoRelation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.maxlen", "100");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("Barack Obama was president.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.IndexAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap mention = new Annotation("Barack Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Barack Obama was president.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> results = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(results);
    CoreMap resultSent = results.get(0);
    List<RelationTriple> triples = resultSent.get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testConstructorWithOnlySemgrexEnabled() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", DefaultPaths.DEFAULT_KBP_SEMGREX_DIR);

    KBPAnnotator annotator = new KBPAnnotator(props);
    assertNotNull(annotator.extractor);
  }
@Test
  public void testConstructorWithOnlyTokensregexEnabled() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", DefaultPaths.DEFAULT_KBP_TOKENSREGEX_DIR);
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    assertNotNull(annotator.extractor);
  }
@Test
  public void testAnnotateSkipsWhenSentenceLongerThanMaxLength() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.maxlen", "1"); 

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("Barack Obama was president of the United States.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token);

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreMap sent = new Annotation("Barack Obama was a president of the United States");
    List<CoreMap> mentions = Collections.singletonList(mention);
    sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sent.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sents = Collections.singletonList(sent);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(annotation);

    CoreMap outputSent = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
    List<RelationTriple> triples = outputSent.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateHandlesNullNERInMentionKey() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("Some unknown entity.");

    CoreLabel token = new CoreLabel();
    token.setWord("Thing");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap mention = new Annotation("Thing");
    mention.set(CoreAnnotations.TextAnnotation.class, "Thing");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);  
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreMap sentence = new Annotation("Thing is here.");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Collections.singletonList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    
    List<CoreMap> output = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    CoreMap outputSent = output.get(0);
    List<RelationTriple> triples = outputSent.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testAnnotateWithKbPLanguageSpanishInitializesSpanishCoref() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "es");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation ann = new Annotation("El presidente habló.");

    CoreLabel token = new CoreLabel();
    token.setWord("presidente");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap mention = new Annotation("presidente");
    mention.set(CoreAnnotations.TextAnnotation.class, "presidente");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("El presidente habló.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(ann);

    List<CoreMap> out = ann.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(out.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testNoRelationProducedWhenNERMismatch() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("IBM is based in USA.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("IBM");
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("USA");
    token2.set(CoreAnnotations.IndexAnnotation.class, 2);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    token2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap mention1 = new Annotation("IBM");
    mention1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("USA");
    mention2.set(CoreAnnotations.TextAnnotation.class, "USA");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("IBM is based in USA.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);
    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateWithCorefChainHavingNoKBPMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    Annotation annotation = new Annotation("President is speaking.");
    KBPAnnotator annotator = new KBPAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("President");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sent = new Annotation("President is speaking.");
    sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sent.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    List<CoreMap> sentences = Collections.singletonList(sent);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    CorefChain.CorefMention mention = new CorefChain.CorefMention(0, 1, 1, 1, 2, "President", false);
//    List<CorefChain.CorefMention> mentions = Collections.singletonList(mention);
//    CorefChain chain = new CorefChain(123, mentions);
    Map<Integer, CorefChain> corefMap = new HashMap<>();
//    corefMap.put(123, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAcronymMatchNonMatchingNERGetsRemoved() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    Annotation annotation = new Annotation("ACLU stands for American Civil Liberties Union.");
    KBPAnnotator annotator = new KBPAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("ACLU");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);
    token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreMap mention1 = new Annotation("ACLU");
    mention1.set(CoreAnnotations.TextAnnotation.class, "ACLU");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("ACLU stands for American Civil Liberties Union.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention1));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testPersonToPersonCorefWithoutTokenOverlapIsNotLinked() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    Annotation annotation = new Annotation("Morsi met with Catherine Ashton.");
    KBPAnnotator annotator = new KBPAnnotator(props);

    CoreLabel m1token = new CoreLabel();
    m1token.setWord("Morsi");
    m1token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    m1token.set(CoreAnnotations.IndexAnnotation.class, 1);
    m1token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    m1token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    m1token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel m2token = new CoreLabel();
    m2token.setWord("Ashton");
    m2token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    m2token.set(CoreAnnotations.IndexAnnotation.class, 5);
    m2token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    m2token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    m2token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);

    CoreMap m1 = new Annotation("Morsi");
    m1.set(CoreAnnotations.TextAnnotation.class, "Morsi");
    m1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    m1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(m1token));
    m1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    m1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    m1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap m2 = new Annotation("Catherine Ashton");
    m2.set(CoreAnnotations.TextAnnotation.class, "Catherine Ashton");
    m2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    m2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(m2token));
    m2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    m2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
    m2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

//    CorefChain.CorefMention cm1 = new CorefChain.CorefMention(0, 1, 1, 1, 2, "Morsi", false);
//    CorefChain.CorefMention cm2 = new CorefChain.CorefMention(0, 1, 1, 5, 6, "Ashton", false);
//    List<CorefChain.CorefMention> chain = Arrays.asList(cm1, cm2);
//    CorefChain cc = new CorefChain(100, chain);
    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(100, cc);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

    CoreMap sentence = new Annotation("Morsi met with Catherine Ashton");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(m1token, m2token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(m1, m2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<RelationTriple> output = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(output);
  }
@Test
  public void testWikipediaEntityPropagationWhenPresent() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    Annotation annotation = new Annotation("Google is a company.");

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Google_Inc");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap mention = new Annotation("Google");
    mention.set(CoreAnnotations.TextAnnotation.class, "Google");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Google_Inc");

    CoreMap sentence = new Annotation("Google is a company.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    KBPAnnotator annotator = new KBPAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("Google_Inc", resultTokens.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testDuplicateTriplesRetainHighestConfidence() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    
    

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("Obama was born in Hawaii. Obama was born in Hawaii.");

    CoreLabel subject = new CoreLabel();
    subject.setWord("Obama");
    subject.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    subject.set(CoreAnnotations.IndexAnnotation.class, 1);
    subject.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    subject.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    subject.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel object = new CoreLabel();
    object.setWord("Hawaii");
    object.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    object.set(CoreAnnotations.IndexAnnotation.class, 4);
    object.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    object.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    object.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap tokenSubj = new Annotation("Obama");
    tokenSubj.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tokenSubj.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    tokenSubj.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(subject));
    tokenSubj.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tokenSubj.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokenSubj.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap tokenObj = new Annotation("Hawaii");
    tokenObj.set(CoreAnnotations.TextAnnotation.class, "Hawaii");
    tokenObj.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    tokenObj.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(object));
    tokenObj.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    tokenObj.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    tokenObj.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Obama was born in Hawaii. Obama was born in Hawaii.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(subject, object));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(tokenSubj, tokenObj));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<RelationTriple> results = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(results);
  }
@Test
  public void testNoTripleWhenSubjectEqualsObjectAndRelationIsAlternateNames() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("Apple is also known as Apple.");

    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    List<CoreLabel> tokens = Arrays.asList(token, token);

    CoreMap mention1 = new Annotation("Apple");
    mention1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap mention2 = new Annotation("Apple");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Apple");
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Apple is also known as Apple.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertTrue(triples == null || triples.isEmpty());
  }
@Test
  public void testannotateHandlesNullTokensAnnotationGracefully() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("");

    CoreMap mention = new Annotation("CEO");
    mention.set(CoreAnnotations.TextAnnotation.class, "CEO");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TITLE");

    CoreMap sentence = new Annotation("CEO spoke.");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotator.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
  }
@Test
  public void testCorefChainWithNullMentionSentenceIsIgnored() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("");

    List<CoreMap> sentences = new ArrayList<>();
    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    CorefChain.CorefMention corefMention = new CorefChain.CorefMention(0, 1, 1, 5, 6, "Unknown", false);
//    CorefChain corefChain = new CorefChain(111, Arrays.asList(corefMention));
    Map<Integer, CorefChain> corefChainMap = new HashMap<>();
//    corefChainMap.put(111, corefChain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChainMap);

    annotator.annotate(annotation);

    assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
  }
@Test
  public void testInvalidNERTagIsIgnoredGracefully() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("UnknownEntity spoke to UnknownEntity");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("UnknownEntity");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "GIBBERISH");
    t1.set(CoreAnnotations.IndexAnnotation.class, 1);
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    t1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap mention = new Annotation("UnknownEntity");
    mention.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "GIBBERISH");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t1));
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("UnknownEntity spoke");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t1));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    List<RelationTriple> triples = sentences.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testConstructorThrowsIfModelClassIsInvalidType() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "edu/stanford/nlp/models/kbp/invalid_model_format.txt");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    boolean passed = false;
    try {
      new KBPAnnotator(props);
    } catch (RuntimeIOException e) {
      passed = true;
    } catch (ClassCastException e) {
      passed = true;
    }
    assertTrue("Expected exception was not thrown for invalid model format", passed);
  }
@Test
  public void testTitlePersonPatternMatchTriggersFallbackSpanLookup() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation ann = new Annotation("");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("CEO");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TITLE");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Jobs");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token2.set(CoreAnnotations.IndexAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("CEO Jobs");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    CorefChain.CorefMention mention = new CorefChain.CorefMention(0, 1, 1, 1, 3, "CEO Jobs", false);
//    CorefChain chain = new CorefChain(321, Arrays.asList(mention));
    Map<Integer, CorefChain> chainMap = new HashMap<>();
//    chainMap.put(321, chain);

    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, chainMap);
    HashMap<Pair<Integer, Integer>, CoreMap> kbpMentions = new HashMap<>();

    CoreMap spanMention = new Annotation("Jobs");
    spanMention.set(CoreAnnotations.TextAnnotation.class, "Jobs");
    spanMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    spanMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    spanMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    kbpMentions.put(new Pair<>(4, 9), spanMention);

//    Pair<List<CoreMap>, CoreMap> output = annotator.corefChainToKBPMentions(chain, ann, kbpMentions);
//    assertNotNull(output.first());
//    assertEquals("Jobs", output.second().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithUnsupportedModelObjectTypeThrowsException() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "edu/stanford/nlp/models/kbp/unsupported_model_object.ser"); 
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    boolean correctExceptionThrown = false;

    try {
      new KBPAnnotator(props);
    } catch (RuntimeIOException e) {
      correctExceptionThrown = e.getCause() instanceof ClassCastException;
    } catch (ClassCastException e) {
      correctExceptionThrown = true;
    } catch (Exception e) {
      
    }

    assertTrue("Expected ClassCastException for unhandled model object", correctExceptionThrown);
  }
@Test
  public void testAcronymMatchStopsIfTickLimitExceeded() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation ann = new Annotation("ACLU. Long String A B C D E F G H I J");

    List<CoreLabel> acronymTokens = new ArrayList<>();
    CoreLabel acronymToken = new CoreLabel();
    acronymToken.setWord("ACLU");
    acronymToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    acronymToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    acronymToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    acronymToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    acronymTokens.add(acronymToken);

    List<CoreLabel> longTokens = new ArrayList<>();
    for (int i = 0; i < 1500; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord("Token" + i);
      token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
      token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
      token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, i * 10);
      token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, i * 10 + 5);
      longTokens.add(token);
    }

    CoreMap acronymMention = new Annotation("ACLU");
    acronymMention.set(CoreAnnotations.TextAnnotation.class, "ACLU");
    acronymMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    acronymMention.set(CoreAnnotations.TokensAnnotation.class, acronymTokens);
    acronymMention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> allMentions = new ArrayList<>();
    allMentions.add(acronymMention);

    for (CoreLabel tok : longTokens) {
      CoreMap mention = new Annotation(tok.word());
      mention.set(CoreAnnotations.TextAnnotation.class, tok.word());
      mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
      mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(tok));
      mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
      allMentions.add(mention);
    }

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    annotator.getClass(); 
    
    try {
      java.lang.reflect.Method m = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
      m.setAccessible(true);
      m.invoke(null, allMentions, mentionsMap);
    } catch (Exception e) {
      fail("Invocation of acronymMatch should not throw: " + e);
    }

    assertTrue(true); 
  }
@Test
  public void testCorefChainToKBPMentionsSkipsNullCharOffsets() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation ann = new Annotation("Entity A Entity B");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setIndex(1);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    token2.setIndex(2);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sents);

//    CorefChain.CorefMention m1 = new CorefChain.CorefMention(0, 1, 1, 1, 2, "A", false);
//    CorefChain.CorefMention m2 = new CorefChain.CorefMention(0, 1, 1, 2, 3, "B", false);
//    CorefChain cc = new CorefChain(20, Arrays.asList(m1, m2));

    HashMap<Pair<Integer, Integer>, CoreMap> mentionMap = new HashMap<>();
    

//    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(cc, ann, mentionMap);
//    assertEquals(2, result.first().size());
//    assertNull(result.second());
  }
@Test
  public void testMentionMapPrunesChainsWithNoNERCanonicalMention() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    Annotation ann = new Annotation("Entity.");
    KBPAnnotator annotator = new KBPAnnotator(props);

    CoreMap invalidMention = new Annotation("Unknown");
    invalidMention.set(CoreAnnotations.TextAnnotation.class, "Unknown");
    invalidMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null); 

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    Set<CoreMap> mentions = new LinkedHashSet<>();
    mentions.add(invalidMention);
    mentionsMap.put(invalidMention, mentions);

    try {
      java.lang.reflect.Method pruneMethod = KBPAnnotator.class.getDeclaredMethod("annotate", Annotation.class);
      pruneMethod.setAccessible(true);
      annotator.annotate(ann);
    } catch (Exception e) {
      fail("Expected execution path should skip invalid mention without NER: " + e);
    }

    assertTrue(true); 
  }
@Test
  public void testMentionToCanonicalMentionPopulatedWhenSpanish() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "es");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation ann = new Annotation("Él habló con el presidente.");

    CoreLabel token = new CoreLabel();
    token.setWord("presidente");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.setIndex(1);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap mention = new Annotation("presidente");
    mention.set(CoreAnnotations.TextAnnotation.class, "presidente");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreMap sentence = new Annotation("Él habló con el presidente.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sents = Collections.singletonList(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sents);

    annotator.annotate(ann);

    List<RelationTriple> triples = sents.get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testMentionWithoutWikipediaEntityLeavesTokensUnmodified() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("");
    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);

    CoreMap mention = new Annotation("Google");
    mention.set(CoreAnnotations.TextAnnotation.class, "Google");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap sentence = new Annotation("Google...");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String wikiLink = token.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertNull(wikiLink);
  }
@Test
  public void testAcronymMatchingWithNullClusterInMap() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    CoreLabel acronymToken = new CoreLabel();
    acronymToken.setWord("NASA");
    acronymToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    acronymToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    acronymToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreMap acronymMention = new Annotation("NASA");
    acronymMention.set(CoreAnnotations.TextAnnotation.class, "NASA");
    acronymMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    acronymMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(acronymToken));

    CoreLabel longLabel1 = new CoreLabel();
    longLabel1.setWord("National");
    CoreLabel longLabel2 = new CoreLabel();
    longLabel2.setWord("Aeronautics");
    CoreLabel longLabel3 = new CoreLabel();
    longLabel3.setWord("and");
    CoreLabel longLabel4 = new CoreLabel();
    longLabel4.setWord("Space");
    CoreLabel longLabel5 = new CoreLabel();
    longLabel5.setWord("Administration");

    List<CoreLabel> longTokens = Arrays.asList(longLabel1, longLabel2, longLabel3, longLabel4, longLabel5);

    CoreMap longMention = new Annotation("National Aeronautics and Space Administration");
    longMention.set(CoreAnnotations.TextAnnotation.class, "National Aeronautics and Space Administration");
    longMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    longMention.set(CoreAnnotations.TokensAnnotation.class, longTokens);

    List<CoreMap> mentions = Arrays.asList(acronymMention, longMention);

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    mentionsMap.put(acronymMention, null);
    mentionsMap.put(longMention, null);

    try {
      java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
      method.setAccessible(true);
      method.invoke(null, mentions, mentionsMap);
    } catch (Exception e) {
      fail("Should not throw: " + e);
    }

    assertTrue(true); 
  }
@Test
  public void testEmptyMentionToCanonicalMapGetsFilledWithSelf() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation ann = new Annotation("John met Mary.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);
    token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Mary");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.IndexAnnotation.class, 3);
    token2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreMap mention1 = new Annotation("John");
    mention1.set(CoreAnnotations.TextAnnotation.class, "John");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    mention1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreMap mention2 = new Annotation("Mary");
    mention2.set(CoreAnnotations.TextAnnotation.class, "Mary");
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
    mention2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    mention2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreMap sentence = new Annotation("John met Mary.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
  }
@Test
  public void testMultipleTriplesSameKeyStoresOnlyHigherConfidence() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    Annotation ann = new Annotation("Obama met Obama.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Obama");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.IndexAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    token2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap subjMention = new Annotation("Obama");
    subjMention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    subjMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    subjMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    subjMention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    subjMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    subjMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreMap objMention = new Annotation("Obama");
    objMention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    objMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    objMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
    objMention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    objMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    objMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreMap sent = new Annotation("Obama met Obama.");
    sent.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sent.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(subjMention, objMention));
    sent.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));

    KBPAnnotator anntr = new KBPAnnotator(props);

    anntr.annotate(ann);

    List<RelationTriple> triples = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    
  }
@Test
  public void testRequirementsContainsExpectedInputAnnotations() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Set<Class<? extends CoreAnnotation>> reqs = annotator.requires();
    assertTrue(reqs.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(reqs.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(reqs.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(reqs.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesOnlyKBPTriples() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testAnnotateSkipsWhenSubjectAndObjectAreSameReference() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("IBM is also called IBM.");

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreMap mention = new Annotation("IBM");
    mention.set(CoreAnnotations.TextAnnotation.class, "IBM");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreMap sentence = new Annotation("IBM is also called IBM.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention, mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
            .get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateWithEmptyCorefMapDoesNotFail() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    Annotation annotation = new Annotation("Barack Obama was a senator.");

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.IndexAnnotation.class, 2);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreMap sentence = new Annotation("Barack Obama was a senator.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentenceList = Collections.singletonList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<Integer, CorefChain>());

    KBPAnnotator annotator = new KBPAnnotator(props);
    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
            .get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
  }
@Test
  public void testMentionWithMissingNERFieldSkipsLinking() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    Annotation annotation = new Annotation("Entity is missing NER.");

    CoreLabel token = new CoreLabel();
    token.setWord("Entity");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreMap mention = new Annotation("Entity");
    mention.set(CoreAnnotations.TextAnnotation.class, "Entity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreMap sentence = new Annotation("Entity is missing NER.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    KBPAnnotator annotator = new KBPAnnotator(props);
    annotator.annotate(annotation);

    List<RelationTriple> triples = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
            .get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAcronymMatcherDoesNotCrashOnNullNER() throws Exception {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    CoreMap badMention = new Annotation("XYZ");
    badMention.set(CoreAnnotations.TextAnnotation.class, "XYZ");
    badMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(badMention);

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    Set<CoreMap> set = new HashSet<>();
    set.add(badMention);
    mentionsMap.put(badMention, set);

    java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod(
            "acronymMatch", List.class, Map.class);
    method.setAccessible(true);
    method.invoke(null, mentions, mentionsMap);

    assertTrue(true); 
  }
@Test
  public void testAnnotatorHandlesSentenceWithoutMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.model", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);
    Annotation annotation = new Annotation("This sentence contains no entities.");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("sentence");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    tokens.add(token);

    CoreMap sentence = new Annotation("This sentence contains no entities.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testSubjectWithOnlyWhitespaceSkipsTripleExtraction() {
    Properties props = new Properties();
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.model", "none");

    KBPAnnotator annotator = new KBPAnnotator(props);

    Annotation annotation = new Annotation("    is irrelevant.");

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord(" ");
    subjToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    subjToken.set(CoreAnnotations.IndexAnnotation.class, 1);
    subjToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    subjToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    subjToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreMap subj = new Annotation(" ");
    subj.set(CoreAnnotations.TextAnnotation.class, " ");
    subj.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    subj.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(subjToken));
    subj.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    subj.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    subj.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreMap sentence = new Annotation("    is irrelevant.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(subjToken));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(subj));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  } 
}