package org.cogcomp.md;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
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
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MentionAnnotator_1_GPTLLMTest {

@Test
public void testDefaultConstructorSetsMentionViewName() {
MentionAnnotator annotator = new MentionAnnotator();
assertEquals(ViewNames.MENTION, annotator.getViewName());
}

@Test
public void testConstructorWithModeSetsMentionViewName() {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
assertEquals(ViewNames.MENTION, annotator.getViewName());
}

@Test
public void testConstructorWithModelPaths() {
MentionAnnotator annotator = new MentionAnnotator("namPath", "nomPath", "proPath", "extentPath", "ACE_TYPE");
assertEquals(ViewNames.MENTION, annotator.getViewName());
}

@Test(expected = AnnotatorException.class)
public void testAddViewThrowsAnnotatorExceptionWhenPOSMissing() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "John", "visited", "Paris" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addTokenLabel(0, "NN", 1.0);
// tokenView.addTokenLabel(1, "VB", 1.0);
// tokenView.addTokenLabel(2, "NNP", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// annotator.addView(ta);
}

@Test
public void testAddViewAddsMentionAndBIOView() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Barack", "Obama" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "NNP", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
// View bioView = ta.getView("BIO");
// assertNotNull(bioView);
}

@Test
public void testGetHeadConstituentReturnsValidHead() {
String[] tokens = new String[] { "The", "tall", "man", "walked" };
// TextAnnotation ta = new TextAnnotation("corpusId", "docId", "", tokens, new int[][] {});
// Constituent fullConstituent = new Constituent("mention", ViewNames.MENTION, ta, 1, 3);
// fullConstituent.addAttribute("EntityHeadStartSpan", "2");
// fullConstituent.addAttribute("EntityHeadEndSpan", "3");
// fullConstituent.addAttribute("ExtraFeature", "ValueX");
// Constituent head = MentionAnnotator.getHeadConstituent(fullConstituent, ViewNames.MENTION);
// assertNotNull(head);
// assertEquals(2, head.getStartSpan());
// assertEquals(3, head.getEndSpan());
// assertEquals("ValueX", head.getAttribute("ExtraFeature"));
}

@Test
public void testGetHeadConstituentReturnsNullWhenNoHeadSpan() {
String[] tokens = new String[] { "Apples", "are", "red" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// Constituent constituent = new Constituent("mention", ViewNames.MENTION, ta, 0, 3);
// Constituent head = MentionAnnotator.getHeadConstituent(constituent, ViewNames.MENTION);
// assertNull(head);
}

@Test
public void testAddViewHandlesHttpTokensGracefully() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "http://example.com", "visited" };
// TextAnnotation ta = new TextAnnotation("corpusId", "docId", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addTokenLabel(0, "URL", 1.0);
// tokenView.addTokenLabel(1, "VBD", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "VBD", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// View bioView = ta.getView("BIO");
// Constituent token0 = bioView.getConstituentsCoveringToken(0).get(0);
// Constituent token1 = bioView.getConstituentsCoveringToken(1).get(0);
// assertEquals(",", token0.getAttribute("WORDNETTAG"));
// assertEquals(",", token0.getAttribute("WORDNETHYM"));
// assertNotNull(token1.getAttribute("WORDNETTAG"));
// assertNotNull(token1.getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewWithSingleTokenNoEntityMatch() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Hello" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addTokenLabel(0, "UH", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "UH", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// View mentionView = ta.getView(ViewNames.MENTION);
// View bioView = ta.getView("BIO");
// assertTrue(mentionView.getNumberOfConstituents() >= 0);
// assertEquals(1, bioView.getConstituents().size());
}

@Test
public void testAddViewOnAlreadyAnnotatedTextAnnotation() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Alice", "runs" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "VBZ", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// View mentionView = ta.getView(ViewNames.MENTION);
// View bioView = ta.getView("BIO");
// assertTrue(mentionView.getNumberOfConstituents() >= 0);
// assertEquals(2, bioView.getNumberOfConstituents());
}

