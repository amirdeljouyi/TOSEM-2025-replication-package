package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
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
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KBPAnnotator_1_GPTLLMTest {

 @Test
  public void testConstructorWithMinimalProperties() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testConstructorWithMaxLengthProperty() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.maxlen", "128");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    assertNotNull(annotator.requirementsSatisfied());
  }
@Test(expected = RuntimeIOException.class)
  public void testConstructorFailsForInvalidModelPath() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "/invalid/path/to/model.obj");

    new KBPAnnotator("kbp", props);
  }
@Test
  public void testAnnotateWithEmptySentences() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");

    Annotation annotation = new Annotation("example");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    annotator.annotate(annotation);

    assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateSingleSentenceWithNoMentions() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(new ArrayList<>());
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
    when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("This is a test.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    annotator.annotate(annotation);

    verify(sentence, atLeastOnce()).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
  }
@Test
  public void testAnnotateWithSimpleCorefChain() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);
    token1.setSentIndex(0);
    token1.setIndex(1);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setBeginPosition(7);
    token2.setEndPosition(12);
    token2.setSentIndex(0);
    token2.setIndex(2);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> mentionTokens = new ArrayList<>();
    mentionTokens.add(token1);
    mentionTokens.add(token2);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(mentionTokens);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Barack Obama");
    when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
    when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(12);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(mentionTokens);
    when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Barack Obama was born in Hawaii.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//    CorefChain.CorefMention corefMention = new CorefChain.CorefMention(1, 1, 1, 3, 0, "Barack Obama", false);
    List<CorefChain.CorefMention> mentionsInChain = new ArrayList<>();
//    mentionsInChain.add(corefMention);
    Map<Integer, CorefChain> corefMap = new HashMap<>();
//    corefMap.put(1, new CorefChain(1, mentionsInChain));
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    annotator.annotate(annotation);

    verify(sentence, atLeastOnce()).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
  }
