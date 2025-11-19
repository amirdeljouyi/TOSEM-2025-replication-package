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

public class SentimentModel_1_GPTLLMTest {

  @Test
  public void testModelFromMatricesValidConfiguration() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 4;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.useTensors = true;
    options.lowercaseWordVectors = false;
    options.unkWord = "*UNK*";
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(3, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vec = new SimpleMatrix(4, 1);
    wordVectors.put("good", vec);
    wordVectors.put("*UNK*", vec);
    SentimentModel model = SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
    assertNotNull(model);
    assertNotNull(model.getWordVector("good"));
    assertEquals(3, model.numClasses);
    assertEquals(4, model.numHid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testModelFromMatricesInvalidConfiguration() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 4;
    options.combineClassification = false;
    options.simplifiedModel = false;
    SimpleMatrix W = new SimpleMatrix(4, 9);
    SimpleMatrix Wcat = new SimpleMatrix(3, 5);
    SimpleTensor Wt = new SimpleTensor(8, 8, 4);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("bad", new SimpleMatrix(4, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(4, 1));
    SentimentModel.modelFromMatrices(W, Wcat, Wt, wordVectors, options);
  }

  @Test
  public void testUnaryClassificationRetrieval() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.numHid = 3;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClassification =
        TwoDimensionalMap.treeMap();
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    SimpleMatrix unary = new SimpleMatrix(2, 4);
    unaryClassification.put("", unary);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            binaryTensors,
            binaryClassification,
            unaryClassification,
            wordVectors,
            options);
    SimpleMatrix result = model.getUnaryClassification("NP");
    assertNotNull(result);
    assertEquals(2, result.numRows());
    assertEquals(4, result.numCols());
  }

