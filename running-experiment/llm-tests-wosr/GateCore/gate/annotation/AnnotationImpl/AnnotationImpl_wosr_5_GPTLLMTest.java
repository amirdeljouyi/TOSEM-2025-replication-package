public class AnnotationImpl_wosr_5_GPTLLMTest { 

 @Test
  public void testGetId() {
    FeatureMap features = new FeatureMapImpl();
    Node startNode = new NodeImpl(0L);
    Node endNode = new NodeImpl(5L);
    AnnotationImpl annotation = new AnnotationImpl(1, startNode, endNode, "Person", features);
    assertEquals(Integer.valueOf(1), annotation.getId());
  }
@Test
  public void testGetType() {
    FeatureMap features = new FeatureMapImpl();
    Node startNode = new NodeImpl(10L);
    Node endNode = new NodeImpl(20L);
    AnnotationImpl annotation = new AnnotationImpl(2, startNode, endNode, "Organization", features);
    assertEquals("Organization", annotation.getType());
  }
@Test
  public void testGetStartNode() {
    FeatureMap features = new FeatureMapImpl();
    Node startNode = new NodeImpl(100L);
    Node endNode = new NodeImpl(200L);
    AnnotationImpl annotation = new AnnotationImpl(3, startNode, endNode, "Date", features);
    assertSame(startNode, annotation.getStartNode());
  }
@Test
  public void testGetEndNode() {
    FeatureMap features = new FeatureMapImpl();
    Node startNode = new NodeImpl(50L);
    Node endNode = new NodeImpl(75L);
    AnnotationImpl annotation = new AnnotationImpl(4, startNode, endNode, "Location", features);
    assertSame(endNode, annotation.getEndNode());
  }
@Test
  public void testToStringNotNull() {
    FeatureMap features = new FeatureMapImpl();
    features.put("key", "value");
    Node startNode = new NodeImpl(1L);
    Node endNode = new NodeImpl(2L);
    AnnotationImpl annotation = new AnnotationImpl(10, startNode, endNode, "Entity", features);
    String str = annotation.toString();
    assertTrue(str.contains("AnnotationImpl"));
    assertTrue(str.contains("id=10"));
    assertTrue(str.contains("type=Entity"));
    assertTrue(str.contains("start="));
    assertTrue(str.contains("end="));
  }
@Test
  public void testCompareToEqualId() {
    FeatureMap f = new FeatureMapImpl();
    AnnotationImpl a1 = new AnnotationImpl(100, new NodeImpl(5L), new NodeImpl(10L), "Token", f);
    AnnotationImpl a2 = new AnnotationImpl(100, new NodeImpl(5L), new NodeImpl(10L), "Token", f);
    assertEquals(0, a1.compareTo(a2));
  }
@Test
  public void testCompareToLessThan() {
    FeatureMap f = new FeatureMapImpl();
    AnnotationImpl a1 = new AnnotationImpl(10, new NodeImpl(5L), new NodeImpl(10L), "Token", f);
    AnnotationImpl a2 = new AnnotationImpl(20, new NodeImpl(5L), new NodeImpl(10L), "Token", f);
    assertTrue(a1.compareTo(a2) < 0);
  }
@Test
  public void testCompareToGreaterThan() {
    FeatureMap f = new FeatureMapImpl();
    AnnotationImpl a1 = new AnnotationImpl(30, new NodeImpl(1L), new NodeImpl(2L), "Name", f);
    AnnotationImpl a2 = new AnnotationImpl(15, new NodeImpl(1L), new NodeImpl(2L), "Name", f);
    assertTrue(a1.compareTo(a2) > 0);
  }
@Test
  public void testEqualsSameObject() {
    FeatureMap features = new FeatureMapImpl();
    features.put("a", 1);
    Node start = new NodeImpl(1L);
    Node end = new NodeImpl(5L);
    AnnotationImpl ann = new AnnotationImpl(123, start, end, "Label", features);
    assertTrue(ann.equals(ann));
  }
@Test
  public void testEqualsIdenticalFields() {
    FeatureMap features1 = new FeatureMapImpl();
    features1.put("k", "v");
    FeatureMap features2 = new FeatureMapImpl();
    features2.put("k", "v");
    Node start1 = new NodeImpl(100L);
    Node end1 = new NodeImpl(200L);
    Node start2 = new NodeImpl(100L);
    Node end2 = new NodeImpl(200L);

    AnnotationImpl a1 = new AnnotationImpl(5, start1, end1, "Entity", features1);
    AnnotationImpl a2 = new AnnotationImpl(5, start2, end2, "Entity", features2);

    assertTrue(a1.equals(a2));
  }
@Test
  public void testEqualsDifferentId() {
    FeatureMap f = new FeatureMapImpl();
    Node start = new NodeImpl(10L);
    Node end = new NodeImpl(20L);
    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Label", f);
    AnnotationImpl a2 = new AnnotationImpl(2, start, end, "Label", f);
    assertFalse(a1.equals(a2));
  }
@Test
  public void testEqualsDifferentStartOffset() {
    FeatureMap features = new FeatureMapImpl();
    Node start = new NodeImpl(1L);
    Node end = new NodeImpl(5L);
    Node startAlt = new NodeImpl(2L);
    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Type", features);
    AnnotationImpl a2 = new AnnotationImpl(1, startAlt, end, "Type", features);
    assertFalse(a1.equals(a2));
  }
@Test
  public void testEqualsDifferentType() {
    FeatureMap f = new FeatureMapImpl();
    Node s = new NodeImpl(0L);
    Node e = new NodeImpl(10L);
    AnnotationImpl a1 = new AnnotationImpl(77, s, e, "Person", f);
    AnnotationImpl a2 = new AnnotationImpl(77, s, e, "Entity", f);
    assertFalse(a1.equals(a2));
  }
@Test
  public void testCoextensiveTrue() {
    FeatureMap f = new FeatureMapImpl();
    Node aStart = new NodeImpl(2L);
    Node aEnd = new NodeImpl(5L);
    Node bStart = new NodeImpl(2L);
    Node bEnd = new NodeImpl(5L);
    AnnotationImpl base = new AnnotationImpl(10, aStart, aEnd, "A", f);
    AnnotationImpl candidate = new AnnotationImpl(20, bStart, bEnd, "B", f);
    assertTrue(base.coextensive(candidate));
  }
@Test
  public void testCoextensiveFalse() {
    FeatureMap f = new FeatureMapImpl();
    Node aStart = new NodeImpl(1L);
    Node aEnd = new NodeImpl(5L);
    Node bStart = new NodeImpl(1L);
    Node bEnd = new NodeImpl(6L);
    AnnotationImpl a1 = new AnnotationImpl(1, aStart, aEnd, "Entity", f);
    AnnotationImpl a2 = new AnnotationImpl(2, bStart, bEnd, "Entity", f);
    assertFalse(a1.coextensive(a2));
  }
@Test
  public void testOverlapsTrue() {
    FeatureMap f = new FeatureMapImpl();
    Node s1 = new NodeImpl(1L), e1 = new NodeImpl(5L);
    Node s2 = new NodeImpl(3L), e2 = new NodeImpl(10L);
    AnnotationImpl base = new AnnotationImpl(1, s1, e1, "A", f);
    AnnotationImpl overlap = new AnnotationImpl(2, s2, e2, "B", f);
    assertTrue(base.overlaps(overlap) || overlap.overlaps(base));
  }
@Test
  public void testOverlapsFalse() {
    FeatureMap f = new FeatureMapImpl();
    Node s1 = new NodeImpl(1L), e1 = new NodeImpl(5L);
    Node s2 = new NodeImpl(6L), e2 = new NodeImpl(8L);
    AnnotationImpl a = new AnnotationImpl(1, s1, e1, "A", f);
    AnnotationImpl b = new AnnotationImpl(2, s2, e2, "B", f);
    assertFalse(a.overlaps(b));
  }
@Test
  public void testWithinSpanTrue() {
    FeatureMap f = new FeatureMapImpl();
    Node outerStart = new NodeImpl(0L), outerEnd = new NodeImpl(10L);
    Node innerStart = new NodeImpl(2L), innerEnd = new NodeImpl(5L);
    AnnotationImpl outer = new AnnotationImpl(1, outerStart, outerEnd, "outer", f);
    AnnotationImpl inner = new AnnotationImpl(2, innerStart, innerEnd, "inner", f);
    assertTrue(inner.withinSpanOf(outer));
  }
@Test
  public void testWithinSpanFalse() {
    FeatureMap f = new FeatureMapImpl();
    Node outerStart = new NodeImpl(0L), outerEnd = new NodeImpl(3L);
    Node innerStart = new NodeImpl(2L), innerEnd = new NodeImpl(5L);
    AnnotationImpl outer = new AnnotationImpl(1, outerStart, outerEnd, "outer", f);
    AnnotationImpl inner = new AnnotationImpl(2, innerStart, innerEnd, "inner", f);
    assertFalse(inner.withinSpanOf(outer));
  }
@Test
  public void testIsCompatibleFeaturesSubsumed() {
    FeatureMap f1 = new FeatureMapImpl();
    FeatureMap f2 = new FeatureMapImpl();
    f1.put("x", 1);
    f2.put("x", 1);
    f2.put("y", 2);
    Node s = new NodeImpl(1L);
    Node e = new NodeImpl(8L);
    AnnotationImpl a = new AnnotationImpl(1, s, e, "T", f1);
    AnnotationImpl b = new AnnotationImpl(2, s, e, "T", f2);
    assertTrue(a.isCompatible(b));
  }
@Test
  public void testIsPartiallyCompatibleTrue() {
    FeatureMap f1 = new FeatureMapImpl();
    FeatureMap f2 = new FeatureMapImpl();
    f1.put("a", 100);
    f2.put("a", 100);
    Node s1 = new NodeImpl(4L), e1 = new NodeImpl(10L);
    Node s2 = new NodeImpl(7L), e2 = new NodeImpl(20L);
    AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "first", f1);
    AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "second", f2);
    assertTrue(a1.isPartiallyCompatible(a2));
  }
