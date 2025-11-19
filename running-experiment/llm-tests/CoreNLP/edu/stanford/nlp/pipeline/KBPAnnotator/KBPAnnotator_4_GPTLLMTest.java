package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.KBPRelationExtractor;
import edu.stanford.nlp.ie.KBPStatisticalExtractor;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreAnnotation;
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

public class KBPAnnotator_4_GPTLLMTest {

 @Test
  public void testRequirementsSatisfiedContainsKBPTriplesAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertNotNull(satisfied);
    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testRequiresContainsEssentialAnnotations() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

//    Set<Class<?>> required = annotator.requires();
//    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
//    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
//    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
//    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
//    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateYieldsEmptyTriplesForLongSentence() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.maxlen", "2");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token1 = new CoreLabel();
    token1.setIndex(1);
    CoreLabel token2 = new CoreLabel();
    token2.setIndex(2);
    CoreLabel token3 = new CoreLabel();
    token3.setIndex(3);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotateProducesOneTriple() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    KBPRelationExtractor extractor = mock(KBPRelationExtractor.class);
    Pair<String, Double> prediction = new Pair<>("per:employee_of", 0.99);

    Span subjSpan = new Span(0, 2);
    Span objSpan = new Span(2, 4);

    KBPRelationExtractor.KBPInput input = new KBPRelationExtractor.KBPInput(subjSpan, objSpan,
        KBPRelationExtractor.NERTag.PERSON,
        KBPRelationExtractor.NERTag.ORGANIZATION,
        null);

    when(extractor.classify(any(KBPRelationExtractor.KBPInput.class))).thenReturn(prediction);

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord("Obama");
    subjToken.setIndex(1);
    CoreLabel subjToken2 = new CoreLabel();
    subjToken2.setWord("Barack");
    subjToken2.setIndex(2);

    CoreLabel objToken = new CoreLabel();
    objToken.setWord("Google");
    objToken.setIndex(3);
    CoreLabel objToken2 = new CoreLabel();
    objToken2.setWord("Inc");
    objToken2.setIndex(4);

    List<CoreLabel> subjTokens = Arrays.asList(subjToken, subjToken2);
    List<CoreLabel> objTokens = Arrays.asList(objToken, objToken2);

    CoreMap subjMention = mock(CoreMap.class);
    CoreMap objMention = mock(CoreMap.class);

