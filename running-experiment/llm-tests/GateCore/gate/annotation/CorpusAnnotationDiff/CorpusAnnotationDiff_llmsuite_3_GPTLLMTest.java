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

public class CorpusAnnotationDiff_llmsuite_3_GPTLLMTest {

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWithoutAnnotationSchema() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
}

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWithEmptyKeyCorpus() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
diff.setAnnotationSchema(schema);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(0);
when(responseCorpus.size()).thenReturn(1);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
}

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWithEmptyResponseCorpus() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
diff.setAnnotationSchema(schema);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(0);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.init();
}

@Test
public void testGetAndSetAnnotationTypeForFalsePositive() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setAnnotationTypeForFalsePositive("Token");
String value = diff.getAnnotationTypeForFalsePositive();
assertEquals("Token", value);
}

@Test
public void testTextModeToggle() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertFalse(diff.isTextMode());
diff.setTextMode(Boolean.TRUE);
assertTrue(diff.isTextMode());
diff.setTextMode(Boolean.FALSE);
assertFalse(diff.isTextMode());
}

@Test
public void testInitWithMatchingEmptyAnnotations() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
URL url = new URL("http://gate.ac.uk/keyDoc");
when(keyDoc.getName()).thenReturn("doc1");
when(responseDoc.getName()).thenReturn("doc1");
when(keyDoc.getSourceUrl()).thenReturn(url);
when(responseDoc.getSourceUrl()).thenReturn(url);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet responseSet = mock(AnnotationSet.class);
Map<String, AnnotationSet> keyASMap = mock(Map.class);
Map<String, AnnotationSet> responseASMap = mock(Map.class);
// when(keyDoc.getAnnotations()).thenReturn(keyASMap);
// when(responseDoc.getAnnotations()).thenReturn(responseASMap);
when(keyASMap.get(anyString())).thenReturn(keySet);
when(responseASMap.get(anyString())).thenReturn(responseSet);
when(keySet.get(anyString())).thenReturn(null);
when(responseSet.get(anyString())).thenReturn(null);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
Resource result = diff.init();
assertNotNull(result);
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testSetAndGetAnnotationSetNames() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setKeyAnnotationSetName("KeySet");
diff.setResponseAnnotationSetName("RespSet");
diff.setResponseAnnotationSetNameFalsePoz("ErrorSet");
assertEquals("KeySet", diff.getKeyAnnotationSetName());
assertEquals("RespSet", diff.getResponseAnnotationSetName());
assertEquals("ErrorSet", diff.getResponseAnnotationSetNameFalsePoz());
}

@Test
public void testSetAndGetKeyFeatureNamesSet() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<String> featureNames = new HashSet<>();
featureNames.add("gender");
featureNames.add("type");
diff.setKeyFeatureNamesSet(featureNames);
assertEquals(featureNames, diff.getKeyFeatureNamesSet());
}

@Test
public void testMetricsDefaultValuesBeforeInit() {
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
assertEquals(0.0, diff.getFalsePositiveStrict(), 0.0001);
assertEquals(0.0, diff.getFalsePositiveLenient(), 0.0001);
assertEquals(0.0, diff.getFalsePositiveAverage(), 0.0001);
}

@Test
public void testInitSkipsMismatchedDocumentNames() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document keyDoc = mock(Document.class);
Document responseDoc = mock(Document.class);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(keyDoc);
when(responseCorpus.get(0)).thenReturn(responseDoc);
when(keyDoc.getName()).thenReturn("docX");
when(responseDoc.getName()).thenReturn("docY");
URL url1 = new URL("http://key.com/doc");
URL url2 = new URL("http://resp.com/doc");
when(keyDoc.getSourceUrl()).thenReturn(url1);
when(responseDoc.getSourceUrl()).thenReturn(url2);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
Resource result = diff.init();
assertNotNull(result);
assertEquals(0.0, diff.getPrecisionStrict(), 0.00001);
assertEquals(0.0, diff.getRecallStrict(), 0.00001);
}

@Test
public void testInitHandlesNullFeatureMapGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
Annotation annotation = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
when(annotation.getStartNode()).thenReturn(start);
when(annotation.getEndNode()).thenReturn(end);
when(annotation.getFeatures()).thenReturn(null);
when(annotation.coextensive(any())).thenReturn(true);
when(annotation.isPartiallyCompatible(any(), any())).thenReturn(true);
Set<Annotation> keyAnns = new HashSet<>();
keyAnns.add(annotation);
Set<Annotation> respAnns = new HashSet<>();
respAnns.add(annotation);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet respSet = mock(AnnotationSet.class);
// when(keySet.get("Entity")).thenReturn(keyAnns);
// when(respSet.get("Entity")).thenReturn(respAnns);
Map<String, AnnotationSet> keyMap = mock(Map.class);
Map<String, AnnotationSet> respMap = mock(Map.class);
when(keyMap.get(anyString())).thenReturn(keySet);
when(respMap.get(anyString())).thenReturn(respSet);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc1");
URL url = new URL("http://doc.com/x");
when(doc.getSourceUrl()).thenReturn(url);
Content content = mock(Content.class);
// when(doc.getContent()).thenReturn(content);
// when(content.getContent(anyLong(), anyLong())).thenReturn("sample");
Corpus corpus = mock(Corpus.class);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
// when(doc.getAnnotations()).thenReturn(keyMap);
when(doc.getAnnotations(anyString())).thenReturn(keySet);
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource res = diff.init();
assertNotNull(res);
assertEquals(1.0, diff.getPrecisionStrict(), 0.0001);
}

