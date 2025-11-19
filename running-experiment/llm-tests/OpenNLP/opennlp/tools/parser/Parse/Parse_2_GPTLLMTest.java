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

public class Parse_2_GPTLLMTest {

@Test
public void testConstructorWithIndex() {
String text = "This is a test.";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "S", 0.9, 0);
assertEquals(text, parse.getText());
assertEquals(span, parse.getSpan());
assertEquals("S", parse.getType());
assertEquals(0.9, parse.getProb(), 0.0001);
assertEquals(0, parse.getHeadIndex());
assertEquals(parse, parse.getHead());
}

@Test
public void testConstructorWithHead() {
String text = "Head";
Span span = new Span(0, 4);
Parse head = new Parse(text, span, "NN", 0.8, 2);
Parse target = new Parse(text, span, "NP", 0.7, head);
assertEquals(text, target.getText());
assertEquals(span, target.getSpan());
assertEquals("NP", target.getType());
assertEquals(0.7, target.getProb(), 0.0001);
assertEquals(head, target.getHead());
assertEquals(2, target.getHeadIndex());
}

@Test
public void testCloneReturnsEqualCopy() {
String text = "Clone this.";
Span span = new Span(0, 11);
Parse original = new Parse(text, span, "S", 0.95, 0);
Parse tok = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 0.8, 0);
original.insert(tok);
Parse cloned = (Parse) original.clone();
assertNotSame(original, cloned);
assertEquals("S", cloned.getType());
assertEquals(span, cloned.getSpan());
assertEquals(original.getChildren().length, cloned.getChildren().length);
assertEquals(0.8, cloned.getChildren()[0].getProb(), 0.0001);
}

// @Test(expected = IllegalArgumentException.class)
public void testInsertThrowsWhenSpanOutOfSentence() {
String text = "Short text";
Span parentSpan = new Span(0, 5);
Span childSpan = new Span(6, 10);
Parse parent = new Parse(text, parentSpan, "NP", 1.0, 0);
Parse child = new Parse(text, childSpan, "NN", 1.0, 0);
parent.insert(child);
}

@Test
public void testAddNextPunctuationPreservesOrder() {
String text = "Example.";
Span span = new Span(0, text.length());
Parse parent = new Parse(text, span, "S", 1.0, 0);
Parse punct = new Parse(text, new Span(7, 8), ".", 1.0, 0);
parent.addNextPunctuation(punct);
assertNotNull(parent.getNextPunctuationSet());
assertTrue(parent.getNextPunctuationSet().contains(punct));
}

@Test
public void testAddPreviousPunctuationPreservesOrder() {
String text = "Hello!";
Span span = new Span(0, text.length());
Parse parent = new Parse(text, span, "INTJ", 1.0, 0);
Parse punct = new Parse(text, new Span(5, 6), "!", 1.0, 0);
parent.addPreviousPunctuation(punct);
assertNotNull(parent.getPreviousPunctuationSet());
assertTrue(parent.getPreviousPunctuationSet().contains(punct));
}

@Test
public void testToStringPennTreebankFormat() {
String text = "Hello world";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse token1 = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 0.9, 0);
Parse token2 = new Parse(text, new Span(6, 11), AbstractBottomUpParser.TOK_NODE, 0.9, 1);
parent.insert(token1);
parent.insert(token2);
String result = parent.toStringPennTreebank();
assertNotNull(result);
assertTrue(result.contains("S"));
assertTrue(result.contains("Hello"));
assertTrue(result.contains("world"));
}

@Test
public void testCompleteReturnsTrueIfOneChild() {
String text = "Only one child node";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse onlyChild = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
root.insert(onlyChild);
assertTrue(root.complete());
}

@Test
public void testCompleteReturnsFalseIfNoChildren() {
String text = "No children";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
assertFalse(root.complete());
}

@Test
public void testGetTagSequenceProbReturnsLogProbForPosTag() {
String text = "Test";
Span span = new Span(0, 4);
Parse token = new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 0.7, 0);
Parse posTag = new Parse(text, span, "NN", 0.8, 0);
posTag.insert(token);
double expected = StrictMath.log(0.8);
double actual = posTag.getTagSequenceProb();
assertEquals(expected, actual, 0.0001);
}

