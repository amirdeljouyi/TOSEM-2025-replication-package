package opennlp.tools.parser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import opennlp.tools.util.Span;

public class ParsePromptTest {

  @Test
  public void constructorExposesInitialStateAndMutators() {
    Parse parse = new Parse("bright moon", new Span(0, 6), "JJ", -1.25, 3);

    assertEquals("bright moon", parse.getText());
    assertEquals(new Span(0, 6), parse.getSpan());
    assertEquals("bright", parse.getCoveredText());
    assertEquals("bright", parse.toString());
    assertEquals("JJ", parse.getType());
    assertEquals(-1.25, parse.getProb(), 0.0);
    assertEquals(3, parse.getHeadIndex());
    assertSame(parse, parse.getHead());
    assertEquals(0, parse.getChildCount());
    assertFalse(parse.complete());
    assertNull(parse.getParent());
    assertNull(parse.getLabel());
    assertNull(parse.getDerivation());
    assertNull(parse.getPreviousPunctuationSet());
    assertNull(parse.getNextPunctuationSet());

    parse.setType("ADJ");
    parse.setLabel("start");
    parse.addProb(0.5);
    parse.isChunk(true);

    assertEquals("ADJ", parse.getType());
    assertEquals("start", parse.getLabel());
    assertEquals(-0.75, parse.getProb(), 0.0);
    assertTrue(parse.isChunk());
  }

  @Test
  public void headConstructorCopiesHeadAndIndexOrUsesSelfForNull() {
    Parse token = new Parse("cat", new Span(0, 3), "TK", 1.0, 7);
    Parse phrase = new Parse("cat", new Span(0, 3), "NP", 0.8, token);
    Parse withoutHead = new Parse("cat", new Span(0, 3), "NP", 0.8, (Parse) null);

    assertSame(token, phrase.getHead());
    assertEquals(7, phrase.getHeadIndex());
    assertSame(withoutHead, withoutHead.getHead());
    assertEquals(0, withoutHead.getHeadIndex());
  }

  @Test
  public void punctuationCanBeAccumulatedSortedOrReplaced() {
    Parse word = new Parse("a,.", new Span(0, 1), "NN", 1.0, 0);
    Parse comma = new Parse("a,.", new Span(1, 2), ",", 0.2, 1);
    Parse period = new Parse("a,.", new Span(2, 3), ".", 0.9, 2);

    word.addPreviousPunctuation(comma);
    word.addPreviousPunctuation(period);
    word.addNextPunctuation(comma);

    assertArrayEquals(new Parse[] {period, comma},
        word.getPreviousPunctuationSet().toArray(new Parse[0]));
    assertArrayEquals(new Parse[] {comma}, word.getNextPunctuationSet().toArray(new Parse[0]));

    Collection<Parse> previous = new ArrayList<>(Collections.singletonList(period));
    Collection<Parse> next = new ArrayList<>(Arrays.asList(period, comma));
    word.setPrevPunctuation(previous);
    word.setNextPunctuation(next);

    assertSame(previous, word.getPreviousPunctuationSet());
    assertSame(next, word.getNextPunctuationSet());
  }

  @Test
  public void insertOrdersChildrenAndNestsContainedConstituents() {
    Parse root = new Parse("one two", new Span(0, 7), "S", 1.0, 0);
    Parse two = new Parse("one two", new Span(4, 7), "TK", 1.0, 1);
    Parse one = new Parse("one two", new Span(0, 3), "TK", 1.0, 0);
    Parse nounPhrase = new Parse("one two", new Span(0, 3), "NP", 0.8, 0);

    root.insert(two);
    root.insert(one);
    root.insert(nounPhrase);

    assertArrayEquals(new Parse[] {nounPhrase, two}, root.getChildren());
    assertArrayEquals(new Parse[] {one}, nounPhrase.getChildren());
    assertSame(root, nounPhrase.getParent());
    assertSame(nounPhrase, one.getParent());
    assertSame(root, two.getParent());
    assertEquals(0, root.indexOf(nounPhrase));
    assertEquals(1, root.indexOf(two));
    assertEquals(-1, root.indexOf(one));
  }

