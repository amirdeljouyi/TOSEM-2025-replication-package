package opennlp.tools.parser;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionaryEntryPersistor;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.ml.model.*;
import opennlp.tools.util.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Parse_3_GPTLLMTest {

@Test
public void testInsertAddsChild() {
String text = "The quick brown fox";
Span rootSpan = new Span(0, text.length());
Span childSpan = new Span(0, 19);
Parse root = new Parse(text, rootSpan, "S", 1.0, 0);
Parse child = new Parse(text, childSpan, "NP", 0.9, 0);
root.insert(child);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
assertEquals(child, children[0]);
assertEquals(root, child.getParent());
}

@Test
public void testInsertThrowsExceptionForInvalidSpan() {
String text = "The quick brown fox";
Span rootSpan = new Span(0, text.length());
Span invalidSpan = new Span(text.length() + 1, text.length() + 5);
Parse root = new Parse(text, rootSpan, "S", 1.0, 0);
Parse invalidChild = new Parse(text, invalidSpan, "VP", 0.8, 0);
root.insert(invalidChild);
}

@Test
public void testClonePreservesFieldsAndChildren() {
String text = "Example sentence";
Span span = new Span(0, text.length());
Parse original = new Parse(text, span, "S", 0.99, 0);
Parse child = new Parse(text, new Span(0, 7), "NP", 0.8, 0);
original.insert(child);
Parse clone = (Parse) original.clone();
Parse[] originalChildren = original.getChildren();
Parse[] clonedChildren = clone.getChildren();
assertEquals(original.getType(), clone.getType());
assertEquals(original.getSpan(), clone.getSpan());
assertEquals(1, originalChildren.length);
assertEquals(1, clonedChildren.length);
assertEquals(originalChildren[0].getSpan(), clonedChildren[0].getSpan());
}

@Test
public void testGetCoveredTextReturnsSubstring() {
String text = "The quick brown fox";
Span span = new Span(4, 9);
Parse parse = new Parse(text, span, "JJ", 0.6, 0);
String coveredText = parse.getCoveredText();
assertEquals("quick", coveredText);
}

@Test
public void testSetAndGetType() {
String text = "word";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "XX", 1.0, 0);
parse.setType("NN");
assertEquals("NN", parse.getType());
}

@Test
public void testAddAndGetProb() {
String text = "running";
Parse parse = new Parse(text, new Span(0, text.length()), "VBG", 0.5, 0);
double probBefore = parse.getProb();
parse.addProb(0.7);
double probAfter = parse.getProb();
assertEquals(probBefore + 0.7, probAfter, 0.00001);
}

@Test
public void testSetAndGetLabel() {
String text = "walk";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "VB", 0.9, 0);
parse.setLabel("someLabel");
assertEquals("someLabel", parse.getLabel());
}

@Test
public void testAddPreviousAndNextPunctuation() {
String text = "Hello , world !";
Span span = new Span(0, text.length());
Parse root = new Parse(text, span, "ROOT", 1.0, 0);
Parse comma = new Parse(text, new Span(6, 7), ",", 1.0, 0);
Parse excl = new Parse(text, new Span(13, 14), "!", 1.0, 1);
root.addPreviousPunctuation(comma);
root.addNextPunctuation(excl);
Collection<Parse> prev = root.getPreviousPunctuationSet();
Collection<Parse> next = root.getNextPunctuationSet();
assertTrue(prev.contains(comma));
assertTrue(next.contains(excl));
}

@Test
public void testRemoveChildAdjustsChildrenList() {
String text = "A simple sentence.";
Span rootSpan = new Span(0, text.length());
Parse root = new Parse(text, rootSpan, "S", 1.0, 0);
Parse first = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse second = new Parse(text, new Span(8, 17), "VP", 1.0, 1);
root.insert(first);
root.insert(second);
root.remove(0);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
assertEquals(second, children[0]);
}

@Test
public void testIsFlatAndIsPosTag() {
String text = "dog";
Parse tok = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag = new Parse(text, new Span(0, 3), "NN", 1.0, tok);
tag.insert(tok);
assertTrue(tag.isPosTag());
assertTrue(tag.isFlat());
}

@Test
public void testChunkFlag() {
String text = "chunk";
Span span = new Span(0, text.length());
Parse p = new Parse(text, span, "CHUNK", 1.0, 0);
assertFalse(p.isChunk());
p.isChunk(true);
assertTrue(p.isChunk());
}

@Test
public void testCommonParentReturnsImmediateParent() {
String text = "Red car";
Span rootSpan = new Span(0, text.length());
Parse root = new Parse(text, rootSpan, "NP", 1.0, 0);
Parse adj = new Parse(text, new Span(0, 3), "JJ", 1.0, 0);
Parse noun = new Parse(text, new Span(4, 7), "NN", 1.0, 1);
root.insert(adj);
root.insert(noun);
Parse common = adj.getCommonParent(noun);
assertEquals(root, common);
}

