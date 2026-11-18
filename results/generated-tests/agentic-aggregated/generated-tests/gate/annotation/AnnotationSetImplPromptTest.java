package gate.annotation;

import static org.junit.Assert.*;

import gate.Annotation;
import gate.AnnotationSet;
import gate.FeatureMap;
import gate.Node;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.event.AnnotationSetEvent;
import gate.event.AnnotationSetListener;
import gate.event.GateEvent;
import gate.event.GateListener;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class AnnotationSetImplPromptTest {

  @Test
  public void constructorExposesDocumentNameAndEmptyViews() {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghij"));
    AnnotationSetImpl set = new AnnotationSetImpl(document, "analysis");

    assertSame(document, set.getDocument());
    assertEquals("analysis", set.getName());
    assertEquals(0, set.size());
    assertTrue(set.isEmpty());
    assertTrue(set.get().isEmpty());
    assertSame(document, set.get().getDocument());
    assertNull(set.firstNode());
    assertNull(set.lastNode());
    assertTrue(set.getAllTypes().isEmpty());
  }

  @Test
  public void addByOffsetsAssignsIdsAndSupportsIdLookup() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("0123456789"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("kind", "word");

    Integer firstId = set.add(1L, 4L, "Token", features);
    Integer secondId = set.add(4L, 7L, "Token", new SimpleFeatureMapImpl());

    assertEquals(Integer.valueOf(0), firstId);
    assertEquals(Integer.valueOf(1), secondId);
    assertEquals(2, set.size());
    assertEquals("Token", set.get(firstId).getType());
    assertEquals(Long.valueOf(1L), set.get(firstId).getStartNode().getOffset());
    assertEquals(Long.valueOf(4L), set.get(firstId).getEndNode().getOffset());
    assertEquals("word", set.get(firstId).getFeatures().get("kind"));
    assertSame(set.get(firstId).getEndNode(), set.get(secondId).getStartNode());
    assertNull(set.get(99));
  }

  @Test
  public void explicitIdAdvancesAutomaticIdAndZeroLengthUsesOneNode() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("0123456789"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    set.add(12, 2L, 5L, "Mention", new SimpleFeatureMapImpl());
    Integer nextId = set.add(6L, 6L, "Point", new SimpleFeatureMapImpl());

    assertEquals(Integer.valueOf(13), nextId);
    assertNotNull(set.get(12));
    assertSame(set.get(nextId).getStartNode(), set.get(nextId).getEndNode());
    assertEquals(2, set.size());
  }

  @Test
  public void addUsingExistingNodesCreatesAnnotationAtThoseNodes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("0123456789"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer baseId = set.add(2L, 8L, "Span", new SimpleFeatureMapImpl());
    Node start = set.get(baseId).getStartNode();
    Node end = set.get(baseId).getEndNode();

    Integer addedId = set.add(start, end, "DuplicateSpan", new SimpleFeatureMapImpl());

    assertSame(start, set.get(addedId).getStartNode());
    assertSame(end, set.get(addedId).getEndNode());
    assertEquals(2, set.getStartingAt(2L).size());
    assertEquals(1, set.get("DuplicateSpan").size());
  }

  @Test
  public void typeAndMultipleTypeQueriesReturnOnlyRequestedAnnotations() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnop"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer tokenOne = set.add(0L, 2L, "Token", new SimpleFeatureMapImpl());
    Integer person = set.add(3L, 7L, "Person", new SimpleFeatureMapImpl());
    Integer tokenTwo = set.add(8L, 10L, "Token", new SimpleFeatureMapImpl());
    set.add(11L, 15L, "Location", new SimpleFeatureMapImpl());
    Set<String> requested = new HashSet<String>(Arrays.asList("Token", "Person", "Missing"));

    AnnotationSet tokens = set.get("Token");
    AnnotationSet selected = set.get(requested);

    assertEquals(2, tokens.size());
    assertTrue(tokens.contains(set.get(tokenOne)));
    assertTrue(tokens.contains(set.get(tokenTwo)));
    assertEquals(3, selected.size());
    assertTrue(selected.contains(set.get(person)));
    assertTrue(set.get("Missing").isEmpty());
    assertEquals(new HashSet<String>(Arrays.asList("Token", "Person", "Location")), set.getAllTypes());
  }

  @Test
  public void featureConstraintQueryUsesSubsumption() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnop"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap rich = new SimpleFeatureMapImpl();
    rich.put("category", "NNP");
    rich.put("source", "gazetteer");
    FeatureMap other = new SimpleFeatureMapImpl();
    other.put("category", "NN");
    Integer matching = set.add(0L, 4L, "Token", rich);
    set.add(5L, 8L, "Token", other);
    set.add(9L, 12L, "Entity", rich);
    FeatureMap constraint = new SimpleFeatureMapImpl();
    constraint.put("category", "NNP");

    AnnotationSet result = set.get("Token", constraint);

    assertEquals(1, result.size());
    assertTrue(result.contains(set.get(matching)));
    constraint.put("missing", Boolean.TRUE);
    assertTrue(set.get("Token", constraint).isEmpty());
    assertTrue(set.get("Absent", new SimpleFeatureMapImpl()).isEmpty());
  }

  @Test
  public void featureNameQueryCanFilterOneTypeOrAllTypes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnop"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    FeatureMap both = new SimpleFeatureMapImpl();
    both.put("a", 1);
    both.put("b", 2);
    FeatureMap onlyA = new SimpleFeatureMapImpl();
    onlyA.put("a", 3);
    Integer tokenBoth = set.add(0L, 2L, "Token", both);
    set.add(3L, 5L, "Token", onlyA);
    Integer entityBoth = set.add(6L, 9L, "Entity", both);
    Set<Object> names = new HashSet<Object>(Arrays.asList("a", "b"));

    AnnotationSet typed = set.get("Token", names);
    AnnotationSet allTypes = set.get((String)null, names);

    assertEquals(1, typed.size());
    assertTrue(typed.contains(set.get(tokenBoth)));
    assertEquals(2, allTypes.size());
    assertTrue(allTypes.contains(set.get(entityBoth)));
    assertTrue(set.get("Missing", names).isEmpty());
  }

  @Test
  public void offsetQueriesDistinguishNextStartFromExactStart() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnop"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer first = set.add(1L, 4L, "A", new SimpleFeatureMapImpl());
    Integer second = set.add(6L, 9L, "B", new SimpleFeatureMapImpl());
    Integer third = set.add(6L, 11L, "C", new SimpleFeatureMapImpl());

    assertEquals(1, set.get(0L).size());
    assertTrue(set.get(0L).contains(set.get(first)));
    assertEquals(2, set.get(5L).size());
    assertTrue(set.get(5L).contains(set.get(second)));
    assertTrue(set.get(5L).contains(set.get(third)));
    assertTrue(set.getStartingAt(5L).isEmpty());
    assertEquals(2, set.getStartingAt(6L).size());
    assertTrue(set.get(12L).isEmpty());
  }

  @Test
  public void overlappingRangeHonorsBoundaryAndOptionalType() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnopqrst"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer crossesStart = set.add(1L, 6L, "Token", new SimpleFeatureMapImpl());
    set.add(1L, 5L, "Token", new SimpleFeatureMapImpl());
    Integer startsAt = set.add(5L, 7L, "Entity", new SimpleFeatureMapImpl());
    Integer inside = set.add(8L, 9L, "Token", new SimpleFeatureMapImpl());
    set.add(10L, 12L, "Token", new SimpleFeatureMapImpl());

    AnnotationSet all = set.get(5L, 10L);
    AnnotationSet tokens = set.get("Token", 5L, 10L);
    AnnotationSet blankType = set.get("   ", 5L, 10L);

    assertEquals(3, all.size());
    assertTrue(all.contains(set.get(crossesStart)));
    assertTrue(all.contains(set.get(startsAt)));
    assertTrue(all.contains(set.get(inside)));
    assertEquals(2, tokens.size());
    assertEquals(all, blankType);
  }

  @Test
  public void strictContainedAndCoveringQueriesApplyDifferentGeometry() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnopqrst"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer exactToken = set.add(3L, 8L, "Token", new SimpleFeatureMapImpl());
    Integer exactEntity = set.add(3L, 8L, "Entity", new SimpleFeatureMapImpl());
    Integer contained = set.add(4L, 7L, "Token", new SimpleFeatureMapImpl());
    Integer covering = set.add(1L, 10L, "Token", new SimpleFeatureMapImpl());
    set.add(2L, 6L, "Token", new SimpleFeatureMapImpl());

    AnnotationSet strict = set.getStrict(3L, 8L);
    AnnotationSet within = set.getContained(3L, 8L);
    AnnotationSet cover = set.getCovering("Token", 3L, 8L);

    assertEquals(2, strict.size());
    assertTrue(strict.contains(set.get(exactToken)));
    assertTrue(strict.contains(set.get(exactEntity)));
    assertEquals(3, within.size());
    assertTrue(within.contains(set.get(contained)));
    assertEquals(2, cover.size());
    assertTrue(cover.contains(set.get(exactToken)));
    assertTrue(cover.contains(set.get(covering)));
    assertTrue(set.getContained(9L, 2L).isEmpty());
    assertTrue(set.getCovering(null, 9L, 2L).isEmpty());
  }

  @Test
  public void firstLastNextAndDocumentOrderUseAllRelevantNodes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnopqrst"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer late = set.add(10L, 14L, "Late", new SimpleFeatureMapImpl());
    Integer early = set.add(1L, 3L, "Early", new SimpleFeatureMapImpl());
    Integer middle = set.add(5L, 18L, "Middle", new SimpleFeatureMapImpl());

    List<Annotation> ordered = set.inDocumentOrder();

    assertEquals(Long.valueOf(1L), set.firstNode().getOffset());
    assertEquals(Long.valueOf(18L), set.lastNode().getOffset());
    assertEquals(Long.valueOf(3L), set.nextNode(set.firstNode()).getOffset());
    assertEquals(Arrays.asList(set.get(early), set.get(middle), set.get(late)), ordered);
    assertNull(set.nextNode(set.lastNode()));
  }

  @Test
  public void removalAfterIndexingUpdatesTypeAndPositionQueries() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnopqrst"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer removedId = set.add(2L, 7L, "Token", new SimpleFeatureMapImpl());
    Integer survivorId = set.add(2L, 9L, "Entity", new SimpleFeatureMapImpl());
    Annotation removed = set.get(removedId);
    Annotation survivor = set.get(survivorId);
    assertEquals(1, set.get("Token").size());
    assertEquals(2, set.getStartingAt(2L).size());

    assertTrue(set.remove(removed));

    assertNull(set.get(removedId));
    assertTrue(set.get("Token").isEmpty());
    assertEquals(1, set.getStartingAt(2L).size());
    assertTrue(set.getStartingAt(2L).contains(survivor));
    assertFalse(set.remove(removed));
  }

  @Test
  public void iteratorRemovalUpdatesEveryIndex() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghij"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer id = set.add(2L, 6L, "Token", new SimpleFeatureMapImpl());
    set.get("Token");
    set.getStartingAt(2L);
    Iterator<Annotation> iterator = set.iterator();

    assertSame(set.get(id), iterator.next());
    iterator.remove();

    assertTrue(set.isEmpty());
    assertTrue(set.get("Token").isEmpty());
    assertTrue(set.getStartingAt(2L).isEmpty());
    assertNull(set.firstNode());
    assertNull(set.lastNode());
  }

  @Test
  public void clearFiresRemovalEventsAndResetsAllPublicViews() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijkl"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    set.add(1L, 3L, "A", new SimpleFeatureMapImpl());
    set.add(4L, 8L, "B", new SimpleFeatureMapImpl());
    set.get("A");
    set.getStartingAt(1L);
    final int[] removed = new int[] {0};
    set.addAnnotationSetListener(new AnnotationSetListener() {
      @Override public void annotationAdded(AnnotationSetEvent event) { fail("unexpected add"); }
      @Override public void annotationRemoved(AnnotationSetEvent event) { removed[0]++; }
    });

    set.clear();

    assertEquals(2, removed[0]);
    assertTrue(set.isEmpty());
    assertTrue(set.getAllTypes().isEmpty());
    assertNull(set.firstNode());
    assertNull(set.lastNode());
    assertTrue(set.inDocumentOrder().isEmpty());
  }

  @Test
  public void listenersReceiveAddsAndRemovalsAndCanBeDetached() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghij"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    final int[] annotationCounts = new int[] {0, 0};
    final int[] gateCount = new int[] {0};
    AnnotationSetListener annotationListener = new AnnotationSetListener() {
      @Override public void annotationAdded(AnnotationSetEvent event) { annotationCounts[0]++; }
      @Override public void annotationRemoved(AnnotationSetEvent event) { annotationCounts[1]++; }
    };
    GateListener gateListener = new GateListener() {
      @Override public void processGateEvent(GateEvent event) { gateCount[0]++; }
    };
    set.addAnnotationSetListener(annotationListener);
    set.addAnnotationSetListener(annotationListener);
    set.addGateListener(gateListener);
    set.addGateListener(gateListener);
    Integer id = set.add(1L, 4L, "Token", new SimpleFeatureMapImpl());
    set.remove(set.get(id));
    set.removeAnnotationSetListener(annotationListener);
    set.removeGateListener(gateListener);
    set.add(5L, 8L, "Token", new SimpleFeatureMapImpl());

    assertArrayEquals(new int[] {1, 1}, annotationCounts);
    assertEquals(1, gateCount[0]);
  }

  @Test
  public void addAllCopiesAnnotationsWithFreshIdsAndPreservesData() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnopqrst"));
    AnnotationSetImpl source = new AnnotationSetImpl(document);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("score", 7);
    Integer sourceOne = source.add(1L, 4L, "Token", features);
    source.add(6L, 9L, "Entity", new SimpleFeatureMapImpl());
    AnnotationSetImpl target = new AnnotationSetImpl(document);

    assertTrue(target.addAll(source));

    assertEquals(2, target.size());
    assertEquals(1, target.get("Token").size());
    Annotation copied = target.get("Token").iterator().next();
    assertNotEquals(sourceOne, copied.getId());
    assertEquals(Long.valueOf(1L), copied.getStartNode().getOffset());
    assertEquals(Long.valueOf(4L), copied.getEndNode().getOffset());
    assertEquals(Integer.valueOf(7), copied.getFeatures().get("score"));
    assertFalse(target.addAll(Collections.<Annotation>emptyList()));
  }

  @Test
  public void addingSameObjectAgainDoesNotReportChangeButReplacementRepairsIndexes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijklmnopqrst"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    Integer id = set.add(1L, 4L, "Old", new SimpleFeatureMapImpl());
    Annotation original = set.get(id);
    set.get("Old");
    set.getStartingAt(1L);
    Annotation replacement = new AnnotationImpl(id, new NodeImpl(80, 7L),
        new NodeImpl(81, 11L), "New", new SimpleFeatureMapImpl());

    assertFalse(set.add(original));
    assertTrue(set.add(replacement));

    assertSame(replacement, set.get(id));
    assertTrue(set.get("Old").isEmpty());
    assertEquals(1, set.get("New").size());
    assertTrue(set.getStartingAt(1L).isEmpty());
    assertTrue(set.getStartingAt(7L).contains(replacement));
  }

  @Test
  public void copyConstructorAndCloneRetainAnnotationsButHaveIndependentMembership() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghijkl"));
    AnnotationSetImpl original = new AnnotationSetImpl(document, "named");
    Integer id = original.add(2L, 6L, "Token", new SimpleFeatureMapImpl());
    original.get("Token");
    original.getStartingAt(2L);
    AnnotationSetImpl copy = new AnnotationSetImpl(original);
    AnnotationSetImpl clone = (AnnotationSetImpl)original.clone();

    assertEquals(original, copy);
    assertEquals("named", copy.getName());
    assertSame(document, copy.getDocument());
    assertSame(original.get(id), copy.get(id));
    assertEquals(original, clone);
    assertTrue(copy.remove(copy.get(id)));
    assertEquals(1, original.size());
    assertTrue(copy.isEmpty());
  }

  @Test
  public void invalidOffsetsAreRejectedWithoutChangingSet() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcde"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);

    assertThrows(InvalidOffsetException.class,
        () -> set.add(-1L, 2L, "Bad", new SimpleFeatureMapImpl()));
    assertThrows(InvalidOffsetException.class,
        () -> set.add(4L, 3L, "Bad", new SimpleFeatureMapImpl()));
    assertThrows(InvalidOffsetException.class,
        () -> set.add(0L, 6L, "Bad", new SimpleFeatureMapImpl()));
    assertThrows(InvalidOffsetException.class,
        () -> set.add(5, 2L, 7L, "Bad", new SimpleFeatureMapImpl()));
    assertTrue(set.isEmpty());
  }

  @Test
  public void typeSetAndRelationSetViewsAreStableAndProtected() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghij"));
    AnnotationSetImpl set = new AnnotationSetImpl(document);
    set.add(1L, 3L, "Token", new SimpleFeatureMapImpl());
    Set<String> types = set.getAllTypes();

    assertThrows(UnsupportedOperationException.class, () -> types.remove("Token"));
    assertSame(set.getRelations(), set.getRelations());
    assertSame(set, set.getRelations().getAnnotationSet());
  }
}
