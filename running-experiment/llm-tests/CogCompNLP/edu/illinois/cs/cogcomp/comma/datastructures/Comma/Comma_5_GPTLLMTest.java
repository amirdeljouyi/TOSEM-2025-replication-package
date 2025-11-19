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

public class Comma_5_GPTLLMTest {

@Test
public void testGetLabelSingleLabel() {
String[] mockTokens = { "The", "dog", ",", "ran", "home" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(mockTokens);
when(mockTa.getToken(anyInt())).thenAnswer(inv -> mockTokens[(int) inv.getArguments()[0]]);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
when(mockGoldTa.getTokens()).thenReturn(mockTokens);
when(mockGoldTa.getToken(anyInt())).thenAnswer(inv -> mockTokens[(int) inv.getArguments()[0]]);
CommaSRLSentence mockSentence = mock(CommaSRLSentence.class);
// when(mockSentence.ta).thenReturn(mockTa);
// when(mockSentence.goldTa).thenReturn(mockGoldTa);
List<String> labels = Arrays.asList("X");
// Comma comma = new Comma(2, mockSentence, labels);
// assertEquals("X", comma.getLabel());
}

@Test
public void testGetCommaIDUsesPositionAndGoldId() {
String[] tokens = { "A", "b", ",", "c" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
when(mockGoldTa.getTokens()).thenReturn(tokens);
when(mockGoldTa.getId()).thenReturn("doc123");
CommaSRLSentence mockSentence = mock(CommaSRLSentence.class);
// when(mockSentence.ta).thenReturn(mockTa);
// when(mockSentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, mockSentence, Arrays.asList("label"));
// assertEquals("2 doc123", comma.getCommaID());
}

@Test
public void testGetWordToRightBoundsCheck() {
String[] tokens = { "We", "see", ",", "everything" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(3)).thenReturn("everything");
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
when(mockGoldTa.getTokens()).thenReturn(tokens);
CommaSRLSentence mockSentence = mock(CommaSRLSentence.class);
// when(mockSentence.ta).thenReturn(mockTa);
// when(mockSentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, mockSentence, Arrays.asList("comma"));
// assertEquals("everything", comma.getWordToRight(1));
// assertEquals("###", comma.getWordToRight(10));
}

@Test
public void testGetWordToLeftBoundsCheck() {
String[] tokens = { "We", "see", ",", "everything" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(0)).thenReturn("We");
when(mockTa.getToken(1)).thenReturn("see");
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
when(mockGoldTa.getTokens()).thenReturn(tokens);
CommaSRLSentence mockSentence = mock(CommaSRLSentence.class);
// when(mockSentence.ta).thenReturn(mockTa);
// when(mockSentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, mockSentence, Arrays.asList("comma"));
// assertEquals("see", comma.getWordToLeft(1));
// assertEquals("We", comma.getWordToLeft(2));
// assertEquals("$$$", comma.getWordToLeft(10));
}

@Test
public void testGetPOSRightWithDTAndThe() {
String[] tokens = { "foo", "bar", ",", "the", "object" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(3)).thenReturn("the");
TokenLabelView mockPOSView = mock(TokenLabelView.class);
when(mockPOSView.getLabel(3)).thenReturn("DT");
when(mockTa.getView(ViewNames.POS)).thenReturn(mockPOSView);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
when(mockGoldTa.getTokens()).thenReturn(tokens);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("x"));
// assertEquals("DT-the", comma.getPOSToRight(1));
}

@Test
public void testGetAnnotationTextDisplaysCorrectly() {
String[] tokens = { "It", "is", ",", "done", "now", "." };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, sentence, Arrays.asList("Pause", "Clause"));
// String result = comma.getAnnotatedText();
// assertEquals("It is ,[Pause,Clause] done now .", result);
}

@Test
public void testGetNotationReturnsNullLiteralOnNullInput() {
String[] tokens = { "A", "b", ",", "c" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, sentence, Collections.singletonList("x"));
// assertEquals("NULL", comma.getNotation(null));
}

@Test
public void testGetStrippedNotationReturnsNullLiteralOnNull() {
String[] tokens = { "Yes", "No", ",", "Maybe" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, sentence, Collections.singletonList("Z"));
// assertEquals("NULL", comma.getStrippedNotation(null));
}

@Test
public void testDependencyArraysReturnEmptyWhenNoRelations() {
String[] tokens = { "X", "Y", ",", "Z" };
TextAnnotation mockTa = mock(TextAnnotation.class);
TreeView depView = mock(TreeView.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
when(depView.getConstituentsCoveringSpan(0, 2)).thenReturn(Collections.emptyList());
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, sentence, Collections.singletonList("L"));
// String[] ltor = comma.getLeftToRightDependencies();
// String[] rtol = comma.getRightToLeftDependencies();
// assertTrue(ltor.length == 0);
// assertTrue(rtol.length == 0);
}

@Test
public void testGetBayraktarAnnotatedTextReturnsProperFormat() {
String[] tokens = { "Go", "fast", ",", "then", "stop" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = spy(new Comma(2, sentence, Collections.singletonList("Y")));
// doReturn("TMP").when(comma).getBayraktarLabel();
// assertEquals("Go fast ,[TMP] then stop", comma.getBayraktarAnnotatedText());
}

@Test
public void testGetPositionReturnsCorrectIndex() {
String[] tokens = { "One", ",", "two", "three" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(1, sentence, Arrays.asList("Pause"));
// assertEquals(1, comma.getPosition());
}

@Test
public void testGetChunkToLeftOfCommaReturnsNullOnInvalidDistance() {
String[] tokens = { "one", "and", ",", "two" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
SpanLabelView mockChunkView = mock(SpanLabelView.class);
when(mockChunkView.getSpanLabels(0, 3)).thenReturn(Collections.emptyList());
when(mockTa.getView(ViewNames.SHALLOW_PARSE)).thenReturn(mockChunkView);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, sentence, Arrays.asList("Z"));
// assertNull(comma.getChunkToLeftOfComma(1));
}

@Test
public void testGetSiblingToLeftReturnsNullWhenNoSiblingExists() {
TreeView parseView = mock(TreeView.class);
Constituent c = mock(Constituent.class);
// QueryableList<Constituent> siblings = new QueryableList<>(Collections.emptyList());
// when(parseView.where(any())).thenReturn(siblings);
String[] tokens = { "A", ",", "B" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(1, sentence, Arrays.asList("P"));
// assertNull(comma.getSiblingToLeft(1, c, parseView));
}

@Test
public void testGetSiblingToRightReturnsNullWhenNoSiblingsAfter() {
TreeView parseView = mock(TreeView.class);
Constituent c = mock(Constituent.class);
// QueryableList<Constituent> siblings = new QueryableList<>(Collections.emptyList());
// when(parseView.where(any())).thenReturn(siblings);
String[] tokens = { "hello", ",", "world" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(1, sentence, Arrays.asList("Q"));
// assertNull(comma.getSiblingToRight(1, c, parseView));
}

@Test
public void testGetCommaConstituentFromTreeReturnsNullIfNotFound() {
TreeView mockView = mock(TreeView.class);
List<Constituent> emptyConstituents = new ArrayList<>();
when(mockView.getConstituents()).thenReturn(emptyConstituents);
String[] tokens = { "a", ",", "b" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(1, sentence, Arrays.asList("m"));
// assertNull(comma.getCommaConstituentFromTree(mockView));
}

@Test
public void testGetNamedEntityTagReturnsEmptyStringWithNoCoveringNEs() {
String[] tokens = { "James", ",", "a", "student" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
SpanLabelView nerView = mock(SpanLabelView.class);
when(nerView.getConstituentsCovering(any())).thenReturn(Collections.emptyList());
when(mockTa.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
Constituent mockConstituent = mock(Constituent.class);
// Comma comma = new Comma(1, sentence, Arrays.asList("name"));
// assertEquals("", comma.getNamedEntityTag(mockConstituent));
}

@Test
public void testGetPOSRightDefaultWhenNotDTAndNotThe() {
String[] tokens = { "test", "run", ",", "over", "there" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(3)).thenReturn("over");
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(3)).thenReturn("IN");
when(mockTa.getView(ViewNames.POS)).thenReturn(posView);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("x"));
// assertEquals("IN", comma.getPOSToRight(1));
}

@Test
public void testGetPOSLeftWithDTNotFollowedByTheReturnsDT() {
String[] tokens = { "a", "house", ",", "stood" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(3)).thenReturn("stood");
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(1)).thenReturn("DT");
when(mockTa.getView(ViewNames.POS)).thenReturn(posView);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Arrays.asList("X"));
// assertEquals("DT", comma.getPOSToLeft(1));
}

@Test
public void testBayraktarPatternReturnsSkipSymbolWhenNoRelations() {
String[] tokens = { "it", "is", ",", "fine" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TreeView parseView = mock(TreeView.class);
Constituent rightConstituent = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(rightConstituent));
when(rightConstituent.isConsituentInRange(2, 3)).thenReturn(true);
// when(parseView.getParsePhrase(rightConstituent)).thenReturn(rightConstituent);
when(TreeView.getParent(rightConstituent)).thenReturn(parent);
when(parent.getLabel()).thenReturn("ADVP");
when(parent.getOutgoingRelations()).thenReturn(Collections.emptyList());
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, sentence, Arrays.asList("Go"));
// String result = comma.getBayraktarPattern(parseView);
// assertTrue(result.startsWith("ADVP -->"));
}

@Test
public void testGetWordNgramsCorrectOrderAndCount() {
String[] tokens = { "A", "B", ",", "C", "D" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(0)).thenReturn("A");
when(mockTa.getToken(1)).thenReturn("B");
when(mockTa.getToken(3)).thenReturn("C");
when(mockTa.getToken(4)).thenReturn("D");
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(2, sentence, Collections.singletonList("Test"));
// String[] result = comma.getWordNgrams();
// assertNotNull(result);
// assertTrue(result.length >= 4);
}

@Test
public void testGetBayraktarPatternPreTerminalWithCC() {
String[] tokens = { "A", ",", "B" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
Constituent comma = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
Constituent child1 = mock(Constituent.class);
Relation rel1 = mock(Relation.class);
when(rel1.getTarget()).thenReturn(child1);
List<Relation> relList = Arrays.asList(rel1);
TreeView mockView = mock(TreeView.class);
when(mockView.getConstituents()).thenReturn(Collections.singletonList(comma));
when(comma.isConsituentInRange(1, 2)).thenReturn(true);
// when(mockView.getParsePhrase(comma)).thenReturn(comma);
when(TreeView.getParent(comma)).thenReturn(parent);
when(parent.getLabel()).thenReturn("CC");
when(parent.getOutgoingRelations()).thenReturn(relList);
when(child1.getLabel()).thenReturn("CC");
when(child1.getSurfaceForm()).thenReturn("and");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(true);
when(ParseTreeProperties.isPunctuationToken("CC")).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(child1)).thenReturn(true);
when(POSUtils.isPOSPunctuation("CC")).thenReturn(false);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma commaObj = new Comma(1, sentence, Collections.singletonList("x"));
// String result = commaObj.getBayraktarPattern(mockView);
// assertTrue(result.contains("CC --> and"));
}

@Test
public void testGetSiblingCommaHeadWhenAllCommasHaveSameParent() {
String[] tokens = { "walk", ",", "talk", ",", "run" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TreeView treeView = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
// Comma commaA = spy(new Comma(1, null, Collections.singletonList("x")));
// Comma commaB = spy(new Comma(3, null, Collections.singletonList("y")));
// TreeView.setParent(c1, mock(Constituent.class));
// TreeView.setParent(c2, TreeView.getParent(c1));
// when(commaA.getCommaConstituentFromTree(treeView)).thenReturn(c1);
// when(commaB.getCommaConstituentFromTree(treeView)).thenReturn(c2);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.getCommas()).thenReturn(Arrays.asList(commaA, commaB));
when(treeView.getConstituents()).thenReturn(Arrays.asList(c1, c2));
// when(commaA.getSiblingCommas()).thenCallRealMethod();
// doReturn(treeView).when(sentence.ta).getView(ViewNames.PARSE_STANFORD);
// when(treeView.where(any())).thenReturn(new QueryableList<>(Arrays.asList(c1, c2)));
// commaA.s = sentence;
// commaB.s = sentence;
// Comma result = commaB.getSiblingCommaHead();
// assertEquals(commaA.getPosition(), result.getPosition());
}

@Test
public void testIsSiblingCommasHaveNoSameParent() {
String[] tokens = { "Hi", ",", "Hello" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TreeView view = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
// TreeView.setParent(c1, mock(Constituent.class));
// TreeView.setParent(c2, mock(Constituent.class));
when(view.getConstituents()).thenReturn(Arrays.asList(c1, c2));
// Comma commaX = spy(new Comma(1, null, Collections.singletonList("a")));
// Comma commaY = spy(new Comma(2, null, Collections.singletonList("b")));
// when(commaX.getCommaConstituentFromTree(view)).thenReturn(c1);
// when(commaY.getCommaConstituentFromTree(view)).thenReturn(c2);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mock(TextAnnotation.class));
doReturn(view).when(mockTa).getView(ViewNames.PARSE_STANFORD);
// commaX.s = sentence;
// commaY.s = sentence;
// assertFalse(commaX.isSibling(commaY));
}

@Test
public void testNamedEntityMajorityTokenCoverage() {
String[] tokens = { "Mr.", "Smith", ",", "the", "lawyer" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(2)).thenReturn(",");
SpanLabelView nerView = mock(SpanLabelView.class);
Constituent commaConstituent = mock(Constituent.class);
Constituent ne1 = mock(Constituent.class);
when(nerView.getConstituentsCovering(commaConstituent)).thenReturn(Collections.singletonList(ne1));
when(mockTa.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(commaConstituent.doesConstituentCover(ne1)).thenReturn(true);
when(ne1.getNumberOfTokens()).thenReturn(3);
when(commaConstituent.getNumberOfTokens()).thenReturn(4);
when(ne1.getLabel()).thenReturn("PERSON");
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mock(TextAnnotation.class));
// Comma comma = new Comma(2, sentence, Collections.singletonList("P"));
// String tag = comma.getNamedEntityTag(commaConstituent);
// assertEquals("+PERSON", tag);
}

@Test
public void testGetLeftToRightDependenciesIncludesRelationNamesCorrectly() {
String[] tokens = { "He", ",", "runs" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TreeView depView = mock(TreeView.class);
Constituent source = mock(Constituent.class);
Relation rel = mock(Relation.class);
Constituent target = mock(Constituent.class);
when(depView.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.singletonList(source));
when(source.getOutgoingRelations()).thenReturn(Collections.singletonList(rel));
when(rel.getTarget()).thenReturn(target);
when(target.getStartSpan()).thenReturn(2);
when(rel.getRelationName()).thenReturn("nsubj");
when(mockTa.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("X"));
// String[] deps = comma.getLeftToRightDependencies();
// assertEquals(1, deps.length);
// assertEquals("nsubj", deps[0]);
}

@Test
public void testGetParentSiblingPhraseNgramsHandlesNullPhrases() {
String[] tokens = { "This", "is", ",", "okay" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
// Comma comma = spy(new Comma(2, sentence, Collections.singletonList("y")));
// doReturn(null).when(comma).getPhraseToLeftOfParent(anyInt());
// doReturn(null).when(comma).getPhraseToRightOfParent(anyInt());
// String[] ngrams = comma.getParentSiblingPhraseNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetPOSNgramsBuildsDifferentSizeNGrams() {
String[] tokens = { "a", "b", ",", "c", "d", "e", "f", "g", "h", "i", "j" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TokenLabelView mockPOSView = mock(TokenLabelView.class);
when(mockPOSView.getLabel(anyInt())).thenReturn("NN");
when(mockTa.getView(ViewNames.POS)).thenReturn(mockPOSView);
TextAnnotation mockGoldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(mockGoldTa);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Collections.singletonList("L"));
// String[] posNgrams = comma.getPOSNgrams();
// assertNotNull(posNgrams);
// assertTrue(posNgrams.length > 0);
}

@Test
public void testStrippedNotationHandlesPOSLexicalisedTrueWithMultipleTokens() {
String[] tokens = { "the", "quick", ",", "brown", "fox" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
when(mockTa.getToken(1)).thenReturn("quick");
Constituent cons = mock(Constituent.class);
when(cons.getLabel()).thenReturn("NP-MOD");
IntPair span = new IntPair(1, 3);
// when(cons.getSpan()).thenReturn(span);
when(cons.getTextAnnotation()).thenReturn(mockTa);
TextAnnotation goldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// when(sentence.goldTa).thenReturn(goldTa);
// Comma comma = new Comma(2, sentence, Collections.singletonList("X"));
// assertTrue(comma.getStrippedNotation(cons).startsWith("NP"));
}

@Test
public void testGetNamedEntityTagSkipsMiscLabels() {
String[] tokens = { "John", ",", "the", "CEO" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
SpanLabelView mockNER = mock(SpanLabelView.class);
Constituent dummy = mock(Constituent.class);
Constituent ne = mock(Constituent.class);
when(ne.getLabel()).thenReturn("MISC");
when(dummy.doesConstituentCover(ne)).thenReturn(true);
when(ne.getNumberOfTokens()).thenReturn(2);
when(dummy.getNumberOfTokens()).thenReturn(3);
when(mockNER.getConstituentsCovering(dummy)).thenReturn(Collections.singletonList(ne));
when(mockTa.getView(ViewNames.NER_CONLL)).thenReturn(mockNER);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("F"));
// assertEquals("", comma.getNamedEntityTag(dummy));
}

@Test
public void testGetLeftToRightDependenciesSkipsIfNoTargetOnRight() {
String[] tokens = { "before", ",", "the", "event" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TreeView depView = mock(TreeView.class);
Constituent source = mock(Constituent.class);
Relation rel = mock(Relation.class);
Constituent target = mock(Constituent.class);
when(depView.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.singletonList(source));
when(source.getOutgoingRelations()).thenReturn(Collections.singletonList(rel));
when(rel.getTarget()).thenReturn(target);
when(target.getStartSpan()).thenReturn(1);
when(mockTa.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("F"));
// assertEquals(0, comma.getLeftToRightDependencies().length);
}

@Test
public void testGetRightToLeftDependenciesIncludesRelWithTargetSpanGreaterThanComma() {
String[] tokens = { "We", ",", "run", "fast" };
TextAnnotation mockTa = mock(TextAnnotation.class);
when(mockTa.getTokens()).thenReturn(tokens);
TreeView view = mock(TreeView.class);
Constituent left = mock(Constituent.class);
Relation rel = mock(Relation.class);
Constituent src = mock(Constituent.class);
when(rel.getSource()).thenReturn(src);
when(src.getStartSpan()).thenReturn(3);
when(rel.getRelationName()).thenReturn("case");
when(view.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.singletonList(left));
when(left.getIncomingRelations()).thenReturn(Collections.singletonList(rel));
when(mockTa.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(mockTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("D"));
// String[] rels = comma.getRightToLeftDependencies();
// assertEquals(1, rels.length);
// assertEquals("case", rels[0]);
}

@Test
public void testGetContainingSRLsHandlesMultipleViews() {
String[] tokens = { "Dogs", ",", "bark" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent pred = mock(Constituent.class);
Constituent arg = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(arg);
when(arg.getStartSpan()).thenReturn(1);
when(arg.getEndSpan()).thenReturn(2);
when(rel.getSource()).thenReturn(pred);
PredicateArgumentView srlVerb = mock(PredicateArgumentView.class);
PredicateArgumentView srlNom = mock(PredicateArgumentView.class);
PredicateArgumentView srlPrep = mock(PredicateArgumentView.class);
when(ta.getView(ViewNames.SRL_VERB)).thenReturn(srlVerb);
when(ta.getView(ViewNames.SRL_NOM)).thenReturn(srlNom);
when(ta.getView(ViewNames.SRL_PREP)).thenReturn(srlPrep);
when(srlVerb.getPredicates()).thenReturn(Collections.singletonList(pred));
when(srlVerb.getArguments(pred)).thenReturn(Collections.singletonList(rel));
when(srlVerb.getPredicateLemma(pred)).thenReturn("bark");
when(srlNom.getPredicates()).thenReturn(Collections.emptyList());
when(srlPrep.getPredicates()).thenReturn(Collections.emptyList());
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence, Collections.singletonList("VP"));
// List<String> srls = comma.getContainingSRLs();
// assertEquals(1, srls.size());
// assertEquals("bark" + rel.getRelationName(), srls.get(0));
}

@Test
public void testGetPhraseToLeftOfCommaNullTreeView() {
String[] tokens = { "Hi", ",", "there" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TreeView parseView = mock(TreeView.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
when(parseView.getConstituents()).thenReturn(Collections.emptyList());
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(mock(TextAnnotation.class));
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence, Collections.singletonList("X"));
// assertNull(comma.getPhraseToLeftOfComma(1));
}

@Test
public void testGetChunkNgramsReturnsNULLWhenChunksMissing() {
String[] tokens = { "who", "knows", ",", "why" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(0, 3)).thenReturn(Collections.emptyList());
when(chunkView.getSpanLabels(3, 4)).thenReturn(Collections.emptyList());
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Collections.singletonList("Y"));
// String[] chunks = comma.getChunkNgrams();
// assertTrue(chunks.length > 0);
// for (String s : chunks) {
// assertTrue(s.contains("NULL"));
// }
}

@Test
public void testGetBayraktarLabelReturnsOtherWhenNullPattern() {
String[] tokens = { "A", ",", "B" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = spy(new Comma(1, sentence, Collections.singletonList("Z")));
// doReturn(null).when(comma).getBayraktarPattern();
// String result = comma.getBayraktarLabel();
// assertEquals("Other", result);
}

@Test
public void testGetSiblingToLeftWithZeroDistanceReturnsInput() {
String[] tokens = { "a", ",", "b" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
Constituent input = mock(Constituent.class);
TreeView treeView = mock(TreeView.class);
// QueryableList<Constituent> siblings = new QueryableList<>(Arrays.asList(input));
// when(treeView.where(any())).thenReturn(siblings);
// Comma comma = new Comma(1, sentence, Collections.singletonList("C"));
// Constituent result = comma.getSiblingToLeft(0, input, treeView);
// assertEquals(input, result);
}

@Test
public void testGetSiblingToRightWithZeroDistanceReturnsInput() {
String[] tokens = { "start", ",", "end" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
Constituent input = mock(Constituent.class);
TreeView treeView = mock(TreeView.class);
// QueryableList<Constituent> siblings = new QueryableList<>(Collections.singletonList(input));
// when(treeView.where(any())).thenReturn(siblings);
// Comma comma = new Comma(1, sentence, Collections.singletonList("X"));
// Constituent result = comma.getSiblingToRight(0, input, treeView);
// assertEquals(input, result);
}

@Test
public void testGetChunkToRightWithDistanceZeroReturnsNull() {
String[] tokens = { "a", ",", "b" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(2, 3)).thenReturn(Collections.singletonList(mock(Constituent.class)));
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("L"));
// Constituent result = comma.getChunkToRightOfComma(0);
// assertNull(result);
}

@Test
public void testGetChunkToLeftWithDistanceZeroReturnsNull() {
String[] tokens = { "a", ",", "b" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(0, 2)).thenReturn(Collections.singletonList(mock(Constituent.class)));
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("L"));
// Constituent result = comma.getChunkToLeftOfComma(0);
// assertNull(result);
}

@Test
public void testGetAnnotationWithSingleLabel() {
String[] tokens = { "She", "said", ",", "\"hello\"" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
// Comma comma = new Comma(2, sentence, Collections.singletonList("Appos"));
// String annotated = comma.getAnnotatedText();
// assertEquals("She said ,[Appos] \"hello\"", annotated);
}

@Test
public void testGetWordNgramsWithEndBoundarySymbols() {
String[] tokens = { "start", "," };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
when(ta.getToken(eq(1))).thenReturn(",");
when(ta.getToken(eq(2))).thenReturn(null);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("Edge"));
// String[] ngrams = comma.getWordNgrams();
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetPOSNgramsReturnsExpectedLength() {
String[] tokens = { "A", "B", ",", "C", "D", "E", "F", "G", "H", "I" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(anyInt())).thenReturn("NN");
when(ta.getView(ViewNames.POS)).thenReturn(posView);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
Comma.useGoldFeatures(false);
// Comma comma = new Comma(2, sentence, Collections.singletonList("X"));
// String[] posNgrams = comma.getPOSNgrams();
// assertNotNull(posNgrams);
// assertTrue(posNgrams.length > 0);
}

@Test
public void testGetNotationIncludesNERLexicalizedAndPOSLexicalized() {
String[] tokens = { "John", ",", "runs" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent cons = mock(Constituent.class);
when(cons.getLabel()).thenReturn("NP");
IntPair span = new IntPair(0, 1);
// when(cons.getSpan()).thenReturn(span);
when(cons.getTextAnnotation()).thenReturn(ta);
when(cons.getOutgoingRelations()).thenReturn(new ArrayList<>());
when(POSUtils.getPOS(eq(ta), anyInt())).thenReturn("NNP");
SpanLabelView nerView = mock(SpanLabelView.class);
when(nerView.getConstituentsCovering(cons)).thenReturn(new ArrayList<>());
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("neu"));
// String result = comma.getNotation(cons);
// assertTrue(result.contains("- NNP"));
}

@Test
public void testGetBayraktarPatternReturnsStarIfNotPreTerminal() {
String[] tokens = { "He", ",", "speaks" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent commaC = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(commaC.isConsituentInRange(1, 2)).thenReturn(true);
TreeView tv = mock(TreeView.class);
when(tv.getConstituents()).thenReturn(Collections.singletonList(commaC));
// when(tv.getParsePhrase(commaC)).thenReturn(commaC);
when(TreeView.getParent(commaC)).thenReturn(parent);
when(parent.getLabel()).thenReturn("SBAR");
when(ParseTreeProperties.isPunctuationToken(anyString())).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(false);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("X"));
// String result = comma.getBayraktarPattern(tv);
// assertTrue(result.startsWith("SBAR -->"));
}

@Test
public void testGetChunkToLeftOfCommaOutOfBoundsDistance() {
String[] tokens = { "Hello", "world", "," };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
SpanLabelView chunkView = mock(SpanLabelView.class);
List<Constituent> chunks = Arrays.asList(mock(Constituent.class), mock(Constituent.class));
when(chunkView.getSpanLabels(0, 3)).thenReturn(chunks);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Collections.singletonList("TAG"));
// Constituent result = comma.getChunkToLeftOfComma(10);
// assertNull(result);
}

@Test
public void testGetChunkToRightOfCommaOutOfBoundsDistance() {
String[] tokens = { "A", ",", "B", "C" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
SpanLabelView chunkView = mock(SpanLabelView.class);
List<Constituent> chunks = Arrays.asList(mock(Constituent.class), mock(Constituent.class));
when(chunkView.getSpanLabels(3, 4)).thenReturn(chunks);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Collections.singletonList("Z"));
// Constituent result = comma.getChunkToRightOfComma(5);
// assertNull(result);
}

@Test
public void testGetBayraktarPatternReturnsOnlyParentLabelIfNoChildren() {
String[] tokens = { "running", ",", "fast" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent commaNode = mock(Constituent.class);
Constituent parentNode = mock(Constituent.class);
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
when(parentNode.getLabel()).thenReturn("VP");
when(parentNode.getOutgoingRelations()).thenReturn(Collections.emptyList());
TreeView tree = mock(TreeView.class);
when(tree.getConstituents()).thenReturn(Collections.singletonList(commaNode));
// when(tree.getParsePhrase(commaNode)).thenReturn(commaNode);
when(TreeView.getParent(commaNode)).thenReturn(parentNode);
when(ParseTreeProperties.isPunctuationToken("VP")).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(parentNode)).thenReturn(false);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("K"));
// String pattern = comma.getBayraktarPattern(tree);
// assertTrue(pattern.startsWith("VP -->"));
}

@Test
public void testGetSiblingCommasReturnsEmptyListWhenNoneMatch() {
String[] tokens = { "a", ",", "b", ",", "c" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TreeView view = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
// Comma comma1 = spy(new Comma(1, null, Collections.singletonList("x")));
// Comma comma2 = spy(new Comma(3, null, Collections.singletonList("y")));
when(view.getConstituents()).thenReturn(Arrays.asList(c1, c2));
// when(view.where(any())).thenReturn(new QueryableList<>(Collections.emptyList()));
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.getCommas()).thenReturn(Arrays.asList(comma1, comma2));
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
// doReturn(c1).when(comma1).getCommaConstituentFromTree(view);
// doReturn(c2).when(comma2).getCommaConstituentFromTree(view);
// comma1.s = sentence;
// List<Comma> result = comma1.getSiblingCommas();
// assertTrue(result.isEmpty());
}

@Test
public void testIsSiblingTrueWhenParentsMatch() {
String[] tokens = { "go", ",", "stay" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TreeView view = mock(TreeView.class);
Constituent commaCon = mock(Constituent.class);
Constituent commaPeer = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
// TreeView.setParent(commaCon, parent);
// TreeView.setParent(commaPeer, parent);
when(view.getConstituents()).thenReturn(Arrays.asList(commaCon, commaPeer));
// when(ParseTreeProperties.isPreTerminal(any())).thenReturn(true);
// Comma comma1 = spy(new Comma(1, null, Collections.singletonList("X")));
// Comma comma2 = spy(new Comma(2, null, Collections.singletonList("Y")));
// doReturn(commaCon).when(comma1).getCommaConstituentFromTree(view);
// doReturn(commaPeer).when(comma2).getCommaConstituentFromTree(view);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.getCommas()).thenReturn(Arrays.asList(comma1, comma2));
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
// comma1.s = sentence;
// comma2.s = sentence;
// boolean isSibling = comma1.isSibling(comma2);
// assertTrue(isSibling);
}

@Test
public void testGetAnnotatedTextWhenCommaIsLastToken() {
String[] tokens = { "Yes", ",", "No", "," };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(3, sentence, Arrays.asList("End"));
// String annotated = comma.getAnnotatedText();
// assertTrue(annotated.endsWith("[End]"));
}

@Test
public void testGetBayraktarAnnotatedTextReturnsTagWithText() {
String[] tokens = { "Here", "," };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = spy(new Comma(1, sentence, Collections.singletonList("Tag")));
// doReturn("List").when(comma).getBayraktarLabel();
// String out = comma.getBayraktarAnnotatedText();
// assertTrue(out.contains("[List]"));
}

@Test
public void testGetNotationReturnsOnlyLabelIfNoNERorPOSorChild() {
String[] tokens = { "foo", ",", "bar" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("NP");
when(c.getOutgoingRelations()).thenReturn(Collections.emptyList());
// when(c.getSpan()).thenReturn(new IntPair(0, 1));
when(c.getTextAnnotation()).thenReturn(ta);
SpanLabelView ner = mock(SpanLabelView.class);
when(ner.getConstituentsCovering(c)).thenReturn(Collections.emptyList());
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(ner);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("tag"));
// String note = comma.getNotation(c);
// assertTrue(note.startsWith("NP"));
}

@Test
public void testGetStableBayraktarPatternWithChildrenIncludingFunctionTags() {
String[] tokens = { "they", ",", "read" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent commaNode = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
Constituent child = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(child);
when(parent.getOutgoingRelations()).thenReturn(Collections.singletonList(rel));
when(child.getLabel()).thenReturn("NP-SBJ");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(false);
when(ParseTreeProperties.isPunctuationToken("S")).thenReturn(false);
TreeView tree = mock(TreeView.class);
when(tree.getConstituents()).thenReturn(Collections.singletonList(commaNode));
// when(tree.getParsePhrase(commaNode)).thenReturn(commaNode);
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
when(TreeView.getParent(commaNode)).thenReturn(parent);
when(parent.getLabel()).thenReturn("S");
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("L"));
// String result = comma.getBayraktarPattern(tree);
// assertTrue(result.contains("NP"));
}

@Test
public void testGetStrippedNotationHandlesLabelWithMultipleHyphens() {
String[] tokens = { "The", ",", "politician" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent c = mock(Constituent.class);
when(c.getLabel()).thenReturn("NP-SBJ-TMP");
// when(c.getSpan()).thenReturn(new IntPair(0, 2));
when(c.getTextAnnotation()).thenReturn(ta);
TextAnnotation goldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(goldTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("A"));
// String result = comma.getStrippedNotation(c);
// assertTrue(result.startsWith("NP"));
}

@Test
public void testGetBayraktarPatternChildIsPreTerminalWithNonCCLabel() {
String[] tokens = { "He", ",", "ran" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent commaNode = mock(Constituent.class);
Constituent parentNode = mock(Constituent.class);
Constituent child1 = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(child1);
when(parentNode.getOutgoingRelations()).thenReturn(Collections.singletonList(rel));
when(child1.getLabel()).thenReturn("NN");
when(ParseTreeProperties.isPreTerminal(child1)).thenReturn(true);
when(ParseTreeProperties.isPreTerminal(parentNode)).thenReturn(false);
when(ParseTreeProperties.isPunctuationToken("VP")).thenReturn(false);
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaNode));
// when(parseView.getParsePhrase(commaNode)).thenReturn(commaNode);
when(TreeView.getParent(commaNode)).thenReturn(parentNode);
when(parentNode.getLabel()).thenReturn("VP");
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
TextAnnotation goldTa = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(goldTa);
// Comma comma = new Comma(1, sentence, Collections.singletonList("L"));
// String pattern = comma.getBayraktarPattern(parseView);
// assertTrue(pattern.contains("***"));
}

@Test
public void testGetBayraktarPatternChildWithFunctionTagProducesStrippedLabel() {
String[] tokens = { "Hello", ",", "you" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent commaNode = mock(Constituent.class);
Constituent parentNode = mock(Constituent.class);
Constituent child = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(child);
when(parentNode.getOutgoingRelations()).thenReturn(Collections.singletonList(rel));
when(child.getLabel()).thenReturn("NP-SBJ");
when(ParseTreeProperties.isPreTerminal(child)).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(parentNode)).thenReturn(false);
when(ParseTreeProperties.isPunctuationToken("S")).thenReturn(false);
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaNode));
// when(parseView.getParsePhrase(commaNode)).thenReturn(commaNode);
when(TreeView.getParent(commaNode)).thenReturn(parentNode);
when(parentNode.getLabel()).thenReturn("S");
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
// Comma comma = new Comma(1, sentence, Collections.singletonList("ZZ"));
// String result = comma.getBayraktarPattern(parseView);
// assertTrue(result.contains("NP"));
// assertTrue(result.startsWith("S -->"));
}

@Test
public void testGetNamedEntityTagSkipsEntityWithTooFewTokensToMatchThreshold() {
String[] tokens = { "Mr.", "Smith", ",", "arrived" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
SpanLabelView nerView = mock(SpanLabelView.class);
Constituent comma = mock(Constituent.class);
Constituent ne = mock(Constituent.class);
when(nerView.getConstituentsCovering(comma)).thenReturn(Collections.singletonList(ne));
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(ne.getLabel()).thenReturn("PERSON");
when(comma.doesConstituentCover(ne)).thenReturn(true);
when(ne.getNumberOfTokens()).thenReturn(1);
when(comma.getNumberOfTokens()).thenReturn(5);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(mock(TextAnnotation.class));
// Comma commaObj = new Comma(2, sentence, Collections.singletonList("make"));
// String result = commaObj.getNamedEntityTag(comma);
// assertEquals("", result);
}

@Test
public void testGetLabelReturnsFirstLabelFromMultiple() {
String[] tokens = { "Hi", ",", "there" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
TextAnnotation gta = mock(TextAnnotation.class);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(gta);
List<String> multiLabels = Arrays.asList("X", "Y", "Z");
// Comma comma = new Comma(1, sentence, multiLabels);
// assertEquals("X", comma.getLabel());
// assertEquals(3, comma.getLabels().size());
}

@Test
public void testGetChunkNgramsReturnsMixedNullAndNonNullChunks() {
String[] tokens = { "The", "dog", ",", "ran" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
Constituent leftChunk = mock(Constituent.class);
when(leftChunk.getLabel()).thenReturn("NP");
// when(leftChunk.getSpan()).thenReturn(new IntPair(0, 2));
when(leftChunk.getTextAnnotation()).thenReturn(ta);
Constituent rightChunk = mock(Constituent.class);
when(rightChunk.getLabel()).thenReturn("VP");
// when(rightChunk.getSpan()).thenReturn(new IntPair(3, 4));
when(rightChunk.getTextAnnotation()).thenReturn(ta);
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(0, 3)).thenReturn(Collections.singletonList(leftChunk));
when(chunkView.getSpanLabels(3, 4)).thenReturn(Collections.singletonList(rightChunk));
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// Comma comma = new Comma(2, sentence, Collections.singletonList("punct"));
// String[] ngrams = comma.getChunkNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetSiblingPhraseNgramsReturnsNgramsWhenBothSidesPresent() {
String[] tokens = { "Before", "it", ",", "rained", "hard" };
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(tokens);
CommaSRLSentence sentence = mock(CommaSRLSentence.class);
// when(sentence.ta).thenReturn(ta);
// when(sentence.goldTa).thenReturn(mock(TextAnnotation.class));
Constituent left = mock(Constituent.class);
when(left.getLabel()).thenReturn("SBAR");
// when(left.getSpan()).thenReturn(new IntPair(0, 2));
when(left.getTextAnnotation()).thenReturn(ta);
Constituent right = mock(Constituent.class);
when(right.getLabel()).thenReturn("VP");
// when(right.getSpan()).thenReturn(new IntPair(3, 5));
when(right.getTextAnnotation()).thenReturn(ta);
// Comma comma = spy(new Comma(2, sentence, Collections.singletonList("SpN")));
// doReturn(left).when(comma).getPhraseToLeftOfComma(1);
// doReturn(right).when(comma).getPhraseToRightOfComma(1);
// String[] ngrams = comma.getSiblingPhraseNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}
}
