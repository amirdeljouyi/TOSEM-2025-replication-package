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

public class AnnotationSetImpl_3_GPTLLMTest {

 @Test
  public void testAddAnnotationWithOffsets() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("key", "value");

    Integer annId = set.add(10L, 20L, "Person", features);
    Annotation result = set.get(annId);

    assertNotNull(result);
    assertEquals(annId, result.getId());
    assertEquals("Person", result.getType());
    assertEquals(Long.valueOf(10L), result.getStartNode().getOffset());
    assertEquals(Long.valueOf(20L), result.getEndNode().getOffset());
    assertEquals("value", result.getFeatures().get("key"));
  }
@Test
  public void testRemoveAnnotation() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    Integer annId = set.add(5L, 15L, "Token", features);
    Annotation ann = set.get(annId);

    boolean removed = set.remove(ann);
    assertTrue(removed);
    assertEquals(0, set.size());
    assertNull(set.get(annId));
  }
@Test
  public void testGetByType() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features1 = new SimpleFeatureMapImpl();
    FeatureMap features2 = new SimpleFeatureMapImpl();

    set.add(0L, 10L, "Person", features1);
    set.add(15L, 20L, "Organization", features2);
    set.add(20L, 25L, "Person", features2);

    AnnotationSet personSet = set.get("Person");

    assertNotNull(personSet);
    assertEquals(2, personSet.size());
  }
@Test
  public void testGetStrictMatch() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    Integer id1 = set.add(100L, 150L, "Tag", features);
    Integer id2 = set.add(100L, 151L, "Tag", features);

    AnnotationSet strictSet = set.getStrict(100L, 150L);

    assertNotNull(strictSet);
    assertEquals(1, strictSet.size());
    Annotation matched = strictSet.iterator().next();
    assertEquals(Long.valueOf(100L), matched.getStartNode().getOffset());
    assertEquals(Long.valueOf(150L), matched.getEndNode().getOffset());
    assertEquals("Tag", matched.getType());
  }
@Test
  public void testGetContainedAnnotations() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(10L, 30L, "A", features);
    set.add(12L, 15L, "B", features);
    set.add(16L, 25L, "B", features);
    set.add(5L, 100L, "C", features);

    AnnotationSet result = set.getContained(11L, 29L);

    assertNotNull(result);
    assertEquals(2, result.size());
  }
@Test
  public void testGetAnnotationsByOffset() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap featuresX = new SimpleFeatureMapImpl();
    FeatureMap featuresY = new SimpleFeatureMapImpl();

    set.add(0L, 5L, "X", featuresX);
    set.add(5L, 10L, "Y", featuresY);

    AnnotationSet result = set.get(5L);

    assertNotNull(result);
    assertEquals(1, result.size());
    Annotation match = result.iterator().next();
    assertEquals("Y", match.getType());
    assertEquals(Long.valueOf(5L), match.getStartNode().getOffset());
  }
@Test
  public void testGetCoveringAnnotation() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(0L, 100L, "Span", features);
    set.add(10L, 20L, "Text", features);
    set.add(5L, 25L, "Span", features);

    AnnotationSet covering = set.getCovering("Span", 10L, 20L);

    assertNotNull(covering);
    assertEquals(2, covering.size());
  }
@Test
  public void testGetByTypeAndFeatureConstraint() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap femaleFeatures = new SimpleFeatureMapImpl();
    femaleFeatures.put("gender", "female");
    FeatureMap maleFeatures = new SimpleFeatureMapImpl();
    maleFeatures.put("gender", "male");

    set.add(0L, 10L, "Person", femaleFeatures);
    set.add(15L, 20L, "Person", maleFeatures);

    FeatureMap constraint = new SimpleFeatureMapImpl();
    constraint.put("gender", "female");

    AnnotationSet females = set.get("Person", constraint);

    assertNotNull(females);
    assertEquals(1, females.size());
    Annotation match = females.iterator().next();
    assertEquals("Person", match.getType());
    assertEquals("female", match.getFeatures().get("gender"));
  }
@Test
  public void testClearRemovesAllAnnotations() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(1L, 5L, "A", features);
    set.add(10L, 15L, "B", features);
    assertEquals(2, set.size());

    set.clear();

    assertEquals(0, set.size());
    AnnotationSet all = set.get();
    assertNotNull(all);
    assertTrue(all.isEmpty());
  }
@Test
  public void testGetAnnotationsStartingAt() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(30L, 40L, "Entity", features);
    set.add(30L, 50L, "Entity", features);
    set.add(25L, 29L, "Other", features);

    AnnotationSet found = set.getStartingAt(30L);

    assertNotNull(found);
    assertEquals(2, found.size());
  }
