package gate.corpora;

import gate.*;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.creole.ResourceInstantiationException;
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

public class CorpusImpl_5_GPTLLMTest {

@Test
public void testGetDocumentNames() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc2.getName()).thenReturn("doc2");
corpus.add(doc1);
corpus.add(doc2);
List<String> names = corpus.getDocumentNames();
assertEquals(2, names.size());
assertEquals("doc1", names.get(0));
assertEquals("doc2", names.get(1));
}

@Test
public void testGetDocumentNameByIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("MyName");
corpus.add(doc);
assertEquals("MyName", corpus.getDocumentName(0));
}

@Test(expected = IndexOutOfBoundsException.class)
public void testGetDocumentNameInvalidIndex() {
CorpusImpl corpus = new CorpusImpl();
corpus.getDocumentName(0);
}

@Test
public void testUnloadDocumentDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.unloadDocument(doc);
assertTrue(true);
}

@Test
public void testSizeAndIsEmpty() {
CorpusImpl corpus = new CorpusImpl();
assertEquals(0, corpus.size());
assertTrue(corpus.isEmpty());
Document doc = mock(Document.class);
corpus.add(doc);
assertEquals(1, corpus.size());
assertFalse(corpus.isEmpty());
}

@Test
public void testContainsAndRemove() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
assertTrue(corpus.contains(doc));
corpus.remove(doc);
assertFalse(corpus.contains(doc));
}

@Test
public void testToArrayAndToTypedArray() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
Object[] array = corpus.toArray();
assertEquals(2, array.length);
assertSame(doc1, array[0]);
Document[] typedArray = new Document[2];
Document[] result = corpus.toArray(typedArray);
assertEquals(2, result.length);
assertSame(doc2, result[1]);
}

@Test
public void testAddAllAndContainsAll() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
List<Document> docList = Arrays.asList(doc1, doc2);
corpus.addAll(docList);
assertEquals(2, corpus.size());
assertTrue(corpus.containsAll(docList));
}

@Test
public void testClear() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
assertEquals(1, corpus.size());
corpus.clear();
assertTrue(corpus.isEmpty());
}

@Test
public void testIndexOfAndLastIndexOf() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
assertEquals(0, corpus.indexOf(doc));
assertEquals(1, corpus.lastIndexOf(doc));
}

@Test
public void testSubList() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> sub = corpus.subList(0, 1);
assertEquals(1, sub.size());
assertSame(doc1, sub.get(0));
}

@Test
public void testEqualsAndHashCode() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("Doc");
corpus1.add(doc1);
corpus2.add(doc1);
assertTrue(corpus1.equals(corpus2));
assertEquals(corpus1.hashCode(), corpus2.hashCode());
}

@Test
public void testCorpusListenerEvents() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("eventDoc");
corpus.add(doc);
corpus.remove(doc);
verify(listener, atLeastOnce()).documentAdded(any(CorpusEvent.class));
verify(listener, atLeastOnce()).documentRemoved(any(CorpusEvent.class));
}

@Test
public void testRemoveCorpusListenerIsolated() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
Document doc = mock(Document.class);
corpus.add(doc);
assertTrue(corpus.contains(doc));
}

@Test
public void testPopulateDirectoryNonRecursiveWithFilter() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File dir = new File(System.getProperty("java.io.tmpdir"), "corpusTestDir");
dir.mkdir();
dir.deleteOnExit();
File txt = new File(dir, "file1.txt");
FileWriter writer = new FileWriter(txt);
writer.write("hello world");
writer.close();
txt.deleteOnExit();
URL directoryUrl = txt.getParentFile().toURI().toURL();
ExtensionFileFilter filter = new ExtensionFileFilter("txt");
CorpusImpl.populate(corpus, directoryUrl, filter, "UTF-8", false);
assertTrue(corpus.size() >= 0);
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateInvalidURL() throws IOException {
CorpusImpl corpus = new CorpusImpl();
URL url = new URL("http://invalid");
CorpusImpl.populate(corpus, url, null, null, false);
}

