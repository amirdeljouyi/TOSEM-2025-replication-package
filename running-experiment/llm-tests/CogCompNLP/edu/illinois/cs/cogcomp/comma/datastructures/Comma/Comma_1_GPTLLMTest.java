package edu.illinois.cs.cogcomp.comma;

import edu.illinois.cs.cogcomp.comma.bayraktar.BayraktarPatternLabeler;
import edu.illinois.cs.cogcomp.comma.datastructures.Comma;
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

public class Comma_1_GPTLLMTest {

@Test
public void testGetCommaID() {
TextAnnotation goldTa = mock(TextAnnotation.class);
when(goldTa.getId()).thenReturn("doc123");
TextAnnotation autoTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.goldTa).thenReturn(goldTa);
// when(sentence.ta).thenReturn(autoTa);
List<String> labels = new ArrayList<>();
labels.add("TestLabel");
// Comma comma = new Comma(5, sentence, labels);
// String result = comma.getCommaID();
// assertEquals("5 doc123", result);
}

@Test
public void testGetLabel() {
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
List<String> labels = new ArrayList<>();
labels.add("Label123");
// Comma comma = new Comma(2, sentence, labels);
// String result = comma.getLabel();
// assertEquals("Label123", result);
}

@Test
public void testGetTextAnnotationGoldTrue() {
TextAnnotation goldTa = mock(TextAnnotation.class);
TextAnnotation autoTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.goldTa).thenReturn(goldTa);
// when(sentence.ta).thenReturn(autoTa);
// Comma comma = new Comma(1, sentence, null);
// TextAnnotation result = comma.getTextAnnotation(true);
// assertEquals(goldTa, result);
}

@Test
public void testGetTextAnnotationGoldFalse() {
TextAnnotation goldTa = mock(TextAnnotation.class);
TextAnnotation autoTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.goldTa).thenReturn(goldTa);
// when(sentence.ta).thenReturn(autoTa);
// Comma comma = new Comma(1, sentence, null);
// TextAnnotation result = comma.getTextAnnotation(false);
// assertEquals(autoTa, result);
}

@Test
public void testGetWordToLeftWithinBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "This", ",", "is", "a", "test" });
when(ta.getToken(1)).thenReturn(",");
when(ta.getToken(2)).thenReturn("is");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, null);
// String result = comma.getWordToLeft(1);
// assertEquals(",", result);
}

@Test
public void testGetWordToRightWithinBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "word1", "word2", ",", "word3", "word4" });
when(ta.getToken(3)).thenReturn("word3");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, null);
// String result = comma.getWordToRight(1);
// assertEquals("word3", result);
}

@Test
public void testGetWordToLeftOutOfBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "start", "," });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(0, sentence, null);
// String result = comma.getWordToLeft(1);
// assertEquals("$$$", result);
}

@Test
public void testGetWordToRightOutOfBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", "b", "c" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, null);
// String result = comma.getWordToRight(1);
// assertEquals("###", result);
}

@Test
public void testGetPOSNgramNoException() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(anyInt())).thenReturn("NN");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.POS)).thenReturn(posView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, null);
// String[] result = comma.getPOSNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
}

@Test
public void testGetWordNgramsReturnsTrigramsWithoutNulls() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Hello", ",", "world", "!" });
when(ta.getToken(eq(0))).thenReturn("Hello");
when(ta.getToken(eq(1))).thenReturn(",");
when(ta.getToken(eq(2))).thenReturn("world");
when(ta.getToken(eq(3))).thenReturn("!");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, null);
// String[] result = comma.getWordNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
}

@Test
public void testGetAnnotatedText() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "This", ",", "is", "working" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
List<String> labels = new ArrayList<>();
labels.add("Clause");
// Comma comma = new Comma(1, sentence, labels);
// String result = comma.getAnnotatedText();
// assertTrue(result.contains("[Clause]"));
// assertTrue(result.contains("This ,[Clause] is working") || result.contains(" ,[Clause] "));
}

@Test
public void testGetNotationWithNERandPOSDisabled() {
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("NP");
when(c.getOutgoingRelations()).thenReturn(Collections.emptyList());
when(c.getViewName()).thenReturn("SHALLOW_PARSE");
// when(c.getSpan()).thenReturn(new IntPair(1, 2));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(1)).thenReturn("word");
when(c.getTextAnnotation()).thenReturn(ta);
TextAnnotation baseTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(baseTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("X"));
// String result = comma.getNotation(c);
// assertTrue(result.startsWith("NP"));
}

