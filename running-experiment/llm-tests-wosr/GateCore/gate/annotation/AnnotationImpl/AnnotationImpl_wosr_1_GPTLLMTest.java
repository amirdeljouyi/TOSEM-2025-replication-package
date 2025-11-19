public class AnnotationImpl_wosr_1_GPTLLMTest { 

 @Test
  public void testGetIdReturnsCorrectId() {
    Integer id = 42;
    Node start = Factory.newNode(5L);
    Node end = Factory.newNode(10L);
    String type = "Token";
    FeatureMap features = new SimpleFeatureMapImpl();

    AnnotationImpl annotation = new AnnotationImpl(id, start, end, type, features);
    Assert.assertEquals(id, annotation.getId());
  }
@Test
  public void testGetTypeReturnsCorrectType() {
    Integer id = 5;
    Node start = Factory.newNode(0L);
    Node end = Factory.newNode(4L);
    String type = "Sentence";
    FeatureMap features = new SimpleFeatureMapImpl();

    AnnotationImpl annotation = new AnnotationImpl(id, start, end, type, features);
    Assert.assertEquals("Sentence", annotation.getType());
  }
@Test
  public void testGetStartNodeReturnsCorrectNode() {
    Integer id = 1;
    Node start = Factory.newNode(2L);
    Node end = Factory.newNode(5L);
    String type = "Entity";
    FeatureMap features = new SimpleFeatureMapImpl();

    AnnotationImpl annotation = new AnnotationImpl(id, start, end, type, features);
    Assert.assertEquals(start, annotation.getStartNode());
  }
@Test
  public void testGetEndNodeReturnsCorrectNode() {
    Integer id = 1;
    Node start = Factory.newNode(2L);
    Node end = Factory.newNode(5L);
    String type = "Entity";
    FeatureMap features = new SimpleFeatureMapImpl();

    AnnotationImpl annotation = new AnnotationImpl(id, start, end, type, features);
    Assert.assertEquals(end, annotation.getEndNode());
  }
@Test
  public void testEqualsTrueForIdenticalAnnotations() {
    Integer id = 11;
    Node start = Factory.newNode(3L);
    Node end = Factory.newNode(7L);
    String type = "Token";

    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("category", "NN");

    AnnotationImpl a1 = new AnnotationImpl(id, start, end, type, features);
    AnnotationImpl a2 = new AnnotationImpl(id, start, end, type, features);

    Assert.assertTrue(a1.equals(a2));
  }
@Test
  public void testEqualsFalseForDifferentId() {
    Node start = Factory.newNode(10L);
    Node end = Factory.newNode(20L);
    String type = "Token";

    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("lemma", "run");

    AnnotationImpl a1 = new AnnotationImpl(1, start, end, type, features);
    AnnotationImpl a2 = new AnnotationImpl(2, start, end, type, features);

    Assert.assertFalse(a1.equals(a2));
  }
@Test
  public void testEqualsFalseForNullComparison() {
    Node start = Factory.newNode(10L);
    Node end = Factory.newNode(20L);
    FeatureMap features = new SimpleFeatureMapImpl();

    AnnotationImpl annotation = new AnnotationImpl(100, start, end, "Entity", features);
    Assert.assertFalse(annotation.equals(null));
  }
@Test
  public void testHashCodeConsistencyWithEquals() {
    Integer id = 99;
    Node start = Factory.newNode(10L);
    Node end = Factory.newNode(20L);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("pos", "VBD");

    AnnotationImpl a1 = new AnnotationImpl(id, start, end, "Token", features);
    AnnotationImpl a2 = new AnnotationImpl(id, start, end, "Token", features);

    Assert.assertEquals(a1, a2);
    Assert.assertEquals(a1.hashCode(), a2.hashCode());
  }
@Test
  public void testCompareToSameIdReturnsZero() {
    Integer id = 6;
    Node start = Factory.newNode(0L);
    Node end = Factory.newNode(1L);
    FeatureMap features = new SimpleFeatureMapImpl();

    AnnotationImpl a1 = new AnnotationImpl(id, start, end, "A", features);
    AnnotationImpl a2 = new AnnotationImpl(id, start, end, "B", features);  

    Assert.assertEquals(0, a1.compareTo(a2));
  }
@Test
  public void testCompareToLowerIdReturnsNegative() {
    AnnotationImpl a1 = new AnnotationImpl(1, Factory.newNode(0L), Factory.newNode(5L), "X", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(0L), Factory.newNode(5L), "X", new SimpleFeatureMapImpl());

    Assert.assertTrue(a1.compareTo(a2) < 0);
  }
@Test
  public void testCompareToHigherIdReturnsPositive() {
    AnnotationImpl a1 = new AnnotationImpl(5, Factory.newNode(0L), Factory.newNode(2L), "Tag", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(0L), Factory.newNode(2L), "Tag", new SimpleFeatureMapImpl());

    Assert.assertTrue(a1.compareTo(a2) > 0);
  }
@Test
  public void testCoextensiveTrue() {
    Node n0 = Factory.newNode(100L);
    Node n1 = Factory.newNode(200L);
    AnnotationImpl a1 = new AnnotationImpl(10, n0, n1, "TypeA", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(11, n0, n1, "TypeB", new SimpleFeatureMapImpl());

    Assert.assertTrue(a1.coextensive(a2));
  }
@Test
  public void testCoextensiveFalseDifferentOffsets() {
    AnnotationImpl a1 = new AnnotationImpl(1, Factory.newNode(0L), Factory.newNode(10L), "type", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(1L), Factory.newNode(10L), "type", new SimpleFeatureMapImpl());

    Assert.assertFalse(a1.coextensive(a2));
  }
@Test
  public void testOverlapsTrue() {
    AnnotationImpl a1 = new AnnotationImpl(1, Factory.newNode(0L), Factory.newNode(10L), "Type", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(5L), Factory.newNode(15L), "Type", new SimpleFeatureMapImpl());

    Assert.assertTrue(a1.overlaps(a2));
  }
@Test
  public void testOverlapsFalse_NoOverlap() {
    AnnotationImpl a1 = new AnnotationImpl(1, Factory.newNode(0L), Factory.newNode(10L), "A", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(15L), Factory.newNode(20L), "B", new SimpleFeatureMapImpl());

    Assert.assertFalse(a1.overlaps(a2));
  }
@Test
  public void testWithinSpanOfTrue() {
    AnnotationImpl a1 = new AnnotationImpl(1, Factory.newNode(5L), Factory.newNode(10L), "Inner", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(0L), Factory.newNode(20L), "Outer", new SimpleFeatureMapImpl());

    Assert.assertTrue(a1.withinSpanOf(a2));
  }
@Test
  public void testWithinSpanOfFalse() {
    AnnotationImpl a1 = new AnnotationImpl(1, Factory.newNode(5L), Factory.newNode(25L), "A", new SimpleFeatureMapImpl());
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(10L), Factory.newNode(20L), "B", new SimpleFeatureMapImpl());

    Assert.assertFalse(a1.withinSpanOf(a2));
  }
@Test
  public void testIsCompatibleTrueWhenCoextensiveAndFeaturesSubsumed() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("key", "value");

    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("key", "value");
    f2.put("extra", "yes");

    Node start = Factory.newNode(0L);
    Node end = Factory.newNode(5L);

    AnnotationImpl a1 = new AnnotationImpl(1, start, end, "A", f1);
    AnnotationImpl a2 = new AnnotationImpl(2, start, end, "A", f2);

    Assert.assertTrue(a1.isCompatible(a2));
  }
