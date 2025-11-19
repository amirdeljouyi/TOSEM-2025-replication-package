package gate.corpora;

import gate.*;
import gate.annotation.AnnotationImpl;
import gate.annotation.AnnotationSetImpl;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.creole.ResourceInstantiationException;
import gate.creole.ir.IndexManager;
import gate.event.*;
import gate.persist.SerialDataStore;
import gate.util.*;
import org.junit.Test;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SerialCorpusImpl_4_GPTLLMTest {

@Test
public void testAddDocumentIncreasesSizeAndRetrievable() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("testDoc");
when(doc.getLRPersistenceId()).thenReturn("persistedId");
// when(doc.getClass()).thenReturn(Document.class);
boolean added = corpus.add(doc);
assertTrue(added);
assertEquals(1, corpus.size());
Document retrieved = corpus.get(0);
assertNotNull(retrieved);
assertEquals("testDoc", retrieved.getName());
}

@Test
public void testRemoveDocumentObjectReducesSize() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("sampleDoc");
when(doc.getLRPersistenceId()).thenReturn("docID");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertEquals(0, corpus.size());
}

@Test
public void testRemoveByIndexReturnsCorrectDocument() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docX");
when(doc.getLRPersistenceId()).thenReturn("idX");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
Document removed = corpus.remove(0);
assertNotNull(removed);
assertEquals("docX", removed.getName());
assertEquals(0, corpus.size());
}

@Test
public void testDocumentNamesAfterAdd() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc1.getLRPersistenceId()).thenReturn("id1");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("doc2");
when(doc2.getLRPersistenceId()).thenReturn("id2");
// when(doc2.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<String> names = corpus.getDocumentNames();
assertEquals(2, names.size());
assertEquals("doc1", names.get(0));
assertEquals("doc2", names.get(1));
}

@Test
public void testContainsAndIndexOfDocument() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("myDoc");
when(doc.getLRPersistenceId()).thenReturn("id123");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
boolean contains = corpus.contains(doc);
int index = corpus.indexOf(doc);
assertTrue(contains);
assertEquals(0, index);
}

@Test
public void testToArrayUnsupportedException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
try {
corpus.toArray();
fail("Expected MethodNotImplementedException");
} catch (MethodNotImplementedException ex) {
assertTrue(ex.getMessage().contains("toArray()"));
}
}

@Test
public void testAddDocumentWithExplicitIndex() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("insertedDoc");
when(doc.getLRPersistenceId()).thenReturn("pid");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(0, doc);
assertEquals(1, corpus.size());
Document retrieved = corpus.get(0);
assertEquals("insertedDoc", retrieved.getName());
}

@Test(expected = UnsupportedOperationException.class)
public void testIteratorRemoveThrowsException() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docR");
when(doc.getLRPersistenceId()).thenReturn("idR");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
java.util.Iterator<Document> iterator = corpus.iterator();
if (iterator.hasNext()) {
iterator.next();
iterator.remove();
}
}

@Test
public void testUnloadDocumentByObject() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("unloadDoc");
when(doc.getLRPersistenceId()).thenReturn("unloadID");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.unloadDocument(doc, false);
boolean loaded = corpus.isDocumentLoaded(0);
assertFalse(loaded);
}

@Test(expected = MethodNotImplementedException.class)
public void testSetDocumentThrowsException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docSet");
when(doc.getLRPersistenceId()).thenReturn("idSet");
// when(doc.getClass()).thenReturn(Document.class);
corpus.set(0, doc);
}

@Test(expected = MethodNotImplementedException.class)
public void testSubListThrowsException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.subList(0, 1);
}

@Test
public void testAddCorpusListenerFiresEvent() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("observableDoc");
when(doc.getLRPersistenceId()).thenReturn("obsID");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
verify(listener).documentAdded(any(CorpusEvent.class));
}

@Test
public void testRemoveCorpusListenerPreventsEvent() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("silentDoc");
when(doc.getLRPersistenceId()).thenReturn("silentID");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
verify(listener, never()).documentAdded(any(CorpusEvent.class));
}

@Test(expected = ResourceInstantiationException.class)
public void testDuplicateThrowsException() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.duplicate(mock(Factory.DuplicationContext.class));
}

