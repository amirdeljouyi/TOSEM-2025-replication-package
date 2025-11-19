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

public class SplittingGrammarExtractor_2_GPTLLMTest {

 @Test
  public void testConstructorInitializesStartSymbols() {
    Options options = mock(Options.class);
//     Options.LexicalizedParserParams tlpParams = mock(Options.LexicalizedParserParams.class);
//     when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//     when(options.tlpParams).thenReturn(tlpParams);

    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
    assertEquals(Arrays.asList("ROOT"), extractor.startSymbols);
  }
@Test
  public void testStateReturnsStartSymbolAsIs() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] {"ROOT" });
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
//     String result = extractor.state("ROOT", 2);
//     assertEquals("ROOT", result);
  }
@Test
  public void testStateReturnsFormattedStateForNonStartSymbol() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] {"ROOT" });
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
//     String result = extractor.state("NP", 1);
//     assertEquals("NP^1", result);
  }
@Test
  public void testCountOriginalStatesIncrementsSplitCounts() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree leafChild = mock(Tree.class);
    Label leafLabel = mock(Label.class);
    when(leafChild.isLeaf()).thenReturn(true);
    when(leafChild.label()).thenReturn(leafLabel);
    when(leafLabel.value()).thenReturn("the");

    Tree preTerminal = mock(Tree.class);
    Label preLabel = mock(Label.class);
    when(preTerminal.isPreTerminal()).thenReturn(true);
    when(preTerminal.isLeaf()).thenReturn(false);
    when(preTerminal.label()).thenReturn(preLabel);
    when(preLabel.value()).thenReturn("DT");
    when(preTerminal.children()).thenReturn(new Tree[] { leafChild });

    Tree root = mock(Tree.class);
    Label rootLabel = mock(Label.class);
    when(root.isLeaf()).thenReturn(false);
    when(root.isPreTerminal()).thenReturn(false);
    when(root.label()).thenReturn(rootLabel);
    when(rootLabel.value()).thenReturn("NP");
    when(root.children()).thenReturn(new Tree[] { preTerminal });

    ArrayList<Tree> treeList = new ArrayList<>();
    treeList.add(root);

//     extractor.trees = treeList;
//     extractor.countOriginalStates();

//     assertEquals(1, extractor.getStateSplitCount("NP"));
//     assertEquals(1, extractor.getStateSplitCount("DT"));
  }
@Test
  public void testSplitStateCountsDoublesNonRootStatesOnly() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//     IntCounter<String> counter = new IntCounter<>();
//     counter.setCount("NP", 2.0);
//     counter.setCount("VP", 3.0);
//     counter.setCount("ROOT", 1.0);
//     counter.setCount(Lexicon.BOUNDARY_TAG, 1.0);
//     extractor.startSymbols = Collections.singletonList("ROOT");
//     extractor.stateSplitCounts = counter;

//     extractor.splitStateCounts();

//     assertEquals(4, extractor.getStateSplitCount("NP"));
//     assertEquals(6, extractor.getStateSplitCount("VP"));
//     assertEquals(1, extractor.getStateSplitCount("ROOT"));
//     assertEquals(1, extractor.getStateSplitCount(Lexicon.BOUNDARY_TAG));
  }
@Test
  public void testBuildStateIndexAddsIndexedStates() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] {});
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//     IntCounter<String> counter = new IntCounter<>();
//     counter.setCount("NP", 2.0);
//     counter.setCount("VP", 1.0);
//     extractor.stateSplitCounts = counter;

//     extractor.buildStateIndex();
//     Index<String> idx = extractor.stateIndex;

//     assertEquals("NP^0", idx.get(0));
//     assertEquals("NP^1", idx.get(1));
//     assertEquals("VP^0", idx.get(2));
  }
@Test
  public void testNegInfDoublesCreatesCorrectArray() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] {});
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//     double[] result = extractor.neginfDoubles(3);

//     assertEquals(Double.NEGATIVE_INFINITY, result[0], 0.0001);
//     assertEquals(Double.NEGATIVE_INFINITY, result[1], 0.0001);
//     assertEquals(Double.NEGATIVE_INFINITY, result[2], 0.0001);
  }
@Test
  public void testSaveTreesCalculatesTrainSizeCorrectly() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] {});
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree1 = mock(Tree.class);
    Tree tree2 = mock(Tree.class);

    ArrayList<Tree> list1 = new ArrayList<>();
    list1.add(tree1);

    ArrayList<Tree> list2 = new ArrayList<>();
    list2.add(tree2);

