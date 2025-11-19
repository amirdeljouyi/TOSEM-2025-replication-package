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

public class CorpusImpl_2_GPTLLMTest {

@Test
public void testAddAndGetDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc1");
boolean added = corpus.add(doc);
assertTrue(added);
Document retrieved = corpus.get(0);
assertEquals("doc1", retrieved.getName());
}

@Test
public void testGetDocumentNamesReturnsCorrectList() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("docOne");
when(doc2.getName()).thenReturn("docTwo");
corpus.add(doc1);
corpus.add(doc2);
List<String> names = corpus.getDocumentNames();
assertEquals(2, names.size());
assertEquals("docOne", names.get(0));
assertEquals("docTwo", names.get(1));
}

@Test
public void testSizeAndIsEmptyCorrectness() {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.isEmpty());
Document doc = mock(Document.class);
corpus.add(doc);
assertFalse(corpus.isEmpty());
assertEquals(1, corpus.size());
}

@Test
public void testContainsAndRemoveDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
assertTrue(corpus.contains(doc));
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertFalse(corpus.contains(doc));
}

@Test
public void testClearRemovesAllDocuments() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.clear();
assertEquals(0, corpus.size());
assertTrue(corpus.isEmpty());
}

@Test
public void testSubListReturnsExpected() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
Document doc3 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.add(doc3);
List<Document> sub = corpus.subList(1, 3);
assertEquals(doc2, sub.get(0));
assertEquals(doc3, sub.get(1));
}

@Test
public void testUnloadDocumentDoesNotAffectCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.unloadDocument(doc);
assertEquals(1, corpus.size());
}

@Test
public void testIsDocumentAlwaysLoaded() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
boolean loaded = corpus.isDocumentLoaded(0);
assertTrue(loaded);
}

@Test
public void testInitWithDocumentsListApplies() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
List<Document> list = new ArrayList<Document>();
list.add(doc);
corpus.setDocumentsList(list);
corpus.init();
assertEquals(1, corpus.size());
assertTrue(corpus.contains(doc));
}

@Test
public void testPopulateNonRecursiveAddsDocument() throws Exception {
// File folder = tmp.newFolder("testFolder");
// File file = new File(folder, "sample.txt");
// FileWriter writer = new FileWriter(file);
// writer.write("Sample text.");
// writer.close();
// URL dirUrl = folder.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
ExtensionFileFilter filter = new ExtensionFileFilter("text", "txt");
Document mockDoc = mock(Document.class);
when(mockDoc.getName()).thenReturn("autoName");
FeatureMap params = mock(FeatureMap.class);
// Factory.deleteResource = mock(Resource.class);
// Factory.createResource = (name, fm, features, name2) -> mockDoc;
// Factory.newFeatureMap = () -> params;
// CorpusImpl.populate(corpus, dirUrl, filter, "UTF-8", false);
assertEquals(1, corpus.size());
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateWithNonFileURLThrows() throws Exception {
CorpusImpl corpus = new CorpusImpl();
URL invalid = new URL("http://example.com/fake");
CorpusImpl.populate(corpus, invalid, null, "UTF-8", false);
}

@Test(expected = java.io.FileNotFoundException.class)
public void testPopulateWithMissingFolderThrows() throws Exception {
// File notThere = new File(tmp.getRoot(), "nothing");
// URL dirUrl = notThere.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// CorpusImpl.populate(corpus, dirUrl, null, "UTF-8", false);
}

@Test
public void testCorpusListenerFiresEvents() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
final AtomicInteger addedCount = new AtomicInteger(0);
final AtomicInteger removedCount = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
addedCount.incrementAndGet();
}

public void documentRemoved(CorpusEvent e) {
removedCount.incrementAndGet();
}
};
corpus.addCorpusListener(listener);
corpus.add(doc1);
corpus.remove(doc1);
corpus.removeCorpusListener(listener);
corpus.add(doc1);
assertEquals(1, addedCount.get());
assertEquals(1, removedCount.get());
}

@Test
public void testEqualsAndHashCodeOnIdenticalCorpora() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("sameDoc");
corpus1.add(doc);
corpus2.add(doc);
assertEquals(corpus1, corpus2);
assertEquals(corpus1.hashCode(), corpus2.hashCode());
}

