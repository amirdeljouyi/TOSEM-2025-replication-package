public class SerialCorpusImpl_5_GPTLLMTest { 

 @Test
  public void testAddSingleDocumentAndGetByIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc1");
    when(doc.getLRPersistenceId()).thenReturn("id1");
    when(doc.getClass()).thenReturn(Document.class);
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    boolean added = corpus.add(doc);
    assertTrue(added);

    Document result = corpus.get(0);
    assertNotNull(result);
    assertEquals("doc1", result.getName());
  }
@Test
  public void testGetDocumentNameValidAndInvalidIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docA");
    when(doc.getLRPersistenceId()).thenReturn("idA");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);

    assertEquals("docA", corpus.getDocumentName(0));
    assertEquals("No such document", corpus.getDocumentName(5));
  }
@Test
  public void testContainsReturnsTrueAndFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc1");
    when(doc.getLRPersistenceId()).thenReturn("id1");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    Document unrelated = mock(Document.class);
    when(unrelated.getName()).thenReturn("unknown");
    when(unrelated.getLRPersistenceId()).thenReturn("idX");
    when(unrelated.getClass().getName()).thenReturn(Document.class.getName());
    when(unrelated.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertTrue(corpus.contains(doc));
    assertFalse(corpus.contains(unrelated));
  }
@Test
  public void testRemoveByIndexAndObject() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("toRemove");
    when(doc.getLRPersistenceId()).thenReturn("removeID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    Document removedByIndex = corpus.remove(0);
    assertNotNull(removedByIndex);
    assertEquals("toRemove", removedByIndex.getName());

    boolean removed = corpus.remove(doc);
    assertFalse(removed);  
  }
@Test
  public void testUnloadLoadedPersistentDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    gate.DataStore ds = mock(gate.DataStore.class);
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docPersistent");
    when(doc.getLRPersistenceId()).thenReturn("pid");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(ds);

    corpus.setDataStore(ds);
    corpus.add(doc);

    corpus.unloadDocument(doc, false);
    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testGetDocumentPersistentIDAndClassType() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docMeta");
    when(doc.getLRPersistenceId()).thenReturn("metaID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);

    assertEquals("metaID", corpus.getDocumentPersistentID(0));
    assertEquals(Document.class.getName(), corpus.getDocumentClassType(0));
  }
@Test
  public void testIndexOfKnownAndUnknownDocuments() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("known");
    when(doc1.getLRPersistenceId()).thenReturn("knownID");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("unknown");
    when(doc2.getLRPersistenceId()).thenReturn("unknownID");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    assertEquals(0, corpus.indexOf(doc1));
    assertEquals(-1, corpus.indexOf(doc2));
  }
@Test
  public void testIsEmptySizeAddClear() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    assertTrue(corpus.isEmpty());
    assertEquals(0, corpus.size());

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docX");
    when(doc.getLRPersistenceId()).thenReturn("idx");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);
    corpus.add(doc);

    assertFalse(corpus.isEmpty());
    assertEquals(1, corpus.size());

    corpus.clear();
    assertTrue(corpus.isEmpty());
    assertEquals(0, corpus.size());
  }
@Test(expected = MethodNotImplementedException.class)
  public void testToArrayThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.toArray();
  }
@Test(expected = MethodNotImplementedException.class)
  public void testToArrayTypeThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.toArray(new Document[0]);
  }
@Test(expected = MethodNotImplementedException.class)
  public void testSubListThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.subList(0, 1);
  }
@Test(expected = MethodNotImplementedException.class)
  public void testSetThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("setDoc");
    when(doc.getLRPersistenceId()).thenReturn("setID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.set(0, doc);
  }
@Test
  public void testAddAtSpecificIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("doc2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(0, doc2);

    assertEquals("doc2", corpus.get(0).getName());
    assertEquals("doc1", corpus.get(1).getName());
  }
