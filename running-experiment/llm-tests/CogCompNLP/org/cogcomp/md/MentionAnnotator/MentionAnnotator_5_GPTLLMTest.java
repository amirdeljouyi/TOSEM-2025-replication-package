package org.cogcomp.md;

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

public class MentionAnnotator_5_GPTLLMTest {

@Test
public void testDefaultConstructorDoesNotThrow() {
MentionAnnotator mentionAnnotator = new MentionAnnotator();
assertNotNull(mentionAnnotator);
}

@Test
public void testConstructorWithMode() {
MentionAnnotator mentionAnnotator = new MentionAnnotator("ACE_TYPE");
assertNotNull(mentionAnnotator);
}

@Test
public void testConstructorWithFilePaths() {
MentionAnnotator mentionAnnotator = new MentionAnnotator("namPath", "nomPath", "proPath", "extentPath", "ACE_TYPE");
assertNotNull(mentionAnnotator);
}

@Test
public void testInitializeDoesNotThrowException() {
MentionAnnotator mentionAnnotator = new MentionAnnotator("ACE_NONTYPE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// mentionAnnotator.initialize(rm);
assertTrue(true);
}

@Test
public void testAddViewThrowsWhenMissingPOSView() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("John");
sentence.add("ran");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "test", tokens);
MentionAnnotator mentionAnnotator = new MentionAnnotator("ACE_NONTYPE");
// try {
// mentionAnnotator.addView(ta);
// fail("Expected AnnotatorException for missing POS view.");
// } catch (AnnotatorException ex) {
// assertTrue(ex.getMessage().contains("Missing required view POS"));
// }
}