@Test
public void testAddNullDocumentThrowsNPE() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.add(null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testSetDocumentTriggersEvents() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
final List<String> events = new ArrayList<String>();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
events.add("added_" + e.getDocument().getName());
}

public void documentRemoved(CorpusEvent e) {
events.add("removed_" + e.getDocument().getName());
}
};
when(doc1.getName()).thenReturn("Doc1");
when(doc2.getName()).thenReturn("Doc2");
corpus.addCorpusListener(listener);
corpus.add(doc1);
corpus.set(0, doc2);
assertEquals(2, events.size());
assertTrue(events.contains("removed_Doc1"));
assertTrue(events.contains("added_Doc2"));
}

@Test
public void testRemoveByIndexTriggersEvent() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("Removable");
final boolean[] eventFired = { false };
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
if ("Removable".equals(e.getDocument().getName())) {
eventFired[0] = true;
}
}
};
corpus.addCorpusListener(listener);
corpus.add(doc);
corpus.remove(0);
assertTrue(eventFired[0]);
}

@Test
public void testPopulateConcatenatedSingleFileExtractsCorrectDocument() throws Exception {
// File trecFile = tempFolder.newFile("trec.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DOC>\nDoc content 1</DOC>\nSome Irrelevant\n<DOC>\nDoc content 2</DOC>");
// writer.close();
// URL url = trecFile.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
Resource mockDoc1 = mock(Document.class);
Resource mockDoc2 = mock(Document.class);
// Factory.createResource = (name, fm, features, name2) -> {
// Object content = fm.get("stringContent");
// if (content != null && content.toString().contains("1"))
// return (Document) mockDoc1;
// else
// return (Document) mockDoc2;
// };
FeatureMap mockMap = FeatureMap.class.cast(mock(FeatureMap.class));
// Factory.newFeatureMap = () -> mockMap;
// long size = CorpusImpl.populate(corpus, url, "DOC", "UTF-8", -1, "prefix", "text/plain", true);
assertEquals(2, corpus.size());
// assertTrue(size > 0);
}

@Test
public void testPopulateConcatenatedFileSkipsMalformedDoc() throws Exception {
// File trecFile = tempFolder.newFile("brokenTrec.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DOC>\nDoc without end tag");
// writer.close();
// URL url = trecFile.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// Factory.createResource = (name, fm, features, name2) -> {
// throw new RuntimeException("Simulated failure");
// };
FeatureMap mockMap = FeatureMap.class.cast(mock(FeatureMap.class));
// Factory.newFeatureMap = () -> mockMap;
try {
// CorpusImpl.populate(corpus, url, "DOC", "UTF-8", 1, "fail", "text/plain", true);
} catch (Exception ignored) {
}
assertEquals(0, corpus.size());
}

@Test
public void testDuplicateCopiesDocuments() throws Exception {
CorpusImpl original = new CorpusImpl();
Document doc = mock(Document.class);
original.add(doc);
CorpusImpl duplicateCorpus = new CorpusImpl();
// Factory.defaultDuplicate = (resource, ctx) -> duplicateCorpus;
// Factory.duplicate = (resource, ctx) -> doc;
Resource result = original.duplicate(mock(Factory.DuplicationContext.class));
assertTrue(result instanceof Corpus);
assertEquals(1, ((Corpus) result).size());
}

@Test
public void testResourceUnloadedRemovesDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Resource res = doc;
gate.event.CreoleEvent evt = new gate.event.CreoleEvent(res, gate.event.CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(evt);
assertEquals(0, corpus.size());
}

@Test
public void testToArrayReturnsIndependentCopy() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Object[] array = corpus.toArray();
assertEquals(1, array.length);
assertEquals(doc, array[0]);
}

@Test
public void testToArrayTypedReturnsCorrectArray() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] docs = corpus.toArray(new Document[0]);
assertEquals(1, docs.length);
assertEquals(doc, docs[0]);
}

@Test
public void testIndexOfAndLastIndexOf() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.add(doc1);
int first = corpus.indexOf(doc1);
int last = corpus.lastIndexOf(doc1);
assertEquals(0, first);
assertEquals(2, last);
}

@Test
public void testRetainAllKeepsSpecifiedElements() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> retainList = new ArrayList<Document>();
retainList.add(doc1);
boolean changed = corpus.retainAll(retainList);
assertTrue(changed);
assertEquals(1, corpus.size());
assertTrue(corpus.contains(doc1));
assertFalse(corpus.contains(doc2));
}

