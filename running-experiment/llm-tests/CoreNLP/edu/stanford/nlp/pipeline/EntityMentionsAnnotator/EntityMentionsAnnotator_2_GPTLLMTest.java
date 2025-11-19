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
import org.w3c.dom.Element;

import java.util.*;

import static org.junit.Assert.*;

public class EntityMentionsAnnotator_2_GPTLLMTest {

 @Test
  public void testSingleNamedEntityMention() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Steve");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Steve Jobs");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Jobs");
    token2.setBeginPosition(6);
    token2.setEndPosition(10);
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Steve Jobs");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    assertEquals("PERSON", mentions.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("Steve Jobs", mentions.get(0).get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }
@Test
  public void testNonEntityTokensIgnored() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("is");
    token1.setBeginPosition(0);
    token1.setEndPosition(2);
    token1.setNER("O");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("a");
    token2.setBeginPosition(3);
    token2.setEndPosition(4);
    token2.setNER("O");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testEntityMentionIndexSetCorrectly() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("California");
    token.setBeginPosition(0);
    token.setEndPosition(10);
    token.setNER("LOCATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "CA");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    Integer mentionIndex = mention.get(CoreAnnotations.EntityMentionIndexAnnotation.class);
    Integer canonicalIndex = mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class);

    assertEquals(Integer.valueOf(0), mentionIndex);
    assertEquals(Integer.valueOf(0), canonicalIndex);
  }
@Test
  public void testMentionTextSetUsingOffsetsWhenTextProvided() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Google LLC");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("Google is a company.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertEquals("Google", mention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testTimexAnnotationPreserved() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Monday");
    token.setNER("DATE");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-09-18");
    Timex timex = new Timex("t1");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    Timex resultTimex = mention.get(TimeAnnotations.TimexAnnotation.class);
    assertNotNull(resultTimex);
    assertEquals("t1", resultTimex.tid());
  }
@Test
  public void testPronominalSheMentionGetsGender() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("She");
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    boolean found = false;
    
    if (mentions != null && !mentions.isEmpty()) {
      String text = mentions.get(0).get(CoreAnnotations.TextAnnotation.class);
      String gender = mentions.get(0).get(CoreAnnotations.GenderAnnotation.class);
      if ("She".equalsIgnoreCase(text)) {
        assertEquals("FEMALE", gender);
        found = true;
      }
    }

    assertTrue(found);
  }
@Test
  public void testPronominalHeMentionGetsGender() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("He");
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    boolean found = false;

    if (mentions != null && !mentions.isEmpty()) {
      String text = mentions.get(0).get(CoreAnnotations.TextAnnotation.class);
      String gender = mentions.get(0).get(CoreAnnotations.GenderAnnotation.class);
      if ("He".equalsIgnoreCase(text)) {
        assertEquals("MALE", gender);
        found = true;
      }
    }

    assertTrue(found);
  }
@Test
  public void testNamedEntityTagProbsSetProperly() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Map<String, Double> probs = new HashMap<>();
    probs.put("PERSON", 0.9);
    probs.put("LOCATION", 0.2);

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setNER("PERSON");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Barack Obama");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    Map<String, Double> outputProbs = mentions.get(0).get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    assertNotNull(outputProbs);
    assertEquals(0.9, outputProbs.get("PERSON"), 1e-6);
    assertEquals(0.2, outputProbs.get("LOCATION"), 1e-6);
  }
@Test
  public void testEmptyAnnotationNoSentences() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);

    assertNull(annotation.get(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testNullTokensInSentence() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, null);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(document);
      
      assertTrue(true);
    } catch (Exception e) {
      fail("Should gracefully handle null tokens without exception");
    }
  }
@Test
  public void testMentionWithNullNERTag() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setNER(null);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Apple Inc.");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Apple is a company.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    if (mentions != null && !mentions.isEmpty()) {
      String entityType = mentions.get(0).get(CoreAnnotations.EntityTypeAnnotation.class);
      assertNull(entityType);
    } else {
      assertTrue(true); 
    }
  }
@Test
  public void testTokensWithDifferentNERBreakChunking() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Barack");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Hawaii");
    token2.setNER("LOCATION");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Hawaii");

    token1.setBeginPosition(0);
    token1.setEndPosition(6);
    token2.setBeginPosition(7);
    token2.setEndPosition(13);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Barack Hawaii");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(2, mentions.size()); 
  }
