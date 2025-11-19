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

public class RelationFeatureExtractor_2_GPTLLMTest {

@Test
public void testIsNoun_NN() {
String tag = "NN";
boolean result = RelationFeatureExtractor.isNoun(tag);
assertTrue(result);
}

@Test
public void testIsNoun_RB() {
String tag = "RB";
boolean result = RelationFeatureExtractor.isNoun(tag);
assertTrue(result);
}

@Test
public void testIsNoun_WP() {
String tag = "WP";
boolean result = RelationFeatureExtractor.isNoun(tag);
assertTrue(result);
}

@Test
public void testIsNoun_VB() {
String tag = "VB";
boolean result = RelationFeatureExtractor.isNoun(tag);
assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartA_ReturnsCorrectTokens() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Barack", "Obama" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "ta1", tokens);
Constituent source = new Constituent("PER", 1.0, "testView", ta, 0, 2);
Relation relation = new Relation("testRel", source, source, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartA(relation);
assertEquals(2, features.size());
assertEquals("Barack", features.get(0));
assertEquals("Obama", features.get(1));
}

@Test
public void testGetLexicalFeaturePartB_ReturnsTargetTokens() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "United", "Nations" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "ta2", tokens);
Constituent target = new Constituent("ORG", 1.0, "testView", ta, 0, 2);
Relation relation = new Relation("testRel", target, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartB(relation);
assertEquals(2, features.size());
assertEquals("United", features.get(0));
assertEquals("Nations", features.get(1));
}

@Test
public void testGetLexicalFeaturePartC_WithSingleWordBetween() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "John", "'s", "company" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "ta", tokens);
Constituent c1 = new Constituent("PER", 1.0, "view", ta, 0, 1);
Constituent c2 = new Constituent("ORG", 1.0, "view", ta, 2, 3);
Relation relation = new Relation("testRel", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartC(relation);
assertEquals(1, features.size());
assertEquals("singleword_'s", features.get(0));
}

@Test
public void testGetLexicalFeaturePartD_NoTokensBetween() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "A", "B" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c1 = new Constituent("A", 1.0, "testView", ta, 0, 1);
Constituent c2 = new Constituent("B", 1.0, "testView", ta, 1, 2);
Relation relation = new Relation("rel", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getLexicalFeaturePartD(relation);
assertEquals(1, result.size());
assertEquals("No_between_features", result.get(0));
}

@Test
public void testGetCorefTag_TRUE() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "John", "Smith" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "coref", tokens);
Constituent c1 = new Constituent("PER", 1.0, "corefView", ta, 0, 1);
c1.addAttribute("EntityID", "E1");
Constituent c2 = new Constituent("PER", 1.0, "corefView", ta, 1, 2);
c2.addAttribute("EntityID", "E1");
Relation relation = new Relation("corefRelation", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
String result = extractor.getCorefTag(relation);
assertEquals("TRUE", result);
}

@Test
public void testGetCorefTag_FALSE_NoEntityID() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Entity1", "Entity2" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "ta", tokens);
Constituent c1 = new Constituent("ENT", 1.0, "view", ta, 0, 1);
Constituent c2 = new Constituent("ENT", 1.0, "view", ta, 1, 2);
Relation relation = new Relation("no_id", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
String result = extractor.getCorefTag(relation);
assertEquals("FALSE", result);
}

@Test
public void testGetTemplateFeature_FormulaicDetected() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "CEO", ",", "Amazon" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "temp", tokens);
Constituent c1 = new Constituent("PER", 1.0, "view", ta, 0, 1);
c1.addAttribute("EntityType", "PER");
Constituent c2 = new Constituent("ORG", 1.0, "view", ta, 2, 3);
c2.addAttribute("EntityType", "ORG");
Relation relation = new Relation("rel", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getTemplateFeature(relation);
boolean found = false;
for (String f : features) {
if (f.equals("is_formulaic_structure")) {
found = true;
}
}
assertTrue(found);
}

@Test
public void testPatternRecognition_SAME_SOURCE_TARGET_EXCEPTION() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "New", "York" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "pattern", tokens);
Constituent cons1 = new Constituent("LOC", 1.0, "view", ta, 0, 2);
cons1.addAttribute("EntityType", "LOC");
Constituent cons2 = new Constituent("LOC", 1.0, "view", ta, 0, 2);
cons2.addAttribute("EntityType", "LOC");
List<String> features = RelationFeatureExtractor.patternRecognition(cons1, cons2);
boolean found = false;
for (String s : features) {
if (s.equals("SAME_SOURCE_TARGET_EXCEPTION")) {
found = true;
}
}
assertTrue(found);
}

