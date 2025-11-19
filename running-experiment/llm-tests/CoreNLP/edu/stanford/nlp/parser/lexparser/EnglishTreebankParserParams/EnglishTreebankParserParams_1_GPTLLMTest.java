package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.ling.CategoryWordTag;
import edu.stanford.nlp.ling.CategoryWordTagFactory;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Index;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EnglishTreebankParserParams_1_GPTLLMTest {

 @Test
  public void testHeadFinder() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder headFinder = params.headFinder();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree vbd = tf.newTreeNode("VBD", Arrays.asList(tf.newLeaf("saw")));
    Tree np = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("him")));
    Tree vp = tf.newTreeNode("VP", Arrays.asList(vbd, np));
    Tree head = headFinder.determineHead(vp);
    assertEquals("VBD", head.label().value());
  }
@Test
  public void testTypedDependencyHeadFinderDefault() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder hf = params.typedDependencyHeadFinder();
    assertNotNull(hf);
  }
@Test
  public void testTreeReaderFactoryReadsTree() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReaderFactory factory = params.treeReaderFactory();
    TreeReader reader = factory.newTreeReader(new StringReader("(S (NP (DT The) (NN cat)) (VP (VBZ sleeps)))"));
    Tree tree = reader.readTree();
    assertNotNull(tree);
    assertEquals("S", tree.label().value());
  }
@Test
  public void testMemoryTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank tb = params.memoryTreebank();
    assertNotNull(tb);
  }
@Test
  public void testDiskTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    DiskTreebank tb = params.diskTreebank();
    assertNotNull(tb);
  }
@Test
  public void testTestMemoryTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank tb = params.testMemoryTreebank();
    assertNotNull(tb);
  }
@Test
  public void testCollinizerNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    AbstractCollinizer collinizer = params.collinizer();
    AbstractCollinizer collinizerEvalb = params.collinizerEvalb();
    assertNotNull(collinizer);
    assertNotNull(collinizerEvalb);
  }
@Test
  public void testTreebankLanguagePack() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreebankLanguagePack tlp = params.treebankLanguagePack();
    assertTrue(tlp instanceof PennTreebankLanguagePack);
  }
//@Test
//  public void testLexiconInitialization() {
//    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
//    Options options = new Options();
////    Index<String> wordIndex = new Index<>();
////    Index<String> tagIndex = new Index<>();
//    Lexicon lexicon = params.lex(options, wordIndex, tagIndex);
//    assertNotNull(lexicon);
//  }
@Test
  public void testSubcategoryStripperTransformsNP_TMP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("NP-TMP");
    label.setWord("Saturday");
    label.setTag("NNP");
    Tree leaf = tf.newLeaf(label);
    Tree tree = tf.newTreeNode("NP-TMP", Arrays.asList(leaf));
    tree.setLabel(new CategoryWordTag("NP-TMP", "Saturday", "NNP"));
    Tree transformed = params.subcategoryStripper().transformTree(tree);
    assertNotNull(transformed);
    assertTrue(transformed.label().value().startsWith("NP"));
  }
@Test
  public void testSubcategoryStripperADVRetention() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[]{"-retainADVSubcategories"};
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("ADVP-ADV");
    label.setWord("quickly");
    label.setTag("RB");
    Tree leaf = tf.newLeaf(label);
    Tree tree = tf.newTreeNode("ADVP-ADV", Arrays.asList(leaf));
    tree.setLabel(new CategoryWordTag("ADVP-ADV", "quickly", "RB"));
    Tree transformed = params.subcategoryStripper().transformTree(tree);
    assertEquals("ADVP-ADV", transformed.label().value());
  }
@Test
  public void testSetOptionFlagReturnsNextIndex() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int next = params.setOptionFlag(new String[]{"-splitIN", "3"}, 0);
    assertEquals(2, next);
  }
@Test
  public void testSetOptionFlagInvalidReturnsSameIndex() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[]{"-unknownFlag"}, 0);
    assertEquals(0, result);
  }
@Test
  public void testSisterSplitterDefaultLevel() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] splitters = params.sisterSplitters();
    assertNotNull(splitters);
    assertTrue(splitters.length > 0);
  }
@Test
  public void testTransformTreeReturnsSameLeafTree() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("test");
    label.setWord("test");
    label.setTag("NN");
    Tree leaf = tf.newLeaf(label);
    Tree transformed = params.transformTree(leaf, null);
    assertEquals(leaf, transformed);
  }
@Test
  public void testSupportsBasicDependenciesTrue() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertTrue(params.supportsBasicDependencies());
  }
@Test
  public void testDefaultTestSentenceStructure() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> sentence = params.defaultTestSentence();
    assertEquals(6, sentence.size());
    assertEquals("This", sentence.get(0).word());
    assertEquals(".", sentence.get(5).word());
  }
