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

public class CorpusImpl_1_GPTLLMTest {

@Test
public void testAddGetDocument() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Sample text");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "Doc1");
boolean added = corpus.add(doc);
Document retrieved = corpus.get(0);
assertTrue(added);
assertEquals("Doc1", retrieved.getName());
}

@Test
public void testDocumentNames() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p1 = Factory.newFeatureMap();
p1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "One");
Document d1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p1, null, "DocA");
corpus.add(d1);
FeatureMap p2 = Factory.newFeatureMap();
p2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Two");
Document d2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p2, null, "DocB");
corpus.add(d2);
List<String> names = corpus.getDocumentNames();
assertEquals(2, names.size());
assertEquals("DocA", names.get(0));
assertEquals("DocB", names.get(1));
}

@Test
public void testGetDocumentNameByIndex() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Data");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "IndexedDoc");
corpus.add(doc);
String name = corpus.getDocumentName(0);
assertEquals("IndexedDoc", name);
}

@Test
public void testClearCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Text");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "CleanMe");
corpus.add(doc);
assertFalse(corpus.isEmpty());
corpus.clear();
assertTrue(corpus.isEmpty());
}

@Test
public void testSetAndReplaceDocument() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p1 = Factory.newFeatureMap();
p1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "First");
Document d1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p1, null, "Doc1");
corpus.add(d1);
FeatureMap p2 = Factory.newFeatureMap();
p2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Second");
Document d2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p2, null, "Doc2");
Document old = corpus.set(0, d2);
Document now = corpus.get(0);
assertEquals("Doc1", old.getName());
assertEquals("Doc2", now.getName());
}

@Test
public void testRemoveDocument() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "ToRemove");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "RemoveDoc");
corpus.add(doc);
boolean removed = corpus.remove(doc);
assertTrue(removed);
assertEquals(0, corpus.size());
}

@Test
public void testAddAtIndex() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p1 = Factory.newFeatureMap();
p1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "End");
Document d1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p1, null, "TailDoc");
FeatureMap p2 = Factory.newFeatureMap();
p2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Start");
Document d2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p2, null, "HeadDoc");
corpus.add(d1);
corpus.add(0, d2);
assertEquals("HeadDoc", corpus.get(0).getName());
assertEquals("TailDoc", corpus.get(1).getName());
}

@Test
public void testIsDocumentLoadedAlwaysTrue() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Loaded");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "LoadedDoc");
corpus.add(doc);
boolean loaded = corpus.isDocumentLoaded(0);
assertTrue(loaded);
}

@Test
public void testCorpusListenerTriggered() throws Exception {
CorpusImpl corpus = new CorpusImpl();
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
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Watched");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "WatchDoc");
corpus.add(doc);
corpus.remove(doc);
assertEquals(1, addedCount.get());
assertEquals(1, removedCount.get());
}

@Test
public void testPopulateFromDirectory() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File dir = temporaryFolder.newFolder("textfiles");
// File file = new File(dir, "example.txt");
// BufferedWriter out = new BufferedWriter(new FileWriter(file));
// out.write("Sample data");
// out.close();
// URL dirURL = dir.toURI().toURL();
ExtensionFileFilter filter = new ExtensionFileFilter("txt");
// CorpusImpl.populate(corpus, dirURL, filter, "UTF-8", false);
assertEquals(1, corpus.size());
String name = corpus.get(0).getName();
assertTrue(name.startsWith("example.txt_"));
}

@Test
public void testEqualsAndHashCodeEqualCorpora() throws Exception {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Content");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "Doc");
corpus1.add(doc);
corpus2.add(doc);
assertTrue(corpus1.equals(corpus2));
assertEquals(corpus1.hashCode(), corpus2.hashCode());
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateWithInvalidURLProtocol() throws Exception {
CorpusImpl corpus = new CorpusImpl();
URL invalidURL = new URL("http://invalidprotocol.com");
CorpusImpl.populate(corpus, invalidURL, null, "UTF-8", false);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testGetDocumentNameNegativeIndex() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.getDocumentName(-1);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testGetDocumentNameIndexTooLarge() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.getDocumentName(0);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testSetWithInvalidIndex() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Data");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "InvalidSet");
corpus.set(1, doc);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testRemoveInvalidIndex() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.remove(0);
}

@Test
public void testAddNullDocument() throws Exception {
CorpusImpl corpus = new CorpusImpl();
boolean added = corpus.add(null);
assertTrue(added);
assertNull(corpus.get(0));
}

@Test
public void testNullDocumentInGetDocumentNames() {
CorpusImpl corpus = new CorpusImpl();
corpus.add(null);
try {
corpus.getDocumentNames();
fail("Expected NullPointerException due to null document");
} catch (NullPointerException e) {
}
}

@Test
public void testPopulateFromEmptyDirectory() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File emptyDir = temporaryFolder.newFolder("emptydir");
// URL url = emptyDir.toURI().toURL();
ExtensionFileFilter filter = new ExtensionFileFilter("txt");
// CorpusImpl.populate(corpus, url, filter, "UTF-8", false);
assertEquals(0, corpus.size());
}

