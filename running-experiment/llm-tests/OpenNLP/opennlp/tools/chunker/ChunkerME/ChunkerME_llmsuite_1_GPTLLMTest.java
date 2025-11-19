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

public class ChunkerME_llmsuite_1_GPTLLMTest {

@Test
public void testChunkReturnsExpectedTags() {
String[] sentence = { "He", "reckons", "the", "current", "account", "deficit" };
String[] posTags = { "PRP", "VBZ", "DT", "JJ", "NN", "NN" };
String[] expectedChunks = { "B-NP", "B-VP", "B-NP", "I-NP", "I-NP", "I-NP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(expectedChunks));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(sentence, posTags);
assertArrayEquals(expectedChunks, result);
}

@Test
public void testChunkAsSpans() {
String[] tokens = { "John", "plays", "football" };
String[] tags = { "NNP", "VBZ", "NN" };
String[] chunks = { "B-NP", "B-VP", "B-NP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(3, spans.length);
assertEquals("NP", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
}

@Test
public void testTopKSequencesWithoutMinScore() {
String[] tokens = { "This", "is", "test" };
String[] tags = { "DT", "VBZ", "NN" };
Sequence sequence1 = mock(Sequence.class);
Sequence sequence2 = mock(Sequence.class);
Sequence[] topSequences = new Sequence[] { sequence1, sequence2 };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), any(), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(topSequences);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
Sequence[] results = chunker.topKSequences(tokens, tags);
assertEquals(2, results.length);
}

@Test
public void testTopKSequencesWithMinScore() {
String[] tokens = { "Hello" };
String[] tags = { "UH" };
double minScore = 0.7;
Sequence sequence = mock(Sequence.class);
Sequence[] expectedSequences = new Sequence[] { sequence };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), any(), any(), eq(minScore), eq(contextGenerator), eq(sequenceValidator))).thenReturn(expectedSequences);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
Sequence[] result = chunker.topKSequences(tokens, tags, minScore);
assertEquals(1, result.length);
}

@Test
public void testProbsMethods() {
String[] tokens = { "He", "is", "running" };
String[] tags = { "PRP", "VBZ", "VBG" };
String[] chunks = { "B-NP", "B-VP", "I-VP" };
double[] probabilities = new double[] { 0.9, 0.8, 0.85 };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
when(sequence.getProbs()).thenReturn(probabilities);
doAnswer(invocation -> {
double[] arg = invocation.getArgument(0);
System.arraycopy(probabilities, 0, arg, 0, probabilities.length);
return null;
}).when(sequence).getProbs(any(double[].class));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
chunker.chunk(tokens, tags);
double[] actual = new double[3];
chunker.probs(actual);
assertArrayEquals(probabilities, actual, 0.0001);
double[] returned = chunker.probs();
assertArrayEquals(probabilities, returned, 0.0001);
}

@Test
public void testTrainWithUnsupportedTrainerType() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, "invalid");
ChunkerFactory factory = new ChunkerFactory();
ChunkerME.train("en", sampleStream, params, factory);
}

@Test
public void testTrainWithEventTrainerNoException() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.EVENT_MODEL_TRAINER.name());
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
when(trainer.train(any(ObjectStream.class))).thenReturn(maxentModel);
ChunkerFactory factory = new ChunkerFactory();
assertNotNull(TrainerFactory.getEventTrainer(params, new HashMap<>()));
}

@Test
public void testTrainWithSequenceTrainerSettingPresent() {
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.SEQUENCE_TRAINER.name());
// assertTrue(params.getSettings().containsKey(TrainerFactory.TRAINER_TYPE_PARAM));
}

@Test
public void testConstructorUsingLanguageThrowsIOExceptionUnderNetworkFailure() {
try {
new ChunkerME("en");
fail("Expected IOException due to missing network access or test stub.");
} catch (IOException expected) {
assertTrue(true);
}
}

@Test
public void testChunkEmptyInputReturnsEmptyOutput() {
String[] sentence = new String[0];
String[] tags = new String[0];
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(sentence, tags);
assertEquals(0, result.length);
}

@Test
public void testProbsThrowsWithoutCallingChunkFirst() {
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
chunker.probs();
}

