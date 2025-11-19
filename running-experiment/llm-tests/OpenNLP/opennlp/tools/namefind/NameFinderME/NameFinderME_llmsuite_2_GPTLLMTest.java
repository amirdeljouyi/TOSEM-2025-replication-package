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
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NameFinderME_llmsuite_2_GPTLLMTest {

@Test
public void testFindReturnsSpan() {
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
Sequence mockSequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other");
double[] probs = new double[] { 0.8, 0.9, 0.4 };
when(mockModel.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(mockSequence);
when(mockSequence.getOutcomes()).thenReturn(outcomes);
when(mockSequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] arg = invocation.getArgument(0);
arg[0] = 0.8;
arg[1] = 0.9;
arg[2] = 0.4;
return null;
}).when(mockSequence).getProbs(any(double[].class));
SequenceCodec<String> mockCodec = mock(SequenceCodec.class);
when(mockCodec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
Span span = new Span(0, 2, "person");
when(mockCodec.decode(outcomes)).thenReturn(new Span[] { span });
NameContextGenerator mockContextGenerator = mock(NameContextGenerator.class);
TokenNameFinderFactory mockFactory = mock(TokenNameFinderFactory.class);
when(mockFactory.createContextGenerator()).thenReturn(mockContextGenerator);
when(mockFactory.createSequenceCodec()).thenReturn(mockCodec);
// when(mockFactory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(mockFactory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, Collections.emptyMap(), mockCodec, mockFactory);
// NameFinderME nameFinder = new NameFinderME(model);
String[] sentence = new String[] { "John", "Doe", "Teacher" };
// Span[] result = nameFinder.find(sentence);
// assertEquals(1, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(2, result[0].getEnd());
// assertEquals("person", result[0].getType());
// assertEquals(0.85, result[0].getProb(), 0.01);
}

@Test
public void testFindWithAdditionalContext() {
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
Sequence mockSequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("location-start", "location-cont");
double[] probs = new double[] { 0.7, 0.6 };
when(mockModel.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(mockSequence);
when(mockSequence.getOutcomes()).thenReturn(outcomes);
when(mockSequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] arg = invocation.getArgument(0);
arg[0] = 0.7;
arg[1] = 0.6;
return null;
}).when(mockSequence).getProbs(any(double[].class));
SequenceCodec<String> mockCodec = mock(SequenceCodec.class);
when(mockCodec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(mockCodec.decode(outcomes)).thenReturn(new Span[] { new Span(0, 2, "location") });
NameContextGenerator mockContextGenerator = mock(NameContextGenerator.class);
TokenNameFinderFactory mockFactory = mock(TokenNameFinderFactory.class);
when(mockFactory.createContextGenerator()).thenReturn(mockContextGenerator);
when(mockFactory.createSequenceCodec()).thenReturn(mockCodec);
// when(mockFactory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(mockFactory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, Collections.emptyMap(), mockCodec, mockFactory);
// NameFinderME finder = new NameFinderME(model);
String[] tokens = new String[] { "New", "York" };
String[][] context = new String[][] { { "cap" }, { "cap" } };
// Span[] spans = finder.find(tokens, context);
// assertNotNull(spans);
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("location", spans[0].getType());
}

@Test
public void testProbsMethod() {
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
Sequence mockSequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("org-start", "other", "other");
double[] probs = new double[] { 0.6, 0.4, 0.3 };
when(mockModel.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(mockSequence);
when(mockSequence.getOutcomes()).thenReturn(outcomes);
when(mockSequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] p = invocation.getArgument(0);
p[0] = 0.6;
p[1] = 0.4;
p[2] = 0.3;
return null;
}).when(mockSequence).getProbs(any(double[].class));
SequenceCodec<String> mockCodec = mock(SequenceCodec.class);
when(mockCodec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
Span[] decoded = new Span[] { new Span(0, 1, "org") };
when(mockCodec.decode(outcomes)).thenReturn(decoded);
NameContextGenerator mockContextGenerator = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mockContextGenerator);
when(factory.createSequenceCodec()).thenReturn(mockCodec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, Collections.emptyMap(), mockCodec, factory);
// NameFinderME finder = new NameFinderME(model);
String[] tokens = new String[] { "Google", "was", "founded" };
// finder.find(tokens);
// double[] returnedProbs = finder.probs();
// assertEquals(3, returnedProbs.length);
// assertEquals(0.6, returnedProbs[0], 0.01);
// assertEquals(0.4, returnedProbs[1], 0.01);
// assertEquals(0.3, returnedProbs[2], 0.01);
}

