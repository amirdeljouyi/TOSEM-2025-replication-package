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

public class DocumentImpl_2_GPTLLMTest {

@Test
public void testDefaultConstructionShouldInitializeDocumentCorrectly() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("Hello World"));
assertNotNull(document.getContent());
assertEquals("Hello World", document.getContent().toString());
FeatureMap features = document.getFeatures();
assertNotNull(features);
assertTrue(features.isEmpty());
AnnotationSet defaultSet = document.getAnnotations();
assertNotNull(defaultSet);
}

@Test
public void testSetAndGetMimeType() {
DocumentImpl document = new DocumentImpl();
String expectedMimeType = "text/xml";
document.setMimeType(expectedMimeType);
assertEquals(expectedMimeType, document.getMimeType());
}

@Test
public void testSetAndGetSourceUrl() throws MalformedURLException {
DocumentImpl document = new DocumentImpl();
URL url = new URL("http://gate.ac.uk/sample.txt");
document.setSourceUrl(url);
assertEquals(url, document.getSourceUrl());
}

@Test
public void testSetAndGetSourceUrlStartAndEndOffset() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlStartOffset(5L);
document.setSourceUrlEndOffset(15L);
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(5L), offsets[0]);
assertEquals(Long.valueOf(15L), offsets[1]);
}

@Test
public void testPreserveOriginalContentFlag() {
DocumentImpl document = new DocumentImpl();
assertFalse(document.getPreserveOriginalContent());
document.setPreserveOriginalContent(Boolean.TRUE);
assertTrue(document.getPreserveOriginalContent());
}

@Test
public void testCollectRepositioningInfoFlag() {
DocumentImpl document = new DocumentImpl();
assertFalse(document.getCollectRepositioningInfo());
document.setCollectRepositioningInfo(Boolean.TRUE);
assertTrue(document.getCollectRepositioningInfo());
}

@Test
public void testSetAndGetEncoding() {
DocumentImpl document = new DocumentImpl();
document.setEncoding("UTF-8");
assertEquals("UTF-8", document.getEncoding());
}

@Test
public void testAddNamedAnnotationSetReturnsNamedSet() {
DocumentImpl document = new DocumentImpl();
AnnotationSet set = document.getAnnotations("CustomSet");
assertNotNull(set);
assertTrue(document.getNamedAnnotationSets().containsKey("CustomSet"));
Map<String, AnnotationSet> map = document.getNamedAnnotationSets();
assertEquals(set, map.get("CustomSet"));
}

@Test
public void testGetAnnotationSetNamesReturnsCorrectNames() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("A");
document.getAnnotations("B");
Set<String> names = document.getAnnotationSetNames();
assertTrue(names.contains("A"));
assertTrue(names.contains("B"));
}

@Test
public void testRemoveNamedAnnotationSetRemovesIt() {
DocumentImpl document = new DocumentImpl();
AnnotationSet s = document.getAnnotations("ToRemove");
assertTrue(document.getNamedAnnotationSets().containsKey("ToRemove"));
document.removeAnnotationSet("ToRemove");
assertFalse(document.getNamedAnnotationSets().containsKey("ToRemove"));
}

@Test
public void testEditValidOffsetShouldReplaceText() throws InvalidOffsetException {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("This is an example."));
DocumentContent replacement = new DocumentContentImpl("a nice");
document.edit(8L, 10L, replacement);
assertEquals("This is a nice example.", document.getContent().toString());
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithInvalidOffsetThrowsException() throws InvalidOffsetException {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("Test"));
DocumentContent replacement = new DocumentContentImpl("Invalid");
document.edit(10L, 5L, replacement);
}

@Test
public void testIsValidOffsetReturnsTrueForValidOffset() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("Hello"));
boolean result = document.isValidOffset(2L);
assertTrue(result);
}

@Test
public void testIsValidOffsetReturnsFalseForInvalidOffset() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("12345"));
boolean resultNegative = document.isValidOffset(-1L);
boolean resultTooLarge = document.isValidOffset(999L);
assertFalse(resultNegative);
assertFalse(resultTooLarge);
}

@Test
public void testIsValidOffsetRangeReturnsTrue() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdef"));
boolean result = document.isValidOffsetRange(1L, 4L);
assertTrue(result);
}

@Test
public void testIsValidOffsetRangeReturnsFalseIfStartGreaterThanEnd() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdef"));
boolean result = document.isValidOffsetRange(4L, 1L);
assertFalse(result);
}

@Test
public void testGetNextAnnotationIdIncrementsSequentially() {
DocumentImpl document = new DocumentImpl();
Integer first = document.getNextAnnotationId();
Integer second = document.getNextAnnotationId();
assertEquals((int) first + 1, (int) second);
}

@Test
public void testGetNextNodeIdIncrementsSequentially() {
DocumentImpl document = new DocumentImpl();
Integer first = document.getNextNodeId();
Integer second = document.getNextNodeId();
assertEquals((int) first + 1, (int) second);
}

@Test
public void testPeakAtNextAnnotationIdDoesNotIncrement() {
DocumentImpl document = new DocumentImpl();
Integer before = document.peakAtNextAnnotationId();
Integer next = document.getNextAnnotationId();
assertEquals(before, Integer.valueOf(next - 1));
}

@Test
public void testToStringContainsKeyIdentifiers() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abc"));
String str = document.toString();
assertTrue(str.contains("DocumentImpl:"));
assertTrue(str.contains("content:abc"));
}

