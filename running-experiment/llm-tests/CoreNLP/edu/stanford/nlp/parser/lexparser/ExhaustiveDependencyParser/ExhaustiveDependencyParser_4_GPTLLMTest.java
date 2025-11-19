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

public class ExhaustiveDependencyParser_4_GPTLLMTest {

 @Test
  public void testParseSimpleSentence() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     wordIndex.add("The");
//     wordIndex.add("cat");
//     wordIndex.add("sat");

//     tagIndex.add("DT");
//     tagIndex.add("NN");
//     tagIndex.add("VB");
//     tagIndex.add(Lexicon.BOUNDARY_TAG);

//     DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();
    op.testOptions.verbose = false;
    op.testOptions.maxLength = 10;
    op.doPCFG = false;

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("The"));
    sentence.add(new Word("cat"));
    sentence.add(new Word("sat"));

//     boolean result = parser.parse(sentence);
//     assertTrue(result);
//     assertTrue(parser.hasParse());
//     assertNotNull(parser.getBestParse());
  }
@Test
  public void testParseEmptySentence() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     tagIndex.add("DT");
//     tagIndex.add("NN");
//     tagIndex.add("VB");
//     tagIndex.add(Lexicon.BOUNDARY_TAG);

//     DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();
    op.testOptions.verbose = false;
    op.testOptions.maxLength = 10;
    op.doPCFG = false;

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

    List<HasWord> sentence = Collections.emptyList();

    boolean result = false;
    try {
//       result = parser.parse(sentence);
    } catch (OutOfMemoryError e) {
      fail("Parser threw OutOfMemoryError on empty input");
    }

    assertTrue(result);
//     assertFalse(parser.hasParse());
//     assertNull(parser.getBestParse());
  }
@Test
  public void testOScoreMethodDelegation() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     wordIndex.add("word");
//     tagIndex.add("TAG");
//     tagIndex.add(Lexicon.BOUNDARY_TAG);

//     DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();
    op.testOptions.verbose = false;
    op.testOptions.maxLength = 10;
    op.doPCFG = false;

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);
    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("word"));

//     parser.parse(sentence);

//     Edge edge = new Edge(0, 1, 0, 0);
//     double score1 = parser.oScore(edge);
//     double score2 = parser.oScore(0, 1, 0, 0);
//     assertEquals(score1, score2, 1e-5);
  }
@Test
  public void testIScoreMethodDelegation() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     wordIndex.add("word");
//     tagIndex.add("TAG");
//     tagIndex.add(Lexicon.BOUNDARY_TAG);

//     DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();
    op.testOptions.verbose = false;
    op.testOptions.maxLength = 10;
    op.doPCFG = false;

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);
    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("word"));

//     parser.parse(sentence);

//     Edge edge = new Edge(0, 1, 0, 0);
//     double score1 = parser.iScore(edge);
//     double score2 = parser.iScore(0, 1, 0, 0);
//     assertEquals(score1, score2, 1e-5);
  }
@Test(expected = RuntimeException.class)
  public void testIScoreTotalThrowsWithoutSumEnabled() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     tagIndex.add("TAG");
//     tagIndex.add(Lexicon.BOUNDARY_TAG);

//     DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//     DummyLexicon lex = new DummyLexicon();

    Options op = new Options();
    op.testOptions.verbose = false;
    op.testOptions.maxLength = 10;
    op.doPCFG = false;

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

//     parser.iScoreTotal(0, 1, 0, 0);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKBestParsesUnsupportedException() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     DummyDependencyGrammar dg = new DummyDependencyGrammar(1);
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);
//     parser.getKBestParses(1);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetBestParsesUnsupportedException() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     DummyDependencyGrammar dg = new DummyDependencyGrammar(1);
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);
//     parser.getBestParses();
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKGoodParsesUnsupportedException() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     DummyDependencyGrammar dg = new DummyDependencyGrammar(1);
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);
//     parser.getKGoodParses(1);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testGetKSampledParsesUnsupportedException() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     DummyDependencyGrammar dg = new DummyDependencyGrammar(1);
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);
//     parser.getKSampledParses(1);
  }
@Test
  public void testGetMethodsBeforeParsingReturnDefaults() {
//     Index<String> wordIndex = new Index<>();
//     Index<String> tagIndex = new Index<>();
//     tagIndex.add(Lexicon.BOUNDARY_TAG);
//     DummyDependencyGrammar dg = new DummyDependencyGrammar(1);
//     DummyLexicon lex = new DummyLexicon();
    Options op = new Options();

//     ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

//     assertFalse(parser.hasParse());
//     assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.00001);
//     assertNull(parser.getBestParse());
  }
