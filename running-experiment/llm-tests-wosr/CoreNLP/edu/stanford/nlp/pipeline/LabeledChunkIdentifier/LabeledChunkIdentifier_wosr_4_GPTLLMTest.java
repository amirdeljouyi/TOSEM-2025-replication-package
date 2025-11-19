public class LabeledChunkIdentifier_wosr_4_GPTLLMTest { 

 @Test
  public void testSingleIOBChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Bill");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "went");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    tokens.add(token1);
    tokens.add(token2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, chunkTokens.size());
    assertEquals("Bill", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(0, (int)chunk.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(1, (int)chunk.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testContiguousChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "City");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("LOC", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("New York City", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(0, (int)chunk.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(3, (int)chunk.get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testOOnlyTokensReturnNoChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "World");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");

    tokens.add(token1);
    tokens.add(token2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }
@Test
  public void testNullLabelDefaultsHandled() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "EntityOne");
    token1.set(CoreAnnotations.AnswerAnnotation.class, null);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "EntityTwo");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");

    tokens.add(token1);
    tokens.add(token2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("LOC", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("EntityTwo", chunk.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testOverlappingChunksThrowsException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "B");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");

    tokens.add(token1);
    tokens.add(token2);

    try {
      identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
      fail("Expected RuntimeException not thrown");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("prev chunk not ended yet"));
    }
  }
@Test
  public void testIgnoreProvidedTagWithCustomTags() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    identifier.setNegLabel("O");
    identifier.setDefaultNegTag("O");
    identifier.setDefaultPosTag("I");

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Paris");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "X-LOC");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");

    tokens.add(token1);
    tokens.add(token2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("LOC", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testSettersAndGetters() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultNegTag("Z");
    identifier.setDefaultPosTag("Y");
    identifier.setNegLabel("XXX");
    identifier.setIgnoreProvidedTag(true);

    assertEquals("Z", identifier.getDefaultNegTag());
    assertEquals("Y", identifier.getDefaultPosTag());
    assertEquals("XXX", identifier.getNegLabel());
    assertTrue(identifier.isIgnoreProvidedTag());
  }
@Test
  public void testLabelWithoutDashPattern() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType type = identifier.getTagType("PER");

    assertEquals("PER", type.label);
    assertEquals("I", type.tag);
    assertEquals("PER", type.type);
  }
@Test
  public void testIOEStyleChunking() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "E-ORG");

    tokens.add(token1);
    tokens.add(token2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("Alpha Beta", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testMixedSBEIO() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Ann");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "S-PER");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "met");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "John");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "Smith");
    token4.set(CoreAnnotations.AnswerAnnotation.class, "E-PER");

    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(2, chunks.size());
    assertEquals("Ann", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("John Smith", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  } 
}