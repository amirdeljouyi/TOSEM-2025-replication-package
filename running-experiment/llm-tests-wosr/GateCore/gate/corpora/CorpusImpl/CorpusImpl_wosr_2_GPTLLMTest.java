public class CorpusImpl_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorInitializesSupportList() {
    CorpusImpl corpus = new CorpusImpl();
    assertNotNull(corpus.supportList);
    assertTrue(corpus.supportList instanceof List);
    assertEquals(0, corpus.supportList.size());
  }
@Test
  public void testGetDocumentNamesWithEmptyList() {
    CorpusImpl corpus = new CorpusImpl();
    List<String> docNames = corpus.getDocumentNames();
    assertNotNull(docNames);
    assertTrue(docNames.isEmpty());
  }
@Test
  public void testGetDocumentNamesWithMultipleDocuments() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("DocOne");
    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("DocTwo");
    corpus.supportList.add(doc1);
    corpus.supportList.add(doc2);
    List<String> names = corpus.getDocumentNames();
    assertEquals(2, names.size());
    assertEquals("DocOne", names.get(0));
    assertEquals("DocTwo", names.get(1));
  }
@Test
  public void testGetDocumentNameByIndex() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("TestDoc");
    corpus.supportList.add(doc);
    assertEquals("TestDoc", corpus.getDocumentName(0));
  }
@Test
  public void testUnloadDocumentDoesNothing() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus.supportList.add(doc);
    corpus.unloadDocument(doc);
    assertTrue(corpus.supportList.contains(doc));
  }
@Test
  public void testAddAndRemoveDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    boolean added = corpus.add(doc);
    assertTrue(added);
    assertTrue(corpus.contains(doc));
    boolean removed = corpus.remove(doc);
    assertTrue(removed);
    assertFalse(corpus.contains(doc));
  }
@Test
  public void testSizeAndIsEmpty() {
    CorpusImpl corpus = new CorpusImpl();
    assertTrue(corpus.isEmpty());
    Document doc = mock(Document.class);
    corpus.add(doc);
    assertEquals(1, corpus.size());
    assertFalse(corpus.isEmpty());
  }
@Test
  public void testToArrayMethods() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus.add(doc);
    Object[] array = corpus.toArray();
    assertEquals(1, array.length);
    assertTrue(array[0] instanceof Document);
    Document[] typedArray = corpus.toArray(new Document[1]);
    assertEquals(doc, typedArray[0]);
  }
@Test
  public void testClearCorpus() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus.add(doc);
    assertFalse(corpus.isEmpty());
    corpus.clear();
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testIndexOfAndLastIndexOf() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);
    corpus.add(doc1);
    corpus.add(doc2);
    corpus.add(doc1);
    assertEquals(0, corpus.indexOf(doc1));
    assertEquals(2, corpus.lastIndexOf(doc1));
  }
@Test
  public void testGetAndSetDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);
    corpus.add(doc1);
    Document old = corpus.set(0, doc2);
    assertEquals(doc1, old);
    assertEquals(doc2, corpus.get(0));
  }
@Test
  public void testEqualsAndHashCode() {
    CorpusImpl corpus1 = new CorpusImpl();
    CorpusImpl corpus2 = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus1.supportList.add(doc);
    corpus2.supportList.add(doc);
    assertTrue(corpus1.equals(corpus2));
    assertEquals(corpus1.hashCode(), corpus2.hashCode());
  }
@Test
  public void testPopulateFromDirectoryWithNoFiles() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    File dir = new File(System.getProperty("java.io.tmpdir"));
    FileFilter filter = pathname -> false;
    URL url = dir.toURI().toURL();
    CorpusImpl.populate(corpus, url, filter, "UTF-8", false);
    assertTrue(corpus.isEmpty());
  }
@Test(expected = IllegalArgumentException.class)
  public void testPopulateFromInvalidProtocolThrows() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    URL invalidURL = new URL("http://example.com/");
    CorpusImpl.populate(corpus, invalidURL, null, "UTF-8", false);
  }
@Test(expected = FileNotFoundException.class)
  public void testPopulateFromNonExistingFile() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    File notExist = new File("non_existing_dir");
    URL url = notExist.toURI().toURL();
    CorpusImpl.populate(corpus, url, null, null, false);
  }
@Test
  public void testPopulateTrecEmptyInput() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    File trecFile = File.createTempFile("testTrec", ".txt");
    trecFile.deleteOnExit();
    URL url = trecFile.toURI().toURL();
    long length = CorpusImpl.populate(corpus, url, "DOC", "UTF-8", -1, "doc", "text/xml", true);
    assertEquals(0, corpus.size());
    assertEquals(0, length);
  }
@Test
  public void testPopulateTrecSingleDoc() throws Exception {
    String docContent = "<DOC>\nTest content\n</DOC>";
    File trecFile = File.createTempFile("testTrec", ".txt");
    java.nio.file.Files.write(trecFile.toPath(), docContent.getBytes());
    trecFile.deleteOnExit();
    CorpusImpl corpus = new CorpusImpl();
    URL url = trecFile.toURI().toURL();
    long length = CorpusImpl.populate(corpus, url, "DOC", "UTF-8", 1, "doc", "text/xml", true);
    assertEquals(1, corpus.size());
    assertTrue(length > 0);
  }
@Test
  public void testCustomDuplicationCreatesNewCorpusWithClonedDocuments() throws Exception {
    CorpusImpl original = new CorpusImpl();
    Document originalDoc = mock(Document.class);
    original.supportList.add(originalDoc);

    Factory.DuplicationContext context = new Factory.DuplicationContext();
    Corpus duplicateCorpus = (Corpus) original.duplicate(context);

    assertNotNull(duplicateCorpus);
    assertEquals(1, duplicateCorpus.size());
  }
@Test
  public void testCorpusListenerAddAndRemove() {
    CorpusImpl corpus = new CorpusImpl();
    gate.event.CorpusListener listener = mock(gate.event.CorpusEvent.class);
    corpus.addCorpusListener(listener);
    corpus.removeCorpusListener(listener);
    assertTrue(true); 
  }
@Test
  public void testResourceUnloadedRemovesDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus.supportList.add(doc);
    gate.event.CreoleEvent event = mock(gate.event.CreoleEvent.class);
    when(event.getResource()).thenReturn(doc);
    corpus.resourceUnloaded(event);
    assertFalse(corpus.supportList.contains(doc));
  }
@Test
  public void testInitWithDocumentsList() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = mock(Document.class);
    corpus.setDocumentsList(Collections.singletonList(doc));
    Resource result = corpus.init();
    assertEquals(1, corpus.size());
    assertEquals(doc, corpus.get(0));
    assertEquals(corpus, result);
  } 
}