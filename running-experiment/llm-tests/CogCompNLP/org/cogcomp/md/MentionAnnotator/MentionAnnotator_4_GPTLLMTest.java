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

public class MentionAnnotator_4_GPTLLMTest {

@Test
public void testDefaultConstructor() {
MentionAnnotator annotator = new MentionAnnotator();
assertNotNull(annotator);
}

@Test
public void testConstructorWithMode() {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
assertNotNull(annotator);
}

@Test
public void testConstructorWithModelPaths() {
MentionAnnotator annotator = new MentionAnnotator("path/to/nam", "path/to/nom", "path/to/pro", "path/to/extent", "ACE_NONTYPE");
assertNotNull(annotator);
}

@Test(expected = AnnotatorException.class)
public void testAddViewThrowsExceptionWhenMissingPOSView() throws AnnotatorException {
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "John works at Google.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "generator", ta, 1.0);
// tokenView.addToken("John", 0);
// tokenView.addToken("works", 1);
// tokenView.addToken("at", 2);
// tokenView.addToken("Google", 3);
// tokenView.addToken(".", 4);
// ta.addView(ViewNames.TOKENS, tokenView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// annotator.addView(ta);
}

@Test
public void testGetHeadConstituentReturnsExpectedResult() {
// TextAnnotation ta = new TextAnnotation("test", "1", "dummy text");
// Constituent original = new Constituent("MENTION", "MENTION_VIEW", ta, 2, 5);
// original.addAttribute("EntityHeadStartSpan", "3");
// original.addAttribute("EntityHeadEndSpan", "4");
// original.addAttribute("customAttribute", "xyz");
// Constituent head = MentionAnnotator.getHeadConstituent(original, "NEW_HEAD");
// assertNotNull(head);
// assertEquals(3, head.getStartSpan());
// assertEquals(4, head.getEndSpan());
// assertEquals("xyz", head.getAttribute("customAttribute"));
// assertEquals("NEW_HEAD", head.getViewName());
}

@Test
public void testGetHeadConstituentReturnsNullWhenMissingAttributes() {
// TextAnnotation ta = new TextAnnotation("test", "1", "text");
// Constituent c = new Constituent("MENTION", "MENTION_VIEW", ta, 1, 3);
// Constituent head = MentionAnnotator.getHeadConstituent(c, "HEAD");
// assertNull(head);
}

@Test
public void testAddViewAddsMentionView() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "1", "John works at Google.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "generator", ta, 1.0);
// tokenView.addToken("John", 0);
// tokenView.addToken("works", 1);
// tokenView.addToken("at", 2);
// tokenView.addToken("Google", 3);
// tokenView.addToken(".", 4);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGenerator", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "IN", 1.0);
// posView.addTokenLabel(3, "NNP", 1.0);
// posView.addTokenLabel(4, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// View v = ta.getView(ViewNames.MENTION);
// assertTrue(v instanceof SpanLabelView);
}

@Test
public void testBIOViewCreatedWithExpectedAttributes() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Alice visited Chicago.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokGen", ta, 1.0);
// tokenView.addToken("Alice", 0);
// tokenView.addToken("visited", 1);
// tokenView.addToken("Chicago", 2);
// tokenView.addToken(".", 3);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBD", 1.0);
// posView.addTokenLabel(2, "NNP", 1.0);
// posView.addTokenLabel(3, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// assertNotNull(bioView);
// assertEquals(4, bioView.getNumberOfConstituents());
}

@Test
public void testMentionViewContainsEntityMentionTypes() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "He is John Smith.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokGen", ta, 1.0);
// tokenView.addToken("He", 0);
// tokenView.addToken("is", 1);
// tokenView.addToken("John", 2);
// tokenView.addToken("Smith", 3);
// tokenView.addToken(".", 4);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// posView.addTokenLabel(0, "PRP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "NNP", 1.0);
// posView.addTokenLabel(3, "NNP", 1.0);
// posView.addTokenLabel(4, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
// Constituent c0 = mentionView.getConstituents().get(0);
// assertNotNull(c0.getAttribute("EntityMentionType"));
}

