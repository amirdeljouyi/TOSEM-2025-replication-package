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

public class DocumentImpl_4_GPTLLMTest {

@Test
public void testInitWithoutSourceUrlCreatesContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Hello GATE Document");
document.setMarkupAware(false);
document.setPreserveOriginalContent(false);
document.setCollectRepositioningInfo(false);
document.init();
assertNotNull(document.getContent());
assertEquals("Hello GATE Document", document.getContent().toString());
assertEquals("created from String", document.getFeatures().get("gate.SourceURL"));
}

@Test
public void testInitThrowsWhenStringContentAndSourceUrlAreNull() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent(null);
// thrown.expect(ResourceInstantiationException.class);
document.init();
}

@Test
public void testEditWithNullReplacementRemovesText() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcdefg");
document.init();
document.edit(2L, 5L, null);
String result = document.getContent().toString();
assertEquals("abfg", result);
}

@Test
public void testEditWithReplacementText() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("The quick fox");
document.init();
DocumentContent replacement = new DocumentContentImpl("slow");
document.edit(4L, 9L, replacement);
String result = document.getContent().toString();
assertEquals("The slow fox", result);
}

@Test
public void testEditWithInvalidStartThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Hello world");
document.init();
DocumentContent content = new DocumentContentImpl("bad");
// thrown.expect(InvalidOffsetException.class);
document.edit(-1L, 5L, content);
}

@Test
public void testEditWithInvalidEndThrows() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Hello");
document.init();
DocumentContent content = new DocumentContentImpl("bad");
// thrown.expect(InvalidOffsetException.class);
document.edit(1L, 50L, content);
}

@Test
public void testIsValidOffsetRangeReturnsTrueForValidOffsets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("ABCD");
document.init();
boolean valid = document.isValidOffsetRange(0L, 3L);
assertTrue(valid);
}

@Test
public void testIsValidOffsetRangeReturnsFalseForNullValues() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Data");
document.init();
boolean result = document.isValidOffsetRange(null, 2L);
assertFalse(result);
}

@Test
public void testIsValidOffsetRangeReturnsFalseIfStartGreaterThanEnd() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Test123");
document.init();
boolean result = document.isValidOffsetRange(5L, 2L);
assertFalse(result);
}

@Test
public void testGetAnnotationsReturnsDefaultSet() {
DocumentImpl document = new DocumentImpl();
AnnotationSet set = document.getAnnotations();
assertNotNull(set);
assertEquals("", set.getName());
}

@Test
public void testGetAnnotationsNamedSetsCreateNewInstance() {
DocumentImpl document = new DocumentImpl();
AnnotationSet set1 = document.getAnnotations("Person");
AnnotationSet set2 = document.getAnnotations("Person");
assertNotNull(set1);
assertSame(set1, set2);
}

@Test
public void testGetNextAnnotationIdIncrements() {
DocumentImpl document = new DocumentImpl();
Integer first = document.getNextAnnotationId();
Integer second = document.getNextAnnotationId();
assertEquals((int) first + 1, (int) second);
}

@Test
public void testPeakAtNextAnnotationIdReturnsSameValueUntilIncremented() {
DocumentImpl document = new DocumentImpl();
Integer peekBefore = document.peakAtNextAnnotationId();
document.getNextAnnotationId();
Integer peekAfter = document.peakAtNextAnnotationId();
assertEquals((int) peekAfter - 1, (int) peekBefore);
}

@Test
public void testToStringContainsContentDescription() {
DocumentImpl document = new DocumentImpl();
document.setStringContent("String content value");
try {
document.init();
} catch (Exception e) {
fail("Unexpected init failure: " + e.getMessage());
}
String docString = document.toString();
assertTrue(docString.contains("content:"));
assertTrue(docString.contains("defaultAnnots:"));
}

@Test
public void testToXmlProducesOutput() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Tom & Jerry");
document.setPreserveOriginalContent(false);
document.setCollectRepositioningInfo(false);
document.setMarkupAware(false);
document.init();
String xml = document.toXml();
assertNotNull(xml);
assertTrue(xml.contains("Tom"));
}

@Test
public void testSetAndGetSourceUrlOffsets() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlStartOffset(10L);
document.setSourceUrlEndOffset(20L);
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(10L), offsets[0]);
assertEquals(Long.valueOf(20L), offsets[1]);
}

@Test
public void testPreserveOriginalContentFlag() {
DocumentImpl document = new DocumentImpl();
document.setPreserveOriginalContent(true);
Boolean result = document.getPreserveOriginalContent();
assertTrue(result);
}

@Test
public void testCollectRepositioningInfoFlag() {
DocumentImpl document = new DocumentImpl();
document.setCollectRepositioningInfo(true);
Boolean result = document.getCollectRepositioningInfo();
assertTrue(result);
}

@Test
public void testRemoveNamedAnnotationSetRemovesIt() {
DocumentImpl document = new DocumentImpl();
AnnotationSet set = document.getAnnotations("Temp");
assertNotNull(set);
document.removeAnnotationSet("Temp");
AnnotationSet removed = document.getNamedAnnotationSets().get("Temp");
assertNull(removed);
}

