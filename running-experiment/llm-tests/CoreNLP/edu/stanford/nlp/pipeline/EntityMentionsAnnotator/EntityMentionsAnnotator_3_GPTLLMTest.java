package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
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

public class EntityMentionsAnnotator_3_GPTLLMTest {

 @Test
  public void testNamedEntityAnnotation_PERSON() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.setBeginPosition(7);
    token2.setEndPosition(12);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("Barack Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Barack Obama");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> docMentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(docMentions);
    assertEquals(1, docMentions.size());

    CoreMap mention = docMentions.get(0);
    String type = mention.get(CoreAnnotations.EntityTypeAnnotation.class);
    assertEquals("PERSON", type);

    List<CoreLabel> mentionTokens = mention.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, mentionTokens.size());
    assertEquals("Barack", mentionTokens.get(0).word());
    assertEquals("Obama", mentionTokens.get(1).word());
  }
@Test
  public void testDetermineEntityMentionConfidencesWithTagProbabilities() {
    CoreLabel token = new CoreLabel();
    Map<String, Double> probs = new HashMap<>();
    probs.put("PERSON", 0.85);
    probs.put("ORGANIZATION", 0.63);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("PERSON", 0.76);
    probs2.put("ORGANIZATION", 0.80);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    tokens.add(token2);

    CoreMap mention = new Annotation("");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(0.76, result.get("PERSON"), 0.0001);
    assertEquals(0.63, result.get("ORGANIZATION"), 0.0001);
  }
@Test
  public void testAnnotatePronominalMentions_She() {
    CoreLabel pronoun = new CoreLabel();
    pronoun.setWord("She");
    pronoun.set(CoreAnnotations.TextAnnotation.class, "She");
    pronoun.setBeginPosition(0);
    pronoun.setEndPosition(3);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(pronoun);

    CoreMap sentence = new Annotation("She");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("She");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testOverlapsWithMentionReturnsTrue() {
    CoreLabel tokenA1 = new CoreLabel();
    tokenA1.setBeginPosition(0);
    tokenA1.setEndPosition(4);

    CoreLabel tokenA2 = new CoreLabel();
    tokenA2.setBeginPosition(5);
    tokenA2.setEndPosition(10);

    List<CoreLabel> tokensA = new ArrayList<>();
    tokensA.add(tokenA1);
    tokensA.add(tokenA2);

    CoreMap existingMention = new Annotation("");
    existingMention.set(CoreAnnotations.TokensAnnotation.class, tokensA);

    CoreLabel tokenB = new CoreLabel();
    tokenB.setBeginPosition(3);
    tokenB.setEndPosition(7);
    List<CoreLabel> tokensB = new ArrayList<>();
    tokensB.add(tokenB);

    CoreMap newMention = new Annotation("");
    newMention.set(CoreAnnotations.TokensAnnotation.class, tokensB);

    List<CoreMap> existingMentions = new ArrayList<>();
    existingMentions.add(existingMention);
  }
@Test
  public void testEntityMentionIndexIsSetCorrectly() {
    CoreLabel token = new CoreLabel();
    token.setWord("London");
    token.setNER("LOCATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "London");
    token.setBeginPosition(0);
    token.setEndPosition(6);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("London");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("London");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "London");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());

    CoreMap mention = mentions.get(0);
    Integer idx = mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class);
    assertNotNull(idx);
    assertEquals(Integer.valueOf(0), idx);
  }
@Test
  public void testMentionGetsWikipediaEntityIfSetOnToken() {
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Stanford_University");
    token.setBeginPosition(0);
    token.setEndPosition(8);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Stanford");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Stanford");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String wiki = mention.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("Stanford_University", wiki);
  }
@Test
  public void testSupportsCustomPropertyInstantiation() {
    Properties props = new Properties();
    props.setProperty("custom.nerCoreAnnotation", "edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation");
    props.setProperty("custom.acronyms", "true");
    props.setProperty("custom.language", "en");

    new EntityMentionsAnnotator("custom", props);
    
  }
@Test
  public void testDetermineEntityMentionConfidencesReturnsNullIfNoTokenProbs() {
    CoreLabel token = new CoreLabel();
    token.setWord("Unknown");
    token.setNER("O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(result);
  }
@Test
  public void testAnnotateWithEmptySentencesList() {
    Annotation annotation = new Annotation("Empty");

    List<CoreMap> sentences = new ArrayList<>();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(0, mentions.size());
  }
@Test
  public void testAnnotateWithNullTokenBeginAnnotation() {
    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.TextAnnotation.class, "Apple");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Apple");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Apple");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Apple");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
  }
