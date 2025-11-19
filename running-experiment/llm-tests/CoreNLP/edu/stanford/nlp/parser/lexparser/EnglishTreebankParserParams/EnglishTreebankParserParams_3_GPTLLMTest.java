package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Index;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EnglishTreebankParserParams_3_GPTLLMTest {

 @Test
  public void testDiskTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertNotNull(params.diskTreebank());
  }
@Test
  public void testMemoryTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertNotNull(params.memoryTreebank());
  }
@Test
  public void testTreeReaderFactoryReadsTree() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReaderFactory trf = params.treeReaderFactory();
    StringReader input = new StringReader("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ barks)) (. .)))");
    TreeReader reader = trf.newTreeReader(input);
    Tree tree = reader.readTree();
    assertNotNull(tree);
    assertEquals("ROOT", tree.label().value());
  }
@Test
  public void testDefaultTestSentenceReturnsExpected() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> sentence = params.defaultTestSentence();
    assertEquals(6, sentence.size());
    assertEquals("This", sentence.get(0).word());
    assertEquals("test", sentence.get(4).word());
    assertEquals(".", sentence.get(5).word());
  }
@Test
  public void testTransformTreeReturnsSameLeaf() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree leaf = Tree.valueOf("(NN Test)");
    Tree result = params.transformTree(leaf, leaf);
    assertNotNull(result);
    assertEquals("NN", result.label().value());
  }
@Test
  public void testSubcategoryStripperRemovesADVSubcategory() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree input = Tree.valueOf("(ADVP-ADV (RB quickly))");
    TreeTransformer stripper = params.subcategoryStripper();
    Tree output = stripper.transformTree(input);
    assertNotNull(output);
    assertEquals("ADVP", output.label().value());
  }
@Test
  public void testCollinizerInstance() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    AbstractCollinizer collinizer = params.collinizer();
    assertNotNull(collinizer);
  }
//@Test
//  public void testLexiconTrainerDefaultSet() {
//    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
//
//    Options op = new Options();
//    Index<String> wordIndex = new Index<>();
//    wordIndex.add("the");
//    wordIndex.add("dog");
//
//    Index<String> tagIndex = new Index<>();
//    tagIndex.add("DT");
//    tagIndex.add("NN");
//
//    Lexicon lexicon = params.lex(op, wordIndex, tagIndex);
//    assertNotNull(lexicon);
//    assertNotNull(op.lexOptions.uwModelTrainer);
//    assertTrue(op.lexOptions.uwModelTrainer.contains("EnglishUnknownWordModelTrainer"));
//  }
@Test
  public void testSetOptionFlagSplitPoss2TransformsTree() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flag = { "-splitPoss", "2" };
    int nextIndex = params.setOptionFlag(flag, 0);
    assertEquals(2, nextIndex);

    Tree tree = Tree.valueOf("(NP (NP (NNP John)) (POS 's))");
    Tree transformed = params.subcategoryStripper().transformTree(tree);
    assertNotNull(transformed);
    assertEquals("POSSP", transformed.label().value());
  }
@Test
  public void testSetOptionFlagRetainTMPSubcategories() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flag = { "-retainTMPSubcategories" };
    int nextIndex = params.setOptionFlag(flag, 0);
    assertEquals(1, nextIndex);

    Tree tree = Tree.valueOf("(NP-TMP (DT the) (NN week))");
    Tree transformed = params.subcategoryStripper().transformTree(tree);
    assertNotNull(transformed);
    assertEquals("NP-TMP", transformed.label().value());
  }
@Test
  public void testTypedDependencyHeadFinder() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder hf = params.typedDependencyHeadFinder();
    assertNotNull(hf);
  }
@Test
  public void testTransformTreeCorrectTags() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] opt = { "-correctTags" };
    int n = params.setOptionFlag(opt, 0);
    assertEquals(1, n);

    Tree tree = Tree.valueOf("(NP (IN about) (NN revenue))");
    Tree transformed = params.transformTree(tree, tree);
    assertNotNull(transformed);
    Tree firstChild = transformed.getChild(0);
    String label = firstChild.label().value();
    assertNotNull(label);
  }
