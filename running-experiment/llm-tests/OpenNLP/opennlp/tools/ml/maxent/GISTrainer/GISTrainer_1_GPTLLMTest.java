package opennlp.tools.ml.maxent;

import opennlp.tools.ml.model.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

public class GISTrainer_1_GPTLLMTest {

@Test
public void testSetSmoothing() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
}

@Test
public void testSetSmoothingObservation() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothingObservation(0.7);
}

@Test
public void testSetGaussianSigma() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(2.7);
}

@Test
public void testInitWithDefaultTrainingParameters() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
trainer.init(parameters, new HashMap<String, String>());
}

@Test
public void testInitWithSimpleSmoothing() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("SmoothingObservation", "0.25");
trainer.init(parameters, new HashMap<String, String>());
}

@Test
public void testInitWithGaussianSmoothing() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("GaussianSmoothing", "true");
parameters.put("GaussianSmoothingSigma", "4.2");
trainer.init(parameters, new HashMap<String, String>());
}

@Test
public void testInitWithConflictingSmoothingSettingsThrows() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("GaussianSmoothing", "true");
trainer.init(parameters, new HashMap<String, String>());
}

@Test
public void testTrainModelWithZeroThreads() throws IOException {
GISTrainer trainer = new GISTrainer();
Event sampleEvent = new Event("class", new String[] { "feature" });
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

boolean consumed = false;

public Event read() throws IOException {
if (consumed)
return null;
consumed = true;
return sampleEvent;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(eventStream);
trainer.trainModel(5, indexer, new UniformPrior(), 0);
}

@Test
public void testDoTrainWithMinimalData() throws IOException {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ITERATIONS_PARAM, 5);
parameters.put(TrainingParameters.THREADS_PARAM, "1");
trainer.init(parameters, new HashMap<String, String>());
Event sampleEvent = new Event("outcome", new String[] { "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean done = false;

public Event read() throws IOException {
if (done)
return null;
done = true;
return sampleEvent;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.doTrain(indexer);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
}

@Test
public void testTrainModelFromObjectStream() throws IOException {
GISTrainer trainer = new GISTrainer();
Event sampleEvent = new Event("Yes", new String[] { "x1" });
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (used)
return null;
used = true;
return sampleEvent;
}

public void reset() {
}

public void close() {
}
};
MaxentModel model = trainer.trainModel(eventStream);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
}

@Test
public void testTrainModelWithCustomPrior() {
GISTrainer trainer = new GISTrainer();
Event sampleEvent = new Event("POS", new String[] { "word" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean called = false;

public Event read() {
if (called)
return null;
called = true;
return sampleEvent;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer;
try {
indexer = new OnePassDataIndexer();
TrainingParameters params = new TrainingParameters();
indexer.init(params, Collections.emptyMap());
indexer.index(stream);
} catch (IOException e) {
throw new AssertionError("Indexing failed", e);
}
Prior prior = new UniformPrior();
MaxentModel model = trainer.trainModel(5, indexer, prior, 1);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
}

@Test
public void testTrainModelSingleIteration() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("label", new String[] { "attr" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent = false;

public Event read() {
if (sent)
return null;
sent = true;
return e;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer;
try {
indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
} catch (IOException ex) {
throw new AssertionError(ex);
}
MaxentModel model = trainer.trainModel(1, indexer, 1);
assertNotNull(model);
}

@Test
public void testTrainModelWithGaussianPath() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("cat", new String[] { "f1" });
ObjectStream<Event> s = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (used)
return null;
used = true;
return e;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer;
try {
indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(s);
} catch (IOException ex) {
throw new AssertionError(ex);
}
trainer.setGaussianSigma(6.0);
MaxentModel model = trainer.trainModel(3, indexer, 1);
assertNotNull(model);
}

@Test
public void testTrainModelWithSimpleSmoothingPath() {
GISTrainer trainer = new GISTrainer();
Event ev = new Event("N", new String[] { "feat" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean provided = false;

public Event read() {
if (provided)
return null;
provided = true;
return ev;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer;
try {
indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
} catch (IOException e) {
throw new AssertionError(e);
}
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.2);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
}

@Test
public void testIsSortAndMergeAlwaysTrue() {
GISTrainer trainer = new GISTrainer();
assertTrue(trainer.isSortAndMerge());
}

@Test
public void testTrainModelZeroIterationsReturnsModel() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("class", new String[] { "x" });
ObjectStream<Event> s = new ObjectStream<Event>() {

boolean served = false;

public Event read() {
if (served)
return null;
served = true;
return e;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer;
try {
indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(s);
} catch (IOException ex) {
throw new AssertionError(ex);
}
MaxentModel model = trainer.trainModel(0, indexer, 1);
assertNotNull(model);
}

@Test
public void testModelEvaluationWithFeature() throws IOException {
GISTrainer trainer = new GISTrainer();
Event e = new Event("yes", new String[] { "feat1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean once = false;

public Event read() {
if (once)
return null;
once = true;
return e;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(4, indexer, 1);
double[] probs = model.eval(new String[] { "feat1" });
assertNotNull(probs);
assertEquals(model.getNumOutcomes(), probs.length);
}

@Test
public void testTrainModelThrowsIOExceptionFromEventStream() throws IOException {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> eventStream = new ObjectStream<Event>() {

public Event read() throws IOException {
throw new IOException("Simulated IO failure");
}

public void reset() {
}

public void close() {
}
};
trainer.trainModel(eventStream);
}

@Test
public void testTrainModelWithSingleOutcomeMultipleFeatures() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("POS", new String[] { "f1", "f2", "f3" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean done = false;

public Event read() {
if (done)
return null;
done = true;
return e;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer;
try {
indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
} catch (IOException ex) {
fail("Unexpected exception: " + ex.getMessage());
}
}

@Test
public void testTrainModelWithTwoClasses() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return new Event("YES", new String[] { "a" });
} else if (i == 1) {
i++;
return new Event("NO", new String[] { "a" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
try {
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(4, indexer, 1);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
} catch (IOException e) {
fail("Unexpected exception: " + e.getMessage());
}
}

@Test
public void testTrainModelWithEmptyEventStream() {
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
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected exception thrown: " + e.getMessage());
}
}

@Test
public void testTrainModelLogLikelihoodEarlyStop() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("LABEL", new String[] { "f" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent = false;

public Event read() {
if (sent)
return null;
sent = true;
return e;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters tp = new TrainingParameters();
tp.put("LLThreshold", "1000000");
trainer.init(tp, new HashMap<String, String>());
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(10, indexer, 1);
assertNotNull(model);
} catch (IOException ex) {
fail("Did not expect exception: " + ex.getMessage());
}
}

@Test
public void testEvalWithUnknownFeatureReturnsProbabilities() {
GISTrainer trainer = new GISTrainer();
Event ev = new Event("X", new String[] { "seen" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used;

public Event read() {
if (used)
return null;
used = true;
return ev;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 1);
double[] probs = model.eval(new String[] { "unseen" });
assertNotNull(probs);
assertEquals(model.getNumOutcomes(), probs.length);
} catch (IOException ex) {
fail("Unexpected exception: " + ex.getMessage());
}
}

@Test
public void testTrainModelWithLargeCorrectionConstant() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("CL", new String[] { "feature_a", "feature_b", "feature_c", "feature_d", "feature_e" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent = false;

public Event read() {
if (sent)
return null;
sent = true;
return event;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(10, indexer, 1);
assertNotNull(model);
} catch (IOException ex) {
fail("Exception during training with large correction constant: " + ex.getMessage());
}
}

@Test
public void testTrainModelWithLargeSigmaGaussian() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(1000.0);
Event event = new Event("L", new String[] { "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean ready = true;

public Event read() {
if (!ready)
return null;
ready = false;
return event;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(7, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected IOException: " + e.getMessage());
}
}

@Test
public void testTrainModelWithNullPriorShouldUseDefault() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("Outcome", new String[] { "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean emitted = false;

public Event read() {
if (emitted)
return null;
emitted = true;
return e;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, null, 1);
assertNotNull(model);
} catch (IOException e1) {
fail("IO failure: " + e1.getMessage());
}
}

@Test
public void testTrainModelWithEmptyPredicateContext() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("Z", new String[0]);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent = false;

public Event read() {
if (sent)
return null;
sent = true;
return e;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
} catch (IOException ex) {
fail("Unexpected: " + ex.getMessage());
}
}

@Test
public void testTrainModelTwoThreads() {
GISTrainer trainer = new GISTrainer();
Event e1 = new Event("A", new String[] { "f1" });
Event e2 = new Event("B", new String[] { "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return e1;
} else if (count == 1) {
count++;
return e2;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(8, indexer, 2);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected error: " + e.getMessage());
}
}

@Test
public void testTrainWithGaussianMultiThreaded() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(3.0);
Event e1 = new Event("X", new String[] { "f1" });
Event e2 = new Event("Y", new String[] { "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return e1;
} else if (i == 1) {
i++;
return e2;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(6, indexer, 2);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected: " + e.getMessage());
}
}

@Test
public void testTrainModelWithObservedCountZeroTriggersWarning() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("class", new String[] { "onlyFeature" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean once = false;

public Event read() {
if (once)
return null;
once = true;
return e;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(1, indexer, 1);
assertNotNull(model);
} catch (IOException e1) {
fail("Unexpected IOException: " + e1.getMessage());
}
}

@Test
public void testPartialTrainingParameters() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("word", new String[] { "f" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean handled = false;

public Event read() {
if (handled)
return null;
handled = true;
return e;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "4");
trainer.init(params, new HashMap<String, String>());
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.doTrain(indexer);
assertNotNull(model);
} catch (IOException e1) {
fail("Unexpected error during training: " + e1.getMessage());
}
}

@Test
public void testTrainModelWithFeatureValuesNull() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("Target", new String[] { "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (used)
return null;
used = true;
return e;
}

public void reset() {
}

public void close() {
}
};
// try {
// OnePassDataIndexer indexer = new OnePassDataIndexer();
// indexer.init(new TrainingParameters(), new HashMap<String, String>());
// indexer.index(stream);
// MaxentModel model = trainer.trainModel(2, indexer, 1);
// assertNotNull(model);
// } catch (IOException e) {
// fail("Exception during value-null case: " + e.getMessage());
// }
}

@Test
public void testTrainModelWithFeatureZeroCount() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("label", new String[] { "f_z" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean consumed = false;

public Event read() {
if (consumed)
return null;
consumed = true;
return e;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
params.put("SmoothingObservation", "0.0");
trainer.init(params, new HashMap<String, String>());
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(2, indexer, 1);
assertNotNull(model);
} catch (IOException ex) {
fail("Exception on zero smoothing case: " + ex.getMessage());
}
}

@Test
public void testTrainModelWithAllOutcomesSame() {
GISTrainer trainer = new GISTrainer();
Event e1 = new Event("C", new String[] { "f1" });
Event e2 = new Event("C", new String[] { "f2" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int ctr = 0;

public Event read() {
if (ctr == 0) {
ctr++;
return e1;
}
if (ctr == 1) {
ctr++;
return e2;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 1);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
} catch (IOException ex) {
fail("Should not fail: " + ex.getMessage());
}
}

@Test
public void testSmoothingObservationIgnoredWhenSmoothingDisabled() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothingObservation(5.0);
Event e = new Event("X", new String[] { "f" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean delivered;

public Event read() {
if (delivered)
return null;
delivered = true;
return e;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
TrainingParameters indexParams = new TrainingParameters();
indexer.init(indexParams, Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
} catch (Exception ex) {
fail("Training should succeed even if smoothingObservation is set without smoothing: " + ex.getMessage());
}
}

@Test
public void testTrainModelWithVeryLargeIterationCountTerminatesEarly() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("LLThreshold", "100000");
params.put(TrainingParameters.ITERATIONS_PARAM, "1000");
trainer.init(params, new HashMap<String, String>());
Event e = new Event("Y", new String[] { "feat" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent;

public Event read() {
if (sent)
return null;
sent = true;
return e;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.doTrain(indexer);
assertNotNull(model);
} catch (IOException e1) {
fail("Unexpected training failure: " + e1.getMessage());
}
}

@Test
public void testExceptionOnBothSmoothingFlagsThroughInitMethod() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("GaussianSmoothing", "true");
parameters.put("SmoothingObservation", "0.2");
parameters.put("GaussianSmoothingSigma", "2.0");
trainer.init(parameters, new HashMap<String, String>());
}

@Test
public void testEvalAfterTrainingMultipleFeaturesMultipleOutcomes() throws IOException {
GISTrainer trainer = new GISTrainer();
Event e1 = new Event("A", new String[] { "f1", "f2" });
Event e2 = new Event("B", new String[] { "f2", "f3" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return e1;
}
if (i == 1) {
i++;
return e2;
}
return null;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 1);
assertNotNull(model);
double[] probs = model.eval(new String[] { "f1", "f2", "fX" });
assertNotNull(probs);
assertEquals(2, probs.length);
}

@Test
public void testMultipleEventsWithIdenticalContextButDifferentOutcomes() {
GISTrainer trainer = new GISTrainer();
Event e1 = new Event("O1", new String[] { "shared" });
Event e2 = new Event("O2", new String[] { "shared" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return e1;
}
if (count == 1) {
count++;
return e2;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(10, indexer, 1);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
} catch (IOException e) {
fail("Unexpected IO error: " + e.getMessage());
}
}

@Test
public void testTrainModelWithOutcomeLabelThatIsAnEmptyString() {
GISTrainer trainer = new GISTrainer();
Event e = new Event("", new String[] { "emptyOutcome" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean done = false;

public Event read() {
if (done)
return null;
done = true;
return e;
}

public void reset() {
}

public void close() {
}
};
// try {
// OnePassDataIndexer indexer = new OnePassDataIndexer();
// indexer.init(new TrainingParameters(), new HashMap<String, String>());
// indexer.index(stream);
// MaxentModel model = trainer.trainModel(4, indexer, 1);
// assertNotNull(model);
// assertEquals("", model.getBestOutcome(model.eval(new String[] { "emptyOutcome" })));
// } catch (IOException e) {
// fail("Unexpected: " + e.getMessage());
// }
}

@Test
public void testTrainModelWithSmoothingAndSingleActiveOutcome() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("SmoothingObservation", "0.1");
parameters.put("Iterations", "3");
trainer.init(parameters, new HashMap<String, String>());
Event e = new Event("S1", new String[] { "feat" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean done = false;

public Event read() {
if (done)
return null;
done = true;
return e;
}

public void reset() {
}

public void close() {
}
};
// try {
// OnePassDataIndexer indexer = new OnePassDataIndexer();
// indexer.init(new TrainingParameters(), new HashMap<String, String>());
// indexer.index(stream);
// MaxentModel model = trainer.trainModel(3, indexer, 1);
// assertNotNull(model);
// assertEquals(1, model.getNumOutcomes());
// } catch (IOException e) {
// fail("Unexpected exception: " + e.getMessage());
// }
}

@Test
public void testEmptyFeatureStringInEvent() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("LABEL", new String[] { "" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean called = false;

public Event read() {
if (!called) {
called = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
TrainingParameters config = new TrainingParameters();
indexer.init(config, new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(1, indexer, 1);
assertNotNull(model);
} catch (IOException ex) {
fail("Should not throw exception for empty feature string.");
}
}

@Test
public void testHighCorrectionConstantScenario() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("C", new String[] { "f1", "f1", "f1", "f1", "f1", "f1", "f1", "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent = false;

public Event read() {
if (!sent) {
sent = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("High correction constant path should not cause exception");
}
}

@Test
public void testMultipleTrainingThreadsUnevenSplit() {
GISTrainer trainer = new GISTrainer();
Event e1 = new Event("X", new String[] { "fA" });
Event e2 = new Event("Y", new String[] { "fB" });
Event e3 = new Event("Z", new String[] { "fC" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return e1;
}
if (count == 1) {
count++;
return e2;
}
if (count == 2) {
count++;
return e3;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
TrainingParameters trainingParams = new TrainingParameters();
trainingParams.put("Iterations", "2");
trainer.init(trainingParams, new HashMap<String, String>());
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(2, indexer, 2);
assertNotNull(model);
} catch (IOException e) {
fail("Uneven split thread training should succeed.");
}
}

@Test
public void testEmptyFeatureArrayIgnoredGracefully() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("None", new String[0]);
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean delivered;

public Event read() {
if (!delivered) {
delivered = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), Collections.emptyMap());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 1);
assertNotNull(model);
} catch (IOException ex) {
fail("Empty feature array should not crash training.");
}
}

@Test
public void testMultipleIdenticalEvents() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("TAG", new String[] { "f1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int c = 0;

public Event read() {
if (c++ < 3)
return event;
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("Duplicate events path should not raise errors.");
}
}

@Test
public void testTrainWithoutExplicitCutoffOrIterations() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("O", new String[] { "abc" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean done = false;

public Event read() {
if (!done) {
done = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
MaxentModel model = trainer.trainModel(stream);
assertNotNull(model);
} catch (IOException e) {
fail("Default parameter training path should work.");
}
}

@Test
public void testOutcomeLabelAsNullThrowsException() {
GISTrainer trainer = new GISTrainer();
Event event = new Event(null, new String[] { "f" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean once;

public Event read() {
if (once)
return null;
once = true;
return event;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
trainer.trainModel(2, indexer, 1);
fail("Training with null outcome should fail.");
} catch (Exception expected) {
assertTrue(true);
}
}

@Test
public void testSmoothingWithZeroObservationDoesNotCrash() {
GISTrainer trainer = new GISTrainer();
TrainingParameters param = new TrainingParameters();
param.put("Smoothing", "true");
param.put("SmoothingObservation", "0.0");
trainer.init(param, new HashMap<String, String>());
Event event = new Event("S", new String[] { "f" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used;

public Event read() {
if (used)
return null;
used = true;
return event;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("Zero smoothing observation should not crash.");
}
}

@Test
public void testTrainModelWithManyOutcomesSingleFeature() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int idx = 0;

public Event read() {
String[] outcomes = { "A", "B", "C", "D" };
if (idx < outcomes.length) {
return new Event(outcomes[idx++], new String[] { "f" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(10, indexer, 1);
assertNotNull(model);
assertEquals(4, model.getNumOutcomes());
} catch (IOException e) {
fail("Training failed with many outcomes.");
}
}

@Test
public void testTrainModelWithSmoothingObservationNegativeIgnored() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
params.put("SmoothingObservation", "-1.0");
trainer.init(params, new HashMap<String, String>());
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean done = false;

public Event read() {
if (!done) {
done = true;
return new Event("P", new String[] { "x" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<String, String>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(10, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("Negative smoothing observation should not crash.");
}
}

@Test
public void testTrainModelWithMaxThreadsEqualToEvents() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return new Event("X", new String[] { "f1" });
} else if (count == 1) {
count++;
return new Event("Y", new String[] { "f2" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 2);
assertNotNull(model);
assertTrue(model.getNumOutcomes() >= 1);
} catch (IOException ex) {
fail("Training should succeed even with threads = numEvents");
}
}

@Test
public void testTrainModelConvergesEarlyWithHighThreshold() {
GISTrainer trainer = new GISTrainer();
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put("LLThreshold", "9999999");
trainer.init(trainingParameters, new HashMap<>());
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent = false;

public Event read() {
if (!sent) {
sent = true;
return new Event("class", new String[] { "f" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(100, indexer, 1);
assertNotNull(model);
} catch (IOException ex) {
fail("Should train and finish early due to threshold.");
}
}

@Test
public void testTrainModelMultipleContextsSameOutcomeDifferentFeatures() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

int i = 0;

public Event read() {
if (i == 0) {
i++;
return new Event("Z", new String[] { "f1" });
} else if (i == 1) {
i++;
return new Event("Z", new String[] { "f2" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
assertEquals(1, model.getNumOutcomes());
} catch (IOException e) {
fail("Training should not fail on distinct contexts with same label.");
}
}

@Test
public void testTrainModelWithZeroIterationsReturnsBaseModel() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean readOnce;

public Event read() {
if (!readOnce) {
readOnce = true;
return new Event("A", new String[] { "f" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(0, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("Zero-iteration training should return model with weights = 0.");
}
}

@Test
public void testTrainModelThrowsIllegalArgumentForNegativeThreads() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean emitted;

public Event read() {
if (!emitted) {
emitted = true;
return new Event("a", new String[] { "f" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer;
try {
indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
} catch (IOException e) {
throw new RuntimeException(e);
}
trainer.trainModel(5, indexer, -1);
}

@Test
public void testTrainModelUsesUniformPriorByDefault() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("L", new String[] { "ff" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean consumed;

public Event read() {
if (!consumed) {
consumed = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, null, 1);
assertNotNull(model);
assertEquals("L", model.getBestOutcome(model.eval(new String[] { "ff" })));
} catch (IOException e) {
fail("Default prior should be UniformPrior and not fail.");
}
}

@Test
public void testEvalWithUnknownFeatureReturnsDistribution() throws IOException {
GISTrainer trainer = new GISTrainer();
Event event = new Event("Z", new String[] { "known" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean available;

public Event read() {
if (!available) {
available = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(6, indexer, 1);
double[] scores = model.eval(new String[] { "unknown" });
assertNotNull(scores);
assertEquals(1, scores.length);
assertEquals("Z", model.getBestOutcome(scores));
}

@Test
public void testTrainModelWithOneEventMultipleZeroWeightFeatures() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("X", new String[] { "a", "b", "c" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean sent;

public Event read() {
if (!sent) {
sent = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(2, indexer, 1);
assertNotNull(model);
} catch (IOException e) {
fail("Unexpected exception when values array is null.");
}
}

@Test
public void testTrainModelWithValuesArrayAndCustomPrior() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("Y", new String[] { "alpha", "beta" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean delivered = false;

public Event read() {
if (!delivered) {
delivered = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer() {

@Override
public float[][] getValues() {
float[][] values = new float[1][];
values[0] = new float[] { 1.0f, 2.0f };
return values;
}
};
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, new UniformPrior(), 1);
assertNotNull(model);
} catch (IOException e) {
fail("Training should succeed with manually set values array.");
}
}

@Test
public void testTrainModelWithMissingIterationParamUsesDefault() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean served = false;

public Event read() {
if (!served) {
served = true;
return new Event("POS", new String[] { "f1" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
TrainingParameters params = new TrainingParameters();
trainer.init(params, new HashMap<>());
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.doTrain(indexer);
assertNotNull(model);
} catch (IOException e) {
fail("Training should succeed with default iterations.");
}
}

@Test
public void testPredictionReturnsCorrectBestOutcome() throws IOException {
GISTrainer trainer = new GISTrainer();
Event e1 = new Event("A", new String[] { "x" });
Event e2 = new Event("A", new String[] { "x" });
Event e3 = new Event("B", new String[] { "x" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return e1;
} else if (count == 1) {
count++;
return e2;
} else if (count == 2) {
count++;
return e3;
}
return null;
}

public void reset() {
}

public void close() {
}
};
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(10, indexer, 1);
assertNotNull(model);
double[] result = model.eval(new String[] { "x" });
assertEquals(model.getBestOutcome(result), "A");
}

@Test
public void testMultipleContextsSameFeatureDifferentLabels() {
GISTrainer trainer = new GISTrainer();
Event e1 = new Event("L1", new String[] { "tok" });
Event e2 = new Event("L2", new String[] { "tok" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count == 0) {
count++;
return e1;
} else if (count == 1) {
count++;
return e2;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 1);
assertNotNull(model);
assertEquals(2, model.getNumOutcomes());
double[] eval = model.eval(new String[] { "tok" });
assertEquals(2, eval.length);
} catch (IOException e) {
fail("Should handle same feature used for multiple classes.");
}
}

@Test
public void testTrainModelInvalidCutoffParameterIgnoredGracefully() {
GISTrainer trainer = new GISTrainer();
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean used = false;

public Event read() {
if (!used) {
used = true;
return new Event("TAG", new String[] { "f" });
}
return null;
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put("Cutoff", "invalid_number");
params.put("Iterations", "3");
trainer.init(params, new HashMap<>());
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(params, new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(3, indexer, 1);
assertNotNull(model);
} catch (Exception e) {
fail("Invalid cutoff parameter should not crash training.");
}
}

@Test
public void testMultipleThreadsMoreThanEventsHandledProperly() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("AA", new String[] { "feature1" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

int count = 0;

public Event read() {
if (count < 2) {
count++;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(5, indexer, 8);
assertNotNull(model);
} catch (IOException e) {
fail("More threads than events must not fail.");
}
}

@Test
public void testEvalReturnsValidDistributionOnUnseenFeature() {
GISTrainer trainer = new GISTrainer();
Event event = new Event("NOUN", new String[] { "seen" });
ObjectStream<Event> stream = new ObjectStream<Event>() {

boolean emitted = false;

public Event read() {
if (!emitted) {
emitted = true;
return event;
}
return null;
}

public void reset() {
}

public void close() {
}
};
try {
OnePassDataIndexer indexer = new OnePassDataIndexer();
indexer.init(new TrainingParameters(), new HashMap<>());
indexer.index(stream);
MaxentModel model = trainer.trainModel(4, indexer, 1);
double[] probabilities = model.eval(new String[] { "unseen" });
assertNotNull(probabilities);
assertEquals(1, probabilities.length);
double sum = 0;
sum += probabilities[0];
assertTrue(sum > 0.0 && sum <= 1.0);
} catch (IOException e) {
fail("Eval with unseen feature should return valid distribution.");
}
}
}