  @Test
  public void testGetBinaryTransformWithSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 5;
    options.numHid = 6;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    SimpleMatrix transform = new SimpleMatrix(6, 13);
    binaryTransform.put("", "", transform);
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            Generics.newTreeMap(),
            options);
    SimpleMatrix found = model.getBinaryTransform("NP", "VP");
    assertNotNull(found);
    assertEquals(6, found.numRows());
    assertEquals(13, found.numCols());
  }

  @Test
  public void testGetWordVectorKnownAndUnknown() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 4;
    options.lowercaseWordVectors = true;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix vec = new SimpleMatrix(4, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("great", vec);
    wordVectors.put("*UNK*", vec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(4, 9),
            new SimpleMatrix(3, 5),
            new SimpleTensor(8, 8, 4),
            wordVectors,
            options);
    SimpleMatrix known = model.getWordVector("GREAT");
    assertNotNull(known);
    SimpleMatrix unknown = model.getWordVector("UNKNOWNWORD");
    assertNotNull(unknown);
    assertEquals(vec, unknown);
  }

  @Test
  public void testBasicCategoryWithSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = true;
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            new HashMap<>(),
            options);
    String result = model.basicCategory("NP");
    assertEquals("", result);
  }

  @Test
  public void testToStringIncludesWordVectors() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vec = new SimpleMatrix(3, 1);
    wordVectors.put("awesome", vec);
    wordVectors.put("terrible", vec);
    wordVectors.put("*UNK*", vec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            wordVectors,
            options);
    String output = model.toString();
    assertTrue(output.contains("'awesome'"));
    assertTrue(output.contains("'terrible'"));
    assertTrue(output.contains("Word vectors"));
  }

  @Test
  public void testGetWForNodeBinaryStructure() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SimpleMatrix transform = new SimpleMatrix(2, 5);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", transform);
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("A");
    Tree right = new LabeledScoredTreeFactory().newLeaf("B");
    Tree node = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(left, right));
    SimpleMatrix mat = model.getWForNode(node);
    assertNotNull(mat);
    assertEquals(2, mat.numRows());
    assertEquals(5, mat.numCols());
  }

  @Test(expected = AssertionError.class)
  public void testGetWForNodeUnaryThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree child = new LabeledScoredTreeFactory().newLeaf("A");
    Tree node = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(child));
    model.getWForNode(node);
  }

  @Test(expected = AssertionError.class)
  public void testGetWForNodeTooManyChildrenThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree child1 = new LabeledScoredTreeFactory().newLeaf("A");
    Tree child2 = new LabeledScoredTreeFactory().newLeaf("B");
    Tree child3 = new LabeledScoredTreeFactory().newLeaf("C");
    Tree node =
        new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(child1, child2, child3));
    model.getWForNode(node);
  }

  @Test
  public void testGetClassWForNodeReturnsMatrix() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 4;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> unaryClassification = new HashMap<>();
    SimpleMatrix classW = new SimpleMatrix(3, 5);
    unaryClassification.put("", classW);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClassification,
            new HashMap<>(),
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("NP");
    Tree right = new LabeledScoredTreeFactory().newLeaf("VP");
    Tree root = new LabeledScoredTreeFactory().newTreeNode("S", Arrays.asList(left, right));
    SimpleMatrix result = model.getClassWForNode(root);
    assertNotNull(result);
    assertEquals(3, result.numRows());
    assertEquals(5, result.numCols());
  }

  @Test(expected = RuntimeException.class)
  public void testInitRandomWordVectorsThrowsWhenNumHidZero() {
    RNNOptions options = new RNNOptions();
    options.numHid = 0;
    options.lowercaseWordVectors = false;
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(1, 3),
            new SimpleMatrix(1, 2),
            new SimpleTensor(2, 2, 1),
            new HashMap<>(),
            options);
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("test");
    Tree sentence =
        new LabeledScoredTreeFactory().newTreeNode("S", Collections.singletonList(leaf));
    List<Tree> trainingTrees = new ArrayList<>();
    trainingTrees.add(sentence);
    model.initRandomWordVectors(trainingTrees);
  }

  @Test(expected = RuntimeException.class)
  public void testReadWordVectorsWithoutUnkWordThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.unkWord = "*UNK*";
    options.wordVectors = "";
    Map<String, SimpleMatrix> dummyMap = new HashMap<>();
    dummyMap.put("known", new SimpleMatrix(3, 1));
    // Embedding dummyEmbedding = new Embedding(dummyMap, options.numHid);
    options.wordVectors = "not_used.txt";
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            dummyMap,
            options);
    model.wordVectors.clear();
    model.readWordVectors();
  }

  @Test
  public void testParamsToVectorAndVectorToParamsWithZeroSize() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
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
    double[] params = model.paramsToVector();
    assertEquals(2, params.length);
    double[] copy = new double[] {params[0], params[1]};
    model.vectorToParams(copy);
    double[] result = model.paramsToVector();
    assertEquals(params[0], result[0], 1e-6);
    assertEquals(params[1], result[1], 1e-6);
  }

  @Test
  public void testVectorToParamsWithNoMatricesDoesNotThrow() {
    RNNOptions options = new RNNOptions();
    options.numHid = 1;
    options.numClasses = 1;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    double[] vector = new double[0];
    model.vectorToParams(vector);
    assertEquals(0, model.paramsToVector().length);
  }

  @Test
  public void testTotalParamSizeWithTensorsAndMatrices() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 3;
    options.useTensors = true;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", new SimpleMatrix(2, 5));
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("", "", new SimpleTensor(4, 4, 2));
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClassification =
        TwoDimensionalMap.treeMap();
    binaryClassification.put("", "", new SimpleMatrix(3, 3));
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    unaryClass.put("", new SimpleMatrix(3, 3));
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    wordVectors.put("happy", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform, binaryTensors, binaryClassification, unaryClass, wordVectors, options);
    int expectedSize =
        binaryTransform.get("", "").getNumElements()
            + binaryClassification.get("", "").getNumElements()
            + binaryTensors.get("", "").getNumElements()
            + unaryClass.get("").getNumElements()
            + 2 * 2;
    assertEquals(expectedSize, model.totalParamSize());
  }

  @Test
  public void testBasicCategoryStripsAtSymbolPrefix() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    // options.langpack = new BaseLangPack();
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(1, 3),
            new SimpleMatrix(1, 2),
            new SimpleTensor(2, 2, 1),
            new HashMap<>(),
            options);
    String clean = model.basicCategory("@NP");
    assertEquals("NP", clean);
  }

  @Test
  public void testGetClassWForUnaryNodeReturnsUnaryMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    unaryClass.put("", new SimpleMatrix(2, 4));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClass,
            new HashMap<>(),
            options);
    Tree child = new LabeledScoredTreeFactory().newLeaf("NN");
    Tree parent =
        new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(child));
    SimpleMatrix result = model.getClassWForNode(parent);
    assertNotNull(result);
    assertEquals(2, result.numRows());
    assertEquals(4, result.numCols());
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNodeThrowsWhenTensorsDisabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.useTensors = false;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("A");
    Tree right = new LabeledScoredTreeFactory().newLeaf("B");
    Tree node = new LabeledScoredTreeFactory().newTreeNode("X", Arrays.asList(left, right));
    model.getTensorForNode(node);
  }

  @Test
  public void testUnknownWordIsReturnedWhenLowercaseNotPresent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.lowercaseWordVectors = true;
    options.unkWord = "*UNK*";
    SimpleMatrix unkVec = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("good", new SimpleMatrix(3, 1));
    wordVectors.put("*UNK*", unkVec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            wordVectors,
            options);
    String mapped = model.getVocabWord("GOOD");
    assertEquals("*UNK*", mapped);
  }

  @Test
  public void testPrintParamInformationIndexOutOfBounds() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    wordVectors.put("abc", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    int largeIndex = model.totalParamSize() + 10;
    model.printParamInformation(largeIndex);
  }

  @Test
  public void testUnknownWordWithMixedCaseMapping() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.lowercaseWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SimpleMatrix vec = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("known", vec);
    wordVectors.put("*UNK*", vec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            wordVectors,
            options);
    String result = model.getVocabWord("Known");
    assertEquals("known", result);
  }

  @Test
  public void testGetBinaryClassificationReturnsUnaryWhenCombined() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    SimpleMatrix mat = new SimpleMatrix(2, 4);
    unaryClass.put("", mat);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClass,
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertNotNull(result);
    assertEquals(mat, result);
  }

  @Test
  public void testGetBinaryClassificationNonCombinedReturnsExpected() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    SimpleMatrix matrix = new SimpleMatrix(2, 4);
    binaryClass.put("", "", matrix);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            new HashMap<>(),
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertNotNull(result);
    assertEquals(matrix, result);
  }

  @Test
  public void testEmptyBinaryTensorReturnsNullGracefully() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.combineClassification = false;
    options.useTensors = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    SimpleTensor tensor = model.getBinaryTensor("S", "VP");
    assertNull(tensor);
  }

  @Test
  public void testToStringContainsSectionHeadersEvenWhenEmpty() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
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
    String output = model.toString();
    assertTrue(output.contains("Word vectors"));
    assertTrue(output.contains("*UNK*"));
  }

  @Test
  public void testIdentityMatrixIsCorrectlyInitialized() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            wordVectors,
            options);
    SimpleMatrix identity = SimpleMatrix.identity(3);
    SimpleMatrix modelIdentity = model.identity;
    assertEquals(identity, modelIdentity);
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeInvalidNodeNoChildren() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("word");
    Tree node = new LabeledScoredTreeFactory().newTreeNode("S", new ArrayList<>());
    model.getClassWForNode(node);
  }

  @Test
  public void testGetVocabWordReturnsOriginalIfFoundWithoutLowercase() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.lowercaseWordVectors = false;
    options.unkWord = "*UNK*";
    options.simplifiedModel = true;
    options.combineClassification = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    SimpleMatrix vec = new SimpleMatrix(3, 1);
    wordVectors.put("MiXeD", vec);
    wordVectors.put("*UNK*", vec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            wordVectors,
            options);
    String value = model.getVocabWord("MiXeD");
    assertEquals("MiXeD", value);
  }

  @Test
  public void testUnaryCategoryWithSymbolStripped() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    // options.langpack = new BaseLangPack();
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            new HashMap<>(),
            options);
    String result = model.basicCategory("@VP");
    assertEquals("VP", result);
  }

  @Test
  public void testPrintParamInformationWhenFirstParameterMatched() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    SimpleMatrix mat = new SimpleMatrix(3, 7);
    binaryTransform.put("A", "B", mat);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(3, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    model.printParamInformation(0);
  }

  @Test
  public void testParamsToVectorIncludesAllParameterTypes() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.useTensors = true;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix w = new SimpleMatrix(2, 5);
    SimpleMatrix wc = new SimpleMatrix(2, 3);
    SimpleTensor wt = new SimpleTensor(4, 4, 2);
    SimpleMatrix uc = new SimpleMatrix(2, 3);
    SimpleMatrix vec1 = new SimpleMatrix(2, 1);
    SimpleMatrix vec2 = new SimpleMatrix(2, 1);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("A", "B", w);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("A", "B", wc);
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("A", "B", wt);
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    unaryClass.put("X", uc);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("happy", vec1);
    wordVectors.put("*UNK*", vec2);
    SentimentModel model =
        new SentimentModel(
            binaryTransform, binaryTensors, binaryClass, unaryClass, wordVectors, options);
    double[] vector = model.paramsToVector();
    assertEquals(
        w.getNumElements()
            + wc.getNumElements()
            + wt.getNumElements()
            + uc.getNumElements()
            + vec1.getNumElements()
            + vec2.getNumElements(),
        vector.length);
  }

  @Test
  public void testGetClassWForUnaryNodeReturnsCorrectClassificationMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix matrix = new SimpleMatrix(2, 4);
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    unaryClass.put("", matrix);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaryClass,
            new HashMap<>(),
            options);
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("word");
    Tree unary =
        new LabeledScoredTreeFactory()
            .newTreeNode("NT", new ArrayList<Tree>(Collections.singletonList(leaf)));
    SimpleMatrix result = model.getClassWForNode(unary);
    assertNotNull(result);
    assertEquals(matrix, result);
  }

  @Test
  public void testGetBinaryTransformCategoryMappingFallback() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix m = new SimpleMatrix(3, 7);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", m);
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getBinaryTransform("NP", "VP");
    assertNotNull(result);
    assertEquals(3, result.numRows());
    assertEquals(7, result.numCols());
  }

  @Test
  public void testGetBinaryClassificationWithEmptyFallbackInMap() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix m = new SimpleMatrix(2, 3);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClassification =
        TwoDimensionalMap.treeMap();
    binaryClassification.put("", "", m);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClassification,
            new HashMap<>(),
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertEquals(m, result);
  }

  @Test
  public void testToStringIncludesMatrixLabelsWhenPresent() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    // options.langpack = new BaseLangPack();
    options.unkWord = "*UNK*";
    SimpleMatrix w = new SimpleMatrix(2, 5);
    SimpleMatrix wc = new SimpleMatrix(2, 3);
    SimpleTensor wt = new SimpleTensor(4, 4, 2);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("@NP", "@VP", w);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("@NP", "@VP", wc);
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("@NP", "@VP", wt);
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            binaryTransform, binaryTensors, binaryClass, unaryClass, wordVectors, options);
    String result = model.toString();
    assertTrue(result.contains("NP VP:"));
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeFailsWithThreeOrMoreChildren() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix m = new SimpleMatrix(2, 4);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("", "", m);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("a");
    Tree center = new LabeledScoredTreeFactory().newLeaf("b");
    Tree right = new LabeledScoredTreeFactory().newLeaf("c");
    Tree parent =
        new LabeledScoredTreeFactory().newTreeNode("X", Arrays.asList(left, center, right));
    model.getClassWForNode(parent);
  }

  @Test
  public void testGetUnaryClassificationReturnsNullIfMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getUnaryClassification("NP");
    assertNull(result);
  }

  @Test
  public void testGetWordVectorReturnsNullIfUnkNotDefined() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.lowercaseWordVectors = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("valid", new SimpleMatrix(3, 1));
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            vectors,
            options);
    SimpleMatrix v = model.getWordVector("zzz");
    assertNull(v);
  }

  @Test
  public void testZeroLengthParamsHandledGracefully() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    double[] params = model.paramsToVector();
    assertEquals(0, params.length);
    model.vectorToParams(new double[0]);
  }

  @Test
  public void testRandomWordVectorOutputRange() {
    Random rng = new Random(42);
    SimpleMatrix vector = SentimentModel.randomWordVector(5, rng);
    assertEquals(5, vector.numRows());
    assertEquals(1, vector.numCols());
    assertTrue(vector.elementMaxAbs() <= 1.0);
  }

  @Test
  public void testRandomTransformMatrixIncludesBiasColumn() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.numClasses = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    // options.trainOptions = new RNNOptions.TrainerOptions();
    options.trainOptions.scalingForInit = 0.1;
    options.unkWord = "*UNK*";
    SimpleMatrix dummy = new SimpleMatrix(4, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", dummy);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(4, 9),
            new SimpleMatrix(3, 5),
            new SimpleTensor(8, 8, 4),
            wordVectors,
            options);
    SimpleMatrix mat = model.randomTransformMatrix();
    assertEquals(4, mat.numRows());
    assertEquals(9, mat.numCols());
  }

  @Test
  public void testInitRandomWordVectorsLowercaseEnabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.lowercaseWordVectors = true;
    Tree wordA = new LabeledScoredTreeFactory().newLeaf("Hello");
    Tree wordB = new LabeledScoredTreeFactory().newLeaf("WORLD");
    List<Tree> leaves = new ArrayList<>();
    leaves.add(wordA);
    leaves.add(wordB);
    Tree root = new LabeledScoredTreeFactory().newTreeNode("S", leaves);
    List<Tree> trees = new ArrayList<>();
    trees.add(root);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    model.initRandomWordVectors(trees);
    assertTrue(model.wordVectors.containsKey("hello"));
    assertTrue(model.wordVectors.containsKey("world"));
    assertTrue(model.wordVectors.containsKey("*UNK*"));
    assertEquals(3, model.wordVectors.get("hello").getNumElements());
  }

  @Test
  public void testWordVectorMappingPreservesCapitalizationIfDisabled() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.lowercaseWordVectors = false;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    SimpleMatrix vec = new SimpleMatrix(2, 1);
    vectors.put("*UNK*", vec);
    vectors.put("Apple", vec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(2, 5),
            new SimpleMatrix(2, 3),
            new SimpleTensor(4, 4, 2),
            vectors,
            options);
    String result = model.getVocabWord("Apple");
    assertEquals("Apple", result);
  }

  @Test
  public void testVectorToParamsIgnoresExtraParameters() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix vec = new SimpleMatrix(2, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", vec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(2, 5),
            new SimpleMatrix(2, 3),
            new SimpleTensor(4, 4, 2),
            wordVectors,
            options);
    int size = model.totalParamSize();
    double[] overSized = new double[size + 10];
    Arrays.fill(overSized, 1.0);
    model.vectorToParams(overSized);
    double[] afterRestore = model.paramsToVector();
    assertEquals(size, afterRestore.length);
  }

  @Test
  public void testMultipleBinaryProductionInitialization() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 2;
    options.combineClassification = false;
    options.useTensors = true;
    options.simplifiedModel = true;
    options.numHid = 3;
    options.randomWordVectors = true;
    // options.trainOptions = new RNNOptions.TrainerOptions();
    options.trainOptions.scalingForInit = 0.05;
    options.unkWord = "*UNK*";
    Tree l = new LabeledScoredTreeFactory().newLeaf("great");
    Tree r = new LabeledScoredTreeFactory().newLeaf("movie");
    Tree root = new LabeledScoredTreeFactory().newTreeNode("NP", Arrays.asList(l, r));
    List<Tree> trees = new ArrayList<>();
    trees.add(root);
    SentimentModel model = new SentimentModel(options, trees);
    assertTrue(model.binaryTransform.size() > 0);
    assertTrue(model.unaryClassification.size() > 0);
    assertTrue(model.wordVectors.containsKey("*UNK*"));
  }

  @Test
  public void testToStringIncludesClassificationSections() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    // options.langpack = new BaseLangPack();
    options.unkWord = "*UNK*";
    SimpleMatrix unary = new SimpleMatrix(2, 3);
    SimpleMatrix binary = new SimpleMatrix(2, 3);
    Map<String, SimpleMatrix> unaryClass = new HashMap<>();
    unaryClass.put("X", unary);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("NP", "VP", binary);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            unaryClass,
            vectors,
            options);
    String result = model.toString();
    assertTrue(result.contains("Binary classification matrix"));
    assertTrue(result.contains("Unary classification matrix"));
  }

  @Test
  public void testPrintParamInformationEndOfWordVector() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SimpleMatrix vec1 = new SimpleMatrix(3, 1);
    SimpleMatrix vec2 = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("w1", vec1);
    wordVectors.put("w2", vec2);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            wordVectors,
            options);
    int total = model.totalParamSize();
    model.printParamInformation(total - 1);
  }

  @Test
  public void testPrintParamInformationEmptyModel() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 1;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    model.printParamInformation(0);
  }

  @Test
  public void testGetVocabWordLowercasingMissMatchFallback() {
    RNNOptions options = new RNNOptions();
    options.lowercaseWordVectors = true;
    options.unkWord = "*UNK*";
    SimpleMatrix vec = new SimpleMatrix(2, 1);
    Map<String, SimpleMatrix> map = new HashMap<>();
    map.put("lowercase", vec);
    map.put("*UNK*", vec);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(2, 5),
            new SimpleMatrix(2, 3),
            new SimpleTensor(4, 4, 2),
            map,
            options);
    String resolved = model.getVocabWord("UPPERCASE");
    assertEquals("*UNK*", resolved);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForUnaryNodeThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.useTensors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("word");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(leaf));
    model.getTensorForNode(parent);
  }

  @Test(expected = AssertionError.class)
  public void testGetTensorForNodeInvalidNumChildrenThrows() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.useTensors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree a = new LabeledScoredTreeFactory().newLeaf("A");
    Tree b = new LabeledScoredTreeFactory().newLeaf("B");
    Tree c = new LabeledScoredTreeFactory().newLeaf("C");
    Tree tree = new LabeledScoredTreeFactory().newTreeNode("root", Arrays.asList(a, b, c));
    model.getTensorForNode(tree);
  }

  @Test(expected = AssertionError.class)
  public void testGetClassWForNodeThrowsOnInvalidChildrenSize() {
    RNNOptions options = new RNNOptions();
    options.numClasses = 3;
    options.numHid = 3;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree node = new LabeledScoredTreeFactory().newTreeNode("X", Collections.emptyList());
    model.getClassWForNode(node);
  }

  @Test
  public void testUnicodeWordVectorFallback() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.lowercaseWordVectors = false;
    options.unkWord = "*UNK*";
    options.simplifiedModel = true;
    options.combineClassification = true;
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    SimpleMatrix matrix = new SimpleMatrix(2, 1);
    vectors.put("συναισθημα", matrix);
    vectors.put("*UNK*", matrix);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(2, 5),
            new SimpleMatrix(2, 3),
            new SimpleTensor(4, 4, 2),
            vectors,
            options);
    String vocabWord = model.getVocabWord("unseen_unicode");
    assertEquals("*UNK*", vocabWord);
    SimpleMatrix vec = model.getWordVector("unseen_unicode");
    assertEquals(matrix, vec);
  }

  @Test
  public void testEmptyTreeInTrainingCausesUnknownToBeOnlyVector() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.lowercaseWordVectors = true;
    options.randomWordVectors = true;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.numClasses = 2;
    // options.trainOptions = new RNNOptions.TrainerOptions();
    options.trainOptions.scalingForInit = 0.1;
    options.unkWord = "*UNK*";
    Tree emptyTree = new LabeledScoredTreeFactory().newTreeNode("", new ArrayList<Tree>());
    List<Tree> trees = Collections.singletonList(emptyTree);
    SentimentModel model = new SentimentModel(options, trees);
    assertTrue(model.wordVectors.containsKey("*UNK*"));
    // assertEquals(2, model.wordVectors.get("*UNK*").getNumRows());
    // assertEquals(1, model.wordVectors.get("*UNK*").getNumCols());
  }

  @Test
  public void testCategoryMapReturnsSameMatrixWhenSimplified() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix matrix = new SimpleMatrix(2, 3);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("", "", matrix);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            new HashMap<>(),
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getBinaryClassification("NP", "VP");
    assertEquals(matrix, result);
  }

  @Test
  public void testUnicodeCategoryNameConvertedCorrectly() {
    RNNOptions options = new RNNOptions();
    options.simplifiedModel = false;
    // options.langpack = new BaseLangPack();
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(1, 3),
            new SimpleMatrix(1, 2),
            new SimpleTensor(2, 2, 1),
            new HashMap<>(),
            options);
    String transformed = model.basicCategory("@συναισθημα");
    assertEquals("συναισθημα", transformed);
  }

  @Test
  public void testNamedMatricesToStringIncludesLabels() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = false;
    options.simplifiedModel = false;
    // options.langpack = new BaseLangPack();
    options.unkWord = "*UNK*";
    SimpleMatrix binaryMat = new SimpleMatrix(2, 5);
    SimpleMatrix unaryMat = new SimpleMatrix(2, 3);
    SimpleMatrix wordVec = new SimpleMatrix(2, 1);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClass = TwoDimensionalMap.treeMap();
    binaryClass.put("NP", "VP", binaryMat);
    Map<String, SimpleMatrix> unary = new HashMap<>();
    unary.put("NN", unaryMat);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("Python", wordVec);
    wordVectors.put("*UNK*", wordVec);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            binaryClass,
            unary,
            wordVectors,
            options);
    String dump = model.toString();
    assertTrue(dump.contains("Binary classification matrices"));
    assertTrue(dump.contains("Unary classification matrices"));
    assertTrue(dump.contains("NP VP:"));
    assertTrue(dump.contains("NN:"));
    assertTrue(dump.contains("'Python'"));
  }

  @Test
  public void testParamsToVectorWithOnlyUnaryClassifications() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix uc = new SimpleMatrix(2, 3);
    Map<String, SimpleMatrix> unaries = new HashMap<>();
    unaries.put("", uc);
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unaries,
            wordVectors,
            options);
    double[] vec = model.paramsToVector();
    assertEquals(uc.getNumElements() + 2, vec.length);
  }

  @Test
  public void testEmptyBinaryTensorReturnsNullWhenPresentKeyIsMissing() {
    RNNOptions options = new RNNOptions();
    options.numHid = 4;
    options.useTensors = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleTensor tensor = new SimpleTensor(8, 8, 4);
    TwoDimensionalMap<String, String, SimpleTensor> binaryTensors = TwoDimensionalMap.treeMap();
    binaryTensors.put("A", "B", tensor);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            binaryTensors,
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    SimpleTensor result = model.getBinaryTensor("UNKNOWN", "ANY");
    assertNull(result);
  }

  @Test
  public void testCombinedClassificationBypassesBinaryMatrixCheck() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    Map<String, SimpleMatrix> unary = new HashMap<>();
    SimpleMatrix matrix = new SimpleMatrix(2, 4);
    unary.put("", matrix);
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            unary,
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getBinaryClassification("X", "Y");
    assertEquals(matrix, result);
  }

  @Test
  public void testRandomWordVectorRangeIsGaussianScaled() {
    Random rand = new Random(15);
    SimpleMatrix vector = SentimentModel.randomWordVector(5, rand);
    assertEquals(5, vector.getNumElements());
    assertTrue(vector.elementMaxAbs() < 1.0);
  }

  @Test
  public void testRandomClassificationMatrixBiasColumnInitialized() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.numClasses = 2;
    // options.trainOptions = new RNNOptions.TrainerOptions();
    options.trainOptions.scalingForInit = 0.1;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    options.lowercaseWordVectors = false;
    SimpleMatrix dummy = new SimpleMatrix(3, 1);
    Map<String, SimpleMatrix> vectors = new HashMap<>();
    vectors.put("*UNK*", dummy);
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(3, 7),
            new SimpleMatrix(2, 4),
            new SimpleTensor(6, 6, 3),
            vectors,
            options);
    SimpleMatrix classification = model.randomClassificationMatrix();
    assertEquals(2, classification.numRows());
    assertEquals(4, classification.numCols());
    double biasValue = classification.get(0, 3);
    assertTrue(biasValue >= 0.0 && biasValue <= 1.0);
  }

  @Test
  public void testGetWForNodeFromCategoryMappingReturnsCorrectMatrix() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.combineClassification = false;
    options.simplifiedModel = true;
    options.unkWord = "*UNK*";
    SimpleMatrix matrix = new SimpleMatrix(3, 7);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryTransform = TwoDimensionalMap.treeMap();
    binaryTransform.put("", "", matrix);
    SentimentModel model =
        new SentimentModel(
            binaryTransform,
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    Tree left = new LabeledScoredTreeFactory().newLeaf("NP");
    Tree right = new LabeledScoredTreeFactory().newLeaf("VP");
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("S", Arrays.asList(left, right));
    SimpleMatrix result = model.getWForNode(parent);
    assertEquals(matrix, result);
  }

  @Test
  public void testGetWordVectorUnkAbsentReturnsNull() {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.unkWord = "*UNK*";
    options.lowercaseWordVectors = false;
    SentimentModel model =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(2, 5),
            new SimpleMatrix(2, 3),
            new SimpleTensor(4, 4, 2),
            new HashMap<>(),
            options);
    SimpleMatrix result = model.getWordVector("doesnotexist");
    assertNull(result);
  }

  @Test
  public void testModelSerializationAndDeserialization()
      throws IOException, ClassNotFoundException {
    RNNOptions options = new RNNOptions();
    options.numHid = 2;
    options.combineClassification = true;
    options.simplifiedModel = true;
    options.numClasses = 2;
    options.unkWord = "*UNK*";
    options.lowercaseWordVectors = true;
    Map<String, SimpleMatrix> wordVectors = new HashMap<>();
    wordVectors.put("awesome", new SimpleMatrix(2, 1));
    wordVectors.put("*UNK*", new SimpleMatrix(2, 1));
    SentimentModel original =
        SentimentModel.modelFromMatrices(
            new SimpleMatrix(2, 5),
            new SimpleMatrix(2, 3),
            new SimpleTensor(4, 4, 2),
            wordVectors,
            options);
    File tempFile = File.createTempFile("sentimentModel", ".ser");
    tempFile.deleteOnExit();
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));
    out.writeObject(original);
    out.close();
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));
    SentimentModel loaded = (SentimentModel) in.readObject();
    in.close();
    assertNotNull(loaded);
    assertEquals(original.numHid, loaded.numHid);
    assertEquals(original.numClasses, loaded.numClasses);
    assertNotNull(loaded.wordVectors.get("awesome"));
  }

  @Test
  public void testPrintParamInformationBeyondParameterLength() {
    RNNOptions options = new RNNOptions();
    options.numHid = 3;
    options.simplifiedModel = true;
    options.combineClassification = true;
    options.numClasses = 2;
    options.unkWord = "*UNK*";
    SentimentModel model =
        new SentimentModel(
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            TwoDimensionalMap.treeMap(),
            new HashMap<>(),
            new HashMap<>(),
            options);
    model.printParamInformation(100);
  }

  @Test
  public void testIdentityMatrixInitializationMatchesExpected() {
    int hiddenSize = 3;
    // SentimentModel model = SentimentModel.modelFromMatrices(new SimpleMatrix(hiddenSize,
    // hiddenSize * 2 + 1), new SimpleMatrix(2, hiddenSize + 1), new SimpleTensor(hiddenSize * 2,
    // hiddenSize * 2, hiddenSize), Collections.singletonMap("", new SimpleMatrix(2, hiddenSize +
    // 1)), new HashMap<>(), new RNNOptions() {
    //
    // {
    // this.numHid = hiddenSize;
    // this.numClasses = 2;
    // this.unkWord = "*UNK*";
    // this.combineClassification = true;
    // this.simplifiedModel = true;
    // }
    // });
    SimpleMatrix expectedId = SimpleMatrix.identity(hiddenSize);
    // assertEquals(expectedId, model.identity);
  }
}
