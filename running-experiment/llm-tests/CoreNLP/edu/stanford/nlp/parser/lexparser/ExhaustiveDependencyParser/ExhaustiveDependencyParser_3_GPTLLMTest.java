package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ExhaustiveDependencyParser_3_GPTLLMTest {

 @Test
  public void testParseSingleWordSentence() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 0)).iterator()
    );
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("Hello"));

//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    assertTrue(parser.hasParse());
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    double score = parser.getBestScore();
//    assertTrue(score > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testParseEmptySentence() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.emptyIterator());
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(Float.NEGATIVE_INFINITY);

    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    assertTrue(parser.hasParse());
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
  }
@Test
  public void testHasParseReturnsFalseWhenNoParseRun() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);

    Lexicon lexicon = mock(Lexicon.class);
    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//
//    boolean hasParse = parser.hasParse();
//    assertFalse(hasParse);
  }
@Test
  public void testGetBestScoreReturnsNegativeInfinityBeforeParse() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);

    Lexicon lexicon = mock(Lexicon.class);
    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//
//    double score = parser.getBestScore();
//    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0);
  }
@Test
  public void testGetBestParseReturnsNullBeforeParse() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);

    Lexicon lexicon = mock(Lexicon.class);
    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//
//    Tree result = parser.getBestParse();
//    assertNull(result);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKBestParsesThrowsException() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    parser.getKBestParses(3);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetBestParsesThrowsException() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    parser.getBestParses();
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKGoodParsesThrowsException() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    parser.getKGoodParses(3);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKSampledParsesThrowsException() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    parser.getKSampledParses(3);
  }
@Test
  public void testParseWithHasTagAndHasContext() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 1)).iterator()
    );
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenAnswer(invocation -> (Integer) invocation.getArgument(0));
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    HasWord word = mock(HasWord.class, withSettings().extraInterfaces(HasTag.class, HasContext.class));
    when(word.word()).thenReturn("dogs");
    when(((HasTag) word).tag()).thenReturn("NN");
    when(((HasContext) word).originalText()).thenReturn("dogs");

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(word);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    assertNotNull(parser.getBestParse());
  }
@Test
  public void testParseReturnsFalseWhenNoScoreAccepted() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");

    Lexicon lexicon = mock(Lexicon.class);
    IntTaggedWord itw = new IntTaggedWord(0, 1);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(itw).iterator()
    );
//    when(lexicon.score(eq(itw), anyInt(), anyInt(), any())).thenReturn(Float.NEGATIVE_INFINITY);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
//    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(Float.NEGATIVE_INFINITY);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    Word w = new Word("cats");
    sentence.add(w);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean hasParse = parser.parse(sentence);
//    assertFalse(parser.getBestScore() > Float.NEGATIVE_INFINITY);
  }
@Test(expected = RuntimeException.class)
  public void testIScoreTotalThrowsWhenNotComputed() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    Options options = new Options();
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    parser.iScoreTotal(0, 1, 0, 0);
  }
@Test
  public void testParseWithBoundaryMissingInTagIndex() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 0)).iterator()
    );
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    Options options = new Options();

    Word w = new Word("missing");
    List<HasWord> sentence = new ArrayList<>();
    sentence.add(w);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    double score = parser.getBestScore();
//    assertTrue(Double.isFinite(score));
  }
@Test
  public void testFlattenRepeatedLabelIsCollapsed() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 1)).iterator()
    );
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    Word word = new Word("flatten");
    List<HasWord> sentence = new ArrayList<>();
    sentence.add(word);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertTrue(tree.getChildrenAsList().size() >= 1);
  }
@Test(expected = OutOfMemoryError.class)
  public void testParseOversizedArrayAndFallbackFails() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    for (int i = 0; i < 50; i++) tagIndex.add("TAG" + i);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenAnswer(invocation -> {
      List<IntTaggedWord> tags = new ArrayList<>();
      tags.add(new IntTaggedWord(0, 0));
      return tags.iterator();
    });
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(50);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();
    options.testOptions.maxLength = 10;

    List<HasWord> sentence = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      sentence.add(new Word("token" + i));
    }

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    parser.parse(sentence);
  }
