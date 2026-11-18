package edu.stanford.nlp.sentiment;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.neural.SimpleTensor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.TwoDimensionalMap;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class SentimentModelPromptTest {

  @Test
  public void modelFromMatricesRequiresCombinedClassification() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-nocombineClassification"}, 0);
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(new double[][]{{1.0}, {2.0}}));

    try {
      SentimentModel.modelFromMatrices(new SimpleMatrix(2, 5), new SimpleMatrix(3, 3),
          new SimpleTensor(4, 4, 2), words, options);
      fail("Expected combined classification to be required");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("combineClassification"));
    }
  }

  @Test
  public void modelFromMatricesRequiresSimplifiedModel() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-nosimplifiedModel"}, 0);
    options.setOption(new String[]{"-combineClassification"}, 0);
    options.setOption(new String[]{"-nosimplifiedModel"}, 0);
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(new double[][]{{1.0}, {2.0}}));

    try {
      SentimentModel.modelFromMatrices(new SimpleMatrix(2, 5), new SimpleMatrix(3, 3),
          new SimpleTensor(4, 4, 2), words, options);
      fail("Expected the simplified model to be required");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("simplifiedModel"));
    }
  }

  @Test
  public void combinedModelUsesSingleMatricesAndNormalizesVocabulary() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "2"}, 0);
    options.setOption(new String[]{"-numClasses", "3"}, 0);
    options.setOption(new String[]{"-lowercaseWordVectors"}, 0);
    SimpleMatrix transform = new SimpleMatrix(new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}});
    SimpleMatrix classes = new SimpleMatrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
    SimpleTensor tensor = new SimpleTensor(4, 4, 2);
    Map<String, SimpleMatrix> words = new TreeMap<>();
    SimpleMatrix known = new SimpleMatrix(new double[][]{{0.25}, {0.75}});
    SimpleMatrix unknown = new SimpleMatrix(new double[][]{{-0.25}, {-0.75}});
    words.put("great", known);
    words.put("*UNK*", unknown);
    SentimentModel model = SentimentModel.modelFromMatrices(transform, classes, tensor, words, options);

    assertSame(transform, model.getBinaryTransform("NP", "VP"));
    assertSame(tensor, model.getBinaryTensor("anything", "else"));
    assertSame(classes, model.getUnaryClassification("ADJP"));
    assertSame(classes, model.getBinaryClassification("NP", "VP"));
    assertEquals("", model.basicCategory("@NP-SBJ"));
    assertEquals("great", model.getVocabWord("GrEaT"));
    assertSame(known, model.getWordVector("GREAT"));
    assertEquals("*UNK*", model.getVocabWord("missing"));
    assertSame(unknown, model.getWordVector("missing"));
  }

  @Test
  public void untiedModelUsesBasicCategoriesAndRemovesAtPrefix() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-numClasses", "2"}, 0);
    options.setOption(new String[]{"-nosimplifiedModel"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    SimpleMatrix transform = new SimpleMatrix(new double[][]{{2, 3, 4}});
    transforms.put("NP", "VP", transform);
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClasses = TwoDimensionalMap.treeMap();
    SimpleMatrix binaryClass = new SimpleMatrix(new double[][]{{5, 6}, {7, 8}});
    binaryClasses.put("NP", "VP", binaryClass);
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    SimpleMatrix unaryClass = new SimpleMatrix(new double[][]{{9, 10}, {11, 12}});
    unaryClasses.put("NN", unaryClass);
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(new double[][]{{13}}));
    SentimentModel model = new SentimentModel(transforms, tensors, binaryClasses, unaryClasses, words, options);

    assertEquals("NP", model.basicCategory("@NP-SBJ=2"));
    assertEquals("VP", model.basicCategory("VP-TMP"));
    assertSame(transform, model.getBinaryTransform("@NP-SBJ", "VP-TMP"));
    assertSame(binaryClass, model.getBinaryClassification("NP-SBJ", "VP=4"));
    assertSame(unaryClass, model.getUnaryClassification("@NN-TMP"));
    assertNull(model.getBinaryTransform("ADJP", "VP"));
  }

  @Test
  public void treeDirectedAccessSelectsBinaryAndUnaryParameters() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-numClasses", "2"}, 0);
    options.setOption(new String[]{"-nosimplifiedModel"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    SimpleMatrix transform = new SimpleMatrix(new double[][]{{1, 2, 3}});
    transforms.put("NP", "VP", transform);
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    SimpleTensor tensor = new SimpleTensor(2, 2, 1);
    tensors.put("NP", "VP", tensor);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClasses = TwoDimensionalMap.treeMap();
    SimpleMatrix binaryClass = new SimpleMatrix(new double[][]{{4, 5}, {6, 7}});
    binaryClasses.put("NP", "VP", binaryClass);
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    SimpleMatrix unaryClass = new SimpleMatrix(new double[][]{{8, 9}, {10, 11}});
    unaryClasses.put("NN", unaryClass);
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(new double[][]{{12}}));
    SentimentModel model = new SentimentModel(transforms, tensors, binaryClasses, unaryClasses, words, options);
    Tree binaryNode = Tree.valueOf("(ROOT (NP-SBJ dog) (VP-TMP runs))");
    Tree unaryNode = Tree.valueOf("(ROOT (NN-TMP dog))");

    assertSame(transform, model.getWForNode(binaryNode));
    assertSame(tensor, model.getTensorForNode(binaryNode));
    assertSame(binaryClass, model.getClassWForNode(binaryNode));
    assertSame(unaryClass, model.getClassWForNode(unaryNode));
  }

  @Test
  public void transformLookupRejectsUnaryAndLeafNodes() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    transforms.put("", "", new SimpleMatrix(1, 3));
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    tensors.put("", "", new SimpleTensor(2, 2, 1));
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("", new SimpleMatrix(5, 2));
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(1, 1));
    SentimentModel model = new SentimentModel(transforms, tensors, TwoDimensionalMap.treeMap(),
        unaryClasses, words, options);

    try {
      model.getWForNode(Tree.valueOf("(ROOT (NN dog))"));
      fail("Expected unary transforms to be rejected");
    } catch (AssertionError expected) {
      assertTrue(expected.getMessage().contains("No unary transform"));
    }
    try {
      model.getWForNode(Tree.valueOf("(dog)"));
      fail("Expected leaf transforms to be rejected");
    } catch (AssertionError expected) {
      assertTrue(expected.getMessage().contains("children size of 0"));
    }
  }

  @Test
  public void tensorLookupRejectsDisabledTensorsBeforeInspectingTree() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("", new SimpleMatrix(5, 2));
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(1, 1));
    SentimentModel model = new SentimentModel(TwoDimensionalMap.treeMap(), TwoDimensionalMap.treeMap(),
        TwoDimensionalMap.treeMap(), unaryClasses, words, options);

    try {
      model.getTensorForNode(Tree.valueOf("leaf"));
      fail("Expected disabled tensor access to fail");
    } catch (AssertionError expected) {
      assertEquals("Not using tensors", expected.getMessage());
    }
  }

  @Test
  public void classLookupRejectsLeafWhenClassifiersAreSeparate() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-nocombineClassification"}, 0);
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(1, 1));
    SentimentModel model = new SentimentModel(TwoDimensionalMap.treeMap(), TwoDimensionalMap.treeMap(),
        TwoDimensionalMap.treeMap(), new TreeMap<>(), words, options);

    try {
      model.getClassWForNode(Tree.valueOf("(leaf)"));
      fail("Expected a leaf node to be rejected");
    } catch (AssertionError expected) {
      assertTrue(expected.getMessage().contains("children size of 0"));
    }
  }

  @Test
  public void parameterVectorRoundTripsAllParameterKindsInOrder() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-numClasses", "2"}, 0);
    options.setOption(new String[]{"-nocombineClassification"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    transforms.put("", "", new SimpleMatrix(new double[][]{{1, 2, 3}}));
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    SimpleTensor tensor = new SimpleTensor(2, 2, 1);
    tensor.setSlice(0, new SimpleMatrix(new double[][]{{8, 9}, {10, 11}}));
    tensors.put("", "", tensor);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClasses = TwoDimensionalMap.treeMap();
    binaryClasses.put("", "", new SimpleMatrix(new double[][]{{4, 5}, {6, 7}}));
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("", new SimpleMatrix(new double[][]{{12, 13}, {14, 15}}));
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(new double[][]{{16}}));
    words.put("word", new SimpleMatrix(new double[][]{{17}}));
    SentimentModel model = new SentimentModel(transforms, tensors, binaryClasses, unaryClasses, words, options);

    assertEquals(17, model.totalParamSize());
    assertArrayEquals(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17},
        model.paramsToVector(), 0.0);
    double[] replacement = new double[]{-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -14, -15, -16, -17};
    model.vectorToParams(replacement);
    assertArrayEquals(replacement, model.paramsToVector(), 0.0);
    assertEquals(-9.0, model.getBinaryTensor("x", "y").getSlice(0).get(0, 1), 0.0);
    assertEquals(-17.0, model.getWordVector("word").get(0), 0.0);
  }

  @Test
  public void vectorToParamsRejectsUnusedExtraValues() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    transforms.put("", "", new SimpleMatrix(1, 3));
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("", new SimpleMatrix(5, 2));
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(1, 1));
    SentimentModel model = new SentimentModel(transforms, TwoDimensionalMap.treeMap(),
        TwoDimensionalMap.treeMap(), unaryClasses, words, options);

    try {
      model.vectorToParams(new double[15]);
      fail("Expected unused vector values to be rejected");
    } catch (AssertionError expected) {
      assertTrue(expected.getMessage().contains("entirely use"));
    }
  }

  @Test
  public void explicitConstructorInfersHiddenSizeFromWordVectors() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "0"}, 0);
    options.setOption(new String[]{"-numClasses", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    transforms.put("", "", new SimpleMatrix(3, 7));
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("", new SimpleMatrix(2, 4));
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(3, 1));
    words.put("known", new SimpleMatrix(3, 1));
    SentimentModel model = new SentimentModel(transforms, TwoDimensionalMap.treeMap(),
        TwoDimensionalMap.treeMap(), unaryClasses, words, options);

    assertEquals(35, model.totalParamSize());
    assertEquals(35, model.paramsToVector().length);
  }

  @Test
  public void trainingConstructorBuildsDeterministicLowercasedVocabulary() {
    RNNOptions firstOptions = new RNNOptions();
    firstOptions.setOption(new String[]{"-numHid", "2"}, 0);
    firstOptions.setOption(new String[]{"-numClasses", "3"}, 0);
    firstOptions.setOption(new String[]{"-randomSeed", "42"}, 0);
    firstOptions.setOption(new String[]{"-lowercaseWordVectors"}, 0);
    firstOptions.setOption(new String[]{"-nouseTensors"}, 0);
    RNNOptions secondOptions = new RNNOptions();
    secondOptions.setOption(new String[]{"-numHid", "2"}, 0);
    secondOptions.setOption(new String[]{"-numClasses", "3"}, 0);
    secondOptions.setOption(new String[]{"-randomSeed", "42"}, 0);
    secondOptions.setOption(new String[]{"-lowercaseWordVectors"}, 0);
    secondOptions.setOption(new String[]{"-nouseTensors"}, 0);
    Tree firstTree = Tree.valueOf("(2 (1 Great) (3 Movie))");
    Tree secondTree = Tree.valueOf("(4 (2 GREAT) (1 fun))");
    SentimentModel first = new SentimentModel(firstOptions, Arrays.asList(firstTree, secondTree));
    SentimentModel second = new SentimentModel(secondOptions, Arrays.asList(firstTree, secondTree));

    assertEquals("great", first.getVocabWord("GREAT"));
    assertEquals("movie", first.getVocabWord("Movie"));
    assertEquals("fun", first.getVocabWord("FUN"));
    assertEquals("*UNK*", first.getVocabWord("absent"));
    assertEquals(27, first.totalParamSize());
    assertArrayEquals(first.paramsToVector(), second.paramsToVector(), 0.0);
    assertSame(first.getClassWForNode(firstTree), first.getUnaryClassification("anything"));
  }

  @Test
  public void trainingConstructorRejectsUnknownRandomVectorSize() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "0"}, 0);

    try {
      new SentimentModel(options, Collections.singletonList(Tree.valueOf("(1 word)")));
      fail("Expected unknown random vector size to fail");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("unknown numHid"));
    }
  }

  @Test
  public void trainingConstructorRejectsUntiedModel() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-nosimplifiedModel"}, 0);

    try {
      new SentimentModel(options, Collections.singletonList(Tree.valueOf("(1 word)")));
      fail("Expected untied training initialization to fail");
    } catch (UnsupportedOperationException expected) {
      assertEquals("Not yet implemented", expected.getMessage());
    }
  }

  @Test
  public void toStringRendersNamedPluralSectionsAndWordVectors() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-numClasses", "2"}, 0);
    options.setOption(new String[]{"-nosimplifiedModel"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    transforms.put("A", "B", new SimpleMatrix(new double[][]{{1, 2, 3}}));
    transforms.put("C", "D", new SimpleMatrix(new double[][]{{4, 5, 6}}));
    TwoDimensionalMap<String, String, SimpleTensor> tensors = TwoDimensionalMap.treeMap();
    SimpleTensor tensor = new SimpleTensor(2, 2, 1);
    tensor.set(7.0);
    tensors.put("A", "B", tensor);
    TwoDimensionalMap<String, String, SimpleMatrix> binaryClasses = TwoDimensionalMap.treeMap();
    binaryClasses.put("A", "B", new SimpleMatrix(new double[][]{{8, 9}, {10, 11}}));
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("NN", new SimpleMatrix(new double[][]{{12, 13}, {14, 15}}));
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(new double[][]{{16}}));
    SentimentModel model = new SentimentModel(transforms, tensors, binaryClasses, unaryClasses, words, options);

    String rendered = model.toString();
    assertTrue(rendered.contains("Binary transform matrices\n"));
    assertTrue(rendered.contains("A B:\n"));
    assertTrue(rendered.contains("C D:\n"));
    assertTrue(rendered.contains("Binary transform tensor\n"));
    assertTrue(rendered.contains("Binary classification matrix\n"));
    assertTrue(rendered.contains("Unary classification matrix\n"));
    assertTrue(rendered.contains("NN:\n"));
    assertTrue(rendered.contains("Word vectors\n'*UNK*'\n"));
    assertTrue(rendered.contains("16.00000000"));
  }

  @Test
  public void serializationRoundTripPreservesPublicBehavior() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-numClasses", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    TwoDimensionalMap<String, String, SimpleMatrix> transforms = TwoDimensionalMap.treeMap();
    transforms.put("", "", new SimpleMatrix(new double[][]{{1, 2, 3}}));
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("", new SimpleMatrix(new double[][]{{4, 5}, {6, 7}}));
    Map<String, SimpleMatrix> words = new TreeMap<>();
    words.put("*UNK*", new SimpleMatrix(new double[][]{{8}}));
    words.put("known", new SimpleMatrix(new double[][]{{9}}));
    SentimentModel original = new SentimentModel(transforms, TwoDimensionalMap.treeMap(),
        TwoDimensionalMap.treeMap(), unaryClasses, words, options);
    Path file = Files.createTempFile("sentiment-model-", ".ser");

    original.saveSerialized(file.toString());
    SentimentModel restored = SentimentModel.loadSerialized(file.toString());
    assertArrayEquals(original.paramsToVector(), restored.paramsToVector(), 0.0);
    assertEquals(original.totalParamSize(), restored.totalParamSize());
    assertEquals("known", restored.getVocabWord("known"));
    assertEquals(8.0, restored.getWordVector("missing").get(0), 0.0);
    assertEquals(original.toString(), restored.toString());
    Files.delete(file);
  }

  @Test
  public void loadSerializedWrapsMissingFileFailure() {
    try {
      SentimentModel.loadSerialized("definitely-missing-sentiment-model-file.ser");
      fail("Expected missing model loading to fail");
    } catch (RuntimeIOException expected) {
      assertNotNull(expected.getCause());
    }
  }

  @Test
  public void nullVocabularyInputFallsBackOnlyWithoutLowercasing() {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "1"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    Map<String, SimpleMatrix> unaryClasses = new TreeMap<>();
    unaryClasses.put("", new SimpleMatrix(5, 2));
    Map<String, SimpleMatrix> words = new java.util.HashMap<>();
    SimpleMatrix unknown = new SimpleMatrix(new double[][]{{3.0}});
    words.put("*UNK*", unknown);
    SentimentModel model = new SentimentModel(TwoDimensionalMap.treeMap(), TwoDimensionalMap.treeMap(),
        TwoDimensionalMap.treeMap(), unaryClasses, words, options);

    assertEquals("*UNK*", model.getVocabWord(null));
    assertSame(unknown, model.getWordVector(null));
  }
}