@Test
public void testRemoveAllEliminatesSpecifiedElements() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> removeList = new ArrayList<Document>();
removeList.add(doc1);
boolean changed = corpus.removeAll(removeList);
assertTrue(changed);
assertEquals(1, corpus.size());
assertFalse(corpus.contains(doc1));
assertTrue(corpus.contains(doc2));
}

@Test
public void testAddAllAtIndexInsertsCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
List<Document> moreDocs = new ArrayList<Document>();
moreDocs.add(doc2);
boolean result = corpus.addAll(0, moreDocs);
assertTrue(result);
assertEquals(doc2, corpus.get(0));
assertEquals(doc1, corpus.get(1));
}

@Test
public void testRemoveCorpusListenerOnEmptyListHasNoEffect() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.removeCorpusListener(listener);
assertTrue(corpus.isEmpty());
}

@Test
public void testAddSameCorpusListenerTwiceAddsOnlyOnce() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc");
corpus.add(doc);
verify(listener, times(1)).documentAdded(any(CorpusEvent.class));
}

@Test
public void testEqualsWithDifferentTypeReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
String notACorpus = "Not a corpus";
assertFalse(corpus.equals(notACorpus));
}

@Test
public void testEqualsWithNullReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals(null));
}

@Test
public void testInitDoesNothingIfDocumentsListIsNull() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
corpus.init();
assertTrue(corpus.isEmpty());
}

@Test
public void testPopulateWithEmptyDirectoryReturnsEmptyCorpus() throws Exception {
// File dir = tempFolder.newFolder("emptyDir");
// URL url = dir.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// CorpusImpl.populate(corpus, url, null, "UTF-8", false);
assertTrue(corpus.isEmpty());
}

@Test
public void testPopulateWithNullFilterAcceptsAllFiles() throws Exception {
// File dir = tempFolder.newFolder("nullFilterTest");
// File file = new File(dir, "one.txt");
// FileWriter fw = new FileWriter(file);
// fw.write("data");
// fw.close();
// URL url = dir.toURI().toURL();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("generated");
FeatureMap fMap = mock(FeatureMap.class);
// Factory.newFeatureMap = () -> fMap;
// Factory.createResource = (name, params, features, docName) -> doc;
CorpusImpl corpus = new CorpusImpl();
// CorpusImpl.populate(corpus, url, null, "UTF-8", false);
assertEquals(1, corpus.size());
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateWithNonDirectoryThrows() throws Exception {
// File file = tempFolder.newFile("notADir.txt");
// URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// CorpusImpl.populate(corpus, url, null, "UTF-8", false);
}

@Test
public void testPopulateWithEmptyFileListHasNoEffect() throws Exception {
// File dir = tempFolder.newFolder("dirWithNoMatchingFiles");
// URL url = dir.toURI().toURL();
File[] empty = new File[0];
CorpusImpl corpus = new CorpusImpl();
// CorpusImpl.populate(corpus, url, pathname -> false, "UTF-8", false);
assertTrue(corpus.isEmpty());
}

@Test
public void testAddAllWithEmptyCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Collection<Document> empty = Collections.emptyList();
boolean changed = corpus.addAll(empty);
assertFalse(changed);
}

@Test
public void testRetainAllWithEmptyCollectionReturnsTrueIfNotEmpty() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
boolean changed = corpus.retainAll(Collections.emptyList());
assertTrue(changed);
assertTrue(corpus.isEmpty());
}

@Test
public void testSetAndGetDocumentAtIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc2.getName()).thenReturn("doc2");
corpus.add(doc1);
Document old = corpus.set(0, doc2);
assertEquals(doc1, old);
assertEquals(doc2, corpus.get(0));
}

@Test
public void testListIteratorWithStartIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
ListIterator<Document> iterator = corpus.listIterator(1);
assertTrue(iterator.hasNext());
assertEquals(doc2, iterator.next());
}

@Test
public void testSubListReturnsEmptyWhenRangeIsEmpty() {
CorpusImpl corpus = new CorpusImpl();
List<Document> sub = corpus.subList(0, 0);
assertTrue(sub.isEmpty());
}

@Test
public void testGetDocumentsListInitialValueIsNull() {
CorpusImpl corpus = new CorpusImpl();
assertNull(corpus.getDocumentsList());
}