@Test
  public void testMentionWithTimexTidMismatch() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("2023");
    token1.setNER("DATE");
    token1.setBeginPosition(0);
    token1.setEndPosition(4);
    Timex timex1 = new Timex("t1");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex1);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("April");
    token2.setNER("DATE");
    token2.setBeginPosition(5);
    token2.setEndPosition(10);
    Timex timex2 = new Timex("t2"); 
    token2.set(TimeAnnotations.TimexAnnotation.class, timex2);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-04");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("2023 April");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    
    assertEquals(2, mentions.size());
  }
@Test
  public void testTokenWithNERButNoNERProbs() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Tesla");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Tesla");

    

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("Tesla was founded...");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    Map<String, Double> probs = mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertNull(probs); 
  }
@Test
  public void testEntityMentionOffsetBeyondTextLength() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Universe");
    token.setNER("LOCATION");
    token.setBeginPosition(200);
    token.setEndPosition(208); 
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Universe");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Short text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    
    assertNotNull(mention);
    
    assertTrue(true);
  }
@Test
  public void testChunkWithUndefinedMentionAnnotationsList() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("NASA");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "NASA");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    

    Annotation annotation = new Annotation("NASA launches spacecraft.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(annotation);
      assertNotNull(sentence.get(CoreAnnotations.MentionsAnnotation.class));
      assertTrue(true);
    } catch (Exception e) {
      fail("Annotator should handle missing sentence-level MentionsAnnotation list.");
    }
  }
@Test
  public void testNamedEntityTagProbsAllAboveThresholdGetDefaulted() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Map<String, Double> probs = new HashMap<>();
    probs.put("PERSON", 1.5);
    probs.put("LOCATION", 1.3);
    probs.put("ORGANIZATION", 1.2);

    CoreLabel token = new CoreLabel();
    token.setWord("Example");
    token.setNER("PERSON");
    token.setBeginPosition(0);
    token.setEndPosition(7);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Example Name");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("Example sentence.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    Map<String, Double> output = mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    assertNotNull(output);
    assertEquals(-1.0, output.get("PERSON"), 0.0001);
    assertEquals(-1.0, output.get("LOCATION"), 0.0001);
    assertEquals(-1.0, output.get("ORGANIZATION"), 0.0001);
  }
@Test
  public void testSentenceWithEmptyTokenList() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Empty token test.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testOverlapsWithMentionOverlappingSpan() throws Exception {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    CoreMap mention1 = new ArrayCoreMap();
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreLabel token2 = new CoreLabel();
    token2.setBeginPosition(3);
    token2.setEndPosition(8);

    CoreMap mention2 = new ArrayCoreMap();
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));

    List<CoreMap> haystack = Collections.singletonList(mention2);
    List<CoreLabel> tokenList = Collections.singletonList(token1);

    mention1.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));

    java.lang.reflect.Method method = EntityMentionsAnnotator.class.getDeclaredMethod("overlapsWithMention", CoreMap.class, List.class);
    method.setAccessible(true);
    Optional result = (Optional) method.invoke(null, mention1, haystack);
    assertTrue(result.isPresent());
  }
@Test
  public void testTokensCompatibilityWithDifferentTimexTid() throws Exception {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setNER("DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2022-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, new Timex("t1"));

    CoreLabel token2 = new CoreLabel();
    token2.setNER("DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2022-01-01");
    token2.set(TimeAnnotations.TimexAnnotation.class, new Timex("t2"));

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    java.lang.reflect.Field field = EntityMentionsAnnotator.class.getDeclaredField("IS_TOKENS_COMPATIBLE");
    field.setAccessible(true);
    Object predicate = field.get(annotator);
    java.lang.reflect.Method testMethod = predicate.getClass().getDeclaredMethod("test", Object.class);
    testMethod.setAccessible(true);

    boolean result = (boolean) testMethod.invoke(predicate, pair);
    assertFalse(result); 
  }
@Test
  public void testTokensCompatibilityDifferentNumericCompositeValues() throws Exception {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setNER("NUMBER");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "5");
    token1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.setNER("NUMBER");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "5");
    token2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 10); 

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    java.lang.reflect.Field field = EntityMentionsAnnotator.class.getDeclaredField("IS_TOKENS_COMPATIBLE");
    field.setAccessible(true);
    Object predicate = field.get(annotator);
    java.lang.reflect.Method method = predicate.getClass().getDeclaredMethod("test", Object.class);
    method.setAccessible(true);
    boolean result = (boolean) method.invoke(predicate, pair);

    assertFalse(result); 
  }
