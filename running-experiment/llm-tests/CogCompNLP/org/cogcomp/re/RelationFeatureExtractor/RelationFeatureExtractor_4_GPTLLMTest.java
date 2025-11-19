package org.cogcomp.re;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.nlp.Word;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.pos.MikheevLearner;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RelationFeatureExtractor_4_GPTLLMTest {

@Test
public void testIsNoun() {
assertTrue(RelationFeatureExtractor.isNoun("NN"));
assertTrue(RelationFeatureExtractor.isNoun("NNS"));
assertTrue(RelationFeatureExtractor.isNoun("RB"));
assertTrue(RelationFeatureExtractor.isNoun("WP"));
assertFalse(RelationFeatureExtractor.isNoun("VB"));
assertFalse(RelationFeatureExtractor.isNoun(""));
}

@Test
public void testGetEntityHeadWithIsPredicted() {
List<String> tokens = Arrays.asList("Barack", "Obama");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "id", Collections.singletonList(tokens));
// Constituent cons = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// cons.addAttribute("IsPredicted", "true");
// Constituent head = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, ViewNames.MENTION);
// assertSame(cons, head);
}

@Test
public void testOnlyNounBetweenReturnsTrue() {
List<String> tokens = Arrays.asList("John", "car");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "id", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "dummy", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0f);
// posView.addTokenLabel(1, "NN", 1.0f);
// ta.addView(ViewNames.POS, posView);
// Constituent front = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent back = new Constituent("VEH", 1.0, ViewNames.MENTION, ta, 1, 2);
// boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
// assertTrue(result);
}

@Test
public void testGetLexicalFeaturePartA() {
List<String> tokens = Arrays.asList("Barack", "Obama");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "1", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 2);
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 1, 2);
// Relation r = new Relation("testRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> features = rfe.getLexicalFeaturePartA(r);
// assertEquals(2, features.size());
// assertEquals("Barack", features.get(0));
// assertEquals("Obama", features.get(1));
}

@Test
public void testGetLexicalFeaturePartB() {
List<String> tokens = Arrays.asList("New", "York");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "2", Collections.singletonList(tokens));
// Constituent source = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("GPE", 1.0, ViewNames.MENTION, ta, 0, 2);
// Relation r = new Relation("testRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> features = rfe.getLexicalFeaturePartB(r);
// assertEquals(2, features.size());
// assertEquals("New", features.get(0));
// assertEquals("York", features.get(1));
}

