public class SplittingGrammarExtractor_wosr_1_GPTLLMTest { 

 @Test
  public void testSaveTreesSingleCollection() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree t1 = buildPreTerminalTree();
    List<Tree> list = Collections.singletonList(t1);
    extractor.saveTrees(list, 0.5, null, -1.0);

    assertEquals(1, extractor.trees.size());
    assertEquals(0.5, extractor.trainSize, 0.0001);
    assertEquals(1, extractor.treeWeights.size());
    assertEquals(0.5, extractor.treeWeights.getCount(t1), 0.0001);
  }
@Test
  public void testSaveTreesBothCollections() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree t1 = buildPreTerminalTree();
    Tree t2 = buildSimpleUnaryTree();
    List<Tree> l1 = Collections.singletonList(t1);
    List<Tree> l2 = Collections.singletonList(t2);

    extractor.saveTrees(l1, 1.0, l2, 2.0);

    assertEquals(2, extractor.trees.size());
    assertEquals(3.0, extractor.trainSize, 0.0001);
    assertEquals(2, extractor.treeWeights.size());
    assertEquals(1.0, extractor.treeWeights.getCount(t1), 0.0001);
    assertEquals(2.0, extractor.treeWeights.getCount(t2), 0.0001);
  }
@Test
  public void testCountOriginalStates() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree tree = buildSimpleUnaryTree();
    extractor.trees.add(tree);

    extractor.countOriginalStates();

    assertTrue(extractor.originalStates.contains("NP"));
    assertTrue(extractor.originalStates.contains("NN"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("NP"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("NN"));
  }
@Test
  public void testStateWithBoundaryTag() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    String result = extractor.state(Lexicon.BOUNDARY_TAG, 3);
    assertEquals(Lexicon.BOUNDARY_TAG, result);
  }
@Test
  public void testStateWithStartSymbol() {
    Options opts = mockOptions();
    opts.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    String start = opts.langpack().startSymbol();
    String result = extractor.state(start, 1);
    assertEquals(start, result);
  }
@Test
  public void testStateRegularTag() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    String result = extractor.state("NP", 2);
    assertEquals("NP^2", result);
  }
@Test
  public void testInitialBetasAndLexiconUnaryRule() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree tree = buildSimpleUnaryTree();
    extractor.trees.add(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

    extractor.initialBetasAndLexicon();

    assertNotNull(extractor.lex);
    assertTrue(extractor.unaryBetas.contains("NP", "NN"));
    assertEquals(1.0, extractor.lex.numTrees(), 0.0001);
  }
@Test
  public void testInitialBetasAndLexiconBinaryRule() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree tree = buildSimpleBinaryTree();
    extractor.trees.add(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

    extractor.initialBetasAndLexicon();

    assertTrue(extractor.binaryBetas.contains("NP", "DT", "NN"));
    assertEquals(1.0, extractor.lex.numTrees(), 0.0001);
  }
@Test
  public void testSplitStateCounts() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    extractor.stateSplitCounts.setCount("NP", 1.0);
    extractor.stateSplitCounts.setCount("VP", 1.0);
    extractor.stateSplitCounts.setCount("S", 1.0);
    extractor.stateSplitCounts.setCount(Lexicon.BOUNDARY_TAG, 1.0);

    extractor.startSymbols.add("S");

    extractor.splitStateCounts();

    assertEquals(2, extractor.stateSplitCounts.getIntCount("NP"));
    assertEquals(2, extractor.stateSplitCounts.getIntCount("VP"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("S"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount(Lexicon.BOUNDARY_TAG));
  }
@Test
  public void testRecalculateBetasSplit() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree tree = buildSimpleUnaryTree();
    extractor.trees.add(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;
    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();
    extractor.splitStateCounts();

    boolean result = extractor.recalculateBetas(true);

    assertFalse(result);
    assertNotNull(extractor.lex);
  }
@Test
  public void testBuildStateIndex() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    extractor.stateSplitCounts.setCount("NP", 2.0);
    extractor.stateSplitCounts.setCount("VP", 1.0);

    extractor.buildStateIndex();

    Index<String> index = extractor.stateIndex;
    assertEquals(3, index.size());
    assertTrue(index.contains("NP^0"));
    assertTrue(index.contains("NP^1"));
    assertTrue(index.contains("VP^0"));
  }
@Test
  public void testBuildGrammarsUpdatesBgug() {
    Options opts = mockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree tree = buildSimpleUnaryTree();
    extractor.trees.add(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;
    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();

    extractor.buildStateIndex();
    extractor.buildGrammars();

    assertNotNull(extractor.bgug);
    assertNotNull(extractor.bgug.first());
    assertNotNull(extractor.bgug.second());
    assertTrue(extractor.bgug.first().numRules() > 0 || extractor.bgug.second().numRules() > 0);
  }
@Test
  public void testExtractSimpleUnary() {
    Options opts = mockOptions();
    opts.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    Tree tree = buildSimpleUnaryTree();
    List<Tree> sample = Collections.singletonList(tree);
    extractor.extract(sample);

    assertNotNull(extractor.lex);
    assertNotNull(extractor.bgug);
    assertNotNull(extractor.bgug.first());
    assertNotNull(extractor.bgug.second());
    assertTrue(extractor.lex.numTrees() > 0);
  } 
}