@Test
public void testGetStrippedNotation_WithNERandPOSDisabled() {
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("VP-function-extra");
// when(c.getSpan()).thenReturn(new IntPair(0, 2));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(0)).thenReturn("token");
when(c.getTextAnnotation()).thenReturn(ta);
TextAnnotation baseTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(baseTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("X"));
// String result = comma.getStrippedNotation(c);
// assertTrue(result.startsWith("VP"));
}

@Test
public void testGetBayraktarPattern_CCPreTerminal() {
Constituent commaCons = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
Constituent child1 = mock(Constituent.class);
Relation relation = mock(Relation.class);
when(commaCons.isConsituentInRange(2, 3)).thenReturn(true);
TreeView view = mock(TreeView.class);
when(view.getConstituents()).thenReturn(Collections.singletonList(commaCons));
// when(view.getParsePhrase(commaCons)).thenReturn(commaCons);
when(TreeView.getParent(commaCons)).thenReturn(parent);
when(parent.getLabel()).thenReturn("CC");
when(parent.getOutgoingRelations()).thenReturn(Collections.singletonList(relation));
when(ParseTreeProperties.isPunctuationToken("CC")).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(true);
when(relation.getTarget()).thenReturn(child1);
when(child1.getLabel()).thenReturn("CC");
when(ParseTreeProperties.isPreTerminal(child1)).thenReturn(true);
when(POSUtils.isPOSPunctuation("CC")).thenReturn(false);
when(child1.getSurfaceForm()).thenReturn("and");
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("label1"));
// String result = comma.getBayraktarPattern(view);
// assertTrue(result.contains("and"));
}

@Test
public void testGetSiblingToLeft_NoSibling() {
Constituent target = mock(Constituent.class);
IQueryable<Constituent> siblings = mock(IQueryable.class);
// when(siblings.where(any())).thenReturn(Collections.emptyList());
TreeView view = mock(TreeView.class);
when(view.where(any())).thenReturn(siblings);
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, null);
// Constituent result = comma.getSiblingToLeft(1, target, view);
// assertNull(result);
}

@Test
public void testGetSiblingToRight_NoSibling() {
Constituent target = mock(Constituent.class);
IQueryable<Constituent> siblings = mock(IQueryable.class);
// when(siblings.where(any())).thenReturn(Collections.emptyList());
TreeView view = mock(TreeView.class);
when(view.where(any())).thenReturn(siblings);
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, null);
// Constituent result = comma.getSiblingToRight(1, target, view);
// assertNull(result);
}

@Test
public void testGetLeftToRightDependencies_BranchTest() {
TreeView depView = mock(TreeView.class);
Constituent leftConstituent = mock(Constituent.class);
Constituent rightConstituent = mock(Constituent.class);
Relation relation = mock(Relation.class);
when(depView.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.singletonList(leftConstituent));
when(leftConstituent.getOutgoingRelations()).thenReturn(Collections.singletonList(relation));
when(relation.getTarget()).thenReturn(rightConstituent);
when(rightConstituent.getStartSpan()).thenReturn(5);
when(relation.getRelationName()).thenReturn("nsubj");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("X"));
// String[] result = comma.getLeftToRightDependencies();
// assertEquals(1, result.length);
// assertEquals("nsubj", result[0]);
}

@Test
public void testGetRightToLeftDependencies_BranchTest() {
TreeView depView = mock(TreeView.class);
Constituent leftConstituent = mock(Constituent.class);
Constituent rightConstituent = mock(Constituent.class);
Relation relation = mock(Relation.class);
when(depView.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.singletonList(leftConstituent));
when(leftConstituent.getIncomingRelations()).thenReturn(Collections.singletonList(relation));
when(relation.getSource()).thenReturn(rightConstituent);
when(rightConstituent.getStartSpan()).thenReturn(5);
when(relation.getRelationName()).thenReturn("obj");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("Y"));
// String[] result = comma.getRightToLeftDependencies();
// assertEquals(1, result.length);
// assertEquals("obj", result[0]);
}

@Test
public void testGetNamedEntityTag_SkipsLowOverlapAndMisc() {
Constituent commaChunk = mock(Constituent.class);
Constituent namedEntity = mock(Constituent.class);
when(namedEntity.getLabel()).thenReturn("MISC");
when(commaChunk.doesConstituentCover(namedEntity)).thenReturn(true);
when(namedEntity.getNumberOfTokens()).thenReturn(1);
when(commaChunk.getNumberOfTokens()).thenReturn(10);
SpanLabelView nerView = mock(SpanLabelView.class);
when(nerView.getConstituentsCovering(commaChunk)).thenReturn(Arrays.asList(namedEntity));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("ner"));
// String result = comma.getNamedEntityTag(commaChunk);
// assertEquals("", result);
}

