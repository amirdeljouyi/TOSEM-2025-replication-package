package edu.illinois.cs.cogcomp.comma;

import edu.illinois.cs.cogcomp.comma.bayraktar.BayraktarPatternLabeler;
import edu.illinois.cs.cogcomp.comma.datastructures.Comma;
import edu.illinois.cs.cogcomp.comma.datastructures.CommaProperties;
import edu.illinois.cs.cogcomp.comma.datastructures.CommaSRLSentence;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.stanford.nlp.util.IntPair;
import junit.framework.TestCase;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class Comma_3_GPTLLMTest {

@Test
public void testGetCommaID() {
String[] tokens = { "The", "quick", "brown", ",", "fox" };
// TextAnnotation goldTa = new TextAnnotation("testCorpus", "goldId", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation autoTa = new TextAnnotation("testCorpus", "goldId", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence srlSentence = new CommaSRLSentence(goldTa, autoTa);
Comma.useGoldFeatures(true);
List<String> labels = Arrays.asList("G1");
// Comma comma = new Comma(3, srlSentence, labels);
// String result = comma.getCommaID();
// assertEquals("3 goldId", result);
}

@Test
public void testGetLabel() {
String[] tokens = { "One", "two", ",", "three" };
// TextAnnotation goldTa = new TextAnnotation("test", "doc1", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation autoTa = new TextAnnotation("test", "doc1", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sentence = new CommaSRLSentence(goldTa, autoTa);
Comma.useGoldFeatures(true);
// Comma comma = new Comma(2, sentence, Arrays.asList("ENUM"));
// String result = comma.getLabel();
// assertEquals("ENUM", result);
}

@Test
public void testGetTextAnnotationGold() {
String[] tokens = { "Hello", ",", "world" };
// TextAnnotation goldTA = new TextAnnotation("test", "uuid1", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation autoTA = new TextAnnotation("test", "uuid1", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sent = new CommaSRLSentence(goldTA, autoTA);
Comma.useGoldFeatures(true);
// Comma comma = new Comma(1, sent, Arrays.asList("APPOS"));
// TextAnnotation resultTA = comma.getTextAnnotation(true);
// assertEquals(goldTA, resultTA);
}

@Test
public void testGetTextAnnotationAuto() {
String[] tokens = { "Good", "morning", "," };
// TextAnnotation goldTA = new TextAnnotation("test", "uuid2", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation autoTA = new TextAnnotation("test", "uuid2", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sentence = new CommaSRLSentence(goldTA, autoTA);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("OTHER"));
// TextAnnotation result = comma.getTextAnnotation(false);
// assertEquals(autoTA, result);
}

@Test
public void testGetWordToRightWithinBounds() {
String[] tokens = { "This", "is", ",", "text" };
// TextAnnotation goldTa = new TextAnnotation("test", "doc1", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation autoTa = new TextAnnotation("test", "doc1", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence s = new CommaSRLSentence(goldTa, autoTa);
// Comma comma = new Comma(2, s, Arrays.asList("ENUM"));
// String word = comma.getWordToRight(1);
// assertEquals("text", word);
}

@Test
public void testGetWordToRightOutOfBounds() {
String[] tokens = { "start", ",", "end" };
// TextAnnotation goldTa = new TextAnnotation("test", "doc2", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation autoTa = new TextAnnotation("test", "doc2", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sentence = new CommaSRLSentence(goldTa, autoTa);
// Comma comma = new Comma(2, sentence, Arrays.asList("CL"));
// String result = comma.getWordToRight(1);
// assertEquals("###", result);
}

@Test
public void testGetWordToLeftWithinBounds() {
String[] tokens = { "A", "B", "," };
// TextAnnotation gold = new TextAnnotation("c", "d", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation auto = new TextAnnotation("c", "d", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence srl = new CommaSRLSentence(gold, auto);
// Comma comma = new Comma(2, srl, Arrays.asList("APPOS"));
// String val = comma.getWordToLeft(1);
// assertEquals("B", val);
}

@Test
public void testGetWordToLeftOutOfBounds() {
String[] tokens = { ",", "begin" };
// TextAnnotation gold = new TextAnnotation("x", "y", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation auto = new TextAnnotation("x", "y", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sentence = new CommaSRLSentence(gold, auto);
// Comma comma = new Comma(0, sentence, Arrays.asList("OTHER"));
// String result = comma.getWordToLeft(1);
// assertEquals("$$$", result);
}

@Test
public void testAnnotatedTextIncludesLabels() {
String[] tokens = { "It", "is", ",", "done" };
// TextAnnotation autoTA = new TextAnnotation("corpus", "sid", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation goldTA = new TextAnnotation("corpus", "sid", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sentence = new CommaSRLSentence(goldTA, autoTA);
// Comma comma = new Comma(2, sentence, Arrays.asList("APPOS", "CL"));
// String output = comma.getAnnotatedText();
// assertTrue(output.contains("[APPOS,CL]"));
}

@Test
public void testGetLabelWithMultipleValues() {
String[] tokens = { "X", ",", "Y" };
// TextAnnotation gold = new TextAnnotation("m", "n", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation auto = new TextAnnotation("m", "n", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence s = new CommaSRLSentence(gold, auto);
// Comma comma = new Comma(1, s, Arrays.asList("CL", "ENUM", "APPOS"));
// String label = comma.getLabel();
// assertEquals("CL", label);
}

@Test
public void testGetStrippedNotationWithHyphenatedLabel() {
String[] tokens = { "A", "," };
// TextAnnotation ta = new TextAnnotation("test", "doc", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "source", ta, 1.0);
// ta.addView(ViewNames.NER_CONLL, nerView);
// Constituent c = new Constituent("NP-ADVP", ViewNames.SHALLOW_PARSE, ta, 0, 1);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("OTHER"));
// String result = comma.getStrippedNotation(c);
// assertTrue(result.startsWith("NP"));
}

@Test
public void testGetChunkToRightOfCommaIndexZero() {
String[] tokens = { "a", ",", "b", "c" };
// TextAnnotation ta = new TextAnnotation("ta", "001", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "test", ta, 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunkView);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("G"));
// Constituent result = comma.getChunkToRightOfComma(0);
// assertNull(result);
}

@Test
public void testGetSiblingToRightReturnsNullWhenNoSibling() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("test", "id", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0);
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// Constituent c = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 1);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("X"));
// Constituent result = comma.getSiblingToRight(1, c, tree);
// assertNull(result);
}