@Test
public void testGetEntityHeadForConstituent_PredictedAttributePresent() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Barack", "Obama" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c = new Constituent("PER", 1.0, "view", ta, 0, 2);
c.addAttribute("IsPredicted", "true");
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "view");
assertEquals(c, result);
}

@Test
public void testGetEntityHeadForConstituent_EntityHeadCharOffsetInvalid() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "X" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c = new Constituent("ENT", 1.0, "view", ta, 0, 1);
c.addAttribute("EntityHeadStartCharOffset", "150");
c.addAttribute("EntityHeadEndCharOffset", "155");
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "view");
assertNull(result);
}

@Test
public void testIsPossessive_NoPossessiveTokens() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Alice", "met", "Bob" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "gen", ta, 1.0);
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
ta.addView(ViewNames.POS, pos);
Constituent s = new Constituent("PER", 1.0, "view", ta, 0, 1);
s.addAttribute("EntityHeadStartSpan", "0");
Constituent t = new Constituent("PER", 1.0, "view", ta, 2, 3);
t.addAttribute("EntityHeadStartSpan", "2");
Relation r = new Relation("R", s, t, 1.0);
boolean result = RelationFeatureExtractor.isPossessive(r);
assertFalse(result);
}

@Test
public void testIsPreposition_NoINorTOBetween() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "He", "likes", "red", "apples" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "p", tokens);
View pos = new View(ViewNames.POS, "gen", ta, 1.0);
pos.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 2, 3));
pos.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 3, 4));
ta.addView(ViewNames.POS, pos);
Constituent s = new Constituent("PER", 1.0, "view", ta, 0, 1);
s.addAttribute("EntityHeadStartSpan", "0");
Constituent t = new Constituent("OBJ", 1.0, "view", ta, 3, 4);
t.addAttribute("EntityHeadStartSpan", "3");
Relation r = new Relation("rel", s, t, 1.0);
boolean result = RelationFeatureExtractor.isPreposition(r);
assertFalse(result);
}

@Test
public void testPatternRecognition_TargetBeforeSourceWithComma() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Amazon", ",", "CEO" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent source = new Constituent("ORG", 1.0, "v", ta, 2, 3);
source.addAttribute("EntityType", "ORG");
Constituent target = new Constituent("PER", 1.0, "v", ta, 0, 1);
target.addAttribute("EntityType", "PER");
List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
boolean found = false;
for (String f : features) {
if (f.equals("FORMULAIC")) {
found = true;
}
}
assertTrue(found);
}

@Test
public void testGetLexicalFeaturePartCC_NoTokensBetweenHeads() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "X", "Y" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent s = new Constituent("A", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityHeadStartSpan", "0");
Constituent t = new Constituent("B", 1.0, "v", ta, 1, 2);
t.addAttribute("EntityHeadStartSpan", "1");
Relation r = new Relation("rel", s, t, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getLexicalFeaturePartCC(r);
assertTrue(feats.isEmpty());
}

@Test
public void testGetMentionFeature_ConstituentCoversOther() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "John", "from", "IBM", "in", "New", "York" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "mention", tokens);
Constituent s = new Constituent("PER", 1.0, "v", ta, 0, 5);
s.addAttribute("EntityType", "PER");
s.addAttribute("EntityMentionType", "NAM");
Constituent t = new Constituent("LOC", 1.0, "v", ta, 4, 5);
t.addAttribute("EntityType", "LOC");
t.addAttribute("EntityMentionType", "NOM");
Relation r = new Relation("rel", s, t, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getMentionFeature(r);
boolean hasContainFeature = false;
for (String f : features) {
if (f.startsWith("mlvl_cont_2")) {
hasContainFeature = true;
}
}
assertTrue(hasContainFeature);
}

@Test
public void testGetLexicalFeaturePartD_TokensBetweenMentions() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "CEO", "of", "IBM", "in", "New", "York" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent s = new Constituent("PER", 1.0, "v", ta, 0, 1);
Constituent t = new Constituent("LOC", 1.0, "v", ta, 4, 5);
Relation r = new Relation("rel", s, t, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getLexicalFeaturePartD(r);
assertTrue(feats.contains("between_first_of"));
assertTrue(feats.contains("between_first_in"));
}