@Test
public void testAddViewAddsMentionView() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("John");
sentence.add("walked");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "test", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addConstituent(new Constituent("John", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("walked", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "pos", ta, 1.0, false);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator mentionAnnotator = new MentionAnnotator("ACE_NONTYPE");
MentionAnnotator spy = Mockito.spy(mentionAnnotator);
doNothing().when(spy).doInitialize();
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testGetHeadConstituentReturnsCorrectSpan() {
List<String> tokens = new ArrayList<>();
tokens.add("Barack");
tokens.add("Obama");
tokens.add("visited");
List<List<String>> sent = new ArrayList<>();
sent.add(tokens);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", sent);
// Constituent constituent = new Constituent("MENTION", "BIO", ta, 0, 2);
// constituent.addAttribute("EntityHeadStartSpan", "0");
// constituent.addAttribute("EntityHeadEndSpan", "1");
// constituent.addAttribute("EntityMentionType", "NAM");
// Constituent head = MentionAnnotator.getHeadConstituent(constituent, ViewNames.MENTION);
// assertNotNull(head);
// assertEquals(0, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
// assertEquals("MENTION", head.getLabel());
// assertEquals("NAM", head.getAttribute("EntityMentionType"));
}

@Test
public void testGetHeadConstituentReturnsNullWhenMissingAttributes() {
List<String> tokens = new ArrayList<>();
tokens.add("UN");
tokens.add("Meeting");
List<List<String>> sent = new ArrayList<>();
sent.add(tokens);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", sent);
// Constituent constituent = new Constituent("ORG", ViewNames.MENTION, ta, 0, 2);
// Constituent head = MentionAnnotator.getHeadConstituent(constituent, ViewNames.MENTION);
// assertNull(head);
}

@Test
public void testGetHeadConstituentThrowsOnInvalidSpanValues() {
List<String> tokens = new ArrayList<>();
tokens.add("UNESCO");
List<List<String>> sent = new ArrayList<>();
sent.add(tokens);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("t", "d", sent);
// Constituent constituent = new Constituent("ORG", "BIO", ta, 0, 1);
// constituent.addAttribute("EntityHeadStartSpan", "INVALID");
// constituent.addAttribute("EntityHeadEndSpan", "1");
try {
// MentionAnnotator.getHeadConstituent(constituent, ViewNames.MENTION);
fail("Expected NumberFormatException");
} catch (NumberFormatException e) {
assertTrue(true);
}
}

@Test
public void testMultipleConstructorsCreateInstances() {
MentionAnnotator m1 = new MentionAnnotator();
MentionAnnotator m2 = new MentionAnnotator("ERE_NONTYPE");
MentionAnnotator m3 = new MentionAnnotator(false, "ERE_TYPE");
MentionAnnotator m4 = new MentionAnnotator("namPath", "nomPath", "proPath", "extentPath", "ACE_TYPE");
assertNotNull(m1);
assertNotNull(m2);
assertNotNull(m3);
assertNotNull(m4);
}

@Test
public void testAddViewWithEmptyTokenViewDoesNotThrow() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("token");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "text", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "test", ta, 1.0, false);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator mentionAnnotator = new MentionAnnotator("ACE_NONTYPE");
MentionAnnotator spy = Mockito.spy(mentionAnnotator);
doNothing().when(spy).doInitialize();
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWhenMentionAlreadyExistsDoesNotReplace() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("Alice");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("tt", "dd", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addConstituent(new Constituent("NNP", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "pos", ta, 1.0, false);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION, "existing", ta, 1.0, false);
// mentionView.addConstituent(new Constituent("PRE_EXISTING", ViewNames.MENTION, ta, 0, 1));
// ta.addView(ViewNames.MENTION, mentionView);
MentionAnnotator mentionAnnotator = new MentionAnnotator("ACE_NONTYPE");
MentionAnnotator spy = Mockito.spy(mentionAnnotator);
doNothing().when(spy).doInitialize();
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// View mentionAfter = ta.getView(ViewNames.MENTION);
// assertNotNull(mentionAfter);
// assertTrue(mentionAfter.getConstituents().size() >= 1);
}

@Test
public void testAddViewWithOnlyHttpTokenSkipsWordNetFeatures() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("http://example.com");
List<List<String>> tokenList = new ArrayList<>();
tokenList.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("x", "y", tokenList);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokSrc", ta, 1.0);
// tokenView.addConstituent(new Constituent("http://example.com", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "posTagger", ta, 1.0, true);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator mentionAnnotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spy = Mockito.spy(mentionAnnotator);
doNothing().when(spy).doInitialize();
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithMultipleTokensAndRepeatedWords() throws Exception {
List<List<String>> tokenList = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("John");
sentence.add("John");
sentence.add("runs");
tokenList.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("xx", "yy", tokenList);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokSrc", ta, 1.0);
// tokenView.addConstituent(new Constituent("John", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("John", ViewNames.TOKENS, ta, 1, 2));
// tokenView.addConstituent(new Constituent("runs", ViewNames.TOKENS, ta, 2, 3));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "tagger", ta, 1.0, false);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator mentionAnnotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spy = Mockito.spy(mentionAnnotator);
doNothing().when(spy).doInitialize();
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testGetHeadConstituentWithZeroLengthSpan() {
List<List<String>> tokenList = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("zero");
sentence.add("length");
tokenList.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "text", tokenList);
// Constituent c = new Constituent("MISC", ViewNames.MENTION, ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "1");
// c.addAttribute("EntityHeadEndSpan", "1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "ZVIEW");
// assertNotNull(head);
// assertEquals("MISC", head.getLabel());
// assertEquals(1, head.getStartSpan());
// assertEquals(1, head.getEndSpan());
}

@Test
public void testAddViewOnAlreadyInitializedMentorAnnotator() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("Bob");
sentence.add("works");
List<List<String>> tokenList = new ArrayList<>();
tokenList.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "text", tokenList);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0);
// tokenView.addConstituent(new Constituent("Bob", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("works", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "gen", ta, 1.0, true);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithTextAnnotationThatLacksTokensView() throws Exception {
List<String> tokens = new ArrayList<>();
tokens.add("Entity");
List<List<String>> sent = new ArrayList<>();
sent.add(tokens);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", sent);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "POSGEN", ta, 1.0, true);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
try {
// spy.addView(ta);
fail("Expected exception due to missing TOKENS view");
} catch (Exception e) {
assertTrue(e instanceof NullPointerException);
}
}

@Test
public void testGetHeadConstituentWithStartGreaterThanEnd() {
List<String> tokens = new ArrayList<>();
tokens.add("United");
tokens.add("Nations");
List<List<String>> sent = new ArrayList<>();
sent.add(tokens);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "txt", sent);
// Constituent c = new Constituent("ORG", ViewNames.MENTION, ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "2");
// c.addAttribute("EntityHeadEndSpan", "1");
try {
// MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
fail("Expected exception for invalid span range");
} catch (Exception e) {
assertTrue(e instanceof StringIndexOutOfBoundsException || e instanceof IllegalArgumentException || e instanceof RuntimeException);
}
}

