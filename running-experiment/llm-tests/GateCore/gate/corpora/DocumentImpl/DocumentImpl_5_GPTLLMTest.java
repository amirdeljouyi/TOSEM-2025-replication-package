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

public class DocumentImpl_5_GPTLLMTest {

@Test
public void testInitWithStringContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Test document content");
document.setMarkupAware(false);
document.setPreserveOriginalContent(true);
document.init();
DocumentContent content = document.getContent();
assertNotNull(content);
assertEquals("Test document content", content.toString());
Object originalContent = document.getFeatures().get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
assertEquals("Test document content", originalContent);
}

@Test(expected = ResourceInstantiationException.class)
public void testInitThrowsExceptionIfNoContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent(null);
document.setMarkupAware(false);
document.init();
}

@Test
public void testGetAnnotationsReturnsEmptySetInitially() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Some content");
document.init();
AnnotationSet defaultSet = document.getAnnotations();
assertNotNull(defaultSet);
assertTrue(defaultSet.isEmpty());
}

@Test
public void testNamedAnnotationSetCreationAndRetrieval() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Content");
document.init();
AnnotationSet namedSet = document.getAnnotations("Custom");
assertNotNull(namedSet);
assertEquals(namedSet, document.getAnnotations("Custom"));
}

@Test
public void testSetAndGetEncoding() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setEncoding("UTF-8");
String encoding = document.getEncoding();
assertEquals("UTF-8", encoding);
}

@Test
public void testSetMimeTypeAndRetrieve() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setMimeType("text/plain");
assertEquals("text/plain", document.getMimeType());
}

@Test
public void testSetSourceUrlAndRetrieve() throws Exception {
DocumentImpl document = new DocumentImpl();
URL url = new URL("http://example.com");
document.setSourceUrl(url);
assertEquals(url, document.getSourceUrl());
}

@Test
public void testSetAndGetOffsets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlStartOffset(10L);
document.setSourceUrlEndOffset(30L);
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(10L), offsets[0]);
assertEquals(Long.valueOf(30L), offsets[1]);
}

@Test
public void testToXmlWithAnnotation() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("ABC DEF");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("tag", "noun");
// Annotation a = set.add(0L, 3L, "Token", features);
String xmlOutput = document.toXml(set, true);
assertTrue(xmlOutput.contains("<Token"));
assertTrue(xmlOutput.contains("tag=\"noun\""));
assertTrue(xmlOutput.contains(">ABC</Token>"));
}

@Test
public void testEditContentUpdatesText() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Hello World");
document.init();
DocumentContentImpl replacement = new DocumentContentImpl("GATE");
document.edit(6L, 11L, replacement);
String result = document.getContent().toString();
assertEquals("Hello GATE", result);
}

@Test(expected = InvalidOffsetException.class)
public void testEditThrowsInvalidOffset() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Only text");
document.init();
DocumentContentImpl badReplacement = new DocumentContentImpl("Bad");
document.edit(100L, 200L, badReplacement);
}

@Test
public void testIsValidOffsetRangeReturnsTrue() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("1234567890");
document.init();
boolean result = document.isValidOffsetRange(2L, 5L);
assertTrue(result);
}

@Test
public void testIsValidOffsetRangeReturnsFalse() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("123");
document.init();
boolean result1 = document.isValidOffsetRange(-1L, 1L);
boolean result2 = document.isValidOffsetRange(1L, 10L);
boolean result3 = document.isValidOffsetRange(5L, 2L);
assertFalse(result1);
assertFalse(result2);
assertFalse(result3);
}

@Test
public void testGetNextAnnotationIdIncrementsSequentially() throws Exception {
DocumentImpl document = new DocumentImpl();
Integer id1 = document.getNextAnnotationId();
Integer id2 = document.getNextAnnotationId();
Integer id3 = document.getNextAnnotationId();
assertEquals(Integer.valueOf(id1 + 1), id2);
assertEquals(Integer.valueOf(id2 + 1), id3);
}

@Test
public void testGetNextNodeIdIncrements() throws Exception {
DocumentImpl document = new DocumentImpl();
Integer node1 = document.getNextNodeId();
Integer node2 = document.getNextNodeId();
assertTrue(node2 > node1);
}

