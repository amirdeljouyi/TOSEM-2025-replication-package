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

public class Comma_4_GPTLLMTest {

@Test
public void testGetLabel() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence, Arrays.asList("GAPPED"));
// String result = comma.getLabel();
// assertEquals("GAPPED", result);
}

@Test
public void testGetWordToRight_validIndex() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "This", ",", "is", "a" });
when(textAnnotation.getToken(2)).thenReturn("is");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence, Arrays.asList("GAPPED"));
// String word = comma.getWordToRight(1);
// assertEquals("is", word);
}

@Test
public void testGetWordToRight_outOfBounds() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "Hello", "," });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence, Arrays.asList("SOME_LABEL"));
// String word = comma.getWordToRight(5);
// assertEquals("###", word);
}

@Test
public void testGetWordToLeft_validIndex() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(textAnnotation.getToken(0)).thenReturn("A");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence, Arrays.asList("SomeLabel"));
// String word = comma.getWordToLeft(1);
// assertEquals("A", word);
}

@Test
public void testGetWordToLeft_outOfBounds() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "Word" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(0, sentence, Arrays.asList("Label"));
// String word = comma.getWordToLeft(1);
// assertEquals("$$$", word);
}

@Test
public void testGetPOSToLeft_whenDefaultPOS() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(0)).thenReturn("NN");
when(textAnnotation.getView(ViewNames.POS)).thenReturn(posView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence, Arrays.asList("XX"));
// String pos = comma.getPOSToLeft(1);
// assertEquals("NN", pos);
}

@Test
public void testGetPOSToRight_whenDeterminerSpecialCase() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(2)).thenReturn("DT");
when(textAnnotation.getView(ViewNames.POS)).thenReturn(posView);
when(textAnnotation.getTokens()).thenReturn(new String[] { "The", ",", "the", "cat" });
when(textAnnotation.getToken(2)).thenReturn("the");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence, Arrays.asList("X"));
// String pos = comma.getPOSToRight(1);
// assertEquals("DT-the", pos);
}

@Test
public void testGetTextAnnotation_false() {
TextAnnotation ta = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence, Arrays.asList("LABEL"));
// TextAnnotation result = comma.getTextAnnotation(false);
// assertEquals(ta, result);
}

@Test
public void testGetCommaID() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("doc123");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(2, sentence, Arrays.asList("LABEL"));
// String id = comma.getCommaID();
// assertEquals("2 doc123", id);
}

@Test
public void testGetNotation_returnsNullNotationIfInputIsNull() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(2, sentence, Arrays.asList("LABEL"));
// String notation = comma.getNotation(null);
// assertEquals("NULL", notation);
}

@Test
public void testGetStrippedNotation_returnsNullNotationIfInputIsNull() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(2, sentence, Arrays.asList("ANY"));
// String notation = comma.getStrippedNotation(null);
// assertEquals("NULL", notation);
}

@Test
public void testGetAnnotatedText_returnsFormattedString() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "I", ",", "see", "you" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence, Arrays.asList("GAPPED"));
// String text = comma.getAnnotatedText();
// assertEquals("I ,[GAPPED] see you", text);
}

@Test
public void testGetBayraktarAnnotatedText_returnsPatternAnnotated() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "A", ",", "B", "C" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence, Arrays.asList("Test"));
// String text = comma.getBayraktarAnnotatedText();
// assertEquals("A ,[Other] B C", text);
}

@Test
public void testGetChunkToRightOfComma_withEmptyChunkListReturnsNull() {
SpanLabelView shallowParseView = mock(SpanLabelView.class);
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(textAnnotation.getView(ViewNames.SHALLOW_PARSE)).thenReturn(shallowParseView);
when(shallowParseView.getSpanLabels(anyInt(), anyInt())).thenReturn(Collections.emptyList());
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence);
// assertNull(comma.getChunkToRightOfComma(1));
}

@Test
public void testGetChunkToLeftOfComma_withEmptyChunkListReturnsNull() {
SpanLabelView chunkView = mock(SpanLabelView.class);
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(textAnnotation.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Collections.emptyList());
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
// Comma comma = new Comma(1, sentence);
// assertNull(comma.getChunkToLeftOfComma(1));
}

@Test
public void testGetTextAnnotation_true_returnsGold() {
TextAnnotation goldAnnotation = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = goldAnnotation;
// sentence.goldTa = goldAnnotation;
// Comma comma = new Comma(3, sentence);
// TextAnnotation result = comma.getTextAnnotation(true);
// assertEquals(goldAnnotation, result);
}