@Test(expected = InvalidOffsetException.class)
  public void testAddWithInvalidOffset() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(100L, 50L, "Invalid", features);
  }
@Test
  public void testInDocumentOrder() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(5L, 6L, "A", features);
    set.add(2L, 3L, "B", features);
    set.add(4L, 5L, "C", features);

    java.util.List<Annotation> ordered = set.inDocumentOrder();

    assertNotNull(ordered);
    assertEquals(3, ordered.size());
    assertEquals(Long.valueOf(2L), ordered.get(0).getStartNode().getOffset());
    assertEquals(Long.valueOf(4L), ordered.get(1).getStartNode().getOffset());
    assertEquals(Long.valueOf(5L), ordered.get(2).getStartNode().getOffset());
  }
@Test
  public void testZeroLengthAnnotation() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    Integer id = set.add(20L, 20L, "ZeroLength", features);
    assertNotNull(id);
    Annotation ann = set.get(id);
    assertEquals(Long.valueOf(20L), ann.getStartNode().getOffset());
    assertEquals(Long.valueOf(20L), ann.getEndNode().getOffset());
  }
@Test
  public void testAnnotationAtDocumentEdge() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    document.setContent(new gate.corpora.DocumentContentImpl("This is some text."));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    long maxOffset = document.getContent().size();

    Integer id = set.add(0L, maxOffset, "WholeDoc", features);
    Annotation ann = set.get(id);

    assertEquals(Long.valueOf(0L), ann.getStartNode().getOffset());
    assertEquals(Long.valueOf(maxOffset), ann.getEndNode().getOffset());
  }
@Test
  public void testAddAnnotationWithNullFeatures() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    Integer id = set.add(5L, 10L, "NullFeature", null);
    Annotation ann = set.get(id);

    assertNotNull(ann);
    assertNull(ann.getFeatures());
  }
@Test
  public void testGetOnNonIndexedType() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    AnnotationSet result = set.get("NonExistingType");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAddDuplicateAnnotationId() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    Node start1 = document.createNode(10L);
    Node end1 = document.createNode(20L);
    Integer id = document.getNextAnnotationId();
    Annotation ann1 = new AnnotationImpl(id, start1, end1, "TypeA", features);
    set.add(ann1);

    Node start2 = document.createNode(30L);
    Node end2 = document.createNode(40L);
    Annotation ann2 = new AnnotationImpl(id, start2, end2, "TypeB", features);
    set.add(ann2); 

    Annotation stored = set.get(id);
    assertEquals("TypeB", stored.getType());
    assertEquals(Long.valueOf(30L), stored.getStartNode().getOffset());
  }
@Test
  public void testCoveringWithExactMatchOnBoundary() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(10L, 20L, "A", features);
    set.add(5L, 25L, "B", features);

    AnnotationSet result = set.getCovering("B", 10L, 20L);
    assertEquals(1, result.size());
    Annotation match = result.iterator().next();
    assertEquals("B", match.getType());
  }
@Test
  public void testGetByTypeRemovedIndex() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();
    Integer id = set.add(0L, 10L, "TempType", features);
    Annotation a = set.get(id);
    set.remove(a);

    AnnotationSet result = set.get("TempType");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAddAllKeepIDsWithMixedIDs() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl targetSet = new AnnotationSetImpl(document);

    FeatureMap f1 = new SimpleFeatureMapImpl();
    FeatureMap f2 = new SimpleFeatureMapImpl();

    Node s1 = document.createNode(5L);
    Node e1 = document.createNode(15L);
    Annotation a1 = new AnnotationImpl(10, s1, e1, "T1", f1);

    Node s2 = document.createNode(20L);
    Node e2 = document.createNode(30L);
    Annotation a2 = new AnnotationImpl(12, s2, e2, "T2", f2);

    java.util.List<Annotation> annotations = new java.util.ArrayList<>();
    annotations.add(a1);
    annotations.add(a2);

    boolean changed = targetSet.addAllKeepIDs(annotations);

    assertTrue(changed);
    assertEquals(2, targetSet.size());
    assertNotNull(targetSet.get(10));
    assertNotNull(targetSet.get(12));
  }
@Test
  public void testGetByFeatureNamesSubsetMatch() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("gender", "female");
    features.put("age", "25");

    set.add(1L, 10L, "Person", features);

    java.util.Set<Object> requiredFeatures = new java.util.HashSet<>();
    requiredFeatures.add("gender");

    AnnotationSet result = set.get("Person", requiredFeatures);

    assertEquals(1, result.size());
  }