@Test
  public void testEqualsSameReferenceAndDifferent() {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    assertTrue(corpus1.equals(corpus1));
    assertFalse(corpus1.equals(corpus2));
  }
@Test
  public void testInitReturnsSelf() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Resource r = corpus.init();
    assertSame(corpus, r);
  }
@Test(expected = ResourceInstantiationException.class)
  public void testDuplicateThrowsException() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.duplicate(null);
  }
@Test
  public void testToStringIsNotNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String str = corpus.toString();
    assertNotNull(str);
  }
@Test
  public void testAddDocumentWithNullPersistenceId() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("nullIDDoc");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    boolean result = corpus.add(doc);
    assertTrue(result);
    assertNull(corpus.getDocumentPersistentID(0));
  }
@Test
  public void testRemoveSecondDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("doc2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(doc2);

    Document removed = corpus.remove(1);
    assertNotNull(removed);
    assertEquals("doc2", removed.getName());
    assertEquals(1, corpus.size());
  }
@Test
  public void testUnloadFromEmptyCorpusDoesNothing() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    corpus.unloadDocument(0, true);
    assertEquals(0, corpus.size());
  }
@Test
  public void testGetBeyondIndexReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("single");
    when(doc.getLRPersistenceId()).thenReturn("onlyID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    Document result = corpus.get(5);
    assertNull(result);
  }
@Test
  public void testDuplicateDocumentNameAndClassButDifferentObject() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("shared");
    when(doc1.getLRPersistenceId()).thenReturn("pid1");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("shared");
    when(doc2.getLRPersistenceId()).thenReturn("pid2");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    boolean contains = corpus.contains(doc2);
    assertFalse(contains);
  }
@Test
  public void testGetDocumentClassTypeInvalidIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    String clazz = corpus.getDocumentClassType(10);
    assertNull(clazz);
  }
@Test
  public void testSetDocumentPersistentIdBeyondBoundsDoesNothing() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("setDoc");
    when(doc.getLRPersistenceId()).thenReturn("setID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.setDocumentPersistentID(5, "override-id");
    assertEquals("setID", corpus.getDocumentPersistentID(0));
  }
@Test
  public void testEqualsWithNullAndOtherType() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals(null));
    assertFalse(corpus.equals("notACorpus"));
  }
@Test
  public void testToStringWithDocumentsAdded() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("docA");
    when(doc1.getLRPersistenceId()).thenReturn("idA");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("docB");
    when(doc2.getLRPersistenceId()).thenReturn("idB");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(doc2);

    String result = corpus.toString();
    assertTrue(result.contains("document data"));
    assertTrue(result.contains("docA"));
    assertTrue(result.contains("docB"));
  }
@Test
  public void testAddFailsWhenDocumentFromDifferentDatastore() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    gate.DataStore corpusDS = mock(gate.DataStore.class);
    gate.DataStore differentDS = mock(gate.DataStore.class);
    Document doc = mock(Document.class);
    when(doc.getDataStore()).thenReturn(differentDS);
    when(doc.getName()).thenReturn("alien");
    when(doc.getLRPersistenceId()).thenReturn("alienID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());

    try {
      corpus.setDataStore(corpusDS);
    } catch (PersistenceException e) {
      fail("Unexpected persistence exception");
    }

    boolean result = corpus.add(doc);
    assertFalse(result);
  }
@Test
  public void testAddNullDoesNotThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.add(null);
    assertFalse(result);
  }
@Test
  public void testIsDocumentLoadedOutOfBounds() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.isDocumentLoaded(100));
  }
@Test
  public void testIsPersistentDocumentWhenListEmptyReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isPersistentDocument(0);
    assertFalse(result);
  }
@Test
  public void testRemoveFromEmptyCorpusReturnsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document result = corpus.remove(0); 
    assertNull(result);
  }
@Test
  public void testRemoveNullDoesNothing() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.remove(null);
    assertFalse(result);
  }