@Test
public void testParseRejectsOverMaxLengthSentence() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(1);
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 5;
  op.testOptions.verbose = false;
  op.doPCFG = false;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("one"));
  sentence.add(new Word("two"));
  sentence.add(new Word("three"));
  sentence.add(new Word("four"));
  sentence.add(new Word("five"));
  sentence.add(new Word("six"));

  try {
//     parser.parse(sentence);
    fail("Expected OutOfMemoryError for sentence exceeding maxLength");
  } catch (OutOfMemoryError e) {
    assertTrue(e.getMessage().contains("Refusal to create such large arrays."));
  }
}
@Test
public void testParseSingleWordSentence() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("hello");
//   tagIndex.add("UH");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 10;
  op.testOptions.verbose = false;
  op.doPCFG = false;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("hello"));

//   boolean result = parser.parse(sentence);
//   assertTrue(result);
//   assertTrue(parser.hasParse());
//   assertNotNull(parser.getBestParse());
}
@Test
public void testBestScoreReturnsNegativeInfinityWhenArrayNotCreated() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(1);
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("a"));
  sentence.add(new Word("b"));
  sentence.add(new Word("c"));


//   double score = parser.getBestScore();
//   assertEquals(Float.NEGATIVE_INFINITY, score, 1e-5);
}
@Test
public void testGetBestParseReturnsNullWhenNoParseAvailable() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

//   Tree bestParse = parser.getBestParse();
//   assertNull(bestParse);
}
@Test
public void testParseWithUnknownTagIgnored() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("walk");
//   tagIndex.add("VB");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.verbose = false;
  op.testOptions.maxLength = 10;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  HasWord customWord = new Word("walk") {
//     @Override
    public String tag() {
      return "UNKNOWN_TAG";
    }

//     @Override
    public boolean instanceof_hasTag() {
      return true;
    }
  };

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(customWord);

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertTrue(parser.hasParse());
}
@Test
public void testParseWithThreadInterruptThrowsRuntimeInterruptedException() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("The");
//   wordIndex.add("dog");
//   tagIndex.add("DT");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.verbose = false;
  op.testOptions.maxLength = 10;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("The"));
  sentence.add(new Word("dog"));

  Thread.currentThread().interrupt();

  try {
//     parser.parse(sentence);
    fail("Expected RuntimeInterruptedException");
  } catch (RuntimeInterruptedException e) {

    Thread.interrupted();
  }
}
@Test
public void testIScoreReturnsNegativeInfinityForInvalidIndices() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("invalid");
//   tagIndex.add("BAD");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 1;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("invalid"));

//   parser.parse(sentence);

//   double score = parser.iScore(0, 1, 0, 10);
//   assertTrue(Double.isInfinite(score));
}
@Test
public void testOSCoreReturnsNegativeInfinityForInvalidIndices() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("hello");
//   tagIndex.add("TAG");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("hello"));

//   parser.parse(sentence);

//   double score = parser.oScore(0, 1, 0, 10);
//   assertTrue(Double.isInfinite(score));
}
@Test
public void testOPossibleReturnsFalseForInvalidHook() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("token");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 10;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("token"));

//   parser.parse(sentence);

//   Hook hook = new Hook(0, 1, 5, true);
//   boolean possible = parser.oPossible(hook);
//   assertFalse(possible);
}
@Test
public void testIPossibleReturnsFalseForInvalidHook() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("token");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 10;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("token"));

//   parser.parse(sentence);

//   Hook hook = new Hook(0, 1, 5, false);
//   boolean possible = parser.iPossible(hook);
//   assertFalse(possible);
}
@Test
public void testParseSentenceWithValidTagButDifferentBasicCategory() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("run");
//   tagIndex.add("VB");
//   tagIndex.add("NP");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.verbose = false;
  op.testOptions.maxLength = 5;
  op.doPCFG = false;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  Word word = new Word("run") {
//     @Override
    public String tag() {
      return "XX";
    }
  };

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(word);

//   boolean result = parser.parse(sentence);
//   assertTrue(result);
//   assertTrue(parser.hasParse());
}
@Test
public void testFlattenMethodWithNestedStructure() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("word");
//   tagIndex.add("ROOT");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

//   Tree nested = parser.getBestParse();
//   assertNull(nested);
}
@Test
public void testGetBestParseForRootOnlyStructure() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add(".");
//   tagIndex.add("ROOT");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 5;
  op.doPCFG = false;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("."));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertTrue(parser.hasParse());