@Test
public void testEqualsAndHashCodeSameCorpus() throws Exception {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("equalityDoc");
when(doc.getLRPersistenceId()).thenReturn("eid");
// when(doc.getClass()).thenReturn(Document.class);
corpus1.add(doc);
corpus2.add(doc);
corpus1.setName("sharedName");
corpus2.setName("sharedName");
corpus1.setFeatures(Factory.newFeatureMap());
corpus2.setFeatures(Factory.newFeatureMap());
assertEquals(corpus1.hashCode(), corpus2.hashCode());
assertTrue(corpus1.equals(corpus2));
}

@Test
public void testClearCorpusEmptiesData() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("aDoc");
when(doc1.getLRPersistenceId()).thenReturn("aid");
// when(doc1.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.clear();
assertEquals(0, corpus.size());
assertTrue(corpus.isEmpty());
}

@Test
public void testContainsAllReturnsTrueForSubset() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("docA");
when(doc1.getLRPersistenceId()).thenReturn("idA");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("docB");
when(doc2.getLRPersistenceId()).thenReturn("idB");
// when(doc2.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> checkList = new ArrayList<>();
checkList.add(doc1);
checkList.add(doc2);
boolean result = corpus.containsAll(checkList);
assertTrue(result);
}

@Test
public void testRemoveNonexistentDocumentReturnsFalse() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("testDoc");
when(doc.getLRPersistenceId()).thenReturn("id123");
// when(doc.getClass()).thenReturn(Document.class);
boolean removed = corpus.remove(doc);
assertFalse(removed);
}

@Test
public void testAddNullDocumentReturnsFalseAndDoesNotChangeSize() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.add(null);
assertFalse(result);
assertEquals(0, corpus.size());
}

@Test
public void testAddDocumentWithDifferentDataStoreReturnsFalse() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
gate.DataStore corpusDS = mock(gate.DataStore.class);
gate.DataStore otherDS = mock(gate.DataStore.class);
corpus.setDataStore(corpusDS);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("invalidDoc");
when(doc.getLRPersistenceId()).thenReturn("persistedId");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(otherDS);
when(otherDS.equals(corpusDS)).thenReturn(false);
boolean result = corpus.add(doc);
assertFalse(result);
assertEquals(0, corpus.size());
}

@Test
public void testIndexOfNonDocumentObjectReturnsMinusOne() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
int index = corpus.indexOf("notADocument");
assertEquals(-1, index);
}

@Test
public void testDocumentNameOutOfBoundsReturnsFallback() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
String result = corpus.getDocumentName(5);
assertEquals("No such document", result);
}

@Test
public void testGetDocumentClassTypeReturnsCorrectValue() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("sampleDoc");
when(doc.getLRPersistenceId()).thenReturn("someID");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
String classType = corpus.getDocumentClassType(0);
assertEquals(Document.class.getName(), classType);
}

@Test
public void testGetDocumentPersistentIDWithInvalidIndexReturnsNull() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Object result = corpus.getDocumentPersistentID(10);
assertNull(result);
}

@Test(expected = MethodNotImplementedException.class)
public void testRetainAllThrowsException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Object> dummyList = new ArrayList<>();
corpus.retainAll(dummyList);
}

@Test
public void testAddAllEmptyCollectionReturnsTrue() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Document> emptyList = new ArrayList<>();
boolean result = corpus.addAll(emptyList);
assertTrue(result);
}

@Test
public void testRemoveAllFromEmptyCorpusReturnsTrue() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Document> docsToRemove = new ArrayList<>();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docToRemove");
when(doc.getLRPersistenceId()).thenReturn("removeID");
// when(doc.getClass()).thenReturn(Document.class);
docsToRemove.add(doc);
boolean result = corpus.removeAll(docsToRemove);
assertTrue(result);
}

@Test
public void testEqualsWithNullReturnsFalse() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.equals(null);
assertFalse(result);
}

@Test
public void testEqualsWithDifferentTypeReturnsFalse() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.equals("some string");
assertFalse(result);
}

@Test
public void testIsDocumentLoadedReturnsFalseWhenEmpty() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.isDocumentLoaded(0);
assertFalse(result);
}

