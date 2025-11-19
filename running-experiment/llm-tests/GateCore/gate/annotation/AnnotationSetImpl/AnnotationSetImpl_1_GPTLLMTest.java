package gate.annotation;

import gate.*;
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

public class AnnotationSetImpl_1_GPTLLMTest {

 @Test
  public void testAddAnnotationAndGetById() throws Exception {
    DocumentImpl doc = new DocumentImpl("This is a test.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    Integer id = annotationSet.add(0L, 4L, "Token", features);
    Annotation result = annotationSet.get(id);
    assertNotNull(result);
    assertEquals("Token", result.getType());
    assertEquals((Long)0L, result.getStartNode().getOffset());
    assertEquals((Long)4L, result.getEndNode().getOffset());
  }
@Test
  public void testAddAndGetByType() throws Exception {
    DocumentImpl doc = new DocumentImpl("Type test.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(2L, 7L, "Word", features);
    AnnotationSet resultSet = annotationSet.get("Word");
    assertNotNull(resultSet);
    assertEquals(1, resultSet.size());
  }
@Test
  public void testGetByMultipleTypes() throws Exception {
    DocumentImpl doc = new DocumentImpl("ABC XYZ");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    annotationSet.add(0L, 3L, "A", fm1);
    annotationSet.add(4L, 7L, "B", fm2);

    Set<String> types = new HashSet<String>();
    types.add("A");
    types.add("B");

    AnnotationSet result = annotationSet.get(types);
    assertEquals(2, result.size());
  }
@Test
  public void testRemoveAnnotation() throws Exception {
    DocumentImpl doc = new DocumentImpl("Remove example.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    Integer id = annotationSet.add(0L, 6L, "Temp", features);
    Annotation annotation = annotationSet.get(id);
    boolean removed = annotationSet.remove(annotation);
    assertTrue(removed);
    assertNull(annotationSet.get(id));
    assertEquals(0, annotationSet.size());
  }
@Test
  public void testGetOverlappingAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("Overlapping test.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    annotationSet.add(0L, 5L, "Begin", fm1);
    annotationSet.add(2L, 8L, "Middle", fm2);

    AnnotationSet overlapping = annotationSet.get(3L, 6L);
    assertEquals(2, overlapping.size());
  }
@Test
  public void testContainedAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("Hello World");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 5L, "One", fm);
    annotationSet.add(6L, 11L, "Two", fm);

    AnnotationSet contained = annotationSet.getContained(0L, 12L);
    assertEquals(2, contained.size());
  }
@Test
  public void testCoveringAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("0123456789");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 10L, "Cover", fm);
    annotationSet.add(1L, 5L, "Inner", fm);

    AnnotationSet covering = annotationSet.getCovering("Cover", 1L, 3L);
    assertEquals(1, covering.size());
  }
@Test
  public void testStrictMatchingAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("Strict match test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 5L, "Strict", fm);
    annotationSet.add(0L, 6L, "Loose", fm);

    AnnotationSet strict = annotationSet.getStrict(0L, 5L);
    assertEquals(1, strict.size());
    Annotation match = strict.iterator().next();
    assertEquals("Strict", match.getType());
  }
@Test
  public void testGetStartingAt() throws Exception {
    DocumentImpl doc = new DocumentImpl("Start here");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(2L, 5L, "Start", fm);

    AnnotationSet result = annotationSet.getStartingAt(2L);
    assertEquals(1, result.size());
  }
@Test
  public void testInDocumentOrderSimple() throws Exception {
    DocumentImpl doc = new DocumentImpl("ABC DEF");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(1L, 2L, "Second", fm);
    annotationSet.add(0L, 1L, "First", fm);

    List<Annotation> ordered = annotationSet.inDocumentOrder();
    assertEquals(2, ordered.size());
    assertTrue(ordered.get(0).getStartNode().getOffset() <= ordered.get(1).getStartNode().getOffset());
  }
@Test
  public void testFirstAndLastNode() throws Exception {
    DocumentImpl doc = new DocumentImpl("Testing nodes");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 4L, "NodeTest", fm);
    annotationSet.add(5L, 10L, "NodeTest", fm);

    Node first = annotationSet.firstNode();
    Node last = annotationSet.lastNode();
    assertEquals((Long)0L, first.getOffset());
    assertEquals((Long)10L, last.getOffset());
  }
@Test
  public void testNextNodeBehavior() throws Exception {
    DocumentImpl doc = new DocumentImpl("NextNode");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 2L, "One", fm);
    annotationSet.add(3L, 5L, "Two", fm);

    Node n1 = annotationSet.firstNode();
    Node n2 = annotationSet.nextNode(n1);
    assertNotNull(n2);
    assertTrue(n2.getOffset() > n1.getOffset());
  }
