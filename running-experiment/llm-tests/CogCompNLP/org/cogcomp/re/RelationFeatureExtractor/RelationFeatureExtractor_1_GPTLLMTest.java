package org.cogcomp.re;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
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

public class RelationFeatureExtractor_1_GPTLLMTest {

@Test
public void testGetLexicalFeaturePartA() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "John has a car.");
View view = new View("TEST", "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 2);
view.addConstituent(source);
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 3, 4);
view.addConstituent(target);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartA(r);
assertEquals(2, features.size());
assertEquals("John", features.get(0));
assertEquals("has", features.get(1));
}

@Test
public void testGetLexicalFeaturePartB() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Mary drives a bike.");
View view = new View("TEST", "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
view.addConstituent(source);
Constituent target = new Constituent("VEH", 1.0, "TEST", ta, 3, 4);
view.addConstituent(target);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartB(r);
assertEquals(1, features.size());
assertEquals("bike", features.get(0));
}

@Test
public void testGetLexicalFeaturePartC_NoWord() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Anna likes apples.");
View view = new View("TEST", "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
view.addConstituent(source);
Constituent target = new Constituent("FOOD", 1.0, "TEST", ta, 2, 3);
view.addConstituent(target);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartC(r);
assertEquals(1, features.size());
assertEquals("No_singleword", features.get(0));
}

@Test
public void testGetLexicalFeaturePartC_WithWord_Right() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Tom eats red apples.");
View view = new View("TEST", "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
view.addConstituent(source);
Constituent target = new Constituent("ADJ", 1.0, "TEST", ta, 2, 3);
view.addConstituent(target);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartC(r);
assertEquals(1, features.size());
assertEquals("No_singleword", features.get(0));
}

@Test
public void testGetLexicalFeaturePartF() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Barack Obama visited Paris.");
View view = new View("EntityHeads", "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, "EntityHeads", ta, 0, 2);
source.addAttribute("EntityHeadStartCharOffset", "0");
source.addAttribute("EntityHeadEndCharOffset", "6");
view.addConstituent(source);
Constituent target = new Constituent("LOC", 1.0, "EntityHeads", ta, 3, 4);
target.addAttribute("EntityHeadStartCharOffset", "20");
target.addAttribute("EntityHeadEndCharOffset", "25");
view.addConstituent(target);
ta.addView("EntityHeads", view);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartF(r);
assertEquals(3, features.size());
assertTrue(features.get(0).startsWith("HM1_"));
assertTrue(features.get(1).startsWith("HM2_"));
assertTrue(features.get(2).startsWith("HM12_"));
}

@Test
public void testIsNoun_NNP() {
boolean result = RelationFeatureExtractor.isNoun("NNP");
assertTrue(result);
}

@Test
public void testIsNoun_NN() {
boolean result = RelationFeatureExtractor.isNoun("NN");
assertTrue(result);
}

@Test
public void testIsNoun_VB() {
boolean result = RelationFeatureExtractor.isNoun("VB");
assertFalse(result);
}

@Test
public void testCorefTagEqualEntityID() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Alice visited Moscow.");
View view = new View("TEST", "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("LOC", 1.0, "TEST", ta, 2, 3);
source.addAttribute("EntityID", "e1");
target.addAttribute("EntityID", "e1");
view.addConstituent(source);
view.addConstituent(target);
ta.addView("TEST", view);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
String result = extractor.getCorefTag(r);
assertEquals("TRUE", result);
}

@Test
public void testCorefTagDifferentEntityID() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Bob likes apples.");
View view = new View("TEST", "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 2, 3);
source.addAttribute("EntityID", "e1");
target.addAttribute("EntityID", "e2");
view.addConstituent(source);
view.addConstituent(target);
ta.addView("TEST", view);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
String result = extractor.getCorefTag(r);
assertEquals("FALSE", result);
}

