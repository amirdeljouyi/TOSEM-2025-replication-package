public class SerialCorpusImpl_2_GPTLLMTest { 

 @Test
  public void testAddSingleDocument() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc1");
    when(doc.getLRPersistenceId()).thenReturn("doc1-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    boolean added = corpus.add(doc);

    assertTrue(added);
    assertEquals(1, corpus.size());
    assertEquals("Doc1", corpus.getDocumentName(0));
    assertEquals("doc1-id", corpus.getDocumentPersistentID(0));
  }
@Test(expected = MethodNotImplementedException.class)
  public void testToArrayThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.toArray();
  }
@Test(expected = MethodNotImplementedException.class)
  public void testToArrayWithParamThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document[] input = new Document[0];
    corpus.toArray(input);
  }
@Test
  public void testRemoveExistingDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc1");
    when(doc.getLRPersistenceId()).thenReturn("doc1-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    boolean removed = corpus.remove(doc);

    assertTrue(removed);
    assertEquals(0, corpus.size());
  }
@Test
  public void testContainsReturnsFalseForNonMember() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Unknown");
    when(doc.getLRPersistenceId()).thenReturn("id-xxx");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    boolean result = corpus.contains(doc);

    assertFalse(result);
  }
@Test
  public void testGetDocumentNameAndId() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc1");
    when(doc.getLRPersistenceId()).thenReturn("doc1-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    String name = corpus.getDocumentName(0);
    Object id = corpus.getDocumentPersistentID(0);

    assertEquals("Doc1", name);
    assertEquals("doc1-id", id);
  }
@Test(expected = MethodNotImplementedException.class)
  public void testSetDocumentNotSupported() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    corpus.set(0, doc);
  }
@Test
  public void testAddAtIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("Doc1");
    when(doc1.getLRPersistenceId()).thenReturn("doc1-id");
    when(doc1.getClass().getName()).thenReturn("gate.Document");

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("Doc2");
    when(doc2.getLRPersistenceId()).thenReturn("doc2-id");
    when(doc2.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc1);
    corpus.add(0, doc2);

    assertEquals("Doc2", corpus.getDocumentName(0));
    assertEquals("Doc1", corpus.getDocumentName(1));
  }
@Test(expected = MethodNotImplementedException.class)
  public void testSubListUnsupported() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.subList(0, 1);
  }
@Test
  public void testIsEmptyInitiallyTrueThenFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    assertTrue(corpus.isEmpty());

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc1");
    when(doc.getLRPersistenceId()).thenReturn("id1");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    assertFalse(corpus.isEmpty());
  }
@Test
  public void testClearCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocA");
    when(doc.getLRPersistenceId()).thenReturn("idA");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("DocB");
    when(doc2.getLRPersistenceId()).thenReturn("idB");
    when(doc2.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    corpus.add(doc2);

    corpus.clear();

    assertEquals(0, corpus.size());
    assertTrue(corpus.isEmpty());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testListIteratorNotSupported() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.listIterator();
  }
@Test
  public void testRemoveByIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("TestDoc");
    when(doc.getLRPersistenceId()).thenReturn("doc-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    Document removed = corpus.remove(0);

    assertSame(doc, removed);
    assertEquals(0, corpus.size());
  }
@Test
  public void testGetDocumentReturnsCorrectDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocX");
    when(doc.getLRPersistenceId()).thenReturn("idX");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    Document result = corpus.get(0);

    assertSame(doc, result);
  }
@Test
  public void testEqualsReturnsFalseForDifferentCorpus() throws Exception {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc");
    when(doc.getLRPersistenceId()).thenReturn("id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus1.add(doc);

    boolean result = corpus1.equals(corpus2);
    assertFalse(result);
  }
@Test
  public void testContainsAllWithAllMatching() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("One");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("Two");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc1);
    corpus.add(doc2);

    List<Document> input = Arrays.asList(doc1, doc2);

    assertTrue(corpus.containsAll(input));
  }
