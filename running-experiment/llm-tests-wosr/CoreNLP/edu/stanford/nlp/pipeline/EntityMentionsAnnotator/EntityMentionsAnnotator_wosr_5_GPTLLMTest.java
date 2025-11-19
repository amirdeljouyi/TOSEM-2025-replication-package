public class EntityMentionsAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testAnnotate_singleNamedEntity() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setNER("PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Barack");
    token.setBeginPosition(0);
    token.setEndPosition(6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Obama");
    token2.setBeginPosition(7);
    token2.setEndPosition(12);

    List<CoreLabel> tokens = Arrays.asList(token, token2);
    CoreMap sentence = new Annotation(new HashMap<>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);

    assertEquals("PERSON", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals(0, (int) mention.get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertNotNull(mention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotate_withPronounHe() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("He");
    token.setNER("O");
    token.setBeginPosition(0);
    token.setEndPosition(2);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation(new HashMap<>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    boolean foundPronoun = false;
    for (CoreMap mention : mentions) {
      if ("He".equals(mention.get(CoreAnnotations.TextAnnotation.class))) {
        foundPronoun = true;
        assertEquals("PERSON", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
        assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
      }
    }
    assertTrue(foundPronoun);
  }
@Test
  public void testAnnotate_namedEntityWithWikipediaEntity() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setNER("PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Barack Obama");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Barack_Obama");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation(new HashMap<>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Barack");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    assertEquals("Barack", mention.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PERSON", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("Barack_Obama", mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testAnnotate_timexAnnotationPropagation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Timex timex = new Timex("t1");
    CoreLabel token = new CoreLabel();
    token.setWord("today");
    token.setNER("DATE");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2020-01-01");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation(new HashMap<>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("today");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    assertNotNull(mention.get(TimeAnnotations.TimexAnnotation.class));
    assertEquals("t1", mention.get(TimeAnnotations.TimexAnnotation.class).tid());
  }
@Test
  public void testAnnotate_emptySentences() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    List<CoreMap> sentences = new ArrayList<>();
    Annotation annotation = new Annotation("empty");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> resultMentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(resultMentions);
    assertTrue(resultMentions.isEmpty());
  }
@Test
  public void testDetermineEntityMentionConfidences_normalData() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("PERSON", 0.9);
    probs1.put("LOCATION", 0.1);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("PERSON", 0.7);
    probs2.put("LOCATION", 0.2);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    CoreMap mention = new Annotation("");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(0.7, result.get("PERSON"), 1e-6);
    assertEquals(0.1, result.get("LOCATION"), 1e-6);
  }
@Test
  public void testDetermineEntityMentionConfidences_missingProbs() {
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();

    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<>());
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<>());

    CoreMap mention = new Annotation("");
    mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(result);
  }
@Test
  public void testAnnotate_namedEntityWithSentenceOffset() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Stanford University");
    token.setBeginPosition(10);
    token.setEndPosition(18);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation(new HashMap<>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    List<CoreMap> sentenceList = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("This is Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> entityMentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(entityMentions);
    assertEquals(1, entityMentions.size());
    CoreMap mention = entityMentions.get(0);
    assertEquals("Stanford", mention.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Stanford University", mention.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    assertEquals("ORGANIZATION", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
  } 
}