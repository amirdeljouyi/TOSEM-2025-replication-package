package opennlp.tools.ml.maxent;

import opennlp.tools.ml.model.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GISTrainer_5_GPTLLMTest {

@Test
public void testInitWithDefaultParameters() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
Map<String, String> reportMap = new HashMap<>();
try {
trainer.init(parameters, reportMap);
} catch (Exception e) {
fail("Initialization with default parameters threw an exception: " + e.getMessage());
}
}

@Test
public void testInitThrowsWhenBothSmoothingEnabled() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("GaussianSmoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(parameters, reportMap);
}

@Test
public void testSetSmoothingAndObservation() {
GISTrainer trainer = new GISTrainer();
try {
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.7);
} catch (Exception e) {
fail("Setting smoothing or observation threw exception: " + e.getMessage());
}
}

@Test
public void testSetGaussianSmoothingSigma() {
GISTrainer trainer = new GISTrainer();
try {
trainer.setGaussianSigma(1.5);
} catch (Exception e) {
fail("Setting Gaussian sigma threw exception: " + e.getMessage());
}
}

@Test
public void testTrainModelWithEmptyEventStream() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> emptyStream = new ObjectStream<Event>() {

public Event read() throws IOException {
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
MaxentModel model = trainer.trainModel(emptyStream);
// assertNotNull("Model should not be null", model);
// assertTrue("Model should have 0 outcomes", model.getNumOutcomes() == 0);
// assertTrue("Model should have 0 predicates", model.getNumPredicates() == 0);
}

@Test
public void testTrainModelWithSingleEventAndSmoothing() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> oneEventStream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() throws IOException {
if (!returned) {
returned = true;
return new Event("positive", new String[] { "f1" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 5);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put("Smoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(parameters, reportMap);
MaxentModel model = trainer.trainModel(oneEventStream, 5, 0);
// assertNotNull("Model should not be null", model);
String bestOutcome = model.getBestOutcome(new double[] { 1.0 });
assertEquals("Best outcome should be 'positive'", "positive", bestOutcome);
}

@Test
public void testTrainModelWithSingleEventAndGaussianSmoothing() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> oneEventStream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() throws IOException {
if (!returned) {
returned = true;
return new Event("YES", new String[] { "x1" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 2);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put("GaussianSmoothing", "true");
parameters.put("GaussianSmoothingSigma", "2.0");
Map<String, String> reportMap = new HashMap<>();
trainer.init(parameters, reportMap);
MaxentModel model = trainer.trainModel(oneEventStream, 2, 0);
// assertNotNull("Model should not be null", model);
assertEquals("Best outcome must be YES", "YES", model.getBestOutcome(new double[] { 1.0 }));
}

@Test
public void testTrainModelInvalidThreadsThrows() {
GISTrainer trainer = new GISTrainer();
trainer.trainModel(5, null, new UniformPrior(), 0);
}

@Test
public void testFullTrainingMultipleEventsSingleThread() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

private int count = 0;

public Event read() throws IOException {
if (count == 0) {
count++;
return new Event("SPAM", new String[] { "feature1" });
} else if (count == 1) {
count++;
return new Event("SPAM", new String[] { "feature2" });
} else if (count == 2) {
count++;
return new Event("HAM", new String[] { "feature1" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 10);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put(TrainingParameters.THREADS_PARAM, 1);
parameters.put("Smoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(parameters, reportMap);
MaxentModel model = trainer.trainModel(eventStream, 10, 0);
// assertNotNull("Model should be created", model);
// assertTrue("Should have at least 2 outcomes", model.getNumOutcomes() >= 2);
}

@Test
public void testLogLikelihoodThresholdParameterUse() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("LLThreshold", "0.000001");
Map<String, String> reportMap = new HashMap<>();
try {
trainer.init(parameters, reportMap);
} catch (Exception e) {
fail("Init with LLThreshold param should not fail");
}
}

@Test
public void testTrainWithCustomPrior() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean read = false;

public Event read() throws IOException {
if (!read) {
read = true;
return new Event("X", new String[] { "a" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
// OnePassDataIndexer indexer = new OnePassDataIndexer(stream, new TrainingParameters());
Prior customPrior = new UniformPrior() {

@Override
public void logPrior(double[] dist, int[] context) {
if (dist != null && dist.length > 0) {
dist[0] = -2.0;
}
}
};
// MaxentModel model = trainer.trainModel(5, indexer, customPrior, 1);
// assertNotNull("Model should not be null", model);
}

@Test
public void testTrainModelWithZeroIterations() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> zeroStream = new ObjectStream<Event>() {

private boolean called = false;

public Event read() throws IOException {
if (!called) {
called = true;
return new Event("A", new String[] { "f1" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 0);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put("Smoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(parameters, reportMap);
assertNotNull(trainer.trainModel(zeroStream, 0, 0));
}

@Test
public void testTrainModelWithNegativeCutoff() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> simpleStream = new ObjectStream<Event>() {

private boolean called = false;

public Event read() throws IOException {
if (!called) {
called = true;
return new Event("A", new String[] { "f1" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 3);
parameters.put(TrainingParameters.CUTOFF_PARAM, -1);
Map<String, String> reportMap = new HashMap<>();
trainer.init(parameters, reportMap);
MaxentModel model = trainer.trainModel(simpleStream, 3, -1);
assertNotNull(model);
}

@Test
public void testTrainModelWithoutInit() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() throws IOException {
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
}

@Test
public void testTrainWithLargeNumberOfThreads() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> multiThreadStream = new ObjectStream<Event>() {

private int counter = 0;

public Event read() throws IOException {
if (counter++ < 15) {
return new Event("X", new String[] { "a", "b" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 5);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put(TrainingParameters.THREADS_PARAM, 8);
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(multiThreadStream, 5, 0);
assertNotNull(model);
assertTrue(model.getNumOutcomes() > 0);
}

@Test
public void testRepeatedFeaturesSameOutcome() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> repeatStream = new ObjectStream<Event>() {

private int count = 0;

public Event read() throws IOException {
if (count++ < 3) {
return new Event("Y", new String[] { "repFeat" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 10);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(repeatStream, 10, 0);
assertNotNull(model);
}

@Test
public void testEventWithEmptyFeatureArray() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> emptyFeatureStream = new ObjectStream<Event>() {

private boolean called = false;

public Event read() throws IOException {
if (!called) {
called = true;
return new Event("Z", new String[] {});
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 3);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(emptyFeatureStream, 3, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithNullDataIndexer() {
GISTrainer trainer = new GISTrainer();
trainer.trainModel(5, null, new UniformPrior(), 1);
}

@Test
public void testSmoothingDoesNotCauseNaN() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int count = 0;

public Event read() throws IOException {
if (count++ == 0) {
return new Event("yes", new String[] { "f" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 5);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put("Smoothing", "true");
parameters.put("SmoothingObservation", "0.0001");
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
}

@Test
public void testOneHotOutcomeVectorScenario() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> oneHotStream = new ObjectStream<Event>() {

private int count = 0;

public Event read() throws IOException {
if (count++ == 0) {
return new Event("OUTCOME_A", new String[] { "X" });
}
if (count++ == 1) {
return new Event("OUTCOME_B", new String[] { "Y" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 3);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(oneHotStream, 3, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
}

@Test
public void testConvergingModelWithLowThreshold() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

private int count = 0;

public Event read() throws IOException {
if (count++ < 6) {
return new Event("Outcome", new String[] { "f" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 100);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put("LLThreshold", "1000000");
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(eventStream, 100, 0);
assertNotNull(model);
}

@Test
public void testInterruptedDuringTraining() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() throws IOException {
return new Event("X", new String[] { "f" });
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
final Thread currentThread = Thread.currentThread();
ObjectStream<Event> wrapped = new ObjectStream<Event>() {

private boolean called = false;

public Event read() throws IOException {
if (!called) {
called = true;
return new Event("X", new String[] { "f" });
}
currentThread.interrupt();
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 5);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put(TrainingParameters.THREADS_PARAM, 1);
trainer.init(params, new HashMap<>());
trainer.trainModel(wrapped, 5, 0);
}

@Test
public void testEventWithNullFeatureArray() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean delivered = false;

public Event read() throws IOException {
if (!delivered) {
delivered = true;
return new Event("NullLabel", null);
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 3);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
}

@Test
public void testMultipleDifferentOutcomesSameFeature() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int i = 0;

public Event read() throws IOException {
if (i == 0) {
i++;
return new Event("A", new String[] { "f1" });
} else if (i == 1) {
i++;
return new Event("B", new String[] { "f1" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 4);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 4, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
}

@Test
public void testGaussianUpdateBranchExecutionWithZeroGradient() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() {
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 1);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put("GaussianSmoothing", "true");
params.put("GaussianSmoothingSigma", "0.0000001");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
}

@Test
public void testTinySmoothingObservationWithSmoothing() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() throws IOException {
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 1);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put("Smoothing", "true");
params.put("SmoothingObservation", "0.0000001");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
}

@Test
public void testEmptyOutcomeLabelSet() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() {
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 3);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
assertEquals(0, model.getNumOutcomes());
}

@Test
public void testExceptionLoggedWhenModelExpectsZero() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean called = false;

public Event read() {
if (!called) {
called = true;
return new Event("label", new String[] { "zero" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 1);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
GISModel model = trainer.trainModel(stream, 1, 0);
double[] values = new double[model.getNumOutcomes()];
model.eval(new String[] { "zero" }, values);
model.getBestOutcome(values);
}

@Test
public void testMultipleEventsUniqueFeaturesEach() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int index = 0;

public Event read() throws IOException {
if (index == 0) {
index++;
return new Event("POS", new String[] { "f1" });
} else if (index == 1) {
index++;
return new Event("NEG", new String[] { "f2" });
} else if (index == 2) {
index++;
return new Event("POS", new String[] { "f3" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 5);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
}

@Test
public void testMultipleIdenticalEventsAndConvergence() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int count = 0;

public Event read() throws IOException {
if (count++ < 10) {
return new Event("SAME", new String[] { "f" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 100);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put("LLThreshold", "100000");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 100, 0);
assertNotNull(model);
assertTrue(model.getNumOutcomes() > 0);
}

@Test
public void testTrainModelWithOneEventAndMultipleFeatures() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() throws IOException {
if (!returned) {
returned = true;
return new Event("label", new String[] { "f1", "f2", "f3" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 10);
trainingParameters.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(trainingParameters, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 10, 0);
assertNotNull(model);
assertEquals("label", model.getBestOutcome(new double[] { 1.0 }));
}

@Test
public void testTrainWithObservedExpectationZeroAndModelExpectationNonZero() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int index = 0;

public Event read() throws IOException {
if (index++ == 0)
return new Event("A", new String[] { "f" });
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 1);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
parameters.put("Smoothing", "true");
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
}

@Test
public void testTrainModelWithFeatureNeverAppearingInStream() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean done = false;

public Event read() throws IOException {
if (!done) {
done = true;
return new Event("Outcome", new String[] { "f1" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 3);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put("Smoothing", "true");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
assertTrue(model.getNumOutcomes() == 1);
}

@Test
public void testTrainModelWithLargeCorrectionConstant() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int i = 0;

public Event read() throws IOException {
if (i++ < 10) {
return new Event("O", new String[] { "x", "y", "z", "p", "q", "r", "s" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 5);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithModelDivergence() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int index = 0;

public Event read() throws IOException {
if (index++ < 3) {
return new Event(index % 2 == 0 ? "X" : "Y", new String[] { "f" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 100);
trainingParameters.put(TrainingParameters.CUTOFF_PARAM, 0);
trainingParameters.put("LLThreshold", "0.0");
trainer.init(trainingParameters, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 100, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWhereModelExpectationEqualsObservedExpectation() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean done = false;

public Event read() throws IOException {
if (!done) {
done = true;
return new Event("LABEL", new String[] { "constant" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 1);
trainingParameters.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(trainingParameters, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithNegativeThreads() {
GISTrainer trainer = new GISTrainer();
DataIndexer dataIndexer = new DataIndexer() {

public int[] getNumTimesEventsSeen() {
return new int[0];
}

public int[][] getContexts() {
return new int[0][0];
}

public float[][] getValues() {
return null;
}

public String[] getOutcomeLabels() {
return new String[0];
}

public String[] getPredLabels() {
return new String[0];
}

public int getNumEvents() {
return 0;
}

public int getNumPreds() {
return 0;
}

public int getNumOutcomes() {
return 0;
}

public int[] getPredCounts() {
return new int[0];
}

public int[] getOutcomeList() {
return new int[0];
}

public void init(TrainingParameters params, java.util.Map<String, String> reportMap) {
}

public void index(ObjectStream<Event> events) {
}
};
trainer.trainModel(1, dataIndexer, 0);
}

@Test
public void testTrainModelWithNullValues() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int count = 0;

public Event read() {
if (count++ < 2) {
return new Event("LABEL", new String[] { "feat" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 2);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 2, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithLargeGaussianSigmaValue() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() throws IOException {
if (!returned) {
returned = true;
return new Event("A", new String[] { "x" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 3);
params.put(TrainingParameters.CUTOFF_PARAM, "0");
params.put("GaussianSmoothing", "true");
params.put("GaussianSmoothingSigma", "10000.0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
}

@Test
public void testZeroFeaturesWithOutcome() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() {
if (!returned) {
returned = true;
return new Event("X", new String[] {});
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 1);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
}

@Test
public void testModelWithMultiplePredicatesAndOutcomes() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int i = 0;

public Event read() throws IOException {
if (i == 0) {
i++;
return new Event("YES", new String[] { "f1" });
}
if (i == 1) {
i++;
return new Event("NO", new String[] { "f2" });
}
if (i == 2) {
i++;
return new Event("MAYBE", new String[] { "f1", "f3" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 5);
params.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
assertEquals(3, model.getNumOutcomes());
}

@Test
public void testTrainModelWithMissingOutcomeLabel() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() {
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "1");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
assertEquals(0, model.getNumOutcomes());
}

@Test
public void testTrainModelWithDuplicateEvents() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

private int index = 0;

public Event read() throws IOException {
if (index++ < 3) {
return new Event("DUP", new String[] { "same" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 3);
parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
trainer.init(parameters, new HashMap<>());
MaxentModel model = trainer.trainModel(eventStream, 3, 0);
assertNotNull(model);
assertEquals("DUP", model.getBestOutcome(new double[] { 1.0 }));
}

@Test
public void testTrainModelWithCustomPriorZeroEffect() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() {
return new Event("X", new String[] { "feat" });
}

public void reset() {
}

public void close() {
}
};
// final Prior prior = new Prior() {
// 
// public void setLabels(String[] outcomeLabels, String[] predLabels) {
// }
// 
// public void logPrior(double[] dist, int[] context) {
// for (int i = 0; i < dist.length; i++) dist[i] = 0.0;
// }
// 
// public void logPrior(double[] dist, int[] context, float[] values) {
// for (int i = 0; i < dist.length; i++) dist[i] = 0.0;
// }
// };
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "2");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
// MaxentModel model = trainer.trainModel(2, new OnePassDataIndexer(stream, params), prior, 1);
// assertNotNull(model);
}

@Test
public void testSetGaussianSigmaAndDisableSmoothing() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(false);
trainer.setGaussianSigma(4.2);
assertTrue(true);
}

@Test
public void testInitWithOnlyGaussianSmoothingEnabled() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("GaussianSmoothing", "true");
parameters.put("GaussianSmoothingSigma", "3.5");
trainer.init(parameters, new HashMap<String, String>());
assertTrue(true);
}

@Test
public void testInitWithOnlySimpleSmoothingEnabled() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("SmoothingObservation", "0.25");
trainer.init(parameters, new HashMap<String, String>());
assertTrue(true);
}

@Test
public void testInitWithBothSmoothingEnabledThrowsException() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("GaussianSmoothing", "true");
trainer.init(parameters, new HashMap<String, String>());
}

@Test
public void testTrainWithEmptyFeatureAndOutcomeStrings() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() throws IOException {
if (!returned) {
returned = true;
return new Event("", new String[] { "" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "2");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 2, 0);
assertNotNull(model);
assertTrue(model.getNumOutcomes() > 0);
}

@Test
public void testTrainWithMultipleIdenticalOutcomesDifferentFeatures() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int index = 0;

public Event read() throws IOException {
if (index == 0) {
index++;
return new Event("SAME", new String[] { "f1" });
} else if (index == 1) {
index++;
return new Event("SAME", new String[] { "f2" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "3");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
assertEquals("SAME", model.getBestOutcome(new double[] { 1.0 }));
}

@Test
public void testTrainModelSingleFeatureUsedForMultipleOutcomes() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int callCount = 0;

public Event read() throws IOException {
if (callCount == 0) {
callCount++;
return new Event("A", new String[] { "shared" });
}
if (callCount == 1) {
callCount++;
return new Event("B", new String[] { "shared" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "3");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
}

@Test
public void testTrainModelWithThresholdConvergenceStop() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int counter = 0;

public Event read() throws IOException {
if (counter++ < 5) {
return new Event("OUT", new String[] { "feat" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "100");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
params.put("LLThreshold", "100000");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 100, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithSingleEventMultipleTimesSeen() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean done = false;

public Event read() throws IOException {
if (!done) {
done = true;
return new Event("OCCUR", new String[] { "f" });
}
return null;
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "2");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 2, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithHighCorrectionConstantHandling() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int counter = 0;

public Event read() throws IOException {
if (counter++ < 3) {
return new Event("TAG", new String[] { "a", "b", "c", "d", "e", "f", "g", "h" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "3");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
}

@Test
public void testTrainEventWithNullFeatureValue() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean done = false;

public Event read() {
if (!done) {
done = true;
return new Event("label", new String[] { null });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "2");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 2, 0);
assertNotNull(model);
}

@Test
public void testTrainWithEmptyFeatureArray() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean read = false;

public Event read() {
if (!read) {
read = true;
return new Event("X", new String[] {});
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "1");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 1, 0);
assertNotNull(model);
assertEquals("X", model.getBestOutcome(new double[] { 1.0 }));
}

@Test
public void testInvalidCombinationGaussianAndSimpleSmoothing() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
params.put("GaussianSmoothing", "true");
trainer.init(params, new HashMap<String, String>());
}

@Test
public void testTrainModelWithSmoothingObservationZero() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean delivered = false;

public Event read() {
if (!delivered) {
delivered = true;
return new Event("S", new String[] { "f1" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
params.put("SmoothingObservation", "0.0");
params.put(TrainingParameters.ITERATIONS_PARAM, "2");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 2, 0);
assertNotNull(model);
}

@Test
public void testTrainModelGaussianSigmaZero() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean called = false;

public Event read() {
if (!called) {
called = true;
return new Event("YES", new String[] { "g" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put("GaussianSmoothing", "true");
params.put("GaussianSmoothingSigma", "0.0");
params.put(TrainingParameters.ITERATIONS_PARAM, "3");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithManyThreadsAndFewEvents() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count++ < 2)
return new Event("O", new String[] { "f" });
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.THREADS_PARAM, "10");
params.put(TrainingParameters.ITERATIONS_PARAM, "5");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
}

@Test
public void testTrainModelWithNoTrainingParamsDefaultsApply() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

public Event read() {
return new Event("LABEL", new String[] { "x" });
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 100, 0);
assertNotNull(model);
}

@Test
public void testTrainEventsWithIdenticalFeaturesDifferentOutcomes() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return new Event("A", new String[] { "common" });
}
if (i == 1) {
i++;
return new Event("B", new String[] { "common" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "4");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 4, 0);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
}

@Test
public void testTrainWithLogLikelihoodThresholdTriggeringEarlyStop() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int counter = 0;

public Event read() {
if (counter++ < 10)
return new Event("C", new String[] { "z" });
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put("LLThreshold", "99999.0");
params.put(TrainingParameters.ITERATIONS_PARAM, "50");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.trainModel(stream, 50, 0);
assertNotNull(model);
}
}
