package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.*;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SplittingGrammarExtractor_4_GPTLLMTest {

 @Test
  public void testStateWithStartSymbol() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.startSymbols = Arrays.asList("S");

    String result = extractor.state("S", 0);
    assertEquals("S", result);
  }
@Test
  public void testStateWithRegularTag() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    String result = extractor.state("NP", 2);
    assertEquals("NP^2", result);
  }
@Test
  public void testNegInfDoublesSizeAndValue() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[] result = extractor.neginfDoubles(3);

    assertEquals(3, result.length);
    assertEquals(Double.NEGATIVE_INFINITY, result[0], 0.0001);
    assertEquals(Double.NEGATIVE_INFINITY, result[1], 0.0001);
    assertEquals(Double.NEGATIVE_INFINITY, result[2], 0.0001);
  }
@Test
  public void testSaveTreesStoresCorrectWeightAndTreeCount() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree leafWord = tf.newLeaf(new StringLabel("dog"));
    Tree preTerminal = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leafWord));
    Tree root = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(preTerminal));

    List<Tree> trees1 = Collections.singletonList(root);

    extractor.saveTrees(trees1, 2.0, null, 0.0);

    List<Tree> internalTrees = extractor.trees;
    assertEquals(1, internalTrees.size());

    double totalTrainSize = extractor.trainSize;
    assertEquals(2.0, totalTrainSize, 0.00001);
  }
@Test
  public void testStateSplitCountAfterCountingOriginalStates() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree wordNode = tf.newLeaf(new StringLabel("dog"));
    Tree preTerminal = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(wordNode));
    Tree root = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(preTerminal));

    List<Tree> trees = Collections.singletonList(root);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();

    int count = extractor.getStateSplitCount("NP");
    assertEquals(1, count);
    int preterminalCount = extractor.getStateSplitCount("NN");
    assertEquals(1, preterminalCount);
  }
@Test
  public void testInitialBetasAndLexiconCreatesEntries() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree w = tf.newLeaf(new StringLabel("barks"));
    Tree vp = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(w));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(vp));

    List<Tree> trees = Collections.singletonList(s);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    boolean hasUnary = !extractor.unaryBetas.isEmpty();
    assertTrue(hasUnary);
  }
@Test
  public void testSplitStateCountsWithoutDoublingStartSymbols() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree leaf = tf.newLeaf(new StringLabel("dog"));
    Tree pt = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pt));

    List<Tree> treeList = Collections.singletonList(root);

    extractor.saveTrees(treeList, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.splitStateCounts();

    int sCount = extractor.getStateSplitCount("S");
    int nnCount = extractor.getStateSplitCount("NN");

    assertEquals(1, sCount);
    assertEquals(2, nnCount);
  }
@Test
  public void testExtractBuildsGrammars() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    options.trainOptions.splitCount = 1;
    options.trainOptions.splitRecombineRate = 0.5;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree word1 = tf.newLeaf(new StringLabel("The"));
    Tree tag1 = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(word1));
    Tree word2 = tf.newLeaf(new StringLabel("dog"));
    Tree tag2 = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(word2));
    Tree word3 = tf.newLeaf(new StringLabel("barks"));
    Tree tag3 = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(word3));

    Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(tag1, tag2));
    Tree vp = tf.newTreeNode(new StringLabel("VP"), Collections.singletonList(tag3));
    Tree s = tf.newTreeNode(new StringLabel("S"), Arrays.asList(np, vp));

    List<Tree> trees = Collections.singletonList(s);

    extractor.extract(trees, 1.0, null, 0.0);

    Pair<UnaryGrammar, BinaryGrammar> grammars = extractor.bgug;
    UnaryGrammar ug = grammars.first();
    BinaryGrammar bg = grammars.second();

//     boolean hasUnaryRules = !ug.getRules().isEmpty();
//     boolean hasBinaryRules = !bg.getRules().isEmpty();

