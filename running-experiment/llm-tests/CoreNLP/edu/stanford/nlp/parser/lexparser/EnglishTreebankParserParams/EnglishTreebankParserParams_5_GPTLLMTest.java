package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Index;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EnglishTreebankParserParams_5_GPTLLMTest {

 @Test
  public void testTreebankLanguagePackIsPenn() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreebankLanguagePack tlp = params.treebankLanguagePack();
    assertNotNull(tlp);
    assertEquals(PennTreebankLanguagePack.class, tlp.getClass());
  }
@Test
  public void testMemoryTreebankIsNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank memoryTreebank = params.memoryTreebank();
    assertNotNull(memoryTreebank);
  }
@Test
  public void testDiskTreebankIsNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    DiskTreebank diskTreebank = params.diskTreebank();
    assertNotNull(diskTreebank);
  }
@Test
  public void testTestMemoryTreebankIsNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank testBank = params.testMemoryTreebank();
    assertNotNull(testBank);
  }
@Test
  public void testTreeReaderFactoryReadsTree() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReaderFactory factory = params.treeReaderFactory();
    Reader reader = new StringReader("(ROOT (S (NP (DT This)) (VP (VBZ is) (NP (DT a) (NN test))) (. .)))");
    TreeReader treeReader = factory.newTreeReader(reader);
    Tree tree = treeReader.readTree();
    assertNotNull(tree);
    assertEquals("ROOT", tree.label().value());
    treeReader.close();
  }
@Test
  public void testDefaultTestSentenceIsCorrect() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> sentence = params.defaultTestSentence();
    assertEquals(6, sentence.size());
    assertEquals("This", sentence.get(0).word());
    assertEquals("is", sentence.get(1).word());
    assertEquals("just", sentence.get(2).word());
    assertEquals("a", sentence.get(3).word());
    assertEquals("test", sentence.get(4).word());
    assertEquals(".", sentence.get(5).word());
  }
@Test
  public void testSubcategoryStripperStripsTMPWhenNotRetained() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree tmpTree = tf.newTreeNode("NP-TMP", new ArrayList<Tree>());
    Tree strippedTree = params.subcategoryStripper().transformTree(tmpTree);
    assertNotNull(strippedTree);
    assertEquals("NP", strippedTree.label().value());
  }
@Test
  public void testSubcategoryStripperRetainsTMPWhenFlagOn() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-retainTMPSubcategories" };
    params.setOptionFlag(args, 0);
    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree tmpTree = tf.newTreeNode("NP-TMP", new ArrayList<Tree>());
    Tree strippedTree = params.subcategoryStripper().transformTree(tmpTree);
    assertNotNull(strippedTree);
    assertEquals("NP-TMP", strippedTree.label().value());
  }
//@Test
//  public void testLexiconIsBaseLexicon() {
//    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
//    Options options = new Options();
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    Lexicon lex = params.lex(options, wordIndex, tagIndex);
//    assertNotNull(lex);
//    assertTrue(lex instanceof BaseLexicon);
//    assertEquals("edu.stanford.nlp.parser.lexparser.EnglishUnknownWordModelTrainer", options.lexOptions.uwModelTrainer);
//  }
@Test
  public void testTransformTreeReturnsNullOnNullInput() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree transformed = params.transformTree(null, null);
    assertNull(transformed);
  }
@Test
  public void testTransformTreeReturnsLeafUnchanged() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("hello");
    Tree transformed = params.transformTree(leaf, null);
    assertEquals("hello", transformed.label().value());
  }
@Test
  public void testCorrectTagChangesJjToNnpForKnownMistake() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-correctTags" };
    params.setOptionFlag(args, 0);
    CoreLabel label = new CoreLabel();
    label.setValue("JJ");
    label.setTag("JJ");
    label.setWord("U.S.");
    List<Tree> children = new ArrayList<>();
    children.add(new LabeledScoredTreeFactory().newLeaf("U.S."));
    Tree parent = new LabeledScoredTreeFactory().newTreeNode(label, children);
    Tree result = params.transformTree(parent, parent);
    String category = result.label().value();
    assertTrue(category.startsWith("NNP"));
  }
@Test
  public void testSisterSplittersDefaultLevel1() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] splits = params.sisterSplitters();
    assertNotNull(splits);
    assertTrue(splits.length > 0);
    assertEquals("ADJP=l=VBD", splits[0]);
  }