@Test
public void testGetMentionFeature_Basic() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Sarah bought an iPhone.");
View view = new View(ViewNames.MENTION_ACE, "test", ta, 1.0);
Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION_ACE, ta, 0, 1);
Constituent target = new Constituent("OBJ", 1.0, ViewNames.MENTION_ACE, ta, 3, 4);
source.addAttribute("EntityMentionType", "NAM");
target.addAttribute("EntityMentionType", "NOM");
source.addAttribute("EntityType", "PER");
target.addAttribute("EntityType", "OBJ");
view.addConstituent(source);
view.addConstituent(target);
ta.addView(ViewNames.MENTION_ACE, view);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getMentionFeature(r);
assertTrue(result.contains("source_mtype_PER"));
assertTrue(result.contains("target_mtype_OBJ"));
assertTrue(result.contains("mlvl_NAM_NOM"));
}

@Test
public void testGetEntityHeadForConstituent_IsPredictedAttribute() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "This is a test.");
Constituent cons = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
cons.addAttribute("IsPredicted", "true");
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, "HEAD");
assertNotNull(result);
assertEquals(cons, result);
}

@Test
public void testGetEntityHeadForConstituent_ACECharOffsetsInvalidSpan() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Hello world.");
Constituent cons = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
cons.addAttribute("EntityHeadStartCharOffset", "5");
cons.addAttribute("EntityHeadEndCharOffset", "2");
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(cons, ta, "HEAD");
assertNull(result);
}

@Test
public void testIsPossessive_TokensWithQuoteS() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Tom ' s bike.");
View view = new View(ViewNames.POS, "test", ta, 1.0);
// view.addTokenLabel(0, "NNP", 1.0f);
// view.addTokenLabel(1, "POS", 1.0f);
// view.addTokenLabel(2, "NN", 1.0f);
// view.addTokenLabel(3, ".", 1.0f);
ta.addView(ViewNames.POS, view);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 2);
Constituent target = new Constituent("VEH", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPossessive(r);
assertTrue(result);
}

@Test
public void testOnlyNounBetween_WithNNandJJ() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "The big red house.");
View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "DT", 1.0f);
// pos.addTokenLabel(1, "JJ", 1.0f);
// pos.addTokenLabel(2, "JJ", 1.0f);
// pos.addTokenLabel(3, "NN", 1.0f);
// pos.addTokenLabel(4, ".", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent front = new Constituent("ADJ", 1.0, "TEST", ta, 0, 1);
Constituent back = new Constituent("OBJ", 1.0, "TEST", ta, 3, 4);
boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
assertFalse(result);
}

@Test
public void testIsPreposition_WithoutFoundPrepositionToken() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Cats chase dogs.");
View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0f);
// pos.addTokenLabel(1, "VBP", 1.0f);
// pos.addTokenLabel(2, "NNS", 1.0f);
// pos.addTokenLabel(3, ".", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("ANIM", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("ANIM", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPreposition(r);
assertFalse(result);
}

@Test
public void testGetTemplateFeature_CoversAllTags() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "John 's car in company headquarters");
View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0f);
// pos.addTokenLabel(1, "POS", 1.0f);
// pos.addTokenLabel(2, "NN", 1.0f);
// pos.addTokenLabel(3, "IN", 1.0f);
// pos.addTokenLabel(4, "NN", 1.0f);
// pos.addTokenLabel(5, "NNS", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 2);
Constituent target = new Constituent("ORG", 1.0, "TEST", ta, 4, 6);
source.addAttribute("EntityType", "PER");
target.addAttribute("EntityType", "ORG");
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getTemplateFeature(r);
assertTrue(features.contains("is_possessive_structure") || features.contains("is_preposition_structure") || features.contains("is_formulaic_structure") || features.contains("is_premodifier_structure"));
}

@Test
public void testPatternRecognition_SAME_HEAD() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "book");
Constituent source = new Constituent("OBJ", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 0, 1);
List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
assertTrue(features.contains("SAME_SOURCE_TARGET_EXCEPTION"));
}

@Test
public void testPatternRecognition_SAME_START_EXTENT() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "apple fruit");
Constituent source = new Constituent("OBJ", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 0, 1);
source.addAttribute("EntityType", "OBJ");
target.addAttribute("EntityType", "OBJ");
List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
assertTrue(features.contains("SAME_SOURCE_TARGET_EXTENT_EXCEPTION"));
}

