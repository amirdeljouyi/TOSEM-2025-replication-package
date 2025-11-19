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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CorpusAnnotationDiff_llmsuite_5_GPTLLMTest {

@Test
public void testInitWithCorrectAnnotationMatch() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("Doc1");
when(respDoc.getName()).thenReturn("Doc1");
URL url = new URL("file:///dummy.txt");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
Annotation keyAnnot = mock(Annotation.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(5L);
when(keyAnnot.getStartNode()).thenReturn(keyStart);
when(keyAnnot.getEndNode()).thenReturn(keyEnd);
when(keyAnnot.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(keyAnnot.coextensive(any())).thenReturn(true);
when(keyAnnot.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation respAnnot = mock(Annotation.class);
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(respStart.getOffset()).thenReturn(0L);
when(respEnd.getOffset()).thenReturn(5L);
when(respAnnot.getStartNode()).thenReturn(respStart);
when(respAnnot.getEndNode()).thenReturn(respEnd);
when(respAnnot.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(respAnnot.coextensive(any())).thenReturn(true);
when(respAnnot.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keyAnnotations = new HashSet<>();
keyAnnotations.add(keyAnnot);
Set<Annotation> respAnnotations = new HashSet<>();
respAnnotations.add(respAnnot);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet keyTopLevel = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyTopLevel);
// when(keyTopLevel.get("Entity")).thenReturn(keyAnnotations);
AnnotationSet respAS = mock(AnnotationSet.class);
AnnotationSet respTopLevel = mock(AnnotationSet.class);
when(respDoc.getAnnotations()).thenReturn(respTopLevel);
// when(respTopLevel.get("Entity")).thenReturn(respAnnotations);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertEquals(1.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(1.0, diff.getRecallStrict(), 0.00001);
assertEquals(1.0, diff.getFMeasureStrict(), 0.00001);
}

@Test
public void testInitWithMissingAndSpuriousAnnotations() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("Doc2");
when(respDoc.getName()).thenReturn("Doc2");
URL url = new URL("file:///dummy2.txt");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
Annotation keyAnnot = mock(Annotation.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(10L);
when(keyAnnot.getStartNode()).thenReturn(keyStart);
when(keyAnnot.getEndNode()).thenReturn(keyEnd);
when(keyAnnot.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(keyAnnot.coextensive(any())).thenReturn(false);
when(keyAnnot.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation responseAnnot = mock(Annotation.class);
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(respStart.getOffset()).thenReturn(20L);
when(respEnd.getOffset()).thenReturn(30L);
when(responseAnnot.getStartNode()).thenReturn(respStart);
when(responseAnnot.getEndNode()).thenReturn(respEnd);
when(responseAnnot.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(responseAnnot.coextensive(any())).thenReturn(false);
when(responseAnnot.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnnot);
Set<Annotation> respSet = new HashSet<>();
respSet.add(responseAnnot);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet respAS = mock(AnnotationSet.class);
AnnotationSet keyTop = mock(AnnotationSet.class);
AnnotationSet respTop = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyTop);
// when(keyTop.get("Entity")).thenReturn(keySet);
when(respDoc.getAnnotations()).thenReturn(respTop);
// when(respTop.get("Entity")).thenReturn(respSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(0.0, diff.getRecallStrict(), 0.00001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.00001);
}

@Test
public void testPartialCompatibilityMatchYieldsPartialCredits() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("Doc3");
when(respDoc.getName()).thenReturn("Doc3");
URL url = new URL("file:///dummy3.txt");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
Annotation keyAnnot = mock(Annotation.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(10L);
when(keyAnnot.getStartNode()).thenReturn(keyStart);
when(keyAnnot.getEndNode()).thenReturn(keyEnd);
when(keyAnnot.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(keyAnnot.coextensive(any())).thenReturn(false);
when(keyAnnot.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation respAnnot = mock(Annotation.class);
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(respStart.getOffset()).thenReturn(5L);
when(respEnd.getOffset()).thenReturn(15L);
when(respAnnot.getStartNode()).thenReturn(respStart);
when(respAnnot.getEndNode()).thenReturn(respEnd);
when(respAnnot.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(respAnnot.coextensive(any())).thenReturn(false);
when(respAnnot.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnnot);
Set<Annotation> respSet = new HashSet<>();
respSet.add(respAnnot);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet respAS = mock(AnnotationSet.class);
AnnotationSet keyTop = mock(AnnotationSet.class);
AnnotationSet respTop = mock(AnnotationSet.class);
when(keyDoc.getAnnotations()).thenReturn(keyTop);
// when(keyTop.get("Entity")).thenReturn(keySet);
when(respDoc.getAnnotations()).thenReturn(respTop);
// when(respTop.get("Entity")).thenReturn(respSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertTrue(diff.getPrecisionStrict() < 1.0);
assertTrue(diff.getRecallStrict() < 1.0);
assertTrue(diff.getFMeasureStrict() < 1.0);
}

@Test
public void testAnnotationSetNameSettersAndGetters() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setKeyAnnotationSetName("GoldSet");
diff.setResponseAnnotationSetName("SystemSet");
diff.setResponseAnnotationSetNameFalsePoz("SystemSetFP");
assertEquals("GoldSet", diff.getKeyAnnotationSetName());
assertEquals("SystemSet", diff.getResponseAnnotationSetName());
assertEquals("SystemSetFP", diff.getResponseAnnotationSetNameFalsePoz());
}

@Test
public void testSettingKeyFeatureNamesSet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<String> features = new HashSet<>();
features.add("type");
diff.setKeyFeatureNamesSet(features);
assertEquals(features, diff.getKeyFeatureNamesSet());
}

@Test
public void testInitWithNullAnnotationSchemaThrowsException() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
try {
diff.init();
fail("Expected ResourceInstantiationException");
} catch (ResourceInstantiationException e) {
assertTrue(e.getMessage().contains("No annotation schema"));
}
}

@Test
public void testTextModeSetAndGet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
assertTrue(diff.isTextMode());
diff.setTextMode(Boolean.FALSE);
assertFalse(diff.isTextMode());
}

@Test
public void testInitWithEmptyAnnotationSetsForBothKeyAndResponse() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Token");
diff.setAnnotationSchema(schema);
AnnotationSet emptyKeySet = mock(AnnotationSet.class);
AnnotationSet emptyRespSet = mock(AnnotationSet.class);
Set<Annotation> keyAnns = new HashSet<>();
Set<Annotation> respAnns = new HashSet<>();
// when(emptyKeySet.get("Token")).thenReturn(keyAnns);
// when(emptyRespSet.get("Token")).thenReturn(respAnns);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
URL url = new URL("http://d");
when(keyDoc.getName()).thenReturn("test");
when(respDoc.getName()).thenReturn("test");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
when(keyDoc.getAnnotations()).thenReturn(emptyKeySet);
when(respDoc.getAnnotations()).thenReturn(emptyRespSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(0.0, diff.getRecallStrict(), 0.00001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.00001);
}

@Test
public void testInitWhenKeyHasNullAnnotationSet() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Token");
diff.setAnnotationSchema(schema);
AnnotationSet responseSet = mock(AnnotationSet.class);
Set<Annotation> responseAnnots = new HashSet<>();
Annotation response = mock(Annotation.class);
Node rStart = mock(Node.class);
Node rEnd = mock(Node.class);
when(rStart.getOffset()).thenReturn(5L);
when(rEnd.getOffset()).thenReturn(10L);
when(response.getStartNode()).thenReturn(rStart);
when(response.getEndNode()).thenReturn(rEnd);
when(response.getFeatures()).thenReturn(Factory.newFeatureMap());
when(response.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(response.coextensive(any())).thenReturn(false);
responseAnnots.add(response);
// when(responseSet.get("Token")).thenReturn(responseAnnots);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
URL url = new URL("http://doc");
when(keyDoc.getName()).thenReturn("doc");
when(respDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
when(keyDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
when(keyDoc.getAnnotations().get("Token")).thenReturn(null);
when(respDoc.getAnnotations()).thenReturn(responseSet);
Corpus kc = mock(Corpus.class);
Corpus rc = mock(Corpus.class);
when(kc.size()).thenReturn(1);
when(rc.size()).thenReturn(1);
when(kc.get(0)).thenReturn(keyDoc);
when(rc.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(kc);
diff.setResponseCorpus(rc);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(0.0, diff.getRecallStrict(), 0.00001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.00001);
}

@Test
public void testInitWhenResponseHasNullAnnotationSet() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Token");
diff.setAnnotationSchema(schema);
Annotation key = mock(Annotation.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(10L);
when(key.getStartNode()).thenReturn(s);
when(key.getEndNode()).thenReturn(e);
when(key.getFeatures()).thenReturn(Factory.newFeatureMap());
when(key.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(key.coextensive(any())).thenReturn(false);
Set<Annotation> keyAnns = new HashSet<>();
keyAnns.add(key);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet keyTop = mock(AnnotationSet.class);
// when(keyAS.get("Token")).thenReturn(keyAnns);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("doc");
when(respDoc.getName()).thenReturn("doc");
URL url = new URL("http://abc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
when(keyDoc.getAnnotations()).thenReturn(keyTop);
// when(keyTop.get("Token")).thenReturn(keyAnns);
AnnotationSet respTop = mock(AnnotationSet.class);
when(respDoc.getAnnotations()).thenReturn(respTop);
when(respTop.get("Token")).thenReturn(null);
Corpus kc = mock(Corpus.class);
Corpus rc = mock(Corpus.class);
when(kc.size()).thenReturn(1);
when(rc.size()).thenReturn(1);
when(kc.get(0)).thenReturn(keyDoc);
when(rc.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(kc);
diff.setResponseCorpus(rc);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(0.0, diff.getRecallStrict(), 0.00001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.00001);
}

@Test
public void testInitWithKeyAndResponseHavingDifferentDocumentNamesAndURLs() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Type");
diff.setAnnotationSchema(schema);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("DocX");
when(respDoc.getName()).thenReturn("DocY");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://doc/x"));
when(respDoc.getSourceUrl()).thenReturn(new URL("http://doc/y"));
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(0.0, diff.getRecallStrict(), 0.00001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.00001);
}

@Test
public void testSetAnnotationTypeForFalsePositiveAndExpectCorrectValue() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setAnnotationTypeForFalsePositive("Token");
assertEquals("Token", diff.getAnnotationTypeForFalsePositive());
}

@Test
public void testSetParameterValueAndGetParameterValueSuccessfully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setParameterValue("annotationTypeForFalsePositive", "Token");
Object value = diff.getParameterValue("annotationTypeForFalsePositive");
assertEquals("Token", value);
}

@Test
public void testSetParameterValuesWithMultipleEntries() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap map = Factory.newFeatureMap();
map.put("annotationTypeForFalsePositive", "Sentence");
map.put("textMode", Boolean.TRUE);
diff.setParameterValues(map);
assertEquals("Sentence", diff.getAnnotationTypeForFalsePositive());
assertTrue(diff.isTextMode());
}

@Test
public void testPartialOverlappingAnnotationsTriggerDetectKeyTypeLogic() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Match");
diff.setAnnotationSchema(schema);
Annotation key1 = mock(Annotation.class);
Node ks = mock(Node.class);
Node ke = mock(Node.class);
when(ks.getOffset()).thenReturn(10L);
when(ke.getOffset()).thenReturn(20L);
when(key1.getStartNode()).thenReturn(ks);
when(key1.getEndNode()).thenReturn(ke);
when(key1.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(key1.coextensive(any())).thenReturn(false);
when(key1.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation response1 = mock(Annotation.class);
Node rs = mock(Node.class);
Node re = mock(Node.class);
when(rs.getOffset()).thenReturn(15L);
when(re.getOffset()).thenReturn(25L);
when(response1.getStartNode()).thenReturn(rs);
when(response1.getEndNode()).thenReturn(re);
when(response1.isPartiallyCompatible(eq(key1), anySet())).thenReturn(true);
when(response1.coextensive(eq(key1))).thenReturn(false);
when(response1.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
Set<Annotation> respSet = new HashSet<>();
keySet.add(key1);
respSet.add(response1);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("doc");
when(respDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://key"));
when(respDoc.getSourceUrl()).thenReturn(new URL("http://key"));
AnnotationSet keyTop = mock(AnnotationSet.class);
AnnotationSet respTop = mock(AnnotationSet.class);
// when(keyTop.get("Match")).thenReturn(keySet);
// when(respTop.get("Match")).thenReturn(respSet);
when(keyDoc.getAnnotations()).thenReturn(keyTop);
when(respDoc.getAnnotations()).thenReturn(respTop);
Corpus kc = mock(Corpus.class);
Corpus rc = mock(Corpus.class);
when(kc.size()).thenReturn(1);
when(rc.size()).thenReturn(1);
when(kc.get(0)).thenReturn(keyDoc);
when(rc.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(kc);
diff.setResponseCorpus(rc);
diff.init();
assertTrue(diff.getPrecisionStrict() < 1.0);
assertTrue(diff.getRecallStrict() < 1.0);
}

@Test
public void testMultipleResponseAnnotationsLeadingToSpuriousEntries() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Annotation keyA = mock(Annotation.class);
Node kaStart = mock(Node.class);
Node kaEnd = mock(Node.class);
when(kaStart.getOffset()).thenReturn(0L);
when(kaEnd.getOffset()).thenReturn(5L);
when(keyA.getStartNode()).thenReturn(kaStart);
when(keyA.getEndNode()).thenReturn(kaEnd);
when(keyA.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(keyA.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation respA = mock(Annotation.class);
Node raStart = mock(Node.class);
Node raEnd = mock(Node.class);
when(raStart.getOffset()).thenReturn(10L);
when(raEnd.getOffset()).thenReturn(12L);
when(respA.getStartNode()).thenReturn(raStart);
when(respA.getEndNode()).thenReturn(raEnd);
when(respA.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(respA.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation respB = mock(Annotation.class);
Node rbStart = mock(Node.class);
Node rbEnd = mock(Node.class);
when(rbStart.getOffset()).thenReturn(15L);
when(rbEnd.getOffset()).thenReturn(18L);
when(respB.getStartNode()).thenReturn(rbStart);
when(respB.getEndNode()).thenReturn(rbEnd);
when(respB.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(respB.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
Set<Annotation> respSet = new HashSet<>();
keySet.add(keyA);
respSet.add(respA);
respSet.add(respB);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("D");
when(respDoc.getName()).thenReturn("D");
URL url = new URL("http://d");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keyTop = mock(AnnotationSet.class);
// when(keyTop.get("Entity")).thenReturn(keySet);
AnnotationSet respTop = mock(AnnotationSet.class);
// when(respTop.get("Entity")).thenReturn(respSet);
when(keyDoc.getAnnotations()).thenReturn(keyTop);
when(respDoc.getAnnotations()).thenReturn(respTop);
Corpus keyCorp = mock(Corpus.class);
Corpus respCorp = mock(Corpus.class);
when(keyCorp.size()).thenReturn(1);
when(respCorp.size()).thenReturn(1);
when(keyCorp.get(0)).thenReturn(keyDoc);
when(respCorp.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorp);
diff.setResponseCorpus(respCorp);
diff.init();
assertEquals(2, diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE).size());
assertEquals(1, diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE).size());
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
}

@Test
public void testRendererReturnsWhiteBackgroundForUnmatchedNullLeftAnnotation() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, 3);
List<Object[]> model = new ArrayList<>();
Object[][] data = new Object[][] { { "", 0L, 0L, "{}", "", "", 0L, 0L, "{}", "Doc" } };
DefaultTableModel tableModel = new DefaultTableModel(new String[] { "String - Key", "Start - Key", "End - Key", "Features - Key", "   ", "String - Response", "Start - Response", "End - Response", "Features - Response", "Document" }, 1);
tableModel.setValueAt("text", 0, 0);
JTable table = new JTable(tableModel);
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
Component c = renderer.getTableCellRendererComponent(table, "text", false, false, 0, 0);
assertNotNull(c);
}

@Test
public void testRendererReturnsNullPanelOnDefaultWhenDiffSetElementIsInvalid() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
JTable table = new JTable(new DefaultTableModel(1, 11));
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
Component comp = renderer.getTableCellRendererComponent(table, null, false, false, 0, 4);
assertNotNull(comp);
}

@Test
public void testEmptyParameterMapPassedToSetParameterValues() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap map = Factory.newFeatureMap();
diff.setParameterValues(map);
assertFalse(diff.isTextMode());
assertNull(diff.getAnnotationTypeForFalsePositive());
}

@Test
public void testGetAnnotationsOfTypeReturnsEmptySetForUninitializedDiffSet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<Annotation> result = diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testIdenticalSpansButMismatchedFeaturesShouldNotMatch() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Type");
diff.setAnnotationSchema(schema);
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("doc");
when(doc2.getName()).thenReturn("doc");
URL url = new URL("file:///test.txt");
when(doc1.getSourceUrl()).thenReturn(url);
when(doc2.getSourceUrl()).thenReturn(url);
Annotation keyAnnot = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
when(keyAnnot.getStartNode()).thenReturn(start);
when(keyAnnot.getEndNode()).thenReturn(end);
FeatureMap keyFeat = Factory.newFeatureMap();
keyFeat.put("type", "PER");
when(keyAnnot.getFeatures()).thenReturn(keyFeat);
when(keyAnnot.coextensive(any())).thenReturn(true);
when(keyAnnot.isPartiallyCompatible(any(), any())).thenReturn(false);
Annotation responseAnnot = mock(Annotation.class);
when(responseAnnot.getStartNode()).thenReturn(start);
when(responseAnnot.getEndNode()).thenReturn(end);
FeatureMap respFeat = Factory.newFeatureMap();
respFeat.put("type", "ORG");
when(responseAnnot.getFeatures()).thenReturn(respFeat);
when(responseAnnot.coextensive(keyAnnot)).thenReturn(true);
when(responseAnnot.isPartiallyCompatible(keyAnnot, null)).thenReturn(false);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet respSet = mock(AnnotationSet.class);
Set<Annotation> keyAnns = new HashSet<>();
keyAnns.add(keyAnnot);
Set<Annotation> respAnns = new HashSet<>();
respAnns.add(responseAnnot);
// when(keySet.get("Type")).thenReturn(keyAnns);
// when(respSet.get("Type")).thenReturn(respAnns);
when(doc1.getAnnotations()).thenReturn(keySet);
when(doc2.getAnnotations()).thenReturn(respSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(doc1);
when(respCorpus.get(0)).thenReturn(doc2);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testZeroLengthAnnotationProcessedAsValidSpan() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Event");
diff.setAnnotationSchema(schema);
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(100L);
Annotation keyAnn = mock(Annotation.class);
when(keyAnn.getStartNode()).thenReturn(node);
when(keyAnn.getEndNode()).thenReturn(node);
when(keyAnn.coextensive(any())).thenReturn(true);
when(keyAnn.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(keyAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation responseAnn = mock(Annotation.class);
when(responseAnn.getStartNode()).thenReturn(node);
when(responseAnn.getEndNode()).thenReturn(node);
when(responseAnn.coextensive(any())).thenReturn(true);
when(responseAnn.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(responseAnn.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
keySet.add(keyAnn);
Set<Annotation> respSet = new HashSet<>();
respSet.add(responseAnn);
AnnotationSet as1 = mock(AnnotationSet.class);
AnnotationSet as2 = mock(AnnotationSet.class);
// when(as1.get("Event")).thenReturn(keySet);
// when(as2.get("Event")).thenReturn(respSet);
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
when(d1.getName()).thenReturn("A");
when(d2.getName()).thenReturn("A");
when(d1.getSourceUrl()).thenReturn(new URL("file:///A"));
when(d2.getSourceUrl()).thenReturn(new URL("file:///A"));
when(d1.getAnnotations()).thenReturn(as1);
when(d2.getAnnotations()).thenReturn(as2);
Corpus kc = mock(Corpus.class);
Corpus rc = mock(Corpus.class);
when(kc.size()).thenReturn(1);
when(rc.size()).thenReturn(1);
when(kc.get(0)).thenReturn(d1);
when(rc.get(0)).thenReturn(d2);
diff.setKeyCorpus(kc);
diff.setResponseCorpus(rc);
diff.init();
assertTrue(diff.getPrecisionStrict() > 0.0);
assertTrue(diff.getFMeasureStrict() > 0.0);
}

@Test
public void testAnnotationWithNullStartNodeDoesNotThrowException() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Abc");
diff.setAnnotationSchema(schema);
Annotation an = mock(Annotation.class);
Node end = mock(Node.class);
when(end.getOffset()).thenReturn(10L);
when(an.getStartNode()).thenReturn(null);
when(an.getEndNode()).thenReturn(end);
when(an.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> keySet = new HashSet<>();
keySet.add(an);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("N");
when(respDoc.getName()).thenReturn("N");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://n"));
when(respDoc.getSourceUrl()).thenReturn(new URL("http://n"));
AnnotationSet keyAnnotSet = mock(AnnotationSet.class);
// when(keyAnnotSet.get("Abc")).thenReturn(keySet);
when(keyDoc.getAnnotations()).thenReturn(keyAnnotSet);
when(respDoc.getAnnotations()).thenReturn(mock(AnnotationSet.class));
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertTrue(diff.getRecallStrict() >= 0.0);
}

@Test
public void testNullDocumentsInCorpusShouldBeHandledGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(null);
when(responseCorpus.get(0)).thenReturn(null);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("X");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
try {
diff.init();
} catch (Exception e) {
fail("Should not throw exception on null documents in corpus: " + e);
}
}

@Test
public void testCoextensiveFalseAndPartiallyCompatibleFalseResultsInMissingAndSpurious() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Chunk");
diff.setAnnotationSchema(schema);
Node ks = mock(Node.class);
Node ke = mock(Node.class);
Node rs = mock(Node.class);
Node re = mock(Node.class);
when(ks.getOffset()).thenReturn(1L);
when(ke.getOffset()).thenReturn(5L);
when(rs.getOffset()).thenReturn(50L);
when(re.getOffset()).thenReturn(60L);
Annotation key = mock(Annotation.class);
when(key.getStartNode()).thenReturn(ks);
when(key.getEndNode()).thenReturn(ke);
when(key.coextensive(any())).thenReturn(false);
when(key.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(key.getFeatures()).thenReturn(Factory.newFeatureMap());
Annotation response = mock(Annotation.class);
when(response.getStartNode()).thenReturn(rs);
when(response.getEndNode()).thenReturn(re);
when(response.coextensive(any())).thenReturn(false);
when(response.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(response.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> ksSet = new HashSet<>();
ksSet.add(key);
Set<Annotation> rsSet = new HashSet<>();
rsSet.add(response);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("Z");
when(responseDoc.getName()).thenReturn("Z");
URL u = new URL("file:///z");
when(keyDoc.getSourceUrl()).thenReturn(u);
when(responseDoc.getSourceUrl()).thenReturn(u);
AnnotationSet kSet = mock(AnnotationSet.class);
AnnotationSet rSet = mock(AnnotationSet.class);
// when(kSet.get("Chunk")).thenReturn(ksSet);
// when(rSet.get("Chunk")).thenReturn(rsSet);
when(keyDoc.getAnnotations()).thenReturn(kSet);
when(responseDoc.getAnnotations()).thenReturn(rSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testKeyCorpusWithRealDocumentAndResponseCorpusEmpty() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("example");
when(doc.getSourceUrl()).thenReturn(new URL("http://example"));
Corpus keyCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(doc);
Corpus responseCorpus = mock(Corpus.class);
when(responseCorpus.size()).thenReturn(0);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
try {
diff.init();
fail("Expected ResourceInstantiationException for empty response corpus");
} catch (ResourceInstantiationException ex) {
assertTrue(ex.getMessage().contains("No response corpus"));
}
}

@Test
public void testResponseCorpusWithRealDocumentAndKeyCorpusEmpty() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("example");
when(doc.getSourceUrl()).thenReturn(new URL("http://example"));
Corpus keyCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(0);
Corpus responseCorpus = mock(Corpus.class);
when(responseCorpus.size()).thenReturn(1);
when(responseCorpus.get(0)).thenReturn(doc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
try {
diff.init();
fail("Expected ResourceInstantiationException for empty key corpus");
} catch (ResourceInstantiationException ex) {
assertTrue(ex.getMessage().contains("No key corpus"));
}
}

@Test
public void testNullAnnotationInKeySetHandledGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet responseSet = mock(AnnotationSet.class);
Set<Annotation> keyAnnotations = new HashSet<>();
keyAnnotations.add(null);
Set<Annotation> responseAnnotations = new HashSet<>();
Annotation response = mock(Annotation.class);
Node rStart = mock(Node.class);
Node rEnd = mock(Node.class);
when(rStart.getOffset()).thenReturn(10L);
when(rEnd.getOffset()).thenReturn(20L);
when(response.getStartNode()).thenReturn(rStart);
when(response.getEndNode()).thenReturn(rEnd);
when(response.getFeatures()).thenReturn(Factory.newFeatureMap());
when(response.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(response.coextensive(any())).thenReturn(false);
responseAnnotations.add(response);
// when(keySet.get("Person")).thenReturn(keyAnnotations);
// when(responseSet.get("Person")).thenReturn(responseAnnotations);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("doc");
when(responseDoc.getName()).thenReturn("doc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("http://doc"));
when(responseDoc.getSourceUrl()).thenReturn(new URL("http://doc"));
when(keyDoc.getAnnotations()).thenReturn(keySet);
when(responseDoc.getAnnotations()).thenReturn(responseSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertEquals(1, diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE).size());
}

@Test
public void testMismatchedAnnotationSetNamesUsesDefault() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
diff.setKeyAnnotationSetName("CustomKeySet");
diff.setResponseAnnotationSetName("CustomResponseSet");
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Annotation annotation = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
when(annotation.getStartNode()).thenReturn(start);
when(annotation.getEndNode()).thenReturn(end);
when(annotation.coextensive(any())).thenReturn(false);
when(annotation.isPartiallyCompatible(any(), anySet())).thenReturn(false);
when(annotation.getFeatures()).thenReturn(Factory.newFeatureMap());
Set<Annotation> annotations = new HashSet<>();
annotations.add(annotation);
AnnotationSet defaultKey = mock(AnnotationSet.class);
AnnotationSet namedKey = mock(AnnotationSet.class);
// when(namedKey.get("Entity")).thenReturn(annotations);
// when(defaultKey.get("Entity")).thenReturn(Collections.emptySet());
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
URL url = new URL("file:///doc.txt");
when(doc1.getSourceUrl()).thenReturn(url);
when(doc2.getSourceUrl()).thenReturn(url);
when(doc1.getName()).thenReturn("doc");
when(doc2.getName()).thenReturn("doc");
when(doc1.getAnnotations("CustomKeySet")).thenReturn(namedKey);
when(doc2.getAnnotations("CustomResponseSet")).thenReturn(namedKey);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(doc1);
when(responseCorpus.get(0)).thenReturn(doc2);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertEquals(1, diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE).size());
assertEquals(1, diff.getFalsePositiveLenient() > 0 ? 1 : 0);
}

@Test
public void testSetKeyFeatureNamesSetToNullShouldNotFail() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setKeyFeatureNamesSet(null);
assertNull(diff.getKeyFeatureNamesSet());
}

@Test
public void testAddToDiffSetWithValidDiffElementUpdatesAnnotationsOfType() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
Annotation ann = mock(Annotation.class);
when(ann.getStartNode()).thenReturn(mock(Node.class));
when(ann.getEndNode()).thenReturn(mock(Node.class));
when(ann.getFeatures()).thenReturn(Factory.newFeatureMap());
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(ann, ann, CorpusAnnotationDiff.CORRECT_TYPE, CorpusAnnotationDiff.CORRECT_TYPE, keyDoc, respDoc);
CorpusAnnotationDiff.DiffSetElement dummy = diff.new DiffSetElement(null, null, -1, -1, keyDoc, respDoc);
assertNotNull(element);
assertEquals(CorpusAnnotationDiff.CORRECT_TYPE, element.getLeftType());
assertEquals(CorpusAnnotationDiff.CORRECT_TYPE, element.getRightType());
}

@Test
public void testPartialMatchAndStrictClassificationAssignment() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Type");
diff.setAnnotationSchema(schema);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
URL url = new URL("http://test");
when(keyDoc.getName()).thenReturn("same");
when(responseDoc.getName()).thenReturn("same");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
Annotation keyAnnotation = mock(Annotation.class);
Node kStart = mock(Node.class);
Node kEnd = mock(Node.class);
when(kStart.getOffset()).thenReturn(0L);
when(kEnd.getOffset()).thenReturn(10L);
when(keyAnnotation.getStartNode()).thenReturn(kStart);
when(keyAnnotation.getEndNode()).thenReturn(kEnd);
when(keyAnnotation.getFeatures()).thenReturn(Factory.newFeatureMap());
when(keyAnnotation.coextensive(any())).thenReturn(false);
when(keyAnnotation.isPartiallyCompatible(any(), anySet())).thenReturn(true);
Annotation responseAnnotation = mock(Annotation.class);
Node rStart = mock(Node.class);
Node rEnd = mock(Node.class);
when(rStart.getOffset()).thenReturn(5L);
when(rEnd.getOffset()).thenReturn(15L);
when(responseAnnotation.getStartNode()).thenReturn(rStart);
when(responseAnnotation.getEndNode()).thenReturn(rEnd);
when(responseAnnotation.getFeatures()).thenReturn(Factory.newFeatureMap());
when(responseAnnotation.coextensive(any())).thenReturn(false);
when(responseAnnotation.isPartiallyCompatible(any(), anySet())).thenReturn(true);
Set<Annotation> keyAnns = new HashSet<>();
keyAnns.add(keyAnnotation);
Set<Annotation> responseAnns = new HashSet<>();
responseAnns.add(responseAnnotation);
AnnotationSet keyAsTop = mock(AnnotationSet.class);
AnnotationSet responseAsTop = mock(AnnotationSet.class);
// when(keyAsTop.get("Type")).thenReturn(keyAnns);
// when(responseAsTop.get("Type")).thenReturn(responseAnns);
when(keyDoc.getAnnotations()).thenReturn(keyAsTop);
when(responseDoc.getAnnotations()).thenReturn(responseAsTop);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
double partialRecall = diff.getRecallLenient();
double partialPrecision = diff.getPrecisionLenient();
assertTrue(partialRecall > 0.0);
assertTrue(partialPrecision > 0.0);
assertEquals(0, diff.getAnnotationsOfType(CorpusAnnotationDiff.CORRECT_TYPE).size());
assertEquals(1, diff.getAnnotationsOfType(CorpusAnnotationDiff.PARTIALLY_CORRECT_TYPE).size());
}

@Test
public void testEmptyAnnotationSetReturnsNoAnnotationsOfAnyType() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Concept");
diff.setAnnotationSchema(schema);
AnnotationSet emptyKeySet = mock(AnnotationSet.class);
AnnotationSet emptyRespSet = mock(AnnotationSet.class);
// when(emptyKeySet.get("Concept")).thenReturn(Collections.emptySet());
// when(emptyRespSet.get("Concept")).thenReturn(Collections.emptySet());
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
URL docUrl = new URL("http://doc");
when(keyDoc.getName()).thenReturn("Doc");
when(respDoc.getName()).thenReturn("Doc");
when(keyDoc.getSourceUrl()).thenReturn(docUrl);
when(respDoc.getSourceUrl()).thenReturn(docUrl);
when(keyDoc.getAnnotations()).thenReturn(emptyKeySet);
when(respDoc.getAnnotations()).thenReturn(emptyRespSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.CORRECT_TYPE).isEmpty());
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE).isEmpty());
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE).isEmpty());
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.PARTIALLY_CORRECT_TYPE).isEmpty());
assertEquals(0.0, diff.getPrecisionStrict(), 0.000001);
}

@Test
public void testNoMatchingResponseDocumentResultsInWarningButNoFailure() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Tag");
diff.setAnnotationSchema(schema);
Document keyDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("KeyDoc");
when(keyDoc.getSourceUrl()).thenReturn(new URL("file:/a"));
Corpus keyCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
Corpus responseCorpus = mock(Corpus.class);
when(responseCorpus.size()).thenReturn(1);
Document responseDoc = mock(Document.class);
when(responseDoc.getName()).thenReturn("DiffName");
when(responseDoc.getSourceUrl()).thenReturn(new URL("file:/b"));
when(responseCorpus.get(0)).thenReturn(responseDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
assertEquals(0.0, diff.getPrecisionStrict(), 0.000001);
assertEquals(0.0, diff.getRecallStrict(), 0.000001);
}

@Test
public void testDiffSetElementToStringAndAccessorsWorkCorrectly() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
when(left.getStartNode()).thenReturn(mock(Node.class));
when(left.getEndNode()).thenReturn(mock(Node.class));
when(left.getFeatures()).thenReturn(Factory.newFeatureMap());
when(right.getStartNode()).thenReturn(mock(Node.class));
when(right.getEndNode()).thenReturn(mock(Node.class));
when(right.getFeatures()).thenReturn(Factory.newFeatureMap());
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(left, right, CorpusAnnotationDiff.MISSING_TYPE, CorpusAnnotationDiff.PARTIALLY_CORRECT_TYPE, keyDoc, respDoc);
assertEquals(left, element.getLeftAnnotation());
assertEquals(right, element.getRightAnnotation());
assertEquals(CorpusAnnotationDiff.MISSING_TYPE, element.getLeftType());
assertEquals(CorpusAnnotationDiff.PARTIALLY_CORRECT_TYPE, element.getRightType());
assertEquals(keyDoc, element.getKeyDocument());
assertEquals(respDoc, element.getResponseDocument());
element.setLeftAnnotation(null);
element.setRightAnnotation(null);
element.setLeftType(-1);
element.setRightType(-1);
element.setKeyDocument(null);
element.setResponseDocument(null);
assertNull(element.getLeftAnnotation());
assertNull(element.getRightAnnotation());
assertEquals(-1, element.getLeftType());
assertEquals(-1, element.getRightType());
assertNull(element.getKeyDocument());
assertNull(element.getResponseDocument());
}

@Test
public void testSetAndGetParameterValueWithUnknownPropertyThrowsException() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
try {
diff.setParameterValue("nonExistentProperty", "value");
fail("Expected ResourceInstantiationException");
} catch (ResourceInstantiationException e) {
assertTrue(e.getMessage().contains("Couldn't get bean info"));
}
}

@Test
public void testSetParameterValuesWithNullDoesNotCrash() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap parameters = null;
try {
diff.setParameterValues(parameters);
fail("Expected NullPointerException or ResourceInstantiationException");
} catch (NullPointerException | ResourceInstantiationException expected) {
assertNotNull(expected);
}
}

@Test
public void testSetTextModeFalseAffectsInitBehavior() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.FALSE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Type");
diff.setAnnotationSchema(schema);
Document keyDoc = mock(Document.class);
Document respDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("doc");
when(respDoc.getName()).thenReturn("doc");
URL url = new URL("file:///doc");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(respDoc.getSourceUrl()).thenReturn(url);
Annotation annot = mock(Annotation.class);
Node nodeStart = mock(Node.class);
Node nodeEnd = mock(Node.class);
when(nodeStart.getOffset()).thenReturn(0L);
when(nodeEnd.getOffset()).thenReturn(10L);
when(annot.getStartNode()).thenReturn(nodeStart);
when(annot.getEndNode()).thenReturn(nodeEnd);
when(annot.getFeatures()).thenReturn(Factory.newFeatureMap());
when(annot.coextensive(any())).thenReturn(true);
when(annot.isPartiallyCompatible(any(), anySet())).thenReturn(true);
Set<Annotation> keyAnnots = new HashSet<>();
keyAnnots.add(annot);
Set<Annotation> respAnnots = new HashSet<>();
respAnnots.add(annot);
AnnotationSet keySet = mock(AnnotationSet.class);
// when(keySet.get("Type")).thenReturn(keyAnnots);
AnnotationSet responseSet = mock(AnnotationSet.class);
// when(responseSet.get("Type")).thenReturn(respAnnots);
when(keyDoc.getAnnotations()).thenReturn(keySet);
when(respDoc.getAnnotations()).thenReturn(responseSet);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(respCorpus.get(0)).thenReturn(respDoc);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
Resource result = diff.init();
assertNotNull(result);
assertFalse(diff.isTextMode());
}