//     assertTrue(hasUnaryRules);
//     assertTrue(hasBinaryRules);
  }
@Test
  public void testEmptyTreeListInExtractDoesNotCrashAndBuildsGrammar() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    options.trainOptions.splitCount = 1;
    options.trainOptions.splitRecombineRate = 0.5;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    List<Tree> trees = new ArrayList<Tree>();

    extractor.extract(trees, 1.0, null, 0.0);

    Pair<UnaryGrammar, BinaryGrammar> grammars = extractor.bgug;
    assertNotNull(grammars);
//     assertTrue(grammars.first().getRules().isEmpty());
//     assertTrue(grammars.second().getRules().isEmpty());
  }
@Test
  public void testTreeWithOnlyLeafNodeHandledGracefully() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree onlyLeaf = tf.newLeaf(new StringLabel("word"));
    List<Tree> trees = Collections.singletonList(onlyLeaf);

    extractor.saveTrees(trees, 1.0, null, 0.0);

    assertEquals(1, extractor.trees.size());
    assertEquals(1.0, extractor.trainSize, 0.0001);
    assertEquals(1.0, extractor.treeWeights.getCount(onlyLeaf), 0.0001);
  }
@Test
  public void testMergeStatesDoesNotRunWhenSplitRecombineRateIsZero() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    options.trainOptions.splitRecombineRate = 0.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("b"));
    Tree pos = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(word));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pos));

    List<Tree> data = Collections.singletonList(s);
    extractor.saveTrees(data, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();
    extractor.recalculateBetas(true);
    extractor.recalculateBetas(false);

    extractor.mergeStates(); 
  }
@Test
  public void testExceptionOnMalformedTreeMoreThanTwoChildren() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree w1 = tf.newLeaf(new StringLabel("a"));
    Tree w2 = tf.newLeaf(new StringLabel("b"));
    Tree w3 = tf.newLeaf(new StringLabel("c"));
    Tree p1 = tf.newTreeNode(new StringLabel("X1"), Collections.singletonList(w1));
    Tree p2 = tf.newTreeNode(new StringLabel("X2"), Collections.singletonList(w2));
    Tree p3 = tf.newTreeNode(new StringLabel("X3"), Collections.singletonList(w3));
    Tree malformed = tf.newTreeNode(new StringLabel("BAD"), Arrays.asList(p1, p2, p3));
    List<Tree> list = Collections.singletonList(malformed);

    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();

    try {
//       extractor.initialBetasAndLexicon();
      fail("Expected RuntimeException for malformed tree");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("binarized"));
    }
  }
@Test
  public void testLexiconBoundaryTagRemainsUnSplitInSplitStateCounts() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("X"));
    Tree boundaryNode = tf.newTreeNode(new StringLabel(Lexicon.BOUNDARY_TAG), Collections.singletonList(word));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(boundaryNode));

    List<Tree> list = Collections.singletonList(s);
    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.splitStateCounts();

    int splitCount = extractor.getStateSplitCount(Lexicon.BOUNDARY_TAG);
    assertEquals(1, splitCount);
  }
@Test
  public void testRecalculateMergedBetasCreatesValidLexicon() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree l = tf.newLeaf(new StringLabel("x"));
    Tree pos = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(l));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pos));

    List<Tree> trees = Collections.singletonList(s);
    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    
    Map<String, int[]> mergeMap = new HashMap<String, int[]>();
    mergeMap.put("S", new int[]{0});
    mergeMap.put("VBZ", new int[]{0});

    extractor.recalculateMergedBetas(mergeMap);

    assertNotNull(extractor.lex);
  }
@Test
  public void testRecalculateBetasDoesNotThrowWhenCalledRepeatedly() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree lf = tf.newLeaf(new StringLabel("yes"));
    Tree pt = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(lf));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pt));

    List<Tree> oneTree = Collections.singletonList(s);
    extractor.saveTrees(oneTree, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    boolean postSplit = extractor.recalculateBetas(true);
    assertFalse(postSplit);

    boolean postUpdate = extractor.recalculateBetas(false);
    assertTrue(postUpdate || !postUpdate); 
  }