@Test
  public void testAnnotateMentionWithoutNormalizedNERFallsBackToText() {
    CoreLabel token = new CoreLabel();
    token.setWord("Mars");
    token.setNER("LOCATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.setBeginPosition(0);
    token.setEndPosition(4);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Mars");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Mars");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Mars");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);

    String normalized = mention.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    assertEquals("Mars", mention.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOCATION", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
  }
@Test
  public void testAnnotateMentionWithTimexAnnotation() {
    Timex timex = new Timex("t1", "2023-01-01");

    CoreLabel token = new CoreLabel();
    token.setWord("Monday");
    token.setNER("DATE");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-01-01");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.setBeginPosition(0);
    token.setEndPosition(6);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Monday");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Monday");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Monday");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);

    Timex resultTimex = mention.get(TimeAnnotations.TimexAnnotation.class);
    assertNotNull(resultTimex);
    assertEquals("t1", resultTimex.tid());
  }
@Test
  public void testAnnotateDoesNotOverwriteExistingWikipediaAnnotation() {
    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    token.setNER("LOCATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Existing_Entity");
    token.set(CoreAnnotations.TextAnnotation.class, "Paris");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Paris");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Paris");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Paris");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String wiki = mention.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("Existing_Entity", wiki);
  }
@Test
  public void testAcronymDetectionIsCaseSensitive() {
    CoreLabel orgToken = new CoreLabel();
    orgToken.setWord("International Business Machines");
    orgToken.setNER("ORGANIZATION");
    orgToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    List<CoreLabel> orgTokens = new ArrayList<>();
    orgTokens.add(orgToken);

    CoreMap existingOrgMention = new Annotation("International Business Machines");
    existingOrgMention.set(CoreAnnotations.TokensAnnotation.class, orgTokens);
    existingOrgMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreLabel acronymToken = new CoreLabel();
    acronymToken.setWord("IBM");
    acronymToken.setNER("O");
    acronymToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> sentenceTokens = new ArrayList<>();
    sentenceTokens.add(acronymToken);

    CoreMap sentence = new Annotation("IBM");
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("IBM");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator acrAnnotator = new EntityMentionsAnnotator("config", new Properties() {{
      setProperty("config.acronyms", "true");
      setProperty("config.language", "en");
    }});

    
    acrAnnotator.annotate(annotation);

    List<CoreMap> resultMentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertTrue(resultMentions == null || resultMentions.size() == 0);
  }
@Test
  public void testCustomPropertyClassNotFoundLoggingError() {
    Properties props = new Properties();
    props.setProperty("bad.nerCoreAnnotation", "edu.nonexistent.FooAnnotation");

    
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("bad", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(6);

    CoreMap sentence = new Annotation("Google");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("Google");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Google");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testTokensForCharactersReturnsEmptyWhenNoOverlap() {
    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(10);
    token1.setEndPosition(20);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
  }
@Test
  public void testTokensForCharactersReturnsMatchingToken() {
    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
  }
@Test
  public void testAnnotateWithNoMentionsAnnotationInSentence() {
    CoreLabel token = new CoreLabel();
    token.setWord("Tesla");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Tesla");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Tesla");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Tesla");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Tesla");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testDetermineEntityMentionConfidencesWithMissingLabel() {
    CoreLabel token = new CoreLabel();
    Map<String, Double> tagProbs = new HashMap<>();
    tagProbs.put("PERSON", 0.7);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, tagProbs);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> tagProbs2 = new HashMap<>();
    tagProbs2.put("ORGANIZATION", 0.6); 
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, tagProbs2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    tokens.add(token2);

    CoreMap mention = new Annotation("Mixed");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(0.7, result.get("PERSON"), 0.0001);
    assertEquals(0.6, result.get("ORGANIZATION"), 0.0001);
  }
@Test
  public void testDetermineEntityMentionConfidencesIncompleteProbMap() {
    CoreLabel token = new CoreLabel(); 

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("Entity with no prob");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(result);
  }
@Test
  public void testMentionIndexPropagationToTokens() {
    CoreLabel token = new CoreLabel();
    token.setWord("Amazon");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Amazon");
    token.setBeginPosition(0);
    token.setEndPosition(6);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Amazon");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Amazon");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Amazon");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    Integer tokenIndex = tokens.get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class);
    assertNotNull(tokenIndex);
    assertEquals(Integer.valueOf(0), tokenIndex);
  }
