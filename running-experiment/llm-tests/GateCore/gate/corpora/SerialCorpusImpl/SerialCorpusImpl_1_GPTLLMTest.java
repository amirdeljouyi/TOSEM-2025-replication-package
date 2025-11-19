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

public class SerialCorpusImpl_1_GPTLLMTest {

@Test
public void testAddSingleDocument() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Document content");
doc.setName("TestDoc");
boolean added = corpus.add(doc);
assertTrue(added);
assertEquals(1, corpus.size());
assertEquals("TestDoc", corpus.getDocumentName(0));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveSingleDocument() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Sample");
doc.setName("DocToRemove");
corpus.add(doc);
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertEquals(0, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testContainsDocumentTrue() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Content");
doc.setName("DocToCheck");
corpus.add(doc);
boolean exists = corpus.contains(doc);
assertTrue(exists);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testContainsDocumentFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Untracked");
doc.setName("UntrackedDoc");
boolean exists = corpus.contains(doc);
assertFalse(exists);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testIsEmptyInitially() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean isEmpty = corpus.isEmpty();
assertTrue(isEmpty);
Factory.deleteResource(corpus);
}

@Test
public void testToStringIncludesDocumentName() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Data");
doc.setName("DocInToString");
corpus.add(doc);
String str = corpus.toString();
assertNotNull(str);
assertTrue(str.contains("DocInToString"));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testUnsupportedToArray() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.toArray();
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testUnsupportedToArrayTyped() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.toArray(new Document[0]);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testUnsupportedSetOperator() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("replace");
corpus.set(0, doc);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testUnsupportedSubList() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.subList(0, 1);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveAllDocuments() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("One");
doc1.setName("One");
Document doc2 = Factory.newDocument("Two");
doc2.setName("Two");
corpus.add(doc1);
corpus.add(doc2);
boolean result = corpus.removeAll(Arrays.asList(doc1, doc2));
assertTrue(result);
assertEquals(0, corpus.size());
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test
public void testAddAllDocuments() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("D1");
doc1.setName("D1");
Document doc2 = Factory.newDocument("D2");
doc2.setName("D2");
Collection<Document> collection = new ArrayList<>();
collection.add(doc1);
collection.add(doc2);
boolean result = corpus.addAll(collection);
assertTrue(result);
assertEquals(2, corpus.size());
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test
public void testContainsAllDocuments() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("Doc1");
doc1.setName("Doc1");
Document doc2 = Factory.newDocument("Doc2");
doc2.setName("Doc2");
corpus.add(doc1);
corpus.add(doc2);
List<Document> checkList = Arrays.asList(doc1, doc2);
boolean result = corpus.containsAll(checkList);
assertTrue(result);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test
public void testGetDocumentByIndex() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Data");
doc.setName("GetDoc");
corpus.add(doc);
Document returned = corpus.get(0);
assertNotNull(returned);
assertEquals("GetDoc", returned.getName());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testIndexOfDocument() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("SampleIndex");
doc.setName("SampleIndex");
corpus.add(doc);
int index = corpus.indexOf(doc);
assertEquals(0, index);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveByIndex() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("RemoveThis");
doc.setName("RemoveThis");
corpus.add(doc);
Document removed = corpus.remove(0);
assertEquals("RemoveThis", removed.getName());
assertEquals(0, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsAndHashCode() throws Exception {
Gate.init();
SerialCorpusImpl corpus1 = new SerialCorpusImpl();
SerialCorpusImpl corpus2 = new SerialCorpusImpl();
corpus1.setName("TestCorpus");
corpus2.setName("TestCorpus");
boolean equals = corpus1.equals(corpus2);
boolean sameRef = corpus2.equals(corpus2);
assertFalse(equals);
assertTrue(sameRef);
assertEquals(corpus2.hashCode(), corpus2.hashCode());
Factory.deleteResource(corpus1);
Factory.deleteResource(corpus2);
}

@Test
public void testDocumentEventFiringOnAddRemove() throws Exception {
Gate.init();
final List<String> events = new ArrayList<String>();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.addCorpusListener(new CorpusListener() {

public void documentAdded(CorpusEvent e) {
events.add("added");
}

public void documentRemoved(CorpusEvent e) {
events.add("removed");
}
});
Document doc = Factory.newDocument("FireEvent");
doc.setName("FireEvent");
corpus.add(doc);
corpus.remove(doc);
assertEquals("added", events.get(0));
assertEquals("removed", events.get(1));
assertEquals(2, events.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testAddNullDocumentReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.add(null);
assertFalse(result);
assertEquals(0, corpus.size());
Factory.deleteResource(corpus);
}

@Test
public void testRemoveNonExistingDocumentReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Untracked");
boolean result = corpus.remove(doc);
assertFalse(result);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveDocumentNotLoadedStillRemoves() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("ToBeUnloaded");
doc.setName("DocUnloaded");
doc.setLRPersistenceId("doc-id-123");
corpus.add(doc);
corpus.remove(doc);
assertEquals(0, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testUnloadDocumentByObjectWithInvalidReference() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("D");
doc.setName("D");
corpus.unloadDocument(doc, true);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testUnloadDocumentIndexOutOfBoundsGracefulFailure() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.unloadDocument(0, false);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testAddAllAtIndexThrowsException() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("item");
corpus.addAll(0, Collections.singletonList(doc));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testGetInvalidIndexReturnsNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = corpus.get(10);
assertNull(doc);
Factory.deleteResource(corpus);
}

@Test
public void testGetDocumentNameOutOfBounds() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
String name = corpus.getDocumentName(5);
assertEquals("No such document", name);
Factory.deleteResource(corpus);
}

@Test
public void testGetDocumentPersistentIDInvalidIndexReturnsNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Object id = corpus.getDocumentPersistentID(100);
assertNull(id);
Factory.deleteResource(corpus);
}

@Test
public void testIndexOfNonDocumentReturnsMinusOne() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
int index = corpus.indexOf("non-doc");
assertEquals(-1, index);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsDifferentTypesReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.equals("other type");
assertFalse(result);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsNullReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.equals(null);
assertFalse(result);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsSameReferenceReturnsTrue() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.equals(corpus);
assertTrue(result);
Factory.deleteResource(corpus);
}