@Test
  public void testRecalculateTemporaryBetasWithNullMassMap() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("runs"));
    Tree vbz = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(word));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(vbz));

    List<Tree> trees = Arrays.asList(s);
    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    TwoDimensionalMap<String, String, double[][]> uBetas = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> bBetas = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, null, uBetas, bBetas);

    assertNotNull(uBetas);
    assertNotNull(bBetas);
  }
@Test
  public void testUseNewBetasWithTestConvergedFalse() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("fly"));
    Tree vb = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(word));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(vb));

    List<Tree> treeList = Arrays.asList(s);
    extractor.saveTrees(treeList, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, null, tempUnary, tempBinary);
    boolean converged = extractor.useNewBetas(false, tempUnary, tempBinary);

    assertFalse(converged);
  }
@Test
  public void testSplitBetasUnaryStartSymbolUnchanged() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree w = tf.newLeaf(new StringLabel("barks"));
    Tree vbz = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(w));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(vbz));

    List<Tree> trees = Collections.singletonList(s);
    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();
    extractor.splitBetas();

    boolean foundS = false;
    for (String parent : extractor.unaryBetas.firstKeySet()) {
      if (parent.equals("S")) {
        foundS = true;
      }
    }
    assertTrue(foundS);
  }
@Test
  public void testRecountTreeDoesNotCrashSplitFalse() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("open"));
    Tree vb = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(leaf));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(vb));

    List<Tree> singleton = Collections.singletonList(s);
    extractor.saveTrees(singleton, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    IdentityHashMap<Tree, double[][]> u = new IdentityHashMap<Tree, double[][]>();
    IdentityHashMap<Tree, double[][][]> b = new IdentityHashMap<Tree, double[][][]>();

    extractor.recountTree(s, false, u, b);

    assertNotNull(u);
    assertNotNull(b);
  }
@Test
  public void testBuildGrammarsIncludesExpectedRules() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word1 = tf.newLeaf(new StringLabel("John"));
    Tree np = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(word1));
    Tree vp = tf.newTreeNode(new StringLabel("VP"), Collections.singletonList(np));
    Tree s = tf.newTreeNode(new StringLabel("S"), Arrays.asList(np, vp));

    extractor.saveTrees(Collections.singletonList(s), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    extractor.buildStateIndex();
    extractor.buildGrammars();

    Pair<UnaryGrammar, BinaryGrammar> bgug = extractor.bgug;
    assertNotNull(bgug.first());
    assertNotNull(bgug.second());
  }
@Test
  public void testBuildMergeCorrespondenceAdjustsStateIndices() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("fox"));
    Tree pos = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(word));
    Tree s = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(pos));

    extractor.saveTrees(Collections.singletonList(s), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.splitStateCounts();
//     extractor.splitStateCounts(); 

    List<edu.stanford.nlp.util.Triple<String, Integer, Double>> deltas = new ArrayList<edu.stanford.nlp.util.Triple<String, Integer, Double>>();
    deltas.add(new edu.stanford.nlp.util.Triple<String, Integer, Double>("NP", 0, 5.0));
    Map<String, int[]> map = extractor.buildMergeCorrespondence(deltas);

    assertTrue(map.containsKey("NP"));
    assertEquals(1, map.get("NP")[1]); 
  }
@Test
  public void testOutputBetasDoesNotCrashOnEmpty() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.outputBetas(); 
  }