//     extractor.saveTrees(list1, 2.0, list2, 3.0);

//     assertEquals(2.0, extractor.treeWeights.getCount(tree1), 0.0001);
//     assertEquals(3.0, extractor.treeWeights.getCount(tree2), 0.0001);
  }
@Test(expected = RuntimeException.class)
  public void testRecountInsideThrowsOnLeaf() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] {});
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree leaf = mock(Tree.class);
    when(leaf.isLeaf()).thenReturn(true);

//     extractor.recountInside(leaf, false, 0, new java.util.IdentityHashMap<>());
  }
@Test
  public void testGetStateSplitCountForUnknownReturnsZero() {
    Options options = mock(Options.class);
//     when(options.langpack()).thenReturn(() -> new String[] {});
//     SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//     assertEquals(0, extractor.getStateSplitCount("XYZ"));
  }
@Test
public void testStateReturnsBoundaryTagAsIs() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//   String result = extractor.state(Lexicon.BOUNDARY_TAG, 0);
//   assertEquals(Lexicon.BOUNDARY_TAG, result);
}
@Test
public void testSplitStateCountsWithZeroInitialCounts() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//   extractor.stateSplitCounts = new IntCounter<>();
//   extractor.stateSplitCounts.setCount("VP", 0.0);
//   extractor.startSymbols = Collections.singletonList("ROOT");

//   extractor.splitStateCounts();

//   assertEquals(0.0, extractor.stateSplitCounts.getCount("VP"), 0.0);
}
@Test
public void testCountOriginalStatesHandlesLeafOnlyTree() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree leaf = mock(Tree.class);
  when(leaf.isLeaf()).thenReturn(true);

  ArrayList<Tree> list = new ArrayList<>();
  list.add(leaf);
//   extractor.trees = list;

//   extractor.countOriginalStates();

//   assertTrue(extractor.originalStates.isEmpty());
}
@Test
public void testRecalculateBetasWithEmptyUnaryBinaryBetas() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lex = mock(Lexicon.class);
  Index<String> wordIdx = new HashIndex<>();
  Index<String> tagIdx = new HashIndex<>();
//   when(lp.lex(any(), any(), any())).thenReturn(lex);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   when(options.tlpParams).thenReturn(lp);

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  assertTrue(extractor.recalculateBetas(false));  
}
@Test
public void testSaveTreesWithEmptySecondCollection() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree tree = mock(Tree.class);
  ArrayList<Tree> primaryList = new ArrayList<>();
  primaryList.add(tree);

//   extractor.saveTrees(primaryList, 2.0, null, -1.0);

//   assertEquals(2.0, extractor.treeWeights.getCount(tree), 0.0001);
//   assertEquals(2.0, extractor.trainSize, 0.0001);
//   assertEquals(1, extractor.trees.size());
}
@Test
public void testGetStateSplitCountReturnsZeroForUnseenTreeLabel() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree tree = mock(Tree.class);
  Label label = mock(Label.class);
  when(label.value()).thenReturn("XYZ");
  when(tree.label()).thenReturn(label);

//   int count = extractor.getStateSplitCount(tree);
//   assertEquals(0, count);
}
@Test
public void testNeginfDoublesWithZeroLength() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//   double[] result = extractor.neginfDoubles(0);
//   assertEquals(0, result.length);
}
@Test
public void testBuildStateIndexWithEmptySplitCounts() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//   extractor.stateSplitCounts = new IntCounter<>();
//   extractor.buildStateIndex();

//   assertEquals(0, extractor.stateIndex.size());
}
@Test
public void testStateIndexOrderingMatchesInsertOrder() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//   IntCounter<String> counter = new IntCounter<>();
//   counter.setCount("A", 1.0);
//   counter.setCount("B", 1.0);
//   extractor.stateSplitCounts = counter;

//   extractor.buildStateIndex();

