package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.parser.common.ParserUtils;
import edu.stanford.nlp.parser.metrics.Eval;
import edu.stanford.nlp.parser.metrics.ParserQueryEval;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.ReflectionLoading;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LexicalizedParser_4_GPTLLMTest {

// @Test
//  public void testParseStringsWithValidInput() {
//    Options options = new Options();
////    Lexicon lex = new DummyLexicon();
////    BinaryGrammar bg = new DummyBinaryGrammar();
////    UnaryGrammar ug = new DummyUnaryGrammar();
////    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    stateIndex.add("S");
//    Index<String> wordIndex = new HashIndex<>();
//    wordIndex.add("This");
//    Index<String> tagIndex = new HashIndex<>();
//    tagIndex.add("NN");
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<String> sentence = Arrays.asList("This", "is", "a", "test");
//    Tree tree = parser.parseStrings(sentence);
//
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseStringsWithEmptyInput() {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<String> sentence = Collections.emptyList();
//    Tree tree = parser.parseStrings(sentence);
//
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseListReturnsXTreeOnFailure() {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<HasWord> lst = Arrays.asList(new Word("invalid"));
//    Tree tree = parser.parse(lst);
//
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseMultipleTwoSentences() {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<HasWord> sentence1 = Arrays.asList(new Word("The"), new Word("dog"));
//    List<HasWord> sentence2 = Arrays.asList(new Word("A"), new Word("cat"), new Word("runs"));
//    List<List<HasWord>> input = Arrays.asList(sentence1, sentence2);
//    List<Tree> result = parser.parseMultiple(input);
//
//    assertEquals(2, result.size());
//    assertNotNull(result.get(0));
//    assertNotNull(result.get(1));
//  }
//@Test
//  public void testParseTreeReturnsNullOnFailure() {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<HasWord> words = Arrays.asList(new Word("no"), new Word("parse"));
//    Tree tree = parser.parseTree(words);
//
//    assertNull(tree);
//  }
//@Test
//  public void testSetOptionFlagsPropagatesMaxLength() {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    parser.setOptionFlags("-maxLength", "55");
//
//    assertEquals(55, parser.getOp().testOptions.maxLength);
//  }
//@Test
//  public void testGetTreePrintReturnsNotNull() {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    TreePrint tp = parser.getTreePrint();
//
//    assertNotNull(tp);
//  }
//@Test
//  public void testGetExtraEvalsReturnsEmptyWhenRerankerIsNull() {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<?> evals = parser.getExtraEvals();
//
//    assertTrue(evals.isEmpty());
//  }
//@Test
//  public void testSerializationAndDeserialization() throws Exception {
//    Options options = new Options();
//    Lexicon lex = new DummyLexicon();
//    BinaryGrammar bg = new DummyBinaryGrammar();
//    UnaryGrammar ug = new DummyUnaryGrammar();
//    DependencyGrammar dg = new DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    stateIndex.add("X");
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser original = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//    ObjectOutputStream oos = new ObjectOutputStream(bos);
//    oos.writeObject(original);
//    oos.close();
//
//    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
//    ObjectInputStream ois = new ObjectInputStream(bis);
//    LexicalizedParser deserialized = LexicalizedParser.loadModel(ois);
//
//    assertNotNull(deserialized);
//  }
//@Test
//  public void testParseMultipleWithEmptyInputReturnsEmptyList() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<List<HasWord>> emptyInput = Collections.emptyList();
//    List<Tree> output = parser.parseMultiple(emptyInput);
//    assertNotNull(output);
//    assertTrue(output.isEmpty());
//  }
//@Test
//  public void testParseTreeWithNullListReturnsNull() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    Tree result = parser.parseTree(null);
//    assertNull(result);
//  }
//@Test
//  public void testSetOptionFlagsWithInvalidFlagThrowsException() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    boolean thrown = false;
//    try {
//      parser.setOptionFlags("-badUnknownOption");
//    } catch (IllegalArgumentException e) {
//      thrown = true;
//    }
//    assertTrue(thrown);
//  }
//@Test(expected = RuntimeException.class)
//  public void testLoadModel_ObjectInputStreamWithIncompatibleObjectThrowsException() throws Exception {
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//    ObjectOutputStream out = new ObjectOutputStream(bos);
//    out.writeObject("Not a Parser");
//    out.close();
//
//    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
//    ObjectInputStream in = new ObjectInputStream(bis);
//    LexicalizedParser.loadModel(in);
//  }
//@Test
//  public void testCopyLexicalizedParserHandlesNullReranker() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//    parser.reranker = null;
//
//    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(parser);
//    assertNotNull(copy);
//    assertNull(copy.reranker);
//  }
//@Test(expected = RuntimeIOException.class)
//  public void testSaveParserToSerializedThrowsIOException() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    String invalidPath = "/invalid_dir/invalid_file.ser";
//    parser.saveParserToSerialized(invalidPath);
//  }
//@Test(expected = RuntimeIOException.class)
//  public void testSaveParserToTextFileThrowsIOException() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    String invalidPath = "/non_existent_dir/failure.gz";
//    parser.saveParserToTextFile(invalidPath);
//  }
//@Test
//  public void testParseStringsWithOnlyOneWord() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    wordIndex.add("hello");
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<String> input = Collections.singletonList("hello");
//    Tree result = parser.parseStrings(input);
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testParseListWithNullWordListReturnsXTree() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    Tree result = parser.parse(null);
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testParseMultipleWithOneSentenceReturnsOneTree() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//    List<HasWord> sentence = Arrays.asList(new Word("Hello"), new Word("world"));
//    List<List<HasWord>> input = Collections.singletonList(sentence);
//
//    List<Tree> result = parser.parseMultiple(input);
//    assertEquals(1, result.size());
//    assertNotNull(result.get(0));
//  }
//@Test
//  public void testParserQueryReturnsRerankingParserQueryWhenRerankerIsSet() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//    parser.reranker = new Reranker() {
//      public List<?> getEvals() { return Collections.emptyList(); }
//
//      public Tree rerank(List<Tree> kBestParses, Sentence sentence) { return kBestParses.get(0); }
//    };
//
//    ParserQuery pq = parser.parserQuery();
//    assertTrue(pq instanceof RerankingParserQuery);
//  }
//@Test
//  public void testParseHandlesExceptionInParserQueryGracefully() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options) {
//      @Override
//      public ParserQuery parserQuery() {
//        return new ParserQuery() {
//          public boolean parse(List<? extends HasWord> sentence) {
//            throw new RuntimeException("Simulated failure during parse()");
//          }
//
//          public Tree getBestParse() { return null; }
//          public double getPCFGScore() { return 0; }
//          public Tree getBestPCFGParse() { return null; }
//          public Tree getBestDependencyParse() { return null; }
//          public List<Tree> getKBestParses(int k) { return null; }
//          public boolean hasParse() { return false; }
//          public List getKBestPCFGParses(int k) { return null; }
//          public List getKBestDependencyParses(int k) { return null; }
//          public List getBestParseTagged() { return null; }
//          public List getParserQueryEvals() { return null; }
//        };
//      }
//    };
//
//    List<HasWord> sentence = Arrays.asList(new Word("throws"), new Word("exception"));
//    Tree tree = parser.parse(sentence);
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
@Test
  public void testGetParserFromSerializedFileHandlesStreamCorruption() {
    String filePath = "corrupt-stream.ser";
    try {
      FileOutputStream fos = new FileOutputStream(filePath);
      fos.write("corrupt-data".getBytes());
      fos.close();

      LexicalizedParser result = LexicalizedParser.getParserFromSerializedFile(filePath);
      assertNull(result);
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    } finally {
      new File(filePath).delete();
    }
  }
@Test(expected = RuntimeException.class)
  public void testConfirmBeginBlockThrowsOnNull() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String fileName = "dummy.txt";
    String line = null;
    LexicalizedParser.class.getDeclaredMethod("confirmBeginBlock", String.class, String.class)
        .invoke(null, fileName, line);
  }
@Test(expected = RuntimeException.class)
  public void testConfirmBeginBlockThrowsOnInvalidPrefix() throws Exception {
    String file = "dummy-file";
    String line = "INVALID BLOCK";
    java.lang.reflect.Method method = LexicalizedParser.class.getDeclaredMethod("confirmBeginBlock", String.class, String.class);
    method.setAccessible(true);
    method.invoke(null, file, line);
  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testSaveParserToTextFileWithRerankerSetThrowsException() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//    parser.reranker = new Reranker() {
//      public List getEvals() { return Collections.emptyList(); }
//      public Tree rerank(List<Tree> kBest, Sentence s) { return null; }
//    };
//
//    parser.saveParserToTextFile("any-file.txt");
//  }
//@Test
//  public void testLoadModelWithStringPathAndListFlags() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> idx = new HashIndex<>();
//
//    String path = "model.dummy";
//    List<String> flags = Arrays.asList("-maxLength", "10");
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, idx, idx, idx, options);
//    assertNotNull(LexicalizedParser.loadModel(path, flags));
//  }
//@Test
//  public void testLoadModelWithNoExtraFlags() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> idx = new HashIndex<>();
//
//    String path = "model.dummy";
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, idx, idx, idx, options);
//    assertNotNull(LexicalizedParser.loadModel(path, options));
//  }
//@Test
//  public void testCopyLexicalizedParserHandlesFieldsCorrectly() {
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> stateIndex = new HashIndex<>();
//    stateIndex.add("S");
//    Index<String> wordIndex = new HashIndex<>();
//    wordIndex.add("hello");
//    Index<String> tagIndex = new HashIndex<>();
//    tagIndex.add("NN");
//
//    Options op = new Options();
//
//    LexicalizedParser original = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, op);
//    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);
//
//    assertNotNull(copy);
//    assertEquals(original.getOp(), copy.getOp());
//    assertEquals(original.wordIndex, copy.wordIndex);
//    assertEquals(original.tagIndex, copy.tagIndex);
//  }
//@Test
//  public void testParseMultipleWithNullInputReturnsEmptyList() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> stateIndex = new HashIndex<>();
//    Index<String> wordIndex = new HashIndex<>();
//    Index<String> tagIndex = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//    List<Tree> result = parser.parseMultiple((List<List<? extends HasWord>>) null);
//
//    assertNull(result);
//  }
//@Test
//  public void testGetExtraEvalsWithRerankerPresent() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> idx = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, idx, idx, idx, options);
//
//    Eval dummyEval = new Eval("dummy") {
//      @Override
//      public double evaluate(Tree guess, Tree gold, PrintWriter pw) { return 0.0; }
//    };
//
//    parser.reranker = new Reranker() {
//      @Override public List<Eval> getEvals() {
//        return Collections.singletonList(dummyEval);
//      }
//
//      @Override public Tree rerank(List<Tree> kbest, Sentence sentence) { return kbest.get(0); }
//    };
//
//    List<Eval> evals = parser.getExtraEvals();
//    assertEquals(1, evals.size());
//    assertEquals("dummy", evals.get(0).name);
//  }
//@Test
//  public void testSaveParserToTextFileWithGzExtension() {
//    try {
//      Options options = new Options();
//      Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//      BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//      UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//      DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//      Index<String> stateIndex = new HashIndex<>();
//      stateIndex.add("S");
//      Index<String> wordIndex = new HashIndex<>();
//      Index<String> tagIndex = new HashIndex<>();
//
//      LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, options);
//
//      String path = "test_output.gz";
//      parser.saveParserToTextFile(path);
//
//      File f = new File(path);
//      assertTrue(f.exists());
//      f.delete();
//    } catch (Exception e) {
//      fail("Unexpected exception thrown: " + e.getMessage());
//    }
//  }
//@Test
//  public void testGetTLPParamsReturnsExpectedInstance() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> idx = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, idx, idx, idx, options);
//    assertEquals(options.tlpParams, parser.getTLPParams());
//  }
@Test(expected = ClassCastException.class)
  public void testLoadModelThrowsClassCastExceptionIfObjectIsWrongType() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject("not a parser");
    oos.close();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);
    LexicalizedParser.loadModel(ois);
  }
