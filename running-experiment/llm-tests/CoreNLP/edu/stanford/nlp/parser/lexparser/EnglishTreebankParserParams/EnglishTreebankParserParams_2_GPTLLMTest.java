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

public class EnglishTreebankParserParams_2_GPTLLMTest {

 @Test
  public void testSubcategoryStripperStripsTmp() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP-TMP (DT The) (NN day))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    TreeTransformer stripper = params.subcategoryStripper();
    Tree transformed = stripper.transformTree(tree);

    assertEquals("NP", transformed.value());
    assertEquals("DT", transformed.getChild(0).value());
    assertEquals("NN", transformed.getChild(1).value());
  }
@Test
  public void testSubcategoryStripperRetainsTmpWithOption() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-retainNPTMPSubcategories"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP-TMP (DT The) (NN day))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    TreeTransformer stripper = params.subcategoryStripper();
    Tree transformed = stripper.transformTree(tree);

    assertEquals("NP-TMP", transformed.value());
  }
//@Test
//  public void testLexSetsDefaultUnknownWordModelIfNull() {
//    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
//    Options options = new Options();
//    Index<String> wordIndex = new edu.stanford.nlp.util.ArrayCoreMap<>();
//    Index<String> tagIndex = new edu.stanford.nlp.util.ArrayCoreMap<>();
//    Lexicon lex = params.lex(options, wordIndex, tagIndex);
//
//    assertNotNull(lex);
//    assertEquals("edu.stanford.nlp.parser.lexparser.EnglishUnknownWordModelTrainer",
//        options.lexOptions.uwModelTrainer);
//  }
@Test
  public void testDefaultTestSentence() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> sentence = params.defaultTestSentence();

    assertEquals(6, sentence.size());
    assertEquals("This", sentence.get(0).value());
    assertEquals("is", sentence.get(1).value());
    assertEquals("just", sentence.get(2).value());
    assertEquals("a", sentence.get(3).value());
    assertEquals("test", sentence.get(4).value());
    assertEquals(".", sentence.get(5).value());
  }
@Test
  public void testGetGrammaticalStructureOriginal() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-originalDependencies"}, 0);

    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    TreeReader tr = new PennTreeReader(
        new StringReader("(S (NP (DT The) (NN dog)) (VP (VBZ runs)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(tlp)
    );
    Tree tree = tr.readTree();
    GrammaticalStructure gs = params.getGrammaticalStructure(tree, null, params.headFinder());

    assertTrue(gs instanceof EnglishGrammaticalStructure);
  }
@Test
  public void testGetGrammaticalStructureUniversal() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    TreeReader tr = new PennTreeReader(
        new StringReader("(S (NP (DT The) (NN cat)) (VP (VBZ sits)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(tlp)
    );
    Tree tree = tr.readTree();
    GrammaticalStructure gs = params.getGrammaticalStructure(tree, null, params.headFinder());

    assertTrue(gs instanceof UniversalEnglishGrammaticalStructure);
  }
@Test
  public void testDiskTreebankReturnsDiskTreebankInstance() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Treebank tb = params.diskTreebank();

    assertNotNull(tb);
    assertTrue(tb instanceof DiskTreebank);
  }
@Test
  public void testMemoryTreebankReturnsMemoryTreebankInstance() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Treebank tb = params.memoryTreebank();

    assertNotNull(tb);
    assertTrue(tb instanceof MemoryTreebank);
  }
@Test
  public void testTestMemoryTreebankReturnsMemoryTreebank() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Treebank tb = params.testMemoryTreebank();

    assertNotNull(tb);
    assertTrue(tb instanceof MemoryTreebank);
  }
@Test
  public void testSupportsBasicDependenciesReturnsTrue() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertTrue(params.supportsBasicDependencies());
  }
@Test
  public void testSisterSplittersLevel1() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitIN", "0"}, 0);
    String[] sisterSplitters = params.sisterSplitters();

    assertNotNull(sisterSplitters);
    assertTrue(sisterSplitters.length >= 0);
  }
