public class AnnotationImpl_wosr_3_GPTLLMTest { 

 @Test
  public void testGetters() {
    Integer id = 1;
    Node start = new MockNode(10L);
    Node end = new MockNode(20L);
    String type = "Person";
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("key", "value");

    AnnotationImpl annotation = new AnnotationImpl(id, start, end, type, features);

    assertEquals(id, annotation.getId());
    assertEquals(start, annotation.getStartNode());
    assertEquals(end, annotation.getEndNode());
    assertEquals(type, annotation.getType());
    assertEquals(features, annotation.getFeatures());
  }
@Test
  public void testToStringFormat() {
    Integer id = 42;
    Node start = new MockNode(5L);
    Node end = new MockNode(10L);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("f1", "v1");

    AnnotationImpl annotation = new AnnotationImpl(id, start, end, "Entity", features);

    String expected = "AnnotationImpl: id=42; type=Entity; features={f1=v1}; start=" + start +
        "; end=" + end + System.getProperty("line.separator");

    assertEquals(expected, annotation.toString());
  }
@Test
  public void testHashCodeAndEquals() {
    Integer id = 100;
    Node start = new MockNode(0L);
    Node end = new MockNode(5L);
    String type = "Concept";
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("x", "y");

    AnnotationImpl a1 = new AnnotationImpl(id, start, end, type, features);
    AnnotationImpl a2 = new AnnotationImpl(id, new MockNode(0L), new MockNode(5L), type, new SimpleFeatureMapImpl());
    a2.getFeatures().put("x", "y");

    assertEquals(a1, a2);
    assertEquals(a1.hashCode(), a2.hashCode());
  }
@Test
  public void testEqualsReturnsFalseForNull() {
    AnnotationImpl a = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "TypeA", new SimpleFeatureMapImpl());
    assertFalse(a.equals(null));
  }
@Test
  public void testNotEqualDifferentType() {
    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "TypeA", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "TypeB", new SimpleFeatureMapImpl());
    assertNotEquals(a1, a2);
  }
@Test
  public void testIsCompatibleTrue() {
    FeatureMap fm1 = new SimpleFeatureMapImpl();
    fm1.put("a", "b");

    FeatureMap fm2 = new SimpleFeatureMapImpl();
    fm2.put("a", "b");
    fm2.put("extra", "value");

    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(5L), "Test", fm1);
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(0L), new MockNode(5L), "Test", fm2);

    assertTrue(a1.isCompatible(a2));
  }
@Test
  public void testIsCompatibleFalseDiffStart() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("a", "b");

    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(5L), "T", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(1L), new MockNode(5L), "T", fm);

    assertFalse(a1.isCompatible(a2));
  }
@Test
  public void testIsCompatibleWithNullAnnotation() {
    AnnotationImpl a = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "T", new SimpleFeatureMapImpl());
    assertFalse(a.isCompatible(null));
  }
@Test
  public void testIsCompatibleWithFeaturesSetTrue() {
    FeatureMap fm1 = new SimpleFeatureMapImpl();
    fm1.put("a", "x");
    fm1.put("b", "y");

    FeatureMap fm2 = new SimpleFeatureMapImpl();
    fm2.put("a", "x");
    fm2.put("b", "y");
    fm2.put("c", "z");

    Set<Object> keys = new HashSet<>();
    keys.add("a");

    AnnotationImpl a = new AnnotationImpl(1, new MockNode(0L), new MockNode(5L), "T", fm1);
    AnnotationImpl b = new AnnotationImpl(2, new MockNode(0L), new MockNode(5L), "T", fm2);

    assertTrue(a.isCompatible(b, keys));
  }
@Test
  public void testIsPartiallyCompatibleTrue() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("x", "y");

    AnnotationImpl outer = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "X", fm);
    AnnotationImpl inner = new AnnotationImpl(2, new MockNode(5L), new MockNode(15L), "X", fm);

    assertTrue(outer.isPartiallyCompatible(inner));
  }
@Test
  public void testIsPartiallyCompatibleFalseDueToNoOverlap() {
    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("x", "y");

    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(5L), "A", fm);
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(5L), new MockNode(10L), "A", fm);

    assertFalse(a1.isPartiallyCompatible(a2));
  }
@Test
  public void testCoextensiveTrue() {
    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(5L), "Type", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(0L), new MockNode(5L), "Type", new SimpleFeatureMapImpl());

    assertTrue(a1.coextensive(a2));
  }
@Test
  public void testCoextensiveFalseDifferentEnd() {
    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(5L), "Type", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(0L), new MockNode(6L), "Type", new SimpleFeatureMapImpl());

    assertFalse(a1.coextensive(a2));
  }
@Test
  public void testOverlapsTrue() {
    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(5L), new MockNode(15L), "T", new SimpleFeatureMapImpl());

    assertTrue(a1.overlaps(a2));
  }
@Test
  public void testOverlapsFalse() {
    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(5L), "A", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(5L), new MockNode(15L), "A", new SimpleFeatureMapImpl());

    assertFalse(a1.overlaps(a2));
  }
@Test
  public void testWithinSpanOfTrue() {
    AnnotationImpl inner = new AnnotationImpl(1, new MockNode(5L), new MockNode(10L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl outer = new AnnotationImpl(2, new MockNode(0L), new MockNode(15L), "T", new SimpleFeatureMapImpl());

    assertTrue(inner.withinSpanOf(outer));
  }
@Test
  public void testWithinSpanOfFalse() {
    AnnotationImpl a = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl b = new AnnotationImpl(2, new MockNode(5L), new MockNode(8L), "T", new SimpleFeatureMapImpl());

    assertFalse(a.withinSpanOf(b));
  }
@Test
  public void testCompareTo() {
    AnnotationImpl a1 = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "A", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, new MockNode(0L), new MockNode(10L), "A", new SimpleFeatureMapImpl());

    assertTrue(a1.compareTo(a2) < 0);
    assertTrue(a2.compareTo(a1) > 0);
    assertEquals(0, a1.compareTo(a1));
  }
@Test
  public void testAddAndRemoveAnnotationListener() {
    AnnotationImpl annot = new AnnotationImpl(1, new MockNode(0L), new MockNode(10L), "A", new SimpleFeatureMapImpl());
    AnnotationListener listener = new AnnotationListener() {
      @Override
      public void annotationUpdated(AnnotationEvent e) {}
    };

    annot.addAnnotationListener(listener);
    annot.removeAnnotationListener(listener);
    
    
  } 
}