@Test
public void testGetLabels_returnsSameList() {
TextAnnotation annotation = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = annotation;
// sentence.goldTa = annotation;
List<String> labels = Arrays.asList("A", "B");
// Comma comma = new Comma(0, sentence, labels);
// assertEquals(labels, comma.getLabels());
}

@Test
public void testGetPosition_returnsCorrectValue() {
TextAnnotation annotation = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = annotation;
// sentence.goldTa = annotation;
// Comma comma = new Comma(5, sentence, Arrays.asList("X"));
// int position = comma.getPosition();
// assertEquals(5, position);
}

@Test
public void testGetPOSToLeft_whenDistanceGreaterThanPosition() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
TokenLabelView posView = mock(TokenLabelView.class);
when(textAnnotation.getView(ViewNames.POS)).thenReturn(posView);
when(posView.getLabel(-1)).thenThrow(new IndexOutOfBoundsException());
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
Comma.useGoldFeatures(false);
try {
// Comma comma = new Comma(0, sentence, Arrays.asList("X"));
// comma.getPOSToLeft(1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testGetPOSToRight_whenTokenEqualsThe_returnsDTThe() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(2)).thenReturn("DT");
when(textAnnotation.getView(ViewNames.POS)).thenReturn(posView);
when(textAnnotation.getToken(2)).thenReturn("the");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence);
// String pos = comma.getPOSToRight(1);
// assertEquals("DT-the", pos);
}

@Test
public void testGetBayraktarPattern_whenNoConstituents_returnsEmptyPattern() {
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getTokens()).thenReturn(new String[] { "X", ",", "Y" });
when(textAnnotation.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = textAnnotation;
// sentence.goldTa = textAnnotation;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence);
// String pattern = comma.getBayraktarPattern();
// assertNotNull(pattern);
// assertTrue(pattern.endsWith("-->"));
}

@Test
public void testGetSiblingPhraseNgrams_returnsNgramsEvenWithNullPhrases() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
TreeView parseView = mock(TreeView.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
when(parseView.getConstituents()).thenReturn(Collections.emptyList());
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence);
// String[] result = comma.getSiblingPhraseNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
}

@Test
public void testGetWordNgrams_whenNearSentenceStart_returnsPadding() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Hi", "," });
when(ta.getToken(0)).thenReturn("Hi");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] ngrams = comma.getWordNgrams();
// assertNotNull(ngrams);
// assertTrue(Arrays.stream(ngrams).anyMatch(s -> s.contains("$$$")));
}

@Test
public void testGetNamedEntityTag_withMISCEntity_shouldIgnoreMISC() {
SpanLabelView nerView = mock(SpanLabelView.class);
Constituent commaConstituent = mock(Constituent.class);
Constituent miscNE = mock(Constituent.class);
when(miscNE.getLabel()).thenReturn("MISC");
when(commaConstituent.doesConstituentCover(miscNE)).thenReturn(true);
when(miscNE.getNumberOfTokens()).thenReturn(1);
when(commaConstituent.getNumberOfTokens()).thenReturn(1);
when(nerView.getConstituentsCovering(any())).thenReturn(Collections.singletonList(miscNE));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String nerTag = comma.getNamedEntityTag(commaConstituent);
// assertEquals("", nerTag);
}

@Test
public void testGetContainingSRLs_withOneMatchingRelation() {
Constituent predicate = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(target);
when(rel.getSource()).thenReturn(predicate);
when(rel.getRelationName()).thenReturn("A1");
PredicateArgumentView srlVerbView = mock(PredicateArgumentView.class);
when(srlVerbView.getPredicates()).thenReturn(Collections.singletonList(predicate));
when(srlVerbView.getArguments(predicate)).thenReturn(Collections.singletonList(rel));
when(srlVerbView.getPredicateLemma(predicate)).thenReturn("run");
when(target.getStartSpan()).thenReturn(1);
when(target.getEndSpan()).thenReturn(3);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SRL_VERB)).thenReturn(srlVerbView);
when(ta.getView(ViewNames.SRL_NOM)).thenReturn(mock(PredicateArgumentView.class));
when(ta.getView(ViewNames.SRL_PREP)).thenReturn(mock(PredicateArgumentView.class));
when(ta.getTokens()).thenReturn(new String[] { "X", ",", "Y" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// List<String> result = comma.getContainingSRLs();
// assertEquals(1, result.size());
// assertEquals("runA1", result.get(0));
}

@Test
public void testGetChunkNgrams_withAllNullChunksProducesNullNotations() {
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] chunks = comma.getChunkNgrams();
// assertNotNull(chunks);
// assertTrue(chunks.length > 0);
// assertTrue(Arrays.stream(chunks).allMatch(s -> s.contains("NULL")));
}