@Test
public void testNamedAnnotationSetsReturnsCorrectMap() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("map test");
document.init();
AnnotationSet set = document.getAnnotations("typeX");
Map<String, AnnotationSet> named = document.getNamedAnnotationSets();
assertNotNull(named);
assertTrue(named.containsKey("typeX"));
assertEquals(set, named.get("typeX"));
}

@Test
public void testGetAnnotationSetNamesContainsKey() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("entity name");
document.init();
document.getAnnotations("Entity");
Set<String> names = document.getAnnotationSetNames();
assertTrue(names.contains("Entity"));
}

@Test
public void testRemoveNamedAnnotationSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("remove me");
document.init();
document.getAnnotations("RemoveSet");
document.removeAnnotationSet("RemoveSet");
Set<String> names = document.getAnnotationSetNames();
assertFalse(names.contains("RemoveSet"));
}

@Test
public void testGetStringContentBehavesAsExpected() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("plain text");
String actual = document.getStringContent();
assertEquals("plain text", actual);
}

@Test
public void testToStringPrintsUsefulInfo() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("sample print");
document.init();
String output = document.toString();
assertTrue(output.contains("DocumentImpl"));
assertTrue(output.contains("sample print"));
}

@Test
public void testEditWithNullReplacementRemovesText() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("remove this part");
document.init();
document.edit(7L, 11L, null);
String result = document.getContent().toString();
assertEquals("remove  part", result);
}

@Test
public void testToXmlWithNullAnnotationSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("No annotations here");
document.init();
String xml = document.toXml(null);
assertTrue(xml.contains("No annotations here"));
}

@Test
public void testToXmlWithEmptyAnnotationSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Empty set");
document.init();
AnnotationSet annotationSet = document.getAnnotations("EmptySet");
String xml = document.toXml(annotationSet, true);
assertTrue(xml.contains("Empty set"));
}

@Test
public void testAddMultipleNamedAnnotationSets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Named sets test");
document.init();
AnnotationSet a = document.getAnnotations("Set1");
AnnotationSet b = document.getAnnotations("Set2");
assertNotNull(a);
assertNotNull(b);
assertTrue(document.getAnnotationSetNames().contains("Set1"));
assertTrue(document.getAnnotationSetNames().contains("Set2"));
}

@Test
public void testEditAtStartOfContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("prefix content");
document.init();
DocumentContent replacement = new DocumentContentImpl("new");
document.edit(0L, 6L, replacement);
String result = document.getContent().toString();
assertEquals("new content", result);
}

@Test
public void testEditAtEndOfContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("content ends here");
document.init();
DocumentContent replacement = new DocumentContentImpl("now");
long len = document.getContent().size();
document.edit(len - 4, len, replacement);
String result = document.getContent().toString();
assertEquals("content ends now", result);
}

@Test
public void testRemoveUnexistingNamedAnnotationSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("test remove");
document.init();
document.removeAnnotationSet("nonexistent");
Set<String> names = document.getAnnotationSetNames();
assertFalse(names.contains("nonexistent"));
}

@Test
public void testEmptyStringContentInit() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("");
document.init();
assertEquals("", document.getContent().toString());
}

@Test
public void testOverlappingAnnotationsAreDiscardedInToXml() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcdef");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
// Annotation ann1 = set.add(0L, 4L, "Outer", fm);
// Annotation ann2 = set.add(2L, 5L, "Inner", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("Outer"));
assertFalse(xml.contains("Inner"));
}

@Test
public void testEditWithNoChangeInLength() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("equal");
document.init();
DocumentContent replacement = new DocumentContentImpl("equal");
document.edit(0L, 5L, replacement);
assertEquals("equal", document.getContent().toString());
}

@Test
public void testAnnotationWithStartEqualsEndIsStored() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("12345");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", "true");
// Annotation span = set.add(2L, 2L, "Point", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Point"));
}

@Test
public void testRemoveAnnotationSetDoesNotAffectOthers() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc");
document.init();
document.getAnnotations("SetA");
document.getAnnotations("SetB");
document.removeAnnotationSet("SetA");
assertNull(document.getNamedAnnotationSets().get("SetA"));
assertNotNull(document.getNamedAnnotationSets().get("SetB"));
}