@Test
  public void testTransformTreeRetainsPOSSPNode() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] option = { "-splitPoss", "2" };
    int idx = params.setOptionFlag(option, 0);
    assertEquals(2, idx);

    Tree tree = Tree.valueOf("(NP (NP (JJ company)) (POS 's))");
    Tree stripped = params.subcategoryStripper().transformTree(tree);
    assertNotNull(stripped);
    assertEquals("POSSP", stripped.label().value());
  }
@Test
  public void testSisterSplittersReturnsLevel1() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] set = { "-baseNP", "1" };
    int next = params.setOptionFlag(set, 0);
    assertEquals(2, next);

    String[] splitters = params.sisterSplitters();
    assertNotNull(splitters);
    assertTrue(splitters.length > 0);
  }
@Test
  public void testSetUnknownOptionReturnsSameIndex() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flag = { "-nonexistentFlag" };
    int result = params.setOptionFlag(flag, 0);
    assertEquals(0, result);
  }
@Test
  public void testTransformTreeWithSplitINOption() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] splitIn = { "-splitIN", "3" };
    int idx = params.setOptionFlag(splitIn, 0);
    assertEquals(2, idx);

    Tree tree = Tree.valueOf("(PP (IN in) (NP (NNP London)))");
    Tree transformed = params.transformTree(tree, tree);
    Tree inNode = transformed.getChild(0);
    assertTrue(inNode.value().startsWith("IN"));
  }
@Test
  public void testTransformTreeWithBaseNPOption() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] opt = { "-baseNP", "2" };
    int idx = params.setOptionFlag(opt, 0);
    assertEquals(2, idx);

    Tree tree = Tree.valueOf("(NP (DT The) (NN cat))");
    Tree transformed = params.transformTree(tree, tree);
    assertNotNull(transformed);
    assertEquals("NP", transformed.label().value());
  }
@Test
  public void testSupportsBasicDependenciesTrue() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertTrue(params.supportsBasicDependencies());
  }
