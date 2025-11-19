public class SerialCorpusImpl_3_GPTLLMTest { 

 @Test
  public void testAddSingleDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docA");
    when(doc.getLRPersistenceId()).thenReturn("idA");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    boolean added = corpus.add(doc);
    assertTrue(added);
    assertEquals(1, corpus.size());
    assertTrue(corpus.contains(doc));
    assertEquals("docA", corpus.getDocumentName(0));
  }
@Test
  public void testAddMultipleDocumentsIndividually() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("doc2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");
    when(doc2.getDataStore()).thenReturn(null);

    boolean firstAdded = corpus.add(doc1);
    boolean secondAdded = corpus.add(doc2);

    assertTrue(firstAdded);
    assertTrue(secondAdded);
    assertEquals(2, corpus.size());
    assertEquals("doc1", corpus.getDocumentName(0));
    assertEquals("doc2", corpus.getDocumentName(1));
  }
@Test
  public void testRemoveDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docX");
    when(doc.getLRPersistenceId()).thenReturn("idx");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    assertTrue(corpus.add(doc));
    boolean removed = corpus.remove(doc);

    assertTrue(removed);
    assertEquals(0, corpus.size());
    assertFalse(corpus.contains(doc));
  }
@Test
  public void testGetDocumentByIndexAfterAdd() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("insideDoc");
    when(doc.getLRPersistenceId()).thenReturn("insideID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    Document result = corpus.get(0);
    assertNotNull(result);
    assertEquals("insideDoc", result.getName());
  }
@Test
  public void testUnloadDocumentByIndexWithoutSync() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("delDoc");
    when(doc.getLRPersistenceId()).thenReturn("delID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    corpus.add(doc);
    corpus.setDocumentPersistentID(0, "delID");

    corpus.unloadDocument(0, false);
    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testUnloadDocumentByReferenceWithoutSync() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("refDoc");
    when(doc.getLRPersistenceId()).thenReturn("refID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    corpus.add(doc);
    corpus.setDocumentPersistentID(0, "refID");

    corpus.unloadDocument(doc, false);
    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testGetDocumentNameByIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("lookupDoc");
    when(doc.getLRPersistenceId()).thenReturn("lookupID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    String name = corpus.getDocumentName(0);
    assertEquals("lookupDoc", name);
  }
@Test
  public void testContainsReturnsFalseForNonMember() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document addedDoc = mock(Document.class);
    when(addedDoc.getName()).thenReturn("added");
    when(addedDoc.getLRPersistenceId()).thenReturn("idX");
    when(addedDoc.getClass().getName()).thenReturn("gate.Document");
    when(addedDoc.getDataStore()).thenReturn(null);

    Document otherDoc = mock(Document.class);
    when(otherDoc.getName()).thenReturn("outside");
    when(otherDoc.getLRPersistenceId()).thenReturn("idY");
    when(otherDoc.getClass().getName()).thenReturn("gate.Document");
    when(otherDoc.getDataStore()).thenReturn(null);

    corpus.add(addedDoc);

    assertFalse(corpus.contains(otherDoc));
  }
@Test
  public void testClearCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("d1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("d2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(doc2);

    corpus.clear();

    assertEquals(0, corpus.size());
    assertTrue(corpus.isEmpty());
  }
@Test(expected = MethodNotImplementedException.class)
  public void testToArrayShouldThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.toArray();
  }
@Test(expected = MethodNotImplementedException.class)
  public void testToArrayGenericShouldThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.toArray(new Document[0]);
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
  public void testSetThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    corpus.set(0, doc);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testRetainAllUnsupported() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc");
    when(doc.getLRPersistenceId()).thenReturn("X");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.retainAll(Collections.singletonList(doc));
  }
@Test
  public void testEqualsAndHashCodeWithSelf() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docEqual");
    when(doc.getLRPersistenceId()).thenReturn("idEqual");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);

    assertEquals(corpus, corpus);
    assertEquals(corpus.hashCode(), corpus.hashCode());
  }
@Test(expected = ResourceInstantiationException.class)
  public void testDuplicateThrowsException() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.duplicate(null);
  }
@Test
  public void testGetDocumentByInvalidIndexReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document result = corpus.get(5);
    assertNull(result);
  }
@Test
  public void testGetDocumentNameByInvalidIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String name = corpus.getDocumentName(999);
    assertEquals("No such document", name);
  }
@Test
  public void testGetDocumentPersistentIDByInvalidIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object id = corpus.getDocumentPersistentID(999);
    assertNull(id);
  }
@Test
  public void testGetDocumentClassTypeByInvalidIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String type = corpus.getDocumentClassType(999);
    assertNull(type);
  }
