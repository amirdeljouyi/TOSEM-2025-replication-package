public class LabeledChunkIdentifier_wosr_1_GPTLLMTest { 

 @Test
  public void testSingleTokenChunk_IOB2() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Bill");
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    tokens.add(token);

    List<CoreMap> chunks = chunker.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Bill", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(token, chunk.get(CoreAnnotations.TokensAnnotation.class).get(0));
  }
@Test
  public void testMultiTokenChunk_IOB2() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Xerox");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(tok1);

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Corp");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    tokens.add(tok2);

    List<CoreMap> chunks = chunker.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Xerox Corp", chunk.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMultipleChunks_IOB2() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Alice");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(tok1);

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "works");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    tokens.add(tok2);

    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "at");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    tokens.add(tok3);

    CoreLabel tok4 = new CoreLabel();
    tok4.set(CoreAnnotations.TextAnnotation.class, "IBM");
    tok4.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    tok4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    tokens.add(tok4);

    List<CoreMap> chunks = chunker.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertEquals(2, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testNoChunksPresent() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(tok1);

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "World");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    tokens.add(tok2);

    List<CoreMap> chunks = chunker.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);

    assertTrue(chunks.isEmpty());
  }
@Test
  public void testNullLabel() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    String nullLabel = null;
    LabeledChunkIdentifier.LabelTagType result = chunker.getTagType(nullLabel);

    assertEquals("O", result.label);
    assertEquals("O", result.tag);
    assertEquals("O", result.type);
  }
@Test
  public void testIgnoreProvidedTag() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();
    chunker.setIgnoreProvidedTag(true);

    String label = "B-ORG";
    LabeledChunkIdentifier.LabelTagType result = chunker.getTagType(label);

    assertEquals("B-ORG", result.label);
    assertEquals("I", result.tag);
    assertEquals("ORG", result.type);
  }
@Test
  public void testCustomTags() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();
    chunker.setNegLabel("NONE");
    chunker.setDefaultPosTag("P");
    chunker.setDefaultNegTag("N");

    LabeledChunkIdentifier.LabelTagType result = chunker.getTagType("ENTITY");

    assertEquals("ENTITY", result.label);
    assertEquals("P", result.tag);
    assertEquals("ENTITY", result.type);
  }
@Test
  public void testIncompatibilityBreaksChunk() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "San");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    tokens.add(tok1);

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Francisco");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    tokens.add(tok2);

    Predicate<Pair<CoreLabel, CoreLabel>> incompatible = p -> {
      CoreLabel current = p.first;
      CoreLabel prev = p.second;
      return !("Francisco".equals(current.get(CoreAnnotations.TextAnnotation.class)));
    };

    List<CoreMap> chunks = chunker.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class,
        incompatible);

    assertEquals(1, chunks.size());
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testChunkStartWithoutPreviousEndThrowsException() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(tok1);

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Inc");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    tokens.add(tok2);

    chunker.getAnnotatedChunks(tokens, 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
  }
@Test
  public void testTypeMismatchTriggersNewChunk() {
    LabeledChunkIdentifier.LabelTagType prev = new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    LabeledChunkIdentifier.LabelTagType cur = new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");

    boolean end = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    boolean start = LabeledChunkIdentifier.isStartOfChunk(prev, cur);

    assertTrue(end);
    assertTrue(start);
  }
@Test
  public void testDotTagTreatedAsNoneChunk() {
    LabeledChunkIdentifier chunker = new LabeledChunkIdentifier();

    LabeledChunkIdentifier.LabelTagType t = new LabeledChunkIdentifier.LabelTagType(".", ".", "PER");

    assertFalse(isChunk(t));
  } 
}