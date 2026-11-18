package opennlp.tools.chunker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import opennlp.tools.ml.maxent.GISModel;
import opennlp.tools.ml.model.Context;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChunkerMEPromptTest {

  @Test
  public void chunkReturnsOneOutcomePerToken() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    String[] actual = chunker.chunk(
        new String[] {"A", "small", "test", "."},
        new String[] {"DT", "JJ", "NN", "."});

    assertArrayEquals(new String[] {"O", "O", "O", "O"}, actual);
  }

  @Test
  public void chunkHandlesEmptySentence() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    String[] actual = chunker.chunk(new String[0], new String[0]);

    assertArrayEquals(new String[0], actual);
    assertArrayEquals(new double[0], chunker.probs(), 0.0);
  }

  @Test
  public void chunkIgnoresSurplusTagsAfterMatchingAllTokens() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    String[] actual = chunker.chunk(new String[] {"word"},
        new String[] {"NN", "SURPLUS"});

    assertArrayEquals(new String[] {"O"}, actual);
  }

  @Test
  public void chunkRejectsTooFewTags() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    try {
      chunker.chunk(new String[] {"one", "two"}, new String[] {"CD"});
      fail("Expected a tag lookup past the end of the array");
    } catch (ArrayIndexOutOfBoundsException expected) {
      assertTrue(expected.getMessage() == null || !expected.getMessage().isEmpty());
    }
  }

  @Test
  public void chunkRejectsNullTokens() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    try {
      chunker.chunk(null, new String[0]);
      fail("Expected null tokens to be rejected");
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void chunkAsSpansReturnsNoSpansForOutsidePredictions() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    Span[] spans = chunker.chunkAsSpans(
        new String[] {"No", "phrase", "."},
        new String[] {"DT", "NN", "."});

    assertArrayEquals(new Span[0], spans);
    assertArrayEquals(new double[] {1.0, 1.0, 1.0}, chunker.probs(), 0.0);
  }

  @Test
  public void chunkAsSpansConvertsEveryBeginPrediction() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"B-NP"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    Span[] spans = chunker.chunkAsSpans(
        new String[] {"Red", "cars", "arrived"},
        new String[] {"JJ", "NNS", "VBD"});

    assertEquals(3, spans.length);
    assertEquals(new Span(0, 1, "NP"), spans[0]);
    assertEquals(new Span(1, 2, "NP"), spans[1]);
    assertEquals(new Span(2, 3, "NP"), spans[2]);
  }

  @Test
  public void probsBeforeChunkFailsBecauseNoSequenceWasDecoded() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    try {
      chunker.probs();
      fail("Expected probabilities to require a prior chunk call");
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void probsCopiesIntoCallerArrayWithoutChangingUnusedCells() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);
    chunker.chunk(new String[] {"first", "second"}, new String[] {"NN", "NN"});
    double[] destination = new double[] {-1.0, -1.0, 73.0};

    chunker.probs(destination);

    assertArrayEquals(new double[] {1.0, 1.0, 73.0}, destination, 0.0);
  }

  @Test
  public void laterChunkCallReplacesProbabilityState() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);
    chunker.chunk(new String[] {"one", "two", "three"},
        new String[] {"CD", "CD", "CD"});
    assertArrayEquals(new double[] {1.0, 1.0, 1.0}, chunker.probs(), 0.0);

    chunker.chunk(new String[] {"replacement"}, new String[] {"NN"});

    assertArrayEquals(new double[] {1.0}, chunker.probs(), 0.0);
  }

  @Test
  public void topKSequencesReturnsDeterministicCandidate() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    Sequence[] sequences = chunker.topKSequences(
        new String[] {"one", "two"}, new String[] {"CD", "CD"});

    assertEquals(1, sequences.length);
    assertEquals(Arrays.asList("O", "O"), sequences[0].getOutcomes());
    assertArrayEquals(new double[] {1.0, 1.0}, sequences[0].getProbs(), 0.0);
    assertEquals(0.0, sequences[0].getScore(), 0.0);
  }

  @Test
  public void topKSequencesWithMinimumScoreKeepsQualifiedCandidate() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    Sequence[] sequences = chunker.topKSequences(
        new String[] {"token"}, new String[] {"NN"}, -0.01);

    assertEquals(1, sequences.length);
    assertEquals("O", sequences[0].getOutcome(0));
  }

  @Test
  public void topKSequencesWithStrictMinimumScoreCanRemoveAllCandidates() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);

    Sequence[] sequences = chunker.topKSequences(
        new String[] {"token"}, new String[] {"NN"}, 0.0);

    assertArrayEquals(new Sequence[0], sequences);
  }

  @Test
  public void topKSequencesDoesNotChangeLastChunkProbabilities() {
    GISModel statisticalModel = new GISModel(new Context[0], new String[0],
        new String[] {"O"});
    ChunkerModel model = new ChunkerModel("en", statisticalModel,
        new HashMap<String, String>(), new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(model);
    chunker.chunk(new String[] {"remember", "this"}, new String[] {"VB", "DT"});

    Sequence[] alternatives = chunker.topKSequences(
        new String[] {"unrelated"}, new String[] {"JJ"});

    assertEquals(1, alternatives.length);
    assertArrayEquals(new double[] {1.0, 1.0}, chunker.probs(), 0.0);
  }

  @Test
  public void trainEventModelProducesUsableChunkerAndHonorsBeamSetting() throws Exception {
    ChunkSample first = new ChunkSample(
        new String[] {"Blue", "cars", "move"},
        new String[] {"JJ", "NNS", "VBP"},
        new String[] {"B-NP", "I-NP", "B-VP"});
    ChunkSample second = new ChunkSample(
        new String[] {"Green", "trucks", "stop"},
        new String[] {"JJ", "NNS", "VBP"},
        new String[] {"B-NP", "I-NP", "B-VP"});
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Iterations", 2);
    parameters.put("Cutoff", 0);
    parameters.put("BeamSize", 3);

    ChunkerModel trained = ChunkerME.train("en",
        new CollectionObjectStream<ChunkSample>(Arrays.asList(first, second)),
        parameters, new ChunkerFactory());
    ChunkerME chunker = new ChunkerME(trained);
    String[] predictions = chunker.chunk(
        new String[] {"Red", "vans", "wait"},
        new String[] {"JJ", "NNS", "VBP"});

    assertNotNull(trained.getChunkerModel());
    assertEquals(3, predictions.length);
    assertEquals(3, chunker.probs().length);
    assertTrue(chunker.topKSequences(new String[] {"Red"}, new String[] {"JJ"}).length <= 3);
  }

  @Test
  public void trainRejectsEventModelSequenceTrainer() throws Exception {
    ChunkSample first = new ChunkSample(
        new String[] {"The", "cat", "slept"},
        new String[] {"DT", "NN", "VBD"},
        new String[] {"B-NP", "I-NP", "B-VP"});
    ChunkSample second = new ChunkSample(
        new String[] {"A", "dog", "ran"},
        new String[] {"DT", "NN", "VBD"},
        new String[] {"B-NP", "I-NP", "B-VP"});
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Algorithm", "PERCEPTRON_SEQUENCE");
    parameters.put("Iterations", 2);
    parameters.put("Cutoff", 0);

    try {
      ChunkerME.train("en",
          new CollectionObjectStream<ChunkSample>(Arrays.asList(first, second)),
          parameters, new ChunkerFactory());
      fail("Expected event-model sequence training to be unsupported");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("EVENT_MODEL_SEQUENCE_TRAINER"));
    }
  }

  @Test
  public void trainRejectsUnsupportedTrainerType() throws Exception {
    ChunkSample sample = new ChunkSample(
        new String[] {"word"}, new String[] {"NN"}, new String[] {"O"});
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Algorithm", "NO_SUCH_TRAINER");

    try {
      ChunkerME.train("en",
          new CollectionObjectStream<ChunkSample>(Collections.singletonList(sample)),
          parameters, new ChunkerFactory());
      fail("Expected an unsupported trainer configuration to fail");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("not supported"));
    }
  }

  @Test
  public void constructorRejectsNullModel() {
    try {
      new ChunkerME((ChunkerModel) null);
      fail("Expected a model to be required");
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }
  }
}