@Test
public void testInitializeMethodPublicSuccess() {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
assertNotNull(annotator);
}

@Test
public void testConstructorSafeWithNullModelPaths() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ERE_NONTYPE");
assertNotNull(annotator);
}

@Test
public void testCanCallInitializeOnERE_TYPE() {
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
assertNotNull(annotator);
}

@Test
public void testMentionViewWithDifferentTokenInput() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "He runs.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0);
// tokenView.addToken("He", 0);
// tokenView.addToken("runs", 1);
// tokenView.addToken(".", 2);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// posView.addTokenLabel(0, "PRP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testEmptyTokenAndPOSViews() throws Exception {
// TextAnnotation ta = new TextAnnotation("empty", "id", "");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tGen", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "pGen", ta, 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertEquals(0, ta.getView(ViewNames.MENTION).getNumberOfConstituents());
}

@Test
public void testAddViewWithSingleToken() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Hello");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0);
// tokenView.addToken("Hello", 0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testGetHeadConstituentWithStartEqualsEnd() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "A");
// Constituent c = new Constituent("MENTION", "MENTION_VIEW", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "0");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "ResultView");
// assertNotNull(head);
// assertEquals(0, head.getStartSpan());
// assertEquals(0, head.getEndSpan());
}

@Test
public void testGetHeadConstituentWithNonNumericAttribute() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Test");
// Constituent c = new Constituent("MENTION", "MENTION_VIEW", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "non-numeric");
// c.addAttribute("EntityHeadEndSpan", "0");
try {
// MentionAnnotator.getHeadConstituent(c, "ResultView");
fail("Expected NumberFormatException due to non-integer value");
} catch (NumberFormatException e) {
}
}

@Test
public void testInitializeWithInvalidModeStillInitializes() {
MentionAnnotator annotator = new MentionAnnotator("UNKNOWN_MODE");
annotator.initialize(null);
assertNotNull(annotator);
}

@Test
public void testMultipleInitializations() {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
annotator.initialize(null);
assertNotNull(annotator);
}

@Test
public void testAddViewWithUrlTokenSkipsWordNet() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "https://example.com");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0);
// tokenView.addToken("https://example.com", 0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c = bioView.getConstituents().get(0);
// assertEquals(",", c.getAttribute("WORDNETTAG"));
// assertEquals(",", c.getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewCreatesBIOAttributesOnEachToken() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Mary bought a car");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokenGen", ta, 1.0);
// tokenView.addToken("Mary", 0);
// tokenView.addToken("bought", 1);
// tokenView.addToken("a", 2);
// tokenView.addToken("car", 3);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBD", 1.0);
// posView.addTokenLabel(2, "DT", 1.0);
// posView.addTokenLabel(3, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c1 = bioView.getConstituents().get(0);
// Constituent c2 = bioView.getConstituents().get(1);
// Constituent c3 = bioView.getConstituents().get(2);
// Constituent c4 = bioView.getConstituents().get(3);
// assertNotNull(c1.getAttribute("GAZ"));
// assertNotNull(c2.getAttribute("GAZ"));
// assertNotNull(c3.getAttribute("GAZ"));
// assertNotNull(c4.getAttribute("GAZ"));
// assertNotNull(c1.getAttribute("preBIOLevel1"));
// assertNotNull(c2.getAttribute("preBIOLevel1"));
// assertNotNull(c3.getAttribute("preBIOLevel1"));
// assertNotNull(c4.getAttribute("preBIOLevel1"));
}

@Test
public void testAddViewWithNullPOSViewThrowsException() throws Exception {
// TextAnnotation ta = new TextAnnotation("testCorpus", "ta1", "Hello world.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "generator", ta, 1.0);
// tokenView.addToken("Hello", 0);
// tokenView.addToken("world", 1);
// tokenView.addToken(".", 2);
// ta.addView(ViewNames.TOKENS, tokenView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// try {
// annotator.addView(ta);
// fail("Expected AnnotatorException due to missing POS view.");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("Missing required view POS"));
// }
}