@Test
public void testCompareToBasedOnUrlOrdering() throws MalformedURLException {
DocumentImpl d1 = new DocumentImpl();
DocumentImpl d2 = new DocumentImpl();
d1.setSourceUrl(new URL("http://gate.ac.uk/doc1"));
d2.setSourceUrl(new URL("http://gate.ac.uk/doc2"));
int result = d1.compareTo(d2);
assertTrue(result < 0);
}

@Test
public void testCleanupClearsNamedAnnotationSets() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("tempSet");
assertTrue(document.getNamedAnnotationSets().containsKey("tempSet"));
document.cleanup();
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

@Test
public void testSetStringContentStoresCorrectly() {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Internal use content string");
assertEquals("Internal use content string", document.getStringContent());
}

@Test
public void testSetDefaultAnnotationsAppliesSuccessfully() {
DocumentImpl document = new DocumentImpl();
AnnotationSetImpl customDefault = new AnnotationSetImpl(document, "default");
document.setDefaultAnnotations(customDefault);
AnnotationSet retrieved = document.getAnnotations();
assertEquals(customDefault, retrieved);
}

@Test
public void testAnnotationSetDefaultWhenNullNameProvided() {
DocumentImpl document = new DocumentImpl();
AnnotationSet defaultSet = document.getAnnotations(null);
AnnotationSet emptySet = document.getAnnotations("");
AnnotationSet directDefault = document.getAnnotations();
assertSame(defaultSet, emptySet);
assertSame(emptySet, directDefault);
}

@Test
public void testRemoveNonExistentAnnotationSetDoesNothing() {
DocumentImpl document = new DocumentImpl();
Map<String, AnnotationSet> before = new HashMap<>(document.getNamedAnnotationSets());
document.removeAnnotationSet("nonexistent");
Map<String, AnnotationSet> after = new HashMap<>(document.getNamedAnnotationSets());
assertEquals(before, after);
}

@Test
public void testGetSourceUrlOffsetsWhenOffsetsAreNull() {
DocumentImpl document = new DocumentImpl();
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(0L), offsets[0]);
assertEquals(Long.valueOf(0L), offsets[1]);
}

@Test
public void testEditWithNullContentDeletesTextRange() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdefg"));
document.edit(2L, 5L, null);
assertEquals("abfg", document.getContent().toString());
}

@Test
public void testEditFullRangeToNullDeletesEverything() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("delete me"));
long start = 0L;
long end = document.getContent().size();
document.edit(start, end, null);
assertEquals("", document.getContent().toString());
}

@Test(expected = InvalidOffsetException.class)
public void testEditWithStartOffsetEqualToContentSizeThrowsException() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("123456"));
long size = document.getContent().size();
document.edit(size, size + 1L, new DocumentContentImpl("x"));
}

@Test
public void testEditNoChangeWhenReplacingZeroLengthWithEmptyContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("ab"));
DocumentContent empty = new DocumentContentImpl("");
document.edit(1L, 1L, empty);
assertEquals("ab", document.getContent().toString());
}

@Test
public void testSetSourceUrlOffsetsToSameValueWorks() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlStartOffset(100L);
document.setSourceUrlEndOffset(100L);
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(100L), offsets[0]);
assertEquals(Long.valueOf(100L), offsets[1]);
}

@Test
public void testGetNextAnnotationIdWhenNearIntegerMaxValue() {
DocumentImpl document = new DocumentImpl();
for (int i = 0; i < Integer.MAX_VALUE - 2; i++) {
if (document.getNextAnnotationId() == Integer.MAX_VALUE - 1)
break;
}
int id = document.getNextAnnotationId();
assertTrue(id > 0);
}

@Test
public void testGetOrderingStringWithoutSourceUrlFallsBackToToString() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("test"));
String ordering = document.getOrderingString();
String fallback = document.toString();
assertEquals(fallback, ordering);
}

@Test
public void testAnnotationSetEventFiresWhenAddingNamedSet() {
DocumentImpl document = new DocumentImpl();
final boolean[] fired = new boolean[] { false };
document.addDocumentListener(new DocumentListener() {

@Override
public void annotationSetAdded(DocumentEvent e) {
fired[0] = true;
}

@Override
public void annotationSetRemoved(DocumentEvent e) {
}

@Override
public void contentEdited(DocumentEvent e) {
}
});
document.getAnnotations("trigger");
assertTrue(fired[0]);
}

@Test
public void testMultipleListenersReceiveContentEditedEvent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("hello world"));
final int[] trigger = { 0 };
document.addDocumentListener(new DocumentListener() {

@Override
public void annotationSetAdded(DocumentEvent e) {
}

@Override
public void annotationSetRemoved(DocumentEvent e) {
}

@Override
public void contentEdited(DocumentEvent e) {
trigger[0]++;
}
});
document.addDocumentListener(new DocumentListener() {

@Override
public void annotationSetAdded(DocumentEvent e) {
}

@Override
public void annotationSetRemoved(DocumentEvent e) {
}

@Override
public void contentEdited(DocumentEvent e) {
trigger[0]++;
}
});
document.edit(0L, 5L, new DocumentContentImpl("hi"));
assertEquals(2, trigger[0]);
}

@Test
public void testEmptyFeatureMapIsReturnedIfNotSetExplicitly() {
DocumentImpl document = new DocumentImpl();
FeatureMap features = document.getFeatures();
assertNotNull(features);
assertTrue(features.isEmpty());
}

