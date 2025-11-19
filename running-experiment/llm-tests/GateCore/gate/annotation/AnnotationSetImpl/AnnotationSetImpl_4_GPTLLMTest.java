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

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnnotationSetImpl_4_GPTLLMTest {

 @Test
  public void testAddAnnotationIncrementsSizeCorrectly() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(0L, 5L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("class", "Token");

    Integer id = annotationSet.add(0L, 5L, "Token", features);
    Annotation annotation = annotationSet.get(id);

    assertNotNull(annotation);
    assertEquals("Token", annotation.getType());
    assertEquals(1, annotationSet.size());
  }
@Test(expected = InvalidOffsetException.class)
  public void testAddAnnotationThrowsForInvalidOffsets() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(10L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(10L, 5L)).thenReturn(false);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(10L, 5L, "Invalid", features);
  }
@Test
  public void testGetAnnotationByOffsetReturnsCorrectResult() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(50L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(5L, 15L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("category", "Noun");

    Integer id = annotationSet.add(5L, 15L, "Word", features);
    Annotation annotation = annotationSet.get(id);

    AnnotationSet setAtOffset5 = annotationSet.get(5L);

    assertEquals(1, setAtOffset5.size());
    assertEquals(annotation, setAtOffset5.iterator().next());
  }
@Test
  public void testGetByTypeAndFeatureReturnsOnlyMatching() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(0L, 5L)).thenReturn(true);
    when(doc.isValidOffsetRange(10L, 15L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap matchFeature = Factory.newFeatureMap();
    matchFeature.put("type", "Person");

    FeatureMap nonMatchFeature = Factory.newFeatureMap();
    nonMatchFeature.put("type", "Location");

    annotationSet.add(0L, 5L, "Entity", matchFeature);
    annotationSet.add(10L, 15L, "Entity", nonMatchFeature);

    FeatureMap query = Factory.newFeatureMap();
    query.put("type", "Person");

    AnnotationSet result = annotationSet.get("Entity", query);
    assertEquals(1, result.size());
    assertEquals("Person", result.iterator().next().getFeatures().get("type"));
  }
@Test
  public void testGetContainedAnnotationsBetweenOffsets() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2, 3);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(5L, 10L)).thenReturn(true);
    when(doc.isValidOffsetRange(12L, 18L)).thenReturn(true);
    when(doc.isValidOffsetRange(3L, 25L)).thenReturn(true);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("label", "A");

    FeatureMap features2 = Factory.newFeatureMap();
    features2.put("label", "B");

    FeatureMap features3 = Factory.newFeatureMap();
    features3.put("label", "C");

    annotationSet.add(5L, 10L, "Span", features);
    annotationSet.add(12L, 18L, "Span", features2);
    annotationSet.add(3L, 25L, "Span", features3);

    AnnotationSet containedSet = annotationSet.getContained(4L, 19L);
    assertEquals(2, containedSet.size());

    Set<Object> labels = Factory.newFeatureMap();
    labels.add("A");
    labels.add("B");

    boolean foundA = false;
    boolean foundB = false;

    for (Annotation a : containedSet) {
      String val = (String) a.getFeatures().get("label");
      if ("A".equals(val)) foundA = true;
      if ("B".equals(val)) foundB = true;
    }

    assertTrue(foundA);
    assertTrue(foundB);
  }
@Test
  public void testRemoveAnnotationNotPresentReturnsFalse() {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    Annotation annotation = mock(Annotation.class);
    when(annotation.getId()).thenReturn(123);
    boolean removed = annotationSet.remove(annotation);
    assertFalse(removed);
  }
@Test
  public void testAddAnnotationWithDuplicateIdOverwritesOld() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(10);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(0L, 10L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features1 = Factory.newFeatureMap();
    features1.put("type", "initial");
    annotationSet.add(0L, 10L, "Tag1", features1);

    
    Annotation annotation = mock(Annotation.class);
    when(annotation.getId()).thenReturn(10);
    when(annotation.getType()).thenReturn("OverrideTag");
    when(annotation.getFeatures()).thenReturn(Factory.newFeatureMap());

    annotationSet.add(annotation);

    Annotation fetched = annotationSet.get(10);
    assertEquals("OverrideTag", fetched.getType()); 
  }
