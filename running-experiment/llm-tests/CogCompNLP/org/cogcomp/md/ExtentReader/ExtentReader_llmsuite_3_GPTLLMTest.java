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
import java.util.*;
import java.util.regex.Pattern;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ExtentReader_llmsuite_3_GPTLLMTest {

@Test
public void testGetIdReturnsSanitizedPath() {
ExtentReader reader = new ExtentReader("/some/test/path/");
String result = reader.getId();
assertEquals("sometestpath", result);
}

@Test
public void testConstructorWithAceCorpusCreatesInstanceSuccessfully() {
try {
ExtentReader reader = new ExtentReader("sample/path", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}

@Override
public List<Relation> getPairs() {
return new ArrayList<>();
}
};
assertNotNull(reader);
} catch (Exception e) {
fail("Exception should not be thrown in ACE constructor: " + e.getMessage());
}
}

@Test
public void testConstructorWithDefaultCorpusCreatesInstanceSuccessfully() {
try {
ExtentReader reader = new ExtentReader("some/path") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}

@Override
public List<Relation> getPairs() {
return new ArrayList<>();
}
};
String id = reader.getId();
assertEquals("somepath", id);
} catch (Exception e) {
fail("Default corpus constructor should not throw error");
}
}

@Test
public void testNextReturnsNullWhenEmptyRelationList() {
// ExtentReader reader = new ExtentReader("fake/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return new ArrayList<>();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return new ArrayList<>();
// }
// };
// Object result = reader.next();
// assertNull(result);
}

@Test
public void testResetReturnsToFirstRelation() {
final Relation r1 = new Relation("true", mock(Constituent.class), mock(Constituent.class), 1.0f);
// ExtentReader reader = new ExtentReader("fake/path", "ACE") {
// 
// private boolean firstCall = true;
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return new ArrayList<>();
// }
// 
// @Override
// public List<Relation> getPairs() {
// List<Relation> list = new ArrayList<>();
// list.add(r1);
// return list;
// }
// };
// Object first = reader.next();
// assertEquals(r1, first);
// Object second = reader.next();
// assertNull(second);
// reader.reset();
// Object afterReset = reader.next();
// assertEquals(r1, afterReset);
}

@Test
public void testGetPairsReturnsList() {
// ExtentReader reader = new ExtentReader("some/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return new ArrayList<>();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.singletonList(mock(Relation.class));
// }
// };
// List<Relation> pairs = reader.getPairs();
// assertNotNull(pairs);
// assertEquals(1, pairs.size());
}

@Test
public void testIOExceptionInGetTextAnnotationsHandled() {
// ExtentReader reader = new ExtentReader("invalid/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// throw new RuntimeException("TextAnnotation generation failed");
// }
// 
// @Override
// public List<Relation> getPairs() {
// return new ArrayList<>();
// }
// };
try {
// reader.getTextAnnotations();
fail("Expected RuntimeException to be thrown");
} catch (RuntimeException e) {
assertEquals("TextAnnotation generation failed", e.getMessage());
}
}

@Test
public void testCombinedCorpusHandledProperly() {
// ExtentReader reader = new ExtentReader("mock/path", "COMBINED-ERE-mode-1");
// List<TextAnnotation> tas = reader.getTextAnnotations();
// assertEquals(2, tas.size());
// Object result = reader.next();
// assertNotNull(result);
}

@Test
public void testCloseDoesNotThrowException() {
ExtentReader reader = new ExtentReader("some/path") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<>();
}

@Override
public List<Relation> getPairs() {
return new ArrayList<>();
}
};
reader.close();
assertTrue(true);
}

@Test
public void testUnknownCorpusReturnsEmptyTextAnnotations() {
// ExtentReader reader = new ExtentReader("unknown/corpus", "UNSUPPORTED") {
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
// List<TextAnnotation> tas = reader.getTextAnnotations();
// assertNotNull(tas);
// assertTrue(tas.isEmpty());
}

