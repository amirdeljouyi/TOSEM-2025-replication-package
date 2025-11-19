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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExhaustiveDependencyParser_2_GPTLLMTest {

 @Test
  public void testParseSimpleSentence() {
    Index<String> wordIndex = new HashIndex<>();
    wordIndex.add("the");
    wordIndex.add("cat");
    wordIndex.add(".");
    wordIndex.add(Lexicon.BOUNDARY_TAG);

    Index<String> tagIndex = new HashIndex<>();
    tagIndex.add("DT");
    tagIndex.add("NN");
    tagIndex.add(".");
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);

    Options options = new Options();
    options.testOptions.maxLength = 10;
    options.testOptions.verbose = false;
    options.doPCFG = false;

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<HasWord>();
    sentence.add(new TaggedWord("the", "DT"));
    sentence.add(new TaggedWord("cat", "NN"));
    sentence.add(new TaggedWord(".", "."));

//    boolean parsed = parser.parse(sentence);
//
//    assertTrue(parsed);
//    assertTrue(parser.hasParse());
//    assertTrue(parser.getBestScore() > Float.NEGATIVE_INFINITY);
//    assertNotNull(parser.getBestParse());
  }
@Test
  public void testParseEmptySentence() {
    Index<String> wordIndex = new HashIndex<>();
    wordIndex.add(Lexicon.BOUNDARY_TAG);

    Index<String> tagIndex = new HashIndex<>();
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);

    Options options = new Options();
    options.testOptions.maxLength = 2;
    options.testOptions.verbose = false;
    options.doPCFG = false;

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<HasWord>();

//    boolean parsed = parser.parse(sentence);
//
//    assertFalse(parsed);
//    assertFalse(parser.hasParse());
//    assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.01);
//    assertNull(parser.getBestParse());
  }
@Test(expected = OutOfMemoryError.class)
  public void testParseTooLongSentenceTriggersOOM() {
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();
    wordIndex.add("token0");
    tagIndex.add("DT");
    tagIndex.add(Lexicon.BOUNDARY_TAG);
    wordIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);

    Options options = new Options();
    options.testOptions.maxLength = 2;
    options.testOptions.verbose = false;
    options.doPCFG = false;

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<HasWord>();
    sentence.add(new Word("token0"));
    sentence.add(new Word("token0"));
    sentence.add(new Word("token0"));
    sentence.add(new Word("token0"));

//    parser.parse(sentence);
  }
@Test
  public void testIScoreAndOScoreConsistency() {
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();
    wordIndex.add("the");
    wordIndex.add("cat");
    wordIndex.add(".");
    wordIndex.add(Lexicon.BOUNDARY_TAG);

    tagIndex.add("DT");
    tagIndex.add("NN");
    tagIndex.add(".");
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);

    Options options = new Options();
    options.testOptions.maxLength = 10;
    options.testOptions.verbose = false;
    options.doPCFG = false;

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<HasWord>();
    sentence.add(new TaggedWord("the", "DT"));
    sentence.add(new TaggedWord("cat", "NN"));

//    parser.parse(sentence);

    int tagIndexNN = tagIndex.indexOf("NN");

//    Edge edge = new Edge(0, 2, 1, tagIndexNN);

//    double iScore = parser.iScore(edge);
//    double oScore = parser.oScore(edge);

//    assertTrue(iScore <= 0.0);
//    assertTrue(oScore <= 0.0);
  }
@Test
  public void testIPossibleAndOPossibleHooks() {
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();
    wordIndex.add("the");
    wordIndex.add("cat");
    wordIndex.add(".");
    wordIndex.add(Lexicon.BOUNDARY_TAG);

    tagIndex.add("DT");
    tagIndex.add("NN");
    tagIndex.add(".");
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndexex, wordIndex);

    Options options = new Options();
    options.testOptions.maxLength = 10;
    options.testOptions.verbose = false;
    options.doPCFG = false;

//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<HasWord>();
    sentence.add(new TaggedWord("the", "DT"));
    sentence.add(new TaggedWord("cat", "NN"));

//    parser.parse(sentence);

    int tag = tagIndex.indexOf("NN");