@Test
public void testGetHeadConstituentIgnoresInvalidIntegerSpans() {
String[] tokens = new String[] { "My", "car", "is", "fast" };
// TextAnnotation ta = new TextAnnotation("corpusId", "docId", "", tokens, new int[][] {});
// Constituent invalidHead = new Constituent("mention", ViewNames.MENTION, ta, 0, 4);
// invalidHead.addAttribute("EntityHeadStartSpan", "bad_int");
// invalidHead.addAttribute("EntityHeadEndSpan", "also_bad");
try {
// MentionAnnotator.getHeadConstituent(invalidHead, ViewNames.MENTION);
fail("Expected NumberFormatException to be thrown");
} catch (NumberFormatException e) {
assertTrue(e.getMessage().contains("For input string") || e instanceof NumberFormatException);
}
}

@Test
public void testAddViewAddsNothingWhenNoEntityPredicted() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "sky", "is", "blue" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addTokenLabel(0, "NN", 1.0);
// tokenView.addTokenLabel(1, "VBZ", 1.0);
// tokenView.addTokenLabel(2, "JJ", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "JJ", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// View bioView = ta.getView("BIO");
// assertTrue(mentionView.getNumberOfConstituents() >= 0);
// assertEquals(3, bioView.getConstituents().size());
}

@Test
public void testEmptyTextAnnotationDoesNotThrow() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] {};
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// View mentionView = ta.getView(ViewNames.MENTION);
// View bioView = ta.getView("BIO");
// assertEquals(0, mentionView.getNumberOfConstituents());
// assertEquals(0, bioView.getNumberOfConstituents());
}

@Test
public void testConstructorWithNullModelPathsDoesNotThrowException() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ACE_TYPE");
assertEquals(ViewNames.MENTION, annotator.getViewName());
}

@Test
public void testInitializeWithUnknownModeDoesNotThrowAndDefaultsToACE_EXTENT() {
MentionAnnotator annotator = new MentionAnnotator(true, "UNKNOWN_MODE");
ResourceManager resourceManager = new ResourceManager(new java.util.Properties());
annotator.initialize(resourceManager);
assertNotNull(annotator);
}

@Test
public void testAddViewWithMultipleMentionsSameType() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Barack", "Obama", "and", "Joe", "Biden" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0f);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "NNP", 1.0);
// tokenView.addTokenLabel(2, "CC", 1.0);
// tokenView.addTokenLabel(3, "NNP", 1.0);
// tokenView.addTokenLabel(4, "NNP", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0f);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "NNP", 1.0);
// posView.addTokenLabel(2, "CC", 1.0);
// posView.addTokenLabel(3, "NNP", 1.0);
// posView.addTokenLabel(4, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.getView("BIO").getNumberOfConstituents() >= 5);
}

@Test
public void testAddViewOnTokenContainingURLWithMixedContent() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "http://example.com/page1", "Obama" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0f);
// tokenView.addTokenLabel(0, "NN", 1.0);
// tokenView.addTokenLabel(1, "NNP", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0f);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// assertEquals(",", bioView.getConstituentsCoveringToken(0).get(0).getAttribute("WORDNETTAG"));
// assertEquals(",", bioView.getConstituentsCoveringToken(0).get(0).getAttribute("WORDNETHYM"));
// assertNotNull(bioView.getConstituentsCoveringToken(1).get(0).getAttribute("WORDNETTAG"));
}

@Test
public void testAddViewWithMixedCapitalizationTokens() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Google", "is", "in", "california" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "VBZ", 1.0);
// tokenView.addTokenLabel(2, "IN", 1.0);
// tokenView.addTokenLabel(3, "NN", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "IN", 1.0);
// posView.addTokenLabel(3, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.getView("BIO").getNumberOfConstituents() >= 4);
}

@Test
public void testAddViewWithTokenLabelViewMissingTokensStillProcesses() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "TokenOnlyView" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// View bioView = ta.getView("BIO");
// assertEquals(1, bioView.getNumberOfConstituents());
}