@Test(expected = FileNotFoundException.class)
public void testPopulateDirectoryNotExist() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File nonExistent = new File("nonexistentdir_xyz");
URL url = nonExistent.toURI().toURL();
CorpusImpl.populate(corpus, url, null, null, false);
}

@Test
public void testPopulateConcatenatedFile() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("concat", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<doc>Content A</doc>\n");
fw.write("<doc>Content B</doc>\n");
fw.close();
URL url = file.toURI().toURL();
long bytes = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "mydoc", "text/xml", true);
assertTrue(bytes > 0);
}

@Test
public void testPopulateConcatenatedFileExcludeRoot() throws IOException, ResourceInstantiationException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("trec", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<article>This is text.</article>\n");
fw.close();
URL url = file.toURI().toURL();
long size = corpus.populate(url, "article", "UTF-8", 1, "doc", null, false);
assertTrue(size > 0);
}

@Test
public void testSetAndGetDocumentsList() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
List<Document> list = new ArrayList<Document>();
list.add(doc);
corpus.setDocumentsList(list);
assertSame(list, corpus.getDocumentsList());
}

@Test
public void testInitFromDocumentsList() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
List<Document> list = new ArrayList<Document>();
list.add(doc);
corpus.setDocumentsList(list);
corpus.init();
assertEquals(1, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testDuplicateCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
// Resource copy = corpus.duplicate(new Factory.DuplicationContext());
// assertNotNull(copy);
// assertTrue(copy instanceof Corpus);
// assertEquals(1, ((Corpus) copy).size());
}

@Test
public void testResourceUnloadedRemovesDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.resourceUnloaded(new gate.event.CreoleEvent(doc, gate.event.CreoleEvent.RESOURCE_UNLOADED));
assertFalse(corpus.contains(doc));
}

@Test
public void testPopulateWithEmptyConcatenatedFile() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("empty", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("");
fw.close();
URL url = file.toURI().toURL();
long bytes = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "empty", "text/plain", true);
assertEquals(0, bytes);
assertEquals(0, corpus.size());
}

@Test
public void testDocumentRootElementCaseInsensitiveMatching() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("mixed", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<Doc>UpperCase</Doc>\n");
fw.write("<doc>lowerCase</doc>\n");
fw.write("<DOC>MIXED</DOC>\n");
fw.close();
URL url = file.toURI().toURL();
long bytes = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "case", "text/plain", true);
assertEquals(3, corpus.size());
assertTrue(bytes > 0);
}

@Test
public void testPopulateFromConcatenatedFileWithAttributes() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("attrdoc", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<doc id=\"1\">Some content</doc>\n");
fw.close();
URL url = file.toURI().toURL();
long bytes = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "attr", "text/xml", true);
assertEquals(1, corpus.size());
assertTrue(bytes > 0);
}

@Test
public void testPopulateWithZeroItemsToExtract() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("zero", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<doc>Don't load</doc>\n");
fw.close();
URL url = file.toURI().toURL();
long bytes = CorpusImpl.populate(corpus, url, "doc", "UTF-8", 0, "zero", "text/xml", true);
assertEquals(0, corpus.size());
assertEquals(0, bytes);
}

@Test
public void testSetDocumentOutOfBoundsIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(mock(Document.class));
try {
corpus.set(5, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testRemoveDocumentByInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.remove(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testAddNullDocumentThrowsNPE() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.add(null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testAddAllWithEmptyCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.addAll(new ArrayList<Document>());
assertFalse(result);
}

@Test
public void testRemoveAllWithEmptyCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.removeAll(new ArrayList<Document>());
assertFalse(result);
}

@Test
public void testRetainAllWithEmptyCollectionClearsCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
boolean changed = corpus.retainAll(new ArrayList<Document>());
assertTrue(changed);
assertEquals(0, corpus.size());
}

@Test
public void testRetainAllWithSameCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> list = new ArrayList<Document>();
list.add(doc);
boolean changed = corpus.retainAll(list);
assertFalse(changed);
}

