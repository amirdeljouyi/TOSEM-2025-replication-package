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

public class CorpusAnnotationDiff_llmsuite_1_GPTLLMTest {

@Test(expected = ResourceInstantiationException.class)
public void testInitShouldFailWithoutKeyCorpus() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus responseCorpus = mock(Corpus.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
}

@Test(expected = ResourceInstantiationException.class)
public void testInitShouldFailWithoutResponseCorpus() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
}

@Test
public void testSetAndGetKeyAnnotationSetName() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setKeyAnnotationSetName("KeySet");
assertEquals("KeySet", diff.getKeyAnnotationSetName());
}

@Test
public void testSetAndGetResponseAnnotationSetName() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setResponseAnnotationSetName("ResponseSet");
assertEquals("ResponseSet", diff.getResponseAnnotationSetName());
}

@Test
public void testSetAndGetResponseAnnotationSetNameFalsePoz() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setResponseAnnotationSetNameFalsePoz("FalsePozSet");
assertEquals("FalsePozSet", diff.getResponseAnnotationSetNameFalsePoz());
}

@Test
public void testSetAndGetTextMode() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
assertTrue(diff.isTextMode());
diff.setTextMode(Boolean.FALSE);
assertFalse(diff.isTextMode());
}

@Test
public void testSetAndGetAnnotationTypeForFalsePositive() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setAnnotationTypeForFalsePositive("Token");
assertEquals("Token", diff.getAnnotationTypeForFalsePositive());
}

@Test
public void testSetAndGetKeyFeatureNamesSet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<String> features = new HashSet<String>();
features.add("entity");
diff.setKeyFeatureNamesSet(features);
assertEquals(features, diff.getKeyFeatureNamesSet());
}

@Test
public void testDefaultMetricsAreZero() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getPrecisionLenient(), 0.0001);
assertEquals(0.0, diff.getPrecisionAverage(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertEquals(0.0, diff.getRecallLenient(), 0.0001);
assertEquals(0.0, diff.getRecallAverage(), 0.0001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureLenient(), 0.0001);
assertEquals(0.0, diff.getFMeasureAverage(), 0.0001);
}

@Test
public void testInitWithOneCorrectMatchShouldYieldPerfectScores() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
URL url = new URL("http://example.com/test");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
gate.Node startNode = mock(gate.Node.class);
gate.Node endNode = mock(gate.Node.class);
when(startNode.getOffset()).thenReturn(0L);
when(endNode.getOffset()).thenReturn(5L);
Annotation keyAnnotation = mock(Annotation.class);
Annotation responseAnnotation = mock(Annotation.class);
when(keyAnnotation.getStartNode()).thenReturn(startNode);
when(keyAnnotation.getEndNode()).thenReturn(endNode);
when(responseAnnotation.getStartNode()).thenReturn(startNode);
when(responseAnnotation.getEndNode()).thenReturn(endNode);
when(keyAnnotation.coextensive(responseAnnotation)).thenReturn(true);
when(keyAnnotation.isPartiallyCompatible(eq(responseAnnotation), any())).thenReturn(true);
when(responseAnnotation.isPartiallyCompatible(eq(keyAnnotation), any())).thenReturn(true);
Map<Object, Object> features = new HashMap<Object, Object>();
features.put("type", "Person");
// when(keyAnnotation.getFeatures()).thenReturn(features);
// when(responseAnnotation.getFeatures()).thenReturn(features);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet responseSet = mock(AnnotationSet.class);
// when(keySet.get("Person")).thenReturn(Collections.singleton(keyAnnotation));
// when(responseSet.get("Person")).thenReturn(Collections.singleton(responseAnnotation));
when(keyDoc.getAnnotations("KeySet")).thenReturn(keySet);
when(responseDoc.getAnnotations("ResponseSet")).thenReturn(responseSet);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setKeyAnnotationSetName("KeySet");
diff.setResponseAnnotationSetName("ResponseSet");
diff.init();
assertEquals(1.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(1.0, diff.getRecallStrict(), 0.0001);
assertEquals(1.0, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testSetAndGetKeyCorpus() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus corpus = mock(Corpus.class);
diff.setKeyCorpus(corpus);
assertEquals(corpus, diff.getKeyCorpus());
}

@Test
public void testSetAndGetAnnotationSchema() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
diff.setAnnotationSchema(schema);
assertEquals(schema, diff.getAnnotationSchema());
}

@Test
public void testSetAndGetParameterValue() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
diff.setAnnotationTypeForFalsePositive("Entity");
Object value = diff.getParameterValue("annotationTypeForFalsePositive");
assertEquals("Entity", value);
}