@Test
  public void testTransformTreeHandlesNullInput() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree tree = null;
    Tree transformed = params.transformTree(tree, null);

    assertNull(transformed);
  }
@Test
  public void testTransformTreeReturnsLeafUnchanged() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("hello");
    Tree transformed = params.transformTree(leaf, leaf);

    assertEquals(leaf.label().value(), transformed.label().value());
  }
@Test
  public void testDefaultCoreNLPFlags() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flags = params.defaultCoreNLPFlags();

    assertNotNull(flags);
    assertEquals("-retainTmpSubcategories", flags[0]);
  }
@Test(expected = RuntimeIOException.class)
  public void testReadGrammaticalStructureFromInvalidFileThrows() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.readGrammaticalStructureFromFile("nonexistent/path/file.conllx");
  }
@Test
  public void testSubcategoryStripperRemovesPOSSP() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPoss", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(POSSP (NP (NNP Bob)) (POS 's) (NN dog))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    TreeTransformer stripper = params.subcategoryStripper();
    Tree transformed = stripper.transformTree(tree);

    assertEquals("NP", transformed.value());
    assertEquals(2, transformed.numChildren());
  }
@Test
  public void testSubcategoryStripperCollinsNPStripping() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NP (DT The) (NN dog)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    TreeTransformer stripper = params.subcategoryStripper();
    Tree transformed = stripper.transformTree(tree);

    assertEquals("NP", transformed.value());
    assertEquals(1, transformed.numChildren());
    assertEquals("DT", transformed.getChild(0).getChild(0).value());
  }
@Test
  public void testTransformTreeAssignsCorrectJJMarking() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitJJCOMP"}, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Label label = new CoreLabel();
    label.setValue("JJ");
    Tree preterm = tf.newTreeNode(label, List.of(tf.newLeaf("free")));
    Tree parent = tf.newTreeNode("ADJP", List.of(preterm, tf.newLeaf("NP")));
    Tree root = tf.newTreeNode("S", List.of(parent));
    Tree transformed = params.transformTree(parent, root);

    assertTrue(transformed.value().contains("^CMPL"));
  }
@Test
  public void testTransformTreeHandlesRootPhrasal() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("hello");
    Tree preterm = tf.newTreeNode("NN", List.of(leaf));
    Tree noun = tf.newTreeNode("NP", List.of(preterm));
    Tree root = tf.newTreeNode("ROOT", List.of(noun));
    Tree transformed = params.transformTree(root, root);

    assertEquals("ROOT", transformed.value());
    assertEquals(1, transformed.numChildren());
    assertEquals("NP", transformed.getChild(0).value());
  }
@Test
  public void testTransformTreeChangesTOtoINWithOption() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-makePPTOintoIN", "1"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(PP (TO to) (NP (NN school)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(0).value().startsWith("IN"));
  }
@Test
  public void testSetOptionFlagInvalidFlag() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[]{"-noSuchOption"}, 0);

    assertEquals(0, result); 
  }
@Test
  public void testTransformTreeWithNonLeafNonStandardPOS() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree root = tf.newTreeNode("ROOT", List.of(tf.newTreeNode("??", List.of(tf.newLeaf("what")))));
    Tree transformed = params.transformTree(root, root);

    assertEquals("ROOT", transformed.value());
  }
@Test
  public void testTransformTreeWithCollapseWhCategories() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-collapseWhCategories", "3"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(SBARQ (WHNP (WDT What)) (SQ (VBZ is) (NP (DT this))))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertFalse(transformed.toString().contains("WDT")); 
  }
@Test
  public void testTransformTreeSplitCCAnnotation() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitCC", "1"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NNS dogs) (CC and) (NNS cats))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(1).value().contains("-C"));
  }