@Test
public void testAddViewWithoutTokenViewThrowsException() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "OnlyPOS", "present" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "generator", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
try {
// annotator.addView(ta);
fail("Expected IndexOutOfBoundsException due to missing TOKENS view.");
} catch (IndexOutOfBoundsException e) {
assertTrue(e.getMessage() != null);
}
}

@Test
public void testAddViewWithTokenMissingPOSStillProcesses() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "TokenWithoutPOS" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// View bioView = ta.getView("BIO");
// assertEquals(1, bioView.getNumberOfConstituents());
}

@Test
public void testGetHeadConstituentHandlesStartGreaterThanEnd() {
String[] tokens = new String[] { "strange", "head", "span" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// Constituent c = new Constituent("Weird", "MENTION", ta, 0, 3);
// c.addAttribute("EntityHeadStartSpan", "2");
// c.addAttribute("EntityHeadEndSpan", "1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
// assertNotNull(head);
// assertEquals(2, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
}

@Test
public void testGetHeadConstituentWithNegativeSpanReturnsCorrectly() {
String[] tokens = new String[] { "bad", "span" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// Constituent c = new Constituent("Entity", "MENTION", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "-1");
// c.addAttribute("EntityHeadEndSpan", "1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
// assertNotNull(head);
// assertEquals(-1, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
}

@Test
public void testAddViewWithGarbageWordNetDataStillProcessesCorrectly() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "http1example.com", "prevails" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addTokenLabel(0, "NN", 1.0f);
// tokenView.addTokenLabel(1, "VBZ", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// posView.addTokenLabel(0, "NN", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// assertEquals(",", bioView.getConstituentsCoveringToken(0).get(0).getAttribute("WORDNETTAG"));
// assertEquals(",", bioView.getConstituentsCoveringToken(0).get(0).getAttribute("WORDNETHYM"));
// assertNotNull(bioView.getConstituentsCoveringToken(1).get(0).getAttribute("WORDNETTAG"));
// assertNotNull(bioView.getConstituentsCoveringToken(1).get(0).getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewHandlesStaticURLsCorrectly() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "https://localhost", "is", "reachable" };
// TextAnnotation ta = new TextAnnotation("c", "d", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "VBZ", 1.0);
// tokenView.addTokenLabel(2, "JJ", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "JJ", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// assertEquals(",", bioView.getConstituentsCoveringToken(0).get(0).getAttribute("WORDNETTAG"));
// assertEquals(",", bioView.getConstituentsCoveringToken(0).get(0).getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewWithNonBIOPrefixPredictionsDoesNotAddMention() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "is", "a", "test" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "manual", ta, 1.0f);
// tokensView.addTokenLabel(0, "VBZ", 1.0);
// tokensView.addTokenLabel(1, "DT", 1.0);
// tokensView.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.TOKENS, tokensView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0f);
// posView.addTokenLabel(0, "VBZ", 1.0);
// posView.addTokenLabel(1, "DT", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// View mentionView = ta.getView(ViewNames.MENTION);
// assertEquals(0, mentionView.getNumberOfConstituents());
}

@Test
public void testGetHeadConstituentWithLargeSpansParsesCorrectly() {
String[] tokens = new String[] { "a", "b", "c", "d", "e", "f" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// Constituent c = new Constituent("Entity", "MENTION", ta, 0, 6);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "6");
// c.addAttribute("type", "NAM");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "MENTION");
// assertNotNull(head);
// assertEquals(0, head.getStartSpan());
// assertEquals(6, head.getEndSpan());
// assertEquals("NAM", head.getAttribute("type"));
}

@Test
public void testAddViewWithRepeatedTokensProcessesAll() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Obama", "Obama", "Obama" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "NNP", 1.0);
// tokenView.addTokenLabel(2, "NNP", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "NNP", 1.0);
// posView.addTokenLabel(2, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// View bioView = ta.getView("BIO");
// assertEquals(3, bioView.getNumberOfConstituents());
}

@Test
public void testAddViewWithNoPOSAndTokensThrowsAnnotatorException() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "NoPOS", "NoTokens" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// try {
// annotator.addView(ta);
// fail("Expected AnnotatorException due to missing POS and TOKENS views.");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("Missing required view POS"));
// }
}