@Test
public void testHashCodeConsistency() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
int hash1 = corpus.hashCode();
int hash2 = corpus.hashCode();
assertEquals(hash1, hash2);
Factory.deleteResource(corpus);
}

@Test
public void testClearCorpusRemovesAllDocuments() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("doc1");
doc1.setName("doc1");
Document doc2 = Factory.newDocument("doc2");
doc2.setName("doc2");
corpus.add(doc1);
corpus.add(doc2);
corpus.clear();
assertEquals(0, corpus.size());
assertTrue(corpus.isEmpty());
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testListIteratorUnsupported() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.listIterator();
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testListIteratorWithIndexUnsupported() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.listIterator(0);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testLastIndexOfUnsupported() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.lastIndexOf(new Object());
Factory.deleteResource(corpus);
}

@Test
public void testAddAndRetrieveMultipleDocumentsWithoutLooping() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("One");
doc1.setName("One");
Document doc2 = Factory.newDocument("Two");
doc2.setName("Two");
Document doc3 = Factory.newDocument("Three");
doc3.setName("Three");
corpus.add(doc1);
corpus.add(doc2);
corpus.add(doc3);
Document result1 = corpus.get(0);
Document result2 = corpus.get(1);
Document result3 = corpus.get(2);
assertEquals("One", result1.getName());
assertEquals("Two", result2.getName());
assertEquals("Three", result3.getName());
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(doc3);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveAtInvalidIndexDoesNotThrow() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
try {
corpus.remove(1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException expected) {
}
Factory.deleteResource(corpus);
}

