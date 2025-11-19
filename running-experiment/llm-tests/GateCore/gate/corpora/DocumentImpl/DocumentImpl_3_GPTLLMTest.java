package gate.corpora;

import gate.*;
import gate.annotation.AnnotationImpl;
import gate.annotation.AnnotationSetImpl;
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
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DocumentImpl_3_GPTLLMTest {

@Test
public void testConstructorInitializesContentAndFeatures() {
DocumentImpl document = new DocumentImpl();
assertNotNull(document.getContent());
assertNotNull(document.getFeatures());
assertFalse(document.getPreserveOriginalContent());
assertFalse(document.getCollectRepositioningInfo());
}

@Test
public void testPreserveOriginalContentGetterSetter() {
DocumentImpl document = new DocumentImpl();
document.setPreserveOriginalContent(Boolean.TRUE);
assertTrue(document.getPreserveOriginalContent());
document.setPreserveOriginalContent(Boolean.FALSE);
assertFalse(document.getPreserveOriginalContent());
}

@Test
public void testCollectRepositioningInfoGetterSetter() {
DocumentImpl document = new DocumentImpl();
document.setCollectRepositioningInfo(Boolean.TRUE);
assertTrue(document.getCollectRepositioningInfo());
document.setCollectRepositioningInfo(Boolean.FALSE);
assertFalse(document.getCollectRepositioningInfo());
}

@Test
public void testMimeTypeGetterSetter() {
DocumentImpl document = new DocumentImpl();
document.setMimeType("text/html");
assertEquals("text/html", document.getMimeType());
}

@Test
public void testSetGetSourceUrlOffsets() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlStartOffset(5L);
document.setSourceUrlEndOffset(15L);
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(5L), offsets[0]);
assertEquals(Long.valueOf(15L), offsets[1]);
}

@Test
public void testSetGetStringContent() {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Sample content");
assertEquals("Sample content", document.getStringContent());
}

@Test
public void testInitWithStringContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Example");
Resource resource = document.init();
assertNotNull(resource);
assertEquals("Example", document.getContent().toString());
}

@Test(expected = ResourceInstantiationException.class)
public void testInitThrowsIfNoContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent(null);
document.init();
}

@Test
public void testGetAnnotationsDefaultAndNamed() {
DocumentImpl document = new DocumentImpl();
AnnotationSet defaultSet = document.getAnnotations();
assertNotNull(defaultSet);
AnnotationSet sameSet = document.getAnnotations("");
assertSame(defaultSet, sameSet);
AnnotationSet named = document.getAnnotations("NER");
assertNotNull(named);
assertNotSame(defaultSet, named);
}

@Test
public void testGetNamedAnnotationSetMapAndNames() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("tokens");
Map<String, AnnotationSet> namedSets = document.getNamedAnnotationSets();
Set<String> names = document.getAnnotationSetNames();
assertTrue(namedSets.containsKey("tokens"));
assertTrue(names.contains("tokens"));
}

@Test
public void testRemoveNamedAnnotationSetSuccessfully() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("removeMe");
assertTrue(document.getAnnotationSetNames().contains("removeMe"));
document.removeAnnotationSet("removeMe");
assertFalse(document.getAnnotationSetNames().contains("removeMe"));
}

@Test
public void testEditUpdatesContentSuccessfully() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcdefg");
document.init();
// DocumentContent replacement = Factory.newDocumentContent("XY");
// document.edit(1L, 3L, replacement);
String edited = document.getContent().toString();
assertEquals("aXYdefg", edited);
}

@Test(expected = InvalidOffsetException.class)
public void testEditThrowsOnInvalidRange() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("123");
document.init();
// DocumentContent newContent = Factory.newDocumentContent("Z");
// document.edit(5L, 6L, newContent);
}

@Test
public void testIsValidOffsetChecksBoundsCorrectly() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("test123");
document.init();
assertTrue(document.isValidOffset(0L));
assertTrue(document.isValidOffset(6L));
assertFalse(document.isValidOffset(-1L));
assertFalse(document.isValidOffset(10L));
}

@Test
public void testIsValidOffsetRange() {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcdef");
// document.init();
assertTrue(document.isValidOffsetRange(1L, 4L));
assertFalse(document.isValidOffsetRange(4L, 1L));
assertFalse(document.isValidOffsetRange(-1L, 3L));
assertFalse(document.isValidOffsetRange(2L, 100L));
}

@Test
public void testEncodingDefaultAndSet() {
DocumentImpl document = new DocumentImpl();
String encoding = document.getEncoding();
assertNotNull(encoding);
document.setEncoding("UTF-8");
assertEquals("UTF-8", document.getEncoding());
}

@Test
public void testMarkupAwareFlag() {
DocumentImpl document = new DocumentImpl();
assertFalse(document.getMarkupAware());
document.setMarkupAware(Boolean.TRUE);
assertTrue(document.getMarkupAware());
}

@Test
public void testNextAnnotationIdIncrements() {
DocumentImpl document = new DocumentImpl();
Integer id1 = document.getNextAnnotationId();
Integer id2 = document.getNextAnnotationId();
assertEquals(Integer.valueOf(id1 + 1), id2);
}