@Test
  public void testIsCompatibleFalseWhenDifferentOffsets() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("k", "v");
    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("k", "v");

    AnnotationImpl a1 = new AnnotationImpl(1, Factory.newNode(0L), Factory.newNode(5L), "Type", f1);
    AnnotationImpl a2 = new AnnotationImpl(2, Factory.newNode(1L), Factory.newNode(5L), "Type", f2);

    Assert.assertFalse(a1.isCompatible(a2));
  }
@Test
  public void testToStringIncludesIdAndType() {
    AnnotationImpl annotation = new AnnotationImpl(123, Factory.newNode(0L), Factory.newNode(1L), "TypeX", new SimpleFeatureMapImpl());
    String str = annotation.toString();
    Assert.assertTrue(str.contains("id=123"));
    Assert.assertTrue(str.contains("type=TypeX"));
  }
@Test
  public void testIsPartiallyCompatibleTrueWhenOverlapAndFeaturesMatch() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("x", "1");

    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("x", "1");
    f2.put("y", "2");

    AnnotationImpl a1 = new AnnotationImpl(100, Factory.newNode(5L), Factory.newNode(10L), "T", f1);
    AnnotationImpl a2 = new AnnotationImpl(101, Factory.newNode(8L), Factory.newNode(20L), "T", f2);

    Assert.assertTrue(a1.isPartiallyCompatible(a2));
  }
@Test
  public void testIsPartiallyCompatibleFalseWhenNoOverlap() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("x", "1");

    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("x", "1");

    AnnotationImpl a1 = new AnnotationImpl(100, Factory.newNode(0L), Factory.newNode(4L), "Type", f1);
    AnnotationImpl a2 = new AnnotationImpl(101, Factory.newNode(10L), Factory.newNode(15L), "Type", f2);

    Assert.assertFalse(a1.isPartiallyCompatible(a2));
  }
@Test
  public void testIsCompatibleWithFeatureSubsetTrue() {
    FeatureMap f1 = new SimpleFeatureMapImpl();
    f1.put("x", "1");
    f1.put("y", "2");

    FeatureMap f2 = new SimpleFeatureMapImpl();
    f2.put("x", "1");
    f2.put("y", "2");
    f2.put("z", "3");

    Set<Object> featureSubset = new HashSet<>();
    featureSubset.add("x");

    Node s = Factory.newNode(3L);
    Node e = Factory.newNode(30L);

    AnnotationImpl a1 = new AnnotationImpl(1, s, e, "T", f1);
    AnnotationImpl a2 = new AnnotationImpl(2, s, e, "T", f2);

    Assert.assertTrue(a1.isCompatible(a2, featureSubset));
  } 
}