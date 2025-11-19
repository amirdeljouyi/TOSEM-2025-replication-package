public class WordToSentenceProcessor_wosr_5_GPTLLMTest { 

 @Test
  public void testOneSentenceMode() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("This"));
    words.add(new Word("is"));
    words.add(new Word("a"));
    words.add(new Word("test"));
    words.add(new Word("."));

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(1, sentences.size());
    assertEquals(5, sentences.get(0).size());
    assertEquals("This", sentences.get(0).get(0).word());
    assertEquals(".", sentences.get(0).get(4).word());
  }
@Test
  public void testSimpleSentenceSplit() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("This"));
    words.add(new Word("is"));
    words.add(new Word("one"));
    words.add(new Word("."));
    words.add(new Word("This"));
    words.add(new Word("is"));
    words.add(new Word("two"));
    words.add(new Word("."));

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(2, sentences.size());
    assertEquals("This", sentences.get(0).get(0).word());
    assertEquals(".", sentences.get(0).get(3).word());
    assertEquals("two", sentences.get(1).get(2).word());
  }
@Test
  public void testSentenceWithBoundaryFollower() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("Hello"));
    words.add(new Word("world"));
    words.add(new Word("!"));
    words.add(new Word(")"));
    words.add(new Word("Next"));
    words.add(new Word("sentence"));
    words.add(new Word("."));

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(2, sentences.size());
    assertEquals(")", sentences.get(0).get(3).word());
  }
@Test
  public void testTwoConsecutiveNewlinesSplit() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("First"));
    words.add(new Word("line"));
    words.add(new Word("."));
    words.add(new Word(WhitespaceLexer.NEWLINE));
    words.add(new Word(WhitespaceLexer.NEWLINE));
    words.add(new Word("Second"));
    words.add(new Word("line"));
    words.add(new Word("."));

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(2, sentences.size());
    assertEquals("First", sentences.get(0).get(0).word());
    assertEquals("Second", sentences.get(1).get(0).word());
  }
@Test
  public void testAlwaysBreakOnNewline() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("Line"));
    words.add(new Word("one"));
    words.add(new Word(WhitespaceLexer.NEWLINE));
    words.add(new Word("Line"));
    words.add(new Word("two"));
    words.add(new Word("."));

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(NewlineIsSentenceBreak.ALWAYS);
    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(2, sentences.size());
    assertEquals("Line", sentences.get(1).get(0).word());
  }
@Test
  public void testNeverBreakOnNewline() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("First"));
    words.add(new Word("sentence"));
    words.add(new Word(WhitespaceLexer.NEWLINE));
    words.add(new Word("Second"));
    words.add(new Word("part"));
    words.add(new Word("."));

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(NewlineIsSentenceBreak.NEVER);
    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(1, sentences.size());
    assertEquals(6, sentences.get(0).size());
  }
@Test
  public void testEmptySentenceOnOnlyDiscardTokenWithAllowEmpty() {
    Set<String> boundarySet = new HashSet<>();
    boundarySet.add("[EOL]");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(boundarySet);
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("[EOL]"));

    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(1, sentences.size());
    assertEquals(0, sentences.get(0).size());
  }
@Test
  public void testForcedSentenceEndAnnotation() {
    CoreMap token1 = new Word("This");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    token1.set(CoreAnnotations.TextAnnotation.class, "This");

    CoreMap token2 = new Word("is");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token2.set(CoreAnnotations.TextAnnotation.class, "is");

    CoreMap token3 = new Word("end");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "end");
    token3.set(CoreAnnotations.TextAnnotation.class, "end");
    token3.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);

    CoreMap token4 = new Word("Next");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "Next");
    token4.set(CoreAnnotations.TextAnnotation.class, "Next");

    List<CoreMap> words = new ArrayList<>();
    words.add(token1);
    words.add(token2);
    words.add(token3);
    words.add(token4);

    WordToSentenceProcessor<CoreMap> processor = new WordToSentenceProcessor<>();
    List<List<CoreMap>> sentences = processor.process(words);

    assertEquals(2, sentences.size());
    assertEquals("This", sentences.get(0).get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Next", sentences.get(1).get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testSentenceRegionFiltering() {
    Set<String> boundary = new HashSet<>();
    boundary.add(WhitespaceLexer.NEWLINE);

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        ".", "[)\\]]", boundary, Collections.emptySet(), "s", NewlineIsSentenceBreak.ALWAYS,
        null, Collections.emptySet(), false, false
    );

    List<HasWord> words = new ArrayList<>();
    words.add(new Word("<s>"));
    words.add(new Word("Inside"));
    words.add(new Word("sentence"));
    words.add(new Word("."));
    words.add(new Word("</s>"));
    words.add(new Word("Outside"));
    words.add(new Word("text"));
    words.add(new Word("."));

    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(1, sentences.size());
    assertEquals("Inside", sentences.get(0).get(0).word());
  }
@Test
  public void testTrailingQuoteHandling() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("She"));
    words.add(new Word("said"));
    words.add(new Word("."));
    words.add(new Word("\""));
    words.add(new Word("Yes"));
    words.add(new Word("."));

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<List<HasWord>> sentences = processor.process(words);

    assertEquals(2, sentences.size());
    assertEquals("\"", sentences.get(0).get(3).word());
  }
@Test
  public void testMultiTokenBoundaryPatternMatch() {
    List<Word> input = new ArrayList<>();
    input.add(new Word("They"));
    input.add(new Word("said"));
    input.add(new Word("\""));
    input.add(new Word("Go"));
    input.add(new Word("ahead"));
    input.add(new Word("\""));
    input.add(new Word("."));

    SequencePattern<Word> pattern = SequencePattern.compile("edu.stanford.nlp.ling.Word", "[{word: \"Go\"} , {word: \"ahead\"}]");
    
    WordToSentenceProcessor<Word> processor = new WordToSentenceProcessor<>(
      WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
      WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
      WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
      Collections.emptySet(),
      null,
      NewlineIsSentenceBreak.NEVER,
      pattern,
      Collections.emptySet(),
      false,
      false
    );

    List<List<Word>> sentences = processor.process(input);

    assertEquals(1, sentences.size());
    assertEquals(7, sentences.get(0).size());
  } 
}