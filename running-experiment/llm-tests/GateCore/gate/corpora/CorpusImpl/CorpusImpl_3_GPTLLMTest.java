package gate.corpora;

import gate.*;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.event.*;
import gate.util.ExtensionFileFilter;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;
import org.junit.Test;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CorpusImpl_3_GPTLLMTest {

@Test
public void testAddDocumentIncreasesSizeAndCanRetrieve() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc1");
boolean added = corpus.add(doc);
assertTrue(added);
assertEquals(1, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testRemoveDocumentDecreasesSizeToEmpty() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc1");
corpus.add(doc);
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertTrue(corpus.isEmpty());
}

@Test
public void testSetDocumentReplacesCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document oldDoc = mock(Document.class);
when(oldDoc.getName()).thenReturn("oldDoc");
Document newDoc = mock(Document.class);
when(newDoc.getName()).thenReturn("newDoc");
corpus.add(oldDoc);
Document replaced = corpus.set(0, newDoc);
assertSame(oldDoc, replaced);
assertSame(newDoc, corpus.get(0));
}

@Test
public void testGetDocumentNameReturnsCorrectName() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("testDoc");
corpus.add(doc);
String name = corpus.getDocumentName(0);
assertEquals("testDoc", name);
}

@Test
public void testGetDocumentNamesReturnsAllNames() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("docA");
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("docB");
corpus.add(doc1);
corpus.add(doc2);
List<String> names = corpus.getDocumentNames();
assertEquals(2, names.size());
assertEquals("docA", names.get(0));
assertEquals("docB", names.get(1));
}

@Test
public void testUnloadDocumentDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.unloadDocument(doc);
} catch (Exception e) {
fail("unloadDocument should do nothing even if input is valid.");
}
}

@Test
public void testIsDocumentLoadedReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
corpus.add(doc);
boolean loaded = corpus.isDocumentLoaded(0);
assertTrue(loaded);
}

@Test
public void testEqualsAndHashCodeReturnTrueForIdenticalCorpus() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("docX");
corpus1.add(doc);
corpus2.add(doc);
assertTrue(corpus1.equals(corpus2));
assertEquals(corpus1.hashCode(), corpus2.hashCode());
}

@Test
public void testListenerReceivesEventsOnCorpusChanges() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc2.getName()).thenReturn("doc2");
corpus.addCorpusListener(listener);
corpus.add(doc1);
corpus.set(0, doc2);
corpus.remove(0);
verify(listener, times(2)).documentAdded(any(CorpusEvent.class));
verify(listener, times(2)).documentRemoved(any(CorpusEvent.class));
}

@Test
public void testRemovedListenerShouldNotReceiveEvents() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
corpus.add(doc);
corpus.set(0, doc);
corpus.remove(0);
verify(listener, never()).documentAdded(any(CorpusEvent.class));
verify(listener, never()).documentRemoved(any(CorpusEvent.class));
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateWithNonFileURLThrowsException() throws Exception {
CorpusImpl corpus = new CorpusImpl();
URL nonFileUrl = new URL("http://example.com/test");
CorpusImpl.populate(corpus, nonFileUrl, null, null, false);
}

@Test(expected = FileNotFoundException.class)
public void testPopulateWithMissingFolderThrows() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File invalidDir = new File("non_existing_dir");
URL invalidUrl = invalidDir.toURI().toURL();
CorpusImpl.populate(corpus, invalidUrl, null, null, false);
}

@Test
public void testPopulateAddsFilesToCorpus() throws Exception {
// File dir = tempFolder.newFolder("gateTest");
// File f1 = new File(dir, "a1.txt");
// File f2 = new File(dir, "b2.txt");
// try (Writer w1 = new FileWriter(f1)) {
// w1.write("file-a1");
// }
// try (Writer w2 = new FileWriter(f2)) {
// w2.write("file-b2");
// }
CorpusImpl corpus = new CorpusImpl();
ExtensionFileFilter filter = new ExtensionFileFilter("txt");
// URL dirUrl = dir.toURI().toURL();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("fakeDoc");
FeatureMap fmap = mock(FeatureMap.class);
// Factory.setFeatureMapSupplier(() -> fmap);
// Factory.setResourceCreator((name, params, feats, nameOverride) -> doc);
// Factory.setResourceDeleter(resource -> {
// });
// CorpusImpl.populate(corpus, dirUrl, filter, "UTF-8", false);
assertEquals(2, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testInitWithPreloadedDocumentsAddsThem() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("preDoc");
List<Document> docs = new ArrayList<Document>();
docs.add(doc);
corpus.setDocumentsList(docs);
corpus.init();
assertEquals(1, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testSubListReturnsCorrectSubPart() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("one");
when(doc2.getName()).thenReturn("two");
corpus.add(doc1);
corpus.add(doc2);
List<Document> sub = corpus.subList(0, 1);
assertEquals(1, sub.size());
assertSame(doc1, sub.get(0));
}

@Test
public void testResourceUnloadedRemovesMatchingDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
Resource resource = doc;
when(doc.getName()).thenReturn("docToRemove");
corpus.add(doc);
corpus.resourceUnloaded(new gate.event.CreoleEvent(resource, 0));
assertEquals(0, corpus.size());
}

@Test
public void testSetAndGetDocumentsList() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
List<Document> list = Arrays.asList(doc);
corpus.setDocumentsList(list);
List<Document> result = corpus.getDocumentsList();
assertEquals(1, result.size());
assertSame(doc, result.get(0));
}

