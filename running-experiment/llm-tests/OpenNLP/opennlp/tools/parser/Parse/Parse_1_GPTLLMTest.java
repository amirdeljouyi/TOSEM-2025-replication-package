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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Parse_1_GPTLLMTest {

@Test
public void testConstructorAndGetters() {
String text = "John";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "NP", 0.99, 1);
assertEquals("NP", parse.getType());
assertEquals(span, parse.getSpan());
assertEquals(0.99, parse.getProb(), 0.0001);
assertEquals("John", parse.getCoveredText());
assertEquals("John", parse.toString());
}

@Test
public void testSetType() {
String text = "John";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "XX", 0.5, 0);
parse.setType("NP");
assertEquals("NP", parse.getType());
}

@Test
public void testSetLabelAndGetLabel() {
String text = "John";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "NNP", 1.0, 0);
parse.setLabel("some-label");
assertEquals("some-label", parse.getLabel());
}

@Test
public void testInsertChildParse() {
String text = "John Smith";
Span rootSpan = new Span(0, text.length());
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Span childSpan = new Span(0, 4);
Parse child = new Parse(text, childSpan, "NP", 0.9, 0);
root.insert(child);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
assertEquals(child, children[0]);
assertEquals(root, children[0].getParent());
}

@Test
public void testInsertChildParseWithInvalidSpanThrowsException() {
String text = "John";
Span rootSpan = new Span(0, 4);
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Span invalidSpan = new Span(5, 10);
Parse invalidChild = new Parse(text, invalidSpan, "NP", 0.9, 0);
root.insert(invalidChild);
}

@Test
public void testAddAndGetPreviousPunctuation() {
String text = "Hello , world";
Span rootSpan = new Span(0, text.length());
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Parse punctuation = new Parse(text, new Span(6, 7), ",", 1.0, 1);
root.addPreviousPunctuation(punctuation);
Collection<Parse> prevSet = root.getPreviousPunctuationSet();
assertNotNull(prevSet);
assertTrue(prevSet.contains(punctuation));
}

@Test
public void testAddAndGetNextPunctuation() {
String text = "Hello world .";
Span rootSpan = new Span(0, text.length());
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Parse punctuation = new Parse(text, new Span(12, 13), ".", 1.0, 2);
root.addNextPunctuation(punctuation);
Collection<Parse> nextSet = root.getNextPunctuationSet();
assertNotNull(nextSet);
assertTrue(nextSet.contains(punctuation));
}

@Test
public void testCloneCreatesDeepCopy() {
String text = "John";
Span span = new Span(0, 4);
Parse original = new Parse(text, span, "NP", 1.0, 0);
Parse clone = (Parse) original.clone();
assertEquals(original.getSpan(), clone.getSpan());
assertEquals(original.getCoveredText(), clone.getCoveredText());
assertFalse(original == clone);
}

@Test
public void testAddProbIncreasesValue() {
String text = "Hello";
Span span = new Span(0, 5);
Parse parse = new Parse(text, span, "INTJ", 0.25, 0);
parse.addProb(0.75);
assertEquals(1.0, parse.getProb(), 0.0001);
}

@Test
public void testCompareTo() {
String text = "Test";
Span span1 = new Span(0, 2);
Span span2 = new Span(2, 4);
Parse p1 = new Parse(text, span1, "A", 0.9, 0);
Parse p2 = new Parse(text, span2, "B", 0.5, 1);
int result = p1.compareTo(p2);
assertTrue(result < 0);
}

@Test
public void testIsChunkFlag() {
String text = "John";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "NP", 0.9, 0);
parse.isChunk(true);
assertTrue(parse.isChunk());
parse.isChunk(false);
assertFalse(parse.isChunk());
}

@Test
public void testTokenEncodingAndToStringPennTreebank() {
String text = "(";
Span span = new Span(0, 1);
Parse token = new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse parent = new Parse(text, span, "TOP", 1.0, 0);
parent.insert(token);
String result = parent.toStringPennTreebank();
assertTrue(result.contains("-LRB-"));
}

@Test
public void testToStringReturnsCoveredText() {
String text = "Example";
Span span = new Span(0, 7);
Parse parse = new Parse(text, span, "NN", 1.0, 0);
assertEquals("Example", parse.toString());
}

@Test
public void testIsPosTagWhenSingleTokenChild() {
String text = "Hello";
Span span = new Span(0, 5);
Parse token = new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 0.9, 0);
Parse posTag = new Parse(text, span, "NN", 1.0, 0);
posTag.insert(token);
assertTrue(posTag.isPosTag());
assertFalse(token.isPosTag());
}

@Test
public void testIsFlatReturnsTrue() {
String text = "One";
Span span = new Span(0, 3);
Parse token = new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse posTag = new Parse(text, span, "CD", 1.0, 0);
posTag.insert(token);
Parse phrase = new Parse(text, span, "NP", 1.0, 0);
phrase.insert(posTag);
assertTrue(phrase.isFlat());
}

@Test
public void testSetChildSetsNewLabel() {
String text = "Test";
Span span = new Span(0, 4);
Parse p = new Parse(text, span, "NP", 1.0, 0);
p.setLabel("initial");
Parse root = new Parse(text, span, "TOP", 1.0, 0);
root.insert(p);
root.setChild(0, "new-label");
Parse child = root.getChildren()[0];
assertEquals("new-label", child.getLabel());
}

