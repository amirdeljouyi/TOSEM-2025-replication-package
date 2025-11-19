public class WordsToSentencesAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testSingleSentenceSplitting() {
    Annotation annotation = new Annotation("This is a sentence.");
    
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "This");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "a");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "sentence");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    tokens.add(token4);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, ".");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    tokens.add(token5);

    annotation.set(CoreAnnotations.TextAnnotation.class, "This is a sentence.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("This is a sentence.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMultipleSentencesSplitting() {
    Annotation annotation = new Annotation("This is one. This is two.");

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "This");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    tokens.add(t1);

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "is");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    tokens.add(t2);

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "one");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    tokens.add(t3);

    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.TextAnnotation.class, ".");
    t4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    t4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    tokens.add(t4);

    CoreLabel t5 = new CoreLabel();
    t5.set(CoreAnnotations.TextAnnotation.class, "This");
    t5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    t5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    tokens.add(t5);

    CoreLabel t6 = new CoreLabel();
    t6.set(CoreAnnotations.TextAnnotation.class, "is");
    t6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    t6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    tokens.add(t6);

    CoreLabel t7 = new CoreLabel();
    t7.set(CoreAnnotations.TextAnnotation.class, "two");
    t7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 21);
    t7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    tokens.add(t7);

    CoreLabel t8 = new CoreLabel();
    t8.set(CoreAnnotations.TextAnnotation.class, ".");
    t8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 24);
    t8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    tokens.add(t8);

    annotation.set(CoreAnnotations.TextAnnotation.class, "This is one. This is two.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("This is one.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("This is two.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNoTokensThrowsException() {
    Annotation annotation = new Annotation("Hello");

    boolean thrown = false;

    try {
      WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
      annotator.annotate(annotation);
    } catch (IllegalArgumentException e) {
      thrown = true;
      assertTrue(e.getMessage().contains("unable to find words"));
    }
    assertTrue(thrown);
  }
@Test
  public void testEmptySentenceWithLineNumbersEnabled() {
    CoreLabel tokenNewline = new CoreLabel();
    tokenNewline.set(CoreAnnotations.TextAnnotation.class, "\n");
    tokenNewline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tokenNewline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    tokenNewline.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    tokenNewline.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tokenNewline);

    Annotation annotation = new Annotation("\n");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "\n");

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(0, sentences.size());
    List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, resultTokens.size());
  }
@Test
  public void testNewlineTokenFollowedByRegularSentence() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "\n");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Hi");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Hi");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    Annotation annotation = new Annotation("\nHi.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "\nHi.");

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("Hi.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNonSplitterBehavior() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ",");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, ",");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "world");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "world");

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "!");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "!");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);

    Annotation annotation = new Annotation("Hello, world!");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello, world!");

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("Hello, world!", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDuplicateAnnotateCallLogsErrorOnce() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Test");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "Test");

    Annotation annotation = new Annotation("Test");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Test");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  } 
}