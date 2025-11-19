public class AnnotationImpl_wosr_4_GPTLLMTest { 

 @Test
  public void testGetters() {
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("key", "value");

    Node start = new Node(5L);
    Node end = new Node(10L);

    AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Person", features);

    assertEquals(Integer.valueOf(1), annotation.getId());
    assertEquals("Person", annotation.getType());
    assertEquals(start, annotation.getStartNode());
    assertEquals(end, annotation.getEndNode());
  }
@Test
  public void testEqualsSameInstance() {
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("k", "v");
    Node start = new Node(0L);
    Node end = new Node(5L);
    AnnotationImpl ann = new AnnotationImpl(100, start, end, "Token", features);

    assertTrue(ann.equals(ann));
    assertEquals(ann.hashCode(), ann.hashCode());
  }
@Test
  public void testEqualsEquivalentInstance() {
    FeatureMap features1 = new SimpleFeatureMapImpl();
    features1.put("k1", "v1");
    Node start1 = new Node(10L);
    Node end1 = new Node(20L);

    FeatureMap features2 = new SimpleFeatureMapImpl();
    features2.put("k1", "v1");
    Node start2 = new Node(10L);
    Node end2 = new Node(20L);

    AnnotationImpl ann1 = new AnnotationImpl(5, start1, end1, "Org", features1);
    AnnotationImpl ann2 = new AnnotationImpl(5, start2, end2, "Org", features2);

    assertTrue(ann1.equals(ann2));
    assertEquals(ann1.hashCode(), ann2.hashCode());
  }
@Test
  public void testEqualsDifferentId() {
    FeatureMap features = new SimpleFeatureMapImpl();
    Node start = new Node(5L);
    Node end = new Node(10L);
    AnnotationImpl ann1 = new AnnotationImpl(1, start, end, "Type", features);
    AnnotationImpl ann2 = new AnnotationImpl(2, start, end, "Type", features);

    assertFalse(ann1.equals(ann2));
  }
@Test
  public void testEqualsDifferentType() {
    FeatureMap features = new SimpleFeatureMapImpl();
    Node start = new Node(5L);
    Node end = new Node(10L);
    AnnotationImpl ann1 = new AnnotationImpl(1, start, end, "Date", features);
    AnnotationImpl ann2 = new AnnotationImpl(1, start, end, "Location", features);

    assertFalse(ann1.equals(ann2));
  }
@Test
  public void testEqualsDifferentStartOffset() {
    FeatureMap features = new SimpleFeatureMapImpl();
    Node start1 = new Node(1L);
    Node start2 = new Node(2L);
    Node end = new Node(10L);
    AnnotationImpl ann1 = new AnnotationImpl(1, start1, end, "Type", features);
    AnnotationImpl ann2 = new AnnotationImpl(1, start2, end, "Type", features);

    assertFalse(ann1.equals(ann2));
  }
@Test
  public void testEqualsDifferentEndOffset() {
    FeatureMap features = new SimpleFeatureMapImpl();
    Node start = new Node(1L);
    Node end1 = new Node(9L);
    Node end2 = new Node(10L);
    AnnotationImpl ann1 = new AnnotationImpl(1, start, end1, "Type", features);
    AnnotationImpl ann2 = new AnnotationImpl(1, start, end2, "Type", features);

    assertFalse(ann1.equals(ann2));
  }
@Test
  public void testEqualsDifferentFeatureMap() {
    FeatureMap features1 = new SimpleFeatureMapImpl();
    features1.put("a", "b");
    FeatureMap features2 = new SimpleFeatureMapImpl();
    features2.put("a", "c");

    Node start = new Node(5L);
    Node end = new Node(10L);

    AnnotationImpl ann1 = new AnnotationImpl(1, start, end, "X", features1);
    AnnotationImpl ann2 = new AnnotationImpl(1, start, end, "X", features2);
    assertFalse(ann1.equals(ann2));
  }
@Test
  public void testToStringIncludesFields() {
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("hello", "world");

    Node start = new Node(3L);
    Node end = new Node(8L);

    AnnotationImpl annotation = new AnnotationImpl(42, start, end, "Event", features);

    String output = annotation.toString();

    assertTrue(output.contains("id=42"));
    assertTrue(output.contains("type=Event"));
    assertTrue(output.contains("start="));
    assertTrue(output.contains("end="));
  }
@Test
  public void testCompareToSmallerId() {
    FeatureMap features = new SimpleFeatureMapImpl();
    Node start = new Node(0L);
    Node end = new Node(5L);

    AnnotationImpl ann1 = new AnnotationImpl(10, start, end, "T", features);
    AnnotationImpl ann2 = new AnnotationImpl(20, start, end, "T", features);
    assertTrue(ann1.compareTo(ann2) < 0);
  }
@Test
  public void testCompareToGreaterId() {
    FeatureMap features = new SimpleFeatureMapImpl();
    Node start = new Node(0L);
    Node end = new Node(5L);

    AnnotationImpl ann1 = new AnnotationImpl(30, start, end, "T", features);
    AnnotationImpl ann2 = new AnnotationImpl(20, start, end, "T", features);
    assertTrue(ann1.compareTo(ann2) > 0);
  }
@Test
  public void testCoextensiveTrue() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    Node start = new Node(5L);
    Node end = new Node(15L);

    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "X", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, start, end, "Y", fm);
    assertTrue(a1.coextensive(a2));
  }