//    Hook hookRight = new FakeHook(0, 1, tag, true);
//    Hook hookLeft = new FakeHook(0, 1, tag, false);
//
//    boolean oPossibleRight = parser.oPossible(hookRight);
//    boolean oPossibleLeft = parser.oPossible(hookLeft);
//    boolean iPossibleRight = parser.iPossible(hookRight);
//    boolean iPossibleLeft = parser.iPossible(hookLeft);
//
//    assertTrue(oPossibleRight || oPossibleLeft);
//    assertTrue(iPossibleRight || iPossibleLeft);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testUnimplementedGetKBestParsesThrowsException() {
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();
    wordIndex.add("the");
    tagIndex.add("DT");
    wordIndex.add(Lexicon.BOUNDARY_TAG);
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
//
//    Options options = new Options();
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

//    parser.getKBestParses(1);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testUnimplementedGetBestParsesThrowsException() {
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();
    wordIndex.add(Lexicon.BOUNDARY_TAG);
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
//    Options options = new Options();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

//    parser.getBestParses();
  }
@Test(expected = UnsupportedOperationException.class)
  public void testUnimplementedGetKGoodParsesThrowsException() {
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();
    wordIndex.add(Lexicon.BOUNDARY_TAG);
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
//    Options options = new Options();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//
//    parser.getKGoodParses(3);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testUnimplementedGetKSampledParsesThrowsException() {
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();
    wordIndex.add(Lexicon.BOUNDARY_TAG);
    tagIndex.add(Lexicon.BOUNDARY_TAG);

//    FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//    FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
//    Options options = new Options();
//
//    ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
//
//    parser.getKSampledParses(2);
  }
@Test
public void testParseSingleWordSentence() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("cat");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add("NN");
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
  Options options = new Options();
  options.testOptions.maxLength = 5;
  options.testOptions.verbose = false;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("cat", "NN"));

//  boolean parsed = parser.parse(sentence);
//
//  assertTrue(parsed);
//  assertTrue(parser.hasParse());
//  assertNotNull(parser.getBestParse());
//  assertTrue(parser.getBestScore() > Float.NEGATIVE_INFINITY);
}
@Test
public void testParseSentenceWithUnknownTag() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("quick");
  wordIndex.add("fox");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add("JJ");
  tagIndex.add("NN");
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  FakeLexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      return Float.NEGATIVE_INFINITY;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.emptyIterator();
//    }
//  };

  Options options = new Options();
  options.testOptions.verbose = false;
  options.doPCFG = false;
  options.testOptions.maxLength = 5;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("quick", "ZX")); 
  sentence.add(new TaggedWord("fox", "NN"));

//  boolean parsed = parser.parse(sentence);
//
//  assertFalse(parsed);
//  assertFalse(parser.hasParse());
//  assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.01);
//  assertNull(parser.getBestParse());
}
@Test
public void testParseWithInterruptedThread() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("a");
  wordIndex.add("b");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add("X");
  tagIndex.add("Y");
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);

  Options options = new Options();
  options.testOptions.maxLength = 5;
  options.doPCFG = false;
  options.testOptions.verbose = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("a", "X"));
  sentence.add(new TaggedWord("b", "Y"));

  Thread.currentThread().interrupt(); 

  try {
//    parser.parse(sentence);
    fail("Expected RuntimeInterruptedException");
  } catch (RuntimeInterruptedException e) {
    
    Thread.interrupted(); 
  }
}
@Test
public void testGetBestScoreWithoutParseCall() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("x");
  tagIndex.add("T");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
  Options options = new Options();

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

//  assertFalse(parser.hasParse());
//  assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.01);
//  assertNull(parser.getBestParse());
}
@Test
public void testDependencyParserGoalTagMissing() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("m");
  wordIndex.add("n");
  
  tagIndex.add("A");
  tagIndex.add("B");

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
  Options options = new Options();
  options.testOptions.maxLength = 5;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("m", "A"));
  sentence.add(new TaggedWord("n", "B"));