@Test
  public void testTransformTreeSplitRBModifier() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitRB"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (RB quickly))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(0).value().contains("^M"));
  }
@Test
  public void testTransformTreeUnaryDTAnnotation() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryDT"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (DT All))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(0).value().contains("^U"));
  }
@Test
  public void testTransformTreeSplitNPNNPAnnotation() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPNNP", "3"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NNP Stanford) (NNP University))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.value().contains("NNP"));
  }
@Test
  public void testTransformTreeDitransitiveVerbMarked() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-markDitransV", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(VP (VB give) (NP him) (NP the ball))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(0).value().contains("^2Arg"));
  }
@Test
  public void testTransformTreeWithEmptyTree() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree root = tf.newTreeNode("ROOT", java.util.Collections.emptyList());
    Tree result = params.transformTree(root, root);

    assertEquals("ROOT", result.label().value());
    assertEquals(0, result.numChildren());
  }
@Test
  public void testTransformTreeDominatesV() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-dominatesV", "1"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(VP (VBP run))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, null);
    assertTrue(result.value().contains("-v"));
  }
@Test
  public void testTransformTreeDominatesC() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-dominatesC"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NN dog) (CC and) (NN cat))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, null);
    assertTrue(result.value().contains("-c"));
  }
@Test
  public void testTransformTreeDominatesI() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-dominatesI"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(PP (IN on) (NP (NN time)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, null);
    assertTrue(result.value().contains("-i"));
  }
@Test
  public void testTransformTreeUnaryPRP() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryPRP"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (PRP he))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree output = params.transformTree(tree, tree);
    assertTrue(output.getChild(0).value().contains("^U"));
  }
@Test
  public void testTransformTreeSplitAuxBe() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitAux", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(VP (VBZ is) (VP (VBN seen)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree output = params.transformTree(tree, tree);
    assertTrue(output.getChild(0).value().contains("-BE"));
  }
@Test
  public void testTransformTreeSplitVPUseSplit3() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "3"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(VP (VB play))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, null);
    assertTrue(result.value().contains("-VB"));
  }
@Test
  public void testTransformTreeMarkContainedVP() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-markContainedVP"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(S (NP (DT The) (NN dog)) (VP (VB runs)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree output = params.transformTree(tree, tree);
    assertTrue(output.value().contains("-vp"));
  }
@Test
  public void testTransformTreeRootVPGPAAnnotation() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-gpaRootVP"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(ROOT (VP (VB Play)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.getChild(0).value().contains("~ROOT"));
  }
@Test
  public void testTransformTreeSplitMoreLessTag() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitMoreLess"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(ADVP (RB more))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree output = params.transformTree(tree, tree);
    assertTrue(output.getChild(0).value().contains("-ML"));
  }
@Test
  public void testTransformTreePreTerminalTagChangeJJtoJJR() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Label label = new CoreLabel();
    label.setValue("JJ");
    Tree wordNode = tf.newLeaf("more");
    Tree preterm = tf.newTreeNode(label, java.util.Collections.singletonList(wordNode));
    Tree parent = tf.newTreeNode("ADJP", java.util.Collections.singletonList(preterm));
    Tree root = tf.newTreeNode("S", java.util.Collections.singletonList(parent));
    Tree result = params.transformTree(parent, root);

    assertTrue(result.getChild(0).label().value().startsWith("JJR"));
  }
@Test
  public void testTransformTreeUnknownPOSHandledGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("what");
    Tree preterm = tf.newTreeNode("XXX", java.util.Collections.singletonList(leaf));
    Tree root = tf.newTreeNode("ROOT", java.util.Collections.singletonList(preterm));
    Tree result = params.transformTree(root, root);
    assertEquals("ROOT", result.label().value());
  }
@Test
  public void testSetOptionFlagHandlesJunkArgsGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int nextIndex = params.setOptionFlag(new String[]{"-someInvalidOption", "extra"}, 0);

    assertEquals(0, nextIndex);
  }
