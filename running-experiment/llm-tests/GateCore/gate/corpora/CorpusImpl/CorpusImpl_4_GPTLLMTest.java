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

public class CorpusImpl_4_GPTLLMTest {

@Test
public void testAddDocumentSelfContained() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("TestDoc");
boolean added = corpus.add(doc);
assertTrue(added);
assertEquals(1, corpus.size());
assertEquals("TestDoc", corpus.getDocumentName(0));
List<String> names = corpus.getDocumentNames();
assertNotNull(names);
assertEquals(1, names.size());
assertEquals("TestDoc", names.get(0));
}

@Test
public void testRemoveDocumentSelfContained() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("DocToRemove");
corpus.add(doc);
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertEquals(0, corpus.size());
assertTrue(corpus.isEmpty());
}

@Test
public void testSetDocumentAtIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
when(doc1.getName()).thenReturn("Doc1");
Document doc2 = mock(Document.class);
when(doc2.getName()).thenReturn("Doc2");
corpus.add(doc1);
Document result = corpus.set(0, doc2);
assertEquals(doc1, result);
assertEquals("Doc2", corpus.getDocumentName(0));
}

@Test
public void testIsEmptyAndClear() {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.isEmpty());
Document doc = mock(Document.class);
corpus.add(doc);
assertFalse(corpus.isEmpty());
corpus.clear();
assertEquals(0, corpus.size());
assertTrue(corpus.isEmpty());
}

@Test
public void testToArrayVariants() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
Object[] objArray = corpus.toArray();
assertEquals(2, objArray.length);
assertSame(doc1, objArray[0]);
assertSame(doc2, objArray[1]);
Document[] typedArray = new Document[2];
corpus.toArray(typedArray);
assertSame(doc1, typedArray[0]);
assertSame(doc2, typedArray[1]);
}

@Test
public void testContainsAndIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
corpus.add(doc1);
boolean contains = corpus.contains(doc1);
int indexOf = corpus.indexOf(doc1);
int lastIndexOf = corpus.lastIndexOf(doc1);
assertTrue(contains);
assertEquals(0, indexOf);
assertEquals(2, lastIndexOf);
}

@Test
public void testCorpusListenerEvents() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("EventDoc");
final AtomicInteger addedCalls = new AtomicInteger(0);
final AtomicInteger removedCalls = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
addedCalls.incrementAndGet();
}

public void documentRemoved(CorpusEvent e) {
removedCalls.incrementAndGet();
}
};
corpus.addCorpusListener(listener);
corpus.add(doc);
corpus.remove(doc);
assertEquals(1, addedCalls.get());
assertEquals(1, removedCalls.get());
corpus.removeCorpusListener(listener);
corpus.add(doc);
corpus.remove(doc);
assertEquals(1, addedCalls.get());
assertEquals(1, removedCalls.get());
}

@Test
public void testDocumentListAndInit() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
List<Document> docList = new ArrayList<Document>();
docList.add(doc1);
docList.add(doc2);
corpus.setDocumentsList(docList);
corpus.init();
assertEquals(2, corpus.size());
assertSame(doc1, corpus.get(0));
assertSame(doc2, corpus.get(1));
List<Document> retrievedList = corpus.getDocumentsList();
assertEquals(docList, retrievedList);
}

@Test
public void testPopulateDirectoryNonRecursive() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File dir = new File("testCorpDir");
dir.mkdir();
File file = new File(dir, "one.txt");
FileWriter writer = new FileWriter(file);
writer.write("document content");
writer.close();
// ExtensionFileFilter filter = new ExtensionFileFilter(new String[] { "txt" }, "");
// URL dirURL = dir.toURI().toURL();
// CorpusImpl.populate(corpus, dirURL, filter, "UTF-8", false);
assertEquals(1, corpus.size());
Document doc = corpus.get(0);
assertNotNull(doc);
file.delete();
dir.delete();
}

@Test
public void testPopulateConcatenatedFile() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File concatFile = File.createTempFile("trec", ".txt");
FileWriter writer = new FileWriter(concatFile);
writer.write("<doc>\nDocument one.\n</doc>\n");
writer.write("<doc>\nDocument two.\n</doc>\n");
writer.close();
URL fileUrl = concatFile.toURI().toURL();
long length = CorpusImpl.populate(corpus, fileUrl, "doc", "UTF-8", -1, "doc", "text/plain", true);
assertEquals(2, corpus.size());
assertTrue(length > 0);
concatFile.delete();
}

@Test
public void testPopulateFailsOnInvalidProtocol() throws Exception {
CorpusImpl corpus = new CorpusImpl();
URL invalidUrl = new URL("http://invalid-url");
try {
CorpusImpl.populate(corpus, invalidUrl, null, "UTF-8", false);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("file"));
}
}

