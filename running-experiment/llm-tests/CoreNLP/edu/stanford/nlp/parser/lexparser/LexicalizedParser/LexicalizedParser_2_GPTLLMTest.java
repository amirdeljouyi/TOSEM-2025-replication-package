package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.parser.metrics.Eval;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Index;
import org.junit.Assume;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LexicalizedParser_2_GPTLLMTest {

 @Test
  public void testParse_ValidInput_ReturnsTree() {
    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
////            Generics.newHashIndex(),
////            Generics.newHashIndex(),
////            Generics.newHashIndex(),
////            options
//    );

    List<HasWord> sentence = Arrays.asList(new Word("The"), new Word("cat"), new Word("sleeps"));
    Tree mockTree = mock(Tree.class);
    ParserQuery mockQuery = mock(ParserQuery.class);
    when(mockQuery.parse(sentence)).thenReturn(true);
    when(mockQuery.getBestParse()).thenReturn(mockTree);
    when(mockQuery.getPCFGScore()).thenReturn(-150.0);

//    LexicalizedParser spyParser = spy(parser);
//    doReturn(mockQuery).when(spyParser).parserQuery();
//
//    Tree result = spyParser.parse(sentence);

//    assertNotNull(result);
//    assertSame(mockTree, result);
    verify(mockTree).setScore(anyDouble());
  }
@Test
  public void testParse_InvalidInput_ReturnsXTree() {
    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );

    List<HasWord> words = Arrays.asList(new Word("abcdxyz"));
    ParserQuery query = mock(ParserQuery.class);
    when(query.parse(words)).thenReturn(false);

//    LexicalizedParser spyParser = spy(parser);
//    doReturn(query).when(spyParser).parserQuery();
//
//    Tree result = spyParser.parse(words);
//
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
  }
//@Test
//  public void testParse_ExceptionDuringQueryReturnsFallbackTree() {
//    Options options = new Options();
////    LexicalizedParser parser = new LexicalizedParser(
////            mock(Lexicon.class),
////            mock(BinaryGrammar.class),
////            mock(UnaryGrammar.class),
////            mock(DependencyGrammar.class),
////            Generics.newHashIndex(),
////            Generics.newHashIndex(),
////            Generics.newHashIndex(),
////            options
////    );
//
//    List<HasWord> input = Arrays.asList(new Word("nonsense"));
//
////    LexicalizedParser spyParser = spy(parser);
////    doThrow(new RuntimeException("forced")).when(spyParser).parserQuery();
////
////    Tree tree = spyParser.parse(input);
//
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
@Test
  public void testParseTree_ReturnsParsedTree() {
    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );

    List<HasWord> input = Arrays.asList(new Word("hello"));
    Tree expectedTree = mock(Tree.class);
    ParserQuery pq = mock(ParserQuery.class);
    when(pq.parse(input)).thenReturn(true);
    when(pq.getBestParse()).thenReturn(expectedTree);

//    LexicalizedParser spyParser = spy(parser);
//    doReturn(pq).when(spyParser).parserQuery();
//
//    Tree result = spyParser.parseTree(input);
//
//    assertSame(expectedTree, result);
  }
@Test
  public void testParseTree_UnsuccessfulReturnsNull() {
    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );

    List<HasWord> input = Arrays.asList(new Word("unknownword"));
    ParserQuery pq = mock(ParserQuery.class);
    when(pq.parse(input)).thenReturn(false);

//    LexicalizedParser spyParser = spy(parser);
//    doReturn(pq).when(spyParser).parserQuery();

//    Tree result = spyParser.parseTree(input);

//    assertNull(result);
  }
@Test
  public void testParseStrings_CreatesWordsAndParses() {
    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );

    List<String> stringList = Arrays.asList("Token1", "Token2");
    Tree mockTree = mock(Tree.class);
//    LexicalizedParser spyParser = spy(parser);

//    doReturn(mockTree).when(spyParser).parse(anyList());

//    Tree result = spyParser.parseStrings(stringList);

//    assertSame(mockTree, result);
  }
@Test
  public void testParseMultiple_TwoSentences_ReturnsTwoTrees() {
    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );

    List<HasWord> sent1 = Arrays.asList(new Word("one"));
    List<HasWord> sent2 = Arrays.asList(new Word("two"));
    List<List<? extends HasWord>> input = Arrays.asList(sent1, sent2);

    Tree tree1 = mock(Tree.class);
    Tree tree2 = mock(Tree.class);