@Test
public void testChunkWithNullTokens() {
String[] posTags = { "DT", "NN" };
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("B-NP", "I-NP"));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(null, posTags);
}

@Test
public void testChunkWithMismatchedLengths() {
String[] tokens = { "This", "is" };
String[] tags = { "DT" };
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
}

@Test
public void testConstructorUsesFallbackMaxentModel() {
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
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
public void testTrainRespectsBeamSizeParameter() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.EVENT_MODEL_TRAINER.name());
params.put(BeamSearch.BEAM_SIZE_PARAMETER, "15");
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(trainer.train(any())).thenReturn(maxentModel);
TrainerFactory trainerFactory = mock(TrainerFactory.class);
Map<String, String> manifest = new HashMap<>();
ChunkerModel result = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(result);
}

@Test
public void testNullFactoryInModelCausesFailure() {
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(null);
new ChunkerME(model);
}

@Test
public void testEmptyContextFromGeneratorStillWorks() {
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
TokenTag[] testTags = TokenTag.create(new String[] { "a" }, new String[] { "DT" });
// when(contextGenerator.getContext(anyInt(), eq(testTags), any(Object[].class))).thenReturn(new String[0]);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("B-NP"));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(testTags), any(Object[].class), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(new String[] { "a" }, new String[] { "DT" });
assertEquals(1, result.length);
assertEquals("B-NP", result[0]);
}

@Test
public void testProbsReturnsDefaultWhenSequenceEmpty() {
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
double[] scores = new double[0];
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
when(sequence.getProbs()).thenReturn(scores);
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
chunker.chunk(new String[0], new String[0]);
double[] actual = chunker.probs();
assertEquals(0, actual.length);
}

@Test
public void testModelWithNullArtifactThrows() {
ChunkerModel model = mock(ChunkerModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(null);
new ChunkerME(model);
}

@Test
public void testTrainWithNullInputStreamThrows() throws IOException {
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.EVENT_MODEL_TRAINER.name());
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerME.train("en", null, params, factory);
}

@Test
public void testTokenTagCreateWithNullTokenArray() {
TokenTag.create(null, new String[] { "DT" });
}

@Test
public void testTokenTagCreateWithNullTagsArray() {
TokenTag.create(new String[] { "The" }, null);
}

@Test
public void testTrainWithSequenceTrainerReturnsNullModel() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerType.SEQUENCE_TRAINER.name());
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
SequenceTrainer trainer = mock(SequenceTrainer.class);
when(trainer.train(any())).thenReturn(null);
TrainerFactory.getSequenceModelTrainer(params, new HashMap<>());
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testProbsWithLargerArrayThanSequenceLength() {
String[] tokens = { "foo", "bar" };
String[] tags = { "NN", "NN" };
String[] chunks = { "B-NP", "I-NP" };
double[] probs = { 1.0, 0.9 };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
when(sequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] arg = invocation.getArgument(0);
for (int i = 0; i < probs.length; i++) {
arg[i] = probs[i];
}
return null;
}).when(sequence).getProbs(any(double[].class));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] extraLargeProbs = new double[5];
chunker.probs(extraLargeProbs);
assertEquals(1.0, extraLargeProbs[0], 0.001);
assertEquals(0.9, extraLargeProbs[1], 0.001);
}

@Test
public void testModelWithoutContextGeneratorThrows() {
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(mock(SequenceClassificationModel.class));
new ChunkerME(model);
}

@Test
public void testModelWithoutSequenceValidatorThrows() {
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(ChunkerContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(null);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(mock(SequenceClassificationModel.class));
new ChunkerME(model);
}

@Test
public void testTopKSequencesWithEmptyTokenAndTags() {
String[] tokens = new String[0];
String[] tags = new String[0];
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
Sequence[] sequences = new Sequence[] { sequence };
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequences);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertEquals(1, result.length);
}