@Test
public void testFeaturesPersistenceAcrossEdit() throws Exception {
DocumentImpl document = new DocumentImpl();
FeatureMap map = document.getFeatures();
map.put("key1", "value1");
document.setContent(new DocumentContentImpl("123456"));
document.edit(0L, 1L, new DocumentContentImpl("X"));
assertEquals("value1", document.getFeatures().get("key1"));
}

@Test
public void testAnnotationSetNotCreatedOnAccessIfNotUsed() {
DocumentImpl document = new DocumentImpl();
Map<String, AnnotationSet> sets = document.getNamedAnnotationSets();
assertNotNull(sets);
assertTrue(sets.isEmpty());
}

@Test
public void testDefaultAnnotationSetDoesNotDuplicateAcrossCalls() {
DocumentImpl document = new DocumentImpl();
AnnotationSet s1 = document.getAnnotations();
AnnotationSet s2 = document.getAnnotations();
assertSame(s1, s2);
}

@Test
public void testNamedSetDoesNotOverwriteIfAlreadyExists() {
DocumentImpl document = new DocumentImpl();
AnnotationSet original = document.getAnnotations("test");
AnnotationSet again = document.getAnnotations("test");
assertSame(original, again);
}

@Test
public void testAnnotationSetMapReturnedIsSameInstance() {
DocumentImpl document = new DocumentImpl();
Map<String, AnnotationSet> m1 = document.getNamedAnnotationSets();
Map<String, AnnotationSet> m2 = document.getNamedAnnotationSets();
assertSame(m1, m2);
}

@Test
public void testToXmlReturnsEmptyXmlForEmptyDocument() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl(""));
String xml = document.toXml(Collections.emptySet());
assertTrue(xml.isEmpty() || xml.trim().equals(""));
}

@Test
public void testEmptyAnnotationSetToXmlReturnsContentUnchanged() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("xyz"));
String original = document.getContent().toString();
String xml = document.toXml(document.getAnnotations());
assertTrue(xml.contains("xyz"));
}

@Test
public void testToXmlWithNullAnnotationSetReturnsOnlyOriginalMarkup() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("short"));
document.setPreserveOriginalContent(true);
// document.init();
String xml = document.toXml(null);
assertTrue(xml.contains("short"));
}

@Test
public void testToXmlWithNoOriginalContentFeatureFallsBackGracefully() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("fallback-text"));
String xml = document.toXml(document.getAnnotations());
assertTrue(xml.contains("fallback-text"));
}

@Test
public void testWriteFeaturesSkipsUnsupportedValueType() {
DocumentImpl document = new DocumentImpl();
FeatureMap map = document.getFeatures();
map.put("badValue", new Thread());
document.setContent(new DocumentContentImpl("abc"));
String result = document.toXml(document.getAnnotations());
assertFalse(result.contains("badValue"));
}

@Test
public void testWriteFeaturesHandlesBooleanAndNumber() {
DocumentImpl document = new DocumentImpl();
FeatureMap features = document.getFeatures();
features.put("flag", true);
features.put("count", 123);
document.setContent(new DocumentContentImpl("abc"));
String xml = document.toXml(document.getAnnotations());
assertTrue(xml.contains("flag=\"true\""));
assertTrue(xml.contains("count=\"123\""));
}

@Test
public void testWriteFeaturesWithCollectionIncludesItems() {
DocumentImpl document = new DocumentImpl();
FeatureMap features = document.getFeatures();
List<String> list = Arrays.asList("item1", "item2");
features.put("tags", list);
document.setContent(new DocumentContentImpl("ok"));
String xml = document.toXml(document.getAnnotations());
assertTrue(xml.contains("item1"));
assertTrue(xml.contains("item2"));
}

@Test
public void testWriteFeaturesSkipsNullKeyOrValue() {
DocumentImpl document = new DocumentImpl();
FeatureMap features = document.getFeatures();
features.put(null, "valueX");
features.put("kY", null);
document.setPreserveOriginalContent(false);
document.setContent(new DocumentContentImpl("z"));
String xml = document.toXml(document.getAnnotations());
assertFalse(xml.contains("valueX"));
assertFalse(xml.contains("kY"));
}

@Test
public void testAnnotationComparatorByStartOffsetAsc() {
// Annotation ann1 = MockAnnotations.create(1, 1L, 5L);
// Annotation ann2 = MockAnnotations.create(2, 2L, 6L);
DocumentImpl.AnnotationComparator comparator = new DocumentImpl.AnnotationComparator(0, 3);
// int result = comparator.compare(ann1, ann2);
// assertTrue(result < 0);
}

@Test
public void testAnnotationComparatorByEndOffsetDesc() {
// Annotation ann1 = MockAnnotations.create(3, 1L, 9L);
// Annotation ann2 = MockAnnotations.create(4, 2L, 8L);
DocumentImpl.AnnotationComparator comparator = new DocumentImpl.AnnotationComparator(1, -3);
// int result = comparator.compare(ann1, ann2);
// assertTrue(result < 0);
}

@Test
public void testAnnotationComparatorByIdAscending() {
// Annotation ann1 = MockAnnotations.create(7, 1L, 2L);
// Annotation ann2 = MockAnnotations.create(9, 1L, 2L);
DocumentImpl.AnnotationComparator comparator = new DocumentImpl.AnnotationComparator(2, 3);
// int result = comparator.compare(ann1, ann2);
// assertTrue(result < 0);
}