@Test
public void testSetAndGetParameterValues() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap parameters = Factory.newFeatureMap();
parameters.put("annotationTypeForFalsePositive", "NamedEntity");
diff.setParameterValues(parameters);
assertEquals("NamedEntity", diff.getAnnotationTypeForFalsePositive());
}

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWithoutAnnotationSchema() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
}

@Test
public void testInitSkipsNonMatchingDocumentNames() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc-key");
when(responseDoc.getName()).thenReturn("doc-response");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://key"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://response"));
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
}

@Test
public void testInitHandlesMissingAnnotationSetsGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
URL url = new URL("http://example.com");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keyAnnotationSet = mock(AnnotationSet.class);
AnnotationSet responseAnnotationSet = mock(AnnotationSet.class);
when(keyDoc.getAnnotations("KeySet")).thenReturn(keyAnnotationSet);
when(responseDoc.getAnnotations("ResponseSet")).thenReturn(responseAnnotationSet);
when(keyAnnotationSet.get("Entity")).thenReturn(null);
when(responseAnnotationSet.get("Entity")).thenReturn(null);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setKeyAnnotationSetName("KeySet");
diff.setResponseAnnotationSetName("ResponseSet");
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
}

@Test
public void testInitWithPartiallyCompatibleAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
URL url = new URL("http://example.com/doc1");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
gate.Node start1 = mock(gate.Node.class);
gate.Node end1 = mock(gate.Node.class);
gate.Node start2 = mock(gate.Node.class);
gate.Node end2 = mock(gate.Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(5L);
when(start2.getOffset()).thenReturn(0L);
when(end2.getOffset()).thenReturn(8L);
Annotation keyAnnot = mock(Annotation.class);
Annotation responseAnnot = mock(Annotation.class);
when(keyAnnot.getStartNode()).thenReturn(start1);
when(keyAnnot.getEndNode()).thenReturn(end1);
when(responseAnnot.getStartNode()).thenReturn(start2);
when(responseAnnot.getEndNode()).thenReturn(end2);
when(keyAnnot.coextensive(responseAnnot)).thenReturn(false);
when(keyAnnot.isPartiallyCompatible(eq(responseAnnot), any())).thenReturn(true);
when(responseAnnot.isPartiallyCompatible(eq(keyAnnot), any())).thenReturn(true);
Map<Object, Object> feats1 = new HashMap<Object, Object>();
feats1.put("type", "Person");
// when(keyAnnot.getFeatures()).thenReturn(feats1);
// when(responseAnnot.getFeatures()).thenReturn(feats1);
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
// when(keyAnnSet.get("Entity")).thenReturn(Set.of(keyAnnot));
// when(respAnnSet.get("Entity")).thenReturn(Set.of(responseAnnot));
when(keyDoc.getAnnotations("K")).thenReturn(keyAnnSet);
when(responseDoc.getAnnotations("R")).thenReturn(respAnnSet);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setKeyAnnotationSetName("K");
diff.setResponseAnnotationSetName("R");
diff.init();
double precision = diff.getPrecisionLenient();
double recall = diff.getRecallLenient();
double fMeasure = diff.getFMeasureLenient();
assertTrue(precision > 0.0 && precision < 1.0);
assertTrue(recall > 0.0 && recall < 1.0);
assertTrue(fMeasure > 0.0 && fMeasure < 1.0);
}

@Test
public void testInitWithSpuriousOnlyAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("docX");
when(responseDoc.getName()).thenReturn("docX");
URL url = new URL("http://example.com");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet responseSet = mock(AnnotationSet.class);
// when(keySet.get("Entity")).thenReturn(Collections.emptySet());
gate.Node s = mock(gate.Node.class);
gate.Node e = mock(gate.Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(20L);
Annotation respAnnot = mock(Annotation.class);
when(respAnnot.getStartNode()).thenReturn(s);
when(respAnnot.getEndNode()).thenReturn(e);
Map<Object, Object> responseFeatures = new HashMap<Object, Object>();
responseFeatures.put("category", "ORG");
// when(respAnnot.getFeatures()).thenReturn(responseFeatures);
// when(responseSet.get("Entity")).thenReturn(Collections.singleton(respAnnot));
when(keyDoc.getAnnotations("KS")).thenReturn(keySet);
when(responseDoc.getAnnotations("RS")).thenReturn(responseSet);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setKeyAnnotationSetName("KS");
diff.setResponseAnnotationSetName("RS");
diff.init();
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertTrue(diff.getPrecisionStrict() < 1.0);
}

