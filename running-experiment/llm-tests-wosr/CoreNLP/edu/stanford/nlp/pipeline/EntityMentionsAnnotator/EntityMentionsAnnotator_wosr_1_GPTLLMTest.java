public class EntityMentionsAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testAnnotate_WithSingleNamedEntity() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Stanford University");
    token.setBeginPosition(0);
    token.setEndPosition(8);

    ArrayList<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> entityMentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(entityMentions);
    assertEquals(1, entityMentions.size());
    CoreMap entity = entityMentions.get(0);
    assertEquals("ORGANIZATION", entity.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("Stanford University", entity.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    assertEquals("Stanford", entity.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotate_WithNoNER_ShouldProduceNoMention() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("home");
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.setBeginPosition(0);
    token.setEndPosition(4);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("home");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(0, mentions.size());
  }
@Test
  public void testAnnotate_WithPronominalMention_She() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("She");
    token.setNER("O");
    token.setBeginPosition(0);
    token.setEndPosition(3);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("She");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    String gender = mentions.get(0).get(CoreAnnotations.GenderAnnotation.class);
    assertEquals("FEMALE", gender);
  }
@Test
  public void testAnnotate_WithMultipleNERSpans() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "John Smith");
    token1.setBeginPosition(0);
    token1.setEndPosition(4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "John Smith");
    token2.setBeginPosition(5);
    token2.setEndPosition(10);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("John Smith");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    CoreMap mention = mentions.get(0);
    assertEquals("PERSON", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("John Smith", mention.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDetermineEntityMentionConfidences_MultipleTokens() {
    CoreLabel tokenA = new CoreLabel();
    Map<String, Double> probsA = new HashMap<>();
    probsA.put("PERSON", 0.9);
    tokenA.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probsA);

    CoreLabel tokenB = new CoreLabel();
    Map<String, Double> probsB = new HashMap<>();
    probsB.put("PERSON", 0.6);
    tokenB.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probsB);

    List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB);

    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(0.6, result.get("PERSON"), 0.0001);
  }
@Test
  public void testDetermineEntityMentionConfidences_MissingProbs() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(entityMention);
    assertNull(result);
  }
@Test
  public void testAnnotate_WithWikipediaEntity() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setNER("PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Barack_Obama");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("Obama");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    String wiki = mentions.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("Barack_Obama", wiki);
  }
@Test
  public void testAcronymExpansion_NotTriggeredWhenDoAcronymsFalse() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("NASA");
    token.setNER("O");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token.setBeginPosition(0);
    token.setEndPosition(4);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("NASA");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    List<CoreMap> entityMentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertTrue(entityMentions.isEmpty());
  } 
}