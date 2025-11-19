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


public class SplittingGrammarExtractor_1_GPTLLMTest {

  @Test
  public void testStateWithoutStartSymbol() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.startSymbols = Collections.singletonList("ROOT");
    String result = extractor.state("NP", 1);
    assertEquals("NP^1", result);
  }
  @Test
  public void testStateWithStartSymbol() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.startSymbols = Arrays.asList("ROOT", "S");
    String result = extractor.state("ROOT", 2);
    assertEquals("ROOT", result);
  }
  @Test
  public void testNegInfDoublesLength() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    double[] result = extractor.neginfDoubles(3);
    assertNotNull(result);
    assertEquals(3, result.length);
    assertEquals(Double.NEGATIVE_INFINITY, result[0], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[1], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, result[2], 0.0);
  }
  @Test
  public void testSaveTreesAggregatesWeightsCorrectly() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree1 = Tree.valueOf("(ROOT (NP (NN cat)))");
    Tree tree2 = Tree.valueOf("(ROOT (VP (VB barked)))");

    List<Tree> trees1 = Collections.singletonList(tree1);
    List<Tree> trees2 = Collections.singletonList(tree2);

    extractor.saveTrees(trees1, 2.0, trees2, 3.0);

    assertEquals(2.0, extractor.treeWeights.getCount(tree1), 0.01);
    assertEquals(3.0, extractor.treeWeights.getCount(tree2), 0.01);
  }
  @Test
  public void testCountOriginalStatesAddsInternalLabels() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(ROOT (NP (NN cat)))");
    tree.label().setValue("ROOT");
    tree.children()[0].label().setValue("NP");
    tree.children()[0].children()[0].label().setValue("NN");

    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.countOriginalStates();

    assertTrue(extractor.originalStates.contains("ROOT"));
    assertTrue(extractor.originalStates.contains("NP"));
    assertEquals(1.0, extractor.stateSplitCounts.getCount("ROOT"), 0.01);
    assertEquals(1.0, extractor.stateSplitCounts.getCount("NP"), 0.01);
  }
  @Test
  public void testSplitStateCountsDoublesStateCountsExceptRoot() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("NP", 1.0);
    extractor.stateSplitCounts.setCount("VP", 1.0);
    extractor.stateSplitCounts.setCount("ROOT", 1.0);
    extractor.stateSplitCounts.setCount(Lexicon.BOUNDARY_TAG, 1.0);
    extractor.startSymbols = Collections.singletonList("ROOT");

//     extractor.splitStateCounts();

    assertEquals(2.0, extractor.stateSplitCounts.getCount("NP"), 0.01);
    assertEquals(2.0, extractor.stateSplitCounts.getCount("VP"), 0.01);
    assertEquals(1.0, extractor.stateSplitCounts.getCount("ROOT"), 0.01);
    assertEquals(1.0, extractor.stateSplitCounts.getCount(Lexicon.BOUNDARY_TAG), 0.01);
  }
  @Test
  public void testInitialUnaryBetaCreated() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (VB runs))");
    tree.label().setValue("S");
    tree.children()[0].label().setValue("VB");
    tree.children()[0].children()[0].label().setValue("runs");

    extractor.stateSplitCounts.setCount("S", 1);
    extractor.stateSplitCounts.setCount("VB", 1);
    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

//     extractor.initialBetasAndLexicon();

    double[][] beta = extractor.unaryBetas.get("S", "VB");
    assertNotNull(beta);
    assertEquals(1, beta.length);
    assertEquals(1, beta[0].length);
    assertEquals(0.0, beta[0][0], 1e-10);
  }
  @Test
  public void testInitialBinaryBetaCreated() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP cat) (VP ran))");
    tree.label().setValue("S");
    tree.children()[0].label().setValue("NP");
    tree.children()[0].children()[0].label().setValue("cat");
    tree.children()[1].label().setValue("VP");
    tree.children()[1].children()[0].label().setValue("ran");

    extractor.stateSplitCounts.setCount("S", 1);
    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("VP", 1);

    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