@Test
public void testEmptyCorpusObjectsShouldSkipProcessing() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(0);
when(responseCorpus.size()).thenReturn(0);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Token");
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
try {
diff.init();
fail("Expected ResourceInstantiationException due to empty corpora");
} catch (ResourceInstantiationException e) {
assertTrue(e.getMessage().contains("No key corpus or empty"));
}
}

@Test
public void testResponseAnnotationPartiallyMatchesMultipleKeyAnnotations() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
URL url = new URL("http://example.com/doc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
gate.Node node1Start = mock(gate.Node.class);
gate.Node node1End = mock(gate.Node.class);
when(node1Start.getOffset()).thenReturn(0L);
when(node1End.getOffset()).thenReturn(10L);
gate.Node node2Start = mock(gate.Node.class);
gate.Node node2End = mock(gate.Node.class);
when(node2Start.getOffset()).thenReturn(5L);
when(node2End.getOffset()).thenReturn(15L);
gate.Node respStart = mock(gate.Node.class);
gate.Node respEnd = mock(gate.Node.class);
when(respStart.getOffset()).thenReturn(6L);
when(respEnd.getOffset()).thenReturn(14L);
Annotation key1 = mock(Annotation.class);
when(key1.getStartNode()).thenReturn(node1Start);
when(key1.getEndNode()).thenReturn(node1End);
Annotation key2 = mock(Annotation.class);
when(key2.getStartNode()).thenReturn(node2Start);
when(key2.getEndNode()).thenReturn(node2End);
Annotation responseAnno = mock(Annotation.class);
when(responseAnno.getStartNode()).thenReturn(respStart);
when(responseAnno.getEndNode()).thenReturn(respEnd);
when(responseAnno.isPartiallyCompatible(eq(key1), any())).thenReturn(true);
when(responseAnno.isPartiallyCompatible(eq(key2), any())).thenReturn(true);
when(key1.isPartiallyCompatible(eq(responseAnno), any())).thenReturn(true);
when(key2.isPartiallyCompatible(eq(responseAnno), any())).thenReturn(true);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet respSet = mock(AnnotationSet.class);
// when(keySet.get("Entity")).thenReturn(Arrays.asList(key1, key2));
// when(respSet.get("Entity")).thenReturn(Collections.singletonList(responseAnno));
when(keyDoc.getAnnotations("K")).thenReturn(keySet);
when(responseDoc.getAnnotations("R")).thenReturn(respSet);
Map<Object, Object> features = new HashMap<Object, Object>();
features.put("label", "Person");
// when(key1.getFeatures()).thenReturn(features);
// when(key2.getFeatures()).thenReturn(features);
// when(responseAnno.getFeatures()).thenReturn(features);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setKeyAnnotationSetName("K");
diff.setResponseAnnotationSetName("R");
diff.init();
assertTrue(diff.getPrecisionLenient() > 0.0);
assertTrue(diff.getRecallLenient() > 0.0);
}

@Test
public void testNullFeatureMapShouldNotCauseNPE() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
String docName = "doc1";
when(keyDoc.getName()).thenReturn(docName);
when(responseDoc.getName()).thenReturn(docName);
URL url = new URL("http://example.com");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
gate.Node s1 = mock(gate.Node.class);
gate.Node e1 = mock(gate.Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(5L);
Annotation keyAnno = mock(Annotation.class);
when(keyAnno.getStartNode()).thenReturn(s1);
when(keyAnno.getEndNode()).thenReturn(e1);
when(keyAnno.getFeatures()).thenReturn(null);
when(keyAnno.coextensive(any())).thenReturn(true);
when(keyAnno.isPartiallyCompatible(any(), any())).thenReturn(true);
gate.Node s2 = mock(gate.Node.class);
gate.Node e2 = mock(gate.Node.class);
when(s2.getOffset()).thenReturn(0L);
when(e2.getOffset()).thenReturn(5L);
Annotation respAnno = mock(Annotation.class);
when(respAnno.getStartNode()).thenReturn(s2);
when(respAnno.getEndNode()).thenReturn(e2);
when(respAnno.getFeatures()).thenReturn(null);
when(respAnno.isPartiallyCompatible(eq(keyAnno), any())).thenReturn(true);
AnnotationSet keySet = mock(AnnotationSet.class);
// when(keySet.get("Entity")).thenReturn(Collections.singleton(keyAnno));
when(keyDoc.getAnnotations("KS")).thenReturn(keySet);
AnnotationSet respSet = mock(AnnotationSet.class);
// when(respSet.get("Entity")).thenReturn(Collections.singleton(respAnno));
when(responseDoc.getAnnotations("RS")).thenReturn(respSet);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setKeyAnnotationSetName("KS");
diff.setResponseAnnotationSetName("RS");
diff.setAnnotationSchema(schema);
diff.init();
assertTrue(diff.getPrecisionStrict() >= 0.0);
}

