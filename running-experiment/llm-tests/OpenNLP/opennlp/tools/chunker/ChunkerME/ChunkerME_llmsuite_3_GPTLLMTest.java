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

public class ChunkerME_llmsuite_3_GPTLLMTest {

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsExpectedOutcomes() {
String[] tokens = { "John", "lives", "in", "Denver" };
String[] tags = { "NNP", "VBZ", "IN", "NNP" };
String[] chunkTags = { "B-NP", "B-VP", "B-PP", "B-NP" };
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Arrays.asList(chunkTags));
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertArrayEquals(chunkTags, result);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansProducesSpansCorrectly() {
String[] tokens = { "Alice", "loves", "NLP" };
String[] tags = { "NNP", "VBZ", "NNP" };
String[] chunkTags = { "B-NP", "B-VP", "B-NP" };
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Arrays.asList(chunkTags));
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertTrue(spans.length > 0);
assertEquals("NP", spans[0].getType());
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesReturnsMultipleSequences() {
String[] tokens = { "He", "is", "smart" };
String[] tags = { "PRP", "VBZ", "JJ" };
// Sequence sequence1 = new Sequence(Arrays.asList("B-NP", "B-VP", "B-ADJP"), 0.9);
// Sequence sequence2 = new Sequence(Arrays.asList("O", "O", "O"), 0.5);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
// when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), any(), any(), any(), any())).thenReturn(new Sequence[] { sequence1, sequence2 });
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] sequences = chunker.topKSequences(tokens, tags);
assertEquals(2, sequences.length);
assertEquals(0.9, sequences[0].getScore(), 0.01);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithMinScoreFiltersLowScores() {
String[] tokens = { "AI", "is", "cool" };
String[] tags = { "NN", "VBZ", "JJ" };
// Sequence highScore = new Sequence(Arrays.asList("B-NP", "B-VP", "B-ADJP"), 0.95);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
// when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), any(), any(), eq(0.2), any(), any())).thenReturn(new Sequence[] { highScore });
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] sequences = chunker.topKSequences(tokens, tags, 0.2);
assertEquals(1, sequences.length);
assertTrue(sequences[0].getScore() >= 0.2);
}

@Test
@SuppressWarnings("unchecked")
public void testProbsMethodReturnsExpectedValues() {
String[] tokens = { "Deep", "learning", "rocks" };
String[] tags = { "JJ", "NN", "VBZ" };
String[] chunkTags = { "B-NP", "I-NP", "B-VP" };
double[] probs = { 0.9, 0.8, 0.7 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunkTags));
when(sequence.getProbs()).thenReturn(probs);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] result = chunker.probs();
assertArrayEquals(probs, result, 1e-6);
}

@Test
@SuppressWarnings("unchecked")
public void testProbsMethodWithProvidedArray() {
String[] tokens = { "Word1", "Word2" };
String[] tags = { "NN", "VB" };
String[] chunkTags = { "B-NP", "B-VP" };
double[] expectedProbs = { 0.6, 0.4 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunkTags));
doAnswer(invocation -> {
double[] arg = invocation.getArgument(0);
arg[0] = 0.6;
arg[1] = 0.4;
return null;
}).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] result = new double[2];
chunker.probs(result);
assertEquals(0.6, result[0], 0.01);
assertEquals(0.4, result[1], 0.01);
}

@Test
public void testTrainEventModelReturnsNonNullModel() throws IOException {
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
// when(trainer.train(any())).thenReturn(maxentModel);
TrainerFactory trainerFactoryMock = mock(TrainerFactory.class);
ChunkerModel result = ChunkerME.train("en", stream, params, factory);
assertNotNull(result);
assertEquals("en", result.getLanguage());
}

