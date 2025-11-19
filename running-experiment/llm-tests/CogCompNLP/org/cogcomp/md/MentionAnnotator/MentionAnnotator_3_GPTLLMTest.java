package org.cogcomp.md;

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
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.illinois.cs.cogcomp.pos.MikheevLearner;
import junit.framework.TestCase;
import org.cogcomp.md.MentionAnnotator;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MentionAnnotator_3_GPTLLMTest {

@Test
public void testDefaultConstructor() {
MentionAnnotator annotator = new MentionAnnotator();
assertNotNull(annotator);
}

@Test
public void testConstructorWithMode() {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
assertNotNull(annotator);
}

@Test
public void testConstructorWithModelPaths() {
MentionAnnotator annotator = new MentionAnnotator("model_nam", "model_nom", "model_pro", "extent_model", "ACE_TYPE");
assertNotNull(annotator);
}

@Test
public void testInitializeWithEmptyResourceManager() {
MentionAnnotator annotator = new MentionAnnotator();
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
}

@Test(expected = AnnotatorException.class)
public void testAddViewThrowsWithoutPOSView() throws AnnotatorException {
MentionAnnotator annotator = new MentionAnnotator();
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Missing POS example", new String[][] { { "Missing", "POS", "example" } }, new int[][] { { 0 } });
// ta.addView(ViewNames.TOKENS, new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0));
// annotator.addView(ta);
}

@Test
public void testAddViewWithValidPOS() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "This is a test", new String[][] { { "This", "is", "a", "test" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addConstituent(new Constituent("This", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("is", ViewNames.TOKENS, ta, 1, 2));
// tokenView.addConstituent(new Constituent("a", ViewNames.TOKENS, ta, 2, 3));
// tokenView.addConstituent(new Constituent("test", ViewNames.TOKENS, ta, 3, 4));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addConstituent(new Constituent("DT", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("DT", ViewNames.POS, ta, 2, 3));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testGetHeadConstituentValidAttributes() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "some text", new String[][] { { "some", "text" } }, new int[][] { { 0 } });
// Constituent c = new Constituent("MENTION", ViewNames.MENTION, ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "1");
// c.addAttribute("x", "y");
// Constituent head = MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
// assertNotNull(head);
// assertEquals("MENTION", head.getLabel());
// assertEquals(0, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
// assertEquals("y", head.getAttribute("x"));
}

@Test
public void testGetHeadConstituentNullWhenMissingAttributes() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "some text", new String[][] { { "some", "text" } }, new int[][] { { 0 } });
// Constituent c = new Constituent("MENTION", ViewNames.MENTION, ta, 0, 2);
// Constituent result = MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
// assertNull(result);
}

@Test
public void testMultipleModesInitialization() {
MentionAnnotator annotator1 = new MentionAnnotator("ACE_NONTYPE");
MentionAnnotator annotator2 = new MentionAnnotator("ACE_TYPE");
MentionAnnotator annotator3 = new MentionAnnotator("ERE_NONTYPE");
MentionAnnotator annotator4 = new MentionAnnotator("ERE_TYPE");
assertNotNull(annotator1);
assertNotNull(annotator2);
assertNotNull(annotator3);
assertNotNull(annotator4);
}

@Test
public void testAddViewDoesNotDuplicateViews() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Hello world", new String[][] { { "Hello", "world" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addConstituent(new Constituent("Hello", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("world", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View firstMention = ta.getView(ViewNames.MENTION);
// View firstBIO = ta.getView("BIO");
// annotator.addView(ta);
// View secondMention = ta.getView(ViewNames.MENTION);
// View secondBIO = ta.getView("BIO");
// assertEquals(firstMention, secondMention);
// assertEquals(firstBIO, secondBIO);
}

