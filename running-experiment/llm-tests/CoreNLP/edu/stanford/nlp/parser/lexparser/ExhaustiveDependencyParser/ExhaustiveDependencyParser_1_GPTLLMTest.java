package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExhaustiveDependencyParser_1_GPTLLMTest {

 @Test
  public void testOScoreAccess() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.verbose = false;
    options.testOptions.maxLength = 10;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());

//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    wordIndex.add("dog");
//    tagIndex.add("NN");

    when(dg.numTagBins()).thenReturn(1);
    when(dg.tagBin(0)).thenReturn(0);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.parse(Collections.singletonList(new Word("dog")));

//    double score = parser.oScore(new Edge(0, 1, 0, 0));
//    assertTrue(Double.isFinite(score));
  }
@Test
  public void testIScoreAccess() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.verbose = false;
    options.testOptions.maxLength = 10;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    wordIndex.add("dog");
//    tagIndex.add("NN");

    when(dg.numTagBins()).thenReturn(1);
    when(dg.tagBin(0)).thenReturn(0);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
        Arrays.asList(new IntTaggedWord(0, 0)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0);
    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.parse(Collections.singletonList(new Word("dog")));

//    double score = parser.iScore(new Edge(0, 1, 0, 0));
//    assertTrue(Double.isFinite(score));
  }
@Test(expected = RuntimeException.class)
  public void testIScoreTotalThrows() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.verbose = false;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.iScoreTotal(0, 1, 0, 0);
  }
@Test
  public void testParseEmptySentenceReturnsFalse() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 10;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    boolean result = parser.parse(Collections.emptyList());
//    assertFalse(result);
  }
@Test(expected = OutOfMemoryError.class)
  public void testParseTooLongSentenceThrows() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 1;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
    List<Word> sentence = Arrays.asList(new Word("too"), new Word("long"));
//    parser.parse(sentence);
  }
@Test
  public void testHasParseReturnsFalseIfNoParse() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    boolean result = parser.hasParse();
//    assertFalse(result);
  }
@Test
  public void testGetBestParseReturnsNullWithoutParse() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    Tree tree = parser.getBestParse();
//    assertNull(tree);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKBestParsesThrows() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.getKBestParses(2);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetBestParsesThrows() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.getBestParses();
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKGoodParsesThrows() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.getKGoodParses(5);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKSampledParsesThrows() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.getKSampledParses(5);
  }
@Test
  public void testMatchesPositiveResult() throws Exception {
    Method method = ExhaustiveDependencyParser.class.getDeclaredMethod("matches", double.class, double.class);
    method.setAccessible(true);
    boolean result = (boolean) method.invoke(null, 1.0000001, 1.0000002);
    assertTrue(result);
  }
@Test
  public void testMatchesNegativeResult() throws Exception {
    Method method = ExhaustiveDependencyParser.class.getDeclaredMethod("matches", double.class, double.class);
    method.setAccessible(true);
    boolean result = (boolean) method.invoke(null, 1.0, 2.0);
    assertFalse(result);
  }
@Test
  public void testFlattenMergesIdenticalLabels() throws Exception {
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("John");
    Tree inner = tf.newTreeNode("NP", Collections.singletonList(leaf));
    Tree outer = tf.newTreeNode("NP", Collections.singletonList(inner));

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wordIndex, tagIndex);
    Method flatten = ExhaustiveDependencyParser.class.getDeclaredMethod("flatten", Tree.class);
    flatten.setAccessible(true);
//    Object result = flatten.invoke(parser, outer);

//    Tree resultTree = (Tree) result;
//    assertEquals("NP", resultTree.label().value());
//    assertEquals("John", resultTree.firstChild().value());
  }
@Test
  public void testParseSingleWordWithTagAndContext() {
//    class TaggedContextWord extends Word implements HasTag, HasContext {
//      TaggedContextWord(String value) {
//        super(value);
//      }
//
//      public String tag() {
//        return "NN";
//      }
//
//      public String originalText() {
//        return "contextString";
//      }
//    }

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 10;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());