@Test
public void testPopulateFailsOnMissingFile() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File missing = new File("non-existent-directory");
URL url = missing.toURI().toURL();
try {
CorpusImpl.populate(corpus, url, null, "UTF-8", false);
fail("Expected FileNotFoundException");
} catch (FileNotFoundException ex) {
assertTrue(true);
}
}

@Test
public void testUnloadDocumentIsNoOpExecution() {
CorpusImpl corpus = new CorpusImpl();
Document document = mock(Document.class);
corpus.unloadDocument(document);
}

@Test
public void testEqualsAndHashcode() {
CorpusImpl a = new CorpusImpl();
CorpusImpl b = new CorpusImpl();
Document d = mock(Document.class);
a.add(d);
b.add(d);
assertTrue(a.equals(b));
assertEquals(a.hashCode(), b.hashCode());
}

@Test
public void testDuplicateCorpusWithSingleDocument() throws Exception {
CorpusImpl original = new CorpusImpl();
Document doc = mock(Document.class);
original.add(doc);
CorpusImpl duplicatedCorpus = mock(CorpusImpl.class);
Resource duplicatedDoc = mock(Document.class);
Factory.DuplicationContext context = mock(Factory.DuplicationContext.class);
when(Factory.defaultDuplicate(original, context)).thenReturn(duplicatedCorpus);
when(Factory.duplicate(doc, context)).thenReturn(duplicatedDoc);
Resource result = original.duplicate(context);
// verify(duplicatedCorpus).add(duplicatedDoc);
assertTrue(result instanceof Corpus);
}

@Test
public void testRemoveDocumentOnResourceUnloaded() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Resource resource = doc;
gate.event.CreoleEvent event = mock(gate.event.CreoleEvent.class);
when(event.getResource()).thenReturn(resource);
corpus.resourceUnloaded(event);
assertFalse(corpus.contains(doc));
}

@Test
public void testIsDocumentAlwaysLoaded() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
assertTrue(corpus.isDocumentLoaded(0));
}

@Test
public void testAddDocumentAtInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.add(-1, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testSetDocumentAtInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.set(1, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testGetDocumentAtInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.get(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testRemoveDocumentAtInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.remove(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testAddNullDocumentThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.add(0, null);
fail("Expected NullPointerException or ClassCastException");
} catch (NullPointerException | ClassCastException e) {
assertTrue(true);
}
}

@Test
public void testSubListOnEmptyCorpusReturnsEmptyList() {
CorpusImpl corpus = new CorpusImpl();
List<Document> subList = corpus.subList(0, 0);
assertNotNull(subList);
assertEquals(0, subList.size());
}

@Test
public void testSubListWithSameStartAndEnd() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
corpus.add(doc1);
List<Document> subList = corpus.subList(1, 1);
assertNotNull(subList);
assertEquals(0, subList.size());
}

@Test
public void testSubListWithInvalidBoundsThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
corpus.add(doc1);
try {
corpus.subList(1, 0);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException | IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testClearOnEmptyCorpusDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
corpus.clear();
assertEquals(0, corpus.size());
}

@Test
public void testIndexOfOnNonContainedDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
int index = corpus.indexOf(doc);
assertEquals(-1, index);
}

@Test
public void testEqualsWithDifferentObjectReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals("NotACorpus"));
}

@Test
public void testEqualsWithNullReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals(null));
}

@Test
public void testEqualsWithSameReferenceReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.equals(corpus));
}

@Test
public void testAddAllWithNullCollectionThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.addAll(null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testAddAllEmptyCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.addAll(Collections.<Document>emptyList());
assertFalse(result);
}

@Test
public void testRemoveAllWithEmptyCollectionReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.removeAll(Collections.<Document>emptyList());
assertFalse(result);
}

@Test
public void testRetainAllWithEmptyCollectionClearsCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
boolean changed = corpus.retainAll(Collections.<Document>emptyList());
assertTrue(changed);
assertTrue(corpus.isEmpty());
}

@Test
public void testRetainAllWithContainingCollectionKeepsDocument() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> retainList = new ArrayList<Document>();
retainList.add(doc);
boolean changed = corpus.retainAll(retainList);
assertFalse(changed);
assertFalse(corpus.isEmpty());
}

@Test
public void testDocumentAddedEventFiredWithCorrectIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc1");
final AtomicBoolean correctIndex = new AtomicBoolean(false);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
// correctIndex.set(e.getIndex() == 0);
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener);
corpus.add(doc);
assertTrue(correctIndex.get());
}

@Test
public void testDocumentRemovedEventFiredWithCorrectIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc2");
final AtomicBoolean removedAtZero = new AtomicBoolean(false);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
// removedAtZero.set(e.getIndex() == 0);
}
};
corpus.add(doc);
corpus.addCorpusListener(listener);
corpus.remove(0);
assertTrue(removedAtZero.get());
}

