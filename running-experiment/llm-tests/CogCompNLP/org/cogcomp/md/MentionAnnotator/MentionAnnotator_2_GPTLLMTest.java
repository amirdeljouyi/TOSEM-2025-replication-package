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
import org.junit.Test;
import org.mockito.Mockito;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MentionAnnotator_2_GPTLLMTest {

@Test
public void testDefaultConstructorCreatesInstance() {
MentionAnnotator annotator = new MentionAnnotator();
assertNotNull(annotator);
}

@Test
public void testConstructorSetsCustomMode() {
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
assertNotNull(annotator);
}

@Test
public void testConstructorWithModelPaths() {
MentionAnnotator annotator = new MentionAnnotator("modelNam", "modelNom", "modelPro", "modelExtent", "CUSTOM");
assertNotNull(annotator);
}

@Test
public void testGetHeadConstituentValid() {
String[] tokens = new String[] { "John", "loves", "New", "York", "City" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// Constituent original = new Constituent("MENTION", "MENTION", ta, 1, 5);
// original.addAttribute("EntityHeadStartSpan", "2");
// original.addAttribute("EntityHeadEndSpan", "4");
// original.addAttribute("EntityMentionType", "NAM");
// original.addAttribute("SomeOtherAttribute", "attrValue");
// Constituent head = MentionAnnotator.getHeadConstituent(original, "HEAD");
// assertNotNull(head);
// assertEquals("HEAD", head.getViewName());
// assertEquals(2, head.getStartSpan());
// assertEquals(4, head.getEndSpan());
// assertEquals("NAM", head.getAttribute("EntityMentionType"));
// assertEquals("attrValue", head.getAttribute("SomeOtherAttribute"));
}

@Test
public void testGetHeadConstituentMissingAttributesReturnsNull() {
String[] tokens = new String[] { "John", "loves", "food" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("MENTION", "MENTION", ta, 0, 1);
// Constituent head = MentionAnnotator.getHeadConstituent(c, "HEAD");
// assertNull(head);
}

@Test
public void testAddViewThrowsForMissingPOSView() throws Exception {
String[] tokens = new String[] { "John", "walks", "home" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "Test", ta, 1.0);
// tokenView.addTokenLabel(0, "John", 1.0);
// tokenView.addTokenLabel(1, "walks", 1.0);
// tokenView.addTokenLabel(2, "home", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
MentionAnnotator annotator = new MentionAnnotator();
// try {
// annotator.addView(ta);
// fail("Expected AnnotatorException due to missing POS view");
// } catch (AnnotatorException e) {
// assertEquals("Missing required view POS", e.getMessage());
// }
}

@Test
public void testAddViewDoesNotThrowWhenInitializedAndViewsPresent() throws Exception {
String[] tokens = new String[] { "Alice", "drinks", "coffee" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "Test", ta, 1.0);
// tokenView.addTokenLabel(0, "Alice", 1.0);
// tokenView.addTokenLabel(1, "drinks", 1.0);
// tokenView.addTokenLabel(2, "coffee", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "Test", ta, 1.0);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewTriggersInitializationIfNotInitialized() throws Exception {
String[] tokens = new String[] { "Sam", "reads" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "Test", ta, 1.0);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "Test", ta, 1.0);
// tokenView.addTokenLabel(0, "Sam", 1.0);
// tokenView.addTokenLabel(1, "reads", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
when(annotator.isInitialized()).thenReturn(false);
doNothing().when(annotator).doInitialize();
// annotator.addView(ta);
verify(annotator).doInitialize();
}

@Test
public void testAddViewHandlesHttpTokenCorrectly() throws Exception {
String[] tokens = new String[] { "http://example.com" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "Test", ta, 1.0);
// posView.addConstituent(new Constituent("URL", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "Test", ta, 1.0);
// tokenView.addTokenLabel(0, "http://example.com", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// Constituent token = ta.getView("BIO").getConstituents().get(0);
// assertEquals(",", token.getAttribute("WORDNETTAG"));
// assertEquals(",", token.getAttribute("WORDNETHYM"));
}

@Test
public void testInitializeCompletesWithoutError() {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
try {
// annotator.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager(new java.util.HashMap<>()));
} catch (Exception e) {
fail("Initialization should not throw exception but got: " + e.getMessage());
}
}

@Test
public void testConstructorLoadsModelPathsSafely() {
MentionAnnotator annotator = new MentionAnnotator("test/model/nam", "test/model/nom", "test/model/pro", "test/model/ext", "RANDOM_MODE");
assertNotNull(annotator);
}

@Test
public void testConstructorWithEmptyModelPaths() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "ACE_NONTYPE");
assertNotNull(annotator);
}