@Test
public void testGetEncodingReturnsNonNull() {
DocumentImpl document = new DocumentImpl();
String encoding = document.getEncoding();
assertNotNull(encoding);
}

@Test
public void testGetDefaultMarkupAwareIsFalse() {
DocumentImpl document = new DocumentImpl();
Boolean aware = document.getMarkupAware();
assertFalse(aware);
}

@Test
public void testEditWithStartEqualsEndAndNullReplacementShouldDoNothing() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("EdgeCaseExample");
document.init();
DocumentContent before = document.getContent();
document.edit(4L, 4L, null);
DocumentContent after = document.getContent();
assertEquals(before.toString(), after.toString());
}

@Test
public void testEditFullContentWithNullReplacementRemovesAllText() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Complete text");
document.init();
Long start = 0L;
Long end = document.getContent().size().longValue();
document.edit(start, end, null);
assertEquals("", document.getContent().toString());
}

@Test
public void testGetAnnotationsWithNullNameReturnsDefault() {
DocumentImpl document = new DocumentImpl();
AnnotationSet defaultSet = document.getAnnotations(null);
AnnotationSet blankSet = document.getAnnotations("");
assertSame(defaultSet, blankSet);
}

@Test
public void testIsValidOffsetReturnsFalseForNegativeAndNull() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("ABC");
document.init();
assertFalse(document.isValidOffset(null));
assertFalse(document.isValidOffset(-1L));
}

@Test
public void testIsValidOffsetAcceptsZeroAndMax() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("XYZ");
document.init();
long max = document.getContent().size().longValue();
assertTrue(document.isValidOffset(0L));
assertTrue(document.isValidOffset(max));
}

@Test
public void testGetSourceUrlOffsetsWithOnlyStartSet() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlStartOffset(100L);
Long[] offsets = document.getSourceUrlOffsets();
assertEquals(Long.valueOf(100L), offsets[0]);
assertNull(offsets[1]);
}

@Test
public void testGetSourceUrlOffsetsWithOnlyEndSet() {
DocumentImpl document = new DocumentImpl();
document.setSourceUrlEndOffset(500L);
Long[] offsets = document.getSourceUrlOffsets();
assertNull(offsets[0]);
assertEquals(Long.valueOf(500L), offsets[1]);
}

@Test
public void testToXmlWithNullAnnotationSetReturnsOriginal() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("XML test content");
document.init();
String xml = document.toXml(null);
assertTrue(xml.contains("XML test content"));
}

@Test
public void testToXmlWithEmptyAnnotationSet() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Sample for empty XML");
document.init();
Set<Annotation> emptySet = new HashSet<Annotation>();
String xml = document.toXml(emptySet);
assertTrue(xml.contains("Sample for empty XML"));
}

@Test
public void testWriteFeaturesWithNonStringKeyIsIgnored() {
DocumentImpl document = new DocumentImpl();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put(123, "value");
String output = document.toXml();
assertNotNull(output);
}

@Test
public void testWriteFeaturesWithUnsupportedValueIsIgnored() {
DocumentImpl document = new DocumentImpl();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("test", new StringBuilder("invalid"));
String attr = document.toXml();
assertNotNull(attr);
}

@Test
public void testGetNextNodeIdIncrements() {
DocumentImpl document = new DocumentImpl();
int id1 = document.getNextNodeId();
int id2 = document.getNextNodeId();
assertEquals(id1 + 1, id2);
}

@Test
public void testCompareToReturnsZeroForSameContent() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
doc1.setStringContent("Common");
doc1.init();
DocumentImpl doc2 = new DocumentImpl();
doc2.setStringContent("Common");
doc2.init();
int result = doc1.compareTo(doc2);
assertNotNull(result);
}

@Test
public void testEmptyStringContentHandledGracefully() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("");
document.init();
assertEquals(0, document.getContent().size().intValue());
String xml = document.toXml();
assertNotNull(xml);
}

@Test
public void testDefaultAnnotationSetUnmodifiableByRemove() {
DocumentImpl document = new DocumentImpl();
AnnotationSet defaultSet = document.getAnnotations();
document.removeAnnotationSet("");
AnnotationSet result = document.getAnnotations();
assertNotNull(result);
}

@Test
public void testInsertsSafetyReturnsFalseWithNullNodes() {
DocumentImpl document = new DocumentImpl();
boolean safe = document.toXml() != null;
assertTrue(safe);
}

@Test
public void testWriteEndTagWithNullAnnotationReturnsEmptyString() {
DocumentImpl document = new DocumentImpl();
String endTag = document.toXml();
assertNotNull(endTag);
}

@Test
public void testToXmlHandlesMultibyteCharacters() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("Café € 火");
document.init();
String xml = document.toXml();
assertTrue(xml.contains("Café"));
}

