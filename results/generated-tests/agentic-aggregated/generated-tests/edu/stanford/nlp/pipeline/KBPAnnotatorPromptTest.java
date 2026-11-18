package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.IntTuple;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KBPAnnotatorPromptTest {

  @Test
  public void constructorAcceptsDisabledExtractorsAndEnglishLanguage() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    properties.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("kbp", properties);

    assertNotNull(annotator);
  }

  @Test
  public void convenienceConstructorAcceptsSpanishConfiguration() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    properties.setProperty("kbp.language", "es");

    KBPAnnotator annotator = new KBPAnnotator(properties);

    assertNotNull(annotator);
  }

  @Test(expected = NumberFormatException.class)
  public void constructorRejectsNonNumericMaximumLength() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    properties.setProperty("kbp.maxlen", "several");

    new KBPAnnotator(properties);
  }

  @Test
  public void requirementsSatisfiedContainsOnlyKbpTriplesAndIsImmutable() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);

    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();

    assertEquals(1, result.size());
    assertTrue(result.contains(CoreAnnotations.KBPTriplesAnnotation.class));
    try {
      result.clear();
      fail("requirementsSatisfied should return an unmodifiable set");
    } catch (UnsupportedOperationException expected) {
      assertEquals(1, result.size());
    }
  }

  @Test
  public void requiresReportsCompletePipelineContractAndIsImmutable() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);

    Set<Class<? extends CoreAnnotation>> result = annotator.requires();

    assertEquals(12, result.size());
    assertTrue(result.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.IndexAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.SentenceIndexAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.LemmaAnnotation.class));
    assertTrue(result.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(result.contains(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
    assertTrue(result.contains(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.OriginalTextAnnotation.class));
    try {
      result.add(CoreAnnotations.KBPTriplesAnnotation.class);
      fail("requires should return an unmodifiable set");
    } catch (UnsupportedOperationException expected) {
      assertFalse(result.contains(CoreAnnotations.KBPTriplesAnnotation.class));
    }
  }

  @Test
  public void corefChainResolvesExactCharacterOffsets() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);
    CoreLabel alice = new CoreLabel();
    alice.setWord("Alice");
    alice.setIndex(1);
    alice.setSentIndex(0);
    alice.setBeginPosition(0);
    alice.setEndPosition(5);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(alice));
    Annotation annotation = new Annotation(Collections.singletonList(sentence));
    CoreMap kbpMention = new ArrayCoreMap();
    kbpMention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    IntTuple position = new IntTuple(2);
    position.set(0, 1);
    position.set(1, 1);
    CorefMention corefMention = new CorefMention(null, null, null, null,
        1, 2, 1, 7, 11, 1, position, "Alice");
    Map<IntPair, Set<CorefMention>> mentionMap = new LinkedHashMap<>();
    mentionMap.put(new IntPair(1, 1), Collections.singleton(corefMention));
    CorefChain chain = new CorefChain(7, mentionMap, corefMention);
    HashMap<Pair<Integer, Integer>, CoreMap> offsets = new HashMap<>();
    offsets.put(new Pair<>(0, 5), kbpMention);

    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, annotation, offsets);

    assertEquals(1, result.first().size());
    assertSame(kbpMention, result.first().get(0));
    assertSame(kbpMention, result.second());
  }

  @Test
  public void corefChainChoosesLongestMappedKbpMention() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);
    CoreLabel alice = new CoreLabel();
    alice.setWord("Alice");
    alice.setIndex(1);
    alice.setSentIndex(0);
    alice.setBeginPosition(0);
    alice.setEndPosition(5);
    CoreLabel smith = new CoreLabel();
    smith.setWord("Smith");
    smith.setIndex(2);
    smith.setSentIndex(0);
    smith.setBeginPosition(6);
    smith.setEndPosition(11);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(alice, smith));
    Annotation annotation = new Annotation(Collections.singletonList(sentence));
    CoreMap shortMention = new ArrayCoreMap();
    shortMention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    CoreMap longMention = new ArrayCoreMap();
    longMention.set(CoreAnnotations.TextAnnotation.class, "Alice Smith");
    IntTuple positionOne = new IntTuple(2);
    positionOne.set(0, 1);
    positionOne.set(1, 1);
    IntTuple positionTwo = new IntTuple(2);
    positionTwo.set(0, 1);
    positionTwo.set(1, 2);
    CorefMention first = new CorefMention(null, null, null, null,
        1, 2, 1, 4, 20, 1, positionOne, "Alice");
    CorefMention second = new CorefMention(null, null, null, null,
        1, 3, 2, 4, 21, 1, positionTwo, "Alice Smith");
    Map<IntPair, Set<CorefMention>> mentionMap = new LinkedHashMap<>();
    mentionMap.put(new IntPair(1, 1), Collections.singleton(first));
    mentionMap.put(new IntPair(1, 2), Collections.singleton(second));
    CorefChain chain = new CorefChain(4, mentionMap, second);
    HashMap<Pair<Integer, Integer>, CoreMap> offsets = new HashMap<>();
    offsets.put(new Pair<>(0, 5), shortMention);
    offsets.put(new Pair<>(0, 11), longMention);

    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, annotation, offsets);

    assertEquals(Arrays.asList(longMention, shortMention), result.first());
    assertSame(longMention, result.second());
  }

  @Test
  public void corefChainReturnsNullBestMentionWhenNothingMaps() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);
    CoreLabel token = new CoreLabel();
    token.setWord("unknown");
    token.setIndex(1);
    token.setSentIndex(0);
    token.setBeginPosition(3);
    token.setEndPosition(10);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation annotation = new Annotation(Collections.singletonList(sentence));
    IntTuple position = new IntTuple(2);
    position.set(0, 1);
    position.set(1, 1);
    CorefMention mention = new CorefMention(null, null, null, null,
        1, 2, 1, 9, 30, 1, position, "unknown");
    Map<IntPair, Set<CorefMention>> mentionMap = new LinkedHashMap<>();
    mentionMap.put(new IntPair(1, 1), Collections.singleton(mention));
    CorefChain chain = new CorefChain(9, mentionMap, mention);

    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(
        chain, annotation, new HashMap<Pair<Integer, Integer>, CoreMap>());

    assertEquals(1, result.first().size());
    assertNull(result.first().get(0));
    assertNull(result.second());
  }

  @Test
  public void corefChainFallsBackFromTitlePersonSpanToPersonMention() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);
    CoreLabel president = new CoreLabel();
    president.setWord("President");
    president.setIndex(1);
    president.setSentIndex(0);
    president.setBeginPosition(0);
    president.setEndPosition(9);
    president.setTag("NNP");
    president.setNER("TITLE");
    CoreLabel alice = new CoreLabel();
    alice.setWord("Alice");
    alice.setIndex(2);
    alice.setSentIndex(0);
    alice.setBeginPosition(10);
    alice.setEndPosition(15);
    alice.setTag("NNP");
    alice.setNER("PERSON");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(president, alice));
    Annotation annotation = new Annotation(Collections.singletonList(sentence));
    CoreMap personMention = new ArrayCoreMap();
    personMention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    IntTuple position = new IntTuple(2);
    position.set(0, 1);
    position.set(1, 1);
    CorefMention mention = new CorefMention(null, null, null, null,
        1, 3, 2, 12, 40, 1, position, "President Alice");
    Map<IntPair, Set<CorefMention>> mentionMap = new LinkedHashMap<>();
    mentionMap.put(new IntPair(1, 2), Collections.singleton(mention));
    CorefChain chain = new CorefChain(12, mentionMap, mention);
    HashMap<Pair<Integer, Integer>, CoreMap> offsets = new HashMap<>();
    offsets.put(new Pair<>(10, 15), personMention);

    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(chain, annotation, offsets);

    assertSame(personMention, result.first().get(0));
    assertSame(personMention, result.second());
  }

  @Test
  public void annotateSentenceWithoutMentionsProducesEmptyTriples() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setValue("Hello");
    token.setOriginalText("Hello");
    token.setIndex(1);
    token.setSentIndex(0);
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.setTag("UH");
    token.setLemma("hello");
    token.setNER("O");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "Hello");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, new SemanticGraph());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, new SemanticGraph());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());
    Annotation annotation = new Annotation(Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(sentence.get(CoreAnnotations.KBPTriplesAnnotation.class));
    assertTrue(sentence.get(CoreAnnotations.KBPTriplesAnnotation.class).isEmpty());
  }

  @Test
  public void annotateHonorsMaximumLengthBeforeClassifyingCandidates() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    properties.setProperty("kbp.maxlen", "0");
    KBPAnnotator annotator = new KBPAnnotator(properties);
    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.setValue("Alice");
    token.setOriginalText("Alice");
    token.setIndex(1);
    token.setSentIndex(0);
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.setTag("NNP");
    token.setLemma("Alice");
    token.setNER("PERSON");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "Alice");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "Alice");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, new SemanticGraph());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, new SemanticGraph());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());
    Annotation annotation = new Annotation(Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(sentence.get(CoreAnnotations.KBPTriplesAnnotation.class));
    assertTrue(sentence.get(CoreAnnotations.KBPTriplesAnnotation.class).isEmpty());
  }

  @Test
  public void annotateWithSingleRecognizedMentionProducesNoRelation() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);
    CoreLabel token = new CoreLabel();
    token.setWord("Acme");
    token.setValue("Acme");
    token.setOriginalText("Acme");
    token.setIndex(1);
    token.setSentIndex(0);
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.setTag("NNP");
    token.setLemma("Acme");
    token.setNER("ORGANIZATION");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "Acme");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "Acme");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, new SemanticGraph());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class, new SemanticGraph());
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());
    Annotation annotation = new Annotation(Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertTrue(sentence.get(CoreAnnotations.KBPTriplesAnnotation.class).isEmpty());
  }

  @Test(expected = NullPointerException.class)
  public void annotateRejectsNullAnnotation() {
    Properties properties = new Properties();
    properties.setProperty("kbp.model", "none");
    properties.setProperty("kbp.semgrex", "none");
    properties.setProperty("kbp.tokensregex", "none");
    KBPAnnotator annotator = new KBPAnnotator(properties);

    annotator.annotate(null);
  }
}