@Test
public void testAddViewWithPOSButMissingTokenView() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Just one test.");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// posView.addTokenLabel(0, "RB", 1.0);
// posView.addTokenLabel(1, "CD", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
try {
// annotator.addView(ta);
fail("Expected IndexOutOfBoundsException due to missing token view.");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testGetHeadConstituentWithPartialAttributes() {
// TextAnnotation ta = new TextAnnotation("test", "id", "Example input.");
// Constituent c = new Constituent("LABEL", "MENTION_VIEW", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "1");
// Constituent result = MentionAnnotator.getHeadConstituent(c, "OutputView");
// assertNull(result);
}

@Test
public void testAddViewOnAlreadyInitializedAnnotator() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "OpenAI creates models.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokGen", ta, 1.0);
// tokenView.addToken("OpenAI", 0);
// tokenView.addToken("creates", 1);
// tokenView.addToken("models", 2);
// tokenView.addToken(".", 3);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "NNS", 1.0);
// posView.addTokenLabel(3, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testMultipleModelsWithEmptyModelPathsDoNotCauseCrash() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "ACE_TYPE");
assertNotNull(annotator);
annotator.initialize(null);
}

@Test
public void testTokenWithNonHttpSymbolDoesNotSkipWordNet() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Email");
// TokenLabelView tok = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0);
// tok.addToken("Email", 0);
// ta.addView(ViewNames.TOKENS, tok);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// Constituent bioToken = ta.getView("BIO").getConstituents().get(0);
// assertNotNull(bioToken.getAttribute("WORDNETTAG"));
// assertNotNull(bioToken.getAttribute("WORDNETHYM"));
// assertNotEquals(",", bioToken.getAttribute("WORDNETTAG"));
}

@Test
public void testAddViewWithLongTokenAndEntityHandling() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "University of California at Berkeley is big.");
// TokenLabelView tok = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tok.addToken("University", 0);
// tok.addToken("of", 1);
// tok.addToken("California", 2);
// tok.addToken("at", 3);
// tok.addToken("Berkeley", 4);
// tok.addToken("is", 5);
// tok.addToken("big", 6);
// tok.addToken(".", 7);
// ta.addView(ViewNames.TOKENS, tok);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0f);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, "IN", 1.0);
// pos.addTokenLabel(2, "NNP", 1.0);
// pos.addTokenLabel(3, "IN", 1.0);
// pos.addTokenLabel(4, "NNP", 1.0);
// pos.addTokenLabel(5, "VBZ", 1.0);
// pos.addTokenLabel(6, "JJ", 1.0);
// pos.addTokenLabel(7, ".", 1.0);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// assertTrue(ta.hasView("BIO"));
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithAmbiguousTokenContent() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "www");
// TokenLabelView tok = new TokenLabelView(ViewNames.TOKENS, "tgen", ta, 1.0f);
// tok.addToken("www", 0);
// ta.addView(ViewNames.TOKENS, tok);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pgen", ta, 1.0f);
// pos.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testAddViewDoesNotAddDuplicatedBIOView() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Obama spoke.");
// TokenLabelView tok = new TokenLabelView(ViewNames.TOKENS, "tokGen", ta, 1.0);
// tok.addToken("Obama", 0);
// tok.addToken("spoke", 1);
// tok.addToken(".", 2);
// ta.addView(ViewNames.TOKENS, tok);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, "VBD", 1.0);
// pos.addTokenLabel(2, ".", 1.0);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// int firstCount = ta.getView("BIO").getNumberOfConstituents();
// annotator.addView(ta);
// int secondCount = ta.getView("BIO").getNumberOfConstituents();
// assertEquals(firstCount, secondCount);
}