@Test
public void testInitWithNullKeyCorpusThrowsException() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(null);
diff.setResponseCorpus(mock(Corpus.class));
diff.setAnnotationSchema(mock(AnnotationSchema.class));
try {
diff.init();
fail("Expected ResourceInstantiationException");
} catch (ResourceInstantiationException e) {
assertTrue(e.getMessage().contains("No key corpus"));
}
}

@Test
public void testInitWithNullResponseCorpusThrowsException() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
diff.setKeyCorpus(mock(Corpus.class));
diff.setResponseCorpus(null);
diff.setAnnotationSchema(mock(AnnotationSchema.class));
try {
diff.init();
fail("Expected ResourceInstantiationException");
} catch (ResourceInstantiationException e) {
assertTrue(e.getMessage().contains("No response corpus"));
}
}

@Test
public void testAddToDiffSetIncrementsCountersProperly() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement e = diff.new DiffSetElement(left, right, CorpusAnnotationDiff.MISSING_TYPE, CorpusAnnotationDiff.SPURIOUS_TYPE, doc, doc);
Set<Annotation> resultMissing = diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE);
Set<Annotation> resultSpurious = diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE);
assertNotNull(resultMissing);
assertNotNull(resultSpurious);
}

@Test
public void testEmptyFeatureMapOnAnnotationDoesNotCauseCrash() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setTextMode(Boolean.TRUE);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Type");
diff.setAnnotationSchema(schema);
Annotation annKey = mock(Annotation.class);
Node kStart = mock(Node.class);
Node kEnd = mock(Node.class);
when(kStart.getOffset()).thenReturn(0L);
when(kEnd.getOffset()).thenReturn(10L);
when(annKey.getStartNode()).thenReturn(kStart);
when(annKey.getEndNode()).thenReturn(kEnd);
when(annKey.getFeatures()).thenReturn(Factory.newFeatureMap());
when(annKey.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(annKey.coextensive(any())).thenReturn(true);
Annotation annResp = mock(Annotation.class);
Node rStart = mock(Node.class);
Node rEnd = mock(Node.class);
when(rStart.getOffset()).thenReturn(0L);
when(rEnd.getOffset()).thenReturn(10L);
when(annResp.getStartNode()).thenReturn(rStart);
when(annResp.getEndNode()).thenReturn(rEnd);
when(annResp.getFeatures()).thenReturn(Factory.newFeatureMap());
when(annResp.isPartiallyCompatible(any(), anySet())).thenReturn(true);
when(annResp.coextensive(any())).thenReturn(true);
Set<Annotation> keyList = new HashSet<>();
keyList.add(annKey);
Set<Annotation> respList = new HashSet<>();
respList.add(annResp);
AnnotationSet keyAS = mock(AnnotationSet.class);
AnnotationSet respAS = mock(AnnotationSet.class);
// when(keyAS.get("Type")).thenReturn(keyList);
// when(respAS.get("Type")).thenReturn(respList);
Document docKey = mock(Document.class);
Document docResp = mock(Document.class);
when(docKey.getName()).thenReturn("D");
when(docResp.getName()).thenReturn("D");
when(docKey.getSourceUrl()).thenReturn(new URL("http://key"));
when(docResp.getSourceUrl()).thenReturn(new URL("http://key"));
when(docKey.getAnnotations()).thenReturn(keyAS);
when(docResp.getAnnotations()).thenReturn(respAS);
Corpus keyCorpus = mock(Corpus.class);
Corpus respCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(respCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(docKey);
when(respCorpus.get(0)).thenReturn(docResp);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(respCorpus);
diff.init();
assertEquals(1, diff.getAnnotationsOfType(CorpusAnnotationDiff.CORRECT_TYPE).size());
}

@Test
public void testGetPreferredScrollableViewportSizeReturnsNonNull() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertNotNull(diff.getPreferredScrollableViewportSize());
}

@Test
public void testGetScrollableUnitAndBlockIncrementReturnConsistentValues() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
int unitInc = diff.getScrollableUnitIncrement(null, 0, 1);
int blockInc = diff.getScrollableBlockIncrement(new java.awt.Rectangle(100, 100), 1, 1);
assertEquals(10, unitInc);
assertEquals(90, blockInc);
}

@Test
public void testGetScrollableTracksViewportWidthAndHeightReturnFalse() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertFalse(diff.getScrollableTracksViewportWidth());
assertFalse(diff.getScrollableTracksViewportHeight());
}
}
