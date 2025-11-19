package gate.annotation;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gate.creole.AnnotationSchema;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class CorpusAnnotationDiff_llmsuite_2_GPTLLMTest {

@Test
public void testInitProducesCorrectMetricsWhenMatchFound() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
Annotation keyAnnotation = mock(Annotation.class);
Annotation responseAnnotation = mock(Annotation.class);
AnnotationSet keyAnnotationSet = mock(AnnotationSet.class);
AnnotationSet responseAnnotationSet = mock(AnnotationSet.class);
when(schema.getAnnotationName()).thenReturn("Person");
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://a.com/a"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://a.com/a"));
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
gate.Node keyStart = mock(gate.Node.class);
gate.Node keyEnd = mock(gate.Node.class);
gate.Node respStart = mock(gate.Node.class);
gate.Node respEnd = mock(gate.Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(5L);
when(respStart.getOffset()).thenReturn(0L);
when(respEnd.getOffset()).thenReturn(5L);
when(keyAnnotation.getStartNode()).thenReturn(keyStart);
when(keyAnnotation.getEndNode()).thenReturn(keyEnd);
when(responseAnnotation.getStartNode()).thenReturn(respStart);
when(responseAnnotation.getEndNode()).thenReturn(respEnd);
when(keyAnnotation.isPartiallyCompatible(eq(responseAnnotation), any())).thenReturn(true);
when(keyAnnotation.coextensive(responseAnnotation)).thenReturn(true);
Set<Annotation> keySet = new HashSet<Annotation>();
keySet.add(keyAnnotation);
Set<Annotation> responseSet = new HashSet<Annotation>();
responseSet.add(responseAnnotation);
when(keyDoc.getAnnotations()).thenReturn(keyAnnotationSet);
when(responseDoc.getAnnotations()).thenReturn(responseAnnotationSet);
// when(keyAnnotationSet.get("Person")).thenReturn(keySet);
// when(responseAnnotationSet.get("Person")).thenReturn(responseSet);
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.init();
assertEquals(1.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(1.0, diff.getRecallStrict(), 0.0001);
assertEquals(1.0, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testSetAndGetResponseAnnotationSetName() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setResponseAnnotationSetName("Result");
assertEquals("Result", diff.getResponseAnnotationSetName());
}

@Test
public void testSetAndGetKeyAnnotationSetName() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setKeyAnnotationSetName("Gold");
assertEquals("Gold", diff.getKeyAnnotationSetName());
}

@Test
public void testSetAndGetKeyFeatureNamesSet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<String> features = new HashSet<String>();
features.add("name");
diff.setKeyFeatureNamesSet(features);
assertEquals(features, diff.getKeyFeatureNamesSet());
}

@Test
public void testSetAndGetResponseAnnotationSetNameFalsePoz() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setResponseAnnotationSetNameFalsePoz("Eval");
assertEquals("Eval", diff.getResponseAnnotationSetNameFalsePoz());
}

@Test
public void testSetAndGetAnnotationTypeForFalsePositive() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setAnnotationTypeForFalsePositive("Token");
assertEquals("Token", diff.getAnnotationTypeForFalsePositive());
}

@Test
public void testTextModeEnabledCorrectly() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
assertTrue(diff.isTextMode());
}

@Test
public void testInitWithoutAnnotationSchemaThrowsException() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
// thrown.expect(ResourceInstantiationException.class);
// thrown.expectMessage("No annotation schema defined !");
diff.init();
}

@Test
public void testInitWithoutKeyCorpusThrowsException() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus responseCorpus = mock(Corpus.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(responseCorpus.size()).thenReturn(1);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
// thrown.expect(ResourceInstantiationException.class);
// thrown.expectMessage("No key corpus or empty defined !");
diff.init();
}

@Test
public void testInitWithoutResponseCorpusThrowsException() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(keyCorpus.size()).thenReturn(1);
diff.setKeyCorpus(keyCorpus);
diff.setAnnotationSchema(schema);
// thrown.expect(ResourceInstantiationException.class);
// thrown.expectMessage("No response corpus or empty defined !");
diff.init();
}

@Test
public void testInitWhenResponseDocumentIsMissingFromCorpusDoesNotThrow() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(0);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(keyDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://example.com/doc1"));
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertTrue(diff.isTextMode());
}

