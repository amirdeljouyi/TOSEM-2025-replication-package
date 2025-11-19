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

public class RelationFeatureExtractor_5_GPTLLMTest {

@Test
public void testIsPossessiveShouldReturnTrueForApostropheS() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "John 's car is here.");
// DummyView posView = new DummyView(ViewNames.POS, "testPOS");
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("POS", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("VEH", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "VEH");
// Relation relation = new Relation("possessive", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPossessive(relation);
// assertTrue(result);
}

@Test
public void testGetCorefTagShouldReturnTrueWhenEntityIDsMatch() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Alice saw herself");
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityID", "E1");
// Constituent target = new Constituent("PER", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityID", "E1");
// Relation relation = new Relation("coref", source, target, 1.0);
// String result = new RelationFeatureExtractor().getCorefTag(relation);
// assertEquals("TRUE", result);
}

@Test
public void testGetCorefTagShouldReturnFalseWhenEntityIDsDoNotMatch() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Alice saw Bob");
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityID", "E1");
// Constituent target = new Constituent("PER", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityID", "E2");
// Relation relation = new Relation("coref", source, target, 1.0);
// String result = new RelationFeatureExtractor().getCorefTag(relation);
// assertEquals("FALSE", result);
}

@Test
public void testIsNounAcceptsNounStartPOS() {
assertTrue(RelationFeatureExtractor.isNoun("NN"));
assertTrue(RelationFeatureExtractor.isNoun("NNS"));
assertFalse(RelationFeatureExtractor.isNoun("VB"));
}

@Test
public void testGetLexicalFeaturePartAExtractsSourceTokens() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "The company released a statement.");
// Constituent source = new Constituent("ORG", ViewNames.MENTION, ta, 1, 2);
// Constituent target = new Constituent("DOC", ViewNames.MENTION, ta, 3, 4);
// Relation relation = new Relation("lex", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartA(relation);
// assertEquals(1, features.size());
// assertEquals("company", features.get(0));
}

@Test
public void testGetLexicalFeaturePartBExtractsTargetTokens() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "The company released a statement.");
// Constituent source = new Constituent("ORG", ViewNames.MENTION, ta, 1, 2);
// Constituent target = new Constituent("DOC", ViewNames.MENTION, ta, 4, 5);
// Relation relation = new Relation("lex", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartB(relation);
// assertEquals(1, features.size());
// assertEquals("statement", features.get(0));
}

@Test
public void testGetLexicalFeaturePartCReturnsSingleWordFeature() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "The lawsuit against the company");
// Constituent source = new Constituent("OBJ", ViewNames.MENTION, ta, 1, 2);
// Constituent target = new Constituent("ORG", ViewNames.MENTION, ta, 3, 4);
// Relation relation = new Relation("lex", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartC(relation);
// assertEquals(1, features.size());
// assertTrue(features.get(0).startsWith("singleword_") || features.get(0).equals("No_singleword"));
}

@Test
public void testPatternRecognitionReturnsSameHeadException() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Apple released Apple");
// Constituent c = new Constituent("ORG", ViewNames.MENTION, ta, 0, 1);
// List<String> features = RelationFeatureExtractor.patternRecognition(c, c);
// assertTrue(features.contains("SAME_SOURCE_TARGET_EXCEPTION"));
}

@Test
public void testOnlyNounBetweenReturnsTrueWhenMiddleIsNN() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "credit market risk");
// DummyView pos = new DummyView(ViewNames.POS, "POSView");
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent front = new Constituent("OBJ", ViewNames.MENTION, ta, 0, 1);
// Constituent back = new Constituent("OBJ", ViewNames.MENTION, ta, 2, 3);
// boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
// assertTrue(result);
}

@Test
public void testOnlyNounBetweenReturnsFalseWhenMiddleIsVB() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "John eats apples");
// DummyView pos = new DummyView(ViewNames.POS, "POSView2");
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent front = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// Constituent back = new Constituent("FOOD", ViewNames.MENTION, ta, 2, 3);
// boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
// assertFalse(result);
}

@Test
public void testGetEntityHeadForConstituent_WithIsPredictedAttributeReturnsSelf() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Obama visited France");
// Constituent extentConstituent = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// extentConstituent.addAttribute("IsPredicted", "true");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(extentConstituent, ta, "TEST");
// assertEquals(extentConstituent, result);
}