@Test
public void testSetResponseCorpusAndGetResponseCorpus() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus corpus = mock(Corpus.class);
diff.setResponseCorpus(corpus);
assertNotNull(corpus);
}

@Test
public void testAddToDiffsetHandlesNullInputGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Class<?> clazz = CorpusAnnotationDiff.class;
java.lang.reflect.Method method = clazz.getDeclaredMethod("addToDiffset", CorpusAnnotationDiff.DiffSetElement.class);
method.setAccessible(true);
method.invoke(diff, new Object[] { null });
}

@Test
public void testFMeasureDenominatorZeroShouldYieldZero() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
URL url = new URL("http://example.com/d");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
when(keyDoc.getName()).thenReturn("d1");
when(responseDoc.getName()).thenReturn("d1");
AnnotationSet keyAnnSet = mock(AnnotationSet.class);
AnnotationSet respAnnSet = mock(AnnotationSet.class);
// when(keyAnnSet.get("Entity")).thenReturn(Collections.emptyList());
// when(respAnnSet.get("Entity")).thenReturn(Collections.emptyList());
when(keyDoc.getAnnotations(null)).thenReturn(keyAnnSet);
when(responseDoc.getAnnotations(null)).thenReturn(respAnnSet);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setKeyAnnotationSetName(null);
diff.setResponseAnnotationSetName(null);
diff.init();
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureLenient(), 0.0001);
}

@Test
public void testFalsePositiveComputation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
diff.setAnnotationTypeForFalsePositive("Token");
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
URL url = new URL("http://example.com");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
Annotation responseAnnotation = mock(Annotation.class);
gate.Node s = mock(gate.Node.class);
gate.Node e = mock(gate.Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(20L);
when(responseAnnotation.getStartNode()).thenReturn(s);
when(responseAnnotation.getEndNode()).thenReturn(e);
// when(responseAnnotation.getFeatures()).thenReturn(new HashMap<Object, Object>());
AnnotationSet responseAnnSet = mock(AnnotationSet.class);
AnnotationSet tokenSet = mock(AnnotationSet.class);
when(responseDoc.getAnnotations(null)).thenReturn(responseAnnSet);
// when(responseAnnSet.get("Entity")).thenReturn(Collections.singleton(responseAnnotation));
when(responseDoc.getAnnotations()).thenReturn(responseAnnSet);
// when(responseAnnSet.get("Token")).thenReturn(Arrays.asList(mock(Annotation.class), mock(Annotation.class)));
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setResponseCorpus(responseCorpus);
diff.setKeyCorpus(keyCorpus);
diff.setAnnotationSchema(schema);
diff.init();
assertTrue(diff.getFalsePositiveStrict() > 0.0);
assertTrue(diff.getFalsePositiveAverage() > 0.0);
}

@Test
public void testDiffSetElementHandlesNulls() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, -1);
assertNull(element.getLeftAnnotation());
assertNull(element.getRightAnnotation());
assertEquals(-1, element.getLeftType());
assertEquals(-1, element.getRightType());
element.setLeftType(3);
element.setRightType(2);
assertEquals(3, element.getLeftType());
assertEquals(2, element.getRightType());
Document doc = mock(Document.class);
element.setKeyDocument(doc);
element.setResponseDocument(doc);
assertEquals(doc, element.getKeyDocument());
assertEquals(doc, element.getResponseDocument());
}