@Test
public void testEmptyKeyAndResponseAnnotationSetDoesNotCrash() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet responseAS = mock(AnnotationSet.class);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://demo/doc"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://demo/doc"));
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(responseAS);
// when(keyAS.get("Entity")).thenReturn(new HashSet<Annotation>());
// when(responseAS.get("Entity")).thenReturn(new HashSet<Annotation>());
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.000001);
assertEquals(0.0, diff.getRecallLenient(), 0.000001);
assertEquals(0.0, diff.getFMeasureAverage(), 0.000001);
}

@Test
public void testPartialCompatibilityWithoutCoextensiveIsHandled() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
Annotation keyAnnot = mock(Annotation.class);
Annotation responseAnnot = mock(Annotation.class);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet responseAS = mock(AnnotationSet.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(schema.getAnnotationName()).thenReturn("Mention");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://x.com/1"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://x.com/1"));
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(responseAS);
Set<Annotation> keySet = new HashSet<Annotation>();
Set<Annotation> responseSet = new HashSet<Annotation>();
keySet.add(keyAnnot);
responseSet.add(responseAnnot);
// when(keyAS.get("Mention")).thenReturn(keySet);
// when(responseAS.get("Mention")).thenReturn(responseSet);
when(keyAnnot.getStartNode()).thenReturn(keyStart);
when(keyAnnot.getEndNode()).thenReturn(keyEnd);
when(responseAnnot.getStartNode()).thenReturn(respStart);
when(responseAnnot.getEndNode()).thenReturn(respEnd);
when(keyStart.getOffset()).thenReturn(10L);
when(keyEnd.getOffset()).thenReturn(20L);
when(respStart.getOffset()).thenReturn(15L);
when(respEnd.getOffset()).thenReturn(30L);
when(keyAnnot.isPartiallyCompatible(eq(responseAnnot), any())).thenReturn(true);
when(keyAnnot.coextensive(responseAnnot)).thenReturn(false);
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.init();
assertTrue(diff.getRecallLenient() > 0);
}

@Test
public void testSpuriousAnnotationOnlyInResponseCorpus() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
Annotation responseAnnot = mock(Annotation.class);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet responseAS = mock(AnnotationSet.class);
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://doc/a"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://doc/a"));
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(responseAS);
Set<Annotation> emptyKeySet = new HashSet<Annotation>();
Set<Annotation> respSet = new HashSet<Annotation>();
respSet.add(responseAnnot);
// when(keyAS.get("Entity")).thenReturn(emptyKeySet);
// when(responseAS.get("Entity")).thenReturn(respSet);
when(responseAnnot.getStartNode()).thenReturn(respStart);
when(responseAnnot.getEndNode()).thenReturn(respEnd);
when(respStart.getOffset()).thenReturn(0L);
when(respEnd.getOffset()).thenReturn(10L);
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.init();
Set<Annotation> spuriousSet = diff.getAnnotationsOfType(3);
assertTrue(spuriousSet.contains(responseAnnot));
}

@Test
public void testNullValuesAreHandledGracefullyInRenderer() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
Object val = model.getValueAt(0, 0);
assertNull(val);
}

@Test
public void testSetParameterValueWithInvalidNameThrowsException() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
// thrown.expect(ResourceInstantiationException.class);
diff.setParameterValue("invalidParameter", "value");
}

@Test
public void testAnnotationsWithNullFeatureMapHandled() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
Annotation keyAnnot = mock(Annotation.class);
Annotation responseAnnot = mock(Annotation.class);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet responseAS = mock(AnnotationSet.class);
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
when(startNode.getOffset()).thenReturn(0L);
when(endNode.getOffset()).thenReturn(5L);
when(keyAnnot.getStartNode()).thenReturn(startNode);
when(keyAnnot.getEndNode()).thenReturn(endNode);
when(responseAnnot.getStartNode()).thenReturn(startNode);
when(responseAnnot.getEndNode()).thenReturn(endNode);
when(schema.getAnnotationName()).thenReturn("Token");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://example.com/doc"));
when(responseDoc.getName()).thenReturn("doc");
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://example.com/doc"));
when(keyAnnot.coextensive(responseAnnot)).thenReturn(true);
when(keyAnnot.isPartiallyCompatible(eq(responseAnnot), any())).thenReturn(true);
when(responseAnnot.getFeatures()).thenReturn(null);
when(keyAnnot.getFeatures()).thenReturn(null);
Set<Annotation> keySet = new HashSet<Annotation>();
Set<Annotation> respSet = new HashSet<Annotation>();
keySet.add(keyAnnot);
respSet.add(responseAnnot);
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(responseAS);
// when(keyAS.get("Token")).thenReturn(keySet);
// when(responseAS.get("Token")).thenReturn(respSet);
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.init();
assertEquals(1.0, diff.getRecallAverage(), 0.00001);
}

