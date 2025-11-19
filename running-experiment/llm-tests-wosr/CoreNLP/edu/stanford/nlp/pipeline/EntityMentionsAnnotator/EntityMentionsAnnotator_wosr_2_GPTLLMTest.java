public class EntityMentionsAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testDefaultConstructorCreatesAnnotator() {
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    assertNotNull(annotator);
    Set<Class<? extends CoreAnnotation>> requiredAnnotations = annotator.requires();
    assertTrue(requiredAnnotations.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requiredAnnotations.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requiredAnnotations.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testCustomConstructorWithAcronymProperty() {
    Properties props = new Properties();
    props.setProperty("ner.acronyms", "true");
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("ner", props);
    assertNotNull(annotator);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testEmptyAnnotationResultsInNoMentions() {
    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);
    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertTrue(mentions.isEmpty());
  }
@Test
  public void testSingleNamedEntityProducesMention() {
    Annotation doc = new Annotation("John went home.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.TextAnnotation.class, "John");
    token1.setBeginPosition(0);
    token1.setEndPosition(4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("went");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token2.setBeginPosition(5);
    token2.setEndPosition(9);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("home");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token3.setBeginPosition(10);
    token3.setEndPosition(14);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token4.setBeginPosition(14);
    token4.setEndPosition(15);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    CoreMap sentence = new Annotation("John went home.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertEquals(1, mentions.size());

    CoreMap mention = mentions.get(0);
    assertEquals("John", mention.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PERSON", mention.get(CoreAnnotations.EntityTypeAnnotation.class));
  }
@Test
  public void testAnnotateWithPronominalMentionHe() {
    Annotation doc = new Annotation("He is here.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("He");
    token1.setBeginPosition(0);
    token1.setEndPosition(2);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setBeginPosition(3);
    token2.setEndPosition(5);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("here");
    token3.setBeginPosition(6);
    token3.setEndPosition(10);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setBeginPosition(10);
    token4.setEndPosition(11);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    CoreMap sentence = new Annotation("He is here.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    boolean hasPronoun = false;
    for (CoreMap m : mentions) {
      if ("He".equalsIgnoreCase(m.get(CoreAnnotations.TextAnnotation.class))) {
        hasPronoun = true;
        assertEquals("PERSON", m.get(CoreAnnotations.EntityTypeAnnotation.class));
        assertEquals("MALE", m.get(CoreAnnotations.GenderAnnotation.class));
      }
    }
    assertTrue(hasPronoun);
  }
@Test
  public void testMentionHasConfidenceScores() {
    Annotation doc = new Annotation("London is big.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("London");
    token1.setNER("LOCATION");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token1.setBeginPosition(0);
    token1.setEndPosition(6);

    Map<String, Double> probsMap = new HashMap<>();
    probsMap.put("LOCATION", 0.85);
    probsMap.put("PERSON", 0.05);
    token1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probsMap);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setNER("O");
    token2.setBeginPosition(7);
    token2.setEndPosition(9);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("big");
    token3.setNER("O");
    token3.setBeginPosition(10);
    token3.setEndPosition(13);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setNER("O");
    token4.setBeginPosition(13);
    token4.setEndPosition(14);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    CoreMap sentence = new Annotation("London is big.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    assertFalse(mentions.isEmpty());

    CoreMap mention = mentions.get(0);
    Map<String, Double> probs = mention.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertNotNull(probs);
    assertEquals(Double.valueOf(0.85), probs.get("LOCATION"));
    assertEquals(Double.valueOf(0.05), probs.get("PERSON"));
  }
@Test
  public void testTimexMentionHandledCorrectly() {
    Annotation doc = new Annotation("Today is sunny.");
    Timex timex = new Timex("t0", "2024-01-01");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Today");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "DATE");
    token1.set(TimeAnnotations.TimexAnnotation.class, timex);
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token2.setBeginPosition(6);
    token2.setEndPosition(8);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("sunny");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token3.setBeginPosition(9);
    token3.setEndPosition(14);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token4.setBeginPosition(14);
    token4.setEndPosition(15);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    CoreMap sentence = new Annotation("Today is sunny.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
    annotator.annotate(doc);

    List<CoreMap> mentions = doc.get(CoreAnnotations.MentionsAnnotation.class);
    assertNotNull(mentions);
    boolean foundTimex = false;
    for (CoreMap m : mentions) {
      Timex t = m.get(TimeAnnotations.TimexAnnotation.class);
      if (t != null && "t0".equals(t.tid())) {
        foundTimex = true;
        break;
      }
    }
    assertTrue(foundTimex);
  }
@Test
  public void testMentionOverlappingDetection() {
    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention1 = new Annotation("IBM");
    mention1.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("IB");
    token2.setBeginPosition(0);
    token2.setEndPosition(2);
    List<CoreLabel> tokens2 = new ArrayList<>();
    tokens2.add(token2);

    CoreMap mention2 = new Annotation("IB");
    mention2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

    List<CoreMap> haystack = new ArrayList<>();
    haystack.add(mention1);

    Optional<CoreMap> overlap = EntityMentionsAnnotator.overlapsWithMention(mention2, haystack);
    assertTrue(overlap.isPresent());
  } 
}