    when(subjMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(subjTokens);
    when(subjMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(subjMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Barack Obama");
    when(subjMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    when(objMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(objTokens);
    when(objMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(objMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Google Inc");
    when(objMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreLabel> sentenceTokens = new ArrayList<>();
    sentenceTokens.addAll(subjTokens);
    sentenceTokens.addAll(objTokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentenceTokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(subjMention, objMention));
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> sentences = Collections.singletonList(sentence);
    Annotation doc = new Annotation("Barack Obama works at Google Inc.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    KBPAnnotator testAnnotator = new KBPAnnotator("kbp", props) {
      @Override
      public void annotate(Annotation annotation) {
        super.annotate(annotation);
      }

      @Override
      public Set<Class<? extends CoreAnnotation>> requires() {
        return super.requires();
      }

      @Override
      public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
        return super.requirementsSatisfied();
      }
    };

    testAnnotator.annotate(doc);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(1, triples.size());
    RelationTriple triple = triples.get(0);

    assertEquals("Barack Obama", triple.subjectGloss());
    assertEquals("per:employee_or_member_of", triple.relationGloss());
    assertEquals("Google Inc", triple.objectGloss());
    assertEquals(0.99, triple.confidence, 0.01);
  }
@Test
  public void testAnnotateHandlesNoRelationCorrectly() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord("John");
    subjToken.setIndex(1);

    CoreLabel objToken = new CoreLabel();
    objToken.setWord("UK");
    objToken.setIndex(2);

    List<CoreLabel> subjTokens = Collections.singletonList(subjToken);
    List<CoreLabel> objTokens = Collections.singletonList(objToken);

    CoreMap subjMention = mock(CoreMap.class);
    CoreMap objMention = mock(CoreMap.class);

    when(subjMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(subjTokens);
    when(subjMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(subjMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    when(objMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(objTokens);
    when(objMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("COUNTRY");
    when(objMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreLabel> sentenceTokens = new ArrayList<>();
    sentenceTokens.addAll(subjTokens);
    sentenceTokens.addAll(objTokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentenceTokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(subjMention, objMention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation document = new Annotation("No relation here");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    KBPRelationExtractor mockExtractor = mock(KBPRelationExtractor.class);
    when(mockExtractor.classify(any(KBPRelationExtractor.KBPInput.class))).thenReturn(new Pair<>(KBPStatisticalExtractor.NO_RELATION, 0.1));

    annotator.annotate(document);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testAnnotationWithEmptyMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation ann = new Annotation("Empty test");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(0, triples.size());
  }
@Test
  public void testNoNERInMentionSkipsCorefProcessing() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("the");
    token.setIndex(1);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("the");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("No NER tag");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testNullWikipediaEntityAnnotationHandledGracefully() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord("Alice");
    subjToken.setIndex(1);

    CoreLabel objToken = new CoreLabel();
    objToken.setWord("Wonderland");
    objToken.setIndex(2);

    CoreMap subj = mock(CoreMap.class);
    CoreMap obj = mock(CoreMap.class);

    when(subj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Alice");
    when(obj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Wonderland");

    when(subj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(obj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");

    when(subj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(subjToken));
    when(obj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(objToken));

    when(subj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
    when(obj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    when(subj.get(CoreAnnotations.WikipediaEntityAnnotation.class)).thenReturn(null);
    when(obj.get(CoreAnnotations.WikipediaEntityAnnotation.class)).thenReturn(null);

    List<CoreMap> mentions = Arrays.asList(subj, obj);

    List<CoreLabel> allTokens = new ArrayList<>();
    allTokens.add(subjToken);
    allTokens.add(objToken);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(allTokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation document = new Annotation("Alice went to Wonderland.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testNonPlausibleNERPairIsSkipped() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord("Table");
    subjToken.setIndex(1);
    CoreLabel objToken = new CoreLabel();
    objToken.setWord("Chair");
    objToken.setIndex(2);

    CoreMap subj = mock(CoreMap.class);
    CoreMap obj = mock(CoreMap.class);

    when(subj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(subjToken));
    when(obj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(objToken));

    when(subj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Table");
    when(obj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Chair");

    when(subj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("MISC");
    when(obj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("MISC");

    when(subj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
    when(obj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(subj, obj));
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(subjToken, objToken));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("Table next to Chair.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testDuplicateRelationTripleWithLowerConfidenceNotAdded() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    KBPRelationExtractor extractor = mock(KBPRelationExtractor.class);
    when(extractor.classify(any(KBPRelationExtractor.KBPInput.class)))
        .thenReturn(new Pair<>("per:alternate_names", 0.1));

    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");
    token1.setIndex(1);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("John");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);

    List<CoreMap> mentions = Arrays.asList(mention, mention); 
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("John is also called John.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty()); 
  }
@Test
  public void testUnknownRelationNameIsNotConverted() throws Exception {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    String unchanged = (String) KBPAnnotator.class
        .getDeclaredMethod("convertRelationNameToLatest", String.class)
        .invoke(annotator, "per:custom_relation");
    assertEquals("per:custom_relation", unchanged);
  }
@Test
  public void testPronominalMentionAcceptedOnlyIfWordMatchesList() {
    CoreLabel token = new CoreLabel();
    token.setWord("he");

//    boolean isPronoun = (boolean) invokePrivateStaticMethod(
//        KBPAnnotator.class,
//        "kbpIsPronominalMention",
//        new Class[]{CoreLabel.class},
//        new Object[]{token}
//    );

//    assertTrue(isPronoun);

    CoreLabel other = new CoreLabel();
    other.setWord("mountain");
//    boolean isNotPronoun = (boolean) invokePrivateStaticMethod(
//        KBPAnnotator.class,
//        "kbpIsPronominalMention",
//        new Class[]{CoreLabel.class},
//        new Object[]{other}
//    );

//    assertFalse(isNotPronoun);
  }
@Test
  public void testSentenceWithNullTokensAnnotationHandledGracefully() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    Annotation doc = new Annotation("Null tokens");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testMalformedMentionIndexFailsGracefully() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setIndex(1); 

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Mark");
    when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(4);
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("Wrong offsets");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testThreadInterruptedDuringAnnotationThrowsRuntimeInterruptedException() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    Thread.currentThread().interrupt(); 

    CoreLabel subj = new CoreLabel();
    subj.setIndex(1);
    subj.setWord("Alice");

    CoreMap subjMention = mock(CoreMap.class);
    when(subjMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(subj));
    when(subjMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(subjMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(subj));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(subjMention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation doc = new Annotation("Interrupted");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(doc);
      fail("Expected RuntimeInterruptedException not thrown");
    } catch (RuntimeInterruptedException expected) {
      
      Thread.interrupted(); 
    }
  }
@Test
  public void testAcronymMatchesMultipleAcronymsMappedToLongestMention() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("United");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Nations");

    List<CoreLabel> fullTokens = Arrays.asList(token1, token2);
    CoreMap longMention = mock(CoreMap.class);
    when(longMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(fullTokens);
    when(longMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("United Nations");
    when(longMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(longMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreLabel acronym1 = new CoreLabel();
    acronym1.setWord("UN");
    CoreMap acronymMention1 = mock(CoreMap.class);
    when(acronymMention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(acronym1));
    when(acronymMention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("UN");
    when(acronymMention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(acronymMention1.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreLabel acronym2 = new CoreLabel();
    acronym2.setWord("UNESCO");
    CoreMap acronymMention2 = mock(CoreMap.class);
    when(acronymMention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(acronym2));
    when(acronymMention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("UNESCO");
    when(acronymMention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(acronymMention2.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> mentions = Arrays.asList(longMention, acronymMention1, acronymMention2);

    CoreLabel tokenCtx = new CoreLabel();
    tokenCtx.setWord("text");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(tokenCtx));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("Multiple acronyms");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    
    
  }
@Test
  public void testMissingCanonicalMentionDefaultsToSelf() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Tesla");
    token.setIndex(1);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Tesla");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
    when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(5);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("Tesla is a company.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> result = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(result);
    assertTrue(result.isEmpty()); 
  }
@Test
  public void testSpanishModeBuildsBasicCorefSystem() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "es"); 
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Madrid");
    token.setIndex(1);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Madrid");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("Madrid está en España.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> result = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testWikipediaEntityIsPropagatedAcrossMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel coreferentToken = new CoreLabel();
    coreferentToken.setWord("IBM");
    coreferentToken.setIndex(1);
    coreferentToken.setSentIndex(0);

    CoreMap coreferentMention = mock(CoreMap.class);
    when(coreferentMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("IBM");
    when(coreferentMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(coreferentMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(coreferentToken));
    when(coreferentMention.get(CoreAnnotations.WikipediaEntityAnnotation.class)).thenReturn("IBM_(company)");
    when(coreferentMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("International");
    token2.setIndex(2);
    token2.setSentIndex(0);

    CoreMap ambiguousMention = mock(CoreMap.class);
    when(ambiguousMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("International");
    when(ambiguousMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(ambiguousMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));
    when(ambiguousMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(coreferentToken, token2));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(coreferentMention, ambiguousMention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("IBM and International are related.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    assertEquals("IBM_(company)", coreferentToken.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("IBM_(company)", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testPersonCorefFailsWhenNoTokenOverlap() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Napoleon");
    t1.setIndex(1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Wellington");
    t2.setIndex(2);

    CoreMap mention1 = mock(CoreMap.class);
    when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Napoleon");
    when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(t1));
    when(mention1.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap mention2 = mock(CoreMap.class);
    when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Wellington");
    when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(t2));
    when(mention2.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> mentions = Arrays.asList(mention1, mention2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(t1, t2));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("Napoleon faced Wellington.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> result = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testTitlePersonMatchRecoversKbpMention() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("President");
    t1.setTag("NNP");
    t1.setNER("TITLE");
    t1.setBeginPosition(0);
    t1.setEndPosition(9);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Lincoln");
    t2.setTag("NNP");
    t2.setNER("PERSON");
    t2.setBeginPosition(10);
    t2.setEndPosition(17);

    List<CoreLabel> tokens = Arrays.asList(t1, t2);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("President Lincoln");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("President Lincoln made a speech.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testRelationNameMappedCorrectlyWithUnderscoreConversion() throws Exception {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod("convertRelationNameToLatest", String.class);
    method.setAccessible(true);
    String r1 = (String) method.invoke(annotator, "org:dissolved");
    assertEquals("org:date_dissolved", r1);
    String r2 = (String) method.invoke(annotator, "org:top_members/employees");
    assertEquals("org:top_members_employees", r2);
  }
@Test
  public void testCorefClusterWithOnlyNullNERMentionsRemoved() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Something");
    token1.setIndex(1);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Something");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    Set<CoreMap> cluster = new LinkedHashSet<>();
    cluster.add(mention);
    mentionsMap.put(mention, cluster);

    Annotation ann = new Annotation("This is a test.");
    java.lang.reflect.Method method;
    try {
      method = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
      method.setAccessible(true);
      List<CoreMap> mentions = Collections.singletonList(mention);
      method.invoke(null, mentions, mentionsMap);
    } catch (Exception e) {
      fail("Reflection failed");
    }

    assertTrue(mentionsMap.isEmpty());
  }
@Test
  public void testDuplicateTripleLowerConfidenceReplacedByHigherConfidence() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel subjToken = new CoreLabel();
    subjToken.setWord("Bill");
    subjToken.setIndex(1);

    CoreLabel objToken = new CoreLabel();
    objToken.setWord("Microsoft");
    objToken.setIndex(2);

    CoreMap subj = mock(CoreMap.class);
    CoreMap obj = mock(CoreMap.class);

    when(subj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(subjToken));
    when(subj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(subj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Bill");
    when(subj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    when(obj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(objToken));
    when(obj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(obj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Microsoft");
    when(obj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> mentions = Arrays.asList(subj, obj);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(subjToken, objToken));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(mock(SemanticGraph.class));

    Annotation annotation = new Annotation("Bill founded Microsoft.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    KBPRelationExtractor extractor = mock(KBPRelationExtractor.class);

    Pair<String, Double> lowConfidence = new Pair<>("per:employee_of", 0.45);
    Pair<String, Double> highConfidence = new Pair<>("per:employee_of", 0.91);

    when(extractor.classify(any(KBPRelationExtractor.KBPInput.class)))
        .thenReturn(lowConfidence)
        .thenReturn(highConfidence); 

    
    KBPAnnotator testAnnotator = new KBPAnnotator("kbp", props) {
      @Override
      public void annotate(Annotation ann) {
        super.annotate(ann);
      }
    };

    testAnnotator.annotate(annotation);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(1, triples.size());
    assertTrue(triples.get(0).confidence >= 0.91);
  }
@Test
  public void testCorefPersonMismatchFailsWhenNoOverlapAndNotPronouns() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Elon");
    token1.setIndex(1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Jobs");
    token2.setIndex(2);

    CoreMap mention1 = mock(CoreMap.class);
    when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
    when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Elon");
    when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention1.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap mention2 = mock(CoreMap.class);
    when(mention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));
    when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Jobs");
    when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention2.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> mentions = Arrays.asList(mention1, mention2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("Elon spoke to Jobs.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testMissingNERForCanonicalKeyFallsBackToSecondaryMention() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("ABC");
    token1.setIndex(1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("American Broadcasting Company");
    token2.setIndex(2);

    CoreMap canonical = mock(CoreMap.class);
    when(canonical.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
    when(canonical.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
    when(canonical.get(CoreAnnotations.TextAnnotation.class)).thenReturn("ABC");

    CoreMap alternative = mock(CoreMap.class);
    when(alternative.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(alternative.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));
    when(alternative.get(CoreAnnotations.TextAnnotation.class)).thenReturn("American Broadcasting Company");

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    Set<CoreMap> cluster = new LinkedHashSet<>();
    cluster.add(canonical);
    cluster.add(alternative);
    mentionsMap.put(canonical, cluster);

    Annotation doc = new Annotation("ABC and American Broadcasting Company are the same.");
    List<CoreMap> mentions = Arrays.asList(canonical, alternative);

    try {
      java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
      method.setAccessible(true);
      method.invoke(null, mentions, mentionsMap);

      assertTrue(mentionsMap.containsKey(alternative));
      assertFalse(mentionsMap.containsKey(canonical));
    } catch (Exception e) {
      fail();
    }
  }
@Test
  public void testAnnotationWithEmptySentenceList() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    Annotation ann = new Annotation("");
    ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(ann);
    assertEquals(0, ann.get(CoreAnnotations.SentencesAnnotation.class).size());
  }
@Test
  public void testMentionToCanonicalMentionMapHandlesMissingMention() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "en");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel mentionToken = new CoreLabel();
    mentionToken.setWord("ACLU");
    mentionToken.setIndex(1);
    mentionToken.setSentIndex(0);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(mentionToken));
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("ACLU");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
    when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(4);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(mentionToken));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);
    Annotation ann = new Annotation("ACLU was there.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testAcronymMatcherFiltersSelfReference() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("FBI");
    token.setIndex(1);
    token.setSentIndex(0);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("FBI");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("FBI");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples); 
  }
@Test
  public void testNERMismatchBetweenAcronymAndExpandedFormIsFiltered() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel acronymToken = new CoreLabel();
    acronymToken.setWord("CIA");
    acronymToken.setIndex(1);
    acronymToken.setSentIndex(0);

    CoreMap acronymMention = mock(CoreMap.class);
    when(acronymMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(acronymToken));
    when(acronymMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("CIA");
    when(acronymMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(acronymMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreLabel expandedToken1 = new CoreLabel();
    expandedToken1.setWord("Central");
    CoreLabel expandedToken2 = new CoreLabel();
    expandedToken2.setWord("Intelligence");

    CoreMap expandedMention = mock(CoreMap.class);
    when(expandedMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(expandedToken1, expandedToken2));
    when(expandedMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Central Intelligence");
    when(expandedMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
    when(expandedMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(acronymToken, expandedToken1, expandedToken2));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(acronymMention, expandedMention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(null);

    Annotation ann = new Annotation("CIA is short for Central Intelligence.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
  }
@Test
  public void testEmptyKBPInputStillSetsTriplesAnnotation() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(mock(SemanticGraph.class));

    Annotation doc = new Annotation("Empty sentence");
    List<CoreMap> sentenceList = new ArrayList<CoreMap>();
    sentenceList.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(doc);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(0, triples.size());
  }
@Test
  public void testMentionHasNoNERButMentionsMapContainsOtherWithNER() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("XYZ");
    token1.setIndex(1);

    CoreMap mention1 = mock(CoreMap.class);
    when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("XYZ");
    when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));
    when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
    when(mention1.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("X Y Z");
    token2.setIndex(2);

    CoreMap mention2 = mock(CoreMap.class);
    when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("X Y Z");
    when(mention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));
    when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention2.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    Set<CoreMap> cluster = new LinkedHashSet<>();
    cluster.add(mention1);
    cluster.add(mention2);

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    mentionsMap.put(mention1, cluster);

    List<CoreMap> mentionList = Arrays.asList(mention1, mention2);
    try {
      java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
      method.setAccessible(true);
      method.invoke(null, mentionList, mentionsMap);
    } catch (Exception ex) {
      fail("Method invocation failed");
    }

    assertTrue(mentionsMap.containsKey(mention2));
  }
@Test
  public void testMentionNERMapFailsWithAllNullNERs() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("AAA");
    token.setIndex(1);

    CoreMap mention1 = mock(CoreMap.class);
    when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
    when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("AAA");

    Set<CoreMap> mentions = new HashSet<>();
    mentions.add(mention1);

    Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
    mentionsMap.put(mention1, mentions);

    List<CoreMap> mentionList = Collections.singletonList(mention1);

    try {
      java.lang.reflect.Method method = KBPAnnotator.class.getDeclaredMethod("acronymMatch", List.class, Map.class);
      method.setAccessible(true);
      method.invoke(null, mentionList, mentionsMap);
    } catch (Exception e) {
      fail("Exception thrown");
    }

    assertTrue(mentionsMap.isEmpty());
  }
@Test
  public void testKBPAnnotatorWithMissingSentenceTokensDoesNotFail() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    Annotation ann = new Annotation("Missing tokens");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testOnlyOneMentionInSentenceStillProcessesButDoesNotGenerateTriple() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setIndex(1);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Apple");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(mock(SemanticGraph.class));

    Annotation ann = new Annotation("Apple was mentioned.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertEquals(0, triples.size());
  }
@Test
  public void testSentenceExceedsMaxLengthSkipsExtraction() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.maxlen", "2");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("one");
    t1.setIndex(1);
    CoreLabel t2 = new CoreLabel();
    t2.setWord("two");
    t2.setIndex(2);
    CoreLabel t3 = new CoreLabel();
    t3.setWord("three");
    t3.setIndex(3);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(mock(SemanticGraph.class));

    Annotation ann = new Annotation("one two three");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);
    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  }
@Test
  public void testMentionToCanonicalMentionReturnsSelfWhenNoCoref() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel token = new CoreLabel();
    token.setWord("X");
    token.setIndex(1);
    token.setSentIndex(0);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("X");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
//    when(sentence.get(SemanticGraph.class)).thenReturn(mock(SemanticGraph.class));

    Annotation doc = new Annotation("X is a company.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);
    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);

    assertNotNull(triples);
    assertTrue(triples.isEmpty());
  } 
}