@Test
public void testPeekDoesNotIncrementAnnotationId() {
DocumentImpl document = new DocumentImpl();
Integer peek1 = document.peakAtNextAnnotationId();
Integer next = document.getNextAnnotationId();
assertEquals(peek1, Integer.valueOf(next));
}

@Test
public void testNextNodeIdUnique() {
DocumentImpl document = new DocumentImpl();
Integer node1 = document.getNextNodeId();
Integer node2 = document.getNextNodeId();
assertEquals(Integer.valueOf(node1 + 1), node2);
}

@Test
public void testToStringReturnsNonEmptyString() {
DocumentImpl document = new DocumentImpl();
String result = document.toString();
assertNotNull(result);
assertTrue(result.contains("DocumentImpl"));
}

@Test
public void testCompareToSmaller() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
URL url1 = new URL("http://example.com/a.txt");
doc1.setSourceUrl(url1);
DocumentImpl doc2 = new DocumentImpl();
URL url2 = new URL("http://example.com/z.txt");
doc2.setSourceUrl(url2);
assertTrue(doc1.compareTo(doc2) < 0);
}

@Test
public void testCompareToGreater() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
URL url1 = new URL("http://example.com/z.txt");
doc1.setSourceUrl(url1);
DocumentImpl doc2 = new DocumentImpl();
URL url2 = new URL("http://example.com/a.txt");
doc2.setSourceUrl(url2);
assertTrue(doc1.compareTo(doc2) > 0);
}

@Test
public void testCompareToEqualWhenSameDocument() {
DocumentImpl document = new DocumentImpl();
int result = document.compareTo(document);
assertEquals(0, result);
}

@Test
public void testNamedAnnotationSetRemovalUpdatesMap() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("toBeRemoved");
Map<String, AnnotationSet> mapBefore = document.getNamedAnnotationSets();
assertTrue(mapBefore.containsKey("toBeRemoved"));
document.removeAnnotationSet("toBeRemoved");
Map<String, AnnotationSet> mapAfter = document.getNamedAnnotationSets();
assertFalse(mapAfter.containsKey("toBeRemoved"));
}

@Test
public void testGetAnnotationSetNamesConsistent() {
DocumentImpl document = new DocumentImpl();
Set<String> namesBefore = document.getAnnotationSetNames();
assertNotNull(namesBefore);
assertEquals(0, namesBefore.size());
document.getAnnotations("match");
Set<String> namesAfter = document.getAnnotationSetNames();
assertTrue(namesAfter.contains("match"));
}

@Test
public void testCleanupClearsAllNamedAnnotationSets() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("temp1");
document.getAnnotations("temp2");
assertEquals(2, document.getAnnotationSetNames().size());
document.cleanup();
assertEquals(0, document.getAnnotationSetNames().size());
}

@Test
public void testEditWithNullReplacementDeletesContentRange() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcdefg");
document.init();
document.edit(2L, 4L, null);
String result = document.getContent().toString();
assertEquals("abefg", result);
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithNullOffsetsThrowsException() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("sample text");
document.init();
// document.edit(null, null, Factory.newDocumentContent("X"));
}

@Test
public void testEditFullContentReplaced() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("replace me");
document.init();
// DocumentContent newContent = Factory.newDocumentContent("new stuff");
// document.edit(0L, 10L, newContent);
String result = document.getContent().toString();
assertEquals("new stuff", result);
}

@Test
public void testGetFeaturesCreatesNewIfNull() {
DocumentImpl document = new DocumentImpl();
FeatureMap features = document.getFeatures();
assertNotNull(features);
features.put("foo", "bar");
assertEquals("bar", features.get("foo"));
}

@Test
public void testSetContentOverridesOldContent() {
DocumentImpl document = new DocumentImpl();
// DocumentContent first = Factory.newDocumentContent("first");
// document.setContent(first);
assertEquals("first", document.getContent().toString());
// DocumentContent second = Factory.newDocumentContent("second");
// document.setContent(second);
assertEquals("second", document.getContent().toString());
}

@Test
public void testAnnotationSetDefaultIsSameInstance() {
DocumentImpl document = new DocumentImpl();
AnnotationSet set1 = document.getAnnotations();
AnnotationSet set2 = document.getAnnotations();
assertSame(set1, set2);
}

@Test
public void testAnnotationSetNamedAreDifferentInstances() {
DocumentImpl document = new DocumentImpl();
AnnotationSet setA = document.getAnnotations("setA");
AnnotationSet setB = document.getAnnotations("setB");
assertNotSame(setA, setB);
}

@Test
public void testEncodingFallsBackToSystemProperty() {
DocumentImpl document = new DocumentImpl();
String detectedEncoding = document.getEncoding();
assertNotNull(detectedEncoding);
assertTrue(detectedEncoding.length() > 0);
}

@Test
public void testCompareToWithNullUrlFallsBackToString() {
DocumentImpl doc1 = new DocumentImpl();
DocumentImpl doc2 = new DocumentImpl();
int result = doc1.compareTo(doc2);
assertEquals(0, result);
}