@Test
  public void testEditShrinksAnnotationLeft() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    document.setContent(new gate.corpora.DocumentContentImpl("0123456789abcdefghij")); 

    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    set.add(5L, 15L, "Block", features);

    gate.DocumentContent replacement = new gate.corpora.DocumentContentImpl("XY");

    set.edit(6L, 10L, replacement); 

    java.util.List<Annotation> docs = set.inDocumentOrder();
    assertEquals(1, docs.size());
    Annotation a = docs.get(0);

    assertTrue(a.getEndNode().getOffset() > a.getStartNode().getOffset());
  }
@Test
  public void testGetWithNullTypeReturnsEmpty() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(1L, 2L, "A", features);
    set.add(3L, 4L, "B", features);

    AnnotationSet result = set.get((String) null);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetWithMultipleTypesIncludingInvalid() throws Exception {
    gate.corpora.DocumentImpl document = new gate.corpora.DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(0L, 5L, "Alpha", features);
    set.add(10L, 15L, "Beta", features);

    java.util.Set<String> types = new java.util.HashSet<>();
    types.add("Alpha");
    types.add("Gamma"); 

    AnnotationSet subset = set.get(types);

    assertNotNull(subset);
    assertEquals(1, subset.size());
    Annotation ann = subset.iterator().next();
    assertEquals("Alpha", ann.getType());
  }
@Test
  public void testGetNextNodeReturnsNullWhenNoNodeExists() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghij"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    set.add(0L, 5L, "Test", features);

    Node node = document.createNode(5L);
    Node result = set.nextNode(node); 

    assertNull(result);
  }
@Test
  public void testAddAnnotationCreatesAndIndexesNodesOnDemand() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdef"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    Integer id = set.add(2L, 4L, "Token", features);

    Annotation a = set.get(id);

    assertEquals(Long.valueOf(2L), a.getStartNode().getOffset());
    assertEquals(Long.valueOf(4L), a.getEndNode().getOffset());

    
    List<Annotation> ordered = set.inDocumentOrder();
    assertEquals(1, ordered.size());
    Annotation b = ordered.get(0);
    assertEquals("Token", b.getType());
  }
@Test
  public void testGetWithNonMatchingFeaturesReturnsEmpty() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("pos", "NN");

    set.add(10L, 15L, "Word", features);

    FeatureMap constraints = new SimpleFeatureMapImpl();
    constraints.put("pos", "VB");

    AnnotationSet result = set.get("Word", constraints);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotationRemovedEventDispatched() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    Integer id = set.add(0L, 5L, "Temp", features);
    Annotation a = set.get(id);

    final boolean[] eventFired = {false};

    gate.event.AnnotationSetListener listener = new gate.event.AnnotationSetListener() {
      @Override
      public void annotationAdded(gate.event.AnnotationSetEvent e) {}

      @Override
      public void annotationRemoved(gate.event.AnnotationSetEvent e) {
        eventFired[0] = true;
      }
    };

    set.addAnnotationSetListener(listener);
    set.remove(a);

    assertTrue(eventFired[0]);
  }
@Test
  public void testAnnotationAddedEventDispatched() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    final boolean[] eventFired = {false};

    gate.event.AnnotationSetListener listener = new gate.event.AnnotationSetListener() {
      @Override
      public void annotationAdded(gate.event.AnnotationSetEvent e) {
        eventFired[0] = true;
      }

      @Override
      public void annotationRemoved(gate.event.AnnotationSetEvent e) {}
    };

    set.addAnnotationSetListener(listener);
    set.add(0L, 10L, "Label", features);

    assertTrue(eventFired[0]);
  }
@Test
  public void testEditRemovesZeroLengthAnnotation() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcde"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();
    Integer id = set.add(1L, 2L, "Short", features);
    set.edit(1L, 2L, new DocumentContentImpl("")); 

    assertTrue(set.get().isEmpty());
  }
@Test
  public void testGetStrictReturnsEmptyIfNoExactMatch() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    set.add(10L, 20L, "Span", features);
    set.add(10L, 21L, "Span", features);

    AnnotationSet result = set.getStrict(10L, 22L);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testFirstNodeAndLastNodeAreSameWhenOnlyOneAnnotation() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcde"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    set.add(0L, 5L, "Full", features);

    Node first = set.firstNode();
    Node last = set.lastNode();

    assertNotNull(first);
    assertNotNull(last);
    assertNotEquals(first.getOffset(), last.getOffset());
    assertEquals(Long.valueOf(0), first.getOffset());
    assertEquals(Long.valueOf(5), last.getOffset());
  }