@Test
public void testGetShallowParseFeature_NoSpanBetween() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "red car");
View chunker = new TokenLabelView(ViewNames.SHALLOW_PARSE, "test", ta, 1.0);
// chunker.addTokenLabel(0, "B-NP", 1.0f);
// chunker.addTokenLabel(1, "I-NP", 1.0f);
ta.addView(ViewNames.SHALLOW_PARSE, chunker);
Constituent source = new Constituent("ADJ", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 1, 2);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<Pair<String, String>> result = extractor.getShallowParseFeature(r);
assertFalse(result.isEmpty());
}

@Test
public void testDependencyFeature_SentenceMismatch() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "A. B.");
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
List<Pair<String, String>> result = RelationFeatureExtractor.getDependencyFeature(r);
assertTrue(result.isEmpty());
}

@Test
public void testGetEntityHeadForConstituent_WithMentionAnnotatorStyle() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Michael Jordan played basketball.");
Constituent extent = new Constituent("PER", 1.0, "TEST", ta, 0, 2);
extent.addAttribute("EntityHeadStartSpan", "0");
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "TEST");
assertNotNull(result);
assertEquals("PER", result.getLabel());
}

@Test
public void testIsPossessive_NoFrontConstituentLogic() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Department of Education");
View posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "IN", 1.0);
// posView.addTokenLabel(2, "NNP", 1.0);
ta.addView(ViewNames.POS, posView);
Constituent source = new Constituent("ORG", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("ORG", 1.0, "TEST", ta, 2, 3);
Relation rel = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPossessive(rel);
assertFalse(result);
}

@Test
public void testIsFormulaic_NoCommaBetweenEntities() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "United Nations Headquarters");
View posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "NNPS", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
ta.addView(ViewNames.POS, posView);
Constituent source = new Constituent("ORG", 1.0, "TEST", ta, 0, 2);
source.addAttribute("EntityType", "ORG");
Constituent target = new Constituent("GPE", 1.0, "TEST", ta, 2, 3);
target.addAttribute("EntityType", "GPE");
Relation rel = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isFormulaic(rel);
assertTrue(result);
}

@Test
public void testIsPremodifier_BetweenHeadOffsetExact() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "city bus driver");
View posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "NN", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
ta.addView(ViewNames.POS, posView);
Constituent source = new Constituent("OBJ", 1.0, "TEST", ta, 0, 1);
source.addAttribute("EntityType", "GPE");
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 2, 3);
target.addAttribute("EntityType", "ORG");
Relation rel = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPremodifier(rel);
assertTrue(result);
}

@Test
public void testGetLexicalFeaturePartD_SourcePrecedesTarget_ThreeWordsBetween() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "A B C D E");
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 4, 5);
Relation rel = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getLexicalFeaturePartD(rel);
assertTrue(result.contains("between_first_B"));
assertTrue(result.contains("in_between_C"));
}

@Test
public void testGetLexicalFeaturePartE_RightEdgeOfSentence() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "He left quickly.");
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("ACTION", 1.0, "TEST", ta, 2, 3);
Relation rel = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getLexicalFeaturePartE(rel);
assertTrue(result.contains("fwM1_NULL") || result.contains("fwM2_NULL") || result.contains("swM1_NULL") || result.contains("swM2_NULL"));
}

@Test
public void testGetStructualFeature_SpanOverlap() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Apple Inc. is located in California.");
Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION_ACE, ta, 0, 2);
Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION_ACE, ta, 1, 2);
source.addAttribute("EntityType", "ORG");
target.addAttribute("EntityType", "LOC");
View view = new View(ViewNames.MENTION_ACE, "test", ta, 1.0);
view.addConstituent(source);
view.addConstituent(target);
ta.addView(ViewNames.MENTION_ACE, view);
Relation rel = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getStructualFeature(rel);
assertTrue(result.contains("m2_in_m1"));
assertTrue(result.stream().anyMatch(s -> s.contains("cb1_")));
}

@Test
public void testGetStructualFeature_NoMentionViewFallback() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Fire destroyed the old warehouse.");
Constituent source = new Constituent("EVENT", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("LOC", 1.0, "TEST", ta, 4, 5);
source.addAttribute("EntityType", "EVENT");
target.addAttribute("EntityType", "LOC");
View mentionView = new View(ViewNames.MENTION, "test", ta, 1.0);
mentionView.addConstituent(source);
mentionView.addConstituent(target);
ta.addView(ViewNames.MENTION, mentionView);
Relation rel = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getStructualFeature(rel);
assertTrue(result.stream().anyMatch(s -> s.startsWith("middle_mention_size")));
}