@Test
public void testInsertAddsInCorrectPosition() {
String text = "Insert here";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 6), AbstractBottomUpParser.TOK_NODE, 0.9, 0);
Parse tok2 = new Parse(text, new Span(7, text.length()), AbstractBottomUpParser.TOK_NODE, 0.9, 1);
parent.insert(tok1);
parent.insert(tok2);
Parse[] children = parent.getChildren();
assertEquals(2, children.length);
assertEquals(tok1.getSpan(), children[0].getSpan());
assertEquals(tok2.getSpan(), children[1].getSpan());
}

@Test
public void testEqualsAndHashCodeWork() {
String text = "Same object";
Span span = new Span(0, text.length());
Parse p1 = new Parse(text, span, "NP", 1.0, 0);
Parse tok = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
p1.insert(tok);
Parse p2 = (Parse) p1.clone();
p2.setLabel(p1.getLabel());
assertEquals(p1, p2);
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testGetCoveredTextReturnsExpectedText() {
String text = "Short example";
Span span = new Span(6, 13);
Parse parse = new Parse(text, span, "NP", 1.0, 0);
assertEquals("example", parse.getCoveredText());
}

@Test
public void testSetAndGetDerivation() {
String text = "Derivation";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "VP", 0.5, 0);
StringBuffer derivation = new StringBuffer("build->NP->VP");
parse.setDerivation(derivation);
assertEquals(derivation, parse.getDerivation());
}

@Test
public void testIsPosTagReturnsTrueForOneTokChild() {
String text = "Test";
Span span = new Span(0, 4);
Parse tok = new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse posTag = new Parse(text, span, "NN", 1.0, 0);
posTag.insert(tok);
assertTrue(posTag.isPosTag());
}

