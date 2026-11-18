package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QuoteAttributionAnnotatorPromptTest {

  @Test
  public void mentionAnnotationHasStringValueType() {
    assertEquals(String.class, new QuoteAttributionAnnotator.MentionAnnotation().getType());
  }

  @Test
  public void mentionBeginAnnotationHasIntegerValueType() {
    assertEquals(Integer.class, new QuoteAttributionAnnotator.MentionBeginAnnotation().getType());
  }

  @Test
  public void mentionEndAnnotationHasIntegerValueType() {
    assertEquals(Integer.class, new QuoteAttributionAnnotator.MentionEndAnnotation().getType());
  }

  @Test
  public void mentionTypeAnnotationHasStringValueType() {
    assertEquals(String.class, new QuoteAttributionAnnotator.MentionTypeAnnotation().getType());
  }

  @Test
  public void mentionSieveAnnotationHasStringValueType() {
    assertEquals(String.class, new QuoteAttributionAnnotator.MentionSieveAnnotation().getType());
  }

  @Test
  public void speakerAnnotationHasStringValueType() {
    assertEquals(String.class, new QuoteAttributionAnnotator.SpeakerAnnotation().getType());
  }

  @Test
  public void speakerSieveAnnotationHasStringValueType() {
    assertEquals(String.class, new QuoteAttributionAnnotator.SpeakerSieveAnnotation().getType());
  }

  @Test
  public void canonicalMentionAnnotationHasStringValueType() {
    assertEquals(String.class, new QuoteAttributionAnnotator.CanonicalMentionAnnotation().getType());
  }

  @Test
  public void canonicalMentionBeginAnnotationHasIntegerValueType() {
    assertEquals(Integer.class,
        new QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation().getType());
  }

  @Test
  public void canonicalMentionEndAnnotationHasIntegerValueType() {
    assertEquals(Integer.class,
        new QuoteAttributionAnnotator.CanonicalMentionEndAnnotation().getType());
  }

  @Test
  public void annotationKeysCanRoundTripValuesThroughCoreMap() {
    CoreMap quote = new ArrayCoreMap();
    quote.set(QuoteAttributionAnnotator.MentionAnnotation.class, "Alice");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 3);
    quote.set(QuoteAttributionAnnotator.MentionEndAnnotation.class, 4);
    quote.set(QuoteAttributionAnnotator.MentionTypeAnnotation.class, "name");
    quote.set(QuoteAttributionAnnotator.MentionSieveAnnotation.class, "tri");
    quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "Alice Smith");
    quote.set(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class, "det");
    quote.set(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class, "Alice Smith");
    quote.set(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class, 0);
    quote.set(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class, 2);

    assertEquals("Alice", quote.get(QuoteAttributionAnnotator.MentionAnnotation.class));
    assertEquals(Integer.valueOf(3),
        quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class));
    assertEquals(Integer.valueOf(4),
        quote.get(QuoteAttributionAnnotator.MentionEndAnnotation.class));
    assertEquals("name", quote.get(QuoteAttributionAnnotator.MentionTypeAnnotation.class));
    assertEquals("tri", quote.get(QuoteAttributionAnnotator.MentionSieveAnnotation.class));
    assertEquals("Alice Smith", quote.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    assertEquals("det", quote.get(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class));
    assertEquals("Alice Smith",
        quote.get(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    assertEquals(Integer.valueOf(0),
        quote.get(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class));
    assertEquals(Integer.valueOf(2),
        quote.get(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class));
  }

  @Test
  public void requirementsSatisfiedContainsEveryDocumentAnnotationProduced() throws Exception {
    Class<?> factoryClass = Class.forName("sun.reflect.ReflectionFactory");
    Object factory = factoryClass.getMethod("getReflectionFactory").invoke(null);
    Constructor<Object> objectConstructor = Object.class.getDeclaredConstructor();
    Constructor<?> serializationConstructor = (Constructor<?>) factoryClass.getMethod(
        "newConstructorForSerialization", Class.class, Constructor.class).invoke(
            factory, QuoteAttributionAnnotator.class, objectConstructor);
    QuoteAttributionAnnotator annotator =
        (QuoteAttributionAnnotator) serializationConstructor.newInstance();

    Set<Class<? extends CoreAnnotation>> actual = annotator.requirementsSatisfied();

    assertEquals(11, actual.size());
    assertTrue(actual.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.MentionBeginAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.MentionEndAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.MentionTypeAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.MentionSieveAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class));
    assertTrue(actual.contains(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.ParagraphIndexAnnotation.class));
  }

  @Test
  public void requirementsSatisfiedReturnsIndependentMutableSets() throws Exception {
    Class<?> factoryClass = Class.forName("sun.reflect.ReflectionFactory");
    Object factory = factoryClass.getMethod("getReflectionFactory").invoke(null);
    Constructor<Object> objectConstructor = Object.class.getDeclaredConstructor();
    Constructor<?> serializationConstructor = (Constructor<?>) factoryClass.getMethod(
        "newConstructorForSerialization", Class.class, Constructor.class).invoke(
            factory, QuoteAttributionAnnotator.class, objectConstructor);
    QuoteAttributionAnnotator annotator =
        (QuoteAttributionAnnotator) serializationConstructor.newInstance();

    Set<Class<? extends CoreAnnotation>> first = annotator.requirementsSatisfied();
    Set<Class<? extends CoreAnnotation>> second = annotator.requirementsSatisfied();
    first.clear();

    assertNotSame(first, second);
    assertTrue(first.isEmpty());
    assertEquals(11, second.size());
  }

  @Test
  public void requiresWithoutCoreferenceContainsAllBaseInputs() throws Exception {
    Class<?> factoryClass = Class.forName("sun.reflect.ReflectionFactory");
    Object factory = factoryClass.getMethod("getReflectionFactory").invoke(null);
    Constructor<Object> objectConstructor = Object.class.getDeclaredConstructor();
    Constructor<?> serializationConstructor = (Constructor<?>) factoryClass.getMethod(
        "newConstructorForSerialization", Class.class, Constructor.class).invoke(
            factory, QuoteAttributionAnnotator.class, objectConstructor);
    QuoteAttributionAnnotator annotator =
        (QuoteAttributionAnnotator) serializationConstructor.newInstance();

    Set<Class<? extends CoreAnnotation>> actual = annotator.requires();

    assertEquals(15, actual.size());
    assertTrue(actual.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.LemmaAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.BeforeAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.AfterAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.TokenBeginAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.TokenEndAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.IndexAnnotation.class));
    assertTrue(actual.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertFalse(actual.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
  }

  @Test
  public void entityConversionAcceptsPersonAndNonPersonMentionsAcrossCalls() throws Exception {
    Class<?> factoryClass = Class.forName("sun.reflect.ReflectionFactory");
    Object factory = factoryClass.getMethod("getReflectionFactory").invoke(null);
    Constructor<Object> objectConstructor = Object.class.getDeclaredConstructor();
    Constructor<?> serializationConstructor = (Constructor<?>) factoryClass.getMethod(
        "newConstructorForSerialization", Class.class, Constructor.class).invoke(
            factory, QuoteAttributionAnnotator.class, objectConstructor);
    QuoteAttributionAnnotator annotator =
        (QuoteAttributionAnnotator) serializationConstructor.newInstance();
    Annotation firstDocument = new Annotation("Alice Smith met Acme.");
    CoreMap alice = new Annotation("Alice   Smith");
    alice.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    CoreMap acme = new Annotation("Acme");
    acme.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    firstDocument.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(alice, acme));
    Annotation secondDocument = new Annotation("Bob arrived.");
    CoreMap bob = new Annotation("Bob");
    bob.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    secondDocument.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(bob));

    annotator.entityMentionsToCharacterMap(firstDocument);
    annotator.entityMentionsToCharacterMap(secondDocument);

    assertEquals("Alice   Smith", alice.toString());
    assertEquals("Acme", acme.toString());
    assertEquals("Bob", bob.toString());
  }

  @Test(expected = NullPointerException.class)
  public void entityConversionRejectsDocumentWithoutMentionList() throws Exception {
    Class<?> factoryClass = Class.forName("sun.reflect.ReflectionFactory");
    Object factory = factoryClass.getMethod("getReflectionFactory").invoke(null);
    Constructor<Object> objectConstructor = Object.class.getDeclaredConstructor();
    Constructor<?> serializationConstructor = (Constructor<?>) factoryClass.getMethod(
        "newConstructorForSerialization", Class.class, Constructor.class).invoke(
            factory, QuoteAttributionAnnotator.class, objectConstructor);
    QuoteAttributionAnnotator annotator =
        (QuoteAttributionAnnotator) serializationConstructor.newInstance();
    Annotation document = new Annotation("No entity annotations");

    annotator.entityMentionsToCharacterMap(document);
  }

  @Test(expected = NullPointerException.class)
  public void entityConversionRejectsMentionWithoutNerTag() throws Exception {
    Class<?> factoryClass = Class.forName("sun.reflect.ReflectionFactory");
    Object factory = factoryClass.getMethod("getReflectionFactory").invoke(null);
    Constructor<Object> objectConstructor = Object.class.getDeclaredConstructor();
    Constructor<?> serializationConstructor = (Constructor<?>) factoryClass.getMethod(
        "newConstructorForSerialization", Class.class, Constructor.class).invoke(
            factory, QuoteAttributionAnnotator.class, objectConstructor);
    QuoteAttributionAnnotator annotator =
        (QuoteAttributionAnnotator) serializationConstructor.newInstance();
    Annotation document = new Annotation("Alice");
    CoreMap mention = new Annotation("Alice");
    document.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

    annotator.entityMentionsToCharacterMap(document);
  }

  @Test(expected = NullPointerException.class)
  public void constructorRejectsNullProperties() {
    new QuoteAttributionAnnotator(null);
  }

  @Test
  public void constructorReportsMissingFamilyWordsResource() {
    Properties properties = new Properties();
    properties.setProperty("familyWordsFile", "missing-family-words-file-for-test.txt");
    properties.setProperty("genderNamesFile", "missing-gender-file-for-test.txt");
    properties.setProperty("animacyWordsFile", "missing-animacy-file-for-test.txt");

    try {
      new QuoteAttributionAnnotator(properties);
      fail("Expected the missing family-word resource to fail construction");
    } catch (NullPointerException expected) {
      assertTrue(expected.getMessage() == null || expected.getMessage().contains("lines"));
    }
  }

  @Test
  public void constructorRejectsMalformedGenderDataAfterReadingFamilyData() throws Exception {
    Path family = Files.createTempFile("quote-family", ".txt");
    Path gender = Files.createTempFile("quote-gender", ".txt");
    Path animacy = Files.createTempFile("quote-animacy", ".txt");
    Files.write(family, Arrays.asList("mother", " FATHER ", ""), StandardCharsets.UTF_8);
    Files.write(gender, Collections.singletonList("malformed"), StandardCharsets.UTF_8);
    Files.write(animacy, Collections.singletonList("person"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("familyWordsFile", family.toString());
    properties.setProperty("genderNamesFile", gender.toString());
    properties.setProperty("animacyWordsFile", animacy.toString());

    try {
      new QuoteAttributionAnnotator(properties);
      fail("Expected malformed gender data to fail construction");
    } catch (ArrayIndexOutOfBoundsException expected) {
      assertTrue(expected.getMessage() == null || !expected.getMessage().isEmpty());
    }
  }

  @Test
  public void constructorRejectsBlankAnimacyEntryAfterReadingValidGenderData() throws Exception {
    Path family = Files.createTempFile("quote-family", ".txt");
    Path gender = Files.createTempFile("quote-gender", ".txt");
    Path animacy = Files.createTempFile("quote-animacy", ".txt");
    Files.write(family, Collections.singletonList("sister"), StandardCharsets.UTF_8);
    Files.write(gender, Arrays.asList("king\t4 1", "queen\t1 5"), StandardCharsets.UTF_8);
    Files.write(animacy, Arrays.asList("person", ""), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("familyWordsFile", family.toString());
    properties.setProperty("genderNamesFile", gender.toString());
    properties.setProperty("animacyWordsFile", animacy.toString());

    try {
      new QuoteAttributionAnnotator(properties);
      fail("Expected a blank animacy entry to fail construction");
    } catch (StringIndexOutOfBoundsException expected) {
      assertTrue(expected.getMessage() == null || expected.getMessage().contains("0"));
    }
  }

  @Test
  public void constructorReachesDependencyModelLoadingWithValidLexicons() throws Exception {
    Path family = Files.createTempFile("quote-family", ".txt");
    Path gender = Files.createTempFile("quote-gender", ".txt");
    Path animacy = Files.createTempFile("quote-animacy", ".txt");
    Files.write(family, Arrays.asList("mother", "brother"), StandardCharsets.UTF_8);
    Files.write(gender, Arrays.asList("man\t3 1", "woman\t1 3"), StandardCharsets.UTF_8);
    Files.write(animacy, Arrays.asList("person", "teacher"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("familyWordsFile", family.toString());
    properties.setProperty("genderNamesFile", gender.toString());
    properties.setProperty("animacyWordsFile", animacy.toString());
    properties.setProperty("depparse.model", "missing-dependency-model-for-test.txt");

    try {
      new QuoteAttributionAnnotator(properties);
      fail("Expected the deliberately missing dependency model to fail construction");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage() == null
          || expected.getMessage().contains("missing-dependency-model-for-test.txt")
          || expected.getCause() != null);
    }
  }
}