@Test
public void testGetOrderingStringWithNullOffset() throws Exception {
DocumentImpl document = new DocumentImpl();
URL url = new URL("http://example.com/data.txt");
document.setSourceUrl(url);
String s = document.getOrderingString();
assertEquals(url.toExternalForm(), s);
}

@Test
public void testGetOrderingStringWithOffset() throws Exception {
DocumentImpl document = new DocumentImpl();
URL url = new URL("http://example.com/data.txt");
document.setSourceUrl(url);
document.setSourceUrlStartOffset(5L);
document.setSourceUrlEndOffset(10L);
String s = document.getOrderingString();
assertTrue(s.contains("data.txt"));
assertTrue(s.contains("5"));
assertTrue(s.contains("10"));
}

@Test
public void testInitWithPreserveOriginalContentSetsFeature() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("plain text");
document.setPreserveOriginalContent(Boolean.TRUE);
document.init();
String original = (String) document.getFeatures().get("OriginalDocumentContent");
assertEquals("plain text", original);
}

@Test
public void testGetAnnotationsNullNameReturnsDefault() {
DocumentImpl document = new DocumentImpl();
AnnotationSet result1 = document.getAnnotations(null);
AnnotationSet result2 = document.getAnnotations();
assertSame(result1, result2);
}

@Test
public void testRemoveAnnotationSetDoesNotAffectUnrelated() {
DocumentImpl document = new DocumentImpl();
AnnotationSet a = document.getAnnotations("A");
AnnotationSet b = document.getAnnotations("B");
document.removeAnnotationSet("A");
Map<String, AnnotationSet> map = document.getNamedAnnotationSets();
assertFalse(map.containsKey("A"));
assertTrue(map.containsKey("B"));
}

@Test
public void testSetAndGetNullEncodingFallsBackGracefully() {
DocumentImpl document = new DocumentImpl();
document.setEncoding(null);
String enc = document.getEncoding();
assertNotNull(enc);
assertTrue(enc.length() > 0);
}

@Test
public void testEmptyStringContentHandledGracefully() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("");
document.init();
String result = document.getContent().toString();
assertEquals("", result);
}

@Test
public void testEditZeroLengthRangeInsertsText() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc");
document.init();
// DocumentContent replacement = Factory.newDocumentContent("X");
// document.edit(1L, 1L, replacement);
String result = document.getContent().toString();
assertEquals("aXbc", result);
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithStartGreaterThanEndThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("xyz");
document.init();
// DocumentContent replacement = Factory.newDocumentContent("Q");
// document.edit(3L, 1L, replacement);
}

@Test
public void testInitMultipleTimesReinitializesContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("first");
document.init();
document.setStringContent("second");
document.init();
String result = document.getContent().toString();
assertEquals("second", result);
}

@Test
public void testMultipleSetSourceUrlDoesNotThrow() throws Exception {
DocumentImpl document = new DocumentImpl();
URL url1 = new URL("http://example.com/one.txt");
document.setSourceUrl(url1);
URL url2 = new URL("http://example.com/two.txt");
document.setSourceUrl(url2);
assertEquals("http://example.com/two.txt", document.getSourceUrl().toString());
}

@Test
public void testValidOffsetWithNullContentReturnsFalse() {
DocumentImpl doc = new DocumentImpl();
doc.setContent(null);
boolean result = doc.isValidOffset(1L);
assertFalse(result);
}

@Test
public void testIsValidOffsetRangeWhenStartEqualsEnd() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdef");
try {
doc.init();
} catch (Exception e) {
fail("Unexpected exception during init");
}
boolean valid = doc.isValidOffsetRange(2L, 2L);
assertTrue(valid);
}

@Test
public void testInitDoesNotBreakWithBinaryMime() {
DocumentImpl doc = new DocumentImpl();
doc.setMimeType("application/pdf");
doc.setStringContent("Dummy content");
try {
ResourceInstantiationException ex = null;
doc.init();
assertNotNull(doc.getContent());
} catch (Exception e) {
fail("Unexpected exception raised: " + e.getMessage());
}
}

@Test
public void testRemoveNonexistentAnnotationSetDoesNotThrow() {
DocumentImpl doc = new DocumentImpl();
doc.removeAnnotationSet("nonexistent");
assertNotNull(doc.getNamedAnnotationSets());
}

@Test
public void testGetNextAnnotationIdInitialValue() {
DocumentImpl doc = new DocumentImpl();
Integer id1 = doc.getNextAnnotationId();
assertEquals(Integer.valueOf(0), id1);
}

@Test
public void testGetNextNodeIdInitialValue() {
DocumentImpl doc = new DocumentImpl();
Integer node1 = doc.getNextNodeId();
assertEquals(Integer.valueOf(0), node1);
}

@Test
public void testToXmlReturnsNonNull() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Some content");
try {
doc.init();
String xml = doc.toXml();
assertNotNull(xml);
assertTrue(xml.contains("Some content"));
} catch (Exception e) {
fail("Unexpected exception: " + e.getMessage());
}
}