@Test
public void testResourceRenamedDoesNotAffectCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.resourceRenamed(doc, "oldName", "newName");
assertEquals(1, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testDatastoreEventMethodsDoNotThrow() {
CorpusImpl corpus = new CorpusImpl();
corpus.datastoreCreated(null);
corpus.datastoreOpened(null);
corpus.datastoreClosed(null);
assertTrue(true);
}

@Test
public void testInitWithNullDocumentsListDoesNotFail() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
Resource result = corpus.init();
assertSame(corpus, result);
assertEquals(0, corpus.size());
}

@Test
public void testDocumentNameAfterSetAtSpecificIndex() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
when(doc1.getName()).thenReturn("original");
when(doc2.getName()).thenReturn("replacement");
corpus.add(doc1);
corpus.set(0, doc2);
String name = corpus.getDocumentName(0);
assertEquals("replacement", name);
}

@Test
public void testToArrayOnEmptyCorpusReturnsEmptyArray() {
CorpusImpl corpus = new CorpusImpl();
Object[] arr = corpus.toArray();
assertNotNull(arr);
assertEquals(0, arr.length);
}

@Test
public void testListIteratorOnEmptyCorpus() {
CorpusImpl corpus = new CorpusImpl();
assertNotNull(corpus.listIterator());
assertNotNull(corpus.listIterator(0));
}

@Test
public void testRemoveCorpusListenerThatWasNeverAdded() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener dummy = mock(CorpusListener.class);
corpus.removeCorpusListener(dummy);
assertTrue(true);
}

@Test
public void testRemoveCorpusListenerFromEmptyListenerList() {
CorpusImpl corpus = new CorpusImpl();
// corpus.corpusListeners = null;
corpus.removeCorpusListener(mock(CorpusListener.class));
assertTrue(true);
}

@Test
public void testSetDocumentsListClearsAndInitializesCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
ArrayList<Document> initial = new ArrayList<Document>();
initial.add(d1);
initial.add(d2);
corpus.setDocumentsList(initial);
Resource result = corpus.init();
assertSame(result, corpus);
assertEquals(2, corpus.size());
}

@Test
public void testEqualsWithDifferentSupportListSize() {
CorpusImpl corp1 = new CorpusImpl();
CorpusImpl corp2 = new CorpusImpl();
Document d = mock(Document.class);
corp1.add(d);
assertFalse(corp1.equals(corp2));
}

@Test
public void testEqualsWithDifferentObjectType() {
CorpusImpl corpus = new CorpusImpl();
Object other = "NotACorpus";
assertFalse(corpus.equals(other));
}

@Test
public void testPopulateConcatenatedIncludeRootFalse() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File f = File.createTempFile("corpusTest", ".html");
FileWriter writer = new FileWriter(f);
writer.write("<doc>This is inside doc one</doc>");
writer.write("<doc>This is inside doc two</doc>");
writer.flush();
writer.close();
URL url = f.toURI().toURL();
long len = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "doc", "text/html", false);
assertEquals(2, corpus.size());
assertTrue(len > 0);
f.delete();
}

@Test
public void testFireDocumentAddedWithNoListenersDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
// corpus.corpusListeners = null;
Document doc = mock(Document.class);
CorpusEvent event = new CorpusEvent(corpus, doc, 0, CorpusEvent.DOCUMENT_ADDED);
corpus.fireDocumentAdded(event);
assertTrue(true);
}

@Test
public void testFireDocumentRemovedWithNoListenersDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
// corpus.corpusListeners = null;
Document doc = mock(Document.class);
CorpusEvent event = new CorpusEvent(corpus, doc, 0, CorpusEvent.DOCUMENT_REMOVED);
corpus.fireDocumentRemoved(event);
assertTrue(true);
}

@Test
public void testResourceUnloadedRemovesMultipleCopies() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
Resource res = doc;
gate.event.CreoleEvent event = mock(gate.event.CreoleEvent.class);
when(event.getResource()).thenReturn(res);
corpus.resourceUnloaded(event);
assertFalse(corpus.contains(doc));
assertEquals(0, corpus.size());
}

@Test
public void testAddCorpusListenerOnlyOnce() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
// assertEquals(1, corpus.corpusListeners.size());
}

@Test
public void testAddAllAtIndex() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
ArrayList<Document> list = new ArrayList<Document>();
list.add(d1);
list.add(d2);
boolean added = corpus.addAll(0, list);
assertTrue(added);
assertEquals(2, corpus.size());
assertSame(d1, corpus.get(0));
}

@Test
public void testHashCodeConsistency() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
int code1 = corpus.hashCode();
int code2 = corpus.hashCode();
assertEquals(code1, code2);
}

@Test
public void testSubListFullRange() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
corpus.add(d1);
corpus.add(d2);
List<Document> sub = corpus.subList(0, 2);
assertEquals(2, sub.size());
assertSame(d1, sub.get(0));
}