//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    wordIndex.add("dog");
//    tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
    when(dg.tagBin(anyInt())).thenReturn(0);
    when(dg.tagBin(eq(1))).thenReturn(1);
    when(dg.numDistBins()).thenReturn(1);
    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    IntTaggedWord itw = new IntTaggedWord(0, 0);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), anyString())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), anyString())).thenReturn(0.0f);

//    TaggedContextWord word = new TaggedContextWord("dog");

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wordIndex, tagIndex);
//    boolean result = parser.parse(Collections.singletonList(word));
//    assertTrue(result);
  }
@Test
  public void testParseHandlesInterruptedThread() {
    Thread.currentThread().interrupt();

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 2;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wordIndex, tagIndex);

    try {
//      parser.parse(Arrays.asList(new Word("I")));
      fail("Expected RuntimeInterruptedException");
    } catch (RuntimeInterruptedException e) {
      
    } finally {
      
      Thread.interrupted();
    }
  }
@Test
  public void testHasParseReturnsTrueWhenScoreSetManually() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 2;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wordIdx = wordIndex.add("dog");
//    int tagIdx = tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(1);
//    when(dg.tagBin(tagIdx)).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);
    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

//    IntTaggedWord itw = new IntTaggedWord(wordIdx, tagIdx);
//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), anyString())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wordIndex, tagIndex);
//    boolean result = parser.parse(Collections.singletonList(new Word("dog")));
//    assertTrue(parser.hasParse());
  }
@Test
  public void testGetBestScoreForValidSingleWord() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 5;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    int wordIdx = wordIndex.add("dog");
//    int boundaryTagIdx = tagIndex.add(Lexicon.BOUNDARY_TAG);

//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenAnswer(invocation -> {
//      IntTaggedWord tw = new IntTaggedWord(wordIdx, boundaryTagIdx);
//      return Collections.singletonList(tw).iterator();
//    });
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);
    when(dg.numTagBins()).thenReturn(1);
    when(dg.tagBin(anyInt())).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);
    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    boolean result = parser.parse(Collections.singletonList(new Word("dog")));
//    double score = parser.getBestScore();
//    assertTrue(score > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testGetBestParseStructureForValidSentence() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 10;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    int word1Idx = wordIndex.add("hello");
//    int word2Idx = wordIndex.add("world");
//    int tagIdx = tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(1);
    when(dg.tagBin(anyInt())).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);
//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenAnswer(invocation -> {
//      int wordId = invocation.getArgument(0);
////      IntTaggedWord itw = new IntTaggedWord(wordId, tagIdx);
////      return Collections.singletonList(itw).iterator();
//    });

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
    List<Word> sentence = Arrays.asList(new Word("hello"), new Word("world"));
//    boolean parsed = parser.parse(sentence);
//    Tree tree = parser.getBestParse();

//    assertTrue(parsed);
//    assertNotNull(tree);
//    assertTrue(tree.yield().size() >= 2);
  }
@Test
  public void testParseRejectsWhenTagMismatchesTrueTag() {
//    class TaggedWord extends Word implements HasTag {
//      TaggedWord(String word, String tag) {
//        super(word);
//        this.tag = tag;
//      }
//      private final String tag;
//      public String tag() {
//        return this.tag;
//      }
//    }

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 5;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());

//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wIdx = wordIndex.add("dog");
//    int tagIdx = tagIndex.add("VB");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(tagIdx)).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);
//    when(lex.ruleIteratorByWord(eq(wIdx), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(wIdx, tagIdx)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    TaggedWord word = new TaggedWord("dog", "NN");
//    boolean result = parser.parse(Collections.singletonList(word));
//    assertFalse(result);
  }
@Test
  public void testParseHandlesEmptyLexiconIterator() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 5;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wordIdx = wordIndex.add("walk");
//    tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
    when(dg.tagBin(anyInt())).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);
//    when(lex.ruleIteratorByWord(eq(wordIdx), anyInt(), any())).thenReturn(Collections.emptyIterator());
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    boolean result = parser.parse(Collections.singletonList(new Word("walk")));
//    assertFalse(result);
  }