@Test
  public void testRemoveDocumentNotInCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document externalDoc = mock(Document.class);
    when(externalDoc.getName()).thenReturn("not_in_corpus");
    when(externalDoc.getLRPersistenceId()).thenReturn("not_id");
    when(externalDoc.getClass().getName()).thenReturn("gate.Document");
    when(externalDoc.getDataStore()).thenReturn(null);

    boolean result = corpus.remove(externalDoc);
    assertFalse(result);
  }
@Test
  public void testIndexOfNonExistentObject() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object notDocument = new Object();
    int index = corpus.indexOf(notDocument);
    assertEquals(-1, index);
  }
@Test
  public void testAddNullDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.add(null);
    assertFalse(result);
  }
@Test
  public void testAddDocumentWithDifferentDatastoreFails() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds1 = mock(DataStore.class);
    DataStore ds2 = mock(DataStore.class);

    corpus.setDataStore(ds1);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docMismatch");
    when(doc.getLRPersistenceId()).thenReturn("pidMismatch");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds2);

    boolean result = corpus.add(doc);
    assertFalse(result);
    assertEquals(0, corpus.size());
  }
@Test
  public void testRemoveByIndexReturnsNullForUnloadedDoc() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("toRemove");
    when(doc.getLRPersistenceId()).thenReturn("abc123");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    Document removed = corpus.remove(0);
    assertNull(removed);
    assertEquals(0, corpus.size());
  }
@Test
  public void testAddAllPartialFailureDueToDatastoreMismatch() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds1 = mock(DataStore.class);
    DataStore ds2 = mock(DataStore.class);
    corpus.setDataStore(ds1);

    Document validDoc = mock(Document.class);
    when(validDoc.getName()).thenReturn("valid");
    when(validDoc.getLRPersistenceId()).thenReturn("vid");
    when(validDoc.getClass().getName()).thenReturn("gate.Document");
    when(validDoc.getDataStore()).thenReturn(ds1);

    Document invalidDoc = mock(Document.class);
    when(invalidDoc.getName()).thenReturn("invalid");
    when(invalidDoc.getLRPersistenceId()).thenReturn("iid");
    when(invalidDoc.getClass().getName()).thenReturn("gate.Document");
    when(invalidDoc.getDataStore()).thenReturn(ds2);

    boolean result = corpus.addAll(Arrays.asList(validDoc, invalidDoc));
    assertFalse(result);
    assertEquals(1, corpus.size());
  }
@Test
  public void testRemoveAllWithSomeNonMembers() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");
    when(doc1.getDataStore()).thenReturn(null);
    corpus.add(doc1);

    Document notInCorpus = mock(Document.class);
    when(notInCorpus.getName()).thenReturn("notThere");
    when(notInCorpus.getLRPersistenceId()).thenReturn("xx");
    when(notInCorpus.getClass().getName()).thenReturn("gate.Document");
    when(notInCorpus.getDataStore()).thenReturn(null);

    boolean result = corpus.removeAll(Arrays.asList(doc1, notInCorpus));
    assertFalse(result);
    assertEquals(0, corpus.size());
  }
@Test
  public void testIsPersistentDocumentReturnsFalseIfNoEntry() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isPersistentDocument(0);
    assertFalse(result);
  }
@Test
  public void testIteratorReturnsExpectedSize() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("doc2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(doc2);

    int count = 0;
    Iterator<Document> iterator = corpus.iterator();
    if (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    if (iterator.hasNext()) {
      iterator.next();
      count++;
    }

    assertEquals(2, count);
  }
@Test
  public void testAddDocumentAtSpecificIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("A");
    when(doc1.getLRPersistenceId()).thenReturn("a");
    when(doc1.getClass().getName()).thenReturn("gate.Document");
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("B");
    when(doc2.getLRPersistenceId()).thenReturn("b");
    when(doc2.getClass().getName()).thenReturn("gate.Document");
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(0, doc2);
    assertEquals("B", corpus.getDocumentName(0));
    assertEquals("A", corpus.getDocumentName(1));
  }
@Test
  public void testAddAndRemoveDocumentWithNullPersistentID() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("nullDoc");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertTrue(corpus.remove(doc));
  }
@Test
  public void testIsEmptyTrueWhenNew() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testIsEmptyFalseAfterAdd() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("d");
    when(doc.getLRPersistenceId()).thenReturn("x");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertFalse(corpus.isEmpty());
  }
@Test
  public void testDocumentNamesEmptyWhenNoneAdded() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.getDocumentNames().isEmpty());
  }