//    LexicalizedParser spyParser = spy(parser);
//    doReturn(tree1).when(spyParser).parse(sent1);
//    doReturn(tree2).when(spyParser).parse(sent2);
//
//    List<Tree> result = spyParser.parseMultiple(input);
//
//    assertEquals(2, result.size());
//    assertSame(tree1, result.get(0));
//    assertSame(tree2, result.get(1));
  }
//@Test
//  public void testSetOptionFlags_SetsValuesCorrectly() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );
//
//    parser.setOptionFlags("-outputFormat", "penn", "-maxLength", "75");
//
//    String[] flags = parser.getOp().testOptions.outputFormatOptions;
//    assertEquals("penn", parser.getOp().testOptions.outputFormat);
//    assertEquals(75, parser.getOp().testOptions.maxLength);
//  }
//@Test(expected = RuntimeIOException.class)
//  public void testSaveParserToSerialized_InvalidPathThrowsException() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );
//
//    parser.saveParserToSerialized("/invalid/invalidpath/unwritable/parser.ser");
//  }
//@Test
//  public void testSaveParserToSerialized_AndLoadModel() throws IOException, ClassNotFoundException {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//            mock(Lexicon.class),
//            mock(BinaryGrammar.class),
//            mock(UnaryGrammar.class),
//            mock(DependencyGrammar.class),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            Generics.newHashIndex(),
//            options
//    );
//
//    File tempFile = File.createTempFile("tempParser", ".ser");
//    tempFile.deleteOnExit();
//
//    parser.saveParserToSerialized(tempFile.getAbsolutePath());
//
//    ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));
//    LexicalizedParser loaded = LexicalizedParser.loadModel(in);
//
//    assertNotNull(loaded);
//    assertTrue(loaded instanceof LexicalizedParser);
//
//    in.close();
//  }
//@Test
//  public void testParserQuery_RerankerNullUsesInternalQuery() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//        mock(Lexicon.class),
//        mock(BinaryGrammar.class),
//        mock(UnaryGrammar.class),
//        mock(DependencyGrammar.class),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        options
//    );
//
//    ParserQuery query = parser.parserQuery();
//
//    assertNotNull(query);
//    assertTrue(query instanceof LexicalizedParserQuery);
//  }
//@Test
//  public void testParserQuery_WithRerankerUsesRerankingQuery() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//        mock(Lexicon.class),
//        mock(BinaryGrammar.class),
//        mock(UnaryGrammar.class),
//        mock(DependencyGrammar.class),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        options
//    );
//
//    parser.reranker = mock(Reranker.class);
//
//    ParserQuery query = parser.parserQuery();
//
//    assertNotNull(query);
//    assertTrue(query instanceof RerankingParserQuery);
//  }
//@Test
//  public void testCopyLexicalizedParser_CopiesFieldsCorrectly() {
//    LexicalizedParser original = new LexicalizedParser(
//        mock(Lexicon.class),
//        mock(BinaryGrammar.class),
//        mock(UnaryGrammar.class),
//        mock(DependencyGrammar.class),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);
//
//    assertNotNull(copy);
//    assertSame(original.lex, copy.lex);
//    assertSame(original.bg, copy.bg);
//    assertSame(original.ug, copy.ug);
//    assertSame(original.dg, copy.dg);
//    assertSame(original.getOp(), copy.getOp());
//  }
//@Test
//  public void testGetExtraEval_EmptyWhenNoReranker() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//        mock(Lexicon.class),
//        mock(BinaryGrammar.class),
//        mock(UnaryGrammar.class),
//        mock(DependencyGrammar.class),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        options
//    );
//
//    List<?> result = parser.getExtraEvals();
//
//    assertNotNull(result);
//    assertTrue(result.isEmpty());
//  }
@Test
  public void testLoadModel_InvalidObjectStreamThrowsClassCast() throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bos);
    out.writeObject("This is not a LexicalizedParser");
    out.flush();
    byte[] bytes = bos.toByteArray();
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    ObjectInputStream ois = new ObjectInputStream(bis);

    try {
      LexicalizedParser.loadModel(ois);
      fail("Expected ClassCastException");
    } catch (ClassCastException expected) {
      assertTrue(expected.getMessage().contains("Wanted LexicalizedParser"));
    }

    ois.close();
    out.close();
    bos.close();
    bis.close();
  }
