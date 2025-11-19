package gate.annotation;

import gate.*;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.event.AnnotationSetEvent;
import gate.event.AnnotationSetListener;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnnotationSetImpl_2_GPTLLMTest {

 @Test
  public void testAddAnnotationAndRetrieveById() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    when(document.getNextAnnotationId()).thenReturn(100);
    DocumentContent content = mock(DocumentContent.class);
    when(document.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);
    when(document.isValidOffsetRange(10L, 20L)).thenReturn(true);
    
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap features = Factory.newFeatureMap();
    features.put("key", "value");

    Integer id = annotationSet.add(10L, 20L, "Person", features);

    Annotation annotation = annotationSet.get(id);
    assertNotNull(annotation);
    assertEquals("Person", annotation.getType());
    assertEquals("value", annotation.getFeatures().get("key"));
  }
@Test
  public void testAddExistingAnnotationReplaces() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node start1 = new NodeImpl(1, 0L);
    Node end1 = new NodeImpl(2, 10L);
    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("label", "A");
    Annotation existing = new AnnotationImpl(document, 1, start1, end1, "Old", fm1);
    annotationSet.add(existing);

    Node start2 = new NodeImpl(1, 0L);
    Node end2 = new NodeImpl(2, 10L);
    FeatureMap fm2 = Factory.newFeatureMap();
    fm2.put("label", "B");
    Annotation updated = new AnnotationImpl(document, 1, start2, end2, "New", fm2);
    annotationSet.add(updated);

    Annotation result = annotationSet.get(1);
    assertEquals("New", result.getType());
    assertEquals("B", result.getFeatures().get("label"));
  }
@Test
  public void testRemoveAnnotationDecreasesSize() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node start = new NodeImpl(1, 0L);
    Node end = new NodeImpl(2, 10L);
    FeatureMap fm = Factory.newFeatureMap();
    Annotation ann = new AnnotationImpl(document, 5, start, end, "TypeX", fm);

    annotationSet.add(ann);
    assertEquals(1, annotationSet.size());

    annotationSet.remove(ann);
    assertEquals(0, annotationSet.size());
  }
@Test
  public void testGetTypeReturnsCorrectAnnotations() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node s1 = new NodeImpl(1, 0L);
    Node e1 = new NodeImpl(2, 5L);
    FeatureMap f1 = Factory.newFeatureMap();
    Annotation a1 = new AnnotationImpl(document, 1, s1, e1, "Person", f1);

    Node s2 = new NodeImpl(3, 6L);
    Node e2 = new NodeImpl(4, 10L);
    FeatureMap f2 = Factory.newFeatureMap();
    Annotation a2 = new AnnotationImpl(document, 2, s2, e2, "Location", f2);

    Node s3 = new NodeImpl(5, 11L);
    Node e3 = new NodeImpl(6, 15L);
    FeatureMap f3 = Factory.newFeatureMap();
    Annotation a3 = new AnnotationImpl(document, 3, s3, e3, "Person", f3);

    annotationSet.add(a1);
    annotationSet.add(a2);
    annotationSet.add(a3);

    AnnotationSet persons = annotationSet.get("Person");
    assertEquals(2, persons.size());
    assertTrue(persons.contains(a1));
    assertTrue(persons.contains(a3));
    assertFalse(persons.contains(a2));
  }
@Test
  public void testClearEmptiesAnnotationSet() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node begin = new NodeImpl(10, 0L);
    Node end = new NodeImpl(11, 5L);
    FeatureMap fm = Factory.newFeatureMap();
    Annotation a = new AnnotationImpl(document, 1, begin, end, "Test", fm);

    annotationSet.add(a);
    assertEquals(1, annotationSet.size());

    annotationSet.clear();
    assertEquals(0, annotationSet.size());
  }
@Test
  public void testAddAllCopiesAnnotationsWithNewIds() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(document.getNextAnnotationId()).thenReturn(100, 101);
    when(document.getContent()).thenReturn(content);
    when(content.size()).thenReturn(500L);
    when(document.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node s1 = new NodeImpl(1, 10);
    Node e1 = new NodeImpl(2, 20);
    Node s2 = new NodeImpl(3, 30);
    Node e2 = new NodeImpl(4, 40);
    FeatureMap f1 = Factory.newFeatureMap();
    FeatureMap f2 = Factory.newFeatureMap();

    Annotation a1 = new AnnotationImpl(document, 10, s1, e1, "Type1", f1);
    Annotation a2 = new AnnotationImpl(document, 20, s2, e2, "Type2", f2);

    java.util.List<Annotation> source = new java.util.ArrayList<Annotation>();
    source.add(a1);
    source.add(a2);

    annotationSet.addAll(source);
    assertEquals(2, annotationSet.size());
  }