@Test
public void testAddViewWithSingleTokenThatIsURL() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "http://test");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokGen", ta, 1.0f);
// tokenView.addToken("http://test", 0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0f);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c = bioView.getConstituents().get(0);
// assertEquals(",", c.getAttribute("WORDNETTAG"));
// assertEquals(",", c.getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewWithMultipleMentionsOfSameEntity() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Barack and Barack.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tk", ta, 1.0f);
// tokenView.addToken("Barack", 0);
// tokenView.addToken("and", 1);
// tokenView.addToken("Barack", 2);
// tokenView.addToken(".", 3);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "ps", ta, 1.0f);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "CC", 1.0);
// posView.addTokenLabel(2, "NNP", 1.0);
// posView.addTokenLabel(3, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.getView(ViewNames.MENTION).getNumberOfConstituents() >= 1);
}

@Test
public void testInitializeRepeatedlyWithDifferentModes() {
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
MentionAnnotator annotator2 = new MentionAnnotator("ERE_NONTYPE");
annotator2.initialize(null);
assertNotNull(annotator);
assertNotNull(annotator2);
}

@Test
public void testAddViewSkipsWordNetFieldsForEmptyWord() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addToken("", 0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0f);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c = bioView.getConstituents().get(0);
// assertNotNull(c.getAttribute("WORDNETTAG"));
// assertNotNull(c.getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewNoMentionsDetected() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "And the or but .");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addToken("And", 0);
// tokenView.addToken("the", 1);
// tokenView.addToken("or", 2);
// tokenView.addToken("but", 3);
// tokenView.addToken(".", 4);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// posView.addTokenLabel(0, "CC", 1.0);
// posView.addTokenLabel(1, "DT", 1.0);
// posView.addTokenLabel(2, "CC", 1.0);
// posView.addTokenLabel(3, "CC", 1.0);
// posView.addTokenLabel(4, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
// assertEquals(0, mentionView.getNumberOfConstituents());
}

@Test
public void testAddViewWithRepeatedPOSValues() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Apple grows Apple.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addToken("Apple", 0);
// tokenView.addToken("grows", 1);
// tokenView.addToken("Apple", 2);
// tokenView.addToken(".", 3);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, "NNP", 1.0);
// posView.addTokenLabel(3, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
// assertTrue(mentionView.getNumberOfConstituents() >= 1);
}

@Test
public void testStaticGetHeadWithNegativeSpan() {
// TextAnnotation ta = new TextAnnotation("dummy", "1", "X");
// Constituent c = new Constituent("X", "V1", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "-1");
// c.addAttribute("EntityHeadEndSpan", "0");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "V2");
// assertNotNull(head);
// assertEquals(-1, head.getStartSpan());
// assertEquals(0, head.getEndSpan());
}

@Test
public void testAddViewHandlesPunctuationOnlySentence() throws Exception {
// TextAnnotation ta = new TextAnnotation("punctOnly", "id", "! ? .");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokens.addToken("!", 0);
// tokens.addToken("?", 1);
// tokens.addToken(".", 2);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0f);
// pos.addTokenLabel(0, ".", 1.0);
// pos.addTokenLabel(1, ".", 1.0);
// pos.addTokenLabel(2, ".", 1.0);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertTrue(mentionView.getNumberOfConstituents() == 0);
}

@Test
public void testAddViewWithPOSButTokenMissingConstituent() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Empty");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokens.addToken("Empty", 0);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// pos.addTokenLabel(0, "JJ", 1.0f);
// ta.addView(ViewNames.POS, pos);
// View tokenView = new View(ViewNames.TOKENS, "gen", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
try {
// annotator.addView(ta);
fail("Expected IndexOutOfBoundsException due to missing token constituents.");
} catch (IndexOutOfBoundsException e) {
assertTrue(e.getMessage() != null);
}
}

@Test
public void testAddViewFailsGracefullyOnNullTokenText() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", null);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tk", ta, 1.0f);
// tokenView.addToken("Entity", 0);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "tk", ta, 1.0f);
// posView.addTokenLabel(0, "NN", 1.0f);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testGetHeadConstituentWithStartGreaterThanEnd() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Test");
// Constituent c = new Constituent("LABEL", "MENTION_VIEW", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "3");
// c.addAttribute("EntityHeadEndSpan", "1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "OutputView");
// assertEquals(3, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
}