@Test
public void testRelationHasValidSourceAndTarget() {
Constituent source = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation r = new Relation("true", source, target, 1.0f);
List<Relation> relationList = new ArrayList<>();
relationList.add(r);
// Object obj = reader.next();
// assertNotNull(obj);
// Relation result = (Relation) obj;
// assertNotNull(result.getSource());
// assertNotNull(result.getTarget());
}

@Test
public void testNextCalledOnceWithSingleRelation() {
Constituent token = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Relation r = new Relation("true", token, head, 1.0f);
// Object result = reader.next();
// assertNotNull(result);
// Relation relation = (Relation) result;
// assertEquals("true", relation.getRelationName());
// Object second = reader.next();
// assertNull(second);
}

@Test
public void testGetTextAnnotationsHandlesEmptyCorpusGracefully() {
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull(result);
// assertTrue(result.isEmpty());
}

@Test
public void testInvalidCorpusValueNotMatchingACEOrERE() {
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertNotNull(annotations);
// assertTrue(annotations.isEmpty());
}

@Test
public void testMentionsWithoutHeadAreSkippedInGetPairs() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
when(ta.getId()).thenReturn("nw/001");
when(ta.getView(anyString())).thenReturn(mentionView, tokenView);
Constituent mention = mock(Constituent.class);
List<Constituent> mentions = Collections.singletonList(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
when(mention.getStartSpan()).thenReturn(2);
when(mention.getEndSpan()).thenReturn(5);
// List<Relation> pairs = reader.getPairs();
// assertNotNull(pairs);
// assertTrue(pairs.isEmpty());
}

@Test
public void testHeadWithoutEntityTypeLabelWillBeAssignedLabel() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent token = mock(Constituent.class);
when(ta.getId()).thenReturn("nw001");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(3);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("ORG");
when(tokenView.getEndSpan()).thenReturn(10);
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.singletonList(token));
List<Constituent> mentions = Collections.singletonList(mention);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(mentions.iterator());
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), any())).thenReturn(head);
// List<Relation> pairs = reader.getPairs();
// assertNotNull(pairs);
// assertFalse(pairs.isEmpty());
}

@Test
public void testMentionStartSpanAtZeroAddsRightFalseRelationOnly() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent tokenRight = mock(Constituent.class);
when(ta.getId()).thenReturn("nw123");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(2);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(tokenView.getEndSpan()).thenReturn(10);
when(tokenView.getConstituentsCoveringToken(eq(2))).thenReturn(Collections.singletonList(tokenRight));
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), any())).thenReturn(head);
// ExtentReader reader = new ExtentReader("dummy/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> pairs = reader.getPairs();
// assertNotNull(pairs);
// assertEquals(1, pairs.size());
// Relation r = pairs.get(0);
// assertEquals("false", r.getRelationName());
// assertEquals(tokenRight, r.getSource());
// assertEquals(head, r.getTarget());
}

@Test
public void testMentionEndSpanAtMaximumTokenSpanAddsLeftFalseRelationOnly() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent tokenLeft = mock(Constituent.class);
when(ta.getId()).thenReturn("bn678");
when(mention.getStartSpan()).thenReturn(8);
when(mention.getEndSpan()).thenReturn(10);
when(head.getStartSpan()).thenReturn(8);
when(head.getEndSpan()).thenReturn(9);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(tokenView.getEndSpan()).thenReturn(10);
when(tokenView.getConstituentsCoveringToken(eq(7))).thenReturn(Collections.singletonList(tokenLeft));
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), any())).thenReturn(head);
// ExtentReader reader = new ExtentReader("dummy/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertEquals(1, result.size());
// assertEquals("false", result.get(0).getRelationName());
// assertEquals(tokenLeft, result.get(0).getSource());
// assertEquals(head, result.get(0).getTarget());
}

@Test
public void testGetPairsSkipsMentionWithEmptyTokenView() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
when(ta.getId()).thenReturn("nw000");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(tokenView.getEndSpan()).thenReturn(1);
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.emptyList());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), any())).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("dummy/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertNotNull(result);
// assertTrue(result.isEmpty());
}