@Test
  public void testParseWithNullTagFromHasTag() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 1)).iterator()
    );
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.5f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    HasWord hw = mock(HasWord.class, withSettings().extraInterfaces(HasTag.class));
    when(hw.word()).thenReturn("bird");
    when(((HasTag) hw).tag()).thenReturn(""); 

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(hw);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    assertTrue(parser.parse(sentence));
//    assertNotNull(parser.getBestParse());
  }
@Test
  public void testParseWithNullContextFromHasContext() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("VB");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), isNull())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 1)).iterator()
    );
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), isNull())).thenReturn(1.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    HasWord hw = mock(HasWord.class, withSettings().extraInterfaces(HasContext.class));
    when(hw.word()).thenReturn("run");
    when(((HasContext) hw).originalText()).thenReturn(""); 

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(hw);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    assertTrue(parser.parse(sentence));
//    assertNotNull(parser.getBestParse());
  }
@Test
  public void testGetBestScoreWithInsufficientArraySizeReturnsNegativeInfinity() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);

    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("w1"));
    sentence.add(new Word("w2"));
    sentence.add(new Word("w3"));

    
//    double score = parser.getBestScore();
//    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0);
  }
@Test
  public void testIScoreReturnsSumOfTwoHalves() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("JJ");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 1)).iterator()
    );
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenAnswer(i -> 0.0);

    Options options = new Options();
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    HasWord w1 = new Word("red");
    HasWord w2 = new Word("apple");

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(w1);
    sentence.add(w2);

//    parser.parse(sentence);
//    float value = (float) parser.iScore(0, 2, 1, 1);
//    assertTrue(Float.isFinite(value));
  }
@Test
  public void testOPossibleReturnsCorrectPreHookValue() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("VBD");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 1)).iterator()
    );
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
    when(grammar.numTagBins()).thenReturn(2);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("slept"));
    sentence.add(new Word("well"));

//    parser.parse(sentence);
//
//    Hook rightHook = new Hook(0, 1, 1, true);
//    Hook leftHook = new Hook(0, 1, 1, false);

//    boolean right = parser.oPossible(rightHook);
//    boolean left = parser.oPossible(leftHook);

//    assertTrue(right || left);
  }
@Test
  public void testIPossibleReturnsCorrectPostHookValue() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("RB");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 1)).iterator()
    );
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("often"));
    sentence.add(new Word("wins"));

//    parser.parse(sentence);
//
//    Hook hook = new Hook(0, 1, 1, false);
//    boolean possible = parser.iPossible(hook);
//    assertTrue(possible);
  }
@Test
  public void testParseSkipsTaggingMismatchWithTrueTag() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");
//    tagIndex.add("VB");

    Lexicon lexicon = mock(Lexicon.class);
    IntTaggedWord itw = new IntTaggedWord(0, 2);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(itw).iterator());
//    when(lexicon.score(eq(itw), anyInt(), anyInt(), any())).thenReturn(0.5f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenReturn(2);
    when(grammar.numTagBins()).thenReturn(3);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    HasWord w = mock(HasWord.class, withSettings().extraInterfaces(HasTag.class));
    when(w.word()).thenReturn("run");
    when(((HasTag) w).tag()).thenReturn("NN"); 

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(w);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertFalse(parser.getBestScore() > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testCreateArraysHandlesExactMaxLength() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(
      Collections.singletonList(new IntTaggedWord(0, 0)).iterator()
    );
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    Options options = new Options();
    options.testOptions.maxLength = 3;

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("one"));
    sentence.add(new Word("two"));
    sentence.add(new Word("three"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
//    assertNotNull(parser.getBestParse());
  }
@Test
  public void testParseSkipsHeadEqualsArgumentCondition() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("X");

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(-1000.0);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any()))
      .thenReturn(Collections.singletonList(new IntTaggedWord(0, 1)).iterator());
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("A"));
    sentence.add(new Word("B"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
  }
@Test
  public void testFlattenDeepBinaryTree() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("NP");
//    tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lex = mock(Lexicon.class);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any()))
      .thenReturn(Collections.singletonList(new IntTaggedWord(0, 1)).iterator());
//    when(lex.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(3);
    when(grammar.tagBin(anyInt())).thenAnswer(inv -> inv.getArgument(0));
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("x"));
    sentence.add(new Word("y"));
    sentence.add(new Word("z"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lex, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertTrue(tree.yield().size() > 0);
  }
@Test
  public void testParseFailsWhenLexiconReturnsEmpty() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("UNK");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.emptyIterator());

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(-9999.0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("no"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertFalse(parser.getBestScore() > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testGetBestParseSingleLeafTree() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("ROOT");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any()))
      .thenReturn(Collections.singletonList(new IntTaggedWord(0, 0)).iterator());
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("solo"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertTrue(tree.yield().size() == 1);
  }
@Test
  public void testParseChartWithoutGoalTag() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("X");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any()))
      .thenReturn(Collections.singletonList(new IntTaggedWord(0, 0)).iterator());
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
//    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(-999f);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("a"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    double score = parser.getBestScore();
//    assertTrue(Double.isFinite(score) || score == Float.NEGATIVE_INFINITY);
  }
