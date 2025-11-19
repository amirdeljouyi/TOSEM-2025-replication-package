public class WordsToSentencesAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testAnnotate_singleSentence() {
    Annotation annotation = new Annotation("Hello world.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.BeforeAnnotation.class, "");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.set(CoreAnnotations.TextAnnotation.class, "world");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "world");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token2.set(CoreAnnotations.AfterAnnotation.class, "");

    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token3.set(CoreAnnotations.BeforeAnnotation.class, "");
    token3.set(CoreAnnotations.AfterAnnotation.class, "");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello world.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertEquals("Hello world.", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(0, sentence.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class).intValue());
    assertEquals(12, sentence.get(CoreAnnotations.CharacterOffsetEndAnnotation.class).intValue());
    assertEquals(0, sentence.get(CoreAnnotations.SentenceIndexAnnotation.class).intValue());
    assertEquals(0, sentence.get(CoreAnnotations.TokenBeginAnnotation.class).intValue());
    assertEquals(3, sentence.get(CoreAnnotations.TokenEndAnnotation.class).intValue());
  }
@Test
  public void testAnnotate_emptyTokens_throwsException() {
    Annotation annotation = new Annotation("Empty");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Empty");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    try {
      annotator.annotate(annotation);
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("unexpected empty sentence"));
    }
  }
@Test
  public void testAnnotate_missingTokensAnnotation_throwsException() {
    Annotation annotation = new Annotation("Missing tokens");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Missing tokens");

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    try {
      annotator.annotate(annotation);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unable to find words/tokens"));
    }
  }
@Test
  public void testNewlineSplitter_ignoresEmptySentences() {
    Annotation annotation = new Annotation("Line one.\n\nLine two.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Line");
    token1.set(CoreAnnotations.TextAnnotation.class, "Line");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Line");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("one");
    token2.set(CoreAnnotations.TextAnnotation.class, "one");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "one");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel newline1 = new CoreLabel();
    newline1.setWord("\n");
    newline1.set(CoreAnnotations.TextAnnotation.class, "\n");
    newline1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    newline1.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel newline2 = new CoreLabel();
    newline2.setWord("\n");
    newline2.set(CoreAnnotations.TextAnnotation.class, "\n");
    newline2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    newline2.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Line");
    token3.set(CoreAnnotations.TextAnnotation.class, "Line");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "Line");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("two");
    token4.set(CoreAnnotations.TextAnnotation.class, "two");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "two");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(newline1);
    tokens.add(newline2);
    tokens.add(token3);
    tokens.add(token4);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Line one.\n\nLine two.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals(1, sentences.get(0).get(CoreAnnotations.LineNumberAnnotation.class).intValue());
    assertEquals(3, sentences.get(1).get(CoreAnnotations.LineNumberAnnotation.class).intValue());
  }
@Test
  public void testNonSplitter_noSplittingOccurs() {
    Annotation annotation = new Annotation("Just one line.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Just");
    token1.set(CoreAnnotations.TextAnnotation.class, "Just");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Just");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("one");
    token2.set(CoreAnnotations.TextAnnotation.class, "one");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "one");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("line");
    token3.set(CoreAnnotations.TextAnnotation.class, "line");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "line");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Just one line.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sent = sentences.get(0);
    assertEquals("Just one line.", sent.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(4, sent.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals(0, sent.get(CoreAnnotations.SentenceIndexAnnotation.class).intValue());
  }
@Test
  public void testAnnotate_alreadySplit_returnsEarly() {
    Annotation annotation = new Annotation("Repeat test.");
    CoreLabel token = new CoreLabel();
    token.setWord("Repeat");
    token.set(CoreAnnotations.TextAnnotation.class, "Repeat");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(new Annotation("Repeat"));

    annotation.set(CoreAnnotations.TextAnnotation.class, "Repeat test.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);

    List<CoreMap> after = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, after.size());
    assertEquals("Repeat", after.get(0).get(CoreAnnotations.TextAnnotation.class));
  } 
}