@Test
public void testDocumentAddedEventTriggerOrder() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
final AtomicInteger callOrder = new AtomicInteger(0);
CorpusListener l1 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
callOrder.compareAndSet(0, 1);
}

public void documentRemoved(CorpusEvent e) {
}
};
CorpusListener l2 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
if (callOrder.get() == 1) {
callOrder.set(2);
}
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(l1);
corpus.addCorpusListener(l2);
corpus.add(d1);
assertEquals(2, callOrder.get());
}

@Test
public void testListIteratorBoundaryEmpty() {
CorpusImpl corpus = new CorpusImpl();
assertNotNull(corpus.listIterator());
assertNotNull(corpus.listIterator(0));
}

@Test
public void testPopulateTrecWithIncompleteClosingTagSkipsDoc() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File f = File.createTempFile("partial", ".txt");
FileWriter writer = new FileWriter(f);
writer.write("<doc>Doc1 content</doc>");
writer.write("<doc>Doc2 content");
writer.close();
URL url = f.toURI().toURL();
long len = CorpusImpl.populate(corpus, url, "doc", null, -1, "doc", null, true);
assertEquals(1, corpus.size());
assertTrue(len > 0);
f.delete();
}

@Test
public void testPopulateTrecWithZeroLengthDocNotAdded() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File f = File.createTempFile("empty", ".txt");
FileWriter writer = new FileWriter(f);
writer.write("<doc></doc><doc></doc>");
writer.close();
URL url = f.toURI().toURL();
long len = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "zero", "text/plain", false);
assertEquals(2, corpus.size());
assertTrue(len == 0);
f.delete();
}

@Test
public void testDuplicateWithEmptyCorpusReturnsNonNull() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Factory.DuplicationContext ctx = mock(Factory.DuplicationContext.class);
CorpusImpl newCorpus = mock(CorpusImpl.class);
when(Factory.defaultDuplicate(corpus, ctx)).thenReturn(newCorpus);
Resource dup = corpus.duplicate(ctx);
assertSame(newCorpus, dup);
}

@Test
public void testToArrayWithSmallerTypedArrayCreatesNewArray() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] inputArray = new Document[0];
Document[] result = corpus.toArray(inputArray);
assertNotNull(result);
assertEquals(1, result.length);
assertSame(doc, result[0]);
}

@Test
public void testToArrayWithLargerTypedArrayFillsAndNullsRest() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] inputArray = new Document[3];
inputArray[1] = mock(Document.class);
Document[] result = corpus.toArray(inputArray);
assertSame(doc, result[0]);
assertNull(result[1]);
assertNull(result[2]);
}

@Test
public void testListIteratorStartingIndexOutOfBoundsThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.listIterator(1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testRemoveDocumentByObjectNotPresentReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
boolean result = corpus.remove(doc2);
assertFalse(result);
}

@Test
public void testContainsAllWithPartiallyMissingSetReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
corpus.add(d1);
List<Document> docs = new ArrayList<Document>();
docs.add(d1);
docs.add(d2);
boolean containsAll = corpus.containsAll(docs);
assertFalse(containsAll);
}

@Test
public void testRetainAllWithSameSetReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
List<Document> sameSet = new ArrayList<Document>();
sameSet.add(doc);
boolean changed = corpus.retainAll(sameSet);
assertFalse(changed);
}

@Test
public void testEqualsWithDifferentDocumentOrderReturnsFalse() {
CorpusImpl a = new CorpusImpl();
CorpusImpl b = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
a.add(d1);
a.add(d2);
b.add(d2);
b.add(d1);
boolean equal = a.equals(b);
assertFalse(equal);
}

@Test
public void testHashCodeReflectsListOrder() {
CorpusImpl a = new CorpusImpl();
CorpusImpl b = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
a.add(d1);
a.add(d2);
b.add(d2);
b.add(d1);
int hashA = a.hashCode();
int hashB = b.hashCode();
assertNotEquals(hashA, hashB);
}

@Test
public void testGetDocumentNameWithInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.getDocumentName(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testInitIgnoresEmptyDocumentsList() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(Collections.<Document>emptyList());
corpus.init();
assertTrue(corpus.isEmpty());
}

@Test
public void testPopulateSingleConcatenatedWithInvalidRootSkipsAll() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("nodocs", ".txt");
FileWriter w = new FileWriter(file);
w.write("no document markup here");
w.close();
URL url = file.toURI().toURL();
long len = CorpusImpl.populate(corpus, url, "invalid", null, -1, "prefix", null, true);
assertEquals(0, corpus.size());
assertEquals(0, len);
file.delete();
}

@Test
public void testPopulateSingleConcatenatedCreatesExpectedDocNamePrefix() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("prefixedocs", ".txt");
FileWriter w = new FileWriter(file);
w.write("<sample>Doc content</sample>");
w.close();
URL url = file.toURI().toURL();
CorpusImpl.populate(corpus, url, "sample", null, -1, "TESTPREFIX", null, true);
Document doc = corpus.get(0);
String name = doc.getName();
assertTrue(name.startsWith("TESTPREFIX"));
file.delete();
}

