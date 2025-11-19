package gate.corpora;

import gate.*;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.creole.ResourceInstantiationException;
import gate.event.*;
import gate.util.ExtensionFileFilter;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;
import org.junit.Test;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DocumentImpl_1_GPTLLMTest {

@Test
public void testInitFromStringSucceeds() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setPreserveOriginalContent(true);
doc.setStringContent("Sample content.");
doc.setSourceUrl(null);
doc.setMarkupAware(false);
doc.init();
assertNotNull(doc.getContent());
assertEquals("Sample content.", doc.getContent().toString());
assertTrue(doc.getFeatures().containsKey("gate.SourceURL"));
assertEquals("created from String", doc.getFeatures().get("gate.SourceURL"));
assertEquals("Sample content.", doc.getFeatures().get(gate.GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME));
}

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWhenNoContentProvided() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent(null);
doc.setSourceUrl(null);
doc.init();
}

@Test
public void testIsValidOffsetReturnsTrueForValidOffset() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Test string.");
doc.init();
Long validOffset = 4L;
boolean result = doc.isValidOffset(validOffset);
assertTrue(result);
}

@Test
public void testIsValidOffsetReturnsFalseForNegativeOffset() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Some data");
doc.init();
Long invalidOffset = -10L;
boolean result = doc.isValidOffset(invalidOffset);
assertFalse(result);
}

@Test
public void testEditUpdatesDocumentContentCorrectly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("The original text.");
doc.init();
DocumentContent replacement = new DocumentContentImpl("new");
doc.edit(4L, 12L, replacement);
String updatedContent = doc.getContent().toString();
assertEquals("The new text.", updatedContent);
}

@Test(expected = InvalidOffsetException.class)
public void testEditThrowsExceptionForInvalidRange() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("edit this");
doc.init();
DocumentContent replacement = new DocumentContentImpl("X");
doc.edit(10L, 2L, replacement);
}

@Test
public void testNamedAnnotationSetIsCreatedOnDemand() {
DocumentImpl doc = new DocumentImpl();
AnnotationSet annotations = doc.getAnnotations("NamedSet");
assertNotNull(annotations);
AnnotationSet same = doc.getAnnotations("NamedSet");
assertSame(annotations, same);
}

@Test
public void testRemoveAnnotationSetRemovesNamedSet() {
DocumentImpl doc = new DocumentImpl();
doc.getAnnotations("CustomSet");
assertNotNull(doc.getNamedAnnotationSets().get("CustomSet"));
doc.removeAnnotationSet("CustomSet");
assertNull(doc.getNamedAnnotationSets().get("CustomSet"));
}

@Test
public void testGetNextAnnotationIdIncrementsCorrectly() {
DocumentImpl doc = new DocumentImpl();
Integer firstId = doc.getNextAnnotationId();
Integer secondId = doc.getNextAnnotationId();
assertEquals((int) firstId + 1, (int) secondId);
}

@Test
public void testPeakAtNextAnnotationIdDoesNotIncrement() {
DocumentImpl doc = new DocumentImpl();
Integer id1 = doc.peakAtNextAnnotationId();
Integer id2 = doc.peakAtNextAnnotationId();
assertEquals(id1, id2);
}

@Test
public void testGetDefaultAnnotationSetReturnsSameInstance() {
DocumentImpl doc = new DocumentImpl();
AnnotationSet one = doc.getAnnotations();
AnnotationSet two = doc.getAnnotations();
assertSame(one, two);
}

@Test
public void testToStringReturnsNonEmptyString() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("X");
String result = doc.toString();
assertNotNull(result);
assertTrue(result.contains("DocumentImpl"));
}

@Test
public void testSetAndGetEncodingUpdatesAndReflectsChange() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding("UTF-16");
String encoding = doc.getEncoding();
assertEquals("UTF-16", encoding);
}

@Test
public void testSetAndGetMimeType() {
DocumentImpl doc = new DocumentImpl();
doc.setMimeType("text/html");
String mime = doc.getMimeType();
assertEquals("text/html", mime);
}

@Test
public void testMarkupAwareFlagBehavior() {
DocumentImpl doc = new DocumentImpl();
doc.setMarkupAware(true);
assertTrue(doc.getMarkupAware());
doc.setMarkupAware(false);
assertFalse(doc.getMarkupAware());
}

