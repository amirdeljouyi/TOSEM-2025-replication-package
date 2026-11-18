package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ThreeDimensionalMap;
import edu.stanford.nlp.util.TwoDimensionalMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SplittingGrammarExtractorPromptTest {

  @Test
  public void stateLeavesStartSymbolUnsplitAndAnnotatesOrdinaryLabels() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    assertEquals("ROOT", extractor.state("ROOT", 7));
    assertEquals("NP^0", extractor.state("NP", 0));
    assertEquals("NP^12", extractor.state("NP", 12));
    assertEquals("^3", extractor.state("", 3));
  }

  @Test(expected = NullPointerException.class)
  public void stateRejectsNullLabel() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.state(null, 0);
  }

  @Test
  public void saveTreesAndCountOriginalStatesIgnoreLeavesAndDeduplicateLabels() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree first = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");
    Tree second = Tree.valueOf("(ROOT (S (NN dogs) (VB run)))");

    extractor.saveTrees(Arrays.asList(first, second), 2.0, null, 0.0);
    extractor.countOriginalStates();

    assertEquals(1, extractor.getStateSplitCount("ROOT"));
    assertEquals(1, extractor.getStateSplitCount("S"));
    assertEquals(1, extractor.getStateSplitCount("NN"));
    assertEquals(1, extractor.getStateSplitCount("VB"));
    assertEquals(0, extractor.getStateSplitCount("cats"));
    assertEquals(1, extractor.getStateSplitCount(first));
  }

  @Test
  public void saveTreesSkipsSecondCollectionWhenItsWeightIsNegative() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree retained = Tree.valueOf("(ROOT (S (NN cat) (VB sleeps)))");
    Tree skipped = Tree.valueOf("(OTHER (JJ very) (NN bad))");

    extractor.saveTrees(Collections.singletonList(retained), 1.0,
        Collections.singletonList(skipped), -1.0);
    extractor.countOriginalStates();

    assertEquals(1, extractor.getStateSplitCount("ROOT"));
    assertEquals(0, extractor.getStateSplitCount("OTHER"));
    assertEquals(0, extractor.getStateSplitCount("JJ"));
  }

  @Test
  public void extractWithoutSplittingBuildsUnaryAndBinaryModel() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");

    extractor.extract(Collections.singletonList(tree));
    IdentityHashMap<Tree, double[]> inside = new IdentityHashMap<>();
    int endPosition = extractor.recountInside(tree, false, 0, inside);

    assertEquals(2, endPosition);
    assertEquals(1, inside.get(tree).length);
    assertTrue(Double.isFinite(inside.get(tree)[0]));
    assertEquals(1, extractor.getStateSplitCount("S"));
  }

  @Test
  public void extractWithOneSplitDoublesNonRootStates() {
    Options options = new Options();
    options.setOptions("-splitCount", "1", "-splitRecombineRate", "0.0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");

    extractor.extract(Collections.singletonList(tree));
    IdentityHashMap<Tree, double[]> inside = new IdentityHashMap<>();
    int endPosition = extractor.recountInside(tree, false, 0, inside);

    assertEquals(1, extractor.getStateSplitCount("ROOT"));
    assertEquals(2, extractor.getStateSplitCount("S"));
    assertEquals(2, extractor.getStateSplitCount("NN"));
    assertEquals(2, extractor.getStateSplitCount("VB"));
    assertEquals(2, endPosition);
    assertEquals(1, inside.get(tree).length);
  }

  @Test
  public void recountTreePopulatesInsideOutsideAndBothTransitionKinds() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");
    extractor.extract(Collections.singletonList(tree));
    IdentityHashMap<Tree, double[]> inside = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> outside = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> unary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binary = new IdentityHashMap<>();

    extractor.recountTree(tree, false, inside, outside, unary, binary);
    Tree sentence = tree.getChild(0);

    assertNotNull(inside.get(sentence.getChild(0)));
    assertNotNull(outside.get(sentence.getChild(1)));
    assertEquals(0.0, outside.get(tree)[0], 0.0);
    assertEquals(0.0, unary.get(tree)[0][0], 1.0e-12);
    assertEquals(0.0, binary.get(sentence)[0][0][0], 1.0e-12);
  }

  @Test
  public void recalculateTemporaryBetasAggregatesTreeWeightsAndStateMass() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree first = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");
    Tree second = Tree.valueOf("(ROOT (S (NN dogs) (VB run)))");
    extractor.extract(Collections.singletonList(first), 2.0,
        Collections.singletonList(second), 3.0);
    Map<String, double[]> mass = new HashMap<>();
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary =
        new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, mass, unary, binary);

    assertEquals(5.0, mass.get("ROOT")[0], 1.0e-12);
    assertEquals(5.0, mass.get("NN")[0], 1.0e-12);
    assertEquals(Math.log(5.0), unary.get("ROOT", "S")[0][0], 1.0e-12);
    assertEquals(Math.log(5.0), binary.get("S", "NN", "VB")[0][0][0], 1.0e-12);
  }

  @Test
  public void recalculateBetasConvergesForStableSingleStateGrammar() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");
    extractor.extract(Collections.singletonList(tree));

    assertTrue(extractor.recalculateBetas(false));
    assertTrue(extractor.recalculateBetas(false));
  }

  @Test
  public void testConvergenceAcceptsEqualUnaryAndBinaryBetas() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.extract(Collections.singletonList(
        Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))")));
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("ROOT", "S", new double[][] {{0.0}});
    ThreeDimensionalMap<String, String, String, double[][][]> binary =
        new ThreeDimensionalMap<>();
    binary.put("S", "NN", "VB", new double[][][] {{{0.0}}});

    assertTrue(extractor.testConvergence(unary, binary));
  }

  @Test
  public void testConvergenceRejectsUnaryDifferenceAboveEpsilon() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.extract(Collections.singletonList(
        Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))")));
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("ROOT", "S", new double[][] {{0.0002}});
    ThreeDimensionalMap<String, String, String, double[][][]> binary =
        new ThreeDimensionalMap<>();
    binary.put("S", "NN", "VB", new double[][][] {{{0.0}}});

    assertFalse(extractor.testConvergence(unary, binary));
  }

  @Test
  public void testConvergenceRejectsBinaryDifferenceAboveEpsilon() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    extractor.extract(Collections.singletonList(
        Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))")));
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("ROOT", "S", new double[][] {{0.0}});
    ThreeDimensionalMap<String, String, String, double[][][]> binary =
        new ThreeDimensionalMap<>();
    binary.put("S", "NN", "VB", new double[][][] {{{-0.0002}}});

    assertFalse(extractor.testConvergence(unary, binary));
  }

  @Test
  public void rescaleTemporaryBetasNormalizesFiniteUnaryAndBinaryRows() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("A", "B", new double[][] {{Math.log(2.0), Math.log(6.0)}});
    ThreeDimensionalMap<String, String, String, double[][][]> binary =
        new ThreeDimensionalMap<>();
    binary.put("P", "L", "R", new double[][][] {
        {{Math.log(1.0), Math.log(2.0)}, {Math.log(3.0), Math.log(4.0)}}
    });

    extractor.rescaleTemporaryBetas(unary, binary);

    assertEquals(0.25, Math.exp(unary.get("A", "B")[0][0]), 1.0e-12);
    assertEquals(0.75, Math.exp(unary.get("A", "B")[0][1]), 1.0e-12);
    assertEquals(0.1, Math.exp(binary.get("P", "L", "R")[0][0][0]), 1.0e-12);
    assertEquals(0.4, Math.exp(binary.get("P", "L", "R")[0][1][1]), 1.0e-12);
  }

  @Test
  public void rescaleTemporaryBetasUsesUniformRowsWhenAllMassIsInfinite() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("A", "B", new double[][] {
        {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY}
    });
    ThreeDimensionalMap<String, String, String, double[][][]> binary =
        new ThreeDimensionalMap<>();
    binary.put("P", "L", "R", new double[][][] {
        {{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY}}
    });

    extractor.rescaleTemporaryBetas(unary, binary);

    assertEquals(0.5, Math.exp(unary.get("A", "B")[0][0]), 1.0e-12);
    assertEquals(0.5, Math.exp(unary.get("A", "B")[0][1]), 1.0e-12);
    assertEquals(0.5, Math.exp(binary.get("P", "L", "R")[0][0][0]), 1.0e-12);
    assertEquals(0.5, Math.exp(binary.get("P", "L", "R")[0][0][1]), 1.0e-12);
  }

  @Test
  public void mergeTransitionsCombinesUnaryParentStatesUsingStateWeights() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(P (T word))");
    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    oldUnary.put(tree, new double[][] {
        {Math.log(0.8), Math.log(0.2)},
        {Math.log(0.1), Math.log(0.9)}
    });
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();
    Map<String, int[]> correspondence = new HashMap<>();
    correspondence.put("P", new int[] {0, 0});
    correspondence.put("T", new int[] {0, 1});

    extractor.mergeTransitions(tree, oldUnary, oldBinary, newUnary, newBinary,
        new double[] {Math.log(0.25), Math.log(0.75)}, correspondence);

    assertEquals(1, newUnary.get(tree).length);
    assertEquals(2, newUnary.get(tree)[0].length);
    assertEquals(0.275, Math.exp(newUnary.get(tree)[0][0]), 1.0e-12);
    assertEquals(0.725, Math.exp(newUnary.get(tree)[0][1]), 1.0e-12);
    assertTrue(newBinary.isEmpty());
  }

  @Test
  public void mergeTransitionsCombinesBinaryParentAndLeftStates() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(P (L left) (R right))");
    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();
    oldBinary.put(tree, new double[][][] {
        {{Math.log(0.1), Math.log(0.2)}, {Math.log(0.3), Math.log(0.4)}},
        {{Math.log(0.4), Math.log(0.1)}, {Math.log(0.2), Math.log(0.3)}}
    });
    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();
    Map<String, int[]> correspondence = new HashMap<>();
    correspondence.put("P", new int[] {0, 0});
    correspondence.put("L", new int[] {0, 0});
    correspondence.put("R", new int[] {0, 1});

    extractor.mergeTransitions(tree, oldUnary, oldBinary, newUnary, newBinary,
        new double[] {Math.log(0.4), Math.log(0.6)}, correspondence);

    assertEquals(1, newBinary.get(tree).length);
    assertEquals(1, newBinary.get(tree)[0].length);
    assertEquals(2, newBinary.get(tree)[0][0].length);
    assertEquals(0.52, Math.exp(newBinary.get(tree)[0][0][0]), 1.0e-12);
    assertEquals(0.48, Math.exp(newBinary.get(tree)[0][0][1]), 1.0e-12);
    assertTrue(newUnary.isEmpty());
  }

  @Test
  public void mergeTransitionsStopsImmediatelyAtLeafAndPreterminal() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree preterminal = Tree.valueOf("(NN cat)");
    Tree leaf = preterminal.getChild(0);
    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();

    extractor.mergeTransitions(preterminal, oldUnary, oldBinary, newUnary, newBinary,
        new double[] {0.0}, Collections.emptyMap());
    extractor.mergeTransitions(leaf, oldUnary, oldBinary, newUnary, newBinary,
        new double[] {0.0}, Collections.emptyMap());

    assertTrue(newUnary.isEmpty());
    assertTrue(newBinary.isEmpty());
  }

  @Test(expected = RuntimeException.class)
  public void recountInsideRejectsBareLeaf() {
    Options options = new Options();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree leaf = Tree.valueOf("(NN cat)").getChild(0);

    extractor.recountInside(leaf, false, 0, new IdentityHashMap<>());
  }

  @Test(expected = RuntimeException.class)
  public void extractRejectsUnbinarizedNodeWithThreeChildren() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (S (NN one) (NN two) (NN three)))");

    extractor.extract(Collections.singletonList(tree));
  }

  @Test
  public void useNewBetasAcceptsRecalculatedModelAndKeepsItOperational() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");
    extractor.extract(Collections.singletonList(tree));
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary =
        new ThreeDimensionalMap<>();
    extractor.recalculateTemporaryBetas(false, null, unary, binary);

    boolean converged = extractor.useNewBetas(true, unary, binary);
    IdentityHashMap<Tree, double[]> inside = new IdentityHashMap<>();
    int endPosition = extractor.recountInside(tree, false, 0, inside);

    assertTrue(converged);
    assertEquals(2, endPosition);
    assertTrue(Double.isFinite(inside.get(tree)[0]));
  }

  @Test
  public void rebuildingIndexesAndGrammarsAfterExtractionIsIdempotent() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (S (NN cats) (VB sleep)))");
    extractor.extract(Collections.singletonList(tree));

    extractor.buildStateIndex();
    extractor.buildGrammars();
    extractor.mergeStates();
    extractor.buildStateIndex();
    extractor.buildGrammars();

    assertEquals(1, extractor.getStateSplitCount("ROOT"));
    assertEquals(1, extractor.getStateSplitCount("S"));
  }

  @Test
  public void diagnosticMethodsHandleLeafPreterminalAndTrainedBetas() {
    Options options = new Options();
    options.setOptions("-splitCount", "0");
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    Tree tree = Tree.valueOf("(ROOT (NN cat))");
    extractor.extract(Collections.singletonList(tree));

    extractor.outputTransitions(tree.getChild(0).getChild(0),
        new IdentityHashMap<>(), new IdentityHashMap<>());
    extractor.outputTransitions(tree.getChild(0), 2,
        new IdentityHashMap<>(), new IdentityHashMap<>());
    extractor.outputBetas();

    assertEquals(1, extractor.getStateSplitCount("NN"));
  }
}