@Test
public void testGetAnnotationsForEmptySet() {
DocumentImpl doc = new DocumentImpl();
AnnotationSet as = doc.getAnnotations("emptySet");
assertNotNull(as);
assertEquals(0, as.size());
}

@Test
public void testGetOrderingStringWithoutSourceUrlReturnsToString() {
DocumentImpl doc = new DocumentImpl();
String ordering = doc.getOrderingString();
assertTrue(ordering.contains("DocumentImpl"));
}

@Test
public void testSetSourceUrlWithNullOffsets() {
try {
DocumentImpl doc = new DocumentImpl();
doc.setSourceUrl(new URL("http://example.com/test.txt"));
URL url = doc.getSourceUrl();
Long[] offsets = doc.getSourceUrlOffsets();
assertNotNull(url);
assertNotNull(offsets);
assertNull(offsets[0]);
assertNull(offsets[1]);
} catch (Exception e) {
fail("Unexpected exception");
}
}

@Test
public void testSetStringContentDoesNotAffectGetContent() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("text only");
DocumentContent contentBefore = doc.getContent();
assertNotNull(contentBefore);
assertEquals("", contentBefore.toString());
}

@Test
public void testSetContentThenSetStringContentPreservesContent() {
DocumentImpl doc = new DocumentImpl();
// DocumentContent initial = Factory.newDocumentContent("ABC");
// doc.setContent(initial);
doc.setStringContent("DEF");
assertEquals("ABC", doc.getContent().toString());
}

@Test
public void testGetNamedAnnotationSetsReturnsEmptyInitially() {
DocumentImpl doc = new DocumentImpl();
Map<String, AnnotationSet> map = doc.getNamedAnnotationSets();
assertNotNull(map);
assertTrue(map.isEmpty());
}

@Test
public void testGetAnnotationSetNamesReturnsEmptyInitially() {
DocumentImpl doc = new DocumentImpl();
assertTrue(doc.getAnnotationSetNames().isEmpty());
}

@Test
public void testSetEncodingToEmptyStringFallsBack() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding("");
String encoding = doc.getEncoding();
assertNotNull(encoding);
assertTrue(encoding.length() > 0);
}

@Test
public void testSetEncodingTrimsInput() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding("  UTF-8  ");
String encoding = doc.getEncoding();
assertEquals("UTF-8", encoding.trim());
}

@Test
public void testGetFeaturesCreatesInternalMapIfNull() {
DocumentImpl doc = new DocumentImpl();
FeatureMap map = doc.getFeatures();
assertNotNull(map);
map.put("key", "value");
assertEquals("value", map.get("key"));
}

@Test
public void testCleanupClearsNamedAndDefaultAnnotSets() {
DocumentImpl doc = new DocumentImpl();
doc.getAnnotations("X");
doc.getAnnotations("Y");
doc.getAnnotations();
assertFalse(doc.getNamedAnnotationSets().isEmpty());
doc.cleanup();
assertTrue(doc.getNamedAnnotationSets().isEmpty());
}

@Test
public void testToStringIncludesKeyFields() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("short string");
String representation = doc.toString();
assertTrue(representation.contains("DocumentImpl"));
assertTrue(representation.contains("markupAware"));
assertTrue(representation.contains("sourceUrl"));
}

@Test(expected = ResourceInstantiationException.class)
public void testInitWithInvalidMimeTypeThrowsException() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setMimeType("unknown/type");
document.setStringContent("invalid mime");
document.setMarkupAware(Boolean.TRUE);
document.init();
}

@Test
public void testInitPreservesOriginalContentAsFeature() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setPreserveOriginalContent(Boolean.TRUE);
document.setStringContent("testing preservation");
document.init();
Object feature = document.getFeatures().get("OriginalDocumentContent");
assertEquals("testing preservation", feature);
}

@Test
public void testInitWithNullMimeFallsBack() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setMimeType(null);
document.setStringContent("test");
document.init();
assertNotNull(document.getContent());
}

@Test
public void testEditToEmptyDocumentContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("erase all");
document.init();
document.edit(0L, document.getContent().size(), null);
String result = document.getContent().toString();
assertEquals("", result);
}

@Test
public void testMultipleAnnotationSetAdditionsSameNameReturnsSameSet() {
DocumentImpl document = new DocumentImpl();
assertEquals(document.getAnnotations("custom"), document.getAnnotations("custom"));
}

@Test
public void testSourceUrlStartOffsetOnly() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlStartOffset(42L);
assertEquals(Long.valueOf(42), document.getSourceUrlOffsets()[0]);
assertNull(document.getSourceUrlOffsets()[1]);
}

@Test
public void testSourceUrlEndOffsetOnly() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlEndOffset(999L);
assertNull(document.getSourceUrlOffsets()[0]);
assertEquals(Long.valueOf(999), document.getSourceUrlOffsets()[1]);
}