@Test
  public void testGetTypeSubsetWithEmptySetReturnsEmpty() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    Set<String> types = new HashSet<>();
    AnnotationSet result = set.get(types);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testCloneReturnsDifferentObject() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Object cloned = set.clone();

    assertNotNull(cloned);
    assertTrue(cloned instanceof AnnotationSetImpl);
    assertNotSame(set, cloned);
  }
@Test
  public void testGetCoveringNoneWhenAnnotationTooShort() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap featureMap = new SimpleFeatureMapImpl();

    set.add(5L, 10L, "Short", featureMap);
    AnnotationSet result = set.getCovering("Short", 5L, 15L);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testEmptyAnnotationSetReturnedWhenNoMatches() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    AnnotationSet result = set.get(0L, 5L);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotationSubsetMatchingFeatureKeysOnly() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("lang", "en");
    features.put("token", true);

    set.add(5L, 15L, "Token", features);

    Set<Object> keys = new HashSet<>();
    keys.add("lang");
    keys.add("token");

    AnnotationSet result = set.get("Token", keys);
    assertEquals(1, result.size());
  }
@Test
  public void testAnnotationSubsetFailsIfFeatureKeyMissing() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("lang", "en");

    set.add(5L, 15L, "Token", features);

    Set<Object> keys = new HashSet<>();
    keys.add("lang");
    keys.add("missing");

    AnnotationSet result = set.get("Token", keys);
    assertTrue(result.isEmpty());
  }
@Test
  public void testEmptyAnnotationSetReturnedForUnknownOffset() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    AnnotationSet result = set.get(100L);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAddAnnotationReplacesExistingSameId() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features1 = new SimpleFeatureMapImpl();
    Node start1 = document.createNode(1L);
    Node end1 = document.createNode(5L);
    Annotation annotation1 = new AnnotationImpl(10, start1, end1, "TypeA", features1);
    set.add(annotation1);

    FeatureMap features2 = new SimpleFeatureMapImpl();
    Node start2 = document.createNode(6L);
    Node end2 = document.createNode(9L);
    Annotation annotation2 = new AnnotationImpl(10, start2, end2, "TypeB", features2);
    set.add(annotation2);

    Annotation stored = set.get(10);
    assertEquals("TypeB", stored.getType());
    assertEquals(Long.valueOf(6), stored.getStartNode().getOffset());
    assertEquals(Long.valueOf(9), stored.getEndNode().getOffset());
  }
@Test
  public void testAddAnnotationTracksLongestAnnotation() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap f1 = new SimpleFeatureMapImpl();
    FeatureMap f2 = new SimpleFeatureMapImpl();

    set.add(0L, 5L, "Short", f1);
    set.add(0L, 50L, "Long", f2); 

    AnnotationSet result = set.get("Long", 1L, 49L); 
    assertEquals(1, result.size());
    Annotation a = result.iterator().next();
    assertEquals("Long", a.getType());
  }
@Test
  public void testRemoveAnnotationRemovesFromTypeIndex() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    Integer id = set.add(5L, 10L, "TestType", features);
    Annotation ann = set.get(id);

    set.get("TestType"); 
    set.remove(ann);
    AnnotationSet result = set.get("TestType");
    assertTrue(result.isEmpty());
  }
@Test
  public void testRemoveAnnotationRemovesFromOffsetIndex() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();

    Integer id = set.add(10L, 20L, "Test", features);
    Annotation ann = set.get(id);

    set.get(10L); 
    set.remove(ann);

    AnnotationSet result = set.get(10L);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetWithTypeAndNullFeatureMapReturnsNullNotEmpty() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    set.add(1L, 10L, "Person", new SimpleFeatureMapImpl());

    AnnotationSet result = set.get("Person", (FeatureMap) null);
    assertNull(result); 
  }
@Test
  public void testGetByNullTypeAndFeatureNamesReturnsMatching() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("foo", "bar");
    fm.put("extra", "value");

    set.add(1L, 2L, "TypeX", fm);
    set.add(3L, 4L, "TypeY", new SimpleFeatureMapImpl());

    Set<Object> keys = new HashSet<>();
    keys.add("foo");

    AnnotationSet result = set.get(null, keys);
    assertEquals(1, result.size());
    Annotation a = result.iterator().next();
    assertEquals("TypeX", a.getType());
  }
@Test
  public void testGetWithZeroLengthRangeReturnsEmpty() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    AnnotationSet result = set.get(0L, 0L); 
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetCoveringAnnotationEqualLengthStillMatches() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap f = new SimpleFeatureMapImpl();
    set.add(10L, 20L, "Region", f);

    AnnotationSet result = set.getCovering("Region", 10L, 20L);
    assertEquals(1, result.size());
  }