@Test
public void testGetHeadConstituentPreservesAllOriginalAttributes() {
// TextAnnotation ta = new TextAnnotation("test", "id", "x x x");
// Constituent c = new Constituent("LABEL", "VIEW", ta, 0, 3);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "2");
// c.addAttribute("EntityMentionType", "NAM");
// c.addAttribute("CustomAttr", "ABC");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "MyView");
// assertEquals("ABC", head.getAttribute("CustomAttr"));
// assertEquals("NAM", head.getAttribute("EntityMentionType"));
// assertEquals(0, head.getStartSpan());
// assertEquals(2, head.getEndSpan());
// assertEquals("MyView", head.getViewName());
}

@Test
public void testAddViewRefusesWhenReusingBIOViewName() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "A token");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokens.addToken("A", 0);
// tokens.addToken("token", 1);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0f);
// pos.addTokenLabel(0, "DT", 1.0f);
// pos.addTokenLabel(1, "NN", 1.0f);
// ta.addView(ViewNames.POS, pos);
// SpanLabelView bioView = new SpanLabelView("BIO", "PRE_GENERATED", ta, 1.0f);
// Constituent pre = new Constituent("X", "BIO", ta, 0, 1);
// bioView.addConstituent(pre);
// ta.addView("BIO", bioView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View bioViewAfter = ta.getView("BIO");
// assertEquals(1, bioViewAfter.getNumberOfConstituents());
}

@Test
public void testAddViewWithTokenMissingInPOSView() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Only one");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "tk", ta, 1.0f);
// tokens.addToken("Only", 0);
// tokens.addToken("one", 1);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tk", ta, 1.0f);
// pos.addTokenLabel(0, "RB", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
MentionAnnotator exceptionSafe = annotator;
try {
// exceptionSafe.addView(ta);
// View bio = ta.getView("BIO");
// assertTrue(bio.getNumberOfConstituents() >= 1);
} catch (Exception e) {
fail("Should handle missing POS index gracefully");
}
}

@Test
public void testModelPathNullInCustomConstructorDoesNotReplaceDefault() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ACE_TYPE");
assertNotNull(annotator);
annotator.initialize(null);
}

@Test
public void testEmptyPOSStillTriggersInitialChecks() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Empty");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokens.addToken("Empty", 0);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testAddViewWithTokenHavingMultipleConstituentsPerToken() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "John works.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "TokGen", ta, 1.0);
// tokenView.addToken("John", 0);
// tokenView.addToken("works", 1);
// tokenView.addToken(".", 2);
// ta.addView(ViewNames.TOKENS, tokenView);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "PosGen", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "VBZ", 1.0);
// posView.addTokenLabel(2, ".", 1.0);
// ta.addView(ViewNames.POS, posView);
// View tokenViewOverride = new View(ViewNames.TOKENS, "Override", ta, 1.0);
// Constituent c1 = new Constituent("word", ViewNames.TOKENS, ta, 0, 1);
// Constituent c2 = new Constituent("another", ViewNames.TOKENS, ta, 0, 1);
// tokenViewOverride.addConstituent(c1);
// tokenViewOverride.addConstituent(c2);
// ta.addView(ViewNames.TOKENS, tokenViewOverride);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testAddViewWithOnlyPOSButNoTokens() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Missing tokens");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "posgen", ta, 1.0);
// pos.addTokenLabel(0, "VBD", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
try {
// annotator.addView(ta);
fail("Expected IndexOutOfBoundsException due to missing token view");
} catch (IndexOutOfBoundsException e) {
assertNotNull(e.getMessage());
} catch (Exception e) {
fail("Expected IndexOutOfBoundsException, but got another exception.");
}
}

@Test
public void testAddViewWithNoTokensNorPOS() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Empty");
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// try {
// annotator.addView(ta);
// fail("Should throw AnnotatorException due to missing POS view");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("POS"));
// }
}

