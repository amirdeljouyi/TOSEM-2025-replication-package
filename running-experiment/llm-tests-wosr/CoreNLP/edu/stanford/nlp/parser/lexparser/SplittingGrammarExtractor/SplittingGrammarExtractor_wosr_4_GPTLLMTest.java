public class SplittingGrammarExtractor_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructorInitializesFields() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);
    assertNotNull(extractor.startSymbols);
    assertTrue(extractor.startSymbols.contains("ROOT"));
  }
@Test
  public void testStateBoundaryDoesNotSplit() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    String result = extractor.state(Lexicon.BOUNDARY_TAG, 1);
    assertEquals(Lexicon.BOUNDARY_TAG, result);
  }
@Test
  public void testStateStartSymbolDoesNotSplit() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    String result = extractor.state("ROOT", 3);
    assertEquals("ROOT", result);
  }
@Test
  public void testStateGenericSymbolSplitsCorrectly() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    String result = extractor.state("NP", 2);
    assertEquals("NP^2", result);
  }
@Test
  public void testGetStateSplitCountReturnsZeroIfNotSet() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    assertEquals(0, extractor.getStateSplitCount("NP"));
  }
@Test
  public void testNegInfDoublesCreatesCorrectArray() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    double[] result = extractor.neginfDoubles(3);

    assertEquals(3, result.length);
    assertEquals(Double.NEGATIVE_INFINITY, result[0], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[1], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[2], 0.0);
  }
@Test
  public void testSplitStateCountsDoublesRootIgnored() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("S"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    extractor.stateSplitCounts.incrementCount("NP", 1);
    extractor.stateSplitCounts.incrementCount("VP", 1);
    extractor.stateSplitCounts.incrementCount("S", 1);
    extractor.splitStateCounts();

    assertEquals(2, extractor.getStateSplitCount("NP"));
    assertEquals(2, extractor.getStateSplitCount("VP"));
    assertEquals(1, extractor.getStateSplitCount("S"));
  }
@Test
  public void testSplitBetasUnaryExpandsStates() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    extractor.unaryBetas.put("X", "Y", new double[][] {{0.0}});
    extractor.stateSplitCounts.setCount("X", 1);
    extractor.stateSplitCounts.setCount("Y", 1);
    extractor.splitBetas();

    double[][] newBetas = extractor.unaryBetas.get("X", "Y");
    assertEquals(2, newBetas.length);
    assertEquals(2, newBetas[0].length); 
  }
@Test
  public void testSplitBetasBinaryExpandsStates() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    extractor.binaryBetas.put("A", "B", "C", new double[][][] {{{0.0}}});
    extractor.stateSplitCounts.setCount("A", 1);
    extractor.stateSplitCounts.setCount("B", 1);
    extractor.stateSplitCounts.setCount("C", 1);
    extractor.splitBetas();

    double[][][] updated = extractor.binaryBetas.get("A", "B", "C");
    assertNotNull(updated);
    assertTrue(updated.length >= 2); 
    assertTrue(updated[0].length >= 2); 
    assertTrue(updated[0][0].length >= 2); 
  }
@Test
  public void testRecalculateTemporaryBetasAssignsLexicon() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    mockOptions.tlpParams = new DummyLexiconFactory();

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    DummyTree tree = DummyTree.simplePreTerminal("NN", "dog");
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);
    extractor.trees = trees;
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, null, unary, binary);
    assertNotNull(extractor.tempLex);
  }
@Test
  public void testUseNewBetasAssignsLexicon() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    mockOptions.tlpParams = new DummyLexiconFactory();

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    extractor.tempWordIndex = new DummyIndex<>();
    extractor.tempTagIndex = new DummyIndex<>();
    extractor.tempLex = new DummyLexicon();

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    double[][] valuesUnary = new double[1][1];
    valuesUnary[0][0] = 0.0;
    unary.put("A", "B", valuesUnary);

    double[][][] valuesBinary = new double[1][1][1];
    valuesBinary[0][0][0] = 0.0;
    binary.put("A", "B", "C", valuesBinary);

    boolean converged = extractor.useNewBetas(true, unary, binary);
    assertTrue(converged);
    assertSame(extractor.lex, extractor.tempLex);
  }
@Test
  public void testRecalculateTemporaryBetasUpdatesLexiconCounts() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    mockOptions.tlpParams = new DummyLexiconFactory();

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    Tree tree = DummyTree.preTerminal("NN", "dog");
    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, null, unary, binary);

    assertNotNull(extractor.tempLex);
    assertTrue(extractor.tempWordIndex.size() > 0);
    assertTrue(extractor.tempTagIndex.size() > 0);
  }
@Test
  public void testBuildStateIndexAddsCorrectStates() {
    Options mockOptions = new Options();
    mockOptions.setLangpack(new DummyLangpack("ROOT"));
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(mockOptions);

    extractor.stateSplitCounts.setCount("NP", 2);
    extractor.stateSplitCounts.setCount("VP", 1);
    extractor.originalStates.add("NP");
    extractor.originalStates.add("VP");

    extractor.buildStateIndex();

    assertEquals(3, extractor.stateIndex.size());
    assertTrue(extractor.stateIndex.contains("NP^0"));
    assertTrue(extractor.stateIndex.contains("NP^1"));
    assertTrue(extractor.stateIndex.contains("VP^0"));
  } 
}