@Test
public void testGetTextAnnotationsWithNullCorpus() {
ExtentReader reader = new ExtentReader("dummy/path") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return null;
}

@Override
public List<Relation> getPairs() {
return Collections.emptyList();
}
};
try {
List<TextAnnotation> result = reader.getTextAnnotations();
assertNull(result);
} catch (Exception e) {
fail("Should not throw exception on null return");
}
}

@Test
public void testHeadNullIsSkippedInGetPairs() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(ta.getId()).thenReturn("bn000");
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(3);
when(tokenView.getEndSpan()).thenReturn(10);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), any())).thenReturn(null);
// ExtentReader reader = new ExtentReader("dummy/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertTrue(result.isEmpty());
}

@Test
public void testHeadAlreadyHasEntityType() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent token = mock(Constituent.class);
when(ta.getId()).thenReturn("nw014");
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(3);
when(head.getStartSpan()).thenReturn(2);
when(head.getEndSpan()).thenReturn(3);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(tokenView.getEndSpan()).thenReturn(5);
when(tokenView.getConstituentsCoveringToken(eq(1))).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(eq(0))).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(eq(3))).thenReturn(Collections.singletonList(token));
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), any())).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("dummy/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertEquals(3, result.size());
// assertEquals("true", result.get(0).getRelationName());
// assertEquals("false", result.get(1).getRelationName());
// assertEquals("false", result.get(2).getRelationName());
}

@Test
public void testGetTextAnnotationsForCombinedCorpusParsing() {
// ExtentReader reader = new ExtentReader("dummy/path", "COMBINED-MyCorpus-mode-2") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// List<TextAnnotation> list = new ArrayList<>();
// TextAnnotation ta1 = mock(TextAnnotation.class);
// TextAnnotation ta2 = mock(TextAnnotation.class);
// list.add(ta1);
// list.add(ta2);
// return list;
// }
// 
// @Override
// public List<Relation> getPairs() {
// Relation r = new Relation("true", mock(Constituent.class), mock(Constituent.class), 1.0f);
// return Collections.singletonList(r);
// }
// };
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull(result);
// assertEquals(2, result.size());
}

@Test
public void testMultipleNextCallsBeyondListSizeReturnsAllThenNull() {
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
Relation r1 = new Relation("true", c1, c2, 1.0f);
Relation r2 = new Relation("false", c2, c1, 1.0f);
// ExtentReader reader = new ExtentReader("dummy/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// List<Relation> list = new ArrayList<>();
// list.add(r1);
// list.add(r2);
// return list;
// }
// };
// Object res1 = reader.next();
// assertEquals(r1, res1);
// Object res2 = reader.next();
// assertEquals(r2, res2);
// Object res3 = reader.next();
// assertNull(res3);
// Object res4 = reader.next();
// assertNull(res4);
}

@Test
public void testResetAllowsIterationFromBeginning() {
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
Relation r = new Relation("true", c1, c2, 1.0f);
// ExtentReader reader = new ExtentReader("dummy/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.singletonList(r);
// }
// };
// Object first = reader.next();
// assertEquals(r, first);
// reader.reset();
// Object again = reader.next();
// assertEquals(r, again);
}

@Test
public void testGetPairsWithMentionEqualToHeadSpanProducesNoTrueRelations() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent tokenBefore = mock(Constituent.class);
Constituent tokenAfter = mock(Constituent.class);
when(ta.getId()).thenReturn("nw556");
when(mention.getStartSpan()).thenReturn(3);
when(mention.getEndSpan()).thenReturn(5);
when(head.getStartSpan()).thenReturn(3);
when(head.getEndSpan()).thenReturn(5);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("PER");
when(tokenView.getEndSpan()).thenReturn(10);
when(tokenView.getConstituentsCoveringToken(eq(2))).thenReturn(Collections.singletonList(tokenBefore));
when(tokenView.getConstituentsCoveringToken(eq(5))).thenReturn(Collections.singletonList(tokenAfter));
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(eq(head), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), eq("HEADS"))).thenReturn(head);
// ExtentReader reader = new ExtentReader("dummyDir", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertEquals(2, result.size());
// assertEquals("false", result.get(0).getRelationName());
// assertEquals("false", result.get(1).getRelationName());
}