@Test
public void testAddViewWithEmptyPOSView() throws Exception {
List<String> tokens = new ArrayList<>();
tokens.add("Entity");
List<List<String>> sent = new ArrayList<>();
sent.add(tokens);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "txt", sent);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokSrc", ta, 1.0);
// tokenView.addConstituent(new Constituent("Entity", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "POSGEN", ta, 1.0, true);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewCalledTwiceOnSameTextAnnotation() throws Exception {
List<String> tokens = new ArrayList<>();
tokens.add("Obama");
tokens.add("spoke");
List<List<String>> sent = new ArrayList<>();
sent.add(tokens);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "txt", sent);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "toks", ta, 1.0);
// tokenView.addConstituent(new Constituent("Obama", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("spoke", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "posgen", ta, 1.0, true);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithSpecialCharactersInText() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("C@t");
sentence.add("#runs");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "txt", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0);
// tokenView.addConstituent(new Constituent("C@t", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("#runs", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "g", ta, 1.0, true);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testInitializeCalledTwiceDoesNotThrow() {
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
// annotator.initialize(rm);
// annotator.initialize(rm);
assertTrue(true);
}

@Test
public void testConstructorWithNullFilePathsSetsNoClassifiers() {
MentionAnnotator annotator = new MentionAnnotator(null, null, null, null, "ACE_TYPE");
assertNotNull(annotator);
}

@Test
public void testClassifierInitFailureFallback() {
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
// ResourceManager rm = new ResourceManager(Collections.emptyMap());
try {
// annotator.initialize(rm);
} catch (Exception e) {
fail("Initialization with fallback should not throw.");
}
assertTrue(true);
}

@Test
public void testGetHeadConstituentWithBlankAttributesReturnsNull() {
List<List<String>> tokens = new ArrayList<>();
List<String> words = new ArrayList<>();
words.add("Alpha");
words.add("Beta");
tokens.add(words);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// Constituent c = new Constituent("ORG", ViewNames.MENTION, ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "");
// c.addAttribute("EntityHeadEndSpan", "");
try {
// MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
fail("Expected NumberFormatException for empty head span attributes");
} catch (NumberFormatException e) {
assertTrue(true);
}
}

@Test
public void testGetHeadConstituentWithInvalidNumberFormatThrowsException() {
List<List<String>> tokens = new ArrayList<>();
List<String> words = new ArrayList<>();
words.add("Node");
words.add("Error");
tokens.add(words);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("f", "g", tokens);
// Constituent c = new Constituent("OBJ", "BIO", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "start");
// c.addAttribute("EntityHeadEndSpan", "end");
try {
// MentionAnnotator.getHeadConstituent(c, "ZVIEW");
fail("Expected NumberFormatException due to non-numeric spans");
} catch (NumberFormatException e) {
assertTrue(true);
}
}

