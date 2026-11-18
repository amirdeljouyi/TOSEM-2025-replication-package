package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GenderAnnotatorPromptTest {

  @Test
  public void loadGenderNamesReadsCommaSeparatedAndSeparateLinesCaseInsensitively() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Path additionalFile = Files.createTempFile("gender-extra", ".txt");
    Files.write(maleFile, Collections.singletonList("placeholder"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("placeholder"), StandardCharsets.UTF_8);
    Files.write(additionalFile, Arrays.asList("ALICE,Bob", "ChArLiE"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    HashSet<String> names = new HashSet<>();

    annotator.loadGenderNames(names, additionalFile.toString());

    assertEquals(3, names.size());
    assertTrue(names.contains("alice"));
    assertTrue(names.contains("bob"));
    assertTrue(names.contains("charlie"));
    assertFalse(names.contains("ALICE"));
    Files.delete(maleFile);
    Files.delete(femaleFile);
    Files.delete(additionalFile);
  }

  @Test
  public void loadGenderNamesAddsToExistingSetAndEliminatesDuplicates() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Path additionalFile = Files.createTempFile("gender-extra", ".txt");
    Files.write(maleFile, Collections.singletonList("placeholder"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("placeholder"), StandardCharsets.UTF_8);
    Files.write(additionalFile, Arrays.asList("SAM,sam", "TAYLOR"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    HashSet<String> names = new HashSet<>(Collections.singletonList("existing"));

    annotator.loadGenderNames(names, additionalFile.toString());

    assertEquals(3, names.size());
    assertTrue(names.contains("existing"));
    assertTrue(names.contains("sam"));
    assertTrue(names.contains("taylor"));
    Files.delete(maleFile);
    Files.delete(femaleFile);
    Files.delete(additionalFile);
  }

  @Test
  public void annotateEntityMentionSetsMentionAndEveryToken() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("placeholder"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("placeholder"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreLabel first = CoreLabel.wordFromString("Alex");
    CoreLabel second = CoreLabel.wordFromString("Morgan");
    CoreLabel third = CoreLabel.wordFromString("Jr.");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second, third));

    annotator.annotateEntityMention(mention, "NONBINARY");

    assertEquals("NONBINARY", mention.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("NONBINARY", first.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("NONBINARY", second.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("NONBINARY", third.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateRecognizesMaleFirstNameIgnoringInputCase() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("JOHN"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("custom-name-is-accepted", properties);
    CoreLabel first = CoreLabel.wordFromString("jOhN");
    CoreLabel last = CoreLabel.wordFromString("Smith");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, last));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("John Smith arrived.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("MALE", first.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("MALE", last.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateRecognizesFemaleFirstNameAndOverwritesOldGender() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("MARY"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreLabel first = CoreLabel.wordFromString("Mary");
    CoreLabel last = CoreLabel.wordFromString("Jones");
    first.set(CoreAnnotations.GenderAnnotation.class, "OLD_TOKEN_VALUE");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.GenderAnnotation.class, "OLD_MENTION_VALUE");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, last));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("Mary Jones arrived.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("FEMALE", first.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("FEMALE", last.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateUsesOnlyFirstTokenOfMultiTokenPersonName() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreLabel title = CoreLabel.wordFromString("Dr.");
    CoreLabel knownName = CoreLabel.wordFromString("Mary");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(title, knownName));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("Dr. Mary");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(title.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(knownName.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateLeavesUnknownPersonAndItsExistingAnnotationsUnchanged() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreLabel first = CoreLabel.wordFromString("Xenon");
    CoreLabel last = CoreLabel.wordFromString("Doe");
    first.set(CoreAnnotations.GenderAnnotation.class, "TOKEN_SENTINEL");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.GenderAnnotation.class, "MENTION_SENTINEL");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, last));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("Xenon Doe");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertEquals("MENTION_SENTINEL", mention.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("TOKEN_SENTINEL", first.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(last.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateSkipsNonPersonEvenWhenNameIsKnown() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreLabel token = CoreLabel.wordFromString("John");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("John");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateGivesMalePrecedenceWhenNameAppearsInBothLists() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("alex"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("alex"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreLabel token = CoreLabel.wordFromString("Alex");
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("Alex");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateProcessesMultipleSentencesAndMixedMentions() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreLabel john = CoreLabel.wordFromString("John");
    CoreLabel company = CoreLabel.wordFromString("Mary");
    CoreLabel mary = CoreLabel.wordFromString("Mary");
    CoreLabel unknown = CoreLabel.wordFromString("Unknown");
    CoreMap johnMention = new ArrayCoreMap();
    johnMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    johnMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(john));
    CoreMap companyMention = new ArrayCoreMap();
    companyMention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
    companyMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(company));
    CoreMap maryMention = new ArrayCoreMap();
    maryMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    maryMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(mary));
    CoreMap unknownMention = new ArrayCoreMap();
    unknownMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    unknownMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(unknown));
    CoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(johnMention, companyMention));
    CoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(maryMention, unknownMention));
    Annotation document = new Annotation("John met Mary.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(firstSentence, secondSentence));

    annotator.annotate(document);

    assertEquals("MALE", johnMention.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(companyMention.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("FEMALE", maryMention.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(unknownMention.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("MALE", john.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(company.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals("FEMALE", mary.get(CoreAnnotations.GenderAnnotation.class));
    assertNull(unknown.get(CoreAnnotations.GenderAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test
  public void annotateAcceptsDocumentWithNoSentencesToVisit() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    Annotation document = new Annotation("empty structure");
    List<CoreMap> noSentences = Collections.emptyList();
    document.set(CoreAnnotations.SentencesAnnotation.class, noSentences);

    annotator.annotate(document);

    assertSame(noSentences, document.get(CoreAnnotations.SentencesAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test(expected = NullPointerException.class)
  public void annotateFailsWhenSentencesAnnotationIsMissing() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    Annotation document = new Annotation("no sentence annotation");

    annotator.annotate(document);
  }

  @Test(expected = NullPointerException.class)
  public void annotateFailsWhenPersonEntityTypeIsMissing() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokensAnnotation.class,
        Collections.singletonList(CoreLabel.wordFromString("John")));
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("John");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void annotateFailsForPersonMentionWithNoTokens() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    Annotation document = new Annotation("empty mention");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);
  }

  @Test
  public void requiresDeclaresExactlyTheFiveInputAnnotations() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertEquals(5, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.EntityTypeAnnotation.class));
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void requiresReturnsUnmodifiableSet() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);

    annotator.requires().add(CoreAnnotations.GenderAnnotation.class);
  }

  @Test
  public void requirementsSatisfiedContainsOnlyGenderAndIsUnmodifiable() throws Exception {
    Path maleFile = Files.createTempFile("gender-male", ".txt");
    Path femaleFile = Files.createTempFile("gender-female", ".txt");
    Files.write(maleFile, Collections.singletonList("john"), StandardCharsets.UTF_8);
    Files.write(femaleFile, Collections.singletonList("mary"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("gender.maleNamesFile", maleFile.toString());
    properties.setProperty("gender.femaleNamesFile", femaleFile.toString());
    GenderAnnotator annotator = new GenderAnnotator("gender", properties);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(Collections.singleton(CoreAnnotations.GenderAnnotation.class), satisfied);
    boolean rejectedMutation = false;
    try {
      satisfied.clear();
    } catch (UnsupportedOperationException expected) {
      rejectedMutation = true;
    }
    assertTrue(rejectedMutation);
    Files.delete(maleFile);
    Files.delete(femaleFile);
  }
}