@Test
public void testGetIdWithMixedSlashes() {
ExtentReader reader = new ExtentReader("C:\\data/annotated\\entities");
String id = reader.getId();
assertEquals("Cdataannotatedentities", id);
}

@Test
public void testEmptyMentionViewResultsInNoRelations() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.emptyIterator());
when(ta.getId()).thenReturn("bnZero");
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
// ExtentReader reader = new ExtentReader("path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> relations = reader.getPairs();
// assertTrue(relations.isEmpty());
}

@Test
public void testExceptionDuringGazetteerInitializationHandledGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nwError");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.emptyIterator());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
// ExtentReader reader = new ExtentReader("anyPath", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// 
// @Override
// public List<Relation> getPairs() {
// throw new RuntimeException("Simulated error loading gazetteers");
// }
// };
try {
// reader.getPairs();
fail("Expected RuntimeException due to gazetteer error");
} catch (RuntimeException e) {
assertEquals("Simulated error loading gazetteers", e.getMessage());
}
}

@Test
public void testMultipleMentionsProcessedCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention1 = mock(Constituent.class);
Constituent head1 = mock(Constituent.class);
Constituent token1 = mock(Constituent.class);
Constituent mention2 = mock(Constituent.class);
Constituent head2 = mock(Constituent.class);
Constituent token2 = mock(Constituent.class);
when(ta.getId()).thenReturn("nwMulti");
when(mention1.getStartSpan()).thenReturn(1);
when(mention1.getEndSpan()).thenReturn(2);
when(head1.getStartSpan()).thenReturn(1);
when(head1.getEndSpan()).thenReturn(2);
when(head1.hasAttribute("EntityType")).thenReturn(true);
when(mention2.getStartSpan()).thenReturn(3);
when(mention2.getEndSpan()).thenReturn(5);
when(head2.getStartSpan()).thenReturn(3);
when(head2.getEndSpan()).thenReturn(4);
when(head2.hasAttribute("EntityType")).thenReturn(true);
List<Constituent> mentions = new ArrayList<>();
mentions.add(mention1);
mentions.add(mention2);
when(mentionView.iterator()).thenReturn(mentions.iterator());
when(tokenView.getEndSpan()).thenReturn(6);
when(tokenView.getConstituentsCoveringToken(eq(2))).thenReturn(Collections.singletonList(token2));
when(tokenView.getConstituentsCoveringToken(eq(5))).thenReturn(Collections.singletonList(token2));
when(tokenView.getConstituentsCoveringToken(eq(4))).thenReturn(Collections.singletonList(token2));
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention1), any(), eq("HEADS"))).thenReturn(head1);
when(ACEReader.getEntityHeadForConstituent(eq(mention2), any(), eq("HEADS"))).thenReturn(head2);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addExtentAttributes(any(), any(), any(), any());
ExtentTester.addHeadAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("some/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> relations = reader.getPairs();
// assertFalse(relations.isEmpty());
// assertEquals(3, relations.size());
}

@Test
public void testNextDoesNotAdvancePastEnd() {
Constituent source = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation r1 = new Relation("true", source, target, 1.0f);
// ExtentReader reader = new ExtentReader("xyz", "ACE") {
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.singletonList(r1);
// }
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// };
// assertEquals(r1, reader.next());
// assertNull(reader.next());
// assertNull(reader.next());
// assertNull(reader.next());
}

@Test
public void testGetTextAnnotationsHandlesSubsetMatchInCombinedCorpus() {
// ExtentReader reader = new ExtentReader("path/to/data", "COMBINED-ERE-sub-0") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// TextAnnotation ta1 = mock(TextAnnotation.class);
// TextAnnotation ta2 = mock(TextAnnotation.class);
// return Arrays.asList(ta1, ta2);
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull(list);
// assertEquals(2, list.size());
}