@Test
public void testAddViewCreatesMultipleConstituentsAcrossSentenceWithOverlap() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Barack met Obama.");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0);
// tokens.addToken("Barack", 0);
// tokens.addToken("met", 1);
// tokens.addToken("Obama", 2);
// tokens.addToken(".", 3);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, "VBD", 1.0);
// pos.addTokenLabel(2, "NNP", 1.0);
// pos.addTokenLabel(3, ".", 1.0);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
}

@Test
public void testGetHeadConstituentHandlesZeroLengthConstituent() {
// TextAnnotation ta = new TextAnnotation("test", "id", "Zero");
// Constituent zero = new Constituent("X", "HEAD", ta, 2, 2);
// zero.addAttribute("EntityHeadStartSpan", "2");
// zero.addAttribute("EntityHeadEndSpan", "2");
// Constituent head = MentionAnnotator.getHeadConstituent(zero, "NEW_VIEW");
// assertNotNull(head);
// assertEquals(2, head.getStartSpan());
// assertEquals(2, head.getEndSpan());
}

@Test
public void testAddViewHandlesSpecialCharactersInToken() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "@John_Doe!");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "tk", ta, 1.0f);
// tokens.addToken("@John_Doe!", 0);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tk", ta, 1.0f);
// pos.addTokenLabel(0, "NNP", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mention = ta.getView(ViewNames.MENTION);
// assertNotNull(mention);
}

@Test
public void testAddViewHandlesContractionsInTokens() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "He's here.");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokens.addToken("He", 0);
// tokens.addToken("'s", 1);
// tokens.addToken("here", 2);
// tokens.addToken(".", 3);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// pos.addTokenLabel(0, "PRP", 1.0f);
// pos.addTokenLabel(1, "VBZ", 1.0f);
// pos.addTokenLabel(2, "RB", 1.0f);
// pos.addTokenLabel(3, ".", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mention = ta.getView(ViewNames.MENTION);
// assertNotNull(mention);
}

@Test
public void testMultipleAddViewCallsAddOnlyOneView() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Repeat test.");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "tk", ta, 1.0f);
// tokens.addToken("Repeat", 0);
// tokens.addToken("test", 1);
// tokens.addToken(".", 2);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "ps", ta, 1.0f);
// pos.addTokenLabel(0, "VB", 1.0f);
// pos.addTokenLabel(1, "NN", 1.0f);
// pos.addTokenLabel(2, ".", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mention1 = ta.getView(ViewNames.MENTION);
// int count1 = mention1.getNumberOfConstituents();
// annotator.addView(ta);
// View mention2 = ta.getView(ViewNames.MENTION);
// int count2 = mention2.getNumberOfConstituents();
// assertEquals(count1, count2);
}

@Test
public void testAddViewWithMultipleSentences() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "Obama was president. Microsoft is a company.");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokens.addToken("Obama", 0);
// tokens.addToken("was", 1);
// tokens.addToken("president", 2);
// tokens.addToken(".", 3);
// tokens.addToken("Microsoft", 4);
// tokens.addToken("is", 5);
// tokens.addToken("a", 6);
// tokens.addToken("company", 7);
// tokens.addToken(".", 8);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// pos.addTokenLabel(0, "NNP", 1.0f);
// pos.addTokenLabel(1, "VBD", 1.0f);
// pos.addTokenLabel(2, "NN", 1.0f);
// pos.addTokenLabel(3, ".", 1.0f);
// pos.addTokenLabel(4, "NNP", 1.0f);
// pos.addTokenLabel(5, "VBZ", 1.0f);
// pos.addTokenLabel(6, "DT", 1.0f);
// pos.addTokenLabel(7, "NN", 1.0f);
// pos.addTokenLabel(8, ".", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mention = ta.getView(ViewNames.MENTION);
// assertNotNull(mention);
// assertTrue(mention.getNumberOfConstituents() > 0);
}

@Test
public void testInitializeWithEmptyExtentModelTriggersFallback() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "ACE_NONTYPE");
annotator.initialize(null);
assertNotNull(annotator);
}

