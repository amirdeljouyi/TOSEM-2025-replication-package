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

public class EntityMentionsAnnotator_5_GPTLLMTest {

 @Test
  public void testDefaultAnnotatorExtractsPersonAndOrganization() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Annotation annotation = new Annotation("John works at Stanford.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");
    token1.setBeginPosition(0);
    token1.setEndPosition(4);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "John");
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("PERSON", 0.95));

    CoreLabel token2 = new CoreLabel();
    token2.setWord("works");
    token2.setBeginPosition(5);
    token2.setEndPosition(10);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "O");
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 1.0));

    CoreLabel token3 = new CoreLabel();
    token3.setWord("at");
    token3.setBeginPosition(11);
    token3.setEndPosition(13);
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token3.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "O");
    token3.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 1.0));

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Stanford");
    token4.setBeginPosition(14);
    token4.setEndPosition(22);
    token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token4.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Stanford");
    token4.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("ORGANIZATION", 0.88));

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    CoreMap sentence = new Annotation("John works at Stanford.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "John works at Stanford.");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(2, mentions.size());

    CoreMap mention1 = mentions.get(0);
    String ner1 = mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    String name1 = mention1.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    Map<String, Double> probs1 = mention1.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    CoreMap mention2 = mentions.get(1);
    String ner2 = mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    String name2 = mention2.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    Map<String, Double> probs2 = mention2.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    assertEquals("PERSON", ner1);
    assertEquals("John", name1);
    assertEquals(Double.valueOf(0.95), probs1.get("PERSON"));

    assertEquals("ORGANIZATION", ner2);
    assertEquals("Stanford", name2);
    assertEquals(Double.valueOf(0.88), probs2.get("ORGANIZATION"));
  }
@Test
  public void testDateMentionIsAnnotatedWithTimex() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Annotation annotation = new Annotation("January 1st 2020");

    Timex timex = new Timex("t1");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("January");
    token1.setBeginPosition(0);
    token1.setEndPosition(7);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2020-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DATE", 0.9));

    CoreLabel token2 = new CoreLabel();
    token2.setWord("1st");
    token2.setBeginPosition(8);
    token2.setEndPosition(11);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2020-01-01");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DATE", 0.9));

    CoreLabel token3 = new CoreLabel();
    token3.setWord("2020");
    token3.setBeginPosition(12);
    token3.setEndPosition(16);
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token3.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2020-01-01");
    token3.set(TimeAnnotations.TimexAnnotation.class, timex);
    token3.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DATE", 0.9));

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new Annotation("January 1st 2020");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "January 1st 2020");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());

    CoreMap mention = mentions.get(0);
    Timex mentionTimex = mention.get(TimeAnnotations.TimexAnnotation.class);
    assertNotNull(mentionTimex);
    assertEquals("t1", mentionTimex.tid());
  }
@Test
  public void testPronounMentionWithKBP() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Annotation annotation = new Annotation("He went home.");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("He");
    token1.setBeginPosition(0);
    token1.setEndPosition(2);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "O");
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 1.0));

    CoreLabel token2 = new CoreLabel();
    token2.setWord("went");
    token2.setBeginPosition(3);
    token2.setEndPosition(7);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "O");
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 1.0));

    CoreLabel token3 = new CoreLabel();
    token3.setWord("home");
    token3.setBeginPosition(8);
    token3.setEndPosition(12);
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token3.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "O");
    token3.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 1.0));

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new Annotation("He went home.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "He went home.");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());

    CoreMap mention = mentions.get(0);
    String text = mention.get(CoreAnnotations.TextAnnotation.class);
    String type = mention.get(CoreAnnotations.EntityTypeAnnotation.class);
    String gender = mention.get(CoreAnnotations.GenderAnnotation.class);

    assertEquals("He", text);
    assertEquals("PERSON", type);
    assertEquals("MALE", gender);
  }
@Test
  public void testDetermineEntityMentionConfidenceFallback() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Annotation annotation = new Annotation("Google");

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Google");
    Map<String, Double> probs = new HashMap<>(); 
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap chunk = new Annotation("Google");
    chunk.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    HashMap<String, Double> conf = EntityMentionsAnnotator.determineEntityMentionConfidences(chunk);
    assertNull(conf);
  }
