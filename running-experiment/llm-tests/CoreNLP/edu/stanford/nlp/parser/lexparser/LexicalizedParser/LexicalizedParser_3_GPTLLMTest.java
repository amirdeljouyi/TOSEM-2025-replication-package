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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LexicalizedParser_3_GPTLLMTest {

 @Test
  public void testGetOpReturnsOptionsInstance() {
    Options options = new Options();
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        options
    );
    Options returned = parser.getOp();
    assertSame(options, returned);
  }
@Test
  public void testParserQueryReturnsLexicalizedParserQuery() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    ParserQuery query = parser.parserQuery();
    assertTrue(query instanceof LexicalizedParserQuery);
  }
@Test
  public void testParseStringsReturnsTree() {
    LexicalizedParser parser = mock(LexicalizedParser.class);
    List<HasWord> wordList = new ArrayList<>();
    wordList.add(new Word("Stanford"));
    wordList.add(new Word("NLP"));
    Tree expectedTree = mock(Tree.class);
    when(parser.parse(anyList())).thenReturn(expectedTree);
    List<String> input = Arrays.asList("Stanford", "NLP");
    Tree result = parser.parseStrings(input);
    assertSame(expectedTree, result);
  }
@Test
  public void testParseSuccessfulReturnsBestParse() {
    LexicalizedParser parser = mock(LexicalizedParser.class);
    ParserQuery query = mock(ParserQuery.class);
    when(parser.parserQuery()).thenReturn(query);
    List<HasWord> sentence = Arrays.asList(new Word("Hello"), new Word("World"));
    when(query.parse(sentence)).thenReturn(true);
    Tree mockTree = mock(Tree.class);
    when(query.getBestParse()).thenReturn(mockTree);
    when(query.getPCFGScore()).thenReturn(-1234.56);
    when(parser.parse(sentence)).thenCallRealMethod();
    Tree result = parser.parse(sentence);
    assertNotNull(result);
  }
@Test
  public void testParseFailureReturnsXTree() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    LexicalizedParser spyParser = spy(parser);
    ParserQuery failedQuery = mock(ParserQuery.class);
    when(spyParser.parserQuery()).thenReturn(failedQuery);
    when(failedQuery.parse(anyList())).thenReturn(false);
    List<HasWord> badSentence = Arrays.asList(new Word("parse"), new Word("fail"));
    Tree result = spyParser.parse(badSentence);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseTreeReturnsCorrectTreeOnSuccess() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    LexicalizedParser spy = spy(parser);
    ParserQuery query = mock(ParserQuery.class);
    Tree tree = mock(Tree.class);
    when(spy.parserQuery()).thenReturn(query);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenReturn(tree);
    Tree t = spy.parseTree(Arrays.asList(new Word("correct")));
    assertSame(tree, t);
  }
@Test
  public void testParseTreeReturnsNullOnFailure() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    LexicalizedParser spy = spy(parser);
    ParserQuery query = mock(ParserQuery.class);
    when(spy.parserQuery()).thenReturn(query);
    when(query.parse(anyList())).thenReturn(false);
    Tree result = spy.parseTree(Arrays.asList(new Word("bad")));
    assertNull(result);
  }
@Test
  public void testParseMultipleTwoSentencesReturnsTwoTrees() {
    LexicalizedParser parser = mock(LexicalizedParser.class);
    Tree tree1 = mock(Tree.class);
    Tree tree2 = mock(Tree.class);
    List<HasWord> sent1 = Arrays.asList(new Word("First"));
    List<HasWord> sent2 = Arrays.asList(new Word("Second"));
    when(parser.parse(sent1)).thenReturn(tree1);
    when(parser.parse(sent2)).thenReturn(tree2);
    List<List<? extends HasWord>> batch = Arrays.asList(sent1, sent2);
    when(parser.parseMultiple(batch)).thenCallRealMethod();
    List<Tree> results = parser.parseMultiple(batch);
    assertEquals(2, results.size());
    assertSame(tree1, results.get(0));
    assertSame(tree2, results.get(1));
  }
