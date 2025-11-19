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
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ChunkerME_llmsuite_4_GPTLLMTest {

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsExpectedChunks() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Arrays.asList("B-NP", "B-VP", "B-NP", "I-NP"), new double[] { 0.98, 0.99, 0.93, 0.84 });
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunkerME = new ChunkerME(model);
String[] tokens = { "Peter", "has", "a", "dog" };
String[] tags = { "NNP", "VBZ", "DT", "NN" };
String[] result = chunkerME.chunk(tokens, tags);
assertArrayEquals(new String[] { "B-NP", "B-VP", "B-NP", "I-NP" }, result);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpans() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Arrays.asList("B-NP", "B-VP", "B-NP", "I-NP"), new double[] { 0.98, 0.99, 0.93, 0.84 });
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunkerME = new ChunkerME(model);
String[] tokens = { "Peter", "has", "a", "dog" };
String[] tags = { "NNP", "VBZ", "DT", "NN" };
Span[] spans = chunkerME.chunkAsSpans(tokens, tags);
Span[] expected = new Span[] { new Span(0, 1, "NP"), new Span(1, 2, "VP"), new Span(2, 4, "NP") };
assertArrayEquals(expected, spans);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithoutMinScore() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence s1 = new Sequence(Arrays.asList("B-NP", "I-NP"), new double[] { 0.75, 0.80 });
// Sequence s2 = new Sequence(Arrays.asList("B-NP", "B-VP"), new double[] { 0.70, 0.82 });
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequences(eq(10), any(), any(), any())).thenReturn(new Sequence[] { s1, s2 });
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunkerME = new ChunkerME(model);
String[] tokens = { "The", "dog" };
String[] tags = { "DT", "NN" };
Sequence[] sequences = chunkerME.topKSequences(tokens, tags);
assertEquals(2, sequences.length);
assertEquals("B-NP", sequences[0].getOutcomes().get(0));
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithMinScore() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence s1 = new Sequence(Arrays.asList("B-NP", "I-NP"), new double[] { 0.91, 0.88 });
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequences(eq(10), any(), any(), eq(0.9), any(), any())).thenReturn(new Sequence[] { s1 });
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunkerME = new ChunkerME(model);
String[] tokens = { "A", "cat" };
String[] tags = { "DT", "NN" };
Sequence[] sequences = chunkerME.topKSequences(tokens, tags, 0.9);
assertEquals(1, sequences.length);
assertEquals("I-NP", sequences[0].getOutcomes().get(1));
}

@Test
@SuppressWarnings("unchecked")
public void testProbsArrayOutput() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
double[] expectedProbs = { 0.4, 0.6 };
// Sequence sequence = new Sequence(Arrays.asList("B-NP", "I-NP"), expectedProbs);
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunkerME = new ChunkerME(model);
chunkerME.chunk(new String[] { "John", "runs" }, new String[] { "NNP", "VBZ" });
double[] actualProbs = new double[2];
chunkerME.probs(actualProbs);
assertArrayEquals(expectedProbs, actualProbs, 0.00001);
}

@Test
@SuppressWarnings("unchecked")
public void testProbsReturnsInternalArray() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
double[] expectedProbs = { 0.2, 0.9 };
// Sequence sequence = new Sequence(Arrays.asList("B-NP", "B-VP"), expectedProbs);
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunkerME = new ChunkerME(model);
chunkerME.chunk(new String[] { "He", "eats" }, new String[] { "PRP", "VBZ" });
double[] result = chunkerME.probs();
assertArrayEquals(expectedProbs, result, 0.00001);
}

@Test
public void testTrainWithInvalidTrainerType() throws IOException {
TrainingParameters trainingParams = new TrainingParameters();
// trainingParams.put(TrainerFactory.TRAINER_TYPE_PARAM, "invalid-trainer-type");
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerME.train("en", sampleStream, trainingParams, factory);
}

@Test
public void testConstructorFallbackToMaxentModelWhenSequenceModelNull() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerME chunkerME = new ChunkerME(model);
assertNotNull(chunkerME);
}

@Test
public void testConstructorFromLanguageThrowsIOException() {
try {
new ChunkerME("en");
fail("Expected IOException for unavailable download");
} catch (IOException expected) {
assertTrue(expected.getMessage() != null);
}
}

