package edu.illinois.cs.cogcomp.core.algorithms;

import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TreeGrepPromptTest {

    @Test
    public void constructorExposesExactPatternAndUsesItsStringForm() {
        Tree<String> pattern = new Tree<>("S");
        pattern.addLeaf("NP");
        pattern.addLeaf("VP");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertSame(pattern, grep.getPattern());
        assertEquals(pattern.toString(), grep.toString());
    }

    @Test
    public void matchesAreUnavailableBeforeFirstSearch() {
        TreeGrep<String> grep = new TreeGrep<>(new Tree<>("token"));

        assertNull(grep.getMatches());
    }

    @Test
    public void leafPatternMatchesLeafAndNonLeafNodesWithSameLabel() {
        Tree<String> pattern = new Tree<>("X");
        Tree<String> tree = new Tree<>("X");
        Tree<String> nested = new Tree<>("X");
        nested.addLeaf("word");
        tree.addSubtree(nested);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.matches(tree));
        assertEquals(2, grep.getMatches().size());
        assertSame(tree, grep.getMatches().get(0).getRootMatch());
        assertSame(nested, grep.getMatches().get(1).getRootMatch());
        assertEquals(1, grep.getMatches().get(0).getPatternDFSMatches().size());
        assertEquals(1, grep.getMatches().get(0).getPatternLeafMatches().size());
    }

    @Test
    public void recursiveSearchFindsInteriorNodeButRootOnlySearchDoesNot() {
        Tree<String> pattern = new Tree<>("VP");
        pattern.addLeaf("runs");
        Tree<String> tree = new Tree<>("S");
        tree.addLeaf("subject");
        Tree<String> vp = new Tree<>("VP");
        vp.addLeaf("runs");
        tree.addSubtree(vp);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertFalse(grep.doesThisTreeMatch(tree));
        assertTrue(grep.getMatches().isEmpty());
        assertTrue(grep.matches(tree));
        assertEquals(1, grep.getMatches().size());
        assertSame(vp, grep.getMatches().get(0).getRootMatch());
    }

    @Test
    public void rootOnlySearchReturnsCompleteDepthFirstAndLeafMappings() {
        Tree<String> pattern = new Tree<>("S");
        Tree<String> patternNp = new Tree<>("NP");
        patternNp.addLeaf("NN");
        pattern.addSubtree(patternNp);
        pattern.addLeaf("VP");
        Tree<String> tree = new Tree<>("S");
        Tree<String> treeNp = new Tree<>("NP");
        Tree<String> noun = new Tree<>("NN");
        treeNp.addSubtree(noun);
        Tree<String> verbPhrase = new Tree<>("VP");
        verbPhrase.addLeaf("ignored-because-pattern-leaf");
        tree.addSubtree(treeNp);
        tree.addSubtree(verbPhrase);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(tree));
        TreeGrepMatch<String> match = grep.getMatches().get(0);
        List<Tree<String>> dfs = match.getPatternDFSMatches();
        assertEquals(4, dfs.size());
        assertSame(tree, dfs.get(0));
        assertSame(treeNp, dfs.get(1));
        assertSame(noun, dfs.get(2));
        assertSame(verbPhrase, dfs.get(3));
        assertEquals(2, match.getPatternLeafMatches().size());
        assertSame(noun, match.getPatternLeafMatches().get(0));
        assertSame(verbPhrase, match.getPatternLeafMatches().get(1));
        assertSame(pattern, match.getPattern());
    }

    @Test
    public void ordinaryChildrenMayMatchAtMiddleContiguousPosition() {
        Tree<String> pattern = new Tree<>("R");
        pattern.addLeaf("A");
        pattern.addLeaf("B");
        Tree<String> tree = new Tree<>("R");
        tree.addLeaf("before");
        Tree<String> a = new Tree<>("A");
        Tree<String> b = new Tree<>("B");
        tree.addSubtree(a);
        tree.addSubtree(b);
        tree.addLeaf("after");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(tree));
        assertEquals(1, grep.getMatches().size());
        assertSame(a, grep.getMatches().get(0).getPatternLeafMatches().get(0));
        assertSame(b, grep.getMatches().get(0).getPatternLeafMatches().get(1));
    }

    @Test
    public void ordinaryChildrenProduceOneMatchForEveryValidOffset() {
        Tree<String> pattern = new Tree<>("R");
        pattern.addLeaf("A");
        pattern.addLeaf("B");
        Tree<String> tree = new Tree<>("R");
        Tree<String> firstA = new Tree<>("A");
        Tree<String> firstB = new Tree<>("B");
        Tree<String> secondA = new Tree<>("A");
        Tree<String> secondB = new Tree<>("B");
        tree.addSubtree(firstA);
        tree.addSubtree(firstB);
        tree.addSubtree(secondA);
        tree.addSubtree(secondB);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(tree));
        assertEquals(2, grep.getMatches().size());
        assertSame(firstA, grep.getMatches().get(0).getPatternLeafMatches().get(0));
        assertSame(firstB, grep.getMatches().get(0).getPatternLeafMatches().get(1));
        assertSame(secondA, grep.getMatches().get(1).getPatternLeafMatches().get(0));
        assertSame(secondB, grep.getMatches().get(1).getPatternLeafMatches().get(1));
    }

    @Test
    public void nonContiguousOrReversedChildrenDoNotMatch() {
        Tree<String> pattern = new Tree<>("R");
        pattern.addLeaf("A");
        pattern.addLeaf("B");
        Tree<String> separated = new Tree<>("R");
        separated.addLeaf("A");
        separated.addLeaf("gap");
        separated.addLeaf("B");
        Tree<String> reversed = new Tree<>("R");
        reversed.addLeaf("B");
        reversed.addLeaf("A");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertFalse(grep.doesThisTreeMatch(separated));
        assertTrue(grep.getMatches().isEmpty());
        assertFalse(grep.doesThisTreeMatch(reversed));
        assertTrue(grep.getMatches().isEmpty());
    }

    @Test
    public void nonLeafPatternCannotMatchSameLabeledLeaf() {
        Tree<String> pattern = new Tree<>("R");
        pattern.addLeaf("child");
        Tree<String> tree = new Tree<>("R");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertFalse(grep.doesThisTreeMatch(tree));
        assertNotNull(grep.getMatches());
        assertTrue(grep.getMatches().isEmpty());
    }

    @Test
    public void startAnchorRequiresMatchAtFirstChildAndIsNotInResultMapping() {
        Tree<String> pattern = new Tree<>("R");
        pattern.addLeaf("^^^");
        pattern.addLeaf("A");
        pattern.addLeaf("B");
        Tree<String> anchoredTree = new Tree<>("R");
        Tree<String> a = new Tree<>("A");
        Tree<String> b = new Tree<>("B");
        anchoredTree.addSubtree(a);
        anchoredTree.addSubtree(b);
        anchoredTree.addLeaf("tail");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(anchoredTree));
        assertEquals(1, grep.getMatches().size());
        assertEquals(3, grep.getMatches().get(0).getPatternDFSMatches().size());
        assertSame(a, grep.getMatches().get(0).getPatternDFSMatches().get(1));
        assertSame(b, grep.getMatches().get(0).getPatternDFSMatches().get(2));

        Tree<String> shiftedTree = new Tree<>("R");
        shiftedTree.addLeaf("prefix");
        shiftedTree.addLeaf("A");
        shiftedTree.addLeaf("B");
        assertFalse(grep.doesThisTreeMatch(shiftedTree));
    }

    @Test
    public void endAnchorRequiresMatchAtLastChildAndIsNotInResultMapping() {
        Tree<String> pattern = new Tree<>("R");
        pattern.addLeaf("A");
        pattern.addLeaf("B");
        pattern.addLeaf("$$$");
        Tree<String> anchoredTree = new Tree<>("R");
        anchoredTree.addLeaf("prefix");
        Tree<String> a = new Tree<>("A");
        Tree<String> b = new Tree<>("B");
        anchoredTree.addSubtree(a);
        anchoredTree.addSubtree(b);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(anchoredTree));
        assertEquals(3, grep.getMatches().get(0).getPatternDFSMatches().size());
        assertSame(a, grep.getMatches().get(0).getPatternDFSMatches().get(1));
        assertSame(b, grep.getMatches().get(0).getPatternDFSMatches().get(2));

        Tree<String> trailingTree = new Tree<>("R");
        trailingTree.addLeaf("A");
        trailingTree.addLeaf("B");
        trailingTree.addLeaf("suffix");
        assertFalse(grep.doesThisTreeMatch(trailingTree));
    }

    @Test
    public void endAnchorFailsCleanlyWhenTreeHasTooFewChildren() {
        Tree<String> pattern = new Tree<>("R");
        pattern.addLeaf("A");
        pattern.addLeaf("B");
        pattern.addLeaf("$$$");
        Tree<String> tree = new Tree<>("R");
        tree.addLeaf("A");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertFalse(grep.doesThisTreeMatch(tree));
        assertTrue(grep.getMatches().isEmpty());
    }

    @Test
    public void nestedAmbiguityProducesCartesianProductMatches() {
        Tree<String> pattern = new Tree<>("R");
        Tree<String> patternP = new Tree<>("P");
        patternP.addLeaf("X");
        pattern.addSubtree(patternP);
        Tree<String> tree = new Tree<>("R");
        Tree<String> treeP = new Tree<>("P");
        Tree<String> firstX = new Tree<>("X");
        Tree<String> secondX = new Tree<>("X");
        treeP.addSubtree(firstX);
        treeP.addSubtree(secondX);
        tree.addSubtree(treeP);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(tree));
        assertEquals(2, grep.getMatches().size());
        assertSame(tree, grep.getMatches().get(0).getRootMatch());
        assertSame(treeP, grep.getMatches().get(0).getPatternDFSMatches().get(1));
        assertSame(firstX, grep.getMatches().get(0).getPatternLeafMatches().get(0));
        assertSame(secondX, grep.getMatches().get(1).getPatternLeafMatches().get(0));
    }

    @Test
    public void independentAmbiguousChildrenProduceFullCrossProduct() {
        Tree<String> pattern = new Tree<>("R");
        Tree<String> patternP = new Tree<>("P");
        patternP.addLeaf("X");
        Tree<String> patternQ = new Tree<>("Q");
        patternQ.addLeaf("Y");
        pattern.addSubtree(patternP);
        pattern.addSubtree(patternQ);
        Tree<String> tree = new Tree<>("R");
        Tree<String> treeP = new Tree<>("P");
        treeP.addLeaf("X");
        treeP.addLeaf("X");
        Tree<String> treeQ = new Tree<>("Q");
        treeQ.addLeaf("Y");
        treeQ.addLeaf("Y");
        tree.addSubtree(treeP);
        tree.addSubtree(treeQ);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(tree));
        assertEquals(4, grep.getMatches().size());
        assertEquals(5, grep.getMatches().get(3).getPatternDFSMatches().size());
        assertSame(treeP.getChild(1), grep.getMatches().get(3).getPatternLeafMatches().get(0));
        assertSame(treeQ.getChild(1), grep.getMatches().get(3).getPatternLeafMatches().get(1));
    }

    @Test
    public void recursiveSearchReturnsMatchesInBreadthFirstNodeOrder() {
        Tree<String> pattern = new Tree<>("X");
        Tree<String> root = new Tree<>("root");
        Tree<String> shallowX = new Tree<>("X");
        Tree<String> branch = new Tree<>("branch");
        Tree<String> deepX = new Tree<>("X");
        root.addSubtree(shallowX);
        root.addSubtree(branch);
        branch.addSubtree(deepX);
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.matches(root));
        assertEquals(2, grep.getMatches().size());
        assertSame(shallowX, grep.getMatches().get(0).getRootMatch());
        assertSame(deepX, grep.getMatches().get(1).getRootMatch());
    }

    @Test
    public void eachSearchReplacesPriorResultsIncludingAfterFailure() {
        Tree<String> pattern = new Tree<>("hit");
        Tree<String> firstTree = new Tree<>("root");
        Tree<String> firstHit = new Tree<>("hit");
        firstTree.addSubtree(firstHit);
        Tree<String> secondTree = new Tree<>("miss");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertTrue(grep.matches(firstTree));
        List<TreeGrepMatch<String>> oldResults = grep.getMatches();
        assertEquals(1, oldResults.size());
        assertFalse(grep.matches(secondTree));
        assertNotSame(oldResults, grep.getMatches());
        assertTrue(grep.getMatches().isEmpty());
        assertEquals(1, oldResults.size());
    }

    @Test
    public void genericIntegerLabelsUseValueEquality() {
        Tree<Integer> pattern = new Tree<>(Integer.valueOf(1000));
        pattern.addLeaf(Integer.valueOf(2000));
        Tree<Integer> tree = new Tree<>(Integer.valueOf(1000));
        Tree<Integer> child = new Tree<>(Integer.valueOf(2000));
        tree.addSubtree(child);
        TreeGrep<Integer> grep = new TreeGrep<>(pattern);

        assertTrue(grep.doesThisTreeMatch(tree));
        assertSame(tree, grep.getMatches().get(0).getRootMatch());
        assertSame(child, grep.getMatches().get(0).getPatternLeafMatches().get(0));
    }

    @Test
    public void rootLabelMismatchSkipsOtherwiseMatchingDescendantsInRootOnlyMode() {
        Tree<String> pattern = new Tree<>("expected-root");
        pattern.addLeaf("child");
        Tree<String> tree = new Tree<>("different-root");
        tree.addLeaf("child");
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        assertFalse(grep.doesThisTreeMatch(tree));
        assertTrue(grep.getMatches().isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void nullTreeIsRejectedByRecursiveSearch() {
        TreeGrep<String> grep = new TreeGrep<>(new Tree<>("pattern"));

        grep.matches(null);
    }

    @Test(expected = NullPointerException.class)
    public void nullTreeIsRejectedByRootOnlySearch() {
        TreeGrep<String> grep = new TreeGrep<>(new Tree<>("pattern"));

        grep.doesThisTreeMatch(null);
    }

    @Test(expected = NullPointerException.class)
    public void nullTreeLabelIsRejectedDuringComparison() {
        Tree<String> pattern = new Tree<>("pattern");
        Tree<String> tree = new Tree<>();
        TreeGrep<String> grep = new TreeGrep<>(pattern);

        grep.doesThisTreeMatch(tree);
    }
}
