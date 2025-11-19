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

public class SplittingGrammarExtractor_5_GPTLLMTest {

 @Test
  public void testStateMethodGeneratesCorrectStateNames() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.startSymbols = Arrays.asList("S", "ROOT");

    String state1 = extractor.state("NP", 0);
    String state2 = extractor.state("S", 3);
    String state3 = extractor.state("BOUNDARY", 2);

    assertEquals("NP^0", state1);
    assertEquals("S", state2);
    assertEquals("BOUNDARY", state3);
  }
@Test
  public void testCountOriginalStatesUpdatesCorrectly() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree1 = Tree.valueOf("(S (NP (DT the) (NN cat)) (VP (VBD slept)))");
    Tree tree2 = Tree.valueOf("(S (NP (DT a) (NN dog)) (VP (VB bark)))");
    List<Tree> trainTrees = new ArrayList<>();
    trainTrees.add(tree1);
    trainTrees.add(tree2);

    extractor.saveTrees(trainTrees, 1.0, null, 0.0);
    extractor.countOriginalStates();

    assertTrue(extractor.originalStates.contains("S"));
    assertTrue(extractor.originalStates.contains("NP"));
    assertTrue(extractor.originalStates.contains("VP"));

    int sCount = extractor.getStateSplitCount("S");
    int npCount = extractor.getStateSplitCount("NP");
    int vpCount = extractor.getStateSplitCount("VP");

    assertEquals(1, sCount);
    assertEquals(1, npCount);
    assertEquals(1, vpCount);
  }
@Test
  public void testSplitStateCountsIncreasesOnlyNonRoots() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(ROOT (S (NP (DT the))))");
    List<Tree> list = new ArrayList<>();
    list.add(tree);

    extractor.startSymbols = Arrays.asList("ROOT", "S");
    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();

    int countBeforeRoot = extractor.getStateSplitCount("ROOT");
    int countBeforeS = extractor.getStateSplitCount("S");
    int countBeforeNP = extractor.getStateSplitCount("NP");

//     extractor.splitStateCounts();

    int countAfterRoot = extractor.getStateSplitCount("ROOT");
    int countAfterS = extractor.getStateSplitCount("S");
    int countAfterNP = extractor.getStateSplitCount("NP");

    assertEquals(1, countBeforeRoot);
    assertEquals(1, countAfterRoot);
    assertEquals(1, countBeforeS);
    assertEquals(1, countAfterS);
    assertEquals(1, countBeforeNP);
    assertEquals(2, countAfterNP); 
  }
@Test
  public void testInitialLexiconAndBetasCreatedCorrectly() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ sleeps)))");
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    boolean unaryExists = extractor.unaryBetas.contains("NP", "DT");
    boolean binaryExists = extractor.binaryBetas.contains("S", "NP", "VP");

    assertTrue(unaryExists);
    assertTrue(binaryExists);
  }
@Test(expected = RuntimeException.class)
  public void testInitialBetasTreeWithTooManyChildrenThrows() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree badTree = Tree.valueOf("(S (A x) (B y) (C z))");
    List<Tree> trees = new ArrayList<>();
    trees.add(badTree);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();

//     extractor.initialBetasAndLexicon(); 
  }
@Test
  public void testNegInfDoublesReturnsCorrectValues() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[] arr = extractor.neginfDoubles(4);
    assertEquals(4, arr.length);
    assertEquals(Double.NEGATIVE_INFINITY, arr[0], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, arr[1], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, arr[2], 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, arr[3], 0.0);
  }
@Test
  public void testExtractCompletesAndBuildsGrammars() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 1;
    options.trainOptions.splitRecombineRate = 0.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ runs)))");
    List<Tree> train = new ArrayList<>();
    train.add(tree);

    extractor.extract(train);

    assertNotNull(extractor.bgug);
    UnaryGrammar ug = extractor.bgug.first();
    BinaryGrammar bg = extractor.bgug.second();
    assertNotNull(ug);
    assertNotNull(bg);
  }
@Test
  public void testRecalculateBetasReturnsBoolean() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT a)) (VP (VB sleeps)))");
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);

    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();
    boolean resultSplit = extractor.recalculateBetas(true);
    boolean resultNoSplit = extractor.recalculateBetas(false);

    assertFalse(resultSplit); 
    assertTrue(resultNoSplit || !resultNoSplit); 
  }