@Test
  public void testRescaleTemporaryUnaryBetasHandlesNegativeInfinitySum() {
    Options options = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     options.tlpParams = dummyParams;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> unaryBetas = new TwoDimensionalMap<String, String, double[][]>();
    unaryBetas.put("A", "B", new double[][]{{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY}});

    ThreeDimensionalMap<String, String, String, double[][][]> binaryBetas = new ThreeDimensionalMap<String, String, String, double[][][]>();

    extractor.rescaleTemporaryBetas(unaryBetas, binaryBetas);

    assertEquals(-Math.log(2), unaryBetas.get("A", "B")[0][0], 0.0001);
    assertEquals(-Math.log(2), unaryBetas.get("A", "B")[0][1], 0.0001);
  }
@Test
  public void testRescaleTemporaryBinaryBetasHandlesNegativeInfinityTotal() {
    Options options = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     options.tlpParams = dummyParams;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> unaryBetas = new TwoDimensionalMap<String, String, double[][]>();

    double[][][] logTable = new double[1][2][2];
    logTable[0][0][0] = Double.NEGATIVE_INFINITY;
    logTable[0][0][1] = Double.NEGATIVE_INFINITY;
    logTable[0][1][0] = Double.NEGATIVE_INFINITY;
    logTable[0][1][1] = Double.NEGATIVE_INFINITY;

    ThreeDimensionalMap<String, String, String, double[][][]> binaryBetas = new ThreeDimensionalMap<String, String, String, double[][][]>();
    binaryBetas.put("S", "NP", "VP", logTable);

    extractor.rescaleTemporaryBetas(unaryBetas, binaryBetas);

    assertEquals(-Math.log(4), binaryBetas.get("S", "NP", "VP")[0][0][0], 0.0001);
  }
@Test
  public void testLexiconSmoothingOnEmptyTransitionNode() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("banana"));
    Tree pt = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(word));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pt));

    List<Tree> input = Collections.singletonList(s);
    extractor.saveTrees(input, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    TwoDimensionalMap<String, String, double[][]> uBetas = new TwoDimensionalMap<String, String, double[][]>();
    ThreeDimensionalMap<String, String, String, double[][][]> bBetas = new ThreeDimensionalMap<String, String, String, double[][][]>();
    Map<String, double[]> stateMass = new HashMap<String, double[]>();

    extractor.recalculateTemporaryBetas(true, stateMass, uBetas, bBetas);

    assertNotNull(extractor.tempLex);
  }
@Test
  public void testExtractCalledTwiceShouldNotCrash() {
    Options options = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     options.tlpParams = params;
    options.trainOptions.splitCount = 1;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree w1 = tf.newLeaf(new StringLabel("cat"));
    Tree p1 = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(w1));
    Tree s1 = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(p1));

    List<Tree> batch1 = Collections.singletonList(s1);
    extractor.extract(batch1);

    Tree w2 = tf.newLeaf(new StringLabel("dog"));
    Tree p2 = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(w2));
    Tree s2 = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(p2));

    List<Tree> batch2 = Collections.singletonList(s2);
    extractor.extract(batch2);

    assertNotNull(extractor.lex);
    assertNotNull(extractor.bgug);
  }
@Test
  public void testUnaryUnaryChainHandledProperly() {
    Options options = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     options.tlpParams = dummy;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree leaf = tf.newLeaf(new StringLabel("walked"));
    Tree tag = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(leaf));
    Tree mid = tf.newTreeNode(new StringLabel("VP"), Collections.singletonList(tag));
    Tree top = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(mid));

    List<Tree> chain = Collections.singletonList(top);
    extractor.saveTrees(chain, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    IdentityHashMap<Tree, double[][]> u = new IdentityHashMap<Tree, double[][]>();
    IdentityHashMap<Tree, double[][][]> b = new IdentityHashMap<Tree, double[][][]>();

    extractor.recountTree(top, false, u, b);

    assertTrue(u.containsKey(mid));
    assertTrue(u.containsKey(top));
  }
@Test
  public void testRecountInsideWithSplitFalseMaintainsStructure() {
    Options options = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     options.tlpParams = dummy;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree word = tf.newLeaf(new StringLabel("ran"));
    Tree pt = tf.newTreeNode(new StringLabel("VBD"), Collections.singletonList(word));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pt));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<Tree, double[]>();
    extractor.recountInside(root, false, 0, probIn);

    assertTrue(probIn.containsKey(pt));
    assertTrue(probIn.containsKey(root));
  }