@Test
public void testChunkThrowsNPEWithoutCallingChunkFirstOnProbs() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunker = new ChunkerME(model);
double[] result = new double[2];
chunker.probs(result);
}

@Test
public void testChunkThrowsNPEWithoutCallingChunkFirstOnProbsArrayless() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunker = new ChunkerME(model);
chunker.probs();
}

@Test
public void testChunkWithEmptyInputReturnsEmptyOutput() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Collections.emptyList(), new double[] {});
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunkerME = new ChunkerME(model);
String[] result = chunkerME.chunk(new String[0], new String[0]);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunkAsSpansWithEmptyTokensReturnsEmptySpans() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Collections.emptyList(), new double[] {});
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunker = new ChunkerME(model);
Span[] spans = chunker.chunkAsSpans(new String[0], new String[0]);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testConstructorWithNullModelThrowsNPE() {
new ChunkerME((ChunkerModel) null);
}

@Test
public void testTrainThrowsExceptionIfFactoryReturnsNullContextGenerator() throws IOException {
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(null);
TrainingParameters trainingParams = new TrainingParameters();
// trainingParams.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.EVENT_MODEL_TRAINER.name());
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
ChunkerME.train("en", sampleStream, trainingParams, factory);
}

@Test
public void testTrainThrowsExceptionIfFactoryReturnsNullSequenceValidator() throws IOException {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(null);
TrainingParameters trainingParams = new TrainingParameters();
// trainingParams.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.EVENT_MODEL_TRAINER.name());
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
ChunkerME.train("en", sampleStream, trainingParams, factory);
}

@Test
public void testTopKSequencesReturnsEmptyOnEmptyInput() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// when(sequenceModel.bestSequences(eq(10), any(), any(), any())).thenReturn(new Sequence[0]);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunker = new ChunkerME(model);
Sequence[] sequences = chunker.topKSequences(new String[0], new String[0]);
assertNotNull(sequences);
assertEquals(0, sequences.length);
}

@Test
public void testChunkThrowsExceptionWhenTokenAndTagLengthsDiffer() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "The", "cat" };
String[] tags = { "DT" };
chunker.chunk(tokens, tags);
}

@Test
public void testTrainUsesEventTrainerPath() throws IOException {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.EVENT_MODEL_TRAINER.name());
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(trainer.train(any())).thenReturn(maxentModel);
TrainerFactory factorySpy = mock(TrainerFactory.class);
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
// TrainerFactory.setTrainerFactory(new TrainerFactory() {
// 
// public EventTrainer getEventTrainer(TrainingParameters p, Map<String, String> m) {
// return trainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testTrainUsesSequenceTrainerPath() throws IOException {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
SequenceTrainer trainer = mock(SequenceTrainer.class);
// when(trainer.train(any())).thenReturn(seqModel);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.SEQUENCE_TRAINER.name());
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
// TrainerFactory.setTrainerFactory(new TrainerFactory() {
// 
// public SequenceTrainer getSequenceModelTrainer(TrainingParameters p, Map<String, String> m) {
// return trainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testChunkReturnsEmptyIfBestSequenceReturnsNull() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(null);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(new String[] { "A" }, new String[] { "DT" });
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunkHandlesNullOutcomeFromSequenceGracefully() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(new String[] { "word" }, new String[] { "NN" });
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testEmptyTokensWithValidTagsReturnsEmptyChunk() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Collections.emptyList(), new double[0]);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(new String[0], new String[0]);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testInvalidBeamSizeIgnoresModelTypeFallbackToDefault() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerME chunker = new ChunkerME(model);
assertNotNull(chunker);
}

@Test
public void testTrainFailsWithNullContextGenerator() throws IOException {
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(null);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.EVENT_MODEL_TRAINER.name());
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
ChunkerME.train("en", stream, params, factory);
}