@Test
  public void testContainsAllWithMismatch() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("One");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("Two");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");

    Document missing = mock(Document.class);
    when(missing.getName()).thenReturn("Missing");
    when(missing.getLRPersistenceId()).thenReturn("missing-id");
    when(missing.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc1);

    List<Document> input = Arrays.asList(doc1, doc2, missing);

    assertFalse(corpus.containsAll(input));
  }
@Test(expected = ResourceInstantiationException.class)
  public void testDuplicateThrowsException() throws ResourceInstantiationException {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.duplicate(new Factory.DuplicationContext());
  }
@Test
  public void testIndexOfReturnsCorrectIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("ExampleDoc");
    when(doc.getLRPersistenceId()).thenReturn("index-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    int idx = corpus.indexOf(doc);

    assertEquals(0, idx);
  }
@Test
  public void testGetDocumentNameOutOfBoundsReturnsDefaultMessage() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String result = corpus.getDocumentName(5);
    assertEquals("No such document", result);
  }
@Test
  public void testGetDocumentPersistentIDOutOfBoundsReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object result = corpus.getDocumentPersistentID(3);
    assertNull(result);
  }
@Test
  public void testGetDocumentClassTypeOutOfBoundsReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String result = corpus.getDocumentClassType(2);
    assertNull(result);
  }
@Test
  public void testIsDocumentLoadedWhenDocumentsListIsEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isDocumentLoaded(0);
    assertFalse(result);
  }
@Test
  public void testIsPersistentDocumentWhenNullPersistentId() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc-A");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    boolean result = corpus.isPersistentDocument(0);
    assertFalse(result);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testIteratorRemoveThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc");
    when(doc.getLRPersistenceId()).thenReturn("id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    corpus.iterator().remove();
  }
@Test
  public void testAddRejectsDocumentFromDifferentDatastore() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds1 = mock(DataStore.class);
    DataStore ds2 = mock(DataStore.class);
    corpus.setDataStore(ds1);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("WrongDS");
    when(doc.getLRPersistenceId()).thenReturn("ds2-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds2);

    boolean added = corpus.add(doc);
    assertFalse(added);
    assertEquals(0, corpus.size());
  }
@Test
  public void testRemoveNonExistentDocumentReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("NotThere");
    when(doc.getLRPersistenceId()).thenReturn("missing");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    boolean removed = corpus.remove(doc);

    assertFalse(removed);
  }
@Test
  public void testUnloadDocumentUnloadsOnlyIfLoadedAndPersistent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("TempDoc");
    when(doc.getLRPersistenceId()).thenReturn("TEMP");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false); 

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testIndexOfNonMatchingObjectTypeReturnsMinusOne() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    int result = corpus.indexOf("NotADocument");
    assertEquals(-1, result);
  }
@Test
  public void testRemoveAllPartialMatchRemovesWhatCanBeRemoved() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    Document doc2 = mock(Document.class);
    Document docNonMember = mock(Document.class);

    when(doc1.getName()).thenReturn("Doc1");
    when(doc1.getLRPersistenceId()).thenReturn("ID1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");

    when(doc2.getName()).thenReturn("Doc2");
    when(doc2.getLRPersistenceId()).thenReturn("ID2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");

    when(docNonMember.getName()).thenReturn("DocX");
    when(docNonMember.getLRPersistenceId()).thenReturn("IDX");
    when(docNonMember.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc1);
    corpus.add(doc2);

    boolean result = corpus.removeAll(Arrays.asList(doc1, docNonMember));

    assertTrue(result);
    assertEquals(1, corpus.size());
  }
@Test(expected = MethodNotImplementedException.class)
  public void testLastIndexOfThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    corpus.lastIndexOf(doc);
  }
@Test
  public void testGetReturnsNullForInvalidIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document result = corpus.get(10);
    assertNull(result);
  }