@Test
  public void testTransformTreeChangesNP_TMPToNP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel leafLabel = new CoreLabel();
    leafLabel.setValue("NNP");
    leafLabel.setWord("Saturday");
    leafLabel.setTag("NNP");
    Tree leaf = tf.newLeaf(leafLabel);

    CoreLabel tmpLabel = new CoreLabel();
    tmpLabel.setValue("NP-TMP");
    tmpLabel.setWord("Saturday");
    tmpLabel.setTag("NNP");

    Tree child = tf.newTreeNode(tmpLabel, Arrays.asList(leaf));
    Tree rootLabel = tf.newTreeNode("S", Arrays.asList(child));
    child.setLabel(new CategoryWordTag("NP-TMP", "Saturday", "NNP"));
    rootLabel.setLabel(new CategoryWordTag("S", "S", "S"));

    Tree transformed = params.subcategoryStripper().transformTree(rootLabel);
    Tree childTransformed = transformed.getChild(0);
    assertTrue(childTransformed.label().value().startsWith("NP"));
  }
@Test
  public void testUnknownSetOptionFlagReturnsUnchangedIndex() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int index = params.setOptionFlag(new String[]{"-notAValidFlag"}, 0);
    assertEquals(0, index);
  }
@Test
  public void testSetOptionFlagMakeCopulaHead() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int index = params.setOptionFlag(new String[]{"-makeCopulaHead"}, 0);
    assertEquals(1, index);
  }
@Test
  public void testSetOptionFlagOriginalDependencies() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int newIndex = params.setOptionFlag(new String[]{"-originalDependencies"}, 0);
    assertEquals(1, newIndex);

    HeadFinder hf = params.typedDependencyHeadFinder();
    assertNotNull(hf);
  }
@Test
  public void testSubcategoryStripperReturnsNullWhenAllChildrenRemoved() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree phrasal = tf.newTreeNode("NP", Arrays.asList());
    Tree result = params.subcategoryStripper().transformTree(phrasal);
    assertNull(result);
  }
@Test
  public void testTransformTreeWithNullInputReturnsNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree transformed = params.transformTree(null, null);
    assertNull(transformed);
  }
@Test
  public void testTransformTreeWithNonPreterminalNonPhrasalNode() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue(":");
    label.setWord(":");
    label.setTag(":");
    Tree leaf = tf.newLeaf(label);
    Tree node = tf.newTreeNode(":", Arrays.asList(leaf));
    node.setLabel(label);

    Tree transformed = params.transformTree(node, node);
    assertNotNull(transformed);
    assertEquals(":", transformed.label().value());
  }
@Test
  public void testSisterSplitLevelZeroReturnsEmptyArray() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int idx = params.setOptionFlag(new String[]{"-baseNP", "1"}, 0);
    assertEquals(2, idx);

    String[] result = params.sisterSplitters();
    assertNotNull(result);
    assertTrue(result.length > 0);

    int idx2 = params.setOptionFlag(new String[]{"-splitIN", "0"}, 0);
    assertEquals(2, idx2);

    String[] split = params.sisterSplitters();
    assertNotNull(split);
  }
@Test
  public void testLexiconTrainerResetWhenNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Options options = new Options();
    options.lexOptions.uwModelTrainer = null;

//    Lexicon lex = params.lex(options, new Index<>(), new Index<>());
//    assertNotNull(lex);
  }
@Test
  public void testTransformTreeHandlesTreeWithScoreAndLeaf() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setWord("test");
    label.setTag("NN");
    label.setValue("NN");
    Tree leaf = tf.newLeaf(label);
    leaf.setScore(0.85);
    Tree result = params.subcategoryStripper().transformTree(leaf);
    assertNotNull(result);
    assertEquals(0.85, result.score(), 0.001);
  }
@Test
  public void testTransformTreeOnPhrasalWithOneChildStripsNestedNP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-baseNP", "2"};
    int updated = params.setOptionFlag(args, 0);
    assertEquals(2, updated);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree innerNP = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("book")));
    Tree outerNP = tf.newTreeNode("NP", Arrays.asList(innerNP));
    Tree topTree = tf.newTreeNode("S", Arrays.asList(outerNP));

    Tree transformed = params.subcategoryStripper().transformTree(topTree);
    assertNotNull(transformed);
    Tree np = transformed.getChild(0);
    assertEquals("NP", np.label().value());
  }
@Test
  public void testTransformTreeMergesPOSSP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-splitPoss", "2"};
    int updated = params.setOptionFlag(args, 0);
    assertEquals(2, updated);

    TreeFactory tf = new LabeledScoredTreeFactory(new CategoryWordTagFactory());
    Tree np1 = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("John")));
    Tree pos = tf.newTreeNode("POS", Arrays.asList(tf.newLeaf("'s")));
    Tree possp = tf.newTreeNode("POSSP", Arrays.asList(np1, pos));
    Tree root = tf.newTreeNode("S", Arrays.asList(possp));

    Tree result = params.subcategoryStripper().transformTree(root);
    assertNotNull(result);
    Tree transformedRoot = result.getChild(0);
    assertEquals("NP", transformedRoot.label().value().substring(0, 2));
  }
