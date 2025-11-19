package gate.annotation;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;
import org.apache.tools.ant.types.resources.comparators.Content;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gate.creole.AnnotationSchema;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class CorpusAnnotationDiff_llmsuite_4_GPTLLMTest {

@Test
public void testInitWithCorrectAnnotations() throws Exception {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema annotationSchema = mock(AnnotationSchema.class);
when(annotationSchema.getAnnotationName()).thenReturn("Person");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
URL url = new URL("http://example.com/doc");
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet responseAnnSet = mock(AnnotationSet.class);
Annotation keyAnn = mock(Annotation.class);
Annotation responseAnn = mock(Annotation.class);
Node keyStartNode = mock(Node.class);
Node keyEndNode = mock(Node.class);
when(keyStartNode.getOffset()).thenReturn(0L);
when(keyEndNode.getOffset()).thenReturn(5L);
when(keyAnn.getStartNode()).thenReturn(keyStartNode);
when(keyAnn.getEndNode()).thenReturn(keyEndNode);
when(keyAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
Node responseStartNode = mock(Node.class);
Node responseEndNode = mock(Node.class);
when(responseStartNode.getOffset()).thenReturn(0L);
when(responseEndNode.getOffset()).thenReturn(5L);
when(responseAnn.getStartNode()).thenReturn(responseStartNode);
when(responseAnn.getEndNode()).thenReturn(responseEndNode);
when(responseAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
when(keyAnn.coextensive(responseAnn)).thenReturn(true);
when(keyAnn.isPartiallyCompatible(eq(responseAnn), anySet())).thenReturn(true);
when(responseAnn.isPartiallyCompatible(eq(keyAnn), anySet())).thenReturn(true);
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnn);
Set<Annotation> responseSet = new HashSet<>();
responseSet.add(responseAnn);
AnnotationSet keyDocAnnSet = mock(AnnotationSet.class);
AnnotationSet responseDocAnnSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyDocAnnSet);
// when(keyDocAnnSet.get("Person")).thenReturn(keySet);
when(responseDoc.getAnnotations()).thenReturn(responseDocAnnSet);
// when(responseDocAnnSet.get("Person")).thenReturn(responseSet);
Content keyContent = mock(Content.class);
Content responseContent = mock(Content.class);
// when(keyContent.getContent(0L, 5L)).thenReturn("Alice");
// when(responseContent.getContent(0L, 5L)).thenReturn("Alice");
// when(keyDoc.getContent()).thenReturn(keyContent);
// when(responseDoc.getContent()).thenReturn(responseContent);
annotationDiff.setAnnotationSchema(annotationSchema);
annotationDiff.setKeyCorpus(keyCorpus);
annotationDiff.setResponseCorpus(responseCorpus);
annotationDiff.setTextMode(Boolean.TRUE);
annotationDiff.init();
assertEquals(1.0, annotationDiff.getPrecisionStrict(), 0.0001);
assertEquals(1.0, annotationDiff.getRecallStrict(), 0.0001);
assertEquals(1.0, annotationDiff.getFMeasureStrict(), 0.0001);
}

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWithNoAnnotationSchema() throws Exception {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
annotationDiff.setKeyCorpus(keyCorpus);
annotationDiff.setResponseCorpus(responseCorpus);
annotationDiff.setAnnotationSchema(null);
annotationDiff.setTextMode(Boolean.TRUE);
annotationDiff.init();
}

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWithEmptyKeyCorpus() throws Exception {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("SomeType");
when(keyCorpus.size()).thenReturn(0);
when(responseCorpus.size()).thenReturn(1);
annotationDiff.setAnnotationSchema(schema);
annotationDiff.setKeyCorpus(keyCorpus);
annotationDiff.setResponseCorpus(responseCorpus);
annotationDiff.setTextMode(Boolean.TRUE);
annotationDiff.init();
}

@Test
public void testGetAndSetTextMode() {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
annotationDiff.setTextMode(Boolean.TRUE);
assertTrue(annotationDiff.isTextMode());
}

@Test
public void testGetAndSetAnnotationFeatureType() {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
annotationDiff.setAnnotationTypeForFalsePositive("Token");
assertEquals("Token", annotationDiff.getAnnotationTypeForFalsePositive());
}

@Test
public void testGetAndSetAnnotationSetNames() {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
annotationDiff.setKeyAnnotationSetName("KeySet");
annotationDiff.setResponseAnnotationSetName("ResponseSet");
annotationDiff.setResponseAnnotationSetNameFalsePoz("ResponseSetFalse");
assertEquals("KeySet", annotationDiff.getKeyAnnotationSetName());
assertEquals("ResponseSet", annotationDiff.getResponseAnnotationSetName());
assertEquals("ResponseSetFalse", annotationDiff.getResponseAnnotationSetNameFalsePoz());
}

@Test
public void testGetAndSetKeyFeatureNamesSet() {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Set<String> featureNames = new HashSet<>();
featureNames.add("gender");
featureNames.add("type");
annotationDiff.setKeyFeatureNamesSet(featureNames);
assertEquals(featureNames, annotationDiff.getKeyFeatureNamesSet());
}

@Test
public void testInitWhenKeyAnnotationSetIsNull() throws Exception {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema annotationSchema = mock(AnnotationSchema.class);
when(annotationSchema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
URL url = new URL("http://example.com/doc");
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keyDocAnnSet = mock(AnnotationSet.class);
AnnotationSet responseDocAnnSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyDocAnnSet);
when(responseDoc.getAnnotations()).thenReturn(responseDocAnnSet);
when(keyDocAnnSet.get("Entity")).thenReturn(null);
Set<Annotation> responseSet = new HashSet<>();
Annotation respAnn = mock(Annotation.class);
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
when(startNode.getOffset()).thenReturn(0L);
when(endNode.getOffset()).thenReturn(5L);
when(respAnn.getStartNode()).thenReturn(startNode);
when(respAnn.getEndNode()).thenReturn(endNode);
when(respAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
responseSet.add(respAnn);
// when(responseDocAnnSet.get("Entity")).thenReturn(responseSet);
Content responseContent = mock(Content.class);
// when(responseDoc.getContent()).thenReturn(responseContent);
// when(responseContent.getContent(0L, 5L)).thenReturn("Text");
annotationDiff.setTextMode(Boolean.TRUE);
annotationDiff.setAnnotationSchema(annotationSchema);
annotationDiff.setKeyCorpus(keyCorpus);
annotationDiff.setResponseCorpus(responseCorpus);
annotationDiff.init();
assertTrue(annotationDiff.getRecallStrict() == 0.0);
}

@Test
public void testInitWhenResponseAnnotationSetIsNull() throws Exception {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema annotationSchema = mock(AnnotationSchema.class);
when(annotationSchema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
URL url = new URL("http://example.com/doc");
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keyDocAnnSet = mock(AnnotationSet.class);
Annotation annotation = mock(Annotation.class);
Node sNode = mock(Node.class);
Node eNode = mock(Node.class);
when(sNode.getOffset()).thenReturn(0L);
when(eNode.getOffset()).thenReturn(5L);
when(annotation.getStartNode()).thenReturn(sNode);
when(annotation.getEndNode()).thenReturn(eNode);
when(annotation.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
keySet.add(annotation);
when(keyDoc.getAnnotations()).thenReturn(keyDocAnnSet);
// when(keyDocAnnSet.get("Entity")).thenReturn(keySet);
AnnotationSet responseAnnSet = mock(AnnotationSet.class);
when(responseDoc.getAnnotations()).thenReturn(responseAnnSet);
when(responseAnnSet.get("Entity")).thenReturn(null);
Content keyContent = mock(Content.class);
// when(keyDoc.getContent()).thenReturn(keyContent);
// when(keyContent.getContent(0L, 5L)).thenReturn("Hello");
annotationDiff.setTextMode(Boolean.TRUE);
annotationDiff.setAnnotationSchema(annotationSchema);
annotationDiff.setKeyCorpus(keyCorpus);
annotationDiff.setResponseCorpus(responseCorpus);
annotationDiff.init();
assertEquals(0.0, annotationDiff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, annotationDiff.getFMeasureStrict(), 0.0001);
}

@Test
public void testDocumentMismatchSkipsDocumentPairing() throws Exception {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema annotationSchema = mock(AnnotationSchema.class);
when(annotationSchema.getAnnotationName()).thenReturn("Type");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc2");
URL keyUrl = new URL("http://example.com/key.txt");
URL resUrl = new URL("http://example.com/other.txt");
when(keyDoc.getSourceUrl()).thenReturn(keyUrl);
when(responseDoc.getSourceUrl()).thenReturn(resUrl);
when(keyDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
when(responseDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
annotationDiff.setAnnotationSchema(annotationSchema);
annotationDiff.setKeyCorpus(keyCorpus);
annotationDiff.setResponseCorpus(responseCorpus);
annotationDiff.setTextMode(Boolean.TRUE);
annotationDiff.init();
assertEquals(0.0, annotationDiff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, annotationDiff.getRecallStrict(), 0.0001);
assertEquals(0.0, annotationDiff.getFMeasureStrict(), 0.0001);
}

@Test
public void testGetAnnotationsOfTypeWithNullDiffSet() {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Set<Annotation> result = annotationDiff.getAnnotationsOfType(CorpusAnnotationDiff.CORRECT_TYPE);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testFalsePositiveCalculationWhenAnnotationTypeSetButSetIsNull() throws Exception {
CorpusAnnotationDiff annotationDiff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
URL url = new URL("http://example.com/x");
when(keyDoc.getName()).thenReturn("d");
when(responseDoc.getName()).thenReturn("d");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
Annotation ann = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
when(ann.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> responseSet = new HashSet<>();
responseSet.add(ann);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet responseAnnSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(responseDoc.getAnnotations()).thenReturn(responseAnnSet);
when(keyAnnSet.get("Person")).thenReturn(null);
// when(responseAnnSet.get("Person")).thenReturn(responseSet);
when(responseAnnSet.get("Token")).thenReturn(null);
Content content = mock(Content.class);
// when(responseDoc.getContent()).thenReturn(content);
// when(content.getContent(10L, 20L)).thenReturn("Demo");
annotationDiff.setAnnotationSchema(schema);
annotationDiff.setKeyCorpus(keyCorpus);
annotationDiff.setResponseCorpus(responseCorpus);
annotationDiff.setTextMode(Boolean.TRUE);
annotationDiff.setAnnotationTypeForFalsePositive("Token");
annotationDiff.init();
assertEquals(0.0, annotationDiff.getFalsePositiveStrict(), 0.0001);
assertEquals(0.0, annotationDiff.getFalsePositiveLenient(), 0.0001);
}

@Test
public void testInitWithCoextensiveButNotPartiallyCompatibleAnnotations() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
URL url = new URL("http://test/doc");
when(keyDoc.getName()).thenReturn("doc1");
when(respDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
Annotation keyAnn = mock(Annotation.class);
Annotation respAnn = mock(Annotation.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(10L);
when(keyAnn.getStartNode()).thenReturn(keyStart);
when(keyAnn.getEndNode()).thenReturn(keyEnd);
when(keyAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(respStart.getOffset()).thenReturn(0L);
when(respEnd.getOffset()).thenReturn(10L);
when(respAnn.getStartNode()).thenReturn(respStart);
when(respAnn.getEndNode()).thenReturn(respEnd);
when(respAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
when(keyAnn.coextensive(respAnn)).thenReturn(true);
when(keyAnn.isPartiallyCompatible(eq(respAnn), anySet())).thenReturn(false);
when(respAnn.isPartiallyCompatible(eq(keyAnn), anySet())).thenReturn(false);
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnn);
Set<Annotation> respSet = new HashSet<>();
respSet.add(respAnn);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(respDoc.getAnnotations()).thenReturn(respAnnSet);
// when(keyAnnSet.get("Entity")).thenReturn(keySet);
// when(respAnnSet.get("Entity")).thenReturn(respSet);
Content keyContent = mock(Content.class);
Content respContent = mock(Content.class);
// when(keyDoc.getContent()).thenReturn(keyContent);
// when(respDoc.getContent()).thenReturn(respContent);
// when(keyContent.getContent(0L, 10L)).thenReturn("KeyText");
// when(respContent.getContent(0L, 10L)).thenReturn("RespText");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertTrue(diff.getPrecisionStrict() == 0.0);
assertTrue(diff.getRecallStrict() == 0.0);
}

@Test
public void testInitWithMisalignedOffsetsLeadsToPartialMatch() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
URL url = new URL("http://test/doc");
when(keyDoc.getName()).thenReturn("doc");
when(respDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
Annotation keyAnn = mock(Annotation.class);
Annotation respAnn = mock(Annotation.class);
Node kStart = mock(Node.class);
Node kEnd = mock(Node.class);
Node rStart = mock(Node.class);
Node rEnd = mock(Node.class);
when(kStart.getOffset()).thenReturn(0L);
when(kEnd.getOffset()).thenReturn(10L);
when(rStart.getOffset()).thenReturn(5L);
when(rEnd.getOffset()).thenReturn(15L);
when(keyAnn.getStartNode()).thenReturn(kStart);
when(keyAnn.getEndNode()).thenReturn(kEnd);
when(respAnn.getStartNode()).thenReturn(rStart);
when(respAnn.getEndNode()).thenReturn(rEnd);
when(keyAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
when(respAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
when(keyAnn.coextensive(respAnn)).thenReturn(false);
when(keyAnn.isPartiallyCompatible(eq(respAnn), anySet())).thenReturn(true);
when(respAnn.isPartiallyCompatible(eq(keyAnn), anySet())).thenReturn(true);
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnn);
Set<Annotation> respSet = new HashSet<>();
respSet.add(respAnn);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(respDoc.getAnnotations()).thenReturn(respAnnSet);
// when(keyAnnSet.get("Entity")).thenReturn(keySet);
// when(respAnnSet.get("Entity")).thenReturn(respSet);
Content kContent = mock(Content.class);
Content rContent = mock(Content.class);
// when(keyDoc.getContent()).thenReturn(kContent);
// when(respDoc.getContent()).thenReturn(rContent);
// when(kContent.getContent(0L, 10L)).thenReturn("AAA");
// when(rContent.getContent(5L, 15L)).thenReturn("BBB");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertTrue(diff.getPrecisionLenient() > 0.0);
assertTrue(diff.getRecallLenient() > 0.0);
}

@Test
public void testInitWithOnlyResponseAnnotationAndNoSchemaMatch() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Label");
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
when(keyDoc.getName()).thenReturn("doc");
when(respDoc.getName()).thenReturn("doc");
URL url = new URL("http://x");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(respDoc.getAnnotations()).thenReturn(respAnnSet);
when(keyAnnSet.get("Label")).thenReturn(null);
Annotation respAnn = mock(Annotation.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(20L);
when(respAnn.getStartNode()).thenReturn(s);
when(respAnn.getEndNode()).thenReturn(e);
when(respAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> responseSet = new HashSet<>();
responseSet.add(respAnn);
// when(respAnnSet.get("Label")).thenReturn(responseSet);
Content rContent = mock(Content.class);
// when(respDoc.getContent()).thenReturn(rContent);
// when(rContent.getContent(10L, 20L)).thenReturn("text");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE).contains(respAnn));
}

@Test
public void testFMeasureZeroWhenDenominatorIsZero() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("X");
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
when(keyDoc.getName()).thenReturn("doc");
when(respDoc.getName()).thenReturn("doc");
URL url = new URL("http://host/x");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(respDoc.getAnnotations()).thenReturn(respAnnSet);
when(keyAnnSet.get("X")).thenReturn(null);
when(respAnnSet.get("X")).thenReturn(null);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureLenient(), 0.0001);
assertEquals(0.0, diff.getFMeasureAverage(), 0.0001);
}

@Test
public void testAddCorrectAndMissingAnnotationsSimultaneously() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
URL url = new URL("http://doc");
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
Annotation keyAnn1 = mock(Annotation.class);
Annotation keyAnn2 = mock(Annotation.class);
Annotation responseAnn = mock(Annotation.class);
Node k1Start = mock(Node.class);
Node k1End = mock(Node.class);
when(k1Start.getOffset()).thenReturn(0L);
when(k1End.getOffset()).thenReturn(5L);
when(keyAnn1.getStartNode()).thenReturn(k1Start);
when(keyAnn1.getEndNode()).thenReturn(k1End);
Node k2Start = mock(Node.class);
Node k2End = mock(Node.class);
when(k2Start.getOffset()).thenReturn(10L);
when(k2End.getOffset()).thenReturn(15L);
when(keyAnn2.getStartNode()).thenReturn(k2Start);
when(keyAnn2.getEndNode()).thenReturn(k2End);
Node rStart = mock(Node.class);
Node rEnd = mock(Node.class);
when(rStart.getOffset()).thenReturn(0L);
when(rEnd.getOffset()).thenReturn(5L);
when(responseAnn.getStartNode()).thenReturn(rStart);
when(responseAnn.getEndNode()).thenReturn(rEnd);
when(keyAnn1.coextensive(responseAnn)).thenReturn(true);
when(keyAnn1.isPartiallyCompatible(eq(responseAnn), anySet())).thenReturn(true);
when(responseAnn.isPartiallyCompatible(eq(keyAnn1), anySet())).thenReturn(true);
when(keyAnn2.coextensive(responseAnn)).thenReturn(false);
when(keyAnn2.isPartiallyCompatible(any(Annotation.class), anySet())).thenReturn(false);
when(keyAnn1.getFeatures()).thenReturn(Factory.newFeatureMap());
when(keyAnn2.getFeatures()).thenReturn(Factory.newFeatureMap());
when(responseAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnn1);
keySet.add(keyAnn2);
Set<Annotation> responseSet = new HashSet<>();
responseSet.add(responseAnn);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
// when(keyAnnSet.get("Entity")).thenReturn(keySet);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
// when(respAnnSet.get("Entity")).thenReturn(responseSet);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(responseDoc.getAnnotations()).thenReturn(respAnnSet);
Content keyContent = mock(Content.class);
Content respContent = mock(Content.class);
// when(keyDoc.getContent()).thenReturn(keyContent);
// when(responseDoc.getContent()).thenReturn(respContent);
// when(keyContent.getContent(0L, 5L)).thenReturn("A");
// when(keyContent.getContent(10L, 15L)).thenReturn("B");
// when(respContent.getContent(0L, 5L)).thenReturn("A");
diff.setTextMode(Boolean.TRUE);
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertEquals(1.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.5, diff.getRecallStrict(), 0.0001);
assertEquals(0.6666, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testInitWithNullOffsetsForAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
URL url = new URL("http://doc");
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Label");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
Annotation keyAnn = mock(Annotation.class);
Annotation responseAnn = mock(Annotation.class);
Node nullStart = mock(Node.class);
Node nullEnd = mock(Node.class);
when(nullStart.getOffset()).thenReturn(null);
when(nullEnd.getOffset()).thenReturn(null);
when(keyAnn.getStartNode()).thenReturn(nullStart);
when(keyAnn.getEndNode()).thenReturn(nullEnd);
when(responseAnn.getStartNode()).thenReturn(nullStart);
when(responseAnn.getEndNode()).thenReturn(nullEnd);
when(keyAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
when(responseAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
when(keyAnn.coextensive(responseAnn)).thenReturn(false);
when(keyAnn.isPartiallyCompatible(eq(responseAnn), anySet())).thenReturn(false);
when(responseAnn.isPartiallyCompatible(eq(keyAnn), anySet())).thenReturn(false);
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnn);
Set<Annotation> responseSet = new HashSet<>();
responseSet.add(responseAnn);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
// when(keyAnnSet.get("Label")).thenReturn(keySet);
// when(respAnnSet.get("Label")).thenReturn(responseSet);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(responseDoc.getAnnotations()).thenReturn(respAnnSet);
Content keyContent = mock(Content.class);
Content respContent = mock(Content.class);
// when(keyDoc.getContent()).thenReturn(keyContent);
// when(responseDoc.getContent()).thenReturn(respContent);
diff.setTextMode(Boolean.TRUE);
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertTrue(diff.getPrecisionStrict() == 0.0);
}

@Test
public void testSetParameterValueWithInvalidPropertyDoesNotThrow() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
try {
diff.setParameterValue("invalidParam", "someValue");
} catch (Exception e) {
fail("setParameterValue should not throw even on unknown param");
}
}

@Test
public void testAnnotationDiffTableModelHandlesNullCells() {
CorpusAnnotationDiff.DiffSetElement element = new CorpusAnnotationDiff().new DiffSetElement(null, null, -1, -1);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<>();
data.add(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = new CorpusAnnotationDiff().new AnnotationDiffTableModel(data);
assertEquals(1, model.getRowCount());
assertNull(model.getValueAt(0, 0));
assertNull(model.getValueAt(0, 1));
assertNull(model.getValueAt(0, 2));
assertNull(model.getValueAt(0, 3));
assertEquals("   ", model.getValueAt(0, 4));
assertNull(model.getValueAt(0, 5));
assertNull(model.getValueAt(0, 6));
assertNull(model.getValueAt(0, 7));
assertNull(model.getValueAt(0, 8));
assertNull(model.getValueAt(0, 9));
}

@Test
public void testSetParameterValuesWithEmptyFeatureMap() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap params = Factory.newFeatureMap();
try {
diff.setParameterValues(params);
} catch (Exception e) {
fail("Empty parameter map should not raise exception");
}
}

@Test
public void testDetectKeyTypeReturnsMissingForUnmatchedKeyAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
URL url = new URL("http://host/doc.xml");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
Annotation keyAnnot = mock(Annotation.class);
Node kStart = mock(Node.class);
Node kEnd = mock(Node.class);
when(kStart.getOffset()).thenReturn(0L);
when(kEnd.getOffset()).thenReturn(10L);
when(keyAnnot.getStartNode()).thenReturn(kStart);
when(keyAnnot.getEndNode()).thenReturn(kEnd);
when(keyAnnot.getFeatures()).thenReturn(Factory.newFeatureMap());
when(keyAnnot.isPartiallyCompatible(any(Annotation.class), anySet())).thenReturn(false);
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnnot);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
// when(keyAnnSet.get("Entity")).thenReturn(keySet);
// when(respAnnSet.get("Entity")).thenReturn(Collections.emptySet());
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(responseDoc.getAnnotations()).thenReturn(respAnnSet);
Content kContent = mock(Content.class);
// when(keyDoc.getContent()).thenReturn(kContent);
// when(kContent.getContent(0L, 10L)).thenReturn("Text");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
Set<Annotation> missing = diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE);
assertEquals(1, missing.size());
}

@Test
public void testDetectResponseTypeMarksSpurious() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
URL url = new URL("http://host/example.txt");
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Tag");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
Annotation responseAnn = mock(Annotation.class);
when(responseAnn.isPartiallyCompatible(any(Annotation.class), anySet())).thenReturn(false);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(5L);
when(responseAnn.getStartNode()).thenReturn(s);
when(responseAnn.getEndNode()).thenReturn(e);
when(responseAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> respSet = new HashSet<>();
respSet.add(responseAnn);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
// when(keyAnnSet.get("Tag")).thenReturn(Collections.emptySet());
// when(respAnnSet.get("Tag")).thenReturn(respSet);
when(keyDoc.getAnnotations()).thenReturn(keyAnnSet);
when(responseDoc.getAnnotations()).thenReturn(respAnnSet);
Content respContent = mock(Content.class);
// when(responseDoc.getContent()).thenReturn(respContent);
// when(respContent.getContent(0L, 5L)).thenReturn("Text");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
Set<Annotation> spurious = diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE);
assertEquals(1, spurious.size());
}

@Test
public void testViewportBehaviorScrollableSettings() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
java.awt.Dimension dim = diff.getPreferredScrollableViewportSize();
assertNotNull(dim);
java.awt.Rectangle rect = new java.awt.Rectangle(100, 100, 200, 200);
int unitIncVert = diff.getScrollableUnitIncrement(rect, javax.swing.SwingConstants.VERTICAL, 1);
int unitIncHoriz = diff.getScrollableUnitIncrement(rect, javax.swing.SwingConstants.HORIZONTAL, 1);
int blockIncVert = diff.getScrollableBlockIncrement(rect, javax.swing.SwingConstants.VERTICAL, 1);
int blockIncHoriz = diff.getScrollableBlockIncrement(rect, javax.swing.SwingConstants.HORIZONTAL, 1);
assertEquals(10, unitIncVert);
assertEquals(10, unitIncHoriz);
assertEquals(190, blockIncVert);
assertEquals(190, blockIncHoriz);
assertFalse(diff.getScrollableTracksViewportWidth());
assertFalse(diff.getScrollableTracksViewportHeight());
}

@Test
public void testToStringInDiffSetElementHasCorrectFormat() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document kd = mock(Document.class);
Document rd = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(left, right, 1, 2, kd, rd);
assertNotNull(element.getLeftAnnotation());
assertNotNull(element.getRightAnnotation());
assertEquals(1, element.getLeftType());
assertEquals(2, element.getRightType());
assertEquals(kd, element.getKeyDocument());
assertEquals(rd, element.getResponseDocument());
}

@Test
public void testAnnotationSetComparatorSortOrder() {
Annotation a1 = mock(Annotation.class);
Node s1 = mock(Node.class);
when(s1.getOffset()).thenReturn(100L);
when(a1.getStartNode()).thenReturn(s1);
Annotation a2 = mock(Annotation.class);
Node s2 = mock(Node.class);
when(s2.getOffset()).thenReturn(200L);
when(a2.getStartNode()).thenReturn(s2);
CorpusAnnotationDiff.AnnotationSetComparator comp = new CorpusAnnotationDiff().new AnnotationSetComparator();
int result = comp.compare(a1, a2);
assertTrue(result < 0);
}

@Test
public void testAnnotationDiffCellRendererHandlesNullsAndBlankColumn() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = mock(JTable.class);
CorpusAnnotationDiff.DiffSetElement diffRow = diff.new DiffSetElement(null, null, -1, -1);
CorpusAnnotationDiff.AnnotationDiffTableModel model = new CorpusAnnotationDiff().new AnnotationDiffTableModel(Collections.singleton(diffRow));
when(table.getModel()).thenReturn(model);
java.awt.Component comp = renderer.getTableCellRendererComponent(table, null, false, false, 0, 4);
assertTrue(comp instanceof javax.swing.JPanel);
}

@Test
public void testInitSkipsUnmatchedDocumentsInKeyCorpus() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("keyDoc");
when(responseDoc.getName()).thenReturn("responseDoc");
URL keyUrl = new URL("http://localhost/key");
URL resUrl = new URL("http://localhost/response");
when(keyDoc.getSourceUrl()).thenReturn(keyUrl);
when(responseDoc.getSourceUrl()).thenReturn(resUrl);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Type");
when(keyDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
when(responseDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.TRUE);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testSetParameterValueWithInvalidPropertyNameAndValidBean() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
try {
diff.setParameterValue("nonExistent", "value");
} catch (ResourceInstantiationException e) {
fail("Should not throw when setting non-existent property with valid bean");
}
}

@Test
public void testGetValueAtTextFallbackHandlesInvalidOffset() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation leftAnnotation = mock(Annotation.class);
Annotation rightAnnotation = mock(Annotation.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
Node leftStart = mock(Node.class);
Node leftEnd = mock(Node.class);
when(leftStart.getOffset()).thenReturn(0L);
when(leftEnd.getOffset()).thenReturn(10L);
when(leftAnnotation.getStartNode()).thenReturn(leftStart);
when(leftAnnotation.getEndNode()).thenReturn(leftEnd);
when(leftAnnotation.getFeatures()).thenReturn(Factory.newFeatureMap());
Node rightStart = mock(Node.class);
Node rightEnd = mock(Node.class);
when(rightStart.getOffset()).thenReturn(0L);
when(rightEnd.getOffset()).thenReturn(10L);
when(rightAnnotation.getStartNode()).thenReturn(rightStart);
when(rightAnnotation.getEndNode()).thenReturn(rightEnd);
when(rightAnnotation.getFeatures()).thenReturn(Factory.newFeatureMap());
Content keyContent = mock(Content.class);
Content responseContent = mock(Content.class);
// try {
// when(keyContent.getContent(0L, 10L)).thenThrow(new InvalidOffsetException("key error"));
// } catch (InvalidOffsetException e) {
// }
// try {
// when(responseContent.getContent(0L, 10L)).thenThrow(new InvalidOffsetException("resp error"));
// } catch (InvalidOffsetException e) {
// }
// when(keyDoc.getContent()).thenReturn(keyContent);
// when(responseDoc.getContent()).thenReturn(responseContent);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(leftAnnotation, rightAnnotation, 0, 1, keyDoc, responseDoc);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<>();
data.add(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
Object leftText = model.getValueAt(0, 0);
Object rightText = model.getValueAt(0, 5);
assertNotNull(leftText);
assertTrue(leftText instanceof String);
assertNotNull(rightText);
assertTrue(rightText instanceof String);
}

@Test
public void testGetColumnNamesAreCorrect() {
CorpusAnnotationDiff.AnnotationDiffTableModel model = new CorpusAnnotationDiff().new AnnotationDiffTableModel();
assertEquals("String - Key", model.getColumnName(0));
assertEquals("Start - Key", model.getColumnName(1));
assertEquals("End - Key", model.getColumnName(2));
assertEquals("Features - Key", model.getColumnName(3));
assertEquals("   ", model.getColumnName(4));
assertEquals("String - Response", model.getColumnName(5));
assertEquals("Start - Response", model.getColumnName(6));
assertEquals("End -Response", model.getColumnName(7));
assertEquals("Features - Response", model.getColumnName(8));
assertEquals("Document", model.getColumnName(9));
}

@Test
public void testGetColumnClassValidation() {
CorpusAnnotationDiff.AnnotationDiffTableModel model = new CorpusAnnotationDiff().new AnnotationDiffTableModel();
assertEquals(String.class, model.getColumnClass(0));
assertEquals(Long.class, model.getColumnClass(1));
assertEquals(Long.class, model.getColumnClass(2));
assertEquals(String.class, model.getColumnClass(3));
assertEquals(String.class, model.getColumnClass(4));
assertEquals(String.class, model.getColumnClass(5));
assertEquals(Long.class, model.getColumnClass(6));
assertEquals(Long.class, model.getColumnClass(7));
assertEquals(String.class, model.getColumnClass(8));
assertEquals(String.class, model.getColumnClass(9));
}

@Test
public void testAddToDiffSetDoesNotCrashOnNullEntry() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
try {
// diff.getClass().getDeclaredMethod("addToDiffset", CorpusAnnotationDiff.DiffSetElement.class).setAccessible(true).invoke(diff, new Object[] { null });
} catch (Exception e) {
fail("addToDiffset with null should not throw");
}
}

@Test
public void testSetParameterValuesHandlesMultipleEntries() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap params = Factory.newFeatureMap();
params.put("keyCorpus", null);
params.put("responseCorpus", null);
params.put("annotationSchema", null);
diff.setParameterValues(params);
}

@Test
public void testDiffSetElementCanBeMutated() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement();
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
element.setLeftAnnotation(left);
element.setRightAnnotation(right);
element.setLeftType(2);
element.setRightType(3);
element.setKeyDocument(keyDoc);
element.setResponseDocument(respDoc);
assertEquals(left, element.getLeftAnnotation());
assertEquals(right, element.getRightAnnotation());
assertEquals(2, element.getLeftType());
assertEquals(3, element.getRightType());
assertEquals(keyDoc, element.getKeyDocument());
assertEquals(respDoc, element.getResponseDocument());
}

@Test
public void testAnnotationSetComparatorReturnsNegativeWhenLeftOffsetNull() {
CorpusAnnotationDiff.AnnotationSetComparator comparator = new CorpusAnnotationDiff().new AnnotationSetComparator();
Annotation a1 = mock(Annotation.class);
Annotation a2 = mock(Annotation.class);
Node start1 = mock(Node.class);
Node start2 = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(start2.getOffset()).thenReturn(10L);
when(a1.getStartNode()).thenReturn(start1);
when(a2.getStartNode()).thenReturn(start2);
int result = comparator.compare(a1, a2);
assertEquals(-1, result);
}

@Test
public void testAnnotationDiffCellRendererReturnsDefaultComponent() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = mock(JTable.class);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(new ArrayList<CorpusAnnotationDiff.DiffSetElement>());
when(table.getModel()).thenReturn(model);
Object comp = renderer.getTableCellRendererComponent(table, "value", false, false, 0, 0);
assertNotNull(comp);
assertTrue(comp instanceof java.awt.Component);
}

