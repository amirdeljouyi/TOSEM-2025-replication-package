package gate.corpora;

import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.event.CorpusEvent;
import gate.event.CorpusListener;
import gate.util.MethodNotImplementedException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

import static org.junit.Assert.*;

public class SerialCorpusImplPromptTest {

  @Test
  public void transientSourceCopiesCorpusMetadataAndDocuments() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source corpus");
    source.setFeatures(Factory.newFeatureMap());
    source.getFeatures().put("domain", "news");
    DocumentImpl first = new DocumentImpl();
    first.setName("first");
    DocumentImpl second = new DocumentImpl();
    second.setName("second");
    source.add(first);
    source.add(second);
    SerialCorpusImpl corpus = new SerialCorpusImpl();

    corpus.setTransientSource(source);

    assertEquals("source corpus", corpus.getName());
    assertSame(source.getFeatures(), corpus.getFeatures());
    assertEquals(Arrays.asList("first", "second"), corpus.getDocumentNames());
    assertEquals(Arrays.asList(null, null), corpus.getDocumentPersistentIDs());
    assertEquals(Arrays.asList(DocumentImpl.class.getName(), DocumentImpl.class.getName()),
        corpus.getDocumentClassTypes());
    assertSame(first, corpus.get(0));
    assertSame(second, corpus.get(1));
    assertNull(corpus.getTransientSource());
  }

  @Test
  public void addAndIndexedAddMaintainListOrderAndMetadata() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);
    DocumentImpl last = new DocumentImpl();
    last.setName("last");
    last.setLRPersistenceId("id-last");
    DocumentImpl first = new DocumentImpl();
    first.setName("first");
    first.setLRPersistenceId("id-first");

    assertTrue(corpus.add(last));
    corpus.add(0, first);

    assertEquals(2, corpus.size());
    assertEquals(Arrays.asList("first", "last"), corpus.getDocumentNames());
    assertEquals(Arrays.asList("id-first", "id-last"), corpus.getDocumentPersistentIDs());
    assertSame(first, corpus.get(0));
    assertSame(last, corpus.get(1));
    assertTrue(corpus.contains(first));
    assertEquals(1, corpus.indexOf(last));
  }

  @Test
  public void nullAndNonDocumentInputsAreRejectedWithoutMutation() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    assertFalse(corpus.add((Document)null));
    corpus.add(0, (Document)null);
    assertFalse(corpus.contains(null));
    assertFalse(corpus.contains("not a document"));
    assertFalse(corpus.remove(null));
    assertFalse(corpus.remove("not a document"));
    assertEquals(-1, corpus.indexOf("not a document"));
    assertTrue(corpus.isEmpty());
  }

  @Test
  public void metadataAccessAndPersistentIdUpdateHandleUpperBounds() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl doc = new DocumentImpl();
    doc.setName("doc");
    source.add(doc);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    corpus.setDocumentPersistentID(0, "persisted");
    corpus.setDocumentPersistentID(4, "ignored");

    assertEquals("doc", corpus.getDocumentName(0));
    assertEquals("persisted", corpus.getDocumentPersistentID(0));
    assertEquals(DocumentImpl.class.getName(), corpus.getDocumentClassType(0));
    assertEquals("No such document", corpus.getDocumentName(1));
    assertNull(corpus.getDocumentPersistentID(1));
    assertNull(corpus.getDocumentClassType(1));
    assertNull(corpus.get(1));
  }

  @Test
  public void unloadWithoutSyncDropsOnlyInMemoryDocument() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl doc = new DocumentImpl();
    doc.setName("doc");
    source.add(doc);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    assertTrue(corpus.isDocumentLoaded(0));
    assertFalse(corpus.isPersistentDocument(0));
    corpus.unloadDocument(0, false);

    assertFalse(corpus.isDocumentLoaded(0));
    assertFalse(corpus.isPersistentDocument(0));
    assertEquals(1, corpus.size());
    assertEquals("doc", corpus.getDocumentName(0));
  }

  @Test
  public void unloadByDocumentIgnoresAbsentDocument() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl member = new DocumentImpl();
    member.setName("member");
    source.add(member);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);
    DocumentImpl absent = new DocumentImpl();
    absent.setName("absent");

    corpus.unloadDocument(absent, false);

    assertTrue(corpus.isDocumentLoaded(0));
    assertSame(member, corpus.get(0));
  }

  @Test
  public void equivalentPersistentDocumentCanBeFoundAfterUnload() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl original = new DocumentImpl();
    original.setName("same");
    original.setLRPersistenceId("doc-id");
    source.add(original);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);
    corpus.setDocumentPersistentID(0, "doc-id");
    corpus.unloadDocument(0, false);
    DocumentImpl equivalent = new DocumentImpl();
    equivalent.setName("same");
    equivalent.setLRPersistenceId("doc-id");

    assertEquals(0, corpus.findDocument(equivalent));
    assertTrue(corpus.contains(equivalent));
    assertEquals(0, corpus.indexOf(equivalent));
  }

  @Test
  public void removeByObjectUpdatesMetadataAndReturnsExpectedStatus() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl first = new DocumentImpl();
    first.setName("first");
    DocumentImpl second = new DocumentImpl();
    second.setName("second");
    source.add(first);
    source.add(second);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    assertTrue(corpus.remove(first));
    assertFalse(corpus.remove(first));

    assertEquals(1, corpus.size());
    assertSame(second, corpus.get(0));
    assertEquals(Collections.singletonList("second"), corpus.getDocumentNames());
  }

  @Test
  public void removeByIndexReturnsLoadedDocumentButNullForUnloadedOne() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl loaded = new DocumentImpl();
    loaded.setName("loaded");
    DocumentImpl unloaded = new DocumentImpl();
    unloaded.setName("unloaded");
    source.add(loaded);
    source.add(unloaded);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);
    corpus.unloadDocument(1, false);

    assertNull(corpus.remove(1));
    assertSame(loaded, corpus.remove(0));
    assertTrue(corpus.isEmpty());
  }

  @Test
  public void corpusListenersReceiveAddAndRemoveEventsAndCanBeDetached() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);
    AtomicInteger additions = new AtomicInteger();
    AtomicInteger removals = new AtomicInteger();
    AtomicReference<CorpusEvent> latest = new AtomicReference<CorpusEvent>();
    CorpusListener listener = (CorpusListener)Proxy.newProxyInstance(
        CorpusListener.class.getClassLoader(), new Class<?>[] {CorpusListener.class},
        (proxy, method, args) -> {
          if("equals".equals(method.getName())) return proxy == args[0];
          if("hashCode".equals(method.getName())) return System.identityHashCode(proxy);
          if("toString".equals(method.getName())) return "recording corpus listener";
          latest.set((CorpusEvent)args[0]);
          if("documentAdded".equals(method.getName())) additions.incrementAndGet();
          if("documentRemoved".equals(method.getName())) removals.incrementAndGet();
          return null;
        });
    DocumentImpl first = new DocumentImpl();
    first.setName("first");
    first.setLRPersistenceId("first-id");
    DocumentImpl second = new DocumentImpl();
    second.setName("second");
    corpus.addCorpusListener(listener);
    corpus.addCorpusListener(listener);

    corpus.add(first);
    assertEquals(1, additions.get());
    assertSame(first, latest.get().getDocument());
    assertEquals(0, latest.get().getDocumentIndex());
    assertEquals("first-id", latest.get().getDocumentLRID());
    corpus.remove(first);
    assertEquals(1, removals.get());
    corpus.removeCorpusListener(listener);
    corpus.add(second);
    assertEquals(1, additions.get());
  }

  @Test
  public void iteratorLoadsInOrderAndDisallowsRemoval() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl first = new DocumentImpl();
    first.setName("first");
    DocumentImpl second = new DocumentImpl();
    second.setName("second");
    source.add(first);
    source.add(second);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    Iterator<Document> iterator = corpus.iterator();
    assertTrue(iterator.hasNext());
    assertSame(first, iterator.next());
    assertTrue(iterator.hasNext());
    assertSame(second, iterator.next());
    assertFalse(iterator.hasNext());
    try {
      iterator.remove();
      fail("iterator removal should be unsupported");
    } catch(UnsupportedOperationException expected) {
      assertTrue(expected.getMessage().contains("does not support remove"));
    }
  }

  @Test
  public void bulkOperationsReportPartialFailuresPrecisely() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);
    DocumentImpl first = new DocumentImpl();
    first.setName("first");
    DocumentImpl second = new DocumentImpl();
    second.setName("second");

    assertFalse(corpus.addAll(Arrays.asList(first, null, second)));
    assertTrue(corpus.containsAll(Arrays.asList(first, second)));
    assertFalse(corpus.containsAll(Arrays.asList(first, new DocumentImpl())));
    assertFalse(corpus.removeAll(Arrays.asList(first, new DocumentImpl())));
    assertEquals(1, corpus.size());
    assertSame(second, corpus.get(0));
    assertTrue(corpus.removeAll(Collections.singletonList(second)));
    assertTrue(corpus.isEmpty());
  }

  @Test
  public void clearRemovesDocumentsAndAllCachedMetadata() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl doc = new DocumentImpl();
    doc.setName("doc");
    source.add(doc);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    corpus.clear();

    assertTrue(corpus.isEmpty());
    assertEquals(Collections.emptyList(), corpus.getDocumentNames());
    assertEquals(Collections.emptyList(), corpus.getDocumentPersistentIDs());
    assertEquals(Collections.emptyList(), corpus.getDocumentClassTypes());
  }

  @Test
  public void equalityIsReflexiveButSeparateMetadataEntriesAreNotEqual() throws Exception {
    Gate.init();
    CorpusImpl sourceOne = new CorpusImpl();
    sourceOne.setName("equal");
    sourceOne.setFeatures(Factory.newFeatureMap());
    DocumentImpl doc = new DocumentImpl();
    doc.setName("doc");
    sourceOne.add(doc);
    CorpusImpl sourceTwo = new CorpusImpl();
    sourceTwo.setName("equal");
    sourceTwo.setFeatures(Factory.newFeatureMap());
    sourceTwo.add(doc);
    SerialCorpusImpl first = new SerialCorpusImpl();
    first.setTransientSource(sourceOne);
    SerialCorpusImpl second = new SerialCorpusImpl();
    second.setTransientSource(sourceTwo);

    assertEquals(first, first);
    assertNotEquals(first, second);
    assertNotEquals(first, null);
    assertNotEquals(first, sourceOne);
    second.setName("different");
    assertNotEquals(first, second);
  }

  @Test
  public void unsupportedListOperationsThrowTheirDocumentedExceptions() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);
    DocumentImpl doc = new DocumentImpl();
    doc.setName("doc");

    try {
      corpus.toArray();
      fail("toArray should be unimplemented");
    } catch(MethodNotImplementedException expected) {
      assertTrue(expected.getMessage().contains("toArray()"));
    }
    try {
      corpus.toArray(new Document[0]);
      fail("typed toArray should be unimplemented");
    } catch(MethodNotImplementedException expected) {
      assertTrue(expected.getMessage().contains("toArray(Object[] a)"));
    }
    try {
      corpus.set(0, doc);
      fail("set should be unimplemented");
    } catch(MethodNotImplementedException expected) {
      assertNotNull(expected);
    }
    try {
      corpus.addAll(0, Collections.singletonList(doc));
      fail("indexed addAll should be unsupported");
    } catch(UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
    try {
      corpus.retainAll(Collections.singletonList(doc));
      fail("retainAll should be unsupported");
    } catch(UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void navigationMethodsExplicitlyRemainUnimplemented() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    try {
      corpus.lastIndexOf(new DocumentImpl());
      fail("lastIndexOf should be unimplemented");
    } catch(MethodNotImplementedException expected) {
      assertNotNull(expected);
    }
    try {
      ListIterator<Document> ignored = corpus.listIterator();
      fail("listIterator should be unimplemented: " + ignored);
    } catch(MethodNotImplementedException expected) {
      assertNotNull(expected);
    }
    try {
      ListIterator<Document> ignored = corpus.listIterator(0);
      fail("indexed listIterator should be unimplemented: " + ignored);
    } catch(MethodNotImplementedException expected) {
      assertNotNull(expected);
    }
    try {
      List<Document> ignored = corpus.subList(0, 0);
      fail("subList should be unimplemented: " + ignored);
    } catch(MethodNotImplementedException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void duplicateIsRejectedWithResourceTypeInMessage() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    try {
      corpus.duplicate(null);
      fail("persistent corpus duplication should fail");
    } catch(Exception expected) {
      assertTrue(expected.getMessage().contains(SerialCorpusImpl.class.getName()));
      assertTrue(expected.getMessage().contains("not permitted"));
    }
  }

  @Test
  public void indexMetadataDefaultsToNullAndCanBeStoredAsFeatures() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    assertNull(corpus.getIndexDefinition());
    assertNull(corpus.getIndexManager());
    assertNull(corpus.getIndexStatistics());
    corpus.setIndexDefinition(null);
    assertNull(corpus.getIndexDefinition());
    assertNull(corpus.getIndexManager());
  }

  @Test
  public void toStringExposesDocumentMetadataAndLoadState() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl doc = new DocumentImpl();
    doc.setName("visible-name");
    source.add(doc);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    String text = corpus.toString();

    assertTrue(text.startsWith("document data "));
    assertTrue(text.contains("visible-name"));
    assertTrue(text.contains(" documents "));
  }

  @Test
  public void cleanupEmptiesAnInitializedCorpus() throws Exception {
    Gate.init();
    CorpusImpl source = new CorpusImpl();
    source.setName("source");
    source.setFeatures(Factory.newFeatureMap());
    DocumentImpl doc = new DocumentImpl();
    doc.setName("doc");
    source.add(doc);
    SerialCorpusImpl corpus = new SerialCorpusImpl();
    corpus.setTransientSource(source);

    corpus.cleanup();

    assertEquals(0, corpus.size());
    assertEquals(Collections.emptyList(), corpus.getDocumentNames());
  }
}