@Test
  public void testAddZeroLengthAnnotation() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(2);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(50L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(5L, 5L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("meaning", "empty");

    Integer id = annotationSet.add(5L, 5L, "ZeroLength", features);
    Annotation a = annotationSet.get(id);
    assertNotNull(a);
    assertEquals(a.getStartNode(), a.getEndNode());
  }
@Test
  public void testGetStrictReturnsEmptyIfAnnotationsOverlapButNotStrict() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(3);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(200L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(1L, 10L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(1L, 10L, "TestType", features);

    AnnotationSet result = annotationSet.getStrict(0L, 10L);
    assertEquals(0, result.size());
  }
@Test
  public void testGetCoveringReturnsEmptyForInvalidSpan() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(4);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(300L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(0L, 100L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(5L, 15L, "Box", features);

    AnnotationSet result = annotationSet.getCovering("Box", 20L, 30L);
    assertEquals(0, result.size());
  }
@Test
  public void testGetContainedReturnsEmptyWhenAllAnnotationsOutsideRange() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(10L, 20L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(10L, 20L, "Sentence", features);

    AnnotationSet result = annotationSet.getContained(0L, 5L);
    assertEquals(0, result.size());
  }
@Test
  public void testGetFeatureNamesPartialMatchReturnsEmpty() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(200L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(50L, 60L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("attr1", "val1");
    features.put("attr2", "val2");
    annotationSet.add(50L, 60L, "Unit", features);

    Set<Object> requiredFeatures = Factory.newFeatureMap().keySet();
    requiredFeatures.add("attr1");
    requiredFeatures.add("missingFeature");

    AnnotationSet result = annotationSet.get("Unit", requiredFeatures);
    assertEquals(0, result.size());
  }
@Test
  public void testGetOnEmptySetReturnsEmptyAnnotationSet() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    AnnotationSet result = annotationSet.get();

    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testEditDeletesZeroLengthAnnotation() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(5L, 10L)).thenReturn(true);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(5L, 10L, "Modifiable", features);

    Gate.getUserConfig().put(GateConstants.DOCEDIT_INSERT_PREPEND, Boolean.TRUE);

    annotationSet.edit(5L, 10L, Factory.newDocumentContent(""));

    AnnotationSet result = annotationSet.get();
    assertEquals(0, result.size());
  }
@Test
  public void testAddAllOnEmptyCollectionReturnsFalse() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    boolean result = annotationSet.addAll(new java.util.ArrayList<Annotation>());
    assertFalse(result);
  }
@Test
  public void testGetByTypeReturnsEmptySetIfTypeDoesNotExist() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    annotationSet.add(0L, 10L, "ExistingType", features);

    AnnotationSet result = annotationSet.get("NonExistingType");
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetByMultipleTypesIncludesOnlyKnownTypes() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    annotationSet.add(0L, 5L, "TypeA", Factory.newFeatureMap());
    annotationSet.add(5L, 10L, "TypeB", Factory.newFeatureMap());

    Set<String> types = new HashSet<String>();
    types.add("TypeA");
    types.add("UnknownType");

    AnnotationSet result = annotationSet.get(types);
    assertEquals(1, result.size());
    assertEquals("TypeA", result.iterator().next().getType());
  }
@Test
  public void testNextNodeReturnsNullForLastNode() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(50L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    annotationSet.add(0L, 10L, "T", Factory.newFeatureMap());
    Node last = annotationSet.lastNode();

    Node next = annotationSet.nextNode(last);
    assertNull(next);
  }
@Test
  public void testGetWithFeatureConstraintAndOffsetReturnsEmptyIfNoMatch() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(60L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("type", "A");
    annotationSet.add(5L, 15L, "Label", features);

    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("type", "Different");

    AnnotationSet result = annotationSet.get("Label", constraint, 5L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAddAllKeepIDsAddsWithoutCloning() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);
    when(doc.getNextAnnotationId()).thenReturn(10, 20);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    AnnotationSetImpl source = new AnnotationSetImpl(doc);
    source.add(0L, 5L, "A", Factory.newFeatureMap());

    AnnotationSetImpl target = new AnnotationSetImpl(doc);
    target.addAllKeepIDs(source);

    assertEquals(1, target.size());
    Annotation a = target.get(10);
    assertEquals("A", a.getType());
  }