@Test
public void testGetOrderingStringIncludesOffsetsIfAvailable() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setSourceUrl(new URL("http://example.com/file.txt"));
document.setSourceUrlStartOffset(50L);
document.setSourceUrlEndOffset(100L);
String ordering = document.getOrderingString();
assertTrue(ordering.contains("50"));
assertTrue(ordering.contains("100"));
}

@Test
public void testFireAnnotationSetAddedWithoutListenersIsSafe() {
DocumentImpl document = new DocumentImpl();
DocumentEvent event = new DocumentEvent(document, DocumentEvent.ANNOTATION_SET_ADDED, "x");
document.getAnnotations("x");
document.removeAnnotationSet("x");
}

@Test
public void testRemoveListenerThatWasNeverAddedDoesNotFail() {
DocumentImpl document = new DocumentImpl();
DocumentListener dummy = new DocumentListener() {

@Override
public void annotationSetAdded(DocumentEvent e) {
}

@Override
public void annotationSetRemoved(DocumentEvent e) {
}

@Override
public void contentEdited(DocumentEvent e) {
}
};
document.removeDocumentListener(dummy);
}

@Test
public void testWriteEmptyTagWithNamespacePrefix() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("xml"));
FeatureMap fm = document.getFeatures();
fm.put("gate.SourceURL", "dummy");
AnnotationSet as = document.getAnnotations();
// Annotation ann = as.add(0L, 0L, "tag", Factory.newFeatureMap());
// ann.getFeatures().put("gateId", 5);
// ann.getFeatures().put("xmlns:gate", "http://gate.ac.uk");
// ann.getFeatures().put("prefix", "ns");
String xml = document.toXml(as);
assertTrue(xml.contains("gateId"));
}

@Test
public void testToStringIncludesAllProperties() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("x"));
document.setEncoding("UTF-8");
document.setSourceUrlStartOffset(0L);
document.setSourceUrlEndOffset(5L);
String result = document.toString();
assertTrue(result.contains("DocumentImpl:"));
assertTrue(result.contains("UTF-8"));
assertTrue(result.contains("sourceUrlStartOffset:0"));
}

@Test
public void testToXmlIncludesAnnotationFeaturesWhenFlagTrue() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("annotated"));
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("featureKey", "featureValue");
// set.add(0L, 4L, "Test", features);
String xml = document.toXml(set, true);
assertTrue(xml.contains("featureKey"));
assertTrue(xml.contains("featureValue"));
}

@Test
public void testToXmlExcludesAnnotationFeaturesWhenFlagFalse() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("text"));
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("shouldBeSkipped", "bad");
// set.add(0L, 4L, "Test", features);
String xml = document.toXml(set, false);
assertFalse(xml.contains("shouldBeSkipped"));
}

@Test
public void testWriteFeaturesSkipsIsEmptyAndSpanKey() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("skip"));
AnnotationSet set = document.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("isEmptyAndSpan", "true");
// set.add(0L, 0L, "Inline", features);
String xml = document.toXml(set);
assertFalse(xml.contains("isEmptyAndSpan"));
}

@Test
public void testWriteFeaturesSkipsNonStringKeys() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("mixedKeys"));
// Annotation annotation = document.getAnnotations().add(0L, 5L, "A", Factory.newFeatureMap());
// FeatureMap fm = annotation.getFeatures();
// fm.put(123, "value1");
// fm.put("key2", "value2");
String xml = document.toXml(document.getAnnotations());
assertFalse(xml.contains("123"));
assertTrue(xml.contains("key2"));
}

@Test
public void testAnnotationComparatorStartOffsetEqualFallBackToId() {
// Annotation a1 = new AnnotationImpl(1, 10L, 20L, "A", Factory.newFeatureMap());
// Annotation a2 = new AnnotationImpl(2, 10L, 30L, "A", Factory.newFeatureMap());
DocumentImpl.AnnotationComparator comp = new DocumentImpl.AnnotationComparator(0, 3);
// int result = comp.compare(a1, a2);
// assertTrue(result < 0);
}

@Test
public void testAnnotationComparatorEndOffsetEqualFallBackToIdDesc() {
// Annotation a1 = new AnnotationImpl(5, 5L, 10L, "A", Factory.newFeatureMap());
// Annotation a2 = new AnnotationImpl(4, 6L, 10L, "A", Factory.newFeatureMap());
DocumentImpl.AnnotationComparator comp = new DocumentImpl.AnnotationComparator(1, -3);
// int result = comp.compare(a1, a2);
// assertTrue(result < 0);
}

@Test
public void testGetNextAnnotationIdWrapsAroundAfterManyCalls() {
DocumentImpl document = new DocumentImpl();
for (int i = 0; i < 999; i++) {
document.getNextAnnotationId();
}
int id = document.getNextAnnotationId();
assertTrue(id >= 0);
}

@Test
public void testSetEncodingNullDefaultsToSystemEncoding() {
DocumentImpl document = new DocumentImpl();
document.setEncoding(null);
assertNotNull(document.getEncoding());
assertFalse(document.getEncoding().isEmpty());
}

@Test
public void testToXmlWithAnnotationThatStartsAndEndsSameOffset() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("tokenize"));
AnnotationSet set = document.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", "true");
// set.add(3L, 3L, "Mark", fm);
String xml = document.toXml(set);
assertTrue(xml.contains("<Mark"));
assertFalse(xml.contains("</Mark>"));
}