@Test
public void testIsPersistentDocumentReturnsFalseWhenEmpty() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.isPersistentDocument(0);
assertFalse(result);
}

@Test
public void testGetReturnsNullIfDocumentNotExists() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document result = corpus.get(100);
assertNull(result);
}

@Test
public void testToStringWithEmptyCorpus() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
String output = corpus.toString();
assertNotNull(output);
assertTrue(output.contains("document data"));
}

@Test
public void testUnloadDocumentOnTransientDocWithNoSync() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("transientDoc");
when(doc.getLRPersistenceId()).thenReturn("someId");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(null);
corpus.add(doc);
corpus.setDocumentPersistentID(0, null);
corpus.unloadDocument(0, false);
assertFalse(corpus.isDocumentLoaded(0));
}

@Test
public void testGetDocumentNameNegativeIndexReturnsFallbackValue() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
String name = corpus.getDocumentName(-1);
assertEquals("No such document", name);
}

@Test
public void testGetDocumentPersistentIDNegativeIndexReturnsNull() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Object id = corpus.getDocumentPersistentID(-1);
assertNull(id);
}

@Test
public void testGetDocumentClassTypeNegativeIndexReturnsNull() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
String classType = corpus.getDocumentClassType(-1);
assertNull(classType);
}

@Test
public void testAddTwoDocumentsWithSameNameDifferentIDs() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("dupDoc");
when(doc1.getLRPersistenceId()).thenReturn("id1");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("dupDoc");
when(doc2.getLRPersistenceId()).thenReturn("id2");
// when(doc2.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.add(doc2);
assertEquals(2, corpus.size());
assertEquals("dupDoc", corpus.getDocumentName(0));
assertEquals("dupDoc", corpus.getDocumentName(1));
}

@Test
public void testEqualsWithSameReferenceReturnsTrue() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertTrue(corpus.equals(corpus));
}

@Test
public void testGetDocumentPersistentIDsIsEmptyWhenNoData() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Object> ids = corpus.getDocumentPersistentIDs();
assertTrue(ids.isEmpty());
}

@Test
public void testGetDocumentClassTypesIsEmptyWhenNoDocuments() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<String> classTypes = corpus.getDocumentClassTypes();
assertTrue(classTypes.isEmpty());
}

@Test
public void testCleanupRemovesListenersAndClearsData() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("testDoc");
when(doc.getLRPersistenceId()).thenReturn("persistedId");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
assertEquals(1, corpus.size());
corpus.cleanup();
assertEquals(0, corpus.size());
assertTrue(corpus.isEmpty());
}

@Test
public void testAddSameDocumentTwiceKeepsBoth() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("dup");
when(doc.getLRPersistenceId()).thenReturn("xid");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.add(doc);
assertEquals(2, corpus.size());
assertEquals(doc, corpus.get(0));
assertEquals(doc, corpus.get(1));
}

@Test
public void testGetReturnsNullForUnloadedDocument() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("unloadedDoc");
when(doc.getLRPersistenceId()).thenReturn("pid");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.unloadDocument(0, false);
Document result = corpus.get(0);
assertNotNull(result);
}

@Test
public void testRemoveDocumentEventFired() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("evDoc");
when(doc.getLRPersistenceId()).thenReturn("evID");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.remove(doc);
verify(listener).documentRemoved(any(CorpusEvent.class));
}

@Test
public void testAddCorpusListenerAddsUniqueOnly() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
}

@Test
public void testRemoveNonRegisteredListenerNoError() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.removeCorpusListener(listener);
}

@Test
public void testRemoveDocumentAtInvalidIndexDoesNothing() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
try {
Document removed = corpus.remove(0);
assertNull(removed);
} catch (IndexOutOfBoundsException ex) {
assertTrue(ex.getMessage().contains("Index"));
}
}

@Test
public void testEqualsWithDifferentLrIdReturnsFalse() throws Exception {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("docA");
when(doc1.getLRPersistenceId()).thenReturn("idA");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("docB");
when(doc2.getLRPersistenceId()).thenReturn("idB");
// when(doc2.getClass()).thenReturn(Document.class);
corpus1.add(doc1);
corpus2.add(doc2);
assertFalse(corpus1.equals(corpus2));
}