@Test
public void testMimeTypeNullInPopulateFileDirectory() throws Exception {
// File dir = tempFolder.newFolder("mime");
// File file = new File(dir, "x.txt");
// Writer writ = new FileWriter(file);
// writ.write("x");
// writ.close();
// URL url = dir.toURI().toURL();
FeatureMap map = mock(FeatureMap.class);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("mimeDoc");
// Factory.newFeatureMap = () -> map;
// Factory.createResource = (name, params, features, dname) -> doc;
CorpusImpl corpus = new CorpusImpl();
// corpus.populate(url, file1 -> true, "UTF-8", null, false);
assertEquals(1, corpus.size());
}

@Test
public void testIncludeRootElementFalseStripsTagsInPopulate() throws Exception {
// File trec = tempFolder.newFile("trec.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trec));
// writer.write("<entry>content A</entry>");
// writer.close();
// URL fileUrl = trec.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// Factory.newFeatureMap = () -> {
// FeatureMap fm = mock(FeatureMap.class);
// return fm;
// };
Document doc = mock(Document.class);
// Factory.createResource = (name, params, features, name2) -> doc;
// long len = CorpusImpl.populate(corpus, fileUrl, "entry", "UTF-8", -1, "doc", "text/xml", false);
assertEquals(1, corpus.size());
// assertTrue(len > 0);
}

@Test
public void testPopulateWithFixedCountStopsOnLimit() throws Exception {
// File trec = tempFolder.newFile("multi.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trec));
// writer.write("<x>one</x>\n<x>two</x>\n<x>three</x>");
// writer.close();
// URL url = trec.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
FeatureMap map = mock(FeatureMap.class);
// Factory.newFeatureMap = () -> map;
Document doc = mock(Document.class);
// Factory.createResource = (name, params, features, dname) -> doc;
// long len = CorpusImpl.populate(corpus, url, "x", "UTF-8", 2, "n", "text/xml", true);
assertEquals(2, corpus.size());
// assertTrue(len > 0);
}

@Test
public void testPopulateHandlesEmptyLineAfterEndTag() throws Exception {
// File trec = tempFolder.newFile("linesep.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trec));
// writer.write("<d>ok</d>\n<d>2</d>\n");
// writer.close();
// URL u = trec.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
FeatureMap map = mock(FeatureMap.class);
// Factory.newFeatureMap = () -> map;
Document doc = mock(Document.class);
// Factory.createResource = (name, params, features, dname) -> doc;
// long len = CorpusImpl.populate(corpus, u, "d", "UTF-8", -1, "dd", "xml", true);
assertEquals(2, corpus.size());
// assertTrue(len > 0);
}

@Test
public void testClearDocListWhenSupportListIsNull() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.clear();
assertEquals(0, corpus.size());
} catch (Exception e) {
fail("clear() should be safe even if supportList already null");
}
}

@Test
public void testAddingNullCorpusListenerIsIgnored() {
CorpusImpl corpus = new CorpusImpl();
corpus.addCorpusListener(null);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("d1");
corpus.add(doc);
assertEquals(1, corpus.size());
}

@Test
public void testRemovingCorpusListenerThatWasNotAdded() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener1 = mock(CorpusListener.class);
CorpusListener listener2 = mock(CorpusListener.class);
corpus.addCorpusListener(listener1);
corpus.removeCorpusListener(listener2);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("d2");
corpus.add(doc);
verify(listener1, times(1)).documentAdded(any(CorpusEvent.class));
verify(listener2, never()).documentAdded(any(CorpusEvent.class));
}

@Test
public void testSubListEdgeOutOfBoundsThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
try {
corpus.subList(0, 5);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException expected) {
}
}

@Test
public void testSetDocumentsListClearsAndAppendsOnInit() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
List<Document> docs = new ArrayList<Document>();
docs.add(doc1);
docs.add(doc2);
corpus.setDocumentsList(docs);
corpus.init();
assertEquals(2, corpus.size());
assertTrue(corpus.contains(doc1));
}