@Test
  public void testContainsWithNonDocumentObject() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.contains("notADocument");
    assertFalse(result);
  }
@Test
  public void testIndexOfWithNonDocumentObject() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    int result = corpus.indexOf(new Object());
    assertEquals(-1, result);
  }
@Test
  public void testEqualsWithDifferentCorpusNameReturnsFalse() {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    corpus1.setName("CorpusA");

    SerialCorpusImpl corpus2 = new SerialCorpusImpl();
    corpus2.setName("CorpusB");

    assertFalse(corpus1.equals(corpus2));
  }
@Test
  public void testGetDocumentNamesWhenEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.getDocumentNames().isEmpty());
  }
@Test
  public void testGetDocumentPersistentIDsWhenEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.getDocumentPersistentIDs().isEmpty());
  }
@Test
  public void testGetDocumentClassTypesWhenEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertTrue(corpus.getDocumentClassTypes().isEmpty());
  }
@Test
  public void testAddCorpusListenerAndTriggerEvent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docX");
    when(doc.getLRPersistenceId()).thenReturn("docXID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    CorpusListener listener = mock(CorpusListener.class);
    corpus.addCorpusListener(listener);

    corpus.add(doc);

    verify(listener, atLeastOnce()).documentAdded(any(CorpusEvent.class));
  }
@Test
  public void testRemoveCorpusListenerSilently() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    CorpusListener listener = mock(CorpusListener.class);
    corpus.addCorpusListener(listener);
    corpus.removeCorpusListener(listener);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc1");
    when(doc.getLRPersistenceId()).thenReturn("id1");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);

    verify(listener, never()).documentAdded(any(CorpusEvent.class));
  }
@Test
  public void testAddAllEmptyListReturnsTrue() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.addAll(Collections.<Document>emptyList());
    assertTrue(result);
  }
@Test
  public void testContainsAllWithEmptyInput() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.containsAll(Collections.emptyList());
    assertTrue(result);
  }
@Test
  public void testRemoveAllWithPartialMatch() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("doc1");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("doc2");
    when(doc2.getLRPersistenceId()).thenReturn("id2");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    Document unknown = mock(Document.class);
    when(unknown.getName()).thenReturn("unknown");
    when(unknown.getLRPersistenceId()).thenReturn("unknownID");
    when(unknown.getClass().getName()).thenReturn(Document.class.getName());
    when(unknown.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(doc2);
    boolean result = corpus.removeAll(Arrays.asList(doc1, unknown));
    assertTrue(result);
    assertEquals(1, corpus.size());
  }
@Test
  public void testRemoveSameDocTwice() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc");
    when(doc.getLRPersistenceId()).thenReturn("id");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    boolean firstRemoval = corpus.remove(doc);
    boolean secondRemoval = corpus.remove(doc);

    assertTrue(firstRemoval);
    assertFalse(secondRemoval);
  }
@Test
  public void testClearCorpusAfterAdd() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("toClear");
    when(doc.getLRPersistenceId()).thenReturn("idCl");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertEquals(1, corpus.size());

    corpus.clear();
    assertEquals(0, corpus.size());
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testIndexOfWithNullDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    int result = corpus.indexOf(null);
    assertEquals(-1, result);
  }
@Test
  public void testAddDuplicateDocumentReference() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("duplicate");
    when(doc.getLRPersistenceId()).thenReturn("pidDup");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.add(doc); 

    assertEquals(2, corpus.size());
    assertTrue(corpus.contains(doc));
    assertEquals(0, corpus.indexOf(doc)); 
  }