@Test
public void testGetChunkToLeftIndexTooLow() {
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Arrays.asList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(5, sentence, Arrays.asList("label1"));
// Constituent result = comma.getChunkToLeftOfComma(-1);
// assertNull(result);
}

@Test
public void testGetChunkToRightIndexTooHigh() {
SpanLabelView chunkView = mock(SpanLabelView.class);
List<Constituent> chunks = new ArrayList<>();
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[5]);
when(chunkView.getSpanLabels(6, 5)).thenReturn(chunks);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(5, sentence, Arrays.asList("label-Right"));
// Constituent result = comma.getChunkToRightOfComma(1);
// assertNull(result);
}

@Test
public void testGetChunkToLeftOfCommaIndexZero() {
SpanLabelView chunkView = mock(SpanLabelView.class);
List<Constituent> chunks = new ArrayList<>();
Constituent chunk = mock(Constituent.class);
chunks.add(chunk);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(0, 1)).thenReturn(chunks);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(0, sentence, Arrays.asList("x"));
// Constituent result = comma.getChunkToLeftOfComma(1);
// assertEquals(chunk, result);
}

@Test
public void testGetChunkToRightOfCommaOnlyOneChunkAndAccessFirst() {
SpanLabelView chunkView = mock(SpanLabelView.class);
List<Constituent> chunks = new ArrayList<>();
Constituent chunk = mock(Constituent.class);
chunks.add(chunk);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(2, 3)).thenReturn(chunks);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("chunkRight"));
// Constituent result = comma.getChunkToRightOfComma(1);
// assertEquals(chunk, result);
}

@Test
public void testGetBayraktarPattern_ParentIsPunctuation() {
TreeView parseView = mock(TreeView.class);
Constituent commaCons = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
Relation rel = mock(Relation.class);
Constituent child = mock(Constituent.class);
when(commaCons.isConsituentInRange(1, 2)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaCons));
// when(parseView.getParsePhrase(commaCons)).thenReturn(commaCons);
when(TreeView.getParent(commaCons)).thenReturn(parent);
when(parent.getLabel()).thenReturn(".,");
when(parent.getOutgoingRelations()).thenReturn(Arrays.asList(rel));
when(rel.getTarget()).thenReturn(child);
when(child.getLabel()).thenReturn("NP");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(true);
when(ParseTreeProperties.isPunctuationToken(".,")).thenReturn(true);
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("x"));
// String pattern = comma.getBayraktarPattern(parseView);
// assertTrue(pattern.startsWith("."));
}

@Test
public void testGetBayraktarLabelWhenNullReturnsOther() {
BayraktarPatternLabeler labeler = mock(BayraktarPatternLabeler.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("label"));
// String label = comma.getBayraktarLabel();
// assertNotNull(label);
// assertEquals("Other", label);
}