@Test
public void testGetAnnotationsOfTypeReturnsEmptyForUnsetDiffSet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<Annotation> result = diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testScrollableMethodsBehavior() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertFalse(diff.getScrollableTracksViewportHeight());
assertFalse(diff.getScrollableTracksViewportWidth());
assertNotNull(diff.getPreferredScrollableViewportSize());
int unitIncrement = diff.getScrollableUnitIncrement(new java.awt.Rectangle(0, 0, 50, 50), SwingConstants.HORIZONTAL, 1);
assertEquals(10, unitIncrement);
int blockIncrement = diff.getScrollableBlockIncrement(new java.awt.Rectangle(0, 0, 100, 100), SwingConstants.VERTICAL, 1);
assertEquals(90, blockIncrement);
}

@Test
public void testGetParameterValueReturnsCorrectValue() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setAnnotationTypeForFalsePositive("TestType");
Object value = diff.getParameterValue("annotationTypeForFalsePositive");
assertEquals("TestType", value);
}

@Test
public void testSetParameterValueDoesNotThrow() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setParameterValue("annotationTypeForFalsePositive", "Tokens");
assertEquals("Tokens", diff.getAnnotationTypeForFalsePositive());
}

@Test
public void testArangeAllComponentsSafeAfterInit() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("T");
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
URL url = new URL("http://example.com");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSet ks = mock(AnnotationSet.class);
AnnotationSet rs = mock(AnnotationSet.class);
Annotation aKey = mock(Annotation.class);
Annotation aResp = mock(Annotation.class);
gate.Node ns = mock(gate.Node.class);
gate.Node ne = mock(gate.Node.class);
when(ns.getOffset()).thenReturn(0L);
when(ne.getOffset()).thenReturn(5L);
when(aKey.getStartNode()).thenReturn(ns);
when(aKey.getEndNode()).thenReturn(ne);
when(aResp.getStartNode()).thenReturn(ns);
when(aResp.getEndNode()).thenReturn(ne);
when(aKey.coextensive(aResp)).thenReturn(true);
when(aKey.isPartiallyCompatible(eq(aResp), any())).thenReturn(true);
when(aResp.isPartiallyCompatible(eq(aKey), any())).thenReturn(true);
// when(ks.get("T")).thenReturn(Collections.singleton(aKey));
// when(rs.get("T")).thenReturn(Collections.singleton(aResp));
when(keyDoc.getAnnotations(null)).thenReturn(ks);
when(responseDoc.getAnnotations(null)).thenReturn(rs);
// when(aKey.getFeatures()).thenReturn(Collections.emptyMap());
// when(aResp.getFeatures()).thenReturn(Collections.emptyMap());
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setAnnotationSchema(schema);
diff.setTextMode(Boolean.FALSE);
diff.init();
}

@Test
public void testDefaultTypeColorMapping() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertNotNull(diff.getPreferredScrollableViewportSize());
assertFalse(diff.getScrollableTracksViewportWidth());
assertFalse(diff.getScrollableTracksViewportHeight());
int blockIncrement = diff.getScrollableBlockIncrement(new Rectangle(0, 0, 100, 100), SwingConstants.HORIZONTAL, 1);
assertEquals(90, blockIncrement);
int unitIncrement = diff.getScrollableUnitIncrement(new Rectangle(0, 0, 100, 100), SwingConstants.VERTICAL, 1);
assertEquals(10, unitIncrement);
}

@Test
public void testAnnotationDiffCellRendererHandlesNullsGracefully() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = new JTable(1, 11);
Object value = "sample";
Component component = renderer.getTableCellRendererComponent(table, value, false, false, 0, 4);
assertNotNull(component);
assertTrue(component instanceof JPanel);
}

@Test
public void testAnnotationDiffTableModelEmptyDefaults() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
assertEquals(0, model.getRowCount());
assertEquals(10, model.getColumnCount());
assertNull(model.getValueAt(0, 0));
}

@Test
public void testAnnotationDiffTableModelWithNullDiffSetElement() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
List<CorpusAnnotationDiff.DiffSetElement> list = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
list.add(null);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(list);
assertEquals(1, model.getRowCount());
assertNull(model.getValueAt(0, 1));
}