@Test
  public void testGetParserFromFileReturnsNullIfBothLoadersFail() {
    Options op = new Options();
    String fakePath = "nonexistent.parser";
    LexicalizedParser parser = LexicalizedParser.getParserFromFile(fakePath, op);
    assertNull(parser);
  }
//@Test
//  public void testSetOptionFlagsAcceptsMultipleValidFlags() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> i = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, i, i, i, options);
//
//    parser.setOptionFlags("-nostateAnnotation", "-maxLength", "25", "-outputFormat", "penn");
//
//    assertEquals(25, parser.getOp().testOptions.maxLength);
//    assertEquals("penn", parser.getOp().testOptions.outputFormat);
//  }
//@Test
//  public void testParserQueryReturnsLexicalizedParserQueryByDefault() {
//    Options opts = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> i = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, i, i, i, opts);
//    ParserQuery query = parser.parserQuery();
//    assertTrue(query instanceof LexicalizedParserQuery);
//  }
//@Test
//  public void testGetExtraEvalsReturnsEmptyWhenRerankerReturnsNullEvalList() {
//    Options opts = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> i = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, i, i, i, opts);
//    parser.reranker = new Reranker() {
//      public List<Eval> getEvals() { return null; }
//      public Tree rerank(List<Tree> list, Sentence s) { return null; }
//    };
//
//    List<Eval> evals = parser.getExtraEvals();
//    assertTrue(evals.isEmpty());
//  }
//@Test
//  public void testParseTreeReturnsNullOnEmptyList() {
//    Options opts = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> i = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, i, i, i, opts);
//
//    Tree tree = parser.parseTree(Collections.emptyList());
//    assertNull(tree);
//  }
//@Test
//  public void testParseMultipleMultiCoreWrapperWithSingleThread() {
//    Options opts = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> i = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, i, i, i, opts);
//    List<List<HasWord>> input = new ArrayList<>();
//    input.add(Arrays.asList(new Word("one"), new Word("sentence")));
//
//    List<Tree> trees = parser.parseMultiple(input, 1);
//    assertEquals(1, trees.size());
//    assertNotNull(trees.get(0));
//  }
//@Test
//  public void testParseWithOneWord() {
//    Options opts = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> i = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, i, i, i, opts);
//    Tree result = parser.parse(Collections.singletonList(new Word("solo")));
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testParseReturnsXTreeWithCorrectLabelAndChildren() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> index = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, index, index, index, options);
//    List<HasWord> words = Arrays.asList(new Word("Hello"), new Word("World"));
//
//    Tree tree = parser.parse(words);
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//    assertEquals(2, tree.numChildren());
//    assertEquals("Hello", tree.getChild(0).label().value());
//    assertEquals("World", tree.getChild(1).label().value());
//  }
//@Test
//  public void testParserQueryReentrancyWithReranker() {
//    Options opts = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> i = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, i, i, i, opts);
//    parser.reranker = new Reranker() {
//      @Override
//      public Tree rerank(List<Tree> list, Sentence s) {
//        Tree t = Tree.valueOf("(X fallback)");
//        t.setScore(42.42);
//        return t;
//      }
//
//      @Override
//      public List<Eval> getEvals() {
//        List<Eval> list = new ArrayList<>();
//        list.add(new Eval("dummy") {
//          @Override
//          public double evaluate(Tree guess, Tree gold, PrintWriter pw) {
//            return 1.0;
//          }
//        });
//        return list;
//      }
//    };
//
//    ParserQuery pq = parser.parserQuery();
//    assertNotNull(pq);
//    Tree best = pq.getBestParse();
//    assertNotNull(best);
//  }
//@Test
//  public void testLoadModelTextFileGzStub() {
//    try {
//      Options op = new Options();
//      Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//      BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//      UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//      DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//      Index<String> idx = new HashIndex<>();
//      String fakeTextFile = "fake-model.txt.gz";
//
//      File file = new File(fakeTextFile);
//      FileOutputStream fos = new FileOutputStream(file);
//      fos.write("BEGIN OPTIONS\n".getBytes());
//      fos.write("BEGIN STATE_INDEX\n".getBytes());
//      fos.write("BEGIN WORD_INDEX\n".getBytes());
//      fos.write("BEGIN TAG_INDEX\n".getBytes());
//      fos.write("BEGIN LEXICON null\n".getBytes());
//      fos.write("BEGIN UNARY_GRAMMAR\n".getBytes());
//      fos.write("BEGIN BINARY_GRAMMAR\n".getBytes());
//      fos.write("BEGIN DEPENDENCY_GRAMMAR\n".getBytes());
//      fos.close();
//
//      LexicalizedParser parser = LexicalizedParser.loadModel(fakeTextFile);
//      assertNull(parser);
//      file.delete();
//    } catch (IOException e) {
//      fail("Unexpected exception in fake .gz load test: " + e);
//    }
//  }
//@Test
//  public void testSaveParserToSerializedWithMinimalState() {
//    try {
//      Options op = new Options();
//      Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//      BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//      UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//      DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//      Index<String> index = new HashIndex<>();
//      LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, index, index, index, op);
//      String path = "temp-lexparser.ser";
//
//      parser.saveParserToSerialized(path);
//      File file = new File(path);
//      assertTrue(file.exists());
//      file.delete();
//    } catch (Exception e) {
//      fail("Unexpected during saveParserToSerialized: " + e.toString());
//    }
//  }
@Test
  public void testTrainFromTreebank_StringPath_MinimalTreebank() {
    String dir = "minimal-treebank-dir";
    File f = new File(dir);
    f.mkdir();
    File treeFile = new File(f, "tree1.mrg");
    try {
      FileWriter fw = new FileWriter(treeFile);
      fw.write("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ barks))))\n");
      fw.close();

      Options op = new Options();
      LexicalizedParser parser = LexicalizedParser.trainFromTreebank(dir, null, op);
      assertNotNull(parser);
    } catch (IOException e) {
      fail("Failed to create minimal tree file: " + e);
    } finally {
      treeFile.delete();
      f.delete();
    }
  }