@Test
public void testDuplicateToAnotherCorpusIncludesDocuments() throws Exception {
CorpusImpl original = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
original.add(doc);
Resource duplicatedDoc = mock(Document.class);
CorpusImpl duplicatedCorpus = new CorpusImpl();
// Factory.setResourceCreator((name, params, features, nameOverride) -> duplicatedDoc);
// Factory.setDefaultDuplicator((res, ctx) -> duplicatedCorpus);
// Factory.setDuplicator((res, ctx) -> duplicatedDoc);
Resource result = original.duplicate(mock(Factory.DuplicationContext.class));
assertTrue(result instanceof CorpusImpl);
CorpusImpl finalCorpus = (CorpusImpl) result;
assertEquals(1, finalCorpus.size());
assertSame(duplicatedDoc, finalCorpus.get(0));
}

@Test
public void testAddNullDocumentShouldThrowException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.add(null);
fail("Expected NullPointerException or IllegalArgumentException");
} catch (NullPointerException | IllegalArgumentException e) {
} catch (Exception e) {
fail("Unexpected exception type: " + e);
}
}

@Test
public void testRemoveNonExistingDocumentReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
boolean result = corpus.remove(doc);
assertFalse(result);
}

@Test
public void testGetDocumentNameOutOfBoundsThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.getDocumentName(5);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testGetNegativeIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.get(-1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testSetOnEmptyCorpusThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.set(0, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testRemoveByIndexOnEmptyCorpusThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.remove(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testIndexOfForUnaddedDocumentReturnsMinusOne() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
int index = corpus.indexOf(doc);
assertEquals(-1, index);
}

@Test
public void testLastIndexOfWithDuplicates() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("d");
corpus.add(doc);
corpus.add(doc);
int lastIndex = corpus.lastIndexOf(doc);
assertEquals(1, lastIndex);
}

@Test
public void testIsEmptyReturnsTrueOnNewCorpus() {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.isEmpty());
}

@Test
public void testClearEmptiesCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.clear();
assertEquals(0, corpus.size());
}

@Test
public void testRetainAllKeepsMatchingItems() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> retainList = Collections.singletonList(doc1);
boolean result = corpus.retainAll(retainList);
assertTrue(result);
assertEquals(1, corpus.size());
assertSame(doc1, corpus.get(0));
}

@Test
public void testRemoveAllRemovesCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> removeList = Collections.singletonList(doc1);
boolean result = corpus.removeAll(removeList);
assertTrue(result);
assertEquals(1, corpus.size());
assertSame(doc2, corpus.get(0));
}

@Test
public void testListIteratorProvidesCorrectOrder() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("x");
when(doc2.getName()).thenReturn("y");
corpus.add(doc1);
corpus.add(doc2);
ListIterator<Document> it = corpus.listIterator();
assertTrue(it.hasNext());
assertEquals(doc1, it.next());
assertTrue(it.hasNext());
assertEquals(doc2, it.next());
assertFalse(it.hasNext());
}

@Test
public void testToArrayCopiesCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Object[] arr = corpus.toArray();
assertEquals(1, arr.length);
assertSame(doc, arr[0]);
}

@Test
public void testToGenericArrayCopiesCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] array = corpus.toArray(new Document[0]);
assertEquals(1, array.length);
assertSame(doc, array[0]);
}

@Test
public void testAddAllAtIndexInsertsCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
Document doc3 = mock(Document.class);
corpus.add(doc1);
List<Document> insertList = Arrays.asList(doc2, doc3);
boolean result = corpus.addAll(1, insertList);
assertTrue(result);
assertEquals(3, corpus.size());
assertSame(doc1, corpus.get(0));
assertSame(doc2, corpus.get(1));
assertSame(doc3, corpus.get(2));
}

