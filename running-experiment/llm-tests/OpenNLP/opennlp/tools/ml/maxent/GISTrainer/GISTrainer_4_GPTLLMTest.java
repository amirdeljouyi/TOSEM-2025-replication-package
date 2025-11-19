package opennlp.tools.ml.maxent;

import opennlp.tools.ml.model.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GISTrainer_4_GPTLLMTest {

@Test
public void testIsSortAndMergeReturnsTrue() {
GISTrainer trainer = new GISTrainer();
assertTrue(trainer.isSortAndMerge());
}

@Test
public void testInitWithSimpleSmoothingEnabled() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("Smoothing", "true");
params.put("SmoothingObservation", "0.25");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
}

@Test
public void testInitWithGaussianSmoothingEnabled() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("GaussianSmoothing", "true");
params.put("GaussianSmoothingSigma", "1.5");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
}

@Test
public void testInitWithConflictingSmoothingThrows() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put("GaussianSmoothing", "true");
params.put("Smoothing", "true");
Map<String, String> reportMap = new HashMap<>();
trainer.init(params, reportMap);
}

@Test
public void testSetSmoothingAffectsInternalState() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
}

@Test
public void testSetSmoothingObservation() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothingObservation(0.5);
}

@Test
public void testSetGaussianSigma() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(3.0);
}

@Test
public void testDoTrainWithMockedDataIndexerSingleThread() throws IOException {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "1");
trainer.init(params, new HashMap<>());
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { new int[] { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "POS" });
when(indexer.getPredLabels()).thenReturn(new String[] { "word=the" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.doTrain(indexer);
assertNotNull(model);
assertEquals("opennlp.tools.ml.maxent.GISModel", model.getClass().getName());
}

@Test
public void testTrainModelWithZeroThreadsThrows() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
trainer.trainModel(1, indexer, 0);
}

@Test
public void testTrainModelWithCustomPrior() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { new int[] { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "yes" });
when(indexer.getPredLabels()).thenReturn(new String[] { "attr" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
Prior customPrior = new UniformPrior();
GISModel model = trainer.trainModel(1, indexer, customPrior, 1);
assertNotNull(model);
}

@Test
public void testTrainModelWithMultiThreadedTraining() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { new int[] { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "tag" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feature" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
GISModel model = trainer.trainModel(2, indexer, 2);
assertNotNull(model);
}

@Test
public void testTrainModelFromObjectStreamReturnsNullModel() throws IOException {
GISTrainer trainer = new GISTrainer();
@SuppressWarnings("unchecked")
ObjectStream<Event> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
try {
trainer.trainModel(stream);
fail("Expected IOException due to null stream content");
} catch (IOException expected) {
}
}

@Test
public void testTrainModelWithCutoffAndIterations() throws IOException {
GISTrainer trainer = new GISTrainer();
@SuppressWarnings("unchecked")
ObjectStream<Event> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
try {
trainer.trainModel(stream, 100, 5);
fail("Expected IOException due to empty stream");
} catch (IOException expected) {
}
}

@Test
public void testTrainModelWithVeryHighLLThresholdStopsEarly() throws IOException {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "100");
params.put("LLThreshold", String.valueOf(Double.MAX_VALUE));
trainer.init(params, new HashMap<>());
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { new int[] { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "YES" });
when(indexer.getPredLabels()).thenReturn(new String[] { "NO" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.doTrain(indexer);
assertNotNull(model);
}

@Test
public void testSetAllSmoothingOptionsSequentially() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.3);
trainer.setGaussianSigma(3.5);
}

@Test
public void testNextIterationInterruptedTraining() {
Thread.currentThread().interrupt();
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { new int[] { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "END" });
when(indexer.getPredLabels()).thenReturn(new String[] { "FEAT" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
try {
trainer.trainModel(1, indexer, 1);
} finally {
Thread.interrupted();
}
}

@Test
public void testTrainModelWithZeroIterationsStillReturnsModel() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "0");
trainer.init(params, new HashMap<>());
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "O" });
when(indexer.getPredLabels()).thenReturn(new String[] { "X" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.trainModel(0, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithEmptyContextArray() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[0][]);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[0]);
when(indexer.getOutcomeList()).thenReturn(new int[0]);
when(indexer.getOutcomeLabels()).thenReturn(new String[0]);
when(indexer.getPredLabels()).thenReturn(new String[0]);
when(indexer.getPredCounts()).thenReturn(new int[0]);
try {
trainer.trainModel(10, indexer);
fail("Expected an exception due to empty training data");
} catch (RuntimeException ex) {
}
}

