package gate.corpora;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.Resource;
import gate.event.CorpusEvent;
import gate.event.CorpusListener;
import gate.event.CreoleEvent;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;

public class CorpusImplPromptTest {

  @Test
  public void newCorpusHasEmptyListSemantics() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();

    assertTrue(corpus.isEmpty());
    assertEquals(0, corpus.size());
    assertEquals(Collections.emptyList(), corpus.getDocumentNames());
    assertArrayEquals(new Object[0], corpus.toArray());
    assertFalse(corpus.contains(new DocumentImpl()));

    corpus.cleanup();
  }

  @Test
  public void addAndReadDocumentsPreservesInsertionOrder() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    first.setName("alpha");
    DocumentImpl second = new DocumentImpl();
    second.setName("beta");

    assertTrue(corpus.add(first));
    assertTrue(corpus.add(second));
    assertEquals(2, corpus.size());
    assertSame(first, corpus.get(0));
    assertSame(second, corpus.get(1));
    assertEquals("alpha", corpus.getDocumentName(0));
    assertEquals(Arrays.asList("alpha", "beta"), corpus.getDocumentNames());

    corpus.cleanup();
  }

  @Test
  public void indexedAddPlacesDocumentAtRequestedPosition() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    first.setName("first");
    DocumentImpl third = new DocumentImpl();
    third.setName("third");
    DocumentImpl second = new DocumentImpl();
    second.setName("second");

    corpus.add(first);
    corpus.add(third);
    corpus.add(1, second);

    assertEquals(Arrays.asList("first", "second", "third"), corpus.getDocumentNames());
    corpus.cleanup();
  }

  @Test
  public void setReturnsOldDocumentAndFiresRemovalThenAddition() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl oldDocument = new DocumentImpl();
    oldDocument.setName("old");
    DocumentImpl replacement = new DocumentImpl();
    replacement.setName("new");
    List<String> calls = new ArrayList<String>();
    List<CorpusEvent> events = new ArrayList<CorpusEvent>();
    corpus.add(oldDocument);
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
        calls.add("added");
        events.add(event);
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
        calls.add("removed");
        events.add(event);
      }
    });

    assertSame(oldDocument, corpus.set(0, replacement));
    assertEquals(Arrays.asList("removed", "added"), calls);
    assertSame(oldDocument, events.get(0).getDocument());
    assertSame(replacement, events.get(1).getDocument());
    assertEquals(0, events.get(0).getDocumentIndex());
    assertEquals(0, events.get(1).getDocumentIndex());
    assertSame(corpus, events.get(0).getSource());

    corpus.cleanup();
  }

  @Test
  public void removeByIndexReturnsDocumentAndReportsOriginalIndex() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    List<CorpusEvent> removed = new ArrayList<CorpusEvent>();
    corpus.add(first);
    corpus.add(second);
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
        removed.add(event);
      }
    });

    assertSame(second, corpus.remove(1));
    assertEquals(1, removed.size());
    assertSame(second, removed.get(0).getDocument());
    assertEquals(1, removed.get(0).getDocumentIndex());
    assertEquals(1, corpus.size());

    corpus.cleanup();
  }

  @Test
  public void removeByObjectDistinguishesPresentAndAbsentDocuments() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl present = new DocumentImpl();
    DocumentImpl absent = new DocumentImpl();
    List<CorpusEvent> removed = new ArrayList<CorpusEvent>();
    corpus.add(present);
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
        removed.add(event);
      }
    });

    assertFalse(corpus.remove(absent));
    assertTrue(corpus.remove(present));
    assertEquals(1, removed.size());
    assertSame(present, removed.get(0).getDocument());

    corpus.cleanup();
  }

  @Test
  public void addAllAppendsDocumentsAndFiresOneEventPerDocument() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    List<CorpusEvent> added = new ArrayList<CorpusEvent>();
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
        added.add(event);
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
      }
    });

    assertTrue(corpus.addAll(Arrays.asList(first, second)));
    assertEquals(2, added.size());
    assertEquals(0, added.get(0).getDocumentIndex());
    assertEquals(1, added.get(1).getDocumentIndex());
    assertFalse(corpus.addAll(Collections.<Document>emptyList()));

    corpus.cleanup();
  }

  @Test
  public void indexedAddAllInsertsACollectionAndReportsShiftedIndices() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl outerFirst = new DocumentImpl();
    DocumentImpl outerLast = new DocumentImpl();
    DocumentImpl insertedFirst = new DocumentImpl();
    DocumentImpl insertedSecond = new DocumentImpl();
    List<CorpusEvent> added = new ArrayList<CorpusEvent>();
    corpus.add(outerFirst);
    corpus.add(outerLast);
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
        added.add(event);
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
      }
    });

    assertTrue(corpus.addAll(1, Arrays.asList(insertedFirst, insertedSecond)));
    assertEquals(Arrays.asList(outerFirst, insertedFirst, insertedSecond, outerLast), corpus);
    assertEquals(1, added.get(0).getDocumentIndex());
    assertEquals(2, added.get(1).getDocumentIndex());

    corpus.cleanup();
  }

  @Test
  public void clearRemovesEveryDocumentAndFiresRemovalEvents() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    List<CorpusEvent> removed = new ArrayList<CorpusEvent>();
    corpus.add(first);
    corpus.add(second);
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
        removed.add(event);
      }
    });

    corpus.clear();

    assertTrue(corpus.isEmpty());
    assertEquals(2, removed.size());
    assertSame(first, removed.get(0).getDocument());
    assertSame(second, removed.get(1).getDocument());
    corpus.cleanup();
  }

  @Test
  public void removeAllAndRetainAllUseListMutationSemantics() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    DocumentImpl third = new DocumentImpl();
    corpus.addAll(Arrays.asList(first, second, third));

    assertTrue(corpus.removeAll(Arrays.asList(first, new DocumentImpl())));
    assertEquals(Arrays.asList(second, third), corpus);
    assertTrue(corpus.retainAll(Collections.singleton(third)));
    assertEquals(Collections.singletonList(third), corpus);
    assertFalse(corpus.retainAll(Collections.singleton(third)));

    corpus.cleanup();
  }

  @Test
  public void duplicateReferencesAreFoundAtFirstAndLastPositions() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl repeated = new DocumentImpl();
    DocumentImpl middle = new DocumentImpl();
    corpus.addAll(Arrays.asList(repeated, middle, repeated));

    assertTrue(corpus.contains(repeated));
    assertTrue(corpus.containsAll(Arrays.asList(middle, repeated)));
    assertEquals(0, corpus.indexOf(repeated));
    assertEquals(2, corpus.lastIndexOf(repeated));
    assertEquals(-1, corpus.indexOf(new DocumentImpl()));

    corpus.cleanup();
  }

  @Test
  public void iteratorRemoveMutatesCorpusAndFiresEvent() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl document = new DocumentImpl();
    List<CorpusEvent> removed = new ArrayList<CorpusEvent>();
    corpus.add(document);
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
        removed.add(event);
      }
    });
    Iterator<Document> iterator = corpus.iterator();

    assertSame(document, iterator.next());
    iterator.remove();
    assertTrue(corpus.isEmpty());
    assertEquals(1, removed.size());
    assertSame(document, removed.get(0).getDocument());

    corpus.cleanup();
  }

  @Test
  public void listIteratorCanSetAddAndRemoveThroughCorpusProxy() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl original = new DocumentImpl();
    DocumentImpl replacement = new DocumentImpl();
    DocumentImpl appended = new DocumentImpl();
    List<String> calls = new ArrayList<String>();
    corpus.add(original);
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
        calls.add("add");
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
        calls.add("remove");
      }
    });
    ListIterator<Document> iterator = corpus.listIterator();

    assertSame(original, iterator.next());
    iterator.set(replacement);
    iterator.add(appended);
    assertSame(appended, iterator.previous());
    iterator.remove();

    assertEquals(Collections.singletonList(replacement), corpus);
    assertEquals(Arrays.asList("remove", "add", "add", "remove"), calls);
    corpus.cleanup();
  }

  @Test
  public void listIteratorFromIndexStartsAtRequestedPosition() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    corpus.addAll(Arrays.asList(first, second));

    ListIterator<Document> iterator = corpus.listIterator(1);

    assertEquals(1, iterator.nextIndex());
    assertSame(second, iterator.next());
    assertFalse(iterator.hasNext());
    corpus.cleanup();
  }

  @Test
  public void subListIsBackedByCorpusAndPropagatesRemoval() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    DocumentImpl third = new DocumentImpl();
    List<CorpusEvent> removed = new ArrayList<CorpusEvent>();
    corpus.addAll(Arrays.asList(first, second, third));
    corpus.addCorpusListener(new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
        removed.add(event);
      }
    });
    List<Document> middleAndLast = corpus.subList(1, 3);

    assertSame(second, middleAndLast.remove(0));
    assertEquals(Arrays.asList(first, third), corpus);
    assertEquals(1, removed.get(0).getDocumentIndex());

    corpus.cleanup();
  }

  @Test
  public void arrayConversionsHonorRawAndTypedArrayContracts() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    corpus.addAll(Arrays.asList(first, second));

    assertArrayEquals(new Object[] {first, second}, corpus.toArray());
    Document[] exact = corpus.toArray(new Document[0]);
    assertArrayEquals(new Document[] {first, second}, exact);
    Document[] oversized = new Document[] {null, null, first};
    assertSame(oversized, corpus.toArray(oversized));
    assertSame(first, oversized[0]);
    assertSame(second, oversized[1]);
    assertNull(oversized[2]);

    corpus.cleanup();
  }

  @Test
  public void equalityAndHashCodeFollowCorpusContents() throws Exception {
    Gate.init();
    CorpusImpl left = new CorpusImpl();
    CorpusImpl right = new CorpusImpl();
    DocumentImpl document = new DocumentImpl();
    left.add(document);
    right.add(document);

    assertEquals(left, right);
    assertEquals(right, left);
    assertEquals(left.hashCode(), right.hashCode());
    assertNotEquals(left, Collections.singletonList(document));
    assertNotEquals(left, null);

    left.cleanup();
    right.cleanup();
  }

  @Test
  public void invalidIndicesPropagateListBoundsFailures() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();

    assertThrows(IndexOutOfBoundsException.class, () -> corpus.get(0));
    assertThrows(IndexOutOfBoundsException.class, () -> corpus.remove(0));
    assertThrows(IndexOutOfBoundsException.class, () -> corpus.add(1, new DocumentImpl()));
    assertThrows(IndexOutOfBoundsException.class, () -> corpus.listIterator(1));

    corpus.cleanup();
  }

  @Test
  public void nullMemberIsAcceptedButNameLookupFailsClearly() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();

    assertTrue(corpus.add(null));
    assertTrue(corpus.contains(null));
    assertNull(corpus.get(0));
    assertThrows(NullPointerException.class, corpus::getDocumentNames);
    assertThrows(NullPointerException.class, () -> corpus.getDocumentName(0));

    corpus.cleanup();
  }

  @Test
  public void initCopiesConfiguredDocumentsEachTimeItIsCalled() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl first = new DocumentImpl();
    DocumentImpl second = new DocumentImpl();
    List<Document> configured = Arrays.asList(first, second);
    corpus.setDocumentsList(configured);

    assertSame(configured, corpus.getDocumentsList());
    assertSame(corpus, corpus.init());
    assertEquals(Arrays.asList(first, second), corpus);
    assertSame(corpus, corpus.init());
    assertEquals(Arrays.asList(first, second, first, second), corpus);

    corpus.cleanup();
  }

  @Test
  public void initWithNullOrEmptyConfigurationLeavesCorpusUnchanged() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl existing = new DocumentImpl();
    corpus.add(existing);

    corpus.setDocumentsList(null);
    assertSame(corpus, corpus.init());
    corpus.setDocumentsList(Collections.<Document>emptyList());
    assertSame(corpus, corpus.init());
    assertEquals(Collections.singletonList(existing), corpus);

    corpus.cleanup();
  }

  @Test
  public void transientUnloadIsNoOpAndLoadedFlagIsAlwaysTrue() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl document = new DocumentImpl();
    corpus.add(document);

    corpus.unloadDocument(document);

    assertEquals(Collections.singletonList(document), corpus);
    assertTrue(corpus.isDocumentLoaded(0));
    assertTrue(corpus.isDocumentLoaded(999));
    corpus.cleanup();
  }

  @Test
  public void resourceUnloadedRemovesEveryOccurrenceOfDocument() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl repeated = new DocumentImpl();
    DocumentImpl retained = new DocumentImpl();
    corpus.addAll(Arrays.asList(repeated, retained, repeated));

    corpus.resourceUnloaded(new CreoleEvent(repeated, 2));

    assertEquals(Collections.singletonList(retained), corpus);
    corpus.cleanup();
  }

  @Test
  public void resourceUnloadedIgnoresNonDocumentResource() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl document = new DocumentImpl();
    CorpusImpl otherResource = new CorpusImpl();
    corpus.add(document);

    corpus.resourceUnloaded(new CreoleEvent(otherResource, 2));

    assertEquals(Collections.singletonList(document), corpus);
    corpus.cleanup();
    otherResource.cleanup();
  }

  @Test
  public void duplicateListenerRegistrationDoesNotDuplicateNotifications() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    List<CorpusEvent> events = new ArrayList<CorpusEvent>();
    CorpusListener listener = new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
        events.add(event);
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
      }
    };
    corpus.addCorpusListener(listener);
    corpus.addCorpusListener(listener);

    corpus.add(new DocumentImpl());

    assertEquals(1, events.size());
    corpus.cleanup();
  }

  @Test
  public void removedListenerReceivesNoFurtherNotifications() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    List<CorpusEvent> events = new ArrayList<CorpusEvent>();
    CorpusListener listener = new CorpusListener() {
      @Override
      public void documentAdded(CorpusEvent event) {
        events.add(event);
      }

      @Override
      public void documentRemoved(CorpusEvent event) {
      }
    };
    corpus.addCorpusListener(listener);
    corpus.removeCorpusListener(listener);
    corpus.removeCorpusListener(listener);

    corpus.add(new DocumentImpl());

    assertTrue(events.isEmpty());
    corpus.cleanup();
  }

  @Test
  public void informationalCreoleCallbacksLeaveContentsUntouched() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    DocumentImpl document = new DocumentImpl();
    corpus.add(document);
    CreoleEvent event = new CreoleEvent(document, 1);

    corpus.resourceLoaded(event);
    corpus.resourceRenamed(document, "before", "after");
    corpus.datastoreOpened(event);
    corpus.datastoreCreated(event);
    corpus.datastoreClosed(event);

    assertEquals(Collections.singletonList(document), corpus);
    corpus.cleanup();
  }

  @Test
  public void directoryPopulateRejectsNonFileUrl() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    URL url = new URL("http://example.invalid/documents");

    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> CorpusImpl.populate(corpus, url, null, "UTF-8", false));

    assertTrue(failure.getMessage().contains("file:"));
    corpus.cleanup();
  }

  @Test
  public void directoryPopulateRejectsMissingLocation() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path parent = Files.createTempDirectory("corpus-missing-parent");
    File missing = parent.resolve("missing").toFile();

    assertThrows(FileNotFoundException.class,
        () -> CorpusImpl.populate(corpus, missing.toURI().toURL(), null, "UTF-8", false));

    corpus.cleanup();
    parent.toFile().delete();
  }

  @Test
  public void directoryPopulateRejectsRegularFile() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path file = Files.createTempFile("corpus-not-directory", ".txt");

    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> CorpusImpl.populate(corpus, file.toUri().toURL(), null, "UTF-8", false));

    assertTrue(failure.getMessage().contains("not a directory"));
    corpus.cleanup();
    Files.deleteIfExists(file);
  }

  @Test
  public void emptyDirectoryPopulationLeavesCorpusEmpty() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path directory = Files.createTempDirectory("empty-corpus-directory");

    CorpusImpl.populate(corpus, directory.toUri().toURL(), null, "UTF-8", false);

    assertTrue(corpus.isEmpty());
    corpus.cleanup();
    Files.deleteIfExists(directory);
  }

  @Test
  public void directoryPopulationFiltersAndSortsAcceptedFiles() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path directory = Files.createTempDirectory("populated-corpus-directory");
    Path beta = Files.write(directory.resolve("beta.txt"), "second".getBytes(StandardCharsets.UTF_8));
    Path alpha = Files.write(directory.resolve("alpha.txt"), "first".getBytes(StandardCharsets.UTF_8));
    Path ignored = Files.write(directory.resolve("ignored.xml"), "ignored".getBytes(StandardCharsets.UTF_8));
    FileFilter textOnly = new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.getName().endsWith(".txt");
      }
    };

    CorpusImpl.populate(corpus, directory.toUri().toURL(), textOnly, "UTF-8", "text/plain", false);

    assertEquals(2, corpus.size());
    assertTrue(corpus.getDocumentName(0).startsWith("alpha.txt_"));
    assertTrue(corpus.getDocumentName(1).startsWith("beta.txt_"));
    assertEquals("first", corpus.get(0).getContent().toString());
    assertEquals("second", corpus.get(1).getContent().toString());
    corpus.cleanup();
    Files.deleteIfExists(alpha);
    Files.deleteIfExists(beta);
    Files.deleteIfExists(ignored);
    Files.deleteIfExists(directory);
  }

  @Test
  public void recursiveDirectoryPopulationFindsNestedFiles() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path directory = Files.createTempDirectory("recursive-corpus-directory");
    Path nested = Files.createDirectory(directory.resolve("nested"));
    Path inner = Files.write(nested.resolve("inner.txt"), "inside".getBytes(StandardCharsets.UTF_8));
    Path outer = Files.write(directory.resolve("outer.txt"), "outside".getBytes(StandardCharsets.UTF_8));

    corpus.populate(directory.toUri().toURL(), null, "UTF-8", true);

    assertEquals(2, corpus.size());
    assertTrue(corpus.getDocumentNames().get(0).startsWith("inner.txt_"));
    assertTrue(corpus.getDocumentNames().get(1).startsWith("outer.txt_"));
    corpus.cleanup();
    Files.deleteIfExists(inner);
    Files.deleteIfExists(outer);
    Files.deleteIfExists(nested);
    Files.deleteIfExists(directory);
  }

  @Test
  public void concatenatedPopulationExtractsRequestedCountWithoutRootElement() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path input = Files.createTempFile("concatenated-corpus", ".txt");
    String source = "preamble\n<DOC id=\"1\">\nFirst body\n</DOC>\n<DOC>\nSecond body\n</DOC>\n";
    Files.write(input, source.getBytes(StandardCharsets.UTF_8));

    long byteCount = CorpusImpl.populate(corpus, input.toUri().toURL(), "doc", "UTF-8", 1,
        " sample ", "text/plain", false);

    assertEquals(1, corpus.size());
    assertTrue(corpus.getDocumentName(0).startsWith("sample_1_"));
    assertEquals("\nFirst body\n", corpus.get(0).getContent().toString());
    assertEquals("\nFirst body\n".getBytes().length, byteCount);
    corpus.cleanup();
    Files.deleteIfExists(input);
  }

  @Test
  public void concatenatedPopulationCanIncludeRootsAndExtractAllDocuments() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path input = Files.createTempFile("rooted-concatenated-corpus", ".txt");
    String source = "<ENTRY>one</ENTRY>\n<EnTrY class=\"x\">two</eNtRy>";
    Files.write(input, source.getBytes(StandardCharsets.UTF_8));

    long byteCount = corpus.populate(input.toUri().toURL(), "ENTRY", "", -1, null,
        "text/plain", true);

    assertEquals(2, corpus.size());
    assertEquals("<ENTRY>one</ENTRY>", corpus.get(0).getContent().toString());
    assertEquals("<EnTrY class=\"x\">two</eNtRy>", corpus.get(1).getContent().toString());
    assertTrue(corpus.getDocumentName(0).startsWith("1_"));
    assertEquals(corpus.get(0).getContent().toString().getBytes().length
        + corpus.get(1).getContent().toString().getBytes().length, byteCount);
    corpus.cleanup();
    Files.deleteIfExists(input);
  }

  @Test
  public void concatenatedPopulationWithNoMatchingElementReturnsZero() throws Exception {
    Gate.init();
    CorpusImpl corpus = new CorpusImpl();
    Path input = Files.createTempFile("unmatched-concatenated-corpus", ".txt");
    Files.write(input, "plain text only".getBytes(StandardCharsets.UTF_8));

    long byteCount = CorpusImpl.populate(corpus, input.toUri().toURL(), "doc", null, -1,
        "prefix", null, false);

    assertEquals(0L, byteCount);
    assertTrue(corpus.isEmpty());
    corpus.cleanup();
    Files.deleteIfExists(input);
  }

  @Test
  public void duplicateCreatesDistinctCorpusAndDistinctDocuments() throws Exception {
    Gate.init();
    Corpus original = (Corpus)Factory.createResource("gate.corpora.CorpusImpl");
    Document document = Factory.newDocument("duplicated content");
    document.setName("source-document");
    original.add(document);

    Resource duplicatedResource = Factory.duplicate((Resource)original);
    Corpus duplicate = (Corpus)duplicatedResource;

    assertNotSame(original, duplicate);
    assertEquals(1, duplicate.size());
    assertNotSame(document, duplicate.get(0));
    assertEquals("duplicated content", duplicate.get(0).getContent().toString());
    assertEquals("source-document", duplicate.getDocumentName(0));
    Factory.deleteResource(duplicate);
    Factory.deleteResource(original);
    Factory.deleteResource(document);
  }
}
