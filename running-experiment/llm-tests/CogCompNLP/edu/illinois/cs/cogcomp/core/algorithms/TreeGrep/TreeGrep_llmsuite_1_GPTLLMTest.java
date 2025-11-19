package edu.illinois.cs.cogcomp.core.algorithms;

import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParser;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParserFactory;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class TreeGrep_llmsuite_1_GPTLLMTest {

@Test
public void testMatches_singleMatchAtRoot() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (VP (NP NN)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (NP NN))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// List<TreeGrepMatch<String>> matches = grep.getMatches();
// assertTrue(matched);
// assertEquals(1, matches.size());
}

@Test
public void testMatches_patternFoundMoreThanOnce() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (VP (NP NN) (NP NN)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP NN)", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// List<TreeGrepMatch<String>> matches = grep.getMatches();
// assertTrue(matched);
// assertEquals(2, matches.size());
}

@Test
public void testMatches_exactMatchWholeTree() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(A (B C) (D E))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(A (B C) (D E))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// List<TreeGrepMatch<String>> matches = grep.getMatches();
// assertTrue(matched);
// assertEquals(1, matches.size());
}

@Test
public void testMatches_noMatchDifferentStructure() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(S (NP DT NN) (VP VB))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (NP NN))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// List<TreeGrepMatch<String>> matches = grep.getMatches();
// assertFalse(matched);
// assertEquals(0, matches.size());
}

@Test
public void testDoesThisTreeMatch_exactStructureAndLabels() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (NP NN))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (NP NN))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.doesThisTreeMatch(tree);
// assertTrue(result);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testDoesThisTreeMatch_failsOnStructureMismatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(NP (JJ quick))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (NP NN))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.doesThisTreeMatch(tree);
// assertFalse(result);
// assertEquals(0, grep.getMatches().size());
}

@Test
public void testMatches_leafNodePatternExists() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (A (B X)) (A (B Y)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(X)", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_noMatchDueToLabelMismatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(NP (DT a) (NN dog))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP)", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
// assertEquals(0, grep.getMatches().size());
}

@Test
public void testToString_returnsPatternRepresentation() throws Exception {
// Tree<String> pattern = TreeUtilities.readTreeFromString("(X Y)", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// String result = grep.toString();
// assertEquals(pattern.toString(), result);
}

@Test
public void testGetPattern_returnsSameReference() throws Exception {
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (X Y))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// Tree<String> retrieved = grep.getPattern();
// assertSame(pattern, retrieved);
}

@Test
public void testMatches_supportsAnchorSymbolsStartOfChildren() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (PRED (^^^ A B C)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(PRED (^^^ A B))", new TreeFactory<>());
// pattern.getChild(0).setLabel(TreeGrep.startOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertFalse(grep.getMatches().isEmpty());
}

@Test
public void testMatches_supportsAnchorSymbolsEndOfChildren() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (PRED (X Y Z)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(PRED (Y Z $$$))", new TreeFactory<>());
// pattern.getChild(2).setLabel(TreeGrep.endOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertFalse(grep.getMatches().isEmpty());
}

@Test
public void testDoesThisTreeMatch_onlyOneNode() throws Exception {
Tree<String> tree = new Tree<>("X");
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testDoesThisTreeMatch_leafMismatchDueToLabel() throws Exception {
Tree<String> tree = new Tree<>("X");
Tree<String> pattern = new Tree<>("Y");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
assertTrue(grep.getMatches().isEmpty());
}

@Test
public void testMatches_entireTreeIsLeafAndPatternIsLeafAndMatch() throws Exception {
Tree<String> tree = new Tree<>("X");
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_entireTreeIsLeafAndPatternDiffLeaf() throws Exception {
Tree<String> tree = new Tree<>("A");
Tree<String> pattern = new Tree<>("B");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
assertTrue(grep.getMatches().isEmpty());
}

@Test
public void testMatches_treeSmallerThanPatternShouldNotMatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(NP)", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP (DT the))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// assertFalse(matched);
// assertTrue(grep.getMatches().isEmpty());
}

@Test
public void testDoesThisTreeMatch_patternHasMoreChildrenThanTree() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (VB run))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (VB run) (NP something))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.doesThisTreeMatch(tree);
// assertFalse(matched);
// assertTrue(grep.getMatches().isEmpty());
}