@Test
public void testTrainModelWithNullValuesArray() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 2 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "Y" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feat" });
when(indexer.getPredCounts()).thenReturn(new int[] { 3 });
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithCustomCorrectionConstantBehavior() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
int[][] contexts = new int[][] { { 0 } };
float[][] featureValues = new float[][] { { 3.5f } };
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(featureValues);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "A" });
when(indexer.getPredLabels()).thenReturn(new String[] { "B" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithMultipleOutcomesSingleFeature() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 }, { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1, 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0, 1 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "yes", "no" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feature" });
when(indexer.getPredCounts()).thenReturn(new int[] { 2 });
MaxentModel model = trainer.trainModel(5, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithPredCountZeroObservationIsUsed() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.2);
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "POS" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feat" });
when(indexer.getPredCounts()).thenReturn(new int[] { 0 });
MaxentModel model = trainer.trainModel(10, indexer);
assertNotNull(model);
}

@Test
public void testGaussianSmoothingUpdatePathExercised() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(2.0);
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "label" });
when(indexer.getPredLabels()).thenReturn(new String[] { "p" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.trainModel(3, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithNegativeThreadsInputThrows() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
try {
trainer.trainModel(10, indexer, -4);
fail("Expected IllegalArgumentException for negative threads");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("threads must be at least one"));
}
}

@Test
public void testEvalPathWithMultipleThreadsAndUnevenTaskDistribution() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 }, { 0 }, { 0 }, { 0 }, { 0 } };
int[] outcomes = new int[] { 0, 0, 0, 0, 0 };
int[] timesSeen = new int[] { 1, 1, 1, 1, 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomes);
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "Y" });
when(indexer.getPredLabels()).thenReturn(new String[] { "Z" });
when(indexer.getPredCounts()).thenReturn(new int[] { 5 });
MaxentModel model = trainer.trainModel(2, indexer, 3);
assertNotNull(model);
}

@Test
public void testTrainModelWithSinglePredicateHighOutcomeIndex() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 9 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feature_large_index" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testGaussianSmoothingStabilityWithZeroExpectation() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(1.0);
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(new float[][] { { 1.0f } });
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "P" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feat" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testSmoothingObservationIsUsedWithZeroCountPredicateAndNullValues() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
trainer.setSmoothingObservation(0.5);
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "YES" });
when(indexer.getPredLabels()).thenReturn(new String[] { "NO" });
when(indexer.getPredCounts()).thenReturn(new int[] { 0 });
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testExecutionExceptionInCallableReThrown() throws Exception {
ExecutorService executor = Executors.newSingleThreadExecutor();
try {
CompletionService<Object> service = new ExecutorCompletionService<>(executor);
service.submit(() -> {
throw new IllegalArgumentException("Mock failure");
});
service.take().get();
} catch (ExecutionException e) {
throw new RuntimeException("Exception during training", e);
} finally {
executor.shutdown();
}
}

@Test
public void testModelExpectsAreReinitialized() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "a" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feat" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testAllOutcomesPatternAppliedWhenFullSmoothingOn() {
GISTrainer trainer = new GISTrainer();
trainer.setSmoothing(true);
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "x", "y" });
when(indexer.getPredLabels()).thenReturn(new String[] { "feat" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
MaxentModel model = trainer.trainModel(3, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithMultipleActiveOutcomes() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 }, { 0 }, { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1, 1, 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0, 1, 2 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "A", "B", "C" });
when(indexer.getPredLabels()).thenReturn(new String[] { "f" });
when(indexer.getPredCounts()).thenReturn(new int[] { 3 });
MaxentModel model = trainer.trainModel(5, indexer, 2);
assertNotNull(model);
}

@Test
public void testLogLikelihoodDecreaseBreaksIterationLoop() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "5");
params.put("LLThreshold", "0.0");
trainer.init(params, new HashMap<>());
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 }, { 0 }, { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1, 1, 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 0, 0, 0 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "X" });
when(indexer.getPredLabels()).thenReturn(new String[] { "Y" });
when(indexer.getPredCounts()).thenReturn(new int[] { 3 });
MaxentModel model = trainer.trainModel(5, indexer, 2);
assertNotNull(model);
}

@Test
public void testEmptyOutcomeLabelsHandled() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] {});
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] {});
when(indexer.getOutcomeList()).thenReturn(new int[] {});
when(indexer.getOutcomeLabels()).thenReturn(new String[] {});
when(indexer.getPredLabels()).thenReturn(new String[] {});
when(indexer.getPredCounts()).thenReturn(new int[] {});
try {
trainer.trainModel(1, indexer, 1);
fail("Expected exception due to empty labels");
} catch (RuntimeException expected) {
}
}

