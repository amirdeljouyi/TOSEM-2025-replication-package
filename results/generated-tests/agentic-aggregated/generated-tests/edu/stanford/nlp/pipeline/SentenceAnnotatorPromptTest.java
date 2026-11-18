package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class SentenceAnnotatorPromptTest {

  @Test
  public void rejectsAnnotationWithoutSentences() {
    Annotation document = new Annotation("document without sentence splitting");
    AtomicInteger processed = new AtomicInteger();
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 0; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap sentence) { processed.incrementAndGet(); }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap sentence) { fail("No failure callback expected"); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    try {
      annotator.annotate(document);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("unable to find sentences"));
      assertTrue(expected.getMessage().contains("document without sentence splitting"));
    }
    assertEquals(0, processed.get());
  }

  @Test
  public void acceptsPresentButEmptySentenceList() {
    Annotation document = new Annotation("empty");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());
    AtomicInteger processed = new AtomicInteger();
    AtomicInteger failed = new AtomicInteger();
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 0; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap sentence) { processed.incrementAndGet(); }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap sentence) { failed.incrementAndGet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    annotator.annotate(document);

    assertEquals(0, processed.get());
    assertEquals(0, failed.get());
  }

  @Test
  public void sequentialModeProcessesEverySentenceInListOrderAndInPlace() {
    Annotation document = new Annotation("Alpha. Beta.");
    CoreMap first = new ArrayCoreMap();
    CoreMap second = new ArrayCoreMap();
    first.set(CoreAnnotations.TextAnnotation.class, "Alpha.");
    second.set(CoreAnnotations.TextAnnotation.class, "Beta.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(first, second));
    List<CoreMap> observed = new CopyOnWriteArrayList<>();
    AtomicReference<Annotation> observedDocument = new AtomicReference<>();
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 0; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap sentence) {
        observedDocument.set(annotation);
        observed.add(sentence);
        sentence.set(CoreAnnotations.ValueAnnotation.class, "visited-" + observed.size());
      }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap sentence) { fail("No failure callback expected"); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    annotator.annotate(document);

    assertSame(document, observedDocument.get());
    assertEquals(2, observed.size());
    assertSame(first, observed.get(0));
    assertSame(second, observed.get(1));
    assertEquals("visited-1", first.get(CoreAnnotations.ValueAnnotation.class));
    assertEquals("visited-2", second.get(CoreAnnotations.ValueAnnotation.class));
  }

  @Test
  public void sequentialModePropagatesCallbackFailureAndStopsLaterSentences() {
    Annotation document = new Annotation("first second");
    CoreMap first = new ArrayCoreMap();
    CoreMap second = new ArrayCoreMap();
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(first, second));
    AtomicInteger calls = new AtomicInteger();
    IllegalStateException problem = new IllegalStateException("deliberate failure");
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 0; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap sentence) {
        calls.incrementAndGet();
        throw problem;
      }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap sentence) { fail("No failure callback expected"); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    try {
      annotator.annotate(document);
      fail("Expected callback failure");
    } catch (IllegalStateException actual) {
      assertSame(problem, actual);
    }
    assertEquals(1, calls.get());
  }

  @Test
  public void sequentialModeHonorsAndClearsCallingThreadInterrupt() {
    Annotation document = new Annotation("sentence");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(new ArrayCoreMap()));
    AtomicInteger calls = new AtomicInteger();
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 0; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap sentence) { calls.incrementAndGet(); }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap sentence) { fail("No failure callback expected"); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    Thread.currentThread().interrupt();
    try {
      annotator.annotate(document);
      fail("Expected RuntimeInterruptedException");
    } catch (RuntimeInterruptedException expected) {
      assertFalse(Thread.currentThread().isInterrupted());
    } finally {
      Thread.interrupted();
    }
    assertEquals(0, calls.get());
  }

  @Test
  public void multiThreadedModeCanRunTwoSentenceCallbacksConcurrently() {
    Annotation document = new Annotation("one two");
    CoreMap first = new ArrayCoreMap();
    CoreMap second = new ArrayCoreMap();
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(first, second));
    CountDownLatch bothEntered = new CountDownLatch(2);
    AtomicInteger calls = new AtomicInteger();
    AtomicBoolean overlapObserved = new AtomicBoolean(true);
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 2; }
      @Override protected long maxTime() { return 0; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap sentence) {
        calls.incrementAndGet();
        bothEntered.countDown();
        try {
          if (!bothEntered.await(2, TimeUnit.SECONDS)) overlapObserved.set(false);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          overlapObserved.set(false);
        }
        sentence.set(CoreAnnotations.ValueAnnotation.class, "done");
      }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap sentence) { fail("No failure callback expected"); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    annotator.annotate(document);

    assertEquals(2, calls.get());
    assertTrue(overlapObserved.get());
    assertEquals("done", first.get(CoreAnnotations.ValueAnnotation.class));
    assertEquals("done", second.get(CoreAnnotations.ValueAnnotation.class));
  }

  @Test
  public void positiveTimeoutUsesWrapperEvenWithOneThread() {
    Annotation document = new Annotation("sentence");
    CoreMap sentence = new ArrayCoreMap();
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    AtomicReference<Thread> callbackThread = new AtomicReference<>();
    Thread callingThread = Thread.currentThread();
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 1000; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap value) {
        callbackThread.set(Thread.currentThread());
        assertSame(document, annotation);
        assertSame(sentence, value);
      }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap value) { fail("No failure callback expected"); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    annotator.annotate(document);

    assertNotNull(callbackThread.get());
    assertNotSame(callingThread, callbackThread.get());
  }

  @Test
  public void timeoutInterruptsBusyWorkerThenRetriesPendingSentence() {
    Annotation document = new Annotation("slow fast");
    CoreMap slow = new ArrayCoreMap();
    CoreMap fast = new ArrayCoreMap();
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(slow, fast));
    CountDownLatch releaseOnlyByInterrupt = new CountDownLatch(1);
    AtomicInteger calls = new AtomicInteger();
    AtomicBoolean slowWasInterrupted = new AtomicBoolean();
    AtomicInteger failures = new AtomicInteger();
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 30; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap sentence) {
        calls.incrementAndGet();
        if (sentence == slow) {
          try {
            releaseOnlyByInterrupt.await(2, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            slowWasInterrupted.set(true);
            Thread.currentThread().interrupt();
          }
        } else {
          sentence.set(CoreAnnotations.ValueAnnotation.class, "retried");
        }
      }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap sentence) { failures.incrementAndGet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };

    annotator.annotate(document);

    assertTrue(slowWasInterrupted.get());
    assertEquals(2, calls.get());
    assertEquals("retried", fast.get(CoreAnnotations.ValueAnnotation.class));
    assertEquals(0, failures.get());
  }

  @Test
  public void annotationPipelineInvokesSentenceAnnotatorAndPreservesMutations() {
    Annotation document = new Annotation("pipeline sentence");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "pipeline sentence");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    AtomicInteger calls = new AtomicInteger();
    SentenceAnnotator annotator = new SentenceAnnotator() {
      @Override protected int nThreads() { return 1; }
      @Override protected long maxTime() { return 0; }
      @Override protected void doOneSentence(Annotation annotation, CoreMap value) {
        calls.incrementAndGet();
        value.set(CoreAnnotations.ValueAnnotation.class,
            annotation.get(CoreAnnotations.TextAnnotation.class).toUpperCase());
      }
      @Override protected void doOneFailedSentence(Annotation annotation, CoreMap value) { fail("No failure callback expected"); }
      @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
      @Override public Set<Class<? extends CoreAnnotation>> requires() { return Collections.emptySet(); }
    };
    AnnotationPipeline pipeline = new AnnotationPipeline();
    pipeline.addAnnotator(annotator);

    pipeline.annotate(document);

    assertEquals(1, calls.get());
    assertEquals("PIPELINE SENTENCE", sentence.get(CoreAnnotations.ValueAnnotation.class));
  }
}