@Test
public void testGetSiblingToLeftReturnsNullWhenNoSibling() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("test", "id", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0);
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// Constituent c = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 1);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("X"));
// Constituent result = comma.getSiblingToLeft(1, c, tree);
// assertNull(result);
}

@Test
public void testGetBayraktarPatternReturnsDefaultWhenParentIsPunctuation() {
String[] tokens = { "a", ",", "b" };
// TextAnnotation ta = new TextAnnotation("t", "x", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0);
// Constituent commaToken = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent parent = new Constituent(".", ViewNames.PARSE_STANFORD, ta, 1, 2);
// tree.addConstituent(commaToken);
// tree.addConstituent(parent);
// tree.addConstituentRelation(new Relation("child", parent, commaToken, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// ta.addView(ViewNames.PARSE_GOLD, tree);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence, Arrays.asList("Other"));
// String pattern = comma.getBayraktarPattern(tree);
// assertTrue(pattern.startsWith(". -->"));
}

@Test
public void testGetNamedEntityTagReturnsEmptyWhenViewMissing() {
String[] tokens = { "X", ",", "Y" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// Constituent chunk = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 1);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("ENUM"));
// String result = comma.getNamedEntityTag(chunk);
// assertEquals("", result);
}

@Test
public void testGetBayraktarLabelReturnsDefaultWhenNull() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("xx", "yy", "", new String[][] { tokens }, new int[][] {});
// ta.addView(ViewNames.PARSE_STANFORD, new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0));
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// String label = comma.getBayraktarLabel();
// assertNotNull(label);
// assertEquals("Other", label);
}

@Test
public void testGetSiblingCommasWhenThereAreNoSiblings() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("c", "d", "", new String[][] { tokens }, new int[][] {});
// TreeView treeView = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0);
// ta.addView(ViewNames.PARSE_STANFORD, treeView);
// ta.addView(ViewNames.PARSE_GOLD, treeView);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("ENUM"));
// s.setCommas(Collections.singletonList(comma));
// List<Comma> siblings = comma.getSiblingCommas();
// assertEquals(1, siblings.size());
// assertEquals(comma, siblings.get(0));
}

@Test
public void testGetChunkNgramsHandlesNullChunks() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("x", "y", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView spanLabelView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "test", ta, 1.0f);
// ta.addView(ViewNames.SHALLOW_PARSE, spanLabelView);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("ENUM"));
// String[] ngrams = comma.getChunkNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetParentSiblingPhraseNgramsHandlesNullPhrases() {
String[] tokens = { "W", ",", "X", "Y", "Z" };
// TextAnnotation ta = new TextAnnotation("p", "q", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.PARSE_STANFORD, "source", ta, 1.0f);
// ta.addView(ViewNames.PARSE_STANFORD, view);
// ta.addView(ViewNames.PARSE_GOLD, view);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("APPOS"));
Comma.useGoldFeatures(false);
// String[] features = comma.getParentSiblingPhraseNgrams();
// assertNotNull(features);
// assertTrue(features.length > 0);
}