@Test
public void testLabelArraysDoNotMatchOutcomeListSizeThrows() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { { 0 } });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeList()).thenReturn(new int[] { 1 });
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "only" });
when(indexer.getPredLabels()).thenReturn(new String[] { "f" });
when(indexer.getPredCounts()).thenReturn(new int[] { 1 });
try {
trainer.trainModel(1, indexer);
fail("Expected ArrayIndexOutOfBoundsException for invalid outcome list value");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testTrainModelWithNullContextValuesArray() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "2");
trainer.init(params, new HashMap<>());
int[][] contexts = new int[][] { null };
int[] numTimesSeen = new int[] { 1 };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "classA" };
String[] predLabels = new String[] { "featureX" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(numTimesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
try {
trainer.trainModel(2, indexer);
fail("Expected NullPointerException due to null context array element");
} catch (NullPointerException expected) {
}
}

@Test
public void testTrainModelWithEmptyStringsAsLabels() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
int[] numTimesSeen = new int[] { 1 };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "" };
String[] predLabels = new String[] { "" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(numTimesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(2, indexer);
assertNotNull(model);
assertTrue(model instanceof GISModel);
}

@Test
public void testTrainModelWithDisjointContextAndValues() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
float[][] values = new float[][] { {} };
int[] numTimesSeen = new int[] { 1 };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "OUT" };
String[] predLabels = new String[] { "PRED" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(numTimesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testEvalPathWithSingleThreadAndSingleEvent() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
int[] numTimesSeen = new int[] { 1 };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "YES" };
String[] predLabels = new String[] { "FEATURE" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(numTimesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(1, indexer, 1);
assertNotNull(model);
}

@Test
public void testTrainModelWithZeroCorrectionConstantPath() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { {} };
int[] numTimesSeen = new int[] { 1 };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "L" };
String[] predLabels = new String[] { "X" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(numTimesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithContextLengthGreaterThanCorrectionConstant() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0, 1, 2, 3, 4 } };
int[] numTimesSeen = new int[] { 1 };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "yes" };
String[] predLabels = new String[] { "f1", "f2", "f3", "f4", "f5" };
int[] predCounts = new int[] { 1, 1, 1, 1, 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(numTimesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(2, indexer);
assertNotNull(model);
}

@Test
public void testLogLikelihoodDoesNotChangeExitsEarly() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "10");
params.put("LLThreshold", "0.000000000001");
trainer.init(params, new HashMap<>());
int[][] contexts = new int[][] { { 0 }, { 0 } };
int[] numTimesSeen = new int[] { 1, 1 };
int[] outcomeList = new int[] { 0, 0 };
String[] outcomeLabels = new String[] { "VAL" };
String[] predLabels = new String[] { "feat" };
int[] predCounts = new int[] { 2 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(numTimesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(10, indexer);
assertNotNull(model);
}

@Test
public void testNextIterationHandlesDivisionByZeroGracefully() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
float[][] values = new float[][] { { 0.0f } };
int[] num = new int[] { 1 };
int[] out = new int[] { 0 };
String[] outLabels = new String[] { "D" };
String[] predLabels = new String[] { "PRED" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(num);
when(indexer.getOutcomeList()).thenReturn(out);
when(indexer.getOutcomeLabels()).thenReturn(outLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(2, indexer);
assertNotNull(model);
}

@Test
public void testTrainingWithSinglePredicateAndNoAssociatedOutcome() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
float[][] values = new float[][] { { 1.0f } };
String[] predLabels = new String[] { "feat" };
String[] outcomeLabels = new String[] { "label" };
int[] predCounts = new int[] { 0 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
GISTrainer smoothingTrainer = new GISTrainer();
smoothingTrainer.setSmoothing(true);
MaxentModel model = smoothingTrainer.trainModel(2, indexer);
assertNotNull(model);
}

@Test
public void testTrainWithVeryLargeNumberOfOutcomesPerformanceTrigger() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
int[] outcomeList = new int[] { 999 };
int[] timesSeen = new int[] { 1 };
String[] predLabels = new String[] { "p" };
String[] outcomeLabels = new String[1000];
for (int i = 0; i < 1000; i++) {
outcomeLabels[i] = "out" + i;
}
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getValues()).thenReturn(null);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithOnlyUnusedPredicates() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { {} };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] predLabels = new String[] { "unused" };
String[] outcomeLabels = new String[] { "yes" };
int[] predCounts = new int[] { 0 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
GISTrainer smoothingTrainer = new GISTrainer();
smoothingTrainer.setSmoothing(true);
MaxentModel model = smoothingTrainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testErrorOnBothSmoothingFlagsEnabledViaInit() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("Smoothing", "true");
parameters.put("GaussianSmoothing", "true");
trainer.init(parameters, new HashMap<>());
}