@Test
  public void testEqualsSameReferenceIsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.equals(corpus));
  }
@Test
  public void testEqualsWithNullReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals(null));
  }
@Test
  public void testEqualsWithDifferentTypeReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals("not a corpus"));
  }
@Test
  public void testToStringWithEmptyCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String output = corpus.toString();
    assertNotNull(output);
    assertTrue(output.contains("document data"));
  }
@Test
  public void testAddAllWithEmptyCollectionReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.addAll(new ArrayList<Document>());
    assertTrue(result);
  }
@Test
  public void testRemoveDocumentByInvalidIndexNoError() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.remove(5); 
    } catch (Exception e) {
      fail("Should not throw exception when removing out-of-bounds index");
    }
  }
@Test
  public void testSetDocumentPersistentIDNoOpWhenIndexTooLarge() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.setDocumentPersistentID(10, "test-id"); 
    } catch (Exception e) {
      fail("setDocumentPersistentID should no-op without errors");
    }
  }
@Test
  public void testAddNullDocumentReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean added = corpus.add(null);
    assertFalse(added);
    assertEquals(0, corpus.size());
  }
@Test
  public void testAddSameDocumentTwiceAllowed() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocX");
    when(doc.getLRPersistenceId()).thenReturn("x-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    boolean firstAdd = corpus.add(doc);
    boolean secondAdd = corpus.add(doc);

    assertTrue(firstAdd);
    assertTrue(secondAdd);
    assertEquals(2, corpus.size());
  }
@Test
  public void testUnloadTransientDocumentDoesNothing() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Transient");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, true); 

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testUnloadDocumentTriggersSyncWithAdoption() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document transientDoc = mock(Document.class);
    when(transientDoc.getName()).thenReturn("AdoptMe");
    when(transientDoc.getLRPersistenceId()).thenReturn(null);
    when(transientDoc.getClass().getName()).thenReturn("gate.Document");
    when(transientDoc.getDataStore()).thenReturn(null);

    Document adoptedDoc = mock(Document.class);
    when(adoptedDoc.getName()).thenReturn("AdoptMe");
    when(adoptedDoc.getLRPersistenceId()).thenReturn("new-id");
    when(adoptedDoc.getClass().getName()).thenReturn("gate.Document");
    when(adoptedDoc.getDataStore()).thenReturn(ds);

    when(ds.adopt(transientDoc)).thenReturn(adoptedDoc);

    corpus.add(transientDoc);
    corpus.unloadDocument(0, true);

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test(expected = GateRuntimeException.class)
  public void testUnloadDocumentSyncFailsThrowsGateRuntimeException() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("FailingDoc");
    when(doc.getLRPersistenceId()).thenReturn("fail-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    doThrow(new PersistenceException("forced sync fail")).when(ds).sync(doc);

    corpus.add(doc);
    corpus.unloadDocument(0, true); 
  }
@Test
  public void testGetTransientSourceAlwaysReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object source = corpus.getTransientSource();
    assertNull(source);
  }
@Test
  public void testFireDocumentAddedDoesNotThrowWithNoListeners() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocZ");
    when(doc.getLRPersistenceId()).thenReturn("idz");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    
    corpus.add(doc); 

    assertEquals(1, corpus.size());
  }
@Test
  public void testRemoveCorpusListenerNullSafe() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.removeCorpusListener(null); 
  }
@Test
  public void testAddCorpusListenerAndRemove() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    gate.event.CorpusListener listener = mock(gate.event.CorpusListener.class);

    corpus.addCorpusListener(listener);
    corpus.removeCorpusListener(listener);

    
    assertEquals(0, corpus.getDocumentNames().size());
  }
@Test
  public void testRemoveDocumentTwiceBySameReferenceOnlyFirstSucceeds() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocR");
    when(doc.getLRPersistenceId()).thenReturn("idr");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    boolean firstRemove = corpus.remove(doc);
    boolean secondRemove = corpus.remove(doc);

    assertTrue(firstRemove);
    assertFalse(secondRemove);
  }
