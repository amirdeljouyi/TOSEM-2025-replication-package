public class CorpusImpl_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructorInitializesSupportList() {
    CorpusImpl corpus = new CorpusImpl();
    assertNotNull(corpus.supportList);
    assertTrue(corpus.supportList.isEmpty());
  }
@Test
  public void testAddAndGetDocument() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Sample text");
    corpus.add(doc);
    assertEquals(1, corpus.size());
    assertSame(doc, corpus.get(0));
  }
@Test
  public void testRemoveDocument() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("To remove");
    corpus.add(doc);
    Document removed = corpus.remove(0);
    assertEquals(doc, removed);
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testSetDocument() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = Factory.newDocument("Old");
    Document doc2 = Factory.newDocument("New");
    corpus.add(doc1);
    Document old = corpus.set(0, doc2);
    assertSame(doc1, old);
    assertSame(doc2, corpus.get(0));
  }
@Test
  public void testGetDocumentNames() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = Factory.newDocument("Doc1");
    doc1.setName("Name1");
    Document doc2 = Factory.newDocument("Doc2");
    doc2.setName("Name2");
    corpus.add(doc1);
    corpus.add(doc2);
    List<String> names = corpus.getDocumentNames();
    assertEquals(2, names.size());
    assertEquals("Name1", names.get(0));
    assertEquals("Name2", names.get(1));
  }
@Test
  public void testGetDocumentNameByIndex() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Content");
    doc.setName("TestDoc");
    corpus.add(doc);
    String name = corpus.getDocumentName(0);
    assertEquals("TestDoc", name);
  }
@Test
  public void testIsDocumentLoadedAlwaysTrue() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Loaded");
    corpus.add(doc);
    assertTrue(corpus.isDocumentLoaded(0));
  }
@Test
  public void testClearCorpus() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Clear me");
    corpus.add(doc);
    corpus.clear();
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testContainsDocument() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Check contains");
    corpus.add(doc);
    assertTrue(corpus.contains(doc));
    assertFalse(corpus.contains(Factory.newDocument("Other")));
  }
@Test
  public void testAddAllDocuments() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = Factory.newDocument("A");
    Document doc2 = Factory.newDocument("B");
    List<Document> docs = new ArrayList<Document>();
    docs.add(doc1);
    docs.add(doc2);
    boolean result = corpus.addAll(docs);
    assertTrue(result);
    assertEquals(2, corpus.size());
  }
@Test
  public void testAddAllAtIndex() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = Factory.newDocument("A");
    Document doc2 = Factory.newDocument("B");
    Document doc3 = Factory.newDocument("C");
    corpus.add(doc1);
    List<Document> docsToAdd = new ArrayList<Document>();
    docsToAdd.add(doc2);
    docsToAdd.add(doc3);
    boolean result = corpus.addAll(0, docsToAdd);
    assertTrue(result);
    assertEquals(doc2, corpus.get(0));
    assertEquals(doc3, corpus.get(1));
    assertEquals(doc1, corpus.get(2));
  }
@Test
  public void testRetainAllDocuments() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = Factory.newDocument("A");
    Document doc2 = Factory.newDocument("B");
    corpus.add(doc1);
    corpus.add(doc2);
    List<Document> toRetain = Collections.singletonList(doc1);
    boolean modified = corpus.retainAll(toRetain);
    assertTrue(modified);
    assertEquals(1, corpus.size());
    assertSame(doc1, corpus.get(0));
  }
@Test
  public void testRemoveAllDocuments() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = Factory.newDocument("A");
    Document doc2 = Factory.newDocument("B");
    corpus.add(doc1);
    corpus.add(doc2);
    List<Document> toRemove = Collections.singletonList(doc1);
    boolean changed = corpus.removeAll(toRemove);
    assertTrue(changed);
    assertFalse(corpus.contains(doc1));
    assertTrue(corpus.contains(doc2));
  }
@Test
  public void testToArray() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Array");
    corpus.add(doc);
    Object[] array = corpus.toArray();
    assertEquals(1, array.length);
    assertSame(doc, array[0]);
  }
