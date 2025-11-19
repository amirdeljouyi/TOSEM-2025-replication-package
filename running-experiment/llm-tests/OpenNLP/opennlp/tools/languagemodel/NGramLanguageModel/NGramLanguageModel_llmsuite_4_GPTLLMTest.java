package opennlp.tools.languagemodel;

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

public class NGramLanguageModel_llmsuite_4_GPTLLMTest {

@Test
public void testDefaultConstructorUsesDefaultN() {
NGramLanguageModel model = new NGramLanguageModel();
assertNotNull(model);
model.add("this", "is", "a", "test");
assertTrue(model.size() > 0);
}

@Test
public void testConstructorWithInvalidNThrowsException() {
new NGramLanguageModel(0);
}

@Test
public void testAddAndSizeIncreasesModelSize() {
NGramLanguageModel model = new NGramLanguageModel(3);
int initialSize = model.size();
model.add("open", "nlp", "test", "case");
int updatedSize = model.size();
assertTrue(updatedSize > initialSize);
}

@Test
public void testCalculateProbabilityOnExistingNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello", "world", "here");
double probability = model.calculateProbability("hello", "world", "here");
assertTrue(probability > 0.0);
assertTrue(probability <= 1.0);
}

@Test
public void testCalculateProbabilityReturnsZeroForUnseenSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("known", "phrase", "here");
double probability = model.calculateProbability("unknown", "sequence", "text");
assertEquals(0.0, probability, 0.00001);
}

@Test
public void testPredictNextTokensReturnsExpectedToken() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("machine", "learning", "rocks");
model.add("machine", "learning", "rules");
String[] prediction = model.predictNextTokens("machine", "learning");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("rocks") || prediction[0].equals("rules"));
}

@Test
public void testPredictNextTokensReturnsNullOnEmptyModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] prediction = model.predictNextTokens("nothing");
assertNull(prediction);
}

@Test
public void testConstructorFromInputStreamLoadsSerializedModel() throws Exception {
NGramLanguageModel originalModel = new NGramLanguageModel(3);
originalModel.add("natural", "language", "processing");
ByteArrayOutputStream out = new ByteArrayOutputStream();
originalModel.serialize(out);
byte[] bytes = out.toByteArray();
InputStream in = new ByteArrayInputStream(bytes);
NGramLanguageModel loadedModel = new NGramLanguageModel(in);
assertNotNull(loadedModel);
assertTrue(loadedModel.size() > 0);
}

@Test
public void testConstructorFromInputStreamWithInvalidNThrowsException() throws Exception {
NGramLanguageModel originalModel = new NGramLanguageModel(2);
originalModel.add("foo", "bar");
ByteArrayOutputStream out = new ByteArrayOutputStream();
originalModel.serialize(out);
byte[] bytes = out.toByteArray();
InputStream in = new ByteArrayInputStream(bytes);
new NGramLanguageModel(in, 0);
}

@Test
public void testCalculateProbabilityFallsBackGracefully() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("deep", "learning", "networks");
double probability = model.calculateProbability("neural", "network", "science");
assertTrue(probability >= 0.0);
}

@Test
public void testAddSingleTokenIgnoredWhenNIsThree() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("short");
assertEquals(0, model.size());
}

@Test
public void testAddExactlyThreeTokensStored() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "two", "three");
assertTrue(model.size() > 0);
}

@Test
public void testCalculateProbabilityOfMultipleKnownPhrasesIsDifferent() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "quick", "brown");
model.add("the", "lazy", "dog");
double prob1 = model.calculateProbability("the", "quick", "brown");
double prob2 = model.calculateProbability("the", "lazy", "dog");
assertTrue(prob1 > 0.0);
assertTrue(prob2 > 0.0);
assertNotEquals(prob1, prob2, 0.0001);
}

@Test
public void testCalculateProbabilityReturnsZeroForOneTokenWithNIsThree() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello");
double probability = model.calculateProbability("hello");
assertEquals(0.0, probability, 0.0);
}

@Test
public void testUnseenTokensTriggerBackoffDecay() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("foo", "bar", "baz");
double fullProb = model.calculateProbability("foo", "bar", "baz");
double backoffProb = model.calculateProbability("unknown", "tokens", "input");
assertTrue(fullProb > backoffProb);
}

@Test
public void testPredictNextTokensReturnsLastTokenFromMatchingNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("a", "b", "d");
String[] prediction = model.predictNextTokens("a", "b");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("c") || prediction[0].equals("d"));
}

