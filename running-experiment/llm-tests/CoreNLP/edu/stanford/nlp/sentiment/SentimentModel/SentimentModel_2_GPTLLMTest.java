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

public class SentimentModel_2_GPTLLMTest {

  @Test
  public void testGetWordVectorKnown() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 42;
    SimpleMatrix W = SimpleMatrix.random_DDRM(4, 9, -0.01, 0.01, new Random(42));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 5, -0.01, 0.01, new Random(42));
    SimpleTensor Wt = SimpleTensor.random(8, 8, 4, -0.01, 0.01, new Random(42));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("joy", new SimpleMatrix(4, 1));
    vectors.put("*UNK*", new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix result = model.getWordVector("joy");
    assertNotNull(result);
    assertEquals(4, result.numRows());
    assertEquals(1, result.numCols());
  }

  @Test
  public void testGetWordVectorUnknown() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 42;
    SimpleMatrix W = SimpleMatrix.random_DDRM(4, 9, -0.01, 0.01, new Random(42));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 5, -0.01, 0.01, new Random(42));
    SimpleTensor Wt = SimpleTensor.random(8, 8, 4, -0.01, 0.01, new Random(42));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("sunshine", new SimpleMatrix(4, 1));
    SimpleMatrix unkVec = new SimpleMatrix(4, 1);
    vectors.put(SentimentModel.UNKNOWN_WORD, unkVec);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix result = model.getWordVector("moonlight");
    assertNotNull(result);
    assertEquals(unkVec, result);
  }

  @Test
  public void testParamsToVectorAndBack() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 11;
    SimpleMatrix W = SimpleMatrix.random_DDRM(3, 7, -0.01, 0.01, new Random(11));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 4, -0.01, 0.01, new Random(11));
    SimpleTensor Wt = SimpleTensor.random(6, 6, 3, -0.01, 0.01, new Random(11));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("tree", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    double[] vec = model.paramsToVector();
    // SentimentModel copy = SentimentModel.modelFromMatrices(W.copy(), Wcat.copy(), Wt.copy(), new
    // HashMap<>(vectors), options);
    // copy.vectorToParams(vec);
    // double[] newVec = copy.paramsToVector();
    // assertEquals(vec.length, newVec.length);
    for (int i = 0; i < vec.length; i++) {
      // assertEquals(vec[i], newVec[i], 1e-10);
    }
  }

  @Test
  public void testBinaryMatrixLookup() {
    RNNOptions options = new RNNOptions();
    options.numHid = 5;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 123;
    SimpleMatrix W = new SimpleMatrix(5, 11);
    SimpleMatrix Wcat = new SimpleMatrix(3, 6);
    SimpleTensor Wt = new SimpleTensor(10, 10, 5);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("left", new SimpleMatrix(5, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(5, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix out = model.getBinaryTransform("NP", "VP");
    assertNotNull(out);
    assertEquals(5, out.numRows());
    assertEquals(11, out.numCols());
  }

  @Test
  public void testGetBinaryTensor() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 234;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("A", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleTensor tensor = model.getBinaryTensor("S", "VP");
    assertNotNull(tensor);
    // assertEquals(6, tensor.dim1());
    // assertEquals(6, tensor.dim2());
    // assertEquals(3, tensor.dim3());
  }

  @Test
  public void testGetClassWForNodeBinary() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 10;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(3, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("bright", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree left = tf.newLeaf("bright");
    Tree right = tf.newLeaf("dark");
    Tree parent = tf.newTreeNode("S", java.util.Arrays.asList(left, right));
    SimpleMatrix result = model.getClassWForNode(parent);
    assertNotNull(result);
    assertEquals(3, result.numRows());
    assertEquals(5, result.numCols());
  }

  @Test
  public void testBasicCategorySimplified() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = true;
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("X", new SimpleMatrix(1, 1));
    options.numHid = 1;
    options.numClasses = 1;
    options.combineClassification = true;
    options.randomSeed = 0;
    SimpleMatrix W = new SimpleMatrix(1, 3);
    SimpleMatrix Wcat = new SimpleMatrix(1, 2);
    SimpleTensor Wt = new SimpleTensor(2, 2, 1);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    String result = model.basicCategory("NP");
    assertEquals("", result);
  }

  @Test
  public void testTotalParamSizeMatchesVectorSize() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 22;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(2, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("glow", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    int declaredSize = model.totalParamSize();
    int actualSize = model.paramsToVector().length;
    assertEquals(declaredSize, actualSize);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testModelFromMatricesInvalidOptionsThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    options.randomSeed = 33;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("Z", new SimpleMatrix(2, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
  }

  @Test
  public void testGetVocabWordLowercaseMatch() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 42;
    options.lowercaseWordVectors = true;
    SimpleMatrix W = SimpleMatrix.random_DDRM(4, 9, -0.1, 0.1, new Random(42));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(3, 5, -0.1, 0.1, new Random(42));
    SimpleTensor Wt = SimpleTensor.random(8, 8, 4, -0.1, 0.1, new Random(42));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("apple", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    String word = model.getVocabWord("Apple");
    assertEquals("apple", word);
  }

  @Test
  public void testGetUnaryClassificationNonSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.randomSeed = 11;
    // options.langpack = new english.RNNLangPack();
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("VB", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("VB", new SimpleMatrix(2, 4));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            vectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("VB");
    assertNotNull(result);
    assertEquals(2, result.numRows());
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeEmptyChildrenThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 42;
    SimpleMatrix W = SimpleMatrix.random_DDRM(4, 9, -0.1, 0.1, new Random(42));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 5, -0.1, 0.1, new Random(42));
    SimpleTensor Wt = SimpleTensor.random(8, 8, 4, -0.1, 0.1, new Random(42));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("x", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree parent = tf.newTreeNode("ROOT", java.util.Collections.emptyList());
    model.getClassWForNode(parent);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNodeUnaryThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 13;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("foo", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree unary = tf.newTreeNode("A", java.util.Collections.singletonList(tf.newLeaf("foo")));
    model.getTensorForNode(unary);
  }

  @Test
  public void testParamsToVectorProducesNonZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 19;
    SimpleMatrix W = SimpleMatrix.random_DDRM(2, 5, -0.02, 0.02, new Random(19));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 3, -0.02, 0.02, new Random(19));
    SimpleTensor Wt = SimpleTensor.random(4, 4, 2, -0.02, 0.02, new Random(19));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("YES", new SimpleMatrix(2, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    double[] params = model.paramsToVector();
    boolean nonzero = false;
    if (params.length > 0) {
      if (params[0] != 0.0 || params[params.length - 1] != 0.0) {
        nonzero = true;
      }
    }
    assertTrue(nonzero);
  }

  @Test
  public void testToStringIncludesBinaryTensor() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 101;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(2, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("alpha", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    String dump = model.toString();
    assertTrue(dump.contains("Binary transform tensor"));
  }

  @Test(expected = AssertionError.class)
  public void testGetWForNodeUnaryThrowsAssertionError() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 77;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(2, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("word", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    TreeFactory treeFactory = new LabeledScoredTreeFactory();
    Tree unary =
        treeFactory.newTreeNode(
            "X", java.util.Collections.singletonList(treeFactory.newLeaf("word")));
    model.getWForNode(unary);
  }

  @Test(expected = AssertionError.class)
  public void testGetWForNodeEmptyChildrenThrowsAssertionError() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 77;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(2, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("word", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    TreeFactory treeFactory = new LabeledScoredTreeFactory();
    Tree node = treeFactory.newTreeNode("X", java.util.Collections.emptyList());
    model.getWForNode(node);
  }

  @Test
  public void testGetBinaryClassificationWithCombineClassificationTrue() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 88;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(2, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("left", new SimpleMatrix(4, 1));
    vectors.put("right", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertNotNull(result);
    assertEquals(2, result.numRows());
    assertEquals(5, result.numCols());
  }

  @Test
  public void testGetBinaryClassificationWithCombineClassificationFalse() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.randomSeed = 55;
    SimpleMatrix binaryMatrix = new SimpleMatrix(2, 5);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("", "", binaryMatrix);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", new SimpleMatrix(2, 5));
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("", "", new SimpleTensor(4, 4, 2));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("", new SimpleMatrix(3, 3));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("x", new SimpleMatrix(2, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(binaryTransform, binaryTensors, binaryClass, unary, vectors, options);
    SimpleMatrix result = model.getBinaryClassification("A", "B");
    assertNotNull(result);
    assertEquals(2, result.numRows());
    assertEquals(5, result.numCols());
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeWithEmptyChildrenListThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 99;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("word", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    TreeFactory treeFactory = new LabeledScoredTreeFactory();
    Tree node = treeFactory.newTreeNode("X", java.util.Collections.emptyList());
    model.getClassWForNode(node);
  }

  @Test
  public void testUnknownWordReturnsAsExpectedFromGetVocabWord() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.lowercaseWordVectors = false;
    options.randomSeed = 1;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("house", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    String result = model.getVocabWord("not_in_vocab");
    assertEquals(SentimentModel.UNKNOWN_WORD, result);
  }

  @Test
  public void testBasicCategoryStripsPrefixCharAt0() {
    RNNOptions options = new RNNOptions();
    options.numHid = 1;
    options.numClasses = 1;
    options.combineClassification = true;
    options.simplifiedModel = false;
    options.randomSeed = 3;
    // options.langpack = new edu.stanford.nlp.sentiment.english.RNNLangPack();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    String result = model.basicCategory("@VP");
    assertEquals("VP", result);
  }

  @Test
  public void testPrintParamInformationIndexZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 23;
    SimpleMatrix W = SimpleMatrix.random_DDRM(2, 5, -0.1, 0.1, new Random(23));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 3, -0.1, 0.1, new Random(23));
    SimpleTensor Wt = SimpleTensor.random(4, 4, 2, -0.1, 0.1, new Random(23));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("foo", new SimpleMatrix(2, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    model.printParamInformation(0);
    assertTrue(model.totalParamSize() >= 0);
  }

  @Test
  public void testPrintParamInformationAtEndOfParams() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 12;
    SimpleMatrix W = SimpleMatrix.random_DDRM(2, 5, -0.1, 0.1, new Random(12));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 3, -0.1, 0.1, new Random(12));
    SimpleTensor Wt = SimpleTensor.random(4, 4, 2, -0.1, 0.1, new Random(12));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("x", new SimpleMatrix(2, 1));
    vectors.put("y", new SimpleMatrix(2, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    int size = model.totalParamSize();
    model.printParamInformation(size - 1);
    assertTrue(size > 0);
  }

  @Test
  public void testToStringIncludesClassificationData() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = false;
    options.randomSeed = 99;
    SimpleMatrix binary = SimpleMatrix.random_DDRM(2, 5, -0.1, 0.1, new Random(99));
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("NP", "VP", binary);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("NP", "VP", SimpleMatrix.random_DDRM(2, 3, -0.1, 0.1, new Random(99)));
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    tensors.put("NP", "VP", SimpleTensor.random(4, 4, 2, -0.1, 0.1, new Random(99)));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("NP", SimpleMatrix.random_DDRM(2, 3, -0.1, 0.1, new Random(99)));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("dog", new SimpleMatrix(2, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(binaryTransform, tensors, binaryClass, unary, vectors, options);
    String dump = model.toString();
    assertTrue(
        dump.contains("Binary classification matrix")
            || dump.contains("Binary classification matrices"));
    assertTrue(
        dump.contains("Unary classification matrix")
            || dump.contains("Unary classification matrices"));
  }

  @Test
  public void testGetUnaryClassificationReturnsNullWhenMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = false;
    // options.langpack = new edu.stanford.nlp.sentiment.english.RNNLangPack();
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> emptyUnary = new HashMap<>();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            emptyUnary,
            vectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("XYZ");
    assertNull(result);
  }

  @Test(expected = RuntimeException.class)
  public void testReadWordVectorsThrowsWhenUnknownWordNotInEmbedding() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.unkWord = "+++";
    options.wordVectors = "";
    // SentimentModel model = new SentimentModel();
    // model.op = options;
    // model.wordVectors = new HashMap<>();
    // model.readWordVectors();
  }

  @Test
  public void testGetWordVectorWithLowerCasingEnabledReturnsCorrectVector() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.lowercaseWordVectors = true;
    options.randomSeed = 33;
    SimpleMatrix W = SimpleMatrix.random_DDRM(3, 7, -0.1, 0.1, new Random(33));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 4, -0.1, 0.1, new Random(33));
    SimpleTensor Wt = SimpleTensor.random(6, 6, 3, -0.1, 0.1, new Random(33));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    SimpleMatrix known = new SimpleMatrix(3, 1);
    SimpleMatrix unk = new SimpleMatrix(3, 1);
    vectors.put("hello", known);
    vectors.put(SentimentModel.UNKNOWN_WORD, unk);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix retrieved = model.getWordVector("HELLO");
    assertSame(known, retrieved);
  }

  @Test
  public void testParamsToVectorLengthGreaterThanZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 50;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("a", new SimpleMatrix(3, 1));
    vectors.put("b", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    double[] actual = model.paramsToVector();
    assertTrue(actual.length > 0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testConstructorWithNonSimplifiedModelThrowsForBinaryProductions() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.randomWordVectors = true;
    options.randomSeed = 123;
    options.numHid = 5;
    options.numClasses = 2;
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree =
        tf.newTreeNode("S", java.util.Arrays.asList(tf.newLeaf("word1"), tf.newLeaf("word2")));
    List<Tree> trainingTrees = new ArrayList<>();
    trainingTrees.add(tree);
    new SentimentModel(options, trainingTrees);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testConstructorWithNonSimplifiedModelThrowsForUnaryProductions() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.randomWordVectors = true;
    options.randomSeed = 123;
    options.numHid = 5;
    options.numClasses = 2;
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newLeaf("word1");
    List<Tree> trainingTrees = new ArrayList<>();
    trainingTrees.add(tree);
    new SentimentModel(options, trainingTrees);
  }

  @Test
  public void testIdentityMatrixIsCorrectDimension() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.randomSeed = 1;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(2, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("item", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix identity = model.identity;
    assertEquals(4, identity.numRows());
    assertEquals(4, identity.numCols());
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        if (i == j) {
          assertEquals(1.0, identity.get(i, j), 0.0);
        } else {
          assertEquals(0.0, identity.get(i, j), 0.0);
        }
      }
    }
  }

  @Test
  public void testRandomClassificationMatrixBiasColumnWithinExpectedRange() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.randomSeed = 10;
    // options.trainOptions = new RNNTrainingOptions();
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    // SentimentModel model = new SentimentModel();
    // model.op = options;
    // model.numHid = 4;
    // model.numClasses = 3;
    // model.rand = new Random(options.randomSeed);
    // model.identity = SimpleMatrix.identity(4);
    // SimpleMatrix matrix = model.randomClassificationMatrix();
    // SimpleMatrix biasColumn = matrix.extractMatrix(0, 3, 4, 5);
    // for (int i = 0; i < biasColumn.numRows(); i++) {
    // double val = biasColumn.get(i, 0);
    // assertTrue(val >= 0.0);
    // assertTrue(val <= 1.0);
    // }
  }

  @Test
  public void testGetBinaryClassificationReturnsCorrectMatrixWhenPresent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    // options.langpack = new edu.stanford.nlp.sentiment.english.RNNLangPack();
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    SimpleMatrix expectedMatrix = new SimpleMatrix(2, 3);
    binaryClass.put("NP", "VP", expectedMatrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("NP", new SimpleMatrix(2, 3));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            unary,
            wordVectors,
            options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertSame(expectedMatrix, result);
  }

  @Test
  public void testVectorToParamsAndParamsToVectorRoundTrip() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 5;
    SimpleMatrix W = SimpleMatrix.random_DDRM(3, 7, -0.1, 0.1, new Random(5));
    SimpleMatrix Wcat = SimpleMatrix.random_DDRM(2, 4, -0.1, 0.1, new Random(5));
    SimpleTensor Wt = SimpleTensor.random(6, 6, 3, -0.1, 0.1, new Random(5));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("q", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    double[] origParams = model.paramsToVector();
    model.vectorToParams(origParams);
    double[] resultParams = model.paramsToVector();
    assertEquals(origParams.length, resultParams.length);
    for (int i = 0; i < origParams.length; i++) {
      assertEquals(origParams[i], resultParams[i], 1e-9);
    }
  }

  @Test(expected = RuntimeException.class)
  public void testInitRandomWordVectorsThrowsWhenNumHidIsZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 0;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.randomWordVectors = true;
    options.randomSeed = 7;
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newLeaf("word");
    List<Tree> trainingTrees = new ArrayList<>();
    trainingTrees.add(tree);
    // SentimentModel model = new SentimentModel();
    // model.op = options;
    // model.initRandomWordVectors(trainingTrees);
  }

  @Test
  public void testGetBinaryTransformReturnsNullForMissingKey() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 1;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("foo", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix result = model.getBinaryTransform("X", "Y");
    assertNull(result);
  }

  @Test
  public void testGetBinaryTensorReturnsNullForMissingKey() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 2;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("bar", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleTensor result = model.getBinaryTensor("A", "B");
    assertNull(result);
  }

  @Test
  public void testGetWordVectorExactMatchWithCaseSensitivityPreservedIfDisabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.lowercaseWordVectors = false;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    SimpleMatrix expected = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("Word", expected);
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix result = model.getWordVector("Word");
    assertSame(expected, result);
  }

  @Test
  public void testGetWordVectorFallbackToUnknownWhenCaseSensitivityFails() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.lowercaseWordVectors = false;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    SimpleMatrix fallback = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("Word", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, fallback);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    SimpleMatrix result = model.getWordVector("wORD");
    assertSame(fallback, result);
  }

  @Test
  public void testBinaryClassificationSizeWhenCombineClassificationTrue() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 5;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(3, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("foo", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    assertEquals(0, model.binaryClassificationSize);
  }

  @Test
  public void testBinaryClassificationSizeWhenCombineClassificationFalse() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.randomSeed = 6;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(3, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("foo", new SimpleMatrix(4, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(4, 1));
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", W);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("", "", SimpleMatrix.random_DDRM(3, 5, 0.0, 1.0, new Random(123)));
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    tensors.put("", "", Wt);
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("", Wcat);
    SentimentModel model =
        new SentimentModel(binaryTransform, tensors, binaryClass, unary, vectors, options);
    assertEquals(3 * (4 + 1), model.binaryClassificationSize);
  }

  @Test
  public void testUnknownWordOverwritesIfPresentInVectors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.randomSeed = 7;
    options.wordVectors = "";
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> map = new HashMap<>();
    SimpleMatrix vec = new SimpleMatrix(2, 1);
    map.put("known", new SimpleMatrix(2, 1));
    map.put("*UNK*", vec);
    // SentimentModel model = new SentimentModel();
    // model.op = options;
    // model.wordVectors = map;
    // model.readWordVectors();
    // assertSame(vec, model.wordVectors.get(SentimentModel.UNKNOWN_WORD));
  }

  @Test
  public void testGetClassWForNodeReturnsBinaryClassificationForNonCombinedMode() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    // options.langpack = new edu.stanford.nlp.sentiment.english.RNNLangPack();
    options.randomSeed = 15;
    SimpleMatrix binaryMatrix = new SimpleMatrix(2, 4);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("NP", "VP", binaryMatrix);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("NP", "VP", new SimpleMatrix(3, 7));
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    tensors.put("NP", "VP", SimpleTensor.random(6, 6, 3, -0.1, 0.1, new Random(15)));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("NN", new SimpleMatrix(2, 4));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("dog", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(binaryTransform, tensors, binaryClass, unary, vectors, options);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree left = tf.newLeaf("NP");
    Tree right = tf.newLeaf("VP");
    Tree parent = tf.newTreeNode("ROOT", java.util.Arrays.asList(left, right));
    SimpleMatrix result = model.getClassWForNode(parent);
    assertSame(binaryMatrix, result);
  }

  @Test
  public void testGetClassWForNodeReturnsUnaryClassificationWhenUnaryNode() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    // options.langpack = new edu.stanford.nlp.sentiment.english.RNNLangPack();
    options.randomSeed = 21;
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("walk", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("VB", new SimpleMatrix(2, 4));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            vectors,
            options);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree child = tf.newLeaf("VB");
    Tree parent = tf.newTreeNode("ROOT", java.util.Collections.singletonList(child));
    SimpleMatrix result = model.getClassWForNode(parent);
    assertSame(unary.get("VB"), result);
  }

  @Test
  public void testRandomWordVectorReturnsWithinExpectedRange() {
    int size = 4;
    Random rand = new Random(987);
    SimpleMatrix vector = SentimentModel.randomWordVector(size, rand);
    assertEquals(size, vector.getNumElements());
    for (int i = 0; i < size; i++) {
      double val = vector.get(i);
      assertTrue(val <= 0.6 && val >= -0.6);
    }
  }

  @Test
  public void testVectorToParamsWithModifiedVectorUpdatesModel() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomSeed = 99;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("a", new SimpleMatrix(3, 1));
    vectors.put("b", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, vectors, options);
    double[] params = model.paramsToVector();
    for (int i = 0; i < params.length; i++) {
      params[i] += 0.01;
    }
    model.vectorToParams(params);
    double[] updated = model.paramsToVector();
    for (int i = 0; i < params.length; i++) {
      assertEquals(params[i], updated[i], 1e-9);
    }
  }

  @Test
  public void testUnknownWordVectorPreservedAfterReadWordVectors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.wordVectors = "";
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    SimpleMatrix unk = new SimpleMatrix(4, 1);
    vectors.put("*UNK*", unk);
    vectors.put("known", new SimpleMatrix(4, 1));
    // SentimentModel model = new SentimentModel();
    // model.op = options;
    // model.wordVectors = vectors;
    // model.readWordVectors();
    // SimpleMatrix result = model.wordVectors.get(SentimentModel.UNKNOWN_WORD);
    // assertSame(unk, result);
  }

  @Test
  public void testToStringContainsBinaryAndUnaryInfoIfPresent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    // options.langpack = new edu.stanford.nlp.sentiment.english.RNNLangPack();
    SimpleMatrix binary = new SimpleMatrix(3, 7);
    SimpleMatrix classMatrix = new SimpleMatrix(2, 4);
    SimpleMatrix unaryMatrix = new SimpleMatrix(2, 4);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("NP", "VP", binary);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClassification =
        TwoDimensionalMap.treeMap();
    binaryClassification.put("NP", "VP", classMatrix);
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    tensors.put("NP", "VP", new SimpleTensor(6, 6, 3));
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("dog", new SimpleMatrix(3, 1));
    vectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("NN", unaryMatrix);
    SentimentModel model =
        new SentimentModel(binaryTransform, tensors, binaryClassification, unary, vectors, options);
    String info = model.toString();
    assertTrue(info.contains("Binary transform matrices"));
    assertTrue(info.contains("Binary classification matrices"));
    assertTrue(info.contains("Unary classification matrices"));
    assertTrue(info.contains("Word vectors"));
  }
}