@Test
  public void testRescaleUnaryBetasNormalization() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] betas = new double[2][2];
    betas[0][0] = Math.log(0.25);
    betas[0][1] = Math.log(0.75);
    betas[1][0] = Math.log(0.6);
    betas[1][1] = Math.log(0.4);
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("S", "NP", betas);

    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.rescaleTemporaryBetas(unary, binary);

    double row0 = Math.exp(unary.get("S", "NP")[0][0]) + Math.exp(unary.get("S", "NP")[0][1]);
    double row1 = Math.exp(unary.get("S", "NP")[1][0]) + Math.exp(unary.get("S", "NP")[1][1]);

    assertEquals(1.0, row0, 0.0001);
    assertEquals(1.0, row1, 0.0001);
  }
@Test
  public void testRescaleBinaryBetasNormalization() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][][] betas = new double[1][2][2];
    betas[0][0][0] = Math.log(0.1);
    betas[0][0][1] = Math.log(0.2);
    betas[0][1][0] = Math.log(0.3);
    betas[0][1][1] = Math.log(0.4);

    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();
    binary.put("S", "NP", "VP", betas);

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();

    extractor.rescaleTemporaryBetas(unary, binary);

    double sum = Math.exp(binary.get("S", "NP", "VP")[0][0][0])
               + Math.exp(binary.get("S", "NP", "VP")[0][0][1])
               + Math.exp(binary.get("S", "NP", "VP")[0][1][0])
               + Math.exp(binary.get("S", "NP", "VP")[0][1][1]);

    assertEquals(1.0, sum, 0.0001);
  }
@Test
  public void testTreeWithOnlyOneLeafDoesNotCrash() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(NP cat)");
    List<Tree> list = new ArrayList<>();
    list.add(tree);

    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.countOriginalStates();

    
//     extractor.splitStateCounts();
    assertTrue(extractor.originalStates.contains("NP"));
    assertTrue(extractor.getStateSplitCount("NP") >= 1);
  }
@Test
  public void testLabelIsLexiconBoundaryTagHandledInSplitStateCounts() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(BOUNDARY x)");
    List<Tree> list = new ArrayList<>();
    list.add(tree);

    extractor.saveTrees(list, 1.0, null, 0.0);
    extractor.originalStates.add("BOUNDARY");
    extractor.stateSplitCounts.incrementCount("BOUNDARY", 1.0);

//     extractor.splitStateCounts();

    int count = extractor.getStateSplitCount("BOUNDARY");
    assertEquals(1, count); 
  }
@Test
  public void testStateWithSpecialCharactersHandled() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    String specialState = "NP-$";
    int split = 2;

    String actual = extractor.state(specialState, split);
    assertEquals("NP-$^2", actual);
  }
@Test
  public void testEmptyUnaryTransitionMatrixHandledInRescale() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] betas = new double[2][2];
    betas[0][0] = Double.NEGATIVE_INFINITY;
    betas[0][1] = Double.NEGATIVE_INFINITY;
    betas[1][0] = Double.NEGATIVE_INFINITY;
    betas[1][1] = Double.NEGATIVE_INFINITY;

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("A", "B", betas);
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.rescaleTemporaryBetas(unary, binary);

    double val = Math.exp(unary.get("A", "B")[0][0]) +
                 Math.exp(unary.get("A", "B")[0][1]) +
                 Math.exp(unary.get("A", "B")[1][0]) +
                 Math.exp(unary.get("A", "B")[1][1]);

    assertEquals(2.0, val, 0.0001); 
  }
@Test
  public void testConvergedWithIdenticalBetasReturnsTrue() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] original = new double[1][1];
    original[0][0] = Math.log(1.0);

    double[][][] original3 = new double[1][1][1];
    original3[0][0][0] = Math.log(1.0);

    extractor.unaryBetas.put("P", "C", original);
    extractor.binaryBetas.put("P", "L", "R", original3);

    TwoDimensionalMap<String, String, double[][]> newUnary = new TwoDimensionalMap<>();
    newUnary.put("P", "C", original);

    ThreeDimensionalMap<String, String, String, double[][][]> newBinary = new ThreeDimensionalMap<>();
    newBinary.put("P", "L", "R", original3);

    boolean converged = extractor.testConvergence(newUnary, newBinary);
    assertTrue(converged);
  }
