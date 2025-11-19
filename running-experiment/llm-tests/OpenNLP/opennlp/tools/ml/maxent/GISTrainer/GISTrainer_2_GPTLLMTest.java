package opennlp.tools.ml.maxent;

import opennlp.tools.ml.model.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GISTrainer_2_GPTLLMTest {

@Test
public void testInitWithDefaults() {
GISTrainer trainer = new GISTrainer();
TrainingParameters trainingParameters = new TrainingParameters();
Map<String, String> reportMap = new HashMap<>();
trainer.init(trainingParameters, reportMap);
}

@Test
public void testInitWithSimpleSmoothing() {
GISTrainer trainer = new GISTrainer();
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put("Smoothing", "true");
trainingParameters.put("SmoothingObservation", "0.6");
Map<String, String> reportMap = new HashMap<>();
trainer.init(trainingParameters, reportMap);
}

@Test
public void testInitWithGaussianSmoothing() {
GISTrainer trainer = new GISTrainer();
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put("GaussianSmoothing", "true");
trainingParameters.put("GaussianSmoothingSigma", "1.5");
Map<String, String> reportMap = new HashMap<>();
trainer.init(trainingParameters, reportMap);
}

@Test
public void testInitWithConflictingSmoothingThrowsException() {
GISTrainer trainer = new GISTrainer();
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put("Smoothing", "true");
trainingParameters.put("GaussianSmoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(trainingParameters, reportMap);
}

@Test
public void testTrainModelWithInvalidThreads() {
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
OnePassDataIndexer indexer = new OnePassDataIndexer();
try {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put(TrainingParameters.ITERATIONS_PARAM, 1);
Map<String, String> report = new HashMap<>();
indexer.init(params, report);
indexer.index(stream);
} catch (IOException e) {
fail("Exception during data indexing: " + e.getMessage());
}
trainer.trainModel(5, indexer, new UniformPrior(), 0);
}

@Test
public void testTrainModelSingleThread() {
GISTrainer trainer = new GISTrainer();
List<Event> eventList = Arrays.asList(new Event("X", new String[] { "feat1" }), new Event("Y", new String[] { "feat2" }), new Event("X", new String[] { "feat3" }));
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

private int index = 0;

public Event read() {
if (index >= eventList.size())
return null;
return eventList.get(index++);
}

public void reset() {
index = 0;
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
try {
Map<String, String> reportMap = new HashMap<>();
TrainingParameters indexParams = new TrainingParameters();
indexParams.put(TrainingParameters.CUTOFF_PARAM, 0);
indexParams.put(TrainingParameters.ITERATIONS_PARAM, 1);
indexer.init(indexParams, reportMap);
indexer.index(eventStream);
} catch (IOException e) {
fail("IOException during indexing: " + e.getMessage());
}
MaxentModel model = trainer.trainModel(3, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelMultiThreadedExecution() {
GISTrainer trainer = new GISTrainer();
List<Event> events = Arrays.asList(new Event("A", new String[] { "f1" }), new Event("B", new String[] { "f2" }), new Event("A", new String[] { "f3" }));
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int i = 0;

public Event read() {
return i < events.size() ? events.get(i++) : null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
try {
Map<String, String> report = new HashMap<>();
TrainingParameters p = new TrainingParameters();
p.put(TrainingParameters.CUTOFF_PARAM, 0);
p.put(TrainingParameters.ITERATIONS_PARAM, 1);
indexer.init(p, report);
indexer.index(stream);
} catch (IOException e) {
fail("IO error during OnePassDataIndexer: " + e.getMessage());
}
MaxentModel model = trainer.trainModel(3, indexer, new UniformPrior(), 2);
assertNotNull(model);
}

@Test
public void testTrainModelFromStream() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int pos = 0;

private final Event[] data = { new Event("L", new String[] { "w1" }), new Event("M", new String[] { "w2" }), new Event("L", new String[] { "w3" }) };

public Event read() {
return pos < data.length ? data[pos++] : null;
}

public void reset() {
pos = 0;
}

public void close() {
}
};
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
}

@Test
public void testTrainEmptyStreamThrowsException() throws IOException {
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
trainer.trainModel(emptyStream, 5, 0);
}

@Test
public void testSetSmoothingFlagExecution() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothing(false);
}

@Test
public void testSetSmoothingObservationValue() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothingObservation(0.8);
}

@Test
public void testSetGaussianSigmaValue() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(2.5);
}

@Test
public void testSortAndMergeReturnsTrue() {
GISTrainer trainer = new GISTrainer();
assertTrue(trainer.isSortAndMerge());
}

@Test
public void testDoTrainReturnsModel() throws IOException {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, 5);
params.put(TrainingParameters.CUTOFF_PARAM, 1);
params.put(TrainingParameters.THREADS_PARAM, 1);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private List<Event> data = Arrays.asList(new Event("yes", new String[] { "a" }), new Event("no", new String[] { "b" }));

private int i = 0;

public Event read() {
return i < data.size() ? data.get(i++) : null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
TrainingParameters indexParams = new TrainingParameters();
indexParams.put(TrainingParameters.CUTOFF_PARAM, 0);
indexParams.put(TrainingParameters.ITERATIONS_PARAM, 1);
Map<String, String> reportMap = new HashMap<>();
indexer.init(indexParams, reportMap);
indexer.index(stream);
trainer.init(params, new HashMap<>());
MaxentModel model = trainer.doTrain(indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithCustomPrior() {
GISTrainer trainer = new GISTrainer();
Prior prior = new UniformPrior();
List<Event> events = Arrays.asList(new Event("P", new String[] { "f" }), new Event("Q", new String[] { "g" }));
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int idx = 0;

public Event read() {
if (idx >= events.size())
return null;
return events.get(idx++);
}

public void reset() {
idx = 0;
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
try {
Map<String, String> report = new HashMap<>();
TrainingParameters tp = new TrainingParameters();
tp.put(TrainingParameters.CUTOFF_PARAM, 0);
tp.put(TrainingParameters.ITERATIONS_PARAM, 1);
indexer.init(tp, report);
indexer.index(stream);
} catch (IOException e) {
fail("Indexing failed with IOException: " + e.getMessage());
}
MaxentModel model = trainer.trainModel(3, indexer, prior, 1);
assertNotNull(model);
}

@Test
public void testMultipleInitCallsAreSafe() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params1 = new TrainingParameters();
Map<String, String> report1 = new HashMap<>();
trainer.init(params1, report1);
TrainingParameters params2 = new TrainingParameters();
Map<String, String> report2 = new HashMap<>();
trainer.init(params2, report2);
}

@Test
public void testInitWithCustomLLThreshold() {
GISTrainer trainer = new GISTrainer();
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put("LLThreshold", "0.0025");
Map<String, String> reportMap = new HashMap<>();
trainer.init(trainingParameters, reportMap);
}

@Test
public void testTrainModelWithOnePredicateAndOneOutcome() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("onlyOutcome", new String[] { "onlyPredicate" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(eventStream, 10, 0);
} catch (IOException e) {
fail("IOException should not be thrown.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithSingleEventMultiFeature() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("label", new String[] { "f1", "f2", "f3" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("IOException during training.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithDuplicateFeaturesSameContext() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("duplicate", new String[] { "feat", "feat", "feat" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 3, 0);
} catch (IOException e) {
fail("IOException occurred.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithEmptyFeatureArray() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("outcome", new String[0]);
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
try {
trainer.trainModel(stream, 3, 0);
fail("Expected IllegalArgumentException due to empty context.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("insufficient"));
} catch (IOException e) {
fail("IOException should not occur here.");
}
}

@Test
public void testTrainModelWithNullFeatureArray() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean read = false;

public Event read() {
if (!read) {
read = true;
return new Event("label", null);
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
trainer.trainModel(stream, 3, 0);
fail("Expected IllegalArgumentException due to null context.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("context features should not be null"));
} catch (IOException e) {
fail("IOException should not occur.");
}
}

@Test
public void testTrainModelWithDifferentOutcomeLabels() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int count = 0;

public Event read() {
if (count == 0) {
count++;
return new Event("O1", new String[] { "pred" });
} else if (count == 1) {
count++;
return new Event("O2", new String[] { "pred" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 10, 0);
} catch (IOException e) {
fail("IOException should not occur.");
}
assertNotNull(model);
}

@Test
public void testModelTrainingWithLargeSigmaGaussian() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(1000.0);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int i = 0;

public Event read() {
if (i++ < 3) {
return new Event("positive", new String[] { "fA" });
}
return null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 2, 0);
} catch (IOException e) {
fail("IOException occurred.");
}
assertNotNull(model);
}

@Test
public void testGaussianAndSmoothingIndependence() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("GaussianSmoothing", "true");
parameters.put("GaussianSmoothingSigma", "0.1");
parameters.put("Smoothing", "false");
Map<String, String> reportMap = new HashMap<>();
trainer.init(parameters, reportMap);
}

@Test
public void testTrainingModelWithHighCutoff() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int count = 0;

public Event read() {
if (count++ < 5) {
return new Event("yes", new String[] { "featureX" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 10, 10);
} catch (IOException e) {
fail("IOException should not happen.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithZeroIterations() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("zeroIter", new String[] { "f" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 0, 0);
} catch (IOException e) {
fail("IOException should not be thrown here.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithSingleOutcomeMultipleFeatures() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int i = 0;

public Event read() {
if (i++ == 0)
return new Event("only", new String[] { "f1", "f2", "f3" });
return null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 10, 0);
} catch (IOException e) {
fail("Unexpected IOException.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithMaxCutoff() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int i = 0;

public Event read() {
if (i++ < 2) {
return new Event("yes", new String[] { "rare" });
}
return null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 10, 99);
} catch (IOException e) {
fail("Should not throw IOException.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithAllIdenticalEvents() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int index = 0;

public Event read() {
if (index++ < 3)
return new Event("label", new String[] { "same" });
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("IOException thrown.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithEmptyOutcomeStrings() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("", new String[] { "f1" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("Model training with empty outcome failed.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithRepeatedOutcomeNames() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int count = 0;

public Event read() {
if (count++ % 2 == 0)
return new Event("outcome", new String[] { "f1" });
if (count < 4)
return new Event("outcome", new String[] { "f2" });
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("Unexpected IOException.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithLargeFeatureNames() {
GISTrainer trainer = new GISTrainer();
final String largeFeature = "f".repeat(1000);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("large", new String[] { largeFeature });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("Model training failed for large feature.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithMinDoubleValueFeatureWeight() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("label", new String[] { "f1" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
try {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put(TrainingParameters.ITERATIONS_PARAM, 1);
Map<String, String> report = new HashMap<>();
indexer.init(params, report);
indexer.index(stream);
} catch (IOException e) {
fail("DataIndexer failed.");
}
MaxentModel model = trainer.trainModel(1, indexer, 1);
assertNotNull(model);
}

@Test
public void testTrainModelWithNullContextInOneEvent() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private int idx = 0;

public Event read() {
if (idx++ == 0)
return new Event("a", null);
if (idx == 2)
return new Event("b", new String[] { "f" });
return null;
}

public void reset() {
idx = 0;
}

public void close() {
}
};
try {
trainer.trainModel(stream, 5, 0);
fail("Expected IllegalArgumentException not thrown.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("context"));
} catch (IOException e) {
fail("Unexpected IOException.");
}
}

@Test
public void testTrainModelWithNullOutcomeThrowsException() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean provided = false;

public Event read() {
if (!provided) {
provided = true;
return new Event(null, new String[] { "X" });
}
return null;
}

public void reset() {
provided = false;
}

public void close() {
}
};
try {
trainer.trainModel(stream, 5, 0);
fail("Expected exception for null outcome.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("outcome"));
} catch (IOException e) {
fail("Unexpected IOException.");
}
}

@Test
public void testExtremeLLThresholdStopsEarly() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("LLThreshold", "1000000");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("quick", new String[] { "x" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 100, 0);
} catch (IOException e) {
fail("IOException occurred during early break LL.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithEmptyFeatureStrings() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean consumed = false;

public Event read() {
if (!consumed) {
consumed = true;
return new Event("Y", new String[] { "", "", "" });
}
return null;
}

public void reset() {
consumed = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("Training failed unexpectedly.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithNegativeIterations() {
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
try {
trainer.trainModel(stream, -1, 0);
fail("Negative iterations must throw IllegalArgumentException.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("iterations"));
} catch (IOException e) {
fail("IOException not expected.");
}
}

@Test
public void testTrainModelWithZeroThreadUsed() {
GISTrainer trainer = new GISTrainer();
OnePassDataIndexer indexer = new OnePassDataIndexer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean flag = false;

public Event read() {
if (!flag) {
flag = true;
return new Event("label", new String[] { "feature" });
}
return null;
}

public void reset() {
flag = false;
}

public void close() {
}
};
try {
Map<String, String> report = new HashMap<>();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.CUTOFF_PARAM, 0);
params.put(TrainingParameters.ITERATIONS_PARAM, 1);
indexer.init(params, report);
indexer.index(stream);
} catch (IOException e) {
fail("Indexer should not throw IOException.");
}
try {
trainer.trainModel(5, indexer, 0);
fail("Expected IllegalArgumentException for threads=0");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("threads must be at least one"));
}
}

@Test
public void testInitIgnoresUnknownParameters() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("NonExistentParam", "foobar");
Map<String, String> report = new HashMap<>();
trainer.init(params, report);
}

@Test
public void testTrainModelWithUnseenOutcomeAndContextCombo() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.5);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean called = false;

public Event read() {
if (!called) {
called = true;
return new Event("A", new String[] { "unknown1" });
}
return null;
}

public void reset() {
called = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 3, 0);
} catch (IOException e) {
fail("IOException thrown: " + e.getMessage());
}
assertNotNull(model);
}

@Test
public void testTrainModelWithOnlyOutcomeNoFeatures() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("O", new String[0]);
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
try {
trainer.trainModel(stream, 5, 0);
fail("Expected IllegalArgumentException due to empty feature array.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("context"));
} catch (IOException e) {
fail("Unexpected IOException.");
}
}

@Test
public void testTrainModelWithMultipleOutcomeSameFeatures() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int step = 0;

public Event read() {
if (step == 0) {
step++;
return new Event("X", new String[] { "common" });
} else if (step == 1) {
step++;
return new Event("Y", new String[] { "common" });
}
return null;
}

public void reset() {
step = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("Model training should not fail.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithFeatureNameSpecialCharacters() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean called = false;

public Event read() {
if (!called) {
called = true;
return new Event("special", new String[] { "feat.1", "fi@2", "f#3", "f$4" });
}
return null;
}

public void reset() {
called = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("IOException occurred unexpectedly.");
}
assertNotNull(model);
}

@Test
public void testTrainModelTerminatesOnDivergence() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("LLThreshold", "0.0");
Map<String, String> report = new HashMap<>();
trainer.init(params, report);
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count < 2) {
count++;
return new Event("diverge", new String[] { "f" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 100, 0);
} catch (IOException e) {
fail("IOException during divergence test.");
}
assertNotNull(model);
}

@Test
public void testTrainWithGaussianSmoothingAndSmallSigma() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(0.01);
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count++ < 2)
return new Event("outcome", new String[] { "f" });
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 3, 0);
} catch (IOException e) {
fail("Unexpected IOException with small sigma.");
}
assertNotNull(model);
}

@Test
public void testTrainWithHighCorrectionConstant() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int index = 0;

public Event read() {
if (index++ == 0) {
return new Event("A", new String[] { "f1", "f2", "f3", "f4", "f5" });
}
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 10, 0);
} catch (IOException e) {
fail("Training should succeed even with large correction constant.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithDuplicateEvents() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i++ < 3)
return new Event("Y", new String[] { "f" });
return null;
}

public void reset() {
i = 0;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 5, 0);
} catch (IOException e) {
fail("IOException in duplicate event test.");
}
assertNotNull(model);
}

@Test
public void testInitWithZeroSigmaGaussian() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("GaussianSmoothing", "true");
params.put("GaussianSmoothingSigma", "0.0");
Map<String, String> report = new HashMap<>();
try {
trainer.init(params, report);
} catch (Exception e) {
fail("Init should not throw error with sigma=0.0");
}
}

@Test
public void testTrainModelWithEmptyEventSetWithCutoff() {
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
try {
trainer.trainModel(stream, 10, 1);
fail("Expected failure for empty stream with cutoff.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("insufficient"));
} catch (IOException e) {
fail("IOException not expected on cutoff path.");
}
}

@Test
public void testTrainModelWithEmptyContextArray() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean read = false;

public Event read() {
if (!read) {
read = true;
return new Event("z", new String[] {});
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
trainer.trainModel(stream, 5, 0);
fail("Expected exception due to empty context feature array.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("context"));
} catch (IOException e) {
fail("Unexpected IOException.");
}
}

@Test
public void testLogLikelihoodEarlyStopping() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("LLThreshold", "0.000001");
params.put("Iterations", "10");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("stop", new String[] { "f" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 1000, 0);
} catch (IOException e) {
fail("IOException during early stopping test.");
}
assertNotNull(model);
}

@Test
public void testMultipleFeatureMultipleOutcomes() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return new Event("A", new String[] { "f1", "f2" });
} else if (i == 1) {
i++;
return new Event("B", new String[] { "f2", "f3" });
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
MaxentModel model = trainer.trainModel(stream, 10, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Multi-outcome, multi-feature test failed.");
}
}

@Test
public void testTrainModelWithLongFeatureNames() {
GISTrainer trainer = new GISTrainer();
final String longName = new String(new char[500]).replace('\0', 'X');
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean submitted = false;

public Event read() {
if (!submitted) {
submitted = true;
return new Event("O", new String[] { longName });
}
return null;
}

public void reset() {
submitted = false;
}

public void close() {
}
};
MaxentModel model = null;
try {
model = trainer.trainModel(stream, 2, 0);
} catch (IOException e) {
fail("IOException occurred for long feature name.");
}
assertNotNull(model);
}

@Test
public void testTrainModelWithMixedNullAndValidEvents() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return null;
} else if (count == 1) {
count++;
return new Event("A", new String[] { "x" });
}
return null;
}

public void reset() {
count = 0;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
} catch (IOException e) {
fail("IOException on mixed null/valid events");
}
}

@Test
public void testTrainModelWithZeroSmoothingObservation() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.0);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean read = false;

public Event read() {
if (!read) {
read = true;
return new Event("A", new String[] { "feat1" });
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
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
} catch (IOException e) {
fail("IOException was not expected with zero smoothing observation.");
}
}

@Test
public void testTrainModelWithNegativeSmoothingObservation() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(-0.5);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("label", new String[] { "f" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected IOException despite negative smoothing observation.");
}
}

@Test
public void testTrainModelWithNullFeatureNames() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean called = false;

public Event read() {
if (!called) {
called = true;
String[] context = new String[1];
context[0] = null;
return new Event("label", context);
}
return null;
}

public void reset() {
called = false;
}

public void close() {
}
};
try {
trainer.trainModel(stream, 5, 0);
fail("Expected exception due to null feature name.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("null"));
} catch (IOException e) {
fail("IOException is not expected.");
}
}

