package org.cogcomp.md;

import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
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

public class BIOReader_2_GPTLLMTest {

@Test
public void testConstructorInitializesId() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
List<Constituent> tokens = new ArrayList<>();
Constituent token0 = mock(Constituent.class);
when(token0.toString()).thenReturn("token0");
List<Constituent> tokenList0 = new ArrayList<>();
tokenList0.add(token0);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(tokenList0);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token0));
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
when(mention.getLabel()).thenReturn("ORG");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("path/TEST_FOLDER", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}

// @Override
protected void annotateTas() {
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<>();
}
};
assertEquals("TEST_FOLDER_NAM", reader.id);
}

@Test
public void testResetAndNextBehavior() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("token0");
List<Constituent> tokenList = new ArrayList<>();
tokenList.add(token);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(tokenList);
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
when(mention.getLabel()).thenReturn("ORG");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getText()).thenReturn("token0");
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock/path/sample", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}

protected List<Constituent> getTokensFromTAs() {
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("token0");
List<Constituent> list = new ArrayList<>();
list.add(token);
return list;
}
};
Object first = reader.next();
assertNotNull(first);
Object second = reader.next();
assertNull(second);
reader.reset();
Object afterReset = reader.next();
assertNotNull(afterReset);
}

@Test
public void testNextReturnsNullWhenListIsEmpty() {
BIOReader reader = new BIOReader("mock/path/sample", "ColumnFormat-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}

// @Override
protected void annotateTas() {
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<>();
}
};
Object next = reader.next();
assertNull(next);
}

@Test
public void testCloseDoesNotAffectTokenList() {
BIOReader reader = new BIOReader("mock/path/sample", "ColumnFormat-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}

protected void annotateTas() {
}

protected List<Constituent> getTokensFromTAs() {
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("token0");
List<Constituent> list = new ArrayList<>();
list.add(token);
return list;
}
};
Object beforeClose = reader.next();
assertNotNull(beforeClose);
reader.close();
reader.reset();
Object afterCloseReset = reader.next();
assertNotNull(afterCloseReset);
}

@Test
public void testEntityMentionTypesAreFilteredCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("mentionToken");
List<Constituent> tokenList = new ArrayList<>();
tokenList.add(token);
when(tokenView.getConstituents()).thenReturn(tokenList);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(tokenList);
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
when(mention.getLabel()).thenReturn("ORG");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock/path/test", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object next = reader.next();
assertNotNull(next);
assertTrue(next instanceof Constituent);
}

@Test
public void testTagSchemaBIOLUBehavior() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("unitToken");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("PER");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock/input", "ColumnFormat-EVAL", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
}

@Test
public void testGetTextAnnotationsWithUnsupportedMode() {
BIOReader reader = new BIOReader("mock/data", "UnknownMode-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return super.getTextAnnotations();
}
};
List<TextAnnotation> result = reader.getTextAnnotations();
assertTrue(result.isEmpty());
}

@Test
public void testGetTokensFromTAsSkipsNullHead() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("tok0");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getLabel()).thenReturn("PER");
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testEntityMentionTypeMismatchSkipsMention() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("tok");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getLabel()).thenReturn("PER");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock/folder", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testEntityTypeVEHAndWEAStillIncluded() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("vehToken");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityType")).thenReturn("VEH");
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("VEH");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getText()).thenReturn("vehToken");
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock/folder", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
}

@Test
public void testAllMentionTypesAcceptedWhenTypeIsALL() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("tok");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityType")).thenReturn("ORG");
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getLabel()).thenReturn("ORG");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getText()).thenReturn("tok");
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("test/path", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
}

@Test
public void testTrainingModeFalseEvaluationData() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("testToken");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("PER");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getText()).thenReturn("testToken");
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock", "ColumnFormat-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
Constituent c = (Constituent) result;
String isTraining = c.getAttribute("isTraining");
assertEquals("false", isTraining);
}

@Test
public void testInvalidPathInConstructorStillParsesModeCorrectly() {
BIOReader reader = new BIOReader("invalid////path///folder", "ERE-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}
};
assertTrue(reader.id.contains("folder_NAM"));
}

@Test
public void testUnknownModeInConstructorTriggersConsoleMessage() {
BIOReader reader = new BIOReader("mock/path/xyzdata", "XYZ-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return super.getTextAnnotations();
}
};
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
assertEquals(0, tas.size());
}

@Test
public void testTokenWithHttpSkipsWordNetLookupPath() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("http://example.com");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getLabel()).thenReturn("PER");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getText()).thenReturn("http://example.com");
List<TextAnnotation> taList = new ArrayList<>();
taList.add(ta);
BIOReader reader = new BIOReader("mock/httpdata", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return taList;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
Constituent c = (Constituent) result;
assertEquals(",", c.getAttribute("WORDNETTAG"));
assertEquals(",", c.getAttribute("WORDNETHYM"));
}