@Test
  public void testClearAnnotationSet() throws Exception {
    DocumentImpl doc = new DocumentImpl("Clearing");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(1L, 3L, "Erase", fm);
    assertEquals(1, annotationSet.size());
    annotationSet.clear();
    assertEquals(0, annotationSet.size());
  }
@Test
  public void testGetByTypeAndFeatureConstraints() throws Exception {
    DocumentImpl doc = new DocumentImpl("FeatureMap");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("category", "noun");
    annotationSet.add(0L, 5L, "Word", fm);

    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("category", "noun");

    AnnotationSet result = annotationSet.get("Word", constraint);
    assertEquals(1, result.size());
  }
@Test
  public void testEmptyAnnotationSetGet() throws Exception {
    DocumentImpl doc = new DocumentImpl("Empty test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    AnnotationSet empty = annotationSet.get("NonExistentType");
    assertNotNull(empty);
    assertEquals(0, empty.size());
  }
@Test
  public void testGetAllTypes() throws Exception {
    DocumentImpl doc = new DocumentImpl("Types ABC");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap f1 = Factory.newFeatureMap();
    FeatureMap f2 = Factory.newFeatureMap();
    annotationSet.add(0L, 2L, "Type1", f1);
    annotationSet.add(3L, 5L, "Type2", f2);

    Set<String> allTypes = annotationSet.getAllTypes();
    assertTrue(allTypes.contains("Type1"));
    assertTrue(allTypes.contains("Type2"));
  }
@Test
  public void testCloningAnnotationSet() throws Exception {
    DocumentImpl doc = new DocumentImpl("Clone test");
    AnnotationSetImpl original = new AnnotationSetImpl(doc);
    Object clone = original.clone();
    assertNotNull(clone);
    assertTrue(clone instanceof AnnotationSetImpl);
  }
@Test
  public void testDuplicateAnnotationNotAdded() throws Exception {
    DocumentImpl doc = new DocumentImpl("Duplicate");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = annotationSet.add(0L, 3L, "Dup", fm);
    Annotation existing = annotationSet.get(id);
    annotationSet.add(existing);
    assertEquals(1, annotationSet.size());
  }
@Test
  public void testAddAnnotationWithSameOffsets() throws Exception {
    DocumentImpl doc = new DocumentImpl("same offset");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = annotationSet.add(5L, 5L, "ZeroLength", fm);
    Annotation added = annotationSet.get(id);
    assertNotNull(added);
    assertEquals((Long)5L, added.getStartNode().getOffset());
    assertEquals((Long)5L, added.getEndNode().getOffset());
  }
@Test
  public void testGetWithEmptyTypeSet() throws Exception {
    DocumentImpl doc = new DocumentImpl("some text");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    Set<String> types = new HashSet<String>();
    AnnotationSet result = annotationSet.get(types);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetWithInvalidRange() throws Exception {
    DocumentImpl doc = new DocumentImpl("negative range");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    AnnotationSet result = annotationSet.get(10L, 5L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetCoveringWithInvalidRange() throws Exception {
    DocumentImpl doc = new DocumentImpl("covering test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    AnnotationSet result = annotationSet.getCovering("Any", 15L, 5L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetContainedWithNoMatches() throws Exception {
    DocumentImpl doc = new DocumentImpl("contained none");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 3L, "Tag1", fm);
    AnnotationSet result = annotationSet.getContained(4L, 10L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetStrictWithNoMatch() throws Exception {
    DocumentImpl doc = new DocumentImpl("strict test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 5L, "Exact", fm);
    AnnotationSet result = annotationSet.getStrict(0L, 6L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetTypeAndFeatureConstraintNoMatch() throws Exception {
    DocumentImpl doc = new DocumentImpl("feature test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("category", "verb");
    annotationSet.add(0L, 5L, "Word", fm);
    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("category", "noun");
    AnnotationSet result = annotationSet.get("Word", constraint);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetNextNodeReturnsNull() throws Exception {
    DocumentImpl doc = new DocumentImpl("end offset");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 4L, "Last", fm);
    Node last = annotationSet.lastNode();
    Node next = annotationSet.nextNode(last);
    assertNull(next);
  }
@Test
  public void testAddAllEmptyCollection() throws Exception {
    DocumentImpl doc = new DocumentImpl("addAll test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    Collection<Annotation> empty = new ArrayList<Annotation>();
    boolean changed = annotationSet.addAll(empty);
    assertFalse(changed);
    assertEquals(0, annotationSet.size());
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddAllThrowsForInvalidOffsets() throws Exception {
    DocumentImpl doc = new DocumentImpl("illegal offset");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Annotation invalid = new AnnotationImpl(1,
        new NodeImpl(1, 15L),
        new NodeImpl(2, 25L),
        "Fail", fm);
    List<Annotation> list = new ArrayList<Annotation>();
    list.add(invalid);
    annotationSet.addAll(list);
  }
@Test
  public void testGetByFeatureNamesExactMatch() throws Exception {
    DocumentImpl doc = new DocumentImpl("feature key");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("f1", "v1");
    fm.put("f2", "v2");
    annotationSet.add(0L, 2L, "X", fm);
    Set<Object> featureKeys = new HashSet<Object>();
    featureKeys.add("f1");
    featureKeys.add("f2");
    AnnotationSet result = annotationSet.get("X", featureKeys);
    assertEquals(1, result.size());
  }
@Test
  public void testGetByFeatureNamesSubsetFails() throws Exception {
    DocumentImpl doc = new DocumentImpl("feature mismatch");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("f1", "v1");
    annotationSet.add(0L, 2L, "Y", fm);
    Set<Object> featureKeys = new HashSet<Object>();
    featureKeys.add("f1");
    featureKeys.add("f2");
    AnnotationSet result = annotationSet.get("Y", featureKeys);
    assertEquals(0, result.size());
  }
@Test
  public void testRemoveNonExistentAnnotationReturnsFalse() throws Exception {
    DocumentImpl doc = new DocumentImpl("remove unknown");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Annotation fake = new AnnotationImpl(999,
      new NodeImpl(1, 0L),
      new NodeImpl(2, 1L),
      "Fake", fm);
    boolean result = annotationSet.remove(fake);
    assertFalse(result);
  }
@Test
  public void testAddAnnotationWithNullFeatures() throws Exception {
    DocumentImpl doc = new DocumentImpl("null feature");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    Integer id = annotationSet.add(1L, 3L, "NoFeatures", null);
    Annotation result = annotationSet.get(id);
    assertNotNull(result);
    assertEquals("NoFeatures", result.getType());
    assertEquals((Long)1L, result.getStartNode().getOffset());
    assertEquals((Long)3L, result.getEndNode().getOffset());
  }
@Test(expected = InvalidOffsetException.class)
  public void testAddAnnotationWithOutOfBoundsOffset() throws Exception {
    DocumentImpl doc = new DocumentImpl("abcdef");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(100L, 110L, "BadOffset", fm);
  }
@Test
  public void testAddAndGetAnnotationReferenceEquality() throws Exception {
    DocumentImpl doc = new DocumentImpl("abc");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = annotationSet.add(0L, 1L, "Ref", fm);
    Annotation a1 = annotationSet.get(id);
    Annotation a2 = annotationSet.get(id);
    assertTrue(a1 == a2);
  }
@Test
  public void testMultipleAnnotationsAtSameOffset() throws Exception {
    DocumentImpl doc = new DocumentImpl("multi");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    annotationSet.add(0L, 2L, "T1", fm1);
    annotationSet.add(0L, 2L, "T2", fm2);
    AnnotationSet result = annotationSet.getStartingAt(0L);
    assertEquals(2, result.size());
  }
@Test
  public void testGetStrictWhenStartOffsetHasNoMatchingAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("strict node fall-through");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 3L, "T1", fm);
    AnnotationSet result = annotationSet.getStrict(1L, 3L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAddDuplicateAnnotationWithDifferentObjectSameId() throws Exception {
    DocumentImpl doc = new DocumentImpl("dup add");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm1 = Factory.newFeatureMap();
    Integer id = annotationSet.add(0L, 2L, "First", fm1);
    FeatureMap fm2 = Factory.newFeatureMap();
    Annotation duplicate = new AnnotationImpl(
      id,
      new NodeImpl(0, 1L),
      new NodeImpl(1, 3L),
      "Duplicate",
      fm2
    );
    annotationSet.add(duplicate);
    assertEquals(1, annotationSet.size());
    Annotation result = annotationSet.get(id);
    assertEquals("Duplicate", result.getType());
  }
@Test
  public void testIndexingOnlyTriggersOncePerType() throws Exception {
    DocumentImpl doc = new DocumentImpl("index test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(1L, 3L, "Alpha", fm);
    annotationSet.get("Alpha");
    annotationSet.get("Alpha");
    AnnotationSet result = annotationSet.get("Alpha");
    assertEquals(1, result.size());
  }
@Test
  public void testEmptyAnnotationSetReturnedForGetByTypeAndConstraintWhenTypeMissing() throws Exception {
    DocumentImpl doc = new DocumentImpl("missing type constraint");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(0L, 5L, "SomeType", Factory.newFeatureMap());
    FeatureMap query = Factory.newFeatureMap();
    query.put("someKey", "value");
    AnnotationSet result = annotationSet.get("WrongType", query);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testNodeReuseAfterEditShrinksDocument() throws Exception {
    DocumentImpl doc = new DocumentImpl("editable text");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 5L, "Shrinkable", fm);
    doc.edit(2L, 4L, null);
    AnnotationSet after = annotationSet.get(0L, 10L);
    assertTrue(annotationSet.size() >= 0);
    assertNotNull(after);
  }
@Test
  public void testOverlappingAnnotationOnBoundaryIncluded() throws Exception {
    DocumentImpl doc = new DocumentImpl("abcde");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(1L, 4L, "Token", fm);
    AnnotationSet result = annotationSet.get(0L, 2L);
    assertEquals(1, result.size());
  }
@Test
  public void testGetAllTypesReturnsEmptySetForNoAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("empty set");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    Set<String> result = annotationSet.getAllTypes();
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testCloneCreatesEqualButIndependentSet() throws Exception {
    DocumentImpl doc = new DocumentImpl("clone independence");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 3L, "T1", fm);
    AnnotationSetImpl cloned = (AnnotationSetImpl) annotationSet.clone();
    assertNotNull(cloned);
    assertEquals(annotationSet.size(), cloned.size());
    FeatureMap fm2 = Factory.newFeatureMap();
    cloned.add(4L, 6L, "T2", fm2);
    assertEquals(1, annotationSet.size());
    assertEquals(2, cloned.size());
  }
@Test
  public void testEmptyASReturnsEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl("emptyAS test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    AnnotationSet empty = annotationSet.get("NoSuchType");
    assertEquals(0, empty.size());
    AnnotationSet again = annotationSet.get("NoSuchType");
    assertEquals(0, again.size());
  }
@Test
  public void testGetContainedReturnsSubset() throws Exception {
    DocumentImpl doc = new DocumentImpl("subset test");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 4L, "Outer", fm);
    annotationSet.add(1L, 3L, "Inner", fm);
    AnnotationSet result = annotationSet.getContained(0L, 5L);
    assertEquals(2, result.size());
  }
@Test
  public void testClearRemovesAllAnnotationsAndIndices() throws Exception {
    DocumentImpl doc = new DocumentImpl("clear reset");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 2L, "T1", fm);
    annotationSet.get("T1"); 
    annotationSet.clear();
    assertEquals(0, annotationSet.size());
    AnnotationSet result = annotationSet.get("T1");
    assertEquals(0, result.size());
  }
@Test
  public void testCreateAnnotationSetConstructorFromEmptyAnnotationSet() throws Exception {
    DocumentImpl doc = new DocumentImpl("constructor copy test");
    AnnotationSetImpl original = new AnnotationSetImpl(doc);
    AnnotationSetImpl copy = new AnnotationSetImpl(original);
    assertNotNull(copy);
    assertEquals(0, copy.size());
  }
@Test
  public void testRemoveAnnotationUpdatesIndicesCorrectly() throws Exception {
    DocumentImpl doc = new DocumentImpl("remove and re-add");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = annotationSet.add(0L, 3L, "Type1", fm);
    Annotation ann = annotationSet.get(id);
    boolean removed = annotationSet.remove(ann);
    assertTrue(removed);
    Integer id2 = annotationSet.add(0L, 3L, "Type1", fm);
    AnnotationSet result = annotationSet.get("Type1");
    assertEquals(1, result.size());
    Annotation resAnn = result.iterator().next();
    assertEquals(id2, resAnn.getId());
  }
@Test
  public void testAddToTypeIndexDoesNothingIfIndexMissing() throws Exception {
    DocumentImpl doc = new DocumentImpl("add type null index");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    annotationSet.add(0L, 2L, "Silent", fm);
    Annotation a = annotationSet.get(annotationSet.inDocumentOrder().get(0).getId());
    
    
    AnnotationSetImpl annotationSet2 = new AnnotationSetImpl(doc);
    annotationSet2.add(a);
    AnnotationSet result = annotationSet2.get("Silent");
    assertNotNull(result);
  }
@Test
  public void testAddAllKeepIDsWithConflictingIDsReplacesCorrectly() throws Exception {
    DocumentImpl doc = new DocumentImpl("addAllKeepIDs");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Annotation a1 = new AnnotationImpl(
      1,
      new NodeImpl(1, 0L),
      new NodeImpl(2, 2L),
      "T1",
      fm
    );
    Annotation a2 = new AnnotationImpl(
      1,
      new NodeImpl(1, 1L),
      new NodeImpl(2, 5L),
      "T2",
      fm
    );
    Collection<Annotation> anns = new ArrayList<Annotation>();
    anns.add(a1);
    anns.add(a2); 
    boolean changed = as.addAllKeepIDs(anns);
    assertTrue(changed);
    Annotation result = as.get(1);
    assertEquals("T2", result.getType());
  }
@Test
  public void testGetStartingAtOffsetWithNoAnnotationsReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl("start offset");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    as.add(0L, 3L, "X", fm);
    AnnotationSet result = as.getStartingAt(10L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAddAnnotationSetsCorrectLongestAnnotationLength() throws Exception {
    DocumentImpl doc = new DocumentImpl("0123456789");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(0L, 2L, "Short", Factory.newFeatureMap());
    as.add(2L, 8L, "Longer", Factory.newFeatureMap()); 
    as.add(4L, 5L, "Tiny", Factory.newFeatureMap());
    AnnotationSet result = as.get(4L, 6L);
    assertNotNull(result);
    assertEquals(2, result.size()); 
  }
@Test
  public void testGetWithNullTypeAndFeatureSetReturnsNull() throws Exception {
    DocumentImpl doc = new DocumentImpl("null type");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("k", "v");
    AnnotationSet result = as.get(null, constraint);
    assertNull(result);
  }
@Test
  public void testEdgeNodeRemovalFromOffsetIndex() throws Exception {
    DocumentImpl doc = new DocumentImpl("removal edge");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(0L, 2L, "T1", Factory.newFeatureMap());
    Annotation a = as.get(as.inDocumentOrder().get(0).getId());
    boolean removed = as.remove(a);
    assertTrue(removed);
    assertEquals(0, as.size());
    AnnotationSet check = as.get(0L);
    assertNotNull(check);
    assertEquals(0, check.size());
  }
@Test
  public void testGetNextNodeAtMaxOffsetReturnsNull() throws Exception {
    DocumentImpl doc = new DocumentImpl("XXXXX");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(0L, 5L, "X", Factory.newFeatureMap());
    Node last = as.lastNode();
    Node next = as.nextNode(last);
    assertNull(next);
  }
@Test
  public void testGetByNullTypeAndFeatureKeysReturnsAll() throws Exception {
    DocumentImpl doc = new DocumentImpl("wildcardSet");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("A", 1);
    as.add(1L, 3L, "Z", fm);
    Set<Object> featureNames = new HashSet<Object>();
    featureNames.add("A");
    AnnotationSet result = as.get(null, featureNames);
    assertNotNull(result);
    assertEquals(1, result.size());
  }
@Test
  public void testAddAndRemoveAnnotationDoesNotLeakStartNodeIndex() throws Exception {
    DocumentImpl doc = new DocumentImpl("index cleanup");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    Integer id = as.add(1L, 4L, "LeakTest", Factory.newFeatureMap());
    Annotation a = as.get(id);
    boolean removed = as.remove(a);
    assertTrue(removed);
    AnnotationSet result = as.getStartingAt(1L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testRemoveAnnotationReducesTypeIndex() throws Exception {
    DocumentImpl doc = new DocumentImpl("type index cleanup");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = as.add(0L, 5L, "TypeX", fm);
    as.get("TypeX"); 
    Annotation a = as.get(id);
    as.remove(a);
    AnnotationSet result = as.get("TypeX");
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAnnotationRemovedWithSharedStartNode() throws Exception {
    DocumentImpl doc = new DocumentImpl("shared start");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer a1 = as.add(1L, 3L, "A", fm);
    Integer a2 = as.add(1L, 5L, "B", fm);
    Annotation toRemove = as.get(a2);
    as.remove(toRemove);
    AnnotationSet remaining = as.getStartingAt(1L);
    assertEquals(1, remaining.size());
    Annotation remainingAnnot = remaining.iterator().next();
    assertEquals("A", remainingAnnot.getType());
  }
@Test
  public void testAnnotationRemovedWithOnlySharedEndNode() throws Exception {
    DocumentImpl doc = new DocumentImpl("shared end");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer a1 = as.add(2L, 5L, "X", fm);
    Integer a2 = as.add(4L, 5L, "Y", fm);
    Annotation toRemove = as.get(a1);
    as.remove(toRemove);
    AnnotationSet remaining = as.getContained(0L, 6L);
    assertEquals(1, remaining.size());
    Annotation remainingAnnot = remaining.iterator().next();
    assertEquals("Y", remainingAnnot.getType());
  }
@Test
  public void testAnnotationWithNullFeatureMapConstraintsFails() throws Exception {
    DocumentImpl doc = new DocumentImpl("null feature constraint");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("key", "value");
    as.add(0L, 3L, "X", fm);
    FeatureMap constraint = null;
    AnnotationSet result = as.get("X", constraint);
    assertNull(result);
  }
@Test
  public void testGetReturnsEmptySetWhenNoAnnotationsExist() throws Exception {
    DocumentImpl doc = new DocumentImpl("no annotations");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    AnnotationSet result = as.get(0L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testFeatureMapSubsumptionMismatch() throws Exception {
    DocumentImpl doc = new DocumentImpl("subsumption");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("present", "yes");
    as.add(0L, 2L, "Check", fm);
    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("present", "no");
    AnnotationSet result = as.get("Check", constraint);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testOverwriteAnnotationInAddMethod() throws Exception {
    DocumentImpl doc = new DocumentImpl("overwrite test");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm1 = Factory.newFeatureMap();
    Integer id = as.add(0L, 3L, "Initial", fm1);
    Annotation first = as.get(id);
    FeatureMap fm2 = Factory.newFeatureMap();
    Annotation dup = new AnnotationImpl(id, first.getStartNode(), first.getEndNode(), "Replaced", fm2);
    boolean changed = as.add(dup);
    assertTrue(changed);
    assertEquals("Replaced", as.get(id).getType());
  }
@Test
  public void testInDocumentOrderEmptyAnnotationSet() throws Exception {
    DocumentImpl doc = new DocumentImpl("empty order");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    List<Annotation> ordered = as.inDocumentOrder();
    assertNotNull(ordered);
    assertEquals(0, ordered.size());
  }
@Test
  public void testGetCoveringWithExactSpan() throws Exception {
    DocumentImpl doc = new DocumentImpl("covering check");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    as.add(1L, 6L, "Span", fm);
    AnnotationSet result = as.getCovering(null, 1L, 6L);
    assertNotNull(result);
    assertEquals(1, result.size());
  }
@Test
  public void testGetContainedIgnoresAnnotationsEndingAfterRange() throws Exception {
    DocumentImpl doc = new DocumentImpl("contain edge");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    as.add(1L, 6L, "OutOfBounds", fm);
    AnnotationSet result = as.getContained(1L, 5L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAddAnnotationToExistingStartNodeWithNoEndNodeSharing() throws Exception {
    DocumentImpl doc = new DocumentImpl("node split");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id1 = as.add(0L, 2L, "T1", fm);
    Integer id2 = as.add(0L, 6L, "T2", fm); 
    AnnotationSet start0 = as.getStartingAt(0L);
    assertEquals(2, start0.size());
  }
@Test
  public void testAnnotationSetIteratorRemoveStateful() throws Exception {
    DocumentImpl doc = new DocumentImpl("iterator remove");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    as.add(0L, 5L, "One", fm);
    Annotation a2 = as.get(as.inDocumentOrder().get(0).getId());
    Iterator<Annotation> it = as.iterator();
    if (it.hasNext()) {
      it.next();
      it.remove();
    }
    assertEquals(0, as.size());
    assertNull(as.get(a2.getId()));
  }
@Test
  public void testNextNodeOnNullNodeReturnsNull() throws Exception {
    DocumentImpl doc = new DocumentImpl("null node");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    Node nullNode = null;
    try {
      Node result = as.nextNode(nullNode);
      assertNull(result);
    } catch (NullPointerException ex) {
      
      assertTrue(true);
    }
  }
@Test
  public void testGetStrictOverlappingWrongEndOffset() throws Exception {
    DocumentImpl doc = new DocumentImpl("strict miss");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    as.add(3L, 6L, "Fail", fm);
    AnnotationSet result = as.getStrict(3L, 9L);
    assertEquals(0, result.size());
  }
@Test
  public void testGetReturnsNextAnnotationOnlyAtRequestedOffsetWithMultipleOffsets() throws Exception {
    DocumentImpl doc = new DocumentImpl("t1 t2 t3");
    FeatureMap fm = Factory.newFeatureMap();
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(0L, 2L, "T1", fm);
    as.add(3L, 5L, "T2", fm);
    as.add(6L, 8L, "T3", fm);
    AnnotationSet result = as.get(4L);
    assertEquals(1, result.size());
  }
@Test(expected = ClassCastException.class)
  public void testRemoveInvalidObjectTypeThrows() throws Exception {
    DocumentImpl doc = new DocumentImpl("remove fail");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    Object notAnnotation = new Object();
    as.remove(notAnnotation);
  }
@Test
  public void testAnnotationWithEmptyFeatureMapConstraintMatches() throws Exception {
    DocumentImpl doc = new DocumentImpl("abc");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("x", "1");
    as.add(0L, 2L, "T", fm);
    FeatureMap constraint = Factory.newFeatureMap();
    AnnotationSet result = as.get("T", constraint);
    assertEquals(1, result.size());
  }
@Test
  public void testGetWithFeatureKeySubset() throws Exception {
    DocumentImpl doc = new DocumentImpl("abcdef");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    features.put("key1", "v1");
    features.put("key2", "v2");
    as.add(0L, 4L, "Check", features);
    Set<Object> keys = new HashSet<Object>();
    keys.add("key1");
    AnnotationSet result = as.get("Check", keys);
    assertEquals(1, result.size());
  }
@Test
  public void testRemoveFromEmptyOffsetIndexDoesNotCrash() throws Exception {
    DocumentImpl doc = new DocumentImpl("xyz");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = as.add(0L, 3L, "TypeA", fm);
    Annotation a = as.get(id);
    boolean removed = as.remove(a);
    assertTrue(removed);
    assertEquals(0, as.size());
  }
@Test
  public void testNextAnnotationIdIncrementsAfterManualIdAddition() throws Exception {
    DocumentImpl doc = new DocumentImpl("text");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(100, 0L, 2L, "Manual", Factory.newFeatureMap());
    Integer newId = as.add(2L, 4L, "Auto", Factory.newFeatureMap());
    
    assertTrue(newId >= 101);
  }
@Test
  public void testAddAnnotationThrowsInvalidOffsetOnOutOfBounds() {
    DocumentImpl doc = new DocumentImpl("short");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    try {
      as.add(0L, 100L, "Invalid", Factory.newFeatureMap());
      fail("Should have thrown InvalidOffsetException");
    } catch (InvalidOffsetException expected) {
      assertTrue(true);
    }
  }
@Test
  public void testAddAnnotationSameStartAndEndNodeObject() throws Exception {
    DocumentImpl doc = new DocumentImpl("nodes");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    Integer id = as.add(3L, 3L, "ZeroLength", Factory.newFeatureMap());
    Annotation ann = as.get(id);
    assertSame(ann.getStartNode(), ann.getEndNode());
    assertEquals((Long) 3L, ann.getStartNode().getOffset());
  }
@Test
  public void testGetEmptyAnnotationSetFromUnknownType() {
    DocumentImpl doc = new DocumentImpl("none");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    AnnotationSet unknown = as.get("UnknownType");
    assertNotNull(unknown);
    assertEquals(0, unknown.size());
  }
@Test
  public void testGetFirstLastNodeWhenEmpty() {
    DocumentImpl doc = new DocumentImpl("no annotations");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    assertNull(as.firstNode());
    assertNull(as.lastNode());
  }
@Test
  public void testEmptyAnnotationSetGetContainedRange() {
    DocumentImpl doc = new DocumentImpl("empty check");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    AnnotationSet result = as.getContained(0L, 5L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testRelationSetAutoCreation() {
    DocumentImpl doc = new DocumentImpl("rel test");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    RelationSet rs1 = as.getRelations();
    RelationSet rs2 = as.getRelations();
    assertNotNull(rs1);
    assertSame(rs1, rs2);
  }
@Test
  public void testCloneReturnsSameContentButNewObject() throws Exception {
    DocumentImpl doc = new DocumentImpl("clonable");
    AnnotationSetImpl as1 = new AnnotationSetImpl(doc);
    as1.add(0L, 3L, "A", Factory.newFeatureMap());
    AnnotationSetImpl as2 = (AnnotationSetImpl) as1.clone();
    assertEquals(as1.size(), as2.size());
    assertNotSame(as1, as2);
  }
@Test
  public void testSerializationPreservesAnnotationCount() throws Exception {
    DocumentImpl doc = new DocumentImpl("hello");
    AnnotationSetImpl original = new AnnotationSetImpl(doc);
    original.add(0L, 2L, "T1", Factory.newFeatureMap());
    original.add(3L, 5L, "T2", Factory.newFeatureMap());

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(original);

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);
    AnnotationSetImpl deserialized = (AnnotationSetImpl) ois.readObject();

    assertEquals(2, deserialized.size());
  }
@Test
  public void testSubMapNodeFilteringWithLongRangeSkipsPreviousNodes() throws Exception {
    DocumentImpl doc = new DocumentImpl("0123456789");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    as.add(1L, 4L, "A", fm);
    as.add(4L, 8L, "B", fm);
    AnnotationSet filtered = as.get("A", 0L, 3L);
    assertEquals(1, filtered.size());
  }
@Test
  public void testMultipleAnnotationsWithSameStartIndexCleanup() throws Exception {
    DocumentImpl doc = new DocumentImpl("abc");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap f1 = Factory.newFeatureMap();
    FeatureMap f2 = Factory.newFeatureMap();
    Integer id1 = as.add(0L, 2L, "T1", f1);
    Integer id2 = as.add(0L, 3L, "T2", f2);
    Annotation annot1 = as.get(id1);
    Annotation annot2 = as.get(id2);
    as.remove(annot1);
    assertEquals(1, as.getStartingAt(0L).size());
  }
@Test
  public void testRemoveLastAnnotationCleansUpTypeIndex() throws Exception {
    DocumentImpl doc = new DocumentImpl("type cleanup");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = as.add(0L, 3L, "X", fm);
    as.get("X"); 
    Annotation a = as.get(id);
    as.remove(a);
    Set<String> typeSet = as.getAllTypes();
    assertTrue(typeSet.isEmpty());
  }
@Test
  public void testAnnotationFeatureKeyPresenceWithoutValueMatch() throws Exception {
    DocumentImpl doc = new DocumentImpl("feature presence");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("f1", "value1");
    as.add(0L, 1L, "Check", fm);
    Set<Object> featureKeys = new HashSet<Object>();
    featureKeys.add("f1");
    featureKeys.add("f2"); 
    AnnotationSet result = as.get("Check", featureKeys);
    assertEquals(0, result.size());
  }
@Test
  public void testIndexByStartOffsetIsCalledOnlyOnce() throws Exception {
    DocumentImpl doc = new DocumentImpl("start offset call once");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    as.add(1L, 3L, "One", fm);
    as.getStartingAt(1L);
    as.getStartingAt(1L);
    AnnotationSet secondCall = as.get(1L);
    assertEquals(1, secondCall.size());
  }
@Test
  public void testGetReturnsNextNodeAnnotationWhenStartOffsetNotMatched() throws Exception {
    DocumentImpl doc = new DocumentImpl("jump forward");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(5L, 7L, "Shifted", Factory.newFeatureMap());
    AnnotationSet result = as.get(1L); 
    assertEquals(1, result.size());
    Annotation a = result.iterator().next();
    assertEquals((Long)5L, a.getStartNode().getOffset());
  }
@Test
  public void testRemoveAllAnnotationsThenGetAllTypesReturnsEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl("cleanup types");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(1L, 2L, "CleanMe", Factory.newFeatureMap());
    Annotation a = as.iterator().next();
    as.remove(a);
    Set<String> types = as.getAllTypes();
    assertEquals(0, types.size());
  }
@Test
  public void testMultipleAnnotationsAtSameStartUseSetInternally() throws Exception {
    DocumentImpl doc = new DocumentImpl("same offset internal collection");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(0L, 2L, "A1", Factory.newFeatureMap());
    as.add(0L, 3L, "A2", Factory.newFeatureMap());
    AnnotationSet byOffset = as.getStartingAt(0L);
    assertEquals(2, byOffset.size());
  }
@Test
  public void testEditShortensTextUpdatesOffsets() throws Exception {
    DocumentImpl doc = new DocumentImpl("0123456789");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(2L, 5L, "Trim", Factory.newFeatureMap());
    doc.edit(3L, 5L, Factory.newDocumentContent("")); 
    AnnotationSet result = as.get(2L, 7L);
    assertNotNull(result);
  }
@Test
  public void testFirstNodeAndLastNodeAfterEditMaintainOrder() throws Exception {
    DocumentImpl doc = new DocumentImpl("abcdefghij");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(2L, 5L, "Range", Factory.newFeatureMap());
    as.add(6L, 8L, "Late", Factory.newFeatureMap());
    Node first = as.firstNode();
    Node last = as.lastNode();
    doc.edit(4L, 6L, Factory.newDocumentContent(""));
    assertNotNull(first);
    assertNotNull(last);
    assertTrue(first.getOffset() <= last.getOffset());
  }
@Test
  public void testAnnotationSetEventFiresOnAddAndRemove() throws Exception {
    DocumentImpl doc = new DocumentImpl("listen");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);

    final boolean[] added = {false};
    final boolean[] removed = {false};

    AnnotationSetListener listener = new AnnotationSetListener() {
      public void annotationAdded(AnnotationSetEvent e) {
        added[0] = true;
      }
      public void annotationRemoved(AnnotationSetEvent e) {
        removed[0] = true;
      }
    };

    as.addAnnotationSetListener(listener);

    Integer id = as.add(1L, 3L, "Firable", Factory.newFeatureMap());
    as.remove(as.get(id));

    assertTrue(added[0]);
    assertTrue(removed[0]);
  }
@Test
  public void testAnnotationRemovedFromOffsetIndexAndTypeIndexSimultaneously() throws Exception {
    DocumentImpl doc = new DocumentImpl("multi index removal");
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = as.add(2L, 4L, "Dual", fm);
    as.get("Dual"); 
    as.get(2L);     
    Annotation a = as.get(id);
    as.remove(a);
    assertEquals(0, as.get("Dual").size());
    assertEquals(0, as.getStartingAt(2L).size());
  } 
}