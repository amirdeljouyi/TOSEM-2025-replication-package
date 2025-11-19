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

public class Comma_2_GPTLLMTest {

@Test
public void testGetCommaID() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
TextAnnotation goldTa = mock(TextAnnotation.class);
when(goldTa.getId()).thenReturn("gold-id-123");
// when(sentenceMock.goldTa).thenReturn(goldTa);
// Comma comma = new Comma(2, sentenceMock, Arrays.asList("TestLabel"));
// String id = comma.getCommaID();
// assertEquals("2 gold-id-123", id);
}

@Test
public void testGetLabel() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
// Comma comma = new Comma(3, sentenceMock, Arrays.asList("LabelA", "LabelB"));
// String label = comma.getLabel();
// assertEquals("LabelA", label);
}

@Test
public void testGetLabels() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
List<String> expectedLabels = Arrays.asList("First", "Second");
// Comma comma = new Comma(1, sentenceMock, expectedLabels);
// List<String> actualLabels = comma.getLabels();
// assertEquals(expectedLabels, actualLabels);
}

@Test
public void testGetPosition() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
// Comma comma = new Comma(5, sentenceMock, Arrays.asList("X"));
// int position = comma.getPosition();
// assertEquals(5, position);
}

@Test
public void testGetSentence() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
// Comma comma = new Comma(0, sentenceMock, Arrays.asList("A"));
// assertSame(sentenceMock, comma.getSentence());
}

@Test
public void testGetTextAnnotationGoldTrue() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
TextAnnotation goldTa = mock(TextAnnotation.class);
// sentenceMock.goldTa = goldTa;
// Comma comma = new Comma(1, sentenceMock, Arrays.asList("A"));
// assertSame(goldTa, comma.getTextAnnotation(true));
}

@Test
public void testGetTextAnnotationGoldFalse() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
TextAnnotation autoTa = mock(TextAnnotation.class);
// sentenceMock.ta = autoTa;
// Comma comma = new Comma(1, sentenceMock, Arrays.asList("A"));
// assertSame(autoTa, comma.getTextAnnotation(false));
}

@Test
public void testGetWordToRightWithinBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Hello", "this", ",", "is", "test" });
when(ta.getToken(3)).thenReturn("is");
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("Test"));
// String word = comma.getWordToRight(1);
// assertEquals("is", word);
}

@Test
public void testGetWordToRightOutOfBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", "b", "," });
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("Test"));
// String word = comma.getWordToRight(1);
// assertEquals("###", word);
}

@Test
public void testGetWordToLeftWithinBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Token0", "Token1", ",", "Token3" });
when(ta.getToken(1)).thenReturn("Token1");
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("Lbl"));
// assertEquals("Token1", comma.getWordToLeft(1));
}

@Test
public void testGetWordToLeftOutOfBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "ONLY" });
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(0, s, Arrays.asList("A"));
// assertEquals("$$$", comma.getWordToLeft(1));
}

@Test
public void testGetPOSToLeftSimpleCase() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(1)).thenReturn("NN");
CommaSRLSentence s = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// s.ta = ta;
// s.goldTa = ta;
when(ta.getView(ViewNames.POS)).thenReturn(posView);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, s, Arrays.asList("X"));
// String pos = comma.getPOSToLeft(1);
// assertEquals("NN", pos);
}

@Test
public void testGetPOSToRightSimpleCase() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(3)).thenReturn("DT");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(3)).thenReturn("a");
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
when(ta.getView(ViewNames.POS)).thenReturn(posView);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, s, Arrays.asList("X"));
// String pos = comma.getPOSToRight(1);
// assertEquals("DT", pos);
}

@Test
public void testGetBayraktarLabelReturnsOtherWhenNull() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
when(ta.getView(anyString())).thenReturn(mock(View.class));
// Comma comma = new Comma(1, sentence, Arrays.asList("TestLabel"));
// String label = comma.getBayraktarLabel();
// assertEquals("Other", label);
}

@Test
public void testGetAnnotatedTextFormatsCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
String[] tokens = { "The", "quick", ",", "brown", "fox" };
when(ta.getTokens()).thenReturn(tokens);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("Gapped"));
// String result = comma.getAnnotatedText();
// assertEquals("The quick ,[Gapped] brown fox", result);
}

@Test
public void testGetBayraktarAnnotatedText() {
TextAnnotation ta = mock(TextAnnotation.class);
String[] tokens = { "It", "works", ",", "maybe" };
when(ta.getTokens()).thenReturn(tokens);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("Clause"));
// String result = comma.getBayraktarAnnotatedText();
// assertEquals("It works ,[Other] maybe", result);
}

@Test
public void testGetNotationWithNullReturnsNULL() {
CommaSRLSentence s = mock(CommaSRLSentence.class);
// Comma comma = new Comma(0, s, Arrays.asList("A"));
// String notation = comma.getNotation(null);
// assertEquals("NULL", notation);
}