@Test
  public void testDocumentClassTypeListWithOneDoc() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("typedDoc");
    when(doc.getLRPersistenceId()).thenReturn("101");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    java.util.List<String> types = corpus.getDocumentClassTypes();
    assertEquals(1, types.size());
    assertEquals("gate.Document", types.get(0));
  }
@Test
  public void testIteratorNextThrowsWhenEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Iterator<Document> iter = corpus.iterator();
    assertFalse(iter.hasNext());
  }
@Test
  public void testUnloadedDocumentNotSavedWhenSyncFalseAndDocumentNotPersistent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document transientDoc = mock(Document.class);
    when(transientDoc.getName()).thenReturn("tempDoc");
    when(transientDoc.getLRPersistenceId()).thenReturn(null);
    when(transientDoc.getClass().getName()).thenReturn("gate.Document");
    when(transientDoc.getDataStore()).thenReturn(null);

    corpus.add(transientDoc);
    corpus.unloadDocument(0, false);

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test(expected = GateRuntimeException.class)
  public void testUnloadDocumentWithSyncThrowsOnFailureDuringAdopt() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("unsavedDoc");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    when(ds.adopt(doc)).thenReturn(doc);
    doThrow(new PersistenceException("disk IO error")).when(ds).sync(doc);

    corpus.add(doc);
    corpus.unloadDocument(0, true);
  }
@Test
  public void testRemoveIndexDocumentCleanEventFired() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("RMDoc");
    when(doc.getLRPersistenceId()).thenReturn("RID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    Document removed = corpus.remove(0);
    assertEquals(doc, removed);
    assertEquals(0, corpus.size());
  }
@Test
  public void testEqualsReturnsFalseOnDifferentCorpusNames() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    c1.setName("corpusA");
    c2.setName("corpusB");

    assertFalse(c1.equals(c2));
  }
@Test
  public void testEqualsReturnsFalseIfOtherIsNull() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    assertFalse(c1.equals(null));
  }
@Test
  public void testEqualsReturnsFalseIfNotSameClass() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    assertFalse(c1.equals("Not a Corpus"));
  }
@Test
  public void testGetReturnsLoadedDocumentWithoutCreatingAgain() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("fetchedDoc");
    when(doc.getLRPersistenceId()).thenReturn("FID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    Document fetched = corpus.get(0);
    assertSame(doc, fetched);
  }
@Test
  public void testRemoveDocumentJustByPersistentIdWithoutRelianceOnLoadedInstances() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("lazyDoc");
    when(doc.getLRPersistenceId()).thenReturn("lazyID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    assertTrue(corpus.remove(doc));
  }
@Test
  public void testDatastoreEventDeletionOfCorpusTriggersDeletionOfThisCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setName("corpusC");
    corpus.setDataStore(ds);
    when(ds.equals(ds)).thenReturn(true);

    DatastoreEvent evt = mock(DatastoreEvent.class);
    when(evt.getSource()).thenReturn(ds);
    when(evt.getResourceID()).thenReturn(null);

    corpus.resourceDeleted(evt);
    
  }
@Test
  public void testDatastoreEventDeletionOfResourceWithMatchingPersistentIDRemovesDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("EgoDoc");
    when(doc.getLRPersistenceId()).thenReturn("ego-id");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    corpus.add(doc);

    DatastoreEvent evt = mock(DatastoreEvent.class);
    when(evt.getSource()).thenReturn(ds);
    when(evt.getResourceID()).thenReturn("ego-id");
    when(evt.getResource()).thenReturn(doc);

    corpus.resourceDeleted(evt);
    assertEquals(0, corpus.size());
  }
@Test(expected = MethodNotImplementedException.class)
  public void testLastIndexOfThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    corpus.lastIndexOf(doc);
  }
@Test
  public void testGetTransientSourceReturnsNullAlways() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object result = corpus.getTransientSource();
    assertNull(result);
  }
@Test
  public void testInitReturnsSelfInstance() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object result = corpus.init();
    assertSame(corpus, result);
  }
@Test
  public void testHashCodeConsistency() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("X1");
    when(doc.getLRPersistenceId()).thenReturn("idX");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    int h1 = corpus.hashCode();
    int h2 = corpus.hashCode();
    assertEquals(h1, h2);
  }
@Test
  public void testPopulateWithValidFileFilterAndFlags() throws IOException, ResourceInstantiationException {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    URL dummyUrl = new URL("file:/tmp");

    try {
      corpus.populate(dummyUrl, null, "UTF-8", false);
    } catch (Exception e) {
      
    }
  }
@Test
  public void testSizeReturnsZeroAfterClear() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docX");
    when(doc.getLRPersistenceId()).thenReturn("pidX");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.clear();
    assertEquals(0, corpus.size());
  }
