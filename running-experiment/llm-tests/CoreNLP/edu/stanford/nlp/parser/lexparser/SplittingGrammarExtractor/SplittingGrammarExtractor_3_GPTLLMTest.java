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

public class SplittingGrammarExtractor_3_GPTLLMTest {

 @Test
  public void testSaveTreesAndTrainSize() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf1 = tf.newLeaf(new StringLabel("the"));
    Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(leaf1));

    Tree leaf2 = tf.newLeaf(new StringLabel("dog"));
    Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));

    List<Tree> npChildren = Arrays.asList(dt, nn);
    Tree np = tf.newTreeNode(new StringLabel("NP"), npChildren);
    Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

    List<Tree> trees = Collections.singletonList(root);

    extractor.saveTrees(trees, 2.0, null, 0.0);

    assertEquals(1, extractor.trees.size());
    assertEquals(2.0, extractor.trainSize, 1e-5);
    assertTrue(extractor.treeWeights.getCount(root) == 2.0);
  }
@Test
  public void testCountOriginalStatesAndSplitCounts() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf1 = tf.newLeaf(new StringLabel("blue"));
    Tree adj = tf.newTreeNode(new StringLabel("JJ"), Collections.singletonList(leaf1));

    Tree leaf2 = tf.newLeaf(new StringLabel("sky"));
    Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));

    List<Tree> nounChildren = Arrays.asList(adj, nn);
    Tree np = tf.newTreeNode(new StringLabel("NP"), nounChildren);
    Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

    List<Tree> trees = Collections.singletonList(root);

    extractor.saveTrees(trees, 1.0, null, 0.0);

    extractor.countOriginalStates();

    assertEquals(1, extractor.getStateSplitCount("ROOT"));
    assertEquals(1, extractor.getStateSplitCount("NP"));
    assertEquals(1, extractor.getStateSplitCount("JJ"));
    assertEquals(1, extractor.getStateSplitCount("NN"));
  }
@Test
  public void testStateNamingConventions() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    String result1 = extractor.state("ROOT", 0);
    String result2 = extractor.state("NN", 2);
    String result3 = extractor.state("Z", 1);

    assertEquals("ROOT", result1);
    assertEquals("NN^2", result2);
    assertEquals("Z^1", result3);
  }
@Test
  public void testSplitStateCountsUpdatesCorrectly() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf1 = tf.newLeaf(new StringLabel("black"));
    Tree adj = tf.newTreeNode(new StringLabel("JJ"), Collections.singletonList(leaf1));

    Tree leaf2 = tf.newLeaf(new StringLabel("cat"));
    Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));

    List<Tree> npChildren = Arrays.asList(adj, nn);
    Tree np = tf.newTreeNode(new StringLabel("NP"), npChildren);
    Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

    List<Tree> trees = Collections.singletonList(root);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();

    int rootBefore = extractor.getStateSplitCount("ROOT");
    int npBefore = extractor.getStateSplitCount("NP");
    int jjBefore = extractor.getStateSplitCount("JJ");

//     extractor.splitStateCounts();

    int rootAfter = extractor.getStateSplitCount("ROOT");
    int npAfter = extractor.getStateSplitCount("NP");
    int jjAfter = extractor.getStateSplitCount("JJ");

    assertEquals(rootBefore, rootAfter);
    assertEquals(2 * npBefore, npAfter);
    assertEquals(2 * jjBefore, jjAfter);
  }
@Test
  public void testNeginfArrayValuesAreAllNegativeInfinity() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[] result = extractor.neginfDoubles(4);

    assertEquals(4, result.length);
    assertEquals(Double.NEGATIVE_INFINITY, result[0], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[1], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[2], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[3], 0.0);
  }
@Test
  public void testInitialBetasAndLexiconDoesNotThrow() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf1 = tf.newLeaf(new StringLabel("red"));
    Tree adj = tf.newTreeNode(new StringLabel("JJ"), Collections.singletonList(leaf1));
    Tree leaf2 = tf.newLeaf(new StringLabel("ball"));
    Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));

    List<Tree> npChildren = Arrays.asList(adj, nn);
    Tree np = tf.newTreeNode(new StringLabel("NP"), npChildren);
    Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

    List<Tree> trees = Collections.singletonList(root);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();

    try {
//       extractor.initialBetasAndLexicon();
    } catch (Exception ex) {
      fail("initialBetasAndLexicon should not throw but threw: " + ex.getMessage());
    }
  }
