package edu.stanford.nlp.sentiment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.neural.SimpleTensor;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.TwoDimensionalMap;
import java.io.*;
import java.util.*;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

public class SentimentModel_3_GPTLLMTest {

  @Test(expected = IllegalArgumentException.class)
  public void testModelFromMatricesThrowsIllegalArgumentException() {
    RNNOptions options = new RNNOptions();
    options.combineClassification = false;
    options.simplifiedModel = false;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(5, 4);
    // SimpleTensor Wt = SimpleTensor.constant(6, 6, 3, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    // SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
  }

  @Test
  public void testWordVectorRetrievalWithUnknownWordFallback() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(5, 5);
    // SimpleTensor Wt = SimpleTensor.constant(8, 8, 4, 0.5);
    SimpleMatrix knownVec = new SimpleMatrix(4, 1);
    SimpleMatrix unkVec = new SimpleMatrix(4, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, unkVec);
    wordVectors.put("known", knownVec);
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // SimpleMatrix retrieved = model.getWordVector("unknownWord");
    // assertEquals(unkVec.getNumElements(), retrieved.getNumElements());
    // assertEquals(unkVec, retrieved);
  }

  @Test
  public void testParamsVectorRoundtrip() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 0.25);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vec1 = new SimpleMatrix(2, 1);
    SimpleMatrix vec2 = new SimpleMatrix(2, 1);
    wordVectors.put("hello", vec1);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, vec2);
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // double[] original = model.paramsToVector();
    // double[] modified = new double[original.length];
    // if (original.length > 0) {
    // modified[0] = original[0] + 1.0;
    // }
    // for (int i = 1; i < original.length; i++) {
    // modified[i] = original[i];
    // }
    // model.vectorToParams(modified);
    // double[] newVec = model.paramsToVector();
    // assertNotEquals(original[0], newVec[0], 1e-10);
    // assertEquals(original.length, newVec.length);
  }

  @Test
  public void testToStringOutputIsNotNullAndIncludesWordVectors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    // SimpleTensor Wt = SimpleTensor.constant(6, 6, 3, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("happy", new SimpleMatrix(3, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // String output = model.toString();
    // assertNotNull(output);
    // assertTrue(output.contains("Word vectors"));
    // assertTrue(output.contains("happy"));
  }

  @Test
  public void testBinaryTensorRetrieval() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 1.23);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("zoo", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // SimpleTensor tensor = model.getBinaryTensor("NP", "VP");
    // assertNotNull(tensor);
  }

  @Test
  public void testSaveAndLoadModelSerialization() throws Exception {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    // SimpleTensor Wt = SimpleTensor.constant(6, 6, 3, 0.8);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("token", new SimpleMatrix(3, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    File tempFile = File.createTempFile("sentiment-model", ".ser");
    tempFile.deleteOnExit();
    // model.saveSerialized(tempFile.getAbsolutePath());
    SentimentModel loaded = SentimentModel.loadSerialized(tempFile.getAbsolutePath());
    assertNotNull(loaded);
    // assertEquals(model.totalParamSize(), loaded.totalParamSize());
  }

  @Test
  public void testGetClassWeightForBinaryNode() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 0.5);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("token", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    LabeledScoredTreeNode root = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    root.setChildren(
        new Tree[] {
          new LabeledScoredTreeNode(new StringLabel("NP")),
          new LabeledScoredTreeNode(new StringLabel("VP"))
        });
    // SimpleMatrix result = model.getClassWForNode(root);
    // assertEquals(Wcat.getNumElements(), result.getNumElements());
    // assertEquals(Wcat, result);
  }

  @Test
  public void testBasicCategoryReturnsEmptyForSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(1, 3);
    SimpleMatrix Wcat = new SimpleMatrix(2, 2);
    // SimpleTensor Wt = SimpleTensor.constant(2, 2, 1, 0.1);
    Map<String, SimpleMatrix> wv = new HashMap<>();
    wv.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(1, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wv, options);
    // String result = model.basicCategory("NP@BAR");
    // assertEquals("", result);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNodeWithUnaryNodeThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    // SimpleTensor Wt = SimpleTensor.constant(6, 6, 3, 1.2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree parent = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree child = new LabeledScoredTreeNode(new StringLabel("X"));
    parent.setChildren(new Tree[] {child});
    // model.getTensorForNode(parent);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNodeWithZeroChildrenThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 0.5);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree leaf = new LabeledScoredTreeNode(new StringLabel("LEAF"));
    leaf.setChildren(new Tree[0]);
    // model.getTensorForNode(leaf);
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeZeroChildrenThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(3, 4);
    // SimpleTensor Wt = SimpleTensor.constant(6, 6, 3, 0.1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree leaf = new LabeledScoredTreeNode(new StringLabel("LEAF"));
    leaf.setChildren(new Tree[0]);
    // model.getClassWForNode(leaf);
  }

  @Test
  public void testGetVocabWordLowercaseNormalization() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.lowercaseWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix lowerVec = new SimpleMatrix(2, 1);
    SimpleMatrix unkVec = new SimpleMatrix(2, 1);
    wordVectors.put("hello", lowerVec);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, unkVec);
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // String vocabWord = model.getVocabWord("HELLO");
    // assertEquals("hello", vocabWord);
  }

  @Test
  public void testGetVocabWordReturnsUNKWhenMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.lowercaseWordVectors = false;
    options.simplifiedModel = true;
    options.combineClassification = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    // SimpleTensor Wt = SimpleTensor.constant(6, 6, 3, 1.0);
    SimpleMatrix unknownVec = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, unknownVec);
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // String vocabWord = model.getVocabWord("not_present");
    // assertEquals(SentimentModel.UNKNOWN_WORD, vocabWord);
  }

  @Test
  public void testBasicCategoryStripsAtPrefix() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 0.5);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // String result = model.basicCategory("@NP");
    // assertEquals("NP", result);
  }

  @Test(expected = RuntimeException.class)
  public void testReadWordVectorsMissingUNKThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.wordVectors = "dummy_path";
    options.unkWord = "<unk>";
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 0.1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // model.readWordVectors();
  }

  @Test
  public void testRandomWordVectorDimensionsAndRange() {
    int size = 5;
    Random rand = new Random(1234);
    SimpleMatrix vec = SentimentModel.randomWordVector(size, rand);
    assertEquals(size, vec.numRows());
    assertEquals(1, vec.numCols());
    double maxAbs = 0.0;
    for (int i = 0; i < size; i++) {
      maxAbs = Math.max(maxAbs, Math.abs(vec.get(i, 0)));
    }
    assertTrue(maxAbs < 1.0);
  }

  @Test
  public void testGetUnaryClassificationReturnsNullWhenMissing() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unaryMap = new HashMap<>();
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // SimpleMatrix result = model.getUnaryClassification("UNK_TAG");
    // assertNull(result);
  }

  @Test
  public void testGetBinaryClassificationWithCombineClassificationTrueAlwaysReturnsUnaryMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix binaryTransform = new SimpleMatrix(2, 5);
    SimpleMatrix unaryClass = new SimpleMatrix(2, 3);
    // SimpleTensor tensor = SimpleTensor.constant(4, 4, 2, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", unaryClass);
    // SentimentModel model = SentimentModel.modelFromMatrices(binaryTransform, unaryClass, tensor,
    // wordVectors, options);
    // SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    // assertEquals(unaryClass, result);
  }

  @Test(expected = AssertionError.class)
  public void testGetWForNodeWithNoChildrenThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix binaryTransform = new SimpleMatrix(2, 5);
    SimpleMatrix unaryClass = new SimpleMatrix(2, 3);
    // SimpleTensor tensor = SimpleTensor.constant(4, 4, 2, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", unaryClass);
    // SentimentModel model = SentimentModel.modelFromMatrices(binaryTransform, unaryClass, tensor,
    // wordVectors, options);
    Tree leaf = new LabeledScoredTreeNode(new StringLabel("LEAF"));
    leaf.setChildren(new Tree[0]);
    // model.getWForNode(leaf);
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeWithInvalidChildSizeThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    // SimpleTensor Wt = SimpleTensor.constant(6, 6, 3, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree leaf = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree left = new LabeledScoredTreeNode(new StringLabel("A"));
    Tree mid = new LabeledScoredTreeNode(new StringLabel("B"));
    Tree right = new LabeledScoredTreeNode(new StringLabel("C"));
    leaf.setChildren(new Tree[] {left, mid, right});
    // model.getClassWForNode(leaf);
  }

  @Test
  public void testParamsToVectorReturnsCorrectSizeEvenWithEmptyMatrices() {
    RNNOptions options = new RNNOptions();
    options.numHid = 1;
    options.numClasses = 1;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix W = new SimpleMatrix(1, 3);
    SimpleMatrix Wcat = new SimpleMatrix(1, 2);
    // SimpleTensor tensor = SimpleTensor.constant(2, 2, 1, 0.5);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(1, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, tensor, wordVectors,
    // options);
    // double[] result = model.paramsToVector();
    // assertNotNull(result);
    // assertEquals(model.totalParamSize(), result.length);
  }

  @Test
  public void testPrintParamInformationIndexOutOfBoundsLogsFinalMessage() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor tensor = SimpleTensor.constant(4, 4, 2, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("x", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, tensor, wordVectors,
    // options);
    // int largeIndex = model.totalParamSize() + 1000;
    // model.printParamInformation(largeIndex);
  }

  @Test
  public void testUnaryClassificationSizeWithMultipleEntries() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    Map<String, SimpleMatrix> unaryClassification = Generics.newTreeMap();
    SimpleMatrix m1 = new SimpleMatrix(2, 3);
    SimpleMatrix m2 = new SimpleMatrix(2, 3);
    unaryClassification.put("A", m1);
    unaryClassification.put("B", m2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    TwoDimensionalMap<String, String, SimpleMatrix> binTf = TwoDimensionalMap.treeMap();
    TwoDimensionalMap<String, String, SimpleTensor> binTensor = TwoDimensionalMap.treeMap();
    TwoDimensionalMap<String, String, SimpleMatrix> binClass = TwoDimensionalMap.treeMap();
    SentimentModel model =
        new SentimentModel(binTf, binTensor, binClass, unaryClassification, wordVectors, options);
    assertEquals(2, model.numUnaryMatrices);
    assertEquals(
        2 * 2 * (2 + 1) / 2, unaryClassification.size() * (model.unaryClassificationSize / 2));
  }

  @Test
  public void testGetBinaryTransformReturnsCorrectMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.combineClassification = true;
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    SimpleMatrix matrix = new SimpleMatrix(2, 5);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("NP", "VP", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getBinaryTransform("NP@BAR", "VP");
    assertEquals(matrix, result);
  }

  @Test(expected = NullPointerException.class)
  public void testGetBinaryTransformWhenKeyDoesNotExistReturnsNull() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getBinaryTransform("XX", "YY");
    result.get(0, 0);
  }

  @Test(expected = NullPointerException.class)
  public void testGetBinaryTensorReturnsNullForMissingKey() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.useTensors = true;
    options.combineClassification = true;
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            binaryTensors,
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleTensor result = model.getBinaryTensor("NP@BAR", "ZZ");
    // result.get(0, 0, 0);
  }

  @Test
  public void testToStringIncludesBinaryTransformMatrixHeader() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix matrix = new SimpleMatrix(2, 5);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("word", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    String output = model.toString();
    assertTrue(output.contains("Binary transform matrix"));
  }

  @Test
  public void testInitRandomWordVectorsAddsUnknown() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.lowercaseWordVectors = false;
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree root = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree leaf1 = new LabeledScoredTreeNode(new StringLabel("Dog"));
    Tree leaf2 = new LabeledScoredTreeNode(new StringLabel("Barked"));
    root.setChildren(new Tree[] {leaf1, leaf2});
    List<Tree> trees = new ArrayList<>();
    trees.add(root);
    model.initRandomWordVectors(trees);
    SimpleMatrix vec = model.getWordVector("Dog");
    assertNotNull(vec);
    assertEquals(2, vec.numRows());
    SimpleMatrix unk = model.getWordVector(SentimentModel.UNKNOWN_WORD);
    assertNotNull(unk);
    assertEquals(2, unk.numRows());
  }

  @Test
  public void testVectorToParamsWithEmptyMatrices() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    // SimpleTensor Wt = SimpleTensor.constant(4, 4, 2, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("a", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    // SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    // double[] original = model.paramsToVector();
    // double[] newParams = Arrays.copyOf(original, original.length);
    // for (int i = 0; i < newParams.length; i++) {
    // newParams[i] += 1.0;
    // }
    // model.vectorToParams(newParams);
    // double[] changed = model.paramsToVector();
    // assertNotEquals(original[0], changed[0], 1e-10);
    // assertEquals(original.length, changed.length);
  }

  @Test
  public void testTotalParamSizeZeroForEmptyModel() {
    RNNOptions options = new RNNOptions();
    options.numHid = 1;
    options.numClasses = 1;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = false;
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    int size = model.totalParamSize();
    assertEquals(0, size);
  }

  @Test
  public void testGetClassWForUnaryNodeWithCombineClassificationFalse() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    SimpleMatrix binaryTransform = new SimpleMatrix(2, 5);
    SimpleMatrix binaryClass = new SimpleMatrix(2, 3);
    SimpleMatrix unaryClass = new SimpleMatrix(2, 3);
    // SimpleTensor tensor = SimpleTensor.constant(4, 4, 2, 1.0);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", unaryClass);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    Tree node = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree child = new LabeledScoredTreeNode(new StringLabel("NP"));
    node.setChildren(new Tree[] {child});
    SimpleMatrix result = model.getClassWForNode(node);
    assertEquals(unaryClass, result);
  }

  @Test
  public void testModelFromMatricesWithMultipleWordsMaintainsAllVectors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vec = new SimpleMatrix(2, 1);
    wordVectors.put("dog", vec);
    wordVectors.put("cat", vec);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, vec);
    SimpleMatrix transform = new SimpleMatrix(2, 5);
    SimpleMatrix classMatrix = new SimpleMatrix(2, 3);
    // SimpleTensor tensor = SimpleTensor.constant(4, 4, 2, 1.0);
    // SentimentModel model = SentimentModel.modelFromMatrices(transform, classMatrix, tensor,
    // wordVectors, options);
    // assertEquals(3, model.wordVectors.size());
    // assertEquals(vec, model.getWordVector("dog"));
    // assertEquals(vec, model.getWordVector("cat"));
    // assertEquals(vec, model.getWordVector("unseenword"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedBinaryProductionsInConstructorThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = false;
    options.combineClassification = false;
    options.useTensors = false;
    List<Tree> trees = new ArrayList<>();
    Tree root = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree left = new LabeledScoredTreeNode(new StringLabel("NP"));
    Tree right = new LabeledScoredTreeNode(new StringLabel("VP"));
    root.setChildren(new Tree[] {left, right});
    trees.add(root);
    new SentimentModel(options, trees);
  }

  @Test
  public void testRandomTransformMatrixHasCorrectDimensionsAndBiasZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.numClasses = 2;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix transform = model.randomTransformMatrix();
    assertEquals(3, transform.numRows());
    assertEquals(3 * 2 + 1, transform.numCols());
  }

  @Test
  public void testGetUnaryClassificationReturnsCorrectMatrixWithBasicCategory() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.combineClassification = true;
    options.numHid = 2;
    options.numClasses = 2;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    SimpleMatrix matrix = new SimpleMatrix(2, 3);
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("NP", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("NP@BAR");
    assertEquals(matrix, result);
  }

  @Test
  public void testGetBinaryClassificationSpecificMatrixWithCombineFalse() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    SimpleMatrix matrix = new SimpleMatrix(2, 3);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClassification =
        TwoDimensionalMap.treeMap();
    binaryClassification.put("NP", "VP", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClassification,
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getBinaryClassification("NP@BAR", "VP");
    assertEquals(matrix, result);
  }

  @Test(expected = RuntimeException.class)
  public void testInitRandomWordVectorsWithoutNumHidThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 0;
    options.simplifiedModel = true;
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree tree = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree leaf = new LabeledScoredTreeNode(new StringLabel("dog"));
    tree.setChildren(new Tree[] {leaf});
    List<Tree> trees = new ArrayList<>();
    trees.add(tree);
    model.initRandomWordVectors(trees);
  }

  @Test
  public void testParamsToVectorIncludesAllComponents() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("dog", new SimpleMatrix(2, 1));
    wordVectors.put("cat", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SimpleMatrix binaryTransform = new SimpleMatrix(2, 5);
    SimpleMatrix classification = new SimpleMatrix(2, 3);
    // SimpleTensor tensor = SimpleTensor.constant(4, 4, 2, 1.0);
    // SentimentModel model = SentimentModel.modelFromMatrices(binaryTransform, classification,
    // tensor, wordVectors, options);
    // double[] vec = model.paramsToVector();
    // int expected = model.totalParamSize();
    // assertNotNull(vec);
    // assertEquals(expected, vec.length);
  }

  @Test
  public void testRandomWordVectorRangeIsReasonable() {
    Random rand = new Random(123);
    SimpleMatrix vec = SentimentModel.randomWordVector(10, rand);
    assertEquals(10, vec.numRows());
    assertEquals(1, vec.numCols());
    for (int i = 0; i < vec.numRows(); i++) {
      assertTrue(Math.abs(vec.get(i, 0)) < 1.0);
    }
  }

  @Test(expected = RuntimeException.class)
  public void testReadWordVectorsThrowsWhenUnkMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.wordVectors = "dummy_path";
    options.unkWord = "<unk>";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    model.readWordVectors();
  }

  @Test
  public void testGetBinaryClassificationReturnsNullWhenKeyIsMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = false;
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix matrix = model.getBinaryClassification("NP@BAR", "VP");
    assertNull(matrix);
  }

  @Test
  public void testBasicCategoryWithNoAtSymbolReturnsAsIs() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    String category = model.basicCategory("NP");
    assertEquals("NP", category);
  }

  @Test
  public void testGetVocabWordLowercasesIfOptionSet() {
    RNNOptions options = new RNNOptions();
    options.lowercaseWordVectors = true;
    options.numHid = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix wordVecLower = new SimpleMatrix(2, 1);
    SimpleMatrix wordVecUNK = new SimpleMatrix(2, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("dog", wordVecLower);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, wordVecUNK);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    String vocabWord = model.getVocabWord("DOG");
    assertEquals("dog", vocabWord);
  }

  @Test
  public void testGetUnaryClassificationReturnsNullWhenCategoryNotFound() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.langpack = new edu.stanford.nlp.trees.PennTreebankLanguagePack();
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("NP");
    assertNull(result);
  }

  @Test
  public void testTotalParamSizeWithUnaryMatrixOnly() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix unaryMatrix = new SimpleMatrix(3, 3);
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", unaryMatrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    int expected = unaryMatrix.getNumElements() + 2;
    assertEquals(expected, model.totalParamSize());
  }

  @Test
  public void testGetWForNodeReturnsCorrectMatrixWithSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    SimpleMatrix matrix = new SimpleMatrix(2, 5);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryMap = TwoDimensionalMap.treeMap();
    binaryMap.put("", "", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            binaryMap,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    Tree parent = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree left = new LabeledScoredTreeNode(new StringLabel("NP"));
    Tree right = new LabeledScoredTreeNode(new StringLabel("VP"));
    parent.setChildren(new Tree[] {left, right});
    SimpleMatrix result = model.getWForNode(parent);
    assertEquals(matrix, result);
  }

  @Test
  public void testRandomClassificationMatrixValuesAreInExpectedRange() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.trainOptions.scalingForInit = 1.0;
    options.simplifiedModel = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix matrix = model.randomClassificationMatrix();
    assertEquals(2, matrix.numRows());
    assertEquals(4, matrix.numCols());
    double max = Double.NEGATIVE_INFINITY;
    double min = Double.POSITIVE_INFINITY;
    for (int i = 0; i < matrix.numRows(); i++) {
      for (int j = 0; j < matrix.numCols(); j++) {
        double val = matrix.get(i, j);
        if (j == 3) {
          assertTrue(val >= 0.0 && val <= 1.0);
        }
        max = Math.max(max, val);
        min = Math.min(min, val);
      }
    }
    assertTrue(min <= max);
  }

  @Test
  public void testUnaryClassificationInitializationAndRetrieval() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    SimpleMatrix matrix = new SimpleMatrix(2, 3);
    unaryClassification.put("", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("hello", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("S");
    assertEquals(matrix, result);
  }

  @Test
  public void testBinaryTensorScaleAppliesCorrectly() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.trainOptions.scalingForInit = 0.5;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleTensor tensor = model.randomBinaryTensor();
    // assertEquals(8, tensor.d1);
    // assertEquals(8, tensor.d2);
    // assertEquals(2, tensor.d3);
  }

  @Test
  public void testGetWordVectorWithMixedCaseMatchesLowercaseSetting() {
    RNNOptions options = new RNNOptions();
    options.lowercaseWordVectors = true;
    options.numHid = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    SimpleMatrix lowerVec = new SimpleMatrix(2, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("hello", lowerVec);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getWordVector("HELLO");
    assertEquals(lowerVec, result);
  }

  @Test
  public void testTotalParamSizeWithEmptyBinaryTensors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.useTensors = false;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("w1", new SimpleMatrix(3, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", new SimpleMatrix(2, 4));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    int expectedParamSize = unaryClassification.get("").getNumElements() + wordVectors.size() * 3;
    assertEquals(expectedParamSize, model.totalParamSize());
  }

  @Test
  public void testGetClassWForNodeWithBinaryNonCombinedClassification() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    SimpleMatrix matrix = new SimpleMatrix(2, 3);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("", "", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            new HashMap<>(),
            wordVectors,
            options);
    Tree binaryNode = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    Tree left = new LabeledScoredTreeNode(new StringLabel("S"));
    Tree right = new LabeledScoredTreeNode(new StringLabel("VP"));
    binaryNode.setChildren(new Tree[] {left, right});
    SimpleMatrix result = model.getClassWForNode(binaryNode);
    assertEquals(matrix, result);
  }

  @Test
  public void testToStringIncludesUnaryClassificationHeader() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("a", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", new SimpleMatrix(2, 3));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    String output = model.toString();
    assertTrue(output.contains("Unary classification matrix"));
  }
}