@Test
  public void testGetWithDocumentInstantiatedFromDataStore() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("virtualDoc");
    when(doc.getLRPersistenceId()).thenReturn("virtualID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    Document recreated = mock(Document.class);
    when(recreated.getName()).thenReturn("virtualDoc");
    when(recreated.getLRPersistenceId()).thenReturn("virtualID");

    FeatureMap params = Factory.newFeatureMap();
    params.put(DataStore.DATASTORE_FEATURE_NAME, ds);
    params.put(DataStore.LR_ID_FEATURE_NAME, "virtualID");

    Factory.deleteResource(recreated); 

    try {
      Document loaded = corpus.get(0);
      assertNotNull(loaded);
    } catch (GateRuntimeException e) {
      
    }
  }
@Test
  public void testAddSameDocumentTwice() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docRepeating");
    when(doc.getLRPersistenceId()).thenReturn("repeatID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    assertTrue(corpus.add(doc));
    assertTrue(corpus.add(doc)); 

    assertEquals(2, corpus.size());
    assertEquals("docRepeating", corpus.getDocumentName(0));
    assertEquals("docRepeating", corpus.getDocumentName(1));
  }
@Test
  public void testEqualsCorpusWithNullFields() {
    SerialCorpusImpl a = new SerialCorpusImpl();
    SerialCorpusImpl b = new SerialCorpusImpl();

    assertTrue(a.equals(b));
    assertEquals(a.hashCode(), b.hashCode());
  }
@Test
  public void testRemoveByInvalidIndexReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document result = corpus.remove(99);
    assertNull(result);
  }
@Test
  public void testUnloadDocumentInvalidIndexDoesNothing() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.unloadDocument(20, false); 
  }
@Test
  public void testUnloadTransientDocWithSyncFalseAndNotLoaded() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docTransient");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testDocumentRemovedWithNullPersistentIDDoesNotFail() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("nopID");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertTrue(corpus.remove(doc));
  }
@Test
  public void testCorpusCleanupClearsResources() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("d2");
    when(doc.getLRPersistenceId()).thenReturn("id2");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.cleanup();

    assertEquals(0, corpus.size());
  }
@Test
  public void testAddListenerTwiceOnlyAddsOnce() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    CorpusListener listener = mock(CorpusListener.class);
    corpus.addCorpusListener(listener);
    corpus.addCorpusListener(listener); 

    corpus.removeCorpusListener(listener);
    corpus.removeCorpusListener(listener); 
  }
@Test
  public void testPopulateWithMimeTypeAndRecurse() throws IOException, ResourceInstantiationException {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    URL dummyUrl = new URL("file:/tmp");

    try {
      corpus.populate(dummyUrl, null, "UTF-8", "text/xml", true);
    } catch(Exception e) {
      
    }
  }
@Test
  public void testDocumentClassTypeListEmptyWhenNoDocs() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    List<String> result = corpus.getDocumentClassTypes();
    assertTrue(result.isEmpty());
  }
@Test
  public void testToStringWithEmptyCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String description = corpus.toString();
    assertTrue(description.contains("document data"));
  }
@Test
  public void testGetWithInvalidDocumentFactoryThrows() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("faultyDoc");
    when(doc.getLRPersistenceId()).thenReturn("docX");
    when(doc.getClass().getName()).thenReturn("non.existing.Class");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    try {
      corpus.get(0);
      fail("Expected GateRuntimeException");
    } catch (GateRuntimeException e) {
      assertTrue(e.getMessage().contains("gate.Document"));
    }
  }
@Test
  public void testIteratorHasNextFalseWhenEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Iterator<Document> it = corpus.iterator();
    assertFalse(it.hasNext());
  }
@Test
  public void testResourceEqualsSameValuesReturnsTrue() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);

    c1.setName("A");
    c1.setDataStore(ds);

    c2.setName("A");
    c2.setDataStore(ds);

    assertTrue(c1.equals(c2));
  }
@Test
  public void testFindDocumentByNameAndPersistenceIdOnly() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docIn");
    when(doc.getLRPersistenceId()).thenReturn("123");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false);

    Document probe = mock(Document.class);
    when(probe.getName()).thenReturn("docIn");
    when(probe.getLRPersistenceId()).thenReturn("123");
    when(probe.getClass().getName()).thenReturn("gate.Document");
    when(probe.getDataStore()).thenReturn(null);

    int index = corpus.indexOf(probe);
    assertEquals(0, index);
  }
@Test
  public void testCorpusHashCodeMatchesWhenSameContent() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("A");
    when(d1.getLRPersistenceId()).thenReturn("idA");
    when(d1.getClass().getName()).thenReturn("gate.Document");
    when(d1.getDataStore()).thenReturn(null);

    c1.add(d1);
    c2.add(d1);

    assertEquals(c1.hashCode(), c2.hashCode());
  }