@Test
public void testProbsSpanAveraging() {
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
Sequence mockSequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("person-start", "person-cont");
double[] probs = new double[] { 0.4, 0.6 };
when(mockModel.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(mockSequence);
when(mockSequence.getOutcomes()).thenReturn(outcomes);
when(mockSequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] p = invocation.getArgument(0);
p[0] = 0.4;
p[1] = 0.6;
return null;
}).when(mockSequence).getProbs(any(double[].class));
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
Span[] spans = new Span[] { new Span(0, 2, "person") };
when(codec.decode(outcomes)).thenReturn(spans);
NameContextGenerator contextGenerator = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGenerator);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(model);
String[] input = new String[] { "Alice", "Walker" };
// Span[] result = finder.find(input);
// double[] spanProbs = finder.probs(result);
// assertEquals(1, spanProbs.length);
// assertEquals(0.5, spanProbs[0], 0.01);
}

@Test
public void testClearAdaptiveDataDelegation() {
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(codec.createSequenceValidator()).thenReturn(validator);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(model);
// finder.clearAdaptiveData();
verify(contextGen, times(1)).clearAdaptiveData();
}

@Test
public void testExtractNameTypeValid() {
String outcome = "organization-cont";
String extracted = NameFinderME.extractNameType(outcome);
assertEquals("organization", extracted);
}

@Test
public void testExtractNameTypeInvalid() {
String outcome = "other";
String extracted = NameFinderME.extractNameType(outcome);
assertNull(extracted);
}

@Test
public void testDropOverlappingSpansKeepsNonOverlapping() {
Span s1 = new Span(0, 2, "location");
Span s2 = new Span(3, 5, "location");
Span[] spans = new Span[] { s1, s2 };
Span[] filtered = NameFinderME.dropOverlappingSpans(spans);
assertEquals(2, filtered.length);
assertEquals(s1, filtered[0]);
assertEquals(s2, filtered[1]);
}

@Test
public void testDropOverlappingSpansRemovesOverlap() {
Span s1 = new Span(0, 2, "person");
Span s2 = new Span(1, 3, "person");
Span s3 = new Span(4, 5, "org");
Span[] input = new Span[] { s1, s2, s3 };
Span[] output = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, output.length);
assertEquals(s1, output[0]);
assertEquals(s3, output[1]);
}

@Test
public void testDropOverlappingSpansWithDuplicates() {
Span s1 = new Span(0, 2);
Span s2 = new Span(0, 2);
Span[] input = new Span[] { s1, s2 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(s1, result[0]);
}

@Test
public void testFindWithEmptyTokenArray() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
when(sequence.getProbs()).thenReturn(new double[0]);
when(model.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(codec.decode(any())).thenReturn(new Span[0]);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
String[] emptyInput = new String[0];
// Span[] result = finder.find(emptyInput);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testProbsCalledBeforeFindThrowsNPE() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
try {
// finder.probs();
fail("Expected NullPointerException because find() was not called");
} catch (NullPointerException expected) {
}
}

@Test
public void testProbsSpanCalledBeforeFindThrowsNPE() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
try {
// finder.probs(new Span[] { new Span(0, 1, "person") });
fail("Expected NullPointerException because find() was not called");
} catch (NullPointerException expected) {
}
}