@Test
public void testGetLabelWithSingleEmptyStringLabel() {
String[] tokens = { "This", ",", "test" };
// TextAnnotation ta = new TextAnnotation("id", "sid", "", new String[][] { tokens }, new int[][] {});
// TextAnnotation auto = new TextAnnotation("id", "sid", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sentence = new CommaSRLSentence(ta, auto);
// Comma comma = new Comma(1, sentence, Collections.singletonList(""));
// String label = comma.getLabel();
// assertEquals("", label);
}

@Test
public void testGetBayraktarPatternReturnsFallbackForNoChildren() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0f);
// Constituent commaCon = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent parent = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 1, 2);
// view.addConstituent(commaCon);
// view.addConstituent(parent);
// view.addConstituentRelation(new Relation("child", parent, commaCon, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, view);
// ta.addView(ViewNames.PARSE_GOLD, view);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence, Arrays.asList("CL"));
// String pattern = comma.getBayraktarPattern();
// assertTrue(pattern != null && pattern.startsWith("NP -->"));
}

@Test
public void testGetSiblingCommaHeadWhenMultipleSiblingsPresent() {
String[] tokens = { "One", ",", "Two", ",", "Three" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "parser", ta, 1.0);
// Constituent c1 = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent c2 = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 3, 4);
// Constituent parent = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 5);
// tree.addConstituent(c1);
// tree.addConstituent(c2);
// tree.addConstituent(parent);
// tree.addConstituentRelation(new Relation("child", parent, c1, 1.0));
// tree.addConstituentRelation(new Relation("child", parent, c2, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// ta.addView(ViewNames.PARSE_GOLD, tree);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma use1 = new Comma(1, sentence, Arrays.asList("ENUM"));
// Comma use2 = new Comma(3, sentence, Arrays.asList("ENUM"));
// sentence.setCommas(Arrays.asList(use1, use2));
// Comma head = use2.getSiblingCommaHead();
// assertEquals(1, head.getPosition());
}

@Test
public void testGetLeftToRightDependenciesWhenNoOutgoingCrossingRelationsExist() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView depView = new TreeView(ViewNames.DEPENDENCY_STANFORD, "source", ta, 1.0);
// Constituent token1 = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 0, 1);
// Constituent token2 = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 1, 2);
// Constituent token3 = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 2, 3);
// depView.addConstituent(token1);
// depView.addConstituent(token2);
// depView.addConstituent(token3);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, depView);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// String[] result = comma.getLeftToRightDependencies();
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testGetRightToLeftDependenciesWhenNoIncomingCrossingRelationsExist() {
String[] tokens = { "X", ",", "Y" };
// TextAnnotation ta = new TextAnnotation("source", "id", "", new String[][] { tokens }, new int[][] {});
// TreeView depView = new TreeView(ViewNames.DEPENDENCY_STANFORD, "src", ta, 1.0);
// Constituent left = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 0, 1);
// Constituent mid = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 1, 2);
// Constituent right = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 2, 3);
// depView.addConstituent(left);
// depView.addConstituent(mid);
// depView.addConstituent(right);
// Relation rel = new Relation("mod", left, mid, 1.0);
// mid.addIncomingRelation(rel);
// depView.addRelation(rel);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, depView);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("OTHER"));
// String[] rtol = comma.getRightToLeftDependencies();
// assertNotNull(rtol);
// assertEquals(0, rtol.length);
}

@Test
public void testGetBayraktarAnnotatedTextNotNullWhenLabelIsNull() {
String[] tokens = { "Start", ",", "Something" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.PARSE_STANFORD, "source", ta, 1.0f);
// Constituent node = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent parent = new Constituent("NN", ViewNames.PARSE_STANFORD, ta, 1, 2);
// view.addConstituent(node);
// view.addConstituent(parent);
// view.addConstituentRelation(new Relation("child", parent, node, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, view);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("ENUM"));
// String result = comma.getBayraktarAnnotatedText();
// assertNotNull(result);
// assertTrue(result.contains("["));
}

@Test
public void testGetNotationWithNERAndPOSDisabled() {
String[] tokens = { "Named", "Entity", "," };
// TextAnnotation ta = new TextAnnotation("corpusId", "id42", "", new String[][] { tokens }, new int[][] {});
// Constituent cons = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("G"));
// CommaProperties.getInstance().disableLexicalFeatures();
// String notation = comma.getNotation(cons);
// assertNotNull(notation);
// assertEquals("NP", notation);
}