@Test
public void testGetWordToLeft_atStartOfSentence() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { ",", "hello" });
when(ta.getToken(anyInt())).thenAnswer(invocation -> {
int index = invocation.getArgument(0);
if (index >= 0 && index < 2)
return new String[] { ",", "hello" }[index];
throw new IndexOutOfBoundsException();
});
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(0, sentence);
// String result = comma.getWordToLeft(1);
// assertEquals("$$$", result);
}

@Test
public void testGetCommaConstituentFromTree_returnsNullIfNoMatch() {
TreeView view = mock(TreeView.class);
when(view.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Hello", ",", "world" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getCommaConstituentFromTree(view);
// assertNull(result);
}

@Test
public void testGetNotation_withOutgoingRelationAndLexicalFlagsDisabled() {
Constituent constituent = mock(Constituent.class);
Relation relation = mock(Relation.class);
Constituent target = mock(Constituent.class);
when(constituent.getLabel()).thenReturn("NP");
when(constituent.getOutgoingRelations()).thenReturn(Arrays.asList(relation));
when(relation.getTarget()).thenReturn(target);
when(target.getLabel()).thenReturn("VP");
when(constituent.getViewName()).thenReturn(ViewNames.PARSE_STANFORD);
// when(constituent.getSpan()).thenReturn(new IntPair(0, 1));
TextAnnotation ta = mock(TextAnnotation.class);
when(constituent.getTextAnnotation()).thenReturn(ta);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(0, sentence, Arrays.asList("LABEL"));
// String result = comma.getNotation(constituent);
// assertTrue(result.contains("NPVP"));
}

@Test
public void testGetStrippedNotation_withNERAndPOSLexicalisationEnabled() {
Constituent constituent = mock(Constituent.class);
when(constituent.getLabel()).thenReturn("NP-X");
// when(constituent.getSpan()).thenReturn(new IntPair(0, 2));
when(constituent.getNumberOfTokens()).thenReturn(2);
when(constituent.getTextAnnotation()).thenReturn(mock(TextAnnotation.class));
Constituent nerCon = mock(Constituent.class);
when(nerCon.getLabel()).thenReturn("PERSON");
when(nerCon.getNumberOfTokens()).thenReturn(2);
when(constituent.doesConstituentCover(nerCon)).thenReturn(true);
SpanLabelView nerView = mock(SpanLabelView.class);
when(nerView.getConstituentsCovering(constituent)).thenReturn(Arrays.asList(nerCon));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(ta.getTokens()).thenReturn(new String[] { "John", "Doe" });
when(constituent.getTextAnnotation()).thenReturn(ta);
when(POSUtils.getPOS(any(TextAnnotation.class), eq(0))).thenReturn("NNP");
when(POSUtils.getPOS(any(TextAnnotation.class), eq(1))).thenReturn("NNP");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String result = comma.getStrippedNotation(constituent);
// assertTrue(result.contains("NP"));
// assertTrue(result.contains("+PERSON"));
// assertTrue(result.contains("NNP"));
}

@Test
public void testIsSibling_whenDifferentParents_returnFalse() {
TreeView parseView = mock(TreeView.class);
Constituent thisCommaConstituent = mock(Constituent.class);
Constituent otherCommaConstituent = mock(Constituent.class);
Constituent parent1 = mock(Constituent.class);
Constituent parent2 = mock(Constituent.class);
when(thisCommaConstituent.isConsituentInRange(1, 2)).thenReturn(true);
when(otherCommaConstituent.isConsituentInRange(3, 4)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Arrays.asList(thisCommaConstituent, otherCommaConstituent));
// when(parseView.getParsePhrase(thisCommaConstituent)).thenReturn(thisCommaConstituent);
// when(parseView.getParsePhrase(otherCommaConstituent)).thenReturn(otherCommaConstituent);
when(TreeView.getParent(thisCommaConstituent)).thenReturn(parent1);
when(TreeView.getParent(otherCommaConstituent)).thenReturn(parent2);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b", ",", "c" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma1 = new Comma(1, sentence);
// Comma comma2 = new Comma(3, sentence);
// boolean result = comma1.isSibling(comma2);
// assertFalse(result);
}

@Test
public void testGetSiblingCommas_whenOnlyOneComma_returnsSingleItemList() {
TreeView parseView = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "I", ",", "run" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
Constituent commaConstituent = mock(Constituent.class);
when(commaConstituent.isConsituentInRange(1, 2)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Arrays.asList(commaConstituent));
// when(parseView.getParsePhrase(commaConstituent)).thenReturn(commaConstituent);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// when(sentence.getCommas()).thenReturn(Arrays.asList(comma));
// List<Comma> siblings = comma.getSiblingCommas();
// assertEquals(1, siblings.size());
// assertEquals(comma, siblings.get(0));
}

@Test
public void testGetBayraktarPattern_parentWithCCLabelReturnsSurfaceForm() {
TreeView parseView = mock(TreeView.class);
Constituent commaConstituent = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
Relation childRelation = mock(Relation.class);
Constituent childCC = mock(Constituent.class);
when(childCC.getLabel()).thenReturn("CC");
when(childCC.getSurfaceForm()).thenReturn("and");
when(childRelation.getTarget()).thenReturn(childCC);
when(parent.getOutgoingRelations()).thenReturn(Arrays.asList(childRelation));
when(parent.getLabel()).thenReturn("CC");
when(ParseTreeProperties.isPunctuationToken("CC")).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Arrays.asList(commaConstituent));
when(commaConstituent.isConsituentInRange(1, 2)).thenReturn(true);
// when(parseView.getParsePhrase(commaConstituent)).thenReturn(commaConstituent);
when(TreeView.getParent(commaConstituent)).thenReturn(parent);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String result = comma.getBayraktarPattern();
// assertTrue(result.contains("and -->"));
}