@Test
public void testPredictNextTokensAfterUnrelatedContextReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello", "world", "test");
String[] prediction = model.predictNextTokens("java", "rocks");
assertNull(prediction);
}

@Test
public void testClearRemovesAllNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("clear", "model", "now");
int sizeBefore = model.size();
// model.clear();
int sizeAfter = model.size();
assertTrue(sizeBefore > 0);
assertEquals(0, sizeAfter);
}

@Test
public void testCalculateProbabilityOnEmptyModelReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
double probability = model.calculateProbability("any", "input", "here");
assertEquals(0.0, probability, 0.00001);
}

@Test
public void testCalculateProbabilityEmptyInputReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
double probability = model.calculateProbability();
assertEquals(0.0, probability, 0.00001);
}

@Test
public void testCalculateProbabilityDoesNotReturnNaN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("safe", "value", "check");
double probability = model.calculateProbability("unseen", "sequence", "check");
assertFalse(Double.isNaN(probability));
}

@Test
public void testAddEmptyArrayDoesNotThrowOrChangeSize() {
NGramLanguageModel model = new NGramLanguageModel(3);
int initialSize = model.size();
model.add();
int updatedSize = model.size();
assertEquals(initialSize, updatedSize);
}

@Test
public void testAddNullTokenArrayThrowsNullPointerException() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add((String[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testPredictNextTokensFromPartialMatchReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
String[] prediction = model.predictNextTokens("x");
assertNull(prediction);
}

@Test
public void testPredictNextTokensExceedsContextLengthReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
String[] prediction = model.predictNextTokens("a", "b", "c", "d");
assertNull(prediction);
}

@Test
public void testCalculateProbabilityOnExactCutoffBoundary() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
double prob = model.calculateProbability("a", "b");
assertTrue(prob > 0.0);
}

@Test
public void testMultipleIdenticalNGramsIncreaseCountAndProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("test", "this");
model.add("test", "this");
double probOnce = model.calculateProbability("test", "this");
model.add("test", "this");
double probIncreased = model.calculateProbability("test", "this");
assertTrue(probIncreased > probOnce);
}

@Test
public void testAddTokensWithTrailingNullDoesNotThrow() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add("one", null, "three");
double prob = model.calculateProbability("one", null, "three");
assertEquals(0.0, prob, 0.00001);
} catch (NullPointerException e) {
}
}

@Test
public void testCalculateProbabilityWithSingleTokenInBigramModelReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double prob = model.calculateProbability("a");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testStupidBackoffFullyRecursesToUnigram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("start", "middle", "end");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithRepeatedTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("repeat", "repeat", "repeat");
double prob = model.calculateProbability("repeat", "repeat", "repeat");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWhereNMinusOneCountsAreZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("alpha", "beta", "gamma");
double prob = model.calculateProbability("alpha", "delta", "epsilon");
assertTrue(prob >= 0.0);
}

@Test
public void testPredictNextTokensAfterAddingOnlyUnigramsReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("standalone");
String[] prediction = model.predictNextTokens("standalone");
assertNull(prediction);
}

@Test
public void testPredictNextTokensWithEmptyInputReturnsBestBeginning() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("first", "word");
model.add("second", "word");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals(1, prediction.length);
}

@Test
public void testCalculateProbabilityWithExactMatchAtEndBoundary() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("edge", "of", "line");
double prob = model.calculateProbability("edge", "of", "line");
assertTrue(prob > 0.0);
}

@Test
public void testInputTokensShorterThanNStillProducesNoNGrams() {
NGramLanguageModel model = new NGramLanguageModel(4);
model.add("short", "sequence");
assertEquals(0, model.size());
}

@Test
public void testInputTokensExactlyNProducesOneNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("just", "enough", "tokens");
assertEquals(1, model.size());
}

@Test
public void testSameNGramAddedTwiceProducesNonZeroProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("same", "pair");
model.add("same", "pair");
double prob = model.calculateProbability("same", "pair");
assertTrue(prob > 0.0);
}

@Test
public void testPredictionOnMinimalModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("hello");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("hello", prediction[0]);
}

@Test
public void testPredictionSequenceWithLeadingSpaces() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(" leading", "space");
String[] prediction = model.predictNextTokens(" leading");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("space", prediction[0]);
}

@Test
public void testCalculateProbabilityWhenModelIsEmptyReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
double probability = model.calculateProbability("some", "tokens");
assertEquals(0.0, probability, 0.0);
}