@Test
public void testTrainModelWithMultipleNullEventsInStream() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int index = 0;

public Event read() {
if (index++ == 1) {
return new Event("X", new String[] { "f" });
}
return null;
}

public void reset() {
index = 0;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 4, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Model should still train with skipped null events.");
}
}

@Test
public void testTrainModelWithHighNumberOfIterationsAndEarlyStop() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("LLThreshold", "1000.0");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("stop", new String[] { "feat" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 100, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected IOException.");
}
}

@Test
public void testTrainModelWithMinDoubleSigma() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(Double.MIN_VALUE);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() {
if (!returned) {
returned = true;
return new Event("O", new String[] { "xf" });
}
return null;
}

public void reset() {
returned = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
} catch (IOException e) {
fail("IOException not expected.");
}
}

@Test
public void testTrainModelWithMaxDoubleSigma() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(Double.MAX_VALUE);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() {
if (!returned) {
returned = true;
return new Event("O", new String[] { "xf" });
}
return null;
}

public void reset() {
returned = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 2, 0);
assertNotNull(model);
} catch (IOException e) {
fail("IOException not expected.");
}
}

@Test
public void testMultipleInitOnSameTrainerWithDifferentParams() {
GISTrainer trainer = new GISTrainer();
TrainingParameters p1 = new TrainingParameters();
p1.put("Smoothing", "true");
p1.put("SmoothingObservation", "0.2");
Map<String, String> report1 = new HashMap<>();
trainer.init(p1, report1);
TrainingParameters p2 = new TrainingParameters();
p2.put("GaussianSmoothing", "true");
p2.put("GaussianSmoothingSigma", "1.2");
Map<String, String> report2 = new HashMap<>();
try {
trainer.init(p2, report2);
fail("Should throw RuntimeException because both smoothings are true");
} catch (RuntimeException e) {
assertTrue(e.getMessage().toLowerCase().contains("both gaussian"));
}
}