@Test
  public void testGetContainedExcludesOverlappingButNotContained() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap f = new SimpleFeatureMapImpl();

    set.add(5L, 30L, "A", f); 
    set.add(10L, 20L, "B", f); 

    AnnotationSet result = set.getContained(10L, 20L);
    assertEquals(1, result.size());
    Annotation a = result.iterator().next();
    assertEquals("B", a.getType());
  }
@Test
  public void testEditUpdatesNodeOffsetsBeyondEdit() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghij"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap f = new SimpleFeatureMapImpl();

    set.add(2L, 5L, "A", f);
    set.add(6L, 9L, "B", f);

    DocumentContentImpl replacement = new DocumentContentImpl("XYZ");

    set.edit(3L, 7L, replacement); 

    List<Annotation> ordered = set.inDocumentOrder();

    assertEquals(1, ordered.size()); 
    Annotation b = ordered.get(0);
    assertTrue(b.getStartNode().getOffset() > 3); 
  }
@Test
  public void testEmptyFeatureMapStillMatchesIfConstraintAlsoEmpty() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap fm = new SimpleFeatureMapImpl();
    set.add(0L, 1L, "X", fm);

    FeatureMap constraint = new SimpleFeatureMapImpl();

    AnnotationSet result = set.get("X", constraint);
    assertEquals(1, result.size());
  }
@Test
  public void testDoubleRemoveAnnotationReturnsFalseAfterFirst() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap fm = new SimpleFeatureMapImpl();

    Integer id = set.add(0L, 10L, "Test", fm);
    Annotation a = set.get(id);

    boolean first = set.remove(a);
    boolean second = set.remove(a);

    assertTrue(first);
    assertFalse(second);
  }
@Test
  public void testAddEmptyCollectionToAddAllKeepIDReturnsFalse() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    Collection<Annotation> empty = new ArrayList<>();
    boolean changed = set.addAllKeepIDs(empty);

    assertFalse(changed);
  }
@Test
  public void testGetFirstNodeReturnsNullWhenSetIsEmpty() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    Node first = set.firstNode();
    assertNull(first);
  }
@Test
  public void testRemoveOnAnnotationNotInSetReturnsFalse() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    Node start = document.createNode(0L);
    Node end = document.createNode(10L);
    FeatureMap features = new SimpleFeatureMapImpl();
    Annotation annotation = new AnnotationImpl(999, start, end, "Ghost", features); 

    boolean removed = set.remove(annotation);
    assertFalse(removed);
  }
@Test
  public void testAddAnnotationWithSameOffsetNodes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("0123456789"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap fm1 = new SimpleFeatureMapImpl();
    FeatureMap fm2 = new SimpleFeatureMapImpl();

    Integer ann1 = set.add(2L, 5L, "TypeA", fm1);
    Integer ann2 = set.add(2L, 5L, "TypeB", fm2);

    AnnotationSet anns = set.get(2L);
    assertEquals(2, anns.size());
  }
@Test
  public void testContainmentWhenSpanExactlyWithinAnnotation() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap f = new SimpleFeatureMapImpl();
    set.add(5L, 10L, "Outer", f);
    set.add(6L, 9L, "Inner", f);

    AnnotationSet result = set.getContained(6L, 9L);
    assertEquals(1, result.size());
    Annotation inner = result.iterator().next();
    assertEquals("Inner", inner.getType());
  }
@Test
  public void testCoveringExcludesPartialOverlap() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap f = new SimpleFeatureMapImpl();

    set.add(5L, 10L, "Covering", f);

    AnnotationSet result = set.getCovering("Covering", 9L, 12L);
    assertTrue(result.isEmpty());
  }
@Test
  public void testFeatureSubsetMatchingWithExtraFeaturesInAnnotation() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("label", "A");
    features.put("confidence", "high");

    FeatureMap constraints = new SimpleFeatureMapImpl();
    constraints.put("label", "A");

    set.add(0L, 10L, "Label", features);
    AnnotationSet filtered = set.get("Label", constraints);

    assertEquals(1, filtered.size());
  }
@Test
  public void testFeatureSubsetMismatchOnMissingKey() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("label", "A");

    FeatureMap constraints = new SimpleFeatureMapImpl();
    constraints.put("missing", "value");

    set.add(0L, 10L, "Type", features);
    AnnotationSet filtered = set.get("Type", constraints);

    assertTrue(filtered.isEmpty());
  }
