public class AnnotationSetImpl_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructorWithDocument() throws GateException {
    Document doc = Factory.newDocument("Sample text");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    assertEquals(doc, set.getDocument());
    assertNull(set.getName());
    assertEquals(0, set.size());
  }
@Test
  public void testConstructorWithDocumentAndName() throws GateException {
    Document doc = Factory.newDocument("Another sample");
    AnnotationSetImpl set = new AnnotationSetImpl(doc, "MySet");
    assertEquals(doc, set.getDocument());
    assertEquals("MySet", set.getName());
  }
@Test
  public void testAddAnnotationUsingOffsets() throws GateException {
    Document doc = Factory.newDocument("Hello world");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Integer id = set.add(0L, 5L, "Token", Factory.newFeatureMap());
    Annotation a = set.get(id);
    assertEquals("Token", a.getType());
    assertEquals(Long.valueOf(0L), a.getStartNode().getOffset());
    assertEquals(Long.valueOf(5L), a.getEndNode().getOffset());
  }
@Test
  public void testAddAnnotationAndRetrieveByType() throws GateException {
    Document doc = Factory.newDocument("Hello world");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap features = Factory.newFeatureMap();
    set.add(0L, 5L, "Word", features);
    AnnotationSet words = set.get("Word");
    assertEquals(1, words.size());
    assertEquals("Word", words.iterator().next().getType());
  }
@Test
  public void testGetContainedAnnotations() throws GateException {
    Document doc = Factory.newDocument("Hello world again");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 5L, "Token", Factory.newFeatureMap());
    set.add(6L, 11L, "Token", Factory.newFeatureMap());
    AnnotationSet contained = set.getContained(0L, 11L);
    assertEquals(2, contained.size());
  }
@Test
  public void testGetOverlappingAnnotations() throws GateException {
    Document doc = Factory.newDocument("Sample document");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(2L, 6L, "Overlap", Factory.newFeatureMap());
    set.add(4L, 8L, "Overlap", Factory.newFeatureMap());
    AnnotationSet overlap = set.get(3L, 7L);
    assertEquals(2, overlap.size());
  }
@Test
  public void testGetStrictAnnotations() throws GateException {
    Document doc = Factory.newDocument("Some text here");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 4L, "Phrase", Factory.newFeatureMap());
    set.add(0L, 4L, "Phrase", Factory.newFeatureMap());
    AnnotationSet exact = set.getStrict(0L, 4L);
    assertEquals(2, exact.size());
  }
@Test
  public void testRemoveAnnotationByInstance() throws GateException {
    Document doc = Factory.newDocument("Test document");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Integer id = set.add(1L, 4L, "Test", Factory.newFeatureMap());
    Annotation a = set.get(id);
    boolean removed = set.remove(a);
    assertTrue(removed);
    assertEquals(0, set.size());
    assertNull(set.get(id));
  }
@Test
  public void testClearAnnotationSet() throws GateException {
    Document doc = Factory.newDocument("Another test");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 4L, "Alpha", Factory.newFeatureMap());
    set.add(5L, 9L, "Beta", Factory.newFeatureMap());
    assertEquals(2, set.size());
    set.clear();
    assertEquals(0, set.size());
  }
@Test
  public void testGetAnnotationsByFeatureName() throws GateException {
    Document doc = Factory.newDocument("Features are cool");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("class", "NN");
    set.add(0L, 3L, "POS", fm);
    Set<Object> featureSet = new HashSet<Object>();
    featureSet.add("class");
    AnnotationSet result = set.get("POS", featureSet);
    assertEquals(1, result.size());
  }
@Test
  public void testGetAnnotationsByMultipleTypes() throws GateException {
    Document doc = Factory.newDocument("Types test");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 5L, "Type1", Factory.newFeatureMap());
    set.add(6L, 10L, "Type2", Factory.newFeatureMap());

    Set<String> types = new HashSet<String>();
    types.add("Type1");
    types.add("Type2");

    AnnotationSet result = set.get(types);
    assertEquals(2, result.size());
  }
@Test
  public void testInDocumentOrder() throws GateException {
    Document doc = Factory.newDocument("In order");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(5L, 7L, "Later", Factory.newFeatureMap());
    set.add(0L, 2L, "Earlier", Factory.newFeatureMap());
    List<Annotation> ordered = set.inDocumentOrder();
    assertEquals(2, ordered.size());
    assertTrue(ordered.get(0).getStartNode().getOffset() <= ordered.get(1).getStartNode().getOffset());
  }
@Test
  public void testGetCoveringAnnotations() throws GateException {
    Document doc = Factory.newDocument("Cover this text");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 10L, "Block", Factory.newFeatureMap());
    AnnotationSet result = set.getCovering("Block", 2L, 5L);
    assertEquals(1, result.size());
  }
@Test
  public void testAddAnnotationViaFactoryObject() throws GateException {
    Document doc = Factory.newDocument("Internal add");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    FeatureMap fm = Factory.newFeatureMap();
    Integer id = set.add(1L, 3L, "Tag", fm);
    assertNotNull(set.get(id));
  }
@Test
  public void testEmptySetReturnsImmutableAnnotationSetEmpty() throws GateException {
    Document doc = Factory.newDocument("Empty test");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    AnnotationSet result = set.get();
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetStartingAtOffset() throws GateException {
    Document doc = Factory.newDocument("Start here now");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(5L, 9L, "TagStart", Factory.newFeatureMap());
    AnnotationSet result = set.getStartingAt(5L);
    assertEquals(1, result.size());
  }
@Test
  public void testAddDuplicateAnnotationReplacesOld() throws GateException {
    Document doc = Factory.newDocument("Overwrite");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    Integer id = set.add(0L, 3L, "Old", Factory.newFeatureMap());
    Annotation old = set.get(id);
    Annotation replacement = old;
    boolean added = set.add(replacement);
    assertFalse(added);
  }
@Test
  public void testAddAllKeepIDs() throws GateException {
    Document doc = Factory.newDocument("AddAll");
    AnnotationSetImpl source = new AnnotationSetImpl(doc);
    source.add(0L, 2L, "One", Factory.newFeatureMap());
    source.add(3L, 5L, "Two", Factory.newFeatureMap());

    AnnotationSetImpl target = new AnnotationSetImpl(doc);
    boolean changed = target.addAllKeepIDs(source);
    assertTrue(changed);
    assertEquals(2, target.size());
  }
@Test
  public void testRemoveAnnotationUsingIterator() throws GateException {
    Document doc = Factory.newDocument("Iterator remove");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 4L, "A", Factory.newFeatureMap());
    Iterator<Annotation> iter = set.iterator();
    assertTrue(iter.hasNext());
    Annotation next = iter.next();
    assertNotNull(next);
    iter.remove();
    assertEquals(0, set.size());
  }
@Test
  public void testGetByOffsetNoMatchesReturnsEmptySet() throws GateException {
    Document doc = Factory.newDocument("Not found test");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    AnnotationSet result = set.get(1000L);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetAllTypesReturnsCorrectSet() throws GateException {
    Document doc = Factory.newDocument("All types");
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    set.add(0L, 2L, "TypeA", Factory.newFeatureMap());
    set.add(3L, 5L, "TypeB", Factory.newFeatureMap());
    Set<String> types = set.getAllTypes();
    assertEquals(2, types.size());
    assertTrue(types.contains("TypeA"));
    assertTrue(types.contains("TypeB"));
  } 
}