package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class POSTaggerAnnotatorPromptTest {

  @Test
  public void requiresReportsCompleteInputContract() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(new MaxentTagger());

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertEquals(5, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
  }

  @Test
  public void requiresReturnsUnmodifiableSet() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(new MaxentTagger());
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertThrows(UnsupportedOperationException.class,
        () -> requirements.remove(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void requirementsSatisfiedContainsOnlyPartOfSpeech() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(new MaxentTagger());

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(Collections.singleton(CoreAnnotations.PartOfSpeechAnnotation.class), satisfied);
    assertThrows(UnsupportedOperationException.class,
        () -> satisfied.add(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void annotateRejectsAnnotationWithoutSentences() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(new MaxentTagger());
    Annotation document = new Annotation("words without sentence structure");

    RuntimeException failure = assertThrows(RuntimeException.class,
        () -> annotator.annotate(document));

    assertTrue(failure.getMessage().contains("unable to find words/tokens"));
    assertTrue(failure.getMessage().contains("words without sentence structure"));
  }

  @Test
  public void annotateAcceptsDocumentWithNoSentences() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(new MaxentTagger());
    Annotation document = new Annotation(Collections.emptyList());

    annotator.annotate(document);

    assertTrue(document.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
    assertTrue(document.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }

  @Test
  public void sentenceLongerThanMaximumGetsFallbackTagsWithoutCallingModel() {
    AtomicBoolean called = new AtomicBoolean(false);
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        called.set(true);
        return Arrays.asList(new TaggedWord("One", "CD"), new TaggedWord("runs", "VBZ"));
      }
    };
    CoreLabel first = CoreLabel.wordFromString("One");
    CoreLabel second = CoreLabel.wordFromString("runs");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(model, 1, 1);

    annotator.annotate(document);

    assertFalse(called.get());
    assertEquals("X", first.tag());
    assertEquals("X", second.tag());
  }

  @Test
  public void sentenceAtMaximumLengthIsTaggedByModel() {
    AtomicBoolean called = new AtomicBoolean(false);
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        called.set(true);
        return Arrays.asList(new TaggedWord(sentence.get(0).word(), "NNP"),
            new TaggedWord(sentence.get(1).word(), "VBZ"));
      }
    };
    CoreLabel first = CoreLabel.wordFromString("Ada");
    CoreLabel second = CoreLabel.wordFromString("codes");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(model, 2, 1);

    annotator.annotate(document);

    assertTrue(called.get());
    assertEquals("NNP", first.tag());
    assertEquals("VBZ", second.tag());
  }

  @Test
  public void modelTagsReplaceExistingTokenTags() {
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "NNS"));
      }
    };
    CoreLabel token = CoreLabel.wordFromString("tests");
    token.setTag("OLD");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new POSTaggerAnnotator(model).annotate(document);

    assertEquals("NNS", token.tag());
    assertSame(token, document.get(CoreAnnotations.TokensAnnotation.class).get(0));
  }

  @Test
  public void modelReturningNullProducesFallbackTag() {
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        return null;
      }
    };
    CoreLabel token = CoreLabel.wordFromString("mystery");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new POSTaggerAnnotator(model).annotate(document);

    assertEquals("X", token.tag());
  }

  @Test
  public void outOfMemoryFromModelIsRecoveredWithFallbackTags() {
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        throw new OutOfMemoryError("synthetic test exhaustion");
      }
    };
    CoreLabel first = CoreLabel.wordFromString("keep");
    CoreLabel second = CoreLabel.wordFromString("going");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new POSTaggerAnnotator(model).annotate(document);

    assertEquals("X", first.tag());
    assertEquals("X", second.tag());
  }

  @Test
  public void directModelConstructorDoesNotRequestTagReuse() {
    AtomicBoolean observedReuse = new AtomicBoolean(true);
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        observedReuse.set(reuseTags);
        return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "JJ"));
      }
    };
    CoreLabel token = CoreLabel.wordFromString("clear");
    token.setTag("PRIOR");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new POSTaggerAnnotator(model, 10, 1).annotate(document);

    assertFalse(observedReuse.get());
    assertEquals("JJ", token.tag());
  }

  @Test
  public void multipleSentencesAreTaggedIndependentlyInOrder() {
    AtomicInteger calls = new AtomicInteger();
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        int invocation = calls.incrementAndGet();
        return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "T" + invocation));
      }
    };
    CoreLabel first = CoreLabel.wordFromString("First");
    CoreLabel second = CoreLabel.wordFromString("Second");
    CoreMap firstSentence = new ArrayCoreMap();
    CoreMap secondSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TextAnnotation.class, "First");
    secondSentence.set(CoreAnnotations.TextAnnotation.class, "Second");
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(first));
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(second));
    Annotation document = new Annotation(Arrays.asList(firstSentence, secondSentence));

    new POSTaggerAnnotator(model, 10, 1).annotate(document);

    assertEquals(2, calls.get());
    assertEquals("T1", first.tag());
    assertEquals("T2", second.tag());
    assertEquals("FirstSecond", document.toString());
  }

  @Test
  public void multithreadedPathProcessesEverySentence() {
    AtomicInteger calls = new AtomicInteger();
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        calls.incrementAndGet();
        return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "THREAD"));
      }
    };
    CoreLabel first = CoreLabel.wordFromString("alpha");
    CoreLabel second = CoreLabel.wordFromString("beta");
    CoreLabel third = CoreLabel.wordFromString("gamma");
    CoreMap firstSentence = new ArrayCoreMap();
    CoreMap secondSentence = new ArrayCoreMap();
    CoreMap thirdSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(first));
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(second));
    thirdSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(third));
    Annotation document = new Annotation(Arrays.asList(firstSentence, secondSentence, thirdSentence));

    new POSTaggerAnnotator(model, 10, 2).annotate(document);

    assertEquals(3, calls.get());
    assertEquals("THREAD", first.tag());
    assertEquals("THREAD", second.tag());
    assertEquals("THREAD", third.tag());
  }

  @Test
  public void mixedLengthDocumentTagsShortSentenceAndSkipsLongSentence() {
    AtomicInteger calls = new AtomicInteger();
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        calls.incrementAndGet();
        return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "SHORT"));
      }
    };
    CoreLabel shortToken = CoreLabel.wordFromString("yes");
    CoreLabel longFirst = CoreLabel.wordFromString("too");
    CoreLabel longSecond = CoreLabel.wordFromString("long");
    CoreMap shortSentence = new ArrayCoreMap();
    CoreMap longSentence = new ArrayCoreMap();
    shortSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(shortToken));
    longSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(longFirst, longSecond));
    Annotation document = new Annotation(Arrays.asList(shortSentence, longSentence));

    new POSTaggerAnnotator(model, 1, 1).annotate(document);

    assertEquals(1, calls.get());
    assertEquals("SHORT", shortToken.tag());
    assertEquals("X", longFirst.tag());
    assertEquals("X", longSecond.tag());
  }

  @Test
  public void missingTokensOnSentenceFailsFast() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "tokenless");
    Annotation document = new Annotation("document");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(new MaxentTagger());

    assertThrows(NullPointerException.class, () -> annotator.annotate(document));
  }

  @Test
  public void tooFewModelResultsExposeInconsistentModelOutput() {
    MaxentTagger model = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence,
                                           boolean reuseTags) {
        return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "FIRST"));
      }
    };
    CoreLabel first = CoreLabel.wordFromString("one");
    CoreLabel second = CoreLabel.wordFromString("two");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    assertThrows(IndexOutOfBoundsException.class,
        () -> new POSTaggerAnnotator(model).annotate(document));
    assertEquals("FIRST", first.tag());
    assertNull(second.tag());
  }

  @Test
  public void nullModelFailsWhenTaggingIsRequired() {
    CoreLabel token = CoreLabel.wordFromString("word");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    POSTaggerAnnotator annotator = new POSTaggerAnnotator((MaxentTagger) null);

    assertThrows(NullPointerException.class, () -> annotator.annotate(document));
    assertNull(token.tag());
  }
}