@Test
  public void testGetAllTypesReturnsCorrectTypeSet() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Annotation a = new AnnotationImpl(document, 1, new NodeImpl(1, 5L), new NodeImpl(2, 10L), "Alpha", Factory.newFeatureMap());
    Annotation b = new AnnotationImpl(document, 2, new NodeImpl(3, 15L), new NodeImpl(4, 20L), "Beta", Factory.newFeatureMap());
    Annotation c = new AnnotationImpl(document, 3, new NodeImpl(5, 25L), new NodeImpl(6, 30L), "Alpha", Factory.newFeatureMap());

    annotationSet.add(a);
    annotationSet.add(b);
    annotationSet.add(c);

    java.util.Set<String> typeSet = annotationSet.getAllTypes();
    assertEquals(2, typeSet.size());
    assertTrue(typeSet.contains("Alpha"));
    assertTrue(typeSet.contains("Beta"));
  }
@Test
  public void testGetAnnotationWithFeatureMapMatch() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap required = Factory.newFeatureMap();
    required.put("name", "ORG");

    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("name", "ORG");

    FeatureMap fm2 = Factory.newFeatureMap();
    fm2.put("name", "PER");

    Annotation a = new AnnotationImpl(document, 1, new NodeImpl(1, 0L), new NodeImpl(2, 10L), "Entity", fm1);
    Annotation b = new AnnotationImpl(document, 2, new NodeImpl(3, 20L), new NodeImpl(4, 30L), "Entity", fm2);

    annotationSet.add(a);
    annotationSet.add(b);

    AnnotationSet result = annotationSet.get("Entity", required);
    assertEquals(1, result.size());
    assertTrue(result.contains(a));
    assertFalse(result.contains(b));
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddInvalidOffsetThrowsException() {
    DocumentImpl document = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(document.getContent()).thenReturn(content);
    when(document.isValidOffsetRange(20L, 10L)).thenReturn(false);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(20L, 10L, "InvalidType", fm);
  }
@Test
  public void testGetByEmptyTypeSetReturnsEmpty() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Set<String> emptyTypes = new HashSet<String>();
    AnnotationSet result = annotationSet.get(emptyTypes);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetWithUnknownTypeReturnsEmpty() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    AnnotationSet result = annotationSet.get("NON_EXISTENT_TYPE");
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testRemoveNonexistentAnnotationReturnsFalse() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node start = new NodeImpl(1, 0L);
    Node end = new NodeImpl(2, 5L);
    FeatureMap fm = Factory.newFeatureMap();
    Annotation fakeAnnotation = new AnnotationImpl(document, 999, start, end, "Fake", fm);

    boolean result = annotationSet.remove(fakeAnnotation);
    assertFalse(result);
  }
@Test
  public void testGetStrictReturnsEmptyWhenNoMatch() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Annotation a = new AnnotationImpl(
        document,
        1,
        new NodeImpl(1, 5L),
        new NodeImpl(2, 15L),
        "Type",
        Factory.newFeatureMap()
    );

    annotationSet.add(a);

    AnnotationSet strict = annotationSet.getStrict(6L, 15L);
    assertNotNull(strict);
    assertEquals(0, strict.size());
  }
@Test
  public void testGetContainedEmptyWhenEndBeforeStart() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    AnnotationSet result = annotationSet.getContained(30L, 10L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetCoveringReturnsEmptyWhenLongerThanAnyAnnotation() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Annotation a = new AnnotationImpl(
        document,
        1,
        new NodeImpl(5, 10L),
        new NodeImpl(6, 15L),
        "Text",
        Factory.newFeatureMap()
    );

    annotationSet.add(a);

    AnnotationSet result = annotationSet.getCovering("Text", 10L, 30L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetWithNullFeatureMapReturnsAllTypeMatches() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("key", "value");

    Annotation a = new AnnotationImpl(
        document,
        1,
        new NodeImpl(1, 1L),
        new NodeImpl(2, 5L),
        "X",
        fm1
    );

    annotationSet.add(a);

    AnnotationSet result = annotationSet.get("X", Factory.newFeatureMap());
    assertEquals(1, result.size());
  }
@Test
  public void testAddAnnotationWithSameStartAndEnd() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    when(document.getNextAnnotationId()).thenReturn(88);
    when(document.isValidOffsetRange(10L, 10L)).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(document.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fmap = Factory.newFeatureMap();
    Integer id = annotationSet.add(10L, 10L, "EmptyRange", fmap);

    assertNotNull(annotationSet.get(id));
    assertEquals(1, annotationSet.size());
  }
@Test
  public void testDoubleClearStillEmpty() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.clear();
    annotationSet.clear();

    assertEquals(0, annotationSet.size());
  }
