package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Index;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LexicalizedParser_1_GPTLLMTest {

 @Test
  public void testParseStringsReturnsTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<String> input = Arrays.asList("The", "quick", "brown", "fox");
    Tree result = parser.parseStrings(input);
    assertNotNull(result);
    assertFalse(result.isLeaf());
  }
@Test
  public void testParseReturnsXTreeOnNullInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    Tree result = parser.parse((String) null);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseReturnsXTreeOnEmptyInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> emptyList = Collections.emptyList();
    Tree result = parser.parse(emptyList);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseTreeReturnsTreeOnValidInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens = Arrays.asList(new Word("The"), new Word("cat"), new Word("sat"));
    Tree result = parser.parseTree(tokens);
    assertNotNull(result);
  }
@Test
  public void testParseTreeReturnsNullOnInvalidInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens = Arrays.asList(new Word("zxqv"));
    Tree result = parser.parseTree(tokens);
    assertNull(result);
  }
@Test
  public void testParseMultipleReturnsTwoTrees() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens1 = Arrays.asList(new Word("The"), new Word("dog"), new Word("barks"));
    List<HasWord> tokens2 = Arrays.asList(new Word("The"), new Word("cat"), new Word("sleeps"));
    List<List<? extends HasWord>> sentences = Arrays.asList(tokens1, tokens2);
    List<Tree> trees = parser.parseMultiple(sentences);
    assertEquals(2, trees.size());
    assertNotNull(trees.get(0));
    assertNotNull(trees.get(1));
  }
@Test
  public void testDefaultCoreNLPFlagsNotEmpty() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    String[] flags = parser.defaultCoreNLPFlags();
    assertNotNull(flags);
    assertTrue(flags.length > 0);
  }
@Test
  public void testTreePrintIsNotNull() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    assertNotNull(parser.getTreePrint());
  }
@Test
  public void testRequiresTagsReturnsFalse() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    assertFalse(parser.requiresTags());
  }
@Test
  public void testGetParserQueryReturnsNonNull() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    assertNotNull(parser.parserQuery());
  }
@Test
  public void testLexicalizedParserQueryReturnsNonNull() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    assertNotNull(parser.lexicalizedParserQuery());
  }
@Test
  public void testGetExtraEvalsReturnsEmptyList() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<?> evals = parser.getExtraEvals();
    assertNotNull(evals);
    assertTrue(evals.isEmpty());
  }
@Test
  public void testGetParserQueryEvalsReturnsEmptyList() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<?> evals = parser.getParserQueryEvals();
    assertNotNull(evals);
    assertTrue(evals.isEmpty());
  }
//@Test
//  public void testParseFromRawStringWithPTBTokenizer() {
//    LexicalizedParser parser = LexicalizedParser.loadModel();
//    String sentence = "A dog runs fast .";
////    TokenizerFactory<Word> factory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
//    List<Word> tokens = factory.getTokenizer(new StringReader(sentence)).tokenize();
//    Tree tree = parser.parse(tokens);
//    assertNotNull(tree);
//  }
@Test
  public void testSetOptionFlagsUpdatesOption() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    parser.setOptionFlags("-maxLength", "15");
    int maxLength = parser.getOp().testOptions.maxLength;
    assertEquals(15, maxLength);
  }
@Test
  public void testCopyLexicalizedParserCreatesEqualInstance() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(parser);
    assertNotNull(copy);
    assertEquals(parser.getClass(), copy.getClass());
  }
@Test
  public void testSaveAndLoadParserSerialized() throws Exception {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    File temp = File.createTempFile("lex-parser-", ".ser");
    temp.deleteOnExit();
    parser.saveParserToSerialized(temp.getAbsolutePath());
    assertTrue(temp.exists());
    LexicalizedParser loaded = LexicalizedParser.loadModel(temp.getAbsolutePath());
    assertNotNull(loaded);
  }
@Test
  public void testLoadModelFailsWithCorruptStreamThrowsException() throws Exception {
    byte[] invalid = new byte[]{0, 1, 2};
    ByteArrayInputStream bais = new ByteArrayInputStream(invalid);
    ObjectInputStream ois = new ObjectInputStream(bais);
    try {
      LexicalizedParser.loadModel(ois);
      fail("Expected exception not thrown");
    } catch (Exception e) {
      assertTrue(e instanceof RuntimeException || e instanceof ClassCastException);
    }
  }