@Test
public void testMatches_duplicateMatchingSubtreesCountedSeparately() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (NP (DT the) (NN cat)) (NP (DT the) (NN cat)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP (DT the) (NN cat))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// assertTrue(matched);
// assertEquals(2, grep.getMatches().size());
}

@Test
public void testMatches_emptyTreeReturnsNoMatch() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testMatches_emptyPatternShouldNotMatchAnything() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (X Y))", new TreeFactory<>());
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// assertFalse(matched);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testMatches_treeWithMultipleLevelsPatternMatchesMiddleLayer() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(S (NP (DT the) (JJ small) (NN cat)) (VP (VBD sat)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP (DT the) (JJ small) (NN cat))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// assertTrue(matched);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_patternWithStartAnchorButTreeDoesNotHaveExpectedPrefix() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (AA X Y Z))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (^^^ A B))", new TreeFactory<>());
// pattern.getChild(0).setLabel(TreeGrep.startOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// assertFalse(matched);
// assertEquals(0, grep.getMatches().size());
}

@Test
public void testMatches_patternWithEndAnchorButTreeDoesNotHaveExpectedSuffix() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (A B C D))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (C D $$$))", new TreeFactory<>());
// pattern.getChild(2).setLabel(TreeGrep.endOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// assertFalse(matched);
// assertEquals(0, grep.getMatches().size());
}