@Test
  public void testGetByTypeWithEmptyAnnotationSetReturnsEmpty() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    AnnotationSet result = annotationSet.get("Anything");
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetReturnsSameObjectFromIdAfterReplace() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node s1 = new NodeImpl(10, 1L);
    Node e1 = new NodeImpl(11, 5L);
    FeatureMap f1 = Factory.newFeatureMap();
    f1.put("f", "1");

    Annotation original = new AnnotationImpl(document, 100, s1, e1, "TypeA", f1);
    annotationSet.add(original);

    Node s2 = new NodeImpl(20, 1L);
    Node e2 = new NodeImpl(21, 5L);
    FeatureMap f2 = Factory.newFeatureMap();
    f2.put("f", "2");

    Annotation replacement = new AnnotationImpl(document, 100, s2, e2, "TypeB", f2);
    annotationSet.add(replacement);

    Annotation result = annotationSet.get(100);
    assertEquals("2", result.getFeatures().get("f"));
    assertEquals("TypeB", result.getType());
  }
@Test
  public void testMultipleAnnotationsSameOffsetIndexing() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node s = new NodeImpl(10, 15L);
    Node e1 = new NodeImpl(11, 20L);
    Node e2 = new NodeImpl(12, 25L);

    Annotation a1 = new AnnotationImpl(document, 1, s, e1, "Type1", Factory.newFeatureMap());
    Annotation a2 = new AnnotationImpl(document, 2, s, e2, "Type2", Factory.newFeatureMap());

    annotationSet.add(a1);
    annotationSet.add(a2);

    Annotation result1 = annotationSet.get(1);
    Annotation result2 = annotationSet.get(2);

    assertNotNull(result1);
    assertNotNull(result2);
    assertEquals(2, annotationSet.size());
  }
@Test
  public void testAnnotationSetGetWithFeatureNamesOnly() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("a", "x");
    fm.put("b", "y");

    Annotation a = new AnnotationImpl(
        document,
        1,
        new NodeImpl(1, 10L),
        new NodeImpl(2, 20L),
        "AttrType",
        fm
    );

    annotationSet.add(a);

    Set<Object> keys = new HashSet<Object>();
    keys.add("a");

    AnnotationSet result = annotationSet.get("AttrType", keys);
    assertEquals(1, result.size());

    Set<Object> keysAbsent = new HashSet<Object>();
    keysAbsent.add("not_exists");

    AnnotationSet resultEmpty = annotationSet.get("AttrType", keysAbsent);
    assertEquals(0, resultEmpty.size());
  }
@Test
  public void testAddAnnotationWithStartEqualsEndOffset() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    when(document.getNextAnnotationId()).thenReturn(1);
    when(document.isValidOffsetRange(5L, 5L)).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(document.getContent()).thenReturn(content);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);
    Integer id = annotationSet.add(5L, 5L, "ZeroSpan", Factory.newFeatureMap());

    Annotation a = annotationSet.get(id);
    assertNotNull(a);
    assertEquals(a.getStartNode(), a.getEndNode());
  }
@Test
  public void testAddAnnotationWithNullFeatures() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    when(document.getNextAnnotationId()).thenReturn(1);
    when(document.isValidOffsetRange(0L, 5L)).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(50L);
    when(document.getContent()).thenReturn(content);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);
    Integer id = annotationSet.add(0L, 5L, "SimpleType", null);
    Annotation a = annotationSet.get(id);

    assertNotNull(a);
    assertEquals("SimpleType", a.getType());
    assertNotNull(a.getFeatures());
  }
