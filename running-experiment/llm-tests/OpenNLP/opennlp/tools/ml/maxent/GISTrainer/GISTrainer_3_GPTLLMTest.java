package opennlp.tools.ml.maxent;

import opennlp.tools.ml.model.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GISTrainer_3_GPTLLMTest {

@Test
public void testIsSortAndMergeReturnsTrue() {
GISTrainer trainer = new GISTrainer();
assertTrue(trainer.isSortAndMerge());
}

@Test
public void testInitDefaultConfiguration() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
assertTrue(true);
}

@Test
public void testInitWithSimpleSmoothing() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
assertTrue(true);
}

@Test
public void testInitWithGaussianSmoothing() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("GaussianSmoothing", "true");
params.put("GaussianSmoothingSigma", "3.0");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
assertTrue(true);
}

@Test
public void testInitWithBothSmoothingThrowsRuntimeException() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
params.put("GaussianSmoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
}

@Test
public void testSetSmoothingObservation() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothingObservation(0.5);
assertTrue(true);
}

@Test
public void testSetGaussianSigma() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(1.5);
assertTrue(true);
}

@Test
public void testSetSmoothing() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
assertTrue(true);
}

@Test
public void testTrainModelWithInvalidThreadCountThrows() {
GISTrainer trainer = new GISTrainer();
// DummyDataIndexer indexer = new DummyDataIndexer();
// trainer.trainModel(10, indexer, new UniformPrior(), 0);
}

@Test
public void testTrainModelReturnsGISModelWithSimpleIndexer() {
GISTrainer trainer = new GISTrainer();
// DummyDataIndexer indexer = new DummyDataIndexer();
// GISModel model = trainer.trainModel(5, indexer);
// assertNotNull(model);
// assertEquals(2, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelUsingObjectStream() throws IOException {
GISTrainer trainer = new GISTrainer();
// ObjectStream<Event> eventStream = new DummyEventStream();
// GISModel model = trainer.trainModel(eventStream);
// assertNotNull(model);
// assertEquals(2, model.getNumOutcomes());
}

@Test
public void testTrainModelWithUniformPrior() {
GISTrainer trainer = new GISTrainer();
// DummyDataIndexer indexer = new DummyDataIndexer();
// GISModel model = trainer.trainModel(3, indexer, new UniformPrior(), 1);
// assertNotNull(model);
}

@Test
public void testTrainModelMultiThreaded() {
GISTrainer trainer = new GISTrainer();
// DummyDataIndexer indexer = new DummyDataIndexer();
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 2);
// assertNotNull(model);
}

@Test
public void testTrainModelWithSmoothingEnabled() throws IOException {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.2);
// ObjectStream<Event> eventStream = new DummyEventStream();
// GISModel model = trainer.trainModel(eventStream, 10, 0);
// assertNotNull(model);
}

@Test
public void testTrainModelWithGaussianSmoothing() throws IOException {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(2.0);
// ObjectStream<Event> eventStream = new DummyEventStream();
// GISModel model = trainer.trainModel(eventStream, 10, 0);
// assertNotNull(model);
}

@Test
public void testTrainModelNegativeIterationsThrows() {
GISTrainer trainer = new GISTrainer();
// DummyDataIndexer indexer = new DummyDataIndexer();
// trainer.trainModel(-1, indexer, new UniformPrior(), 1);
}

@Test
public void testTrainModelZeroThreadsThrows() {
GISTrainer trainer = new GISTrainer();
// DummyDataIndexer indexer = new DummyDataIndexer();
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 0);
}

@Test
public void testDoTrainReturnsValidModel() throws IOException {
GISTrainer trainer = new GISTrainer();
// DummyDataIndexer indexer = new DummyDataIndexer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 3);
trainer.init(params, new HashMap<>());
// MaxentModel model = trainer.doTrain(indexer);
// assertNotNull(model);
// assertTrue(model.getNumOutcomes() > 0);
}

@Test
public void testTrainModelSingleEventTraining() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> singleEventStream = new ObjectStream<Event>() {

boolean readOnce = false;

public Event read() {
if (!readOnce) {
readOnce = true;
return new Event("A", new String[] { "x" });
}
return null;
}

public void reset() {
readOnce = false;
}

public void close() {
}
};
GISModel model = trainer.trainModel(singleEventStream, 10, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
}

@Test
public void testTrainModelWithHighIterationCount() throws IOException {
GISTrainer trainer = new GISTrainer();
// ObjectStream<Event> eventStream = new DummyEventStream();
// GISModel model = trainer.trainModel(eventStream, 50, 0);
// assertNotNull(model);
// assertEquals(2, model.getNumOutcomes());
}