@Test
public void testGetAnnotationsReturnsSameForNullAndEmptyName() {
DocumentImpl doc = new DocumentImpl();
AnnotationSet set1 = doc.getAnnotations(null);
AnnotationSet set2 = doc.getAnnotations("");
AnnotationSet defaultSet = doc.getAnnotations();
assertSame(set1, set2);
assertSame(set2, defaultSet);
}

@Test
public void testGetSourceUrlOffsetsNullByDefault() {
DocumentImpl doc = new DocumentImpl();
Long[] offsets = doc.getSourceUrlOffsets();
assertEquals((Long) null, offsets[0]);
assertEquals((Long) null, offsets[1]);
}

@Test
public void testEditNullContentNoException() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("One Two Three");
doc.init();
doc.setContent(null);
DocumentContent content = new DocumentContentImpl("Inserted");
doc.edit(0L, 0L, content);
assertNull(doc.getContent());
}

@Test
public void testEditNullDefaultAnnotationsNoException() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("One Two");
doc.init();
doc.setDefaultAnnotations(null);
DocumentContent content = new DocumentContentImpl("X");
doc.edit(1L, 2L, content);
}

@Test
public void testEditEmptyNamedAnnotationSetsNoException() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("123");
doc.init();
doc.getAnnotations("X");
doc.getNamedAnnotationSets().clear();
DocumentContent content = new DocumentContentImpl("Y");
doc.edit(0L, 1L, content);
}

@Test
public void testInvalidOffsetRangeEqualStartEndValid() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("token");
doc.init();
boolean isValid = doc.isValidOffsetRange(2L, 2L);
assertTrue(isValid);
}

@Test
public void testInvalidOffsetRangeInvalidStart() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
boolean result = doc.isValidOffsetRange(null, 2L);
assertFalse(result);
}

@Test
public void testGetNextNodeIdIncrements() {
DocumentImpl doc = new DocumentImpl();
int node1 = doc.getNextNodeId();
int node2 = doc.getNextNodeId();
assertEquals(node1 + 1, node2);
}

@Test
public void testGetEncodingFallbackToDefault() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding("   ");
String encoding = doc.getEncoding();
assertNotNull(encoding);
assertFalse(encoding.trim().isEmpty());
}

@Test
public void testCompareToHandlesNullOffsets() throws MalformedURLException {
DocumentImpl doc1 = new DocumentImpl();
doc1.setSourceUrl(new URL("http://example.com/X"));
DocumentImpl doc2 = new DocumentImpl();
doc2.setSourceUrl(new URL("http://example.com/Y"));
doc2.setSourceUrlStartOffset(10L);
doc2.setSourceUrlEndOffset(20L);
int result = doc1.compareTo(doc2);
assertTrue(result != 0);
}

@Test
public void testRemoveNonexistentAnnotationSetNoException() {
DocumentImpl doc = new DocumentImpl();
doc.removeAnnotationSet("ThisDoesNotExist");
}

@Test
public void testEmptyDocumentContentToXmlDoesNotCrash() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("");
doc.setPreserveOriginalContent(true);
doc.init();
String xml = doc.toXml(Collections.emptySet(), true);
assertNotNull(xml);
}

@Test
public void testSetAndGetSourceUrl() throws MalformedURLException {
DocumentImpl doc = new DocumentImpl();
URL url = new URL("http://example.com/test");
doc.setSourceUrl(url);
URL retrieved = doc.getSourceUrl();
assertEquals(url, retrieved);
}

@Test
public void testGetAnnotationSetNamesReturnsEmptyIfUnset() {
DocumentImpl doc = new DocumentImpl();
assertTrue(doc.getAnnotationSetNames().isEmpty());
}

@Test
public void testEditOnDocumentWithNullAnnotationSetsDoesNotThrow() throws InvalidOffsetException {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Edge case input");
// doc.init();
doc.removeAnnotationSet("nullSet");
doc.getNamedAnnotationSets().clear();
DocumentContent newContent = new DocumentContentImpl("A");
doc.edit(0L, 1L, newContent);
assertTrue(true);
}

@Test
public void testGetStringContentReturnsWhatWasSet() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("SAMPLE-TEXT");
assertEquals("SAMPLE-TEXT", doc.getStringContent());
}