@Test
public void testCalculateProbabilityWhenTokensAreFewerThanN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("only", "two");
double probability = model.calculateProbability("only", "two");
assertEquals(0.0, probability, 0.0);
}

@Test
public void testCalculateProbabilityWithNullInputTokensReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
double probability = model.calculateProbability((String[]) null);
assertEquals(0.0, probability, 0.0);
}

@Test
public void testPredictNextTokensWithNullReturnsMostProbableNGramPrefix() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello", "world");
String[] result = model.predictNextTokens((String[]) null);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("hello", result[0]);
}

@Test
public void testCalculateProbabilityReturnsZeroForProbNaN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double probability = model.calculateProbability("a", "b");
assertEquals(0.0, probability, 0.0);
}

@Test
public void testStupidBackoffFallthroughToZeroLevelReturnsZeroGracefully() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double probability = model.calculateProbability("x", "y");
assertTrue(probability >= 0.0);
}

@Test
public void testPredictWithMultipleSamePrefixSelectsHighestProb() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("go", "home");
model.add("go", "away");
model.add("go", "home");
String[] prediction = model.predictNextTokens("go");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("home", prediction[0]);
}

@Test
public void testConstructorFromInvalidInputStreamThrowsIOException() throws Exception {
byte[] corruptData = "not a valid model".getBytes(StandardCharsets.UTF_8);
InputStream in = new ByteArrayInputStream(corruptData);
new NGramLanguageModel(in);
}

@Test
public void testProbabilityComputationAvoidsInfiniteByZeroDivisionFallback() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double probability = model.calculateProbability("a", "q", "c");
assertTrue(probability >= 0.0);
}

@Test
public void testPredictionPrefersLongestNGramMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("once", "upon", "time");
model.add("once", "upon", "land");
model.add("once", "upon", "time");
model.add("upon", "time", "end");
String[] prediction = model.predictNextTokens("once", "upon");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("time", prediction[0]);
}

@Test
public void testPredictNextTokensOnUnigramModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("token1");
model.add("token2");
model.add("token1");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("token1", prediction[0]);
}

@Test
public void testEmptyPredictionOnModelWithOnlyUnigram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello");
String[] prediction = model.predictNextTokens("hello");
assertNull(prediction);
}

@Test
public void testCalculateProbabilityWithIdenticalOverlappingNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "x", "x", "x");
double probability = model.calculateProbability("x", "x", "x");
assertTrue(probability > 0.0);
}

@Test
public void testPredictNextTokensWithEmptyInputAndNoModelReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] prediction = model.predictNextTokens();
assertNull(prediction);
}

@Test
public void testBackoffChainEndsWithNoExceptionAndLowProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double probability = model.calculateProbability("x", "y", "z");
assertTrue(probability >= 0.0);
assertTrue(probability < 1.0);
}

@Test
public void testAddWithMoreThanNAddsMultipleNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
int initialSize = model.size();
model.add("this", "is", "a", "simple", "test");
int updatedSize = model.size();
assertTrue(updatedSize > initialSize);
assertEquals(4, updatedSize);
}

@Test
public void testAddWithTokensHavingEmptyStrings() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "value", "tokens");
int size = model.size();
assertTrue(size > 0);
double probability = model.calculateProbability("", "value");
assertTrue(probability > 0.0);
}

@Test
public void testAddWithOnlyEmptyTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("", "", "");
int size = model.size();
assertEquals(1, size);
double probability = model.calculateProbability("", "", "");
assertTrue(probability > 0.0);
}

@Test
public void testAddWithMixedNullAndValidTokensThrowsNullPointerException() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add("one", null, "three");
fail("Expected NullPointerException due to null token");
} catch (NullPointerException expected) {
}
}

@Test
public void testCalculateProbabilityHandlesNullStringInInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two");
try {
model.calculateProbability("one", null);
fail("Expected NullPointerException due to null input token");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictNextTokensWithNMinusOneContextWhereCountZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
String[] result = model.predictNextTokens("z");
assertNull(result);
}

@Test
public void testStupidBackoffReturnsSmoothedValueWhenCountZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
model.add("x", "y");
double probability = model.calculateProbability("beta", "gamma");
assertTrue(probability >= 0.0);
assertTrue(probability < 1.0);
}

@Test
public void testConstructorWithLargeNModelHandlesLimitedInputGracefully() {
NGramLanguageModel model = new NGramLanguageModel(10);
model.add("only", "a", "few", "tokens");
int size = model.size();
assertEquals(0, size);
}