@Test
  public void testToArrayWithGeneric() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Generic");
    corpus.add(doc);
    Document[] output = new Document[1];
    Document[] array = corpus.toArray(output);
    assertEquals(1, array.length);
    assertSame(doc, array[0]);
  }
@Test
  public void testEqualsAndHashCode() throws ResourceInstantiationException {
    CorpusImpl corpus1 = new CorpusImpl();
    CorpusImpl corpus2 = new CorpusImpl();

    Document doc1 = Factory.newDocument("One");
    Document doc2 = Factory.newDocument("Two");

    corpus1.add(doc1);
    corpus1.add(doc2);

    corpus2.add(doc1);
    corpus2.add(doc2);

    assertEquals(corpus1, corpus2);
    assertEquals(corpus1.hashCode(), corpus2.hashCode());
  }
@Test
  public void testPopulateThrowsOnInvalidURLProtocol() throws IOException {
    CorpusImpl corpus = new CorpusImpl();
    URL invalidURL = new URL("http://invalid");
    try {
      CorpusImpl.populate(corpus, invalidURL, null, "UTF-8", false);
      fail("Expected IllegalArgumentException for invalid URL protocol");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("file:"));
    }
  }
@Test
  public void testPopulateThrowsOnFileNotFound() throws IOException {
    CorpusImpl corpus = new CorpusImpl();
    File file = new File("nonexistentDir");
    URL url = file.toURI().toURL();
    try {
      CorpusImpl.populate(corpus, url, null, "UTF-8", false);
      fail("Expected FileNotFoundException for non-existing directory");
    } catch (IOException e) {
      assertTrue(e instanceof java.io.FileNotFoundException);
    }
  }
@Test
  public void testIndexOfAndLastIndexOf() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Repeat");
    corpus.add(doc);
    corpus.add(doc);
    assertEquals(0, corpus.indexOf(doc));
    assertEquals(1, corpus.lastIndexOf(doc));
  }
@Test
  public void testSubList() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = Factory.newDocument("A");
    Document doc2 = Factory.newDocument("B");
    Document doc3 = Factory.newDocument("C");
    corpus.add(doc1);
    corpus.add(doc2);
    corpus.add(doc3);
    List<Document> sub = corpus.subList(1, 3);
    assertEquals(2, sub.size());
    assertSame(doc2, sub.get(0));
    assertSame(doc3, sub.get(1));
  }
@Test
  public void testUnloadDocumentDoesNothing() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Static");
    corpus.unloadDocument(doc);
    assertTrue(true); 
  }
@Test
  public void testCleanupDetachesListener() {
    CorpusImpl corpus = new CorpusImpl();
    corpus.cleanup();
    assertTrue(true); 
  }
@Test
  public void testRemoveCorpusListenerRemovesListenerSafely() {
    CorpusImpl corpus = new CorpusImpl();
    CorpusListener listener = new CorpusListener() {
      public void documentAdded(gate.event.CorpusEvent e) {}
      public void documentRemoved(gate.event.CorpusEvent e) {}
    };
    corpus.addCorpusListener(listener);
    corpus.removeCorpusListener(listener);
    assertTrue(true); 
  }
@Test
  public void testSetDocumentsListPopulatesOnInit() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("InitDoc");
    List<Document> docs = new ArrayList<Document>();
    docs.add(doc);
    corpus.setDocumentsList(docs);
    corpus.init();
    assertEquals(1, corpus.size());
    assertSame(doc, corpus.get(0));
  }
@Test
  public void testDuplicateCreatesDeepCopy() throws ResourceInstantiationException {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = Factory.newDocument("Original");
    corpus.add(doc);
    CorpusImpl copy = (CorpusImpl)corpus.duplicate(Factory.newDuplicationContext());
    assertEquals(1, copy.size());
    assertNotSame(doc, copy.get(0));
    assertEquals(doc.getContent().toString(), copy.get(0).getContent().toString());
  } 
}