@Test
  public void testPronominalMentionWithGenderHe() {
    CoreLabel token = new CoreLabel();
    token.setWord("He");
    token.set(CoreAnnotations.TextAnnotation.class, "He");
    token.setBeginPosition(0);
    token.setEndPosition(2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("He");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("He");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testOverlapsWithMentionNoOverlap() {
    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(0);
    token1.setEndPosition(3);

    CoreMap mention1 = new Annotation("First");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreLabel token2 = new CoreLabel();
    token2.setBeginPosition(4);
    token2.setEndPosition(6);

    CoreMap mention2 = new Annotation("Second");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  }
@Test
  public void testOverlapsWithMentionExactMatch() {
    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(10);
    token1.setEndPosition(15);

    CoreMap mention1 = new Annotation("EntityA");
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreLabel token2 = new CoreLabel();
    token2.setBeginPosition(10);
    token2.setEndPosition(15);

    CoreMap mention2 = new Annotation("EntityB");
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
  }
@Test
  public void testMultipleSentenceEntityMentionIndexing() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Alice");
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Alice");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    CoreMap sentence1 = new Annotation("Alice");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sentence1.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Bob");
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Bob");
    token2.setBeginPosition(6);
    token2.setEndPosition(9);

    CoreMap sentence2 = new Annotation("Bob");
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
    sentence2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    sentence2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence1);
    sentences.add(sentence2);

    Annotation document = new Annotation("Alice Bob");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "Alice Bob");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> allMentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, allMentions.size());

    CoreMap mention0 = allMentions.get(0);
    assertEquals(Integer.valueOf(0), mention0.get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals(Integer.valueOf(0), mention0.get(CoreAnnotations.SentenceIndexAnnotation.class));

    CoreMap mention1 = allMentions.get(1);
    assertEquals(Integer.valueOf(1), mention1.get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals(Integer.valueOf(1), mention1.get(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testMentionWithNoNERFallsBackToText() {
    CoreLabel token = new CoreLabel();
    token.setWord("UnknownEntity");
    token.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
    token.setBeginPosition(0);
    token.setEndPosition(13);

    CoreMap sentence = new Annotation("UnknownEntity");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("UnknownEntity");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String text = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("UnknownEntity", text);
  }
@Test
  public void testMentionWithCharacterOffsetsSetsTextSlice() {
    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.setBeginPosition(7);
    token.setEndPosition(13);  
    token.set(CoreAnnotations.TextAnnotation.class, "Google");

    CoreMap sentence = new Annotation("Google");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 1);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation document = new Annotation("Visit Google");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TextAnnotation.class, "Visit Google");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String mentionText = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Google", mentionText);
  }
@Test
  public void testEntityMentionWithoutWikipediaInfo() {
    CoreLabel token = new CoreLabel();
    token.setWord("Harvard");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(7);
    token.set(CoreAnnotations.TextAnnotation.class, "Harvard");
    

    CoreMap sentence = new Annotation("Harvard");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation doc = new Annotation("Harvard");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TextAnnotation.class, "Harvard");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String wiki = mention.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertNull(wiki);  
  }
@Test
  public void testDetermineEntityMentionConfidences_MissingProbOnSecondToken() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("ORGANIZATION", 0.7);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel token2 = new CoreLabel();  
    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("LOCATION", 0.9);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap mention = new Annotation("");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);

    assertTrue(result.containsKey("ORGANIZATION"));
    assertTrue(result.containsKey("LOCATION"));

    assertEquals(0.7, result.get("ORGANIZATION"), 0.0001);
    assertEquals(0.9, result.get("LOCATION"), 0.0001);
  }
@Test
  public void testMentionTimexAggregationNullSkipped() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("next");
    token1.setNER("DATE");
    token1.setBeginPosition(0);
    token1.setEndPosition(4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Monday");
    token2.setNER("DATE");
    token2.setBeginPosition(5);
    token2.setEndPosition(11);
    Timex timex = new Timex("t3", "2024-01-22");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("next Monday");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation document = new Annotation("next Monday");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    document.set(CoreAnnotations.TextAnnotation.class, "next Monday");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    Timex mentionTimex = mentions.get(0).get(TimeAnnotations.TimexAnnotation.class);
    assertNotNull(mentionTimex);
    assertEquals("t3", mentionTimex.tid());
  }
@Test
  public void testEmptyTokensInSentence() {
    CoreMap sentence = new Annotation("Empty");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Empty");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Empty");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testTokensWithNullNERAnnotations() {
    CoreLabel token = new CoreLabel();
    token.setWord("EntityX");
    token.setBeginPosition(0);
    token.setEndPosition(7);
    token.set(CoreAnnotations.TextAnnotation.class, "EntityX");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("EntityX");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("EntityX");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "EntityX");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testTokensWithoutBeginEndPositionInTextUpdate() {
    CoreLabel token = new CoreLabel();
    token.setWord("Bookstore");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Bookstore");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Bookstore");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Bookstore");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    
  }
