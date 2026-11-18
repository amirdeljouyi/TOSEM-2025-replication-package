package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class EntityMentionsAnnotatorPromptTest {

  @Test
  public void defaultRequirementsDescribeNerInputAndMentionsOutput() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(3, required.size());
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals(Collections.singleton(CoreAnnotations.MentionsAnnotation.class), satisfied);
    try {
      required.clear();
      fail("requirements must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(3, required.size());
    }
  }

  @Test
  public void customNerKeyChangesRequirements() {
    Properties properties = new Properties();
    properties.setProperty("entity.nerCoreAnnotation", CoreAnnotations.LemmaAnnotation.class.getName());
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("entity", properties);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertEquals(2, required.size());
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertFalse(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void confidenceIsNullWhenFirstTokenHasNoProbabilities() {
    CoreLabel token = new CoreLabel();
    token.setWord("Ada");
    Annotation mention = new Annotation("Ada");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    assertNull(EntityMentionsAnnotator.determineEntityMentionConfidences(mention));
  }

  @Test
  public void confidenceUsesMinimumAndOnlyLabelsFromFirstToken() {
    CoreLabel first = new CoreLabel();
    Map<String, Double> firstProbabilities = new LinkedHashMap<>();
    firstProbabilities.put("PERSON", 0.91);
    firstProbabilities.put("O", 1.20);
    first.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, firstProbabilities);
    CoreLabel second = new CoreLabel();
    Map<String, Double> secondProbabilities = new LinkedHashMap<>();
    secondProbabilities.put("PERSON", 0.72);
    secondProbabilities.put("O", 1.15);
    secondProbabilities.put("LOCATION", 0.01);
    second.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, secondProbabilities);
    Annotation mention = new Annotation("Ada Lovelace");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));

    HashMap<String, Double> result =
        EntityMentionsAnnotator.determineEntityMentionConfidences(mention);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(0.72, result.get("PERSON"), 0.000001);
    assertEquals(-1.0, result.get("O"), 0.000001);
    assertFalse(result.containsKey("LOCATION"));
  }

  @Test
  public void annotatesMultiTokenEntityWithMetadataAndDocumentText() {
    CoreLabel ada = new CoreLabel();
    ada.setWord("Ada");
    ada.setOriginalText("Ada");
    ada.setNER("PERSON");
    ada.setBeginPosition(0);
    ada.setEndPosition(3);
    ada.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Ada Lovelace");
    ada.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    Map<String, Double> adaProbabilities = new HashMap<>();
    adaProbabilities.put("PERSON", 0.94);
    ada.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, adaProbabilities);
    CoreLabel lovelace = new CoreLabel();
    lovelace.setWord("Lovelace");
    lovelace.setOriginalText("Lovelace");
    lovelace.setNER("PERSON");
    lovelace.setBeginPosition(4);
    lovelace.setEndPosition(12);
    lovelace.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Ada Lovelace");
    lovelace.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Ada_Lovelace");
    Map<String, Double> lovelaceProbabilities = new HashMap<>();
    lovelaceProbabilities.put("PERSON", 0.81);
    lovelace.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, lovelaceProbabilities);
    CoreLabel period = new CoreLabel();
    period.setWord(".");
    period.setOriginalText(".");
    period.setNER("O");
    period.setBeginPosition(12);
    period.setEndPosition(13);
    Annotation sentence = new Annotation("Ada Lovelace.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(ada, lovelace, period));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    Annotation document = new Annotation("Ada Lovelace.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator().annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    assertEquals("Ada Lovelace", mention.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PERSON", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("Ada Lovelace", mention.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    assertEquals("Ada_Lovelace", mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals(Integer.valueOf(0), mention.get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), mention.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), mention.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(0), mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), ada.get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals(0.81,
        mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).get("PERSON"), 0.000001);
    assertSame(mention, sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0));
  }

  @Test
  public void differentNormalizedValuesSplitAdjacentTokensWithSameNer() {
    CoreLabel first = new CoreLabel();
    first.setWord("one");
    first.setNER("NUMBER");
    first.setBeginPosition(0);
    first.setEndPosition(3);
    first.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "1");
    CoreLabel second = new CoreLabel();
    second.setWord("two");
    second.setNER("NUMBER");
    second.setBeginPosition(4);
    second.setEndPosition(7);
    second.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2");
    Annotation sentence = new Annotation("one two");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));
    Annotation document = new Annotation("one two");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator().annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, mentions.size());
    assertEquals("one", mentions.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("1", mentions.get(0).get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    assertEquals("two", mentions.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("2", mentions.get(1).get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }

  @Test
  public void numericCompositeValuesControlNumberChunkCompatibility() {
    CoreLabel first = new CoreLabel();
    first.setWord("twenty");
    first.setNER("NUMBER");
    first.setBeginPosition(0);
    first.setEndPosition(6);
    first.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 20);
    CoreLabel second = new CoreLabel();
    second.setWord("one");
    second.setNER("NUMBER");
    second.setBeginPosition(7);
    second.setEndPosition(10);
    second.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 21);
    CoreLabel third = new CoreLabel();
    third.setWord("units");
    third.setNER("NUMBER");
    third.setBeginPosition(11);
    third.setEndPosition(16);
    third.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 21);
    Annotation sentence = new Annotation("twenty one units");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second, third));
    Annotation document = new Annotation("twenty one units");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator().annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, mentions.size());
    assertEquals(1, mentions.get(0).get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals(2, mentions.get(1).get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("one units", mentions.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void differentTimexIdsSplitDatesAndTimexIsCopiedToMention() {
    Timex firstTimex = new Timex("DATE", "2024-01-01", null, "t1", "Monday", -1, -1);
    Timex secondTimex = new Timex("DATE", "2024-01-02", null, "t2", "Tuesday", -1, -1);
    CoreLabel monday = new CoreLabel();
    monday.setWord("Monday");
    monday.setNER("DATE");
    monday.setBeginPosition(0);
    monday.setEndPosition(6);
    monday.set(TimeAnnotations.TimexAnnotation.class, firstTimex);
    CoreLabel tuesday = new CoreLabel();
    tuesday.setWord("Tuesday");
    tuesday.setNER("DATE");
    tuesday.setBeginPosition(7);
    tuesday.setEndPosition(14);
    tuesday.set(TimeAnnotations.TimexAnnotation.class, secondTimex);
    Annotation sentence = new Annotation("Monday Tuesday");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(monday, tuesday));
    Annotation document = new Annotation("Monday Tuesday");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator().annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, mentions.size());
    assertSame(firstTimex, mentions.get(0).get(TimeAnnotations.TimexAnnotation.class));
    assertSame(secondTimex, mentions.get(1).get(TimeAnnotations.TimexAnnotation.class));
  }

  @Test
  public void indexesMentionsAcrossSentencesAndUsesSentenceTokenOffsets() {
    CoreLabel paris = new CoreLabel();
    paris.setWord("Paris");
    paris.setNER("LOCATION");
    paris.setBeginPosition(0);
    paris.setEndPosition(5);
    Annotation firstSentence = new Annotation("Paris");
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(paris));
    firstSentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    CoreLabel france = new CoreLabel();
    france.setWord("France");
    france.setNER("LOCATION");
    france.setBeginPosition(6);
    france.setEndPosition(12);
    Annotation secondSentence = new Annotation("France");
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(france));
    secondSentence.set(CoreAnnotations.TokenBeginAnnotation.class, 7);
    Annotation document = new Annotation("Paris France");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(firstSentence, secondSentence));

    new EntityMentionsAnnotator().annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, mentions.size());
    assertEquals(Integer.valueOf(0), mentions.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals(Integer.valueOf(1), mentions.get(1).get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals(Integer.valueOf(7), mentions.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(8), mentions.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(1), mentions.get(1).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals(Integer.valueOf(1), france.get(CoreAnnotations.EntityMentionIndexAnnotation.class));
  }

  @Test
  public void englishPronounsBecomePersonMentionsWithGender() {
    CoreLabel she = new CoreLabel();
    she.setWord("She");
    she.setOriginalText("She");
    she.setNER("O");
    she.setBeginPosition(0);
    she.setEndPosition(3);
    CoreLabel arrived = new CoreLabel();
    arrived.setWord("arrived");
    arrived.setOriginalText("arrived");
    arrived.setNER("O");
    arrived.setBeginPosition(4);
    arrived.setEndPosition(11);
    Annotation sentence = new Annotation("She arrived");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(she, arrived));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 4);
    Annotation document = new Annotation("She arrived");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator().annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    CoreMap pronoun = mentions.get(0);
    assertEquals("She", pronoun.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PERSON", pronoun.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("PERSON", pronoun.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("FEMALE", pronoun.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("FEMALE", she.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals(Integer.valueOf(4), pronoun.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(0), pronoun.get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertNull(pronoun.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
  }

  @Test
  public void nonEnglishConfigurationDoesNotAddPronouns() {
    Properties properties = new Properties();
    properties.setProperty("entity.language", "zh");
    CoreLabel he = new CoreLabel();
    he.setWord("he");
    he.setNER("O");
    he.setBeginPosition(0);
    he.setEndPosition(2);
    Annotation sentence = new Annotation("he");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(he));
    Annotation document = new Annotation("he");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator("entity", properties).annotate(document);

    assertTrue(document.get(CoreAnnotations.MentionsAnnotation.class).isEmpty());
    assertTrue(sentence.get(CoreAnnotations.MentionsAnnotation.class).isEmpty());
    assertNull(he.get(CoreAnnotations.GenderAnnotation.class));
  }

  @Test
  public void customNerAndNormalizedKeysDriveAnnotation() {
    Properties properties = new Properties();
    properties.setProperty("entity.nerCoreAnnotation", CoreAnnotations.LemmaAnnotation.class.getName());
    properties.setProperty("entity.nerNormalizedCoreAnnotation", CoreAnnotations.ValueAnnotation.class.getName());
    CoreLabel token = new CoreLabel();
    token.setWord("Acme");
    token.setNER("O");
    token.setLemma("ORGANIZATION");
    token.setValue("Acme_Corporation");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    Annotation sentence = new Annotation("Acme");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation("Acme");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator("entity", properties).annotate(document);

    CoreMap mention = document.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertEquals("ORGANIZATION", mention.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("ORGANIZATION", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("Acme_Corporation", mention.get(CoreAnnotations.ValueAnnotation.class));
    assertEquals("Acme", mention.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void acronymOptionRecognizesOrganizationAbbreviation() {
    Properties properties = new Properties();
    properties.setProperty("entity.acronyms", "true");
    CoreLabel international = new CoreLabel();
    international.setWord("International");
    international.setNER("ORGANIZATION");
    international.setBeginPosition(0);
    international.setEndPosition(13);
    CoreLabel business = new CoreLabel();
    business.setWord("Business");
    business.setNER("ORGANIZATION");
    business.setBeginPosition(14);
    business.setEndPosition(22);
    CoreLabel machines = new CoreLabel();
    machines.setWord("Machines");
    machines.setNER("ORGANIZATION");
    machines.setBeginPosition(23);
    machines.setEndPosition(31);
    CoreLabel ibm = new CoreLabel();
    ibm.setWord("IBM");
    ibm.setNER("O");
    ibm.setBeginPosition(32);
    ibm.setEndPosition(35);
    Annotation sentence = new Annotation("International Business Machines IBM");
    sentence.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(international, business, machines, ibm));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    Annotation document = new Annotation("International Business Machines IBM");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator("entity", properties).annotate(document);

    assertEquals("ORGANIZATION", ibm.ner());
    assertEquals(1, document.get(CoreAnnotations.MentionsAnnotation.class).size());
    assertEquals("International Business Machines",
        document.get(CoreAnnotations.MentionsAnnotation.class).get(0)
            .get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void emptySentenceProducesEmptyMentionLists() {
    Annotation sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new EntityMentionsAnnotator().annotate(document);

    assertNotNull(sentence.get(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(sentence.get(CoreAnnotations.MentionsAnnotation.class).isEmpty());
    assertNotNull(document.get(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(document.get(CoreAnnotations.MentionsAnnotation.class).isEmpty());
  }
}