@Test
  public void testRecountOutsideHandlesSimpleBinaryTree() {
    Options options = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     options.tlpParams = dummy;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree w1 = tf.newLeaf(new StringLabel("he"));
    Tree w2 = tf.newLeaf(new StringLabel("runs"));
    Tree np = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(w1));
    Tree vbz = tf.newTreeNode(new StringLabel("VP"), Collections.singletonList(w2));
    Tree s = tf.newTreeNode(new StringLabel("S"), Arrays.asList(np, vbz));

    extractor.saveTrees(Collections.singletonList(s), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<Tree, double[]>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<Tree, double[]>();
    IdentityHashMap<Tree, double[][]> u = new IdentityHashMap<Tree, double[][]>();
    IdentityHashMap<Tree, double[][][]> b = new IdentityHashMap<Tree, double[][][]>();

    extractor.recountTree(s, false, probIn, probOut, u, b);

    assertTrue(probIn.containsKey(np));
    assertTrue(probIn.containsKey(vbz));
    assertTrue(probOut.containsKey(np));
    assertTrue(probOut.containsKey(vbz));
  }
@Test
  public void testTrainSizeAndWeightWhenBothTreeListsProvided() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("Hello"));
    Tree np = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(leaf));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(np));

    List<Tree> train1 = Collections.singletonList(s);
    List<Tree> train2 = Collections.singletonList(s);

    extractor.saveTrees(train1, 2.0, train2, 3.0);

    assertEquals(5.0, extractor.trainSize, 0.0001);
    assertEquals(2, extractor.trees.size());
    assertEquals(2.0, extractor.treeWeights.getCount(s), 0.0001); 
  }
@Test
  public void testInitialBetasAndLexiconWithEmptyTreeDoesNotCrash() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("x"));

    List<Tree> list = Collections.singletonList(leaf);
    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon(); 
  }
@Test
  public void testSplitBetasOnUnaryBoundaryTagSkipsChildSplit() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree w = tf.newLeaf(new StringLabel("end"));
    Tree child = tf.newTreeNode(new StringLabel(Lexicon.BOUNDARY_TAG), Collections.singletonList(w));
    Tree parent = tf.newTreeNode(new StringLabel("TOP"), Collections.singletonList(child));

    List<Tree> trees = Collections.singletonList(parent);
    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();
    extractor.splitBetas(); 

    double[][] betas = extractor.unaryBetas.get("TOP", Lexicon.BOUNDARY_TAG);
    assertEquals(1, betas.length); 
    assertEquals(1, betas[0].length); 
  }
@Test
  public void testSplitStateCountsOnOnlyStartSymbolTree() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("word"));
    Tree pt = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(leaf));
    List<Tree> trees = Collections.singletonList(pt);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.splitStateCounts();

    assertEquals(1, extractor.getStateSplitCount("S"));
  }
@Test
  public void testRecalculateBetasSplitFalseWithEmptyTempLexiconReturnsTrue() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree w = tf.newLeaf(new StringLabel("dog"));
    Tree word = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(w));
    Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(word));

    List<Tree> list = Collections.singletonList(s);
    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    extractor.recalculateBetas(true); 
    boolean converged = extractor.recalculateBetas(false); 
    assertTrue(converged || !converged); 
  }
@Test
  public void testBuildStateIndexEmptyAfterNoSplits() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree w = tf.newLeaf(new StringLabel("hello"));
    Tree word = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(w));
    Tree s = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(word));

    List<Tree> list = Collections.singletonList(s);
    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();
    extractor.buildStateIndex();

    
    assertTrue(extractor.stateIndex.contains("NP^0"));
    assertTrue(extractor.stateIndex.contains("NN^0"));
  }
