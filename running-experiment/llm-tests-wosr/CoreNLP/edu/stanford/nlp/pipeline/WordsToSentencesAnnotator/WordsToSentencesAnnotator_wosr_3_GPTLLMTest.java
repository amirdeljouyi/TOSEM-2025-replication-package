public class WordsToSentencesAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testAnnotateWithSingleSentence() {
    String sentenceText = "This is a sentence.";
    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setValue("This");
    token1.set(CoreAnnotations.TextAnnotation.class, sentenceText);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setValue("is");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("a");
    token3.setValue("a");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("sentence");
    token4.setValue("sentence");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    token5.setValue(".");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    Annotation annotation = new Annotation(sentenceText);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, sentenceText);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertEquals("This is a sentence.", sentence.get(CoreAnnotations.TextAnnotation.class));
    List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, sentenceTokens.size());
  }
@Test
  public void testAnnotateWithMultipleSentencesDefault() {
    String text = "Hello world. This is a test.";

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Hello");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("world");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreLabel t3 = new CoreLabel();
    t3.setWord(".");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel t4 = new CoreLabel();
    t4.setWord("This");
    t4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    t4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    CoreLabel t5 = new CoreLabel();
    t5.setWord("is");
    t5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    t5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);

    CoreLabel t6 = new CoreLabel();
    t6.setWord("a");
    t6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 21);
    t6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreLabel t7 = new CoreLabel();
    t7.setWord("test");
    t7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    t7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

    CoreLabel t8 = new CoreLabel();
    t8.setWord(".");
    t8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 27);
    t8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());

    CoreMap sentence1 = sentences.get(0);
    assertEquals("Hello world.", sentence1.get(CoreAnnotations.TextAnnotation.class));

    CoreMap sentence2 = sentences.get(1);
    assertEquals("This is a test.", sentence2.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNonSplitterAlwaysReturnsOneSentence() {
    String text = "Sentence one. Sentence two. Sentence three.";

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Sentence");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("one");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel t3 = new CoreLabel();
    t3.setWord(".");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreLabel t4 = new CoreLabel();
    t4.setWord("Sentence");
    t4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    t4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreLabel t5 = new CoreLabel();
    t5.setWord("two");
    t5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    t5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);

    CoreLabel t6 = new CoreLabel();
    t6.setWord(".");
    t6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 26);
    t6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

    CoreLabel t7 = new CoreLabel();
    t7.setWord("Sentence");
    t7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 28);
    t7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 36);

    CoreLabel t8 = new CoreLabel();
    t8.setWord("three");
    t8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 37);
    t8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 42);

    CoreLabel t9 = new CoreLabel();
    t9.setWord(".");
    t9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 42);
    t9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 43);

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertEquals("Sentence one. Sentence two. Sentence three.",
                 sentence.get(CoreAnnotations.TextAnnotation.class));
    List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(9, sentenceTokens.size());
  }
@Test(expected = IllegalArgumentException.class)
  public void testAnnotateThrowsOnMissingTokensAnnotation() {
    String text = "Nothing to split here.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);
  }
@Test
  public void testAnnotateSkipsIfAlreadyAnnotated() {
    String text = "Previously processed.";
    CoreLabel token = new CoreLabel();
    token.setWord("Processed");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap dummySentence = new Annotation("Processed");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(dummySentence));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("Processed", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAnnotateWithNewlineSplitterSkipsEmptySentences() {
    String text = "First line.\n\nSecond line.";
    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("line");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreLabel newline1 = new CoreLabel();
    newline1.setWord("\n");
    newline1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    newline1.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    newline1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel newline2 = new CoreLabel();
    newline2.setWord("\n");
    newline2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    newline2.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    newline2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Second");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

    CoreLabel token5 = new CoreLabel();
    token5.setWord("line");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);

    CoreLabel token6 = new CoreLabel();
    token6.setWord(".");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 24);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, newline1, newline2, token4, token5, token6);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    CoreMap sentence1 = sentences.get(0);
    CoreMap sentence2 = sentences.get(1);
    assertNotNull(sentence1);
    assertNotNull(sentence2);
  } 
}