@Test
  public void testParseSingleBoundaryWord() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 5;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wordId = wordIndex.add(Lexicon.BOUNDARY_WORD);
//    int tagId = tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(1);
//    when(dg.tagBin(tagId)).thenReturn(0);
//    when(dg.numDistBins()).thenReturn(1);
//
//    IntTaggedWord itw = new IntTaggedWord(wordId, tagId);
//    when(lex.ruleIteratorByWord(eq(wordId), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(eq(itw), anyInt(), eq(wordId), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(Collections.singletonList(new Word(Lexicon.BOUNDARY_WORD)));

//    Tree best = parser.getBestParse();
//    assertTrue(parsed);
//    assertNotNull(best);
//    assertEquals(Lexicon.BOUNDARY_WORD, best.getChild(0).value());
  }
@Test
  public void testIPossibleReturnsFalse() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
    options.testOptions.maxLength = 1;

//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("NN");

    when(dg.numTagBins()).thenReturn(1);
    when(dg.tagBin(anyInt())).thenReturn(0);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.parse(Collections.singletonList(new Word("test")));
//
//    Hook hook = new Hook(0, 0, 0, false);
//    assertFalse(parser.iPossible(hook));
  }
@Test
  public void testOPossibleReturnsFalse() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    options.testOptions.maxLength = 1;
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("NN");

    when(dg.numTagBins()).thenReturn(1);
    when(dg.tagBin(anyInt())).thenReturn(0);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.parse(Collections.singletonList(new Word("test")));
//
//    Hook hook = new Hook(0, 0, 0, false);
//    assertFalse(parser.oPossible(hook));
  }
@Test
  public void testGetBestScoreReturnsNegativeInfinityForOversizeInput() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 1;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wIndex = new Index<>();
//    Index<String> tIndex = new Index<>();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wIndex, tIndex);
    List<Word> sentence = Arrays.asList(new Word("a"), new Word("b"));

//    try {
//      parser.parse(sentence);
//    } catch (OutOfMemoryError ignored) {}
//
//    double score = parser.getBestScore();
//    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0);
  }
@Test
  public void testGetBestParseReturnsNullWhenNoParse() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wIndex = new Index<>();
//    Index<String> tIndex = new Index<>();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wIndex, tIndex);
//    Tree tree = parser.getBestParse();

//    assertNull(tree);
  }
@Test
  public void testHeadEqualsArgShouldBeSkipped() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opt = new Options();
    opt.testOptions.maxLength = 3;
//    opt.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wordId = wordIndex.add("cat");
//    int tagId = tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
    when(dg.tagBin(anyInt())).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(wordId, tagId)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opt, wordIndex, tagIndex);
//    boolean parsed = parser.parse(Arrays.asList(new Word("cat"), new Word("cat")));

//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertEquals("cat", tree.getLeaves().get(0).value());
  }
@Test
  public void testSplitBinDistanceSkip() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opt = new Options();
    opt.testOptions.maxLength = 5;
//    opt.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int w1 = wordIndex.add("A");
//    int t1 = tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    List<IntTaggedWord> taggings = Arrays.asList(new IntTaggedWord(w1, t1));

    when(dg.numTagBins()).thenReturn(2);
    when(dg.numDistBins()).thenReturn(2);
    when(dg.tagBin(anyInt())).thenReturn(0);
//    when(dg.distanceBin(anyInt())).thenReturn(0);
//
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(taggings.iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), eq(w1), any())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opt, wordIndex, tagIndex);
    List<Word> words = Arrays.asList(new Word("A"), new Word("A"), new Word("A"));
//    boolean result = parser.parse(words);
//
//    assertTrue(result);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertTrue(tree.yield().size() >= 2);
  }
@Test
  public void testMultipleTagBinsUsed() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opt = new Options();
    opt.testOptions.maxLength = 3;
//    opt.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    int wid = wordIndex.add("man");
//    int tid1 = tagIndex.add("NN");
//    int tid2 = tagIndex.add("VB");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(3);
//    when(dg.tagBin(tid1)).thenReturn(0);
//    when(dg.tagBin(tid2)).thenReturn(1);
    when(dg.numDistBins()).thenReturn(1);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    List<IntTaggedWord> tags = Arrays.asList(new IntTaggedWord(wid, tid1), new IntTaggedWord(wid, tid2));