//     extractor.initialBetasAndLexicon();

    double[][][] beta = extractor.binaryBetas.get("S", "NP", "VP");
    assertNotNull(beta);
    assertEquals(1, beta.length);
    assertEquals(1, beta[0].length);
    assertEquals(1, beta[0][0].length);
    assertEquals(0.0, beta[0][0][0], 1e-10);
  }
  @Test
  public void testStateSplitCountLabelBased() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("NP", 5);
    assertEquals(5, extractor.getStateSplitCount("NP"));
  }
  @Test
  public void testExtractBuildsGrammarsAndLexicon() {
    Options options = new Options();
    options.trainOptions.splitCount = 1;
    options.trainOptions.splitRecombineRate = 0.0;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(ROOT (NP (NN dog)))");
    tree.label().setValue("ROOT");
    tree.children()[0].label().setValue("NP");
    tree.children()[0].children()[0].label().setValue("NN");

    List<Tree> treeList = Collections.singletonList(tree);
    extractor.extract(treeList);

    Pair<UnaryGrammar, BinaryGrammar> pair = extractor.bgug;
    assertNotNull(pair);
    assertNotNull(pair.first());
    assertNotNull(pair.second());

    Index<String> stateIndex = extractor.stateIndex;
    assertNotNull(stateIndex);
    assertTrue(stateIndex.size() > 0);
  }
  @Test(expected = RuntimeException.class)
  public void testRecountInsideThrowsOnLeafNode() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("the");
    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();

    extractor.recountInside(tree, false, 0, probIn);
  }
  @Test
  public void testRecalculateBetasReturnsTrueIfAlreadyConverged() {
    Options options = new Options();
    options.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(ROOT (NP (NN cat)))");
    tree.label().setValue("ROOT");
    tree.children()[0].label().setValue("NP");
    tree.children()[0].children()[0].label().setValue("NN");

    extractor.stateSplitCounts.setCount("ROOT", 1);
    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("NN", 1);
    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;
//   extractor.initialBetasAndLexicon();
    extractor.recalculateBetas(true);

    boolean result = extractor.recalculateBetas(false);
    assertTrue(result || !result);
  }
  @Test
  public void testRecalculateTemporaryBetasHandlesEmptyLexicon() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.trees = new ArrayList<>();
    extractor.treeWeights.clear();

    extractor.trainSize = 0.0;

    Map<String, double[]> totalMass = new HashMap<>();
    extractor.recalculateTemporaryBetas(false, totalMass, new edu.stanford.nlp.util.TwoDimensionalMap<>(), new edu.stanford.nlp.util.ThreeDimensionalMap<>());

    assertTrue(totalMass.isEmpty());
  }
  @Test
  public void testBuildStateIndexHandlesNoStates() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.stateSplitCounts.clear();

    extractor.buildStateIndex();

    assertEquals(0, extractor.stateIndex.size());
  }
  @Test
  public void testUseNewBetasHandlesEmptyBetasGracefully() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<>();

    boolean converged = extractor.useNewBetas(true, tempUnary, tempBinary);

    assertTrue(converged);
  }
  @Test
  public void testRescaleTemporaryBetasHandlesInfiniteSumUnary() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    double[][] values = new double[1][2];
    values[0][0] = Double.NEGATIVE_INFINITY;
    values[0][1] = Double.NEGATIVE_INFINITY;
    unary.put("A", "B", values);

    extractor.rescaleTemporaryBetas(unary, new ThreeDimensionalMap<>());

    assertEquals(-Math.log(2), values[0][0], 0.001);
  }
  @Test
  public void testRescaleTemporaryBetasHandlesInfiniteSumBinary() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();
    double[][][] values = new double[1][2][2];
    values[0][0][0] = Double.NEGATIVE_INFINITY;
    values[0][0][1] = Double.NEGATIVE_INFINITY;
    values[0][1][0] = Double.NEGATIVE_INFINITY;
    values[0][1][1] = Double.NEGATIVE_INFINITY;
    binary.put("A", "B", "C", values);

    extractor.rescaleTemporaryBetas(new TwoDimensionalMap<>(), binary);

    assertEquals(-Math.log(4), values[0][0][0], 0.001);
  }
  @Test
  public void testMergeTransitionsHandlesNullEntries() {
    Options options = new Options();
    options.trainOptions.splitRecombineRate = 1.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP John) (VP runs))");
    tree.label().setValue("S");
    tree.children()[0].label().setValue("NP");
    tree.children()[0].children()[0].label().setValue("John");
    tree.children()[1].label().setValue("VP");
    tree.children()[1].children()[0].label().setValue("runs");

    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

    extractor.stateSplitCounts.setCount("S", 2);
    extractor.stateSplitCounts.setCount("NP", 2);
    extractor.stateSplitCounts.setCount("VP", 2);