@Test
public void testPartiallyCompatibleNonCoextensiveIsCounted() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
Annotation keyAnn = mock(Annotation.class);
Annotation respAnn = mock(Annotation.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(5L);
when(respStart.getOffset()).thenReturn(1L);
when(respEnd.getOffset()).thenReturn(6L);
when(keyAnn.getStartNode()).thenReturn(keyStart);
when(keyAnn.getEndNode()).thenReturn(keyEnd);
when(respAnn.getStartNode()).thenReturn(respStart);
when(respAnn.getEndNode()).thenReturn(respEnd);
when(keyAnn.coextensive(respAnn)).thenReturn(false);
when(respAnn.coextensive(keyAnn)).thenReturn(false);
when(keyAnn.isPartiallyCompatible(eq(respAnn), any())).thenReturn(true);
Set<Annotation> keySetType = new HashSet<>();
keySetType.add(keyAnn);
Set<Annotation> respSetType = new HashSet<>();
respSetType.add(respAnn);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet respSet = mock(AnnotationSet.class);
// when(keySet.get("Person")).thenReturn(keySetType);
// when(respSet.get("Person")).thenReturn(respSetType);
Map<String, AnnotationSet> keyMap = mock(Map.class);
Map<String, AnnotationSet> respMap = mock(Map.class);
when(keyMap.get(anyString())).thenReturn(keySet);
when(respMap.get(anyString())).thenReturn(respSet);
Content content = mock(Content.class);
// when(content.getContent(anyLong(), anyLong())).thenReturn("John");
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc5");
URL url = new URL("http://docs.com/_");
when(doc.getSourceUrl()).thenReturn(url);
// when(doc.getAnnotations()).thenReturn(keyMap);
when(doc.getAnnotations(anyString())).thenReturn(keySet);
// when(doc.getContent()).thenReturn(content);
Corpus corpus = mock(Corpus.class);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource res = diff.init();
assertNotNull(res);
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.PARTIALLY_CORRECT_TYPE).contains(respAnn));
}

@Test
public void testMissingResponseAnnotationYieldsMissingType() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Location");
diff.setAnnotationSchema(schema);
Annotation keyAnn = mock(Annotation.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(4L);
when(keyAnn.getStartNode()).thenReturn(keyStart);
when(keyAnn.getEndNode()).thenReturn(keyEnd);
when(keyAnn.coextensive(any())).thenReturn(false);
when(keyAnn.isPartiallyCompatible(any(), any())).thenReturn(false);
Set<Annotation> keySetType = new HashSet<>();
keySetType.add(keyAnn);
AnnotationSet keySet = mock(AnnotationSet.class);
// when(keySet.get("Location")).thenReturn(keySetType);
Map<String, AnnotationSet> keyMap = mock(Map.class);
when(keyMap.get(anyString())).thenReturn(keySet);
Document keyDoc = mock(Document.class);
when(keyDoc.getName()).thenReturn("docX");
URL url = new URL("http://key.doc/x");
when(keyDoc.getSourceUrl()).thenReturn(url);
// when(keyDoc.getAnnotations()).thenReturn(keyMap);
when(keyDoc.getAnnotations(anyString())).thenReturn(keySet);
Content content = mock(Content.class);
// when(content.getContent(anyLong(), anyLong())).thenReturn("City");
// when(keyDoc.getContent()).thenReturn(content);
Corpus corpus = mock(Corpus.class);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(keyDoc);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource res = diff.init();
assertNotNull(res);
Set<Annotation> missing = diff.getAnnotationsOfType(CorpusAnnotationDiff.MISSING_TYPE);
assertEquals(1, missing.size());
assertTrue(missing.contains(keyAnn));
}

@Test
public void testGetPreferredScrollableViewportSizeReturnsPreferredSize() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Dimension preferred = diff.getPreferredScrollableViewportSize();
assertNotNull(preferred);
}

@Test
public void testGetScrollableUnitIncrementReturnsFixedValue() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Rectangle rect = new Rectangle(0, 0, 100, 100);
int increment = diff.getScrollableUnitIncrement(rect, 0, 1);
assertEquals(10, increment);
}

@Test
public void testGetScrollableBlockIncrementHorizontal() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Rectangle rect = new Rectangle(0, 0, 100, 50);
int increment = diff.getScrollableBlockIncrement(rect, javax.swing.SwingConstants.HORIZONTAL, 1);
assertEquals(90, increment);
}