@Test
  public void testGetOverlappingAnnotationsReturnsEmptyWhenNoneOverlap() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    when(document.getNextAnnotationId()).thenReturn(1);
    when(document.isValidOffsetRange(0L, 10L)).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(document.getContent()).thenReturn(content);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);
    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(0L, 5L, "Alpha", features);

    AnnotationSet result = annotationSet.get(10L, 15L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testEditShiftsNodeOffsetsCorrectly() {
    DocumentImpl document = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(document.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fm = Factory.newFeatureMap();

    Node start = new NodeImpl(1, 10L);
    Node end = new NodeImpl(2, 20L);
    AnnotationImpl annotation = new AnnotationImpl(document, 10, start, end, "X", fm);
    annotationSet.add(annotation);

    Gate.getUserConfig().put(GateConstants.DOCEDIT_INSERT_PREPEND, Boolean.FALSE);
    annotationSet.edit(15L, 15L, new DocumentContentImpl("inserted"));

    assertTrue(annotationSet.get(10).getEndNode().getOffset() > 20L);
  }
@Test
  public void testCloneProducesEqualButNotSameInstance() throws Exception {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Object clone = annotationSet.clone();
    assertNotNull(clone);
    assertTrue(clone instanceof AnnotationSetImpl);
    assertNotSame(annotationSet, clone);
  }
@Test
  public void testEmptyASReturnsNonNullValidSet() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    AnnotationSet empty = annotationSet.get("NoType");
    assertNotNull(empty);
    assertEquals(0, empty.size());
    assertEquals(document, empty.getDocument());
  }
@Test
  public void testAddAnnotationOutOfDocumentOrderInDocumentOrder() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Annotation a = new AnnotationImpl(document, 1, new NodeImpl(1, 50L), new NodeImpl(2, 60L), "A", Factory.newFeatureMap());
    Annotation b = new AnnotationImpl(document, 2, new NodeImpl(3, 10L), new NodeImpl(4, 20L), "B", Factory.newFeatureMap());

    annotationSet.add(a);
    annotationSet.add(b);
    List<Annotation> ordered = annotationSet.inDocumentOrder();

    assertEquals(Long.valueOf(10L), ordered.get(0).getStartNode().getOffset());
    assertEquals(Long.valueOf(50L), ordered.get(1).getStartNode().getOffset());
  }
@Test
  public void testGetNextNodeReturnsNullIfNoMore() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Annotation a = new AnnotationImpl(document, 1, new NodeImpl(1, 0L), new NodeImpl(2, 5L), "A", Factory.newFeatureMap());
    annotationSet.add(a);

    Node lastNode = new NodeImpl(999, 5L);
    Node result = annotationSet.nextNode(lastNode);
    assertNull(result);
  }
@Test
  public void testGetCoveringNonExistentTypeReturnsEmpty() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fm = Factory.newFeatureMap();
    Annotation a = new AnnotationImpl(document, 1, new NodeImpl(1, 0L), new NodeImpl(2, 50L), "Different", fm);
    annotationSet.add(a);

    AnnotationSet result = annotationSet.getCovering("Target", 10L, 40L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testEmptyGetStartingAtReturnsEmptyAnnotationSet() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    AnnotationSet result = annotationSet.getStartingAt(200L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAddAnnotationWithEmptyFeatureMapStillAccessible() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fm = Factory.newFeatureMap();
    Annotation a = new AnnotationImpl(document, 10, new NodeImpl(1, 0L), new NodeImpl(2, 10L), "Test", fm);
    annotationSet.add(a);

    assertEquals(1, annotationSet.size());
    assertEquals("Test", annotationSet.get(10).getType());
    assertTrue(annotationSet.get(10).getFeatures().isEmpty());
  }
@Test
  public void testUnindexedAnnotationSetStillReturnsById() {
    DocumentImpl document = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Annotation a = new AnnotationImpl(document, 3, new NodeImpl(1, 0L), new NodeImpl(2, 5L), "Plain", Factory.newFeatureMap());
    annotationSet.add(a);

    Annotation result = annotationSet.get(3);
    assertNotNull(result);
    assertEquals("Plain", result.getType());
  }
@Test
  public void testFeatureMapSubsumesPartialMatchIsFalse() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap fA = Factory.newFeatureMap();
    fA.put("x", "1");
    fA.put("y", "2");

    FeatureMap constraints = Factory.newFeatureMap();
    constraints.put("x", "1");

    Annotation ann = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 10L), "TypeA", fA);
    annotationSet.add(ann);

    AnnotationSet result = annotationSet.get("TypeA", constraints);
    assertEquals(1, result.size());
  }