@Test
  public void testHeadFinderIsNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder hf = params.headFinder();
    assertNotNull(hf);
  }
@Test
  public void testTypedDependencyHeadFinderIsNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder hf = params.typedDependencyHeadFinder();
    assertNotNull(hf);
  }
@Test
  public void testSupportsBasicDependenciesReturnsTrue() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    boolean result = params.supportsBasicDependencies();
    assertTrue(result);
  }
@Test
  public void testDefaultCoreNLPFlagsContainsRetainTmp() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flags = params.defaultCoreNLPFlags();
    assertEquals(1, flags.length);
    assertEquals("-retainTmpSubcategories", flags[0]);
  }
@Test
  public void testTransformTreeChangesToCorrectedPOS() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flag = new String[] { "-correctTags" };
    params.setOptionFlag(flag, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("JJ");
    label.setTag("JJ");
    label.setWord("ours");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("ours");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree result = params.transformTree(preterminal, preterminal);
    String newCat = result.label().value();
    assertTrue("Expected correction to PRP tag", newCat.startsWith("PRP"));
  }
@Test
  public void testTransformTreeHandlesEmptyPrePreTerminalTree() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("word");

    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.setTag("NN");
    label.setWord("word");

    Tree preterminal = tf.newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree outer = tf.newTreeNode(label, new ArrayList<Tree>());
    List<Tree> outerChildren = new ArrayList<Tree>();
    outerChildren.add(preterminal);
    outer.setChildren(outerChildren);

    Tree transformed = params.transformTree(outer, outer);
    assertNotNull(transformed);
    assertEquals("NP", transformed.label().value());
  }
@Test
  public void testSetUnknownOptionReturnsOriginalIndex() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-unknownFlag" };
    int result = params.setOptionFlag(args, 0);
    assertEquals(0, result);
  }
@Test
  public void testSetKnownFlagIncompleteArgsSkips() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitIN" }; 
    int result = params.setOptionFlag(args, 0);
    assertEquals(0, result);
  }
@Test
  public void testSubcategoryStripperReturnsNullForEmptyChildren() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newTreeNode((String) null, new ArrayList<Tree>());
    Tree result = params.subcategoryStripper().transformTree(leaf);
    assertNull(result);
  }
@Test
  public void testTransformTreeWithNoParentOrRoot() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    CoreLabel label = new CoreLabel();
    label.setValue("VBZ");
    label.setTag("VBZ");
    label.setWord("is");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("is");
    leaf.setLabel(label);
    Tree result = params.transformTree(leaf, null);

    assertNotNull(result);
    assertEquals("is", result.label().value());
  }
@Test
  public void testSetHeadFinderToInvalidClass() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-headFinder", "com.invalid.NonexistentHeadFinder" };
    int result = params.setOptionFlag(args, 0);
    assertEquals(2, result); 
  }
@Test
  public void testTransformTreeWithPreTerminalPPAndSplitIN5() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] argsSplitIN = new String[] { "-splitIN", "5" };
    params.setOptionFlag(argsSplitIN, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("TO");
    label.setTag("TO");
    label.setWord("to");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("to");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("PP", new ArrayList<Tree>());
    List<Tree> parentChildren = new ArrayList<Tree>();
    parentChildren.add(preterminal);
    parent.setChildren(parentChildren);

    Tree root = new LabeledScoredTreeFactory().newTreeNode("NP", new ArrayList<Tree>());
    List<Tree> rootChildren = new ArrayList<Tree>();
    rootChildren.add(parent);
    root.setChildren(rootChildren);

    Tree result = params.transformTree(preterminal, root);
    assertNotNull(result.label());
    assertTrue(result.label().value().contains("IN"));
  }
@Test
  public void testTransformTreeSplitBaseNPInsertsExtraNPNode() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] argsBaseNP = new String[] { "-baseNP", "2" };
    params.setOptionFlag(argsBaseNP, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.setTag("NN");
    label.setWord("car");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("car");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree outer = new LabeledScoredTreeFactory().newTreeNode("S", new ArrayList<Tree>());
    List<Tree> outerChildren = new ArrayList<Tree>();
    outerChildren.add(preterminal);
    outer.setChildren(outerChildren);

    Tree result = params.transformTree(preterminal, outer);
    assertNotNull(result);
    assertTrue(result.label().value().contains("NP"));
  }
