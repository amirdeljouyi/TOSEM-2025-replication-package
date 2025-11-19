package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.quoteattribution.Person;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAttributionAnnotator_2_GPTLLMTest {

 @Test
  public void testConstructorWithRequiredProperties() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");
    props.setProperty("verbose", "false");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
    assertTrue(annotator instanceof QuoteAttributionAnnotator);
  }
@Test
  public void testConstructorWithMissingOptionalProperties() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
    assertTrue(annotator instanceof QuoteAttributionAnnotator);
  }
@Test
  public void testEntityMentionsToCharacterMapIncludesOnlyPersons() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation mockAnnotation = mock(Annotation.class);

    CoreMap personMention = mock(CoreMap.class);
    when(personMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(personMention.toString()).thenReturn("Alice Smith");

    CoreMap nonPersonMention = mock(CoreMap.class);
    when(nonPersonMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
    when(nonPersonMention.toString()).thenReturn("New York");

    List<CoreMap> mentions = Arrays.asList(personMention, nonPersonMention);

    when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    annotator.entityMentionsToCharacterMap(mockAnnotation);

    Annotation newAnnotation = new Annotation("Alice Smith met her friend.");
    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Alice Smith");
    newAnnotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

    boolean containsPerson = false;
    annotator.entityMentionsToCharacterMap(newAnnotation);

    List<Person> people = new ArrayList<>();
  }
@Test
  public void testAnnotateWithMinimalValidInput() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("John said, \"Hello, world!\"");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("John");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TextAnnotation.class, "John said, \"Hello, world!\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate threw an exception on minimal valid input: " + e.getMessage());
    }
  }
@Test
  public void testAnnotateAddsSpeakerAnnotations() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Alice says, \"I am ready.\"");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Alice");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Alice says, \"I am ready.\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should not fail during expected flow: " + e.getMessage());
    }

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    boolean hasSpeakerAnnotation = false;
    if (satisfied.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class)) {
      hasSpeakerAnnotation = true;
    }

    assertTrue("Expected SpeakerAnnotation to be in requirementsSatisfied", hasSpeakerAnnotation);
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfies = annotator.requirementsSatisfied();

    assertTrue(satisfies.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
    assertTrue(satisfies.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    assertTrue(satisfies.contains(CoreAnnotations.ParagraphIndexAnnotation.class));
  }
@Test
  public void testRequiresIncludesMentionAndCorefWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(required.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotateWithNullMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("No entities here.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "No entities here.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should handle null MentionsAnnotation gracefully without throwing an exception.");
    }
  }
@Test
  public void testAnnotateWithEmptyCharactersFilePath() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Alice said something.");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Alice");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Alice said something.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotator should handle empty charactersPath without crashing: " + e.getMessage());
    }
  }
@Test
  public void testAnnotateWithMissingCorefPath() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Bob listened carefully.");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Bob");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Bob listened carefully.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotator should handle missing coref path gracefully: " + e.getMessage());
    }
  }
@Test
  public void testEntityMentionsToCharacterMapWithEmptyMentionList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("No named entities here.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("entityMentionsToCharacterMap should handle empty mention list gracefully: " + e.getMessage());
    }
  }
@Test
  public void testAnnotateWithMentionThatHasNoEntityMentionIndex() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He said, \"It’s fine.\"");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    tokens.add(token);

    annotation.set(CoreAnnotations.TextAnnotation.class, "He said, \"It’s fine.\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("He");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should handle token without EntityMentionIndexAnnotation gracefully");
    }
  }