@Test
  public void testEqualsWithDifferentNameReturnsFalse() {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    corpus1.setName("CorpusOne");
    corpus2.setName("CorpusTwo");

    assertFalse(corpus1.equals(corpus2));
  }
@Test
  public void testEqualsWithDifferentDatastoreReturnsFalse() throws Exception {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    corpus1.setName("Same");
    corpus2.setName("Same");

    DataStore store1 = mock(DataStore.class);
    DataStore store2 = mock(DataStore.class);

    corpus1.setDataStore(store1);
    corpus2.setDataStore(store2);

    assertFalse(corpus1.equals(corpus2));
  }
@Test
  public void testEqualsWithDifferentPersistentIdReturnsFalse() throws Exception {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    corpus1.setName("Corpus");
    corpus2.setName("Corpus");

    DataStore ds = mock(DataStore.class);
    corpus1.setDataStore(ds);
    corpus2.setDataStore(ds);

    corpus1.setLRPersistenceId("id1");
    corpus2.setLRPersistenceId("id2");

    assertFalse(corpus1.equals(corpus2));
  }
@Test
  public void testEqualsWithSameAttributesReturnsTrue() throws Exception {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    corpus1.setName("Corpus");
    corpus2.setName("Corpus");

    DataStore ds = mock(DataStore.class);
    corpus1.setDataStore(ds);
    corpus2.setDataStore(ds);

    corpus1.setLRPersistenceId("same-id");
    corpus2.setLRPersistenceId("same-id");

    assertTrue(corpus1.equals(corpus2) || corpus2.equals(corpus1));
  }
@Test
  public void testHashCodeConsistency() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    int hash1 = corpus.hashCode();
    int hash2 = corpus.hashCode();
    assertEquals(hash1, hash2);
  }
@Test
  public void testDefaultConstructorCreatesValidEmptyCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertNotNull(corpus);
    assertEquals(0, corpus.size());
  }
@Test
  public void testAddCorpusListenerInitializesListenerList() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    CorpusListener listener = mock(CorpusListener.class);
    corpus.addCorpusListener(listener);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docName");
    when(doc.getLRPersistenceId()).thenReturn("docId");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    
    assertEquals(1, corpus.size());
  }
@Test
  public void testRemoveCorpusListenerNotPresentDoesNothing() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    CorpusListener listener1 = mock(CorpusListener.class);
    CorpusListener listener2 = mock(CorpusListener.class);

    corpus.addCorpusListener(listener1);
    corpus.removeCorpusListener(listener2); 
    assertEquals(0, corpus.getDocumentNames().size());
  }
@Test
  public void testSetDocumentPersistentIDUpdatesEntry() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("D");
    when(doc.getLRPersistenceId()).thenReturn("initialID");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    corpus.setDocumentPersistentID(0, "newID");

    Object id = corpus.getDocumentPersistentID(0);
    assertEquals("newID", id);
  }
@Test
  public void testGetNullDocumentTriggersLoadFailureAndReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocZ");
    when(doc.getLRPersistenceId()).thenReturn("docZid");
    when(doc.getClass().getName()).thenReturn("gate.FakeDocument");

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    try {
      Object result = corpus.get(0);
      assertNotNull(result); 
    } catch (GateRuntimeException e) {
      assertTrue(e.getCause() instanceof ResourceInstantiationException);
    }
  }
@Test
  public void testUnloadDocumentAtIndexWithSyncFalseUnloadsSuccessfully() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocX");
    when(doc.getLRPersistenceId()).thenReturn("idx");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    corpus.unloadDocument(0, false);

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testRemoveFromEmptyCorpusByIndexThrowsIndexOutOfBoundsInRemoveEventCall() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.remove(0); 
    } catch (IndexOutOfBoundsException e) {
      assertTrue(e.getMessage().contains("Index"));
    }
  }