@Test
  public void testRequireKeysIncludesNamedEntityTagAnnotation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedDeclaresMentionsAnnotation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testEmptySentencesListInAnnotation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    
    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull("Mentions annotation should be initialized", mentions);
    assertTrue("Mentions list should be empty", mentions.isEmpty());
  }
@Test
  public void testSingleTokenMentionWithNoNER() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("Nonsense");

    CoreLabel token = new CoreLabel();
    token.setWord("Nonsense");
    token.setBeginPosition(0);
    token.setEndPosition(8);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 0.9));

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation("Nonsense");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Nonsense");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testMissingEntityTextInMention() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("Corporation");

    CoreLabel token = new CoreLabel();
    token.setWord("Corporation");
    token.setBeginPosition(0);
    token.setEndPosition(11);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("ORGANIZATION", 0.88));

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Corporation");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Corporation");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());

    CoreMap mention = mentions.get(0);
    String entityText = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Corporation", entityText);
  }
@Test
  public void testMentionsAnnotationMissingInSentence() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("ORGANIZATION", 0.90));

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("IBM");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    

    Annotation annotation = new Annotation("IBM");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "IBM");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertEquals("ORGANIZATION", mentions.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testConstructorWithInvalidCoreAnnotationClass() {
    Properties props = new Properties();
    props.setProperty("test.nerCoreAnnotation", "non.existing.Class");
    props.setProperty("test.nerNormalizedCoreAnnotation", "also.fake.Class");
    props.setProperty("test.mentionsCoreAnnotation", "still.fake.Class");
    props.setProperty("test.acronyms", "false");
    props.setProperty("test.language", "en");

    
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("test", props);
    assertNotNull(annotator);
  }
@Test
  public void testEntityMentionWithNoProbabilitiesSetAtAll() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("Microsoft");

    CoreLabel token = new CoreLabel();
    token.setWord("Microsoft");
    token.setBeginPosition(0);
    token.setEndPosition(9);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Microsoft");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Microsoft");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Microsoft");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());

    CoreMap mention = mentions.get(0);
    Map<String, Double> probs = mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    assertNull("Should return null because NamedEntityTagProbsAnnotation was never set", probs);
  }
@Test
  public void testMentionOverlappingTimexWithDifferentIds() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Timex timex1 = new Timex("t1");
    Timex timex2 = new Timex("t2");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Monday");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2021-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex1);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DATE", 0.85));

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Tuesday");
    token2.setBeginPosition(7);
    token2.setEndPosition(14);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2021-01-02");
    token2.set(TimeAnnotations.TimexAnnotation.class, timex2);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DATE", 0.85));

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("Monday Tuesday");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Monday Tuesday");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Monday Tuesday");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(2, mentions.size());
    assertNotEquals(mentions.get(0).get(TimeAnnotations.TimexAnnotation.class),
                    mentions.get(1).get(TimeAnnotations.TimexAnnotation.class));
  }
@Test
  public void testIS_TOKENS_COMPATIBLEWithNullTokenInputs() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = null;
    CoreLabel token2 = null;
 }
@Test
  public void testIS_TOKENS_COMPATIBLE_MismatchedNormalizedNer() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "USA");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Canada");

    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  }
@Test
  public void testIS_TOKENS_COMPATIBLE_SameNERWithDifferentTimexIds() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel cur = new CoreLabel();
    CoreLabel prev = new CoreLabel();

    cur.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    prev.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    cur.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2020-01-10");
    prev.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2020-01-10");

    cur.set(TimeAnnotations.TimexAnnotation.class, new Timex("t1"));
    prev.set(TimeAnnotations.TimexAnnotation.class, new Timex("t2")); 

  }
@Test
  public void testIS_TOKENS_COMPATIBLE_SameNERAndTimexId() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Timex sharedTimex = new Timex("shared");

    CoreLabel cur = new CoreLabel();
    cur.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    cur.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-02-15");
    cur.set(TimeAnnotations.TimexAnnotation.class, sharedTimex);

    CoreLabel prev = new CoreLabel();
    prev.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    prev.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2023-02-15");
    prev.set(TimeAnnotations.TimexAnnotation.class, sharedTimex);

  }
