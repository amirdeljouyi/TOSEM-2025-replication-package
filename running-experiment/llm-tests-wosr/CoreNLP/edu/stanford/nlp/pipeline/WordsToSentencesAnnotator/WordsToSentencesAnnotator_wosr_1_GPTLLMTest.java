public class WordsToSentencesAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testNonSplitterSingleSentence() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "world");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    tokens.add(token2);

    Annotation annotation = new Annotation("Hello world");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello world");

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertEquals("Hello world", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(0, sentence.get(CoreAnnotations.SentenceIndexAnnotation.class).intValue());
  }
@Test
  public void testNewlineSplitterSentenceSplitting() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token1);

    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    newline.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    newline.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline.setNewline(true);
    tokens.add(newline);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    tokens.add(token2);

    Annotation annotation = new Annotation("Hello\nworld");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello\nworld");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());

    CoreMap sentence1 = sentences.get(0);
    CoreMap sentence2 = sentences.get(1);

    assertEquals("Hello", sentence1.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("world", sentence2.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(1, sentence2.get(CoreAnnotations.SentenceIndexAnnotation.class).intValue());
  }
@Test
  public void testEmptyTokensThrowsException() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    Annotation annotation = new Annotation("No tokens");

    try {
      annotator.annotate(annotation);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unable to find words"));
    }
  }
@Test
  public void testMultipleAnnotationWarningTriggered() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token);

    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);
    annotator.annotate(annotation);  

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testSectionAnnotationsTransferredCorrectly() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Test");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreMap sectionStart = new Annotation("");
    sectionStart.set(CoreAnnotations.DocIDAnnotation.class, "doc1");
    token.set(CoreAnnotations.SectionStartAnnotation.class, sectionStart);
    tokens.add(token);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Test");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertEquals("doc1", sentence.get(CoreAnnotations.DocIDAnnotation.class));
  }
@Test
  public void testQuotedSectionHandling() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Quote");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token);

    Annotation annotation = new Annotation("Quote");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Quote");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation section = new Annotation("section");
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    Annotation quote = new Annotation("quote");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "TestAuthor");
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<Annotation>());

    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertTrue(sentence.get(CoreAnnotations.QuotedAnnotation.class));
    assertEquals("TestAuthor", sentence.get(CoreAnnotations.AuthorAnnotation.class));
  }
@Test
  public void testAnnotateWithDocIDPropagation() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    Annotation annotation = new Annotation("DocID test");
    annotation.set(CoreAnnotations.TextAnnotation.class, "DocID test");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "DocID");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "test");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    tokens.add(token2);

    annotation.set(CoreAnnotations.DocIDAnnotation.class, "docX");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals("docX", sentences.get(0).get(CoreAnnotations.DocIDAnnotation.class));
    List<CoreLabel> resultTokens = sentences.get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("docX", resultTokens.get(0).get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(1, resultTokens.get(0).getIndex());
    assertEquals(0, resultTokens.get(0).getSentIndex());
  }
@Test
  public void testAnnotationPreservesTokenIndices() {
    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();

    Annotation annotation = new Annotation("Index test");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Index test");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Index");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "test");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    tokens.add(token2);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    List<CoreLabel> updatedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, updatedTokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class).intValue());
    assertEquals(1, updatedTokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class).intValue());
    assertEquals(1, updatedTokens.get(0).getIndex().intValue());
  }
@Test
  public void testAnnotationSplitsMultipleSentences() {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "false");
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("World");
    token3.set(CoreAnnotations.TextAnnotation.class, "World");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    tokens.add(token4);

    Annotation annotation = new Annotation("Hello. World.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello. World.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    assertEquals("Hello.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("World.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  } 
}