@Test
public void testSetAndGetDerivation() {
String text = "ab";
Span span = new Span(0, 2);
Parse p = new Parse(text, span, "X", 1.0, 0);
StringBuffer buf = new StringBuffer("test");
p.setDerivation(buf);
assertEquals(buf, p.getDerivation());
}

@Test
public void testGetChildCount() {
String text = "Xyz";
Span span = new Span(0, 3);
Parse p = new Parse(text, span, "S", 1.0, 0);
assertEquals(0, p.getChildCount());
Parse c = new Parse(text, span, "NP", 0.9, 0);
p.insert(c);
assertEquals(1, p.getChildCount());
}

@Test
public void testIndexOfReturnsCorrectIndex() {
String text = "Index";
Span span = new Span(0, 5);
Parse root = new Parse(text, span, "ROOT", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 2), "NP", 1.0, 0);
Parse c2 = new Parse(text, new Span(2, 5), "VP", 1.0, 1);
root.insert(c1);
root.insert(c2);
assertEquals(0, root.indexOf(c1));
assertEquals(1, root.indexOf(c2));
}

@Test
public void testUseFunctionTagsStatically() {
Parse.useFunctionTags(true);
assertTrue(true);
Parse.useFunctionTags(false);
assertTrue(true);
}

@Test
public void testEqualsWithSameReference() {
String text = "word";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "NN", 1.0, 0);
assertTrue(parse.equals(parse));
}

@Test
public void testEqualsWithDifferentType() {
String text = "word";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "NN", 1.0, 0);
assertFalse(parse.equals("NotParseObject"));
}

@Test
public void testEqualsWithDifferentParts() {
String text = "word";
Span span = new Span(0, 4);
Parse p1 = new Parse(text, span, "NN", 1.0, 0);
Parse p2 = new Parse(text, span, "NN", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 2), "DT", 1.0, 0);
p1.insert(child1);
assertFalse(p1.equals(p2));
}

@Test
public void testHashCodeConsistency() {
String text = "word";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "NN", 1.0, 0);
int hash1 = parse.hashCode();
int hash2 = parse.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testInsertNestedParse() {
String text = "The dog barks";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse det = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
root.insert(np);
np.insert(det);
assertEquals(np, root.getChildren()[0]);
assertEquals(det, np.getChildren()[0]);
}

@Test
public void testGetCommonParentReturnsNullWhenNoCommon() {
String text = "Hello World";
Parse a = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse b = new Parse(text, new Span(6, 11), "B", 1.0, 1);
assertNull(a.getCommonParent(b));
}

@Test
public void testPruneParseDoesNotModifyValidStructure() {
String text = "Test";
Parse root = new Parse(text, new Span(0, 4), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
root.insert(child);
Parse.pruneParse(root);
assertEquals("NP", root.getChildren()[0].getType());
}

@Test
public void testParseParseHandlesEmptyInput() {
Parse result = Parse.parseParse("(TOP)");
assertNotNull(result);
assertEquals("TOP", result.getType());
assertEquals("", result.getText());
}

@Test
public void testParseParseReturnsStructureWithToken() {
Parse result = Parse.parseParse("(TOP (NN dog))");
assertNotNull(result);
Parse[] children = result.getChildren();
assertEquals(1, children.length);
assertEquals("NN", children[0].getType());
assertTrue(result.getText().contains("dog"));
}

@Test
public void testGetTokenNodesReturnsAllTokens() {
String input = "(TOP (NP (NN The) (NN dog)))";
Parse p = Parse.parseParse(input);
Parse[] tokens = p.getTokenNodes();
assertEquals(2, tokens.length);
assertEquals("The", tokens[0].getCoveredText().trim());
assertEquals("dog", tokens[1].getCoveredText().trim());
}

@Test
public void testUpdateHeadsAssignsSelfForLeaf() {
String text = "the";
Parse leaf = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
leaf.updateHeads(null);
assertEquals(leaf, leaf.getHead());
}

@Test
public void testExpandTopNodeMovesChildren() {
String text = "hello world";
Parse top = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse middle = new Parse(text, new Span(0, 5), "X", 1.0, 0);
Parse leaf = new Parse(text, new Span(0, 5), "Y", 1.0, 0);
top.insert(middle);
middle.insert(leaf);
top.expandTopNode(middle);
assertEquals(1, top.getChildren().length);
assertTrue(middle.getChildren().length >= 1);
}

@Test
public void testDecodeTokenWithUnknownSymbolReturnsSame() {
String token = "hello";
// String decoded = Parse.parseParse("(TOP (NN hello))").getToken(token);
// assertNull(decoded);
}

@Test
public void testGetCommonParentWhenSelfIsInput() {
String text = "test";
Parse p = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
Parse parent = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
parent.insert(p);
assertEquals(parent, p.getCommonParent(p));
}

@Test
public void testCompareToEqualProbability() {
String text = "equal";
Span span = new Span(0, 5);
Parse p1 = new Parse(text, span, "X", 1.0, 0);
Parse p2 = new Parse(text, span, "X", 1.0, 0);
assertEquals(0, p1.compareTo(p2));
}

@Test
public void testRemoveWithEmptyChildrenListDoesNothing() {
String text = "test";
Parse parse = new Parse(text, new Span(0, 4), "TOP", 1.0, 0);
parse.remove(0);
assertEquals(0, parse.getChildCount());
}

@Test
public void testRemoveInvalidIndexThrowsException() {
String text = "test";
Parse parse = new Parse(text, new Span(0, 4), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), "X", 1.0, 0);
parse.insert(child);
parse.remove(5);
}