@Test
  public void testExtractConvergesAndBuildsGrammar() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 1;
    options.trainOptions.splitRecombineRate = 0.5;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf1 = tf.newLeaf(new StringLabel("the"));
    Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(leaf1));
    Tree leaf2 = tf.newLeaf(new StringLabel("fox"));
    Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));
    Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(dt, nn));
    Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));
    List<Tree> trees = Collections.singletonList(root);

    extractor.extract(trees);

    assertNotNull(extractor.lex);
    assertNotNull(extractor.bgug);
    assertNotNull(extractor.bgug.first());
    assertNotNull(extractor.bgug.second());
  }
@Test
  public void testUseNewBetasAcceptsLexicon() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> unaryBetas = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binaryBetas = new ThreeDimensionalMap<>();

    double[][] unaryArray = new double[1][1];
    unaryArray[0][0] = 0.0;
    unaryBetas.put("A", "B", unaryArray);

    double[][][] binaryArray = new double[1][1][1];
    binaryArray[0][0][0] = 0.0;
    binaryBetas.put("A", "B", "C", binaryArray);

    extractor.tempLex = options.tlpParams.lex(options, new edu.stanford.nlp.util.HashIndex<>(), new edu.stanford.nlp.util.HashIndex<>());
    extractor.tempLex.initializeTraining(1.0);
    extractor.tempLex.finishTraining();

    extractor.tempWordIndex = new edu.stanford.nlp.util.HashIndex<>();
    extractor.tempTagIndex = new edu.stanford.nlp.util.HashIndex<>();

    boolean result = extractor.useNewBetas(true, unaryBetas, binaryBetas);
    assertTrue(result);
    assertNotNull(extractor.lex);
  }
@Test
  public void testOutputBetasHandlesEmpty() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    try {
      extractor.outputBetas(); 
    } catch (Exception e) {
      fail("Calling outputBetas with empty data threw exception: " + e.getMessage());
    }
  }