@Test
  public void testTransformTreeLeavesPOSSPBecomesNPWhenSplitPoss2() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitPoss", "2" };
    params.setOptionFlag(args, 0);

    TreeFactory factory = new LabeledScoredTreeFactory();
    Tree posLeaf = factory.newLeaf("'s");
    CoreLabel posLabel = new CoreLabel();
    posLabel.setValue("POS");
    posLabel.setTag("POS");
    posLabel.setWord("'s");
    posLeaf.setLabel(posLabel);

    Tree npLeaf = factory.newLeaf("dog");
    CoreLabel npLabel = new CoreLabel();
    npLabel.setValue("NN");
    npLabel.setTag("NN");
    npLabel.setWord("dog");
    npLeaf.setLabel(npLabel);

    Tree npPreterminal = factory.newTreeNode(npLabel, new ArrayList<Tree>());
    List<Tree> npChildren = new ArrayList<Tree>();
    npChildren.add(npLeaf);
    npPreterminal.setChildren(npChildren);

    List<Tree> children = new ArrayList<Tree>();
    children.add(npPreterminal);
    children.add(posLeaf);

    CoreLabel parentLabel = new CoreLabel();
    parentLabel.setValue("NP");
    parentLabel.setTag("NP");
    parentLabel.setWord("'s");

    Tree np = factory.newTreeNode(parentLabel, children);
    Tree result = params.transformTree(np, np);

    assertNotNull(result);
    assertTrue(result.label().value().startsWith("POSSP"));
  }
@Test
  public void testTransformTreeWithMarkReflexivePRP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-markReflexivePRP" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("PRP");
    label.setTag("PRP");
    label.setWord("myself");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("myself");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree result = params.transformTree(preterminal, preterminal);
    assertNotNull(result);
    assertTrue(result.label().value().contains("-SE"));
  }
@Test
  public void testSetMultipleFlagsWithFlagGroupJennyCombination() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-jenny" };
    int result = params.setOptionFlag(args, 0);
    assertEquals(1, result); 
  }
@Test
  public void testSplitCCFlag_AddsCCSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitCC", "3" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("CC");
    label.setTag("CC");
    label.setWord("and");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("and");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree result = params.transformTree(preterminal, preterminal);
    assertTrue(result.label().value().contains("-A"));
  }
@Test
  public void testSplitNOTAddsNSuffixForNegations() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitNOT" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("RB");
    label.setTag("RB");
    label.setWord("not");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("not");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree result = params.transformTree(preterminal, preterminal);
    assertTrue(result.label().value().contains("-N"));
  }
@Test
  public void testUnaryINAddsUSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-unaryIN" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("IN");
    label.setTag("IN");
    label.setWord("with");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("with");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("PP", new ArrayList<Tree>());
    List<Tree> parentChildren = new ArrayList<Tree>();
    parentChildren.add(preterminal);
    parent.setChildren(parentChildren);

    Tree result = params.transformTree(preterminal, parent);
    assertTrue(result.label().value().contains("^U"));
  }
@Test
  public void testCollapseWhCategoriesCollapsesLabel() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-collapseWhCategories", "3" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("WHNP");
    label.setTag("WDT");
    label.setWord("which");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("which");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    List<Tree> children = new ArrayList<Tree>();
    children.add(leaf);
    preterminal.setChildren(children);

    Tree result = params.transformTree(preterminal, preterminal);
    assertFalse(result.label().value().contains("WH"));
  }
@Test
  public void testDominatesVAppendsVMarker() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-dominatesV", "1" };
    params.setOptionFlag(args, 0);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("run");
    CoreLabel label = new CoreLabel();
    label.setValue("VB");
    label.setTag("VB");
    label.setWord("run");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    preterminal.setChildren(List.of(leaf));

    Tree phrasal = new LabeledScoredTreeFactory().newTreeNode("VP", new ArrayList<Tree>());
    phrasal.setChildren(List.of(preterminal));

    Tree result = params.transformTree(phrasal, phrasal);
    assertTrue(result.label().value().endsWith("-v"));
  }