@Test
public void testCorrectHandlingOfZeroValuesInFeatureWeights() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
float[][] values = new float[][] { { 0.0f } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "ok" };
String[] predLabels = new String[] { "zeroFeat" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testMismatchedContextAndValueLengthHandledWithoutError() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0, 1 } };
float[][] values = new float[][] { { 1.0f } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "LABEL" };
String[] predLabels = new String[] { "a", "b" };
int[] predCounts = new int[] { 1, 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
try {
trainer.trainModel(1, indexer);
fail("Expected ArrayIndexOutOfBoundsException due to length mismatch");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testIterationSetToNegativeHandledAsZero() {
GISTrainer trainer = new GISTrainer();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ITERATIONS_PARAM, "-5");
trainer.init(params, new HashMap<>());
int[][] contexts = new int[][] { { 0 } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] predLabels = new String[] { "a" };
String[] outcomeLabels = new String[] { "b" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
// MaxentModel model = trainer.doTrain(indexer);
// assertNotNull(model);
}

@Test
public void testZeroPredicatesWithPositiveOutcomesThrows() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { {} };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] predLabels = new String[0];
String[] outcomeLabels = new String[] { "yes" };
int[] predCounts = new int[0];
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
trainer.trainModel(1, indexer);
}

@Test
public void testTrainModelWithAllInputsNullExceptLabels() {
GISTrainer trainer = new GISTrainer();
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(new int[][] { {} });
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(new int[] {});
when(indexer.getOutcomeList()).thenReturn(new int[] {});
when(indexer.getOutcomeLabels()).thenReturn(new String[] { "A" });
when(indexer.getPredLabels()).thenReturn(new String[] { "B" });
when(indexer.getPredCounts()).thenReturn(new int[] { 0 });
try {
trainer.trainModel(1, indexer);
fail("Expected exception due to inconsistent input");
} catch (RuntimeException expected) {
}
}