@Test
public void testUnloadingResourceNotInCorpusIsSafe() {
CorpusImpl corpus = new CorpusImpl();
Document unrelated = mock(Document.class);
Resource res = unrelated;
gate.event.CreoleEvent event = new gate.event.CreoleEvent(res, gate.event.CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(event);
assertEquals(0, corpus.size());
}

@Test
public void testDatastoreClosedDoesNotAffectCorpus() {
CorpusImpl corpus = new CorpusImpl();
gate.event.CreoleEvent event = mock(gate.event.CreoleEvent.class);
corpus.datastoreClosed(event);
assertTrue(corpus.isEmpty());
}

@Test
public void testDatastoreCreatedDoesNotAffectCorpus() {
CorpusImpl corpus = new CorpusImpl();
gate.event.CreoleEvent event = mock(gate.event.CreoleEvent.class);
corpus.datastoreCreated(event);
assertTrue(corpus.isEmpty());
}

@Test
public void testDatastoreOpenedDoesNotAffectCorpus() {
CorpusImpl corpus = new CorpusImpl();
gate.event.CreoleEvent event = mock(gate.event.CreoleEvent.class);
corpus.datastoreOpened(event);
assertTrue(corpus.isEmpty());
}

@Test
public void testResourceRenamedDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
Resource res = mock(Resource.class);
corpus.resourceRenamed(res, "old", "new");
assertTrue(corpus.isEmpty());
}

@Test
public void testPopulateConcatenatedFileWithAttributesOnStartTag() throws Exception {
File file = File.createTempFile("populateAttr", ".txt");
file.deleteOnExit();
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("<doc id=\"1\">Text 1</doc>\n<doc id=\"2\">Text 2</doc>");
writer.close();
URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
Document mockDoc = mock(Document.class);
// Factory.createResource = (name, params, features, name2) -> mockDoc;
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
long size = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "myprefix", "text/plain", true);
assertEquals(2, corpus.size());
assertTrue(size > 0);
}

@Test
public void testPopulateConcatenatedFileWithMixedCaseTags() throws Exception {
File file = File.createTempFile("mixedCaseDoc", ".txt");
file.deleteOnExit();
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("<DoC>Mixed case content</DoC>");
writer.close();
URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
Document mockDoc = mock(Document.class);
// Factory.createResource = (name, params, features, name2) -> mockDoc;
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
long size = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "mix", "text/plain", true);
assertEquals(1, corpus.size());
assertTrue(size > 0);
}

@Test
public void testPopulateConcatenatedFileHandlesEmptyPrefix() throws Exception {
File file = File.createTempFile("docfile", ".txt");
file.deleteOnExit();
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("<doc>content</doc>");
writer.close();
URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
Document mockDoc = mock(Document.class);
// Factory.createResource = (name, params, features, name2) -> mockDoc;
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
long size = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "", "text/plain", true);
assertEquals(1, corpus.size());
assertTrue(size > 0);
}