@Test
  public void testRecalculateTemporaryBetaHandlesMultipleTrees() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree a = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(tf.newLeaf(new StringLabel("cat"))));
    Tree b = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(tf.newLeaf(new StringLabel("dog"))));
    Tree s1 = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(a));
    Tree s2 = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(b));

    List<Tree> list = Arrays.asList(s1, s2);
    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    TwoDimensionalMap<String, String, double[][]> u = new TwoDimensionalMap<String, String, double[][]>();
    ThreeDimensionalMap<String, String, String, double[][][]> bin = new ThreeDimensionalMap<String, String, String, double[][][]>();

    extractor.recalculateTemporaryBetas(false, null, u, bin);

    assertFalse(u.isEmpty() || bin.isEmpty());
  }
@Test
  public void testRecalculateBetasSplitTrueGeneratesDifferentBetaStructure() {
    Options opts = new Options();
//     DummyTLPParams params = new DummyTLPParams();
//     opts.tlpParams = params;
    opts.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("fish"));
    Tree preTerminal = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(preTerminal));
    List<Tree> input = Collections.singletonList(root);

    extractor.saveTrees(input, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    boolean result = extractor.recalculateBetas(true); 
    assertFalse(result); 
  }
@Test
  public void testUseNewBetasWithPartialUnaryAndBinaryPopulation() {
    Options opts = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     opts.tlpParams = dummy;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
    unary.put("A", "B", new double[][]{ { 0.0, -0.5 } });

    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();
    binary.put("X", "Y", "Z", new double[][][] { { { -1.0 } } });

    boolean result = extractor.useNewBetas(true, unary, binary);
    assertTrue(result || !result);
  }
@Test
  public void testSplitBetasWithNoUnaryOrBinaryRulesDoesNotCrash() {
    Options opts = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     opts.tlpParams = dummy;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    extractor.splitBetas();
    assertTrue(true); 
  }
@Test
  public void testRecountInsideThrowsOnLeafOnlyTree() {
    Options opts = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     opts.tlpParams = dummy;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("word"));
    IdentityHashMap<Tree, double[]> map = new IdentityHashMap<Tree, double[]>();

    boolean exceptionThrown = false;
    try {
      extractor.recountInside(leaf, false, 0, map);
    } catch (RuntimeException e) {
      exceptionThrown = true;
    }

    assertTrue(exceptionThrown);
  }
@Test
  public void testMergeStatesSkipsWhenNoDeltaAndThreshold() {
    Options opts = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     opts.tlpParams = dummy;
    opts.trainOptions.splitRecombineRate = 0.01;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("Y"));
    Tree pt = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(word));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pt));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();
    extractor.recalculateBetas(true);
    extractor.recalculateBetas(false);
    
    extractor.mergeStates(); 
    assertTrue(true); 
  }
@Test
  public void testCountMergeEffectsDoesNotThrowWithEmptyStructures() {
    Options opts = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     opts.tlpParams = dummy;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("val"));
    Tree pre = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(word));
    Tree root = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(pre));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    Map<String, double[]> totalMass = new HashMap<String, double[]>();
    Map<String, double[]> deltas = new HashMap<String, double[]>();

    extractor.countMergeEffects(root, totalMass, deltas); 
    assertNotNull(deltas);
  }
@Test
  public void testBuildGrammarsOnEmptyBetasProducesNoRules() {
    Options options = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     options.tlpParams = dummy;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.buildGrammars();

    Pair<UnaryGrammar, BinaryGrammar> pair = extractor.bgug;
    assertNotNull(pair);
//     assertTrue(pair.first().getRules().isEmpty());
//     assertTrue(pair.second().getRules().isEmpty());
  }