@Test
  public void testGetStrictZeroLengthAnnotation() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(eq(10L), eq(10L))).thenReturn(true);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(10L, 10L, "Z", Factory.newFeatureMap());

    AnnotationSet exact = annotationSet.getStrict(10L, 10L);
    assertEquals(1, exact.size());
    assertEquals("Z", exact.iterator().next().getType());
  }
@Test
  public void testFirstLastNodeReturnsNullIfSetIsEmpty() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);

    assertNull(annotationSet.firstNode());
    assertNull(annotationSet.lastNode());
  }
@Test
  public void testCloneReturnsEqualSizeAnnotationSet() throws InvalidOffsetException, CloneNotSupportedException {
    DocumentImpl doc = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(0L, 10L)).thenReturn(true);
    when(content.size()).thenReturn(20L);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(0L, 10L, "First", Factory.newFeatureMap());

    AnnotationSetImpl clonedSet = (AnnotationSetImpl) annotationSet.clone();
    assertEquals(annotationSet.size(), clonedSet.size());
  }
@Test
  public void testGetWithFullSpan() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(200L);
    when(doc.isValidOffsetRange(0L, 50L)).thenReturn(true);

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(0L, 50L, "Big", Factory.newFeatureMap());

    AnnotationSet result = annotationSet.get(0L, 50L);
    assertEquals(1, result.size());
    assertEquals("Big", result.iterator().next().getType());
  }
@Test
  public void testRemoveNullAnnotationSetListenerDoesNotThrow() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.removeAnnotationSetListener(null);
    assertTrue(true); 
  }
@Test
  public void testAddNullAnnotationSetListenerDoesNotThrowExceptionOnDuplicateAddRemove() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.addAnnotationSetListener(null);
    annotationSet.addAnnotationSetListener(null);
    annotationSet.removeAnnotationSetListener(null);
    assertTrue(true); 
  }
@Test
  public void testAddAnnotationUpdatesLongestAnnotation() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(1000L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(10L, 30L, "Short", Factory.newFeatureMap());
    as.add(5L, 100L, "Longer", Factory.newFeatureMap());

    AnnotationSet spanned = as.getCovering("Longer", 6L, 90L);
    assertEquals(1, spanned.size());
  }
@Test
  public void testGetWithNoMatchingStartNodesReturnsEmptySet() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(10L, 20L, "Type", Factory.newFeatureMap());

    AnnotationSet result = as.get(50L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testAddToStartOffsetIndexConvertsAnnotationToSet() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    
    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(10L, 20L, "A", Factory.newFeatureMap());
    as.add(10L, 30L, "B", Factory.newFeatureMap());

    AnnotationSet results = as.getStartingAt(10L);
    assertEquals(2, results.size());
  }
@Test
  public void testRemoveAnnotationRemovesTypeKeyIfEmpty() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    Integer id = as.add(0L, 5L, "XType", Factory.newFeatureMap());

    Annotation retrieved = as.get(id);
    as.remove(retrieved); 

    AnnotationSet typeFiltered = as.get("XType");
    assertNotNull(typeFiltered);
    assertEquals(0, typeFiltered.size());
  }
@Test
  public void testGetWithFeatureMapSubsetConstraintMatchesCorrectly() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(500L);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    features.put("alpha", "x");
    features.put("beta", "y");
    as.add(50L, 100L, "TypeCheck", features);

    FeatureMap query = Factory.newFeatureMap();
    query.put("alpha", "x");
    AnnotationSet result = as.get("TypeCheck", query);
    assertEquals(1, result.size());
  }