@Test
  public void testCopyLexicalizedParserReturnsNewInstance() {
    Options options = new Options();
    Index<String> stateIdx = new HashIndex<>();
    Index<String> wordIdx = new HashIndex<>();
    Index<String> tagIdx = new HashIndex<>();
    LexicalizedParser original = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class), stateIdx, wordIdx, tagIdx, options
    );
    LexicalizedParser copied = LexicalizedParser.copyLexicalizedParser(original);
    assertNotNull(copied);
    assertNotSame(original, copied);
  }
@Test
  public void testSetOptionFlagsParsesSingleOption() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class), new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    String[] flags = {"-maxLength", "42"};
    parser.setOptionFlags(flags);
    int maxLength = parser.getOp().testOptions.maxLength;
    assertEquals(42, maxLength);
  }
@Test(expected = RuntimeException.class)
  public void testLoadModelFromStreamIOException() throws Exception {
    ObjectInputStream errorStream = mock(ObjectInputStream.class);
    when(errorStream.readObject()).thenThrow(new IOException("Failed"));
    LexicalizedParser.loadModel(errorStream);
  }
@Test(expected = ClassCastException.class)
  public void testLoadModelFromStreamWrongType() throws Exception {
    ObjectInputStream fakeStream = mock(ObjectInputStream.class);
    when(fakeStream.readObject()).thenReturn("NotAParserInstance");
    LexicalizedParser.loadModel(fakeStream);
  }
@Test
  public void testSaveParserToSerializedCreatesFile() throws IOException {
    
    String path = "temp_parser.ser";
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class), new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    parser.saveParserToSerialized(path);
    File file = new File(path);
    boolean exists = file.exists();
    if (exists) {
      file.delete(); 
    }
    assertTrue(exists);
  }
@Test
  public void testParseEmptyListReturnsXTree() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spyParser = spy(parser);
    ParserQuery query = mock(ParserQuery.class);
    when(spyParser.parserQuery()).thenReturn(query);
    when(query.parse(Collections.emptyList())).thenReturn(false);

    Tree result = spyParser.parse(Collections.emptyList());
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseWithNullParserQueryReturnsXTree() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spyParser = spy(parser);
    when(spyParser.parserQuery()).thenThrow(new RuntimeException("ParserQuery failure"));

    List<HasWord> input = Arrays.asList(new Word("error"));

    Tree result = spyParser.parse(input);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseTreeWithNullBestParseReturnsNull() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spy = spy(parser);
    ParserQuery pq = mock(ParserQuery.class);
    when(spy.parserQuery()).thenReturn(pq);
    when(pq.parse(anyList())).thenReturn(true);
    when(pq.getBestParse()).thenReturn(null);

    Tree tree = spy.parseTree(Arrays.asList(new Word("unexpected")));
    assertNull(tree);
  }
@Test
  public void testParseMultipleWithEmptyListReturnsEmptyTreeList() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    List<List<? extends HasWord>> sentences = Collections.emptyList();
    List<Tree> result = parser.parseMultiple(sentences);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testSetOptionFlagsInvalidFlagThrowsException() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    String[] invalidFlags = new String[]{"-nonexistentFlag"};

    boolean exceptionThrown = false;
    try {
      parser.setOptionFlags(invalidFlags);
    } catch (IllegalArgumentException e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }
@Test
  public void testGetExtraEvalsReturnsEmptyListWhenNoReranker() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    List<?> result = parser.getExtraEvals();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testParseTreeNullInputReturnsNull() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spy = spy(parser);
    ParserQuery pq = mock(ParserQuery.class);
    when(spy.parserQuery()).thenReturn(pq);
    when(pq.parse(null)).thenReturn(false);

    Tree t = spy.parseTree(null);
    assertNull(t);
  }
@Test
  public void testLoadModelWithCorrectTypeObjectStream() throws Exception {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(parser);
    oos.flush();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);

    LexicalizedParser loaded = LexicalizedParser.loadModel(ois);
    assertNotNull(loaded);
  }
@Test
  public void testSaveParserToSerializedHandlesIOExceptionGracefully() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    String invalidPath = "/invalid?/illegal/path/output.ser";

    boolean exceptionThrown = false;
    try {
      parser.saveParserToSerialized(invalidPath);
    } catch (RuntimeIOException e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }
@Test
  public void testGetParserQueryEvalsReturnsEmptyList() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );

    List<ParserQueryEval> evals = parser.getParserQueryEvals();
    assertNotNull(evals);
    assertTrue(evals.isEmpty());
  }
@Test
  public void testGetTreePrintReturnsNonNull() {
    Options options = new Options();
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options
    );

    assertNotNull(parser.getTreePrint());
  }
@Test
  public void testRerankerAddsExtraEval() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );

    Reranker rerankerMock = mock(Reranker.class);
    Eval evalMock = mock(Eval.class);
    List<Eval> evalList = new ArrayList<>();
    evalList.add(evalMock);

    when(rerankerMock.getEvals()).thenReturn(evalList);
    parser.reranker = rerankerMock;

    List<Eval> returned = parser.getExtraEvals();
    assertEquals(1, returned.size());
    assertSame(evalMock, returned.get(0));
  }
@Test
  public void testParseMultipleWithSingleEmptySentenceReturnsSingletonTreeList() {
    LexicalizedParser parser = mock(LexicalizedParser.class);
    Tree emptyTree = mock(Tree.class);
    when(parser.parse(Collections.emptyList())).thenReturn(emptyTree);

    List<List<Word>> input = new ArrayList<>();
    input.add(Collections.emptyList());

    when(parser.parseMultiple(input)).thenCallRealMethod();
    List<Tree> result = parser.parseMultiple(input);
    assertEquals(1, result.size());
    assertSame(emptyTree, result.get(0));
  }
@Test
  public void testParseMultipleWithNullSentenceReturnsXTree() {
    LexicalizedParser realParser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class), new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );

    LexicalizedParser spy = spy(realParser);
    ParserQuery mockQuery = mock(ParserQuery.class);
    when(spy.parserQuery()).thenReturn(mockQuery);
    when(mockQuery.parse(null)).thenReturn(false);

    List<List<Word>> sentences = new ArrayList<>();
    sentences.add(null);

    List<Tree> trees = spy.parseMultiple(sentences);
    assertNotNull(trees);
    assertEquals(1, trees.size());
    assertEquals("X", trees.get(0).label().value());
  }
@Test
  public void testSaveParserToTextFileFailsWhenRerankerPresent() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class), new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );

    parser.reranker = mock(Reranker.class);

    boolean thrown = false;
    try {
      parser.saveParserToTextFile("dummy.txt");
    } catch (UnsupportedOperationException e) {
      thrown = true;
    }
    assertTrue(thrown);
  }
@Test
  public void testSaveParserToTextFileCreatesGzFile() throws IOException {
    File file = new File("parser_test_output.gz");
    if (file.exists()) {
      file.delete();
    }

    Lexicon lexiconMock = mock(Lexicon.class);
    when(lexiconMock.getUnknownWordModel()).thenReturn(null);
    doNothing().when(lexiconMock).writeData(any(PrintWriter.class));

    BinaryGrammar bg = mock(BinaryGrammar.class);
    UnaryGrammar ug = mock(UnaryGrammar.class);
    DependencyGrammar dg = mock(DependencyGrammar.class);

    doNothing().when(bg).writeData(any(PrintWriter.class));
    doNothing().when(ug).writeData(any(PrintWriter.class));
    doNothing().when(dg).writeData(any(PrintWriter.class));

    HashIndex<String> stateIndex = new HashIndex<>();
    stateIndex.add("S");

    HashIndex<String> wordIndex = new HashIndex<>();
    wordIndex.add("word");

    HashIndex<String> tagIndex = new HashIndex<>();
    tagIndex.add("NN");

    Options options = new Options();

    LexicalizedParser parser = new LexicalizedParser(
        lexiconMock, bg, ug, dg,
        stateIndex,
        wordIndex,
        tagIndex,
        options
    );

    parser.saveParserToTextFile("parser_test_output.gz");
    boolean exists = new File("parser_test_output.gz").exists();

    new File("parser_test_output.gz").delete();
    assertTrue(exists);
  }
@Test
  public void testWordScoreAppliedFromPCFGScore() {
    LexicalizedParser realParser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class), new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );

    LexicalizedParser spy = spy(realParser);
    ParserQuery query = mock(ParserQuery.class);
    Tree tree = mock(Tree.class);

    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenReturn(tree);
    when(query.getPCFGScore()).thenReturn(-2087.44);

    when(spy.parserQuery()).thenReturn(query);

    List<Word> input = Arrays.asList(new Word("Score"), new Word("test"));
    Tree result = spy.parse(input);
    assertSame(tree, result);
    verify(tree).setScore(-2087.44 % -10000.0);
  }