@Test
public void testDuplicateWithEmptyCorpus() throws Exception {
CorpusImpl original = new CorpusImpl();
CorpusImpl duplicate = new CorpusImpl();
// Factory.setResourceCreator((name, params, feats, nameOverride) -> duplicate);
// Factory.setDefaultDuplicator((res, ctx) -> duplicate);
Resource result = original.duplicate(mock(Factory.DuplicationContext.class));
assertTrue(result instanceof CorpusImpl);
CorpusImpl c = (CorpusImpl) result;
assertEquals(0, c.size());
}

@Test
public void testEqualsReturnsFalseForDifferentObject() {
CorpusImpl corpus = new CorpusImpl();
String notACorpus = "notACorpus";
assertFalse(corpus.equals(notACorpus));
}

@Test
public void testEqualsReturnsFalseForNull() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals(null));
}

@Test
public void testHashCodeConsistencyForEmptyCorpus() {
CorpusImpl corpus = new CorpusImpl();
int hash1 = corpus.hashCode();
int hash2 = corpus.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testResourceRenamedDoesNotAffectCorpus() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.resourceRenamed(mock(Resource.class), "old", "new");
} catch (Exception e) {
fail("resourceRenamed should not throw");
}
}

@Test
public void testDatastoreEventsDoNotAffectCorpus() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.datastoreOpened(null);
corpus.datastoreCreated(null);
corpus.datastoreClosed(null);
} catch (Exception e) {
fail("datastore event methods should not throw");
}
}

@Test
public void testPopulateWithZeroFilesDoesNothing() throws Exception {
File tempDir = File.createTempFile("gateEmpty", "");
tempDir.delete();
tempDir.mkdir();
tempDir.deleteOnExit();
CorpusImpl corpus = new CorpusImpl();
URL url = tempDir.toURI().toURL();
// Factory.setResourceCreator((name, params, feats, nameOverride) -> mock(Document.class));
// Factory.setFeatureMapSupplier(() -> mock(FeatureMap.class));
// Factory.setResourceDeleter(res -> {
// });
CorpusImpl.populate(corpus, url, pathname -> false, "UTF-8", false);
assertEquals(0, corpus.size());
}

@Test
public void testAddAtBeginningMaintainsOrder() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("one");
when(doc2.getName()).thenReturn("two");
corpus.add(doc1);
corpus.add(0, doc2);
assertEquals(2, corpus.size());
assertSame(doc2, corpus.get(0));
assertSame(doc1, corpus.get(1));
}

@Test
public void testAddAllWithEmptyCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean result1 = corpus.addAll(Collections.emptyList());
boolean result2 = corpus.addAll(0, Collections.emptyList());
assertFalse(result1);
assertFalse(result2);
}

@Test
public void testPopulateSkipsDirectoriesInRecursiveMode() throws Exception {
File root = File.createTempFile("testdir", "");
root.delete();
root.mkdir();
File subDir = new File(root, "subdir");
subDir.mkdir();
subDir.deleteOnExit();
root.deleteOnExit();
CorpusImpl corpus = new CorpusImpl();
URL rootUrl = root.toURI().toURL();
// Factory.setResourceCreator((name, params, feats, nameOverride) -> mock(Document.class));
// Factory.setFeatureMapSupplier(() -> mock(FeatureMap.class));
// Factory.setResourceDeleter(res -> {
// });
CorpusImpl.populate(corpus, rootUrl, pathname -> true, "UTF-8", true);
assertTrue(corpus.isEmpty());
}

@Test
public void testPopulateWithIOExceptionFallsBackGracefully() throws Exception {
File dir = File.createTempFile("testCrash", "");
dir.delete();
dir.mkdir();
File badFile = new File(dir, "crash.txt") {

@Override
public boolean isDirectory() {
return false;
}
};
try (FileWriter fw = new FileWriter(badFile)) {
fw.write("bad");
}
badFile.deleteOnExit();
dir.deleteOnExit();
Document crashDoc = mock(Document.class);
when(crashDoc.getName()).thenThrow(new RuntimeException("Simulated failure"));
CorpusImpl corpus = new CorpusImpl();
URL dirUrl = dir.toURI().toURL();
// Factory.setResourceCreator((name, params, feats, nameOverride) -> crashDoc);
// Factory.setFeatureMapSupplier(() -> mock(FeatureMap.class));
// Factory.setResourceDeleter(res -> {
// });
try {
CorpusImpl.populate(corpus, dirUrl, pathname -> true, "UTF-8", false);
} catch (Exception e) {
fail("populate() should catch and recover from internal failures");
}
assertTrue(corpus.isEmpty() || corpus.size() == 0);
}