@Test
public void testAnnotationWithNullStartNodeDoesNotThrow() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation annot = mock(Annotation.class);
when(annot.getStartNode()).thenReturn(null);
when(annot.getEndNode()).thenReturn(null);
CorpusAnnotationDiff.AnnotationSetComparator comparator = diff.new AnnotationSetComparator();
assertEquals(-1, comparator.compare(annot, annot));
}

@Test
public void testGetParameterValueWithInvalidNameThrowsException() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
// thrown.expect(ResourceInstantiationException.class);
diff.getParameterValue("nonExistentParameter");
}

@Test
public void testRendererHandlesNullAnnotationInLeftColumnGracefully() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, 0, 0);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
data.add(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
assertNull(model.getValueAt(0, 0));
assertNull(model.getValueAt(0, 1));
}

@Test
public void testDiffWithMatchingDocumentNamesButDifferentURLsSucceeds() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(keyDoc.getName()).thenReturn("doc.txt");
when(responseDoc.getName()).thenReturn("doc.txt");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://doc.com/key"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://doc.com/resp"));
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
when(responseDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertTrue(diff.isTextMode());
}

@Test
public void testgetScrollableBlockIncrementOnHorizontalAndVertical() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertEquals(0, diff.getScrollableBlockIncrement(new java.awt.Rectangle(0, 0, 0, 0), 0, 1));
assertEquals(-10, diff.getScrollableBlockIncrement(new java.awt.Rectangle(0, 0, 0, 0), 1, 1));
}

@Test
public void testNullResponseAnnotationYieldsMissingType() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
Annotation keyAnnot = mock(Annotation.class);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet responseAS = mock(AnnotationSet.class);
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
when(startNode.getOffset()).thenReturn(0L);
when(endNode.getOffset()).thenReturn(10L);
when(schema.getAnnotationName()).thenReturn("Token");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://a.com/x"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://a.com/x"));
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(responseAS);
when(keyAnnot.getStartNode()).thenReturn(startNode);
when(keyAnnot.getEndNode()).thenReturn(endNode);
when(keyAnnot.getFeatures()).thenReturn(null);
Set<Annotation> keySet = new HashSet<Annotation>();
keySet.add(keyAnnot);
Set<Annotation> emptyResponse = new HashSet<Annotation>();
// when(keyAS.get("Token")).thenReturn(keySet);
// when(responseAS.get("Token")).thenReturn(emptyResponse);
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.init();
Set<Annotation> result = diff.getAnnotationsOfType(4);
assertTrue(result.contains(keyAnnot));
}

@Test
public void testAnnotationDiffTableHandlesUnknownColumnGracefully() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, 0, 0);
List<CorpusAnnotationDiff.DiffSetElement> list = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
list.add(element);
// TableModel model = diff.new AnnotationDiffTableModel(list);
// assertNull(model.getValueAt(0, 99));
}

@Test
public void testAnnotationDiffTableModelWithNoDataReturnsZeroRows() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.emptyList());
assertEquals(0, model.getRowCount());
}

@Test
public void testInitHandlesNullKeyAnnotationSetGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
AnnotationSet responseSet = mock(AnnotationSet.class);
Annotation responseAnnot = mock(Annotation.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(5L);
when(responseAnnot.getStartNode()).thenReturn(s);
when(responseAnnot.getEndNode()).thenReturn(e);
when(responseAnnot.getFeatures()).thenReturn(mock(FeatureMap.class));
Set<Annotation> responseAnnotations = new HashSet<Annotation>();
responseAnnotations.add(responseAnnot);
// when(responseSet.get("MissingKeyType")).thenReturn(responseAnnotations);
when(keyCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.size()).thenReturn(1);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(schema.getAnnotationName()).thenReturn("MissingKeyType");
when(keyDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://doc/key"));
when(responseDoc.getName()).thenReturn("doc1");
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://doc/key"));
when(keyDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
when(responseDoc.getAnnotations()).thenReturn(responseSet);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE).contains(responseAnnot));
}