@Test
public void testGetAnnotationTextWhenNoLabels() {
String[] tokens = { "start", ",", "end" };
// TextAnnotation ta = new TextAnnotation("c", "d", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence srl = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, srl, Collections.singletonList(""));
// String annotated = comma.getAnnotatedText();
// assertTrue(annotated.contains("[]") || annotated.contains("[] "));
}

@Test
public void testGetNamedEntityTagOutputWhenPartialCoverage() {
String[] tokens = { "Barack", ",", "Obama" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "source", ta, 1.0);
// Constituent ner = new Constituent("PERSON", ViewNames.NER_CONLL, ta, 0, 1);
// nerView.addConstituent(ner);
// ta.addView(ViewNames.NER_CONLL, nerView);
// Constituent broader = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// String result = comma.getNamedEntityTag(broader);
// assertTrue(result.contains("+PERSON") || result.equals(""));
}

@Test
public void testGetSiblingToLeftWithZeroDistanceReturnsSame() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("test", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView treeView = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0f);
// Constituent c = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 1);
// treeView.addConstituent(c);
// ta.addView(ViewNames.PARSE_STANFORD, treeView);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("G"));
// Constituent result = comma.getSiblingToLeft(0, c, treeView);
// assertEquals(c, result);
}

@Test
public void testGetSiblingToRightWithZeroDistanceReturnsSame() {
String[] tokens = { "Hello", ",", "World" };
// TextAnnotation ta = new TextAnnotation("a", "b", "", new String[][] { tokens }, new int[][] {});
// TreeView treeView = new TreeView(ViewNames.PARSE_STANFORD, "in", ta, 1f);
// Constituent c = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 1);
// treeView.addConstituent(c);
// ta.addView(ViewNames.PARSE_STANFORD, treeView);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// Constituent result = comma.getSiblingToRight(0, c, treeView);
// assertEquals(c, result);
}

@Test
public void testGetNotationReturnsOnlyLabelWhenNoNERAndNoPOS() {
String[] tokens = { "entity", "," };
// TextAnnotation ta = new TextAnnotation("corpus", "sid", "", new String[][] { tokens }, new int[][] {});
// Constituent c = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 1);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// CommaProperties.getInstance().setLexicaliseNER(false);
// CommaProperties.getInstance().setLexicalisePOS(false);
// Comma comma = new Comma(1, sentence, Arrays.asList("ENUM"));
// String result = comma.getNotation(c);
// assertEquals("NP", result);
}

@Test
public void testGetContainingSRLsWhenAllViewsAreEmpty() {
String[] tokens = { "Run", ",", "away" };
// TextAnnotation ta = new TextAnnotation("x", "y", "", new String[][] { tokens }, new int[][] {});
// PredicateArgumentView verbSRL = new PredicateArgumentView(ViewNames.SRL_VERB, "test", ta, 1.0f);
// PredicateArgumentView nomSRL = new PredicateArgumentView(ViewNames.SRL_NOM, "test", ta, 1.0f);
// PredicateArgumentView prepSRL = new PredicateArgumentView(ViewNames.SRL_PREP, "test", ta, 1.0f);
// ta.addView(ViewNames.SRL_VERB, verbSRL);
// ta.addView(ViewNames.SRL_NOM, nomSRL);
// ta.addView(ViewNames.SRL_PREP, prepSRL);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("CL"));
// List<String> result = comma.getContainingSRLs();
// assertNotNull(result);
// assertTrue(result.isEmpty());
}

@Test
public void testGetPhraseToLeftOfParentWithoutProperParent() {
String[] tokens = { "It", ",", "happened" };
// TextAnnotation ta = new TextAnnotation("set", "1", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "source", ta, 1.0f);
// Constituent token = new Constituent("NN", ViewNames.PARSE_STANFORD, ta, 1, 2);
// tree.addConstituent(token);
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// ta.addView(ViewNames.PARSE_GOLD, tree);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
Comma.useGoldFeatures(false);
// Constituent leftOfParent = comma.getPhraseToLeftOfParent(1);
// assertNull(leftOfParent);
}

@Test
public void testGetPhraseToRightOfParentWhenParentHasNoSiblings() {
String[] tokens = { "It", ",", "rained" };
// TextAnnotation ta = new TextAnnotation("set", "2", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.PARSE_STANFORD, "parser", ta, 1.0f);
// Constituent token = new Constituent("NN", ViewNames.PARSE_STANFORD, ta, 1, 2);
// view.addConstituent(token);
// ta.addView(ViewNames.PARSE_STANFORD, view);
// ta.addView(ViewNames.PARSE_GOLD, view);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// Constituent sibling = comma.getPhraseToRightOfParent(1);
// assertNull(sibling);
}