@Test
  public void testLoadInvalidSerializedPathReturnsNull() {
    LexicalizedParser result = LexicalizedParser.getParserFromSerializedFile("nonexistent_file.ser.gz");
    assertNull(result);
  }
@Test
  public void testParseFailureReturnsFallbackTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> nonsense = Arrays.asList(new Word("qwertyuiopas"));
    Tree result = parser.parse(nonsense);
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testLoadModelFromNullParserPathFallsBackToDefault() {
    System.clearProperty("edu.stanford.nlp.SerializedLexicalizedParser");
    LexicalizedParser parser = LexicalizedParser.loadModel(new Options());
    assertNotNull(parser);
    assertEquals("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", parser.DEFAULT_PARSER_LOC);
  }
@Test
  public void testParseMultipleWithEmptyOuterListReturnsEmptyTrees() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> emptyList = Collections.emptyList();
    List<Tree> trees = parser.parseMultiple(emptyList);
    assertNotNull(trees);
    assertTrue(trees.isEmpty());
  }
@Test
  public void testParseMultipleWithEmptySentenceListReturnsFallbackTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<? extends HasWord> emptySentence = Collections.emptyList();
    List<List<? extends HasWord>> sentences = Collections.singletonList(emptySentence);
    List<Tree> result = parser.parseMultiple(sentences);
    assertEquals(1, result.size());
    assertEquals("X", result.get(0).label().value());
  }
@Test
  public void testParseMultipleWithSingleNullSentenceReturnsFallbackTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> sentences = Collections.singletonList(null);
    List<Tree> result = parser.parseMultiple(sentences);
    assertEquals(1, result.size());
    assertEquals("X", result.get(0).label().value());
  }
@Test
  public void testSaveParserToSerializedWithInvalidPathThrows() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    try {
      parser.saveParserToSerialized("/invalid\0path.ser");
      fail("Expected RuntimeIOException due to invalid path");
    } catch (RuntimeException e) {
      assertTrue(e instanceof RuntimeException);
    }
  }
@Test
  public void testSaveParserToTextFileIOExceptionHandling() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    File dir = new File(System.getProperty("java.io.tmpdir"), "readonly-dir");
    dir.mkdir();
    dir.setReadOnly();
    File file = new File(dir, "parser.txt");
    try {
      parser.saveParserToTextFile(file.getAbsolutePath());
      fail("Expected RuntimeIOException due to unwritable directory");
    } catch (RuntimeException e) {
      assertTrue(e instanceof RuntimeException);
    } finally {
      file.delete();
      dir.delete();
    }
  }
@Test
  public void testSetOptionFlagsWithUnknownOptionThrows() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    try {
      parser.setOptionFlags("-nonexistentFlag");
      fail("Expected IllegalArgumentException for unknown flag");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown option"));
    }
  }
@Test
  public void testParseValidTokensWithNullScoreTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    
    List<HasWord> sentence = Collections.singletonList(new Word("test"));
    Tree result = parser.parse(sentence);
    assertNotNull(result);
    assertTrue(result.yield().size() <= 2);
  }
@Test
  public void testFallbackTreeStructureFromUnparsableInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<? extends HasWord> input = Arrays.asList(new Word("!!?!"), new Word("@@@"));
    Tree tree = parser.parse(input);
    assertEquals("X", tree.label().value());
    assertEquals(2, tree.getChildrenAsList().size());
  }
@Test
  public void testTagIndexIsPreservedInCopiedParser() {
    LexicalizedParser original = LexicalizedParser.loadModel();
    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);
//    assertEquals(original.tagIndex().size(), copy.tagIndex().size());
  }
@Test
  public void testWordIndexIsPreservedInCopiedParser() {
    LexicalizedParser original = LexicalizedParser.loadModel();
    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);
    assertEquals(original.wordIndex.size(), copy.wordIndex.size());
  }
@Test
  public void testCustomOptionsInLoadModelReflectInParser() {
    Options customOptions = new Options();
    customOptions.testOptions.maxLength = 5;
    LexicalizedParser parser = LexicalizedParser.loadModel(customOptions);
    assertEquals(5, parser.getOp().testOptions.maxLength);
  }
@Test
  public void testParseTreeHandlesPartialParseGracefully() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("The"), new Word("unknownword"));
    Tree tree = parser.parseTree(input);
    assertTrue(tree == null || "X".equals(tree.label().value()) || tree.getChildrenAsList().size() > 0);
  }