@Test
  public void testRemoveFromTypeIndexRemovesMapEntryIfEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 10L), "ZapType", Factory.newFeatureMap());
    set.add(a);
    Annotation beforeRemoval = set.get("ZapType").get(1);
    assertNotNull(beforeRemoval);

    set.remove(a);
    AnnotationSet afterRemoval = set.get("ZapType");
    assertNotNull(afterRemoval);
    assertTrue(afterRemoval.isEmpty());
  }
@Test
  public void testMultipleAnnotationsStartingFromSameNodeAreGroupedCorrectly() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node sharedStartNode = new NodeImpl(1, 50L);
    Node end1 = new NodeImpl(2, 60L);
    Node end2 = new NodeImpl(3, 70L);

    set.add(new AnnotationImpl(doc, 1, sharedStartNode, end1, "X", Factory.newFeatureMap()));
    set.add(new AnnotationImpl(doc, 2, sharedStartNode, end2, "X", Factory.newFeatureMap()));

    Annotation result1 = set.get(1);
    Annotation result2 = set.get(2);

    assertNotNull(result1);
    assertNotNull(result2);
    assertEquals(2, set.get("X").size());
  }
@Test
  public void testGetWithEmptyFeatureNameSetReturnsAllOfType() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("a", "1");

    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 8L), "Tag", fm);
    set.add(a);

    Set<Object> emptyFeatureRequest = new HashSet<Object>();
    AnnotationSet result = set.get("Tag", emptyFeatureRequest);

    assertEquals(1, result.size());
  }
@Test
  public void testGetFirstLastNodeAfterMultipleAdds() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(new AnnotationImpl(doc, 1, new NodeImpl(1, 5L), new NodeImpl(2, 10L), "A", Factory.newFeatureMap()));
    set.add(new AnnotationImpl(doc, 2, new NodeImpl(3, 20L), new NodeImpl(4, 30L), "B", Factory.newFeatureMap()));
    set.add(new AnnotationImpl(doc, 3, new NodeImpl(5, 15L), new NodeImpl(6, 22L), "C", Factory.newFeatureMap()));

    Node first = set.firstNode();
    Node last = set.lastNode();
    assertEquals(Long.valueOf(5L), first.getOffset());
    assertEquals(Long.valueOf(30L), last.getOffset());
  }
@Test
  public void testAddAnnotationUsingExplicitIdIncrementsDocumentCounter() throws Exception {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.getContent()).thenReturn(mock(DocumentContent.class));
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(5, 0L, 10L, "Explicit", Factory.newFeatureMap());

    verify(doc).setNextAnnotationId(6); 
  }
@Test
  public void testGetAnnotationUsingConstructorClonesIndexesIfSameImpl() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl original = new AnnotationSetImpl(doc);

    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 8L), "TypeX", Factory.newFeatureMap());
    original.add(a);

    AnnotationSetImpl clone = new AnnotationSetImpl(original);

    Annotation cloned = clone.get(1);
    assertNotNull(cloned);
    assertEquals("TypeX", cloned.getType());
  }
@Test
  public void testEmptyGetCoveringOnLongSpanReturnsEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 10L), "Short", Factory.newFeatureMap());
    set.add(a);

    AnnotationSet result = set.getCovering("Short", 0L, 100L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testRelationSetCreatedLazilyOnDemand() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    RelationSet rel = set.getRelations();
    assertNotNull(rel);

    RelationSet rel2 = set.getRelations();
    assertSame(rel, rel2); 
  }
@Test
  public void testEmptyAnnotationSetReturnedFromEmptyASMethod() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl base = new AnnotationSetImpl(doc);

    AnnotationSet empty = base.get(); 

    assertNotNull(empty);
    assertEquals(0, empty.size());
    assertEquals(doc, empty.getDocument());
  }
@Test
  public void testGetByNullIdReturnsNull() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Annotation result = set.get(null);
    assertNull(result);
  }
@Test
  public void testGetReturnsEmptySetIfAnnotationSetEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    AnnotationSet all = set.get();
    assertNotNull(all);
    assertEquals(0, all.size());
  }
@Test
  public void testGetContainedOnExactBordersIncludesMatch() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Annotation ann = new AnnotationImpl(doc, 1, new NodeImpl(1, 10L), new NodeImpl(2, 20L), "A", Factory.newFeatureMap());
    set.add(ann);

    AnnotationSet result = set.getContained(10L, 20L);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(ann));
  }