@Test
public void testGetNotationWithOutgoingRelationAndPOSLexicalization() {
String[] tokens = { "massive", "data" };
// TextAnnotation ta = new TextAnnotation("t", "id", "", new String[][] { tokens }, new int[][] {});
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "JJ", 1.0);
// posView.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent head = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 2);
// Constituent child = new Constituent("NN", ViewNames.PARSE_STANFORD, ta, 1, 2);
// head.addRelation(new Relation("child", head, child, 1.0));
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// CommaProperties.getInstance().setLexicaliseNER(false);
// CommaProperties.getInstance().setLexicalisePOS(true);
// Comma comma = new Comma(1, sentence, Arrays.asList("ENUM"));
// String result = comma.getNotation(head);
// assertTrue(result.contains("NP") && result.contains("JJ") && result.contains("NN"));
}

@Test
public void testGetNamedEntityTagReturnsEmptyWhenNEIsShorterThanThreshold() {
String[] tokens = { "OpenAI", ",", "is" };
// TextAnnotation ta = new TextAnnotation("z", "abc", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "src", ta, 1.0);
// Constituent ne = new Constituent("ORG", ViewNames.NER_CONLL, ta, 0, 1);
// nerView.addConstituent(ne);
// ta.addView(ViewNames.NER_CONLL, nerView);
// Constituent span = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Collections.singletonList("APPOS"));
// String nerTag = comma.getNamedEntityTag(span);
// assertTrue(nerTag.contains("+ORG") || nerTag.isEmpty());
}

@Test
public void testGetPOSNgramsWindowWithMissingPOSView() {
String[] tokens = { "this", ",", "test", "passes" };
// TextAnnotation ta = new TextAnnotation("w", "r", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// CommaProperties.getInstance().setLexicalisePOS(true);
// Comma comma = new Comma(1, s, Arrays.asList("CL"));
// String[] ngrams = comma.getPOSNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length >= 0);
}

@Test
public void testGetChunkToLeftOfCommaReturnsNullOnInvalidDistance() {
String[] tokens = { "A", ",", "B", "C" };
// TextAnnotation ta = new TextAnnotation("w", "r", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "manual", ta, 1.0f);
// chunkView.addSpanLabel(0, 1, "NP", 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunkView);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// Constituent result = comma.getChunkToLeftOfComma(3);
// assertNull(result);
}

@Test
public void testGetChunkToRightOfCommaWhenDistanceGreaterThanSizeReturnsNull() {
String[] tokens = { "a", ",", "b", "c" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "annotator", ta, 1.0);
// chunkView.addSpanLabel(2, 3, "VP", 1.0);
// chunkView.addSpanLabel(3, 4, "NP", 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunkView);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// Constituent result = comma.getChunkToRightOfComma(3);
// assertNull(result);
}

@Test
public void testGetBayraktarPatternUsesCCAsSurfaceForm() {
String[] tokens = { "A", "but", "B" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0f);
// Constituent comma = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent parent = new Constituent("CC", ViewNames.PARSE_STANFORD, ta, 1, 2);
// tree.addConstituent(comma);
// tree.addConstituent(parent);
// tree.addConstituentRelation(new Relation("child", parent, comma, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// ta.addView(ViewNames.PARSE_GOLD, tree);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma c = new Comma(1, sentence, Arrays.asList("CL"));
Comma.useGoldFeatures(false);
// String pattern = c.getBayraktarPattern(tree);
// assertTrue(pattern.contains("but"));
}

@Test
public void testGetNamedEntityTagSkipsMISCEntity() {
String[] tokens = { "John", ",", "Smith" };
// TextAnnotation ta = new TextAnnotation("test", "doc", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "source", ta, 1.0);
// Constituent ne = new Constituent("MISC", ViewNames.NER_CONLL, ta, 0, 2);
// nerView.addConstituent(ne);
// ta.addView(ViewNames.NER_CONLL, nerView);
// Constituent c = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("OTHER"));
// String result = comma.getNamedEntityTag(c);
// assertEquals("", result);
}

@Test
public void testGetStrippedNotationWithNERAndPOSLexicalization() {
String[] tokens = { "company", "stock" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "test", ta, 1.0);
// nerView.addSpanLabel(0, 2, "ORG", 1.0);
// ta.addView(ViewNames.NER_CONLL, nerView);
// Constituent c = new Constituent("NP-BLAH", ViewNames.SHALLOW_PARSE, ta, 0, 2);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// CommaProperties.getInstance().setLexicaliseNER(true);
// CommaProperties.getInstance().setLexicalisePOS(true);
// Comma comma = new Comma(1, sentence, Arrays.asList("ENUM"));
// String result = comma.getStrippedNotation(c);
// assertTrue(result.contains("NP"));
// assertTrue(result.contains("+ORG"));
// assertTrue(result.contains("NN"));
}