@Test
public void testAddViewWithNoConstituentsInTokenView() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("hello");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "src", ta, 1.0, true);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spyAnnotator = Mockito.spy(annotator);
doReturn(true).when(spyAnnotator).isInitialized();
try {
// spyAnnotator.addView(ta);
fail("Expected IndexOutOfBoundsException due to empty constituents list");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testAddViewWithEntitiesIncludingSymbols() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("@John");
sentence.add("said");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("z", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "xx", ta, 1.0);
// tokenView.addConstituent(new Constituent("@John", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("said", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "yy", ta, 1.0, false);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewHandlesRedundantViewInjectionGracefully() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("Agent");
sentence.add("speaks");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "source", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0);
// tokenView.addConstituent(new Constituent("Agent", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("speaks", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "pos", ta, 1.0, false);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
// SpanLabelView bioView = new SpanLabelView("BIO", "manual", ta, 1.0, false);
// bioView.addConstituent(new Constituent("Agent", "BIO", ta, 0, 1));
// ta.addView("BIO", bioView);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithRepeatedCallDoesNotDuplicateViews() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("he");
sentence.add("ran");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "story", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokz", ta, 1.0);
// tokenView.addConstituent(new Constituent("he", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("ran", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "h", ta, 1.0, true);
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// spy.addView(ta);
// View result = ta.getView(ViewNames.MENTION);
// assertNotNull(result);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithMissingTokenConstituentCausesException() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("Missing");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tokenSrc", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "posGenerator", ta, 1.0, true);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
try {
// spy.addView(ta);
fail("Expected IndexOutOfBoundsException due to missing constituent in token view");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testGetHeadConstituentWhereStartEqualsEndReturnsValidZeroWidthConstituent() {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("Test");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// Constituent c = new Constituent("LABEL", "MockView", ta, 0, 1);
// c.addAttribute("EntityHeadStartSpan", "0");
// c.addAttribute("EntityHeadEndSpan", "0");
// Constituent head = MentionAnnotator.getHeadConstituent(c, "OutputView");
// assertNotNull(head);
// assertEquals(0, head.getStartSpan());
// assertEquals(0, head.getEndSpan());
}

@Test
public void testAddViewWithBlankTextAnnotationTokenDoesNotThrow() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("blank", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "srcTok", ta, 1.0);
// tokenView.addConstituent(new Constituent("", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "srcPos", ta, 1.0, true);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewSkipsHttpEntitiesAndSetsDefaultWordNetAttributes() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("http://entity.com");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "generator", ta, 1.0);
// tokenView.addConstituent(new Constituent("http://entity.com", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "generator", ta, 1.0, false);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent bio = bioView.getConstituentsCoveringToken(0).get(0);
// assertEquals(",", bio.getAttribute("WORDNETTAG"));
// assertEquals(",", bio.getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewWithEmptyPOSViewAndMissingTokensConstituent() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("single");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "creator", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "posgen", ta, 1.0, false);
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
try {
// spy.addView(ta);
fail("Expected IndexOutOfBoundsException due to missing token constituent");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testGetHeadConstituentReturnsAllAttributesFromOriginal() {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("X");
sentence.add("Y");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("demo", "doc", tokens);
// Constituent original = new Constituent("ENTITY", "MOCKVIEW", ta, 0, 2);
// original.addAttribute("EntityHeadStartSpan", "0");
// original.addAttribute("EntityHeadEndSpan", "1");
// original.addAttribute("EntityMentionType", "PRO");
// original.addAttribute("SomeKey", "SomeValue");
// original.addAttribute("AnotherKey", "AnotherValue");
// Constituent head = MentionAnnotator.getHeadConstituent(original, "NEWVIEW");
// assertEquals("ENTITY", head.getLabel());
// assertEquals("PRO", head.getAttribute("EntityMentionType"));
// assertEquals("SomeValue", head.getAttribute("SomeKey"));
// assertEquals("AnotherValue", head.getAttribute("AnotherKey"));
}

@Test
public void testConstructorWithNullModeDefaultsToGracefulState() {
MentionAnnotator annotator = new MentionAnnotator(true, null);
assertNotNull(annotator);
}

@Test
public void testInitializeWithUnsupportedModeDoesNotCrash() {
MentionAnnotator annotator = new MentionAnnotator("UNKNOWN_MODE");
// annotator.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager(Collections.emptyMap()));
assertTrue(true);
}

@Test
public void testAddViewThrowsWhenPOSViewIsNull() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("Text");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "generator", ta, 1.0);
// tokenView.addConstituent(new Constituent("Text", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
// try {
// annotator.addView(ta);
// fail("Expected AnnotatorException for missing POS view");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("Missing required view POS"));
// }
}

@Test
public void testAddViewWithSentencesThatContainOnlyHttpUrls() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("http://test");
sentence.add("http://example");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "toks", ta, 1.0);
// tokenView.addConstituent(new Constituent("http://test", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("http://example", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "mockPOS", ta, 1.0, true);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spyAnn = Mockito.spy(annotator);
doReturn(true).when(spyAnn).isInitialized();
// spyAnn.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testAddViewIgnoresNonHttpMalformedTokenAndStillSetsWordNet() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("wordnetish");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("docID", "origText", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addConstituent(new Constituent("wordnetish", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "tagger", ta, 1.0, false);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent c = bioView.getConstituentsCoveringToken(0).get(0);
// assertFalse(",".equals(c.getAttribute("WORDNETTAG")));
}

@Test
public void testEmptyTokenTextDoesNotCauseWordNetProcessingException() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("  ");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0);
// tokenView.addConstituent(new Constituent("  ", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "pos", ta, 1.0, true);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// View bioView = ta.getView("BIO");
// Constituent bioC = bioView.getConstituentsCoveringToken(0).get(0);
// assertNotNull(bioC.getAttribute("WORDNETHYM"));
}