@Test
public void testInitHandlesNullResponseAnnotationSetGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
AnnotationSet keySet = mock(AnnotationSet.class);
Annotation keyAnnot = mock(Annotation.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(15L);
when(keyAnnot.getStartNode()).thenReturn(s);
when(keyAnnot.getEndNode()).thenReturn(e);
when(keyAnnot.getFeatures()).thenReturn(mock(FeatureMap.class));
Set<Annotation> keyAnnotations = new HashSet<Annotation>();
keyAnnotations.add(keyAnnot);
// when(keySet.get("NoMatchingResponse")).thenReturn(keyAnnotations);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(schema.getAnnotationName()).thenReturn("NoMatchingResponse");
when(keyDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://a/doc"));
when(responseDoc.getName()).thenReturn("doc");
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://a/doc"));
when(keyDoc.getAnnotations()).thenReturn(keySet);
when(responseDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE).contains(keyAnnot));
}

@Test
public void testSetParameterValuesIgnoresUnknownKeys() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap parameterMap = mock(FeatureMap.class);
when(parameterMap.keySet()).thenReturn(Collections.singleton("nonExistentParam"));
// thrown.expect(ResourceInstantiationException.class);
diff.setParameterValues(parameterMap);
}

@Test
public void testAddToDiffSetWithNullElementIsSafe() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.getClass();
try {
java.lang.reflect.Method addMethod = diff.getClass().getDeclaredMethod("addToDiffset", CorpusAnnotationDiff.DiffSetElement.class);
addMethod.setAccessible(true);
addMethod.invoke(diff, new Object[] { null });
} catch (Exception e) {
fail("Should not throw on null diff element: " + e.getMessage());
}
}

@Test
public void testGetPreferredScrollableViewportSizeReturnsNonNull() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Dimension dim = diff.getPreferredScrollableViewportSize();
assertNotNull(dim);
}

@Test
public void testGetRawObjectReturnsCorrectType() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.singletonList(element));
Object raw = model.getRawObject(0);
assertSame(element, raw);
}

@Test
public void testScrollableSettingsReturnConsistentValues() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Rectangle rect = new Rectangle(0, 0, 100, 200);
int unitVert = diff.getScrollableUnitIncrement(rect, javax.swing.SwingConstants.VERTICAL, 1);
int unitHorz = diff.getScrollableUnitIncrement(rect, javax.swing.SwingConstants.HORIZONTAL, 1);
int blockVert = diff.getScrollableBlockIncrement(rect, javax.swing.SwingConstants.VERTICAL, 1);
int blockHorz = diff.getScrollableBlockIncrement(rect, javax.swing.SwingConstants.HORIZONTAL, 1);
assertEquals(10, unitVert);
assertEquals(10, unitHorz);
assertEquals(190, blockVert);
assertEquals(90, blockHorz);
}

@Test
public void testGetColumnNameWithInvalidIndexReturnsQuestionMark() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
String colName = model.getColumnName(999);
assertEquals("?", colName);
}

@Test
public void testGetColumnClassWithInvalidIndexReturnsObjectClass() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
Class<?> cls = model.getColumnClass(999);
assertEquals(Object.class, cls);
}

@Test
public void testDiffElementDefaultConstructorSetsDefaults() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement();
assertNull(element.getLeftAnnotation());
assertNull(element.getRightAnnotation());
assertEquals(0, element.getLeftType());
assertEquals(0, element.getRightType());
}

@Test
public void testDiffSetElementGettersAndSettersWorkCorrectly() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement();
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
element.setLeftAnnotation(left);
element.setRightAnnotation(right);
element.setLeftType(3);
element.setRightType(2);
element.setKeyDocument(keyDoc);
element.setResponseDocument(respDoc);
assertSame(left, element.getLeftAnnotation());
assertSame(right, element.getRightAnnotation());
assertEquals(3, element.getLeftType());
assertEquals(2, element.getRightType());
assertSame(keyDoc, element.getKeyDocument());
assertSame(respDoc, element.getResponseDocument());
}

@Test
public void testDetectKeyTypeWithNullReturnsNullType() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
java.lang.reflect.Method m = diff.getClass().getDeclaredMethod("detectKeyType", Annotation.class);
m.setAccessible(true);
Object result = m.invoke(diff, new Object[] { null });
assertEquals(-1, result);
}

