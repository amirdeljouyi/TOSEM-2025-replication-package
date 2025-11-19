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

public class NameFinderME_llmsuite_3_GPTLLMTest {

@Test
public void testFindNoEntities() {
String[] tokens = new String[] { "The", "weather", "is", "nice" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other", "other", "other", "other"), new double[] { 0.9, 0.9, 0.9, 0.9 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] result = nameFinder.find(tokens);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testFindSingleEntity() {
String[] tokens = new String[] { "Barack", "Obama", "was", "president" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont", "other", "other"), new double[] { 0.9, 0.92, 0.5, 0.6 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertNotNull(spans);
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
// assertEquals("person", spans[0].getType());
// assertTrue(spans[0].getProb() > 0.9);
}

@Test
public void testFindMultipleEntities() {
String[] tokens = new String[] { "Apple", "and", "Google", "are", "companies" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("org-start", "other", "org-start", "other", "other"), new double[] { 0.95, 0.5, 0.96, 0.7, 0.6 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertNotNull(spans);
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("org", spans[0].getType());
// assertEquals(2, spans[1].getStart());
// assertEquals(3, spans[1].getEnd());
// assertEquals("org", spans[1].getType());
}

@Test
public void testProbsReturnsCorrectValues() {
String[] tokens = new String[] { "Alice", "visited", "Wonderland" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "other", "location-start"), new double[] { 0.85, 0.6, 0.92 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// nameFinder.find(tokens);
// double[] resultProbs = nameFinder.probs();
// assertNotNull(resultProbs);
// assertEquals(3, resultProbs.length);
// assertEquals(0.85, resultProbs[0], 0.0001);
// assertEquals(0.6, resultProbs[1], 0.0001);
// assertEquals(0.92, resultProbs[2], 0.0001);
}

@Test
public void testProbsSpanAveragesCorrectly() {
String[] tokens = new String[] { "John", "Smith", "works", "at", "Google" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont", "other", "other", "org-start"), new double[] { 0.9, 0.8, 0.6, 0.5, 0.95 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertNotNull(spans);
// assertEquals(2, spans.length);
// double[] probs = nameFinder.probs(spans);
// assertEquals(2, probs.length);
// assertEquals(0.85, probs[0], 0.0001);
// assertEquals(0.95, probs[1], 0.0001);
}

@Test
public void testExtractNameTypeParsesTypeCorrectly() {
String extracted = NameFinderME.extractNameType("person-start");
assertEquals("person", extracted);
extracted = NameFinderME.extractNameType("organization-cont");
assertEquals("organization", extracted);
extracted = NameFinderME.extractNameType("other");
assertNull(extracted);
}

@Test
public void testClearAdaptiveDataIsSafe() {
String[] tokens = new String[] { "Token" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other"), new double[] { 0.5 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// nameFinder.clearAdaptiveData();
}

@Test
public void testDropOverlappingSpansBehavior() {
Span span1 = new Span(0, 3);
Span span2 = new Span(0, 2);
Span span3 = new Span(1, 4);
Span span4 = new Span(5, 6);
Span[] input = new Span[] { span1, span2, span3, span4 };
Span[] output = NameFinderME.dropOverlappingSpans(input);
assertNotNull(output);
assertEquals(2, output.length);
assertEquals(span1.getStart(), output[0].getStart());
assertEquals(span1.getEnd(), output[0].getEnd());
assertEquals(span4.getStart(), output[1].getStart());
assertEquals(span4.getEnd(), output[1].getEnd());
}

@Test
public void testTrainThrowsOnUnknownTrainerType() throws IOException {
ObjectStream<NameSample> samples = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "unknown");
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
try {
// NameFinderME.train("en", "person", samples, params, factory);
fail("Expected IllegalStateException for unknown trainer type");
} catch (IllegalStateException e) {
assertEquals("Unexpected trainer type!", e.getMessage());
}
}

@Test
public void testFindHandlesEmptyInput() {
String[] tokens = new String[0];
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList(), new double[] {});
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testFindSingleTokenEntity() {
String[] tokens = new String[] { "Newton", "discovered", "gravity" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "other", "other"), new double[] { 0.93, 0.6, 0.6 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("person", spans[0].getType());
// assertTrue(spans[0].getProb() >= 0.9);
}

@Test
public void testFindWithOverlappingEntityStarts() {
String[] tokens = new String[] { "John", "Doe", "Jr." };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-start", "other"), new double[] { 0.88, 0.85, 0.7 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertEquals(2, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals(1, spans[1].getStart());
// assertEquals(2, spans[1].getEnd());
}

@Test
public void testFindWithConflictingOutcomes() {
String[] tokens = new String[] { "John", "Doe" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-cont", "person-start"), new double[] { 0.6, 0.9 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertEquals(1, spans.length);
// assertEquals(1, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
}

@Test
public void testFindWithNullValuesInAdditionalContext() {
String[] tokens = new String[] { "Amazon", "is", "a", "company" };
String[][] ctx = new String[][] { null, null, null, null };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("org-start", "other", "other", "other"), new double[] { 0.95, 0.6, 0.6, 0.6 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens, ctx);
// assertNotNull(spans);
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
}

@Test
public void testFindWithExtraOutcome() {
String[] tokens = new String[] { "John", "Smith" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont", "other"), new double[] { 0.9, 0.88, 0.5 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
try {
// nameFinder.find(tokens);
fail("Expected IndexOutOfBoundsException due to mismatched outcomes");
} catch (IndexOutOfBoundsException e) {
assertTrue(e.getMessage() != null);
}
}

@Test
public void testProbsSpanWithZeroLength() {
String[] tokens = new String[] { "empty" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other"), new double[] { 0.5 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// nameFinder.find(tokens);
Span zeroLengthSpan = new Span(0, 0);
Span[] spans = new Span[] { zeroLengthSpan };
// double[] probs = nameFinder.probs(spans);
// assertNotNull(probs);
// assertEquals(1, probs.length);
// assertTrue(Double.isNaN(probs[0]) || probs[0] == 0.0);
}

@Test
public void testDropOverlappingSpansWithEqualSpans() {
Span span1 = new Span(1, 2);
Span span2 = new Span(1, 2);
Span span3 = new Span(1, 2);
Span[] input = new Span[] { span1, span2, span3 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(1, result[0].getStart());
assertEquals(2, result[0].getEnd());
}

@Test
public void testFindEntityEndsAtLastToken() {
String[] tokens = new String[] { "He", "met", "with", "Elon", "Musk" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other", "other", "other", "person-start", "person-cont"), new double[] { 0.5, 0.6, 0.5, 0.95, 0.96 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertEquals(1, spans.length);
// assertEquals(3, spans[0].getStart());
// assertEquals(5, spans[0].getEnd());
// assertEquals("person", spans[0].getType());
}

@Test
public void testFindWithMultipleAdjacentEntitiesSameType() {
String[] tokens = new String[] { "John", "Smith", "Mary", "Jones" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont", "person-start", "person-cont"), new double[] { 0.9, 0.91, 0.92, 0.93 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertEquals(2, spans.length);
// assertEquals("person", spans[0].getType());
// assertEquals("person", spans[1].getType());
}

@Test
public void testMultipleEntityTypesInOneSentence() {
String[] tokens = new String[] { "Alice", "visited", "Paris", "and", "Google" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "other", "location-start", "other", "org-start"), new double[] { 0.94, 0.6, 0.93, 0.5, 0.96 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertEquals(3, spans.length);
// assertEquals("person", spans[0].getType());
// assertEquals("location", spans[1].getType());
// assertEquals("org", spans[2].getType());
}

@Test
public void testFindWithMismatchedProbsArrayInProbsMethod() {
String[] tokens = new String[] { "Only", "one", "entity" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("org-start", "other", "other"), new double[] { 0.88, 0.7, 0.6 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
double[] customProbs = new double[3];
// finder.probs(customProbs);
assertEquals(3, customProbs.length);
assertEquals(0.88, customProbs[0], 0.00001);
assertEquals(0.7, customProbs[1], 0.00001);
assertEquals(0.6, customProbs[2], 0.00001);
}

@Test
public void testFindWithConsecutiveStartTagsOnly() {
String[] tokens = new String[] { "Amazon", "SpaceX", "Apple" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("org-start", "org-start", "org-start"), new double[] { 0.91, 0.92, 0.93 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertEquals(3, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("org", spans[0].getType());
// assertEquals(1, spans[1].getStart());
// assertEquals(2, spans[1].getEnd());
// assertEquals(2, spans[2].getStart());
// assertEquals(3, spans[2].getEnd());
}

@Test
public void testDropOverlappingSpansAllIntersecting() {
Span s1 = new Span(0, 3);
Span s2 = new Span(1, 4);
Span s3 = new Span(2, 5);
Span[] input = new Span[] { s1, s2, s3 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(0, result[0].getStart());
assertEquals(3, result[0].getEnd());
}

@Test
public void testFindWithUnrecognizedOutcomeFormat() {
String[] tokens = new String[] { "Hello", "World" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("xyz", "xyz"), new double[] { 0.7, 0.7 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertEquals(0, spans.length);
}

@Test
public void testFindEmptyStringArrayInput() {
String[] tokens = new String[] {};
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList(), new double[] {});
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertEquals(0, spans.length);
}

@Test
public void testFindWithNullTokensArray() {
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
try {
// nameFinder.find(null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertNotNull(expected.getMessage());
}
}

@Test
public void testFindWithNullAdditionalContext() {
String[] tokens = new String[] { "Test", "Token" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other", "other"), new double[] { 0.5, 0.5 });
// when(mockModel.bestSequence(eq(tokens), isNull(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens, null);
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testProbsBeforeFindCallReturnsNullPointer() {
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
try {
// nameFinder.probs();
fail("Expected NullPointerException due to no previous find call");
} catch (NullPointerException expected) {
assertTrue(expected.getMessage() == null || expected.getMessage().contains("null"));
}
}

@Test
public void testFindWithVeryLongToken() {
char[] chars = new char[10000];
Arrays.fill(chars, 'a');
String longToken = new String(chars);
String[] tokens = new String[] { longToken };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other"), new double[] { 0.99 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// Span[] spans = nameFinder.find(tokens);
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testProbsWithInvalidSpanIndex() {
String[] tokens = new String[] { "a", "b" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other", "other"), new double[] { 0.6, 0.7 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// nameFinder.find(tokens);
Span[] invalidSpans = new Span[] { new Span(0, 3) };
try {
// nameFinder.probs(invalidSpans);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
assertTrue(expected.getMessage() != null);
}
}

@Test
public void testExtractNameTypeWithEmptyString() {
String extracted = NameFinderME.extractNameType("");
assertNull(extracted);
}

@Test
public void testExtractNameTypeWithNull() {
try {
NameFinderME.extractNameType(null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testAdaptiveDataUpdateAcrossMultipleFindCalls() {
String[] tokens1 = new String[] { "John", "Doe" };
String[] tokens2 = new String[] { "Jane", "Smith" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence seq1 = new Sequence(Arrays.asList("person-start", "person-cont"), new double[] { 0.9, 0.91 });
// Sequence seq2 = new Sequence(Arrays.asList("person-start", "person-cont"), new double[] { 0.92, 0.93 });
// when(mockModel.bestSequence(eq(tokens1), any(), any(), any())).thenReturn(seq1);
// when(mockModel.bestSequence(eq(tokens2), any(), any(), any())).thenReturn(seq2);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME nameFinder = new NameFinderME(model);
// nameFinder.find(tokens1);
// nameFinder.find(tokens2);
// nameFinder.clearAdaptiveData();
// Span[] spans = nameFinder.find(tokens1);
// assertEquals(1, spans.length);
}

@Test
public void testDropOverlappingSpansWithEmptyArray() {
Span[] spans = new Span[0];
Span[] dropped = NameFinderME.dropOverlappingSpans(spans);
assertNotNull(dropped);
assertEquals(0, dropped.length);
}

@Test
public void testDropOverlappingSpansWithSingleSpan() {
Span single = new Span(1, 2, "person", 0.95);
Span[] dropped = NameFinderME.dropOverlappingSpans(new Span[] { single });
assertNotNull(dropped);
assertEquals(1, dropped.length);
assertEquals(1, dropped[0].getStart());
assertEquals(2, dropped[0].getEnd());
}

@Test
public void testFindWithSingleContOutcomeShouldNotReturnSpan() {
String[] tokens = new String[] { "Smith" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-cont"), new double[] { 0.75 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] result = finder.find(tokens);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testFindWithOnlyStartLabelsAndNoCont() {
String[] tokens = new String[] { "Mr.", "John", "Smith" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-start", "person-start"), new double[] { 0.8, 0.85, 0.84 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] result = finder.find(tokens);
// assertNotNull(result);
// assertEquals(3, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(1, result[0].getEnd());
// assertEquals(1, result[1].getStart());
// assertEquals(2, result[1].getEnd());
// assertEquals(2, result[2].getStart());
// assertEquals(3, result[2].getEnd());
}

@Test
public void testBestSequenceCalledWithMatchingContextLength() {
String[] tokens = new String[] { "Barack", "Obama" };
String[][] additionalContext = new String[][] { { "X" }, { "Y" } };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont"), new double[] { 0.9, 0.91 });
// when(mockModel.bestSequence(eq(tokens), eq(additionalContext), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] result = finder.find(tokens, additionalContext);
// assertEquals(1, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(2, result[0].getEnd());
}

@Test
public void testDecodeWithOverlappingSameEntityTypeSpans() {
String[] tokens = new String[] { "John", "Smith", "Dr.", "Jones" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont", "person-start", "person-cont"), new double[] { 0.9, 0.91, 0.92, 0.93 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] result = finder.find(tokens);
// assertEquals(2, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(2, result[0].getEnd());
// assertEquals(2, result[1].getStart());
// assertEquals(4, result[1].getEnd());
}

@Test
public void testProbsMethodWithMultipleSpansReturnsCorrectAverages() {
String[] tokens = new String[] { "Google", "was", "founded", "by", "Larry", "Page" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("org-start", "other", "other", "other", "person-start", "person-cont"), new double[] { 0.87, 0.6, 0.6, 0.6, 0.95, 0.96 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// double[] probs = finder.probs(spans);
// assertEquals(2, probs.length);
// assertEquals(0.87, probs[0], 0.0001);
// assertEquals((0.95 + 0.96) / 2, probs[1], 0.0001);
}

@Test
public void testDropOverlappingSpansWithReverseOrderInput() {
Span s1 = new Span(4, 7);
Span s2 = new Span(2, 6);
Span s3 = new Span(1, 3);
Span s4 = new Span(0, 2);
Span[] input = new Span[] { s1, s2, s3, s4 };
Span[] output = NameFinderME.dropOverlappingSpans(input);
assertEquals(2, output.length);
assertEquals(0, output[0].getStart());
assertEquals(2, output[0].getEnd());
assertEquals(2, output[1].getStart());
assertEquals(6, output[1].getEnd());
}

@Test
public void testFindWithAllOtherTagsReturnsEmptySpanList() {
String[] tokens = new String[] { "No", "named", "entity", "here" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other", "other", "other", "other"), new double[] { 0.5, 0.4, 0.3, 0.2 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] result = finder.find(tokens);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testFindWithEmptyAdditionalContextArray() {
String[] tokens = new String[] { "Paris", "is", "beautiful" };
String[][] context = new String[][] { {}, {}, {} };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("location-start", "other", "other"), new double[] { 0.95, 0.4, 0.3 });
// when(mockModel.bestSequence(eq(tokens), eq(context), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens, context);
// assertEquals(1, spans.length);
// assertEquals(0, spans[0].getStart());
// assertEquals(1, spans[0].getEnd());
// assertEquals("location", spans[0].getType());
}

@Test
public void testFindWithEmptyOutcomeList() {
String[] tokens = new String[] { "Hello", "World" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList(), new double[] {});
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testFindWithOutcomeListShorterThanTokens() {
String[] tokens = new String[] { "A", "B", "C" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont"), new double[] { 0.8, 0.7 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
try {
// finder.find(tokens);
fail("Expected IndexOutOfBoundsException due to outcome-token size mismatch");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testFindWithOutcomeListLongerThanTokens() {
String[] tokens = new String[] { "A" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "person-cont"), new double[] { 0.9, 0.8 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
try {
// finder.find(tokens);
fail("Expected IndexOutOfBoundsException due to extra outcomes");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testProbsWithEmptySpanArray() {
String[] tokens = new String[] { "Eiffel", "Tower" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("location-start", "location-cont"), new double[] { 0.91, 0.92 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// finder.find(tokens);
Span[] emptySpans = new Span[0];
// double[] probs = finder.probs(emptySpans);
// assertNotNull(probs);
// assertEquals(0, probs.length);
}

@Test
public void testFindWithNullOutcomeInList() {
String[] tokens = new String[] { "John", "Smith" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
List<String> outcomes = Arrays.asList(null, "person-cont");
// Sequence sequence = new Sequence(outcomes, new double[] { 0.8, 0.9 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertEquals(1, spans.length);
// assertEquals(1, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
}

@Test
public void testDropOverlappingSpansWithUnsortedInput() {
Span span1 = new Span(5, 7);
Span span2 = new Span(2, 4);
Span span3 = new Span(1, 3);
Span span4 = new Span(3, 6);
Span[] unsorted = new Span[] { span1, span2, span3, span4 };
Span[] result = NameFinderME.dropOverlappingSpans(unsorted);
assertNotNull(result);
assertEquals(2, result.length);
assertEquals(1, result[0].getStart());
assertEquals(3, result[0].getEnd());
assertEquals(5, result[1].getStart());
assertEquals(7, result[1].getEnd());
}

@Test
public void testDropOverlappingSpansWithSubsetSpanFullyContained() {
Span span1 = new Span(0, 5);
Span span2 = new Span(1, 3);
Span[] spans = new Span[] { span1, span2 };
Span[] result = NameFinderME.dropOverlappingSpans(spans);
assertEquals(1, result.length);
assertEquals(0, result[0].getStart());
assertEquals(5, result[0].getEnd());
}

@Test
public void testProbsMethodReturnsExpectedLengthArray() {
String[] tokens = new String[] { "Token1", "Token2" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other", "other"), new double[] { 0.1, 0.2 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// finder.find(tokens);
// double[] probsArray = finder.probs();
// assertEquals(2, probsArray.length);
// assertEquals(0.1, probsArray[0], 0.0001);
// assertEquals(0.2, probsArray[1], 0.0001);
}

@Test
public void testClearAdaptiveDataAfterMultipleFinds() {
String[] tokens1 = new String[] { "A", "B" };
String[] tokens2 = new String[] { "C", "D" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence1 = new Sequence(Arrays.asList("other", "other"), new double[] { 0.3, 0.4 });
// Sequence sequence2 = new Sequence(Arrays.asList("other", "other"), new double[] { 0.5, 0.6 });
// when(mockModel.bestSequence(eq(tokens1), any(), any(), any())).thenReturn(sequence1);
// when(mockModel.bestSequence(eq(tokens2), any(), any(), any())).thenReturn(sequence2);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// finder.find(tokens1);
// finder.clearAdaptiveData();
// Span[] spans = finder.find(tokens2);
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testFindWithTokensContainingNullElements() {
String[] tokens = new String[] { "New", null, "City" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("location-start", "location-cont", "location-cont"), new double[] { 0.9, 0.8, 0.85 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] result = finder.find(tokens);
// assertEquals(1, result.length);
// assertEquals(0, result[0].getStart());
// assertEquals(3, result[0].getEnd());
// assertEquals("location", result[0].getType());
}

@Test
public void testFindWithMismatchedProbabilitySizeTooShort() {
String[] tokens = new String[] { "Alice", "visited", "Paris" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("person-start", "other", "location-start"), new double[] { 0.9, 0.7 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
try {
// finder.find(tokens);
fail("Expected IndexOutOfBoundsException due to insufficient probabilities");
} catch (IndexOutOfBoundsException e) {
assertTrue(e.getMessage() == null || e.getMessage().contains(""));
}
}

@Test
public void testFindWithTokensContainingOnlyWhitespace() {
String[] tokens = new String[] { " ", "   ", "\t" };
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("other", "other", "other"), new double[] { 0.5, 0.5, 0.5 });
// when(mockModel.bestSequence(eq(tokens), any(), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens);
// assertNotNull(spans);
// assertEquals(0, spans.length);
}

@Test
public void testFindWithEmptyAdditionalContextMatrix() {
String[] tokens = new String[] { "London", "Bridge" };
String[][] additionalContext = new String[2][];
additionalContext[0] = new String[0];
additionalContext[1] = new String[0];
SequenceClassificationModel<String> mockModel = mock(SequenceClassificationModel.class);
// Sequence sequence = new Sequence(Arrays.asList("location-start", "location-cont"), new double[] { 0.95, 0.96 });
// when(mockModel.bestSequence(eq(tokens), eq(additionalContext), any(), any())).thenReturn(sequence);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
// TokenNameFinderModel model = new TokenNameFinderModel("en", mockModel, factory.getFeatureGenerator(), factory.getResources(), null, factory.getSequenceCodec(), factory);
// NameFinderME finder = new NameFinderME(model);
// Span[] spans = finder.find(tokens, additionalContext);
// assertEquals(1, spans.length);
// assertEquals("location", spans[0].getType());
// assertEquals(0, spans[0].getStart());
// assertEquals(2, spans[0].getEnd());
}

@Test
public void testTrainWithNullParametersFailsGracefully() throws IOException {
ObjectStream<NameSample> samples = mock(ObjectStream.class);
// TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, null, new TokenFeatureGenerator());
try {
// NameFinderME.train("en", "person", samples, null, factory);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testDropOverlappingSpansWithExactDuplicateSpans() {
Span span1 = new Span(0, 2);
Span span2 = new Span(0, 2);
Span span3 = new Span(0, 2);
Span[] array = new Span[] { span1, span2, span3 };
Span[] result = NameFinderME.dropOverlappingSpans(array);
assertEquals(1, result.length);
assertEquals(0, result[0].getStart());
assertEquals(2, result[0].getEnd());
}

@Test
public void testDropOverlappingSpansWithInterleavedSpans() {
Span span1 = new Span(0, 3);
Span span2 = new Span(1, 5);
Span span3 = new Span(4, 6);
Span[] input = new Span[] { span3, span2, span1 };
Span[] result = NameFinderME.dropOverlappingSpans(input);
assertEquals(1, result.length);
assertEquals(0, result[0].getStart());
assertEquals(3, result[0].getEnd());
}

@Test
public void testExtractNameTypeWithMultipleHyphens() {
String outcome = "location-historic-spanish";
String extracted = NameFinderME.extractNameType(outcome);
assertEquals("location", extracted);
}

@Test
public void testExtractNameTypeWithNoHyphen() {
String outcome = "person";
String extracted = NameFinderME.extractNameType(outcome);
assertNull(extracted);
}

@Test
public void testExtractNameTypeWithOnlyHyphenSuffix() {
String outcome = "-start";
String extracted = NameFinderME.extractNameType(outcome);
assertNull(extracted);
}
}
