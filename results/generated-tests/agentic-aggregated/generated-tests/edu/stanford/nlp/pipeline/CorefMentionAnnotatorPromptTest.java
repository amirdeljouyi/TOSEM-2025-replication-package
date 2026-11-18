package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class CorefMentionAnnotatorPromptTest {

  @Test
  public void synchronizesOneSharedToken() {
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 1, Collections.singletonList(joe), null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void synchronizesSeveralSharedTokens() {
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreLabel smith = new CoreLabel();
    smith.setWord("Smith");
    List<CoreLabel> tokens = Arrays.asList(joe, smith);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 2, tokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void rejectsDifferentTokenObjectsWithSameText() {
    CoreLabel corefJoe = new CoreLabel();
    corefJoe.setWord("Joe");
    CoreLabel entityJoe = new CoreLabel();
    entityJoe.setWord("Joe");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(corefJoe));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 1, Collections.singletonList(corefJoe), null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(entityJoe));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void skipsOneLeadingTitleForPersonEntity() {
    CoreLabel president = new CoreLabel();
    president.setWord("President");
    president.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    List<CoreLabel> sentenceTokens = Arrays.asList(president, joe);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 2, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void skipsMultipleLeadingTitlesForPersonEntity() {
    CoreLabel former = new CoreLabel();
    former.setWord("Former");
    former.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    CoreLabel president = new CoreLabel();
    president.setWord("President");
    president.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    List<CoreLabel> sentenceTokens = Arrays.asList(former, president, joe);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 3, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void doesNotSkipTitleForOrganizationEntity() {
    CoreLabel president = new CoreLabel();
    president.setWord("President");
    president.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    List<CoreLabel> sentenceTokens = Arrays.asList(president, joe);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 2, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void fineGrainedTitleComparisonIsCaseSensitive() {
    CoreLabel president = new CoreLabel();
    president.setWord("President");
    president.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "title");
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    List<CoreLabel> sentenceTokens = Arrays.asList(president, joe);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 2, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void entityTypeComparisonIsCaseSensitive() {
    CoreLabel president = new CoreLabel();
    president.setWord("President");
    president.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    List<CoreLabel> sentenceTokens = Arrays.asList(president, joe);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 2, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "person");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void synchronizesPossessiveSuffixAfterEntityTokens() {
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreLabel possessive = new CoreLabel();
    possessive.setWord("'s");
    List<CoreLabel> sentenceTokens = Arrays.asList(joe, possessive);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 2, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void synchronizesTitleNameAndPossessiveCombination() {
    CoreLabel president = new CoreLabel();
    president.setWord("President");
    president.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "TITLE");
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreLabel smith = new CoreLabel();
    smith.setWord("Smith");
    CoreLabel possessive = new CoreLabel();
    possessive.setWord("'s");
    List<CoreLabel> sentenceTokens = Arrays.asList(president, joe, smith, possessive);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 4, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(joe, smith));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void rejectsNonPossessiveTrailingCorefToken() {
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreLabel car = new CoreLabel();
    car.setWord("car");
    List<CoreLabel> sentenceTokens = Arrays.asList(joe, car);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 2, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void rejectsMoreThanOneTrailingCorefToken() {
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreLabel possessive = new CoreLabel();
    possessive.setWord("'s");
    CoreLabel car = new CoreLabel();
    car.setWord("car");
    List<CoreLabel> sentenceTokens = Arrays.asList(joe, possessive, car);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 3, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void rejectsEntityMentionLongerThanCorefMention() {
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreLabel smith = new CoreLabel();
    smith.setWord("Smith");
    List<CoreLabel> sentenceTokens = Arrays.asList(joe, smith);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 1, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void rejectsEmptyEntityMentionEvenWhenCorefHasPossessive() {
    CoreLabel possessive = new CoreLabel();
    possessive.setWord("'s");
    List<CoreLabel> sentenceTokens = Collections.singletonList(possessive);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 1, sentenceTokens, null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void rejectsEmptyCorefAndEntityMentions() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 0, Collections.emptyList(), null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    assertFalse(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test(expected = NullPointerException.class)
  public void missingEntityTypeIsRejectedExplicitly() {
    CoreLabel joe = new CoreLabel();
    joe.setWord("Joe");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    Mention corefMention = new Mention(-1, 0, 1, Collections.singletonList(joe), null, null);
    corefMention.sentNum = 0;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(joe));

    CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention);
  }

  @Test
  public void usesMentionSentenceNumberAcrossMultipleSentences() {
    CoreLabel firstJoe = new CoreLabel();
    firstJoe.setWord("Joe");
    CoreLabel secondJoe = new CoreLabel();
    secondJoe.setWord("Joe");
    CoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(firstJoe));
    CoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(secondJoe));
    Annotation document = new Annotation(Arrays.asList(firstSentence, secondSentence));
    Mention corefMention = new Mention(-1, 0, 1, Collections.singletonList(secondJoe), null, null);
    corefMention.sentNum = 1;
    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    entityMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(secondJoe));

    assertTrue(CorefMentionAnnotator.synchCorefMentionEntityMention(document, corefMention, entityMention));
  }

  @Test
  public void ruleModeDeclaresConstituencyRequirements() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "RULE");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(properties);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.BeginIndexAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.EndIndexAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(requirements.contains(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class));
  }

  @Test
  public void dependencyModeDoesNotDeclareConstituencyRequirements() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "DEPENDENCY");
    properties.setProperty("coref.md.isTraining", "true");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(properties);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requires();

    assertFalse(requirements.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertFalse(requirements.contains(CoreAnnotations.BeginIndexAnnotation.class));
    assertFalse(requirements.contains(CoreAnnotations.EndIndexAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.EntityTypeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.IndexAnnotation.class));
  }

  @Test
  public void satisfiedRequirementsContainAllPublishedAnnotations() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "RULE");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(properties);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(4, satisfied.size());
    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.UtteranceAnnotation.class));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void satisfiedRequirementsAreUnmodifiable() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "RULE");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(properties);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    satisfied.clear();
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMentionDetectorTypeFailsConstruction() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "not-a-detector");

    new CorefMentionAnnotator(properties);
  }

  @Test
  public void annotateEmptyDocumentPublishesEmptyResultsAndMappings() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "RULE");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(properties);
    Annotation document = new Annotation(Collections.emptyList());

    annotator.annotate(document);

    assertNotNull(document.get(CorefCoreAnnotations.CorefMentionsAnnotation.class));
    assertTrue(document.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).isEmpty());
    assertEquals(new HashMap<Integer, Integer>(),
        document.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class));
    assertEquals(new HashMap<Integer, Integer>(),
        document.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class));
    assertEquals("true", properties.getProperty("removeNestedMentions"));
  }

  @Test
  public void chineseNewswireConllDocumentKeepsNestedMentionsEnabled() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "RULE");
    properties.setProperty("coref.language", "zh");
    properties.setProperty("coref.conll", "true");
    properties.setProperty("coref.specialCaseNewswire", "true");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(properties);
    Annotation document = new Annotation(Collections.emptyList());
    document.set(CoreAnnotations.DocIDAnnotation.class, "nw/test/document");

    annotator.annotate(document);

    assertEquals("false", properties.getProperty("removeNestedMentions"));
    assertTrue(document.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).isEmpty());
  }

  @Test
  public void repeatedAnnotationOfEmptyDocumentReplacesResultsCleanly() {
    Properties properties = new Properties();
    properties.setProperty("coref.demonym", "/dev/null");
    properties.setProperty("coref.animate", "/dev/null");
    properties.setProperty("coref.inanimate", "/dev/null");
    properties.setProperty("coref.states", "/dev/null");
    properties.setProperty("coref.big.gender.number", "/dev/null");
    properties.setProperty("coref.countries", "/dev/null");
    properties.setProperty("coref.states.provinces", "/dev/null");
    properties.setProperty("coref.md.type", "RULE");
    CorefMentionAnnotator annotator = new CorefMentionAnnotator(properties);
    Annotation document = new Annotation(Collections.emptyList());
    List<Mention> staleMentions = new ArrayList<>();
    staleMentions.add(new Mention());
    document.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, staleMentions);

    annotator.annotate(document);
    annotator.annotate(document);

    assertTrue(document.get(CorefCoreAnnotations.CorefMentionsAnnotation.class).isEmpty());
    assertTrue(document.get(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class).isEmpty());
    assertTrue(document.get(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class).isEmpty());
  }
}