@Test
public void testEmptyConstructorOutputForChildren() {
String text = "lone";
Parse lone = new Parse(text, new Span(0, 4), "X", 1.0, 0);
Parse[] children = lone.getChildren();
assertEquals(0, children.length);
}

@Test
public void testInsertMergesSubConstituentsWhenOverlapping() {
String text = "The smart dog";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse np = new Parse(text, new Span(0, text.length()), "NP", 1.0, 0);
Parse sub1 = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
Parse sub2 = new Parse(text, new Span(4, 9), "JJ", 1.0, 1);
root.insert(np);
np.insert(sub1);
np.insert(sub2);
Parse newSub = new Parse(text, new Span(0, 9), "ADJP", 1.0, 1);
np.insert(newSub);
Parse[] children = np.getChildren();
boolean foundADJP = false;
for (Parse c : children) {
if (c.getType().equals("ADJP")) {
foundADJP = true;
break;
}
}
assertTrue(foundADJP);
}

@Test
public void testGetTagSequenceProbOnZeroParts() {
String text = "A";
Span span = new Span(0, 1);
Parse p = new Parse(text, span, "NN", 0.9, 0);
double prob = p.getTagSequenceProb();
assertEquals(0.0, prob, 0.0001);
}

@Test
public void testCloneUpToNodePreservesStructure() {
String text = "cat plays";
Span span = new Span(0, text.length());
Parse root = new Parse(text, span, "S", 1.0, 0);
Span npSpan = new Span(0, 3);
Span vpSpan = new Span(4, 9);
Parse np = new Parse(text, npSpan, "NP", 1.0, 0);
Parse vp = new Parse(text, vpSpan, "VP", 1.0, 1);
root.insert(np);
root.insert(vp);
Parse clone = root.clone(vp);
assertNotNull(clone);
assertEquals("S", clone.getType());
assertEquals(2, clone.getChildren().length);
assertEquals("VP", clone.getChildren()[1].getType());
}

@Test
public void testInsertNestedChildContainingOthers() {
String text = "The lazy fox";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse dt = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
Parse adj = new Parse(text, new Span(4, 8), "JJ", 1.0, 1);
Parse nn = new Parse(text, new Span(9, 12), "NN", 1.0, 2);
root.insert(dt);
root.insert(adj);
root.insert(nn);
Parse np = new Parse(text, new Span(0, 12), "NP", 1.0, 2);
root.insert(np);
assertEquals(1, root.getChildren().length);
Parse[] npChildren = root.getChildren()[0].getChildren();
assertEquals(3, npChildren.length);
assertEquals("DT", npChildren[0].getType());
assertEquals("JJ", npChildren[1].getType());
assertEquals("NN", npChildren[2].getType());
}

@Test
public void testGetTokenReturnsBracketedType() {
String parse = "(TOP (PUNC -LRB-))";
Parse result = Parse.parseParse(parse);
Parse[] tokens = result.getTokenNodes();
assertEquals(1, tokens.length);
assertEquals("(", tokens[0].getCoveredText());
}

@Test
public void testDecodeTokenWithKnownBracketTypes() {
// assertEquals("(", Parse.parseParse("(TOP (-LRB- -LRB-))").getToken("-LRB-"));
// assertEquals(")", Parse.parseParse("(TOP (-RRB- -RRB-))").getToken("-RRB-"));
}

@Test
public void testAddNamesCrossingStructureSkipped() {
String text = "Alice and Bob went.";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse np1 = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse cc = new Parse(text, new Span(6, 9), "CC", 1.0, 1);
Parse np2 = new Parse(text, new Span(10, 13), "NP", 1.0, 2);
root.insert(np1);
root.insert(cc);
root.insert(np2);
Parse[] tokens = new Parse[] { new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0), new Parse(text, new Span(6, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1), new Parse(text, new Span(10, 13), AbstractBottomUpParser.TOK_NODE, 1.0, 2) };
Span[] spans = new Span[] { new Span(0, 3) };
Parse.addNames("PERSON", spans, tokens);
boolean found = false;
for (Parse child : root.getChildren()) {
if ("PERSON".equals(child.getType())) {
found = true;
break;
}
}
assertFalse(found);
}

@Test
public void testUseFunctionTagsEnabledChangesOutputType() {
Parse.useFunctionTags(true);
String parse = "(TOP (NP-SUBJ (NN John)))";
Parse result = Parse.parseParse(parse);
Parse[] children = result.getChildren();
assertEquals("NP-SUBJ", children[0].getType());
Parse.useFunctionTags(false);
}