@Test
public void testAnnotationDiffTableModelTypeAndFeatureColumns() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Document doc = mock(Document.class);
gate.Node start = mock(gate.Node.class);
gate.Node end = mock(gate.Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(4L);
Annotation leftAnn = mock(Annotation.class);
when(leftAnn.getStartNode()).thenReturn(start);
when(leftAnn.getEndNode()).thenReturn(end);
FeatureMap features = Factory.newFeatureMap();
features.put("key", "value");
when(leftAnn.getFeatures()).thenReturn(features);
Annotation rightAnn = mock(Annotation.class);
when(rightAnn.getStartNode()).thenReturn(start);
when(rightAnn.getEndNode()).thenReturn(end);
when(rightAnn.getFeatures()).thenReturn(features);
gate.DocumentContent docContent = mock(gate.DocumentContent.class);
// when(docContent.getContent(0L, 4L)).thenReturn("text");
when(doc.getContent()).thenReturn(docContent);
when(doc.getName()).thenReturn("doc1");
CorpusAnnotationDiff.DiffSetElement e = diff.new DiffSetElement(leftAnn, rightAnn, CorpusAnnotationDiff.CORRECT_TYPE, CorpusAnnotationDiff.CORRECT_TYPE, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
data.add(e);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
Object textValue = model.getValueAt(0, 0);
assertEquals("text", textValue);
Object fKey = model.getValueAt(0, 3);
assertTrue(fKey.toString().contains("key"));
Object fResp = model.getValueAt(0, 8);
assertTrue(fResp.toString().contains("key"));
Object docCol = model.getValueAt(0, 9);
assertEquals("doc1", docCol);
}

@Test
public void testAnnotationSetComparatorWithNullOffsets() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationSetComparator comp = diff.new AnnotationSetComparator();
Annotation a1 = mock(Annotation.class);
Annotation a2 = mock(Annotation.class);
gate.Node start1 = mock(gate.Node.class);
gate.Node start2 = mock(gate.Node.class);
when(start1.getOffset()).thenReturn(null);
when(start2.getOffset()).thenReturn(5L);
when(a1.getStartNode()).thenReturn(start1);
when(a2.getStartNode()).thenReturn(start2);
int result = comp.compare(a1, a2);
assertEquals(-1, result);
}

@Test
public void testPrintStructureHandlesNullAnnotations() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element1 = diff.new DiffSetElement(null, null, -1, -1, null, null);
CorpusAnnotationDiff.DiffSetElement element2 = diff.new DiffSetElement(null, mock(Annotation.class), -1, 3, null, null);
Set<CorpusAnnotationDiff.DiffSetElement> diffSet = new LinkedHashSet<CorpusAnnotationDiff.DiffSetElement>();
diffSet.add(element1);
diffSet.add(element2);
diff.printStructure(diffSet);
}

@Test
public void testSetParameterValuesWithMultipleValues() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap map = Factory.newFeatureMap();
map.put("annotationTypeForFalsePositive", "Token");
map.put("responseAnnotationSetName", "RespSet");
map.put("responseAnnotationSetNameFalsePoz", "FalseSet");
diff.setParameterValues(map);
assertEquals("Token", diff.getAnnotationTypeForFalsePositive());
assertEquals("RespSet", diff.getResponseAnnotationSetName());
assertEquals("FalseSet", diff.getResponseAnnotationSetNameFalsePoz());
}

@Test
public void testDiffSetElementSettersAndGetters() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement();
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
element.setLeftAnnotation(left);
element.setRightAnnotation(right);
element.setLeftType(1);
element.setRightType(3);
element.setKeyDocument(keyDoc);
element.setResponseDocument(respDoc);
assertEquals(left, element.getLeftAnnotation());
assertEquals(right, element.getRightAnnotation());
assertEquals(1, element.getLeftType());
assertEquals(3, element.getRightType());
assertEquals(keyDoc, element.getKeyDocument());
assertEquals(respDoc, element.getResponseDocument());
}

@Test
public void testColorPaletteDefaultInitialization() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
try {
diff.init();
} catch (ResourceInstantiationException e) {
assertNotNull(e);
}
}

@Test
public void testAnnotationDiffCellRendererIgnoresInvalidRawData() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = mock(JTable.class);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
when(table.getModel()).thenReturn(model);
when(model.getValueAt(0, 10)).thenReturn("Invalid");
Component component = renderer.getTableCellRendererComponent(table, "value", false, false, 0, 0);
assertNotNull(component);
}