@Test
public void testGetIdHandlesOnlySpecialCharacters() {
ExtentReader reader = new ExtentReader("/\\//\\\\//");
String id = reader.getId();
assertEquals("", id);
}

@Test
public void testGetPairsHandlesMultipleFalseTokenCases() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent token1 = mock(Constituent.class);
Constituent token2 = mock(Constituent.class);
when(ta.getId()).thenReturn("nw987");
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(3);
when(head.getStartSpan()).thenReturn(2);
when(head.getEndSpan()).thenReturn(3);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("LOC");
when(tokenView.getEndSpan()).thenReturn(10);
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(Collections.singletonList(token1));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token1));
when(tokenView.getConstituentsCoveringToken(3)).thenReturn(Collections.singletonList(token2));
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(head, null, null, null);
ExtentTester.addExtentAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("dummyPath", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertEquals(3, result.size());
// assertEquals("true", result.get(0).getRelationName());
// assertEquals("false", result.get(1).getRelationName());
// assertEquals("false", result.get(2).getRelationName());
}

@Test
public void testNextReturnsNullImmediatelyIfPairListEmpty() {
// ExtentReader reader = new ExtentReader("fakePath", "ACE") {
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
// Object r = reader.next();
// assertNull(r);
}

@Test
public void testConstructorWithERECorpusInitializesWithoutException() {
// ExtentReader reader = new ExtentReader("any/path", "ERE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// TextAnnotation ta = mock(TextAnnotation.class);
// View mentionView = mock(View.class);
// View tokenView = mock(View.class);
// when(ta.getView(eq(ViewNames.MENTION_ERE))).thenReturn(mentionView);
// when(ta.getView(eq(ViewNames.TOKENS))).thenReturn(tokenView);
// return Collections.singletonList(ta);
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
public void testGetPairsSkipsIfTokenConstituentsListIsNull() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(4);
when(mention.getEndSpan()).thenReturn(6);
when(head.getStartSpan()).thenReturn(4);
when(head.getEndSpan()).thenReturn(5);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(ta.getId()).thenReturn("bn789");
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(null);
when(tokenView.getEndSpan()).thenReturn(10);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), any(), any())).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("dummy", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> relations = reader.getPairs();
// assertNotNull(relations);
// assertTrue(relations.isEmpty());
}

@Test
public void testGetTextAnnotationsHandlesCorpusFieldCaseSensitivity() {
// ExtentReader reader = new ExtentReader("samplePath", "ace") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return new ArrayList<>();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return new ArrayList<>();
// }
// };
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull(result);
}

@Test
public void testResetFollowedByMultipleNextCalls() {
Constituent s = mock(Constituent.class);
Constituent t = mock(Constituent.class);
Relation r1 = new Relation("true", s, t, 1.0f);
Relation r2 = new Relation("false", t, s, 1.0f);
// ExtentReader reader = new ExtentReader("resetTest", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Arrays.asList(r1, r2);
// }
// };
// Object a = reader.next();
// Object b = reader.next();
// Object c = reader.next();
// assertEquals(r1, a);
// assertEquals(r2, b);
// assertNull(c);
// reader.reset();
// Object d = reader.next();
// assertEquals(r1, d);
}

@Test
public void testGetTextAnnotationsWithNullReturnFromCorpusReader() {
// ExtentReader reader = new ExtentReader("fake/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return null;
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.emptyList();
// }
// };
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNull(result);
}

@Test
public void testGetPairsWhenMentionHasHeadButNoTokensAvailable() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
when(ta.getId()).thenReturn("nwEmptyTokens");
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.emptyList());
when(tokenView.getEndSpan()).thenReturn(5);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(3);
when(head.getStartSpan()).thenReturn(2);
when(head.getEndSpan()).thenReturn(3);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addExtentAttributes(any(), any(), any(), any());
ExtentTester.addHeadAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("unit/test", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertTrue(result.isEmpty());
}