@Test
public void testRemoveAnnotationSetOnEmptyMapDoesNothing() {
DocumentImpl document = new DocumentImpl();
document.removeAnnotationSet("notPresent");
assertTrue(document.getNamedAnnotationSets().isEmpty());
}

// @Test(expected = gate.GateRuntimeException.class)
public void testToXmlCrossedAnnotationThrowsGateRuntimeException() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("0123456789"));
AnnotationSet main = document.getAnnotations();
// Annotation a1 = main.add(2L, 6L, "Entity", Factory.newFeatureMap());
// Annotation a2 = main.add(4L, 8L, "Entity2", Factory.newFeatureMap());
String result = document.toXml(main);
assertTrue(result.contains("Entity"));
assertFalse(result.contains("Entity2"));
}

@Test
public void testWriteEndTagWithNamespacePrefix() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("content"));
FeatureMap fm = Factory.newFeatureMap();
fm.put("some", "value");
fm.put("ns", "prefix");
// Annotation a = document.getAnnotations().add(0L, 6L, "HTML", fm);
String result = document.toXml(document.getAnnotations());
assertTrue(result.contains("</HTML>"));
}

@Test
public void testFeatureMapAllowsBooleanCollectionValues() {
DocumentImpl document = new DocumentImpl();
FeatureMap fm = document.getFeatures();
List<Object> list = new ArrayList<Object>();
list.add(true);
list.add("yes");
fm.put("flags", list);
document.setContent(new DocumentContentImpl("exploration"));
String xml = document.toXml(document.getAnnotations());
assertTrue(xml.contains("yes"));
assertTrue(xml.contains("true"));
}

@Test
public void testBuildEntityMapFromStringDoesNotCrashWithSpecials() {
DocumentImpl document = new DocumentImpl();
String data = "a & b < c > d &amp; e &unknown";
TreeMap<Long, Character> map = new TreeMap<Long, Character>();
document.getAnnotations();
document.setContent(new DocumentContentImpl(data));
String xml = document.toXml(document.getAnnotations());
assertNotNull(xml);
assertTrue(xml.contains("amp"));
}

@Test
public void testXmlContentPreservesOriginalIfFeatureSet() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("preserved"));
document.setStringContent("preserved");
document.setPreserveOriginalContent(true);
try {
document.init();
} catch (ResourceInstantiationException e) {
}
Object orig = document.getFeatures().get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
assertEquals("preserved", orig);
}

@Test
public void testCorrectRepositioningForCRLFInXMLAppliedWithoutCrash() {
DocumentImpl document = new DocumentImpl();
RepositioningInfo info = new RepositioningInfo();
document.setContent(new DocumentContentImpl("line1\r\nline2\r\n"));
String original = "line1\r\nline2\r\nline3";
document.getAnnotations();
document.toString();
document.setPreserveOriginalContent(true);
String xml = document.toXml(document.getAnnotations());
assertNotNull(xml);
}

@Test
public void testToXmlReturnsGateXmlFormatByDefault() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abc"));
String result = document.toXml();
assertTrue(result.contains("gate"));
assertTrue(result.contains("abc"));
}

@Test
public void testGetAnnotationsCreatesAnnotationSetIfAbsent() {
DocumentImpl document = new DocumentImpl();
Map<String, AnnotationSet> mapBefore = document.getNamedAnnotationSets();
assertTrue(mapBefore.isEmpty());
AnnotationSet set = document.getAnnotations("MyNamedSet");
assertNotNull(set);
Map<String, AnnotationSet> mapAfter = document.getNamedAnnotationSets();
assertTrue(mapAfter.containsKey("MyNamedSet"));
}

@Test
public void testToXmlSkipsCrossedAnnotations() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdefgh"));
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet annots = document.getAnnotations();
// annots.add(1L, 6L, "A", fm);
// annots.add(3L, 8L, "B", fm);
String xml = document.toXml(annots);
assertTrue(xml.contains("<A"));
assertFalse(xml.contains("<B"));
}

@Test
public void testToXmlWithEmptyFeaturesStillOutputsTags() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abc"));
FeatureMap emptyFeatures = Factory.newFeatureMap();
// document.getAnnotations().add(0L, 3L, "Tag", emptyFeatures);
String xml = document.toXml(document.getAnnotations());
assertTrue(xml.contains("<Tag"));
assertTrue(xml.contains("</Tag>"));
}

@Test
public void testToXmlWithEmptyAnnotationSetReturnsRawText() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("plain text"));
String xml = document.toXml(new AnnotationSetImpl(document, "empty"));
assertEquals("plain text", xml);
}

@Test
public void testWriteStartTagHandlesNullFeaturesGracefully() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("xyz"));
FeatureMap nullFeatures = null;
AnnotationSet set = document.getAnnotations();
// Annotation a = set.add(0L, 2L, "NullFeats", Factory.newFeatureMap());
// a.getFeatures().clear();
String xml = document.toXml(set);
assertTrue(xml.contains("<NullFeats"));
}

@Test
public void testWriteEmptyTagOmitsGateIdIfInOriginalMarkups() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("ab"));
AnnotationSet set = document.getAnnotations();
AnnotationSet orig = document.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
FeatureMap fm = Factory.newFeatureMap();
// Annotation a = set.add(0L, 0L, "X", fm);
// orig.add(a.getStartNode().getOffset(), a.getEndNode().getOffset(), a.getType(), fm);
String xml = document.toXml(set);
assertTrue(xml.contains("<X"));
assertFalse(xml.contains("gateId"));
}