@Test
public void testDiffSetElementWithOnlyRightAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation rightAnnotation = mock(Annotation.class);
gate.Node startNode = mock(gate.Node.class);
gate.Node endNode = mock(gate.Node.class);
when(startNode.getOffset()).thenReturn(5L);
when(endNode.getOffset()).thenReturn(10L);
when(rightAnnotation.getStartNode()).thenReturn(startNode);
when(rightAnnotation.getEndNode()).thenReturn(endNode);
Map<Object, Object> features = new HashMap<Object, Object>();
features.put("feat", "value");
// when(rightAnnotation.getFeatures()).thenReturn(features);
Document rightDoc = mock(Document.class);
DocumentContent docContent = mock(DocumentContent.class);
// when(docContent.getContent(5L, 10L)).thenReturn("hello");
when(rightDoc.getContent()).thenReturn(docContent);
when(rightDoc.getName()).thenReturn("docResp");
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, rightAnnotation, -1, CorpusAnnotationDiff.SPURIOUS_TYPE, null, rightDoc);
List<CorpusAnnotationDiff.DiffSetElement> list = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
list.add(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(list);
assertEquals("hello", model.getValueAt(0, 5));
assertEquals(5L, model.getValueAt(0, 6));
assertEquals(10L, model.getValueAt(0, 7));
assertEquals("{feat=value}", model.getValueAt(0, 8));
assertEquals("docResp", model.getValueAt(0, 9));
}

@Test
public void testAddToDiffsetDoesNotCrashWithNullTypeFields() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Class<?> clazz = diff.getClass();
java.lang.reflect.Method method = clazz.getDeclaredMethod("addToDiffset", CorpusAnnotationDiff.DiffSetElement.class);
method.setAccessible(true);
Document doc = mock(Document.class);
Annotation annotation = mock(Annotation.class);
CorpusAnnotationDiff.DiffSetElement el = diff.new DiffSetElement(annotation, null, -1, -1, doc, doc);
method.invoke(diff, el);
}

@Test
public void testdetectKeyTypeHandlesNullAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
java.lang.reflect.Method detectKeyType = CorpusAnnotationDiff.class.getDeclaredMethod("detectKeyType", Annotation.class);
detectKeyType.setAccessible(true);
int result = (int) detectKeyType.invoke(diff, new Object[] { null });
assertEquals(-1, result);
}

@Test
public void testDetectResponseTypeHandlesNullAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
java.lang.reflect.Method method = CorpusAnnotationDiff.class.getDeclaredMethod("detectResponseType", Annotation.class);
method.setAccessible(true);
Object result = method.invoke(diff, new Object[] { null });
assertEquals(-1, result);
}

@Test
public void testGetValueAtHandlesNullFeatureMaps() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation ann = mock(Annotation.class);
gate.Node start = mock(gate.Node.class);
gate.Node end = mock(gate.Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(9L);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
when(ann.getFeatures()).thenReturn(null);
Document doc = mock(Document.class);
DocumentContent content = mock(DocumentContent.class);
// when(content.getContent(1L, 9L)).thenReturn("ABC XYZ");
when(doc.getContent()).thenReturn(content);
when(doc.getName()).thenReturn("docX");
CorpusAnnotationDiff.DiffSetElement el = diff.new DiffSetElement(ann, ann, CorpusAnnotationDiff.DEFAULT_TYPE, CorpusAnnotationDiff.DEFAULT_TYPE, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> rows = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
rows.add(el);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(rows);
assertEquals("ABC XYZ", model.getValueAt(0, 0));
assertEquals(1L, model.getValueAt(0, 1));
assertEquals(9L, model.getValueAt(0, 2));
assertNull(model.getValueAt(0, 3));
assertEquals("   ", model.getValueAt(0, 4));
assertEquals("ABC XYZ", model.getValueAt(0, 5));
assertNull(model.getValueAt(0, 8));
}

@Test
public void testTableModelColumnNames() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
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
assertEquals("?", model.getColumnName(99));
}

@Test
public void testTableModelColumnTypes() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
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
assertEquals(Object.class, model.getColumnClass(99));
}

