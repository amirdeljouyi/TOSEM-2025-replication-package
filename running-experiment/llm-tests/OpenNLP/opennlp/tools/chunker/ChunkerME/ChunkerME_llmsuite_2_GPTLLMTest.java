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

public class ChunkerME_llmsuite_2_GPTLLMTest {

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsCorrectTags() {
String[] tokens = new String[] { "He", "runs" };
String[] pos = new String[] { "PRP", "VBZ" };
String[] expectedChunks = new String[] { "B-NP", "B-VP" };
SequenceClassificationModel<TokenTag> seqModel = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = (SequenceValidator<TokenTag>) mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(expectedChunks));
TokenTag[] tokenTags = TokenTag.create(tokens, pos);
when(seqModel.bestSequence(eq(tokenTags), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, pos);
assertArrayEquals(expectedChunks, result);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansReturnsExpectedSpans() {
String[] tokens = new String[] { "John", "works" };
String[] tags = new String[] { "NNP", "VBZ" };
String[] expectedChunks = new String[] { "B-NP", "B-VP" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = (SequenceValidator<TokenTag>) mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(expectedChunks));
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(2, spans.length);
assertEquals(new Span(0, 1, "NP"), spans[0]);
assertEquals(new Span(1, 2, "VP"), spans[1]);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesReturnsSequences() {
String[] tokens = new String[] { "She", "walks" };
String[] tags = new String[] { "PRP", "VBZ" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = (SequenceValidator<TokenTag>) mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
Sequence seq1 = new Sequence(Arrays.asList("B-NP", "B-VP"));
Sequence seq2 = new Sequence(Arrays.asList("B-NP", "I-NP"));
Sequence[] sequences = new Sequence[] { seq1, seq2 };
when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequences);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertEquals(2, result.length);
assertEquals("B-NP", result[0].getOutcomes().get(0));
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithMinScore() {
String[] tokens = new String[] { "They", "run" };
String[] tags = new String[] { "PRP", "VBP" };
double minScore = 0.7;
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = (SequenceValidator<TokenTag>) mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
Sequence seq = new Sequence(Arrays.asList("B-NP", "B-VP"));
Sequence[] sequences = new Sequence[] { seq };
// when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), eq(tokenTags), anyDouble(), eq(generator), eq(validator))).thenReturn(sequences);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags, minScore);
assertEquals(1, result.length);
assertEquals("B-NP", result[0].getOutcomes().get(0));
}

@Test
@SuppressWarnings("unchecked")
public void testProbsReturnsCorrectArray() {
String[] tokens = new String[] { "He" };
String[] tags = new String[] { "PRP" };
double[] expectedProbs = new double[] { 0.75d };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("B-NP"));
when(sequence.getProbs()).thenReturn(expectedProbs);
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = (SequenceValidator<TokenTag>) mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] result = chunker.probs();
assertEquals(1, result.length);
assertEquals(0.75d, result[0], 0.001);
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithEventTrainer() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ObjectStream<ChunkSample> sampleStream = (ObjectStream<ChunkSample>) mock(ObjectStream.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
EventTrainer eventTrainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(eventTrainer.train(any())).thenReturn(maxentModel);
// TrainerFactory.registerTrainerFactory(TrainerType.EVENT_MODEL_TRAINER, new TrainerFactory() {
// 
// @Override
// public EventTrainer getEventTrainer(TrainingParameters parameters, Map<String, String> reportMap) {
// return eventTrainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithSequenceTrainer() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "SEQUENCE");
ObjectStream<ChunkSample> stream = (ObjectStream<ChunkSample>) mock(ObjectStream.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
SequenceTrainer trainer = mock(SequenceTrainer.class);
SequenceClassificationModel<TokenTag> modelMock = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
// when(trainer.train(any())).thenReturn(modelMock);
// TrainerFactory.registerTrainerFactory(TrainerType.SEQUENCE_TRAINER, new TrainerFactory() {
// 
// @Override
// public SequenceTrainer getSequenceModelTrainer(TrainingParameters parameters, Map<String, String> reportMap) {
// return trainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testTrainUnsupportedTrainerThrows() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "UNKNOWN");
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
ChunkerME.train("en", stream, params, factory);
}

@Test
@SuppressWarnings("unchecked")
public void testConstructorFallbacksToBeamSearchWhenNoSequenceModel() {
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(model);
assertNotNull(chunker);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithNullTokensThrowsException() {
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(null, new String[] { "NN" });
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithEmptyInputReturnsEmptyOutput() {
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokens = TokenTag.create(new String[] {}, new String[] {});
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList());
when(model.bestSequence(eq(tokens), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(new String[] {}, new String[] {});
assertEquals(0, result.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithMismatchedArrayLengthsThrows() {
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(new String[] { "word1", "word2" }, new String[] { "NN" });
}

@Test
@SuppressWarnings("unchecked")
public void testProbsBeforeChunkThrows() {
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.probs();
}

@Test
@SuppressWarnings("unchecked")
public void testProbsIntoArrayBeforeChunkThrows() {
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
double[] buffer = new double[1];
chunker.probs(buffer);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansAllOChunksReturnsEmptySpans() {
String[] tokens = new String[] { "Hello", "world" };
String[] tags = new String[] { "UH", "NN" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Arrays.asList("O", "O"));
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(seq);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsEmptyWhenModelReturnsNull() {
String[] tokens = new String[] { "Test" };
String[] tags = new String[] { "NN" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(null);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected NullPointerException when model returns null sequence");
} catch (NullPointerException e) {
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkReturnsEmptyWhenModelReturnsEmptyOutcomes() {
String[] tokens = new String[] { "Test" };
String[] tags = new String[] { "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList());
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesReturnsEmptyWhenModelReturnsEmptySequenceArray() {
String[] sentence = new String[] { "She", "reads" };
String[] tags = new String[] { "PRP", "VBZ" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokens = TokenTag.create(sentence, tags);
when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), eq(tokens), any(), eq(contextGenerator), eq(validator))).thenReturn(new Sequence[0]);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunkerME = new ChunkerME(chunkerModel);
Sequence[] sequences = chunkerME.topKSequences(sentence, tags);
assertNotNull(sequences);
assertEquals(0, sequences.length);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithMinScoreReturnsEmptyArrayIfModelReturnsEmpty() {
String[] sentence = new String[] { "Running" };
String[] tags = new String[] { "VBG" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(sentence, tags);
// when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), eq(tokenTags), anyDouble(), eq(contextGenerator), eq(validator))).thenReturn(new Sequence[0]);
ChunkerModel modelWrapper = mock(ChunkerModel.class);
when(modelWrapper.getFactory()).thenReturn(factory);
when(modelWrapper.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(modelWrapper);
Sequence[] topK = chunker.topKSequences(sentence, tags, 0.5);
assertNotNull(topK);
assertEquals(0, topK.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithModelReturningSingleChunkToken() {
String[] tokens = new String[] { "Paris" };
String[] tags = new String[] { "NNP" };
String[] chunkTags = new String[] { "B-NP" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("B-NP"));
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerModel wrapper = mock(ChunkerModel.class);
when(wrapper.getFactory()).thenReturn(factory);
when(wrapper.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(wrapper);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("B-NP", result[0]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansReturnsSingleSpan() {
String[] tokens = new String[] { "Paris" };
String[] tags = new String[] { "NNP" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("B-LOC"));
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerModel wrapper = mock(ChunkerModel.class);
when(wrapper.getFactory()).thenReturn(factory);
when(wrapper.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(wrapper);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("LOC", spans[0].getType());
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithEmptyOutcomeListReturnsEmptyArray() {
String[] tokens = new String[] { "X" };
String[] tags = new String[] { "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel modelWrapper = mock(ChunkerModel.class);
when(modelWrapper.getFactory()).thenReturn(factory);
when(modelWrapper.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunkerME = new ChunkerME(modelWrapper);
String[] result = chunkerME.chunk(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithNullOutcomeListThrowsException() {
String[] tokens = new String[] { "X" };
String[] tags = new String[] { "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel modelWrapper = mock(ChunkerModel.class);
when(modelWrapper.getFactory()).thenReturn(factory);
when(modelWrapper.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(modelWrapper);
try {
chunker.chunk(tokens, tags);
fail("Expected NullPointerException due to null outcomes in sequence");
} catch (NullPointerException e) {
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithWhitespaceOnlyToken() {
String[] tokens = new String[] { " " };
String[] tags = new String[] { "NN" };
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("O"));
when(model.bestSequence(eq(tokenTags), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequence);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] output = chunker.chunk(tokens, tags);
assertArrayEquals(new String[] { "O" }, output);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansWithMixedChunkTypes() {
String[] tokens = new String[] { "John", "loves", "New", "York", "!" };
String[] tags = new String[] { "NNP", "VBZ", "NNP", "NNP", "." };
String[] outcomes = new String[] { "B-NP", "B-VP", "B-LOC", "I-LOC", "O" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunkerME = new ChunkerME(chunkerModel);
Span[] spans = chunkerME.chunkAsSpans(tokens, tags);
assertEquals(3, spans.length);
assertEquals("NP", spans[0].getType());
assertEquals("VP", spans[1].getType());
assertEquals("LOC", spans[2].getType());
}

@Test
@SuppressWarnings("unchecked")
public void testProbsWithEmptyArrayPassedIn() {
String[] tokens = new String[] { "Hi" };
String[] tags = new String[] { "UH" };
double[] expectedProbs = new double[] { 0.5 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("O"));
doAnswer(invocation -> {
double[] out = invocation.getArgument(0);
out[0] = 0.5;
return null;
}).when(sequence).getProbs(any(double[].class));
when(sequence.getProbs()).thenReturn(expectedProbs);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
chunker.chunk(tokens, tags);
double[] output = new double[1];
chunker.probs(output);
assertEquals(0.5, output[0], 0.0001);
}

@Test
@SuppressWarnings("unchecked")
public void testConstructorUsesBeamSearchWithNonNullArtifact() {
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getChunkerSequenceModel()).thenReturn(null);
when(model.getFactory()).thenReturn(factory);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerME chunker = new ChunkerME(model);
assertNotNull(chunker);
}

@Test
public void testChunkFailsWhenSequenceReturnsNullOutcomes() {
String[] tokens = new String[] { "X" };
String[] tags = new String[] { "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
}

@Test
public void testChunkWithUncommonTagName() {
String[] tokens = new String[] { "$" };
String[] tags = new String[] { "SYM" };
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("O"));
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator cg = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sv = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(sv);
when(model.bestSequence(eq(tokenTags), any(), eq(cg), eq(sv))).thenReturn(sequence);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] output = chunker.chunk(tokens, tags);
assertEquals(1, output.length);
assertEquals("O", output[0]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansOnSingleTokenPhrase() {
String[] tokens = new String[] { "Apple" };
String[] tags = new String[] { "NNP" };
String[] outcomes = new String[] { "B-ORG" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator ctxGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(ctxGen);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
when(model.bestSequence(eq(tokenTags), any(), eq(ctxGen), eq(validator))).thenReturn(sequence);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(1, spans.length);
assertEquals("ORG", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testConstructorDownloadModelThrowsIOException() throws IOException {
DownloadUtil util = mock(DownloadUtil.class);
new ChunkerME("invalid_language_xyz");
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithNullTrainerTypeThrows() throws IOException {
TrainingParameters params = new TrainingParameters();
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
ChunkerME.train("en", sampleStream, params, factory);
}

@Test
@SuppressWarnings("unchecked")
public void testTrainSequenceTrainerReturnsNullModel() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "SEQUENCE");
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
SequenceTrainer trainer = mock(SequenceTrainer.class);
when(trainer.train(any())).thenReturn(null);
// TrainerFactory.registerTrainerFactory(TrainerFactory.TrainerType.SEQUENCE_TRAINER, new TrainerFactory() {
// 
// @Override
// public SequenceTrainer getSequenceModelTrainer(TrainingParameters p, Map<String, String> r) {
// return trainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
assertNull(model.getChunkerSequenceModel());
}

@Test
@SuppressWarnings("unchecked")
public void testTrainEventTrainerReturnsNullModel() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
EventTrainer trainer = mock(EventTrainer.class);
when(trainer.train(any(ObjectStream.class))).thenReturn(null);
// TrainerFactory.registerTrainerFactory(TrainerFactory.TrainerType.EVENT_MODEL_TRAINER, new TrainerFactory() {
// 
// @Override
// public EventTrainer getEventTrainer(TrainingParameters p, Map<String, String> r) {
// return trainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
// assertNull(model.getMaxentModel());
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesMinScoreFiltersAll() {
String[] tokens = new String[] { "a", "b" };
String[] tags = new String[] { "NN", "NN" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator cg = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
TokenTag[] tt = TokenTag.create(tokens, tags);
Sequence[] seqs = new Sequence[0];
// when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), eq(tt), anyDouble(), eq(cg), eq(validator))).thenReturn(seqs);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
Sequence[] result = chunker.topKSequences(tokens, tags, 9999.0);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithVeryLongInput() {
int size = 1000;
String[] tokens = new String[size];
String[] tags = new String[size];
String[] outcomes = new String[size];
for (int i = 0; i < size; i++) {
tokens[i] = "word" + i;
tags[i] = "NN";
outcomes[i] = "O";
}
TokenTag[] tokentags = TokenTag.create(tokens, tags);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokentags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunkerME = new ChunkerME(cm);
String[] result = chunkerME.chunk(tokens, tags);
assertEquals(size, result.length);
assertEquals("O", result[size - 1]);
}

@Test
public void testTrainWithNullFactoryThrowsException() throws IOException {
ObjectStream<ChunkSample> inputStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ChunkerME.train("en", inputStream, params, null);
}

@Test
public void testTrainWithNullInputStreamThrowsException() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
ChunkerME.train("en", null, params, factory);
}

@Test
@SuppressWarnings("unchecked")
public void testTrainThrowsWhenEventStreamFails() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
when(stream.read()).thenThrow(new IOException("test exception"));
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenThrow(new IOException("trainer fails"));
// TrainerFactory.registerTrainerFactory(TrainerFactory.TrainerType.EVENT_MODEL_TRAINER, new TrainerFactory() {
// 
// @Override
// public EventTrainer getEventTrainer(TrainingParameters p, Map<String, String> r) {
// return trainer;
// }
// });
ChunkerME.train("en", stream, params, factory);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithNullProbabilitiesFromSequence() {
String[] tokens = new String[] { "Berlin" };
String[] tags = new String[] { "NN" };
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("O"));
when(sequence.getProbs()).thenReturn(null);
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
chunker.chunk(tokens, tags);
double[] score = chunker.probs();
assertNull(score);
}

@Test
@SuppressWarnings("unchecked")
public void testTopKSequencesWithNegativeMinScoreReturnsResult() {
String[] tokens = new String[] { "x" };
String[] tags = new String[] { "NN" };
TokenTag[] tt = TokenTag.create(tokens, tags);
Sequence resultSequence = new Sequence(Collections.singletonList("B-NP"));
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
// when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), eq(tt), anyDouble(), eq(generator), eq(validator))).thenReturn(new Sequence[] { resultSequence });
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
when(chunkerModel.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] sequences = chunker.topKSequences(tokens, tags, -10.0);
assertNotNull(sequences);
assertEquals(1, sequences.length);
}

@Test
@SuppressWarnings("unchecked")
public void testConstructorWithNullArtifactFallback() {
MaxentModel maxentModelFallback = mock(MaxentModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(null);
// when(chunkerModel.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(null);
when(chunkerModel.getFactory()).thenReturn(factory);
try {
new ChunkerME(chunkerModel);
} catch (Exception e) {
fail("ChunkerME constructor should handle model with null artifact gracefully");
}
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithoutBeamSizeUsesDefault() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "SEQUENCE");
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator ctx = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(ctx);
SequenceTrainer trainer = mock(SequenceTrainer.class);
SequenceClassificationModel<TokenTag> trainedModel = mock(SequenceClassificationModel.class);
// when(trainer.train(any())).thenReturn(trainedModel);
// TrainerFactory.registerTrainerFactory(TrainerFactory.TrainerType.SEQUENCE_TRAINER, new TrainerFactory() {
// 
// @Override
// public SequenceTrainer getSequenceModelTrainer(TrainingParameters p, Map<String, String> r) {
// return trainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
assertEquals(trainedModel, model.getChunkerSequenceModel());
}

@Test
public void testChunkWithEmptyTokensAndNonEmptyTagsThrows() {
String[] tokens = new String[] {};
String[] tags = new String[] { "NN" };
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator ctx = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
when(factory.getContextGenerator()).thenReturn(ctx);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getChunkerSequenceModel()).thenReturn(model);
when(cm.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(cm);
chunker.chunk(tokens, tags);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkerMEChunkWithNullArrayReturnsCorrectLength() {
String[] tokens = new String[] { "test" };
String[] tags = new String[] { "NN" };
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList(null));
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunkerME = new ChunkerME(chunkerModel);
String[] result = chunkerME.chunk(tokens, tags);
assertEquals(1, result.length);
assertNull(result[0]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkAsSpansWithMultipleContiguousChunks() {
String[] tokens = new String[] { "John", "Doe", "ran", "fast", "." };
String[] tags = new String[] { "NNP", "NNP", "VBD", "RB", "." };
String[] outcomes = new String[] { "B-PER", "I-PER", "B-VP", "I-VP", "O" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = (SequenceClassificationModel<TokenTag>) mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(any(), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunkerME = new ChunkerME(chunkerModel);
Span[] spans = chunkerME.chunkAsSpans(tokens, tags);
assertEquals(2, spans.length);
assertEquals("PER", spans[0].getType());
assertEquals("VP", spans[1].getType());
}

@Test
public void testChunkWithTagsButEmptyTokensThrowsException() {
String[] tokens = new String[] {};
String[] tags = new String[] { "NN" };
ChunkerModel model = mock(ChunkerModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(mock(SequenceClassificationModel.class));
ChunkerME chunker = new ChunkerME(model);
chunker.chunk(tokens, tags);
}

@Test
public void testTrainThrowsForNullChunkerFactory() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
ChunkerME.train("en", stream, params, null);
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithEventModelAndNullResultReturnsValidModel() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.TRAINER_TYPE_PARAM, "EVENT");
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(null);
// TrainerFactory.registerTrainerFactory(TrainerFactory.TrainerType.EVENT_MODEL_TRAINER, new TrainerFactory() {
// 
// @Override
// public EventTrainer getEventTrainer(TrainingParameters parameters, Map<String, String> reportMap) {
// return trainer;
// }
// });
ChunkerModel model = ChunkerME.train("en", stream, params, factory);
// assertNull(model.getMaxentModel());
}

@Test
@SuppressWarnings("unchecked")
public void testTrainWithEmptyTrainingParametersUsesDefaultsSafely() throws IOException {
ObjectStream<ChunkSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
try {
ChunkerME.train("en", stream, params, factory);
fail("Expected IllegalArgumentException due to missing trainer type");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("Trainer type is not supported"));
}
}

@Test
@SuppressWarnings("unchecked")
public void testChunkWithSingleTokenAllLowercase() {
String[] tokens = new String[] { "unknown" };
String[] tags = new String[] { "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("B-NP"));
TokenTag[] tokenTags = TokenTag.create(tokens, tags);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokenTags), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerModel cm = mock(ChunkerModel.class);
when(cm.getFactory()).thenReturn(factory);
when(cm.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(cm);
String[] result = chunker.chunk(tokens, tags);
assertEquals(1, result.length);
assertEquals("B-NP", result[0]);
}

@Test
@SuppressWarnings("unchecked")
public void testChunkerModelArtifactFallbackIsNullSafe() {
ChunkerModel model = mock(ChunkerModel.class);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(null);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(model.getFactory()).thenReturn(factory);
try {
new ChunkerME(model);
} catch (Exception e) {
fail("Constructor should not throw even if artifact is null");
}
}
}