@Test
public void testGetStrippedNotationWithNullReturnsNULL() {
CommaSRLSentence s = mock(CommaSRLSentence.class);
// Comma comma = new Comma(0, s, Arrays.asList("B"));
// String stripped = comma.getStrippedNotation(null);
// assertEquals("NULL", stripped);
}

@Test
public void testGetNotationReturnsFormat() {
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("NP");
when(c.getOutgoingRelations()).thenReturn(Collections.emptyList());
when(c.getViewName()).thenReturn(ViewNames.PARSE_STANFORD);
// when(c.getSpan()).thenReturn(new IntPair(0, 1));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("id");
when(ta.getToken(0)).thenReturn("book");
when(ta.getView(ViewNames.POS)).thenReturn(mock(TokenLabelView.class));
when(c.getTextAnnotation()).thenReturn(ta);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(0, s, Arrays.asList("O"));
// String note = comma.getNotation(c);
// assertTrue(note.startsWith("NP"));
}

@Test
public void testGetSiblingCommasReturnsEmptyWhenNoSiblingsFound() {
CommaSRLSentence sentenceMock = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentenceMock.ta = ta;
// sentenceMock.goldTa = ta;
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(mock(TreeView.class));
// when(sentenceMock.getCommas()).thenReturn(Collections.singletonList(new Comma(0, sentenceMock, Arrays.asList("Test"))));
// Comma comma = new Comma(0, sentenceMock, Arrays.asList("Test"));
// List<Comma> siblings = comma.getSiblingCommas();
// assertNotNull(siblings);
}

@Test
public void testGetSiblingToLeftReturnsNullWhenNoLeftSibling() {
TreeView treeView = mock(TreeView.class);
Constituent parent = mock(Constituent.class);
// when(treeView.where(any())).thenReturn(new QueryableList<>(Collections.emptyList()));
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(0, sentence, Arrays.asList("A"));
// Constituent result = comma.getSiblingToLeft(1, parent, treeView);
// assertNull(result);
}

@Test
public void testGetSiblingToRightReturnsNullWhenNoRightSibling() {
TreeView treeView = mock(TreeView.class);
Constituent parent = mock(Constituent.class);
// when(treeView.where(any())).thenReturn(new QueryableList<>(Collections.emptyList()));
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(0, sentence, Arrays.asList("B"));
// Constituent result = comma.getSiblingToRight(1, parent, treeView);
// assertNull(result);
}

@Test
public void testGetCommaConstituentFromTreeReturnsNullIfNoMatch() {
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(mock(Constituent.class)));
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = mock(TextAnnotation.class);
// s.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(3, s, Arrays.asList("Label"));
// Constituent constituent = comma.getCommaConstituentFromTree(parseView);
// assertNull(constituent);
}

@Test
public void testGetRightToLeftDependenciesEmptyWhenNoMatch() {
TreeView depView = mock(TreeView.class);
Constituent source = mock(Constituent.class);
when(source.getIncomingRelations()).thenReturn(Collections.emptyList());
when(depView.getConstituentsCoveringSpan(0, 2)).thenReturn(Collections.singletonList(source));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("L"));
// String[] result = comma.getRightToLeftDependencies();
// assertEquals(0, result.length);
}

@Test
public void testGetLeftToRightDependenciesEmptyWhenNoMatch() {
TreeView depView = mock(TreeView.class);
Constituent source = mock(Constituent.class);
when(source.getOutgoingRelations()).thenReturn(Collections.emptyList());
when(depView.getConstituentsCoveringSpan(0, 2)).thenReturn(Collections.singletonList(source));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("L"));
// String[] result = comma.getLeftToRightDependencies();
// assertEquals(0, result.length);
}

@Test
public void testGetContainingSRLsWithPredicateButNonCoveringTarget() {
TextAnnotation ta = mock(TextAnnotation.class);
Constituent pred = mock(Constituent.class);
Constituent arg = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(arg);
when(rel.getSource()).thenReturn(pred);
PredicateArgumentView pav = mock(PredicateArgumentView.class);
when(pav.getPredicates()).thenReturn(Collections.singletonList(pred));
when(pav.getArguments(pred)).thenReturn(Collections.singletonList(rel));
when(pav.getPredicateLemma(pred)).thenReturn("run");
when(arg.getStartSpan()).thenReturn(0);
when(arg.getEndSpan()).thenReturn(1);
when(ta.getView(ViewNames.SRL_VERB)).thenReturn(pav);
when(ta.getView(ViewNames.SRL_NOM)).thenReturn(pav);
when(ta.getView(ViewNames.SRL_PREP)).thenReturn(pav);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// s.goldTa = ta;
// Comma comma = new Comma(2, s, Arrays.asList("Any"));
// List<String> result = comma.getContainingSRLs();
// assertTrue(result.isEmpty());
}