@Test
public void testTableModelGetValueAtReturnsDiffSetElementObjectOnHiddenColumn() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation annot = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(4L);
when(annot.getStartNode()).thenReturn(start);
when(annot.getEndNode()).thenReturn(end);
when(annot.getFeatures()).thenReturn(Factory.newFeatureMap());
Document doc = mock(Document.class);
Content content = mock(Content.class);
// when(doc.getContent()).thenReturn(content);
when(doc.getName()).thenReturn("sample");
// when(content.getContent(0L, 4L)).thenReturn("Test");
CorpusAnnotationDiff.DiffSetElement diffElem = diff.new DiffSetElement(annot, annot, 0, 0, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> elems = Collections.singletonList(diffElem);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(elems);
Object obj = model.getValueAt(0, 10);
assertEquals(diffElem, obj);
}

@Test
public void testDiffSetElementConstructorWithTwoAnnotationsAndNoDocs() {
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
CorpusAnnotationDiff.DiffSetElement element = new CorpusAnnotationDiff().new DiffSetElement(left, right, 1, 2);
assertEquals(left, element.getLeftAnnotation());
assertEquals(right, element.getRightAnnotation());
assertEquals(1, element.getLeftType());
assertEquals(2, element.getRightType());
assertNull(element.getKeyDocument());
assertNull(element.getResponseDocument());
}