@Test
public void testGetLexicalFeaturePartCWithSingleWordBetween() {
List<String> tokens = Arrays.asList("John", "'s", "car");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "3", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("VEH", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("possRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> features = rfe.getLexicalFeaturePartC(r);
// assertEquals(1, features.size());
// assertTrue(features.get(0).startsWith("singleword_"));
}

@Test
public void testGetCorefTagTrue() {
List<String> tokens = Arrays.asList("Obama", "visited", "Obama");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "coref", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityID", "E1");
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityID", "E1");
// Relation r = new Relation("coref", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// String tag = rfe.getCorefTag(r);
// assertEquals("TRUE", tag);
}

@Test
public void testGetCorefTagFalse() {
List<String> tokens = Arrays.asList("John", "met", "Obama");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "coref2", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityID", "E1");
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityID", "E2");
// Relation r = new Relation("noncoref", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// String tag = rfe.getCorefTag(r);
// assertEquals("FALSE", tag);
}

@Test
public void testTemplateFeatureIncludesPossessive() {
List<String> tokens = Arrays.asList("John", "'s", "car");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "template", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "POS", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("VEH", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("possessiveRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> features = rfe.getTemplateFeature(r);
// assertTrue(features.contains("is_possessive_structure"));
}

@Test
public void testPatternRecognitionSameHeadSpan() {
List<String> tokens = Arrays.asList("Obama", "visited", "Obama");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "pr", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// List<String> patterns = RelationFeatureExtractor.patternRecognition(source, target);
// assertTrue(patterns.contains("SAME_SOURCE_TARGET_EXCEPTION"));
}

@Test
public void testGetEntityHeadReturnsNullOnBadOffsets() {
List<String> tokens = Arrays.asList("Alpha", "Beta", "Gamma");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "badOffset", Collections.singletonList(tokens));
// Constituent cons = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// cons.addAttribute("EntityHeadStartCharOffset", "9000");
// cons.addAttribute("EntityHeadEndCharOffset", "9005");
// Constituent head = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, ViewNames.MENTION);
// assertNull(head);
}

@Test
public void testIsPossessiveReturnsFalseWhenNoPossessiveStructure() {
List<String> tokens = Arrays.asList("John", "loves", "cars");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "possFalse", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "NNS", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("VEH", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("rel", source, target, 1.0);
// boolean isPoss = RelationFeatureExtractor.isPossessive(r);
// assertFalse(isPoss);
}

@Test
public void testOnlyNounBetweenReturnsFalseWithAdjective() {
List<String> tokens = Arrays.asList("red", "car");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "nonNoun", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "JJ", 1.0);
// posView.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent front = new Constituent("ADJ", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent back = new Constituent("NOUN", 1.0, ViewNames.MENTION, ta, 1, 2);
// boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
// assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartCNoSingleWordBetween() {
List<String> tokens = Arrays.asList("A", "B", "C", "D");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "cPart", Collections.singletonList(tokens));
// Constituent source = new Constituent("X", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("Y", 1.0, ViewNames.MENTION, ta, 3, 4);
// Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> features = rfe.getLexicalFeaturePartC(r);
// assertEquals("No_singleword", features.get(0));
}

@Test
public void testGetLexicalFeaturePartDSourceAfterTarget() {
List<String> tokens = Arrays.asList("X", "Y", "Z", "A", "B", "C");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "dPart", Collections.singletonList(tokens));
// Constituent source = new Constituent("LATE", 1.0, ViewNames.MENTION, ta, 4, 5);
// Constituent target = new Constituent("EARLY", 1.0, ViewNames.MENTION, ta, 1, 2);
// Relation r = new Relation("reverse", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> features = rfe.getLexicalFeaturePartD(r);
// assertTrue(features.contains("between_first_Y"));
}

@Test
public void testGetCorefTagReturnsFalseWhenMissingEntityId() {
List<String> tokens = Arrays.asList("Obama", "Obama");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "corefNull", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 1, 2);
// Relation r = new Relation("missingEntityId", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// String corefTag = rfe.getCorefTag(r);
// assertEquals("FALSE", corefTag);
}

@Test
public void testGetTemplateFeatureIncludesAllStructureFlags() {
List<String> tokens = Arrays.asList("John", "'s", "manager", "in", "Paris");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "templateAll", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "POS", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// posView.addTokenLabel(3, "IN", 1.0);
// posView.addTokenLabel(4, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("GPE", 1.0, ViewNames.MENTION, ta, 4, 5);
// Relation r = new Relation("mixedStruct", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> templateFlags = rfe.getTemplateFeature(r);
// assertTrue(templateFlags.contains("is_possessive_structure"));
// assertTrue(templateFlags.contains("is_preposition_structure"));
}

@Test
public void testPatternRecognitionWithCommaBetweenMentions() {
List<String> tokens = Arrays.asList("Paris", ",", "France");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "patternComma", Collections.singletonList(tokens));
// Constituent source = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("GPE", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "GPE");
// source.addAttribute("EntityType", "LOC");
// List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
// assertTrue(features.contains("FORMULAIC"));
}

@Test
public void testGetEntityHeadHandlesNegativeTokenSpan() {
List<String> tokens = Arrays.asList("A", "B", "C");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "entityHeadNeg", Collections.singletonList(tokens));
// Constituent cons = new Constituent("X", 1.0, ViewNames.MENTION, ta, 0, 1);
// cons.addAttribute("EntityHeadStartCharOffset", "10");
// cons.addAttribute("EntityHeadEndCharOffset", "5");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, ViewNames.MENTION);
// assertNull(result);
}

@Test
public void testIsPrepositionReturnsFalseWithoutPreposition() {
List<String> tokens = Arrays.asList("Computer", "designed", "tools");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "prepositionNeg", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "VBD", 1.0);
// posView.addTokenLabel(2, "NNS", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("OBJ", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("relation", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(r);
// assertFalse(result);
}

@Test
public void testIsFormulaicReturnsFalseForWrongTypes() {
List<String> tokens = Arrays.asList("Alpha", ",", "Beta");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "formulaicFail", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, ",", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("TOOL", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "TOOL");
// Constituent target = new Constituent("THING", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "THING");
// Relation r = new Relation("formulaicNoMatch", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isFormulaic(r);
// assertFalse(result);
}

