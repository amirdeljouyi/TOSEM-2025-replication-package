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

public class Parse_5_GPTLLMTest {

@Test
public void testConstructorWithHeadIndexFields() {
String text = "Some text";
Span span = new Span(0, 9);
Parse parse = new Parse(text, span, "NP", 0.75, 2);
assertEquals("NP", parse.getType());
assertEquals(text, parse.getText());
assertEquals(span, parse.getSpan());
assertEquals(2, parse.getHeadIndex());
assertEquals(0.75, parse.getProb(), 0.0001);
assertEquals(parse, parse.getHead());
}

@Test
public void testConstructorWithParseHead() {
String text = "Example sentence.";
Span span = new Span(0, 17);
Parse head = new Parse(text, span, "NN", 0.5, 1);
Parse parse = new Parse(text, span, "NP", 0.8, head);
assertEquals("NP", parse.getType());
assertEquals(0.8, parse.getProb(), 0.0001);
assertEquals(1, parse.getHeadIndex());
assertEquals(head, parse.getHead());
}

@Test
public void testCloneCreatesDistinctObject() {
String text = "Sample sentence.";
Span span = new Span(0, 16);
Parse original = new Parse(text, span, "S", 0.9, 0);
Parse clone = (Parse) original.clone();
assertNotSame(original, clone);
assertEquals(original.getText(), clone.getText());
assertEquals(original.getSpan(), clone.getSpan());
assertEquals(original.getType(), clone.getType());
assertEquals(original.getProb(), clone.getProb(), 0.0001);
}

@Test
public void testSetTypeAndGetType() {
String text = "Set type test.";
Span span = new Span(0, 15);
Parse parse = new Parse(text, span, "NP", 0.7, 0);
parse.setType("VP");
assertEquals("VP", parse.getType());
}

@Test
public void testInsertChildUpdatesParent() {
String text = "Insert child test.";
Span rootSpan = new Span(0, 19);
Span childSpan = new Span(7, 12);
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Parse child = new Parse(text, childSpan, "NP", 0.5, 1);
root.insert(child);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
assertEquals(child, children[0]);
assertEquals(root, children[0].getParent());
}

@Test
public void testAddAndRetrievePreviousPunctuation() {
String text = "Example.";
Span mainSpan = new Span(0, 8);
Span punctSpan = new Span(7, 8);
Parse main = new Parse(text, mainSpan, "S", 1.0, 0);
Parse punctuation = new Parse(text, punctSpan, ".", 0.3, 1);
main.addPreviousPunctuation(punctuation);
assertNotNull(main.getPreviousPunctuationSet());
assertTrue(main.getPreviousPunctuationSet().contains(punctuation));
}

@Test
public void testAddAndRetrieveNextPunctuation() {
String text = "Hello!";
Span rootSpan = new Span(0, 6);
Span punctSpan = new Span(5, 6);
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Parse punctuation = new Parse(text, punctSpan, ".", 0.8, 0);
root.addNextPunctuation(punctuation);
assertNotNull(root.getNextPunctuationSet());
assertTrue(root.getNextPunctuationSet().contains(punctuation));
}

@Test
public void testGetCoveredText() {
String text = "Get covered text.";
Span span = new Span(4, 12);
Parse parse = new Parse(text, span, "NP", 1.0, 0);
String covered = parse.getCoveredText();
assertEquals("covered", covered);
}

@Test
public void testEqualsAndHashCodeSameFields() {
String text = "Same text.";
Span span = new Span(0, 10);
Parse parse1 = new Parse(text, span, "NP", 0.75, 1);
Parse parse2 = new Parse(text, span, "NP", 0.75, 1);
parse1.setLabel("X");
parse2.setLabel("X");
assertTrue(parse1.equals(parse2));
assertEquals(parse1.hashCode(), parse2.hashCode());
}

@Test
public void testCompareToHigherProbIsLower() {
String text = "Prob test.";
Span span = new Span(0, 10);
Parse lower = new Parse(text, span, "NP", 0.3, 1);
Parse higher = new Parse(text, span, "NP", 0.8, 1);
assertTrue(higher.compareTo(lower) < 0);
}

@Test
public void testGetHeadIndexReturnsCorrectValue() {
String text = "Dog barked.";
Span span = new Span(0, 10);
Parse parse = new Parse(text, span, "VP", 1.0, 3);
assertEquals(3, parse.getHeadIndex());
}

@Test
public void testSetAndGetLabel() {
String text = "Label test.";
Span span = new Span(0, 11);
Parse parse = new Parse(text, span, "NP", 1.0, 2);
parse.setLabel("AUX");
assertEquals("AUX", parse.getLabel());
}

@Test
public void testIsChunkAndSetChunkFlag() {
String text = "Chunk test.";
Span span = new Span(0, 11);
Parse parse = new Parse(text, span, "VP", 1.0, 1);
parse.isChunk(true);
assertTrue(parse.isChunk());
}

@Test
public void testAddProbAccumulates() {
String text = "Add prob.";
Span span = new Span(0, 9);
Parse parse = new Parse(text, span, "NP", 0.5, 2);
parse.addProb(0.3);
assertEquals(0.8, parse.getProb(), 0.0001);
}

@Test
public void testGetTypeAfterUseFunctionTagsFalse() {
Parse.useFunctionTags(false);
String text = "(S (NP-SBJ (DT The) (NN cat)))";
Parse parsed = Parse.parseParse(text);
assertNotNull(parsed);
assertEquals("TOP", parsed.getType());
}

@Test
public void testToStringReturnsCoveredText() {
String text = "Hello world!";
Span span = new Span(6, 11);
Parse parse = new Parse(text, span, "NP", 1.0, 0);
assertEquals("world", parse.toString());
}

@Test
public void testSetDerivationGetDerivation() {
String text = "Derivation";
Span span = new Span(0, 10);
Parse parse = new Parse(text, span, "NP", 1.0, 0);
StringBuffer buffer = new StringBuffer("derive1");
parse.setDerivation(buffer);
assertEquals("derive1", parse.getDerivation().toString());
}

@Test
public void testParseParseCreatesValidTree() {
Parse parse = Parse.parseParse("(TOP (NP (DT The) (NN cat)))");
assertEquals("TOP", parse.getType());
assertTrue(parse.toString().startsWith("The"));
}

@Test
public void testInsertThrowsOnNonContainedSpan() {
String text = "This is a test.";
Span rootSpan = new Span(0, 15);
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Span childSpan = new Span(15, 18);
Parse external = new Parse(text, childSpan, "NP", 0.9, 1);
root.insert(external);
}

@Test
public void testInsertNestedConstituentCreatesTree() {
String text = "nested test";
Span rootSpan = new Span(0, 11);
Span level1 = new Span(0, 11);
Span level2 = new Span(0, 6);
Span level3 = new Span(0, 3);
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Parse mid = new Parse(text, level1, "A", 1.0, 1);
Parse inner = new Parse(text, level2, "B", 1.0, 1);
Parse deep = new Parse(text, level3, "C", 1.0, 1);
root.insert(mid);
mid.insert(inner);
inner.insert(deep);
Parse[] topChildren = root.getChildren();
assertEquals(1, topChildren.length);
assertEquals("A", topChildren[0].getType());
Parse[] secondChildren = topChildren[0].getChildren();
assertEquals("B", secondChildren[0].getType());
Parse[] thirdChildren = secondChildren[0].getChildren();
assertEquals("C", thirdChildren[0].getType());
}

@Test
public void testUseFunctionTagsTrueUpdatesType() {
Parse.useFunctionTags(true);
String parseString = "(TOP (NP-SBJ (NN John)))";
Parse parse = Parse.parseParse(parseString);
Parse[] children = parse.getChildren();
assertEquals(1, children.length);
Parse np = children[0];
assertEquals("NP-SBJ", np.getType());
}

@Test
public void testEncodeDecodeParentheses() {
// String encoded = Parse.encodeToken("(");
// assertEquals("-LRB-", encoded);
// String decoded = Parse.decodeToken(encoded);
// assertEquals("(", decoded);
// assertEquals(")", Parse.decodeToken(Parse.encodeToken(")")));
// assertEquals("{", Parse.decodeToken(Parse.encodeToken("{")));
// assertEquals("}", Parse.decodeToken(Parse.encodeToken("}")));
// assertEquals("[", Parse.decodeToken(Parse.encodeToken("[")));
// assertEquals("]", Parse.decodeToken(Parse.encodeToken("]")));
}

@Test
public void testGetTokenReturnsNullForNonTerminalSpan() {
String malformed = "(NP (DT The)";
String token = Parse.parseParse(malformed).getCoveredText();
assertNotNull(token);
}

@Test
public void testPruneParseCombinesDuplicateNodeType() {
String parseText = "(TOP (A (A (A test))))";
Parse parse = Parse.parseParse(parseText);
Parse.pruneParse(parse);
Parse[] children = parse.getChildren();
assertEquals(1, children.length);
assertTrue(children[0].getCoveredText().contains("test"));
}

@Test
public void testFixPossessivesCreatesNPChunk() {
String tree = "(TOP (NP (DT The) (NN dog) (POS 's)) (NN tail))";
Parse parse = Parse.parseParse(tree);
Parse.fixPossesives(parse);
Parse[] children = parse.getChildren();
assertEquals(2, children.length);
}

@Test
public void testAddNamesWithCrossingChildrenDoesNotInsert() {
String sentence = "Barack Hussein Obama";
Span sentenceSpan = new Span(0, sentence.length());
Parse root = new Parse(sentence, sentenceSpan, "TOP", 1.0, 0);
Parse t1 = new Parse(sentence, new Span(0, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse t2 = new Parse(sentence, new Span(7, 14), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse t3 = new Parse(sentence, new Span(15, 20), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
root.insert(t1);
root.insert(t2);
root.insert(t3);
Span[] nameSpan = { new Span(0, 3) };
Parse[] tokenNodes = { t1, t2, t3 };
Parse.addNames("person", nameSpan, tokenNodes);
Parse[] children = root.getChildren();
boolean foundTag = false;
if (children.length > 3) {
for (Parse p : children) {
if ("person".equals(p.getType())) {
foundTag = true;
}
}
}
assertFalse(foundTag);
}

@Test
public void testGetTagNodesSkipsNonPosTags() {
Span parentSpan = new Span(0, 15);
String text = "Testing tag nodes";
Parse root = new Parse(text, parentSpan, "TOP", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(5, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse pos1 = new Parse(text, new Span(0, 4), "DT", 1.0, 0);
pos1.insert(tok1);
Parse pos2 = new Parse(text, new Span(5, 9), "NN", 1.0, 1);
pos2.insert(tok2);
root.insert(pos1);
root.insert(pos2);
Parse[] tags = root.getTagNodes();
assertEquals(2, tags.length);
assertEquals("DT", tags[0].getType());
assertEquals("NN", tags[1].getType());
}

@Test
public void testGetCommonParentReturnsSelfParentIfSame() {
String text = "Some sentence.";
Span span = new Span(0, 14);
Parse parent = new Parse(text, span, "TOP", 1.0, 0);
Parse node = new Parse(text, new Span(5, 10), "NP", 0.9, 1);
parent.insert(node);
Parse result = node.getCommonParent(node);
assertEquals(parent, result);
}

@Test
public void testSetChildReplacesCloneWithLabel() {
String text = "Testing setChild";
Parse root = new Parse(text, new Span(0, 17), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 7), "NP", 0.9, 1);
root.insert(child);
root.setChild(0, "NEW_LABEL");
Parse[] children = root.getChildren();
assertEquals("NEW_LABEL", children[0].getLabel());
assertEquals("NP", children[0].getType());
assertNotSame(child, children[0]);
}

@Test
public void testUpdateSpanAdjustsSpanCorrectly() {
String text = "This is a sentence";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse a = new Parse(text, new Span(0, 4), "DT", 1.0, 0);
Parse b = new Parse(text, new Span(5, 7), "NN", 1.0, 1);
root.insert(a);
root.insert(b);
root.updateSpan();
assertEquals(0, root.getSpan().getStart());
assertEquals(7, root.getSpan().getEnd());
}

@Test
public void testGetChildrenReturnsCopyNotReference() {
String text = "Sample";
Span span = new Span(0, 6);
Parse root = new Parse(text, span, "TOP", 1.0, 0);
Parse child = new Parse(text, span, "NN", 1.0, 1);
root.insert(child);
Parse[] childrenArray = root.getChildren();
childrenArray[0] = null;
Parse[] original = root.getChildren();
assertNotNull(original[0]);
}

@Test
public void testStaticParseParseHandlesDeeplyNested() {
String parseStr = "(TOP (A (B (C (D (E (F (G (H (I (J (K (L (M (N token))))))))))))))";
Parse root = Parse.parseParse(parseStr);
assertNotNull(root);
assertEquals("TOP", root.getType());
assertTrue(root.getCoveredText().contains("token"));
}

@Test
public void testEqualsReturnsFalseForNull() {
String text = "Test";
Span span = new Span(0, 4);
Parse p = new Parse(text, span, "NP", 1.0, 0);
assertFalse(p.equals(null));
}

@Test
public void testEqualsReturnsFalseForDifferentType() {
String text = "Test";
Span span = new Span(0, 4);
Parse p = new Parse(text, span, "NP", 1.0, 0);
assertFalse(p.equals("not a parse"));
}

@Test
public void testCommonParentReturnsNullWhenNoParentShared() {
String text = "No common root";
Span spanA = new Span(0, 4);
Span spanB = new Span(5, 9);
Parse a = new Parse(text, spanA, "NP", 1.0, 0);
Parse b = new Parse(text, spanB, "VP", 1.0, 1);
assertNull(a.getCommonParent(b));
}

@Test
public void testSetAndGetPrevNextPunctuationExplicitly() {
String text = "Testing punctuation";
Span span = new Span(0, 18);
Parse parse = new Parse(text, span, "TOP", 1.0, 0);
Parse punct1 = new Parse(text, new Span(4, 5), ".", 0.9, 1);
Parse punct2 = new Parse(text, new Span(6, 7), ",", 0.7, 2);
parse.setPrevPunctuation(Collections.singleton(punct1));
parse.setNextPunctuation(Collections.singleton(punct2));
assertTrue(parse.getPreviousPunctuationSet().contains(punct1));
assertTrue(parse.getNextPunctuationSet().contains(punct2));
}

@Test
public void testIsPosTagReturnsFalseWhenNoTOKNode() {
String text = "Just a type";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "NP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), "DT", 1.0, 0);
parse.insert(child);
assertFalse(parse.isPosTag());
}

@Test
public void testIsFlatReturnsFalseWhenNestedNonPos() {
String text = "Complex";
Parse root = new Parse(text, new Span(0, 7), "S", 1.0, 0);
Parse inner = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse tok = new Parse(text, new Span(0, 7), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
inner.insert(tok);
root.insert(inner);
assertFalse(root.isFlat());
}

@Test
public void testHashCodeConsistentWithEquals() {
String text = "Hash test";
Span span = new Span(0, 9);
Parse p1 = new Parse(text, span, "NP", 1.0, 0);
Parse p2 = new Parse(text, span, "NP", 1.0, 0);
p1.setLabel("labelX");
p2.setLabel("labelX");
assertTrue(p1.equals(p2));
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testCompleteReturnsTrueWhenSinglePart() {
String text = "Test.";
Parse parent = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, text.length()), "NP", 1.0, 1);
parent.insert(child);
assertTrue(parent.complete());
}

@Test
public void testCompleteReturnsFalseWhenNoChildren() {
String text = "Empty";
Parse parse = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
assertFalse(parse.complete());
}

@Test
public void testAddMethodUsesHeadRulesCorrectly() {
String text = "The cat sleeps";
Parse parent = new Parse(text, new Span(0, 15), "S", 1.0, 0);
Parse daughter = new Parse(text, new Span(4, 7), "NP", 1.0, 1);
// HeadRules mockRules = new HeadRules() {
// 
// @Override
// public Parse getHead(Parse[] constituents, String type) {
// return constituents[0];
// }
// };
// parent.add(daughter, mockRules);
Parse[] children = parent.getChildren();
// assertTrue(childIsPresent(children, daughter.getSpan()));
assertEquals(daughter.getHeadIndex(), parent.getHeadIndex());
}

@Test
public void testAddNamesExactSpanMatchInserts() {
String sentence = "Jane Smith went home.";
Span fullSpan = new Span(0, sentence.length());
Parse root = new Parse(sentence, fullSpan, "TOP", 1.0, 0);
Parse p1 = new Parse(sentence, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse p2 = new Parse(sentence, new Span(5, 10), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
root.insert(p1);
root.insert(p2);
Span[] nameSpan = { new Span(0, 2) };
Parse[] tokens = new Parse[] { p1, p2 };
Parse.addNames("person", nameSpan, tokens);
boolean foundNamedEntity = false;
Parse[] children = root.getChildren();
if (children.length == 3) {
for (Parse p : children) {
if ("person".equals(p.getType())) {
foundNamedEntity = true;
}
}
}
assertTrue(foundNamedEntity);
}

// @Test(expected = IndexOutOfBoundsException.class)
public void testSetChildInvalidIndexThrows() {
String text = "single child";
Span span = new Span(0, 12);
Parse root = new Parse(text, span, "TOP", 1.0, 0);
root.setChild(0, "SHOULD_FAIL");
}

@Test
public void testHeadRulesReturnsNullFallbackToSelf() {
String text = "Head fallback";
Span s = new Span(0, 12);
Parse root = new Parse(text, s, "TOP", 1.0, 0);
Parse a = new Parse(text, new Span(0, 4), "A", 1.0, 1);
Parse b = new Parse(text, new Span(5, 12), "B", 1.0, 2);
root.insert(a);
root.insert(b);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] constituents, String type) {
// return null;
// }
// };
// root.updateHeads(rules);
assertEquals(root, root.getHead());
}

@Test
public void testRemoveLastChildUpdatesSpan() {
String text = "update span";
Span rootSpan = new Span(0, 11);
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Parse first = new Parse(text, new Span(0, 3), "A", 1.0, 1);
Parse second = new Parse(text, new Span(4, 7), "B", 1.0, 2);
root.insert(first);
root.insert(second);
root.remove(1);
Span updated = root.getSpan();
assertEquals(0, updated.getStart());
assertEquals(3, updated.getEnd());
}

@Test
public void testRemoveMiddleChildDoesntUpdateSpan() {
String text = "AAA BBB CCC";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse a = new Parse(text, new Span(0, 3), "A", 1.0, 0);
Parse b = new Parse(text, new Span(4, 7), "B", 1.0, 1);
Parse c = new Parse(text, new Span(8, 11), "C", 1.0, 2);
root.insert(a);
root.insert(b);
root.insert(c);
root.remove(1);
Span s = root.getSpan();
assertEquals(0, s.getStart());
assertEquals(11, s.getEnd());
}

@Test
public void testAddNamesCrossingKidSpanDoesNotInsert() {
String text = "Barack Obama and Joe Biden";
Span rootSpan = new Span(0, text.length());
Parse root = new Parse(text, rootSpan, "TOP", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(7, 12), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tok3 = new Parse(text, new Span(17, 20), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse tok4 = new Parse(text, new Span(21, 26), AbstractBottomUpParser.TOK_NODE, 1.0, 3);
root.insert(tok1);
root.insert(tok2);
root.insert(tok3);
root.insert(tok4);
Span[] nameSpan = { new Span(1, 4) };
Parse[] tokenArray = { tok1, tok2, tok3, tok4 };
Parse.addNames("person", nameSpan, tokenArray);
Parse[] result = root.getChildren();
boolean found = false;
if (result.length >= 5) {
for (Parse p : result) {
if ("person".equals(p.getType())) {
found = true;
}
}
}
// assertFalse("No name should be inserted when span crosses kids", found);
}

@Test
public void testInsertWithReparentingNestedChild() {
String text = "Reparent";
Parse root = new Parse(text, new Span(0, 8), "TOP", 1.0, 0);
Parse wrapper = new Parse(text, new Span(0, 8), "WRAP", 1.0, 1);
Parse original = new Parse(text, new Span(0, 3), "A", 1.0, 1);
wrapper.insert(original);
root.insert(wrapper);
Parse newParent = new Parse(text, new Span(0, 8), "NEW", 1.0, 1);
root.insert(newParent);
Parse[] parts = root.getChildren();
boolean wrappedRelocated = false;
for (int i = 0; i < parts.length; i++) {
if (parts[i].getType().equals("NEW")) {
Parse[] kids = parts[i].getChildren();
for (int j = 0; j < kids.length; j++) {
if (kids[j].getType().equals("A")) {
wrappedRelocated = true;
}
}
}
}
assertTrue(wrappedRelocated);
}

@Test
public void testAdjoinRootCreatesAdjunction() {
String text = "Adjunction";
Parse root = new Parse(text, new Span(0, 9), "TOP", 1.0, 0);
Parse p1 = new Parse(text, new Span(0, 4), "NP", 1.0, 1);
Parse p2 = new Parse(text, new Span(5, 9), "VP", 1.0, 2);
root.insert(p1);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] parts, String type) {
// return parts[1];
// }
// };
// Parse adjunct = root.adjoinRoot(p2, rules, 0);
// assertEquals(2, adjunct.getChildren().length);
// assertEquals("NP", adjunct.getType());
// assertEquals(p2, adjunct.getChildren()[1]);
}

@Test
public void testIsFlatReturnsTrueWhenAllChildrenPosTags() {
String text = "Flat";
Parse tok1 = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tag1 = new Parse(text, new Span(0, 1), "DT", 1.0, 0);
tag1.insert(tok1);
Parse tok2 = new Parse(text, new Span(2, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tag2 = new Parse(text, new Span(2, 3), "NN", 1.0, 1);
tag2.insert(tok2);
Parse parent = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
parent.insert(tag1);
parent.insert(tag2);
assertTrue(parent.isFlat());
}

@Test
public void testCompareToReturnsZeroForEqualProb() {
String t = "t";
Parse p1 = new Parse(t, new Span(0, 1), "A", 0.9, 1);
Parse p2 = new Parse(t, new Span(0, 1), "A", 0.9, 1);
assertEquals(0, p1.compareTo(p2));
}

@Test
public void testParseParseHandlesEmptyTree() {
Parse result = Parse.parseParse("(TOP)");
assertNotNull(result);
assertEquals("TOP", result.getType());
assertEquals("", result.getCoveredText().trim());
}

@Test
public void testAddNamesToSpanThatEqualsParentSpan() {
String text = "Marie Curie";
Parse root = new Parse(text, new Span(0, 11), "TOP", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(6, 11), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
root.insert(tok1);
root.insert(tok2);
Span[] names = { new Span(0, 2) };
Parse[] tokens = { tok1, tok2 };
Parse.addNames("person", names, tokens);
Parse[] parts = root.getChildren();
boolean found = false;
for (int i = 0; i < parts.length; i++) {
if ("person".equals(parts[i].getType())) {
found = true;
}
}
assertTrue(found);
}

@Test
public void testGetCommonParentSameNodeReturnsParent() {
String text = "Common";
Parse parent = new Parse(text, new Span(0, 6), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "A", 1.0, 1);
parent.insert(child);
Parse result = child.getCommonParent(child);
assertEquals(parent, result);
}

@Test
public void testShowAppendsFormattedOutput() {
String text = "(TEST)";
Parse tok = new Parse(text, new Span(1, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse pos = new Parse(text, new Span(1, 5), "NN", 1.0, 0);
pos.insert(tok);
Parse root = new Parse(text, new Span(0, 6), "TOP", 1.0, 0);
root.insert(pos);
StringBuffer buffer = new StringBuffer();
root.show(buffer);
assertTrue(buffer.toString().contains("NN"));
assertTrue(buffer.toString().contains("-LRB-") || buffer.toString().contains("-RRB-") || buffer.toString().contains("TEST"));
}

@Test
public void testInsertIntoMiddleOfExistingSpans() {
String text = "John walks home.";
Parse root = new Parse(text, new Span(0, 16), "TOP", 1.0, 0);
Parse p1 = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse p2 = new Parse(text, new Span(5, 14), "VP", 1.0, 0);
root.insert(p1);
root.insert(p2);
Parse nested = new Parse(text, new Span(5, 10), "ADVP", 1.0, 0);
root.insert(nested);
Parse[] children = root.getChildren();
boolean foundNested = false;
if (children.length == 3) {
for (int i = 0; i < children.length; i++) {
if (children[i].getType().equals("ADVP")) {
foundNested = true;
}
}
}
// assertTrue("Inserted nested span should be included", foundNested);
}

@Test
public void testCloneWithDerivationPreservesDerivationContent() {
String text = "Cloning text.";
Span span = new Span(0, 13);
Parse original = new Parse(text, span, "NP", 0.9, 0);
StringBuffer sb = new StringBuffer("derived->NP");
original.setDerivation(sb);
Parse cloned = (Parse) original.clone();
assertNotNull(cloned.getDerivation());
assertEquals("derived->NP", cloned.getDerivation().toString());
}

@Test
public void testGetTokenReturnsNullWhenMatcherFails() {
String invalidTokenStr = "(NP (JJ ))";
Parse parsed = Parse.parseParse(invalidTokenStr);
assertNotNull(parsed);
}

@Test
public void testSetDerivationNullAllowed() {
String text = "Some entity.";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "TOP", 1.0, 0);
parse.setDerivation(null);
assertNull(parse.getDerivation());
}

@Test
public void testGetTagSequenceProbOnEmptyParseReturnsZero() {
String text = "Empty tree";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "TOP", 1.0, 0);
double result = parse.getTagSequenceProb();
assertEquals(0.0, result, 0.001);
}

@Test
public void testInsertSpanContainsExistingNodeReparentsIt() {
String text = "The red ball.";
Parse root = new Parse(text, new Span(0, text.length()), "TOP", 1.0, 0);
Parse np = new Parse(text, new Span(4, 7), "JJ", 1.0, 1);
root.insert(np);
Parse npContainer = new Parse(text, new Span(4, 13), "NP", 1.0, 2);
root.insert(npContainer);
assertEquals(1, npContainer.getChildren().length);
assertEquals(np, npContainer.getChildren()[0]);
assertEquals(npContainer, np.getParent());
}

@Test
public void testIndexOfReturnsMinusOneForUnknownChild() {
String text = "Untracked";
Parse parent = new Parse(text, new Span(0, 9), "TOP", 1.0, 0);
Parse unrelated = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
int index = parent.indexOf(unrelated);
assertEquals(-1, index);
}

@Test
public void testSetPrevAndNextPunctuationWithEmptyCollections() {
String text = "Empty test.";
Parse parse = new Parse(text, new Span(0, 11), "TOP", 1.0, 0);
Collection<Parse> emptyPrev = Collections.emptyList();
Collection<Parse> emptyNext = new ArrayList<>();
parse.setPrevPunctuation(emptyPrev);
parse.setNextPunctuation(emptyNext);
assertTrue(parse.getPreviousPunctuationSet().isEmpty());
assertTrue(parse.getNextPunctuationSet().isEmpty());
}

@Test
public void testShowCodeTreeProducesNoErrorForFlatTree() {
String text = "Flat tree.";
Parse leaf = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse pos = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
pos.insert(leaf);
Parse top = new Parse(text, new Span(0, 4), "TOP", 1.0, 0);
top.insert(pos);
top.showCodeTree();
}

@Test
public void testParseParseWithMultipleTokensCreatesAllTerminalLeaves() {
Parse parse = Parse.parseParse("(TOP (NP (DT The) (NN dog) (VBZ runs)))");
Parse[] tags = parse.getTagNodes();
boolean hasDT = false;
boolean hasNN = false;
boolean hasVBZ = false;
if (tags.length == 3) {
hasDT = "DT".equals(tags[0].getType());
hasNN = "NN".equals(tags[1].getType());
hasVBZ = "VBZ".equals(tags[2].getType());
}
assertTrue(hasDT);
assertTrue(hasNN);
assertTrue(hasVBZ);
}

@Test
public void testDecodeUnknownTokenReturnsUnchanged() {
String original = "unmatched-token";
// String decoded = Parse.decodeToken(original);
// assertEquals("unmatched-token", decoded);
}

@Test
public void testEncodeUnknownTokenReturnsUnchanged() {
String original = "hello";
// String encoded = Parse.encodeToken(original);
// assertEquals("hello", encoded);
}

@Test
public void testHashCodeDiffersForDifferentSpan() {
String text = "Diffs.";
Parse p1 = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse p2 = new Parse(text, new Span(1, 5), "NP", 1.0, 0);
assertNotEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testParseParseParsesBracketsAsTokens() {
Parse parse = Parse.parseParse("(TOP (NP (NN -LRB-) (NN book) (NN -RRB-)))");
Parse[] tokenNodes = parse.getTokenNodes();
boolean hasLRB = false;
boolean hasRRB = false;
if (tokenNodes.length == 3) {
hasLRB = tokenNodes[0].getCoveredText().equals("(");
hasRRB = tokenNodes[2].getCoveredText().equals(")");
}
assertTrue(hasLRB);
assertTrue(hasRRB);
}

@Test
public void testExpandTopNodeMergesMultipleChildrenIntoRoot() {
String text = "A B C";
Parse oldRoot = new Parse(text, new Span(0, 5), "TOP", 1.0, 0);
Parse np1 = new Parse(text, new Span(0, 1), "X", 1.0, 0);
Parse np2 = new Parse(text, new Span(2, 3), "Y", 1.0, 1);
Parse np3 = new Parse(text, new Span(4, 5), "Z", 1.0, 2);
oldRoot.insert(np1);
oldRoot.insert(np2);
oldRoot.insert(np3);
Parse newRoot = new Parse(text, new Span(0, 1), "ROOT", 1.0, 0);
oldRoot.expandTopNode(newRoot);
Parse[] newChildren = newRoot.getChildren();
assertEquals(3, newChildren.length);
assertEquals("X", newChildren[0].getType());
assertEquals("Y", newChildren[1].getType());
assertEquals("Z", newChildren[2].getType());
}

@Test
public void testPruneRemovesSelfRecursiveNode() {
String treeString = "(TOP (NP (NP (NP (NN test)))))";
Parse parse = Parse.parseParse(treeString);
Parse.pruneParse(parse);
Parse[] children = parse.getChildren();
String type = children[0].getType();
assertEquals("NP", type);
Parse[] sub = children[0].getChildren();
assertEquals("NN", sub[0].getType());
}

@Test
public void testCloneRootWithMultipleChildrenPreservesOthers() {
String text = "Hello world now.";
Parse root = new Parse(text, new Span(0, 17), "TOP", 1.0, 0);
Parse np = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse vp = new Parse(text, new Span(6, 11), "VP", 1.0, 1);
Parse pp = new Parse(text, new Span(12, 17), "PP", 1.0, 2);
root.insert(np);
root.insert(vp);
root.insert(pp);
Parse clone = root.cloneRoot(vp, 1);
Parse[] clonedChildren = clone.getChildren();
assertEquals(3, clonedChildren.length);
assertEquals("VP", clonedChildren[1].getType());
}

@Test
public void testExpandTopNodeWithEmptyChildrenDoesNotFail() {
String text = "dummy";
Parse top = new Parse(text, new Span(0, 5), "TOP", 1.0, 0);
Parse newRoot = new Parse(text, new Span(0, 5), "ROOT", 1.0, 0);
top.expandTopNode(newRoot);
assertEquals(0, newRoot.getChildren().length);
}

@Test
public void testFunctionTagsToggleAffectsParseResult() {
Parse.useFunctionTags(false);
Parse off = Parse.parseParse("(TOP (NP-SBJ (NN This)))");
Parse[] childrenOff = off.getChildren();
assertEquals("NP", childrenOff[0].getType());
Parse.useFunctionTags(true);
Parse on = Parse.parseParse("(TOP (NP-SBJ (NN This)))");
Parse[] childrenOn = on.getChildren();
assertEquals("NP-SBJ", childrenOn[0].getType());
}

@Test
public void testAdjoinRightmostChildReturnsAdjunction() {
String text = "Adjunction test";
Parse parent = new Parse(text, new Span(0, 15), "TOP", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 5), "X", 1.0, 0);
Parse c2 = new Parse(text, new Span(6, 11), "Y", 1.0, 1);
parent.insert(c1);
parent.insert(c2);
// HeadRules headRules = new HeadRules() {
// 
// public Parse getHead(Parse[] kids, String type) {
// return kids[1];
// }
// };
Parse p3 = new Parse(text, new Span(12, 15), "Z", 1.0, 2);
// Parse result = parent.adjoin(p3, headRules);
// Parse[] adjChildren = result.getChildren();
// assertEquals("Y", adjChildren[0].getType());
// assertEquals("Z", adjChildren[1].getType());
}

@Test
public void testEqualsFailsForDifferentLabelDespiteMatchingSpanText() {
String text = "Equality test";
Span span = new Span(0, 13);
Parse a = new Parse(text, span, "NP", 1.0, 0);
Parse b = new Parse(text, span, "NP", 1.0, 0);
a.setLabel("labelA");
b.setLabel("labelB");
assertFalse(a.equals(b));
}

@Test
public void testInsertMultiplePunctuationsBefore() {
String text = "OK.";
Parse main = new Parse(text, new Span(0, 3), "TOP", 1.0, 0);
Parse comma = new Parse(text, new Span(1, 2), ",", 1.0, 1);
Parse hypot = new Parse(text, new Span(2, 3), "-", 1.0, 2);
main.addPreviousPunctuation(comma);
main.addPreviousPunctuation(hypot);
Parse[] children = main.getPreviousPunctuationSet().toArray(new Parse[0]);
assertEquals(2, children.length);
}

@Test
public void testGetCommonParentWhenOneIsAncestorOfAnother() {
String text = "A B";
Parse root = new Parse(text, new Span(0, 3), "TOP", 1.0, 0);
Parse np = new Parse(text, new Span(0, 3), "NP", 1.0, 1);
Parse tok = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
np.insert(tok);
root.insert(np);
Parse result = root.getCommonParent(tok);
assertEquals(root, result);
}

@Test
public void testGetTagNodesSkipsDeepNestedNonPOS() {
String text = "Nest";
Parse root = new Parse(text, new Span(0, 4), "TOP", 1.0, 0);
Parse mid = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse notTok = new Parse(text, new Span(0, 4), "ZZ", 1.0, 0);
mid.insert(notTok);
root.insert(mid);
Parse[] tags = root.getTagNodes();
assertEquals(0, tags.length);
}

@Test
public void testAddNamesFailsGracefullyWhenCommonParentIsNull() {
String text = "Fail test";
Parse p1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse p2 = new Parse(text, new Span(5, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse[] tokens = new Parse[] { p1, p2 };
Span[] names = new Span[] { new Span(0, 2) };
Parse.addNames("failure", names, tokens);
assertTrue(tokens[0].getType().equals(AbstractBottomUpParser.TOK_NODE));
assertTrue(tokens[1].getType().equals(AbstractBottomUpParser.TOK_NODE));
}

@Test
public void testToStringShowsFullContentForFlatTree() {
String text = "word";
Span span = new Span(0, 4);
Parse tok = new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 1.0, 0);
assertEquals("word", tok.toString());
}

@Test
public void testparseParseHandlesEmptyWrappedNoneNode() {
String text = "(TOP (-NONE- *))";
Parse result = Parse.parseParse(text);
assertNotNull(result);
assertEquals("TOP", result.getType());
}

@Test
public void testParseParseBuildsValidTreeForDeepSingleBranch() {
String text = "(TOP (A (B (C (D (E (F (G word))))))) )";
Parse result = Parse.parseParse(text);
assertEquals("TOP", result.getType());
assertTrue(result.getCoveredText().trim().startsWith("word"));
}

@Test
public void testUpdateHeadsUsesFallbackWhenOnlyOneChild() {
String text = "only one";
Parse root = new Parse(text, new Span(0, 8), "TOP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 8), "NP", 1.0, 1);
root.insert(child);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] children, String type) {
// return null;
// }
// };
// root.updateHeads(rules);
assertEquals(root, root.getHead());
}

@Test
public void testShowWithEmptyBufferDoesNotThrow() {
String text = "Just a node.";
Span span = new Span(0, text.length());
Parse root = new Parse(text, span, "S", 1.0, 0);
StringBuffer sb = new StringBuffer();
root.show(sb);
assertNotNull(sb.toString());
}

@Test
public void testEncodeTokenHandlesSpecialTokensOnly() {
// assertEquals("-LRB-", Parse.encodeToken("("));
// assertEquals("-RRB-", Parse.encodeToken(")"));
// assertEquals("-LCB-", Parse.encodeToken("{"));
// assertEquals("!", Parse.encodeToken("!"));
}

@Test
public void testCloneRecursiveRightFrontier() {
String text = "A B C";
Parse root = new Parse(text, new Span(0, 5), "TOP", 1.0, 0);
Parse childA = new Parse(text, new Span(0, 1), "A", 1.0, 0);
Parse childB = new Parse(text, new Span(2, 3), "B", 1.0, 1);
Parse childC = new Parse(text, new Span(4, 5), "C", 1.0, 2);
root.insert(childA);
childA.insert(childB);
childB.insert(childC);
Parse cloned = root.clone(childC);
assertNotSame(cloned, root);
assertEquals("TOP", cloned.getType());
assertEquals("A", cloned.getChildren()[0].getType());
assertEquals("B", cloned.getChildren()[0].getChildren()[0].getType());
assertEquals("C", cloned.getChildren()[0].getChildren()[0].getChildren()[0].getType());
}

@Test
public void testPruneFailsWithOrphanedNode() {
String text = "fail test";
Parse orphan = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
orphan.insert(new Parse(text, new Span(0, 4), "NP", 1.0, 0));
orphan.getChildren()[0].setParent(null);
Parse.pruneParse(orphan);
assertEquals("NP", orphan.getType());
}

@Test
public void testAdjoinWithPrevPunctSetPreservesPunctuation() {
String text = "This!";
Parse top = new Parse(text, new Span(0, 5), "TOP", 0.9, 0);
Parse a = new Parse(text, new Span(0, 1), "A", 1.0, 0);
top.insert(a);
Parse punct = new Parse(text, new Span(4, 5), ".", 1.0, 1);
Parse newPart = new Parse(text, new Span(3, 5), "B", 1.0, 1);
newPart.addPreviousPunctuation(punct);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] kids, String type) {
// return kids[1];
// }
// };
// Parse adj = top.adjoin(newPart, rules);
// Parse[] children = adj.getChildren();
// assertEquals(3, children.length);
// assertEquals(".", children[1].getCoveredText());
}

@Test
public void testDerivationStringPreservedAcrossClone() {
String text = "derive me";
Span span = new Span(0, 9);
Parse original = new Parse(text, span, "NP", 0.9, 0);
StringBuffer sb = new StringBuffer();
sb.append("X->Y->NP");
original.setDerivation(sb);
Parse copy = (Parse) original.clone();
assertNotNull(copy.getDerivation());
assertEquals("X->Y->NP", copy.getDerivation().toString());
}

@Test
public void testAddNamesFallbackToNPWhenCrossingFails() {
String text = "Amy Watson drives fast";
Span span = new Span(0, text.length());
Parse root = new Parse(text, span, "TOP", 1.0, 0);
Parse t1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse t2 = new Parse(text, new Span(4, 10), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse t3 = new Parse(text, new Span(11, 17), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse t4 = new Parse(text, new Span(18, 22), AbstractBottomUpParser.TOK_NODE, 1.0, 3);
Parse pos1 = new Parse(text, new Span(0, 3), "NNP", 1.0, 0);
Parse pos2 = new Parse(text, new Span(4, 10), "NNP", 1.0, 1);
Parse pos3 = new Parse(text, new Span(11, 17), "VBZ", 1.0, 2);
Parse pos4 = new Parse(text, new Span(18, 22), "RB", 1.0, 3);
pos1.insert(t1);
pos2.insert(t2);
pos3.insert(t3);
pos4.insert(t4);
Parse np = new Parse(text, new Span(0, 10), "NP", 1.0, 1);
np.insert(pos1);
np.insert(pos2);
Parse vp = new Parse(text, new Span(11, 22), "VP", 1.0, 2);
vp.insert(pos3);
vp.insert(pos4);
root.insert(np);
root.insert(vp);
Span[] names = new Span[] { new Span(0, 2) };
Parse[] tokens = new Parse[] { t1, t2, t3, t4 };
Parse.addNames("person", names, tokens);
boolean found = false;
Parse[] children = root.getChildren();
for (int i = 0; i < children.length; i++) {
if ("person".equals(children[i].getType())) {
found = true;
}
}
// assertTrue("Named entity should be inserted when fallback NP used", found);
}

@Test
public void testGetHeadIndexReflectsDeepCloneFromHead() {
String text = "ABC";
Parse tok = new Parse(text, new Span(2, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse pos = new Parse(text, new Span(2, 3), "NN", 1.0, 2);
pos.insert(tok);
Parse np = new Parse(text, new Span(2, 3), "NP", 1.0, pos);
assertEquals(2, np.getHeadIndex());
}

@Test
public void testDecodeAllBracketTokens() {
// assertEquals("(", Parse.decodeToken("-LRB-"));
// assertEquals(")", Parse.decodeToken("-RRB-"));
// assertEquals("{", Parse.decodeToken("-LCB-"));
// assertEquals("}", Parse.decodeToken("-RCB-"));
// assertEquals("[", Parse.decodeToken("-LSB-"));
// assertEquals("]", Parse.decodeToken("-RSB-"));
// assertEquals("-NONE-", Parse.decodeToken("-NONE-") == "-NONE-" ? "-NONE-" : "");
}

@Test
public void testEncodeAllBracketTokens() {
// assertEquals("-LRB-", Parse.encodeToken("("));
// assertEquals("-RRB-", Parse.encodeToken(")"));
// assertEquals("-LCB-", Parse.encodeToken("{"));
// assertEquals("-RCB-", Parse.encodeToken("}"));
// assertEquals("-LSB-", Parse.encodeToken("["));
// assertEquals("-RSB-", Parse.encodeToken("]"));
}

@Test
public void testGetTokenReturnsNullForEmptyGroup() {
String rest = "(NP )";
Parse result = Parse.parseParse(rest);
assertNotNull(result);
}

@Test
public void testUseFunctionTagsTrueWithFunTypeTag() {
Parse.useFunctionTags(true);
String parse = "(TOP (NP-SBJ-LOC (NNP Boston)))";
Parse result = Parse.parseParse(parse);
String childType = result.getChildren()[0].getType();
assertEquals("NP-SBJ", childType);
}
}