@Test
  public void testNormalizedNERValueTakesPrecedenceOverText() {
    CoreLabel token = new CoreLabel();
    token.setWord("SF");
    token.setNER("CITY");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "San Francisco");
    token.set(CoreAnnotations.TextAnnotation.class, "SF");
    token.setBeginPosition(0);
    token.setEndPosition(2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("SF");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("SF");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "SF");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);

    assertEquals("San Francisco", mention.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }
@Test
  public void testSetGenderAnnotationOnTokenIfAvailable() {
    CoreLabel token = new CoreLabel();
    token.setWord("He");
    token.set(CoreAnnotations.TextAnnotation.class, "He");
    token.setBeginPosition(0);
    token.setEndPosition(2);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("He");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("He");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    }
@Test
  public void testTokenCompatibilityNumberTagSameValue() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "5");
    token1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "5");
    token2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 5);

    Pair<CoreLabel, CoreLabel> tokensPair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Predicate<Pair<CoreLabel, CoreLabel>> predicate;

    try {
      java.lang.reflect.Field f = EntityMentionsAnnotator.class.getDeclaredField("IS_TOKENS_COMPATIBLE");
      f.setAccessible(true);
      predicate = (Predicate<Pair<CoreLabel, CoreLabel>>) f.get(annotator);
    } catch (Exception e) {
      throw new AssertionError("Could not access IS_TOKENS_COMPATIBLE");
    }

    assertTrue(predicate.test(tokensPair));
  }
@Test
  public void testTokenCompatibilityDateTagDifferentTimexIds() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");

    Timex timex1 = new Timex("t1", "2024-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");

    Timex timex2 = new Timex("t2", "2024-01-01");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex2);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Predicate<Pair<CoreLabel, CoreLabel>> predicate;

    try {
      java.lang.reflect.Field f = EntityMentionsAnnotator.class.getDeclaredField("IS_TOKENS_COMPATIBLE");
      f.setAccessible(true);
      predicate = (Predicate<Pair<CoreLabel, CoreLabel>>) f.get(annotator);
    } catch (Exception e) {
      throw new AssertionError("Could not access IS_TOKENS_COMPATIBLE");
    }

    assertFalse(predicate.test(pair));
  }
@Test
  public void testMentionWithNullCharacterOffsetsSkipsTextUpdate() {
    CoreLabel token = new CoreLabel();
    token.setWord("NASA");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.TextAnnotation.class, "NASA");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("NASA");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("NASA");
    doc.set(CoreAnnotations.TextAnnotation.class, "NASA");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String value = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("NASA", value);
  }
@Test
  public void testKbpIsPronominalMentionNegativeCase() {
    CoreLabel token = new CoreLabel();
    token.setWord("RandomWord");
  }
@Test
  public void testMentionGetsWikipediaFromSecondToken() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("New");
    token1.setNER("ORGANIZATION");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.setBeginPosition(0);
    token1.setEndPosition(3);
    token1.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("York");
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.setBeginPosition(4);
    token2.setEndPosition(8);
    token2.set(CoreAnnotations.WikipediaEntityAnnotation.class, "New_York");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("New York");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("New York");
    doc.set(CoreAnnotations.TextAnnotation.class, "New York");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    CoreMap mention = doc.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertEquals("New_York", mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testOverlapsWithMentionExactCharBoundary() {
    CoreLabel token = new CoreLabel();
    token.setBeginPosition(0);
    token.setEndPosition(5);

    CoreMap refMention = new Annotation("");
    refMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreLabel overlapToken = new CoreLabel();
    overlapToken.setBeginPosition(5);
    overlapToken.setEndPosition(10);

    CoreMap testMention = new Annotation("");
    testMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(overlapToken));
  }
@Test
  public void testEmptyPropertiesInConstructorFallBackDefaults() {
    Properties props = new Properties();
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("anything", props);

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.TextAnnotation.class, "IBM");

    CoreMap sentence = new Annotation("IBM");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation document = new Annotation("IBM");
    document.set(CoreAnnotations.TextAnnotation.class, "IBM");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
  }
@Test
  public void testAnnotatePronominalMentionAcrossTwoSentences() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("She");
    token1.set(CoreAnnotations.TextAnnotation.class, "She");
    token1.setBeginPosition(0);
    token1.setEndPosition(3);

    CoreMap sentence1 = new Annotation("She");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    sentence1.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("He");
    token2.set(CoreAnnotations.TextAnnotation.class, "He");
    token2.setBeginPosition(4);
    token2.setEndPosition(6);

    CoreMap sentence2 = new Annotation("He");
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
    sentence2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    sentence2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);

    Annotation doc = new Annotation("She He");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

    }