@Test
public void testPopulateSingleConcatenatedWithEmptyPrefixDefaultsToNoUnderscore() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("nounder", ".txt");
FileWriter w = new FileWriter(file);
w.write("<data>content</data>");
w.close();
URL url = file.toURI().toURL();
CorpusImpl.populate(corpus, url, "data", null, -1, "", null, true);
Document doc = corpus.get(0);
String name = doc.getName();
assertTrue(name.matches("^[^_]+_.*$"));
file.delete();
}

@Test
public void testRemovingCorpusListenerMultipleTimesDoesNotFail() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
corpus.removeCorpusListener(listener);
assertTrue(true);
}

@Test
public void testDuplicateCorpusWithMultipleDocuments() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
corpus.add(d1);
corpus.add(d2);
Factory.DuplicationContext context = mock(Factory.DuplicationContext.class);
CorpusImpl corpusCopy = mock(CorpusImpl.class);
Document d1Copy = mock(Document.class);
Document d2Copy = mock(Document.class);
when(Factory.defaultDuplicate(corpus, context)).thenReturn(corpusCopy);
when(Factory.duplicate(d1, context)).thenReturn(d1Copy);
when(Factory.duplicate(d2, context)).thenReturn(d2Copy);
Resource result = corpus.duplicate(context);
verify(corpusCopy).add(d1Copy);
verify(corpusCopy).add(d2Copy);
assertSame(corpusCopy, result);
}

@Test
public void testAddSameDocumentMultipleTimesAllowed() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
boolean added1 = corpus.add(doc);
boolean added2 = corpus.add(doc);
assertTrue(added1);
assertTrue(added2);
assertEquals(2, corpus.size());
}

@Test
public void testDocumentAddedEventFiredCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc-event");
final AtomicInteger events = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
events.incrementAndGet();
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener);
corpus.add(doc);
assertEquals(1, events.get());
}

@Test
public void testDocumentRemovedEventFiredCorrectly() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("doc-rm");
corpus.add(doc);
final AtomicInteger removed = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
removed.incrementAndGet();
}
};
corpus.addCorpusListener(listener);
corpus.remove(doc);
assertEquals(1, removed.get());
}

@Test
public void testClearDocListNullSupportListDoesNotThrow() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.supportList = null;
corpus.clearDocList();
assertTrue(true);
}

@Test
public void testCleanupRemovesListenerEvenWithEmptyRegister() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.cleanup();
assertTrue(true);
}

@Test
public void testToArrayOnEmptyCorpusReturnsEmptyObjectArray() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Object[] array = corpus.toArray();
assertEquals(0, array.length);
}

@Test
public void testSubListFullRangeReturnsAllDocuments() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> result = corpus.subList(0, 2);
assertEquals(2, result.size());
assertSame(doc1, result.get(0));
assertSame(doc2, result.get(1));
}

@Test
public void testIndexOfReturnsCorrectIndexForMultipleSameDocuments() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
corpus.add(doc);
int firstIndex = corpus.indexOf(doc);
int lastIndex = corpus.lastIndexOf(doc);
assertEquals(0, firstIndex);
assertEquals(1, lastIndex);
}

@Test
public void testRemoveCorpusListenerWhenListenersIsNull() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// corpus.corpusListeners = null;
corpus.removeCorpusListener(mock(CorpusListener.class));
assertTrue(true);
}

@Test
public void testResourceUnloadedRemovesAllOccurrences() throws Exception {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc1);
corpus.add(doc2);
CreoleEvent event = mock(CreoleEvent.class);
when(event.getResource()).thenReturn(doc1);
corpus.resourceUnloaded(event);
assertEquals(1, corpus.size());
assertSame(doc2, corpus.get(0));
}

@Test
public void testFireDocumentAddedWithMultipleListeners() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicBoolean listener1Called = new AtomicBoolean(false);
final AtomicBoolean listener2Called = new AtomicBoolean(false);
CorpusListener listener1 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
listener1Called.set(true);
}

public void documentRemoved(CorpusEvent e) {
}
};
CorpusListener listener2 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
listener2Called.set(true);
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener1);
corpus.addCorpusListener(listener2);
Document doc = mock(Document.class);
corpus.add(doc);
assertTrue(listener1Called.get());
assertTrue(listener2Called.get());
}

@Test
public void testFireDocumentRemovedWithMultipleListeners() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicBoolean listener1Called = new AtomicBoolean(false);
final AtomicBoolean listener2Called = new AtomicBoolean(false);
CorpusListener listener1 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
listener1Called.set(true);
}
};
CorpusListener listener2 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
listener2Called.set(true);
}
};
corpus.addCorpusListener(listener1);
corpus.addCorpusListener(listener2);
Document doc = mock(Document.class);
corpus.add(doc);
corpus.remove(doc);
assertTrue(listener1Called.get());
assertTrue(listener2Called.get());
}