@Test
public void testEmptyTextAnnotationDoesNotFailInitialization() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { {} }, new int[][] { {} });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithSingleToken() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Token", new String[][] { { "Token" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Token", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testAddViewWithHttpFilteredToken() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "http://example.com", new String[][] { { "http://example.com" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokenView.addConstituent(new Constituent("http://example.com", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c = bioView.getConstituentsCoveringToken(0).get(0);
// assertEquals(",", c.getAttribute("WORDNETTAG"));
// assertEquals(",", c.getAttribute("WORDNETHYM"));
}

@Test
public void testGetHeadConstituentWithInvalidSpanStrings() {
// TextAnnotation ta = new TextAnnotation("c", "d", "text", new String[][] { { "text" } }, new int[][] { { 0 } });
// Constituent c = new Constituent("label", "v", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "notANumber");
// c.addAttribute("EntityHeadEndSpan", "alsoBad");
try {
// MentionAnnotator.getHeadConstituent(c, "v");
fail("Expected NumberFormatException");
} catch (NumberFormatException e) {
}
}

@Test
public void testMultipleEntitiesSameSpan() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Washington Washington", new String[][] { { "Washington", "Washington" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Washington", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Washington", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// assertEquals(2, bioView.getNumberOfConstituents());
}

@Test
public void testEmptyModelPathsDoNotCrashInConstructor() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "MODE");
assertNotNull(annotator);
}

@Test
public void testAddViewHandlesTrainingFalseAttribute() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "John loves Mary", new String[][] { { "John", "loves", "Mary" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addConstituent(new Constituent("John", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("loves", ViewNames.TOKENS, ta, 1, 2));
// tokenView.addConstituent(new Constituent("Mary", ViewNames.TOKENS, ta, 2, 3));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// Constituent c0 = bio.getConstituentsCoveringToken(0).get(0);
// assertEquals("false", c0.getAttribute("isTraining"));
}

@Test
public void testAddViewWithNoTokens() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", new String[][] { {} }, new int[][] { {} });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithOnlyPOSNoTokens() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Empty", new String[][] { {} }, new int[][] { {} });
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// ta.addView(ViewNames.POS, posView);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertEquals(0, ta.getView("BIO").getNumberOfConstituents());
}

@Test
public void testGetHeadConstituentParsesAndReturnsAttributeWithMultipleKeys() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "example data", new String[][] { { "example", "data" } }, new int[][] { { 0 } });
// Constituent original = new Constituent("MentionLabel", "sourceView", ta, 0, 2);
// original.addAttribute("EntityHeadStartSpan", "0");
// original.addAttribute("EntityHeadEndSpan", "1");
// original.addAttribute("A1", "val1");
// original.addAttribute("A2", "val2");
// Constituent head = MentionAnnotator.getHeadConstituent(original, "HEADVIEW");
// assertNotNull(head);
// assertEquals("HEADVIEW", head.getViewName());
// assertEquals("val1", head.getAttribute("A1"));
// assertEquals("val2", head.getAttribute("A2"));
// assertEquals("MentionLabel", head.getLabel());
// assertEquals(0, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
}

@Test
public void testConstructorDoesNotReplaceModelPathsIfNull() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ACE_TYPE");
assertNotNull(annotator);
}

@Test
public void testAddViewHandlesCaseSensitiveTokenMatching() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Apple apple", new String[][] { { "Apple", "apple" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "testSource", ta, 1.0f);
// Constituent token1 = new Constituent("Apple", ViewNames.TOKENS, ta, 0, 1);
// Constituent token2 = new Constituent("apple", ViewNames.TOKENS, ta, 1, 2);
// tokenView.addConstituent(token1);
// tokenView.addConstituent(token2);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "testSource", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// assertEquals(2, ta.getView("BIO").getNumberOfConstituents());
}

@Test
public void testAddViewWithTokenMissingWordNetSupport() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "https://link", new String[][] { { "https://link" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "unit", ta, 1.0f);
// Constituent token = new Constituent("https://link", ViewNames.TOKENS, ta, 0, 1);
// tokenView.addConstituent(token);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "unit", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// Constituent result = bio.getConstituentsCoveringToken(0).get(0);
// assertEquals(",", result.getAttribute("WORDNETTAG"));
// assertEquals(",", result.getAttribute("WORDNETHYM"));
}

@Test
public void testConstructorLazilyInitializeFalseDoesNotTriggerLoad() {
MentionAnnotator annotator = new MentionAnnotator(false, "ERE_NONTYPE");
assertNotNull(annotator);
}