@Test
  public void testAnnotatePronominalSheLowerCase() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("she");
    token.setNER("O");
    token.setBeginPosition(0);
    token.setEndPosition(3);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation document = new Annotation("she ran fast.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<CoreMap> mentions = document.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    boolean matched = false;
    for (CoreMap m : mentions) {
      if ("she".equalsIgnoreCase(m.get(CoreAnnotations.TextAnnotation.class))) {
        assertEquals("FEMALE", m.get(CoreAnnotations.GenderAnnotation.class));
        matched = true;
      }
    }
    assertTrue(matched);
  }
@Test
  public void testMissingAnnotationTextAnnotationField() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "International Business Machines");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation((Annotation) null);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    String text = mention.get(CoreAnnotations.TextAnnotation.class);
    assertNotNull(text); 
  }
@Test
  public void testTokenWithWikipediaEntityAnnotationAssignedToMention() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    token.setNER("LOCATION");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Paris, France");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Paris_(France)");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Paris is a popular city.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertEquals("Paris_(France)", mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testAcronymMentionAddedWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("test.acronyms", "true");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("test", props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("International");
    token1.setNER("ORGANIZATION");
    token1.setBeginPosition(0);
    token1.setEndPosition(12);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Business");
    token2.setNER("ORGANIZATION");
    token2.setBeginPosition(13);
    token2.setEndPosition(21);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Machines");
    token3.setNER("ORGANIZATION");
    token3.setBeginPosition(22);
    token3.setEndPosition(30);
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreLabel acronymToken = new CoreLabel();
    acronymToken.setWord("IBM");
    acronymToken.setNER("O");
    acronymToken.setBeginPosition(31);
    acronymToken.setEndPosition(34);
    acronymToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, acronymToken);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("International Business Machines IBM");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    boolean foundAcronymMention = false;
    for (CoreMap m : mentions) {
      if ("ORGANIZATION".equals(m.get(CoreAnnotations.NamedEntityTagAnnotation.class))) {
        List<CoreLabel> mTokens = m.get(CoreAnnotations.TokensAnnotation.class);
        if (mTokens.size() == 1 && "IBM".equals(mTokens.get(0).word())) {
          foundAcronymMention = true;
        }
      }
    }
    assertTrue(foundAcronymMention);
  }
@Test
  public void testAcronymNotAddedWhenMixedCase() {
    Properties props = new Properties();
    props.setProperty("custom.acronyms", "true");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("custom", props);

    CoreLabel orgToken = new CoreLabel();
    orgToken.setWord("National");
    orgToken.setNER("ORGANIZATION");
    orgToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreLabel acronym = new CoreLabel();
    acronym.setWord("NaSa"); 
    acronym.setNER("O");
    acronym.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(orgToken, acronym);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation ann = new Annotation("National NaSa");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    boolean foundNaSa = false;
    for (CoreMap m : mentions) {
      List<CoreLabel> mTokens = m.get(CoreAnnotations.TokensAnnotation.class);
      if (mTokens.size() == 1 && "NaSa".equals(mTokens.get(0).word())) {
        foundNaSa = true;
      }
    }

    
    assertFalse(foundNaSa);
  }
@Test
  public void testMentionWithoutOffsetAnnotations() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("OpenAI");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation ann = new Annotation("OpenAI is an AI lab.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertFalse(mentions.isEmpty());

    CoreMap mention = mentions.get(0);
    
    assertNotNull(mention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMultiTokenMentionWithSameTimexTid() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Timex sharedTimex = new Timex("t1");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Friday");
    token1.setNER("DATE");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-10-13");
    token1.set(TimeAnnotations.TimexAnnotation.class, sharedTimex);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("13th");
    token2.setNER("DATE");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-10-13");
    token2.set(TimeAnnotations.TimexAnnotation.class, sharedTimex);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation ann = new Annotation("Friday 13th");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    assertEquals("DATE", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
  }
@Test
  public void testPronounNotInWordListsIsIgnored() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Those");
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation ann = new Annotation("Those are good.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> mentions = ann.get(CoreAnnotations.MentionsAnnotation.class);
    
    boolean found = false;
    for (CoreMap m : mentions) {
      if ("Those".equalsIgnoreCase(m.get(CoreAnnotations.TextAnnotation.class))) {
        found = true;
      }
    }
    assertFalse(found);
  }
@Test
  public void testOverlapsWithMentionNoOverlapStillEmpty() throws Exception {
    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(0);
    token1.setEndPosition(4);

    CoreMap needle = new ArrayCoreMap();
    needle.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreLabel other = new CoreLabel();
    other.setBeginPosition(10);
    other.setEndPosition(15);

    CoreMap hay = new ArrayCoreMap();
    hay.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(other));

    List<CoreMap> haystack = Collections.singletonList(hay);

    java.lang.reflect.Method method = EntityMentionsAnnotator.class.getDeclaredMethod("overlapsWithMention", CoreMap.class, List.class);
    method.setAccessible(true);
    Optional result = (Optional) method.invoke(null, needle, haystack);
    assertFalse(result.isPresent());
  }
@Test
  public void testInvalidPropertyClassDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("x.nerCoreAnnotation", "non.existent.ClassName");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("x", props);

    
    assertNotNull(annotator);
  }