@Test
  public void testRemoveFromOffsetIndexCleansUpRedundantStartNodes() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2, 3);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    Integer id1 = as.add(10L, 20L, "Minor", Factory.newFeatureMap());
    Integer id2 = as.add(10L, 30L, "Major", Factory.newFeatureMap());

    Annotation a1 = as.get(id1);
    as.remove(a1); 

    AnnotationSet stillAtOffset = as.getStartingAt(10L);
    assertEquals(1, stillAtOffset.size());
  }
@Test
  public void testGetWithMaxOffsetEdgeValue() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(Long.MAX_VALUE);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    Long maxStart = Long.MAX_VALUE - 10;
    Long maxEnd = Long.MAX_VALUE - 1;
    as.add(maxStart, maxEnd, "Edge", Factory.newFeatureMap());

    AnnotationSet fetched = as.get(maxStart, maxEnd);
    assertEquals(1, fetched.size());
    assertEquals("Edge", fetched.iterator().next().getType());
  }
@Test
  public void testGetCoveringEmptyWhenAnnotationDoesNotFullySpanRange() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(100);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(200L);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(10L, 30L, "Cover", Factory.newFeatureMap());

    AnnotationSet result = as.getCovering("Cover", 5L, 35L); 
    assertEquals(0, result.size());
  }
@Test
  public void testGetStrictNoMatchWhenEndOffsetsSlightlyWrong() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl as = new AnnotationSetImpl(doc);
    as.add(40L, 50L, "Text", Factory.newFeatureMap());

    AnnotationSet notExact = as.getStrict(40L, 49L);
    assertEquals(0, notExact.size());
  }
@Test
  public void testGetEmptyTypeAfterClearDoesNotThrow() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    when(doc.getNextAnnotationId()).thenReturn(10);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 10L, "test", Factory.newFeatureMap());

    set.clear(); 
    AnnotationSet typeResult = set.get("test");

    assertNotNull(typeResult);
    assertEquals(0, typeResult.size());
  }
@Test
  public void testAddToStartOffsetIndexAnnotationReplacesCollectionDownsize() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Integer id1 = set.add(5L, 10L, "X", Factory.newFeatureMap());
    Integer id2 = set.add(5L, 10L, "Y", Factory.newFeatureMap());

    Annotation toRemove = set.get(id1);
    set.remove(toRemove); 

    AnnotationSet rem = set.getStartingAt(5L);
    assertEquals(1, rem.size()); 
  }
@Test
  public void testInDocumentOrderWithNoIndexReturnsEmptyList() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    List<Annotation> result = set.inDocumentOrder();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testEmptyAnnotationSetCloneNotSameInstance() throws CloneNotSupportedException {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Object clone = set.clone();
    assertNotNull(clone);
    assertNotSame(set, clone);
    assertTrue(clone instanceof AnnotationSetImpl);
  }
@Test
  public void testGetNextNodeReturnsNullNoGreaterKey() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    when(doc.getNextAnnotationId()).thenReturn(1);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(40L, 40L, "X", Factory.newFeatureMap());
    Node last = set.lastNode();
    Node next = set.nextNode(last);
    assertNull(next); 
  }
@Test
  public void testSerializationDeserializationCyclePreservesAnnotationSet() throws IOException, ClassNotFoundException, InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class, withSettings().serializable());
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl original = new AnnotationSetImpl(doc);
    original.add(10L, 20L, "Persist", Factory.newFeatureMap());

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(original);

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);
    Object deserialized = ois.readObject();

    assertTrue(deserialized instanceof AnnotationSetImpl);
    AnnotationSetImpl restored = (AnnotationSetImpl) deserialized;
    assertEquals(1, restored.size());
  }
@Test
  public void testEditWithSingleNodePreservesNodeNotDuplicating() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    when(doc.getNextAnnotationId()).thenReturn(1);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    set.add(15L, 20L, "Span", Factory.newFeatureMap());

    gate.Gate.getUserConfig().put(gate.GateConstants.DOCEDIT_INSERT_PREPEND, Boolean.TRUE);

    set.edit(17L, 17L, Factory.newDocumentContent("a"));

    assertEquals(1, set.size());
  }