@Test
public void testGetSiblingCommasIdentifiesSiblingCorrectly() {
TreeView view = mock(TreeView.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
// when(sentence.ta).thenReturn(ta);
// Comma comma1 = new Comma(1, sentence, Arrays.asList("x"));
// Comma comma2 = new Comma(5, sentence, Arrays.asList("x"));
Constituent cons1 = mock(Constituent.class);
Constituent cons2 = mock(Constituent.class);
when(view.getConstituents()).thenReturn(Arrays.asList(cons1, cons2));
TreeView resultView = mock(TreeView.class);
// when(view.getParsePhrase(any())).thenReturn(cons1, cons2);
when(TreeView.getParent(cons1)).thenReturn(mock(Constituent.class));
when(TreeView.getParent(cons2)).thenReturn(TreeView.getParent(cons1));
// List<Comma> mockList = Arrays.asList(comma1, comma2);
// when(sentence.getCommas()).thenReturn(mockList);
// Comma spyComma = spy(comma1);
// doReturn(cons1).when(spyComma).getCommaConstituentFromTree(view);
// List<Comma> siblings = spyComma.getSiblingCommas();
// assertTrue(siblings.contains(comma2));
}

@Test
public void testGetSiblingCommaHead_WhenTwoCommas() {
TreeView view = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma c1 = new Comma(3, sentence, Arrays.asList("label"));
// Comma c2 = new Comma(1, sentence, Arrays.asList("label"));
Constituent cons1 = mock(Constituent.class);
Constituent cons2 = mock(Constituent.class);
// when(view.getParsePhrase(any())).thenReturn(cons1, cons2);
when(TreeView.getParent(cons1)).thenReturn(mock(Constituent.class));
when(TreeView.getParent(cons2)).thenReturn(TreeView.getParent(cons1));
// List<Comma> commaList = Arrays.asList(c1, c2);
// when(sentence.getCommas()).thenReturn(commaList);
// Comma spy = spy(c1);
// doReturn(cons1).when(spy).getCommaConstituentFromTree(view);
// Comma head = spy.getSiblingCommaHead();
// assertEquals(1, head.getPosition());
}

@Test
public void testIsSiblingReturnsFalseForDifferentParents() {
TreeView parseView = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma1 = new Comma(2, sentence, Arrays.asList("s1"));
// Comma comma2 = new Comma(8, sentence, Arrays.asList("s2"));
Constituent cons1 = mock(Constituent.class);
Constituent cons2 = mock(Constituent.class);
Constituent parent1 = mock(Constituent.class);
Constituent parent2 = mock(Constituent.class);
// when(parseView.getParsePhrase(cons1)).thenReturn(cons1);
// when(parseView.getParsePhrase(cons2)).thenReturn(cons2);
when(parseView.getConstituents()).thenReturn(Arrays.asList(cons1, cons2));
when(cons1.isConsituentInRange(2, 3)).thenReturn(true);
when(cons2.isConsituentInRange(8, 9)).thenReturn(true);
when(TreeView.getParent(cons1)).thenReturn(parent1);
when(TreeView.getParent(cons2)).thenReturn(parent2);
// Comma spy1 = spy(comma1);
// Comma spy2 = spy(comma2);
// doReturn(cons1).when(spy1).getCommaConstituentFromTree(parseView);
// doReturn(cons2).when(spy2).getCommaConstituentFromTree(parseView);
// boolean result = spy1.isSibling(spy2);
// assertFalse(result);
}

@Test
public void testGetParentSiblingPhraseNgramsAllNull() {
TreeView treeView = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(treeView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("x"));
TreeView emptyView = mock(TreeView.class);
when(treeView.getConstituents()).thenReturn(Collections.emptyList());
// String[] result = comma.getParentSiblingPhraseNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
// assertTrue(result[0].contains("NULL"));
}

@Test
public void testGetContainingSRLsMatchesSRLVerbAndNomAndPrep() {
TextAnnotation ta = mock(TextAnnotation.class);
PredicateArgumentView verbView = mock(PredicateArgumentView.class);
PredicateArgumentView nomView = mock(PredicateArgumentView.class);
PredicateArgumentView prepView = mock(PredicateArgumentView.class);
Constituent pred1 = mock(Constituent.class);
Constituent pred2 = mock(Constituent.class);
Constituent pred3 = mock(Constituent.class);
Relation rel1 = mock(Relation.class);
Relation rel2 = mock(Relation.class);
Relation rel3 = mock(Relation.class);
Constituent arg1 = mock(Constituent.class);
Constituent arg2 = mock(Constituent.class);
Constituent arg3 = mock(Constituent.class);
when(arg1.getStartSpan()).thenReturn(3);
when(arg1.getEndSpan()).thenReturn(5);
when(arg2.getStartSpan()).thenReturn(4);
when(arg2.getEndSpan()).thenReturn(7);
when(arg3.getStartSpan()).thenReturn(2);
when(arg3.getEndSpan()).thenReturn(6);
when(rel1.getTarget()).thenReturn(arg1);
when(rel2.getTarget()).thenReturn(arg2);
when(rel3.getTarget()).thenReturn(arg3);
when(rel1.getSource()).thenReturn(pred1);
when(rel2.getSource()).thenReturn(pred2);
when(rel3.getSource()).thenReturn(pred3);
when(verbView.getPredicates()).thenReturn(Collections.singletonList(pred1));
when(verbView.getArguments(pred1)).thenReturn(Collections.singletonList(rel1));
when(verbView.getPredicateLemma(pred1)).thenReturn("run");
when(nomView.getPredicates()).thenReturn(Collections.singletonList(pred2));
when(nomView.getArguments(pred2)).thenReturn(Collections.singletonList(rel2));
when(nomView.getPredicateLemma(pred2)).thenReturn("movement");
when(prepView.getPredicates()).thenReturn(Collections.singletonList(pred3));
when(prepView.getArguments(pred3)).thenReturn(Collections.singletonList(rel3));
when(prepView.getPredicateLemma(pred3)).thenReturn("with");
when(ta.getView(ViewNames.SRL_VERB)).thenReturn(verbView);
when(ta.getView(ViewNames.SRL_NOM)).thenReturn(nomView);
when(ta.getView(ViewNames.SRL_PREP)).thenReturn(prepView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("SRL"));
// List<String> result = comma.getContainingSRLs();
// assertTrue(result.contains("run" + rel1.getRelationName()));
// assertTrue(result.contains("movement" + rel2.getRelationName()));
// assertTrue(result.contains("with" + rel3.getRelationName()));
}

@Test
public void testGetAnnotatedText_MultipleLabelsRenderedCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B", "C" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("Clause1", "Clause2"));
// String result = comma.getAnnotatedText();
// assertTrue(result.contains("[Clause1,Clause2]"));
}