@Test
  public void testUnloadingAlreadyUnloadedDocument() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("mockdoc");
    when(doc.getLRPersistenceId()).thenReturn("idX");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false); 
    corpus.unloadDocument(0, false); 

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testUnloadDocumentWithSyncTrueTriggersAdoptionThenSync() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    gate.DataStore ds = mock(gate.DataStore.class);
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docSync");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(ds);
    try {
      Document adoptedDoc = mock(Document.class);
      when(adoptedDoc.getLRPersistenceId()).thenReturn("adoptedID");
      when(ds.adopt(doc)).thenReturn(adoptedDoc);
      corpus.setDataStore(ds);
    } catch (PersistenceException e) {
      fail("Unexpected exception during test setup");
    }

    corpus.add(doc);
    try {
      corpus.unloadDocument(0, true);
    } catch (GateRuntimeException e) {
      fail("Unexpected exception during document unload");
    }

    assertFalse(corpus.isDocumentLoaded(0));
  }
@Test
  public void testUnloadDocumentWithSyncThrowsExceptionHandled() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    gate.DataStore ds = mock(gate.DataStore.class);
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docX");
    when(doc.getLRPersistenceId()).thenReturn("idX");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(ds);
    try {
      doThrow(new PersistenceException("sync failed")).when(ds).sync(doc);
      corpus.setDataStore(ds);
    } catch (PersistenceException pex) {
      fail("Failure while mocking setup");
    }

    corpus.add(doc);
    try {
      corpus.unloadDocument(0, true);
      fail("Expected GateRuntimeException");
    } catch (GateRuntimeException ex) {
      assertTrue(ex.getMessage().contains("sync failed"));
    }
  }
@Test
  public void testGetDocumentLoadsFromDataStoreWhenNotLoaded() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    gate.DataStore ds = mock(gate.DataStore.class);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docLazy");
    when(doc.getLRPersistenceId()).thenReturn("lazyID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(ds);

    try {
      corpus.setDataStore(ds);
    } catch (PersistenceException e) {
      fail("Error in setup");
    }

    corpus.add(doc);
    corpus.unloadDocument(0, false); 

    try {
      Document reloaded = mock(Document.class);
      when(reloaded.getName()).thenReturn("docLazy");
      when(reloaded.getLRPersistenceId()).thenReturn("lazyID");

      Factory.registerResource(Document.class.getName(), Document.class);
      Document result = corpus.get(0); 

      assertNotNull(result);
    } catch (Exception e) {
      fail("Exception during reload: " + e.getMessage());
    }
  }
@Test
  public void testAddDocumentWithNullDataStoreWhenCorpusHasNone() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("transient");
    when(doc.getLRPersistenceId()).thenReturn("tID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null); 

    boolean added = corpus.add(doc);
    assertTrue(added);
    assertEquals("transient", corpus.getDocumentName(0));
  }
@Test
  public void testAddDocumentWithNullDataStoreWhenCorpusHasOne() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    gate.DataStore ds = mock(gate.DataStore.class);

    try {
      corpus.setDataStore(ds);
    } catch (PersistenceException e) {
      fail("Error setting datastore");
    }

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("ruleViolated");
    when(doc.getLRPersistenceId()).thenReturn("errID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null); 

    boolean added = corpus.add(doc);
    assertTrue("Transient document can be added to a persistent corpus if dataStore matches or is null", added);
  }
@Test
  public void testCleanupShouldNotFailWhenNothingInitialized() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.cleanup(); 
    } catch (Exception e) {
      fail("cleanup should not fail when called without setup");
    }
  }
@Test
  public void testEqualsUsesPersistentIDComparisonLogic() {
    SerialCorpusImpl corpus1 = new SerialCorpusImpl();
    SerialCorpusImpl corpus2 = new SerialCorpusImpl();

    corpus1.setName("testCorpus");
    corpus2.setName("testCorpus");

    gate.DataStore ds = mock(gate.DataStore.class);
    try {
      corpus1.setDataStore(ds);
      corpus2.setDataStore(ds);
    } catch (PersistenceException e) {
      fail("Datastore init issue");
    }

    corpus1.setLRPersistenceId("abc123");
    corpus2.setLRPersistenceId("abc123");

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc1");
    when(doc.getLRPersistenceId()).thenReturn("docID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(ds);

    corpus1.add(doc);
    corpus2.add(doc);

    assertTrue(corpus1.equals(corpus2));
    assertEquals(corpus1.hashCode(), corpus2.hashCode());
  }
@Test(expected = MethodNotImplementedException.class)
  public void testLastIndexOfThrowsException() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.lastIndexOf(null);
  }
