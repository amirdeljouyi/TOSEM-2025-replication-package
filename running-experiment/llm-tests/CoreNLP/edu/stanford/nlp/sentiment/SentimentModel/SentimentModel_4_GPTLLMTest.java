package edu.stanford.nlp.sentiment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.neural.SimpleTensor;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.TwoDimensionalMap;
import java.io.*;
import java.util.*;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

public class SentimentModel_4_GPTLLMTest {

  @Test
  public void testRandomWordVectorGeneration() {
    Random random = new Random(123);
    SimpleMatrix vec = SentimentModel.randomWordVector(4, random);
    assertNotNull(vec);
    assertEquals(4, vec.getNumElements());
  }

  @Test
  public void testGetWordVectorKnownWord() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numHid = 4;
    options.numClasses = 3;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("KnownWord");
    List<Tree> trees = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, trees);
    SimpleMatrix vector = model.getWordVector("KnownWord");
    assertNotNull(vector);
    assertEquals(4, vector.getNumElements());
  }

  @Test
  public void testGetWordVectorUnknownReturnsUnkVector() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("sample");
    List<Tree> trainingTrees = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, trainingTrees);
    SimpleMatrix vector = model.getWordVector("unknownword_abc_xyz");
    assertNotNull(vector);
    assertEquals(3, vector.getNumElements());
  }

  @Test
  public void testParamsToVectorAndBack() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("A");
    List<Tree> trainingTrees = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, trainingTrees);
    double[] original = model.paramsToVector();
    double[] copy = Arrays.copyOf(original, original.length);
    model.vectorToParams(copy);
    double[] after = model.paramsToVector();
    assertArrayEquals(original, after, 1e-10);
  }

  @Test
  public void testBasicCategorySimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = true;
    SentimentModel model = new SentimentModel(options, new ArrayList<>());
    String basic = model.basicCategory("NP");
    assertEquals("", basic);
  }

  @Test
  public void testGetClassificationUnaryMatrix() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("word");
    List<Tree> trainingTrees = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, trainingTrees);
    SimpleMatrix mat = model.getUnaryClassification("");
    assertNotNull(mat);
    assertEquals(2, mat.numRows());
    assertEquals(4, mat.numCols());
  }

  @Test
  public void testModelFromMatricesValid() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor tensor = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("hello", new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, tensor, wordVectors, options);
    assertNotNull(model);
    assertEquals(3, model.numClasses);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testModelFromMatricesInvalidOptionsThrows() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = false;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor tensor = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("word", new SimpleMatrix(2, 1));
    SentimentModel.modelFromMatrices(W, Wcat, tensor, wordVectors, options);
  }

  @Test
  public void testToStringProducesNonEmptyOutput() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numClasses = 2;
    options.numHid = 4;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("Leaf");
    List<Tree> trees = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, trees);
    String output = model.toString();
    assertNotNull(output);
    assertTrue(output.contains("Word vectors"));
  }

  @Test(expected = AssertionError.class)
  public void testGetWForNodeWithUnaryChildThrows() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numClasses = 2;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("(A B)");
    SentimentModel model =
        new SentimentModel(options, Collections.singletonList(Tree.valueOf("Leaf")));
    model.getWForNode(tree);
  }

  @Test
  public void testGetBinaryTransformMatrix() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numClasses = 2;
    options.numHid = 3;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("abc");
    List<Tree> trainingTrees = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, trainingTrees);
    SimpleMatrix transform = model.getBinaryTransform("", "");
    assertNotNull(transform);
    assertEquals(3, transform.numRows());
  }

  @Test
  public void testGetBinaryTensorIfEnabled() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numClasses = 2;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.useTensors = true;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("test");
    List<Tree> trainingTrees = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, trainingTrees);
    SimpleTensor tensor = model.getBinaryTensor("", "");
    assertNotNull(tensor);
  }

  @Test(expected = AssertionError.class)
  public void testGetBinaryTensorWhenTensorDisabledThrows() {
    RNNOptions options = new RNNOptions();
    options.useTensors = false;
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree treeNode = new LabeledScoredTreeFactory().newLeaf("word");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(treeNode));
    Tree binaryNode = Tree.valueOf("(A B C)");
    model.getTensorForNode(binaryNode);
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeInvalidChildCountThrows() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.numClasses = 3;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("Token");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    Tree badTree = Tree.valueOf("(A B C D)");
    model.getClassWForNode(badTree);
  }

  @Test(expected = RuntimeException.class)
  public void testInitRandomVectorsThrowsWhenNumHidIsZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 0;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.numClasses = 2;
    options.trainOptions.scalingForInit = 1.0;
    SentimentModel model = new SentimentModel(options, new ArrayList<>());
    model.initRandomWordVectors(Collections.singletonList(Tree.valueOf("A")));
  }

  @Test
  public void testGetVocabWordReturnsOriginalForExactMatch() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.lowercaseWordVectors = false;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("WordCase");
    List<Tree> trees = Collections.singletonList(leaf);
    SentimentModel model = new SentimentModel(options, trees);
    String vocabWord = model.getVocabWord("WordCase");
    assertEquals("WordCase", vocabWord);
  }

  @Test
  public void testWordVectorWithLowercaseMatch() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.lowercaseWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("Token");
    List<Tree> trees = Collections.singletonList(leaf);
    SentimentModel model = new SentimentModel(options, trees);
    assertNotNull(model.getWordVector("token"));
    assertNotNull(model.getWordVector("Token"));
  }

  @Test
  public void testUnaryClassificationReturnsNullIfNoneExists() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.lowercaseWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("sample");
    List<Tree> list = Collections.singletonList(leaf);
    SentimentModel model = new SentimentModel(options, list);
    SimpleMatrix matrix = model.getUnaryClassification("nonexistent_category");
    assertNull(matrix);
  }

  @Test
  public void testGetBinaryClassificationUsesUnaryMapWhenCombineEnabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("leaf");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    SimpleMatrix matrix = model.getBinaryClassification("A", "B");
    assertNotNull(matrix);
    assertEquals(3, matrix.numRows());
  }

  @Test
  public void testTotalParamSizeWithMinimalConfig() {
    RNNOptions options = new RNNOptions();
    options.numHid = 1;
    options.numClasses = 1;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("w");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    int size = model.totalParamSize();
    assertTrue(size > 0);
  }

  @Test(expected = RuntimeException.class)
  public void testReadWordVectorsFailsWhenUnknownVectorIsMissing() {
    RNNOptions options = new RNNOptions();
    options.wordVectors = "data/does/not/actually/exist.txt";
    options.numHid = 3;
    options.randomWordVectors = false;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.numClasses = 2;
    options.trainOptions.scalingForInit = 1.0;
    SentimentModel model = new SentimentModel(options, new ArrayList<>());
    model.readWordVectors();
  }

  @Test
  public void testPrintParamInformationPrintsEndBoundary() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.lowercaseWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("test");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    int size = model.totalParamSize();
    model.printParamInformation(size);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForUnaryNodeThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.useTensors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("a");
    Tree unary =
        new LabeledScoredTreeFactory().newTreeNode("LABEL", Collections.singletonList(leaf));
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    model.getTensorForNode(unary);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForInvalidChildrenCountThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.useTensors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree node = Tree.valueOf("(X A B C)");
    Tree dummy = new LabeledScoredTreeFactory().newLeaf("w");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(dummy));
    model.getTensorForNode(node);
  }

  @Test
  public void testGetClassWForNodeWithTwoChildrenClassMatrixRetrieved() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.useTensors = false;
    options.trainOptions.scalingForInit = 1.0;
    Tree left = new LabeledScoredTreeFactory().newLeaf("L");
    Tree right = new LabeledScoredTreeFactory().newLeaf("R");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("P", Arrays.asList(left, right));
    SentimentModel model = new SentimentModel(options, Collections.singletonList(left));
    SimpleMatrix result = model.getClassWForNode(parent);
    assertNotNull(result);
    assertEquals(2, result.numRows());
  }

  @Test
  public void testGetClassWForNodeUnarySimplifiedCombineFalse() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree terminal = new LabeledScoredTreeFactory().newLeaf("X");
    Tree unary =
        new LabeledScoredTreeFactory().newTreeNode("TAG", Collections.singletonList(terminal));
    SentimentModel model = new SentimentModel(options, Collections.singletonList(terminal));
    SimpleMatrix mat = model.getClassWForNode(unary);
    assertNotNull(mat);
    assertEquals(3, mat.numRows());
    assertEquals(5, mat.numCols());
  }

  @Test
  public void testGetWForNodeValidTwoChildren() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.randomWordVectors = true;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree left = new LabeledScoredTreeFactory().newLeaf("A");
    Tree right = new LabeledScoredTreeFactory().newLeaf("B");
    Tree binaryNode = new LabeledScoredTreeFactory().newTreeNode("X", Arrays.asList(left, right));
    SentimentModel model = new SentimentModel(options, Collections.singletonList(left));
    SimpleMatrix w = model.getWForNode(binaryNode);
    assertNotNull(w);
    assertEquals(3, w.numRows());
  }

  @Test
  public void testGetBinaryClassificationWithoutCombineFlag() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 4;
    options.randomWordVectors = true;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("x");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    SimpleMatrix result = model.getBinaryClassification("X", "Y");
    assertNotNull(result);
    assertEquals(4, result.numRows());
  }

  @Test
  public void testBasicCategoryDropsAtSymbolWhenSimplifiedFalse() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = false;
    // options.langpack = new RNNLangpackStub();
    SentimentModel model = new SentimentModel(options, new ArrayList<>());
    String category = model.basicCategory("@NP");
    assertEquals("NP", category);
  }

  @Test
  public void testGetUnaryClassificationCategoryStripped() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = false;
    options.trainOptions.scalingForInit = 1.0;
    // options.langpack = new RNNLangpackStub();
    SimpleMatrix mat = new SimpleMatrix(2, 3);
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("NP", mat);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            new HashMap<>(),
            options);
    SimpleMatrix retrieved = model.getUnaryClassification("@NP");
    assertSame(mat, retrieved);
  }

  @Test
  public void testPrintParamInformationJustWithinBounds() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("end");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    int length = model.totalParamSize();
    model.printParamInformation(length - 1);
  }

  @Test
  public void testParamsToVectorZeroWordVectorShape() {
    RNNOptions options = new RNNOptions();
    options.numHid = 1;
    options.numClasses = 1;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("xx");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    double[] params = model.paramsToVector();
    assertNotNull(params);
    assertTrue(params.length > 0);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testVectorToParamsTooShortThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("Z");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    double[] params = new double[1];
    model.vectorToParams(params);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testModelFromMatricesTensorShapeInvalidThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    SimpleTensor tensor = new SimpleTensor(3, 3, 3);
    Map<String, SimpleMatrix> wordVecs = new HashMap<>();
    wordVecs.put("x", new SimpleMatrix(2, 1));
    SentimentModel.modelFromMatrices(W, Wcat, tensor, wordVecs, options);
  }

  @Test
  public void testUnknownWordVectorFallsBackCleanly() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.lowercaseWordVectors = true;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("Test");
    List<Tree> list = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, list);
    SimpleMatrix vec1 = model.getWordVector("nonexistentWord12345");
    SimpleMatrix vec2 = model.getWordVector(SentimentModel.UNKNOWN_WORD);
    assertNotNull(vec1);
    assertNotNull(vec2);
    assertTrue(vec1.isIdentical(vec2, 1e-8));
  }

  @Test
  public void testUnknownWordCaseDoesNotMatchKnownCaseWhenLowercasingDisabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.lowercaseWordVectors = false;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("Apple");
    List<Tree> trees = Collections.singletonList(leaf);
    SentimentModel model = new SentimentModel(options, trees);
    String vocabWord = model.getVocabWord("apple");
    assertEquals(SentimentModel.UNKNOWN_WORD, vocabWord);
  }

  @Test
  public void testParamsToVectorAndVectorToParamsHandlesIdentityPreservation() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("X");
    List<Tree> list = Collections.singletonList(tree);
    SentimentModel model = new SentimentModel(options, list);
    double[] original = model.paramsToVector();
    double[] vectorCopy = Arrays.copyOf(original, original.length);
    model.vectorToParams(vectorCopy);
    double[] finalVector = model.paramsToVector();
    assertArrayEquals(original, finalVector, 1e-10);
  }

  @Test(expected = RuntimeException.class)
  public void testReadWordVectorsThrowsWhenUnkWordMissing() {
    RNNOptions options = new RNNOptions();
    options.wordVectors = "data/null-vectors.txt";
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.numClasses = 2;
    options.randomWordVectors = false;
    options.unkWord = "UNKNOWN_NOT_PRESENT";
    SentimentModel model = new SentimentModel(options, new ArrayList<Tree>());
    model.readWordVectors();
  }

  @Test
  public void testToStringBinaryTensorOutputFormat() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.useTensors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree t = new LabeledScoredTreeFactory().newLeaf("Y");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(t));
    String out = model.toString();
    assertNotNull(out);
    assertTrue(out.contains("Binary transform tensor") || out.contains("Binary transform tensors"));
  }

  @Test(expected = RuntimeException.class)
  public void testInitRandomWordVectorsThrowsWhenNumHidIsZeroExplicitly() {
    RNNOptions options = new RNNOptions();
    options.numHid = 0;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    SentimentModel model = new SentimentModel(options, new ArrayList<Tree>());
    List<Tree> list = Collections.singletonList(Tree.valueOf("(X Y)"));
    model.initRandomWordVectors(list);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedBinaryProductionException() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.simplifiedModel = false;
    options.combineClassification = true;
    Tree tree = Tree.valueOf("(S (NP they) (VP sleep))");
    List<Tree> list = Collections.singletonList(tree);
    new SentimentModel(options, list);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedUnaryProductionException() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.simplifiedModel = false;
    options.combineClassification = true;
    Tree tree = Tree.valueOf("(D @DT)");
    List<Tree> list = Collections.singletonList(tree);
    new SentimentModel(options, list);
  }

  @Test(expected = RuntimeException.class)
  public void testSaveSerializedThrowsIfPathIsInvalid() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("w");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    String invalidFilePath =
        File.separator
            + "this"
            + File.separator
            + "should"
            + File.separator
            + "fail"
            + File.separator
            + "sent.model";
    model.saveSerialized(invalidFilePath);
  }

  @Test
  public void testBasicCategoryBehaviorWithEmptyString() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.simplifiedModel = false;
    options.combineClassification = true;
    // options.langpack = new RNNLangpackStub();
    SentimentModel model = new SentimentModel(options, new ArrayList<Tree>());
    String basic = model.basicCategory("");
    assertEquals("", basic);
  }

  @Test
  public void testGetUnaryClassificationReturnsNullForMissingCategoryEvenIfSimplified() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 4;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("X");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleMatrix result = model.getUnaryClassification("nonexistent-label");
    assertNull(result);
  }

  @Test
  public void testGetBinaryClassificationReturnsCorrectMatrixInUntiedMode() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 3;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = false;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("A");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleMatrix matrix = model.getBinaryClassification("AnyLeft", "AnyRight");
    assertNotNull(matrix);
    assertEquals(2, matrix.numRows());
    assertEquals(4, matrix.numCols());
  }

  @Test
  public void testToStringEmptyModelReturnsValidOutput() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("W");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    String result = model.toString();
    assertNotNull(result);
    assertTrue(result.contains("Word vectors"));
  }

  @Test
  public void testGetBinaryTransformFallsBackOnSimplifiedEmptyKeys() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("word");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleMatrix matrix = model.getBinaryTransform("NP", "VP");
    assertNotNull(matrix);
    assertEquals(2, matrix.numRows());
  }

  @Test
  public void testGetBinaryTensorReturnsCorrectTensorForSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.useTensors = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("D");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleTensor tensor = model.getBinaryTensor("NP", "VP");
    assertNotNull(tensor);
    assertEquals(16, tensor.getNumElements());
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeWithEmptyChildrenThrowsAssertionError() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree node = Tree.valueOf("(ROOT)");
    SentimentModel model =
        new SentimentModel(options, Collections.singletonList(Tree.valueOf("x")));
    model.getClassWForNode(node);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNodeWithUnaryChildrenThrowsAssertionError() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 2;
    options.useTensors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree child = Tree.valueOf("X");
    Tree unaryNode = Tree.valueOf("(X Y)");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(child));
    model.getTensorForNode(unaryNode);
  }

  @Test
  public void testVectorToParamsDoesNotThrowWhenExactSizeVectorGiven() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("T");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    double[] vector = model.paramsToVector();
    model.vectorToParams(vector);
    assertNotNull(vector);
  }

  @Test
  public void testTotalParamSizeGreaterThanZeroForTinyModel() {
    RNNOptions options = new RNNOptions();
    options.numHid = 1;
    options.numClasses = 1;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("Z");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    int size = model.totalParamSize();
    assertTrue(size > 0);
  }

  @Test
  public void testGetBinaryClassificationReturnsNullWhenKeyMissingAndUntied() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 3;
    options.combineClassification = false;
    options.simplifiedModel = false;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    // RNNLangpackStub langpack = new RNNLangpackStub();
    // options.langpack = langpack;
    SentimentModel model =
        new SentimentModel(options, Collections.singletonList(Tree.valueOf("x")));
    SimpleMatrix result = model.getBinaryClassification("Non", "existent");
    assertNull(result);
  }

  @Test
  public void testGetUnaryClassificationReturnsCorrectMatrixWhenPresentUntiedMode() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 2;
    options.combineClassification = true;
    options.simplifiedModel = false;
    // options.langpack = new RNNLangpackStub();
    SimpleMatrix mat = new SimpleMatrix(2, 3);
    Map<String, SimpleMatrix> unaryMap = new HashMap<>();
    unaryMap.put("NN", mat);
    SentimentModel model =
        new SentimentModel(
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            unaryMap,
            new HashMap<String, SimpleMatrix>(),
            options);
    SimpleMatrix result = model.getUnaryClassification("@NN");
    assertSame(mat, result);
  }

  @Test
  public void testGetVocabWordReturnsKnownEvenIfMixedCaseWhenLowercasingEnabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.lowercaseWordVectors = true;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("Dog");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    String vocab = model.getVocabWord("dOG");
    assertEquals("dog", vocab);
  }

  @Test
  public void testUnknownWordVectorExistsInWordVectorsAfterLoad() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.lowercaseWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("cat");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    boolean contains = model.wordVectors.containsKey(SentimentModel.UNKNOWN_WORD);
    assertTrue(contains);
    assertNotNull(model.wordVectors.get(SentimentModel.UNKNOWN_WORD));
  }

  @Test
  public void testIdentityMatrixIsCorrectDimension() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.lowercaseWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("dog");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    SimpleMatrix identity = model.identity;
    assertNotNull(identity);
    assertEquals(3, identity.numCols());
    assertEquals(3, identity.numRows());
  }

  @Test
  public void testBinaryTransformSizesMatchExpectedShape() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.randomWordVectors = true;
    options.useTensors = false;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("word");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleMatrix W = model.binaryTransform.get("", "");
    assertNotNull(W);
    assertEquals(2, W.numRows());
    assertEquals(5, W.numCols());
  }

  @Test
  public void testRandomTransformBlockIncludesIdentityComponent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 0.5;
    Tree tree = Tree.valueOf("Z");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleMatrix block = model.randomTransformBlock();
    assertNotNull(block);
    assertEquals(2, block.numRows());
    assertEquals(2, block.numCols());
    boolean hasDiagonal = Math.abs(block.get(0, 0)) > 0 && Math.abs(block.get(1, 1)) > 0;
    assertTrue(hasDiagonal);
  }

  @Test
  public void testParamsToVectorIncludesAllExpectedParameterCounts() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("A");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    int expectedSize = model.totalParamSize();
    double[] vector = model.paramsToVector();
    assertEquals(expectedSize, vector.length);
  }

  @Test
  public void testOverriddenToStringIncludesMatrixData() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.randomWordVectors = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree t = new LabeledScoredTreeFactory().newLeaf("s");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(t));
    String out = model.toString();
    assertTrue(out.contains("Word vectors"));
    assertTrue(
        out.contains("Binary transform matrix") || out.contains("Binary transform matrices"));
    assertFalse(out.contains("null"));
  }

  @Test
  public void testInitializationOfBinaryTensorRespectsShapeScaling() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 2.0;
    Tree t = new LabeledScoredTreeFactory().newLeaf("t");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(t));
    SimpleTensor tensor = model.binaryTensors.get("", "");
    assertNotNull(tensor);
    // assertEquals(4, tensor.getNumSlices());
    // assertEquals(4, tensor.getNumRows());
    // assertEquals(2, tensor.getNumCols());
  }

  @Test
  public void testClassificationMatrixOutputMatchesExpectedShape() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 4;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("A");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleMatrix result = model.getUnaryClassification("");
    assertNotNull(result);
    assertEquals(4, result.numRows());
    assertEquals(4, result.numCols());
  }

  @Test
  public void testBinaryClassificationReturnsNullIfKeyMissingWhenNotSimplified() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 4;
    options.simplifiedModel = false;
    options.combineClassification = false;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    // options.langpack = new edu.stanford.nlp.trees.EnglishTreebankLangPack();
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("run");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(leaf));
    SimpleMatrix matrix = model.getBinaryClassification("ABC", "XYZ");
    assertNull(matrix);
  }

  @Test
  public void testBasicCategoryHandlesTrimmedBareStrings() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    // options.langpack = new edu.stanford.nlp.trees.EnglishTreebankLangPack();
    SentimentModel model = new SentimentModel(options, new ArrayList<Tree>());
    String basic = model.basicCategory("NP-TMP");
    assertEquals("NP", basic);
  }

  @Test
  public void testParamsToVectorIncludesZeroWordVectors() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 1;
    options.numHid = 2;
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("a");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    model.wordVectors.clear();
    double[] vector = model.paramsToVector();
    assertTrue(vector.length > 0);
  }

  @Test
  public void testVectorToParamsWithExactZeroParamsSucceeds() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 1;
    options.numHid = 1;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = Tree.valueOf("A");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    double[] v = model.paramsToVector();
    model.vectorToParams(v);
  }

  @Test
  public void testUnknownWordVectorIsAccessibleFromWordVectorMap() {
    RNNOptions options = new RNNOptions();
    options.randomWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.numClasses = 1;
    options.numHid = 2;
    options.lowercaseWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree tree = new LabeledScoredTreeFactory().newLeaf("Walk");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    String unk = SentimentModel.UNKNOWN_WORD;
    assertTrue(model.wordVectors.containsKey(unk));
    assertNotNull(model.wordVectors.get(unk));
  }

  @Test
  public void
      testGetUnaryClassificationReturnsNullIfNotMatchEvenAfterBasicCategorySimplifiedFalse() {
    RNNOptions options = new RNNOptions();
    options.combineClassification = true;
    options.simplifiedModel = false;
    options.numHid = 2;
    options.numClasses = 3;
    // options.langpack = new edu.stanford.nlp.trees.EnglishTreebankLangPack();
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("NN", new SimpleMatrix(3, 3));
    SentimentModel model =
        new SentimentModel(
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            unary,
            new HashMap<String, SimpleMatrix>(),
            options);
    SimpleMatrix result = model.getUnaryClassification("XYZ");
    assertNull(result);
  }

  @Test
  public void testGetClassWForNodeReturnsBinaryMatrixWhereExpected() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    Tree left = new LabeledScoredTreeFactory().newLeaf("A");
    Tree right = new LabeledScoredTreeFactory().newLeaf("B");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("ROOT", Arrays.asList(left, right));
    SentimentModel model = new SentimentModel(options, Collections.singletonList(left));
    SimpleMatrix mat = model.getClassWForNode(parent);
    assertNotNull(mat);
  }

  @Test
  public void testUnknownCategoryHandledGracefullyByBasicCategory() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    // options.langpack = new edu.stanford.nlp.trees.EnglishTreebankLangPack();
    SentimentModel model = new SentimentModel(options, new ArrayList<Tree>());
    String basic = model.basicCategory(null);
    assertNotNull(basic);
  }

  @Test
  public void testClassifierParametersAreScaledProperly() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 2;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 10.0;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    Tree tree = Tree.valueOf("B");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(tree));
    SimpleMatrix output = model.getClassWForNode(Tree.valueOf("(X A B)"));
    double max = output.elementMaxAbs();
    assertTrue(max >= 0.1);
  }

  @Test(expected = NullPointerException.class)
  public void testGetWordVectorThrowsIfWordVectorsNull() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SentimentModel model =
        new SentimentModel(
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            edu.stanford.nlp.util.TwoDimensionalMap.treeMap(),
            new HashMap<String, SimpleMatrix>(),
            null,
            options);
    model.getWordVector("word");
  }
}