@Test
public void testGetLexicalFeaturePartE_SourceBeginningOfSentence() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Amazon", "acquired", "Zappos" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "text", tokens);
Constituent s = new Constituent("ORG", 1.0, "v", ta, 0, 1);
Constituent t = new Constituent("ORG", 1.0, "v", ta, 2, 3);
Relation r = new Relation("rel", s, t, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getLexicalFeaturePartE(r);
assertTrue(feats.contains("fwM1_NULL"));
assertTrue(feats.contains("swM1_NULL"));
}

@Test
public void testIsPremodifier_frontNullCase() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "New", "York" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "test", ta, 1.0);
pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("GPE", 1.0, "view", ta, 1, 2);
source.addAttribute("EntityHeadStartSpan", "1");
source.addAttribute("EntityType", "GPE");
Constituent target = new Constituent("GPE", 1.0, "view", ta, 0, 1);
target.addAttribute("EntityHeadStartSpan", "0");
target.addAttribute("EntityType", "GPE");
Relation rel = new Relation("premod", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPremodifier(rel);
assertFalse(result);
}

@Test
public void testGetStructuralFeature_noCoveringAndNoMentionView() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Barack", "Obama", "is", "president" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c1 = new Constituent("PER", 1.0, "v", ta, 0, 2);
c1.addAttribute("EntityType", "PER");
c1.addAttribute("EnityType", "PER");
Constituent c2 = new Constituent("TITLE", 1.0, "v", ta, 3, 4);
c2.addAttribute("EntityType", "ORG");
c2.addAttribute("EnityType", "ORG");
Relation r = new Relation("rel", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getStructualFeature(r);
assertTrue(feats.contains("middle_mention_size_null"));
assertTrue(feats.contains("cb1_PER_ORG_m1_m2_no_coverage"));
}

@Test
public void testGetTemplateFeature_AllFalse() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "He", "runs", "Google" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "test", ta, 1.0);
pos.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("PER", 1.0, "v", ta, 0, 1);
source.addAttribute("EntityHeadStartSpan", "0");
source.addAttribute("EntityType", "PER");
Constituent target = new Constituent("ORG", 1.0, "v", ta, 2, 3);
target.addAttribute("EntityHeadStartSpan", "2");
target.addAttribute("EntityType", "ORG");
Relation rel = new Relation("template", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getTemplateFeature(rel);
assertTrue(features.isEmpty());
}

@Test
public void testGetLexicalFeaturePartF_NullHeadsAvoided() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "The", "United", "States" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent s = new Constituent("LOC", 1.0, "v", ta, 0, 2);
s.addAttribute("EntityHeadStartCharOffset", "0");
s.addAttribute("EntityHeadEndCharOffset", "9");
Constituent t = new Constituent("LOC", 1.0, "v", ta, 2, 3);
t.addAttribute("EntityHeadStartCharOffset", "10");
t.addAttribute("EntityHeadEndCharOffset", "16");
Relation r = new Relation("rel", s, t, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getLexicalFeaturePartF(r);
assertEquals(3, feats.size());
assertTrue(feats.get(0).startsWith("HM1_"));
assertTrue(feats.get(1).startsWith("HM2_"));
}

@Test
public void testGetMentionFeature_OverlapBothContainedCases() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Amazon", "Web", "Services" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "mention", tokens);
Constituent outer = new Constituent("ORG", 1.0, "v", ta, 0, 3);
outer.addAttribute("EntityMentionType", "NAM");
outer.addAttribute("EntityType", "ORG");
Constituent inner = new Constituent("ORG", 1.0, "v", ta, 1, 2);
inner.addAttribute("EntityMentionType", "NOM");
inner.addAttribute("EntityType", "ORG");
Relation r = new Relation("rel", inner, outer, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getMentionFeature(r);
boolean contains1 = false;
boolean contains2 = false;
for (String f : feats) {
if (f.startsWith("mlvl_cont_1"))
contains1 = true;
if (f.startsWith("mlvl_cont_2"))
contains2 = true;
}
assertTrue(contains1 || contains2);
}

@Test
public void testGetCollocationsFeature_EmptyContext() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "CEO" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c = new Constituent("TITLE", 1.0, "v", ta, 0, 1);
c.addAttribute("EntityHeadStartSpan", "0");
Relation r = new Relation("rel", c, c, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getCollocationsFeature(r);
assertTrue(feats.size() >= 5);
}

@Test
public void testGetLexicalFeaturePartA_EmptySpan() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Obama" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c = new Constituent("PER", 1.0, "v", ta, 0, 0);
Relation r = new Relation("rel", c, c, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getLexicalFeaturePartA(r);
assertTrue(result.isEmpty());
}