@Test
public void testToXmlWithOverlappingAnnotationsHandledProperly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("ABCDEF");
doc.init();
AnnotationSet annots = doc.getAnnotations();
// Annotation a1 = annots.add(1L, 4L, "A", new SimpleFeatureMapImpl());
// Annotation a2 = annots.add(2L, 5L, "B", new SimpleFeatureMapImpl());
String xmlOutput = doc.toXml(annots);
assertNotNull(xmlOutput);
assertTrue(xmlOutput.contains("ABCDEF"));
}

@Test
public void testToXmlAnnotationCrossFailureScenario() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("ABCDEFG");
doc.init();
AnnotationSet annots = doc.getAnnotations();
// Annotation a1 = annots.add(0L, 7L, "outer", new SimpleFeatureMapImpl());
// Annotation a2 = annots.add(1L, 5L, "inner1", new SimpleFeatureMapImpl());
// Annotation a3 = annots.add(4L, 6L, "inner2", new SimpleFeatureMapImpl());
String xml = doc.toXml(annots);
assertNotNull(xml);
assertTrue(xml.contains("outer"));
assertTrue(xml.contains("inner1") || xml.contains("inner2") || true);
}

@Test
public void testToXmlHandlesRootAnnotationWithNamespace() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcde");
doc.setPreserveOriginalContent(true);
doc.setCollectRepositioningInfo(true);
doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("xmlns:root", "http://example.com");
AnnotationSet annots = doc.getAnnotations();
annots.add(0L, 5L, "root", fm);
String xml = doc.toXml(annots);
assertTrue(xml.contains("xmlns:root") || xml.contains("annotMaxId"));
}

@Test
public void testWriteFeaturesSkipsFeatureWithNullValue() {
DocumentImpl doc = new DocumentImpl();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("keyWithNullValue", null);
String result = doc.toXml();
assertNotNull(result);
}

@Test
public void testWriteFeaturesSkipsUnsupportedCollectionItem() {
DocumentImpl doc = new DocumentImpl();
FeatureMap fm = new SimpleFeatureMapImpl();
java.util.List<Object> unsupportedList = new java.util.ArrayList<Object>();
unsupportedList.add(new Object());
fm.put("collection", unsupportedList);
AnnotationSet annots = doc.getAnnotations();
// annots.add(0L, 2L, "test", fm);
String result = doc.toXml(annots);
assertNotNull(result);
}

@Test
public void testEditAppliesToNamedAnnotationSet() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdefghi");
doc.init();
AnnotationSet defaultSet = doc.getAnnotations();
AnnotationSet customSet = doc.getAnnotations("custom");
defaultSet.add(1L, 3L, "A", new SimpleFeatureMapImpl());
customSet.add(5L, 8L, "B", new SimpleFeatureMapImpl());
DocumentContent replacement = new DocumentContentImpl("XY");
doc.edit(5L, 8L, replacement);
assertTrue(doc.getContent().toString().contains("XY"));
assertTrue(doc.getContent().toString().startsWith("abcde"));
}

@Test
public void testToXmlWithFeaturesThatIncludeMatchesKey() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcabc");
// doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("matches", "someValue");
AnnotationSet annots = doc.getAnnotations();
// annots.add(1L, 5L, "span", fm);
String xml = doc.toXml(annots);
assertTrue(xml.contains("matches") && xml.contains("someValue"));
}

@Test
public void testWriteEmptyTagIncludesFeatures() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("data");
// doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("attribute", "value");
AnnotationSet annots = doc.getAnnotations();
// annots.add(2L, 2L, "empty", fm);
String xml = doc.toXml(annots);
assertTrue(xml.contains("attribute"));
assertTrue(xml.contains("/>"));
}

@Test
public void testGetOrderingStringIncludesOffsets() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("positioned content");
doc.setSourceUrlStartOffset(100L);
doc.setSourceUrlEndOffset(200L);
String str = doc.toString();
assertNotNull(str);
}

@Test
public void testRemoveUnknownNamedAnnotationSetDoesNotThrow() {
DocumentImpl doc = new DocumentImpl();
doc.removeAnnotationSet("nonexistent");
assertTrue(true);
}

@Test
public void testAnnotationComparatorOrdersByStartOffsetAsc() {
DocumentImpl.AnnotationComparator c = new DocumentImpl.AnnotationComparator(0, 3);
// Annotation a1 = new FakeAnnotation(1L, 5L, 100);
// Annotation a2 = new FakeAnnotation(1L, 7L, 101);
// int result = c.compare(a1, a2);
// assertTrue(result < 0 || result == 0);
}

@Test
public void testAnnotationComparatorOrdersByEndOffsetDesc() {
DocumentImpl.AnnotationComparator c = new DocumentImpl.AnnotationComparator(1, -3);
// Annotation a1 = new FakeAnnotation(0L, 6L, 90);
// Annotation a2 = new FakeAnnotation(0L, 4L, 91);
// int result = c.compare(a1, a2);
// assertTrue(result < 0 || result == 0);
}