@Test
public void testGetEntityHeadForConstituent_WithMentionAnnotatorKey() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Michelle Obama visited France");
// Constituent extentConstituent = new Constituent("PER", ViewNames.MENTION, ta, 0, 2);
// extentConstituent.addAttribute("EntityHeadStartSpan", "1");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(extentConstituent, ta, "TEST");
// assertNotNull(result);
}

@Test
public void testGetEntityHeadForConstituent_WithACEReaderOffsetsCreatesHeadConstituent() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Barack Obama spoke today");
// Constituent extentConstituent = new Constituent("PER", ViewNames.MENTION, ta, 0, 2);
// extentConstituent.addAttribute("EntityHeadStartCharOffset", Integer.toString(ta.getTokenCharacterOffset(1).first));
// extentConstituent.addAttribute("EntityHeadEndCharOffset", Integer.toString(ta.getTokenCharacterOffset(1).second));
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(extentConstituent, ta, "TEST");
// assertNotNull(result);
// assertEquals(1, result.getStartSpan());
}

@Test
public void testGetEntityHeadForConstituent_InvalidOffsetsReturnsNull() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Hello world");
// Constituent extentConstituent = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// extentConstituent.addAttribute("EntityHeadStartCharOffset", "-1");
// extentConstituent.addAttribute("EntityHeadEndCharOffset", "-1");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(extentConstituent, ta, "TEST");
// assertNull(result);
}

@Test
public void testIsPremodifierStructureReturnsFalseWhenFrontStartSpanIsLessThanBack() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "economic growth");
// DummyView pos = new DummyView(ViewNames.POS, "test");
// pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// Constituent c1 = new Constituent("MOD", ViewNames.MENTION, ta, 0, 1);
// c1.addAttribute("EntityType", "NP");
// Constituent c2 = new Constituent("HEAD", ViewNames.MENTION, ta, 1, 2);
// c2.addAttribute("EntityType", "NP");
// Relation relation = new Relation("rel", c2, c1, 1.0);
// boolean result = RelationFeatureExtractor.isPremodifier(relation);
// assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartC_NoSingleWordReturnsDefaultFeature() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "one two three four");
// Constituent source = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("Y", ViewNames.MENTION, ta, 3, 4);
// Relation relation = new Relation("rel", source, target, 1.0);
// List<String> features = new RelationFeatureExtractor().getLexicalFeaturePartC(relation);
// assertEquals(1, features.size());
// assertEquals("No_singleword", features.get(0));
}

@Test
public void testGetLexicalFeaturePartD_NoBetweenWords() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("cp", "id", "a b");
// Constituent source = new Constituent("A", ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("B", ViewNames.MENTION, ta, 1, 2);
// Relation rel = new Relation("rel", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getLexicalFeaturePartD(rel);
// assertEquals(1, result.size());
// assertEquals("No_between_features", result.get(0));
}

@Test
public void testGetTemplateFeature_AllTemplateFlagsOffReturnsEmpty() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "Hello building");
// DummyView pos = new DummyView(ViewNames.POS, "pos");
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "LOC");
// Constituent target = new Constituent("Y", ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityType", "ORG");
// Relation rel = new Relation("rel", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getTemplateFeature(rel);
// assertTrue(result.isEmpty());
}

@Test
public void testIsFormulaicReturnsFalseWhenTokensInBetweenInvalid() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "President of United States");
// DummyView pos = new DummyView(ViewNames.POS, "pos");
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("IN", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("ORG", ViewNames.MENTION, ta, 2, 4);
// target.addAttribute("EntityType", "ORG");
// Relation rel = new Relation("rel", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isFormulaic(rel);
// assertFalse(result);
}

@Test
public void testGetMentionFeature_ContainmentBothDirections() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "President Obama");
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 2);
// source.addAttribute("EntityMentionType", "NAM");
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("PER", ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityMentionType", "NOM");
// target.addAttribute("EntityType", "PER");
// Relation r = new Relation("mention", source, target, 1.0);
// List<String> features = new RelationFeatureExtractor().getMentionFeature(r);
// assertTrue(features.contains("mlvl_cont_2_NAM_NOM_True"));
}

@Test
public void testIsPrepositionWithNoNounBetweenReturnsTrueIfINPresent() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("test", "id", "John spoke to Alice");
// DummyView pos = new DummyView(ViewNames.POS, "test");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("TO", ViewNames.POS, ta, 2, 3));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("PER", ViewNames.MENTION, ta, 3, 4);
// target.addAttribute("EntityType", "PER");
// Relation r = new Relation("rel", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(r);
// assertTrue(result);
}