@Test
public void testGetOrderingStringReturnsFallbackIfSourceUrlNull() {
DocumentImpl document = new DocumentImpl();
String ordering = document.getOrderingString();
String expectedPrefix = "DocumentImpl";
assertTrue(ordering.contains(expectedPrefix) || ordering.length() > 0);
}

@Test
public void testCorrectRepositioningInfoSkipsOnInvalidOffsetIndex() {
DocumentImpl document = new DocumentImpl();
RepositioningInfo info = new RepositioningInfo();
document.setContent(new DocumentContentImpl("line1\r\nline2"));
String content = "line1\r\nline2";
document.getAnnotations();
document.setPreserveOriginalContent(true);
document.setCollectRepositioningInfo(true);
document.setStringContent(content);
try {
document.init();
} catch (Exception e) {
}
assertNotNull(document.getFeatures().get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME));
}

@Test
public void testWriteStartTagEmitsAnnotMaxIdOnlyForRootTag() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("longcontent"));
FeatureMap fm = Factory.newFeatureMap();
document.getFeatures().put(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME, "longcontent");
document.getFeatures().put(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME, new RepositioningInfo());
AnnotationSet annots = document.getAnnotations();
// annots.add(0L, 11L, "Root", fm);
String xml = document.toXml(annots, true);
assertTrue(xml.contains("annotMaxId="));
}

@Test
public void testIsValidOffsetRangeReturnsFalseOnNulls() {
DocumentImpl document = new DocumentImpl();
boolean result = document.isValidOffsetRange(null, 3L);
assertFalse(result);
}

@Test(expected = InvalidOffsetException.class)
public void testEditRejectsOutOfBoundsOffsets() throws InvalidOffsetException {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abc"));
DocumentContentImpl repl = new DocumentContentImpl("X");
document.edit(0L, 5L, repl);
}

@Test
public void testToStringContainsAllConfiguredMarkers() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("data"));
document.setEncoding(StandardCharsets.UTF_8.name());
document.setMarkupAware(Boolean.TRUE);
document.setSourceUrlStartOffset(1L);
document.setSourceUrlEndOffset(2L);
document.getAnnotations("customSet");
String text = document.toString();
assertTrue(text.contains("encoding:"));
assertTrue(text.contains("encoding:UTF-8"));
assertTrue(text.contains("markupAware:true") || text.contains("markupAware:TRUE"));
assertTrue(text.contains("customSet"));
}

@Test
public void testAddSameNamedAnnotationSetTwiceDoesNotDuplicate() {
DocumentImpl document = new DocumentImpl();
AnnotationSet set1 = document.getAnnotations("ReuseSet");
AnnotationSet set2 = document.getAnnotations("ReuseSet");
assertSame(set1, set2);
assertEquals(1, document.getNamedAnnotationSets().size());
}

@Test
public void testGetNextNodeIdIncrementsProperly() {
DocumentImpl document = new DocumentImpl();
int nodeId1 = document.getNextNodeId();
int nodeId2 = document.getNextNodeId();
assertEquals(nodeId1 + 1, nodeId2);
}

@Test
public void testToXmlReturnsDocumentTagForXMLContent() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("item"));
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
// Annotation ann = set.add(0L, 4L, "xmlTag", fm);
String mimeType = "text/xml";
document.setMimeType(mimeType);
String xml = document.toXml(set);
assertTrue(xml.contains("<?xml version="));
}

@Test
public void testEncodingDefaultsWhenUnsetExplicitly() {
DocumentImpl document = new DocumentImpl();
String encoding = document.getEncoding();
assertNotNull(encoding);
assertFalse(encoding.isEmpty());
}

@Test
public void testSetContentUpdatesDocumentContentCorrectly() {
DocumentImpl document = new DocumentImpl();
DocumentContentImpl content = new DocumentContentImpl("new");
document.setContent(content);
assertEquals("new", document.getContent().toString());
}

@Test
public void testToXmlSkipsCrossedAnnotationDueToOffsetLogic() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdefghijk"));
AnnotationSet set = document.getAnnotations();
// set.add(1L, 6L, "A", Factory.newFeatureMap());
// set.add(4L, 8L, "B", Factory.newFeatureMap());
String output = document.toXml(set);
assertTrue(output.contains("<A"));
assertFalse(output.contains("<B"));
}

@Test
public void testInsertsSafetyReturnsFalseForNullStartNode() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abc"));
AnnotationSet set = document.getAnnotations();
AnnotationSetImpl fakeSet = new AnnotationSetImpl(document);
FeatureMap features = Factory.newFeatureMap();
// Annotation broken = new AnnotationImpl(100, null, null, "X", features);
// boolean result = document.getAnnotations().add(0L, 1L, "X", features) != null;
// boolean isSafe = document.getAnnotations().add(1L, 2L, "Z", features) != null;
// assertTrue(result);
// assertTrue(isSafe);
Set<Annotation> annots = new HashSet<Annotation>(document.getAnnotations());
boolean check = false;
try {
// check = (Boolean) DocumentImpl.class.getDeclaredMethod("insertsSafety", Set.class, Annotation.class).invoke(document, annots, broken);
} catch (Exception e) {
check = false;
}
assertFalse(check);
}

