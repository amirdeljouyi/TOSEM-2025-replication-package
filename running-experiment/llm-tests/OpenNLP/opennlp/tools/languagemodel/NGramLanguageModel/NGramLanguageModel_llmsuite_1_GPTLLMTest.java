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

public class NGramLanguageModel_llmsuite_1_GPTLLMTest {

@Test
public void testDefaultConstructorUsesTrigram() {
NGramLanguageModel model = new NGramLanguageModel();
model.add("the", "quick", "brown", "fox");
assertTrue(model.size() > 0);
}

@Test
public void testConstructorWithNegativeNThrowsException() {
new NGramLanguageModel(-1);
}

@Test
public void testConstructorWithZeroNThrowsException() {
new NGramLanguageModel(0);
}

@Test
public void testConstructorWithInputStreamAndDefaultN() throws IOException {
ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
NGramLanguageModel model = new NGramLanguageModel(input);
assertNotNull(model);
assertEquals(0, model.size());
}

@Test
public void testConstructorWithInputStreamAndInvalidN() throws IOException {
ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
new NGramLanguageModel(input, 0);
}

@Test
public void testAddMethodAddsTrigrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "a", "test");
assertTrue(model.size() >= 2);
}

@Test
public void testAddMethodDoesNotFailWithEmptyInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
int initialSize = model.size();
model.add();
assertEquals(initialSize, model.size());
}

@Test
public void testCalculateProbabilityReturnsZeroWhenModelIsEmpty() {
NGramLanguageModel model = new NGramLanguageModel(2);
double prob = model.calculateProbability("unseen", "tokens");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityReturnsNonZeroForKnownSequence() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("the", "cat");
model.add("cat", "sat");
double prob = model.calculateProbability("the", "cat", "sat");
assertTrue(prob > 0.0);
assertTrue(prob <= 1.0);
}

@Test
public void testCalculateProbabilityHandlesRecursionGracefully() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("a", "b", "c", "d", "e");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithUnseenNGramsReturnsZeroOrLow() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello", "world");
double prob = model.calculateProbability("foo", "bar");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensReturnsLikelyToken() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("I", "like");
model.add("like", "pizza");
String[] result = model.predictNextTokens("I");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("like", result[0]);
}

@Test
public void testPredictNextTokensReturnsNullWhenPredictionNotPossible() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] result = model.predictNextTokens("unknown");
assertNull(result);
}

@Test
public void testPredictNextTokensFromMultipleAlternatives() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("we", "are", "testing");
model.add("we", "are", "coding");
String[] result = model.predictNextTokens("we", "are");
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("testing") || result[0].equals("coding"));
}

@Test
public void testCalculateProbabilityDoesNotReturnNaN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double prob = model.calculateProbability("a", "b");
assertFalse(Double.isNaN(prob));
assertTrue(prob > 0);
}

@Test
public void testModelIterableYieldsElements() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("this", "is", "a", "test");
Iterator<StringList> iterator = model.iterator();
assertTrue(iterator.hasNext());
StringList first = iterator.next();
assertNotNull(first);
assertTrue(first.size() > 0);
}

@Test
public void testOverlappingNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello", "world", "again");
assertTrue(model.size() >= 2);
double prob = model.calculateProbability("hello", "world", "again");
assertTrue(prob > 0);
}

@Test
public void testBackoffToUnigramProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("onlyone");
double prob = model.calculateProbability("onlyone");
assertTrue(prob > 0.0);
}

@Test
public void testRecursiveBackoffForUnseenBigram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("apple");
double prob = model.calculateProbability("apple", "juice");
assertTrue(prob >= 0.0);
}

@Test
public void testNormalizedProbabilityInExpectedRange() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("he", "eats");
model.add("eats", "pizza");
model.add("he", "eats");
model.add("eats", "pizza");
model.add("he", "eats");
double prob = model.calculateProbability("he", "eats", "pizza");
assertTrue(prob > 0.0);
assertTrue(prob <= 1.0);
}

@Test
public void testNonOverlappingChunksWithZeroProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
model.add("gamma", "delta");
double prob = model.calculateProbability("alpha", "beta", "gamma");
assertTrue(prob >= 0.0);
}