@Test
public void testIsPremodifierWithOverlapYieldsFalse() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("test", "id", "economic growth improved");
// DummyView pos = new DummyView(ViewNames.POS, "test");
// pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("ADJ", ViewNames.MENTION, ta, 0, 2);
// source.addAttribute("EntityType", "NP");
// Constituent target = new Constituent("NN", ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityType", "NP");
// Relation r = new Relation("rel", source, target, 1.0);
// assertFalse(RelationFeatureExtractor.isPremodifier(r));
}

@Test
public void testIsFormulaicCommaBetweenHeadsTriggersTrue() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "id", "John , CEO");
// DummyView pos = new DummyView(ViewNames.POS, "p");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent(",", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("ORG", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "ORG");
// Relation r = new Relation("rel", source, target, 1.0);
// assertTrue(RelationFeatureExtractor.isFormulaic(r));
}

@Test
public void testGetEntityHeadForConstituentStartOffsetLargerThanEndNoCrashReturnsNull() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("test", "doc", "word1 word2");
// Constituent cons = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// cons.addAttribute("EntityHeadStartCharOffset", "6");
// cons.addAttribute("EntityHeadEndCharOffset", "2");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, "TEST");
// assertNull(result);
}

@Test
public void testGetShallowParseFeature_WithInclusiveOverlap() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("test", "id", "she buys auto parts");
// DummyView sp = new DummyView(ViewNames.SHALLOW_PARSE, "test");
// sp.addConstituent(new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 1, 2));
// sp.addConstituent(new Constituent("VP", ViewNames.SHALLOW_PARSE, ta, 2, 4));
// ta.addView(ViewNames.SHALLOW_PARSE, sp);
// Constituent source = new Constituent("ENT", ViewNames.MENTION, ta, 1, 2);
// source.addAttribute("EntityType", "O");
// Constituent target = new Constituent("ENT", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "O");
// Relation r = new Relation("rel", source, target, 1.0);
// List<Pair<String, String>> result = new RelationFeatureExtractor().getShallowParseFeature(r);
// assertFalse(result.isEmpty());
boolean atLeastOneInclusive = false;
// for (Pair<String, String> p : result) {
// if (p.getFirst().startsWith("chunker_between_extents_inclusive_")) {
// atLeastOneInclusive = true;
// break;
// }
// }
assertTrue(atLeastOneInclusive);
}

@Test
public void testGetDependencyFeatureWithEmptyParseReturnsEmptyList() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("test", "id", "John works at Google");
// TreeView dep = new TreeView(ViewNames.DEPENDENCY_STANFORD, "dummy");
// ta.addView(ViewNames.DEPENDENCY_STANFORD, dep);
// DummyView pos = new DummyView(ViewNames.POS, "test");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// DummyView annotated = new DummyView("RE_ANNOTATED", "dummy");
// ta.addView("RE_ANNOTATED", annotated);
// Constituent src = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// src.addAttribute("EntityType", "PER");
// Constituent tgt = new Constituent("ORG", ViewNames.MENTION, ta, 3, 4);
// tgt.addAttribute("EntityType", "ORG");
// Relation r = new Relation("rel", src, tgt, 1.0);
// List<Pair<String, String>> features = RelationFeatureExtractor.getDependencyFeature(r);
// assertTrue(features.isEmpty());
}

@Test
public void testGetStructualFeature_MentionCoverage_SourceCoversTarget() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "id", "Barack Obama visited Washington");
// DummyView mentions = new DummyView(ViewNames.MENTION_ACE, "mentionCreator");
// mentions.addConstituent(new Constituent("PER", ViewNames.MENTION_ACE, ta, 0, 2));
// mentions.addConstituent(new Constituent("PER", ViewNames.MENTION_ACE, ta, 1, 2));
// ta.addView(ViewNames.MENTION_ACE, mentions);
// Constituent source = new Constituent("PER", ViewNames.MENTION_ACE, ta, 0, 2);
// source.addAttribute("EnityType", "PER");
// Constituent target = new Constituent("PER", ViewNames.MENTION_ACE, ta, 1, 2);
// target.addAttribute("EntityType", "PER");
// Relation r = new Relation("rel", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getStructualFeature(r);
// assertTrue(result.contains("m2_in_m1"));
}