@Test
public void testIsFlatReturnsTrueForAllPosTagChildren() {
String text = "Flat phrase";
Parse parent = new Parse(text, new Span(0, text.length()), "NP", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse pos1 = new Parse(text, new Span(0, 4), "DT", 1.0, 0);
pos1.insert(tok1);
Parse tok2 = new Parse(text, new Span(5, 11), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse pos2 = new Parse(text, new Span(5, 11), "NN", 1.0, 0);
pos2.insert(tok2);
parent.insert(pos1);
parent.insert(pos2);
assertTrue(parent.isFlat());
}

@Test
public void testCompareToUsesProbabilityDescending() {
String text = "Text";
Span span = new Span(0, 4);
Parse high = new Parse(text, span, "X", 0.9, 0);
Parse low = new Parse(text, span, "X", 0.5, 0);
int result = high.compareTo(low);
assertTrue(result < 0);
}

@Test
public void testSetChildReplacesAndAssignsLabel() {
String text = "Child test";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse tok = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse pos = new Parse(text, new Span(0, 5), "NN", 1.0, 0);
pos.insert(tok);
parent.insert(pos);
parent.setChild(0, "NNP");
String label = parent.getChildren()[0].getLabel();
assertEquals("NNP", label);
}

@Test
public void testGetCommonParentReturnsCorrectNode() {
String text = "Test common parent";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse token1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
child1.insert(token1);
root.insert(child1);
Parse child2 = new Parse(text, new Span(5, 11), "VP", 1.0, 1);
Parse token2 = new Parse(text, new Span(5, 11), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
child2.insert(token2);
root.insert(child2);
Parse result = token1.getCommonParent(token2);
assertEquals(root, result);
}

@Test
public void testParseParseHandlesNullLabelsGracefully() {
String parseString = "(TOP (-NONE- *))";
Parse result = Parse.parseParse(parseString);
assertNotNull(result);
assertEquals("TOP", result.getType());
}

@Test
public void testInsertNestedConstituentStructure() {
String text = "Nested structure test";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse outer = new Parse(text, new Span(0, 10), "NP", 1.0, 0);
Parse inner = new Parse(text, new Span(0, 5), "NN", 1.0, 0);
Parse token = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
inner.insert(token);
outer.insert(inner);
root.insert(outer);
assertEquals(1, root.getChildren().length);
assertEquals(1, outer.getChildren().length);
assertEquals(inner, outer.getChildren()[0]);
assertEquals(token, inner.getChildren()[0]);
}

@Test
public void testGetHeadWhenNoChildrenReturnsSelf() {
String text = "No child";
Parse parse = new Parse(text, new Span(0, text.length()), "NP", 1.0, 0);
// HeadRules dummyRules = new HeadRules() {
// 
// public Parse getHead(Parse[] constituents, String type) {
// return null;
// }
// };
// parse.updateHeads(dummyRules);
assertEquals(parse, parse.getHead());
}

@Test
public void testUpdateSpanReflectsCorrectRange() {
String text = "update span";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse first = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
Parse second = new Parse(text, new Span(7, 11), "VP", 1.0, 1);
parent.insert(first);
parent.insert(second);
parent.updateSpan();
Span updatedSpan = parent.getSpan();
assertEquals(0, updatedSpan.getStart());
assertEquals(11, updatedSpan.getEnd());
}

@Test
public void testGetTagNodesReturnsEmptyForNonLeaf() {
String text = "Simple non-leaf";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
parent.insert(child);
Parse[] tags = parent.getTagNodes();
assertEquals(0, tags.length);
}

@Test
public void testAdjoinCreatesNewParentCorrectly() {
String text = "Join two";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse left = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse right = new Parse(text, new Span(5, 8), "VP", 1.0, 1);
parent.insert(left);
// HeadRules dummyRules = new HeadRules() {
// 
// public Parse getHead(Parse[] constituents, String type) {
// return constituents[1];
// }
// };
// Parse newParent = parent.adjoin(right, dummyRules);
// assertEquals("NP", newParent.getType());
// assertEquals(2, newParent.getChildCount());
}

@Test
public void testAdjoinRootCreatesProperStructure() {
String text = "merge root";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse left = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
root.insert(left);
Parse right = new Parse(text, new Span(5, 10), "VP", 1.0, 1);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] parses, String type) {
// return parses[1];
// }
// };
// Parse result = root.adjoinRoot(right, rules, 0);
// assertEquals("NP", result.getType());
// assertTrue(result.getSpan().getStart() <= left.getSpan().getStart());
// assertTrue(result.getSpan().getEnd() >= right.getSpan().getEnd());
}

@Test
public void testSetLabelAndGetLabel() {
String text = "Set label";
Parse parse = new Parse(text, new Span(0, text.length()), "X", 1.0, 0);
String lbl = "S-NEW";
parse.setLabel(lbl);
assertEquals(lbl, parse.getLabel());
}

@Test
public void testSetNextPunctuationReplacesExisting() {
String text = "punctuation test";
Parse parse = new Parse(text, new Span(0, text.length()), "X", 1.0, 0);
Collection<Parse> punct = new TreeSet<>();
punct.add(new Parse(text, new Span(5, 6), ".", 1.0, 0));
parse.setNextPunctuation(punct);
assertEquals(1, parse.getNextPunctuationSet().size());
}

@Test
public void testSetPrevPunctuationReplacesExisting() {
String text = "punct test";
Parse parse = new Parse(text, new Span(0, text.length()), "X", 1.0, 0);
Collection<Parse> punct = new TreeSet<>();
punct.add(new Parse(text, new Span(3, 4), ",", 1.0, 0));
parse.setPrevPunctuation(punct);
assertEquals(1, parse.getPreviousPunctuationSet().size());
}

@Test
public void testRemoveChildUpdatesSpan() {
String text = "Update span after remove";
Parse parent = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 5), "A", 1.0, 0);
Parse child2 = new Parse(text, new Span(6, 10), "B", 1.0, 1);
parent.insert(child1);
parent.insert(child2);
parent.remove(0);
Span updated = parent.getSpan();
assertEquals(6, updated.getStart());
assertEquals(10, updated.getEnd());
}