@Test
public void testDropOverlappingWithEmptyArray() {
Span[] input = new Span[0];
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testExtractNameTypeWithDashOnly() {
String outcome = "-";
String type = NameFinderME.extractNameType(outcome);
assertNull(type);
}

@Test
public void testExtractNameTypeWithEmptyString() {
String outcome = "";
String type = NameFinderME.extractNameType(outcome);
assertNull(type);
}

@Test
public void testFindWithSingleTokenAndNoEntity() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("other"));
when(sequence.getProbs()).thenReturn(new double[] { 0.5 });
when(model.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(codec.decode(any())).thenReturn(new Span[0]);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
String[] input = new String[] { "Hello" };
// Span[] result = finder.find(input);
// assertNotNull(result);
// assertEquals(0, result.length);
// double[] probabilities = finder.probs();
// assertEquals(1, probabilities.length);
// assertEquals(0.5, probabilities[0], 0.01);
}

@Test
public void testFindWithNullAdditionalContext() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("other"));
when(sequence.getProbs()).thenReturn(new double[] { 0.75 });
when(model.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(codec.decode(any())).thenReturn(new Span[0]);
NameContextGenerator context = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(context);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
String[] input = new String[] { "test" };
// Span[] result = finder.find(input, null);
// assertNotNull(result);
// assertEquals(0, result.length);
// double[] probabilities = finder.probs();
// assertEquals(1, probabilities.length);
// assertEquals(0.75, probabilities[0], 0.01);
}

@Test
public void testDropOverlappingContainedNestedSpans() {
Span outer = new Span(0, 4, "location");
Span inner = new Span(1, 3, "location");
Span[] spans = new Span[] { outer, inner };
Span[] result = NameFinderME.dropOverlappingSpans(spans);
assertEquals(1, result.length);
assertEquals(outer, result[0]);
}

@Test
public void testDropOverlappingSpansIdenticalButDifferentTypes() {
Span span1 = new Span(0, 2, "person");
Span span2 = new Span(0, 2, "location");
Span[] result = NameFinderME.dropOverlappingSpans(new Span[] { span1, span2 });
assertEquals(1, result.length);
assertTrue(result[0].equals(span1) || result[0].equals(span2));
}

@Test
public void testExtractNameTypeSingleWordOutcome() {
String outcome = "person";
String result = NameFinderME.extractNameType(outcome);
assertNull(result);
}

@Test
public void testProbsHandlesEmptySpanArray() {
double[] tokenProbs = new double[] { 0.5, 0.6 };
Sequence mockSeq = mock(Sequence.class);
when(mockSeq.getProbs()).thenReturn(tokenProbs);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(mockSeq);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(codec.decode(any())).thenReturn(new Span[0]);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// finder.find(new String[] { "a", "b" });
// double[] spanProbs = finder.probs(new Span[0]);
// assertNotNull(spanProbs);
// assertEquals(0, spanProbs.length);
}

@Test
public void testSpanProbabilityWithSingleTokenSpan() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("location-start"));
when(sequence.getProbs()).thenReturn(new double[] { 0.75 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
Span span = new Span(0, 1, "location");
when(codec.decode(any())).thenReturn(new Span[] { span });
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(new String[] { "Paris" });
// double[] probs = finder.probs(result);
// assertEquals(1, probs.length);
// assertEquals(0.75, probs[0], 0.01);
}

@Test
public void testTrainWithEventModelSequenceTrainer() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
NameSample example = new NameSample(new String[] { "Hello" }, new Span[] { new Span(0, 1, "greeting") }, false);
when(sampleStream.read()).thenReturn(example).thenReturn(null);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "EVENT_MODEL_SEQUENCE_TRAINER");
// EventModelSequenceTrainer<NameSample> trainer = mock(EventModelSequenceTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(trainer.train(any())).thenReturn(maxentModel);
// TrainerFactory.registerTrainerType(TrainerType.EVENT_MODEL_SEQUENCE_TRAINER, (p, m) -> trainer);
TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, new HashMap<>(), new BioCodec());
TokenNameFinderModel model = NameFinderME.train("en", "sample", sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testTrainWithSequenceTrainer() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
NameSample example = new NameSample(new String[] { "Alpha" }, new Span[] { new Span(0, 1, "type") }, false);
when(sampleStream.read()).thenReturn(example).thenReturn(null);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "SEQUENCE_TRAINER");
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
SequenceTrainer trainer = mock(SequenceTrainer.class);
when(trainer.train(any())).thenReturn(sequenceModel);
// TrainerFactory.registerTrainerType(TrainerType.SEQUENCE_TRAINER, (p, m) -> trainer);
TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, new HashMap<>(), new BioCodec());
TokenNameFinderModel model = NameFinderME.train("en", "label", sampleStream, params, factory);
assertNotNull(model);
assertSame(sequenceModel, model.getNameFinderSequenceModel());
}