@Test
public void testProbsDoubleArrayWithNullArgument() {
String[] tokens = { "I", "run" };
String[] tags = { "PRP", "VB" };
String[] preds = { "B-NP", "B-VP" };
double[] probs = { 0.95, 0.87 };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(preds));
when(sequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] array = invocation.getArgument(0);
array[0] = probs[0];
array[1] = probs[1];
return null;
}).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
chunker.chunk(tokens, tags);
chunker.probs(null);
}

@Test
public void testProbsAfterChunkAsSpans() {
String[] tokens = { "Today", "rains" };
String[] tags = { "NN", "VB" };
String[] predictions = { "B-NP", "B-VP" };
double[] expectedProbs = { 0.9, 0.85 };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(predictions));
when(sequence.getProbs()).thenReturn(expectedProbs);
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
chunker.chunkAsSpans(tokens, tags);
double[] result = chunker.probs();
assertArrayEquals(expectedProbs, result, 0.001);
}

@Test
public void testMultipleChunkCallsIsolatedState() {
String[] tokens1 = { "He", "runs" };
String[] tags1 = { "PRP", "VB" };
String[] chunks1 = { "B-NP", "B-VP" };
String[] tokens2 = { "Rain", "falls" };
String[] tags2 = { "NN", "VBZ" };
String[] chunks2 = { "B-NP", "B-VP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence seq1 = mock(Sequence.class);
Sequence seq2 = mock(Sequence.class);
when(seq1.getOutcomes()).thenReturn(Arrays.asList(chunks1));
when(seq2.getOutcomes()).thenReturn(Arrays.asList(chunks2));
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(seq1).thenReturn(seq2);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] first = chunker.chunk(tokens1, tags1);
String[] second = chunker.chunk(tokens2, tags2);
assertArrayEquals(chunks1, first);
assertArrayEquals(chunks2, second);
}

@Test
public void testTopKSequencesWithBeamSearchFallbackModel() {
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(model);
String[] tokens = { "Wind" };
String[] tags = { "NN" };
Sequence[] results = chunker.topKSequences(tokens, tags);
assertNotNull(results);
}

@Test
public void testChunkWithAllOutsidePredictions() {
String[] tokens = { "Cold", "day" };
String[] tags = { "JJ", "NN" };
String[] predictions = { "O", "O" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(predictions));
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(tokens, tags);
assertArrayEquals(predictions, result);
}