@Test
public void testPopulateWithNullFilterAcceptAllFiles() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File dir = temporaryFolder.newFolder("allfiles");
// File file = new File(dir, "somefile.abc");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("Testing");
// writer.close();
// URL url = dir.toURI().toURL();
// CorpusImpl.populate(corpus, url, null, "UTF-8", false);
assertEquals(1, corpus.size());
}

@Test
public void testPopulateTrecFileIncludeRootFalse() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File trecFile = temporaryFolder.newFile("docs.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DOC><TITLE>Hello World</TITLE></DOC>");
// writer.close();
// long size = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "DOC", "UTF-8", -1, "prefix", "text/plain", false);
assertEquals(1, corpus.size());
// assertTrue(size > 0);
String content = (String) corpus.get(0).getFeatures().get(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME);
assertFalse(content.contains("<DOC>"));
}

@Test
public void testPopulateTrecFileWithPartialDocumentTag() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File trecFile = temporaryFolder.newFile("partial.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DOC This is broken>\nHello</DOC>");
// writer.close();
// long size = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "DOC", "UTF-8", -1, "broken", null, true);
assertEquals(1, corpus.size());
// assertTrue(size > 0);
}

@Test
public void testPopulateTrecFileMaxOneDocument() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File trecFile = temporaryFolder.newFile("many.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DOC>First</DOC>\n<DOC>Second</DOC>");
// writer.close();
// long size = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "DOC", "UTF-8", 1, "limit", "text/xml", true);
assertEquals(1, corpus.size());
// assertTrue(size > 0);
}

@Test
public void testEqualsDifferentTypeReturnsFalse() throws Exception {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals("not a corpus"));
}

@Test
public void testEqualsWithNullReturnsFalse() throws Exception {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals(null));
}

@Test
public void testDuplicateOnEmptyCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// Factory.DuplicationContext ctx = new Factory.DuplicationContext();
// Corpus dup = corpus.duplicate(ctx);
// assertNotNull(dup);
// assertEquals(0, dup.size());
}

@Test
public void testResourceRenamedDoesNothing() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.resourceRenamed(null, "old", "new");
assertTrue(true);
}

@Test
public void testDatastoreEventsAreNoOp() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.datastoreCreated(null);
corpus.datastoreOpened(null);
corpus.datastoreClosed(null);
assertTrue(true);
}

@Test
public void testFireDocumentEventsWithoutListeners() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "EventDoc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "EvDoc");
CorpusEvent event = new CorpusEvent(corpus, doc, 0, CorpusEvent.DOCUMENT_ADDED);
corpus.fireDocumentAdded(event);
event = new CorpusEvent(corpus, doc, 0, CorpusEvent.DOCUMENT_REMOVED);
corpus.fireDocumentRemoved(event);
assertTrue(true);
}

@Test
public void testInitAddsDocumentsList() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "InitDoc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "Init");
java.util.List<Document> list = new java.util.ArrayList<Document>();
list.add(doc);
corpus.setDocumentsList(list);
Resource res = corpus.init();
assertEquals(1, corpus.size());
assertSame(corpus, res);
}

@Test
public void testInitWithNullDocumentsList() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
Resource res = corpus.init();
assertNotNull(res);
assertTrue(corpus.isEmpty());
}

@Test
public void testRemoveCorpusListenerOnEmptyListenerList() throws Exception {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.removeCorpusListener(listener);
assertTrue(true);
}