@Test
  public void testDominatesIAddsISuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-dominatesI" };
    params.setOptionFlag(args, 0);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("in");
    CoreLabel label = new CoreLabel();
    label.setValue("IN");
    label.setTag("IN");
    label.setWord("in");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree phrase = new LabeledScoredTreeFactory().newTreeNode("PP", List.of(preterminal));

    Tree result = params.transformTree(phrase, phrase);
    assertTrue(result.label().value().endsWith("-i"));
  }
@Test
  public void testDominatesCAddsCSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-dominatesC" };
    params.setOptionFlag(args, 0);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("and");
    CoreLabel label = new CoreLabel();
    label.setValue("CC");
    label.setTag("CC");
    label.setWord("and");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree phrase = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(preterminal));

    Tree result = params.transformTree(phrase, phrase);
    assertTrue(result.label().value().endsWith("-c"));
  }
@Test
  public void testSplitSGapped4AppendsDashGForNPOnly() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitSGapped", "4" };
    params.setOptionFlag(args, 0);

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("dog");
    leaf.setLabel(new CoreLabel());

    Tree np = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(leaf));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("S", List.of(np));

    Tree result = params.transformTree(root, root);
    assertTrue(result.label().value().contains("-G"));
  }
@Test
  public void testSplitVP3AppendsDeducedVerbalTag() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitVP", "3" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("running");
    label.setTag("VB");
    label.setValue("VB");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("running");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    preterminal.setChildren(List.of(leaf));

    Tree vp = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(preterminal));

    Tree result = params.transformTree(vp, vp);
    assertTrue(result.label().value().contains("VBG"));
  }
@Test
  public void testTransformTreeHandlesCollapseWhCategories2ForPosLabels() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-collapseWhCategories", "2" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("WDT");
    label.setTag("WDT");
    label.setWord("which");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("which");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree result = params.transformTree(preterminal, preterminal);

    assertEquals("DT", result.label().value());
  }
@Test
  public void testTransformTreeSplitAuxAppliesBeSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitAux", "2" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("is");
    label.setTag("VBZ");
    label.setValue("VBZ");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("is");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    preterminal.setChildren(List.of(leaf));

    Tree result = params.transformTree(preterminal, preterminal);
    assertTrue(result.label().value().contains("-BE"));
  }
@Test
  public void testSetOptionFlagHandlesFlagWithNoValueForCollapseWhCategories() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-collapseWhCategories" };
    int result = params.setOptionFlag(args, 0);
    assertEquals(0, result);
  }
@Test
  public void testTransformTreeDoesntFailOnNonVerbNPUnderVP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.setTag("NP");
    label.setWord("pizza");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("pizza");
    leaf.setLabel(label);

    Tree np = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree vp = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(np));

    Tree result = params.transformTree(vp, vp);
    assertNotNull(result);
    assertEquals("VP", result.label().value());
  }
@Test
  public void testSplitTRJJAddsTSuffixForTransitiveJJ() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitTRJJ" };
    params.setOptionFlag(args, 0);

    CoreLabel jjLabel = new CoreLabel();
    jjLabel.setValue("JJ");
    jjLabel.setTag("JJ");
    jjLabel.setWord("due");

    Tree adjLeaf = new LabeledScoredTreeFactory().newLeaf("due");
    adjLeaf.setLabel(jjLabel);

    Tree preterminalJJ = new LabeledScoredTreeFactory().newTreeNode(jjLabel, new ArrayList<Tree>());
    preterminalJJ.setChildren(List.of(adjLeaf));

    CoreLabel npLabel = new CoreLabel();
    npLabel.setValue("NP");
    npLabel.setTag("NP");
    npLabel.setWord("May");

    Tree npLeaf = new LabeledScoredTreeFactory().newLeaf("May");
    npLeaf.setLabel(npLabel);
    Tree preterminalNP = new LabeledScoredTreeFactory().newTreeNode(npLabel, List.of(npLeaf));

    Tree adjp = new LabeledScoredTreeFactory().newTreeNode("ADJP", List.of(preterminalJJ, preterminalNP));

    Tree result = params.transformTree(adjp, adjp);
    String transformedLabel = result.getChild(0).label().value();
    assertTrue(transformedLabel.contains("^T"));
  }