@Test
public void testGetCommaConstituentFromTreeWhenNoMatchReturnsNull() {
String[] tokens = { "first", ",", "second" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0f);
// Constituent unrelated = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 1);
// tree.addConstituent(unrelated);
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("APPOS"));
// Constituent result = comma.getCommaConstituentFromTree(tree);
// assertNull(result);
}

@Test
public void testGetSiblingCommasReturnsOnlyThoseWithSameParent() {
String[] tokens = { "X", ",", "Y", ",", "Z" };
// TextAnnotation ta = new TextAnnotation("t", "d", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.PARSE_STANFORD, "v", ta, 1.0f);
// Constituent comma1 = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent comma2 = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 3, 4);
// Constituent parent = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 5);
// view.addConstituent(parent);
// view.addConstituent(comma1);
// view.addConstituent(comma2);
// view.addConstituentRelation(new Relation("child", parent, comma1, 1.0));
// view.addConstituentRelation(new Relation("child", parent, comma2, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, view);
// ta.addView(ViewNames.PARSE_GOLD, view);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma c1 = new Comma(1, sentence, Arrays.asList("APPOS"));
// Comma c2 = new Comma(3, sentence, Arrays.asList("CL"));
// sentence.setCommas(Arrays.asList(c1, c2));
// List<Comma> siblings = c1.getSiblingCommas();
// assertEquals(2, siblings.size());
}

@Test
public void testIsSiblingReturnsFalseForDifferentParents() {
String[] tokens = { "before", ",", "middle", ",", "after" };
// TextAnnotation ta = new TextAnnotation("id", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.PARSE_STANFORD, "src", ta, 1.0f);
// Constituent par1 = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 3);
// Constituent par2 = new Constituent("VP", ViewNames.PARSE_STANFORD, ta, 3, 5);
// Constituent comma1 = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent comma2 = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 3, 4);
// view.addConstituent(par1);
// view.addConstituent(par2);
// view.addConstituent(comma1);
// view.addConstituent(comma2);
// view.addConstituentRelation(new Relation("child", par1, comma1, 1.0));
// view.addConstituentRelation(new Relation("child", par2, comma2, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, view);
// ta.addView(ViewNames.PARSE_GOLD, view);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma c1 = new Comma(1, sentence, Arrays.asList("APPOS"));
// Comma c2 = new Comma(3, sentence, Arrays.asList("CL"));
// boolean result = c1.isSibling(c2);
// assertFalse(result);
}

@Test
public void testGetPOSNgramsWithMissingTokens() {
String[] tokens = { "Only", "one", "," };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "DT", 1.0);
// posView.addTokenLabel(1, "NN", 1.0);
// posView.addTokenLabel(2, ",", 1.0);
// ta.addView(ViewNames.POS, posView);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("OTHER"));
// String[] ngrams = comma.getPOSNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetChunkNgramsWithAllNullChunks() {
String[] tokens = { "a", ",", "b" };
// TextAnnotation ta = new TextAnnotation("t", "d", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "src", ta, 1.0f);
// chunkView.addSpanLabel(0, 1, "NP", 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunkView);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma c = new Comma(1, s, Arrays.asList("APPOS"));
// String[] ngrams = c.getChunkNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetContainingSRLsIgnoresNonOverlappingArguments() {
String[] tokens = { "He", "said", ",", "go" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// PredicateArgumentView view = new PredicateArgumentView(ViewNames.SRL_VERB, "test", ta, 1.0f);
// Constituent predicate = new Constituent("predicate", ViewNames.SRL_VERB, ta, 0, 1);
// Constituent arg = new Constituent("ARG1", ViewNames.SRL_VERB, ta, 1, 2);
// view.addPredicate(predicate);
// view.addArgument(predicate, arg);
// ta.addView(ViewNames.SRL_VERB, view);
// ta.addView(ViewNames.SRL_NOM, new PredicateArgumentView(ViewNames.SRL_NOM, "src", ta, 1.0f));
// ta.addView(ViewNames.SRL_PREP, new PredicateArgumentView(ViewNames.SRL_PREP, "src", ta, 1.0f));
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("CL"));
// assertTrue(comma.getContainingSRLs().isEmpty());
}

@Test
public void testGetBayraktarPatternWhenParentIsNull() {
String[] tokens = { "yes", ",", "no" };
// TextAnnotation ta = new TextAnnotation("x", "y", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.PARSE_STANFORD, "parser", ta, 1.0f);
// Constituent commaToken = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// view.addConstituent(commaToken);
// ta.addView(ViewNames.PARSE_STANFORD, view);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("ENUM"));
// String result = comma.getBayraktarPattern(view);
// assertTrue(result.contains("null -->"));
}