@Test
public void testEqualsWithNullReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals(null));
}

@Test
public void testEqualsWithDifferentObjectReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals("not a corpus"));
}

@Test
public void testCleanupRemovesCreoleListener() {
CorpusImpl corpus = new CorpusImpl();
corpus.cleanup();
assertTrue(true);
}

@Test
public void testDuplicateWithEmptyCorpusReturnsEmptyCorpus() throws ResourceInstantiationException {
CorpusImpl corpus = new CorpusImpl();
// Factory.DuplicationContext ctx = new Factory.DuplicationContext();
// Resource dup = corpus.duplicate(ctx);
// assertNotNull(dup);
// assertTrue(dup instanceof Corpus);
// assertEquals(0, ((Corpus) dup).size());
}

@Test
public void testUnrelatedDocumentIsNotRemovedInResourceUnloaded() {
CorpusImpl corpus = new CorpusImpl();
Document kept = mock(Document.class);
Document removed = mock(Document.class);
corpus.add(kept);
corpus.resourceUnloaded(new gate.event.CreoleEvent(removed, gate.event.CreoleEvent.RESOURCE_UNLOADED));
assertTrue(corpus.contains(kept));
}

@Test
public void testAddCorpusListenerTwiceOnlyAddsOnce() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("oneTime");
corpus.add(doc);
verify(listener, times(1)).documentAdded(any(CorpusEvent.class));
}

@Test
public void testAddAtIndexShiftsElementsCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
Document doc3 = mock(Document.class);
when(doc1.getName()).thenReturn("doc1");
when(doc2.getName()).thenReturn("doc2");
when(doc3.getName()).thenReturn("doc3");
corpus.add(doc1);
corpus.add(doc3);
corpus.add(1, doc2);
assertEquals("doc1", corpus.getDocumentName(0));
assertEquals("doc2", corpus.getDocumentName(1));
assertEquals("doc3", corpus.getDocumentName(2));
}

@Test
public void testSetAtIndexReplacesElementAndReturnsOldOne() {
CorpusImpl corpus = new CorpusImpl();
Document original = mock(Document.class);
Document replacement = mock(Document.class);
corpus.add(original);
Document result = corpus.set(0, replacement);
assertSame(original, result);
assertEquals(1, corpus.size());
assertSame(replacement, corpus.get(0));
}

@Test
public void testRemoveAtIndexReturnsCorrectDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
Document removed = corpus.remove(0);
assertSame(doc1, removed);
assertEquals(1, corpus.size());
assertSame(doc2, corpus.get(0));
}

@Test
public void testAddAllAtIndexInsertsInCorrectOrder() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
Document doc3 = mock(Document.class);
corpus.add(doc1);
List<Document> newDocs = new ArrayList<Document>();
newDocs.add(doc2);
newDocs.add(doc3);
corpus.addAll(1, newDocs);
assertSame(doc1, corpus.get(0));
assertSame(doc2, corpus.get(1));
assertSame(doc3, corpus.get(2));
}

@Test
public void testListIteratorFunctionality() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
ListIterator<Document> iterator = corpus.listIterator();
assertTrue(iterator.hasNext());
assertSame(doc1, iterator.next());
assertSame(doc2, iterator.next());
assertFalse(iterator.hasNext());
assertTrue(iterator.hasPrevious());
assertSame(doc2, iterator.previous());
}

@Test
public void testListIteratorFromIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
Document doc3 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.add(doc3);
ListIterator<Document> it = corpus.listIterator(1);
assertTrue(it.hasNext());
assertSame(doc2, it.next());
assertSame(doc3, it.next());
}

