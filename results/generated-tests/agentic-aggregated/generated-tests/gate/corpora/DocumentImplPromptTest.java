package gate.corpora;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DocumentContent;
import gate.FeatureMap;
import gate.Resource;
import gate.annotation.AnnotationSetImpl;
import gate.creole.ResourceInstantiationException;
import gate.event.DocumentEvent;
import gate.event.DocumentListener;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class DocumentImplPromptTest {

  @Test
  public void constructorProvidesEmptyContentAndIndependentLazyFeatures() {
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();

    assertEquals("", first.getContent().toString());
    assertEquals(Long.valueOf(0), first.getContent().size());
    assertEquals("", first.getStringContent());
    assertFalse(first.getPreserveOriginalContent());
    assertFalse(first.getCollectRepositioningInfo());
    assertFalse(first.getMarkupAware());
    assertNotNull(first.getFeatures());
    assertSame(first.getFeatures(), first.getFeatures());
    assertNotSame(first.getFeatures(), second.getFeatures());
    first.getFeatures().put("language", "en");
    assertNull(second.getFeatures().get("language"));
  }

  @Test
  public void simplePropertiesRoundTripIncludingNullValues() throws Exception {
    DocumentImpl document = new DocumentImpl();
    URL url = new URL("file:/tmp/source.txt");

    document.setMimeType("text/plain");
    document.setSourceUrl(url);
    document.setSourceUrlStartOffset(4L);
    document.setSourceUrlEndOffset(19L);
    document.setPreserveOriginalContent(Boolean.TRUE);
    document.setCollectRepositioningInfo(Boolean.TRUE);
    document.setMarkupAware(Boolean.TRUE);
    document.setEncoding("UTF-16");

    assertEquals("text/plain", document.getMimeType());
    assertEquals(url, document.getSourceUrl());
    assertEquals(Long.valueOf(4), document.getSourceUrlStartOffset());
    assertEquals(Long.valueOf(19), document.getSourceUrlEndOffset());
    assertArrayEquals(new Long[] {4L, 19L}, document.getSourceUrlOffsets());
    assertTrue(document.getPreserveOriginalContent());
    assertTrue(document.getCollectRepositioningInfo());
    assertTrue(document.getMarkupAware());
    assertEquals("UTF-16", document.getEncoding());

    document.setMimeType(null);
    document.setSourceUrl(null);
    document.setSourceUrlStartOffset(null);
    document.setSourceUrlEndOffset(null);
    assertNull(document.getMimeType());
    assertNull(document.getSourceUrl());
    assertArrayEquals(new Long[] {null, null}, document.getSourceUrlOffsets());
  }

  @Test
  public void initFromStringBuildsContentAndRecordsItsOrigin() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setStringContent("A short document");
    document.setMarkupAware(Boolean.FALSE);

    Resource result = document.init();

    assertSame(document, result);
    assertEquals("A short document", document.getContent().toString());
    assertEquals("created from String", document.getFeatures().get("gate.SourceURL"));
    assertEquals("A short document", document.getStringContent());
  }

  @Test
  public void initPreservesOriginalStringContentWhenRequested() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setStringContent("original <text>");
    document.setPreserveOriginalContent(Boolean.TRUE);
    document.setMarkupAware(Boolean.FALSE);

    document.init();

    assertEquals("original <text>", document.getContent().toString());
    assertEquals("original <text>",
        document.getFeatures().get("Original_document_content_on_load"));
  }

  @Test
  public void initRejectsMissingUrlAndMissingStringContent() {
    DocumentImpl document = new DocumentImpl();
    document.setStringContent(null);

    try {
      document.init();
      fail("Expected ResourceInstantiationException");
    } catch(ResourceInstantiationException expected) {
      assertTrue(expected.getMessage().contains("sourceURL"));
    }
  }

  @Test
  public void initReadsOnlyRequestedRangeFromFileUrl() throws Exception {
    Path file = Files.createTempFile("document-impl-", ".txt");
    Files.write(file, "0123456789".getBytes(StandardCharsets.UTF_8));
    DocumentImpl document = new DocumentImpl();
    document.setSourceUrl(file.toUri().toURL());
    document.setSourceUrlStartOffset(2L);
    document.setSourceUrlEndOffset(7L);
    document.setEncoding("UTF-8");
    document.setMarkupAware(Boolean.FALSE);

    try {
      document.init();
      assertEquals("23456", document.getContent().toString());
      assertEquals(file.toUri().toURL().toExternalForm(),
          document.getFeatures().get("gate.SourceURL"));
      assertEquals(file.toUri().toURL().toExternalForm(),
          document.getFeatures().get("gate.OriginalURL"));
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void contentSetterDoesNotOverwriteStringInitializationParameter() {
    DocumentImpl document = new DocumentImpl();
    document.setStringContent("parameter value");
    DocumentContent replacement = new DocumentContentImpl("actual content");

    document.setContent(replacement);

    assertSame(replacement, document.getContent());
    assertEquals("actual content", document.getContent().toString());
    assertEquals("parameter value", document.getStringContent());
    document.setContent(null);
    assertNull(document.getContent());
    assertEquals("parameter value", document.getStringContent());
  }

  @Test
  public void blankEncodingFallsBackToPlatformCanonicalEncoding() {
    DocumentImpl document = new DocumentImpl();
    document.setEncoding("   ");

    String encoding = document.getEncoding();

    assertEquals(Charset.forName(System.getProperty("file.encoding")).name(), encoding);
    assertEquals(encoding, document.getEncoding());
  }

  @Test
  public void annotationSetsAreCreatedLazilyAndNamesRemainLive() {
    DocumentImpl document = new DocumentImpl();

    AnnotationSet defaultSet = document.getAnnotations();
    AnnotationSet named = document.getAnnotations("Tokens");
    Set<String> names = document.getAnnotationSetNames();
    AnnotationSet later = document.getAnnotations("Sentences");

    assertSame(defaultSet, document.getAnnotations(null));
    assertSame(defaultSet, document.getAnnotations(""));
    assertSame(named, document.getAnnotations("Tokens"));
    assertEquals("Tokens", named.getName());
    assertEquals("Sentences", later.getName());
    assertTrue(names.contains("Tokens"));
    assertTrue(names.contains("Sentences"));
    assertEquals(2, names.size());
    assertSame(named, document.getNamedAnnotationSets().get("Tokens"));
  }

  @Test
  public void namedAnnotationMapIsLiveAndInitiallyEmpty() {
    DocumentImpl document = new DocumentImpl();

    Map<String, AnnotationSet> map = document.getNamedAnnotationSets();

    assertNotNull(map);
    assertTrue(map.isEmpty());
    AnnotationSet set = document.getAnnotations("Lookup");
    assertSame(set, map.get("Lookup"));
    assertSame(map, document.getNamedAnnotationSets());
  }

  @Test
  public void listenerReceivesSetLifecycleAndEditEventsExactlyOnce() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdef"));
    AtomicInteger added = new AtomicInteger();
    AtomicInteger removed = new AtomicInteger();
    AtomicInteger edited = new AtomicInteger();
    AtomicReference<DocumentEvent> lastEvent = new AtomicReference<DocumentEvent>();
    DocumentListener listener = new DocumentListener() {
      @Override
      public void annotationSetAdded(DocumentEvent event) {
        added.incrementAndGet();
        lastEvent.set(event);
      }

      @Override
      public void annotationSetRemoved(DocumentEvent event) {
        removed.incrementAndGet();
        lastEvent.set(event);
      }

      @Override
      public void contentEdited(DocumentEvent event) {
        edited.incrementAndGet();
        lastEvent.set(event);
      }
    };

    document.addDocumentListener(listener);
    document.addDocumentListener(listener);
    document.getAnnotations("Temporary");
    assertEquals(1, added.get());
    assertEquals("Temporary", lastEvent.get().getAnnotationSetName());
    document.removeAnnotationSet("Temporary");
    assertEquals(1, removed.get());
    assertEquals("Temporary", lastEvent.get().getAnnotationSetName());
    document.edit(1L, 3L, new DocumentContentImpl("X"));
    assertEquals(1, edited.get());
    assertEquals(Long.valueOf(1), lastEvent.get().getEditStart());
    assertEquals(Long.valueOf(3), lastEvent.get().getEditEnd());
    document.removeDocumentListener(listener);
    document.getAnnotations("AfterRemoval");
    assertEquals(1, added.get());
  }

  @Test
  public void removingAbsentAnnotationSetDoesNotNotifyListener() {
    DocumentImpl document = new DocumentImpl();
    AtomicInteger removed = new AtomicInteger();
    DocumentListener listener = new DocumentListener() {
      @Override
      public void annotationSetAdded(DocumentEvent event) {
      }

      @Override
      public void annotationSetRemoved(DocumentEvent event) {
        removed.incrementAndGet();
      }

      @Override
      public void contentEdited(DocumentEvent event) {
      }
    };
    document.addDocumentListener(listener);

    document.removeAnnotationSet("missing");
    document.getAnnotations("existing");
    document.removeAnnotationSet("different");

    assertEquals(0, removed.get());
    assertTrue(document.getAnnotationSetNames().contains("existing"));
  }

  @Test
  public void editUpdatesContentAndOffsetsAcrossDefaultAndNamedSets() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("0123456789"));
    AnnotationSet defaults = document.getAnnotations();
    AnnotationSet named = document.getAnnotations("Named");
    Integer beforeId = defaults.add(0L, 2L, "Before", new SimpleFeatureMapImpl());
    Integer afterId = defaults.add(7L, 10L, "After", new SimpleFeatureMapImpl());
    Integer invalidatedId = defaults.add(3L, 5L, "Inside", new SimpleFeatureMapImpl());
    Integer namedId = named.add(6L, 8L, "NamedAfter", new SimpleFeatureMapImpl());

    document.edit(2L, 6L, new DocumentContentImpl("AB"));

    assertEquals("01AB6789", document.getContent().toString());
    assertEquals(Long.valueOf(0), defaults.get(beforeId).getStartNode().getOffset());
    assertEquals(Long.valueOf(2), defaults.get(beforeId).getEndNode().getOffset());
    assertEquals(Long.valueOf(5), defaults.get(afterId).getStartNode().getOffset());
    assertEquals(Long.valueOf(8), defaults.get(afterId).getEndNode().getOffset());
    assertNull(defaults.get(invalidatedId));
    assertEquals(Long.valueOf(2), named.get(namedId).getStartNode().getOffset());
    assertEquals(Long.valueOf(6), named.get(namedId).getEndNode().getOffset());
  }

  @Test
  public void editSupportsInsertionAndDeletionAtBoundaries() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcd"));

    document.edit(0L, 0L, new DocumentContentImpl("XY"));
    assertEquals("XYabcd", document.getContent().toString());
    document.edit(2L, 4L, null);
    assertEquals("XYcd", document.getContent().toString());
    document.edit(4L, 4L, new DocumentContentImpl("!"));
    assertEquals("XYcd!", document.getContent().toString());
  }

  @Test
  public void invalidEditRangesFailWithoutChangingContent() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abc"));

    try {
      document.edit(-1L, 1L, null);
      fail("negative offset should fail");
    } catch(InvalidOffsetException expected) {
      assertTrue(expected.getMessage().contains("-1/1"));
    }
    try {
      document.edit(2L, 1L, null);
      fail("reversed range should fail");
    } catch(InvalidOffsetException expected) {
      assertTrue(expected.getMessage().contains("2/1"));
    }
    try {
      document.edit(0L, 4L, null);
      fail("past-end range should fail");
    } catch(InvalidOffsetException expected) {
      assertTrue(expected.getMessage().contains("0/4"));
    }
    assertEquals("abc", document.getContent().toString());
  }

  @Test
  public void offsetValidationAcceptsEndpointsAndEmptyRanges() {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abc"));

    assertFalse(document.isValidOffset(null));
    assertFalse(document.isValidOffset(-1L));
    assertTrue(document.isValidOffset(0L));
    assertTrue(document.isValidOffset(3L));
    assertFalse(document.isValidOffset(4L));
    assertTrue(document.isValidOffsetRange(0L, 3L));
    assertTrue(document.isValidOffsetRange(2L, 2L));
    assertFalse(document.isValidOffsetRange(3L, 2L));
    assertFalse(document.isValidOffsetRange(null, 2L));
    assertFalse(document.isValidOffsetRange(0L, null));
  }

  @Test
  public void annotationAndNodeIdentifiersAdvanceIndependently() {
    DocumentImpl document = new DocumentImpl();
    document.setNextAnnotationId(41);

    assertEquals(Integer.valueOf(41), document.peakAtNextAnnotationId());
    assertEquals(Integer.valueOf(41), document.getNextAnnotationId());
    assertEquals(Integer.valueOf(42), document.peakAtNextAnnotationId());
    assertEquals(Integer.valueOf(42), document.getNextAnnotationId());
    assertEquals(Integer.valueOf(0), document.getNextNodeId());
    assertEquals(Integer.valueOf(1), document.getNextNodeId());
  }

  @Test
  public void compareToUsesUrlThenBothOffsets() throws Exception {
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    first.setSourceUrl(new URL("file:/same.txt"));
    second.setSourceUrl(new URL("file:/same.txt"));
    first.setSourceUrlStartOffset(1L);
    first.setSourceUrlEndOffset(5L);
    second.setSourceUrlStartOffset(2L);
    second.setSourceUrlEndOffset(5L);

    assertTrue(first.compareTo(second) < 0);
    assertTrue(second.compareTo(first) > 0);
    second.setSourceUrlStartOffset(1L);
    assertEquals(0, first.compareTo(second));
  }

  @Test(expected = ClassCastException.class)
  public void compareToRejectsObjectsThatAreNotDocuments() {
    new DocumentImpl().compareTo("not a document");
  }

  @Test
  public void annotationXmlEscapesTextAndSerializesSupportedFeatures() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("A&B <C>"));
    AnnotationSet annotations = document.getAnnotations();
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("kind", "x&y");
    features.put("count", 3);
    features.put("items", Arrays.asList("one", 2));
    Integer id = annotations.add(0L, 3L, "Token", features);
    Set<Annotation> selected = new HashSet<Annotation>();
    selected.add(annotations.get(id));

    String xml = document.toXml(selected, true);

    assertTrue(xml.contains("<Token "));
    assertTrue(xml.contains("gateId=\"" + id + "\""));
    assertTrue(xml.contains("kind=\"x&amp;y\""));
    assertTrue(xml.contains("count=\"3\""));
    assertTrue(xml.contains("items=\"one;2\""));
    assertTrue(xml.contains("A&amp;B"));
    assertTrue(xml.endsWith(" &lt;C&gt;"));
  }

  @Test
  public void annotationXmlCanExcludeFeaturesAndKeepsNestedTagsBalanced() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdef"));
    AnnotationSet annotations = document.getAnnotations();
    FeatureMap outerFeatures = new SimpleFeatureMapImpl();
    outerFeatures.put("secret", "hidden");
    Integer outer = annotations.add(0L, 6L, "Outer", outerFeatures);
    Integer inner = annotations.add(1L, 4L, "Inner", new SimpleFeatureMapImpl());
    Set<Annotation> selected = new HashSet<Annotation>();
    selected.add(annotations.get(outer));
    selected.add(annotations.get(inner));

    String xml = document.toXml(selected, false);

    assertEquals("<Outer>a<Inner>bcd</Inner>ef</Outer>", xml);
    assertFalse(xml.contains("secret"));
    assertFalse(xml.contains("gateId"));
  }

  @Test
  public void annotationXmlDropsCrossingAnnotationButKeepsSafeOnes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefgh"));
    AnnotationSet original = document.getAnnotations("Original markups");
    original.add(0L, 4L, "Original", new SimpleFeatureMapImpl());
    AnnotationSet candidates = document.getAnnotations();
    Integer crossing = candidates.add(2L, 6L, "Crossing", new SimpleFeatureMapImpl());
    Integer safe = candidates.add(4L, 8L, "Safe", new SimpleFeatureMapImpl());
    Set<Annotation> selected = new HashSet<Annotation>();
    selected.add(candidates.get(crossing));
    selected.add(candidates.get(safe));

    String xml = document.toXml(selected, false);

    assertTrue(xml.contains("<Original>abcd</Original>"));
    assertTrue(xml.contains("<Safe>efgh</Safe>"));
    assertFalse(xml.contains("Crossing"));
  }

  @Test
  public void emptyAnnotationSerializesAsEmptyElement() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abc"));
    AnnotationSet annotations = document.getAnnotations();
    Integer marker = annotations.add(1L, 1L, "Marker", new SimpleFeatureMapImpl());
    Set<Annotation> selected = new HashSet<Annotation>();
    selected.add(annotations.get(marker));

    String xml = document.toXml(selected, false);

    assertEquals("a<Marker gateId=\"" + marker + "\"/>bc", xml);
  }

  @Test
  public void fullGateXmlContainsContentFeaturesAndAnnotationSets() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("hello"));
    document.getFeatures().put("language", "en");
    document.getAnnotations().add(0L, 5L, "Word", new SimpleFeatureMapImpl());
    document.getAnnotations("Extra").add(1L, 4L, "Part", new SimpleFeatureMapImpl());

    String xml = document.toXml();

    assertTrue(xml.contains("<GateDocument"));
    assertTrue(xml.contains("<TextWithNodes>"));
    assertTrue(xml.contains("/>h<Node"));
    assertTrue(xml.contains("/>ell<Node"));
    assertTrue(xml.contains("/>o<Node"));
    assertTrue(xml.contains("language"));
    assertTrue(xml.contains("Word"));
    assertTrue(xml.contains("Extra"));
    assertTrue(xml.contains("Part"));
  }

  @Test
  public void cleanupClearsAnnotationSetsAndAllowsFreshOnes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abc"));
    AnnotationSet oldDefault = document.getAnnotations();
    oldDefault.add(0L, 1L, "Old", new SimpleFeatureMapImpl());
    document.getAnnotations("Named").add(1L, 2L, "OldNamed", new SimpleFeatureMapImpl());

    document.cleanup();

    assertTrue(document.getAnnotationSetNames().isEmpty());
    AnnotationSet freshDefault = document.getAnnotations();
    assertNotSame(oldDefault, freshDefault);
    assertTrue(freshDefault.isEmpty());
    assertEquals("abc", document.getContent().toString());
  }

  @Test
  public void explicitlySuppliedDefaultAnnotationSetIsReturned() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcd"));
    AnnotationSetImpl supplied = new AnnotationSetImpl(document, "supplied");
    supplied.add(0L, 4L, "Span", new SimpleFeatureMapImpl());

    document.setDefaultAnnotations(supplied);

    assertSame(supplied, document.getAnnotations());
    assertEquals(1, document.getAnnotations().size());
    assertNotNull(document.getAnnotations().get("Span"));
  }

  @Test
  public void toStringReportsCurrentPubliclyConfiguredState() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("sample"));
    document.setEncoding("UTF-8");
    document.setMarkupAware(Boolean.TRUE);
    document.setSourceUrl(new URL("file:/sample.txt"));
    document.setSourceUrlStartOffset(3L);
    document.setSourceUrlEndOffset(8L);
    document.getAnnotations("Named");

    String value = document.toString();

    assertTrue(value.startsWith("DocumentImpl:"));
    assertTrue(value.contains("content:sample"));
    assertTrue(value.contains("encoding:UTF-8"));
    assertTrue(value.contains("markupAware:true"));
    assertTrue(value.contains("Named"));
    assertTrue(value.contains("sourceUrl:file:/sample.txt"));
    assertTrue(value.contains("sourceUrlStartOffset:3"));
    assertTrue(value.contains("sourceUrlEndOffset:8"));
  }
}