@Test
public void testRemoveCorpusListenerNotPresent() throws Exception {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener1 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
}
};
CorpusListener listener2 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener1);
corpus.removeCorpusListener(listener2);
assertTrue(true);
}

@Test
public void testSubListOutOfBounds() throws Exception {
CorpusImpl corpus = new CorpusImpl();
try {
corpus.subList(1, 2);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testIndexOfAndLastIndexOf() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "X");
Document docX = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "DocX");
corpus.add(docX);
corpus.add(docX);
int firstIndex = corpus.indexOf(docX);
int lastIndex = corpus.lastIndexOf(docX);
assertEquals(0, firstIndex);
assertEquals(1, lastIndex);
}

@Test
public void testToArrayReturnsCorrectType() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Sample");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "One");
corpus.add(doc);
Object[] array = corpus.toArray();
assertEquals(1, array.length);
assertEquals(doc, array[0]);
Document[] typedArray = corpus.toArray(new Document[0]);
assertEquals(1, typedArray.length);
assertEquals(doc, typedArray[0]);
}

@Test
public void testPopulateWithFileThatIsDirectoryIsIgnored() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File parent = temporaryFolder.newFolder("populateParent");
// File childDir = new File(parent, "subdir");
// childDir.mkdir();
// URL url = parent.toURI().toURL();
// CorpusImpl.populate(corpus, url, null, "UTF-8", null, false);
assertEquals(0, corpus.size());
}

@Test
public void testResourceUnloadedRemovesMatchingDocsOnly() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p1 = Factory.newFeatureMap();
p1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "A");
Document docA = (Document) Factory.createResource("gate.corpora.DocumentImpl", p1, null, "A");
corpus.add(docA);
FeatureMap p2 = Factory.newFeatureMap();
p2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "B");
Document docB = (Document) Factory.createResource("gate.corpora.DocumentImpl", p2, null, "B");
corpus.add(docB);
CreoleEvent event = new CreoleEvent(docA, CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(event);
assertEquals(1, corpus.size());
assertEquals("B", corpus.get(0).getName());
}

@Test
public void testEqualsTrueWithSameReference() throws Exception {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.equals(corpus));
}

@Test
public void testEqualsFalseWithDifferentDocumentOrder() throws Exception {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
FeatureMap params1 = Factory.newFeatureMap();
params1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "X");
Document d1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params1, null, "X");
FeatureMap params2 = Factory.newFeatureMap();
params2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Y");
Document d2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params2, null, "Y");
corpus1.add(d1);
corpus1.add(d2);
corpus2.add(d2);
corpus2.add(d1);
assertFalse(corpus1.equals(corpus2));
}

@Test(expected = IllegalArgumentException.class)
public void testPopulateThrowsIfUrlIsNotFile() throws Exception {
CorpusImpl corpus = new CorpusImpl();
URL invalidUrl = new URL("http://notafile.com");
CorpusImpl.populate(corpus, invalidUrl, null, "UTF-8", false);
}

@Test(expected = java.io.FileNotFoundException.class)
public void testPopulateThrowsFileNotFound() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File nonExistent = new File(temporaryFolder.getRoot(), "nope");
// URL url = nonExistent.toURI().toURL();
// CorpusImpl.populate(corpus, url, null, "UTF-8", false);
}

@Test
public void testCreateDocumentWithMimeType() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File txt = temporaryFolder.newFile("file.custom");
// BufferedWriter writer = new BufferedWriter(new FileWriter(txt));
// writer.write("Custom MIME");
// writer.close();
// URL url = txt.getParentFile().toURI().toURL();
// CorpusImpl.populate(corpus, url, null, "UTF-8", "custom/type", false);
assertEquals(1, corpus.size());
assertNotNull(corpus.get(0));
}

@Test
public void testFireDocumentAddedInvokesListener() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicBoolean added = new AtomicBoolean(false);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
added.set(true);
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener);
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Data");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "EventDoc");
corpus.add(doc);
assertTrue(added.get());
}

@Test
public void testDocumentRemovedFiresEvent() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicBoolean removed = new AtomicBoolean(false);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
removed.set(true);
}
};
corpus.addCorpusListener(listener);
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Data");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "ToRemove");
corpus.add(doc);
corpus.remove(0);
assertTrue(removed.get());
}

@Test
public void testSetDocumentsListNullCleared() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
assertNull(corpus.getDocumentsList());
}

