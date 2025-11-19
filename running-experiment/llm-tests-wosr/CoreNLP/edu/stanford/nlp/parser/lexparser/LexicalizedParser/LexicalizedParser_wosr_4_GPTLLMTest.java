public class LexicalizedParser_wosr_4_GPTLLMTest { 

 @Test
  public void testCopyLexicalizedParserCreatesEqualButDistinctObject() {
    Options options = new Options();
    Lexicon lexicon = new DummyLexicon();
    BinaryGrammar bg = new DummyBinaryGrammar();
    UnaryGrammar ug = new DummyUnaryGrammar();
    DependencyGrammar dg = new DummyDependencyGrammar();
    Index<String> stateIndex = DummyIndex.filledIndex("X", "Y");
    Index<String> wordIndex = DummyIndex.filledIndex("the", "dog");
    Index<String> tagIndex = DummyIndex.filledIndex("DT", "NN");

    LexicalizedParser original = new LexicalizedParser(lexicon, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);

    assertNotNull(copy);
    assertNotSame(original, copy);
    assertEquals(original.wordIndex, copy.wordIndex);
    assertEquals(original.lex.getClass(), copy.lex.getClass());
  }
@Test
  public void testParseStringsCreatesTreeWithExpectedLabel() {
    List<String> sentence = Arrays.asList("The", "cat", "sleeps");
    LexicalizedParser parser = new DummyLexicalizedParser();
    Tree result = parser.parseStrings(sentence);
    
    assertNotNull(result);
    assertEquals("ROOT", result.label().value());
    assertTrue(result.toString().contains("The"));
    assertTrue(result.toString().contains("sleeps"));
  }
@Test
  public void testParseReturnsXTreeOnException() {
    LexicalizedParser parser = new FailingLexicalizedParser();
    List<HasWord> words = Arrays.asList(new Word("unparsable"));

    Tree tree = parser.parse(words);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testParseMultipleParsesAllSentences() {
    LexicalizedParser parser = new DummyLexicalizedParser();
    List<HasWord> sentence1 = Arrays.asList(new Word("Hello"));
    List<HasWord> sentence2 = Arrays.asList(new Word("World"));
    List<List<HasWord>> sentences = Arrays.asList(sentence1, sentence2);

    List<Tree> trees = parser.parseMultiple(sentences);

    assertNotNull(trees);
    assertEquals(2, trees.size());
    assertEquals("ROOT", trees.get(0).label().value());
    assertEquals("ROOT", trees.get(1).label().value());
  }
@Test
  public void testParseTreeReturnsNullOnFailure() {
    LexicalizedParser parser = new AlwaysFailingParserQueryLexicalizedParser();
    List<HasWord> sentence = Arrays.asList(new Word("fails"));

    Tree result = parser.parseTree(sentence);
    assertNull(result);
  }
@Test
  public void testGetExtraEvalsEmptyWhenRerankerNull() {
    LexicalizedParser parser = new DummyLexicalizedParser();
    parser.reranker = null;

    List<?> evals = parser.getExtraEvals();
    assertNotNull(evals);
    assertTrue(evals.isEmpty());
  }
@Test
  public void testGetParserQueryReturnsRerankingWhenNotNull() {
    LexicalizedParser parser = new DummyLexicalizedParser();
    parser.reranker = new DummyReranker();

    assertTrue(parser.parserQuery() instanceof RerankingParserQuery);
  }
@Test
  public void testParserQueryReturnsLexicalizedQueryWhenNoReranker() {
    LexicalizedParser parser = new DummyLexicalizedParser();
    parser.reranker = null;

    assertTrue(parser.parserQuery() instanceof LexicalizedParserQuery);
  }
@Test
  public void testGetLexiconReturnsCorrectLexicon() {
    Lexicon lexicon = new DummyLexicon();
    LexicalizedParser parser = new LexicalizedParser(lexicon, null, null, null, null, null, null, new Options());

    assertSame(lexicon, parser.getLexicon());
  }
@Test
  public void testLoadModelFromSerializedReturnsNullOnBadFile() {
    LexicalizedParser parser = LexicalizedParser.getParserFromSerializedFile("this_file_does_not_exist");
    assertNull(parser);
  }
@Test
  public void testSetOptionFlagsWithNoError() {
    LexicalizedParser parser = new DummyLexicalizedParser();
    parser.setOptionFlags("-outputFormat", "penn", "-maxLength", "2");

    Options op = parser.getOp();
    assertEquals(2, op.testOptions.maxLength);
  }
@Test
  public void testTreePrintNotNull() {
    LexicalizedParser parser = new DummyLexicalizedParser();
    assertNotNull(parser.getTreePrint());
  } 
}