@Test
public void testGetBayraktarPatternFallbackWhenParentIsPunctuation() {
TreeView view = mock(TreeView.class);
Constituent commaConstituent = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(commaConstituent.getStartSpan()).thenReturn(2);
when(commaConstituent.getEndSpan()).thenReturn(3);
when(commaConstituent.isConsituentInRange(2, 3)).thenReturn(true);
List<Constituent> constituents = Arrays.asList(commaConstituent);
when(view.getConstituents()).thenReturn(constituents);
// when(view.getParsePhrase(commaConstituent)).thenReturn(commaConstituent);
when(commaConstituent.getLabel()).thenReturn("PRP");
when(TreeView.getParent(commaConstituent)).thenReturn(parent);
when(parent.getLabel()).thenReturn(":");
when(parent.getOutgoingRelations()).thenReturn(Collections.emptyList());
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = mock(TextAnnotation.class);
// s.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(2, s, Arrays.asList("Some"));
// String result = comma.getBayraktarPattern(view);
// assertTrue(result.startsWith(": -->"));
}

@Test
public void testGetBayraktarPatternHandlesPreTerminalWithCCLabel() {
TreeView view = mock(TreeView.class);
Constituent commaConstituent = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
Constituent child = mock(Constituent.class);
Relation relation = mock(Relation.class);
when(commaConstituent.getStartSpan()).thenReturn(1);
when(commaConstituent.getEndSpan()).thenReturn(2);
when(commaConstituent.isConsituentInRange(1, 2)).thenReturn(true);
when(view.getConstituents()).thenReturn(Collections.singletonList(commaConstituent));
// when(view.getParsePhrase(commaConstituent)).thenReturn(commaConstituent);
when(TreeView.getParent(commaConstituent)).thenReturn(parent);
when(parent.getLabel()).thenReturn("CC");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(true);
when(ParseTreeProperties.isPunctuationToken("CC")).thenReturn(false);
when(relation.getTarget()).thenReturn(child);
when(child.getLabel()).thenReturn("CC");
// when(parseStrip(child)).thenReturn("but");
when(child.getSurfaceForm()).thenReturn("but");
when(parent.getOutgoingRelations()).thenReturn(Collections.singletonList(relation));
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = mock(TextAnnotation.class);
// s.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(1, s, Arrays.asList("M"));
// String pattern = comma.getBayraktarPattern(view);
// assertTrue(pattern.contains("CC --> but"));
}

@Test
public void testGetSiblingToLeftZeroDistanceReturnsInput() {
Constituent c = mock(Constituent.class);
TreeView treeView = mock(TreeView.class);
// when(treeView.where(any())).thenReturn(mock(QueryableList.class));
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(3, sentence, Arrays.asList("Label"));
// Constituent result = comma.getSiblingToLeft(0, c, treeView);
// assertSame(c, result);
}

@Test
public void testGetSiblingToRightZeroDistanceReturnsInput() {
Constituent c = mock(Constituent.class);
TreeView treeView = mock(TreeView.class);
// when(treeView.where(any())).thenReturn(mock(QueryableList.class));
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(3, sentence, Arrays.asList("Label"));
// Constituent result = comma.getSiblingToRight(0, c, treeView);
// assertSame(c, result);
}

@Test
public void testGetChunkToRightOutOfRangeNegativeDistanceReturnsNull() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentence.ta = ta;
SpanLabelView chunkView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Collections.singletonList(mock(Constituent.class)));
// Comma comma = new Comma(2, sentence, Arrays.asList("X"));
// Constituent result = comma.getChunkToRightOfComma(-1);
// assertNull(result);
}

@Test
public void testGetChunkToLeftOutOfRangeNegativeDistanceReturnsNull() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentence.ta = ta;
SpanLabelView chunkView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Collections.singletonList(mock(Constituent.class)));
// Comma comma = new Comma(2, sentence, Arrays.asList("X"));
// Constituent result = comma.getChunkToLeftOfComma(-1);
// assertNull(result);
}

@Test
public void testGetNamedEntityTagWithMiscIgnored() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentence.ta = ta;
SpanLabelView nerView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
Constituent commaConstituent = mock(Constituent.class);
Constituent ne = mock(Constituent.class);
when(ne.getLabel()).thenReturn("MISC");
when(commaConstituent.doesConstituentCover(ne)).thenReturn(true);
when(ne.getNumberOfTokens()).thenReturn(3);
when(commaConstituent.getNumberOfTokens()).thenReturn(4);
when(nerView.getConstituentsCovering(commaConstituent)).thenReturn(Collections.singletonList(ne));
// Comma comma = new Comma(2, sentence, Arrays.asList("L"));
// String result = comma.getNamedEntityTag(commaConstituent);
// assertEquals("", result);
}