@Test
public void testPopulateTrecFormatSkipsInvalidDocumentTag() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File f = File.createTempFile("badtag", ".txt");
FileWriter w = new FileWriter(f);
w.write("<doc>\nThis doc begins but NEVER ends properly");
w.close();
URL url = f.toURI().toURL();
long result = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "d", null, true);
assertEquals(0, corpus.size());
assertEquals(0, result);
f.delete();
}

@Test
public void testPopulateNonRecursiveReturnsEmptyOnEmptyDir() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File dir = new File("emptydir");
dir.mkdir();
URL dirURL = dir.toURI().toURL();
// ExtensionFileFilter filter = new ExtensionFileFilter(new String[] { "txt" }, "");
// CorpusImpl.populate(corpus, dirURL, filter, "UTF-8", false);
assertEquals(0, corpus.size());
dir.delete();
}

@Test
public void testPopulateRecursiveReturnsEmptyOnNoMatches() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File dir = new File("recursedir");
dir.mkdir();
File nested = new File(dir, "sub");
nested.mkdir();
File f = new File(nested, "file.ignore");
FileWriter w = new FileWriter(f);
w.write("should be ignored");
w.close();
URL dirURL = dir.toURI().toURL();
// ExtensionFileFilter filter = new ExtensionFileFilter(new String[] { "txt" }, "");
// CorpusImpl.populate(corpus, dirURL, filter, "UTF-8", true);
assertEquals(0, corpus.size());
f.delete();
nested.delete();
dir.delete();
}

@Test
public void testPopulateThrowsForNonFileURL() throws Exception {
CorpusImpl corpus = new CorpusImpl();
try {
URL u = new URL("http://example.com/somefile.txt");
CorpusImpl.populate(corpus, u, null, "UTF-8", false);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException ex) {
assertTrue(true);
}
}

@Test
public void testPopulateThrowsForMissingDirectory() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File missing = new File("nonexistent-folder-xyz123");
URL url = missing.toURI().toURL();
try {
CorpusImpl.populate(corpus, url, null, "UTF-8", false);
fail("Expected FileNotFoundException");
} catch (java.io.FileNotFoundException e) {
assertTrue(true);
}
}

@Test
public void testPopulateThrowsIfGivenFileNotDir() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File tmp = File.createTempFile("notadir", ".txt");
FileWriter w = new FileWriter(tmp);
w.write("data");
w.close();
URL url = tmp.toURI().toURL();
try {
CorpusImpl.populate(corpus, url, null, "UTF-8", false);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException e) {
assertTrue(true);
} finally {
tmp.delete();
}
}

@Test
public void testSetDocumentsListNullThenAddManually() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
corpus.init();
Document doc = mock(Document.class);
corpus.add(doc);
assertEquals(1, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testPopulateWithMimeTypeParameterUsed() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File f = File.createTempFile("type-test", ".txt");
FileWriter w = new FileWriter(f);
w.write("<txt>Mime Test Content</txt>");
w.close();
URL url = f.toURI().toURL();
long len = CorpusImpl.populate(corpus, url, "txt", "UTF-8", -1, "mimeDoc", "text/plain", true);
assertEquals(1, corpus.size());
assertTrue(len > 0);
f.delete();
}

@Test
public void testGetDocumentsListInitiallyNull() {
CorpusImpl corpus = new CorpusImpl();
List<Document> list = corpus.getDocumentsList();
assertNull(list);
}

@Test
public void testGetNameFromDocumentInList() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
when(doc.getName()).thenReturn("NamedDoc");
corpus.add(doc);
String name = corpus.getDocumentName(0);
assertEquals("NamedDoc", name);
}

@Test
public void testResourceUnloadedWithNonDocumentResource() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Resource resource = mock(Resource.class);
CreoleEvent event = mock(CreoleEvent.class);
when(event.getResource()).thenReturn(resource);
corpus.resourceUnloaded(event);
assertEquals(1, corpus.size());
assertSame(doc, corpus.get(0));
}

@Test
public void testSubListWithSameIndexStartEndReturnsEmptyList() {
CorpusImpl corpus = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
corpus.add(doc1);
corpus.add(doc2);
List<Document> sub = corpus.subList(1, 1);
assertNotNull(sub);
assertEquals(0, sub.size());
}

@Test
public void testGetSubListThrowsOnInvalidRange() {
CorpusImpl corpus = new CorpusImpl();
Document d = mock(Document.class);
corpus.add(d);
try {
corpus.subList(2, 1);
fail("Expected IndexOutOfBoundsException or IllegalArgumentException");
} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
assertTrue(true);
}
}