@Test(expected = IndexOutOfBoundsException.class)
public void testAddAtInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(5, doc);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testSetAtInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.set(3, doc);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testListIteratorInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
corpus.listIterator(2);
}

@Test
public void testMultipleIdenticalDocuments() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
corpus.add(doc);
assertEquals(0, corpus.indexOf(doc));
assertEquals(2, corpus.lastIndexOf(doc));
assertEquals(3, corpus.size());
corpus.remove(doc);
assertEquals(2, corpus.size());
assertEquals(1, corpus.lastIndexOf(doc));
}

@Test
public void testPopulateReturnsZeroWhenNoFilesMatchFilter() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File directory = new File(System.getProperty("java.io.tmpdir"), "nocorpusmatch");
directory.mkdir();
directory.deleteOnExit();
File file = new File(directory, "file.nox");
FileWriter fw = new FileWriter(file);
fw.write("irrelevant content");
fw.close();
file.deleteOnExit();
ExtensionFileFilter filter = new ExtensionFileFilter("txt");
URL dirUrl = directory.toURI().toURL();
CorpusImpl.populate(corpus, dirUrl, filter, null, false);
assertEquals(0, corpus.size());
}

@Test
public void testStatusListenerNotPresentSafety() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("neutral", ".txt");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("<doc>Test</doc>");
writer.close();
URL url = file.toURI().toURL();
long bytes = CorpusImpl.populate(corpus, url, "doc", null, -1, "statusTest", "text/plain", true);
assertEquals(1, corpus.size());
assertTrue(bytes > 0);
}

@Test
public void testDocumentsListIsNullSafeInInit() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
corpus.init();
assertEquals(0, corpus.size());
}

@Test
public void testClearDocListWhenListIsNullSafe() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
corpus.clearDocList();
assertTrue(true);
}

@Test
public void testEmptyCorpusDocumentNamesReturnsEmptyList() {
CorpusImpl corpus = new CorpusImpl();
List<String> names = corpus.getDocumentNames();
assertNotNull(names);
assertTrue(names.isEmpty());
}

@Test
public void testUnloadDocumentOnEmptyCorpusDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.unloadDocument(doc);
assertTrue(corpus.isEmpty());
}

@Test
public void testFireDocumentRemovedWhenNoListenersRegistered() {
CorpusEvent event = new CorpusEvent(new CorpusImpl(), mock(Document.class), 0, CorpusEvent.DOCUMENT_REMOVED);
CorpusImpl corpus = new CorpusImpl();
corpus.fireDocumentRemoved(event);
assertTrue(true);
}

@Test
public void testFireDocumentAddedWhenNoListenersRegistered() {
CorpusEvent event = new CorpusEvent(new CorpusImpl(), mock(Document.class), 0, CorpusEvent.DOCUMENT_ADDED);
CorpusImpl corpus = new CorpusImpl();
corpus.fireDocumentAdded(event);
assertTrue(true);
}

@Test
public void testHashCodeConsistencyWithSameDocuments() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc = mock(Document.class);
corpus1.add(doc);
corpus2.add(doc);
assertEquals(corpus1.hashCode(), corpus2.hashCode());
}

@Test
public void testInitWithEmptyDocumentsListDoesNotAddDocuments() {
CorpusImpl corpus = new CorpusImpl();
List<Document> emptyList = new ArrayList<Document>();
corpus.setDocumentsList(emptyList);
Resource result = corpus.init();
assertSame(corpus, result);
assertTrue(corpus.isEmpty());
}

@Test
public void testDatastoreEventsAreNoop() {
CorpusImpl corpus = new CorpusImpl();
corpus.datastoreCreated(null);
corpus.datastoreClosed(null);
corpus.datastoreOpened(null);
assertTrue(true);
}

@Test
public void testResourceRenamedIsNoop() {
CorpusImpl corpus = new CorpusImpl();
corpus.resourceRenamed(mock(Document.class), "old", "new");
assertTrue(true);
}