@Test
  public void testGetAllTypesReturnsUnmodifiableSet() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 10L, "Alpha", Factory.newFeatureMap());

    Set<String> types = set.getAllTypes();
    assertEquals(1, types.size());
    assertEquals("Alpha", types.iterator().next());

    try {
      types.add("Beta");
      fail("Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
      
    }
  }
@Test
  public void testRemovingAnnotationAlsoCleansStartNodeMap() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(5L, 15L)).thenReturn(true);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    int id = set.add(5L, 15L, "DeleteTest", Factory.newFeatureMap());
    Annotation a = set.get(id);
    set.remove(a);

    AnnotationSet postRemoval = set.getStartingAt(5L);
    assertNotNull(postRemoval);
    assertTrue(postRemoval.isEmpty());
  }
@Test
  public void testGetByTypeAndEmptyFeatureMapReturnsAllOfType() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("key", "value");
    set.add(5L, 10L, "TypeA", fm1);
    set.add(15L, 20L, "TypeA", fm1);

    FeatureMap emptyFeatures = Factory.newFeatureMap();
    AnnotationSet result = set.get("TypeA", emptyFeatures);
    assertEquals(2, result.size());
  }
@Test
  public void testRemoveAllAnnotationsClearsIndices() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(5L, 15L, "Alpha", Factory.newFeatureMap());
    set.add(20L, 30L, "Alpha", Factory.newFeatureMap());

    for (Annotation ann : set) {
      set.remove(ann);
    }

    assertEquals(0, set.size());
    assertNull(set.firstNode());
    assertNull(set.lastNode());
  }
@Test
  public void testGetWithFeatureNamesAllMatch() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(5L, 15L)).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("a", "1");
    fm.put("b", "2");
    set.add(5L, 15L, "Tag", fm);

    Set<Object> keys = fm.keySet();
    AnnotationSet result = set.get("Tag", keys);
    assertEquals(1, result.size());
  }
@Test
  public void testGetWithFeatureNamesNoneMatchReturnsEmpty() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(50L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("k1", "v1");
    set.add(10L, 20L, "Node", fm);

    Set<Object> mismatchKeys = Collections.singleton("differentKey");
    AnnotationSet result = set.get("Node", mismatchKeys);
    assertEquals(0, result.size());
  }
@Test
  public void testGetContainedDoesNotIncludeTouchingAnnotations() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(4L, 10L, "A", Factory.newFeatureMap());  
    set.add(10L, 15L, "B", Factory.newFeatureMap()); 

    AnnotationSet result = set.getContained(10L, 20L);
    assertEquals(1, result.size());
    assertEquals("B", result.iterator().next().getType());
  }
@Test
  public void testAddAnnotationWithNullFeatureMapDoesNotThrow() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(42);
    when(doc.isValidOffsetRange(3L, 6L)).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(10L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Integer id = set.add(3L, 6L, "NF", null);
    Annotation a = set.get(id);
    assertNotNull(a);
    assertEquals("NF", a.getType());
    assertNotNull(a.getFeatures());
    assertTrue(a.getFeatures().isEmpty()); 
  }
@Test
  public void testDeserializationWithNullAnnotationsFallsBack() throws IOException, ClassNotFoundException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(output);

    ObjectInputValidation dummyDoc = () -> {};
    DocumentImpl mockDoc = mock(DocumentImpl.class, withSettings().serializable());
    AnnotationSetImpl inst = new AnnotationSetImpl(mockDoc);
    AnnotationSetImpl deser;

    ByteArrayInputStream input;
    ObjectInputStream ois;

    inst.annotations = null;
    oos.writeObject(inst);
    oos.flush();

    input = new ByteArrayInputStream(output.toByteArray());
    ois = new ObjectInputStream(input);
    deser = (AnnotationSetImpl) ois.readObject();

    assertNotNull(deser);
    assertEquals(0, deser.size());
  }
@Test
  public void testCloneIsEmptyIfOriginalEmpty() throws CloneNotSupportedException {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSetImpl cloned = (AnnotationSetImpl) set.clone();
    assertEquals(0, cloned.size());
  }
