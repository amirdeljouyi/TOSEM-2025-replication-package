package opennlp.tools.ml.maxent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import opennlp.tools.ml.model.DataIndexer;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.OnePassDataIndexer;
import opennlp.tools.ml.model.OnePassRealValueDataIndexer;
import opennlp.tools.ml.model.UniformPrior;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.InsufficientTrainingDataException;
import opennlp.tools.util.TrainingParameters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GISTrainerPromptTest {

  @Test
  public void exposesExpectedTrainerIdentityAndDefaults() {
    GISTrainer trainer = new GISTrainer();
    TrainingParameters parameters = new TrainingParameters();
    trainer.init(parameters, new HashMap<String, String>());

    assertTrue(trainer.isSortAndMerge());
    assertEquals(100, trainer.getIterations());
  }

  @Test
  public void rejectsSimpleAndGaussianSmoothingTogetherDuringInitialization() {
    GISTrainer trainer = new GISTrainer();
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Smoothing", true);
    parameters.put("GaussianSmoothing", true);

    try {
      trainer.init(parameters, new HashMap<String, String>());
      fail("Both smoothing modes must not be accepted together");
    } catch (RuntimeException expected) {
      assertEquals("Cannot set both Gaussian smoothing and Simple smoothing", expected.getMessage());
    }
  }

  @Test
  public void rejectsZeroTrainingThreads() throws Exception {
    OnePassDataIndexer indexer = new OnePassDataIndexer();
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Cutoff", 0);
    indexer.init(parameters, new HashMap<String, String>());
    indexer.index(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("yes", new String[] {"signal"}),
        new Event("no", new String[] {"noise"}))));

    try {
      new GISTrainer().trainModel(2, indexer, 0);
      fail("Zero threads must be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("threads must be at least one"));
      assertTrue(expected.getMessage().contains("0"));
    }
  }

  @Test
  public void rejectsNegativeTrainingThreadsWithSuppliedPrior() throws Exception {
    OnePassDataIndexer indexer = new OnePassDataIndexer();
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Cutoff", 0);
    indexer.init(parameters, null);
    indexer.index(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("left", new String[] {"west"}),
        new Event("right", new String[] {"east"}))));

    try {
      new GISTrainer().trainModel(2, indexer, new UniformPrior(), -3);
      fail("Negative threads must be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("-3"));
    }
  }

  @Test
  public void trainsBinaryClassifierFromEventStream() throws Exception {
    GISTrainer trainer = new GISTrainer();
    GISModel model = trainer.trainModel(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("positive", new String[] {"good", "bright"}),
        new Event("positive", new String[] {"good", "warm"}),
        new Event("positive", new String[] {"good", "clear"}),
        new Event("negative", new String[] {"bad", "dark"}),
        new Event("negative", new String[] {"bad", "cold"}),
        new Event("negative", new String[] {"bad", "cloudy"}))), 20, 0);

    double[] positive = model.eval(new String[] {"good", "bright"});
    double[] negative = model.eval(new String[] {"bad", "cold"});
    assertEquals(2, model.getNumOutcomes());
    assertEquals("positive", model.getBestOutcome(positive));
    assertEquals("negative", model.getBestOutcome(negative));
    assertEquals(1.0, positive[0] + positive[1], 0.000000001);
    assertEquals(1.0, negative[0] + negative[1], 0.000000001);
  }

  @Test
  public void cutoffRemovesRarePredicatesButRetainsFrequentOnes() throws Exception {
    GISTrainer trainer = new GISTrainer();
    GISModel model = trainer.trainModel(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("A", new String[] {"commonA", "rareA"}),
        new Event("A", new String[] {"commonA"}),
        new Event("B", new String[] {"commonB", "rareB"}),
        new Event("B", new String[] {"commonB"}))), 15, 2);

    double[] common = model.eval(new String[] {"commonA"});
    double[] removed = model.eval(new String[] {"rareA"});
    assertEquals("A", model.getBestOutcome(common));
    assertEquals(0.5, removed[0], 0.000000001);
    assertEquals(0.5, removed[1], 0.000000001);
  }

  @Test
  public void zeroIterationsProducesUniformDistribution() throws Exception {
    GISTrainer trainer = new GISTrainer();
    GISModel model = trainer.trainModel(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("cat", new String[] {"meow"}),
        new Event("dog", new String[] {"bark"}))), 0, 0);

    double[] probabilities = model.eval(new String[] {"meow"});
    assertArrayEquals(new double[] {0.5, 0.5}, probabilities, 0.000000001);
  }

  @Test
  public void configuredDoTrainUsesIterationsAndMultipleThreads() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Iterations", 18);
    parameters.put("Threads", 3);
    parameters.put("Cutoff", 0);
    parameters.put("LLThreshold", 0.0);
    GISTrainer trainer = new GISTrainer();
    trainer.init(parameters, new HashMap<String, String>());
    DataIndexer indexer = trainer.getDataIndexer(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("sports", new String[] {"ball", "team"}),
        new Event("sports", new String[] {"score", "team"}),
        new Event("weather", new String[] {"rain", "cloud"}),
        new Event("weather", new String[] {"wind", "cloud"}))));

    MaxentModel model = trainer.doTrain(indexer);
    assertEquals("sports", model.getBestOutcome(model.eval(new String[] {"ball", "team"})));
    assertEquals("weather", model.getBestOutcome(model.eval(new String[] {"rain", "cloud"})));
  }

  @Test
  public void inheritedTrainUpdatesReportAndReturnsUsableModel() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Iterations", 12);
    parameters.put("Cutoff", 0);
    Map<String, String> report = new HashMap<String, String>();
    GISTrainer trainer = new GISTrainer();
    trainer.init(parameters, report);

    MaxentModel model = trainer.train(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("up", new String[] {"rise"}),
        new Event("up", new String[] {"climb"}),
        new Event("down", new String[] {"fall"}),
        new Event("down", new String[] {"drop"}))));

    assertEquals("Event", report.get("TrainerType"));
    assertNotNull(report.get("Training-Eventhash"));
    assertEquals("up", model.getBestOutcome(model.eval(new String[] {"rise"})));
    assertEquals("down", model.getBestOutcome(model.eval(new String[] {"drop"})));
  }

  @Test(expected = InsufficientTrainingDataException.class)
  public void inheritedTrainRejectsSingleOutcomeData() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Iterations", 4);
    parameters.put("Cutoff", 0);
    GISTrainer trainer = new GISTrainer();
    trainer.init(parameters, new HashMap<String, String>());

    trainer.train(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("only", new String[] {"one"}),
        new Event("only", new String[] {"two"}))));
  }

  @Test
  public void explicitUniformPriorOverloadTrainsThreeOutcomes() throws Exception {
    OnePassDataIndexer indexer = new OnePassDataIndexer();
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Cutoff", 0);
    indexer.init(parameters, new HashMap<String, String>());
    indexer.index(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("red", new String[] {"scarlet", "color"}),
        new Event("green", new String[] {"emerald", "color"}),
        new Event("blue", new String[] {"azure", "color"}))));

    GISModel model = new GISTrainer().trainModel(16, indexer, new UniformPrior(), 2);
    assertEquals(3, model.getNumOutcomes());
    assertEquals("red", model.getBestOutcome(model.eval(new String[] {"scarlet"})));
    assertEquals("green", model.getBestOutcome(model.eval(new String[] {"emerald"})));
    assertEquals("blue", model.getBestOutcome(model.eval(new String[] {"azure"})));
    assertEquals(-1, model.getIndex("purple"));
  }

  @Test
  public void oneAndManyThreadTrainingProduceSameProbabilities() throws Exception {
    TrainingParameters indexingParameters = new TrainingParameters();
    indexingParameters.put("Cutoff", 0);
    OnePassDataIndexer firstIndexer = new OnePassDataIndexer();
    firstIndexer.init(indexingParameters, new HashMap<String, String>());
    firstIndexer.index(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("yes", new String[] {"shared", "affirm"}),
        new Event("yes", new String[] {"affirm"}),
        new Event("no", new String[] {"shared", "deny"}),
        new Event("no", new String[] {"deny"}))));
    OnePassDataIndexer secondIndexer = new OnePassDataIndexer();
    secondIndexer.init(indexingParameters, new HashMap<String, String>());
    secondIndexer.index(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("yes", new String[] {"shared", "affirm"}),
        new Event("yes", new String[] {"affirm"}),
        new Event("no", new String[] {"shared", "deny"}),
        new Event("no", new String[] {"deny"}))));

    GISModel single = new GISTrainer().trainModel(12, firstIndexer, 1);
    GISModel parallel = new GISTrainer().trainModel(12, secondIndexer, 4);
    assertArrayEquals(single.eval(new String[] {"shared", "affirm"}),
        parallel.eval(new String[] {"shared", "affirm"}), 0.000000001);
    assertArrayEquals(single.eval(new String[] {"deny"}),
        parallel.eval(new String[] {"deny"}), 0.000000001);
  }

  @Test
  public void simpleSmoothingSettersProduceFinitePredictionsForSparseFeatures() throws Exception {
    GISTrainer trainer = new GISTrainer();
    trainer.setSmoothing(true);
    trainer.setSmoothingObservation(0.25);
    GISModel model = trainer.trainModel(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("fruit", new String[] {"apple"}),
        new Event("fruit", new String[] {"pear"}),
        new Event("vehicle", new String[] {"car"}),
        new Event("vehicle", new String[] {"truck"}))), 10, 0);

    double[] apple = model.eval(new String[] {"apple"});
    double[] truck = model.eval(new String[] {"truck"});
    assertFalse(Double.isNaN(apple[0]));
    assertFalse(Double.isInfinite(apple[1]));
    assertEquals("fruit", model.getBestOutcome(apple));
    assertEquals("vehicle", model.getBestOutcome(truck));
    assertEquals(1.0, apple[0] + apple[1], 0.000000001);
  }

  @Test
  public void gaussianSmoothingSetterProducesNormalizedDiscriminatingModel() throws Exception {
    GISTrainer trainer = new GISTrainer();
    trainer.setGaussianSigma(1.5);
    GISModel model = trainer.trainModel(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("ham", new String[] {"known", "friend"}),
        new Event("ham", new String[] {"known", "message"}),
        new Event("spam", new String[] {"offer", "money"}),
        new Event("spam", new String[] {"offer", "urgent"}))), 12, 0);

    double[] ham = model.eval(new String[] {"known", "friend"});
    double[] spam = model.eval(new String[] {"offer", "urgent"});
    assertEquals("ham", model.getBestOutcome(ham));
    assertEquals("spam", model.getBestOutcome(spam));
    assertEquals(1.0, ham[0] + ham[1], 0.000000001);
    assertEquals(1.0, spam[0] + spam[1], 0.000000001);
  }

  @Test
  public void configuredSimpleSmoothingIsAppliedByDoTrain() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Iterations", 9);
    parameters.put("Cutoff", 0);
    parameters.put("Smoothing", true);
    parameters.put("SmoothingObservation", 0.2);
    GISTrainer trainer = new GISTrainer();
    trainer.init(parameters, null);
    DataIndexer indexer = trainer.getDataIndexer(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("morning", new String[] {"sunrise"}),
        new Event("morning", new String[] {"breakfast"}),
        new Event("evening", new String[] {"sunset"}),
        new Event("evening", new String[] {"dinner"}))));

    MaxentModel model = trainer.doTrain(indexer);
    double[] result = model.eval(new String[] {"breakfast"});
    assertEquals("morning", model.getBestOutcome(result));
    assertTrue(result[model.getIndex("morning")] > result[model.getIndex("evening")]);
  }

  @Test
  public void configuredGaussianSmoothingIsAppliedByDoTrain() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Iterations", 8);
    parameters.put("Cutoff", 0);
    parameters.put("GaussianSmoothing", true);
    parameters.put("GaussianSmoothingSigma", 1.25);
    GISTrainer trainer = new GISTrainer();
    trainer.init(parameters, new HashMap<String, String>());
    DataIndexer indexer = trainer.getDataIndexer(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("indoor", new String[] {"desk", "lamp"}),
        new Event("indoor", new String[] {"sofa", "lamp"}),
        new Event("outdoor", new String[] {"tree", "sky"}),
        new Event("outdoor", new String[] {"grass", "sky"}))));

    MaxentModel model = trainer.doTrain(indexer);
    assertEquals("indoor", model.getBestOutcome(model.eval(new String[] {"desk", "lamp"})));
    assertEquals("outdoor", model.getBestOutcome(model.eval(new String[] {"tree", "sky"})));
  }

  @Test
  public void evalCanReuseCallerProvidedProbabilityArray() throws Exception {
    GISModel model = new GISTrainer().trainModel(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("true", new String[] {"present"}),
        new Event("true", new String[] {"present", "seen"}),
        new Event("false", new String[] {"absent"}),
        new Event("false", new String[] {"absent", "missing"}))), 10, 0);
    double[] target = new double[] {-1.0, -1.0};

    double[] returned = model.eval(new String[] {"present"}, target);
    assertSame(target, returned);
    assertEquals(1.0, returned[0] + returned[1], 0.000000001);
    assertEquals("true", model.getBestOutcome(returned));
  }

  @Test
  public void trainsAndEvaluatesRealValuedContexts() throws Exception {
    TrainingParameters parameters = new TrainingParameters();
    parameters.put("Cutoff", 0);
    OnePassRealValueDataIndexer indexer = new OnePassRealValueDataIndexer();
    indexer.init(parameters, new HashMap<String, String>());
    indexer.index(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("high", new String[] {"upper", "shared"}, new float[] {2.0f, 0.5f}),
        new Event("high", new String[] {"upper", "shared"}, new float[] {1.5f, 0.5f}),
        new Event("low", new String[] {"lower", "shared"}, new float[] {2.0f, 0.5f}),
        new Event("low", new String[] {"lower", "shared"}, new float[] {1.5f, 0.5f}))));

    GISModel model = new GISTrainer().trainModel(14, indexer, 2);
    double[] high = model.eval(new String[] {"upper", "shared"}, new float[] {2.0f, 0.5f});
    double[] low = model.eval(new String[] {"lower", "shared"}, new float[] {2.0f, 0.5f});
    assertEquals("high", model.getBestOutcome(high));
    assertEquals("low", model.getBestOutcome(low));
    assertEquals(1.0, high[0] + high[1], 0.000000001);
    assertEquals(1.0, low[0] + low[1], 0.000000001);
  }

  @Test
  public void unknownContextFallsBackToUniformPrior() throws Exception {
    GISModel model = new GISTrainer().trainModel(new CollectionObjectStream<Event>(Arrays.asList(
        new Event("first", new String[] {"alpha"}),
        new Event("first", new String[] {"aleph"}),
        new Event("second", new String[] {"beta"}),
        new Event("second", new String[] {"beth"}))), 10, 0);

    double[] result = model.eval(new String[] {"never-seen"});
    assertArrayEquals(new double[] {0.5, 0.5}, result, 0.000000001);
    assertEquals("first", model.getOutcome(0));
    assertEquals("second", model.getOutcome(1));
  }
}