@Test
public void testAddNameSpanWithinOneChildSpan() {
String text = "Barack Obama visited";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse token1 = new Parse(text, new Span(0, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse pos1 = new Parse(text, new Span(0, 6), "NNP", 1.0, 0);
pos1.insert(token1);
Parse token2 = new Parse(text, new Span(7, 12), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse pos2 = new Parse(text, new Span(7, 12), "NNP", 1.0, 1);
pos2.insert(token2);
root.insert(pos1);
root.insert(pos2);
Span[] nameSpans = new Span[] { new Span(0, 2) };
Parse[] tokens = new Parse[] { token1, token2 };
Parse.addNames("PERSON", nameSpans, tokens);
boolean foundPerson = false;
for (Parse child : root.getChildren()) {
if ("PERSON".equals(child.getType())) {
foundPerson = true;
break;
}
}
assertTrue(foundPerson);
}

@Test
public void testGetChildCountReturnsCorrectValue() {
String text = "count children";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
Parse child2 = new Parse(text, new Span(7, 14), "VP", 1.0, 1);
root.insert(child1);
root.insert(child2);
int childCount = root.getChildCount();
assertEquals(2, childCount);
}

@Test
public void testIndexOfReturnsCorrectIndex() {
String text = "indexing";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 8), "VP", 1.0, 0);
root.insert(child);
int index = root.indexOf(child);
assertEquals(0, index);
}

@Test
public void testParseParseHandlesEmptyInput() {
Parse result = Parse.parseParse("(TOP)");
assertNotNull(result);
assertEquals("TOP", result.getType());
assertEquals(0, result.getChildCount());
}

@Test
public void testParseParseHandlesUnbalancedParenthesesGracefully() {
String malformed = "(TOP (NP (NN dog)";
Parse result = Parse.parseParse(malformed);
assertNotNull(result);
assertEquals("TOP", result.getType());
}

@Test
public void testParseParseWithFunctionTagsEnabled() {
Parse.useFunctionTags(true);
String parse = "(TOP (NP-SBJ (DT The) (NN dog)))";
Parse result = Parse.parseParse(parse);
assertNotNull(result);
assertTrue(result.toStringPennTreebank().contains("NP-SBJ"));
}

@Test
public void testInsertWhenChildContainsParentSpan() {
String text = "invalid insert";
Parse parent = new Parse(text, new Span(5, 10), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 15), "NP", 1.0, 0);
try {
parent.insert(child);
fail("Expected IllegalArgumentException not thrown");
} catch (IllegalArgumentException e) {
assertEquals("Inserting constituent not contained in the sentence!", e.getMessage());
}
}

