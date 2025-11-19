public class EntityMentionsAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testEmptyAnnotation() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotator.annotate(annotation);
    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testSingleNamedEntityMention() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setOriginalText("Alice");
    token.setWord("Alice");
    token.setNER("PERSON");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Alice");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Alice");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());
    assertEquals("PERSON", mentions.get(0).get(CoreAnnotations.EntityTypeAnnotation.class));
    assertEquals("Alice", mentions.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMultipleMentionsDifferentTypes() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("Google");
    token1.setWord("Google");
    token1.setNER("ORGANIZATION");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Google");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("Monday");
    token2.setWord("Monday");
    token2.setNER("DATE");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token2.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");
    token2.setBeginPosition(7);
    token2.setEndPosition(13);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Google Monday");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(2, mentions.size());

    Set<String> types = new HashSet<>();
    for (CoreMap m : mentions) {
      types.add(m.get(CoreAnnotations.EntityTypeAnnotation.class));
    }

    assertTrue(types.contains("ORGANIZATION"));
    assertTrue(types.contains("DATE"));
  }
@Test
  public void testDetermineEntityMentionConfidencesWithNull() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(entityMention);
    assertNull(result);
  }
@Test
  public void testDetermineEntityMentionConfidencesWithValidProbs() {
    CoreLabel token1 = new CoreLabel();
    Map<String, Double> probs1 = new HashMap<>();
    probs1.put("PERSON", 0.9);
    probs1.put("ORGANIZATION", 0.2);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs1);

    CoreLabel token2 = new CoreLabel();
    Map<String, Double> probs2 = new HashMap<>();
    probs2.put("PERSON", 0.7);
    probs2.put("ORGANIZATION", 0.5);
    token2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs2);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap entityMention = new ArrayCoreMap();
    entityMention.set(CoreAnnotations.TokensAnnotation.class, tokens);

    HashMap<String, Double> result = EntityMentionsAnnotator.determineEntityMentionConfidences(entityMention);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(Double.valueOf(0.7), result.get("PERSON"));
    assertEquals(Double.valueOf(0.2), result.get("ORGANIZATION"));
  }
@Test
  public void testHandleKbpPronominalMentionMale() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token = new CoreLabel();
    token.setWord("he");
    token.setOriginalText("he");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("he");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);
    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    boolean foundHe = false;
    for (CoreMap m : mentions) {
      if ("he".equalsIgnoreCase(m.get(CoreAnnotations.TextAnnotation.class))) {
        foundHe = true;
        assertEquals("MALE", m.get(CoreAnnotations.GenderAnnotation.class));
        assertEquals("PERSON", m.get(CoreAnnotations.EntityTypeAnnotation.class));
      }
    }
    assertTrue(foundHe);
  }
@Test
  public void testHandleNoMentions() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("The");
    token1.setOriginalText("The");
    token1.setNER("O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("cat");
    token2.setOriginalText("cat");
    token2.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = Collections.singletonList(sentence);
    Annotation annotation = new Annotation("The cat");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testOverlappingMentionDetection() {
    CoreLabel token1 = new CoreLabel();
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    CoreLabel token2 = new CoreLabel();
    token2.setBeginPosition(6);
    token2.setEndPosition(10);

    CoreMap needle = new ArrayCoreMap();
    needle.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    CoreLabel h1 = new CoreLabel();
    h1.setBeginPosition(2);
    h1.setEndPosition(8);
    CoreMap hayMention1 = new ArrayCoreMap();
    hayMention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(h1));

    List<CoreMap> haystack = Collections.singletonList(hayMention1);
    Optional<CoreMap> result = invokeOverlapsWithMention(needle, haystack);

    assertTrue(result.isPresent());
    assertEquals(hayMention1, result.get());
  }
@Test
  public void testEntityMentionWithTimex() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

    Timex timex = new Timex("t1");
    CoreLabel token = new CoreLabel();
    token.setWord("Monday");
    token.setOriginalText("Monday");
    token.setNER("DATE");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-01-01");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.setBeginPosition(0);
    token.setEndPosition(6);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);

    Annotation annotation = new Annotation("Monday");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
    assertEquals(1, mentions.size());
    assertEquals("t1", mentions.get(0).get(TimeAnnotations.TimexAnnotation.class).tid());
  } 
}