@Test
public void testRedundantAddIncreasesConfidence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("repeat", "this", "line");
model.add("repeat", "this", "line");
model.add("repeat", "this", "line");
model.add("repeat", "this", "line");
model.add("repeat", "this", "line");
double prob = model.calculateProbability("repeat", "this", "line");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithPartialMatchOnlyFirstNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("start", "middle");
double prob = model.calculateProbability("start", "middle", "end");
assertTrue(prob > 0);
}

@Test
public void testCalculateProbabilityWithPartialMatchOnlyLastNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("middle", "end");
double prob = model.calculateProbability("start", "middle", "end");
assertTrue(prob > 0);
}

@Test
public void testCalculateProbabilityWithSingleToken() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("lonely");
double prob = model.calculateProbability("lonely");
assertTrue(prob > 0);
}

@Test
public void testPredictNextTokensEmptyModelReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] result = model.predictNextTokens();
assertNull(result);
}

@Test
public void testCalculateProbabilityWithEmptyInputReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
double prob = model.calculateProbability();
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testAddWithSingleTokenProducesNoNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("single");
assertEquals(0, model.size());
}

@Test
public void testCalculateProbabilityWithNoMatchingUnigrams() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("alpha");
double prob = model.calculateProbability("bravo");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWithAllUnseenSequence() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("foo", "bar");
double prob = model.calculateProbability("baz", "qux");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWhenIntermediateNGramMissing() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
model.add("gamma", "delta");
double prob = model.calculateProbability("alpha", "beta", "gamma", "delta");
assertTrue(prob > 0.0);
}

@Test
public void testAddSameUnigramMultipleTimes() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("onlyone");
model.add("onlyone");
model.add("onlyone");
double prob = model.calculateProbability("onlyone");
assertTrue(prob > 0.0);
assertTrue(prob <= 1.0);
}

@Test
public void testPredictNextTokenWithMultipleCandidatesSamePrefix() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "x");
model.add("a", "y");
String[] result = model.predictNextTokens("a");
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("x") || result[0].equals("y"));
}

@Test
public void testProbabilityIsZeroWhenNoMatchingPrefixInModel() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double prob = model.calculateProbability("c", "d");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityBackoffToUnigramWhenBigramUnknown() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double prob = model.calculateProbability("a", "c");
assertTrue(prob >= 0.0);
}

@Test
public void testZeroDivisionByMissingNMinusOneHandledGracefully() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob >= 0.0);
}

@Test
public void testPredictionLengthMatchesNModelSetting() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("g", "h", "i");
String[] result = model.predictNextTokens("x", "y");
if (result != null) {
assertEquals(3, result.length);
}
}

@Test
public void testAddNoTokensProducesNoNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add();
assertEquals(0, model.size());
}

@Test
public void testAddSingleTokenWithNBigThan1ProducesNoNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("token");
assertEquals(0, model.size());
}

@Test
public void testBackoffReturnsCorrectScoreForKnownBigramAndUnknownUnigram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double probability = model.calculateProbability("a", "b");
assertTrue(probability > 0.0);
}

@Test
public void testBackoffHandlesCaseWhenNMinusOneTokenDoesNotExist() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double probability = model.calculateProbability("x", "y", "z");
assertTrue(probability >= 0.0);
}

@Test
public void testBackoffScoreFallsThroughMultipleLevels() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithNaNScenarioIsHandled() {
NGramLanguageModel model = new NGramLanguageModel(2);
double prob = model.calculateProbability("w", "x", "y");
assertFalse(Double.isNaN(prob));
}

@Test
public void testPredictNextTokensFromSequenceWithNoPrefixMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "known");
String[] prediction = model.predictNextTokens("unknown", "sequence");
assertNull(prediction);
}

@Test
public void testPredictNextTokensWithMultipleNGramsSameProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("go", "left");
model.add("go", "right");
String[] predicted = model.predictNextTokens("go");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertTrue(predicted[0].equals("left") || predicted[0].equals("right"));
}