@Test
public void testPatternRecognition_CommaSeparatedLocation() throws Exception {
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(tokenizer).createTextAnnotation("test", "test", "Paris , France");
Constituent source = new Constituent("GPE", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("GPE", 1.0, "TEST", ta, 2, 3);
source.addAttribute("EntityType", "GPE");
target.addAttribute("EntityType", "GPE");
List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
assertTrue(result.contains("FORMULAIC"));
}

@Test
public void testGetEntityHeadForConstituent_InvalidCharOffsetsInBoundsButReversed() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "Entity has reversed offsets.");
Constituent c = new Constituent("PER", 1.0, "TEST", ta, 0, 2);
c.addAttribute("EntityHeadStartCharOffset", "20");
c.addAttribute("EntityHeadEndCharOffset", "10");
Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "HEAD_VIEW");
assertNull(result);
}

@Test
public void testGetDependencyFeature_NoPOSViewReturnsEmpty() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "John sees Mary");
View depTreeView = new View(ViewNames.DEPENDENCY_STANFORD, "test", ta, 1.0);
ta.addView(ViewNames.DEPENDENCY_STANFORD, depTreeView);
View reAnnotationView = new View("RE_ANNOTATED", "test", ta, 1.0);
ta.addView("RE_ANNOTATED", reAnnotationView);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("PER", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
List<Pair<String, String>> features = RelationFeatureExtractor.getDependencyFeature(r);
assertNotNull(features);
assertEquals(0, features.size());
}

@Test
public void testIsPossessive_SingleQuote_WithNoS() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "Tom ' went");
View posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0f);
// posView.addTokenLabel(1, "''", 1.0f);
// posView.addTokenLabel(2, "VBD", 1.0f);
ta.addView(ViewNames.POS, posView);
Constituent a = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent b = new Constituent("ACT", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", a, b, 1.0);
boolean result = RelationFeatureExtractor.isPossessive(r);
assertFalse(result);
}

@Test
public void testPatternRecognition_DepPathExceptionHandled() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "A B");
View dep = new View(ViewNames.DEPENDENCY_STANFORD, "test", ta, 1.0);
ta.addView(ViewNames.DEPENDENCY_STANFORD, dep);
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 1, 2);
List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
assertNotNull(result);
}

@Test
public void testIsFormulaic_EntityTypeMismatch() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "tech , org");
View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0f);
// pos.addTokenLabel(1, ",", 1.0f);
// pos.addTokenLabel(2, "NNP", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 2, 3);
source.addAttribute("EntityType", "MISC");
target.addAttribute("EntityType", "DATE");
Relation r = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isFormulaic(r);
assertFalse(result);
}

@Test
public void testGetShallowParseFeature_NullCase_NoHeadGap() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "London London London");
View sp = new TokenLabelView(ViewNames.SHALLOW_PARSE, "test", ta, 1.0);
// sp.addTokenLabel(0, "B-NP", 1.0f);
// sp.addTokenLabel(1, "I-NP", 1.0f);
// sp.addTokenLabel(2, "I-NP", 1.0f);
ta.addView(ViewNames.SHALLOW_PARSE, sp);
Constituent source = new Constituent("LOC", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("LOC", 1.0, "TEST", ta, 0, 1);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<Pair<String, String>> res = extractor.getShallowParseFeature(r);
assertEquals(0, res.size());
}

@Test
public void testOnlyNounBetween_StartEqualsEnd() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "same");
View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent front = new Constituent("A", 1.0, "TEST", ta, 0, 1);
Constituent back = new Constituent("B", 1.0, "TEST", ta, 0, 1);
boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
assertTrue(result);
}

@Test
public void testGetMentionFeature_ContainmentBothDirections() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "A A A");
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 3);
source.addAttribute("EntityMentionType", "NAM");
source.addAttribute("EntityType", "PER");
Constituent target = new Constituent("PER", 1.0, "TEST", ta, 0, 3);
target.addAttribute("EntityMentionType", "NOM");
target.addAttribute("EntityType", "PER");
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getMentionFeature(r);
assertTrue(result.contains("mlvl_cont_1_NAM_NOM_True"));
assertTrue(result.contains("mlvl_cont_2_NAM_NOM_True"));
}