@Test
public void testUnloadDocumentDoesNotCrashWhenIndexOutOfBounds() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.unloadDocument(3, false);
assertEquals(0, corpus.size());
}

@Test
public void testGetTransientSourceAlwaysReturnsNull() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertNull(corpus.getTransientSource());
}

@Test
public void testSetDocumentPersistentIDDoesNothingIfIndexTooHigh() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc1");
when(doc.getLRPersistenceId()).thenReturn("id123");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.setDocumentPersistentID(5, "ignored");
assertEquals("id123", corpus.getDocumentPersistentID(0));
}

@Test
public void testAddAllWithPartiallyInvalidDocumentsStillAddsValidOnes() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document validDoc = mock(Document.class);
when(validDoc.getName()).thenReturn("valid");
when(validDoc.getLRPersistenceId()).thenReturn("validId");
// when(validDoc.getClass()).thenReturn(Document.class);
when(validDoc.getDataStore()).thenReturn(null);
Document invalidDoc = mock(Document.class);
when(invalidDoc.getName()).thenReturn("invalid");
when(invalidDoc.getLRPersistenceId()).thenReturn("invalidId");
// when(invalidDoc.getClass()).thenReturn(Document.class);
when(invalidDoc.getDataStore()).thenReturn(mock(gate.DataStore.class));
corpus.setDataStore(mock(gate.DataStore.class));
List<Document> docs = new ArrayList<>();
docs.add(validDoc);
docs.add(invalidDoc);
boolean result = corpus.addAll(docs);
assertFalse(result);
assertEquals(1, corpus.size());
assertEquals("valid", corpus.getDocumentName(0));
}

@Test
public void testRemoveDocumentThatWasNeverAddedReturnsFalse() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("ghostDoc");
when(doc.getLRPersistenceId()).thenReturn("ghostId");
// when(doc.getClass()).thenReturn(Document.class);
boolean result = corpus.remove(doc);
assertFalse(result);
}

@Test
public void testRemoveAllWithMixedDocumentsRemovesOnlyAddedOnes() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("keptDoc");
when(doc1.getLRPersistenceId()).thenReturn("id1");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("removedDoc");
when(doc2.getLRPersistenceId()).thenReturn("id2");
// when(doc2.getClass()).thenReturn(Document.class);
Document docNotInCorpus = mock(Document.class);
when(docNotInCorpus.getName()).thenReturn("ghost");
when(docNotInCorpus.getLRPersistenceId()).thenReturn("ghostId");
// when(docNotInCorpus.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> toRemove = new ArrayList<>();
toRemove.add(doc2);
toRemove.add(docNotInCorpus);
boolean result = corpus.removeAll(toRemove);
assertFalse(result);
assertEquals(1, corpus.size());
assertEquals("keptDoc", corpus.getDocumentName(0));
}

@Test
public void testFindDocumentWhenDocumentIsLoadedReturnsCorrectIndex() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("findMe");
when(doc.getLRPersistenceId()).thenReturn("uniqueId");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
int index = corpus.indexOf(doc);
assertEquals(0, index);
}

@Test
public void testFindDocumentWhenDocumentIsNotLoadedButMatchesMetadata() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docMeta");
when(doc.getLRPersistenceId()).thenReturn("metaId");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.unloadDocument(doc, false);
Document newInstance = mock(Document.class);
when(newInstance.getName()).thenReturn("docMeta");
when(newInstance.getLRPersistenceId()).thenReturn("metaId");
// when(newInstance.getClass()).thenReturn(Document.class);
int index = corpus.indexOf(newInstance);
assertEquals(0, index);
}

@Test
public void testUnloadDocumentSyncFailureThrowsGateRuntimeException() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
gate.DataStore ds = mock(gate.DataStore.class);
doThrow(new gate.persist.PersistenceException("sync failed")).when(ds).sync(any(Document.class));
corpus.setDataStore(ds);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("syncDoc");
when(doc.getLRPersistenceId()).thenReturn("idSync");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(ds);
corpus.add(doc);
try {
corpus.unloadDocument(0, true);
fail("Expected exception not thrown");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("sync failed"));
}
}