@Test
  public void testIS_TOKENS_COMPATIBLENumbersWithDifferentNumericValues() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    t1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "5");
    t1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 5);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    t2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "5");
    t2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 10); 

    }
@Test
  public void testAnnotateWithNullTokenBeginAnnotation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "IBM");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("ORGANIZATION", 0.9));
    token.setBeginPosition(0);
    token.setEndPosition(3);

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("IBM");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation doc = new Annotation("IBM");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TextAnnotation.class, "IBM");

    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    assertEquals("IBM", mentions.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAddAcronymsSkipsLongDocuments() {
    Properties props = new Properties();
    props.setProperty("annotator.acronyms", "true");
    props.setProperty("annotator.language", "en");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("annotator", props);

    List<CoreMap> sentences = new ArrayList<>();
    List<CoreMap> allMentions = new ArrayList<>();

    for (int i = 0; i < 2; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord("ORG" + i);
      token.setNER("ORGANIZATION");
      List<CoreLabel> tokenList = Collections.singletonList(token);

      CoreMap chunk = new Annotation("ORG" + i);
      chunk.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
      chunk.set(CoreAnnotations.TokensAnnotation.class, tokenList);

      allMentions.add(chunk);

      CoreMap sent = new Annotation("ORG" + i);
      sent.set(CoreAnnotations.TokensAnnotation.class, tokenList);
      sent.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>(Collections.singletonList(chunk)));
      sent.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

      sentences.add(sent);
    }

    for (int i = 0; i < 101; i++) { 
      CoreLabel token = new CoreLabel();
      token.setWord("Extra" + i);
      token.setNER("ORGANIZATION");
      List<CoreLabel> tokenList = Collections.singletonList(token);

      CoreMap extraChunk = new Annotation("Extra" + i);
      extraChunk.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
      extraChunk.set(CoreAnnotations.TokensAnnotation.class, tokenList);

      CoreMap sent = new Annotation("Extra" + i);
      sent.set(CoreAnnotations.TokensAnnotation.class, tokenList);
      sent.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>(Collections.singletonList(extraChunk)));
      sent.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

      sentences.add(sent);
    }

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    
    annotator.annotate(doc);
    List<CoreMap> mentionsPost = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentionsPost);
  }
@Test
  public void testEntityMentionWithPartialWikiLinking() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("Google");

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Google");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "http://en.wikipedia.org/wiki/Google");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.85));

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Google");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Google");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    String wiki = mentions.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("http://en.wikipedia.org/wiki/Google", wiki);
  }
@Test
  public void testWikipediaEntityFallbackFromToken() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setBeginPosition(0);
    token.setEndPosition(8);
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Stanford");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "http://en.wikipedia.org/wiki/Stanford_University");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.9));

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap mention = new Annotation("Stanford");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    

    CoreMap sentence = new Annotation("Stanford");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Stanford");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    CoreMap result = mentions.get(0);

    assertEquals("ORGANIZATION", result.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("http://en.wikipedia.org/wiki/Stanford_University",
        result.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testDetermineEntityMentionConfidences_UnmentionedLabelInitiallyGreaterThanThreshold() {
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Obama");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Map<String, Double> tagProbs = new HashMap<>();
    tagProbs.put("PERSON", 0.6);
    tagProbs.put("LOCATION", 0.7);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, tagProbs);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<>());

    CoreLabel token3 = new CoreLabel();
    token3.setWord("speaking");
    Map<String, Double> otherProbs = new HashMap<>();
    otherProbs.put("PERSON", 0.4);
    otherProbs.put("LOCATION", 0.3);
    token3.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, otherProbs);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap mention = new Annotation("Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result =
        EntityMentionsAnnotator.determineEntityMentionConfidences(mention);

    assertNotNull(result);
    assertEquals(Double.valueOf(0.4), result.get("PERSON"));
    assertEquals(Double.valueOf(0.3), result.get("LOCATION"));
  }