@Test
  public void testRemoveAnnotationNotInSetReturnsFalse() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node start = new NodeImpl(1, 0L);
    Node end = new NodeImpl(2, 5L);
    Annotation a = new AnnotationImpl(doc, 100, start, end, "Test", Factory.newFeatureMap());

    boolean removed = set.remove(a);
    assertFalse(removed);
  }
@Test
  public void testEditZeroLengthAnnotationRemovesIt() {
    DocumentImpl doc = new DocumentImpl();
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    doc.setContent(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Annotation ann = new AnnotationImpl(doc, 1, new NodeImpl(1, 10L), new NodeImpl(2, 20L), "Type", Factory.newFeatureMap());
    set.add(ann);

    Gate.getUserConfig().put(GateConstants.DOCEDIT_INSERT_PREPEND, Boolean.TRUE);
    set.edit(10L, 20L, new DocumentContentImpl(""));

    assertEquals(0, set.size());
  }
@Test
  public void testEditShiftsOffsetsForwardAtBeginning() {
    DocumentImpl doc = new DocumentImpl();
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    doc.setContent(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Annotation ann = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 5L), "Shift", Factory.newFeatureMap());
    set.add(ann);

    Gate.getUserConfig().put(GateConstants.DOCEDIT_INSERT_PREPEND, Boolean.FALSE);
    set.edit(0L, 0L, new DocumentContentImpl("added"));

    Annotation shifted = set.get(1);
    assertTrue(shifted.getStartNode().getOffset() > 0);
  }
@Test
  public void testGetStrictOnEmptyNodeIndexReturnsEmptySet() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    AnnotationSet result = set.getStrict(0L, 10L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testCloneWithPopulatedAnnotationSet() throws Exception {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl original = new AnnotationSetImpl(doc);

    Annotation ann = new AnnotationImpl(doc, 10, new NodeImpl(1, 0L), new NodeImpl(2, 5L), "Clone", Factory.newFeatureMap());
    original.add(ann);

    AnnotationSetImpl cloned = (AnnotationSetImpl) original.clone();
    assertNotNull(cloned);
    assertNotSame(original, cloned);
  }
@Test
  public void testTypeConstraintMatchingReturnsNullWhenNoMatchingTypeInSubMap() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap constraints = Factory.newFeatureMap();
    constraints.put("a", "1");
    AnnotationSet result = set.get("MissingType", constraints);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetByNullTypeAndFeatureNamesWorksAgainstWholeSet() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("x", 1);
    fm.put("y", 2);

    Annotation ann = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 10L), "TypeZ", fm);
    set.add(ann);

    Set<Object> keys = new HashSet<Object>();
    keys.add("x");

    AnnotationSet result = set.get(null, keys);
    assertEquals(1, result.size());
  }
@Test
  public void testRemoveAnnotationWithNullStartNodeDoesNotThrow() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node start = mock(Node.class);
    Node end = mock(Node.class);

    when(start.getId()).thenReturn(null);
    when(start.getOffset()).thenReturn(5L);
    when(end.getId()).thenReturn(999);
    when(end.getOffset()).thenReturn(10L);

    Annotation a = new AnnotationImpl(doc, 1, start, end, "Invalid", Factory.newFeatureMap());

    set.add(a); 
    set.remove(a); 
    assertEquals(0, set.size());
  }
@Test
  public void testAddAnnotationMultipleTimesOnlyOneStored() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node s = new NodeImpl(1, 0L);
    Node e = new NodeImpl(2, 10L);
    Annotation annotation = new AnnotationImpl(doc, 5, s, e, "TypeD", Factory.newFeatureMap());

    boolean result1 = set.add(annotation);
    boolean result2 = set.add(annotation); 

    assertTrue(result1);  
    assertFalse(result2); 
    assertEquals(1, set.size());
  }
@Test
  public void testRelationSetIsSharedAcrossCalls() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    RelationSet rs1 = set.getRelations();
    RelationSet rs2 = set.getRelations();

    assertNotNull(rs1);
    assertSame(rs1, rs2);
  }
@Test
  public void testIndexingDoesNotThrowIfNodesAlreadyIndexed() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    
    Node s = new NodeImpl(1, 0L);
    Node e = new NodeImpl(2, 10L);
    Annotation a = new AnnotationImpl(doc, 10, s, e, "Type1", Factory.newFeatureMap());
    set.add(a);

    
    set.get("Type1");
    set.firstNode();
    assertEquals("Type1", set.get(10).getType());
  }