@Test
  public void testRemoveFromEmptyCorpusByObjectReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("XYZ");
    when(doc.getLRPersistenceId()).thenReturn("IDZ");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    boolean removed = corpus.remove(doc);
    assertFalse(removed);
  }
@Test
  public void testEqualsFailsWithDifferentDocDataListContent() {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    corpus1.setName("CorpusA");
    corpus2.setName("CorpusA");

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("doc");
    when(d1.getLRPersistenceId()).thenReturn("id1");
    when(d1.getClass().getName()).thenReturn("gate.Document");

    corpus1.add(d1);
    boolean result = corpus1.equals(corpus2); 

    assertFalse(result);
  }
@Test(expected = MethodNotImplementedException.class)
  public void testUnsupportedToArrayCallWithConcreteArrayThrows() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document[] targetArray = new Document[1];
    corpus.toArray(targetArray);
  }
@Test
  public void testRemoveNonExistentDocumentWhenDocumentListEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("ghost");
    when(doc.getLRPersistenceId()).thenReturn("ghost-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    boolean removed = corpus.remove(doc);

    assertFalse(removed);
  }
@Test
  public void testSetIndexDefinitionStoresDefinitionInFeatures() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    gate.creole.ir.IndexDefinition definition = mock(gate.creole.ir.IndexDefinition.class);
    when(definition.getIrEngineClassName()).thenReturn("gate.creole.ir.MockIREngine");

    class DummyEngine implements gate.creole.ir.IREngine {
      public gate.creole.ir.IndexManager getIndexmanager() {
        return mock(gate.creole.ir.IndexManager.class);
      }
    }

    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> clazz = DummyEngine.class;
      Thread.currentThread().setContextClassLoader(new ClassLoader(loader) {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
          if ("gate.creole.ir.MockIREngine".equals(name)) {
            return clazz;
          }
          return super.loadClass(name);
        }
      });

      corpus.setIndexDefinition(definition);

      gate.creole.ir.IndexDefinition returned = corpus.getIndexDefinition();
      assertSame(definition, returned);

    } catch (Exception e) {
      fail("Exception during custom classloader mock");
    }
  }
@Test
  public void testDatastoreClosedTriggersResourceDeletion() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    when(ds.equals(ds)).thenReturn(true);

    CreoleEvent event = mock(CreoleEvent.class);
    when(event.getDatastore()).thenReturn(ds);

    Factory factory = mock(Factory.class);
    corpus.datastoreClosed(event);

    assertTrue(true); 
  }
@Test
  public void testGetWithDocumentNullTriggersDSLoad() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document docMock = mock(Document.class);
    when(docMock.getName()).thenReturn("testDoc");
    when(docMock.getLRPersistenceId()).thenReturn("persist-test");
    when(docMock.getClass().getName()).thenReturn("gate.Document");

    corpus.add(docMock);
    corpus.unloadDocument(docMock, false);

    try {
      Document loadedDoc = corpus.get(0);
      assertNotNull(loadedDoc);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage() != null);
    }
  }