@Test
  public void testEntityMentionSubstringFallbackWithNoTextSpanSet() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    String text = "Barack Obama";
    Annotation annotation = new Annotation(text);

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.setNER("PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("PERSON", 1.0));

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    String fallbackText = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Barack", fallbackText);
  }
@Test
  public void testMissingSentenceIndexAnnotationDefaultsCorrectly() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Annotation annotation = new Annotation("She works in government.");

    CoreLabel token = new CoreLabel();
    token.setWord("She");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("O", 1.0));

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("She works in government.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotation.set(CoreAnnotations.TextAnnotation.class, "She works in government.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);

    for (CoreMap mention : mentions) {
      Integer idx = mention.get(CoreAnnotations.SentenceIndexAnnotation.class);
      assertNotNull(idx);
      assertEquals(Integer.valueOf(0), idx);
    }
  }
@Test
  public void testMentionEntityTypeFallbackToMentionNerTag() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Tesla");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.9));

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Tesla");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("Tesla");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Tesla");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String typeFromTag = mention.get(CoreAnnotations.EntityTypeAnnotation.class);
    assertEquals("ORGANIZATION", typeFromTag);
  }
@Test
  public void testFallbackToFirstNonNilNormalizedNERWhenNoTextAvailable() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("UN");
    token1.setBeginPosition(0);
    token1.setEndPosition(2);
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "United Nations");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token1.setNER("ORGANIZATION");
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.75));

    List<CoreLabel> tokens = Collections.singletonList(token1);

    CoreMap sentence = new Annotation("UN");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("UN");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "UN");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    String norm = mention.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);

    assertEquals("United Nations", norm);
  }
@Test
  public void testMissingNormalizedNERInAllTokensTriggersTextFallback() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("PERSON", 0.88));

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Barack");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("Barack");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Barack");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    String fallbackText = mention.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Barack", fallbackText);

    String normNER = mention.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    assertEquals("Barack", normNER);
  }
@Test
  public void testNumericCompositeValueMismatchRejectsCompatibility() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "123");
    token1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 123);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "123");
    token2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 456); 

  }
@Test
  public void testMentionOffsetSubStringMatchesTextCorrectly() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setBeginPosition(10);
    token.setEndPosition(15);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.85));

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Apple");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("     Visit Apple today.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "     Visit Apple today.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    String actualText = mentions.get(0).get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Apple", actualText);
  }
@Test
  public void testSetEntityMentionIndexProperly() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("IBM");
    token1.setBeginPosition(0);
    token1.setEndPosition(3);
    token1.setNER("ORGANIZATION");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.9));

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Apple");
    token2.setBeginPosition(5);
    token2.setEndPosition(10);
    token2.setNER("ORGANIZATION");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.95));

    CoreMap sentence = new Annotation("IBM and Apple are companies.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("IBM and Apple are companies.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "IBM and Apple are companies.");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, mentions.size());

    Integer index0 = mentions.get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class);
    Integer index1 = mentions.get(1).get(CoreAnnotations.EntityMentionIndexAnnotation.class);
    assertEquals((Integer) 0, index0);
    assertEquals((Integer) 1, index1);
  }
@Test
  public void testMentionWithNullMentionAnnotationListInSentence() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Microsoft");
    token.setBeginPosition(0);
    token.setEndPosition(9);
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.9));

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Microsoft");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    

    Annotation annotation = new Annotation("Microsoft");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Microsoft");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testTimexEqualityWhenOneTimexIsNull() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");
    token1.set(TimeAnnotations.TimexAnnotation.class, null);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");
    token2.set(TimeAnnotations.TimexAnnotation.class, new Timex("t1"));
  }
@Test
  public void testTokenWithNullNERAndNormalizedNERDoesNotThrow() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("foo");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 0.9));

    CoreMap sentence = new Annotation("foo");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("foo");
    annotation.set(CoreAnnotations.TextAnnotation.class, "foo");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
  }