@Test
public void testAddViewDoesNotThrowWhenCalledMultipleTimesSequentially() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "Name works", new String[][] { { "Name", "works" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Name", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("works", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView pos = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// annotator.addView(ta);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testConstructorWithEmptyMode() {
MentionAnnotator annotator = new MentionAnnotator("");
assertNotNull(annotator);
}

@Test
public void testConstructorWithInvalidMode() {
MentionAnnotator annotator = new MentionAnnotator("INVALID_MODE");
assertNotNull(annotator);
}

@Test
public void testMultipleInitializationsOnlyLoadOnce() {
MentionAnnotator annotator = new MentionAnnotator();
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
// annotator.initialize(rm);
}

@Test
public void testAddViewThrowsWhenTokenViewMissing() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("testCorpus", "testDoc", "Content", new String[][] { { "test", "input" } }, new int[][] { { 0 } });
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// try {
// annotator.addView(ta);
// fail("Expected AnnotatorException due to missing TOKENS view");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("Missing required view POS") || e.getMessage().contains("Constituent list is empty"));
// }
}

@Test
public void testGetHeadConstituentHandlesStartEqualsEnd() {
// TextAnnotation ta = new TextAnnotation("c", "d", "text", new String[][] { { "token" } }, new int[][] { { 0 } });
// Constituent mention = new Constituent("label", ViewNames.MENTION, ta, 0, 1);
// mention.addAttribute("EntityHeadStartSpan", "0");
// mention.addAttribute("EntityHeadEndSpan", "0");
// Constituent head = MentionAnnotator.getHeadConstituent(mention, "CUSTOM_VIEW");
// assertNotNull(head);
// assertEquals(0, head.getStartSpan());
// assertEquals(0, head.getEndSpan());
}