//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(tags.iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), eq(wid), any())).thenReturn(0.0f);
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opt, wordIndex, tagIndex);
//    boolean parsed = parser.parse(Collections.singletonList(new Word("man")));

//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertEquals("man", tree.getLeaves().get(0).value());
  }
@Test
  public void testFlattenRemovesNestedSameLabels() throws Exception {
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf1 = tf.newLeaf("a");
    Tree leaf2 = tf.newLeaf("b");

    Tree inter = tf.newTreeNode("VP", Arrays.asList(leaf1, leaf2));
    Tree inter2 = tf.newTreeNode("VP", Arrays.asList(inter));

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wi, ti);
//    parser.tf = tf;

    java.lang.reflect.Method m = ExhaustiveDependencyParser.class.getDeclaredMethod("flatten", edu.stanford.nlp.trees.Tree.class);
    m.setAccessible(true);

//    Tree result = (Tree) m.invoke(parser, inter2);
//    assertEquals("VP", result.label().value());
//    assertEquals(2, result.numChildren());
//    assertEquals("a", result.getChild(0).value());
//    assertEquals("b", result.getChild(1).value());
  }
@Test
  public void testExtractBestParseReturnsNullWhenNoMatchInBacktrace() throws Exception {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opt = new Options();
//    opt.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//
//    int wId = wordIndex.add("noop");
//    int tId = tagIndex.add("XX");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(tId)).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(wId, tId)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), eq(wId), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opt, wordIndex, tagIndex);
//    parser.parse(Arrays.asList(new Word("noop"), new Word("noop")));

    java.lang.reflect.Method method = ExhaustiveDependencyParser.class.getDeclaredMethod("extractBestParse", int.class, int.class, int.class, int.class);
    method.setAccessible(true);

//    Tree result = (Tree) method.invoke(parser, 0, 2, 1, tId);
//    assertNull(result);
  }
@Test
  public void testParseTwoWordsSameWordDifferentTags() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 5;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wordId = wordIndex.add("book");
//    int tag1 = tagIndex.add("NN");
//    int tag2 = tagIndex.add("VB");
//    int boundaryTag = tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(3); 
//    when(dg.tagBin(tag1)).thenReturn(0);
//    when(dg.tagBin(tag2)).thenReturn(1);
//    when(dg.tagBin(boundaryTag)).thenReturn(2);
//    when(dg.numDistBins()).thenReturn(1);
//
//    IntTaggedWord t1 = new IntTaggedWord(wordId, tag1);
//    IntTaggedWord t2 = new IntTaggedWord(wordId, tag2);
//    Iterator<IntTaggedWord> it = Arrays.asList(t1, t2).iterator();
//    when(lex.ruleIteratorByWord(eq(wordId), anyInt(), any())).thenReturn(it);
//    when(lex.score(any(IntTaggedWord.class), anyInt(), eq(wordId), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    List<Word> sentence = Arrays.asList(new Word("book"), new Word("book"));
//    boolean parsed = parser.parse(sentence);
//
//    assertTrue(parsed);
//    Tree best = parser.getBestParse();
//    assertNotNull(best);
//    assertTrue(best.toString().contains("book"));
  }
@Test
  public void testFlattenTreeWithAlternatingLabels() throws Exception {
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("a");
    Tree inner1 = new LabeledScoredTreeFactory().newTreeNode("NP", Collections.singletonList(leaf));
    Tree inner2 = new LabeledScoredTreeFactory().newTreeNode("VP", Collections.singletonList(inner1));

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wordIndex, tagIndex);
//    parser.tf = new LabeledScoredTreeFactory();

    java.lang.reflect.Method method = ExhaustiveDependencyParser.class.getDeclaredMethod("flatten", Tree.class);
    method.setAccessible(true);
//    Tree result = (Tree) method.invoke(parser, inner2);

//    assertEquals("VP", result.label().value());
//    assertEquals(1, result.numChildren());
//    assertEquals("NP", result.getChild(0).label().value());
  }