@Test
  public void testDefaultCoreNLPFlagsReturnsTMPFlag() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flags = params.defaultCoreNLPFlags();
    assertNotNull(flags);
    assertEquals("-retainTmpSubcategories", flags[0]);
  }
@Test
  public void testTreeReaderFactoryParsesEmptyTreeProperly() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReaderFactory factory = params.treeReaderFactory();
    TreeReader reader = factory.newTreeReader(new StringReader(""));
    Tree tree = reader.readTree();
    assertNull(tree);
  }
@Test
  public void testSetOptionWithHeadFinderFallback() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String className = "non.existent.HeadFinder";
    int index = params.setOptionFlag(new String[]{"-headFinder", className}, 0);
    assertEquals(2, index);
    
    HeadFinder hf = params.headFinder();
    assertNotNull(hf);
  }
@Test
  public void testSetOptionFlagBaseNP1SplitPoss1SplitTMP2() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[]{
        "-baseNP", "1", "-splitPoss", "1", "-splitTMP", "2"
    };
    int result = params.setOptionFlag(args, 0);
    assertEquals(2, result); 
    result = params.setOptionFlag(args, 2);
    assertEquals(4, result);
    result = params.setOptionFlag(args, 4);
    assertEquals(6, result);
  }
@Test
  public void testTransformTreeHandlesAdverbialSBARReasonClause() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-splitSbar", "3"};
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree labelIn = tf.newTreeNode("IN", Arrays.asList(tf.newLeaf("in")));
    Tree labelOrder = tf.newTreeNode("NN", Arrays.asList(tf.newLeaf("order")));
    Tree sbar = tf.newTreeNode("SBAR", Arrays.asList(labelIn, labelOrder));
    Tree result = params.transformTree(sbar, sbar);
    assertNotNull(result);
    assertTrue(result.label().value().contains("PURP"));
    assertTrue(result.label().value().contains("INF"));
  }
@Test
  public void testTransformTreeCollapseWhCategoriesOption() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-collapseWhCategories", "3"};
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel whnpLabel = new CoreLabel();
    whnpLabel.setValue("WHNP");
    whnpLabel.setWord("what");
    whnpLabel.setTag("WP");

    Tree leaf = tf.newLeaf(whnpLabel);
    Tree whnpNode = tf.newTreeNode(whnpLabel, Arrays.asList(leaf));
    Tree result = params.transformTree(whnpNode, whnpNode);
    assertNotNull(result);
    assertTrue(result.label().value().contains("NP"));
  }
@Test
  public void testTransformTreeSplitAuxVerbForms() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-splitAux", "4"};
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();

    CoreLabel label = new CoreLabel();
    label.setWord("help");
    label.setTag("VB");
    label.setValue("VB");

    Tree leaf = tf.newLeaf(label);
    Tree vp = tf.newTreeNode("VP", Arrays.asList(
        tf.newTreeNode(label, Arrays.asList(leaf))
    ));
    Tree top = tf.newTreeNode("ROOT", Arrays.asList(vp));

    Tree result = params.transformTree(vp, top);
    assertTrue(result.label().value().contains("DO"));
  }
@Test
  public void testTransformTreeSplitCCButAndAmp() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-splitCC", "2"};
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel ccLabel = new CoreLabel();
    ccLabel.setValue("CC");
    ccLabel.setWord("but");
    ccLabel.setTag("CC");

    Tree ccLeaf = tf.newLeaf(ccLabel);
    Tree ccNode = tf.newTreeNode(ccLabel, Arrays.asList(ccLeaf));
    Tree top = tf.newTreeNode("VP", Arrays.asList(ccNode));

    Tree result = params.transformTree(ccNode, top);
    assertTrue(result.label().value().contains("-B"));
  }
@Test
  public void testTransformTreeRightPhrasalRXMarked() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-baseNP", "1", "-rightPhrasal"};
    params.setOptionFlag(args, 0);
    params.setOptionFlag(args, 2);

    TreeFactory tf = new LabeledScoredTreeFactory();

    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.setWord("value");
    label.setTag("NN");

    Tree leaf = tf.newLeaf(label);
    Tree child = tf.newTreeNode("NP", Arrays.asList(leaf));
    Tree parent = tf.newTreeNode("S", Arrays.asList(child));

    Tree result = params.transformTree(parent, parent);
    assertTrue(result.label().value().contains("-RX"));
  }
@Test
  public void testLexiconNotNullWithExistingTrainerSet() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Options options = new Options();
    options.lexOptions.uwModelTrainer = "edu.stanford.nlp.parser.lexparser.MockTrainer";
//    Lexicon lexicon = params.lex(options, new Index<String>(), new Index<String>());
//    assertNotNull(lexicon);
  }
@Test
  public void testDiskTreebankLoadPathCreateInstance() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    DiskTreebank db = params.diskTreebank();
    assertNotNull(db);
  }