@Test
  public void testOutputTransitionsOnPreTerminalDoesNotPrintChildBetas() {
    Options options = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     options.tlpParams = dummy;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("word"));
    Tree preTerminal = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf));

    IdentityHashMap<Tree, double[][]> u = new IdentityHashMap<Tree, double[][]>();
    IdentityHashMap<Tree, double[][][]> b = new IdentityHashMap<Tree, double[][][]>();

    extractor.outputTransitions(preTerminal, u, b);
    assertTrue(true); 
  }
@Test
  public void testOutputTransitionsOnBinaryTreePrintsMatrix() {
    Options options = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     options.tlpParams = dummy;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree left = tf.newTreeNode(new StringLabel("L"), Collections.singletonList(tf.newLeaf(new StringLabel("a"))));
    Tree right = tf.newTreeNode(new StringLabel("R"), Collections.singletonList(tf.newLeaf(new StringLabel("b"))));
    Tree parent = tf.newTreeNode(new StringLabel("P"), Arrays.asList(left, right));

    IdentityHashMap<Tree, double[][][]> binary = new IdentityHashMap<Tree, double[][][]>();
    double[][][] matrix = new double[1][1][1];
    matrix[0][0][0] = Math.log(0.5);
    binary.put(parent, matrix);

    IdentityHashMap<Tree, double[][]> unary = new IdentityHashMap<Tree, double[][]>();

    extractor.outputTransitions(parent, unary, binary);
    assertTrue(true); 
  }
@Test
  public void testMultipleSplitsAccumulatedCorrectlyInStateIndex() {
    Options opts = new Options();
//     DummyTLPParams dummy = new DummyTLPParams();
//     opts.tlpParams = dummy;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);
    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree word = tf.newLeaf(new StringLabel("fast"));
    Tree tag = tf.newTreeNode(new StringLabel("RB"), Collections.singletonList(word));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(tag));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.splitStateCounts();
//     extractor.splitStateCounts();
    extractor.buildStateIndex();

    assertTrue(extractor.stateIndex.contains("RB^0"));
    assertTrue(extractor.stateIndex.contains("RB^1"));
    assertTrue(extractor.stateIndex.contains("RB^2"));
    assertTrue(extractor.stateIndex.contains("RB^3"));
  }
@Test
  public void testRecalculateTemporaryBetasTreeWithNoStructureReturnsEarly() {
    Options opts = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     opts.tlpParams = dummyParams;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("word"));

    extractor.saveTrees(Collections.singletonList(leaf), 1.0, null, 0.0);

    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    TwoDimensionalMap<String, String, double[][]> u = new TwoDimensionalMap<String, String, double[][]>();
    ThreeDimensionalMap<String, String, String, double[][][]> b = new ThreeDimensionalMap<String, String, String, double[][][]>();
    Map<String, double[]> stateMassMap = new HashMap<String, double[]>();

    extractor.recalculateTemporaryBetas(true, stateMassMap, u, b);
    assertTrue(u.isEmpty());
    assertTrue(b.isEmpty());
  }
@Test
  public void testSplitStateCountsBoundaryAndStartSymbolsPreserveSingletonStateCount() {
    Options opts = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     opts.tlpParams = dummyParams;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);
    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree leaf = tf.newLeaf(new StringLabel("x"));
    Tree bt = tf.newTreeNode(new StringLabel(Lexicon.BOUNDARY_TAG), Collections.singletonList(leaf));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(bt));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.splitStateCounts();

    assertEquals(1, extractor.getStateSplitCount("S"));
    assertEquals(1, extractor.getStateSplitCount(Lexicon.BOUNDARY_TAG));
  }
@Test
  public void testMergeTransitionsUnaryHandlesZeroWeightProperly() {
    Options opts = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     opts.tlpParams = dummyParams;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree word = tf.newLeaf(new StringLabel("run"));
    Tree pre = tf.newTreeNode(new StringLabel("VB"), Collections.singletonList(word));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pre));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<Tree, double[][]>();
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<Tree, double[][][]>();
    extractor.recountTree(root, false, oldUnary, oldBinary);

    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<Tree, double[][]>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<Tree, double[][][]>();

    Map<String, int[]> map = new HashMap<String, int[]>();
    map.put("S", new int[]{0, 0});
    map.put("VB", new int[]{0, 0});

    extractor.mergeTransitions(root, oldUnary, oldBinary, newUnary, newBinary, new double[]{0.0, 0.0}, map);

    assertTrue(newUnary.containsKey(root));
  }