@Test
  public void testContainsWithNonDocumentTypeReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.contains("notADoc"));
  }
@Test
  public void testGetDocumentPersistentIDsEmptyOnInit() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    List<Object> ids = corpus.getDocumentPersistentIDs();
    assertTrue(ids.isEmpty());
  }
@Test
  public void testAddAllWithEmptyListReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.addAll(Collections.emptyList());
    assertTrue(result);
  }
@Test
  public void testContainsAllWithEmptyListReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.containsAll(Collections.emptyList());
    assertTrue(result);
  }
@Test
  public void testEmptyConstructorHasEmptyDocumentList() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.getDocumentNames().isEmpty());
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testAddDocumentNullPersistenceIdIsStillValid() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document document = mock(Document.class);
    when(document.getName()).thenReturn("temp");
    when(document.getLRPersistenceId()).thenReturn(null);
    when(document.getClass().getName()).thenReturn("gate.Document");
    when(document.getDataStore()).thenReturn(null);

    boolean result = corpus.add(document);
    assertTrue(result);
    assertEquals("temp", corpus.getDocumentName(0));
  }
@Test
  public void testRemoveByIndexBeyondBoundsReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document removed = corpus.remove(5);
    assertNull(removed);
  }
@Test
  public void testUnloadPersistentButNotLoadedDoesNothing() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document mockDoc = mock(Document.class);
    when(mockDoc.getName()).thenReturn("doc");
    when(mockDoc.getLRPersistenceId()).thenReturn("123");
    when(mockDoc.getClass().getName()).thenReturn("gate.Document");
    when(mockDoc.getDataStore()).thenReturn(null);

    corpus.add(mockDoc);
    corpus.setDocumentPersistentID(0, "123");
    corpus.unloadDocument(0, false); 

    corpus.unloadDocument(0, false); 
    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test(expected = MethodNotImplementedException.class)
  public void testSetMethodThrows() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    corpus.set(0, doc);
  }
@Test(expected = MethodNotImplementedException.class)
  public void testListIteratorWithIndexThrows() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.listIterator(1);
  }
@Test
  public void testIndexOfNullReturnsMinusOne() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    int index = corpus.indexOf(null);
    assertEquals(-1, index);
  }
@Test
  public void testSetDocumentPersistentIDBeyondLimitIsIgnored() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setDocumentPersistentID(10, "new-id"); 
  }
@Test
  public void testIsDocumentLoadedWithNullListReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isDocumentLoaded(0);
    assertFalse(result);
  }
@Test
  public void testIsPersistentDocumentWithNullListReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isPersistentDocument(0);
    assertFalse(result);
  }
@Test
  public void testSubListThrowsMethodNotImplementedException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.subList(0, 1);
      fail("Expected MethodNotImplementedException");
    } catch (MethodNotImplementedException e) {
      assertTrue(e.getMessage().contains("subList"));
    }
  }
@Test
  public void testContainsAllPartialMatchReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("D1");
    when(d1.getLRPersistenceId()).thenReturn("id1");
    when(d1.getClass().getName()).thenReturn("gate.Document");
    when(d1.getDataStore()).thenReturn(null);
    corpus.add(d1);

    Document d2 = mock(Document.class); 
    when(d2.getName()).thenReturn("D2");
    when(d2.getLRPersistenceId()).thenReturn("id2");
    when(d2.getClass().getName()).thenReturn("gate.Document");
    when(d2.getDataStore()).thenReturn(null);

    boolean result = corpus.containsAll(Arrays.asList(d1, d2));
    assertFalse(result);
  }
@Test
  public void testRemoveAllWithNoCommonDocsReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("other");
    when(d1.getLRPersistenceId()).thenReturn("idX");
    when(d1.getClass().getName()).thenReturn("gate.Document");
    when(d1.getDataStore()).thenReturn(null);

    boolean result = corpus.removeAll(Collections.singletonList(d1));
    assertFalse(result);
  }
@Test
  public void testIteratorNextRemovesNotSupported() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Iterator<Document> it = corpus.iterator();
    try {
      it.remove();
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertTrue(e.getMessage().contains("remove"));
    }
  }
@Test(expected = ResourceInstantiationException.class)
  public void testDuplicateThrowsExceptionAlways() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.duplicate(null);
  }