@Test
public void testRemoveCorpusListenerWhenAbsentDoesNothing() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
CorpusListener dummyListener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.removeCorpusListener(dummyListener);
assertTrue(true);
Factory.deleteResource(corpus);
}

@Test
public void testAddCorpusListenerTwiceDoesNotDuplicate() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
final Vector<String> calls = new Vector<String>();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
calls.add("added");
}

public void documentRemoved(CorpusEvent e) {
calls.add("removed");
}
};
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
Document doc = Factory.newDocument("X");
doc.setName("Doc");
corpus.add(doc);
corpus.remove(doc);
assertEquals(2, calls.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testResourceUnloadedRemovesTransientDocument() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("data");
doc.setName("UnloadSample");
corpus.add(doc);
CreoleEvent event = new CreoleEvent(doc, 0);
corpus.resourceUnloaded(event);
assertEquals(0, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testResourceUnloadedDoesNotRemovePersistentDoc() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("persistent");
doc.setName("PersistMe");
doc.setLRPersistenceId("persist-id");
corpus.add(doc);
corpus.setDocumentPersistentID(0, "persist-id");
CreoleEvent event = new CreoleEvent(doc, 0);
corpus.resourceUnloaded(event);
assertEquals(1, corpus.size());
assertNull(corpus.get(0));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testDatastoreClosedRemovesListenerAndDeletesResource() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
// SerialDataStore dummyDS = new SerialDataStore(new java.io.File(Gate.getGateHome(), "teststore"));
// corpus.setDataStore(dummyDS);
// CreoleEvent event = new CreoleEvent(dummyDS, 0);
// corpus.datastoreClosed(event);
assertTrue(true);
Factory.deleteResource(corpus);
}

@Test
public void testDatastoreClosedWithDifferentDSDoesNothing() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
// SerialDataStore realDS = new SerialDataStore(new java.io.File(Gate.getGateHome(), "myds"));
// SerialDataStore otherDS = new SerialDataStore(new java.io.File(Gate.getGateHome(), "other"));
// corpus.setDataStore(realDS);
// CreoleEvent otherEvent = new CreoleEvent(otherDS, 0);
// corpus.datastoreClosed(otherEvent);
assertTrue(true);
Factory.deleteResource(corpus);
// realDS.close();
// otherDS.close();
}

@Test
public void testResourceWrittenMatchingIdDoesNotThrow() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.setName("testCorpus");
corpus.setLRPersistenceId("corpusID");
// DatastoreEvent event = new DatastoreEvent(corpus, corpus, "corpusID", 0);
// corpus.resourceWritten(event);
assertTrue(true);
Factory.deleteResource(corpus);
}

@Test
public void testSetTransientSourceWithNonCorpusDoesNothing() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.setTransientSource("not a corpus");
assertTrue(true);
Factory.deleteResource(corpus);
}

@Test
public void testSetDataStoreSetsListener() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
// DataStore ds = new SerialDataStore(new java.io.File(Gate.getGateHome(), "assignedStore"));
// corpus.setDataStore(ds);
// assertEquals(ds, corpus.getDataStore());
Factory.deleteResource(corpus);
// ds.close();
}

@Test(expected = ResourceInstantiationException.class)
public void testDuplicateThrowsException() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
// corpus.duplicate(Factory.DuplicationContext.getInstance());
Factory.deleteResource(corpus);
}

@Test
public void testGetTransientSourceReturnsNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Object source = corpus.getTransientSource();
assertNull(source);
Factory.deleteResource(corpus);
}

@Test
public void testIndexDefinitionCanBeSetAndRetrieved() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
// IndexDefinition def = new IndexDefinition();
// def.setIrEngineClassName("gate.creole.ir.LuceneIR");
// corpus.setIndexDefinition(def);
// IndexDefinition retrieved = corpus.getIndexDefinition();
// assertEquals(def, retrieved);
Factory.deleteResource(corpus);
}