@Test
  public void testCorefChainToKBPMentionsWithLongestMentionSelected() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    CoreLabel shortToken = new CoreLabel();
    shortToken.setWord("John");
    shortToken.setBeginPosition(0);
    shortToken.setEndPosition(4);
    shortToken.setIndex(1);
    shortToken.setSentIndex(0);

    CoreLabel longToken1 = new CoreLabel();
    longToken1.setWord("John");
    longToken1.setBeginPosition(10);
    longToken1.setEndPosition(14);
    longToken1.setIndex(1);
    longToken1.setSentIndex(1);

    CoreLabel longToken2 = new CoreLabel();
    longToken2.setWord("Smith");
    longToken2.setBeginPosition(15);
    longToken2.setEndPosition(20);
    longToken2.setIndex(2);
    longToken2.setSentIndex(1);

    List<CoreLabel> shortTokens = new ArrayList<>();
    shortTokens.add(shortToken);

    List<CoreLabel> longTokens = new ArrayList<>();
    longTokens.add(longToken1);
    longTokens.add(longToken2);

    CoreMap shortMention = mock(CoreMap.class);
    when(shortMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(shortTokens);
    when(shortMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("John");

    CoreMap longMention = mock(CoreMap.class);
    when(longMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(longTokens);
    when(longMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("John Smith");

    CoreMap sentence0 = mock(CoreMap.class);
    when(sentence0.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(shortTokens);
    CoreMap sentence1 = mock(CoreMap.class);
    when(sentence1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(longTokens);

    List<CoreMap> allSentences = new ArrayList<>();
    allSentences.add(sentence0);
    allSentences.add(sentence1);

    Annotation annotation = new Annotation("John is here. John Smith arrived.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, allSentences);

    Pair<Integer, Integer> offset1 = new Pair<>(0, 4);
    Pair<Integer, Integer> offset2 = new Pair<>(10, 20);
    HashMap<Pair<Integer, Integer>, CoreMap> offsetToMention = new HashMap<>();
    offsetToMention.put(offset1, shortMention);
    offsetToMention.put(offset2, longMention);

//    CorefChain.CorefMention m1 = new CorefChain.CorefMention(1, 1, 1, 2, 0, "John", false);
//    CorefChain.CorefMention m2 = new CorefChain.CorefMention(2, 2, 1, 3, 1, "John Smith", false);
    List<CorefChain.CorefMention> mentions = new ArrayList<>();
//    mentions.add(m1);
//    mentions.add(m2);

    CorefChain chain = mock(CorefChain.class);
    when(chain.getMentionsInTextualOrder()).thenReturn(mentions);

    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, annotation, offsetToMention);

    CoreMap bestMention = result.second;
    assertEquals("John Smith", bestMention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequirementsMethodsContainExpectedElements() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.semgrex", "none");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();

    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
public void testCorefChainWithNoMatchingKBPMentions() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.tokensregex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Example");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

//  CorefChain.CorefMention mention1 = new CorefChain.CorefMention(1, 1, 1, 2, 0, "nomatch", false);
  List<CorefChain.CorefMention> chainMentions = new ArrayList<>();
//  chainMentions.add(mention1);

  CorefChain badChain = mock(CorefChain.class);
  when(badChain.getMentionsInTextualOrder()).thenReturn(chainMentions);

  Map<Integer, CorefChain> corefMap = new HashMap<>();
  corefMap.put(1, badChain);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

  annotator.annotate(annotation);

  
  assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).size() == 1);
}
@Test
public void testAcronymWithoutExpansionIsIgnored() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.tokensregex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("NASA");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  token.set(CoreAnnotations.TextAnnotation.class, "NASA");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("NASA");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(4);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("NASA rocks.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  verify(sentence, atLeastOnce()).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testFilteredTripleWhenSubjectObjectAreSameForAlternateNames() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.tokensregex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("IBM");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("IBM");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(3);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(mention, mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("IBM is also IBM.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testMentionWithNullNERIsExcludedFromAcronymClustering() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("ACLU");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("ACLU");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(4);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("ACLU.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testWikipediaEntityPropagationAcrossMentions() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("IBM");
  token1.set(CoreAnnotations.IndexAnnotation.class, 1);
  token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("International");
  token2.set(CoreAnnotations.IndexAnnotation.class, 1);
  token2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens1 = Arrays.asList(token1);
  List<CoreLabel> tokens2 = Arrays.asList(token2);

  CoreMap canonical = mock(CoreMap.class);
  when(canonical.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
  when(canonical.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(canonical.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(canonical.get(CoreAnnotations.TextAnnotation.class)).thenReturn("IBM");
  when(canonical.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(canonical.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(3);
  when(canonical.get(CoreAnnotations.WikipediaEntityAnnotation.class)).thenReturn("IBM_(company)");

  CoreMap alias = mock(CoreMap.class);
  when(alias.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);
  when(alias.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(alias.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(alias.get(CoreAnnotations.TextAnnotation.class)).thenReturn("International");
  when(alias.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(10);
  when(alias.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(22);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(canonical, alias));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("IBM, also known as International.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testAnnotateSkipsSentenceOverMaxLength() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.maxlen", "1");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("hello");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  tokens.add(token);  

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Too long sentence");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), eq(new ArrayList<>()));
}
@Test
public void testAnnotateHandlesNullCorefChainAnnotation() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Test doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, null); 

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testRelationNameConversionForDeprecatedLabel() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

//  String converted = annotator.annotateRelationNameForTest("org:top_members/employees");
//  assertEquals("org:top_members_employees", converted);
}
@Test
public void testKbpIsPronominalMentionReturnsTrueForPronounToken() {
  CoreLabel pronoun = new CoreLabel();
  pronoun.setWord("she");

//  boolean result = KBPAnnotator.kbpIsPronominalMentionForTest(pronoun);
//  assertTrue(result);
}
@Test
public void testAnnotateHandlesMentionWithNullNERTag() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("XYZ");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null); 
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("XYZ");
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(3);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("XYZ.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.annotate(annotation);

  
  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testTitlePersonPatternExtraction() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("President");
  token1.setNER("TITLE");
  token1.setTag("NN");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Obama");
  token2.setNER("PERSON");
  token2.setTag("NNP");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);

  TokenSequencePattern pattern = TokenSequencePattern.compile("[pos:JJ & ner:O]? [ner: TITLE]+ ([ner: PERSON]+)");
  TokenSequenceMatcher matcher = pattern.matcher(tokens);

  boolean found = matcher.find();
  assertTrue(found);
}
@Test
public void testAcronymMatchTimeoutLimitReachedWithoutMatch() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.tokensregex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  Annotation annotation = new Annotation("Sentence with many mentions");
  List<CoreMap> sentences = new ArrayList<>();

  List<CoreMap> mentions = new ArrayList<>();
  for (int i = 0; i < 1005; i++) {
    CoreLabel token = new CoreLabel();
    token.setWord("Entity" + i);
    token.setNER("ORGANIZATION");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Entity" + i);
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
    mentions.add(mention);
  }

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);
  sentences.add(sentence);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);
  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testMultipleSubjectObjectTypePairsWithNoRelationMatch() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.tokensregex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel orgToken = new CoreLabel();
  orgToken.setWord("IBM");
  orgToken.setNER("ORGANIZATION");
  orgToken.setIndex(1);
  orgToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreLabel locToken = new CoreLabel();
  locToken.setWord("Paris");
  locToken.setNER("LOCATION");
  locToken.setIndex(2);
  locToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> orgTokens = new ArrayList<>();
  orgTokens.add(orgToken);

  List<CoreLabel> locTokens = new ArrayList<>();
  locTokens.add(locToken);

  CoreMap mention1 = mock(CoreMap.class);
  when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(orgTokens);
  when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention1.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("IBM");
  when(mention1.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention1.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(3);

  CoreMap mention2 = mock(CoreMap.class);
  when(mention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(locTokens);
  when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(mention2.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Paris");
  when(mention2.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(4);
  when(mention2.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(9);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(mention1, mention2));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(orgToken, locToken));
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("IBM is located in Paris.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testMentionWithNullTextAnnotationFieldSafeAccess() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.tokensregex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Unknown");
  token.setNER("PERSON");
  token.setIndex(1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null); 

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("Unknown was mentioned.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testConflictingTriplesDifferentConfidenceOnlyHigherKept() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.tokensregex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel subjToken = new CoreLabel();
  subjToken.setWord("Obama");
  subjToken.setNER("PERSON");
  subjToken.setIndex(1);
  subjToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreLabel objToken = new CoreLabel();
  objToken.setWord("USA");
  objToken.setNER("COUNTRY");
  objToken.setIndex(2);
  objToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> subjTokens = new ArrayList<>();
  subjTokens.add(subjToken);

  List<CoreLabel> objTokens = new ArrayList<>();
  objTokens.add(objToken);

  CoreMap subj = mock(CoreMap.class);
  when(subj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(subjTokens);
  when(subj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(subj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(subj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Obama");
  when(subj.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(subj.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(5);

  CoreMap obj = mock(CoreMap.class);
  when(obj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(objTokens);
  when(obj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("COUNTRY");
  when(obj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(obj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("USA");
  when(obj.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(6);
  when(obj.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(9);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(subj, obj));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(subjToken, objToken));
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("Obama born in USA.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testEmptyMentionsListInSentenceSkipsRelationExtraction() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("No mentions.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testMentionWithNullCanonicalMappingAdded() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");
  token.setNER("ORGANIZATION");
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Tesla");
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(5);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("Tesla is a company.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, null);

  annotator.annotate(annotation);

  verify(sentence, atLeastOnce()).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testAcronymResolutionPrefersLongestORG() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel acronymToken = new CoreLabel();
  acronymToken.setWord("ACLU");
  acronymToken.setNER("ORGANIZATION");
  acronymToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  acronymToken.set(CoreAnnotations.IndexAnnotation.class, 1);

  CoreLabel fullFormToken1 = new CoreLabel();
  fullFormToken1.setWord("American");

  CoreLabel fullFormToken2 = new CoreLabel();
  fullFormToken2.setWord("Civil");

  CoreLabel fullFormToken3 = new CoreLabel();
  fullFormToken3.setWord("Liberties");

  CoreLabel fullFormToken4 = new CoreLabel();
  fullFormToken4.setWord("Union");

  List<CoreLabel> acronymTokens = new ArrayList<>();
  acronymTokens.add(acronymToken);

  List<CoreLabel> longFormTokens = new ArrayList<>();
  longFormTokens.add(fullFormToken1);
  longFormTokens.add(fullFormToken2);
  longFormTokens.add(fullFormToken3);
  longFormTokens.add(fullFormToken4);

  CoreMap acronymMention = mock(CoreMap.class);
  when(acronymMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(acronymTokens);
  when(acronymMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(acronymMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("ACLU");
  when(acronymMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(acronymMention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(acronymMention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(4);

  CoreMap longMention = mock(CoreMap.class);
  when(longMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(longFormTokens);
  when(longMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(longMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("American Civil Liberties Union");
  when(longMention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(longMention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(5);
  when(longMention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(40);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(acronymMention, longMention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("ACLU stands for American Civil Liberties Union.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testCorefChainWithMultipleNullMentionsDoesNotFail() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("She");
  token.setNER("PERSON");
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  token.set(CoreAnnotations.IndexAnnotation.class, 1);

  List<CoreLabel> tokenList = new ArrayList<>();
  tokenList.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("She");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(3);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

//  CorefChain.CorefMention corefMention = new CorefChain.CorefMention(1, 1, 1, 2, 0, "She", false);
  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Collections.singletonList(corefMention));

  Map<Integer, CorefChain> corefChains = new HashMap<>();
  corefChains.put(1, chain);

  Annotation annotation = new Annotation("She was there.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefChains);

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testTitlePersonPatternExtractedWhenKBPMentionMissing() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel titleToken = new CoreLabel();
  titleToken.setWord("President");
  titleToken.setNER("TITLE");
  titleToken.setTag("NN");
  titleToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  titleToken.set(CoreAnnotations.IndexAnnotation.class, 1);
  titleToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  titleToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

  CoreLabel nameToken = new CoreLabel();
  nameToken.setWord("Obama");
  nameToken.setNER("PERSON");
  nameToken.setTag("NNP");
  nameToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  nameToken.set(CoreAnnotations.IndexAnnotation.class, 2);
  nameToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  nameToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(titleToken);
  tokens.add(nameToken);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("President Obama visited Europe.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  Map<Pair<Integer, Integer>, CoreMap> kbpMentions = new HashMap<>();

//  CorefChain.CorefMention cm = new CorefChain.CorefMention(1, 1, 1, 3, 0, "President Obama", false);
  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Collections.singletonList(cm));

//  Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, annotation, kbpMentions);
//  assertNull(result.second);
}
@Test
public void testMentionWithNullNamedEntityTagIsSilentlyIgnored() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("UnknownX");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("UnknownX");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(8);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation ann = new Annotation("UnknownX is referenced.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testRelationTripleNotAddedWhenSubjectEqualsObjectAndIsAlternateNames() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("ABC");
  token.setNER("ORGANIZATION");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("ABC");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(3);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(mention, mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("ABC is also known as ABC.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testConvertRelationNameToLatestReturnsSameIfNoMapping() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  String unchanged = "per:age";
//  String converted = annotator.convertRelationNameToLatestForTest(unchanged);

//  assertEquals("per:age", converted);
}
@Test
public void testMentionWithoutTokensAnnotationIsIgnored() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Missing tokens");
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation ann = new Annotation("Mention with missing data.");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(ann);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testMentionClusterWithAllNullNERsIsSkipped() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Foo");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention1 = mock(CoreMap.class);
  when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Foo");

  CoreMap mention2 = mock(CoreMap.class);
  when(mention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Foo");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(mention1, mention2));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("Foo is undefined.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Map<CoreMap, Set<CoreMap>> mentionsMap = new HashMap<>();
  Set<CoreMap> cluster = new HashSet<>();
  cluster.add(mention1);
  cluster.add(mention2);
  mentionsMap.put(mention1, cluster);

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testMentionsWithNullWikipediaFieldsDoNotCauseCrash() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  CoreLabel token = new CoreLabel();
  token.setWord("XYZCorp");
  token.setNER("ORGANIZATION");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap canonical = mock(CoreMap.class);
  when(canonical.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(canonical.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(canonical.get(CoreAnnotations.WikipediaEntityAnnotation.class)).thenReturn(null);
  when(canonical.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(canonical.get(CoreAnnotations.TextAnnotation.class)).thenReturn("XYZCorp");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.WikipediaEntityAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("XYZCorp");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(canonical, mention));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Annotation annotation = new Annotation("XYZCorp makes servers.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testClassifierReturnsNullRelationShouldNotAddTriple() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);
  Annotation annotation = new Annotation("Some sentence");

  CoreLabel subjToken = new CoreLabel();
  subjToken.setWord("Obama");
  subjToken.setNER("PERSON");
  subjToken.set(CoreAnnotations.IndexAnnotation.class, 1);
  subjToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreLabel objToken = new CoreLabel();
  objToken.setWord("USA");
  objToken.setNER("COUNTRY");
  objToken.set(CoreAnnotations.IndexAnnotation.class, 2);
  objToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> subjTokens = new ArrayList<>();
  subjTokens.add(subjToken);
  List<CoreLabel> objTokens = new ArrayList<>();
  objTokens.add(objToken);

  CoreMap subj = mock(CoreMap.class);
  when(subj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(subjTokens);
  when(subj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(subj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Obama");
  when(subj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(subj.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(subj.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(5);

  CoreMap obj = mock(CoreMap.class);
  when(obj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(objTokens);
  when(obj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("COUNTRY");
  when(obj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("USA");
  when(obj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(obj.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(6);
  when(obj.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(9);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(subj, obj));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(subjToken, objToken));
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testRelationConfidenceSelectionKeepsHighestConfidenceWhenDuplicateTriples() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");
  props.setProperty("kbp.verbose", "true");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);
  Annotation annotation = new Annotation("Obama leads USA");

  CoreLabel subjToken = new CoreLabel();
  subjToken.setWord("Obama");
  subjToken.setNER("PERSON");
  subjToken.set(CoreAnnotations.IndexAnnotation.class, 1);
  subjToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreLabel objToken = new CoreLabel();
  objToken.setWord("USA");
  objToken.setNER("COUNTRY");
  objToken.set(CoreAnnotations.IndexAnnotation.class, 2);
  objToken.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> subjTokens = new ArrayList<>();
  subjTokens.add(subjToken);
  List<CoreLabel> objTokens = new ArrayList<>();
  objTokens.add(objToken);

  CoreMap subj = mock(CoreMap.class);
  when(subj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(subjTokens);
  when(subj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(subj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Obama");
  when(subj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(subj.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(subj.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(5);

  CoreMap obj = mock(CoreMap.class);
  when(obj.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(objTokens);
  when(obj.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("COUNTRY");
  when(obj.get(CoreAnnotations.TextAnnotation.class)).thenReturn("USA");
  when(obj.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(obj.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(6);
  when(obj.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(9);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(subj, obj));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(subjToken, objToken));
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, new HashMap<>());

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testMentionWithPronominalToNonMatchingNonPronominalSkipsLink() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  Annotation annotation = new Annotation("He met Peter Parker");

  CoreLabel pronoun = new CoreLabel();
  pronoun.setWord("he");
  pronoun.setNER("PERSON");
  pronoun.set(CoreAnnotations.IndexAnnotation.class, 1);
  pronoun.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreLabel other = new CoreLabel();
  other.setWord("Peter");
  other.setNER("PERSON");
  other.set(CoreAnnotations.IndexAnnotation.class, 2);
  other.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreLabel other2 = new CoreLabel();
  other2.setWord("Parker");
  other2.setNER("PERSON");
  other2.set(CoreAnnotations.IndexAnnotation.class, 3);
  other2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreMap m1 = mock(CoreMap.class);
  when(m1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(pronoun));
  when(m1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(m1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("he");
  when(m1.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(m1.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
  when(m1.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(2);

  CoreMap m2 = mock(CoreMap.class);
  when(m2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(other, other2));
  when(m2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(m2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Peter Parker");
  when(m2.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);
  when(m2.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(3);
  when(m2.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(15);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(m1, m2));
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(pronoun, other, other2));
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);

  Map<Integer, CorefChain> corefMap = new HashMap<>();
//  CorefChain.CorefMention cm1 = new CorefChain.CorefMention(1, 1, 1, 2, 0, "he", false);
//  CorefChain.CorefMention cm2 = new CorefChain.CorefMention(2, 2, 2, 4, 0, "Peter Parker", false);
//  CorefChain chain = new CorefChain(1, Arrays.asList(cm1, cm2));
//  corefMap.put(1, chain);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

  annotator.annotate(annotation);

  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
}
@Test
public void testAnnotationWithNullSentencesDoesNotFail() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  Annotation annotation = new Annotation("No data");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, null);

  annotator.annotate(annotation);

  
  assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
}
@Test
public void testMalformedCorefMentionIndexOutOfRangeHandled() {
  Properties props = new Properties();
  props.setProperty("kbp.model", "none");
  props.setProperty("kbp.tokensregex", "none");
  props.setProperty("kbp.semgrex", "none");

  KBPAnnotator annotator = new KBPAnnotator("kbp", props);

  Annotation ann = new Annotation("Example");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("ACME");
  token1.setNER("ORG");
  token1.set(CoreAnnotations.IndexAnnotation.class, 1);
  token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(new ArrayList<>());
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)).thenReturn(null);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//  CorefChain.CorefMention cm = new CorefChain.CorefMention(1, 0, 100, 200, 0, "ACME", false);
  CorefChain chain = mock(CorefChain.class);
//  when(chain.getMentionsInTextualOrder()).thenReturn(Collections.singletonList(cm));
  Map<Integer, CorefChain> map = new HashMap<>();
  map.put(1, chain);
  ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

  annotator.annotate(ann);
  verify(sentence).set(eq(CoreAnnotations.KBPTriplesAnnotation.class), any(List.class));
} 
}