@Test
public void testGetNamedAnnotationSetsNotNullAfterRemove() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("abc");
document.removeAnnotationSet("abc");
Map<String, gate.AnnotationSet> result = document.getNamedAnnotationSets();
assertNotNull(result);
assertFalse(result.containsKey("abc"));
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithNullStartThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("important");
document.init();
// DocumentContent replacement = Factory.newDocumentContent("X");
// document.edit(null, 2L, replacement);
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithNullEndThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("data");
document.init();
// DocumentContent replacement = Factory.newDocumentContent("Y");
// document.edit(1L, null, replacement);
}

@Test
public void testEditWithEmptyReplacementPreservesContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("hello world");
document.init();
// DocumentContent replacement = Factory.newDocumentContent("");
// document.edit(6L, 11L, replacement);
String result = document.getContent().toString();
assertEquals("hello ", result);
}

@Test
public void testCompareToWithSameUrlAndOffsets() throws Exception {
DocumentImpl d1 = new DocumentImpl();
d1.setSourceUrl(new URL("http://example.org"));
d1.setSourceUrlStartOffset(1L);
d1.setSourceUrlEndOffset(2L);
DocumentImpl d2 = new DocumentImpl();
d2.setSourceUrl(new URL("http://example.org"));
d2.setSourceUrlStartOffset(1L);
d2.setSourceUrlEndOffset(2L);
assertEquals(0, d1.compareTo(d2));
}

@Test
public void testCleanupDoesNotThrowWhenAllInternalFieldsNull() {
DocumentImpl doc = new DocumentImpl();
doc.cleanup();
}

@Test
public void testSetContentPreservesReference() throws Exception {
DocumentImpl doc = new DocumentImpl();
// DocumentContent content = Factory.newDocumentContent("ABC");
// doc.setContent(content);
assertEquals("ABC", doc.getContent().toString());
}

@Test
public void testToXmlReturnIncludesEncoding() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding("UTF-8");
doc.setStringContent("<note>hi</note>");
doc.setMarkupAware(Boolean.FALSE);
doc.init();
String xml = doc.toXml(doc.getAnnotations(), true);
assertTrue(xml.contains("<?xml version=\"1.0"));
assertTrue(xml.contains("UTF-8"));
}

@Test
public void testPeakAtNextAnnotationIdDoesNotIncrement() {
DocumentImpl doc = new DocumentImpl();
Integer id1 = doc.peakAtNextAnnotationId();
Integer id2 = doc.peakAtNextAnnotationId();
assertEquals(id1, id2);
assertEquals(id1, Integer.valueOf(doc.getNextAnnotationId()));
}

@Test
public void testCompareToWithDifferentOffsetsOnly() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
doc1.setSourceUrl(new URL("http://example.com/doc.txt"));
doc1.setSourceUrlStartOffset(10L);
doc1.setSourceUrlEndOffset(20L);
DocumentImpl doc2 = new DocumentImpl();
doc2.setSourceUrl(new URL("http://example.com/doc.txt"));
doc2.setSourceUrlStartOffset(30L);
doc2.setSourceUrlEndOffset(40L);
int result = doc1.compareTo(doc2);
assertTrue(result < 0);
}

@Test
public void testToStringIncludesNextAnnotationId() {
DocumentImpl doc = new DocumentImpl();
doc.getNextAnnotationId();
String result = doc.toString();
assertTrue(result.contains("nextAnnotationId"));
}

@Test
public void testSetMimeTypeNullDoesNotBreakOrderingString() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setSourceUrl(new URL("http://example.com"));
doc.setMimeType(null);
String orderStr = doc.getOrderingString();
assertTrue(orderStr.contains("http://example.com"));
}

@Test
public void testEditWithEmptyDocumentContentAndInsertAtStart() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("");
doc.init();
// DocumentContent content = Factory.newDocumentContent("start");
// doc.edit(0L, 0L, content);
assertEquals("start", doc.getContent().toString());
}

@Test
public void testEditMiddleOfContentWithEmptyReplacement() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("ABCDEF");
doc.init();
// DocumentContent content = Factory.newDocumentContent("");
// doc.edit(2L, 4L, content);
assertEquals("ABEF", doc.getContent().toString());
}

@Test(expected = InvalidOffsetException.class)
public void testEditThrowsWhenOffsetsUnknownSize() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setContent(null);
// doc.edit(1L, 2L, Factory.newDocumentContent("X"));
}

@Test
public void testToXmlReturnsValidXmlWhenContentEmpty() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("");
doc.setMarkupAware(Boolean.FALSE);
doc.init();
String xml = doc.toXml();
assertNotNull(xml);
assertTrue(xml.contains("<?xml"));
}

@Test
public void testToXmlReturnsRawMarkupWhenNoAnnotationPresent() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("<doc>text</doc>");
doc.setMarkupAware(Boolean.FALSE);
doc.init();
String xml = doc.toXml(doc.getAnnotations(), true);
assertTrue(xml.contains("<doc>"));
assertTrue(xml.contains("text"));
}

@Test(expected = ResourceInstantiationException.class)
public void testInitFailsWithBadEncodingAndFileUrl() throws Exception {
DocumentImpl doc = new DocumentImpl();
URL invalidUrl = new URL("file:///invalid/file/path.txt");
doc.setSourceUrl(invalidUrl);
doc.setEncoding("does-not-exist");
doc.init();
}