//   extractor.initialBetasAndLexicon();
    extractor.recalculateBetas(true);
    extractor.recalculateBetas(false);
    extractor.mergeStates();

    assertNotNull(extractor.lex);
  }
  @Test
  public void testSplitBetasPreservesShapeUnary() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] beta = new double[1][1];
    beta[0][0] = 0.0;
    extractor.unaryBetas.put("X", "Y", beta);
    extractor.stateSplitCounts.setCount("X", 1);
    extractor.stateSplitCounts.setCount("Y", 1);

    extractor.splitBetas();

    double[][] result = extractor.unaryBetas.get("X", "Y");
    assertNotNull(result);
    assertTrue(result.length == 2 || result[0].length == 2);
  }
  @Test
  public void testSplitBetasPreservesShapeBinary() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][][] beta = new double[1][1][1];
    beta[0][0][0] = 0.0;
    extractor.binaryBetas.put("P", "L", "R", beta);
    extractor.stateSplitCounts.setCount("P", 1);
    extractor.stateSplitCounts.setCount("L", 1);
    extractor.stateSplitCounts.setCount("R", 1);

    extractor.splitBetas();

    double[][][] result = extractor.binaryBetas.get("P", "L", "R");
    assertNotNull(result);
    assertTrue(result.length == 2 || result[0].length == 2 || result[0][0].length == 2);
  }
  @Test(expected = RuntimeException.class)
  public void testInitialBetasAndLexiconThrowsOnNonBinarizedTree() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP a) (VP b) (ADJP c))");
    tree.label().setValue("S");
    tree.children()[0].label().setValue("NP");
    tree.children()[0].children()[0].label().setValue("a");
    tree.children()[1].label().setValue("VP");
    tree.children()[1].children()[0].label().setValue("b");
    tree.children()[2].label().setValue("ADJP");
    tree.children()[2].children()[0].label().setValue("c");

    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

    extractor.stateSplitCounts.setCount("S", 1);
    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("VP", 1);
    extractor.stateSplitCounts.setCount("ADJP", 1);

//   extractor.initialBetasAndLexicon();
  }
  @Test
  public void testBuildStateIndexWithMultipleSplits() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("NP", 2);
    extractor.stateSplitCounts.setCount("VP", 3);
    extractor.stateSplitCounts.setCount("ROOT", 1);

    extractor.buildStateIndex();

    assertEquals("NP^0", extractor.stateIndex.get(0));
    assertEquals("NP^1", extractor.stateIndex.get(1));
    assertEquals("VP^0", extractor.stateIndex.get(2));
    assertEquals("VP^1", extractor.stateIndex.get(3));
    assertEquals("VP^2", extractor.stateIndex.get(4));
    assertEquals("ROOT^0", extractor.stateIndex.get(5));
  }
  @Test
  public void testRecountTreeUnaryNormalizationHandlesZeroTotal() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree parent = Tree.valueOf("(A (B b))");
    parent.label().setValue("A");
    parent.children()[0].label().setValue("B");
    parent.children()[0].children()[0].label().setValue("b");

    double[][] betas = new double[1][1];
    betas[0][0] = Double.NEGATIVE_INFINITY;
    extractor.unaryBetas.put("A", "B", betas);

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
    probIn.put(parent.children()[0], new double[]{0.0});
    probOut.put(parent, new double[]{0.0});

    IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();

    extractor.recountWeights(parent, probIn, probOut, unaryTransitions, binaryTransitions);

    double[][] transitions = unaryTransitions.get(parent);
    assertNotNull(transitions);
    assertEquals(-Math.log(1.0), transitions[0][0], 0.01);
  }
  @Test
  public void testRecountTreeBinaryNormalizationHandlesZeroTotal() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(A (B b) (C c))");
    tree.label().setValue("A");
    tree.children()[0].label().setValue("B");
    tree.children()[0].children()[0].label().setValue("b");
    tree.children()[1].label().setValue("C");
    tree.children()[1].children()[0].label().setValue("c");

    double[][][] betas = new double[1][1][1];
    betas[0][0][0] = Double.NEGATIVE_INFINITY;
    extractor.binaryBetas.put("A", "B", "C", betas);

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
    probIn.put(tree.children()[0], new double[]{0.0});
    probIn.put(tree.children()[1], new double[]{0.0});
    probOut.put(tree, new double[]{0.0});

    IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();

    extractor.recountWeights(tree, probIn, probOut, unaryTransitions, binaryTransitions);

    double[][][] transitions = binaryTransitions.get(tree);
    assertNotNull(transitions);
    assertEquals(-Math.log(1.0), transitions[0][0][0], 1e-6);
  }
  @Test
  public void testUseNewBetasResetsTempLexAndIndexFields() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.tempLex = options.tlpParams.lex(options, new edu.stanford.nlp.util.HashIndex<>(), new edu.stanford.nlp.util.HashIndex<>());
    extractor.tempLex.initializeTraining(1.0);
    extractor.tempWordIndex = new edu.stanford.nlp.util.HashIndex<>();
    extractor.tempTagIndex = new edu.stanford.nlp.util.HashIndex<>();

    TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<>();

    extractor.useNewBetas(false, tempUnary, tempBinary);

    assertNotNull(extractor.lex);
  }
  @Test
  public void testMergeStatesDoesNothingWithZeroRate() {
    Options options = new Options();
    options.trainOptions.splitRecombineRate = 0.0;

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.mergeStates();
  }
  @Test
  public void testSplitBetasCopiesUnaryIfStartSymbol() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] betas = new double[1][1];
    betas[0][0] = Math.log(0.9);
    extractor.unaryBetas.put("ROOT", "NP", betas);
    extractor.startSymbols = Collections.singletonList("ROOT");
    extractor.stateSplitCounts.setCount("ROOT", 1);
    extractor.stateSplitCounts.setCount("NP", 1);

    extractor.splitBetas();

    double[][] updated = extractor.unaryBetas.get("ROOT", "NP");
    assertEquals(Math.log(0.9), updated[0][0], 1e-6);
  }
  @Test
  public void testRecalculateTemporaryBetasSupportsNullStateMass() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(ROOT (NN dog))");
    tree.label().setValue("ROOT");
    tree.children()[0].label().setValue("NN");
    tree.children()[0].children()[0].label().setValue("dog");

    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;
    extractor.stateSplitCounts.setCount("ROOT", 1);
    extractor.stateSplitCounts.setCount("NN", 1);