@Test
public void testOnlyNounBetween_NoNounsBetween() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "He", "is", "from", "Harvard" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "test", ta, 1.0);
pos.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("IN", ViewNames.POS, ta, 2, 3));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 3, 4));
ta.addView(ViewNames.POS, pos);
Constituent front = new Constituent("PER", 1.0, "view", ta, 0, 1);
Constituent back = new Constituent("ORG", 1.0, "view", ta, 3, 4);
boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
assertFalse(result);
}

@Test
public void testGetDependencyFeature_DifferentSentenceIds() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Apple", "was", "founded", "." });
tokens.add(new String[] { "It", "sells", "iPhones", "." });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
// View dependency = new TreeView(ViewNames.DEPENDENCY_STANFORD, "dummyParse");
// ta.addView(ViewNames.DEPENDENCY_STANFORD, dependency);
View pos = new View(ViewNames.POS, "dummy", ta, 1.0);
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("VBN", ViewNames.POS, ta, 2, 3));
pos.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 4, 5));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 5, 6));
ta.addView(ViewNames.POS, pos);
View reAnnotated = new View("RE_ANNOTATED", "dummy", ta, 1.0);
reAnnotated.addConstituent(new Constituent("NNP", "RE_ANNOTATED", ta, 0, 1));
reAnnotated.getConstituents().get(0).addAttribute("WORDNETTAG", "entity");
reAnnotated.addConstituent(new Constituent("PRP", "RE_ANNOTATED", ta, 4, 5));
reAnnotated.getConstituents().get(1).addAttribute("WORDNETTAG", "pronoun");
ta.addView("RE_ANNOTATED", reAnnotated);
Constituent s = new Constituent("ORG", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityHeadStartSpan", "0");
Constituent t = new Constituent("ORG", 1.0, "v", ta, 4, 5);
t.addAttribute("EntityHeadStartSpan", "4");
Relation r = new Relation("rel", s, t, 1.0);
List<Pair<String, String>> result = RelationFeatureExtractor.getDependencyFeature(r);
assertTrue(result.isEmpty());
}

@Test
public void testGetShallowParseFeature_NullTokensInShallowParseView() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "President", "Lincoln", "visited", "Washington" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View chunkView = new View(ViewNames.SHALLOW_PARSE, "dummy", ta, 1.0);
chunkView.addConstituent(new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 2));
chunkView.addConstituent(new Constituent("VP", ViewNames.SHALLOW_PARSE, ta, 2, 3));
chunkView.addConstituent(new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 3, 4));
ta.addView(ViewNames.SHALLOW_PARSE, chunkView);
Constituent c1 = new Constituent("PER", 1.0, "v", ta, 0, 2);
c1.addAttribute("EntityHeadStartSpan", "0");
Constituent c2 = new Constituent("LOC", 1.0, "v", ta, 3, 4);
c2.addAttribute("EntityHeadStartSpan", "3");
Relation r = new Relation("rel", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<Pair<String, String>> features = extractor.getShallowParseFeature(r);
boolean found1 = false;
boolean found2 = false;
for (Pair<String, String> p : features) {
if (p.getFirst().startsWith("chunker_between_heads_")) {
found1 = true;
}
if (p.getFirst().startsWith("chunker_between_extents_")) {
found2 = true;
}
}
assertTrue(found1);
assertTrue(found2);
}

@Test
public void testGetTemplateFeature_AllActive() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "John", "'s", "father", "in", "Los", "Angeles" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "test", ta, 1.0);
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("POS", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
pos.addConstituent(new Constituent("IN", ViewNames.POS, ta, 3, 4));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 4, 5));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 5, 6));
ta.addView(ViewNames.POS, pos);
Constituent s = new Constituent("PER", 1.0, "v", ta, 0, 3);
s.addAttribute("EntityType", "PER");
s.addAttribute("EntityHeadStartSpan", "0");
Constituent t = new Constituent("LOC", 1.0, "v", ta, 4, 6);
t.addAttribute("EntityType", "LOC");
t.addAttribute("EntityHeadStartSpan", "4");
Relation r = new Relation("rel", s, t, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getTemplateFeature(r);
assertTrue(features.contains("is_formulaic_structure"));
assertTrue(features.contains("is_possessive_structure"));
assertTrue(features.contains("is_preposition_structure"));
}

