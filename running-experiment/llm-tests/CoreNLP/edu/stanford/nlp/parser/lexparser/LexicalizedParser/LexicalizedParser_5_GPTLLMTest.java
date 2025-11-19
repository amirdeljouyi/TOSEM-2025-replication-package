package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Index;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class LexicalizedParser_5_GPTLLMTest {

// @Test
//  public void testCopyLexicalizedParserCreatesDistinctInstance() {
//    Options options = new Options();
//    Lexicon lex = null;
//    BinaryGrammar bg = null;
//    UnaryGrammar ug = null;
//    DependencyGrammar dg = null;
//    LexicalizedParser original = new LexicalizedParser(lex, bg, ug, dg,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);
//    assertNotNull(copy);
//    assertNotSame(original, copy);
//    assertEquals(original.getOp().getClass(), copy.getOp().getClass());
//  }
//@Test
//  public void testParseStringsReturnsTreeWithValidInput() {
//    Options options = new Options();
//    Lexicon lex = null;
//    BinaryGrammar bg = null;
//    UnaryGrammar ug = null;
//    DependencyGrammar dg = null;
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    List<String> input = Arrays.asList("This", "is", "a", "test");
//    Tree tree = parser.parseStrings(input);
//    assertNotNull(tree);
//    assertTrue(tree.yield().size() > 0);
//  }
//@Test
//  public void testParseReturnsFallbackXTreeOnFailure() {
//    Options options = new Options();
//    Lexicon lex = null;
//    BinaryGrammar bg = null;
//    UnaryGrammar ug = null;
//    DependencyGrammar dg = null;
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    List<HasWord> words = Arrays.asList(new Word("Unparseable$^&"), new Word("~!@#"));
//    Tree tree = parser.parse(words);
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseMultipleTwoSentencesReturnsTwoTrees() {
//    Options options = new Options();
//    Lexicon lex = null;
//    BinaryGrammar bg = null;
//    UnaryGrammar ug = null;
//    DependencyGrammar dg = null;
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    List<HasWord> sentence1 = Arrays.asList(new Word("Hello"), new Word("there"));
//    List<HasWord> sentence2 = Arrays.asList(new Word("Goodbye"), new Word("now"));
//    List<List<? extends HasWord>> input = Arrays.asList(sentence1, sentence2);
//
//    List<Tree> result = parser.parseMultiple(input);
//    assertNotNull(result);
//    assertEquals(2, result.size());
//  }
//@Test
//  public void testParseMultipleMultithreadReturnsAllTrees() {
//    Options options = new Options();
//    Lexicon lex = null;
//    BinaryGrammar bg = null;
//    UnaryGrammar ug = null;
//    DependencyGrammar dg = null;
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    List<HasWord> s1 = Arrays.asList(new Word("one"), new Word("two"));
//    List<HasWord> s2 = Arrays.asList(new Word("three"), new Word("four"));
//    List<List<? extends HasWord>> input = Arrays.asList(s1, s2);
//
//    List<Tree> result = parser.parseMultiple(input, 2);
//    assertNotNull(result);
//    assertEquals(2, result.size());
//  }
//@Test
//  public void testParseTreeReturnsNullOnFailure() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    List<HasWord> sentence = Arrays.asList(new Word("?!!"), new Word("@@@"));
//    Tree result = parser.parseTree(sentence);
//    assertNull(result);
//  }
//@Test
//  public void testParserQueryReturnsCorrectType() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    ParserQuery pq = parser.parserQuery();
//    assertTrue(pq instanceof LexicalizedParserQuery);
//  }
//@Test
//  public void testSaveAndLoadSerializedParser() throws Exception {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    File tempFile = File.createTempFile("testParser", ".ser");
//    tempFile.deleteOnExit();
//    parser.saveParserToSerialized(tempFile.getAbsolutePath());
//
//    ObjectInputStream in = new ObjectInputStream(
//        new BufferedInputStream(new FileInputStream(tempFile))
//    );
//    LexicalizedParser loaded = LexicalizedParser.loadModel(in);
//    in.close();
//
//    assertNotNull(loaded);
//    assertEquals(parser.getClass(), loaded.getClass());
//  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testSaveTextFileThrowsIfRerankerSet() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    parser.reranker = mock(Reranker.class);
//    parser.saveParserToTextFile("output.txt");
//  }
//@Test(expected = RuntimeIOException.class)
//  public void testSaveParserToInvalidPathThrowsRuntimeIOException() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    parser.saveParserToSerialized("/invalid/path/parser.ser");
//  }
//@Test
//  public void testSetOptionFlagsUpdatesOptions() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    parser.setOptionFlags("-maxLength", "70");
//    assertEquals(70, parser.getOp().testOptions.maxLength);
//  }
//@Test
//  public void testGetExtraEvalsReturnsEmptyList() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<?> evals = parser.getExtraEvals();
//    assertTrue(evals.isEmpty());
//  }
//@Test
//  public void testGetParserQueryEvalsReturnsEmptyList() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<?> evals = parser.getParserQueryEvals();
//    assertTrue(evals.isEmpty());
//  }
@Test
  public void testLoadInvalidSerializedParserReturnsNull() {
    LexicalizedParser result = LexicalizedParser.getParserFromSerializedFile("non_existent_file.ser");
    assertNull(result);
  }
@Test(expected = RuntimeIOException.class)
  public void testLoadModelFromCorruptedInputStreamThrows() throws Exception {
    byte[] corruptedData = new byte[]{0x00, 0x01, 0x02};
    ByteArrayInputStream bais = new ByteArrayInputStream(corruptedData);
    ObjectInputStream ois = new ObjectInputStream(bais);
    LexicalizedParser.loadModel(ois);
  }
//@Test
//  public void testParseEmptyWordListReturnsXTree() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<HasWord> empty = new ArrayList<>();
//    Tree result = parser.parse(empty);
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testParseStringsPassesEmptyListReturnsNonNullTree() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<String> input = new ArrayList<>();
//    Tree result = parser.parseStrings(input);
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testSaveParserToTextFileAsGz() throws IOException {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(new Lexicon(options), new BinaryGrammar(), new UnaryGrammar(), null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    File tempFile = File.createTempFile("parser", ".gz");
//    tempFile.deleteOnExit();
//    parser.saveParserToTextFile(tempFile.getAbsolutePath());
//    assertTrue(tempFile.exists());
//  }
//@Test
//  public void testSaveParserToSerializedWithGzPath() throws IOException {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(new Lexicon(options), new BinaryGrammar(), new UnaryGrammar(), null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    File tempFile = File.createTempFile("parser", ".ser.gz");
//    tempFile.deleteOnExit();
//    parser.saveParserToSerialized(tempFile.getAbsolutePath());
//    assertTrue(tempFile.exists());
//  }
//@Test
//  public void testParseMultipleEmptyInputReturnsEmptyList() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<List<? extends HasWord>> input = new ArrayList<>();
//    List<Tree> result = parser.parseMultiple(input);
//    assertNotNull(result);
//    assertTrue(result.isEmpty());
//  }
//@Test(expected = IllegalArgumentException.class)
//  public void testSetOptionFlagsWithUnknownFlagThrows() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    parser.setOptionFlags("-unknownFlag");
//  }
@Test
  public void testLoadModelTextFileReturnsNullOnIOException() {
    String invalidPath = "/nonexistent/path/model.txt";
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.getParserFromTextFile(invalidPath, options);
    assertNull(parser);
  }
//@Test
//  public void testParseTreeWithNullInputReturnsNull() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    Tree result = parser.parseTree(null);
//    assertNull(result);
//  }
@Test(expected = ClassCastException.class)
  public void testLoadModelThrowsWhenWrongObjectInStream() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bos);
    out.writeObject("Not a parser");
    out.close();

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream in = new ObjectInputStream(bis);
    LexicalizedParser.loadModel(in);
  }
//@Test
//  public void testGetTreePrintIsNotNull() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    assertNotNull(parser.getTreePrint());
//  }
//@Test
//  public void testDefaultCoreNLPFlagsNonNull() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    String[] flags = parser.defaultCoreNLPFlags();
//    assertNotNull(flags);
//  }
//@Test
//  public void testRequiresTagsReturnsFalse() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    assertFalse(parser.requiresTags());
//  }
//@Test
//  public void testGetLexiconReturnsCorrectInstance() {
//    Options options = new Options();
//    Lexicon lex = new Lexicon(options);
//    LexicalizedParser parser = new LexicalizedParser(lex, null, null, null,
//       options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    assertSame(lex, parser.getLexicon());
//  }
//@Test
//  public void testParserQueryUsesRerankerWrap() {
//    Options options = new Options();
//    LexicalizedParser base = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    base.reranker = new Reranker() {
//      @Override
//      public Tree rerank(Tree gold, List<Tree> kBestParses) { return null; }
//
//      @Override
//      public List getEvals() {
//        return Collections.singletonList("MockEvalObject");
//      }
//    };
//
//    assertNotNull(base.parserQuery());
//    assertEquals(1, base.getExtraEvals().size());
//  }
@Test
  public void testLoadModelOverridesFlags() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", options, "-maxLength", "50");
    assertNotNull(parser);
    assertEquals(50, parser.getOp().testOptions.maxLength);
  }
//@Test
//  public void testParseTreeReturnsNullWhenParserFails() {
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      new Options().tlpParams.stateIndex(),
//      new Options().tlpParams.wordIndex(),
//      new Options().tlpParams.tagIndex(),
//      new Options()) {
//      @Override
//      public ParserQuery parserQuery() {
//        return new ParserQuery() {
//          @Override public boolean parse(List<? extends HasWord> sentence) { return false; }
//          @Override public Tree getBestParse() { return null; }
//        };
//      }
//    };
//
//    List<HasWord> sentence = Arrays.asList(new Word("This"), new Word("fails"));
//    Tree result = parser.parseTree(sentence);
//    assertNull(result);
//  }
//@Test
//  public void testSaveParserToTextFileStreamErrorThrows() {
//    Lexicon lex = new Lexicon(new Options()) {
//      @Override
//      public void writeData(PrintWriter out) {
//        throw new RuntimeException("Simulated failure");
//      }
//    };
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(lex, new BinaryGrammar(), new UnaryGrammar(), null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    try {
//      File file = new File(System.getProperty("java.io.tmpdir"), "fail_test.txt");
//      parser.saveParserToTextFile(file.getAbsolutePath());
//      fail("Expected RuntimeIOException");
//    } catch (RuntimeIOException e) {
//      assertTrue(e.getMessage() != null || e.getCause() != null);
//    }
//  }
@Test
  public void testLoadModelUsesDefaultPathIfPropNotSet() {
    System.clearProperty("edu.stanford.nlp.SerializedLexicalizedParser");
    LexicalizedParser lp = LexicalizedParser.loadModel();
    assertNotNull(lp);
  }
@Test
  public void testLoadModelFromNullObjectReturnsNull() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(new String("Wrong type"));
    oos.close();
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(in);

    try {
      LexicalizedParser.loadModel(ois);
      fail("Expected ClassCastException");
    } catch (ClassCastException e) {
      assertTrue(e.getMessage().contains("Wanted LexicalizedParser"));
    }
  }
//@Test
//  public void testParseMultipleWithSingleEmptySentence() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<HasWord> emptySentence = Collections.emptyList();
//    List<List<? extends HasWord>> input = Collections.singletonList(emptySentence);
//    List<Tree> result = parser.parseMultiple(input);
//    assertNotNull(result);
//    assertEquals(1, result.size());
//    assertEquals("X", result.get(0).label().value());
//  }
//@Test
//  public void testParseMultipleWithSingleNullSentence() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<List<? extends HasWord>> input = Collections.singletonList(null);
//    List<Tree> result = parser.parseMultiple(input);
//    assertNotNull(result);
//    assertEquals(1, result.size());
//    assertEquals("X", result.get(0).label().value());
//  }
@Test
  public void testLoadNonexistentModelFileFailsWithLoggedWarning() {
    LexicalizedParser parser = LexicalizedParser.getParserFromSerializedFile("nonexistent-model-path.ser.gz");
    assertNull(parser);
  }
@Test
  public void testLoadModelUsesSystemPropertyWhenSet() {
    System.setProperty("edu.stanford.nlp.SerializedLexicalizedParser", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    LexicalizedParser parser = LexicalizedParser.loadModel();
    System.clearProperty("edu.stanford.nlp.SerializedLexicalizedParser");
    assertNotNull(parser);
  }
//@Test
//  public void testSaveParserToSerializedGzStreamValid() throws IOException {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//        new Lexicon(options), new BinaryGrammar(), new UnaryGrammar(), null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    File tempGz = File.createTempFile("testparser", ".ser.gz");
//    tempGz.deleteOnExit();
//    parser.saveParserToSerialized(tempGz.getAbsolutePath());
//    assertTrue(tempGz.exists() && tempGz.length() > 0);
//  }
//@Test(expected = RuntimeIOException.class)
//  public void testSaveParserToTextFileThrowsIfOutputNotWritable() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//        new Lexicon(options), new BinaryGrammar(), new UnaryGrammar(), null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    String fakePath = "/invalid/path/output.txt";
//    parser.saveParserToTextFile(fakePath);
//  }
//@Test
//  public void testSetOptionFlagsEmptyDoesNotCrash() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    parser.setOptionFlags();
//    assertNotNull(parser.getOp());
//  }
//@Test
//  public void testParseNullInputStringsReturnsXTree() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    Tree tree = parser.parseStrings(null);
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseSingleWord() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<HasWord> sentence = Arrays.asList(new Word("Hello"));
//    Tree result = parser.parse(sentence);
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
@Test
  public void testLoadModelWithNoFlagsSucceeds() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    assertNotNull(parser);
  }
//@Test
//  public void testParseWithNullListReturnsXTree() {
//    Options opts = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        opts.tlpParams.stateIndex(), opts.tlpParams.wordIndex(), opts.tlpParams.tagIndex(), opts);
//    Tree result = parser.parse(null);
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
@Test(expected = RuntimeException.class)
  public void testLoadModelObjectStreamWithNullLineRaisesException() throws Exception {
    String content = "";
    File tempTxt = File.createTempFile("empty_model", ".txt");
    FileWriter writer = new FileWriter(tempTxt);
    writer.write(content);
    writer.close();

    BufferedReader reader = new BufferedReader(new FileReader(tempTxt));
    String line = reader.readLine();
    if (line == null) {
      throw new RuntimeException(tempTxt.getAbsolutePath() + ": expecting BEGIN block; got end of file.");
    }
    reader.close();
  }
@Test
  public void testLoadModelSkipsToTextFileIfSerializedFails() {
    Options opts = new Options();
    LexicalizedParser parser = LexicalizedParser.getParserFromFile("not_a_real_model_path.fake", opts);
    assertNull(parser);
  }
//@Test
//  public void testSetOptionFlagsAppliesMultipleValidFlags() {
//    Options opts = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      opts.tlpParams.stateIndex(), opts.tlpParams.wordIndex(), opts.tlpParams.tagIndex(), opts);
//    parser.setOptionFlags("-maxLength", "42", "-outputFormat", "penn");
//    assertEquals(42, parser.getOp().testOptions.maxLength);
//    assertEquals("penn", parser.getOp().testOptions.outputFormat);
//  }
//@Test
//  public void testParserQueryReturnsRerankingWhenRerankerSet() {
//    Options opts = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      opts.tlpParams.stateIndex(), opts.tlpParams.wordIndex(), opts.tlpParams.tagIndex(), opts);
//
//    parser.reranker = new Reranker() {
//      @Override
//      public Tree rerank(Tree gold, List<Tree> kBestParses) {
//        return null;
//      }
//
//      @Override
//      public List getEvals() {
//        return Collections.singletonList("dummyEval");
//      }
//    };
//
//    ParserQuery query = parser.parserQuery();
//    assertNotNull(query);
//    assertNotNull(parser.getExtraEvals());
//    assertEquals(1, parser.getExtraEvals().size());
//  }
//@Test
//  public void testSaveParserToTextFileWritesOutputFile() throws Exception {
//    Options opts = new Options();
//    Lexicon lex = new Lexicon(opts);
//    BinaryGrammar bg = new BinaryGrammar();
//    UnaryGrammar ug = new UnaryGrammar();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, null,
//        opts.tlpParams.stateIndex(), opts.tlpParams.wordIndex(), opts.tlpParams.tagIndex(), opts);
//
//    File tempFile = File.createTempFile("test_parser", ".txt");
//    tempFile.deleteOnExit();
//
//    parser.saveParserToTextFile(tempFile.getAbsolutePath());
//
//    assertTrue(tempFile.exists());
//    assertTrue(tempFile.length() > 0);
//  }
//@Test(expected = RuntimeIOException.class)
//  public void testSaveParserToSerializedThrowsOnBadPath() {
//    Options opts = new Options();
//    Lexicon lex = new Lexicon(opts);
//    LexicalizedParser parser = new LexicalizedParser(lex, null, null, null,
//        opts.tlpParams.stateIndex(), opts.tlpParams.wordIndex(), opts.tlpParams.tagIndex(), opts);
//
//    parser.saveParserToSerialized("/invalid/!bad/path/parser.ser");
//  }
//@Test
//  public void testParseMultipleReturnsEmptyListIfInputEmpty() {
//    Options opts = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        opts.tlpParams.stateIndex(), opts.tlpParams.wordIndex(), opts.tlpParams.tagIndex(), opts);
//
//    List<Tree> parsed = parser.parseMultiple(Collections.emptyList());
//
//    assertNotNull(parsed);
//    assertTrue(parsed.isEmpty());
//  }
//@Test(expected = RuntimeException.class)
//  public void testConfirmBeginBlockWithInvalidLineThrows() {
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        new Options().tlpParams.stateIndex(),
//        new Options().tlpParams.wordIndex(),
//        new Options().tlpParams.tagIndex(),
//        new Options());
//
//    String invalidLine = "INVALID";
//    String fileName = "mockFile.txt";
//
//    java.lang.reflect.Method method;
//    try {
//      method = LexicalizedParser.class.getDeclaredMethod("confirmBeginBlock", String.class, String.class);
//      method.setAccessible(true);
//      method.invoke(null, fileName, invalidLine);
//    } catch (Exception e) {
//      throw new RuntimeException(e.getCause());
//    }
//  }
//@Test
//  public void testParseTreeReturnsBestParseTree() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(),
//        options.tlpParams.wordIndex(),
//        options.tlpParams.tagIndex(),
//        options);
//
//    LexicalizedParser parserSpy = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(),
//        options.tlpParams.wordIndex(),
//        options.tlpParams.tagIndex(),
//        options) {
//      @Override
//      public ParserQuery parserQuery() {
//        return new ParserQuery() {
//          @Override
//          public boolean parse(List<? extends HasWord> sentence) { return true; }
//
//          @Override
//          public Tree getBestParse() {
//            Tree tree = new Tree() {
//              @Override public Tree[] children() { return new Tree[0]; }
//
//              @Override public String label() { return "testLabel"; }
//
//              @Override public String value() { return "test"; }
//            };
//            return tree;
//          }
//        };
//      }
//    };
//
//    List<HasWord> sentence = Collections.singletonList(new Word("testing"));
//    Tree tree = parserSpy.parseTree(sentence);
//    assertNotNull(tree);
//  }
//@Test
//  public void testParseTreeReturnsTreeWhenSuccess() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options) {
//
//      @Override
//      public ParserQuery parserQuery() {
//        return new ParserQuery() {
//          public boolean parse(List<? extends HasWord> sentence) { return true; }
//          public Tree getBestParse() {
//            TreeFactory tf = new LabeledScoredTreeFactory();
//            return tf.newTreeNode("ROOT", Collections.emptyList());
//          }
//        };
//      }
//    };
//
//    List<HasWord> input = Arrays.asList(new Word("hello"));
//    Tree result = parser.parseTree(input);
//    assertNotNull(result);
//    assertEquals("ROOT", result.label().value());
//  }
@Test
  public void testLoadModelFallbackToTextAfterStreamCorruption() {
    String dummyPath = "src/test/resources/dummyTextFileThatDoesNotExist.ser";
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.loadModel(dummyPath, options, "-maxLength", "20");
    assertNull(parser);
  }
//@Test
//  public void testToVerifyTreebankLanguagePackIsCorrectType() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options);
//
//    TreebankLanguagePack tlp = parser.treebankLanguagePack();
//    assertTrue(tlp instanceof PennTreebankLanguagePack);
//  }
//@Test
//  public void testParseReturnsTreeEvenOnExceptionInsideParserQuery() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options) {
//      @Override
//      public ParserQuery parserQuery() {
//        throw new RuntimeException("forced fail");
//      }
//    };
//
//    List<HasWord> input = Arrays.asList(new Word("fallback"), new Word("test"));
//    Tree tree = parser.parse(input);
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testSaveToTextFileWontWriteIfRerankerIsSet() {
//    Options options = new Options();
//    Lexicon lex = new Lexicon(options);
//    BinaryGrammar bg = new BinaryGrammar();
//    UnaryGrammar ug = new UnaryGrammar();
//
//    LexicalizedParser parser = new LexicalizedParser(
//      lex, bg, ug, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options
//    );
//
//    parser.reranker = new Reranker() {
//      public Tree rerank(Tree gold, List<Tree> kbest) { return null; }
//      public List getEvals() { return Collections.emptyList(); }
//    };
//
//    try {
//      parser.saveParserToTextFile("tempOut.txt");
//      fail("Expected exception for reranker not null");
//    } catch (UnsupportedOperationException e) {
//      assertTrue(e.getMessage().toLowerCase().contains("rerankers"));
//    }
//  }
@Test
  public void testLoadModelFromEmptyStreamThrowsIOException() throws Exception {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
    ObjectInputStream ois = new ObjectInputStream(bais);
    try {
      LexicalizedParser.loadModel(ois);
      fail("Expected IOException");
    } catch (RuntimeIOException e) {
      assertTrue(e.getCause() instanceof EOFException);
    }
  }
//@Test
//  public void testParseMultipleThrowsWhenNullElementInInput() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options
//    );
//
//    List<List<? extends HasWord>> sentences = Arrays.asList(
//      Arrays.asList(new Word("hello")),
//      null,
//      Arrays.asList(new Word("world")));
//
//    List<Tree> trees = parser.parseMultiple(sentences);
//    assertEquals(3, trees.size());
//    assertEquals("X", trees.get(1).label().value());
//  }
//@Test
//  public void testSetUnknownFlagsSafelyIgnoresInvalidFlag() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options
//    );
//
//    try {
//      parser.setOptionFlags("-unknownOption", "-maxLength", "55");
//    } catch (IllegalArgumentException e) {
//      assertTrue(e.getMessage().contains("-unknownOption"));
//    }
//  }
//@Test
//  public void testParseWithSingleEmptyWordYieldsEmptyYieldTree() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options);
//
//    List<HasWord> sentence = Arrays.asList(new Word(""));
//    Tree tree = parser.parse(sentence);
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseTreeReturnsNullIfProvidedSentenceIsNull() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      options.tlpParams.stateIndex(),
//      options.tlpParams.wordIndex(),
//      options.tlpParams.tagIndex(),
//      options);
//
//    Tree tree = parser.parseTree(null);
//    assertNull(tree);
//  }
//@Test
//  public void testParseMultipleReturnsEmptyListForNullInputList() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    List<Tree> results = parser.parseMultiple(null);
//    assertNotNull(results);
//    assertTrue(results.isEmpty());
//  }
//@Test
//  public void testSetOptionFlagsIgnoresUnknownWhenAllowingPartialParse() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    try {
//      parser.setOptionFlags("-nonexistentFlag");
//      fail("Expected IllegalArgumentException");
//    } catch (IllegalArgumentException e) {
//      assertTrue(e.getMessage().contains("nonexistentFlag"));
//    }
//  }
//@Test
//  public void testSaveParserToSerializedCreatesGzFileSuccessfully() throws Exception {
//    Options options = new Options();
//    Lexicon lex = new Lexicon(options);
//    BinaryGrammar bg = new BinaryGrammar();
//    UnaryGrammar ug = new UnaryGrammar();
//    LexicalizedParser parser = new LexicalizedParser(
//        lex, bg, ug, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options
//    );
//
//    File file = File.createTempFile("parserTest", ".ser.gz");
//    file.deleteOnExit();
//    parser.saveParserToSerialized(file.getAbsolutePath());
//
//    assertTrue(file.exists());
//    assertTrue(file.length() > 0);
//  }
@Test
  public void testLoadModelWithCorruptedGzStreamLogsErrorAndReturnsNull() throws IOException {
    File corruptedFile = File.createTempFile("corrupted", ".ser.gz");
    corruptedFile.deleteOnExit();
    FileOutputStream fos = new FileOutputStream(corruptedFile);
    fos.write("Not a valid gz stream".getBytes());
    fos.close();

    LexicalizedParser result = LexicalizedParser.getParserFromSerializedFile(corruptedFile.getAbsolutePath());

    assertNull(result);  
  }
@Test
  public void testLoadModelFromObjectInputStreamWithWrongTypeThrowsClassCast() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(buffer);
    out.writeObject("This is not a LexicalizedParser");
    out.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    try {
      LexicalizedParser.loadModel(ois);
      fail("Expected ClassCastException");
    } catch (ClassCastException e) {
      assertTrue(e.getMessage().contains("Wanted LexicalizedParser"));
    }
  }
//@Test
//  public void testParseWithNoWordsReturnsTree() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    List<HasWord> emptyInput = Collections.emptyList();
//    Tree tree = parser.parse(emptyInput);
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseSingleHasWordReturnsTree() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//
//    List<HasWord> input = Arrays.asList(new Word("Hello"));
//    Tree tree = parser.parse(input);
//    assertNotNull(tree);
//  }
//@Test
//  public void testDefaultParserLocationValidWhenEnvUnset() {
//    System.clearProperty("edu.stanford.nlp.SerializedLexicalizedParser");
//    String expected = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
//    assertEquals(expected, LexicalizedParser.DEFAULT_PARSER_LOC);
//  }
//@Test
//  public void testParserQueryEvalsIsEmptyByDefault() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    assertTrue(parser.getParserQueryEvals().isEmpty());
//  }
//@Test
//  public void testRerankerEvalsPropagated() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//        options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    parser.reranker = new Reranker() {
//      public Tree rerank(Tree gold, List<Tree> kBestParses) { return null; }
//      public List getEvals() { return Arrays.asList("Eval1", "Eval2"); }
//    };
//    assertEquals(2, parser.getExtraEvals().size());
//  }
//@Test
//  public void testParserQueryCreatedCorrectlyWithoutReranker() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
//      options.tlpParams.stateIndex(), options.tlpParams.wordIndex(), options.tlpParams.tagIndex(), options);
//    ParserQuery query = parser.parserQuery();
//    assertNotNull(query);
//    assertTrue(query instanceof LexicalizedParserQuery);
//  }
}