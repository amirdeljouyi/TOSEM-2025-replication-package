public class AnnotationImpl_wosr_2_GPTLLMTest { 

 @Test
  public void testGetIdReturnsCorrectValue() {
    Node start = new SimpleNode(0L);
    Node end = new SimpleNode(10L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Token", features);

    assertEquals(Integer.valueOf(1), annotation.getId());
  }
@Test
  public void testGetTypeReturnsCorrectValue() {
    Node start = new SimpleNode(2L);
    Node end = new SimpleNode(5L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(5, start, end, "Entity", features);

    assertEquals("Entity", annotation.getType());
  }
@Test
  public void testGetStartNode() {
    Node start = new SimpleNode(3L);
    Node end = new SimpleNode(6L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(7, start, end, "Person", features);

    assertEquals(Long.valueOf(3L), annotation.getStartNode().getOffset());
  }
@Test
  public void testGetEndNode() {
    Node start = new SimpleNode(4L);
    Node end = new SimpleNode(9L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(22, start, end, "Person", features);

    assertEquals(Long.valueOf(9L), annotation.getEndNode().getOffset());
  }
@Test
  public void testToStringContainsAllDetails() {
    Node start = new SimpleNode(1L);
    Node end = new SimpleNode(2L);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("gender", "male");
    AnnotationImpl annotation = new AnnotationImpl(100, start, end, "Pronoun", features);

    String result = annotation.toString();
    assertTrue(result.contains("id=100"));
    assertTrue(result.contains("type=Pronoun"));
    assertTrue(result.contains("features"));
    assertTrue(result.contains("start"));
    assertTrue(result.contains("end"));
  }
@Test
  public void testEqualsSameAnnotation() {
    Node start = new SimpleNode(1L);
    Node end = new SimpleNode(5L);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("key", "value");

    AnnotationImpl a1 = new AnnotationImpl(99, start, end, "Test", features);
    AnnotationImpl a2 = new AnnotationImpl(99, start, end, "Test", features);

    assertTrue(a1.equals(a2));
  }
@Test
  public void testEqualsDifferentFeatures() {
    Node start = new SimpleNode(3L);
    Node end = new SimpleNode(8L);
    FeatureMap features1 = new SimpleFeatureMapImpl();
    features1.put("height", 180);
    FeatureMap features2 = new SimpleFeatureMapImpl();
    features2.put("height", 181);

    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Height", features1);
    AnnotationImpl a2 = new AnnotationImpl(1, start, end, "Height", features2);

    assertFalse(a1.equals(a2));
  }
@Test
  public void testCompareToWithSmallerId() {
    Node start = new SimpleNode(1L);
    Node end = new SimpleNode(2L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Small", features);
    AnnotationImpl a2 = new AnnotationImpl(2, start, end, "Large", features);

    assertTrue(a1.compareTo(a2) < 0);
  }
@Test
  public void testHashCodeConsistency() {
    Node start = new SimpleNode(1L);
    Node end = new SimpleNode(2L);
    FeatureMap features = new SimpleFeatureMapImpl();

    AnnotationImpl a1 = new AnnotationImpl(88, start, end, "Hash", features);
    AnnotationImpl a2 = new AnnotationImpl(88, start, end, "Hash", features);

    assertEquals(a1.hashCode(), a2.hashCode());
  }
@Test
  public void testIsCompatibleTrue() {
    Node start = new SimpleNode(1L);
    Node end = new SimpleNode(2L);

    FeatureMap fm1 = new SimpleFeatureMapImpl();
    fm1.put("k", "v");

    FeatureMap fm2 = new SimpleFeatureMapImpl();
    fm2.put("k", "v");
    fm2.put("extra", "ignored");

    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "X", fm1);
    AnnotationImpl a2 = new AnnotationImpl(1, start, end, "X", fm2);

    assertTrue(a1.isCompatible(a2));
  }
@Test
  public void testIsCompatibleFalseBecauseNotCoextensive() {
    Node start1 = new SimpleNode(1L);
    Node end1 = new SimpleNode(3L);
    Node start2 = new SimpleNode(2L);
    Node end2 = new SimpleNode(4L);

    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("x", 1);

    AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Tag", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "Tag", fm);

    assertFalse(a1.isCompatible(a2));
  }
@Test
  public void testIsCompatibleWithFeatureNameSubset() {
    Node start = new SimpleNode(5L);
    Node end = new SimpleNode(6L);

    FeatureMap fm1 = new SimpleFeatureMapImpl();
    fm1.put("a", "1");
    fm1.put("b", "2");

    FeatureMap fm2 = new SimpleFeatureMapImpl();
    fm2.put("a", "1");
    fm2.put("b", "2");
    fm2.put("c", "3");

    AnnotationImpl ann1 = new AnnotationImpl(10, start, end, "X", fm1);
    AnnotationImpl ann2 = new AnnotationImpl(10, start, end, "X", fm2);

    Set<Object> keys = new HashSet<>();
    keys.add("a");
    keys.add("b");

    assertTrue(ann1.isCompatible(ann2, keys));
  }
@Test
  public void testIsPartiallyCompatibleTrue() {
    Node start1 = new SimpleNode(5L);
    Node end1 = new SimpleNode(15L);
    Node start2 = new SimpleNode(10L);
    Node end2 = new SimpleNode(20L);

    FeatureMap fm1 = new SimpleFeatureMapImpl();
    fm1.put("x", "y");

    FeatureMap fm2 = new SimpleFeatureMapImpl();
    fm2.put("x", "y");
    fm2.put("z", "k");

    AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Ent", fm1);
    AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "Ent", fm2);

    assertTrue(a1.isPartiallyCompatible(a2));
  }
@Test
  public void testCoextensiveSameOffsets() {
    Node start = new SimpleNode(3L);
    Node end = new SimpleNode(9L);

    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("a", "b");

    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T", fm);

    assertTrue(a1.coextensive(a2));
  }
@Test
  public void testOverlapsTrue() {
    Node start1 = new SimpleNode(3L);
    Node end1 = new SimpleNode(10L);
    Node start2 = new SimpleNode(5L);
    Node end2 = new SimpleNode(12L);

    FeatureMap fm = new SimpleFeatureMapImpl();

    AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Overlap", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "Overlap", fm);

    assertTrue(a1.overlaps(a2));
  }
@Test
  public void testOverlapsFalseWhenNoIntersect() {
    Node start1 = new SimpleNode(1L);
    Node end1 = new SimpleNode(2L);
    Node start2 = new SimpleNode(3L);
    Node end2 = new SimpleNode(4L);

    FeatureMap fm = new SimpleFeatureMapImpl();

    AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "X", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "X", fm);

    assertFalse(a1.overlaps(a2));
  }
@Test
  public void testWithinSpanOfTrue() {
    Node outerStart = new SimpleNode(1L);
    Node outerEnd = new SimpleNode(10L);
    Node innerStart = new SimpleNode(2L);
    Node innerEnd = new SimpleNode(9L);

    FeatureMap fm = new SimpleFeatureMapImpl();

    AnnotationImpl outer = new AnnotationImpl(1, outerStart, outerEnd, "Outer", fm);
    AnnotationImpl inner = new AnnotationImpl(2, innerStart, innerEnd, "Inner", fm);

    assertTrue(inner.withinSpanOf(outer));
  }
@Test
  public void testWithinSpanOfFalse() {
    Node aStart = new SimpleNode(5L);
    Node aEnd = new SimpleNode(9L);
    Node bStart = new SimpleNode(6L);
    Node bEnd = new SimpleNode(8L);

    FeatureMap fm = new SimpleFeatureMapImpl();

    AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "A", fm);
    AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "B", fm);

    assertFalse(a.withinSpanOf(b));
  }
@Test
  public void testAddRemoveAnnotationListener() {
    Node start = new SimpleNode(1L);
    Node end = new SimpleNode(2L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, start, end, "T", features);

    AnnotationListener listener = new AnnotationListener() {
      @Override
      public void annotationUpdated(AnnotationEvent e) {
        
      }
    };

    annotation.addAnnotationListener(listener);
    annotation.removeAnnotationListener(listener);
    assertTrue(true); 
  }
@Test
  public void testSetFeaturesTriggersEventWhenListenerPresent() {
    Node start = new SimpleNode(1L);
    Node end = new SimpleNode(2L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Type", features);

    final boolean[] eventTriggered = {false};

    AnnotationListener listener = new AnnotationListener() {
      @Override
      public void annotationUpdated(AnnotationEvent e) {
        if (e.getType() == AnnotationEvent.FEATURES_UPDATED) {
          eventTriggered[0] = true;
        }
      }
    };

    annotation.addAnnotationListener(listener);

    FeatureMap newFeatures = new SimpleFeatureMapImpl();
    newFeatures.put("key", "val");

    annotation.setFeatures(newFeatures);

    assertTrue(eventTriggered[0]);
  } 
}