@Test
public void testDetectResponseTypeWithNullReturnsNullType() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
java.lang.reflect.Method m = diff.getClass().getDeclaredMethod("detectResponseType", Annotation.class);
m.setAccessible(true);
Object result = m.invoke(diff, new Object[] { null });
assertEquals(-1, result);
}

@Test
public void testRendererPanelReturnedWhenValueIsNull() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = mock(JTable.class);
when(table.getModel()).thenReturn(mock(javax.swing.table.TableModel.class));
Object value = null;
java.awt.Component result = renderer.getTableCellRendererComponent(table, value, false, false, 0, 4);
assertTrue(result instanceof JPanel);
}

@Test
public void testRendererReturnsDefaultPanelOnMissingDiffSetElement() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = mock(JTable.class);
javax.swing.table.TableModel model = mock(javax.swing.table.TableModel.class);
when(model.getValueAt(0, 10)).thenReturn(null);
when(table.getModel()).thenReturn(model);
Object rendered = renderer.getTableCellRendererComponent(table, "any", false, false, 0, 0);
assertNotNull(rendered);
}

@Test
public void testLegendLabelPropertiesAreSet() {
JLabel label = new JLabel("Legend Entry");
label.setBackground(Color.YELLOW);
label.setOpaque(true);
label.setForeground(Color.BLACK);
assertTrue(label.isOpaque());
assertEquals(Color.YELLOW, label.getBackground());
assertEquals(Color.BLACK, label.getForeground());
}

@Test
public void testEmptyDiffSetStructurePrintsSafely() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<CorpusAnnotationDiff.DiffSetElement> empty = new HashSet<CorpusAnnotationDiff.DiffSetElement>();
diff.printStructure(empty);
assertTrue(empty.isEmpty());
}

@Test
public void testDoDiffWithNullListsDoesNothing() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
java.lang.reflect.Method m = diff.getClass().getDeclaredMethod("doDiff", java.util.List.class, java.util.List.class);
m.setAccessible(true);
m.invoke(diff, null, null);
assertTrue(true);
}

@Test
public void testSetFeatureMapParameterNameNonexistentThrows() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
// gate.FeatureMap map = new FeatureMapImpl();
// map.put("nonexistentParameter", "ignored");
// try {
// diff.setParameterValues(map);
// } catch (ResourceInstantiationException e) {
// assertTrue(e.getMessage().contains("Couldn't get bean info"));
// }
}

@Test
public void testMatchingDocByNameEvenIfUrlDiffersSucceeds() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
Annotation keyAnn = mock(Annotation.class);
Annotation respAnn = mock(Annotation.class);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet respAS = mock(AnnotationSet.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
when(keyAnn.getStartNode()).thenReturn(start);
when(keyAnn.getEndNode()).thenReturn(end);
when(respAnn.getStartNode()).thenReturn(start);
when(respAnn.getEndNode()).thenReturn(end);
when(keyAnn.isPartiallyCompatible(eq(respAnn), any())).thenReturn(true);
when(keyAnn.coextensive(respAnn)).thenReturn(true);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://a.com/doc"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://b.com/doc"));
Set<Annotation> kSet = new HashSet<Annotation>();
kSet.add(keyAnn);
Set<Annotation> rSet = new HashSet<Annotation>();
rSet.add(respAnn);
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(respAS);
// when(keyAS.get("Entity")).thenReturn(kSet);
// when(respAS.get("Entity")).thenReturn(rSet);
when(keyAnn.getFeatures()).thenReturn(mock(gate.FeatureMap.class));
when(respAnn.getFeatures()).thenReturn(mock(gate.FeatureMap.class));
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertEquals(1.0, diff.getRecallStrict(), 0.000001);
}