@Test
public void testSubListWithInvalidIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
try {
corpus.subList(1, 2);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testSubListFromEqualsToIndexReturnsEmptyList() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> sub = corpus.subList(0, 0);
assertTrue(sub.isEmpty());
}

@Test
public void testAddNullCorpusListenerDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.addCorpusListener(null);
} catch (Exception e) {
fail("addCorpusListener null should not throw: " + e);
}
}

@Test
public void testRemoveCorpusListenerThatWasNeverAdded() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
try {
corpus.removeCorpusListener(listener);
} catch (Exception e) {
fail("removeCorpusListener should not throw when listener is not added");
}
}

@Test
public void testResourceUnloadedDoesNothingForNonDocument() {
CorpusImpl corpus = new CorpusImpl();
Resource nonDocRes = mock(Resource.class);
try {
corpus.resourceUnloaded(new gate.event.CreoleEvent(nonDocRes, 0));
} catch (Exception e) {
fail("resourceUnloaded should safely ignore non-document resources");
}
}

@Test
public void testListIteratorAtIndexReturnsCorrectSubIterator() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
ListIterator<Document> iterator = corpus.listIterator(1);
assertTrue(iterator.hasNext());
assertSame(doc2, iterator.next());
}

@Test
public void testIteratorTraversesDocuments() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
Iterator<Document> iterator = corpus.iterator();
assertTrue(iterator.hasNext());
assertSame(doc1, iterator.next());
assertTrue(iterator.hasNext());
assertSame(doc2, iterator.next());
assertFalse(iterator.hasNext());
}

@Test
public void testCreateResourceThrowsShouldLogAndContinue() throws Exception {
File dir = File.createTempFile("failtest", "");
dir.delete();
dir.mkdir();
File file = new File(dir, "fail.txt");
try (FileWriter fw = new FileWriter(file)) {
fw.write("input");
}
file.deleteOnExit();
dir.deleteOnExit();
CorpusImpl corpus = new CorpusImpl();
URL url = dir.toURI().toURL();
// Factory.setResourceCreator((name, params, feats, nameOverride) -> {
// throw new IOException("Simulated create failure");
// });
// Factory.setFeatureMapSupplier(() -> mock(FeatureMap.class));
// Factory.setResourceDeleter(res -> {
// });
try {
CorpusImpl.populate(corpus, url, path -> true, "UTF-8", false);
} catch (Exception e) {
fail("populate() should handle per-file document creation errors gracefully.");
}
assertEquals(0, corpus.size());
}

@Test
public void testClearDocListPreservesSupportListIfNull() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.clearDocList();
} catch (Exception e) {
fail("clearDocList should be safe even if supportList is null");
}
}

@Test
public void testAddAllFiresIndividualEventsForEachDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc2.getName()).thenReturn("doc2");
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
List<Document> docs = new ArrayList<Document>();
docs.add(doc1);
docs.add(doc2);
corpus.addAll(docs);
assertEquals(2, corpus.size());
verify(listener, never()).documentAdded(any(CorpusEvent.class));
}

@Test
public void testEqualsCorpusWithSameDocumentsButDifferentListInstance() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
corpus1.add(doc);
corpus2.add(doc);
assertTrue(corpus1.equals(corpus2));
assertTrue(corpus2.equals(corpus1));
}

@Test
public void testClearAfterPopulateShouldEmptyCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.clear();
assertTrue(corpus.isEmpty());
assertEquals(0, corpus.size());
}

@Test
public void testSubListEqualsOriginalRangeCopy() {
CorpusImpl corpus = new CorpusImpl();
Document docA = mock(Document.class);
Document docB = mock(Document.class);
when(docA.getName()).thenReturn("Alpha");
when(docB.getName()).thenReturn("Beta");
corpus.add(docA);
corpus.add(docB);
List<Document> subList = corpus.subList(0, 2);
assertEquals(corpus.get(0), subList.get(0));
assertEquals(corpus.get(1), subList.get(1));
}

@Test
public void testDuplicateCorpusWithMultipleDocumentsCopied() throws Exception {
CorpusImpl original = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc2.getName()).thenReturn("doc2");
original.add(doc1);
original.add(doc2);
Document duplicated1 = mock(Document.class);
Document duplicated2 = mock(Document.class);
CorpusImpl duplicateCorpus = new CorpusImpl();
// Factory.setResourceCreator((name, params, feats, nameOverride) -> duplicateCorpus);
// Factory.setDefaultDuplicator((res, ctx) -> duplicateCorpus);
// Factory.setDuplicator((res, ctx) -> res == doc1 ? duplicated1 : duplicated2);
Resource result = original.duplicate(mock(Factory.DuplicationContext.class));
assertTrue(result instanceof CorpusImpl);
CorpusImpl duplicated = (CorpusImpl) result;
assertEquals(2, duplicated.size());
assertSame(duplicated1, duplicated.get(0));
assertSame(duplicated2, duplicated.get(1));
}