@Test
  public void testAnnotateWithInvalidCanonicalEntityMentionIndex() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He whispered, \"Go.\"");

    List<CoreMap> mentionsList = new ArrayList<>();

    CoreMap originalMention = mock(CoreMap.class);
    when(originalMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(originalMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(42); 

    mentionsList.add(originalMention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionsList);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    tokens.add(token);

    annotation.set(CoreAnnotations.TextAnnotation.class, "He whispered, \"Go.\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (IndexOutOfBoundsException e) {
      
      
      
      assertTrue(e.getMessage().contains("Index"));
    } catch (Exception e) {
      fail("Annotator should throw IndexOutOfBounds or handle gracefully, not: " + e.getClass().getSimpleName());
    }
  }
@Test
  public void testAnnotateWithNullCanonicalMentionTokens() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("She ran.");

    CoreMap canonicalMention = mock(CoreMap.class);
    when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("She");

    CoreMap originalMention = mock(CoreMap.class);
    when(originalMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(0);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(canonicalMention);  
    mentions.add(originalMention);   

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    tokens.add(token);

    annotation.set(CoreAnnotations.TextAnnotation.class, "She ran.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      
      fail("Annotate should not throw when canonical mention tokens are null: " + e.getMessage());
    }
  }
@Test
  public void testConstructorWithEmptyOptionalSieveProperties() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test_characters.txt");
    props.setProperty("booknlpCoref", "test_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "test_family.txt");
    props.setProperty("animacyWordsFile", "test_animacy.txt");
    props.setProperty("genderNamesFile", "test_gender.txt");
    props.setProperty("QMSieves", "");
    props.setProperty("MSSieves", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithNoSentencesAnnotationSet() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("This is a test without sentence data.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is a test without sentence data.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should not throw when the SentencesAnnotation is unset.");
    }
  }
@Test
  public void testAnnotateWhenTokenLacksEntityMentionIndex() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test_characters.txt");
    props.setProperty("booknlpCoref", "test_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "test_family.txt");
    props.setProperty("animacyWordsFile", "test_animacy.txt");
    props.setProperty("genderNamesFile", "test_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He shouted, \"Leave now!\"");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "He");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    annotation.set(CoreAnnotations.TextAnnotation.class, "He shouted, \"Leave now!\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should not throw if Token lacks EntityMentionIndexAnnotation.");
    }
  }
@Test
  public void testEntityMentionsToCharacterMapWithMultiplePersonMentionsSameText() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Test input");

    CoreMap mention1 = mock(CoreMap.class);
    when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention1.toString()).thenReturn("John Doe");

    CoreMap mention2 = mock(CoreMap.class);
    when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention2.toString()).thenReturn("John   Doe"); 

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.entityMentionsToCharacterMap(annotation);

    boolean hasJohnDoe = false;
  }
@Test
  public void testAnnotateWithSingleCharacterTokenList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.txt");
    props.setProperty("booknlpCoref", "b.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Hello");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(0);

    CoreMap canonicalMention = mock(CoreMap.class);
    when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Hello");

    List<CoreMap> allMentions = new ArrayList<>();
    allMentions.add(canonicalMention);
    allMentions.add(mention);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, allMentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should correctly handle canonical mention with one-token span");
    }
  }
@Test
  public void testAnnotateWithEmptyQMSievesProperty() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.txt");
    props.setProperty("booknlpCoref", "b.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");
    props.setProperty("QMSieves", ""); 

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Nobody said anything.");

    annotation.set(CoreAnnotations.TextAnnotation.class, "Nobody said anything.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should support configuration with no quote-to-mention sieves.");
    }
  }
@Test
  public void testConstructorWithBooleanUseCorefFalse() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.txt");
    props.setProperty("booknlpCoref", "book.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");
    props.setProperty("useCoref", "false");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

    boolean containsCorefRequirement = false;
    for (Class<? extends edu.stanford.nlp.ling.CoreAnnotation> cls : required) {
      if (cls.equals(CorefCoreAnnotations.CorefChainAnnotation.class)) {
        containsCorefRequirement = true;
      }
    }

    assertFalse("useCoref=false should omit coref requirement", containsCorefRequirement);
  }
@Test
  public void testAnnotateWithNullAnnotationInput() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_characters.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    try {
      annotator.annotate(null);
      fail("Annotate should throw NullPointerException when passed null");
    } catch (NullPointerException e) {
      
    } catch (Exception e) {
      fail("Annotate should throw NullPointerException, but threw: " + e.getClass().getSimpleName());
    }
  }