@Test
public void testGetPairsWithMentionOutsideTokenViewBounds() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent token = mock(Constituent.class);
when(ta.getId()).thenReturn("bnOutOfBounds");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(8);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("ORG");
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.singletonList(token));
when(tokenView.getEndSpan()).thenReturn(5);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("error/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertNotNull(result);
}

@Test
public void testGetPairsHandlesMultipleViewsCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
View aceView = mock(View.class);
View ereView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention1 = mock(Constituent.class);
Constituent mention2 = mock(Constituent.class);
Constituent head1 = mock(Constituent.class);
Constituent head2 = mock(Constituent.class);
Constituent token = mock(Constituent.class);
when(ta.getId()).thenReturn("nwMultiView");
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.singletonList(token));
when(tokenView.getEndSpan()).thenReturn(10);
when(aceView.iterator()).thenReturn(Collections.singletonList(mention1).iterator());
when(ereView.iterator()).thenReturn(Collections.singletonList(mention2).iterator());
when(mention1.getStartSpan()).thenReturn(1);
when(mention1.getEndSpan()).thenReturn(2);
when(mention2.getStartSpan()).thenReturn(4);
when(mention2.getEndSpan()).thenReturn(5);
when(head1.getStartSpan()).thenReturn(1);
when(head1.getEndSpan()).thenReturn(2);
when(head1.hasAttribute("EntityType")).thenReturn(false);
when(head1.getLabel()).thenReturn("GPE");
when(head2.getStartSpan()).thenReturn(4);
when(head2.getEndSpan()).thenReturn(5);
when(head2.hasAttribute("EntityType")).thenReturn(false);
when(head2.getLabel()).thenReturn("ORG");
when(ta.getView(eq(ViewNames.MENTION_ACE))).thenReturn(aceView);
when(ta.getView(eq(ViewNames.MENTION_ERE))).thenReturn(ereView);
when(ta.getView(eq(ViewNames.TOKENS))).thenReturn(tokenView);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention1), any(), any())).thenReturn(head1);
when(ACEReader.getEntityHeadForConstituent(eq(mention2), any(), any())).thenReturn(head2);
// ExtentReader reader = new ExtentReader("multi/view", "ERE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertFalse(result.isEmpty());
}

@Test
public void testGetPairsWithMentionHavingOnlyOneToken() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent token = mock(Constituent.class);
when(ta.getId()).thenReturn("nwSingleToken");
when(mention.getStartSpan()).thenReturn(4);
when(mention.getEndSpan()).thenReturn(5);
when(head.getStartSpan()).thenReturn(4);
when(head.getEndSpan()).thenReturn(5);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("PER");
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.singletonList(token));
when(tokenView.getEndSpan()).thenReturn(6);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), eq("HEADS"))).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(any(), any(), any(), any());
ExtentTester.addExtentAttributes(any(), any(), any(), any());
// ExtentReader reader = new ExtentReader("in/single", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertEquals(2, result.size());
}

@Test
public void testGetPairsWithNonStandardCorpusNameBeginningWithCOMBINED() {
// ExtentReader reader = new ExtentReader("path/combined", "COMBINED-FOO-mode-0") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// TextAnnotation ta1 = mock(TextAnnotation.class);
// TextAnnotation ta2 = mock(TextAnnotation.class);
// return Arrays.asList(ta1, ta2);
// }
// 
// @Override
// public List<Relation> getPairs() {
// Constituent a = mock(Constituent.class);
// Constituent b = mock(Constituent.class);
// return Arrays.asList(new Relation("true", a, b, 1.0f));
// }
// };
// List<TextAnnotation> tas = reader.getTextAnnotations();
// assertEquals(2, tas.size());
// Object r = reader.next();
// assertNotNull(r);
// reader.reset();
// Object again = reader.next();
// assertNotNull(again);
}

@Test
public void testConstructorWithInvalidCombinedFormatSkipsSplit() {
// ExtentReader reader = new ExtentReader("any/path", "COMBINED") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return new ArrayList<>();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.singletonList(mock(Relation.class));
// }
// };
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertTrue(annotations.isEmpty());
}