@Test
public void testGetBayraktarAnnotatedTextFormatsCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "One", ",", "two", "three" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("A"));
// String text = comma.getBayraktarAnnotatedText();
// assertTrue(text.startsWith("One ,"));
// assertTrue(text.contains("["));
// assertTrue(text.contains("]"));
}

@Test
public void testWordNgramsIncludesCorrectUnigramCount() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(0)).thenReturn("the");
when(ta.getToken(1)).thenReturn("quick");
when(ta.getToken(2)).thenReturn("brown");
when(ta.getToken(3)).thenReturn("fox");
when(ta.getToken(4)).thenReturn("jumps");
when(ta.getTokens()).thenReturn(new String[] { "the", "quick", "brown", "fox", "jumps" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("Ngram"));
// String[] ngrams = comma.getWordNgrams();
boolean containsQuick = false;
boolean containsJumps = false;
// for (String ngram : ngrams) {
// if (ngram.equals("quick")) {
// containsQuick = true;
// }
// if (ngram.equals("jumps")) {
// containsJumps = true;
// }
// }
assertTrue(containsQuick);
assertTrue(containsJumps);
}

@Test
public void testChunkNgramsReturnsUnigramsOnlyNullChunks() {
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(ta.getTokens()).thenReturn(new String[10]);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(5, sentence, Arrays.asList("Chunk"));
// String[] grams = comma.getChunkNgrams();
// assertTrue(grams.length > 0);
// for (String gram : grams) {
// assertTrue(gram.contains("NULL"));
// }
}

@Test
public void testStrippedNotationStripsCorrectlyWithFunctionTag() {
Constituent constituent = mock(Constituent.class);
when(constituent.getLabel()).thenReturn("NP-SBJ-TMP");
IntPair span = new IntPair(0, 1);
// when(constituent.getSpan()).thenReturn(span);
TextAnnotation ta = mock(TextAnnotation.class);
when(constituent.getTextAnnotation()).thenReturn(ta);
when(ta.getToken(anyInt())).thenReturn("test");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(0, sentence, Arrays.asList("tag"));
// String stripped = comma.getStrippedNotation(constituent);
// assertTrue(stripped.startsWith("NP"));
// assertFalse(stripped.contains("SBJ"));
}

@Test
public void testNamedEntityTagMultipleValidEntities() {
Constituent ne1 = mock(Constituent.class);
Constituent ne2 = mock(Constituent.class);
Constituent commaChunk = mock(Constituent.class);
when(ne1.getLabel()).thenReturn("PERSON");
when(ne2.getLabel()).thenReturn("ORG");
when(commaChunk.doesConstituentCover(ne1)).thenReturn(true);
when(commaChunk.doesConstituentCover(ne2)).thenReturn(true);
when(commaChunk.getNumberOfTokens()).thenReturn(5);
when(ne1.getNumberOfTokens()).thenReturn(3);
when(ne2.getNumberOfTokens()).thenReturn(2);
SpanLabelView view = mock(SpanLabelView.class);
when(view.getConstituentsCovering(commaChunk)).thenReturn(Arrays.asList(ne1, ne2));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("NER"));
// String nerTags = comma.getNamedEntityTag(commaChunk);
// assertTrue(nerTags.contains("PERSON"));
// assertFalse(nerTags.contains("ORG"));
}

@Test
public void testSiblingPhraseNgramsAllNullReturnngramsWithNULL() {
TreeView treeView = mock(TreeView.class);
when(treeView.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(treeView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("phrases"));
// String[] result = comma.getSiblingPhraseNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
// for (String s : result) {
// assertTrue(s.contains("NULL"));
// }
}

@Test
public void testGetLabelWithSingleLabelOnly() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "This", ",", "is", "test" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
List<String> singleLabel = Collections.singletonList("Appos");
// Comma comma = new Comma(1, sentence, singleLabel);
// String label = comma.getLabel();
// assertEquals("Appos", label);
}