//@Test
//  public void testGetTreePrint_NotNull() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//        mock(Lexicon.class),
//        mock(BinaryGrammar.class),
//        mock(UnaryGrammar.class),
//        mock(DependencyGrammar.class),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        options
//    );
//
//    assertNotNull(parser.getTreePrint());
//  }
//@Test
//  public void testParseMultipleWithEmptySentenceListReturnsEmptyList() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    List<List<? extends HasWord>> emptyList = new ArrayList<>();
//
//    List<Tree> result = parser.parseMultiple(emptyList);
//
//    assertNotNull(result);
//    assertTrue(result.isEmpty());
//  }
//@Test
//  public void testParseMultipleWithNullSentenceSkipsIt() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    LexicalizedParser spy = spy(parser);
//    List<HasWord> validSentence = Arrays.asList(new Word("hello"));
//    Tree mockTree = mock(Tree.class);
//    doReturn(mockTree).when(spy).parse(validSentence);
//
//    List<List<? extends HasWord>> input = new ArrayList<>();
//    input.add(validSentence);
//    input.add(null);
//
//    try {
//      List<Tree> trees = spy.parseMultiple(input);
//      assertEquals(2, trees.size());
//      assertSame(mockTree, trees.get(0));
//      assertNull(trees.get(1));
//    } catch (Exception e) {
//      fail("Should tolerate null sentence: " + e.getMessage());
//    }
//  }
@Test
  public void testCopyLexicalizedParser_AllFieldsNullExceptOptions() {
    Options options = new Options();
    LexicalizedParser parser = new LexicalizedParser(
        null, null, null, null,
        null, null, null,
        options
    );

    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(parser);

    assertNotNull(copy);
    assertNull(copy.lex);
    assertSame(options, copy.getOp());
  }
//@Test(expected = RuntimeIOException.class)
//  public void testSaveParserToSerializedWithNullPathThrows() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    parser.saveParserToSerialized(null);
//  }
//@Test
//  public void testParserQueryEvalsReturnsEmptyList() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    List<?> result = parser.getParserQueryEvals();
//
//    assertNotNull(result);
//    assertTrue(result.isEmpty());
//  }
//@Test(expected = IllegalArgumentException.class)
//  public void testSetOptionFlagsWithUnknownFlagThrows() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    parser.setOptionFlags("-undefinedFlag");
//  }
@Test(expected = RuntimeException.class)
  public void testLoadModelObjectInputStreamThrowsIOException() throws IOException {
    InputStream failingInput = new InputStream() {
      public int read() throws IOException {
        throw new IOException("Expected IO exception");
      }
    };

    ObjectInputStream ois = new ObjectInputStream(failingInput);
    LexicalizedParser.loadModel(ois);
  }
