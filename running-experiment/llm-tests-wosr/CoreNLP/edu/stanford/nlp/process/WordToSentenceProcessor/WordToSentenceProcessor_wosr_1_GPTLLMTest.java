public class WordToSentenceProcessor_wosr_1_GPTLLMTest { 

 @Test
  public void testSimpleSentenceSplitting() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(
        new Word("This"), new Word("is"), new Word("a"), new Word("test"), new Word("."),
        new Word("Next"), new Word("sentence"), new Word("?")
    );
    List<List<HasWord>> result = processor.process(input);

    assertEquals(2, result.size());

    assertEquals(5, result.get(0).size());
    assertEquals("This", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(4).word());

    assertEquals(3, result.get(1).size());
    assertEquals("Next", result.get(1).get(0).word());
    assertEquals("?", result.get(1).get(2).word());
  }
@Test
  public void testPreserveSingleSentenceMode() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    List<HasWord> input = Arrays.asList(
        new Word("Hello"), new Word("world"), new Word("."),
        new Word("Another"), new Word("sentence"), new Word("!")
    );
    List<List<HasWord>> result = processor.process(input);

    assertEquals(1, result.size());
    assertEquals(6, result.get(0).size());
  }
@Test
  public void testEmptyInputReturnsEmptyList() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    List<List<HasWord>> result = processor.process(input);

    assertTrue(result.isEmpty());
  }
@Test
  public void testNewlineAsSentenceBoundaryAlways() {
    Set<String> boundaryToDiscard = new HashSet<>();
    boundaryToDiscard.add("\n");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        boundaryToDiscard, null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS, null, null
    );

    List<HasWord> input = Arrays.asList(
        new Word("First"), new Word("line"), new Word("\n"),
        new Word("Second"), new Word("line"), new Word("\n")
    );
    List<List<HasWord>> result = processor.process(input);

    assertEquals(2, result.size());
    assertEquals("First", result.get(0).get(0).word());
    assertEquals("Second", result.get(1).get(0).word());
  }
@Test
  public void testDiscardTrailingFollower() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(
        new Word("OK"), new Word("."), new Word(")")
    );
    List<List<HasWord>> result = processor.process(input);

    assertEquals(1, result.size());
    assertEquals(3, result.get(0).size());
    assertEquals(")", result.get(0).get(2).word());
  }
@Test
  public void testSentenceBoundaryWithForcedEndAnnotation() {
    WordToSentenceProcessor<Object> processor = new WordToSentenceProcessor<>();
    List<Object> input = Arrays.asList(
        makeCoreMapWord("Alpha"),
        makeCoreMapWord("Beta", true),
        new Word("Gamma")
    );
    List<List<Object>> result = processor.process(input);

    assertEquals(2, result.size());
    assertEquals("Alpha", WordToSentenceProcessor.getString(result.get(0).get(0)));
    assertEquals("Beta", WordToSentenceProcessor.getString(result.get(0).get(1)));
    assertEquals("Gamma", WordToSentenceProcessor.getString(result.get(1).get(0)));
  }
@Test
  public void testDoubleNewlineAsParagraphBreak() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE
    );
    List<HasWord> input = Arrays.asList(
        new Word("One"), new Word("sentence"), new Word("."),
        new Word("\n"), new Word("\n"),
        new Word("Two"), new Word("sentence"), new Word(".")
    );
    List<List<HasWord>> result = processor.process(input);

    assertEquals(2, result.size());
    assertEquals("One", result.get(0).get(0).word());
    assertEquals("Two", result.get(1).get(0).word());
  }
@Test
  public void testAllowEmptySentences() {
    Set<String> boundary = new HashSet<>();
    boundary.add("\n");

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        "", "", boundary,
        null, null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
        null, null,
        false, true
    );
    List<HasWord> input = Arrays.asList(new Word("\n"), new Word("A"), new Word("word"), new Word("\n"), new Word("\n"));
    List<List<HasWord>> result = processor.process(input);

    assertEquals(3, result.size());
    assertEquals(0, result.get(0).size());
    assertEquals("A", result.get(1).get(0).word());
    assertEquals(0, result.get(2).size());
  }
@Test
  public void testXmlBreakElementSplit() {
    Set<String> boundariesToDiscard = Collections.singleton("<p>");
    Set<String> xmlElements = Collections.singleton("p");

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        boundariesToDiscard,
        xmlElements,
        null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        null,
        null,
        false,
        false
    );

    List<HasWord> input = Arrays.asList(
        new Word("Start"), new Word("<p>"), new Word("Next"), new Word(".")
    );

    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Start", result.get(0).get(0).word());
    assertEquals("Next", result.get(1).get(0).word());
  }
@Test
  public void testNonBoundarySymbolsAreNotSplitPoints() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(
        new Word("Hello"), new Word(","),
        new Word("world"), new Word(".")
    );

    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(4, result.get(0).size());
  }
@Test
  public void testIncompatibleNewlineSettingThrowsException() {
    try {
      WordToSentenceProcessor.stringToNewlineIsSentenceBreak("invalid");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Not a valid NewlineIsSentenceBreak name"));
    }
  } 
}