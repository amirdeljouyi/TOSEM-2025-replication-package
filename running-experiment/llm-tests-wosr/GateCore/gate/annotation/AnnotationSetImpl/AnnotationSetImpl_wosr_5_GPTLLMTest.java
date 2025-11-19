public class AnnotationSetImpl_wosr_5_GPTLLMTest { 

 @Test
  public void testConstructorWithDocumentOnly() throws ResourceInstantiationException {
    DocumentImpl doc = new DocumentImpl("This is some content.");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    assertNotNull(set);
    assertEquals(doc, set.getDocument());
    assertNull(set.getName());
    assertEquals(0, set.size());
  }
@Test
  public void testConstructorWithDocumentAndName() throws ResourceInstantiationException {
    DocumentImpl doc = new DocumentImpl("Some content.");
    String name = "CustomAS";
    AnnotationSetImpl set = new AnnotationSetImpl(doc, name);
    assertEquals(name, set.getName());
    assertEquals(doc, set.getDocument());
  }
@Test
  public void testAddAnnotationAndGetById() throws Exception {
    DocumentImpl doc = new DocumentImpl("Some text here.");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("key", "value");

    Integer id = set.add(0L, 4L, "Token", fm);
    Annotation retrieved = set.get(id);
    assertNotNull(retrieved);
    assertEquals("Token", retrieved.getType());
    assertEquals(fm, retrieved.getFeatures());
  }
@Test
  public void testAddExistingAnnotation() throws Exception {
    DocumentImpl doc = new DocumentImpl("ABC DEF");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("attr", "yes");
    Integer id = set.add(0L, 3L, "Test", fm);
    Annotation existing = set.get(id);
    AnnotationSetImpl other = new AnnotationSetImpl(doc);
    boolean changed = other.add(existing);
    assertTrue(changed);
    assertEquals(1, other.size());
  }
@Test
  public void testGetAnnotationByType() throws Exception {
    DocumentImpl doc = new DocumentImpl("123 456");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm1 = Factory.newFeatureMap();
    FeatureMap fm2 = Factory.newFeatureMap();
    set.add(0L, 3L, "Number", fm1);
    set.add(4L, 7L, "Number", fm2);
    AnnotationSet typeSet = set.get("Number");
    assertEquals(2, typeSet.size());
  }
@Test
  public void testQueryWithFeatureMapConstraintsMatches() throws Exception {
    DocumentImpl doc = new DocumentImpl("hello world");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("lemma", "hello");
    set.add(0L, 5L, "Word", fm);

    FeatureMap constraint = Factory.newFeatureMap();
    constraint.put("lemma", "hello");

    AnnotationSet result = set.get("Word", constraint);
    assertEquals(1, result.size());
  }
@Test
  public void testQueryWithFeatureMapConstraintsNoMatch() throws Exception {
    DocumentImpl doc = new DocumentImpl("x y z");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();
    fm.put("tag", "NN");
    set.add(0L, 1L, "Token", fm);

    FeatureMap constraint = new SimpleFeatureMapImpl();
    constraint.put("tag", "VB");

    AnnotationSet result = set.get("Token", constraint);
    assertEquals(0, result.size());
  }
@Test
  public void testGetAnnotationByFeatureNames() throws Exception {
    DocumentImpl doc = new DocumentImpl("example text");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("pos", "NN");
    fm.put("lemma", "example");

    set.add(0L, 7L, "Word", fm);
    Set<Object> features = new HashSet<>();
    features.add("pos");

    AnnotationSet subset = set.get("Word", features);
    assertEquals(1, subset.size());
  }
@Test
  public void testGetByOffset() throws Exception {
    DocumentImpl doc = new DocumentImpl("abc def");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 3L, "Part", Factory.newFeatureMap());
    set.add(4L, 7L, "Part", Factory.newFeatureMap());

    AnnotationSet atOffset = set.get(0L);
    assertEquals(1, atOffset.size());
  }
@Test
  public void testGetStartingAt() throws Exception {
    DocumentImpl doc = new DocumentImpl("data");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 4L, "Data", Factory.newFeatureMap());

    AnnotationSet startAt0 = set.getStartingAt(0L);
    assertEquals(1, startAt0.size());
    AnnotationSet startAt5 = set.getStartingAt(5L);
    assertEquals(0, startAt5.size());
  }
@Test
  public void testGetContainedAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("abcdefgh");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(2L, 6L, "A", Factory.newFeatureMap());
    set.add(1L, 7L, "B", Factory.newFeatureMap());

    AnnotationSet contained = set.getContained(2L, 6L);
    assertEquals(1, contained.size());
  }
@Test
  public void testGetCoveringAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("aaaaabbbbb");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 9L, "Cover", Factory.newFeatureMap());

    AnnotationSet covering = set.getCovering("Cover", 1L, 8L);
    assertEquals(1, covering.size());
  }
@Test
  public void testGetStrictAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("GATE NLP");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 4L, "Word", Factory.newFeatureMap());
    set.add(5L, 8L, "Word", Factory.newFeatureMap());

    AnnotationSet strict = set.getStrict(0L, 4L);
    assertEquals(1, strict.size());
    assertEquals(Long.valueOf(4L), strict.iterator().next().getEndNode().getOffset());
  }
@Test
  public void testRemoveAnnotation() throws Exception {
    DocumentImpl doc = new DocumentImpl("abc");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Integer id = set.add(0L, 2L, "Test", Factory.newFeatureMap());
    Annotation a = set.get(id);
    boolean result = set.remove(a);
    assertTrue(result);
    assertEquals(0, set.size());
  }
@Test
  public void testClear() throws Exception {
    DocumentImpl doc = new DocumentImpl("clear this");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 5L, "Clear", Factory.newFeatureMap());
    set.add(6L, 10L, "This", Factory.newFeatureMap());
    set.clear();
    assertEquals(0, set.size());
  }
@Test
  public void testGetAllTypes() throws Exception {
    DocumentImpl doc = new DocumentImpl("types example");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 5L, "A", Factory.newFeatureMap());
    set.add(6L, 13L, "B", Factory.newFeatureMap());

    Set<String> types = set.getAllTypes();
    assertTrue(types.contains("A"));
    assertTrue(types.contains("B"));
    assertEquals(2, types.size());
  }
@Test
  public void testInDocumentOrder() throws Exception {
    DocumentImpl doc = new DocumentImpl("one two");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(4L, 7L, "Second", Factory.newFeatureMap());
    set.add(0L, 3L, "First", Factory.newFeatureMap());

    List<Annotation> ordered = set.inDocumentOrder();
    assertEquals(2, ordered.size());
    assertTrue(ordered.get(0).getStartNode().getOffset() < ordered.get(1).getStartNode().getOffset());
  }
@Test
  public void testAddAllAnnotations() throws Exception {
    DocumentImpl doc = new DocumentImpl("list items");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    List<Annotation> annotations = new ArrayList<>();
    annotations.add(set.get(set.add(0L, 4L, "Item", Factory.newFeatureMap())));
    annotations.add(set.get(set.add(5L, 10L, "Item", Factory.newFeatureMap())));

    AnnotationSetImpl target = new AnnotationSetImpl(doc);
    boolean changed = target.addAll(annotations);
    assertTrue(changed);
    assertEquals(2, target.size());
  } 
}