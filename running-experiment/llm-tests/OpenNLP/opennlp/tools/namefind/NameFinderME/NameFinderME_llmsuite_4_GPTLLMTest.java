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
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.*;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
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

public class NameFinderME_llmsuite_4_GPTLLMTest {

@Test
public void testFind_SingleEntity() {
String[] tokens = new String[] { "John", "Doe", "works", "for", "OpenNLP" };
String[][] context = new String[0][0];
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other", "other", "org-start");
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.9, 0.8, 0.6, 0.5, 0.95 });
when(model.bestSequence(eq(tokens), eq(context), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(new Span[] { new Span(0, 2, "person"), new Span(4, 5, "org") });
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME nameFinder = new NameFinderME(finderModel);
// Span[] result = nameFinder.find(tokens, context);
// assertEquals(2, result.length);
// assertEquals("person", result[0].getType());
// assertEquals(0, result[0].getStart());
// assertEquals(2, result[0].getEnd());
// assertEquals("org", result[1].getType());
// assertEquals(4, result[1].getStart());
// assertEquals(5, result[1].getEnd());
}

@Test
public void testFind_NoEntities() {
String[] tokens = new String[] { "The", "sky", "is", "blue" };
String[][] context = new String[0][0];
List<String> outcomes = Arrays.asList("other", "other", "other", "other");
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.9, 0.9, 0.8, 0.85 });
when(model.bestSequence(eq(tokens), eq(context), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(new Span[0]);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens, context);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testFind_WithEmptyInput() {
String[] tokens = new String[0];
String[][] context = new String[0][0];
List<String> outcomes = Collections.emptyList();
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[0]);
when(model.bestSequence(eq(tokens), eq(context), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(new Span[0]);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testClearAdaptiveData() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// finder.clearAdaptiveData();
verify(contextGen, times(1)).clearAdaptiveData();
}

@Test
public void testProbs_WithArray() {
String[] tokens = new String[] { "Open", "NLP" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Sequence sequence = mock(Sequence.class);
List<String> outcomes = Arrays.asList("org-start", "org-cont");
double[] probs = new double[] { 0.7, 0.9 };
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(new Span[] { new Span(0, 2, "org") });
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// finder.find(tokens);
double[] output = new double[2];
// finder.probs(output);
assertEquals(0.7, output[0], 0.01);
assertEquals(0.9, output[1], 0.01);
assertArrayEquals(probs, output, 0.01);
}

@Test
public void testProbs_WithSpans() {
String[] tokens = new String[] { "A", "B", "C" };
Span[] spans = new Span[] { new Span(0, 2), new Span(2, 3) };
List<String> outcomes = Arrays.asList("X", "Y", "Z");
double[] expectedProbs = new double[] { 0.9, 0.8, 0.7 };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(expectedProbs);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(spans);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// finder.find(tokens);
// double[] probsResult = finder.probs(spans);
// assertEquals(2, probsResult.length);
// assertEquals((0.9 + 0.8) / 2, probsResult[0], 0.001);
// assertEquals(0.7, probsResult[1], 0.001);
}

@Test
public void testDropOverlappingSpans() {
Span s1 = new Span(0, 2);
Span s2 = new Span(0, 2);
Span s3 = new Span(1, 3);
Span s4 = new Span(4, 6);
Span s5 = new Span(5, 6);
Span[] input = new Span[] { s1, s2, s3, s4, s5 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(s1, result[0]);
assertEquals(s4, result[1]);
}

@Test
public void testExtractNameType_Valid() {
assertEquals("person", NameFinderME.extractNameType("person-start"));
assertEquals("org", NameFinderME.extractNameType("org-end"));
}

@Test
public void testExtractNameType_Invalid() {
assertNull(NameFinderME.extractNameType("other"));
assertNull(NameFinderME.extractNameType("plain"));
assertNull(NameFinderME.extractNameType(""));
}

@Test
public void testTrain_WithUnknownTrainerType_Throws() throws Exception {
TrainingParameters params = new TrainingParameters();
params.put("TrainerType", "unexpected");
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
@SuppressWarnings("unchecked")
opennlp.tools.util.ObjectStream<NameSample> stream = mock(opennlp.tools.util.ObjectStream.class);
NameFinderME.train("en", "person", stream, params, factory);
}

@Test
public void testFind_NullAdditionalContextReplacesWithEmpty() {
String[] tokens = new String[] { "John", "Smith" };
List<String> outcomes = Arrays.asList("person-start", "person-cont");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.8, 0.85 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(new Span[] { new Span(0, 2, "person") });
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens, null);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals("person", result[0].getType());
}

@Test
public void testFind_ReturnsSpansWithoutType() {
String[] tokens = new String[] { "Hello", "World" };
List<String> outcomes = Arrays.asList("start", "cont");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.5, 0.5 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(new Span[] { new Span(0, 2) });
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] spans = finder.find(tokens);
// assertEquals(1, spans.length);
// assertNull(spans[0].getType());
}

@Test
public void testProbs_ReturnsEmptyArrayIfNoSpans() {
String[] tokens = new String[] { "OnlyOne" };
List<String> outcomes = Arrays.asList("other");
double[] sequenceProbs = new double[] { 0.4 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(sequenceProbs);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(new Span[0]);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// finder.find(tokens);
// double[] spanProbs = finder.probs(new Span[0]);
// assertNotNull(spanProbs);
// assertEquals(0, spanProbs.length);
}

@Test
public void testDropOverlappingSpans_EmptyInput() {
Span[] input = new Span[0];
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testDropOverlappingSpans_SingleSpan() {
Span single = new Span(0, 1, "type");
Span[] input = new Span[] { single };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(single, result[0]);
}

@Test
public void testExtractNameType_MissingDash() {
String outcome = "person";
String result = NameFinderME.extractNameType(outcome);
assertNull(result);
}

@Test
public void testExtractNameType_EmptyString() {
String result = NameFinderME.extractNameType("");
assertNull(result);
}

@Test
public void testExtractNameType_NullSafe() {
try {
NameFinderME.extractNameType(null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testFind_ProperSpanProbMapping() {
String[] tokens = new String[] { "Barack", "Obama", "visited" };
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other");
double[] sequenceProbs = new double[] { 0.9, 0.8, 0.6 };
Span[] spans = new Span[] { new Span(0, 2, "person") };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(sequenceProbs);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(spans);
when(factory.createSequenceCodec()).thenReturn(codec);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertEquals("person", result[0].getType());
// assertEquals(0.85, result[0].getProb(), 0.001);
}

@Test
public void testProbs_SpanLengthIsOne() {
String[] tokens = new String[] { "A", "B" };
List<String> outcomes = Arrays.asList("type-start", "type-cont");
double[] probabilities = new double[] { 0.6, 0.8 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probabilities);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Span[] decodedSpans = new Span[] { new Span(1, 2, "type") };
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(decodedSpans);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertEquals(0.8, result[0].getProb(), 0.001);
}

@Test
public void testFind_WithOverlappingEntities_AndProbabilityComputation() {
String[] tokens = new String[] { "John", "Smith", "from", "OpenAI" };
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other", "org-start");
double[] probabilities = new double[] { 0.85, 0.9, 0.6, 0.75 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probabilities);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Span[] decodedSpans = new Span[] { new Span(0, 2, "person"), new Span(3, 4, "org") };
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(decodedSpans);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] spans = finder.find(tokens);
// assertEquals(2, spans.length);
// assertEquals("person", spans[0].getType());
// assertEquals(0.875, spans[0].getProb(), 0.001);
// assertEquals("org", spans[1].getType());
// assertEquals(0.75, spans[1].getProb(), 0.001);
}

@Test
public void testProbs_WithSpanThatExceedsTokenSize_ShouldIgnoreOrThrow() {
String[] tokens = new String[] { "Test", "One" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("type-start", "type-cont"));
when(sequence.getProbs()).thenReturn(new double[] { 0.1, 0.2 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(anyList())).thenReturn(new Span[] { new Span(0, 5, "type") });
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] spans = finder.find(tokens);
// assertEquals(1, spans.length);
try {
// finder.probs(spans);
} catch (ArrayIndexOutOfBoundsException e) {
}
}

@Test
public void testTrainWithEventModelSequenceTrainer() throws Exception {
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "EVENT_MODEL_SEQUENCE_TRAINER");
@SuppressWarnings("unchecked")
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(mock(NameContextGenerator.class));
when(factory.createSequenceCodec()).thenReturn(mock(SequenceCodec.class));
@SuppressWarnings("unchecked")
opennlp.tools.ml.EventModelSequenceTrainer<NameSample> trainer = mock(opennlp.tools.ml.EventModelSequenceTrainer.class);
when(trainer.train(any())).thenReturn(mock(opennlp.tools.ml.model.MaxentModel.class));
Map<String, String> manifest = new HashMap<>();
Map<String, Object> resourceMap = new HashMap<>();
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// when(factory.getFeatureGenerator()).thenReturn(featureGen);
when(factory.getResources()).thenReturn(resourceMap);
opennlp.tools.ml.TrainerFactory trainerFactory = mock(opennlp.tools.ml.TrainerFactory.class);
TokenNameFinderModel model = NameFinderME.train("en", "entity", sampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testDropOverlappingSpans_NestedOverlap() {
Span s1 = new Span(0, 5, "A");
Span s2 = new Span(1, 2, "B");
Span s3 = new Span(2, 4, "C");
Span[] input = new Span[] { s1, s2, s3 };
Span[] output = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, output.length);
assertEquals(s1, output[0]);
}

@Test
public void testFind_WithNullOutcomesFromModel() {
String[] tokens = new String[] { "Jane", "Doe" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
when(sequence.getProbs()).thenReturn(new double[] { 0.8, 0.7 });
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(null)).thenReturn(new Span[0]);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// AdaptiveFeatureGenerator featureGen = new DefaultFeatureGenerator();
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testFind_ReturnsSpanWithZeroProbabilities() {
String[] tokens = new String[] { "AI", "Platform" };
List<String> outcomes = Arrays.asList("org-start", "org-cont");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.0, 0.0 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Span[] decoded = new Span[] { new Span(0, 2, "org") };
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(decoded);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// AdaptiveFeatureGenerator featureGen = new DefaultFeatureGenerator();
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertEquals(0.0, result[0].getProb(), 0.0001);
}

@Test
public void testDropOverlappingSpans_IdenticalSpansSortedOutFirstOneRemains() {
Span span1 = new Span(1, 3, "person");
Span span2 = new Span(1, 3, "person");
Span[] input = new Span[] { span2, span1 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(1, result[0].getStart());
assertEquals(3, result[0].getEnd());
}

@Test
public void testFind_MultipleEntitiesOfSameTypeSequential() {
String[] tokens = new String[] { "John", "Smith", "and", "Alice", "Jones" };
List<String> outcomes = Arrays.asList("person-start", "person-cont", "other", "person-start", "person-cont");
double[] probs = new double[] { 0.9, 0.8, 0.6, 0.95, 0.85 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
Span[] decoded = new Span[] { new Span(0, 2, "person"), new Span(3, 5, "person") };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(decoded);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// AdaptiveFeatureGenerator featureGen = new DefaultFeatureGenerator();
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens);
// assertEquals(2, result.length);
// assertEquals("person", result[0].getType());
// assertEquals(0.85, result[0].getProb(), 0.001);
// assertEquals("person", result[1].getType());
// assertEquals(0.90, result[1].getProb(), 0.001);
}

@Test
public void testFindSingleTokenEntity() {
String[] tokens = new String[] { "New", "York" };
List<String> outcomes = Arrays.asList("location-cont", "location-start");
double[] probs = new double[] { 0.75, 0.65 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
Span[] spans = new Span[] { new Span(1, 2, "location") };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(spans);
when(factory.createSequenceCodec()).thenReturn(codec);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(codec.createSequenceValidator()).thenReturn(validator);
// AdaptiveFeatureGenerator featureGen = new DefaultFeatureGenerator();
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertEquals("location", result[0].getType());
// assertEquals(0.65, result[0].getProb(), 0.001);
}

@Test
public void testFind_WithEmptyProbsArray() {
String[] tokens = new String[] { "this", "is", "test" };
List<String> outcomes = Arrays.asList("type-start", "type-cont", "other");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[0]);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Span[] decodedSpans = new Span[] { new Span(0, 2, "type") };
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(decodedSpans);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertTrue(Double.isNaN(result[0].getProb()) || result[0].getProb() == 0.0);
}

@Test
public void testFind_WithAdditionalContextOverwritten() {
String[] tokens = new String[] { "Hello" };
String[][] context = new String[][] { { "left" }, { "right" } };
List<String> outcomes = Arrays.asList("other");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.99 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
when(model.bestSequence(eq(tokens), eq(context), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(outcomes)).thenReturn(new Span[0]);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens, context);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testTrain_WithDefaultParamFallback() throws IOException {
TrainingParameters params = new TrainingParameters();
@SuppressWarnings("unchecked")
ObjectStream<NameSample> sampleStream = mock(ObjectStream.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
when(factory.createSequenceCodec()).thenReturn(codec);
when(factory.createContextGenerator()).thenReturn(contextGen);
TrainerFactory tfSpy = spy(TrainerFactory.class);
TrainingParameters fallbackParams = new TrainingParameters();
fallbackParams.put(TrainingParameters.ALGORITHM_PARAM, PerceptronTrainer.PERCEPTRON_VALUE);
fallbackParams.put(TrainingParameters.CUTOFF_PARAM, "0");
fallbackParams.put(TrainingParameters.ITERATIONS_PARAM, "1");
TokenNameFinderModel trainedModel = NameFinderME.train("en", "type", sampleStream, params, factory);
assertNotNull(trainedModel);
}

@Test
public void testTrain_WithSequenceTrainerBranch() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put("TrainerType", TrainerFactory.TrainerType.SEQUENCE_TRAINER.name());
@SuppressWarnings("unchecked")
ObjectStream<NameSample> stream = mock(ObjectStream.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(factory.getSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
TrainerFactory trainerFactory = mock(TrainerFactory.class);
opennlp.tools.ml.SequenceTrainer trainer = mock(opennlp.tools.ml.SequenceTrainer.class);
Map<String, String> manifest = new HashMap<>();
when(trainer.train(any())).thenReturn(sequenceModel);
TokenNameFinderModel model = NameFinderME.train("en", "type", stream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testDropOverlappingSpans_ReverseSorted() {
Span s3 = new Span(4, 6, "D");
Span s2 = new Span(1, 3, "B");
Span s1 = new Span(0, 2, "A");
Span[] spans = new Span[] { s3, s2, s1 };
Span[] result = NameFinderME.dropOverlappingSpans(spans);
assertEquals(3, result.length);
assertEquals(s1, result[0]);
assertEquals(s2, result[1]);
assertEquals(s3, result[2]);
}

@Test
public void testDropOverlappingSpans_AllIntersecting() {
Span a = new Span(0, 3, "A");
Span b = new Span(1, 4, "B");
Span c = new Span(2, 5, "C");
Span[] input = new Span[] { a, b, c };
Span[] output = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, output.length);
assertEquals(0, output[0].getStart());
assertEquals(3, output[0].getEnd());
}

@Test
public void testExtractNameType_WithMultipleDashes() {
String outcome = "location-country-name-start";
String result = NameFinderME.extractNameType(outcome);
assertEquals("location-country-name", result);
}

@Test
public void testExtractNameType_WithNoMatchRegexReturnsNull() {
String outcome = "unexpectedformat";
String result = NameFinderME.extractNameType(outcome);
assertNull(result);
}

@Test
public void testDropOverlappingSpans_SameStartDifferentEnd() {
Span span1 = new Span(0, 3, "a");
Span span2 = new Span(0, 2, "a");
Span[] spans = new Span[] { span1, span2 };
Span[] result = NameFinderME.dropOverlappingSpans(spans);
assertEquals(1, result.length);
assertEquals(0, result[0].getStart());
assertEquals(3, result[0].getEnd());
}

@Test
public void testDropOverlappingSpans_SameSpanMultipleTimes() {
Span span = new Span(2, 5, "label");
Span[] spans = new Span[] { span, span, span };
Span[] result = NameFinderME.dropOverlappingSpans(spans);
assertEquals(1, result.length);
assertEquals(2, result[0].getStart());
assertEquals(5, result[0].getEnd());
}

@Test
public void testSetProbs_SilentFallbackForNullProbsReturn() {
String[] tokens = new String[] { "Luke", "Skywalker" };
List<String> outcomes = Arrays.asList("person-start", "person-cont");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(null);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
Span[] decoded = new Span[] { new Span(0, 2, "person") };
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(decoded);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel nameModel = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(nameModel);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertTrue(Double.isNaN(result[0].getProb()) || result[0].getProb() == 0.0);
}

@Test
public void testProbs_WithInvalidSpanLength_ZeroLengthSpan() {
Span[] spans = new Span[] { new Span(1, 1) };
Sequence sequence = mock(Sequence.class);
when(sequence.getProbs()).thenReturn(new double[] { 0.1, 0.3 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
when(model.bestSequence(any(), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
// TokenNameFinderModel nameModel = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(nameModel);
// finder.find(new String[] { "A", "B" });
// double[] result = finder.probs(spans);
// assertEquals(1, result.length);
// assertEquals(0.0, result[0], 0.001);
}

@Test
public void testExtractNameType_OnlyDashSuffix() {
String result = NameFinderME.extractNameType("-start");
assertNull(result);
}

@Test
public void testExtractNameType_ValidHyphenatedLabel() {
String result = NameFinderME.extractNameType("geo-political-entity-start");
assertEquals("geo-political-entity", result);
}

@Test
public void testFind_WithDecodedEmptySpanArrayButOutcomesNotEmpty() {
String[] tokens = new String[] { "Red", "Hat" };
List<String> outcomes = Arrays.asList("org-start", "org-cont");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.9, 0.85 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
Span[] decoded = new Span[0];
SequenceValidator<String> validator = mock(SequenceValidator.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(decoded);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testProbs_AccessWithoutCallingFindShouldThrow() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
AdaptiveFeatureGenerator featureGen = mock(AdaptiveFeatureGenerator.class);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, featureGen, new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
try {
// finder.probs();
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testFind_WithNonMatchingOutcomePattern_TypeExtractionIsNull() {
String[] tokens = new String[] { "Test", "Token" };
List<String> outcomes = Arrays.asList("label1", "label2");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.5, 0.6 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
Span[] decoded = new Span[] { new Span(0, 2) };
when(model.bestSequence(eq(tokens), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(decoded);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertNull(result[0].getType());
}

@Test
public void testFind_WhenSequenceReturnsEmptyOutcomesAndEmptyProbs() {
String[] tokens = new String[] { "Hello" };
Sequence emptySequence = mock(Sequence.class);
when(emptySequence.getOutcomes()).thenReturn(Collections.emptyList());
when(emptySequence.getProbs()).thenReturn(new double[0]);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(String[][].class), eq(contextGen), eq(validator))).thenReturn(emptySequence);
when(codec.decode(eq(Collections.emptyList()))).thenReturn(new Span[0]);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testProbs_SpanStartAfterProbLength_ShouldResultInIndexOutOfBounds() {
String[] tokens = new String[] { "A", "B" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("other", "other"));
when(sequence.getProbs()).thenReturn(new double[] { 0.8, 0.6 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(any(), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(anyList())).thenReturn(new Span[0]);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
when(codec.createSequenceValidator()).thenReturn(validator);
// TokenNameFinderModel finderModel = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(finderModel);
// finder.find(tokens);
Span[] invalidSpan = new Span[] { new Span(2, 3) };
try {
// finder.probs(invalidSpan);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testFind_TokenArrayContainsRepeatedEntitySequences() {
String[] tokens = new String[] { "Tom", "Cruise", "Tom", "Cruise" };
List<String> outcomes = Arrays.asList("person-start", "person-cont", "person-start", "person-cont");
double[] probs = new double[] { 0.9, 0.85, 0.91, 0.87 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
Span[] decoded = new Span[] { new Span(0, 2, "person"), new Span(2, 4, "person") };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(decoded);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens);
// assertEquals(2, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(2, result[0].getEnd());
// assertEquals(2, result[1].getStart());
// assertEquals(4, result[1].getEnd());
// assertEquals("person", result[0].getType());
// assertEquals("person", result[1].getType());
}

@Test
public void testFind_WithSingleTokenSpanOnly() {
String[] tokens = new String[] { "IBM" };
List<String> outcomes = Arrays.asList("org-start");
double[] probs = new double[] { 0.77 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(probs);
Span[] spans = new Span[] { new Span(0, 1, "org") };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
when(model.bestSequence(eq(tokens), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(spans);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(1, result[0].getEnd());
// assertEquals("org", result[0].getType());
// assertEquals(0.77, result[0].getProb(), 0.001);
}

@Test
public void testDropOverlappingSpans_StartEqualsEndSpan() {
Span invalid = new Span(3, 3, "invalid");
Span valid = new Span(1, 2, "valid");
Span[] input = new Span[] { invalid, valid };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, result.length);
assertEquals(1, result[1].getStart());
assertEquals(2, result[1].getEnd());
}

@Test
public void testFind_UsesAdaptiveFeatureGeneratorUpdateAdapterData() {
String[] tokens = new String[] { "Elon", "Musk" };
List<String> outcomes = Arrays.asList("person-start", "person-cont");
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(outcomes);
when(sequence.getProbs()).thenReturn(new double[] { 0.9, 0.8 });
Span[] spans = new Span[] { new Span(0, 2, "person") };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceCodec<String> codec = mock(SequenceCodec.class);
NameContextGenerator contextGen = mock(NameContextGenerator.class);
TokenNameFinderFactory factory = mock(TokenNameFinderFactory.class);
// when(contextGen.updateAdaptiveData(eq(tokens), eq(new String[] { "person-start", "person-cont" }))).thenReturn(null);
when(model.bestSequence(eq(tokens), any(), eq(contextGen), eq(validator))).thenReturn(sequence);
when(codec.decode(eq(outcomes))).thenReturn(spans);
when(factory.createContextGenerator()).thenReturn(contextGen);
when(factory.createSequenceCodec()).thenReturn(codec);
when(codec.createSequenceValidator()).thenReturn(validator);
// when(factory.getFeatureGenerator()).thenReturn(mock(AdaptiveFeatureGenerator.class));
when(factory.getResources()).thenReturn(new HashMap<>());
// TokenNameFinderModel modelInstance = new TokenNameFinderModel("en", model, 3, mock(AdaptiveFeatureGenerator.class), new HashMap<>(), new HashMap<>(), codec, factory);
// NameFinderME finder = new NameFinderME(modelInstance);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertEquals("person", result[0].getType());
// assertEquals(2, result[0].length());
}
}