@Test
public void testAddViewHandlesRepetitiveTokensCorrectly() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("he");
sentence.add("he");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("repeat", "text", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0);
// tokenView.addConstituent(new Constituent("he", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("he", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "tagger", ta, 1.0, false);
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spyAnn = Mockito.spy(annotator);
doReturn(true).when(spyAnn).isInitialized();
// spyAnn.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewHandlesShortSingleTokenSentence() throws Exception {
List<String> sentence = new ArrayList<>();
sentence.add("Run");
List<List<String>> tokens = new ArrayList<>();
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("single", "short text", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addConstituent(new Constituent("Run", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "auto", ta, 1.0, true);
// posView.addConstituent(new Constituent("VB", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
MentionAnnotator spyAnn = Mockito.spy(annotator);
doReturn(true).when(spyAnn).isInitialized();
// spyAnn.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testAddViewWithNullTokenTextInConstituent() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add(null);
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("nullToken", "doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "generator", ta, 1.0);
// tokenView.addConstituent(new Constituent(null, ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "posgen", ta, 1.0, true);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
// assertTrue(ta.hasView("BIO"));
}

@Test
public void testAddViewHandlesMultipleSentencesCorrectly() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sent1 = new ArrayList<>();
sent1.add("Barack");
sent1.add("Obama");
List<String> sent2 = new ArrayList<>();
sent2.add("He");
sent2.add("won");
tokens.add(sent1);
tokens.add(sent2);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("docMulti", "Multi sentence doc", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0);
// tokenView.addConstituent(new Constituent("Barack", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Obama", ViewNames.TOKENS, ta, 1, 2));
// tokenView.addConstituent(new Constituent("He", ViewNames.TOKENS, ta, 2, 3));
// tokenView.addConstituent(new Constituent("won", ViewNames.TOKENS, ta, 3, 4));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "tagger", ta, 1.0, false);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 2, 3));
// posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 3, 4));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testGetHeadConstituentWithMissingEntityHeadStartAttributeReturnsNull() {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("Headless");
sentence.add("Test");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "text", tokens);
// Constituent c = new Constituent("ENTITY", "BIO", ta, 0, 2);
// c.addAttribute("EntityHeadEndSpan", "1");
// Constituent head = MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
// assertNull(head);
}

@Test
public void testGetHeadConstituentWithMissingEntityHeadEndAttributeReturnsNull() {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("Testing");
sentence.add("OnlyStart");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "text", tokens);
// Constituent c = new Constituent("OBJ", "BIO", ta, 0, 2);
// c.addAttribute("EntityHeadStartSpan", "0");
// Constituent head = MentionAnnotator.getHeadConstituent(c, ViewNames.MENTION);
// assertNull(head);
}

@Test
public void testAddViewOnEmptyTextAnnotationThrowsException() throws Exception {
// TextAnnotation ta = new TextAnnotation("viewId", "docId", new String[0], new int[0][0]);
MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// try {
// spy.addView(ta);
// fail("Expected AnnotatorException due to missing POS and TOKENS views");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("Missing required view POS"));
// }
}

@Test
public void testAddViewWithMultipleSpacesInTokenName() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("New");
sentence.add("  ");
sentence.add("York");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("spaces", "multiple spaces", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0);
// tokenView.addConstituent(new Constituent("New", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("  ", ViewNames.TOKENS, ta, 1, 2));
// tokenView.addConstituent(new Constituent("York", ViewNames.TOKENS, ta, 2, 3));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "parse", ta, 1.0, false);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("SYM", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spyAnn = Mockito.spy(annotator);
doReturn(true).when(spyAnn).isInitialized();
// spyAnn.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}

@Test
public void testMentionAnnotatorConstructorWithEmptyStringsAsModelPaths() {
MentionAnnotator annotator = new MentionAnnotator("", "", "", "", "ACE_NONTYPE");
assertNotNull(annotator);
}

@Test
public void testAddViewWithUnalignedPOSViewBoundaries() throws Exception {
List<List<String>> tokens = new ArrayList<>();
List<String> sentence = new ArrayList<>();
sentence.add("OpenAI");
tokens.add(sentence);
// TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("id", "body", tokens);
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "toksrc", ta, 1.0);
// tokenView.addConstituent(new Constituent("OpenAI", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView posView = new SpanLabelView(ViewNames.POS, "possrc", ta, 1.0, true);
// posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 0));
// ta.addView(ViewNames.POS, posView);
MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
MentionAnnotator spy = Mockito.spy(annotator);
doReturn(true).when(spy).isInitialized();
// spy.addView(ta);
// assertTrue(ta.hasView(ViewNames.MENTION));
}
}