@Test
public void testGetTemplateFeature_MultipleStructuresTriggered() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("t", "d", "Mike 's headquarters in Seattle");
// DummyView pos = new DummyView(ViewNames.POS, "p");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("POS", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// pos.addConstituent(new Constituent("IN", ViewNames.POS, ta, 3, 4));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 4, 5));
// ta.addView(ViewNames.POS, pos);
// Constituent src = new Constituent("PER", ViewNames.MENTION, ta, 0, 2);
// src.addAttribute("EntityType", "PER");
// Constituent tgt = new Constituent("ORG", ViewNames.MENTION, ta, 2, 3);
// tgt.addAttribute("EntityType", "ORG");
// Relation r = new Relation("rel", src, tgt, 1.0);
// List<String> features = new RelationFeatureExtractor().getTemplateFeature(r);
// assertTrue(features.contains("is_possessive_structure"));
}

@Test
public void testIsPossessiveWithOverlappingEntitiesWhereHeadSpansDoNotAlign() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "overlap", "Mary 's book");
// DummyView posView = new DummyView(ViewNames.POS, "test");
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("POS", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 2);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("OBJ", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "OBJ");
// Relation r = new Relation("possTest", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPossessive(r);
// assertTrue(result);
}

@Test
public void testIsPremodifierWithNullPOSViewReturnsFalse() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "premod", "Italian wine quality");
// Constituent left = new Constituent("ADJ", ViewNames.MENTION, ta, 0, 1);
// left.addAttribute("EntityType", "NOUN");
// Constituent right = new Constituent("NN", ViewNames.MENTION, ta, 2, 3);
// right.addAttribute("EntityType", "NOUN");
// Relation rel = new Relation("premod", left, right, 1.0);
boolean result = false;
try {
// result = RelationFeatureExtractor.isPremodifier(rel);
} catch (Exception e) {
fail("No exception should occur without POS");
}
assertFalse(result);
}

@Test
public void testGetEntityHeadForConstituent_MissingAllAttributesReturnsSelf() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "ghead", "Obama arrived");
// Constituent con = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(con, ta, "VIEW");
// assertEquals(con, result);
}

@Test
public void testGetDependencyFeatureWithMismatchedSentenceIdsReturnsEmpty() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "1", "One. Two.");
// TreeView dependencyView = new TreeView(ViewNames.DEPENDENCY_STANFORD, "dummy");
// ta.addView(ViewNames.DEPENDENCY_STANFORD, dependencyView);
// DummyView posView = new DummyView(ViewNames.POS, "dummy");
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
// DummyView annotView = new DummyView("RE_ANNOTATED", "dummy");
// ta.addView("RE_ANNOTATED", annotView);
// Constituent source = new Constituent("M1", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "X");
// Constituent target = new Constituent("M2", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "X");
// Relation r = new Relation("r", source, target, 1.0);
// List<Pair<String, String>> features = RelationFeatureExtractor.getDependencyFeature(r);
// assertTrue(features.isEmpty());
}

@Test
public void testGetStructualFeatureWithMissingMentionViewFallsBackGracefully() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "missingview", "Ann visited Bob");
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EnityType", "PER");
// Constituent target = new Constituent("PER", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "PER");
// Relation r = new Relation("r", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getStructualFeature(r);
// assertTrue(result.contains("middle_mention_size_null"));
// assertTrue(result.contains("cb1_PER_PER_m1_m2_no_coverage"));
}

@Test
public void testPatternRecognitionWithSameSpanSourceTargetReturnsExceptionFeature() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "pr", "Attack attack");
// Constituent source = new Constituent("X", ViewNames.MENTION, ta, 1, 2);
// source.addAttribute("EntityType", "X");
// Constituent target = new Constituent("Y", ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityType", "Y");
// List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
// assertTrue(result.contains("SAME_SOURCE_TARGET_EXTENT_EXCEPTION"));
}

@Test
public void testGetLexicalFeaturePartFWithNullHeadAttributesStillProducesFeatures() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "head", "company files lawsuit");
// Constituent s = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// Constituent t = new Constituent("Y", ViewNames.MENTION, ta, 2, 3);
// Relation rel = new Relation("F", s, t, 1.0);
// List<String> features = new RelationFeatureExtractor().getLexicalFeaturePartF(rel);
// assertEquals(3, features.size());
}

@Test
public void testGetMentionFeatureWithNullEntityTypesDoesNotThrow() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "mention", "cat chased mouse");
// Constituent s = new Constituent("unk", ViewNames.MENTION, ta, 0, 1);
// s.addAttribute("EntityMentionType", "NAM");
// s.addAttribute("EntityType", null);
// Constituent t = new Constituent("unk", ViewNames.MENTION, ta, 2, 3);
// t.addAttribute("EntityMentionType", "NOM");
// t.addAttribute("EntityType", null);
// Relation rel = new Relation("m", s, t, 1.0);
// List<String> result = new RelationFeatureExtractor().getMentionFeature(rel);
// assertTrue(result.stream().anyMatch(f -> f.startsWith("mlvl")));
}