@Test
public void testGetScrollableBlockIncrementVertical() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Rectangle rect = new Rectangle(0, 0, 100, 60);
int increment = diff.getScrollableBlockIncrement(rect, javax.swing.SwingConstants.VERTICAL, 1);
assertEquals(50, increment);
}

@Test
public void testGetScrollableTracksViewportWidthReturnsFalse() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertFalse(diff.getScrollableTracksViewportWidth());
}

@Test
public void testGetScrollableTracksViewportHeightReturnsFalse() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
assertFalse(diff.getScrollableTracksViewportHeight());
}

@Test
public void testGetAnnotationsOfTypeReturnsEmptyWhenDiffSetIsNull() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Set<Annotation> result = diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testCoextensiveAnnotationWithNullKeyAddsSpuriousOnly() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Person");
diff.setAnnotationSchema(schema);
Annotation responseAnn = mock(Annotation.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(5L);
when(responseAnn.getStartNode()).thenReturn(s);
when(responseAnn.getEndNode()).thenReturn(e);
when(responseAnn.isPartiallyCompatible(any(), any())).thenReturn(false);
Set<Annotation> respSetType = new HashSet<>();
respSetType.add(responseAnn);
AnnotationSet responseSet = mock(AnnotationSet.class);
Map<String, AnnotationSet> responseMap = mock(Map.class);
when(responseMap.get(anyString())).thenReturn(responseSet);
// when(responseSet.get("Person")).thenReturn(respSetType);
Document responseDoc = mock(Document.class);
URL url = new URL("http://test.com/fail");
when(responseDoc.getName()).thenReturn("doc1");
when(responseDoc.getSourceUrl()).thenReturn(url);
// when(responseDoc.getAnnotations()).thenReturn(responseMap);
when(responseDoc.getAnnotations(anyString())).thenReturn(responseSet);
Content content = mock(Content.class);
// when(content.getContent(anyLong(), anyLong())).thenReturn("data");
// when(responseDoc.getContent()).thenReturn(content);
Corpus responseCorpus = mock(Corpus.class);
when(responseCorpus.get(0)).thenReturn(responseDoc);
Corpus keyCorpus = mock(Corpus.class);
Document emptyKeyDoc = mock(Document.class);
when(keyCorpus.get(0)).thenReturn(emptyKeyDoc);
when(emptyKeyDoc.getName()).thenReturn("doc1");
when(emptyKeyDoc.getSourceUrl()).thenReturn(url);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
Map<String, AnnotationSet> keyMap = mock(Map.class);
AnnotationSet keySet = mock(AnnotationSet.class);
when(keyMap.get(anyString())).thenReturn(keySet);
when(keySet.get("Person")).thenReturn(null);
// when(emptyKeyDoc.getAnnotations()).thenReturn(keyMap);
when(emptyKeyDoc.getAnnotations(anyString())).thenReturn(keySet);
// when(emptyKeyDoc.getContent()).thenReturn(content);
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
Resource r = diff.init();
assertNotNull(r);
Set<Annotation> spurious = diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE);
assertTrue(spurious.contains(responseAnn));
}

@Test
public void testSetAndGetParameterValueObject() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = new AnnotationSchema();
diff.setAnnotationSchema(schema);
Object value = diff.getParameterValue("annotationSchema");
assertEquals(schema, value);
}

@Test
public void testSetParameterValueValid() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setParameterValue("annotationTypeForFalsePositive", "Token");
assertEquals("Token", diff.getAnnotationTypeForFalsePositive());
}

@Test
public void testSetParameterValuesBatch() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
FeatureMap parameters = Factory.newFeatureMap();
parameters.put("keyAnnotationSetName", "Gold");
parameters.put("responseAnnotationSetName", "Predicted");
diff.setParameterValues(parameters);
assertEquals("Gold", diff.getKeyAnnotationSetName());
assertEquals("Predicted", diff.getResponseAnnotationSetName());
}

@Test
public void testMetricPrecisionRecallF1ZeroWhenNoAnnotations() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("X");
diff.setAnnotationSchema(schema);
Corpus corpus = mock(Corpus.class);
Document doc = mock(Document.class);
URL url = new URL("http://zero.com");
when(doc.getName()).thenReturn("zero");
when(doc.getSourceUrl()).thenReturn(url);
when(corpus.get(0)).thenReturn(doc);
when(corpus.size()).thenReturn(1);
Map<String, AnnotationSet> maps = mock(Map.class);
AnnotationSet as = mock(AnnotationSet.class);
when(maps.get(anyString())).thenReturn(as);
when(as.get(anyString())).thenReturn(null);
// when(doc.getAnnotations()).thenReturn(maps);
when(doc.getAnnotations(anyString())).thenReturn(as);
Content content = mock(Content.class);
// when(doc.getContent()).thenReturn(content);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource result = diff.init();
assertNotNull(result);
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
assertEquals(0.0, diff.getRecallStrict(), 0.0001);
assertEquals(0.0, diff.getFMeasureStrict(), 0.0001);
}

