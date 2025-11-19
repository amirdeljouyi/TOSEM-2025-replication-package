public class SplittingGrammarExtractor_wosr_3_GPTLLMTest { 

 @Test
  public void testCountOriginalStates_singleTree() {
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree t = tf.newTreeNode("S", Arrays.asList(
      tf.newTreeNode("NP", Collections.singletonList(
        tf.newTreeNode("NN", Collections.singletonList(tf.newLeaf("dog"))))),
      tf.newTreeNode("VP", Collections.singletonList(
        tf.newTreeNode("VBZ", Collections.singletonList(tf.newLeaf("barks")))))
    ));

    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    extractor.saveTrees(Collections.singleton(t), 1.0, null, -1.0);
    extractor.countOriginalStates();

    assertEquals(3, extractor.originalStates.size());
    assertTrue(extractor.originalStates.contains("S"));
    assertTrue(extractor.originalStates.contains("NP"));
    assertTrue(extractor.originalStates.contains("VP"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("S"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("NP"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("VP"));
  }
@Test
  public void testStateWithStartSymbolReturnsUnmodified() {
    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();
    op.langpack().setStartSymbol("S");

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    extractor.startSymbols = Arrays.asList("S", "ROOT");

    String result = extractor.state("S", 3);
    assertEquals("S", result);
  }
@Test
  public void testStateWithNonStartSymbolReturnsModified() {
    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();
    op.langpack().setStartSymbol("S");

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    extractor.startSymbols = Arrays.asList("S", "ROOT");

    String result = extractor.state("NP", 2);
    assertEquals("NP^2", result);
  }
@Test
  public void testSplitStateCounts() {
    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    extractor.stateSplitCounts.incrementCount("NP", 1);
    extractor.stateSplitCounts.incrementCount("VP", 1);
    extractor.stateSplitCounts.incrementCount("S", 1);
    extractor.stateSplitCounts.incrementCount(Lexicon.BOUNDARY_TAG, 1);

    extractor.startSymbols = Arrays.asList("S");

    extractor.splitStateCounts();

    assertEquals(2, extractor.stateSplitCounts.getIntCount("NP"));
    assertEquals(2, extractor.stateSplitCounts.getIntCount("VP"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("S"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount(Lexicon.BOUNDARY_TAG));
  }
@Test
  public void testInitialBetasAndLexicon_createsUnaryBeta() {
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree child = tf.newTreeNode("NN", Collections.singletonList(tf.newLeaf("dog")));
    Tree preTerminal = tf.newTreeNode("NP", Collections.singletonList(child));

    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    extractor.trees.add(preTerminal);
    extractor.treeWeights.setCount(preTerminal, 1.0);
    extractor.trainSize = 1.0;

    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();

    double[][] unary = extractor.unaryBetas.get("NP", "NN");
    assertNotNull(unary);
    assertEquals(1, unary.length);
    assertEquals(1, unary[0].length);
    assertEquals(0.0, unary[0][0], 1e-7);
  }
@Test
  public void testInitialBetasAndLexicon_createsBinaryBeta() {
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree left = tf.newTreeNode("DT", Collections.singletonList(tf.newLeaf("the")));
    Tree right = tf.newTreeNode("NN", Collections.singletonList(tf.newLeaf("cat")));
    Tree np = tf.newTreeNode("NP", Arrays.asList(left, right));

    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    extractor.trees.add(np);
    extractor.treeWeights.setCount(np, 1.0);
    extractor.trainSize = 1.0;

    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();

    double[][][] binary = extractor.binaryBetas.get("NP", "DT", "NN");
    assertNotNull(binary);
    assertEquals(1, binary.length);
    assertEquals(1, binary[0].length);
    assertEquals(1, binary[0][0].length);
    assertEquals(0.0, binary[0][0][0], 1e-7);
  }
@Test
  public void testNegInfDoublesReturnsCorrectLengthAndValues() {
    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    double[] arr = extractor.neginfDoubles(4);
    assertEquals(4, arr.length);
    assertEquals(Double.NEGATIVE_INFINITY, arr[0], 0.0001);
    assertEquals(Double.NEGATIVE_INFINITY, arr[1], 0.0001);
    assertEquals(Double.NEGATIVE_INFINITY, arr[2], 0.0001);
    assertEquals(Double.NEGATIVE_INFINITY, arr[3], 0.0001);
  }
@Test
  public void testSaveTrees_addsCorrectWeightsAndSize() {
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree1 = tf.newTreeNode("NP", Collections.singletonList(tf.newLeaf("a")));
    Tree tree2 = tf.newTreeNode("VP", Collections.singletonList(tf.newLeaf("run")));

    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    extractor.saveTrees(Collections.singleton(tree1), 2.0, Collections.singleton(tree2), 0.5);

    assertEquals(2, extractor.trees.size());
    assertEquals(2.0, extractor.treeWeights.getCount(tree1), 0.0001);
    assertEquals(0.5, extractor.treeWeights.getCount(tree2), 0.0001);
    assertEquals(2.5, extractor.trainSize, 0.0001);
  }
@Test
  public void testSplitBetasUnaryStructureDoubling() {
    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    extractor.startSymbols = new ArrayList<>();
    double[][] beta = new double[1][1];
    beta[0][0] = Math.log(0.9);
    extractor.unaryBetas.put("A", "B", beta);

    extractor.splitBetas();

    assertTrue(extractor.unaryBetas.get("A", "B").length >= 2);
    assertTrue(extractor.unaryBetas.get("A", "B")[0].length >= 1);
  }
@Test
  public void testSplitBetasBinaryStructureDoubling() {
    Options op = new Options();
    op.tlpParams = new TestLexiconFactory.DummyEnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);
    extractor.startSymbols = new ArrayList<>();
    double[][][] beta = new double[1][1][1];
    beta[0][0][0] = Math.log(0.8);
    extractor.binaryBetas.put("A", "B", "C", beta);

    extractor.splitBetas();

    assertTrue(extractor.binaryBetas.get("A", "B", "C").length >= 2);
    assertTrue(extractor.binaryBetas.get("A", "B", "C")[0].length >= 2);
    assertTrue(extractor.binaryBetas.get("A", "B", "C")[0][0].length >= 2);
  } 
}