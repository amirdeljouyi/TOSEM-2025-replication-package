public class CorpusImpl_wosr_3_GPTLLMTest { 

 @Test
  public void testCorpusInitializationCreatesEmptyList() {
    CorpusImpl corpus = new CorpusImpl();
    assertNotNull(corpus);
    assertTrue(corpus.isEmpty());
    assertEquals(0, corpus.size());
  }
@Test
  public void testAddAndGetDocument() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Hello Corpus");
    Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
    boolean added = corpus.add(doc);
    assertTrue(added);
    assertEquals(1, corpus.size());
    assertEquals(doc, corpus.get(0));
  }
@Test
  public void testRemoveDocumentByObject() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc to Remove");
    Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
    corpus.add(doc);
    assertEquals(1, corpus.size());
    boolean removed = corpus.remove(doc);
    assertTrue(removed);
    assertEquals(0, corpus.size());
  }
@Test
  public void testRemoveDocumentByIndex() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc X");
    Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
    corpus.add(doc);
    Document removed = corpus.remove(0);
    assertEquals(doc, removed);
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testGetDocumentNameByIndex() throws Exception {
    CorpusImpl corpus = new CorpusImpl();
    String testName = "TestDoc_" + System.currentTimeMillis();
    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Named Content");
    Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, testName);
    corpus.add(doc);
    assertEquals(testName, corpus.getDocumentName(0));
  }
@Test
  public void testGetDocumentNamesList() throws Exception {
    CorpusImpl corpus = new CorpusImpl();

    FeatureMap params1 = Factory.newFeatureMap();
    FeatureMap params2 = Factory.newFeatureMap();
    params1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc 1");
    params2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc 2");

    String name1 = "Doc1_" + System.nanoTime();
    String name2 = "Doc2_" + System.nanoTime();
    Document doc1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params1, null, name1);
    Document doc2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params2, null, name2);

    corpus.add(doc1);
    corpus.add(doc2);

    List<String> names = corpus.getDocumentNames();
    assertEquals(2, names.size());
    assertTrue(names.contains(name1));
    assertTrue(names.contains(name2));
  }
@Test
  public void testSetDocumentsListViaInit() throws Exception {
    CorpusImpl corpus = new CorpusImpl();

    FeatureMap params1 = Factory.newFeatureMap();
    FeatureMap params2 = Factory.newFeatureMap();
    params1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Alpha");
    params2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Beta");

    Document doc1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params1);
    Document doc2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params2);

    List<Document> docs = new ArrayList<>();
    docs.add(doc1);
    docs.add(doc2);

    corpus.setDocumentsList(docs);
    corpus.init();

    assertEquals(2, corpus.size());
    assertEquals(doc1, corpus.get(0));
    assertEquals(doc2, corpus.get(1));
  }
@Test
  public void testIsDocumentLoadedAlwaysTrue() throws Exception {
    CorpusImpl corpus = new CorpusImpl();

    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Loaded Check");
    Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
    corpus.add(doc);

    assertTrue(corpus.isDocumentLoaded(0));
  }
@Test
  public void testEqualsAndHashCode() throws Exception {
    CorpusImpl corpusA = new CorpusImpl();
    CorpusImpl corpusB = new CorpusImpl();

    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Same Content");
    Document doc1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
    Document doc2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);

    corpusA.add(doc1);
    corpusB.add(doc1);

    assertTrue(corpusA.equals(corpusB));
    assertEquals(corpusA.hashCode(), corpusB.hashCode());

    corpusB.remove(doc1);
    corpusB.add(doc2);

    assertFalse(corpusA.equals(corpusB));
  }
@Test
  public void testPopulateThrowsForInvalidURL() {
    try {
      CorpusImpl corpus = new CorpusImpl();
      URL dummyUrl = new URL("http://invalid-protocol.com");
      CorpusImpl.populate(corpus, dummyUrl, null, "UTF-8", false);
      fail("Expected IllegalArgumentException due to non-file protocol");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("not of type"));
    } catch (Exception e) {
      fail("Expected IllegalArgumentException, got: " + e);
    }
  }
@Test
  public void testPopulateLocalFileStatic() throws Exception {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "gateTestCorpus");
    tempDir.mkdir();

    File f1 = new File(tempDir, "doc1.txt");
    File f2 = new File(tempDir, "doc2.txt");

    FileWriter fw1 = new FileWriter(f1);
    fw1.write("Doc 1 content");
    fw1.close();

    FileWriter fw2 = new FileWriter(f2);
    fw2.write("Doc 2 content");
    fw2.close();

    URL dirUrl = tempDir.toURI().toURL();
    CorpusImpl corpus = new CorpusImpl();

    try {
      CorpusImpl.populate(corpus, dirUrl, null, "UTF-8", false);
    } catch (Exception e) {
      fail("Populate failed: " + e.getMessage());
    }
    assertEquals(2, corpus.size());
    assertTrue(corpus.get(0).toString().length() > 0);
    assertTrue(corpus.get(1).toString().length() > 0);

    f1.delete();
    f2.delete();
    tempDir.delete();
  }
@Test
  public void testCleanupRemovesCreoleListener() {
    CorpusImpl corpus = new CorpusImpl();
    corpus.cleanup();
    
  }
@Test
  public void testClearCorpus() throws Exception {
    CorpusImpl corpus = new CorpusImpl();

    FeatureMap params = Factory.newFeatureMap();
    params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Some Content");
    Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
    corpus.add(doc);
    assertEquals(1, corpus.size());

    corpus.clear();
    assertTrue(corpus.isEmpty());
  } 
}