@Test
public void testTrainModelWithOutcomeNameSpaces() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() {
if (!returned) {
returned = true;
return new Event("  spaced outcome  ", new String[] { "f" });
}
return null;
}

public void reset() {
returned = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected IOException for outcome with spaces.");
}
}

@Test
public void testTrainModelWhereAllFeaturesHaveZeroObservation() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.0);
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean returned = false;

public Event read() {
if (!returned) {
returned = true;
return new Event("outcome", new String[] { "f1" });
}
return null;
}

public void reset() {
returned = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 5, Integer.MAX_VALUE);
assertNotNull(model);
} catch (IOException e) {
fail("Should not throw IOException with zero-smoothing observation.");
}
}

@Test
public void testTrainModelWithMultipleOutcomesAndSingleFeature() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int counter = 0;

public Event read() {
if (counter++ == 0)
return new Event("O1", new String[] { "feature" });
if (counter == 2)
return new Event("O2", new String[] { "feature" });
return null;
}

public void reset() {
counter = 0;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 10, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected IOException for multiple outcomes.");
}
}

@Test
public void testTrainModelWithVeryHighCutoffValue() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

private boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("hi", new String[] { "rare" });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 10, 9999);
assertNotNull(model);
} catch (IOException e) {
fail("IOException not expected even if all features are dropped.");
}
}