@Test
  public void testTransformTreeWithLeafReturnsSame() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel leafLabel = new CoreLabel();
    leafLabel.setWord("word");
    leafLabel.setValue("NN");
    leafLabel.setTag("NN");

    Tree leaf = tf.newLeaf(leafLabel);
    Tree result = params.transformTree(leaf, null);
    assertEquals(leaf, result);
  }
@Test
  public void testTransformTreeSplitJJCOMPWithComplementS() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-splitJJCOMP"};
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("JJ");
    label.setWord("ready");
    label.setTag("JJ");

    Tree leaf = tf.newLeaf(label);
    Tree complement = tf.newTreeNode("S", Arrays.asList(tf.newLeaf("go")));

    Tree adjp = tf.newTreeNode("ADJP", Arrays.asList(tf.newTreeNode(label, Arrays.asList(leaf)), complement));
    Tree parent = tf.newTreeNode("ROOT", Arrays.asList(adjp));

    Tree result = params.transformTree(adjp, parent);
    assertTrue(result.label().value().contains("^CMPL"));
  }
@Test
  public void testTransformTreeHandlesQuoteSplitting() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-splitQuotes"};
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setWord("'");
    label.setTag("``");
    label.setValue("``");

    Tree leaf = tf.newLeaf(label);
    Tree quoted = tf.newTreeNode("``", Arrays.asList(leaf));
    Tree sentence = tf.newTreeNode("S", Arrays.asList(quoted));

    Tree result = params.transformTree(quoted, sentence);
    assertTrue(result.label().value().contains("-SG"));
  }
@Test
  public void testCollapseWhCategoriesWHNPToNPConversion() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-collapseWhCategories", "1"};
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("who");
    Tree whnp = tf.newTreeNode("WHNP", Arrays.asList(leaf));
    Tree root = tf.newTreeNode("S", Arrays.asList(whnp));

    Tree result = params.transformTree(whnp, root);
    assertTrue(result.label().value().contains("NP"));
  }
@Test
  public void testTransformTreeUnaryDTAndRB() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-unaryDT", "-unaryRB"};
    params.setOptionFlag(args, 0);
    params.setOptionFlag(args, 1);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel dtLabel = new CoreLabel();
    dtLabel.setWord("the");
    dtLabel.setTag("DT");
    dtLabel.setValue("DT");

    CoreLabel rbLabel = new CoreLabel();
    rbLabel.setWord("indeed");
    rbLabel.setTag("RB");
    rbLabel.setValue("RB");

    Tree dtLeaf = tf.newLeaf(dtLabel);
    Tree rbLeaf = tf.newLeaf(rbLabel);

    Tree dtNode = tf.newTreeNode(dtLabel, Arrays.asList(dtLeaf));
    Tree rbNode = tf.newTreeNode(rbLabel, Arrays.asList(rbLeaf));

    Tree dtParent = tf.newTreeNode("NP", Arrays.asList(dtNode));
    Tree rbParent = tf.newTreeNode("ADVP", Arrays.asList(rbNode));

    Tree dtResult = params.transformTree(dtNode, dtParent);
    Tree rbResult = params.transformTree(rbNode, rbParent);

    assertTrue(dtResult.label().value().contains("^U"));
    assertTrue(rbResult.label().value().contains("^U"));
  }
@Test
  public void testTransformTreeHandlesRBReclassifiedToNNP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-correctTags", "-baseNP", "1" };
    params.setOptionFlag(args, 0);
    params.setOptionFlag(args, 1);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setWord("McNally");
    label.setTag("RB");
    label.setValue("RB");
    Tree leaf = tf.newLeaf(label);
    Tree preterminal = tf.newTreeNode("RB", Collections.singletonList(leaf));
    preterminal.setLabel(label);
    Tree parent = tf.newTreeNode("NP", Collections.singletonList(preterminal));
    Tree tree = tf.newTreeNode("S", Collections.singletonList(parent));
    Tree result = params.transformTree(preterminal, tree);
    assertTrue(result.label().value().startsWith("NNP"));
  }
@Test
  public void testTransformTreeSplitJJToNN() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-correctTags" };
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setWord("%");
    label.setTag("JJ");
    label.setValue("JJ");
    Tree leaf = tf.newLeaf(label);
    Tree jjNode = tf.newTreeNode("JJ", Collections.singletonList(leaf));
    Tree parent = tf.newTreeNode("ADJP", Collections.singletonList(jjNode));
    Tree result = params.transformTree(jjNode, parent);
    assertTrue(result.label().value().startsWith("NN"));
  }
@Test
  public void testTransformTreeSplitINWithGrandparentVP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-splitIN", "3" };
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();

    CoreLabel inLabel = new CoreLabel();
    inLabel.setWord("in");
    inLabel.setTag("IN");
    inLabel.setValue("IN");
    Tree inLeaf = tf.newLeaf(inLabel);

    Tree inNode = tf.newTreeNode("IN", Collections.singletonList(inLeaf));
    inNode.setLabel(inLabel);
    Tree pp = tf.newTreeNode("PP", Collections.singletonList(inNode));
    Tree vp = tf.newTreeNode("VP", Collections.singletonList(pp));
    Tree sentence = tf.newTreeNode("S", Collections.singletonList(vp));
    Tree transformedIN = params.transformTree(inNode, sentence);
    assertTrue(transformedIN.label().value().contains("IN"));
  }