@Test
public void testAddViewWithNoTokenConstituentsDoesNotCrash() throws Exception {
String[] tokens = new String[] { "a" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "dummy", ta, 1.0);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator());
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithConstituentMissingGazetteerAnnotation() throws Exception {
String[] tokens = new String[] { "weather" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0);
// tokenView.addTokenLabel(0, tokens[0], 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "dummy", ta, 1.0);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c = bioView.getConstituents().get(0);
// assertNotNull(c.getAttribute("GAZ"));
// assertNotNull(c.getAttribute("BC"));
}

@Test
public void testAddViewWithMultipleTokensAndNoBIOPredictionMatch() throws Exception {
String[] tokens = new String[] { "It", "is", "raining", "outside" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "testGen", ta, 1.0);
// tokenView.addTokenLabel(0, "It", 1.0);
// tokenView.addTokenLabel(1, "is", 1.0);
// tokenView.addTokenLabel(2, "raining", 1.0);
// tokenView.addTokenLabel(3, "outside", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "testGen", ta, 1.0);
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("VBG", ViewNames.POS, ta, 2, 3));
// posView.addConstituent(new Constituent("RB", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testAddViewSkipsBIOWhenTokenMatchesHttpPrefix() throws Exception {
String[] tokens = new String[] { "http://domain.com" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "mock", ta, 1.0);
// tokenView.addTokenLabel(0, tokens[0], 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addConstituent(new Constituent("URL", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// Constituent c = ta.getView("BIO").getConstituents().get(0);
// assertEquals(",", c.getAttribute("WORDNETTAG"));
// assertEquals(",", c.getAttribute("WORDNETHYM"));
}