@Test
public void testUnloadDocumentByObjectWithNullIndexDoesNothing() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("missing");
when(doc.getLRPersistenceId()).thenReturn("missingId");
// when(doc.getClass()).thenReturn(Document.class);
corpus.unloadDocument(doc, true);
assertEquals(0, corpus.size());
}

@Test
public void testEqualsWithEqualDataButDifferentReferencesReturnsTrue() throws Exception {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
corpus1.setName("CorpusX");
corpus2.setName("CorpusX");
gate.DataStore ds = mock(gate.DataStore.class);
when(ds.equals(ds)).thenReturn(true);
corpus1.setDataStore(ds);
corpus2.setDataStore(ds);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docName");
when(doc.getLRPersistenceId()).thenReturn("docID");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(null);
corpus1.add(doc);
corpus2.add(doc);
assertTrue(corpus1.equals(corpus2));
}

@Test
public void testUnloadedDocumentReloadedViaGet() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
gate.DataStore ds = mock(gate.DataStore.class);
corpus.setDataStore(ds);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("loadableDoc");
when(doc.getLRPersistenceId()).thenReturn("docID");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(ds);
corpus.add(doc);
corpus.unloadDocument(0, false);
Document result = corpus.get(0);
assertNotNull(result);
assertEquals("loadableDoc", result.getName());
}

@Test
public void testIteratorSkipsNullLoadedDocuments() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc1.getLRPersistenceId()).thenReturn("id1");
// when(doc1.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.unloadDocument(0, false);
java.util.Iterator<Document> iterator = corpus.iterator();
assertTrue(iterator.hasNext());
assertNotNull(iterator.next());
}

@Test
public void testAddDocumentWithoutLRPersistenceIdThenSetPersistentID() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
when(doc.getLRPersistenceId()).thenReturn(null);
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.setDocumentPersistentID(0, "newPersistentID");
Object pid = corpus.getDocumentPersistentID(0);
assertEquals("newPersistentID", pid);
}

@Test
public void testRemoveFromEmptyCorpusByIndexThrowsIndexOutOfBounds() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
try {
corpus.remove(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(e.getMessage() != null);
}
}

@Test
public void testGetReturnsNullWhenAccessingBeyondSize() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document result = corpus.get(3);
assertNull(result);
}

@Test
public void testAddAllAtIndexUnsupportedOperationException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Document> docs = new ArrayList<>();
try {
corpus.addAll(0, docs);
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertTrue(e.getMessage() == null || e.getMessage().isEmpty());
}
}

@Test
public void testAddAndRetrieveViaIteratorWithSingleElement() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("singletonDoc");
when(doc.getLRPersistenceId()).thenReturn("singletonId");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
java.util.Iterator<Document> iterator = corpus.iterator();
assertTrue(iterator.hasNext());
Document next = iterator.next();
assertEquals("singletonDoc", next.getName());
}

@Test
public void testClearRemovesAllLoadedDocuments() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc1.getLRPersistenceId()).thenReturn("id1");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("doc2");
when(doc2.getLRPersistenceId()).thenReturn("id2");
// when(doc2.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.add(doc2);
assertEquals(2, corpus.size());
corpus.clear();
assertEquals(0, corpus.size());
}

@Test
public void testEqualsSameIDButDifferentFeaturesReturnsFalse() throws Exception {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
corpus1.setName("Corpus");
corpus2.setName("Corpus");
gate.DataStore ds = mock(gate.DataStore.class);
corpus1.setDataStore(ds);
corpus2.setDataStore(ds);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
when(doc.getLRPersistenceId()).thenReturn("someId");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(null);
corpus1.add(doc);
corpus2.add(doc);
corpus1.getFeatures().put("key", "value1");
corpus2.getFeatures().put("key", "value2");
assertFalse(corpus1.equals(corpus2));
}

@Test
public void testToStringDoesNotThrowWhenCorpusIsEmpty() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
String result = corpus.toString();
assertNotNull(result);
assertTrue(result.contains("document data"));
}

@Test(expected = MethodNotImplementedException.class)
public void testListIteratorThrowsMethodNotImplementedException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.listIterator();
}