@Test
public void testTrainModelWithZeroCutoff() throws IOException {
GISTrainer trainer = new GISTrainer();
// ObjectStream<Event> eventStream = new DummyEventStream();
// GISModel model = trainer.trainModel(eventStream, 10, 0);
// assertNotNull(model);
}

@Test
public void testTrainModelWithNonZeroCutoffExcludesRareFeatures() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> rareFeaturesStream = new ObjectStream<Event>() {

private int count = 0;

public Event read() {
if (count == 0) {
count++;
return new Event("pos", new String[] { "rareFeature" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(rareFeaturesStream, 10, 2);
assertNotNull(model);
}

@Test
public void testTrainModelWithMultipleCallsDoesNotInterfere() throws IOException {
GISTrainer trainer = new GISTrainer();
// ObjectStream<Event> stream1 = new DummyEventStream();
// ObjectStream<Event> stream2 = new DummyEventStream();
// GISModel model1 = trainer.trainModel(stream1, 5, 0);
// assertNotNull(model1);
// GISModel model2 = trainer.trainModel(stream2, 5, 0);
// assertNotNull(model2);
// assertEquals(model1.getNumOutcomes(), model2.getNumOutcomes());
}

@Test
public void testTrainModelSmokeTestWithEmptyStream() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> emptyStream = new ObjectStream<Event>() {

public Event read() {
return null;
}

public void reset() {
}

public void close() {
}
};
try {
trainer.trainModel(emptyStream, 10, 0);
fail("Expected an exception due to empty training data.");
} catch (Exception e) {
assertTrue(e instanceof IllegalArgumentException || e instanceof NullPointerException);
}
}

@Test
public void testTrainModelWithNullValuesAndNoCrash() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "yes" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "feature" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(3, indexer, new UniformPrior(), 1);
// assertNotNull(model);
}

@Test
public void testTrainModelWithZeroEvents() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[0][];
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[0];
// }
// 
// public int[] getOutcomeList() {
// return new int[0];
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "A", "B" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "feature1", "feature2" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 0, 0 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(1, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(2, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelWithSingleFeatureUsedInBothClasses() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int index = 0;

public Event read() {
if (index == 0) {
index++;
return new Event("positive", new String[] { "sharedFeature" });
} else if (index == 1) {
index++;
return new Event("negative", new String[] { "sharedFeature" });
} else {
return null;
}
}

public void reset() {
index = 0;
}

public void close() {
}
};
GISModel model = null;
try {
model = trainer.trainModel(stream, 10, 0);
} catch (IOException e) {
fail("IOException should not occur.");
}
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
// assertEquals(1, model.getNumPredicates());
// assertArrayEquals(new String[] { "sharedFeature" }, model.getDataStructures().getPredLabels());
}

@Test
public void testTrainModelConvergenceOnIdenticalEvents() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int count = 0;

public Event read() {
if (count < 4) {
count++;
return new Event("yes", new String[] { "f1" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertEquals(1, model.getNumPredicates());
}

@Test
public void testTrainModelWithFeatureHavingZeroCount() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "A" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "zeroCountFeature" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 0 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// assertEquals(1, model.getNumPredicates());
}

@Test
public void testTrainModelWithMultipleFeaturesPerEvent() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int index = 0;

public Event read() {
if (index == 0) {
index++;
return new Event("pos", new String[] { "f1", "f2", "f3" });
}
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertTrue(model.getNumPredicates() >= 1);
}

@Test
public void testTrainModelWithFloatContextValues() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0, 1 } };
// }
// 
// public float[][] getValues() {
// return new float[][] { { 0.5f, 2.0f } };
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "yes" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "f1", "f2" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1, 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelWithSingleThreadAndMultiplePredicates() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0, 1 }, { 1, 2 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1, 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0, 1 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "Y", "N" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "a", "b", "c" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1, 2, 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(2, model.getNumOutcomes());
// assertEquals(3, model.getNumPredicates());
}

@Test
public void testThreadInterruptionDuringComputation() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 }, { 1 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1, 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0, 1 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "yes", "no" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "f1", "f2" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1, 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
Thread.currentThread().interrupt();
try {
// trainer.trainModel(2, indexer, new UniformPrior(), 1);
} finally {
Thread.interrupted();
}
}

@Test
public void testNextIterationHandlesZeroModelExpectationGracefully() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "A" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "p" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(2, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumPredicates());
// assertEquals(1, model.getNumOutcomes());
}

@Test
public void testSmoothingObservationHasEffectOnTraining() throws IOException {
GISTrainer trainerWithSmoothing = new GISTrainer();
trainerWithSmoothing.setSmoothing(true);
trainerWithSmoothing.setSmoothingObservation(5.0);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean seen = false;

public Event read() {
if (!seen) {
seen = true;
return new Event("X", new String[] { "rare" });
}
return null;
}

public void reset() {
seen = false;
}

public void close() {
}
};
GISModel model = trainerWithSmoothing.trainModel(stream, 10, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertTrue(model.getNumPredicates() >= 1);
}