@Test
public void testGetLexicalFeaturePartCC_HeadsReversed() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "A B C D E");
Constituent source = new Constituent("X", 1.0, "TEST", ta, 4, 5);
source.addAttribute("EntityHeadStartCharOffset", "8");
source.addAttribute("EntityHeadEndCharOffset", "9");
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 0, 1);
target.addAttribute("EntityHeadStartCharOffset", "0");
target.addAttribute("EntityHeadEndCharOffset", "1");
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getLexicalFeaturePartCC(r);
assertTrue(result.stream().anyMatch(f -> f.startsWith("bowbethead_")));
}

@Test
public void testIsPreposition_FrontToBackWithNounPresent() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "He ran from town quickly.");
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "PRP", 1.0f);
// pos.addTokenLabel(1, "VBD", 1.0f);
// pos.addTokenLabel(2, "IN", 1.0f);
// pos.addTokenLabel(3, "NN", 1.0f);
// pos.addTokenLabel(4, "RB", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("EVENT", 1.0, "TEST", ta, 1, 2);
Constituent target = new Constituent("LOC", 1.0, "TEST", ta, 3, 4);
Relation r = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPreposition(r);
assertFalse(result);
}

@Test
public void testIsPremodifier_NonValidModifiersBeforeHead() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "fast new building");
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "RB", 1.0f);
// pos.addTokenLabel(1, "VBN", 1.0f);
// pos.addTokenLabel(2, "NN", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("DESC", 1.0, "TEST", ta, 0, 2);
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 2, 3);
Relation rel = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPremodifier(rel);
assertFalse(result);
}

@Test
public void testIsFormulaic_CommaAndEntityTypeMatch() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "Los Angeles , California");
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0f);
// pos.addTokenLabel(1, "NNP", 1.0f);
// pos.addTokenLabel(2, ",", 1.0f);
// pos.addTokenLabel(3, "NNP", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("LOC", 1.0, "TEST", ta, 0, 2);
source.addAttribute("EntityType", "GPE");
Constituent target = new Constituent("LOC", 1.0, "TEST", ta, 3, 4);
target.addAttribute("EntityType", "GPE");
Relation rel = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isFormulaic(rel);
assertTrue(result);
}

@Test
public void testGetLexicalFeaturePartD_OneTokenBetween_SourceBeforeTarget() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "X middle Y");
Constituent source = new Constituent("A", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("B", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartD(r);
assertTrue(features.contains("between_first_middle"));
assertFalse(features.stream().anyMatch(s -> s.startsWith("in_between_")));
}

@Test
public void testGetLexicalFeaturePartD_TokensBetween_TargetBeforeSource() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "one two three four five");
Constituent source = new Constituent("E1", 1.0, "TEST", ta, 4, 5);
Constituent target = new Constituent("E2", 1.0, "TEST", ta, 0, 1);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartD(r);
assertTrue(features.contains("between_first_two"));
assertTrue(features.contains("between_first_four"));
assertTrue(features.contains("in_between_three"));
}

@Test
public void testGetStructualFeature_MentionCoveringTarget() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "big company building");
View mentionView = new View(ViewNames.MENTION_ACE, "annotator", ta, 1.0);
Constituent source = new Constituent("ORG", 1.0, ViewNames.MENTION_ACE, ta, 0, 3);
source.addAttribute("EntityType", "ORG");
Constituent target = new Constituent("PART", 1.0, ViewNames.MENTION_ACE, ta, 1, 2);
target.addAttribute("EntityType", "PART");
mentionView.addConstituent(source);
mentionView.addConstituent(target);
ta.addView(ViewNames.MENTION_ACE, mentionView);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getStructualFeature(r);
assertTrue(result.contains("m2_in_m1"));
assertTrue(result.stream().anyMatch(s -> s.contains("cb1_")));
}