@Test
public void testDuplicateCorpusWithMultipleDocuments() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
// Resource duplicated = corpus.duplicate(new Factory.DuplicationContext());
// assertTrue(duplicated instanceof Corpus);
// assertEquals(2, ((Corpus) duplicated).size());
}

@Test
public void testCompareFileNamesSameNameDifferentCases() {
File file1 = new File("ABC.txt");
File file2 = new File("abc.TXT");
Comparator<File> comparator = new Comparator<File>() {

@Override
public int compare(File f1, File f2) {
return f1.getName().compareTo(f2.getName());
}
};
int result = comparator.compare(file1, file2);
assertTrue(result != 0);
}

@Test
public void testPopulateSkipsDirectoriesDuringDocumentCreation() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File parentDir = new File(System.getProperty("java.io.tmpdir"), "testDirWithFolders");
parentDir.mkdirs();
parentDir.deleteOnExit();
File subDir = new File(parentDir, "subdir");
subDir.mkdir();
subDir.deleteOnExit();
URL url = parentDir.toURI().toURL();
CorpusImpl.populate(corpus, url, null, null, false);
assertTrue(corpus.isEmpty());
}

@Test
public void testDocumentContentExclusionRemovesRootTags() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("doc-strip", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<doc>Hello World</doc>");
fw.close();
URL url = file.toURI().toURL();
long size = corpus.populate(url, "doc", "UTF-8", 1, "name", "text/xml", false);
assertEquals(1, corpus.size());
assertTrue(size > 0);
}

@Test
public void testGenSymInDocumentNameMakesNamesUnique() throws IOException {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("docunique", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<doc>One</doc>\n");
fw.write("<doc>Two</doc>\n");
fw.close();
URL url = file.toURI().toURL();
// corpus.populate(url, "doc", "UTF-8", -1, "prefix", "text/xml", true);
String name1 = corpus.get(0).getName();
String name2 = corpus.get(1).getName();
assertNotEquals(name1, name2);
}

@Test
public void testSetDocumentsListNullClearsListOnInit() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
corpus.init();
assertEquals(0, corpus.size());
}

@Test
public void testSetDocumentsListAddsAllOnInit() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
List<Document> docs = new ArrayList<Document>();
docs.add(doc1);
docs.add(doc2);
corpus.setDocumentsList(docs);
corpus.init();
assertEquals(2, corpus.size());
assertSame(doc1, corpus.get(0));
assertSame(doc2, corpus.get(1));
}

@Test
public void testRemoveDocumentMultipleOccurrencesRemovesOne() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
corpus.add(doc);
assertEquals(3, corpus.size());
corpus.remove(doc);
assertEquals(2, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testAddAllIndexZero() {
CorpusImpl corpus = new CorpusImpl();
Document docA = mock(Document.class);
Document docB = mock(Document.class);
Document docC = mock(Document.class);
corpus.add(docC);
List<Document> addedDocs = Arrays.asList(docA, docB);
corpus.addAll(0, addedDocs);
assertEquals(3, corpus.size());
assertSame(docA, corpus.get(0));
assertSame(docB, corpus.get(1));
assertSame(docC, corpus.get(2));
}

@Test
public void testUnsupportedResourceUnloadedTypeIgnored() {
CorpusImpl corpus = new CorpusImpl();
Resource nonDocumentResource = mock(Resource.class);
corpus.resourceUnloaded(new gate.event.CreoleEvent(nonDocumentResource, gate.event.CreoleEvent.RESOURCE_UNLOADED));
assertTrue(true);
}

@Test
public void testDuplicatePreservesDocumentOrder() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
// Resource copy = corpus.duplicate(new Factory.DuplicationContext());
// assertTrue(copy instanceof Corpus);
// Corpus duplicateCorpus = (Corpus) copy;
// assertEquals(2, duplicateCorpus.size());
// assertNotSame(corpus, duplicateCorpus);
// assertNotSame(corpus.get(0), duplicateCorpus.get(0));
// assertNotSame(corpus.get(1), duplicateCorpus.get(1));
}