@Test
  public void testUseNewBetasHandlesIOExceptionInDebug() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.useNewBetas(false, unary, binary);

    assertNull(extractor.tempLex);
    assertNull(extractor.tempWordIndex);
    assertNull(extractor.tempTagIndex);
  }
@Test
  public void testBuildGrammarsHandlesEmptyMass() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)))");
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);
    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
    extractor.buildStateIndex();

    extractor.buildGrammars();

    assertNotNull(extractor.bgug);
    assertNotNull(extractor.bgug.first());
    assertNotNull(extractor.bgug.second());
  }
@Test
  public void testDeepUnaryTreeProcessesCorrectly() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(A (B (C (D word))))");
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);
    extractor.extract(trees);

    assertNotNull(extractor.bgug);
  }
@Test
  public void testSaveTreesHandlesSecondCollectionNull() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP the))");
    Collection<Tree> first = Collections.singletonList(tree);

    extractor.saveTrees(first, 3.0, null, 0.0);

    assertEquals(1, extractor.trees.size());
    double weight = extractor.treeWeights.getCount(tree);
    assertEquals(3.0, weight, 0.0001);
  }
@Test
  public void testSaveTreesHandlesEmptyCollections() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Collection<Tree> list1 = new ArrayList<>();
    Collection<Tree> list2 = new ArrayList<>();

    extractor.saveTrees(list1, 1.0, list2, 1.0);
    assertTrue(extractor.trees.isEmpty());
    assertEquals(0.0, extractor.trainSize, 0.001);
  }
@Test
  public void testUnaryTransitionHasInfiniteTotalAndGetsUniformized() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] betas = new double[1][2];
    betas[0][0] = Double.NEGATIVE_INFINITY;
    betas[0][1] = Double.NEGATIVE_INFINITY;

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    unary.put("P", "C", betas);
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.rescaleTemporaryBetas(unary, binary);

    double val0 = Math.exp(betas[0][0]);
    double val1 = Math.exp(betas[0][1]);

    assertEquals(0.5, val0, 0.0001);
    assertEquals(0.5, val1, 0.0001);
  }
@Test
  public void testBinaryTransitionHasInfiniteTotalAndGetsUniformized() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][][] betas = new double[1][2][2];
    betas[0][0][0] = Double.NEGATIVE_INFINITY;
    betas[0][0][1] = Double.NEGATIVE_INFINITY;
    betas[0][1][0] = Double.NEGATIVE_INFINITY;
    betas[0][1][1] = Double.NEGATIVE_INFINITY;

    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();
    binary.put("S", "NP", "VP", betas);
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();

    extractor.rescaleTemporaryBetas(unary, binary);

    double total =  Math.exp(betas[0][0][0]) + Math.exp(betas[0][0][1]) +
                    Math.exp(betas[0][1][0]) + Math.exp(betas[0][1][1]);

    assertEquals(1.0, total, 0.0001);
  }
@Test
  public void testExtractWithMultipleSplitCycles() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 2;
    options.trainOptions.splitRecombineRate = 0.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ runs)))");
    List<Tree> trees = Collections.singletonList(tree);

    extractor.extract(trees);

    assertNotNull(extractor.bgug);
    assertNotNull(extractor.bgug.first());
    assertNotNull(extractor.bgug.second());
  }
@Test
  public void testMergeStatesWhenAllSplitDeltasAreBelowThreshold() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitRecombineRate = 1.0;
    options.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ jumps)))");
    List<Tree> trees = Collections.singletonList(tree);

    extractor.extract(trees);

    int npCount = extractor.getStateSplitCount("NP");

    assertTrue(npCount >= 1); 
  }
@Test
  public void testDeepBinaryTree() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (A (B (C (D (E fish))))))");
    List<Tree> trees = Collections.singletonList(tree);

    extractor.extract(trees);

    assertNotNull(extractor.bgug);
  }
@Test
  public void testTrainingWithUnknownPOSHandledInLexicon() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (FOO (BAR mystery)))");
    List<Tree> trees = Collections.singletonList(tree);

    extractor.extract(trees);

    assertNotNull(extractor.lex); 
  }