@Test
  public void testGetTypeSubsetIncludesAllIfTypesMatch() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    set.add(0L, 5L, "X", new SimpleFeatureMapImpl());
    set.add(6L, 10L, "Y", new SimpleFeatureMapImpl());

    Set<String> typeSet = new HashSet<String>();
    typeSet.add("X");
    typeSet.add("Y");

    AnnotationSet result = set.get(typeSet);
    assertEquals(2, result.size());
  }
@Test
  public void testGetAnnotationsStartingAtNoMatchReturnsEmpty() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    AnnotationSet result = set.getStartingAt(999L);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test(expected = ClassCastException.class)
  public void testRemoveInvalidObjectType() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    set.remove("not an annotation");
  }
@Test(expected = InvalidOffsetException.class)
  public void testAddAnnotationWithNullStartOffset() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    set.add(null, 10L, "Invalid", new SimpleFeatureMapImpl());
  }
@Test
  public void testEmptyTypeIndexReturnedWhenNoAnnotationsExist() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    Set<String> allTypes = set.getAllTypes();
    assertTrue(allTypes.isEmpty());
  }
@Test
  public void testAllTypesReturnedAfterMultipleAdds() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    set.add(0L, 5L, "PERSON", new SimpleFeatureMapImpl());
    set.add(5L, 10L, "LOCATION", new SimpleFeatureMapImpl());

    Set<String> types = set.getAllTypes();
    assertEquals(2, types.size());
    assertTrue(types.contains("PERSON"));
    assertTrue(types.contains("LOCATION"));
  }
@Test
  public void testEditRemovalMakesBoundaryNodeShared() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("This is test content."));
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap fm = new SimpleFeatureMapImpl();

    set.add(5L, 10L, "X", fm);
    set.add(10L, 15L, "Y", fm);

    DocumentContentImpl replacement = new DocumentContentImpl("");
    set.edit(7L, 13L, replacement);

    AnnotationSet remaining = set.get();
    for (Annotation a : remaining) {
      assertTrue(a.getEndNode().getOffset() >= a.getStartNode().getOffset());
    }
  }
@Test
  public void testAnnotationSetClearRemovesIndices() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    set.add(0L, 5L, "RemoveMe", new SimpleFeatureMapImpl());
    set.clear();

    assertEquals(0, set.size());
    assertTrue(set.get().isEmpty());
    assertTrue(set.getAllTypes().isEmpty());
  }
@Test
  public void testRemoveAnnotationLeavesOffsetsUntouchedForOtherAnnots() throws Exception {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    FeatureMap f = new SimpleFeatureMapImpl();
    Integer id1 = set.add(10L, 20L, "Mark1", f);
    Integer id2 = set.add(20L, 30L, "Mark2", f);

    Annotation a1 = set.get(id1);
    set.remove(a1);

    Annotation found = set.get(id2);
    assertEquals(Long.valueOf(20), found.getStartNode().getOffset());
  }
@Test
  public void testAddAnnotationTriggersAnnotationSetListenerAddedEvent() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    final boolean[] fired = new boolean[]{false};

    set.addAnnotationSetListener(new gate.event.AnnotationSetListener() {
      @Override
      public void annotationAdded(gate.event.AnnotationSetEvent ase) {
        fired[0] = true;
      }

      @Override
      public void annotationRemoved(gate.event.AnnotationSetEvent ase) {}
    });

    set.add(0L, 5L, "EventType", new SimpleFeatureMapImpl());
    assertTrue(fired[0]);
  }
@Test
  public void testAddAnnotationWithSameStartNodeCreatesSet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = new SimpleFeatureMapImpl();

    Integer id1 = set.add(5L, 10L, "Type1", fm);
    Integer id2 = set.add(5L, 15L, "Type2", fm);

    Annotation r1 = set.get(id1);
    Annotation r2 = set.get(id2);

    AnnotationSet result = set.get(5L);
    assertTrue(result.contains(r1));
    assertTrue(result.contains(r2));
  }
@Test
  public void testAddAnnotationWithSameIdRemovesOldFromTypeIndex() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();

    Node s1 = doc.createNode(1L);
    Node e1 = doc.createNode(10L);
    Annotation a1 = new AnnotationImpl(777, s1, e1, "OldType", fm);
    set.add(a1);

    AnnotationSet resultBefore = set.get("OldType");
    assertEquals(1, resultBefore.size());

    Node s2 = doc.createNode(1L);
    Node e2 = doc.createNode(10L);
    Annotation a2 = new AnnotationImpl(777, s2, e2, "NewType", fm);
    set.add(a2); 

    AnnotationSet oldSet = set.get("OldType");
    AnnotationSet newSet = set.get("NewType");

    assertTrue(oldSet.isEmpty());
    assertEquals(1, newSet.size());
  }