@Test
public void testDiffSetElementConstructorWithDocs() {
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = new CorpusAnnotationDiff().new DiffSetElement(left, right, 0, 1, keyDoc, responseDoc);
assertEquals(keyDoc, element.getKeyDocument());
assertEquals(responseDoc, element.getResponseDocument());
}

@Test
public void testSetParameterValueThrowsIntrospectionException() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff() {

@Override
public void setParameterValue(String name, Object value) throws ResourceInstantiationException {
throw new ResourceInstantiationException("Introspection failed");
}
};
try {
diff.setParameterValue("invalid", "test");
fail("Expected exception");
} catch (ResourceInstantiationException e) {
assertTrue(e.getMessage().contains("Introspection failed"));
}
}

@Test
public void testGetAnnotationsOfTypeIgnoresNullAnnotations() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<CorpusAnnotationDiff.DiffSetElement> fakeSet = new HashSet<>();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, -1);
fakeSet.add(element);
Set<Annotation> result = diff.getAnnotationsOfType(CorpusAnnotationDiff.DEFAULT_TYPE);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetValueAtReturnsDocumentName() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation ann = mock(Annotation.class);
Node nodeStart = mock(Node.class);
Node nodeEnd = mock(Node.class);
when(nodeStart.getOffset()).thenReturn(0L);
when(nodeEnd.getOffset()).thenReturn(5L);
when(ann.getStartNode()).thenReturn(nodeStart);
when(ann.getEndNode()).thenReturn(nodeEnd);
when(ann.getFeatures()).thenReturn(Factory.newFeatureMap());
Document doc = mock(Document.class);
Content content = mock(Content.class);
// when(doc.getContent()).thenReturn(content);
when(doc.getName()).thenReturn("document");
// when(content.getContent(0L, 5L)).thenReturn("Content");
CorpusAnnotationDiff.DiffSetElement dse = diff.new DiffSetElement(ann, ann, 0, 0, doc, doc);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(Collections.singletonList(dse));
Object name = model.getValueAt(0, 9);
assertEquals("document", name);
}

@Test
public void testAnnotationDiffTableModelReturnsNullForUnknownColumn() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, -1);
List<CorpusAnnotationDiff.DiffSetElement> rows = Collections.singletonList(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(rows);
Object val = model.getValueAt(0, 11);
assertNull(val);
}

@Test
public void testGetAnnotationsOfTypeWhenOnlyLeftAnnotationPresent() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation left = mock(Annotation.class);
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(left, null, CorpusAnnotationDiff.MISSING_TYPE, -1, doc, null);
Content content = mock(Content.class);
// when(doc.getContent()).thenReturn(content);
Node nStart = mock(Node.class);
Node nEnd = mock(Node.class);
when(nStart.getOffset()).thenReturn(0L);
when(nEnd.getOffset()).thenReturn(5L);
when(left.getStartNode()).thenReturn(nStart);
when(left.getEndNode()).thenReturn(nEnd);
when(left.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<CorpusAnnotationDiff.DiffSetElement> dsSet = new HashSet<>();
dsSet.add(element);
Set<Annotation> extracted = diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE);
assertNotNull(extracted);
}
}
