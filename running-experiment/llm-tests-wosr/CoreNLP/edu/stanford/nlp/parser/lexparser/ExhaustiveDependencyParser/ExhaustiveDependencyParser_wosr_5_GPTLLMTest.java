public class ExhaustiveDependencyParser_wosr_5_GPTLLMTest { 

 @Test(expected = UnsupportedOperationException.class)
  public void testGetKBestParsesThrows() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("ROOT");

    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getKBestParses(1);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetBestParsesThrows() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("ROOT");

    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getBestParses();
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKGoodParsesThrows() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("ROOT");

    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getKGoodParses(2);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKSampledParsesThrows() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("ROOT");

    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    parser.getKSampledParses(3);
  }
@Test
  public void testHasParseFalseWhenNoParseRan() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("ROOT");

    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    assertFalse(parser.hasParse());
  }
@Test
  public void testGetBestScoreBeforeParseReturnsNegativeInfinity() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("ROOT");

    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.0);
  }
@Test
  public void testGetBestParseReturnsNullWhenParseFails() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("ROOT");

    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);
    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("nonexistentword"));

    
    try {
      parser.parse(sentence);
    } catch (OutOfMemoryError e) {
      
    }

    assertNull(parser.getBestParse());
  }
@Test
  public void testParseSimpleSentenceReturnsTrueAndBestParseNotNull() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("DT");  
    tagIndex.add(Lexicon.BOUNDARY_TAG);  

    Options options = new Options();
    options.testOptions.maxLength = 10;
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("the"));
    sentence.add(new Word("."));

    boolean result = parser.parse(sentence);

    assertTrue(result);
    assertNotNull(parser.getBestParse());
  }
@Test
  public void testParseRejectsTooLongInput() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Options options = new Options();
    options.testOptions.maxLength = 1;
    Lexicon lexicon = new DummyLexicon();
    DependencyGrammar grammar = new DummyDependencyGrammar(tagIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> longSentence = new ArrayList<>();
    longSentence.add(new Word("one"));
    longSentence.add(new Word("two"));
    longSentence.add(new Word("three"));

    try {
      parser.parse(longSentence);
      fail("Expected OutOfMemoryError");
    } catch (OutOfMemoryError expected) {
      
    }
  } 
}