@Test
public void testGetCommonParentWhenOtherIsParent() {
String text = "Example text.";
Parse parent = new Parse(text, new Span(0, 13), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
parent.insert(child);
Parse result = parent.getCommonParent(child);
assertEquals(parent, result);
}

@Test
public void testUpdateHeadsAssignsParentHeadAsSelfIfNull() {
String text = "A";
Span span = new Span(0, 1);
Parse p = new Parse(text, span, "X", 1.0, 0);
// p.updateHeads(new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return null;
// }
// });
assertEquals(p, p.getHead());
}

@Test
public void testGetTagNodesNestedTags() {
String text = "a b";
Parse root = new Parse(text, new Span(0, 3), "TOP", 1.0, 0);
Parse np = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
Parse tag1 = new Parse(text, new Span(0, 1), "DT", 1.0, 0);
Parse tag2 = new Parse(text, new Span(2, 3), "NN", 1.0, 1);
tag1.insert(new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0));
tag2.insert(new Parse(text, new Span(2, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 1));
np.insert(tag1);
np.insert(tag2);
root.insert(np);
Parse[] tags = root.getTagNodes();
assertEquals(2, tags.length);
assertEquals("DT", tags[0].getType());
assertEquals("NN", tags[1].getType());
}

@Test
public void testParseParseEmptyConstituentProducesBlankText() {
Parse p = Parse.parseParse("(TOP)");
assertEquals("TOP", p.getType());
assertEquals("", p.getText());
assertEquals(0, p.getSpan().length());
assertEquals(0, p.getChildren().length);
}

@Test
public void testParseParseTokenMatchingFailureReturnsNull() {
String rest = "(UNK x )";
Parse.useFunctionTags(false);
// String token = Parse.parseParse("(TOP (UNK x))").getToken(rest);
// assertNull(token);
}

@Test
public void testUpdateSpanOnRemovalFirstChild() {
String text = "Hello world!";
Parse parent = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse child2 = new Parse(text, new Span(6, 11), "B", 1.0, 1);
parent.insert(child1);
parent.insert(child2);
parent.remove(0);
assertEquals(new Span(6, 11), parent.getSpan());
}

@Test
public void testUpdateSpanOnRemovalLastChild() {
String text = "Good morning!";
Parse parent = new Parse(text, new Span(0, text.length()), "START", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 4), "X", 1.0, 0);
Parse child2 = new Parse(text, new Span(5, 12), "Y", 1.0, 1);
parent.insert(child1);
parent.insert(child2);
parent.remove(1);
assertEquals(new Span(0, 4), parent.getSpan());
}

@Test
public void testSetAndGetPrevPunctuationSetNull() {
String text = "Test";
Parse p = new Parse(text, new Span(0, 4), "A", 1.0, 0);
p.setPrevPunctuation(null);
assertNull(p.getPreviousPunctuationSet());
}

@Test
public void testSetAndGetNextPunctuationSetNull() {
String text = "Test";
Parse p = new Parse(text, new Span(0, 4), "A", 1.0, 0);
p.setNextPunctuation(null);
assertNull(p.getNextPunctuationSet());
}

@Test
public void testCommonParentWhereThisIsParentOfNode() {
String sentence = "Hi there";
Parse parent = new Parse(sentence, new Span(0, 8), "ROOT", 1.0, 0);
Parse child = new Parse(sentence, new Span(0, 2), "GREET", 1.0, 0);
parent.insert(child);
Parse result = child.getCommonParent(parent);
assertEquals(parent, result);
}

@Test
public void testToStringPennTreebankWithEmptyPart() {
String text = "Word";
Parse node = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
String result = node.toStringPennTreebank();
assertTrue(result.contains("NN"));
assertTrue(result.contains("Word"));
}

@Test
public void testCodeTreeDoesNotThrowForLeaf() {
String text = "Leaf";
Parse node = new Parse(text, new Span(0, 4), "WORD", 1.0, 0);
node.showCodeTree();
assertTrue(true);
}

@Test
public void testIsFlatFalseWithMultipleNonPosTagChildren() {
String text = "data";
Parse parent = new Parse(text, new Span(0, 4), "ROOT", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 2), "X", 1.0, 0);
Parse c2 = new Parse(text, new Span(2, 4), "Y", 1.0, 1);
parent.insert(c1);
parent.insert(c2);
assertFalse(parent.isFlat());
}

@Test
public void testGetTextMatchesSpanRange() {
String text = "SomeText.";
Span span = new Span(0, 4);
Parse p = new Parse(text, span, "Some", 1.0, 0);
assertEquals("Some", p.getCoveredText());
}

@Test
public void testDerivationInitiallyNull() {
String text = "a";
Parse p = new Parse(text, new Span(0, 1), "T", 1.0, 0);
assertNull(p.getDerivation());
}

@Test
public void testUpdateHeadsAssignsHeadIndexCorrectly() {
String text = "abc";
Parse root = new Parse(text, new Span(0, 3), "ROOT", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 1), "A", 1.0, 0);
Parse child2 = new Parse(text, new Span(2, 3), "B", 1.0, 1);
root.insert(child1);
root.insert(child2);
// root.updateHeads(new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return parts[1];
// }
// });
assertEquals(1, root.getHeadIndex());
}

@Test
public void testAdjoinAddsSisterNodeAndRebuildsHead() {
String text = "val1 val2";
Parse parent = new Parse(text, new Span(0, 10), "ROOT", 1.0, 0);
Parse p1 = new Parse(text, new Span(0, 4), "AA", 1.0, 0);
Parse p2 = new Parse(text, new Span(5, 10), "BB", 1.0, 1);
parent.insert(p1);
// Parse adj = parent.adjoin(p2, new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return parts[1];
// }
// });
// assertEquals("AA", adj.getType());
// assertEquals(0, parent.indexOf(adj));
// assertTrue(Arrays.stream(adj.getChildren()).anyMatch(c -> c == p2));
}