@Test
  public void testSetOptionFlagCascadedPresets() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int r1 = params.setOptionFlag(new String[] { "-goodPCFG" }, 0);
    int r2 = params.setOptionFlag(new String[] { "-linguisticPCFG" }, 0);
    int r3 = params.setOptionFlag(new String[] { "-jenny" }, 0);
    int r4 = params.setOptionFlag(new String[] { "-ijcai03" }, 0);
    int r5 = params.setOptionFlag(new String[] { "-goodFactored" }, 0);
    assertEquals(1, r1);
    assertEquals(1, r2);
    assertEquals(1, r3);
    assertEquals(1, r4);
    assertEquals(1, r5);
  }
@Test
  public void testTransformTreeSplitAuxPOSAnnotation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-splitAux", "2" };
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setWord("'s");
    label.setTag("VBZ");
    label.setValue("VBZ");
    Tree leaf = tf.newLeaf(label);
    Tree verbNode = tf.newTreeNode("VBZ", Collections.singletonList(leaf));
    Tree vp = tf.newTreeNode("VP", Collections.singletonList(verbNode));
    Tree parent = tf.newTreeNode("ROOT", Collections.singletonList(vp));
    Tree transformed = params.transformTree(verbNode, parent);
    assertTrue(transformed.label().value().contains("BE") || transformed.label().value().contains("HV"));
  }
@Test
  public void testTransformTreeApplyJoinJJLogic() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-joinJJ" };
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setTag("JJR");
    label.setWord("better");
    label.setValue("JJR");
    Tree leaf = tf.newLeaf(label);
    Tree node = tf.newTreeNode("JJR", Collections.singletonList(leaf));
    Tree vp = tf.newTreeNode("VP", Collections.singletonList(node));
    Tree transformed = params.transformTree(node, vp);
    assertEquals("JJ", transformed.label().value());
  }
@Test
  public void testTransformTreeSplitRBModifier() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-splitRB" };
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel rbLabel = new CoreLabel();
    rbLabel.setValue("RB");
    rbLabel.setWord("very");
    rbLabel.setTag("RB");
    Tree leaf = tf.newLeaf(rbLabel);
    Tree pos = tf.newTreeNode("RB", Collections.singletonList(leaf));
    Tree adjp = tf.newTreeNode("ADJP", Collections.singletonList(pos));
    Tree parent = tf.newTreeNode("NP", Collections.singletonList(adjp));
    Tree result = params.transformTree(pos, parent);
    assertTrue(result.label().value().contains("^M"));
  }
@Test
  public void testTransformTreeSplitPercentAnnotated() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-splitPercent" };
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setWord("%");
    label.setTag("NN");
    label.setValue("NN");
    Tree leaf = tf.newLeaf(label);
    Tree node = tf.newTreeNode("NN", Collections.singletonList(leaf));
    Tree parent = tf.newTreeNode("NP", Collections.singletonList(node));
    Tree result = params.transformTree(node, parent);
    assertTrue(result.label().value().contains("%"));
  }
@Test
  public void testTransformTreeSplitCCAndAnnotation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-splitCC", "1" };
    params.setOptionFlag(args, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel ccAnd = new CoreLabel();
    ccAnd.setWord("and");
    ccAnd.setTag("CC");
    ccAnd.setValue("CC");
    Tree leaf = tf.newLeaf(ccAnd);
    Tree ccNode = tf.newTreeNode("CC", Collections.singletonList(leaf));
    Tree parent = tf.newTreeNode("NP", Collections.singletonList(ccNode));
    Tree result = params.transformTree(ccNode, parent);
    assertTrue(result.label().value().contains("-C"));
  }
@Test
  public void testSplitBaseNP_CollinsWithEmptyNPChildDoesNotCrash() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-baseNP", "2" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree emptyChild = tf.newTreeNode("NP", Collections.emptyList());
    Tree outerNP = tf.newTreeNode("NP", Arrays.asList(emptyChild));
    Tree top = tf.newTreeNode("ROOT", Arrays.asList(outerNP));

    Tree transformed = params.subcategoryStripper().transformTree(top);
    assertNotNull(transformed);
  }
@Test
  public void testSplitTMPTemporalAnnotationApplied() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-splitTMP", "1" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("NP-TMP");
    label.setTag("NNP");
    label.setWord("Monday");
    Tree leaf = tf.newLeaf(label);
    Tree tmpNode = tf.newTreeNode(label, Arrays.asList(leaf));
    Tree root = tf.newTreeNode("S", Arrays.asList(tmpNode));

    Tree transformed = params.transformTree(tmpNode, root);
    assertNotNull(transformed);
    assertTrue(transformed.label().value().startsWith("NP"));
  }