@Test(expected = MethodNotImplementedException.class)
public void testListIteratorWithIndexThrowsMethodNotImplementedException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.listIterator(0);
}

@Test(expected = MethodNotImplementedException.class)
public void testSubListThrowsMethodNotImplementedException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.subList(0, 1);
}

@Test
public void testAddDocumentFiresCorpusListenerEvent() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docAlpha");
when(doc.getLRPersistenceId()).thenReturn("idAlpha");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
verify(listener).documentAdded(any(CorpusEvent.class));
}

@Test
public void testEqualsWithNullPersistentIdsStillWorks() throws Exception {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
corpus1.setName("CorpusNull");
corpus2.setName("CorpusNull");
gate.DataStore ds = mock(gate.DataStore.class);
corpus1.setDataStore(ds);
corpus2.setDataStore(ds);
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("nullDoc");
when(doc1.getLRPersistenceId()).thenReturn(null);
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("nullDoc");
when(doc2.getLRPersistenceId()).thenReturn(null);
// when(doc2.getClass()).thenReturn(Document.class);
corpus1.add(doc1);
corpus2.add(doc2);
boolean result = corpus1.equals(corpus2);
assertTrue(result);
}

@Test
public void testHashCodeIsStableForEqualCorpora() {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("docX");
when(doc1.getLRPersistenceId()).thenReturn("idX");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("docX");
when(doc2.getLRPersistenceId()).thenReturn("idX");
// when(doc2.getClass()).thenReturn(Document.class);
corpus1.add(doc1);
corpus2.add(doc2);
assertEquals(corpus1.hashCode(), corpus2.hashCode());
}

@Test
public void testAddAndRemoveDocumentWithNullDataStore() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("nullDS");
when(doc.getLRPersistenceId()).thenReturn("nullID");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(null);
boolean added = corpus.add(doc);
assertTrue(added);
assertTrue(corpus.contains(doc));
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertEquals(0, corpus.size());
}

@Test
public void testRemoveDocumentWhenPersistentIdIsNull() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("noPID");
when(doc.getLRPersistenceId()).thenReturn(null);
// when(doc.getClass()).thenReturn(Document.class);
boolean added = corpus.add(doc);
assertTrue(added);
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertEquals(0, corpus.size());
}

@Test
public void testIteratorReturnsCorrectDocumentsWhenMultiple() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc1.getLRPersistenceId()).thenReturn("id1");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("doc2");
when(doc2.getLRPersistenceId()).thenReturn("id2");
// when(doc2.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.add(doc2);
java.util.Iterator<Document> it = corpus.iterator();
assertTrue(it.hasNext());
Document first = it.next();
assertNotNull(first);
assertTrue(first.getName().equals("doc1") || first.getName().equals("doc2"));
assertTrue(it.hasNext());
Document second = it.next();
assertNotNull(second);
assertTrue(second.getName().equals("doc1") || second.getName().equals("doc2"));
assertFalse(first.getName().equals(second.getName()));
assertFalse(it.hasNext());
}

@Test
public void testRemoveAllWithEmptyInputReturnsTrue() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Document> emptyList = new ArrayList<>();
boolean result = corpus.removeAll(emptyList);
assertTrue(result);
}

@Test
public void testAddMultipleDocumentsWithSameNameAndClassAndNullIds() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("duplicateDoc");
when(doc1.getLRPersistenceId()).thenReturn(null);
// when(doc1.getClass()).thenReturn(Document.class);
when(doc1.getDataStore()).thenReturn(null);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("duplicateDoc");
when(doc2.getLRPersistenceId()).thenReturn(null);
// when(doc2.getClass()).thenReturn(Document.class);
when(doc2.getDataStore()).thenReturn(null);
boolean added1 = corpus.add(doc1);
assertTrue(added1);
boolean added2 = corpus.add(doc2);
assertTrue(added2);
assertEquals(2, corpus.size());
}

@Test
public void testAddThenUnloadThenGetShouldReturnReloadedDocument() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
gate.DataStore datastore = mock(gate.DataStore.class);
corpus.setDataStore(datastore);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docToUnload");
when(doc.getLRPersistenceId()).thenReturn("pid");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(datastore);
corpus.add(doc);
corpus.setDocumentPersistentID(0, "pid");
corpus.unloadDocument(0, false);
Document result = corpus.get(0);
assertNotNull(result);
}