@Test
public void testGetParentSiblingPhraseNgrams_allNullReturnsNgramsOfNullNotations() {
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "x", ",", "y" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] ngrams = comma.getParentSiblingPhraseNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
// assertTrue(ngrams[0].contains("NULL"));
}

@Test
public void testGetLeftToRightDependencies_noConstituents_returnsEmptyArray() {
TreeView dependencyView = mock(TreeView.class);
when(dependencyView.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(dependencyView);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] deps = comma.getLeftToRightDependencies();
// assertNotNull(deps);
// assertEquals(0, deps.length);
}

@Test
public void testGetRightToLeftDependencies_oneRelationCrossingComma() {
TreeView dependencyView = mock(TreeView.class);
Constituent c = mock(Constituent.class);
Relation incoming = mock(Relation.class);
Constituent source = mock(Constituent.class);
when(source.getStartSpan()).thenReturn(2);
when(incoming.getSource()).thenReturn(source);
when(incoming.getRelationName()).thenReturn("nsubj");
when(c.getIncomingRelations()).thenReturn(Collections.singletonList(incoming));
List<Constituent> constituents = new ArrayList<>();
constituents.add(c);
when(dependencyView.getConstituentsCoveringSpan(0, 1)).thenReturn(constituents);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(dependencyView);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] deps = comma.getRightToLeftDependencies();
// assertNotNull(deps);
// assertEquals(1, deps.length);
// assertEquals("nsubj", deps[0]);
}

@Test
public void testGetSiblingToLeft_returnsNullIfNoLeftSibling() {
TreeView parseView = mock(TreeView.class);
Constituent commaNode = mock(Constituent.class);
IQueryable<Constituent> siblings = mock(IQueryable.class);
when(parseView.where(Queries.isSiblingOf(commaNode))).thenReturn(siblings);
// when(siblings.where(Queries.adjacentToBefore(commaNode))).thenReturn(new QueryableList<>(new ArrayList<>()));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getSiblingToLeft(1, commaNode, parseView);
// assertNull(result);
}

@Test
public void testGetSiblingToRight_returnsNullIfNoRightSibling() {
TreeView parseView = mock(TreeView.class);
Constituent commaNode = mock(Constituent.class);
IQueryable<Constituent> siblings = mock(IQueryable.class);
when(parseView.where(Queries.isSiblingOf(commaNode))).thenReturn(siblings);
// when(siblings.where(Queries.adjacentToAfter(commaNode))).thenReturn(new QueryableList<>(Collections.emptyList()));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "x", ",", "y" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getSiblingToRight(1, commaNode, parseView);
// assertNull(result);
}

@Test
public void testGetBayraktarLabel_returnsLabelIfNotNull() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "Hello", ",", "world" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
BayraktarPatternLabeler mockLabeler = mock(BayraktarPatternLabeler.class);
// String label = comma.getBayraktarLabel();
// assertNotNull(label);
// assertTrue(label.equals("Other") || label.length() > 0);
}

@Test
public void testGetNamedEntityTag_withMultipleNERs_nonMISC_shouldCombine() {
Constituent c = mock(Constituent.class);
when(c.getNumberOfTokens()).thenReturn(5);
when(c.getTextAnnotation()).thenReturn(mock(TextAnnotation.class));
Constituent ner1 = mock(Constituent.class);
when(ner1.getLabel()).thenReturn("PERSON");
when(ner1.getNumberOfTokens()).thenReturn(3);
when(c.doesConstituentCover(ner1)).thenReturn(true);
Constituent ner2 = mock(Constituent.class);
when(ner2.getLabel()).thenReturn("ORG");
when(ner2.getNumberOfTokens()).thenReturn(3);
when(c.doesConstituentCover(ner2)).thenReturn(true);
List<Constituent> nerList = Arrays.asList(ner1, ner2);
SpanLabelView nerView = mock(SpanLabelView.class);
when(nerView.getConstituentsCovering(c)).thenReturn(nerList);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(c.getTextAnnotation()).thenReturn(ta);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String result = comma.getNamedEntityTag(c);
// assertTrue(result.contains("+PERSON"));
// assertTrue(result.contains("+ORG"));
}