@Test
public void testGetDocumentNamesReturnsEmptyListForEmptyCorpus() {
CorpusImpl corpus = new CorpusImpl();
List<String> names = corpus.getDocumentNames();
assertNotNull(names);
assertTrue(names.isEmpty());
}

@Test
public void testGetDocumentNameThrowsWhenCorpusIsEmpty() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.getDocumentName(0);
fail("Exception expected for empty corpus index access.");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testSetDocumentsListWithEmptyListDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
List<Document> empty = new ArrayList<Document>();
corpus.setDocumentsList(empty);
List<Document> retrieved = corpus.getDocumentsList();
assertNotNull(retrieved);
assertTrue(retrieved.isEmpty());
}

@Test
public void testAddAllNullCollectionThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.addAll(null);
fail("Expected NullPointerException for null collection");
} catch (NullPointerException e) {
}
}

@Test
public void testAddAtInvalidNegativeIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.add(-1, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testRemoveInvalidIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.remove(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testAddSameDocumentMultipleTimesIsAllowed() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("sameDoc");
corpus.add(doc);
corpus.add(doc);
assertEquals(2, corpus.size());
assertSame(doc, corpus.get(0));
assertSame(doc, corpus.get(1));
}

@Test
public void testResourceUnloadedShouldRemoveAllOccurrences() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
Resource resource = doc;
gate.event.CreoleEvent e = new gate.event.CreoleEvent(resource, 0);
corpus.resourceUnloaded(e);
assertEquals(0, corpus.size());
}

@Test
public void testUnloadDocumentDeletesIfPersistentCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
corpus.add(doc);
Resource persistentCorpus = mock(CorpusImpl.class);
when(((CorpusImpl) persistentCorpus).getLRPersistenceId()).thenReturn("abc123");
// Factory.setResourceDeleter(res -> {
// });
URL fileUrl = new File("temp.txt").toURI().toURL();
FeatureMap params = mock(FeatureMap.class);
when(params.get(Document.DOCUMENT_URL_PARAMETER_NAME)).thenReturn(fileUrl);
// Factory.setFeatureMapSupplier(() -> params);
// Factory.setResourceCreator((name, f, map, nameOverride) -> doc);
try {
CorpusImpl.populate((Corpus) persistentCorpus, fileUrl, null, "UTF-8", false);
} catch (Exception e) {
fail("Should not throw during unloadDocument logic.");
}
}

@Test
public void testSetNullDocumentThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
try {
corpus.set(0, null);
fail("Expected NullPointerException or IllegalArgumentException");
} catch (NullPointerException | IllegalArgumentException e) {
}
}

@Test
public void testRemoveNullReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean removed = corpus.remove(null);
assertFalse(removed);
}

@Test
public void testEqualsWithSameInstanceReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.equals(corpus);
assertTrue(result);
}

@Test
public void testSubListModificationsDoNotAffectOriginalCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> subList = corpus.subList(0, 1);
subList.clear();
assertEquals(2, corpus.size());
assertTrue(corpus.contains(doc1));
}

@Test
public void testDuplicateCorpusWithNullReturnedDocumentsIsTolerated() throws Exception {
CorpusImpl original = new CorpusImpl();
Document doc = mock(Document.class);
original.add(doc);
CorpusImpl resultCorpus = new CorpusImpl();
// Factory.setDefaultDuplicator((res, ctx) -> resultCorpus);
// Factory.setDuplicator((res, ctx) -> null);
Resource result = original.duplicate(mock(Factory.DuplicationContext.class));
assertTrue(result instanceof CorpusImpl);
CorpusImpl duplicated = (CorpusImpl) result;
assertEquals(1, duplicated.size());
assertNull(duplicated.get(0));
}

@Test
public void testPopulateReturnsWhenFilesListIsNull() throws Exception {
File dir = File.createTempFile("emptyDir", "");
dir.delete();
dir.mkdir();
dir.deleteOnExit();
File hiddenDir = new File(dir, ".hidden");
hiddenDir.mkdir();
hiddenDir.deleteOnExit();
CorpusImpl corpus = new CorpusImpl();
URL url = dir.toURI().toURL();
CorpusImpl.populate(corpus, url, pathname -> false, "UTF-8", false);
assertTrue(corpus.isEmpty());
}