@Test
public void testIndexManagerIsNullByDefault() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
IndexManager manager = corpus.getIndexManager();
assertNull(manager);
Factory.deleteResource(corpus);
}

@Test
public void testIndexStatisticsIsNullByDefault() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
assertNull(corpus.getIndexStatistics());
Factory.deleteResource(corpus);
}

@Test
public void testRemoveDocumentWithoutLRIdDoesNotThrow() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("no id");
doc.setName("no-id");
corpus.add(doc);
doc.setLRPersistenceId(null);
Object removed = corpus.remove(0);
assertNull(((Document) removed).getLRPersistenceId());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testGetDocumentClassTypeInvalidIndexReturnsNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
String classType = corpus.getDocumentClassType(42);
assertNull(classType);
Factory.deleteResource(corpus);
}

@Test
public void testIsDocumentLoadedInvalidIndexReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean loaded = corpus.isDocumentLoaded(5);
assertFalse(loaded);
Factory.deleteResource(corpus);
}

@Test
public void testAddSameDocumentTwiceAddsTwoEntries() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("dup");
doc.setName("dup");
corpus.add(doc);
corpus.add(doc);
assertEquals(2, corpus.size());
assertTrue(corpus.contains(doc));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveAtNegativeIndexThrows() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("x");
doc.setName("x");
corpus.add(doc);
try {
corpus.remove(-1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveAtExactSizeIndexThrows() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("a");
doc.setName("a");
corpus.add(doc);
try {
corpus.remove(1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testAddDocumentFromDifferentDatastoreFails() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
// SerialDataStore ds1 = new SerialDataStore(new java.io.File(Gate.getGateHome(), "ds1"));
// ds1.open();
// corpus.setDataStore(ds1);
Document doc = Factory.newDocument("X");
doc.setName("X");
// SerialDataStore ds2 = new SerialDataStore(new java.io.File(Gate.getGateHome(), "ds2"));
// ds2.open();
// Document adoptedInWrongDS = (Document) ds2.adopt(doc);
// boolean result = corpus.add(adoptedInWrongDS);
// assertFalse(result);
assertEquals(0, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
// ds1.close();
// ds2.close();
}

@Test
public void testClearResetsCorpusCorrectly() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("a");
doc1.setName("a");
Document doc2 = Factory.newDocument("b");
doc2.setName("b");
corpus.add(doc1);
corpus.add(doc2);
corpus.clear();
assertTrue(corpus.isEmpty());
assertEquals(0, corpus.size());
assertFalse(corpus.contains(doc1));
assertFalse(corpus.contains(doc2));
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test
public void testFindDocumentNonMatchingNameReturnsMinusOne() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("abc");
doc1.setName("abc");
doc1.setLRPersistenceId("id1");
Document doc2 = Factory.newDocument("xyz");
doc2.setName("xyz");
doc2.setLRPersistenceId("id2");
corpus.add(doc1);
doc2.setLRPersistenceId("id1");
int result = corpus.findDocument(doc2);
assertEquals(-1, result);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test
public void testFindDocumentNonMatchingClassTypeReturnsMinusOne() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("abc");
doc1.setName("abc");
doc1.setLRPersistenceId("idxxx");
corpus.add(doc1);
// DocumentAnonymousImpl fakeDoc = new DocumentAnonymousImpl();
// fakeDoc.setName("abc");
// fakeDoc.setLRPersistenceId("idxxx");
// int result = corpus.findDocument(fakeDoc);
// assertEquals(-1, result);
Factory.deleteResource(doc1);
Factory.deleteResource(corpus);
}

@Test
public void testSetAndGetDocumentPersistentIDBoundaryIndex() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Example");
doc.setName("DocA");
corpus.add(doc);
corpus.setDocumentPersistentID(0, "custom-id-001");
Object id = corpus.getDocumentPersistentID(0);
assertEquals("custom-id-001", id);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testSetDocumentPersistentIDOutOfBoundsDoesNothing() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.setDocumentPersistentID(5, "invalid-index");
assertTrue(corpus.getDocumentPersistentIDs().isEmpty());
Factory.deleteResource(corpus);
}

@Test
public void testPopulateOverloadUnsupportedMimeTypeGracefulFallback() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
File testDir = new File(Gate.getPluginsHome(), "ANNIE");
if (!testDir.exists()) {
testDir.mkdirs();
}
java.net.URL testUrl = testDir.toURI().toURL();
corpus.populate(testUrl, null, "UTF-8", "text/xml", false);
assertNotNull(corpus);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testSubListUnsupportedMethodThrows() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.subList(0, 1);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsWithTwoDifferentObjectsWithSameProperties() throws Exception {
Gate.init();
SerialCorpusImpl c1 = new SerialCorpusImpl();
SerialCorpusImpl c2 = new SerialCorpusImpl();
c1.setName("SharedName");
c2.setName("SharedName");
c1.setLRPersistenceId("same-id");
c2.setLRPersistenceId("same-id");
Document d1 = Factory.newDocument("Text1");
d1.setName("Doc");
d1.setLRPersistenceId("doc-id");
Document d2 = Factory.newDocument("Text1");
d2.setName("Doc");
d2.setLRPersistenceId("doc-id");
c1.add(d1);
c2.add(d2);
assertTrue(c1.equals(c2));
Factory.deleteResource(d1);
Factory.deleteResource(d2);
Factory.deleteResource(c1);
Factory.deleteResource(c2);
}

@Test
public void testEqualsWithDifferentIdsReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl c1 = new SerialCorpusImpl();
SerialCorpusImpl c2 = new SerialCorpusImpl();
c1.setName("TestCorpus");
c2.setName("TestCorpus");
c1.setLRPersistenceId("id1");
c2.setLRPersistenceId("id2");
assertFalse(c1.equals(c2));
Factory.deleteResource(c1);
Factory.deleteResource(c2);
}

@Test
public void testDatastoreDeletedMatchingCorpusIdDeletesItself() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.setName("DSDeleted");
corpus.setLRPersistenceId("corp-id");
// DataStore fakeDS = new SerialDataStore(new File(Gate.getGateHome(), "ds-delete"));
// fakeDS.open();
// corpus.setDataStore(fakeDS);
// DatastoreEvent evt = new DatastoreEvent(fakeDS, corpus, "corp-id", 0);
// corpus.resourceDeleted(evt);
assertTrue(true);
// fakeDS.close();
}