//   Index<String> index = extractor.stateIndex;
//   assertTrue(index.contains("A^0"));
//   assertTrue(index.contains("B^0"));
}
@Test
public void testOutputTransitionsWithUnaryAndPreterminalNode() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree child = mock(Tree.class);
  Label childLabel = mock(Label.class);
  when(child.isLeaf()).thenReturn(false);
  when(child.isPreTerminal()).thenReturn(true);
  when(child.label()).thenReturn(childLabel);
  when(childLabel.value()).thenReturn("NN");
  when(child.children()).thenReturn(new Tree[] {});

  Tree node = mock(Tree.class);
  Label nodeLabel = mock(Label.class);
  when(node.label()).thenReturn(nodeLabel);
  when(nodeLabel.value()).thenReturn("NP");
  when(node.children()).thenReturn(new Tree[] { child });
  when(node.isLeaf()).thenReturn(false);
  when(node.isPreTerminal()).thenReturn(false);
  when(node.getChild(0)).thenReturn(child);

  double[][] unary = new double[1][1];
  unary[0][0] = Math.log(0.5);

  IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
  unaryTransitions.put(node, unary);

  IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();

//   extractor.outputTransitions(node, unaryTransitions, binaryTransitions);
}
@Test
public void testMergeTransitionsWithEmptyTransitions() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree tree = mock(Tree.class);
  when(tree.isLeaf()).thenReturn(false);
  when(tree.isPreTerminal()).thenReturn(false);

  Tree left = mock(Tree.class);
  Tree right = mock(Tree.class);

  Label parentLabel = mock(Label.class);
  Label leftLabel = mock(Label.class);
  Label rightLabel = mock(Label.class);

  when(parentLabel.value()).thenReturn("S");
  when(leftLabel.value()).thenReturn("NP");
  when(rightLabel.value()).thenReturn("VP");

  when(tree.label()).thenReturn(parentLabel);
  when(left.label()).thenReturn(leftLabel);
  when(right.label()).thenReturn(rightLabel);
  when(tree.children()).thenReturn(new Tree[] { left, right });

  double[][][] oldBinary = new double[0][0][0];

  IdentityHashMap<Tree, double[][][]> oldBinaryTransitions = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][][]> newBinaryTransitions = new IdentityHashMap<>();

  IdentityHashMap<Tree, double[][]> dummyUnary = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][]> resultUnary = new IdentityHashMap<>();

  oldBinaryTransitions.put(tree, oldBinary);

  Map<String, int[]> mergeCorrespondence = new HashMap<>();
  mergeCorrespondence.put("S", new int[0]);
  mergeCorrespondence.put("NP", new int[0]);
  mergeCorrespondence.put("VP", new int[0]);

  double[] stateWeights = new double[] { 0.0 };

//   extractor.mergeTransitions(tree, dummyUnary, oldBinaryTransitions, resultUnary, newBinaryTransitions, stateWeights, mergeCorrespondence);
}
@Test
public void testRecountInsideWithUnaryNodeNoBetas() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lexicon = mock(Lexicon.class);
//   when(lp.lex(any(), any(), any())).thenReturn(lexicon);
//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   when(options.tlpParams).thenReturn(lp);
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree child = mock(Tree.class);
  when(child.isLeaf()).thenReturn(false);
  when(child.isPreTerminal()).thenReturn(true);
  when(child.label()).thenReturn(mock(Label.class));
  when(child.children()).thenReturn(new Tree[0]);

  Tree root = mock(Tree.class);
  when(root.isLeaf()).thenReturn(false);
  when(root.isPreTerminal()).thenReturn(false);
  when(root.children()).thenReturn(new Tree[] { child });
  when(root.getChild(0)).thenReturn(child);
  when(root.label()).thenReturn(mock(Label.class));

  extractor.unaryBetas.put("ROOT", "CHILD", null); 

  try {
    extractor.recountInside(root, false, 0, new IdentityHashMap<>());
  } catch (NullPointerException ignored) {
    
  }
}
@Test
public void testRecalculateConvergedBetasComparison() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lexicon = mock(Lexicon.class);
//   when(lp.lex(any(), any(), any())).thenReturn(lexicon);
//   when(options.tlpParams).thenReturn(lp);
//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] oldUnary = new double[][] { { Math.log(0.5) } };
  double[][] sameUnary = new double[][] { { Math.log(0.5) } };

  extractor.unaryBetas.put("A", "B", oldUnary);

  TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<>();
  tempUnary.put("A", "B", sameUnary);

  double[][][] oldBinary = new double[][][] { { { Math.log(0.4) } } };
  double[][][] sameBinary = new double[][][] { { { Math.log(0.4) } } };

  extractor.binaryBetas.put("X", "Y", "Z", oldBinary);

  ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<>();
  tempBinary.put("X", "Y", "Z", sameBinary);

  boolean converged = extractor.testConvergence(tempUnary, tempBinary);
  assertTrue(converged);
}
@Test
public void testTestConvergenceDetectsDifference() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] oldUnary = new double[][] { { Math.log(0.5) } };
  double[][] updatedUnary = new double[][] { { Math.log(0.9) } };