@Test
public void testGetDependencyFeature_TargetConstituentMissingParse() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "source link target");
TreeView depView = new TreeView(ViewNames.DEPENDENCY_STANFORD, ta);
ta.addView(ViewNames.DEPENDENCY_STANFORD, depView);
View posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0f);
// posView.addTokenLabel(1, "VBZ", 1.0f);
// posView.addTokenLabel(2, "NN", 1.0f);
ta.addView(ViewNames.POS, posView);
View annotatedView = new View("RE_ANNOTATED", "annotator", ta, 1.0);
ta.addView("RE_ANNOTATED", annotatedView);
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
List<Pair<String, String>> result = RelationFeatureExtractor.getDependencyFeature(r);
assertTrue(result.isEmpty());
}

@Test
public void testPatternRecognition_ValidDependencyStructureMatch() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "A to B");
TreeView depTree = new TreeView(ViewNames.DEPENDENCY_STANFORD, ta);
Constituent nodeA = new Constituent("prep", 1.0, ViewNames.DEPENDENCY_STANFORD, ta, 0, 1);
Constituent nodeB = new Constituent("pobj", 1.0, ViewNames.DEPENDENCY_STANFORD, ta, 2, 3);
depTree.addConstituent(nodeA);
depTree.addConstituent(nodeB);
ta.addView(ViewNames.DEPENDENCY_STANFORD, depTree);
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0f);
// pos.addTokenLabel(1, "TO", 1.0f);
// pos.addTokenLabel(2, "NN", 1.0f);
ta.addView(ViewNames.POS, pos);
View annotated = new View("RE_ANNOTATED", "annotator", ta, 1.0);
Constituent wn1 = new Constituent("WN", 1.0, "RE_ANNOTATED", ta, 0, 1);
wn1.addAttribute("WORDNETTAG", "artifact");
annotated.addConstituent(wn1);
ta.addView("RE_ANNOTATED", annotated);
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 2, 3);
List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
assertTrue(result.contains("prep_pobj_dep_structure"));
}

@Test
public void testIsPossessive_EndSpanOutOfRange() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "Bob owns a house");
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0f);
// pos.addTokenLabel(1, "VBZ", 1.0f);
// pos.addTokenLabel(2, "DT", 1.0f);
// pos.addTokenLabel(3, "NN", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("LOC", 1.0, "TEST", ta, 4, 4);
Relation r = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPossessive(r);
assertFalse(result);
}

@Test
public void testIsPremodifier_ExactTokenGapWithDot() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "Company . Building");
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0f);
// pos.addTokenLabel(1, ".", 1.0f);
// pos.addTokenLabel(2, "NNP", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("ORG", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("ORG", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPremodifier(r);
assertTrue(result);
}

@Test
public void testIsFormulaic_MissingAttributeOnOneArg() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "Lisa , Manager");
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0f);
// pos.addTokenLabel(1, ",", 1.0f);
// pos.addTokenLabel(2, "NNP", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("NAME", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("TITLE", 1.0, "TEST", ta, 2, 3);
target.addAttribute("EntityType", "ORG");
Relation r = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isFormulaic(r);
assertFalse(result);
}

@Test
public void testPatternRecognition_CommaButBackTypeNotRecognized() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "ID", "Seattle , summer");
Constituent source = new Constituent("CITY", 1.0, "TEST", ta, 0, 1);
source.addAttribute("EntityType", "GPE");
Constituent target = new Constituent("SEASON", 1.0, "TEST", ta, 2, 3);
target.addAttribute("EntityType", "TIME");
List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
assertFalse(result.contains("FORMULAIC"));
}

@Test
public void testGetStructualFeature_TargetCoversSource() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "ID", "red balloon outside");
View mention = new View(ViewNames.MENTION_ACE, "annotator", ta, 1.0);
Constituent source = new Constituent("OBJ", 1.0, ViewNames.MENTION_ACE, ta, 1, 2);
source.addAttribute("EntityType", "VEH");
Constituent target = new Constituent("OBJ", 1.0, ViewNames.MENTION_ACE, ta, 0, 2);
target.addAttribute("EntityType", "DESC");
mention.addConstituent(source);
mention.addConstituent(target);
ta.addView(ViewNames.MENTION_ACE, mention);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> res = extractor.getStructualFeature(r);
assertTrue(res.contains("m1_in_m2"));
}