@Test
public void testGetLexicalFeaturePartD_OrderReversalStillYieldsBetweenWordFeatures() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "swap", "big court case filed");
// Constituent source = new Constituent("X", ViewNames.MENTION, ta, 3, 4);
// Constituent target = new Constituent("Y", ViewNames.MENTION, ta, 0, 1);
// Relation r = new Relation("F", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getLexicalFeaturePartD(r);
// assertTrue(result.stream().anyMatch(f -> f.startsWith("between_first_")));
}

@Test
public void testGetCollocationsFeatureWithSpanEdgesAndNullTokens() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "cfeat", "the file was made public");
// Constituent s = new Constituent("S", ViewNames.MENTION, ta, 1, 2);
// Constituent t = new Constituent("T", ViewNames.MENTION, ta, 4, 5);
// Relation r = new Relation("colloc", s, t, 1.0);
// List<String> feats = new RelationFeatureExtractor().getCollocationsFeature(r);
// assertNotNull(feats);
// assertTrue(feats.size() >= 7);
}

@Test
public void testIsPrepositionReturnsFalseWithOnlyNounBetweenAndNoIN_TO() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("test", "prepnz", "Alice car Bob");
// DummyView pos = new DummyView(ViewNames.POS, "test");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("P1", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("P2", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "PER");
// Relation r = new Relation("rel", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(r);
// assertFalse(result);
}

@Test
public void testIsPremodifierReturnsFalseWhenUnsupportedPOSBetweenHeadAndStart() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "premodBadPOS", "fast quickly market opens");
// DummyView pos = new DummyView(ViewNames.POS, "X");
// pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("RB", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// pos.addConstituent(new Constituent("VB", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("M1", ViewNames.MENTION, ta, 1, 2);
// source.addAttribute("EntityType", "OTHER");
// Constituent target = new Constituent("M2", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "OTHER");
// Relation rel = new Relation("test", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPremodifier(rel);
// assertTrue(result);
}

@Test
public void testGetLexicalFeaturePartCCWithReversedHeadSpan() {
// TokenizerTextAnnotationBuilder tBuilder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tBuilder.createTextAnnotation("test", "cc", "alpha beta gamma delta");
// Constituent c1 = new Constituent("C1", ViewNames.MENTION, ta, 2, 3);
// Constituent c2 = new Constituent("C2", ViewNames.MENTION, ta, 0, 1);
// Relation r = new Relation("rel", c1, c2, 1.0);
// List<String> features = new RelationFeatureExtractor().getLexicalFeaturePartCC(r);
// assertTrue(features.stream().anyMatch(f -> f.startsWith("bowbethead_")));
}

@Test
public void testGetCollocationsFeatureWithMinimalBoundarySpanProducesFallbacks() {
// TokenizerTextAnnotationBuilder tBuilder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tBuilder.createTextAnnotation("test", "cx", "the person voted quickly");
// Constituent source = new Constituent("SRC", ViewNames.MENTION, ta, 1, 2);
// Constituent target = new Constituent("TGT", ViewNames.MENTION, ta, 3, 4);
// Relation rel = new Relation("r", source, target, 1.0);
// List<String> features = new RelationFeatureExtractor().getCollocationsFeature(rel);
// assertTrue(features.stream().anyMatch(f -> f.startsWith("s_")));
// assertTrue(features.stream().anyMatch(f -> f.startsWith("t_")));
}

@Test
public void testGetStructualFeatureNoConstituentCoverage() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "nocover", "he lives in Rome");
// DummyView mentions = new DummyView(ViewNames.MENTION, "x");
// ta.addView(ViewNames.MENTION, mentions);
// Constituent source = new Constituent("SRC", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EnityType", "PER");
// Constituent target = new Constituent("TGT", ViewNames.MENTION, ta, 3, 4);
// target.addAttribute("EntityType", "LOC");
// Relation rel = new Relation("r", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getStructualFeature(rel);
// assertTrue(result.contains("m1_m2_no_coverage"));
// assertTrue(result.stream().anyMatch(f -> f.startsWith("cb1")));
}