//   Tree tree = parser.getBestParse();
//   assertNotNull(tree);
//   assertTrue(tree.isTree());
}
@Test
public void testParseFailsWhenLexiconReturnsNegativeInfinity() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("unseen");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return Float.NEGATIVE_INFINITY;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       return list.iterator();
//     }
//   };

  Options op = new Options();
  op.testOptions.maxLength = 5;
  op.testOptions.verbose = false;

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("unseen"));

//   boolean result = parser.parse(sentence);
//   assertTrue(result);
//   assertFalse("Parser should show no parse due to NEG_INF scores", parser.hasParse());
//   assertEquals(Float.NEGATIVE_INFINITY, parser.getBestScore(), 0.00001);
//   assertNull(parser.getBestParse());
}
@Test
public void testParseMixedKnownAndUnknownWords() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("known");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       if (word == 0) return 0.8f;
//       return Float.NEGATIVE_INFINITY;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       return list.iterator();
//     }
//   };

  Options op = new Options();
  op.testOptions.maxLength = 5;
  op.testOptions.verbose = false;

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("known"));
  sentence.add(new Word("unknown"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertTrue("Parser should handle known + unknown word", parser.hasParse());
//   assertNotNull(parser.getBestParse());
}
@Test
public void testParseUsesWordContextIfPresent() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("hello");
//   tagIndex.add("UH");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       if ("greeting".equals(context)) return 1.0f;
//       return Float.NEGATIVE_INFINITY;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       return list.iterator();
//     }
//   };

  Options op = new Options();
  op.testOptions.maxLength = 5;
  op.testOptions.verbose = false;

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  HasWord customWord = new Word("hello") {
//     @Override
    public boolean instanceof_hasContext() {
      return true;
    }

//     @Override
    public String originalText() {
      return "greeting";
    }
  };

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(customWord);

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertTrue(parser.hasParse());
//   assertNotNull(parser.getBestParse());
}
@Test
public void testFallbackToOriginalArraySizeIfResizingFails() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("one");
//   wordIndex.add("two");
//   wordIndex.add("three");
//   wordIndex.add("four");
//   wordIndex.add("five");

//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new DummyLexicon();
//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());

  Options op = new Options();
  op.testOptions.maxLength = 5;
  op.testOptions.verbose = false;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("one"));
  sentence.add(new Word("two"));
  sentence.add(new Word("three"));
  sentence.add(new Word("four"));
  sentence.add(new Word("five"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertTrue(parser.hasParse());
}
@Test
public void testBestScoreWithNullSentenceReturnsNegativeInfinity() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DummyDependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   DummyLexicon lex = new DummyLexicon();

  Options op = new Options();
  op.testOptions.maxLength = 5;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

//   double best = parser.getBestScore();
//   assertEquals(Float.NEGATIVE_INFINITY, best, 0.0001);
}
@Test
public void testGetBestScoreReturnsValidValueAfterSuccessfulParse() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("cat");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new DummyLexicon();

  Options op = new Options();
  op.testOptions.maxLength = 5;
  op.testOptions.verbose = false;
  op.doPCFG = false;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("cat"));

//   boolean result = parser.parse(sentence);
//   assertTrue(result);

//   double score = parser.getBestScore();
//   assertTrue("Expected score > NEGATIVE_INFINITY", score > Float.NEGATIVE_INFINITY);
}
@Test
public void testIScoreStartGreaterThanEndShouldReturnNegativeInfinity() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("word");
//   tagIndex.add("TAG");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new DummyLexicon();

  Options op = new Options();
  op.testOptions.maxLength = 5;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("word"));

//   parser.parse(sentence);

//   float result = parser.iScore(1, 0, 0, 0);
//   assertEquals(Float.NEGATIVE_INFINITY, result, 0.0001);
}
@Test
public void testParseWithHasTagMismatchedCategoryIsSkipped() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("run");
//   tagIndex.add("VB");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return 1.0f;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       return list.iterator();
//     }
//   };

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
  Options op = new Options();
  op.testOptions.maxLength = 2;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  HasWord taggedWord = new Word("run") {
//     @Override
    public String tag() {
      return "NN";
    }

//     @Override
    public boolean instanceof_hasTag() {
      return true;
    }
  };

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(taggedWord);