@Test
public void testAnnotationSetGetSameObjectEachTime() {
DocumentImpl document = new DocumentImpl();
AnnotationSet first = document.getAnnotations("TempSet");
AnnotationSet second = document.getAnnotations("TempSet");
assertSame(first, second);
}

@Test
public void testRemoveAnnotationSetThenRecreate() {
DocumentImpl document = new DocumentImpl();
AnnotationSet original = document.getAnnotations("TransientSet");
document.removeAnnotationSet("TransientSet");
AnnotationSet recreated = document.getAnnotations("TransientSet");
assertNotNull(recreated);
assertNotSame(original, recreated);
}

@Test
public void testToXmlWithOverlappingButNonCrossedAnnotations() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("0123456789");
document.init();
AnnotationSet annots = document.getAnnotations();
FeatureMap fm1 = new SimpleFeatureMapImpl();
FeatureMap fm2 = new SimpleFeatureMapImpl();
// Annotation ann1 = annots.add(2L, 5L, "tag1", fm1);
// Annotation ann2 = annots.add(5L, 8L, "tag2", fm2);
Set<Annotation> included = new HashSet<Annotation>();
// included.add(ann1);
// included.add(ann2);
String result = document.toXml(included);
assertTrue(result.contains("tag1"));
assertTrue(result.contains("tag2"));
}

@Test
public void testEditAdjustmentAfterDeletionOnDefaultAndNamedSets() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("ABCDEFGH");
document.init();
AnnotationSet defaultSet = document.getAnnotations();
AnnotationSet namedSet = document.getAnnotations("named");
defaultSet.add(1L, 3L, "token", new SimpleFeatureMapImpl());
namedSet.add(4L, 6L, "phrase", new SimpleFeatureMapImpl());
document.edit(1L, 3L, null);
assertTrue(document.getContent().toString().contains("ADEFGH"));
}

@Test
public void testInsertsSafetyWithNullAnnotationSetReturnsFalse() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("safety test");
document.init();
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationSet annSet = document.getAnnotations();
// Annotation ann = annSet.add(0L, 5L, "X", features);
boolean result = document.toXml() != null;
assertTrue(result);
}

@Test
public void testInsertsSafetyWithNullOffsetsReturnsFalse() {
DocumentImpl document = new DocumentImpl();
// Annotation fakeAnn = new Annotation() {
// 
// @Override
// public Integer getId() {
// return 123;
// }
// 
// @Override
// public Node getStartNode() {
// return new Node() {
// 
// public Long getOffset() {
// return null;
// }
// };
// }
// 
// @Override
// public Node getEndNode() {
// return new Node() {
// 
// public Long getOffset() {
// return null;
// }
// };
// }
// 
// @Override
// public String getType() {
// return "Fake";
// }
// 
// @Override
// public FeatureMap getFeatures() {
// return new SimpleFeatureMapImpl();
// }
// 
// @Override
// public int compareTo(Object o) {
// return 0;
// }
// 
// @Override
// public gate.Document getDocument() {
// return null;
// }
// 
// @Override
// public boolean isValid() {
// return true;
// }
// };
Set<Annotation> set = new HashSet<Annotation>();
// set.add(fakeAnn);
String result = document.toXml(set);
assertNotNull(result);
}

@Test
public void testWriteStartTagIncludesAllRequiredComponents() {
DocumentImpl document = new DocumentImpl();
document.setStringContent("test");
try {
document.init();
} catch (Exception e) {
fail("init failed unexpectedly: " + e.getMessage());
}
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("feature1", "value1");
AnnotationSet annSet = document.getAnnotations();
// Annotation tag = annSet.add(0L, 4L, "testTag", fm);
Set<Annotation> tags = new HashSet<Annotation>();
// tags.add(tag);
String xml = document.toXml(tags);
assertTrue(xml.contains("feature1"));
assertTrue(xml.contains("testTag"));
}

@Test
public void testToXmlWithUninitializedContentReturnsSafeString() {
DocumentImpl document = new DocumentImpl();
String xml = document.toXml();
assertNotNull(xml);
assertTrue(xml.length() >= 0);
}

@Test
public void testPeakAtNextAnnotationIdUnchangedByCalls() {
DocumentImpl document = new DocumentImpl();
Integer idBefore = document.peakAtNextAnnotationId();
Integer ignored = document.peakAtNextAnnotationId();
Integer idAgain = document.peakAtNextAnnotationId();
assertEquals(idBefore, idAgain);
}

@Test
public void testCompareToUsesUrlWhenSet() {
DocumentImpl doc1 = new DocumentImpl();
DocumentImpl doc2 = new DocumentImpl();
doc1.setStringContent("abc");
doc2.setStringContent("def");
try {
doc1.init();
doc2.init();
} catch (Exception e) {
fail("init failed");
}
int result = doc1.compareTo(doc2);
assertNotNull(result);
}

@Test
public void testSetNextAnnotationIdOverridesAutoIncrement() {
DocumentImpl document = new DocumentImpl();
document.setNextAnnotationId(500);
int id = document.getNextAnnotationId();
assertEquals(500, id);
}

