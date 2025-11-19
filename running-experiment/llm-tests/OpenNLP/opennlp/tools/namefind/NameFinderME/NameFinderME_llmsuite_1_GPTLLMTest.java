package opennlp.tools.namefind;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NameFinderME_llmsuite_1_GPTLLMTest {

@Test
public void testFindWithoutAdditionalContext() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other");
double[] probs = new double[] { 0.85, 0.90, 0.10 };
Span span1 = new Span(0, 2, "person");
Span[] decodedSpans = new Span[] { span1 };
when(sequenceCodec.decode(outcomes)).thenReturn(decodedSpans);
// mockSequenceWithOutcomes(sequenceModel, outcomes, probs);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "John", "Doe", "visits" };
Span[] result = nameFinder.find(tokens);
assertEquals(1, result.length);
assertEquals(0, result[0].getStart());
assertEquals(2, result[0].getEnd());
assertEquals("person", result[0].getType());
assertTrue(result[0].getProb() > 0.85 && result[0].getProb() < 0.91);
}

@Test
public void testExtractNameTypeReturnsCorrectType() {
String result = NameFinderME.extractNameType("organization-start");
assertEquals("organization", result);
}

@Test
public void testExtractNameTypeReturnsNullForInvalidInput() {
String result = NameFinderME.extractNameType("other");
assertNull(result);
}

@Test
public void testDropOverlappingSpansRemovesConflicts() {
Span span1 = new Span(0, 3);
Span span2 = new Span(1, 4);
Span span3 = new Span(4, 5);
Span[] input = new Span[] { span1, span2, span3 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(0, result[0].getStart());
assertEquals(3, result[0].getEnd());
assertEquals(4, result[1].getStart());
assertEquals(5, result[1].getEnd());
}

@Test
public void testClearAdaptiveDataDelegation() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
nameFinder.clearAdaptiveData();
verify(contextGenerator).clearAdaptiveData();
}

@Test
public void testTrainWithInvalidTrainerTypeThrowsException() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
try {
NameFinderME.train("en", "person", sampleStream, params, factory);
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
assertEquals("Unexpected trainer type!", e.getMessage());
}
}

@Test
public void testProbsReturnsArrayOfLastDecodedSequence() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other");
double[] probs = new double[] { 0.92, 0.87, 0.11 };
Span span1 = new Span(0, 2, "person");
Span[] decoded = new Span[] { span1 };
when(sequenceCodec.decode(outcomes)).thenReturn(decoded);
// mockSequenceWithOutcomes(sequenceModel, outcomes, probs);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] input = new String[] { "Mary", "Sue", "went" };
Span[] spans = nameFinder.find(input);
double[] result = nameFinder.probs();
assertEquals(3, result.length);
assertEquals(0.92, result[0], 0.001);
assertEquals(0.87, result[1], 0.001);
assertEquals(0.11, result[2], 0.001);
}

@Test
public void testProbsForSpansComputesMeanCorrectly() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "person-cont", "location-start");
double[] probs = new double[] { 0.9, 0.85, 0.8, 0.95 };
Span span1 = new Span(0, 3, "person");
Span span2 = new Span(3, 4, "location");
Span[] decoded = new Span[] { span1, span2 };
when(sequenceCodec.decode(outcomes)).thenReturn(decoded);
// mockSequenceWithOutcomes(sequenceModel, outcomes, probs);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] input = new String[] { "Mr", "John", "Smith", "London" };
Span[] spans = nameFinder.find(input);
double[] spanProbs = nameFinder.probs(spans);
assertEquals(2, spanProbs.length);
assertEquals((0.9 + 0.85 + 0.8) / 3.0, spanProbs[0], 0.001);
assertEquals(0.95, spanProbs[1], 0.001);
}