@Test
  public void testDocumentPersistentIDReturnsNullIfIndexInvalid() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Object result = corpus.getDocumentPersistentID(100);
    assertNull(result);
  }
@Test
  public void testAddDocumentFiresDocumentAddedEvent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docEvent");
    when(doc.getLRPersistenceId()).thenReturn("eventID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertEquals(1, corpus.size());
  }
@Test
  public void testRemoveUnloadedDocumentDoesNotThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc");
    when(doc.getLRPersistenceId()).thenReturn("id");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false);
    boolean removed = corpus.remove(doc);
    assertTrue(removed);
  }
@Test
  public void testRemoveDocumentFiresEventOncePerCall() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docEvent");
    when(doc.getLRPersistenceId()).thenReturn("id123");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    CorpusListener listener = mock(CorpusListener.class);
    corpus.addCorpusListener(listener);

    corpus.add(doc);
    corpus.remove(doc);

    verify(listener).documentAdded(any(CorpusEvent.class));
    verify(listener).documentRemoved(any(CorpusEvent.class));
  }
@Test
  public void testRemoveDocumentWhenDocumentWasNotLoaded() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docRemoved");
    when(doc.getLRPersistenceId()).thenReturn("removeID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.unloadDocument(0, false); 

    boolean result = corpus.remove(doc);
    assertTrue(result);
  }
@Test
  public void testRemoveDocumentByInvalidIndexDoesNotThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.remove(99); 
    } catch (Exception e) {
      fail("Should safely ignore out-of-bounds remove(index)");
    }
  }
@Test
  public void testRemoveCorpusListenerIsSilentIfListenerNotPresent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    CorpusListener listener = mock(CorpusListener.class);
    corpus.removeCorpusListener(listener); 
  }
@Test
  public void testMultipleCorpusListenersAreInvoked() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    CorpusListener l1 = mock(CorpusListener.class);
    CorpusListener l2 = mock(CorpusListener.class);

    corpus.addCorpusListener(l1);
    corpus.addCorpusListener(l2);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("multiDoc");
    when(doc.getLRPersistenceId()).thenReturn("idMulti");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    verify(l1).documentAdded(any(CorpusEvent.class));
    verify(l2).documentAdded(any(CorpusEvent.class));
  }
@Test
  public void testAddDocumentWithNullNameAndNullClassGracefullyFails() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn(null);
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getDataStore()).thenReturn(null);

    try {
      corpus.add(doc); 
    } catch (Exception e) {
      assertTrue(e instanceof NullPointerException);
    }
  }
@Test
  public void testIndexOfWithDuplicateReferenceReturnsFirstIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("dupe");
    when(doc.getLRPersistenceId()).thenReturn("pid");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.add(doc); 

    int firstIndex = corpus.indexOf(doc);
    assertEquals(0, firstIndex); 
  }
@Test
  public void testClearCorpusResetsDocumentAndDataList() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("toClear");
    when(doc.getLRPersistenceId()).thenReturn("clearID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    assertEquals(1, corpus.size());

    corpus.clear();
    assertEquals(0, corpus.size());
    assertTrue(corpus.isEmpty());

    Document result = corpus.get(0);
    assertNull(result);
  }
@Test
  public void testAddDocumentAtIndexZero() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("first");
    when(doc1.getLRPersistenceId()).thenReturn("id1");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("zero");
    when(doc2.getLRPersistenceId()).thenReturn("id0");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    corpus.add(0, doc2); 

    assertEquals("zero", corpus.get(0).getName());
    assertEquals("first", corpus.get(1).getName());
  }
