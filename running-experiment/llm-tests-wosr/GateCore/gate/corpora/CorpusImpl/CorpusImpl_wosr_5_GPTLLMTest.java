public class CorpusImpl_wosr_5_GPTLLMTest { 

 @Test
  public void testAddAndGetDocumentName() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("testDoc");

    corpus.add(doc);

    assertEquals(1, corpus.size());
    assertEquals("testDoc", corpus.getDocumentName(0));
    assertEquals("testDoc", corpus.getDocumentNames().get(0));
  }
@Test
  public void testSetAndRemoveDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc2.getName()).thenReturn("doc2");

    corpus.add(doc1);
    Document previous = corpus.set(0, doc2);

    assertEquals(doc1, previous);
    assertEquals("doc2", corpus.getDocumentName(0));
    assertEquals(doc2, corpus.remove(0));
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testContainsAndIndexOf() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("alpha");

    corpus.add(doc);

    assertTrue(corpus.contains(doc));
    assertEquals(0, corpus.indexOf(doc));
    assertEquals(0, corpus.lastIndexOf(doc));
  }
@Test
  public void testClearCorpus() {
    CorpusImpl corpus = new CorpusImpl();
    Document docA = mock(Document.class);
    Document docB = mock(Document.class);

    corpus.add(docA);
    corpus.add(docB);

    assertEquals(2, corpus.size());
    corpus.clear();
    assertEquals(0, corpus.size());
  }
@Test
  public void testUnloadDocumentDoesNothing() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus.unloadDocument(doc); 
  }
@Test
  public void testIsDocumentLoadedAlwaysTrue() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus.add(doc);

    assertTrue(corpus.isDocumentLoaded(0));
  }
@Test
  public void testEqualsAndHashCodeWithSameDocuments() {
    CorpusImpl corpusA = new CorpusImpl();
    CorpusImpl corpusB = new CorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("unique");

    corpusA.add(doc);
    corpusB.add(doc);

    assertTrue(corpusA.equals(corpusB));
    assertEquals(corpusA.hashCode(), corpusB.hashCode());
  }
@Test
  public void testVerboseListFiresEvents() {
    CorpusImpl corpus = new CorpusImpl();

    CorpusListener listener = mock(CorpusListener.class);
    corpus.addCorpusListener(listener);

    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc2.getName()).thenReturn("doc2");

    corpus.add(doc1);
    verify(listener, times(1)).documentAdded(any(CorpusEvent.class));

    corpus.set(0, doc2);
    verify(listener, times(1)).documentRemoved(any(CorpusEvent.class));
    verify(listener, times(2)).documentAdded(any(CorpusEvent.class));

    corpus.remove(0);
    verify(listener, times(2)).documentRemoved(any(CorpusEvent.class));
  }
@Test
  public void testAddAndRemoveCorpusListener() {
    CorpusImpl corpus = new CorpusImpl();
    CorpusListener listener = mock(CorpusListener.class);

    corpus.addCorpusListener(listener);
    corpus.removeCorpusListener(listener);
  }
@Test
  public void testGetToArrayMethods() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);

    corpus.add(doc1);
    corpus.add(doc2);

    Object[] arr = corpus.toArray();
    assertEquals(2, arr.length);
    assertEquals(doc1, arr[0]);
    assertEquals(doc2, arr[1]);

    Document[] arr2 = corpus.toArray(new Document[2]);
    assertEquals(doc1, arr2[0]);
    assertEquals(doc2, arr2[1]);
  }
@Test
  public void testAddAllAndContainsAll() {
    CorpusImpl corpus = new CorpusImpl();
    List<Document> docs = new ArrayList<Document>();
    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);
    docs.add(doc1);
    docs.add(doc2);

    corpus.addAll(docs);

    assertEquals(2, corpus.size());
    assertTrue(corpus.containsAll(docs));
  }
@Test
  public void testRemoveAllAndRetainAll() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);
    Document doc3 = mock(Document.class);

    corpus.add(doc1);
    corpus.add(doc2);
    corpus.add(doc3);

    List<Document> removeList = new ArrayList<Document>();
    removeList.add(doc2);

    corpus.removeAll(removeList);
    assertEquals(2, corpus.size());
    assertFalse(corpus.contains(doc2));

    List<Document> retainList = new ArrayList<Document>();
    retainList.add(doc1);
    corpus.retainAll(retainList);
    assertEquals(1, corpus.size());
    assertTrue(corpus.contains(doc1));
    assertFalse(corpus.contains(doc3));
  }
@Test
  public void testCleanupRemovesCreoleListener() {
    CorpusImpl corpus = new CorpusImpl();
    corpus.cleanup(); 
  }
@Test(expected = IllegalArgumentException.class)
  public void testPopulateThrowsOnInvalidUrlProtocol() throws Exception {
    URL url = new URL("http://example.com");
    FileFilter filter = null;
    String encoding = "UTF-8";

    Corpus corpus = mock(Corpus.class);
    CorpusImpl.populate(corpus, url, filter, encoding, false);
  }
@Test(expected = FileNotFoundException.class)
  public void testPopulateThrowsOnMissingDirectory() throws Exception {
    URL url = new File("non_existent_directory").toURI().toURL();
    FileFilter filter = null;
    String encoding = "UTF-8";

    Corpus corpus = mock(Corpus.class);
    CorpusImpl.populate(corpus, url, filter, encoding, false);
  }
@Test
  public void testSetAndGetDocumentsList() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    List<Document> list = new ArrayList<Document>();
    list.add(doc);

    corpus.setDocumentsList(list);
    assertEquals(1, corpus.getDocumentsList().size());
    assertEquals(doc, corpus.getDocumentsList().get(0));
  }
@Test
  public void testInitWithPrePopulatedDocumentsList() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    List<Document> docs = new ArrayList<Document>();
    docs.add(doc);

    corpus.setDocumentsList(docs);
    Resource res = corpus.init();

    assertEquals(CorpusImpl.class, res.getClass());
    assertEquals(1, corpus.size());
    assertEquals(doc, corpus.get(0));
  }
@Test
  public void testResourceUnloadedRemovesDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);

    corpus.add(doc);
    assertTrue(corpus.contains(doc));

    corpus.resourceUnloaded(new CorpusEvent(corpus, doc, 0, CorpusEvent.DOCUMENT_REMOVED));

    assertFalse(corpus.contains(doc));
  }
@Test
  public void testDuplicateCorpusWithDocuments() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    Document originalDoc = mock(Document.class);
    Document duplicatedDoc = mock(Document.class);
    Factory.DuplicationContext context = mock(Factory.DuplicationContext.class);

    corpus.add(originalDoc);

    CorpusImpl duplicateCorpus = new CorpusImpl();
    when(Factory.defaultDuplicate(corpus, context)).thenReturn(duplicateCorpus);
    when(Factory.duplicate(originalDoc, context)).thenReturn(duplicatedDoc);

    Resource result = corpus.duplicate(context);

    assertTrue(result instanceof CorpusImpl);
    assertEquals(1, ((CorpusImpl)result).size());
    assertEquals(duplicatedDoc, ((CorpusImpl)result).get(0));
  } 
}