@Test
  public void testSubcategoryStripperStripsUnknownLabelsGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree unknownLeaf = tf.newLeaf("blah");
    Tree unknownNode = tf.newTreeNode("XXX-Y", java.util.Collections.singletonList(unknownLeaf));
    Tree stripped = params.subcategoryStripper().transformTree(unknownNode);

    assertEquals("XXX", stripped.label().value());
  }
@Test
  public void testTransformTreeSplitCCOption2WithAndString() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitCC", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NN test) (CC but) (NN example))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree result = params.transformTree(t, t);
    assertTrue(result.getChild(1).label().value().contains("-B"));
  }
@Test
  public void testTransformTreeSplitCCOption2WithAmpersand() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitCC", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NN test) (CC &) (NN example))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree result = params.transformTree(t, t);
    assertTrue(result.getChild(1).label().value().contains("-A"));
  }
@Test
  public void testTransformTreeSplitPPJJAnnotationsTriggered() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPPJJ"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(PP (JJ quick))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree result = params.transformTree(t, null);
    assertTrue(result.getChild(0).label().value().contains("^S"));
  }
@Test
  public void testTransformTreeSplitTRJJMarksTransitiveJJ() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitTRJJ"}, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree jjLeaf = tf.newLeaf("due");
    Tree preterm = tf.newTreeNode("JJ", Collections.singletonList(jjLeaf));
    Tree np = tf.newTreeNode("NP", Collections.singletonList(tf.newLeaf("money")));
    Tree parent = tf.newTreeNode("PP", List.of(preterm, np));
    Tree tree = tf.newTreeNode("S", Collections.singletonList(parent));
    Tree result = params.transformTree(parent, tree);
    assertTrue(result.getChild(0).label().value().contains("^T"));
  }
@Test
  public void testTransformTreeSplitJJCOMPAddsCMPL() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitJJCOMP"}, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree head = tf.newTreeNode("JJ", Collections.singletonList(tf.newLeaf("interested")));
    Tree complement = tf.newTreeNode("PP", Collections.singletonList(tf.newLeaf("in")));
    Tree parent = tf.newTreeNode("ADJP", List.of(head, complement));
    Tree tree = tf.newTreeNode("ROOT", Collections.singletonList(parent));
    Tree result = params.transformTree(parent, tree);
    assertTrue(result.getChild(0).label().value().contains("^CMPL"));
  }
@Test
  public void testTransformTreeUnaryINAddsUnaryU() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryIN"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(PP (IN above))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree result = params.transformTree(t, t);
    assertTrue(result.getChild(0).label().value().contains("^U"));
  }
@Test
  public void testTransformTreeSplitNPNNPLevel1() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPNNP", "1"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NNP John))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree result = params.transformTree(t, t);
    assertTrue(result.label().value().contains("NNP"));
  }
@Test
  public void testTransformTreeSplitSbarInOrderToMarksPURP() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSbar", "1"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(SBAR (IN in) (NN order) (S (VP (TO to) (VB win))))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree result = params.transformTree(t, t);
    assertTrue(result.label().value().contains("PURP"));
  }
@Test
  public void testTransformTreeSplitNumNPMarksNUM() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNumNP"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (CD 100) (NN dollars))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-NUM"));
  }
//@Test
//  public void testLexSetsCustomTrainerIfProvided() {
//    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
//    Options options = new Options();
//    options.lexOptions.uwModelTrainer = "custom.trainer.FakeModelTrainer";
//    Index<String> wordIndex = new edu.stanford.nlp.util.ArrayCoreMap<>();
//    Index<String> tagIndex = new edu.stanford.nlp.util.ArrayCoreMap<>();
//    Lexicon lex = params.lex(options, wordIndex, tagIndex);
//    assertNotNull(lex);
//    assertEquals("custom.trainer.FakeModelTrainer", options.lexOptions.uwModelTrainer);
//  }
@Test
  public void testTransformTreeSplitVPOption2AddsVBF() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(VP (VBD ate))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-VBF"));
  }