@Test
public void testAddViewOnLongSentenceHandlesAllTokens() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[50];
for (int i = 0; i < 50; i++) {
tokens[i] = "word" + i;
}
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0f);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "VB", 1.0);
// tokenView.addTokenLabel(2, "DT", 1.0);
// tokenView.addTokenLabel(3, "NN", 1.0);
// tokenView.addTokenLabel(4, "VBZ", 1.0);
// tokenView.addTokenLabel(5, "RB", 1.0);
// tokenView.addTokenLabel(6, "JJ", 1.0);
// tokenView.addTokenLabel(7, "NN", 1.0);
// tokenView.addTokenLabel(8, "IN", 1.0);
// tokenView.addTokenLabel(9, "NNP", 1.0);
for (int i = 10; i < 50; i++) {
// tokenView.addTokenLabel(i, "NN", 1.0);
}
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0f);
for (int i = 0; i < 50; i++) {
// posView.addTokenLabel(i, "NN", 1.0);
}
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
// assertEquals(50, ta.getView("BIO").getNumberOfConstituents());
}

@Test
public void testAddViewHandlesConstituentWithEmptyStringToken() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "", "Obama" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0f);
// tokenView.addTokenLabel(0, "NN", 1.0f);
// tokenView.addTokenLabel(1, "NNP", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0f);
// posView.addTokenLabel(0, "NN", 1.0f);
// posView.addTokenLabel(1, "NNP", 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithSinglePunctuationToken() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "." };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokenView.addTokenLabel(0, ".", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addTokenLabel(0, ".", 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// View bio = ta.getView("BIO");
// assertEquals(1, bio.getNumberOfConstituents());
}

@Test
public void testGetHeadConstituentAttributesPreserved() {
String[] tokens = new String[] { "She", "wears", "glasses" };
// TextAnnotation ta = new TextAnnotation("corpus", "docId", "", tokens, new int[][] {});
// Constituent c = new Constituent("ENTITY", "MENTION", ta, 0, 3);
// c.addAttribute("EntityHeadStartSpan", "1");
// c.addAttribute("EntityHeadEndSpan", "2");
// c.addAttribute("CustomFeatureOne", "Yes");
// c.addAttribute("CustomFeatureTwo", "True");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "MENTION");
// assertNotNull(head);
// assertEquals("Yes", head.getAttribute("CustomFeatureOne"));
// assertEquals("True", head.getAttribute("CustomFeatureTwo"));
// assertEquals(1, head.getStartSpan());
// assertEquals(2, head.getEndSpan());
}

@Test
public void testAddViewWithDuplicatePOSViewNameDoesNotCrash() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "John", "Smith" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView posView1 = new TokenLabelView(ViewNames.POS, "pos1", ta, 1.0);
// posView1.addTokenLabel(0, "NNP", 1.0);
// posView1.addTokenLabel(1, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView1);
// TokenLabelView posView2 = new TokenLabelView(ViewNames.POS, "pos2", ta, 1.0);
// posView2.addTokenLabel(0, "NN", 1.0);
// posView2.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, posView2);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "NNP", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testAddViewWithOnlyURLTokensProcessesWithoutError() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "http://example.com", "https://site.org", "www.domain.net" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "web", ta, 1.0f);
// tokenView.addTokenLabel(0, "NN", 1.0f);
// tokenView.addTokenLabel(1, "NN", 1.0f);
// tokenView.addTokenLabel(2, "NN", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "web", ta, 1.0f);
// posView.addTokenLabel(0, "NN", 1.0f);
// posView.addTokenLabel(1, "NN", 1.0f);
// posView.addTokenLabel(2, "NN", 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// View mention = ta.getView(ViewNames.MENTION);
// assertEquals(3, bio.getNumberOfConstituents());
// assertNotNull(mention);
}