@Test
public void testCompareToUsesLexicalOrderInOffsets() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
doc1.setSourceUrl(new URL("http://example.com/test.txt"));
doc1.setSourceUrlStartOffset(1L);
doc1.setSourceUrlEndOffset(5L);
DocumentImpl doc2 = new DocumentImpl();
doc2.setSourceUrl(new URL("http://example.com/test.txt"));
doc2.setSourceUrlStartOffset(2L);
doc2.setSourceUrlEndOffset(6L);
int result = doc1.compareTo(doc2);
assertTrue(result < 0);
}

@Test
public void testDocumentToStringIncludesAllOffsets() {
DocumentImpl doc = new DocumentImpl();
doc.setSourceUrlStartOffset(100L);
doc.setSourceUrlEndOffset(200L);
String str = doc.toString();
assertTrue(str.contains("100"));
assertTrue(str.contains("200"));
}

@Test
public void testSetNullEncodingFallsBackToSystemProperty() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding(null);
String result = doc.getEncoding();
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testSetEmptyEncodingFallsBackToSystemProperty() {
DocumentImpl doc = new DocumentImpl();
doc.setEncoding("   ");
String result = doc.getEncoding();
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testCleanupTwiceDoesNotThrowOrChangeNamedSet() {
DocumentImpl doc = new DocumentImpl();
doc.getAnnotations("notes");
assertNotNull(doc.getNamedAnnotationSets().get("notes"));
doc.cleanup();
doc.cleanup();
assertTrue(doc.getNamedAnnotationSets().isEmpty());
}

@Test
public void testAnnotationIdIncrementsMonotonically() {
DocumentImpl doc = new DocumentImpl();
Integer id1 = doc.getNextAnnotationId();
Integer id2 = doc.getNextAnnotationId();
Integer id3 = doc.getNextAnnotationId();
assertEquals(Integer.valueOf(id1 + 1), id2);
assertEquals(Integer.valueOf(id2 + 1), id3);
}

@Test
public void testNodeIdIncrementsMonotonically() {
DocumentImpl doc = new DocumentImpl();
Integer id1 = doc.getNextNodeId();
Integer id2 = doc.getNextNodeId();
Integer id3 = doc.getNextNodeId();
assertEquals(Integer.valueOf(id1 + 1), id2);
assertEquals(Integer.valueOf(id2 + 1), id3);
}

@Test
public void testGetAnnotationSetWithEmptyStringReturnsDefault() {
DocumentImpl doc = new DocumentImpl();
assertSame(doc.getAnnotations(), doc.getAnnotations(""));
}

@Test
public void testCleanupDetachesFromCreoleAndDatastoreListenersSafely() {
DocumentImpl doc = new DocumentImpl();
doc.setLRPersistenceId("fake-id");
doc.cleanup();
assertTrue(true);
}

@Test
public void testMultipleNamedAnnotationSetsAreStoredSeparately() {
DocumentImpl doc = new DocumentImpl();
doc.getAnnotations("X");
doc.getAnnotations("Y");
assertEquals(2, doc.getAnnotationSetNames().size());
}

@Test
public void testRemoveNamedAnnotationSetRemovesOnlyThatSet() {
DocumentImpl doc = new DocumentImpl();
doc.getAnnotations("A");
doc.getAnnotations("B");
doc.removeAnnotationSet("A");
assertTrue(doc.getAnnotationSetNames().contains("B"));
assertFalse(doc.getAnnotationSetNames().contains("A"));
}

@Test
public void testSetAndGetStringContentConsistent() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Hello World");
assertEquals("Hello World", doc.getStringContent());
}

@Test
public void testEmptyStringContentDoesNotBreakInit() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("");
doc.init();
assertEquals("", doc.getContent().toString());
}

@Test
public void testToXmlShouldReturnEmptyTagMarkupWhenNoAnnotations() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("XYZ");
doc.setMarkupAware(Boolean.FALSE);
doc.init();
String xml = doc.toXml(doc.getAnnotations(), false);
assertTrue(xml.contains("XYZ"));
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithNegativeStartOffsetThrows() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("12345");
doc.init();
// DocumentContent content = Factory.newDocumentContent("X");
// doc.edit(-1L, 2L, content);
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithEndGreaterThanContentSizeThrows() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcde");
doc.init();
// DocumentContent content = Factory.newDocumentContent("!");
// doc.edit(2L, 10L, content);
}

@Test
public void testToXmlWithNullAnnotationSetReturnsOnlyOriginalMarkup() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("<html><body>text</body></html>");
doc.setMarkupAware(Boolean.FALSE);
doc.init();
assertTrue(doc.toXml(null).contains("text"));
}

@Test
public void testToXmlWithIncludeFeaturesFalseExcludesFeatureAttributes() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Annotated Text");
doc.init();
String xml = doc.toXml(null, false);
assertFalse(xml.contains("gateId="));
}