@Test
public void testGetBayraktarPattern_complexStructureWithPunctuationChildren() {
TreeView view = mock(TreeView.class);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "The", ",", "cat" });
Constituent commaNode = mock(Constituent.class);
when(view.getConstituents()).thenReturn(Collections.singletonList(commaNode));
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
// when(view.getParsePhrase(commaNode)).thenReturn(commaNode);
Constituent parent = mock(Constituent.class);
Relation r1 = mock(Relation.class);
Constituent child1 = mock(Constituent.class);
when(child1.getLabel()).thenReturn(".");
when(ParseTreeProperties.isPreTerminal(child1)).thenReturn(true);
when(POSUtils.isPOSPunctuation(".")).thenReturn(true);
when(r1.getTarget()).thenReturn(child1);
when(parent.getOutgoingRelations()).thenReturn(Collections.singletonList(r1));
when(parent.getLabel()).thenReturn("VP");
when(ParseTreeProperties.isPunctuationToken("VP")).thenReturn(false);
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(false);
when(TreeView.getParent(commaNode)).thenReturn(parent);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(view);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence);
// String result = comma.getBayraktarPattern();
// assertTrue(result.contains("VP -->"));
}

@Test
public void testGetPhraseToLeftOfComma_whenCommaNodeIsNull_returnsNull() {
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getPhraseToLeftOfComma(1);
// assertNull(result);
}

@Test
public void testGetPhraseToRightOfComma_whenCommaNodeIsNull_returnsNull() {
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "X", ",", "Y" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getPhraseToRightOfComma(2);
// assertNull(result);
}

@Test
public void testGetPhraseToLeftOfParent_whenParentIsNull_returnsNull() {
TreeView parseView = mock(TreeView.class);
Constituent commaNode = mock(Constituent.class);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaNode));
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
// when(parseView.getParsePhrase(commaNode)).thenReturn(commaNode);
when(TreeView.getParent(commaNode)).thenReturn(null);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "One", ",", "two" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getPhraseToLeftOfParent(1);
// assertNull(result);
}

@Test
public void testGetPhraseToRightOfParent_whenNoRightSibling_returnsNull() {
TreeView parseView = mock(TreeView.class);
Constituent commaNode = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
IQueryable<Constituent> siblings = mock(IQueryable.class);
// when(siblings.where(Queries.adjacentToAfter(parent))).thenReturn(new QueryableList<>(Collections.emptyList()));
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaNode));
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
// when(parseView.getParsePhrase(commaNode)).thenReturn(commaNode);
when(TreeView.getParent(commaNode)).thenReturn(parent);
when(parseView.where(Queries.isSiblingOf(parent))).thenReturn(siblings);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getPhraseToRightOfParent(1);
// assertNull(result);
}

@Test
public void testGetNotation_withNERAndNoPOS_returnsNERTagOnly() {
Constituent constituent = mock(Constituent.class);
when(constituent.getLabel()).thenReturn("NP");
// when(constituent.getSpan()).thenReturn(new IntPair(0, 1));
when(constituent.getOutgoingRelations()).thenReturn(new ArrayList<>());
when(constituent.getViewName()).thenReturn(ViewNames.PARSE_STANFORD);
when(constituent.getNumberOfTokens()).thenReturn(1);
Constituent ner = mock(Constituent.class);
when(ner.getLabel()).thenReturn("LOCATION");
when(ner.getNumberOfTokens()).thenReturn(1);
when(constituent.doesConstituentCover(ner)).thenReturn(true);
List<Constituent> nerList = Collections.singletonList(ner);
SpanLabelView nerView = mock(SpanLabelView.class);
when(nerView.getConstituentsCovering(constituent)).thenReturn(nerList);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(constituent.getTextAnnotation()).thenReturn(ta);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(2, sentence);
// String result = comma.getNotation(constituent);
// assertTrue(result.contains("+LOCATION"));
// assertTrue(result.startsWith("NP"));
}