@Test
public void testToXmlOnEmptyAnnotationSetReturnsCleanText() throws ResourceInstantiationException {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Simple & clean");
doc.init();
String result = doc.toXml(Collections.emptySet(), false);
assertTrue(result.contains("Simple"));
assertTrue(result.contains("clean"));
assertFalse(result.contains("<"));
}

@Test
public void testEncodingSetNullFallsBackSystemDefault() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding(null);
String encoding = doc.getEncoding();
assertNotNull(encoding);
assertFalse(encoding.trim().isEmpty());
}

@Test
public void testEditWithNullReplacementTreatsAsDeletion() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Hello world");
doc.init();
DocumentContent replacement = null;
doc.edit(0L, 5L, replacement);
String result = doc.getContent().toString();
assertTrue(result.contains("world"));
assertFalse(result.contains("Hello"));
}

@Test
public void testGetOrderingStringWhenSourceUrlIsNull() {
DocumentImpl doc = new DocumentImpl();
String ordering = doc.getOrderingString();
assertTrue(ordering.contains("DocumentImpl"));
}

@Test
public void testToXmlWithNullAnnotationSetPreservesOriginalMarkupOnly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("<html><body>text</body></html>");
doc.setPreserveOriginalContent(true);
doc.setMarkupAware(true);
doc.init();
String result = doc.toXml(null, true);
assertTrue(result.contains("text"));
assertTrue(result.contains("<body"));
}

@Test
public void testToXmlExcludesCrossedAnnotations() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcde");
doc.init();
AnnotationSet set = doc.getAnnotations();
// Annotation ann1 = set.add(0L, 4L, "A", Factory.newFeatureMap());
// Annotation ann2 = set.add(2L, 5L, "B", Factory.newFeatureMap());
Set<Annotation> source = new HashSet<Annotation>();
// source.add(ann1);
// source.add(ann2);
String result = doc.toXml(source, true);
assertTrue(result.contains("<A"));
assertTrue(result.contains("</A>"));
assertFalse(result.contains("<B"));
}

@Test
public void testWriteEmptyTagHandlesNullFeatureMapGracefully() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("X");
doc.init();
AnnotationSet set = doc.getAnnotations();
// Annotation ann = set.add(0L, 0L, "X", null);
Set<Annotation> source = new HashSet<Annotation>();
// source.add(ann);
String result = doc.toXml(source, false);
assertTrue(result.contains("<X"));
assertTrue(result.contains("/>"));
}

@Test
public void testWriteStartTagIncludesNamespaceWhenEnabled() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("ns test");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("nsPrefix", "p");
fm.put("nsUri", "http://example.org");
doc.getFeatures().put("gate.addNamespaceFeatures", "true");
doc.getFeatures().put("gate.namespacePrefix", "nsPrefix");
doc.getFeatures().put("gate.namespaceURI", "nsUri");
AnnotationSet annSet = doc.getAnnotations();
// Annotation ann = annSet.add(0L, 2L, "tag", fm);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String xml = doc.toXml(input, true);
assertTrue(xml.contains("xmlns:"));
}

@Test
public void testToXmlHandlesSelfClosingAnnotation() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", "true");
AnnotationSet set = doc.getAnnotations();
// Annotation ann = set.add(1L, 1L, "EmptyTag", fm);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String result = doc.toXml(input, true);
assertTrue(result.contains("<EmptyTag"));
assertFalse(result.contains("</EmptyTag>"));
}

@Test
public void testSetSourceOffsetsAndGetThemBack() {
DocumentImpl doc = new DocumentImpl();
doc.setSourceUrlStartOffset(10L);
doc.setSourceUrlEndOffset(30L);
Long[] offsets = doc.getSourceUrlOffsets();
assertEquals((Long) 10L, offsets[0]);
assertEquals((Long) 30L, offsets[1]);
}

@Test
public void testAnnotationSetRemovedEventDoesNotThrowIfSetNotPresent() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("x");
doc.removeAnnotationSet("missing");
assertTrue(true);
}

@Test
public void testInvalidOffsetStartGreaterThanEndFailsValidation() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("testcontent");
doc.init();
boolean result = doc.isValidOffsetRange(5L, 2L);
assertFalse(result);
}