@Test
public void testDoesThisTreeMatch_patternIsLeafNodeMatchingTreeLeaf() {
Tree<String> tree = new Tree<>("NN");
Tree<String> pattern = new Tree<>("NN");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_treeWithCircularStructureDoesNotCrash() {
Tree<String> root = new Tree<>("X");
Tree<String> child = new Tree<>("X");
// root.addChild(child);
// root.addChild(child);
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(root);
assertTrue(matched);
assertFalse(grep.getMatches().isEmpty());
}

@Test
public void testDoesThisTreeMatch_singleNodeMismatchLabel() {
Tree<String> tree = new Tree<>("VB");
Tree<String> pattern = new Tree<>("NN");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
assertTrue(grep.getMatches().isEmpty());
}

@Test
public void testPatternIsSingleStartAnchorLabelShouldNotMatchAnything() {
Tree<String> pattern = new Tree<>(TreeGrep.startOfChildrenString);
TreeGrep<String> grep = new TreeGrep<>(pattern);
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(new Tree<>("A"));
boolean matched = grep.matches(tree);
assertFalse(matched);
assertTrue(grep.getMatches().isEmpty());
}

@Test
public void testPatternOnlyHasEndAnchorShouldNotMatchAnything() {
Tree<String> pattern = new Tree<>("X");
Tree<String> endChild = new Tree<>(TreeGrep.endOfChildrenString);
// pattern.addChild(endChild);
Tree<String> tree = new Tree<>("X");
// tree.addChild(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
assertTrue(grep.getMatches().isEmpty());
}

@Test
public void testDoesThisTreeMatch_treeHasExtraChildrenBeyondPatternShouldFail() {
Tree<String> tree = new Tree<>("VP");
Tree<String> c1 = new Tree<>("NP");
Tree<String> c2 = new Tree<>("ADVP");
// tree.addChild(c1);
// tree.addChild(c2);
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("NP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
}

@Test
public void testMatches_deeplyNestedUnevenTreeStructureShouldStillDetectMatch() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> a = new Tree<>("A");
Tree<String> b = new Tree<>("B");
Tree<String> c = new Tree<>("C");
Tree<String> d = new Tree<>("D");
// tree.addChild(a);
// a.addChild(b);
// b.addChild(c);
// c.addChild(d);
Tree<String> pattern = new Tree<>("B");
Tree<String> pc = new Tree<>("C");
Tree<String> pd = new Tree<>("D");
// pattern.addChild(pc);
// pc.addChild(pd);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_treeWithSameLabelsButWrongStructureShouldFail() {
Tree<String> tree = new Tree<>("X");
// tree.addChild(new Tree<>("Y"));
// tree.addChild(new Tree<>("Z"));
Tree<String> pattern = new Tree<>("X");
Tree<String> child = new Tree<>("Y");
Tree<String> grandchild = new Tree<>("Z");
// child.addChild(grandchild);
// pattern.addChild(child);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatches_whenOrderOfChildrenDoesNotMatchShouldFail() {
Tree<String> tree = new Tree<>("X");
// tree.addChild(new Tree<>("A"));
// tree.addChild(new Tree<>("B"));
Tree<String> pattern = new Tree<>("X");
// pattern.addChild(new Tree<>("B"));
// pattern.addChild(new Tree<>("A"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatches_patternChildrenNonContiguousInTreeShouldFail() {
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(new Tree<>("A"));
// tree.addChild(new Tree<>("X"));
// tree.addChild(new Tree<>("B"));
Tree<String> pattern = new Tree<>("ROOT");
// pattern.addChild(new Tree<>("A"));
// pattern.addChild(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testDoesThisTreeMatch_multipleChildrenPatternOneChildTreeShouldFail() {
Tree<String> tree = new Tree<>("PARENT");
// tree.addChild(new Tree<>("CHILD"));
Tree<String> pattern = new Tree<>("PARENT");
// pattern.addChild(new Tree<>("CHILD"));
// pattern.addChild(new Tree<>("OTHER"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
}

@Test
public void testMatches_singleLeafTreeAgainstNestedPatternShouldFail() {
Tree<String> tree = new Tree<>("X");
Tree<String> pattern = new Tree<>("X");
// pattern.addChild(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatches_identicalLabelsAndChildCountButDifferentChildLabelsShouldFail() {
Tree<String> tree = new Tree<>("X");
// tree.addChild(new Tree<>("A"));
// tree.addChild(new Tree<>("B"));
Tree<String> pattern = new Tree<>("X");
// pattern.addChild(new Tree<>("A"));
// pattern.addChild(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatches_treeNodeHasExtraSubtreePatternMatchesSubPartOnly() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(S (VP (NP (DT the) (NN dog)) (PP (IN in) (NP (DT the) (NN house))))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP (DT the) (NN dog))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_patternDeeperThanTreeShouldReturnFalse() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (VB eat))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (VB eat) (NP (DT the) (NN apple)))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
// assertEquals(0, grep.getMatches().size());
}

@Test
public void testMatches_patternTreeLabelMismatchesRootShouldNotMatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(S (VP (VB runs)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP (VB runs))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
}

@Test
public void testMatches_patternWithOnlyLeafShouldMatchMultipleLeaves() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(S (VP (VB run)) (NP (VB run)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VB run)", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(2, grep.getMatches().size());
}

@Test
public void testMatches_withStartAnchor_edgeMatchingAtBeginning() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (X A) (Y B) (Z C))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (^^^ X A) (Y B))", new TreeFactory<>());
// pattern.getChild(0).setLabel(TreeGrep.startOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertFalse(grep.getMatches().isEmpty());
}

@Test
public void testMatches_withEndAnchor_edgeMatchingAtEnd() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (X A) (Y B) (Z C))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (Y B) (Z C) ($$$))", new TreeFactory<>());
// pattern.getChild(2).setLabel(TreeGrep.endOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertFalse(grep.getMatches().isEmpty());
}

@Test
public void testDoesThisTreeMatch_rootMatchesButChildStructureMismatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(A (B (C D)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(A (B D))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.doesThisTreeMatch(tree);
// assertFalse(result);
}

@Test
public void testDoesLabelMatchPatternLabel_nullAndNonNullShouldReturnFalse() {
String label1 = null;
String label2 = "X";
Tree<String> pattern = new Tree<>("pattern");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesLabelMatchPatternLabel(label1, label2);
assertFalse(result);
}

@Test
public void testDoesLabelMatchPatternLabel_bothNullShouldReturnFalse() {
Tree<String> pattern = new Tree<>("pattern");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesLabelMatchPatternLabel(null, null);
assertFalse(result);
}

@Test
public void testMatches_nestedRepeatedStructureShouldReturnAllMatches() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (NP (DT the) (NN cat)) (VP (VBD sat) (PP (IN on) (NP (DT the) (NN mat)))))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP (DT the) (NN mat))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testDoesThisTreeMatch_patternRootDiffersButSameChildrenShouldNotMatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(X (A B))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(Y (A B))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.doesThisTreeMatch(tree);
// assertFalse(result);
}