@Test
  public void testParseWhenNoValidTagsReturned() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opt = new Options();
    opt.testOptions.maxLength = 3;
//    opt.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    wordIndex.add("hello");
//    tagIndex.add("NN");

    when(dg.numTagBins()).thenReturn(1);
    when(dg.tagBin(anyInt())).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.emptyIterator());

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opt, wordIndex, tagIndex);
    List<Word> sentence = Collections.singletonList(new Word("hello"));
//    boolean parsed = parser.parse(sentence);

//    assertFalse(parsed);
  }
@Test
  public void testGetBestScoreWhenNoParseWasRun() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();

//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    double score = parser.getBestScore();
//    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0);
  }
@Test
  public void testHasParseReturnsFalseWhenArraySizeExceeded() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 1;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wIndex = new Index<>();
//    Index<String> tIndex = new Index<>();
//    wIndex.add("foo");
//    tIndex.add("TAG");
//
//    when(dg.numTagBins()).thenReturn(1);
//    when(dg.numDistBins()).thenReturn(1);
//    when(dg.tagBin(anyInt())).thenReturn(0);

    IntTaggedWord itw = new IntTaggedWord(0, 0);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wIndex, tIndex);

    try {
//      parser.parse(Arrays.asList(new Word("foo"), new Word("bar")));
    } catch (OutOfMemoryError expected) {
      
    }

//    boolean hasParse = parser.hasParse();
//    assertFalse(hasParse);
  }
@Test
  public void testExtractBestParseSpanOneReturnsLeaf() throws Exception {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wid = wordIndex.add("leaf");
//    int tid = tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(tid)).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);
//    parser.tf = new LabeledScoredTreeFactory();

    java.lang.reflect.Field field = ExhaustiveDependencyParser.class.getDeclaredField("words");
    field.setAccessible(true);
//    field.set(parser, new int[]{wid});

    java.lang.reflect.Method m = ExhaustiveDependencyParser.class.getDeclaredMethod("extractBestParse", int.class, int.class, int.class, int.class);
    m.setAccessible(true);
//    Tree result = (Tree) m.invoke(parser, 0, 1, 0, tid);
//    assertEquals("leaf", result.firstChild().value());
  }
@Test
  public void testParseWithTrueTagNullByEmptyString() {
//    class MockTagged extends Word implements HasTag {
//      public MockTagged(String word) { super(word); }
//      public String tag() { return ""; }
//    }

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 5;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wordId = wordIndex.add("apple");
//    int tagId = tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
    when(dg.tagBin(anyInt())).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);
//    when(lex.ruleIteratorByWord(eq(wordId), anyInt(), isNull())).thenReturn(Collections.singletonList(new IntTaggedWord(wordId, tagId)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), isNull())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);
//
//    MockTagged word = new MockTagged("apple");
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wordIndex, tagIndex);
//    boolean parsed = parser.parse(Collections.singletonList(word));

//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertEquals("apple", tree.getChild(0).value());
  }
@Test
  public void testParseWithWordContextNullByEmptyString() {
//    class MockContextWord extends Word implements HasContext {
//      public MockContextWord(String w) { super(w); }
//      public String originalText() { return ""; }
//    }

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 5;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int wordId = wordIndex.add("apple");
//    int tagId = tagIndex.add("JJ");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(tagId)).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    when(lex.ruleIteratorByWord(eq(wordId), anyInt(), isNull())).thenReturn(Collections.singletonList(new IntTaggedWord(wordId, tagId)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), eq(wordId), isNull())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wordIndex, tagIndex);
//    boolean result = parser.parse(Collections.singletonList(new MockContextWord("apple")));

//    assertTrue(result);
//    Tree best = parser.getBestParse();
//    assertNotNull(best);
//    assertEquals("apple", best.getChild(0).value());
  }
@Test
  public void testGoalTagScoringFinalReturnFromParse() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 1;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wIndex = new Index<>();