@Test
public void testSubcategoryStripperHandlesEmptyTree() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  TreeTransformer transformer = params.subcategoryStripper();
  Tree stripped = transformer.transformTree(null);
  assertNull(stripped);
}
@Test
public void testSubcategoryStripperSingleLeafTree() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("(NNP Alice)");
  Tree stripped = params.subcategoryStripper().transformTree(tree);
  assertEquals("NNP", stripped.label().value());
  assertEquals("Alice", stripped.children()[0].label().value());
}
@Test
public void testTransformTreeHandlesNullRoot() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("(NP (NNP Alice))");
  Tree transformed = params.transformTree(tree, null);
  assertNotNull(transformed);
  assertEquals("NP", transformed.label().value());
}
@Test
public void testSetOptionFlagHandlesInvalidNumberFormatGracefully() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitVP", "notANumber" };
  try {
    params.setOptionFlag(args, 0);
    fail("Expected NumberFormatException");
  } catch (NumberFormatException e) {
    assertTrue(e.getMessage().contains("For input string"));
  }
}
@Test
public void testTransformTreeHandlesSbarTagging() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] option = { "-splitSbar", "1" };
  int i = params.setOptionFlag(option, 0);
  assertEquals(2, i);

  Tree tree = Tree.valueOf("(SBAR (IN in) (NN order))");
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().startsWith("SBAR"));
}
@Test
public void testTransformTreeSplitNPPRPOption() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] arg = { "-splitNPPRP" };
  int i = params.setOptionFlag(arg, 0);
  assertEquals(1, i);

  Tree tree = Tree.valueOf("(NP (PRP they))");
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().startsWith("NP"));
}
@Test
public void testTransformTreeRightPhrasalTag() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] arg = { "-rightPhrasal" };
  int i = params.setOptionFlag(arg, 0);
  assertEquals(1, i);

  Tree tree = Tree.valueOf("(VP (VB runs) (NP (DT the) (NN game)))");
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().contains("-RX"));
}
@Test
public void testTransformTreeCollapseWhCategories() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flag = { "-collapseWhCategories", "3" };
  int index = params.setOptionFlag(flag, 0);
  assertEquals(2, index);

  Tree tree = Tree.valueOf("(WHNP (WP who))");
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().equals("NP"));
}
@Test
public void testDefaultCoreNLPFlagsReturnsNonNull() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flags = params.defaultCoreNLPFlags();
  assertNotNull(flags);
  assertEquals(1, flags.length);
  assertEquals("-retainTmpSubcategories", flags[0]);
}
@Test
public void testTransformTreeHandlesEmptyChildren() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  TreeFactory tf = new LabeledScoredTreeFactory();
  Label label = new CoreLabel();
  label.setValue("NP");
  Tree fakeTree = tf.newTreeNode(label, new java.util.ArrayList<Tree>());
  Tree transformed = params.transformTree(fakeTree, fakeTree);
  assertNull(transformed);
}
@Test
public void testGetGrammaticalStructureReturnsExpectedTypeForBasic() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  params.setOptionFlag(new String[] { "-originalDependencies" }, 0);

  Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ barks))))");
  GrammaticalStructure gs = params.getGrammaticalStructure(tree, null, params.headFinder());
  assertTrue(gs.getClass().getSimpleName().contains("EnglishGrammaticalStructure"));
}
@Test
public void testGetGrammaticalStructureReturnsUniversalWhenDisabled() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBZ sleeps))))");
  GrammaticalStructure gs = params.getGrammaticalStructure(tree, null, params.headFinder());
  assertTrue(gs.getClass().getSimpleName().contains("UniversalEnglishGrammaticalStructure"));
}
@Test
public void testSetOptionFlagHandlesMultipleSequentialOptions() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-baseNP", "1", "-splitVP", "2", "-splitPoss", "1" };
  int index1 = params.setOptionFlag(args, 0);
  assertEquals(2, index1);
  int index2 = params.setOptionFlag(args, index1);
  assertEquals(4, index2);
  int index3 = params.setOptionFlag(args, index2);
  assertEquals(6, index3);
}
@Test
public void testSetOptionFlagHandlesHeadFinderClassReflectionFailure() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-headFinder", "non.existent.HeadFinderClass" };
  int index = params.setOptionFlag(args, 0);
  assertEquals(2, index); 
}
@Test
public void testSetOptionFlagParsesACL03PCFGPresetCorrectly() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-acl03pcfg" };
  int index = params.setOptionFlag(args, 0);
  assertEquals(1, index);

  String[] flags = params.defaultCoreNLPFlags();
  assertNotNull(flags);
  assertEquals("-retainTmpSubcategories", flags[0]);
}
@Test
public void testTransformTreeBaseTagDeductionVBGFromWordSuffix() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitVP", "4" };
  int index = params.setOptionFlag(args, 0);
  assertEquals(2, index);

  CoreLabel label = new CoreLabel();
  label.setValue("VP");
  label.setTag("VB");
  label.setWord("eating");
  Tree wordLeaf = new LabeledScoredTreeFactory().newLeaf(label);
  Tree vpNode = new LabeledScoredTreeFactory().newTreeNode(label, java.util.Arrays.asList(wordLeaf));

  Tree transformed = params.transformTree(vpNode, vpNode);
  assertTrue(transformed.label().value().contains("VBG"));
}
@Test
public void testTransformTreeHandlesUnaryInOption() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-unaryIN" };
  int index = params.setOptionFlag(args, 0);
  assertEquals(1, index);

  Tree tree = Tree.valueOf("(IN in)");
  Tree wrapper = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree transformed = params.transformTree(wrapper, wrapper);
  Tree inNode = transformed.firstChild();
  assertTrue(inNode.label().value().startsWith("IN"));
}
@Test
public void testTransformTreeWithMakePPTOintoINFlag() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-makePPTOintoIN", "1" };
  int index = params.setOptionFlag(args, 0);
  assertEquals(2, index);

  Tree tree = Tree.valueOf("(PP (TO to) (NP (NNP London)))");
  Tree transformed = params.transformTree(tree, tree);
  Tree toNode = transformed.getChild(0);
  String tag = toNode.label().value();
  assertTrue(tag.startsWith("IN") || tag.contains("-IN"));
}
@Test
public void testTransformTreeAppliesSplitNPADVFlag() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitNPADV", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(NP-ADV (DT the) (NN moment))");
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().startsWith("NP"));
}
@Test
public void testSubcategoryStripperPreservesNP_TMPWhenOptionSet() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-retainNPTMPSubcategories" };
  int index = params.setOptionFlag(args, 0);
  assertEquals(1, index);

  Tree tree = Tree.valueOf("(NP-TMP (CD Monday))");
  Tree stripped = params.subcategoryStripper().transformTree(tree);
  assertEquals("NP-TMP", stripped.label().value());
}
@Test
public void testTransformTreeHandlesJJCategoryWithWordStock() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-correctTags" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(ADJP (JJ stock))");
  Tree transformed = params.transformTree(tree, tree);
  Tree leaf = transformed.getChild(0);
  assertFalse("JJ incorrectly tagged 'stock'", leaf.label().value().equals("JJ"));
}
@Test
public void testTransformTreeHandlesMultipleBaseNPInsert() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-baseNP", "2" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(NP (NP (DT A) (NN house)))");
  Tree transformed = params.transformTree(tree, tree);
  assertNotNull(transformed);
  assertEquals("NP", transformed.label().value());
}
@Test
public void testSetOptionFlagHandlesMissingArgument() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitVP" };
  try {
    params.setOptionFlag(args, 0);
    fail("Expected ArrayIndexOutOfBoundsException when missing argument");
  } catch (ArrayIndexOutOfBoundsException e) {
    assertTrue(e.getMessage() == null || e.getMessage().isEmpty());
  }
}
@Test
public void testTransformTreeHandlesSplitPossLevel1() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitPoss", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(NP (NP (NN kid)) (POS 's))");
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().contains("-P"));
}
@Test
public void testTransformTreeHandlesEmptyPOSNodeGracefully() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("(POS )");
  Tree parent = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree result = params.transformTree(parent, parent);
  assertEquals("NP", result.label().value());
}
@Test
public void testTransformTreeHandlesUnusualCoordination() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-markCC", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(NP (NN apples) (CC or) (NN oranges))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-CC"));
}
@Test
public void testTypedDependencyHeadFinderReturnsDefaultWhenNoFlagSet() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  HeadFinder hf = params.typedDependencyHeadFinder();
  assertNotNull(hf);
  assertTrue(hf.getClass().getSimpleName().contains("UniversalSemanticHeadFinder"));
}
@Test
public void testTreeReaderFactoryParsesMalformedTreeGracefully() throws Exception {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  TreeReaderFactory factory = params.treeReaderFactory();
  TreeReader reader = factory.newTreeReader(new StringReader("()"));
  Tree tree = reader.readTree();
  assertNull(tree); 
}
@Test
public void testTransformTreeHandlesEmptyQPStructure() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree qp = Tree.valueOf("(QP)");
  Tree result = params.transformTree(qp, qp);
  assertNull(result);
}
@Test
public void testTransformTreeHandlesWhitespaceWord() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(" ");
  Tree node = Tree.valueOf("(NNP )");
  Tree result = params.transformTree(node, node);
  assertNotNull(result);
}
@Test
public void testCollapseWhCategoriesCombinedFlags() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-collapseWhCategories", "6" }; 
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(WHADVP (WRB why))");
  Tree transformed = params.transformTree(tree, tree);
  assertNotNull(transformed);
  assertEquals("ADVP", transformed.label().value());

  Tree tree2 = Tree.valueOf("(WHNP (WDT which))");
  Tree transformed2 = params.transformTree(tree2, tree2);
  assertEquals("NP", transformed2.label().value());
}
@Test
public void testTransformTreeHandlesNonPreTerminalLeafOnlyNode() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree single = Tree.valueOf("(VBD ran)");
  Tree transformed = params.transformTree(single, single);
  assertEquals("VBD", transformed.label().value());
}
@Test
public void testTransformTreeHandlesMultipleSiblingCoordination() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-markCC", "2" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(VP (VB eat) (NP (NNP Mike)) (CC and) (NP (NNP Nancy)))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-CC"));
}
@Test
public void testSubcategoryStripperPreservesADVIfOptionSet() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-retainADVSubcategories" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(ADVP-ADV (RB quickly))");
  Tree stripped = params.subcategoryStripper().transformTree(tree);
  assertEquals("ADVP-ADV", stripped.label().value());
}
@Test
public void testSisterSplittersReturnsEmptyArrayForInvalidLevel() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-baseNP", "1" };
  params.setOptionFlag(args, 0);

  Tree dummy = Tree.valueOf("(NP (DT the) (NN cat))");
  Tree transformed = params.transformTree(dummy, dummy);
  String[] splitters = params.sisterSplitters();
  assertNotNull(splitters);
  assertTrue(splitters.length > 0);

  
  String[] reset = { "-baseNP", "5" };
  params.setOptionFlag(reset, 0);
  String[] defaultSplitters = params.sisterSplitters();
  assertEquals(0, defaultSplitters.length);
}
@Test
public void testTransformTreeHandlesRootCategorySFlaggedByGpaRootVP() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-gpaRootVP" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(ROOT (VP (VB walk)))");
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().contains("~ROOT"));
}
@Test
public void testSetOptionFlagHandlesCollapseWhCategoriesInvalidArgument() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-collapseWhCategories", "invalid" };
  try {
    params.setOptionFlag(args, 0);
    fail("Expected NumberFormatException");
  } catch (NumberFormatException e) {
    assertTrue(e.getMessage().contains("invalid"));
  }
}
@Test
public void testTransformTreeHandlesUnknownVBTag() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitVP", "4" };
  params.setOptionFlag(args, 0);

  CoreLabel label = new CoreLabel();
  label.setValue("VP");
  label.setTag("VB");
  label.setWord("confuzzled");
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
  Tree tree = new LabeledScoredTreeFactory().newTreeNode(label, java.util.Collections.singletonList(leaf));
  Tree transformed = params.transformTree(tree, tree);
  assertTrue(transformed.label().value().contains("VB"));
}
@Test
public void testTransformTreeHandlesUnusualINAnnotation() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitIN", "6" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(PP (IN beside) (NP (NN chair)))");
  Tree transformed = params.transformTree(tree, tree);
  Tree prep = transformed.getChild(0);
  assertTrue(prep.label().value().startsWith("IN"));
}
@Test
public void testTransformTreeHandlesUnaryRBWithoutSplit() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree singleRb = Tree.valueOf("(RB alone)");
  Tree wrapper = Tree.valueOf("(ADVP " + singleRb.toString() + ")");
  Tree transformed = params.transformTree(wrapper, wrapper);
  Tree child = transformed.getChild(0);
  assertTrue(child.label().value().startsWith("RB"));
}
@Test
public void testSubcategoryStripperHandlesPOSSPStructureNotTriggeringRestructure() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("(POSSP (NP (NN market)) (POS 's))");
  TreeTransformer stripper = params.subcategoryStripper();
  Tree result = stripper.transformTree(tree);
  assertEquals("NP", result.label().value());
}
@Test
public void testTransformTreeHandlesUniversalTagFallbackWhenTagUnknown() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flag = { "-splitVP", "4" };
  params.setOptionFlag(flag, 0);

  CoreLabel label = new CoreLabel();
  label.setValue("VP");
  label.setTag("XX");
  label.setWord("infodump");
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
  Tree root = new LabeledScoredTreeFactory().newTreeNode(label, java.util.Collections.singletonList(leaf));
  Tree result = params.transformTree(root, root);
  assertTrue(result.label().value().contains("VB"));
}
@Test
public void testTransformTreeHandlesNullWordAndTagGracefully() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();

  CoreLabel label = new CoreLabel(); 
  label.setValue("NN");
  Tree leaf = new LabeledScoredTreeFactory().newLeaf(label);
  Tree tree = new LabeledScoredTreeFactory().newTreeNode(label, java.util.Collections.singletonList(leaf));
  Tree result = params.transformTree(tree, tree);
  assertNotNull(result);
  assertNotNull(result.label().value());
}
@Test
public void testSetOptionFlagAppliesJennyPresetCorrectly() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-jenny" };
  int result = params.setOptionFlag(args, 0);
  assertEquals(1, result);

  Tree tree = Tree.valueOf("(NP (DT some) (NN example))");
  Tree transformed = params.transformTree(tree, tree);
  assertNotNull(transformed);
}
@Test
public void testTransformTreeHandlesEmptyCategory() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("( ( ) )");
  Tree result = null;
  try {
    result = params.transformTree(tree, tree);
  } catch (Exception e) {
    
    fail("transformTree threw exception on empty category");
  }
  assertNotNull(result);
}
@Test
public void testTransformTreeDitransitiveVerbWithTwoNPs() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flags = { "-markDitransV", "2" };
  params.setOptionFlag(flags, 0);

  Tree tree = Tree.valueOf("(VP (VB give) (NP (DT a) (NN cat)) (NP (DT a) (NN toy)))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("^2Arg"));
}
@Test
public void testTransformTreeHandlesJJCOMPComplementDetection() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] options = { "-splitJJCOMP" };
  params.setOptionFlag(options, 0);

  Tree tree = Tree.valueOf("(ADJP (JJ afraid) (SBAR (IN that) (S (NP ...) (VP ...))))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("^CMPL"));
}
@Test
public void testTransformTreeReflexivePRPMarkedCorrectly() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flag = { "-markReflexivePRP" };
  params.setOptionFlag(flag, 0);

  Tree tree = Tree.valueOf("(PRP himself)");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-SE"));
}
@Test
public void testTransformTreeSplitCCFlag1OnAndCoordination() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitCC", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(CC and)");
  Tree wrapper = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree transformed = params.transformTree(wrapper, wrapper);
  Tree ccNode = transformed.getChild(0);
  assertTrue(ccNode.label().value().contains("-C"));
}
@Test
public void testTransformTreeSplitCCFlag2OnButCoordination() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitCC", "2" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(CC but)");
  Tree wrapper = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree transformed = params.transformTree(wrapper, wrapper);
  Tree ccNode = transformed.getChild(0);
  assertTrue(ccNode.label().value().contains("-B"));
}
@Test
public void testTransformTreeSplitCCFlag3OnlyForAnd() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitCC", "3" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(CC and)");
  Tree wrapper = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree transformed = params.transformTree(wrapper, wrapper);
  Tree ccNode = transformed.getChild(0);
  assertTrue(ccNode.label().value().contains("-A"));
}
@Test
public void testTransformTreeSplitRBInsideNP() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitRB" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(NP (RB clearly))");
  Tree result = params.transformTree(tree, tree);
  Tree rb = result.getChild(0);
  assertTrue(rb.label().value().contains("^M"));
}
@Test
public void testTransformTreeSplitSGappedVersion1WithNoNP() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitSGapped", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(S (VP (VB Runs)))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-G"));
}
@Test
public void testTransformTreeSplitSGappedVersion2WithNPOnly() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitSGapped", "2" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(S (NP (NNP John)))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-G"));
}
@Test
public void testTransformTreeSplitSGappedVersion3WithSAndCC() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitSGapped", "3" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(S (S (NP (NNP John)) (VP (VBZ runs))) (CC and) (VP (VBZ jumps)))");
  Tree result = params.transformTree(tree, tree);
  assertFalse(result.label().value().contains("-G"));
}
@Test
public void testTransformTreeSplitAuxVerbBeDetectsProperly() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitAux", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(VBZ is)");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-BE"));
}
@Test
public void testTransformTreeSplitAuxVerbHaveDetectsProperly() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitAux", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(VBZ has)");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-HV"));
}
@Test
public void testTransformTreeSplitAuxVerbDoDetectedOnlyWhenLevel3Plus() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitAux", "3" }; 
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(VBP do)");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-DO"));
}
@Test
public void testTransformTreeSplitNumNPAnnotation() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitNumNP" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(NP (CD 50) (NN dollars))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-NUM"));
}
@Test
public void testTransformTreeSplitMoreLessApplied() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitMoreLess" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(RB more)");
  Tree wrapper = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree result = params.transformTree(wrapper, wrapper);
  Tree child = result.getChild(0);
  assertTrue(child.label().value().contains("-ML"));
}
@Test
public void testTransformTreeCollapseWhCategoriesDoesNotCollapseNP() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-collapseWhCategories", "6" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(WHNP (WDT which))");
  Tree transformed = params.transformTree(tree, tree);
  assertEquals("NP", transformed.label().value());
}
@Test
public void testTransformTreeSplitSTagLevel3AppliesVBNF() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flags = { "-splitSTag", "3" };
  params.setOptionFlag(flags, 0);

  Tree tree = Tree.valueOf("(S (VP (VB eat)))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("-VBNF"));
}
@Test
public void testTransformTreeRetainsSplitNPPercentOnQP() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flags = { "-splitNPpercent", "3" };
  params.setOptionFlag(flags, 0);

  Tree tree = Tree.valueOf("(QP (CD 20) (% %))");
  Tree wrapper = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree result = params.transformTree(wrapper, wrapper);
  assertTrue(result.label().value().contains("-%"));
}
@Test
public void testSetOptionFlagHandlesSplitBaseNPWithInvalidValue() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-baseNP", "abc" };
  try {
    params.setOptionFlag(args, 0);
    fail("Expected NumberFormatException for non-integer value");
  } catch (NumberFormatException e) {
    assertTrue(e.getMessage().contains("abc"));
  }
}
@Test
public void testTransformTreeSplitVPLevel3UsesTagVBD() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitVP", "3" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(VP (VBD went))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("VBF"));
}
@Test
public void testTransformTreeSplitVPNPAgrUsesPluralAgreement() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitVPNPAgr" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(NP (NNS dogs))");
  Tree root = Tree.valueOf("(S " + tree.toString() + ")");
  Tree transformed = params.transformTree(root, root);
  Tree np = transformed.getChild(0);
  assertTrue(np.label().value().contains("-PL"));
}
@Test
public void testTransformTreeSplitSTagLevel5IncludesVBNFTag() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitSTag", "5" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(S (VP (VB go)))");
  Tree result = params.transformTree(tree, tree);
  String label = result.label().value();
  assertTrue(label.contains("VBNF") || label.contains("VBF"));
}
@Test
public void testTransformTreeJoinNounTagsMapsNNPToNN() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flag = { "-joinNounTags" };
  params.setOptionFlag(flag, 0);

  Tree tree = Tree.valueOf("(NNP Obama)");
  Tree wrapper = Tree.valueOf("(NP " + tree.toString() + ")");
  Tree result = params.transformTree(wrapper, wrapper);
  Tree nnp = result.getChild(0);
  assertEquals("NN", nnp.label().value());
}
@Test
public void testTransformTreeSplitJJCOMPDetectsNPComplement() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flag = { "-splitJJCOMP" };
  params.setOptionFlag(flag, 0);

  Tree tree = Tree.valueOf("(ADJP (JJ proud) (NP (DT his) (NN son)))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("^CMPL"));
}
@Test
public void testTransformTreeSplitTRJJDetectsTransitiveJJ() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flag = { "-splitTRJJ" };
  params.setOptionFlag(flag, 0);

  Tree tree = Tree.valueOf("(ADJP (JJ due) (NP (NN tomorrow)))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("^T"));
}
@Test
public void testTransformTreeSplitAuxLevel2DetectsHasAsHV() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] flag = { "-splitAux", "2" };
  params.setOptionFlag(flag, 0);

  Tree tree = Tree.valueOf("(VBZ 's)");
  Tree wrapper = Tree.valueOf("(VP " + tree.toString() + ")");
  Tree result = params.transformTree(wrapper, wrapper);
  Tree vbz = result.getChild(0);
  assertTrue(vbz.label().value().contains("-HV") || vbz.label().value().contains("-BE"));
}
@Test
public void testTransformTreeHandlesEmptyNPWithNoChildren() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("(NP)");
  Tree result = null;
  try {
    result = params.transformTree(tree, tree);
  } catch (Exception e) {
    fail("Empty NP should be handled without exception");
  }
  assertNull(result);
}
@Test
public void testTransformTreeVPSubCatRecordsCorrectly() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-vpSubCat" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(VP (VB eat) (NP (DT some) (NN food)) (PP (IN with) (NP (NN fork))))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("^aNP"));
}
@Test
public void testTransformTreeHeadTagPresentUnderSplitVP1() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  String[] args = { "-splitVP", "1" };
  params.setOptionFlag(args, 0);

  Tree tree = Tree.valueOf("(VP (VB walk))");
  Tree result = params.transformTree(tree, tree);
  assertTrue(result.label().value().contains("VB"));
}
@Test
public void testTransformTreeHandlesEmptyRoot() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  Tree tree = Tree.valueOf("(ROOT)");
  Tree result = params.transformTree(tree, tree);
  assertNull(result);
}
@Test
public void testSetOptionFlagHandlesUnknownCompositeFlags() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  int index = params.setOptionFlag(new String[] {"-noSuchFlagConfigured"}, 0);
  assertEquals(0, index);
}
@Test
public void testTransformTreeUnaryDTAnnotationApplied() {
  EnglishTreebankParserParams params = new EnglishTreebankParserParams();
  params.setOptionFlag(new String[] { "-unaryDT" }, 0);
  Tree tree = Tree.valueOf("(NP (DT this))");
  Tree result = params.transformTree(tree, tree);
  Tree dt = result.getChild(0);
  assertTrue(dt.label().value().contains("^U"));
} 
}