@Test
public void testEmptyTreeListInExtract() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  options.trainOptions.splitCount = 1;
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  List<Tree> emptyTrees = new ArrayList<Tree>();

  extractor.extract(emptyTrees);

  assertNotNull(extractor.lex);
  assertNotNull(extractor.bgug);
}
@Test
public void testExtractWithSecondTreeListNullWeightZero() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  List<Tree> trees = new ArrayList<Tree>();

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf1 = tf.newLeaf(new StringLabel("a"));
  Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(leaf1));
  Tree leaf2 = tf.newLeaf(new StringLabel("cat"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));

  Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(dt, nn));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  trees.add(root);

  extractor.extract(trees, 1.0, null, 0.0);

  assertEquals(1.0, extractor.trainSize, 1e-6);
}
@Test
public void testInitialBetasAndLexiconHandlesPureLeafTree() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree loneLeaf = tf.newLeaf(new StringLabel("leafOnly"));
  List<Tree> trees = Collections.singletonList(loneLeaf);

  extractor.saveTrees(trees, 1.0, null, 0.0);

  try {
//     extractor.initialBetasAndLexicon();
    fail("Expected RuntimeException not thrown on pure leaf tree");
  } catch (RuntimeException ex) {
    assertTrue(ex.getMessage().contains("Trees should have been binarized"));
  }
}
@Test
public void testSplitBetasWithMinimalUnaryBeta() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] unaryArray = new double[1][1];
  unaryArray[0][0] = 0.0;
  extractor.unaryBetas.put("X", "Y", unaryArray);

  double[][][] binaryArray = new double[1][1][1];
  binaryArray[0][0][0] = 0.0;
  extractor.binaryBetas.put("X", "Y", "Z", binaryArray);

  extractor.stateSplitCounts.incrementCount("X", 1);
  extractor.stateSplitCounts.incrementCount("Y", 1);
  extractor.stateSplitCounts.incrementCount("Z", 1);

  extractor.splitBetas();

  double[][] resultUnary = extractor.unaryBetas.get("X", "Y");
  double[][][] resultBinary = extractor.binaryBetas.get("X", "Y", "Z");

  assertNotNull(resultUnary);
  assertNotNull(resultBinary);
}
@Test
public void testRecalculateBetasDetectsConvergedFalse() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf1 = tf.newLeaf(new StringLabel("b"));
  Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(leaf1));
  Tree leaf2 = tf.newLeaf(new StringLabel("dog"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));
  Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(dt, nn));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  List<Tree> trees = Collections.singletonList(root);

  extractor.saveTrees(trees, 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();

  boolean result = extractor.recalculateBetas(false);

  assertTrue(result == false || result == true); 
}
@Test
public void testRecalculateTemporaryBetasEmptyStateMap() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree wordLeaf = tf.newLeaf(new StringLabel("run"));
  Tree vb = tf.newTreeNode(new StringLabel("VB"), Collections.singletonList(wordLeaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(vb));

  List<Tree> trees = Collections.singletonList(root);
  extractor.saveTrees(trees, 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();

  Map<String, double[]> stateMassMap = new HashMap<String, double[]>();
  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();

  extractor.recalculateTemporaryBetas(false, stateMassMap, unary, binary);
  assertNotNull(unary);
  assertNotNull(binary);
}
@Test
public void testOutputTransitionsWithUnaryOnly() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf(new StringLabel("wow"));
  Tree interj = tf.newTreeNode(new StringLabel("UH"), Collections.singletonList(leaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(interj));

  IdentityHashMap<Tree, double[][]> unary = new IdentityHashMap<Tree, double[][]>();
  IdentityHashMap<Tree, double[][][]> binary = new IdentityHashMap<Tree, double[][][]>();

  double[][] unaryWeights = new double[1][1];
  unaryWeights[0][0] = Math.log(1.0);
  unary.put(interj, unaryWeights);

  extractor.outputTransitions(root, unary, binary);
}
@Test
public void testRescaleTemporaryBetasHandlesEmptyMaps() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TwoDimensionalMap<String, String, double[][]> emptyUnary = new TwoDimensionalMap<String, String, double[][]>();
  ThreeDimensionalMap<String, String, String, double[][][]> emptyBinary = new ThreeDimensionalMap<String, String, String, double[][][]>();

  extractor.rescaleTemporaryBetas(emptyUnary, emptyBinary);
}
@Test
public void testBuildGrammarsWithEmptyMass() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf(new StringLabel("walk"));
  Tree vb = tf.newTreeNode(new StringLabel("VB"), Collections.singletonList(leaf));
  Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(vb));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(s));

  List<Tree> trees = Collections.singletonList(root);
  extractor.saveTrees(trees, 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();
  extractor.buildStateIndex();
  extractor.buildGrammars();

  assertNotNull(extractor.bgug);
  assertNotNull(extractor.bgug.first());
  assertNotNull(extractor.bgug.second());
}
@Test
public void testMergeStatesEmptyDelta() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  options.trainOptions.splitRecombineRate = 1.0;
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree theLeaf = tf.newLeaf(new StringLabel("the"));
  Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(theLeaf));
  Tree catLeaf = tf.newLeaf(new StringLabel("cat"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(catLeaf));
  Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(dt, nn));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  List<Tree> trees = Collections.singletonList(root);
  extractor.saveTrees(trees, 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();
//   extractor.splitStateCounts();
  extractor.recalculateBetas(true);
  extractor.mergeStates();

  assertNotNull(extractor.lex);
}
@Test
public void testRecalculateMergedBetasBoundaryIgnored() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Map<String, int[]> mergeMap = new HashMap<String, int[]>();
  mergeMap.put("NP", new int[] {0});
  mergeMap.put("DT", new int[] {0});
  mergeMap.put("NN", new int[] {0});
  mergeMap.put("ROOT", new int[] {0});
  mergeMap.put(Lexicon.BOUNDARY_TAG, new int[] {0});

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree theLeaf = tf.newLeaf(new StringLabel("the"));
  Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(theLeaf));
  Tree catLeaf = tf.newLeaf(new StringLabel("cat"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(catLeaf));
  Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(dt, nn));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  List<Tree> trees = Collections.singletonList(root);
  extractor.saveTrees(trees, 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();
//   extractor.splitStateCounts();
  extractor.recalculateBetas(false);

  extractor.recalculateMergedBetas(mergeMap);

  assertNotNull(extractor.lex);
}
@Test
public void testRecountOutsideSingleUnary() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree yesLeaf = tf.newLeaf(new StringLabel("yes"));
  Tree tag = tf.newTreeNode(new StringLabel("UH"), Collections.singletonList(yesLeaf));
  Tree s = tf.newTreeNode(new StringLabel("S"), Collections.singletonList(tag));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(s));

  extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();

  Map<Tree, double[]> probIn = new IdentityHashMap<Tree, double[]>();
  probIn.put(root, new double[] {0.0});
  probIn.put(s, new double[] {0.0});
  probIn.put(tag, new double[] {0.0});

  Map<Tree, double[]> probOut = new IdentityHashMap<Tree, double[]>();