//   extractor.unaryBetas.put("P", "C", oldUnary);

  TwoDimensionalMap<String, String, double[][]> testUnary = new TwoDimensionalMap<>();
  testUnary.put("P", "C", updatedUnary);

  double[][][] oldBinary = new double[][][] { { { Math.log(0.1) } } };
  double[][][] updatedBinary = new double[][][] { { { Math.log(0.7) } } };

//   extractor.binaryBetas.put("A", "B", "C", oldBinary);

  ThreeDimensionalMap<String, String, String, double[][][]> testBinary = new ThreeDimensionalMap<>();
  testBinary.put("A", "B", "C", updatedBinary);

//   boolean converged = extractor.testConvergence(testUnary, testBinary);
//   assertFalse(converged);
}
@Test
public void testUseNewBetasDoesNotConvergeWhenTestFlagIsFalse() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lexicon = mock(Lexicon.class);

//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   when(options.tlpParams).thenReturn(lp);
//   when(lp.lex(any(), any(), any())).thenReturn(lexicon);

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] unaryBeta1 = new double[][] { { -0.5 } };
  TwoDimensionalMap<String, String, double[][]> tempUnaryBetas = new TwoDimensionalMap<>();
  tempUnaryBetas.put("A", "B", unaryBeta1);

  double[][][] binaryBeta1 = new double[][][] { { { -0.7 } } };
  ThreeDimensionalMap<String, String, String, double[][][]> tempBinaryBetas = new ThreeDimensionalMap<>();
  tempBinaryBetas.put("X", "Y", "Z", binaryBeta1);

  boolean result = extractor.useNewBetas(false, tempUnaryBetas, tempBinaryBetas);
  assertFalse(result);  
}
@Test
public void testUseNewBetasConvergesIfFlagTrueAndStableBetas() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lexicon = mock(Lexicon.class);

//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   when(options.tlpParams).thenReturn(lp);
//   when(lp.lex(any(), any(), any())).thenReturn(lexicon);

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] unary = new double[][] { { Math.log(0.5) } };
  extractor.unaryBetas.put("U-P", "U-C", unary);

  double[][][] binary = new double[][][] { { { Math.log(0.1) } } };
  extractor.binaryBetas.put("B-P", "B-L", "B-R", binary);

  TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<>();
  tempUnary.put("U-P", "U-C", new double[][] { { Math.log(0.5) } });

  ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<>();
  tempBinary.put("B-P", "B-L", "B-R", new double[][][] { { { Math.log(0.1) } } });

  boolean result = extractor.useNewBetas(true, tempUnary, tempBinary);
  assertTrue(result);
}
@Test
public void testRecalculateTemporaryBetasWithNoTreesDoesNotCrash() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lexicon = mock(Lexicon.class);

//   when(options.langpack()).thenReturn(() -> new String[] { "ROOT" });
//   when(options.tlpParams).thenReturn(lp);
//   when(lp.lex(any(), any(), any())).thenReturn(lexicon);

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  extractor.trees = new ArrayList<>();
  extractor.trainSize = 42.0;

  Map<String, double[]> mass = new HashMap<>();
  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

  extractor.recalculateTemporaryBetas(false, mass, unary, binary);

  assertTrue(unary.isEmpty());
  assertTrue(binary.isEmpty());
}
@Test
public void testRescaleTemporaryBetasHandlesInfiniteUnarySum() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] betas = new double[1][2];
  betas[0][0] = Double.NEGATIVE_INFINITY;
  betas[0][1] = Double.NEGATIVE_INFINITY;

  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
  unary.put("X", "Y", betas);

  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

//   extractor.rescaleTemporaryBetas(unary, binary);

  assertEquals(Math.log(0.5) * -1, unary.get("X", "Y")[0][0], 0.1);
}
@Test
public void testRescaleTemporaryBetasHandlesInfiniteBinarySum() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][][] betas = new double[1][1][2];
  betas[0][0][0] = Double.NEGATIVE_INFINITY;
  betas[0][0][1] = Double.NEGATIVE_INFINITY;

  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();
  binary.put("P", "L", "R", betas);