@Test
  public void testSplitJJCOMPAppendsCMPLSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitJJCOMP" };
    params.setOptionFlag(args, 0);

    CoreLabel jjLabel = new CoreLabel();
    jjLabel.setValue("JJ");
    jjLabel.setTag("JJ");
    jjLabel.setWord("likely");

    Tree adjLeaf = new LabeledScoredTreeFactory().newLeaf("likely");
    adjLeaf.setLabel(jjLabel);
    Tree preterminalJJ = new LabeledScoredTreeFactory().newTreeNode(jjLabel, List.of(adjLeaf));

    Tree sNode = new LabeledScoredTreeFactory().newTreeNode("S", new ArrayList<Tree>());
    Tree adjp = new LabeledScoredTreeFactory().newTreeNode("ADJP", List.of(preterminalJJ, sNode));

    Tree result = params.transformTree(adjp, adjp);
    String transformedCat = result.getChild(0).label().value();
    assertTrue(transformedCat.contains("^CMPL"));
  }
@Test
  public void testJoinNounTagsRemapsNNPToNN() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-joinNounTags" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("Stanford");
    label.setValue("NNP");
    label.setTag("NNP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("Stanford");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree result = params.transformTree(preterminal, preterminal);
    assertTrue(result.label().value().startsWith("NN"));
  }
@Test
  public void testSplitMoreLessAddsMLSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitMoreLess" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("more");
    label.setTag("RBR");
    label.setValue("RBR");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("more");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree result = params.transformTree(preterminal, preterminal);
    assertTrue(result.label().value().contains("-ML"));
  }
@Test
  public void testRightPhrasalAddsRXSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-rightPhrasal" };
    params.setOptionFlag(args, 0);

    CoreLabel npLabel = new CoreLabel();
    npLabel.setWord("dog");
    npLabel.setTag("NN");
    npLabel.setValue("NP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("dog");
    leaf.setLabel(npLabel);
    Tree np = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(leaf));

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(np));
    Tree result = params.transformTree(parent, parent);
    assertTrue(result.label().value().endsWith("-RX"));
  }
@Test
  public void testSplitVP2AddsVBFForFiniteVerb() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitVP", "2" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("walked");
    label.setTag("VBD");
    label.setValue("VBD");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("walked");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree vp = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(preterminal));
    Tree result = params.transformTree(vp, vp);
    assertTrue(result.label().value().contains("-VBF"));
  }
@Test
  public void testSplitNPNNPLevel1AddsNNPSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitNPNNP", "1" };
    params.setOptionFlag(args, 0);

    CoreLabel nnp = new CoreLabel();
    nnp.setWord("IBM");
    nnp.setTag("NNP");
    nnp.setValue("NNP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("IBM");
    leaf.setLabel(nnp);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(nnp, List.of(leaf));

    Tree np = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(preterminal));
    Tree result = params.transformTree(np, np);
    assertTrue(result.label().value().endsWith("-NNP"));
  }
@Test
  public void testSplitSGapped2MarksSWhenOnlyOneNPPresent() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitSGapped", "2" };
    params.setOptionFlag(args, 0);

    CoreLabel npLabel = new CoreLabel();
    npLabel.setWord("money");
    npLabel.setTag("NN");
    npLabel.setValue("NP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("money");
    leaf.setLabel(npLabel);
    Tree np = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(leaf));

    Tree root = new LabeledScoredTreeFactory().newTreeNode("S", List.of(np));

    Tree result = params.transformTree(root, root);
    assertTrue(result.label().value().contains("-G"));
  }
@Test
  public void testSplitPossLevel1AppendsPSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitPoss", "1" };
    params.setOptionFlag(args, 0);

    CoreLabel word1Label = new CoreLabel();
    word1Label.setWord("dog");
    word1Label.setTag("NN");
    word1Label.setValue("NP");

    CoreLabel word2Label = new CoreLabel();
    word2Label.setWord("'s");
    word2Label.setTag("POS");
    word2Label.setValue("POS");

    Tree word1Leaf = new LabeledScoredTreeFactory().newLeaf("dog");
    word1Leaf.setLabel(word1Label);

    Tree word2Leaf = new LabeledScoredTreeFactory().newLeaf("'s");
    word2Leaf.setLabel(word2Label);

    Tree word1Preterminal = new LabeledScoredTreeFactory().newTreeNode(word1Label, new ArrayList<Tree>());
    word1Preterminal.setChildren(List.of(word1Leaf));

    Tree word2Preterminal = new LabeledScoredTreeFactory().newTreeNode(word2Label, new ArrayList<Tree>());
    word2Preterminal.setChildren(List.of(word2Leaf));

    Tree np = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(word1Preterminal, word2Preterminal));
    Tree result = params.transformTree(np, np);

    assertTrue(result.label().value().endsWith("-P"));
  }