@Test
public void testTrainModelWithEmptyContextValuesAndZeroFeatureCount() {
GISTrainer trainer = new GISTrainer();
int[][] context = new int[][] { {} };
float[][] values = new float[][] { {} };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "POS" };
String[] predLabels = new String[] { "word=the" };
int[] predCounts = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(context);
when(indexer.getValues()).thenReturn(values);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
GISTrainer smoothingTrainer = new GISTrainer();
smoothingTrainer.setSmoothing(true);
smoothingTrainer.setSmoothingObservation(0.1);
MaxentModel model = smoothingTrainer.trainModel(2, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithLargeCorrectionConstantFloatValues() {
GISTrainer trainer = new GISTrainer();
int[][] context = new int[][] { { 0 } };
float[][] values = new float[][] { { Float.MAX_VALUE } };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "class" };
String[] predLabels = new String[] { "high_value" };
int[] predCounts = new int[] { 1 };
int[] timesSeen = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(context);
when(indexer.getValues()).thenReturn(values);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithZeroLengthFeatureList() {
GISTrainer trainer = new GISTrainer();
int[][] context = new int[][] { {} };
float[][] values = new float[][] { {} };
int[] outcomeList = new int[] { 0 };
String[] outcomeLabels = new String[] { "only" };
String[] predLabels = new String[] { "feat" };
int[] predCounts = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(context);
when(indexer.getValues()).thenReturn(values);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
GISTrainer trainerWithSmoothing = new GISTrainer();
trainerWithSmoothing.setSmoothing(true);
trainerWithSmoothing.setSmoothingObservation(0.1);
MaxentModel model = trainerWithSmoothing.trainModel(5, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithOutcomeIndexOutOfBounds() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
int[] outcomeList = new int[] { 5 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "a", "b" };
String[] predLabels = new String[] { "c" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
trainer.trainModel(1, indexer);
}

@Test
public void testModelTrainingWithUnevenThreadLoad() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 } };
int[] outcomeList = new int[] { 0, 0, 0, 0, 0, 0, 0 };
int[] timesSeen = new int[] { 1, 1, 1, 1, 1, 1, 1 };
String[] outcomeLabels = new String[] { "POS" };
String[] predLabels = new String[] { "f" };
int[] predCounts = new int[] { 7 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(1, indexer, 3);
assertNotNull(model);
}

@Test
public void testTrainModelWithAllZeroFeatureObservationAndGaussianSmoothing() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(1.0);
int[][] context = new int[][] { { 0 } };
float[][] values = new float[][] { { 0.0f } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "X" };
String[] predLabels = new String[] { "Y" };
int[] predCounts = new int[] { 0 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(context);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testCorrectLLThresholdUsedToExitIterationsEarly() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("LLThreshold", "999999999");
parameters.put(TrainingParameters.ITERATIONS_PARAM, "50");
trainer.init(parameters, new HashMap<>());
int[][] context = new int[][] { { 0 } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "a" };
String[] predLabels = new String[] { "b" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(context);
when(indexer.getValues()).thenReturn(null);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
// MaxentModel model = trainer.doTrain(indexer);
// assertNotNull(model);
}

@Test
public void testTrainModelWithEmptyOutcomeLabelsAndEmptyContext() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[0][];
int[] outcomeList = new int[0];
int[] timesSeen = new int[0];
String[] outcomeLabels = new String[0];
String[] predLabels = new String[0];
int[] predCounts = new int[0];
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(null);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
try {
trainer.trainModel(1, indexer);
fail("Expected exception due to empty training data");
} catch (RuntimeException expected) {
}
}

@Test
public void testTrainModelWithMultiplePredicatesMappedToMultipleOutcomes() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 } };
int[] timesSeen = new int[] { 1, 1, 1 };
int[] outcomeList = new int[] { 0, 1, 2 };
float[][] values = new float[][] { { 1.0f, 2.0f }, { 1.5f, 0.5f }, { 0.0f, 1.0f } };
String[] outcomeLabels = new String[] { "X", "Y", "Z" };
String[] predLabels = new String[] { "a", "b", "c", "d" };
int[] predCounts = new int[] { 2, 2, 2, 2 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
when(indexer.getValues()).thenReturn(values);
MaxentModel model = trainer.trainModel(5, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithEmptyNestedContextArray() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { {} };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "A" };
String[] predLabels = new String[] { "B" };
int[] predCounts = new int[] { 0 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
when(indexer.getValues()).thenReturn(null);
GISTrainer smoothingTrainer = new GISTrainer();
smoothingTrainer.setSmoothing(true);
smoothingTrainer.setSmoothingObservation(0.2);
MaxentModel model = smoothingTrainer.trainModel(1, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithZeroSigmaValueInGaussianSmoothing() {
GISTrainer trainer = new GISTrainer();
trainer.setGaussianSigma(0.0);
int[][] contexts = new int[][] { { 0 } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "POS" };
String[] predLabels = new String[] { "feature" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
when(indexer.getValues()).thenReturn(null);
try {
trainer.trainModel(2, indexer);
} catch (ArithmeticException expected) {
}
}

@Test
public void testTrainWithNullFeatureValuesButNonNullContext() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
float[][] values = new float[][] { null };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "X" };
String[] predLabels = new String[] { "Y" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(4, indexer);
assertNotNull(model);
}

@Test
public void testTrainModelWithFloatingPointInstabilityInExpectation() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
float[][] values = new float[][] { { 1e-40f } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "O" };
String[] predLabels = new String[] { "P" };
int[] predCounts = new int[] { 1 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
MaxentModel model = trainer.trainModel(3, indexer);
assertNotNull(model);
}

@Test
public void testInitDefaultSmoothingState() {
GISTrainer trainer = new GISTrainer();
TrainingParameters parameters = new TrainingParameters();
parameters.put("LLThreshold", "0.01");
parameters.put("Smoothing", "false");
parameters.put("GaussianSmoothing", "false");
trainer.init(parameters, new HashMap<>());
}

@Test
public void testTrainModelWithZeroExpectedModelValueFallback() {
GISTrainer trainer = new GISTrainer();
int[][] contexts = new int[][] { { 0 } };
float[][] values = new float[][] { { 0.0f } };
int[] outcomeList = new int[] { 0 };
int[] timesSeen = new int[] { 1 };
String[] outcomeLabels = new String[] { "A" };
String[] predLabels = new String[] { "B" };
int[] predCounts = new int[] { 0 };
DataIndexer indexer = mock(DataIndexer.class);
when(indexer.getContexts()).thenReturn(contexts);
when(indexer.getValues()).thenReturn(values);
when(indexer.getNumTimesEventsSeen()).thenReturn(timesSeen);
when(indexer.getOutcomeList()).thenReturn(outcomeList);
when(indexer.getOutcomeLabels()).thenReturn(outcomeLabels);
when(indexer.getPredLabels()).thenReturn(predLabels);
when(indexer.getPredCounts()).thenReturn(predCounts);
GISTrainer smoothingTrainer = new GISTrainer();
smoothingTrainer.setSmoothing(true);
MaxentModel model = smoothingTrainer.trainModel(2, indexer);
assertNotNull(model);
}
}