@Test
  public void testIsCompatibleWithFeatureSetTrue() {
    FeatureMap sourceMap = new FeatureMapImpl();
    sourceMap.put("key1", "value1");
    sourceMap.put("key2", "value2");

    FeatureMap targetMap = new FeatureMapImpl();
    targetMap.put("key1", "value1");
    targetMap.put("key2", "value2");
    targetMap.put("key3", "value3");

    Set<Object> featureKeys = new HashSet<>();
    featureKeys.add("key1");

    Node start = new NodeImpl(10L);
    Node end = new NodeImpl(20L);

    AnnotationImpl source = new AnnotationImpl(1, start, end, "classA", sourceMap);
    AnnotationImpl target = new AnnotationImpl(2, start, end, "classB", targetMap);

    assertTrue(source.isCompatible(target, featureKeys));
  }
@Test
  public void testSetFeaturesFiresEvents() {
    FeatureMap initial = new FeatureMapImpl();
    Node s = new NodeImpl(1L), e = new NodeImpl(5L);
    AnnotationImpl a = new AnnotationImpl(1, s, e, "T", initial);

    final boolean[] observed = {false};
    AnnotationListener listener = new AnnotationListener() {
      @Override
      public void annotationUpdated(AnnotationEvent e) {
        observed[0] = true;
        assertEquals(AnnotationEvent.FEATURES_UPDATED, e.getType());
        assertSame(a, e.getAnnotation());
      }
    };

    a.addAnnotationListener(listener);

    FeatureMap updated = new FeatureMapImpl();
    updated.put("key", "newValue");
    a.setFeatures(updated);

    assertTrue(observed[0]);
  }
@Test
  public void testAddRemoveAnnotationListener() {
    FeatureMap f = new FeatureMapImpl();
    Node s = new NodeImpl(1L), e = new NodeImpl(4L);
    AnnotationImpl ann = new AnnotationImpl(1, s, e, "Type", f);

    AnnotationListener listener = new AnnotationListener() {
      @Override
      public void annotationUpdated(AnnotationEvent e) {
        
      }
    };

    ann.addAnnotationListener(listener);
    ann.removeAnnotationListener(listener);
    assertNotNull(ann);
  }
@Test
  public void testHashCodeConsistencyWithEquals() {
    FeatureMap f1 = new FeatureMapImpl();
    Node s1 = new NodeImpl(1L);
    Node e1 = new NodeImpl(5L);

    FeatureMap f2 = new FeatureMapImpl();
    Node s2 = new NodeImpl(1L);
    Node e2 = new NodeImpl(5L);

    AnnotationImpl a1 = new AnnotationImpl(10, s1, e1, "Tag", f1);
    AnnotationImpl a2 = new AnnotationImpl(10, s2, e2, "Tag", f2);

    assertTrue(a1.equals(a2));
    assertEquals(a1.hashCode(), a2.hashCode());
  } 
}