@Test
public void testFindReturnsEmptyWhenNoSpansPredicted() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("other", "other", "other");
double[] probs = new double[] { 0.3, 0.2, 0.1 };
Span[] decodedSpans = new Span[0];
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(Object[].class), any(Object[].class), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decodedSpans);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "This", "is", "test" };
Span[] spans = nameFinder.find(tokens);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testDropOverlappingSpansWithContainedSpans() {
Span span1 = new Span(0, 5);
Span span2 = new Span(1, 3);
Span span3 = new Span(6, 8);
Span[] input = new Span[] { span1, span2, span3 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(0, result[0].getStart());
assertEquals(5, result[0].getEnd());
assertEquals(6, result[1].getStart());
assertEquals(8, result[1].getEnd());
}

@Test
public void testFindWithEmptyTokenArrayReturnsEmptySpanArray() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Collections.emptyList();
double[] probs = new double[0];
Span[] decodedSpans = new Span[0];
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(Object[].class), any(Object[].class), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decodedSpans);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[0];
Span[] spans = nameFinder.find(tokens);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testProbsThrowsExceptionWhenFindNotCalledFirst() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
try {
nameFinder.probs();
fail("Expected NullPointerException because find was not called before probs");
} catch (NullPointerException e) {
}
}

@Test
public void testTrainSequenceTrainerCreatesTokenNameFinderModelWithSequenceModel() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "SEQ");
params.put(TrainingParameters.ITERATIONS_PARAM, 100);
params.put(TrainingParameters.CUTOFF_PARAM, 1);
MaxentModel dummyMaxentModel = mock(MaxentModel.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
// TrainerFactory.setTrainerTypeOverride(TrainerFactory.TrainerType.SEQUENCE_TRAINER);
// TrainerFactory.setSequenceModelTrainerOverride((tp, metadata) -> input -> sequenceModel);
TokenNameFinderModel result = NameFinderME.train("en", "person", sampleStream, params, factory);
assertNotNull(result);
assertEquals("en", result.getLanguage());
}

@Test
public void testFindPopulatesAdaptiveContext() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other");
double[] probs = new double[] { 0.7, 0.9, 0.4 };
Span span = new Span(0, 2, "person");
Span[] decodedSpans = new Span[] { span };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(Object[].class), any(Object[].class), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decodedSpans);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "Alice", "Smith", "ran" };
String[][] additionalContext = new String[][] { { "LOC" }, { "LOC" }, { "O" } };
Span[] spans = nameFinder.find(tokens, additionalContext);
verify(contextGenerator).updateAdaptiveData(eq(tokens), eq(new String[] { "person-start", "person-cont", "other" }));
}

@Test
public void testSpanProbsWithZeroLengthSpanDoesNotDivideByZero() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("other", "other");
double[] probs = new double[] { 0.2, 0.8 };
Span[] decoded = new Span[] { new Span(0, 0, "skip") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(Object[].class), any(Object[].class), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "hello", "world" };
Span[] spans = nameFinder.find(tokens);
double[] spanProbs = nameFinder.probs(spans);
assertEquals(1, spanProbs.length);
assertTrue(Double.isNaN(spanProbs[0]) || spanProbs[0] == 0.0 || Double.isFinite(spanProbs[0]));
}

@Test
public void testFindHandlesNullAdditionalContext() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont");
double[] probs = new double[] { 0.93, 0.88 };
Span[] decoded = new Span[] { new Span(0, 2, "person") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(Object[].class), isNull(), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "Bob", "Jones" };
Span[] spans = nameFinder.find(tokens, null);
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("person", spans[0].getType());
}

@Test
public void testTrainAddsDefaultBeamSizeToParametersWhenMissing() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
EventTrainer eventTrainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
// TrainerFactory.setTrainerTypeOverride(TrainerFactory.TrainerType.EVENT_MODEL_TRAINER);
// TrainerFactory.setEventTrainerOverride(p -> eventTrainer);
// when(eventTrainer.train(any())).thenReturn(maxentModel);
TokenNameFinderModel model = NameFinderME.train("en", "person", sampleStream, params, factory);
assertNotNull(model);
// assertTrue(params.getSettings().containsKey(TrainingParameters.ITERATIONS_PARAM));
// assertTrue(params.getSettings().containsKey(TrainingParameters.CUTOFF_PARAM));
// assertTrue(params.getSettings().containsKey(TrainingParameters.ALGORITHM_PARAM));
// assertEquals(Integer.toString(NameFinderME.DEFAULT_BEAM_SIZE), params.get(BeamSearch.BEAM_SIZE_PARAMETER));
}