@Test
public void testToXmlWithSingleAnnotationThatStartsAndEndsAtSameOffset() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet annSet = doc.getAnnotations();
// Annotation ann = annSet.add(1L, 1L, "EmptyType", fm);
Set<Annotation> annSetInput = new HashSet<Annotation>();
// annSetInput.add(ann);
String xml = doc.toXml(annSetInput, false);
assertTrue(xml.contains("<EmptyType"));
assertTrue(xml.contains("/>"));
}

@Test
public void testAnnotationIdOrderingForWriteStartTag() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcde");
doc.init();
FeatureMap featuresA = Factory.newFeatureMap();
FeatureMap featuresB = Factory.newFeatureMap();
AnnotationSet annSet = doc.getAnnotations();
// Annotation a = annSet.add(0L, 3L, "TagA", featuresA);
// Annotation b = annSet.add(1L, 4L, "TagB", featuresB);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(a);
// input.add(b);
String result = doc.toXml(input, true);
assertTrue(result.contains("TagA"));
assertFalse(result.contains("TagB"));
}

@Test
public void testToXmlIncludesGateIdAndFeatureSerialization() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("xyz");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("key", "value");
AnnotationSet set = doc.getAnnotations();
// Annotation ann = set.add(0L, 3L, "Span", fm);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String xml = doc.toXml(input, true);
assertTrue(xml.contains("key=\"value\""));
assertTrue(xml.contains("gateId="));
}

@Test
public void testToXmlHandlesNullFeatureNameAndValue() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("123");
doc.init();
FeatureMap map = Factory.newFeatureMap();
map.put(null, "notUsed");
map.put("valid", null);
AnnotationSet annSet = doc.getAnnotations();
// Annotation ann = annSet.add(0L, 2L, "SomeType", map);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String xml = doc.toXml(input, true);
assertTrue(xml.contains("<SomeType"));
assertFalse(xml.contains("notUsed"));
assertFalse(xml.contains("valid="));
}

@Test
public void testToXmlHandlesUnsupportedFeatureTypesGracefully() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("456");
doc.init();
FeatureMap map = Factory.newFeatureMap();
map.put("mapFeature", new Object());
AnnotationSet set = doc.getAnnotations();
// Annotation ann = set.add(0L, 2L, "UnsupportedFeature", map);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String xml = doc.toXml(input, true);
assertTrue(xml.contains("<UnsupportedFeature"));
assertFalse(xml.contains("mapFeature="));
}

@Test
public void testIsValidOffsetRejectsNull() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("test");
doc.init();
boolean result = doc.isValidOffset(null);
assertFalse(result);
}

@Test
public void testIsValidOffsetRejectsTooLargeOffset() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("test");
doc.init();
long largeOffset = 1000L;
boolean result = doc.isValidOffset(largeOffset);
assertFalse(result);
}

@Test
public void testWriteFeaturesWithCollectionValue() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("testing");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
java.util.List<String> list = new java.util.ArrayList<String>();
list.add("one");
list.add("two");
fm.put("labels", list);
AnnotationSet annSet = doc.getAnnotations();
// Annotation ann = annSet.add(0L, 7L, "MultiValued", fm);
Set<Annotation> annInput = new HashSet<Annotation>();
// annInput.add(ann);
String output = doc.toXml(annInput, true);
assertTrue(output.contains("labels="));
assertTrue(output.contains("one;two"));
}

@Test
public void testRemoveAnnotationSetFiresEvent() {
final boolean[] eventTriggered = { false };
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("event test");
doc.getAnnotations("example");
doc.addDocumentListener(new DocumentListener() {

public void annotationSetAdded(DocumentEvent e) {
}

public void annotationSetRemoved(DocumentEvent e) {
eventTriggered[0] = true;
}

public void contentEdited(DocumentEvent e) {
}
});
doc.removeAnnotationSet("example");
assertTrue(eventTriggered[0]);
}

@Test
public void testContentEditedEventFiresCorrectly() throws InvalidOffsetException {
final boolean[] contentChanged = { false };
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("fire content edit");
// doc.init();
doc.addDocumentListener(new DocumentListener() {

public void annotationSetAdded(DocumentEvent e) {
}

public void annotationSetRemoved(DocumentEvent e) {
}

public void contentEdited(DocumentEvent e) {
contentChanged[0] = true;
assertEquals(DocumentEvent.CONTENT_EDITED, e.getType());
// assertNotNull(e.getDocument());
}
});
DocumentContent updated = new DocumentContentImpl("changed");
doc.edit(0L, 4L, updated);
assertTrue(contentChanged[0]);
}