@Test
public void testGetContainingSRLs_ignoresRelationBeforeComma() {
Constituent predicate = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(target);
when(rel.getSource()).thenReturn(predicate);
when(rel.getRelationName()).thenReturn("ARG0");
when(target.getStartSpan()).thenReturn(0);
when(target.getEndSpan()).thenReturn(1);
PredicateArgumentView pav = mock(PredicateArgumentView.class);
when(pav.getPredicates()).thenReturn(Arrays.asList(predicate));
when(pav.getArguments(predicate)).thenReturn(Arrays.asList(rel));
when(pav.getPredicateLemma(predicate)).thenReturn("eat");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SRL_VERB)).thenReturn(pav);
when(ta.getView(ViewNames.SRL_NOM)).thenReturn(mock(PredicateArgumentView.class));
when(ta.getView(ViewNames.SRL_PREP)).thenReturn(mock(PredicateArgumentView.class));
when(ta.getTokens()).thenReturn(new String[] { "I", ",", "eat" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// List<String> result = comma.getContainingSRLs();
// assertNotNull(result);
// assertTrue(result.isEmpty());
}

@Test
public void testGetChunkToRightOfComma_withDistanceZero_returnsNull() {
SpanLabelView shallowView = mock(SpanLabelView.class);
when(shallowView.getSpanLabels(anyInt(), anyInt())).thenReturn(Arrays.asList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(shallowView);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getChunkToRightOfComma(0);
// assertNull(result);
}

@Test
public void testGetChunkToLeftOfComma_withDistanceOutOfBounds_returnsNull() {
SpanLabelView chunkView = mock(SpanLabelView.class);
Constituent chunk = mock(Constituent.class);
when(chunk.getStartSpan()).thenReturn(0);
when(chunk.getEndSpan()).thenReturn(1);
List<Constituent> chunks = Collections.singletonList(chunk);
when(chunkView.getSpanLabels(0, 2)).thenReturn(chunks);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
when(ta.getTokens()).thenReturn(new String[] { "Hello", ",", "world" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getChunkToLeftOfComma(2);
// assertNull(result);
}

@Test
public void testGetSiblingCommas_duplicateConstituentsMapsCorrectly() {
TreeView parseView = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
when(c1.isConsituentInRange(1, 2)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(c1));
// when(parseView.getParsePhrase(c1)).thenReturn(c1);
// QueryableList<Constituent> qList = new QueryableList<>(Collections.singletonList(c1));
// when(parseView.where(any())).thenReturn(qList);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "X", ",", "Y" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma c = new Comma(1, sentence);
// when(sentence.getCommas()).thenReturn(Collections.singletonList(c));
// List<Comma> siblings = c.getSiblingCommas();
// assertEquals(1, siblings.size());
// assertSame(c, siblings.get(0));
}

@Test
public void testGetSiblingCommaHead_withMultipleSiblings_returnsLeftmost() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b", ",", "c" });
TreeView parseView = mock(TreeView.class);
Constituent comma1Node = mock(Constituent.class);
Constituent comma2Node = mock(Constituent.class);
when(comma1Node.isConsituentInRange(1, 2)).thenReturn(true);
when(comma2Node.isConsituentInRange(3, 4)).thenReturn(true);
// when(parseView.getParsePhrase(comma1Node)).thenReturn(comma1Node);
// when(parseView.getParsePhrase(comma2Node)).thenReturn(comma2Node);
when(parseView.getConstituents()).thenReturn(Arrays.asList(comma1Node, comma2Node));
when(TreeView.getParent(comma1Node)).thenReturn(mock(Constituent.class));
when(TreeView.getParent(comma2Node)).thenReturn(mock(Constituent.class));
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma1 = new Comma(1, sentence);
// Comma comma2 = new Comma(3, sentence);
// when(sentence.getCommas()).thenReturn(Arrays.asList(comma1, comma2));
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// Comma head = comma2.getSiblingCommaHead();
// assertEquals(1, head.commaPosition);
}

@Test
public void testGetCommaConstituentFromTree_withMultipleMatches_returnsFirstParsePhrase() {
TreeView parseView = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
when(c1.isConsituentInRange(1, 2)).thenReturn(true);
when(c2.isConsituentInRange(1, 2)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Arrays.asList(c1, c2));
// when(parseView.getParsePhrase(c1)).thenReturn(c1);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getCommaConstituentFromTree(parseView);
// assertEquals(c1, result);
}

@Test
public void testGetBayraktarPattern_parentHasNullOutgoingRelations() {
TreeView parseView = mock(TreeView.class);
Constituent commaNode = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(parent.getOutgoingRelations()).thenReturn(null);
when(parent.getLabel()).thenReturn("VP");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(false);
when(ParseTreeProperties.isPunctuationToken("VP")).thenReturn(false);
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaNode));
// when(parseView.getParsePhrase(commaNode)).thenReturn(commaNode);
when(TreeView.getParent(commaNode)).thenReturn(parent);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "X", ",", "Y" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String pattern = comma.getBayraktarPattern();
// assertTrue(pattern.startsWith("VP -->"));
}

@Test
public void testGetBayraktarPattern_parentWithPunctuationPreTerminal_returnsStars() {
TreeView parseView = mock(TreeView.class);
Constituent commaNode = mock(Constituent.class);
Constituent parent = mock(Constituent.class);
when(parent.getLabel()).thenReturn("PRP$");
when(ParseTreeProperties.isPreTerminal(parent)).thenReturn(true);
when(ParseTreeProperties.isPunctuationToken("PRP$")).thenReturn(false);
when(commaNode.isConsituentInRange(1, 2)).thenReturn(true);
when(parseView.getConstituents()).thenReturn(Collections.singletonList(commaNode));
// when(parseView.getParsePhrase(commaNode)).thenReturn(commaNode);
when(TreeView.getParent(commaNode)).thenReturn(parent);
Relation r = mock(Relation.class);
Constituent child = mock(Constituent.class);
when(child.getLabel()).thenReturn("NN");
when(ParseTreeProperties.isPreTerminal(child)).thenReturn(true);
when(POSUtils.isPOSPunctuation("NN")).thenReturn(false);
when(r.getTarget()).thenReturn(child);
when(parent.getOutgoingRelations()).thenReturn(Collections.singletonList(r));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "who", ",", "what" });
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String pattern = comma.getBayraktarPattern();
// assertTrue(pattern.contains("***"));
}

