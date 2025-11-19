public class WordToSentenceProcessor_wosr_4_GPTLLMTest { 

 @Test
  public void testDefaultConstructorBasicSplit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();

    CoreLabel word1 = new CoreLabel();
    word1.setWord("Hello");
    CoreLabel word2 = new CoreLabel();
    word2.setWord(".");
    CoreLabel word3 = new CoreLabel();
    word3.setWord("How");
    CoreLabel word4 = new CoreLabel();
    word4.setWord("are");
    CoreLabel word5 = new CoreLabel();
    word5.setWord("you");
    CoreLabel word6 = new CoreLabel();
    word6.setWord("?");

    List<HasWord> input = Arrays.asList(word1, word2, word3, word4, word5, word6);
    List<List<HasWord>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("How", output.get(1).get(0).word());
    assertEquals("you", output.get(1).get(2).word());
  }
@Test
  public void testNoSplitSingleSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);

    CoreLabel word1 = new CoreLabel();
    word1.setWord("This");
    CoreLabel word2 = new CoreLabel();
    word2.setWord("is");
    CoreLabel word3 = new CoreLabel();
    word3.setWord("one");
    CoreLabel word4 = new CoreLabel();
    word4.setWord("sentence");
    CoreLabel word5 = new CoreLabel();
    word5.setWord(".");

    List<HasWord> input = Arrays.asList(word1, word2, word3, word4, word5);
    List<List<HasWord>> output = processor.process(input);

    assertEquals(1, output.size());
    assertEquals(5, output.get(0).size());
    assertEquals("sentence", output.get(0).get(3).word());
  }
@Test
  public void testSplitOnNewlineAlways() {
    Set<String> newlines = new HashSet<>();
    newlines.add("\n");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(newlines);

    CoreLabel word1 = new CoreLabel();
    word1.setWord("First");
    CoreLabel nl1 = new CoreLabel();
    nl1.setWord("\n");
    CoreLabel word2 = new CoreLabel();
    word2.setWord("Second");
    CoreLabel nl2 = new CoreLabel();
    nl2.setWord("\n");

    List<HasWord> input = Arrays.asList(word1, nl1, word2, nl2);
    List<List<HasWord>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals("First", output.get(0).get(0).word());
    assertEquals("Second", output.get(1).get(0).word());
  }
@Test
  public void testBoundaryFollowersAtSentenceSplit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();

    CoreLabel word1 = new CoreLabel();
    word1.setWord("Hi");
    CoreLabel word2 = new CoreLabel();
    word2.setWord(".");
    CoreLabel word3 = new CoreLabel();
    word3.setWord(")");
    CoreLabel word4 = new CoreLabel();
    word4.setWord("Bye");
    CoreLabel word5 = new CoreLabel();
    word5.setWord("!");

    List<HasWord> input = Arrays.asList(word1, word2, word3, word4, word5);
    List<List<HasWord>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals(")", output.get(0).get(2).word());
    assertEquals("Bye", output.get(1).get(0).word());
  }
@Test
  public void testEmptyInput() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<List<HasWord>> output = processor.process(Collections.emptyList());

    assertTrue(output.isEmpty());
  }
@Test
  public void testForcedSentenceEndAnnotation() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("end");
    token3.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Next");

    List<CoreLabel> input = Arrays.asList(token1, token2, token3, token4);
    List<List<CoreLabel>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals("end", output.get(0).get(2).word());
    assertEquals("Next", output.get(1).get(0).word());
  }
@Test
  public void testQuoteBalancing() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("He");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("said");
    CoreLabel token3 = new CoreLabel();
    token3.setWord(",");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("\"");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Stop");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("!");

    List<HasWord> input = Arrays.asList(token1, token2, token3, token4, token5, token6);
    List<List<HasWord>> output = processor.process(input);

    assertEquals(1, output.size());
    assertEquals("Stop", output.get(0).get(4).word());
  }
@Test
  public void testForcedSentenceUntilEndAnnotation() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Part1");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Part2");
    token2.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Part3");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("End");
    token4.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);

    CoreLabel token5 = new CoreLabel();
    token5.setWord("After");

    List<CoreLabel> input = Arrays.asList(token1, token2, token3, token4, token5);
    List<List<CoreLabel>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals(4, output.get(0).size());
    assertEquals("After", output.get(1).get(0).word());
  }
@Test
  public void testMultiTokenBoundaryPattern() {
    List<String> tokens = Arrays.asList("This", "is", "an", "example", "\n", "\n", "New", "sentence");
    List<CoreLabel> input = new ArrayList<>();
    for (String s : tokens) {
      CoreLabel cl = new CoreLabel();
      cl.setWord(s);
      input.add(cl);
    }

    Set<String> discard = new HashSet<>();
    discard.add("\n");

    List<SequencePattern<CoreLabel>> seqPatterns = new ArrayList<>();
    
    SequencePattern<CoreLabel> pattern = SequencePattern.compile("([{word:/\\n/}]+ [{word:/\\n/}]+)");
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        discard,
        Collections.emptySet(),
        null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
        pattern,
        null,
        false,
        false
    );

    List<List<CoreLabel>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals("example", output.get(0).get(3).word());
    assertEquals("New", output.get(1).get(0).word());
  }
@Test
  public void testXmlBreakElements() {
    Set<String> newlines = new HashSet<>();
    newlines.add("\n");

    Set<String> xmlElements = new HashSet<>();
    xmlElements.add("p");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Text");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("New");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("Section");

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        newlines,
        xmlElements,
        null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        null,
        null,
        false,
        false
    );

    List<HasWord> input = Arrays.asList(token1, token2, token3, token4);
    List<List<HasWord>> output = processor.process(input);

    assertEquals(2, output.size());
    assertEquals("Text", output.get(0).get(0).word());
    assertEquals("New", output.get(1).get(0).word());
  }
@Test
  public void testRegionFiltering() {
    Set<String> newlines = new HashSet<>();
    newlines.add("\n");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Outside1");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<region>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("InsideText");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</region>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Outside2");

    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(
        WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
        WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
        newlines,
        Collections.emptySet(),
        "region",
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        null,
        null,
        false,
        false
    );

    List<HasWord> input = Arrays.asList(token1, token2, token3, token4, token5);
    List<List<HasWord>> output = processor.process(input);

    assertEquals(1, output.size());
    assertEquals("InsideText", output.get(0).get(0).word());
  } 
}