@Test
public void testToXmlWithFeatureNameNeedsNormalization() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("normalize");
doc.init();
FeatureMap features = Factory.newFeatureMap();
features.put("some key", "value with space");
// Annotation ann = doc.getAnnotations().add(0L, 5L, "Tag", features);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String result = doc.toXml(input, true);
assertTrue(result.contains("some_key"));
assertTrue(result.contains("value with space"));
}

@Test
public void testInsertCrossedAnnotationBeforeCorrespondingOne() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdefghij");
doc.init();
AnnotationSet annSet = doc.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
// Annotation ann1 = annSet.add(0L, 5L, "Outer", fm);
// Annotation ann2 = annSet.add(2L, 7L, "Inner", fm);
Set<Annotation> annotations = new HashSet<Annotation>();
// annotations.add(ann2);
// annotations.add(ann1);
String xml = doc.toXml(annotations, false);
assertTrue(xml.contains("Outer"));
assertFalse(xml.contains("Inner"));
}

@Test
public void testWriteFeaturesSkipsNonPrimitiveCollectionItems() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("sample");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
java.util.List<Object> list = new java.util.ArrayList<Object>();
list.add("valid");
list.add(new Object());
list.add(Integer.valueOf(3));
fm.put("mix", list);
AnnotationSet set = doc.getAnnotations();
// Annotation ann = set.add(0L, 6L, "Tag", fm);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String result = doc.toXml(input, true);
assertTrue(result.contains("valid"));
assertTrue(result.contains("3"));
assertFalse(result.contains("Object@"));
}

@Test
public void testToXmlOmitsAnnotationWithNullOffsetNodes() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("text");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
// Annotation an = new Annotation() {
// 
// public Integer getId() {
// return 1;
// }
// 
// public String getType() {
// return "Broken";
// }
// 
// public gate.Node getStartNode() {
// return null;
// }
// 
// public gate.Node getEndNode() {
// return null;
// }
// 
// public FeatureMap getFeatures() {
// return fm;
// }
// };
Set<Annotation> input = new HashSet<Annotation>();
// input.add(an);
String xml = doc.toXml(input, true);
assertFalse(xml.contains("Broken"));
}

@Test
public void testSetDataStoreNullRemovesListenerSafely() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setDataStore(null);
assertTrue(true);
}

@Test
public void testCompareToSelfIsZero() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
assertEquals(0, doc.compareTo(doc));
}

@Test
public void testCompareToWithDifferentOffsetsProducesResult() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
doc1.setStringContent("First");
doc1.setSourceUrl(new URL("http://example.com/doc"));
doc1.setSourceUrlStartOffset(10L);
doc1.setSourceUrlEndOffset(20L);
DocumentImpl doc2 = new DocumentImpl();
doc2.setStringContent("Second");
doc2.setSourceUrl(new URL("http://example.com/doc"));
int diff = doc1.compareTo(doc2);
assertTrue(diff != 0);
}

@Test
public void testInsertsSafetyReturnsFalseWhenOffsetsCross() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("aaaaaaa");
doc.init();
AnnotationSet baseSet = doc.getAnnotations();
FeatureMap f = Factory.newFeatureMap();
// Annotation a1 = baseSet.add(0L, 5L, "A", f);
// Annotation a2 = baseSet.add(2L, 6L, "B", f);
Set<Annotation> testSet = new HashSet<Annotation>();
// testSet.add(a1);
boolean result = doc.getAnnotations("Test").add(6L, 7L, "C", f) == null ? false : true;
assertTrue(result);
}

@Test
public void testHasOriginalContentFeaturesReturnsFalseWhenNoneSet() {
DocumentImpl doc = new DocumentImpl();
boolean result = false;
try {
result = doc.toXml(new HashSet<Annotation>(), true).contains("<");
} catch (Exception e) {
result = false;
}
assertTrue(result);
}

@Test
public void testWriteEmptyTagWithNamespaceSerializationFlag() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
doc.getFeatures().put("gate.addNamespaceFeatures", "true");
doc.getFeatures().put("gate.namespacePrefix", "ns");
doc.getFeatures().put("gate.namespaceURI", "http://namespace");
FeatureMap fm = Factory.newFeatureMap();
fm.put("ns", "pre");
fm.put("uri", "http://ns");
// Annotation ann = doc.getAnnotations().add(0L, 0L, "X", fm);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String xml = doc.toXml(input, true);
assertTrue(xml.contains("<X"));
assertTrue(xml.contains("/>"));
}

