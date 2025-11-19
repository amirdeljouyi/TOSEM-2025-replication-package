public class AnnotationSetImpl_wosr_1_GPTLLMTest { 

 @Test
  public void testAddAnnotationUsingOffsets() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("This is some content for testing.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);
    Integer id = annotationSet.add(0L, 4L, "Token", Factory.newFeatureMap());

    Assert.assertNotNull(id);
    Annotation annotation = annotationSet.get(id);
    Assert.assertEquals("Token", annotation.getType());
    Assert.assertEquals(Long.valueOf(0L), annotation.getStartNode().getOffset());
    Assert.assertEquals(Long.valueOf(4L), annotation.getEndNode().getOffset());
  }
@Test
  public void testAddAnnotationUsingNodes() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Some text.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node start = new NodeImpl(1, 5L);
    Node end = new NodeImpl(2, 9L);
    Integer id = annotationSet.add(start, end, "Entity", Factory.newFeatureMap());

    Assert.assertNotNull(id);
    Annotation retrieved = annotationSet.get(id);
    Assert.assertEquals("Entity", retrieved.getType());
    Assert.assertEquals(start.getOffset(), retrieved.getStartNode().getOffset());
    Assert.assertEquals(end.getOffset(), retrieved.getEndNode().getOffset());
  }
@Test
  public void testAddAndRetrieveByType() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Example document.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(0L, 7L, "Person", Factory.newFeatureMap());
    annotationSet.add(8L, 16L, "Location", Factory.newFeatureMap());

    AnnotationSet personSet = annotationSet.get("Person");

    Assert.assertEquals(1, personSet.size());
    Assert.assertEquals("Person", personSet.iterator().next().getType());
  }
@Test
  public void testGetContainedAnnotations() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Some information to be retrieved.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(0L, 10L, "Range", Factory.newFeatureMap());
    annotationSet.add(12L, 15L, "Range", Factory.newFeatureMap());
    annotationSet.add(5L, 13L, "Range", Factory.newFeatureMap());

    AnnotationSet contained = annotationSet.getContained(0L, 15L);

    Assert.assertEquals(2, contained.size());
  }
@Test
  public void testRemoveAnnotation() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Short text.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Integer id = annotationSet.add(0L, 5L, "Word", Factory.newFeatureMap());
    Annotation annotation = annotationSet.get(id);

    boolean removed = annotationSet.remove(annotation);
    Annotation lookup = annotationSet.get(id);

    Assert.assertTrue(removed);
    Assert.assertNull(lookup);
  }
@Test
  public void testAddAllPreservesTypeAndOffsets() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Multiple tokens in text.");
    AnnotationSetImpl originalSet = new AnnotationSetImpl(document);

    originalSet.add(0L, 8L, "TypeA", Factory.newFeatureMap());
    originalSet.add(9L, 14L, "TypeB", Factory.newFeatureMap());

    AnnotationSetImpl copySet = new AnnotationSetImpl(document);
    copySet.addAll(originalSet);

    Assert.assertEquals(originalSet.size(), copySet.size());
  }
@Test
  public void testGetCoveringAnnotations() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Sample test document.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(0L, 20L, "Span", Factory.newFeatureMap());
    annotationSet.add(5L, 10L, "Nested", Factory.newFeatureMap());

    AnnotationSet covering = annotationSet.getCovering("Span", 5L, 10L);

    Assert.assertEquals(1, covering.size());
    Assert.assertEquals("Span", covering.iterator().next().getType());
  }
@Test
  public void testInDocumentOrder() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Ordered annotation test.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(15L, 20L, "Later", Factory.newFeatureMap());
    annotationSet.add(0L, 5L, "First", Factory.newFeatureMap());
    annotationSet.add(6L, 10L, "Middle", Factory.newFeatureMap());

    List<Annotation> ordered = annotationSet.inDocumentOrder();

    Assert.assertEquals(3, ordered.size());
    Assert.assertTrue(ordered.get(0).getStartNode().getOffset() < ordered.get(1).getStartNode().getOffset());
    Assert.assertTrue(ordered.get(1).getStartNode().getOffset() < ordered.get(2).getStartNode().getOffset());
  }
@Test
  public void testGetAllTypes() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Types test input.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(0L, 3L, "A", Factory.newFeatureMap());
    annotationSet.add(4L, 6L, "B", Factory.newFeatureMap());
    annotationSet.add(7L, 9L, "C", Factory.newFeatureMap());

    Set<String> types = annotationSet.getAllTypes();

    Assert.assertTrue(types.contains("A"));
    Assert.assertTrue(types.contains("B"));
    Assert.assertTrue(types.contains("C"));
  }
@Test
  public void testGetStartingAt() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Testing start offset.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(0L, 5L, "Alpha", Factory.newFeatureMap());
    annotationSet.add(0L, 3L, "Alpha", Factory.newFeatureMap());
    annotationSet.add(6L, 9L, "Beta", Factory.newFeatureMap());

    AnnotationSet starting = annotationSet.getStartingAt(0L);
    Assert.assertEquals(2, starting.size());
  }
@Test
  public void testGetByFeatureValueMatch() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Matching features test.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    FeatureMap fm1 = Factory.newFeatureMap();
    fm1.put("key1", "value1");
    FeatureMap fm2 = Factory.newFeatureMap();
    fm2.put("key1", "value2");

    annotationSet.add(0L, 4L, "Test", fm1);
    annotationSet.add(5L, 9L, "Test", fm2);

    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("key1", "value1");

    AnnotationSet matched = annotationSet.get("Test", constraint);

    Assert.assertEquals(1, matched.size());
    Assert.assertEquals("value1", matched.iterator().next().getFeatures().get("key1"));
  }
@Test
  public void testClearAnnotationSet() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("Clearing content.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(0L, 4L, "One", Factory.newFeatureMap());
    annotationSet.add(5L, 9L, "Two", Factory.newFeatureMap());

    annotationSet.clear();

    Assert.assertEquals(0, annotationSet.size());
  }
@Test
  public void testGetStrictMatchingAnnotations() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl("test string");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    annotationSet.add(0L, 4L, "Strict", Factory.newFeatureMap());
    annotationSet.add(0L, 5L, "Strict", Factory.newFeatureMap());
    annotationSet.add(1L, 4L, "Strict", Factory.newFeatureMap());

    AnnotationSet strict = annotationSet.getStrict(0L, 4L);

    Assert.assertEquals(1, strict.size());
    Annotation ann = strict.iterator().next();
    Assert.assertEquals(Long.valueOf(0L), ann.getStartNode().getOffset());
    Assert.assertEquals(Long.valueOf(4L), ann.getEndNode().getOffset());
  }
@Test
  public void testAnnotationSetCloneReturnsSameType() throws CloneNotSupportedException {
    DocumentImpl document = new DocumentImpl("Cloning test.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Object cloned = annotationSet.clone();

    Assert.assertTrue(cloned instanceof AnnotationSetImpl);
  }
@Test
  public void testRemoveNonExistentAnnotationReturnsFalse() {
    DocumentImpl document = new DocumentImpl("Non-existent.");
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(document);

    Node start = new NodeImpl(1, 0L);
    Node end = new NodeImpl(2, 1L);
    AnnotationImpl fake = new AnnotationImpl(annotationSet, 999, start, end, "Fake", Factory.newFeatureMap());

    boolean result = annotationSet.remove(fake);

    Assert.assertFalse(result);
  } 
}