@Test
  public void testSentenceMissingTokenBeginAnnotationHandledGracefully() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Amazon");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Amazon Inc.");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation ann = new Annotation("Amazon is large.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
  }
@Test
  public void testNonEnglishLanguageSkipsKBPPronouns() {
    Properties props = new Properties();
    props.setProperty("z.language", "zh"); 

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("z", props);

    CoreLabel token = new CoreLabel();
    token.setWord("he");
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation ann = new Annotation("he is a student");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> docMentions = ann.get(CoreAnnotations.MentionsAnnotation.class);
    
    assertNotNull(docMentions);
    boolean sheFound = false;
    for (CoreMap m : docMentions) {
      if ("he".equalsIgnoreCase(m.get(CoreAnnotations.TextAnnotation.class))) {
        sheFound = true;
      }
    }
    assertFalse(sheFound);
  }
@Test
  public void testMentionWithNullTokensListHandled() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokensAnnotation.class, null);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation ann = new Annotation("Bad mention format");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      annotator.annotate(ann);
      assertTrue(true); 
    } catch (Exception e) {
      fail("Mentions with null token list should be handled gracefully.");
    }
  }
@Test
  public void testEntityTypeFallbackToTextAnnotationWhenNormalizedIsNull() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    token.setNER("LOCATION");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation document = new Annotation("Paris is lovely.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertEquals("LOCATION", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("Paris", mention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testWikipediaEntityAnnotationFilteredOIgnored() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setNER("LOCATION");
    token.setWord("London");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "London");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation ann = new Annotation("London is rainy.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    String wiki = mention.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    
    assertEquals("O", wiki);
  }
@Test
  public void testMultipleSentencesAllMentionsAggregated() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Google");
    t1.setNER("ORGANIZATION");
    t1.setBeginPosition(0);
    t1.setEndPosition(6);
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    t1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Google LLC");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t1));
    sentence1.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Microsoft");
    t2.setNER("ORGANIZATION");
    t2.setBeginPosition(7);
    t2.setEndPosition(16);
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    t2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Microsoft Corp");

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t2));
    sentence2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence2.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation ann = new Annotation("Google. Microsoft.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

    annotator.annotate(ann);

    List<CoreMap> aggMentions = ann.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, aggMentions.size());
    assertEquals("Google", aggMentions.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Microsoft", aggMentions.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMixedTokenProbsInMention() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("PERSON", 0.9);

    Map<String, Double> probs2 = null;

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Barack");
    t1.setNER("PERSON");
    t1.setBeginPosition(0);
    t1.setEndPosition(6);
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    t1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Obama");
    t2.setNER("PERSON");
    t2.setBeginPosition(7);
    t2.setEndPosition(12);
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    t2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2); 

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("Barack Obama");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = doc.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    Map<String, Double> generated = mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertNotNull(generated);
    assertTrue(generated.containsKey("PERSON"));
    assertEquals(0.9, generated.get("PERSON"), 0.001);
  }
@Test
  public void testSkipAcronymDueToMaxOrgLimit() {
    Properties props = new Properties();
    props.setProperty("x.acronyms", "true");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("x", props);

    List<CoreMap> sentences = new ArrayList<>();

    List<CoreLabel> orgTokens = new ArrayList<>();
    for (int i = 0; i < 101; i++) {
      CoreLabel tok = new CoreLabel();
      tok.setWord("ExampleOrg" + i);
      tok.setNER("ORGANIZATION");
      tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
      orgTokens.add(tok);
    }

    CoreLabel acronym = new CoreLabel();
    acronym.setWord("EO");
    acronym.setNER("O");
    acronym.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> allTokens = new ArrayList<>(orgTokens);
    allTokens.add(acronym);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, allTokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    sentences.add(sentence);

    Annotation ann = new Annotation("Document with too many ORGs and 1 acronym EO");
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(ann);

    List<CoreMap> allMentions = ann.get(CoreAnnotations.MentionsAnnotation.class);
    boolean foundEO = false;
    for (CoreMap m : allMentions) {
      if ("EO".equals(m.get(CoreAnnotations.TextAnnotation.class))) {
        foundEO = true;
      }
    }
    assertFalse(foundEO); 
  }