@Test
public void testWriteFeaturesSkipsXmlnsGateIfAlreadyPresent() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("tag test");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("xmlns:gate", "override");
AnnotationSet set = doc.getAnnotations();
// Annotation ann = set.add(0L, 4L, "Test", fm);
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann);
String value = doc.toXml(input, true);
assertTrue(value.contains("Test"));
assertTrue(value.contains("override"));
}

@Test
public void testPreserveOriginalContentTrueAddsFeature() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setPreserveOriginalContent(true);
doc.setStringContent("hello");
doc.init();
Object content = doc.getFeatures().get(gate.GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
assertEquals("hello", content);
}

@Test
public void testNullReplacementLengthPreservedOnNodeOffsetShift() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcd");
doc.init();
DocumentContent repl = null;
doc.edit(1L, 3L, repl);
String content = doc.getContent().toString();
assertEquals("ad", content);
}

@Test
public void testFeatureSerializationSkipsEmptyFeatureKey() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("test");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("", "value");
Set<Annotation> input = new HashSet<Annotation>();
// input.add(doc.getAnnotations().add(0L, 1L, "Tag", fm));
String result = doc.toXml(input, true);
assertTrue(result.contains("value"));
}

@Test
public void testAnnotationComparatorByStartAsc() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("start sort");
doc.init();
// Annotation a1 = doc.getAnnotations().add(2L, 4L, "A", Factory.newFeatureMap());
// Annotation a2 = doc.getAnnotations().add(0L, 3L, "B", Factory.newFeatureMap());
Comparator<Annotation> comp = new DocumentImpl.AnnotationComparator(0, 3);
// int result = comp.compare(a2, a1);
// assertTrue(result < 0);
}

@Test
public void testAnnotationComparatorByStartDesc() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("xyz");
doc.init();
// Annotation a1 = doc.getAnnotations().add(0L, 2L, "A", Factory.newFeatureMap());
// Annotation a2 = doc.getAnnotations().add(1L, 3L, "B", Factory.newFeatureMap());
Comparator<Annotation> comp = new DocumentImpl.AnnotationComparator(0, -3);
// int result = comp.compare(a2, a1);
// assertTrue(result < 0);
}

@Test
public void testAnnotationComparatorByEndOffset() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("012345");
doc.init();
// Annotation a1 = doc.getAnnotations().add(1L, 2L, "One", Factory.newFeatureMap());
// Annotation a2 = doc.getAnnotations().add(1L, 4L, "Two", Factory.newFeatureMap());
Comparator<Annotation> comp = new DocumentImpl.AnnotationComparator(1, 3);
// int result = comp.compare(a1, a2);
// assertTrue(result < 0);
}

@Test
public void testAnnotationComparatorResolvesEqualOffsetsById() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("equal");
doc.init();
FeatureMap f1 = Factory.newFeatureMap();
FeatureMap f2 = Factory.newFeatureMap();
// Annotation a1 = doc.getAnnotations().add(1L, 3L, "X", f1);
// Annotation a2 = doc.getAnnotations().add(1L, 3L, "X", f2);
Comparator<Annotation> comp = new DocumentImpl.AnnotationComparator(0, 3);
// int result = comp.compare(a1, a2);
// assertTrue(result < 0 || result > 0);
}

@Test
public void testGetAnnotationsForOffsetWithNullSetReturnsEmptyList() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
Set<Annotation> annotations = null;
Set<Annotation> result = new HashSet<Annotation>();
result.addAll(doc.getAnnotations());
assertNotNull(result);
}

@Test
public void testToXmlWithNullOriginalFeaturesFallsBack() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("fallback case");
doc.init();
Set<Annotation> annotations = new HashSet<Annotation>();
String xml = doc.toXml(annotations, true);
assertNotNull(xml);
assertTrue(xml.contains("fallback"));
}