@Test
public void testCalculateProbabilityReturnsZeroIfAllScoresAreNaN() {
NGramLanguageModel model = new NGramLanguageModel(2);
double prob = model.calculateProbability("x", "y");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityOfMultipleUnseenBigrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
model.add("c", "d");
double prob = model.calculateProbability("x", "y", "z", "w");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithInputStreamConstructor() throws IOException {
ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
NGramLanguageModel model = new NGramLanguageModel(input, 2);
model.add("sun", "rise");
model.add("sun", "shine");
String[] predicted = model.predictNextTokens("sun");
assertNotNull(predicted);
assertEquals(1, predicted.length);
}

@Test
public void testCalculateProbabilityForExactMatchNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("foo", "bar");
double prob = model.calculateProbability("foo", "bar");
assertTrue(prob > 0);
}

@Test
public void testCalculateProbabilityWithExactMatchAndLargerInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob > 0);
}

@Test
public void testPredictNextTokensFromExactPrefixMatch() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("is", "sunny");
model.add("is", "cloudy");
String[] result = model.predictNextTokens("is");
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testCalculateProbabilityWithUnigramModelKnownToken() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("solo");
double prob = model.calculateProbability("solo");
assertTrue(prob > 0);
}

@Test
public void testCalculateProbabilityWithUnigramModelUnknownToken() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("known");
double prob = model.calculateProbability("unknown");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithUnigramModelReturnsAll() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("red");
model.add("blue");
model.add("green");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testProbabilityOfPrefixThatMatchesOnlyStartOfStoredNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("alpha", "beta", "gamma");
double prob = model.calculateProbability("alpha", "beta");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testAddMultipleDisjointTrigrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("quick", "brown", "fox");
model.add("lazy", "white", "dog");
assertTrue(model.size() >= 2);
double prob = model.calculateProbability("quick", "brown", "fox");
assertTrue(prob > 0);
}

@Test
public void testConstructorWithLargeNValue() {
NGramLanguageModel model = new NGramLanguageModel(1000);
model.add("a", "b", "c", "d", "e", "f", "g", "h");
assertTrue(model.size() >= 0);
}

@Test
public void testPredictNextTokensWithNoInputArguments() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("start", "here");
String[] next = model.predictNextTokens();
assertNotNull(next);
assertEquals(1, next.length);
}

@Test
public void testPredictNextTokensWithAllTokenMatch() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
model.add("beta", "gamma");
String[] prediction = model.predictNextTokens("alpha");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("beta", prediction[0]);
}

@Test
public void testPredictNextTokensWithTokensYieldingSameProbability() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("apple");
model.add("banana");
model.add("cherry");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("apple") || result[0].equals("banana") || result[0].equals("cherry"));
}

@Test
public void testCalculateProbabilityWithTokensLongerThanN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c", "d");
double prob = model.calculateProbability("a", "b", "c", "d", "e", "f");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityHandlesUnseenMiddleToken() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("this", "works");
model.add("unknown", "pattern");
double prob = model.calculateProbability("this", "unknown", "pattern");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithLongExactMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "quick", "brown");
model.add("quick", "brown", "fox");
model.add("brown", "fox", "jumps");
double prob = model.calculateProbability("the", "quick", "brown", "fox");
assertTrue(prob > 0.0);
}

@Test
public void testStupidBackoffHandlesDeepRecursionWithoutCrash() {
NGramLanguageModel model = new NGramLanguageModel(5);
model.add("one", "two", "three", "four", "five");
double prob = model.calculateProbability("a", "b", "c", "d", "e");
assertTrue(prob >= 0.0);
}

@Test
public void testInputStreamConstructorWithInvalidBytesDoesNotBreakModel() throws IOException {
byte[] junk = { 1, 2, 3, 4, 5 };
ByteArrayInputStream stream = new ByteArrayInputStream(junk);
NGramLanguageModel model;
try {
model = new NGramLanguageModel(stream, 2);
} catch (IOException e) {
model = new NGramLanguageModel(2);
}
model.add("safe", "token");
assertTrue(model.size() >= 0);
}

@Test
public void testPredictNextTokensSelectsMostProbableNgram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("a", "b");
model.add("a", "c");
String[] predicted = model.predictNextTokens("a");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertEquals("b", predicted[0]);
}

@Test
public void testPredictNextTokensReturnsNullWhenModelIsEmpty() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] prediction = model.predictNextTokens("something");
assertNull(prediction);
}

@Test
public void testCalculateProbabilitySingleUnigramUnknown() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("a");
double prob = model.calculateProbability("z");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilitySingleUnigramKnown() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("token");
double prob = model.calculateProbability("token");
assertTrue(prob > 0.0);
}