@Test
public void testGetDependencyFeature_TokenNotParsed() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "ID", "A B C");
View dep = new View(ViewNames.DEPENDENCY_STANFORD, "annotator", ta, 1.0);
ta.addView(ViewNames.DEPENDENCY_STANFORD, dep);
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0f);
// pos.addTokenLabel(1, "IN", 1.0f);
// pos.addTokenLabel(2, "NNP", 1.0f);
ta.addView(ViewNames.POS, pos);
View reAnn = new View("RE_ANNOTATED", "source", ta, 1.0);
Constituent tag = new Constituent("WN", 1.0, "RE_ANNOTATED", ta, 0, 1);
tag.addAttribute("WORDNETTAG", "location");
reAnn.addConstituent(tag);
ta.addView("RE_ANNOTATED", reAnn);
Constituent source = new Constituent("A", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("B", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
List<Pair<String, String>> res = RelationFeatureExtractor.getDependencyFeature(r);
assertTrue(res.isEmpty());
}

@Test
public void testGetTemplateFeature_StructureFlagsEmpty() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "ID", "alpha beta gamma");
View posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0f);
// posView.addTokenLabel(1, "NN", 1.0f);
// posView.addTokenLabel(2, "NN", 1.0f);
ta.addView(ViewNames.POS, posView);
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
source.addAttribute("EntityType", "MISC");
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 2, 3);
target.addAttribute("EntityType", "UNKNOWN");
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getTemplateFeature(r);
assertTrue(result.isEmpty());
}

@Test
public void testGetMentionFeature_NoContainment() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "id", "start middle end");
Constituent source = new Constituent("X", 1.0, "MENTION", ta, 0, 1);
source.addAttribute("EntityMentionType", "NOM");
source.addAttribute("EntityType", "OBJ");
Constituent target = new Constituent("Y", 1.0, "MENTION", ta, 2, 3);
target.addAttribute("EntityMentionType", "NAM");
target.addAttribute("EntityType", "OBJ");
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> res = extractor.getMentionFeature(r);
assertTrue(res.contains("mlvl_NOM_NAM"));
assertTrue(res.contains("mt_OBJ_OBJ"));
assertTrue(res.contains("mlvl_mt_NOM_OBJ_NAM_OBJ"));
assertTrue(res.contains("mlvl_mt_NOM_OBJ_NAM_OBJ"));
}

@Test
public void testGetLexicalFeaturePartA_EmptySpan() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("x", "y", "hello world");
Constituent source = new Constituent("OBJ", 1.0, "TEST", ta, 1, 1);
Constituent target = new Constituent("OTHER", 1.0, "TEST", ta, 1, 2);
Relation rel = new Relation("REL", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> res = extractor.getLexicalFeaturePartA(rel);
assertTrue(res.isEmpty());
}

@Test
public void testIsPremodifier_FrontEqualsBackFailsCondition() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "city hall");
View pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0f);
// pos.addTokenLabel(1, "NN", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("LOC", 1.0, "TEST", ta, 0, 2);
Constituent target = new Constituent("LOC", 1.0, "TEST", ta, 0, 2);
Relation relation = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPremodifier(relation);
assertFalse(result);
}

@Test
public void testIsNoun_NonNounTags() {
assertFalse(RelationFeatureExtractor.isNoun("VBZ"));
assertFalse(RelationFeatureExtractor.isNoun("IN"));
assertFalse(RelationFeatureExtractor.isNoun("DT"));
assertFalse(RelationFeatureExtractor.isNoun("JJ"));
}

@Test
public void testPatternRecognition_ReverseStartSpanHeadOnly() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "alpha beta");
Constituent source = new Constituent("X", 1.0, "TEST", ta, 1, 2);
source.addAttribute("EntityHeadStartCharOffset", "7");
source.addAttribute("EntityHeadEndCharOffset", "10");
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 0, 1);
target.addAttribute("EntityHeadStartCharOffset", "0");
target.addAttribute("EntityHeadEndCharOffset", "5");
List<String> res = RelationFeatureExtractor.patternRecognition(source, target);
assertFalse(res.contains("SAME_SOURCE_TARGET_EXCEPTION"));
}