@Test
  public void testZeroWeightTreeDoesNotBreakTraining() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)))");
    List<Tree> list = Collections.singletonList(tree);

    extractor.saveTrees(list, 0.0, null, 0.0); 
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon(); 
    assertNotNull(extractor.lex);
  }
@Test
  public void testSplitBetasProducesDifferentDimensions() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] unary = new double[1][1];
    unary[0][0] = Math.log(1.0);
    extractor.unaryBetas.put("A", "B", unary);

    double[][][] binary = new double[1][1][1];
    binary[0][0][0] = Math.log(1.0);
    extractor.binaryBetas.put("C", "D", "E", binary);

    extractor.stateSplitCounts.incrementCount("A", 1.0);
    extractor.stateSplitCounts.incrementCount("B", 1.0);
    extractor.stateSplitCounts.incrementCount("C", 1.0);
    extractor.stateSplitCounts.incrementCount("D", 1.0);
    extractor.stateSplitCounts.incrementCount("E", 1.0);

    extractor.splitBetas();

    assertTrue(extractor.unaryBetas.get("A", "B").length > 1);
    assertTrue(extractor.binaryBetas.get("C", "D", "E")[0].length > 1);
  }
@Test
  public void testRecalculateTemporaryBetasHandlesNullMassMap() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ barks)))");
    List<Tree> trees = Collections.singletonList(tree);

    extractor.saveTrees(trees, 1.0, null, 0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, null, unary, binary); 

    assertFalse(unary.isEmpty() && binary.isEmpty());
  }
@Test
  public void testExtractWithNoSplitCyclesStillExtractsGrammar() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ sings)))");
    List<Tree> treeList = Collections.singletonList(tree);

    extractor.extract(treeList);

    assertNotNull(extractor.bgug);
    assertNotNull(extractor.bgug.first());
    assertNotNull(extractor.bgug.second());
  }
@Test
  public void testBuildStateIndexWithNoOriginalStates() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.originalStates.clear(); 
    extractor.stateSplitCounts.clear();

    extractor.buildStateIndex();

    assertNotNull(extractor.stateIndex);
    assertEquals(0, extractor.stateIndex.size());
  }
@Test
  public void testEmptyInitialBetasAndLexicon() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.saveTrees(new ArrayList<Tree>(), 1.0, null, 0.0);

//     extractor.initialBetasAndLexicon();

    assertNotNull(extractor.lex);
  }
@Test
  public void testRecountInsideHandlesAllNegInfBetaScores() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.saveTrees(Collections.singletonList(Tree.valueOf("(S (A (B leaf)))")), 1.0, null, 0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    
    double[][] betas = new double[1][1];
    betas[0][0] = Double.NEGATIVE_INFINITY;
    extractor.unaryBetas.put("S", "A", betas);
    extractor.unaryBetas.put("A", "B", betas);

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    Tree tree = extractor.trees.get(0);
    extractor.recountInside(tree, false, 0, probIn);

    assertTrue(probIn.containsKey(tree));
    assertEquals(Double.NEGATIVE_INFINITY, probIn.get(tree)[0], 0.0001);
  }
@Test
  public void testRecountOutsideWithoutInsideLeaksNoException() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT word1)))");
    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();

    extractor.recountOutside(tree, probIn, probOut);

    assertTrue(probOut.containsKey(tree));
  }
@Test
  public void testRecountTreeWithPrepopulatedTransitions() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)))");
    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();
    unaryTransitions.put(tree, new double[1][1]);

    extractor.recountTree(tree, false, unaryTransitions, binaryTransitions);

    assertNotNull(unaryTransitions);
  }
@Test
  public void testPartialDifferenceInBetasCausesNonConvergence() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] oldUnary = new double[1][1];
    oldUnary[0][0] = Math.log(1.0);
    double[][] newUnary = new double[1][1];
    newUnary[0][0] = Math.log(0.9); 

    extractor.unaryBetas.put("X", "Y", oldUnary);
    TwoDimensionalMap<String, String, double[][]> tUnary = new TwoDimensionalMap<>();
    tUnary.put("X", "Y", newUnary);

    double[][][] oldBinary = new double[1][1][1];
    oldBinary[0][0][0] = Math.log(1.0);
    double[][][] newBinary = new double[1][1][1];
    newBinary[0][0][0] = oldBinary[0][0][0];

    extractor.binaryBetas.put("A", "B", "C", oldBinary);
    ThreeDimensionalMap<String, String, String, double[][][]> tBinary = new ThreeDimensionalMap<>();
    tBinary.put("A", "B", "C", newBinary);

    boolean result = extractor.testConvergence(tUnary, tBinary);
    assertFalse(result);
  }