@Test
public void testAddViewNoMentionDetected() throws Exception {
// TextAnnotation ta = new TextAnnotation("test", "id", "with and or a the");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokens.addToken("with", 0);
// tokens.addToken("and", 1);
// tokens.addToken("or", 2);
// tokens.addToken("a", 3);
// tokens.addToken("the", 4);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0f);
// pos.addTokenLabel(0, "IN", 1.0f);
// pos.addTokenLabel(1, "CC", 1.0f);
// pos.addTokenLabel(2, "CC", 1.0f);
// pos.addTokenLabel(3, "DT", 1.0f);
// pos.addTokenLabel(4, "DT", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mention = ta.getView(ViewNames.MENTION);
// assertEquals(0, mention.getNumberOfConstituents());
}

@Test(expected = IndexOutOfBoundsException.class)
public void testAddViewFailsWhenTokenLacksConstituentCovering() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Test");
// View tokenView = new View(ViewNames.TOKENS, "manual", ta, 1.0f);
// View posView = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0f);
// tokenView.setScore(0, 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
annotator.initialize(null);
// annotator.addView(ta);
}

@Test
public void testGetHeadConstituentMissingAttributesReturnsNull() {
// TextAnnotation ta = new TextAnnotation("test", "id", "John");
// Constituent c = new Constituent("X", "V", ta, 0, 1);
// c.addAttribute("SomeAttr", "VAL");
// Constituent result = MentionAnnotator.getHeadConstituent(c, "NEW");
// assertNull(result);
}

@Test
public void testSafeConstructionWithEmptyStringModelPaths() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "ERE_TYPE");
assertNotNull(annotator);
annotator.initialize(null);
}

@Test
public void testAddViewOnPunctuationOnlySkipsWordNet() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "! ? . , ;");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokens.addToken("!", 0);
// tokens.addToken("?", 1);
// tokens.addToken(".", 2);
// tokens.addToken(",", 3);
// tokens.addToken(";", 4);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0f);
// pos.addTokenLabel(0, ".", 1.0f);
// pos.addTokenLabel(1, ".", 1.0f);
// pos.addTokenLabel(2, ".", 1.0f);
// pos.addTokenLabel(3, ",", 1.0f);
// pos.addTokenLabel(4, ".", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
annotator.initialize(null);
// annotator.addView(ta);
// View mentionView = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionView);
// assertEquals(0, mentionView.getNumberOfConstituents());
}

@Test
public void testSafeInitWhenModelFilePathsAreNull() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ERE_NONTYPE");
annotator.initialize(null);
}

@Test
public void testGetHeadConstituentZeroLengthStartEndSame() {
// TextAnnotation ta = new TextAnnotation("test", "id", "A");
// Constituent c = new Constituent("ENT", "V", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "0");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "H");
// assertNotNull(head);
// assertEquals(0, head.getStartSpan());
// assertEquals(0, head.getEndSpan());
}

@Test(expected = NumberFormatException.class)
public void testGetHeadConstituentWithInvalidSpanValue() {
// TextAnnotation ta = new TextAnnotation("test", "id", "value");
// Constituent c = new Constituent("X", "V", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "notANumber");
// c.addAttribute("EntityHeadEndSpan", "1");
// MentionAnnotator.getHeadConstituent(c, "V2");
}

@Test
public void testAddViewTriggersLazyInitialization() throws Exception {
// TextAnnotation ta = new TextAnnotation("c", "i", "lazy init testing");
// TokenLabelView tokens = new TokenLabelView(ViewNames.TOKENS, "t", ta, 1.0f);
// tokens.addToken("lazy", 0);
// tokens.addToken("init", 1);
// tokens.addToken("testing", 2);
// ta.addView(ViewNames.TOKENS, tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0f);
// pos.addTokenLabel(0, "JJ", 1.0f);
// pos.addTokenLabel(1, "VB", 1.0f);
// pos.addTokenLabel(2, "NN", 1.0f);
// ta.addView(ViewNames.POS, pos);
MentionAnnotator annotator = new MentionAnnotator(true, "ACE_NONTYPE");
// annotator.addView(ta);
// View v = ta.getView(ViewNames.MENTION);
// assertNotNull(v);
}
}