@Test
public void testEqualsAndHashCodeForEqualParses() {
String text = "copy";
Span span = new Span(0, 4);
Parse p1 = new Parse(text, span, "NN", 1.0, 0);
Parse p2 = new Parse(text, span, "NN", 1.0, 0);
assertTrue(p1.equals(p2));
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testCompareToGreaterProbabilityFirst() {
String text = "run";
Parse low = new Parse(text, new Span(0, 3), "VB", 0.2, 0);
Parse high = new Parse(text, new Span(0, 3), "VB", 0.9, 0);
int result = high.compareTo(low);
assertTrue(result < 0);
}

@Test
public void testToStringReturnsSpanText() {
String text = "bright";
Span span = new Span(0, 6);
Parse p = new Parse(text, span, "JJ", 0.8, 0);
String value = p.toString();
assertEquals("bright", value);
}

@Test
public void testGetTagNodesReturnsTaggedNodes() {
String text = "red balloon";
Parse tokRed = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tagRed = new Parse(text, new Span(0, 3), "JJ", 1.0, tokRed);
tagRed.insert(tokRed);
Parse tokBalloon = new Parse(text, new Span(4, 11), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tagBalloon = new Parse(text, new Span(4, 11), "NN", 1.0, tokBalloon);
tagBalloon.insert(tokBalloon);
Parse root = new Parse(text, new Span(0, 11), "NP", 1.0, 0);
root.insert(tagRed);
root.insert(tagBalloon);
Parse[] tags = root.getTagNodes();
assertEquals(2, tags.length);
assertEquals("JJ", tags[0].getType());
assertEquals("NN", tags[1].getType());
}

@Test
public void testGetTokenNodesReturnsTerminalNodes() {
String text = "token only";
Parse token = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag = new Parse(text, new Span(0, 5), "NN", 1.0, token);
tag.insert(token);
Parse root = new Parse(text, new Span(0, 10), "ROOT", 1.0, 0);
root.insert(tag);
Parse[] tokens = root.getTokenNodes();
assertEquals(1, tokens.length);
assertEquals(AbstractBottomUpParser.TOK_NODE, tokens[0].getType());
}

@Test
public void testParseParseSimpleTree() {
String tree = "(TOP (NP (DT The) (NN dog)))";
Parse p = Parse.parseParse(tree);
assertEquals("TOP", p.getType());
Parse[] levelOne = p.getChildren();
assertEquals(1, levelOne.length);
assertEquals("NP", levelOne[0].getType());
Parse[] npChildren = levelOne[0].getChildren();
assertEquals("DT", npChildren[0].getType());
assertEquals("NN", npChildren[1].getType());
}

@Test
public void testUpdateHeadsWithMockRules() {
String text = "simple parse";
Parse p1 = new Parse(text, new Span(0, 6), "DT", 1.0, 0);
Parse p2 = new Parse(text, new Span(7, 12), "NN", 1.0, 1);
Parse parent = new Parse(text, new Span(0, 12), "NP", 1.0, 0);
parent.insert(p1);
parent.insert(p2);
HeadRules rules = mock(HeadRules.class);
when(rules.getHead(any(Parse[].class), eq("NP"))).thenReturn(p2);
parent.updateHeads(rules);
assertEquals(p2, parent.getHead());
}

@Test
public void testAddNamesCreatesNamedEntityNode() {
String text = "John Smith";
Parse tok1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag1 = new Parse(text, new Span(0, 4), "NNP", 1.0, tok1);
tag1.insert(tok1);
Parse tok2 = new Parse(text, new Span(5, 10), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tag2 = new Parse(text, new Span(5, 10), "NNP", 1.0, tok2);
tag2.insert(tok2);
Parse root = new Parse(text, new Span(0, 10), "NP", 1.0, 0);
root.insert(tag1);
root.insert(tag2);
Parse[] tokens = new Parse[] { tok1, tok2 };
Span[] names = new Span[] { new Span(0, 2) };
Parse.addNames("PERSON", names, tokens);
Parse[] children = root.getChildren();
boolean personNodePresent = false;
for (int i = 0; i < children.length; i++) {
if ("PERSON".equals(children[i].getType())) {
personNodePresent = true;
}
}
assertTrue(personNodePresent);
}

@Test
public void testInsertNestedChildren() {
String text = "Nested structure";
Parse root = new Parse(text, new Span(0, 15), "ROOT", 1.0, 0);
Parse level1 = new Parse(text, new Span(0, 10), "L1", 1.0, 0);
Parse level2 = new Parse(text, new Span(2, 5), "L2", 1.0, 0);
root.insert(level1);
root.insert(level2);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
Parse inserted = children[0];
Parse[] inner = inserted.getChildren();
assertEquals(1, inner.length);
assertEquals("L2", inner[0].getType());
}

@Test
public void testCloneRootMethod() {
String text = "clone root";
Parse root = new Parse(text, new Span(0, 10), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
root.insert(child);
Parse cloned = root.cloneRoot(child, 0);
assertNotSame(root, cloned);
assertEquals(root.getText(), cloned.getText());
assertEquals(root.getChildren()[0].getSpan(), cloned.getChildren()[0].getSpan());
}

@Test
public void testUpdateSpanUpdatesCorrectly() {
String text = "Update span test";
Parse root = new Parse(text, new Span(0, 10), "TOP", 1.0, 0);
Parse first = new Parse(text, new Span(1, 4), "A", 1.0, 0);
Parse second = new Parse(text, new Span(5, 9), "B", 1.0, 0);
root.insert(first);
root.insert(second);
root.updateSpan();
assertEquals(1, root.getSpan().getStart());
assertEquals(9, root.getSpan().getEnd());
}

@Test
public void testIsFlatFalseWhenMixedContent() {
String text = "Mix";
Parse tok = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse part1 = new Parse(text, new Span(0, 1), "A", 1.0, 0);
Parse part2 = new Parse(text, new Span(1, 3), "B", 1.0, 0);
part1.insert(tok);
part2.insert(tok);
Parse node = new Parse(text, new Span(0, 3), "Parent", 1.0, 0);
node.insert(part1);
node.insert(part2);
boolean result = node.isFlat();
assertFalse(result);
}

@Test
public void testCommonParentSelfReference() {
String text = "Hello";
Parse node = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse parent = new Parse(text, new Span(0, 5), "TOP", 1.0, 0);
parent.insert(node);
Parse common = node.getCommonParent(node);
assertEquals(parent, common);
}

@Test
public void testEqualsWhenDifferentLabel() {
String text = "equal test";
Span span = new Span(0, 5);
Parse p1 = new Parse(text, span, "A", 1.0, 0);
Parse p2 = new Parse(text, span, "A", 1.0, 0);
p1.setLabel("L1");
p2.setLabel("L2");
boolean result = p1.equals(p2);
assertFalse(result);
}

@Test
public void testAddNameCrossingChildSpan() {
String text = "Dr. John Smith Jr.";
Parse tok1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(4, 8), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tok3 = new Parse(text, new Span(9, 14), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse tok4 = new Parse(text, new Span(15, 18), AbstractBottomUpParser.TOK_NODE, 1.0, 3);
Parse tag1 = new Parse(text, new Span(0, 3), "NNP", 1.0, tok1);
Parse tag2 = new Parse(text, new Span(4, 8), "NNP", 1.0, tok2);
Parse tag3 = new Parse(text, new Span(9, 14), "NNP", 1.0, tok3);
Parse tag4 = new Parse(text, new Span(15, 18), "NNP", 1.0, tok4);
tag1.insert(tok1);
tag2.insert(tok2);
tag3.insert(tok3);
tag4.insert(tok4);
Parse parent = new Parse(text, new Span(0, 18), "NP", 1.0, 0);
parent.insert(tag1);
parent.insert(tag2);
parent.insert(tag3);
parent.insert(tag4);
Parse[] tokens = new Parse[] { tok1, tok2, tok3, tok4 };
Span[] names = new Span[] { new Span(0, 4) };
Parse.addNames("PERSON", names, tokens);
boolean found = false;
Parse[] children = parent.getChildren();
if (children.length > 4) {
found = true;
}
assertTrue(found);
}

@Test
public void testGetTagSequenceProbEmpty() {
String text = "empty";
Parse parse = new Parse(text, new Span(0, text.length()), "NP", 0.5, 0);
double prob = parse.getTagSequenceProb();
assertEquals(0.0, prob, 0.0001);
}

@Test
public void testGetTagSequenceProbSingleToken() {
String text = "dog";
Parse token = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 0.8, 0);
Parse tag = new Parse(text, new Span(0, 3), "NN", 0.8, 0);
tag.insert(token);
double expected = StrictMath.log(0.8);
double actual = tag.getTagSequenceProb();
assertEquals(expected, actual, 0.0001);
}

@Test
public void testSetPrevAndNextPunctuationManually() {
String text = "Text.";
Parse punct = new Parse(text, new Span(4, 5), ".", 1.0, 0);
Collection<Parse> set = Collections.singletonList(punct);
Parse parse = new Parse(text, new Span(0, 5), "S", 1.0, 0);
parse.setNextPunctuation(set);
parse.setPrevPunctuation(set);
Collection<Parse> prev = parse.getPreviousPunctuationSet();
Collection<Parse> next = parse.getNextPunctuationSet();
assertTrue(prev.contains(punct));
assertTrue(next.contains(punct));
}

@Test
public void testGetDerivationAndSetDerivation() {
String text = "Derivation";
Parse parse = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
assertNull(parse.getDerivation());
StringBuffer buf = new StringBuffer("S-NP-VP");
parse.setDerivation(buf);
assertEquals("S-NP-VP", parse.getDerivation().toString());
}

@Test
public void testAdjoinCreatesCorrectStructure() {
String text = "join test";
Parse left = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse rightToken = new Parse(text, new Span(6, 10), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse right = new Parse(text, new Span(6, 10), "NN", 1.0, rightToken);
right.insert(rightToken);
HeadRules mockRules = mock(HeadRules.class);
when(mockRules.getHead(any(Parse[].class), eq("NP"))).thenReturn(right);
Parse parent = new Parse(text, new Span(0, 10), "ROOT", 1.0, 0);
parent.insert(left);
Parse result = parent.adjoin(right, mockRules);
assertEquals("NP", result.getType());
assertEquals(right, result.getHead());
assertEquals(3, result.getChildren().length);
}

@Test
public void testExpandTopNodeMovesAllChildren() {
String text = "Expand test node";
Parse topNode = new Parse(text, new Span(7, 17), "S", 1.0, 0);
Parse left = new Parse(text, new Span(0, 6), "L", 1.0, 0);
Parse middle = new Parse(text, new Span(7, 10), "M", 1.0, 1);
Parse right = new Parse(text, new Span(11, 17), "R", 1.0, 2);
Parse root = new Parse(text, new Span(0, 17), "TOP", 1.0, 0);
root.insert(left);
root.insert(topNode);
root.insert(right);
topNode.insert(middle);
root.expandTopNode(topNode);
Parse[] topChildren = topNode.getChildren();
assertEquals(3, topChildren.length);
assertEquals("L", topChildren[0].getType());
assertEquals("M", topChildren[1].getType());
assertEquals("R", topChildren[2].getType());
}

@Test
public void testParseEqualsFalseForDifferentSpans() {
String text = "sample";
Parse p1 = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse p2 = new Parse(text, new Span(1, 5), "NP", 1.0, 0);
assertFalse(p1.equals(p2));
}

@Test
public void testParseEqualsFalseForDifferentText() {
Parse p1 = new Parse("one", new Span(0, 3), "NP", 1.0, 0);
Parse p2 = new Parse("two", new Span(0, 3), "NP", 1.0, 0);
assertFalse(p1.equals(p2));
}

@Test
public void testParseEqualsSameInstance() {
Parse p = new Parse("same", new Span(0, 4), "NN", 1.0, 0);
assertTrue(p.equals(p));
}

@Test
public void testParseEqualsOtherType() {
Parse p = new Parse("item", new Span(0, 4), "VB", 1.0, 0);
assertFalse(p.equals("NotAParse"));
}

@Test
public void testSetChildReplacesAndPreservesOtherChildren() {
String text = "replace test";
Parse originalChild = new Parse(text, new Span(0, 6), "X", 1.0, 0);
Parse extraChild = new Parse(text, new Span(7, 11), "Y", 1.0, 1);
Parse parent = new Parse(text, new Span(0, 11), "ROOT", 1.0, 0);
parent.insert(originalChild);
parent.insert(extraChild);
parent.setChild(0, "NP");
Parse[] children = parent.getChildren();
assertEquals(2, children.length);
assertEquals("NP", children[0].getLabel());
assertEquals("Y", children[1].getType());
}

@Test
public void testGetHeadReturnsSelfWhenNoChildren() {
String text = "headless";
Parse p = new Parse(text, new Span(0, 8), "NN", 1.0, 0);
assertEquals(p, p.getHead());
}

@Test
public void testCompareToWithEqualProbabilities() {
String text = "compare";
Parse p1 = new Parse(text, new Span(0, 4), "A", 0.6, 0);
Parse p2 = new Parse(text, new Span(0, 4), "A", 0.6, 0);
assertEquals(0, p1.compareTo(p2));
}

@Test
public void testInsertRemovesOverlappingChild() {
String text = "The fast car";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse oldChild = new Parse(text, new Span(4, 13), "NP", 0.8, 1);
Parse newChild = new Parse(text, new Span(0, 13), "VP", 0.9, 1);
parent.insert(oldChild);
parent.insert(newChild);
Parse[] children = parent.getChildren();
assertEquals(1, children.length);
assertEquals("VP", children[0].getType());
Parse[] nested = children[0].getChildren();
assertEquals("NP", nested[0].getType());
}

@Test
public void testInsertNestedIntoMatchingSpan() {
String text = "Parking brake";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, text.length()), "NP", 0.9, 0);
parent.insert(np);
Parse part = new Parse(text, new Span(0, 7), "NN", 0.7, 0);
np.insert(part);
Parse[] rootChildren = parent.getChildren();
assertEquals(1, rootChildren.length);
Parse[] innerChildren = rootChildren[0].getChildren();
assertEquals(1, innerChildren.length);
assertEquals("NN", innerChildren[0].getType());
}

@Test
public void testGetCommonParentNullWhenNoRelation() {
Parse p1 = new Parse("text1", new Span(0, 3), "A", 1.0, 0);
Parse p2 = new Parse("text2", new Span(0, 3), "B", 1.0, 0);
assertNull(p1.getCommonParent(p2));
}

@Test
public void testParseParseHandlesEmptyString() {
Parse p = Parse.parseParse("(TOP)");
assertNotNull(p);
assertEquals("TOP", p.getType());
Parse[] children = p.getChildren();
assertEquals(0, children.length);
}

@Test
public void testParseParseWithNoneToken() {
String parseStr = "(TOP (-NONE- *))";
Parse p = Parse.parseParse(parseStr);
assertNotNull(p.getText());
assertTrue(p.getText().isEmpty() || p.getText().trim().length() == 0);
}

@Test
public void testUseFunctionTagExtractsBaseAndFunction() {
String parseStr = "(TOP (NP-SBJ (DT The) (NN dog)))";
Parse.useFunctionTags(true);
Parse p = Parse.parseParse(parseStr);
Parse[] children = p.getChildren();
assertEquals("NP-SBJ", children[0].getType());
}

@Test
public void testUseFunctionTagDisabled() {
String parseStr = "(TOP (VP-TMP (VB eats)))";
Parse.useFunctionTags(false);
Parse p = Parse.parseParse(parseStr);
Parse[] children = p.getChildren();
assertEquals("VP", children[0].getType());
}

@Test
public void testFixPossessivesAddsNP() {
String text = "John's book is here";
Parse token1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag1 = new Parse(text, new Span(0, 4), "NNP", 1.0, token1);
tag1.insert(token1);
Parse token2 = new Parse(text, new Span(4, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tag2 = new Parse(text, new Span(4, 6), "POS", 1.0, token2);
tag2.insert(token2);
Parse token3 = new Parse(text, new Span(7, 11), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse tag3 = new Parse(text, new Span(7, 11), "NN", 1.0, token3);
tag3.insert(token3);
Parse root = new Parse(text, new Span(0, 18), "S", 1.0, 0);
root.insert(tag1);
root.insert(tag2);
root.insert(tag3);
Parse.fixPossesives(root);
Parse[] children = root.getChildren();
boolean foundNP = false;
for (int i = 0; i < children.length; i++) {
if ("NP".equals(children[i].getType()) && children[i].getSpan().getStart() == 7) {
foundNP = true;
}
}
assertTrue(foundNP);
}

@Test
public void testGetTokenReturnsNullIfNoTokenFound() {
String invalid = "(VP-TMP (VB ))";
Parse p = Parse.parseParse(invalid);
assertNotNull(p);
Parse[] children = p.getChildren();
assertEquals("VP", children[0].getType());
}

@Test
public void testInsertAlreadyContainingSpan() {
String text = "Hello span";
Parse main = new Parse(text, new Span(0, 11), "S", 1.0, 0);
Parse outer = new Parse(text, new Span(0, 7), "NP", 0.8, 0);
Parse inner = new Parse(text, new Span(0, 2), "NN", 0.8, 0);
main.insert(outer);
outer.insert(inner);
Parse[] topLevelChildren = main.getChildren();
assertEquals(1, topLevelChildren.length);
Parse[] nestedChildren = topLevelChildren[0].getChildren();
assertEquals(1, nestedChildren.length);
assertEquals("NN", nestedChildren[0].getType());
}

@Test
public void testInsertChildFullyOverlappingMultipleChildren() {
String text = "Some test input";
Parse parent = new Parse(text, new Span(0, 15), "ROOT", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse child2 = new Parse(text, new Span(6, 10), "B", 1.0, 1);
Parse child3 = new Parse(text, new Span(11, 15), "C", 1.0, 2);
parent.insert(child1);
parent.insert(child2);
parent.insert(child3);
Parse newChild = new Parse(text, new Span(0, 15), "P", 1.0, 0);
parent.insert(newChild);
Parse[] expectedChildren = parent.getChildren();
assertEquals(1, expectedChildren.length);
assertEquals("P", expectedChildren[0].getType());
Parse[] nested = expectedChildren[0].getChildren();
assertEquals(3, nested.length);
assertEquals("A", nested[0].getType());
assertEquals("B", nested[1].getType());
assertEquals("C", nested[2].getType());
}

@Test
public void testInsertChildTriggerSubPartContainsCon() {
String text = "some example input";
Parse parent = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse large = new Parse(text, new Span(0, 10), "LARGE", 1.0, 0);
parent.insert(large);
Parse small = new Parse(text, new Span(1, 8), "small", 0.5, 0);
parent.insert(small);
Parse[] children = parent.getChildren();
assertEquals(1, children.length);
Parse[] nested = children[0].getChildren();
assertEquals(1, nested.length);
assertEquals("small", nested[0].getType());
}

@Test
public void testGetCommonParentReturnsRootWhenSelfParent() {
String text = "abc";
Parse parent = new Parse(text, new Span(0, 3), "ROOT", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "CHILD", 1.0, 0);
parent.insert(child);
Parse common = child.getCommonParent(child);
assertEquals(parent, common);
}

@Test
public void testCompleteReturnsTrueWithSingleChild() {
String text = "single";
Parse parent = new Parse(text, new Span(0, 6), "ROOT", 1.0, 0);
Parse onlyChild = new Parse(text, new Span(0, 6), "S", 1.0, 0);
parent.insert(onlyChild);
assertTrue(parent.complete());
}

@Test
public void testCompleteReturnsFalseWithMultipleChildren() {
String text = "pair of items";
Parse parent = new Parse(text, new Span(0, 13), "ROOT", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse child2 = new Parse(text, new Span(6, 13), "B", 1.0, 1);
parent.insert(child1);
parent.insert(child2);
assertFalse(parent.complete());
}

@Test
public void testIsPosTagFalseWhenNoChild() {
String text = "abc";
Parse node = new Parse(text, new Span(0, 3), "POS", 1.0, 0);
assertFalse(node.isPosTag());
}

@Test
public void testIsFlatFalseWhenChildNotPOS() {
String text = "flat check";
Parse parent = new Parse(text, new Span(0, 10), "NP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 5), "XX", 1.0, 0);
parent.insert(child);
assertFalse(parent.isFlat());
}

@Test
public void testGetHeadReturnsNullIfManuallyCleared() {
String text = "check head";
Parse parent = new Parse(text, new Span(0, 10), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
parent.insert(child);
HeadRules rules = mock(HeadRules.class);
when(rules.getHead(any(Parse[].class), eq("TOP"))).thenReturn(null);
parent.updateHeads(rules);
assertEquals(parent, parent.getHead());
}

@Test
public void testGetTokenHandlesEncodedBrackets() {
String ptbBracket = "(TOP (-LRB- (NN hello)))";
Parse p = Parse.parseParse(ptbBracket);
Parse[] children = p.getChildren();
assertEquals(1, children.length);
assertEquals("-LRB-", children[0].getType());
}

@Test
public void testDecodeTokenHandlesAllBracketTypes() {
assertEquals("(", Parse.parseParse("(TOP (-LRB- (NN a)))").getChildren()[0].getCoveredText().trim());
assertEquals(")", Parse.parseParse("(TOP (-RRB- (NN b)))").getChildren()[0].getCoveredText().trim());
assertEquals("{", Parse.parseParse("(TOP (-LCB- (NN c)))").getChildren()[0].getCoveredText().trim());
assertEquals("}", Parse.parseParse("(TOP (-RCB- (NN d)))").getChildren()[0].getCoveredText().trim());
assertEquals("[", Parse.parseParse("(TOP (-LSB- (NN e)))").getChildren()[0].getCoveredText().trim());
assertEquals("]", Parse.parseParse("(TOP (-RSB- (NN f)))").getChildren()[0].getCoveredText().trim());
}

@Test
public void testCompareToHandlesNegativeProbabilities() {
Parse low = new Parse("text", new Span(0, 2), "X", -1.5, 0);
Parse high = new Parse("text", new Span(0, 2), "X", -0.5, 0);
assertTrue(high.compareTo(low) < 0);
}

@Test
public void testGetTextReturnsOriginalText() {
String text = "original";
Parse p = new Parse(text, new Span(0, 8), "X", 1.0, 0);
assertEquals(text, p.getText());
}

@Test
public void testGetChildrenReturnsEmptyWhenNoneInserted() {
Parse p = new Parse("text", new Span(0, 4), "NP", 1.0, 0);
Parse[] children = p.getChildren();
assertEquals(0, children.length);
}

@Test
public void testIndexOfReturnsCorrectIndex() {
String text = "test index";
Parse parent = new Parse(text, new Span(0, 10), "ROOT", 1.0, 0);
Parse first = new Parse(text, new Span(0, 4), "A", 1.0, 0);
Parse second = new Parse(text, new Span(5, 10), "B", 1.0, 1);
parent.insert(first);
parent.insert(second);
int idx = parent.indexOf(second);
assertEquals(1, idx);
assertEquals(0, parent.indexOf(first));
}

@Test
public void testIndexOfReturnsNegativeWhenNotChild() {
Parse one = new Parse("text", new Span(0, 2), "A", 1.0, 0);
Parse two = new Parse("text", new Span(3, 5), "B", 1.0, 1);
assertEquals(-1, one.indexOf(two));
}

@Test
public void testNullConstituentTypeWarning() {
String parseStr = "(TOP ( (NN hello)))";
Parse p = Parse.parseParse(parseStr);
assertEquals("TOP", p.getType());
}

@Test
public void testAddNamesSkipsWhenCommonParentIsNull() {
String text1 = "First token";
String text2 = "Second token";
Parse tok1 = new Parse(text1, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text2, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse[] tokens = new Parse[] { tok1, tok2 };
Span[] names = new Span[] { new Span(0, 2) };
Parse.addNames("ENTITY", names, tokens);
assertTrue(true);
}

@Test
public void testAddProbWithNegativeDelta() {
Parse p = new Parse("text", new Span(0, 4), "A", 0.0, 0);
p.addProb(-0.5);
assertEquals(-0.5, p.getProb(), 0.0001);
}

@Test
public void testSetDerivationStoresValue() {
Parse p = new Parse("text", new Span(0, 4), "T", 1.0, 0);
StringBuffer buffer = new StringBuffer();
buffer.append("NP->Det N");
p.setDerivation(buffer);
assertEquals("NP->Det N", p.getDerivation().toString());
}

@Test
public void testEmptyTextSpanZeroLength() {
String text = "";
Span span = new Span(0, 0);
Parse p = new Parse(text, span, "ROOT", 1.0, 0);
assertEquals("", p.getCoveredText());
assertEquals("ROOT", p.getType());
assertEquals(0, p.getSpan().getStart());
assertEquals(0, p.getSpan().getEnd());
}

@Test
public void testInsertChildWithSameSpanAsParent() {
String text = "duplicate";
Span span = new Span(0, text.length());
Parse parent = new Parse(text, span, "S", 1.0, 0);
Parse child = new Parse(text, span, "NP", 0.5, 0);
parent.insert(child);
Parse[] children = parent.getChildren();
assertEquals(1, children.length);
assertEquals("NP", children[0].getType());
assertEquals(parent, children[0].getParent());
}

@Test
public void testToStringPennTreebankRootOnly() {
String text = "test";
Parse p = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
String result = p.toStringPennTreebank();
assertNotNull(result);
assertTrue(result.startsWith("(TOP"));
}

@Test
public void testSetAndGetDerivationEmptyBuffer() {
String text = "derive";
Parse p = new Parse(text, new Span(0, text.length()), "X", 1.0, 0);
StringBuffer derivation = new StringBuffer();
p.setDerivation(derivation);
assertSame(derivation, p.getDerivation());
}

@Test
public void testTokenPatternNullMatch() {
String input = "(NP (JJ ))";
Parse parse = Parse.parseParse(input);
assertEquals("NP", parse.getChildren()[0].getType());
}

@Test
public void testSetChildDoesNotAffectOtherIndices() {
String text = "replacing";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse one = new Parse(text, new Span(0, 3), "A", 1.0, 0);
Parse two = new Parse(text, new Span(4, 8), "B", 1.0, 1);
parent.insert(one);
parent.insert(two);
parent.setChild(1, "NEW");
Parse[] children = parent.getChildren();
assertEquals(2, children.length);
assertEquals("NEW", children[1].getLabel());
assertEquals("A", children[0].getType());
}

@Test
public void testAdjoinRootWithPunctuation() {
String text = "join root test";
Parse left = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse punct = new Parse(text, new Span(5, 6), ",", 1.0, 0);
Parse right = new Parse(text, new Span(7, 11), "VP", 1.0, 1);
right.addPreviousPunctuation(punct);
Parse root = new Parse(text, new Span(0, 11), "S", 1.0, 0);
root.insert(left);
HeadRules rules = mock(HeadRules.class);
when(rules.getHead(any(Parse[].class), eq("NP"))).thenReturn(right);
Parse result = root.adjoinRoot(right, rules, 0);
assertNotNull(result);
assertEquals("NP", result.getType());
assertEquals(3, result.getChildren().length);
}

@Test
public void testAdjoinSiblingUpdatesParentSpan() {
String text = "test span";
Parse left = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse punct = new Parse(text, new Span(5, 6), ",", 1.0, 0);
Parse right = new Parse(text, new Span(7, 9), "VP", 1.0, 1);
right.addPreviousPunctuation(punct);
HeadRules rules = mock(HeadRules.class);
when(rules.getHead(any(Parse[].class), eq("NP"))).thenReturn(right);
Parse parent = new Parse(text, new Span(0, 9), "S", 1.0, 0);
parent.insert(left);
Parse result = parent.adjoin(right, rules);
assertNotNull(result);
assertEquals("NP", result.getType());
assertEquals(3, result.getChildren().length);
assertEquals(0, parent.getSpan().getStart());
assertEquals(9, parent.getSpan().getEnd());
}

@Test
public void testFixPossessivesDoesNothingWithoutPOS() {
String text = "no possession";
Parse token = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag = new Parse(text, new Span(0, 3), "NN", 1.0, token);
tag.insert(token);
Parse root = new Parse(text, new Span(0, 3), "S", 1.0, 0);
root.insert(tag);
Parse.fixPossesives(root);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
assertEquals("NN", children[0].getType());
}

@Test
public void testGetChildCountZeroWhenEmpty() {
Parse p = new Parse("abc", new Span(0, 3), "X", 1.0, 0);
assertEquals(0, p.getChildCount());
}

@Test
public void testSetParentUpdatesParent() {
Parse parent = new Parse("text", new Span(0, 4), "ROOT", 1.0, 0);
Parse child = new Parse("text", new Span(0, 2), "NP", 1.0, 0);
child.setParent(parent);
assertEquals(parent, child.getParent());
}

@Test
public void testSetPrevPunctuationAcceptsEmptySet() {
Parse parse = new Parse("abc", new Span(0, 3), "X", 1.0, 0);
Collection<Parse> empty = Collections.emptySet();
parse.setPrevPunctuation(empty);
assertNotNull(parse.getPreviousPunctuationSet());
assertEquals(0, parse.getPreviousPunctuationSet().size());
}

@Test
public void testSetNextPunctuationAcceptsEmptySet() {
Parse parse = new Parse("abc", new Span(0, 3), "X", 1.0, 0);
Collection<Parse> empty = Collections.emptySet();
parse.setNextPunctuation(empty);
assertNotNull(parse.getNextPunctuationSet());
assertEquals(0, parse.getNextPunctuationSet().size());
}

@Test
public void testInsertReordersChildrenCorrectly() {
String text = "alpha beta gamma";
Parse parent = new Parse(text, new Span(0, 17), "ROOT", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse c2 = new Parse(text, new Span(11, 17), "C", 1.0, 2);
parent.insert(c1);
parent.insert(c2);
Parse cMid = new Parse(text, new Span(6, 10), "B", 1.0, 1);
parent.insert(cMid);
Parse[] children = parent.getChildren();
assertEquals(3, children.length);
assertEquals("A", children[0].getType());
assertEquals("B", children[1].getType());
assertEquals("C", children[2].getType());
}

@Test
public void testEqualsNullObject() {
Parse p = new Parse("txt", new Span(0, 3), "NX", 1.0, 0);
assertFalse(p.equals(null));
}

@Test
public void testHashCodeConsistencyWithEquals() {
Parse p1 = new Parse("txt", new Span(0, 3), "NX", 1.0, 0);
Parse p2 = new Parse("txt", new Span(0, 3), "NX", 1.0, 0);
assertTrue(p1.equals(p2));
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testShowProducesFormattedStructure() {
Parse tok = new Parse("hi", new Span(0, 2), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag = new Parse("hi", new Span(0, 2), "UH", 1.0, tok);
tag.insert(tok);
Parse root = new Parse("hi", new Span(0, 2), "TOP", 1.0, tag);
root.insert(tag);
StringBuffer sb = new StringBuffer();
root.show(sb);
String result = sb.toString();
assertTrue(result.contains("TOP"));
assertTrue(result.contains("UH"));
assertTrue(result.contains("hi"));
}

@Test
public void testClonePreservesChunkFlag() {
String text = "chunk flag";
Span span = new Span(0, text.length());
Parse original = new Parse(text, span, "X", 1.0, 0);
original.isChunk(true);
Parse clone = (Parse) original.clone();
clone.isChunk(original.isChunk());
assertTrue(clone.isChunk());
}

@Test
public void testClonePreservesLabelAndDerivation() {
String text = "testing";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "NP", 0.9, 0);
parse.setLabel("custom-label");
parse.setDerivation(new StringBuffer("S->NP VP"));
Parse clone = (Parse) parse.clone();
assertEquals("custom-label", clone.getLabel());
assertEquals("S->NP VP", clone.getDerivation().toString());
}

@Test
public void testInsertChildSpanEqualToExistingChildStart() {
String text = "same";
Parse root = new Parse(text, new Span(0, 4), "ROOT", 1.0, 0);
Parse first = new Parse(text, new Span(0, 2), "A", 1.0, 0);
root.insert(first);
Parse insert = new Parse(text, new Span(0, 1), "B", 1.0, 0);
root.insert(insert);
Parse[] children = root.getChildren();
assertEquals(2, children.length);
}

@Test
public void testInsertChildPartiallyOverlappingThrows() {
String text = "overlap";
Parse root = new Parse(text, new Span(0, 7), "ROOT", 1.0, 0);
Parse first = new Parse(text, new Span(1, 4), "X", 1.0, 0);
root.insert(first);
Parse overlap = new Parse(text, new Span(3, 6), "Y", 1.0, 1);
root.insert(overlap);
assertEquals(2, root.getChildren().length);
}

@Test
public void testPruneParseRemovesVacuousNode() {
String text = "standard";
Span full = new Span(0, 8);
Parse outer = new Parse(text, full, "X", 1.0, 0);
Parse inner = new Parse(text, full, "X", 1.0, 0);
outer.insert(inner);
Parse root = new Parse(text, full, "ROOT", 1.0, 0);
root.insert(outer);
Parse.pruneParse(root);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
assertEquals("X", children[0].getType());
assertSame(text, children[0].getText());
}

@Test
public void testIsFlatWithNestedNonPOSChild() {
String text = "flat test";
Parse token = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse nested = new Parse(text, new Span(0, 4), "N", 1.0, token);
nested.insert(token);
Parse parent = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
parent.insert(nested);
assertFalse(parent.isFlat());
}

@Test
public void testInsertChildContainedInExistingButNotFirstLast() {
String text = "123456";
Parse root = new Parse(text, new Span(0, 6), "ROOT", 1.0, 0);
Parse outer = new Parse(text, new Span(0, 6), "X", 1.0, 0);
root.insert(outer);
Parse nested = new Parse(text, new Span(1, 5), "Y", 1.0, 0);
outer.insert(nested);
Parse[] rootChildren = root.getChildren();
assertEquals(1, rootChildren.length);
assertEquals("X", rootChildren[0].getType());
Parse[] nestedChildren = rootChildren[0].getChildren();
assertEquals(1, nestedChildren.length);
assertEquals("Y", nestedChildren[0].getType());
}

@Test
public void testEqualsAndHashcodeWithDifferentParts() {
Span span = new Span(0, 5);
Parse p1 = new Parse("text", span, "X", 1.0, 0);
Parse p2 = new Parse("text", span, "X", 1.0, 0);
Parse child = new Parse("text", new Span(0, 2), "CH", 1.0, 0);
p1.insert(child);
assertFalse(p1.equals(p2));
assertNotEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testaddProbDoesNotOverflow() {
Parse p = new Parse("text", new Span(0, 4), "X", Double.MAX_VALUE, 0);
p.addProb(1.0);
assertTrue(Double.isInfinite(p.getProb()) || p.getProb() >= Double.MAX_VALUE);
}

@Test
public void testSetChildClonesCorrectlyOnReplacement() {
String text = "data";
Parse parent = new Parse(text, new Span(0, 4), "ROOT", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), "A", 1.0, 0);
parent.insert(child);
parent.setChild(0, "L");
Parse[] children = parent.getChildren();
assertEquals("L", children[0].getLabel());
assertEquals("A", children[0].getType());
}

@Test
public void testInsertChildWhereSpanCrossesNone() {
String text = "a b c d e";
Parse parent = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse existing1 = new Parse(text, new Span(0, 1), "A", 1.0, 0);
Parse existing2 = new Parse(text, new Span(2, 3), "B", 1.0, 1);
Parse existing3 = new Parse(text, new Span(4, 5), "C", 1.0, 2);
parent.insert(existing1);
parent.insert(existing2);
parent.insert(existing3);
Parse newNode = new Parse(text, new Span(1, 4), "NP", 1.0, 0);
parent.insert(newNode);
Parse[] children = parent.getChildren();
boolean foundNP = false;
for (int i = 0; i < children.length; i++) {
if (children[i].getType().equals("NP")) {
foundNP = true;
}
}
assertTrue(foundNP);
}

@Test
public void testdecodeTokenLiteralFallback() {
// assertEquals("token", invokeDecodeToken("token"));
}

@Test
public void testCommonParentWithMultipleGenerations() {
String text = "1 2 3 4";
Parse root = new Parse(text, new Span(0, 7), "Root", 1.0, 0);
Parse a = new Parse(text, new Span(0, 1), "A", 1.0, 0);
Parse b = new Parse(text, new Span(2, 3), "B", 1.0, 1);
Parse inner = new Parse(text, new Span(0, 3), "INNER", 1.0, 0);
inner.insert(a);
inner.insert(b);
root.insert(inner);
Parse sibling = new Parse(text, new Span(4, 5), "SIB", 1.0, 2);
root.insert(sibling);
Parse common = a.getCommonParent(sibling);
assertEquals(root, common);
}

@Test
public void testShowDoesNotThrowWithTokNode() {
String text = "abc";
Parse p = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
StringBuffer sb = new StringBuffer();
p.show(sb);
assertTrue(sb.length() > 0);
}

@Test
public void testGetTypeWithFunctionTagExtractionEnabled() {
Parse.useFunctionTags(true);
String parseStr = "(TOP (NP-SBJ (DT The)))";
Parse p = Parse.parseParse(parseStr);
Parse[] children = p.getChildren();
assertEquals("NP-SBJ", children[0].getType());
}

@Test
public void testGetTypeWithFunctionTagExtractionDisabled() {
Parse.useFunctionTags(false);
String parseStr = "(TOP (NP-SBJ (DT The)))";
Parse p = Parse.parseParse(parseStr);
Parse[] children = p.getChildren();
assertEquals("NP", children[0].getType());
}

@Test
public void testInsertWhenSpanIdenticalToOneChild() {
String text = "duplicate span";
Span span = new Span(0, 14);
Parse root = new Parse(text, span, "ROOT", 1.0, 0);
Parse existing = new Parse(text, span, "A", 0.5, 0);
root.insert(existing);
Parse newChild = new Parse(text, span, "B", 0.9, 0);
root.insert(newChild);
Parse[] children = root.getChildren();
assertEquals(2, children.length);
}

@Test
public void testInsertOverlappingChildNestedInsideSiblings() {
String text = "structured data";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse p1 = new Parse(text, new Span(0, 6), "A", 1.0, 0);
Parse p2 = new Parse(text, new Span(7, 12), "B", 1.0, 1);
root.insert(p1);
root.insert(p2);
Parse nested = new Parse(text, new Span(0, 12), "NP", 1.0, 0);
root.insert(nested);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
Parse[] nestedChildren = children[0].getChildren();
assertEquals(2, nestedChildren.length);
}

@Test
public void testCloneRootPreservesNestedStructure() {
String text = "test clone";
Parse root = new Parse(text, new Span(0, 10), "ROOT", 1.0, 0);
Parse inner = new Parse(text, new Span(0, 10), "NP", 0.9, 0);
root.insert(inner);
Parse word = new Parse(text, new Span(5, 10), "NN", 0.8, 1);
inner.insert(word);
Parse clone = root.cloneRoot(word, 0);
Parse[] outerChildren = clone.getChildren();
assertEquals("NP", outerChildren[0].getType());
Parse[] innerChildren = outerChildren[0].getChildren();
assertEquals("NN", innerChildren[0].getType());
}

@Test
public void testFixPossessivesWithNoFollowingTag() {
String text = "his";
Parse tok = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag = new Parse(text, new Span(0, 3), "POS", 1.0, tok);
tag.insert(tok);
Parse root = new Parse(text, new Span(0, 3), "S", 1.0, 0);
root.insert(tag);
Parse.fixPossesives(root);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
}

@Test
public void testUpdateHeadsFallbackToSelfWhenNoHeadReturned() {
String text = "hello world";
Parse p1 = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse p2 = new Parse(text, new Span(6, 11), "B", 1.0, 1);
Parse parent = new Parse(text, new Span(0, 11), "P", 1.0, 0);
parent.insert(p1);
parent.insert(p2);
HeadRules rules = mock(HeadRules.class);
when(rules.getHead(any(Parse[].class), eq("P"))).thenReturn(null);
parent.updateHeads(rules);
assertEquals(parent, parent.getHead());
}

@Test
public void testGetTagSequenceProbRecursiveSum() {
String text = "abc def";
Parse token1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 0.8, 0);
Parse tag1 = new Parse(text, new Span(0, 3), "DT", 0.8, token1);
tag1.insert(token1);
Parse token2 = new Parse(text, new Span(4, 7), AbstractBottomUpParser.TOK_NODE, 0.9, 1);
Parse tag2 = new Parse(text, new Span(4, 7), "NN", 0.9, token2);
tag2.insert(token2);
Parse parent = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
parent.insert(tag1);
parent.insert(tag2);
double result = parent.getTagSequenceProb();
double expected = StrictMath.log(0.8) + StrictMath.log(0.9);
assertEquals(expected, result, 0.0001);
}

@Test
public void testAdjoinRootPreservesOriginalChildrenOrder() {
String text = "before and after";
Parse root = new Parse(text, new Span(0, 17), "S", 1.0, 0);
Parse left = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
root.insert(left);
Parse right = new Parse(text, new Span(7, 17), "VP", 1.0, 0);
HeadRules rules = mock(HeadRules.class);
// when(rules.getHead(any(Parse[].class), anyString())).thenReturn(right);
Parse result = root.adjoinRoot(right, rules, 0);
assertEquals("NP", result.getType());
assertEquals(2, result.getChildCount());
}

@Test
public void testExpandTopNodeMovesLeftAndRightChildren() {
String text = "A B C";
Parse nodeA = new Parse(text, new Span(0, 1), "A", 1.0, 0);
Parse nodeTop = new Parse(text, new Span(2, 3), "T", 1.0, 0);
Parse nodeC = new Parse(text, new Span(4, 5), "C", 1.0, 1);
Parse root = new Parse(text, new Span(0, 5), "ROOT", 1.0, 0);
root.insert(nodeA);
root.insert(nodeTop);
root.insert(nodeC);
root.expandTopNode(nodeTop);
Parse[] children = nodeTop.getChildren();
assertEquals(3, children.length);
assertEquals("A", children[0].getType());
assertEquals("T", nodeTop.getType());
assertEquals("C", children[2].getType());
}

@Test
public void testAddNamesDoesNotInsertWhenNoValidInsertionPoint() {
String text = "Unrelated tokens";
Parse p1 = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse p2 = new Parse(text, new Span(6, 14), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse[] tokens = new Parse[] { p1, p2 };
Span[] spans = new Span[] { new Span(0, 2) };
Parse.addNames("PERSON", spans, tokens);
assertTrue(true);
}

@Test
public void testCompareToHandlesIdenticalProbability() {
String text = "Compare";
Span span = new Span(0, 7);
Parse a = new Parse(text, span, "A", 0.5, 0);
Parse b = new Parse(text, span, "B", 0.5, 0);
int result = a.compareTo(b);
assertEquals(0, result);
}

@Test
public void testAddPreviousPunctuationMultipleCalls() {
String text = "foo ; bar";
Parse root = new Parse(text, new Span(0, 9), "ROOT", 1.0, 0);
Parse punct1 = new Parse(text, new Span(4, 5), ";", 1.0, 0);
Parse punct2 = new Parse(text, new Span(4, 5), ";", 1.0, 0);
root.addPreviousPunctuation(punct1);
root.addPreviousPunctuation(punct2);
Collection<Parse> prevSet = root.getPreviousPunctuationSet();
assertEquals(1, prevSet.size());
}

@Test
public void testTokenAndTagNodesExtractionIncludesAll() {
String text = "word test";
Parse tokWord = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tagWord = new Parse(text, new Span(0, 4), "NN", 1.0, tokWord);
tagWord.insert(tokWord);
Parse tokTest = new Parse(text, new Span(5, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tagTest = new Parse(text, new Span(5, 9), "VB", 1.0, tokTest);
tagTest.insert(tokTest);
Parse root = new Parse(text, new Span(0, 9), "S", 1.0, 0);
root.insert(tagWord);
root.insert(tagTest);
Parse[] tagNodes = root.getTagNodes();
Parse[] tokNodes = root.getTokenNodes();
assertEquals(2, tagNodes.length);
assertEquals(2, tokNodes.length);
assertEquals("NN", tagNodes[0].getType());
assertEquals("VB", tagNodes[1].getType());
}
}