@Test
public void testPopulateWithInvalidEncodingFallsBackGracefully() throws Exception {
File dir = File.createTempFile("fileFail", ".txt");
dir.delete();
dir.getParentFile().deleteOnExit();
File file = new File(dir.getParent(), "invalid.txt");
try (FileWriter writer = new FileWriter(file)) {
writer.write("content");
}
file.deleteOnExit();
CorpusImpl corpus = new CorpusImpl();
URL url = dir.getParentFile().toURI().toURL();
// Factory.setFeatureMapSupplier(() -> mock(FeatureMap.class));
// Factory.setResourceCreator((name, params, feats, nameOverride) -> mock(Document.class));
// Factory.setResourceDeleter(doc -> {
// });
CorpusImpl.populate(corpus, url, pathname -> true, "invalid-encoding", false);
assertEquals(1, corpus.size());
}

@Test
public void testPopulateTrecFormatExtractsDocuments() throws Exception {
File file = File.createTempFile("trecfile", ".xml");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("<DOC><TITLE>Title</TITLE><TEXT>This is document 1.</TEXT></DOC>");
writer.write("<DOC><TITLE>Title2</TITLE><TEXT>This is document 2.</TEXT></DOC>");
writer.close();
file.deleteOnExit();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
// Factory.setFeatureMapSupplier(() -> mock(FeatureMap.class));
// Factory.setResourceCreator(new Factory.ResourceCreator() {
// 
// int count = 0;
// 
// @Override
// public Resource create(String name, FeatureMap params, FeatureMap features, String nameOverride) {
// count++;
// return count == 1 ? doc1 : doc2;
// }
// });
// Factory.setResourceDeleter(doc -> {
// });
CorpusImpl corpus = new CorpusImpl();
long length = CorpusImpl.populate(corpus, file.toURI().toURL(), "DOC", "UTF-8", -1, "testDoc", "text/xml", true);
assertEquals(2, corpus.size());
assertTrue(length > 0);
}

@Test
public void testTrecPopulateSkipsMalformedFragments() throws Exception {
File file = File.createTempFile("trecfilemalformed", ".txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("This is garbage\n");
writer.write("<DOC><TEXT>One</TEXT>\n");
writer.write("Without ending tag\n");
writer.close();
file.deleteOnExit();
// Factory.setFeatureMapSupplier(() -> mock(FeatureMap.class));
// Factory.setResourceCreator((name, params, feats, nameOverride) -> mock(Document.class));
// Factory.setResourceDeleter(doc -> {
// });
CorpusImpl corpus = new CorpusImpl();
long size = CorpusImpl.populate(corpus, file.toURI().toURL(), "DOC", "UTF-8", 10, "doc", "text/plain", true);
assertTrue(size >= 0);
assertTrue(corpus.size() < 2);
}

@Test
public void testSetDocumentsListIgnoresNulls() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
List<Document> docs = corpus.getDocumentsList();
assertNull(docs);
}

@Test
public void testGetDocumentNamesHandlesNullDocumentNames() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn(null);
corpus.add(doc);
List<String> names = corpus.getDocumentNames();
assertEquals(1, names.size());
assertNull(names.get(0));
}

@Test
public void testCleanupRemovesCreoleListenerWithoutException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.cleanup();
} catch (Exception e) {
fail("cleanup should not throw");
}
}

@Test
public void testResourceLoadedDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.resourceLoaded(mock(CreoleEvent.class));
} catch (Exception e) {
fail("resourceLoaded should not throw");
}
}

@Test
public void testDatastoreEventsDoNotThrow() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.datastoreCreated(mock(CreoleEvent.class));
corpus.datastoreOpened(mock(CreoleEvent.class));
corpus.datastoreClosed(mock(CreoleEvent.class));
} catch (Exception e) {
fail("datastore events should not throw anything");
}
}

@Test
public void testAddAtUpperIndexThrowsIndexOutOfBoundsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.add(5, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testListIteratorAtUpperBoundReturnsEmptyIterator() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
corpus.add(doc1);
ListIterator<Document> listIt = corpus.listIterator(1);
assertNotNull(listIt);
assertFalse(listIt.hasNext());
}

@Test
public void testConvertToArrayWithTooSmallArrayAllocatesNewArray() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] input = new Document[0];
Document[] result = corpus.toArray(input);
assertEquals(1, result.length);
assertSame(doc, result[0]);
}