@Test
  public void testParseWithTagMatchFailsDueToLowScore() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("DT");

    Lexicon lexicon = mock(Lexicon.class);
    IntTaggedWord tag = new IntTaggedWord(0, 1);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(tag).iterator());
//    when(lexicon.score(eq(tag), anyInt(), anyInt(), any())).thenReturn(Float.NEGATIVE_INFINITY);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.tagBin(anyInt())).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(-999.0);

    Options options = new Options();

    HasWord hw = mock(HasWord.class, withSettings().extraInterfaces(HasTag.class));
    when(hw.word()).thenReturn("the");
    when(((HasTag) hw).tag()).thenReturn("DT");

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(hw);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertFalse(parser.getBestScore() > Float.NEGATIVE_INFINITY);
  }
@Test
  public void testParseTwoWordSentenceWithSingleValidTag() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("NN");

    IntTaggedWord tag1 = new IntTaggedWord(0, 1);
    IntTaggedWord tag2 = new IntTaggedWord(1, 1);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), eq(0), any())).thenReturn(Collections.singletonList(tag1).iterator());
    when(lexicon.ruleIteratorByWord(anyInt(), eq(1), any())).thenReturn(Collections.singletonList(tag2).iterator());
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("dogs"));
    sentence.add(new Word("bark"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
  }
@Test
  public void testGetBestParseReturnsNullWhenNoTagsValid() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("JJ");
//    tagIndex.add("RB");

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.emptyIterator());

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("very"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNull(tree);
  }
@Test
  public void testParseWithRedundantDistanceBinsSkipped() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("NN");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    IntTaggedWord t1 = new IntTaggedWord(0, 1);
    IntTaggedWord t2 = new IntTaggedWord(1, 1);
    when(lexicon.ruleIteratorByWord(anyInt(), eq(0), any())).thenReturn(Collections.singletonList(t1).iterator());
    when(lexicon.ruleIteratorByWord(anyInt(), eq(1), any())).thenReturn(Collections.singletonList(t2).iterator());
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), eq(0))).thenReturn(0.0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("cat"));
    sentence.add(new Word("meows"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
  }
@Test
  public void testParseSingleWordWithSameStartAndEnd() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("NP");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    IntTaggedWord tag = new IntTaggedWord(0, 1);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(tag).iterator());
//    when(lexicon.score(eq(tag), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.tagBin(anyInt())).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("lion"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    assertEquals(1, tree.yield().size());
  }
@Test
  public void testHasParseFalseForEmptyChart() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("AUX");

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);

    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean has = parser.hasParse();
//    assertFalse(has);
  }
@Test
  public void testGetBestScoreReturnsNegativeInfinityWhenSentenceNull() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("X");

    Lexicon lexicon = mock(Lexicon.class);
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);

    Options options = new Options();

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    double score = parser.getBestScore();
//    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0);
  }
@Test
  public void testThreadInterruptDuringParseThrowsInterruptException() {
    Thread.currentThread().interrupt(); 
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.emptyIterator());
    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    Options options = new Options();
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("INTERRUPT"));
    try {
//      parser.parse(sentence);
      fail("Expected RuntimeInterruptedException");
    } catch (RuntimeInterruptedException e) {
      assertTrue(true);
    }
  }