@Test
  public void testSentenceWithoutMentionsAnnotationStillWorks() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Meta");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Meta Inc.");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    

    Annotation ann = new Annotation("Meta works.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testDuplicateMentionsAcrossSentencesHandled() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Amazon");
    token1.setNER("ORGANIZATION");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Amazon");

    CoreMap s1 = new ArrayCoreMap();
    s1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
    s1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    s1.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Amazon");
    token2.setNER("ORGANIZATION");
    token2.setBeginPosition(7);
    token2.setEndPosition(13);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Amazon");

    CoreMap s2 = new ArrayCoreMap();
    s2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
    s2.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    s2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("Amazon. Amazon.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(s1, s2));

    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, mentions.size());
    assertNotEquals(
        mentions.get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class),
        mentions.get(1).get(CoreAnnotations.EntityMentionIndexAnnotation.class)
    );
  }
@Test
  public void testEmptyCoreLabelMentionTextFallback() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setNER("PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("Text with unnamed entity");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    String mentionText = mention.get(CoreAnnotations.TextAnnotation.class);
    assertNotNull(mentionText);
  }
@Test
  public void testMentionWithoutWikipediaValueAnnotation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setNER("PERSON");
    token.setWord("Newton");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Isaac Newton");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, null); 

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("Newton was a scientist.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertNull(mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testTimexNullTIDStillHandled() throws Exception {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Timex timex1 = new Timex("");
    Timex timex2 = new Timex("");

    CoreLabel token1 = new CoreLabel();
    token1.setNER("DATE");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2021");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex1);

    CoreLabel token2 = new CoreLabel();
    token2.setNER("DATE");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2021");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex2);

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    java.lang.reflect.Field field = EntityMentionsAnnotator.class.getDeclaredField("IS_TOKENS_COMPATIBLE");
    field.setAccessible(true);
    Object predicate = field.get(annotator);
    java.lang.reflect.Method testMethod = predicate.getClass().getDeclaredMethod("test", Object.class);
    testMethod.setAccessible(true);

    boolean result = (boolean) testMethod.invoke(predicate, pair);
    assertTrue(result);
  }
@Test
  public void testMentionAggregationMissingIndexAnnotations() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Facebook");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(8);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Meta");

    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("Facebook started as Thefacebook.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    Integer idx = doc.get(CoreAnnotations.MentionsAnnotation.class).get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class);
    assertEquals(Integer.valueOf(0), idx);
  }
@Test
  public void testOverlapsWithMentionMultipleMentionsReturnFirstOnly() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setBeginPosition(10);
    token.setEndPosition(15);

    CoreMap needle = new ArrayCoreMap();
    needle.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreLabel tok1 = new CoreLabel();
    tok1.setBeginPosition(5);
    tok1.setEndPosition(12); 

    CoreLabel tok2 = new CoreLabel();
    tok2.setBeginPosition(14);
    tok2.setEndPosition(18); 

    CoreMap mention1 = new ArrayCoreMap();
    mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(tok1));

    CoreMap mention2 = new ArrayCoreMap();
    mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(tok2));

    List<CoreMap> haystack = new ArrayList<>();
    haystack.add(mention1);
    haystack.add(mention2);

    java.lang.reflect.Method method = EntityMentionsAnnotator.class.getDeclaredMethod("overlapsWithMention", CoreMap.class, List.class);
    method.setAccessible(true);

    Optional result = (Optional) method.invoke(null, needle, haystack);
    assertTrue(result.isPresent());
    assertEquals(mention1, result.get());
  }
@Test
  public void testRequiresDynamicWhenNERAnnotationClassIsDefault() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedOnlyMentionsAnnotation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Set<Class<? extends CoreAnnotation>> out = annotator.requirementsSatisfied();

    assertNotNull(out);
    assertEquals(1, out.size());
    assertTrue(out.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testPronominalMentionWithGenderOnlySetOnMention() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("She");
    token.setNER("O");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("She runs.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = doc.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
    assertNotNull(mention.get(CoreAnnotations.TokensAnnotation.class));
    assertEquals("She", mention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testTextAnnotationFromCharacterOffsetsFallback() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(10);
    token.setEndPosition(18);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Stanford University");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("Some intro Stanford is a place.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TextAnnotation.class, "Some intro Stanford is a place.");

    annotator.annotate(doc);

    CoreMap mention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    String extractedText = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Stanford", extractedText);
  }
@Test
  public void testMentionProbsNullIfTokenHasNoProbAnnotations() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setNER("ORGANIZATION");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "IBM");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("IBM is a tech company.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = doc.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    assertNull(mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
  }