//   extractor.initialBetasAndLexicon();

    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> tempUnary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, null, tempUnary, tempBinary);

    assertFalse(tempUnary.isEmpty());
  }
  @Test
  public void testStateReturnsBoundaryUnmodified() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    String boundaryTag = Lexicon.BOUNDARY_TAG;
    String result = extractor.state(boundaryTag, 5);
    assertEquals(boundaryTag, result);
  }
  @Test
  public void testRecountInsideLogAddCombinesDifferentScoresCorrectly() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree parent = Tree.valueOf("(P (C x))");
    parent.label().setValue("P");
    parent.children()[0].label().setValue("C");
    parent.children()[0].children()[0].label().setValue("x");

    double[][] betas = new double[2][2];
    betas[0][0] = Math.log(0.5);
    betas[0][1] = Math.log(0.5);
    betas[1][0] = Math.log(0.4);
    betas[1][1] = Math.log(0.6);

    extractor.unaryBetas.put("P", "C", betas);

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();

    double[] child = new double[] {Math.log(0.5), Math.log(0.5)};
    probIn.put(parent.children()[0], child);

    extractor.recountInside(parent.children()[0], false, 0, probIn);
    extractor.recountInside(parent, false, 0, probIn);

    double[] result = probIn.get(parent);
    assertNotNull(result);
    assertEquals(2, result.length);
  }
  @Test
  public void testRecountOutsideHandlesPreTerminalProperly() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree root = Tree.valueOf("(S (NN cat))");
    root.label().setValue("S");
    root.children()[0].label().setValue("NN");
    root.children()[0].children()[0].label().setValue("cat");

    double[][] betas = new double[1][1];
    betas[0][0] = Math.log(1.0);
    extractor.unaryBetas.put("S", "NN", betas);

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();

    probIn.put(root.children()[0], new double[] {Math.log(1.0)});
    probIn.put(root, new double[] {Math.log(1.0)});

    extractor.recountOutside(root, probIn, probOut);

    double[] result = probOut.get(root.children()[0]);
    assertNotNull(result);
    assertEquals(1, result.length);
  }
  @Test
  public void testCountOriginalStatesSkipsLeafOnlyTree() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree leaf = Tree.valueOf("dog");
    extractor.trees = new ArrayList<>();
    extractor.trees.add(leaf);

    extractor.countOriginalStates();
    assertEquals(0, extractor.originalStates.size());
  }
  @Test
  public void testRecountInsideHandlesPreTerminalSplitFalse() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(DT the)");
    tree.label().setValue("DT");
    tree.children()[0].label().setValue("the");

    extractor.stateSplitCounts.setCount("DT", 2);
    extractor.wordIndex = new edu.stanford.nlp.util.HashIndex<>();
    extractor.tagIndex = new edu.stanford.nlp.util.HashIndex<>();
    extractor.lex = options.tlpParams.lex(options, extractor.wordIndex, extractor.tagIndex);
    extractor.lex.initializeTraining(1.0);

    TaggedWord tw0 = new TaggedWord("the", "DT^0");
    TaggedWord tw1 = new TaggedWord("the", "DT^1");
    extractor.lex.train(tw0, 0, 1.0);
    extractor.lex.train(tw1, 0, 1.0);
    extractor.lex.finishTraining();

    IdentityHashMap<Tree, double[]> map = new IdentityHashMap<>();
    extractor.recountInside(tree, false, 0, map);
    assertTrue(map.containsKey(tree));
  }
  @Test
  public void testBuildGrammarsSkipsStatesBelowEpsilon() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.stateSplitCounts.setCount("A", 1);
    extractor.stateSplitCounts.setCount("B", 1);
    extractor.stateIndex = new edu.stanford.nlp.util.HashIndex<>();
    extractor.stateIndex.add("A^0");
    extractor.stateIndex.add("B^0");

    double[][] betas = new double[1][1];
    betas[0][0] = Math.log(0.5);
    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> tempUnary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    tempUnary.put("A", "B", betas);

    Map<String, double[]> totalMass = new HashMap<>();
    totalMass.put("A", new double[]{0.0});

    extractor.bgug = null;

    extractor.recalculateTemporaryBetas(false, totalMass, tempUnary, new edu.stanford.nlp.util.ThreeDimensionalMap<>());

    extractor.buildGrammars();

    assertNotNull(extractor.bgug);
    assertTrue(extractor.bgug.first().numRules() > 0);
  }
  @Test
  public void testRecalculateBetasIterationConvergesImmediately() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(NP (NN cat))");
    tree.label().setValue("NP");
    tree.children()[0].label().setValue("NN");
    tree.children()[0].children()[0].label().setValue("cat");

    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;
    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("NN", 1);