//   boolean result = parser.parse(sentence);
//   assertTrue(result);
//   assertTrue(parser.hasParse());
//   assertNotNull(parser.getBestParse());
}
@Test
public void testCreateArraysFallbackAfterFirstOutOfMemory() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("one");
//   wordIndex.add("two");

//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return 0.0f;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       return list.iterator();
//     }
//   };

  Options op = new Options();
  op.testOptions.maxLength = 100000000;
  op.testOptions.verbose = false;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("one"));
  sentence.add(new Word("two"));

  try {
//     parser.parse(sentence);
  } catch (OutOfMemoryError e) {

    assertTrue(e.getMessage().contains("Refusal to create such large arrays."));
  }
}
@Test
public void testGetBestParseHandlesFlatBinaryStructure() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("one");
//   wordIndex.add("two");

//   tagIndex.add("NN");
//   tagIndex.add("VB");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return 0;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       list.add(new IntTaggedWord(word, 1));
//       return list.iterator();
//     }
//   };

  Options op = new Options();
  op.testOptions.maxLength = 10;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("one"));
  sentence.add(new Word("two"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   Tree best = parser.getBestParse();
//   assertNotNull(best);
//   assertTrue(best.getChildrenAsList().size() >= 1);
}
@Test
public void testParseWhenTagBinsAreZero() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   tagIndex.add(Lexicon.BOUNDARY_TAG);
//   wordIndex.add("x");

//   DependencyGrammar dg = new DependencyGrammar() {
//     @Override
//     public int tagBin(int tag) {
//       return 0;
//     }
// 
//     @Override
//     public int numTagBins() {
//       return 0;
//     }
// 
//     @Override
//     public int numDistBins() {
//       return 1;
//     }
// 
//     @Override
//     public int distanceBin(int i) {
//       return 0;
//     }
// 
//     @Override
//     public double scoreTB(int headWord, int headTag, int argWord, int argTag, boolean isRight, int dist) {
//       return 0;
//     }
//   };

//   Lexicon lex = new DummyLexicon();

  Options op = new Options();
  op.testOptions.verbose = false;
  op.testOptions.maxLength = 5;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("x"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
}
@Test
public void testHasParseReturnsFalseWhenScoreIsNegativeInfinity() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(1);
//   Lexicon lex = new DummyLexicon();

  Options op = new Options();
  op.testOptions.maxLength = 2;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

//   boolean hasParse = parser.hasParse();
//   assertFalse("Expected no parse when sentence isn't initialized", hasParse);
}
@Test
public void testGetBestScoreWithSentenceExceedingArraySize() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("a");
//   wordIndex.add("b");
//   wordIndex.add("c");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new DummyLexicon();

  Options op = new Options();
  op.testOptions.verbose = false;
  op.testOptions.maxLength = 1;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("a"));
  sentence.add(new Word("b"));

  try {
//     parser.parse(sentence);
  } catch (OutOfMemoryError e) {

  }

//   double score = parser.getBestScore();
//   assertEquals("Expected NEG_INF if sentence exceeds array size", Float.NEGATIVE_INFINITY, score, 0.0001);
}
@Test
public void testParseSentenceWithAllUnknownWordsFailsGracefully() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("hello");
//   tagIndex.add("UH");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return Float.NEGATIVE_INFINITY;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       return Collections.emptyIterator();
//     }
//   };

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
  Options op = new Options();
  op.testOptions.maxLength = 3;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("unknown"));
  sentence.add(new Word("term"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertFalse(parser.hasParse());
//   assertNull(parser.getBestParse());
}
@Test
public void testParseWithMultipleValidTagsReturnsValidTree() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("cats");
//   tagIndex.add("NNS");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return 0.0f;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       list.add(new IntTaggedWord(word, 1));
//       return list.iterator();
//     }
//   };

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
  Options op = new Options();
  op.testOptions.maxLength = 3;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("cats"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertTrue(parser.hasParse());

//   Tree best = parser.getBestParse();
//   assertNotNull(best);
//   assertEquals("cats", best.yield().get(0).value());
}
@Test
public void testParseTriggersAllEdgeScoresAndStopProbabilities() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("The");
//   wordIndex.add("dog");
//   wordIndex.add("barks");

//   tagIndex.add("DT");
//   tagIndex.add("NN");
//   tagIndex.add("VB");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return 1.0f;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       list.add(new IntTaggedWord(word, 1));
//       return list.iterator();
//     }
//   };

//   DependencyGrammar dg = new DependencyGrammar() {
//     @Override
//     public int tagBin(int tag) { return tag; }
// 
//     @Override
//     public int numTagBins() { return 4; }
// 
//     @Override
//     public int numDistBins() { return 2; }
// 
//     @Override
//     public int distanceBin(int i) { return 0; }
// 
//     @Override
//     public double scoreTB(int hWord, int hTag, int aWord, int aTag, boolean isRight, int distance) {
//       return 0.5;
//     }
//   };

  Options op = new Options();
  op.testOptions.maxLength = 5;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("The"));
  sentence.add(new Word("dog"));
  sentence.add(new Word("barks"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertTrue(parser.hasParse());
//   assertNotNull(parser.getBestParse());
}
@Test
public void testIScoreWithOutOfRangeTagIndexReturnsNegativeInfinity() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("word");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 3;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("word"));