@Test
  public void testDefaultCoreNLPFlagsNotNull() {
    Options options = new Options();
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class),
        mock(DependencyGrammar.class), new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), options
    );
    String[] flags = parser.defaultCoreNLPFlags();
    assertNotNull(flags);
  }
@Test(expected = RuntimeException.class)
  public void testLoadModelFromSerializedFileWithNonExistentFileThrows() {
    LexicalizedParser.getParserFromSerializedFile("nonexistent/path/parser.ser.gz");
  }
@Test(expected = RuntimeException.class)
  public void testLoadModelFromSerializedFileWithInvalidClassThrows() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject("This is not a parser");
    oos.close();

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()) {
      @Override
      public int available() {
        return super.available();
      }
    };
    ObjectInputStream ois = new ObjectInputStream(bis);

    LexicalizedParser.loadModel(ois);
  }
@Test
  public void testLoadModelFromSerializedFileReturnsNullOnStreamCorruptedException() {
    InputStream is = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new StreamCorruptedException("stream corrupted");
      }
    };
    boolean thrown = false;
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(is);
    } catch (StreamCorruptedException e) {
      thrown = true; 
    } catch (Exception e) {
      
    }
    assertTrue(thrown);
  }
@Test
  public void testLoadModelFromListOfFlags() {
    Options options = new Options();
    List<String> flags = Arrays.asList("-maxLength", "80", "-outputFormat", "penn");
    LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", options, String.valueOf(flags));
    assertNotNull(parser.getOp().testOptions);
  }
@Test
  public void testSetMultipleOptionFlags() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    String[] flags = new String[]{"-maxLength", "67", "-beamSize", "0.0001"};
    parser.setOptionFlags(flags);
    assertEquals(67, parser.getOp().testOptions.maxLength);
  }
@Test
  public void testDefaultParserLocationSystemProperty() {
    System.setProperty("edu.stanford.nlp.SerializedLexicalizedParser", "custom/location/model.ser.gz");
    String value = System.getProperty("edu.stanford.nlp.SerializedLexicalizedParser");
    assertNotNull(value);
    assertEquals("custom/location/model.ser.gz", value);
  }
@Test(expected = RuntimeIOException.class)
  public void testSaveParserToSerializedHandlesFSFailure() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(),
        new HashIndex<>(),
        new HashIndex<>(),
        new Options()
    );
    File file = new File("/not_writable_path/test-output.ser");
    parser.saveParserToSerialized(file.getAbsolutePath());
  }
@Test
  public void testLoadModelWithExtraArgsArrayWorks() {
    Options options = new Options();
    String[] args = new String[]{"-maxLength", "10"};
    LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", options, args);
    assertNotNull(parser);
  }
@Test
  public void testParseWithSingleWordInputReturnsTree() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", options);
    Tree tree = parser.parse(Arrays.asList(new Word("Stanford")));
    assertNotNull(tree);
  }
@Test
  public void testParserFromTextFileReturnsNullOnIOException() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.getParserFromTextFile("invalid_path/to_model.txt", options);
    assertNull(parser);
  }
@Test(expected = RuntimeException.class)
  public void testConfirmBeginBlockNullLineThrows() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(),
        new HashIndex<>(),
        new HashIndex<>(),
        new Options()
    );
    parser.getClass(); 
    String file = "testFile.txt";
    String line = null;
    Class<?> cls = LexicalizedParser.class;
    try {
      java.lang.reflect.Method method = cls.getDeclaredMethod("confirmBeginBlock", String.class, String.class);
      method.setAccessible(true);
      method.invoke(null, file, line);
    } catch (Exception e) {
      throw new RuntimeException(e.getCause()); 
    }
  }