@Test
  public void testAddAllKeepIDsAcceptsEmptyCollection() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    boolean changed = set.addAllKeepIDs(Collections.<Annotation>emptyList());
    assertFalse(changed);
    assertEquals(0, set.size());
  }
@Test
  public void testGetStartingAtOffsetWhereNodeExistsButAnnotationDoesNot() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node node = new NodeImpl(1, 50L);
    Node end = new NodeImpl(2, 60L);
    Annotation ann = new AnnotationImpl(doc, 1, node, end, "Type", Factory.newFeatureMap());
    set.add(ann);

    AnnotationSet result = set.getStartingAt(30L); 
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetReturnsEmptyWhenTypeExistsButConstraintsDoNotMatch() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap actual = Factory.newFeatureMap();
    actual.put("x", 1);
    actual.put("y", 2);

    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("z", 3);

    Annotation ann = new AnnotationImpl(doc, 1, new NodeImpl(1, 5L), new NodeImpl(2, 10L), "MatchType", actual);
    set.add(ann);

    AnnotationSet mismatch = set.get("MatchType", constraint);
    assertTrue(mismatch.isEmpty());
  }
@Test
  public void testAddCreatesStartAndEndNodesIfMissing() throws Exception {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.getContent()).thenReturn(mock(DocumentContent.class));
    when(doc.getContent().size()).thenReturn(100L);
    when(doc.isValidOffsetRange(0L, 10L)).thenReturn(true);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Integer id = set.add(0L, 10L, "NewAdd", Factory.newFeatureMap());
    assertNotNull(set.get(id));

    Node start = set.get(id).getStartNode();
    Node end = set.get(id).getEndNode();
    assertNotNull(start);
    assertNotNull(end);
    assertFalse(set.get(id).getStartNode() == null);
  }
@Test
  public void testGetAllTypesReturnsUnmodifiableSet() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 10L), "TagX", Factory.newFeatureMap());
    set.add(a);

    Set<String> types = set.getAllTypes();
    assertEquals(1, types.size());
    assertTrue(types.contains("TagX"));

    try {
      types.add("Bad");
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      
    }
  }
@Test
  public void testRemoveFromOffsetIndexWorksWithEmptyCollections() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node node = new NodeImpl(1, 20L);
    Node end = new NodeImpl(2, 30L);
    Annotation ann = new AnnotationImpl(doc, 1, node, end, "Clear", Factory.newFeatureMap());
    set.add(ann);
    set.remove(ann); 

    assertEquals(0, set.getStartingAt(20L).size());
  }
@Test
  public void testEmptyAnnotationSetInGetTypeWithNoAnnotations() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    AnnotationSet result = set.get("AnyType");
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testNextNodeWithOffsetBeyondRangeReturnsNull() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node s = new NodeImpl(1, 5L);
    Node e = new NodeImpl(2, 15L);
    set.add(new AnnotationImpl(doc, 1, s, e, "B", Factory.newFeatureMap()));

    Node late = new NodeImpl(999, 25L);
    Node result = set.nextNode(late);
    assertNull(result);
  }
@Test
  public void testRemoveAnnotationMissingFromIndicesReturnsFalse() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node s = new NodeImpl(1, 0L);
    Node e = new NodeImpl(2, 5L);
    Annotation ann = new AnnotationImpl(doc, 123, s, e, "Ghost", Factory.newFeatureMap());

    boolean result = set.remove(ann);
    assertFalse(result);
  }
@Test
  public void testRemoveAnnotationWhenStartNodeValueIsSingleObject() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node start = new NodeImpl(1, 0L);
    Node end = new NodeImpl(2, 10L);
    Annotation a = new AnnotationImpl(doc, 1, start, end, "Single", Factory.newFeatureMap());
    set.add(a);

    set.remove(a); 
    assertEquals(0, set.size());
  }
@Test
  public void testRemoveAnnotationWhenStartNodeValueIsCollectionAndEndsWithOne() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node sharedStart = new NodeImpl(1, 5L);
    Node endA = new NodeImpl(2, 10L);
    Node endB = new NodeImpl(3, 15L);

    Annotation a = new AnnotationImpl(doc, 1, sharedStart, endA, "Shared", Factory.newFeatureMap());
    Annotation b = new AnnotationImpl(doc, 2, sharedStart, endB, "Shared", Factory.newFeatureMap());

    set.add(a);
    set.add(b);
    set.remove(a); 

    assertEquals(1, set.size());
    AnnotationSet result = set.get("Shared");
    assertEquals(1, result.size());
  }