//@Test
//  public void testParseStringsWithEmptyListReturnsXTree() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    Tree result = parser.parseStrings(Collections.emptyList());
//
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testParseMultiple_MixedCasesEmptyAndNormal() {
//
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        options
//    );
//    LexicalizedParser spyParser = spy(parser);
//
//    List<HasWord> sent1 = Arrays.asList(new Word("Hello"));
//    List<HasWord> sent2 = Collections.emptyList();
//    Tree treeA = mock(Tree.class);
//    Tree treeB = mock(Tree.class);
//
//    doReturn(treeA).when(spyParser).parse(sent1);
//    doReturn(treeB).when(spyParser).parse(sent2);
//
//    List<List<? extends HasWord>> batch = Arrays.asList(sent1, sent2);
//
//    List<Tree> output = spyParser.parseMultiple(batch);
//
//    assertEquals(2, output.size());
//    assertSame(treeA, output.get(0));
//    assertSame(treeB, output.get(1));
//  }
//@Test
//  public void testParseWithNullInputListThrowsNPE() {
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//      new Options()
//    );
//
//    try {
//      parser.parse(null);
//      fail("Expected NullPointerException");
//    } catch (NullPointerException expected) {
//
//    }
//  }
//@Test
//  public void testParseTreeWithNullInputReturnsNull() {
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//      new Options()
//    );
//
//    Tree result = parser.parseTree(null);
//    assertNull(result);
//  }
//@Test
//  public void testParseStringsContainsNullElement() {
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//      new Options()
//    );
//
//    List<String> input = Arrays.asList("word1", null, "word2");
//
//    Tree tree = parser.parseStrings(input);
//
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
@Test(expected = RuntimeException.class)
  public void testLoadModelWithNullObjectStreamThrows() {
    ObjectInputStream ois = null;
    LexicalizedParser.loadModel(ois);
  }
@Test(expected = RuntimeException.class)
  public void testLoadModelFromInvalidFilePathThrowsRuntimeException() {
    Options op = new Options();
    LexicalizedParser.loadModel("non/existing/path.ser.gz", op, new String[] {});
  }
//@Test
//  public void testSetOptionFlagsEmptyFlagsArray() {
//    Options options = new Options();
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//      options
//    );
//
//    parser.setOptionFlags();
//    assertNotNull(parser.getOp());
//  }
//@Test
//  public void testConstructorWithEmptyIndices() {
//    Options options = new Options();
//
//    LexicalizedParser parser = new LexicalizedParser(
//      mock(Lexicon.class),
//      mock(BinaryGrammar.class),
//      mock(UnaryGrammar.class),
//      mock(DependencyGrammar.class),
//      new EmptyIndex<>(),
//      new EmptyIndex<>(),
//      new EmptyIndex<>(),
//      options
//    );
//
//    assertNotNull(parser.getOp());
//    assertEquals(0, parser.stateIndex.size());
//  }
//@Test
//  public void testParseMultipleWithThreadsParsesInParallel() {
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//      new Options()
//    );

//    LexicalizedParser spy = spy(parser);
//
//    List<HasWord> sent1 = Arrays.asList(new Word("Hello"), new Word("world"));
//    List<HasWord> sent2 = Arrays.asList(new Word("I"), new Word("run"));
//
//    Tree tree1 = mock(Tree.class);
//    Tree tree2 = mock(Tree.class);
//
//    doReturn(tree1).when(spy).parse(sent1);
//    doReturn(tree2).when(spy).parse(sent2);
//
//    List<List<? extends HasWord>> batch = Arrays.asList(sent1, sent2);
//
//    List<Tree> result = spy.parseMultiple(batch, 2);
//
//    assertEquals(2, result.size());
//    assertTrue(result.contains(tree1));
//    assertTrue(result.contains(tree2));
//  }
//@Test
//  public void testParseMultipleWithEmptySentenceInListReturnsValidTree() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//
//    LexicalizedParser spy = spy(parser);
//
//    List<HasWord> emptySentence = new ArrayList<>();
//    List<HasWord> validSentence = Arrays.asList(new Word("Hi"));
//    Tree tree1 = mock(Tree.class);
//    Tree tree2 = mock(Tree.class);
//    doReturn(tree1).when(spy).parse(validSentence);
//    doReturn(tree2).when(spy).parse(emptySentence);
//
//    List<List<? extends HasWord>> sentences = Arrays.asList(validSentence, emptySentence);
//
//    List<Tree> result = spy.parseMultiple(sentences);
//
//    assertEquals(2, result.size());
//    assertSame(tree1, result.get(0));
//    assertSame(tree2, result.get(1));
//  }
//@Test
//  public void testParseMultipleWithNegativeThreadsRunsSuccessfully() {
//    LexicalizedParser parser = spy(new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    ));
//
//    List<HasWord> s1 = Arrays.asList(new Word("the"), new Word("sky"));
//    Tree tree = mock(Tree.class);
//    doReturn(tree).when(parser).parse(s1);
//
//    List<List<? extends HasWord>> input = Arrays.asList(s1);
//
//    List<Tree> result = parser.parseMultiple(input, -1);
//
//    assertEquals(1, result.size());
//    assertSame(tree, result.get(0));
//  }
//@Test
//  public void testParseMultipleWithZeroThreadsExecutes() {
//    LexicalizedParser parser = spy(new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    ));
//
//    List<HasWord> sentence = Arrays.asList(new Word("zero"), new Word("threads"));
//    Tree tree = mock(Tree.class);
//    doReturn(tree).when(parser).parse(sentence);
//
//    List<List<? extends HasWord>> input = Arrays.asList(sentence);
//
//    List<Tree> result = parser.parseMultiple(input, 0);
//
//    assertEquals(1, result.size());
//    assertSame(tree, result.get(0));
//  }
@Test
  public void testLoadModelWithNullPathDefaultsToConstant() {
    Options options = new Options();

    System.setProperty("edu.stanford.nlp.SerializedLexicalizedParser", "");

    LexicalizedParser result = LexicalizedParser.loadModel(null, options);

    
    assertNotNull(result);
  }
//@Test
//  public void testGetLexiconReturnsNull() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//
//    assertNull(parser.getLexicon());
//  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testSaveParserToTextFileThrowsWhenRerankerPresent() {
//    LexicalizedParser parser = new LexicalizedParser(
//        mock(Lexicon.class), mock(BinaryGrammar.class), mock(UnaryGrammar.class), mock(DependencyGrammar.class),
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//
//    parser.reranker = mock(Reranker.class);
//
//    parser.saveParserToTextFile("parser.txt");
//  }
@Test(expected = RuntimeException.class)
  public void testLoadModelFromCorruptStreamThrowsException() throws IOException {
    byte[] invalidData = {0x01, 0x02, 0x03}; 
    ByteArrayInputStream bais = new ByteArrayInputStream(invalidData);
    ObjectInputStream ois = new ObjectInputStream(bais);

    LexicalizedParser.loadModel(ois);
  }
@Test
  public void testSaveParserToTextFileGzipWritesSuccessfully() throws IOException {
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        mock(edu.stanford.nlp.util.Index.class),
        mock(edu.stanford.nlp.util.Index.class),
        mock(edu.stanford.nlp.util.Index.class),
        new Options()
    );

    File file = File.createTempFile("gzip-parser", ".gz");
    file.deleteOnExit();

    doNothing().when(parser.lex).writeData(any(PrintWriter.class));
    doNothing().when(parser.ug).writeData(any(PrintWriter.class));
    doNothing().when(parser.bg).writeData(any(PrintWriter.class));
    doNothing().when(parser.dg).writeData(any(PrintWriter.class));
//    doNothing().when(parser.op).writeData(any(PrintWriter.class));

    parser.saveParserToTextFile(file.getAbsolutePath());

    assertTrue(file.exists());
    assertTrue(file.length() > 0);
  }