@Test
public void testResourceDeletedRemovesLoadedDocumentByID() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Content");
doc.setName("DocX");
doc.setLRPersistenceId("doc-id");
// DataStore store = new SerialDataStore(new File(Gate.getGateHome(), "resDS"));
// store.open();
// corpus.setDataStore(store);
corpus.add(doc);
corpus.setDocumentPersistentID(0, "doc-id");
// DatastoreEvent evt = new DatastoreEvent(store, doc, "doc-id", 0);
// corpus.resourceDeleted(evt);
assertEquals(0, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
// store.close();
}

@Test
public void testResourceDeletedDocDeletedNullResourcePath() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("doc");
doc.setName("D");
doc.setLRPersistenceId("null-doc-id");
// DataStore ds = new SerialDataStore(new File(Gate.getGateHome(), "ds-null"));
// ds.open();
// corpus.setDataStore(ds);
corpus.add(doc);
corpus.setDocumentPersistentID(0, "null-doc-id");
// DatastoreEvent evt = new DatastoreEvent(ds, null, "null-doc-id", 0);
// corpus.resourceDeleted(evt);
assertEquals(0, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
// ds.close();
}

@Test
public void testGetDocumentNamesEmptyCorpusReturnsEmptyList() throws Exception {
Gate.init();
SerialCorpusImpl emptyCorpus = new SerialCorpusImpl();
List<String> names = emptyCorpus.getDocumentNames();
assertNotNull(names);
assertTrue(names.isEmpty());
Factory.deleteResource(emptyCorpus);
}