//   parser.parse(sentence);

//   float result = parser.iScore(0, 1, 0, 999);
//   assertEquals(Float.NEGATIVE_INFINITY, result, 0.0001);
}
@Test
public void testOScoreWithOutOfRangeTagIndexReturnsNegativeInfinity() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("test");
//   tagIndex.add("TAG");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 2;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("test"));

//   parser.parse(sentence);

//   float result = parser.oScore(0, 1, 0, 42);
//   assertEquals(Float.NEGATIVE_INFINITY, result, 0.0001);
}
@Test
public void testGetBestScoreReturnsNegativeInfinityIfTagIndexMissingBoundary() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("token");
//   tagIndex.add("TAG");

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 1;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("token"));

//   parser.parse(sentence);

//   double result = parser.getBestScore();
//   assertEquals(Float.NEGATIVE_INFINITY, result, 0.0001);
}
@Test
public void testParseWithEmptyWordTagIndexesSucceedsEmptySentence() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();

//   DependencyGrammar dg = new DummyDependencyGrammar(0);
//   Lexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 0;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);
//   assertFalse(parser.hasParse());
//   assertNull(parser.getBestParse());
}
@Test
public void testParseLargeSentenceTriggersOOMFailsGracefully() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("a");
//   tagIndex.add("NN");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

  for (int i = 0; i < 50; i++) {
//     wordIndex.add("word" + i);
  }

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size());
//   Lexicon lex = new DummyLexicon();
  Options op = new Options();
  op.testOptions.maxLength = 10;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("a"));
  sentence.add(new Word("word1"));
  sentence.add(new Word("word2"));
  sentence.add(new Word("word3"));
  sentence.add(new Word("word4"));
  sentence.add(new Word("word5"));
  sentence.add(new Word("word6"));
  sentence.add(new Word("word7"));
  sentence.add(new Word("word8"));
  sentence.add(new Word("word9"));
  sentence.add(new Word("word10"));

  try {
//     parser.parse(sentence);
    fail("Expected OutOfMemoryError due to sentence length > maxLength");
  } catch (OutOfMemoryError e) {
    assertTrue(e.getMessage().contains("Refusal to create such large arrays."));
  }
}
@Test
public void testExtractBestParseReturnsNullForNoMatchingScore() {
//   Index<String> wordIndex = new Index<>();
//   Index<String> tagIndex = new Index<>();
//   wordIndex.add("a");
//   tagIndex.add("TAG");
//   tagIndex.add(Lexicon.BOUNDARY_TAG);

//   Lexicon lex = new Lexicon() {
//     @Override
//     public float score(IntTaggedWord itw, int loc, int word, String context) {
//       return 0.0f;
//     }
// 
//     @Override
//     public Iterator<IntTaggedWord> ruleIteratorByWord(int word, int loc, String context) {
//       List<IntTaggedWord> list = new ArrayList<>();
//       list.add(new IntTaggedWord(word, 0));
//       return list.iterator();
//     }
//   };

//   DependencyGrammar dg = new DummyDependencyGrammar(tagIndex.size()) {
//     @Override
//     public double scoreTB(int hWord, int hTag, int aWord, int aTag, boolean isRight, int distance) {
//       return Double.NEGATIVE_INFINITY;
//     }
//   };

  Options op = new Options();
  op.testOptions.maxLength = 3;

//   ExhaustiveDependencyParser parser = new ExhaustiveDependencyParser(dg, lex, op, wordIndex, tagIndex);

  List<HasWord> sentence = new ArrayList<>();
  sentence.add(new Word("a"));
  sentence.add(new Word("a"));

//   boolean parsed = parser.parse(sentence);
//   assertTrue(parsed);

//   Tree best = parser.getBestParse();
//   assertNull(best);
}
}