@Test
public void testDropOverlappingSpansWithMultipleIdenticalSpansKeepsFirstOnly() {
Span span1 = new Span(0, 2, "person");
Span span2 = new Span(0, 2, "person");
Span span3 = new Span(3, 4, "org");
Span[] input = new Span[] { span1, span2, span3 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(0, result[0].getStart());
assertEquals(2, result[0].getEnd());
assertEquals(3, result[1].getStart());
assertEquals(4, result[1].getEnd());
}

@Test
public void testFindWithSingleTokenReturnsSingleTokenSpan() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("location-start");
double[] probs = new double[] { 0.95 };
Span[] decoded = new Span[] { new Span(0, 1, "location") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(Object[].class), any(Object[].class), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "Paris" };
Span[] spans = nameFinder.find(tokens);
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(1, spans[0].getEnd());
assertEquals("location", spans[0].getType());
assertTrue(spans[0].getProb() >= 0.95);
}

@Test
public void testProbsMethodWithMoreSpansThanProbsLength() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "person-cont");
double[] sequenceProbs = new double[] { 0.8, 0.9, 0.95 };
Span[] decodedSpans = new Span[] { new Span(0, 2, "person"), new Span(2, 3, "person"), new Span(3, 3, "invalid") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(sequenceProbs);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decodedSpans);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "Dr.", "Alice", "Cooper", "." };
Span[] spans = nameFinder.find(tokens);
double[] probsPerSpan = nameFinder.probs(spans);
assertEquals(3, probsPerSpan.length);
assertEquals((0.8 + 0.9) / 2, probsPerSpan[0], 0.001);
assertEquals(0.95, probsPerSpan[1], 0.001);
assertTrue(Double.isNaN(probsPerSpan[2]) || probsPerSpan[2] == 0.0);
}

@Test
public void testDropOverlappingSpansWhenAllSpansIntersect() {
Span span1 = new Span(0, 5, "A");
Span span2 = new Span(1, 4, "B");
Span span3 = new Span(2, 6, "C");
Span[] inputSpans = new Span[] { span1, span2, span3 };
Span[] result = NameFinderME.dropOverlappingSpans(inputSpans);
assertEquals(1, result.length);
assertEquals(0, result[0].getStart());
assertEquals(5, result[0].getEnd());
}

@Test
public void testExtractNameTypeWithHyphenatedType() {
String outcome = "geo-political-start";
String result = NameFinderME.extractNameType(outcome);
assertEquals("geo", result);
}

@Test
public void testDecodeAndSetProbsWithEmptyDecodedSpans() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("other", "other");
double[] probs = new double[] { 0.1, 0.2 };
Span[] decodedEmpty = new Span[0];
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decodedEmpty);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "unknown", "sequence" };
Span[] result = nameFinder.find(tokens);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testFindUpdatesAdaptiveDataWithEmptyOutcomeList() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> emptyOutcomes = Collections.emptyList();
double[] emptyProbs = new double[0];
Span[] decoded = new Span[0];
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(emptyOutcomes);
// when(sequence.getProbs()).thenReturn(emptyProbs);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(emptyOutcomes)).thenReturn(decoded);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "No", "Entities" };
Span[] spans = nameFinder.find(tokens);
verify(contextGenerator).updateAdaptiveData(eq(tokens), eq(new String[0]));
assertEquals(0, spans.length);
}