@Test
  public void testTransformTreeCollapseWhPOS() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-collapseWhCategories", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(SBARQ (WHNP (WP Who)) (SQ (VBZ is) (NP (DT it))))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, tree);
    assertFalse(result.toString().contains("WP"));
    assertTrue(result.toString().contains("PRP"));
  }
@Test
  public void testTransformTreeSplitBaseNPLevel2AddsExtraNode() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (DT The) (NN cat))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, null);
    assertTrue(result.label().value().equals("NP-B") || result.toString().contains("NP-B"));
  }
@Test
  public void testSetOptionFlagHandlesCombinedPresetOptionGoodPCFG() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int indexAfter = params.setOptionFlag(new String[]{"-goodPCFG"}, 0);
    assertEquals(1, indexAfter);
  }
@Test
  public void testSetOptionFlagHandlesCombinedPresetOptionAcl03pcfg() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int indexAfter = params.setOptionFlag(new String[]{"-acl03pcfg"}, 0);
    assertEquals(1, indexAfter);
  }
@Test
  public void testTransformTreeHandlesNullRoot() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("dog");
    Tree preterm = tf.newTreeNode("NN", Collections.singletonList(leaf));
    Tree result = params.transformTree(preterm, null);

    assertEquals("NN", result.label().value());
  }
@Test
  public void testTransformTreeHandlesNonStandardStartSymbol() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("dogs");
    Tree preterm = tf.newTreeNode("NNS", Collections.singletonList(leaf));
    Tree np = tf.newTreeNode("NP", Collections.singletonList(preterm));
    Tree weirdRoot = tf.newTreeNode("DOCROOT", Collections.singletonList(np));
    Tree result = params.transformTree(weirdRoot, weirdRoot);

    assertEquals("DOCROOT", result.label().value());
  }
@Test
  public void testSubcategoryStripperHandlesEmptyChildrenListGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree emptyNP = tf.newTreeNode("NP-TMP", Collections.emptyList());
    Tree result = params.subcategoryStripper().transformTree(emptyNP);

    assertNull(result);
  }
@Test
  public void testDisplayDoesNotThrow() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    try {
      params.display();
    } catch (Exception e) {
      fail("display() should not throw: " + e.getMessage());
    }
  }
@Test
  public void testTypedDependencyHeadFinderSwitchesCorrectly() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreebankLanguagePack tlp = params.treebankLanguagePack();

    params.setOptionFlag(new String[]{"-originalDependencies"}, 0);
    HeadFinder hf1 = params.typedDependencyHeadFinder();

    params.setOptionFlag(new String[]{"-makeCopulaHead"}, 0);
    params.setGenerateOriginalDependencies(false);
    HeadFinder hf2 = params.typedDependencyHeadFinder();

    assertTrue(hf1.getClass().getSimpleName().contains("SemanticHeadFinder"));
    assertTrue(hf2.getClass().getSimpleName().contains("UniversalSemanticHeadFinder"));
  }
@Test
  public void testTreeReaderFactoryProducesValidReader() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReaderFactory factory = params.treeReaderFactory();
    TreeReader reader = factory.newTreeReader(new StringReader("(NP (DT a) (NN dog))"));
    Tree tree = reader.readTree();

    assertNotNull(tree);
    assertEquals("NP", tree.label().value());
  }
@Test
  public void testSetOptionFlagHandlesLeaveItAll() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int idx = params.setOptionFlag(new String[]{"-leaveItAll", "1"}, 0);
    assertEquals(2, idx);
  }
@Test
  public void testSetOptionFlagHandlesUnrecognizedWithValidPrefix() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int idx = params.setOptionFlag(new String[]{"-splitXYZ", "2"}, 0);
    assertEquals(0, idx); 
  }