@Test
  public void testTransformTreeHandlesUnaryDTAndUnaryINTogether() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[] { "-unaryDT", "-unaryIN" };
    params.setOptionFlag(args, 0);
    params.setOptionFlag(args, 1);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel dt = new CoreLabel();
    dt.setValue("DT");
    dt.setTag("DT");
    dt.setWord("the");

    Tree dtLeaf = tf.newLeaf(dt);
    Tree dtNode = tf.newTreeNode("DT", Arrays.asList(dtLeaf));
    Tree unaryNP = tf.newTreeNode("NP", Arrays.asList(dtNode));

    CoreLabel in = new CoreLabel();
    in.setValue("IN");
    in.setTag("IN");
    in.setWord("on");

    Tree inLeaf = tf.newLeaf(in);
    Tree inNode = tf.newTreeNode("IN", Arrays.asList(inLeaf));
    Tree unaryPP = tf.newTreeNode("PP", Arrays.asList(inNode));

    Tree parent = tf.newTreeNode("S", Arrays.asList(unaryNP, unaryPP));

    Tree resultDT = params.transformTree(dtNode, parent);
    Tree resultIN = params.transformTree(inNode, parent);

    assertTrue(resultDT.label().value().contains("^U"));
    assertTrue(resultIN.label().value().contains("^U"));
  }
@Test
  public void testTransformTreeSplitCCOption3() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-splitCC", "3" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("CC");
    label.setTag("CC");
    label.setWord("and");

    Tree andLeaf = tf.newLeaf(label);
    Tree ccNode = tf.newTreeNode(label, Arrays.asList(andLeaf));
    Tree parent = tf.newTreeNode("NP", Arrays.asList(ccNode));
    Tree result = params.transformTree(ccNode, parent);
    assertTrue(result.label().value().contains("-A"));
  }
@Test
  public void testTransformTreeContainsDitransitiveVerbAnnotation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-markDitransV", "2" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("VB");
    label.setTag("VB");
    label.setWord("give");

    Tree leaf = tf.newLeaf(label);
    Tree verb = tf.newTreeNode("VB", Arrays.asList(leaf));

    Tree np1 = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("him")));
    Tree np2 = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("book")));

    Tree vp = tf.newTreeNode("VP", Arrays.asList(verb, np1, np2));
    Tree result = params.transformTree(verb, vp);
    assertTrue(result.label().value().contains("^2Arg"));
  }
@Test
  public void testSplitSbarInfOnlyAppliedWithoutPURP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-splitSbar", "2" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree to = tf.newTreeNode("TO", Arrays.asList(tf.newLeaf("to")));
    Tree vb = tf.newTreeNode("VB", Arrays.asList(tf.newLeaf("invest")));
    Tree s = tf.newTreeNode("S", Arrays.asList(to, vb));
    Tree sbar = tf.newTreeNode("SBAR", Arrays.asList(s));

    Tree result = params.transformTree(sbar, sbar);
    assertTrue(result.label().value().contains("INF"));
    assertFalse(result.label().value().contains("PURP"));
  }
@Test
  public void testTransformTreeCollapseWhCategoriesPOSTags() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-collapseWhCategories", "2" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("WDT");
    label.setTag("WDT");
    label.setWord("which");

    Tree leaf = tf.newLeaf(label);
    Tree wdtNode = tf.newTreeNode("WDT", Collections.singletonList(leaf));
    Tree parent = tf.newTreeNode("SBAR", Arrays.asList(wdtNode));

    Tree result = params.transformTree(wdtNode, parent);
    assertEquals("DT", result.label().value());
  }
@Test
  public void testTransformTreeMarkContainedVP() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-markContainedVP" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree vb = tf.newTreeNode("VB", Arrays.asList(tf.newLeaf("run")));
    Tree vpInner = tf.newTreeNode("VP", Arrays.asList(vb));
    Tree np = tf.newTreeNode("NP", Arrays.asList(vpInner));
    Tree root = tf.newTreeNode("S", Arrays.asList(np));
    Tree result = params.transformTree(np, root);
    assertTrue(result.label().value().contains("-vp"));
  }
@Test
  public void testTransformTreeSisterSplitLevelZeroReturnsEmpty() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-baseNP", "1" }; 
    String[] emptyArgs = { "-noAnnotations" }; 
    params.setOptionFlag(args, 0);
    params.setOptionFlag(emptyArgs, 0);
    String[] result = params.sisterSplitters();
    assertNotNull(result);
    assertEquals(0, result.length);
  }
@Test
  public void testDefaultTestSentenceInstanceValues() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> sentence = params.defaultTestSentence();
    assertEquals(6, sentence.size());
    assertEquals("just", sentence.get(2).word());
  }
