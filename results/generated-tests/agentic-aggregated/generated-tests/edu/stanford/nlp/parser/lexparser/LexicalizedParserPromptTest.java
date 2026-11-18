package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.trees.CompositeTreeTransformer;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Triple;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LexicalizedParserPromptTest {

  @Test
  public void constructorExposesLanguageConfigurationAndBasicContract() {
    Options options = new Options();
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options);

    assertSame(options, parser.getOp());
    assertNotNull(parser.getTLPParams());
    assertNotNull(parser.treebankLanguagePack());
    assertNotNull(parser.defaultCoreNLPFlags());
    assertFalse(parser.requiresTags());
    assertNull(parser.getLexicon());
  }

  @Test
  public void copyCreatesDistinctParserWithSamePublicConfiguration() {
    Options options = new Options();
    LexicalizedParser original = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options);

    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);

    assertNotSame(original, copy);
    assertSame(original.getOp(), copy.getOp());
    assertSame(original.getTLPParams(), copy.getTLPParams());
    assertSame(original.treebankLanguagePack(), copy.treebankLanguagePack());
    assertSame(original.getLexicon(), copy.getLexicon());
  }

  @Test
  public void emptyEvaluationCollectionsAreImmutableAndIndependentOfParsing() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());

    assertTrue(parser.getExtraEvals().isEmpty());
    assertTrue(parser.getParserQueryEvals().isEmpty());
    try {
      parser.getExtraEvals().add(null);
      fail("Expected an immutable empty evaluation list");
    } catch (UnsupportedOperationException expected) {
      assertTrue(parser.getParserQueryEvals().isEmpty());
    }
  }

  @Test
  public void getTreePrintHonorsAValidRuntimeOutputOption() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());

    parser.setOptionFlags("-outputFormat", "oneline");

    assertNotNull(parser.getTreePrint());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setOptionFlagsRejectsUnknownFlag() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());

    parser.setOptionFlags("-definitelyNotAParserFlag");
  }

  @Test
  public void loadModelFromObjectStreamReturnsSerializedParser() throws Exception {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(bytes);
    output.writeObject(parser);
    output.close();

    LexicalizedParser loaded = LexicalizedParser.loadModel(
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())));

    assertNotSame(parser, loaded);
    assertEquals(parser.getTLPParams().getClass(), loaded.getTLPParams().getClass());
    assertFalse(loaded.requiresTags());
  }

  @Test
  public void loadModelFromObjectStreamRejectsWrongObjectType() throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(bytes);
    output.writeObject("not a parser");
    output.close();

    try {
      LexicalizedParser.loadModel(new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())));
      fail("Expected the serialized object type to be checked");
    } catch (ClassCastException expected) {
      assertTrue(expected.getMessage().contains("java.lang.String"));
    }
  }

  @Test
  public void loadModelFromObjectStreamWrapsPrematureEndOfStream() throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(bytes);
    output.close();

    try {
      LexicalizedParser.loadModel(new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())));
      fail("Expected stream failure");
    } catch (RuntimeIOException expected) {
      assertNotNull(expected.getCause());
    }
  }

  @Test
  public void serializedFileRoundTripPreservesParserConfiguration() throws Exception {
    Path file = Files.createTempFile("lexicalized-parser-", ".ser");
    try {
      Options options = new Options();
      LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
          new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options);
      parser.saveParserToSerialized(file.toString());

      LexicalizedParser loaded = LexicalizedParser.getParserFromSerializedFile(file.toString());

      assertNotNull(loaded);
      assertEquals(options.getClass(), loaded.getOp().getClass());
      assertEquals(parser.treebankLanguagePack().getClass(), loaded.treebankLanguagePack().getClass());
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void saveSerializedWrapsFileSystemFailure() throws Exception {
    Path directory = Files.createTempDirectory("lexicalized-parser-dir-");
    try {
      LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
          new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());
      try {
        parser.saveParserToSerialized(directory.toString());
        fail("Expected writing to a directory to fail");
      } catch (RuntimeIOException expected) {
        assertNotNull(expected.getCause());
      }
    } finally {
      Files.deleteIfExists(directory);
    }
  }

  @Test
  public void missingSerializedFileReturnsNullAfterLoadFailure() throws Exception {
    Path missing = Files.createTempFile("missing-parser-", ".ser");
    Files.delete(missing);

    assertNull(LexicalizedParser.getParserFromSerializedFile(missing.toString()));
  }

  @Test
  public void serializedPathOverloadsAcceptVarargsListAndExplicitOptions() throws Exception {
    Path file = Files.createTempFile("lexicalized-parser-overloads-", ".ser");
    try {
      LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
          new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());
      parser.saveParserToSerialized(file.toString());

      LexicalizedParser fromVarargs = LexicalizedParser.loadModel(
          file.toString(), "-outputFormat", "oneline");
      LexicalizedParser fromList = LexicalizedParser.loadModel(
          file.toString(), Arrays.asList("-outputFormat", "oneline"));
      LexicalizedParser fromExplicitOptions = LexicalizedParser.loadModel(
          file.toString(), new Options(), "-outputFormat", "oneline");

      assertNotNull(fromVarargs.getTreePrint());
      assertNotNull(fromList.getTreePrint());
      assertNotNull(fromExplicitOptions.getTreePrint());
      assertEquals(fromVarargs.getTLPParams().getClass(), fromList.getTLPParams().getClass());
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void corruptedSerializedFileReturnsNullForTextFallback() throws Exception {
    Path file = Files.createTempFile("lexicalized-parser-corrupt-", ".ser");
    try {
      Files.write(file, Arrays.asList("this is a text grammar candidate"));

      assertNull(LexicalizedParser.getParserFromSerializedFile(file.toString()));
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void parseFailureFallsBackToXTreeAndKeepsProvidedTags() {
    Options options = new Options();
    options.setOptions("-PCFG");
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options);

    Tree result = parser.parse(Arrays.asList(new TaggedWord("Blue", "JJ"), new Word("sky")));

    assertEquals("(X (JJ Blue) (XX sky))", result.toString());
  }

  @Test
  public void parseStringsConvertsStringsAndFallsBackWithoutLosingOrder() {
    Options options = new Options();
    options.setOptions("-PCFG");
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options);

    Tree result = parser.parseStrings(Arrays.asList("one", "two", "three"));

    assertEquals("(X (XX one) (XX two) (XX three))", result.toString());
  }

  @Test(expected = NullPointerException.class)
  public void parseStringsRejectsNullListBeforeStartingParser() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());

    parser.parseStrings(null);
  }

  @Test
  public void parseMultipleSequentialPreservesSentenceOrder() {
    Options options = new Options();
    options.setOptions("-PCFG");
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options);
    List<List<Word>> sentences = Arrays.asList(
        Collections.singletonList(new Word("first")),
        Arrays.asList(new Word("second"), new Word("sentence")));

    List<Tree> results = parser.parseMultiple(sentences);

    assertEquals(2, results.size());
    assertEquals("(X (XX first))", results.get(0).toString());
    assertEquals("(X (XX second) (XX sentence))", results.get(1).toString());
  }

  @Test
  public void parseMultipleParallelPreservesSentenceOrderAndHandlesEmptySentence() {
    Options options = new Options();
    options.setOptions("-PCFG");
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options);
    List<List<Word>> sentences = Arrays.asList(
        Arrays.asList(new Word("alpha"), new Word("beta")),
        Collections.emptyList(),
        Collections.singletonList(new Word("omega")));

    List<Tree> results = parser.parseMultiple(sentences, 2);

    assertEquals(3, results.size());
    assertEquals("(X (XX alpha) (XX beta))", results.get(0).toString());
    assertEquals("X", results.get(1).toString());
    assertEquals("(X (XX omega))", results.get(2).toString());
  }

  @Test
  public void parseMultipleOnNoSentencesReturnsEmptyListsInBothModes() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options());

    assertTrue(parser.parseMultiple(Collections.emptyList()).isEmpty());
    assertTrue(parser.parseMultiple(Collections.emptyList(), 2).isEmpty());
  }

  @Test
  public void parserQueryFactoriesProduceFreshQueriesForTrainedGrammar() {
    Options options = new Options();
    options.setOptions("-PCFG");
    MemoryTreebank treebank = new MemoryTreebank();
    treebank.add(Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBD slept)) (. .)))"));
    treebank.add(Tree.valueOf("(ROOT (S (NP (DT A) (NN dog)) (VP (VBD ran)) (. .)))"));
    LexicalizedParser parser = LexicalizedParser.trainFromTreebank(treebank, options);

    ParserQuery first = parser.parserQuery();
    ParserQuery second = parser.parserQuery();
    LexicalizedParserQuery specialized = parser.lexicalizedParserQuery();

    assertNotNull(first);
    assertNotNull(second);
    assertNotNull(specialized);
    assertNotSame(first, second);
    assertNotSame(first, specialized);
  }

  @Test
  public void trainedParserParsesKnownSentenceThroughThreeEntryPoints() {
    Options options = new Options();
    options.setOptions("-PCFG");
    MemoryTreebank treebank = new MemoryTreebank();
    treebank.add(Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBD slept)) (. .)))"));
    treebank.add(Tree.valueOf("(ROOT (S (NP (DT A) (NN dog)) (VP (VBD ran)) (. .)))"));
    LexicalizedParser parser = LexicalizedParser.trainFromTreebank(treebank, options);
    List<Word> sentence = Arrays.asList(new Word("The"), new Word("cat"),
        new Word("slept"), new Word("."));

    Tree parsed = parser.parse(sentence);
    Tree nullableParse = parser.parseTree(sentence);
    Tree stringParse = parser.parseStrings(Arrays.asList("The", "cat", "slept", "."));

    assertNotNull(parsed);
    assertNotNull(nullableParse);
    assertNotNull(stringParse);
    assertEquals("[The, cat, slept, .]", parsed.yieldWords().toString());
    assertEquals(parsed.toString(), nullableParse.toString());
    assertEquals(parsed.toString(), stringParse.toString());
    assertNotEquals("X", parsed.value());
    assertNull(parser.parseTree(Collections.emptyList()));
  }

  @Test
  public void textGrammarRoundTripCanParseWithLoadedGrammar() throws Exception {
    Path file = Files.createTempFile("lexicalized-parser-grammar-", ".txt");
    try {
      Options options = new Options();
      options.setOptions("-PCFG");
      MemoryTreebank treebank = new MemoryTreebank();
      treebank.add(Tree.valueOf("(ROOT (S (NP (NNP Alice)) (VP (VBZ smiles)) (. .)))"));
      treebank.add(Tree.valueOf("(ROOT (S (NP (NNP Bob)) (VP (VBZ laughs)) (. .)))"));
      LexicalizedParser trained = LexicalizedParser.trainFromTreebank(treebank, options);
      trained.saveParserToTextFile(file.toString());

      LexicalizedParser loaded = LexicalizedParser.getParserFromFile(file.toString(), new Options());
      Tree parsed = loaded.parseStrings(Arrays.asList("Alice", "smiles", "."));

      assertNotNull(loaded.getLexicon());
      assertEquals("[Alice, smiles, .]", parsed.yieldWords().toString());
      assertNotEquals("X", parsed.value());
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void trainTransformerBinarizesWithoutChangingTerminalYield() {
    Options options = new Options();
    Tree original = Tree.valueOf("(ROOT (S (NP (DT The) (JJ small) (NN cat)) (VP (VBD slept)) (. .)))");

    TreeAnnotatorAndBinarizer binarizer = LexicalizedParser.buildTrainBinarizer(options);
    CompositeTreeTransformer transformer = LexicalizedParser.buildTrainTransformer(options, binarizer);
    Tree transformed = transformer.transformTree(original.deepCopy());

    assertNotNull(binarizer);
    assertNotNull(transformer);
    assertEquals("[The, small, cat, slept, .]", original.yieldWords().toString());
    assertEquals("[The, small, cat, slept, ., .$.]", transformed.yieldWords().toString());
    assertTrue(transformed.toString().contains("@"));
  }

  @Test
  public void annotatedTreebankMethodTransformsPrimarySecondaryAndTuneSets() {
    Options options = new Options();
    MemoryTreebank primary = new MemoryTreebank();
    MemoryTreebank secondary = new MemoryTreebank();
    MemoryTreebank tune = new MemoryTreebank();
    primary.add(Tree.valueOf("(ROOT (S (NP (PRP She)) (VP (VBZ sings)) (. .)))"));
    secondary.add(Tree.valueOf("(ROOT (S (NP (PRP He)) (VP (VBZ runs)) (. .)))"));
    tune.add(Tree.valueOf("(ROOT (S (NP (PRP It)) (VP (VBZ works)) (. .)))"));

    Triple<Treebank, Treebank, Treebank> result =
        LexicalizedParser.getAnnotatedBinaryTreebankFromTreebank(primary, secondary, tune, options);

    assertEquals(1, result.first().size());
    assertEquals(1, result.second().size());
    assertEquals(1, result.third().size());
    assertEquals("[She, sings, ., .$.]", result.first().iterator().next().yieldWords().toString());
    assertEquals("[He, runs, ., .$.]", result.second().iterator().next().yieldWords().toString());
    assertEquals("[It, works, ., .$.]", result.third().iterator().next().yieldWords().toString());
  }

  @Test
  public void mainWithNoArgumentsReturnsNormally() {
    LexicalizedParser.main(new String[0]);
  }
}
