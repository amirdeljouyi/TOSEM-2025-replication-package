public class SplittingGrammarExtractor_wosr_2_GPTLLMTest { 

 @Test
  public void testStateSplitCountForTreeInitialValueIsZero() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode(new StringLabel("S"),
      Arrays.asList(
        tf.newTreeNode(new StringLabel("NP"),
          Arrays.asList(tf.newLeaf(new StringLabel("John")))
        ),
        tf.newTreeNode(new StringLabel("VP"),
          Arrays.asList(tf.newLeaf(new StringLabel("runs")))
        )
      )
    );

    assertEquals(0, extractor.getStateSplitCount(tree));
  }
@Test
  public void testStateSplitCountAfterCountOriginalStates() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode(new StringLabel("ROOT"),
      Arrays.asList(tf.newTreeNode(new StringLabel("S"),
        Arrays.asList(tf.newTreeNode(new StringLabel("NP"),
          Arrays.asList(tf.newTreeNode(new StringLabel("NNP"),
            Arrays.asList(tf.newLeaf(new StringLabel("John")))
          ))
        ))
      ))
    );

    List<Tree> trainingTrees = new ArrayList<>();
    trainingTrees.add(tree);

    extractor.saveTrees(trainingTrees, 1.0, null, 0.0);
    extractor.countOriginalStates();
    
    assertEquals(1, extractor.getStateSplitCount("ROOT"));
    assertEquals(1, extractor.getStateSplitCount("S"));
    assertEquals(1, extractor.getStateSplitCount("NP"));
    assertEquals(1, extractor.getStateSplitCount("NNP"));
  }
@Test
  public void testStateMethodForStartSymbolReturnsUnchanged() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    String tag = options.langpack().startSymbol();
    String stateTag = extractor.state(tag, 5);
    assertEquals(tag, stateTag);
  }
@Test
  public void testStateMethodForNonStartSymbolAppendsCaretAndIndex() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    String tag = "VB";
    String result = extractor.state(tag, 2);
    assertEquals("VB^2", result);
  }
@Test
  public void testSplitStateCountsCorrectDoubling() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.incrementCount("NP", 1);
    extractor.stateSplitCounts.incrementCount("VP", 1);
    extractor.stateSplitCounts.incrementCount(options.langpack().startSymbol(), 1);
    extractor.stateSplitCounts.incrementCount("BOUNDARY", 1);
    
    extractor.splitStateCounts();

    assertEquals(2, extractor.stateSplitCounts.getIntCount("NP"));
    assertEquals(2, extractor.stateSplitCounts.getIntCount("VP"));
    assertEquals(1, extractor.stateSplitCounts.getIntCount(options.langpack().startSymbol()));
    assertEquals(1, extractor.stateSplitCounts.getIntCount("BOUNDARY"));
  }
@Test
  public void testNegInfDoublesReturnsArrayOfNegInfinity() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[] result = extractor.neginfDoubles(4);
    assertEquals(4, result.length);
    assertEquals(Double.NEGATIVE_INFINITY, result[0], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[1], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[2], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[3], 0.0);
  }
@Test
  public void testExtractWithMinimalTreeExecutesWithoutError() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode(new StringLabel(options.langpack().startSymbol()),
      Arrays.asList(
        tf.newTreeNode(new StringLabel("NP"),
          Arrays.asList(tf.newLeaf(new StringLabel("dogs")))
        )
      )
    );

    List<Tree> trainingBatch = new ArrayList<>();
    trainingBatch.add(tree);

    extractor.extract(trainingBatch);
    assertNotNull(extractor.bgug);
  }
@Test
  public void testRecalculateBetasSplitStates() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode(new StringLabel("ROOT"),
      Arrays.asList(
        tf.newTreeNode(new StringLabel("X"),
          Arrays.asList(tf.newLeaf(new StringLabel("a")))
        )
      )
    );

    List<Tree> trainingBatch = new ArrayList<>();
    trainingBatch.add(tree);

    extractor.saveTrees(trainingBatch, 1.0, null, 0.0);
    extractor.countOriginalStates();
    extractor.initialBetasAndLexicon();
    extractor.splitStateCounts();
    boolean converged = extractor.recalculateBetas(true);

    assertFalse(converged); 
  }
@Test
  public void testBuildStateIndexPopulatesExpectedStates() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("NP", 2);
    extractor.stateSplitCounts.setCount("VP", 3);
    extractor.buildStateIndex();

    assertEquals(5, extractor.stateIndex.size());
    assertEquals("NP^0", extractor.stateIndex.get(0));
    assertEquals("NP^1", extractor.stateIndex.get(1));
    assertEquals("VP^0", extractor.stateIndex.get(2));
    assertEquals("VP^1", extractor.stateIndex.get(3));
    assertEquals("VP^2", extractor.stateIndex.get(4));
  }
@Test
  public void testBuildGrammarsAfterExtractGeneratesRules() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode(new StringLabel(options.langpack().startSymbol()),
      Arrays.asList(
        tf.newTreeNode(new StringLabel("NP"),
          Arrays.asList(tf.newTreeNode(new StringLabel("NN"),
            Arrays.asList(tf.newLeaf(new StringLabel("dog")))
          ))
        )
      )
    );

    List<Tree> trainingBatch = new ArrayList<>();
    trainingBatch.add(tree);

    extractor.extract(trainingBatch);

    Pair<?, ?> grammarPair = extractor.bgug;
    assertNotNull(grammarPair.first());
    assertNotNull(grammarPair.second());
  } 
}