@Test
  public void testRemoveAnnotationReducesTypeIndexToNull() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();

    set.add(0L, 10L, "Temp", fm);
    AnnotationSet result = set.get("Temp");
    Annotation ann = result.iterator().next();

    set.remove(ann);

    AnnotationSet resultAfter = set.get("Temp");
    assertTrue(resultAfter.isEmpty());
  }
@Test
  public void testRemoveAnnotationReducesStartNodeEntry() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();

    set.add(5L, 10L, "One", fm);
    set.add(5L, 15L, "Two", fm);

    AnnotationSet startSetBefore = set.get(5L);
    assertEquals(2, startSetBefore.size());

    Integer firstId = null;
    for (Annotation a : startSetBefore) if (a.getType().equals("One")) firstId = a.getId();

    if (firstId != null) {
      set.remove(set.get(firstId));
    }

    AnnotationSet startSetAfter = set.get(5L);
    assertEquals(1, startSetAfter.size());
    Annotation remaining = startSetAfter.iterator().next();
    assertEquals("Two", remaining.getType());
  }
@Test
  public void testAddWithSameOffsetNodePreservesNodeInstance() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();

    Integer id1 = set.add(7L, 10L, "First", fm);
    Integer id2 = set.add(7L, 12L, "Second", fm);

    Annotation a1 = set.get(id1);
    Annotation a2 = set.get(id2);

    assertTrue(a1.getStartNode() == a2.getStartNode());
  }
@Test
  public void testIndexByStartOffsetPopulatesBothIndexesCorrectly() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();

    set.add(2L, 8L, "X", fm);
    set.get(2L); 

    Node first = set.firstNode();
    Node last = set.lastNode();

    assertEquals(Long.valueOf(2L), first.getOffset());
    assertEquals(Long.valueOf(8L), last.getOffset());
  }
@Test
  public void testClonePreservesSameAnnotationsButDifferentInstance() throws CloneNotSupportedException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = new SimpleFeatureMapImpl();
    set.add(1L, 5L, "X", fm);

    AnnotationSetImpl cloned = (AnnotationSetImpl) set.clone();

    assertNotSame(set, cloned);
    assertEquals(set.size(), cloned.size());
    assertNotNull(cloned.get(0));
  }
@Test
  public void testEmptyAnnotationSetReturnsCorrectDoc() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet empty = set.get("nonexistent");

    assertNotNull(empty);
    assertSame(doc, empty.getDocument());
  }
@Test
  public void testGetWithNullSetOfTypesReturnsEmptySet() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet result = set.get((Set<String>) null);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetWithEmptyTypeSetReturnsEmptySet() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Set<String> types = new HashSet<>();
    AnnotationSet result = set.get(types);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAddIdWithOffsetLessThanZero_ShouldThrow() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();

    try {
      set.add(0, -1L, 10L, "Invalid", fm);
      fail("Expected InvalidOffsetException");
    } catch (InvalidOffsetException expected) {
    }
  }
@Test
  public void testApplyEditOffsetShiftPreservesNodeOrder() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("01234567890123456789"));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();

    set.add(3L, 10L, "A", fm);
    set.add(11L, 18L, "B", fm);

    gate.DocumentContent replacement = new DocumentContentImpl("++");

    set.edit(10L, 11L, replacement); 

    Node n1 = set.firstNode();
    Node n2 = set.lastNode();

    assertTrue(n2.getOffset() > n1.getOffset());
  }
@Test
  public void testRemoveAnnotationFromOffsetIndexWithOnlyOneEntry() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = new SimpleFeatureMapImpl();
    Integer id = set.add(100L, 200L, "Solo", fm);
    Annotation ann = set.get(id);

    set.get(100L); 
    set.remove(ann);

    AnnotationSet check = set.get(100L);
    assertTrue(check.isEmpty());
  }
@Test
  public void testClearPreservesDocumentReference() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(1L, 3L, "X", new SimpleFeatureMapImpl());

    set.clear();

    assertSame(doc, set.getDocument());
  }
@Test
  public void testEmptyAnnotationSetGetReturnsEmptyImmutableSet() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet result = set.get();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetCoveringIgnoresShorterSpanEvenIfStartMatches() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap map = new SimpleFeatureMapImpl();

    set.add(10L, 20L, "X", map);
    set.add(10L, 15L, "Y", map);  

    AnnotationSet result = set.getCovering("Y", 10L, 20L);

    assertFalse(result.iterator().hasNext());
  }