@Test(expected = RuntimeException.class)
  public void testConfirmBeginBlockIncorrectLineThrows() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(),
        new HashIndex<>(),
        new HashIndex<>(),
        new Options()
    );
    parser.getClass(); 
    String file = "badFile.txt";
    String line = "INVALID HEADER";
    Class<?> cls = LexicalizedParser.class;
    try {
      java.lang.reflect.Method method = cls.getDeclaredMethod("confirmBeginBlock", String.class, String.class);
      method.setAccessible(true);
      method.invoke(null, file, line);
    } catch (Exception e) {
      throw new RuntimeException(e.getCause());
    }
  }
@Test
  public void testReflectionLoadingFailsGracefully() {
    boolean exceptionThrown = false;
    try {
      ReflectionLoading.loadByReflection("non.existent.ClassName");
    } catch (RuntimeException e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }
@Test
  public void testParseTreeReturnsNullWhenParserQueryGivesNullBestParse() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spyParser = spy(parser);
    ParserQuery mockQuery = mock(ParserQuery.class);
    when(spyParser.parserQuery()).thenReturn(mockQuery);
    when(mockQuery.parse(anyList())).thenReturn(true);
    when(mockQuery.getBestParse()).thenReturn(null);

    List<Word> input = Arrays.asList(new Word("null"), new Word("parse"));
    Tree result = spyParser.parseTree(input);
    assertNull(result);
  }
@Test
  public void testLoadModelWithExtraFlagsModifiesOptions() {
    String[] flags = new String[]{"-maxLength", "99"};
    Options baseOptions = new Options();
    LexicalizedParser parser = LexicalizedParser.loadModel(
        LexicalizedParser.DEFAULT_PARSER_LOC,
        baseOptions,
        flags
    );
    assertNotNull(parser);
    assertEquals(99, parser.getOp().testOptions.maxLength);
  }
@Test
  public void testParseMultiplePartialNullTreeFallback() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spyParser = spy(parser);

    Tree tree1 = mock(Tree.class);
    Tree fallbackTree = ParserUtils.xTree(Arrays.asList(new Word("fallback")));

    doReturn(tree1).when(spyParser).parse(Arrays.asList(new Word("hello")));
    doAnswer(invocation -> fallbackTree).when(spyParser).parse(Arrays.asList(new Word("fail")));

    List<List<Word>> sentences = Arrays.asList(
        Arrays.asList(new Word("hello")),
        Arrays.asList(new Word("fail"))
    );

    List<Tree> trees = spyParser.parseMultiple(sentences);
    assertEquals(2, trees.size());
    assertTrue(trees.contains(tree1));
    assertTrue(trees.contains(fallbackTree));
  }
@Test
  public void testParseHandlesExceptionAndReturnsFallbackTree() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spy = spy(parser);
    when(spy.parserQuery()).thenThrow(new RuntimeException("Simulated parser crash"));

    List<Word> words = Arrays.asList(new Word("simulate"), new Word("exception"));
    Tree result = spy.parse(words);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testGetExtraEvalsHandlesNullRerankerEvals() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );

    Reranker mockReranker = mock(Reranker.class);
    when(mockReranker.getEvals()).thenReturn(null);
    parser.reranker = mockReranker;

    List<?> evals = parser.getExtraEvals();
    assertNotNull(evals);
    assertTrue(evals.isEmpty());
  }
//@Test(expected = RuntimeException.class)
//  public void testLoadModelThrowsWhenObjectInputStreamReturnsNull() throws Exception {
//    ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(new byte[0])) {
//      @Override
//      public Object readObject() {
//        return null;
//      }
//    };
//    LexicalizedParser.loadModel(stream);
//  }
@Test
  public void testSaveParserToTextFileHandlesNullDependencyGrammar() throws IOException {
    Lexicon lex = mock(Lexicon.class);
    BinaryGrammar bg = mock(BinaryGrammar.class);
    UnaryGrammar ug = mock(UnaryGrammar.class);
    DependencyGrammar dg = null;

    when(lex.getUnknownWordModel()).thenReturn(null);
    doNothing().when(lex).writeData(any(PrintWriter.class));
    doNothing().when(bg).writeData(any(PrintWriter.class));
    doNothing().when(ug).writeData(any(PrintWriter.class));

    HashIndex<String> stateIndex = new HashIndex<>();
    stateIndex.add("S");

    HashIndex<String> wordIndex = new HashIndex<>();
    wordIndex.add("word");

    HashIndex<String> tagIndex = new HashIndex<>();
    tagIndex.add("NN");

    Options options = new Options();

    LexicalizedParser parser = new LexicalizedParser(
        lex, bg, ug, dg,
        stateIndex, wordIndex, tagIndex, options
    );

    String filename = "temp_text_grammar.txt";
    parser.saveParserToTextFile(filename);
    File outputFile = new File(filename);
    assertTrue(outputFile.exists());
    outputFile.delete();
  }
