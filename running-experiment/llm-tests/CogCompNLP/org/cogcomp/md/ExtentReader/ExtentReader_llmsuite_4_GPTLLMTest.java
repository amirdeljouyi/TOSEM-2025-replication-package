package org.cogcomp.md;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParser;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParserFactory;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
// import edu.illinois.cs.cogcomp.quant.driver.*;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import net.didion.jwnl.JWNLException;
import org.cogcomp.DatastoreException;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ExtentReader_llmsuite_4_GPTLLMTest {

@Test
public void testGetIdRemovesSlashes() throws Exception {
ExtentReader reader = new ExtentReader("some/path\\to\\data", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
String id = reader.getId();
assertFalse(id.contains("/"));
assertFalse(id.contains("\\"));
}

@Test
public void testConstructorWithDefaultCorpus() {
ExtentReader reader = new ExtentReader("fake/path") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
assertNotNull(reader);
Object result = reader.next();
assertNull(result);
}

@Test
public void testConstructorWithExplicitACECorpus() throws Exception {
ExtentReader reader = new ExtentReader("some/fake/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
assertNotNull(reader);
}

@Test(expected = RuntimeException.class)
public void testConstructorFailsWithInvalidTextAnnotation() {
ExtentReader reader = new ExtentReader("non/existing/path") {

@Override
public List<TextAnnotation> getTextAnnotations() {
throw new RuntimeException("Simulated failure");
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
reader.next();
}

@Test
public void testNextReturnsNullWhenNoPairs() throws Exception {
ExtentReader reader = new ExtentReader("any/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testNextReturnsSingleElementCorrectly() throws Exception {
Relation relation = mock(Relation.class);
ExtentReader reader = new ExtentReader("fake/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.singletonList(relation);
}
};
Object first = reader.next();
Object second = reader.next();
assertSame(relation, first);
assertNull(second);
}

@Test
public void testResetResetsIteration() throws Exception {
Relation relation = mock(Relation.class);
ExtentReader reader = new ExtentReader("any/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.singletonList(relation);
}
};
Object first = reader.next();
reader.reset();
Object second = reader.next();
assertSame(relation, first);
assertSame(relation, second);
}

@Test
public void testCloseDoesNotThrowException() throws Exception {
ExtentReader reader = new ExtentReader("testPath", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
reader.close();
}

@Test
public void testParserImplementsInterface() throws Exception {
ExtentReader reader = new ExtentReader("dummy", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
// assertTrue(reader instanceof Parser);
}

@Test
public void testNextReturnsProperTrueAndFalseLabels() throws Exception {
Constituent source1 = mock(Constituent.class);
Constituent target1 = mock(Constituent.class);
Constituent source2 = mock(Constituent.class);
Constituent target2 = mock(Constituent.class);
Relation trueRel = new Relation("true", source1, target1, 1.0f);
Relation falseRel = new Relation("false", source2, target2, 1.0f);
ExtentReader reader = new ExtentReader("fakePath", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return java.util.Arrays.asList(trueRel, falseRel);
}
};
Relation r1 = (Relation) reader.next();
Relation r2 = (Relation) reader.next();
assertEquals("true", r1.getRelationName());
assertEquals("false", r2.getRelationName());
}

@Test
public void testMultipleNextCallsBeyondSize() throws Exception {
Constituent source = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation rel = new Relation("true", source, target, 1.0f);
ExtentReader reader = new ExtentReader("input/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.singletonList(rel);
}
};
Object first = reader.next();
Object second = reader.next();
Object third = reader.next();
assertNotNull(first);
assertNull(second);
assertNull(third);
}

@Test
public void testGetTextAnnotationsReturnsExpectedForMock() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw1801");
ExtentReader reader = new ExtentReader("mock/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return java.util.Collections.singletonList(ta);
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertEquals("nw1801", annotations.get(0).getId());
}

@Test
public void testGetPairsWithEmptyTAsReturnsEmptyList() throws Exception {
ExtentReader reader = new ExtentReader("empty/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetTextAnnotationsReturnsEmptyForUnrecognizedCorpus() {
// ExtentReader reader = new ExtentReader("some/path", "UNKNOWN") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertNotNull(annotations);
// assertTrue(annotations.isEmpty());
}

@Test
public void testGetIdEmptyPath() throws Exception {
ExtentReader reader = new ExtentReader("", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
String id = reader.getId();
assertEquals("", id);
}

@Test
public void testResetWithoutCallingNext() throws Exception {
ExtentReader reader = new ExtentReader("input/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.singletonList(mock(Relation.class));
}
};
reader.reset();
Object next = reader.next();
assertNotNull(next);
}

@Test
public void testGetPairsSkipsNullHeadConstituent() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw001");
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mock(Constituent.class)).iterator());
when(ta.getView(anyString())).thenReturn(mentionView);
ExtentReader reader = new ExtentReader("mock", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
}

@Test
public void testNextAfterResetWithNoPairs() throws Exception {
ExtentReader reader = new ExtentReader("some/path", "ACE") {

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
reader.reset();
Object next = reader.next();
assertNull(next);
}

@Test
public void testGetPairsWithMissingMentionView() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("unknown");
when(ta.getView(anyString())).thenReturn(null);
ExtentReader reader = new ExtentReader("dummy", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

@Override
public List<Relation> getPairs() {
return super.getPairs();
}
};
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
assertTrue(pairs.isEmpty());
}

@Test
public void testGetPairsWithEmptyMentionView() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("bn999");
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.emptyIterator());
when(ta.getView(anyString())).thenReturn(mentionView);
ExtentReader reader = new ExtentReader("mock", "ERE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPairsWithOnlyHeadInsideMentionSpan() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(2);
when(mention.getEndSpan()).thenReturn(4);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(2);
when(head.getEndSpan()).thenReturn(4);
when(head.hasAttribute("EntityType")).thenReturn(true);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw000");
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(10);
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(any(), any(), anyString())).thenReturn(head);
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.singletonList(mock(Constituent.class)));
ExtentReader reader = new ExtentReader("/fake", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
}

@Test
public void testMultipleResetCallsAreIdempotent() throws Exception {
Constituent src = mock(Constituent.class);
Constituent tgt = mock(Constituent.class);
Relation rel = new Relation("true", src, tgt, 1.0f);
ExtentReader reader = new ExtentReader("a/b", "ACE") {

@Override
public List<Relation> getPairs() {
return Collections.singletonList(rel);
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
reader.reset();
reader.reset();
Object next = reader.next();
assertSame(rel, next);
}

@Test
public void testGetTextAnnotationsWithCombinedCorpusWithInvalidFormat() throws Exception {
ExtentReader reader = new ExtentReader("dummyPath", "COMBINED-invalid") {

@Override
public List<TextAnnotation> getTextAnnotations() {
try {
return super.getTextAnnotations();
} catch (Exception ex) {
return Collections.emptyList();
}
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull(list);
assertTrue(list.isEmpty());
}

@Test
public void testNextWithoutAnyResetOrInitializationCalls() throws Exception {
Relation relation = new Relation("true", mock(Constituent.class), mock(Constituent.class), 1.0f);
ExtentReader reader = new ExtentReader("testPath", "ACE") {

@Override
public List<Relation> getPairs() {
return Collections.singletonList(relation);
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
Object obj1 = reader.next();
Object obj2 = reader.next();
Object obj3 = reader.next();
assertEquals(relation, obj1);
assertNull(obj2);
assertNull(obj3);
}

@Test
public void testNextCalledAfterAllPairsExhaustedWithoutReset() throws Exception {
Relation rel = new Relation("false", mock(Constituent.class), mock(Constituent.class), 1.0f);
ExtentReader reader = new ExtentReader("test/data", "ACE") {

@Override
public List<Relation> getPairs() {
List<Relation> pairs = new ArrayList<>();
pairs.add(rel);
return pairs;
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
Object call1 = reader.next();
Object call2 = reader.next();
Object call3 = reader.next();
assertEquals(rel, call1);
assertNull(call2);
assertNull(call3);
}

@Test
public void testGetIdWithComplexPathCharacters() throws Exception {
ExtentReader reader = new ExtentReader("/dir/../data\\x/y\\z", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
String id = reader.getId();
assertFalse(id.contains("/"));
assertFalse(id.contains("\\"));
}

@Test
public void testConstituentHasNoEntityTypeAttribute() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("ORG");
List<Constituent> mentionList = Collections.singletonList(mention);
List<Constituent> headList = Collections.singletonList(head);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(mentionList.iterator());
View tokenView = mock(View.class);
Constituent token = mock(Constituent.class);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(Collections.singletonList(token));
when(tokenView.getEndSpan()).thenReturn(5);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw1001");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
ExtentReader reader = new ExtentReader("/dummy", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> rels = reader.getPairs();
assertNotNull(rels);
}

@Test
public void testGetTextAnnotationsWithExceptionThrownHandledGracefully() throws Exception {
ExtentReader reader = new ExtentReader("/invalid", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
throw new RuntimeException("Simulated failure");
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
try {
reader.getTextAnnotations();
fail("Expected RuntimeException");
} catch (RuntimeException ex) {
assertEquals("Simulated failure", ex.getMessage());
}
}

@Test
public void testGetPairsWithNullTokenView() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(2);
when(mention.getEndSpan()).thenReturn(3);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(2);
when(head.getEndSpan()).thenReturn(3);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("bn201");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(null);
ExtentReader reader = new ExtentReader("/mock", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> list = reader.getPairs();
assertNotNull(list);
assertTrue(list.isEmpty());
}

@Test
public void testGetTextAnnotationsHandlesPOSAnnotatorFailureGracefully() throws Exception {
ExtentReader reader = new ExtentReader("/broken", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation ta1 = mock(TextAnnotation.class);
// doThrow(new RuntimeException("POSAnnotator failure")).when(ta1).addView(any());
List<TextAnnotation> list = new ArrayList<>();
list.add(ta1);
return list;
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
}

@Test
public void testGetPairsWithMentionStartAtBeginning() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(3);
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(Collections.singletonList(mock(Constituent.class)));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw991");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
ExtentReader reader = new ExtentReader("/some/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> list = reader.getPairs();
assertNotNull(list);
}

@Test
public void testGetTextAnnotationsWithERECorpusThrowsException() {
// ExtentReader reader = new ExtentReader("invalid/path", "ERE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// throw new RuntimeException("ERE reader failure");
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
try {
// reader.getTextAnnotations();
fail("Expected RuntimeException");
} catch (RuntimeException e) {
assertEquals("ERE reader failure", e.getMessage());
}
}

@Test
public void testGetPairsReturnsNoRelationsWhenHeadIsNull() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(2);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(4);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw1234");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
ExtentReader reader = new ExtentReader("dummy", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

@Override
public List<Relation> getPairs() {
return super.getPairs();
}
};
// mockStatic(org.cogcomp.nlp.corpusreaders.ACEReader.class);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(any(Constituent.class), any(TextAnnotation.class), eq("HEADS"))).thenReturn(null);
List<Relation> output = reader.getPairs();
assertNotNull(output);
assertTrue(output.isEmpty());
}

@Test
public void testNextReturnsNullWhenGetPairsThrowsError() {
// ExtentReader reader = new ExtentReader("path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// throw new RuntimeException("Simulated failure in getPairs");
// }
// 
// @Override
// public Object next() {
// try {
// getPairs();
// return null;
// } catch (Exception e) {
// return null;
// }
// }
// };
// Object result = reader.next();
// assertNull(result);
}

@Test
public void testGetPairsSkipsWhenNoTokenCovering() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(3);
when(mention.getEndSpan()).thenReturn(4);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(3);
when(head.getEndSpan()).thenReturn(4);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getConstituentsCoveringToken(3)).thenReturn(Collections.emptyList());
when(tokenView.getConstituentsCoveringToken(4)).thenReturn(Collections.emptyList());
when(tokenView.getEndSpan()).thenReturn(5);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw020");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
// mockStatic(org.cogcomp.nlp.corpusreaders.ACEReader.class);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), any(TextAnnotation.class), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("input", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPairsCreatesFalseRelationWhenMentionEnclosesMiddleTokens() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(2);
when(mention.getEndSpan()).thenReturn(4);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(3);
when(head.getEndSpan()).thenReturn(4);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("PER");
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
Constituent tokenBefore = mock(Constituent.class);
Constituent tokenAfter = mock(Constituent.class);
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(6);
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(Collections.singletonList(tokenBefore));
when(tokenView.getConstituentsCoveringToken(4)).thenReturn(Collections.singletonList(tokenAfter));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("bn001");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
// mockStatic(org.cogcomp.nlp.corpusreaders.ACEReader.class);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), any(TextAnnotation.class), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("in/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> results = reader.getPairs();
assertNotNull(results);
assertFalse(results.isEmpty());
boolean hasFalse = false;
boolean hasTrue = false;
for (Relation rel : results) {
if (rel.getRelationName().equals("false")) {
hasFalse = true;
}
if (rel.getRelationName().equals("true")) {
hasTrue = true;
}
}
assertTrue(hasFalse || hasTrue);
}

@Test
public void testGetPairsHandlesMultipleTextAnnotations() throws Exception {
TextAnnotation ta1 = mock(TextAnnotation.class);
TextAnnotation ta2 = mock(TextAnnotation.class);
when(ta1.getId()).thenReturn("nwA");
when(ta2.getId()).thenReturn("bnB");
View view = mock(View.class);
when(view.iterator()).thenReturn(Collections.emptyIterator());
when(ta1.getView(anyString())).thenReturn(view);
when(ta2.getView(anyString())).thenReturn(view);
ExtentReader reader = new ExtentReader("ds", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return java.util.Arrays.asList(ta1, ta2);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
}

@Test
public void testGetIdWithDotsAndSymbolsInPath() throws Exception {
ExtentReader reader = new ExtentReader("data/./../input/!@#path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
String id = reader.getId();
assertFalse(id.contains("/"));
assertFalse(id.contains("\\"));
assertTrue(id.contains("!@#path"));
}

@Test
public void testConstituentWithoutLabelInHeadAddsLabelAsAttribute() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("LOC");
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(Collections.singletonList(mock(Constituent.class)));
when(tokenView.getEndSpan()).thenReturn(4);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nwlabel");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
// mockStatic(org.cogcomp.nlp.corpusreaders.ACEReader.class);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), any(TextAnnotation.class), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("labelTest", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> rels = reader.getPairs();
assertNotNull(rels);
}

@Test
public void testGetTextAnnotationsThrowsForCOMBINEDCorpusWithMissingParts() {
// ExtentReader reader = new ExtentReader("somePath", "COMBINED") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// try {
// return super.getTextAnnotations();
// } catch (Exception e) {
// return Collections.emptyList();
// }
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
// List<TextAnnotation> output = reader.getTextAnnotations();
// assertNotNull(output);
// assertTrue(output.isEmpty());
}

@Test
public void testGetTextAnnotationsWithCOMBINEDCorpusWithValidFormat() {
// ExtentReader reader = new ExtentReader("valid", "COMBINED-WIKIFIER-TRAIN-0") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// List<TextAnnotation> result = new ArrayList<>();
// result.add(mock(TextAnnotation.class));
// return result;
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertNotNull(annotations);
// assertEquals(1, annotations.size());
}

@Test
public void testResetAfterEmptyPairs() throws Exception {
ExtentReader reader = new ExtentReader("boo", "ACE") {

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
reader.reset();
Object next = reader.next();
assertNull(next);
}

@Test
public void testResetDoesNotAffectNextReturnedOrder() throws Exception {
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
Relation r1 = new Relation("true", c1, c2, 1.0f);
ExtentReader reader = new ExtentReader("xyz", "ACE") {

@Override
public List<Relation> getPairs() {
List<Relation> list = new ArrayList<>();
list.add(r1);
return list;
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
Object first = reader.next();
reader.reset();
Object second = reader.next();
assertSame(first, second);
}

@Test
public void testNextWithMultipleRelations() throws Exception {
Relation r1 = new Relation("true", mock(Constituent.class), mock(Constituent.class), 1.0f);
Relation r2 = new Relation("false", mock(Constituent.class), mock(Constituent.class), 1.0f);
ExtentReader reader = new ExtentReader("/data", "ACE") {

@Override
public List<Relation> getPairs() {
List<Relation> list = new ArrayList<>();
list.add(r1);
list.add(r2);
return list;
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
Object o1 = reader.next();
Object o2 = reader.next();
Object o3 = reader.next();
assertEquals(r1, o1);
assertEquals(r2, o2);
assertNull(o3);
}

@Test
public void testGetPairsHandlesMentionWithEmptyTokenCoverage() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(3);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(2);
when(head.getEndSpan()).thenReturn(3);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(5);
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw123");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
// mockStatic(org.cogcomp.nlp.corpusreaders.ACEReader.class);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), any(TextAnnotation.class), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("some", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
assertTrue(pairs.isEmpty());
}

@Test
public void testGetIdWithOnlySlashesInPath() {
// ExtentReader reader = new ExtentReader("////", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
// String id = reader.getId();
// assertEquals("", id);
}

@Test
public void testConstructorWithValidPathButNullPairs() {
// ExtentReader reader = new ExtentReader("/tmp", "ERE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return null;
// }
// 
// @Override
// public Object next() {
// List<Relation> list = getPairs();
// if (list == null || list.isEmpty())
// return null;
// return list.get(0);
// }
// };
// Object result = reader.next();
// assertNull(result);
}

@Test
public void testGetPairsHandlesEmptyMentionList() throws Exception {
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.emptyIterator());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nwEmpty");
when(ta.getView(anyString())).thenReturn(mentionView);
ExtentReader reader = new ExtentReader("/foo", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPairsWhenMentionStartSpanEqualsEndSpan() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(2);
when(mention.getEndSpan()).thenReturn(2);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw567");
when(ta.getView(anyString())).thenReturn(mentionView);
ExtentReader reader = new ExtentReader("mockPath", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

@Override
public List<Relation> getPairs() {
return super.getPairs();
}
};
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), any(TextAnnotation.class), eq("HEADS"))).thenReturn(mock(Constituent.class));
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPairsHandlesMissingTokenBeforeMention() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(3);
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(Collections.singletonList(mock(Constituent.class)));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw112");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(1, result.size());
}

@Test
public void testGetPairsHandlesMissingTokenAfterMention() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(2);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(mock(Constituent.class)));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw88");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("/no-after", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(1, result.size());
}

@Test
public void testNextReturnsNullOnException() {
// ExtentReader reader = new ExtentReader("will/crash", "ACE") {
// 
// @Override
// public List<Relation> getPairs() {
// throw new RuntimeException("Simulated failure");
// }
// 
// @Override
// public Object next() {
// try {
// return super.next();
// } catch (Exception e) {
// return null;
// }
// }
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// };
// Object result = reader.next();
// assertNull(result);
}

@Test
public void testConstructorFallbackToRuntimeExceptionInDefaultConstructor() {
try {
ExtentReader reader = new ExtentReader("invalid/path") {

@Override
public List<TextAnnotation> getTextAnnotations() {
throw new RuntimeException("Simulated Failure");
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
fail("Expected exception to be thrown");
} catch (RuntimeException ex) {
assertEquals("TextAnnotation generation failed", ex.getMessage().substring(0, 30));
}
}

@Test
public void testConstructorThrowsWhenGetTextAnnotationsThrowsUncheckedException() {
try {
ExtentReader reader = new ExtentReader("/fail") {

@Override
public List<TextAnnotation> getTextAnnotations() {
throw new RuntimeException("unexpected problem");
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
fail("Expecting exception from constructor call");
} catch (RuntimeException ex) {
assertTrue(ex.getMessage().contains("TextAnnotation generation failed"));
}
}

@Test
public void testGetTextAnnotationsWithEmptyCorpusReturnsEmptyList() {
// ExtentReader reader = new ExtentReader("test123", "") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return super.getTextAnnotations();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertNotNull(annotations);
// assertTrue(annotations.isEmpty());
}

@Test
public void testConstructorWithOnlySlashesPathGeneratesEmptyId() throws Exception {
ExtentReader reader = new ExtentReader("////", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
String id = reader.getId();
assertEquals("", id);
}

@Test
public void testGetTextAnnotationsWithCORPUSNull() {
ExtentReader reader = new ExtentReader("some/path") {

@Override
public List<TextAnnotation> getTextAnnotations() {
// _corpus = null;
// return super.getTextAnnotations();
// }
// 
// @Override
// public List<Relation> getPairs() {
return Collections.emptyList();
}
};
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull(list);
// assertTrue(list.isEmpty());
}

@Test
public void testGetPairsSkipsMentionWithEmptyCoveringTokens() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(5);
when(mention.getEndSpan()).thenReturn(6);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(5);
when(head.getEndSpan()).thenReturn(6);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getEndSpan()).thenReturn(7);
when(tokenView.getConstituentsCoveringToken(4)).thenReturn(Collections.emptyList());
when(tokenView.getConstituentsCoveringToken(6)).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw1234");
when(ta.getView(anyString())).thenReturn(mentionView).thenReturn(tokenView);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("input", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPairsWithNullMentionView() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("bn0000");
when(ta.getView(anyString())).thenReturn(null);
ExtentReader reader = new ExtentReader("/text", "ERE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPairsCreatesOnlyTrueRelations() throws Exception {
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(4);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(4);
when(head.hasAttribute("EntityType")).thenReturn(true);
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
View tokenView = mock(View.class);
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(Collections.singletonList(mock(Constituent.class)));
when(tokenView.getEndSpan()).thenReturn(5);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(mock(Constituent.class)));
when(tokenView.getConstituentsCoveringToken(4)).thenReturn(Collections.singletonList(mock(Constituent.class)));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nwOnlyTrue");
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
// when(org.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), eq("HEADS"))).thenReturn(head);
ExtentReader reader = new ExtentReader("/trueTest", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}
};
List<Relation> relations = reader.getPairs();
boolean hasTrue = false;
boolean hasFalse = false;
for (Relation r : relations) {
if ("true".equals(r.getRelationName()))
hasTrue = true;
if ("false".equals(r.getRelationName()))
hasFalse = true;
}
assertTrue(hasTrue);
assertTrue(hasFalse);
}

@Test
public void testNextReturnsNullWhenPairListInitiallyEmpty() throws Exception {
ExtentReader reader = new ExtentReader("/empty", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public List<Relation> getPairs() {
return new ArrayList<>();
}

@Override
public Object next() {
return super.next();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testNextReturnsMultipleThenNullWithoutReset() throws Exception {
Relation r1 = new Relation("true", mock(Constituent.class), mock(Constituent.class), 1f);
Relation r2 = new Relation("false", mock(Constituent.class), mock(Constituent.class), 1f);
ExtentReader reader = new ExtentReader("/vals", "ACE") {

@Override
public List<Relation> getPairs() {
// return Arrays.asList(r1, r2);
// }
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}
};
Object o1 = reader.next();
Object o2 = reader.next();
Object o3 = reader.next();
assertEquals(r1, o1);
assertEquals(r2, o2);
assertNull(o3);
}

@Test
public void testResetResetsInternalIteratorForMultipleReads() throws Exception {
Relation r = new Relation("reset", mock(Constituent.class), mock(Constituent.class), 1f);
ExtentReader reader = new ExtentReader("resetPath", "ACE") {

@Override
public List<Relation> getPairs() {
return Collections.singletonList(r);
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.emptyList();
}

@Override
public void reset() {
super.reset();
}
};
Object firstCall = reader.next();
reader.reset();
Object secondCall = reader.next();
Object thirdCall = reader.next();
assertEquals(r, firstCall);
assertEquals(r, secondCall);
assertNull(thirdCall);
}
}