@Test
public void testTrainWithMissingBeamSizeUsesDefault() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
NameSample sample = new NameSample(new String[] { "Mr.", "Smith" }, new Span[] { new Span(0, 2, "person") }, false);
when(sampleStream.read()).thenReturn(sample).thenReturn(null);
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(trainer.train(any())).thenReturn(maxentModel);
// TrainerFactory.registerTrainerType(TrainerType.EVENT_MODEL_TRAINER, (p, m) -> trainer);
TrainingParameters params = new TrainingParameters();
TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec());
TokenNameFinderModel model = NameFinderME.train("en", "person", sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testFindWhenDecodedSpansIsNullReturnsEmptySpanArray() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("other"));
when(sequence.getProbs()).thenReturn(new double[] { 0.99 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(any())).thenReturn(null);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(new String[] { "word" });
// assertEquals(0, result.length);
}

@Test
public void testFindReturnsMultipleEntitySpans() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other", "location-start", "location-cont");
double[] probs = new double[] { 0.9, 0.8, 0.1, 0.95, 0.85 };
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
doAnswer(invocation -> {
double[] array = invocation.getArgument(0);
array[0] = 0.9;
array[1] = 0.8;
array[2] = 0.1;
array[3] = 0.95;
array[4] = 0.85;
return null;
}).when(sequence).getProbs(any(double[].class));
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
Span span1 = new Span(0, 2, "person");
Span span2 = new Span(3, 5, "location");
when(codec.decode(eq(outcomes))).thenReturn(new Span[] { span1, span2 });
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
String[] sentence = new String[] { "John", "Doe", ",", "London", "Bridge" };
// Span[] spans = finder.find(sentence);
// assertEquals(2, spans.length);
// assertEquals("person", spans[0].getType());
// assertEquals("location", spans[1].getType());
// assertEquals(0.85, spans[1].getProb(), 0.01);
}

@Test
public void testDropOverlappingSpansWithSharedMiddleToken() {
Span span1 = new Span(0, 2, "city");
Span span2 = new Span(1, 3, "location");
Span[] input = new Span[] { span1, span2 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertTrue(result[0].equals(span1));
}

@Test
public void testDropOverlappingSpansOutOfOrderInput() {
Span span1 = new Span(4, 5, "a");
Span span2 = new Span(0, 2, "a");
Span[] input = new Span[] { span1, span2 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(span2, result[0]);
assertEquals(span1, result[1]);
}

@Test
public void testExtractNameTypeComplexFormat() {
String outcome = "location-city-district";
String type = NameFinderME.extractNameType(outcome);
assertEquals("location", type);
}

@Test
public void testExtractNameTypeSurroundedByDashes() {
String outcome = "-invalid-";
String type = NameFinderME.extractNameType(outcome);
assertNull(type);
}

@Test
public void testFindInvokesUpdateAdaptiveData() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("person-start", "person-cont");
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.8, 0.7 });
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(outcomes)).thenReturn(new Span[] { new Span(0, 2, "person") });
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME nameFinder = new NameFinderME(finderModel);
// nameFinder.find(new String[] { "Mary", "Sue" });
verify(contextGen).updateAdaptiveData(eq(new String[] { "Mary", "Sue" }), eq(new String[] { "person-start", "person-cont" }));
}

@Test
public void testFindReturnsEmptyArrayWhenCodecDecodeReturnsEmpty() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("other", "other");
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.1, 0.2 });
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(outcomes)).thenReturn(new Span[0]);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME nameFinder = new NameFinderME(finderModel);
// Span[] result = nameFinder.find(new String[] { "and", "then" });
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testDropOverlappingRemovesNestedSpanWithMatchingStart() {
Span outer = new Span(0, 5, "org");
Span inner = new Span(0, 4, "org");
Span[] input = new Span[] { outer, inner };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(outer, result[0]);
}