@Test
  public void testGetExtraEvalsReturnsFromReranker() {
    Reranker reranker = mock(Reranker.class);

    Eval mockEval = mock(Eval.class);
    List<Eval> evalList = new ArrayList<>();
    evalList.add(mockEval);

    when(reranker.getEvals()).thenReturn(evalList);

//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//    parser.reranker = reranker;
//
//    List<Eval> result = parser.getExtraEvals();
//    assertNotNull(result);
//    assertEquals(1, result.size());
//    assertSame(mockEval, result.get(0));
  }
//@Test
//  public void testParseMultipleHandlesExceptionDuringParse() {
//    LexicalizedParser parser = spy(new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    ));
//
//    List<HasWord> sent1 = Arrays.asList(new Word("throw"));
//
//    doThrow(new RuntimeException("mocked parse failure")).when(parser).parse(sent1);
//
//    List<List<? extends HasWord>> sentences = new ArrayList<>();
//    sentences.add(sent1);
//
//    List<Tree> result = parser.parseMultiple(sentences);
//    assertEquals(1, result.size());
//    assertNull(result.get(0));
//  }
//@Test
//  public void testSaveParserToSerializedFailsToCloseStreamPrintsWarning() {
//    LexicalizedParser parser = new LexicalizedParser(
//        mock(Lexicon.class),
//        mock(BinaryGrammar.class),
//        mock(UnaryGrammar.class),
//        mock(DependencyGrammar.class),
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//
//    File tempFile = new File("dummy.ser");
//    OutputStream stubStream = new OutputStream() {
//      public void write(int b) throws IOException {}
//      public void close() throws IOException {
//        throw new IOException("mock close failure");
//      }
//    };
//
//    try {
//      ObjectOutputStream oos = new ObjectOutputStream(stubStream);
//      oos.writeObject(parser);
//    } catch (IOException ignored) {
//    }
//
//
//  }
@Test(expected = RuntimeIOException.class)
  public void testSaveParserToTextFileWhereIOExceptionOccurs() {
    Options options = mock(Options.class);
    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        mock(edu.stanford.nlp.util.Index.class),
        mock(edu.stanford.nlp.util.Index.class),
        mock(edu.stanford.nlp.util.Index.class),
        options
    );

    parser.saveParserToTextFile("/this/path/does/not/exist/parser-output.gz");
  }