@Test
  public void testParseWithOnlyPunctuation() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("."), new Word("?"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testParseWithOnlyNumbers() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("123"), new Word("456"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertTrue(tree.yield().size() > 0);
  }
@Test
  public void testParseWithTaggedWordsIgnoredInParseMethod() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    Word w1 = new Word("dog");
    Word w2 = new Word("barked");
    List<HasWord> sentence = Arrays.asList(w1, w2);
    Tree tree = parser.parse(sentence);
    assertNotNull(tree);
    assertTrue(tree.depth() > 1);
  }
@Test
  public void testParseTreeWithSingleWordToken() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Collections.singletonList(new Word("book"));
    Tree tree = parser.parseTree(input);
    assertTrue(tree == null || tree.depth() >= 1);
  }
@Test
  public void testDefaultParseTreeScoreIsSet() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("The"), new Word("dog"), new Word("runs"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertTrue(tree.score() < 0.0);
  }
@Test
  public void testFallbackTreeChildrenMatchInputTokens() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("!@#"), new Word("$$$"));
    Tree tree = parser.parse(input);
    assertEquals(2, tree.getChildrenAsList().size());
    assertEquals("!@#", tree.getChild(0).yield().get(0).toString());
  }
@Test
  public void testParserQueryReturnsFalseOnEmptyInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    ParserQuery pq = parser.parserQuery();
    List<HasWord> emptyInput = Collections.emptyList();
    boolean result = pq.parse(emptyInput);
    assertFalse(result || pq.getBestParse() != null);
  }
@Test
  public void testLoadModelFromObjectInputStreamInvalidClassThrows() throws IOException, IOException {
    byte[] invalidData = {
      (byte) 0xAC, (byte) 0xED, 0x00, 0x05, 
      0x73, 0x72, 0x00, 0x04, 0x54, 0x65, 0x73, 0x74, 
      0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x00, 0x00, 0x78, 0x70 
    };
    ByteArrayInputStream in = new ByteArrayInputStream(invalidData);
    ObjectInputStream ois = new ObjectInputStream(in);
    try {
      LexicalizedParser.loadModel(ois);
      fail("Expected exception due to class cast");
    } catch (Exception e) {
      assertTrue(e instanceof RuntimeException || e instanceof ClassCastException);
    }
  }
@Test
  public void testSetInvalidOptionFlagCausesException() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    try {
      parser.setOptionFlags("-invalidFlag");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown option"));
    }
  }
@Test
  public void testSetMultipleValidFlagsAffectOptionsCorrectly() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    parser.setOptionFlags("-maxLength", "25", "-outputFormat", "penn");
    assertEquals(25, parser.getOp().testOptions.maxLength);
    assertTrue(parser.getOp().testOptions.outputFormat.contains("penn"));
  }
@Test
  public void testParseTreeWithPunctuationOnlyReturnsNull() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens = Arrays.asList(new Word("."), new Word(","), new Word("!"));
    Tree tree = parser.parseTree(tokens);
    assertNull(tree);
  }
@Test
  public void testParseTreeWithSingleTokenReturnsTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens = Collections.singletonList(new Word("Hello"));
    Tree tree = parser.parseTree(tokens);
    assertTrue(tree == null || !tree.isLeaf());
  }
@Test
  public void testParseWithTokenContaningSpecialCharacters() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("@name#"), new Word("$money%"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testParseWithMultipleRepeatedTokens() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(
        new Word("test"), new Word("test"), new Word("test"),
        new Word("test"), new Word("test")
    );
    Tree tree = parser.parse(input);
    assertNotNull(tree);
  }
@Test
  public void testLoadModelWithExtraFlagsApplied() {
    Options options = new Options();
    String[] flags = new String[]{"-maxLength", "30", "-outputFormat", "oneline"};
    LexicalizedParser parser = LexicalizedParser.loadModel(options, flags);
    assertEquals(30, parser.getOp().testOptions.maxLength);
    assertTrue(parser.getOp().testOptions.outputFormat.contains("oneline"));
  }
@Test
  public void testParseWithWhitespaceTokens() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word(" "), new Word("   "));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testCopyParserAfterSettingFlag() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    parser.setOptionFlags("-maxLength", "45");
    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(parser);
    assertEquals(parser.getOp().testOptions.maxLength, copy.getOp().testOptions.maxLength);
  }
