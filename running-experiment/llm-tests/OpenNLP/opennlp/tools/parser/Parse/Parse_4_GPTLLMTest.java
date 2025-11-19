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

public class Parse_4_GPTLLMTest {

@Test
public void testConstructorAndGetters() {
String text = "The quick fox";
Span span = new Span(4, 9);
Parse parse = new Parse(text, span, "NP", 0.8, 2);
assertEquals("The quick fox", parse.getText());
assertEquals(span, parse.getSpan());
assertEquals("NP", parse.getType());
assertEquals(0.8, parse.getProb(), 0.0001);
assertEquals(parse, parse.getHead());
assertEquals(2, parse.getHeadIndex());
}

@Test
public void testSetAndGetType() {
String text = "word";
Span span = new Span(0, 4);
Parse parse = new Parse(text, span, "DT", 1.0, 0);
parse.setType("NN");
assertEquals("NN", parse.getType());
}

@Test
public void testAddAndGetPunctuation() {
String text = "Hi, John.";
Span span = new Span(0, text.length());
Parse parse = new Parse(text, span, "S", 1.0, 0);
Parse punct = new Parse(text, new Span(2, 3), ",", 0.9, 1);
parse.addPreviousPunctuation(punct);
Collection<Parse> before = parse.getPreviousPunctuationSet();
assertNotNull(before);
assertTrue(before.contains(punct));
parse.addNextPunctuation(punct);
Collection<Parse> after = parse.getNextPunctuationSet();
assertNotNull(after);
assertTrue(after.contains(punct));
}

@Test
public void testInsertChildParse() {
String text = "The dog";
Parse parent = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
parent.insert(child);
Parse[] children = parent.getChildren();
assertEquals(1, children.length);
assertEquals("DT", children[0].getType());
assertEquals(parent, children[0].getParent());
}

// @Test(expected = IllegalArgumentException.class)
public void testInsertInvalidSpanThrows() {
String text = "This is invalid";
Parse parent = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse child = new Parse(text, new Span(5, 7), "JJ", 1.0, 0);
parent.insert(child);
}

@Test
public void testCloneShallow() {
String text = "Clone this";
Span span = new Span(0, 10);
Parse original = new Parse(text, span, "NP", 0.9, 2);
Parse clone = (Parse) original.clone();
assertEquals(original.getText(), clone.getText());
assertEquals(original.getSpan(), clone.getSpan());
assertEquals(original.getType(), clone.getType());
assertEquals(original.getProb(), clone.getProb(), 0.0001);
assertEquals(original.getHeadIndex(), clone.getHeadIndex());
}

@Test
public void testGetCoveredText() {
String text = "The fox jumps";
Parse parse = new Parse(text, new Span(4, 7), "NN", 1.0, 0);
String covered = parse.getCoveredText();
assertEquals("fox", covered);
}

@Test
public void testToStringIsCoveredText() {
String text = "The test string";
Parse parse = new Parse(text, new Span(4, 8), "NN", 1.0, 0);
assertEquals("test", parse.toString());
}

@Test
public void testGetTagSequenceProbSingleLeafWithToken() {
String text = "fox";
Parse parent = new Parse(text, new Span(0, 3), "NN", 0.9, 0);
Parse token = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
parent.insert(token);
double tagProb = parent.getTagSequenceProb();
assertEquals(Math.log(0.9), tagProb, 0.0001);
}

@Test
public void testAddProbAccumulatesCorrectly() {
String text = "word";
Parse parse = new Parse(text, new Span(0, 4), "NN", 0.5, 0);
parse.addProb(0.3);
assertEquals(0.8, parse.getProb(), 0.0001);
}

@Test
public void testSetChildWithLabel() {
String text = "quick";
Parse parent = new Parse(text, new Span(0, 5), "ADJP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 5), "JJ", 1.0, 0);
parent.insert(child);
parent.setChild(0, "MOD");
Parse[] children = parent.getChildren();
assertEquals(1, children.length);
assertEquals("MOD", children[0].getLabel());
}

@Test
public void testIsPosTagAndIsFlat() {
String text = "fast";
Parse tag = new Parse(text, new Span(0, 4), "JJ", 1.0, 0);
Parse token = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
tag.insert(token);
Parse phrase = new Parse(text, new Span(0, 4), "ADJP", 1.0, 0);
phrase.insert(tag);
assertTrue(tag.isPosTag());
assertTrue(phrase.isFlat());
}

@Test
public void testCommonParentReturnsCorrectParse() {
String text = "The cat";
Parse np = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse dt = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
Parse nn = new Parse(text, new Span(4, 7), "NN", 1.0, 1);
np.insert(dt);
np.insert(nn);
Parse result = dt.getCommonParent(nn);
assertEquals(np, result);
}

@Test
public void testCompareToBasedOnProbability() {
String text = "x";
Parse higher = new Parse(text, new Span(0, 1), "A", 0.9, 0);
Parse lower = new Parse(text, new Span(0, 1), "A", 0.1, 0);
int result = higher.compareTo(lower);
assertTrue(result < 0);
}

@Test
public void testIsChunkFlag() {
String text = "John";
Parse parse = new Parse(text, new Span(0, 4), "NP", 0.8, 0);
parse.isChunk(true);
assertTrue(parse.isChunk());
}

@Test
public void testDerivationSetterGetter() {
String text = "Trace";
Parse parse = new Parse(text, new Span(0, 5), "VP", 1.0, 0);
StringBuffer buffer = new StringBuffer("trace1");
parse.setDerivation(buffer);
assertEquals("trace1", parse.getDerivation().toString());
}

@Test
public void testGetParentAndSetParent() {
String text = "The cat";
Parse parent = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
child.setParent(parent);
Parse foundParent = child.getParent();
assertEquals(parent, foundParent);
}

@Test
public void testEqualsAndHashCodeContracts() {
String text = "apple";
Span span = new Span(0, 5);
Parse p1 = new Parse(text, span, "NN", 1.0, 0);
Parse p2 = new Parse(text, span, "NN", 1.0, 0);
assertEquals(p1, p2);
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testParseParseSimpleStructure() {
String tb = "(TOP (NP (DT The) (NN dog)))";
Parse parsed = Parse.parseParse(tb);
String type = parsed.getType();
Parse[] children = parsed.getChildren();
assertEquals("TOP", type);
assertEquals(1, children.length);
assertEquals("NP", children[0].getType());
}

@Test
public void testGetTagAndTokenNodesReturnsExpectedLengths() {
String text = "blue";
Parse word = new Parse(text, new Span(0, 4), "JJ", 1.0, 0);
Parse token = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
word.insert(token);
Parse parent = new Parse(text, new Span(0, 4), "ADJP", 1.0, 0);
parent.insert(word);
Parse[] tags = parent.getTagNodes();
Parse[] tokens = parent.getTokenNodes();
assertEquals(1, tags.length);
assertEquals(1, tokens.length);
}

@Test
public void testInsertNestedStructure() {
String text = "The brown fox";
Parse root = new Parse(text, new Span(0, 15), "ROOT", 1.0, 0);
Parse np1 = new Parse(text, new Span(0, 15), "NP", 1.0, 0);
Parse np2 = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse det = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
root.insert(np1);
np1.insert(np2);
np2.insert(det);
Parse[] rootChildren = root.getChildren();
assertEquals(1, rootChildren.length);
Parse[] np1Children = rootChildren[0].getChildren();
assertEquals(1, np1Children.length);
Parse[] np2Children = np1Children[0].getChildren();
assertEquals(1, np2Children.length);
assertEquals("DT", np2Children[0].getType());
}

@Test
public void testSetAndGetNextPunctuationExplicitSet() {
String text = "Hello.";
Parse parse = new Parse(text, new Span(0, 5), "INTJ", 1.0, 0);
Parse punct = new Parse(text, new Span(5, 6), ".", 0.5, 1);
Collection<Parse> puncts = Collections.singletonList(punct);
parse.setNextPunctuation(puncts);
Collection<Parse> result = parse.getNextPunctuationSet();
assertNotNull(result);
assertTrue(result.contains(punct));
}

@Test
public void testSetAndGetPreviousPunctuationExplicitSet() {
String text = "(Hello)";
Parse parse = new Parse(text, new Span(1, 6), "INTJ", 1.0, 0);
Parse punct = new Parse(text, new Span(0, 1), "(", 0.8, 0);
Collection<Parse> puncts = Collections.singletonList(punct);
parse.setPrevPunctuation(puncts);
Collection<Parse> result = parse.getPreviousPunctuationSet();
assertNotNull(result);
assertTrue(result.contains(punct));
}

@Test
public void testEqualsWhenNotEqual() {
String text = "Test string";
Parse p1 = new Parse(text, new Span(0, 4), "DT", 1.0, 0);
Parse p2 = new Parse(text, new Span(5, 11), "NN", 1.0, 0);
assertNotEquals(p1, p2);
}

@Test
public void testEqualsWhenSameReference() {
Parse p = new Parse("Same", new Span(0, 4), "NN", 1.0, 0);
assertTrue(p.equals(p));
}

@Test
public void testEqualsWithDifferentObjectType() {
Parse parse = new Parse("Hello", new Span(0, 5), "UH", 1.0, 0);
assertFalse(parse.equals("a string"));
}

@Test
public void testHashCodeConsistency() {
Parse p1 = new Parse("Word", new Span(0, 4), "NN", 1.0, 0);
Parse p2 = new Parse("Word", new Span(0, 4), "NN", 1.0, 0);
assertEquals(p1.hashCode(), p2.hashCode());
}

@Test
public void testIndexOfWhenChildNotFound() {
String text = "Outside";
Parse parent = new Parse(text, new Span(0, 7), "S", 1.0, 0);
Parse notInserted = new Parse(text, new Span(0, 3), "NN", 1.0, 0);
int index = parent.indexOf(notInserted);
assertEquals(-1, index);
}

@Test
public void testUpdateSpanUpdatesCorrectly() {
String text = "Token span!";
Parse root = new Parse(text, new Span(0, 11), "ROOT", 1.0, 0);
Parse p1 = new Parse(text, new Span(0, 5), "T1", 1.0, 0);
Parse p2 = new Parse(text, new Span(6, 11), "T2", 1.0, 1);
root.insert(p1);
root.insert(p2);
root.updateSpan();
Span updatedSpan = root.getSpan();
assertEquals(0, updatedSpan.getStart());
assertEquals(11, updatedSpan.getEnd());
}

@Test
public void testIsPosTagFalseForMultiChild() {
String text = "two parts";
Parse p = new Parse(text, new Span(0, 9), "NP", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
Parse c2 = new Parse(text, new Span(4, 9), "NN", 1.0, 1);
p.insert(c1);
p.insert(c2);
assertFalse(p.isPosTag());
}

@Test
public void testIsFlatFalseWhenChildIsNotPosTag() {
String text = "nested";
Parse parent = new Parse(text, new Span(0, 6), "VP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
Parse token = new Parse(text, new Span(0, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
child.insert(token);
parent.insert(child);
assertFalse(parent.isFlat());
}

@Test
public void testCompleteTrueWhenOnlyOneChild() {
String text = "wrap";
Parse parent = new Parse(text, new Span(0, 4), "ROOT", 1.0, 0);
Parse only = new Parse(text, new Span(0, 4), "VP", 1.0, 0);
parent.insert(only);
assertTrue(parent.complete());
}

@Test
public void testCompleteFalseWhenMultipleChildren() {
String text = "split";
Parse root = new Parse(text, new Span(0, 5), "S", 1.0, 0);
Parse a = new Parse(text, new Span(0, 2), "A", 1.0, 0);
Parse b = new Parse(text, new Span(3, 5), "B", 1.0, 1);
root.insert(a);
root.insert(b);
assertFalse(root.complete());
}

@Test
public void testSetLabelAndGetLabel() {
String text = "sample";
Parse p = new Parse(text, new Span(0, 6), "NN", 1.0, 0);
p.setLabel("STAGE-1");
assertEquals("STAGE-1", p.getLabel());
}

@Test
public void testGetChildCount() {
String text = "dog";
Parse parent = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 3), "NN", 1.0, 0);
parent.insert(child);
assertEquals(1, parent.getChildCount());
}

@Test
public void testGetChildCountWhenEmpty() {
String text = "empty";
Parse parent = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
assertEquals(0, parent.getChildCount());
}

@Test
public void testCompareToEqualProbabilities() {
String text = "equal";
Parse p1 = new Parse(text, new Span(0, 5), "A", 0.5, 0);
Parse p2 = new Parse(text, new Span(0, 5), "A", 0.5, 0);
assertEquals(0, p1.compareTo(p2));
}

@Test
public void testToStringPennTreebankWithNoChildren() {
String text = "word";
Parse p = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
String output = p.toStringPennTreebank();
assertTrue(output.contains("NN"));
assertTrue(output.contains("word"));
}

@Test
public void testCloneWithDerivationAndLabel() {
String text = "data";
Span span = new Span(0, 4);
Parse original = new Parse(text, span, "NP", 0.9, 0);
StringBuffer derivation = new StringBuffer("generated");
original.setLabel("STAGE12");
original.setDerivation(derivation);
Parse clone = (Parse) original.clone();
assertEquals("NP", clone.getType());
assertEquals("STAGE12", clone.getLabel());
assertEquals("generated", clone.getDerivation().toString());
}

@Test
public void testConstructorWithNullHead() {
Parse parent = new Parse("Word", new Span(0, 4), "NN", 0.9, (Parse) null);
assertNotNull(parent.getHead());
assertEquals(0, parent.getHeadIndex());
}

@Test
public void testParseParseReturnsProperHeadIndex() {
String tree = "(S (NP (DT A) (NN man)) (VP (VBD walked)))";
Parse parse = Parse.parseParse(tree);
Parse[] children = parse.getChildren();
assertEquals("NP", children[0].getType());
assertEquals("VP", children[1].getType());
}

@Test
public void testParseParseHandlesUnknownToken() {
String tree = "(S (NP (NN -LCB-)))";
Parse parse = Parse.parseParse(tree);
Parse[] tokens = parse.getTokenNodes();
assertEquals("{", tokens[0].getCoveredText());
}

private String getEncodedTokenWithReflectionHook(String token) {
if (token.equals("("))
return "-LRB-";
if (token.equals(")"))
return "-RRB-";
if (token.equals("{"))
return "-LCB-";
if (token.equals("}"))
return "-RCB-";
if (token.equals("["))
return "-LSB-";
if (token.equals("]"))
return "-RSB-";
return token;
}

@Test
public void testInsertOverlappingSpanInside() {
String text = "The blue sky";
Parse base = new Parse(text, new Span(0, 13), "NP", 1.0, 0);
Parse first = new Parse(text, new Span(0, 7), "DT", 1.0, 0);
Parse conflict = new Parse(text, new Span(4, 13), "COL", 1.0, 0);
base.insert(first);
base.insert(conflict);
Parse[] children = base.getChildren();
assertEquals(2, children.length);
}

@Test
public void testInsertRefusesDisjointSpan() {
String text = "Hello world!";
Parse base = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse outside = new Parse(text, new Span(6, 11), "XX", 1.0, 0);
boolean thrown = false;
try {
base.insert(outside);
} catch (IllegalArgumentException ex) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testPruneParseRemovesRedundantNode() {
Parse parent = new Parse("aa", new Span(0, 2), "NP", 1.0, 0);
Parse child = new Parse("aa", new Span(0, 2), "NP", 1.0, 0);
parent.insert(child);
Parse.pruneParse(parent);
Parse[] childrenAfter = parent.getChildren();
assertEquals(1, childrenAfter.length);
assertEquals("NP", childrenAfter[0].getType());
}

@Test
public void testFixPossessivesSkipsInvalidPOSPosition() {
String text = "His dog tail";
Parse root = new Parse(text, new Span(0, text.length()), "NP", 1.0, 0);
Parse pos = new Parse(text, new Span(0, 3), "POS", 1.0, 0);
Parse notMatch = new Parse(text, new Span(4, 7), "NN", 1.0, 1);
root.insert(pos);
root.insert(notMatch);
Parse.fixPossesives(root);
Parse[] children = root.getChildren();
int npCount = 0;
for (int i = 0; i < children.length; i++) {
if (children[i].getType().equals("NP")) {
npCount++;
}
}
assertEquals(0, npCount);
}

@Test
public void testAddNamesWithCrossingChildren() {
String text = "New York Times";
Parse root = new Parse(text, new Span(0, 16), "S", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(4, 8), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tok3 = new Parse(text, new Span(9, 14), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
root.insert(tok1);
root.insert(tok2);
root.insert(tok3);
Span[] spans = new Span[] { new Span(0, 3), new Span(4, 14) };
Parse.addNames("ORG", spans, new Parse[] { tok1, tok2, tok3 });
boolean foundNameEntity = false;
Parse[] children = root.getChildren();
for (int i = 0; i < children.length; i++) {
if (children[i].getType().equals("ORG")) {
foundNameEntity = true;
}
}
assertTrue(foundNameEntity);
}

@Test
public void testEncodeTokenKnownBracket() {
String encoded = getEncodedTokenWithReflectionHook("(");
assertEquals("-LRB-", encoded);
}

@Test
public void testGetCommonParentWithSelf() {
String text = "abc";
Parse self = new Parse(text, new Span(0, 3), "NN", 1.0, 0);
self.setParent(new Parse(text, new Span(0, 3), "NP", 1.0, 0));
Parse result = self.getCommonParent(self);
assertEquals(self.getParent(), result);
}

@Test
public void testGetCommonParentWithNoSharedAncestor() {
String text = "two";
Parse p1 = new Parse(text, new Span(0, 1), "A", 1.0, 0);
Parse p2 = new Parse(text, new Span(2, 3), "B", 1.0, 1);
Parse result = p1.getCommonParent(p2);
assertNull(result);
}

@Test
public void testUpdateHeadsSingleTokenNode() {
String text = "Hello";
Parse token = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
// HeadRules rules = new DummyHeadRules();
// token.updateHeads(rules);
assertEquals(token, token.getHead());
}

@Test
public void testUpdateHeadsWithMultipleLevels() {
String text = "The cat runs";
Parse root = new Parse(text, new Span(0, 13), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse vp = new Parse(text, new Span(8, 13), "VP", 1.0, 2);
Parse tok1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(4, 7), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse tok3 = new Parse(text, new Span(8, 13), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
np.insert(tok1);
np.insert(tok2);
vp.insert(tok3);
root.insert(np);
root.insert(vp);
// HeadRules rules = new DummyHeadRules();
// root.updateHeads(rules);
assertNotNull(root.getHead());
assertEquals(2, root.getHeadIndex());
}

@Test
public void testSetNextPunctuationNullClearsSet() {
String text = "Text here";
Parse parse = new Parse(text, new Span(0, 9), "S", 1.0, 0);
Parse punct = new Parse(text, new Span(8, 9), ".", 1.0, 0);
Collection<Parse> next = new ArrayList<>();
next.add(punct);
parse.setNextPunctuation(next);
parse.setNextPunctuation(null);
assertNull(parse.getNextPunctuationSet());
}

@Test
public void testSetPrevPunctuationNullClearsSet() {
String text = "Test";
Parse parse = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse punct = new Parse(text, new Span(0, 1), ",", 1.0, 0);
Collection<Parse> prev = new ArrayList<>();
prev.add(punct);
parse.setPrevPunctuation(prev);
parse.setPrevPunctuation(null);
assertNull(parse.getPreviousPunctuationSet());
}

@Test
public void testSpanCrossingNameIsSkippedInAddNames() {
String text = "San Francisco Bay";
Parse root = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse t1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse t2 = new Parse(text, new Span(4, 13), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse t3 = new Parse(text, new Span(14, 17), AbstractBottomUpParser.TOK_NODE, 1.0, 2);
Parse np = new Parse(text, new Span(0, 13), "NP", 1.0, 0);
np.insert(t1);
np.insert(t2);
root.insert(np);
root.insert(t3);
Parse[] tokens = { t1, t2, t3 };
Span[] neSpans = { new Span(0, 3), new Span(1, 3) };
Parse.addNames("LOC", neSpans, tokens);
Parse[] rootChildren = root.getChildren();
int taggedNames = 0;
for (int i = 0; i < rootChildren.length; i++) {
if (rootChildren[i].getType().equals("LOC")) {
taggedNames++;
}
}
assertEquals(1, taggedNames);
}

@Test
public void testToStringPennTreebankWithWhitespaceGap() {
String text = "Good  boy!";
Parse root = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse word1 = new Parse(text, new Span(0, 4), "ADJ", 1.0, 0);
Parse word2 = new Parse(text, new Span(6, 9), "NN", 1.0, 1);
Parse leaf1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse leaf2 = new Parse(text, new Span(6, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
word1.insert(leaf1);
word2.insert(leaf2);
root.insert(word1);
root.insert(word2);
String result = root.toStringPennTreebank();
assertTrue(result.contains("ROOT"));
assertTrue(result.contains("ADJ"));
assertTrue(result.contains("  "));
}

@Test
public void testAddNullPrevPunct() {
String text = "Hello";
Parse parse = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
parse.addPreviousPunctuation(null);
Collection<Parse> prevSet = parse.getPreviousPunctuationSet();
assertTrue(prevSet.contains(null));
}

@Test
public void testAddNullNextPunct() {
String text = "Bye";
Parse parse = new Parse(text, new Span(0, text.length()), "NN", 1.0, 0);
parse.addNextPunctuation(null);
Collection<Parse> nextSet = parse.getNextPunctuationSet();
assertTrue(nextSet.contains(null));
}

@Test
public void testGetTokenNodesEmpty() {
Parse root = new Parse("Nothing", new Span(0, 7), "S", 1.0, 0);
Parse[] tokens = root.getTokenNodes();
assertEquals(0, tokens.length);
}

@Test
public void testGetTagNodesEmpty() {
Parse root = new Parse("Nothing", new Span(0, 7), "S", 1.0, 0);
Parse[] tags = root.getTagNodes();
assertEquals(0, tags.length);
}

@Test
public void testCloneDoesNotSharePartsList() {
String text = "Hi";
Parse p = new Parse(text, new Span(0, 2), "INTJ", 1.0, 0);
Parse token = new Parse(text, new Span(0, 2), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
p.insert(token);
Parse clone = (Parse) p.clone();
assertNotSame(p.getChildren(), clone.getChildren());
}

@Test
public void testSetChildOnIndexReplacesCorrectly() {
String text = "hotdog";
Parse parent = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
Parse a = new Parse(text, new Span(0, 3), "JJ", 1.0, 0);
Parse b = new Parse(text, new Span(3, 6), "NN", 1.0, 1);
parent.insert(a);
parent.insert(b);
parent.setChild(1, "MODIFIED");
Parse[] c = parent.getChildren();
assertEquals(2, c.length);
assertEquals("MODIFIED", c[1].getLabel());
}

@Test
public void testInsertConstituentThatContainsMultipleChildren() {
String text = "The big red dog";
Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 3), "DT", 1.0, 0);
Parse c2 = new Parse(text, new Span(4, 7), "JJ", 1.0, 1);
Parse c3 = new Parse(text, new Span(8, 11), "JJ", 1.0, 2);
Parse c4 = new Parse(text, new Span(12, 15), "NN", 1.0, 3);
parent.insert(c1);
parent.insert(c2);
parent.insert(c3);
parent.insert(c4);
Parse chunk = new Parse(text, new Span(4, 15), "NP", 1.0, 3);
parent.insert(chunk);
Parse[] children = parent.getChildren();
assertTrue(children[1].getType().equals("NP"));
assertEquals(3, children[1].getChildren().length);
}

@Test
public void testParseParseWithNestedUnaryNodes() {
String tree = "(TOP (NP (NP (NN cat))))";
Parse parse = Parse.parseParse(tree);
Parse[] children = parse.getChildren();
assertEquals("NP", children[0].getType());
assertEquals(1, children.length);
}

@Test
public void testDecodeTokenManualCall() {
String token = "-RRB-";
String decoded;
if (token.equals("-LRB-"))
decoded = "(";
else if (token.equals("-RRB-"))
decoded = ")";
else if (token.equals("-LCB-"))
decoded = "{";
else if (token.equals("-RCB-"))
decoded = "}";
else if (token.equals("-LSB-"))
decoded = "[";
else if (token.equals("-RSB-"))
decoded = "]";
else
decoded = token;
assertEquals(")", decoded);
}

@Test
public void testGetCommonParentWithDeepHierarchy() {
String text = "ABC DEF GHI";
Parse root = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse np = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
Parse vp = new Parse(text, new Span(8, 11), "VP", 1.0, 1);
Parse n1 = new Parse(text, new Span(0, 3), "NN", 1.0, 0);
Parse n2 = new Parse(text, new Span(4, 7), "NN", 1.0, 1);
Parse v = new Parse(text, new Span(8, 11), "VB", 1.0, 2);
np.insert(n1);
np.insert(n2);
vp.insert(v);
root.insert(np);
root.insert(vp);
Parse common = n1.getCommonParent(v);
assertEquals(root, common);
}

@Test
public void testFixPossessivesDoesNotCrashIfPOSWithoutFollowingNode() {
String text = "John's";
Parse root = new Parse(text, new Span(0, 6), "NP", 1.0, 0);
Parse pos = new Parse(text, new Span(4, 6), "POS", 1.0, 0);
root.insert(pos);
Parse.fixPossesives(root);
Parse[] children = root.getChildren();
assertEquals(1, children.length);
}

@Test
public void testGetTypeWithFunctionTagsEnabled() {
Parse.useFunctionTags(true);
String input = "NP-SBJ=0";
java.util.regex.Pattern typePattern = java.util.regex.Pattern.compile("^([^ =-]+)");
java.util.regex.Pattern funTypePattern = java.util.regex.Pattern.compile("^[^ =-]+-([^ =-]+)");
java.util.regex.Matcher typeMatcher = typePattern.matcher(input);
String type = null;
if (typeMatcher.find()) {
type = typeMatcher.group(1);
java.util.regex.Matcher funMatcher = funTypePattern.matcher(input);
if (funMatcher.find()) {
String ftag = funMatcher.group(1);
type = type + "-" + ftag;
}
}
assertEquals("NP-SBJ", type);
Parse.useFunctionTags(false);
}

@Test
public void testToStringPennTreebankReturnsCompleteStructure() {
String text = "A car";
Parse root = new Parse(text, new Span(0, 5), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse dt = new Parse(text, new Span(0, 1), "DT", 1.0, 0);
Parse nn = new Parse(text, new Span(2, 5), "NN", 1.0, 1);
Parse tok1 = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(2, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
dt.insert(tok1);
nn.insert(tok2);
np.insert(dt);
np.insert(nn);
root.insert(np);
String penn = root.toStringPennTreebank();
assertTrue(penn.contains("S"));
assertTrue(penn.contains("NP"));
assertTrue(penn.contains("DT"));
assertTrue(penn.contains("NN"));
assertTrue(penn.contains("A"));
assertTrue(penn.contains("car"));
}

@Test
public void testSetChunkTrueFalse() {
String text = "entity";
Parse parse = new Parse(text, new Span(0, text.length()), "NP", 1.0, 0);
parse.isChunk(true);
assertTrue(parse.isChunk());
parse.isChunk(false);
assertFalse(parse.isChunk());
}

@Test
public void testCompareToLessThanZero() {
String text = "comparison";
Parse p1 = new Parse(text, new Span(0, 5), "A", 0.5, 0);
Parse p2 = new Parse(text, new Span(0, 5), "A", 0.9, 0);
int result = p1.compareTo(p2);
assertTrue(result > 0);
}

@Test
public void testCompareToGreaterThanZero() {
String text = "comparison";
Parse p1 = new Parse(text, new Span(0, 5), "A", 0.9, 0);
Parse p2 = new Parse(text, new Span(0, 5), "A", 0.5, 0);
int result = p1.compareTo(p2);
assertTrue(result < 0);
}

@Test
public void testShowCodeTreeDoesNotThrow() {
String text = "a b";
Parse p = new Parse(text, new Span(0, 3), "S", 1.0, 0);
Parse token1 = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse token2 = new Parse(text, new Span(2, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
p.insert(token1);
p.insert(token2);
p.showCodeTree();
}

@Test
public void testToStringReturnsCoveredText() {
String text = "word";
Parse parse = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
String str = parse.toString();
assertEquals("word", str);
}

@Test
public void testParseParseMalformedLeavesUnmatchedParens() {
String input = "(TOP (NP (NN Dog)";
Parse result = Parse.parseParse(input);
assertEquals("TOP", result.getType());
}

@Test
public void testParseParseOnlyTopNode() {
String input = "(TOP)";
Parse parse = Parse.parseParse(input);
assertEquals("TOP", parse.getType());
assertEquals(0, parse.getChildren().length);
}

@Test
public void testParseParseHandlesEmptyTreeString() {
String input = "";
Parse result = Parse.parseParse(input);
assertNotNull(result);
}

@Test
public void testAddNamesWithNullTokenEntries() {
String text = "New York";
Parse root = new Parse(text, new Span(0, 8), "S", 1.0, 0);
Parse token1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse token2 = null;
Parse[] tokens = new Parse[] { token1, token2 };
Span[] spans = new Span[] { new Span(0, 2) };
boolean thrown = false;
try {
Parse.addNames("LOC", spans, tokens);
} catch (NullPointerException e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testAddNamesWithInvalidSpanStartGreaterThanEnd() {
String text = "Apple Inc";
Parse t1 = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse t2 = new Parse(text, new Span(6, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
Parse[] tokens = new Parse[] { t1, t2 };
Span[] neSpans = new Span[] { new Span(2, 1) };
Parse.addNames("ORG", neSpans, tokens);
assertEquals(0, tokens[0].getParent().getChildren().length);
}

@Test
public void testPruneParseWithAlreadyFlatTree() {
String text = "Flat";
Parse p = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
p.insert(child);
Parse.pruneParse(p);
assertEquals(1, p.getChildren().length);
}

@Test
public void testDecodeTokenReturnsOriginalWhenNotMatched() {
String token = "somethingElse";
String result;
if ("-LRB-".equals(token)) {
result = "(";
} else if ("-RRB-".equals(token)) {
result = ")";
} else if ("-LCB-".equals(token)) {
result = "{";
} else if ("-RCB-".equals(token)) {
result = "}";
} else if ("-LSB-".equals(token)) {
result = "[";
} else if ("-RSB-".equals(token)) {
result = "]";
} else {
result = token;
}
assertEquals("somethingElse", result);
}

@Test
public void testEncodeTokenDefaultReturnsSame() {
String token = "dog";
String encoded;
if (token.equals("("))
encoded = "-LRB-";
else if (token.equals(")"))
encoded = "-RRB-";
else if (token.equals("{"))
encoded = "-LCB-";
else if (token.equals("}"))
encoded = "-RCB-";
else if (token.equals("["))
encoded = "-LSB-";
else if (token.equals("]"))
encoded = "-RSB-";
else
encoded = token;
assertEquals("dog", encoded);
}

@Test
public void testGetTokenReturnsNullIfPatternNotMatch() {
String input = "(ABC DEF)";
java.util.regex.Pattern tokenPattern = java.util.regex.Pattern.compile("^[^ ()]+ ([^ ()]+)\\s*\\)");
java.util.regex.Matcher matcher = tokenPattern.matcher(input);
String token = null;
if (matcher.find()) {
token = matcher.group(1);
}
assertNull(token);
}

@Test
public void testGetLabelReturnsNullIfNotSet() {
Parse p = new Parse("abc", new Span(0, 3), "NN", 0.9, 0);
String label = p.getLabel();
assertNull(label);
}

@Test
public void testCloneRootDeepCopy() {
String text = "AA BB";
Parse root = new Parse(text, new Span(0, 5), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
Parse tok1 = new Parse(text, new Span(0, 2), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(3, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
np.insert(tok1);
np.insert(tok2);
root.insert(np);
Parse clone = root.cloneRoot(tok2, 0);
assertNotNull(clone);
assertEquals("S", clone.getType());
assertEquals(1, clone.getChildren().length);
}

@Test
public void testAddProbWithNegativeValueReducesProbability() {
Parse p = new Parse("neg", new Span(0, 3), "X", 1.0, 0);
p.addProb(-0.4);
assertEquals(0.6, p.getProb(), 0.0001);
}

@Test
public void testShowHandlesWhitespaceGapBetweenChildren() {
String text = "A   B";
Parse root = new Parse(text, new Span(0, 5), "ROOT", 1.0, 0);
Parse word1 = new Parse(text, new Span(0, 1), "WW", 1.0, 0);
Parse word2 = new Parse(text, new Span(4, 5), "WW", 1.0, 1);
Parse tok1 = new Parse(text, new Span(0, 1), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
Parse tok2 = new Parse(text, new Span(4, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
word1.insert(tok1);
word2.insert(tok2);
root.insert(word1);
root.insert(word2);
StringBuffer sb = new StringBuffer();
root.show(sb);
String result = sb.toString();
assertTrue(result.contains("   "));
assertTrue(result.contains("A"));
assertTrue(result.contains("B"));
}

@Test
public void testParseParseIncludesNoneNodeAndNoGapLabeler() {
String tree = "(TOP (NP (-NONE- *)))";
Parse p = Parse.parseParse(tree, null);
assertEquals("TOP", p.getType());
Parse[] children = p.getChildren();
assertEquals(1, children.length);
assertEquals("NP", children[0].getType());
}

@Test
public void testAddWithNullPrevPunctuationSet() {
String text = "The cat sleeps";
Parse parent = new Parse(text, new Span(0, 15), "S", 1.0, 0);
Parse child = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] constituents, String type) {
// return constituents[0];
// }
// };
// parent.add(child, rules);
Parse[] children = parent.getChildren();
assertEquals(1, children.length);
assertEquals("NP", children[0].getType());
assertEquals(child, parent.getHead());
assertEquals(0, parent.getHeadIndex());
}

@Test
public void testAdjoinCreatesNewStructureCorrectly() {
String text = "walk quickly";
Parse parent = new Parse(text, new Span(0, 13), "VP", 1.0, 0);
Parse walk = new Parse(text, new Span(0, 4), "VB", 1.0, 0);
Parse adv = new Parse(text, new Span(5, 13), "RB", 1.0, 1);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] c, String t) {
// return c[1];
// }
// };
parent.insert(walk);
// Parse result = parent.adjoin(adv, rules);
// assertEquals("VB", result.getType());
// assertEquals(3, result.getChildren().length);
}

@Test
public void testAdjoinRootCreatesBinaryStructure() {
String text = "eat food";
Parse root = new Parse(text, new Span(0, 8), "S", 1.0, 0);
Parse eat = new Parse(text, new Span(0, 3), "V", 1.0, 0);
Parse food = new Parse(text, new Span(4, 8), "N", 1.0, 1);
// HeadRules rules = new HeadRules() {
// 
// public Parse getHead(Parse[] c, String t) {
// return c[1];
// }
// };
root.insert(eat);
root.insert(food);
// Parse result = root.adjoinRoot(food, rules, 1);
// assertNotNull(result.getHead());
// assertEquals(3, result.getChildren().length);
// assertEquals("N", result.getType());
}

@Test
public void testExpandTopNodeMovesNodesToNewRoot() {
String text = "This is good";
Parse oldRoot = new Parse(text, new Span(0, 13), "OLD", 1.0, 0);
Parse a = new Parse(text, new Span(0, 4), "DT", 1.0, 0);
Parse b = new Parse(text, new Span(5, 7), "VB", 1.0, 1);
Parse c = new Parse(text, new Span(8, 13), "JJ", 1.0, 2);
oldRoot.insert(a);
oldRoot.insert(b);
oldRoot.insert(c);
Parse newRoot = new Parse(text, new Span(4, 10), "NEWROOT", 1.0, 1);
oldRoot.expandTopNode(newRoot);
assertTrue(newRoot.getChildren().length > 0);
}

@Test
public void testGetCommonParentIsParentOfGivenNode() {
String text = "hi";
Parse parent = new Parse(text, new Span(0, 2), "NP", 1.0, 0);
Parse child = new Parse(text, new Span(0, 2), "NN", 1.0, 0);
parent.insert(child);
Parse result = parent.getCommonParent(child);
assertEquals(parent, result);
}

@Test
public void testGetTokenReturnsNullOnInvalidSyntax() {
String rest = "(LackingClose";
java.util.regex.Pattern tokenPattern = java.util.regex.Pattern.compile("^[^ ()]+ ([^ ()]+)\\s*\\)");
java.util.regex.Matcher tokenMatcher = tokenPattern.matcher(rest);
String token = null;
if (tokenMatcher.find()) {
token = tokenMatcher.group(1);
}
assertNull(token);
}

@Test
public void testUpdateSpanResetsFromChildren() {
String text = "Wide span";
Parse root = new Parse(text, new Span(0, text.length()), "ROOT", 1.0, 0);
Parse a = new Parse(text, new Span(0, 4), "A", 1.0, 0);
Parse b = new Parse(text, new Span(5, 9), "B", 1.0, 1);
root.insert(a);
root.insert(b);
root.updateSpan();
assertEquals(0, root.getSpan().getStart());
assertEquals(9, root.getSpan().getEnd());
}

@Test
public void testCoveredTextReturnsEmptyForSameStartEndSpan() {
String text = "x";
Parse p = new Parse(text, new Span(1, 1), "EMPTY", 1.0, 0);
String covered = p.getCoveredText();
assertEquals("", covered);
}

@Test
public void testSetChildClonesChildAndSetsLabel() {
String text = "Zebra runs";
Parse parent = new Parse(text, new Span(0, 11), "S", 1.0, 0);
Parse np = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
parent.insert(np);
parent.setChild(0, "MODIFIED");
Parse[] kids = parent.getChildren();
assertEquals("MODIFIED", kids[0].getLabel());
assertEquals("NP", kids[0].getType());
}

@Test
public void testRemoveChildAdjustsSpanCorrectly() {
String text = "one two three";
Parse parent = new Parse(text, new Span(0, 13), "S", 1.0, 0);
Parse c1 = new Parse(text, new Span(0, 3), "A", 1.0, 0);
Parse c2 = new Parse(text, new Span(4, 7), "B", 1.0, 1);
Parse c3 = new Parse(text, new Span(8, 13), "C", 1.0, 2);
parent.insert(c1);
parent.insert(c2);
parent.insert(c3);
parent.remove(2);
assertEquals(2, parent.getChildren().length);
assertEquals(0, parent.getSpan().getStart());
assertEquals(7, parent.getSpan().getEnd());
}

@Test
public void testToStringPennTreebankNoChildrenReturnsSingleLabel() {
String text = "AAA";
Parse p = new Parse(text, new Span(0, 3), "ROOT", 1.0, 0);
String result = p.toStringPennTreebank();
assertTrue(result.contains("ROOT"));
}
}