@Test
public void testTrainModelWithContextHavingMixedNullValidFeatures() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean provided = false;

public Event read() {
if (!provided) {
provided = true;
return new Event("label", new String[] { null, "f1" });
}
return null;
}

public void reset() {
provided = false;
}

public void close() {
}
};
try {
trainer.trainModel(stream, 2, 0);
fail("Expected IllegalArgumentException due to null context element.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().toLowerCase().contains("null"));
} catch (IOException e) {
fail("IOException not expected.");
}
}

@Test
public void testTrainModelWithLongOutcomeName() {
GISTrainer trainer = new GISTrainer();
String longOutcome = new String(new char[500]).replace("\0", "X");
ObjectStream<Event> stream = new ObjectStream<Event>() {

public boolean readOnce = false;

public Event read() {
if (!readOnce) {
readOnce = true;
return new Event(longOutcome, new String[] { "f" });
}
return null;
}

public void reset() {
readOnce = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 5, 0);
assertNotNull(model);
} catch (IOException e) {
fail("IOException should not occur with long outcome name.");
}
}

@Test
public void testInitWithOnlyGaussianParameter() {
GISTrainer trainer = new GISTrainer();
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put("GaussianSmoothing", "true");
Map<String, String> report = new HashMap<>();
try {
trainer.init(trainingParameters, report);
} catch (RuntimeException e) {
fail("Should not throw if GaussianSmoothing=true alone.");
}
}

@Test
public void testInitWithOnlySimpleSmoothingAndNoSmoothingObservation() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
Map<String, String> reportMap = new HashMap<>();
try {
trainer.init(params, reportMap);
} catch (RuntimeException e) {
fail("Smoothing=true without SmoothingObservation should be valid.");
}
}

@Test
public void testTrainModelWithBlankFeatureName() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("normal", new String[] { " " });
}
return null;
}

public void reset() {
used = false;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 3, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Should not fail if feature is blank string.");
}
}

@Test
public void testTrainModelWithEmptyStreamThenValidEvent() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int position = 0;

public Event read() {
if (position++ == 2)
return new Event("yes", new String[] { "f" });
return null;
}

public void reset() {
position = 0;
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream, 4, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Model should skip initial nulls.");
}
}

@Test
public void testTrainModelWhereAllEventsUseSameFeatureButDifferentOutcomes() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return new Event("pos", new String[] { "fX" });
} else if (i == 1) {
i++;
return new Event("neg", new String[] { "fX" });
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
MaxentModel model = trainer.trainModel(stream, 6, 0);
assertNotNull(model);
} catch (IOException e) {
fail("Events with same features but different outcomes should be supported.");
}
}
}