//  boolean parsed = parser.parse(sentence);
//
//  assertTrue(parsed);
//  assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.01);
//  assertNull(parser.getBestParse());
}
@Test
public void testParseSentenceWithNullTagAndNullContextInHasTagAndHasContext() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("run");
  tagIndex.add("VB");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featureSpec) {
//      return 0.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featureSpec) {
//      IntTaggedWord itw = new IntTaggedWord(word, 0);
//      return Collections.singleton(itw).iterator();
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 5;
  options.testOptions.verbose = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
//  sentence.add(new HasWord() {
//    public String word() {
//      return "run";
//    }
//
//    public String toString() {
//      return "run";
//    }
//
//    @Override
//    public boolean equals(Object o) {
//      return false;
//    }
//
//    @Override
//    public int hashCode() {
//      return super.hashCode();
//    }
//  });

//  boolean result = parser.parse(sentence);
//
//  assertTrue(result);
//  assertTrue(parser.hasParse());
//  assertNotNull(parser.getBestParse());
}
@Test
public void testParseRejectsLexiconNegativeInfinityScore() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("x");
  tagIndex.add("X");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featureSpec) {
//      return Float.NEGATIVE_INFINITY;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featureSpec) {
//      IntTaggedWord itw = new IntTaggedWord(word, 0);
//      return Collections.singleton(itw).iterator();
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 5;
  options.testOptions.verbose = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("x", "X"));

//  boolean result = parser.parse(sentence);

//  assertFalse("Parser should reject when all lexicon scores are negative infinity", result);
}
@Test
public void testMultipleLexiconTagMatchesAccepted() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("bank");
  tagIndex.add("NN");
  tagIndex.add("VB");
  tagIndex.add(Lexicon.BOUNDARY_TAG);
  wordIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featureSpec) {
//      return 1.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featureSpec) {
//      IntTaggedWord itw1 = new IntTaggedWord(word, 0);
//      IntTaggedWord itw2 = new IntTaggedWord(word, 1);
//      return Arrays.asList(itw1, itw2).iterator();
//    }
//  };

  Options options = new Options();
  options.testOptions.verbose = false;
  options.testOptions.maxLength = 5;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("bank", "")); 

//  boolean result = parser.parse(sentence);
//
//  assertTrue("Expected parse to succeed with multiple positive tag matches from lexicon", result);
//  assertTrue(parser.hasParse());
//  assertNotNull(parser.getBestParse());
}
@Test
public void testGetBestParseReturnsNullWhenNoParseAvailable() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("nothing");
  tagIndex.add("NONE");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featureSpec) {
//      return Float.NEGATIVE_INFINITY;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featureSpec) {
//      return Collections.emptyIterator();
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 5;
  options.testOptions.verbose = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("nothing", "NONE"));

//  boolean parsed = parser.parse(sentence);
//  Tree best = parser.getBestParse();

//  assertFalse(parsed);
//  assertNull(best);
}
@Test
public void testParseSentenceWithBoundaryAlreadyPresent() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featureSpec) {
//      return 1.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featureSpec) {
//      IntTaggedWord itw = new IntTaggedWord(word, 0);
//      return Collections.singleton(itw).iterator();
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 3;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord(Lexicon.BOUNDARY_TAG, Lexicon.BOUNDARY_TAG));

//  boolean parsed = parser.parse(sentence);
//  Tree best = parser.getBestParse();

//  assertTrue(parsed);
//  assertNotNull(best);
//  assertNotNull(best.label());
}
@Test
public void testParseWithExactMaxAllowedLength() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("a"); wordIndex.add("b"); wordIndex.add("c");
  tagIndex.add("X"); tagIndex.add("Y"); tagIndex.add("Z"); tagIndex.add(Lexicon.BOUNDARY_TAG);
  wordIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 3;
  options.testOptions.verbose = false;
  options.doPCFG = false;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return 0.0f;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String feat) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };

//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int distanceBin(int dist) {
//      return 0;
//    }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 0.0f;
//    }
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("a", "X"));
  sentence.add(new TaggedWord("b", "Y"));
  sentence.add(new TaggedWord("c", "Z"));