@Test
public void testMentionWithSingleTokenCreatesUnitLabelInLU() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Single");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("FAC");
when(mention.getLabel()).thenReturn("FAC");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("mock/facetest", "ColumnFormat-EVAL", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
Constituent c = (Constituent) result;
assertEquals("U-FAC", c.getAttribute("BIO"));
}

@Test
public void testMentionWithMultipleTokensCreatesBIOAndLastTag() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token1 = mock(Constituent.class);
when(token1.toString()).thenReturn("New");
Constituent token2 = mock(Constituent.class);
when(token2.toString()).thenReturn("York");
List<Constituent> tokenList = new ArrayList<>();
tokenList.add(token1);
tokenList.add(token2);
when(tokenView.getConstituents()).thenReturn(tokenList);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token1));
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(Collections.singletonList(token2));
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("GPE");
when(mention.getLabel()).thenReturn("GPE");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(2);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("mock/multitag", "ColumnFormat-EVAL", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object c1 = reader.next();
Object c2 = reader.next();
assertNotNull(c1);
assertNotNull(c2);
assertTrue(c1 instanceof Constituent);
assertTrue(c2 instanceof Constituent);
Constituent cons1 = (Constituent) c1;
Constituent cons2 = (Constituent) c2;
String tag1 = cons1.getAttribute("BIO");
String tag2 = cons2.getAttribute("BIO");
assertEquals("B-GPE", tag1);
assertEquals("L-GPE", tag2);
}

@Test
public void testEmptyTokenViewReturnsNoBIOTags() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(new ArrayList<>());
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(new ArrayList<>());
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("somePath", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionWithNullEntityMentionTypeIsIgnored() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Book");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getAttribute("EntityMentionType")).thenReturn(null);
when(mention.getLabel()).thenReturn("PER");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("fake", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionViewMissingDoesNotCauseException() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Alpha");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenThrow(new IllegalStateException("MENTIONS view not found"));
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("somepath", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testEntityMentionTypeWithSPEPrefixIsHandledCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Word");
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
when(mention.getLabel()).thenReturn("ORG");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("mock", "ColumnFormat-TRAIN", "SPE_NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
}

@Test
public void testTokenToTagsAllOWhenMentionViewIsEmpty() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Hello");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(new ArrayList<>());
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("dummy", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNotNull(result);
Constituent cons = (Constituent) result;
String bio = cons.getAttribute("BIO");
assertEquals("O", bio);
}

@Test
public void testConstructorWithNonStandardPathExtractsCorrectId() {
BIOReader reader = new BIOReader("/data/corpus/ere/", "ERE-TRAIN", "PRO", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}
};
assertEquals("ere_PRO", reader.id);
}

@Test
public void testConstructorHandlesMissingHyphenInMode() {
BIOReader reader = new BIOReader("mock/path/folderA", "ERE", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}
};
assertEquals("folderA_NAM", reader.id);
}

@Test
public void testMentionHeadIsStartEqualsEndSpan() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("ZeroLen");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("PER");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("path/", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionWithUnrecognizedEntityTypeStillAnnotatesToken() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Alien");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("XYZ");
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("XYZ");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("junk", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
Constituent c = (Constituent) result;
String tag = c.getAttribute("BIO");
assertTrue(tag.startsWith("B-"));
}

@Test
public void testExceptionInAnnotateTasDoesNotCrashConstructor() {
BIOReader reader = new BIOReader("mock", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation ta = mock(TextAnnotation.class);
return Collections.singletonList(ta);
}
};
assertNotNull(reader);
Object result = reader.next();
assertNull(result);
}

@Test
public void testEmptyTextAnnotationsListHandledGracefully() {
BIOReader reader = new BIOReader("mock/path/empty", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}
};
Object value = reader.next();
assertNull(value);
}

@Test
public void testMultipleCallsToResetRestartIteration() {
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Again");
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-TRAIN", "NAM", true) {

private int callCount = 0;

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(new ArrayList<>());
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
return Collections.singletonList(ta);
}
};
Object first = reader.next();
reader.reset();
Object second = reader.next();
assertNotNull(first);
assertNotNull(second);
assertTrue(first instanceof Constituent);
assertTrue(second instanceof Constituent);
assertEquals(first.toString(), second.toString());
}

@Test
public void testEntityTypeVEHAndMentionTypeNOMEmitsTag() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("Truck");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityMentionType")).thenReturn("NOM");
when(mention.getAttribute("EntityType")).thenReturn("VEH");
when(mention.getLabel()).thenReturn("VEH");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("data", "ColumnFormat-TRAIN", "NOM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object output = reader.next();
assertNotNull(output);
Constituent c = (Constituent) output;
assertTrue(c.getAttribute("BIO").startsWith("B-VEH"));
}