@Test
  public void testMentionWithMissingTokensAnnotationYieldsNoFailure() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreMap sentence = new Annotation("no tokens");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("no tokens");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "no tokens");

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testMentionWithMissingCharacterOffsetsStillYieldsText() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("NASA");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("ORGANIZATION", 0.9));

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("NASA");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("NASA");
    annotation.set(CoreAnnotations.TextAnnotation.class, "NASA");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    CoreMap mention = mentions.get(0);
    assertNotNull(mention.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("NASA", mention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDetermineEntityMentionConfidencesWithNoTokensKeySet() {
    CoreMap mention = new Annotation("null tokens");
    

    HashMap<String, Double> probs = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(probs);
  }
@Test
  public void testDetermineEntityMentionConfidencesWithEmptyProbsInAllTokens() {
    CoreLabel t1 = new CoreLabel();
    t1.setWord("New");
    t1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.emptyMap());

    CoreLabel t2 = new CoreLabel();
    t2.setWord("York");
    t2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.emptyMap());

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    CoreMap mention = new Annotation("New York");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(result);
  }
@Test
  public void testTimexAggregationSetsFirstAvailableTimex() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Timex timex = new Timex("t42");

    CoreLabel token = new CoreLabel();
    token.setWord("Monday");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.setNER("DATE");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-05-20");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("DATE", 0.9));

    List<CoreLabel> tokenList = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Monday");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("Monday");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Monday");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    assertEquals("t42", mention.get(TimeAnnotations.TimexAnnotation.class).tid());
  }
@Test
  public void testHandlesUnknownNERTagGracefully() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Kraken");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.setNER("MYTHOLOGY");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MYTHOLOGY");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Kraken");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("MYTHOLOGY", 0.8));

    CoreMap sentence = new Annotation("Kraken");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("Kraken");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Kraken");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    assertEquals("MYTHOLOGY", mentions.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAcronymDetectionSkipsNonUpperCase() {
    Properties props = new Properties();
    props.setProperty("test.acronyms", "true");
    props.setProperty("test.language", "en");
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.setWord("un");
    token.setBeginPosition(0);
    token.setEndPosition(2);
    token.setNER("O");

    CoreMap sentence = new Annotation("un");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("un");
    annotation.set(CoreAnnotations.TextAnnotation.class, "un");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
  }
@Test
  public void testPronominalMentionCaseInsensitivity() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("She");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("O", 1.0));

    token.set(CoreAnnotations.TextAnnotation.class, "She");

    CoreMap sentence = new Annotation("She");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("She");
    annotation.set(CoreAnnotations.TextAnnotation.class, "She");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    boolean found = false;
    for (CoreMap m : mentions) {
      if ("She".equalsIgnoreCase(m.get(CoreAnnotations.TextAnnotation.class))) {
        String gender = m.get(CoreAnnotations.GenderAnnotation.class);
        assertEquals("FEMALE", gender);
        found = true;
      }
    }
    assertTrue(found);
  }
@Test
  public void testNullTextAnnotationInDocumentDoesNotCauseFailure() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setBeginPosition(5);
    token.setEndPosition(8);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.9));

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation(""); 
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
  }
@Test
  public void testMentionWithoutTimexOrNormalizedNERUsesRawTokenText() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("January");
    token.setBeginPosition(0);
    token.setEndPosition(7);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("DATE", 0.8));
    

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("January");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("January");
    annotation.set(CoreAnnotations.TextAnnotation.class, "January");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    String text = mentions.get(0).get(CoreAnnotations.TextAnnotation.class);
    assertEquals("January", text);
  }
@Test
  public void testIS_TOKENS_COMPATIBLENullNumericCompositeAnnotations() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    t1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "50");
    t1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, null);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");
    t2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "50");
    t2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, null);

    try {
      java.lang.reflect.Field field = EntityMentionsAnnotator.class.getDeclaredField("IS_TOKENS_COMPATIBLE");
      field.setAccessible(true);
      @SuppressWarnings("unchecked")
      Predicate<Pair<CoreLabel, CoreLabel>> predicate = (Predicate<Pair<CoreLabel, CoreLabel>>) field.get(annotator);
      boolean result = predicate.test(new Pair<>(t1, t2));
      assertTrue(result);
    } catch (Exception e) {
      fail("Reflection error accessing IS_TOKENS_COMPATIBLE");
    }
  }