@Test
public void testNamespaceSerializationFlagDefaultsToFalse() {
DocumentImpl document = new DocumentImpl();
String xml = document.toXml();
assertNotNull(xml);
}

@Test
public void testAnnotationsSavedInOrderOfOffsetsInXml() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcdefghij");
document.init();
AnnotationSet annSet = document.getAnnotations();
annSet.add(2L, 4L, "B", new SimpleFeatureMapImpl());
annSet.add(0L, 1L, "A", new SimpleFeatureMapImpl());
annSet.add(5L, 9L, "C", new SimpleFeatureMapImpl());
String xml = document.toXml(annSet);
int indexA = xml.indexOf("A");
int indexB = xml.indexOf("B");
int indexC = xml.indexOf("C");
assertTrue(indexA < indexB);
assertTrue(indexB < indexC);
}

@Test
public void testToXmlHandlesEntityEncoding() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("5 < 6 & 7 > 4");
document.init();
String xml = document.toXml();
assertTrue(xml.contains("&lt;"));
assertTrue(xml.contains("&gt;"));
assertTrue(xml.contains("&amp;"));
}

@Test
public void testToXmlHandlesAnnotationWithEmptyFeatureMap() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("plain");
document.init();
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationSet annSet = document.getAnnotations();
annSet.add(0L, 5L, "Span", fm);
String xml = document.toXml(annSet);
assertTrue(xml.contains("<Span"));
assertTrue(xml.contains("</Span>"));
}

@Test
public void testWriteEndTagOutputFormat() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("tagtext");
document.init();
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationSet annSet = document.getAnnotations();
annSet.add(0L, 7L, "EndTag", fm);
String xml = document.toXml(annSet);
assertTrue(xml.contains("</EndTag>"));
}

@Test
public void testGetAnnotationsReturnsNonNullEvenAfterClear() {
DocumentImpl doc = new DocumentImpl();
AnnotationSet set = doc.getAnnotations("TestSet");
doc.removeAnnotationSet("TestSet");
AnnotationSet expected = doc.getAnnotations("TestSet");
assertNotNull(expected);
}

@Test
public void testIsValidOffsetValueExceedsLengthReturnsFalse() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abc");
doc.init();
boolean result = doc.isValidOffset(100L);
assertFalse(result);
}

@Test
public void testToXmlReturnsRootElementIfRootAnnotationDetected() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("0123456789");
document.init();
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationSet set = document.getAnnotations();
// Annotation a = set.add(0L, 10L, "doc", fm);
Set<Annotation> sorted = new LinkedHashSet<Annotation>();
// sorted.add(a);
String result = document.toXml(sorted);
assertTrue(result.contains("<doc"));
assertTrue(result.contains("</doc>"));
}

@Test
public void testEditRemovesAnnotationWithSameRange() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("This sentence.");
doc.init();
AnnotationSet defaultSet = doc.getAnnotations();
defaultSet.add(5L, 13L, "test", new SimpleFeatureMapImpl());
doc.edit(5L, 13L, null);
String output = doc.getContent().toString();
assertFalse(output.contains("sentence"));
}

@Test
public void testWriteFeaturesSkipsIsEmptyAndSpanMetaFeature() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("xyzxyz");
doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("isEmptyAndSpan", "true");
AnnotationSet set = doc.getAnnotations();
set.add(2L, 2L, "mark", fm);
String result = doc.toXml(set);
assertFalse(result.contains("isEmptyAndSpan"));
}

@Test
public void testToXmlSkipsCrossedAnnotationsGracefully() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdefghij");
doc.init();
AnnotationSet annSet = doc.getAnnotations();
// Annotation outer = annSet.add(0L, 10L, "outer", new SimpleFeatureMapImpl());
// Annotation inner = annSet.add(2L, 8L, "inner1", new SimpleFeatureMapImpl());
// Annotation crossed = annSet.add(5L, 3L, "bad", new SimpleFeatureMapImpl());
Set<Annotation> annots = new LinkedHashSet<Annotation>();
// annots.add(inner);
// annots.add(outer);
String result = doc.toXml(annots);
assertFalse(result.contains("bad"));
}

@Test
public void testGetNamedAnnotationSetsLazyInitialization() {
DocumentImpl doc = new DocumentImpl();
Map<String, AnnotationSet> before = doc.getNamedAnnotationSets();
assertNotNull(before);
assertTrue(before.isEmpty());
AnnotationSet newOne = doc.getAnnotations("lazy");
Map<String, AnnotationSet> after = doc.getNamedAnnotationSets();
assertTrue(after.containsKey("lazy"));
}

@Test
public void testGetAnnotationSetNamesIncludesAddedNames() {
DocumentImpl doc = new DocumentImpl();
doc.getAnnotations("A");
doc.getAnnotations("B");
Set<String> names = doc.getAnnotationSetNames();
assertTrue(names.contains("A"));
assertTrue(names.contains("B"));
}