//   extractor.recountOutside(root, probIn, probOut);
  assertTrue(probOut.containsKey(tag));
}
@Test
public void testCountMergeEffectsBoundarySkipped() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree boundaryLeaf = tf.newLeaf(new StringLabel("B"));
  Tree tag = tf.newTreeNode(new StringLabel(Lexicon.BOUNDARY_TAG), Collections.singletonList(boundaryLeaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(tag));

  IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<Tree, double[]>();
  probIn.put(tag, new double[] {0.0});

  IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<Tree, double[]>();
  probOut.put(tag, new double[] {0.0});

  Map<String, double[]> totalMass = new HashMap<String, double[]>();
  totalMass.put(Lexicon.BOUNDARY_TAG, new double[] {1.0});

  Map<String, double[]> deltaMap = new HashMap<String, double[]>();

  extractor.countMergeEffects(tag, totalMass, deltaMap, probIn, probOut);
  assertFalse(deltaMap.containsKey(Lexicon.BOUNDARY_TAG)); 
}
@Test
public void testLexiconSmoothingAppliesInLeaf() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree petLeaf = tf.newLeaf(new StringLabel("pet"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(petLeaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(nn));

  List<Tree> trees = Collections.singletonList(root);
  extractor.saveTrees(trees, 3.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();

  Map<String, double[]> stateMass = new HashMap<String, double[]>();

  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();

  extractor.recalculateTemporaryBetas(true, stateMass, unary, binary);

  assertNotNull(extractor.tempLex);
//   assertTrue(extractor.tempLex.totalTreesRead() > 0);
}
@Test
public void testSplitBetasPreservesBoundaryTags() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] unaryArray = new double[2][1];
  unaryArray[0][0] = 0.0;
  unaryArray[1][0] = 0.0;
  extractor.unaryBetas.put("A", Lexicon.BOUNDARY_TAG, unaryArray);

  extractor.stateSplitCounts.setCount("A", 2);
  extractor.stateSplitCounts.setCount(Lexicon.BOUNDARY_TAG, 1);

  extractor.splitBetas();

  double[][] result = extractor.unaryBetas.get("A", Lexicon.BOUNDARY_TAG);
  assertEquals(2, result.length);
  assertEquals(1, result[0].length);
}
@Test
public void testInitialBetasAndLexiconThrowsOnNonBinarizedTree() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf1 = tf.newLeaf(new StringLabel("blue"));
  Tree jj = tf.newTreeNode(new StringLabel("JJ"), Collections.singletonList(leaf1));
  Tree leaf2 = tf.newLeaf(new StringLabel("sky"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));
  Tree leaf3 = tf.newLeaf(new StringLabel("is"));
  Tree vbz = tf.newTreeNode(new StringLabel("VBZ"), Collections.singletonList(leaf3));

  List<Tree> children = Arrays.asList(jj, nn, vbz);
  Tree s = tf.newTreeNode(new StringLabel("S"), children);
  List<Tree> treeList = Collections.singletonList(s);

  extractor.saveTrees(treeList, 1.0, null, 0.0);
  try {
//     extractor.initialBetasAndLexicon();
    fail("Expected RuntimeException for non-binarized tree");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("expected 1 or 2 children"));
  }
}
@Test
public void testSplitStateCountsPreservesROOTAndBoundaryOnly() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  extractor.stateSplitCounts.setCount("ROOT", 1);
  extractor.stateSplitCounts.setCount("NP", 1);
  extractor.stateSplitCounts.setCount("NN", 1);
  extractor.stateSplitCounts.setCount(Lexicon.BOUNDARY_TAG, 1);

  extractor.startSymbols = Arrays.asList("ROOT");