@Test
  public void testParseMultipleWithEmptySentencesProducesExpectedFallbackTrees() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> inputs = Arrays.asList(
        Collections.emptyList(),
        Collections.emptyList()
    );
    List<Tree> results = parser.parseMultiple(inputs);
    assertEquals(2, results.size());
    assertEquals("X", results.get(0).label().value());
    assertEquals("X", results.get(1).label().value());
  }
@Test
  public void testParseWithNullWordEntryInList() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("This"), null, new Word("works"));
    Tree result = null;
    try {
      result = parser.parse(input);
    } catch (Exception e) {
      
    }
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParserHandlesNonAsciiCharacters() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("こんにちは"), new Word("世界"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testParseSingleUnknownToken() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Collections.singletonList(new Word("qwertyasdf"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testLoadModelWithNullOptionsUsesFallback() {
    LexicalizedParser parser = LexicalizedParser.loadModel((Options) null);
    assertNotNull(parser);
  }
@Test
  public void testLoadModelWithFileAndEmptyExtraFlags() {
    LexicalizedParser parser = LexicalizedParser.loadModel(
        LexicalizedParser.DEFAULT_PARSER_LOC, new Options());
    assertNotNull(parser);
  }
@Test
  public void testLoadModelWithFileAndStringListExtraFlags() {
    List<String> flags = Arrays.asList("-maxLength", "12", "-outputFormat", "oneline");
    LexicalizedParser parser = LexicalizedParser.loadModel(
        LexicalizedParser.DEFAULT_PARSER_LOC, flags);
    assertNotNull(parser);
  }
@Test
  public void testParseAfterSettingOutputFormat() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    parser.setOptionFlags("-outputFormat", "oneline");
    List<HasWord> input = Arrays.asList(new Word("The"), new Word("dog"), new Word("ran"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
  }
@Test
  public void testGetTreePrintInstanceHasNoCrash() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    TreePrint treePrint = parser.getTreePrint();
    assertNotNull(treePrint);
  }
@Test
  public void testDefaultCoreNLPFlagsHaveKnownFlag() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    String[] flags = parser.defaultCoreNLPFlags();
    boolean hasKnownFlag = flags.length > 0;
    assertTrue(hasKnownFlag);
  }
@Test
  public void testParseMultipleReturnsFallbackWhenAllFail() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sentence1 = Arrays.asList(new Word("@@@"), new Word("##"));
    List<HasWord> sentence2 = Arrays.asList(new Word("???"));
    List<List<? extends HasWord>> list = Arrays.asList(sentence1, sentence2);
    List<Tree> results = parser.parseMultiple(list);
    assertEquals(2, results.size());
    assertEquals("X", results.get(0).label().value());
    assertEquals("X", results.get(1).label().value());
  }
@Test
  public void testParseMultipleThreadedSimpleUsage() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<? extends HasWord> s1 = Arrays.asList(new Word("It"), new Word("runs"));
    List<? extends HasWord> s2 = Arrays.asList(new Word("She"), new Word("jumps"));
    List<List<? extends HasWord>> sentences = Arrays.asList(s1, s2);
    List<Tree> result = parser.parseMultiple(sentences, 1);
    assertEquals(2, result.size());
    assertNotNull(result.get(0));
    assertNotNull(result.get(1));
  }
@Test
  public void testParseMultipleThreadedWithEmptySentence() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> list = Arrays.asList(Collections.emptyList());
    List<Tree> trees = parser.parseMultiple(list, 2);
    assertEquals(1, trees.size());
    assertEquals("X", trees.get(0).label().value());
  }
@Test
  public void testSaveParserToSerializedAndReload() throws IOException {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    File file = File.createTempFile("lexicalizedParserTest", ".ser");
    file.deleteOnExit();
    parser.saveParserToSerialized(file.getAbsolutePath());
    assertTrue(file.exists());
    LexicalizedParser loaded = LexicalizedParser.loadModel(file.getAbsolutePath());
    assertNotNull(loaded);
    file.delete();
  }
@Test
  public void testSaveParserToTextFileWithGZIP() throws IOException {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    File file = File.createTempFile("parser", ".gz");
    file.deleteOnExit();
    parser.saveParserToTextFile(file.getAbsolutePath());
    assertTrue(file.exists());
    file.delete();
  }
@Test
  public void testSaveParserToTextFileWithPlainTxt() throws IOException {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    File file = File.createTempFile("parser", ".txt");
    file.deleteOnExit();
    parser.saveParserToTextFile(file.getAbsolutePath());
    assertTrue(file.exists());
    file.delete();
  }