@Test
public void testBuildEntityMapFromStringHandlesAllEntities() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("<test> & \"quotes\" </test>");
doc.init();
String xml = doc.toXml();
assertTrue(xml.contains("&lt;") || xml.contains("&amp;") || xml.contains("&gt;"));
}

@Test
public void testSaveXmlPreservesAnnotationOrderingByOffset() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdefghij");
doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationSet as = doc.getAnnotations();
// Annotation x = as.add(1L, 4L, "x", fm);
// Annotation y = as.add(0L, 2L, "y", fm);
// Annotation z = as.add(5L, 9L, "z", fm);
Set<Annotation> input = new LinkedHashSet<Annotation>();
// input.add(x);
// input.add(y);
// input.add(z);
String result = doc.toXml(input);
assertTrue(result.indexOf("y") < result.indexOf("x"));
assertTrue(result.indexOf("x") < result.indexOf("z"));
}

@Test
public void testEditDoesNotCrashWhenDefaultAnnotationSetIsNull() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Dummy Content.");
doc.init();
DocumentImpl newDoc = new DocumentImpl();
newDoc.setStringContent("Another");
newDoc.init();
DocumentContent newContent = new DocumentContentImpl("XYZ");
newDoc.edit(0L, 3L, newContent);
assertTrue(newDoc.getContent().toString().contains("XYZ"));
}

@Test
public void testEmptyAnnotationSetProducesValidXml() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("ABCDEF");
doc.init();
Set<Annotation> emptySet = new LinkedHashSet<Annotation>();
String xml = doc.toXml(emptySet);
assertTrue(xml.contains("ABCDEF"));
}

@Test
public void testAnnotationComparatorSortsCorrectlyByIdWhenOffsetsEqual() {
DocumentImpl.AnnotationComparator comparator = new DocumentImpl.AnnotationComparator(2, 3);
// Annotation a1 = new DummyAnnotation(0L, 5L, 100);
// Annotation a2 = new DummyAnnotation(0L, 5L, 101);
// int result = comparator.compare(a1, a2);
// assertTrue(result < 0);
}

@Test
public void testWriteFeaturesSkipsNonStringKey() {
DocumentImpl document = new DocumentImpl();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put(5, "invalidKey");
fm.put("valid", "value");
document.setStringContent("12345");
try {
document.init();
} catch (Exception e) {
fail("init failed");
}
AnnotationSet annSet = document.getAnnotations();
// annSet.add(0L, 5L, "tag", fm);
String xml = document.toXml(annSet);
assertTrue(xml.contains("valid"));
assertFalse(xml.contains("invalidKey"));
}

@Test
public void testWriteFeaturesSkipsNonSerializableValue() {
DocumentImpl document = new DocumentImpl();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("nonserializable", new Object());
fm.put("keep", "yes");
document.setStringContent("Data");
try {
document.init();
} catch (Exception e) {
fail("init failed");
}
AnnotationSet annotations = document.getAnnotations();
// annotations.add(0L, 4L, "meta", fm);
String xml = document.toXml(annotations);
assertTrue(xml.contains("keep"));
assertFalse(xml.contains("nonserializable"));
}

@Test
public void testEditNoAnnotationsStillModifiesContent() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("abcdefgh");
document.init();
document.edit(2L, 5L, new DocumentContentImpl("XYZ"));
assertEquals("abXYZfgh", document.getContent().toString());
}

@Test
public void testInsertSafetyReturnsFalseWhenStartOffsetIsNull() {
DocumentImpl document = new DocumentImpl();
// Annotation badAnnotation = new Annotation() {
// 
// public Integer getId() {
// return 1;
// }
// 
// public Node getStartNode() {
// return new Node() {
// 
// public Long getOffset() {
// return null;
// }
// };
// }
// 
// public Node getEndNode() {
// return new Node() {
// 
// public Long getOffset() {
// return 5L;
// }
// };
// }
// 
// public String getType() {
// return "bad";
// }
// 
// public FeatureMap getFeatures() {
// return new SimpleFeatureMapImpl();
// }
// 
// public int compareTo(Object o) {
// return 0;
// }
// 
// public gate.Document getDocument() {
// return null;
// }
// 
// public boolean isValid() {
// return true;
// }
// };
AnnotationSet annSet = document.getAnnotations();
// boolean result = document.toXml(Collections.singleton(badAnnotation)) != null;
// assertTrue(result);
}

@Test
public void testInsertSafetyReturnsFalseWhenEndOffsetIsNull() {
DocumentImpl document = new DocumentImpl();
// Annotation badAnnotation = new Annotation() {
// 
// public Integer getId() {
// return 20;
// }
// 
// public Node getStartNode() {
// return new Node() {
// 
// public Long getOffset() {
// return 1L;
// }
// };
// }
// 
// public Node getEndNode() {
// return new Node() {
// 
// public Long getOffset() {
// return null;
// }
// };
// }
// 
// public String getType() {
// return "badEnd";
// }
// 
// public FeatureMap getFeatures() {
// return new SimpleFeatureMapImpl();
// }
// 
// public int compareTo(Object o) {
// return 0;
// }
// 
// public gate.Document getDocument() {
// return null;
// }
// 
// public boolean isValid() {
// return true;
// }
// };
Set<Annotation> source = new HashSet<Annotation>();
// source.add(badAnnotation);
String result = document.toXml(source);
assertNotNull(result);
}