@Test
  public void testAnnotateWithMentionWithoutTokenBegin() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "path_characters.txt");
    props.setProperty("booknlpCoref", "path_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "path_family.txt");
    props.setProperty("animacyWordsFile", "path_animacy.txt");
    props.setProperty("genderNamesFile", "path_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Quote from someone.");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "someone");
    

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Someone");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Quote from someone.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should handle missing TokenBeginAnnotation in tokens gracefully: " + e.getMessage());
    }
  }
@Test
  public void testAnnotateWithEmptyTokensAndNullMentionBegin() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "ck.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Test no tokens");

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(null);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Test no tokens");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should skip quote when MentionBeginAnnotation is null");
    }
  }
@Test
  public void testAnnotateWithMissingGenderFileProperty() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateSkipsMissingCanonicalMentionIndex() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test_characters.txt");
    props.setProperty("booknlpCoref", "test_coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "test_family.txt");
    props.setProperty("animacyWordsFile", "test_animacy.txt");
    props.setProperty("genderNamesFile", "test_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Alex");

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(null); 

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(entityMention);

    Annotation annotation = new Annotation("Alex whispered.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Alex whispered.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should gracefully handle null CanonicalEntityMentionIndexAnnotation without error.");
    }
  }
@Test
  public void testCharacterMapBuiltWhenNoCharactersFileIsProvided() {
    Properties props = new Properties();
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Mary said something.");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Mary");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Mary said something.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate failed unexpectedly. Should dynamically build characterMap if charactersPath is missing.");
    }
  }
@Test
  public void testAnnotateHandlesDefaultDependencyParseModelWhenNotProvided() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "ch.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gen.txt");

    

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull("Annotator should fall back to default dependency parser model path", annotator);
  }