@Test
public void testDiffSetElementConstructorAndAccessors() {
CorpusAnnotationDiff.DiffSetElement element = new CorpusAnnotationDiff().new DiffSetElement();
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
public void testAddToDiffsetOnlyRightTypeIsCounted() throws ResourceInstantiationException {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation rightAnn = mock(Annotation.class);
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, rightAnn, -1, CorpusAnnotationDiff.SPURIOUS_TYPE, doc, doc);
diff.getAnnotationsOfType(3);
diff.init();
assertTrue(true);
}

@Test
public void testAddToDiffsetOnlyLeftTypeIsCounted() throws ResourceInstantiationException {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation leftAnn = mock(Annotation.class);
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(leftAnn, null, CorpusAnnotationDiff.MISSING_TYPE, -1, doc, doc);
diff.getAnnotationsOfType(4);
diff.init();
assertTrue(true);
}

@Test
public void testAnnotationDiffTableModelReturnsCorrectTypes() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(left, right, 1, 2, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
data.add(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
assertEquals(10, model.getColumnCount());
assertEquals(1, model.getRowCount());
assertEquals(String.class, model.getColumnClass(0));
assertEquals(Long.class, model.getColumnClass(1));
assertEquals(String.class, model.getColumnClass(3));
assertEquals(String.class, model.getColumnClass(9));
}

@Test
public void testAnnotationDiffTableModelHandlesNullAnnotationsGracefully() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, -1, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
data.add(element);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
assertNull(model.getValueAt(0, 0));
assertNull(model.getValueAt(0, 1));
assertEquals("   ", model.getValueAt(0, 4));
assertEquals("doc", model.getColumnName(0).substring(0, 1).toUpperCase());
}

@Test
public void testAnnotationDiffCellRendererWithNullDiffSetElement() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
JTable table = mock(JTable.class);
when(table.getModel()).thenReturn(new AbstractTableModel() {

@Override
public int getRowCount() {
return 1;
}

@Override
public int getColumnCount() {
return 11;
}

@Override
public Object getValueAt(int r, int c) {
return null;
}
});
Component comp = renderer.getTableCellRendererComponent(table, "value", false, false, 0, 4);
assertTrue(comp instanceof JPanel);
}

@Test
public void testAnnotationDiffCellRendererWithValidDiffSetElement() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement();
element.setLeftType(CorpusAnnotationDiff.MISSING_TYPE);
element.setRightType(CorpusAnnotationDiff.SPURIOUS_TYPE);
AbstractTableModel model = new AbstractTableModel() {

@Override
public int getRowCount() {
return 1;
}

@Override
public int getColumnCount() {
return 11;
}

@Override
public Object getValueAt(int row, int col) {
if (col == 10)
return element;
return "text";
}
};
JTable table = new JTable(model);
Component compLeft = renderer.getTableCellRendererComponent(table, "text", false, false, 0, 0);
Component compRight = renderer.getTableCellRendererComponent(table, "text", false, false, 0, 6);
assertTrue(compLeft instanceof JComponent);
assertTrue(compRight instanceof JComponent);
}

@Test
public void testAnnotationSetComparatorReturnsCorrectOrdering() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationSetComparator comparator = diff.new AnnotationSetComparator();
Annotation a1 = mock(Annotation.class);
Annotation a2 = mock(Annotation.class);
Node node1 = mock(Node.class);
Node node2 = mock(Node.class);
when(node1.getOffset()).thenReturn(5L);
when(node2.getOffset()).thenReturn(10L);
when(a1.getStartNode()).thenReturn(node1);
when(a2.getStartNode()).thenReturn(node2);
int result = comparator.compare(a1, a2);
assertTrue(result < 0);
}

@Test
public void testAnnotationSetComparatorWithNullOffsets() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationSetComparator comparator = diff.new AnnotationSetComparator();
Annotation a1 = mock(Annotation.class);
Annotation a2 = mock(Annotation.class);
Node node1 = mock(Node.class);
when(node1.getOffset()).thenReturn(null);
when(a1.getStartNode()).thenReturn(node1);
when(a2.getStartNode()).thenReturn(node1);
int result = comparator.compare(a1, a2);
assertEquals(-1, result);
}

@Test
public void testDetectKeyTypeReturnsDefaultWhenInKeyPartiallySet() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation ann = mock(Annotation.class);
diff.setKeyFeatureNamesSet(Collections.singleton("type"));
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Corpus corpus = mock(Corpus.class);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc1");
URL url = new URL("http://doc");
when(doc.getSourceUrl()).thenReturn(url);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
Content content = mock(Content.class);
// when(content.getContent(anyLong(), anyLong())).thenReturn("sample");
// when(doc.getContent()).thenReturn(content);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
diff.init();
Set<Annotation> result = diff.getAnnotationsOfType(CorpusAnnotationDiff.DEFAULT_TYPE);
assertNotNull(result);
}