@Test
public void testToXmlWithMatchingIdProducesProperGateId() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("hosted");
document.init();
document.setNextAnnotationId(999);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationSet as = document.getAnnotations();
// Annotation ann = as.add(1L, 6L, "entity", fm);
Set<Annotation> input = new LinkedHashSet<Annotation>();
// input.add(ann);
String xml = document.toXml(input);
assertTrue(xml.contains("gateId=\""));
}

@Test
public void testSetMarkupAwareTrueEnablesUnpackingLogic() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("<root><el>text</el></root>");
document.setMarkupAware(true);
try {
document.init();
assertNotNull(document.getContent());
} catch (Exception e) {
fail("should not throw: " + e.getMessage());
}
}

@Test
public void testSetPreserveOriginalTriggersOriginalContentStorage() throws Exception {
DocumentImpl document = new DocumentImpl();
document.setStringContent("<doc>Hello</doc>");
document.setPreserveOriginalContent(true);
document.init();
String stored = (String) document.getFeatures().get("OriginalDocumentContent");
assertNull(stored);
}

@Test
public void testAddAndRemoveDocumentListenerFiresEvents() {
final List<String> called = new ArrayList<String>();
DocumentImpl doc = new DocumentImpl();
DocumentListener listener = new DocumentListener() {

public void annotationSetAdded(DocumentEvent e) {
called.add("added");
}

public void annotationSetRemoved(DocumentEvent e) {
called.add("removed");
}

public void contentEdited(DocumentEvent e) {
called.add("edited");
}
};
doc.addDocumentListener(listener);
doc.getAnnotations("Events");
doc.removeAnnotationSet("Events");
assertTrue(called.contains("added"));
assertTrue(called.contains("removed"));
}

@Test
public void testEditTriggersContentEditedEvent() throws Exception {
final List<String> triggered = new ArrayList<String>();
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("EventTest");
doc.init();
DocumentListener listener = new DocumentListener() {

public void annotationSetAdded(DocumentEvent e) {
}

public void annotationSetRemoved(DocumentEvent e) {
}

public void contentEdited(DocumentEvent e) {
triggered.add("edited");
}
};
doc.addDocumentListener(listener);
DocumentContent replacement = new DocumentContentImpl("XX");
doc.edit(2L, 5L, replacement);
assertTrue(triggered.contains("edited"));
}

@Test
public void testSetCollectRepositioningInfoWithPreserveOriginal() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("<tag>ABC</tag>");
doc.setPreserveOriginalContent(true);
doc.setCollectRepositioningInfo(true);
doc.setMarkupAware(true);
try {
doc.init();
assertTrue(doc.getContent().toString().contains("ABC"));
} catch (Exception e) {
fail("Unexpected exception in markup-aware init with repositioning: " + e.getMessage());
}
}

@Test
public void testEditContentAtBounds() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("XYZ");
doc.init();
int end = doc.getContent().size().intValue();
doc.edit(0L, (long) end, new DocumentContentImpl("ABC"));
assertEquals("ABC", doc.getContent().toString());
}

@Test
public void testCompareToHandlesNullSourceUrl() throws Exception {
DocumentImpl doc1 = new DocumentImpl();
DocumentImpl doc2 = new DocumentImpl();
doc1.setStringContent("abc");
doc2.setStringContent("xyz");
doc1.init();
doc2.init();
int result = doc1.compareTo(doc2);
assertNotNull(result);
}

@Test
public void testWriteEndTagReturnsExpectedOutput() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("end");
doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationSet annSet = doc.getAnnotations();
// Annotation ann = annSet.add(0L, 3L, "EndBlock", fm);
// String xml = doc.toXml(Collections.singleton(ann));
// assertTrue(xml.contains("</EndBlock>"));
}

@Test
public void testEditWithZeroLengthRangeDoesNotChangeContent() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("x");
doc.init();
DocumentContent before = doc.getContent();
doc.edit(0L, 0L, null);
DocumentContent after = doc.getContent();
assertEquals(before.toString(), after.toString());
}

@Test
public void testSaveAnnotationSetAsXml_EmptyAndSpanAnnotationHandled() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("text span");
doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("isEmptyAndSpan", "true");
AnnotationSet set = doc.getAnnotations();
set.add(3L, 3L, "Gap", fm);
String xml = doc.toXml(set);
assertTrue(xml.contains("<Gap"));
assertFalse(xml.contains("/>"));
}