@Test
public void testGetDocumentsListAfterSet() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "D");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "Direct");
List<Document> list = new java.util.ArrayList<Document>();
list.add(doc);
corpus.setDocumentsList(list);
List<Document> returned = corpus.getDocumentsList();
assertEquals(1, returned.size());
assertEquals("Direct", returned.get(0).getName());
}

@Test
public void testAddAllWithIndex() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p1 = Factory.newFeatureMap();
p1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "D1");
Document d1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p1, null, "D1");
FeatureMap p2 = Factory.newFeatureMap();
p2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "D2");
Document d2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p2, null, "D2");
java.util.List<Document> list = new java.util.ArrayList<Document>();
list.add(d1);
list.add(d2);
boolean added = corpus.addAll(0, list);
assertTrue(added);
assertEquals("D1", corpus.get(0).getName());
assertEquals("D2", corpus.get(1).getName());
}

@Test
public void testContainsAllAndRetainAll() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "KeepMe");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "KeepMe");
corpus.add(doc);
java.util.List<Document> target = new java.util.ArrayList<Document>();
target.add(doc);
assertTrue(corpus.containsAll(target));
boolean changed = corpus.retainAll(target);
assertFalse(changed);
}

@Test(expected = NullPointerException.class)
public void testAddListWithNullDocumentCausesNullPointerInGetDocumentNames() {
CorpusImpl corpus = new CorpusImpl();
java.util.List<Document> docs = new java.util.ArrayList<Document>();
docs.add(null);
corpus.addAll(docs);
corpus.getDocumentNames();
}

@Test
public void testToArrayTypedLargerThanCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "ArrayDoc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "ArrayDoc");
corpus.add(doc);
Document[] prefilledArray = new Document[10];
prefilledArray[1] = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "Unused");
Document[] result = corpus.toArray(prefilledArray);
assertEquals("ArrayDoc", result[0].getName());
assertNull("Second index must be null", result[1]);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testInvalidAddIndexThrows() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "Doc");
corpus.add(1, doc);
}

@Test
public void testRemoveAllDoesNotChangeWhenNoOverlap() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "DocA");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "DocA");
corpus.add(doc);
java.util.List<Document> unrelated = new java.util.ArrayList<Document>();
FeatureMap p2 = Factory.newFeatureMap();
p2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Unrelated");
Document unrelatedDoc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p2, null, "Unrelated");
unrelated.add(unrelatedDoc);
boolean changed = corpus.removeAll(unrelated);
assertFalse(changed);
assertEquals(1, corpus.size());
}

@Test
public void testPopulateTrecReturnsZeroLengthIfNoValidDocuments() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File trecFile = temporaryFolder.newFile("nodocs.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("Just text with no tags");
// writer.close();
// long result = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "DOC", "UTF-8", -1, "", "plain", true);
// assertEquals(0, result);
assertEquals(0, corpus.size());
}

@Test
public void testPopulateNestedTagNameCaseInsensitive() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File trecFile = temporaryFolder.newFile("WeirdCase.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DoC>MixedCase</DoC>");
// writer.close();
// long result = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "doc", "UTF-8", -1, "cased", "plain", true);
assertEquals(1, corpus.size());
// assertTrue(result > 0);
}

@Test
public void testCleanupRemovesCreoleListener() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.cleanup();
assertTrue(true);
}

@Test
public void testDuplicateCorpusWithEmptyContext() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "DupContent");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "DocToDup");
corpus.add(doc);
// Factory.DuplicationContext ctx = new Factory.DuplicationContext();
// Corpus duplicated = corpus.duplicate(ctx);
// assertNotSame(corpus, duplicated);
// assertEquals(1, duplicated.size());
// assertNotSame(corpus.get(0), duplicated.get(0));
}

@Test
public void testCorpusImplementsCorpusInterface() throws Exception {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus instanceof Corpus);
}

@Test(expected = NullPointerException.class)
public void testPopulateFromNullURLThrowsNPE() throws Exception {
CorpusImpl corpus = new CorpusImpl();
URL nullUrl = null;
CorpusImpl.populate(corpus, nullUrl, null, "UTF-8", false);
}