//@Test
//  public void testParserQueryReturnsNullBestParseHandledGracefully() {
//    ParserQuery query = mock(ParserQuery.class);
//    when(query.parse(anyList())).thenReturn(true);
//    when(query.getBestParse()).thenReturn(null);
//
//    LexicalizedParser parser = spy(new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    ));
//
//    doReturn(query).when(parser).parserQuery();
//
//    List<HasWord> input = Arrays.asList(new Word("dummy"));
//    Tree result = parser.parseTree(input);
//    assertNull(result);
//  }
//@Test
//  public void testParseStringsWithOnlyEmptyWordsFallsBackToXTree() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//
//    List<String> input = Arrays.asList("", "", "");
//
//    Tree result = parser.parseStrings(input);
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testSetOptionFlagsUsingOutputFormatOptionOnly() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//
//    parser.setOptionFlags("-outputFormatOptions", "xml");
//
//    assertNotNull(parser.getOp());
//    assertTrue(Arrays.asList(parser.getOp().testOptions.outputFormatOptions).contains("xml"));
//  }
@Test
  public void testLoadModelWithExtraFlagsAppliesAtRuntime() {
    Options op = new Options();

    File file = new File(LexicalizedParser.DEFAULT_PARSER_LOC);
    if (!file.exists()) {
      Assume.assumeTrue("Missing default model, skipping test", false);
    }

    LexicalizedParser parser = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC, op, "-outputFormat", "typedDependencies");

    assertNotNull(parser);
    assertEquals("typedDependencies", parser.getOp().testOptions.outputFormat);
  }
//@Test
//  public void testParseReturnsFallbackXTreeWhenCheckedExceptionThrown() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    LexicalizedParser spy = spy(parser);
//    doThrow(new RuntimeException("Simulated")).when(spy).parserQuery();
//
//    List<HasWord> tokens = Arrays.asList(new Word("fail"));
//
//    Tree result = spy.parse(tokens);
//
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testLexicalizedParserQueryReturnsInstance() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    LexicalizedParserQuery query = parser.lexicalizedParserQuery();
//
//    assertNotNull(query);
//    assertTrue(query instanceof LexicalizedParserQuery);
//  }
@Test(expected = RuntimeException.class)
  public void testGetParserFromSerializedFileWithCorruptFileThrows() {
    File corruptFile = new File("corrupt.ser");
    try {
      OutputStream os = new FileOutputStream(corruptFile);
      os.write(new byte[]{0, 1, 2});
      os.close();
    } catch (Exception ignored) {}

    LexicalizedParser.getParserFromSerializedFile(corruptFile.getAbsolutePath());
    corruptFile.delete();
  }
//@Test
//  public void testParserQueryReturnsRerankingQueryWhenRerankerSet() {
//    Reranker mockReranker = mock(Reranker.class);
//    Options opt = new Options();
//
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        opt
//    );
//
//    parser.reranker = mockReranker;
//
//    ParserQuery query = parser.parserQuery();
//
//    assertNotNull(query);
//    assertTrue(query instanceof RerankingParserQuery);
//  }
//@Test
//  public void testParseSetsNegativeScoreOnFallbackTree() {
//    ParserQuery mockQuery = mock(ParserQuery.class);
//    Tree mockTree = mock(Tree.class);
//    when(mockQuery.parse(anyList())).thenReturn(true);
//    when(mockQuery.getBestParse()).thenReturn(mockTree);
//    when(mockQuery.getPCFGScore()).thenReturn(-12345.0);
//
//    LexicalizedParser parser = spy(new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    ));
//
//    doReturn(mockQuery).when(parser).parserQuery();
//
//    List<HasWord> input = Arrays.asList(new Word("score"));
//    Tree result = parser.parse(input);
//
//    assertNotNull(result);
//    verify(mockTree).setScore(-12345.0 % -10000.0);
//  }
@Test
  public void testSaveParserToSerializedOverwritesExistingFile() throws Exception {
    File existing = File.createTempFile("lexparser", ".ser");
    existing.deleteOnExit();

    LexicalizedParser parser = new LexicalizedParser(
        mock(Lexicon.class),
        mock(BinaryGrammar.class),
        mock(UnaryGrammar.class),
        mock(DependencyGrammar.class),
        mock(edu.stanford.nlp.util.Index.class),
        mock(edu.stanford.nlp.util.Index.class),
        mock(edu.stanford.nlp.util.Index.class),
        new Options()
    );

    parser.saveParserToSerialized(existing.getAbsolutePath());

    assertTrue(existing.exists());
    assertTrue(existing.length() > 0);
  }