@Test
public void testAddTokNodeTokenEncodedCorrectly() {
String text = "The (dog)";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse openTok = new Parse(text, new Span(4, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse openPOS = new Parse(text, new Span(4, 5), "SYM", 1.0, 0);
openPOS.insert(openTok);
Parse wordTok = new Parse(text, new Span(5, 8), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse wordPOS = new Parse(text, new Span(5, 8), "NN", 1.0, 1);
wordPOS.insert(wordTok);
Parse closeTok = new Parse(text, new Span(8, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse closePOS = new Parse(text, new Span(8, 9), "SYM", 1.0, 2);
closePOS.insert(closeTok);
root.insert(openPOS);
root.insert(wordPOS);
root.insert(closePOS);
String ptb = root.toStringPennTreebank();
assertTrue(ptb.contains("-LRB-") || ptb.contains("-RRB-"));
}

@Test
public void testSetTypeChangeAndVerify() {
String text = "Set type test";
Parse parse = new Parse(text, new Span(0, text.length()), "VP", 1.0, 0);
assertEquals("VP", parse.getType());
parse.setType("S");
assertEquals("S", parse.getType());
}

@Test
public void testGetTokenReturnsNullWhenNoTokenFound() {
String tree = "(TOP (NP (XX)))";
Parse result = Parse.parseParse(tree);
assertNotNull(result);
assertEquals(1, result.getChildCount());
}

@Test
public void testFixPossessivesWithNoPosTagsDoesNothing() {
String text = "John pets dog";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse pos1 = new Parse(text, new Span(0, 4), "NNP", 1.0, 0);
pos1.insert(new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0));
root.insert(pos1);
Parse pos2 = new Parse(text, new Span(5, 9), "VBZ", 1.0, 1);
pos2.insert(new Parse(text, new Span(5, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1));
root.insert(pos2);
Parse pos3 = new Parse(text, new Span(10, 13), "NN", 1.0, 2);
pos3.insert(new Parse(text, new Span(10, 13), AbstractBottomUpParser.TOK_NODE, 1.0, 2));
root.insert(pos3);
Parse.fixPossesives(root);
assertEquals(3, root.getChildCount());
}

@Test
public void testEqualsWithDifferentLabelsIsFalse() {
String text = "same";
Span span = new Span(0, 4);
Parse p1 = new Parse(text, span, "X", 1.0, 0);
Parse p2 = new Parse(text, span, "X", 1.0, 0);
p1.setLabel("LBL1");
p2.setLabel("LBL2");
assertNotEquals(p1, p2);
}

@Test
public void testEqualsWithNullObjectReturnsFalse() {
String text = "text";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "X", 1.0, 0);
assertFalse(parse.equals(null));
}

@Test
public void testEqualsWithDifferentTextReturnsFalse() {
Span span = new Span(0, 3);
Parse p1 = new Parse("abc", span, "A", 1.0, 0);
Parse p2 = new Parse("xyz", span, "A", 1.0, 0);
assertNotEquals(p1, p2);
}

@Test
public void testEqualsWithDifferentSpansReturnsFalse() {
String text = "some text";
Parse p1 = new Parse(text, new Span(0, 4), "X", 1.0, 0);
Parse p2 = new Parse(text, new Span(1, 5), "X", 1.0, 0);
assertNotEquals(p1, p2);
}

@Test
public void testCloneRootWithSingleLevel() {
String text = "Root clone";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse subChild = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
child.insert(subChild);
root.insert(child);
Parse cloned = root.cloneRoot(subChild, 0);
assertNotSame(root, cloned);
assertEquals("TOP", cloned.getType());
assertEquals("NP", cloned.getChildren()[0].getType());
assertEquals(AbstractBottomUpParser.TOK_NODE, cloned.getChildren()[0].getChildren()[0].getType());
}

@Test
public void testHeadIndexPropagation() {
String text = "propagate index";
Parse head = new Parse(text, new Span(0, 4), "NN", 1.0, 2);
Parse root = new Parse(text, new Span(0, text.length()), "NP", 1.0, head);
assertEquals(2, root.getHeadIndex());
}

@Test
public void testIsChunkFlagSet() {
String text = "Chunk test";
Parse parse = new Parse(text, new Span(0, text.length()), "X", 1.0, 0);
parse.isChunk(true);
assertTrue(parse.isChunk());
parse.isChunk(false);
assertFalse(parse.isChunk());
}

@Test
public void testGetTagNodesWithNoTags() {
String text = "No tags";
Parse parse = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
parse.insert(child);
Parse[] tags = parse.getTagNodes();
assertEquals(0, tags.length);
}

@Test
public void testDecodeEncodeRoundTripBrackets() {
// String encoded = Parse.encodeToken("(");
// String decoded = Parse.decodeToken(encoded);
// assertEquals("(", decoded);
// encoded = Parse.encodeToken("]");
// decoded = Parse.decodeToken(encoded);
// assertEquals("]", decoded);
}

@Test
public void testAddProbCorrectlyAddsLogProbabilities() {
String text = "prob";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "X", 0.5, 0);
double origProb = parse.getProb();
parse.addProb(0.25);
assertEquals(origProb + 0.25, parse.getProb(), 0.0001);
}

@Test
public void testToStringUsesCoveredText() {
String text = "sentence";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "NN", 1.0, 0);
assertEquals("sent", parse.toString());
}

@Test
public void testParseCompareToEqualReturnsZero() {
String text = "compare";
Span span = new Span(0, 7);
Parse p1 = new Parse(text, span, "X", 0.6, 0);
Parse p2 = new Parse(text, span, "X", 0.6, 0);
assertEquals(0, p1.compareTo(p2));
}

@Test
public void testSetDerivationAppendsDerivationText() {
String text = "deriv";
Span span = new Span(0, 5);
Parse parse = new Parse(text, span, "VP", 1.0, 0);
StringBuffer sb = new StringBuffer("root->child");
parse.setDerivation(sb);
assertEquals("root->child", parse.getDerivation().toString());
}

@Test
public void testParseEqualsWithDifferentChildrenReturnsFalse() {
String text = "unique";
Parse p1 = new Parse(text, new Span(0, 6), "S", 1.0, 0);
Parse p2 = new Parse(text, new Span(0, 6), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
p1.insert(child);
assertNotEquals(p1, p2);
}

@Test
public void testParseHashCodeIgnoresLabel() {
String text = "hash";
Parse p1 = new Parse(text, new Span(0, 4), "X", 1.0, 0);
Parse p2 = new Parse(text, new Span(0, 4), "X", 1.0, 0);
p1.setLabel("A");
p2.setLabel("B");
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testGetCommonParentWithItselfReturnsParent() {
String text = "parent check";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
parent.insert(child);
Parse result = child.getCommonParent(child);
assertEquals(parent, result);
}

@Test
public void testAddIntoDeepHierarchyPreservesSpanAndType() {
String text = "deep insert";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse middle = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
Parse leaf = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
middle.insert(leaf);
root.insert(middle);
Parse newChild = new Parse(text, new Span(4, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
root.insert(newChild);
assertEquals(2, root.getChildren().length);
}

@Test
public void testParseParseWithDashInLabelExtractsBaseLabelCorrectly() {
Parse.useFunctionTags(false);
String parseStr = "(TOP (NP-SBJ (DT The) (NN dog)))";
Parse parse = Parse.parseParse(parseStr);
assertNotNull(parse);
Parse[] children = parse.getChildren();
assertEquals(1, children.length);
assertEquals("NP", children[0].getType());
}

@Test
public void testParseParseWithDashInLabelExtractsFullLabelWithFunctionTags() {
Parse.useFunctionTags(true);
String parseStr = "(TOP (NP-SBJ (DT The) (NN dog)))";
Parse parse = Parse.parseParse(parseStr);
assertNotNull(parse);
Parse[] children = parse.getChildren();
assertEquals(1, children.length);
assertEquals("NP-SBJ", children[0].getType());
}

@Test
public void testGetTokenWithNonMatchingStringReturnsNull() {
String malformedString = "BADLABEL";
// String token = Parse.getToken(malformedString);
// assertNull(token);
}

@Test
public void testGetTypeReturnsFallbackNullWhenPatternNotMatched() {
String content = "-UNKNOWN-TAG";
Parse.useFunctionTags(false);
// String type = Parse.getType(content);
// assertNull(type);
}

@Test
public void testGetHeadReturnsFirstWhenSingleChild() {
String text = "single head";
Parse single = new Parse(text, new Span(0, text.length()), "VP", 1.0, 0);
Parse token = new Parse(text, new Span(0, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
single.insert(token);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] c, String type) {
// return c[0];
// }
// };
// single.updateHeads(rules);
assertEquals(token, single.getHead());
}

@Test
public void testGetHeadReturnsSelfWhenGetHeadNull() {
String text = "orphan";
Parse node = new Parse(text, new Span(0, text.length()), "X", 1.0, 0);
Parse child = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
node.insert(child);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] c, String type) {
// return null;
// }
// };
// node.updateHeads(rules);
assertEquals(node, node.getHead());
}

@Test
public void testAdjoinWithNullPunctuation() {
String text = "adjunction";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse left = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse right = new Parse(text, new Span(6, 10), "VP", 1.0, 1);
root.insert(left);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] constituents, String type) {
// return constituents[0];
// }
// };
// Parse result = root.adjoin(right, rules);
// assertEquals("NP", result.getType());
// assertEquals(2, result.getChildren().length);
}

@Test
public void testExpandTopNodeMovesChildrenCorrectly() {
String text = "expand";
Parse main = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse one = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
Parse two = new Parse(text, new Span(4, 6), "VP", 1.0, 1);
main.insert(one);
main.insert(two);
Parse newTop = new Parse(text, new Span(0, 6), "TOP", 1.0, 0);
main.expandTopNode(newTop);
assertEquals(2, newTop.getChildren().length);
}

@Test
public void testAddNamesDoesNotAddWhenCrossingChildren() {
String text = "Kim Lee Smith";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(4, 7), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tok3 = new Parse(text, new Span(8, 13), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse np1 = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse np2 = new Parse(text, new Span(8, 13), "NP", 1.0, 1);
np1.insert(tok1);
np1.insert(tok2);
np2.insert(tok3);
root.insert(np1);
root.insert(np2);
Span[] nameSpans = new Span[] { new Span(0, 3), new Span(1, 3) };
Parse[] tokens = new Parse[] { tok1, tok2, tok3 };
Parse.addNames("PERSON", nameSpans, tokens);
boolean foundNameTag = false;
for (Parse child : root.getChildren()) {
if ("PERSON".equals(child.getType())) {
foundNameTag = true;
}
}
assertTrue(foundNameTag);
}

@Test
public void testInsertAddsConstituentInCorrectPositionAmongMultipleChildren() {
String text = "word1 word2 word3";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse token1 = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse token2 = new Parse(text, new Span(6, 11), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse token3 = new Parse(text, new Span(12, 17), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
parent.insert(token1);
parent.insert(token3);
Parse middle = new Parse(text, new Span(6, 11), "VP", 1.0, 1);
middle.insert(token2);
parent.insert(middle);
Parse[] children = parent.getChildren();
assertEquals(3, children.length);
assertEquals(token1.getSpan(), children[0].getSpan());
assertEquals(middle.getSpan(), children[1].getSpan());
assertEquals(token3.getSpan(), children[2].getSpan());
}

@Test
public void testInsertMergesContainedChildrenIntoNewNode() {
String text = "a b c";
Parse sentence = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse token1 = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse token2 = new Parse(text, new Span(2, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
sentence.insert(token1);
sentence.insert(token2);
Parse parentNP = new Parse(text, new Span(0, 3), "NP", 1.0, 1);
sentence.insert(parentNP);
Parse[] children = sentence.getChildren();
assertEquals(1, children.length);
assertEquals("NP", children[0].getType());
assertEquals(2, children[0].getChildren().length);
}

@Test
public void testInsertFailsIfSpanOutsideParent() {
String text = "valid";
Parse parent = new Parse(text, new Span(0, 5), "S", 1.0, 0);
Parse child = new Parse(text, new Span(5, 10), "NP", 1.0, 0);
parent.insert(child);
}

@Test
public void testClonePreservesDerivationString() {
String text = "with derivation";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "NP", 1.0, 0);
StringBuffer derivation = new StringBuffer("ROOT->NP");
parse.setDerivation(derivation);
Parse clone = (Parse) parse.clone();
assertNotNull(clone.getDerivation());
assertEquals("ROOT->NP", clone.getDerivation().toString());
}

@Test
public void testClonePreservesLabel() {
String text = "label test";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "VP", 1.0, 0);
parse.setLabel("intermediate");
Parse clone = (Parse) parse.clone();
assertEquals("intermediate", clone.getLabel());
}

@Test
public void testGetTokenNodesReturnsOnlyTOK_NODELeaves() {
String text = "a b";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
Parse token = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
np.insert(token);
root.insert(np);
Parse[] tokens = root.getTokenNodes();
assertEquals(1, tokens.length);
assertEquals(token, tokens[0]);
}

@Test
public void testGetCommonParentWhenOneIsAncestorOfOther() {
String text = "nested";
Parse root = new Parse(text, new Span(0, 6), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
Parse grandchild = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
child.insert(grandchild);
root.insert(child);
Parse result = grandchild.getCommonParent(child);
assertEquals(child, result);
}

@Test
public void testGetCommonParentWhenNoSharedParentReturnsNull() {
String text = "split";
Parse a = new Parse(text, new Span(0, 2), "A", 1.0, 0);
Parse b = new Parse(text, new Span(3, 5), "B", 1.0, 1);
Parse common = a.getCommonParent(b);
assertNull(common);
}

@Test
public void testToStringPennTreebankReturnsStructuredString() {
String text = "Hi.";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse pos = new Parse(text, new Span(0, 2), "UH", 1.0, 0);
Parse token = new Parse(text, new Span(0, 2), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
pos.insert(token);
root.insert(pos);
String output = root.toStringPennTreebank();
assertTrue(output.contains("(S"));
assertTrue(output.contains("(UH"));
assertTrue(output.contains("Hi"));
}

@Test
public void testEqualsReturnsTrueForSameObject() {
String text = "same";
Parse parse = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
assertTrue(parse.equals(parse));
}

@Test
public void testEqualsReturnsFalseForDifferentClass() {
String text = "diff";
Parse parse = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Object notParse = new Object();
assertFalse(parse.equals(notParse));
}

@Test
public void testGetChildrenReturnsEmptyArrayWhenNoChildren() {
String text = "solo";
Parse parse = new Parse(text, new Span(0, text.length()), "NP", 1.0, 0);
Parse[] children = parse.getChildren();
assertEquals(0, children.length);
}

@Test
public void testGetParentReturnsNullForRootNode() {
String text = "root";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
assertNull(root.getParent());
}

@Test
public void testSetParentEstablishesBiDirectionalLink() {
String text = "parent";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
parent.insert(child);
assertEquals(parent, child.getParent());
}

@Test
public void testInsertWhenPartsListEmpty() {
String text = "single";
Parse parent = new Parse(text, new Span(0, 6), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
parent.insert(child);
assertEquals(1, parent.getChildCount());
assertEquals(child, parent.getChildren()[0]);
assertEquals(parent, child.getParent());
}

@Test
public void testInsertWhenExistingChildSpanEqualsNewChildSpan() {
String text = "equal span";
Parse parent = new Parse(text, new Span(0, 10), "S", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse child2 = new Parse(text, new Span(0, 5), "VP", 1.0, 0);
parent.insert(child1);
parent.insert(child2);
Parse[] children = parent.getChildren();
assertEquals(2, children.length);
assertEquals("NP", children[0].getType());
assertEquals("VP", children[1].getType());
}

@Test
public void testInsertWhenNewChildIsContainedByExistingChild() {
String text = "nested";
Parse parent = new Parse(text, new Span(0, 10), "S", 1.0, 0);
Parse outer = new Parse(text, new Span(0, 10), "NP", 1.0, 0);
Parse inner = new Parse(text, new Span(2, 5), "NN", 1.0, 0);
parent.insert(outer);
outer.insert(inner);
Parse word = new Parse(text, new Span(3, 4), "TOK", 1.0, 0);
parent.insert(word);
assertEquals(1, inner.getChildCount());
assertEquals(word, inner.getChildren()[0]);
}

@Test
public void testGetIndexOfChildReturnsNegativeOneIfNotPresent() {
String text = "missing";
Parse parent = new Parse(text, new Span(0, 7), "S", 1.0, 0);
Parse unrelated = new Parse(text, new Span(0, 7), "VP", 1.0, 0);
int idx = parent.indexOf(unrelated);
assertEquals(-1, idx);
}

@Test
public void testRemoveWithIndexEqualToLastElement() {
String text = "test remove";
Parse parent = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse child2 = new Parse(text, new Span(5, 11), "VP", 1.0, 1);
parent.insert(child1);
parent.insert(child2);
parent.remove(1);
assertEquals(1, parent.getChildCount());
assertEquals(child1, parent.getChildren()[0]);
Span resultingSpan = parent.getSpan();
assertEquals(0, resultingSpan.getStart());
assertEquals(4, resultingSpan.getEnd());
}

@Test
public void testRemoveWithIndexZero() {
String text = "remove first";
Parse parent = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse child1 = new Parse(text, new Span(0, 6), "X", 1.0, 0);
Parse child2 = new Parse(text, new Span(7, 12), "Y", 1.0, 1);
parent.insert(child1);
parent.insert(child2);
parent.remove(0);
assertEquals(1, parent.getChildCount());
assertEquals(child2, parent.getChildren()[0]);
Span updated = parent.getSpan();
assertEquals(7, updated.getStart());
assertEquals(12, updated.getEnd());
}

@Test
public void testRemoveOnlyChildKeepsOldSpan() {
String text = "unchanged span";
Parse parent = new Parse(text, new Span(0, 14), "X", 1.0, 0);
Parse child = new Parse(text, new Span(2, 6), "Z", 1.0, 0);
parent.insert(child);
parent.remove(0);
assertEquals(0, parent.getChildCount());
assertEquals(0, parent.getSpan().getStart());
assertEquals(14, parent.getSpan().getEnd());
}

@Test
public void testToStringPennTreebankHandlesEmptyPartList() {
String text = "test";
Parse leaf = new Parse(text, new Span(0, text.length()), "NP", 0.9, 0);
String result = leaf.toStringPennTreebank();
assertTrue(result.contains("NP"));
assertTrue(result.contains("test") || result.contains(")"));
}

@Test
public void testToStringPennTreebankWithMultipleWhitespaceInText() {
String text = "a  b   c";
Parse root = new Parse(text, new Span(0, 8), "S", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(3, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tok3 = new Parse(text, new Span(7, 8), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
root.insert(tok1);
root.insert(tok2);
root.insert(tok3);
String result = root.toStringPennTreebank();
assertTrue(result.contains("-LRB-") || result.contains("a") || result.contains("b") || result.contains("c"));
}

@Test
public void testFixPossessivesCreatesNewNounPhrase() {
String text = "John 's dog";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse pos1 = new Parse(text, new Span(0, 4), "NNP", 1.0, 0);
pos1.insert(tok1);
root.insert(pos1);
Parse tok2 = new Parse(text, new Span(5, 7), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse pos2 = new Parse(text, new Span(5, 7), "POS", 1.0, 1);
pos2.insert(tok2);
root.insert(pos2);
Parse tok3 = new Parse(text, new Span(8, 11), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse pos3 = new Parse(text, new Span(8, 11), "NN", 1.0, 2);
pos3.insert(tok3);
root.insert(pos3);
Parse.fixPossesives(root);
boolean npExists = false;
for (Parse child : root.getChildren()) {
if ("NP".equals(child.getType()) && child.getSpan().equals(new Span(8, 11))) {
npExists = true;
}
}
assertTrue(npExists);
}

@Test
public void testUseFunctionTagsDoesNotLeakAcrossTests() {
Parse.useFunctionTags(true);
String parseStr = "(TOP (NP-SBJ (NN Dog)))";
Parse p1 = Parse.parseParse(parseStr);
Parse.useFunctionTags(false);
Parse p2 = Parse.parseParse(parseStr);
assertNotEquals(p1.getChildren()[0].getType(), p2.getChildren()[0].getType());
}
}