@Test
public void testPatternRecognition_NoMatchingPattern() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Barack", "met", "Angela" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent s = new Constituent("PER", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityHeadStartSpan", "0");
s.addAttribute("EntityType", "PER");
Constituent t = new Constituent("PER", 1.0, "v", ta, 2, 3);
t.addAttribute("EntityHeadStartSpan", "2");
t.addAttribute("EntityType", "PER");
List<String> result = RelationFeatureExtractor.patternRecognition(s, t);
assertTrue(result.isEmpty());
}

@Test
public void testGetEntityHeadForConstituent_MentionAnnotatorBranch() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Washington" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c = new Constituent("GPE", 1.0, "view", ta, 0, 1);
c.addAttribute("EntityHeadStartSpan", "0");
View dummyView = new View("dummy", "gen", ta, 1.0);
ta.addView("dummy", dummyView);
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "dummy");
assertNotNull(result);
assertEquals("GPE", result.getLabel());
assertEquals(0, result.getStartSpan());
}

@Test
public void testGetPatternRecognition_SameExtentSpan() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Amazon" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent s = new Constituent("ORG", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityHeadStartSpan", "0");
Constituent t = new Constituent("ORG", 1.0, "v", ta, 0, 1);
t.addAttribute("EntityHeadStartSpan", "0");
List<String> result = RelationFeatureExtractor.patternRecognition(s, t);
assertTrue(result.contains("SAME_SOURCE_TARGET_EXCEPTION"));
}

@Test
public void testIsPossessive_TokensWithSeparateQuoteAndS() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Alice", "'", "s", "car" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "dummy", ta, 1.0);
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("''", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 2, 3));
pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 3, 4));
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("PER", 1.0, "v", ta, 0, 1);
source.addAttribute("EntityHeadStartSpan", "0");
Constituent target = new Constituent("MISC", 1.0, "v", ta, 3, 4);
target.addAttribute("EntityHeadStartSpan", "3");
Relation relation = new Relation("rel", source, target, 1.0);
boolean isPossessive = RelationFeatureExtractor.isPossessive(relation);
assertFalse(isPossessive);
}

@Test
public void testIsPremodifier_BetweenWithNonPremodifierPOS() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "red", "jumps", "truck" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "posGen", ta, 1.0);
pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
ta.addView(ViewNames.POS, pos);
Constituent front = new Constituent("ADJ", 1.0, "view", ta, 0, 1);
front.addAttribute("EntityHeadStartSpan", "0");
Constituent back = new Constituent("NOUN", 1.0, "view", ta, 2, 3);
back.addAttribute("EntityHeadStartSpan", "2");
Relation relation = new Relation("premod", front, back, 1.0);
boolean result = RelationFeatureExtractor.isPremodifier(relation);
assertFalse(result);
}

@Test
public void testGetLexicalFeaturePartD_OneTokenBetween_MirroredSpan() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Obama", "visited", "Paris" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent source = new Constituent("PER", 1.0, "view", ta, 0, 1);
Constituent target = new Constituent("LOC", 1.0, "view", ta, 2, 3);
Relation r = new Relation("reverse", target, source, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getLexicalFeaturePartD(r);
assertEquals(3, feats.size());
assertEquals("between_first_visited", feats.get(0));
assertEquals("between_first_visited", feats.get(1));
assertEquals("No_between_features", feats.get(2));
}

@Test
public void testGetLexicalFeaturePartE_TargetAtSentenceEnd() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "The", "CEO", "is", "Elon" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent source = new Constituent("TITLE", 1.0, "v", ta, 1, 2);
Constituent target = new Constituent("PER", 1.0, "v", ta, 3, 4);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getLexicalFeaturePartE(r);
assertTrue(feats.contains("fwM2_NULL"));
assertTrue(feats.contains("swM2_NULL"));
}

@Test
public void testGetStructualFeature_TargetInSourceCoverage() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Amazon", "Web", "Services" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "ta", tokens);
Constituent source = new Constituent("ORG", 1.0, "v", ta, 0, 3);
source.addAttribute("EnityType", "ORG");
source.addAttribute("EntityType", "ORG");
Constituent target = new Constituent("ORG", 1.0, "v", ta, 1, 2);
target.addAttribute("EnityType", "ORG");
target.addAttribute("EntityType", "ORG");
Relation relation = new Relation("cover", source, target, 1.0);
View mentions = new View(ViewNames.MENTION_ACE, "dummy", ta, 1.0);
ta.addView(ViewNames.MENTION_ACE, mentions);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getStructualFeature(relation);
assertTrue(features.contains("m2_in_m1"));
assertTrue(features.get(features.size() - 1).endsWith("_m2_in_m1"));
}