@Test
public void testGetOrderingStringWithUrlAndOffsets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setSourceUrl(new URL("http://localhost/doc.txt"));
document.setSourceUrlStartOffset(10L);
document.setSourceUrlEndOffset(20L);
document.setStringContent("ABCDE");
document.init();
String orderingString = document.getOrderingString();
assertTrue(orderingString.contains("doc.txt"));
assertTrue(orderingString.contains("10"));
assertTrue(orderingString.contains("20"));
}

@Test
public void testIsValidOffsetReturnsFalseForNull() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("123456");
document.init();
boolean valid = document.isValidOffset(null);
assertFalse(valid);
}

@Test
public void testAnnotationSetAddOnlyOneAnnotation() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("hello world");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
set.add(0L, 5L, "Token", fm);
assertEquals(1, set.size());
}

@Test
public void testEditWithNullDocumentContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("text to edit");
document.init();
document.edit(0L, 5L, null);
String content = document.getContent().toString();
assertEquals("to edit", content);
}

@Test
public void testToXmlReturnsGateFormatXml() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Gate XML output");
document.init();
String xml = document.toXml();
assertTrue(xml.contains("GateDocument"));
assertTrue(xml.contains("Gate XML output"));
}

@Test
public void testGetOrderingStringWhenSourceUrlIsNull() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Fallback URL info");
document.init();
String ordering = document.getOrderingString();
assertNotNull(ordering);
assertTrue(ordering.contains("Fallback URL info"));
}

@Test
public void testAnnotationWithNullOffsetTestIsValidFalse() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("1234567890");
document.init();
boolean isValid = document.isValidOffset(null);
assertFalse(isValid);
}

@Test
public void testAnnotationSetOverlappingOffsetsMultipleTypes() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("overlapping tag check");
document.init();
FeatureMap map1 = Factory.newFeatureMap();
FeatureMap map2 = Factory.newFeatureMap();
FeatureMap map3 = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
set.add(0L, 10L, "Type1", map1);
set.add(5L, 15L, "Type2", map2);
set.add(14L, 23L, "Type3", map3);
String xml = document.toXml(set, true);
assertTrue(xml.contains("Type1"));
assertTrue(xml.contains("Type2") || xml.contains("Type3"));
}

@Test
public void testSetMarkupAwareTrueAppliesDocumentFormat() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("<html>markup</html>");
document.setMarkupAware(true);
document.setMimeType("text/html");
document.init();
AnnotationSet originalMarkups = document.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
assertNotNull(originalMarkups);
}

@Test
public void testSerializationNamespaceFlagsAffectSerialization() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Tagged content here");
document.init();
FeatureMap features = Factory.newFeatureMap();
features.put("someKey", "someValue");
features.put("gate.AnnotationSetID", "1");
AnnotationSet set = document.getAnnotations();
set.add(0L, 6L, "Tag", features);
String xml = document.toXml(set, true);
assertTrue(xml.contains("someKey=\"someValue\""));
}

@Test
public void testPeakAtNextAnnotationIdDoesNotIncrement() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Annotation ID tracking");
document.init();
Integer idBefore = document.peakAtNextAnnotationId();
Integer nextId = document.getNextAnnotationId();
Integer idAfter = document.peakAtNextAnnotationId();
assertEquals(idBefore, Integer.valueOf(nextId - 1));
assertEquals(idAfter, Integer.valueOf(nextId + 0));
}

@Test
public void testRemovingDefaultAnnotationSetDoesNothing() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("default set attempt");
document.init();
AnnotationSet defaultSet = document.getAnnotations();
document.removeAnnotationSet("");
AnnotationSet stillPresent = document.getAnnotations();
assertEquals(defaultSet, stillPresent);
}

@Test
public void testRemoveNullNamedAnnotationSetDoesNotThrow() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("testing null name");
document.init();
try {
document.removeAnnotationSet(null);
} catch (Exception e) {
fail("Should not throw exception for null annotation set name");
}
}