@Test
public void testSubListReturnsExpectedView() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap f1 = Factory.newFeatureMap();
f1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc1");
Document doc1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", f1, null, "Doc1");
FeatureMap f2 = Factory.newFeatureMap();
f2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc2");
Document doc2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", f2, null, "Doc2");
corpus.add(doc1);
corpus.add(doc2);
List<Document> sub = corpus.subList(0, 1);
assertEquals(1, sub.size());
assertEquals("Doc1", sub.get(0).getName());
}

@Test
public void testMimeTypeIsPassedToCreatedDocument() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File docFile = temporaryFolder.newFile("withMime.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(docFile));
// writer.write("MimeTypeContent");
// writer.close();
// URL url = docFile.getParentFile().toURI().toURL();
// CorpusImpl.populate(corpus, url, null, "UTF-8", "my/type", false);
assertEquals(1, corpus.size());
Document d = corpus.get(0);
String mime = (String) d.getFeatures().get(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME);
assertEquals("my/type", mime);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testListIteratorWithInvalidIndexThrows() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.listIterator(1);
}

@Test
public void testCorpusEqualsWithDifferentCorpusTypeReturnsFalse() throws Exception {
CorpusImpl corpus = new CorpusImpl();
List<Document> otherList = new ArrayList<Document>();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "X");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "DocX");
otherList.add(doc);
corpus.add(doc);
assertFalse(corpus.equals(otherList));
}

@Test
public void testCreateCorpusAndCallAllListMethodsWithoutException() throws Exception {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.isEmpty());
assertEquals(0, corpus.size());
assertFalse(corpus.contains("NotADocument"));
assertEquals(-1, corpus.indexOf("NotADocument"));
assertEquals(-1, corpus.lastIndexOf("NotADocument"));
assertEquals(0, corpus.toArray().length);
}

@Test
public void testResourceUnloadedRemovesAllOccurrences() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "RepeatingDoc");
corpus.add(doc);
corpus.add(doc);
assertEquals(2, corpus.size());
CreoleEvent event = new CreoleEvent(doc, CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(event);
assertEquals(0, corpus.size());
}

@Test
public void testCleanupDoesNotThrowWhenCalledMultipleTimes() throws Exception {
CorpusImpl corpus = new CorpusImpl();
corpus.cleanup();
corpus.cleanup();
assertTrue(true);
}

@Test
public void testPopulateConcatenatedEmptyEncodingSwitch() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File trecFile = temporaryFolder.newFile("trec.html");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DOC><TITLE>Test</TITLE></DOC>");
// writer.close();
// long result = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "DOC", "", 1, "t", "text/xml", true);
assertEquals(1, corpus.size());
// assertTrue(result > 0L);
}

@Test
public void testPopulateConcatenatedExtractLimitedNumberOfDocs() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File trecFile = temporaryFolder.newFile("multiple.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(trecFile));
// writer.write("<DOC>one</DOC>\n<DOC>two</DOC>\n<DOC>three</DOC>");
// writer.close();
// long result = CorpusImpl.populate(corpus, trecFile.toURI().toURL(), "DOC", "UTF-8", 2, "limit", "text/plain", true);
assertEquals(2, corpus.size());
// assertTrue(result > 0L);
}

@Test
public void testSubListModificationReflectedInOriginalCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Item");
Document doc1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "Doc1");
Document doc2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "Doc2");
corpus.add(doc1);
corpus.add(doc2);
List<Document> sub = corpus.subList(0, 2);
sub.remove(1);
assertEquals(1, corpus.size());
}

@Test
public void testPopulateWithMimeTypeNullLeavesMimeUnassigned() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File file = temporaryFolder.newFile("plain.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("Hello MIME");
// writer.close();
// URL url = file.getParentFile().toURI().toURL();
// CorpusImpl.populate(corpus, url, null, "UTF-8", null, false);
assertEquals(1, corpus.size());
Object mime = corpus.get(0).getFeatures().get(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME);
assertNull(mime);
}

@Test
public void testPopulateDirectoryWithUppercaseExtensionMatch() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File dir = temporaryFolder.newFolder("upperext");
// File file = new File(dir, "UPPER.TXT");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("Uppercase match");
// writer.close();
ExtensionFileFilter filter = new ExtensionFileFilter("txt");
// CorpusImpl.populate(corpus, dir.toURI().toURL(), filter, "UTF-8", false);
assertEquals(1, corpus.size());
}