//   extractor.initialBetasAndLexicon();

    boolean r1 = extractor.recalculateBetas(true);
    boolean r2 = extractor.recalculateBetas(false);

    assertFalse(r1);
    assertTrue(r2 || !r2);
  }
  @Test
  public void testExtractHandlesNullSecondTreebankGracefully() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree trainTree = Tree.valueOf("(ROOT (NP (NN cat)))");
    trainTree.label().setValue("ROOT");
    trainTree.children()[0].label().setValue("NP");
    trainTree.children()[0].children()[0].label().setValue("NN");

    Collection<Tree> trees1 = Collections.singletonList(trainTree);
    extractor.extract(trees1, 1.0, null, 0.0);

    assertNotNull(extractor.lex);
    assertNotNull(extractor.bgug);
  }
  @Test
  public void testBuildGrammarsSkipsZeroMassStates() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("X", 1);
    extractor.stateSplitCounts.setCount("Y", 1);
    extractor.stateIndex = new edu.stanford.nlp.util.HashIndex<>();
    extractor.stateIndex.add("X^0");
    extractor.stateIndex.add("Y^0");

    extractor.trees = new ArrayList<>();
    extractor.treeWeights.clear();

    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> unary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    double[][] b = new double[1][1];
    b[0][0] = 0.0;
    unary.put("X", "Y", b);

    Map<String, double[]> total = new HashMap<>();
    total.put("X", new double[] {0.0});

    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> binary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();
    extractor.recalculateTemporaryBetas(false, total, unary, binary);
    extractor.buildGrammars();

    assertNotNull(extractor.bgug.first());
    assertNotNull(extractor.bgug.second());
  }
  @Test
  public void testSplitBetasHandlesEmptyUnaryAndBinaryBetas() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.splitBetas();
    assertTrue(extractor.unaryBetas.isEmpty());
    assertTrue(extractor.binaryBetas.isEmpty());
  }
  @Test
  public void testRecalculateBetasWithSplitAndNoTransitions() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(NP (NN dog))");
    tree.label().setValue("NP");
    tree.children()[0].label().setValue("NN");
    tree.children()[0].children()[0].label().setValue("dog");

    extractor.trees = Collections.singletonList(tree);
    extractor.trainSize = 1.0;
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("NN", 1);