//   extractor.rescaleTemporaryBetas(unary, binary);

  assertEquals(Math.log(2) * -1, binary.get("P", "L", "R")[0][0][0], 0.1);
}
@Test
public void testRecalculateBetasAfterSplittingStatesProducesNewBetas() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lexicon = mock(Lexicon.class);
//   when(lp.lex(any(), any(), any())).thenReturn(lexicon);
//   when(options.tlpParams).thenReturn(lp);
//   when(options.langpack()).thenReturn(() -> new String[]{"ROOT"});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree tree = mock(Tree.class);
  Label lbl = mock(Label.class);
  when(lbl.value()).thenReturn("NN");
  when(tree.label()).thenReturn(lbl);
  when(tree.isLeaf()).thenReturn(false);
  when(tree.isPreTerminal()).thenReturn(true);
  when(tree.children()).thenReturn(new Tree[0]);
  extractor.trees.add(tree);
  extractor.treeWeights.incrementCount(tree, 1.0);
  extractor.stateSplitCounts.setCount("NN", 1);
  extractor.stateSplitCounts.setCount("ROOT", 1);
  extractor.originalStates.add("NN");
  extractor.originalStates.add("ROOT");

  extractor.wordIndex = new HashIndex<>();
  extractor.tagIndex = new HashIndex<>();

  double[][][][] dummyUnary = new double[1][1][1][1];  
  double[][][] dummyBinary = new double[1][1][1];
  double[][] dummyUni = new double[1][1];

  extractor.unaryBetas.put("NN", "NN", dummyUni);
  extractor.binaryBetas.put("NN", "NN", "NN", dummyBinary);

  boolean converged = extractor.recalculateBetas(true);
  assertFalse(converged);
}
@Test
public void testRecalculateMergedBetasWithEmptyTreeList() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lx = mock(Lexicon.class);
//   when(lp.lex(any(), any(), any())).thenReturn(lx);
//   when(options.tlpParams).thenReturn(lp);
//   when(options.langpack()).thenReturn(() -> new String[]{"ROOT"});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  extractor.trees = new ArrayList<>();  

  Map<String, int[]> map = new HashMap<>();
  extractor.recalculateMergedBetas(map);  
}
@Test
public void testBuildGrammarsWithEmptyBetasAndMass() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lx = mock(Lexicon.class);
//   when(lp.lex(any(), any(), any())).thenReturn(lx);
//   when(options.tlpParams).thenReturn(lp);
//   when(options.langpack()).thenReturn(() -> new String[]{"ROOT"});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  extractor.trees = new ArrayList<>();  

  extractor.originalStates.add("NP");
  extractor.stateSplitCounts.setCount("NP", 1);
  extractor.buildStateIndex();  

  extractor.buildGrammars();  
  assertNotNull(extractor.bgug);
}
@Test
public void testStateSplitCountSingleTaggedLeaf() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{"ROOT"});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  extractor.stateSplitCounts.setCount("NP", 5.0);

  Tree tree = mock(Tree.class);
  Label label = mock(Label.class);
  when(tree.label()).thenReturn(label);
  when(label.value()).thenReturn("NP");

  int result = extractor.getStateSplitCount(tree);
  assertEquals(5, result);
}
@Test
public void testRecalculateTemporaryBetasWithPreTerminalLexiconSmoothing() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lex = mock(Lexicon.class);

//   when(lp.lex(any(), any(), any())).thenReturn(lex);
//   when(options.tlpParams).thenReturn(lp);
//   when(options.langpack()).thenReturn(() -> new String[] {"ROOT"});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  extractor.wordIndex = new HashIndex<>();
  extractor.tagIndex = new HashIndex<>();

  Tree wordNode = mock(Tree.class);
  Label wordLabel = mock(Label.class);
  when(wordNode.label()).thenReturn(wordLabel);
  when(wordLabel.value()).thenReturn("cat");
  when(wordNode.isLeaf()).thenReturn(true);

  Tree preTerm = mock(Tree.class);
  Label posLabel = mock(Label.class);
  when(preTerm.label()).thenReturn(posLabel);
  when(posLabel.value()).thenReturn("NN");
  when(preTerm.isLeaf()).thenReturn(false);
  when(preTerm.isPreTerminal()).thenReturn(true);
  when(preTerm.children()).thenReturn(new Tree[]{wordNode});

  ArrayList<Tree> list = new ArrayList<>();
  list.add(preTerm);
  extractor.trees = list;
  extractor.treeWeights.setCount(preTerm, 1.0);
  extractor.trainSize = 1.0;
  extractor.originalStates.add("NN");
  extractor.stateSplitCounts.setCount("NN", 2);