//  boolean parsed = parser.parse(sentence);
//
//  assertTrue(parsed);
//  assertTrue(parser.hasParse());
//  assertTrue(parser.getBestScore() > Float.NEGATIVE_INFINITY);
//  assertNotNull(parser.getBestParse());
}
@Test
public void testCreateArraysManuallyWithLargeTagSize() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("word");
  tagIndex.add("NOUN");
  tagIndex.add("VERB");
  tagIndex.add("ADJ");
  tagIndex.add("ADV");
  tagIndex.add(Lexicon.BOUNDARY_TAG);
  wordIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 2;
  options.testOptions.verbose = false;
  options.doPCFG = false;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return 1.0f;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String feat) {
//      List<IntTaggedWord> list = new ArrayList<>();
//      list.add(new IntTaggedWord(word, 0));
//      list.add(new IntTaggedWord(word, 1));
//      list.add(new IntTaggedWord(word, 2));
//      return list.iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int distanceBin(int dist) {
//      return 0;
//    }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 0.0f;
//    }
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("word", "NOUN"));
  sentence.add(new TaggedWord("word", "VERB"));

//  boolean parsed = parser.parse(sentence);
//
//  assertTrue(parsed);
//  assertNotNull(parser.getBestParse());
}
@Test
public void testParseMultipleWordsWithTagMismatchFiltering() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("sky");
  wordIndex.add("is");
  tagIndex.add("JJ");
  tagIndex.add("VBZ");
  tagIndex.add("NN");
  tagIndex.add(Lexicon.BOUNDARY_TAG);
  wordIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 3;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return 1.0f;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String feat) {
//      IntTaggedWord itw1 = new IntTaggedWord(word, 0);
//      IntTaggedWord itw2 = new IntTaggedWord(word, 1);
//      return Arrays.asList(itw1, itw2).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int distanceBin(int dist) {
//      return 0;
//    }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 0.0f;
//    }
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("sky", "NN")); 
  sentence.add(new TaggedWord("is", ""));    

//  boolean parsed = parser.parse(sentence);
//  boolean hasParse = parser.hasParse();
//
//  assertTrue(parsed);
//  assertTrue(hasParse);
//  assertNotNull(parser.getBestParse());
}
@Test
public void testFlattenReturnsSameTreeForLeaf() {
  TreeFactory tf = new LabeledScoredTreeFactory();
  Tree leaf = tf.newLeaf("token");

  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("token");
  tagIndex.add("TAG");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();

//  FakeDependencyGrammar grammar = new FakeDependencyGrammar(tagIndex, wordIndex);
//  FakeLexicon lexicon = new FakeLexicon(tagIndex, wordIndex);
//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  Tree result = leaf;
//  boolean same = result == parser.getBestParse();
//  assertNull(parser.getBestParse());
  assertTrue(result.isLeaf());
}
@Test
public void testParseWithAllUnknownWords() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("KNOWN");
  tagIndex.add("TAG");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 4;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      return Float.NEGATIVE_INFINITY;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String feat) {
//      return Collections.emptyIterator();
//    }
//  };

//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int distanceBin(int dist) {
//      return 0;
//    }
//
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 1.0f;
//    }
//
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//
//    public int numDistBins() {
//      return 1;
//    }
//
//    public int tagBin(int tag) {
//      return tag;
//    }
//  };
//
//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new Word("UNSEEN1"));
  sentence.add(new Word("UNSEEN2"));

//  boolean parsed = parser.parse(sentence);

//  assertFalse(parsed);
//  assertFalse(parser.hasParse());
//  assertNull(parser.getBestParse());
//  assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.0001);
}
@Test
public void testEdgeParseWithOneUnknownAndOneKnownWord() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("known");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add("NN");
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      return itw.word == 0 ? 1.0f : Float.NEGATIVE_INFINITY;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      if (word == 0) {
//        return Collections.singleton(new IntTaggedWord(word, 0)).iterator();
//      }
//      return Collections.emptyIterator();
//    }
//  };

//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//    public int distanceBin(int dist) {
//      return 0;
//    }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 1.0f;
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 2;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("known", "NN"));
  sentence.add(new TaggedWord("unknown", "NN"));