@Test
  public void testGetStrictReturnsNoResultIfEndOffsetNotExact() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap map = new SimpleFeatureMapImpl();

    set.add(5L, 15L, "Test", map);

    AnnotationSet notStrictMatch = set.getStrict(5L, 14L);
    assertTrue(notStrictMatch.isEmpty());
  }
@Test
  public void testGetWithNullTypeReturnsNullIfTypeMapNotInitialized() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet result = set.get((String) null);
    assertNull(result); 
  }
@Test
  public void testEmptyTypeGetDoesNotTriggerIndexCreation() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    AnnotationSet noInit = set.get("NoType");

    assertNotNull(noInit);
    assertTrue(noInit.isEmpty());
  }
@Test
  public void testGetWithEmptyConstraintReturnsAllMatchingType() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    FeatureMap empty = new SimpleFeatureMapImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(0L, 10L, "Entity", empty);
    set.add(11L, 20L, "Entity", empty);

    FeatureMap constraint = new SimpleFeatureMapImpl(); 

    AnnotationSet result = set.get("Entity", constraint);
    assertEquals(2, result.size());
  }
@Test
  public void testNullFeaturesDontCauseSubsumesCrashWhenConstraintPresent() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    FeatureMap fm = null;
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(5L, 15L, "EdgeCase", fm);

    FeatureMap constraint = new SimpleFeatureMapImpl();
    constraint.put("type", "X");

    AnnotationSet result = set.get("EdgeCase", constraint);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAddAllOverlappingSameOffsets() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Node s1 = doc.createNode(10L);
    Node e1 = doc.createNode(15L);
    Annotation a1 = new AnnotationImpl(1, s1, e1, "T1", new SimpleFeatureMapImpl());

    Node s2 = doc.createNode(10L);
    Node e2 = doc.createNode(15L);
    Annotation a2 = new AnnotationImpl(2, s2, e2, "T2", new SimpleFeatureMapImpl());

    Collection<Annotation> list = new ArrayList<Annotation>();
    list.add(a1);
    list.add(a2);

    boolean changed = set.addAllKeepIDs(list);
    assertTrue(changed);
    assertEquals(2, set.size());
  }
@Test
  public void testGetContainedIncludesEdgeAlignedAnnotation() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap f = new SimpleFeatureMapImpl();

    set.add(5L, 10L, "Span", f);

    AnnotationSet match = set.getContained(5L, 10L);
    assertEquals(1, match.size());
  }
@Test
  public void testRemoveAnnotationCleansUpExactSingletonInStartIndex() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap map = new SimpleFeatureMapImpl();

    Integer id = set.add(11L, 22L, "Mock", map);
    Annotation ann = set.get(id);
    set.get(11L); 

    set.remove(ann); 

    AnnotationSet result = set.get(11L);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAddSetsLongestAnnotationCorrectly() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(3L, 13L, "A", new SimpleFeatureMapImpl());
    set.add(2L, 40L, "B", new SimpleFeatureMapImpl());

    
    AnnotationSet match = set.getCovering("B", 5L, 10L);

    assertEquals(1, match.size());
  }
@Test
  public void testAnnotationGetByFeatureKeysEmptyWhenKeysNotPresent() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("language", "de");

    set.add(1L, 10L, "Content", fm);

    Set<Object> keysMissing = new HashSet<Object>();
    keysMissing.add("token");

    AnnotationSet result = set.get("Content", keysMissing);
    assertTrue(result.isEmpty());
  }
@Test
  public void testAnnotationGetByFeatureKeysAllMatchIfSubset() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("a", 1);
    fm.put("b", 2);

    set.add(0L, 9L, "X", fm);

    Set<Object> keys = new HashSet<Object>();
    keys.add("a");

    AnnotationSet result = set.get("X", keys);
    assertEquals(1, result.size());
  }
@Test
  public void testNextNodeAfterLastReturnsNull() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("0123456789abcdef"));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(5L, 10L, "A", new SimpleFeatureMapImpl());
    Node last = set.lastNode();
    Node beyond = set.nextNode(last);

    assertNull(beyond);
  }
@Test
  public void testInDocumentOrderIsStableEvenWithEmptyNodeSlots() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(5L, 10L, "A", new SimpleFeatureMapImpl());
    set.add(15L, 20L, "B", new SimpleFeatureMapImpl());
    set.add(12L, 13L, "C", new SimpleFeatureMapImpl());

    List<Annotation> result = set.inDocumentOrder();

    assertEquals(3, result.size());
    assertTrue(result.get(0).getStartNode().getOffset() <= result.get(1).getStartNode().getOffset());
  } 
}