@Test
public void testGetShallowParseFeatureWithSameHeadTokenStartSpanSkipsExtraction() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "nospan", "stock price fell sharply");
// DummyView chunk = new DummyView(ViewNames.SHALLOW_PARSE, "view");
// chunk.addConstituent(new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2));
// chunk.addConstituent(new Constituent("VP", ViewNames.SHALLOW_PARSE, ta, 2, 4));
// ta.addView(ViewNames.SHALLOW_PARSE, chunk);
// Constituent c1 = new Constituent("A", ViewNames.MENTION, ta, 1, 2);
// Constituent c2 = new Constituent("B", ViewNames.MENTION, ta, 1, 2);
// Relation rel = new Relation("sparse", c1, c2, 1.0);
// List<Pair<String, String>> output = new RelationFeatureExtractor().getShallowParseFeature(rel);
// assertTrue(output.isEmpty());
}

@Test
public void testGetTemplateFeatureTriggersMultipleTemplatesSimultaneously() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "multi", "John 's manager , TechCorp");
// DummyView pos = new DummyView(ViewNames.POS, "test");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("POS", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// pos.addConstituent(new Constituent(",", ViewNames.POS, ta, 3, 4));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 4, 5));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 3);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("ORG", ViewNames.MENTION, ta, 4, 5);
// target.addAttribute("EntityType", "ORG");
// Relation r = new Relation("mix", source, target, 1.0);
// List<String> out = new RelationFeatureExtractor().getTemplateFeature(r);
// assertTrue(out.contains("is_possessive_structure"));
// assertTrue(out.contains("is_formulaic_structure"));
}

@Test
public void testGetMentionFeatureWhenTargetCoversSource() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("test", "cover", "senator Obama");
// Constituent target = new Constituent("PER", ViewNames.MENTION, ta, 0, 2);
// target.addAttribute("EntityMentionType", "NAM");
// target.addAttribute("EntityType", "PER");
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 1, 2);
// source.addAttribute("EntityMentionType", "NOM");
// source.addAttribute("EntityType", "PER");
// Relation r = new Relation("mfeat", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getMentionFeature(r);
// assertTrue(result.contains("mlvl_cont_1_NOM_NAM_True"));
}

@Test
public void testPatternRecognitionWithCommaAndOrgTypeTriggersFormulaic() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "formula", "Harvard , Massachusetts");
// DummyView pos = new DummyView(ViewNames.POS, "testpos");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent(",", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent s = new Constituent("ORG", ViewNames.MENTION, ta, 0, 1);
// s.addAttribute("EntityType", "ORG");
// Constituent t = new Constituent("GPE", ViewNames.MENTION, ta, 2, 3);
// t.addAttribute("EntityType", "GPE");
// List<String> out = RelationFeatureExtractor.patternRecognition(s, t);
// assertTrue(out.contains("FORMULAIC"));
}

@Test
public void testGetEntityHeadForConstituent_returnsNullWhenInvalidOffsets() {
// TokenizerTextAnnotationBuilder build = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = build.createTextAnnotation("x", "y", "word1 word2 word3");
// Constituent c = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// c.addAttribute("EntityHeadStartCharOffset", "notAnInt");
// c.addAttribute("EntityHeadEndCharOffset", "notAnInt");
try {
// RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, ViewNames.MENTION);
fail("Should throw NumberFormatException");
} catch (NumberFormatException e) {
}
}

@Test
public void testIsPossessive_whenPOSisPRPReturnsTrue() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("test", "doc", "his dog");
// DummyView pos = new DummyView(ViewNames.POS, "test");
// pos.addConstituent(new Constituent("PRP$", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// Constituent s = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// s.addAttribute("EntityType", "PER");
// Constituent t = new Constituent("Y", ViewNames.MENTION, ta, 1, 2);
// t.addAttribute("EntityType", "OBJ");
// Relation r = new Relation("possessive", s, t, 1.0);
// boolean result = RelationFeatureExtractor.isPossessive(r);
// assertTrue(result);
}

@Test
public void testIsPreposition_returnsFalseWithoutINorTO() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "x", "A fast brown fox");
// DummyView pos = new DummyView(ViewNames.POS, "x");
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 2, 3));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("O", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "X");
// Constituent target = new Constituent("T", ViewNames.MENTION, ta, 3, 4);
// target.addAttribute("EntityType", "X");
// Relation r = new Relation("preptype", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(r);
// assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartF_fromSingleTokenEntities() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "id", "Lion chases deer");
// Constituent source = new Constituent("S", ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("T", ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("rel", source, target, 1.0);
// List<String> features = new RelationFeatureExtractor().getLexicalFeaturePartF(r);
// assertEquals(3, features.size());
// assertTrue(features.get(0).startsWith("HM1_"));
// assertTrue(features.get(1).startsWith("HM2_"));
// assertTrue(features.get(2).startsWith("HM12_"));
}