@Test
public void testGetWordNgramsProducesTokensAroundBoundaries() {
String[] tokens = { "alpha", ",", "beta" };
// TextAnnotation ta = new TextAnnotation("set", "doc", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("CL"));
// String[] ngrams = comma.getWordNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
// for (String s1 : ngrams) {
// assertNotNull(s1);
// }
}

@Test
public void testGetAnnotatedTextAtStartOfSentence() {
String[] tokens = { ",", "early", "comma" };
// TextAnnotation ta = new TextAnnotation("a", "b", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(0, s, Collections.singletonList("APPOS"));
// String result = comma.getAnnotatedText();
// assertTrue(result.startsWith("[APPOS]") || result.contains("[APPOS]"));
}

@Test
public void testGetStrippedNotationWithOnlyLabelAndDisabledFeatures() {
String[] tokens = { "entity1", "entity2" };
// TextAnnotation ta = new TextAnnotation("c", "d", "", new String[][] { tokens }, new int[][] {});
// Constituent con = new Constituent("L1-L2", ViewNames.SHALLOW_PARSE, ta, 0, 2);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// CommaProperties.getInstance().setLexicaliseNER(false);
// CommaProperties.getInstance().setLexicalisePOS(false);
// Comma comma = new Comma(1, s, Arrays.asList("ENUM"));
// String result = comma.getStrippedNotation(con);
// assertEquals("L1", result);
}

@Test
public void testGetLeftToRightDependenciesCrossingCommaOnly() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("set", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.DEPENDENCY_STANFORD, "src", ta, 1.0f);
// Constituent left = new Constituent("token", ViewNames.DEPENDENCY_STANFORD, ta, 0, 1);
// Constituent right = new Constituent("token", ViewNames.DEPENDENCY_STANFORD, ta, 2, 3);
// view.addConstituent(left);
// view.addConstituent(right);
// Relation rel = new Relation("cross", left, right, 1.0f);
// left.addRelation(rel);
// view.addRelation(rel);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, view);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("OTHER"));
// String[] rels = comma.getLeftToRightDependencies();
// assertEquals(1, rels.length);
// assertEquals("cross", rels[0]);
}

@Test
public void testGetRightToLeftDependenciesCrossingCommaOnly() {
String[] tokens = { "Left", ",", "Right" };
// TextAnnotation ta = new TextAnnotation("a", "b", "", new String[][] { tokens }, new int[][] {});
// TreeView view = new TreeView(ViewNames.DEPENDENCY_STANFORD, "dep", ta, 1.0f);
// Constituent left = new Constituent("tok", ViewNames.DEPENDENCY_STANFORD, ta, 0, 1);
// Constituent right = new Constituent("tok", ViewNames.DEPENDENCY_STANFORD, ta, 2, 3);
// view.addConstituent(left);
// view.addConstituent(right);
// Relation back = new Relation("r2l", right, left, 1.0f);
// left.addIncomingRelation(back);
// view.addRelation(back);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, view);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("CL"));
// String[] result = comma.getRightToLeftDependencies();
// assertEquals(1, result.length);
// assertEquals("r2l", result[0]);
}

@Test
public void testGetNamedEntityTagWithMultipleOverlapsReturned() {
String[] tokens = { "Barack", "Obama", ",", "was", "president" };
// TextAnnotation ta = new TextAnnotation("g", "id", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "ner", ta, 1.0f);
// nerView.addSpanLabel(0, 2, "PERSON", 1.0);
// nerView.addSpanLabel(1, 2, "POLITICIAN", 1.0);
// ta.addView(ViewNames.NER_CONLL, nerView);
// Constituent wider = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(2, s, Arrays.asList("APPOS"));
// String result = comma.getNamedEntityTag(wider);
// assertTrue(result.contains("+PERSON"));
}

@Test
public void testGetNotationWithFunctionTagLabel() {
String[] tokens = { "X", ",", "Y" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// Constituent c = new Constituent("NP-LOC", ViewNames.PARSE_STANFORD, ta, 0, 2);
// TreeView parse = new TreeView(ViewNames.PARSE_STANFORD, "src", ta, 1.0);
// ta.addView(ViewNames.PARSE_STANFORD, parse);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// CommaProperties.getInstance().setLexicaliseNER(false);
// CommaProperties.getInstance().setLexicalisePOS(false);
// Comma comma = new Comma(1, s, Arrays.asList("APPOS"));
// String result = comma.getNotation(c);
// assertEquals("NP-LOC", result);
}

@Test
public void testGetPhraseToLeftOfCommaWithNegativeDistance() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("c", "d", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "parser", ta, 1.0f);
// Constituent commaToken = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// tree.addConstituent(commaToken);
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// ta.addView(ViewNames.PARSE_GOLD, tree);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("CL"));
// Constituent result = comma.getPhraseToLeftOfComma(-5);
// assertNull(result);
}