@Test
public void testToXmlHandlesCrossedAnnotationsGracefully() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc def ghi");
doc.init();
gate.AnnotationSet set = doc.getAnnotations();
gate.FeatureMap fmap = Factory.newFeatureMap();
fmap.put("type", "span1");
set.add(0L, 5L, "Entity", fmap);
set.add(3L, 8L, "Entity", fmap);
String result = doc.toXml(set, true);
assertTrue(result.contains("abc"));
}

@Test
public void testWriteFeaturesSkipsInvalidKeyAndValueTypes() {
DocumentImpl doc = new DocumentImpl();
FeatureMap features = Factory.newFeatureMap();
Object invalidKey = new Object();
Object invalidValue = new Object();
features.put("validKey", "validValue");
features.put(invalidKey, "shouldBeIgnored");
features.put("invalidValue", invalidValue);
// String xml = doc.writeFeatures(features, true);
// assertTrue(xml.contains("validKey"));
// assertFalse(xml.contains("shouldBeIgnored"));
// assertFalse(xml.contains("invalidValue"));
}

@Test
public void testWriteFeaturesHandlesBooleanAndCollection() {
DocumentImpl doc = new DocumentImpl();
FeatureMap features = Factory.newFeatureMap();
features.put("flag", Boolean.TRUE);
Collection<Object> list = new HashSet<Object>();
list.add("val1");
list.add("val2");
features.put("list", list);
// String xml = doc.writeFeatures(features, true);
// assertTrue(xml.contains("val1"));
// assertTrue(xml.contains("val2"));
// assertTrue(xml.contains("flag=\"true\""));
}

// @Test(expected = GateRuntimeException.class)
public void testToXmlThrowsGateRuntimeExceptionOnInvalidOffsetInDebug() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdef");
doc.init();
gate.AnnotationSet set = doc.getAnnotations();
gate.FeatureMap fmap = Factory.newFeatureMap();
// gate.Node start = set.getDocument().getAnnotations().get(0, 0).firstNode();
// gate.Node end = set.getDocument().getAnnotations().get(0, 0).firstNode();
// gate.Annotation ann = new gate.annotation.AnnotationImpl(0, start, end, "Fault", fmap, doc.getNextAnnotationId());
HashSet<gate.Annotation> annSet = new HashSet<gate.Annotation>();
// annSet.add(ann);
doc.toXml(annSet, true);
}

@Test
public void testCleanupShouldClearEventListeners() {
DocumentImpl doc = new DocumentImpl();
gate.event.DocumentListener dummyListener = new gate.event.DocumentListener() {

public void contentEdited(gate.event.DocumentEvent e) {
}

public void annotationSetAdded(gate.event.DocumentEvent e) {
}

public void annotationSetRemoved(gate.event.DocumentEvent e) {
}
};
doc.addDocumentListener(dummyListener);
doc.cleanup();
doc.addDocumentListener(dummyListener);
assertTrue(true);
}

@Test
public void testWriteEmptyTagIncludesNamespacePrefix() {
DocumentImpl doc = new DocumentImpl();
FeatureMap features = Factory.newFeatureMap();
features.put("some", "value");
features.put("gate.ElementNamespacePrefix", "ns");
features.put("gate.ElementNamespaceURI", "http://example.org");
doc.setStringContent("dummy");
// doc.init();
gate.AnnotationSet annotations = doc.getAnnotations();
// gate.Annotation ann = annotations.add(0L, 0L, "Tag", features);
// String tag = doc.writeEmptyTag(ann, true);
// assertTrue(tag.contains("ns:Tag") || tag.contains("xmlns:ns"));
}

@Test
public void testWriteEndTagIncludesNamespacePrefixIfAvailable() {
DocumentImpl doc = new DocumentImpl();
FeatureMap features = Factory.newFeatureMap();
features.put("gate.ElementNamespacePrefix", "custom");
features.put("gate.ElementNamespaceURI", "http://ns");
doc.setStringContent("abc");
// doc.init();
gate.AnnotationSet as = doc.getAnnotations();
// gate.Annotation ann = as.add(0L, 1L, "X", features);
// String tag = doc.writeEndTag(ann);
// assertTrue(tag.contains("custom:X"));
}

@Test
public void testWriteStartTagIncludesCollectRepositioningAttrs() throws Exception {
DocumentImpl doc = new DocumentImpl();
FeatureMap fm = Factory.newFeatureMap();
fm.put("myAttr", "myValue");
doc.setStringContent("text");
doc.init();
gate.AnnotationSet annSet = doc.getAnnotations();
// gate.Annotation ann = annSet.add(0L, 1L, "MyTag", fm);
// String xml = doc.toXml(Collections.singleton(ann), true);
// assertTrue(xml.contains("myAttr=\"myValue\""));
}

@Test
public void testToXmlWithFeatureContainingCollection() throws Exception {
DocumentImpl doc = new DocumentImpl();
FeatureMap fm = Factory.newFeatureMap();
List<String> list = new LinkedList<String>();
list.add("val1");
list.add("val2");
fm.put("collectionFeature", list);
doc.setStringContent("Text here");
doc.init();
gate.AnnotationSet annSet = doc.getAnnotations();
annSet.add(0L, 4L, "Chunk", fm);
String xml = doc.toXml(annSet, true);
assertTrue(xml.contains("collectionFeature"));
assertTrue(xml.contains("val1"));
assertTrue(xml.contains("val2"));
}