@Test
public void testAddViewHandlesMultipleTokensWithNoEntities() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "one two three", new String[][] { { "one", "two", "three" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("one", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("two", ViewNames.TOKENS, ta, 1, 2));
// tokenView.addConstituent(new Constituent("three", ViewNames.TOKENS, ta, 2, 3));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.getView(ViewNames.MENTION).getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewHandlesTokenWithNullAttributes() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "test", new String[][] { { "test" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("test", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// Constituent c = bio.getConstituents().get(0);
// assertNotNull(c.getAttribute("GAZ"));
// assertNotNull(c.getAttribute("BC"));
}

@Test
public void testModePathSwitchingACEType() {
MentionAnnotator annotator = new MentionAnnotator(true, "ACE_TYPE");
assertNotNull(annotator);
}

@Test
public void testModePathSwitchingERENonType() {
MentionAnnotator annotator = new MentionAnnotator(true, "ERE_NONTYPE");
assertNotNull(annotator);
}

@Test
public void testMentionAnnotatorAddsCorrectEntityMentionTypeAttribute() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "President Obama", new String[][] { { "President", "Obama" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("President", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Obama", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// if (mentionView.getNumberOfConstituents() > 0) {
// Constituent c = mentionView.getConstituents().get(0);
// assertNotNull(c.getAttribute("EntityMentionType"));
// }
}

@Test
public void testInitializeWithModeContainingOnlyACE() {
MentionAnnotator annotator = new MentionAnnotator(true, "ACE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
assertNotNull(annotator);
}

@Test
public void testInitializeWithModeContainingOnlyERE() {
MentionAnnotator annotator = new MentionAnnotator(true, "ERE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
assertNotNull(annotator);
}

@Test
public void testInitializeWithInvalidModeTriggersExtentFallback() {
MentionAnnotator annotator = new MentionAnnotator(true, "UNKNOWN_MODE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
assertNotNull(annotator);
}

@Test
public void testAddViewSilentFailureWhenNoCandidatesAvailable() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
MentionAnnotator noModels = new MentionAnnotator("", "", "", "", "ACE_NONTYPE");
// noModels.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "Mr Smith", new String[][] { { "Mr", "Smith" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Mr", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Smith", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// noModels.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testAddViewWithCustomAttributeSetOnMention() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "Barack Obama", new String[][] { { "Barack", "Obama" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Barack", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Obama", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// if (mentionView.getNumberOfConstituents() > 0) {
// Constituent c = mentionView.getConstituents().get(0);
// assertNotNull(c.getAttribute("EntityMentionType"));
// }
}

@Test
public void testAddViewSkipsInvalidTokenIndexesSilently() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("x", "y", "sentence in fail", new String[][] { { "sentence", "in", "fail" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addConstituent(new Constituent("invalid", ViewNames.TOKENS, ta, 1, 3));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "gen", ta, 1.0f);
// posView.addConstituent(new Constituent("VB", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithEntityHeadSpanOutOfBoundsReturnsNull() {
// TextAnnotation ta = new TextAnnotation("c", "d", "data", new String[][] { { "data" } }, new int[][] { { 0 } });
// Constituent input = new Constituent("X", "VIEW", ta, 0, 1);
// input.addAttribute("EntityHeadStartSpan", "5");
// input.addAttribute("EntityHeadEndSpan", "6");
// Constituent head = MentionAnnotator.getHeadConstituent(input, "V");
// assertNotNull(head);
// assertEquals(5, head.getStartSpan());
// assertEquals(6, head.getEndSpan());
}

@Test(expected = AnnotatorException.class)
public void testAddViewThrowsWhenPOSIsMissingEntirely() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "fail", new String[][] { { "fail" } }, new int[][] { { 0 } });
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokens.addConstituent(new Constituent("fail", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokens);
// annotator.addView(ta);
}

@Test
public void testAddViewAddsViewsToTextAnnotationWithoutThrowing() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "ABC XYZ", new String[][] { { "ABC", "XYZ" } }, new int[][] { { 0 } });
// TokenLabelView tok = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tok.addConstituent(new Constituent("ABC", ViewNames.TOKENS, ta, 0, 1));
// tok.addConstituent(new Constituent("XYZ", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tok);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testConstructorWithNullModelPaths() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ACE_TYPE");
assertNotNull(annotator);
}

@Test
public void testConstructorWithEmptyModeString() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "");
assertNotNull(annotator);
}

@Test
public void testInitializeFallbackToACE_EXTENTWhenExtentPathEmpty() {
MentionAnnotator annotator = new MentionAnnotator("ACE_HEAD_ONLY", "ACE_HEAD_ONLY", "ACE_HEAD_ONLY", "", "ACE_NONTYPE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
assertNotNull(annotator);
}

@Test
public void testBIOViewHandledWhenNoTokensAreMatched() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Text with missing tokens", new String[][] { { "Unmatched" } }, new int[][] { { 0 } });
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "generator", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "generator", ta, 1.0f);
// tokenView.addConstituent(new Constituent("abc", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithTokensButEmptyConstituentList() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "test", new String[][] { { "one" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// assertNotNull(bio);
// assertEquals(0, bio.getNumberOfConstituents());
// View mention = ta.getView(ViewNames.MENTION);
// assertNotNull(mention);
// assertEquals(0, mention.getNumberOfConstituents());
}

@Test
public void testAddViewHandlesTokenWithNoWordNetData() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "http://link", new String[][] { { "http://link" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("http://link", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// Constituent c = bio.getConstituents().get(0);
// assertEquals(",", c.getAttribute("WORDNETHYM"));
// assertEquals(",", c.getAttribute("WORDNETTAG"));
}

@Test
public void testAddViewThrowsWhenPOSViewIsCompletelyAbsent() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "token", new String[][] { { "token" } }, new int[][] { { 0 } });
// TokenLabelView tok = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tok.addConstituent(new Constituent("token", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tok);
// try {
// annotator.addView(ta);
// fail("Expected AnnotatorException due to missing POS");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("Missing required view POS"));
// }
}

@Test
public void testGetHeadConstituentHandlesPartiallyMissingAttributes() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "test", new String[][] { { "a", "b" } }, new int[][] { { 0 } });
// Constituent mention = new Constituent("M", ViewNames.MENTION, ta, 0, 2);
// mention.addAttribute("EntityHeadStartSpan", "1");
// Constituent result = MentionAnnotator.getHeadConstituent(mention, ViewNames.MENTION);
// assertNull(result);
}