@Test
public void testGetPhraseToRightOfCommaWhenSiblingsMissing() {
String[] tokens = { "this", ",", "that" };
// TextAnnotation ta = new TextAnnotation("tid", "sid", "", new String[][] { tokens }, new int[][] {});
// TreeView tree = new TreeView(ViewNames.PARSE_STANFORD, "annotator", ta, 1.0f);
// ta.addView(ViewNames.PARSE_STANFORD, tree);
// ta.addView(ViewNames.PARSE_GOLD, tree);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
Comma.useGoldFeatures(true);
// Comma comma = new Comma(1, s, Arrays.asList("ENUM"));
// Constituent result = comma.getPhraseToRightOfComma(2);
// assertNull(result);
}

@Test
public void testCommaWithNullLabelList() {
String[] tokens = { "A", ",", "B" };
// TextAnnotation ta = new TextAnnotation("corp", "sid", "", new String[][] { tokens }, new int[][] {});
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence);
// assertNull(comma.getLabels());
}

@Test
public void testEmptyTextAnnotation() {
String[] tokens = {};
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { tokens }, new int[][] {});
// ta.setTokens(tokens);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(0, s, Arrays.asList("X"));
// String result = comma.getWordToLeft(1);
// assertEquals("$$$", result);
}

@Test
public void testSiblingPhraseNgramsIncludesNull() {
String[] tokens = { "begin", ",", "end" };
// TextAnnotation ta = new TextAnnotation("x", "y", "", new String[][] { tokens }, new int[][] {});
// TreeView treeView = new TreeView(ViewNames.PARSE_STANFORD, "parser", ta, 1.0f);
// ta.addView(ViewNames.PARSE_STANFORD, treeView);
// ta.addView(ViewNames.PARSE_GOLD, treeView);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// CommaProperties.getInstance().setLexicaliseNER(false);
// CommaProperties.getInstance().setLexicalisePOS(false);
// Comma comma = new Comma(1, s, Arrays.asList("ENUM"));
// String[] ngrams = comma.getSiblingPhraseNgrams();
boolean foundNull = false;
// for (String ngram : ngrams) {
// if (ngram.contains("NULL")) {
// foundNull = true;
// break;
// }
// }
assertTrue(foundNull);
}

@Test
public void testParentSiblingPhraseNgramsCentralTokenIsNULLIfParentNotFound() {
String[] tokens = { "one", ",", "two", "three" };
// TextAnnotation ta = new TextAnnotation("set", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView treeView = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0f);
// ta.addView(ViewNames.PARSE_STANFORD, treeView);
// ta.addView(ViewNames.PARSE_GOLD, treeView);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("CL"));
// String[] result = comma.getParentSiblingPhraseNgrams();
boolean hasNull = false;
// for (String s1 : result) {
// if (s1.contains("NULL")) {
// hasNull = true;
// break;
// }
// }
assertTrue(hasNull);
}

@Test
public void testGetBayraktarAnnotatedTextReturnsOtherIfLabelIsNull() {
String[] tokens = { "foo", ",", "bar" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// TreeView parse = new TreeView(ViewNames.PARSE_STANFORD, "src", ta, 1.0f);
// Constituent node = new Constituent(",", ViewNames.PARSE_STANFORD, ta, 1, 2);
// Constituent parent = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 1, 2);
// parse.addConstituent(node);
// parse.addConstituent(parent);
// parse.addConstituentRelation(new Relation("child", parent, node, 1.0));
// ta.addView(ViewNames.PARSE_STANFORD, parse);
// ta.addView(ViewNames.PARSE_GOLD, parse);
// CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("ENUM"));
// String result = comma.getBayraktarAnnotatedText();
// assertTrue(result.contains("[") && result.contains("]"));
}

@Test
public void testGetNamedEntityTagReturnsEmptyOnNoMatch() {
String[] tokens = { "London", ",", "UK" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { tokens }, new int[][] {});
// SpanLabelView ner = new SpanLabelView(ViewNames.NER_CONLL, "annotator", ta, 1.0f);
// ta.addView(ViewNames.NER_CONLL, ner);
// Constituent c = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 1);
// CommaSRLSentence s = new CommaSRLSentence(ta, ta);
// Comma comma = new Comma(1, s, Arrays.asList("CL"));
// String result = comma.getNamedEntityTag(c);
// assertEquals("", result);
}
}