@Test
public void testGetLexicalFeaturePartCC_OverlappingSpanNoTokenBetween() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "doc", "x y z");
Constituent source = new Constituent("A", 1.0, "TEST", ta, 0, 1);
source.addAttribute("EntityHeadStartCharOffset", "0");
source.addAttribute("EntityHeadEndCharOffset", "1");
Constituent target = new Constituent("B", 1.0, "TEST", ta, 0, 1);
target.addAttribute("EntityHeadStartCharOffset", "0");
target.addAttribute("EntityHeadEndCharOffset", "1");
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartCC(r);
assertTrue(features.isEmpty());
}

@Test
public void testGetCorefTag_SourceMissingEntityId() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "Alice likes coffee");
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("OBJ", 1.0, "TEST", ta, 2, 3);
target.addAttribute("EntityID", "e1");
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
String result = extractor.getCorefTag(r);
assertEquals("FALSE", result);
}

@Test
public void testGetStructualFeature_BothConstituentsEqual() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "id", "same span mention");
View mentionView = new View(ViewNames.MENTION_ACE, "annotator", ta, 1.0);
Constituent c = new Constituent("MISC", 1.0, ViewNames.MENTION_ACE, ta, 0, 1);
c.addAttribute("EntityType", "PER");
mentionView.addConstituent(c);
ta.addView(ViewNames.MENTION_ACE, mentionView);
Relation rel = new Relation("rel", c, c, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getStructualFeature(rel);
assertTrue(result.contains("m1_in_m2") || result.contains("m2_in_m1") || result.contains("m1_m2_no_coverage"));
}

@Test
public void testPatternRecognition_WhenSpanOverlapButNoPunctuation() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("doc", "id", "name label value");
View dependencyView = new TreeView(ViewNames.DEPENDENCY_STANFORD, ta);
ta.addView(ViewNames.DEPENDENCY_STANFORD, dependencyView);
Constituent source = new Constituent("A", 1.0, "TEST", ta, 0, 1);
source.addAttribute("EntityHeadStartCharOffset", "0");
source.addAttribute("EntityHeadEndCharOffset", "1");
Constituent target = new Constituent("B", 1.0, "TEST", ta, 1, 2);
target.addAttribute("EntityHeadStartCharOffset", "6");
target.addAttribute("EntityHeadEndCharOffset", "11");
List<String> tags = RelationFeatureExtractor.patternRecognition(source, target);
assertFalse(tags.contains("FORMULAIC"));
}

@Test
public void testGetMentionFeature_ConstituentsEqualSpanAndType() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "entity");
Constituent c = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
c.addAttribute("EntityMentionType", "NAM");
c.addAttribute("EntityType", "PER");
Relation r = new Relation("rel", c, c, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> result = extractor.getMentionFeature(r);
assertTrue(result.contains("mlvl_NAM_NAM"));
assertTrue(result.contains("mlvl_cont_1_NAM_NAM_True"));
assertTrue(result.contains("mlvl_cont_2_NAM_NAM_True"));
}

@Test
public void testGetLexicalFeaturePartC_SourceImmediatelyBeforeTarget() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "ice cream spoon");
Constituent source = new Constituent("X", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("Y", 1.0, "TEST", ta, 2, 3);
Relation r = new Relation("rel", source, target, 1.0);
RelationFeatureExtractor extractor = new RelationFeatureExtractor();
List<String> features = extractor.getLexicalFeaturePartC(r);
assertEquals(1, features.size());
assertEquals("No_singleword", features.get(0));
}

@Test
public void testIsPossessive_POSAtFrontHeadEdge() throws Exception {
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test", "test", "his car was stolen");
View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "PRP$", 1.0f);
// pos.addTokenLabel(1, "NN", 1.0f);
// pos.addTokenLabel(2, "VBD", 1.0f);
// pos.addTokenLabel(3, "VBN", 1.0f);
ta.addView(ViewNames.POS, pos);
Constituent source = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
Constituent target = new Constituent("VEH", 1.0, "TEST", ta, 1, 2);
Relation relation = new Relation("rel", source, target, 1.0);
boolean result = RelationFeatureExtractor.isPossessive(relation);
assertTrue(result);
}
}
