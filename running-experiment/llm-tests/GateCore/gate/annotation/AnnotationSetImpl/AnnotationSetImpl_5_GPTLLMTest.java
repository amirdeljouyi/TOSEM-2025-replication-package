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

public class AnnotationSetImpl_5_GPTLLMTest {

 @Test
  public void testAddAndRetrieveAnnotationById() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Hello GATE", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("type", "word");

    Integer id = set.add(0L, 5L, "Token", features);

    Annotation result = set.get(id);
    assertNotNull(result);
    assertEquals("Token", result.getType());
    assertEquals(Long.valueOf(0L), result.getStartNode().getOffset());
    assertEquals(Long.valueOf(5L), result.getEndNode().getOffset());
    assertEquals("word", result.getFeatures().get("type"));

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAndRemoveAnnotation() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Hello", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("pos", "NN");

    Integer id = set.add(0L, 5L, "Token", features);
    Annotation annot = set.get(id);
    boolean removed = set.remove(annot);

    assertTrue(removed);
    assertFalse(set.contains(annot));

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByType() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Hello World", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    fm1.put("tag", "NN");
    fm2.put("tag", "VB");

    set.add(0L, 5L, "Person", fm1);
    set.add(6L, 11L, "Organization", fm2);

    AnnotationSet personSet = set.get("Person");
    AnnotationSet orgSet = set.get("Organization");

    assertEquals(1, personSet.size());
    assertEquals(1, orgSet.size());