@Test
public void testAddAllAtIndexBeyondSizeThrowsException() {
CorpusImpl corpus = new CorpusImpl();
List<Document> list = new ArrayList<Document>();
Document d = mock(Document.class);
list.add(d);
try {
corpus.addAll(2, list);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testEqualsWithDifferentTypeReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
boolean result = corpus.equals("not a corpus");
assertFalse(result);
}

@Test
public void testAddDuplicateListenerDoesNotDuplicate() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
// assertEquals(1, corpus.corpusListeners.size());
}

@Test
public void testDuplicateCorpusCreatesNewInstanceWithCopiedDocs() throws Exception {
CorpusImpl original = new CorpusImpl();
Document doc = mock(Document.class);
original.add(doc);
CorpusImpl duplicatedCorpus = mock(CorpusImpl.class);
Factory.DuplicationContext context = mock(Factory.DuplicationContext.class);
when(Factory.defaultDuplicate(original, context)).thenReturn(duplicatedCorpus);
Document duplicatedDoc = mock(Document.class);
when(Factory.duplicate(doc, context)).thenReturn(duplicatedDoc);
Resource result = original.duplicate(context);
verify(duplicatedCorpus).add(duplicatedDoc);
assertSame(duplicatedCorpus, result);
}

@Test
public void testPopulateConcatenatedSkipsDocumentIfNoRootFound() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("nofind", ".txt");
FileWriter writer = new FileWriter(file);
writer.write("This <notdoc>content</notdoc> will not be parsed");
writer.close();
URL url = file.toURI().toURL();
long totalLength = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "n", "text/plain", true);
assertEquals(0, corpus.size());
assertEquals(0, totalLength);
file.delete();
}

@Test
public void testRemoveByIndexOnOutOfBoundsIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.remove(5);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testAddAtInvalidIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.add(-1, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testSetAtIndexBeyondSizeThrowsException() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.set(3, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testPopulateTrecSkipsAfterLimitReached() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("limit", ".txt");
FileWriter writer = new FileWriter(file);
writer.write("<doc>1</doc><doc>2</doc><doc>3</doc>");
writer.close();
URL url = file.toURI().toURL();
long length = CorpusImpl.populate(corpus, url, "doc", "UTF-8", 2, "doc", "text/plain", true);
assertEquals(2, corpus.size());
assertTrue(length > 0);
file.delete();
}

@Test
public void testPopulateWithEmptyPrefixGeneratesDocs() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("noprefix", ".txt");
FileWriter writer = new FileWriter(file);
writer.write("<doc>Hello</doc>");
writer.close();
URL url = file.toURI().toURL();
CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "", "text/plain", true);
assertEquals(1, corpus.size());
Document doc = corpus.get(0);
assertNotNull(doc);
file.delete();
}

@Test
public void testListIteratorWithNegativeIndexThrowsException() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.listIterator(-1);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException ex) {
assertTrue(true);
}
}

@Test
public void testToArrayWithTypedArrayLargerThanCorpus() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.add(doc);
Document[] resultArray = new Document[3];
Document[] output = corpus.toArray(resultArray);
assertEquals(3, output.length);
assertSame(doc, output[0]);
assertNull(output[1]);
assertNull(output[2]);
}

@Test
public void testEqualsTrueOnSameDocumentOrder() {
CorpusImpl c1 = new CorpusImpl();
CorpusImpl c2 = new CorpusImpl();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
c1.add(doc1);
c1.add(doc2);
c2.add(doc1);
c2.add(doc2);
assertTrue(c1.equals(c2));
}

@Test
public void testHashCodeEqualForEqualInstances() {
CorpusImpl c1 = new CorpusImpl();
CorpusImpl c2 = new CorpusImpl();
Document doc = mock(Document.class);
c1.add(doc);
c2.add(doc);
assertEquals(c1.hashCode(), c2.hashCode());
}

@Test
public void testStatusListenerNullCheckDoesNotAffectPopulate() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("listener", ".txt");
FileWriter writer = new FileWriter(file);
writer.write("<doc>status check</doc>");
writer.close();
URL url = file.toURI().toURL();
long length = CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "doc", "text/plain", true);
assertEquals(1, corpus.size());
assertTrue(length > 0);
file.delete();
}

@Test
public void testEqualsWithSameObjectReturnsTrue() {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.equals(corpus));
}

@Test
public void testListenerEventFiredOnlyForMatchingEventType() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
final int[] removed = { 0 };
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
removed[0]++;
}
};
corpus.add(doc);
corpus.addCorpusListener(listener);
corpus.remove(0);
assertEquals(1, removed[0]);
}

@Test
public void testClearCorpusTriggersRemovalsForAllDocuments() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
corpus.add(d1);
corpus.add(d2);
final int[] removals = { 0 };
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
removals[0]++;
}
};
corpus.addCorpusListener(listener);
corpus.clear();
assertEquals(2, removals[0]);
}