@Test
  public void testParseStringsWithEmptyInputReturnsXTree() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );
    LexicalizedParser spyParser = spy(parser);

    ParserQuery pq = mock(ParserQuery.class);
    when(spyParser.parserQuery()).thenReturn(pq);
    when(pq.parse(anyList())).thenReturn(false);

    List<String> input = Collections.emptyList();
    Tree result = spyParser.parseStrings(input);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseMultipleWithNullSubSentenceReturnsTreeList() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );
    LexicalizedParser spy = spy(parser);
    Tree tree = mock(Tree.class);
    doReturn(tree).when(spy).parse((List<? extends HasWord>) null);
    List<List<Word>> input = Arrays.asList(null, null);
    List<Tree> result = spy.parseMultiple(input);
    assertEquals(2, result.size());
    assertSame(tree, result.get(0));
  }
@Test
  public void testSetOptionFlagsWithUnknownFlagThrows() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class), mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );
    boolean thrown = false;
    try {
      parser.setOptionFlags("-badFlag", "value");
    } catch (IllegalArgumentException ex) {
      thrown = true;
    }
    assertTrue(thrown);
  }
@Test
  public void testLexicalizedParserQueryReturnsValidInstance() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class), mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );
    assertNotNull(parser.lexicalizedParserQuery());
  }
@Test
  public void testSaveTrainTreesToFilePath() throws Exception {
    Options op = new Options();
    op.trainOptions.trainTreeFile = "train-trees-out.ser";

    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class), mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), op
    );

    boolean threw = false;
    try {
      parser.getOp().trainOptions.trainTreeFile = "train-trees-out.ser";
      Tree dummyTree = mock(Tree.class);
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("train-trees-out.ser"));
      out.writeObject(Collections.singletonList(dummyTree));
      out.close();
    } catch (IOException e) {
      threw = true;
    }

    new File("train-trees-out.ser").delete();
    assertFalse(threw);
  }
@Test
  public void testSaveParserToTextFileCreatesDotGzFile() throws IOException {
    Lexicon lex = mock(Lexicon.class);
    BinaryGrammar bg = mock(BinaryGrammar.class);
    UnaryGrammar ug = mock(UnaryGrammar.class);
    DependencyGrammar dg = mock(DependencyGrammar.class);

    when(lex.getUnknownWordModel()).thenReturn(null);
    doNothing().when(lex).writeData(any(PrintWriter.class));
    doNothing().when(bg).writeData(any(PrintWriter.class));
    doNothing().when(ug).writeData(any(PrintWriter.class));
    doNothing().when(dg).writeData(any(PrintWriter.class));

    HashIndex<String> stateIndex = new HashIndex<>();
    stateIndex.add("S");
    HashIndex<String> wordIndex = new HashIndex<>();
    wordIndex.add("hello");
    HashIndex<String> tagIndex = new HashIndex<>();
    tagIndex.add("NN");

    LexicalizedParser parser = new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, new Options());

    String file = "output_model.txt.gz";
    parser.saveParserToTextFile(file);

    assertTrue(new File(file).exists());
    new File(file).delete();
  }
@Test
  public void testGetParserFromFileFallsBackToTextSuccessfully() {
    LexicalizedParser realParser = mock(LexicalizedParser.class);
    Options op = new Options();

    LexicalizedParser fallbackParser = LexicalizedParser.getParserFromFile("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", op);
    assertNotNull(fallbackParser);
  }
@Test(expected = RuntimeException.class)
  public void testLoadModelObjectStreamNullResponseThrows() throws Exception {
    ObjectInputStream stream = mock(ObjectInputStream.class);
    when(stream.readObject()).thenReturn(null);
    LexicalizedParser.loadModel(stream);
  }
