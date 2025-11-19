public class EntityMentionsAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testAnnotateWithSimpleNERTag() {
    Annotation annotation = new Annotation("Barack Obama was born in Hawaii.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setNER("PERSON");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Barack");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setNER("PERSON");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Obama");
    token2.setBeginPosition(7);
    token2.setEndPosition(12);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("was");
    token3.setNER("O");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token3.setBeginPosition(13);
    token3.setEndPosition(16);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("born");
    token4.setNER("O");
    token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token4.setBeginPosition(17);
    token4.setEndPosition(21);

    CoreLabel token5 = new CoreLabel();
    token5.setWord("in");
    token5.setNER("O");
    token5.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token5.setBeginPosition(22);
    token5.setEndPosition(24);

    CoreLabel token6 = new CoreLabel();
    token6.setWord("Hawaii");
    token6.setNER("LOCATION");
    token6.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token6.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Hawaii");
    token6.setBeginPosition(25);
    token6.setEndPosition(31);

    CoreLabel token7 = new CoreLabel();
    token7.setWord(".");
    token7.setNER("O");
    token7.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token7.setBeginPosition(31);
    token7.setEndPosition(32);

    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);
    tokens.add(token5);
    tokens.add(token6);
    tokens.add(token7);

    CoreMap sentence = new Annotation("Barack Obama was born in Hawaii.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(2, mentions.size());

    CoreMap mention1 = mentions.get(0);
    List<CoreLabel> mention1Tokens = mention1.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("Barack", mention1Tokens.get(0).word());
    assertEquals("Obama", mention1Tokens.get(1).word());
    assertEquals("PERSON", mention1.get(CoreAnnotations.EntityTypeAnnotation.class));

    CoreMap mention2 = mentions.get(1);
    List<CoreLabel> mention2Tokens = mention2.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("Hawaii", mention2Tokens.get(0).word());
    assertEquals("LOCATION", mention2.get(CoreAnnotations.EntityTypeAnnotation.class));
  }
@Test
  public void testAnnotateMentionsWithNoNER() {
    Annotation annotation = new Annotation("This is a test.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setNER("O");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token1.setBeginPosition(0);
    token1.setEndPosition(4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setNER("O");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token2.setBeginPosition(5);
    token2.setEndPosition(7);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("a");
    token3.setNER("O");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token3.setBeginPosition(8);
    token3.setEndPosition(9);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("test");
    token4.setNER("O");
    token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token4.setBeginPosition(10);
    token4.setEndPosition(14);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    token5.setNER("O");
    token5.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token5.setBeginPosition(14);
    token5.setEndPosition(15);

    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);
    tokens.add(token5);

    CoreMap sentence = new Annotation("This is a test.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
    assertTrue(mentions == null || mentions.isEmpty());
  }
@Test
  public void testDetermineEntityMentionConfidences_AllTokensHaveProbabilities() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("PERSON", 0.8);
    probs1.put("LOCATION", 0.6);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("PERSON", 0.4);  
    probs2.put("LOCATION", 0.9);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> mentionTokens = Arrays.asList(token1, token2);

    CoreMap mention = new Annotation("Barack Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, mentionTokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNotNull(result);
    assertEquals(0.4, result.get("PERSON"), 0.0001);
    assertEquals(0.6, result.get("LOCATION"), 0.0001);
  }
@Test
  public void testDetermineEntityMentionConfidences_SomeMissingProbs() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);

    List<CoreLabel> tokens = Arrays.asList(token1);

    CoreMap mention = new Annotation("Test");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(mention);
    assertNull(result);
  }
@Test
  public void testOverlappingMentions() {
    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    CoreLabel token2 = new CoreLabel();
    token2.setBeginPosition(6);
    token2.setEndPosition(10);

    CoreMap needle = new Annotation("Obama");
    needle.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    CoreLabel token3 = new CoreLabel();
    token3.setBeginPosition(6);
    token3.setEndPosition(12);

    CoreMap hayMention1 = new Annotation("Obama");
    hayMention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token3));

    List<CoreMap> haystack = Arrays.asList(hayMention1);

    Optional<CoreMap> result = EntityMentionsAnnotator.overlapsWithMention(needle, haystack);

    assertTrue(result.isPresent());
    assertEquals(hayMention1, result.get());
  }
@Test
  public void testAnnotatorWithCustomNERCoreAnnotationClass() {
    Properties props = new Properties();
    props.setProperty("em.nerCoreAnnotation", CoreAnnotations.NamedEntityTagAnnotation.class.getName());
    props.setProperty("em.mentionsCoreAnnotation", CoreAnnotations.MentionsAnnotation.class.getName());
    props.setProperty("em.nerNormalizedCoreAnnotation", CoreAnnotations.NormalizedNamedEntityTagAnnotation.class.getName());
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("em", props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  } 
}