@Test
public void testConstructorWithEmptyPathProducesEmptyGroupName() {
BIOReader reader = new BIOReader("", "ACE05-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}
};
assertEquals("_NAM", reader.id);
}

@Test
public void testEntityMentionWithUnsupportedTypeSkippedWhenTypeIsNotALL() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("def");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getAttribute("EntityMentionType")).thenReturn("NOM");
when(mention.getLabel()).thenReturn("PER");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("abc", "ColumnFormat-TRAIN", "PRO", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testEntityMentionWithNullEntityTypeHandledGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("ghi");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn(null);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("UNK");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("xyz", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
try {
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
fail("BIOReader should not throw exception on null EntityType.");
}
}

@Test
public void testTagSplitWithMissingCommaHandledGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("lookup");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("LOC");
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("LOC");
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("path", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
}

@Test
public void testModeParsingValidatesFirstTokenOnly() {
BIOReader reader = new BIOReader("some/path", "ERE-EVAL-SPLIT", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}
};
assertEquals("path_NAM", reader.id);
}

@Test
public void testResetAfterNextNullStillWorks() {
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("after");
BIOReader reader = new BIOReader("mock", "ColumnFormat-EVAL", "NAM", true) {

private boolean initialized = false;

@Override
public List<TextAnnotation> getTextAnnotations() {
if (!initialized) {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(new ArrayList<>());
initialized = true;
return Collections.singletonList(ta);
} else {
return new ArrayList<>();
}
}
};
reader.next();
Object afterNull = reader.next();
reader.reset();
Object resetBack = reader.next();
assertNull(afterNull);
assertNotNull(resetBack);
}

@Test
public void testUnknownModeSkipsMentionViewMappingGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokens = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("X");
when(tokens.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokens.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(new ArrayList<>());
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokens);
when(ta.getView(anyString())).thenReturn(mentionView);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("mock/path", "UnknownMode-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object output = reader.next();
assertNull(output);
}

@Test
public void testMentionOfNonMatchingTypeSkippedDueToPrefixedMode() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokens = mock(View.class);
View mentions = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("word");
when(tokens.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokens.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityType")).thenReturn("ORG");
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getLabel()).thenReturn("ORG");
when(mentions.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokens);
when(ta.getView("MENTIONS")).thenReturn(mentions);
List<TextAnnotation> tas = new ArrayList<>();
tas.add(ta);
BIOReader reader = new BIOReader("dir", "ColumnFormat-TRAIN", "SPE_NOM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionViewIsNullThrowsNoException() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokens = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("alone");
when(tokens.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokens.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokens);
when(ta.getView("MENTIONS")).thenReturn(null);
List<TextAnnotation> tas = Collections.singletonList(ta);
BIOReader reader = new BIOReader("test", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionWithZeroLengthSpanSkipped() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokens = mock(View.class);
View mentions = mock(View.class);
Constituent token = mock(Constituent.class);
when(token.toString()).thenReturn("abc");
when(tokens.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokens.getConstituents()).thenReturn(Collections.singletonList(token));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(1);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getLabel()).thenReturn("PER");
when(mentions.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokens);
when(ta.getView("MENTIONS")).thenReturn(mentions);
List<TextAnnotation> tas = Collections.singletonList(ta);
BIOReader reader = new BIOReader("d", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionsWithMultipleSpanTokensInBIO() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokens = mock(View.class);
View mentions = mock(View.class);
Constituent token1 = mock(Constituent.class);
when(token1.toString()).thenReturn("London");
Constituent token2 = mock(Constituent.class);
when(token2.toString()).thenReturn("Bridge");
List<Constituent> tokenList = Arrays.asList(token1, token2);
when(tokens.getConstituents()).thenReturn(tokenList);
when(tokens.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token1));
when(tokens.getConstituentsCoveringToken(1)).thenReturn(Collections.singletonList(token2));
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(2);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("LOC");
when(mention.getLabel()).thenReturn("LOC");
when(mentions.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokens);
when(ta.getView("MENTIONS")).thenReturn(mentions);
List<TextAnnotation> tas = Collections.singletonList(ta);
BIOReader reader = new BIOReader("dir", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return tas;
}
};
Object c0 = reader.next();
Object c1 = reader.next();
assertNotNull(c0);
assertNotNull(c1);
Constituent ct0 = (Constituent) c0;
Constituent ct1 = (Constituent) c1;
String bio0 = ct0.getAttribute("BIO");
String bio1 = ct1.getAttribute("BIO");
assertEquals("B-LOC", bio0);
assertEquals("I-LOC", bio1);
}
}
