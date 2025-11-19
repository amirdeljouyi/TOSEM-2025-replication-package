public class ExhaustiveDependencyParser_wosr_1_GPTLLMTest { 

 @Test
  public void testParseAndHasParseShortSentence() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("NN");
    tagIndex.add("VBZ");
    tagIndex.add(".");

    TestOptions options = new TestOptions();
    options.testOptions.maxLength = 10;
    options.testOptions.verbose = false;
    options.doPCFG = true;

    Lexicon lexicon = new FakeLexicon(wordIndex, tagIndex);
    DependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    Word w1 = new Word("Dogs");
    Word w2 = new Word("bark");
    sentence.add(w1);
    sentence.add(w2);

    boolean result = parser.parse(sentence);
    assertTrue("Parser should parse valid short sentence", result);
    assertTrue("Parser should have parse after parse()", parser.hasParse());
  }
@Test
  public void testGetBestParseWithNoParseReturnsNull() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("NN");

    TestOptions options = new TestOptions();
    options.testOptions.maxLength = 5;
    options.testOptions.verbose = false;
    options.doPCFG = false;

    Lexicon lexicon = new FakeLexicon(wordIndex, tagIndex);
    DependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    Tree bestParse = parser.getBestParse();
    assertNull("If parse() hasn't been called, getBestParse should return null", bestParse);
  }
@Test
  public void testGetBestScoreBeforeParseIsNegativeInfinity() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("NN");

    TestOptions options = new TestOptions();
    options.testOptions.maxLength = 5;
    options.testOptions.verbose = false;
    options.doPCFG = false;

    Lexicon lexicon = new FakeLexicon(wordIndex, tagIndex);
    DependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    double score = parser.getBestScore();
    assertEquals("Should return negative infinity before parsing", Double.NEGATIVE_INFINITY, score, 0.0);
  }
@Test(expected = RuntimeException.class)
  public void testIScoreTotalThrowsExceptionWhenDisabled() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("NN");

    TestOptions options = new TestOptions();
    options.doPCFG = true;

    Lexicon lexicon = new FakeLexicon(wordIndex, tagIndex);
    DependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    parser.iScoreTotal(0, 1, 0, tagIndex.indexOf("NN"));
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKBestParsesThrowsException() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("VB");

    TestOptions options = new TestOptions();

    Lexicon lexicon = new FakeLexicon(wordIndex, tagIndex);
    DependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    parser.getKBestParses(3);
  }
@Test
  public void testScoreMethodsAfterParse() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("NN");
    tagIndex.add("VB");

    TestOptions options = new TestOptions();
    options.testOptions.maxLength = 5;
    options.doPCFG = true;

    Lexicon lexicon = new FakeLexicon(wordIndex, tagIndex);
    DependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    Word word1 = new Word("Dogs");
    Word word2 = new Word("run");

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(word1);
    sentence.add(word2);

    parser.parse(sentence);

    Edge edge = new Edge(0, 2, 1, tagIndex.indexOf("VB"));
    double oScore = parser.oScore(edge);
    double iScore = parser.iScore(edge);

    assertTrue("Should return valid oScore", oScore > Float.NEGATIVE_INFINITY);
    assertTrue("Should return valid iScore", iScore > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testIPossibleAndOPossibleHooks() {
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();
    tagIndex.add("NN");
    tagIndex.add("VBZ");

    TestOptions options = new TestOptions();
    options.testOptions.maxLength = 5;
    options.doPCFG = true;

    Lexicon lexicon = new FakeLexicon(wordIndex, tagIndex);
    DependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = Arrays.asList(new Word("Cats"), new Word("meow"));
    parser.parse(sentence);

    int tag = tagIndex.indexOf("VBZ");
    Hook hookRight = new Hook(0, 1, tag, true);
    Hook hookLeft = new Hook(0, 1, tag, false);

    assertTrue("oPossible for hookRight should be allowed", parser.oPossible(hookRight));
    assertTrue("oPossible for hookLeft should be allowed", parser.oPossible(hookLeft));
    assertTrue("iPossible for hookRight should be allowed", parser.iPossible(hookRight));
    assertTrue("iPossible for hookLeft should be allowed", parser.iPossible(hookLeft));
  } 
}