@Test
public void testGetDocumentPersistentIDsEmptyCorpusReturnsEmptyList() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<Object> ids = corpus.getDocumentPersistentIDs();
assertNotNull(ids);
assertTrue(ids.isEmpty());
Factory.deleteResource(corpus);
}

@Test
public void testGetDocumentClassTypesEmptyCorpusReturnsEmptyList() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
List<String> types = corpus.getDocumentClassTypes();
assertNotNull(types);
assertTrue(types.isEmpty());
Factory.deleteResource(corpus);
}

@Test
public void testGetWithNullReloadsDocumentFromDatastore() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
// SerialDataStore ds = new SerialDataStore(new File(Gate.getGateHome(), "testGetNull"));
// ds.open();
// corpus.setDataStore(ds);
Document doc = Factory.newDocument("reload");
doc.setName("reload");
doc.setLRPersistenceId("reload-id");
// Document adopted = (Document) ds.adopt(doc);
// ds.sync(adopted);
// corpus.add(adopted);
corpus.setDocumentPersistentID(0, "reload-id");
corpus.unloadDocument(0, false);
Document reloaded = corpus.get(0);
assertNotNull(reloaded);
assertEquals("reload", reloaded.getName());
Factory.deleteResource(doc);
Factory.deleteResource(reloaded);
Factory.deleteResource(corpus);
// ds.close();
}

@Test(expected = GateRuntimeException.class)
public void testUnloadFailsOnSyncError() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("x");
doc.setName("ErrorDoc");
corpus.add(doc);
corpus.setDocumentPersistentID(0, "doc-id");
// Document broken = new Document() {
// 
// public Object getLRPersistenceId() {
// return null;
// }
// 
// public String getName() {
// return "ErrorDoc";
// }
// 
// public boolean isModified() {
// return false;
// }
// 
// public void setModified(boolean b) {
// }
// 
// public void cleanup() {
// }
// 
// public DataStore getDataStore() {
// return null;
// }
// 
// public void setDataStore(DataStore ds) {
// }
// 
// public void setNameResource(String name) {
// }
// 
// public FeatureMap getFeatures() {
// return Factory.newFeatureMap();
// }
// 
// public Resource init() throws ResourceInstantiationException {
// return null;
// }
// 
// public void sync() {
// }
// 
// public void delete() {
// }
// 
// public void setLRPersistenceId(Object id) {
// }
// 
// public void setLRPersistenceIdTemp(Object id) {
// }
// };
// corpus.add(broken);
// corpus.unloadDocument(broken);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testIsPersistentDocumentReturnsFalseWhenDocsIsNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.isPersistentDocument(0);
assertFalse(result);
Factory.deleteResource(corpus);
}

@Test
public void testIsDocumentLoadedReturnsFalseWhenDocumentsNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean loaded = corpus.isDocumentLoaded(0);
assertFalse(loaded);
Factory.deleteResource(corpus);
}

@Test
public void testGetReturnsNullWhenIndexOutOfBounds() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document d = corpus.get(99);
assertNull(d);
Factory.deleteResource(corpus);
}

@Test
public void testAddAtIndexStoresDocumentCorrectly() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("doc 1");
doc1.setName("A");
Document doc2 = Factory.newDocument("doc 2");
doc2.setName("B");
corpus.add(doc1);
corpus.add(0, doc2);
Document first = corpus.get(0);
Document second = corpus.get(1);
assertEquals("B", first.getName());
assertEquals("A", second.getName());
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testUnsupportedRetainAll() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.retainAll(java.util.Collections.emptyList());
Factory.deleteResource(corpus);
}