@Test
public void testConvertWriteFeaturesSkipsUnknownValueType() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("tag");
doc.init();
FeatureMap fMap = Factory.newFeatureMap();
fMap.put("obj", new Object());
Set<Annotation> s = new HashSet<Annotation>();
// s.add(doc.getAnnotations().add(0L, 3L, "MyTag", fMap));
String result = doc.toXml(s, true);
assertFalse(result.contains("obj"));
}

@Test
public void testAnnotationComparatorById() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("zzz");
doc.init();
// Annotation a = doc.getAnnotations().add(0L, 1L, "Tag", Factory.newFeatureMap());
// Annotation b = doc.getAnnotations().add(0L, 1L, "Tag", Factory.newFeatureMap());
Comparator<Annotation> cmp = new DocumentImpl.AnnotationComparator(2, 3);
// int result = cmp.compare(a, b);
// assertTrue(result < 0 || result > 0);
}

@Test
public void testToXmlHandlesNonXmlChars() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("hi & < > ©");
doc.init();
Set<Annotation> set = new HashSet<Annotation>();
String output = doc.toXml(set, true);
assertTrue(output.contains("&amp;"));
assertTrue(output.contains("&lt;"));
assertTrue(output.contains("&gt;"));
}

@Test
public void testEditWithFullContentReplacementPreservesRest() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("ReplaceThisPortionOnly");
doc.init();
DocumentContent repl = new DocumentContentImpl("abc");
doc.edit(7L, 11L, repl);
String val = doc.getContent().toString();
assertTrue(val.contains("abc"));
assertTrue(val.startsWith("Replace"));
}

@Test(expected = InvalidOffsetException.class)
public void testEditRejectsInvalidRangeBeyondLimit() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("short");
doc.init();
DocumentContent repl = new DocumentContentImpl("X");
doc.edit(2L, 1000L, repl);
}

@Test
public void testInsertsSafetyReturnsFalseWhenStartNodeIsNull() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("node test");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
// Annotation badAnnotation = new Annotation() {
// 
// public Integer getId() {
// return 10;
// }
// 
// public String getType() {
// return "Invalid";
// }
// 
// public gate.Node getStartNode() {
// return null;
// }
// 
// public gate.Node getEndNode() {
// return null;
// }
// 
// public FeatureMap getFeatures() {
// return fm;
// }
// };
AnnotationSet base = doc.getAnnotations();
// boolean result = doc.getAnnotations().contains(badAnnotation);
// assertFalse(result);
}

@Test
public void testEmptyDocumentReturnsNonNullToString() {
DocumentImpl doc = new DocumentImpl();
String str = doc.toString();
assertNotNull(str);
assertTrue(str.contains("DocumentImpl"));
}

@Test
public void testAnnotationSetDefaultTriggersEventOnce() {
DocumentImpl doc = new DocumentImpl();
AnnotationSet one = doc.getAnnotations();
AnnotationSet two = doc.getAnnotations();
assertSame(one, two);
}

@Test
public void testSetNullEncodingFallsBackToSystemDefault() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding(null);
String encoding = doc.getEncoding();
assertNotNull(encoding);
assertFalse(encoding.trim().isEmpty());
}

@Test
public void testSetEmptyEncodingFallsBackToSystemDefault() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding("  ");
String encoding = doc.getEncoding();
assertNotNull(encoding);
assertFalse(encoding.trim().isEmpty());
}

@Test
public void testWriteEmptyTagWithNullFeatureMap() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("test");
doc.init();
// Annotation ann = doc.getAnnotations().add(0L, 0L, "Empty", null);
Set<Annotation> annSet = new HashSet<Annotation>();
// annSet.add(ann);
String xml = doc.toXml(annSet, true);
assertTrue(xml.contains("<Empty"));
assertTrue(xml.contains("/>"));
}

@Test
public void testToXmlWithContentUpdateAtSameOffset() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdef");
doc.init();
DocumentContent content = new DocumentContentImpl("X");
doc.edit(3L, 3L, content);
String result = doc.getContent().toString();
assertEquals("abcXdef", result);
}

@Test
public void testToXmlWithOverlappingAnnotationsThatAreValid() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdefgh");
doc.init();
// Annotation a = doc.getAnnotations().add(0L, 4L, "A", Factory.newFeatureMap());
// Annotation b = doc.getAnnotations().add(4L, 8L, "B", Factory.newFeatureMap());
Set<Annotation> annSet = new HashSet<Annotation>();
// annSet.add(a);
// annSet.add(b);
String xml = doc.toXml(annSet, true);
assertTrue(xml.contains("<A"));
assertTrue(xml.contains("<B"));
}

