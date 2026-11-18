package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ie.PresetSequenceClassifier;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class NERCombinerAnnotatorPromptTest {

  @Test
  public void afterIsEmptyDistinguishesMissingEmptyAndNonemptyWhitespace() {
    CoreLabel missing = new CoreLabel();
    CoreLabel empty = new CoreLabel();
    CoreLabel space = new CoreLabel();
    empty.setAfter("");
    space.setAfter(" ");

    assertFalse(NERCombinerAnnotator.afterIsEmpty.apply(missing));
    assertTrue(NERCombinerAnnotator.afterIsEmpty.apply(empty));
    assertFalse(NERCombinerAnnotator.afterIsEmpty.apply(space));
  }

  @Test
  public void mergeTokensCombinesSurfaceMetadataAndInitializesMergeCount() {
    CoreLabel left = new CoreLabel();
    left.setWord("All");
    left.setAfter("");
    left.setBeginPosition(4);
    left.setEndPosition(7);
    left.setSentIndex(3);
    left.setTag("NNP");
    CoreLabel hyphen = new CoreLabel();
    hyphen.setWord("-");
    hyphen.setAfter("");
    hyphen.setBeginPosition(7);
    hyphen.setEndPosition(8);
    hyphen.setSentIndex(3);
    hyphen.setTag("HYPH");

    NERCombinerAnnotator.mergeTokens(left, hyphen);

    assertEquals("All-", left.word());
    assertEquals("", left.after());
    assertEquals(4, left.beginPosition());
    assertEquals(8, left.endPosition());
    assertEquals("All--3", left.value());
    assertEquals("NNP", left.tag());
    assertEquals(Integer.valueOf(1), left.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
    assertEquals("-", hyphen.word());
  }

  @Test
  public void mergeTokensCanExtendAnAlreadyMergedToken() {
    CoreLabel merged = new CoreLabel();
    merged.setWord("All-");
    merged.setAfter("");
    merged.setEndPosition(4);
    merged.setSentIndex(1);
    merged.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 1);
    CoreLabel right = new CoreLabel();
    right.setWord("Star");
    right.setAfter(" ");
    right.setEndPosition(8);

    NERCombinerAnnotator.mergeTokens(merged, right);

    assertEquals("All-Star", merged.word());
    assertEquals(" ", merged.after());
    assertEquals(8, merged.endPosition());
    assertEquals("All-Star-1", merged.value());
    assertEquals(Integer.valueOf(2), merged.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }

  @Test
  public void transferReturnsWithoutChangingAnAnnotationWhenEitherTokenListIsEmpty() {
    Annotation emptySource = new Annotation("");
    emptySource.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    emptySource.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    CoreLabel originalToken = new CoreLabel();
    originalToken.setWord("Ada");
    originalToken.setNER("OLD");
    Annotation original = new Annotation("Ada");
    original.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(originalToken));
    ArrayCoreMap originalSentence = new ArrayCoreMap();
    originalSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(originalToken));
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(originalSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(emptySource, original);

    assertEquals("OLD", originalToken.ner());

    CoreLabel sourceToken = new CoreLabel();
    sourceToken.setNER("PERSON");
    Annotation source = new Annotation("Ada");
    source.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(sourceToken));
    Annotation emptyTarget = new Annotation("");
    emptyTarget.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    emptyTarget.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, emptyTarget);

    assertTrue(emptyTarget.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }

  @Test
  public void transferCopiesNerNormalizationConfidenceAndNumericAnnotations() {
    CoreLabel classified = new CoreLabel();
    classified.setNER("MONEY");
    classified.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "$12.00");
    classified.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "MONEY");
    classified.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "NUMBER");
    classified.set(CoreAnnotations.NumericValueAnnotation.class, 12.0);
    classified.set(CoreAnnotations.NumericTypeAnnotation.class, "MONEY");
    classified.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 12.0);
    classified.set(CoreAnnotations.NumericCompositeTypeAnnotation.class, "MONEY");
    Map<String, Double> confidence = new HashMap<>();
    confidence.put("MONEY", 0.91);
    classified.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, confidence);
    Annotation source = new Annotation("$12");
    source.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(classified));
    CoreLabel originalToken = new CoreLabel();
    originalToken.setWord("$12");
    originalToken.setNER("OLD");
    Annotation target = new Annotation("$12");
    target.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(originalToken));
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(originalToken));
    target.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, target);

    assertEquals("MONEY", originalToken.ner());
    assertEquals("$12.00", originalToken.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    assertEquals("MONEY", originalToken.get(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
    assertEquals("NUMBER", originalToken.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertEquals(Double.valueOf(12.0), originalToken.get(CoreAnnotations.NumericValueAnnotation.class));
    assertEquals("MONEY", originalToken.get(CoreAnnotations.NumericTypeAnnotation.class));
    assertEquals(Double.valueOf(12.0), originalToken.get(CoreAnnotations.NumericCompositeValueAnnotation.class));
    assertEquals("MONEY", originalToken.get(CoreAnnotations.NumericCompositeTypeAnnotation.class));
    assertSame(confidence, originalToken.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
  }

  @Test
  public void transferRepeatsMergedTokenAnnotationsAcrossAllOriginalTokens() {
    CoreLabel merged = new CoreLabel();
    merged.setWord("All-Star");
    merged.setNER("ORGANIZATION");
    merged.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "All-Star");
    merged.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);
    CoreLabel trailing = new CoreLabel();
    trailing.setWord("game");
    trailing.setNER("O");
    Annotation source = new Annotation("All-Star game");
    source.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(merged, trailing));
    CoreLabel all = new CoreLabel();
    CoreLabel hyphen = new CoreLabel();
    CoreLabel star = new CoreLabel();
    CoreLabel game = new CoreLabel();
    Annotation target = new Annotation("All-Star game");
    target.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(all, hyphen, star, game));
    ArrayCoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(all, hyphen));
    ArrayCoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(star, game));
    target.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(firstSentence, secondSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, target);

    assertEquals("ORGANIZATION", all.ner());
    assertEquals("ORGANIZATION", hyphen.ner());
    assertEquals("ORGANIZATION", star.ner());
    assertEquals("All-Star", star.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    assertEquals("O", game.ner());
  }

  @Test
  public void doOneSentenceUsesPresetClassifierAndCopiesOutputFields() throws Exception {
    Properties classifierProperties = new Properties();
    classifierProperties.setProperty("ner.model", "");
    classifierProperties.setProperty("ner.usePresetNERTags", "true");
    classifierProperties.setProperty("ner.applyNumericClassifiers", "false");
    classifierProperties.setProperty("ner.useSUTime", "false");
    NERClassifierCombiner combiner = new NERClassifierCombiner(
        false, false, new PresetSequenceClassifier<CoreLabel>(classifierProperties));
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(combiner, false, 1, 0, 10, false, false);
    CoreLabel ada = new CoreLabel();
    ada.setWord("Ada");
    ada.setOriginalText("Ada");
    ada.set(CoreAnnotations.PresetAnswerAnnotation.class, "PERSON");
    ada.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Ada Lovelace");
    CoreLabel arrived = new CoreLabel();
    arrived.setWord("arrived");
    arrived.setOriginalText("arrived");
    arrived.set(CoreAnnotations.PresetAnswerAnnotation.class, "O");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(ada, arrived));
    Annotation document = new Annotation("Ada arrived");

    annotator.doOneSentence(document, sentence);

    assertEquals("PERSON", ada.ner());
    assertEquals("PERSON", ada.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertEquals("Ada Lovelace", ada.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    assertEquals(Double.valueOf(-1.0), ada.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).get("PERSON"));
    assertEquals("O", arrived.ner());
    assertEquals("O", arrived.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
  }

  @Test
  public void oversizedSentenceFallsBackWithoutOverwritingExistingNer() throws Exception {
    Properties classifierProperties = new Properties();
    classifierProperties.setProperty("ner.model", "");
    classifierProperties.setProperty("ner.usePresetNERTags", "true");
    classifierProperties.setProperty("ner.applyNumericClassifiers", "false");
    classifierProperties.setProperty("ner.useSUTime", "false");
    NERClassifierCombiner combiner = new NERClassifierCombiner(
        false, false, new PresetSequenceClassifier<CoreLabel>(classifierProperties));
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(combiner, false, 1, 0, 1, false, false);
    CoreLabel tagged = new CoreLabel();
    tagged.setWord("Known");
    tagged.setNER("LOCATION");
    CoreLabel untagged = new CoreLabel();
    untagged.setWord("unknown");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(tagged, untagged));

    annotator.doOneSentence(new Annotation("Known unknown"), sentence);

    assertEquals("LOCATION", tagged.ner());
    assertEquals("O", untagged.ner());
    assertNull(tagged.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertNull(untagged.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
  }

  @Test
  public void annotateRunsSentenceClassificationAndRemovesTimexFromMoney() throws Exception {
    Properties classifierProperties = new Properties();
    classifierProperties.setProperty("ner.model", "");
    classifierProperties.setProperty("ner.usePresetNERTags", "true");
    classifierProperties.setProperty("ner.applyNumericClassifiers", "false");
    classifierProperties.setProperty("ner.useSUTime", "false");
    NERClassifierCombiner combiner = new NERClassifierCombiner(
        false, false, new PresetSequenceClassifier<CoreLabel>(classifierProperties));
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(combiner, false, 1, 0, 20, false, false);
    CoreLabel amount = new CoreLabel();
    amount.setWord("cash");
    amount.setOriginalText("cash");
    amount.setBefore("");
    amount.setAfter("");
    amount.set(CoreAnnotations.PresetAnswerAnnotation.class, "MONEY");
    amount.set(TimeAnnotations.TimexAnnotation.class, new Timex("MONEY", "12"));
    CoreLabel person = new CoreLabel();
    person.setWord("Ada");
    person.setOriginalText("Ada");
    person.setBefore(" ");
    person.setAfter("");
    person.set(CoreAnnotations.PresetAnswerAnnotation.class, "PERSON");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "cash Ada");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(amount, person));
    Annotation document = new Annotation(Collections.<CoreMap>singletonList(sentence));

    annotator.annotate(document);

    assertEquals("MONEY", amount.ner());
    assertNull(amount.get(TimeAnnotations.TimexAnnotation.class));
    assertEquals("PERSON", person.ner());
    assertEquals(Double.valueOf(-1.0), person.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).get("PERSON"));
    assertNull(document.get(CoreAnnotations.MentionsAnnotation.class));
  }

  @Test
  public void annotateWithNerTokenizationTransfersOneClassificationToHyphenPieces() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("ner.model", "");
    properties.setProperty("ner.usePresetNERTags", "true");
    properties.setProperty("ner.applyNumericClassifiers", "false");
    properties.setProperty("ner.useSUTime", "false");
    properties.setProperty("ner.applyFineGrained", "false");
    properties.setProperty("ner.buildEntityMentions", "false");
    properties.setProperty("ner.useNERSpecificTokenization", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);
    CoreLabel all = new CoreLabel();
    all.setWord("All");
    all.setOriginalText("All");
    all.setAfter("");
    all.setSentIndex(0);
    all.setBeginPosition(0);
    all.setEndPosition(3);
    all.set(CoreAnnotations.PresetAnswerAnnotation.class, "ORGANIZATION");
    CoreLabel hyphen = new CoreLabel();
    hyphen.setWord("-");
    hyphen.setOriginalText("-");
    hyphen.setAfter("");
    hyphen.setSentIndex(0);
    hyphen.setBeginPosition(3);
    hyphen.setEndPosition(4);
    hyphen.set(CoreAnnotations.PresetAnswerAnnotation.class, "O");
    CoreLabel star = new CoreLabel();
    star.setWord("Star");
    star.setOriginalText("Star");
    star.setAfter("");
    star.setSentIndex(0);
    star.setBeginPosition(4);
    star.setEndPosition(8);
    star.set(CoreAnnotations.PresetAnswerAnnotation.class, "O");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "All-Star");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(all, hyphen, star));
    Annotation document = new Annotation(Collections.<CoreMap>singletonList(sentence));

    annotator.annotate(document);

    assertEquals("ORGANIZATION", all.ner());
    assertEquals("ORGANIZATION", hyphen.ner());
    assertEquals("ORGANIZATION", star.ner());
    assertEquals(3, document.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("-", document.get(CoreAnnotations.TokensAnnotation.class).get(1).word());
  }

  @Test
  public void nerTokenizationDoesNotMergeHyphenBeforeExceptionWord() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("ner.model", "");
    properties.setProperty("ner.usePresetNERTags", "true");
    properties.setProperty("ner.applyNumericClassifiers", "false");
    properties.setProperty("ner.useSUTime", "false");
    properties.setProperty("ner.applyFineGrained", "false");
    properties.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);
    CoreLabel chicago = new CoreLabel();
    chicago.setWord("Chicago");
    chicago.setOriginalText("Chicago");
    chicago.setAfter("");
    chicago.setSentIndex(0);
    chicago.setBeginPosition(0);
    chicago.setEndPosition(7);
    chicago.set(CoreAnnotations.PresetAnswerAnnotation.class, "LOCATION");
    CoreLabel hyphen = new CoreLabel();
    hyphen.setWord("-");
    hyphen.setOriginalText("-");
    hyphen.setAfter("");
    hyphen.setSentIndex(0);
    hyphen.setBeginPosition(7);
    hyphen.setEndPosition(8);
    hyphen.set(CoreAnnotations.PresetAnswerAnnotation.class, "O");
    CoreLabel based = new CoreLabel();
    based.setWord("based");
    based.setOriginalText("based");
    based.setAfter("");
    based.setSentIndex(0);
    based.setBeginPosition(8);
    based.setEndPosition(13);
    based.set(CoreAnnotations.PresetAnswerAnnotation.class, "O");
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "Chicago-based");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(chicago, hyphen, based));
    Annotation document = new Annotation(Collections.<CoreMap>singletonList(sentence));

    annotator.annotate(document);

    assertEquals("LOCATION", chicago.ner());
    assertEquals("O", hyphen.ner());
    assertEquals("O", based.ner());
  }

  @Test
  public void requirementSetsReflectReducedConfigurationAndEntityMentionSetting() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("ner.model", "");
    properties.setProperty("ner.statisticalOnly", "true");
    properties.setProperty("ner.applyNumericClassifiers", "false");
    properties.setProperty("ner.useSUTime", "false");
    properties.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator reduced = new NERCombinerAnnotator(properties);
    Set<Class<? extends CoreAnnotation>> required = reduced.requires();
    Set<Class<? extends CoreAnnotation>> satisfied = reduced.requirementsSatisfied();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.BeforeAnnotation.class));
    assertFalse(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertFalse(required.contains(CoreAnnotations.LemmaAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
    assertFalse(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));

    Properties classifierProperties = new Properties();
    classifierProperties.setProperty("ner.model", "");
    classifierProperties.setProperty("ner.applyNumericClassifiers", "false");
    classifierProperties.setProperty("ner.useSUTime", "false");
    NERClassifierCombiner combiner = new NERClassifierCombiner(
        true, false, new PresetSequenceClassifier<CoreLabel>(classifierProperties));
    NERCombinerAnnotator withMentions = new NERCombinerAnnotator(combiner, false, 1, 0, 5, false, true);
    Set<Class<? extends CoreAnnotation>> mentionSatisfied = withMentions.requirementsSatisfied();
    assertTrue(mentionSatisfied.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(mentionSatisfied.contains(CoreAnnotations.EntityTypeAnnotation.class));
    assertTrue(mentionSatisfied.contains(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertTrue(withMentions.requires().contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void requiresReturnsAnUnmodifiableSet() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("ner.model", "");
    properties.setProperty("ner.statisticalOnly", "true");
    properties.setProperty("ner.applyNumericClassifiers", "false");
    properties.setProperty("ner.useSUTime", "false");
    properties.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

    annotator.requires().add(CoreAnnotations.PartOfSpeechAnnotation.class);
  }
}