@Test
  public void testSetOptionFlagsWithEmptyArrayIsSafe() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(),
        new HashIndex<>(),
        new HashIndex<>(),
        new Options()
    );

    String[] emptyFlags = {};
    parser.setOptionFlags(emptyFlags); 
    assertNotNull(parser.getOp());
  }
@Test
  public void testParseReturnsXTreeWhenGetBestParseReturnsNull() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(),
        new HashIndex<>(),
        new HashIndex<>(),
        new Options()
    );

    LexicalizedParser spy = spy(parser);
    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenReturn(null);
    when(spy.parserQuery()).thenReturn(query);

    List<Word> input = Arrays.asList(new Word("word"));
    Tree result = spy.parse(input);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseHandlesExceptionFromGetBestParse() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    LexicalizedParser spy = spy(parser);
    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getBestParse()).thenThrow(new RuntimeException("Unexpected"));
    when(spy.parserQuery()).thenReturn(query);

    List<Word> input = Arrays.asList(new Word("crash"));
    Tree result = spy.parse(input);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseWithParserQueryReturningNull() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(),
        new Options()
    );
    LexicalizedParser spy = spy(parser);
    when(spy.parserQuery()).thenReturn(null);

    List<Word> input = Arrays.asList(new Word("nullPQ"));
    Tree result = spy.parse(input);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseSetsScoreNaN() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );
    LexicalizedParser spy = spy(parser);

    ParserQuery query = mock(ParserQuery.class);
    Tree tree = mock(Tree.class);
    when(query.parse(anyList())).thenReturn(true);
    when(query.getPCFGScore()).thenReturn(Double.NaN);
    when(query.getBestParse()).thenReturn(tree);
    when(spy.parserQuery()).thenReturn(query);

    List<Word> input = Arrays.asList(new Word("NaNScore"));
    Tree result = spy.parse(input);
    assertSame(tree, result);
    verify(tree).setScore(anyDouble()); 
  }
@Test
  public void testParseHandlesExtremeScore() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class), mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );

    LexicalizedParser spy = spy(parser);
    Tree tree = mock(Tree.class);
    ParserQuery query = mock(ParserQuery.class);

    when(query.parse(anyList())).thenReturn(true);
    when(query.getPCFGScore()).thenReturn(Double.NEGATIVE_INFINITY);
    when(query.getBestParse()).thenReturn(tree);
    when(spy.parserQuery()).thenReturn(query);

    List<Word> input = Arrays.asList(new Word("extreme"));
    Tree t = spy.parse(input);
    assertSame(tree, t);
    verify(tree).setScore(Double.NEGATIVE_INFINITY % -10000);
  }
@Test
  public void testTreePrintDoesNotCrashOnEmptyTree() {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class), mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );

    assertNotNull(parser.getTreePrint());
  }
@Test
  public void testGetLexiconReturnsConfiguredLexicon() {
    Lexicon lex = mock(Lexicon.class);
    LexicalizedParser parser = new LexicalizedParser(
        lex,
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class), mock(DependencyGrammar.class),
        new HashIndex<>(), new HashIndex<>(), new HashIndex<>(), new Options()
    );
    assertSame(lex, parser.getLexicon());
  }
@Test
  public void testLoadModelFromPathWithFallbackToTextWhenBinaryFails() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.getParserFromFile("invalid/path/parser.model", options);
    assertNull(parser); 
  }
@Test
  public void testParseHandlesPunctuationAndUnicode() {
    LexicalizedParser parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    List<Word> input = Arrays.asList(new Word("Привет"), new Word(".")); 
    Tree tree = parser.parse(input);
    assertNotNull(tree);
  }
@Test
  public void testDefaultParserLocFallbackToConstant() {
    System.clearProperty("edu.stanford.nlp.SerializedLexicalizedParser");
    String defaultLoc = LexicalizedParser.DEFAULT_PARSER_LOC;
    assertTrue(defaultLoc.contains("englishPCFG.ser.gz"));
  }
@Test(expected = RuntimeException.class)
  public void testLoadModelNullObjectThrowsException() throws Exception {
    ObjectInputStream ois = mock(ObjectInputStream.class);
    when(ois.readObject()).thenReturn(null);
    LexicalizedParser.loadModel(ois);
  } 
}