@Test
  public void testSplitBaseNPLevel1AppendsBSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-baseNP", "1" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("car");
    label.setTag("NN");
    label.setValue("NP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("car");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    preterminal.setChildren(List.of(leaf));

    Tree outerNP = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(preterminal));
    Tree result = params.transformTree(outerNP, outerNP);

    assertTrue(result.label().value().contains("-B"));
  }
@Test
  public void testSplitNPPercentMarksPhraseWithPercentUnderNP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitNPpercent", "1" };
    params.setOptionFlag(args, 0);

    CoreLabel percentLabel = new CoreLabel();
    percentLabel.setWord("%");
    percentLabel.setTag("NN");
    percentLabel.setValue("NN");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("%");
    leaf.setLabel(percentLabel);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(percentLabel, List.of(leaf));
    Tree np = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(preterminal));
    Tree result = params.transformTree(np, np);
    assertTrue(result.label().value().endsWith("-%"));
  }
@Test
  public void testCollapseWhCategoriesRestorePHRASALLevel4WHNPNotCollapsed() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-collapseWhCategories", "4" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("who");
    label.setTag("WP");
    label.setValue("WHNP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("who");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree result = params.transformTree(preterminal, preterminal);
    assertEquals("WHNP", result.label().value());
  }
@Test
  public void testSplitAuxLevel9MarksGetAsBE() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitAux", "9" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("gotten");
    label.setTag("VBN");
    label.setValue("VBN");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("gotten");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    preterminal.setChildren(List.of(leaf));

    Tree result = params.transformTree(preterminal, preterminal);
    assertTrue(result.label().value().contains("-BE"));
  }
@Test
  public void testSplitSGapped3DoesNotMarkCoordinatedS() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitSGapped", "3" };
    params.setOptionFlag(args, 0);

    Tree np1 = new LabeledScoredTreeFactory().newTreeNode("NP", new ArrayList<Tree>());
    Tree cc = new LabeledScoredTreeFactory().newTreeNode("CC", new ArrayList<Tree>());
    Tree sChild = new LabeledScoredTreeFactory().newTreeNode("S", new ArrayList<Tree>());

    Tree s = new LabeledScoredTreeFactory().newTreeNode("S", List.of(np1, cc, sChild));
    Tree result = params.transformTree(s, s);
    assertFalse(result.label().value().contains("-G"));
  }
@Test
  public void testUnrecognizedCollapseWhCategoryValueDoesNotThrow() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-collapseWhCategories", "5" }; 
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("when");
    label.setTag("WRB");
    label.setValue("WHADVP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("when");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree result = params.transformTree(preterminal, preterminal);

    assertNotNull(result);
    assertTrue(result.label().value().contains("RB"));
  }
@Test
  public void testFallbackToUniversalSemanticHeadFinderWhenOriginalDepsFalse() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder headFinder = params.typedDependencyHeadFinder();
    assertNotNull(headFinder);
    assertTrue(headFinder.getClass().getSimpleName().contains("UniversalSemanticHeadFinder"));
  }
@Test
  public void testSplitINLevel6AppendsVWhenGrandParentIsVP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitIN", "6" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("IN");
    label.setTag("IN");
    label.setWord("as");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("as");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("PP", List.of(preterminal));
    Tree grand = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(parent));

    Tree result = params.transformTree(preterminal, grand);
    assertTrue(result.label().value().contains("-V"));
  }
@Test
  public void testSetOptionFlagHandlesMakeCopulaHeadFlag() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-makeCopulaHead" };
    int consumed = params.setOptionFlag(args, 0);
    assertEquals(1, consumed);
  }
@Test
  public void testSplitTmpAppendsTMPMarker() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitTMP", "2" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.setTag("NP");
    label.setWord("today");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("today");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree tmpAnnotated = new LabeledScoredTreeFactory().newTreeNode("NP-TMP", List.of(preterminal));
    Tree result = params.transformTree(tmpAnnotated, tmpAnnotated);
    assertTrue(result.label().value().contains("TMP"));
  }