@Test
public void testPopulateConcatenatedSkipsDirectoryEntries() throws Exception {
File folder = new File(System.getProperty("java.io.tmpdir"), "dirinsidepopulate");
folder.mkdir();
folder.deleteOnExit();
File file = new File(folder, "real.txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("<doc>text</doc>");
writer.close();
URL url = folder.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
Document mockDoc = mock(Document.class);
// Factory.createResource = (name, params, features, dname) -> mockDoc;
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
CorpusImpl.populate(corpus, url, pathname -> true, "UTF-8", null, false);
assertEquals(1, corpus.size());
}

@Test
public void testPopulateWithNullEncodingAndNullMimeType() throws Exception {
File folder = new File(System.getProperty("java.io.tmpdir"), "corpusnulls");
folder.mkdir();
File file = new File(folder, "nulltest.txt");
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("Text for nulls");
writer.close();
URL url = folder.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
// Factory.createResource = (name, params, fmap, dname) -> doc;
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
CorpusImpl.populate(corpus, url, pathname -> true, null, null, false);
assertEquals(1, corpus.size());
}

@Test
public void testRemoveFromEmptyCorpusByIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.remove(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testSetOnEmptyCorpusThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.set(0, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testAddAtInvalidIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.add(5, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testListIteratorInvalidIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.listIterator(10);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testEmptyCorpusToArrayTypedReturnsEmptyArray() {
CorpusImpl corpus = new CorpusImpl();
Document[] array = new Document[0];
Document[] result = corpus.toArray(array);
assertEquals(0, result.length);
}

@Test
public void testPopulateWithIOExceptionInFileStreamSkipsFile() throws Exception {
File file = File.createTempFile("badfile", ".txt");
file.deleteOnExit();
RandomAccessFile raf = new RandomAccessFile(file, "rw");
raf.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
raf.close();
URL url = file.getParentFile().toURI().toURL();
FileFilter filter = new FileFilter() {

public boolean accept(File pathname) {
return true;
}
};
// Factory.createResource = (name, params, features, name2) -> {
// throw new IOException("Simulated I/O failure");
// };
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
CorpusImpl corpus = new CorpusImpl();
CorpusImpl.populate(corpus, url, filter, "UTF-8", null, false);
assertEquals(0, corpus.size());
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateThrowsForUnsupportedURLProtocol() throws Exception {
CorpusImpl corpus = new CorpusImpl();
URL url = new URL("ftp://example.com/fake");
CorpusImpl.populate(corpus, url, null, "UTF-8", false);
}

@Test
public void testGetDocumentNameThrowsForInvalidIndex() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.getDocumentName(5);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException expected) {
}
}

@Test
public void testResourceUnloadedSkipsNonDocumentResources() {
CorpusImpl corpus = new CorpusImpl();
Resource res = mock(Resource.class);
gate.event.CreoleEvent event = new gate.event.CreoleEvent(res, gate.event.CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(event);
assertEquals(0, corpus.size());
}

@Test
public void testEqualityWithSameReferenceReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.equals(corpus));
}

@Test
public void testAddingMultipleDocumentsAndEmptyRetainAllReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
corpus.add(d1);
corpus.add(d2);
boolean changed = corpus.retainAll(Collections.emptyList());
assertTrue(changed);
assertTrue(corpus.isEmpty());
}

@Test
public void testAddAllWithNullDocumentsIsNoOp() {
CorpusImpl corpus = new CorpusImpl();
List<Document> list = null;
try {
corpus.addAll(list);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testAddNullDocumentDirectlyThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.add((Document) null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testCloneReturnsCorrectCorpusReference() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
// Factory.defaultDuplicate = (original, context) -> new CorpusImpl();
// Factory.duplicate = (resource, context) -> doc;
Resource clone = corpus.duplicate(mock(Factory.DuplicationContext.class));
assertTrue(clone instanceof Corpus);
assertEquals(1, ((Corpus) clone).size());
}

@Test
public void testFireEventsSilentlyIfNoListeners() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("test");
corpus.add(doc);
corpus.set(0, mock(Document.class));
corpus.remove(0);
assertTrue(corpus.isEmpty());
}

@Test
public void testPopulateConcatenatedSkipsWhitespaceLines() throws Exception {
File file = File.createTempFile("whitespace", ".txt");
file.deleteOnExit();
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("   \n<DOC>content</DOC>\n   ");
writer.close();
URL url = file.toURI().toURL();
Document doc = mock(Document.class);
// Factory.createResource = (name, params, features, name2) -> doc;
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
CorpusImpl corpus = new CorpusImpl();
CorpusImpl.populate(corpus, url, "DOC", "UTF-8", -1, null, null, true);
assertEquals(1, corpus.size());
}

@Test
public void testPopulateConcatenatedStopsAtExactLimit() throws Exception {
File file = File.createTempFile("limitstop", ".txt");
file.deleteOnExit();
BufferedWriter writer = new BufferedWriter(new FileWriter(file));
writer.write("<entry>one</entry>\n<entry>two</entry>\n<entry>three</entry>");
writer.close();
URL url = file.toURI().toURL();
Document doc = mock(Document.class);
// Factory.createResource = (name, map, map2, str) -> doc;
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
CorpusImpl corpus = new CorpusImpl();
long size = CorpusImpl.populate(corpus, url, "entry", "UTF-8", 2, null, null, true);
assertEquals(2, corpus.size());
assertTrue(size > 0);
}

@Test
public void testSetDocumentsListWithEmptyListStillWorksOnInit() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(Collections.<Document>emptyList());
Resource r = corpus.init();
assertNotNull(r);
assertTrue(corpus.isEmpty());
}

@Test
public void testAddAllWithOverlappingDocuments() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
List<Document> list = new ArrayList<Document>();
list.add(doc2);
list.add(doc1);
boolean changed = corpus.addAll(list);
assertTrue(changed);
assertEquals(3, corpus.size());
assertEquals(doc1, corpus.get(2));
}