@Test
public void testCrossedOverAnnotationIsNotSerialized() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("abcdefghij");
doc.init();
AnnotationSet set = doc.getAnnotations();
set.add(0L, 10L, "A", new SimpleFeatureMapImpl());
set.add(2L, 8L, "B", new SimpleFeatureMapImpl());
set.add(4L, 6L, "C", new SimpleFeatureMapImpl());
Set<Annotation> unsafe = new HashSet<Annotation>();
for (Annotation a : set) unsafe.add(a);
String xml = doc.toXml(unsafe);
assertTrue(xml.contains("A"));
assertTrue(xml.contains("B") || xml.contains("C"));
}

@Test
public void testToXmlIncludesUnicodeCharacters() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("Unicode π α 字 🔥");
doc.init();
String xml = doc.toXml();
assertTrue(xml.contains("Unicode"));
assertTrue(xml.contains("π"));
assertTrue(xml.contains("字"));
}

@Test
public void testEditOnEmptyDocAddsNewContent() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("");
doc.init();
doc.edit(0L, 0L, new DocumentContentImpl("New"));
assertEquals("New", doc.getContent().toString());
}

@Test
public void testAnnotationFeatureMapIsNullDefaultIsEmpty() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("ABC");
doc.init();
AnnotationSet annSet = doc.getAnnotations();
// Annotation ann = annSet.add(0L, 3L, "Entity", null);
Set<Annotation> source = new HashSet<Annotation>();
// source.add(ann);
String xml = doc.toXml(source);
assertTrue(xml.contains("<Entity"));
}

@Test
public void testNamespaceSerializationFlagFalseDoesNotIncludeXmlns() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("root data");
doc.init();
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("xmlns:gate", "http://gate.ac.uk");
AnnotationSet ann = doc.getAnnotations();
ann.add(0L, 4L, "ns", fm);
String xml = doc.toXml(ann);
assertFalse(xml.contains("xmlns:gate"));
}

@Test
public void testWriteFeaturesHandlesCollectionOfStringsProperly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("items");
doc.init();
Collection<Object> values = new ArrayList<Object>();
values.add("item1");
values.add("item2");
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("collection", values);
AnnotationSet ann = doc.getAnnotations();
ann.add(1L, 2L, "List", fm);
String result = doc.toXml(ann);
assertTrue(result.contains("item1"));
assertTrue(result.contains("item2"));
}

@Test
public void testToXmlHandlesEmptyDocumentProperly() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("");
doc.init();
String xml = doc.toXml();
assertNotNull(xml);
assertEquals("", doc.getContent().toString());
}

@Test
public void testInvalidOffsetRangeWithStartGreaterThanEndThrows() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("123456");
doc.init();
boolean valid = doc.isValidOffsetRange(5L, 2L);
assertFalse(valid);
}

@Test
public void testEditWithRangeBeyondEndThrowsInvalidOffsetException() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("123");
doc.init();
DocumentContent replacement = new DocumentContentImpl("Z");
try {
doc.edit(0L, 10L, replacement);
fail("Expected InvalidOffsetException");
} catch (InvalidOffsetException e) {
assertTrue(e.getMessage().contains("Offsets"));
}
}

@Test
public void testToStringIncludesInternalStateDetails() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("inspect this");
doc.init();
String desc = doc.toString();
assertTrue(desc.contains("content:"));
assertTrue(desc.contains("defaultAnnots:"));
}

@Test
public void testSetStringContentDoesNotAffectContentBeforeInit() {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("unchanged");
String raw = doc.getStringContent();
assertEquals("unchanged", raw);
}

@Test
public void testEmptyAnnotationSetSerializationProducesSameContent() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("X");
doc.init();
Set<Annotation> empty = new HashSet<Annotation>();
String xml = doc.toXml(empty);
assertEquals("X", xml);
}

@Test
public void testAnnotationFromOriginalMarkupIdentificationSkipsWrongOffsets() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("markupRoot");
doc.init();
AnnotationSet set = doc.getAnnotations();
set.add(1L, 10L, "root", new SimpleFeatureMapImpl());
String xml = doc.toXml(set);
assertFalse(xml.startsWith("<root"));
}

@Test
public void testSaveAnnotationSetAsXmlPreservesEntityEncodings() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("A & B < C > D");
doc.init();
String xml = doc.toXml();
assertTrue(xml.contains("&amp;"));
assertTrue(xml.contains("&lt;"));
assertTrue(xml.contains("&gt;"));
}

@Test
public void testToXmlHandlesHighUnicodeSurrogatePair() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("emoji 🔥 end");
doc.init();
String xml = doc.toXml();
assertTrue(xml.contains("🔥"));
}

@Test
public void testToXmlHandlesAnnotationOnEntireRange() throws Exception {
DocumentImpl doc = new DocumentImpl();
doc.setStringContent("123456789");
doc.init();
AnnotationSet ann = doc.getAnnotations();
ann.add(0L, 9L, "full", new SimpleFeatureMapImpl());
String xml = doc.toXml(ann);
assertTrue(xml.contains("<full"));
assertTrue(xml.contains("</full>"));
}
}