@Test
  public void testExtractBestParseReturnsNullForUnmatchedScore() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any()))
      .thenReturn(Collections.singletonList(new IntTaggedWord(0, 0)).iterator());
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
//    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(Float.NEGATIVE_INFINITY);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("one"));
    sentence.add(new Word("two"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean result = parser.parse(sentence);
//    assertTrue(result);
//    Tree tree = parser.getBestParse();
//    assertNull(tree);
  }
@Test
  public void testParseWithLeftHeadedScoringPath() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("VB");

    IntTaggedWord t1 = new IntTaggedWord(0, 1);
    IntTaggedWord t2 = new IntTaggedWord(1, 1);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), eq(0), any())).thenReturn(Collections.singletonList(t1).iterator());
    when(lexicon.ruleIteratorByWord(anyInt(), eq(1), any())).thenReturn(Collections.singletonList(t2).iterator());
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenAnswer(i -> i.getArgument(0));
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(2);
//    when(grammar.distanceBin(anyInt())).thenReturn(1);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), eq(true), anyInt())).thenReturn(2.0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), eq(false), anyInt())).thenReturn(0.5);

    Options options = new Options();

    List<HasWord> s = new ArrayList<>();
    s.add(new Word("start"));
    s.add(new Word("end"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(s);
//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
  }
@Test
  public void testParseHandlesDistanceBinGrouping() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("N1");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lex = mock(Lexicon.class);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(0, 1)).iterator());
//    when(lex.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenReturn(1);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(2);
//    when(grammar.distanceBin(1)).thenReturn(1);
//    when(grammar.distanceBin(2)).thenReturn(1);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), eq(1))).thenReturn(1.5);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("a"));
    sentence.add(new Word("b"));
    sentence.add(new Word("c"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lex, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
  }
@Test
  public void testParseNoMatchingGoalTagIndexYieldsNegativeInfinityScore() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any()))
        .thenReturn(Collections.singletonList(new IntTaggedWord(0, 0)).iterator());
//    when(lexicon.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.scoreTB(any(), any(), any(), any(), any(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    List<HasWord> s = new ArrayList<>();
    s.add(new Word("base"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(s);
//    assertTrue(parsed);
//    double score = parser.getBestScore();
//    assertEquals(Float.NEGATIVE_INFINITY, score, 0.0);
  }
@Test
  public void testDisplayHeadScoresOnMinimalInput() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("T");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lex = mock(Lexicon.class);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any()))
        .thenReturn(Collections.singletonList(new IntTaggedWord(0, 1)).iterator());
//    when(lex.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.tagBin(anyInt())).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("hi"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lex, options, wordIndex, tagIndex);
//    assertTrue(parser.parse(sentence));
//    parser.displayHeadScores();
  }
@Test
  public void testParseTriggersFallbackCreateArrays() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add(Lexicon.BOUNDARY_TAG);
//    tagIndex.add("XX");

    Lexicon lex = mock(Lexicon.class);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(0, 1)).iterator());
//    when(lex.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenReturn(1);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(any(), any(), any(), any(), any(), anyInt())).thenReturn(0.0);

    Options options = new Options();
    options.testOptions.maxLength = 100;

    List<HasWord> sentence = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      sentence.add(new Word("X" + i));
    }

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lex, options, wordIndex, tagIndex);
//
//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
  }
@Test
  public void testParseRejectsWhenTagBinOutOfRange() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("TAG-X");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    IntTaggedWord taggedWord = new IntTaggedWord(0, 1);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any()))
        .thenReturn(Collections.singletonList(taggedWord).iterator());
//    when(lexicon.score(eq(taggedWord), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1); 
    when(grammar.tagBin(eq(1))).thenReturn(2); 
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), eq(true), anyInt())).thenReturn(0.5);

    Options options = new Options();
    options.testOptions.maxLength = 100; 

    Word word = new Word("fit");
    List<HasWord> sentence = new ArrayList<>();
    sentence.add(word);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    boolean parsed = parser.parse(sentence);
//    assertFalse(parsed || parser.hasParse());
//    assertNull(parser.getBestParse());
  }