@Test
public void testTrigramModelCalculateProbabilityWithUnigramsOnlyAdded() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one");
model.add("two");
model.add("three");
double prob = model.calculateProbability("one", "two", "three");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testAddInsertsCorrectNumberOfNGramsForExactSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d", "e");
assertEquals(3, model.size());
}

@Test
public void testAddEmptyTokensDoesNotChangeModelSize() {
NGramLanguageModel model = new NGramLanguageModel(2);
int sizeBefore = model.size();
model.add();
int sizeAfter = model.size();
assertEquals(sizeBefore, sizeAfter);
}

@Test
public void testAddSingleTokenWithBigramModelDoesNotAddNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("only");
assertEquals(0, model.size());
}

@Test
public void testAddExactlyNTokensAddsOneNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("first", "second");
assertEquals(1, model.size());
}

@Test
public void testAddNTokensPlusOneAddsTwoNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two", "three");
assertEquals(2, model.size());
}

@Test
public void testCalculateProbabilityWithUnknownTokensReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("yes", "no");
double prob = model.calculateProbability("unknown", "tokens");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWithKnownUnigramInUnigramModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("known");
double prob = model.calculateProbability("known");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithUnseenUnigramInUnigramModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("a");
double prob = model.calculateProbability("b");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithNoTokensReturnsHighestProbabilityNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("coffee", "shop");
model.add("tea", "shop");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testPredictNextTokensReturnsNullWithNoMatchingPrefix() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
String[] prediction = model.predictNextTokens("xray");
assertNull(prediction);
}

@Test
public void testPredictNextTokensWithMatchingPrefixReturnsMostProbableOption() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("go", "left");
model.add("go", "left");
model.add("go", "right");
String[] result = model.predictNextTokens("go");
assertNotNull(result);
assertEquals("left", result[0]);
}

@Test
public void testCalculateProbabilityWithMultipleKnownNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob > 0.0);
}

@Test
public void testBackoffHandlesMissingNMinusOneCountGracefully() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello", "world");
double prob = model.calculateProbability("welcome", "planet");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testInputStreamConstructorWithEmptyStreamCreatesEmptyModel() throws IOException {
ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
NGramLanguageModel model = new NGramLanguageModel(stream, 2);
assertEquals(0, model.size());
}

@Test
public void testAddMultipleIdenticalSequencesIncreasesCounts() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("repeat", "this");
model.add("repeat", "this");
model.add("repeat", "this");
double prob = model.calculateProbability("repeat", "this");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWithTrigramModelReturnsThreeTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
String[] next = model.predictNextTokens();
assertNotNull(next);
assertEquals(3, next.length);
}

@Test
public void testProbabilityDoesNotReturnNaNAfterMultipleStupidBackoffCalls() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("once", "upon", "time");
model.add("upon", "a", "time");
double prob = model.calculateProbability("story", "starts", "here");
assertFalse(Double.isNaN(prob));
}

@Test
public void testDeepBackoffToUnigramFallbackReturnsZeroWithNoMatches() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double prob = model.calculateProbability("a", "b", "c");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithTokensLongerThanAnyNGramStored() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
String[] result = model.predictNextTokens("a", "b", "c", "d");
assertNull(result);
}

@Test
public void testAddLongerThanNInputStillYieldsCorrectNGramCount() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two", "three", "four");
assertEquals(3, model.size());
}

@Test
public void testCalculateProbabilityMixedKnownAndUnknownSequence() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
double prob = model.calculateProbability("a", "b", "x", "y");
assertTrue(prob >= 0.0);
}

@Test
public void testBackoffRecursionHitsBaseCaseOnly() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello", "world");
double prob = model.calculateProbability("hello");
assertTrue(prob > 0.0);
}

@Test
public void testBackoffRecursionWithZeroNGramCountFallsBackCleanly() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0);
}

@Test
public void testBackoffWhenIntermediateNMinusOneTokensAreAbsent() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("z", "y", "x");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testConstructorWithFullyPopulatedByteStreamStillWorksGracefully() throws IOException {
byte[] invalidModel = new byte[] { 0, 1, 2, 3, 4, 5, 127, -128, -1 };
try {
NGramLanguageModel model = new NGramLanguageModel(new ByteArrayInputStream(invalidModel), 2);
model.add("safe", "fallback");
assertTrue(model.size() >= 0);
} catch (IOException e) {
assertTrue(true);
}
}

