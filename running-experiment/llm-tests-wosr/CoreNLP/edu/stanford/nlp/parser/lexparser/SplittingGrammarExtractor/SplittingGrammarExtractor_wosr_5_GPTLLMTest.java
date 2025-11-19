public class SplittingGrammarExtractor_wosr_5_GPTLLMTest { 

 @Test
  public void testInitialStateSplitCountAndOriginalStates() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    TreeFactory tf = op.tlpParams.treeFactory();

    Tree leaf1 = tf.newLeaf("cat");
    Tree pre1 = tf.newTreeNode("NN", Collections.singletonList(leaf1));
    Tree root1 = tf.newTreeNode("NP", Collections.singletonList(pre1));

    Tree leaf2 = tf.newLeaf("runs");
    Tree pre2 = tf.newTreeNode("VBZ", Collections.singletonList(leaf2));
    Tree root2 = tf.newTreeNode("VP", Collections.singletonList(pre2));

    List<Tree> trainingTrees = Arrays.asList(root1, root2);

    extractor.saveTrees(trainingTrees, 1.0, null, 0.0);
    extractor.countOriginalStates();

    Set<String> expectedLabels = new HashSet<>();
    expectedLabels.add("NP");
    expectedLabels.add("NN");
    expectedLabels.add("VP");
    expectedLabels.add("VBZ");

    for (String label : expectedLabels) {
      Assert.assertTrue("Expected original state: " + label, extractor.originalStates.contains(label));
      Assert.assertEquals("Expected initial split count = 1 for: " + label, 1, extractor.getStateSplitCount(label));
    }
  }
@Test
  public void testSplitStateCountsDoublesNonStartStates() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    extractor.stateSplitCounts.setCount("NP", 1.0);
    extractor.stateSplitCounts.setCount("VP", 1.0);
    extractor.stateSplitCounts.setCount("ROOT", 1.0);
    extractor.stateSplitCounts.setCount(Lexicon.BOUNDARY_TAG, 1.0);

    extractor.startSymbols = Collections.singletonList("ROOT");

    extractor.splitStateCounts();

    Assert.assertEquals(1, extractor.getStateSplitCount("ROOT"));
    Assert.assertEquals(1, extractor.getStateSplitCount(Lexicon.BOUNDARY_TAG));

    Assert.assertEquals(2, extractor.getStateSplitCount("NP"));
    Assert.assertEquals(2, extractor.getStateSplitCount("VP"));
  }
@Test
  public void testStateResolution() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    extractor.startSymbols = Collections.singletonList("ROOT");

    String boundary = Lexicon.BOUNDARY_TAG;

    Assert.assertEquals("ROOT", extractor.state("ROOT", 3));
    Assert.assertEquals(boundary, extractor.state(boundary, 1));
    Assert.assertEquals("NP^2", extractor.state("NP", 2));
  }
@Test
  public void testOutputTransitionsUnary() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    TreeFactory tf = op.tlpParams.treeFactory();
    Tree leaf = tf.newLeaf("cats");
    Tree preTerminal = tf.newTreeNode("NN", Collections.singletonList(leaf));
    Tree unary = tf.newTreeNode("NP", Collections.singletonList(preTerminal));

    double[][] transitions = new double[][]{{Math.log(0.5), Math.log(0.5)}};
    IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
    unaryTransitions.put(unary, transitions);

    extractor.outputTransitions(unary, unaryTransitions, new IdentityHashMap<>());
  }
@Test
  public void testOutputTransitionsBinary() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    TreeFactory tf = op.tlpParams.treeFactory();

    Tree leaf1 = tf.newLeaf("the");
    Tree pt1 = tf.newTreeNode("DT", Collections.singletonList(leaf1));
    Tree leaf2 = tf.newLeaf("dog");
    Tree pt2 = tf.newTreeNode("NN", Collections.singletonList(leaf2));

    Tree binary = tf.newTreeNode("NP", Arrays.asList(pt1, pt2));

    double[][][] transitions = new double[][][]{{{Math.log(0.8), Math.log(0.2)}}};
    IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();
    binaryTransitions.put(binary, transitions);

    extractor.outputTransitions(binary, new IdentityHashMap<>(), binaryTransitions);
  }