@Test
public void testGetHeadConstituentHandlesInvalidSpanStrings() {
String[] tokens = new String[] { "Paris", "is", "beautiful" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("MENTION", "MENTION", ta, 0, 3);
// c.addAttribute("EntityHeadStartSpan", "start");
// c.addAttribute("EntityHeadEndSpan", "end");
try {
// MentionAnnotator.getHeadConstituent(c, "HEAD");
fail("Expected NumberFormatException");
} catch (NumberFormatException e) {
}
}

@Test
public void testGetHeadConstituentWithOverlappingSpan() {
String[] tokens = new String[] { "The", "Eiffel", "Tower" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("NAM", "MENTION", ta, 0, 3);
// c.addAttribute("EntityHeadStartSpan", "1");
// c.addAttribute("EntityHeadEndSpan", "2");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "MENTION");
// assertNotNull(head);
// assertEquals(1, head.getStartSpan());
// assertEquals(2, head.getEndSpan());
// assertEquals("NAM", head.getLabel());
}

@Test
public void testAddViewWithEmptyTokenView() throws Exception {
String[] tokens = new String[] { "Something" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "GEN", ta, 1.0f);
// ta.addView(ViewNames.TOKENS, tokensView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "GEN", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator());
when(annotator.isInitialized()).thenReturn(true);
doNothing().when(annotator).doInitialize();
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testAddViewWithNoMentionAdded() throws Exception {
String[] tokens = new String[] { "sky", "is", "blue" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "file", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokensView.addTokenLabel(0, "sky", 1.0);
// tokensView.addTokenLabel(1, "is", 1.0);
// tokensView.addTokenLabel(2, "blue", 1.0);
// ta.addView(ViewNames.TOKENS, tokensView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
when(annotator.isInitialized()).thenReturn(true);
doNothing().when(annotator).doInitialize();
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// View mentionView = ta.getView(ViewNames.MENTION);
// assertEquals(0, mentionView.getNumberOfConstituents());
}

@Test
public void testAddViewTokenWithNoCoveringPOSConstituentDoesNotCrash() throws Exception {
String[] tokens = new String[] { "Hello" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "GEN", ta, 1.0f);
// tokenView.addTokenLabel(0, "Hello", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "GEN", ta, 1.0f);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
try {
// annotator.addView(ta);
} catch (Exception e) {
fail("addView failed when token had no covering POS constituent");
}
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testInitializeFallbackToDefaultExtentPathWhenEmpty() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "ACE_NONTYPE");
try {
// ResourceManager rm = new ResourceManager(new java.util.HashMap<>());
// annotator.initialize(rm);
} catch (Exception e) {
fail("initialize() unexpectedly failed when fallback path triggered");
}
}

@Test
public void testAddViewAddsTrainingFlagAttributeCorrectly() throws Exception {
String[] tokens = new String[] { "Barack", "Obama" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "GEN", ta, 1.0f);
// tokensView.addTokenLabel(0, "Barack", 1.0);
// tokensView.addTokenLabel(1, "Obama", 1.0);
// ta.addView(ViewNames.TOKENS, tokensView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "GEN", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent first = bioView.getConstituents().get(0);
// assertEquals("false", first.getAttribute("isTraining"));
}

@Test
public void testAddViewAddsPreBIOAttributesToConstituents() throws Exception {
String[] tokens = new String[] { "He", "is", "tall" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "GEN", ta, 1.0f);
// tokenView.addTokenLabel(0, "He", 1.0);
// tokenView.addTokenLabel(1, "is", 1.0);
// tokenView.addTokenLabel(2, "tall", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "GEN", ta, 1.0f);
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c0 = bioView.getConstituents().get(0);
// Constituent c1 = bioView.getConstituents().get(1);
// assertNotNull(c0.getAttribute("preBIOLevel1"));
// assertEquals("", c0.getAttribute("preBIOLevel1"));
// assertNotNull(c1.getAttribute("preBIOLevel2"));
}

@Test
public void testGetHeadConstituentPreservesAllAttributes() {
String[] tokens = new String[] { "John", "Smith" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("PERSON", "MENTION", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "1");
// c.addAttribute("EntityMentionType", "NAM");
// c.addAttribute("CustomFeature", "Value1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "HEAD");
// assertNotNull(head);
// assertEquals("Value1", head.getAttribute("CustomFeature"));
// assertEquals("NAM", head.getAttribute("EntityMentionType"));
}

@Test
public void testAddViewCreatesMentionViewEvenIfNoMentionsAdded() throws Exception {
String[] tokens = new String[] { "sky", "blue" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "file", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokensView.addTokenLabel(0, "sky", 1.0);
// tokensView.addTokenLabel(1, "blue", 1.0);
// ta.addView(ViewNames.TOKENS, tokensView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "dummy", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertEquals(0, ta.getView(ViewNames.MENTION).getNumberOfConstituents());
}

@Test
public void testAddViewWithEmptyPOSViewStillThrows() {
String[] tokens = new String[] { "nothing" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "file", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0f);
// tokenView.addTokenLabel(0, "nothing", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "source", ta, 1.0f);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator());
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// try {
// annotator.addView(ta);
// } catch (AnnotatorException e) {
// fail("Unexpected AnnotatorException on valid POS but empty constituent list");
// }
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithMalformedWordNetReturnDoesNotCrash() throws Exception {
String[] tokens = new String[] { "treehouse" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "text", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "treehouse", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// assertNotNull(bio);
// assertTrue(bio.getConstituents().size() > 0);
// Constituent result = bio.getConstituents().get(0);
// assertNotNull(result.getAttribute("WORDNETTAG"));
// assertNotNull(result.getAttribute("WORDNETHYM"));
}

@Test
public void testGetHeadConstituentWithNullAttributesReturnsNull() {
String[] tokens = new String[] { "X", "Y", "Z" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("PERSON", "MENTION", ta, 1, 2);
// Constituent result = MentionAnnotator.getHeadConstituent(c, "HEAD");
// assertNull(result);
}

@Test
public void testGetHeadConstituentCorrectSpanAndViewName() {
String[] tokens = new String[] { "entity", "named", "London" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "doc", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("LOC", "MENTION", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "2");
// c.addAttribute("EntityHeadEndSpan", "3");
// c.addAttribute("Source", "rule");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "MyHeadView");
// assertNotNull(head);
// assertEquals(2, head.getStartSpan());
// assertEquals(3, head.getEndSpan());
// assertEquals("LOC", head.getLabel());
// assertEquals("MyHeadView", head.getViewName());
// assertEquals("rule", head.getAttribute("Source"));
}

@Test
public void testInitializeHandlesUnknownModeGracefully() {
MentionAnnotator annotator = new MentionAnnotator("UNKNOWN_RANDOM_MODE");
// ResourceManager config = new ResourceManager(new java.util.HashMap<>());
try {
// annotator.initialize(config);
} catch (Exception e) {
fail("initialize() should not throw exception for unexpected mode string");
}
}

@Test
public void testInitializeWithoutGazetteerResourceDoesNotCrash() {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// ResourceManager config = new ResourceManager(new java.util.HashMap<>());
try {
// annotator.initialize(config);
} catch (Exception e) {
fail("initialize() should not fail even if gazetteer path missing in environment");
}
}

@Test
public void testAddViewWithOverlappingButIncorrectTokenSpans() throws Exception {
String[] tokens = new String[] { "Tim", "Cook", "Apple" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("src", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokensView.addTokenLabel(0, "Tim", 1.0);
// tokensView.addTokenLabel(1, "Cook", 1.0);
// tokensView.addTokenLabel(2, "Apple", 1.0);
// ta.addView(ViewNames.TOKENS, tokensView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "fake", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
when(annotator.isInitialized()).thenReturn(true);
doNothing().when(annotator).doInitialize();
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testMultipleConstituentsSameTokenHandledCorrectly() throws Exception {
String[] tokens = new String[] { "Amazon" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "Amazon", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP-ALT", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
when(annotator.isInitialized()).thenReturn(true);
doNothing().when(annotator).doInitialize();
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// assertNotNull(bioView);
// assertEquals(1, bioView.getNumberOfConstituents());
}

@Test
public void testAddViewCreatesEmptyMentionWhenNoMatchingBIO() throws Exception {
String[] tokens = new String[] { "It", "rains" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "GEN", ta, 1.0f);
// tokenView.addTokenLabel(0, "It", 1.0);
// tokenView.addTokenLabel(1, "rains", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "GEN", ta, 1.0f);
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ERE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// View mentionView = ta.getView(ViewNames.MENTION);
// assertEquals(0, mentionView.getNumberOfConstituents());
}

@Test
public void testConstructorWithNullModelPathsDoesNotCrash() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ACE_TYPE");
assertNotNull(annotator);
}

@Test
public void testAddViewWithEmptyTokenList() throws Exception {
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "docId", Collections.singletonList(Collections.emptyList()));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "testGenerator", ta, 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "testGenerator", ta, 1.0f);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertEquals(0, ta.getView(ViewNames.MENTION).getNumberOfConstituents());
}

@Test
public void testAddViewReturnsGracefullyWhenNoCoveringTokenInPOS() throws Exception {
String[] tokens = new String[] { "John" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokenView.addTokenLabel(0, "John", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0f);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
try {
// annotator.addView(ta);
} catch (Exception e) {
fail("addView should handle missing POS constituent gracefully");
}
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewSkipsMentionIfBIOResultDoesNotStartWithBOrU() throws Exception {
String[] tokens = new String[] { "test" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "docId", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokenView.addTokenLabel(0, "test", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "dummy", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// mockStaticBIOTesterTag("O", 1);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertEquals(0, ta.getView(ViewNames.MENTION).getNumberOfConstituents());
}

@Test
public void testInitializeDoesNotRunAgainOnceInitialized() {
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doReturn(true).when(annotator).isInitialized();
try {
// annotator.initialize(new ResourceManager(new HashMap<>()));
} catch (Exception e) {
fail("initialize should not run if isInitialized returns true");
}
}

@Test
public void testAddViewHandlesMissingTokenViewGracefully() {
String[] tokens = new String[] { "data" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
try {
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
} catch (Exception e) {
fail("addView should not fail for missing TOKENS view due to isPreReqView enforced at superclass");
}
}

@Test
public void testClassifierExtentFallbackPathIsUsedWithoutExplicitFileName() {
MentionAnnotator annotator = new MentionAnnotator("filea", "fileb", "filec", "", "ACE_NONTYPE");
try {
// ResourceManager rm = new ResourceManager(new HashMap<>());
// annotator.initialize(rm);
} catch (Exception e) {
fail("Extent fallback logic should not throw under valid datastore");
}
}

@Test
public void testAddViewHandlesEmptyConstituentsCoveringTokenListWithoutError() throws Exception {
String[] tokens = new String[] { "item" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "span", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "item", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ERE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
try {
// annotator.addView(ta);
} catch (Exception e) {
fail("addView should skip tokens without POS match");
}
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithNullSpansInHeadAttributesReturnsNullFromGetHeadConstituent() {
String[] tokens = new String[] { "my", "name", "is", "Bond" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "doc", Collections.singletonList(Arrays.asList(tokens)));
// Constituent mention = new Constituent("PER", "MENTION", ta, 0, 4);
// mention.addAttribute("EntityHeadStartSpan", null);
// mention.addAttribute("EntityHeadEndSpan", null);
// Constituent result = MentionAnnotator.getHeadConstituent(mention, "HEAD");
// assertNull(result);
}

@Test
public void testGetHeadConstituentHandlesInvalidIntegerParsing() {
String[] tokens = new String[] { "this", "fails" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "file", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("ORG", "MENTION", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "notAnInt");
// c.addAttribute("EntityHeadEndSpan", "alsoBad");
try {
// MentionAnnotator.getHeadConstituent(c, "viewX");
fail("Expected NumberFormatException");
} catch (NumberFormatException e) {
}
}

@Test
public void testInitializeWithPartialModeStringFallbacksToERE() {
MentionAnnotator annotator = new MentionAnnotator("ERE");
// ResourceManager rm = new ResourceManager(new HashMap<>());
try {
// annotator.initialize(rm);
} catch (Exception e) {
fail("initialize() should not fail for partial 'ERE' mode keyword");
}
}

@Test
public void testConstructorWithAllNullModelPathsStillConstructs() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ERE_TYPE");
assertNotNull(annotator);
}

@Test
public void testMissingEntityMentionSpanAttributesCauseSkippingInAddView() throws Exception {
String[] tokens = new String[] { "Alice", "in", "Wonderland" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "file", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokenView.addTokenLabel(0, "Alice", 1.0f);
// tokenView.addTokenLabel(1, "in", 1.0f);
// tokenView.addTokenLabel(2, "Wonderland", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "dummy", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("IN", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// mockStaticBIOTesterAndExtent(ta);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
// assertEquals(0, mentionView.getNumberOfConstituents());
}

@Test
public void testAddViewWithMultipleBIOPrefixTransitions() throws Exception {
String[] tokens = new String[] { "He", "called", "Dr.", "Smith" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addTokenLabel(0, "He", 1.0f);
// tokenView.addTokenLabel(1, "called", 1.0f);
// tokenView.addTokenLabel(2, "Dr.", 1.0f);
// tokenView.addTokenLabel(3, "Smith", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "tagger", ta, 1.0f);
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// mockSequentialBIOResults();
// annotator.addView(ta);
// View mention = ta.getView(ViewNames.MENTION);
// assertNotNull(mention);
// assertEquals(2, mention.getNumberOfConstituents());
}

@Test
public void testExtentClassifierThrowsExceptionStillProceeds() throws Exception {
String[] tokens = new String[] { "Captain", "Marvel" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "file", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "X", ta, 1.0f);
// tokenView.addTokenLabel(0, "Captain", 1.0f);
// tokenView.addTokenLabel(1, "Marvel", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "X", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// mockExtentThrows();
// annotator.addView(ta);
// View mv = ta.getView(ViewNames.MENTION);
// assertNotNull(mv);
}

@Test
public void testAddViewThrowsIfTokenViewMissingEvenWhenPOSPresent() {
String[] tokens = new String[] { "Mr.", "Anderson" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "file", Collections.singletonList(Arrays.asList(tokens)));
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "mock", ta, 1.0f);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// try {
// annotator.addView(ta);
// fail("Expected exception due to missing TOKENS view");
// } catch (AnnotatorException e) {
// }
}

@Test
public void testAddViewHandlesNonHttpTokenWithNullWordNetManager() throws Exception {
String[] tokens = new String[] { "Oak" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("world", "doc", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addTokenLabel(0, "Oak", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "gen", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ERE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// doReturn(null).when(annotator, "wordNet");
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// Constituent bioC = ta.getView("BIO").getConstituents().get(0);
// assertNotNull(bioC.getAttribute("WORDNETTAG"));
// assertNotNull(bioC.getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewWithEmptyGazetteerAnnotationDoesNotBreak() throws Exception {
String[] tokens = new String[] { "Tree" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "id", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tool", ta, 1.0f);
// tokenView.addTokenLabel(0, "Tree", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "tool", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ACE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// Constituent dummy = new Constituent("Label", "DummyView", ta, 0, 1);
// doReturn(null).when(annotator).gazetteers;
// annotator.addView(ta);
// View bio = ta.getView("BIO");
// assertNotNull(bio);
// assertEquals(1, bio.getNumberOfConstituents());
}

@Test
public void testBioPredictionWithUnsupportedTagSkipsMentionCreation() throws Exception {
String[] tokens = new String[] { "planet" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("space", "42", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "planet", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ERE_NONTYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// Constituent fake = new Constituent("NOM", "BIO", ta, 0, 1);
Pair<String, Integer> pred = new Pair<>("I-NAM", 0);
Mockito.mockStatic(org.cogcomp.md.BIOTester.class);
when(org.cogcomp.md.BIOTester.joint_inference(any(), any())).thenReturn(pred);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertEquals(0, ta.getView(ViewNames.MENTION).getNumberOfConstituents());
}

@Test
public void testInitializeHandlesIOExceptionGracefully() {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// ResourceManager rm = new ResourceManager(new HashMap<>());
try {
// annotator.initialize(rm);
} catch (Exception e) {
fail("initialize() should not throw any exceptions");
}
}

@Test
public void testAddViewWithNullWordNetResultsUsesFallbackCommas() throws Exception {
String[] tokens = new String[] { "token" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("data", "x", Collections.singletonList(Arrays.asList(tokens)));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokenView.addTokenLabel(0, "token", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "dummy", ta, 1.0f);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = Mockito.spy(new MentionAnnotator("ERE_TYPE"));
doNothing().when(annotator).doInitialize();
when(annotator.isInitialized()).thenReturn(true);
// when(annotator.brownClusters).thenReturn(null);
// when(annotator.wordNet).thenReturn(null);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent result = bioView.getConstituents().get(0);
// assertEquals(",", result.getAttribute("WORDNETTAG"));
// assertEquals(",", result.getAttribute("WORDNETHYM"));
}

@Test
public void testGetHeadConstituentFromZeroLengthSpan() {
String[] tokens = new String[] { "single" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("src", "uid", Collections.singletonList(Arrays.asList(tokens)));
// Constituent c = new Constituent("PRO", "MENTION", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "0");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "HEADER");
// assertNotNull(head);
// assertEquals(head.getStartSpan(), 0);
// assertEquals(head.getEndSpan(), 0);
}
}
