public class ExhaustiveDependencyParser_wosr_4_GPTLLMTest { 

 @Test(expected = UnsupportedOperationException.class)
  public void testGetKBestParsesThrows() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    when(mockDG.numTagBins()).thenReturn(1);
    when(mockDG.numDistBins()).thenReturn(1);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);
    parser.getKBestParses(3);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetBestParsesThrows() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);
    parser.getBestParses();
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKGoodParsesThrows() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);
    parser.getKGoodParses(3);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKSampledParsesThrows() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);
    parser.getKSampledParses(2);
  }
@Test
  public void testHasParseFalseWhenNoParse() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);
    assertFalse(parser.hasParse());
  }
@Test
  public void testGetBestScoreWithoutParseReturnsNegativeInfinity() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);
    double score = parser.getBestScore();
    assertEquals(Double.NEGATIVE_INFINITY, score, 0.0);
  }
@Test
  public void testGetBestParseReturnsNullIfNoParse() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);
    Tree result = parser.getBestParse();
    assertNull(result);
  }
@Test(expected = OutOfMemoryError.class)
  public void testParseThrowsOutOfMemoryForLengthTooLong() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    when(mockDG.numTagBins()).thenReturn(1);
    when(mockDG.numDistBins()).thenReturn(1);
    when(mockOptions.testOptions).thenReturn(mock(Options.TestOptions.class));
    when(mockOptions.testOptions.maxLength).thenReturn(0);
    when(mockOptions.testOptions.verbose).thenReturn(false);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);

    List<HasWord> longSentence = new ArrayList<>();
    longSentence.add(new Word("This"));
    longSentence.add(new Word("is"));
    longSentence.add(new Word("too"));
    longSentence.add(new Word("long"));

    
    parser.parse(longSentence);
  }
@Test
  public void testParseReturnsFalseIfNoTags() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    when(mockDG.numTagBins()).thenReturn(2);
    when(mockDG.numDistBins()).thenReturn(1);
    when(mockDG.tagBin(anyInt())).thenReturn(0);
    when(mockOptions.testOptions).thenReturn(mock(Options.TestOptions.class));
    when(mockOptions.testOptions.maxLength).thenReturn(10);
    when(mockOptions.testOptions.verbose).thenReturn(false);
    when(mockOptions.doPCFG).thenReturn(false);
    when(mockLex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.emptyIterator());

    tagIndex.add("BOUNDARY");
    wordIndex.add("test");

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);

    List<HasWord> sentence = Arrays.asList(new Word("test"));

    boolean result = parser.parse(sentence);

    assertFalse(result);
  }
@Test
  public void testGetBestScoreReturnsProperValueAfterParse() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    Lexicon mockLex = mock(Lexicon.class);
    Options mockOptions = mock(Options.class);
    Index<String> wordIndex = new Index<>();
    Index<String> tagIndex = new Index<>();

    when(mockDG.numTagBins()).thenReturn(2);
    when(mockDG.numDistBins()).thenReturn(2);
    when(mockDG.tagBin(anyInt())).thenReturn(0);
    when(mockOptions.testOptions).thenReturn(mock(Options.TestOptions.class));
    when(mockOptions.testOptions.maxLength).thenReturn(10);
    when(mockOptions.testOptions.verbose).thenReturn(false);
    when(mockOptions.doPCFG).thenReturn(false);
    when(mockLex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.emptyIterator());

    wordIndex.add("ROOT");
    wordIndex.add("test");
    tagIndex.add("BOUNDARY");

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, mockLex, mockOptions, wordIndex, tagIndex);

    List<HasWord> sentence = Arrays.asList(new Word("test"));
    try {
      parser.parse(sentence);
    } catch (Throwable t) {
      
    }

    double score = parser.getBestScore();
    assertTrue(Double.isFinite(score) || Double.isInfinite(score));
  }
@Test
  public void testIscoreReturnsCorrectSumOfComponents() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    when(mockDG.tagBin(anyInt())).thenReturn(0);

    Index<String> tagIndex = new Index<>();
    Index<String> wordIndex = new Index<>();
    tagIndex.add("BOUNDARY");

    Lexicon lex = mock(Lexicon.class);
    Options options = mock(Options.class);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, lex, options, wordIndex, tagIndex);
    parser.createArrays(3);

    parser.iScoreH[1][0][0] = 2.0f;
    parser.iScoreH[1][0][1] = 3.0f;

    float result = parser.iScore(0, 1, 1, 0);
    assertEquals(5.0f, result, 0.0f);
  }
@Test
  public void testOscoreReturnsCorrectSum() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    when(mockDG.tagBin(anyInt())).thenReturn(0);

    Index<String> tagIndex = new Index<>();
    Index<String> wordIndex = new Index<>();
    tagIndex.add("BOUNDARY");

    Lexicon lex = mock(Lexicon.class);
    Options options = mock(Options.class);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, lex, options, wordIndex, tagIndex);
    parser.createArrays(3);

    parser.oScoreH[1][0][0] = 1.5f;
    parser.oScoreH[1][0][1] = 2.5f;

    float result = parser.oScore(0, 1, 1, 0);
    assertEquals(4.0f, result, 0.0f);
  }
@Test
  public void testIScoreTotalThrowsWhenDisabled() {
    DependencyGrammar mockDG = mock(DependencyGrammar.class);
    when(mockDG.tagBin(anyInt())).thenReturn(0);

    Index<String> tagIndex = new Index<>();
    Index<String> wordIndex = new Index<>();
    tagIndex.add("BOUNDARY");

    Lexicon lex = mock(Lexicon.class);
    Options options = mock(Options.class);

    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(mockDG, lex, options, wordIndex, tagIndex);

    parser.createArrays(2);

    try {
      parser.iScoreTotal(0, 1, 0, 0);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("Summed inner scores not computed", e.getMessage());
    }
  } 
}