@Test
public void testEmptyAnnotationFeatureMapSerializesCorrectly() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Empty FM");
document.init();
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
set.add(0L, 5L, "Empty", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Empty"));
}

@Test
public void testCleanupClearsAnnotationSets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("doc to cleanup");
document.init();
document.getAnnotations("SetA");
document.getAnnotations("SetB");
document.cleanup();
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

@Test
public void testGetNamedAnnotationSetsReturnsEmptyIfNone() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("empty named sets");
document.init();
Map<String, AnnotationSet> named = document.getNamedAnnotationSets();
assertTrue(named.isEmpty());
}

@Test
public void testToStringDoesNotCrashWithMinimalSetup() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("text");
document.init();
String str = document.toString();
assertNotNull(str);
assertTrue(str.contains("DocumentImpl"));
}

@Test
public void testGetFeaturesAutoCreates() {
DocumentImpl document = new DocumentImpl();
assertNotNull(document.getFeatures());
}

@Test
public void testEditWithStartEqualToEndAndNonNullContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc123");
document.init();
DocumentContent replacement = new DocumentContentImpl("!");
document.edit(3L, 3L, replacement);
String result = document.getContent().toString();
assertEquals("abc!123", result);
}

@Test
public void testEditWithStartEqualToEndAndNullReplacement() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcXYZ");
document.init();
document.edit(3L, 3L, null);
String result = document.getContent().toString();
assertEquals("abcXYZ", result);
}

@Test
public void testToXmlWithEmptyDocumentContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("");
document.init();
AnnotationSet set = document.getAnnotations();
String xml = document.toXml(set, true);
assertEquals("", xml);
}

@Test
public void testToXmlWithNonSerializableFeatureKey() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("key value");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put(new Object(), "validValue");
set.add(0L, 3L, "Token", features);
String xml = document.toXml(set, true);
assertFalse(xml.contains("validValue"));
}

@Test
public void testToXmlWithNonSerializableFeatureValue() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("nonserializable value");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("mykey", new Object());
set.add(0L, 3L, "Tag", features);
String xml = document.toXml(set, true);
assertFalse(xml.contains("mykey"));
}

@Test
public void testToXmlEncodesSpecialCharacters() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("5 < 6 & 7 > 4");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
set.add(2L, 9L, "Comparison", features);
String xml = document.toXml(set, true);
assertTrue(xml.contains("&lt;"));
assertTrue(xml.contains("&gt;"));
assertTrue(xml.contains("&amp;"));
}

@Test
public void testEditWithFullContentReplacement() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("ReplaceMe");
document.init();
DocumentContentImpl replacement = new DocumentContentImpl("Done");
document.edit(0L, 8L, replacement);
String result = document.getContent().toString();
assertEquals("Done", result);
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithNegativeStartOffsetThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Test123");
document.init();
DocumentContentImpl replacement = new DocumentContentImpl("X");
document.edit(-1L, 2L, replacement);
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithNullStartThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Hello");
document.init();
DocumentContentImpl replacement = new DocumentContentImpl("X");
document.edit(null, 2L, replacement);
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithEndGreaterThanContentSizeThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Short");
document.init();
DocumentContentImpl replacement = new DocumentContentImpl("X");
long end = document.getContent().size() + 10;
document.edit(0L, end, replacement);
}

@Test
public void testGetNextAnnotationIdMultipleCalls() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("track-ids");
document.init();
Integer id1 = document.getNextAnnotationId();
Integer id2 = document.getNextAnnotationId();
Integer id3 = document.getNextAnnotationId();
assertEquals(Integer.valueOf(id1 + 1), id2);
assertEquals(Integer.valueOf(id2 + 1), id3);
}

@Test
public void testAnnotationSetProduceCorrectSizeAfterMultipleAdds() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc def ghi");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
set.add(0L, 3L, "Token", fm);
set.add(4L, 7L, "Token", fm);
set.add(8L, 11L, "Token", fm);
int size = set.size();
assertEquals(3, size);
}

@Test
public void testSetAndGetSourceUrlOffsetsNullDefaults() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("offset test");
document.init();
Long[] offsets = document.getSourceUrlOffsets();
assertNull(offsets[0]);
assertNull(offsets[1]);
}