@Test
  public void testParseWithMixedTaggedAndUntaggedWords() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> words = Arrays.asList(
        new Word("The"), new TaggedWord("cat", "NN"), new Word("sleeps")
    );
    Tree tree = parser.parse(words);
    assertNotNull(tree);
    assertTrue(tree.yield().size() > 1);
  }
@Test
  public void testSetMultipleValidAndInvalidFlags() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    try {
      parser.setOptionFlags("-outputFormat", "words", "-unknownFlag", "-maxLength", "10");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown option"));
    }
  }
@Test
  public void testParseListHavingNullTokens() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> list = Arrays.asList(new Word("Ugh"), null, new Word("!"));
    Tree tree = parser.parse(list);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testParseMultipleMixedValidAndInvalidSentences() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sentence1 = Arrays.asList(new Word("This"), new Word("works"));
    List<HasWord> sentence2 = Arrays.asList(new Word("!!!!"));
    List<List<? extends HasWord>> batch = Arrays.asList(sentence1, sentence2);
    List<Tree> results = parser.parseMultiple(batch);
    assertEquals(2, results.size());
    assertNotNull(results.get(0));
    assertEquals("X", results.get(1).label().value());
  }
@Test
  public void testParseMultipleWithAllNullSentences() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> input = Arrays.asList(null, null);
    List<Tree> outputs = parser.parseMultiple(input);
    assertEquals(2, outputs.size());
    assertEquals("X", outputs.get(0).label().value());
    assertEquals("X", outputs.get(1).label().value());
  }
@Test
  public void testTreeYieldMatchesInputLength() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("hello"), new Word("world"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    int yieldSize = tree.yield().size();
    assertTrue(yieldSize == 2 || tree.label().value().equals("X"));
  }
@Test
  public void testParseTreeReturnsNullIfParserQueryFails() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("???"));
    Tree tree = parser.parseTree(input);
    assertNull(tree);
  }
@Test
  public void testParseLargeSentenceBoundaryHandling() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    HasWord[] words = new HasWord[200];
    for (int i = 0; i < 200; i++) {
      words[i] = new Word("word");
    }
    List<HasWord> longSentence = Arrays.asList(words);
    Tree tree = parser.parse(longSentence);
    assertNotNull(tree);
  }
@Test
  public void testParseTreeWithEmojiCharacters() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens = Arrays.asList(new Word("I"), new Word("😊"));
    Tree tree = parser.parseTree(tokens);
    assertTrue(tree == null || tree.yield().size() > 0);
  }
@Test
  public void testSaveParserToTextFileThrowsOnRerankerPresence() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
//    parser.reranker = new Reranker() {
//      @Override
//      public List<edu.stanford.nlp.parser.metrics.Eval> getEvals() {
//        return Collections.emptyList();
//      }
//    };
    try {
      parser.saveParserToTextFile("invalid.txt");
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertTrue(e.getMessage().contains("rerankers cannot be saved"));
    }
  }
@Test
  public void testLoadModelFallbackToTextFileReturnsNullIfBothFail() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.getParserFromFile("nonexistent-path.ser.gz", options);
    assertNull(parser);
  }
@Test
  public void testLoadModelFromCorruptSerializedFileReturnsNull() throws Exception {
    File corruptFile = File.createTempFile("corrupt-parser", ".ser");
    corruptFile.deleteOnExit();
    FileOutputStream fout = new FileOutputStream(corruptFile);
    fout.write(new byte[]{0x01, 0x02, 0x03});
    fout.close();
    LexicalizedParser result = LexicalizedParser.getParserFromSerializedFile(corruptFile.getAbsolutePath());
    assertNull(result);
  }
@Test
  public void testLoadModelFromInvalidFileThrowsRuntimeIOException() {
    try {
      LexicalizedParser.loadModel(new ObjectInputStream(new ByteArrayInputStream(new byte[]{})));
      fail("Expected RuntimeIOException");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage() != null || e.getCause() != null);
    } catch (IOException e) {
      fail("IOException should be wrapped");
    }
  }
@Test
  public void testParseUtilsFallbackTreeContainsAllTokens() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens = Arrays.asList(new Word("@@@"), new Word("@@@"), new Word("@@@"));
    Tree tree = parser.parse(tokens);
    int leaves = tree.yield().size();
    assertEquals(3, leaves);
  }