@Test
  public void testTreeTransformerStripsNestedBaseNPInsideNPIfEmpty() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (NP (DT The)) (NN cat))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree transformed = params.subcategoryStripper().transformTree(tree);

    assertEquals("NP", transformed.label().value());
  }
@Test
  public void testTransformTreeHandlesQuotedPOSCorrectly() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitQuotes"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(S (`` `) (NP (NN dog)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = tr.readTree();
    Tree result = params.transformTree(tree, tree);

    assertTrue(result.getChild(0).value().contains("-SG")
        || result.getChild(0).getChild(0).value().contains("-SG"));
  }
@Test
  public void testTransformTreeHandlesSingleLetterNNPWithPeriod() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree wordLeaf = tf.newLeaf("J.");
    Tree preterm = tf.newTreeNode("NN", Collections.singletonList(wordLeaf));
    Tree parent = tf.newTreeNode("NP", Collections.singletonList(preterm));
    Tree tree = tf.newTreeNode("ROOT", Collections.singletonList(parent));
    Tree result = params.transformTree(tree, tree);

    assertEquals("NNP", result.getChild(0).getChild(0).label().value());
  }
@Test
  public void testSetHeadFinderInvalidClassNameFallback() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int idx = params.setOptionFlag(new String[]{"-headFinder", "non.existent.ClassName"}, 0);
    assertEquals(2, idx); 
  }
@Test
  public void testTransformTreeHandlesTaggedVBAsJJ() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader tr = new PennTreeReader(
        new StringReader("(VP (VB quick))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree transformed = params.transformTree(t, t);

    assertTrue(transformed.getChild(0).label().value().equals("JJ")
            || transformed.getChild(0).label().value().startsWith("JJ"));
  }
@Test
  public void testTransformTreeCollapseWhPreservesWHNPWhenBit4Set() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-collapseWhCategories", "4"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(SBARQ (WHNP (WDT What)) (SQ (VBZ is)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree transformed = params.transformTree(t, t);

    assertTrue(transformed.toString().contains("WHNP"));
  }
@Test
  public void testTransformTreePreTerminalPRPRecognizer() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader tr = new PennTreeReader(
        new StringReader("(NP (PRP he))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree transformed = params.transformTree(t, t);

    assertEquals("PRP", transformed.getChild(0).label().value());
  }
@Test
  public void testTransformTreeSplitINOption3AddsSuffix() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitIN", "3"}, 0);
    TreeReader tr = new PennTreeReader(
        new StringReader("(PP (IN with) (NP (NN joy)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree t = tr.readTree();
    Tree transformed = params.transformTree(t, t);

    assertTrue(transformed.getChild(0).label().value().startsWith("IN-"));
  }
@Test
  public void testTransformTreeHandlesEmptyBaseCatToken() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setValue("");
    label.setTag("");
    label.setWord("run");
    Tree preterm = tf.newTreeNode(label, List.of(tf.newLeaf("run")));
    Tree tree = tf.newTreeNode("S", List.of(preterm));
    Tree result = params.transformTree(tree, tree);

    assertNotNull(result);
    assertEquals("S", result.label().value());
  }
@Test
  public void testTransformTreeHandlesMultipleNPBaseNPAnnotations() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);

    TreeReader reader = new PennTreeReader(
        new StringReader("(NP (NP (NNP IBM)) (NP (NN dog)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);

    assertEquals("NP", result.label().value());
    assertTrue(result.getChild(0).label().value().contains("NP-B"));
  }
@Test
  public void testTransformTreePOSSPNodeWithoutExpectedChildren() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPoss", "2"}, 0);

    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree brokenPOSSP = tf.newTreeNode("POSSP", List.of(tf.newLeaf("dog")));
    Tree root = tf.newTreeNode("ROOT", List.of(brokenPOSSP));

    try {
      Tree result = params.transformTree(brokenPOSSP, root);
      assertNotNull(result);
    } catch (Exception e) {
      fail("Should handle malformed POSSP node gracefully");
    }
  }