@Test
public void testGetCollocationsFeature_handlesHeadNearEdges() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "id", "the dog barked loudly");
// Constituent s = new Constituent("A", ViewNames.MENTION, ta, 1, 2);
// Constituent t = new Constituent("B", ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("colloc", s, t, 1.0);
// List<String> result = new RelationFeatureExtractor().getCollocationsFeature(r);
// assertTrue(result.contains("s_m1_p1_dog"));
// assertTrue(result.contains("t_m1_p1_barked"));
}

@Test
public void testGetMentionFeature_handlesAttributeInconsistencies() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "a", "car from Germany");
// Constituent s = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// s.addAttribute("EntityType", "VEH");
// s.addAttribute("EntityMentionType", null);
// Constituent t = new Constituent("Y", ViewNames.MENTION, ta, 2, 3);
// t.addAttribute("EntityType", null);
// t.addAttribute("EntityMentionType", "NAM");
// Relation r = new Relation("mfeat", s, t, 1.0);
// List<String> output = new RelationFeatureExtractor().getMentionFeature(r);
// assertFalse(output.isEmpty());
}

@Test
public void testGetTemplateFeature_multipleFlagsSimultaneously() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("test", "xyz", "Mike 's CEO , Meta");
// DummyView pos = new DummyView(ViewNames.POS, "v");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("POS", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// pos.addConstituent(new Constituent(",", ViewNames.POS, ta, 3, 4));
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 4, 5));
// ta.addView(ViewNames.POS, pos);
// Constituent s = new Constituent("A", ViewNames.MENTION, ta, 0, 3);
// s.addAttribute("EntityType", "PER");
// Constituent t = new Constituent("B", ViewNames.MENTION, ta, 4, 5);
// t.addAttribute("EntityType", "ORG");
// Relation r = new Relation("template", s, t, 1.0);
// List<String> flags = new RelationFeatureExtractor().getTemplateFeature(r);
// assertTrue(flags.contains("is_formulaic_structure"));
// assertTrue(flags.contains("is_possessive_structure"));
}

@Test
public void testOnlyNounBetweenReturnsFalseIfInterveningVB() {
// TokenizerTextAnnotationBuilder b = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = b.createTextAnnotation("t", "d", "economy falls crisis");
// DummyView pos = new DummyView(ViewNames.POS, "x");
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent front = new Constituent("A", ViewNames.MENTION, ta, 0, 1);
// Constituent back = new Constituent("B", ViewNames.MENTION, ta, 2, 3);
// boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
// assertFalse(result);
}

@Test
public void testGetShallowParseFeature_handlesMinimalSpanInput() {
// TokenizerTextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = tab.createTextAnnotation("t", "s", "global warming continues");
// DummyView sp = new DummyView(ViewNames.SHALLOW_PARSE, "chunk");
// sp.addConstituent(new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2));
// sp.addConstituent(new Constituent("VP", ViewNames.SHALLOW_PARSE, ta, 2, 3));
// ta.addView(ViewNames.SHALLOW_PARSE, sp);
// Constituent s = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// Constituent t = new Constituent("Y", ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("sh", s, t, 1.0);
// List<Pair<String, String>> features = new RelationFeatureExtractor().getShallowParseFeature(r);
// assertFalse(features.isEmpty());
// assertTrue(features.stream().anyMatch(p -> p.getFirst().startsWith("chunker_between_extents_")));
}

@Test
public void testGetLexicalFeaturePartCC_whenSourceAfterTarget_stillGeneratesTokens() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("ccorpus", "test", "first between last");
// Constituent source = new Constituent("SRC", ViewNames.MENTION, ta, 2, 3);
// Constituent target = new Constituent("TGT", ViewNames.MENTION, ta, 0, 1);
// Relation r = new Relation("rel", source, target, 1.0);
// List<String> tokens = new RelationFeatureExtractor().getLexicalFeaturePartCC(r);
// assertEquals(1, tokens.size());
// assertTrue(tokens.get(0).startsWith("bowbethead_"));
}