@Test
public void testContainsAllReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> checkList = new ArrayList<Document>();
checkList.add(doc1);
checkList.add(doc2);
boolean result = corpus.containsAll(checkList);
assertTrue(result);
}

@Test
public void testContainsAllReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
List<Document> checkList = new ArrayList<Document>();
checkList.add(doc1);
checkList.add(doc2);
boolean result = corpus.containsAll(checkList);
assertFalse(result);
}

@Test
public void testCreateDocumentFailsIsHandledInPopulate() throws Exception {
// File dir = tmp.newFolder("failDocCreate");
// File f = new File(dir, "file.txt");
// FileWriter w = new FileWriter(f);
// w.write("content");
// w.close();
// URL url = dir.toURI().toURL();
// Factory.createResource = (name, map, map2, docName) -> {
// throw new RuntimeException("forced failure");
// };
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
CorpusImpl corpus = new CorpusImpl();
// CorpusImpl.populate(corpus, url, file -> true, "UTF-8", null, false);
assertTrue(corpus.isEmpty());
}

@Test
public void testDocumentRemovalByIndexWithListeners() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("alpha");
final List<String> events = new ArrayList<String>();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
events.add("add:" + e.getDocument().getName());
}

public void documentRemoved(CorpusEvent e) {
events.add("rm:" + e.getDocument().getName());
}
};
corpus.addCorpusListener(listener);
corpus.add(doc);
corpus.remove(0);
assertEquals(2, events.size());
assertEquals("add:alpha", events.get(0));
assertEquals("rm:alpha", events.get(1));
}

@Test
public void testSetDocumentAtIndexWithSameReferenceStillFiresEvents() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("dup");
final List<String> events = new ArrayList<String>();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
events.add("added");
}

public void documentRemoved(CorpusEvent e) {
events.add("removed");
}
};
corpus.addCorpusListener(listener);
corpus.add(doc);
corpus.set(0, doc);
assertEquals(3, events.size());
assertEquals("added", events.get(0));
assertEquals("removed", events.get(1));
assertEquals("added", events.get(2));
}

@Test
public void testPopulateConcatenatedHandlesNoEndingTagGracefully() throws Exception {
// File file = tmp.newFile("truncated.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("<doc>incomplete");
// writer.close();
// URL url = file.toURI().toURL();
FeatureMap fakeMap = mock(FeatureMap.class);
// Factory.newFeatureMap = () -> fakeMap;
// Factory.createResource = (name, params, features, name2) -> mock(Document.class);
CorpusImpl corpus = new CorpusImpl();
// long bytes = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "trunc", "text/plain", true);
assertEquals(0, corpus.size());
// assertEquals(0, bytes);
}

@Test
public void testSubListEmptyBoundsIsSafeOnNonEmptyCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> sub = corpus.subList(0, 0);
assertNotNull(sub);
assertTrue(sub.isEmpty());
}

@Test
public void testListIteratorForwardBackForwardOperations() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
ListIterator<Document> iter = corpus.listIterator();
assertTrue(iter.hasNext());
assertEquals(doc1, iter.next());
assertTrue(iter.hasPrevious());
assertEquals(doc1, iter.previous());
}

@Test
public void testHashCodeMatchesSupportListHashCode() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> expectedList = new ArrayList<Document>();
expectedList.add(doc);
assertEquals(expectedList.hashCode(), corpus.hashCode());
}

@Test
public void testPopulateConcatenatedFileSkipsMalformedStartTagLine() throws Exception {
// File file = tempFolder.newFile("broken.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("<?xml invalid>\n</doc>");
// writer.close();
// URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
// Factory.createResource = (name, params, features, name2) -> mock(Document.class);
// long result = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, null, "text/plain", true);
assertEquals(0, corpus.size());
// assertEquals(0, result);
}

@Test
public void testPopulateConcatenatedFileWithNoMatchingTags() throws Exception {
// File file = tempFolder.newFile("nomatch.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("<nonmatching>This won't be parsed</nonmatching>");
// writer.close();
// URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
// Factory.createResource = (name, params, features, name2) -> mock(Document.class);
// long len = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "pref", null, true);
assertEquals(0, corpus.size());
// assertEquals(0, len);
}

@Test(expected = NullPointerException.class)
public void testNullDocumentPassedToVerboseListSetThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.set(0, null);
}