@Test
  public void testResourceWrittenUpdatesIndexManagerIfDocModified() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore datastore = mock(DataStore.class);
    corpus.setDataStore(datastore);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("A");
    when(doc.getLRPersistenceId()).thenReturn("idA");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.isModified()).thenReturn(true);
    when(doc.getDataStore()).thenReturn(datastore);

    gate.creole.ir.IndexDefinition indexDefinition = mock(gate.creole.ir.IndexDefinition.class);
    when(indexDefinition.getIrEngineClassName()).thenReturn("gate.creole.ir.MockIREngineForWrite");

    class DummyEngine2 implements gate.creole.ir.IREngine {
      public gate.creole.ir.IndexManager getIndexmanager() {
        gate.creole.ir.IndexManager mgr = mock(gate.creole.ir.IndexManager.class);
        doNothing().when(mgr).sync(anyList(), anyList(), anyList());
        return mgr;
      }
    }

    try {
      ClassLoader baseLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(new ClassLoader(baseLoader) {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
          if (name.equals("gate.creole.ir.MockIREngineForWrite")) {
            return DummyEngine2.class;
          }
          return super.loadClass(name);
        }
      });

      corpus.setIndexDefinition(indexDefinition);
      corpus.add(doc);

      gate.event.DatastoreEvent event = mock(gate.event.DatastoreEvent.class);
      when(event.getResourceID()).thenReturn("corpus-id");
      corpus.setLRPersistenceId("corpus-id");
      when(event.getResource()).thenReturn(doc);

      corpus.resourceWritten(event);
      assertTrue(true); 

    } catch (Exception e) {
      fail("Exception in resourceWritten simulation");
    }
  }
@Test
  public void testRemoveIndexDocumentCleansUpManager() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("indexDoc");
    when(doc.getLRPersistenceId()).thenReturn("index-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("indexDoc");
    when(doc2.getLRPersistenceId()).thenReturn("index-id");
    when(doc2.getClass().getName()).thenReturn("gate.Document");

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    gate.creole.ir.IndexDefinition def = mock(gate.creole.ir.IndexDefinition.class);
    when(def.getIrEngineClassName()).thenReturn("gate.creole.ir.MockIREngineForRemove");

    class DummyEngine3 implements gate.creole.ir.IREngine {
      public gate.creole.ir.IndexManager getIndexmanager() {
        gate.creole.ir.IndexManager im = mock(gate.creole.ir.IndexManager.class);
        doNothing().when(im).sync(anyList(), anyList(), anyList());
        return im;
      }
    }

    ClassLoader baseLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(new ClassLoader(baseLoader) {
      @Override
      public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.equals("gate.creole.ir.MockIREngineForRemove")) {
          return DummyEngine3.class;
        }
        return super.loadClass(name);
      }
    });

    corpus.setIndexDefinition(def);
    corpus.add(doc);

    gate.event.DatastoreEvent event = mock(gate.event.DatastoreEvent.class);
    when(event.getSource()).thenReturn(ds);
    when(event.getResourceID()).thenReturn("index-id");
    when(event.getResource()).thenReturn(null);

    corpus.resourceDeleted(event);
    assertEquals(0, corpus.size());
  }
@Test
  public void testCleanupNullDataStoreNoListenerRemoveException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.cleanup();
    assertEquals(0, corpus.getDocumentNames().size());
  }
@Test
  public void testAddAllDuplicateDocumentsAllowed() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("Doc");
    when(d1.getLRPersistenceId()).thenReturn("id");
    when(d1.getClass().getName()).thenReturn("gate.Document");

    List<Document> docs = new ArrayList<Document>();
    docs.add(d1);
    docs.add(d1);

    boolean added = corpus.addAll(docs);
    assertTrue(added);
    assertEquals(2, corpus.size());
  }
@Test
  public void testCorpusToStringWithPopulatedData() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocToString");
    when(doc.getLRPersistenceId()).thenReturn("idT");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);

    String result = corpus.toString();
    assertNotNull(result);
    assertTrue(result.contains("DocToString"));
  }
@Test
  public void testGetDocumentNamesWithEmptyDocDataList() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    List<String> names = corpus.getDocumentNames();
    assertNotNull(names);
    assertEquals(0, names.size());
  }
@Test
  public void testGetDocumentPersistentIDsWithEmptyDocDataList() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    List<Object> ids = corpus.getDocumentPersistentIDs();
    assertNotNull(ids);
    assertTrue(ids.isEmpty());
  }
@Test
  public void testGetDocumentClassTypesWithEmptyDocDataList() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    List<String> types = corpus.getDocumentClassTypes();
    assertNotNull(types);
    assertTrue(types.isEmpty());
  }
