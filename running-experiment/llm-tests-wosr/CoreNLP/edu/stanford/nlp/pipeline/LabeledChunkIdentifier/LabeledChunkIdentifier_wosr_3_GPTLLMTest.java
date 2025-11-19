public class LabeledChunkIdentifier_wosr_3_GPTLLMTest { 

 @Test
  public void testEmptyTokenListReturnsEmptyChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();
    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertNotNull(chunks);
    assertTrue(chunks.isEmpty());
  }
@Test
  public void testSingleTokenWithOProducesNoChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.AnswerAnnotation.class, "O");

    List<CoreMap> chunks = identifier.getAnnotatedChunks(Collections.singletonList(token), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertNotNull(chunks);
    assertTrue(chunks.isEmpty());
  }
@Test
  public void testSingleTokenWithBLabelCreatesChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "PersonX");
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, chunkTokens.size());
    assertEquals("PersonX", chunkTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testChunkSpanningMultipleTokensWithIOB2() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "John");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "works");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, chunkTokens.size());
    assertEquals("John", chunkTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Smith", chunkTokens.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testMultipleChunksWithDifferentTypes() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "NYC");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(2, chunks.size());
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("LOC", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testChunkEndsOnTypeChange() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "San");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Francisco");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(2, chunks.size());
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testIgnoreProvidedTagTreatsEverythingAsDefault() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    identifier.setDefaultNegTag("O");
    identifier.setDefaultPosTag("I");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Google");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Inc");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "is");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, chunkTokens.size());
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testGetTagTypeWithNullReturnsDefault() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultNegTag("O");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType(null);
    assertEquals("O", tagType.label);
    assertEquals("O", tagType.tag);
    assertEquals("O", tagType.type);
  }
@Test
  public void testGetTagTypeWithoutDashUsesDefaultTags() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultNegTag("O");
    identifier.setDefaultPosTag("I");
    LabeledChunkIdentifier.LabelTagType tag1 = identifier.getTagType("O");
    assertEquals("O", tag1.tag);
    LabeledChunkIdentifier.LabelTagType tag2 = identifier.getTagType("PER");
    assertEquals("I", tag2.tag);
  }
@Test
  public void testBackToBackChunksHandledCorrectly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Microsoft");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Apple");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(2, chunks.size());
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testPredicateBreaksChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Google");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "LLC");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);

    Predicate<Pair<CoreLabel, CoreLabel>> incompatible = pair -> false;

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.AnswerAnnotation.class,
        null,
        null,
        incompatible);

    assertEquals(2, chunks.size());
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testOverlappingChunksThrowException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "start");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "middle");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "new");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    tokens.add(t3);

    identifier.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.AnswerAnnotation.class);
  } 
}