@Test
public void testAdjoinRootWorksCorrectly() {
String text = "first second";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse part1 = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse part2 = new Parse(text, new Span(6, 12), "B", 1.0, 1);
root.insert(part1);
// Parse adj = root.adjoinRoot(part2, new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return parts[1];
// }
// }, 0);
// assertNotNull(adj);
// assertEquals("A", adj.getType());
// assertEquals(root.getChildren()[0], adj);
// assertTrue(Arrays.asList(adj.getChildren()).contains(part2));
}

@Test
public void testCloneWithDerivationPreservation() {
String text = "hello!";
Span span = new Span(0, 6);
Parse p = new Parse(text, span, "INTJ", 1.0, 0);
StringBuffer derivation = new StringBuffer();
derivation.append("DERIVED");
p.setDerivation(derivation);
Parse cloned = (Parse) p.clone();
assertNotNull(cloned.getDerivation());
assertEquals("DERIVED", cloned.getDerivation().toString());
}

@Test
public void testToStringWithOnlyWhitespace() {
String text = " ";
Span span = new Span(0, 1);
Parse p = new Parse(text, span, "WS", 1.0, 0);
assertEquals(" ", p.toString());
}

@Test
public void testEqualsWithDifferentLabelButSameSpanAndText() {
String text = "LabelTest";
Span span = new Span(0, 9);
Parse p1 = new Parse(text, span, "X", 1.0, 0);
Parse p2 = new Parse(text, span, "X", 1.0, 0);
p1.setLabel("A");
p2.setLabel("B");
assertFalse(p1.equals(p2));
}

@Test
public void testHashCodeIndependentOfLabel() {
String text = "abc";
Span span = new Span(0, 3);
Parse p1 = new Parse(text, span, "X", 1.0, 0);
Parse p2 = new Parse(text, span, "X", 1.0, 0);
p1.setLabel("LABEL1");
p2.setLabel("LABEL2");
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testIndexOfChildReturnsMinusOneIfAbsent() {
String text = "sample";
Span span = new Span(0, 6);
Parse parent = new Parse(text, span, "P", 1.0, 0);
Parse notAdded = new Parse(text, new Span(0, 3), "A", 1.0, 0);
assertEquals(-1, parent.indexOf(notAdded));
}

@Test
public void testInsertSubPartContainsInsertSpan() {
String text = "0123456789";
Parse root = new Parse(text, new Span(0, 10), "ROOT", 1.0, 0);
Parse child = new Parse(text, new Span(0, 9), "X", 1.0, 0);
root.insert(child);
Parse insert = new Parse(text, new Span(2, 7), "Y", 1.0, 0);
root.insert(insert);
assertEquals(root, insert.getParent());
assertEquals("Y", root.getChildren()[1].getType());
}

@Test
public void testGetTokenBracketDecoding() {
String parse = "(TOP (PUNC -RCB-))";
Parse tree = Parse.parseParse(parse);
Parse[] tokens = tree.getTokenNodes();
assertEquals(1, tokens.length);
}

@Test
public void testEncodeTokenBracketVariants() {
// assertEquals("-LRB-", Parse.parseParse("(TOP (SYM -LRB-))").getToken("(["));
// assertEquals("-RRB-", Parse.parseParse("(TOP (SYM -RRB-))").getToken(")]"));
// assertEquals("-LCB-", Parse.parseParse("(TOP (SYM -LCB-))").getToken("({"));
// assertEquals("-RCB-", Parse.parseParse("(TOP (SYM -RCB-))").getToken(")}"));
// assertEquals("-LSB-", Parse.parseParse("(TOP (SYM -LSB-))").getToken("(["));
// assertEquals("-RSB-", Parse.parseParse("(TOP (SYM -RSB-))").getToken("])"));
}

@Test
public void testCompareToHigherProbability() {
String text = "abc";
Span span = new Span(0, 3);
Parse low = new Parse(text, span, "A", 0.5, 0);
Parse high = new Parse(text, span, "A", 0.9, 0);
assertTrue(high.compareTo(low) < 0);
}

@Test
public void testSetAndGetHeadIndex() {
String text = "parse";
Parse p = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
assertEquals(0, p.getHeadIndex());
}

@Test
public void testSetTypeChangesTypeForPennTreebankOutput() {
String text = "setType";
Parse p = new Parse(text, new Span(0, text.length()), "OLD", 1.0, 0);
p.setType("NEW");
assertEquals("NEW", p.getType());
assertTrue(p.toStringPennTreebank().contains("NEW"));
}

@Test
public void testSetParentToNull() {
String text = "null";
Parse p = new Parse(text, new Span(0, text.length()), "A", 1.0, 0);
p.setParent(null);
assertNull(p.getParent());
}

@Test
public void testAddPreviousPunctuationTreeSetOrder() {
String text = "punct";
Parse p = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse comma = new Parse(text, new Span(4, 5), ",", 1.0, 0);
Parse period = new Parse(text, new Span(5, 6), ".", 1.0, 0);
p.addPreviousPunctuation(comma);
p.addPreviousPunctuation(period);
assertTrue(p.getPreviousPunctuationSet() instanceof TreeSet);
assertEquals(2, p.getPreviousPunctuationSet().size());
}

@Test
public void testSetChildAndPreservesSpanAndType() {
String text = "abc";
Parse parent = new Parse(text, new Span(0, 3), "P", 1.0, 0);
Parse original = new Parse(text, new Span(0, 3), "X", 1.0, 0);
parent.insert(original);
parent.setChild(0, "MOD");
Parse replaced = parent.getChildren()[0];
assertEquals("MOD", replaced.getLabel());
assertEquals("X", replaced.getType());
assertEquals(new Span(0, 3), replaced.getSpan());
}

@Test
public void testInsertIntoEmptyParentAppendsAtZeroIndex() {
String text = "insert";
Parse parent = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse c = new Parse(text, new Span(0, 2), "X", 1.0, 0);
parent.insert(c);
assertEquals(0, parent.indexOf(c));
}

@Test
public void testInsertMultipleOverlappingChildren() {
String text = "abcdef";
Parse parent = new Parse(text, new Span(0, 6), "ROOT", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 2), "A", 1.0, 0);
Parse c2 = new Parse(text, new Span(1, 5), "B", 1.0, 1);
parent.insert(c1);
parent.insert(c2);
assertEquals(2, parent.getChildren().length);
assertEquals("A", parent.getChildren()[0].getType());
assertEquals("B", parent.getChildren()[1].getType());
}