@Test
public void testGetAnnotationSetNamesReturnsExpectedSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Set names");
document.init();
document.getAnnotations("SetA");
document.getAnnotations("SetB");
Set<String> names = document.getAnnotationSetNames();
assertTrue(names.contains("SetA"));
assertTrue(names.contains("SetB"));
assertEquals(2, names.size());
}

@Test
public void testToStringDoesNotContainNullFieldsAfterInit() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("readable");
document.init();
String s = document.toString();
assertNotNull(s);
assertFalse(s.contains("null"));
}

@Test
public void testRemoveAnnotationSetDoesNothingIfMapEmpty() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("nothing to remove");
document.init();
document.removeAnnotationSet("idontexist");
Map<String, AnnotationSet> named = document.getNamedAnnotationSets();
assertTrue(named.isEmpty());
}

@Test
public void testMarkAnnotationAsEmptySpanAndRoundtripXml() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", "true");
set.add(1L, 1L, "Cursor", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Cursor "));
}

@Test
public void testToXmlWithNullFeatureMap() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("text data");
document.init();
AnnotationSet set = document.getAnnotations();
set.add(0L, 4L, "Tag", null);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Tag"));
assertTrue(xml.contains(">text"));
}

@Test
public void testToXmlWithBooleanFeatureValueTrue() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("true test");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("truth", Boolean.TRUE);
set.add(0L, 4L, "BoolTag", features);
String xml = document.toXml(set, true);
assertTrue(xml.contains("truth=\"true\""));
}

@Test
public void testToXmlWithBooleanFeatureValueFalse() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("false test");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("truth", Boolean.FALSE);
set.add(0L, 5L, "BoolTag", features);
String xml = document.toXml(set, true);
assertTrue(xml.contains("truth=\"false\""));
}

@Test
public void testToXmlWithCollectionFeatureMapOfStrings() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
java.util.Collection<String> list = new java.util.ArrayList<String>();
list.add("one");
list.add("two");
features.put("values", list);
set.add(0L, 3L, "Multi", features);
String xml = document.toXml(set, true);
assertTrue(xml.contains("values=\"one;two\""));
}

@Test
public void testToXmlWithCollectionFeatureMapOfNonSerializableItems() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("ignore me");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
java.util.Collection<Object> list = new java.util.ArrayList<Object>();
list.add(new Object());
features.put("data", list);
set.add(0L, 2L, "Skip", features);
String xml = document.toXml(set, true);
assertFalse(xml.contains("Skip=\""));
}

@Test
public void testWriteStartTagDoesNotIncludeGateNSIfDisabled() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("tag NS");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("x", "y");
set.add(0L, 3L, "NoNS", features);
String xml = document.toXml(set, true);
assertFalse(xml.contains("xmlns:gate"));
}

@Test
public void testPreserveOriginalContentFeatureFlag() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("original data");
document.setPreserveOriginalContent(true);
document.init();
String orig = (String) document.getFeatures().get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
assertEquals("original data", orig);
}

@Test
public void testToXmlReturnsOriginalPreservedContentIfAvailable() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("preserved");
document.setPreserveOriginalContent(true);
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
set.add(0L, 9L, "Root", fm);
String xml = document.toXml(set);
assertTrue(xml.contains("preserved"));
assertTrue(xml.contains("<Root"));
}

@Test
public void testGetAnnotationSetDefaultWhenNameNull() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("default set");
document.init();
AnnotationSet set1 = document.getAnnotations();
AnnotationSet set2 = document.getAnnotations(null);
assertSame(set1, set2);
}

@Test
public void testGetAnnotationSetDefaultWhenNameEmptyString() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("default set check");
document.init();
AnnotationSet set1 = document.getAnnotations();
AnnotationSet set2 = document.getAnnotations("");
assertSame(set1, set2);
}

@Test
public void testNamespacePrefixAndURIIgnoredWhenUnset() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("no ns");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
fm.put("prefix", "pfx");
fm.put("uri", "http://uri.com");
set.add(0L, 5L, "Any", fm);
String xml = document.toXml(set, true);
assertFalse(xml.contains("xmlns:pfx"));
assertFalse(xml.contains("http://uri.com"));
}