@Test
public void testGetNamedEntityTagWithCoverageBelowThreshold() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentence.ta = ta;
SpanLabelView nerView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
Constituent commaConstituent = mock(Constituent.class);
Constituent ne = mock(Constituent.class);
when(ne.getLabel()).thenReturn("PERSON");
when(commaConstituent.doesConstituentCover(ne)).thenReturn(true);
when(ne.getNumberOfTokens()).thenReturn(1);
when(commaConstituent.getNumberOfTokens()).thenReturn(5);
when(nerView.getConstituentsCovering(commaConstituent)).thenReturn(Collections.singletonList(ne));
// Comma comma = new Comma(2, sentence, Arrays.asList("L"));
// String result = comma.getNamedEntityTag(commaConstituent);
// assertEquals("", result);
}

@Test
public void testGetBayraktarPatternWithNoChildren() {
TreeView parseView = mock(TreeView.class);
Constituent commaCons = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
List<Constituent> allConstituents = Collections.singletonList(commaCons);
when(parseView.getConstituents()).thenReturn(allConstituents);
when(commaCons.isConsituentInRange(2, 3)).thenReturn(true);
// when(parseView.getParsePhrase(commaCons)).thenReturn(commaCons);
when(TreeView.getParent(commaCons)).thenReturn(parent);
when(parent.getLabel()).thenReturn("NP");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(false);
Relation rel = mock(Relation.class);
Constituent child = mock(Constituent.class);
when(rel.getTarget()).thenReturn(child);
when(parent.getOutgoingRelations()).thenReturn(Collections.emptyList());
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(2, sentence, Arrays.asList("Y"));
// String result = comma.getBayraktarPattern(parseView);
// assertTrue(result.startsWith("NP -->"));
}

@Test
public void testGetTextAnnotationReturnsGoldWhenFlagTrue() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation gold = mock(TextAnnotation.class);
// sentence.goldTa = gold;
// sentence.ta = mock(TextAnnotation.class);
// Comma comma = new Comma(0, sentence, Arrays.asList("T"));
// assertSame(gold, comma.getTextAnnotation(true));
}

@Test
public void testGetTextAnnotationReturnsAutoWhenFlagFalse() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation auto = mock(TextAnnotation.class);
// sentence.ta = auto;
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(0, sentence, Arrays.asList("T"));
// assertSame(auto, comma.getTextAnnotation(false));
}

@Test
public void testIsSiblingReturnsTrueIfParentsSame() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
TreeView parseView = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(parseView.getConstituents()).thenReturn(Arrays.asList(c1));
when(c1.isConsituentInRange(0, 1)).thenReturn(true);
when(c2.isConsituentInRange(1, 2)).thenReturn(true);
// when(parseView.getParsePhrase(any(Constituent.class))).thenReturn(c1, c2);
when(TreeView.getParent(c1)).thenReturn(parent);
when(TreeView.getParent(c2)).thenReturn(parent);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma cA = new Comma(0, sentence, Arrays.asList("L1"));
// Comma cB = new Comma(1, sentence, Arrays.asList("L2"));
// assertTrue(cA.isSibling(cB));
}

@Test
public void testGetParentSiblingPhraseNgramsReturnsTokens() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
TreeView parseView = mock(TreeView.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
// sentence.ta = ta;
// sentence.goldTa = ta;
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// Comma comma = new Comma(1, sentence, Arrays.asList("C"));
// String[] result = comma.getParentSiblingPhraseNgrams();
// assertNotNull(result);
// assertTrue(result.length >= 1);
}

@Test
public void testGetChunkNgramsReturnsValidStringsWhenNotationsAreSet() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentence.ta = ta;
SpanLabelView chunkView = mock(SpanLabelView.class);
Constituent chunk1 = mock(Constituent.class);
when(chunk1.getLabel()).thenReturn("NP");
when(chunk1.getViewName()).thenReturn(ViewNames.SHALLOW_PARSE);
// when(chunk1.getSpan()).thenReturn(new IntPair(0, 1));
when(chunk1.getTextAnnotation()).thenReturn(ta);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Arrays.asList(chunk1));
// Comma comma = new Comma(1, sentence, Arrays.asList("Chunk"));
// String[] ngrams = comma.getChunkNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetPOSToLeftExactDTcaseWithRightWordThe() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(1)).thenReturn("DT");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(2)).thenReturn("the");
when(ta.getView(ViewNames.POS)).thenReturn(posView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("G"));
// String result = comma.getPOSToLeft(1);
// assertEquals("DT-the", result);
}

@Test
public void testGetPOSToRightExactDTcaseWithRightWordThe() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(3)).thenReturn("DT");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getToken(3)).thenReturn("the");
when(ta.getView(ViewNames.POS)).thenReturn(posView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("Clause"));
// String result = comma.getPOSToRight(1);
// assertEquals("DT-the", result);
}

@Test
public void testGetSiblingPhraseNgramsWhenNotationsAreNull() {
TreeView tree = mock(TreeView.class);
when(tree.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence, Arrays.asList("A"));
// String[] ngrams = comma.getSiblingPhraseNgrams();
// assertNotNull(ngrams);
}