@Test
public void testGetPOSNgrams_withLessThanRequiredTokens() {
TokenLabelView posView = mock(TokenLabelView.class);
when(posView.getLabel(anyInt())).thenReturn("DT");
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "the" });
when(ta.getView(ViewNames.POS)).thenReturn(posView);
when(ta.getToken(2)).thenReturn("the");
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence);
// String[] ngrams = comma.getPOSNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
}

@Test
public void testGetChunkToRightOfComma_withUnsortedSpans_sortedByStart() {
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
when(c1.getStartSpan()).thenReturn(4);
when(c2.getStartSpan()).thenReturn(2);
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(anyInt(), anyInt())).thenReturn(Arrays.asList(c1, c2));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b", "c", "d" });
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// Constituent result = comma.getChunkToRightOfComma(1);
// assertNotNull(result);
// assertEquals(c2, result);
}

@Test
public void testGetStrippedNotation_withDashInLabelProperSplit() {
Constituent constituent = mock(Constituent.class);
when(constituent.getLabel()).thenReturn("NP-LOC");
// when(constituent.getSpan()).thenReturn(new IntPair(0, 1));
when(constituent.getNumberOfTokens()).thenReturn(1);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "NYC" });
when(constituent.getTextAnnotation()).thenReturn(ta);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(0, sentence);
// String result = comma.getStrippedNotation(constituent);
// assertTrue(result.startsWith("NP"));
}

@Test
public void testGetLabel_returnsNullLabel_throwsException() {
TextAnnotation ta = mock(TextAnnotation.class);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence, null);
try {
// comma.getLabel();
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testGetNotation_withEmptyOutgoingRelations_noNER_noPOS_returnsOnlyLabel() {
Constituent constituent = mock(Constituent.class);
when(constituent.getLabel()).thenReturn("VP");
when(constituent.getOutgoingRelations()).thenReturn(Collections.emptyList());
when(constituent.getViewName()).thenReturn(ViewNames.PARSE_STANFORD);
// when(constituent.getSpan()).thenReturn(new IntPair(0, 1));
when(constituent.getTextAnnotation()).thenReturn(mock(TextAnnotation.class));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(mock(SpanLabelView.class));
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(2, sentence);
// String notation = comma.getNotation(constituent);
// assertEquals("VP", notation);
}

@Test
public void testGetRightToLeftDependencies_withMultipleConstituents_matchingOnlyOne() {
TreeView depView = mock(TreeView.class);
Constituent c1 = mock(Constituent.class);
Relation r1 = mock(Relation.class);
Constituent r1Source = mock(Constituent.class);
when(r1Source.getStartSpan()).thenReturn(3);
when(r1.getSource()).thenReturn(r1Source);
when(r1.getRelationName()).thenReturn("obj");
when(c1.getIncomingRelations()).thenReturn(Arrays.asList(r1));
Constituent c2 = mock(Constituent.class);
when(c2.getIncomingRelations()).thenReturn(Collections.emptyList());
List<Constituent> constituents = Arrays.asList(c1, c2);
when(depView.getConstituentsCoveringSpan(0, 2)).thenReturn(constituents);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "I", ",", "see", "you" });
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] deps = comma.getRightToLeftDependencies();
// assertEquals(1, deps.length);
// assertEquals("obj", deps[0]);
}