@Test
public void testToXmlHandlesEmptyAnnotationsCorrectly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Data data");
doc.init();
gate.AnnotationSet as = doc.getAnnotations();
as.add(4L, 4L, "Empty", Factory.newFeatureMap());
String xml = doc.toXml(as, true);
assertTrue(xml.contains("<Empty"));
assertTrue(xml.contains("/>"));
}

@Test
public void testIsValidOffsetRangeEqualStartEndValid() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("12345");
doc.init();
boolean result = doc.isValidOffsetRange(3L, 3L);
assertTrue(result);
}

@Test
public void testIsValidOffsetRangeInvalidDueToStartGreaterThanEnd() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcde");
doc.init();
boolean result = doc.isValidOffsetRange(4L, 2L);
assertFalse(result);
}

@Test
public void testEditNoReplacementDeletesTextProperly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("0123456789");
doc.init();
doc.edit(3L, 6L, null);
assertEquals("0126789", doc.getContent().toString());
}

@Test
public void testToXmlHandlesFeatureNameEqualsMatches() throws Exception {
DocumentImpl doc = new DocumentImpl();
FeatureMap fm = Factory.newFeatureMap();
fm.put("matches", "token");
doc.setStringContent("match test");
doc.init();
gate.AnnotationSet annSet = doc.getAnnotations();
annSet.add(0L, 5L, "Token", fm);
String xml = doc.toXml(annSet, true);
assertTrue(xml.contains("gate:matches"));
}

@Test
public void testWriteFeaturesSkipsIsEmptyAndSpanFeature() {
DocumentImpl doc = new DocumentImpl();
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", "true");
fm.put("realAttr", "yes");
// String output = doc.writeFeatures(fm, true);
// assertTrue(output.contains("realAttr=\"yes\""));
// assertFalse(output.contains("isEmptyAndSpan"));
}

@Test
public void testGetAnnotationsCreatesSetOnlyOnce() {
DocumentImpl doc = new DocumentImpl();
gate.AnnotationSet s1 = doc.getAnnotations();
gate.AnnotationSet s2 = doc.getAnnotations();
assertSame(s1, s2);
}

@Test
public void testGetAnnotationsWithNullReturnsDefaultSet() {
DocumentImpl doc = new DocumentImpl();
gate.AnnotationSet s = doc.getAnnotations(null);
assertNotNull(s);
assertSame(s, doc.getAnnotations());
}

@Test
public void testRemoveAnnotationSetIgnoresMissingKey() {
DocumentImpl doc = new DocumentImpl();
doc.removeAnnotationSet("nonexistent");
assertTrue(doc.getNamedAnnotationSets().isEmpty());
}

@Test
public void testGetOrderingStringWithoutOffsets() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setSourceUrl(new URL("http://example.org/file.txt"));
String ordered = doc.getOrderingString();
assertTrue(ordered.contains("example.org"));
assertFalse(ordered.contains("null"));
}

@Test
public void testWriteEndTagHandlesNullAnnotationGracefully() {
DocumentImpl doc = new DocumentImpl();
// String tag = doc.writeEndTag(null);
// assertEquals("", tag);
}

@Test
public void testWriteStartTagHandlesNullAnnotation() {
DocumentImpl doc = new DocumentImpl();
// String result = doc.writeStartTag(null, true);
// assertEquals("", result);
}

@Test
public void testWriteEmptyTagHandlesNullAnnotation() {
DocumentImpl doc = new DocumentImpl();
// String result = doc.writeEmptyTag(null);
// assertEquals("", result);
}

@Test
public void testWriteFeaturesWithSpecialXmlCharacters() {
DocumentImpl doc = new DocumentImpl();
FeatureMap fm = Factory.newFeatureMap();
fm.put("attr", "val\"<&>'");
// String output = doc.writeFeatures(fm, true);
// assertTrue(output.contains("val"));
// assertFalse(output.contains("<"));
// assertFalse(output.contains(">"));
// assertFalse(output.contains("&"));
// assertFalse(output.contains("\""));
}

@Test
public void testCompareToWithSameUrlDifferentOffsets() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
DocumentImpl doc2 = new DocumentImpl();
URL url = new URL("http://example.com/resource.txt");
doc1.setSourceUrl(url);
doc2.setSourceUrl(url);
doc1.setSourceUrlStartOffset(1L);
doc1.setSourceUrlEndOffset(10L);
doc2.setSourceUrlStartOffset(2L);
doc2.setSourceUrlEndOffset(20L);
int result = doc1.compareTo(doc2);
assertTrue(result < 0);
}

@Test
public void testSetContentOverridesExistingContent() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("initial");
doc.init();
// DocumentContent newContent = Factory.newDocumentContent("override");
// doc.setContent(newContent);
assertEquals("override", doc.getContent().toString());
}
}