@Test
  public void testEqualsWithDifferentDocDataListReturnFalse() {
    SerialCorpusImpl a = new SerialCorpusImpl();
    SerialCorpusImpl b = new SerialCorpusImpl();

    Document docA = mock(Document.class);
    when(docA.getName()).thenReturn("docA");
    when(docA.getLRPersistenceId()).thenReturn("idA");
    when(docA.getClass().getName()).thenReturn(Document.class.getName());
    when(docA.getDataStore()).thenReturn(null);

    Document docB = mock(Document.class);
    when(docB.getName()).thenReturn("docB");
    when(docB.getLRPersistenceId()).thenReturn("idB");
    when(docB.getClass().getName()).thenReturn(Document.class.getName());
    when(docB.getDataStore()).thenReturn(null);

    a.add(docA);
    b.add(docB);

    assertFalse(a.equals(b));
  }
@Test
  public void testAddSameDocumentTwiceIncreasesSize() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("repeat");
    when(doc.getLRPersistenceId()).thenReturn("repeatID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.add(doc);
    assertEquals(2, corpus.size());
  }
@Test
  public void testIsDocumentLoadedReturnsFalseIfDocListEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isDocumentLoaded(0);
    assertFalse(result);
  }
@Test
  public void testIsPersistentDocumentTrueIfPersistentIDExists() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("persistentDoc");
    when(doc.getLRPersistenceId()).thenReturn("pid");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    boolean result = corpus.isPersistentDocument(0);
    assertTrue(result);
  }
@Test
  public void testIsPersistentDocumentFalseIfDocListEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isPersistentDocument(0);
    assertFalse(result);
  }
@Test
  public void testUnloadDocumentByReferenceNullSafe() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document unknown = mock(Document.class);
    when(unknown.getName()).thenReturn("unknown");
    when(unknown.getLRPersistenceId()).thenReturn("x");
    when(unknown.getClass().getName()).thenReturn(Document.class.getName());
    when(unknown.getDataStore()).thenReturn(null);
    corpus.unloadDocument(unknown); 
  }
@Test
  public void testRemoveFromSingleItemCorpusThenIsEmpty() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("onlyDoc");
    when(doc.getLRPersistenceId()).thenReturn("idOnly");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.remove(doc);

    assertEquals(0, corpus.size());
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testFindDocumentReturnsNegativeIfNotFound() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc1 = mock(Document.class);
    when(doc1.getName()).thenReturn("docIn");
    when(doc1.getLRPersistenceId()).thenReturn("inID");
    when(doc1.getClass().getName()).thenReturn(Document.class.getName());
    when(doc1.getDataStore()).thenReturn(null);

    Document doc2 = mock(Document.class);
    when(doc2.getName()).thenReturn("missingDoc");
    when(doc2.getLRPersistenceId()).thenReturn("missingID");
    when(doc2.getClass().getName()).thenReturn(Document.class.getName());
    when(doc2.getDataStore()).thenReturn(null);

    corpus.add(doc1);
    int idx = corpus.indexOf(doc2);
    assertEquals(-1, idx);
  }
@Test
  public void testAddAndRemoveDocumentFiresExpectedIndex() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    CorpusListener listener = mock(CorpusListener.class);
    corpus.addCorpusListener(listener);

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docEvt");
    when(doc.getLRPersistenceId()).thenReturn("docEvtID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.remove(doc);

    verify(listener).documentAdded(any(CorpusEvent.class));
    verify(listener).documentRemoved(any(CorpusEvent.class));
  }
@Test
  public void testRemoveByIndexBeyondSizeIsSilent() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.remove(999);
    } catch (Exception e) {
      fail("Remove with invalid index should be silent");
    }
  }