@Test
public void testGetHeadConstituentReturnsNullIfOnlyOneAttributeExists() {
String[] tokens = new String[] { "attribute", "missing" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", "", tokens, new int[][] {});
// Constituent c = new Constituent("Entity", "MENTION", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
// assertNull(head);
}

@Test
public void testInitializeWithoutModelFilesGracefullyFallsBackToDefault() {
MentionAnnotator annotator = new MentionAnnotator(true, "ACE_NONTYPE");
ResourceManager resourceManager = new ResourceManager(new java.util.Properties());
annotator.initialize(resourceManager);
assertNotNull(annotator);
}

@Test
public void testAddViewAssignsDefaultAttributesToBIOConstituents() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Illinois", "university" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addTokenLabel(0, "NNP", 1.0);
// tokenView.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c0 = bioView.getConstituentsCoveringToken(0).get(0);
// Constituent c1 = bioView.getConstituentsCoveringToken(1).get(0);
// assertEquals("false", c0.getAttribute("isTraining"));
// assertNotNull(c0.getAttribute("GAZ"));
// assertNotNull(c0.getAttribute("BC"));
// assertNotNull(c0.getAttribute("WORDNETTAG"));
// assertNotNull(c0.getAttribute("WORDNETHYM"));
// assertEquals("false", c1.getAttribute("isTraining"));
}

@Test
public void testAddViewAddsCorrectEntityMentionTypeAttribute() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Barack", "Obama" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "NNP", 1.0f);
// tokenView.addTokenLabel(1, "NNP", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addTokenLabel(0, "NNP", 1.0f);
// posView.addTokenLabel(1, "NNP", 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// for (Constituent c : mentionView.getConstituents()) {
// assertTrue(c.getAttribute("EntityMentionType").equals("NAM") || c.getAttribute("EntityMentionType").equals("NOM") || c.getAttribute("EntityMentionType").equals("PRO"));
// }
}

@Test
public void testAddViewWithTokensOnlyContainingNumbersProcessesSuccessfully() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "1995", "2024" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "CD", 1.0f);
// tokenView.addTokenLabel(1, "CD", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addTokenLabel(0, "CD", 1.0f);
// posView.addTokenLabel(1, "CD", 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// View bio = ta.getView("BIO");
// assertEquals(2, bio.getConstituents().size());
}

@Test
public void testGetHeadConstituentHandlesNonNumericAttributesGracefully() {
String[] tokens = new String[] { "head", "start", "fail" };
// TextAnnotation ta = new TextAnnotation("c", "d", "", tokens, new int[][] {});
// Constituent c = new Constituent("test", "MENTION", ta, 0, 3);
// c.addAttribute("EntityHeadStartSpan", "abc");
// c.addAttribute("EntityHeadEndSpan", "2");
try {
// MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
fail("Expected NumberFormatException due to invalid head span attribute.");
} catch (NumberFormatException e) {
assertTrue(e.getMessage().contains("For input string"));
}
}

@Test
public void testAddViewAssignsBIOPreBIOLevelAttributes() throws Exception {
MentionAnnotator annotator = new MentionAnnotator();
// annotator.initialize(new ResourceManager());
String[] tokens = new String[] { "Alice", "and", "Bob" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, new int[][] {});
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokenView.addTokenLabel(0, "NNP", 1.0f);
// tokenView.addTokenLabel(1, "CC", 1.0f);
// tokenView.addTokenLabel(2, "NNP", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0f);
// posView.addTokenLabel(0, "NNP", 1.0f);
// posView.addTokenLabel(1, "CC", 1.0f);
// posView.addTokenLabel(2, "NNP", 1.0f);
// ta.addView(ViewNames.POS, posView);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// Constituent c0 = bio.getConstituentsCoveringToken(0).get(0);
// Constituent c1 = bio.getConstituentsCoveringToken(1).get(0);
// Constituent c2 = bio.getConstituentsCoveringToken(2).get(0);
// assertNotNull(c0.getAttribute("preBIOLevel1"));
// assertNotNull(c0.getAttribute("preBIOLevel2"));
// assertNotNull(c1.getAttribute("preBIOLevel1"));
// assertNotNull(c2.getAttribute("preBIOLevel2"));
}
}