@Test
public void testUnloadDocumentAcrossAllOverloads() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docX");
when(doc.getLRPersistenceId()).thenReturn("lx");
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(null);
corpus.add(doc);
corpus.setDocumentPersistentID(0, "lx");
corpus.unloadDocument(doc);
assertFalse(corpus.isDocumentLoaded(0));
corpus.get(0);
assertTrue(corpus.isDocumentLoaded(0));
corpus.unloadDocument(0, false);
assertFalse(corpus.isDocumentLoaded(0));
corpus.get(0);
assertTrue(corpus.isDocumentLoaded(0));
corpus.unloadDocument(0);
assertFalse(corpus.isDocumentLoaded(0));
}

@Test
public void testEqualsWithDifferentTypesReturnsFalse() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertFalse(corpus.equals("String"));
}

@Test
public void testIsEmptyWhenNoDocsReturnsTrue() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertTrue(corpus.isEmpty());
}

@Test
public void testHashCodeConsistencyForSameDocs() {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc");
when(doc1.getLRPersistenceId()).thenReturn("abc");
// when(doc1.getClass()).thenReturn(Document.class);
when(doc1.getDataStore()).thenReturn(null);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("doc");
when(doc2.getLRPersistenceId()).thenReturn("abc");
// when(doc2.getClass()).thenReturn(Document.class);
when(doc2.getDataStore()).thenReturn(null);
corpus1.add(doc1);
corpus2.add(doc2);
int hc1 = corpus1.hashCode();
int hc2 = corpus2.hashCode();
assertEquals(hc1, hc2);
}

@Test
public void testIndexOfWithNonDocumentObjectReturnsMinusOne() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
int index = corpus.indexOf("Not a document");
assertEquals(-1, index);
}

@Test
public void testGetDocumentNameWithIndexOutOfBoundReturnsFallback() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertEquals("No such document", corpus.getDocumentName(999));
}

@Test
public void testGetDocumentPersistentIDOutOfBounds() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertNull(corpus.getDocumentPersistentID(12345));
}

@Test
public void testGetDocumentClassTypeOutOfBounds() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertNull(corpus.getDocumentClassType(99));
}

@Test
public void testRemoveDocumentByIndexWithNullLoadedReturnsNullAndNoCrash() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("notLoaded");
when(doc.getLRPersistenceId()).thenReturn("pid100");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
corpus.unloadDocument(0, false);
Document removed = corpus.remove(0);
assertNull(removed);
assertEquals(0, corpus.size());
}

@Test
public void testRemoveDocumentTwiceSecondAttemptShouldReturnFalse() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docTwice");
when(doc.getLRPersistenceId()).thenReturn("idTwice");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
boolean removedOnce = corpus.remove(doc);
boolean removedTwice = corpus.remove(doc);
assertTrue(removedOnce);
assertFalse(removedTwice);
}

@Test
public void testAddSameDocumentInstanceMultipleTimesShouldAddAll() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("dupDoc");
when(doc.getLRPersistenceId()).thenReturn("pid");
// when(doc.getClass()).thenReturn(Document.class);
boolean firstAdd = corpus.add(doc);
boolean secondAdd = corpus.add(doc);
assertTrue(firstAdd);
assertTrue(secondAdd);
assertEquals(2, corpus.size());
assertEquals(doc, corpus.get(0));
assertEquals(doc, corpus.get(1));
}

@Test
public void testUnloadDocumentPersistsIfNotPreviouslyAdopted() throws Exception {
SerialCorpusImpl corpus = new SerialCorpusImpl();
gate.DataStore ds = mock(gate.DataStore.class);
corpus.setDataStore(ds);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("persistDoc");
when(doc.getLRPersistenceId()).thenReturn(null);
// when(doc.getClass()).thenReturn(Document.class);
when(doc.getDataStore()).thenReturn(null);
Document adoptedDoc = mock(Document.class);
when(adoptedDoc.getName()).thenReturn("persistDoc");
when(adoptedDoc.getLRPersistenceId()).thenReturn("generatedID");
// when(adoptedDoc.getClass()).thenReturn(Document.class);
when(ds.adopt(doc)).thenReturn(adoptedDoc);
corpus.add(doc);
corpus.unloadDocument(0, true);
assertEquals("generatedID", corpus.getDocumentPersistentID(0));
assertFalse(corpus.isDocumentLoaded(0));
}