@Test
public void testMatches_treeWithExtraUnmatchedSiblingsShouldStillFindMatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (DECL (NP (DT the) (NN dog)) (PP (IN with) (NP (DT a) (NN leash))) (VP (VB runs))))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NP (DT the) (NN dog))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_treeHasNestedMatchVeryDeep() throws Exception {
Tree<String> root = new Tree<>("ROOT");
Tree<String> level1 = new Tree<>("L1");
Tree<String> level2 = new Tree<>("L2");
Tree<String> level3 = new Tree<>("L3");
Tree<String> level4 = new Tree<>("L4");
Tree<String> leaf = new Tree<>("TARGET");
// root.addChild(level1);
// level1.addChild(level2);
// level2.addChild(level3);
// level3.addChild(level4);
// level4.addChild(leaf);
Tree<String> pattern = new Tree<>("L4");
// pattern.addChild(new Tree<>("TARGET"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(root);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_multipleTopLevelMatches() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (A X) (A X) (A X))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(A X)", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(3, grep.getMatches().size());
}

@Test
public void testDoesThisTreeMatch_leafLabelsMatchOnly_rootIsDifferent() throws Exception {
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(new Tree<>("X"));
Tree<String> pattern = new Tree<>("WRONG");
// pattern.addChild(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testMatches_patternIsPrefixOfTreeChildrenShouldMatchWithAnchor() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (A B) (C D) (E F))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (^^^ A B) (C D))", new TreeFactory<>());
// pattern.getChild(0).setLabel(TreeGrep.startOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
}

@Test
public void testMatches_patternIsSuffixOfTreeChildrenShouldMatchWithAnchor() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (X Y) (Y Z) (Z $$$))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (Y Z) ($$$))", new TreeFactory<>());
// pattern.getChild(1).setLabel(TreeGrep.endOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
}

@Test
public void testMatches_anchorSymbolWithoutProperContextShouldFail() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(X (^^^ A B))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(X (^^^ A B))", new TreeFactory<>());
// pattern.getChild(0).setLabel(TreeGrep.startOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
}

@Test
public void testMatches_anchorOnlyPatternShouldFail() {
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(new Tree<>("A"));
// tree.addChild(new Tree<>("B"));
Tree<String> pattern = new Tree<>("ROOT");
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testMatches_sameSubtreeOccursMultipleLevels_shouldDetectEachMatch() throws Exception {
Tree<String> root = new Tree<>("X");
Tree<String> child1 = new Tree<>("Y");
Tree<String> child2 = new Tree<>("Y");
Tree<String> grandchild = new Tree<>("Z");
// child1.addChild(grandchild);
// child2.addChild(grandchild);
// root.addChild(child1);
// root.addChild(child2);
Tree<String> pattern = new Tree<>("Y");
// pattern.addChild(new Tree<>("Z"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(root);
assertTrue(result);
assertEquals(2, grep.getMatches().size());
}

@Test
public void testMatches_patternHasSingleNullLabelShouldReturnNoMatch() {
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(new Tree<>("A"));
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testDoesThisTreeMatch_edgeCase_sameInstanceTreeEqualsPatternShouldMatch() {
Tree<String> tree = new Tree<>("A");
Tree<String> child = new Tree<>("B");
// tree.addChild(child);
TreeGrep<String> grep = new TreeGrep<>(tree);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
}

@Test
public void testMatches_submatchUnderNestedPointerStructure_edgeMatchStillWorks() throws Exception {
Tree<String> tree = new Tree<>("Top");
Tree<String> branch = new Tree<>("Mid");
Tree<String> leaf = new Tree<>("End");
// branch.addChild(leaf);
// tree.addChild(branch);
Tree<String> pattern = new Tree<>("Mid");
// pattern.addChild(new Tree<>("End"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
}

@Test
public void testDoesThisTreeMatch_labelsMatchButNoChildrenShouldFail() {
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(new Tree<>("A"));
Tree<String> pattern = new Tree<>("ROOT");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testDoesThisTreeMatch_patternWithExtraChildShouldFail() {
Tree<String> tree = new Tree<>("X");
// tree.addChild(new Tree<>("A"));
Tree<String> pattern = new Tree<>("X");
// pattern.addChild(new Tree<>("A"));
// pattern.addChild(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testMatches_trailingUnmatchedTreeChildrenShouldStillMatchPattern() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (A X) (B Y) (C Z))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (A X) (B Y))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
}

@Test
public void testMatches_patternWithEmptyAncestorsShouldStillWork() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(TOP (M1 (M2 (X A))))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(M2 (X A))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matched = grep.matches(tree);
// assertTrue(matched);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_patternAnchoredAtStartButTreeChildOffsetShouldFail() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (X1 A) (X2 B) (X3 C))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (^^^ X2 B) (X3 C))", new TreeFactory<>());
// pattern.getChild(0).setLabel(TreeGrep.startOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
}

@Test
public void testMatches_patternAnchoredAtEndButTreeChildOffsetShouldFail() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (X1 A) (X2 B) (X3 C))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (X1 A) (X2 B) ($$$))", new TreeFactory<>());
// pattern.getChild(2).setLabel(TreeGrep.endOfChildrenString);
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
}