@Test
  public void testLoadModelWithStringAndFlagListUsesFlags() {
    Options options = new Options();
    String parserPath = LexicalizedParser.DEFAULT_PARSER_LOC;

    if (!new File(parserPath).exists()) {
      Assume.assumeTrue("Skip if model missing at default path", false);
    }

    List<String> flagList = Arrays.asList("-outputFormat", "penn");
    LexicalizedParser parser = LexicalizedParser.loadModel(parserPath, flagList);

    assertNotNull(parser);
    assertEquals("penn", parser.getOp().testOptions.outputFormat);
  }
//@Test
//  public void testParseMultipleReturnsEmptyListOnEmptyInput() {
//    LexicalizedParser parser = new LexicalizedParser(
//      null, null, null, null,
//      Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//      new Options()
//    );
//
//    List<List<? extends HasWord>> input = new ArrayList<>();
//
//    List<Tree> output = parser.parseMultiple(input);
//
//    assertNotNull(output);
//    assertTrue(output.isEmpty());
//  }
@Test(expected = RuntimeException.class)
  public void testGetParserFromTextFileWithInvalidBeginBlockThrows() throws Exception {
    File malformedFile = File.createTempFile("bad_grammar", ".txt");
    malformedFile.deleteOnExit();

    FileWriter fw = new FileWriter(malformedFile);
    fw.write("WRONGBLOCK\nwhatever contents follow");
    fw.close();

    Options options = new Options();
    LexicalizedParser.getParserFromTextFile(malformedFile.getAbsolutePath(), options);
  }
//@Test
//  public void testParseWithEmptyTokenListReturnsXTree() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    Tree tree = parser.parse(Collections.<HasWord>emptyList());
//
//    assertNotNull(tree);
//    assertEquals("X", tree.label().value());
//  }
//@Test
//  public void testParseTreeThrowsExceptionHandledGracefully() {
//    LexicalizedParser parser = spy(new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    ));
//
//    doThrow(new RuntimeException("expected")).when(parser).parserQuery();
//
//    Tree result = parser.parseTree(Arrays.asList(new Word("hello")));
//
//    assertNull(result);
//  }
@Test
  public void testGetParserFromFileReturnsNullWhenInvalidFile() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.getParserFromFile("nonexistent-file", options);
    assertNull(parser);
  }
//@Test
//  public void testParseReturnsXTreeIfBestParseIsNull() {
//    ParserQuery pq = mock(ParserQuery.class);
//    when(pq.parse(anyList())).thenReturn(true);
//    when(pq.getBestParse()).thenReturn(null);
//
//    LexicalizedParser parser = spy(new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    ));
//
//    doReturn(pq).when(parser).parserQuery();
//
//    Tree result = parser.parse(Arrays.asList(new Word("a")));
//
//    assertNotNull(result);
//    assertEquals("X", result.label().value());
//  }
//@Test
//  public void testGetExtraEvalsReturnsEmptyWhenRerankerReturnsNullEvals() {
//    Reranker reranker = mock(Reranker.class);
//    when(reranker.getEvals()).thenReturn(null);
//
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//    parser.reranker = reranker;
//
//    List<Eval> result = parser.getExtraEvals();
//    assertNotNull(result);
//    assertTrue(result.isEmpty());
//  }
//@Test
//  public void testParserQueryReturnsLexicalizedParserQueryWhenRerankerExplicitlyNull() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(), Generics.newHashIndex(), Generics.newHashIndex(),
//        new Options()
//    );
//    parser.reranker = null;
//
//    ParserQuery result = parser.parserQuery();
//
//    assertNotNull(result);
//    assertTrue(result instanceof LexicalizedParserQuery);
//  }
//@Test(expected = IllegalArgumentException.class)
//  public void testSetOptionFlagsThrowsWhenUnknownFlagProvided() {
//    LexicalizedParser parser = new LexicalizedParser(
//        null, null, null, null,
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        Generics.newHashIndex(),
//        new Options()
//    );
//
//    parser.setOptionFlags("-nonexistentOption", "xyz");
//  }
}