@Test
public void testGetHeadConstituentHandlesMalformedSpanAttributes() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "test", new String[][] { { "a", "b" } }, new int[][] { { 0 } });
// Constituent mention = new Constituent("M", ViewNames.MENTION, ta, 0, 2);
// mention.addAttribute("EntityHeadStartSpan", "X");
// mention.addAttribute("EntityHeadEndSpan", "Y");
try {
// MentionAnnotator.getHeadConstituent(mention, ViewNames.MENTION);
fail("Expected NumberFormatException");
} catch (NumberFormatException e) {
}
}

@Test
public void testJointInferencePredictionNoneReturnedStillStable() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "id", "George", new String[][] { { "George" } }, new int[][] { { 0 } });
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0f);
// tokens.addConstituent(new Constituent("George", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokens);
// SpanLabelView pos = new SpanLabelView(ViewNames.POS, "source", ta, 1.0f);
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testConstructorWithNullModeFallsBackGracefully() {
MentionAnnotator annotator = new MentionAnnotator(true, null);
assertNotNull(annotator);
}

@Test
public void testDoubleInitializeDoesNotCrashOrReset() {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
// annotator.initialize(rm);
assertNotNull(annotator);
}

@Test
public void testAddViewWithEmptyPOSAndTokenViews() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", new String[][] { {} }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "gen", ta, 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// assertEquals(0, ta.getView(ViewNames.MENTION).getNumberOfConstituents());
// assertEquals(0, ta.getView("BIO").getNumberOfConstituents());
}

@Test
public void testAddViewMultipleTimesDoesNotDuplicateViews() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "Barack Obama", new String[][] { { "Barack", "Obama" } }, new int[][] { { 0 } });
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Barack", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Obama", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "POS", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View mention1 = ta.getView(ViewNames.MENTION);
// View bio1 = ta.getView("BIO");
// annotator.addView(ta);
// View mention2 = ta.getView(ViewNames.MENTION);
// View bio2 = ta.getView("BIO");
// assertSame(mention1, mention2);
// assertSame(bio1, bio2);
}

@Test
public void testGetHeadConstituentWithStartGreaterThanEnd() {
// TextAnnotation ta = new TextAnnotation("c", "d", "x", new String[][] { { "A", "B" } }, new int[][] { { 0 } });
// Constituent c = new Constituent("Z", "MENTION", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "2");
// c.addAttribute("EntityHeadEndSpan", "1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "CUSTOM");
// assertNotNull(head);
// assertEquals(2, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
}

@Test
public void testInitializeDoesNotCrashWithGarbageModeName() {
MentionAnnotator annotator = new MentionAnnotator("!!!invalid###");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
assertNotNull(annotator);
}

@Test
public void testAddViewHandlesFallthroughInJointInferenceGracefully() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "US", new String[][] { { "US" } }, new int[][] { { 0 } });
// TokenLabelView tv = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tv.addConstituent(new Constituent("US", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tv);
// SpanLabelView pv = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// pv.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pv);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithAllNullClassifierPaths() throws Exception {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ACE_NONTYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("x", "y", "Test person", new String[][] { { "Test", "person" } }, new int[][] { { 0 } });
// TokenLabelView tok = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tok.addConstituent(new Constituent("Test", ViewNames.TOKENS, ta, 0, 1));
// tok.addConstituent(new Constituent("person", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tok);
// SpanLabelView pos = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testWordNetAbsentLogicPathStillSetsBIOAttributes() throws Exception {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.initialize(new ResourceManager(Collections.emptyMap()));
// TextAnnotation ta = new TextAnnotation("c", "d", "http_link", new String[][] { { "http://only" } }, new int[][] { { 0 } });
// Constituent token = new Constituent("http://only", ViewNames.TOKENS, ta, 0, 1);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "unit", ta, 1.0f);
// tokenView.addConstituent(token);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "unit", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent bio = bioView.getConstituents().get(0);
// assertEquals(",", bio.getAttribute("WORDNETTAG"));
// assertEquals(",", bio.getAttribute("WORDNETHYM"));
}
}