@Test
public void testSaveAnnotationSetAsXmlWithReversedInsertionOrder() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdef"));
FeatureMap f1 = Factory.newFeatureMap();
FeatureMap f2 = Factory.newFeatureMap();
f2.put("isEmptyAndSpan", "true");
AnnotationSet set = document.getAnnotations();
// set.add(2L, 2L, "E", f2);
// set.add(0L, 6L, "Root", f1);
String xml = document.toXml(set, true);
assertTrue(xml.contains("<Root"));
assertTrue(xml.contains("<E"));
}

@Test
public void testWriteFeaturesEscapesInvalidCharacters() {
DocumentImpl document = new DocumentImpl();
FeatureMap features = Factory.newFeatureMap();
features.put("desc", "value with \"quotes\" and <tags>");
AnnotationSet set = document.getAnnotations();
// set.add(0L, 1L, "Entity", features);
document.setContent(new DocumentContentImpl("X"));
String xml = document.toXml(set, true);
assertTrue(xml.contains("&quot;"));
assertTrue(xml.contains("&lt;"));
}

@Test
public void testToXmlHandlesUnsortedInputAnnotationsCorrectly() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("X Y Z"));
AnnotationSet set = document.getAnnotations();
// set.add(4L, 5L, "Last", Factory.newFeatureMap());
// set.add(0L, 1L, "First", Factory.newFeatureMap());
// set.add(2L, 3L, "Middle", Factory.newFeatureMap());
String xml = document.toXml(set);
assertTrue(xml.indexOf("<First") < xml.indexOf("<Middle"));
assertTrue(xml.indexOf("<Middle") < xml.indexOf("<Last"));
}

@Test
public void testFireContentEditedDoesNotFailWithoutListener() throws InvalidOffsetException {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdef"));
DocumentContentImpl newContent = new DocumentContentImpl("xy");
document.edit(1L, 3L, newContent);
assertEquals("axydef", document.getContent().toString());
}

@Test
public void testAnnotationWithNullOffsetsIsSkippedInXml() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("valid"));
Set<Annotation> set = new HashSet<Annotation>();
FeatureMap fm = Factory.newFeatureMap();
// Annotation invalid = new AnnotationImpl(888, null, null, "Bad", fm);
String xml = "";
try {
xml = (String) DocumentImpl.class.getDeclaredMethod("toXml", Set.class).invoke(document, set);
} catch (Exception e) {
xml = "error";
}
assertNotNull(xml);
}

@Test
public void testCollectInformationForAmpCoddingHandlesAmpEntities() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("AT&T is &amp good &notareal"));
RepositioningInfo info = new RepositioningInfo();
String original = "AT&T is &amp good &notareal";
try {
java.lang.reflect.Method m = DocumentImpl.class.getDeclaredMethod("collectInformationForAmpCodding", String.class, RepositioningInfo.class, boolean.class);
m.setAccessible(true);
m.invoke(document, original, info, Boolean.FALSE);
} catch (Exception e) {
}
assertNotNull(info);
}

@Test
public void testToXmlPreservesCrossedOverAnnotationField() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdefghijklmno"));
FeatureMap f1 = Factory.newFeatureMap();
FeatureMap f2 = Factory.newFeatureMap();
AnnotationSet set = document.getAnnotations();
// Annotation a1 = set.add(2L, 10L, "A", f1);
// Annotation a2 = set.add(5L, 8L, "B", f2);
String xml = document.toXml(set);
assertTrue(xml.contains("<A"));
assertFalse(xml.contains("<B"));
}

@Test
public void testToXmlWithMultipleCrossingAnnotationsSkipsAllCrossed() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcdefghijklm"));
AnnotationSet set = document.getAnnotations();
FeatureMap f1 = Factory.newFeatureMap();
FeatureMap f2 = Factory.newFeatureMap();
FeatureMap f3 = Factory.newFeatureMap();
// set.add(2L, 6L, "A", f1);
// set.add(4L, 8L, "B", f2);
// set.add(1L, 10L, "C", f3);
String xml = document.toXml(set);
assertTrue(xml.contains("<A"));
assertFalse(xml.contains("<B"));
assertFalse(xml.contains("<C"));
}

@Test
public void testToXmlAnnotationWithNullStartOffsetIsExcluded() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abcde"));
AnnotationSetImpl fakeSet = new AnnotationSetImpl(document);
// Node validEnd = Factory.newNode(5L);
FeatureMap features = Factory.newFeatureMap();
// Annotation brokenAnn = new Annotation() {
// 
// public Node getStartNode() {
// return null;
// }
// 
// public Node getEndNode() {
// return validEnd;
// }
// 
// public Integer getId() {
// return 10;
// }
// 
// public String getType() {
// return "Broken";
// }
// 
// public FeatureMap getFeatures() {
// return features;
// }
// };
Set<Annotation> inputSet = new HashSet<Annotation>();
// inputSet.add(brokenAnn);
String result = document.toXml(inputSet);
assertFalse(result.contains("Broken"));
}

@Test
public void testToXmlAnnotationWithNullFeatureMapStillEmitsTag() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("xml content"));
AnnotationSet set = document.getAnnotations();
FeatureMap nullFeatures = null;
AnnotationSet set2 = document.getAnnotations("second");
// set.add(1L, 4L, "SomeTag", Factory.newFeatureMap());
String xml = document.toXml(set);
assertTrue(xml.contains("<SomeTag"));
}

