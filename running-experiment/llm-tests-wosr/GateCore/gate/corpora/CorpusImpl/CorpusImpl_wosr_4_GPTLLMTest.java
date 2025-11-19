public class CorpusImpl_wosr_4_GPTLLMTest { 

 @Test
  public void testAddAndGetDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = new DummyDocument("testDoc1");
    corpus.add(doc);
    assertEquals(1, corpus.size());
    assertEquals("testDoc1", corpus.get(0).getName());
  }
@Test
  public void testAddAtIndex() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = new DummyDocument("doc1");
    Document doc2 = new DummyDocument("doc2");
    corpus.add(doc1);
    corpus.add(0, doc2);
    assertEquals("doc2", corpus.get(0).getName());
    assertEquals("doc1", corpus.get(1).getName());
  }
@Test
  public void testSetDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc1 = new DummyDocument("doc1");
    Document doc2 = new DummyDocument("doc2");
    corpus.add(doc1);
    Document replaced = corpus.set(0, doc2);
    assertEquals("doc1", replaced.getName());
    assertEquals("doc2", corpus.get(0).getName());
  }
@Test
  public void testRemoveDocumentByIndex() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = new DummyDocument("toRemove");
    corpus.add(doc);
    Document removed = corpus.remove(0);
    assertEquals("toRemove", removed.getName());
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testContainsDocument() {
    CorpusImpl corpus = new CorpusImpl();
    Document doc = new DummyDocument("doc");
    corpus.add(doc);
    assertTrue(corpus.contains(doc));
  }
@Test
  public void testClearCorpus() {
    CorpusImpl corpus = new CorpusImpl();
    corpus.add(new DummyDocument("doc1"));
    corpus.add(new DummyDocument("doc2"));
    corpus.clear();
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testGetDocumentNameByIndex() {
    CorpusImpl corpus = new CorpusImpl();
    corpus.add(new DummyDocument("docX"));
    String name = corpus.getDocumentName(0);
    assertEquals("docX", name);
  }
@Test
  public void testGetDocumentNames() {
    CorpusImpl corpus = new CorpusImpl();
    corpus.add(new DummyDocument("A"));
    corpus.add(new DummyDocument("B"));
    List<String> names = corpus.getDocumentNames();
    assertEquals(2, names.size());
    assertEquals("A", names.get(0));
    assertEquals("B", names.get(1));
  }
@Test
  public void testPopulateDirectoryWithNullFilterNonRecursive() throws Exception {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "corpusTest");
    if (!tempDir.exists()) tempDir.mkdirs();
    File file1 = new File(tempDir, "test1.txt");
    java.nio.file.Files.write(file1.toPath(), "hello".getBytes(StandardCharsets.UTF_8));
    CorpusImpl corpus = new CorpusImpl();
    URL dirUrl = tempDir.toURI().toURL();
    CorpusImpl.populate(corpus, dirUrl, null, "UTF-8", false);
    assertEquals(1, corpus.size());
    assertTrue(corpus.get(0).getName().startsWith("test1.txt"));
    assertTrue(corpus.get(0).getContent().toString().contains("hello"));
    for (int i = 0; i < corpus.size(); i++) Factory.deleteResource(corpus.get(i));
  }
@Test(expected = IllegalArgumentException.class)
  public void testPopulateWithInvalidProtocol() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    URL invalidUrl = new URL("http://localhost/path/to/fake");
    CorpusImpl.populate(corpus, invalidUrl, null, "UTF-8", false);
  }
@Test(expected = IOException.class)
  public void testPopulateWithNonExistentDir() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    File nonExist = new File("fake" + System.nanoTime());
    URL url = nonExist.toURI().toURL();
    CorpusImpl.populate(corpus, url, null, "UTF-8", false);
  }
@Test
  public void testIsDocumentLoadedAlwaysTrue() {
    CorpusImpl corpus = new CorpusImpl();
    corpus.add(new DummyDocument("doc"));
    assertTrue(corpus.isDocumentLoaded(0));
  }
@Test
  public void testEqualsAndHashCode() {
    CorpusImpl corpus1 = new CorpusImpl();
    CorpusImpl corpus2 = new CorpusImpl();
    Document doc = new DummyDocument("eqDoc");
    corpus1.add(doc);
    corpus2.add(doc);
    assertEquals(corpus1, corpus2);
    assertEquals(corpus1.hashCode(), corpus2.hashCode());
  }
@Test
  public void testNotEqualsWithDifferentType() {
    CorpusImpl corpus = new CorpusImpl();
    assertFalse(corpus.equals(new Object()));
  }
@Test
  public void testInitWithPreloadedDocuments() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    List<Document> docs = new ArrayList<>();
    docs.add(new DummyDocument("docInit"));
    corpus.setDocumentsList(docs);
    Resource res = corpus.init();
    assertEquals(1, corpus.size());
    assertSame(corpus, res);
  }
@Test
  public void testPopulateFromConcatenatedFile() throws Exception {
    String trecData = "<DOC>\nhello</DOC>\n<DOC>world</DOC>";
    InputStream is = new ByteArrayInputStream(trecData.getBytes(StandardCharsets.UTF_8));
    File trecFile = File.createTempFile("trec", ".txt");
    java.nio.file.Files.copy(is, trecFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    CorpusImpl corpus = new CorpusImpl();
    long length = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "DOC", "UTF-8", -1, "prefix", "text/plain", true);
    assertEquals(2, corpus.size());
    assertTrue(length > 0);
    assertTrue(corpus.get(0).getContent().toString().contains("hello"));
    assertTrue(corpus.get(1).getName().startsWith("prefix"));
    for (int i = 0; i < corpus.size(); i++) Factory.deleteResource(corpus.get(i));
  }
@Test
  public void testDuplicateCorpus() throws Exception {
    CorpusImpl original = new CorpusImpl();
    Document doc = Factory.newDocument("sample content");
    doc.setName("OriginalDoc");
    original.add(doc);
    Factory.DuplicationContext ctx = new Factory.DuplicationContext();
    CorpusImpl duplicated = (CorpusImpl)original.duplicate(ctx);
    assertEquals(1, duplicated.size());
    assertEquals("OriginalDoc", duplicated.get(0).getName());
    Factory.deleteResource(doc);
    Factory.deleteResource(duplicated.get(0));
  } 
}