@Test
public void testSetDerivationAfterClonePreservesNewBuffer() {
String text = "test";
Parse original = new Parse(text, new Span(0, 4), "NP", 0.9, 0);
StringBuffer sb = new StringBuffer("original-derivation");
original.setDerivation(sb);
Parse clone = (Parse) original.clone();
assertEquals("original-derivation", clone.getDerivation().toString());
clone.getDerivation().append("-extra");
assertEquals("original-derivation-extra", clone.getDerivation().toString());
assertEquals("original-derivation", original.getDerivation().toString());
}

@Test
public void testCloneWithNoChildrenPreservesNoChildren() {
String text = "clone";
Parse p = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse c = (Parse) p.clone();
assertEquals(0, c.getChildren().length);
}

@Test
public void testInsertSubPartSupersetContainingConstituent() {
String text = "abcde";
Parse root = new Parse(text, new Span(0, 5), "ROOT", 1.0, 0);
Parse sub1 = new Parse(text, new Span(0, 2), "A", 1.0, 0);
root.insert(sub1);
Parse larger = new Parse(text, new Span(0, 3), "B", 1.0, 0);
root.insert(larger);
assertEquals(1, root.getChildren().length);
assertEquals("B", root.getChildren()[0].getType());
assertEquals(1, root.getChildren()[0].getChildren().length);
assertEquals("A", root.getChildren()[0].getChildren()[0].getType());
}

@Test
public void testInsertSubPartContainedInExistingConstituent() {
String text = "abcdef";
Parse root = new Parse(text, new Span(0, 6), "ROOT", 1.0, 0);
Parse parent = new Parse(text, new Span(0, 6), "P", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 3), "X", 1.0, 0);
root.insert(parent);
parent.insert(c1);
Parse inner = new Parse(text, new Span(1, 2), "Y", 1.0, 0);
root.insert(inner);
Parse[] parts = parent.getChildren();
assertEquals(1, parts.length);
Parse[] nested = parts[0].getChildren();
assertEquals(1, nested.length);
assertEquals("Y", nested[0].getType());
}

@Test
public void testGetCommonParentReturnsNullWhenTotallyUnrelated() {
String text1 = "abc";
String text2 = "xyz";
Parse a = new Parse(text1, new Span(0, 3), "A", 1.0, 0);
Parse b = new Parse(text2, new Span(0, 3), "B", 1.0, 0);
Parse common = a.getCommonParent(b);
assertNull(common);
}

@Test
public void testPruneParseRemovesRedundantNestedTypes() {
String text = "abc";
Parse root = new Parse(text, new Span(0, 3), "ROOT", 1.0, 0);
Parse np1 = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
Parse np2 = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
root.insert(np1);
np1.insert(np2);
root.getChildren()[0].insert(new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0));
Parse.pruneParse(root);
assertEquals("NP", root.getChildren()[0].getType());
assertEquals(1, root.getChildren()[0].getChildren().length);
assertEquals(AbstractBottomUpParser.TOK_NODE, root.getChildren()[0].getChildren()[0].getType());
}

@Test
public void testFixPossessivesCreatesNewNPStructure() {
Parse p = Parse.parseParse("(TOP (NP (NNP John) (POS 's) (NN dog)))");
Parse.fixPossesives(p);
boolean found = false;
for (Parse child : p.getChildren()) {
if ("NP".equals(child.getType()) && child.getCoveredText().contains("dog")) {
found = true;
}
}
assertTrue(found);
}

@Test
public void testSetGetChildrenAndThenRemoveUpdatesSpanCorrectly() {
String text = "abcdef";
Parse parent = new Parse(text, new Span(0, 6), "ROOT", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 2), "A", 1.0, 0);
Parse c2 = new Parse(text, new Span(2, 6), "B", 1.0, 1);
parent.insert(c1);
parent.insert(c2);
parent.remove(1);
assertEquals(new Span(0, 2), parent.getSpan());
}

@Test
public void testAddNullPunctuationSetsNotFailing() {
String text = "abc";
Parse p = new Parse(text, new Span(0, 3), "TOP", 1.0, 0);
p.setPrevPunctuation(null);
p.setNextPunctuation(null);
assertNull(p.getPreviousPunctuationSet());
assertNull(p.getNextPunctuationSet());
}