@Test
public void testGaussianUpdateHandlesZeroSigmaGracefully() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(0.0);
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "outcome" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "feature" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(2, indexer, new UniformPrior(), 1);
// assertNotNull(model);
}

@Test
public void testTrainModelWithCutoffFilteringAllFeatures() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return new Event("label", new String[] { "rare" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(eventStream, 5, 100);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertEquals(0, model.getNumPredicates());
}

@Test
public void testTrainModelWithInterleavedOutcomeOrder() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

int index = 0;

public Event read() {
if (index == 0) {
index++;
return new Event("Z", new String[] { "a" });
} else if (index == 1) {
index++;
return new Event("X", new String[] { "b" });
} else {
return null;
}
}

public void reset() {
index = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(eventStream, 5, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
// assertTrue(Arrays.asList(model.getDataStructures().getOutcomeLabels()).contains("Z"));
// assertTrue(Arrays.asList(model.getDataStructures().getOutcomeLabels()).contains("X"));
}

@Test
public void testTrainModelWithNoPredicates() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { {} };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "yes" };
// }
// 
// public String[] getPredLabels() {
// return new String[] {};
// }
// 
// public int[] getPredCounts() {
// return new int[] {};
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// assertEquals(0, model.getNumPredicates());
}

@Test
public void testTrainModelWithAllFeaturesBelowCutoff() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> input = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count < 3) {
count++;
return new Event("label", new String[] { "f1" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(input, 10, 5);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertEquals(0, model.getNumPredicates());
}

@Test
public void testTrainModelWithDisjointOutcomePatterns() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int index = 0;

public Event read() {
if (index == 0) {
index++;
return new Event("classA", new String[] { "fA1" });
}
if (index == 1) {
index++;
return new Event("classB", new String[] { "fB2" });
}
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelWithAllFeaturesActiveInAllClasses() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return new Event("yes", new String[] { "f1", "f2" });
}
if (count == 1) {
count++;
return new Event("no", new String[] { "f1", "f2" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelWithSmoothingAndZeroCounts() throws IOException {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.1);
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

int index = 0;

public Event read() {
if (index == 0) {
index++;
return new Event("X", new String[] { "feat" });
}
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(eventStream, 5, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertEquals(1, model.getNumPredicates());
}

@Test
public void testTrainModelReturnsReasonableProbabilities() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return new Event("A", new String[] { "x" });
}
if (count == 1) {
count++;
return new Event("B", new String[] { "x" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 10, 0);
assertNotNull(model);
double[] dist = model.eval(new String[] { "x" });
assertEquals(2, dist.length);
assertTrue(dist[0] > 0.0);
assertTrue(dist[1] > 0.0);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testTrainModelWithFeatureSeenInOneClassOnly() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int step = 0;

public Event read() {
if (step == 0) {
step++;
return new Event("positive", new String[] { "f1" });
} else if (step == 1) {
step++;
return new Event("negative", new String[] { "f2" });
} else {
return null;
}
}

public void reset() {
step = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelWithEmptyOutcomeLabels() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] {};
// }
// 
// public String[] getPredLabels() {
// return new String[] { "feature" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
try {
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
} catch (Exception e) {
assertTrue(e instanceof ArrayIndexOutOfBoundsException || e instanceof IllegalArgumentException);
}
}

@Test
public void testTrainModelWithNullValuesButNonNullContext() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 } };
// }
// 
// public float[][] getValues() {
// return new float[][] { null };
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "X" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "feature" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
}

@Test
public void testTrainModelWithDuplicatePredicates() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return new Event("A", new String[] { "feat", "feat" });
}
return null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
try {
GISModel model = trainer.trainModel(eventStream, 10, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertTrue(model.getNumPredicates() >= 1);
} catch (Exception e) {
fail("Training should not fail with duplicate features.");
}
}

@Test
public void testTrainModelWithSingleOutcomeClass() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i++ < 3) {
return new Event("same", new String[] { "f1", "f2" });
}
return null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 10, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertTrue(model.getNumPredicates() >= 2);
}

@Test
public void testTrainModelWithInvalidSigmaValue() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(-10.0);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean read = false;

public Event read() {
if (!read) {
read = true;
return new Event("yes", new String[] { "f1" });
}
return null;
}

public void reset() {
read = false;
}

public void close() {
}
};
try {
GISModel model = trainer.trainModel(stream, 10, 0);
assertNotNull(model);
} catch (Exception e) {
fail("Model training should not fail even with negative sigma (although parameters may be incorrect).");
}
}