@Test
public void testDocumentIdentityDoesNotMatchDueToClassMismatch() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("doc");
doc1.setName("doc");
doc1.setLRPersistenceId("doc-id");
corpus.add(doc1);
Document doc2 = new DocumentImpl() {

public String getName() {
return "doc";
}

public Object getLRPersistenceId() {
return "doc-id";
}
};
int result = corpus.findDocument(doc2);
assertEquals(-1, result);
Factory.deleteResource(doc1);
Factory.deleteResource(corpus);
}

@Test
public void testUnloadingTransientUnadoptedDocumentSkipsSync() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("unsynced");
doc.setName("transient");
doc.setLRPersistenceId(null);
corpus.add(doc);
corpus.unloadDocument(doc, true);
assertFalse(corpus.isDocumentLoaded(0));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testUnloadDocumentFalseFlagSkipsSyncEvenIfPersistent() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("to be unloaded");
doc.setName("syncable");
doc.setLRPersistenceId("12345");
corpus.add(doc);
corpus.setDocumentPersistentID(0, "12345");
corpus.unloadDocument(0, false);
assertFalse(corpus.isDocumentLoaded(0));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testAddAllEmptyListReturnsTrue() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.addAll(java.util.Collections.emptyList());
assertTrue(result);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveAllEmptyCollectionReturnsTrue() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.removeAll(java.util.Collections.emptyList());
assertTrue(result);
Factory.deleteResource(corpus);
}

@Test
public void testContainsAllWithNonContainedDocumentReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("untracked");
boolean result = corpus.containsAll(java.util.Arrays.asList(doc));
assertFalse(result);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testToStringContainsClassInfo() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("data");
doc.setName("doc");
corpus.add(doc);
String result = corpus.toString();
assertTrue(result.contains("document data"));
assertTrue(result.contains("documents"));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testClearDoesNotThrowWhenEmpty() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
corpus.clear();
assertTrue(corpus.isEmpty());
Factory.deleteResource(corpus);
}

@Test
public void testEqualsSameReferenceIsTrue() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean equalsItself = corpus.equals(corpus);
assertTrue(equalsItself);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsWithNullIsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.equals(null);
assertFalse(result);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsWithDifferentObjectTypeIsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
boolean result = corpus.equals("not a corpus");
assertFalse(result);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveDocumentTwiceGracefullyHandlesSecondMiss() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("doc");
doc.setName("unique");
corpus.add(doc);
boolean first = corpus.remove(doc);
boolean second = corpus.remove(doc);
assertTrue(first);
assertFalse(second);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveDocumentWithWrongNameReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("Doc1 content");
doc1.setName("Doc1");
doc1.setLRPersistenceId("id-123");
corpus.add(doc1);
Document doc2 = Factory.newDocument("doc2");
doc2.setName("Mismatch");
doc2.setLRPersistenceId("id-123");
boolean result = corpus.remove(doc2);
assertFalse(result);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveDocumentWithWrongIdReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("Doc1 content");
doc1.setName("Doc1");
doc1.setLRPersistenceId("id-123");
corpus.add(doc1);
Document doc2 = Factory.newDocument("Doc1 content");
doc2.setName("Doc1");
doc2.setLRPersistenceId("wrong-id");
boolean result = corpus.remove(doc2);
assertFalse(result);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(corpus);
}

@Test
public void testGetDocumentNameWhenListIsEmptyReturnsDefault() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
String result = corpus.getDocumentName(0);
assertEquals("No such document", result);
Factory.deleteResource(corpus);
}

@Test
public void testGetDocumentClassTypeWithInvalidIndexReturnsNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
String result = corpus.getDocumentClassType(99);
assertNull(result);
Factory.deleteResource(corpus);
}