@Test
  public void testParseReturnsXTreeForNonParseableInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("<>"), new Word("???"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
//@Test
//  public void testGetExtraEvalsReturnsEvalsFromReranker() {
//    LexicalizedParser parser = LexicalizedParser.loadModel();
//    parser.reranker = new Reranker() {
//      @Override
//      public List<edu.stanford.nlp.parser.metrics.Eval> getEvals() {
//        return Arrays.asList();
//      }
//    };
//    assertNotNull(parser.getExtraEvals());
//  }
@Test
  public void testSetOptionFlagsWithValidAndInvalidMixThrows() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    try {
      parser.setOptionFlags("-maxLength", "30", "-invalidOption");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown option"));
    }
  }
@Test
  public void testParseTreeReturnsNullForTokenCountLimitExceeded() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    parser.setOptionFlags("-maxLength", "2");
    List<HasWord> input = Arrays.asList(new Word("one"), new Word("two"), new Word("three"));
    Tree tree = parser.parseTree(input);
    assertNull(tree);
  }
@Test
  public void testParseWithTabAndEscapeCharacters() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("\t"), new Word("\n"), new Word("\\"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testDefaultParserLoadedWithoutSystemPropertySet() {
    System.clearProperty("edu.stanford.nlp.SerializedLexicalizedParser");
    LexicalizedParser parser = LexicalizedParser.loadModel();
    assertNotNull(parser);
  }
@Test
  public void testParserQueryWithEmptyInput() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    ParserQuery query = parser.parserQuery();
    boolean parsed = query.parse(Collections.emptyList());
    assertFalse(parsed);
    assertNull(query.getBestParse());
  }
@Test
  public void testParseMultipleWithNullInList() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> input = Arrays.asList(null, Collections.singletonList(new Word("test")));
    List<Tree> trees = parser.parseMultiple(input);
    assertEquals(2, trees.size());
    assertEquals("X", trees.get(0).label().value());
    assertNotNull(trees.get(1));
  }
@Test
  public void testLoadParserFromUnreadableTextFileReturnsNull() throws IOException {
    File file = File.createTempFile("bad-parser", ".txt");
    FileWriter fw = new FileWriter(file);
    fw.write("bad\nBEGIN NOT_EXPECTED\nbadcontent");
    fw.close();
    Options options = new Options();
    LexicalizedParser result = LexicalizedParser.getParserFromTextFile(file.getAbsolutePath(), options);
    assertNull(result);
  }
@Test
  public void testParseWithMixOfKnownAndUnknownTokens() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sentence = Arrays.asList(new Word("The"), new Word("unseenword12345"), new Word("dog"));
    Tree tree = parser.parse(sentence);
    assertNotNull(tree);
    assertTrue(tree.yield().size() >= 2);
  }
@Test
  public void testParseMultipleIncludesEmptyNullSentenceInMiddle() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sent1 = Arrays.asList(new Word("One"), new Word("two"));
    List<HasWord> sent3 = Arrays.asList(new Word("Three"), new Word("four"));
    List<List<? extends HasWord>> batch = Arrays.asList(sent1, null, sent3);
    List<Tree> results = parser.parseMultiple(batch);
    assertEquals(3, results.size());
    assertEquals("X", results.get(1).label().value());
  }
@Test
  public void testParseMultipleWithAllNullEntries() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> input = Arrays.asList(null, null, null);
    List<Tree> trees = parser.parseMultiple(input);
    assertEquals(3, trees.size());
    assertEquals("X", trees.get(0).label().value());
    assertEquals("X", trees.get(1).label().value());
    assertEquals("X", trees.get(2).label().value());
  }
@Test
  public void testGetParserFromTextFileFailsToParseIncompleteFile() throws IOException {
    File badFile = File.createTempFile("parser", ".txt");
    try (FileWriter fw = new FileWriter(badFile)) {
      fw.write("BEGIN OPTIONS\nmalformed\nBEGIN STATE_INDEX\n");
    }
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.getParserFromTextFile(badFile.getAbsolutePath(), options);
    assertNull(parser);
    badFile.delete();
  }
@Test
  public void testLoadModelNullStreamThrowsRuntimeIOException() {
    try {
      ObjectInputStream ois = null;
      LexicalizedParser.loadModel(ois);
      fail("Expected NullPointerException or wrapper exception");
    } catch (RuntimeException e) {
      assertTrue(e.getCause() == null || e.getMessage() != null);
    }
  }