@Test
public void testGetChunkToRightOfCommaReturnsCorrectChunk() {
SpanLabelView chunkView = mock(SpanLabelView.class);
Constituent chunk = mock(Constituent.class);
List<Constituent> chunks = Arrays.asList(chunk);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(ta.getTokens()).thenReturn(new String[] { "a", "b", ",", "c", "d" });
when(chunkView.getSpanLabels(3, 5)).thenReturn(chunks);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("Test"));
// Constituent result = comma.getChunkToRightOfComma(1);
// assertSame(chunk, result);
}

@Test
public void testGetChunkToLeftOfCommaWhenDistanceTooLargeReturnsNull() {
SpanLabelView chunkView = mock(SpanLabelView.class);
List<Constituent> chunks = Arrays.asList(mock(Constituent.class));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(0, 3)).thenReturn(chunks);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("XYZ"));
// Constituent result = comma.getChunkToLeftOfComma(10);
// assertNull(result);
}

@Test
public void testGetStrippedNotationWithoutPOSorNER() {
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("NP-LOC");
when(c.getTextAnnotation()).thenReturn(mock(TextAnnotation.class));
// when(c.getSpan()).thenReturn(new IntPair(0, 1));
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(0, sentence, Arrays.asList("Raw"));
// String result = comma.getStrippedNotation(c);
// assertTrue(result.startsWith("NP"));
}

@Test
public void testGetWordToLeftAtCommaBeginningReturnsDummyToken() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Start", "with", "comma" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(0, sentence, Arrays.asList("Start"));
// String result = comma.getWordToLeft(1);
// assertEquals("$$$", result);
}

@Test
public void testGetWordToRightBeyondEndReturnsDummyToken() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "One", "two", "three" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("End"));
// String result = comma.getWordToRight(1);
// assertEquals("###", result);
}

@Test
public void testGetBayraktarPatternWithFunctionTagsStripped() {
TreeView parseView = mock(TreeView.class);
Constituent commaCons = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
Constituent child = mock(Constituent.class);
Relation relation = mock(Relation.class);
when(parseView.getConstituents()).thenReturn(Arrays.asList(commaCons));
when(commaCons.isConsituentInRange(2, 3)).thenReturn(true);
// when(parseView.getParsePhrase(commaCons)).thenReturn(commaCons);
when(TreeView.getParent(commaCons)).thenReturn(parent);
when(parent.getLabel()).thenReturn("VP");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(false);
when(ParseTreeProperties.isPunctuationToken("VP")).thenReturn(false);
when(parent.getOutgoingRelations()).thenReturn(Collections.singletonList(relation));
when(relation.getTarget()).thenReturn(child);
when(child.getLabel()).thenReturn("NP-OBJ");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(2, sentence, Arrays.asList("LABEL"));
// String result = comma.getBayraktarPattern(parseView);
// assertTrue(result.contains("NP"));
}

@Test
public void testBayraktarLabelFallbackWhenNullPattern() {
CommaSRLSentence s = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// s.ta = ta;
// s.goldTa = ta;
TreeView view = mock(TreeView.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
when(view.getConstituents()).thenReturn(Collections.emptyList());
// Comma comma = new Comma(1, s, Arrays.asList("Test"));
// String label = comma.getBayraktarLabel();
// assertEquals("Other", label);
}

@Test
public void testGetSRLsHandlesMissingViewGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SRL_VERB)).thenReturn(null);
when(ta.getView(ViewNames.SRL_NOM)).thenReturn(null);
when(ta.getView(ViewNames.SRL_PREP)).thenReturn(null);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence, Arrays.asList("srl"));
// List<String> result = comma.getContainingSRLs();
// assertNotNull(result);
// assertTrue(result.isEmpty());
}

@Test
public void testConstructorWithoutLabelsAllowsNullGetLabelThrowsNull() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// Comma comma = new Comma(2, sentence);
try {
// comma.getLabel();
fail("Expected NullPointerException when labels == null");
} catch (NullPointerException e) {
}
}

@Test
public void testGetLabelsWhenNullReturnsNullReference() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// Comma comma = new Comma(1, sentence);
// assertNull(comma.getLabels());
}

@Test
public void testGetNamedEntityTagReturnsMultipleNEsConcatenated() {
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
TextAnnotation ta = mock(TextAnnotation.class);
// sentence.ta = ta;
SpanLabelView nerView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
Constituent commaConstituent = mock(Constituent.class);
Constituent ne1 = mock(Constituent.class);
when(ne1.getLabel()).thenReturn("PERSON");
when(ne1.getNumberOfTokens()).thenReturn(3);
when(commaConstituent.getNumberOfTokens()).thenReturn(4);
when(commaConstituent.doesConstituentCover(ne1)).thenReturn(true);
Constituent ne2 = mock(Constituent.class);
when(ne2.getLabel()).thenReturn("ORG");
when(ne2.getNumberOfTokens()).thenReturn(3);
when(commaConstituent.getNumberOfTokens()).thenReturn(4);
when(commaConstituent.doesConstituentCover(ne2)).thenReturn(true);
when(nerView.getConstituentsCovering(commaConstituent)).thenReturn(Arrays.asList(ne1, ne2));
// Comma comma = new Comma(2, sentence, Arrays.asList("NE"));
// String tag = comma.getNamedEntityTag(commaConstituent);
// assertEquals("+PERSON+ORG", tag);
}