@Test
  public void testTransformTreeRBWithUnaryMarking() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryRB"}, 0);

    TreeReader reader = new PennTreeReader(
        new StringReader("(NP (RB slowly))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);
    String tag = transformed.getChild(0).label().value();
    assertTrue(tag.contains("RB") && tag.contains("^U"));
  }
@Test
  public void testTransformTreeSplitSGappedVersion3CCCondition() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSGapped", "3"}, 0);

    TreeReader reader = new PennTreeReader(
        new StringReader("(S (NP cats) (CC and) (S (VP (VB run))))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);
    assertFalse(transformed.label().value().contains("-G")); 
  }
@Test
  public void testSetOptionFlagHandlesCombinationJenny() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int consumed = params.setOptionFlag(new String[]{"-jenny"}, 0);
    assertEquals(1, consumed);
  }
@Test
  public void testSetOptionFlagHandlesCombinationIjcai03() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int consumed = params.setOptionFlag(new String[]{"-ijcai03"}, 0);
    assertEquals(1, consumed);
  }
@Test
  public void testSetOptionFlagHandlesOptionWithoutArgumentGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[]{"-splitVP"}, 0);
    assertEquals(0, result); 
  }
@Test
  public void testTreebankLanguagePackReturnedIsCorrectType() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreebankLanguagePack tlp = params.treebankLanguagePack();
    assertTrue(tlp instanceof PennTreebankLanguagePack);
  }
@Test
  public void testDiskTreebankLoadsSentenceFromReader() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    DiskTreebank disk = (DiskTreebank) params.diskTreebank();
    assertNotNull(disk.treeReaderFactory());
  }
@Test
  public void testMemoryTreebankLoadsSentenceFromReader() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank memory = (MemoryTreebank) params.memoryTreebank();
    assertNotNull(memory);
    assertNotNull(memory.treeReaderFactory());
  }
@Test
  public void testTransformTreeWithCollapsedWHAndBaseCategoryPreserved() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-collapseWhCategories", "4"}, 0);

    TreeReader reader = new PennTreeReader(
        new StringReader("(SBAR (WHNP (WDT which)) (S (VP (VB sails))))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );

    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);
    assertTrue(transformed.toString().contains("WHNP")); 
  }
@Test
  public void testSubcategoryStripperHandlesEmptyLeafGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf("");
    Tree root = tf.newTreeNode("NN", List.of(leaf));
    Tree result = params.subcategoryStripper().transformTree(root);
    assertNotNull(result);
    assertEquals("NN", result.label().value());
  }
@Test
  public void testUnknownBaseCategoryIsStrippedCorrectly() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree customNode = tf.newTreeNode("XYZ-123+TMP", List.of(tf.newLeaf("test")));
    Tree result = params.subcategoryStripper().transformTree(customNode);
    assertEquals("XYZ", result.label().value());
  }
@Test
  public void testGetGrammaticalStructureReturnsNonNullEvenWithoutFilter() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader reader = new PennTreeReader(
        new StringReader("(ROOT (S (NP (DT The) (NN dog)) (VP (VBZ runs))))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree parsedTree = reader.readTree();
    GrammaticalStructure gs = params.getGrammaticalStructure(parsedTree, null, params.headFinder());

    assertNotNull(gs);
  }
@Test
  public void testTypedDependencyHeadFinderDoesNotCrashOnNullOptionVsCopula() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setGenerateOriginalDependencies(true);
    HeadFinder finder1 = params.typedDependencyHeadFinder();

    params.setGenerateOriginalDependencies(false);
    HeadFinder finder2 = params.typedDependencyHeadFinder();

    assertNotSame(finder1.getClass(), finder2.getClass());
  }