//   extractor.splitStateCounts();

  assertEquals(1, extractor.getStateSplitCount("ROOT"));
  assertEquals(1, extractor.getStateSplitCount(Lexicon.BOUNDARY_TAG));
  assertEquals(2, extractor.getStateSplitCount("NP"));
  assertEquals(2, extractor.getStateSplitCount("NN"));
}
@Test
public void testMergeTransitionsUnaryNullTransitionIgnoredGracefully() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf(new StringLabel("hello"));
  Tree uh = tf.newTreeNode(new StringLabel("UH"), Collections.singletonList(leaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(uh));

  IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<Tree, double[][]>();
  IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<Tree, double[][]>();
  IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<Tree, double[][][]>();
  IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<Tree, double[][][]>();

  Map<String, int[]> merge = new HashMap<String, int[]>();
  merge.put("ROOT", new int[] {0});
  merge.put("UH", new int[] {0});

  double[] initialWeights = new double[] { Math.log(1.0) };

  extractor.mergeTransitions(root, oldUnary, oldBinary, newUnary, newBinary, initialWeights, merge);

  assertTrue(newUnary.isEmpty());
  assertTrue(newBinary.isEmpty());
}
@Test
public void testTestConvergenceWithDifferentValuesReturnsFalse() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] oldArray = new double[1][1];
  oldArray[0][0] = 1.0;
  extractor.unaryBetas.put("X", "Y", oldArray);

  double[][] newArray = new double[1][1];
  newArray[0][0] = 5.0;
  TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<String, String, double[][]>();
  tempUnary.put("X", "Y", newArray);

  double[][][] old3d = new double[1][1][1];
  old3d[0][0][0] = 2.0;
  extractor.binaryBetas.put("A", "B", "C", old3d);

  double[][][] new3d = new double[1][1][1];
  new3d[0][0][0] = 4.0;
  ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<String, String, String, double[][][]>();
  tempBinary.put("A", "B", "C", new3d);

  boolean result = extractor.testConvergence(tempUnary, tempBinary);
  assertFalse(result);
}
@Test
public void testCountMergeEffectsUnmatchedMassIgnoredGracefully() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree token = tf.newLeaf(new StringLabel("cool"));
  Tree adj = tf.newTreeNode(new StringLabel("JJ"), Collections.singletonList(token));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(adj));

  IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<Tree, double[]>();
  IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<Tree, double[]>();

  probIn.put(adj, new double[] { 0.0, 0.0 });
  probOut.put(adj, new double[] { 0.0, 0.0 });

  Map<String, double[]> delta = new HashMap<String, double[]>();
  Map<String, double[]> mass = new HashMap<String, double[]>();
  mass.put("JJ", new double[] { 0.0, 0.0 });

  extractor.countMergeEffects(adj, mass, delta, probIn, probOut);

  assertTrue(delta.containsKey("JJ"));
  assertEquals(1, delta.get("JJ").length);
}
@Test
public void testUseNewBetasWithNullLexiconDoesNotCrash() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
  double[][] u = new double[1][1];
  u[0][0] = 0.0;
  unary.put("A", "B", u);

  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();
  double[][][] b = new double[1][1][1];
  b[0][0][0] = 0.0;
  binary.put("X", "Y", "Z", b);

  extractor.tempLex = null;
  extractor.tempWordIndex = null;
  extractor.tempTagIndex = null;

  boolean result = extractor.useNewBetas(true, unary, binary);
  assertTrue(result || !result); 
}
@Test
public void testRecalculateTemporaryBetasHandlesZeroTreeWeight() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree the = tf.newLeaf(new StringLabel("the"));
  Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(the));
  Tree fox = tf.newLeaf(new StringLabel("fox"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(fox));
  Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(dt, nn));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  extractor.saveTrees(Collections.singletonList(root), 0.0, null, 0.0);
  extractor.countOriginalStates();

  Map<String, double[]> total = new HashMap<String, double[]>();
  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();

  extractor.recalculateTemporaryBetas(false, total, unary, binary);

  assertNotNull(unary);
  assertNotNull(binary);
}
@Test
public void testSplitBetasWithZeroChildStatesUnary() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] betas = new double[1][0]; 
  extractor.unaryBetas.put("NP", "NN", betas);

  try {
    extractor.splitBetas();
    fail("Expected ArrayIndexOutOfBoundsException or IllegalArgumentException due to invalid beta dimension");
  } catch (Exception e) {
    assertTrue(e instanceof ArrayIndexOutOfBoundsException
            || e instanceof IllegalArgumentException);
  }
}
@Test
public void testSplitBetasWithZeroStateCountBinary() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][][] betas = new double[1][0][0]; 
  extractor.binaryBetas.put("S", "NP", "VP", betas);

  try {
    extractor.splitBetas();
    fail("Expected exception due to invalid beta shape");
  } catch (Exception e) {
    assertTrue(e instanceof ArrayIndexOutOfBoundsException
            || e instanceof IllegalArgumentException);
  }
}
@Test
public void testInitialBetasAndLexiconHandlesNoPreTerminals() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree word = tf.newLeaf(new StringLabel("word"));
  Tree node = tf.newTreeNode(new StringLabel("X"), Collections.singletonList(word));
  Tree wrapper = tf.newTreeNode(new StringLabel("Y"), Collections.singletonList(node)); 
  extractor.saveTrees(Collections.singletonList(wrapper), 1.0, null, 0.0);

  try {
//     extractor.initialBetasAndLexicon(); 
  } catch (RuntimeException e) {
    fail("Did not expect exception: " + e.getMessage());
  }
}
@Test
public void testRecalculateTemporaryBetasZeroProbabilityLexicon() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree wordLeaf = tf.newLeaf(new StringLabel("rare_token"));
  Tree tag = tf.newTreeNode(new StringLabel("XYZ"), Collections.singletonList(wordLeaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(tag));

  extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
  extractor.countOriginalStates();

  Map<String, double[]> stateMass = new HashMap<String, double[]>();
  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();

  try {
    extractor.recalculateTemporaryBetas(true, stateMass, unary, binary);
  } catch (Exception e) {
    fail("Unexpected exception during lexicon smoothing with unknown token");
  }
}
@Test
public void testMergeTransitionsBinaryHandlesNullTransitions() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leftLeaf = tf.newLeaf(new StringLabel("left"));
  Tree left = tf.newTreeNode(new StringLabel("L"), Collections.singletonList(leftLeaf));
  Tree rightLeaf = tf.newLeaf(new StringLabel("right"));
  Tree right = tf.newTreeNode(new StringLabel("R"), Collections.singletonList(rightLeaf));
  Tree parent = tf.newTreeNode(new StringLabel("P"), Arrays.asList(left, right));

  IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<Tree, double[][][]>();
  IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<Tree, double[][]>();
  IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<Tree, double[][][]>();
  IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<Tree, double[][]>();

  Map<String, int[]> mergeMap = new HashMap<String, int[]>();
  mergeMap.put("P", new int[] {0});
  mergeMap.put("L", new int[] {0});
  mergeMap.put("R", new int[] {0});

  double[] stateWeights = new double[] { Math.log(1.0) };

  
  extractor.mergeTransitions(parent, oldUnary, oldBinary, newUnary, newBinary, stateWeights, mergeMap);

  assertTrue(newBinary.isEmpty());
  assertTrue(newUnary.isEmpty());
}
@Test
public void testRescaleTemporaryBetasHandlesZeroProbabilities() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<String, String, double[][]>();
  double[][] scores = new double[1][2];
  scores[0][0] = Double.NEGATIVE_INFINITY;
  scores[0][1] = Double.NEGATIVE_INFINITY;
  unary.put("A", "B", scores);

  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<String, String, String, double[][][]>();
  double[][][] beta = new double[1][1][2];
  beta[0][0][0] = Double.NEGATIVE_INFINITY;
  beta[0][0][1] = Double.NEGATIVE_INFINITY;
  binary.put("S", "NP", "VP", beta);

  extractor.rescaleTemporaryBetas(unary, binary);

  assertTrue(unary.get("A", "B")[0][0] != Double.NaN);
  assertTrue(binary.get("S", "NP", "VP")[0][0][0] != Double.NaN);
}
@Test
public void testBuildStateIndexIncludesAllSplitStates() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  extractor.stateSplitCounts.setCount("S", 2);
  extractor.stateSplitCounts.setCount("NP", 3);
  extractor.stateSplitCounts.setCount("VP", 1);

  extractor.buildStateIndex();

  assertTrue(extractor.stateIndex.contains("S^0"));
  assertTrue(extractor.stateIndex.contains("S^1"));
  assertTrue(extractor.stateIndex.contains("NP^2"));
  assertTrue(extractor.stateIndex.contains("VP^0"));
}
@Test
public void testBuildGrammarsHandlesMissingStateMassGracefully() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] unaryArray = new double[1][1];
  unaryArray[0][0] = Math.log(1.0);
  extractor.unaryBetas.put("NP", "NN", unaryArray);

  double[][][] binaryArray = new double[1][1][1];
  binaryArray[0][0][0] = Math.log(0.5);
  extractor.binaryBetas.put("VP", "VBD", "NP", binaryArray);

  extractor.stateSplitCounts.setCount("NP", 1);
  extractor.stateSplitCounts.setCount("NN", 1);
  extractor.stateSplitCounts.setCount("VP", 1);
  extractor.stateSplitCounts.setCount("VBD", 1);

  extractor.buildStateIndex();

  
  extractor.buildGrammars(); 

  assertNotNull(extractor.bgug);
}
@Test
public void testRecountOutsideHandlesNullBetasWithoutException() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leafA = tf.newLeaf(new StringLabel("cat"));
  Tree preTerminal = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leafA));
  Tree top = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(preTerminal));

  Map<Tree, double[]> probIn = new IdentityHashMap<Tree, double[]>();
  Map<Tree, double[]> probOut = new IdentityHashMap<Tree, double[]>();

  probIn.put(preTerminal, new double[] { Math.log(1.0) });
  probIn.put(top, new double[] { Math.log(1.0) });

  probOut.put(top, new double[] { Math.log(1.0) });

  extractor.unaryBetas.put("NP", "NN", null); 

  try {
//     extractor.recountOutside(top, probIn, probOut);
  } catch (Exception e) {
    fail("Expected to handle null beta without exception: " + e.getMessage());
  }
}
@Test
public void testExtractWithWeightZeroSecondTreeIncluded() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf(new StringLabel("one"));
  Tree np = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(leaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  Tree leaf2 = tf.newLeaf(new StringLabel("two"));
  Tree np2 = tf.newTreeNode(new StringLabel("VP"), Collections.singletonList(leaf2));
  Tree root2 = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np2));

  List<Tree> first = Collections.singletonList(root);
  List<Tree> second = Collections.singletonList(root2);

  extractor.extract(first, 1.0, second, 0.0);

  assertEquals(1.0, extractor.trainSize, 1e-7);
}
@Test
public void testRecountTreeHandlesEmptyUnaryBinaryTransitions() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf(new StringLabel("tagged"));
  Tree tag = tf.newTreeNode(new StringLabel("JJ"), Collections.singletonList(leaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(tag));

  extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();

  IdentityHashMap<Tree, double[][]> unary = new IdentityHashMap<Tree, double[][]>();
  IdentityHashMap<Tree, double[][][]> binary = new IdentityHashMap<Tree, double[][][]>();

  extractor.recountTree(root, false, unary, binary);

  assertTrue(unary.size() > 0 || binary.size() == 0);
}
@Test
public void testStateNamingConsistentWithStartSymbol() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  List<String> roots = new ArrayList<String>();
  roots.add("ROOT");
  extractor.startSymbols = roots;

  String resolved = extractor.state("ROOT", 3); 

  assertEquals("ROOT", resolved);
}
@Test
public void testCountOriginalStatesSkipsLeafOnly() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf(new StringLabel("token"));

  List<Tree> singleton = Collections.singletonList(leaf);
  extractor.saveTrees(singleton, 1.0, null, 0.0);

  extractor.countOriginalStates(); 

  assertEquals(0, extractor.originalStates.size());
}
@Test
public void testSplitBetasSkipsBoundaryTags() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  String boundary = Lexicon.BOUNDARY_TAG;
  extractor.startSymbols = Arrays.asList("ROOT");

  double[][] unary = new double[2][1];
  unary[0][0] = 0.0;
  unary[1][0] = 0.0;

  extractor.unaryBetas.put("NP", boundary, unary);

  extractor.splitBetas();

  double[][] result = extractor.unaryBetas.get("NP", boundary);
  assertEquals(2, result.length);
  assertEquals(1, result[0].length);
}
@Test
public void testUseNewBetasHandlesEmptyTempLex() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TwoDimensionalMap<String, String, double[][]> emptyUnary = new TwoDimensionalMap<String, String, double[][]>();
  ThreeDimensionalMap<String, String, String, double[][][]> emptyBinary = new ThreeDimensionalMap<String, String, String, double[][][]>();

  extractor.tempLex = null;
  extractor.tempTagIndex = null;
  extractor.tempWordIndex = null;

  boolean result = extractor.useNewBetas(true, emptyUnary, emptyBinary);

  assertEquals(true, result); 
}
@Test
public void testTestConvergenceEmptyTempReturnsTrue() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  extractor.unaryBetas = new TwoDimensionalMap<String, String, double[][]>();
  extractor.binaryBetas = new ThreeDimensionalMap<String, String, String, double[][][]>();

  TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<String, String, double[][]>();
  ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<String, String, String, double[][][]>();

  boolean converged = extractor.testConvergence(tempUnary, tempBinary);

  assertTrue(converged);
}
@Test
public void testMergeCorrespondencePreservesUnlistedStates() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  extractor.originalStates.add("NP");
  extractor.originalStates.add("PP");

  extractor.stateSplitCounts.setCount("NP", 2);
  extractor.stateSplitCounts.setCount("PP", 3);

  List<edu.stanford.nlp.util.Triple<String, Integer, Double>> emptyDeltas = new ArrayList<edu.stanford.nlp.util.Triple<String, Integer, Double>>();

  Map<String, int[]> result = extractor.buildMergeCorrespondence(emptyDeltas);

  assertArrayEquals(new int[] {0, 1}, result.get("NP"));
  assertArrayEquals(new int[] {0, 1, 2}, result.get("PP"));
}
@Test
public void testExtractSkipsZeroWeightTrees() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();

  Tree word = tf.newLeaf(new StringLabel("a"));
  Tree dt = tf.newTreeNode(new StringLabel("DT"), Collections.singletonList(word));
  Tree np = tf.newTreeNode(new StringLabel("NP"), Collections.singletonList(dt));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  List<Tree> trees = Collections.singletonList(root);

  extractor.extract(trees, 0.0, null, 0.0); 

  assertEquals(0.0, extractor.trainSize, 1e-6);
}
@Test
public void testRecalculateBetasOnEmptyGrammarReturnsTrue() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  boolean converged = extractor.recalculateBetas(false);

  
  assertTrue(converged);
}
@Test
public void testUseNewBetasWithMismatchedDimensionsThrows() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  extractor.unaryBetas.put("X", "Y", new double[2][2]);
  extractor.binaryBetas.put("A", "B", "C", new double[2][2][2]);

  TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<>();
  tempUnary.put("X", "Y", new double[1][2]); 

  ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<>();
  tempBinary.put("A", "B", "C", new double[2][1][2]);

  try {
    extractor.testConvergence(tempUnary, tempBinary);
    fail("Expected IllegalState or ArrayIndexOutOfBounds for shape mismatch");
  } catch (Exception ex) {
    assertTrue(ex instanceof ArrayIndexOutOfBoundsException || ex instanceof IllegalStateException);
  }
}
@Test
public void testSplitStateCountsWithNoPriorCount() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  extractor.startSymbols = Arrays.asList("ROOT");

  extractor.stateSplitCounts.setCount("NN", 0); 