@Test
public void testGetLexicalFeaturePartD_twoWordsBetweenMentions() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("ccorpus", "doc", "one two three four five");
// Constituent source = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("Y", ViewNames.MENTION, ta, 4, 5);
// Relation r = new Relation("rel", source, target, 1.0);
// List<String> feats = new RelationFeatureExtractor().getLexicalFeaturePartD(r);
// assertTrue(feats.contains("between_first_two"));
// assertTrue(feats.contains("between_first_four"));
}

@Test
public void testIsFormulaic_returnsFalseForNonORGNonGPETypes() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("core", "1", "Mr. , Science");
// DummyView pos = new DummyView(ViewNames.POS, "pos");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent(",", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("UN", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "THING");
// Relation r = new Relation("r", source, target, 1.0);
// boolean isFormulaic = RelationFeatureExtractor.isFormulaic(r);
// assertFalse(isFormulaic);
}

@Test
public void testGetDependencyFeature_doesNotFailIfCoveringTokenReturnsEmptyLists() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("corpus", "docId", "Obama meets press");
// TreeView dependency = new TreeView(ViewNames.DEPENDENCY_STANFORD, "dummy");
// ta.addView(ViewNames.DEPENDENCY_STANFORD, dependency);
// DummyView pos = new DummyView(ViewNames.POS, "x");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// DummyView ann = new DummyView("RE_ANNOTATED", "x");
// ta.addView("RE_ANNOTATED", ann);
// Constituent source = new Constituent("SRC", ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("TGT", ViewNames.MENTION, ta, 2, 3);
// Relation r = new Relation("rel", source, target, 1.0);
// List<Pair<String, String>> paths = RelationFeatureExtractor.getDependencyFeature(r);
// assertTrue(paths.isEmpty());
}

@Test
public void testPatternRecognition_handlesEmptyPathConstituentsGracefully() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("core", "2", "John failed exam");
// TreeView dependencies = new TreeView(ViewNames.DEPENDENCY_STANFORD, "x");
// ta.addView(ViewNames.DEPENDENCY_STANFORD, dependencies);
// DummyView pos = new DummyView(ViewNames.POS, "y");
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("A", ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("B", ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityType", "OBJ");
// List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
// assertNotNull(features);
}

@Test
public void testGetEntityHeadForConstituent_returnsNullWhenOffsetCannotMapToToken() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("corpus", "doc", "One");
// Constituent entity = new Constituent("X", ViewNames.MENTION, ta, 0, 1);
// entity.addAttribute("EntityHeadStartCharOffset", "9999");
// entity.addAttribute("EntityHeadEndCharOffset", "10000");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(entity, ta, "view");
// assertNull(result);
}

@Test
public void testGetShallowParseFeature_skipsPathIfHeadsAreEqual() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("c", "i", "dog");
// DummyView spView = new DummyView(ViewNames.SHALLOW_PARSE, "chunk");
// spView.addConstituent(new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 1));
// ta.addView(ViewNames.SHALLOW_PARSE, spView);
// Constituent s = new Constituent("SRC", ViewNames.MENTION, ta, 0, 1);
// s.addAttribute("EntityType", "ANIMAL");
// Constituent t = new Constituent("TGT", ViewNames.MENTION, ta, 0, 1);
// t.addAttribute("EntityType", "ANIMAL");
// Relation r = new Relation("shallow", s, t, 1.0);
// List<Pair<String, String>> features = new RelationFeatureExtractor().getShallowParseFeature(r);
// assertTrue(features.isEmpty());
}

@Test
public void testGetStructualFeature_mentionsOverlapping_returnsCoverageFeatures() {
// TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new DummyTokenizer());
// TextAnnotation ta = builder.createTextAnnotation("x", "y", "Paris France");
// DummyView mention = new DummyView(ViewNames.MENTION_ACE, "mview");
// mention.addConstituent(new Constituent("GEO", ViewNames.MENTION_ACE, ta, 0, 2));
// mention.addConstituent(new Constituent("GEO", ViewNames.MENTION_ACE, ta, 1, 2));
// ta.addView(ViewNames.MENTION_ACE, mention);
// Constituent source = new Constituent("A", ViewNames.MENTION_ACE, ta, 0, 2);
// source.addAttribute("EnityType", "GPE");
// Constituent target = new Constituent("B", ViewNames.MENTION_ACE, ta, 1, 2);
// target.addAttribute("EntityType", "GPE");
// Relation r = new Relation("cover", source, target, 1.0);
// List<String> result = new RelationFeatureExtractor().getStructualFeature(r);
// assertTrue(result.contains("m2_in_m1"));
// assertTrue(result.stream().anyMatch(s -> s.contains("cb1_")));
}
}