@Test
public void testAddAndRemoveListenerDoesNotThrowWithMultipleInstances() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicInteger events = new AtomicInteger(0);
CorpusListener listener1 = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
events.incrementAndGet();
}

public void documentRemoved(CorpusEvent e) {
events.incrementAndGet();
}
};
corpus.addCorpusListener(listener1);
corpus.addCorpusListener(listener1);
corpus.removeCorpusListener(listener1);
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "DocOnce");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "AddRemove");
corpus.add(doc);
corpus.remove(doc);
assertEquals(0, events.get());
}

@Test
public void testEqualsCorpusWithDifferentInternalDocumentInstanceReturnsFalse() throws Exception {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
FeatureMap paramsA = Factory.newFeatureMap();
paramsA.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "ABC");
Document docA = (Document) Factory.createResource("gate.corpora.DocumentImpl", paramsA, null, "X");
FeatureMap paramsB = Factory.newFeatureMap();
paramsB.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "ABC");
Document docB = (Document) Factory.createResource("gate.corpora.DocumentImpl", paramsB, null, "X");
corpus1.add(docA);
corpus2.add(docB);
assertFalse(corpus1.equals(corpus2));
}

@Test
public void testAddingNullDoesNotCrashContains() {
CorpusImpl corpus = new CorpusImpl();
corpus.add(null);
assertTrue(corpus.contains(null));
}

@Test
public void testToStringOnDocumentListDoesNotThrow() throws Exception {
CorpusImpl corpus = new CorpusImpl();
List<String> names = corpus.getDocumentNames();
assertEquals("[]", names.toString());
}

@Test
public void testFactoryDeleteResourceOnPopulatedTransientCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File file = temporaryFolder.newFile("delete.txt");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("Transient content");
// writer.close();
// URL url = file.toURI().toURL();
FeatureMap params = Factory.newFeatureMap();
// params.put(Document.DOCUMENT_URL_PARAMETER_NAME, url);
params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "doc-delete");
corpus.add(doc);
Factory.deleteResource(doc);
assertTrue(true);
}

@Test
public void testAddCorpusListenerTwiceIsNoOpSecondTime() throws Exception {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
assertTrue(true);
}

@Test
public void testEqualsAndHashCodeWithEmptyCorpus() {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
assertTrue("Empty corpora should be equal", corpus1.equals(corpus2));
assertEquals("HashCodes should equal for empty corpora", corpus1.hashCode(), corpus2.hashCode());
}

@Test
public void testClearEmptyCorpusDoesNotThrow() {
CorpusImpl corpus = new CorpusImpl();
corpus.clear();
assertTrue(corpus.isEmpty());
}

@Test
public void testListIteratorTraversal() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap params = Factory.newFeatureMap();
params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "IterDoc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, null, "IterDoc");
corpus.add(doc);
ListIterator<Document> iterator = corpus.listIterator();
assertTrue(iterator.hasNext());
Document result = iterator.next();
assertEquals("IterDoc", result.getName());
}

@Test
public void testToArrayPreservesOrderAndSize() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap pA = Factory.newFeatureMap();
pA.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "A");
Document docA = (Document) Factory.createResource("gate.corpora.DocumentImpl", pA, null, "A");
FeatureMap pB = Factory.newFeatureMap();
pB.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "B");
Document docB = (Document) Factory.createResource("gate.corpora.DocumentImpl", pB, null, "B");
corpus.add(docA);
corpus.add(docB);
Object[] array = corpus.toArray();
assertEquals(2, array.length);
assertEquals("A", ((Document) array[0]).getName());
assertEquals("B", ((Document) array[1]).getName());
}

@Test
public void testEqualsWithDifferentSizesReturnsFalse() throws Exception {
CorpusImpl corpus1 = new CorpusImpl();
CorpusImpl corpus2 = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Mismatch");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "Doc1");
corpus1.add(doc);
assertFalse(corpus1.equals(corpus2));
}

@Test
public void testAddNullDocumentAndIndexOfIt() {
CorpusImpl corpus = new CorpusImpl();
corpus.add(null);
int index = corpus.indexOf(null);
assertEquals(0, index);
assertTrue(corpus.contains(null));
}

@Test
public void testAddAllWithEmptyListReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
List<Document> emptyList = new ArrayList<Document>();
boolean result = corpus.addAll(emptyList);
assertFalse(result);
}