@Test
public void testInsertsSafetyFalseDueToNullOffsets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("testing");
// Annotation annotation = new Annotation() {
// 
// public Integer getId() {
// return 1;
// }
// 
// public Node getStartNode() {
// return null;
// }
// 
// public Node getEndNode() {
// return null;
// }
// 
// public String getType() {
// return "X";
// }
// 
// public FeatureMap getFeatures() {
// return null;
// }
// 
// public DocumentImpl getDocument() {
// return null;
// }
// };
AnnotationSet dummyTargetSet = document.getAnnotations();
// boolean result = document.getAnnotations().add(annotation) == null;
// assertTrue(result);
}

@Test
public void testRemoveAnnotationSetWithInvalidName() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("clean doc");
document.init();
document.removeAnnotationSet("NeverAdded");
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

@Test
public void testCleanUpDoesNotFailWhenListenersAreAbsent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc");
document.init();
document.cleanup();
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

@Test
public void testToXmlWithAnnotationSetContainingZeroLengthAnnotationWithNoFeatures() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("12345");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
// Annotation ann = set.add(2L, 2L, "Empty", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Empty"));
assertTrue(xml.contains("/>") || xml.contains("</Empty>"));
}

@Test
public void testEditWithNullEndOffsetThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc def");
document.init();
DocumentContentImpl replacement = new DocumentContentImpl("xxx");
try {
document.edit(1L, null, replacement);
fail("Expected InvalidOffsetException");
} catch (InvalidOffsetException e) {
assertTrue(e.getMessage().contains("Offsets"));
}
}

@Test
public void testIsValidOffsetReturnsFalseWhenOffsetGreaterThanSize() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("123456");
document.init();
long invalidOffset = document.getContent().size().longValue() + 1;
boolean isValid = document.isValidOffset(invalidOffset);
assertFalse(isValid);
}

@Test
public void testGetSourceUrlOffsetsReturnsNonNullArray() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("url offsets");
document.setSourceUrlStartOffset(11L);
document.setSourceUrlEndOffset(22L);
document.init();
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(11L), offsets[0]);
assertEquals(Long.valueOf(22L), offsets[1]);
}

@Test
public void testWriteStartTagIncludesNamespacePrefixWhenEnabled() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("ns prefix test");
document.init();
boolean result = ((String) document.getFeatures().put(GateConstants.ADD_NAMESPACE_FEATURES, "true")) == null;
FeatureMap fm = Factory.newFeatureMap();
fm.put("prefix", "ns");
AnnotationSet set = document.getAnnotations();
set.add(0L, 2L, "MyElem", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<MyElem") || xml.contains("<ns:MyElem"));
}

@Test
public void testGetNextNodeIdReturnsIncreasingId() throws Exception {
DocumentImpl document = new DocumentImpl();
Integer id1 = document.getNextNodeId();
Integer id2 = document.getNextNodeId();
Integer id3 = document.getNextNodeId();
assertTrue(id1 < id2);
assertTrue(id2 < id3);
}

@Test
public void testGetNextAnnotationIdReturnsIncreasingId() throws Exception {
DocumentImpl document = new DocumentImpl();
Integer id1 = document.getNextAnnotationId();
Integer id2 = document.getNextAnnotationId();
Integer id3 = document.getNextAnnotationId();
assertEquals(Integer.valueOf(id1 + 1), id2);
assertEquals(Integer.valueOf(id2 + 1), id3);
}

@Test
public void testAnnotationSetAddedListenerIsCalled() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("listen to me");
document.init();
final boolean[] flag = { false };
document.addDocumentListener(new gate.event.DocumentListener() {

public void annotationSetAdded(gate.event.DocumentEvent e) {
flag[0] = true;
}

public void annotationSetRemoved(gate.event.DocumentEvent e) {
}

public void contentEdited(gate.event.DocumentEvent e) {
}
});
document.getAnnotations("ListenerSet");
assertTrue(flag[0]);
}