@Test
public void testTrainWithUnsupportedTrainerType() throws IOException {
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVE_BAYES");
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerME.train("en", stream, params, factory);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithEmptyInputReturnsEmptyArray() {
String[] tokens = {};
String[] tags = {};
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithMismatchedTokenAndTagLengthsThrowsException() {
String[] tokens = { "John", "lives" };
String[] tags = { "NNP" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected ArrayIndexOutOfBoundsException due to mismatched token/tag length");
} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testProbsThrowsExceptionWhenCalledBeforeChunk() {
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.probs();
fail("Expected NullPointerException or IllegalStateException due to no previous chunk call");
} catch (NullPointerException | IllegalStateException expected) {
assertTrue(true);
}
}

@Test
public void testConstructorWithNullSequenceModelFallsBackToBeamSearch() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(null);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(chunkerModel.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
assertNotNull(chunker);
}

@Test
public void testTrainReturnsSequenceModelWhenEventModelIsNull() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
SequenceClassificationModel<TokenTag> sequenceModel = mock(SequenceClassificationModel.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
TrainerFactory mockTrainerFactory = mock(TrainerFactory.class);
SequenceTrainer seqTrainer = mock(SequenceTrainer.class);
// when(seqTrainer.train(any())).thenReturn(sequenceModel);
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testTopKSequencesReturnsEmptyArrayWhenModelReturnsNull() {
String[] tokens = { "a", "b", "c" };
String[] tags = { "DT", "NN", "NN" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), any(), any(), any(), any())).thenReturn(null);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNull(result);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithNullTokensThrowsException() {
String[] tokens = null;
String[] tags = { "NNP" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected NullPointerException due to null tokens");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithNullTagsThrowsException() {
String[] tokens = { "John" };
String[] tags = null;
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected NullPointerException due to null tags");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkerMEConstructorWithNullArtifactDoesNotThrow() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(null);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(chunkerModel.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
assertNotNull(chunker);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkerTrainWithNullMaxentModelReturnsSeqModel() throws IOException {
ObjectStream<ChunkSample> chunkSampleStream = mock(ObjectStream.class);
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
SequenceTrainer sequenceTrainer = mock(SequenceTrainer.class);
// when(sequenceTrainer.train(any())).thenReturn(seqModel);
ChunkerModel model = ChunkerME.train("en", chunkSampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testChunkAsSpansWithSinglePhrase() {
String[] tokens = { "New", "York" };
String[] tags = { "NNP", "NNP" };
String[] outcomes = { "B-NP", "I-NP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("NP", spans[0].getType());
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithNegativeScoreReturnsEmpty() {
String[] tokens = { "He", "goes" };
String[] tags = { "PRP", "VBZ" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), any(), any(), eq(-1.0), any(), any())).thenReturn(new Sequence[0]);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags, -1.0);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithSpecialCharacters() {
String[] tokens = { "@", "#", "!" };
String[] tags = { "SYM", "SYM", "SYM" };
String[] outcomes = { "B-O", "I-O", "B-O" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertEquals(3, result.length);
assertEquals("B-O", result[0]);
assertEquals("I-O", result[1]);
assertEquals("B-O", result[2]);
}

@Test
public void testTrainWithNullContextGeneratorThrowsException() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(null);
try {
ChunkerME.train("en", sampleStream, params, factory);
fail("Expected NullPointerException due to null context generator");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansWithMultiplePhrases() {
String[] tokens = { "The", "big", "dog", "ran" };
String[] tags = { "DT", "JJ", "NN", "VBD" };
String[] outcomes = { "B-NP", "I-NP", "I-NP", "B-VP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(2, spans.length);
assertEquals("NP", spans[0].getType());
assertEquals("VP", spans[1].getType());
}

@Test
public void testTrainWithNullTrainingParametersThrowsException() {
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
try {
// ChunkerME.train("en", stream, null, factory);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesNullModelResultHandledGracefully() {
String[] tokens = { "This", "test" };
String[] tags = { "DT", "NN" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), any(), any(), any(), any())).thenReturn(null);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNull(result);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsSingleElementSequence() {
String[] tokens = { "Word" };
String[] tags = { "NN" };
List<String> outcomes = Collections.singletonList("B-NP");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("B-NP", result[0]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsEmptySequenceIfModelReturnsNullOutcomes() {
String[] tokens = { "Apple", "Inc." };
String[] tags = { "NNP", "NNP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
} catch (Exception e) {
fail("Expected graceful handling of null outcomes");
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansReturnsEmptyWhenInputIsEmpty() {
String[] tokens = {};
String[] tags = {};
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testTrainWithInvalidTrainerTypeThrowsIllegalArgumentException() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
TrainingParameters trainingParameters = new TrainingParameters();
trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, "FAKE_ALGO");
ChunkerFactory factory = mock(ChunkerFactory.class);
try {
ChunkerME.train("en", sampleStream, trainingParameters, factory);
fail("Expected IllegalArgumentException for unsupported trainer type");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("Trainer type is not supported"));
}
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithEventModelTrainerReturnsNonNullModel() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
EventTrainer eventTrainer = mock(EventTrainer.class);
Event event = mock(Event.class);
List<Event> eventList = new ArrayList<>();
eventList.add(event);
MaxentModel trainedModel = mock(MaxentModel.class);
when(eventTrainer.train(any(ObjectStream.class))).thenReturn(trainedModel);
TrainingParameters parameters = new TrainingParameters();
parameters.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
parameters.put(BeamSearch.BEAM_SIZE_PARAMETER, "5");
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
ChunkerModel model = ChunkerME.train("en", sampleStream, parameters, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithUnicodeCharacters() {
String[] tokens = { "こんにちは", "世界" };
String[] tags = { "NN", "NN" };
String[] predictions = { "B-NP", "I-NP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(predictions));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertEquals("B-NP", result[0]);
assertEquals("I-NP", result[1]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithDuplicateTokens() {
String[] tokens = { "the", "the", "the" };
String[] tags = { "DT", "DT", "DT" };
String[] outcomes = { "B-NP", "I-NP", "I-NP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertEquals(3, result.length);
assertEquals("B-NP", result[0]);
assertEquals("I-NP", result[1]);
assertEquals("I-NP", result[2]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkNullSequenceReturnedByModelHandledSafely() {
String[] tokens = { "John" };
String[] tags = { "NNP" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(null);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected NullPointerException when bestSequence returned null");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testTrainEventModelWithEmptyEventStreamReturnsException() throws IOException {
ObjectStream<ChunkSample> input = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
MaxentModel maxentModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
when(trainer.train(any(ObjectStream.class))).thenReturn(maxentModel);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
ChunkerModel model = ChunkerME.train("en", input, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansBoundaryTokensIgnoredCorrectly() {
String[] tokens = { "the", "cat", "sat" };
String[] tags = { "DT", "NN", "VBD" };
String[] outcomes = { "O", "B-NP", "B-VP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator ctxGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(ctxGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] result = chunker.chunkAsSpans(tokens, tags);
assertEquals(2, result.length);
assertEquals("NP", result[0].getType());
assertEquals(1, result[0].getStart());
assertEquals(2, result[0].getEnd());
assertEquals("VP", result[1].getType());
assertEquals(2, result[1].getStart());
assertEquals(3, result[1].getEnd());
}

@Test
public void testChunkerMEWithEmptyModelArtifactFallsBackToDefaultSearch() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(null);
MaxentModel emptyBackendModel = mock(MaxentModel.class);
// when(chunkerModel.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(emptyBackendModel);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
assertNotNull(chunker);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithZeroBeamSizeStillReturnsList() {
String[] tokens = { "He", "runs" };
String[] tags = { "PRP", "VBZ" };
// Sequence resultSequence = new Sequence(Arrays.asList("B-NP", "B-VP"), 0.9);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequences(eq(0), any(), any(), any(), any())).thenReturn(new Sequence[] { resultSequence });
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] sequences = chunker.topKSequences(tokens, tags);
assertNotNull(sequences);
assertEquals(1, sequences.length);
assertEquals(0.9, sequences[0].getScore(), 1e-6);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithProbsIncludesMinScoreFiltering() {
String[] tokens = { "He", "codes" };
String[] tags = { "PRP", "VBZ" };
// Sequence seqHigh = new Sequence(Arrays.asList("B-NP", "B-VP"), 0.95);
// Sequence seqLow = new Sequence(Arrays.asList("O", "O"), 0.01);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
// when(model.bestSequences(eq(10), any(), any(), eq(0.5), any(), any())).thenReturn(new Sequence[] { seqHigh });
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags, 0.5);
assertEquals(1, result.length);
assertEquals(0.95, result[0].getScore(), 0.001);
}

@Test
public void testChunkerMEConstructorWithLanguageModelThrowsIOExceptionIfMissingDownload() {
try {
new ChunkerME("xx-unknown");
fail("Expected IOException for unknown model language download");
} catch (IOException expected) {
assertTrue(expected.getMessage().contains("not found"));
}
}

@Test
public void testProbsDoubleArraySmallerThanTokensShouldThrowIndexException() {
String[] tokens = { "the", "cat" };
String[] tags = { "DT", "NN" };
String[] outcomes = { "B-NP", "I-NP" };
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Arrays.asList(outcomes));
doAnswer(invocation -> {
double[] d = invocation.getArgument(0);
d[0] = 0.8;
d[1] = 0.9;
return null;
}).when(seq).getProbs(any(double[].class));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(seq);
ChunkerContextGenerator ctxGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(ctxGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
try {
double[] tooShort = new double[1];
chunker.probs(tooShort);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsEmptyOutcomesDoesNotThrow() {
String[] tokens = { "a", "b" };
String[] tags = { "DT", "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunkerME = new ChunkerME(chunkerModel);
String[] result = chunkerME.chunk(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTrainWithNullStreamThrowsIOException() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
ChunkerFactory factory = mock(ChunkerFactory.class);
try {
ChunkerME.train("en", null, params, factory);
fail("Expected NullPointerException due to null stream");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testTrainWithNullFactoryThrowsException() throws IOException {
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
try {
ChunkerME.train("en", stream, params, null);
fail("Expected NullPointerException due to null factory");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansAllOtagsReturnsEmpty() {
String[] tokens = { "this", "is", "fine" };
String[] tags = { "DT", "VBZ", "JJ" };
String[] outcomes = { "O", "O", "O" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesReturnsNullAndHandledGracefully() {
String[] tokens = { "hello" };
String[] tags = { "UH" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), any(), any(), any(), any())).thenReturn(null);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] sequences = chunker.topKSequences(tokens, tags);
assertNull(sequences);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkerWithNullChunkerSequenceModelFallsBackCorrectly() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(null);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(chunkerModel.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunkerME = new ChunkerME(chunkerModel);
assertNotNull(chunkerME);
}

@Test
public void testChunkWithSingleTokenSentenceProducesCorrectResult() {
String[] tokens = { "OpenNLP" };
String[] tags = { "NNP" };
String[] outcomes = { "B-NP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel modelMock = mock(ChunkerModel.class);
when(modelMock.getChunkerSequenceModel()).thenReturn(model);
when(modelMock.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(modelMock);
String[] result = chunker.chunk(tokens, tags);
assertEquals(1, result.length);
assertEquals("B-NP", result[0]);
}

@Test
public void testProbsAccessBeforeChunkThrowsException() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.probs();
fail("Expected NullPointerException when calling probs() before chunk()");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testChunkerTrainWithMissingBeamSizeDefaultsTo10() throws IOException {
ObjectStream<ChunkSample> data = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
MaxentModel maxentModel = mock(MaxentModel.class);
Event event = mock(Event.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
ChunkerModel model = ChunkerME.train("en", data, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testChunkWithTokenAndTagLengthZeroReturnsEmpty() {
String[] tokens = new String[0];
String[] tags = new String[0];
List<String> output = Collections.emptyList();
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(output);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel modelMock = mock(ChunkerModel.class);
when(modelMock.getChunkerSequenceModel()).thenReturn(model);
when(modelMock.getFactory()).thenReturn(factory);
ChunkerME chunkerME = new ChunkerME(modelMock);
String[] result = chunkerME.chunk(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkProbsArrayLargerThanTokensDoesNotThrow() {
String[] tokens = { "One", "word" };
String[] tags = { "CD", "NN" };
String[] outcomes = { "B-NP", "I-NP" };
double[] expectedProbs = { 0.8, 0.9 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
doAnswer(invocation -> {
double[] arr = invocation.getArgument(0);
arr[0] = 0.8;
arr[1] = 0.9;
return null;
}).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] probs = new double[5];
chunker.probs(probs);
assertEquals(0.8, probs[0], 0.001);
assertEquals(0.9, probs[1], 0.001);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesMinScoreHigherThanAllSequencesReturnsEmpty() {
String[] tokens = { "They", "code" };
String[] tags = { "PRP", "VB" };
// Sequence highScore = new Sequence(Collections.singletonList("B-NP"), 0.2);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), any(), any(), eq(0.9), any(), any())).thenReturn(new Sequence[0]);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunkerME = new ChunkerME(chunkerModel);
Sequence[] sequences = chunkerME.topKSequences(tokens, tags, 0.9);
assertNotNull(sequences);
assertEquals(0, sequences.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkDifferentOutcomeLengthThanTokensThrowsException() {
String[] tokens = { "Token1", "Token2" };
String[] tags = { "NN", "NN" };
List<String> mismatchedOutcomes = Collections.singletonList("B-NP");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(mismatchedOutcomes);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected exception due to mismatched outcomes length");
} catch (Exception expected) {
assertTrue(true);
}
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithBeamSizeParameterParsesCorrectly() throws IOException {
ObjectStream<ChunkSample> chunkSamples = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(BeamSearch.BEAM_SIZE_PARAMETER, "7");
MaxentModel backendModel = mock(MaxentModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
ChunkerModel model = ChunkerME.train("en", chunkSamples, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithSpecialSymbolTagsProducesValidChunks() {
String[] tokens = { "@", "#", "$" };
String[] tags = { "SYM", "SYM", "SYM" };
String[] chunkOutcomes = { "B-NP", "I-NP", "I-NP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunkOutcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertEquals(3, result.length);
assertEquals("B-NP", result[0]);
assertEquals("I-NP", result[1]);
assertEquals("I-NP", result[2]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansHandlesBeginAndEndCorrectly() {
String[] tokens = { "Mr.", "Smith", "works" };
String[] tags = { "NNP", "NNP", "VBZ" };
String[] outcomes = { "B-NP", "I-NP", "B-VP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(2, spans.length);
assertEquals("NP", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("VP", spans[1].getType());
assertEquals(2, spans[1].getStart());
assertEquals(3, spans[1].getEnd());
}

@Test
public void testChunkerTrainWithMissingAlgorithmParamThrowsException() throws IOException {
ObjectStream<ChunkSample> samples = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
ChunkerFactory factory = mock(ChunkerFactory.class);
try {
ChunkerME.train("en", samples, params, factory);
fail("Expected IllegalArgumentException for missing algorithm parameter");
} catch (IllegalArgumentException expected) {
assertTrue(expected.getMessage().contains("not supported"));
}
}

@Test
public void testChunkerMEWithNullArtifactThrowsException() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(null);
// when(chunkerModel.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(null);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(chunkerModel.getFactory()).thenReturn(factory);
try {
new ChunkerME(chunkerModel);
fail("Expected NullPointerException due to null artifact");
} catch (NullPointerException expected) {
assertTrue(true);
}
}
}