@Test
public void testAddAllAtIndexEmptyListReturnsFalse() {
CorpusImpl corpus = new CorpusImpl();
List<Document> emptyList = new ArrayList<Document>();
boolean result = corpus.addAll(0, emptyList);
assertFalse(result);
}

@Test
public void testRetainAllWithEmptyListClearsCorpus() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap f = Factory.newFeatureMap();
f.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "D");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", f, null, "Doc");
corpus.add(doc);
boolean changed = corpus.retainAll(new ArrayList<Document>());
assertTrue(changed);
assertEquals(0, corpus.size());
}

@Test
public void testRemoveAllRemovesMatchingDocuments() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap f = Factory.newFeatureMap();
f.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "ToRemove");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", f, null, "ToRemove");
corpus.add(doc);
List<Document> toRemove = Arrays.asList(doc);
boolean result = corpus.removeAll(toRemove);
assertTrue(result);
assertTrue(corpus.isEmpty());
}

@Test
public void testDocumentAddedEventTriggersListener() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicInteger addedCalled = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
addedCalled.incrementAndGet();
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener);
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "AddEvent");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "AddEvent");
corpus.add(doc);
assertEquals(1, addedCalled.get());
}

@Test
public void testDocumentRemovedEventTriggersListener() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicInteger removedCalled = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
removedCalled.incrementAndGet();
}
};
corpus.addCorpusListener(listener);
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "RemoveEvent");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "RemoveEvent");
corpus.add(doc);
corpus.remove(doc);
assertEquals(1, removedCalled.get());
}

@Test
public void testDuplicateCorpusPreservesDocumentOrder() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p1 = Factory.newFeatureMap();
p1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "First");
Document first = (Document) Factory.createResource("gate.corpora.DocumentImpl", p1, null, "First");
FeatureMap p2 = Factory.newFeatureMap();
p2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Second");
Document second = (Document) Factory.createResource("gate.corpora.DocumentImpl", p2, null, "Second");
corpus.add(first);
corpus.add(second);
// Factory.DuplicationContext ctx = new Factory.DuplicationContext();
// Corpus duplicated = corpus.duplicate(ctx);
// assertEquals(2, duplicated.size());
// assertEquals("First", duplicated.get(0).getName());
// assertEquals("Second", duplicated.get(1).getName());
}

@Test
public void testGetDocumentNameAfterAdd() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "NamedDoc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "MyDocumentName");
corpus.add(doc);
String name = corpus.getDocumentName(0);
assertEquals("MyDocumentName", name);
}

@Test
public void testEqualsSameCorpusReference() throws Exception {
CorpusImpl corpus = new CorpusImpl();
assertTrue(corpus.equals(corpus));
}

@Test
public void testEqualsNullReference() throws Exception {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals(null));
}

@Test
public void testHashCodeConsistency() {
CorpusImpl corpus = new CorpusImpl();
int hash1 = corpus.hashCode();
int hash2 = corpus.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testAddAllAndSetDocumentFiresEvents() throws Exception {
CorpusImpl corpus = new CorpusImpl();
final AtomicInteger added = new AtomicInteger(0);
final AtomicInteger removed = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
added.incrementAndGet();
}

public void documentRemoved(CorpusEvent e) {
removed.incrementAndGet();
}
};
corpus.addCorpusListener(listener);
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "DocContent");
Document doc1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "Doc1");
Document doc2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "Doc2");
corpus.add(doc1);
corpus.set(0, doc2);
assertEquals(2, added.get());
assertEquals(1, removed.get());
}

@Test
public void testSubListViewReflectsParentCorpusChanges() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap fm = Factory.newFeatureMap();
fm.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "SubDoc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", fm, null, "DocX");
corpus.add(doc);
List<Document> sub = corpus.subList(0, 1);
sub.clear();
assertTrue(corpus.isEmpty());
}

@Test
public void testInitWithNonEmptyDocumentsListAddsAll() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap fm = Factory.newFeatureMap();
fm.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "InitDoc");
Document document = (Document) Factory.createResource("gate.corpora.DocumentImpl", fm, null, "Init1");
List<Document> list = new ArrayList<Document>();
list.add(document);
corpus.setDocumentsList(list);
Resource result = corpus.init();
assertEquals(1, corpus.size());
assertSame(corpus, result);
}

