public class LabeledChunkIdentifier_wosr_5_GPTLLMTest { 

 @Test
  public void testIOB2SingleChunkExtraction() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Bill");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "went");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tokens.add(t2);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("Bill", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, chunkTokens.size());
    assertEquals("Bill", chunkTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMultipleChunksWithMixedTypes() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    tokens.add(t2);

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "works");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tokens.add(t3);

    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.TextAnnotation.class, "at");
    t4.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tokens.add(t4);

    CoreLabel t5 = new CoreLabel();
    t5.set(CoreAnnotations.TextAnnotation.class, "Google");
    t5.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tokens.add(t5);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(2, chunks.size());

    CoreMap chunk1 = chunks.get(0);
    assertEquals("John Smith", chunk1.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunk1.get(CoreAnnotations.AnswerAnnotation.class));

    CoreMap chunk2 = chunks.get(1);
    assertEquals("Google", chunk2.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunk2.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testEmptyInputTokens() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }
@Test
  public void testChunkWithNullLabels() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.AnswerAnnotation.class, null);
    tokens.add(token);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }
@Test
  public void testGetTagTypeWithNullLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType(null);
    assertEquals("O", tagType.label);
    assertEquals("O", tagType.tag);
    assertEquals("O", tagType.type);
  }
@Test
  public void testGetTagTypeWithSingleLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("ORG");
    assertEquals("ORG", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("ORG", tagType.type);
  }
@Test
  public void testIgnoreProvidedTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);

    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("B-PER");

    assertEquals("B-PER", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("PER", tagType.type);
  }
@Test
  public void testIsEndOfChunkFromSameType() {
    LabeledChunkIdentifier.LabelTagType prev = new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    LabeledChunkIdentifier.LabelTagType cur = new LabeledChunkIdentifier.LabelTagType("O", "O", "O");

    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }
@Test
  public void testIsStartOfChunkWithNullPrev() {
    LabeledChunkIdentifier.LabelTagType cur = new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");

    boolean result = LabeledChunkIdentifier.isStartOfChunk(null, cur);
    assertTrue(result);
  }
@Test
  public void testIsChunkTrueWhenNotO() {
    LabeledChunkIdentifier.LabelTagType cur = new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(null, cur));
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(cur, new LabeledChunkIdentifier.LabelTagType("O", "O", "O")));
  }
@Test(expected = RuntimeException.class)
  public void testChunkStartBeforeEndThrowsException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "York");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    tokens.add(t2);

    identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
  }
@Test
  public void testCompatiblePredicateBlocksChunking() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    tokens.add(t2);

    Predicate<Pair<CoreLabel, CoreLabel>> predicate = pair -> false;

    List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.AnswerAnnotation.class, predicate);

    assertEquals(2, chunks.size());
    assertEquals("Alpha", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Beta", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDefaultPosAndNegTagConfiguration() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultPosTag("XYZ");
    identifier.setDefaultNegTag("ABC");
    identifier.setNegLabel("NOT");

    LabeledChunkIdentifier.LabelTagType pos = identifier.getTagType("LABEL-ENT");
    assertEquals("XYZ", pos.tag);

    LabeledChunkIdentifier.LabelTagType neg = identifier.getTagType("NOT");
    assertEquals("ABC", neg.tag);
  } 
}