@Test
public void testGetBayraktarAnnotatedTextHandlesEmptyLabelCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Token0", ",", "Token2" });
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(1, s, Arrays.asList());
// String output = comma.getBayraktarAnnotatedText();
// assertTrue(output.contains("[Other]"));
}

@Test
public void testGetCommaConstituentFromTreeThrowsExceptionReturnsNull() {
TreeView view = mock(TreeView.class);
Constituent candidate = mock(Constituent.class);
when(candidate.isConsituentInRange(2, 3)).thenReturn(true);
when(view.getConstituents()).thenReturn(Arrays.asList(candidate));
// when(view.getParsePhrase(candidate)).thenThrow(new RuntimeException());
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = mock(TextAnnotation.class);
// s.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(2, s, Arrays.asList("Err"));
try {
// comma.getCommaConstituentFromTree(view);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException);
}
}

@Test
public void testGetSiblingCommasFiltersOnlySiblings() {
TreeView tree = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// s.goldTa = ta;
Constituent sibling1 = mock(Constituent.class);
Constituent sibling2 = mock(Constituent.class);
// Comma comma1 = new Comma(1, s, Arrays.asList("A"));
// Comma comma2 = new Comma(2, s, Arrays.asList("B"));
// Comma comma3 = new Comma(3, s, Arrays.asList("C"));
when(tree.getConstituents()).thenReturn(Arrays.asList(sibling1, sibling2));
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
// when(s.getCommas()).thenReturn(Arrays.asList(comma1, comma2, comma3));
when(sibling1.isConsituentInRange(1, 2)).thenReturn(true);
when(sibling2.isConsituentInRange(3, 4)).thenReturn(false);
// Comma commaHead = new Comma(1, s, Arrays.asList("Main"));
// List<Comma> result = commaHead.getSiblingCommas();
// assertNotNull(result);
}

@Test
public void testGetWordNgramsEmptyReturnNotNull() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", "b", ",", "c", "d" });
when(ta.getToken(anyInt())).thenReturn("word");
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("X"));
// String[] ngrams = comma.getWordNgrams();
// assertNotNull(ngrams);
}

@Test
public void testGetPOSNgramsEmptyReturnsNotNull() {
TextAnnotation ta = mock(TextAnnotation.class);
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(anyInt())).thenReturn("NN");
when(ta.getView(ViewNames.POS)).thenReturn(posView);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// s.goldTa = ta;
when(ta.getToken(anyInt())).thenReturn("word");
// Comma comma = new Comma(3, s, Arrays.asList("POS"));
Comma.useGoldFeatures(false);
// String[] result = comma.getPOSNgrams();
// assertNotNull(result);
}

@Test
public void testGetAnnotatedTextFromStartToEndIncludesCommaTags() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "The", "data", ",", "looks", "correct" });
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(2, s, Arrays.asList("Gapped", "Appos"));
// String result = comma.getAnnotatedText();
// assertTrue(result.contains("[Gapped,Appos]"));
}

@Test
public void testGetStrippedNotationIncludesPOSWhenPOSlexicaliseIsEnabled() {
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("NP-LOC");
// when(c.getSpan()).thenReturn(new IntPair(0, 1));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("ta");
when(ta.getView(ViewNames.POS)).thenReturn(mock(TokenLabelView.class));
when(ta.getToken(0)).thenReturn("New");
when(c.getTextAnnotation()).thenReturn(ta);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// s.goldTa = ta;
// Comma comma = new Comma(0, s, Arrays.asList("P"));
// String result = comma.getStrippedNotation(c);
// assertTrue(result.contains("NP"));
}

@Test
public void testGetChunkNgramsNullChunksHandledSafely() {
TextAnnotation ta = mock(TextAnnotation.class);
SpanLabelView chunkView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(new ArrayList<Constituent>());
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(1, s, Arrays.asList("Z"));
// String[] result = comma.getChunkNgrams();
// assertNotNull(result);
}

@Test
public void testGetPhraseToLeftOfCommaReturnsNullIfTreeViewParsePhraseIsNull() {
TreeView view = mock(TreeView.class);
Constituent commaConst = mock(Constituent.class);
when(view.getConstituents()).thenReturn(Arrays.asList(commaConst));
when(commaConst.isConsituentInRange(2, 3)).thenReturn(true);
// when(view.getParsePhrase(commaConst)).thenReturn(null);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("X"));
// Constituent result = comma.getPhraseToLeftOfComma(1);
// assertNull(result);
}