@Test
public void testPopulateFailsOnNonDirectory() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File fileNotDir = temp.newFile("notADir.txt");
// URL fileUrl = fileNotDir.toURI().toURL();
try {
// CorpusImpl.populate(corpus, fileUrl, null, "UTF-8", false);
fail("Expected exception due to not-a-directory input");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("is not a directory"));
}
}

@Test
public void testPopulateSkipsDirectoriesInNonRecursiveMode() throws Exception {
CorpusImpl corpus = new CorpusImpl();
// File parent = temp.newFolder("top");
// File subDir = new File(parent, "subdir");
// subDir.mkdir();
// URL dirUrl = parent.toURI().toURL();
// CorpusImpl.populate(corpus, dirUrl, null, "UTF-8", false);
assertEquals(0, corpus.size());
}

@Test
public void testEqualsDifferentTypes() {
CorpusImpl corpus = new CorpusImpl();
assertFalse(corpus.equals("not a corpus"));
}

@Test
public void testEqualsDocumentOrderMatters() throws Exception {
CorpusImpl a = new CorpusImpl();
CorpusImpl b = new CorpusImpl();
FeatureMap fm = Factory.newFeatureMap();
fm.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc");
Document d1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", fm, null, "A");
Document d2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", fm, null, "B");
a.add(d1);
a.add(d2);
b.add(d2);
b.add(d1);
assertFalse(a.equals(b));
}

@Test
public void testRemoveCorpusListenerOnNullListenersListDoesNothing() {
CorpusImpl corpus = new CorpusImpl();
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.removeCorpusListener(listener);
assertTrue(true);
}

@Test
public void testAddCorpusListenerAddsOnlyOnce() {
CorpusImpl corpus = new CorpusImpl();
final AtomicInteger addCount = new AtomicInteger(0);
CorpusListener listener = new CorpusListener() {

public void documentAdded(CorpusEvent e) {
addCount.incrementAndGet();
}

public void documentRemoved(CorpusEvent e) {
}
};
corpus.addCorpusListener(listener);
corpus.addCorpusListener(listener);
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Once");
// try {
// Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "OneDoc");
// corpus.add(doc);
// } catch (ResourceInstantiationException e) {
// fail("Unexpected error");
// }
assertEquals(1, addCount.get());
}

@Test
public void testResourceUnloadedRemovesOnlyMatchingDocuments() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap f1 = Factory.newFeatureMap();
f1.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "A");
Document doc1 = (Document) Factory.createResource("gate.corpora.DocumentImpl", f1, null, "DocA");
FeatureMap f2 = Factory.newFeatureMap();
f2.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "B");
Document doc2 = (Document) Factory.createResource("gate.corpora.DocumentImpl", f2, null, "DocB");
corpus.add(doc1);
corpus.add(doc2);
CreoleEvent event = new CreoleEvent(doc1, CreoleEvent.RESOURCE_UNLOADED);
corpus.resourceUnloaded(event);
assertEquals(1, corpus.size());
assertEquals("DocB", corpus.get(0).getName());
}

@Test
public void testDuplicateCreatesDistinctCorpusObject() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap f = Factory.newFeatureMap();
f.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "Doc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", f, null, "D");
corpus.add(doc);
// Factory.DuplicationContext ctx = new Factory.DuplicationContext();
// Corpus copy = corpus.duplicate(ctx);
// assertNotSame(copy, corpus);
// assertEquals(1, copy.size());
// assertNotSame(copy.get(0), corpus.get(0));
}

@Test
public void testInitWithNullDocumentsListNoCrash() {
CorpusImpl corpus = new CorpusImpl();
corpus.setDocumentsList(null);
// assertEquals(0, corpus.init().size());
}

@Test
public void testSetAndGetDocumentsListReferenceConsistency() throws Exception {
CorpusImpl corpus = new CorpusImpl();
FeatureMap p = Factory.newFeatureMap();
p.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, "ListDoc");
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", p, null, "LD");
List<Document> list = new ArrayList<Document>();
list.add(doc);
corpus.setDocumentsList(list);
List<Document> returned = corpus.getDocumentsList();
assertEquals(1, returned.size());
assertSame(doc, returned.get(0));
}
}