@Test
  public void testDetermineEntityMentionConfidencesTagMissingCompletely() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("LOCATION", 0.9);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> probs2 = new HashMap<>();
    
    probs2.put("ORGANIZATION", 0.6);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap mention = new Annotation("");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertEquals(2, result.size());
    assertEquals(0.9, result.get("LOCATION"), 0.0001);
    assertEquals(0.6, result.get("ORGANIZATION"), 0.0001);
  }
@Test
  public void testEmptyNamedEntityTagProbsAnnotationReturnsNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("Dummy");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = new Annotation("Entity");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(result);
  }
@Test
  public void testPartiallyOverlappingEntityMentionOverlapDetection() {
    CoreLabel refToken = new CoreLabel();
    refToken.setBeginPosition(10);
    refToken.setEndPosition(20);

    CoreMap existingMention = new Annotation("");
    existingMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(refToken));

    CoreLabel newToken = new CoreLabel();
    newToken.setBeginPosition(15);
    newToken.setEndPosition(25);

    CoreMap newMention = new Annotation("");
    newMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(newToken));
  }
@Test
  public void testMentionFallbackToSurfaceTextWhenNoNormalizedNER() {
    CoreLabel token = new CoreLabel();
    token.setWord("MarsCorp");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.TextAnnotation.class, "MarsCorp");
    token.setBeginPosition(0);
    token.setEndPosition(8);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("MarsCorp");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("MarsCorp");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "MarsCorp");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    CoreMap mention = annotation.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    String resultText = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("MarsCorp", resultText);
  }
@Test
  public void testCustomPropertiesConstructorIgnoresInvalidAnnotationClasses() {
    Properties props = new Properties();
    props.setProperty("custom.nerCoreAnnotation", "com.nonexistent.FakeAnnotation");  
    props.setProperty("custom.nerNormalizedCoreAnnotation", "invalid.Class");
    props.setProperty("custom.mentionsCoreAnnotation", "invalid.Class");
    props.setProperty("custom.acronyms", "true");
    props.setProperty("custom.language", "en");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("custom", props);

    CoreLabel token = new CoreLabel();
    token.setWord("OpenAI");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.TextAnnotation.class, "OpenAI");

    CoreMap sentence = new Annotation("OpenAI");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("OpenAI");
    annotation.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
  }
@Test
  public void testAnnotateWithMissingSentencesAnnotationDoesNotFail() {
    Annotation annotation = new Annotation("No Sentences");
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not throw when SentencesAnnotation is missing");
    }
  }
@Test
  public void testAcronymTokenOIsDetectedCorrectlyWhenConfigured() {
    CoreLabel acronymCandidate = new CoreLabel();
    acronymCandidate.setWord("NASA");
    acronymCandidate.setNER("O");
    acronymCandidate.setBeginPosition(0);
    acronymCandidate.setEndPosition(4);
    acronymCandidate.set(CoreAnnotations.TextAnnotation.class, "NASA");

    CoreMap sentence = new Annotation("NASA");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(acronymCandidate));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    CoreLabel orgToken = new CoreLabel();
    orgToken.setWord("National Aeronautics and Space Administration");
    orgToken.setNER("ORGANIZATION");
    orgToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    orgToken.set(CoreAnnotations.TextAnnotation.class, "National Aeronautics and Space Administration");
    List<CoreLabel> orgTokens = Collections.singletonList(orgToken);

    CoreMap orgMention = new Annotation("National Aeronautics and Space Administration");
    orgMention.set(CoreAnnotations.TokensAnnotation.class, orgTokens);
    orgMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    Annotation document = new Annotation("NASA");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    sentences.get(0).get(CoreAnnotations.MentionsAnnotation.class).add(orgMention);

    Properties props = new Properties();
    props.setProperty("acronyms", "true");
    props.setProperty("language", "en");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("custom", props);
    annotator.annotate(document);
    
  }
@Test
  public void testDetermineConfidenceTagRemainsMinus1IfNeverMatched() {
    CoreLabel t1 = new CoreLabel();
    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("LOCATION", 1.1);
    t1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel t2 = new CoreLabel();
    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("LOCATION", 1.1);
    t2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> tokens = Arrays.asList(t1, t2);

    CoreMap mention = new Annotation("nowhere");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertEquals(-1.0, result.get("LOCATION"), 0.0001);
  } 
}