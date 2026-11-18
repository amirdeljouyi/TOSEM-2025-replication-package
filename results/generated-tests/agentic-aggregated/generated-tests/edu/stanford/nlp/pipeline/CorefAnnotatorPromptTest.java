package edu.stanford.nlp.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Dictionaries.Animacy;
import edu.stanford.nlp.coref.data.Dictionaries.Gender;
import edu.stanford.nlp.coref.data.Dictionaries.MentionType;
import edu.stanford.nlp.coref.data.Dictionaries.Number;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.IntTuple;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class CorefAnnotatorPromptTest {

  @Test
  public void getLinksReturnsEmptyListForEmptyResult() {
    Map<Integer, CorefChain> result = new HashMap<>();

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(result);

    assertTrue(links.isEmpty());
    assertTrue(links instanceof ArrayList);
  }

  @Test
  public void getLinksReturnsNoLinkForSingletonChain() {
    IntTuple position = new IntTuple(new int[] { 1, 1 });
    CorefMention mention = new CorefMention(MentionType.PROPER, Number.SINGULAR,
        Gender.FEMALE, Animacy.ANIMATE, 1, 2, 1, 7, 11, 1, position, "Alice");
    Map<IntPair, Set<CorefMention>> mentionMap = new HashMap<>();
    mentionMap.put(new IntPair(1, 1), new LinkedHashSet<>(Collections.singletonList(mention)));
    CorefChain chain = new CorefChain(7, mentionMap, mention);
    Map<Integer, CorefChain> result = new HashMap<>();
    result.put(7, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(result);

    assertEquals(Collections.emptyList(), links);
    assertEquals(1, chain.getMentionsInTextualOrder().size());
  }

  @Test
  public void getLinksConnectsLaterMentionToEarlierMention() {
    IntTuple firstPosition = new IntTuple(new int[] { 1, 1 });
    IntTuple secondPosition = new IntTuple(new int[] { 2, 1 });
    CorefMention first = new CorefMention(MentionType.PROPER, Number.SINGULAR,
        Gender.MALE, Animacy.ANIMATE, 1, 2, 1, 4, 21, 1, firstPosition, "Bob");
    CorefMention second = new CorefMention(MentionType.PRONOMINAL, Number.SINGULAR,
        Gender.MALE, Animacy.ANIMATE, 3, 4, 3, 4, 22, 2, secondPosition, "he");
    Map<IntPair, Set<CorefMention>> mentionMap = new HashMap<>();
    mentionMap.put(new IntPair(1, 1), new LinkedHashSet<>(Collections.singletonList(first)));
    mentionMap.put(new IntPair(2, 3), new LinkedHashSet<>(Collections.singletonList(second)));
    CorefChain chain = new CorefChain(4, mentionMap, first);
    Map<Integer, CorefChain> result = Collections.singletonMap(4, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(result);

    assertEquals(1, links.size());
    assertEquals(new Pair<IntTuple, IntTuple>(secondPosition, firstPosition), links.get(0));
  }

  @Test
  public void getLinksBuildsAllPairwiseLinksForThreeMentions() {
    IntTuple firstPosition = new IntTuple(new int[] { 1, 1 });
    IntTuple secondPosition = new IntTuple(new int[] { 1, 2 });
    IntTuple thirdPosition = new IntTuple(new int[] { 3, 1 });
    CorefMention first = new CorefMention(MentionType.PROPER, Number.SINGULAR,
        Gender.FEMALE, Animacy.ANIMATE, 1, 3, 2, 9, 31, 1, firstPosition, "Dr Lee");
    CorefMention second = new CorefMention(MentionType.PRONOMINAL, Number.SINGULAR,
        Gender.FEMALE, Animacy.ANIMATE, 5, 6, 5, 9, 32, 1, secondPosition, "she");
    CorefMention third = new CorefMention(MentionType.NOMINAL, Number.SINGULAR,
        Gender.FEMALE, Animacy.ANIMATE, 2, 4, 3, 9, 33, 3, thirdPosition, "the doctor");
    Map<IntPair, Set<CorefMention>> mentionMap = new HashMap<>();
    mentionMap.put(new IntPair(3, 3), new LinkedHashSet<>(Collections.singletonList(third)));
    mentionMap.put(new IntPair(1, 5), new LinkedHashSet<>(Collections.singletonList(second)));
    mentionMap.put(new IntPair(1, 2), new LinkedHashSet<>(Collections.singletonList(first)));
    CorefChain chain = new CorefChain(9, mentionMap, first);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(Collections.singletonMap(9, chain));

    assertEquals(3, links.size());
    assertEquals(new Pair<IntTuple, IntTuple>(secondPosition, firstPosition), links.get(0));
    assertEquals(new Pair<IntTuple, IntTuple>(thirdPosition, firstPosition), links.get(1));
    assertEquals(new Pair<IntTuple, IntTuple>(thirdPosition, secondPosition), links.get(2));
  }

  @Test
  public void getLinksUsesLongerMentionFirstWhenStartsAreEqual() {
    IntTuple longPosition = new IntTuple(new int[] { 1, 1 });
    IntTuple shortPosition = new IntTuple(new int[] { 1, 2 });
    CorefMention longer = new CorefMention(MentionType.NOMINAL, Number.SINGULAR,
        Gender.UNKNOWN, Animacy.INANIMATE, 2, 6, 5, 12, 41, 1, longPosition,
        "the old red building");
    CorefMention shorter = new CorefMention(MentionType.NOMINAL, Number.SINGULAR,
        Gender.UNKNOWN, Animacy.INANIMATE, 2, 4, 3, 12, 42, 1, shortPosition,
        "the building");
    Set<CorefMention> sameHeadBucket = new LinkedHashSet<>(Arrays.asList(shorter, longer));
    Map<IntPair, Set<CorefMention>> mentionMap = new HashMap<>();
    mentionMap.put(new IntPair(1, 3), sameHeadBucket);
    CorefChain chain = new CorefChain(12, mentionMap, longer);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(Collections.singletonMap(12, chain));

    assertEquals(Collections.singletonList(
        new Pair<IntTuple, IntTuple>(shortPosition, longPosition)), links);
  }

  @Test
  public void getLinksSkipsMentionsThatComparatorConsidersEqual() {
    IntTuple firstPosition = new IntTuple(new int[] { 1, 1 });
    IntTuple secondPosition = new IntTuple(new int[] { 1, 2 });
    CorefMention first = new CorefMention(MentionType.PROPER, Number.SINGULAR,
        Gender.UNKNOWN, Animacy.ANIMATE, 2, 4, 3, 18, 51, 1, firstPosition, "Taylor");
    CorefMention duplicateSpan = new CorefMention(MentionType.NOMINAL, Number.SINGULAR,
        Gender.UNKNOWN, Animacy.ANIMATE, 2, 4, 3, 18, 52, 1, secondPosition, "the manager");
    Map<IntPair, Set<CorefMention>> mentionMap = new HashMap<>();
    mentionMap.put(new IntPair(1, 3), new LinkedHashSet<>(Arrays.asList(first, duplicateSpan)));
    CorefChain chain = new CorefChain(18, mentionMap, first);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(Collections.singletonMap(18, chain));

    assertTrue(links.isEmpty());
  }

  @Test
  public void getLinksCombinesIndependentChainsWithoutCrossChainLinks() {
    IntTuple a1Position = new IntTuple(new int[] { 1, 1 });
    IntTuple a2Position = new IntTuple(new int[] { 2, 1 });
    IntTuple b1Position = new IntTuple(new int[] { 1, 2 });
    IntTuple b2Position = new IntTuple(new int[] { 4, 1 });
    CorefMention a1 = new CorefMention(MentionType.PROPER, Number.SINGULAR,
        Gender.MALE, Animacy.ANIMATE, 1, 2, 1, 20, 61, 1, a1Position, "Sam");
    CorefMention a2 = new CorefMention(MentionType.PRONOMINAL, Number.SINGULAR,
        Gender.MALE, Animacy.ANIMATE, 1, 2, 1, 20, 62, 2, a2Position, "he");
    CorefMention b1 = new CorefMention(MentionType.PROPER, Number.SINGULAR,
        Gender.UNKNOWN, Animacy.INANIMATE, 4, 5, 4, 21, 63, 1, b1Position, "Acme");
    CorefMention b2 = new CorefMention(MentionType.NOMINAL, Number.SINGULAR,
        Gender.NEUTRAL, Animacy.INANIMATE, 2, 4, 3, 21, 64, 4, b2Position, "the company");
    Map<IntPair, Set<CorefMention>> firstMap = new HashMap<>();
    firstMap.put(new IntPair(1, 1), new LinkedHashSet<>(Collections.singletonList(a1)));
    firstMap.put(new IntPair(2, 1), new LinkedHashSet<>(Collections.singletonList(a2)));
    Map<IntPair, Set<CorefMention>> secondMap = new HashMap<>();
    secondMap.put(new IntPair(1, 4), new LinkedHashSet<>(Collections.singletonList(b1)));
    secondMap.put(new IntPair(4, 3), new LinkedHashSet<>(Collections.singletonList(b2)));
    Map<Integer, CorefChain> result = new LinkedHashMap<>();
    result.put(20, new CorefChain(20, firstMap, a1));
    result.put(21, new CorefChain(21, secondMap, b1));

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(result);

    assertEquals(Arrays.asList(
        new Pair<IntTuple, IntTuple>(a2Position, a1Position),
        new Pair<IntTuple, IntTuple>(b2Position, b1Position)), links);
  }

  @Test
  public void getLinksReturnsFreshMutableResultsOnEveryCall() {
    Map<Integer, CorefChain> result = Collections.emptyMap();

    List<Pair<IntTuple, IntTuple>> first = CorefAnnotator.getLinks(result);
    List<Pair<IntTuple, IntTuple>> second = CorefAnnotator.getLinks(result);
    first.add(new Pair<IntTuple, IntTuple>(new IntTuple(2), new IntTuple(2)));

    assertNotSame(first, second);
    assertEquals(1, first.size());
    assertTrue(second.isEmpty());
  }

  @Test
  public void getLinksRejectsNullResultMap() {
    assertThrows(NullPointerException.class, () -> CorefAnnotator.getLinks(null));
  }

  @Test
  public void getLinksRejectsNullChainValue() {
    Map<Integer, CorefChain> result = new HashMap<>();
    result.put(1, null);

    assertThrows(NullPointerException.class, () -> CorefAnnotator.getLinks(result));
  }

  @Test
  public void constructorRejectsEnglishHybridConfiguration() {
    Properties props = new Properties();
    props.setProperty("coref.language", "english");
    props.setProperty("coref.algorithm", "hybrid");

    RuntimeException error = assertThrows(RuntimeException.class,
        () -> new CorefAnnotator(props));

    assertTrue(error.getCause() instanceof RuntimeException);
    assertNull(props.getProperty("coref.printConLLLoadingMessage"));
  }

  @Test
  public void constructorWrapsUnsupportedLanguageFailure() {
    Properties props = new Properties();
    props.setProperty("coref.language", "klingon");

    RuntimeException error = assertThrows(RuntimeException.class,
        () -> new CorefAnnotator(props));

    assertTrue(error.getCause() instanceof IllegalArgumentException);
  }

  @Test
  public void constructorWrapsUnknownAlgorithmFailure() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "not-an-algorithm");

    RuntimeException error = assertThrows(RuntimeException.class,
        () -> new CorefAnnotator(props));

    assertTrue(error.getCause() instanceof IllegalArgumentException);
  }

  @Test
  public void dependencyConfigurationReportsCompleteRequirements() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "custom");
    props.setProperty("coref.algorithm.class", "edu.stanford.nlp.coref.hybrid.HybridCorefSystem");
    props.setProperty("coref.sieves", "SpeakerMatch");
    props.setProperty("coref.md.type", "dependency");
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.demonym", "/dev/null");
    props.setProperty("coref.animate", "/dev/null");
    props.setProperty("coref.inanimate", "/dev/null");
    props.setProperty("coref.states", "/dev/null");
    props.setProperty("coref.big.gender.number", "/dev/null");
    props.setProperty("coref.countries", "/dev/null");
    props.setProperty("coref.states.provinces", "/dev/null");
    props.setProperty("coref.printConLLLoadingMessage", "original-value");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.IndexAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.ValueAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentenceIndexAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.LemmaAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.EntityTypeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
    assertTrue(requirements.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(requirements.contains(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class));
    assertTrue(requirements.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertFalse(requirements.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertFalse(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
    assertEquals(19, requirements.size());
    assertNull(props.getProperty("coref.printConLLLoadingMessage"));
    assertThrows(UnsupportedOperationException.class,
        () -> requirements.add(TreeCoreAnnotations.TreeAnnotation.class));
  }

  @Test
  public void ruleConfigurationAddsParseRequirementsAndUsesParseExactRequirement() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "custom");
    props.setProperty("coref.algorithm.class", "edu.stanford.nlp.coref.hybrid.HybridCorefSystem");
    props.setProperty("coref.sieves", "PreciseConstructs");
    props.setProperty("coref.md.type", "rule");
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.demonym", "/dev/null");
    props.setProperty("coref.animate", "/dev/null");
    props.setProperty("coref.inanimate", "/dev/null");
    props.setProperty("coref.states", "/dev/null");
    props.setProperty("coref.big.gender.number", "/dev/null");
    props.setProperty("coref.countries", "/dev/null");
    props.setProperty("coref.states.provinces", "/dev/null");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();
    Collection<String> exact = annotator.exactRequirements();

    assertTrue(requirements.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
    assertTrue(requirements.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertEquals(21, requirements.size());
    assertTrue(exact.contains("tokenize"));
    assertTrue(exact.contains("pos"));
    assertTrue(exact.contains("lemma"));
    assertTrue(exact.contains("ner"));
    assertTrue(exact.contains("parse"));
    assertFalse(exact.contains("depparse"));
    assertEquals(5, exact.size());
  }

  @Test
  public void dependencyExactRequirementsRetainDependencyParser() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "custom");
    props.setProperty("coref.algorithm.class", "edu.stanford.nlp.coref.hybrid.HybridCorefSystem");
    props.setProperty("coref.sieves", "SpeakerMatch");
    props.setProperty("coref.md.type", "dependency");
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.demonym", "/dev/null");
    props.setProperty("coref.animate", "/dev/null");
    props.setProperty("coref.inanimate", "/dev/null");
    props.setProperty("coref.states", "/dev/null");
    props.setProperty("coref.big.gender.number", "/dev/null");
    props.setProperty("coref.countries", "/dev/null");
    props.setProperty("coref.states.provinces", "/dev/null");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> exact = annotator.exactRequirements();

    assertEquals(Arrays.asList("tokenize", "pos", "lemma", "ner", "depparse"),
        new ArrayList<>(exact));
  }

  @Test
  public void requirementsSatisfiedContainsOnlyProducedAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "custom");
    props.setProperty("coref.algorithm.class", "edu.stanford.nlp.coref.hybrid.HybridCorefSystem");
    props.setProperty("coref.sieves", "SpeakerMatch");
    props.setProperty("coref.md.type", "dependency");
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.demonym", "/dev/null");
    props.setProperty("coref.animate", "/dev/null");
    props.setProperty("coref.inanimate", "/dev/null");
    props.setProperty("coref.states", "/dev/null");
    props.setProperty("coref.big.gender.number", "/dev/null");
    props.setProperty("coref.countries", "/dev/null");
    props.setProperty("coref.states.provinces", "/dev/null");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(2, satisfied.size());
    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
    satisfied.clear();
    assertTrue(satisfied.isEmpty());
    assertEquals(2, annotator.requirementsSatisfied().size());
  }

  @Test
  public void annotateWithoutSentencesReturnsAndRestoresFineNerTags() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "custom");
    props.setProperty("coref.algorithm.class", "edu.stanford.nlp.coref.hybrid.HybridCorefSystem");
    props.setProperty("coref.sieves", "SpeakerMatch");
    props.setProperty("coref.md.type", "dependency");
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.demonym", "/dev/null");
    props.setProperty("coref.animate", "/dev/null");
    props.setProperty("coref.inanimate", "/dev/null");
    props.setProperty("coref.states", "/dev/null");
    props.setProperty("coref.big.gender.number", "/dev/null");
    props.setProperty("coref.countries", "/dev/null");
    props.setProperty("coref.states.provinces", "/dev/null");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Ada arrived.");
    CoreLabel ada = new CoreLabel();
    ada.setWord("Ada");
    ada.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORIGINAL");
    ada.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    ada.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON_NAME");
    CoreLabel arrived = new CoreLabel();
    arrived.setWord("arrived");
    arrived.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    arrived.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    arrived.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(ada, arrived));

    annotator.annotate(annotation);

    assertEquals("PERSON_NAME", ada.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("O", arrived.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertFalse(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }

  @Test
  public void annotateWithoutSentencesLeavesNamedEntityTagWhenFineTagIsBlank() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "custom");
    props.setProperty("coref.algorithm.class", "edu.stanford.nlp.coref.hybrid.HybridCorefSystem");
    props.setProperty("coref.sieves", "SpeakerMatch");
    props.setProperty("coref.md.type", "dependency");
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.demonym", "/dev/null");
    props.setProperty("coref.animate", "/dev/null");
    props.setProperty("coref.inanimate", "/dev/null");
    props.setProperty("coref.states", "/dev/null");
    props.setProperty("coref.big.gender.number", "/dev/null");
    props.setProperty("coref.countries", "/dev/null");
    props.setProperty("coref.states.provinces", "/dev/null");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("Paris");
    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORIGINAL");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotator.annotate(annotation);

    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void annotateRequiresDocumentTokensBeforeSentenceValidation() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "custom");
    props.setProperty("coref.algorithm.class", "edu.stanford.nlp.coref.hybrid.HybridCorefSystem");
    props.setProperty("coref.sieves", "SpeakerMatch");
    props.setProperty("coref.md.type", "dependency");
    props.setProperty("coref.useCustomMentionDetection", "true");
    props.setProperty("coref.demonym", "/dev/null");
    props.setProperty("coref.animate", "/dev/null");
    props.setProperty("coref.inanimate", "/dev/null");
    props.setProperty("coref.states", "/dev/null");
    props.setProperty("coref.big.gender.number", "/dev/null");
    props.setProperty("coref.countries", "/dev/null");
    props.setProperty("coref.states.provinces", "/dev/null");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Annotation annotation = new Annotation("No token list");

    assertThrows(NullPointerException.class, () -> annotator.annotate(annotation));
  }
}