@Test
public void testGetLexicalFeaturePartCC_ReverseDirectionBow() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "CEO", "of", "Apple" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "fwd", tokens);
Constituent source = new Constituent("ORG", 1.0, "v", ta, 2, 3);
source.addAttribute("EntityHeadStartSpan", "2");
Constituent target = new Constituent("TITLE", 1.0, "v", ta, 0, 1);
target.addAttribute("EntityHeadStartSpan", "0");
Relation r = new Relation("reverse", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> feats = extractor.getLexicalFeaturePartCC(r);
assertEquals(1, feats.size());
assertEquals("bowbethead_of", feats.get(0));
}

@Test
public void testPatternRecognition_TargetArgumentSlightlyBeforeWithComma() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Microsoft", ",", "Inc." });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "p", tokens);
Constituent source = new Constituent("ORG", 1.0, "v", ta, 2, 3);
source.addAttribute("EntityHeadStartSpan", "2");
source.addAttribute("EntityType", "ORG");
Constituent target = new Constituent("ORG", 1.0, "v", ta, 0, 1);
target.addAttribute("EntityHeadStartSpan", "0");
target.addAttribute("EntityType", "ORG");
List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
assertTrue(features.contains("FORMULAIC"));
}

@Test
public void testGetMentionFeature_NonOverlappingMentionsNoContainment() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Red", "Cross", "volunteers" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "m", tokens);
Constituent c1 = new Constituent("ORG", 1.0, "v", ta, 0, 2);
c1.addAttribute("EntityType", "ORG");
c1.addAttribute("EntityMentionType", "NAM");
Constituent c2 = new Constituent("PER", 1.0, "v", ta, 2, 3);
c2.addAttribute("EntityType", "PER");
c2.addAttribute("EntityMentionType", "NOM");
Relation r = new Relation("mention", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getMentionFeature(r);
boolean containsType = false;
boolean containsLevel = false;
boolean containsCoverage = features.contains("mt_ORG_PER") && features.contains("mlvl_NAM_NOM") && features.contains("mlvl_mt_NAM_ORG_NOM_PER") && features.contains("mlvl_cont_1_NAM_NOM_True") == false && features.contains("mlvl_cont_2_NAM_NOM_True") == false;
for (String f : features) {
if (f.equals("source_mtype_ORG"))
containsType = true;
if (f.equals("target_mtype_PER"))
containsType = true;
if (f.equals("mlvl_NAM_NOM"))
containsLevel = true;
}
assertTrue(containsType);
assertTrue(containsLevel);
assertTrue(containsCoverage);
}

@Test
public void testGetEntityHeadForConstituent_charOffsetToTokenOffsetMismatchReturnsNull() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "EntityA", "relates", "EntityB" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c = new Constituent("ENT", 1.0, "test", ta, 0, 1);
c.addAttribute("EntityHeadStartCharOffset", "100");
c.addAttribute("EntityHeadEndCharOffset", "105");
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "dummyView");
assertNull(result);
}

@Test
public void testGetLexicalFeaturePartC_NoSingleWordBetween() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "A", "of", "B", "in", "C" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent c1 = new Constituent("X", 1.0, "v", ta, 0, 1);
Constituent c2 = new Constituent("Y", 1.0, "v", ta, 4, 5);
Relation r = new Relation("C", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getLexicalFeaturePartC(r);
assertEquals(1, result.size());
assertEquals("No_singleword", result.get(0));
}

@Test
public void testGetLexicalFeaturePartD_SourceBeforeTargetWith2TokensBetween() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "X", "y1", "y2", "Y" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent source = new Constituent("X", 1.0, "v", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "v", ta, 3, 4);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartD(r);
assertTrue(features.contains("between_first_y1"));
assertTrue(features.contains("between_first_y2"));
}

@Test
public void testGetLexicalFeaturePartE_SourceStartMiddle_TargetStartEndSentence() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "The", "President", "met", "Putin" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
Constituent source = new Constituent("TITLE", 1.0, "v", ta, 1, 2);
Constituent target = new Constituent("PER", 1.0, "v", ta, 3, 4);
Relation r = new Relation("e", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getLexicalFeaturePartE(r);
assertTrue(result.contains("fwM1_The"));
assertTrue(result.contains("fwM2_NULL"));
assertTrue(result.contains("swM2_NULL"));
}