@Test
  public void testSetUnknownHeadFinderClassGracefullyFallbacks() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-headFinder", "com.invalid.NonExistentClass" };
    int consumed = params.setOptionFlag(args, 0);
    assertEquals(2, consumed); 
  }
@Test
  public void testTransformTreeBasedOnRBInADJPChangesToJJ() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-correctTags" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("free");
    label.setTag("RB");
    label.setValue("RB");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("free");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());
    preterminal.setChildren(List.of(leaf));

    Tree adjp = new LabeledScoredTreeFactory().newTreeNode("ADJP", List.of(preterminal));
    Tree result = params.transformTree(preterminal, adjp);
    assertTrue(result.label().value().startsWith("JJ"));
  }
@Test
  public void testSplitAuxBEWithSistersAndVPChildAnnotatesAsHV() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitAux", "2" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("VBZ");
    label.setTag("VBZ");
    label.setWord("'s");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("'s");
    leaf.setLabel(label);

    Tree vbzPreterm = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    CoreLabel vbnLabel = new CoreLabel();
    vbnLabel.setValue("VBN");
    vbnLabel.setTag("VBN");
    vbnLabel.setWord("given");

    Tree vbnLeaf = new LabeledScoredTreeFactory().newLeaf("given");
    vbnLeaf.setLabel(vbnLabel);

    Tree vbnPreterm = new LabeledScoredTreeFactory().newTreeNode(vbnLabel, List.of(vbnLeaf));
    Tree vpChild = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(vbnPreterm));

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(vbzPreterm, vpChild));
    Tree result = params.transformTree(vbzPreterm, parent);
    assertTrue(result.label().value().contains("-HV"));
  }
@Test
  public void testCollapseWhCategoriesAndWPConvertsToPRP() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-collapseWhCategories", "2" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setValue("WP");
    label.setTag("WP");
    label.setWord("who");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("who");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree result = params.transformTree(preterminal, preterminal);

    assertEquals("PRP", result.label().value());
  }
@Test
  public void testMarkCCLevel2DetectsConjp() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-markCC", "2" };
    params.setOptionFlag(args, 0);

    Tree left = new LabeledScoredTreeFactory().newTreeNode("NP", new ArrayList<Tree>());
    Tree conjp = new LabeledScoredTreeFactory().newTreeNode("CONJP", new ArrayList<Tree>());
    Tree right = new LabeledScoredTreeFactory().newTreeNode("NP", new ArrayList<Tree>());

    Tree root = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(left, conjp, right));

    Tree result = params.transformTree(root, root);
    assertTrue(result.label().value().contains("-CC"));
  }
@Test
  public void testTransformTreeWithOnlyWRBUnderSBARConvertsTagToIN() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-correctTags" };
    params.setOptionFlag(args, 0);

    CoreLabel wrbLabel = new CoreLabel();
    wrbLabel.setWord("because");
    wrbLabel.setTag("WRB");
    wrbLabel.setValue("WRB");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("because");
    leaf.setLabel(wrbLabel);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(wrbLabel, List.of(leaf));
    Tree sbar = new LabeledScoredTreeFactory().newTreeNode("SBAR", List.of(preterminal));

    Tree result = params.transformTree(preterminal, sbar);
    assertEquals("IN", result.label().value());
  }
@Test
  public void testTransformTreeSingleQuoteMarkGetsSGSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitQuotes" };
    params.setOptionFlag(args, 0);

    CoreLabel quote = new CoreLabel();
    quote.setWord("'");
    quote.setTag("``");
    quote.setValue("``");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("'");
    leaf.setLabel(quote);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(quote, List.of(leaf));
    Tree result = params.transformTree(preterminal, preterminal);

    assertTrue(result.label().value().contains("-SG"));
  }
@Test
  public void testTransformTreeSplitRBAddsModifierSuffixForNPParent() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitRB" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("rarely");
    label.setTag("RB");
    label.setValue("RB");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("rarely");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(preterminal));
    Tree result = params.transformTree(preterminal, parent);
    assertTrue(result.label().value().contains("^M"));
  }
@Test
  public void testVPsubCatDoesNotAppendSubcatWhenNoNP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-vpSubCat" };
    params.setOptionFlag(args, 0);

    CoreLabel vb = new CoreLabel();
    vb.setWord("eat");
    vb.setTag("VB");
    vb.setValue("VB");

    Tree vbLeaf = new LabeledScoredTreeFactory().newLeaf("eat");
    vbLeaf.setLabel(vb);
    Tree vbPre = new LabeledScoredTreeFactory().newTreeNode(vb, List.of(vbLeaf));

    Tree vp = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(vbPre));
    Tree result = params.transformTree(vbPre, vp);
    assertTrue(result.label().value().startsWith("VB"));
  }