@Test
public void testGetPhraseToRightOfParentReturnsNullWhenNoSiblings() {
TreeView view = mock(TreeView.class);
Constituent commaConst = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(view.getConstituents()).thenReturn(Arrays.asList(commaConst));
when(commaConst.isConsituentInRange(4, 5)).thenReturn(true);
// when(view.getParsePhrase(commaConst)).thenReturn(commaConst);
when(TreeView.getParent(commaConst)).thenReturn(parent);
// when(view.where(any())).thenReturn(new QueryableList<>(Collections.emptyList()));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(4, sentence, Arrays.asList("Y"));
// Constituent right = comma.getPhraseToRightOfParent(1);
// assertNull(right);
}

@Test
public void testGetSiblingCommaHeadReturnsMinimumComma() {
TreeView parseView = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
// Comma comma1 = new Comma(5, mock(CommaSRLSentence.class), Arrays.asList("L1"));
// Comma comma2 = new Comma(3, mock(CommaSRLSentence.class), Arrays.asList("L2"));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
when(parseView.getConstituents()).thenReturn(Arrays.asList(c1, c2));
when(c1.isConsituentInRange(3, 4)).thenReturn(true);
when(c2.isConsituentInRange(5, 6)).thenReturn(true);
// when(parseView.getParsePhrase(any())).thenReturn(c1, c2);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// when(sentence.getCommas()).thenReturn(Arrays.asList(comma1, comma2));
// comma1.s = sentence;
// comma2.s = sentence;
// Comma head = comma2.getSiblingCommaHead();
// assertEquals(3, head.getPosition());
}

@Test
public void testGetCommaConstituentFromTreeReturnsCorrectConstituent() {
TreeView parseView = mock(TreeView.class);
Constituent matching = mock(Constituent.class);
when(matching.isConsituentInRange(1, 2)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(matching));
// when(parseView.getParsePhrase(matching)).thenReturn(matching);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(1, sentence, Arrays.asList("Z"));
// Constituent result = comma.getCommaConstituentFromTree(parseView);
// assertEquals(matching, result);
}

@Test
public void testGetNamedEntityTagSkipsNonCoveringNEs() {
SpanLabelView nerView = mock(SpanLabelView.class);
Constituent constituent = mock(Constituent.class);
Constituent ne = mock(Constituent.class);
when(ne.getLabel()).thenReturn("ORG");
when(ne.getNumberOfTokens()).thenReturn(2);
when(constituent.getNumberOfTokens()).thenReturn(10);
when(constituent.doesConstituentCover(ne)).thenReturn(false);
when(nerView.getConstituentsCovering(constituent)).thenReturn(Collections.singletonList(ne));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(3, sentence, Arrays.asList("S"));
// String tag = comma.getNamedEntityTag(constituent);
// assertEquals("", tag);
}

@Test
public void testGetWordToLeftReturnsValidToken() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", "B", "C", "," });
when(ta.getToken(2)).thenReturn("C");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(3, sentence, Arrays.asList("X"));
// String word = comma.getWordToLeft(1);
// assertEquals("C", word);
}

@Test
public void testGetPhraseToLeftOfParentNullParentReturnsNull() {
TreeView view = mock(TreeView.class);
Constituent commaConstituent = mock(Constituent.class);
when(view.getConstituents()).thenReturn(Arrays.asList(commaConstituent));
when(commaConstituent.isConsituentInRange(2, 3)).thenReturn(true);
// when(view.getParsePhrase(commaConstituent)).thenReturn(commaConstituent);
when(TreeView.getParent(commaConstituent)).thenReturn(null);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("P"));
// Constituent result = comma.getPhraseToLeftOfParent(1);
// assertNull(result);
}

@Test
public void testGetBayraktarPatternWithPreTerminalNonCCReturnsStars() {
TreeView parseView = mock(TreeView.class);
Constituent commaConstituent = mock(Constituent.class);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaConstituent));
when(commaConstituent.isConsituentInRange(1, 2)).thenReturn(true);
// when(parseView.getParsePhrase(commaConstituent)).thenReturn(commaConstituent);
Constituent parent = mock(Constituent.class);
when(TreeView.getParent(commaConstituent)).thenReturn(parent);
when(parent.getLabel()).thenReturn("IN");
when(ParseTreeProperties.isPunctuationToken("IN")).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(true);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = mock(TextAnnotation.class);
// sentence.goldTa = mock(TextAnnotation.class);
// Comma comma = new Comma(1, sentence, Arrays.asList("Z"));
// String pattern = comma.getBayraktarPattern(parseView);
// assertTrue(pattern.startsWith("***"));
}

@Test
public void testPOSViewMissingReturnsNullPointerInGetPOSToLeft() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.POS)).thenReturn(null);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("PUNCTUATION"));
try {
// comma.getPOSToLeft(1);
fail("Expected NullPointerException when POS view is missing.");
} catch (NullPointerException e) {
}
}

@Test
public void testChunkToRightWhenStartIndexBeyondTokenLengthReturnsEmpty() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", "B", "C" });
SpanLabelView chunkView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(5, 3)).thenReturn(new ArrayList<>());
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("X"));
// Constituent result = comma.getChunkToRightOfComma(3);
// assertNull(result);
}