//  boolean parsed = parser.parse(sentence);
//  Tree best = parser.getBestParse();
//
//  assertTrue(parsed);
//  assertNotNull(best);
}
@Test
public void testParseWithIdenticalTaggedWords() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("scan");
  tagIndex.add("VB");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return 0.2f;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.singleton(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//    public int distanceBin(int d) {
//      return 0;
//    }
//    public float scoreTB(int hw, int htag, int dw, int dtag, boolean l, int dist) {
//      return 1.0f;
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 2;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("scan", "VB"));
  sentence.add(new TaggedWord("scan", "VB"));  

//  boolean parsed = parser.parse(sentence);
//  Tree best = parser.getBestParse();
//
//  assertTrue(parsed);
//  assertNotNull(best);
//  assertTrue(parser.getBestScore() > Float.NEGATIVE_INFINITY);
}
@Test
public void testParseWithNullContextReturnedFromHasContext() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("apple");
  tagIndex.add("NN");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return feat == null ? 0.0f : Float.NEGATIVE_INFINITY;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.singleton(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int distanceBin(int d) {
//      return 0;
//    }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 1.0f;
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 2;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  HasWord token = new TaggedWord("apple", "NN");
  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(token);

//  boolean parsed = parser.parse(sentence);
//  Tree best = parser.getBestParse();
//
//  assertTrue(parsed);
//  assertNotNull(best);
//  assertTrue(parser.getBestScore() > Float.NEGATIVE_INFINITY);
}
@Test
public void testParseWithNoTagFilteringBecauseEmptyTag() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("log");
  tagIndex.add("VB");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return 1.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int distanceBin(int d) {
//      return 0;
//    }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 1.0f;
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 2;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  TaggedWord token = new TaggedWord("log", "");
  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(token);

//  boolean parsed = parser.parse(sentence);
//  assertTrue(parsed);
//  assertNotNull(parser.getBestParse());
}
@Test
public void testNegativeScoreValuesHandledCorrectly() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("bad");
  tagIndex.add("JJ");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      return -5.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int distanceBin(int dist) {
//      return 0;
//    }
//    public float scoreTB(int hw, int ht, int dw, int dt, boolean l, int dist) {
//      return -1.0f;
//    }
//  };

  Options options = new Options();
  options.testOptions.maxLength = 1;
  options.doPCFG = false;

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("bad", "JJ"));

//  boolean parsed = parser.parse(sentence);
//  double score = parser.getBestScore();
//  Tree best = parser.getBestParse();
//
//  assertTrue(parsed);
//  assertTrue(score <= 0.0);
//  assertNotNull(best);
}
@Test
public void testIScoreTotalThrowsExceptionWhenDoiScoreHSumDisabled() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("x");
  tagIndex.add("X");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 2;
  options.testOptions.verbose = false;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return 0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String feat) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int distanceBin(int d) {
//      return 0;
//    }
//    public float scoreTB(int hw, int ht, int dw, int dt, boolean b, int dist) {
//      return 1.0f;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("x", "X"));

//  parser.parse(sentence);

  try {
//    parser.iScoreTotal(0, 1, 0, 0);
    fail("Expected RuntimeException for uninitialized iScoreHSum");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Summed inner scores not computed"));
  }
}
@Test
public void testHasParseReturnsFalseWhenIscoreIsNegativeInfinity() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("z");
  tagIndex.add("NN");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 2;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return Float.NEGATIVE_INFINITY;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String feat) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() {
//      return tagIndex.size();
//    }
//    public int tagBin(int tag) {
//      return tag;
//    }
//    public int numDistBins() {
//      return 1;
//    }
//    public int distanceBin(int dist) {
//      return 0;
//    }
//    public float scoreTB(int a, int b, int c, int d, boolean x, int y) {
//      return 0f;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("z", "NN"));

//  boolean parsed = parser.parse(sentence);
//
//  assertFalse(parsed);
//  assertFalse(parser.hasParse());
//  double result = parser.getBestScore();
//  assertEquals(Float.NEGATIVE_INFINITY, result, 0.0);
//  assertNull(parser.getBestParse());
}
@Test
public void testParseWithMultipleTagsAssignsZeroScoreOnlyOnce() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("jumped");
  tagIndex.add("VB");
  tagIndex.add("VBD");
  tagIndex.add(Lexicon.BOUNDARY_TAG);
  wordIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 1;
  options.testOptions.verbose = false;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      if (itw.tag == 0) {
//        return 2.5f;
//      }
//      if (itw.tag == 1) {
//        return -2.0f;
//      }
//      return Float.NEGATIVE_INFINITY;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Arrays.asList(
//        new IntTaggedWord(word, 0),
//        new IntTaggedWord(word, 1)
//      ).iterator();
//    }
//  };

//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() { return tagIndex.size(); }
//    public int tagBin(int tag) { return tag; }
//    public int numDistBins() { return 1; }
//    public int distanceBin(int dist) { return 0; }
//    public float scoreTB(int hw, int ht, int dw, int dt, boolean l, int dist) {
//      return 1.0f;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("jumped", "")); 

//  boolean parsed = parser.parse(sentence);
//
//  assertTrue(parsed);
//  assertTrue(parser.hasParse());
//  assertTrue(parser.getBestScore() >= 0.0);
}
@Test
public void testGetBestScoreReturnsNegativeInfinityIfSentenceIsNull() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("void");
  tagIndex.add("NN");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();