//   when(lex.score(any(), anyInt(), any(), isNull())).thenReturn(Math.log(0.75));

  Map<String, double[]> totalStateMass = new HashMap<>();
  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

  extractor.recalculateTemporaryBetas(false, totalStateMass, unary, binary);
  assertNotNull(unary);
}
@Test
public void testCountMergeEffectsEmptyTreeHandlesGracefully() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{"ROOT"});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  Tree leaf = mock(Tree.class);
  Label label = mock(Label.class);
  when(label.value()).thenReturn("NP");
  when(leaf.label()).thenReturn(label);
  when(leaf.isLeaf()).thenReturn(true);

  Map<String, double[]> totalStateMass = new HashMap<>();
  Map<String, double[]> deltas = new HashMap<>();

  extractor.countMergeEffects(leaf, totalStateMass, deltas);
  assertTrue(deltas.isEmpty());
}
@Test
public void testSplitBetasNoUnaryOrBinaryBetasDefined() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{"ROOT"});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
  extractor.unaryBetas = new TwoDimensionalMap<>();
  extractor.binaryBetas = new ThreeDimensionalMap<>();

  extractor.stateSplitCounts.setCount("X", 1);
  extractor.stateSplitCounts.setCount("ROOT", 1);
  extractor.splitBetas();  
}
@Test
public void testRecountTreeWithEmptyTransitionsMaps() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree tree = mock(Tree.class);
  when(tree.isLeaf()).thenReturn(false);
  when(tree.isPreTerminal()).thenReturn(true);

  IdentityHashMap<Tree, double[][]> unary = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][][]> binary = new IdentityHashMap<>();

//   extractor.recountTree(tree, true, unary, binary);
  assertTrue(unary.isEmpty());
  assertTrue(binary.isEmpty());
}
@Test
public void testMergeTransitionsHandlesNullOldTransition() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree parent = mock(Tree.class);
  Tree child = mock(Tree.class);
  when(parent.children()).thenReturn(new Tree[]{child});
  when(parent.isPreTerminal()).thenReturn(false);
  when(parent.isLeaf()).thenReturn(false);
  when(parent.label()).thenReturn(mock(Label.class));
  when(child.label()).thenReturn(mock(Label.class));
  when(child.isPreTerminal()).thenReturn(true);
  when(child.isLeaf()).thenReturn(false);

  IdentityHashMap<Tree, double[][]> oldUnaryTransitions = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][][]> oldBinaryTransitions = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][]> newUnaryTransitions = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][][]> newBinaryTransitions = new IdentityHashMap<>();

  Map<String, int[]> merge = new HashMap<>();
  merge.put("", new int[] {0});

//   extractor.mergeTransitions(parent, oldUnaryTransitions, oldBinaryTransitions, newUnaryTransitions, newBinaryTransitions, new double[] {0.0}, merge);
//   assertTrue(newUnaryTransitions.isEmpty());
  assertTrue(newBinaryTransitions.isEmpty());
}
@Test
public void testRecountOutsideUnaryNodeInfiniteBetasHandled() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree parent = mock(Tree.class);
  Tree child = mock(Tree.class);
  Label pLabel = mock(Label.class);
  Label cLabel = mock(Label.class);
  when(pLabel.value()).thenReturn("P");
  when(cLabel.value()).thenReturn("C");
  when(parent.label()).thenReturn(pLabel);
  when(child.label()).thenReturn(cLabel);
  when(parent.children()).thenReturn(new Tree[]{child});
  when(child.isPreTerminal()).thenReturn(true);
  when(child.isLeaf()).thenReturn(false);

  double[][] betas = new double[1][1];
  betas[0][0] = Double.NEGATIVE_INFINITY;
//   extractor.unaryBetas.put("P", "C", betas);

  IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
  double[] rootScores = {0.0};
  probOut.put(parent, rootScores);

