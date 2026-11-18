package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasTag;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.DiskTreebank;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.LeftHeadFinder;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.ModCollinsHeadFinder;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalStructure;
import edu.stanford.nlp.trees.UniversalSemanticHeadFinder;
import edu.stanford.nlp.util.HashIndex;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EnglishTreebankParserParamsPromptTest {

  @Test
  public void constructorProvidesEnglishDefaults() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    assertTrue(params.treebankLanguagePack() instanceof PennTreebankLanguagePack);
    assertTrue(params.headFinder() instanceof ModCollinsHeadFinder);
    assertTrue(params.typedDependencyHeadFinder() instanceof UniversalSemanticHeadFinder);
    assertTrue(params.supportsBasicDependencies());
    assertFalse(params.generateOriginalDependencies());
  }

  @Test
  public void treebankFactoriesAreFreshAndUseConfiguredReaders() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    DiskTreebank firstDisk = params.diskTreebank();
    DiskTreebank secondDisk = params.diskTreebank();
    MemoryTreebank firstMemory = params.memoryTreebank();
    MemoryTreebank secondMemory = params.testMemoryTreebank();
    TreeReader reader = params.treeReaderFactory().newTreeReader(
        new StringReader("(ROOT (S (NP-SBJ (NNP Ada)) (VP (VBZ works)) (. .)))"));
    Tree tree = reader.readTree();

    assertNotSame(firstDisk, secondDisk);
    assertNotSame(firstMemory, secondMemory);
    assertEquals("ROOT", tree.value());
    assertEquals("Ada", tree.yieldWords().get(0).word());
    assertEquals("works", tree.yieldWords().get(1).word());
    assertEquals(".", tree.yieldWords().get(2).word());
    assertNull(reader.readTree());
    reader.close();
  }

  @Test
  public void testMemoryTreebankCanLoadNormalizedPennTree() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank treebank = params.testMemoryTreebank();
    treebank.load(new StringReader("(ROOT (S (NP-SBJ (PRP She)) (VP (VBZ smiles)) (. .)))"));

    assertEquals(1, treebank.size());
    assertEquals("ROOT", treebank.get(0).value());
    assertEquals("NP", treebank.get(0).getChild(0).getChild(0).value());
  }

  @Test
  public void lexCreatesBaseLexiconWithEmptyIndexes() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Options options = new Options();
    Lexicon lexicon = params.lex(options, new HashIndex<String>(), new HashIndex<String>());

    assertTrue(lexicon instanceof BaseLexicon);
  }

  @Test
  public void defaultSentenceContainsIndependentWordObjectsInExpectedOrder() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> first = params.defaultTestSentence();
    List<Word> second = params.defaultTestSentence();

    assertEquals(Arrays.asList(new Word("This"), new Word("is"), new Word("just"),
        new Word("a"), new Word("test"), new Word(".")), first);
    assertNotSame(first, second);
    assertNotSame(first.get(0), second.get(0));
  }

  @Test
  public void sisterSplittersExposeDefaultLevelWithoutReflection() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<String> splitters = Arrays.asList(params.sisterSplitters());

    assertTrue(splitters.contains("ADJP=l=VBD"));
    assertTrue(splitters.contains("PP=r=NP"));
    assertTrue(splitters.contains("NP=l=DT"));
    assertFalse(splitters.contains("S=r=PP"));
  }

  @Test
  public void optionParserReturnsConsumedIndexAndLeavesUnknownOptionUnconsumed() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    assertEquals(2, params.setOptionFlag(new String[]{"-splitIN", "3"}, 0));
    assertEquals(1, params.setOptionFlag(new String[]{"-splitPercent"}, 0));
    assertEquals(1, params.setOptionFlag(new String[]{"-SPLITQUOTES"}, 0));
    assertEquals(0, params.setOptionFlag(new String[]{"-notARealOption"}, 0));
    assertEquals(0, params.setOptionFlag(new String[]{"-splitCC"}, 0));
  }

  @Test
  public void optionParserReportsMalformedNumericValues() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    assertThrows(NumberFormatException.class,
        () -> params.setOptionFlag(new String[]{"-splitVP", "many"}, 0));
    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> params.setOptionFlag(new String[]{"-splitIN"}, 0));
  }

  @Test
  public void originalDependencyOptionSwitchesHeadFinderAndGrammaticalStructure() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertEquals(1, params.setOptionFlag(new String[]{"-originalDependencies"}, 0));
    Tree tree = Tree.valueOf("(ROOT (S (NP (PRP She)) (VP (VBZ smiles)) (. .)))");
    GrammaticalStructure structure = params.getGrammaticalStructure(
        tree, value -> !value.equals("."), params.typedDependencyHeadFinder());

    assertTrue(params.generateOriginalDependencies());
    assertTrue(params.typedDependencyHeadFinder() instanceof SemanticHeadFinder);
    assertTrue(structure instanceof EnglishGrammaticalStructure);
  }

  @Test
  public void universalDependencyStructureIsDefault() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree tree = Tree.valueOf("(ROOT (S (NP (PRP They)) (VP (VBP agree)) (. .)))");
    GrammaticalStructure structure = params.getGrammaticalStructure(
        tree, value -> true, params.typedDependencyHeadFinder());

    assertTrue(structure instanceof UniversalEnglishGrammaticalStructure);
  }

  @Test
  public void missingDependencyFileIsWrappedAsRuntimeIOException() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    assertThrows(RuntimeIOException.class,
        () -> params.readGrammaticalStructureFromFile("definitely-not-present-english-dependencies.conll"));
  }

  @Test
  public void subcategoryStripperRemovesFunctionalCategoriesAndPreservesShape() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree input = Tree.valueOf("(S-TMP (NP-SBJ (NNP Alice)) (VP-PRD (VBZ runs)))");
    input.setValue("S-TMP");
    input.getChild(0).setValue("NP-SBJ");
    input.getChild(1).setValue("VP-PRD");
    input.setScore(4.25);
    Tree output = params.subcategoryStripper().transformTree(input);

    assertEquals("(S (NP (NNP Alice)) (VP (VBZ runs)))", output.toString());
    assertEquals(4.25, output.score(), 0.0);
    assertNotSame(input, output);
  }

  @Test
  public void subcategoryStripperCanRetainTmpAndAdvCategories() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertEquals(1, params.setOptionFlag(new String[]{"-retainTMPSubcategories"}, 0));
    assertEquals(1, params.setOptionFlag(new String[]{"-retainADVSubcategories"}, 0));
    Tree input = Tree.valueOf("(S-TMP-3 (NP-ADV-4 (NN today)) (VP-TMP (VBZ matters)))");
    input.setValue("S-TMP-3");
    input.getChild(0).setValue("NP-ADV-4");
    input.getChild(1).setValue("VP-TMP");
    Tree output = params.subcategoryStripper().transformTree(input);

    assertEquals("(S-TMP (NP-ADV (NN today)) (VP-TMP (VBZ matters)))", output.toString());
  }

  @Test
  public void subcategoryStripperRetainsNpTmpSpecifically() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-retainNPTMPSubcategories"}, 0);
    Tree input = Tree.valueOf("(S (NP-TMP-7 (NN today)) (ADVP-TMP (RB now)))");
    input.getChild(0).setValue("NP-TMP-7");
    input.getChild(1).setValue("ADVP-TMP");
    Tree output = params.subcategoryStripper().transformTree(input);

    assertEquals("(S (NP-TMP (NN today)) (ADVP (RB now)))", output.toString());
  }

  @Test
  public void subcategoryStripperHandlesLeafAndChildlessNodeEdges() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree leaf = new edu.stanford.nlp.trees.LabeledScoredTreeFactory().newLeaf("token");
    leaf.setScore(2.5);
    Tree copiedLeaf = params.subcategoryStripper().transformTree(leaf);
    CoreLabel emptyLabel = new CoreLabel();
    emptyLabel.setValue("NP-SBJ");
    Tree emptyNode = new edu.stanford.nlp.trees.LabeledScoredTreeFactory()
        .newTreeNode(emptyLabel, Arrays.<Tree>asList());

    assertEquals("token", copiedLeaf.value());
    assertEquals(2.5, copiedLeaf.score(), 0.0);
    assertNotSame(leaf, copiedLeaf);
    Tree copiedEmptyNode = params.subcategoryStripper().transformTree(emptyNode);
    assertEquals("NP-SBJ", copiedEmptyNode.value());
    assertTrue(copiedEmptyNode.isLeaf());
    assertNotSame(emptyNode, copiedEmptyNode);
  }

  @Test
  public void baseNpOptionMakesStripperUndoInsertedNestedNp() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    Tree input = Tree.valueOf("(NP-SBJ (NP-B (DT the) (NN cat)))");
    Tree output = params.subcategoryStripper().transformTree(input);

    assertEquals("(NP (DT the) (NN cat))", output.toString());
  }

  @Test
  public void transformTreeReturnsNullAndLeafInputsUnchanged() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree leaf = Tree.valueOf("word");

    assertNull(params.transformTree(null, null));
    assertSame(leaf, params.transformTree(leaf, leaf));
  }

  @Test
  public void correctTagsChangesKnownPennTreebankErrorInNp() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-correctTags"}, 0);
    Tree root = Tree.valueOf("(NP (IN a) (NN plan))");
    Tree preterminal = root.getChild(0);
    CoreLabel label = (CoreLabel) preterminal.label();
    label.setWord("a");
    label.setTag("IN");

    Tree result = params.transformTree(preterminal, root);

    assertSame(preterminal, result);
    assertEquals("DT", result.value());
    assertEquals("DT", ((HasTag) result.label()).tag());
  }

  @Test
  public void lexicalFlagsComposeOnSeveralIndependentPreterminals() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPercent"}, 0);
    params.setOptionFlag(new String[]{"-splitQuotes"}, 0);
    params.setOptionFlag(new String[]{"-splitSFP"}, 0);
    Tree percentRoot = Tree.valueOf("(NP (NN %))");
    Tree percent = percentRoot.firstChild();
    ((CoreLabel) percent.label()).setWord("%");
    ((CoreLabel) percent.label()).setTag("NN");
    Tree quoteRoot = Tree.valueOf("(NP (`` `))");
    Tree quote = quoteRoot.firstChild();
    ((CoreLabel) quote.label()).setWord("`");
    ((CoreLabel) quote.label()).setTag("``");
    Tree punctuationRoot = Tree.valueOf("(S (. ?))");
    Tree punctuation = punctuationRoot.firstChild();
    ((CoreLabel) punctuation.label()).setWord("?");
    ((CoreLabel) punctuation.label()).setTag(".");

    assertEquals("NN-%", params.transformTree(percent, percentRoot).value());
    assertEquals("``-SG", params.transformTree(quote, quoteRoot).value());
    assertEquals(".-QUES", params.transformTree(punctuation, punctuationRoot).value());
  }

  @Test
  public void unaryReflexiveAndConjunctionFlagsAnnotateTags() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryDT"}, 0);
    params.setOptionFlag(new String[]{"-markReflexivePRP"}, 0);
    params.setOptionFlag(new String[]{"-splitCC", "1"}, 0);
    Tree determinerRoot = Tree.valueOf("(NP (DT this))");
    Tree determiner = determinerRoot.firstChild();
    ((CoreLabel) determiner.label()).setWord("this");
    ((CoreLabel) determiner.label()).setTag("DT");
    Tree pronounRoot = Tree.valueOf("(NP (PRP herself))");
    Tree pronoun = pronounRoot.firstChild();
    ((CoreLabel) pronoun.label()).setWord("herself");
    ((CoreLabel) pronoun.label()).setTag("PRP");
    Tree conjunctionRoot = Tree.valueOf("(NP (NN tea) (CC and) (NN cake))");
    Tree conjunction = conjunctionRoot.getChild(1);
    ((CoreLabel) conjunction.label()).setWord("and");
    ((CoreLabel) conjunction.label()).setTag("CC");

    assertEquals("DT^U", params.transformTree(determiner, determinerRoot).value());
    assertEquals("PRP-SE", params.transformTree(pronoun, pronounRoot).value());
    assertEquals("CC-C", params.transformTree(conjunction, conjunctionRoot).value());
  }

  @Test
  public void splitVpAndDominatesVComposeOnPhrasalNode() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "3"}, 0);
    params.setOptionFlag(new String[]{"-dominatesV", "1"}, 0);
    Tree root = Tree.valueOf("(S (VP (VBD walked)))");
    Tree vp = root.firstChild();
    CoreLabel vpLabel = (CoreLabel) vp.label();
    vpLabel.setWord("walked");
    vpLabel.setTag("VBD");

    assertEquals("VP-VBF-v", params.transformTree(vp, root).value());
  }

  @Test
  public void noAnnotationsCancelsConfiguredVpAndDominanceIndependentSettings() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "3"}, 0);
    params.setOptionFlag(new String[]{"-noAnnotations"}, 0);
    Tree root = Tree.valueOf("(S (VP (VB walk)))");
    Tree vp = root.firstChild();
    CoreLabel vpLabel = (CoreLabel) vp.label();
    vpLabel.setWord("walk");
    vpLabel.setTag("VB");

    assertEquals("VP", params.transformTree(vp, root).value());
  }

  @Test
  public void headFinderOptionLoadsNoArgumentImplementation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int next = params.setOptionFlag(
        new String[]{"-headFinder", "edu.stanford.nlp.trees.LeftHeadFinder"}, 0);

    assertEquals(2, next);
    assertTrue(params.headFinder() instanceof LeftHeadFinder);
  }

  @Test
  public void collinizersAreFreshAndRemovePunctuationForEvaluation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    AbstractCollinizer regular = params.collinizer();
    AbstractCollinizer evalb = params.collinizerEvalb();
    Tree guess = Tree.valueOf("(ROOT (S (NP-SBJ (NNP Ada)) (VP (VBZ runs)) (. .)))");
    Tree gold = Tree.valueOf("(ROOT (S (NP-SBJ (NNP Ada)) (VP (VBZ runs)) (. .)))");
    Tree output = regular.transformTree(guess, gold);

    assertNotSame(regular, evalb);
    assertEquals("(S (NP (NNP Ada)) (VP (VBZ runs)))", output.toString());
  }

  @Test
  public void defaultCoreNlpFlagRequestsTmpRetention() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    assertArrayEquals(new String[]{"-retainTmpSubcategories"}, params.defaultCoreNLPFlags());
  }
}