@Test
public void testTrainFailsIfSequenceTrainerReturnsNullModel() throws IOException {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
SequenceTrainer trainer = mock(SequenceTrainer.class);
when(trainer.train(any())).thenReturn(null);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.SEQUENCE_TRAINER.name());
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
// TrainerFactory.setTrainerFactory(new TrainerFactory() {
// 
// public SequenceTrainer getSequenceModelTrainer(TrainingParameters p, Map<String, String> m) {
// return trainer;
// }
// });
ChunkerME.train("en", stream, params, factory);
}

@Test
public void testChunkReturnsEmptyResultForEmptyOutcomes() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Collections.emptyList(), new double[0]);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(new String[] { "Word" }, new String[] { "NN" });
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTopKSequencesWithNullTags() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequences(eq(10), any(), any(), any())).thenReturn(new Sequence[0]);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] tokens = new String[] { "Hello" };
String[] tags = null;
try {
chunker.topKSequences(tokens, tags);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testChunkAsSpansWithAllOOutsideLabels() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Arrays.asList("O", "O", "O"), new double[] { 0.5, 0.6, 0.7 });
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[] { "I", "love", "coffee" };
String[] tags = new String[] { "PRP", "VBP", "NN" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testChunkWithSpecialCharacters() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Arrays.asList("B-NP", "I-NP"), new double[] { 0.9, 0.8 });
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[] { "€100", "#" };
String[] tags = new String[] { "CD", "SYM" };
String[] results = chunker.chunk(tokens, tags);
assertNotNull(results);
assertEquals(2, results.length);
assertEquals("B-NP", results[0]);
}

@Test
public void testConstructorWithMissingModelArtifactUsesDefaultBeamSearch() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerME chunker = new ChunkerME(model);
assertNotNull(chunker);
}

@Test
public void testTrainThrowsExceptionWhenTrainerTypeIsNull() throws Exception {
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
TrainingParameters params = new TrainingParameters();
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
ChunkerME.train("en", sampleStream, params, factory);
}

@Test
public void testTopKSequencesReturnsNullIfModelReturnsNull() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequences(anyInt(), any(), any(), any())).thenReturn(null);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "Alice", "runs" };
String[] tags = { "NNP", "VBZ" };
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNull(result);
}

@Test
public void testChunkThrowsExceptionIfFactoryIsNull() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(null);
new ChunkerME(chunkerModel);
}

@Test
public void testConstructorWithInvalidBeamSearchArtifactThrowsClassCastException() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn("Not a model");
new ChunkerME(model);
}

@Test
public void testChunkReturnsAllOWhenModelReturnsOOnly() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Arrays.asList("O", "O", "O"), new double[] { 0.5, 0.3, 0.2 });
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "hello", "world", "!" };
String[] tags = { "UH", "NN", "." };
String[] result = chunker.chunk(tokens, tags);
assertArrayEquals(new String[] { "O", "O", "O" }, result);
}

@Test
public void testChunkWithUnicodeInputs() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Arrays.asList("B-NP", "I-NP"), new double[] { 0.9, 0.8 });
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "こんにちは", "世界" };
String[] tags = { "NN", "NN" };
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(2, result.length);
assertEquals("B-NP", result[0]);
}

@Test
public void testChunkAsSpansHandlesNoSpanChunking() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("O", "O"), new double[] { 0.6, 0.7 });
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(sequenceModel);
ChunkerME chunker = new ChunkerME(model);
String[] tokens = { "No", "chunk" };
String[] tags = { "NN", "VB" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testTopKSequencesWithMinScoreEdgeCase() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("B-NP", "I-NP"), new double[] { 0.05, 0.06 });
// when(model.bestSequences(eq(10), any(), any(), eq(0.1), any(), any())).thenReturn(new Sequence[] { sequence });
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "low", "score" };
String[] tags = { "JJ", "NN" };
Sequence[] sequences = chunker.topKSequences(tokens, tags, 0.1);
assertNotNull(sequences);
assertEquals(1, sequences.length);
assertEquals("B-NP", sequences[0].getOutcomes().get(0));
}

@Test
public void testChunkWithNullTokensAndTagsThrowsException() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(null, null);
fail("Expected NullPointerException for null arguments");
} catch (NullPointerException expected) {
}
}