@Test
  public void testCollapseWhCategoriesKeepsWHNPIfNotRequested() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("WHNP");
    label.setWord("who");
    label.setTag("WP");

    Tree leaf = tf.newLeaf(label);
    Tree whnp = tf.newTreeNode(label, Collections.singletonList(leaf));
    Tree root = tf.newTreeNode("S", Collections.singletonList(whnp));

    Tree result = params.transformTree(whnp, root);
    assertEquals("WHNP", result.label().value());
  }
@Test
  public void testSetOptionFlagValidCompositeSwitches() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int idx = params.setOptionFlag(new String[] { "-acl03pcfg" }, 0);
    assertEquals(1, idx);
  }
@Test
  public void testTransformTreeSplitPossRestructureAddsPOSSPNode() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int idx = params.setOptionFlag(new String[] { "-splitPoss", "2" }, 0);
    assertEquals(2, idx);

    TreeFactory tf = new LabeledScoredTreeFactory(new CategoryWordTagFactory());
    Tree child1 = tf.newTreeNode("NN", Arrays.asList(tf.newLeaf("John")));
    Tree child2 = tf.newTreeNode("POS", Arrays.asList(tf.newLeaf("'s")));
    Tree np = tf.newTreeNode("NP", Arrays.asList(child1, child2));
    Tree result = params.transformTree(np, np);
    assertEquals("POSSP", result.label().value());
    assertEquals(2, result.numChildren());
  }
@Test
  public void testTransformTreeHeadIsNullFallback() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree npChild = tf.newTreeNode("NP", Collections.singletonList(tf.newLeaf("child")));
    Tree root = tf.newTreeNode("ROOT", Collections.singletonList(npChild));
    Tree result = params.transformTree(npChild, root);
    assertNotNull(result);
  }
@Test
  public void testSplitVPNPAgrNPPluralTagSuffix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-splitVPNPAgr" }, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setWord("dogs");
    label.setTag("NNS");
    label.setValue("NP");
    Tree leaf = tf.newLeaf(label);
    Tree np = tf.newTreeNode(label, Arrays.asList(leaf));
    Tree parent = tf.newTreeNode("S", Arrays.asList(np));

    Tree result = params.transformTree(np, parent);
    assertTrue(result.label().value().endsWith("-PL"));
  }
@Test
  public void testSplitVPFiniteAndBaseTagDistinction() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-splitVP", "3" }, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel verb = new CoreLabel();
    verb.setWord("run");
    verb.setTag("VBZ");
    verb.setValue("VP");

    Tree leaf = tf.newLeaf(verb);
    Tree vp = tf.newTreeNode(verb, Arrays.asList(leaf));
    Tree result = params.transformTree(vp, vp);
    assertTrue(result.label().value().contains("-VBF"));
  }
@Test
  public void testTransformTreeSplitCCDetectionOfConjPhrase() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-markCC", "1" }, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree left = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("A")));
    Tree cc = tf.newTreeNode("CC", Arrays.asList(tf.newLeaf("and")));
    Tree right = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("B")));

    Tree phrase = tf.newTreeNode("NP", Arrays.asList(left, cc, right));
    Tree result = params.transformTree(phrase, phrase);
    assertTrue(result.label().value().contains("-CC"));
  }
@Test
  public void testSplitSTagOnlyAnnotationAppliedToSNotSINV() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-splitSTag", "4" }, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel verb = new CoreLabel();
    verb.setWord("ran");
    verb.setTag("VBD");
    verb.setValue("S");

    Tree leaf = tf.newLeaf(verb);
    Tree s = tf.newTreeNode(verb, Collections.singletonList(leaf));
    Tree root = tf.newTreeNode("ROOT", Collections.singletonList(s));

    Tree result = params.transformTree(s, root);
    assertTrue(result.label().value().contains("-VBF"));
  }
@Test
  public void testTransformTreeSplitSbarDetectPurpWithInOrder() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-splitSbar", "3" }, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree in = tf.newTreeNode("IN", Collections.singletonList(tf.newLeaf("in")));
    Tree order = tf.newTreeNode("NN", Collections.singletonList(tf.newLeaf("order")));
    Tree sbar = tf.newTreeNode("SBAR", Arrays.asList(in, order));
    Tree result = params.transformTree(sbar, sbar);
    assertTrue(result.label().value().contains("PURP"));
  }
@Test
  public void testSubcategoryStripperNullChildDoesNotCrash() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree parent = tf.newTreeNode("NP", Collections.singletonList(null));
    Tree result = params.subcategoryStripper().transformTree(parent);
    assertNull(result);
  }
@Test
  public void testTransformTreeSplitMoreLessMarksComparatives() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-splitMoreLess" }, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("JJR");
    label.setWord("more");
    label.setTag("JJR");

    Tree leaf = tf.newLeaf(label);
    Tree jjNode = tf.newTreeNode(label, Arrays.asList(leaf));
    Tree result = params.transformTree(jjNode, jjNode);
    assertTrue(result.label().value().contains("-ML"));
  }