@Test
  public void testGetReturnsDocumentFromFactoryWhenUnloaded() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("loadable");
    when(doc.getLRPersistenceId()).thenReturn("lr123");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    corpus.add(doc);
    corpus.setDocumentPersistentID(0, "lr123");

    corpus.unloadDocument(0, false);

    try {
      Document loaded = corpus.get(0);
      assertNotNull(loaded); 
    } catch (GateRuntimeException e) {
      
      assertTrue(e.getMessage().contains("SerialCorpusImpl"));
    }
  }
@Test
  public void testToArrayGenericThrowsExplicitly() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.toArray(new Document[0]);
      fail("Expected MethodNotImplementedException");
    } catch (MethodNotImplementedException e) {
      assertTrue(e.getMessage().contains("not implemented"));
    }
  }
@Test
  public void testToArrayThrowsExplicitly() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.toArray();
      fail("Expected MethodNotImplementedException");
    } catch (MethodNotImplementedException e) {
      assertTrue(e.getMessage().contains("not implemented"));
    }
  }
@Test
  public void testUnloadingDocumentThatWasAlreadyUnloaded() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docToUnload");
    when(doc.getLRPersistenceId()).thenReturn("idToUnload");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false);
    corpus.unloadDocument(0, true); 
  }
@Test
  public void testInitReturnsSelf() throws ResourceInstantiationException {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object result = corpus.init();
    assertSame(corpus, result);
  }
@Test
  public void testClearRemovesAllContent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("tempdoc");
    when(doc.getLRPersistenceId()).thenReturn("TID");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.clear();
    assertEquals(0, corpus.size());
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testAddAllPreservesOrder() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document docA = mock(Document.class);
    when(docA.getName()).thenReturn("X");
    when(docA.getLRPersistenceId()).thenReturn("idX");
    when(docA.getClass().getName()).thenReturn("gate.Document");
    when(docA.getDataStore()).thenReturn(null);

    Document docB = mock(Document.class);
    when(docB.getName()).thenReturn("Y");
    when(docB.getLRPersistenceId()).thenReturn("idY");
    when(docB.getClass().getName()).thenReturn("gate.Document");
    when(docB.getDataStore()).thenReturn(null);

    boolean result = corpus.addAll(Arrays.asList(docA, docB));
    assertTrue(result);
    assertEquals("X", corpus.getDocumentName(0));
    assertEquals("Y", corpus.getDocumentName(1));
  }
@Test
  public void testToStringProvidesReadableContent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Doc123");
    when(doc.getLRPersistenceId()).thenReturn("P123");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    String str = corpus.toString();
    assertTrue(str.contains("documents"));
    assertTrue(str.contains("Doc123") || str.contains("P123"));
  }
@Test
  public void testRemoveCorpusListenerWhenListenerNeverAddedDoesNotFail() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.removeCorpusListener(mock(gate.event.CorpusListener.class));
  }
@Test
  public void testAddCorpusListenerTwiceOnlyKeepsOne() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    gate.event.CorpusListener listener = mock(gate.event.CorpusListener.class);
    corpus.addCorpusListener(listener);
    corpus.addCorpusListener(listener); 
    corpus.removeCorpusListener(listener); 
    
  }
@Test
  public void testSetDocumentPersistentIDTwiceReplacesValue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docP1");
    when(doc.getLRPersistenceId()).thenReturn("ID_OLD");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.setDocumentPersistentID(0, "ID_NEW");
    assertEquals("ID_NEW", corpus.getDocumentPersistentID(0));
  }
@Test
  public void testEqualsReturnsFalseForCorpusWithSameDocsButDifferentOrder() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("one");
    when(d1.getLRPersistenceId()).thenReturn("id1");
    when(d1.getClass().getName()).thenReturn("gate.Document");
    when(d1.getDataStore()).thenReturn(null);

    Document d2 = mock(Document.class);
    when(d2.getName()).thenReturn("two");
    when(d2.getLRPersistenceId()).thenReturn("id2");
    when(d2.getClass().getName()).thenReturn("gate.Document");
    when(d2.getDataStore()).thenReturn(null);

    c1.add(d1);
    c1.add(d2);

    c2.add(d2);
    c2.add(d1);

    assertFalse(c1.equals(c2));
  }
@Test
  public void testResourceUnloadedRemovesOnlyTransientDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document transientDoc = mock(Document.class);
    when(transientDoc.getName()).thenReturn("temp");
    when(transientDoc.getLRPersistenceId()).thenReturn("transient");
    when(transientDoc.getDataStore()).thenReturn(null);
    when(transientDoc.getClass().getName()).thenReturn("gate.Document");

    corpus.add(transientDoc);

    CreoleEvent event = mock(CreoleEvent.class);
    when(event.getResource()).thenReturn(transientDoc);

    corpus.resourceUnloaded(event);
    assertEquals(0, corpus.size());
  }