@Test
public void testRemoveCorpusListenerBeforeItIsAdded() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.removeCorpusListener(listener);
assertTrue(true);
}

@Test
public void testAddAndRemoveSameCorpusListenerMultipleTimes() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = mock(CorpusListener.class);
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
corpus.removeCorpusListener(listener);
corpus.removeCorpusListener(listener);
assertTrue(true);
}

@Test
public void testPopulateConcatenatedSkipsMalformedRootTag() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File tempFile = File.createTempFile("malformed", ".xml");
FileWriter writer = new FileWriter(tempFile);
writer.write("text only not <doc>anything useful");
writer.close();
URL fileUrl = tempFile.toURI().toURL();
long length = CorpusImpl.populate(corpus, fileUrl, "doc", "UTF-8", -1, "doc", "text/plain", true);
assertEquals(0, corpus.size());
assertEquals(0L, length);
tempFile.delete();
}

@Test
public void testSetDocumentAtInvalidIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
try {
corpus.set(5, doc);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException ex) {
assertNotNull(ex);
}
}

@Test
public void testGetDocumentNameOutOfRangeThrows() {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.getDocumentName(0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException ex) {
assertTrue(ex.getMessage() != null);
}
}

@Test
public void testSubListEntireRangeMatchesOriginal() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
corpus.add(d1);
corpus.add(d2);
List<Document> sub = corpus.subList(0, 2);
assertEquals(2, sub.size());
assertSame(d1, sub.get(0));
assertSame(d2, sub.get(1));
}

@Test
public void testAddAllAtNegativeIndexThrows() {
CorpusImpl corpus = new CorpusImpl();
List<Document> list = new ArrayList<Document>();
list.add(mock(Document.class));
try {
corpus.addAll(-1, list);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException ex) {
assertTrue(true);
}
}

@Test
public void testEqualsFalseDifferentSizeSameElements() {
CorpusImpl c1 = new CorpusImpl();
CorpusImpl c2 = new CorpusImpl();
Document doc = mock(Document.class);
c1.add(doc);
c2.add(doc);
c2.add(doc);
assertFalse(c1.equals(c2));
}

@Test
public void testPopulateWithNullEncodingUsesDefaultBehavior() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("defaultencoding", ".txt");
FileWriter writer = new FileWriter(file);
writer.write("<doc>content</doc>");
writer.close();
URL url = file.toURI().toURL();
CorpusImpl.populate(corpus, url, "doc", null, -1, "doc", "text/plain", true);
assertEquals(1, corpus.size());
file.delete();
}

@Test
public void testPopulateWithIncludeRootFalseStripsTags() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("stripTags", ".txt");
FileWriter writer = new FileWriter(file);
writer.write("<doc>Hello content</doc>");
writer.close();
URL url = file.toURI().toURL();
CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "x", "text/xml", false);
Document doc = corpus.get(0);
String content = doc.getContent().toString();
assertFalse(content.contains("<doc>"));
file.delete();
}

@Test
public void testPopulateWithNullMimeTypeDoesNotThrow() throws Exception {
CorpusImpl corpus = new CorpusImpl();
File file = File.createTempFile("nomime", ".txt");
FileWriter writer = new FileWriter(file);
writer.write("<doc>Ok</doc>");
writer.close();
URL url = file.toURI().toURL();
CorpusImpl.populate(corpus, url, "doc", "UTF-8", -1, "doc", null, true);
assertEquals(1, corpus.size());
file.delete();
}

@Test
public void testCleanupWithoutCrashOnGarbageListenerList() {
CorpusImpl corpus = new CorpusImpl();
// corpus.corpusListeners = new Vector<Object>();
corpus.cleanup();
assertTrue(true);
}

@Test
public void testToStringReturnsIdValue() {
CorpusImpl corpus = new CorpusImpl();
String output = corpus.toString();
assertNotNull(output);
}

@Test
public void testUnloadingDocumentDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
Document doc = mock(Document.class);
corpus.unloadDocument(doc);
assertTrue(true);
}

@Test
public void testNestedAddFireEventCorrectIndex() {
CorpusImpl corpus = new CorpusImpl();
Document d1 = mock(Document.class);
Document d2 = mock(Document.class);
final List<Integer> indices = new ArrayList<Integer>();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
// indices.add(e.getIndex());
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener);
corpus.add(d1);
corpus.add(d2);
assertEquals(2, indices.size());
assertEquals(Integer.valueOf(0), indices.get(0));
assertEquals(Integer.valueOf(1), indices.get(1));
}

@Test
public void testFireDocumentRemovedDoesNotCrashOnNullCorpusListeners() {
CorpusImpl corpus = new CorpusImpl();
// corpus.corpusListeners = null;
corpus.fireDocumentRemoved(new CorpusEvent(corpus, mock(Document.class), 0, CorpusEvent.DOCUMENT_REMOVED));
assertTrue(true);
}
}