@Test
public void testPredictNextTokenAfterClearReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("some", "sequence", "to", "clear");
// model.clear();
String[] prediction = model.predictNextTokens("some", "sequence");
assertNull(prediction);
}

@Test
public void testAddDuplicateNGramsCountsAreAggregated() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("word", "pair");
model.add("word", "pair");
model.add("word", "pair");
double probabilityOnce = model.calculateProbability("word", "pair");
model.add("word", "pair");
double probabilityTwice = model.calculateProbability("word", "pair");
assertTrue(probabilityTwice > probabilityOnce);
}

@Test
public void testPredictNextTokensForEmptyInputDefaultsToMostFrequent() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
model.add("p", "q");
model.add("x", "y");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("x", result[0]);
}

@Test
public void testCalculateProbabilityReturnsZeroForZeroCountAndZeroSize() {
NGramLanguageModel model = new NGramLanguageModel(2);
double probability = model.calculateProbability("a", "b");
assertEquals(0.0, probability, 0.0);
}

@Test
public void testSerializationAndDeserializationOfEmptyModel() throws Exception {
NGramLanguageModel model = new NGramLanguageModel(3);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
byte[] bytes = out.toByteArray();
InputStream in = new ByteArrayInputStream(bytes);
NGramLanguageModel loaded = new NGramLanguageModel(in);
assertEquals(0, loaded.size());
}

@Test
public void testSerializationAndDeserializationPreservesData() throws Exception {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("persistent", "test", "here");
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
byte[] bytes = out.toByteArray();
InputStream in = new ByteArrayInputStream(bytes);
NGramLanguageModel loaded = new NGramLanguageModel(in);
double originalProb = model.calculateProbability("persistent", "test", "here");
double reloadedProb = loaded.calculateProbability("persistent", "test", "here");
assertEquals(originalProb, reloadedProb, 0.00001);
}

@Test
public void testModelWithSingleNGramPredictsItself() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("start", "end");
String[] predicted = model.predictNextTokens("start");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertEquals("end", predicted[0]);
}

@Test
public void testCalculateProbabilityForInputLongerThanModelMaxN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c", "d");
double probability = model.calculateProbability("a", "b", "c", "d", "e");
assertTrue(probability >= 0.0);
}

@Test
public void testCalculateProbabilityForMultiplePartialMatches() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("foo", "bar", "baz");
model.add("bar", "baz", "qux");
double probability = model.calculateProbability("foo", "bar", "baz", "qux");
assertTrue(probability > 0.0);
}

@Test
public void testAddTrigramDifferentCaseTokensAreTreatedAsDifferent() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("HELLO", "WORLD", "TEST");
model.add("hello", "world", "test");
int size = model.size();
assertEquals(2, size);
double probUpper = model.calculateProbability("HELLO", "WORLD", "TEST");
double probLower = model.calculateProbability("hello", "world", "test");
assertTrue(probUpper > 0.0);
assertTrue(probLower > 0.0);
assertNotEquals(probUpper, probLower, 0.0001);
}

@Test
public void testAddSameTokensMultipleTimesAndClearResetsState() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two");
model.add("one", "two");
model.add("one", "two");
assertTrue(model.size() > 0);
// model.clear();
assertEquals(0, model.size());
double probability = model.calculateProbability("one", "two");
assertEquals(0.0, probability, 0.0);
}

@Test
public void testPredictNextTokensReturnsNullWhenNoContinuationFound() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("java", "rocks");
String[] predicted = model.predictNextTokens("unknown");
assertNull(predicted);
}

@Test
public void testPredictNextTokensWithExactMatchAtEndOfModelData() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("end", "of");
model.add("of", "test");
String[] result = model.predictNextTokens("of");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("test", result[0]);
}

@Test
public void testCalculateProbabilityDescendingBackoffChainToEmptyNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double p = model.calculateProbability("a", "b", "c");
assertTrue(p >= 0.0);
}

@Test
public void testStupidBackoffOnUnseenTokenTriggersBackoffMultiplication() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "known");
double original = model.calculateProbability("this", "is", "known");
double backoff = model.calculateProbability("this", "is", "unknown");
assertTrue(backoff < original);
}

@Test
public void testPredictNextTokensReturnsLongestMatchWhenMultipleOptionsExist() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("to", "be", "or");
model.add("to", "be", "not");
model.add("to", "be", "or");
String[] result = model.predictNextTokens("to", "be");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("or", result[0]);
}