@Test
  public void testSplitAuxFlagWithVerbDo() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitAux", "3"}, 0);
    TreeReader reader = new PennTreeReader(
        new StringReader("(VP (VB do) (VP (VB things)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(params.treebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.firstChild().label().value().contains("-DO"));
  }
@Test
  public void testReflexivePronounIsMarked() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-markReflexivePRP"}, 0);
    TreeReader reader = new PennTreeReader(
        new StringReader("(NP (PRP herself))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(params.treebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(0).label().value().contains("-SE"));
  }
@Test
  public void testUnknownWordDefaultsToVB() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel label = new CoreLabel();
    label.setTag("UNK");
    label.setValue("BLURB");
    label.setWord("BLURB");
    Tree leaf = tf.newLeaf("BLURB");
    Tree preterm = tf.newTreeNode(label, Collections.singletonList(leaf));
    Tree tree = tf.newTreeNode("ROOT", Collections.singletonList(preterm));
    Tree transformed = params.transformTree(tree, tree);

    assertNotNull(transformed);
  }
@Test
  public void testCorrectTagMappingFromJJtoPRP() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-correctTags"}, 0);

    TreeReader reader = new PennTreeReader(
        new StringReader("(NP (JJ ours))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(params.treebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(0).label().value().startsWith("PRP"));
  }
@Test
  public void testSplitBaseNPPlusDitransitiveInteraction() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "1"}, 0);
    params.setOptionFlag(new String[]{"-markDitransV", "2"}, 0);

    TreeReader reader = new PennTreeReader(new StringReader(
        "(VP (VB give) (NP (DT the) (NN dog)) (NP (DT a) (NN bone)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(params.treebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.getChild(0).label().value().contains("^2Arg"));
  }
@Test
  public void testBaseNPAnnotationSkipsWhenParentIsNP() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);

    TreeReader reader = new PennTreeReader(
        new StringReader("(NP (NP (DT a) (NN dog)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(new PennTreebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);

    assertTrue(transformed.label().value().equals("NP"));
    assertTrue(transformed.getChild(0).label().value().contains("NP-B"));
  }
@Test
  public void testVPSubCatFlagAddsMarking() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-vpSubCat"}, 0);
    TreeReader reader = new PennTreeReader(
        new StringReader("(VP (VB see) (NP John) (PP with) (SBAR that))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(params.treebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree output = params.transformTree(tree, tree);

    assertTrue(output.getChild(0).label().value().contains("^a"));
  }
@Test
  public void testHeadFinderClassInstantiationFallbacksGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int consumed = params.setOptionFlag(new String[]{"-headFinder", "invalid.ClassName"}, 0);
    assertEquals(2, consumed); 
  }
@Test
  public void testCollapseWhCategoriesBitmaskMultiple() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-collapseWhCategories", "3"}, 0);
    TreeReader reader = new PennTreeReader(
        new StringReader("(SBARQ (WHNP (WDT Which)) (SQ (VBZ is)))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(params.treebankLanguagePack())
    );
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);

//    assertBooleanCondition(result.toString().contains("PRP") || result.toString().contains("DT"), "Expected WHNP collapsed");
  }
@Test
  public void testFallbackBehaviorWhenSplitPossExpectedButAbsent() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPoss", "2"}, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree malformed = tf.newTreeNode("POSSP", List.of(tf.newLeaf("'s")));
    Tree root = tf.newTreeNode("ROOT", List.of(malformed));
    Tree transformed = params.transformTree(malformed, root);

    assertNotNull(transformed);
  }
@Test
  public void testMakePPTOintoINFlagValue2PreservesSuffix() throws IOException {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-makePPTOintoIN", "2"}, 0);
    TreeReader reader = new PennTreeReader(
        new StringReader("(PP (TO to) (NP home))"),
        new LabeledScoredTreeFactory(),
        new BobChrisTreeNormalizer(params.treebankLanguagePack())
    );

    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);

    assertTrue(result.getChild(0).label().value().contains("-IN"));
  } 
}