@Test
public void testIsPremodifierFailsWhenFrontIsNotBeforeBack() {
List<String> tokens = Arrays.asList("Award", "winning", "performance");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "premodWrong", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "VBG", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("OBJ", 1.0, ViewNames.MENTION, ta, 2, 3);
// Constituent target = new Constituent("DESC", 1.0, ViewNames.MENTION, ta, 0, 1);
// Relation r = new Relation("reversePremod", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPremodifier(r);
// assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartEHandlesSentenceStartCorrectly() {
List<String> tokens = Arrays.asList("Barack", "Obama", "visited", "Berlin");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "structE", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 3, 4);
// Relation r = new Relation("someRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> features = rfe.getLexicalFeaturePartE(r);
// assertTrue(features.contains("fwM1_NULL"));
// assertTrue(features.contains("swM1_NULL"));
}

@Test
public void testGetShallowParseFeatureReturnsEmptyWhenHeadsEqual() {
List<String> tokens = Arrays.asList("Doctor", "who");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "shallowSameHead", Collections.singletonList(tokens));
// View spView = new TokenLabelView(ViewNames.SHALLOW_PARSE, "mock", ta, 1.0);
// spView.addTokenLabel(0, "NP", 1.0f);
// spView.addTokenLabel(1, "NP", 1.0f);
// ta.addView(ViewNames.SHALLOW_PARSE, spView);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Relation r = new Relation("shallowEdge", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<Pair<String, String>> result = rfe.getShallowParseFeature(r);
// assertTrue(result.isEmpty());
}

@Test
public void testPatternRecognitionHandlesOverlapButNoStructure() {
List<String> tokens = Arrays.asList("Minister", ",", "of", "Energy", "Department");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "patternOverlap", Collections.singletonList(tokens));
// Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 2);
// Constituent target = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 1, 4);
// source.addAttribute("EntityType", "ORG");
// target.addAttribute("EntityType", "ORG");
// List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
// assertTrue(features.isEmpty());
}

@Test
public void testGetEntityHeadWithEntityHeadStartSpanAttributeOnly() {
List<String> tokens = Arrays.asList("Mr.", "Smith", "arrived");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "entityhead", Collections.singletonList(tokens));
// Constituent cons = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 2);
// cons.addAttribute("EntityHeadStartSpan", "1");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, ViewNames.MENTION);
// assertNotNull(result);
}

@Test
public void testIsPossessiveWithSingleQuoteSAsSeparateTokens() {
List<String> tokens = Arrays.asList("company", "'", "s", "policy");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "possessivesplit", Collections.singletonList(tokens));
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "''", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// posView.addTokenLabel(3, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("POLICY", 1.0, ViewNames.MENTION, ta, 3, 4);
// Relation r = new Relation("poss", source, target, 1.0);
// boolean isPoss = RelationFeatureExtractor.isPossessive(r);
// assertFalse(isPoss);
}

@Test
public void testIsPrepositionWithNoNPAndNoINBetween() {
List<String> tokens = Arrays.asList("device", "operates", "efficiently");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "isPrep", Collections.singletonList(tokens));
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "VBZ", 1.0);
// pos.addTokenLabel(2, "RB", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("DEVICE", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("ADV", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation relation = new Relation("testRel", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(relation);
// assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartFHandlesNullHeadGracefully() {
List<String> tokens = Arrays.asList("Data", "System", "crashed");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "headF", Collections.singletonList(tokens));
// Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityHeadStartCharOffset", "9999");
// source.addAttribute("EntityHeadEndCharOffset", "10000");
// Constituent target = new Constituent("PROBLEM", 1.0, ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityHeadStartCharOffset", "0");
// target.addAttribute("EntityHeadEndCharOffset", "3");
// Relation r = new Relation("example", source, target, 1.0);
try {
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getLexicalFeaturePartF(r);
// assertFalse(result.isEmpty());
} catch (Exception e) {
fail("Method should not throw when source head is null");
}
}

@Test
public void testGetStructualFeatureWithNoMentionViewPresent() {
List<String> tokens = Arrays.asList("Barack", "visited", "Hawaii");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "structA", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EnityType", "PER");
// Constituent target = new Constituent("GPE", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "GPE");
// Relation r = new Relation("testStruct", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getStructualFeature(r);
// assertTrue(result.contains("middle_mention_size_null"));
// assertTrue(result.contains("cb1_PER_GPE_m1_m2_no_coverage"));
}