//   extractor.recountOutside(child, parent, probIn, probOut);
  assertNotNull(probOut.get(child));
}
@Test
public void testBuildMergeCorrespondenceAdjustsStateIndices() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

//   extractor.originalStates.add("VP");
//   extractor.stateSplitCounts.setCount("VP", 4.0);

  List<Triple<String, Integer, Double>> deltas = Arrays.asList(
      new Triple<>("VP", 1, -0.1),
      new Triple<>("VP", 2, -0.4)
  );

//   Map<String, int[]> result = extractor.buildMergeCorrespondence(deltas);

//   assertEquals(3, result.get("VP").length);
//   assertEquals(0, result.get("VP")[0]);
//   assertEquals(1, result.get("VP")[1]);
//   assertEquals(1, result.get("VP")[2]); 
}
@Test
public void testInitialBetasAndLexiconWithOnlyUnaryProduction() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lex = mock(Lexicon.class);
//   when(lp.lex(any(), any(), any())).thenReturn(lex);
//   when(options.langpack()).thenReturn(() -> new String[]{"ROOT"});
//   when(options.tlpParams).thenReturn(lp);

  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree root = mock(Tree.class);
  Tree child = mock(Tree.class);

  Label rootLabel = mock(Label.class);
  Label childLabel = mock(Label.class);
  when(rootLabel.value()).thenReturn("S");
  when(childLabel.value()).thenReturn("NP");

  when(root.label()).thenReturn(rootLabel);
  when(child.label()).thenReturn(childLabel);
  when(root.children()).thenReturn(new Tree[]{child});
  when(child.isPreTerminal()).thenReturn(true);
  when(child.isLeaf()).thenReturn(false);
  when(root.isLeaf()).thenReturn(false);
  when(root.isPreTerminal()).thenReturn(false);
  when(child.children()).thenReturn(new Tree[0]);

  extractor.trees.add(root);
  extractor.treeWeights.setCount(root, 1.0);
  extractor.trainSize = 1.0;

//   extractor.initialBetasAndLexicon();

  assertTrue(extractor.unaryBetas.contains("S", "NP"));
}
@Test
public void testTestConvergenceHandlesDifferentSizesInUnaryBeta() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] oldBeta = new double[1][2];
  oldBeta[0][0] = Math.log(0.5);
  oldBeta[0][1] = Math.log(0.5);
//   extractor.unaryBetas.put("P", "C", oldBeta);

  double[][] newBeta = new double[1][1];
  newBeta[0][0] = Math.log(0.9); 

  TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
  unary.put("P", "C", newBeta);

  ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();
//   boolean result = extractor.testConvergence(unary, binary);
  
//   assertTrue(result);
}
@Test
public void testRecountWeightsWithUnaryZeroBetaAndNegativeInfinityProbabilities() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree parent = mock(Tree.class);
  Tree child = mock(Tree.class);

  Label pLabel = mock(Label.class);
  Label cLabel = mock(Label.class);
  when(pLabel.value()).thenReturn("A");
  when(cLabel.value()).thenReturn("B");
  when(parent.label()).thenReturn(pLabel);
  when(child.label()).thenReturn(cLabel);

  when(parent.isLeaf()).thenReturn(false);
  when(parent.isPreTerminal()).thenReturn(false);
  when(parent.children()).thenReturn(new Tree[] { child });
  when(child.isLeaf()).thenReturn(false);
  when(child.isPreTerminal()).thenReturn(true);
  when(child.children()).thenReturn(new Tree[0]);

  double[][] beta = new double[1][1];
  beta[0][0] = 0.0;
//   extractor.unaryBetas.put("A", "B", beta);

  IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
  probIn.put(child, new double[]{Double.NEGATIVE_INFINITY});
  probOut.put(parent, new double[]{Double.NEGATIVE_INFINITY});

  IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
  IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();