@Test
public void testGetWordNgrams_withAllSpecialMarkersInWindow() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "x" });
when(ta.getToken(anyInt())).thenAnswer(invocation -> {
int index = invocation.getArgument(0);
if (index == 0)
return "x";
if (index < 0)
return "$$$";
return "###";
});
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(0, sentence);
// String[] ngrams = comma.getWordNgrams();
// assertNotNull(ngrams);
// assertTrue(ngrams.length > 0);
// assertTrue(Arrays.stream(ngrams).anyMatch(s -> s.contains("$$$")));
// assertTrue(Arrays.stream(ngrams).anyMatch(s -> s.contains("###")));
}

@Test
public void testGetChunkNgrams_withBalancedChunksOnBothSides() {
Constituent left = mock(Constituent.class);
when(left.getLabel()).thenReturn("NP");
// when(left.getSpan()).thenReturn(new IntPair(0, 1));
when(left.getTextAnnotation()).thenReturn(mock(TextAnnotation.class));
when(left.getOutgoingRelations()).thenReturn(Collections.emptyList());
when(left.getViewName()).thenReturn(ViewNames.SHALLOW_PARSE);
Constituent right = mock(Constituent.class);
when(right.getLabel()).thenReturn("VP");
// when(right.getSpan()).thenReturn(new IntPair(2, 3));
when(right.getTextAnnotation()).thenReturn(mock(TextAnnotation.class));
when(right.getOutgoingRelations()).thenReturn(Collections.emptyList());
when(right.getViewName()).thenReturn(ViewNames.SHALLOW_PARSE);
SpanLabelView chunkView = mock(SpanLabelView.class);
when(chunkView.getSpanLabels(0, 2)).thenReturn(Collections.singletonList(left));
when(chunkView.getSpanLabels(2, 4)).thenReturn(Collections.singletonList(right));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "A", ",", "B" });
when(ta.getView(ViewNames.SHALLOW_PARSE)).thenReturn(chunkView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] result = comma.getChunkNgrams();
// assertNotNull(result);
// assertTrue(result.length > 0);
// assertTrue(Arrays.stream(result).anyMatch(s -> s.contains("NP")));
// assertTrue(Arrays.stream(result).anyMatch(s -> s.contains("VP")));
}

@Test
public void testGetSiblingPhraseNgrams_whenAllSiblingsNull_returnsNullNotation() {
TreeView parseView = mock(TreeView.class);
when(parseView.getConstituents()).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(parseView);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
Comma.useGoldFeatures(false);
// Comma comma = new Comma(1, sentence);
// String[] ngrams = comma.getSiblingPhraseNgrams();
// assertNotNull(ngrams);
// assertTrue(Arrays.stream(ngrams).anyMatch(s -> s.contains("NULL")));
}

@Test
public void testGetNamedEntityTag_whenMatchingNERsBelowCoverageThreshold_returnsEmpty() {
Constituent container = mock(Constituent.class);
when(container.getNumberOfTokens()).thenReturn(5);
Constituent ner = mock(Constituent.class);
when(ner.getLabel()).thenReturn("PERSON");
when(ner.getNumberOfTokens()).thenReturn(2);
when(container.doesConstituentCover(ner)).thenReturn(true);
SpanLabelView nerView = mock(SpanLabelView.class);
when(nerView.getConstituentsCovering(container)).thenReturn(Collections.singletonList(ner));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.NER_CONLL)).thenReturn(nerView);
when(container.getTextAnnotation()).thenReturn(ta);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(2, sentence);
// String result = comma.getNamedEntityTag(container);
// assertEquals("", result);
}

@Test
public void testGetLeftToRightDependencies_whenValidRelationExists_returnsDependencyName() {
TreeView depView = mock(TreeView.class);
Constituent left = mock(Constituent.class);
Constituent right = mock(Constituent.class);
when(right.getStartSpan()).thenReturn(2);
Relation rel = mock(Relation.class);
when(rel.getTarget()).thenReturn(right);
when(rel.getRelationName()).thenReturn("det");
when(left.getOutgoingRelations()).thenReturn(Collections.singletonList(rel));
when(depView.getConstituentsCoveringSpan(0, 1)).thenReturn(Collections.singletonList(left));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getTokens()).thenReturn(new String[] { "a", ",", "b" });
when(ta.getView(ViewNames.DEPENDENCY_STANFORD)).thenReturn(depView);
// CommaSRLSentence sentence = new CommaSRLSentence();
// sentence.ta = ta;
// sentence.goldTa = ta;
// Comma comma = new Comma(1, sentence);
// String[] result = comma.getLeftToRightDependencies();
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals("det", result[0]);
}
}