//@Test
//  public void testParseWithCoreLabelInput() {
//    Options op = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//    Index<String> index = new HashIndex<>();
//
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, index, index, index, op);
//    CoreLabelTokenFactory factory = new CoreLabelTokenFactory();
//    CoreLabel cl1 = factory.makeToken("Stanford", 0);
//    CoreLabel cl2 = factory.makeToken("NLP", 0);
//
//    Tree result = parser.parse(Arrays.asList(cl1, cl2));
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
@Test
  public void testLoadModelHandlesIOExceptionGracefully() throws Exception {
    ObjectInputStream input = new ObjectInputStream(new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("Simulated IOException");
      }
    });

    boolean caught = false;
    try {
      LexicalizedParser.loadModel(input);
    } catch (RuntimeIOException e) {
      caught = true;
      assertTrue(e.getCause() instanceof IOException);
    }
    assertTrue(caught);
  }
@Test
  public void testLoadModelHandlesClassNotFoundExceptionGracefully() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(new Object() {});
    oos.close();

    ByteArrayInputStream inBytes = new ByteArrayInputStream(out.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(inBytes) {
      @Override
      protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        throw new ClassNotFoundException("Simulated CNFE");
      }
    };

    boolean caught = false;
    try {
      LexicalizedParser.loadModel(ois);
    } catch (RuntimeException e) {
      caught = true;
      assertTrue(e.getCause() instanceof ClassNotFoundException);
    }
    assertTrue(caught);
  }