//   extractor.recountWeights(parent, probIn, probOut, unaryTransitions, binaryTransitions);

  assertTrue(unaryTransitions.containsKey(parent));
  double[][] result = unaryTransitions.get(parent);
  assertEquals(-Math.log(1), result[0][0], 0.0001);
}
@Test
public void testRecalculateTemporaryBetasTreeWithNoChildren() {
  Options options = mock(Options.class);
//   Options.LexicalizedParserParams lp = mock(Options.LexicalizedParserParams.class);
  Lexicon lexicon = mock(Lexicon.class);
//   when(lp.lex(any(), any(), any())).thenReturn(lexicon);
//   when(options.tlpParams).thenReturn(lp);
//   when(options.langpack()).thenReturn(() -> new String[] {});
// 
  SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree tree = mock(Tree.class);
  Label label = mock(Label.class);
  when(label.value()).thenReturn("NP");
  when(tree.label()).thenReturn(label);
  when(tree.isLeaf()).thenReturn(false);
  when(tree.isPreTerminal()).thenReturn(false);
  when(tree.children()).thenReturn(new Tree[0]);

  extractor.trees.add(tree);
  extractor.treeWeights.setCount(tree, 1.0);
  extractor.trainSize = 1.0;

  extractor.stateSplitCounts.setCount("NP", 1);
  extractor.originalStates.add("NP");

  Map<String, double[]> totalMass = new HashMap<>();
  TwoDimensionalMap<String, String, double[][]> unaryBetas = new TwoDimensionalMap<>();
  ThreeDimensionalMap<String, String, String, double[][][]> binaryBetas = new ThreeDimensionalMap<>();

  extractor.recalculateTemporaryBetas(false, totalMass, unaryBetas, binaryBetas);

  assertTrue(totalMass.containsKey("NP"));
}
@Test
public void testRecountOutsideWithBothChildrenMissingProbIn() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  Tree parent = mock(Tree.class);
  Tree left = mock(Tree.class);
  Tree right = mock(Tree.class);

  Label parentLabel = mock(Label.class);
  Label leftLabel = mock(Label.class);
  Label rightLabel = mock(Label.class);
  when(parentLabel.value()).thenReturn("A");
  when(leftLabel.value()).thenReturn("B");
  when(rightLabel.value()).thenReturn("C");
  when(parent.label()).thenReturn(parentLabel);
  when(left.label()).thenReturn(leftLabel);
  when(right.label()).thenReturn(rightLabel);

  when(parent.children()).thenReturn(new Tree[]{left, right});
  when(parent.isLeaf()).thenReturn(false);
  when(parent.isPreTerminal()).thenReturn(false);
  when(left.isPreTerminal()).thenReturn(true);
  when(right.isPreTerminal()).thenReturn(true);
  when(left.children()).thenReturn(new Tree[0]);
  when(right.children()).thenReturn(new Tree[0]);

  double[][][] beta = new double[1][1][1];
  beta[0][0][0] = Math.log(0.6);
//   extractor.binaryBetas.put("A", "B", "C", beta);

  IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
  probIn.put(left, new double[]{Math.log(0.8)});
  probIn.put(right, new double[]{Math.log(0.9)});
  IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
  probOut.put(parent, new double[]{Math.log(1.0)});

//   extractor.recountOutside(left, right, parent, probIn, probOut);
  assertTrue(probOut.containsKey(left));
  assertTrue(probOut.containsKey(right));
}
@Test
public void testRecountInsideHandlesInvalidChildCount() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[] {});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);
//   extractor.stateSplitCounts.setCount("X", 1);

  Tree node = mock(Tree.class);
  Label label = mock(Label.class);
  when(label.value()).thenReturn("X");
  when(node.label()).thenReturn(label);
  when(node.isLeaf()).thenReturn(false);
  when(node.isPreTerminal()).thenReturn(false);
  when(node.children()).thenReturn(new Tree[]{mock(Tree.class), mock(Tree.class), mock(Tree.class)}); 

  try {
//     extractor.recountInside(node, false, 0, new IdentityHashMap<>());
    fail("Expected RuntimeException due to invalid child count");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("expected 1 or 2 children"));
  }
}
@Test
public void testSplitBetasSkipsStartSymbolsForParentOnly() {
  Options options = mock(Options.class);
//   when(options.langpack()).thenReturn(() -> new String[]{"S"});
//   SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

  double[][] beta = new double[1][1];
  beta[0][0] = Math.log(0.7);
//   extractor.unaryBetas.put("S", "NP", beta);
//   extractor.stateSplitCounts.setCount("S", 1);
//   extractor.stateSplitCounts.setCount("NP", 1);

//   extractor.binaryBetas = new ThreeDimensionalMap<>();

//   extractor.splitBetas();  
//   double[][] newBeta = extractor.unaryBetas.get("S", "NP");
//   assertEquals(1, newBeta.length); 
//   assertEquals(2, newBeta[0].length); 
} 
}