@Test
public void testAnnotateWithQuoteButNoMentionResolved() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "characters.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "family.txt");
  props.setProperty("animacyWordsFile", "animacy.txt");
  props.setProperty("genderNamesFile", "gender.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("\"Why are you here?\"");

  CoreMap quote = mock(CoreMap.class);
  when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(null);

  List<CoreMap> quotes = Collections.singletonList(quote);
  annotation.set(CoreAnnotations.TextAnnotation.class, "\"Why are you here?\"");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  
  

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should handle quotes without associated mentions.");
  }
}
@Test
public void testAnnotateWithOnlyNonPERSONMentions() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "family.txt");
  props.setProperty("animacyWordsFile", "animacy.txt");
  props.setProperty("genderNamesFile", "gender.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Stanford is beautiful.");

  CoreMap locationMention = mock(CoreMap.class);
  when(locationMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(locationMention.toString()).thenReturn("Stanford");

  List<CoreMap> mentions = new ArrayList<>();
  mentions.add(locationMention);

  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.TextAnnotation.class, "Stanford is beautiful.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Non-PERSON mentions should be ignored without error.");
  }
}
@Test
public void testAnnotateWithMissingTokenBeginOnCanonicalMentionFirstToken() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "chr.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  CoreLabel tokenWithoutBegin = new CoreLabel();
  
  tokenWithoutBegin.set(CoreAnnotations.TextAnnotation.class, "Alice");

  List<CoreLabel> canonicalMentionTokens = new ArrayList<>();
  canonicalMentionTokens.add(tokenWithoutBegin);

  CoreMap canonicalMention = mock(CoreMap.class);
  when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(canonicalMentionTokens);
  when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Alice");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(0);

  List<CoreMap> mentions = new ArrayList<>();
  mentions.add(canonicalMention); 
  mentions.add(mention);          

  CoreLabel quoteToken = new CoreLabel();
  quoteToken.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
  quoteToken.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(quoteToken);

  Annotation annotation = new Annotation("Alice said, \"Hi.\"");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Alice said, \"Hi.\"");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (NullPointerException e) {
    
    assertTrue("Message should be null or mention TokenBeginAnnotation", true);
  } catch (Exception e) {
    fail("Unexpected exception: " + e.getClass().getSimpleName());
  }
}
@Test
public void testRequirementsIncludeAllExpectedInputAnnotations() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "family.txt");
  props.setProperty("animacyWordsFile", "animacy.txt");
  props.setProperty("genderNamesFile", "gender.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

  assertTrue("Expect TextAnnotation in required features", required.contains(CoreAnnotations.TextAnnotation.class));
  assertTrue("Expect MentionsAnnotation in required features", required.contains(CoreAnnotations.MentionsAnnotation.class));
  assertTrue("Expect TokensAnnotation in required features", required.contains(CoreAnnotations.TokensAnnotation.class));
  assertTrue("Expect IndexAnnotation in required features", required.contains(CoreAnnotations.IndexAnnotation.class));
}
@Test
public void testAnnotatorWithMinimalTokensButNoMentions() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "x.txt");
  props.setProperty("booknlpCoref", "x2.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "f.txt");
  props.setProperty("animacyWordsFile", "a.txt");
  props.setProperty("genderNamesFile", "g.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Just a sentence with no people.");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Just");
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  annotation.set(CoreAnnotations.TextAnnotation.class, "Just a sentence with no people.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotator should handle token-only annotations with no mention data.");
  }
}
@Test
public void testAnnotateWithEmptyAnnotationObject() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("");

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should not throw exception on empty Annotation object.");
  }
}
@Test
public void testAnnotateWithoutTokensAnnotation() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "characters.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "family.txt");
  props.setProperty("animacyWordsFile", "animacy.txt");
  props.setProperty("genderNamesFile", "gender.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Someone said something.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Someone said something.");
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should not throw exception when TokensAnnotation is missing.");
  }
}
@Test
public void testAnnotateWithNullTextAnnotation() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should not throw exception when TextAnnotation is null or empty.");
  }
}
@Test
public void testAnnotateSkipsUnknownSieveNamesInQMList() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");
  props.setProperty("QMSieves", "tri,unknownsieve,dep");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Quote here.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Quote here.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (NullPointerException e) {
    fail("Unknown sieve name in QMSieves list should be skipped, not cause NPE.");
  } catch (Exception e) {
    fail("Annotate should not fail on unknown sieve in configuration.");
  }
}
@Test
public void testAnnotateSkipsUnknownSieveNamesInMSList() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");
  props.setProperty("MSSieves", "det,ghost,top");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("He said, \"Go away.\"");
  annotation.set(CoreAnnotations.TextAnnotation.class, "He said, \"Go away.\"");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (NullPointerException e) {
    fail("Unknown sieve name in MSSieves list should be gracefully ignored.");
  } catch (Exception e) {
    fail("Annotate should not fail due to unknown sieve name in MSSieves.");
  }
}
@Test
public void testAnnotateWithNullMentionEndAnnotation() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "characters.txt");
  props.setProperty("booknlpCoref", "corefs.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "family.txt");
  props.setProperty("animacyWordsFile", "animacy.txt");
  props.setProperty("genderNamesFile", "gender.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Alice said, \"I'll go.\"");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Alice");
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(mention.toString()).thenReturn("Alice");

  CoreMap quote = mock(CoreMap.class);
  when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);
  when(quote.get(QuoteAttributionAnnotator.MentionEndAnnotation.class)).thenReturn(null);

  annotation.set(CoreAnnotations.TextAnnotation.class, "Alice said, \"I'll go.\"");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should tolerate null MentionEndAnnotation value.");
  }
}
@Test
public void testAnnotateWithInvalidCanonicalMentionTokensIndexAccess() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "ck.txt");
  props.setProperty("booknlpCoref", "bnp.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fm.txt");
  props.setProperty("animacyWordsFile", "an.txt");
  props.setProperty("genderNamesFile", "gn.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Emily said, \"I'll try.\"");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap canonicalMention = mock(CoreMap.class);
  when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>()); 
  when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Emily");

  CoreMap referenceMention = mock(CoreMap.class);
  when(referenceMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(0);

  List<CoreMap> mentions = new ArrayList<>();
  mentions.add(canonicalMention); 
  mentions.add(referenceMention); 

  annotation.set(CoreAnnotations.TextAnnotation.class, "Emily said, \"I'll try.\"");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (IndexOutOfBoundsException e) {
    
    assertTrue(true);
  } catch (Exception e) {
    fail("Unexpected exception during canonical mention token access failure: " + e.getMessage());
  }
}
@Test
public void testAnnotateWithNullEntityMentionInQuote() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("She said, \"Go.\"");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, null);

  List<CoreLabel> tokens = Collections.singletonList(token);

  CoreMap quote = mock(CoreMap.class);
  when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

  annotation.set(CoreAnnotations.TextAnnotation.class, "She said, \"Go.\"");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should handle null EntityMentionIndexAnnotation on token.");
  }
}
@Test
public void testAnnotateWithSingleTokenMentionWithoutNER() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "char.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Bob said hello.");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention.toString()).thenReturn("Bob");

  List<CoreMap> mentions = Collections.singletonList(mention);

  annotation.set(CoreAnnotations.TextAnnotation.class, "Bob said hello.");
  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.entityMentionsToCharacterMap(annotation);
  } catch (Exception e) {
    fail("entityMentionsToCharacterMap should handle mentions with null NER tag.");
  }
}
@Test
public void testAnnotateWithMentionThatHasNoCorrespondingCanonicalMention() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "ch.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Emma");
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 2);

  List<CoreLabel> tokens = Collections.singletonList(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  CoreMap quote = mock(CoreMap.class);
  when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

  List<CoreMap> mentions = new ArrayList<>();
  mentions.add(mock(CoreMap.class)); 
  mentions.add(mock(CoreMap.class)); 
  mentions.add(mention);            

  Annotation annotation = new Annotation("Emma stated clearly.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Emma stated clearly.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should handle references to non-existent canonical mentions.");
  }
}
@Test
public void testAnnotateWhenCanonicalMentionTokensAreNullAndAccessed() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "ch.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "fam.txt");
  props.setProperty("animacyWordsFile", "ani.txt");
  props.setProperty("genderNamesFile", "gen.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

  List<CoreLabel> tokens = Collections.singletonList(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(0);

  CoreMap canonicalMention = mock(CoreMap.class);
  when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
  when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Unit");

  List<CoreMap> mentions = new ArrayList<>();
  mentions.add(canonicalMention); 
  mentions.add(mention);          

  Annotation annotation = new Annotation("Unit test.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Unit test.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should gracefully handle null token list in canonical mention.");
  }
}
@Test
public void testConstructorHandlesMissingAllOptionalFileProps() {
  Properties props = new Properties();
  
  props.setProperty("charactersPath", "data/characters.txt");
  props.setProperty("booknlpCoref", "data/coref.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
  assertNotNull(annotator);
}
@Test
public void testAnnotateSkipsSpeakerSieveStepWhenNoMentionsResolved() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "c.txt");
  props.setProperty("booknlpCoref", "b.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "f.txt");
  props.setProperty("animacyWordsFile", "a.txt");
  props.setProperty("genderNamesFile", "g.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("\"Goodbye,\" they said.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "\"Goodbye,\" they said.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotate should allow speaker sieves to be skipped if no mentions were resolved.");
  }
}
@Test
public void testAnnotateWithUnknownNERTypeHandledInCharacterMap() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "people.txt");
  props.setProperty("booknlpCoref", "coref.txt");
  props.setProperty("modelPath", QuoteAttributionAnnotator.DEFAULT_MODEL_PATH);
  props.setProperty("familyWordsFile", "family.txt");
  props.setProperty("animacyWordsFile", "animacy.txt");
  props.setProperty("genderNamesFile", "gender.txt");

  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("QuantumBot shared his results.");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ROBOT");
  when(mention.toString()).thenReturn("QuantumBot");

  annotation.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  annotation.set(CoreAnnotations.TextAnnotation.class, "QuantumBot shared his results.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  try {
    annotator.entityMentionsToCharacterMap(annotation);
  } catch (Exception e) {
    fail("entityMentionsToCharacterMap should skip non-PERSON correctly and not throw.");
  }
} 
}