@Test
  public void testTransformTreeSplitNPNNPType3MarksWithNNPChildAnywhere() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[] { "-splitNPNNP", "3" }, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree child = tf.newTreeNode("NNP", Arrays.asList(tf.newLeaf("Obama")));
    Tree np = tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("president"), child));
    Tree result = params.transformTree(np, np);
    assertTrue(result.label().value().contains("-NNP"));
  }
@Test
  public void testTransformTreeJoinNounTagsConvertsNNPToNN() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = { "-joinNounTags" };
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("NNP");
    label.setWord("Google");
    label.setTag("NNP");

    Tree leaf = tf.newLeaf(label);
    Tree node = tf.newTreeNode("NNP", Arrays.asList(leaf));
    Tree parent = tf.newTreeNode("NP", Collections.singletonList(node));
    Tree result = params.transformTree(node, parent);
    assertEquals("NN", result.label().value());
  }
@Test
  public void testSetOptionFlagWithEmptyArgsReturnsZero() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[0], 0);
    assertEquals(0, result);
  }
@Test
  public void testTransformTreeHandlesUnannotatedVPHeadUnknownPOS() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = new String[]{"-splitVP", "1"};
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel unknownHead = new CoreLabel();
    unknownHead.setValue("VP");
    unknownHead.setWord("unknown");
    unknownHead.setTag("XX");

    Tree headLeaf = tf.newLeaf(unknownHead);
    Tree vpNode = tf.newTreeNode(unknownHead, Arrays.asList(headLeaf));
    Tree parent = tf.newTreeNode("ROOT", Collections.singletonList(vpNode));
    Tree result = params.transformTree(vpNode, parent);
    assertTrue(result.label().value().startsWith("VP") && result.label().value().contains("XX"));
  }
@Test
  public void testSplitSGapped1MarksMissingSubject() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] args = {"-splitSGapped", "1"};
    params.setOptionFlag(args, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree v = tf.newTreeNode("VP", Collections.singletonList(tf.newLeaf("ate")));
    Tree s = tf.newTreeNode("S", Collections.singletonList(v));
    Tree root = tf.newTreeNode("ROOT", Collections.singletonList(s));
    Tree result = params.transformTree(s, root);
    assertTrue(result.label().value().contains("-G"));
  }
@Test
  public void testTreeCollinizerPreservesStructure() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreebankLanguagePack tlp = params.treebankLanguagePack();
    TreeTransformer transformer = (TreeTransformer) params.collinizer();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode("S", Arrays.asList(
      tf.newTreeNode("NP", Arrays.asList(tf.newLeaf("They"))),
      tf.newTreeNode("VP", Arrays.asList(tf.newLeaf("left")))
    ));
    Tree transformed = transformer.transformTree(tree);
    assertNotNull(transformed);
    assertEquals("S", transformed.label().value());
  }
@Test
  public void testSplitNPPRPAppendsPRONTag() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPPRP"}, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.setWord("he");
    label.setTag("PRP");

    Tree leaf = tf.newLeaf(label);
    Tree np = tf.newTreeNode(label, Collections.singletonList(leaf));
    Tree result = params.transformTree(np, np);
    assertTrue(result.label().value().endsWith("-PRON"));
  }
@Test
  public void testHeadFinderFallbackDoesNotThrowIfInvalidClassGiven() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[]{"-headFinder", "com.fake.NonExistent"}, 0);
    assertEquals(2, result);
    assertNotNull(params.headFinder());
  }
@Test
  public void testSplitSFPMarksExclamation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSFP"}, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel excl = new CoreLabel();
    excl.setValue(".");
    excl.setTag(".");
    excl.setWord("!");

    Tree leaf = tf.newLeaf(excl);
    Tree punct = tf.newTreeNode(".", Collections.singletonList(leaf));
    Tree parent = tf.newTreeNode("S", Collections.singletonList(punct));
    Tree result = params.transformTree(punct, parent);
    assertTrue(result.label().value().contains("-EXCL"));
  }
@Test
  public void testTransformTreeSplitNNP_Level2WithInitial() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNNP", "2"}, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree first = tf.newTreeNode("NNP", Collections.singletonList(tf.newLeaf("J.")));
    Tree second = tf.newTreeNode("NNP", Collections.singletonList(tf.newLeaf("Smith")));
    Tree parent = tf.newTreeNode("NP", Arrays.asList(first, second));
    Tree result1 = params.transformTree(first, parent);
    Tree result2 = params.transformTree(second, parent);
    assertTrue(result1.label().value().contains("-I") || result1.label().value().contains("-B"));
    assertTrue(result2.label().value().contains("-E") || result2.label().value().contains("-I"));
  }
@Test
  public void testSplitNPpercentWithPercentPhrase_Type2() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPpercent", "2"}, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree percentLeaf = tf.newLeaf("%");
    Tree np = tf.newTreeNode("NP", Arrays.asList(percentLeaf));
    Tree adjp = tf.newTreeNode("ADJP", Arrays.asList(np));
    Tree result = params.transformTree(adjp, adjp);
    assertTrue(result.label().value().contains("%"));
  } 
}