//  Lexicon lex = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String feat) {
//      return 0.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.emptyIterator();
//    }
//  };

//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() { return tagIndex.size(); }
//    public int tagBin(int tag) { return tag; }
//    public int numDistBins() { return 1; }
//    public int distanceBin(int dist) { return 0; }
//    public float scoreTB(int a, int b, int c, int d, boolean e, int f) {
//      return 0.0f;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lex, options, wordIndex, tagIndex);
//
//  double score = parser.getBestScore();
//
//  assertEquals(Float.NEGATIVE_INFINITY, score, 0.0);
//  assertFalse(parser.hasParse());
//  assertNull(parser.getBestParse());
}
@Test
public void testParseRejectsWhenTrueTagDoesNotMatchLexiconTag() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("walk");
  tagIndex.add("VB");
  tagIndex.add("NN");
  tagIndex.add(Lexicon.BOUNDARY_TAG);
  wordIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 1;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord w, int l, int word, String context) {
//      return 1.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String ctxt) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };

//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() { return tagIndex.size(); }
//    public int numDistBins() { return 1; }
//    public int tagBin(int tag) { return tag; }
//    public int distanceBin(int d) { return 0; }
//    public float scoreTB(int hw, int ht, int dw, int dt, boolean l, int d) {
//      return 0f;
//    }
//  };
//
//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  HasWord word = new TaggedWord("walk", "NN"); 
  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(word);

//  boolean parsed = parser.parse(sentence);
//  assertFalse(parsed);
//  assertFalse(parser.hasParse());
//  assertNull(parser.getBestParse());
}
@Test
public void testParseWithForcedOutOfMemoryWhenArrayReinitialized() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("one");
  tagIndex.add("NN");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 10;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      return 1.0f;
//    }
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() { return tagIndex.size(); }
//    public int tagBin(int tag) { return tag; }
//    public int numDistBins() { return 1; }
//    public int distanceBin(int d) { return 0; }
//    public float scoreTB(int hw, int ht, int dw, int dt, boolean l, int dist) {
//      return 0.0f;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  
  List<HasWord> shortSentence = new ArrayList<HasWord>();
  shortSentence.add(new TaggedWord("one", "NN"));
//  parser.parse(shortSentence);

  List<HasWord> longSentence = new ArrayList<HasWord>();
  for (int i = 0; i < 15; i++) {
    longSentence.add(new TaggedWord("one", "NN"));
  }

  try {
//    parser.parse(longSentence);
    fail("Expected OutOfMemoryError");
  } catch (OutOfMemoryError e) {
    assertTrue(e.getMessage().contains("Refusal to create such large arrays"));
  }
}
@Test
public void testExtractBestParseFallsThroughAndReturnsNull() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("fail");
  tagIndex.add("FAIL");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 2;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      return 0.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featureSpec) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };

//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() { return tagIndex.size(); }
//    public int tagBin(int tag) { return tag; }
//    public int numDistBins() { return 1; }
//    public int distanceBin(int dist) { return 0; }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean left, int dist) {
//      return Float.NEGATIVE_INFINITY;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("fail", "FAIL"));
  sentence.add(new TaggedWord("fail", "FAIL"));

//  boolean parsed = parser.parse(sentence);
//  Tree tree = parser.getBestParse();
//
//  assertTrue(parsed);
//  assertNull("Expected null due to backtrace failure", tree);
}
@Test
public void testParseCreatesAllHeadStopEntriesRegardlessOfDirection() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("left");
  wordIndex.add("right");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add("D");
  tagIndex.add("H");
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 2;
  options.testOptions.verbose = false;
  options.doPCFG = false;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featureSpec) {
//      return 1.0f;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.singletonList(new IntTaggedWord(word, 1)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() { return tagIndex.size(); }
//    public int tagBin(int tag) { return tag; }
//    public int numDistBins() { return 1; }
//    public int distanceBin(int d) { return 0; }
//    public float scoreTB(int headWord, int headTag, int depWord, int depTag, boolean leftHeaded, int dist) {
//      return 0.5f;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);

  List<HasWord> tokens = new ArrayList<HasWord>();
  tokens.add(new TaggedWord("left", "H"));
  tokens.add(new TaggedWord("right", "H"));

//  boolean parsed = parser.parse(tokens);
//
//  assertTrue(parsed);
//  assertTrue(parser.hasParse());
//  assertNotNull(parser.getBestParse());
//  assertTrue(parser.getBestScore() > Float.NEGATIVE_INFINITY);
}
@Test
public void testIAndOPossibleFalseWhenScoresAreAllNegativeInfinity() {
  Index<String> wordIndex = new HashIndex<>();
  Index<String> tagIndex = new HashIndex<>();
  wordIndex.add("zero");
  tagIndex.add("Z");
  wordIndex.add(Lexicon.BOUNDARY_TAG);
  tagIndex.add(Lexicon.BOUNDARY_TAG);

  Options options = new Options();
  options.testOptions.maxLength = 1;
  options.testOptions.verbose = false;
  options.doPCFG = false;

//  Lexicon lexicon = new Lexicon() {
//    public float score(IntTaggedWord itw, int loc, int word, String featSpec) {
//      return Float.NEGATIVE_INFINITY;
//    }
//
//    public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String featSpec) {
//      return Collections.singletonList(new IntTaggedWord(word, 0)).iterator();
//    }
//  };
//
//  DependencyGrammar grammar = new DependencyGrammar() {
//    public int numTagBins() { return tagIndex.size(); }
//    public int tagBin(int tag) { return tag; }
//    public int numDistBins() { return 1; }
//    public int distanceBin(int dist) { return 0; }
//    public float scoreTB(int hw, int ht, int dw, int dt, boolean l, int dist) {
//      return 0.0f;
//    }
//  };

//  ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(grammar, lexicon, options, wordIndex, tagIndex);
  List<HasWord> sentence = new ArrayList<HasWord>();
  sentence.add(new TaggedWord("zero", "Z"));

//  boolean parsed = parser.parse(sentence);

  int tag = 0; 
//  Hook hookLeft = new Hook() {
//    public int start() { return 0; }
//    public int end() { return 1; }
//    public int tag() { return tag; }
//    public int head() { return 0; }
//    public boolean isPreHook() { return false; }
//  };
//  Hook hookRight = new Hook() {
//    public int start() { return 0; }
//    public int end() { return 1; }
//    public int tag() { return tag; }
//    public int head() { return 0; }
//    public boolean isPreHook() { return true; }
//  };
//
//  boolean iLeft = parser.iPossible(hookLeft);
//  boolean iRight = parser.iPossible(hookRight);
//  boolean oLeft = parser.oPossible(hookLeft);
//  boolean oRight = parser.oPossible(hookRight);

//  assertFalse(iLeft || iRight);
//  assertFalse(oLeft || oRight);
} 
}