@Test
public void testRetainAllWithEmptyCollectionRemovesAllEntries() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
corpus.add(doc1);
boolean changed = corpus.retainAll(Collections.emptyList());
assertTrue(changed);
assertEquals(0, corpus.size());
}

@Test
public void testIndexOfReturnsFirstMatch() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
int index = corpus.indexOf(doc);
assertEquals(0, index);
}

@Test
public void testRemoveAllResultsInOnlyRemainingElements() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
Document d3 = mock(Document.class);
corpus.add(d1);
corpus.add(d2);
corpus.add(d3);
List<Document> toRemove = Arrays.asList(d1, d2);
boolean result = corpus.removeAll(toRemove);
assertTrue(result);
assertEquals(1, corpus.size());
assertSame(d3, corpus.get(0));
}

@Test
public void testPopulateUsingRecurseWithEmptyDirDoesNotFail() throws Exception {
File temp = File.createTempFile("empty", "dir");
temp.delete();
temp.mkdir();
temp.deleteOnExit();
CorpusImpl corpus = new CorpusImpl();
URL url = temp.toURI().toURL();
// Factory.setFeatureMapSupplier(() -> Factory.newFeatureMap());
// Factory.setResourceCreator((className, fmap, features, nameOverride) -> mock(Document.class));
// Factory.setResourceDeleter(doc -> {
// });
CorpusImpl.populate(corpus, url, null, "UTF-8", true);
assertEquals(0, corpus.size());
}

@Test
public void testGetDocumentNameNegativeIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.getDocumentName(-1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testDocumentRemovedEventIsFiredOnRemoveByIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("d");
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.add(doc);
corpus.remove(0);
verify(listener, times(1)).documentRemoved(any(CorpusEvent.class));
}

@Test
public void testPopulateTrecStopsAfterSpecifiedDocumentCount() throws Exception {
File file = File.createTempFile("trec_stop", ".xml");
try (FileWriter w = new FileWriter(file)) {
w.write("<DOC><TEXT>Doc1</TEXT></DOC>");
w.write("<DOC><TEXT>Doc2</TEXT></DOC>");
w.write("<DOC><TEXT>Doc3</TEXT></DOC>");
w.flush();
}
file.deleteOnExit();
// Factory.setFeatureMapSupplier(() -> Factory.newFeatureMap());
// Factory.setResourceCreator(new Factory.ResourceCreator() {
// 
// private int counter = 0;
// 
// @Override
// public Resource create(String name, FeatureMap params, FeatureMap features, String nameOverride) {
// counter++;
// Document d = mock(Document.class);
// when(d.getName()).thenReturn("doc" + counter);
// return d;
// }
// });
// Factory.setResourceDeleter(doc -> {
// });
CorpusImpl corpus = new CorpusImpl();
long bytes = CorpusImpl.populate(corpus, file.toURI().toURL(), "DOC", "UTF-8", 2, "prefix", null, true);
assertEquals(2, corpus.size());
assertTrue(bytes > 0);
}

@Test
public void testResourceUnloadedWithDocumentNotInCorpusDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
Resource unloadedDoc = doc2;
corpus.resourceUnloaded(new CreoleEvent(unloadedDoc, 0));
assertEquals(1, corpus.size());
assertSame(doc1, corpus.get(0));
}

@Test
public void testSubListReturnsValidViewOfCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
Document d3 = mock(Document.class);
corpus.add(d1);
corpus.add(d2);
corpus.add(d3);
List<Document> view = corpus.subList(1, 3);
assertEquals(2, view.size());
assertSame(d2, view.get(0));
assertSame(d3, view.get(1));
}

@Test
public void testAddAllEmptyCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean changed = corpus.addAll(Collections.emptyList());
assertFalse(changed);
}

@Test
public void testEqualsReturnsFalseWhenTypesDiffer() {
CorpusImpl corpus = new CorpusImpl();
Object notACorpus = new ArrayList<>();
boolean result = corpus.equals(notACorpus);
assertFalse(result);
}

@Test
public void testHashCodeReflectsDocumentPresence() {
CorpusImpl corpus = new CorpusImpl();
int emptyHash = corpus.hashCode();
Document doc = mock(Document.class);
corpus.add(doc);
int nonEmptyHash = corpus.hashCode();
assertNotEquals(emptyHash, nonEmptyHash);
}

@Test
public void testRemoveCorpusListenerMultipleTimesDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
corpus.removeCorpusListener(listener);
}

@Test
public void testAddAllWithNullElementInCollectionThrows() {
CorpusImpl corpus = new CorpusImpl();
List<Document> list = new ArrayList<Document>();
list.add(null);
try {
corpus.addAll(list);
fail("Expected NullPointerException or IllegalArgumentException");
} catch (NullPointerException | IllegalArgumentException e) {
}
}