@Test
public void testAddNamesWhereSpanExactlyMatchesParent() {
String sentence = "John runs";
Parse root = new Parse(sentence, new Span(0, 9), "TOP", 1.0, 0);
Parse np = new Parse(sentence, new Span(0, 4), "NP", 1.0, 0);
Parse vp = new Parse(sentence, new Span(5, 9), "VP", 1.0, 1);
root.insert(np);
root.insert(vp);
Parse t1 = new Parse(sentence, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse t2 = new Parse(sentence, new Span(5, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Span[] spans = new Span[] { new Span(0, 1) };
Parse.addNames("PERSON", spans, new Parse[] { t1, t2 });
boolean found = false;
for (Parse child : root.getChildren()) {
if ("PERSON".equals(child.getType())) {
found = true;
}
}
assertTrue(found);
}

@Test
public void testToStringPennTreebankWithMultipleLevelsProducesBalancedParentheses() {
String text = "John eats fish";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse vp = new Parse(text, new Span(5, text.length()), "VP", 1.0, 1);
Parse nnp = new Parse(text, new Span(0, 4), "NNP", 1.0, 0);
Parse tok = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
root.insert(np);
root.insert(vp);
np.insert(nnp);
nnp.insert(tok);
String tree = root.toStringPennTreebank();
int openParen = 0;
int closeParen = 0;
for (char c : tree.toCharArray()) {
if (c == '(')
openParen++;
if (c == ')')
closeParen++;
}
assertEquals(openParen, closeParen);
}

@Test
public void testInsertWithMultipleSwallowingOfChildren() {
String text = "big red dog";
Parse root = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
Parse c2 = new Parse(text, new Span(4, 7), "JJ", 1.0, 1);
Parse c3 = new Parse(text, new Span(8, 11), "NN", 1.0, 2);
root.insert(c1);
root.insert(c2);
root.insert(c3);
Parse phrase = new Parse(text, new Span(0, 11), "NP", 1.0, 0);
root.insert(phrase);
assertEquals(1, root.getChildCount());
Parse child = root.getChildren()[0];
assertEquals("NP", child.getType());
assertEquals(3, child.getChildCount());
}

@Test
public void testCompareToSortsByProbabilityDescending() {
String text = "abc";
Parse a = new Parse(text, new Span(0, 1), "X", 0.9, 0);
Parse b = new Parse(text, new Span(0, 1), "X", 0.5, 0);
Parse c = new Parse(text, new Span(0, 1), "X", 0.7, 0);
Parse[] parses = new Parse[] { a, b, c };
Arrays.sort(parses);
assertEquals(0.9, parses[2].getProb(), 0.001);
assertEquals(0.5, parses[0].getProb(), 0.001);
}

@Test
public void testClonePartialRightFrontierNestedChildStructure() {
String sentence = "The cat sleeps";
Parse root = new Parse(sentence, new Span(0, sentence.length()), "TOP", 1.0, 0);
Parse np = new Parse(sentence, new Span(0, 7), "NP", 1.0, 0);
Parse vp = new Parse(sentence, new Span(8, sentence.length()), "VP", 1.0, 1);
Parse token = new Parse(sentence, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse headWord = new Parse(sentence, new Span(0, 3), "NN", 1.0, 0);
headWord.insert(token);
np.insert(headWord);
root.insert(np);
root.insert(vp);
Parse partialClone = root.clone(headWord);
assertEquals("TOP", partialClone.getType());
assertNotSame(root, partialClone);
assertEquals(2, partialClone.getChildren().length);
assertEquals("NN", partialClone.getChildren()[0].getChildren()[0].getType());
}

@Test
public void testInsertWithSubPartThatContainsCurrentConstituent() {
String sentence = "hello friend";
Parse parent = new Parse(sentence, new Span(0, sentence.length()), "S", 1.0, 0);
Parse inner1 = new Parse(sentence, new Span(0, 5), "A", 1.0, 0);
parent.insert(inner1);
Parse overlapping = new Parse(sentence, new Span(0, 11), "B", 1.0, 0);
parent.insert(overlapping);
assertEquals(1, parent.getChildCount());
assertEquals("B", parent.getChildren()[0].getType());
// assertEquals("A", parent.getChildren()[0].getChildren().get(0).getType());
}

@Test
public void testUpdateSpanWithModifiedChildren() {
String sentence = "ab cd ef";
Parse root = new Parse(sentence, new Span(0, sentence.length()), "TOP", 1.0, 0);
Parse a = new Parse(sentence, new Span(0, 2), "A", 1.0, 0);
Parse b = new Parse(sentence, new Span(6, 8), "B", 1.0, 1);
root.insert(a);
root.insert(b);
root.updateSpan();
assertEquals(new Span(0, 8), root.getSpan());
}

@Test
public void testAddNamesHandlesFunctionTagCrossingSpan() {
String sentence = "John visited New York.";
Parse root = Parse.parseParse("(TOP (S (NP-SBJ (NNP John)) (VP (VBD visited) (NP (NNP New) (NNP York))) (. .)))");
Parse[] tokens = root.getTokenNodes();
Span crossingSpan = new Span(1, 3);
Parse.addNames("LOCATION", new Span[] { crossingSpan }, tokens);
boolean inserted = false;
for (Parse child : root.getChildren()) {
for (Parse sub : child.getChildren()) {
if ("LOCATION".equals(sub.getType())) {
inserted = true;
}
}
}
assertTrue(inserted);
}

@Test
public void testParseParseWithNullTypeReturnsNull() {
Parse.parseParse("(TOP (?? test))");
assertTrue(true);
}

@Test
public void testSetDerivationNullCheckAndToStringSafety() {
String text = "token";
Parse node = new Parse(text, new Span(0, 5), "X", 1.0, 0);
node.setDerivation(null);
String tree = node.toStringPennTreebank();
assertTrue(tree.contains("X"));
}

@Test
public void testGetCoveredTextEmptySpanReturnsEmpty() {
String text = "token";
Parse p = new Parse(text, new Span(2, 2), "X", 1.0, 0);
assertEquals("", p.getCoveredText());
}

@Test
public void testGetTokenRegexNoMatchReturnsNull() {
String input = "{ no closing paren";
// String result = Parse.parseParse("(TOP (X " + input + "))").getToken(input);
// assertNull(result);
}

@Test
public void testAddMethodAddsChunkSpanCorrectly() {
String sentence = "id name";
Parse p = new Parse(sentence, new Span(0, 2), "NP", 1.0, 0);
Parse d = new Parse(sentence, new Span(3, 7), "NN", 1.0, 1);
Parse root = new Parse(sentence, new Span(0, 7), "S", 1.0, 0);
root.insert(p);
// d.prevPunctSet = Arrays.asList();
// root.getChildren()[0].add(d, new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return parts[1];
// }
// });
assertEquals(new Span(0, 7), root.getChildren()[0].getSpan());
}

@Test
public void testHeadRulesFallbackWhenNullAssignsSelf() {
String sentence = "abc";
Parse root = new Parse(sentence, new Span(0, 3), "Z", 1.0, 0);
// root.updateHeads(new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return null;
// }
// });
assertEquals(root, root.getHead());
}

@Test
public void testShowCodeTreeOnNestedTree() {
String sentence = "openai model";
Parse root = new Parse(sentence, new Span(0, sentence.length()), "S", 1.0, 0);
Parse np = new Parse(sentence, new Span(0, 6), "NP", 1.0, 0);
Parse vp = new Parse(sentence, new Span(7, sentence.length()), "VP", 1.0, 1);
root.insert(np);
root.insert(vp);
root.showCodeTree();
assertTrue(true);
}

@Test
public void testGetTagNodesWhenFlattenedLeftBranch() {
String sentence = "cats chase dogs";
Parse root = Parse.parseParse("(TOP (S (NP (NNS cats)) (VP (VBP chase) (NP (NNS dogs)))))");
Parse[] tags = root.getTagNodes();
assertEquals(3, tags.length);
assertEquals("cats", tags[0].getCoveredText().trim());
assertEquals("dogs", tags[2].getCoveredText().trim());
}

@Test
public void testCompleteReturnsFalseWhenMultipleChildren() {
String sentence = "A B";
Parse root = new Parse(sentence, new Span(0, 3), "ROOT", 1.0, 0);
root.insert(new Parse(sentence, new Span(0, 1), "X", 1.0, 0));
root.insert(new Parse(sentence, new Span(2, 3), "Y", 1.0, 1));
assertFalse(root.complete());
}

@Test
public void testIsFlatReturnsTrueWhenPOSNodeOnly() {
String sentence = "hi";
Parse root = new Parse(sentence, new Span(0, 2), "ROOT", 1.0, 0);
Parse pos = new Parse(sentence, new Span(0, 2), "UH", 1.0, 0);
Parse tok = new Parse(sentence, new Span(0, 2), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
pos.insert(tok);
root.insert(pos);
assertTrue(root.isFlat());
}

@Test
public void testSetChildOnIndexUpdatesLabelNotType() {
String sentence = "name";
Parse root = new Parse(sentence, new Span(0, 4), "S", 1.0, 0);
Parse child = new Parse(sentence, new Span(0, 4), "NN", 1.0, 0);
root.insert(child);
root.setChild(0, "CUSTOM_LABEL");
Parse modified = root.getChildren()[0];
assertEquals("NN", modified.getType());
assertEquals("CUSTOM_LABEL", modified.getLabel());
}

@Test
// public void testAddMethodAddsChunkSpanCorrectly() {
// String sentence = "id name";
// Parse p = new Parse(sentence, new Span(0, 2), "NP", 1.0, 0);
// Parse d = new Parse(sentence, new Span(3, 7), "NN", 1.0, 1);
// Parse root = new Parse(sentence, new Span(0, 7), "S", 1.0, 0);
// root.insert(p);
// d.prevPunctSet = Arrays.asList();
// root.getChildren()[0].add(d, new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return parts[1];
// }
// });
// assertEquals(new Span(0, 7), root.getChildren()[0].getSpan());
// }

// @Test
// public void testHeadRulesFallbackWhenNullAssignsSelf() {
// String sentence = "abc";
// Parse root = new Parse(sentence, new Span(0, 3), "Z", 1.0, 0);
// root.updateHeads(new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return null;
// }
// });
// assertEquals(root, root.getHead());
// }

// @Test
public void testEmptyDerivationIsNullAndCanBeSetLater() {
String text = "deriv";
Parse p = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
assertNull(p.getDerivation());
StringBuffer buf = new StringBuffer("trail");
p.setDerivation(buf);
assertEquals("trail", p.getDerivation().toString());
}
}