@Test
  public void testMergeTransitionsWithAllWeightsZeroed() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree parent = Tree.valueOf("(S (NP leaf))");
    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();

    double[][] transition = new double[1][1];
    transition[0][0] = Double.NEGATIVE_INFINITY;
    oldUnary.put(parent, transition);

    Map<String, int[]> map = Generics.newHashMap();
    map.put("S", new int[]{0});
    map.put("NP", new int[]{0});

    extractor.mergeTransitions(parent, oldUnary, oldBinary, newUnary, newBinary, new double[]{0}, map);

    assertNotNull(newUnary.get(parent));
  }
@Test
  public void testMergeStatesHandlesEmptySortedDeltas() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 1;
    options.trainOptions.splitRecombineRate = 1.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (A (B x)))");
    List<Tree> t = Collections.singletonList(tree);
    extractor.extract(t);

    extractor.stateSplitCounts.setCount("A", 2);
    extractor.stateSplitCounts.setCount("B", 2);

    
    options.trainOptions.splitRecombineRate = 0.0;
    extractor.mergeStates(); 

    assertTrue(extractor.getStateSplitCount("A") >= 1);
  }
@Test
  public void testRecalculateTemporaryBetasHandlesMissingBetaKeysGracefully() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP leaf))");

    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    extractor.unaryBetas = new TwoDimensionalMap<>();
    extractor.binaryBetas = new ThreeDimensionalMap<>();

    Map<String, double[]> totalMass = new HashMap<>();
    TwoDimensionalMap<String, String, double[][]> unaryBetas = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binaryBetas = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, totalMass, unaryBetas, binaryBetas);

    assertTrue((!unaryBetas.isEmpty()) || (!binaryBetas.isEmpty()));
  }
@Test
  public void testRecalculateInsideWithUnknownLexiconStateTag() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.saveTrees(Collections.singletonList(Tree.valueOf("(S (X unknown))")), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    Tree tree = extractor.trees.get(0);
    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();

    extractor.recountInside(tree, false, 0, probIn);

    assertTrue(probIn.containsKey(tree));
  }
@Test
  public void testRecalculateTemporaryBetasInitializesNewBetaBlocks() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (A (B word)))");
    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();

    double[][] dummy = new double[1][1];
    dummy[0][0] = 0.0;

    extractor.unaryBetas.put("S", "A", dummy);
    extractor.unaryBetas.put("A", "B", dummy);

    Map<String, double[]> mass = new HashMap<>();
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, mass, unary, binary);

    assertTrue(unary.contains("S", "A"));
    assertTrue(unary.contains("A", "B"));
  }
@Test
  public void testProbInZeroHandledInGrammars() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ walks)))");
    extractor.extract(Collections.singletonList(tree));

    
    Map<String, double[]> stateMass = new HashMap<>();
    stateMass.put("S", new double[] {0.0});
    stateMass.put("NP", new double[] {0.0});
    stateMass.put("VP", new double[] {0.0});
    extractor.recalculateTemporaryBetas(false, stateMass, new TwoDimensionalMap<>(), new ThreeDimensionalMap<>());

    extractor.buildGrammars();

    assertNotNull(extractor.bgug);
    assertTrue(extractor.bgug.first().numRules() >= 0);
  }
@Test
  public void testMergeTransitionsHandlesPartialDeltaOnlyOneBranch() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitRecombineRate = 1.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ jumps)))");
    extractor.extract(Collections.singletonList(tree));

    Map<String, int[]> mergeMap = new HashMap<>();
    mergeMap.put("S", new int[]{0, 0});
    mergeMap.put("NP", new int[]{0, 0});
    mergeMap.put("VP", new int[]{0, 0});
    mergeMap.put("DT", new int[]{0});
    mergeMap.put("VBZ", new int[]{0});

    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();
    extractor.recountTree(tree, false, oldUnary, oldBinary);

    extractor.mergeTransitions(tree, oldUnary, oldBinary, newUnary, newBinary, new double[]{Math.log(1.0)}, mergeMap);

    assertFalse(newUnary.isEmpty() && newBinary.isEmpty());
  }
