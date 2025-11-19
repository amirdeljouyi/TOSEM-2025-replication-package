public class ExhaustiveDependencyParser_wosr_2_GPTLLMTest { 

 @Test
  public void testHasParseReturnsFalseWithoutParsing() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 10;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    assertFalse(parser.hasParse());
  }
@Test
  public void testHasParseReturnsTrueAfterParsingSimpleSentence() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 10;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("the"));
    sentence.add(new Word("dog"));

    boolean result = parser.parse(sentence);

    assertTrue(result);
    assertTrue(parser.hasParse());
  }
@Test
  public void testGetBestParseReturnsNullBeforeParse() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 10;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    Tree tree = parser.getBestParse();
    assertNull(tree);
  }
@Test
  public void testGetBestParseReturnsTreeAfterParsing() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 10;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = Arrays.asList(new Word("the"), new Word("cat"));
    boolean parsed = parser.parse(sentence);
    Tree tree = parser.getBestParse();

    assertTrue(parsed);
    assertNotNull(tree);
    assertTrue(tree.yield().size() > 0);
  }
@Test
  public void testGetBestScoreMatchesExpectedScore() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 10;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = Arrays.asList(new Word("one"), new Word("test"));
    parser.parse(sentence);

    double bestScore = parser.getBestScore();
    assertTrue(bestScore > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testParseRejectsTooLongSentence() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 5;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = Arrays.asList(
        new Word("a"), new Word("b"), new Word("c"),
        new Word("d"), new Word("e"), new Word("f")
    );

    try {
      parser.parse(sentence);
      fail("Expected OutOfMemoryError due to sentence length exceeding maxLength");
    } catch (OutOfMemoryError expected) {
      
    }
  }
@Test
  public void testIScoreReturnsNegativeInfinityBeforeParse() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 10;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    float score = parser.iScore(0, 1, 0, 0);
    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0001);
  }
@Test
  public void testOScoreReturnsNegativeInfinityBeforeParse() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();
    options.testOptions.maxLength = 10;

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    float score = parser.oScore(0, 1, 0, 0);
    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0001);
  }
@Test(expected = RuntimeException.class)
  public void testIScoreTotalThrowsWhenFeatureDisabled() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.iScoreTotal(0, 1, 0, 0);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKBestParsesThrowsNotImplemented() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getKBestParses(1);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKGoodParsesThrowsNotImplemented() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getKGoodParses(1);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetBestParsesThrowsNotImplemented() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getBestParses();
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKSampledParsesThrowsNotImplemented() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    DummyGrammar grammar = new DummyGrammar(tagIndex);
    DummyLexicon lexicon = new DummyLexicon(tagIndex, wordIndex);
    Options options = new Options();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getKSampledParses(1);
  } 
}