@Test
public void testGetStructualFeature_TargetBeforeSource_MentionViewExists() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "x", "y", "z" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "m", tokens);
Constituent t = new Constituent("T", 1.0, "v", ta, 0, 1);
Constituent s = new Constituent("S", 1.0, "v", ta, 2, 3);
s.addAttribute("EnityType", "S");
t.addAttribute("EnityType", "T");
s.addAttribute("EntityType", "S");
t.addAttribute("EntityType", "T");
View mention = new View(ViewNames.MENTION_ACE, "dummy", ta, 1.0);
ta.addView(ViewNames.MENTION_ACE, mention);
Relation relation = new Relation("struct2", s, t, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getStructualFeature(relation);
assertTrue(result.contains("middle_mention_size_0"));
assertTrue(result.contains("middle_word_size_1"));
}

@Test
public void testOnlyNounBetween_NoNounTagBetween() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "quick", "very", "fox" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "noun", tokens);
View pos = new View(ViewNames.POS, "v", ta, 1.0);
pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("RB", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
ta.addView(ViewNames.POS, pos);
Constituent c1 = new Constituent("X", 1.0, "view", ta, 0, 1);
Constituent c2 = new Constituent("Y", 1.0, "view", ta, 2, 3);
boolean output = RelationFeatureExtractor.onlyNounBetween(c1, c2);
assertFalse(output);
}

@Test
public void testGetTemplateFeature_ReturnsMultipleTemplatesCorrectly() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "President", "Obama", "of", "USA" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "test", ta, 1.0);
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("IN", ViewNames.POS, ta, 2, 3));
pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 3, 4));
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("PER", 1.0, "v", ta, 0, 2);
source.addAttribute("EntityHeadStartSpan", "1");
source.addAttribute("EntityType", "PER");
Constituent target = new Constituent("GPE", 1.0, "v", ta, 3, 4);
target.addAttribute("EntityHeadStartSpan", "3");
target.addAttribute("EntityType", "GPE");
Relation relation = new Relation("r", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getTemplateFeature(relation);
assertTrue(result.contains("is_formulaic_structure"));
assertTrue(result.contains("is_preposition_structure"));
}

@Test
public void testGetTemplateFeature_UnrelatedEntitiesReturnsEmpty() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "He", "runs", "fast" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
View pos = new View(ViewNames.POS, "dummy", ta, 1.0);
pos.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("RB", ViewNames.POS, ta, 2, 3));
ta.addView(ViewNames.POS, pos);
Constituent c1 = new Constituent("PER", 1.0, "v", ta, 0, 1);
c1.addAttribute("EntityHeadStartSpan", "0");
c1.addAttribute("EntityType", "PER");
Constituent c2 = new Constituent("OTHER", 1.0, "v", ta, 2, 3);
c2.addAttribute("EntityHeadStartSpan", "2");
c2.addAttribute("EntityType", "MISC");
Relation r = new Relation("rel", c1, c2, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getTemplateFeature(r);
assertTrue(features.isEmpty());
}

@Test
public void testGetDependencyFeature_EmptyView_ReturnsEmpty() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "He", "loves", "NY" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc1", tokens);
// ta.addView(ViewNames.DEPENDENCY_STANFORD, new TreeView(ViewNames.DEPENDENCY_STANFORD, "dummy"));
View pos = new View(ViewNames.POS, "dummy", ta, 1.0);
pos.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
ta.addView(ViewNames.POS, pos);
View annotated = new View("RE_ANNOTATED", "dummy", ta, 1.0);
ta.addView("RE_ANNOTATED", annotated);
Constituent s = new Constituent("PER", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityHeadStartSpan", "0");
Constituent t = new Constituent("LOC", 1.0, "v", ta, 2, 3);
t.addAttribute("EntityHeadStartSpan", "2");
Relation r = new Relation("test", s, t, 1.0);
List<Pair<String, String>> features = RelationFeatureExtractor.getDependencyFeature(r);
assertTrue(features.isEmpty());
}

@Test
public void testGetShallowParseFeature_InclusiveChunkFallbackIsUsed() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "University", "of", "Illinois" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "chunkTest", tokens);
View chunk = new View(ViewNames.SHALLOW_PARSE, "dummy", ta, 1.0);
chunk.addConstituent(new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 3));
ta.addView(ViewNames.SHALLOW_PARSE, chunk);
Constituent source = new Constituent("ORG", 1.0, "v", ta, 1, 2);
source.addAttribute("EntityHeadStartSpan", "1");
Constituent target = new Constituent("ORG", 1.0, "v", ta, 1, 2);
Relation r = new Relation("rel", source, target, 1.0);
List<Pair<String, String>> features = new RelationFeatureExtractor().getShallowParseFeature(r);
boolean foundInclusive = false;
for (Pair<String, String> p : features) {
if (p.getFirst().startsWith("chunker_between_extents_inclusive")) {
foundInclusive = true;
}
}
assertTrue(foundInclusive);
}