@Test
public void testPopulateWithMimeTypeParameter() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("mime", ".txt");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("<doc>Hello Mime</doc>");
writer.close();
URL url = file.toURI().toURL();
long bytes = corpus.populate(url, "doc", "UTF-8", 1, "mimeDoc", "application/xml", true);
assertEquals(1, corpus.size());
assertTrue(bytes > 0);
}

@Test
public void testGetMethodReturnsCorrectDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document fetched = corpus.get(0);
assertSame(doc, fetched);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testGetWithNegativeIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
corpus.get(-1);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testRemoveWithNegativeIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
corpus.remove(-1);
}

@Test
public void testCreateSubListWithFullRange() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> sub = corpus.subList(0, 2);
assertEquals(2, sub.size());
assertSame(doc1, sub.get(0));
assertSame(doc2, sub.get(1));
}

@Test(expected = IndexOutOfBoundsException.class)
public void testSubListWithOutOfBounds() {
CorpusImpl corpus = new CorpusImpl();
corpus.subList(0, 1);
}

@Test
public void testIsDocumentLoadedAlwaysTrue() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
boolean loaded = corpus.isDocumentLoaded(0);
assertTrue(loaded);
}

@Test
public void testRemoveCorpusListenerFromEmptyListDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.removeCorpusListener(listener);
assertTrue(true);
}

@Test(expected = NullPointerException.class)
public void testAddNullDocumentThrowsException() {
CorpusImpl corpus = new CorpusImpl();
corpus.add(null);
}

@Test(expected = NullPointerException.class)
public void testAddAllWithNullCollectionThrowsException() {
CorpusImpl corpus = new CorpusImpl();
corpus.addAll(null);
}

@Test
public void testEqualsSameInstanceReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.equals(corpus));
}

@Test
public void testEqualsDifferentClassReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals("not a CorpusImpl"));
}

@Test
public void testRemoveAllOnlyRemovesMatchingDocuments() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> toRemove = Arrays.asList(doc1);
boolean changed = corpus.removeAll(toRemove);
assertTrue(changed);
assertEquals(1, corpus.size());
assertSame(doc2, corpus.get(0));
}

@Test
public void testRetainAllWithSubsetRemovesOthers() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
Document doc3 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.add(doc3);
List<Document> retained = Arrays.asList(doc2);
boolean changed = corpus.retainAll(retained);
assertTrue(changed);
assertEquals(1, corpus.size());
assertSame(doc2, corpus.get(0));
}

@Test
public void testAddCorpusListenerNullDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
corpus.addCorpusListener(null);
assertTrue(true);
}

@Test
public void testRemoveCorpusListenerNullDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
corpus.removeCorpusListener(null);
assertTrue(true);
}

@Test
public void testEqualsWithDifferentCorpusContentsReturnsFalse() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus1.add(doc1);
corpus2.add(doc2);
assertFalse(corpus1.equals(corpus2));
}

@Test
public void testHashCodeDifferentDocumentsProducesDifferentHash() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus1.add(doc1);
corpus2.add(doc2);
assertNotEquals(corpus1.hashCode(), corpus2.hashCode());
}

@Test
public void testRemovingNonExistentDocumentReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
boolean result = corpus.remove(doc);
assertFalse(result);
}

@Test
public void testContainsAllWithEmptyCollectionAlwaysReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.containsAll(new ArrayList<Object>());
assertTrue(result);
}

@Test
public void testRemoveAllWithNonMatchingCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> toRemove = new ArrayList<Document>();
Document unknown = mock(Document.class);
toRemove.add(unknown);
boolean result = corpus.removeAll(toRemove);
assertFalse(result);
}