@Test
  public void testGetStrictReturnsEmptyWhenNoAnnotationExactlyMatchesOffsets() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 9L), "X", Factory.newFeatureMap());
    set.add(a);

    AnnotationSet strictResult = set.getStrict(0L, 10L);
    assertEquals(0, strictResult.size());
  }
@Test
  public void testGetStartingAtOffsetWhereNodeExistsButEmptyAnnotations() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node node = new NodeImpl(1, 100L);
    Node end = new NodeImpl(2, 150L);
    Annotation a = new AnnotationImpl(doc, 1, node, end, "T", Factory.newFeatureMap());
    set.add(a);

    set.remove(a); 
    AnnotationSet result = set.getStartingAt(100L);
    assertEquals(0, result.size());
  }
@Test
  public void testGetReturnsSubsetWithMatchingFeatureKeys() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features1 = Factory.newFeatureMap();
    features1.put("a", "x");
    features1.put("b", "y");

    FeatureMap features2 = Factory.newFeatureMap();
    features2.put("a", "x");
    features2.put("c", "z");

    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 0L), new NodeImpl(2, 5L), "TypeF", features1);
    Annotation b = new AnnotationImpl(doc, 2, new NodeImpl(3, 10L), new NodeImpl(4, 15L), "TypeF", features2);

    set.add(a);
    set.add(b);

    Set<Object> mustHave = new HashSet<Object>();
    mustHave.add("a");

    AnnotationSet subset = set.get("TypeF", mustHave);
    assertEquals(2, subset.size());

    mustHave.add("b");
    AnnotationSet filtered = set.get("TypeF", mustHave);
    assertEquals(1, filtered.size());
    assertTrue(filtered.contains(a));
  }
@Test
  public void testGetLongOffsetBeforeAnyAnnotationReturnsEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 50L), new NodeImpl(2, 55L), "A", Factory.newFeatureMap());
    set.add(a);
    AnnotationSet result = set.get(20L);
    assertEquals(0, result.size());
  }
@Test
  public void testGetTypeSetAndOffsetCombinationReturnsExpected() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("x", "1");

    Annotation a = new AnnotationImpl(doc, 1, new NodeImpl(1, 100L), new NodeImpl(2, 110L), "Named", fm1);
    set.add(a);

    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("x", "1");

    AnnotationSet exactMatch = set.get("Named", constraint, 100L);
    assertEquals(1, exactMatch.size());

    FeatureMap wrong = Factory.newFeatureMap();
    wrong.put("not_present", "any");

    AnnotationSet noMatch = set.get("Named", wrong, 100L);
    assertEquals(0, noMatch.size());
  }
@Test
  public void testNextNodeReturnsFirstNodeAfterGivenOffset() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(new AnnotationImpl(doc, 10, new NodeImpl(1, 10L), new NodeImpl(2, 20L), "X", Factory.newFeatureMap()));
    set.add(new AnnotationImpl(doc, 11, new NodeImpl(3, 25L), new NodeImpl(4, 30L), "Y", Factory.newFeatureMap()));

    Node origin = new NodeImpl(999, 15L);
    Node next = set.nextNode(origin);

    assertNotNull(next);
    assertEquals(Long.valueOf(25L), next.getOffset());
  }
@Test
  public void testGetCoveringReturnsInclusiveCoveringAnnotations() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(new AnnotationImpl(doc, 10, new NodeImpl(1, 0L), new NodeImpl(2, 100L), "Wrapper", Factory.newFeatureMap()));

    AnnotationSet result = set.getCovering("Wrapper", 10L, 90L);
    assertEquals(1, result.size());
  }
@Test
  public void testGetContainedWithNoMatchesReturnsEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(new AnnotationImpl(doc, 10, new NodeImpl(1, 50L), new NodeImpl(2, 60L), "T", Factory.newFeatureMap()));
    AnnotationSet result = set.getContained(0L, 40L);
    assertEquals(0, result.size());
  }
@Test
  public void testFirstNodeReturnsNullIfEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Node n = set.firstNode();
    assertNull(n);
  }
@Test
  public void testLastNodeReturnsNullIfEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Node n = set.lastNode();
    assertNull(n);
  } 
}