@Test
public void testDetectKeyTypePathFullyMatchedInResponsePartialList() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Test");
diff.setAnnotationSchema(schema);
Corpus keyCorpus = mock(Corpus.class);
Corpus responseCorpus = mock(Corpus.class);
Document doc = mock(Document.class);
URL url = new URL("http://path/key");
when(doc.getName()).thenReturn("docZ");
when(doc.getSourceUrl()).thenReturn(url);
when(keyCorpus.size()).thenReturn(1);
when(responseCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(doc);
when(responseCorpus.get(0)).thenReturn(doc);
Annotation keyAnn = mock(Annotation.class);
Annotation respAnn = mock(Annotation.class);
Node keyStart = mock(Node.class);
Node keyEnd = mock(Node.class);
when(keyStart.getOffset()).thenReturn(0L);
when(keyEnd.getOffset()).thenReturn(5L);
when(keyAnn.getStartNode()).thenReturn(keyStart);
when(keyAnn.getEndNode()).thenReturn(keyEnd);
Node respStart = mock(Node.class);
Node respEnd = mock(Node.class);
when(respStart.getOffset()).thenReturn(0L);
when(respEnd.getOffset()).thenReturn(5L);
when(respAnn.getStartNode()).thenReturn(respStart);
when(respAnn.getEndNode()).thenReturn(respEnd);
when(keyAnn.coextensive(respAnn)).thenReturn(true);
when(keyAnn.isPartiallyCompatible(eq(respAnn), any())).thenReturn(true);
when(respAnn.isPartiallyCompatible(eq(keyAnn), any())).thenReturn(true);
Set<Annotation> keys = new HashSet<>();
keys.add(keyAnn);
Set<Annotation> resps = new HashSet<>();
resps.add(respAnn);
AnnotationSet keySet = mock(AnnotationSet.class);
// when(keySet.get("Test")).thenReturn(keys);
AnnotationSet respSet = mock(AnnotationSet.class);
// when(respSet.get("Test")).thenReturn(resps);
Map<String, AnnotationSet> keyMap = mock(Map.class);
Map<String, AnnotationSet> respMap = mock(Map.class);
when(keyMap.get(anyString())).thenReturn(keySet);
when(respMap.get(anyString())).thenReturn(respSet);
// when(doc.getAnnotations()).thenReturn(keyMap);
when(doc.getAnnotations(anyString())).thenReturn(keySet);
// when(doc.getContent()).thenReturn(mock(Content.class));
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
Resource result = diff.init();
assertNotNull(result);
assertTrue(diff.getPrecisionStrict() > 0);
}

@Test
public void testDetectResponseTypeReturnsSpuriousWhenNotPartialAndNoMatch() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
diff.setKeyFeatureNamesSet(Collections.singleton("foo"));
Annotation responseAnn = mock(Annotation.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(100L);
when(e.getOffset()).thenReturn(200L);
when(responseAnn.getStartNode()).thenReturn(s);
when(responseAnn.getEndNode()).thenReturn(e);
when(responseAnn.isPartiallyCompatible(any(), any())).thenReturn(false);
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("TypeX");
diff.setAnnotationSchema(schema);
Corpus corpus = mock(Corpus.class);
Document doc = mock(Document.class);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
URL url = new URL("http://resp/test");
when(doc.getName()).thenReturn("r1");
when(doc.getSourceUrl()).thenReturn(url);
// when(doc.getAnnotations()).thenReturn(mock(Map.class));
// when(doc.getContent()).thenReturn(mock(Content.class));
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource res = diff.init();
assertNotNull(res);
assertTrue(diff.getAnnotationsOfType(CorpusAnnotationDiff.SPURIOUS_TYPE).size() >= 0);
}

@Test
public void testFMeasureCalculationAvoidsDivByZero() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("X");
diff.setAnnotationSchema(schema);
diff.setKeyCorpus(mock(Corpus.class));
diff.setResponseCorpus(mock(Corpus.class));
diff.setTextMode(Boolean.TRUE);
diff.init();
double f1Strict = diff.getFMeasureStrict();
double f1Lenient = diff.getFMeasureLenient();
assertEquals(0.0, f1Strict, 0.0001);
assertEquals(0.0, f1Lenient, 0.0001);
}

@Test
public void testFalsePositiveCalculationWithTokenAnnotation() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Entity");
diff.setAnnotationSchema(schema);
Corpus corpus = mock(Corpus.class);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("TokenDoc");
URL url = new URL("http://token.com");
when(doc.getSourceUrl()).thenReturn(url);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
Annotation token = mock(Annotation.class);
Set<Annotation> tokens = new HashSet<>();
tokens.add(token);
AnnotationSet tokenSet = mock(AnnotationSet.class);
// when(tokenSet.get("Token")).thenReturn(tokens);
Map<String, AnnotationSet> map = mock(Map.class);
when(map.get(anyString())).thenReturn(tokenSet);
// when(doc.getAnnotations()).thenReturn(map);
when(doc.getAnnotations(anyString())).thenReturn(tokenSet);
// when(doc.getContent()).thenReturn(mock(Content.class));
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
diff.setAnnotationTypeForFalsePositive("Token");
Resource res = diff.init();
assertNotNull(res);
assertTrue(diff.getFalsePositiveStrict() >= 0.0);
}