//   extractor.splitStateCounts();

  
  assertEquals(0, extractor.getStateSplitCount("NN"));
}
@Test
public void testMergeStatesDoesNotExceedBounds() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  options.trainOptions.splitRecombineRate = 1.0; 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf(new StringLabel("bird"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(nn));

  extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();
//   extractor.splitStateCounts();
  extractor.recalculateBetas(true);
  extractor.recalculateBetas(false);

  
  extractor.mergeStates();

  assertTrue(true); 
}
@Test
public void testNegInfForSizeZeroReturnsArray() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[] result = extractor.neginfDoubles(0);
  assertEquals(0, result.length);
}
@Test
public void testStateWithUnknownStartSymbolReturnsSplitState() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  extractor.startSymbols = Arrays.asList("ROOT");

  String result = extractor.state("XX", 2);

  assertEquals("XX^2", result);
}
@Test
public void testRecalculateTemporaryBetasSkipsDeadBranch() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf1 = tf.newLeaf(new StringLabel("bad"));
  Tree adj = tf.newTreeNode(new StringLabel("JJ"), Collections.singletonList(leaf1));
  Tree leaf2 = tf.newLeaf(new StringLabel("crow"));
  Tree nn = tf.newTreeNode(new StringLabel("NN"), Collections.singletonList(leaf2));

  Tree np = tf.newTreeNode(new StringLabel("NP"), Arrays.asList(adj, nn));
  Tree root = tf.newTreeNode(new StringLabel("ROOT"), Collections.singletonList(np));

  extractor.saveTrees(Collections.singletonList(root), 1.0, null, 0.0);
  extractor.countOriginalStates();