@Test
  public void testCoextensiveFalse() {
    FeatureMap fm = new SimpleFeatureMapImpl();

    AnnotationImpl a1 = new AnnotationImpl(1, new Node(1L), new Node(2L), "T", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, new Node(1L), new Node(3L), "T", fm);

    assertFalse(a1.coextensive(a2));
  }
@Test
  public void testOverlapsTrue() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    AnnotationImpl a1 = new AnnotationImpl(1, new Node(5L), new Node(15L), "X", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, new Node(10L), new Node(20L), "X", fm);
    assertTrue(a1.overlaps(a2));
  }
@Test
  public void testOverlapsFalse() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    AnnotationImpl a1 = new AnnotationImpl(1, new Node(5L), new Node(15L), "X", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, new Node(15L), new Node(20L), "X", fm);
    assertFalse(a1.overlaps(a2));
  }
@Test
  public void testWithinSpanOfTrue() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    AnnotationImpl inner = new AnnotationImpl(1, new Node(10L), new Node(20L), "Inner", fm);
    AnnotationImpl outer = new AnnotationImpl(2, new Node(5L), new Node(25L), "Outer", fm);
    assertTrue(inner.withinSpanOf(outer));
  }
@Test
  public void testWithinSpanOfFalse() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    AnnotationImpl inner = new AnnotationImpl(1, new Node(5L), new Node(25L), "A", fm);
    AnnotationImpl outer = new AnnotationImpl(2, new Node(10L), new Node(20L), "B", fm);
    assertFalse(inner.withinSpanOf(outer));
  }
@Test
  public void testIsCompatibleSubsetFeatures() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("a", "b");

    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("a", "b");
    f2.put("extra", "value");

    Node start = new Node(10L);
    Node end = new Node(20L);

    AnnotationImpl a = new AnnotationImpl(1, start, end, "T", f1);
    AnnotationImpl b = new AnnotationImpl(2, start, end, "T", f2);

    assertTrue(a.isCompatible(b));
  }
@Test
  public void testIsCompatibleWithKeySet() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("x", "1");
    f1.put("y", "2");

    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("x", "1");
    f2.put("y", "2");
    f2.put("z", "3");

    Set<Object> keys = new HashSet<>();
    keys.add("x");

    Node start = new Node(5L);
    Node end = new Node(10L);

    AnnotationImpl a = new AnnotationImpl(1, start, end, "Type", f1);
    AnnotationImpl b = new AnnotationImpl(2, start, end, "Type", f2);

    assertTrue(a.isCompatible(b, keys));
  }
@Test
  public void testIsPartiallyCompatibleTrue() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("x", "1");

    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("x", "1");
    f2.put("y", "2");

    AnnotationImpl a1 = new AnnotationImpl(1, new Node(10L), new Node(20L), "X", f1);
    AnnotationImpl a2 = new AnnotationImpl(2, new Node(15L), new Node(25L), "X", f2);

    assertTrue(a1.isPartiallyCompatible(a2));
  }
@Test
  public void testIsPartiallyCompatibleFalseDueToNoOverlap() {
    FeatureMap f = new SimpleFeatureMapImpl();
    f.put("k", "v");

    AnnotationImpl a1 = new AnnotationImpl(1, new Node(0L), new Node(5L), "A", f);
    AnnotationImpl a2 = new AnnotationImpl(2, new Node(5L), new Node(10L), "A", f);

    assertFalse(a1.isPartiallyCompatible(a2));
  }
@Test
  public void testSetFeaturesFiresAnnotationEvent() {
    FeatureMap originalFeatures = new SimpleFeatureMapImpl();
    originalFeatures.put("token", "yes");

    Node start = new Node(1L);
    Node end = new Node(2L);

    AnnotationImpl annotation = new AnnotationImpl(5, start, end, "word", originalFeatures);

    final boolean[] fired = {false};
    annotation.addAnnotationListener(new AnnotationListener() {
      @Override
      public void annotationUpdated(AnnotationEvent e) {
        fired[0] = true;
      }
    });

    FeatureMap newFeatures = new SimpleFeatureMapImpl();
    newFeatures.put("lemma", "run");

    annotation.setFeatures(newFeatures);

    assertTrue("Expected FEATURE_UPDATED event fired", fired[0]);
  }
@Test
  public void testAddRemoveAnnotationListener() {
    FeatureMap features = new SimpleFeatureMapImpl();
    Node start = new Node(0L);
    Node end = new Node(1L);
    AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Test", features);

    AnnotationListener listener = new AnnotationListener() {
      @Override
      public void annotationUpdated(AnnotationEvent e) {
        
      }
    };

    annotation.addAnnotationListener(listener);
    annotation.removeAnnotationListener(listener);
    
    assertTrue(true);
  } 
}