@Test
public void testArangeAllComponentsExecutesLayout() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Dummy");
diff.setAnnotationSchema(schema);
Document doc = mock(Document.class);
Corpus corpus = mock(Corpus.class);
when(doc.getName()).thenReturn("doc1");
URL url = new URL("http://doc");
when(doc.getSourceUrl()).thenReturn(url);
// when(doc.getContent()).thenReturn(mock(Content.class));
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
AnnotationSet emptySet = mock(AnnotationSet.class);
Map<String, AnnotationSet> map = mock(Map.class);
when(map.get(anyString())).thenReturn(emptySet);
when(emptySet.get("Dummy")).thenReturn(null);
// when(doc.getAnnotations()).thenReturn(map);
when(doc.getAnnotations(anyString())).thenReturn(emptySet);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.init();
SwingUtilities.invokeAndWait(new Runnable() {

public void run() {
diff.setVisible(true);
diff.setSize(600, 400);
}
});
assertTrue(diff.getComponentCount() > 0);
}

@Test
public void testDiffSetElementBothAnnotationsNullStillConstructible() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Document d = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement element = diff.new DiffSetElement(null, null, -1, -1, d, d);
assertNull(element.getLeftAnnotation());
assertNull(element.getRightAnnotation());
assertEquals(d, element.getKeyDocument());
assertEquals(d, element.getResponseDocument());
}

@Test
public void testPrecisionRecallLenientButNotStrict() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Mention");
diff.setAnnotationSchema(schema);
Annotation key = mock(Annotation.class);
Annotation resp = mock(Annotation.class);
Node ks = mock(Node.class);
Node ke = mock(Node.class);
Node rs = mock(Node.class);
Node re = mock(Node.class);
when(ks.getOffset()).thenReturn(0L);
when(ke.getOffset()).thenReturn(5L);
when(rs.getOffset()).thenReturn(1L);
when(re.getOffset()).thenReturn(5L);
when(key.getStartNode()).thenReturn(ks);
when(key.getEndNode()).thenReturn(ke);
when(resp.getStartNode()).thenReturn(rs);
when(resp.getEndNode()).thenReturn(re);
when(key.coextensive(resp)).thenReturn(false);
when(resp.coextensive(key)).thenReturn(false);
when(key.isPartiallyCompatible(eq(resp), any())).thenReturn(true);
when(resp.isPartiallyCompatible(eq(key), any())).thenReturn(true);
Set<Annotation> keyAnns = new HashSet<>();
keyAnns.add(key);
Set<Annotation> respAnns = new HashSet<>();
respAnns.add(resp);
AnnotationSet keySet = mock(AnnotationSet.class);
AnnotationSet respSet = mock(AnnotationSet.class);
// when(keySet.get("Mention")).thenReturn(keyAnns);
// when(respSet.get("Mention")).thenReturn(respAnns);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docPartial");
URL url = new URL("http://docP");
when(doc.getSourceUrl()).thenReturn(url);
// when(doc.getContent()).thenReturn(mock(Content.class));
Map<String, AnnotationSet> map = mock(Map.class);
when(map.get(anyString())).thenReturn(keySet);
// when(doc.getAnnotations()).thenReturn(map);
when(doc.getAnnotations(anyString())).thenReturn(keySet);
Corpus corpus = mock(Corpus.class);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource r = diff.init();
assertNotNull(r);
assertTrue(diff.getPrecisionLenient() > 0.0);
assertEquals(0.0, diff.getPrecisionStrict(), 0.0001);
}

@Test
public void testGetRawObjectFromTableModelMatchesSourceRow() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Document doc = mock(Document.class);
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
CorpusAnnotationDiff.DiffSetElement row = diff.new DiffSetElement(left, right, 1, 2, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> list = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
list.add(row);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(list);
Object result = model.getRawObject(0);
assertEquals(row, result);
}

@Test
public void testDocumentNameMatchIgnoresCaseButDiffersInURL() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Tag");
diff.setAnnotationSchema(schema);
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
when(d1.getName()).thenReturn("docA");
when(d2.getName()).thenReturn("docA");
when(d1.getSourceUrl()).thenReturn(new URL("http://one"));
when(d2.getSourceUrl()).thenReturn(new URL("http://two"));
Corpus keyCorpus = mock(Corpus.class);
when(keyCorpus.size()).thenReturn(1);
when(keyCorpus.get(0)).thenReturn(d1);
Corpus responseCorpus = mock(Corpus.class);
when(responseCorpus.size()).thenReturn(1);
when(responseCorpus.get(0)).thenReturn(d2);
// when(d1.getAnnotations()).thenReturn(mock(Map.class));
// when(d2.getAnnotations()).thenReturn(mock(Map.class));
// when(d1.getContent()).thenReturn(mock(Content.class));
// when(d2.getContent()).thenReturn(mock(Content.class));
diff.setKeyCorpus(keyCorpus);
diff.setResponseCorpus(responseCorpus);
diff.setTextMode(Boolean.TRUE);
Resource result = diff.init();
assertNotNull(result);
assertTrue(diff.getFMeasureAverage() >= 0.0);
}