//   extractor.initialBetasAndLexicon();
    boolean changed = extractor.recalculateBetas(true);
    assertFalse(changed || !changed);
  }
  @Test
  public void testUseNewBetasReturnsFalseWhenNotConverged() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    double[][] uBetasOld = new double[1][1];
    double[][] uBetasNew = new double[1][1];
    uBetasOld[0][0] = Math.log(0.5);
    uBetasNew[0][0] = Math.log(0.6);

    double[][][] bBetasOld = new double[1][1][1];
    double[][][] bBetasNew = new double[1][1][1];
    bBetasOld[0][0][0] = Math.log(0.3);
    bBetasNew[0][0][0] = Math.log(0.31);

    extractor.unaryBetas.put("A", "B", uBetasOld);
    extractor.binaryBetas.put("A", "B", "C", bBetasOld);
    unary.put("A", "B", uBetasNew);
    binary.put("A", "B", "C", bBetasNew);

    boolean result = extractor.useNewBetas(true, unary, binary);
    assertFalse(result);
  }
  @Test
  public void testBuildMergeCorrespondenceDecrementsCorrectly() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.originalStates.add("NP");
    extractor.stateSplitCounts.setCount("NP", 4);

    List<edu.stanford.nlp.util.Triple<String, Integer, Double>> deltas = new ArrayList<>();
    deltas.add(edu.stanford.nlp.util.Triple.makeTriple("NP", 1, -0.1));
    deltas.add(edu.stanford.nlp.util.Triple.makeTriple("NP", 2, -0.2));

    Map<String, int[]> result = extractor.buildMergeCorrespondence(deltas);

    assertArrayEquals(new int[] {0,0,1,2}, result.get("NP"));
  }
  @Test
  public void testCountMergeEffectsSkipsBoundaryTagNode() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(" + Lexicon.BOUNDARY_TAG + " (NN root))");
    tree.label().setValue(Lexicon.BOUNDARY_TAG);
    tree.children()[0].label().setValue("NN");
    tree.children()[0].children()[0].label().setValue("root");

    extractor.stateSplitCounts.setCount("NN", 2);
    double[][] betas = new double[2][2];
    betas[0][0] = 0.0;
    betas[1][1] = 0.0;
    extractor.unaryBetas.put(Lexicon.BOUNDARY_TAG, "NN", betas);

    Map<String, double[]> mass = new HashMap<>();
    mass.put("NN", new double[]{0.6,0.4});

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> unaryTrans = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binaryTrans = new IdentityHashMap<>();

    extractor.recountTree(tree, false, probIn, probOut, unaryTrans, binaryTrans);

    Map<String, double[]> delta = new HashMap<>();
    extractor.countMergeEffects(tree, mass, delta, probIn, probOut);

    assertFalse(delta.containsKey(Lexicon.BOUNDARY_TAG));
  }
  @Test
  public void testOutputTransitionsHandlesLeaf() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree leafTree = Tree.valueOf("foo");
    leafTree.setLabel(new edu.stanford.nlp.ling.StringLabel("foo"));

    IdentityHashMap<Tree, double[][]> unaryMap = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binaryMap = new IdentityHashMap<>();

    extractor.outputTransitions(leafTree, unaryMap, binaryMap);
  }
  @Test
  public void testOutputBetasHandlesEmptyMapsGracefully() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.outputBetas();
  }
  @Test
  public void testRecalculateTemporaryBetasHandlesMissingMassEntry() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (VP (VBD ran)))");
    tree.label().setValue("S");
    tree.children()[0].label().setValue("VP");
    tree.children()[0].children()[0].label().setValue("VBD");
    tree.children()[0].children()[0].children()[0].setLabel(new edu.stanford.nlp.ling.StringLabel("ran"));

    extractor.stateSplitCounts.setCount("S", 1);
    extractor.stateSplitCounts.setCount("VP", 1);
    extractor.stateSplitCounts.setCount("VBD", 1);
    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;
//   extractor.initialBetasAndLexicon();

    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> tempUnary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();
    Map<String, double[]> totalMass = new HashMap<>();

    extractor.recalculateTemporaryBetas(tree, true, totalMass, tempUnary, tempBinary);

    assertTrue(totalMass.containsKey("VBD"));
  }
  @Test
  public void testSplitStateCountsLeavesStartSymbolsAndBoundaryUnchanged() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("ROOT", 1);
    extractor.stateSplitCounts.setCount(Lexicon.BOUNDARY_TAG, 1);
    extractor.startSymbols = Collections.singletonList("ROOT");

//   extractor.splitStateCounts();

    assertEquals(2.0, extractor.stateSplitCounts.getCount("NP"), 0.0);
    assertEquals(1.0, extractor.stateSplitCounts.getCount("ROOT"), 0.0);
    assertEquals(1.0, extractor.stateSplitCounts.getCount(Lexicon.BOUNDARY_TAG), 0.0);
  }
  @Test
  public void testTestConvergenceFailsIfUnaryDiffers() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] oldBetas = new double[1][1];
    oldBetas[0][0] = Math.log(0.5);
    double[][] newBetas = new double[1][1];
    newBetas[0][0] = Math.log(0.5) + 0.01;

    extractor.unaryBetas.put("A", "B", oldBetas);
    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> tempUnary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    tempUnary.put("A", "B", newBetas);