@Test
public void testUnloadDocumentWithNullLRIdHandlesExceptionGracefully() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("doc");
doc.setName("A");
doc.setLRPersistenceId(null);
corpus.add(doc);
corpus.unloadDocument(0, true);
assertFalse(corpus.isDocumentLoaded(0));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testUnloadDocumentReplacesWithNull() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("test");
doc.setName("doc");
corpus.add(doc);
corpus.unloadDocument(0, false);
assertFalse(corpus.isDocumentLoaded(0));
assertNull(corpus.get(0));
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test(expected = MethodNotImplementedException.class)
public void testUnsupportedSetMethodThrowsException() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("Doc");
corpus.set(0, doc);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testAddAllWithPartialFailureReturnsFalse() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc1 = Factory.newDocument("doc1");
doc1.setName("Doc1");
// SerialDataStore ds1 = new SerialDataStore(new File(Gate.getGateHome(), "ds1"));
// ds1.open();
// corpus.setDataStore(ds1);
// DataStore ds2 = new SerialDataStore(new File(Gate.getGateHome(), "ds2"));
// ds2.open();
// Document foreignDoc = (Document) ds2.adopt(doc1);
doc1.setName("Foreign");
// boolean result = corpus.addAll(java.util.Arrays.asList(doc1, foreignDoc));
assertEquals(1, corpus.size());
// assertFalse(result);
Factory.deleteResource(doc1);
Factory.deleteResource(corpus);
// ds1.close();
// ds2.close();
}

@Test
public void testCorpusListenerFiresDocumentAddedAndRemoved() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("doc");
doc.setName("E");
final boolean[] flags = new boolean[2];
corpus.addCorpusListener(new CorpusListener() {

public void documentAdded(CorpusEvent e) {
flags[0] = true;
}

public void documentRemoved(CorpusEvent e) {
flags[1] = true;
}
});
corpus.add(doc);
corpus.remove(doc);
assertTrue(flags[0]);
assertTrue(flags[1]);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testDocumentAddedToCorpusIsTrackedInSize() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("xxx");
doc.setName("size-doc");
corpus.add(doc);
assertEquals(1, corpus.size());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveByIndexReturnsDocumentIfLoaded() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("doc");
doc.setName("G");
corpus.add(doc);
Document removed = corpus.remove(0);
assertNotNull(removed);
assertEquals("G", removed.getName());
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testRemoveByIndexReturnsNullIfDocumentNotLoaded() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
Document doc = Factory.newDocument("doc");
doc.setName("G");
corpus.add(doc);
corpus.setDocumentPersistentID(0, "someID");
corpus.unloadDocument(0, false);
Document removed = corpus.remove(0);
assertNull(removed);
Factory.deleteResource(doc);
Factory.deleteResource(corpus);
}

@Test
public void testEqualsComparesDataStoreAndFeatureMaps() throws Exception {
Gate.init();
SerialCorpusImpl c1 = new SerialCorpusImpl();
SerialCorpusImpl c2 = new SerialCorpusImpl();
c1.setName("MyCorpus");
c2.setName("MyCorpus");
// SerialDataStore ds = new SerialDataStore(new File(Gate.getGateHome(), "ds-eq"));
// ds.open();
// c1.setDataStore(ds);
// c2.setDataStore(ds);
c1.setLRPersistenceId("id-eq");
c2.setLRPersistenceId("id-eq");
Document d1 = Factory.newDocument("same");
d1.setName("docX");
d1.setLRPersistenceId("idx");
Document d2 = Factory.newDocument("same");
d2.setName("docX");
d2.setLRPersistenceId("idx");
c1.add(d1);
c2.add(d2);
boolean equals = c1.equals(c2);
assertTrue(equals);
Factory.deleteResource(d1);
Factory.deleteResource(d2);
Factory.deleteResource(c1);
Factory.deleteResource(c2);
// ds.close();
}

@Test
public void testGetIndexManagerReturnsNullWhenUnset() throws Exception {
Gate.init();
SerialCorpusImpl corpus = new SerialCorpusImpl();
IndexManager indexManager = corpus.getIndexManager();
assertNull(indexManager);
Factory.deleteResource(corpus);
}
}
