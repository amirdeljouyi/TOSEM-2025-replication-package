public class WordsToSentencesAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testSimpleSentenceSplitDefault() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setValue("This");
    token1.setOriginalText("This");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setValue(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token2);

    Annotation annotation = new Annotation("This.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("This.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNonSplitterReturnsOneSentence() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setOriginalText("Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.setOriginalText("world");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token2);

    Annotation annotation = new Annotation("Hello world");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello world");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("Hello world", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotationWithoutTokensThrowsException() {
    Annotation annotation = new Annotation("Missing tokens only text");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Missing tokens only text");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    boolean thrown = false;
    try {
      annotator.annotate(annotation);
    } catch (IllegalArgumentException e) {
      thrown = true;
      assertTrue(e.getMessage().contains("unable to find words"));
    }
    assertTrue(thrown);
  }
@Test
  public void testNewlineSplittingWithEmptyLine() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Line1");
    token1.setOriginalText("Line1");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel newLine = new CoreLabel();
    newLine.setWord("\n");
    newLine.setOriginalText("\n");
    newLine.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Line2");
    token2.setOriginalText("Line2");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(newLine);
    tokens.add(token2);

    String text = "Line1\nLine2";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    assertEquals("Line1", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Line2", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(1, sentences.get(0).get(CoreAnnotations.LineNumberAnnotation.class).intValue());
    assertEquals(2, sentences.get(1).get(CoreAnnotations.LineNumberAnnotation.class).intValue());
  }
@Test
  public void testMultipleSentenceSplitWithDotDelim() {
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.setOriginalText("First");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Second");
    token3.setOriginalText("Second");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setOriginalText(".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokens.add(token4);

    String text = "First. Second.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    assertEquals("First.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Second.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testOneSentenceMode() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setOriginalText("Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    String text = "Hello.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "true");
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("Hello.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testTokenIndicesSetProperly() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Test");
    token1.setOriginalText("Test");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("case");
    token2.setOriginalText("case");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    String text = "Test case";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, (int) sentenceTokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(1, (int) sentenceTokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(1, (int) sentenceTokens.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(2, (int) sentenceTokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  } 
}