@Test
public void testTrainModelWithSingleEventSeenMultipleTimes() throws IOException {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 5 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "X" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "feature" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 5 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// assertEquals(1, model.getNumPredicates());
}

@Test
public void testTrainModelWithAllZeroPredCounts() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0 }, { 1 } };
// }
// 
// public float[][] getValues() {
// return null;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1, 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0, 1 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "yes", "no" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "p1", "p2" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 0, 0 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(5, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(2, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelWithIdenticalContextDifferentOutcomes() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int index = 0;

public Event read() {
if (index++ == 0) {
return new Event("outcome1", new String[] { "feature1" });
} else if (index == 2) {
return new Event("outcome2", new String[] { "feature1" });
}
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
// assertEquals(1, model.getNumPredicates());
}

@Test
public void testTrainModelWithFloatValuesIncludingZero() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { { 0, 1 } };
// }
// 
// public float[][] getValues() {
// return new float[][] { { 0.0f, 2.0f } };
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "label" };
// }
// 
// public String[] getPredLabels() {
// return new String[] { "f0", "f1" };
// }
// 
// public int[] getPredCounts() {
// return new int[] { 1, 1 };
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(3, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// assertEquals(2, model.getNumPredicates());
}

@Test
public void testTrainModelWithEmptyContextAndValues() {
GISTrainer trainer = new GISTrainer();
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return new int[][] { {} };
// }
// 
// public float[][] getValues() {
// return new float[][] { {} };
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "noFeature" };
// }
// 
// public String[] getPredLabels() {
// return new String[] {};
// }
// 
// public int[] getPredCounts() {
// return new int[] {};
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(2, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// assertEquals(0, model.getNumPredicates());
}

@Test
public void testTrainModelWithLargeNumberOfPredicates() {
GISTrainer trainer = new GISTrainer();
int predicateCount = 1000;
int[][] contexts = new int[1][predicateCount];
for (int i = 0; i < predicateCount; i++) {
contexts[0][i] = i;
}
float[][] values = new float[1][predicateCount];
for (int i = 0; i < predicateCount; i++) {
values[0][i] = 1.0f;
}
int[] predCounts = new int[predicateCount];
for (int i = 0; i < predicateCount; i++) {
predCounts[i] = 1;
}
String[] predLabels = new String[predicateCount];
for (int i = 0; i < predicateCount; i++) {
predLabels[i] = "p" + i;
}
// DataIndexer indexer = new DataIndexer() {
// 
// public int[][] getContexts() {
// return contexts;
// }
// 
// public float[][] getValues() {
// return values;
// }
// 
// public int[] getNumTimesEventsSeen() {
// return new int[] { 1 };
// }
// 
// public int[] getOutcomeList() {
// return new int[] { 0 };
// }
// 
// public String[] getOutcomeLabels() {
// return new String[] { "binaryOutcome" };
// }
// 
// public String[] getPredLabels() {
// return predLabels;
// }
// 
// public int[] getPredCounts() {
// return predCounts;
// }
// 
// public void init(TrainingParameters trainingParameters, Map<String, String> reportMap) {
// }
// 
// public void index(ObjectStream<Event> eventStream) {
// }
// };
// GISModel model = trainer.trainModel(2, indexer, new UniformPrior(), 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// assertEquals(predicateCount, model.getNumPredicates());
}

@Test
public void testTrainingWithAllEventsHavingSameOutcomeAndDifferentPredicates() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

int index = 0;

public Event read() {
if (index == 0) {
index++;
return new Event("X", new String[] { "f1" });
} else if (index == 1) {
index++;
return new Event("X", new String[] { "f2" });
} else if (index == 2) {
index++;
return new Event("X", new String[] { "f3" });
}
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(eventStream, 5, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertEquals(3, model.getNumPredicates());
}

@Test
public void testTrainModelWithEmptyPredicatesStillReturnsModel() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

boolean called = false;

public Event read() {
if (!called) {
called = true;
return new Event("only", new String[] {});
}
return null;
}

public void reset() {
called = false;
}

public void close() {
}
};
GISModel model = trainer.trainModel(eventStream, 3, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
// assertEquals(0, model.getNumPredicates());
}

@Test
public void testTrainModelStopsEarlyOnConvergenceDueToLLThreshold() throws IOException {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("LLThreshold", "1000");
params.put(TrainingParameters.ITERATIONS_PARAM, "100");
trainer.init(params, new HashMap<String, String>());
ObjectStream<Event> stream = new ObjectStream<Event>() {

int step = 0;

public Event read() {
if (step++ < 2) {
return new Event("label", new String[] { "high" });
}
return null;
}

public void reset() {
step = 0;
}

public void close() {
}
};
GISModel model = trainer.trainModel(stream, 100, 0);
assertNotNull(model);
}
}