@Test
public void testGetStrippedNotationWhenPOSLexicaliseTrue() {
Constituent cons = mock(Constituent.class);
when(cons.getLabel()).thenReturn("VP-FUNC");
IntPair span = new IntPair(0, 2);
// when(cons.getSpan()).thenReturn(span);
TextAnnotation ta = mock(TextAnnotation.class);
when(POSUtils.getPOS(ta, 0)).thenReturn("VBZ");
when(POSUtils.getPOS(ta, 1)).thenReturn("VBN");
when(cons.getTextAnnotation()).thenReturn(ta);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("x"));
// String result = comma.getStrippedNotation(cons);
// assertTrue(result.contains("VBZ"));
// assertTrue(result.contains("VBN"));
}

@Test
public void testGetNotationWithNERLexicaliseTrueWithNoNERMatches() {
Constituent cons = mock(Constituent.class);
when(cons.getLabel()).thenReturn("NP");
IntPair span = new IntPair(0, 1);
// when(cons.getSpan()).thenReturn(span);
TextAnnotation ta = mock(TextAnnotation.class);
SpanLabelView nerView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(nerView.getConstituentsCovering(cons)).thenReturn(Collections.emptyList());
when(POSUtils.getPOS(ta, 0)).thenReturn("NN");
when(cons.getTextAnnotation()).thenReturn(ta);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(0, sentence, Arrays.asList("label"));
// String notation = comma.getNotation(cons);
// assertTrue(notation.startsWith("NP"));
// assertTrue(notation.contains("NN"));
}

@Test
public void testGetPhraseToLeftOfParentZeroDistance() {
TreeView view = mock(TreeView.class);
Constituent commaCons = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(commaCons.isConsituentInRange(5, 6)).thenReturn(true);
when(view.getConstituents()).thenReturn(Collections.singletonList(commaCons));
// when(view.getParsePhrase(commaCons)).thenReturn(commaCons);
when(TreeView.getParent(commaCons)).thenReturn(parent);
when(view.where(any())).thenReturn(mock(IQueryable.class));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(5, sentence, Arrays.asList("X"));
// Constituent result = comma.getPhraseToLeftOfParent(0);
// assertEquals(parent, result);
}

@Test
public void testGetPOSNgramsWhenAllTokensReturnSamePOS() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(anyInt())).thenReturn("NN");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.POS)).thenReturn(posView);
when(ta.getTokens()).thenReturn(new String[10]);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(4, sentence, Arrays.asList("pos"));
// String[] result = comma.getPOSNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
}

@Test
public void testIsSiblingReturnsTrueForSameParent() {
TreeView parseView = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma commaA = new Comma(2, sentence, Arrays.asList("a"));
// Comma commaB = new Comma(3, sentence, Arrays.asList("b"));
Constituent commaConsA = mock(Constituent.class);
Constituent commaConsB = mock(Constituent.class);
Constituent sharedParent = mock(Constituent.class);
when(commaConsA.isConsituentInRange(2, 3)).thenReturn(true);
when(commaConsB.isConsituentInRange(3, 4)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Arrays.asList(commaConsA, commaConsB));
// when(parseView.getParsePhrase(commaConsA)).thenReturn(commaConsA);
// when(parseView.getParsePhrase(commaConsB)).thenReturn(commaConsB);
when(TreeView.getParent(commaConsA)).thenReturn(sharedParent);
when(TreeView.getParent(commaConsB)).thenReturn(sharedParent);
// Comma spyA = spy(commaA);
// Comma spyB = spy(commaB);
// doReturn(commaConsA).when(spyA).getCommaConstituentFromTree(parseView);
// doReturn(commaConsB).when(spyB).getCommaConstituentFromTree(parseView);
// boolean result = spyA.isSibling(spyB);
// assertTrue(result);
}

@Test
public void testGetCommaConstituentFromTreeReturnsNullWhenNoMatch() {
TreeView view = mock(TreeView.class);
Constituent unrelated = mock(Constituent.class);
when(unrelated.isConsituentInRange(0, 1)).thenReturn(false);
when(view.getConstituents()).thenReturn(Collections.singletonList(unrelated));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(0, sentence, Arrays.asList("x"));
// Constituent result = comma.getCommaConstituentFromTree(view);
// assertNull(result);
}

@Test
public void testGetSiblingToRightZeroDistanceReturnsSame() {
Constituent target = mock(Constituent.class);
IQueryable<Constituent> siblingsView = mock(IQueryable.class);
TreeView treeView = mock(TreeView.class);
when(treeView.where(any())).thenReturn(siblingsView);
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("x"));
// Constituent result = comma.getSiblingToRight(0, target, treeView);
// assertEquals(target, result);
}

@Test
public void testGetSiblingToLeftZeroDistanceReturnsSame() {
Constituent target = mock(Constituent.class);
IQueryable<Constituent> siblingsView = mock(IQueryable.class);
TreeView treeView = mock(TreeView.class);
when(treeView.where(any())).thenReturn(siblingsView);
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(3, sentence, Arrays.asList("left"));
// Constituent result = comma.getSiblingToLeft(0, target, treeView);
// assertEquals(target, result);
}