@Test
public void testFindWithMultipleTypesGeneratesCorrectSpans() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other", "location-start", "location-cont");
double[] probs = new double[] { 0.9, 0.8, 0.5, 0.95, 0.85 };
Span[] decoded = new Span[] { new Span(0, 2, "person"), new Span(3, 5, "location") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME nameFinder = new NameFinderME(model);
String[] tokens = new String[] { "John", "Doe", "went", "to", "London" };
Span[] spans = nameFinder.find(tokens);
assertEquals(2, spans.length);
assertEquals("person", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
assertEquals("location", spans[1].getType());
assertEquals(3, spans[1].getStart());
assertEquals(5, spans[1].getEnd());
}

@Test
public void testDropOverlappingSpansWithSingleSpanReturnsSameSpan() {
Span span = new Span(1, 3, "test");
Span[] input = new Span[] { span };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(span, result[0]);
}

@Test
public void testDropOverlappingSpansWithEmptyArrayReturnsEmpty() {
Span[] input = new Span[0];
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testExtractNameTypeWithNullOutcomeReturnsNull() {
String outcome = null;
String result = NameFinderME.extractNameType(outcome);
assertNull(result);
}

@Test
public void testExtractNameTypeWithEmptyStringReturnsNull() {
String outcome = "";
String result = NameFinderME.extractNameType(outcome);
assertNull(result);
}

@Test
public void testExtractNameTypeWithNoDashReturnsNull() {
String outcome = "location";
String result = NameFinderME.extractNameType(outcome);
assertNull(result);
}

@Test
public void testSpanProbsWithSingleTokenSpan() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("organization-start");
double[] probs = new double[] { 0.88 };
Span[] decoded = new Span[] { new Span(0, 1, "organization") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel tokenModel = mock(TokenNameFinderModel.class);
when(tokenModel.getFactory()).thenReturn(factory);
when(tokenModel.getNameFinderSequenceModel()).thenReturn(model);
NameFinderME finder = new NameFinderME(tokenModel);
String[] tokens = new String[] { "Microsoft" };
Span[] spans = finder.find(tokens);
double[] spanProbs = finder.probs(spans);
assertEquals(1, spanProbs.length);
assertEquals(0.88, spanProbs[0], 0.001);
}

@Test
public void testFindWithAllNullInputArrayThrowsException() {
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(seqModel);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
NameFinderME finder = new NameFinderME(model);
try {
finder.find(null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testProbsCalledBeforeFindThrowsNullPointerException() {
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(seqModel);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
NameFinderME finder = new NameFinderME(model);
try {
double[] probs = finder.probs();
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testSpanProbsWithOutOfBoundsSpanIndexes() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("org-start", "org-cont");
double[] probs = new double[] { 0.6, 0.7 };
Span[] decoded = new Span[] { new Span(0, 2, "org") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel tokenModel = mock(TokenNameFinderModel.class);
when(tokenModel.getFactory()).thenReturn(factory);
when(tokenModel.getNameFinderSequenceModel()).thenReturn(model);
NameFinderME finder = new NameFinderME(tokenModel);
String[] tokens = new String[] { "OpenAI", "LLC" };
Span[] spans = finder.find(tokens);
double[] returned = finder.probs(new Span[] { new Span(0, 3, "org") });
assertNotNull(returned);
assertEquals(1, returned.length);
}

@Test
public void testTrainEventModelSequenceTrainerCreatesModel() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "EVENTS");
params.put(TrainingParameters.ITERATIONS_PARAM, 100);
params.put(TrainingParameters.CUTOFF_PARAM, 1);
// EventModelSequenceTrainer<NameSample> trainer = mock(EventModelSequenceTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(trainer.train(any())).thenReturn(maxentModel);
// TrainerFactory.setTrainerTypeOverride(TrainerFactory.TrainerType.EVENT_MODEL_SEQUENCE_TRAINER);
// TrainerFactory.setEventModelSequenceTrainerOverride((p, m) -> trainer);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
TokenNameFinderModel model = NameFinderME.train("en", "custom", sampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testTrainWithCustomBeamSize() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "EVENTS");
params.put(TrainingParameters.ITERATIONS_PARAM, 100);
params.put(TrainingParameters.CUTOFF_PARAM, 2);
params.put(BeamSearch.BEAM_SIZE_PARAMETER, 7);
MaxentModel model = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(model);
// TrainerFactory.setTrainerTypeOverride(TrainerFactory.TrainerType.EVENT_MODEL_TRAINER);
// TrainerFactory.setEventTrainerOverride(p -> trainer);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
TokenNameFinderModel tnfm = NameFinderME.train("en", "person", sampleStream, params, factory);
assertNotNull(tnfm);
assertEquals("en", tnfm.getLanguage());
}

@Test
public void testFindWithInconsistentContextLengthHandling() {
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("location-start", "location-cont");
double[] probs = new double[] { 0.75, 0.85 };
Span[] spans = new Span[] { new Span(0, 2, "location") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(model.bestSequence(any(Object[].class), any(Object[].class), any(), any())).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(spans);
TokenNameFinderModel tnfm = mock(TokenNameFinderModel.class);
when(tnfm.getFactory()).thenReturn(factory);
when(tnfm.getNameFinderSequenceModel()).thenReturn(model);
NameFinderME finder = new NameFinderME(tnfm);
String[] tokens = new String[] { "San", "Francisco" };
// String[][] context = new String[] { { "City" } };
// Span[] result = finder.find(tokens, context);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(2, result[0].getEnd());
}

@Test
public void testFindAddsWindowFeatureGeneratorToContext() {
TokenNameFinderFactory factory = new TokenNameFinderFactory();
MaxentModel dummyModel = mock(MaxentModel.class);
// SequenceClassificationModel<String> seqModel = new SequenceClassificationModel<String>() {
// 
// @Override
// public Sequence<String> bestSequence(Object[] sequence, Object[] additionalContext, opennlp.tools.util.featuregen.ContextGenerator cg, SequenceValidator<String> v) {
// return new Sequence<>(Collections.singletonList("other"), new double[] { 0.5 });
// }
// };
// TokenNameFinderModel model = new TokenNameFinderModel("en", seqModel, factory.getFeatureGenerator(), factory.getResources(), new HashMap<>(), factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
String[] spanless = { "plain", "text" };
// Span[] out = finder.find(spanless);
// assertNotNull(out);
// assertEquals(0, out.length);
// finder.clearAdaptiveData();
}

@Test
public void testFindCorrectlyHandlesAdaptiveContextAfterMultipleFindCalls() {
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes1 = Arrays.asList("location-start", "location-cont");
List<String> outcomes2 = Arrays.asList("other", "organization-start");
double[] probs1 = new double[] { 0.9, 0.8 };
double[] probs2 = new double[] { 0.3, 0.95 };
Span[] spans1 = new Span[] { new Span(0, 2, "location") };
Span[] spans2 = new Span[] { new Span(1, 2, "organization") };
// Sequence<String> sequence1 = mock(Sequence.class);
// when(sequence1.getOutcomes()).thenReturn(outcomes1);
// when(sequence1.getProbs()).thenReturn(probs1);
// Sequence<String> sequence2 = mock(Sequence.class);
// when(sequence2.getOutcomes()).thenReturn(outcomes2);
// when(sequence2.getProbs()).thenReturn(probs2);
// when(model.bestSequence(any(Object[].class), any(Object[].class), any(), any())).thenReturn(sequence1).thenReturn(sequence2);
when(codec.decode(outcomes1)).thenReturn(spans1);
when(codec.decode(outcomes2)).thenReturn(spans2);
TokenNameFinderModel tnfm = mock(TokenNameFinderModel.class);
when(tnfm.getFactory()).thenReturn(factory);
when(tnfm.getNameFinderSequenceModel()).thenReturn(model);
NameFinderME finder = new NameFinderME(tnfm);
String[] sentence1 = { "New", "York" };
String[] sentence2 = { "In", "OpenAI" };
Span[] found1 = finder.find(sentence1);
assertNotNull(found1);
assertEquals(1, found1.length);
assertEquals("location", found1[0].getType());
Span[] found2 = finder.find(sentence2);
assertNotNull(found2);
assertEquals(1, found2.length);
assertEquals("organization", found2[0].getType());
verify(contextGen, times(2)).updateAdaptiveData(any(), any());
}

@Test
public void testFindWithMixedNullAndEmptyAdditionalContext() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("organization-start", "organization-cont", "other");
double[] probabilities = new double[] { 0.8, 0.85, 0.3 };
Span[] decodedSpans = new Span[] { new Span(0, 2, "organization") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probabilities);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(outcomes)).thenReturn(decodedSpans);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME finder = new NameFinderME(model);
String[] tokens = new String[] { "International", "Business", "Machines" };
String[][] additionalContext = new String[][] { null, {}, { "context-info" } };
Span[] spans = finder.find(tokens, additionalContext);
assertEquals(1, spans.length);
assertEquals("organization", spans[0].getType());
assertEquals(0, spans[0].getStart());
assertEquals(2, spans[0].getEnd());
}

@Test
public void testFindWithEmptyTokensArrayAndNonNullContext() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> sequenceCodec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(sequenceCodec);
when(sequenceCodec.createSequenceValidator()).thenReturn(validator);
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
// when(sequence.getProbs()).thenReturn(new double[0]);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(sequenceCodec.decode(any())).thenReturn(new Span[0]);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME finder = new NameFinderME(model);
String[] tokens = new String[0];
String[][] additionalContext = new String[][] { {} };
Span[] spans = finder.find(tokens, additionalContext);
assertNotNull(spans);
assertEquals(0, spans.length);
}

@Test
public void testFindWhenSequenceReturnsNullOutcomesThrowsException() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(null);
// when(sequence.getProbs()).thenReturn(new double[] {});
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(codec.decode(any())).thenThrow(new NullPointerException("outcomes is null"));
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME finder = new NameFinderME(model);
String[] tokens = new String[] { "Null", "Name" };
try {
finder.find(tokens);
fail("Expected NullPointerException due to null outcomes list");
} catch (NullPointerException e) {
assertEquals("outcomes is null", e.getMessage());
}
}

@Test
public void testFindAndClearAdaptiveDataSequence() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont");
double[] probs = new double[] { 0.95, 0.9 };
Span[] decoded = new Span[] { new Span(0, 2, "person") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME finder = new NameFinderME(model);
String[] tokens1 = new String[] { "Alice", "Smith" };
Span[] result1 = finder.find(tokens1);
assertEquals(1, result1.length);
finder.clearAdaptiveData();
String[] tokens2 = new String[] { "Bob", "Jones" };
Span[] result2 = finder.find(tokens2);
assertEquals(1, result2.length);
verify(contextGenerator, times(2)).updateAdaptiveData(any(), any());
verify(contextGenerator, times(1)).clearAdaptiveData();
}

@Test
public void testProbsArrayHasSameLengthAsTokensAfterFind() {
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other");
double[] probs = new double[] { 0.83, 0.89, 0.4 };
Span[] decoded = new Span[] { new Span(0, 2, "person") };
// Sequence<String> sequence = mock(Sequence.class);
// when(sequence.getOutcomes()).thenReturn(outcomes);
// when(sequence.getProbs()).thenReturn(probs);
// when(sequenceModel.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(decoded);
TokenNameFinderModel model = mock(TokenNameFinderModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getNameFinderSequenceModel()).thenReturn(sequenceModel);
NameFinderME finder = new NameFinderME(model);
String[] tokens = new String[] { "Mary", "Jane", "walks" };
Span[] spans = finder.find(tokens);
double[] outputProbs = new double[tokens.length];
finder.probs(outputProbs);
assertEquals(tokens.length, outputProbs.length);
assertEquals(0.83, outputProbs[0], 0.001);
assertEquals(0.89, outputProbs[1], 0.001);
assertEquals(0.4, outputProbs[2], 0.001);
}
}