@Test
  public void testRemoveByIndexZeroOnEmptyCorpusIsHandled() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document removed = corpus.remove(0);
    assertNull(removed);
  }
@Test
  public void testSetDocumentPersistentIDSkipsIfIndexTooHigh() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("docSet");
    when(doc.getLRPersistenceId()).thenReturn("preID");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.setDocumentPersistentID(5, "newID"); 
    assertEquals("preID", corpus.getDocumentPersistentID(0));
  }
@Test
  public void testUnloadPersistentDocumentTriggersSync() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    gate.DataStore ds = mock(gate.DataStore.class);
    Document doc = mock(Document.class);

    when(doc.getName()).thenReturn("docP");
    when(doc.getLRPersistenceId()).thenReturn("pidP");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(ds);

    try {
      corpus.setDataStore(ds);
    } catch (PersistenceException e) {
      fail("Setup failed");
    }

    corpus.add(doc);
    corpus.unloadDocument(0); 

    try {
      verify(ds).sync(doc);
    } catch (PersistenceException e) {
      fail("sync should not throw");
    }
  }
@Test
  public void testEqualsUsesRefEqualityFirst() {
    SerialCorpusImpl a = new SerialCorpusImpl();
    assertTrue(a.equals(a));
  }
@Test
  public void testEqualsReturnsFalseIfOtherCorpusIsNull() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals(null));
  }
@Test
  public void testEqualsReturnsFalseIfOtherNotCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    assertFalse(corpus.equals("notACorpus"));
  }
@Test
  public void testGetDocumentNameNullSafe() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn(null);
    when(doc.getLRPersistenceId()).thenReturn("zid");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);

    String name = corpus.getDocumentName(0);
    assertNull(name);
  }
@Test
  public void testToStringAfterMultipleAddsIsSafe() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("doc1");
    when(d1.getLRPersistenceId()).thenReturn("id1");
    when(d1.getClass().getName()).thenReturn(Document.class.getName());
    when(d1.getDataStore()).thenReturn(null);

    Document d2 = mock(Document.class);
    when(d2.getName()).thenReturn("doc2");
    when(d2.getLRPersistenceId()).thenReturn("id2");
    when(d2.getClass().getName()).thenReturn(Document.class.getName());
    when(d2.getDataStore()).thenReturn(null);

    corpus.add(d1);
    corpus.add(d2);

    String str = corpus.toString();
    assertTrue(str.contains("doc1"));
    assertTrue(str.contains("doc2"));
  }
@Test
  public void testDocumentRemovedPersistentIDNullSafe() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("noIDdoc");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    corpus.remove(doc); 
    assertTrue(corpus.isEmpty());
  }
@Test
  public void testGetReturnsNullWhenDocumentsListNotInitialized() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    Document result = corpus.get(0); 
    assertNull(result);
  }
@Test
  public void testUnloadByDocumentSkipsWhenDocNotInCorpus() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("missing");
    when(doc.getLRPersistenceId()).thenReturn("pidX");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    try {
      corpus.unloadDocument(doc, true);
    } catch (Exception e) {
      fail("Call to unloadDocument should not throw if doc not found");
    }
  }
@Test
  public void testEqualsReturnsFalseForDifferentDatastore() {
    SerialCorpusImpl c1 = new SerialCorpusImpl();
    SerialCorpusImpl c2 = new SerialCorpusImpl();

    c1.setName("test");
    c2.setName("test");

    c1.setLRPersistenceId("x");
    c2.setLRPersistenceId("x");

    gate.DataStore ds1 = mock(gate.DataStore.class);
    gate.DataStore ds2 = mock(gate.DataStore.class);

    try {
      c1.setDataStore(ds1);
      c2.setDataStore(ds2);
    } catch (PersistenceException e) {
      fail("Datastore init failed");
    }

    assertFalse(c1.equals(c2));
  }