@Test
public void testPatternRecognition_ShortSpanWithCommaTriggersFormulaic() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Mayor", ",", "London" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "pattern", tokens);
Constituent s = new Constituent("PER", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityType", "PER");
Constituent t = new Constituent("LOC", 1.0, "v", ta, 2, 3);
t.addAttribute("EntityType", "LOC");
List<String> result = RelationFeatureExtractor.patternRecognition(s, t);
assertTrue(result.contains("FORMULAIC"));
}

@Test
public void testGetCollocationsFeature_SourceHeadNearStart() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "CEO", "of", "Google" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "bow1", tokens);
Constituent source = new Constituent("TITLE", 1.0, "v", ta, 0, 1);
source.addAttribute("EntityHeadStartSpan", "0");
Constituent target = new Constituent("ORG", 1.0, "v", ta, 2, 3);
target.addAttribute("EntityHeadStartSpan", "2");
Relation rel = new Relation("r", source, target, 1.0);
List<String> out = new RelationFeatureExtractor().getCollocationsFeature(rel);
boolean hasNullMarkers = false;
for (String f : out) {
if (f.equals("s_m1_m1_null") || f.equals("t_p1_p1_null")) {
hasNullMarkers = true;
}
}
assertTrue(hasNullMarkers);
}

@Test
public void testStructualFeature_FallbackToMentionEREView() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Obama", "met", "Putin" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "mStruct", tokens);
View mentionEre = new View(ViewNames.MENTION_ERE, "gr", ta, 1.0);
ta.addView(ViewNames.MENTION_ERE, mentionEre);
Constituent s = new Constituent("PER", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityType", "PER");
s.addAttribute("EnityType", "PER");
Constituent t = new Constituent("PER", 1.0, "v", ta, 2, 3);
t.addAttribute("EntityType", "PER");
t.addAttribute("EnityType", "PER");
Relation rel = new Relation("rel", s, t, 1.0);
List<String> result = new RelationFeatureExtractor().getStructualFeature(rel);
assertTrue(result.contains("m1_m2_no_coverage"));
}

@Test
public void testMentionFeature_SourceEqualsTarget_Structure() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "NASA" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "same", tokens);
Constituent s = new Constituent("ORG", 1.0, "v", ta, 0, 1);
s.addAttribute("EntityType", "ORG");
s.addAttribute("EntityMentionType", "NAM");
Relation r = new Relation("rel", s, s, 1.0);
List<String> out = new RelationFeatureExtractor().getMentionFeature(r);
boolean hasMentionLevel = false;
boolean hasCoreType = false;
for (String feat : out) {
if (feat.startsWith("mlvl_")) {
hasMentionLevel = true;
}
if (feat.startsWith("mt_")) {
hasCoreType = true;
}
}
assertTrue(hasMentionLevel);
assertTrue(hasCoreType);
}

@Test
public void testGetEntityHeadForConstituent_MissingOffsetKeysReturnsOriginal() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "Google", "Alphabet" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "nofield", tokens);
Constituent c = new Constituent("ORG", 1.0, "v", ta, 0, 1);
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "test");
assertSame(c, result);
}

@Test
public void testGetStructualFeature_nullMentionTypeFallbackToViewNamesMENTION() {
List<String[]> tokens = new ArrayList<>();
tokens.add(new String[] { "IBM", "acquired", "Red", "Hat" });
TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "fallback", tokens);
View mention = new View(ViewNames.MENTION, "foo", ta, 1.0);
mention.addConstituent(new Constituent("ORG", ViewNames.MENTION, ta, 0, 1));
mention.addConstituent(new Constituent("ORG", ViewNames.MENTION, ta, 2, 4));
ta.addView(ViewNames.MENTION, mention);
Constituent c1 = new Constituent("ORG", 1.0, "v", ta, 0, 1);
c1.addAttribute("EntityType", "ORG");
c1.addAttribute("EnityType", "ORG");
Constituent c2 = new Constituent("ORG", 1.0, "v", ta, 2, 4);
c2.addAttribute("EntityType", "ORG");
c2.addAttribute("EnityType", "ORG");
Relation rel = new Relation("rel", c1, c2, 1.0);
List<String> result = new RelationFeatureExtractor().getStructualFeature(rel);
assertTrue(result.contains("middle_mention_size_1"));
}
}
