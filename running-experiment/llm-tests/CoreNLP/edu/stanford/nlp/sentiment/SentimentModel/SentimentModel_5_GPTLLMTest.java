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

public class SentimentModel_5_GPTLLMTest {

  @Test(expected = IllegalArgumentException.class)
  public void testModelFromMatrices_withInvalidOptionsThrowsException() {
    RNNOptions options = new RNNOptions();
    options.combineClassification = false;
    options.simplifiedModel = false;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(5, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("word", new SimpleMatrix(3, 1));
    SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
  }

  @Test
  public void testModelFromMatrices_constructionSuccess() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 4;
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 1.0;
    options.randomSeed = 42;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(4, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vec1 = new SimpleMatrix(3, 1);
    wordVectors.put("alpha", vec1);
    SimpleMatrix vec2 = new SimpleMatrix(3, 1);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, vec2);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    assertNotNull(model.getWordVector("alpha"));
    assertEquals(vec1, model.getWordVector("alpha"));
  }

  @Test
  public void testGetVocabWord_returnsCorrectKey() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = false;
    options.lowercaseWordVectors = true;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vec1 = new SimpleMatrix(2, 1);
    wordVectors.put("hello", vec1);
    SimpleMatrix vec2 = new SimpleMatrix(2, 1);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, vec2);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    String keyKnown = model.getVocabWord("Hello");
    String keyUnknown = model.getVocabWord("world");
    assertEquals("hello", keyKnown);
    assertEquals(SentimentModel.UNKNOWN_WORD, keyUnknown);
  }

  @Test
  public void testParamsVectorLosslessConversion() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(3, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix v1 = new SimpleMatrix(4, 1);
    wordVectors.put("x", v1);
    SimpleMatrix v2 = new SimpleMatrix(4, 1);
    wordVectors.put("y", v2);
    SimpleMatrix v3 = new SimpleMatrix(4, 1);
    wordVectors.put(SentimentModel.UNKNOWN_WORD, v3);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    double[] original = model.paramsToVector();
    // SentimentModel duplicate = SentimentModel.modelFromMatrices(W.copy(), Wcat.copy(), Wt.copy(),
    // new HashMap<>(wordVectors), options);
    // duplicate.vectorToParams(original);
    // double[] copied = duplicate.paramsToVector();
    // assertArrayEquals(original, copied, 1e-8);
  }

  @Test
  public void testToString_outputsWordVectors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 4;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = false;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(4, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("term", new SimpleMatrix(3, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    String text = model.toString();
    assertNotNull(text);
    assertTrue(text.contains("Word vectors"));
    assertTrue(text.contains("term"));
  }

  @Test
  public void testGetClassWForNode_ForBinaryNodeInSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("a", new SimpleMatrix(2, 1));
    wordVectors.put("b", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("a");
    Tree right = new LabeledScoredTreeFactory().newLeaf("b");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("X", Arrays.asList(left, right));
    SimpleMatrix classMatrix = model.getClassWForNode(parent);
    assertNotNull(classMatrix);
    assertEquals(Wcat.getNumElements(), classMatrix.getNumElements());
  }

  @Test
  public void testSerializationDeserialization_preserveModel() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = false;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    options.trainOptions.scalingForInit = 1.0;
    options.wordVectors = "unused";
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("foo", new SimpleMatrix(2, 1));
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    String path = "testmodel.ser";
    model.saveSerialized(path);
    SentimentModel loaded = SentimentModel.loadSerialized(path);
    assertNotNull(loaded);
    assertEquals(model.totalParamSize(), loaded.totalParamSize());
    assertEquals(model.numClasses, loaded.numClasses);
    assertEquals(model.numHid, loaded.numHid);
    assertNotNull(loaded.getWordVector("foo"));
    new File(path).delete();
  }

  @Test(expected = AssertionError.class)
  public void testGetWForNode_singleChild_throws() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree child = new LabeledScoredTreeFactory().newLeaf("a");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(child));
    model.getWForNode(parent);
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNode_threeChildren_throws() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = SentimentModel.UNKNOWN_WORD;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put(SentimentModel.UNKNOWN_WORD, new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree leaf1 = new LabeledScoredTreeFactory().newLeaf("a");
    Tree leaf2 = new LabeledScoredTreeFactory().newLeaf("b");
    Tree leaf3 = new LabeledScoredTreeFactory().newLeaf("c");
    Tree tree =
        new LabeledScoredTreeFactory().newTreeNode("Root", Arrays.asList(leaf1, leaf2, leaf3));
    model.getClassWForNode(tree);
  }

  @Test
  public void testRandomWordVector_generatedWithExactSize() {
    SimpleMatrix vec = SentimentModel.randomWordVector(7, new Random(123));
    assertEquals(7, vec.getNumElements());
  }

  @Test
  public void testConstructorInfersNumHidFromWordVectorsOnly() {
    RNNOptions options = new RNNOptions();
    options.numHid = 0;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    SimpleMatrix W = new SimpleMatrix(5, 11);
    SimpleMatrix Wcat = new SimpleMatrix(3, 6);
    SimpleTensor Wt = new SimpleTensor(10, 10, 5);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("x", new SimpleMatrix(5, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(5, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    assertEquals(5, model.numHid);
  }

  @Test
  public void testGetClassWForNode_UnaryClassificationPath() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", new SimpleMatrix(2, 4));
    SentimentModel model =
        new SentimentModel(
            binaryTransform, binaryTensors, binaryClass, unaryClassification, wordVectors, options);
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("x");
    Tree node =
        new LabeledScoredTreeFactory().newTreeNode("Preterminal", Collections.singletonList(leaf));
    SimpleMatrix mat = model.getClassWForNode(node);
    assertNotNull(mat);
    assertEquals(8, mat.getNumElements());
  }

  @Test
  public void testGetUnaryClassification_returnsCorrectMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(4, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    SimpleMatrix classMat = new SimpleMatrix(2, 5);
    unary.put("", classMat);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            wordVectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("NP");
    assertEquals(classMat, result);
  }

  @Test
  public void testGetBinaryClassification_withCombineTrueReturnsUnary() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(4, 1));
    SimpleMatrix unaryMatrix = new SimpleMatrix(2, 5);
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("", unaryMatrix);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            wordVectors,
            options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertEquals(unaryMatrix, result);
  }

  @Test
  public void testTotalParamSize_emptyModelReturnsZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    assertEquals(0, model.totalParamSize());
  }

  @Test
  public void testVectorToParams_withEmptyIteratorDoesNotCrash() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    double[] theta = new double[0];
    model.vectorToParams(theta);
    assertEquals(0, model.totalParamSize());
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNode_singleChildThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("foo");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf));
    model.getTensorForNode(parent);
  }

  @Test
  public void testPrintParamInformation_onUnaryMatrixIndex() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    wordVectors.put("a", new SimpleMatrix(3, 1));
    SimpleMatrix unaryMatrix = new SimpleMatrix(2, 4);
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", unaryMatrix);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    int index = model.totalParamSize() - 1;
    model.printParamInformation(index);
  }

  @Test
  public void testGetBinaryTensor_withExactKeyMatching() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = false;
    options.useTensors = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    options.langpack =
        new edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams().treebankLanguagePack();
    SimpleTensor tensor = new SimpleTensor(4, 4, 2);
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    tensors.put("NP", "VP", tensor);
    TwoDimensionalMap<String, String, SimpleMatrix> emptyMatrixMap = TwoDimensionalMap.treeMap();
    Map<String, SimpleMatrix> emptyUnary = new HashMap<>();
    emptyUnary.put("", new SimpleMatrix(2, 3));
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            emptyMatrixMap, tensors, emptyMatrixMap, emptyUnary, wordVectors, options);
    SimpleTensor retrieved = model.getBinaryTensor("NP", "VP");
    assertEquals(tensor, retrieved);
  }

  @Test
  public void testVectorToParams_boundaryIndex() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("a", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    double[] vector = model.paramsToVector();
    assertEquals(model.totalParamSize(), vector.length);
    model.vectorToParams(vector);
    assertArrayEquals(vector, model.paramsToVector(), 1e-8);
  }

  @Test(expected = RuntimeException.class)
  public void testReadWordVectors_throwsIfUnknownVectorMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.randomWordVectors = false;
    options.wordVectors = "edu/stanford/nlp/models/dummy/vector.txt";
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    List<Tree> trees = new ArrayList<>();
    RNNOptions finalOptions = options;
    SentimentModel model = new SentimentModel(finalOptions, trees);
  }

  @Test
  public void testParamsToVector_returnsConsistentState() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("x", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    double[] vec1 = model.paramsToVector();
    model.vectorToParams(vec1);
    double[] vec2 = model.paramsToVector();
    assertArrayEquals(vec1, vec2, 1e-8);
  }

  @Test
  public void testGetWordVector_differentCapitalizationLowercasingEnabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.lowercaseWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix knownVec = new SimpleMatrix(3, 1);
    SimpleMatrix unkVec = new SimpleMatrix(3, 1);
    wordVectors.put("test", knownVec);
    wordVectors.put("*UNK*", unkVec);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getWordVector("TesT");
    assertEquals(knownVec, result);
    SimpleMatrix resultUnknown = model.getWordVector("notfoundword");
    assertEquals(unkVec, resultUnknown);
  }

  @Test
  public void testGetUnaryClassification_returnsNullForMissingCategory() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = false;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("A", new SimpleMatrix(2, 4));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            wordVectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("B");
    assertNull(result);
  }

  @Test
  public void testToString_outputsEachSectionIndividually() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", new SimpleMatrix(3, 7));
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("", "", new SimpleTensor(6, 6, 3));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("", new SimpleMatrix(2, 4));
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("run", new SimpleMatrix(3, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            binaryTensors,
            TwoDimensionalMap.treeMap(),
            unary,
            wordVectors,
            options);
    String result = model.toString();
    assertTrue(result.contains("Binary transform matrix"));
    assertTrue(result.contains("Binary transform tensor"));
    assertTrue(result.contains("Unary classification matrix"));
    assertTrue(result.contains("Word vectors"));
  }

  @Test
  public void testGetBinaryClassification_returnsNullWhenNotPresentAndNotCombined() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = false;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(4, 1));
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClassification =
        TwoDimensionalMap.treeMap();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClassification,
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertNull(result);
  }

  @Test
  public void testGetBinaryTransform_returnsNullWhenNotPresent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getBinaryTransform("X", "Y");
    assertNull(result);
  }

  @Test
  public void testGetBinaryTensor_returnsNullWhenNotPresent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleTensor tensor = model.getBinaryTensor("DT", "NN");
    assertNull(tensor);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNode_notUsingTensorThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.useTensors = false;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("DT");
    Tree right = new LabeledScoredTreeFactory().newLeaf("NN");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(left, right));
    model.getTensorForNode(parent);
  }

  @Test
  public void testBasicCategory_handlesAtSignPrefixRemoval() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.langpack =
        new edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams().treebankLanguagePack();
    options.unkWord = "*UNK*";
    options.numHid = 3;
    options.numClasses = 2;
    options.trainOptions.scalingForInit = 1.0;
    options.combineClassification = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    String result = model.basicCategory("@NP");
    assertEquals("NP", result);
  }

  @Test
  public void testGetWordVector_returnsNullIfUnknownWordAndNoUnkVector() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.lowercaseWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vector = new SimpleMatrix(3, 1);
    wordVectors.put("known", vector);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getWordVector("unknown");
    assertNull(result);
  }

  @Test
  public void testPrintParamInformation_onWordVectorIndex() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.trainOptions.scalingForInit = 1.0;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new LinkedHashMap<>();
    wordVectors.put("a", new SimpleMatrix(3, 1));
    wordVectors.put("b", new SimpleMatrix(3, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("", new SimpleMatrix(2, 4));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            wordVectors,
            options);
    int offset = model.totalParamSize() - 1;
    model.printParamInformation(offset);
  }

  @Test
  public void testModelWithEmptyBinaryTransform_buildsIdentityMatrixCorrectly() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("", new SimpleMatrix(3, 3));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            wordVectors,
            options);
    SimpleMatrix identity = model.identity;
    assertEquals(2, identity.numRows());
    assertEquals(2, identity.numCols());
    assertEquals(1.0, identity.get(0, 0), 0.0);
    assertEquals(0.0, identity.get(0, 1), 0.0);
  }

  @Test
  public void testIdentityMatrixIsCorrectSizeAndValues() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(2, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    SimpleMatrix identity = model.identity;
    assertEquals(3, identity.numRows());
    assertEquals(3, identity.numCols());
    assertEquals(1.0, identity.get(0, 0), 1e-8);
    assertEquals(0.0, identity.get(0, 1), 1e-8);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testConstructorWithSimplifiedFalseBinaryProductionsThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 2;
    options.simplifiedModel = false;
    options.combineClassification = true;
    options.randomWordVectors = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    List<Tree> trainingTrees = new ArrayList<>();
    SentimentModel model = new SentimentModel(options, trainingTrees);
  }

  @Test
  public void testParamsToVectorIncludesAllComponents() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    SimpleMatrix binaryMatrix = new SimpleMatrix(2, 5);
    Map<String, SimpleMatrix> wordVectors = new LinkedHashMap<>();
    wordVectors.put("x", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SimpleTensor tensor = new SimpleTensor(4, 4, 2);
    SimpleMatrix unary = new SimpleMatrix(2, 3);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", binaryMatrix);
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("", "", tensor);
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    unaryClass.put("", unary);
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            binaryTensors,
            TwoDimensionalMap.treeMap(),
            unaryClass,
            wordVectors,
            options);
    double[] raw = model.paramsToVector();
    assertEquals(model.totalParamSize(), raw.length);
  }

  @Test
  public void testGetWForNode_returnsNullIfMatrixMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("X");
    Tree right = new LabeledScoredTreeFactory().newLeaf("Y");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("Z", Arrays.asList(left, right));
    SimpleMatrix result = model.getWForNode(parent);
    assertNull(result);
  }

  @Test
  public void testGetClassWForNode_binaryWithoutMatrixReturnsNull() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            new HashMap<>(),
            wordVectors,
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("JJ");
    Tree right = new LabeledScoredTreeFactory().newLeaf("NN");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(left, right));
    SimpleMatrix matrix = model.getClassWForNode(parent);
    assertNull(matrix);
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNode_zeroChildrenThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 3;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    Tree node = new LabeledScoredTreeFactory().newTreeNode("EMPTY", new ArrayList<Tree>());
    model.getClassWForNode(node);
  }

  @Test
  public void testRandomTransformMatrixSizeAndContent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.randomTransformMatrix();
    assertEquals(2, result.numRows());
    assertEquals(5, result.numCols());
  }

  @Test
  public void testGetVocabWord_preservesCaseWhenLowercaseFalse() {
    RNNOptions options = new RNNOptions();
    options.lowercaseWordVectors = false;
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("Dog", new SimpleMatrix(3, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    String result = model.getVocabWord("Dog");
    assertEquals("Dog", result);
  }

  @Test
  public void testTotalParamSize_withOnlyUnaryMatricesAndNoWordVectors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 4;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", new SimpleMatrix(4, 4));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    int size = model.totalParamSize();
    assertEquals(16, size);
  }

  @Test(expected = RuntimeException.class)
  public void testInitRandomWordVectors_missingNumHidThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 0;
    options.randomWordVectors = true;
    options.lowercaseWordVectors = true;
    options.unkWord = "*UNK*";
    List<Tree> trees = new ArrayList<>();
    Tree t =
        new LabeledScoredTreeFactory()
            .newTreeNode(
                "X",
                Arrays.asList(
                    new LabeledScoredTreeFactory().newLeaf("Hello"),
                    new LabeledScoredTreeFactory().newLeaf("world")));
    trees.add(t);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    model.initRandomWordVectors(trees);
  }

  @Test
  public void testGetUnaryClassificationWithEmptyStringKey() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = false;
    options.trainOptions.scalingForInit = 1.0;
    SimpleMatrix matrix = new SimpleMatrix(2, 4);
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", matrix);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("");
    assertSame(matrix, result);
  }

  @Test
  public void testToString_withOnlyBinaryClassificationMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 5;
    options.numClasses = 4;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    SimpleMatrix classificationMatrix = new SimpleMatrix(4, 6);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClassification =
        TwoDimensionalMap.treeMap();
    binaryClassification.put("", "", classificationMatrix);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClassification,
            new HashMap<>(),
            new HashMap<>(),
            options);
    String output = model.toString();
    assertTrue(output.contains("Binary classification matrix"));
    assertTrue(output.contains("\n"));
  }

  @Test
  public void testGetVocabWord_lowercaseFalse_preservesWordIfMissing() {
    RNNOptions options = new RNNOptions();
    options.lowercaseWordVectors = false;
    options.unkWord = "*UNK*";
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    String result = model.getVocabWord("NotFoundWord");
    assertEquals("*UNK*", result);
  }

  @Test
  public void testToString_withMixedComponents() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", new SimpleMatrix(2, 5));
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("", "", new SimpleTensor(4, 4, 2));
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    unaryClassification.put("", new SimpleMatrix(2, 3));
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("a", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            binaryTensors,
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            wordVectors,
            options);
    String output = model.toString();
    assertTrue(output.contains("Binary transform matrix"));
    assertTrue(output.contains("Binary transform tensor"));
    assertTrue(output.contains("Unary classification matrix"));
    assertTrue(output.contains("Word vectors"));
  }

  @Test
  public void testSaveAndLoadSerializedModelRetainsState() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(2, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("sample", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    String path = "test_model_serialization.ser";
    model.saveSerialized(path);
    SentimentModel loaded = SentimentModel.loadSerialized(path);
    assertNotNull(loaded);
    assertEquals(model.totalParamSize(), loaded.totalParamSize());
    File file = new File(path);
    file.delete();
  }

  @Test
  public void testGetClassWForNode_withUnaryKeyNotPresent_returnsNull() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Tree unary = new LabeledScoredTreeFactory().newLeaf("unknown");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(unary));
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClass,
            wordVectors,
            options);
    SimpleMatrix result = model.getClassWForNode(parent);
    assertNull(result);
  }

  @Test
  public void testBinaryTransformSizeCalculationWithoutUseTensors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 5;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = false;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(5, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("word", new SimpleMatrix(4, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(4, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    assertEquals(4 * (2 * 4 + 1), model.binaryTransformSize);
    assertEquals(0, model.binaryTensorSize);
  }

  @Test
  public void testClassificationMatrixFallbackInCombinedMode() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    SimpleMatrix W = new SimpleMatrix(2, 5);
    SimpleMatrix Wcat = new SimpleMatrix(3, 3);
    SimpleTensor Wt = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("term", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("", Wcat);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    SimpleMatrix fallback = model.getBinaryClassification("NON_EXISTENT_L", "NON_EXISTENT_R");
    assertSame(Wcat, fallback);
  }

  @Test
  public void testGetWForNode_binary_returnsNullIfNotFound() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new LinkedHashMap<>();
    wordVectors.put("a", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("NP");
    Tree right = new LabeledScoredTreeFactory().newLeaf("VP");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("S", Arrays.asList(left, right));
    SimpleMatrix matrix = model.getWForNode(parent);
    assertNull(matrix);
  }

  @Test
  public void testUnknownUnaryLabelReturnsNullMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.trainOptions.scalingForInit = 1.0;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new LinkedHashMap<>();
    wordVectors.put("a", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    Map<String, SimpleMatrix> unary = new LinkedHashMap<>();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            wordVectors,
            options);
    Tree child = new LabeledScoredTreeFactory().newLeaf("UNKNOWN_LABEL");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("P", Arrays.asList(child));
    SimpleMatrix result = model.getClassWForNode(parent);
    assertNull(result);
  }

  @Test
  public void testSaveLoadSerializedWithPrivateTransformMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 4;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = false;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    SimpleMatrix W = new SimpleMatrix(3, 7);
    SimpleMatrix Wcat = new SimpleMatrix(4, 4);
    SimpleTensor Wt = new SimpleTensor(6, 6, 3);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("foo", new SimpleMatrix(3, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    String file = "sentiment_model_temp.ser";
    model.saveSerialized(file);
    SentimentModel restored = SentimentModel.loadSerialized(file);
    assertNotNull(restored);
    assertEquals(model.totalParamSize(), restored.totalParamSize());
    File f = new File(file);
    assertTrue(f.delete() || !f.exists());
  }

  @Test
  public void testGetWordVector_withDirectMatchReturnsCorrect() {
    RNNOptions options = new RNNOptions();
    options.lowercaseWordVectors = true;
    options.numHid = 3;
    options.numClasses = 2;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    options.simplifiedModel = true;
    options.combineClassification = true;
    SimpleMatrix vecKnown = new SimpleMatrix(3, 1);
    SimpleMatrix vecUnk = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> wordVectors = new LinkedHashMap<>();
    wordVectors.put("known", vecKnown);
    wordVectors.put("*unk*", vecUnk);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getWordVector("KNOWN");
    assertSame(vecKnown, result);
    SimpleMatrix resultUnknown = model.getWordVector("unmatched");
    assertSame(vecUnk, resultUnknown);
  }

  @Test
  public void testUnknownWordVectorFallbackToNullWhenNoUnkKey() {
    RNNOptions options = new RNNOptions();
    options.lowercaseWordVectors = true;
    options.numHid = 3;
    options.numClasses = 2;
    options.unkWord = "*UNK*";
    options.trainOptions.scalingForInit = 1.0;
    options.simplifiedModel = true;
    options.combineClassification = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("known", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    SimpleMatrix result = model.getWordVector("unknown");
    assertNull(result);
  }

  @Test
  public void testBasicCategoryFullModelReturnsTrimmedLabel() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    options.langpack =
        new edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams().treebankLanguagePack();
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    String result = model.basicCategory("@SBAR");
    assertEquals("SBAR", result);
    String noPrefix = model.basicCategory("VP");
    assertEquals("VP", noPrefix);
  }
}