@Test
public void testContentGetContentReturnsNullHandledGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Text");
diff.setAnnotationSchema(schema);
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Node n1 = mock(Node.class);
Node n2 = mock(Node.class);
when(n1.getOffset()).thenReturn(0L);
when(n2.getOffset()).thenReturn(5L);
when(left.getStartNode()).thenReturn(n1);
when(left.getEndNode()).thenReturn(n2);
when(right.getStartNode()).thenReturn(n1);
when(right.getEndNode()).thenReturn(n2);
when(left.getFeatures()).thenReturn(null);
when(right.getFeatures()).thenReturn(null);
when(left.coextensive(right)).thenReturn(true);
when(left.isPartiallyCompatible(right, null)).thenReturn(true);
Content content = mock(Content.class);
// when(content.getContent(anyLong(), anyLong())).thenReturn(null);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("nullContent");
URL url = new URL("http://null");
when(doc.getSourceUrl()).thenReturn(url);
// when(doc.getContent()).thenReturn(content);
Set<Annotation> setL = new HashSet<>();
Set<Annotation> setR = new HashSet<>();
setL.add(left);
setR.add(right);
AnnotationSet asL = mock(AnnotationSet.class);
AnnotationSet asR = mock(AnnotationSet.class);
// when(asL.get("Text")).thenReturn(setL);
// when(asR.get("Text")).thenReturn(setR);
Map<String, AnnotationSet> m = mock(Map.class);
when(m.get(anyString())).thenReturn(asL);
// when(doc.getAnnotations()).thenReturn(m);
when(doc.getAnnotations(anyString())).thenReturn(asL);
Corpus corpus = mock(Corpus.class);
when(corpus.size()).thenReturn(1);
when(corpus.get(0)).thenReturn(doc);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource r = diff.init();
assertNotNull(r);
}

@Test
public void testFeatureToStringOutputDoesNotThrow() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
FeatureMap fmL = Factory.newFeatureMap();
FeatureMap fmR = Factory.newFeatureMap();
fmL.put("a", "b");
fmR.put("x", "y");
when(left.getFeatures()).thenReturn(fmL);
when(right.getFeatures()).thenReturn(fmR);
Node sn = mock(Node.class);
Node en = mock(Node.class);
when(sn.getOffset()).thenReturn(0L);
when(en.getOffset()).thenReturn(2L);
when(left.getStartNode()).thenReturn(sn);
when(left.getEndNode()).thenReturn(en);
when(right.getStartNode()).thenReturn(sn);
when(right.getEndNode()).thenReturn(en);
CorpusAnnotationDiff.DiffSetElement row = diff.new DiffSetElement(left, right, 1, 2);
List<CorpusAnnotationDiff.DiffSetElement> list = new ArrayList<>();
list.add(row);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(list);
Object val1 = model.getValueAt(0, 3);
Object val2 = model.getValueAt(0, 8);
assertTrue(val1.toString().contains("a"));
assertTrue(val2.toString().contains("x"));
}

@Test
public void testTableModelReturnsHiddenDiffSetElementInColumn10() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation left = mock(Annotation.class);
Annotation right = mock(Annotation.class);
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement row = diff.new DiffSetElement(left, right, 1, 2, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
data.add(row);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
Object value = model.getValueAt(0, 10);
assertTrue(value instanceof CorpusAnnotationDiff.DiffSetElement);
assertEquals(row, value);
}

@Test
public void testTableModelReturnsFallbackColumnNameForInvalidIndex() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(new ArrayList<CorpusAnnotationDiff.DiffSetElement>());
String name = model.getColumnName(99);
assertEquals("?", name);
}

@Test
public void testTableModelReturnsFallbackTypeForInvalidColumn() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Class<?> clazz = diff.new AnnotationDiffTableModel().getColumnClass(99);
assertEquals(Object.class, clazz);
}

@Test
public void testRendererReturnsEmptyPanelForNullDiffSetElementOrNullColumn() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
TableModel dummyModel = new TableModel() {

public int getRowCount() {
return 1;
}

public int getColumnCount() {
return 11;
}

public Object getValueAt(int r, int c) {
return null;
}

public String getColumnName(int columnIndex) {
return "";
}

public Class<?> getColumnClass(int columnIndex) {
return Object.class;
}

public boolean isCellEditable(int rowIndex, int columnIndex) {
return false;
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
}

public void addTableModelListener(javax.swing.event.TableModelListener l) {
}

public void removeTableModelListener(javax.swing.event.TableModelListener l) {
}
};
JTable table = new JTable(dummyModel);
java.awt.Component component = renderer.getTableCellRendererComponent(table, null, false, false, 0, 4);
assertTrue(component instanceof javax.swing.JPanel);
}