@Test
  public void testTransformTreeWithEmptyChildrenDoesNotFail() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.setTag("NP");
    label.setWord("X");

    Tree node = new LabeledScoredTreeFactory().newTreeNode(label, new ArrayList<Tree>());

    Tree result = params.transformTree(node, node);
    assertEquals("NP", result.label().value());
  }
@Test
  public void testSplitJJUnderPPAppendsSuperTag() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flags = new String[] { "-splitPPJJ" };
    params.setOptionFlag(flags, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("big");
    label.setTag("JJ");
    label.setValue("JJ");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("big");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("PP", List.of(preterminal));
    Tree result = params.transformTree(preterminal, parent);

    assertTrue(result.label().value().contains("^S"));
  }
@Test
  public void testSplitAuxFallbackToDOAnnotation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flags = new String[] { "-splitAux", "4" };
    params.setOptionFlag(flags, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("help");
    label.setTag("VB");
    label.setValue("VB");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("help");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree result = params.transformTree(preterminal, preterminal);
    assertTrue(result.label().value().contains("-DO"));
  }
@Test
  public void testUnknownWordsTriggerTagCorrectionToVBZ() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-correctTags" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("heaves");
    label.setValue("VBD");
    label.setTag("VBD");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("heaves");
    leaf.setLabel(label);
    Tree node = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree vp = new LabeledScoredTreeFactory().newTreeNode("VP", List.of(node));

    Tree result = params.transformTree(node, vp);
    assertEquals("VBZ", result.label().value());
  }
@Test
  public void testJoinJJNormalizesAllJJVariantsToJJ() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-joinJJ" };
    params.setOptionFlag(args, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("big");
    label.setValue("JJR");
    label.setTag("JJR");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("big");
    leaf.setLabel(label);
    Tree node = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree result = params.transformTree(node, node);
    assertEquals("JJ", result.label().value());
  }
@Test
  public void testUnaryRBMarksRBIfSingleChildInParent() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-unaryRB" }, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("now");
    label.setValue("RB");
    label.setTag("RB");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("now");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree parent = new LabeledScoredTreeFactory().newTreeNode("ADVP", new ArrayList<Tree>());
    parent.setChildren(List.of(preterminal));

    Tree result = params.transformTree(preterminal, parent);
    assertTrue(result.label().value().contains("^U"));
  }
@Test
  public void testUnaryPRPAppendsUSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-unaryPRP" }, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("he");
    label.setTag("PRP");
    label.setValue("PRP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("he");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("NP", List.of(preterminal));
    Tree result = params.transformTree(preterminal, parent);

    assertTrue(result.label().value().contains("^U"));
  }
@Test
  public void testSplitINLevel2WithSParentAddsCaretS() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-splitIN", "2" }, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("for");
    label.setTag("IN");
    label.setValue("IN");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("for");
    leaf.setLabel(label);
    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));
    Tree parent = new LabeledScoredTreeFactory().newTreeNode("SBAR", List.of(preterminal));
    Tree grand = new LabeledScoredTreeFactory().newTreeNode("S", List.of(parent));

    Tree result = params.transformTree(preterminal, grand);
    assertTrue(result.label().value().contains("^S"));
  }
@Test
  public void testCollapseWhCategories2DoesNotStripNP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-collapseWhCategories", "2" }, 0);

    CoreLabel label = new CoreLabel();
    label.setWord("what");
    label.setValue("WP");
    label.setTag("WP");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf("what");
    leaf.setLabel(label);

    Tree preterminal = new LabeledScoredTreeFactory().newTreeNode(label, List.of(leaf));

    Tree result = params.transformTree(preterminal, preterminal);
    assertEquals("PRP", result.label().value());
  }
//@Test
//  public void testBaseCategoryChangeRetainsAnnotationSuffix() {
//    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
//
//    String original = "NN-TMP";
//    String newBase = "NP";
//    String changed = params.changeBaseCat(original, newBase);
//    assertEquals("NP-TMP", changed);
//  }
}