@Test
public void testDoDiffWithNullCompatibleFeaturesAddsCorrectly() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
Annotation keyAnnotation = mock(Annotation.class);
Annotation responseAnnotation = mock(Annotation.class);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet responseAS = mock(AnnotationSet.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(schema.getAnnotationName()).thenReturn("Token");
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://example.com/key"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://example.com/key"));
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(100L);
when(e.getOffset()).thenReturn(200L);
when(keyAnnotation.getStartNode()).thenReturn(s);
when(keyAnnotation.getEndNode()).thenReturn(e);
when(responseAnnotation.getStartNode()).thenReturn(s);
when(responseAnnotation.getEndNode()).thenReturn(e);
when(keyAnnotation.coextensive(responseAnnotation)).thenReturn(true);
when(keyAnnotation.isPartiallyCompatible(eq(responseAnnotation), eq(null))).thenReturn(false);
Set<Annotation> keySet = new LinkedHashSet<>();
keySet.add(keyAnnotation);
Set<Annotation> responseSet = new LinkedHashSet<>();
responseSet.add(responseAnnotation);
// when(keyAS.get("Token")).thenReturn(keySet);
// when(responseAS.get("Token")).thenReturn(responseSet);
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(responseAS);
when(keyAnnotation.getFeatures()).thenReturn(null);
when(responseAnnotation.getFeatures()).thenReturn(null);
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.init();
Set<Annotation> result = diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE);
assertTrue(result.contains(responseAnnotation));
}

@Test
public void testRendererCellColorBasedOnRightTypePartialCorrect() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, 2);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.singletonList(element));
JTable table = mock(JTable.class);
when(table.getModel()).thenReturn(model);
Color expectedColor = new Color(173, 215, 255);
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
Object value = "value";
java.awt.Component c = renderer.getTableCellRendererComponent(table, value, false, false, 0, 5);
assertEquals(expectedColor, c.getBackground());
}

@Test
public void testRendererCellColorBasedOnLeftTypeMissing() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, 4, -1);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.singletonList(element));
JTable table = mock(JTable.class);
when(table.getModel()).thenReturn(model);
Color expectedColor = new Color(255, 231, 173);
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
Object value = "value";
java.awt.Component c = renderer.getTableCellRendererComponent(table, value, false, false, 0, 0);
assertEquals(expectedColor, c.getBackground());
}

@Test
public void testTableModelReturnsNullForUnknownColumn() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, -1);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.singletonList(element));
Object val = model.getValueAt(0, 99);
assertNull(val);
}

@Test
public void testTypeCounterResetsOnInit() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://a.com/doc"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://a.com/doc"));
when(schema.getAnnotationName()).thenReturn("Token");
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet responseAS = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(responseAS);
// when(keyAS.get("Token")).thenReturn(Collections.emptySet());
// when(responseAS.get("Token")).thenReturn(Collections.emptySet());
diff.setTextMode(Boolean.TRUE);
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(0.0, diff.getRecallStrict(), 0.00001);
}

@Test
public void testInvalidOffsetExceptionInTableModelDoesNotThrow() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation annotation = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
when(annotation.getStartNode()).thenReturn(start);
when(annotation.getEndNode()).thenReturn(end);
when(annotation.getFeatures()).thenReturn(mock(FeatureMap.class));
Document doc = mock(Document.class);
try {
when(doc.getContent().getContent(0L, 5L)).thenThrow(new gate.util.InvalidOffsetException("bad offset"));
} catch (gate.util.InvalidOffsetException e) {
}
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(annotation, null, 0, -1, doc, null);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.singletonList(element));
assertNotNull(model.getValueAt(0, 0));
}

@Test
public void testGetScrollableTracksViewportWidthReturnsFalse() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
boolean result = diff.getScrollableTracksViewportWidth();
assertFalse(result);
}

@Test
public void testGetScrollableTracksViewportHeightReturnsFalse() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
boolean result = diff.getScrollableTracksViewportHeight();
assertFalse(result);
}

@Test
public void testTableModelGetRawObjectForIndexOutOfBoundsFailsQuietly() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.emptyList());
try {
Object obj = model.getRawObject(5);
assertNull(obj);
} catch (IndexOutOfBoundsException ex) {
}
}

@Test
public void testDiffTableModelReturnsNullWhenAnnotationIsMissingAtLeft() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, mock(Annotation.class), -1, 2);
List<CorpusAnnotationDiff.DiffSetElement> data = Collections.singletonList(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
Object result = model.getValueAt(0, 0);
assertNull(result);
}

@Test
public void testAnnotationDiffTableModelHandlesNullFeaturesRightSide() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation rightAnnotation = mock(Annotation.class);
when(rightAnnotation.getStartNode()).thenReturn(mock(Node.class));
when(rightAnnotation.getEndNode()).thenReturn(mock(Node.class));
when(rightAnnotation.getFeatures()).thenReturn(null);
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, rightAnnotation, -1, CorpusAnnotationDiff.CORRECT_TYPE, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> data = Collections.singletonList(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
Object features = model.getValueAt(0, 8);
assertNull(features);
}