    assertEquals("Person", personSet.iterator().next().getType());
    assertEquals("Organization", orgSet.iterator().next().getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByMultipleTypes() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Hello GATE NLP", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features1 = Factory.newFeatureMap();
    FeatureMap features2 = Factory.newFeatureMap();
    features1.put("sem", "greeting");
    features2.put("sem", "tool");

    set.add(0L, 5L, "Greeting", features1);
    set.add(6L, 10L, "Tool", features2);

    Set<String> types = new HashSet<String>();
    types.add("Greeting");
    types.add("Tool");

    AnnotationSet result = set.get(types);
    assertEquals(2, result.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithFeatureConstraints() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Sample", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fullFeatureMap = Factory.newFeatureMap();
    fullFeatureMap.put("cat", "noun");
    fullFeatureMap.put("number", "sg");

    FeatureMap otherFeatureMap = Factory.newFeatureMap();
    otherFeatureMap.put("cat", "verb");

    set.add(0L, 3L, "Word", fullFeatureMap);
    set.add(4L, 6L, "Word", otherFeatureMap);

    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("cat", "noun");

    AnnotationSet filtered = set.get("Word", constraint);
    assertEquals(1, filtered.size());
    assertEquals("noun", filtered.iterator().next().getFeatures().get("cat"));

    Factory.deleteResource(doc);
  }
@Test
  public void testInDocumentOrderReturnsSortedAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("ABC DEF GHI", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f1 = Factory.newFeatureMap();
    FeatureMap f2 = Factory.newFeatureMap();
    FeatureMap f3 = Factory.newFeatureMap();

    set.add(4L, 7L, "Word", f1);
    set.add(0L, 3L, "Word", f2);
    set.add(8L, 11L, "Word", f3);

    List<Annotation> ordered = set.inDocumentOrder();

    assertEquals(3, ordered.size());
    assertEquals(Long.valueOf(0L), ordered.get(0).getStartNode().getOffset());
    assertEquals(Long.valueOf(4L), ordered.get(1).getStartNode().getOffset());
    assertEquals(Long.valueOf(8L), ordered.get(2).getStartNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetStrictMatch() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Unit Test", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("case", "upper");

    set.add(0L, 4L, "Word", fm);
    set.add(5L, 9L, "Word", fm);
    set.add(0L, 9L, "Word", fm);

    AnnotationSet strictMatch = set.getStrict(0L, 9L);

    assertEquals(1, strictMatch.size());
    assertEquals(Long.valueOf(0L), strictMatch.iterator().next().getStartNode().getOffset());
    assertEquals(Long.valueOf(9L), strictMatch.iterator().next().getEndNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetContainedAnnotationsOnly() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("ABCDE", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    FeatureMap fm3 = Factory.newFeatureMap();

    set.add(0L, 10L, "Span", fm1);
    set.add(2L, 4L, "Token", fm2);
    set.add(5L, 7L, "Token", fm3);

    AnnotationSet contained = set.getContained(2L, 4L);
    assertEquals(1, contained.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationInvalidOffsetThrows() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Short text", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap map = Factory.newFeatureMap();
    map.put("fail", true);

    boolean exceptionThrown = false;
    try {
      set.add(0L, 500L, "Invalid", map);
    } catch (InvalidOffsetException ex) {
      exceptionThrown = true;
    }

    assertTrue(exceptionThrown);
    Factory.deleteResource(doc);
  }
@Test
  public void testFirstAndLastNodeCorrectness() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcdef", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(1L, 3L, "X", fm);
    set.add(4L, 6L, "Y", fm);

    Long firstOffset = set.firstNode().getOffset();
    Long lastOffset = set.lastNode().getOffset();

    assertEquals(Long.valueOf(1L), firstOffset);
    assertEquals(Long.valueOf(6L), lastOffset);

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithEmptyAnnotationSetReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Just text", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet result = set.get();
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByTypeReturnsEmptyWhenTypeAbsent() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Text", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(0L, 4L, "RealType", fm);

    AnnotationSet result = set.get("NonExistentType");
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByTypeAndFeatureNamesNoMatch() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Text", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("existing", "yes");
    set.add(0L, 4L, "Type", fm);

    Set<Object> featureNames = new HashSet<Object>();
    featureNames.add("non_existing");

    AnnotationSet result = set.get("Type", featureNames);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetCoveringWithEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcd", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet result = set.getCovering("AnyType", 0L, 2L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetCoveredWithInvalidRangeReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcd", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet result = set.getCovering("Test", 4L, 2L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testNextNodeWhenNoFurtherNodeExists() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Hello", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    set.add(0L, 5L, "Word", features);

    gate.Node lastNode = set.lastNode();
    gate.Node next = set.nextNode(lastNode);

    assertNull(next);
    Factory.deleteResource(doc);
  }
@Test
  public void testRemoveNonExistingAnnotationReturnsFalse() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set1 = new AnnotationSetImpl(doc);
    AnnotationSetImpl set2 = new AnnotationSetImpl(doc);
    
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("dummy", true);
    Integer id = set1.add(0L, 2L, "X", fm);
    Annotation a = set1.get(id);

    boolean result = set2.remove(a);
    assertFalse(result);

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationWithSameIdReplacesPrevious() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcdef", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    fm1.put("v", "A");
    fm2.put("v", "B");

    Integer id1 = set.add(0L, 2L, "TypeA", fm1);
    Annotation original = set.get(id1);

    Annotation duplicate = Factory.newAnnotation(id1, original.getStartNode(), original.getEndNode(), "TypeB", fm2);
    boolean modified = set.add(duplicate);

    assertTrue(modified);
    Annotation updated = set.get(id1);
    assertEquals("TypeB", updated.getType());
    assertEquals("B", updated.getFeatures().get("v"));

    Factory.deleteResource(doc);
  }
@Test
  public void testGetStartingAtWithNoMatchReturnsEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Short", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(0L, 2L, "Alpha", fm);
    set.add(3L, 5L, "Beta", fm);

    AnnotationSet result = set.getStartingAt(10L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithOffsetBeyondEnd() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("12345", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(0L, 3L, "Num", fm);

    AnnotationSet result = set.get(500L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAnnotationZeroLengthNotRemovedUntilEdit() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("123456", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    Integer id = set.add(2L, 2L, "Zero", fm);
    Annotation a = set.get(id);

    assertEquals(Long.valueOf(2L), a.getStartNode().getOffset());
    assertEquals(Long.valueOf(2L), a.getEndNode().getOffset());
    assertTrue(set.contains(a));

    Factory.deleteResource(doc);
  }
@Test
  public void testFeatureNameMatchPartialNotIncluded() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Length test", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("tag", "X");
    fm.put("name", "test");

    set.add(0L, 5L, "Type", fm);

    Set<Object> names = new HashSet<Object>();
    names.add("name");

    AnnotationSet result = set.get("Type", names);
    assertEquals(1, result.size());

    Set<Object> unmatched = new HashSet<Object>();
    unmatched.add("name");
    unmatched.add("missing");

    AnnotationSet emptyResult = set.get("Type", unmatched);
    assertTrue(emptyResult.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByTypeWithNullTypeReturnsEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("test doc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(0L, 4L, "TypeA", fm);

    AnnotationSet result = set.get((String) null);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationWhereStartEqualsEndCreatesValidAnnotation() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("123456", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("zero", true);

    Integer id = set.add(3L, 3L, "ZeroLength", fm);
    Annotation annot = set.get(id);

    assertNotNull(annot);
    assertEquals(Long.valueOf(3L), annot.getStartNode().getOffset());
    assertEquals(Long.valueOf(3L), annot.getEndNode().getOffset());
    assertEquals("ZeroLength", annot.getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithNullTypeAndNoFeatureNamesReturnsAll() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("f1", "v1");
    FeatureMap fm2 = Factory.newFeatureMap();
    fm2.put("f2", "v2");

    set.add(0L, 1L, "TypeX", fm1);
    set.add(2L, 3L, "TypeY", fm2);

    AnnotationSet result = set.get(null, Collections.singleton("f1"));
    assertEquals(1, result.size());
    assertEquals("TypeX", result.iterator().next().getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetContainedWhenStartEqualsEndOffset() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("edge", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(0L, 4L, "Full", fm);
    set.add(2L, 2L, "Zero", fm);

    AnnotationSet result = set.getContained(2L, 2L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationThenAddByIdWithLesserIdIncrementsSequence() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f1 = Factory.newFeatureMap();
    f1.put("v", 1);
    FeatureMap f2 = Factory.newFeatureMap();
    f2.put("v", 999);

    Integer id = set.add(0L, 2L, "T", f1);
    set.add(id - 1, 0L, 1L, "T2", f2); 

    Annotation smallerIdAnnot = set.get(id - 1);
    assertNotNull(smallerIdAnnot);
    assertEquals("T2", smallerIdAnnot.getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testRemoveAnnotationThatDoesNotExistFromEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("xyz", 0L));
    AnnotationSetImpl set1 = new AnnotationSetImpl(doc);
    AnnotationSetImpl set2 = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    Integer id = set1.add(0L, 1L, "X", fm);
    Annotation a = set1.get(id);

    boolean result = set2.remove(a);
    assertFalse(result);

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByOffsetReturnsClosestMatchingAnnotation() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcdef", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(2L, 3L, "Mid", fm);

    AnnotationSet result = set.get(1L);
    assertNotNull(result);
    assertFalse(result.isEmpty());
    Annotation a = result.iterator().next();
    assertEquals(Long.valueOf(2L), a.getStartNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetStrictWithNoMatchingEndOffsetReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(0L, 2L, "Type", fm);

    AnnotationSet result = set.getStrict(0L, 3L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testOnlyStartOffsetIndexCreatedWhenAccessedViaGetStartingAt() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcd", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(1L, 2L, "Alpha", fm);
    set.add(2L, 3L, "Beta", fm);

    AnnotationSet result1 = set.getStartingAt(1L);
    assertEquals(1, result1.size());
    assertEquals(Long.valueOf(1L), result1.iterator().next().getStartNode().getOffset());

    AnnotationSet result2 = set.getStartingAt(0L);
    assertTrue(result2.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAllWithEmptyCollectionDoesNotChangeSet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("input", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    boolean changed = set.addAll(Collections.<Annotation>emptyList());
    assertFalse(changed);
    assertEquals(0, set.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetAllTypesOnEmptySetReturnsEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("nothing here", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Set<String> types = set.getAllTypes();
    assertNotNull(types);
    assertTrue(types.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationAndGetWithOffsetInsideSpan() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("0123456789", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    Integer id = set.add(2L, 6L, "Inner", fm);

    AnnotationSet between = set.get(3L, 5L);
    assertEquals(1, between.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithSingleOffsetAtAnnotationStart() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcdef", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(1L, 3L, "X", fm);

    AnnotationSet result = set.get(1L);
    assertEquals(1, result.size());
    Annotation a = result.iterator().next();
    assertEquals("X", a.getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetAnnotationSetByTypeAndFeatureNamesWithExactFeatureKeyMatchOnly() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc def", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("matchedKey", "true");
    fm.put("extraKey", "123");

    set.add(0L, 3L, "W", fm);

    Set<Object> keys = new HashSet<Object>();
    keys.add("matchedKey");

    AnnotationSet result = set.get("W", keys);
    assertEquals(1, result.size());

    keys.clear();
    keys.add("unmatchedKey");
    AnnotationSet result2 = set.get("W", keys);
    assertTrue(result2.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testCloneShallowAnnotationSetImplViaConstructor() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("xyz", 0L));
    AnnotationSetImpl setA = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("a", 1);
    Integer id = setA.add(0L, 2L, "A", fm);

    AnnotationSetImpl cloned = new AnnotationSetImpl(setA);
    Annotation a = cloned.get(id);
    assertNotNull(a);
    assertEquals("A", a.getType());
    assertEquals(Long.valueOf(0L), a.getStartNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testNextNodeBeforeEndReturnsCorrectNextNode() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("xyzxyz", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 2L, "T", f);
    set.add(2L, 5L, "T2", f);

    Node first = set.firstNode();
    Node next = set.nextNode(first);
    assertNotNull(next);
    assertTrue(next.getOffset() > first.getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testFirstNodeAndLastNodeWithSingleAnnotation() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("1234", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 4L, "Unit", f);

    Node first = set.firstNode();
    Node last = set.lastNode();

    assertNotNull(first);
    assertNotNull(last);
    assertEquals(Long.valueOf(0L), first.getOffset());
    assertEquals(Long.valueOf(4L), last.getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetStrictWithMultipleAnnotationsOnlyOneMatches() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("0123456789", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(1L, 5L, "Alpha", f);
    set.add(2L, 5L, "Alpha", f);
    set.add(1L, 5L, "Beta", f);

    AnnotationSet strict = set.getStrict(2L, 5L);
    assertEquals(1, strict.size());
    Annotation result = strict.iterator().next();
    assertEquals(Long.valueOf(2L), result.getStartNode().getOffset());
    assertEquals(Long.valueOf(5L), result.getEndNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddSameAnnotationTwiceOnlyFirstTriggersAdd() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    Integer id = set.add(0L, 2L, "Same", f);
    Annotation a = set.get(id);

    boolean addedAgain = set.add(a); 

    assertFalse(addedAgain); 

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAllFromDifferentASInSameDocCreatesClonesWithNewIds() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("multi", 0L));
    AnnotationSetImpl set1 = new AnnotationSetImpl(doc);
    AnnotationSetImpl set2 = new AnnotationSetImpl(doc);

    FeatureMap f1 = Factory.newFeatureMap();
    f1.put("f", "x");
    set1.add(0L, 2L, "T1", f1);
    set1.add(3L, 5L, "T2", f1);

    boolean changed = set2.addAll(set1);
    assertTrue(changed);
    assertEquals(2, set2.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testRemoveAnnotationWithNullTypeIndex() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("ABC", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    Integer id = set.add(0L, 1L, "Char", features);
    Annotation annotation = set.get(id);

    boolean result = set.remove(annotation);
    assertTrue(result);
    Annotation still = set.get(id);
    assertNull(still);

    Factory.deleteResource(doc);
  }
@Test
  public void testGetCoveringReturnsEmptyIfAnnotationTooShort() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("ABCDEFGHIJ", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    set.add(2L, 4L, "Short", features);

    AnnotationSet result = set.getCovering("Short", 1L, 9L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testClearResetsAnnotationSetState() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("CLEAR_ME", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f1 = Factory.newFeatureMap();
    f1.put("status", "initial");

    set.add(0L, 3L, "A", f1);
    set.add(4L, 7L, "B", f1);

    assertEquals(2, set.size());
    set.clear();
    assertEquals(0, set.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByNullFeatureMapReturnsNull() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("TestDoc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet result = set.get("SomeType", (FeatureMap) null);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetCoveringWithNullTypeIncludesAll() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("0123456789", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(2L, 8L, "X", fm);

    AnnotationSet result = set.getCovering(null, 3L, 7L);
    assertEquals(1, result.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationWhereTypeIsNull() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    Integer id = set.add(0L, 1L, null, fm);
    Annotation a = set.get(id);

    assertNull(a.getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetSubMapOutsideAnnotationBoundsReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("offsets", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(1L, 3L, "X", fm);

    AnnotationSet result = set.get(5L, 6L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithTypeAndConstraintsOnEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("AAA", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("key", "value");

    AnnotationSet result = set.get("Alpha", fm);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByFeatureNameSubsetThatPartiallyMatches() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("123", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    f.put("x", "1");
    f.put("y", "2");
    set.add(0L, 1L, "Mixed", f);

    Set<Object> featureNames = new HashSet<Object>();
    featureNames.add("x");
    featureNames.add("z");

    AnnotationSet result = set.get("Mixed", featureNames);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddUsingStartEndNodesWithSameNode() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("aaaaa", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    Integer id1 = set.add(2L, 2L, "Zero", f);
    Integer id2 = set.add(2L, 2L, "Zero2", f);

    Annotation a1 = set.get(id1);
    Annotation a2 = set.get(id2);
    assertEquals(a1.getStartNode().getId(), a2.getStartNode().getId());
    assertEquals(a1.getEndNode().getId(), a2.getEndNode().getId());

    Factory.deleteResource(doc);
  }
@Test
  public void testRemoveFromEmptySetReturnsFalse() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("emptytest", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSetImpl other = new AnnotationSetImpl(doc);
    FeatureMap f = Factory.newFeatureMap();
    Integer id = other.add(0L, 3L, "X", f);
    Annotation external = other.get(id);

    boolean result = set.remove(external);
    assertFalse(result);

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithNullFeatureNamesReturnsAllAnnotationsOfType() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("fulltest", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f1 = Factory.newFeatureMap();
    f1.put("x", 1);
    FeatureMap f2 = Factory.newFeatureMap();
    f2.put("y", 2);

    set.add(0L, 2L, "T1", f1);
    set.add(3L, 5L, "T1", f2);

    AnnotationSet result = set.get("T1", null);
    assertEquals(2, result.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationWithInvalidOffsetReversed() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("xyz", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();

    boolean thrown = false;
    try {
      set.add(2L, 1L, "WrongOrder", fm); 
    } catch (InvalidOffsetException e) {
      thrown = true;
    }

    assertTrue(thrown);
    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationWithOffsetOutOfBoundsThrowsException() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("short", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();

    boolean thrown = false;
    try {
      set.add(0L, 1000L, "Big", fm);
    } catch (InvalidOffsetException e) {
      thrown = true;
    }

    assertTrue(thrown);
    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithNullTypeAndEmptyFeatureNamesReturnsFullSet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("f1", 123);
    set.add(0L, 2L, "T1", fm1);

    FeatureMap fm2 = Factory.newFeatureMap();
    fm2.put("f2", 456);
    set.add(2L, 3L, "T2", fm2);

    AnnotationSet result = set.get(null, Collections.<Object>emptySet());
    assertEquals(2, result.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetByOffsetWithGapInAnnotationsReturnsClosest() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("0123456789", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    set.add(0L, 2L, "A", fm);
    set.add(5L, 7L, "B", fm);

    AnnotationSet result = set.get(3L);
    assertFalse(result.isEmpty());
    Annotation found = result.iterator().next();
    assertEquals("B", found.getType());
    assertEquals(Long.valueOf(5L), found.getStartNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetCoveringIgnoresShortAnnotationsAndMatchesExactBounds() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcdefghij", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    set.add(1L, 4L, "Short", fm1);
    set.add(1L, 10L, "Long", fm2);
    set.add(0L, 10L, "All", fm2);

    AnnotationSet result = set.getCovering("Long", 2L, 6L);
    assertEquals(1, result.size());
    Annotation a = result.iterator().next();
    assertEquals("Long", a.getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetContainedWithNoOverlappingAnnotationsReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcd", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 2L, "X", f);

    AnnotationSet result = set.getContained(2L, 4L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testRemoveFromTypeIndexRemovesEmptyTypeEntry() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("testdata", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    Integer id = set.add(0L, 4L, "Alpha", f);
    set.get("Alpha"); 
    boolean result = set.remove(set.get(id));
    assertTrue(result);

    AnnotationSet typeSet = set.get("Alpha");
    assertTrue(typeSet.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAnnotationWithSingleOffsetAppearsInStartAndEndNodeMap() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("aaaa", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    Integer id = set.add(2L, 2L, "Zero", f);
    Annotation a = set.get(id);

    assertEquals(a.getStartNode().getOffset(), a.getEndNode().getOffset());
    assertEquals(Long.valueOf(2L), a.getStartNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationDoesNotFireEventIfNotDifferentInstance() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("event", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    f.put("data", true);
    Integer id = set.add(0L, 2L, "T", f);
    Annotation a = set.get(id);
    boolean result = set.add(a); 

    assertFalse(result); 

    Factory.deleteResource(doc);
  }
@Test
  public void testGetAnnotationAtExactOffsetReturnsEmptyIfNoneStartThere() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("aaa", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 1L, "A", f);
    set.add(1L, 2L, "B", f);

    AnnotationSet result = set.getStartingAt(2L); 
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithSetOfTypesIncludingNonExistingOnes() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("xyz", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 1L, "Real", f);

    Set<String> types = new HashSet<String>();
    types.add("Real");
    types.add("Fake");

    AnnotationSet result = set.get(types);
    assertEquals(1, result.size());
    Annotation a = result.iterator().next();
    assertEquals("Real", a.getType());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationWithSameNodesButDifferentTypeOverridesCorrectly() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abab", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f1 = Factory.newFeatureMap();
    f1.put("k", "v1");
    Integer id = set.add(1L, 2L, "A", f1);
    Annotation first = set.get(id);

    FeatureMap f2 = Factory.newFeatureMap();
    f2.put("k", "v2");
    Annotation updated = Factory.newAnnotation(id, first.getStartNode(), first.getEndNode(), "B", f2);

    boolean added = set.add(updated);
    assertTrue(added);
    Annotation result = set.get(id);
    assertEquals("B", result.getType());
    assertEquals("v2", result.getFeatures().get("k"));

    Factory.deleteResource(doc);
  }
@Test
  public void testEmptyAnnotationSetReturnedFromGetMethod() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("Hello", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet empty = set.get();
    assertNotNull(empty);
    assertTrue(empty.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetStrictWhenNoAnnotationStartsAtGivenOffset() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("x", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(1L, 2L, "A", f);

    AnnotationSet strict = set.getStrict(0L, 2L);
    assertNotNull(strict);
    assertTrue(strict.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetCoveringWithZeroLengthSpanReturnsNothing() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("ab", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 1L, "Type", f);

    AnnotationSet covering = set.getCovering("Type", 1L, 1L);
    assertNotNull(covering);
    assertTrue(covering.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithFeatureNamesSubsetMatchesExactOnly() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap features = Factory.newFeatureMap();
    features.put("a", "1");
    features.put("b", "2");
    set.add(0L, 1L, "Label", features);

    Set<Object> requiredKeys = new HashSet<Object>();
    requiredKeys.add("a");

    AnnotationSet result = set.get("Label", requiredKeys);
    assertNotNull(result);
    assertEquals(1, result.size());

    requiredKeys.add("missing");

    AnnotationSet empty = set.get("Label", requiredKeys);
    assertNotNull(empty);
    assertTrue(empty.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithStartOffsetGreaterThanEndOffsetReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcdef", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    AnnotationSet invalid = set.get(5L, 2L);
    assertNotNull(invalid);
    assertTrue(invalid.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAnnotationWithStartEqualsEndIsValid() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    Integer id = set.add(1L, 1L, "ZeroLen", f);
    Annotation result = set.get(id);

    assertNotNull(result);
    assertEquals(Long.valueOf(1L), result.getStartNode().getOffset());
    assertEquals(Long.valueOf(1L), result.getEndNode().getOffset());

    Factory.deleteResource(doc);
  }
@Test
  public void testAnnotationSetIteratorRemoveReflectsInSetSize() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    Integer id = set.add(0L, 3L, "Unit", f);
    Annotation annotation = set.get(id);

    AnnotationSet iteratorView = set.get();
    Annotation retrieved = iteratorView.iterator().next();

    set.remove(retrieved);
    assertNull(set.get(id));
    assertEquals(0, set.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testNextNodeOfLastNodeReturnsNull() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 1L, "A", f);
    set.add(1L, 2L, "B", f);

    Node last = set.lastNode();
    Node next = set.nextNode(last);

    assertNull(next);

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithOffsetGreaterThanAllStartOffsetsReturnsEmptySet() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("xyz", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 1L, "T1", f);
    set.add(1L, 2L, "T2", f);

    AnnotationSet result = set.get(5L);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetAllTypesContainsUniqueTypesOnly() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcde", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    set.add(0L, 2L, "TypeA", f);
    set.add(2L, 4L, "TypeB", f);
    set.add(3L, 5L, "TypeA", f);  

    Set<String> types = set.getAllTypes();
    assertTrue(types.contains("TypeA"));
    assertTrue(types.contains("TypeB"));
    assertEquals(2, types.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testGetWithSetOfOnlyNonexistentTypesReturnsEmpty() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("text", 0L));
    AnnotationSetImpl set = new AnnotationSetImpl(doc);

    Set<String> unknownTypes = new HashSet<String>();
    unknownTypes.add("Unknown1");
    unknownTypes.add("Unknown2");

    AnnotationSet result = set.get(unknownTypes);
    assertNotNull(result);
    assertTrue(result.isEmpty());

    Factory.deleteResource(doc);
  }
@Test
  public void testAddAllCreatesNewAnnotationWithNewIds() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abcdef", 0L));
    AnnotationSetImpl source = new AnnotationSetImpl(doc);
    AnnotationSetImpl target = new AnnotationSetImpl(doc);

    FeatureMap fm = Factory.newFeatureMap();
    fm.put("copy", true);

    source.add(0L, 2L, "Clone", fm);
    source.add(3L, 4L, "Clone", fm);

    boolean changed = target.addAll(source);
    assertTrue(changed);
    assertEquals(2, target.size());

    Factory.deleteResource(doc);
  }
@Test
  public void testCloneAnnotationSetImplViaConstructorWithNonDefaultImplementation() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(doc.getContent().insert("abc", 0L));
    AnnotationSetImpl original = new AnnotationSetImpl(doc);

    FeatureMap f = Factory.newFeatureMap();
    f.put("x", 1);
    original.add(0L, 1L, "A", f);

    AnnotationSet wrapped = original.get("A"); 
    AnnotationSetImpl clone = new AnnotationSetImpl(wrapped);

    assertEquals(1, clone.size());
    assertEquals("A", clone.iterator().next().getType());

    Factory.deleteResource(doc);
  } 
}