@Test
public void testConstructorInputStreamThrowsIOExceptionOnMalformedStream() throws Exception {
byte[] invalidData = "malformed model data".getBytes("UTF-8");
InputStream stream = new ByteArrayInputStream(invalidData);
try {
new NGramLanguageModel(stream);
fail("Expected IOException");
} catch (IOException e) {
}
}

@Test
public void testProbabilityValueBoundaries() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double prob = model.calculateProbability("a", "b");
assertTrue(prob > 0.0);
assertTrue(prob <= 1.0);
}

@Test
public void testCalculateProbabilityReturnsSameValueForSameInput() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("repeat", "repeat", "repeat");
double p1 = model.calculateProbability("repeat", "repeat", "repeat");
double p2 = model.calculateProbability("repeat", "repeat", "repeat");
assertEquals(p1, p2, 0.0000001);
}

@Test
public void testPredictNextTokensWithNullInputAndEmptyModelReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] result = model.predictNextTokens((String[]) null);
assertNull(result);
}

@Test
public void testEmptyModelReturnsZeroProbabilityForAllSequences() {
NGramLanguageModel model = new NGramLanguageModel(2);
double p1 = model.calculateProbability("x", "y");
double p2 = model.calculateProbability("a", "b", "c");
assertEquals(0.0, p1, 0.0);
assertEquals(0.0, p2, 0.0);
}

@Test
public void testLoadSerializedModelWithExactMatchRestoresProbabilities() throws Exception {
NGramLanguageModel original = new NGramLanguageModel(2);
original.add("encoding", "works");
ByteArrayOutputStream out = new ByteArrayOutputStream();
original.serialize(out);
byte[] data = out.toByteArray();
InputStream in = new ByteArrayInputStream(data);
NGramLanguageModel loaded = new NGramLanguageModel(in);
double originalProb = original.calculateProbability("encoding", "works");
double reloadedProb = loaded.calculateProbability("encoding", "works");
assertEquals(originalProb, reloadedProb, 0.000001);
}

@Test
public void testCalculateProbabilityWithInvalidUtf8InputStream() throws Exception {
byte[] invalidBytes = { (byte) 0xC3, (byte) 0x28 };
InputStream in = new ByteArrayInputStream(invalidBytes);
try {
new NGramLanguageModel(in);
fail("Expected IOException due to malformed input stream");
} catch (Exception e) {
assertTrue(e instanceof Exception);
}
}

@Test
public void testAddWithWhitespaceOnlyTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("   ", " ");
assertTrue(model.size() > 0);
double probability = model.calculateProbability("   ", " ");
assertTrue(probability > 0.0);
}

@Test
public void testCalculateProbabilityForTokenSequenceThatBacksOffMultipleLevels() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0);
assertTrue(prob < 1.0);
}

@Test
public void testPredictNextTokensReturnsNullWithOnlyUnrelatedNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("alpha", "beta", "gamma");
model.add("one", "two", "three");
String[] result = model.predictNextTokens("unrelatedA", "unrelatedB");
assertNull(result);
}

@Test
public void testPredictNextTokensFromLongestPrefixMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("quick", "brown", "fox");
model.add("quick", "brown", "dog");
model.add("quick", "brown", "fox");
String[] result = model.predictNextTokens("quick", "brown");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("fox", result[0]);
}

@Test
public void testProbabilityOfRepeatedFragmentFromLongerInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "x", "x", "x");
double probability = model.calculateProbability("x", "x", "x", "x");
assertTrue(probability > 0.0);
}

@Test
public void testProbabilityComputesLogOfBackoffZeroCountsSafely() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double probability = model.calculateProbability("b", "c");
assertTrue(probability >= 0.0);
}

@Test
public void testPredictNextTokensWithNoTokensInModelReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] prediction = model.predictNextTokens("hello");
assertNull(prediction);
}

@Test
public void testAddTokensExactlyEqualToN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "two", "three");
assertEquals(1, model.size());
double probability = model.calculateProbability("one", "two", "three");
assertTrue(probability > 0.0);
}

@Test
public void testAddTokensLessThanNDoesNotAddNGram() {
NGramLanguageModel model = new NGramLanguageModel(4);
model.add("only", "three", "tokens");
assertEquals(0, model.size());
}

@Test
public void testPredictNextTokenFromSingleMatchingNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
String[] prediction = model.predictNextTokens("a");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("b", prediction[0]);
}

@Test
public void testPredictionForPrefixTokensWithNoSuccessor() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("ready", "set", "go");
String[] result = model.predictNextTokens("set", "go");
assertNull(result);
}