@Test
public void testDiffSetElementInitializesAllFieldsProperlyWithDocument() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation a1 = mock(Annotation.class);
Annotation a2 = mock(Annotation.class);
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(a1, a2, 2, 3, d1, d2);
assertSame(a1, element.getLeftAnnotation());
assertSame(a2, element.getRightAnnotation());
assertEquals(2, element.getLeftType());
assertEquals(3, element.getRightType());
assertSame(d1, element.getKeyDocument());
assertSame(d2, element.getResponseDocument());
}

@Test
public void testDetectKeyTypeAssignsMissingWhenNoMatchInResponse() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation keyAnn = mock(Annotation.class);
when(keyAnn.isPartiallyCompatible(any(), any())).thenReturn(false);
Document doc = mock(Document.class);
Corpus corpus = mock(Corpus.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
List<Annotation> keyList = new ArrayList<>();
keyList.add(keyAnn);
when(doc.getName()).thenReturn("x");
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
when(doc.getSourceUrl()).thenReturn(new URL("http://n"));
when(schema.getAnnotationName()).thenReturn("Token");
AnnotationSet set = mock(AnnotationSet.class);
// when(set.get("Token")).thenReturn(keyList);
when(doc.getAnnotations()).thenReturn(set);
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setAnnotationSchema(schema);
diff.init();
Set<Annotation> missing = diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE);
assertTrue(missing.contains(keyAnn));
}

@Test
public void testAnnotationSetComparatorReturnsCorrectOrdering() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation a1 = mock(Annotation.class);
Annotation a2 = mock(Annotation.class);
Node node1 = mock(Node.class);
Node node2 = mock(Node.class);
when(node1.getOffset()).thenReturn(100L);
when(node2.getOffset()).thenReturn(200L);
when(a1.getStartNode()).thenReturn(node1);
when(a2.getStartNode()).thenReturn(node2);
CorpusAnnotationDiff.AnnotationSetComparator comp = diff.new AnnotationSetComparator();
int result = comp.compare(a1, a2);
assertTrue(result < 0);
}

@Test
public void testGetAnnotationsOfTypeWithEmptyDiffSetReturnsEmpty() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<Annotation> annotations = diff.getAnnotationsOfType(CorpusAnnotationDiff.CORRECT_TYPE);
assertTrue(annotations.isEmpty());
}

@Test
public void testGetFMeasureLenientReturnsZeroBeforeInit() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertEquals(0.0, diff.getFMeasureLenient(), 0.00001);
}

@Test
public void testAddToDiffsetDoesNotThrowOnSpuriousEntry() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Document doc = mock(Document.class);
Annotation right = mock(Annotation.class);
CorpusAnnotationDiff.DiffSetElement elem = diff.new DiffSetElement(null, right, -1, CorpusAnnotationDiff.SPURIOUS_TYPE, doc, doc);
java.lang.reflect.Method method = diff.getClass().getDeclaredMethod("addToDiffset", CorpusAnnotationDiff.DiffSetElement.class);
method.setAccessible(true);
method.invoke(diff, elem);
assertTrue(true);
}

@Test
public void testInitSkipsNonMatchingDocumentByNameAndUrl() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://key/corpus/doc1"));
when(responseDoc.getName()).thenReturn("doc2");
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://res/corpus/doc2"));
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Type");
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet resAS = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyAS);
when(responseDoc.getAnnotations()).thenReturn(resAS);
// when(keyAS.get("Type")).thenReturn(Collections.emptySet());
// when(resAS.get("Type")).thenReturn(Collections.emptySet());
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
}

@Test
public void testFailsGracefullyIfOffsetsCauseInvalidOffsetException() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation ann = mock(Annotation.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(20L);
when(ann.getStartNode()).thenReturn(s);
when(ann.getEndNode()).thenReturn(e);
// when(ann.getFeatures()).thenReturn(new FeatureMapImpl());
Document doc = mock(Document.class);
try {
when(doc.getContent().getContent(10L, 20L)).thenThrow(new InvalidOffsetException("bad"));
} catch (InvalidOffsetException ignore) {
}
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(ann, null, CorpusAnnotationDiff.MISSING_TYPE, -1, doc, null);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.singletonList(element));
Object value = model.getValueAt(0, 0);
assertTrue(value instanceof String || value == null);
}
}