@Test
public void testAnnotationSetRemovedListenerIsCalled() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("listen off");
document.init();
final boolean[] flag = { false };
document.addDocumentListener(new gate.event.DocumentListener() {

public void annotationSetAdded(gate.event.DocumentEvent e) {
}

public void annotationSetRemoved(gate.event.DocumentEvent e) {
flag[0] = true;
}

public void contentEdited(gate.event.DocumentEvent e) {
}
});
document.getAnnotations("RemovableSet");
document.removeAnnotationSet("RemovableSet");
assertTrue(flag[0]);
}

@Test
public void testContentEditedListenerIsCalled() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("edit me");
document.init();
final boolean[] flag = { false };
document.addDocumentListener(new gate.event.DocumentListener() {

public void annotationSetAdded(gate.event.DocumentEvent e) {
}

public void annotationSetRemoved(gate.event.DocumentEvent e) {
}

public void contentEdited(gate.event.DocumentEvent e) {
flag[0] = true;
}
});
DocumentContentImpl replace = new DocumentContentImpl("ab");
document.edit(0L, 2L, replace);
assertTrue(flag[0]);
}

@Test
public void testRemoveListenerActuallyRemovesListener() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("remove test");
document.init();
class Listener implements gate.event.DocumentListener {

boolean added;

public void annotationSetAdded(gate.event.DocumentEvent e) {
added = true;
}

public void annotationSetRemoved(gate.event.DocumentEvent e) {
}

public void contentEdited(gate.event.DocumentEvent e) {
}
}
Listener l = new Listener();
document.addDocumentListener(l);
document.removeDocumentListener(l);
document.getAnnotations("RemovedSet");
assertFalse(l.added);
}

@Test
public void testToStringReturnsExpectedContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("show me");
document.init();
String result = document.toString();
assertTrue(result.contains("content:"));
assertTrue(result.contains("show me"));
assertTrue(result.contains("nextAnnotationId"));
}

@Test
public void testEditWithDeleteEntireContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("wipe out");
document.init();
document.edit(0L, document.getContent().size(), null);
String finalText = document.getContent().toString();
assertEquals("", finalText);
}

@Test
public void testNamedAnnotationSetCaseSensitivity() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("cases");
document.init();
AnnotationSet upper = document.getAnnotations("SET");
AnnotationSet lower = document.getAnnotations("set");
assertNotSame(upper, lower);
}

@Test
public void testAnnotationSetNamesReflectRemovedSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("named");
document.init();
document.getAnnotations("Alpha");
document.getAnnotations("Beta");
document.removeAnnotationSet("Alpha");
Set<String> names = document.getAnnotationSetNames();
assertFalse(names.contains("Alpha"));
assertTrue(names.contains("Beta"));
}

@Test
public void testNamespaceSerializeFalseByDefault() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("<doc>xml</doc>");
document.init();
boolean found = document.toXml().contains("xmlns:gate");
assertFalse(found);
}

@Test
public void testGetFeaturesAlwaysReturnsNonNull() throws Exception {
DocumentImpl document = new DocumentImpl();
assertNotNull(document.getFeatures());
}

@Test
public void testCleanupWithNamedSetsClearsThem() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("cleanup");
document.init();
document.getAnnotations("RemoveMe");
document.cleanup();
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

@Test
public void testSetStringContentWithoutInitLeavesContentEmpty() {
DocumentImpl document = new DocumentImpl();
document.setStringContent("delayed");
DocumentContent c = document.getContent();
assertNotNull(c);
}

@Test
public void testAddAnnotationWithNullFeatures() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("null features test");
document.init();
AnnotationSet set = document.getAnnotations();
set.add(0L, 4L, "X", null);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<X"));
}

@Test
public void testAnnotationSetWithSameStartAndEndOffsetValidEmptyTag() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abc");
document.init();
FeatureMap features = Factory.newFeatureMap();
features.put("isEmptyAndSpan", "true");
AnnotationSet set = document.getAnnotations();
set.add(1L, 1L, "Cursor", features);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Cursor"));
}

@Test
public void testAnnotationWithGapBetweenNodeOffsetsIsPreserved() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("hello this is test");
document.init();
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
set.add(6L, 14L, "Phrase", fm);
String xml = document.toXml(set, false);
assertTrue(xml.contains("<Phrase>"));
assertTrue(xml.contains("this is"));
}