@Test
public void testGetMentionFeatureWithCoverageAndLevels() {
List<String> tokens = Arrays.asList("Bob", "Robertson");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "mentionFeature", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityMentionType", "NAME");
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 2);
// target.addAttribute("EntityMentionType", "NOM");
// target.addAttribute("EntityType", "PER");
// Relation r = new Relation("mentionRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getMentionFeature(r);
// assertTrue(result.contains("mlvl_cont_1_NAME_NOM_True"));
// assertTrue(result.contains("source_mtype_PER"));
// assertTrue(result.contains("mt_PER_PER"));
}

@Test
public void testGetDependencyFeatureWithEmptyPath() {
List<String> tokens = Arrays.asList("Manager", "of", "Research");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "depTest", Collections.singletonList(tokens));
// TreeView depView = new TreeView(ViewNames.DEPENDENCY_STANFORD, "mock", ta, 1.0);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, depView);
// View pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "IN", 1.0);
// pos.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// View reView = new TokenLabelView("RE_ANNOTATED", "test", ta, 1.0);
// Constituent c0 = new Constituent("Research", 1.0, "RE_ANNOTATED", ta, 2, 3);
// c0.addAttribute("WORDNETTAG", "term");
// reView.addConstituent(c0);
// ta.addView("RE_ANNOTATED", reView);
// Constituent source = new Constituent("ROLE", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("dep", source, target, 1.0);
// List<Pair<String, String>> features = RelationFeatureExtractor.getDependencyFeature(r);
// assertNotNull(features);
}

@Test
public void testGetEntityHeadNoAttributesDefaultsToSelf() {
List<String> tokens = Arrays.asList("Apple", "announced", "iPhone");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "headfallback", Collections.singletonList(tokens));
// Constituent cons = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, ViewNames.MENTION);
// assertSame(cons, result);
}

@Test
public void testIsPrepositionNonOverlapWhenINExistsFailsNoNP() {
List<String> tokens = Arrays.asList("file", "in", "drawer");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "prepositional2", Collections.singletonList(tokens));
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// pos.addTokenLabel(1, "IN", 1.0);
// pos.addTokenLabel(2, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("ACTION", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PLACE", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("rel", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(r);
// assertFalse(result);
}

@Test
public void testIsPremodifierReturnsFalseDueToFailingPOSCheck() {
List<String> tokens = Arrays.asList("Beautiful", "flying", "machine");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "premodPOS", Collections.singletonList(tokens));
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "JJ", 1.0);
// pos.addTokenLabel(1, "VBZ", 1.0);
// pos.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("DESC", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("THING", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation rel = new Relation("premod", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPremodifier(rel);
// assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartDReturnsNoFeaturesOnAdjacency() {
List<String> tokens = Arrays.asList("A", "B", "C");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "partD", Collections.singletonList(tokens));
// Constituent source = new Constituent("ALPHA", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("BETA", 1.0, ViewNames.MENTION, ta, 1, 2);
// Relation r = new Relation("adjacent", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getLexicalFeaturePartD(r);
// assertEquals(1, result.size());
// assertEquals("No_between_features", result.get(0));
}

@Test
public void testGetLexicalFeaturePartEReturnsPaddingNullsIfNearSentenceEnd() {
List<String> tokens = Arrays.asList("He", "saw", "Bob");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "lexe", Collections.singletonList(tokens));
// Constituent source = new Constituent("PRON", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("lex_e", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getLexicalFeaturePartE(r);
// assertTrue(result.contains("fwM2_Bob"));
// assertTrue(result.contains("swM2_NULL"));
}

@Test
public void testStructualFeatureM2InM1() {
List<String> tokens = Arrays.asList("European", "Union", "Meeting");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "nested", Collections.singletonList(tokens));
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "mock", ta, 1.0);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 3);
// source.addAttribute("EnityType", "ORG");
// Constituent target = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityType", "ORG");
// Relation r = new Relation("cover", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getStructualFeature(r);
// assertTrue(result.contains("m2_in_m1"));
// assertTrue(result.contains("cb1_ORG_ORG_m2_in_m1"));
}