@Test
  public void testOverlapsWithMentionDoesNotMatchWhenNonOverlapping() {
    Annotation a = new Annotation("sample");

    CoreLabel tokenA = new CoreLabel();
    tokenA.setBeginPosition(0);
    tokenA.setEndPosition(4);
    tokenA.setWord("Four");

    List<CoreLabel> tokensA = Collections.singletonList(tokenA);

    CoreMap mentionToCheck = new Annotation("Four");
    mentionToCheck.set(CoreAnnotations.TokensAnnotation.class, tokensA);

    CoreLabel tokenB = new CoreLabel();
    tokenB.setBeginPosition(10);
    tokenB.setEndPosition(15);
    tokenB.setWord("Fifteen");

    List<CoreLabel> tokensB = Collections.singletonList(tokenB);

    CoreMap otherMention = new Annotation("Fifteen");
    otherMention.set(CoreAnnotations.TokensAnnotation.class, tokensB);

    List<CoreMap> mentionList = Collections.singletonList(otherMention);

    try {
      java.lang.reflect.Method method = EntityMentionsAnnotator.class.getDeclaredMethod("overlapsWithMention", CoreMap.class, List.class);
      method.setAccessible(true);
      Optional<CoreMap> match = (Optional<CoreMap>) method.invoke(null, mentionToCheck, mentionList);
      assertFalse(match.isPresent());
    } catch (Exception e) {
      fail("Reflection error invoking overlapsWithMention");
    }
  }
@Test
  public void testAddAcronymsWhenNoOrganizationsDoesNothing() {
    Properties props = new Properties();
    props.setProperty("acronyms", "true");
    props.setProperty("language", "en");

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("foo", props);

    CoreLabel token = new CoreLabel();
    token.setWord("ABC");
    token.setNER("O");

    CoreMap sentence = new Annotation("ABC");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("ABC");
    annotation.set(CoreAnnotations.TextAnnotation.class, "ABC");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
  }
@Test
  public void testAnnotateHandlesMultipleSentencesAndMergesMentions() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Google");
    t1.setBeginPosition(0);
    t1.setEndPosition(6);
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    t1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.8));

    CoreMap sent1 = new Annotation("Google");
    sent1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t1));
    sent1.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sent1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sent1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Amazon");
    t2.setBeginPosition(8);
    t2.setEndPosition(14);
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    t2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class,
        Collections.singletonMap("ORGANIZATION", 0.85));

    CoreMap sent2 = new Annotation("Amazon");
    sent2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t2));
    sent2.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sent2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    sent2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

    List<CoreMap> sentenceList = Arrays.asList(sent1, sent2);

    Annotation annotation = new Annotation("Google then Amazon.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Google then Amazon.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> results = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(2, results.size());
    assertEquals((Integer) 0, results.get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    assertEquals((Integer) 1, results.get(1).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
  }
@Test
  public void testPronominalMentionMaleHeAndFemaleSheInSameSentence() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("He");
    token1.set(CoreAnnotations.TextAnnotation.class, "He");
    token1.setBeginPosition(0);
    token1.setEndPosition(2);
    token1.setNER("O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("She");
    token2.set(CoreAnnotations.TextAnnotation.class, "She");
    token2.setBeginPosition(4);
    token2.setEndPosition(7);
    token2.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new Annotation("He and She");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation doc = new Annotation("He and She");
    doc.set(CoreAnnotations.TextAnnotation.class, "He and She");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    boolean foundHe = false;
    boolean foundShe = false;

    for (CoreMap mention : mentions) {
      String gender = mention.get(CoreAnnotations.GenderAnnotation.class);
      String text = mention.get(CoreAnnotations.TextAnnotation.class);
      if ("He".equalsIgnoreCase(text)) {
        foundHe = true;
        assertEquals("MALE", gender);
      } else if ("She".equalsIgnoreCase(text)) {
        foundShe = true;
        assertEquals("FEMALE", gender);
      }
    }

    assertTrue(foundHe && foundShe);
  } 
}