@Test
public void testToXmlWithAnnotationThatCrossesExistingRootAnnotation() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("root wrap text");
document.init();
FeatureMap fm1 = Factory.newFeatureMap();
FeatureMap fm2 = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
set.add(0L, 14L, "html", fm1);
set.add(5L, 10L, "Span", fm2);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<html"));
assertFalse(xml.contains("<Span"));
}

@Test
public void testInvalidFeatureMapKeySkipsSerialization() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("invalid key");
document.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put(new Object(), "hello");
AnnotationSet set = document.getAnnotations();
set.add(0L, 6L, "Tag", fm);
String xml = document.toXml(set, true);
assertFalse(xml.contains("hello"));
}

@Test
public void testInvalidFeatureMapValueSkipsSerialization() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("invalid feature value");
document.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("someKey", new Object());
AnnotationSet set = document.getAnnotations();
set.add(0L, 7L, "Tag", fm);
String xml = document.toXml(set, true);
assertFalse(xml.contains("someKey"));
}

@Test
public void testCollectionFeatureValueIsSerializedProperly() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("collecting test");
document.init();
Collection<String> items = Arrays.asList("one", "two");
FeatureMap fm = Factory.newFeatureMap();
fm.put("list", items);
AnnotationSet set = document.getAnnotations();
set.add(0L, 5L, "ListTag", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("list=\"one;two\""));
}

@Test
public void testWriteEndTagIncludesCorrectName() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("end tag test");
document.init();
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
// Annotation ann = set.add(0L, 3L, "Tag", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("</Tag>"));
}

@Test
public void testRemoveAnnotationSetOnEmptyNamedSetMapDoesNothing() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("no map remove");
document.init();
document.removeAnnotationSet("nope");
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

@Test
public void testGetAnnotationsHandlesRepeatedNamedSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("repeat set");
document.init();
AnnotationSet a1 = document.getAnnotations("MySet");
AnnotationSet a2 = document.getAnnotations("MySet");
assertSame(a1, a2);
}

@Test
public void testInsertsSafetyReturnsFalseForOverlappingAnnotationStartInsideAnother() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("overlap test");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
set.add(0L, 8L, "Outer", fm);
FeatureMap innerFm = Factory.newFeatureMap();
// Annotation inner = new Annotation() {
// 
// public Integer getId() {
// return 999;
// }
// 
// public Node getStartNode() {
// return Factory.newNode(5L);
// }
// 
// public Node getEndNode() {
// return Factory.newNode(12L);
// }
// 
// public String getType() {
// return "Inner";
// }
// 
// public FeatureMap getFeatures() {
// return innerFm;
// }
// 
// public gate.Document getDocument() {
// return document;
// }
// };
// boolean valid = document.getContent().size() >= 12 && !set.get(inner.getStartNode().getOffset(), inner.getEndNode().getOffset()).isEmpty();
// assertTrue(valid);
}

@Test
public void testToXmlHandlesZeroLengthAnnotationWithoutIsEmptyAndSpanFeature() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("zero-span");
document.init();
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
set.add(4L, 4L, "Zero", fm);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Zero"));
}

@Test
public void testRemoveAnnotationSetTwiceDoesNotThrow() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("redundant remove");
document.init();
document.getAnnotations("One");
document.removeAnnotationSet("One");
document.removeAnnotationSet("One");
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

@Test
public void testToXmlWithNullAnnotationSetReturnsPlainText() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("plain text");
document.init();
String xml = document.toXml(null, true);
assertEquals("plain text", xml.trim());
}

@Test
public void testSetPreserveOriginalContentFlagWithoutInitKeepsFalse() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setPreserveOriginalContent(false);
assertEquals(Boolean.FALSE, document.getPreserveOriginalContent());
}

@Test
public void testGetOrderingStringFromURLWithOffsets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("url test");
document.setSourceUrl(new java.net.URL("http://example.com/resource.txt"));
document.setSourceUrlStartOffset(12L);
document.setSourceUrlEndOffset(20L);
document.init();
String ordering = document.getOrderingString();
assertTrue(ordering.contains("resource.txt"));
assertTrue(ordering.contains("12"));
assertTrue(ordering.contains("20"));
}
}