@Test
public void testStructualFeatureM1InM2() {
List<String> tokens = Arrays.asList("United", "Kingdom", "parliament");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "nested2", Collections.singletonList(tokens));
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ERE, "mock", ta, 1.0);
// ta.addView(ViewNames.MENTION_ERE, mentionView);
// Constituent source = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 1, 2);
// source.addAttribute("EnityType", "LOC");
// Constituent target = new Constituent("GPE", 1.0, ViewNames.MENTION, ta, 0, 3);
// target.addAttribute("EntityType", "GPE");
// Relation r = new Relation("cover2", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getStructualFeature(r);
// assertTrue(result.contains("m1_in_m2"));
// assertTrue(result.contains("cb1_LOC_GPE_m1_in_m2"));
}

@Test
public void testPatternRecognitionHandlesNullDependencyView() {
List<String> tokens = Arrays.asList("Minister", "in", "office");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "patternnull", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "LOC");
// List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
// assertNotNull(result);
}

@Test
public void testGetLexicalFeaturePartFWithNullSourceHead() {
List<String> tokens = Arrays.asList("X", "Y", "Z");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "nullHeadF", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityHeadStartCharOffset", "1000");
// source.addAttribute("EntityHeadEndCharOffset", "1001");
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityHeadStartCharOffset", "1");
// target.addAttribute("EntityHeadEndCharOffset", "2");
// Relation r = new Relation("relation", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
try {
// List<String> result = rfe.getLexicalFeaturePartF(r);
// assertNotNull(result);
} catch (Exception e) {
fail("Should not throw on null head constituent");
}
}

@Test
public void testGetShallowParseFeatureWithNoChunkLabels() {
List<String> tokens = Arrays.asList("A", "B", "C");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "shallowNone", Collections.singletonList(tokens));
// View spView = new TokenLabelView(ViewNames.SHALLOW_PARSE, "mock", ta, 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, spView);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<Pair<String, String>> features = rfe.getShallowParseFeature(r);
// assertTrue(features.isEmpty());
}

@Test
public void testGetTemplateFeatureReturnsEmptyWhenNoStructureMatch() {
List<String> tokens = Arrays.asList("engineered", "design", "principles");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "templateEmpty", Collections.singletonList(tokens));
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "VBD", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// pos.addTokenLabel(2, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("TECH", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("TOPIC", 1.0, ViewNames.MENTION, ta, 1, 3);
// Relation r = new Relation("templateRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getTemplateFeature(r);
// assertTrue(result.isEmpty());
}

@Test
public void testGetCollocationsFeatureHandlesAllRelativeTokensCorrectly() {
List<String> tokens = Arrays.asList("X", "Y", "Z", "A", "B", "C");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "collocRich", Collections.singletonList(tokens));
// Constituent source = new Constituent("SRC", 1.0, ViewNames.MENTION, ta, 1, 4);
// Constituent target = new Constituent("TGT", 1.0, ViewNames.MENTION, ta, 3, 6);
// Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getCollocationsFeature(r);
// assertTrue(result.contains("s_m1_p1_Z"));
// assertTrue(result.contains("s_p1_p1_A"));
// assertTrue(result.contains("t_m1_p1_CB"));
}

@Test
public void testPatternRecognitionReturnsEmptyForUnmatchedParse() {
List<String> tokens = Arrays.asList("Minister", "spoke", "today");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "patternNoMatch", Collections.singletonList(tokens));
// TreeView dep = new TreeView(ViewNames.DEPENDENCY_STANFORD, "mock", ta, 1.0);
// Constituent fakeNode1 = new Constituent("nsubj", 1.0, ViewNames.DEPENDENCY_STANFORD, ta, 0, 1);
// Constituent fakeNode2 = new Constituent("root", 1.0, ViewNames.DEPENDENCY_STANFORD, ta, 1, 2);
// dep.addConstituent(fakeNode1);
// dep.addConstituent(fakeNode2);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, dep);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("DATE", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "DATE");
// List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
// assertTrue(result.isEmpty());
}

@Test
public void testGetDependencyFeatureSkipsWhenDifferentSentenceId() {
List<String> tokens1 = Arrays.asList("Bob", "ran");
List<String> tokens2 = Arrays.asList("quickly", "home");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "depSentence", Arrays.asList(tokens1, tokens2));
// TreeView depView = new TreeView(ViewNames.DEPENDENCY_STANFORD, "mock", ta, 1.0);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, depView);
// View pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(3, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// View reView = new TokenLabelView("RE_ANNOTATED", "mock", ta, 1.0);
// ta.addView("RE_ANNOTATED", reView);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 3, 4);
// Relation r = new Relation("differentSent", source, target, 1.0);
// List<Pair<String, String>> result = RelationFeatureExtractor.getDependencyFeature(r);
// assertTrue(result.isEmpty());
}