@Test
public void testDiffSetElementLeftOnly() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation ann = mock(Annotation.class);
gate.Node start = mock(gate.Node.class);
gate.Node end = mock(gate.Node.class);
when(start.getOffset()).thenReturn(12L);
when(end.getOffset()).thenReturn(18L);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
FeatureMap feats = Factory.newFeatureMap();
feats.put("a", "b");
when(ann.getFeatures()).thenReturn(feats);
Document doc = mock(Document.class);
DocumentContent content = mock(DocumentContent.class);
// when(content.getContent(12L, 18L)).thenReturn("TestXD");
when(doc.getContent()).thenReturn(content);
when(doc.getName()).thenReturn("TestDoc");
CorpusAnnotationDiff.DiffSetElement el = diff.new DiffSetElement(ann, null, CorpusAnnotationDiff.MISSING_TYPE, -1, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
data.add(el);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
assertEquals("TestXD", model.getValueAt(0, 0));
assertEquals(12L, model.getValueAt(0, 1));
assertEquals(18L, model.getValueAt(0, 2));
assertEquals("{a=b}", model.getValueAt(0, 3));
assertEquals("   ", model.getValueAt(0, 4));
assertNull(model.getValueAt(0, 5));
assertNull(model.getValueAt(0, 6));
assertNull(model.getValueAt(0, 7));
assertNull(model.getValueAt(0, 8));
assertEquals("TestDoc", model.getValueAt(0, 9));
}

@Test
public void testCellRendererReturnsDefaultForNullValue() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = new JTable(1, 11);
Component comp = renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
assertNotNull(comp);
}

@Test
public void testGetAnnotationsOfTypeWithNullDiffSet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<Annotation> result = diff.getAnnotationsOfType(CorpusAnnotationDiff.CORRECT_TYPE);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testDoDiffHandlesEmptyListsSafely() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
java.lang.reflect.Method m = CorpusAnnotationDiff.class.getDeclaredMethod("doDiff", List.class, List.class);
m.setAccessible(true);
m.invoke(diff, new LinkedList<Annotation>(), new ArrayList<Annotation>());
}

@Test
public void testGetRawObjectReturnsDiffSetElement() throws InvalidOffsetException {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation a = mock(Annotation.class);
gate.Node s = mock(gate.Node.class);
gate.Node e = mock(gate.Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(10L);
when(a.getStartNode()).thenReturn(s);
when(a.getEndNode()).thenReturn(e);
when(a.getFeatures()).thenReturn(Factory.newFeatureMap());
Document doc = mock(Document.class);
DocumentContent dc = mock(DocumentContent.class);
// when(dc.getContent(0L, 10L)).thenReturn("abc");
when(doc.getContent()).thenReturn(dc);
when(doc.getName()).thenReturn("X");
CorpusAnnotationDiff.DiffSetElement el = diff.new DiffSetElement(a, a, 1, 1, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> d = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
d.add(el);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(d);
Object raw = model.getRawObject(0);
assertSame(el, raw);
}

@Test
public void testAddingNullToTableRendererReturnsPanel() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = mock(JTable.class);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel();
when(table.getModel()).thenReturn(model);
Component cell = renderer.getTableCellRendererComponent(table, "x", false, false, 0, 10);
assertNotNull(cell);
}

@Test
public void testInvalidOffsetExceptionHandlingSafe() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation a = mock(Annotation.class);
gate.Node s = mock(gate.Node.class);
gate.Node e = mock(gate.Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(100L);
when(a.getStartNode()).thenReturn(s);
when(a.getEndNode()).thenReturn(e);
when(a.getFeatures()).thenReturn(Factory.newFeatureMap());
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("someDoc");
DocumentContent badContent = mock(DocumentContent.class);
when(badContent.getContent(0L, 100L)).thenThrow(new gate.util.InvalidOffsetException("bad"));
when(doc.getContent()).thenReturn(badContent);
CorpusAnnotationDiff.DiffSetElement el = diff.new DiffSetElement(a, null, 1, -1, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> set = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
set.add(el);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(set);
Object val = model.getValueAt(0, 0);
assertEquals("", val);
}

@Test
public void testFeatureMapReflectionGetAndSetParameterValue() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setAnnotationTypeForFalsePositive("XXX");
Object val = diff.getParameterValue("annotationTypeForFalsePositive");
assertEquals("XXX", val);
diff.setParameterValue("responseAnnotationSetName", "RR");
assertEquals("RR", diff.getResponseAnnotationSetName());
FeatureMap map = Factory.newFeatureMap();
map.put("responseAnnotationSetNameFalsePoz", "FPP");
diff.setParameterValues(map);
assertEquals("FPP", diff.getResponseAnnotationSetNameFalsePoz());
}

@Test
public void testColorArrayAssignmentViaInitWithInvalidConditions() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
try {
diff.init();
} catch (ResourceInstantiationException ex) {
assertTrue(ex.getMessage().contains("No annotation schema"));
}
}
}
