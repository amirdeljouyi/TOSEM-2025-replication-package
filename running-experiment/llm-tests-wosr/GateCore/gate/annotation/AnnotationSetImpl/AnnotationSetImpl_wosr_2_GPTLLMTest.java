public class AnnotationSetImpl_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorWithDocument() {
    Document doc = Factory.newDocument("Sample text");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    assertNotNull(set);
    assertEquals(0, set.size());
    assertEquals(doc, set.getDocument());
  }
@Test
  public void testConstructorWithDocumentAndName() {
    Document doc = Factory.newDocument("Sample text");
    String name = "TestAS";
    AnnotationSetImpl set = new AnnotationSetImpl(doc, name);
    assertNotNull(set);
    assertEquals(name, set.getName());
  }
@Test
  public void testAddAnnotationUsingLongOffsets() throws Exception {
    Document doc = Factory.newDocument("Hello world");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap features = new FeatureMapImpl();
    features.put("key", "value");
    Integer id = set.add(0L, 5L, "Token", features);
    assertNotNull(id);
    assertEquals(1, set.size());
  }
@Test
  public void testAddAndRemoveAnnotation() throws Exception {
    DocumentImpl doc = (DocumentImpl)Factory.newDocument("Hello world");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Integer id = set.add(0L, 5L, "Person", new FeatureMapImpl());
    AnnotationImpl ann = (AnnotationImpl)set.get(id);
    assertTrue(set.remove(ann));
    assertEquals(0, set.size());
  }
@Test
  public void testAddAnnotationWithPreexistingNodes() throws Exception {
    DocumentImpl doc = (DocumentImpl)Factory.newDocument("abc def");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Node start = new NodeImpl(1, 0L);
    Node end = new NodeImpl(2, 3L);
    Integer id = set.add(start, end, "MyType", new FeatureMapImpl());
    assertNotNull(set.get(id));
  }
@Test
  public void testGetByType() throws Exception {
    Document doc = Factory.newDocument("abc");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 1L, "Foo", new FeatureMapImpl());
    set.add(1L, 2L, "Bar", new FeatureMapImpl());
    assertEquals(1, set.get("Foo").size());
    assertTrue(set.get("Foo").iterator().next().getType().equals("Foo"));
  }
@Test
  public void testGetByMultipleTypes() throws Exception {
    Document doc = Factory.newDocument("abcdef");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 1L, "X", new FeatureMapImpl());
    set.add(1L, 2L, "Y", new FeatureMapImpl());
    set.add(2L, 3L, "Z", new FeatureMapImpl());
    Set<String> types = new HashSet<String>();
    types.add("X");
    types.add("Z");
    AnnotationSetImpl result = (AnnotationSetImpl)set.get(types);
    assertEquals(2, result.size());
  }
@Test
  public void testGetByFeatureMap() throws Exception {
    Document doc = Factory.newDocument("abc");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap features = new FeatureMapImpl();
    features.put("gender", "male");
    set.add(0L, 1L, "Person", features);
    FeatureMap query = new FeatureMapImpl();
    query.put("gender", "male");
    AnnotationSetImpl result = (AnnotationSetImpl)set.get("Person", query);
    assertEquals(1, result.size());
  }
@Test
  public void testGetByFeatureNames() throws Exception {
    Document doc = Factory.newDocument("abc");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap features = new FeatureMapImpl();
    features.put("x", 1);
    features.put("y", 2);
    set.add(0L, 1L, "Point", features);
    Set<Object> featureNames = new HashSet<Object>();
    featureNames.add("x");
    featureNames.add("y");
    AnnotationSetImpl result = (AnnotationSetImpl)set.get("Point", featureNames);
    assertEquals(1, result.size());
  }
@Test
  public void testInDocumentOrder() throws Exception {
    Document doc = Factory.newDocument("abcdef");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(2L, 4L, "A", new FeatureMapImpl());
    set.add(0L, 1L, "B", new FeatureMapImpl());
    List annotations = set.inDocumentOrder();
    assertEquals(2, annotations.size());
    assertTrue(annotations.get(0) instanceof AnnotationImpl);
  }
@Test
  public void testGetStrict() throws Exception {
    Document doc = Factory.newDocument("abcdef");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(1L, 3L, "X", new FeatureMapImpl());
    set.add(1L, 4L, "X", new FeatureMapImpl());
    AnnotationSetImpl result = (AnnotationSetImpl)set.getStrict(1L, 3L);
    assertEquals(1, result.size());
  }
@Test
  public void testGetOverlapping() throws Exception {
    Document doc = Factory.newDocument("abcdef");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(1L, 3L, "Entity", new FeatureMapImpl());
    set.add(2L, 5L, "Entity", new FeatureMapImpl());
    AnnotationSetImpl result = (AnnotationSetImpl)set.get("Entity", 2L, 4L);
    assertEquals(2, result.size());
  }
@Test
  public void testGetCoveringSpan() throws Exception {
    Document doc = Factory.newDocument("abcdef");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(1L, 6L, "Span", new FeatureMapImpl());
    set.add(0L, 2L, "Span", new FeatureMapImpl());
    AnnotationSetImpl result = (AnnotationSetImpl)set.getCovering("Span", 2L, 5L);
    assertEquals(1, result.size());
  }
@Test
  public void testGetContained() throws Exception {
    Document doc = Factory.newDocument("1234567");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(1L, 5L, "Contained", new FeatureMapImpl());
    set.add(2L, 4L, "Contained", new FeatureMapImpl());
    AnnotationSetImpl result = (AnnotationSetImpl)set.getContained(2L, 5L);
    assertEquals(1, result.size());
  }
@Test
  public void testGetStartingAtOffset() throws Exception {
    Document doc = Factory.newDocument("abc");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(1L, 2L, "At", new FeatureMapImpl());
    AnnotationSetImpl result = (AnnotationSetImpl)set.getStartingAt(1L);
    assertEquals(1, result.size());
  }
@Test
  public void testClearSet() throws Exception {
    Document doc = Factory.newDocument("abc");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 1L, "ClearMe", new FeatureMapImpl());
    set.clear();
    assertEquals(0, set.size());
    assertNull(set.get(0));
  }
@Test
  public void testClone() throws Exception {
    Document doc = Factory.newDocument("Clone me");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 2L, "CloneType", new FeatureMapImpl());
    AnnotationSetImpl clone = new AnnotationSetImpl(set);
    assertNotSame(set, clone);
    assertEquals(1, clone.size());
  }
@Test
  public void testGetAllTypes() throws Exception {
    Document doc = Factory.newDocument("abc xyz");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 2L, "Alpha", new FeatureMapImpl());
    set.add(3L, 6L, "Beta", new FeatureMapImpl());
    Set<String> types = set.getAllTypes();
    assertEquals(2, types.size());
    assertTrue(types.contains("Alpha"));
    assertTrue(types.contains("Beta"));
  }
@Test
  public void testGetByOffsetSingleHit() throws Exception {
    Document doc = Factory.newDocument("abcdefg");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(2L, 5L, "OffsetTest", new FeatureMapImpl());
    AnnotationSetImpl result = (AnnotationSetImpl)set.get(2L);
    assertEquals(1, result.size());
  } 
}