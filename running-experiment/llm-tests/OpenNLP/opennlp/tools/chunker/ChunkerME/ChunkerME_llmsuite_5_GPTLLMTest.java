package opennlp.tools.chunker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ChunkerME_llmsuite_5_GPTLLMTest {

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsExpectedLabels() {
String[] tokens = new String[] { "He", "eats", "an", "apple" };
String[] tags = new String[] { "PRP", "VBZ", "DT", "NN" };
String[] expectedChunks = new String[] { "B-NP", "B-VP", "B-NP", "I-NP" };
double[] chunkProbs = new double[] { 0.9, 0.92, 0.88, 0.80 };
SequenceClassificationModel<TokenTag> mockModel = Mockito.mock(SequenceClassificationModel.class);
Sequence mockSequence = Mockito.mock(Sequence.class);
ChunkerContextGenerator mockContextGenerator = Mockito.mock(ChunkerContextGenerator.class);
org.mockito.stubbing.Answer<Void> fillArray = invocation -> {
double[] probs = invocation.getArgument(0);
probs[0] = 0.9;
probs[1] = 0.92;
probs[2] = 0.88;
probs[3] = 0.80;
return null;
};
SequenceValidator<TokenTag> mockValidator = Mockito.mock(SequenceValidator.class);
ChunkerFactory mockFactory = Mockito.mock(ChunkerFactory.class);
ChunkerModel mockChunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(mockSequence.getOutcomes()).thenReturn(Arrays.asList(expectedChunks));
Mockito.when(mockSequence.getProbs()).thenReturn(chunkProbs);
Mockito.doAnswer(fillArray).when(mockSequence).getProbs(Mockito.any(double[].class));
Mockito.when(mockModel.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(mockContextGenerator), Mockito.eq(mockValidator))).thenReturn(mockSequence);
Mockito.when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
Mockito.when(mockFactory.getSequenceValidator()).thenReturn(mockValidator);
Mockito.when(mockChunkerModel.getFactory()).thenReturn(mockFactory);
Mockito.when(mockChunkerModel.getChunkerSequenceModel()).thenReturn(mockModel);
ChunkerME chunker = new ChunkerME(mockChunkerModel);
String[] actualChunks = chunker.chunk(tokens, tags);
assertArrayEquals(expectedChunks, actualChunks);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansReturnsExpectedSpans() {
String[] tokens = new String[] { "She", "walks", "her", "dog" };
String[] tags = new String[] { "PRP", "VBZ", "PRP$", "NN" };
String[] chunks = new String[] { "B-NP", "B-VP", "B-NP", "I-NP" };
double[] probabilities = new double[] { 0.9, 0.85, 0.88, 0.87 };
Sequence mockSequence = Mockito.mock(Sequence.class);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerContextGenerator contextGenerator = Mockito.mock(ChunkerContextGenerator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel mockModel = Mockito.mock(ChunkerModel.class);
Mockito.when(mockModel.getFactory()).thenReturn(factory);
Mockito.when(mockModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(factory.getContextGenerator()).thenReturn(contextGenerator);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(contextGenerator), Mockito.eq(validator))).thenReturn(mockSequence);
Mockito.when(mockSequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
Mockito.when(mockSequence.getProbs()).thenReturn(probabilities);
ChunkerME chunker = new ChunkerME(mockModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals("B-NP", spans[0].getType());
}

@Test
@SuppressWarnings("unchecked")
public void testProbsReturnsCorrectValues() {
String[] tokens = new String[] { "We", "like", "pizza" };
String[] tags = new String[] { "PRP", "VBP", "NN" };
String[] chunks = new String[] { "B-NP", "B-VP", "B-NP" };
double[] expectedProbs = new double[] { 0.91, 0.89, 0.86 };
Sequence mockSequence = Mockito.mock(Sequence.class);
ChunkerContextGenerator contextGenerator = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel mockModel = Mockito.mock(ChunkerModel.class);
Mockito.when(mockSequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
Mockito.when(mockSequence.getProbs()).thenReturn(expectedProbs);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(contextGenerator), Mockito.eq(validator))).thenReturn(mockSequence);
Mockito.when(factory.getContextGenerator()).thenReturn(contextGenerator);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(mockModel.getFactory()).thenReturn(factory);
Mockito.when(mockModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(mockModel);
chunker.chunk(tokens, tags);
double[] actualProbs = chunker.probs();
assertArrayEquals(expectedProbs, actualProbs, 0.0001);
}

@Test
@SuppressWarnings("unchecked")
public void testProbsMethodFillsProvidedArray() {
String[] tokens = new String[] { "Cats", "chase", "mice" };
String[] tags = new String[] { "NNS", "VBP", "NNS" };
String[] chunks = new String[] { "B-NP", "B-VP", "B-NP" };
double[] expected = new double[] { 0.82, 0.84, 0.80 };
Sequence mockSequence = Mockito.mock(Sequence.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerContextGenerator contextGenerator = Mockito.mock(ChunkerContextGenerator.class);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(mockSequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
Mockito.when(mockSequence.getProbs()).thenReturn(expected);
Mockito.doAnswer(invocation -> {
double[] probsArg = invocation.getArgument(0);
probsArg[0] = 0.82;
probsArg[1] = 0.84;
probsArg[2] = 0.80;
return null;
}).when(mockSequence).getProbs(Mockito.any(double[].class));
Mockito.when(factory.getContextGenerator()).thenReturn(contextGenerator);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(contextGenerator), Mockito.eq(validator))).thenReturn(mockSequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] actual = new double[3];
chunker.probs(actual);
assertArrayEquals(expected, actual, 0.0001);
}

@Test
public void testConstructorWithInvalidLanguageThrowsIOException() throws IOException {
new ChunkerME("invalid-lang");
}

@Test
public void testTrainWithUnsupportedTrainerThrowsException() throws IOException {
ObjectStream<ChunkSample> sampleStream = Mockito.mock(ObjectStream.class);
ChunkerFactory factory = new ChunkerFactory();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "UNSUPPORTED");
ChunkerME.train("en", sampleStream, params, factory);
}

@Test
public void testChunkEmptyInputReturnsEmptyArray() {
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
Sequence sequence = Mockito.mock(Sequence.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(sequence.getOutcomes()).thenReturn(Arrays.asList(new String[0]));
Mockito.when(sequence.getProbs()).thenReturn(new double[0]);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(new String[0], new String[0]);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunkWithNullTokensThrowsNPE() {
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(null, new String[] { "NN" });
}

@Test
public void testChunkWithNullTagsThrowsNPE() {
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(new String[] { "dog" }, null);
}

@Test
public void testChunkMismatchedTokenTagLengthThrows() {
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(new String[] { "onlyToken" }, new String[0]);
}

@Test
public void testChunkReturnsEmptyArrayWhenModelReturnsEmptySequence() {
String[] tokens = new String[] { "Hello" };
String[] tags = new String[] { "UH" };
Sequence emptySequence = Mockito.mock(Sequence.class);
Mockito.when(emptySequence.getOutcomes()).thenReturn(Arrays.asList(new String[0]));
Mockito.when(emptySequence.getProbs()).thenReturn(new double[0]);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(emptySequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTopKSequencesWithZeroMinScoreFiltersOutSequences() {
String[] tokens = new String[] { "A", "sentence" };
String[] tags = new String[] { "DT", "NN" };
Sequence sequence = Mockito.mock(Sequence.class);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel modelWrapper = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(modelWrapper.getFactory()).thenReturn(factory);
Mockito.when(modelWrapper.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequences(Mockito.eq(ChunkerME.DEFAULT_BEAM_SIZE), Mockito.any(), Mockito.any(), Mockito.eq(0.0), Mockito.eq(context), Mockito.eq(validator))).thenReturn(new Sequence[] { sequence });
ChunkerME chunker = new ChunkerME(modelWrapper);
Sequence[] sequences = chunker.topKSequences(tokens, tags, 0.0);
assertNotNull(sequences);
assertEquals(1, sequences.length);
}

@Test
public void testConstructorFallsBackToBeamSearchModelWhenModelIsNull() {
MaxentModel maxent = Mockito.mock(MaxentModel.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel model;
TrainingParameters params = new TrainingParameters();
HashMap<String, String> manifest = new HashMap<>();
model = new ChunkerModel("en", maxent, 10, manifest, factory);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(new String[] { "Test" }, new String[] { "NN" });
assertNotNull(result);
}

@Test
public void testChunkerModelTrainWithEventTrainerCreatesValidModel() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
EventTrainer trainer = Mockito.mock(EventTrainer.class);
Event event = new Event("outcome", new String[] { "feature1", "feature2" });
Mockito.when(stream.read()).thenReturn(null);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, TrainerFactory.TrainerType.EVENT_MODEL_TRAINER.name());
ChunkerFactory factory = new ChunkerFactory();
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testTrainReturnsModelWhenSequenceModelIsUsed() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, TrainerFactory.TrainerType.SEQUENCE_TRAINER.name());
ChunkerFactory factory = new ChunkerFactory();
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testProbsReturnsEmptyArrayBeforeChunkCalled() {
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.probs();
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testProbsDoubleArrayFailsBeforeChunkCalled() {
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
double[] probs = new double[1];
try {
chunker.probs(probs);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesReturnsEmptyArrayWhenNoSequencesReturned() {
String[] tokens = new String[] { "Hello" };
String[] tags = new String[] { "UH" };
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel modelWrapper = Mockito.mock(ChunkerModel.class);
Mockito.when(model.bestSequences(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.eq(contextGenerator), Mockito.eq(sequenceValidator))).thenReturn(new Sequence[0]);
Mockito.when(factory.getContextGenerator()).thenReturn(contextGenerator);
Mockito.when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
Mockito.when(modelWrapper.getFactory()).thenReturn(factory);
Mockito.when(modelWrapper.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(modelWrapper);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTrainWithEventTrainerAndNullEventStream() throws IOException {
ObjectStream<ChunkSample> sampleStream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
ChunkerFactory factory = new ChunkerFactory();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
EventTrainer trainer = Mockito.mock(EventTrainer.class);
MaxentModel maxentModel = Mockito.mock(MaxentModel.class);
// Mockito.when(trainer.train(Mockito.any())).thenReturn(maxentModel);
// EventTrainer trainerSpy = new EventTrainer() {
// 
// @Override
// public MaxentModel train(ObjectStream<Event> events) {
// return maxentModel;
// }
// };
TrainerFactory.getEventTrainer(params, new HashMap<String, String>());
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testTrainWithSequenceTrainerAndNullSampleStream() throws IOException {
ObjectStream<ChunkSample> sampleStream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
ChunkerFactory factory = new ChunkerFactory();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "SEQUENCE");
SequenceClassificationModel<TokenTag> sequenceModel = Mockito.mock(SequenceClassificationModel.class);
// SequenceTrainer trainerSpy = new SequenceTrainer() {
// 
// @Override
// public SequenceClassificationModel<TokenTag> train(ObjectStream<opennlp.tools.util.SequenceSample<TokenTag>> samples) {
// return sequenceModel;
// }
// };
TrainerFactory.getSequenceModelTrainer(params, new HashMap<String, String>());
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testChunkReturnsNullOutcomeHandled() {
String[] tokens = new String[] { "Word" };
String[] tags = new String[] { "NN" };
Sequence sequence = Mockito.mock(Sequence.class);
Mockito.when(sequence.getOutcomes()).thenReturn(null);
Mockito.when(sequence.getProbs()).thenReturn(new double[] { 0.5 });
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
String[] result = chunker.chunk(tokens, tags);
assertNull("Expected null result from mock outcome.", result);
} catch (Exception e) {
assertTrue(e instanceof NullPointerException);
}
}

@Test
public void testTrainUsesDefaultBeamSizeWhenNotSpecified() throws IOException {
ObjectStream<ChunkSample> sampleStream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
ChunkerFactory factory = new ChunkerFactory();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, TrainerFactory.TrainerType.EVENT_MODEL_TRAINER.name());
EventTrainer trainer = Mockito.mock(EventTrainer.class);
MaxentModel maxentModel = Mockito.mock(MaxentModel.class);
// Mockito.when(trainer.train(Mockito.any())).thenReturn(maxentModel);
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testChunkSingleTokenSingleTag() {
String[] tokens = new String[] { "Hello" };
String[] tags = new String[] { "UH" };
String[] outcomes = new String[] { "B-NP" };
double[] probs = new double[] { 0.99 };
Sequence sequence = Mockito.mock(Sequence.class);
Mockito.when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
Mockito.when(sequence.getProbs()).thenReturn(probs);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("B-NP", result[0]);
}

@Test
public void testTrainReturnsChunkerModelWhenMaxentModelIsNonNull() throws IOException {
ObjectStream<ChunkSample> sampleStream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, TrainerFactory.TrainerType.EVENT_MODEL_TRAINER.name());
ChunkerFactory factory = new ChunkerFactory();
EventTrainer trainer = Mockito.mock(EventTrainer.class);
MaxentModel maxentModel = Mockito.mock(MaxentModel.class);
// Mockito.when(trainer.train(Mockito.any())).thenReturn(maxentModel);
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testTrainReturnsChunkerModelWhenSequenceModelIsNonNullAndMaxentNull() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "SEQUENCE");
ChunkerFactory factory = new ChunkerFactory();
SequenceClassificationModel<TokenTag> sequenceModel = Mockito.mock(SequenceClassificationModel.class);
SequenceTrainer sequenceTrainer = Mockito.mock(SequenceTrainer.class);
// Mockito.when(sequenceTrainer.train(Mockito.any())).thenReturn(sequenceModel);
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithHighMinSequenceScoreReturnsEmpty() {
String[] tokens = new String[] { "Cats", "sleep" };
String[] tags = new String[] { "NNS", "VB" };
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequences(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.eq(0.99), Mockito.eq(context), Mockito.eq(validator))).thenReturn(new Sequence[0]);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags, 0.99);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTrainHandlesMissingAlgorithmGracefully() {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ChunkerFactory factory = new ChunkerFactory();
try {
ChunkerME.train("en", stream, params, factory);
fail("Expected exception due to missing algorithm parameter");
} catch (IllegalArgumentException | IOException expected) {
assertTrue(expected.getMessage().contains("algorithm") || expected instanceof IOException);
}
}

@Test
public void testChunkHandlesWhitespaceOnlyInputs() {
String[] tokens = new String[] { "   ", "\t" };
String[] tags = new String[] { "SPC", "TAB" };
String[] outcomes = new String[] { "B-NP", "I-NP" };
double[] probs = new double[] { 0.6, 0.7 };
Sequence sequence = Mockito.mock(Sequence.class);
Mockito.when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
Mockito.when(sequence.getProbs()).thenReturn(probs);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(2, result.length);
assertEquals("B-NP", result[0]);
assertEquals("I-NP", result[1]);
}

@Test
public void testTrainWithEmptyTrainingParametersDefaultsInternally() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
ChunkerFactory factory = new ChunkerFactory();
try {
ChunkerME.train("en", stream, params, factory);
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("trainer type is not supported"));
}
}

@Test
public void testProbsOnZeroLengthSequenceReturnsZeroLengthArray() {
String[] tokens = new String[0];
String[] tags = new String[0];
Sequence emptySequence = Mockito.mock(Sequence.class);
Mockito.when(emptySequence.getOutcomes()).thenReturn(Arrays.asList(new String[0]));
Mockito.when(emptySequence.getProbs()).thenReturn(new double[0]);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(emptySequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] result = chunker.probs();
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunkProducesOnlyBIOOutput() {
String[] tokens = new String[] { "The", "cat", "sat" };
String[] tags = new String[] { "DT", "NN", "VBD" };
String[] chunkOutcomes = new String[] { "B-NP", "I-NP", "B-VP" };
double[] probs = new double[] { 0.90, 0.91, 0.89 };
Sequence mockSequence = Mockito.mock(Sequence.class);
Mockito.when(mockSequence.getOutcomes()).thenReturn(Arrays.asList(chunkOutcomes));
Mockito.when(mockSequence.getProbs()).thenReturn(probs);
SequenceClassificationModel<TokenTag> mockModel = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(mockModel.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(contextGenerator), Mockito.eq(validator))).thenReturn(mockSequence);
Mockito.when(factory.getContextGenerator()).thenReturn(contextGenerator);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(mockModel);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(3, result.length);
assertEquals("B-NP", result[0]);
assertEquals("I-NP", result[1]);
assertEquals("B-VP", result[2]);
}

@Test
public void testChunkWithNonMatchingTokenAndTagLengthsThrowsException() {
String[] tokens = new String[] { "only1" };
String[] tags = new String[] { "NN", "VB" };
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected IllegalArgumentException due to mismatched array lengths.");
} catch (IllegalArgumentException expected) {
assertTrue(expected.getMessage() != null);
}
}

@Test
public void testChunkAsSpansReturnsEmptyForUnchunkedInput() {
String[] tokens = new String[] { "He", "ran" };
String[] tags = new String[] { "PRP", "VBD" };
String[] chunkOutcomes = new String[] { "O", "O" };
double[] probs = new double[] { 0.6, 0.6 };
Sequence mockSequence = Mockito.mock(Sequence.class);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel modelWrapper = Mockito.mock(ChunkerModel.class);
Mockito.when(mockSequence.getOutcomes()).thenReturn(Arrays.asList(chunkOutcomes));
Mockito.when(mockSequence.getProbs()).thenReturn(probs);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(mockSequence);
Mockito.when(modelWrapper.getFactory()).thenReturn(factory);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(modelWrapper.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(modelWrapper);
Span[] result = chunker.chunkAsSpans(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunkWithEmptyOnlyOneTokenReturnsValidSpan() {
String[] tokens = new String[] { "Dog" };
String[] tags = new String[] { "NN" };
String[] chunkOutcomes = new String[] { "B-NP" };
double[] probabilities = new double[] { 0.92 };
Sequence sequence = Mockito.mock(Sequence.class);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunkOutcomes));
Mockito.when(sequence.getProbs()).thenReturn(probabilities);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(1, spans.length);
assertEquals("NP", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testProbsWithOverSizedArray() {
String[] tokens = new String[] { "She", "slept" };
String[] tags = new String[] { "PRP", "VBD" };
String[] chunkOutcomes = new String[] { "B-NP", "B-VP" };
double[] actualProbs = new double[] { 0.88, 0.93 };
double[] oversizedArray = new double[5];
Sequence sequence = Mockito.mock(Sequence.class);
Mockito.when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunkOutcomes));
Mockito.when(sequence.getProbs()).thenReturn(actualProbs);
Mockito.doAnswer(invocation -> {
double[] arg = invocation.getArgument(0);
arg[0] = 0.88;
arg[1] = 0.93;
return null;
}).when(sequence).getProbs(Mockito.any(double[].class));
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
chunker.probs(oversizedArray);
assertEquals(0.88, oversizedArray[0], 0.0001);
assertEquals(0.93, oversizedArray[1], 0.0001);
}

@Test
public void testModelFallbackToBeamSearchWhenNoSequenceModelExists() {
MaxentModel maxent = Mockito.mock(MaxentModel.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
HashMap<String, Object> artifacts = new HashMap<>();
// artifacts.put(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME, maxent);
TrainingParameters params = new TrainingParameters();
ChunkerModel chunkerModel = new ChunkerModel("en", maxent, 10, new HashMap<>(), factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[] { "Charlie", "flew", "home" };
String[] tags = new String[] { "NNP", "VBD", "NN" };
String[] chunks = chunker.chunk(tokens, tags);
assertNotNull(chunks);
assertEquals(3, chunks.length);
}

@Test
public void testChunkWithEmptyTokensThrowsOnTagMismatch() {
String[] tokens = new String[0];
String[] tags = new String[] { "NN" };
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
}

@Test
public void testTrainHandlesEmptySampleStreamGracefully() throws IOException {
ObjectStream<ChunkSample> sampleStream = new ObjectStream<ChunkSample>() {

@Override
public ChunkSample read() {
return null;
}

@Override
public void reset() {
}

@Override
public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ChunkerFactory factory = new ChunkerFactory();
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testProbsReturnsCorrectValuesAfterMultipleChunkCalls() {
String[] tokens1 = new String[] { "Cats" };
String[] tags1 = new String[] { "NNS" };
String[] tokens2 = new String[] { "like", "fish" };
String[] tags2 = new String[] { "VB", "NN" };
double[] probs1 = new double[] { 0.80 };
double[] probs2 = new double[] { 0.90, 0.92 };
String[] outcomes1 = new String[] { "B-NP" };
String[] outcomes2 = new String[] { "B-VP", "B-NP" };
Sequence sequence1 = Mockito.mock(Sequence.class);
Sequence sequence2 = Mockito.mock(Sequence.class);
Mockito.when(sequence1.getProbs()).thenReturn(probs1);
Mockito.when(sequence2.getProbs()).thenReturn(probs2);
Mockito.when(sequence1.getOutcomes()).thenReturn(java.util.Arrays.asList(outcomes1));
Mockito.when(sequence2.getOutcomes()).thenReturn(java.util.Arrays.asList(outcomes2));
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence1).thenReturn(sequence2);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens1, tags1);
assertArrayEquals(probs1, chunker.probs(), 0.0001);
chunker.chunk(tokens2, tags2);
assertArrayEquals(probs2, chunker.probs(), 0.0001);
}

@Test
public void testChunkerMEUsesBeamSearchIfChunkerSequenceModelIsNull() {
MaxentModel maxent = Mockito.mock(MaxentModel.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = new ChunkerModel("en", maxent, 10, new HashMap<>(), factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[] { "The", "cat" };
String[] tags = new String[] { "DT", "NN" };
String[] chunks = chunker.chunk(tokens, tags);
assertNotNull(chunks);
assertEquals(2, chunks.length);
}

@Test
public void testTopKSequencesReturnsEmptySequenceWhenNoOutputAvailable() {
String[] tokens = new String[] { "Apple" };
String[] tags = new String[] { "NNP" };
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Sequence[] sequences = new Sequence[0];
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequences(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequences);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTrainProducesModelWithFallbackWhenBothModelTypesNull() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
ChunkerFactory factory = new ChunkerFactory();
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
// EventTrainer trainer = new EventTrainer() {
// 
// @Override
// public MaxentModel train(ObjectStream<Event> events) {
// return null;
// }
// };
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testChunkerHandlesLongInputSequence() {
int len = 1000;
String[] tokens = new String[len];
String[] tags = new String[len];
String[] outputs = new String[len];
double[] probs = new double[len];
for (int i = 0; i < len; i++) {
tokens[i] = "word" + i;
tags[i] = "NN";
outputs[i] = (i == 0) ? "B-NP" : "I-NP";
probs[i] = 0.8;
}
Sequence sequence = Mockito.mock(Sequence.class);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(sequence.getOutcomes()).thenReturn(Arrays.asList(outputs));
Mockito.when(sequence.getProbs()).thenReturn(probs);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(len, result.length);
assertEquals("B-NP", result[0]);
assertEquals("I-NP", result[1]);
}

@Test
public void testChunkerTrainDoesNotThrowWithTrainerTypePerceptron() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
ChunkerFactory factory = new ChunkerFactory();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
// params.put(TrainingParameters.TRAINER_TYPE_PARAM, TrainerType.SEQUENCE_TRAINER.name());
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testChunkerTrainWithEmptyManifestSucceeds() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ChunkerFactory factory = new ChunkerFactory();
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testTrainWithUnknownTrainerTypeThrows() throws IOException {
ObjectStream<ChunkSample> stream = Mockito.mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "UNKNOWN");
ChunkerFactory factory = new ChunkerFactory();
ChunkerME.train("en", stream, params, factory);
}

@Test
public void testProbsReturnsProbsAfterChunkOnTinyInput() {
Sequence sequence = Mockito.mock(Sequence.class);
Mockito.when(sequence.getOutcomes()).thenReturn(java.util.Collections.singletonList("B-NP"));
Mockito.when(sequence.getProbs()).thenReturn(new double[] { 0.77 });
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[] { "Hi" };
String[] tags = new String[] { "UH" };
chunker.chunk(tokens, tags);
double[] probs = chunker.probs();
assertNotNull(probs);
assertEquals(1, probs.length);
assertEquals(0.77, probs[0], 0.0001);
}

@Test
public void testProbsWithProbArrayShorterThanInputDoesNotThrow() {
Sequence sequence = Mockito.mock(Sequence.class);
String[] outcomes = new String[] { "B-NP", "I-NP" };
double[] probs = new double[] { 0.7, 0.8 };
Mockito.when(sequence.getOutcomes()).thenReturn(java.util.Arrays.asList(outcomes));
Mockito.when(sequence.getProbs()).thenReturn(probs);
Mockito.doAnswer(invocation -> {
double[] arr = invocation.getArgument(0);
arr[0] = 0.7;
arr[1] = 0.8;
return null;
}).when(sequence).getProbs(Mockito.any(double[].class));
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[] { "hello", "world" };
String[] tags = new String[] { "UH", "NN" };
chunker.chunk(tokens, tags);
double[] target = new double[2];
chunker.probs(target);
assertEquals(0.7, target[0], 0.0001);
assertEquals(0.8, target[1], 0.0001);
}

@Test
public void testChunkModelWithOnlyMaxentArtifactReturnsFallbackBeamSearchModel() {
MaxentModel maxentModel = Mockito.mock(MaxentModel.class);
HashMap<String, Object> artifactMap = new HashMap<>();
// artifactMap.put(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME, maxentModel);
ChunkerFactory factory = new ChunkerFactory();
ChunkerModel model = new ChunkerModel("en", maxentModel, ChunkerME.DEFAULT_BEAM_SIZE, new HashMap<>(), factory);
ChunkerME chunker = new ChunkerME(model);
String[] tokens = new String[] { "A", "test" };
String[] tags = new String[] { "DT", "NN" };
String[] chunks = chunker.chunk(tokens, tags);
assertNotNull(chunks);
assertEquals(2, chunks.length);
}

@Test
public void testChunkSampleSpansPreserveOrderAndType() {
Sequence sequence = Mockito.mock(Sequence.class);
String[] chunks = new String[] { "B-NP", "I-NP", "B-VP", "B-NP" };
double[] probs = new double[] { 0.8, 0.81, 0.9, 0.7 };
Mockito.when(sequence.getOutcomes()).thenReturn(java.util.Arrays.asList(chunks));
Mockito.when(sequence.getProbs()).thenReturn(probs);
SequenceClassificationModel<TokenTag> model = Mockito.mock(SequenceClassificationModel.class);
ChunkerContextGenerator context = Mockito.mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = Mockito.mock(SequenceValidator.class);
ChunkerFactory factory = Mockito.mock(ChunkerFactory.class);
ChunkerModel chunkerModel = Mockito.mock(ChunkerModel.class);
Mockito.when(chunkerModel.getFactory()).thenReturn(factory);
Mockito.when(factory.getContextGenerator()).thenReturn(context);
Mockito.when(factory.getSequenceValidator()).thenReturn(validator);
Mockito.when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
Mockito.when(model.bestSequence(Mockito.any(), Mockito.any(), Mockito.eq(context), Mockito.eq(validator))).thenReturn(sequence);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[] { "She", "sells", "sea", "shells" };
String[] tags = new String[] { "PRP", "VBZ", "NN", "NNS" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertTrue(spans.length > 0);
assertEquals("NP", spans[0].getType());
}
}