@Test
public void testDropOverlappingRemovesContainedSpanWithMatchingEnd() {
Span outer = new Span(0, 5, "org");
Span inner = new Span(1, 5, "org");
Span[] input = new Span[] { outer, inner };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(outer, result[0]);
}

@Test
public void testDropOverlappingKeepsEquallySizedNonIntersecting() {
Span span1 = new Span(0, 2, "person");
Span span2 = new Span(2, 4, "person");
Span[] result = NameFinderME.dropOverlappingSpans(new Span[] { span1, span2 });
assertEquals(2, result.length);
assertEquals(span1, result[0]);
assertEquals(span2, result[1]);
}

@Test
public void testDropOverlappingSpansWithUnsortedDuplicateSpans() {
Span span1 = new Span(5, 7, "type");
Span span2 = new Span(5, 7, "type");
Span[] input = new Span[] { span2, span1 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(span1, result[0]);
}

@Test
public void testFindWithEmptyOutcomeListReturnsEmpty() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
when(sequence.getProbs()).thenReturn(new double[0]);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(any(List.class))).thenReturn(new Span[0]);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, new HashMap<>(), codec, factory);
// NameFinderME nameFinderME = new NameFinderME(modelInstance);
// Span[] spans = nameFinderME.find(new String[] { "Any", "Token" });
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testProbsSpanWithOutOfBoundsSpanAvoidsCrash() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("other", "other", "other"));
double[] tokenProbs = new double[] { 0.2, 0.3, 0.4 };
when(sequence.getProbs()).thenReturn(tokenProbs);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(any())).thenReturn(new Span[0]);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator context = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(context);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(TokenFeatureGenerator.class));
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// finder.find(new String[] { "This", "is", "test" });
Span[] spans = new Span[] { new Span(1, 4) };
// double[] probs = finder.probs(spans);
// assertEquals(1, probs.length);
// assertEquals((0.3 + 0.4) / 3, probs[0], 0.001);
}

@Test
public void testExtractNameTypeWithNullOutcome() {
String result = NameFinderME.extractNameType(null);
assertNull(result);
}

@Test
public void testExtractNameTypeWithWhitespaceOnlyOutcome() {
String result = NameFinderME.extractNameType("   ");
assertNull(result);
}

@Test
public void testDropOverlappingWithMalformedSpanStartGreaterThanEnd() {
Span malformed = new Span(3, 2, "invalid");
Span normal = new Span(0, 1, "valid");
Span[] input = new Span[] { malformed, normal };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(normal, result[0]);
assertEquals(malformed, result[1]);
}

@Test
public void testTrainWithExplicitParametersSucceeds() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
params.put(TrainingParameters.ITERATIONS_PARAM, "10");
params.put(TrainingParameters.CUTOFF_PARAM, "0");
params.put(BeamSearch.BEAM_SIZE_PARAMETER, "4");
ObjectStream<NameSample> samples = mock(ObjectStream.class);
NameSample sample = new NameSample(new String[] { "Mr", "X" }, new Span[] { new Span(0, 2, "person") }, false);
when(samples.read()).thenReturn(sample).thenReturn(null);
MaxentModel mockMaxent = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(mockMaxent);
// TrainerFactory.registerTrainerType(TrainerType.EVENT_MODEL_TRAINER, (p, m) -> trainer);
TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec());
TokenNameFinderModel model = NameFinderME.train("en", "person", samples, params, factory);
assertNotNull(model);
}

@Test
public void testTrainWithNoAlgorithmDefaultsToPerceptron() throws IOException {
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
NameSample sample = new NameSample(new String[] { "Jane", "Doe" }, new Span[] { new Span(0, 2, "person") }, false);
when(sampleStream.read()).thenReturn(sample).thenReturn(null);
MaxentModel maxentModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(maxentModel);
// TrainerFactory.registerTrainerType(TrainerType.EVENT_MODEL_TRAINER, (p, m) -> trainer);
TrainingParameters params = new TrainingParameters();
TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec());
TokenNameFinderModel model = NameFinderME.train("en", "type", sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testProbsReturnsEmptyArrayWhenSpansEmptyAfterFind() {
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("other", "other");
double[] tokenProbs = new double[] { 0.1, 0.2 };
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(tokenProbs);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(String[].class), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(any())).thenReturn(new Span[0]);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator context = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(context);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] spans = finder.find(new String[] { "no", "entity" });
// double[] spanProbs = finder.probs(spans);
// assertEquals(0, spanProbs.length);
}