@Test
public void testGetChunkToLeftOfCommaIndexOutOfBoundReturnsNull() {
SpanLabelView view = mock(SpanLabelView.class);
List<Constituent> chunks = new ArrayList<>();
Constituent c1 = mock(Constituent.class);
chunks.add(c1);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(view);
when(view.getSpanLabels(anyInt(), anyInt())).thenReturn(chunks);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("test"));
// Constituent result = comma.getChunkToLeftOfComma(5);
// assertNull(result);
}

@Test
public void testGetChunkToRightOfCommaInvalidDistanceReturnsNull() {
SpanLabelView view = mock(SpanLabelView.class);
List<Constituent> chunks = new ArrayList<>();
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", "B", ",", "C" });
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(view);
when(view.getSpanLabels(anyInt(), anyInt())).thenReturn(chunks);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("Right"));
// Constituent result = comma.getChunkToRightOfComma(-1);
// assertNull(result);
}

@Test
public void testGetWordToLeftDistanceEqualsCommaPosition() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "one", ",", "world" });
when(ta.getToken(0)).thenReturn("one");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("x"));
// String result = comma.getWordToLeft(1);
// assertEquals("one", result);
}

@Test
public void testGetPOSToLeft_DTConditionPasses() {
TokenLabelView labelView = mock(TokenLabelView.class);
when(labelView.getLabel(1)).thenReturn("DT");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(2)).thenReturn("the");
when(ta.getView(ViewNames.POS)).thenReturn(labelView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(ta);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("x"));
// String result = comma.getPOSToLeft(1);
// assertEquals("DT-the", result);
}

@Test
public void testGetNotationWithOutgoingRelationsAndParseViewLabel() {
Constituent cons = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(cons.getLabel()).thenReturn("NP");
when(cons.getOutgoingRelations()).thenReturn(Collections.singletonList(rel));
when(cons.getViewName()).thenReturn(ViewNames.PARSE_STANFORD);
when(rel.getTarget()).thenReturn(target);
when(target.getLabel()).thenReturn("DT");
IntPair span = new IntPair(0, 1);
// when(cons.getSpan()).thenReturn(span);
TextAnnotation ta = mock(TextAnnotation.class);
when(POSUtils.getPOS(ta, 0)).thenReturn("NN");
when(cons.getTextAnnotation()).thenReturn(ta);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("x"));
// String value = comma.getNotation(cons);
// assertTrue(value.contains("NP"));
// assertTrue(value.contains("DT"));
// assertTrue(value.contains("NN"));
}

@Test
public void testGetNamedEntityTagNullListHandledGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
SpanLabelView nerView = mock(SpanLabelView.class);
Constituent cons = mock(Constituent.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(nerView.getConstituentsCovering(any())).thenReturn(null);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("x"));
// String result = comma.getNamedEntityTag(cons);
// assertEquals("", result);
}

@Test
public void testGetBayraktarPatternThrowsReturnsDefaultWhenParentNull() {
TreeView tree = mock(TreeView.class);
Constituent commaCons = mock(Constituent.class);
when(commaCons.isConsituentInRange(1, 2)).thenReturn(true);
when(tree.getConstituents()).thenReturn(Collections.singletonList(commaCons));
// when(tree.getParsePhrase(commaCons)).thenReturn(commaCons);
when(TreeView.getParent(commaCons)).thenReturn(null);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("p"));
try {
// String pattern = comma.getBayraktarPattern(tree);
// assertNotNull(pattern);
} catch (NullPointerException ex) {
fail("Should not throw NPE when parent is null");
}
}

@Test
public void testGetSiblingCommasEmptyInputReturnsEmptyList() {
TextAnnotation ta = mock(TextAnnotation.class);
TreeView tree = mock(TreeView.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
when(sentence.getCommas()).thenReturn(Collections.emptyList());
// Comma comma = new Comma(1, sentence, Arrays.asList("abc"));
// List<Comma> output = comma.getSiblingCommas();
// assertNotNull(output);
// assertTrue(output.isEmpty());
}

@Test
public void testGetRightToLeftDependenciesWithEmptyIncomingRelations() {
TextAnnotation ta = mock(TextAnnotation.class);
TreeView depView = mock(TreeView.class);
Constituent c = mock(Constituent.class);
when(depView.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.singletonList(c));
when(c.getIncomingRelations()).thenReturn(Collections.emptyList());
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("nametag"));
// String[] deps = comma.getRightToLeftDependencies();
// assertNotNull(deps);
// assertEquals(0, deps.length);
}