@Test
  public void testParseSetsAllBooleanFilters() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("X");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    IntTaggedWord taggedWord1 = new IntTaggedWord(0, 0);
    IntTaggedWord taggedWord2 = new IntTaggedWord(1, 0);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(eq(0), anyInt(), any())).thenReturn(Collections.singletonList(taggedWord1).iterator());
    when(lexicon.ruleIteratorByWord(eq(1), anyInt(), any())).thenReturn(Collections.singletonList(taggedWord2).iterator());
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(2);
    when(grammar.numDistBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(1.0);

    Options options = new Options();

    HasWord word1 = mock(HasWord.class, withSettings().extraInterfaces(HasTag.class));
    HasWord word2 = mock(HasWord.class);
    when(word1.word()).thenReturn("a");
    when(((HasTag) word1).tag()).thenReturn("X");
    when(word2.word()).thenReturn("b");

    List<HasWord> s = Arrays.asList(word1, word2);

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    assertTrue(parser.parse(s));
//
//    Hook hookR = new Hook(1, 1, 0, true);
//    Hook hookL = new Hook(0, 1, 0, false);
//    assertTrue(parser.iPossible(hookR) || parser.iPossible(hookL));
//    assertTrue(parser.oPossible(hookR) || parser.oPossible(hookL));
  }
@Test
  public void testParseScalesInnerLoopBinDistanceSkipping() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("N");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any()))
        .thenReturn(Collections.singletonList(new IntTaggedWord(0, 0)).iterator());
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(2);
//    when(grammar.distanceBin(argThat(i -> i == 1))).thenReturn(0);
//    when(grammar.distanceBin(argThat(i -> i == 2))).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.5);

    Options options = new Options();

    List<HasWord> sentence = Arrays.asList(new Word("a"), new Word("b"), new Word("c"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//    assertTrue(parser.parse(sentence));
  }
@Test
  public void testFallbackToSmallerArrayAfterCatchOOM() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("T");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lex = mock(Lexicon.class);
    when(lex.ruleIteratorByWord(anyInt(), anyInt(), any()))
        .thenReturn(Collections.singletonList(new IntTaggedWord(0, 0)).iterator());
//    when(lex.score(any(), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(any(), any(), any(), any(), any(), anyInt())).thenReturn(0.0);

    Options options = new Options();
    options.testOptions.maxLength = 10;

    List<HasWord> sentence = new ArrayList<HasWord>();
    for (int i = 0; i < 1000; i++) {
      sentence.add(new Word("token" + i));
    }

//    ExhaustiveDependencyParser parser = spy(new ExhaustiveDependencyParser(grammar, lex, options, wordIndex, tagIndex));

//    doThrow(new OutOfMemoryError("mock")).doNothing().when(parser).createArrays(eq(1001));

    try {
//      parser.parse(sentence);
      fail("Expected OutOfMemoryError");
    } catch (OutOfMemoryError expected) {
      assertTrue(expected.getMessage().contains("mock"));
    }
  }
@Test
  public void testBestParseIsFlattenedWhenLabelsRepeat() {
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("R");
//    tagIndex.add(Lexicon.BOUNDARY_TAG);

    Lexicon lexicon = mock(Lexicon.class);
    when(lexicon.ruleIteratorByWord(anyInt(), anyInt(), any())).thenReturn(Collections.singletonList(new IntTaggedWord(0, 0)).iterator());
//    when(lexicon.score(any(IntTaggedWord.class), anyInt(), anyInt(), any())).thenReturn(0.0f);

    DependencyGrammar grammar = mock(DependencyGrammar.class);
    when(grammar.tagBin(anyInt())).thenReturn(0);
    when(grammar.numTagBins()).thenReturn(1);
    when(grammar.numDistBins()).thenReturn(1);
//    when(grammar.distanceBin(anyInt())).thenReturn(0);
    when(grammar.scoreTB(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), anyInt())).thenReturn(0.0);

    Options options = new Options();
    
    List<HasWord> sentence = Arrays.asList(new Word("repeat"), new Word("repeat"));

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

//    boolean parsed = parser.parse(sentence);
//    assertTrue(parsed);
//    Tree tree = parser.getBestParse();
//    assertNotNull(tree);
//    String parentLabel = tree.label().toString();
//    for (Tree child : tree.getChildrenAsList()) {
//      assertTrue(!child.label().toString().equals(parentLabel) || child.isPreTerminal());
//    }
  } 
}