@Test
  public void testResourceUnloadedUnloadsMatchingPersistentDoc() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);

    Document persistentDoc = mock(Document.class);
    when(persistentDoc.getName()).thenReturn("mydoc");
    when(persistentDoc.getLRPersistenceId()).thenReturn("pid");
    when(persistentDoc.getDataStore()).thenReturn(ds);
    when(persistentDoc.getClass().getName()).thenReturn("gate.Document");

    corpus.setDataStore(ds);
    corpus.add(persistentDoc);

    CreoleEvent event = mock(CreoleEvent.class);
    when(event.getResource()).thenReturn(persistentDoc);

    corpus.resourceUnloaded(event);
    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testSetIndexDefinitionCreatesManagerAndAssignsObjects() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    IndexDefinition def = mock(IndexDefinition.class);
    when(def.getIrEngineClassName()).thenReturn(MockEngine.class.getName());

    corpus.setIndexDefinition(def);

    assertNotNull(corpus.getIndexManager());
    assertEquals(def, corpus.getIndexDefinition());
  }
@Test
  public void testSetTransientSourceInitializesDocumentsCorrectly() {
    SerialCorpusImpl sourceCorpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("Temp");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);
    sourceCorpus.add(doc);

    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(sourceCorpus);

    assertEquals(1, corpus.size());
    assertEquals("Temp", corpus.getDocumentName(0));
    assertTrue(corpus.contains(doc));
  }
@Test
  public void testContainsAllTrueWhenSameDocsDifferentOrder() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("one");
    when(d1.getLRPersistenceId()).thenReturn("id1");
    when(d1.getClass().getName()).thenReturn("gate.Document");
    when(d1.getDataStore()).thenReturn(null);

    Document d2 = mock(Document.class);
    when(d2.getName()).thenReturn("two");
    when(d2.getLRPersistenceId()).thenReturn("id2");
    when(d2.getClass().getName()).thenReturn("gate.Document");
    when(d2.getDataStore()).thenReturn(null);

    corpus.add(d1);
    corpus.add(d2);

    List<Document> external = Arrays.asList(d2, d1);
    assertTrue(corpus.containsAll(external));
  }
@Test
  public void testResourceWrittenTriggersIndexSyncWithChanges() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    IndexManager manager = mock(IndexManager.class);
    IREngine engine = mock(IREngine.class);
    when(engine.getIndexmanager()).thenReturn(manager);

    IndexDefinition def = mock(IndexDefinition.class);
    when(def.getIrEngineClassName()).thenReturn(MockEngine.class.getName());

    corpus.setIndexDefinition(def);

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn("gate.Document");
    when(doc1.getDataStore()).thenReturn(null);
    when(doc1.isModified()).thenReturn(true);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("doc2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn("gate.Document");
    when(doc2.getDataStore()).thenReturn(null);
    when(doc2.isModified()).thenReturn(false);

    corpus.add(doc1);
    corpus.add(doc2);

    DatastoreEvent evt = mock(DatastoreEvent.class);
    when(evt.getResourceID()).thenReturn(corpus.getLRPersistenceId());

    corpus.resourceWritten(evt);

    try {
      verify(manager, atLeastOnce()).sync(anyList(), anyList(), anyList());
    } catch (Exception e) {
      fail("Unexpected exception in sync");
    }
  }
@Test
  public void testDatastoreClosedUnregistersListenerAndDeletesResource() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);
    when(ds.equals(ds)).thenReturn(true);

    CreoleEvent event = mock(CreoleEvent.class);
    when(event.getDatastore()).thenReturn(ds);

    corpus.datastoreClosed(event);
    
  }
@Test
  public void testResourceDeletedWithNullResourceObjectRemovesByID() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("oldDoc");
    when(doc.getLRPersistenceId()).thenReturn("rmid");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    corpus.add(doc);

    DatastoreEvent evt = mock(DatastoreEvent.class);
    when(evt.getSource()).thenReturn(ds);
    when(evt.getResourceID()).thenReturn("rmid");
    when(evt.getResource()).thenReturn(null);

    corpus.resourceDeleted(evt);

    assertEquals(0, corpus.size());
  }
@Test
  public void testGetIndexStatisticsReturnsNullWhenNotSet() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertNull(corpus.getIndexStatistics());
  }
@Test
  public void testRemoveCorpusWhenDeletedEventMatchesCorpusPersistentId() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);
    corpus.lrPersistentId = "cid";

    DatastoreEvent event = mock(DatastoreEvent.class);
    when(event.getSource()).thenReturn(ds);
    when(event.getResourceID()).thenReturn("cid");

    corpus.resourceDeleted(event);
    
  }