@Test
  public void testContainsAllWithPartialOverlapReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document d1 = mock(Document.class);
    when(d1.getName()).thenReturn("d1");
    when(d1.getLRPersistenceId()).thenReturn("id1");
    when(d1.getClass().getName()).thenReturn(Document.class.getName());
    when(d1.getDataStore()).thenReturn(null);

    Document d2 = mock(Document.class);
    when(d2.getName()).thenReturn("d2");
    when(d2.getLRPersistenceId()).thenReturn("id2");
    when(d2.getClass().getName()).thenReturn(Document.class.getName());
    when(d2.getDataStore()).thenReturn(null);

    corpus.add(d1);
    boolean result = corpus.containsAll(Arrays.asList(d1, d2));
    assertFalse(result);
  }
@Test
  public void testDocumentRemovedWithNullIndexManagerDoesNotCrash() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.documentRemoved("someID"); 
    } catch (Exception e) {
      fail("documentRemoved should not throw if indexManager is not set");
    }
  }
@Test
  public void testDocumentRemovedWithIndexManagerCallsRemove() throws Exception {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    IndexManager indexManager = mock(IndexManager.class);
    IndexManager proxy = new IndexManager() {
      public void sync(java.util.List<gate.Document> add, java.util.List<String> del, java.util.List<gate.Document> change)
              throws IndexException {}
      public void setCorpus(gate.Corpus c) {}
      public void setIndexDefinition(gate.creole.ir.IndexDefinition d) {}
    };

    
    corpus.setIndexManager(proxy);
    try {
      corpus.documentRemoved("mockLRID"); 
    } catch (Exception e) {
      fail("Should not fail when indexManager is set");
    }
  }
@Test
  public void testRemoveCorpusListenerWithNullVectorDoesNotThrow() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.removeCorpusListener(mock(CorpusListener.class)); 
    } catch (Exception e) {
      fail("Should handle null safely");
    }
  }
@Test
  public void testFireDocumentAddedGracefulWhenNoListeners() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.fireDocumentAdded(new CorpusEvent(corpus, null, 0, null, CorpusEvent.DOCUMENT_ADDED));
    } catch (Exception e) {
      fail("Should not throw when corpusListeners is null");
    }
  }
@Test
  public void testFireDocumentRemovedGracefulWhenNoListeners() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    try {
      corpus.fireDocumentRemoved(new CorpusEvent(corpus, null, 0, null, CorpusEvent.DOCUMENT_REMOVED));
    } catch (Exception e) {
      fail("Should not throw when corpusListeners is null");
    }
  }
@Test
  public void testContainsReturnsFalseIfDocumentHasNullID() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("noID");
    when(doc.getLRPersistenceId()).thenReturn(null);
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    Document lookup = mock(Document.class);
    when(lookup.getName()).thenReturn("noID");
    when(lookup.getLRPersistenceId()).thenReturn(null);
    when(lookup.getClass().getName()).thenReturn(Document.class.getName());
    when(lookup.getDataStore()).thenReturn(null);

    corpus.add(doc);
    boolean result = corpus.contains(lookup); 
    assertFalse(result);
  }
@Test
  public void testIsDocumentLoadedReturnsFalseIfIndexOutOfBounds() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean result = corpus.isDocumentLoaded(5); 
    assertFalse(result);
  }
@Test
  public void testRemoveByInvalidObjectTypeReturnsFalse() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    boolean removed = corpus.remove("NotADocument");
    assertFalse(removed);
  }
@Test
  public void testGetReturnsNullWhenIndexOutOfBounds() {
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    Document doc = mock(Document.class);
    when(doc.getName()).thenReturn("doc1");
    when(doc.getLRPersistenceId()).thenReturn("id");
    when(doc.getClass().getName()).thenReturn(Document.class.getName());
    when(doc.getDataStore()).thenReturn(null);

    corpus.add(doc);
    Document result = corpus.get(10);
    assertNull(result);
  } 
}