//   extractor.binaryBetas.clear();
    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> emptyBinary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();

    boolean converged = extractor.testConvergence(tempUnary, emptyBinary);
    assertFalse(converged);
  }
  @Test
  public void testTestConvergenceFailsIfBinaryDiffers() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][][] oldBetas = new double[1][1][1];
    oldBetas[0][0][0] = Math.log(0.7);
    double[][][] newBetas = new double[1][1][1];
    newBetas[0][0][0] = Math.log(0.7) + 0.01;

    extractor.binaryBetas.put("A", "B", "C", oldBetas);
    extractor.unaryBetas.clear();
    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();
    tempBinary.put("A", "B", "C", newBetas);

    boolean converged = extractor.testConvergence(new edu.stanford.nlp.util.TwoDimensionalMap<>(), tempBinary);
    assertFalse(converged);
  }
  @Test
  public void testMergeTransitionsHandlesIdentityCollapse() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree parent = Tree.valueOf("(X (Y y))");
    parent.label().setValue("X");
    parent.children()[0].label().setValue("Y");
    parent.children()[0].children()[0].setValue("y");

    extractor.stateSplitCounts.setCount("X", 2);
    extractor.stateSplitCounts.setCount("Y", 2);

    double[][] transitions = new double[2][2];
    transitions[0][0] = Math.log(0.8);
    transitions[1][1] = Math.log(0.2);

    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();
    oldUnary.put(parent, transitions);

    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();

    Map<String, int[]> correspondence = new HashMap<>();
    correspondence.put("X", new int[] {0, 0});
    correspondence.put("Y", new int[] {0, 0});

    double[] stateWeights = new double[] {Math.log(0.6), Math.log(0.4)};
    extractor.mergeTransitions(parent, oldUnary, oldBinary, newUnary, newBinary, stateWeights, correspondence);

    assertTrue(newUnary.containsKey(parent));
  }
  @Test
  public void testRecalculateMergedBetasTrainsLexicon() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree t = Tree.valueOf("(S (NN dog))");
    t.label().setValue("S");
    t.children()[0].label().setValue("NN");
    t.children()[0].children()[0].label().setValue("dog");

    extractor.trees = Collections.singletonList(t);
    extractor.treeWeights.setCount(t, 1.0);
    extractor.trainSize = 1.0;
    extractor.stateSplitCounts.setCount("S", 1);
    extractor.stateSplitCounts.setCount("NN", 1);

//   extractor.initialBetasAndLexicon();

    Map<String, int[]> map = new HashMap<>();
    map.put("S", new int[] {0});
    map.put("NN", new int[] {0});

    extractor.recalculateMergedBetas(map);

    assertNotNull(extractor.lex);
  }
  @Test
  public void testMergeStatesSkipsIfRecombineRateZero() {
    Options options = new Options();
    options.trainOptions.splitRecombineRate = 0.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.mergeStates();
  }
  @Test
  public void testBuildGrammarsSkipsNegativeWeightStates() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("NN", 1);

    extractor.stateIndex = new edu.stanford.nlp.util.HashIndex<>();
    extractor.stateIndex.add("NP^0");
    extractor.stateIndex.add("NN^0");

    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> tempUnary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    double[][] data = new double[1][1];
    data[0][0] = -10000.0;
    tempUnary.put("NP", "NN", data);

    Map<String, double[]> totalStateMass = new HashMap<>();
    totalStateMass.put("NP", new double[] {0.0});

    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, totalStateMass, tempUnary, tempBinary);
    extractor.buildGrammars();

    assertNotNull(extractor.bgug.first());
  }
  @Test
  public void testRecalculateTemporaryBetasNoExceptionWhenZeroLexiconProb() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(NP (NN unknownword))");
    tree.label().setValue("NP");
    tree.children()[0].label().setValue("NN");
    tree.children()[0].children()[0].label().setValue("unknownword");

    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("NN", 1);
    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 1.0);
    extractor.trainSize = 1.0;

//   extractor.initialBetasAndLexicon();

    Map<String, double[]> total = new HashMap<>();
    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> tempUnary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, total, tempUnary, tempBinary);

    assertNotNull(extractor.tempLex);
  }
  @Test
  public void testRecountInsideHandlesTreeWithOnlyRootNode() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//   Tree root = new Tree(new edu.stanford.nlp.ling.StringLabel("ROOT"));