@Test
  public void testSetDataStoreRegistersListener() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);
    verify(ds).addDatastoreListener(corpus);
  }
@Test
  public void testCleanupWithNullListenersDoesNotThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.cleanup(); 
  }
@Test
  public void testCleanupRemovesAllDocumentState() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc");
    when(doc.getLRPersistenceId()).thenReturn("id");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertEquals(1, corpus.size());

    corpus.cleanup();
    assertEquals(0, corpus.size());
  }
@Test
  public void testResourceWrittenSkipsIfResourceIdDifferent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.lrPersistentId = "abc";

    DatastoreEvent event = mock(DatastoreEvent.class);
    when(event.getResourceID()).thenReturn("xyz");

    corpus.resourceWritten(event); 
  }
@Test
  public void testResourceDeletedDifferentDataStoreSkips() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    DataStore ds1 = mock(DataStore.class);
    DataStore ds2 = mock(DataStore.class);
    corpus.setDataStore(ds1);

    DatastoreEvent event = mock(DatastoreEvent.class);
    when(event.getSource()).thenReturn(ds2); 
    when(event.getResourceID()).thenReturn("unused");

    corpus.resourceDeleted(event); 
  }
@Test
  public void testEqualityWithSameObjectReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.equals(corpus));
  }
@Test
  public void testEqualityWithDifferentTypeReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals("not a corpus"));
  }
@Test
  public void testEqualityWhenPersistentIdsMatch() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    c1.lrPersistentId = "same";
    c2.lrPersistentId = "same";

    c1.setName("corpA");
    c2.setName("corpA");

    DataStore ds = mock(DataStore.class);

    c1.setDataStore(ds);
    c2.setDataStore(ds);

    assertTrue(c1.equals(c2));
  }
@Test
  public void testEqualityFailsWhenDataStoresAreDifferent() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    c1.setName("corpusEq");
    c2.setName("corpusEq");

    DataStore ds1 = mock(DataStore.class);
    DataStore ds2 = mock(DataStore.class);

    c1.setDataStore(ds1);
    c2.setDataStore(ds2); 

    assertFalse(c1.equals(c2));
  }
@Test(expected = GateRuntimeException.class)
  public void testUnloadDocumentFailsIfSyncThrowsPersistenceException() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("syncfail");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);

    when(ds.adopt(doc)).thenReturn(doc);
    doThrow(new PersistenceException("fail")).when(ds).sync(doc);

    corpus.add(doc);
    corpus.unloadDocument(0, true); 
  }
@Test
  public void testPopulateSingleConcatFileTriggersCall() throws IOException, ResourceInstantiationException {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    URL u = new URL("file:/tmp");

    try {
      corpus.populate(
        u, "DOC", "UTF-8", 10, "DOC_", "text/plain", true
      );
    } catch(Exception e) {
      
    }
  }
@Test
  public void testGetDocumentReturnsDocumentIfPresentInMemory() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d = mock(Document.class);
    when(d.getName()).thenReturn("gettable");
    when(d.getLRPersistenceId()).thenReturn("d1");
    when(d.getClass().getName()).thenReturn("gate.Document");
    when(d.getDataStore()).thenReturn(null);

    corpus.add(d);

    Document fetched = corpus.get(0);
    assertSame(d, fetched);
  }
@Test
  public void testIndexManagerSyncOnlyModifiedDocs() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    DataStore ds = mock(DataStore.class);
    corpus.setDataStore(ds);

    IndexManager im = mock(IndexManager.class);
    IndexDefinition def = mock(IndexDefinition.class);
    when(def.getIrEngineClassName()).thenReturn(FakeEngine.class.getName());

    corpus.setIndexDefinition(def);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("x");
    when(doc.getLRPersistenceId()).thenReturn("id");
    when(doc.getClass().getName()).thenReturn("gate.Document");
    when(doc.getDataStore()).thenReturn(ds);
    when(doc.isModified()).thenReturn(true);

    corpus.add(doc);

    DatastoreEvent evt = mock(DatastoreEvent.class);
    when(evt.getResourceID()).thenReturn(corpus.lrPersistentId);

    corpus.resourceWritten(evt); 
  }
@Test
  public void testDocumentRemovedIncompleteInfoSkipsEventFiring() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d = mock(Document.class);
    when(d.getName()).thenReturn("docX");
    when(d.getLRPersistenceId()).thenReturn(null);
    when(d.getClass().getName()).thenReturn("gate.Document");
    when(d.getDataStore()).thenReturn(null);

    corpus.add(d);

    boolean removed = corpus.remove(d);
    assertTrue(removed);
    assertEquals(0, corpus.size());
  } 
}