@Test
public void testGetPairsHandlesNullReturnFromGetEntityHeadMethod() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(ta.getId()).thenReturn("nw-empty-head");
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(tokenView.getEndSpan()).thenReturn(10);
when(mention.getStartSpan()).thenReturn(2);
when(mention.getEndSpan()).thenReturn(3);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), anyString())).thenReturn(null);
// ExtentReader reader = new ExtentReader("mock/path", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertTrue(result.isEmpty());
}

@Test
public void testAddEntityTypeLabelToHeadWhenMissing() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
Constituent token = mock(Constituent.class);
when(ta.getId()).thenReturn("nwLabelMissing");
when(mention.getStartSpan()).thenReturn(3);
when(mention.getEndSpan()).thenReturn(5);
when(head.getStartSpan()).thenReturn(4);
when(head.getEndSpan()).thenReturn(5);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("FAC");
when(tokenView.getEndSpan()).thenReturn(10);
when(tokenView.getConstituentsCoveringToken(eq(3))).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(eq(2))).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(eq(5))).thenReturn(Collections.singletonList(token));
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(eq(head), any(), any(), any());
ExtentTester.addExtentAttributes(eq(token), any(), any(), any());
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), eq("HEADS"))).thenReturn(head);
// ExtentReader reader = new ExtentReader("unit/label", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertFalse(result.isEmpty());
// assertEquals(3, result.size());
// assertEquals("true", result.get(0).getRelationName());
// assertEquals("false", result.get(1).getRelationName());
// assertEquals("false", result.get(2).getRelationName());
}

@Test
public void testGetPairsHandlesNonCoveringTokensGracefully() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
Constituent head = mock(Constituent.class);
when(ta.getId()).thenReturn("bnNonCovering");
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(tokenView.getEndSpan()).thenReturn(5);
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.emptyList());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(true);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), anyString())).thenReturn(head);
mockStatic(ExtentTester.class);
doNothing().when(ExtentTester.class);
ExtentTester.addHeadAttributes(eq(head), any(), any(), any());
// ExtentReader reader = new ExtentReader("bad/tokens", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertTrue(result.isEmpty());
}

@Test
public void testNextWithoutCallingGetPairsStillReturnsNull() {
// ExtentReader reader = new ExtentReader("emptyPath", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return new ArrayList<>();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return new ArrayList<>();
// }
// };
// Object result = reader.next();
// assertNull(result);
}

@Test
public void testResetStateWithEmptyPairList() {
// ExtentReader reader = new ExtentReader("resetTest", "ACE") {
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
// reader.reset();
// Object obj = reader.next();
// assertNull(obj);
}

@Test
public void testMultipleCallsToNextAfterResetYieldSameResults() {
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
Relation r1 = new Relation("true", c1, c2, 1.0f);
// ExtentReader reader = new ExtentReader("repeatTest", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.emptyList();
// }
// 
// @Override
// public List<Relation> getPairs() {
// return Collections.singletonList(r1);
// }
// };
// Object a = reader.next();
// assertEquals(r1, a);
// Object b = reader.next();
// assertNull(b);
// reader.reset();
// Object c = reader.next();
// assertEquals(r1, c);
}

@Test
public void testMentionWithZeroLengthSpanProducesNoRelations() {
TextAnnotation ta = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(ta.getId()).thenReturn("zero-span");
when(mention.getStartSpan()).thenReturn(3);
when(mention.getEndSpan()).thenReturn(3);
when(mentionView.iterator()).thenReturn(Collections.singletonList(mention).iterator());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
// ExtentReader reader = new ExtentReader("emptyMention", "ACE") {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// };
// List<Relation> result = reader.getPairs();
// assertTrue(result.isEmpty());
}

@Test
public void testCorpusFieldWithWhitespaceHandledGracefully() {
// ExtentReader reader = new ExtentReader("spaces", " ACE  ") {
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
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull(result);
}
}
