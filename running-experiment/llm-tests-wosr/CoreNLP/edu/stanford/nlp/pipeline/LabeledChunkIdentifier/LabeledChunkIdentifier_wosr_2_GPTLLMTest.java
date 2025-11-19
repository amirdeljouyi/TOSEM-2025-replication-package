public class LabeledChunkIdentifier_wosr_2_GPTLLMTest { 

 @Test
  public void testGetAnnotatedChunks_IOB2_TwoChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Bill");
    token0.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    tokens.add(token0);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "gives");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Xerox");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Company");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    tokens.add(token3);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(2, chunks.size());

    CoreMap chunk0 = chunks.get(0);
    List<CoreLabel> chunk0Tokens = chunk0.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("Bill", chunk0Tokens.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(0, (int) chunk0.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(1, (int) chunk0.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals("PER", chunk0.get(CoreAnnotations.AnswerAnnotation.class));

    CoreMap chunk1 = chunks.get(1);
    List<CoreLabel> chunk1Tokens = chunk1.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("Xerox", chunk1Tokens.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Company", chunk1Tokens.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(2, (int) chunk1.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(4, (int) chunk1.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals("ORG", chunk1.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testGetAnnotatedChunks_IO_SingleChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Corp");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    tokens.add(t2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, chunkTokens.size());
    assertEquals("IBM", chunkTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Corp", chunkTokens.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testEmptyTokenListReturnsEmptyChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();
    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }
@Test(expected = RuntimeException.class)
  public void testOverlappingChunksThrowsException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "York");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    tokens.add(t2);

    identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
  }
@Test
  public void testNullLabelHandledGracefully() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "UNKNOWN");
    token.set(CoreAnnotations.AnswerAnnotation.class, null);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    tokens.add(token);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }
@Test
  public void testIgnoreProvidedTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    identifier.setNegLabel("O");
    identifier.setDefaultPosTag("I");
    identifier.setDefaultNegTag("O");

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Google");
    token.set(CoreAnnotations.AnswerAnnotation.class, "RANDOM-ORG");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    tokens.add(token);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testGetTagTypeFallbacks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    LabeledChunkIdentifier.LabelTagType result1 = identifier.getTagType("B-LOC");
    assertEquals("B", result1.tag);
    assertEquals("LOC", result1.type);

    LabeledChunkIdentifier.LabelTagType result2 = identifier.getTagType("O");
    assertEquals("O", result2.tag);
    assertEquals("O", result2.type);

    LabeledChunkIdentifier.LabelTagType result3 = identifier.getTagType(null);
    assertEquals("O", result3.tag);
    assertEquals("O", result3.type);
  }
@Test
  public void testSingleTokenChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel single = new CoreLabel();
    single.set(CoreAnnotations.TextAnnotation.class, "Joe");
    single.set(CoreAnnotations.AnswerAnnotation.class, "U-PER");
    single.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    single.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokens.add(single);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, chunkTokens.size());
    assertEquals("Joe", chunkTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMixedChunksAndNonChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t0 = new CoreLabel();
    t0.set(CoreAnnotations.TextAnnotation.class, "Apple");
    t0.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(t0);

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "is");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "good");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    tokens.add(t2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, chunkTokens.size());
    assertEquals("Apple", chunkTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
  } 
}