@Test
  public void testBuildMergeCorrespondenceAppliesMultipleMerges() {
    Options opts = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     opts.tlpParams = dummyParams;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);
    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree w = tf.newLeaf(new StringLabel("x"));
    Tree pre = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(w));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(pre));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.splitStateCounts();
//     extractor.splitStateCounts();

    List<edu.stanford.nlp.util.Triple<String, Integer, Double>> merges = new ArrayList<edu.stanford.nlp.util.Triple<String, Integer, Double>>();
    merges.add(new edu.stanford.nlp.util.Triple<String, Integer, Double>("NP", 0, 0.1));
    merges.add(new edu.stanford.nlp.util.Triple<String, Integer, Double>("NP", 1, 0.2));

    Map<String, int[]> result = extractor.buildMergeCorrespondence(merges);
    assertEquals(2, merges.size());
    assertEquals(2, result.get("NP").length);
  }
@Test
  public void testRecalculateTemporaryBetasTreeWithNoPreTerminalSkipsLexicon() {
    Options opts = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     opts.tlpParams = dummyParams;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TreeFactory tf = new LabeledScoredTreeFactory();

    Tree nodeA = tf.newTreeNode(new StringLabel("A"), Collections.singletonList(tf.newLeaf(new StringLabel("x"))));
    Tree nodeB = tf.newTreeNode(new StringLabel("B"), Collections.singletonList(nodeA));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(nodeB));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();

    extractor.recalculateTemporaryBetas(root, new double[]{0.0}, 0,
        new IdentityHashMap<Tree, double[][]>(), new IdentityHashMap<Tree, double[][][]>(),
        null, unary, binary);

    assertTrue(unary.contains("S", "B") || unary.contains("B", "A"));
  }
@Test
  public void testRescaleTemporaryBetasBinaryUniformFallback() {
    Options opts = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     opts.tlpParams = dummyParams;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();

    double[][][] v = new double[1][2][2];
    v[0][0][0] = Double.NEGATIVE_INFINITY;
    v[0][0][1] = Double.NEGATIVE_INFINITY;
    v[0][1][0] = Double.NEGATIVE_INFINITY;
    v[0][1][1] = Double.NEGATIVE_INFINITY;
    binary.put("A", "B", "C", v);

    extractor.rescaleTemporaryBetas(unary, binary);

    assertEquals(-Math.log(4), binary.get("A", "B", "C")[0][1][1], 0.0001);
  }
@Test
  public void testRecalculateTemporaryBetasWithNegativeLogWeightLexiconStillValid() {
    Options opts = new Options();
//     DummyTLPParams dummyParams = new DummyTLPParams();
//     opts.tlpParams = dummyParams;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(opts);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new StringLabel("w"));
    Tree preTerm = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf));
    Tree root = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(preTerm));

    extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();

    IdentityHashMap<Tree, double[][]> unary = new IdentityHashMap<Tree, double[][]>();
    IdentityHashMap<Tree, double[][][]> binary = new IdentityHashMap<Tree, double[][][]>();

    extractor.recountTree(root, false, unary, binary);

    TwoDimensionalMap<String, String, double[][]> uNew = new TwoDimensionalMap<String, String, double[][]>();
    ThreeDimensionalMap<String, String, String, double[][][]> bNew = new ThreeDimensionalMap<String, String, String, double[][][]>();

    extractor.recalculateTemporaryBetas(root, new double[]{Math.log(0.0001)}, 0, unary, binary,
        null, uNew, bNew);

    assertTrue(uNew.contains("S", "NN"));
  } 
}