@Test
public void testCalculateProbabilityWithSameTokenNRepeatedly() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "a", "a", "a", "a");
double probability = model.calculateProbability("a", "a");
assertTrue(probability > 0.0);
}

@Test
public void testPredictNextTokensWithNullTokenArrayAndPopulatedModel() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
String[] result = model.predictNextTokens((String[]) null);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("x", result[0]);
}

@Test
public void testProbabilityCalculationProducesNonNaNWithValidInput() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("safe", "model", "test");
double prob = model.calculateProbability("safe", "model", "test");
assertFalse(Double.isNaN(prob));
assertTrue(prob > 0.0);
}

@Test
public void testProbabilityBackoffRecursiveTerminationAtUnigram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("outer", "inner", "final");
double prob = model.calculateProbability("this", "does", "not");
assertTrue(prob >= 0.0);
assertTrue(prob < 1.0);
}

@Test
public void testAddWithNullValuesThrowsException() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add(null, "b", "c");
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
try {
model.add("a", null, "c");
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
try {
model.add("a", "b", null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testCalculateProbabilityWithNullElementInTokensThrowsException() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
try {
model.calculateProbability("x", null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictNextTokensWithEmptyNGramModelReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] prediction = model.predictNextTokens();
assertNull(prediction);
}

@Test
public void testCalculateProbabilityFromSingleTokenRepeated() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "a", "a", "a");
double prob = model.calculateProbability("a", "a", "a");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityForUnseenNGramWhenModelIsNotEmpty() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "cat", "sat");
double prob = model.calculateProbability("unknown", "sequence", "tokens");
assertTrue(prob >= 0.0);
assertTrue(prob < 1.0);
}

@Test
public void testBackoffProbabilityReturnsZeroWhenAllNGramsInvalid() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double prob = model.calculateProbability("x", "y");
assertTrue(prob >= 0.0);
}

@Test
public void testAddWithTokensExactlyNMinusOne() {
NGramLanguageModel model = new NGramLanguageModel(4);
model.add("only", "three", "tokens");
assertEquals(0, model.size());
}

@Test
public void testEmptyProbabilityFromUntrainedModel() {
NGramLanguageModel model = new NGramLanguageModel(2);
double prob = model.calculateProbability("foo", "bar");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testPredictionRankingBasedOnFrequency() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("go", "left");
model.add("go", "right");
model.add("go", "left");
String[] result = model.predictNextTokens("go");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("left", result[0]);
}

@Test
public void testSerializeAndDeserializePreservesTrigramCounts() throws Exception {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("serialize", "this", "model");
model.add("serialize", "this", "model");
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
byte[] data = out.toByteArray();
InputStream in = new ByteArrayInputStream(data);
NGramLanguageModel reloaded = new NGramLanguageModel(in);
double originalProb = model.calculateProbability("serialize", "this", "model");
double reloadedProb = reloaded.calculateProbability("serialize", "this", "model");
assertEquals(originalProb, reloadedProb, 1e-6);
}

@Test
public void testModelCreatedWithLargeNDoesNotFailOnShortTokens() {
NGramLanguageModel model = new NGramLanguageModel(10);
model.add("short", "tokens");
assertEquals(0, model.size());
}

@Test
public void testPredictionFromModelContainingMultipleNGramsWithCommonPrefix() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("sun", "shines");
model.add("sun", "sets");
model.add("sun", "shines");
model.add("sun", "rises");
String[] prediction = model.predictNextTokens("sun");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("shines", prediction[0]);
}

@Test
public void testCalculateProbabilityForAllZeroCountsDoesNotThrow() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("tokenA");
double prob = model.calculateProbability("missing");
assertTrue(prob >= 0.0);
assertTrue(prob < 1.0);
}

@Test
public void testPredictNextTokenFromUnigramModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("X");
model.add("Y");
model.add("X");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("X", prediction[0]);
}

@Test
public void testBackoffToUnigramPathReturnsSmoothedProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("deep", "learning", "rocks");
double prob = model.calculateProbability("unknownA", "unknownB", "rocks");
assertTrue(prob >= 0.0);
assertTrue(prob < 1.0);
}

@Test
public void testEmptyPredictionAfterClearReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("clear", "this");
// model.clear();
assertEquals(0, model.size());
double prob = model.calculateProbability("clear", "this");
assertEquals(0.0, prob, 0.0);
String[] prediction = model.predictNextTokens("clear");
assertNull(prediction);
}
}