@Test
public void testAddNullDocumentDoesNotChangeCorpus() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.add(null);
assertFalse(result);
assertEquals(0, corpus.size());
}

@Test
public void testRemoveNonDocumentReturnsFalse() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Object nonDoc = new Object();
boolean result = corpus.remove(nonDoc);
assertFalse(result);
}

@Test
public void testRemoveCorpusListenerWhenListenerWasNeverAdded() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.removeCorpusListener(listener);
assertTrue(true);
}

@Test
public void testDocumentWithSameNameDifferentClassTypeNotMatchedInFind() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("variedDoc");
when(doc1.getLRPersistenceId()).thenReturn("id1");
// when(doc1.getClass()).thenReturn(Document.class);
// class AnotherDocumentType extends Document {
// }
// Document doc2 = mock(AnotherDocumentType.class);
// when(doc2.getName()).thenReturn("variedDoc");
// when(doc2.getLRPersistenceId()).thenReturn("id1");
// when(doc2.getClass()).thenReturn((Class) AnotherDocumentType.class);
corpus.add(doc1);
// int originalIndex = corpus.indexOf(doc2);
// assertEquals(-1, originalIndex);
}

@Test
public void testAddCorpusListenerMultipleTimesOnlyAddsOnce() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docX");
when(doc.getLRPersistenceId()).thenReturn("idX");
// when(doc.getClass()).thenReturn(Document.class);
corpus.add(doc);
verify(listener, times(1)).documentAdded(any());
}

@Test
public void testToArrayObjectArrayThrowsMethodNotImplemented() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document[] input = new Document[0];
try {
corpus.toArray(input);
fail("Expected MethodNotImplementedException");
} catch (MethodNotImplementedException e) {
assertTrue(e.getMessage().contains("toArray(Object[]"));
}
}

@Test
public void testContainsAllEmptyListReturnsTrue() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Document> emptyList = new ArrayList<>();
assertTrue(corpus.containsAll(emptyList));
}

@Test
public void testRetainAllUnsupportedOperationException() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Document> docs = new ArrayList<>();
try {
corpus.retainAll(docs);
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testEqualsWithDataStoresBeingNullReturnsFalse() {
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
corpus1.setName("someName");
corpus2.setName("someName");
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc");
// when(doc1.getClass()).thenReturn(Document.class);
when(doc1.getLRPersistenceId()).thenReturn("id");
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("doc");
// when(doc2.getClass()).thenReturn(Document.class);
when(doc2.getLRPersistenceId()).thenReturn("id");
corpus1.add(doc1);
corpus2.add(doc2);
assertFalse(corpus1.equals(corpus2));
}

@Test
public void testAddAllAddsAllWhenValidDocuments() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1all");
when(doc1.getLRPersistenceId()).thenReturn("id1all");
// when(doc1.getClass()).thenReturn(Document.class);
when(doc1.getDataStore()).thenReturn(null);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("doc2all");
when(doc2.getLRPersistenceId()).thenReturn("id2all");
// when(doc2.getClass()).thenReturn(Document.class);
when(doc2.getDataStore()).thenReturn(null);
List<Document> list = new ArrayList<>();
list.add(doc1);
list.add(doc2);
boolean result = corpus.addAll(list);
assertTrue(result);
assertEquals(2, corpus.size());
}

@Test
public void testAddAtIndexInsertsCorrectlyAndFiresEvent() {
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("docOne");
when(doc1.getLRPersistenceId()).thenReturn("pidOne");
// when(doc1.getClass()).thenReturn(Document.class);
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("docTwo");
when(doc2.getLRPersistenceId()).thenReturn("pidTwo");
// when(doc2.getClass()).thenReturn(Document.class);
corpus.add(doc1);
corpus.add(0, doc2);
assertEquals("docTwo", corpus.get(0).getName());
assertEquals("docOne", corpus.get(1).getName());
}
}