@Test
  public void testGetWithNullFeatureNamesReturnsEmptySet() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(2L, 7L)).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(10L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    features.put("foo", "bar");
    set.add(2L, 7L, "TypeX", features);

    AnnotationSet result = set.get("TypeX", null);
    assertNotNull(result); 
    assertEquals(0, result.size());
  }
@Test
  public void testIndexByStartOffsetWithEmptySetCreatesStableOffsets() {
    DocumentImpl doc = mock(DocumentImpl.class);
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.inDocumentOrder();
    assertEquals(0, set.size());
  }
@Test
  public void testGetContainedReturnsEmptyWhenNoNodesInRange() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(100L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 10L, "Block", Factory.newFeatureMap());

    AnnotationSet result = set.getContained(11L, 20L);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetCoveringReturnsEmptyWhenNoOverlapsExist() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    DocumentContent content = mock(DocumentContent.class);
    when(content.size()).thenReturn(500L);
    when(doc.getContent()).thenReturn(content);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(100L, 120L, "Span", Factory.newFeatureMap());

    AnnotationSet result = set.getCovering("Span", 0L, 50L);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetByTypeSetEmptyReturnsEmptySet() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(200L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(1L, 2L, "T1", Factory.newFeatureMap());
    Set<String> emptySet = new HashSet<String>();

    AnnotationSet result = set.get(emptySet);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testGetSpanningAnnotationAtExactMatchOffset() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(50L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(10L, 20L, "Alpha", Factory.newFeatureMap());

    AnnotationSet result = set.get(15L); 
    assertEquals(1, result.size());
  }
@Test
  public void testRemoveAnnotationMissingFromIndexDoesNotCrash() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(100);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(1000L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap f = Factory.newFeatureMap();
    Integer id = set.add(0L, 10L, "XTag", f);

    Annotation a = set.get(id);
    set.clear(); 
    boolean removed = set.remove(a);
    assertFalse(removed);
  }
@Test
  public void testGetAllTypesReturnsUpdatedTypeList() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(10);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 10L, "A", Factory.newFeatureMap());

    Set<String> types = set.getAllTypes();
    assertTrue(types.contains("A"));

    for (Annotation a : set) {
      set.remove(a);
    }

    Set<String> updatedTypes = set.getAllTypes();
    assertFalse(updatedTypes.contains("A"));
  }
@Test
  public void testAddToStartOffsetIndexHandlesSharedNodeObject() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(1, 2);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(100L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(10L, 20L, "T1", Factory.newFeatureMap());
    set.add(10L, 30L, "T2", Factory.newFeatureMap());

    AnnotationSet resultAt10 = set.getStartingAt(10L);
    assertEquals(2, resultAt10.size());
  }
@Test
  public void testGetWithNullTypeInFeatureNameModeReturnsAllByKey() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    when(doc.getNextAnnotationId()).thenReturn(55);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);

    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(content.size()).thenReturn(300L);

    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    features.put("note", "yes");
    set.add(1L, 2L, "Blank", features);

    Set<Object> keys = new HashSet<Object>();
    keys.add("note");

    AnnotationSet result = set.get(null, keys);
    assertEquals(1, result.size());
    assertEquals("Blank", result.iterator().next().getType());
  }
@Test
  public void testGetAfterAddAllKeepIDsIncludesAllImportedAnnotations() throws InvalidOffsetException {
    DocumentImpl doc = mock(DocumentImpl.class);
    DocumentContent content = mock(DocumentContent.class);
    when(doc.getContent()).thenReturn(content);
    when(doc.isValidOffsetRange(anyLong(), anyLong())).thenReturn(true);
    when(content.size()).thenReturn(400L);
    when(doc.getNextAnnotationId()).thenReturn(1, 2, 3);

    AnnotationSetImpl source = new AnnotationSetImpl(doc);
    source.add(10L, 20L, "CopyType", Factory.newFeatureMap());

    AnnotationSetImpl target = new AnnotationSetImpl(doc);
    target.addAllKeepIDs(source);

    AnnotationSet all = target.get();
    assertEquals(1, all.size());
    assertEquals("CopyType", all.iterator().next().getType());
  } 
}