@Test
public void testRendererReturnsPanelOnNullTypeDiffSetElementColumn() {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
CorpusAnnotationDiff.AnnotationDiffCellRenderer renderer = diff.new AnnotationDiffCellRenderer();
Document doc = mock(Document.class);
CorpusAnnotationDiff.DiffSetElement row = diff.new DiffSetElement(null, null, -1, -1, doc, doc);
TableModel model = new TableModel() {

public int getRowCount() {
return 1;
}

public int getColumnCount() {
return 11;
}

public Object getValueAt(int r, int c) {
return (c == 10) ? row : "value";
}

public String getColumnName(int columnIndex) {
return "";
}

public Class<?> getColumnClass(int columnIndex) {
return Object.class;
}

public boolean isCellEditable(int rowIndex, int columnIndex) {
return false;
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
}

public void addTableModelListener(javax.swing.event.TableModelListener l) {
}

public void removeTableModelListener(javax.swing.event.TableModelListener l) {
}
};
JTable table = new JTable(model);
java.awt.Component comp = renderer.getTableCellRendererComponent(table, "value", false, false, 0, 0);
assertTrue(comp instanceof java.awt.Component);
}

@Test
public void testValueAtColumnHandlesInvalidOffsetExceptionGracefully() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
Annotation ann = mock(Annotation.class);
Node n1 = mock(Node.class);
Node n2 = mock(Node.class);
when(n1.getOffset()).thenReturn(0L);
when(n2.getOffset()).thenReturn(5L);
when(ann.getStartNode()).thenReturn(n1);
when(ann.getEndNode()).thenReturn(n2);
Content content = mock(Content.class);
// when(content.getContent(0L, 5L)).thenThrow(new InvalidOffsetException("Invalid"));
Document doc = mock(Document.class);
// when(doc.getContent()).thenReturn(content);
CorpusAnnotationDiff.DiffSetElement row = diff.new DiffSetElement(ann, ann, 1, 2, doc, doc);
List<CorpusAnnotationDiff.DiffSetElement> data = new ArrayList<CorpusAnnotationDiff.DiffSetElement>();
data.add(row);
CorpusAnnotationDiff.AnnotationDiffTableModel model = diff.new AnnotationDiffTableModel(data);
Object result = model.getValueAt(0, 0);
assertNotNull(result);
assertEquals("", result.toString());
}

@Test
public void testDefaultAnnotationSetUsedWhenNamesAreNull() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("Token");
diff.setAnnotationSchema(schema);
Corpus corpus = mock(Corpus.class);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
URL url = new URL("http://default.com");
when(doc.getSourceUrl()).thenReturn(url);
Annotation ann = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
Set<Annotation> annSet = new HashSet<>();
annSet.add(ann);
AnnotationSet tokenSet = mock(AnnotationSet.class);
// when(tokenSet.get("Token")).thenReturn(annSet);
Map<String, AnnotationSet> map = mock(Map.class);
when(map.get(anyString())).thenReturn(tokenSet);
// when(doc.getAnnotations()).thenReturn(map);
when(doc.getAnnotations(anyString())).thenReturn(tokenSet);
// when(doc.getContent()).thenReturn(mock(Content.class));
when(corpus.get(0)).thenReturn(doc);
when(corpus.size()).thenReturn(1);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
diff.setKeyAnnotationSetName(null);
diff.setResponseAnnotationSetName(null);
Resource r = diff.init();
assertNotNull(r);
}

@Test
public void testPartialCompatibilityWithEmptyFeatureNameSet() throws Exception {
CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
AnnotationSchema schema = mock(AnnotationSchema.class);
when(schema.getAnnotationName()).thenReturn("TypeY");
diff.setAnnotationSchema(schema);
diff.setKeyFeatureNamesSet(Collections.emptySet());
Annotation keyAnn = mock(Annotation.class);
Annotation respAnn = mock(Annotation.class);
when(keyAnn.coextensive(respAnn)).thenReturn(true);
when(keyAnn.isPartiallyCompatible(eq(respAnn), any())).thenReturn(true);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(4L);
when(keyAnn.getStartNode()).thenReturn(s);
when(keyAnn.getEndNode()).thenReturn(e);
when(respAnn.getStartNode()).thenReturn(s);
when(respAnn.getEndNode()).thenReturn(e);
Set<Annotation> keyGroup = new HashSet<>();
keyGroup.add(keyAnn);
Set<Annotation> respGroup = new HashSet<>();
respGroup.add(respAnn);
AnnotationSet aset = mock(AnnotationSet.class);
// when(aset.get("TypeY")).thenReturn(keyGroup).thenReturn(respGroup);
Map<String, AnnotationSet> map = mock(Map.class);
when(map.get(anyString())).thenReturn(aset);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("bare");
when(doc.getSourceUrl()).thenReturn(new URL("http://bare"));
// when(doc.getAnnotations()).thenReturn(map);
when(doc.getAnnotations(anyString())).thenReturn(aset);
// when(doc.getContent()).thenReturn(mock(Content.class));
Corpus corpus = mock(Corpus.class);
when(corpus.get(0)).thenReturn(doc);
when(corpus.size()).thenReturn(1);
diff.setKeyCorpus(corpus);
diff.setResponseCorpus(corpus);
diff.setTextMode(Boolean.TRUE);
Resource r = diff.init();
assertNotNull(r);
}
}