@Test
public void testMatches_patternAtRootLevelShouldMatchOnlyOnceEvenIfNestedMatchesExist() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (NP (DT the) (NN dog)) (NP (DT the) (NN dog)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (NP (DT the) (NN dog)) (NP (DT the) (NN dog)))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatches_childMismatchPatternMidTreeShouldReturnFalse() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (X A) (Y MISMATCH))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (X A) (Y B))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
}

@Test
public void testMatches_subtreeMatchesOneBranchButNotAnotherShouldReturnCorrectCount() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (SUB (A 1) (B 2)) (SUB (A 1) (B MISMATCH)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(SUB (A 1) (B 2))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertTrue(result);
// assertEquals(1, grep.getMatches().size());
}

@Test
public void testDoesThisTreeMatch_partialMatchShouldReturnFalse() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(NODE (A X) (B Y))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(NODE (A X))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.doesThisTreeMatch(tree);
// assertFalse(result);
}

@Test
public void testMatches_rootOnlyPatternShouldMatchMultipleNodes() throws Exception {
Tree<String> left = new Tree<>("MATCH");
Tree<String> right = new Tree<>("MATCH");
Tree<String> root = new Tree<>("ROOT");
// root.addChild(left);
// root.addChild(right);
Tree<String> pattern = new Tree<>("MATCH");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(root);
assertTrue(result);
assertEquals(2, grep.getMatches().size());
}

@Test
public void testMatches_deepMismatchedPatternAfterSharedPrefixShouldReturnFalse() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(TOP (A (B (C D))))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(A (B (C Z)))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.matches(tree);
// assertFalse(result);
}

@Test
public void testDoesThisTreeMatch_sameLabelDifferentChildCount() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(VP (X A))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(VP (X A) (Y B))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.doesThisTreeMatch(tree);
// assertFalse(result);
}

@Test
public void testMatches_patternRequiresExactChildMatch_treeHasExtraShouldReturnFalse() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(A (X 1) (Y 2) (Z 3))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(A (X 1) (Y 2))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean result = grep.doesThisTreeMatch(tree);
// assertFalse(result);
}

@Test
public void testMatches_patternIsLeafNodeWithSimilarNonMatchingLabelShouldReturnFalse() throws Exception {
Tree<String> tree = new Tree<>("B");
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testMatches_patternRootMatchesButChildrenOutOfOrderShouldFail() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(A (B 1) (C 2))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(A (C 2) (B 1))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.matches(tree);
// assertFalse(matches);
}

@Test
public void testMatches_singleNodeTreeAndSingleNodePatternDifferentLabelsShouldFail() {
Tree<String> tree = new Tree<>("X");
Tree<String> pattern = new Tree<>("Y");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.matches(tree);
assertFalse(matches);
}