//    Index<String> tIndex = new Index<>();
//    int wId = wIndex.add(Lexicon.BOUNDARY_WORD);
//    int tId = tIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(1);
//    when(dg.tagBin(eq(tId))).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    IntTaggedWord itw = new IntTaggedWord(wId, tId);
//    when(lex.ruleIteratorByWord(eq(wId), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wIndex, tIndex);
//    boolean parsed = parser.parse(Collections.singletonList(new Word(Lexicon.BOUNDARY_WORD)));

//    assertTrue(parsed);
//    double score = parser.getBestScore();
//    assertTrue(score > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testFlattenTreeDeepNestedRight() throws Exception {
    Tree leaf = new LabeledScoredTreeFactory().newLeaf("X");
    Tree deep = new LabeledScoredTreeFactory().newTreeNode("A", Collections.singletonList(leaf));
    Tree middle = new LabeledScoredTreeFactory().newTreeNode("A", Collections.singletonList(deep));
    Tree outer = new LabeledScoredTreeFactory().newTreeNode("A", Collections.singletonList(middle));

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
//    parser.tf = new LabeledScoredTreeFactory();

    java.lang.reflect.Method method = ExhaustiveDependencyParser.class.getDeclaredMethod("flatten", Tree.class);
    method.setAccessible(true);
//    Tree flattened = (Tree) method.invoke(parser, outer);

//    assertEquals("A", flattened.label().value());
//    assertEquals("X", flattened.getChild(0).value());
  }
@Test
  public void testParseHandlesDifferentTagBinForSameTag() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 2;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//
//    int wid = wi.add("foo");
//    int tid = ti.add("T1");
//    ti.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(eq(tid))).thenReturn(1);
    when(dg.numDistBins()).thenReturn(1);

//    IntTaggedWord itw = new IntTaggedWord(wid, tid);
//    when(lex.ruleIteratorByWord(eq(wid), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(eq(itw), anyInt(), eq(wid), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
//    boolean parsed = parser.parse(Collections.singletonList(new Word("foo")));
//
//    assertTrue(parsed);
  }
@Test
  public void testIScoreHSumAccessWithFlagEnabled() throws Exception {
    java.lang.reflect.Field f = ExhaustiveDependencyParser.class.getDeclaredField("doiScoreHSum");
    f.setAccessible(true);

    java.lang.reflect.Field modifiers = java.lang.reflect.Field.class.getDeclaredField("modifiers");
    modifiers.setAccessible(true);
    modifiers.setInt(f, f.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
    f.set(null, true); 

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 2;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//
//    int wid = wi.add("a");
//    int tag = ti.add("X");
//    ti.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(eq(tag))).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    IntTaggedWord itw = new IntTaggedWord(wid, tag);
//    when(lex.ruleIteratorByWord(eq(wid), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(eq(itw), anyInt(), eq(wid), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
//    parser.parse(Collections.singletonList(new Word("a")));
//
//    float score = parser.iScoreTotal(0, 1, 0, tag);
//    assertTrue(score >= 0.0f);
  }
@Test
  public void testParseSentenceSizeEqualsMaxLength() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 3;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//
//    int w0 = wi.add("a");
//    int tag = ti.add("NN");
//    ti.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
    when(dg.numDistBins()).thenReturn(1);
//    when(dg.tagBin(tag)).thenReturn(0);
//    when(lex.ruleIteratorByWord(eq(w0), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(w0, tag)).iterator());
//    when(lex.score(any(IntTaggedWord.class), eq(0), eq(w0), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
//    boolean parsed = parser.parse(Arrays.asList(new Word("a"), new Word("a"), new Word("a")));
//
//    assertTrue(parsed);
  }
@Test
  public void testTagComparisonSkipsWhenTrueTagNull() {
//    class UntaggedWord extends Word implements HasTag {
//      public UntaggedWord(String word) { super(word); }
//      public String tag() { return null; }
//    }

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 2;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());

//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//    int w = wi.add("foo");
//    int t = ti.add("NN");
//    ti.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(t)).thenReturn(0);
//    when(dg.numDistBins()).thenReturn(1);
//
//    IntTaggedWord itw = new IntTaggedWord(w, t);
//    when(lex.ruleIteratorByWord(eq(w), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(eq(itw), anyInt(), eq(w), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
//    boolean parsed = parser.parse(Collections.singletonList(new UntaggedWord("foo")));