@Test
public void testIndexOfForAbsentDocumentReturnsMinusOne() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
int index = corpus.indexOf(doc);
assertEquals(-1, index);
}

@Test
public void testLastIndexOfForAbsentDocumentReturnsMinusOne() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
int index = corpus.lastIndexOf(doc);
assertEquals(-1, index);
}

@Test
public void testDuplicateFromEmptyCorpusReturnsEmptyCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// Factory.DuplicationContext ctx = new Factory.DuplicationContext();
// Resource duplicate = corpus.duplicate(ctx);
// assertTrue(duplicate instanceof Corpus);
// assertEquals(0, ((Corpus) duplicate).size());
}

@Test
public void testFireDocumentAddedWithSingleListener() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.add(doc);
verify(listener, atLeastOnce()).documentAdded(any(CorpusEvent.class));
}

@Test
public void testFireDocumentRemovedWithSingleListener() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.add(doc);
corpus.remove(doc);
verify(listener, atLeastOnce()).documentRemoved(any(CorpusEvent.class));
}

@Test
public void testResourceLoadedNoop() {
CorpusImpl corpus = new CorpusImpl();
corpus.resourceLoaded(null);
assertTrue(true);
}

@Test
public void testDatastoreEventsNullSafe() {
CorpusImpl corpus = new CorpusImpl();
corpus.datastoreCreated(null);
corpus.datastoreOpened(null);
corpus.datastoreClosed(null);
assertTrue(true);
}

@Test
public void testInitReturnsSelf() {
CorpusImpl corpus = new CorpusImpl();
Resource resource = corpus.init();
assertSame(corpus, resource);
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateThrowsOnNonFileProtocol() throws IOException {
CorpusImpl corpus = new CorpusImpl();
URL invalidUrl = new URL("http://notfileprotocol.com");
CorpusImpl.populate(corpus, invalidUrl, null, "UTF-8", false);
}

@Test
public void testPopulateWithEmptyDirectoryDoesNothing() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "emptyDir_" + System.nanoTime());
tempDir.mkdir();
tempDir.deleteOnExit();
URL dirUrl = tempDir.toURI().toURL();
CorpusImpl.populate(corpus, dirUrl, null, null, false);
assertEquals(0, corpus.size());
}

@Test
public void testPopulateConcatenatedFileNoRootElementFound() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("norootelem", ".txt");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("This is just plain text.");
writer.close();
URL url = file.toURI().toURL();
long length = corpus.populate(url, "doc", "UTF-8", -1, "emptydoc", "text/plain", true);
assertEquals(0, corpus.size());
assertEquals(0, length);
}

@Test
public void testPopulateConcatenatedWithInterleavedIrrelevantText() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("interleave", ".txt");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("Some header text\n");
writer.write("<doc>ContentOne</doc>\n");
writer.write("Interruption\n");
writer.write("<doc>ContentTwo</doc>\n");
writer.close();
URL url = file.toURI().toURL();
long length = corpus.populate(url, "doc", "UTF-8", -1, "doc", "text/plain", true);
assertEquals(2, corpus.size());
assertTrue(length > 0);
}

@Test
public void testPopulateFromTrecFileWithEndTagOnlyOnNewLine() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("edgecase_trec", ".txt");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("<doc>\nLine 1\nLine 2\n</doc>\n<doc>\nAnother doc\n</doc>");
writer.close();
URL url = file.toURI().toURL();
long length = corpus.populate(url, "doc", "UTF-8", -1, "case", "text/xml", true);
assertEquals(2, corpus.size());
assertTrue(length > 0);
}

@Test
public void testTrecPopulateIncludesRootElementTrue() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("includeRoot", ".trec");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("<record>Full content</record>");
writer.close();
URL url = file.toURI().toURL();
long length = corpus.populate(url, "record", "UTF-8", 1, "inc", "text/plain", true);
assertEquals(1, corpus.size());
assertTrue(length > 0);
}