@Test
  public void testAnnotatorHandlesEmptySentenceList() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testChunkWithoutNERTagDoesNotFail() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.setBeginPosition(0);
    token.setEndPosition(4);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("Test");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);
    assertTrue(doc.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testMultipleNERProbsLowerSelected() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("PERSON", 0.9);
    probs1.put("LOCATION", 0.5);

    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("PERSON", 0.8);
    probs2.put("LOCATION", 0.4);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setBeginPosition(7);
    token2.setEndPosition(12);
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("Barack Obama");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    CoreMap mention = doc.get(CoreAnnotations.MentionsAnnotation.class).get(0);
    Map<String, Double> result = mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertEquals(0.8, result.get("PERSON"), 0.0001);
    assertEquals(0.4, result.get("LOCATION"), 0.0001);
  }
@Test
  public void testMentionTextFallbackToIndividualTokenText() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("ML");
    token.setBeginPosition(0);
    token.setEndPosition(2);
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("ML is popular");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(annotation);

    
    assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).size() > 0);
  }
@Test
  public void testTimexTidNullAndEqualHandled() throws Exception {
    CoreLabel token1 = new CoreLabel();
    token1.setNER("DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2025-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, new Timex((Element) null));

    CoreLabel token2 = new CoreLabel();
    token2.setNER("DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2025-01-01");
    token2.set(TimeAnnotations.TimexAnnotation.class, new Timex((Element) null));

    Pair<CoreLabel, CoreLabel> pair = new Pair<>(token1, token2);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    java.lang.reflect.Field field = EntityMentionsAnnotator.class.getDeclaredField("IS_TOKENS_COMPATIBLE");
    field.setAccessible(true);
    Object predicate = field.get(annotator);
    java.lang.reflect.Method testMethod = predicate.getClass().getDeclaredMethod("test", Object.class);
    testMethod.setAccessible(true);

    boolean result = (boolean) testMethod.invoke(predicate, pair);
    assertTrue(result);
  }
@Test
  public void testMentionIndexIncrementsAcrossMultipleMentions() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Apple");
    t1.setNER("ORGANIZATION");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    t1.setBeginPosition(0);
    t1.setEndPosition(5);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Tim");
    t2.setNER("PERSON");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    t2.setBeginPosition(6);
    t2.setEndPosition(9);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(t1, t2));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("Apple Tim");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, mentions.size());
    assertEquals(Integer.valueOf(0), mentions.get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals(Integer.valueOf(1), mentions.get(1).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
  }
@Test
  public void testPronounHeGenderAppliedToMentionTokens() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("He");
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("He works.");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap heMention = mentions.get(0);
    String gender = heMention.get(CoreAnnotations.GenderAnnotation.class);
    assertEquals("MALE", gender);

    CoreLabel pronounToken = heMention.get(CoreAnnotations.TokensAnnotation.class).get(0);
    assertEquals("MALE", pronounToken.get(CoreAnnotations.GenderAnnotation.class));
  }
@Test
  public void testNoNamedEntityTagAnnotationStillSafe() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("thing");
    token.setNER(null);
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("A thing.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAcronymNotAddedIfNotUpperCase() {
    Properties props = new Properties();
    props.setProperty("demo.acronyms", "true");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("demo", props);

    CoreLabel org1 = new CoreLabel();
    org1.setWord("International");
    org1.setNER("ORGANIZATION");
    org1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    CoreLabel acronym = new CoreLabel();
    acronym.setWord("iBM"); 
    acronym.setNER("O");
    acronym.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(org1, acronym);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation ann = new Annotation("International iBM");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(ann);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    boolean foundAcronym = false;
    if (mentions != null) {
      for (CoreMap m : mentions) {
        List<CoreLabel> mTokens = m.get(CoreAnnotations.TokensAnnotation.class);
        if (mTokens.size() == 1 && "iBM".equals(mTokens.get(0).word())) {
          foundAcronym = true;
        }
      }
    }

    assertFalse(foundAcronym);
  } 
}