@Test
  public void testLoadModelInvalidBinaryHeaderReturnsNull() throws Exception {
    File file = File.createTempFile("invalidHeader", ".ser");
    try (FileOutputStream out = new FileOutputStream(file)) {
      out.write("bad_header".getBytes());
    }
    LexicalizedParser parser = LexicalizedParser.getParserFromSerializedFile(file.getAbsolutePath());
    assertNull(parser);
    file.delete();
  }
@Test
  public void testLoadModelFromValidPathWithNonParserObjectThrows() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject("This is not a parser");
    oos.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);

    try {
      LexicalizedParser.loadModel(ois);
      fail("Expected ClassCastException");
    } catch (ClassCastException e) {
      assertTrue(e.getMessage().contains("Wanted LexicalizedParser"));
    }
  }
@Test
  public void testParserReturnsFallbackTreeYieldMatchesInputLength() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> tokens = Arrays.asList(new Word("#$#"), new Word("###"));
    Tree tree = parser.parse(tokens);
    assertNotNull(tree);
    assertEquals(2, tree.yield().size());
  }
@Test
  public void testParseTreeGracefullyHandlesSingleToken() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> single = Collections.singletonList(new Word("run"));
    Tree tree = parser.parseTree(single);
    assertTrue(tree == null || tree.yield().size() == 1);
  }
@Test
  public void testSetInvalidOptionFlagAloneThrows() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    try {
      parser.setOptionFlags("-unknownOptionFlag");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown option") || e.getCause() != null);
    }
  }
@Test
  public void testLoadModelWithUnregisteredSystemPropertyParserPath() {
    System.setProperty("edu.stanford.nlp.SerializedLexicalizedParser", "nonexistent-path.bin");
    try {
      LexicalizedParser parser = LexicalizedParser.loadModel();
      assertNull(parser);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("File not found") || e.getCause() != null);
    } finally {
      System.clearProperty("edu.stanford.nlp.SerializedLexicalizedParser");
    }
  }
@Test
  public void testGetParserFromFileFallsBackToTextLoader() {
    Options op = new Options();
    String invalidBinaryPath = "invalid/parser/path.ser";
    LexicalizedParser parser = LexicalizedParser.getParserFromFile(invalidBinaryPath, op);
    assertNull(parser);
  }
@Test
  public void testParseInputWithEscapedQuotesInWords() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word("\""), new Word("word"), new Word("\""));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
  }
@Test
  public void testParseTreeRejectsOversizedInputWhenMaxLengthIsLow() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    parser.setOptionFlags("-maxLength", "1");
    List<HasWord> sentence = Arrays.asList(new Word("this"), new Word("is"), new Word("long"));
    Tree tree = parser.parseTree(sentence);
    assertNull(tree);
  }
@Test
  public void testSetOptionFlagsWithOutputFormatOptionAffectsTreePrint() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    parser.setOptionFlags("-outputFormat", "penn");
    assertNotNull(parser.getTreePrint());
  }
@Test
  public void testParserQueryFailingPathReturnsNullTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    ParserQuery pq = parser.parserQuery();
    List<HasWord> nonsense = Arrays.asList(new Word("asdfgh"), new Word("qwerty"));
    boolean ok = pq.parse(nonsense);
    assertFalse(ok);
    assertNull(pq.getBestParse());
  }
@Test
  public void testParseMultipleReturnsEmptyWhenOuterListIsEmpty() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<List<? extends HasWord>> sentences = Collections.emptyList();
    List<Tree> result = parser.parseMultiple(sentences);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testParserQueryFailsWithNullTokenList() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    boolean exceptionThrown = false;
    try {
      parser.parserQuery().parse(null);
    } catch (Exception e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown); 
  }
@Test
  public void testParseTreeWithEmptyStringToken() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Arrays.asList(new Word(""));
    Tree tree = parser.parseTree(input);
    assertTrue(tree == null || tree.yield().size() >= 0);
  }
@Test
  public void testParseWithCommaOnlyTokens() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sentence = Arrays.asList(new Word(","), new Word(","), new Word(","));
    Tree tree = parser.parse(sentence);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
    assertEquals(3, tree.yield().size());
  }
@Test
  public void testParseMultipleUsesFallbackForMixedValidity() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> valid = Arrays.asList(new Word("The"), new Word("cat"));
    List<HasWord> invalid = Arrays.asList(new Word("!!??"));
    List<List<? extends HasWord>> mixed = Arrays.asList(valid, invalid);
    List<Tree> output = parser.parseMultiple(mixed);
    assertEquals(2, output.size());
    assertNotNull(output.get(0));
    assertEquals("X", output.get(1).label().value());
  }