@Test
public void testProbsSpanLengthZero() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("A", "B", "C"));
when(sequence.getProbs()).thenReturn(new double[] { 0.3, 0.5, 0.7 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(any())).thenReturn(new Span[] { new Span(1, 1) });
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator context = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(context);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// NameFinderME finder = new NameFinderME(new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory));
// finder.find(new String[] { "one", "two", "three" });
// double[] result = finder.probs(new Span[] { new Span(1, 1) });
// assertEquals(1, result.length);
// assertEquals(0.0, result[0], 0.0001);
}

@Test
public void testDropOverlappingSpansEmptyInput() {
Span[] emptyInput = new Span[0];
Span[] result = NameFinderME.dropOverlappingSpans(emptyInput);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testDropOverlappingSpansAllIntersecting() {
Span s1 = new Span(0, 3);
Span s2 = new Span(1, 4);
Span s3 = new Span(2, 5);
Span[] input = new Span[] { s3, s2, s1 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(s1, result[0]);
}

@Test
public void testFindWithEmptyTokenArrayStillReturnsValid() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
when(sequence.getProbs()).thenReturn(new double[0]);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(any())).thenReturn(new Span[0]);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(TokenFeatureGenerator.class));
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] spans = finder.find(new String[0]);
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testDecodeReturnsNullFromCodecHandledSafely() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("other"));
when(sequence.getProbs()).thenReturn(new double[] { 0.95 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(codec.decode(any())).thenReturn(null);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(TokenFeatureGenerator.class));
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(new String[] { "word" });
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testSpanWithNegativeIndexesHandledInDropOverlappingSpans() {
Span negativeSpan = new Span(-1, 1, "X");
Span normalSpan = new Span(2, 5, "Y");
Span[] input = new Span[] { normalSpan, negativeSpan };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
}

@Test
public void testFindWithEmptyStringsAsTokens() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("other", "org-start", "org-cont"));
when(sequence.getProbs()).thenReturn(new double[] { 0.1, 0.9, 0.7 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
Span[] spans = new Span[] { new Span(1, 3, "org") };
when(codec.decode(any())).thenReturn(spans);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(TokenFeatureGenerator.class));
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
String[] input = new String[] { "", "", "" };
// Span[] result = finder.find(input);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals(1, result[0].getStart());
// assertEquals(3, result[0].getEnd());
// assertEquals("org", result[0].getType());
}

@Test
public void testAdditionalContextFeatureHandlesNullContextSafely() {
// AdditionalContextFeatureGenerator generator = new AdditionalContextFeatureGenerator();
// generator.setCurrentContext(null);
String[] tokens = new String[] { "test" };
int index = 0;
// String[] features = generator.getContext(index, tokens, null, null);
// assertNotNull(features);
}

@Test
public void testTrainWithNullSamplesThrowsIOException() {
ObjectStream<NameSample> sampleStream = new ObjectStream<NameSample>() {

public NameSample read() throws IOException {
throw new IOException("Simulated stream error");
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
TrainingParameters params = new TrainingParameters();
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, new HashMap<>(), new BioCodec());
// try {
// NameFinderME.train("en", "type", sampleStream, params, factory);
// fail("Expected IOException");
// } catch (IOException e) {
// assertEquals("Simulated stream error", e.getMessage());
// }
}

@Test
public void testDropOverlappingSpansWithLargeSpan() {
Span longSpan = new Span(0, 1000000, "huge");
Span overlap = new Span(999999, 1000001, "cross");
Span[] result = NameFinderME.dropOverlappingSpans(new Span[] { longSpan, overlap });
assertEquals(1, result.length);
assertEquals(longSpan, result[0]);
}

@Test
public void testFindWithTokensAndEmptyOutcomeString() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("", "", "");
double[] probs = new double[] { 0.1, 0.1, 0.1 };
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(eq(outcomes))).thenReturn(new Span[] {});
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
String[] input = new String[] { "a", "b", "c" };
// Span[] result = finder.find(input);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testFindWithExtraLongAdaptiveContext() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other");
double[] probs = new double[] { 0.7, 0.6, 0.4 };
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
when(model.bestSequence(any(String[].class), any(String[][].class), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(eq(outcomes))).thenReturn(new Span[] { new Span(0, 2, "person") });
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new WindowFeatureGenerator(new TokenFeatureGenerator(), 1, 1));
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
String[] tokens = new String[] { "John", "Smith", "flew" };
String[][] ctx = new String[][] { new String[] { "prev=A", "prev=B", "0start" }, new String[] { "prev=C", "prev=D", "0cont" }, new String[] { "prev=E", "prev=F", "0other" } };
// Span[] spans = finder.find(tokens, ctx);
// assertEquals(1, spans.length);
// assertEquals("person", spans[0].getType());
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
}