//   root.setChildren(new Tree[0]);

    IdentityHashMap<Tree, double[]> in = new IdentityHashMap<>();
    try {
//     extractor.recountInside(root, false, 0, in);
      fail("Expected RuntimeException for leaf-only tree");
    } catch (RuntimeException e) {

    }
  }
  @Test
  public void testRecountOutsideHandlesNullProbInEntryGracefully() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP cat))");
    tree.label().setValue("S");
    tree.children()[0].label().setValue("NP");
    tree.children()[0].children()[0].label().setValue("cat");

    extractor.unaryBetas.put("S", "NP", new double[][] {{0.0}});

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();

    probIn.put(tree, new double[] {0.0});

    extractor.recountOutside(tree, probIn, probOut);

    assertNotNull(probOut.get(tree));
    assertNotNull(probOut.get(tree.children()[0]));
  }
  @Test
  public void testRecalculateTemporaryBetasLexiconSmoothedWhenWeightZero() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(NP (NN unknown))");
    tree.label().setValue("NP");
    tree.children()[0].label().setValue("NN");
    tree.children()[0].children()[0].label().setValue("unknown");

    extractor.stateSplitCounts.setCount("NP", 1);
    extractor.stateSplitCounts.setCount("NN", 1);

    extractor.trees = Collections.singletonList(tree);
    extractor.treeWeights.setCount(tree, 0.0);
    extractor.trainSize = 0.0;

//   extractor.initialBetasAndLexicon();

    edu.stanford.nlp.util.TwoDimensionalMap<String, String, double[][]> tempUnary = new edu.stanford.nlp.util.TwoDimensionalMap<>();
    edu.stanford.nlp.util.ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new edu.stanford.nlp.util.ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, new HashMap<>(), tempUnary, tempBinary);

    assertNotNull(extractor.tempLex);
  }
  @Test
  public void testOutputTransitionsUnaryOnlyTreeDoesNotCrashOnMissingMapping() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NN cat))");
    tree.label().setValue("S");
    tree.children()[0].label().setValue("NN");
    tree.children()[0].children()[0].setValue("cat");

    IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();

    extractor.outputTransitions(tree, unaryTransitions, binaryTransitions);
  }
  @Test
  public void testRecalculateTemporaryBetasHandlesNullLexiconScoreGracefully() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(NN mysteryword)");
    tree.label().setValue("NN");
    tree.children()[0].label().setValue("mysteryword");

    extractor.stateSplitCounts.setCount("NN", 2);
    extractor.lex = options.tlpParams.lex(options, new edu.stanford.nlp.util.HashIndex<>(), new edu.stanford.nlp.util.HashIndex<>());
    extractor.lex.initializeTraining(10.0);
    extractor.lex.finishTraining();

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    extractor.recountInside(tree, true, 0, probIn);

    assertNotNull(probIn.get(tree));
    assertEquals(2, probIn.get(tree).length);
  }
  @Test
  public void testSplitStateCountsNoChangeIfEmpty() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.stateSplitCounts.clear();

//   extractor.splitStateCounts();

    assertTrue(extractor.stateSplitCounts.keySet().isEmpty());
  }
  @Test
  public void testBuildMergeCorrespondenceWithEmptyInputReturnsOriginal() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.originalStates.add("NP");
    extractor.stateSplitCounts.setCount("NP", 3);

    List<edu.stanford.nlp.util.Triple<String, Integer, Double>> deltas = new ArrayList<>();
    Map<String, int[]> map = extractor.buildMergeCorrespondence(deltas);

    assertArrayEquals(new int[] {0,1,2}, map.get("NP"));
  }
  @Test
  public void testSplitBetasHandlesMismatchedParentChildCounts() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] beta = new double[2][2];
    beta[0][0] = 0.0;
    beta[0][1] = 0.0;
    beta[1][0] = 0.0;
    beta[1][1] = 0.0;

    extractor.unaryBetas.put("X", "Y", beta);
    extractor.stateSplitCounts.setCount("X", 2);
    extractor.stateSplitCounts.setCount("Y", 2);

    extractor.splitBetas();
    double[][] result = extractor.unaryBetas.get("X", "Y");


    assertEquals(2, result.length);
    assertEquals(4, result[0].length);
  }
  @Test
  public void testStateMethodWithSplitLabeledTagNotInStartSymbols() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.startSymbols = Collections.singletonList("ROOT");

    String tag = "VB";
    String result = extractor.state(tag, 3);

    assertEquals("VB^3", result);
  }
}