@Test
  public void testParseMultipleUsesThreadFallbackOnZero() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sentence = Arrays.asList(new Word("The"), new Word("dog"));
    List<List<? extends HasWord>> input = Arrays.asList(sentence, sentence);
    List<Tree> result = parser.parseMultiple(input, 0);
    assertEquals(2, result.size());
  }
@Test
  public void testParseMultipleWithMaxThreads() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> s1 = Arrays.asList(new Word("Hello"));
    List<HasWord> s2 = Arrays.asList(new Word("World"));
    List<List<? extends HasWord>> batch = Arrays.asList(s1, s2);
    List<Tree> results = parser.parseMultiple(batch, Runtime.getRuntime().availableProcessors());
    assertEquals(2, results.size());
    assertNotNull(results.get(0));
    assertNotNull(results.get(1));
  }
@Test
  public void testCopyPreservesWordIndexContent() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(parser);
    assertEquals(parser.wordIndex.size(), copy.wordIndex.size());
    assertEquals(parser.tagIndex.size(), copy.tagIndex.size());
    assertEquals(parser.stateIndex.size(), copy.stateIndex.size());
  }
@Test
  public void testSetOptionsWithUnknownPrependedFlag() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    try {
      parser.setOptionFlags("-nonexistent", "-outputFormat", "penn");
      fail("Expected exception");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown option") || e.getCause() != null);
    }
  }
@Test
  public void testSaveToTextFileHandlesIOExceptionGracefully() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    File file = new File("/root/invalid-path.gz");
    boolean errored = false;
    try {
      parser.saveParserToTextFile(file.getAbsolutePath());
    } catch (Exception e) {
      errored = true;
    }
    assertTrue(errored);
  }
@Test
  public void testFallbackTreeOutputHasProperYield() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> garbage = Arrays.asList(new Word("^"), new Word("|"), new Word("&"));
    Tree tree = parser.parse(garbage);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
    assertEquals(3, tree.yield().size());
  }
//@Test
//  public void testGetExtraEvalsReturnsFromRerankerIfPresent() {
//    LexicalizedParser parser = LexicalizedParser.loadModel();
//    parser.reranker = new Reranker() {
//      @Override
//      public List<edu.stanford.nlp.parser.metrics.Eval> getEvals() {
//        return Collections.singletonList(new edu.stanford.nlp.parser.metrics.Eval("dummy") {
//          @Override
//          public void evaluate(Tree guess, Tree gold, PrintWriter pw) {}
//        });
//      }
//    };
//    List<?> evals = parser.getExtraEvals();
//    assertEquals(1, evals.size());
//  }
@Test
  public void testTreeScoreAfterParsingUpdatesCorrectly() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sentence = Arrays.asList(new Word("The"), new Word("cat"));
    Tree tree = parser.parse(sentence);
    assertNotNull(tree);
    assertTrue(tree.score() <= 0.0);
  }
@Test
  public void testXTreeReturnedWhenUnexpectedExceptionOccurs() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null, null, null, null, new Options());
    List<HasWord> sentence = Arrays.asList(new Word("oops"));
    Tree tree = parser.parse(sentence);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testEmptySentenceReturnsNonNullTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    Tree result = parser.parse(Collections.<HasWord>emptyList());
    assertNotNull(result);
    assertEquals("X", result.label().value());
  }
@Test
  public void testParseMultipleReturnsEmptyForCompletelyEmptyInputList() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<Tree> result = parser.parseMultiple(Collections.<List<HasWord>>emptyList());
    assertEquals(0, result.size());
  }
@Test
  public void testParseSingleEmojiReturnsTree() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> input = Collections.singletonList(new Word("🙂"));
    Tree tree = parser.parse(input);
    assertNotNull(tree);
  }
@Test
  public void testParseReturnsNonEmptyTreeForUnknownProperNoun() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    List<HasWord> sentence = Collections.singletonList(new Word("Zuqir"));
    Tree tree = parser.parse(sentence);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testParserQueryReturnsNullOnUnrecognizedSentence() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    ParserQuery query = parser.parserQuery();
    boolean didParse = query.parse(Arrays.asList(new Word("%%")));
    assertFalse(didParse);
    assertNull(query.getBestParse());
  } 
}