  @Test
  public void insertRejectsConstituentOutsideParentSpan() {
    Parse root = new Parse("abc", new Span(0, 3), "S", 1.0, 0);
    Parse outside = new Parse("abc", new Span(3, 3), "X", 1.0, 0);

    IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
        () -> root.insert(new Parse("abcde", new Span(3, 5), "X", 1.0, 0)));

    assertEquals("Inserting constituent not contained in the sentence!", error.getMessage());
    root.insert(outside);
    assertSame(outside, root.getChildren()[0]);
  }

  @Test
  public void showRendersTextGapsNestedNodesAndEncodedBrackets() {
    Parse root = new Parse("( cat )", new Span(0, 7), "S", 1.0, 0);
    Parse leftBracket = new Parse("( cat )", new Span(0, 1), "TK", 1.0, 0);
    Parse noun = new Parse("( cat )", new Span(2, 5), "NN", 1.0, 0);
    Parse token = new Parse("( cat )", new Span(2, 5), "TK", 1.0, 0);
    Parse rightBracket = new Parse("( cat )", new Span(6, 7), "TK", 1.0, 2);
    noun.insert(token);
    root.insert(leftBracket);
    root.insert(noun);
    root.insert(rightBracket);
    StringBuffer rendered = new StringBuffer("prefix:");

    root.show(rendered);
    root.show();

    assertEquals("prefix:(S -LRB- (NN cat) -RRB-)", rendered.toString());
    assertEquals("(S -LRB- (NN cat) -RRB-)", root.toStringPennTreebank());
  }

  @Test
  public void parseParseBuildsTreeTokensParentsAndRoundTrips() {
    Parse.useFunctionTags(false);
    Parse root = Parse.parseParse("(TOP (S (NP (DT The) (NN cat)) (VP (VBD slept)) (. .)))");
    Parse sentence = root.getChildren()[0];
    Parse nounPhrase = sentence.getChildren()[0];
    Parse[] tags = root.getTagNodes();
    Parse[] tokens = root.getTokenNodes();

    assertEquals("The cat slept . ", root.getText());
    assertEquals("The cat slept . ", root.getCoveredText());
    assertEquals("(TOP (S (NP (DT The) (NN cat)) (VP (VBD slept)) (. .)) )",
        root.toStringPennTreebank());
    assertEquals(1, root.getChildCount());
    assertTrue(root.complete());
    assertSame(root, sentence.getParent());
    assertSame(sentence, nounPhrase.getParent());
    assertArrayEquals(new String[] {"DT", "NN", "VBD", "."},
        new String[] {tags[0].getType(), tags[1].getType(), tags[2].getType(), tags[3].getType()});
    assertArrayEquals(new String[] {"The", "cat", "slept", "."},
        new String[] {tokens[0].getCoveredText(), tokens[1].getCoveredText(),
            tokens[2].getCoveredText(), tokens[3].getCoveredText()});
    assertArrayEquals(new int[] {0, 1, 2, 3},
        new int[] {tokens[0].getHeadIndex(), tokens[1].getHeadIndex(),
            tokens[2].getHeadIndex(), tokens[3].getHeadIndex()});
  }

  @Test
  public void parseParseDecodesAllPennTreebankBracketTokens() {
    Parse.useFunctionTags(false);
    Parse root = Parse.parseParse(
        "(TOP (X (-LRB- -LRB-) (-RRB- -RRB-) (-LCB- -LCB-) (-RCB- -RCB-)"
            + " (-LSB- -LSB-) (-RSB- -RSB-)))");

    assertEquals("( ) { } [ ] ", root.getText());
    assertEquals("(TOP (X (-LRB- -LRB-) (-RRB- -RRB-) (-LCB- -LCB-) (-RCB- -RCB-)"
        + " (-LSB- -LSB-) (-RSB- -RSB-)) )", root.toStringPennTreebank());
  }

  @Test
  public void parseParseOptionallyPreservesOneFunctionTag() {
    Parse.useFunctionTags(true);
    try {
      Parse tagged = Parse.parseParse("(TOP (S (NP-SBJ-1 (NNP Ada)) (VP-TMP (VBD left))))");

      assertEquals("NP-SBJ", tagged.getChildren()[0].getChildren()[0].getType());
      assertEquals("VP-TMP", tagged.getChildren()[0].getChildren()[1].getType());
    } finally {
      Parse.useFunctionTags(false);
    }

    Parse untagged = Parse.parseParse("(TOP (S (NP-SBJ (NNP Ada))))");
    assertEquals("NP", untagged.getChildren()[0].getChildren()[0].getType());
  }

  @Test
  public void cloneCopiesNodeStateAndDerivationButSharesChildren() {
    Parse original = Parse.parseParse("(TOP (S (NN birds)))");
    original.setLabel("built");
    original.setDerivation(new StringBuffer("start-build"));
    Parse copy = (Parse) original.clone();

    assertNotSame(original, copy);
    assertEquals(original, copy);
    assertEquals("built", copy.getLabel());
    assertEquals("start-build", copy.getDerivation().toString());
    assertNotSame(original.getDerivation(), copy.getDerivation());
    assertSame(original.getChildren()[0], copy.getChildren()[0]);

    original.getDerivation().append("-original");
    copy.setLabel("copy");
    assertEquals("start-build", copy.getDerivation().toString());
    assertEquals("built", original.getLabel());
    assertNotEquals(original, copy);
  }

  @Test
  public void cloneRightFrontierCopiesOnlyPathThroughRequestedNode() {
    Parse root = Parse.parseParse("(TOP (S (NP (NN cats)) (VP (VBP purr))))");
    Parse sentence = root.getChildren()[0];
    Parse vp = sentence.getChildren()[1];
    Parse tag = vp.getChildren()[0];

    Parse copy = root.clone(tag);

    assertNotSame(root, copy);
    assertNotSame(sentence, copy.getChildren()[0]);
    assertSame(sentence.getChildren()[0], copy.getChildren()[0].getChildren()[0]);
    assertNotSame(vp, copy.getChildren()[0].getChildren()[1]);
    assertNotSame(tag, copy.getChildren()[0].getChildren()[1].getChildren()[0]);
    assertSame(tag.getChildren()[0],
        copy.getChildren()[0].getChildren()[1].getChildren()[0].getChildren()[0]);
  }

  @Test
  public void cloneRootCopiesSelectedChildFrontier() {
    Parse root = Parse.parseParse("(TOP (A (NN one)) (B (NN two)))");
    Parse target = root.getChildren()[1].getChildren()[0];

    Parse copy = root.cloneRoot(target, 1);

    assertSame(root.getChildren()[0], copy.getChildren()[0]);
    assertNotSame(root.getChildren()[1], copy.getChildren()[1]);
    assertNotSame(target, copy.getChildren()[1].getChildren()[0]);
  }

  @Test
  public void tagSequenceProbabilitySumsLogProbabilities() {
    Parse root = new Parse("a b", new Span(0, 3), "S", 1.0, 0);
    Parse firstTag = new Parse("a b", new Span(0, 1), "NN", 0.5, 0);
    Parse firstToken = new Parse("a b", new Span(0, 1), "TK", 1.0, 0);
    Parse secondTag = new Parse("a b", new Span(2, 3), "VB", 0.25, 1);
    Parse secondToken = new Parse("a b", new Span(2, 3), "TK", 1.0, 1);
    firstTag.insert(firstToken);
    secondTag.insert(secondToken);
    root.insert(firstTag);
    root.insert(secondTag);

    assertEquals(StrictMath.log(0.5) + StrictMath.log(0.25), root.getTagSequenceProb(), 0.0);
    assertEquals(StrictMath.log(0.5), firstTag.getTagSequenceProb(), 0.0);
    assertEquals(0.0, firstToken.getTagSequenceProb(), 0.0);
  }

  @Test
  public void posAndFlatClassificationDistinguishesTreeShapes() {
    Parse root = Parse.parseParse("(TOP (NP (DT a) (NN cat)))");
    Parse nounPhrase = root.getChildren()[0];
    Parse determiner = nounPhrase.getChildren()[0];
    Parse token = determiner.getChildren()[0];

    assertTrue(determiner.isPosTag());
    assertFalse(token.isPosTag());
    assertTrue(nounPhrase.isFlat());
    assertFalse(root.isFlat());
  }

  @Test
  public void setChildReplacesWithLabeledClone() {
    Parse root = Parse.parseParse("(TOP (NN word))");
    Parse oldChild = root.getChildren()[0];

    root.setChild(0, "replacement");
    Parse newChild = root.getChildren()[0];

    assertNotSame(oldChild, newChild);
    assertEquals("replacement", newChild.getLabel());
    assertNull(oldChild.getLabel());
    assertEquals(oldChild.getCoveredText(), newChild.getCoveredText());
    assertSame(oldChild.getChildren()[0], newChild.getChildren()[0]);
  }

  @Test
  public void removeUpdatesSpanOnlyWhenAnEdgeChildIsRemoved() {
    Parse root = new Parse("a b c", new Span(0, 5), "S", 1.0, 0);
    root.insert(new Parse("a b c", new Span(0, 1), "A", 1.0, 0));
    root.insert(new Parse("a b c", new Span(2, 3), "B", 1.0, 1));
    root.insert(new Parse("a b c", new Span(4, 5), "C", 1.0, 2));

    root.remove(1);
    assertEquals(new Span(0, 5), root.getSpan());
    root.remove(0);
    assertEquals(new Span(4, 5), root.getSpan());
    root.remove(0);
    assertEquals(0, root.getChildCount());
    assertEquals(new Span(4, 5), root.getSpan());
  }

  @Test
  public void updateSpanUsesFirstAndLastChild() {
    Parse root = new Parse("xx a b yy", new Span(0, 9), "S", 1.0, 0);
    root.insert(new Parse("xx a b yy", new Span(3, 4), "A", 1.0, 0));
    root.insert(new Parse("xx a b yy", new Span(5, 6), "B", 1.0, 1));

    root.updateSpan();

    assertEquals(new Span(3, 6), root.getSpan());
    assertEquals("a b", root.getCoveredText());
  }

  @Test
  public void commonParentHandlesSiblingsAncestorsIdentityAndDisjointTrees() {
    Parse root = Parse.parseParse("(TOP (S (NP (DT the) (NN cat)) (VP (VBD ran))))");
    Parse sentence = root.getChildren()[0];
    Parse nounPhrase = sentence.getChildren()[0];
    Parse determiner = nounPhrase.getChildren()[0];
    Parse noun = nounPhrase.getChildren()[1];
    Parse verbPhrase = sentence.getChildren()[1];
    Parse unrelated = Parse.parseParse("(TOP (NN other))").getTagNodes()[0];

    assertSame(nounPhrase, determiner.getCommonParent(noun));
    assertSame(sentence, nounPhrase.getCommonParent(verbPhrase));
    assertSame(nounPhrase, determiner.getCommonParent(nounPhrase));
    assertSame(sentence, nounPhrase.getCommonParent(nounPhrase));
    assertNull(determiner.getCommonParent(unrelated));
  }

  @Test
  public void parentCanBeExplicitlyChangedAndCleared() {
    Parse child = new Parse("x", new Span(0, 1), "X", 1.0, 0);
    Parse parent = new Parse("x", new Span(0, 1), "P", 1.0, 0);

    child.setParent(parent);
    assertSame(parent, child.getParent());
    child.setParent(null);
    assertNull(child.getParent());
  }

  @Test
  public void equalityHashingAndProbabilityOrderingFollowPublicContract() {
    Parse low = new Parse("same", new Span(0, 4), "A", 0.1, 0);
    Parse high = new Parse("same", new Span(0, 4), "B", 0.9, 0);
    Parse labeled = new Parse("same", new Span(0, 4), "C", 0.1, 4);

    assertEquals(low, high);
    assertEquals(low.hashCode(), high.hashCode());
    assertEquals(1, low.compareTo(high));
    assertEquals(-1, high.compareTo(low));
    assertEquals(0, low.compareTo(labeled));
    assertEquals(low, low);
    assertNotEquals(low, null);
    assertNotEquals(low, "same");

    labeled.setLabel("different");
    assertNotEquals(low, labeled);
    assertEquals(low.hashCode(), labeled.hashCode());
  }

  @Test
  public void updateHeadsUsesEnglishRulesRecursivelyAndFallsBackForTokens() throws Exception {
    Parse root = Parse.parseParse("(TOP (S (NP (DT the) (NN dog)) (VP (VBD barked))))");
    opennlp.tools.parser.lang.en.HeadRules rules =
        new opennlp.tools.parser.lang.en.HeadRules(new StringReader("3 S 0 VP\n3 VP 0 VBD\n"));

    root.updateHeads(rules);
    Parse nounPhrase = root.getChildren()[0].getChildren()[0];
    Parse verbPhrase = root.getChildren()[0].getChildren()[1];

    assertEquals("NN", nounPhrase.getHead().getType());
    assertEquals(1, nounPhrase.getHeadIndex());
    assertEquals("VBD", verbPhrase.getHead().getType());
    assertEquals(2, root.getHeadIndex());
    assertSame(root.getTokenNodes()[0], root.getTokenNodes()[0].getHead());
  }

  @Test
  public void addIncludesPreviousPunctuationAndRecomputesSpanAndHead() throws Exception {
    Parse phrase = new Parse("cat , runs", new Span(0, 3), "S", 1.0, 0);
    Parse cat = new Parse("cat , runs", new Span(0, 3), "NN", 1.0, 0);
    Parse comma = new Parse("cat , runs", new Span(4, 5), ",", 0.1, 1);
    Parse runs = new Parse("cat , runs", new Span(6, 10), "VBZ", 1.0, 2);
    runs.addPreviousPunctuation(comma);
    opennlp.tools.parser.lang.en.HeadRules rules =
        new opennlp.tools.parser.lang.en.HeadRules(new StringReader("4 S 0 VBZ NN\n"));

    phrase.add(cat, rules);
    phrase.add(runs, rules);

    assertArrayEquals(new Parse[] {cat, comma, runs}, phrase.getChildren());
    assertEquals(new Span(0, 10), phrase.getSpan());
    assertSame(runs, phrase.getHead());
    assertEquals(2, phrase.getHeadIndex());
  }

  @Test
  public void adjoinCreatesParentAroundLastChildPunctuationAndSister() throws Exception {
    Parse root = new Parse("one , two", new Span(0, 3), "TOP", 1.0, 0);
    Parse one = new Parse("one , two", new Span(0, 3), "NP", 1.0, 0);
    Parse comma = new Parse("one , two", new Span(4, 5), ",", 1.0, 1);
    Parse two = new Parse("one , two", new Span(6, 9), "NP", 1.0, 2);
    root.insert(one);
    two.addPreviousPunctuation(comma);
    opennlp.tools.parser.lang.en.HeadRules rules =
        new opennlp.tools.parser.lang.en.HeadRules(new StringReader("3 TOP 0 NP\n"));

    Parse joined = root.adjoin(two, rules);

    assertSame(joined, root.getChildren()[0]);
    assertArrayEquals(new Parse[] {one, comma, two}, joined.getChildren());
    assertEquals(new Span(0, 9), joined.getSpan());
    assertEquals(new Span(0, 9), root.getSpan());
    assertSame(one, joined.getHead());
    assertEquals(0, root.getHeadIndex());
  }

  @Test
  public void adjoinRootReplacesSpecifiedChildOnly() throws Exception {
    Parse root = new Parse("a b c", new Span(0, 5), "TOP", 1.0, 0);
    Parse a = new Parse("a b c", new Span(0, 1), "A", 1.0, 0);
    Parse b = new Parse("a b c", new Span(2, 3), "B", 1.0, 1);
    Parse c = new Parse("a b c", new Span(4, 5), "C", 1.0, 2);
    root.insert(a);
    root.insert(b);
    opennlp.tools.parser.lang.en.HeadRules rules =
        new opennlp.tools.parser.lang.en.HeadRules(new StringReader("3 B 0 C\n"));

    Parse joined = root.adjoinRoot(c, rules, 1);

    assertArrayEquals(new Parse[] {a, joined}, root.getChildren());
    assertArrayEquals(new Parse[] {b, c}, joined.getChildren());
    assertEquals("B", joined.getType());
    assertEquals(new Span(2, 5), joined.getSpan());
    assertSame(c, joined.getHead());
  }

  @Test
  public void expandTopNodeMovesSiblingsIntoSelectedRootInOrder() {
    Parse top = new Parse("a b c", new Span(0, 5), "TOP", 1.0, 0);
    Parse a = new Parse("a b c", new Span(0, 1), "A", 1.0, 0);
    Parse middle = new Parse("a b c", new Span(2, 3), "S", 1.0, 1);
    Parse c = new Parse("a b c", new Span(4, 5), "C", 1.0, 2);
    top.insert(a);
    top.insert(middle);
    top.insert(c);

    top.expandTopNode(middle);

    assertArrayEquals(new Parse[] {middle}, top.getChildren());
    assertArrayEquals(new Parse[] {a, c}, middle.getChildren());
    assertEquals(new Span(0, 5), middle.getSpan());
  }

  @Test
  public void pruneParseRemovesUnaryNodeHavingSameType() {
    Parse wrapper = new Parse("cat", new Span(0, 3), "ROOT", 1.0, 0);
    Parse outer = new Parse("cat", new Span(0, 3), "NP", 1.0, 0);
    Parse inner = new Parse("cat", new Span(0, 3), "NP", 1.0, 0);
    Parse tag = new Parse("cat", new Span(0, 3), "NN", 1.0, 0);
    Parse token = new Parse("cat", new Span(0, 3), "TK", 1.0, 0);
    tag.insert(token);
    inner.insert(tag);
    outer.insert(inner);
    wrapper.insert(outer);

    Parse.pruneParse(outer);

    assertSame(inner, wrapper.getChildren()[0]);
    assertSame(wrapper, inner.getParent());
    assertNull(outer.getParent());
  }

  @Test
  public void fixPossessivesGroupsFollowingPhraseAsNounPhrase() {
    Parse.useFunctionTags(false);
    Parse root = Parse.parseParse("(TOP (NP (NP (NNP John) (POS 's)) (JJ red) (NN car)))");

    Parse.fixPossesives(root);
    Parse outerNp = root.getChildren()[0];

    assertEquals(2, outerNp.getChildCount());
    assertEquals("NP", outerNp.getChildren()[1].getType());
    assertEquals("red car", outerNp.getChildren()[1].getCoveredText());
    assertArrayEquals(new String[] {"JJ", "NN"},
        new String[] {outerNp.getChildren()[1].getChildren()[0].getType(),
            outerNp.getChildren()[1].getChildren()[1].getType()});
  }

  @Test
  public void addNamesWrapsTokenRangeWithoutCrossingChildren() {
    Parse root = Parse.parseParse("(TOP (S (NP (NNP New) (NNP York)) (VP (VBZ shines))))");
    Parse[] tokens = root.getTokenNodes();

    Parse.addNames("LOCATION", new Span[] {new Span(0, 2)}, tokens);
    Parse nounPhrase = root.getChildren()[0].getChildren()[0];

    assertEquals(1, nounPhrase.getChildCount());
    assertEquals("LOCATION", nounPhrase.getChildren()[0].getType());
    assertEquals("New York", nounPhrase.getChildren()[0].getCoveredText());
    assertEquals(2, nounPhrase.getChildren()[0].getChildCount());
    assertEquals(1, nounPhrase.getChildren()[0].getHeadIndex());
  }

  @Test
  public void addNamesIgnoresTokenRangeFromDisconnectedTrees() {
    Parse left = Parse.parseParse("(TOP (NNP Alice))");
    Parse right = Parse.parseParse("(TOP (NNP Bob))");
    Parse[] disconnected = new Parse[] {left.getTokenNodes()[0], right.getTokenNodes()[0]};

    Parse.addNames("PERSON", new Span[] {new Span(0, 2)}, disconnected);

    assertEquals("NNP", left.getChildren()[0].getType());
    assertEquals("NNP", right.getChildren()[0].getType());
  }

  @Test
  public void malformedParseWithExtraClosingParenthesisFails() {
    assertThrows(java.util.EmptyStackException.class, () -> Parse.parseParse("(TOP (NN x))))"));
  }

  @Test
  public void emptyAndIncompleteTreesExposeBoundaryFailures() {
    Parse empty = new Parse("", new Span(0, 0), "TOP", 1.0, 0);

    assertThrows(IndexOutOfBoundsException.class, empty::updateSpan);
    assertThrows(IndexOutOfBoundsException.class, () -> empty.remove(0));
    assertThrows(IndexOutOfBoundsException.class, () -> empty.setChild(0, "x"));
    assertThrows(IndexOutOfBoundsException.class,
        () -> empty.clone(new Parse("", new Span(0, 0), "X", 1.0, 0)));
  }
}