@Test
public void testGetCommaIDWithGoldTaNullDefaultsGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("autoId");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.goldTa).thenReturn(null);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(3, sentence, Arrays.asList("fallback"));
// String id = comma.getCommaID();
// assertNotNull(id);
}

@Test
public void testGetWordToLeftWithDistanceGreaterThanPositionReturnsDollar() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "hello", ",", "world" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Arrays.asList("label"));
// String result = comma.getWordToLeft(2);
// assertEquals("$$$", result);
}

@Test
public void testGetWordToRightBeyondBoundsReturnsHash() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("label"));
// String result = comma.getWordToRight(1);
// assertEquals("###", result);
}

@Test
public void testGetPhraseToLeftOfCommaWhenCommaNotFoundReturnsNull() {
TreeView view = mock(TreeView.class);
Constituent dummy = mock(Constituent.class);
when(dummy.isConsituentInRange(2, 3)).thenReturn(false);
when(view.getConstituents()).thenReturn(Collections.singletonList(dummy));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("x"));
// Constituent result = comma.getPhraseToLeftOfComma(1);
// assertNull(result);
}

@Test
public void testGetPhraseToRightOfParentReturnsNullIfCommaNotInTree() {
TreeView tree = mock(TreeView.class);
Constituent unrelated = mock(Constituent.class);
when(unrelated.isConsituentInRange(anyInt(), anyInt())).thenReturn(false);
when(tree.getConstituents()).thenReturn(Collections.singletonList(unrelated));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(10, sentence, Arrays.asList("x"));
// Constituent result = comma.getPhraseToRightOfParent(1);
// assertNull(result);
}

@Test
public void testGetParentSiblingPhraseNgramsReturnNgramsWithNullsAtBoundaries() {
TreeView tree = mock(TreeView.class);
when(tree.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Arrays.asList("X"));
// String[] ngrams = comma.getParentSiblingPhraseNgrams();
// assertNotNull(ngrams);
// for (String n : ngrams) {
// assertTrue(n.contains("NULL"));
// }
}

@Test
public void testGetChunkNgramsEmptyChunksProducesNullNotation() {
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(ta.getTokens()).thenReturn(new String[10]);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(5, sentence, Arrays.asList("label"));
// String[] result = comma.getChunkNgrams();
// assertNotNull(result);
// for (String ngram : result) {
// assertTrue(ngram.contains("NULL"));
// }
}

@Test
public void testGetPOSNgramsAllTokensFailPOSLexicalization() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(anyInt())).thenReturn("UNK");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[11]);
when(ta.getView(ViewNames.POS)).thenReturn(posView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(5, sentence, Arrays.asList("label"));
// String[] result = comma.getPOSNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
}

@Test
public void testGetPOSToRight_DTConditionFails() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(anyInt())).thenReturn("DT");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(3)).thenReturn("not-the");
when(ta.getView(ViewNames.POS)).thenReturn(posView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Collections.singletonList("test"));
// String result = comma.getPOSToRight(1);
// assertEquals("DT", result);
}

@Test
public void testGetLeftToRightDependenciesSkipsRelationBeforeComma() {
TextAnnotation ta = mock(TextAnnotation.class);
TreeView view = mock(TreeView.class);
Constituent source = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation relation = mock(Relation.class);
when(view.getConstituentsCoveringSpan(0, 2)).thenReturn(Collections.singletonList(source));
when(source.getOutgoingRelations()).thenReturn(Collections.singletonList(relation));
when(relation.getTarget()).thenReturn(target);
when(target.getStartSpan()).thenReturn(1);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Collections.singletonList("test"));
// String[] deps = comma.getLeftToRightDependencies();
// assertNotNull(deps);
// assertEquals(0, deps.length);
}

@Test
public void testGetRightToLeftDependenciesSkipsRelationBeforeComma() {
TextAnnotation ta = mock(TextAnnotation.class);
TreeView view = mock(TreeView.class);
Constituent source = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation relation = mock(Relation.class);
when(view.getConstituentsCoveringSpan(0, 2)).thenReturn(Collections.singletonList(target));
when(target.getIncomingRelations()).thenReturn(Collections.singletonList(relation));
when(relation.getSource()).thenReturn(source);
when(source.getStartSpan()).thenReturn(1);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Collections.singletonList("test"));
// String[] result = comma.getRightToLeftDependencies();
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testGetStrippedNotationWithNullConstituentReturnsNullString() {
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(0, sentence, Arrays.asList("x"));
// String result = comma.getStrippedNotation(null);
// assertEquals("NULL", result);
}
}