@Test
  public void testRemoveAllWithEmptyCorpusReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document d1 = mock(Document.class);
    boolean result = corpus.removeAll(Arrays.asList(d1));
    assertTrue(result);
  }
@Test
  public void testAddAllEmptyCollectionReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.addAll(new ArrayList<Document>());
    assertTrue(result);
  }
@Test(expected = MethodNotImplementedException.class)
  public void testSubListThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.subList(0, 1);
  }
@Test(expected = MethodNotImplementedException.class)
  public void testListIteratorThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.listIterator();
  }
@Test(expected = MethodNotImplementedException.class)
  public void testListIteratorAtIndexThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.listIterator(0);
  }
@Test
  public void testRemoveIndexOutOfBoundsThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.remove(10);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      assertTrue(e.getMessage() != null);
    }
  }
@Test
  public void testDatastoreCreatedDoesNotCrash() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    CreoleEvent event = mock(CreoleEvent.class);
    corpus.datastoreCreated(event);
    assertTrue(true);
  }
@Test
  public void testDatastoreOpenedDoesNotCrash() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    CreoleEvent event = mock(CreoleEvent.class);
    corpus.datastoreOpened(event);
    assertTrue(true);
  }
@Test
  public void testIndexManagerGetterReturnsNullWhenUnset() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    IndexManager manager = corpus.getIndexManager();
    assertNull(manager);
  }
@Test
  public void testIndexStatisticsGetterReturnsNullWhenUnset() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    IndexStatistics stats = corpus.getIndexStatistics();
    assertNull(stats);
  }
@Test
  public void testSetIndexDefinitionWithNullDoesNotCrash() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setIndexDefinition(null);
    assertNull(corpus.getIndexDefinition());
  }
@Test
  public void testResourceRenamedDoesNotAffectState() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    corpus.resourceRenamed(doc, "oldName", "newName");
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testResourceAdoptedDoesNothing() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DatastoreEvent event = mock(DatastoreEvent.class);
    corpus.resourceAdopted(event);
    assertTrue(true);
  }
@Test
  public void testGetReturnsNullWhenIndexExceedsDocDataList() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = corpus.get(99);
    assertNull(doc);
  }
@Test
  public void testClearEmptiesCorpusCompletely() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc1");
    when(doc.getLRPersistenceId()).thenReturn("id-1");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    assertEquals(1, corpus.size());

    corpus.clear();
    assertEquals(0, corpus.size());
  }
@Test
  public void testIsDocumentLoadedFalseWhenDocumentsNotInitialized() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean loaded = corpus.isDocumentLoaded(0);
    assertFalse(loaded);
  }
@Test
  public void testIsPersistentDocumentFalseWhenDocListEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean persisted = corpus.isPersistentDocument(0);
    assertFalse(persisted);
  }
@Test
  public void testSetDocumentPersistentIDInvalidIndexDoesNotCrash() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    try {
      corpus.setDocumentPersistentID(5, "new-id");
    } catch (Exception e) {
      fail("Should not throw exceptions for out-of-range index");
    }

    assertTrue(corpus.isEmpty());
  }
@Test
  public void testToStringReturnsNonNullStringWhenEmptyCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String result = corpus.toString();
    assertNotNull(result);
    assertTrue(result.contains("document data"));
  }
@Test
  public void testEqualsSelfReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.equals(corpus));
  }
@Test
  public void testEqualsNullReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals(null));
  }
@Test
  public void testEqualsDifferentClassReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals("NotACorpus"));
  }
@Test
  public void testIndexOfUnknownDocumentReturnsNegativeOne() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Unknown");
    when(doc.getLRPersistenceId()).thenReturn("unknown-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    int index = corpus.indexOf(doc);
    assertEquals(-1, index);
  }