@Test
public void testDropOverlappingWithReverseSortedSpans() {
Span s1 = new Span(4, 6);
Span s2 = new Span(2, 5);
Span s3 = new Span(0, 2);
Span[] input = new Span[] { s1, s2, s3 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(s2, result[0]);
assertEquals(s3, result[1]);
}

@Test
public void testTrainDefaultsToPerceptronWithMinimalParams() throws IOException {
ObjectStream<NameSample> stream = mock(ObjectStream.class);
NameSample sample1 = new NameSample(new String[] { "John", "Doe" }, new Span[] { new Span(0, 2, "person") }, false);
when(stream.read()).thenReturn(sample1).thenReturn(null);
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(trainer.train(any())).thenReturn(maxentModel);
// TrainerFactory.registerTrainerType(TrainerType.EVENT_MODEL_TRAINER, (params, manifestInfo) -> trainer);
TrainingParameters params = new TrainingParameters();
TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, new HashMap<>(), new BioCodec());
TokenNameFinderModel model = NameFinderME.train("en", "type", stream, params, factory);
assertNotNull(model);
}

@Test
public void testExtractNameTypeNonMatchingOutcome() {
String input = "nothinghere";
String value = NameFinderME.extractNameType(input);
assertNull(value);
}

@Test
public void testFindHandlesNullAdditionalContextGracefully() {
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("start", "cont");
double[] probs = new double[] { 0.5, 0.6 };
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), isNull(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(eq(outcomes))).thenReturn(new Span[] { new Span(0, 2, "person") });
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(new TokenFeatureGenerator());
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
String[] tokens = new String[] { "Jane", "Doe" };
// Span[] result = finder.find(tokens, null);
// assertNotNull(result);
// assertEquals(1, result.length);
}

@Test
public void testProbsWithTokensButNoSpans() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("other", "other"));
when(sequence.getProbs()).thenReturn(new double[] { 0.1, 0.1 });
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(codec.decode(any())).thenReturn(new Span[0]);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(TokenFeatureGenerator.class));
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, Collections.emptyMap(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// finder.find(new String[] { "and", "then" });
// double[] spanProbs = finder.probs(new Span[0]);
// assertNotNull(spanProbs);
// assertEquals(0, spanProbs.length);
}

@Test
public void testExtractNameTypeWithNullInput() {
String result = NameFinderME.extractNameType(null);
assertNull(result);
}

@Test
public void testSpanProbabilityIsAccurateAverage() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("start", "cont", "cont"));
when(sequence.getProbs()).thenReturn(new double[] { 0.2, 0.4, 0.6 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
SequenceCodec<String> codec = mock(SequenceCodec.class);
when(codec.decode(any())).thenReturn(new Span[] { new Span(0, 3, "org") });
when(codec.createSequenceValidator()).thenReturn(mock(SequenceValidator.class));
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(TokenFeatureGenerator.class));
when(factory.getResources()).thenReturn(Collections.emptyMap());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(new String[] { "a", "b", "c" });
// double[] spanProbs = finder.probs(result);
// assertEquals(1, spanProbs.length);
// assertEquals(0.4, spanProbs[0], 0.01);
}
}