//@Test
//  public void testSaveParserToTextFileIOExceptionDuringWrite() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> index = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, index, index, index, options);
//
//    boolean threwException = false;
//    try {
//      File fake = new File("/root/cannot-write.txt.gz");
//      parser.saveParserToTextFile(fake.getAbsolutePath());
//    } catch (RuntimeIOException e) {
//      threwException = true;
//    }
//
//    assertTrue(threwException);
//  }
@Test
  public void testTrainFromTreebankWithInvalidPathReturnsValidParser() {
    File treebankDir = new File("temp-treebank-dir");
    treebankDir.mkdirs();
    try {
      File treeFile = new File(treebankDir, "sample.trees");
      FileWriter fw = new FileWriter(treeFile);
      fw.write("(ROOT (S (NP (DT The) (NN cat)) (VP (VBZ sits))))\n");
      fw.close();

      Options options = new Options();
      LexicalizedParser parser = LexicalizedParser.trainFromTreebank(treebankDir.getPath(), null, options);
      assertNotNull(parser);
    } catch (IOException e) {
      fail("Treebank file creation failed: " + e.getMessage());
    } finally {
      new File(treebankDir.getPath(), "sample.trees").delete();
      treebankDir.delete();
    }
  }
//@Test
//  public void testDependencyGrammarNullHandling() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//
//    Index<String> idx = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, null, idx, idx, idx, options);
//    Tree tree = parser.parse(Collections.singletonList(new Word("any")));
//    assertNotNull(tree);
//  }
//@Test
//  public void testTreebankLanguagePackReturnsExpectedObject() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> index = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, index, index, index, options);
//    TreebankLangParserParams tlpParams = parser.getTLPParams();
//    assertNotNull(tlpParams);
//    assertNotNull(tlpParams.treebankLanguagePack());
//  }
//@Test
//  public void testDefaultCoreNLPFlagsReturnsNonNullArray() {
//    Options options = new Options();
//    Lexicon lex = new LexicalizedParserTest.DummyLexicon();
//    BinaryGrammar bg = new LexicalizedParserTest.DummyBinaryGrammar();
//    UnaryGrammar ug = new LexicalizedParserTest.DummyUnaryGrammar();
//    DependencyGrammar dg = new LexicalizedParserTest.DummyDependencyGrammar();
//
//    Index<String> index = new HashIndex<>();
//    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, index, index, index, options);
//    String[] flags = parser.defaultCoreNLPFlags();
//    assertNotNull(flags);
//  }
}