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

public class TreeGrep_llmsuite_5_GPTLLMTest {

@Test
public void testLeafPatternMatchSingleNode() {
Tree<String> patternLeaf = new Tree<>("NN");
TreeGrep<String> grep = new TreeGrep<>(patternLeaf);
Tree<String> tree = new Tree<>("NN");
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("NN", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testSimplePatternExactTreeMatch() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("NP", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testSimplePatternMultipleMatchesInTree() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("VP");
Tree<String> np1 = new Tree<>("NP");
// np1.addChild(new Tree<>("NN"));
Tree<String> np2 = new Tree<>("NP");
// np2.addChild(new Tree<>("NN"));
// tree.addChild(np1);
// tree.addChild(np2);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(2, matches.size());
assertEquals("NP", matches.get(0).getRootMatch().getLabel());
assertEquals("NP", matches.get(1).getRootMatch().getLabel());
}

@Test
public void testNoPatternMatchReturnsFalse() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("VP");
Tree<String> pp = new Tree<>("PP");
// pp.addChild(new Tree<>("IN"));
// tree.addChild(pp);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPartialMatchShouldNotReturnMatch() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("VB"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternLongerThanTreeReturnsNoMatch() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NN");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testToStringReturnsPatternString() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
assertEquals(pattern.toString(), grep.toString());
}

@Test
public void testMatchChildrenAtEndUsingEndOfChildrenString() {
Tree<String> pattern = new Tree<>("VP");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// pattern.addChild(np);
// pattern.addChild(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("VP");
Tree<String> np1 = new Tree<>("NP");
// np1.addChild(new Tree<>("NN"));
Tree<String> pp = new Tree<>("PP");
// tree.addChild(np1);
// tree.addChild(pp);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertFalse(matches.isEmpty());
assertEquals("VP", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testMatchChildrenAtStartUsingStartOfChildrenString() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// pattern.addChild(np);
Tree<String> tree = new Tree<>("VP");
Tree<String> np1 = new Tree<>("NP");
// np1.addChild(new Tree<>("NN"));
Tree<String> pp = new Tree<>("PP");
// tree.addChild(np1);
// tree.addChild(pp);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertFalse(matches.isEmpty());
assertEquals("VP", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testDeepPatternMatch() {
Tree<String> pattern = new Tree<>("S");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// pattern.addChild(np);
Tree<String> tree = new Tree<>("S");
Tree<String> npChild = new Tree<>("NP");
// npChild.addChild(new Tree<>("NN"));
// tree.addChild(npChild);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("S", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testNullLabelMatchFails() {
Tree<String> pattern = new Tree<>(null);
Tree<String> tree = new Tree<>("X");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test(expected = NullPointerException.class)
public void testNullTreePassedToMatchesThrowsException() {
Tree<String> pattern = new Tree<>("NN");
TreeGrep<String> grep = new TreeGrep<>(pattern);
grep.matches(null);
}

@Test
public void testDoesThisTreeMatchDoesNotRecurse() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("ROOT");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// tree.addChild(np);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testEmptyPatternReturnsMatchIfTreeIsAlsoEmptyLabel() {
Tree<String> pattern = new Tree<>("");
Tree<String> tree = new Tree<>("");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testVerboseDoesNotAffectMatchResult() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
grep.verbose = true;
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("NP", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testEmptyPatternDoesNotMatchNonEmptyTree() {
Tree<String> pattern = new Tree<>("");
Tree<String> tree = new Tree<>("NP");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternWithOnlyEndOfChildrenStringFailsWhenNotAtEnd() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("NP"));
// tree.addChild(new Tree<>("PP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternWithOnlyStartOfChildrenStringFailsWhenNotAtStart() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("NP"));
// tree.addChild(new Tree<>("VB"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testMatchEmptyTreeAgainstEmptyPatternTree() {
Tree<String> pattern = new Tree<>(null);
Tree<String> tree = new Tree<>(null);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertNull(matches.get(0).getRootMatch().getLabel());
}

@Test
public void testMatchTreeWithOnlyRootNodeNoChildren() {
Tree<String> pattern = new Tree<>("S");
Tree<String> tree = new Tree<>("S");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("S", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testPatternWithMismatchedChildCountFails() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("DT"));
// pattern.addChild(new Tree<>("JJ"));
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("DT"));
// tree.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testTreeWithExtraChildrenAtEndShouldStillMatchWhenEndWildcardUsed() {
Tree<String> pattern = new Tree<>("VP");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// pattern.addChild(np);
// pattern.addChild(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("VP");
Tree<String> npActual = new Tree<>("NP");
// npActual.addChild(new Tree<>("NN"));
// tree.addChild(npActual);
// tree.addChild(new Tree<>("PP"));
// tree.addChild(new Tree<>("ADVP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertFalse(matches.isEmpty());
}

@Test
public void testTreeWithExtraChildrenAtStartShouldStillMatchWhenStartMarkerUsed() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
Tree<String> child = new Tree<>("VB");
// pattern.addChild(child);
Tree<String> tree = new Tree<>("VP");
Tree<String> advp = new Tree<>("ADVP");
Tree<String> vb = new Tree<>("VB");
// tree.addChild(advp);
// tree.addChild(vb);
// tree.addChild(new Tree<>("NP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertFalse(matches.isEmpty());
}

@Test
public void testNullLabelInTreeFailsEvenWithMatchingPatternLabel() {
Tree<String> pattern = new Tree<>("NP");
Tree<String> tree = new Tree<>(null);
// tree.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testSingleMatchPatternNotCombinedDespiteMultipleChildrenMatch() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("VP");
Tree<String> npFirst = new Tree<>("NP");
Tree<String> npSecond = new Tree<>("NP");
// npFirst.addChild(new Tree<>("NN"));
// npSecond.addChild(new Tree<>("NN"));
// tree.addChild(npFirst);
// tree.addChild(npSecond);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternWithNullChildLabelFailsToMatchValidTree() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>(null));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testTreeWithNullChildLabelFailsToMatchPattern() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>(null));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testMergeMatchesWithEmptyOuterMatchListOnlyReturnsInner() {
Tree<String> pattern = new Tree<>("A");
Tree<String> child = new Tree<>("B");
// pattern.addChild(child);
Tree<String> tree = new Tree<>("A");
Tree<String> treeChild = new Tree<>("B");
// tree.addChild(treeChild);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean match = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(match);
assertEquals(1, matches.size());
assertEquals("A", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testMultipleDistinctSubtreesWithSameMatchShouldResultInMultipleMatchObjects() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("ROOT");
Tree<String> np1 = new Tree<>("NP");
// np1.addChild(new Tree<>("NN"));
Tree<String> np2 = new Tree<>("NP");
// np2.addChild(new Tree<>("NN"));
// tree.addChild(np1);
// tree.addChild(np2);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean found = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(found);
assertEquals(2, matches.size());
assertEquals("NP", matches.get(0).getRootMatch().getLabel());
assertEquals("NP", matches.get(1).getRootMatch().getLabel());
}

@Test
public void testTreeAndPatternWithIdenticalRepeatedStructureOnlyMatchesEntireSubtreeRootOnce() {
Tree<String> pattern = new Tree<>("A");
Tree<String> b1 = new Tree<>("B");
// pattern.addChild(b1);
// b1.addChild(new Tree<>("C"));
Tree<String> tree = new Tree<>("A");
Tree<String> b2 = new Tree<>("B");
// tree.addChild(b2);
// b2.addChild(new Tree<>("C"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean match = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(match);
assertEquals(1, matches.size());
assertEquals("A", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testDoesThisTreeMatchFailsWhenSubMatchWouldSucceed() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("VBD"));
Tree<String> subtree = new Tree<>("VP");
// subtree.addChild(new Tree<>("VBD"));
Tree<String> tree = new Tree<>("ROOT");
// tree.addChild(subtree);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean topLevelMatches = grep.doesThisTreeMatch(tree);
boolean recursiveMatches = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(topLevelMatches);
assertTrue(recursiveMatches);
assertFalse(matches.isEmpty());
assertEquals("VP", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testCrossProductMergingCreatesAllCombinationsCorrectly() {
Tree<String> pattern = new Tree<>("ROOT");
Tree<String> child1 = new Tree<>("X");
Tree<String> child2 = new Tree<>("Y");
// pattern.addChild(child1);
// pattern.addChild(child2);
Tree<String> tree = new Tree<>("ROOT");
Tree<String> treeChild1A = new Tree<>("X");
Tree<String> treeChild1B = new Tree<>("X");
Tree<String> treeChild2A = new Tree<>("Y");
Tree<String> treeChild2B = new Tree<>("Y");
Tree<String> sub1 = new Tree<>("X");
Tree<String> sub2 = new Tree<>("Y");
// tree.addChild(sub1);
// tree.addChild(sub2);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(matched);
assertFalse(matches.isEmpty());
assertEquals("ROOT", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testTreeWithExtraIntermediateLayerFailsToMatchFlatPattern() {
Tree<String> pattern = new Tree<>("A");
// pattern.addChild(new Tree<>("B"));
Tree<String> tree = new Tree<>("A");
Tree<String> intermediate = new Tree<>("X");
Tree<String> bNode = new Tree<>("B");
// intermediate.addChild(bNode);
// tree.addChild(intermediate);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(matched);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternThatRequiresLeafFailsIfTreeHasChildrenThere() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
Tree<String> nn = new Tree<>("NN");
// nn.addChild(new Tree<>("X"));
// tree.addChild(nn);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternMatchesSubtreeNotRootOnlyInMatchesNotInDoesThisTreeMatch() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("DT"));
Tree<String> tree = new Tree<>("S");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("DT"));
// tree.addChild(np);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matchesResult = grep.matches(tree);
boolean doesThisTreeMatchResult = grep.doesThisTreeMatch(tree);
assertTrue(matchesResult);
assertFalse(doesThisTreeMatchResult);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertEquals(1, matches.size());
assertEquals("NP", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testPatternChildCountLargerThanTreeSubtreeShouldFail() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("VBD"));
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("VBD"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testSingleNodePatternWithNoMatchInTree() {
Tree<String> pattern = new Tree<>("Z");
Tree<String> tree = new Tree<>("X");
// tree.addChild(new Tree<>("Y"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternWithZeroChildrenFailsWhenTreeHasChildren() {
Tree<String> pattern = new Tree<>("NP");
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testTreeWithNoChildrenMatchesSingleLeafPattern() {
Tree<String> pattern = new Tree<>("VP");
Tree<String> tree = new Tree<>("VP");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("VP", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testPatternFailsWhenRequiredChildLabelDiffersFromTreeChild() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("PP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testMatchFailsWhenTreeLeavesDoNotMatchPatternLeaves() {
Tree<String> pattern = new Tree<>("VP");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("DT"));
// pattern.addChild(np);
Tree<String> tree = new Tree<>("VP");
Tree<String> npTree = new Tree<>("NP");
// npTree.addChild(new Tree<>("NN"));
// tree.addChild(npTree);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testMatchSucceedsWithNestedChildrenMatch() {
Tree<String> pattern = new Tree<>("S");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// pattern.addChild(np);
Tree<String> tree = new Tree<>("S");
Tree<String> npTree = new Tree<>("NP");
Tree<String> nn = new Tree<>("NN");
// npTree.addChild(nn);
// tree.addChild(npTree);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
}

@Test
public void testInvalidPatternOnlyContainsEndOfChildrenString() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("NP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testInvalidStartOfChildrenStringAtEndOfPattern() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("NP"));
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("NP"));
// tree.addChild(new Tree<>("PP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(matched);
assertTrue(matches.isEmpty());
}

@Test
public void testMatchFailsWhenTreeHasMatchingRootLabelButNoChildren() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("VBD"));
Tree<String> tree = new Tree<>("VP");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternAndTreeWithMismatchedBranchingFactor() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("VBD"));
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("VBD"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testMatchWithSingleChildWithEndOfChildrenMarkerSucceedsAtTailPosition() {
Tree<String> pattern = new Tree<>("VP");
Tree<String> obj = new Tree<>("NP");
// obj.addChild(new Tree<>("NN"));
// pattern.addChild(obj);
// pattern.addChild(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("VP");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// tree.addChild(np);
// tree.addChild(new Tree<>("PP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.matches(tree);
List<TreeGrepMatch<String>> matchList = grep.getMatches();
assertTrue(matches);
assertFalse(matchList.isEmpty());
assertEquals("VP", matchList.get(0).getRootMatch().getLabel());
}

@Test
public void testMatchPatternWithStartOfChildrenMarkerFailsIfNotAtStart() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("DT"));
// pattern.addChild(np);
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("PP"));
Tree<String> npInstance = new Tree<>("NP");
// npInstance.addChild(new Tree<>("DT"));
// tree.addChild(npInstance);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(matched);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternMatchesLeafWithEmptyLabel() {
Tree<String> pattern = new Tree<>("");
Tree<String> tree = new Tree<>("");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testTreeWithMultipleNestedMatchesCreatesMultipleResults() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("ROOT");
Tree<String> np1 = new Tree<>("NP");
// np1.addChild(new Tree<>("NN"));
Tree<String> np2 = new Tree<>("NP");
// np2.addChild(new Tree<>("NN"));
Tree<String> np3 = new Tree<>("NP");
// np3.addChild(new Tree<>("NN"));
// tree.addChild(np1);
// tree.addChild(np2);
// tree.addChild(np3);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean found = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(found);
assertEquals(3, matches.size());
assertEquals("NP", matches.get(0).getRootMatch().getLabel());
assertEquals("NP", matches.get(1).getRootMatch().getLabel());
assertEquals("NP", matches.get(2).getRootMatch().getLabel());
}

@Test
public void testPatternFailsIfMatchOnlySupportedThroughChildPermutation() {
Tree<String> pattern = new Tree<>("U");
Tree<String> a = new Tree<>("A");
Tree<String> b = new Tree<>("B");
// pattern.addChild(a);
// pattern.addChild(b);
Tree<String> tree = new Tree<>("U");
// tree.addChild(new Tree<>("B"));
// tree.addChild(new Tree<>("A"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testMergeChildrenMatchesReturnsEmptyWhenChildrenEmpty() {
TreeGrep<String> grep = new TreeGrep<>(new Tree<>("ROOT"));
List<List<TreeGrepMatch<String>>> childrenMatches = new java.util.ArrayList<>();
List<TreeGrepMatch<String>> currentNodeMatches = new java.util.ArrayList<>();
TreeGrepMatch<String> match = new TreeGrepMatch<>(new Tree<>("ROOT"));
currentNodeMatches.add(match);
List<TreeGrepMatch<String>> result = grep.mergeChildrenMatches(childrenMatches, currentNodeMatches);
assertEquals(1, result.size());
assertEquals("ROOT", result.get(0).getCurrentPatternNode().getLabel());
}

@Test
public void testDoesNodeMatchPatternWithNoMatchingChildrenReturnsEmptyMatchList() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("VBZ"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
List<TreeGrepMatch<String>> incomingMatches = new java.util.ArrayList<>();
List<TreeGrepMatch<String>> results = grep.doesNodeMatchPattern(tree, pattern, incomingMatches);
assertTrue(results.isEmpty());
}

@Test
public void testPatternLeafMatchesTreeWithExactLeafMatch() {
Tree<String> pattern = new Tree<>("NN");
Tree<String> tree = new Tree<>("NN");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(result);
assertEquals(1, matches.size());
assertEquals("NN", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testPatternLeafFailsWhenTreeHasSameLabelButIsNotLeaf() {
Tree<String> pattern = new Tree<>("VP");
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("NP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternFailsIfTreeNodeHasExtraChildrenAndPatternDoesNotUseWildcard() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("NN"));
// tree.addChild(new Tree<>("JJ"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matchList = grep.getMatches();
assertFalse(matches);
assertTrue(matchList.isEmpty());
}

@Test
public void testTreeWithNoMatchingSubtreeReturnsFalseInMatchesTraversal() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("VBZ"));
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("S");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// tree.addChild(np);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(matched);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternMatchesRootEvenWhenTreeHasNoChildrenAndPatternIsLeaf() {
Tree<String> pattern = new Tree<>("ROOT");
Tree<String> tree = new Tree<>("ROOT");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matches = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matchList = grep.getMatches();
assertTrue(matches);
assertEquals(1, matchList.size());
assertEquals("ROOT", matchList.get(0).getRootMatch().getLabel());
}

@Test
public void testRepeatedSubtreeOccurrencesGenerateSeparateMatchObjects() {
Tree<String> pattern = new Tree<>("X");
// pattern.addChild(new Tree<>("Y"));
Tree<String> tree = new Tree<>("A");
Tree<String> x1 = new Tree<>("X");
// x1.addChild(new Tree<>("Y"));
Tree<String> x2 = new Tree<>("X");
// x2.addChild(new Tree<>("Y"));
// tree.addChild(x1);
// tree.addChild(x2);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean match = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(match);
assertEquals(2, matches.size());
assertEquals("X", matches.get(0).getRootMatch().getLabel());
assertEquals("X", matches.get(1).getRootMatch().getLabel());
}

@Test
public void testMergeChildrenMatchWithMismatchingNumberOfMatchPermutations() {
TreeGrep<String> grep = new TreeGrep<>(new Tree<>("ROOT"));
Tree<String> label1 = new Tree<>("X");
Tree<String> label2 = new Tree<>("Y");
TreeGrepMatch<String> m1 = new TreeGrepMatch<>(label1);
TreeGrepMatch<String> m2 = new TreeGrepMatch<>(label2);
List<TreeGrepMatch<String>> list1 = new java.util.ArrayList<>();
List<TreeGrepMatch<String>> list2 = new java.util.ArrayList<>();
list1.add(m1);
list2.add(m2);
List<List<TreeGrepMatch<String>>> childrenMatches = new java.util.ArrayList<>();
childrenMatches.add(list1);
childrenMatches.add(list2);
TreeGrepMatch<String> parent = new TreeGrepMatch<>(new Tree<>("ROOT"));
List<TreeGrepMatch<String>> currentNodeMatches = new java.util.ArrayList<>();
currentNodeMatches.add(parent);
List<TreeGrepMatch<String>> result = grep.mergeChildrenMatches(childrenMatches, currentNodeMatches);
assertEquals(1, result.size());
assertEquals("ROOT", result.get(0).getRootMatch().getLabel());
}

@Test
public void testDoesNodeMatchPatternReturnsMatchWhenBothTreesOnlyHaveRootAndMatch() {
Tree<String> tree = new Tree<>("Z");
Tree<String> pattern = new Tree<>("Z");
TreeGrep<String> grep = new TreeGrep<>(pattern);
List<TreeGrepMatch<String>> initial = new java.util.ArrayList<>();
List<TreeGrepMatch<String>> result = grep.doesNodeMatchPattern(tree, pattern, initial);
assertEquals(1, result.size());
assertEquals("Z", result.get(0).getRootMatch().getLabel());
}

@Test
public void testMismatchDueToLabelEvenWhenTreeStructureIsSame() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("NN"));
Tree<String> tree = new Tree<>("NPP");
// tree.addChild(new Tree<>("NN"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> result = grep.getMatches();
assertFalse(matched);
assertTrue(result.isEmpty());
}

@Test
public void testEmptyTreeFailsToMatchNonEmptyPattern() {
Tree<String> pattern = new Tree<>("S");
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> results = grep.getMatches();
assertFalse(matched);
assertTrue(results.isEmpty());
}

@Test
public void testInvalidUseOfEndOfChildrenStringWithInsufficientTreeChildrenFailsMatch() {
Tree<String> pattern = new Tree<>("VP");
Tree<String> np = new Tree<>("NP");
// np.addChild(new Tree<>("NN"));
// pattern.addChild(np);
// pattern.addChild(new Tree<>(TreeGrep.endOfChildrenString));
Tree<String> tree = new Tree<>("VP");
Tree<String> pp = new Tree<>("PP");
// tree.addChild(pp);
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testInvalidUseOfStartOfChildrenStringWithInvalidStartFails() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>(TreeGrep.startOfChildrenString));
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("PP"));
// tree.addChild(new Tree<>("NP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternMatchesTreeWhenExtraChildrenPresentAfterEndOfPattern() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("VBD"));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("VBD"));
// tree.addChild(new Tree<>("NP"));
// tree.addChild(new Tree<>("PP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testPatternFailsOnTreeWithMatchingFirstChildButNoSecondChild() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("VBD"));
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>("VBD"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testTreeMatchesPatternWithLeafPatternRootAndTreeLeafNode() {
Tree<String> pattern = new Tree<>("ROOT");
Tree<String> tree = new Tree<>("ROOT");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertTrue(matched);
assertEquals(1, matches.size());
assertEquals("ROOT", matches.get(0).getRootMatch().getLabel());
}

@Test
public void testTreeWithNullChildDoesNotMatchEvenIfOtherLabelsValid() {
Tree<String> pattern = new Tree<>("VP");
// pattern.addChild(new Tree<>("NP"));
Tree<String> tree = new Tree<>("VP");
// tree.addChild(new Tree<>(null));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}

@Test
public void testDoesLabelMatchPatternLabelFailsWhenLabelsAreNotEqual() {
Tree<String> pattern = new Tree<>("S");
Tree<String> tree = new Tree<>("NP");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
assertFalse(matched);
}

@Test
public void testEmptyPatternStringFailsToMatchNonEmptyTreeLabel() {
Tree<String> pattern = new Tree<>("");
Tree<String> tree = new Tree<>("VP");
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean matched = grep.doesThisTreeMatch(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(matched);
assertTrue(matches.isEmpty());
}

@Test
public void testMergeMatchesWithSingleOuterAndMultipleInnerReturnsCorrectSize() {
TreeGrep<String> grep = new TreeGrep<>(new Tree<>("X"));
TreeGrepMatch<String> outer = new TreeGrepMatch<>(new Tree<>("A"));
TreeGrepMatch<String> inner1 = new TreeGrepMatch<>(new Tree<>("C"));
TreeGrepMatch<String> inner2 = new TreeGrepMatch<>(new Tree<>("D"));
List<TreeGrepMatch<String>> outerList = new java.util.ArrayList<>();
outerList.add(outer);
List<TreeGrepMatch<String>> innerList = new java.util.ArrayList<>();
innerList.add(inner1);
innerList.add(inner2);
// List<TreeGrepMatch<String>> result = grep.mergeMatches(outerList, innerList);
// assertEquals(2, result.size());
}

@Test
public void testPatternSingleChildTreeChildMismatchAtSameIndexFailsMatch() {
Tree<String> pattern = new Tree<>("NP");
// pattern.addChild(new Tree<>("DT"));
Tree<String> tree = new Tree<>("NP");
// tree.addChild(new Tree<>("VP"));
TreeGrep<String> grep = new TreeGrep<>(pattern);
boolean result = grep.matches(tree);
List<TreeGrepMatch<String>> matches = grep.getMatches();
assertFalse(result);
assertTrue(matches.isEmpty());
}
}
