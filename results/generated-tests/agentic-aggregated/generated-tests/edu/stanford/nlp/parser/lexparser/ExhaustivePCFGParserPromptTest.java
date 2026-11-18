package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.common.ParserConstraint;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import edu.stanford.nlp.util.ScoredObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ExhaustivePCFGParserPromptTest {

  @Test
  public void unparsedParserReportsNoScoreOrParse() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", "VB", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("dogs", "bark", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", "VB", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("S"), states.indexOf("NN"), states.indexOf("VB"), -0.2));
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN dogs) (VB bark)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    assertFalse(parser.hasParse());
    assertEquals(Double.NEGATIVE_INFINITY, parser.getBestScore(), 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, parser.getBestScore("NOT_A_STATE"), 0.0);
  }

  @Test
  public void parsesBinarySentenceAndReconstructsBestTree() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", "VB", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("dogs", "bark", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", "VB", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("S"), states.indexOf("NN"), states.indexOf("VB"), -0.2));
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN dogs) (VB bark)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    assertTrue(parser.parse(Arrays.asList(new Word("dogs"), new Word("bark"), new Word(Lexicon.BOUNDARY))));
    assertTrue(parser.hasParse());
    assertTrue(Double.isFinite(parser.getBestScore()));
    assertEquals(parser.getBestScore(), parser.getBestScore("ROOT"), 0.0);
    assertEquals("(ROOT (S (NN dogs) (VB bark)) (.$$. .$.))", parser.getBestParse().toString());
    parser.nudgeDownArraySize();
    assertTrue(parser.parse(Arrays.asList(new Word("dogs"), new Word("bark"), new Word(Lexicon.BOUNDARY))));
    assertEquals("(ROOT (S (NN dogs) (VB bark)) (.$$. .$.))", parser.getBestParse().toString());
  }

  @Test
  public void unaryAndBinaryScoresAgreeWithReconstructedTree() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "N", "NN", "VB", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("dogs", "bark", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", "VB", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("S"), states.indexOf("N"), states.indexOf("VB"), -0.4));
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.3));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.addRule(new UnaryRule(states.indexOf("N"), states.indexOf("NN"), -0.2));
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN dogs) (VB bark)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    assertTrue(parser.parse(Arrays.asList(new Word("dogs"), new Word("bark"), new Word(Lexicon.BOUNDARY))));
    Tree best = parser.getBestParse();
    assertEquals("(ROOT (S (N (NN dogs)) (VB bark)) (.$$. .$.))", best.toString());
    assertEquals(parser.getBestScore(), best.score(), 0.00001);
    double rescored = parser.scoreBinarizedTree(best, 0);
    assertTrue(Double.isFinite(rescored));
    assertEquals(rescored, parser.validateBinarizedTree(best, 0), 0.00001);
  }

  @Test
  public void coreLabelOffsetsAndTagSurviveParseReconstruction() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("token", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.addRule(new UnaryRule(states.indexOf("S"), states.indexOf("NN"), -0.2));
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN token)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);
    CoreLabel token = new CoreLabel();
    token.setWord("token");
    token.setTag("NN");
    token.setBeginPosition(7);
    token.setEndPosition(12);
    CoreLabel boundary = new CoreLabel();
    boundary.setWord(Lexicon.BOUNDARY);

    assertTrue(parser.parse(Arrays.asList(token, boundary)));
    CoreLabel leaf = (CoreLabel) parser.getBestParse().getLeaves().get(0).label();
    assertNotSame(token, leaf);
    assertEquals("token", leaf.value());
    assertEquals(7, leaf.beginPosition());
    assertEquals(12, leaf.endPosition());
    assertEquals("NN", leaf.tag());
  }

  @Test
  public void taggedWordCanForceAValidTagAndIsRestored() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", "VB", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("lead", "run", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", "VB", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.addRule(new UnaryRule(states.indexOf("S"), states.indexOf("NN"), -0.2));
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(2.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN lead)) (.$$. .$.))"), 1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (VB run)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    assertTrue(parser.parse(Arrays.asList(new TaggedWord("lead", "NN"), new Word(Lexicon.BOUNDARY))));
    assertEquals("(ROOT (S (NN lead)) (.$$. .$.))", parser.getBestParse().toString());
  }

  @Test
  public void incompatibleForcedTagMakesParseFailWithoutRecovery() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", "VB", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("lead", "run", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", "VB", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.addRule(new UnaryRule(states.indexOf("S"), states.indexOf("NN"), -0.2));
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(2.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN lead)) (.$$. .$.))"), 1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (VB run)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    assertFalse(parser.parse(Arrays.asList(new TaggedWord("lead", "VB"), new Word(Lexicon.BOUNDARY))));
    assertFalse(parser.hasParse());
    assertEquals(Double.NEGATIVE_INFINITY, parser.getBestScore(), 0.0);
    assertNull(parser.getBestParse());
    assertTrue(parser.getBestParses().isEmpty());
  }

  @Test
  public void matchingAndRejectingConstraintsAffectAReusedParser() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", "VB", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("dogs", "bark", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", "VB", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("S"), states.indexOf("NN"), states.indexOf("VB"), -0.2));
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN dogs) (VB bark)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);
    List<Word> sentence = Arrays.asList(new Word("dogs"), new Word("bark"), new Word(Lexicon.BOUNDARY));

    parser.setConstraints(Collections.singletonList(new ParserConstraint(0, 2, Pattern.compile("S"))));
    assertTrue(parser.parse(sentence));
    parser.setConstraints(Collections.singletonList(new ParserConstraint(0, 2, Pattern.compile("VP"))));
    assertFalse(parser.parse(sentence));
    parser.setConstraints(null);
    assertTrue(parser.parse(sentence));
  }

  @Test
  public void kBestGoodAndBestCollectionsAreConsistent() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", "VB", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("dogs", "bark", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", "VB", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("S"), states.indexOf("NN"), states.indexOf("VB"), -0.2));
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN dogs) (VB bark)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);
    assertTrue(parser.parse(Arrays.asList(new Word("dogs"), new Word("bark"), new Word(Lexicon.BOUNDARY))));

    List<ScoredObject<Tree>> best = parser.getBestParses();
    List<ScoredObject<Tree>> kBest = parser.getKBestParses(3);
    List<ScoredObject<Tree>> good = parser.getKGoodParses(3);
    List<ScoredObject<Tree>> none = parser.getKBestParses(0);
    assertEquals(1, best.size());
    assertEquals(1, kBest.size());
    assertEquals(1, good.size());
    assertTrue(none.isEmpty());
    assertEquals(parser.getBestScore(), best.get(0).score(), 0.00001);
    assertEquals(parser.getBestScore(), kBest.get(0).score(), 0.00001);
    assertEquals(parser.getBestParse().toString(), good.get(0).object().toString());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void sampledParsesAreExplicitlyUnsupported() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "NN"));
    Index<String> words = new HashIndex<>(Collections.singletonList("word"));
    Index<String> tags = new HashIndex<>(Collections.singletonList("NN"));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    parser.getKSampledParses(1);
  }

  @Test
  public void htkLatticeEntryPointReturnsFalseEvenForNull() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "NN"));
    Index<String> words = new HashIndex<>(Collections.singletonList("word"));
    Index<String> tags = new HashIndex<>(Collections.singletonList("NN"));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    assertFalse(parser.parse((HTKLatticeReader) null));
  }

  @Test(expected = OutOfMemoryError.class)
  public void configuredMaximumLengthIsEnforcedBeforeParsing() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "NN"));
    Index<String> words = new HashIndex<>(Arrays.asList("one", "two", "three"));
    Index<String> tags = new HashIndex<>(Collections.singletonList("NN"));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "1", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    parser.parse(Arrays.asList(new Word("one"), new Word("two"), new Word("three")));
  }

  @Test
  public void interruptionIsObservedAndClearedByTest() {
    Index<String> states = new HashIndex<>(Arrays.asList("ROOT", "S", "NN", Lexicon.BOUNDARY_TAG));
    Index<String> words = new HashIndex<>(Arrays.asList("token", Lexicon.BOUNDARY));
    Index<String> tags = new HashIndex<>(Arrays.asList("NN", Lexicon.BOUNDARY_TAG));
    Options options = new Options();
    options.setOptions("-PCFG", "-maxLength", "20", "-noDoRecovery");
    BinaryGrammar binary = new BinaryGrammar(states);
    binary.addRule(new BinaryRule(states.indexOf("ROOT"), states.indexOf("S"), states.indexOf(Lexicon.BOUNDARY_TAG), -0.1));
    binary.splitRules();
    UnaryGrammar unary = new UnaryGrammar(states);
    unary.addRule(new UnaryRule(states.indexOf("S"), states.indexOf("NN"), -0.2));
    unary.purgeRules();
    BaseLexicon lexicon = new BaseLexicon(options, words, tags);
    lexicon.initializeTraining(1.0);
    lexicon.train(Tree.valueOf("(ROOT (S (NN token)) (.$$. .$.))"), 1.0);
    lexicon.finishTraining();
    ExhaustivePCFGParser parser = new ExhaustivePCFGParser(binary, unary, lexicon, options, states, words, tags);

    Thread.currentThread().interrupt();
    try {
      parser.parse(Arrays.asList(new Word("token"), new Word(Lexicon.BOUNDARY)));
      fail("Expected parser to honor the thread interrupt flag");
    } catch (RuntimeInterruptedException expected) {
      assertFalse(Thread.currentThread().isInterrupted());
    } finally {
      Thread.interrupted();
    }
  }
}
