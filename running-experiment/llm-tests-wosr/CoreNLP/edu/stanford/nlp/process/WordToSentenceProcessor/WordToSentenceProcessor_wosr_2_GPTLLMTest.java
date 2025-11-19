public class WordToSentenceProcessor_wosr_2_GPTLLMTest { 

 @Test
  public void testBasicPeriodSplitting() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(
        new Word("This"),
        new Word("is"),
        new Word("a"),
        new Word("test"),
        new Word("."),
        new Word("Next"),
        new Word("sentence"),
        new Word(".")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());

    assertEquals(Arrays.asList(new Word("This"), new Word("is"), new Word("a"), new Word("test"), new Word(".")), output.get(0));
    assertEquals(Arrays.asList(new Word("Next"), new Word("sentence"), new Word(".")), output.get(1));
  }
@Test
  public void testWithoutSentenceBoundary() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(
        new Word("Hello"),
        new Word("world")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(Arrays.asList(new Word("Hello"), new Word("world")), output.get(0));
  }
@Test
  public void testOneSentenceMode() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    List<HasWord> input = Arrays.asList(
        new Word("This"), new Word("is"), new Word("one"), new Word("chunk"), new Word("."), new Word("And"), new Word("this"), new Word("too"), new Word(".")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(9, output.get(0).size());
  }
@Test
  public void testNewlineAlwaysBreak() {
    Set<String> boundaries = new HashSet<>();
    boundaries.add("\n");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(boundaries);
    List<HasWord> input = Arrays.asList(
        new Word("Line"),
        new Word("one"),
        new Word("\n"),
        new Word("Line"),
        new Word("two"),
        new Word("\n")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(Arrays.asList(new Word("Line"), new Word("one")), output.get(0));
    assertEquals(Arrays.asList(new Word("Line"), new Word("two")), output.get(1));
  }
@Test
  public void testNewlineTwoConsecutiveBreak() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    List<HasWord> input = Arrays.asList(
        new Word("A"),
        new Word("line"),
        new Word("\n"),
        new Word("\n"),
        new Word("Another"),
        new Word("line")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(Arrays.asList(new Word("A"), new Word("line")), output.get(0));
    assertEquals(Arrays.asList(new Word("Another"), new Word("line")), output.get(1));
  }
@Test
  public void testBoundaryFollowerAppendsToPrevious() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(
        new Word("Test"),
        new Word("."),
        new Word(")")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(Arrays.asList(new Word("Test"), new Word("."), new Word(")")), output.get(0));
  }
@Test
  public void testMultiplePeriodsSplitCorrectly() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(
        new Word("1."),
        new Word("2."),
        new Word("done.")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(3, output.size());
  }
@Test
  public void testEmptyInput() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    List<List<HasWord>> output = processor.process(input);
    assertEquals(0, output.size());
  }
@Test
  public void testXmlBreakElementSplitsSentence() {
    Set<String> xmlBreaks = new HashSet<>(Arrays.asList("p"));
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
        xmlBreaks,
        null,
        NewlineIsSentenceBreak.NEVER,
        null,
        null,
        false,
        false
    );

    List<HasWord> input = Arrays.asList(
        new Word("First"),
        new Word("sentence"),
        new Word("."),
        new Word("<p>"),
        new Word("Second"),
        new Word("sentence"),
        new Word(".")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(Arrays.asList(new Word("First"), new Word("sentence"), new Word(".")), output.get(0));
    assertEquals(Arrays.asList(new Word("Second"), new Word("sentence"), new Word(".")), output.get(1));
  }
@Test
  public void testForcedSentenceEndAnnotation() {
    WordToSentenceProcessor<CoreMap> processor = new WordToSentenceProcessor<>();

    CoreLabel w1 = new CoreLabel();
    w1.setWord("Forced");
    w1.set(CoreAnnotations.TextAnnotation.class, "Forced");
    w1.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);

    CoreLabel w2 = new CoreLabel();
    w2.setWord("End");
    w2.set(CoreAnnotations.TextAnnotation.class, "End");

    List<CoreMap> input = Arrays.asList(w1, w2);
    List<List<CoreMap>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals(1, output.get(0).size());
    assertEquals("Forced", output.get(0).get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(1, output.get(1).size());
    assertEquals("End", output.get(1).get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testAllowEmptySentence() {
    Set<String> boundaries = new HashSet<>();
    boundaries.add("\n");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        "", "", boundaries, null, null,
        NewlineIsSentenceBreak.ALWAYS, null, null, false, true
    );

    List<HasWord> input = Arrays.asList(
        new Word("\n"),
        new Word("\n")
    );
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertTrue(output.get(0).isEmpty());
    assertTrue(output.get(1).isEmpty());
  }
@Test
  public void testDiscardTokenPattern() {
    Set<String> toDiscard = null;
    Set<String> discardPatterns = new HashSet<>();
    discardPatterns.add("^###.*$");

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        "\\.", WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
        null, null, NewlineIsSentenceBreak.NEVER, null, discardPatterns, false, false
    );

    List<HasWord> input = Arrays.asList(
        new Word("Hello"),
        new Word("world"),
        new Word("."),
        new Word("###IGNORE_THIS"),
        new Word("Next"),
        new Word(".")
    );

    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(Arrays.asList(new Word("Hello"), new Word("world"), new Word(".")), output.get(0));
    assertEquals(Arrays.asList(new Word("Next"), new Word(".")), output.get(1));
  }
@Test
  public void testCustomRegexBoundaries() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        "##", "", new HashSet<>(), null, null, NewlineIsSentenceBreak.NEVER, null, null, false, false
    );

    List<HasWord> input = Arrays.asList(
        new Word("First"),
        new Word("##"),
        new Word("Second"),
        new Word("##")
    );

    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(Arrays.asList(new Word("First"), new Word("##")), output.get(0));
    assertEquals(Arrays.asList(new Word("Second"), new Word("##")), output.get(1));
  } 
}