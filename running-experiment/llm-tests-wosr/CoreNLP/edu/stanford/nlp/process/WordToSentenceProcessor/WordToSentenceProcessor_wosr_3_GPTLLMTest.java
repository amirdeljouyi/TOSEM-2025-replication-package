public class WordToSentenceProcessor_wosr_3_GPTLLMTest { 

 @Test
  public void testDefaultOneSentence() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(true);
    List<String> tokens = Arrays.asList("This", "is", "a", "sentence", ".");
    List<List<String>> result = processor.process(tokens);

    assertEquals(1, result.size());
    assertEquals(tokens, result.get(0));
  }
@Test
  public void testDefaultBreakOnPeriod() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> tokens = Arrays.asList("This", "is", "sentence", "one", ".", "This", "is", "two", ".");
    List<List<String>> result = processor.process(tokens);

    assertEquals(2, result.size());
    assertEquals(Arrays.asList("This", "is", "sentence", "one", "."), result.get(0));
    assertEquals(Arrays.asList("This", "is", "two", "."), result.get(1));
  }
@Test
  public void testBoundaryFollowerIncluded() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> tokens = Arrays.asList("Hello", ".", ")", "New", "sentence", ".");
    List<List<String>> result = processor.process(tokens);

    assertEquals(2, result.size());
    assertEquals(Arrays.asList("Hello", ".", ")"), result.get(0));
    assertEquals(Arrays.asList("New", "sentence", "."), result.get(1));
  }
@Test
  public void testNewlineDiscardAndBreakOnTwoConsecutiveNewlines() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> tokens = Arrays.asList("First", "sentence", ".", "\n", "\n", "Second", "one", ".");
    List<List<String>> result = processor.process(tokens);

    assertEquals(2, result.size());
    assertEquals(Arrays.asList("First", "sentence", "."), result.get(0));
    assertEquals(Arrays.asList("Second", "one", "."), result.get(1));
  }
@Test
  public void testForcedEndAnnotationSplitting() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("one");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("END");
    token3.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Sentence");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("two");
    CoreLabel token6 = new CoreLabel();
    token6.setWord(".");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    List<List<CoreLabel>> result = processor.process(tokens);

    assertEquals(2, result.size());
    assertEquals(Arrays.asList(token1, token2, token3), result.get(0));
    assertEquals(Arrays.asList(token4, token5, token6), result.get(1));
  }
@Test
  public void testEmptyInputReturnsEmptyList() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> tokens = Collections.emptyList();
    List<List<String>> result = processor.process(tokens);

    assertTrue(result.isEmpty());
  }
@Test
  public void testNoSentenceEndReturnsOneSentence() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> tokens = Arrays.asList("This", "has", "no", "punctuation");
    List<List<String>> result = processor.process(tokens);

    assertEquals(1, result.size());
    assertEquals(tokens, result.get(0));
  }
@Test
  public void testXmlBreakElementsDiscarded() {
    Set<String> xmlTags = Collections.singleton("p");
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
        xmlTags,
        null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        null,
        null,
        false,
        false
    );

    List<String> tokens = Arrays.asList("This", "is", "sentence", ".", "<p>", "Another", "sentence", ".");
    List<List<String>> result = processor.process(tokens);

    assertEquals(2, result.size());
    assertEquals(Arrays.asList("This", "is", "sentence", "."), result.get(0));
    assertEquals(Arrays.asList("Another", "sentence", "."), result.get(1));
  }
@Test
  public void testRegionBeginAndEndPatterns() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
        null,
        "region",
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        null,
        null,
        false,
        false
    );

    List<String> tokens = Arrays.asList("outside", "<region>", "This", "is", "inside", ".", "</region>", "ignored");
    List<List<String>> result = processor.process(tokens);

    assertEquals(1, result.size());
    assertEquals(Arrays.asList("This", "is", "inside", "."), result.get(0));
  }
@Test
  public void testAllowEmptySentencesTrue() {
    Set<String> boundary = Collections.singleton("\n");
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        "",
        "",
        boundary,
        null,
        null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
        null,
        null,
        false,
        true 
    );

    List<String> tokens = Arrays.asList("A", "sentence", ".", "\n", "\n", "Another", ".", "\n");
    List<List<String>> result = processor.process(tokens);

    assertEquals(4, result.size());
    assertEquals(Arrays.asList("A", "sentence", "."), result.get(0));
    assertEquals(Collections.emptyList(), result.get(1));
    assertEquals(Arrays.asList("Another", "."), result.get(2));
    assertEquals(Collections.emptyList(), result.get(3));
  }
@Test
  public void testOneSentencePerLineConstructor() {
    Set<String> boundaries = new HashSet<>();
    boundaries.add("\n");
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(boundaries);

    List<String> tokens = Arrays.asList("Sentence", "one", "\n", "Sentence", "two", "\n");
    List<List<String>> result = processor.process(tokens);

    assertEquals(2, result.size());
    assertEquals(Arrays.asList("Sentence", "one"), result.get(0));
    assertEquals(Arrays.asList("Sentence", "two"), result.get(1));
  }
@Test
  public void testSingleQuoteAtEndOfSentence() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> tokens = Arrays.asList("He", "said", "'", "Hello", ".", "'");

    List<List<String>> result = processor.process(tokens);

    assertEquals(1, result.size());
    assertEquals(Arrays.asList("He", "said", "'", "Hello", ".", "'"), result.get(0));
  }
@Test
  public void testHandlesMultipleConsecutiveSeparators() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> tokens = Arrays.asList("First", ".", "\n", "\n", "\n", "Second", ".");

    List<List<String>> result = processor.process(tokens);

    assertEquals(2, result.size());
    assertEquals(Arrays.asList("First", "."), result.get(0));
    assertEquals(Arrays.asList("Second", "."), result.get(1));
  }
@Test(expected = IllegalArgumentException.class)
  public void testStringToNewlineIsSentenceBreakInvalid() {
    WordToSentenceProcessor.stringToNewlineIsSentenceBreak("invalid_option");
  } 
}