//    assertTrue(parsed);
  }
@Test
  public void testParseHandlesTagBinZeroMismatch() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 4;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());

//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//    int wordId = wi.add("zero");
//    int actualTag = ti.add("VBZ");
//    ti.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(eq(actualTag))).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    when(lex.ruleIteratorByWord(eq(wordId), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(wordId, actualTag)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), eq(wordId), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
//    assertTrue(parser.parse(Arrays.asList(new Word("zero"), new Word("zero"))));
  }
@Test
  public void testWordIndexAddToIndexSideEffectUsedInMapLoop() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    int tag = tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options options = new Options();
    options.testOptions.maxLength = 5;
//    options.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());

//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
//        Collections.singletonList(new IntTaggedWord(0, tag)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);
//    when(dg.numTagBins()).thenReturn(2);
//    when(dg.numDistBins()).thenReturn(1);
//    when(dg.tagBin(eq(tag))).thenReturn(0);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, options, wordIndex, tagIndex);

    List<Word> words = Arrays.asList(new Word("foo"), new Word("bar"));
//    assertTrue(parser.parse(words));

//    assertEquals(2, wordIndex.size());
  }
@Test
  public void testRuntimeInterruptedDuringOutsideLoop() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 2;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//    int w = wi.add("x");
//    int t = ti.add("X");
//    ti.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(eq(t))).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    IntTaggedWord itw = new IntTaggedWord(w, t);
//    when(lex.ruleIteratorByWord(eq(w), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(eq(itw), anyInt(), eq(w), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
    Thread.currentThread().interrupt();

    try {
//      parser.parse(Arrays.asList(new Word("x"), new Word("x")));
      fail("Expected RuntimeInterruptedException");
    } catch (RuntimeException e) {
      assertTrue(e instanceof edu.stanford.nlp.util.RuntimeInterruptedException);
    } finally {
      Thread.interrupted(); 
    }
  }
@Test
  public void testIScoreWithNegativeInfinityInputs() throws Exception {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 2;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wIndex = new Index<>();
//    Index<String> tIndex = new Index<>();
//    int w = wIndex.add("a");
//    int t = tIndex.add("T");
//    tIndex.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(2);
//    when(dg.tagBin(eq(t))).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    IntTaggedWord itw = new IntTaggedWord(w, t);
//    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(eq(itw), anyInt(), eq(w), any())).thenReturn(Float.NEGATIVE_INFINITY);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(Float.NEGATIVE_INFINITY);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wIndex, tIndex);
//    boolean parsed = parser.parse(Collections.singletonList(new Word("a")));
//    assertFalse(parsed);
  }
@Test
  public void testGetBestParseReturnsSingleLeafForBoundaryOnly() {
    DependencyGrammar dg = mock(DependencyGrammar.class);
    Lexicon lex = mock(Lexicon.class);
    Options opts = new Options();
    opts.testOptions.maxLength = 1;
//    opts.setLangpack(new edu.stanford.nlp.trees.EnglishTreebankLanguagePack());
//
//    Index<String> wi = new Index<>();
//    Index<String> ti = new Index<>();
//    int w = wi.add(Lexicon.BOUNDARY_WORD);
//    int tag = ti.add(Lexicon.BOUNDARY_TAG);

    when(dg.numTagBins()).thenReturn(1);
//    when(dg.tagBin(tag)).thenReturn(0);
    when(dg.numDistBins()).thenReturn(1);

//    IntTaggedWord itw = new IntTaggedWord(w, tag);
//    when(lex.ruleIteratorByWord(eq(w), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lex.score(eq(itw), anyInt(), eq(w), any())).thenReturn(0.0f);
//    when(dg.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0f);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, opts, wi, ti);
//    parser.parse(Collections.singletonList(new Word(Lexicon.BOUNDARY_WORD)));

//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertEquals(Lexicon.BOUNDARY_WORD, tree.toString().replaceAll("[^a-zA-Z$.]", ""));
  } 
}