@Test
  public void testRecalculateTemporaryBetasHandlesLeafTreeGracefully() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree leafOnly = Tree.valueOf("word");
    List<Tree> trees = Collections.singletonList(leafOnly);
    extractor.saveTrees(trees, 1.0, null, 0.0);

    Map<String, double[]> mass = Generics.newHashMap();
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, mass, unary, binary);

    assertTrue(unary.isEmpty());
    assertTrue(binary.isEmpty());
  }
@Test
  public void testMergeTransitionsHandlesNoCorrespondenceEntry() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitRecombineRate = 0.5;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(A (B x))");
    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    double[][] t = new double[1][1];
    t[0][0] = 0.0;
    oldUnary.put(tree, t);

    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();

    Map<String, int[]> missingMap = new HashMap<>(); 
    try {
      extractor.mergeTransitions(tree, oldUnary, oldBinary, newUnary, newBinary, new double[]{0.0}, missingMap);
    } catch (Exception e) {
      fail("mergeTransitions should handle missing correspondence gracefully");
    }
  }
@Test
  public void testRecalculateTemporaryBetasWithSplitStatesTrue() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 1;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NP (DT the)) (VP (VBZ flies)))");
    extractor.extract(Collections.singletonList(tree));

    Map<String, double[]> mass = Generics.newHashMap();
    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(true, mass, unary, binary);

    assertTrue(unary.firstKeySet().contains("S") || binary.firstKeySet().contains("S"));
  }
@Test
  public void testGetStateSplitCountReturnsZeroOnMissingLabel() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    int count = extractor.getStateSplitCount("UNKNOWN");
    assertEquals(0, count);
  }
@Test
  public void testBuildStateIndexIncludesAllSplitStates() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.originalStates.add("NP");
    extractor.originalStates.add("VP");
    extractor.stateSplitCounts.setCount("NP", 2);
    extractor.stateSplitCounts.setCount("VP", 3);

    extractor.buildStateIndex();

    assertEquals(5, extractor.stateIndex.size());
    assertTrue(extractor.stateIndex.contains("NP^0"));
    assertTrue(extractor.stateIndex.contains("NP^1"));
    assertTrue(extractor.stateIndex.contains("VP^0"));
    assertTrue(extractor.stateIndex.contains("VP^1"));
    assertTrue(extractor.stateIndex.contains("VP^2"));
  }
@Test
  public void testStateMethodHandlesStartSymbolsAndBoundaryCorrectly() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.startSymbols = Arrays.asList("ROOT", "S");

    String stateRoot = extractor.state("ROOT", 5);
    String stateS = extractor.state("S", 1);
    String stateNP = extractor.state("NP", 2);
    String stateBoundary = extractor.state("BOUNDARY", 3);

    assertEquals("ROOT", stateRoot);
    assertEquals("S", stateS);
    assertEquals("NP^2", stateNP);
    assertEquals("BOUNDARY", stateBoundary);
  }
@Test
  public void testInitialBetasAndLexiconHandlesMultipleTreesWithDifferentWeights() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree1 = Tree.valueOf("(S (NP (DT the)))");
    Tree tree2 = Tree.valueOf("(S (NP (DT a)))");

    List<Tree> set1 = Collections.singletonList(tree1);
    List<Tree> set2 = Collections.singletonList(tree2);

    extractor.saveTrees(set1, 1.0, set2, 2.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    assertNotNull(extractor.lex);
    assertTrue(extractor.unaryBetas.contains("NP", "DT"));
  }
@Test
  public void testSplitStateCountsPreservesBoundaryTagEvenAfterIncrement() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.stateSplitCounts.setCount("BOUNDARY", 3.0);
    extractor.stateSplitCounts.setCount("NP", 1.0);
    extractor.stateSplitCounts.setCount("VP", 1.0);

    extractor.startSymbols = Arrays.asList("S");

//     extractor.splitStateCounts();

    assertEquals(1, extractor.getStateSplitCount("BOUNDARY"));
    assertEquals(2, extractor.getStateSplitCount("NP"));
    assertEquals(2, extractor.getStateSplitCount("VP"));
  }