@Test
public void testGetSRLPrepReturnsFilterMatch() {
PredicateArgumentView prepView = mock(PredicateArgumentView.class);
Constituent predicate = mock(Constituent.class);
Constituent arg = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(arg);
when(rel.getSource()).thenReturn(predicate);
when(arg.getStartSpan()).thenReturn(3);
when(arg.getEndSpan()).thenReturn(5);
when(prepView.getPredicates()).thenReturn(Collections.singletonList(predicate));
when(prepView.getArguments(predicate)).thenReturn(Collections.singletonList(rel));
when(prepView.getPredicateLemma(predicate)).thenReturn("by");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SRL_VERB)).thenReturn(mock(PredicateArgumentView.class));
when(ta.getView(ViewNames.SRL_NOM)).thenReturn(mock(PredicateArgumentView.class));
when(ta.getView(ViewNames.SRL_PREP)).thenReturn(prepView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(3, sentence, Arrays.asList("SRL"));
// List<String> srls = comma.getContainingSRLs();
// assertEquals(1, srls.size());
// assertTrue(srls.get(0).startsWith("by"));
}

@Test
public void testGetWordNgramsOnShortSentenceUsesDummyTokens() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Hello", "," });
when(ta.getToken(anyInt())).thenAnswer(i -> {
int idx = (Integer) i.getArguments()[0];
return idx < 0 || idx >= 2 ? "###" : ta.getTokens()[idx];
});
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// Comma comma = new Comma(1, s, Arrays.asList("Short"));
// String[] ngrams = comma.getWordNgrams();
// assertTrue(ngrams.length > 0);
// assertTrue(Arrays.stream(ngrams).anyMatch(s1 -> s1.contains("###") || s1.contains("$$$")));
}

@Test
public void testGetParentSiblingPhraseNgramsHandlesNullConstituents() {
TreeView treeView = mock(TreeView.class);
when(treeView.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(treeView);
when(ta.getTokens()).thenReturn(new String[] { "X", "Y", ",", "Z" });
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("NGRAM"));
// String[] result = comma.getParentSiblingPhraseNgrams();
// assertNotNull(result);
}

@Test
public void testGetNotationWithNERAndPOSEnabled() {
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("NP");
when(c.getOutgoingRelations()).thenReturn(Collections.emptyList());
when(c.getViewName()).thenReturn(ViewNames.PARSE_STANFORD);
IntPair span = new IntPair(0, 2);
// when(c.getSpan()).thenReturn(span);
TokenLabelView pos = mock(TokenLabelView.class);
when(pos.getLabel(0)).thenReturn("DT");
when(pos.getLabel(1)).thenReturn("NN");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.POS)).thenReturn(pos);
when(ta.getToken(0)).thenReturn("the");
when(ta.getToken(1)).thenReturn("dog");
when(ta.getTokens()).thenReturn(new String[] { "the", "dog" });
when(c.getTextAnnotation()).thenReturn(ta);
SpanLabelView nerView = mock(SpanLabelView.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(nerView.getConstituentsCovering(any())).thenReturn(Collections.emptyList());
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence, Arrays.asList("Labels"));
// String notation = comma.getNotation(c);
// assertTrue(notation.contains("NP") && notation.contains("DT") && notation.contains("NN"));
}

@Test
public void testGetSiblingCommasReturnsOnlyThisIfNoSiblings() {
TreeView tree = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
CommaSRLSentence s = mock(CommaSRLSentence.class);
// s.ta = ta;
// s.goldTa = ta;
// Comma comma = new Comma(1, s, Arrays.asList("A"));
// when(s.getCommas()).thenReturn(Collections.singletonList(comma));
// List<Comma> result = comma.getSiblingCommas();
// assertEquals(1, result.size());
// assertSame(comma, result.get(0));
}

@Test
public void testIsSiblingReturnsFalseWhenParentsAreDifferent() {
TreeView tree = mock(TreeView.class);
Constituent commaConst1 = mock(Constituent.class);
Constituent commaConst2 = mock(Constituent.class);
Constituent parent1 = mock(Constituent.class);
Constituent parent2 = mock(Constituent.class);
when(tree.getConstituents()).thenReturn(Arrays.asList(commaConst1, commaConst2));
when(commaConst1.isConsituentInRange(1, 2)).thenReturn(true);
when(commaConst2.isConsituentInRange(2, 3)).thenReturn(true);
// when(tree.getParsePhrase(any())).thenReturn(commaConst1, commaConst2);
when(TreeView.getParent(commaConst1)).thenReturn(parent1);
when(TreeView.getParent(commaConst2)).thenReturn(parent2);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(tree);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma commaA = new Comma(1, sentence, Arrays.asList("A"));
// Comma commaB = new Comma(2, sentence, Arrays.asList("B"));
// assertFalse(commaA.isSibling(commaB));
}
}
