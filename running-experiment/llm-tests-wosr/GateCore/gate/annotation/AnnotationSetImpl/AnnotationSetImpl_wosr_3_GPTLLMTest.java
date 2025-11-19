public class AnnotationSetImpl_wosr_3_GPTLLMTest { 

 @Test
  public void testConstructorWithDocument() {
    Document doc = new DocumentImpl();
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    assertNotNull(annotationSet);
    assertEquals(doc, annotationSet.getDocument());
  }
@Test
  public void testConstructorWithDocumentAndName() {
    Document doc = new DocumentImpl();
    String name = "TestSet";
    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc, name);
    assertEquals(name, annotationSet.getName());
    assertEquals(doc, annotationSet.getDocument());
  }
@Test
  public void testAddAnnotationAndGetById() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "Hello";
      }

      public Long size() {
        return 5L;
      }

      public String getContent(Long start, Long end) {
        return "Hello".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("label", "greet");
    Integer id = annotationSet.add(0L, 5L, "Token", features);
    Annotation retrieved = annotationSet.get(id);
    assertNotNull(retrieved);
    assertEquals("Token", retrieved.getType());
    assertEquals(features, retrieved.getFeatures());
  }
@Test
  public void testAddAndRemoveAnnotation() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "abc";
      }

      public Long size() {
        return 3L;
      }

      public String getContent(Long start, Long end) {
        return "abc".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();
    Integer id = annotationSet.add(0L, 3L, "Word", fm);
    Annotation annot = annotationSet.get(id);

    assertTrue(annotationSet.remove(annot));
    assertNull(annotationSet.get(id));
  }
@Test
  public void testGetByType() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "sample text";
      }

      public Long size() {
        return 11L;
      }

      public String getContent(Long start, Long end) {
        return "sample text".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap f1 = new SimpleFeatureMapImpl();
    FeatureMap f2 = new SimpleFeatureMapImpl();
    annotationSet.add(0L, 6L, "Word", f1);
    annotationSet.add(7L, 11L, "Word", f2);

    AnnotationSet wordSet = annotationSet.get("Word");
    assertEquals(2, wordSet.size());

    AnnotationSet fakeSet = annotationSet.get("NonExistent");
    assertNotNull(fakeSet);
    assertEquals(0, fakeSet.size());
  }
@Test
  public void testGetAllTypes() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "foo bar";
      }

      public Long size() {
        return 7L;
      }

      public String getContent(Long start, Long end) {
        return "foo bar".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    FeatureMap fm = new SimpleFeatureMapImpl();
    annotationSet.add(0L, 3L, "Noun", fm);
    annotationSet.add(4L, 7L, "Verb", fm);

    Set<String> types = annotationSet.getAllTypes();
    assertEquals(new HashSet<>(Arrays.asList("Noun", "Verb")), types);
  }
@Test
  public void testGetContainedAnnotations() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "abcd efgh";
      }

      public Long size() {
        return 9L;
      }

      public String getContent(Long start, Long end) {
        return "abcd efgh".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(0L, 4L, "Token", new SimpleFeatureMapImpl());
    annotationSet.add(5L, 9L, "Token", new SimpleFeatureMapImpl());
    annotationSet.add(0L, 9L, "Phrase", new SimpleFeatureMapImpl());

    AnnotationSet contained = annotationSet.getContained(0L, 9L);
    assertEquals(2, contained.size()); 
  }
@Test
  public void testGetCoveringAnnotations() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "012345678";
      }

      public Long size() {
        return 9L;
      }

      public String getContent(Long start, Long end) {
        return "012345678".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(0L, 9L, "Sentence", new SimpleFeatureMapImpl());
    annotationSet.add(2L, 4L, "Word", new SimpleFeatureMapImpl());

    AnnotationSet covering = annotationSet.getCovering("Sentence", 2L, 4L);
    assertEquals(1, covering.size());

    AnnotationSet empty = annotationSet.getCovering("Word", 0L, 9L);
    assertTrue(empty.isEmpty());
  }
@Test
  public void testClearAnnotations() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "xyz";
      }

      public Long size() {
        return 3L;
      }

      public String getContent(Long start, Long end) {
        return "xyz".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(0L, 1L, "Char", new SimpleFeatureMapImpl());
    annotationSet.add(1L, 2L, "Char", new SimpleFeatureMapImpl());
    assertEquals(2, annotationSet.size());
    annotationSet.clear();
    assertEquals(0, annotationSet.size());
  }
@Test
  public void testEmptyGet() {
    Document doc = new DocumentImpl();
    AnnotationSetImpl set = new AnnotationSetImpl(doc);
    AnnotationSet empty = set.get();
    assertNotNull(empty);
    assertTrue(empty.isEmpty());
  }
@Test
  public void testAddAllMethod() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "test";
      }

      public Long size() {
        return 4L;
      }

      public String getContent(Long start, Long end) {
        return "test".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    Integer a1 = annotationSet.add(0L, 2L, "A", new SimpleFeatureMapImpl());
    Integer a2 = annotationSet.add(2L, 4L, "B", new SimpleFeatureMapImpl());
    Annotation ann1 = annotationSet.get(a1);
    Annotation ann2 = annotationSet.get(a2);

    Document doc2 = new DocumentImpl();
    AnnotationSetImpl newSet = new AnnotationSetImpl(doc2);
    try {
      newSet.addAll(Arrays.asList(ann1, ann2));
    } catch (IllegalArgumentException e) {
      
      assertTrue(e.getMessage().contains("gate.util.InvalidOffsetException"));
    }
  }
@Test
  public void testInDocumentOrder() throws InvalidOffsetException {
    Document doc = new DocumentImpl();
    doc.setContent(new DocumentContent() {
      public String getContent() {
        return "doc";
      }

      public Long size() {
        return 3L;
      }

      public String getContent(Long start, Long end) {
        return "doc".substring(start.intValue(), end.intValue());
      }
    });

    AnnotationSetImpl annotationSet = new AnnotationSetImpl(doc);
    annotationSet.add(1L, 2L, "C", new SimpleFeatureMapImpl());
    annotationSet.add(0L, 1L, "A", new SimpleFeatureMapImpl());
    annotationSet.add(2L, 3L, "B", new SimpleFeatureMapImpl());

    List<Annotation> ordered = annotationSet.inDocumentOrder();
    assertEquals(3, ordered.size());
    assertTrue(ordered.get(0).getStartNode().getOffset() <= ordered.get(1).getStartNode().getOffset());
  } 
}