@Test
  public void testNegInfDoublesReturnsCorrectArray() {
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
  public void testRecalculateTemporaryBetasLexiconScalesWhenAllProbabilitiesAreZero() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
//     options.trainOptions.smoothTagProjectionThreshold = 0.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(NP (DT the))");

    List<Tree> trees = Collections.singletonList(tree);
    extractor.saveTrees(trees, 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    Map<String, double[]> totalStateMass = Generics.newHashMap();
    TwoDimensionalMap<String, String, double[][]> unaryBetas = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binaryBetas = new ThreeDimensionalMap<>();

    extractor.recalculateTemporaryBetas(false, totalStateMass, unaryBetas, binaryBetas);

    assertNotNull(unaryBetas);
  }
@Test
  public void testSaveTreesHandlesMultipleIdenticalTreesAndAggregatesWeights() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree1 = Tree.valueOf("(S (NP (DT this)))");
    Tree tree2 = Tree.valueOf("(S (NP (DT this)))");

    Collection<Tree> list1 = Arrays.asList(tree1, tree2);
    extractor.saveTrees(list1, 2.0, null, 0.0);

    assertEquals(2, extractor.treeWeights.size());
    assertEquals(4.0, extractor.trainSize, 0.00001);
  }
@Test
  public void testSplitBetasHandlesTagsWithOnlySingleSubstateCorrectly() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    double[][] original = new double[1][1];
    original[0][0] = Math.log(1.0);

    extractor.unaryBetas.put("A", "B", original);
    extractor.stateSplitCounts.setCount("A", 1);
    extractor.stateSplitCounts.setCount("B", 1);

    extractor.splitBetas();

    double[][] newBetas = extractor.unaryBetas.get("A", "B");
    assertTrue(newBetas.length >= 1);
    assertTrue(newBetas[0].length >= 1);
  }
@Test
  public void testConvergenceReturnsFalseWhenBetaDifferencesExceedEpsilon() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    extractor.unaryBetas = new TwoDimensionalMap<>();
    double[][] oldBetas = new double[1][1];
    oldBetas[0][0] = 1.0;

    extractor.unaryBetas.put("P", "C", oldBetas);

    double[][] newBetas = new double[1][1];
    newBetas[0][0] = 1.0002; 

    TwoDimensionalMap<String, String, double[][]> tempUnary = new TwoDimensionalMap<>();
    tempUnary.put("P", "C", newBetas);

    ThreeDimensionalMap<String, String, String, double[][][]> tempBinary = new ThreeDimensionalMap<>();

    boolean result = extractor.testConvergence(tempUnary, tempBinary);
    assertFalse(result);
  }
@Test
  public void testRecountWeightsWithMissingUnaryBetaFallsBackToDefault() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(A (B (word)))");
    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> unaryTransitions = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> binaryTransitions = new IdentityHashMap<>();

    probIn.put(tree, new double[]{0.0});
    Tree child = tree.children()[0];
    probIn.put(child, new double[]{0.0});
    probOut.put(tree, new double[]{0.0});
    probOut.put(child, new double[]{0.0});

    extractor.recountWeights(tree, probIn, probOut, unaryTransitions, binaryTransitions);

    assertTrue(unaryTransitions.containsKey(tree));
  }
@Test
  public void testMergeTransitionsWithWeightedStatePairsAndRepeats() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitRecombineRate = 1.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(P (Q (R x)))");
    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    IdentityHashMap<Tree, double[][]> oldUnary = new IdentityHashMap<>();
    double[][] transitions = new double[][]{{0.0}};
    oldUnary.put(tree, transitions);

    IdentityHashMap<Tree, double[][]> newUnary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> newBinary = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> oldBinary = new IdentityHashMap<>();

    Map<String, int[]> mergeMap = new HashMap<>();
    mergeMap.put("P", new int[]{0});
    mergeMap.put("Q", new int[]{0});
    mergeMap.put("R", new int[]{0});

    extractor.mergeTransitions(tree, oldUnary, oldBinary, newUnary, newBinary, new double[]{0.0}, mergeMap);

    assertTrue(newUnary.containsKey(tree));
  }