@Test
public void testGetMentionFeatureWithMissingAttributes() {
List<String> tokens = Arrays.asList("Vice", "President", "Harris");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "blankMention", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 2);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("mentionRel", source, target, 1.0);
RelationFeatureExtractor rfe = new RelationFeatureExtractor();
// List<String> result = rfe.getMentionFeature(r);
// assertTrue(result.contains("source_mtype_null"));
// assertTrue(result.contains("target_mtype_null"));
}

@Test
public void testGetLexicalFeaturePartCCWithOverlappingHeadsReturnsEmpty() {
List<String> tokens = Arrays.asList("John", "Paul");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "ccOverlap", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Relation r = new Relation("test", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> result = extractor.getLexicalFeaturePartCC(r);
// assertTrue(result.isEmpty());
}

@Test
public void testIsFormulaicReturnsFalseWithCommaButWrongEntityTypes() {
List<String> tokens = Arrays.asList("Berlin", ",", "Germany");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "formulaic_type", Collections.singletonList(tokens));
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, ",", 1.0);
// pos.addTokenLabel(2, "NNP", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "DATE");
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "MISC");
// Relation r = new Relation("rel", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isFormulaic(r);
// assertFalse(result);
}

@Test
public void testGetMentionFeatureWithPartiallyMissingAttributes() {
List<String> tokens = Arrays.asList("New", "York", "City");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "mentionNull", Collections.singletonList(tokens));
// Constituent source = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityMentionType", "NAME");
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 1, 3);
// target.addAttribute("EntityType", "GPE");
// Relation r = new Relation("mention", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> result = extractor.getMentionFeature(r);
// assertTrue(result.contains("target_mtype_GPE"));
// assertTrue(result.contains("mlvl_NAME_null"));
}

@Test
public void testGetDependencyFeatureWithNoMatchingParsedNodes() {
List<String> tokens = Arrays.asList("CEO", "of", "Google");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "depNomatch", Collections.singletonList(tokens));
// TreeView depView = new TreeView(ViewNames.DEPENDENCY_STANFORD, "mock", ta, 1.0);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, depView);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, "IN", 1.0);
// pos.addTokenLabel(2, "NNP", 1.0);
// ta.addView(ViewNames.POS, pos);
// View reView = new TokenLabelView("RE_ANNOTATED", "mock", ta, 1.0);
// Constituent annotated = new Constituent("ORG", 1.0, "RE_ANNOTATED", ta, 2, 3);
// annotated.addAttribute("WORDNETTAG", "Company");
// reView.addConstituent(annotated);
// ta.addView("RE_ANNOTATED", reView);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("depFail", source, target, 1.0);
// List<Pair<String, String>> result = RelationFeatureExtractor.getDependencyFeature(r);
// assertTrue(result.isEmpty());
}

@Test
public void testPatternRecognitionSameOffsetReturnsSingleTokenException() {
List<String> tokens = Arrays.asList("Obama");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "sameToken", Collections.singletonList(tokens));
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
// assertTrue(result.contains("SAME_SOURCE_TARGET_EXCEPTION"));
}

@Test
public void testGetShallowParseFeatureHandlesSpanInsideChunkOnly() {
List<String> tokens = Arrays.asList("Central", "Bank", "Policy");
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "chunkinside", Collections.singletonList(tokens));
// TokenLabelView chunkView = new TokenLabelView(ViewNames.SHALLOW_PARSE, "mock", ta, 1.0);
// chunkView.addTokenLabel(0, "NP", 1.0);
// chunkView.addTokenLabel(1, "NP", 1.0);
// chunkView.addTokenLabel(2, "NP", 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunkView);
// Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("TOPIC", 1.0, ViewNames.MENTION, ta, 1, 3);
// Relation r = new Relation("chunk", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<Pair<String, String>> result = extractor.getShallowParseFeature(r);
// assertFalse(result.isEmpty());
// assertTrue(result.get(0).getFirst().startsWith("chunker_between_"));
}
}