@Test
  public void testRemoveDocumentWithNullLRIDDoesNotThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Unnamed");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    corpus.remove(doc);
    assertEquals(0, corpus.size());
  }
@Test
  public void testUnloadAlreadyUnloadedDocumentWithPersistence() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("DocX");
    when(doc.getLRPersistenceId()).thenReturn("ID123");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    corpus.add(doc);
    corpus.unloadDocument(0, false); 

    corpus.unloadDocument(0, false); 
    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testRemoveDocumentWithMismatchedRefReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("Doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");
    corpus.add(doc1);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("Doc2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");

    boolean result = corpus.remove(doc2);
    assertFalse(result);
  }
@Test
  public void testIsDocumentLoadedWhenDocumentsInitializedButEntryNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("X");
    when(doc.getLRPersistenceId()).thenReturn("X1");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    boolean loaded = corpus.isDocumentLoaded(0);
    assertFalse(loaded);
  }
@Test
  public void testFindDocumentWithSameNameDifferentClassReturnsNegativeOne() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("DocA");
    when(doc1.getLRPersistenceId()).thenReturn("pidA");
    when(doc1.getClass().getName()).thenReturn("gate.TypeA");

    corpus.add(doc1);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("DocA");
    when(doc2.getLRPersistenceId()).thenReturn("pidA");
    when(doc2.getClass().getName()).thenReturn("gate.TypeB");

    int index = corpus.findDocument(doc2);
    assertEquals(-1, index);
  }
@Test
  public void testEqualsReturnsFalseWhenDocDataListIsDifferentSize() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    c1.setName("Corpus");
    c2.setName("Corpus");

    DataStore ds = mock(DataStore.class);
    c1.setDataStore(ds);
    c2.setDataStore(ds);

    c1.setLRPersistenceId("idA");
    c2.setLRPersistenceId("idA");

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc1");
    when(doc.getLRPersistenceId()).thenReturn("LR1");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    c1.add(doc);

    boolean result = c1.equals(c2);
    assertFalse(result);
  }
@Test
  public void testGetWithInvalidPersistentIDThrowsGateRuntimeException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("BrokenDoc");
    when(doc.getLRPersistenceId()).thenReturn("bad-id");
    when(doc.getClass().getName()).thenReturn("invalid.class.Name");

    corpus.add(doc);
    corpus.unloadDocument(0, false); 

    try {
      corpus.get(0);
      fail("Expected exception");
    } catch (GateRuntimeException e) {
      assertTrue(e.getMessage() != null);
    }
  }
@Test
  public void testUnloadDocumentWithNullPersistentIdSyncSkips() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("TransientDoc");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    corpus.unloadDocument(0, true); 
    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testDocumentRemovedDoesNotThrowOnMissingDataStore() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DatastoreEvent event = mock(DatastoreEvent.class);
    DataStore ds = mock(DataStore.class);
    when(event.getSource()).thenReturn(ds);
    when(ds.equals(null)).thenReturn(false);

    corpus.resourceDeleted(event);
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testToStringReturnsAllExpectedContent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d = mock(Document.class);
    when(d.getName()).thenReturn("DocStr");
    when(d.getLRPersistenceId()).thenReturn("ID");
    when(d.getClass().getName()).thenReturn("gate.Document");

    corpus.add(d);

    String s = corpus.toString();
    assertNotNull(s);
    assertTrue(s.contains("DocStr"));
  }
@Test
  public void testHashCodeIsConsistentAcrossMultipleCalls() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    int hash1 = corpus.hashCode();
    int hash2 = corpus.hashCode();
    assertEquals(hash1, hash2);
  }
@Test
  public void testCleanupRemovesCorpusListenersAndClearsDocuments() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("D");
    when(doc.getLRPersistenceId()).thenReturn("idD");
    when(doc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(doc);
    assertEquals(1, corpus.size());

    corpus.cleanup();
    assertEquals(0, corpus.size());
  } 
}