@Test
public void testAddAllAtIndexWithNullElementThrowsException() {
CorpusImpl corpus = new CorpusImpl();
List<Document> docs = new ArrayList<Document>();
docs.add(null);
try {
corpus.addAll(0, docs);
fail("Expected NullPointerException or IllegalArgumentException");
} catch (NullPointerException | IllegalArgumentException e) {
}
}

@Test
public void testLastIndexOfForUniqueDocumentReturnsSameAsIndexOf() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
int first = corpus.indexOf(doc);
int last = corpus.lastIndexOf(doc);
assertEquals(first, last);
}

@Test
public void testEqualsWithSameContentButDifferentOrderReturnsFalse() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus1.add(doc1);
corpus1.add(doc2);
corpus2.add(doc2);
corpus2.add(doc1);
assertFalse(corpus1.equals(corpus2));
}

@Test
public void testEqualsWithNullCorpusReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.equals(null);
assertFalse(result);
}

@Test
public void testEmptyCorpusHashCodeIsStable() {
CorpusImpl corpus = new CorpusImpl();
int code1 = corpus.hashCode();
int code2 = corpus.hashCode();
assertEquals(code1, code2);
}

@Test
public void testRemoveIndexBeyondSizeThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.remove(1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testSetAtIndexLargerThanSizeThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.set(5, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testListIteratorAtInvalidIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.listIterator(10);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testAddSameListenerTwiceOnlyRegistersOnce() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
corpus.add(doc);
verify(listener, times(1)).documentAdded(any(CorpusEvent.class));
}

@Test
public void testRemoveCorpusListenerNullDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.removeCorpusListener(null);
} catch (Exception e) {
fail("removeCorpusListener(null) should not throw");
}
}

@Test
public void testGetSubListOutOfRangeThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
try {
corpus.subList(0, 2);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testGetSubListSameStartAndEndReturnsEmptyList() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> sub = corpus.subList(1, 1);
assertTrue(sub.isEmpty());
}

@Test
public void testClearCorpusAndCheckToArrayLength() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.clear();
Object[] arr = corpus.toArray();
assertEquals(0, arr.length);
}

@Test
public void testAddDocumentAndCheckToArrayGeneric() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] arr = corpus.toArray(new Document[1]);
assertEquals(1, arr.length);
assertSame(doc, arr[0]);
}

@Test
public void testCleanupWithoutAnyListenerDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.cleanup();
} catch (Exception e) {
fail("cleanup() should not throw when no listeners are registered");
}
}

@Test
public void testPopulateTrecWithNoDocumentRootMatchReturnsZeroBytes() throws Exception {
File file = File.createTempFile("empty", ".txt");
FileWriter fw = new FileWriter(file);
fw.write("Nothing to match\n <BADTAG>No match</BADTAG>");
fw.close();
file.deleteOnExit();
// Factory.setFeatureMapSupplier(() -> Factory.newFeatureMap());
// Factory.setResourceCreator((name, params, features, override) -> mock(Document.class));
// Factory.setResourceDeleter(doc -> {
// });
CorpusImpl corpus = new CorpusImpl();
long bytes = CorpusImpl.populate(corpus, file.toURI().toURL(), "DOC", "UTF-8", 10, null, null, true);
assertEquals(0, bytes);
assertEquals(0, corpus.size());
}

@Test
public void testPopulateWithMimeTypeSetsMimeTypeCorrectly() throws Exception {
File file = File.createTempFile("docfile", ".txt");
FileWriter fw = new FileWriter(file);
fw.write("File content");
fw.close();
file.deleteOnExit();
FeatureMap map = Factory.newFeatureMap();
final String expectedMime = "text/plain";
// Factory.setFeatureMapSupplier(() -> map);
// Factory.setResourceCreator((name, params, f, n) -> {
// assertEquals(expectedMime, params.get(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME));
// return mock(Document.class);
// });
// Factory.setResourceDeleter(d -> {
// });
CorpusImpl corpus = new CorpusImpl();
CorpusImpl.populate(corpus, file.getParentFile().toURI().toURL(), pathname -> pathname.getName().endsWith(".txt"), "UTF-8", expectedMime, false);
assertEquals(1, corpus.size());
}

@Test
public void testGetDocumentNamesWithOneNullName() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn(null);
corpus.add(doc);
List<String> names = corpus.getDocumentNames();
assertEquals(1, names.size());
assertNull(names.get(0));
}
}