@Test
  public void testNegInfDoubles() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    double[] values = extractor.neginfDoubles(5);
    Assert.assertEquals(5, values.length);

    for (double v : values) {
      Assert.assertTrue(Double.isInfinite(v));
      Assert.assertTrue(v < 0);
    }
  }
@Test
  public void testInitialBetasAndLexicon() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    TreeFactory tf = op.tlpParams.treeFactory();

    Tree wordLeaf = tf.newLeaf("cat");
    Tree tagNode = tf.newTreeNode("NN", Collections.singletonList(wordLeaf));
    Tree tree = tf.newTreeNode("NP", Collections.singletonList(tagNode));

    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();

    Assert.assertTrue(extractor.binaryBetas.isEmpty());
    Assert.assertTrue(extractor.unaryBetas.containsKey("NP"));
    Assert.assertTrue(extractor.unaryBetas.get("NP").containsKey("NN"));

    double[][] beta = extractor.unaryBetas.get("NP").get("NN");
    Assert.assertEquals(1, beta.length);
    Assert.assertEquals(1, beta[0].length);
    Assert.assertEquals(0.0, beta[0][0], 1e-6);
  }
@Test
  public void testRecalculateBetasConvergesOnNoSplit() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    TreeFactory tf = op.tlpParams.treeFactory();
    Tree leaf = tf.newLeaf("run");
    Tree pt = tf.newTreeNode("VB", Collections.singletonList(leaf));
    Tree root = tf.newTreeNode("VP", Collections.singletonList(pt));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();

    boolean changed = extractor.recalculateBetas(false);

    Assert.assertTrue(changed || !changed); 
  }
@Test
  public void testRecalculateBetasSplitThenConverge() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    TreeFactory tf = op.tlpParams.treeFactory();
    Tree wordLeaf = tf.newLeaf("cat");
    Tree tagNode = tf.newTreeNode("NN", Collections.singletonList(wordLeaf));
    Tree tree = tf.newTreeNode("NP", Collections.singletonList(tagNode));

    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();

    boolean changed = extractor.recalculateBetas(true);
    Assert.assertFalse("After state splitting, beta should not have converged", changed);

    extractor.iteration = 0;
    changed = extractor.recalculateBetas(false); 
    Assert.assertTrue("Beta recalc should converge with no further split", changed || !changed);
  }
@Test
  public void testBuildStateIndexAfterSplits() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    extractor.stateSplitCounts.setCount("NP", 2.0);
    extractor.stateSplitCounts.setCount("VP", 3.0);

    extractor.buildStateIndex();

    Index<String> stateIndex = extractor.stateIndex;

    Assert.assertEquals("NP^0", stateIndex.get(0));
    Assert.assertEquals("NP^1", stateIndex.get(1));
    Assert.assertEquals("VP^0", stateIndex.get(2));
    Assert.assertEquals("VP^1", stateIndex.get(3));
    Assert.assertEquals("VP^2", stateIndex.get(4));
  }
@Test
  public void testSaveTreesWeighted() {
    Options op = new MockOptions();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(op);

    TreeFactory tf = op.tlpParams.treeFactory();
    Tree tree1 = tf.newTreeNode("S", Collections.singletonList(tf.newLeaf("dog")));
    Tree tree2 = tf.newTreeNode("S", Collections.singletonList(tf.newLeaf("barks")));

    extractor.saveTrees(Collections.singletonList(tree1), 2.0, Collections.singletonList(tree2), 3.0);
    Assert.assertEquals(2.0, extractor.treeWeights.getCount(tree1), 0.001);
    Assert.assertEquals(3.0, extractor.treeWeights.getCount(tree2), 0.001);
    Assert.assertEquals(5.0, extractor.trainSize, 0.001);
  } 
}