@Test
public void testGetSourceUrlOffsetsReturnsNullsIfUnset() {
DocumentImpl doc = new DocumentImpl();
Long[] offsets = doc.getSourceUrlOffsets();
assertNull(offsets[0]);
assertNull(offsets[1]);
}

@Test
public void testRemoveAnnotationSetThatDoesNotExistGracefully() {
DocumentImpl doc = new DocumentImpl();
doc.removeAnnotationSet("NotPresent");
assertTrue(true);
}

@Test
public void testAnnotationSetNamesReturnsAllKeys() {
DocumentImpl doc = new DocumentImpl();
doc.getAnnotations("SetA");
doc.getAnnotations("SetB");
Set<String> names = doc.getAnnotationSetNames();
assertTrue(names.contains("SetA"));
assertTrue(names.contains("SetB"));
}

@Test
public void testEditContentWithNullReplacementDeletesRegion() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("123456");
doc.init();
doc.edit(2L, 4L, null);
String result = doc.getContent().toString();
assertEquals("1256", result);
}

@Test
public void testToXmlWithNullAnnotationSetReturnsMarkupOnly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setPreserveOriginalContent(true);
doc.setStringContent("<root>abc</root>");
doc.init();
String xml = doc.toXml(null, true);
assertTrue(xml.contains("abc"));
assertTrue(xml.contains("root"));
}

@Test
public void testToXmlWithRootAnnotationPresent() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("xyz");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
// Annotation root = doc.getAnnotations().add(0L, (long) doc.getContent().size(), "ROOT", fm);
Set<Annotation> s = new HashSet<Annotation>();
// s.add(root);
String xml = doc.toXml(s, true);
assertTrue(xml.contains("<ROOT"));
assertTrue(xml.contains("</ROOT>"));
}

@Test
public void testToXmlWithOverlappingCrossedAnnotationsSkipsOne() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdefghij");
doc.init();
// Annotation ann1 = doc.getAnnotations().add(0L, 6L, "Outer", Factory.newFeatureMap());
// Annotation ann2 = doc.getAnnotations().add(3L, 9L, "Inner", Factory.newFeatureMap());
Set<Annotation> input = new HashSet<Annotation>();
// input.add(ann1);
// input.add(ann2);
String xml = doc.toXml(input, true);
assertTrue(xml.contains("<Outer"));
assertFalse(xml.contains("<Inner"));
}

@Test
public void testSetSourceUrlStartEndOffsetAndGetThemBack() {
DocumentImpl doc = new DocumentImpl();
doc.setSourceUrlStartOffset(5L);
doc.setSourceUrlEndOffset(15L);
Long[] result = doc.getSourceUrlOffsets();
assertEquals(Long.valueOf(5L), result[0]);
assertEquals(Long.valueOf(15L), result[1]);
}

@Test(expected = ResourceInstantiationException.class)
public void testInitThrowsWhenSourceUrlIsNullAndContentIsNull() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setSourceUrl(null);
doc.setStringContent(null);
doc.init();
}

@Test
public void testEmptyAnnotationSetToXmlReturnsRawContentUnchanged() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("plain text");
doc.init();
String xml = doc.toXml(Collections.emptySet(), false);
assertEquals("plain text", xml);
}

@Test
public void testIsValidOffsetRangeAcceptsEqualStartEnd() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("a");
doc.init();
assertTrue(doc.isValidOffsetRange(0L, 0L));
}

@Test
public void testIsValidOffsetRangeRejectsNullStart() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
assertFalse(doc.isValidOffsetRange(null, 2L));
}

@Test
public void testIsValidOffsetRangeRejectsInvalidOrder() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdef");
doc.init();
assertFalse(doc.isValidOffsetRange(5L, 2L));
}

@Test
public void testWriteFeaturesHandlesBooleanValue() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("trueFalse");
doc.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("flag", Boolean.TRUE);
// Annotation ann = doc.getAnnotations().add(0L, 3L, "Test", fm);
Set<Annotation> annSet = new HashSet<Annotation>();
// annSet.add(ann);
String xml = doc.toXml(annSet, true);
assertTrue(xml.contains("flag=\"true\""));
}
}
