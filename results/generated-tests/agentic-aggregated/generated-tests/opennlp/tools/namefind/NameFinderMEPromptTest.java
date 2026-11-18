package opennlp.tools.namefind;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class NameFinderMEPromptTest {

  @Test
  public void extractsTypesFromTypedOutcomes() {
    assertEquals("person", NameFinderME.extractNameType("person-start"));
    assertEquals("organization", NameFinderME.extractNameType("organization-cont"));
    assertEquals("medical-device", NameFinderME.extractNameType("medical-device-start"));
  }

  @Test
  public void returnsNullForUntypedOrMalformedOutcomes() {
    assertNull(NameFinderME.extractNameType("other"));
    assertNull(NameFinderME.extractNameType("start"));
    assertNull(NameFinderME.extractNameType("-start"));
    assertNull(NameFinderME.extractNameType("person-"));
  }

  @Test
  public void dropsNoSpansFromEmptyInput() {
    Span[] input = new Span[0];
    Span[] result = NameFinderME.dropOverlappingSpans(input);

    assertEquals(0, result.length);
    assertNotSame(input, result);
  }

  @Test
  public void sortsAndPreservesDisjointAndAdjacentSpans() {
    Span late = new Span(7, 9, "place");
    Span first = new Span(0, 2, "person");
    Span adjacent = new Span(2, 5, "organization");

    Span[] result = NameFinderME.dropOverlappingSpans(
        new Span[] {late, adjacent, first});

    assertArrayEquals(new Span[] {first, adjacent, late}, result);
  }

  @Test
  public void keepsOuterSpanAndDropsContainedSpans() {
    Span contained = new Span(2, 4, "short");
    Span outer = new Span(1, 7, "outer");
    Span after = new Span(8, 10, "after");

    Span[] result = NameFinderME.dropOverlappingSpans(
        new Span[] {contained, after, outer});

    assertArrayEquals(new Span[] {outer, after}, result);
  }

  @Test
  public void keepsFirstSortedSpanAcrossAChainOfCrossingSpans() {
    Span first = new Span(0, 4, "first");
    Span crossingFirst = new Span(3, 7, "second");
    Span crossingOnlyRemovedSpan = new Span(6, 9, "third");

    Span[] result = NameFinderME.dropOverlappingSpans(
        new Span[] {crossingOnlyRemovedSpan, crossingFirst, first});

    assertArrayEquals(new Span[] {first, crossingOnlyRemovedSpan}, result);
  }

  @Test
  public void resolvesIdenticalOffsetsUsingSpanSortOrder() {
    Span zType = new Span(1, 3, "zeta");
    Span aType = new Span(1, 3, "alpha");

    Span[] result = NameFinderME.dropOverlappingSpans(new Span[] {zType, aType});

    assertArrayEquals(new Span[] {aType}, result);
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullSpanArray() {
    NameFinderME.dropOverlappingSpans(null);
  }

  @Test
  public void findsTypedSpanAndAttachesMeanProbability() {
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        Sequence answer = new Sequence();
        answer.add("person-start", 0.8);
        answer.add("person-cont", 0.6);
        answer.add("other", 0.9);
        return answer;
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() {
        return new String[] {"person-start", "person-cont", "other"};
      }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);

    Span[] spans = finder.find(new String[] {"Ada", "Lovelace", "wrote"});

    assertEquals(1, spans.length);
    assertEquals(0, spans[0].getStart());
    assertEquals(2, spans[0].getEnd());
    assertEquals("person", spans[0].getType());
    assertEquals(0.7, spans[0].getProb(), 0.0000001);
    assertArrayEquals(new double[] {0.8, 0.6, 0.9}, finder.probs(), 0.0000001);
    double[] copied = new double[4];
    finder.probs(copied);
    assertArrayEquals(new double[] {0.8, 0.6, 0.9, 0.0}, copied, 0.0000001);
  }

  @Test
  public void decodesMultipleNamesAndComputesIndependentMeans() {
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        Sequence answer = new Sequence();
        answer.add("person-start", 0.9);
        answer.add("other", 0.4);
        answer.add("place-start", 0.3);
        answer.add("place-cont", 0.7);
        return answer;
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() {
        return new String[] {"person-start", "place-start", "place-cont", "other"};
      }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);

    Span[] spans = finder.find(new String[] {"Ada", "visited", "New", "York"});

    assertEquals(2, spans.length);
    assertEquals(new Span(0, 1, "person"), spans[0]);
    assertEquals(0.9, spans[0].getProb(), 0.0000001);
    assertEquals(new Span(2, 4, "place"), spans[1]);
    assertEquals(0.5, spans[1].getProb(), 0.0000001);
    assertArrayEquals(new double[] {0.9, 0.5}, finder.probs(spans), 0.0000001);
  }

  @Test
  public void forwardsTokensAndAdditionalContextToSequenceModel() {
    AtomicReference<String[]> seenTokens = new AtomicReference<>();
    AtomicReference<Object[]> seenContext = new AtomicReference<>();
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        seenTokens.set(sequence);
        seenContext.set(context);
        Sequence answer = new Sequence();
        answer.add("other", 0.75);
        return answer;
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() {
        return new String[] {"entity-start", "other"};
      }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);
    String[] tokens = new String[] {"token"};
    String[][] context = new String[][] {{"document=7"}, {"section=intro"}};

    Span[] spans = finder.find(tokens, context);

    assertEquals(0, spans.length);
    assertSame(tokens, seenTokens.get());
    assertSame(context, seenContext.get());
  }

  @Test
  public void noContextOverloadSuppliesEmptyContext() {
    AtomicReference<Object[]> seenContext = new AtomicReference<>();
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        seenContext.set(context);
        return new Sequence();
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() { return new String[] {"entity-start", "other"}; }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);

    Span[] spans = finder.find(new String[0]);

    assertEquals(0, spans.length);
    assertEquals(0, seenContext.get().length);
    assertEquals(0, finder.probs().length);
  }

  @Test
  public void laterFindReplacesEarlierProbabilitySequence() {
    AtomicInteger calls = new AtomicInteger();
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        Sequence answer = new Sequence();
        if (calls.getAndIncrement() == 0) {
          answer.add("entity-start", 0.2);
          answer.add("other", 0.3);
        } else {
          answer.add("other", 0.95);
        }
        return answer;
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() { return new String[] {"entity-start", "other"}; }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);

    finder.find(new String[] {"first", "call"});
    assertArrayEquals(new double[] {0.2, 0.3}, finder.probs(), 0.0000001);
    finder.find(new String[] {"second"});

    assertArrayEquals(new double[] {0.95}, finder.probs(), 0.0000001);
  }

  @Test
  public void supportsFindClearAndFindAdaptiveLifecycle() {
    AtomicInteger calls = new AtomicInteger();
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        calls.incrementAndGet();
        Sequence answer = new Sequence();
        answer.add("entity-start", 0.88);
        return answer;
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() { return new String[] {"entity-start", "other"}; }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);

    Span[] beforeClear = finder.find(new String[] {"OpenNLP"});
    finder.clearAdaptiveData();
    Span[] afterClear = finder.find(new String[] {"Apache"});

    assertEquals(2, calls.get());
    assertArrayEquals(new Span[] {new Span(0, 1, "entity")}, beforeClear);
    assertArrayEquals(new Span[] {new Span(0, 1, "entity")}, afterClear);
    assertEquals(0.88, afterClear[0].getProb(), 0.0000001);
  }

  @Test(expected = NullPointerException.class)
  public void probabilitiesAreUnavailableBeforeFirstFind() {
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        return new Sequence();
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() { return new String[] {"entity-start", "other"}; }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);

    finder.probs();
  }

  @Test
  public void spanProbabilitySupportsSubspansAndZeroLengthSpan() {
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        Sequence answer = new Sequence();
        answer.add("entity-start", 0.2);
        answer.add("entity-cont", 0.4);
        answer.add("entity-cont", 0.9);
        return answer;
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() {
        return new String[] {"entity-start", "entity-cont", "other"};
      }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);
    finder.find(new String[] {"one", "two", "three"});

    double[] probabilities = finder.probs(
        new Span[] {new Span(1, 3), new Span(2, 2)});

    assertEquals(0.65, probabilities[0], 0.0000001);
    assertTrue(Double.isNaN(probabilities[1]));
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void rejectsSpanOutsideLastSequenceWhenComputingProbability() {
    SequenceClassificationModel<String> classifier = new SequenceClassificationModel<String>() {
      @Override public Sequence bestSequence(String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator, SequenceValidator<String> validator) {
        Sequence answer = new Sequence();
        answer.add("other", 0.8);
        return answer;
      }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          double minimum, BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public Sequence[] bestSequences(int count, String[] sequence, Object[] context,
          BeamSearchContextGenerator<String> generator,
          SequenceValidator<String> validator) { return new Sequence[0]; }
      @Override public String[] getOutcomes() { return new String[] {"entity-start", "other"}; }
    };
    TokenNameFinderModel model = new TokenNameFinderModel("en", classifier, null,
        Collections.emptyMap(), Collections.emptyMap(), new BioCodec(),
        new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(model);
    finder.find(new String[] {"one"});

    finder.probs(new Span[] {new Span(0, 2)});
  }

  @Test
  public void trainingAddsDefaultsBeforeRejectingUnknownTrainer() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put(TrainingParameters.ALGORITHM_PARAM, "no.such.Trainer");

    try {
      NameFinderME.train("en", "person",
          new CollectionObjectStream<NameSample>(Collections.emptyList()), parameters,
          new TokenNameFinderFactory());
      fail("An unknown trainer must be rejected");
    } catch (IllegalStateException expected) {
      assertEquals("Unexpected trainer type!", expected.getMessage());
    }

    assertEquals("no.such.Trainer",
        parameters.getStringParameter(TrainingParameters.ALGORITHM_PARAM, null));
    assertEquals(0, parameters.getIntParameter(TrainingParameters.CUTOFF_PARAM, -1));
    assertEquals(300, parameters.getIntParameter(TrainingParameters.ITERATIONS_PARAM, -1));
  }

  @Test
  public void trainingPreservesCallerSuppliedValuesBeforeFailure() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put(TrainingParameters.ALGORITHM_PARAM, "missing.Trainer");
    parameters.put(TrainingParameters.CUTOFF_PARAM, 9);
    parameters.put(TrainingParameters.ITERATIONS_PARAM, 17);

    try {
      NameFinderME.train("en", null,
          new CollectionObjectStream<NameSample>(Collections.emptyList()), parameters,
          new TokenNameFinderFactory());
      fail("An unknown trainer must be rejected");
    } catch (IllegalStateException expected) {
      assertEquals("Unexpected trainer type!", expected.getMessage());
    }

    assertEquals(9, parameters.getIntParameter(TrainingParameters.CUTOFF_PARAM, -1));
    assertEquals(17, parameters.getIntParameter(TrainingParameters.ITERATIONS_PARAM, -1));
  }

  @Test
  public void trainsSmallModelAndUsesItAcrossSentences() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put(TrainingParameters.ITERATIONS_PARAM, 5);
    parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
    NameSample first = new NameSample(
        new String[] {"Alice", "works"}, new Span[] {new Span(0, 1, "person")}, true);
    NameSample second = new NameSample(
        new String[] {"Bob", "runs"}, new Span[] {new Span(0, 1, "person")}, true);
    NameSample third = new NameSample(
        new String[] {"rain", "falls"}, new Span[0], true);

    TokenNameFinderModel trained = NameFinderME.train("en", "person",
        new CollectionObjectStream<NameSample>(Arrays.asList(first, second, third)),
        parameters, new TokenNameFinderFactory());
    NameFinderME finder = new NameFinderME(trained);

    Span[] firstResult = finder.find(new String[] {"Alice", "works"});
    finder.clearAdaptiveData();
    Span[] secondResult = finder.find(new String[] {"rain", "falls"});

    assertEquals("en", trained.getLanguage());
    assertEquals(2, finder.probs().length);
    assertNotNull(firstResult);
    assertNotNull(secondResult);
    assertTrue(finder.probs()[0] >= 0.0);
    assertTrue(finder.probs()[0] <= 1.0);
    assertTrue(finder.probs()[1] >= 0.0);
    assertTrue(finder.probs()[1] <= 1.0);
  }
}