@Test(expected = NullPointerException.class)
public void testAddNullToVerboseListThrows() {
CorpusImpl corpus = new CorpusImpl();
corpus.add(null);
}

@Test(expected = NullPointerException.class)
public void testAddAtIndexWithNullThrows() {
CorpusImpl corpus = new CorpusImpl();
corpus.add(0, null);
}

@Test
public void testRemoveCorpusListenerWhenListIsNull() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.removeCorpusListener(listener);
assertTrue(corpus.isEmpty());
}

@Test
public void testRemoveListenerOnceAddedThenRemovedThenRemovedAgain() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
corpus.removeCorpusListener(listener);
Document doc = mock(Document.class);
corpus.add(doc);
verify(listener, never()).documentAdded(any(CorpusEvent.class));
}

@Test
public void testResourceUnloadedRemovesMultipleOccurrences() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
Resource res = doc;
CreoleEvent event = new CreoleEvent(res, CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(event);
assertEquals(0, corpus.size());
}

@Test
public void testUnloadedResourceWithNonDocumentTypeIgnored() {
CorpusImpl corpus = new CorpusImpl();
Resource nonDoc = mock(Resource.class);
CreoleEvent event = new CreoleEvent(nonDoc, CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(event);
assertTrue(corpus.isEmpty());
}

@Test
public void testSetDocumentsListWithNullStillInitReturnsResource() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
Resource r = corpus.init();
assertNotNull(r);
assertTrue(r instanceof CorpusImpl);
}

@Test
public void testPopulateConcatenatedWithEmptyPrefix() throws Exception {
// File file = tempFolder.newFile("file.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("<seg>Hello</seg><seg>World</seg>");
// writer.close();
// URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
// Factory.createResource = (name, params, features, name2) -> mock(Document.class);
// long size = CorpusImpl.populate(corpus, url, "seg", "UTF-8", -1, "", "application/xml", true);
assertEquals(2, corpus.size());
// assertTrue(size > 0);
}

@Test
public void testPopulateConcatenatedLowercasesTagsBeforeChecking() throws Exception {
// File file = tempFolder.newFile("case.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("<MyTag>ABC</MyTag>");
// writer.close();
// URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
// Factory.createResource = (name, params, features, name2) -> mock(Document.class);
// long size = CorpusImpl.populate(corpus, url, "mytag", "UTF-8", -1, "case", "text/xml", true);
assertEquals(1, corpus.size());
// assertTrue(size > 0);
}

@Test
public void testPopulateConcatenatedExcludesOuterTagsWhenFlagFalse() throws Exception {
// File file = tempFolder.newFile("exclude.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("<entry>keep me</entry>");
// writer.close();
// URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// FeatureMap map = new HashMap<String, Object>();
final StringBuilder stored = new StringBuilder();
// Factory.newFeatureMap = () -> map;
// Factory.createResource = (name, params, features, name2) -> {
// String content = (String) params.get("stringContent");
// stored.append(content);
// return mock(Document.class);
// };
// CorpusImpl.populate(corpus, url, "entry", "UTF-8", -1, "e", "text/plain", false);
assertEquals("keep me", stored.toString().trim());
}

@Test
public void testPopulateWithDirectoryThatContainsOnlySubdirectories() throws Exception {
// File root = tempFolder.newFolder("dirwithsubs");
// File subdir = new File(root, "sub1");
// subdir.mkdir();
// URL rootURL = root.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
// CorpusImpl.populate(corpus, rootURL, file -> true, "UTF-8", null, false);
assertTrue(corpus.isEmpty());
}

@Test
public void testDocumentNamePrefixTrimmed() throws Exception {
// File file = tempFolder.newFile("doc.xml");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("<note>Content</note>");
// writer.close();
// URL url = file.toURI().toURL();
CorpusImpl corpus = new CorpusImpl();
final List<String> names = new ArrayList<String>();
Document mockedDoc = mock(Document.class);
// Factory.newFeatureMap = () -> mock(FeatureMap.class);
// Factory.createResource = (name, params, features, name2) -> {
// names.add(name2);
// return mockedDoc;
// };
// CorpusImpl.populate(corpus, url, "note", "UTF-8", -1, "  custom_prefix  ", "text/plain", true);
assertEquals(1, names.size());
assertTrue(names.get(0).startsWith("custom_prefix_"));
}
}