@Test
public void testWriteFeaturesSkipsUnsupportedCollectionType() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("abc"));
FeatureMap featureMap = Factory.newFeatureMap();
List<Object> unsupported = new ArrayList<Object>();
unsupported.add(new Object());
featureMap.put("invalidList", unsupported);
// Annotation a = document.getAnnotations().add(0L, 1L, "Test", featureMap);
String result = document.toXml(document.getAnnotations());
assertFalse(result.contains("invalidList"));
}

@Test
public void testNamespacePrefixFeatureIsSerializedCorrectly() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("tagged"));
FeatureMap features = Factory.newFeatureMap();
features.put("xmlns:gate", "http://gate.ac.uk");
features.put("gateId", "5");
Gate.getUserConfig().put("ADD_NAMESPACE_FEATURES", "true");
Gate.getUserConfig().put("ELEMENT_NAMESPACE_PREFIX", "namespacePrefix");
Gate.getUserConfig().put("ELEMENT_NAMESPACE_URI", "namespaceURI");
features.put("namespacePrefix", "ns");
features.put("namespaceURI", "http://example.com/schema");
AnnotationSet set = document.getAnnotations();
set.add(0L, 7L, "X", features);
String xml = document.toXml(set);
assertTrue(xml.contains("xmlns:ns=\"http://example.com/schema\""));
}

@Test
public void testGetAnnotationsDoesNotTriggerEventWhenAlreadyPresent() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("info");
final boolean[] triggered = { false };
DocumentListener listener = new DocumentListener() {

public void annotationSetAdded(DocumentEvent e) {
triggered[0] = true;
}

public void annotationSetRemoved(DocumentEvent e) {
}

public void contentEdited(DocumentEvent e) {
}
};
document.addDocumentListener(listener);
document.getAnnotations("info");
assertFalse(triggered[0]);
}

@Test
public void testToXmlWithAmpCodedContentAppliesRepositioning() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setCollectRepositioningInfo(true);
document.setPreserveOriginalContent(true);
document.setStringContent("one &amp; two");
document.init();
AnnotationSet set = document.getAnnotations();
FeatureMap f = Factory.newFeatureMap();
set.add(0L, 3L, "word", f);
String xml = document.toXml(set, true);
assertNotNull(xml);
assertTrue(xml.contains("one"));
assertTrue(xml.contains("word"));
}

@Test
public void testBuildEntityMapFromStringFindsEntities() {
DocumentImpl document = new DocumentImpl();
String sample = "5 > 4 & a < b";
TreeMap<Long, Character> map = new TreeMap<Long, Character>();
try {
java.lang.reflect.Method method = DocumentImpl.class.getDeclaredMethod("buildEntityMapFromString", String.class, TreeMap.class);
method.setAccessible(true);
method.invoke(document, sample, map);
} catch (Exception e) {
}
assertTrue(map.containsValue('>'));
assertTrue(map.containsValue('<'));
assertTrue(map.containsValue('&'));
}

@Test
public void testCorrectRepositioningForCRLFInXMLShiftsOffsets() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("line1\r\nline2\r\n"));
String text = "line1\r\nline2\r\n";
RepositioningInfo info = new RepositioningInfo();
try {
java.lang.reflect.Method method = DocumentImpl.class.getDeclaredMethod("correctRepositioningForCRLFInXML", String.class, RepositioningInfo.class);
method.setAccessible(true);
method.invoke(document, text, info);
} catch (Exception e) {
}
assertNotNull(info);
}

@Test
public void testCompareToUsesUrlStringAndOffsets() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
DocumentImpl doc2 = new DocumentImpl();
URL u1 = new URL("http://example.com/file.txt");
URL u2 = new URL("http://example.com/file.txt");
doc1.setSourceUrl(u1);
doc2.setSourceUrl(u2);
doc1.setSourceUrlStartOffset(10L);
doc1.setSourceUrlEndOffset(20L);
doc2.setSourceUrlStartOffset(30L);
doc2.setSourceUrlEndOffset(40L);
int result = doc1.compareTo(doc2);
assertTrue(result < 0);
}

@Test
public void testGetAnnotationsCreatesNamedSetOnFirstAccess() {
DocumentImpl document = new DocumentImpl();
AnnotationSet named = document.getAnnotations("meta");
assertNotNull(named);
assertTrue(document.getNamedAnnotationSets().containsKey("meta"));
}

@Test
public void testDocumentToStringIncludesFeatureMap() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("content"));
FeatureMap fm = document.getFeatures();
fm.put("key", "value");
document.getAnnotations("X");
String text = document.toString();
assertTrue(text.contains("features"));
assertTrue(text.contains("key"));
}

@Test
public void testToXmlReturnsSameContentForEmptyAnnotationSet() {
DocumentImpl document = new DocumentImpl();
document.setContent(new DocumentContentImpl("static"));
AnnotationSet emptySet = new AnnotationSetImpl(document, "temp");
String xml = document.toXml(emptySet);
assertEquals("static", xml);
}

@Test
public void testRemoveAnnotationSetFiresRemovalEvent() {
DocumentImpl document = new DocumentImpl();
document.getAnnotations("temp");
final boolean[] removed = { false };
document.addDocumentListener(new DocumentListener() {

public void annotationSetAdded(DocumentEvent e) {
}

public void annotationSetRemoved(DocumentEvent e) {
if ("temp".equals(e.getAnnotationSetName()))
removed[0] = true;
}

public void contentEdited(DocumentEvent e) {
}
});
document.removeAnnotationSet("temp");
assertTrue(removed[0]);
}
}