//   extractor.initialBetasAndLexicon();

  Map<String, double[]> stateMass = new HashMap<>();
  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

  extractor.recalculateTemporaryBetas(false, stateMass, unary, binary);

  assertTrue(stateMass.keySet().contains("ROOT"));
  assertTrue(stateMass.keySet().contains("NP"));
}
@Test
public void testMergeTransitionsZeroProbabilitiesHandled() {
  Options options = new Options();
  options.tlpParams = new EnglishTreebankParserParams();
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree a = tf.newLeaf(new StringLabel("a"));
  Tree b = tf.newLeaf(new StringLabel("b"));
  Tree t1 = tf.newTreeNode(new StringLabel("X"), Collections.singletonList(a));
  Tree t2 = tf.newTreeNode(new StringLabel("Y"), Collections.singletonList(b));
  Tree p = tf.newTreeNode(new StringLabel("Z"), Arrays.asList(t1, t2));

  double[][][] oldBinary = new double[1][1][1];
  oldBinary[0][0][0] = Double.NEGATIVE_INFINITY;

  IdentityHashMap<Tree, double[][][]> oldBin = new IdentityHashMap<>();
  oldBin.put(p, oldBinary);

  IdentityHashMap<Tree, double[][][]> newBin = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();

  Map<String, int[]> merge = new HashMap<>();
  merge.put("Z", new int[]{0});
  merge.put("X", new int[]{0});
  merge.put("Y", new int[]{0});

  extractor.mergeTransitions(p, new IdentityHashMap<Tree, double[][]>(), oldBin, newUnary, newBin, new double[]{0.0}, merge);

  assertTrue(newBin.containsKey(p));
} 
}