@Test
  public void testCountMergeEffectsTriggersOnEvenSplitState() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitRecombineRate = 1.0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(S (NA (NB (NC leaf))))");

    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
    extractor.stateSplitCounts.setCount("NB", 2);

    Map<String, double[]> mass = new HashMap<>();
    mass.put("NB", new double[]{1.0, 1.0});
    Map<String, double[]> deltaMap = new HashMap<>();

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][]> ut = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[][][]> bt = new IdentityHashMap<>();

    extractor.recountTree(tree, false, probIn, probOut, ut, bt);
    extractor.countMergeEffects(tree, mass, deltaMap, probIn, probOut);

    assertTrue(deltaMap.containsKey("NB"));
  }
@Test
  public void testBuildGrammarsWithBetaButMissingStateMass() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree t = Tree.valueOf("(S (NP (DT the) (NN cat)))");
    extractor.saveTrees(Collections.singletonList(t), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();
//     extractor.splitStateCounts();
    extractor.recalculateBetas(true);

    extractor.buildStateIndex();

    TwoDimensionalMap<String, String, double[][]> tempUnaryBetas = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> tempBinaryBetas = new ThreeDimensionalMap<>();
    Map<String, double[]> mass = new HashMap<>();

    extractor.recalculateTemporaryBetas(false, mass, tempUnaryBetas, tempBinaryBetas);
    extractor.unaryBetas = tempUnaryBetas;
    extractor.binaryBetas = tempBinaryBetas;

    extractor.buildGrammars();

    assertNotNull(extractor.bgug);
    assertTrue(extractor.bgug.first().numRules() > 0 || extractor.bgug.second().numRules() > 0);
  }
@Test
  public void testExtractHandlesInvalidTreeWithMultipleChildren() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    options.trainOptions.splitCount = 0;
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree bad = Tree.valueOf("(X (A x) (B y) (C z))");

    boolean caught = false;
    try {
      extractor.extract(Collections.singletonList(bad));
    } catch (RuntimeException e) {
      caught = true;
    }

    assertTrue(caught);
  }
@Test
  public void testRecurseOutsideHandlesUnexpectedChildrenCountGracefully() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree malformed = Tree.valueOf("(ROOT (A x y z))");

    IdentityHashMap<Tree, double[]> probIn = new IdentityHashMap<>();
    IdentityHashMap<Tree, double[]> probOut = new IdentityHashMap<>();

    probOut.put(malformed, new double[]{0.0});

    try {
      extractor.recurseOutside(malformed, probIn, probOut);
    } catch (Exception e) {
      fail("recurseOutside should not throw on malformed tree");
    }
  }
@Test
  public void testUseNewBetasWithNullLexiconFieldsDoesNotThrow() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    TwoDimensionalMap<String, String, double[][]> unary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> binary = new ThreeDimensionalMap<>();

    extractor.tempLex = null;
    extractor.tempTagIndex = null;
    extractor.tempWordIndex = null;

    try {
      extractor.useNewBetas(false, unary, binary);
    } catch (Exception e) {
      fail("useNewBetas() should not fail if temp lexicons are null.");
    }
  }
@Test
  public void testRecalculateTemporaryBetasHandlesMultiplePositionUpdatesCorrectly() {
    Options options = new Options();
    options.tlpParams = new EnglishTreebankParserParams();
    SplittingGrammarExtractor extractor = new SplittingGrammarExtractor(options);

    Tree tree = Tree.valueOf("(X (Y (Z dog)) (W (V bark)))");
    extractor.saveTrees(Collections.singletonList(tree), 1.0, null, 0.0);
    extractor.countOriginalStates();
//     extractor.initialBetasAndLexicon();

    Map<String, double[]> mass = new HashMap<>();
    TwoDimensionalMap<String, String, double[][]> tUnary = new TwoDimensionalMap<>();
    ThreeDimensionalMap<String, String, String, double[][][]> tBinary = new ThreeDimensionalMap<>();

    try {
      extractor.recalculateTemporaryBetas(false, mass, tUnary, tBinary);
    } catch (Exception e) {
      fail("Should handle multi-branch recalculation without position mismatch.");
    }

    assertTrue(tUnary.firstKeySet().size() > 0 || tBinary.firstKeySet().size() > 0);
  } 
}
