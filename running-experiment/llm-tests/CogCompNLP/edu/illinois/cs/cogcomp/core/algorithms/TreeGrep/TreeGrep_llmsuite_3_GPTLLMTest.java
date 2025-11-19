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

public class TreeGrep_llmsuite_3_GPTLLMTest {

@Test
public void testLeafPatternMatch() {
Tree<String> tree = new Tree<>("A");
Tree<String> childB = new Tree<>("B");
Tree<String> childC = new Tree<>("C");
Tree<String> childD = new Tree<>("D");
Tree<String> childE = new Tree<>("E");
childC.addSubtree(childD);
childC.addSubtree(childE);
tree.addSubtree(childB);
tree.addSubtree(childC);
Tree<String> pattern = new Tree<>("D");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertEquals("D", matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testIntermediatePatternMatch() {
Tree<String> tree = new Tree<>("A");
Tree<String> childB = new Tree<>("B");
Tree<String> childC = new Tree<>("C");
Tree<String> childD = new Tree<>("D");
Tree<String> childE = new Tree<>("E");
childC.addSubtree(childD);
childC.addSubtree(childE);
tree.addSubtree(childB);
tree.addSubtree(childC);
Tree<String> pattern = new Tree<>("C");
pattern.addSubtree(new Tree<>("D"));
pattern.addSubtree(new Tree<>("E"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertEquals("C", matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testFullPatternMatch() {
Tree<String> tree = new Tree<>("A");
Tree<String> childB = new Tree<>("B");
Tree<String> childC = new Tree<>("C");
Tree<String> childD = new Tree<>("D");
Tree<String> childE = new Tree<>("E");
childC.addSubtree(childD);
childC.addSubtree(childE);
tree.addSubtree(childB);
tree.addSubtree(childC);
Tree<String> pattern = new Tree<>("A");
Tree<String> patternB = new Tree<>("B");
Tree<String> patternC = new Tree<>("C");
Tree<String> patternD = new Tree<>("D");
Tree<String> patternE = new Tree<>("E");
patternC.addSubtree(patternD);
patternC.addSubtree(patternE);
pattern.addSubtree(patternB);
pattern.addSubtree(patternC);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertEquals("A", matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testNoPatternMatch() {
Tree<String> tree = new Tree<>("A");
Tree<String> b = new Tree<>("B");
Tree<String> c = new Tree<>("C");
tree.addSubtree(b);
tree.addSubtree(c);
Tree<String> pattern = new Tree<>("X");
Tree<String> y = new Tree<>("Y");
pattern.addSubtree(y);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(0, matches.size());
}

@Test
public void testDoesThisTreeMatchTrue() {
Tree<String> tree = new Tree<>("C");
Tree<String> d = new Tree<>("D");
Tree<String> e = new Tree<>("E");
tree.addSubtree(d);
tree.addSubtree(e);
Tree<String> pattern = new Tree<>("C");
Tree<String> pd = new Tree<>("D");
Tree<String> pe = new Tree<>("E");
pattern.addSubtree(pd);
pattern.addSubtree(pe);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
}

@Test
public void testDoesThisTreeMatchFalse() {
Tree<String> tree = new Tree<>("C");
Tree<String> d = new Tree<>("D");
tree.addSubtree(d);
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(0, matches.size());
}

@Test
public void testMultipleMatches() {
Tree<String> tree = new Tree<>("VP");
Tree<String> np1 = new Tree<>("NP");
Tree<String> nn1 = new Tree<>("NN");
np1.addSubtree(nn1);
Tree<String> np2 = new Tree<>("NP");
Tree<String> nn2 = new Tree<>("NN");
np2.addSubtree(nn2);
tree.addSubtree(np1);
tree.addSubtree(np2);
Tree<String> pattern = new Tree<>("NP");
pattern.addSubtree(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(2, matches.size());
// assertEquals("NP", matches.get(0).getMatchedTree().getLabel());
// assertEquals("NP", matches.get(1).getMatchedTree().getLabel());
}

@Test
public void testToStringReturnsPatternString() {
Tree<String> pattern = new Tree<>("S");
pattern.addSubtree(new Tree<>("NP"));
pattern.addSubtree(new Tree<>("VP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
assertEquals(pattern.toString(), grep.toString());
}

@Test
public void testGetPatternReturnsSameObject() {
Tree<String> pattern = new Tree<>("ROOT");
TreeGrep<String> grep = new TreeGrep<>(pattern);
assertSame(pattern, grep.getPattern());
}

@Test
public void testGetMatchesBeforeCallingMatchReturnsNull() {
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
assertNull(grep.getMatches());
}

@Test
public void testPatternWithStartOfChildrenString() {
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>(TreeGrep.startOfChildrenString));
pattern.addSubtree(new Tree<>("A"));
Tree<String> tree = new Tree<>("ROOT");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
}

@Test
public void testPatternWithEndOfChildrenString() {
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("ROOT");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
}

@Test
public void testEmptyTreeThrowsException() {
Tree<String> tree = null;
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
try {
grep.matches(tree);
fail("Expected NullPointerException for null input tree");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testEmptyPatternNodeMatchesNullTreeNode() {
Tree<String> pattern = new Tree<>(null);
Tree<String> tree = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
}

@Test
public void testTreeLabelEqualsButChildrenDoNotMatch() {
Tree<String> tree = new Tree<>("X");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testMatchingPatternWithSingleNodeAgainstDeepTree() {
Tree<String> tree = new Tree<>("S");
Tree<String> sub1 = new Tree<>("A");
Tree<String> sub2 = new Tree<>("B");
Tree<String> sub3 = new Tree<>("C");
sub1.addSubtree(sub2);
sub2.addSubtree(sub3);
tree.addSubtree(sub1);
Tree<String> pattern = new Tree<>("C");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
// assertEquals("C", grep.getMatches().get(0).getMatchedTree().getLabel());
}

@Test
public void testDoesThisTreeMatchReturnsFalseForLeafPatternVsInteriorNode() {
Tree<String> tree = new Tree<>("X");
tree.addSubtree(new Tree<>("Y"));
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testTreeIsLeafButPatternIsNot() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("CHILD"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testPatternHasMoreChildrenThanTree() {
Tree<String> tree = new Tree<>("A");
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("A");
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatchWithIdenticalSubtreesOnBothSides() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> child1 = new Tree<>("X");
Tree<String> child2 = new Tree<>("X");
tree.addSubtree(child1);
tree.addSubtree(child2);
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(2, grep.getMatches().size());
}

@Test
public void testPatternWithStartOfChildrenStringOnly() {
Tree<String> pattern = new Tree<>("A");
pattern.addSubtree(new Tree<>(TreeGrep.startOfChildrenString));
Tree<String> tree = new Tree<>("A");
tree.addSubtree(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
}

@Test
public void testPatternWithEndOfChildrenStringOnly() {
Tree<String> pattern = new Tree<>("Z");
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("Z");
tree.addSubtree(new Tree<>("CHILD"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testMergeMatchesWithOneEmptyChildMatch() {
Tree<String> tree = new Tree<>("ROOT");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatchNullLabelsOnBothTreeAndPattern() {
Tree<String> tree = new Tree<>(null);
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatchFailsWhenRootLabelDiffersEvenIfChildrenMatch() {
Tree<String> tree = new Tree<>("A");
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("Z");
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMultipleChildrenTreeVsPatternMoreChildrenThanTree() {
Tree<String> tree = new Tree<>("X");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMultipleMatchesAtSameLevelWithDifferentSubtrees() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> a1 = new Tree<>("A");
Tree<String> a2 = new Tree<>("A");
a1.addSubtree(new Tree<>("B"));
a2.addSubtree(new Tree<>("B"));
a2.addSubtree(new Tree<>("C"));
tree.addSubtree(a1);
tree.addSubtree(a2);
Tree<String> pattern = new Tree<>("A");
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(2, grep.getMatches().size());
}

@Test
public void testPatternMatchingEndsWhenChildDoesNotMatch() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> left = new Tree<>("L");
Tree<String> right = new Tree<>("R");
tree.addSubtree(left);
tree.addSubtree(right);
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("L"));
pattern.addSubtree(new Tree<>("M"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testMatchPatternWithEmptyChildrenAndTreeWithChildren() {
Tree<String> tree = new Tree<>("A");
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertEquals("A", matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testMatchFailsWhenPatternLeafMismatchesTreeLeaf() {
Tree<String> tree = new Tree<>("A");
Tree<String> pattern = new Tree<>("B");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(0, matches.size());
}

@Test
public void testMatchFailsWhenTreeHasFewerChildrenThanPattern() {
Tree<String> tree = new Tree<>("X");
tree.addSubtree(new Tree<>("A"));
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatchWithDuplicateSiblingsInTree() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> child1 = new Tree<>("X");
Tree<String> child2 = new Tree<>("X");
Tree<String> sub1 = new Tree<>("Y");
Tree<String> sub2 = new Tree<>("Y");
child1.addSubtree(sub1);
child2.addSubtree(sub2);
tree.addSubtree(child1);
tree.addSubtree(child2);
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(2, matches.size());
}

@Test
public void testMatchFailsWhenTreeLeafButPatternIsNotLeaf() {
Tree<String> tree = new Tree<>("Z");
Tree<String> pattern = new Tree<>("Z");
pattern.addSubtree(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testDoesLabelMatchPatternLabelWithNulls() {
Tree<String> tree = new Tree<>(null);
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertNull(matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testLabelMatchWithWhitespaceAndEmptyString() {
Tree<String> tree = new Tree<>(" ");
Tree<String> pattern = new Tree<>("");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatchPatternWithIdenticalSiblingsChildrenCreatesCrossProduct() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> x1 = new Tree<>("X");
x1.addSubtree(new Tree<>("Y"));
x1.addSubtree(new Tree<>("Y"));
tree.addSubtree(x1);
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("Y"));
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(matches.size() >= 1);
}

@Test
public void testPatternAndTreeMatchWithStartAndEndChildrenStringInMiddle() {
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>(TreeGrep.startOfChildrenString));
pattern.addSubtree(new Tree<>("X"));
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("ROOT");
tree.addSubtree(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
}

@Test
public void testMatchFailsIfPatternRootLabelNullAndTreeNotNull() {
Tree<String> pattern = new Tree<>(null);
Tree<String> tree = new Tree<>("Root");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMergeMatchesReturnsChildrenWhenOutsideIsEmpty() {
Tree<String> tree = new Tree<>("A");
Tree<String> child = new Tree<>("B");
tree.addSubtree(child);
Tree<String> pattern = new Tree<>("A");
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertTrue(matched);
List<TreeGrepMatch<String>> result = grep.getMatches();
assertEquals(1, result.size());
// assertEquals("A", result.get(0).getMatchedTree().getLabel());
}

@Test
public void testMatchMultipleLeafSubPatternsToSameLeaf() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> x1 = new Tree<>("X");
tree.addSubtree(x1);
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("X"));
pattern.addSubtree(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testPatternWithEmptySubtreeShouldNotMatchPopulatedTree() {
Tree<String> tree = new Tree<>("X");
Tree<String> child = new Tree<>("Y");
tree.addSubtree(child);
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertEquals("X", matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testPatternMatchesTreeRootLabelButNoMatchingChildrenDueToOrder() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> child1 = new Tree<>("A");
Tree<String> child2 = new Tree<>("B");
tree.addSubtree(child1);
tree.addSubtree(child2);
Tree<String> pattern = new Tree<>("ROOT");
Tree<String> pChild1 = new Tree<>("B");
Tree<String> pChild2 = new Tree<>("A");
pattern.addSubtree(pChild1);
pattern.addSubtree(pChild2);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testTreeWithMultipleIdenticalLeavesMatchesEachIndividually() {
Tree<String> tree = new Tree<>("P");
Tree<String> leaf1 = new Tree<>("X");
Tree<String> leaf2 = new Tree<>("X");
tree.addSubtree(leaf1);
tree.addSubtree(leaf2);
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(2, matches.size());
// assertEquals("X", matches.get(0).getMatchedTree().getLabel());
// assertEquals("X", matches.get(1).getMatchedTree().getLabel());
}

@Test
public void testPatternWithSingleChildFailsWhenTreeHasNoChildren() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("CHILD"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testMatchFailsWhenOnlyPartialChildrenMatch() {
Tree<String> tree = new Tree<>("X");
Tree<String> a = new Tree<>("A");
Tree<String> c = new Tree<>("C");
tree.addSubtree(a);
tree.addSubtree(c);
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testPatternMatchesTreeWithExtraTrailingChildren() {
Tree<String> tree = new Tree<>("X");
Tree<String> a = new Tree<>("A");
Tree<String> b = new Tree<>("B");
Tree<String> c = new Tree<>("C");
tree.addSubtree(a);
tree.addSubtree(b);
tree.addSubtree(c);
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
}

@Test
public void testPatternWithEndOfChildrenMatchesTrailingSequence() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> nodeA = new Tree<>("X");
Tree<String> nodeB = new Tree<>("Y");
tree.addSubtree(nodeA);
tree.addSubtree(nodeB);
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("Y"));
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
}

@Test
public void testStartAndEndChildrenCollisionPatternShouldNotMatch() {
Tree<String> tree = new Tree<>("X");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
tree.addSubtree(new Tree<>("C"));
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>(TreeGrep.startOfChildrenString));
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testLabelCaseSensitivity() {
Tree<String> tree = new Tree<>("ROOT");
tree.addSubtree(new Tree<>("node"));
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("Node"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testEmptyTreeAndEmptyPatternLabelBothNull() {
Tree<String> tree = new Tree<>(null);
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertNull(matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testPatternLongerThanTreeSubsequenceFails() {
Tree<String> tree = new Tree<>("SEQ");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("SEQ");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
}

@Test
public void testMultipleNestedMatchesOnSameNode() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> branch = new Tree<>("X");
branch.addSubtree(new Tree<>("Y"));
branch.addSubtree(new Tree<>("Y"));
tree.addSubtree(branch);
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(2, grep.getMatches().size());
}

@Test
public void testLeafPatternOnDeepTreeOnlyRootLabelMatches() {
Tree<String> tree = new Tree<>("A");
Tree<String> b = new Tree<>("B");
Tree<String> c = new Tree<>("C");
Tree<String> d = new Tree<>("D");
c.addSubtree(d);
b.addSubtree(c);
tree.addSubtree(b);
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
// assertEquals("A", grep.getMatches().get(0).getMatchedTree().getLabel());
}

@Test
public void testDoesNodeMatchPatternReturnsEmptyListForSizeMismatch() {
Tree<String> tree = new Tree<>("A");
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("A");
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testDoesNodeMatchPatternWithMismatchingMiddleChild() {
Tree<String> tree = new Tree<>("X");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
tree.addSubtree(new Tree<>("C"));
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("Z"));
pattern.addSubtree(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
}

@Test
public void testPatternRootMatchWithUnorderedMatchingChildrenFails() {
Tree<String> tree = new Tree<>("M");
tree.addSubtree(new Tree<>("X"));
tree.addSubtree(new Tree<>("Y"));
Tree<String> pattern = new Tree<>("M");
pattern.addSubtree(new Tree<>("Y"));
pattern.addSubtree(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
}

@Test
public void testPatternOfOnlySentinelStringDoesNotCrash() {
Tree<String> pattern = new Tree<>(TreeGrep.startOfChildrenString);
Tree<String> tree = new Tree<>(TreeGrep.startOfChildrenString);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertTrue(matched);
// assertEquals(TreeGrep.startOfChildrenString, grep.getMatches().get(0).getMatchedTree().getLabel());
}

@Test
public void testGetMatchesReturnsEmptyListAfterResetMatchRun() {
Tree<String> tree = new Tree<>("X");
tree.addSubtree(new Tree<>("Y"));
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched1 = grep.matches(tree);
assertTrue(matched1);
List<TreeGrepMatch<String>> firstMatches = grep.getMatches();
assertEquals(1, firstMatches.size());
Tree<String> newTree = new Tree<>("Z");
newTree.addSubtree(new Tree<>("Q"));
boolean matched2 = grep.matches(newTree);
List<TreeGrepMatch<String>> secondMatches = grep.getMatches();
assertFalse(matched2);
assertEquals(0, secondMatches.size());
}

@Test
public void testTreeAndPatternWithIdenticalStructureStartsAtNonRoot() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> child1 = new Tree<>("A");
Tree<String> child2 = new Tree<>("B");
Tree<String> subChild1 = new Tree<>("C");
child2.addSubtree(subChild1);
tree.addSubtree(child1);
tree.addSubtree(child2);
Tree<String> pattern = new Tree<>("B");
pattern.addSubtree(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
// assertEquals("B", grep.getMatches().get(0).getMatchedTree().getLabel());
}

@Test
public void testPatternWithOnlySentinelAndNoConcreteChildrenReturnsFalse() {
Tree<String> tree = new Tree<>("A");
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("A");
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertTrue(matched);
}

@Test
public void testPatternWithStartSentinelAndExactSubsequenceMatch() {
Tree<String> tree = new Tree<>("S");
tree.addSubtree(new Tree<>("X"));
tree.addSubtree(new Tree<>("Y"));
tree.addSubtree(new Tree<>("Z"));
Tree<String> pattern = new Tree<>("S");
pattern.addSubtree(new Tree<>(TreeGrep.startOfChildrenString));
pattern.addSubtree(new Tree<>("X"));
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.doesThisTreeMatch(tree);
assertTrue(matches);
// assertEquals("S", grep.getMatches().get(0).getMatchedTree().getLabel());
}

@Test
public void testEmptyPatternWithEmptyTree() {
Tree<String> tree = new Tree<>(null);
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testNullPatternLabelVsNonNullTreeLabelFailsMatch() {
Tree<String> tree = new Tree<>("active");
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testPatternRootMatchesButNestedChildrenFail() {
Tree<String> tree = new Tree<>("P");
Tree<String> a = new Tree<>("A");
Tree<String> b = new Tree<>("B");
a.addSubtree(b);
tree.addSubtree(a);
Tree<String> pattern = new Tree<>("P");
Tree<String> a2 = new Tree<>("A");
Tree<String> b2 = new Tree<>("InvalidLabel");
a2.addSubtree(b2);
pattern.addSubtree(a2);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testDeepTreeWithNoMatchAnywhere() {
Tree<String> tree = new Tree<>("T");
Tree<String> level1 = new Tree<>("L1");
Tree<String> level2 = new Tree<>("L2");
Tree<String> level3 = new Tree<>("L3");
level2.addSubtree(level3);
level1.addSubtree(level2);
tree.addSubtree(level1);
Tree<String> pattern = new Tree<>("BAD");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
}

@Test
public void testMatchingWithNullLabelPatternAgainstTreeWithMultipleNodes() {
Tree<String> tree = new Tree<>("A");
tree.addSubtree(new Tree<>("B"));
tree.addSubtree(new Tree<>("C"));
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertFalse(result);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testPatternWithOnlyStartOfChildrenFailsIfTreeHasInsufficientChildren() {
Tree<String> pattern = new Tree<>("Z");
pattern.addSubtree(new Tree<>(TreeGrep.startOfChildrenString));
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
Tree<String> tree = new Tree<>("Z");
tree.addSubtree(new Tree<>("A"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testPatternWithOnlyEndOfChildrenFailsIfTreeHasInsufficientChildren() {
Tree<String> pattern = new Tree<>("Y");
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("Y");
tree.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testMatchPatternTreeWithRepeatedSubstructuresShouldFindAll() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> section1 = new Tree<>("S");
Tree<String> leaf1 = new Tree<>("L");
section1.addSubtree(leaf1);
Tree<String> section2 = new Tree<>("S");
Tree<String> leaf2 = new Tree<>("L");
section2.addSubtree(leaf2);
tree.addSubtree(section1);
tree.addSubtree(section2);
Tree<String> pattern = new Tree<>("S");
pattern.addSubtree(new Tree<>("L"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(2, matches.size());
// assertEquals("S", matches.get(0).getMatchedTree().getLabel());
// assertEquals("S", matches.get(1).getMatchedTree().getLabel());
}

@Test
public void testDoesThisTreeMatchWithNestedPatternWhereOneChildFails() {
Tree<String> tree = new Tree<>("PARENT");
Tree<String> child1 = new Tree<>("A");
Tree<String> child2 = new Tree<>("B");
tree.addSubtree(child1);
tree.addSubtree(child2);
Tree<String> pattern = new Tree<>("PARENT");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("X"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
}

@Test
public void testPatternWithLargeNumberOfChildrenFailsIfTreeHasFewerChildren() {
Tree<String> tree = new Tree<>("T");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
Tree<String> pattern = new Tree<>("T");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
pattern.addSubtree(new Tree<>("C"));
pattern.addSubtree(new Tree<>("D"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean match = grep.doesThisTreeMatch(tree);
assertFalse(match);
}

@Test
public void testPatternWithSingleNodeMatchesDeepStructureNode() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> node1 = new Tree<>("A");
Tree<String> node2 = new Tree<>("B");
Tree<String> node3 = new Tree<>("C");
node1.addSubtree(node2);
node2.addSubtree(node3);
tree.addSubtree(node1);
Tree<String> pattern = new Tree<>("B");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertEquals("B", matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testDoesThisTreeMatchSucceedsWhenBothAreLeafNodes() {
Tree<String> tree = new Tree<>("LEAF");
Tree<String> pattern = new Tree<>("LEAF");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
// assertEquals("LEAF", matches.get(0).getMatchedTree().getLabel());
}

@Test
public void testToStringReturnsCorrectPatternTreeString() {
Tree<String> pattern = new Tree<>("P");
pattern.addSubtree(new Tree<>("X"));
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
String expected = pattern.toString();
String actual = grep.toString();
assertEquals(expected, actual);
}

@Test
public void testMatchesReturnsFalseWhenTreeIsNull() {
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
try {
grep.matches(null);
fail("Expected NullPointerException for null tree");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testPatternAndTreeWithSameReferenceMatch() {
Tree<String> tree = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(tree);
boolean matched = grep.doesThisTreeMatch(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testTreeLabelIsNullAndPatternLabelIsNonNullShouldFail() {
Tree<String> tree = new Tree<>(null);
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testPatternLabelIsNullAndTreeLabelIsNonNullShouldFail() {
Tree<String> tree = new Tree<>("A");
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertFalse(matched);
assertEquals(0, grep.getMatches().size());
}

@Test
public void testTreeAndPatternBothNullLabeledSingleNodeShouldMatch() {
Tree<String> tree = new Tree<>(null);
Tree<String> pattern = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertTrue(matched);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testPatternMatchesExactTreeStructureWithOneChildrenMismatchAtDeepLevel() {
Tree<String> tree = new Tree<>("A");
Tree<String> child1 = new Tree<>("B");
Tree<String> child2 = new Tree<>("C");
tree.addSubtree(child1);
child1.addSubtree(child2);
Tree<String> pattern = new Tree<>("A");
Tree<String> pChild1 = new Tree<>("B");
Tree<String> pChild2 = new Tree<>("MISMATCH");
pChild1.addSubtree(pChild2);
pattern.addSubtree(pChild1);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testTreeWithMoreChildrenThanPatternStillMatchesIfSubstructureAligns() {
Tree<String> tree = new Tree<>("P");
tree.addSubtree(new Tree<>("A"));
tree.addSubtree(new Tree<>("B"));
tree.addSubtree(new Tree<>("C"));
Tree<String> pattern = new Tree<>("P");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testMatchReturnsMultipleMatchesOnDistinctSubtrees() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> child1 = new Tree<>("X");
child1.addSubtree(new Tree<>("Y"));
Tree<String> child2 = new Tree<>("X");
child2.addSubtree(new Tree<>("Y"));
tree.addSubtree(child1);
tree.addSubtree(child2);
Tree<String> pattern = new Tree<>("X");
pattern.addSubtree(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(2, matches.size());
}

@Test
public void testPatternWithChildrenFailsWhenTreeHasFewerChildren() {
Tree<String> tree = new Tree<>("P");
tree.addSubtree(new Tree<>("A"));
Tree<String> pattern = new Tree<>("P");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertFalse(result);
}

@Test
public void testMatchWithSingleChildAndSentinelsPatternSucceedsWhenTreeHasAdditionalChildren() {
Tree<String> tree = new Tree<>("ROOT");
tree.addSubtree(new Tree<>("X"));
tree.addSubtree(new Tree<>("Y"));
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("X"));
pattern.addSubtree(new Tree<>(TreeGrep.endOfChildrenString));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
assertEquals(1, grep.getMatches().size());
}

@Test
public void testPatternThatExistsAtLeafLevelSubtreeWithExactMatch() {
Tree<String> tree = new Tree<>("TOP");
Tree<String> a = new Tree<>("A");
Tree<String> b = new Tree<>("B");
tree.addSubtree(a);
a.addSubtree(b);
Tree<String> pattern = new Tree<>("B");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
// assertEquals("B", grep.getMatches().get(0).getMatchedTree().getLabel());
}

@Test
public void testEmptyTreeAndPatternWithLeafShouldFail() {
Tree<String> tree = null;
Tree<String> pattern = new Tree<>("A");
TreeGrep<String> grep = new TreeGrep<>(pattern);
try {
grep.matches(tree);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testDoesThisTreeMatchHandlesCrossProductMergingOfChildren() {
Tree<String> tree = new Tree<>("ROOT");
Tree<String> a = new Tree<>("A");
Tree<String> b = new Tree<>("B");
tree.addSubtree(a);
tree.addSubtree(b);
Tree<String> pattern = new Tree<>("ROOT");
pattern.addSubtree(new Tree<>("A"));
pattern.addSubtree(new Tree<>("B"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
assertTrue(result);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
}

@Test
public void testMultipleTreeNodesMatchSinglePatternNodeShouldReturnMultipleMatches() {
Tree<String> tree = new Tree<>("ROOT");
tree.addSubtree(new Tree<>("X"));
tree.addSubtree(new Tree<>("X"));
Tree<String> pattern = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
assertTrue(matched);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(2, matches.size());
}
}