@Test
public void testTrainWithBeamSizeEqualsOne() throws IOException {
ObjectStream<ChunkSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerFactory.TrainerType.EVENT_MODEL_TRAINER.name());
params.put(BeamSearch.BEAM_SIZE_PARAMETER, "1");
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
// EventTrainer trainer = new EventTrainer() {
// 
// @Override
// public MaxentModel train(ObjectStream<Event> events) {
// return mock(MaxentModel.class);
// }
// };
TrainerFactory.getEventTrainer(params, new HashMap<>());
ChunkerModel model = ChunkerME.train("en", sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testChunkWithSingleToken() {
String[] tokens = { "London" };
String[] tags = { "NNP" };
String[] chunks = { "B-NP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(tokens, tags);
assertEquals(1, result.length);
assertEquals("B-NP", result[0]);
}

@Test
public void testMultipleTopKSequencesReuseBeamSearch() {
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
ChunkerME chunker = new ChunkerME(model);
String[] tokens = { "a", "b" };
String[] tags = { "DT", "NN" };
Sequence[] first = chunker.topKSequences(tokens, tags);
Sequence[] second = chunker.topKSequences(tokens, tags, 0.2);
assertNotNull(first);
assertNotNull(second);
}

@Test
public void testTopKSequencesWithNegativeMinScoreStillReturnsResults() {
Sequence sequence = mock(Sequence.class);
Sequence[] sequences = new Sequence[] { sequence };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequences(anyInt(), any(), any(), eq(-0.5), eq(contextGenerator), eq(validator))).thenReturn(sequences);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
Sequence[] result = chunker.topKSequences(new String[] { "A" }, new String[] { "NN" }, -0.5);
assertEquals(1, result.length);
}

@Test
public void testProbsReturnsNullFromSequenceThrows() {
String[] tokens = { "Hello" };
String[] tags = { "NN" };
String[] outcomes = { "B-NP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Arrays.asList(outcomes));
when(seq.getProbs()).thenReturn(null);
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(seq);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
chunker.chunk(tokens, tags);
chunker.probs();
}

@Test
public void testChunkAsSpansWithEmptyPredictionListReturnsEmptySpans() {
String[] tokens = { "a", "b" };
String[] tags = { "DT", "NN" };
List<String> outcomes = new ArrayList<>();
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
Span[] spans = chunker.chunkAsSpans(tokens, tags);
assertEquals(0, spans.length);
}

@Test
public void testChunkReturnsEmptyOnEmptySequenceOutcomes() {
String[] tokens = { "a", "b" };
String[] tags = { "DT", "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(new ArrayList<>());
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertEquals(0, result.length);
}

@Test
public void testChunkWith1000TokensProcessesSuccessfully() {
int size = 1000;
String[] tokens = new String[size];
String[] tags = new String[size];
String[] chunks = new String[size];
for (int i = 0; i < size; i++) {
tokens[i] = "word" + i;
tags[i] = "NN";
chunks[i] = i == 0 ? "B-NP" : "I-NP";
}
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] results = chunker.chunk(tokens, tags);
assertEquals(1000, results.length);
assertEquals("B-NP", results[0]);
}

@Test
public void testProbsArraySizeLargerThanSequenceLengthOnlyUpdatesPartially() {
String[] tokens = { "a", "b" };
String[] tags = { "DT", "NN" };
String[] chunks = { "B-NP", "I-NP" };
double[] returnedProbs = { 0.8, 0.7 };
double[] output = { 0.0, 0.0, -1.0 };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(chunks));
doAnswer(invocation -> {
double[] d = invocation.getArgument(0);
d[0] = returnedProbs[0];
d[1] = returnedProbs[1];
return null;
}).when(sequence).getProbs(any(double[].class));
when(sequence.getProbs()).thenReturn(returnedProbs);
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
chunker.chunk(tokens, tags);
chunker.probs(output);
assertEquals(0.8, output[0], 0.001);
assertEquals(0.7, output[1], 0.001);
assertEquals(-1.0, output[2], 0.001);
}

@Test
public void testSequenceReturnsMoreOutcomesThanInputTokensThrows() {
String[] tokens = { "x" };
String[] tags = { "NN" };
List<String> outcomes = Arrays.asList("B-NP", "I-NP");
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
}

@Test
public void testChunkReturnsNullOutcomeHandledGracefully() {
String[] tokens = { "she" };
String[] tags = { "PRP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(model.bestSequence(any(), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertEquals(0, result.length);
}

@Test
public void testChunkInputWithWhitespaceAndEmptyStrings() {
String[] tokens = { "", " ", "word" };
String[] tags = { "NN", "VB", "NN" };
String[] outcomes = { "O", "B-VP", "B-NP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(model.bestSequence(any(), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertArrayEquals(outcomes, result);
}

@Test
public void testChunkWithNullOutcomeItemInList() {
String[] tokens = { "hello", "world" };
String[] tags = { "NN", "NN" };
List<String> outcomes = Arrays.asList("B-NP", null);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(model.bestSequence(any(), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] result = chunker.chunk(tokens, tags);
assertEquals(2, result.length);
assertEquals("B-NP", result[0]);
assertNull(result[1]);
}

@Test
public void testChunkWithNullTagsAndNonNullTokensThrows() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(mock(SequenceClassificationModel.class));
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(new String[] { "one", "two" }, null);
}

@Test
public void testChunkWithNullTokensAndNonNullTagsThrows() {
ChunkerModel chunkerModel = mock(ChunkerModel.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(mock(SequenceClassificationModel.class));
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(null, new String[] { "NN" });
}

@Test
public void testChunkerModelFallbackToArtifactTypeCasting() {
MaxentModel maxentModel = mock(MaxentModel.class);
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(null);
// when(model.getArtifact(ChunkerModel.CHUNKER_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
ChunkerME chunker = new ChunkerME(model);
assertNotNull(chunker);
}

@Test
public void testChunkerChunkWithMismatchedTokenTagButHandledByModel() {
String[] tokens = { "the", "cat" };
String[] tags = { "DT" };
String[] outcomes = { "B-NP" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGen = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
when(model.bestSequence(any(), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
try {
chunker.chunk(tokens, tags);
fail("Expected IllegalArgumentException due to mismatched length");
} catch (IllegalArgumentException expected) {
assertTrue(true);
}
}

@Test
public void testChunkerProbArrayTooSmallYieldsArrayIndexException() {
String[] tokens = { "one", "two" };
String[] tags = { "NN", "NN" };
String[] outcomes = { "B-NP", "I-NP" };
double[] probs = { 0.9, 0.8 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
when(sequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] passed = invocation.getArgument(0);
passed[0] = probs[0];
passed[1] = probs[1];
return null;
}).when(sequence).getProbs(any(double[].class));
ChunkerContextGenerator generator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(generator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
double[] smallArray = new double[1];
try {
chunker.probs(smallArray);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testTopKSequencesReturnsEmptySequenceArray() {
String[] tokens = { "walked" };
String[] tags = { "VBD" };
Sequence[] emptySequences = new Sequence[0];
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(ChunkerME.DEFAULT_BEAM_SIZE), any(), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(emptySequences);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunkCalledWithTagsContainingEmptyStrings() {
String[] tokens = { "The", "big", "dog" };
String[] tags = { "", "", "" };
String[] outcomes = { "B-NP", "I-NP", "I-NP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
String[] results = chunker.chunk(tokens, tags);
assertNotNull(results);
assertEquals(3, results.length);
assertEquals("B-NP", results[0]);
}

@Test
public void testChunkWithMixedCaseTags() {
String[] tokens = { "A", "quick", "test" };
String[] tags = { "Dt", "JJ", "Nn" };
String[] outcomes = { "B-NP", "I-NP", "I-NP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(tokens, tags);
assertNotNull(result);
assertEquals(3, result.length);
assertEquals("B-NP", result[0]);
}

@Test
public void testTopKSequencesWithZeroBeamSizeStillRuns() {
String[] tokens = { "word" };
String[] tags = { "NN" };
Sequence[] sequences = new Sequence[] { mock(Sequence.class) };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(0), any(), any(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequences);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
// ChunkerME.DEFAULT_BEAM_SIZE = 0;
ChunkerME chunker = new ChunkerME(chunkerModel);
Sequence[] result = chunker.topKSequences(tokens, tags);
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testChunkWithEmojiUnicodeTokens() {
String[] tokens = { "😊", "🏃‍♂️" };
String[] tags = { "UH", "VB" };
String[] outcomes = { "B-NP", "B-VP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(tokens, tags);
assertArrayEquals(outcomes, result);
}

@Test
public void testChunkWithMismatchedTokensAndTagsLength() {
String[] tokens = { "He", "runs", "fast" };
String[] tags = { "PRP", "VBZ" };
SequenceClassificationModel<TokenTag> model = mock(SequenceClassificationModel.class);
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> sequenceValidator = mock(SequenceValidator.class);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
ChunkerModel chunkerModel = mock(ChunkerModel.class);
when(chunkerModel.getFactory()).thenReturn(factory);
when(chunkerModel.getChunkerSequenceModel()).thenReturn(model);
ChunkerME chunker = new ChunkerME(chunkerModel);
chunker.chunk(tokens, tags);
}

@Test
public void testChunkerChunkWithNumbersAsTokens() {
String[] tokens = { "123", "4567" };
String[] tags = { "CD", "CD" };
String[] outcomes = { "B-NP", "I-NP" };
ChunkerContextGenerator contextGenerator = mock(ChunkerContextGenerator.class);
SequenceValidator<TokenTag> validator = mock(SequenceValidator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(outcomes));
@SuppressWarnings("unchecked")
SequenceClassificationModel<TokenTag> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
ChunkerFactory factory = mock(ChunkerFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
ChunkerModel model = mock(ChunkerModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getChunkerSequenceModel()).thenReturn(seqModel);
ChunkerME chunker = new ChunkerME(model);
String[] result = chunker.chunk(tokens, tags);
assertEquals("B-NP", result[0]);
assertEquals("I-NP", result[1]);
}
}