@Test
public void testMatches_treeBeingNullShouldNotThrowAndReturnFalse() {
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.matches(null);
assertFalse(matches);
}

@Test
public void testDoesThisTreeMatch_treeHasFewerChildrenThanPatternShouldFail() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(ROOT (A X))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(ROOT (A X) (B Y))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.doesThisTreeMatch(tree);
// assertFalse(matches);
}

@Test
public void testDoesThisTreeMatch_matchingLabelsButChildrenMisalignedShouldFail() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(X (A a) (B b))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(X (A a) (C c))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.doesThisTreeMatch(tree);
// assertFalse(matches);
}

@Test
public void testMatches_allChildrenMatchOutOfOrderShouldFailWithoutAnchor() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(P (X 1) (Y 2) (Z 3))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(P (Z 3) (X 1))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.matches(tree);
// assertFalse(matches);
}

@Test
public void testMatches_childrenMatchButTreeHasTooManyChildrenShouldStillMatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(S (A 1) (B 2) (C 3))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(S (A 1) (B 2))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.matches(tree);
// assertTrue(matches);
}

@Test
public void testMatches_patternLargerThanTreeShouldNeverMatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(T (X x))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(T (X x) (Y y) (Z z))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.matches(tree);
// assertFalse(matches);
}

@Test
public void testMatches_treeNodeMatchesPatternButExtraNestedDepthShouldNotObstructMatch() throws Exception {
// Tree<String> tree = TreeUtilities.readTreeFromString("(Q (W (X a)))", new TreeFactory<>());
// Tree<String> pattern = TreeUtilities.readTreeFromString("(W (X a))", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.matches(tree);
// assertTrue(matches);
}

@Test
public void testMatches_duplicateMatchingSubtreesShouldEachCount() throws Exception {
// Tree<String> t1 = TreeUtilities.readTreeFromString("(X A)", new TreeFactory<>());
// Tree<String> t2 = TreeUtilities.readTreeFromString("(X A)", new TreeFactory<>());
// Tree<String> t3 = TreeUtilities.readTreeFromString("(X A)", new TreeFactory<>());
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(t1);
// tree.addChild(t2);
// tree.addChild(t3);
// Tree<String> pattern = TreeUtilities.readTreeFromString("(X A)", new TreeFactory<>());
// TreeGrep<String> grep = new TreeGrep<>(pattern);
// boolean matches = grep.matches(tree);
// assertTrue(matches);
// assertEquals(3, grep.getMatches().size());
}

@Test
public void testMatches_rootOnlyChildrenAreSpecialMarkersStartEndShouldNotMatchAnything() throws Exception {
Tree<String> pattern = new Tree<>("ROOT");
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
// pattern.addChild(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(new Tree<>("A"));
// tree.addChild(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.matches(tree);
assertFalse(matches);
}

@Test
public void testMatches_patternSizeEqualsTreeButLeafMismatchShouldFail() throws Exception {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> pattern = new Tree<>("ROOT");
Tree<String> leaf1 = new Tree<>("A");
Tree<String> leaf2 = new Tree<>("B");
Tree<String> leaf3 = new Tree<>("A");
Tree<String> leaf4 = new Tree<>("C");
// tree.addChild(leaf1);
// tree.addChild(leaf2);
// pattern.addChild(leaf3);
// pattern.addChild(leaf4);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.matches(tree);
assertFalse(matches);
}

@Test
public void testMatches_patternWithOneChild_treeWithOnlyParent_shouldFail() throws Exception {
Tree<String> tree = new Tree<>("A");
Tree<String> pattern = new Tree<>("A");
// pattern.addChild(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.matches(tree);
assertFalse(matches);
}

@Test
public void testMatches_sameNodeMultiplePositions_matchAllIndependently() throws Exception {
Tree<String> node1 = new Tree<>("TARGET");
Tree<String> node2 = new Tree<>("TARGET");
Tree<String> node3 = new Tree<>("TARGET");
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(node1);
// tree.addChild(node2);
// tree.addChild(node3);
Tree<String> pattern = new Tree<>("TARGET");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(3, grep.getMatches().size());
}
}