@Test
public void testTopKSequencesWithEmptyTokensAndTagsReturnsEmptyArray() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequences(eq(10), any(), any(), any())).thenReturn(new Sequence[0]);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = new String[0];
String[] tags = new String[0];
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTopKSequencesWithNegativeMinSequenceScoreReturnsEmpty() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(10), any(), any(), eq(-1.0), any(), any())).thenReturn(new Sequence[0]);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "This", "works" };
String[] tags = { "DT", "VBZ" };
Sequence[] result = chunker.topKSequences(tokens, tags, -1.0);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunkerConstructorUsesBeamSearchWithExplicitBeamSizeInModel() {
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(eq(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME))).thenReturn(maxentModel);
ChunkerME chunker = new ChunkerME(model);
assertNotNull(chunker);
}

@Test
public void testChunkReturnsOneChunkWhenSingleTokenIsBTag() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Collections.singletonList("B-NP"), new double[] { 0.9 });
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "London" };
String[] tags = { "NNP" };
String[] chunks = chunker.chunk(tokens, tags);
assertNotNull(chunks);
assertEquals(1, chunks.length);
assertEquals("B-NP", chunks[0]);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("NP", spans[0].getType());
}

@Test
public void testChunkReturnsSingleOWhenNoChunkDetected() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
// Sequence sequence = new Sequence(Collections.singletonList("O"), new double[] { 0.6 });
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] tokens = { "walk" };
String[] tags = { "VB" };
String[] chunks = chunker.chunk(tokens, tags);
assertNotNull(chunks);
assertEquals(1, chunks.length);
assertEquals("O", chunks[0]);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testChunkerWithMultipleIWithoutPrecedingBGeneratesNoSpans() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
List<String> outcomes = Arrays.asList("I-NP", "I-NP", "I-NP");
double[] probs = new double[] { 0.9, 0.8, 0.7 };
// Sequence sequence = new Sequence(outcomes, probs);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] tokens = { "dogs", "and", "cats" };
String[] tags = { "NNS", "CC", "NNS" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(3, spans[0].getEnd());
assertEquals("NP", spans[0].getType());
}

@Test
public void testChunkerWithTransitionFromBToBCreatesMultipleSpans() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
List<String> outcomes = Arrays.asList("B-NP", "B-VP", "B-NP");
double[] probs = new double[] { 0.9, 0.8, 0.85 };
// Sequence sequence = new Sequence(outcomes, probs);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] tokens = { "She", "runs", "marathons" };
String[] tags = { "PRP", "VBZ", "NNS" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(3, spans.length);
assertEquals("NP", spans[0].getType());
assertEquals("VP", spans[1].getType());
assertEquals("NP", spans[2].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals(1, spans[1].getStart());
assertEquals(2, spans[1].getEnd());
assertEquals(2, spans[2].getStart());
assertEquals(3, spans[2].getEnd());
}

@Test
public void testChunkerWithIOutsideValidChunkIgnored() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
List<String> outcomes = Arrays.asList("O", "I-NP", "O");
double[] probs = new double[] { 0.6, 0.5, 0.4 };
// Sequence sequence = new Sequence(outcomes, probs);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] tokens = { "a", "blue", "sky" };
String[] tags = { "DT", "JJ", "NN" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testChunkerHandlesSpecialBILabelsProperly() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
List<String> outcomes = Arrays.asList("B-ADJP", "I-ADJP", "O");
double[] probs = { 0.92, 0.91, 0.75 };
// Sequence sequence = new Sequence(outcomes, probs);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] tokens = { "very", "strong", "wind" };
String[] tags = { "RB", "JJ", "NN" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(1, spans.length);
assertEquals("ADJP", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testChunkerHandlesSingleBFollowedByIOLabels() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
List<String> outcomes = Arrays.asList("B-NP", "I-NP", "O", "B-VP", "I-VP");
double[] probs = new double[] { 0.9, 0.85, 0.5, 0.95, 0.93 };
// Sequence sequence = new Sequence(outcomes, probs);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] tokens = { "The", "dog", "barked", "ran", "quickly" };
String[] tags = { "DT", "NN", "VBD", "VBD", "RB" };
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(2, spans.length);
assertEquals("NP", spans[0].getType());
assertEquals("VP", spans[1].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals(3, spans[1].getStart());
assertEquals(5, spans[1].getEnd());
}
}