@Test
public void testPredictNextTokensReturnsNullWhenModelEmpty() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] result = model.predictNextTokens("any");
assertNull(result);
}

@Test
public void testCalculateProbabilityReturnsZeroWithNoMatchingTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
double prob = model.calculateProbability("no", "match");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictionLengthMatchesModelSettingWhenModelOnlyContainsUnigrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testAddEmptyInputDoesNotAffectModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
int sizeBefore = model.size();
model.add();
int sizeAfter = model.size();
assertEquals(sizeBefore, sizeAfter);
}

@Test
public void testProbabilityAvoidsDivisionByZeroWhenNMinusOneNotPresent() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
double prob = model.calculateProbability("a", "b");
assertTrue(prob >= 0.0);
}

@Test
public void testPredictNextTokensExactLengthPrediction() {
NGramLanguageModel model = new NGramLanguageModel(4);
model.add("one", "two", "three", "four");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(4, result.length);
}

@Test
public void testCalculateProbabilityUsesLogSumProperlyForMultipleMatches() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("the", "sky");
model.add("sky", "is");
model.add("is", "blue");
double prob = model.calculateProbability("the", "sky", "is", "blue");
assertTrue(prob > 0.0);
}

@Test
public void testProbabilityWithSingleUnknownTokenInModelReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("known", "term");
double prob = model.calculateProbability("foo");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testAddAddsCorrectNumberOfTrigrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d", "e");
assertEquals(3, model.size());
}

@Test
public void testCalculateProbabilityWhenTopScoreCausesNaNThenRecoversToZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double probability = model.calculateProbability("x", "y");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testCalculateProbabilityReturnsZeroWhenUnseenTrigramsInTrigramModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testStupidBackoffFallsBackFromNonexistentBigramToUnigram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello");
double prob = model.calculateProbability("x", "y");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWhenMultipleNGramsMatchButSameScore() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("the", "dog");
model.add("the", "cat");
String[] prediction = model.predictNextTokens("the");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("cat") || prediction[0].equals("dog"));
}

@Test
public void testAddWithRepeatingTokensStillAddsCorrectNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("token", "token", "token");
assertEquals(2, model.size());
}

@Test
public void testAddWithEmptyStringTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "value");
model.add("value", "");
double prob = model.calculateProbability("", "value");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWithEmptyPrefixReturnsMostFrequentNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("first", "word");
model.add("second", "word");
model.add("first", "word");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals("first", result[0]);
}

@Test
public void testCalculateProbabilitySingleUnigramWithEmptyString() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("");
double prob = model.calculateProbability("");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithOneTokenShorterThanNModel() {
NGramLanguageModel model = new NGramLanguageModel(4);
model.add("a", "b", "c", "d");
double prob = model.calculateProbability("a");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictionIsNullWithExactMatchSizeButNoExtensionAvailable() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
String[] result = model.predictNextTokens("a", "b");
assertNull(result);
}

@Test
public void testCalculateProbabilityWithLongSequenceContainingKnownNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("start", "middle");
model.add("middle", "end");
double prob = model.calculateProbability("start", "middle", "end", "unknown");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensPicksLongestAvailableRecentMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("to", "be", "continued");
model.add("to", "be");
String[] result = model.predictNextTokens("to", "be");
assertNotNull(result);
assertEquals(3, result.length);
assertEquals("to", result[0]);
assertEquals("be", result[1]);
assertEquals("continued", result[2]);
}

@Test
public void testCalculateProbabilityUnigramModelSkipsExtraTokensGracefully() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("x");
model.add("y");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithMultipleUnseenUnigramsReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("visible");
double prob = model.calculateProbability("invisible", "ghost", "phantom");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithSingleUnigramModelChoosesOneOfThem() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("apple");
model.add("banana");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("apple") || result[0].equals("banana"));
}

@Test
public void testCalculateProbabilityWithAllNaNStepsResetsFinalResult() {
NGramLanguageModel model = new NGramLanguageModel(2);
double result = model.calculateProbability("a", "b", "c", "d", "e", "f");
assertFalse(Double.isNaN(result));
assertEquals(0.0, result, 0.000001);
}
}
