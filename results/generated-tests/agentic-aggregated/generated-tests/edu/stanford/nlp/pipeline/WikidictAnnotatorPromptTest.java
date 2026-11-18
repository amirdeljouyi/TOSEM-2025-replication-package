package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class WikidictAnnotatorPromptTest {

  @Test
  public void normalizeTimexRemovesTimeOfDay() {
    assertEquals("2017-04-03", WikidictAnnotator.normalizeTimex("2017-04-03T18:25"));
  }

  @Test
  public void normalizeTimexRemovesEverythingFromFirstT() {
    assertEquals("2017", WikidictAnnotator.normalizeTimex("2017T12T45"));
  }

  @Test
  public void normalizeTimexPreservesDateWithoutTime() {
    assertEquals("2024-W08", WikidictAnnotator.normalizeTimex("2024-W08"));
  }

  @Test
  public void normalizeTimexSpecialCasesPresent() {
    assertEquals("PRESENT", WikidictAnnotator.normalizeTimex("PRESENT"));
  }

  @Test(expected = NullPointerException.class)
  public void normalizeTimexRejectsNull() {
    WikidictAnnotator.normalizeTimex(null);
  }

  @Test
  public void linkNormalizesConcreteDateTimex() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("date.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("date", properties);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "Monday evening");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "dAtE");
    mention.set(TimeAnnotations.TimexAnnotation.class, new Timex("DATE", "2023-10-09T19:30"));

    assertEquals(Optional.of("2023-10-09"), annotator.link(mention));
    Files.delete(dictionary);
  }

  @Test
  public void linkNormalizesTimeAndSetTimexes() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("temporal.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("temporal", properties);
    CoreMap timeMention = new ArrayCoreMap();
    timeMention.set(CoreAnnotations.TextAnnotation.class, "noon");
    timeMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TIME");
    timeMention.set(TimeAnnotations.TimexAnnotation.class, new Timex("TIME", "2022-01-02T12:00"));
    CoreMap setMention = new ArrayCoreMap();
    setMention.set(CoreAnnotations.TextAnnotation.class, "weekly");
    setMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "set");
    setMention.set(TimeAnnotations.TimexAnnotation.class, new Timex("SET", "P1W"));

    assertEquals(Optional.of("2022-01-02"), annotator.link(timeMention));
    assertEquals(Optional.of("P1W"), annotator.link(setMention));
    Files.delete(dictionary);
  }

  @Test
  public void linkRejectsSymbolicAndValuelessTimexes() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("symbolic.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("symbolic", properties);
    CoreMap present = new ArrayCoreMap();
    present.set(CoreAnnotations.TextAnnotation.class, "today");
    present.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    present.set(TimeAnnotations.TimexAnnotation.class, new Timex("DATE", "PRESENT_REF"));
    CoreMap past = new ArrayCoreMap();
    past.set(CoreAnnotations.TextAnnotation.class, "previously");
    past.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TIME");
    past.set(TimeAnnotations.TimexAnnotation.class, new Timex("TIME", "PAST"));
    CoreMap valueless = new ArrayCoreMap();
    valueless.set(CoreAnnotations.TextAnnotation.class, "someday");
    valueless.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    valueless.set(TimeAnnotations.TimexAnnotation.class, new Timex("DATE", null));

    assertFalse(annotator.link(present).isPresent());
    assertFalse(annotator.link(past).isPresent());
    assertFalse(annotator.link(valueless).isPresent());
    Files.delete(dictionary);
  }

  @Test
  public void linkNormalizesOrdinalNumber() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("ordinal.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("ordinal", properties);
    CoreMap integer = new ArrayCoreMap();
    integer.set(CoreAnnotations.TextAnnotation.class, "twenty-first");
    integer.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ordinal");
    integer.set(CoreAnnotations.NumericValueAnnotation.class, 21);
    CoreMap decimal = new ArrayCoreMap();
    decimal.set(CoreAnnotations.TextAnnotation.class, "one and a halfth");
    decimal.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
    decimal.set(CoreAnnotations.NumericValueAnnotation.class, 1.5d);

    assertEquals(Optional.of("21"), annotator.link(integer));
    assertEquals(Optional.of("1.5"), annotator.link(decimal));
    Files.delete(dictionary);
  }

  @Test
  public void ordinalWithoutNumericValueDoesNotLink() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("ordinal.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("ordinal", properties);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "last");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");

    assertEquals(Optional.empty(), annotator.link(mention));
    Files.delete(dictionary);
  }

  @Test
  public void linkPreservesSimpleNumericSurfaceForms() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("number.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("number", properties);
    CoreMap integer = new ArrayCoreMap();
    integer.set(CoreAnnotations.TextAnnotation.class, "007");
    CoreMap decimal = new ArrayCoreMap();
    decimal.set(CoreAnnotations.TextAnnotation.class, "12.50");
    decimal.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreMap dots = new ArrayCoreMap();
    dots.set(CoreAnnotations.TextAnnotation.class, "...");

    assertEquals(Optional.of("007"), annotator.link(integer));
    assertEquals(Optional.of("12.50"), annotator.link(decimal));
    assertEquals(Optional.of("..."), annotator.link(dots));
    Files.delete(dictionary);
  }

  @Test
  public void linkRejectsSignedCommaAndEmptyNumbers() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("number.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("number", properties);
    CoreMap signed = new ArrayCoreMap();
    signed.set(CoreAnnotations.TextAnnotation.class, "-12");
    CoreMap comma = new ArrayCoreMap();
    comma.set(CoreAnnotations.TextAnnotation.class, "1,000");
    CoreMap empty = new ArrayCoreMap();
    empty.set(CoreAnnotations.TextAnnotation.class, "");

    assertEquals(Optional.empty(), annotator.link(signed));
    assertEquals(Optional.empty(), annotator.link(comma));
    assertEquals(Optional.empty(), annotator.link(empty));
    Files.delete(dictionary);
  }

  @Test
  public void caselessDictionaryHonorsThresholdOriginalTextAndNer() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-caseless", ".tsv");
    Files.write(dictionary, Arrays.asList(
        "Stanford\tStanford_University\t0.90",
        "Mercury\tMercury_(planet)\t0.50",
        "Discarded\tLow_score_page\t0.49"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("lookup.wikidict", dictionary.toString());
    properties.setProperty("lookup.caseless", "true");
    properties.setProperty("lookup.threshold", "0.5");
    WikidictAnnotator annotator = new WikidictAnnotator("lookup", properties);
    CoreMap stanford = new ArrayCoreMap();
    stanford.set(CoreAnnotations.TextAnnotation.class, "changed text");
    stanford.set(CoreAnnotations.OriginalTextAnnotation.class, "STANFORD");
    stanford.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    CoreMap mercury = new ArrayCoreMap();
    mercury.set(CoreAnnotations.TextAnnotation.class, "mErCuRy");
    mercury.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    CoreMap discarded = new ArrayCoreMap();
    discarded.set(CoreAnnotations.TextAnnotation.class, "discarded");
    discarded.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
    CoreMap outside = new ArrayCoreMap();
    outside.set(CoreAnnotations.TextAnnotation.class, "stanford");
    outside.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreMap missingNer = new ArrayCoreMap();
    missingNer.set(CoreAnnotations.TextAnnotation.class, "stanford");

    assertEquals(Optional.of("Stanford_University"), annotator.link(stanford));
    assertEquals(Optional.of("Mercury_(planet)"), annotator.link(mercury));
    assertEquals(Optional.empty(), annotator.link(discarded));
    assertEquals(Optional.empty(), annotator.link(outside));
    assertEquals(Optional.empty(), annotator.link(missingNer));
    Files.delete(dictionary);
  }

  @Test
  public void caseSensitiveDictionaryUsesLastDuplicateAndAnnotatesMentionTokens() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-sensitive", ".tsv");
    Files.write(dictionary, Arrays.asList(
        "Paris\tParis_(mythology)\t0.1",
        "Paris\tParis\t0.9"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("entitylink.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator(properties);
    CoreMap lowerCase = new ArrayCoreMap();
    lowerCase.set(CoreAnnotations.TextAnnotation.class, "paris");
    lowerCase.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    CoreLabel parisToken = CoreLabel.wordFromString("Paris");
    CoreMap parisMention = new ArrayCoreMap();
    parisMention.set(CoreAnnotations.TextAnnotation.class, "Paris");
    parisMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    parisMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(parisToken));
    CoreLabel otherToken = CoreLabel.wordFromString("rocks");
    otherToken.set(CoreAnnotations.WikipediaEntityAnnotation.class, "stale");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(parisToken, otherToken));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(parisMention));
    Annotation document = new Annotation("Paris rocks");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    assertEquals(Optional.empty(), annotator.link(lowerCase));
    annotator.annotate(document);
    assertEquals("Paris", parisMention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("Paris", parisToken.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("O", otherToken.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    Files.delete(dictionary);
  }

  @Test
  public void annotateHandlesMultipleSentencesAndNormalizationKinds() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("multi.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("multi", properties);
    CoreLabel dateToken = CoreLabel.wordFromString("tonight");
    CoreMap dateMention = new ArrayCoreMap();
    dateMention.set(CoreAnnotations.TextAnnotation.class, "tonight");
    dateMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TIME");
    dateMention.set(TimeAnnotations.TimexAnnotation.class, new Timex("TIME", "2020-05-06T20:00"));
    dateMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(dateToken));
    CoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(dateToken));
    firstSentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(dateMention));
    CoreLabel ordinalToken = CoreLabel.wordFromString("second");
    CoreMap ordinalMention = new ArrayCoreMap();
    ordinalMention.set(CoreAnnotations.TextAnnotation.class, "second");
    ordinalMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
    ordinalMention.set(CoreAnnotations.NumericValueAnnotation.class, 2);
    ordinalMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(ordinalToken));
    CoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(ordinalToken));
    secondSentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(ordinalMention));
    Annotation document = new Annotation("tonight second");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(firstSentence, secondSentence));

    annotator.annotate(document);

    assertEquals("2020-05-06", dateMention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("2020-05-06", dateToken.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("2", ordinalMention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("2", ordinalToken.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    Files.delete(dictionary);
  }

  @Test
  public void annotateLeavesUnlinkedMentionUnsetAndResetsAllTokens() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("unlinked.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("unlinked", properties);
    CoreLabel token = CoreLabel.wordFromString("unknown");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "old-link");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "unknown");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("unknown");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertNull(mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    Files.delete(dictionary);
  }

  @Test
  public void annotateRejectsDocumentWithoutSentences() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("missing.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("missing", properties);

    try {
      annotator.annotate(new Annotation("No sentence annotations"));
      fail("a document without sentence annotations should be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("unable to find sentences"));
    }
    Files.delete(dictionary);
  }

  @Test
  public void configuredThreadCountDoesNotChangeSentenceResults() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("parallel.wikidict", dictionary.toString());
    properties.setProperty("parallel.threads", "2");
    WikidictAnnotator annotator = new WikidictAnnotator("parallel", properties);
    CoreLabel first = CoreLabel.wordFromString("12");
    CoreMap firstMention = new ArrayCoreMap();
    firstMention.set(CoreAnnotations.TextAnnotation.class, "12");
    firstMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(first));
    CoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(first));
    firstSentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(firstMention));
    CoreLabel second = CoreLabel.wordFromString("34");
    CoreMap secondMention = new ArrayCoreMap();
    secondMention.set(CoreAnnotations.TextAnnotation.class, "34");
    secondMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(second));
    CoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(second));
    secondSentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(secondMention));
    Annotation document = new Annotation("12 34");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(firstSentence, secondSentence));

    annotator.annotate(document);

    assertEquals(2, annotator.nThreads());
    assertEquals(-1L, annotator.maxTime());
    assertEquals("12", first.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("34", second.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    Files.delete(dictionary);
  }

  @Test
  public void requirementsSatisfiedContainsOnlyWikipediaEntity() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("requirements.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("requirements", properties);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requirementsSatisfied();

    assertEquals(1, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
    try {
      requirements.clear();
      fail("requirementsSatisfied should be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(1, requirements.size());
    }
    Files.delete(dictionary);
  }

  @Test
  public void requiresContainsExactInputsAndIsImmutable() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-empty", ".tsv");
    Properties properties = new Properties();
    properties.setProperty("requirements.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("requirements", properties);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertEquals(5, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.MentionsAnnotation.class));
    try {
      requirements.remove(CoreAnnotations.TextAnnotation.class);
      fail("requires should be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(5, requirements.size());
    }
    Files.delete(dictionary);
  }

  @Test
  public void constructorWrapsMissingDictionaryFailure() {
    Properties properties = new Properties();
    properties.setProperty("broken.wikidict", "/definitely/not/a/wikidict.tsv");

    try {
      new WikidictAnnotator("broken", properties);
      fail("missing dictionary should fail construction");
    } catch (RuntimeException expected) {
      assertNotNull(expected.getCause());
    }
  }

  @Test
  public void constructorWrapsInvalidScoreWhenThresholdEnabled() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-invalid-score", ".tsv");
    Files.write(dictionary, Collections.singletonList("Name\tPage\tnot-a-number"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("broken.wikidict", dictionary.toString());
    properties.setProperty("broken.threshold", "0.1");

    try {
      new WikidictAnnotator("broken", properties);
      fail("invalid score should fail construction");
    } catch (RuntimeException expected) {
      assertTrue(expected.getCause() instanceof NumberFormatException);
    }
    Files.delete(dictionary);
  }

  @Test
  public void constructorSkipsLinesWhoseFirstFieldIsEmpty() throws Exception {
    Path dictionary = Files.createTempFile("wikidict-skipped", ".tsv");
    Files.write(dictionary, Collections.singletonList("\tPage\t1.0"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("skip.wikidict", dictionary.toString());
    WikidictAnnotator annotator = new WikidictAnnotator("skip", properties);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TextAnnotation.class, "Page");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

    assertEquals(Optional.empty(), annotator.link(mention));
    Files.delete(dictionary);
  }
}