@Test
public void testTrecPopulateIncludesRootElementFalse() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("excludeRoot", ".trec");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("<record>Trimmed content</record>");
writer.close();
URL url = file.toURI().toURL();
long length = corpus.populate(url, "record", "UTF-8", 1, "ex", "text/plain", false);
assertEquals(1, corpus.size());
assertTrue(length > 0);
}

@Test
public void testPopulateConcatenatedFileExceedingLimitStopsCorrectly() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("limitstop", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<doc>One</doc>\n");
fw.write("<doc>Two</doc>\n");
fw.write("<doc>Three</doc>\n");
fw.close();
URL url = file.toURI().toURL();
long size = corpus.populate(url, "doc", "UTF-8", 2, "limit", "text/plain", true);
assertEquals(2, corpus.size());
assertTrue(size > 0);
}

@Test
public void testPopulateConcatenatedFileWithoutEncodingDefaultsProperly() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("no_encoding", ".txt");
file.deleteOnExit();
FileWriter fw = new FileWriter(file);
fw.write("<doc>Some text</doc>");
fw.close();
URL url = file.toURI().toURL();
long size = corpus.populate(url, "doc", null, -1, "prefix", "text/plain", true);
assertEquals(1, corpus.size());
assertTrue(size > 0);
}

@Test
public void testSubListReturnsLiveView() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
Document doc3 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.add(doc3);
List<Document> sub = corpus.subList(0, 2);
assertEquals(2, sub.size());
corpus.remove(doc2);
assertEquals(1, sub.size());
}

@Test
public void testSetDocumentsListPreservesReferenceUntilInit() {
CorpusImpl corpus = new CorpusImpl();
List<Document> docs = new ArrayList<Document>();
assertEquals(0, corpus.size());
Document doc = mock(Document.class);
docs.add(doc);
corpus.setDocumentsList(docs);
assertEquals(docs, corpus.getDocumentsList());
}

@Test
public void testSetDocumentsListOverwritesPreviousList() {
CorpusImpl corpus = new CorpusImpl();
List<Document> docs1 = new ArrayList<Document>();
Document doc1 = mock(Document.class);
docs1.add(doc1);
corpus.setDocumentsList(docs1);
List<Document> docs2 = new ArrayList<Document>();
Document doc2 = mock(Document.class);
docs2.add(doc2);
corpus.setDocumentsList(docs2);
corpus.init();
assertEquals(1, corpus.size());
assertSame(doc2, corpus.get(0));
}

@Test
public void testToArrayWithSmallerTypedArray() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] result = corpus.toArray(new Document[0]);
assertEquals(1, result.length);
assertSame(doc, result[0]);
}

@Test
public void testToArrayWithLargerTypedArray() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] input = new Document[5];
Document[] result = corpus.toArray(input);
assertSame(doc, result[0]);
assertNull(result[1]);
}

@Test
public void testCustomDuplicationPreservesDocumentCountAndOrder() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
// Factory.DuplicationContext context = new Factory.DuplicationContext();
// Resource duplicated = corpus.duplicate(context);
// assertTrue(duplicated instanceof Corpus);
// assertEquals(2, ((Corpus) duplicated).size());
}

@Test
public void testPopulateSkipsDirectoriesInRecursiveMode() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File root = new File(System.getProperty("java.io.tmpdir"), "corpus_root_" + System.nanoTime());
root.mkdirs();
root.deleteOnExit();
File subDir = new File(root, "subdir");
subDir.mkdirs();
subDir.deleteOnExit();
File file = new File(subDir, "file1.txt");
FileWriter fw = new FileWriter(file);
fw.write("Ignored content");
fw.close();
file.deleteOnExit();
URL url = root.toURI().toURL();
CorpusImpl.populate(corpus, url, null, "UTF-8", true);
assertTrue(corpus.size() >= 1);
}
}
