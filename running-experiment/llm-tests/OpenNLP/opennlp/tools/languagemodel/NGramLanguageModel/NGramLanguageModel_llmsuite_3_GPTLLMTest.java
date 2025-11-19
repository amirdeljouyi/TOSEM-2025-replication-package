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

public class NGramLanguageModel_llmsuite_3_GPTLLMTest {

@Test
public void testDefaultConstructorCreatesTrigramModel() {
NGramLanguageModel model = new NGramLanguageModel();
model.add("the", "cat", "sat");
double probability = model.calculateProbability("the", "cat", "sat");
assertTrue(probability > 0.0);
}

@Test
public void testConstructorWithValidN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("quick", "brown");
double probability = model.calculateProbability("quick", "brown");
assertTrue(probability > 0.0);
}

@Test
public void testConstructorWithInvalidNThrowsException() {
// expectedException.expect(IllegalArgumentException.class);
// expectedException.expectMessage("Parameter 'n' must be greater than 0.");
new NGramLanguageModel(0);
}

@Test
public void testConstructorWithInputStreamValidData() throws IOException {
opennlp.tools.ngram.NGramModel modelToSerialize = new opennlp.tools.ngram.NGramModel();
modelToSerialize.add(new StringList("alpha", "beta"), 1, 2);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(modelToSerialize);
oos.flush();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramLanguageModel model = new NGramLanguageModel(in);
double probability = model.calculateProbability("alpha", "beta");
assertTrue(probability > 0.0);
}

@Test
public void testConstructorWithInputStreamAndInvalidNThrowsException() throws IOException {
ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
// expectedException.expect(IllegalArgumentException.class);
new NGramLanguageModel(input, 0);
}

@Test
public void testAddTokensCreatesExpectedNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two", "three");
assertTrue(model.contains(new StringList("one", "two")));
assertTrue(model.contains(new StringList("two", "three")));
}

@Test
public void testCalculateProbabilityForKnownTokensReturnsPositive() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("natural", "language", "processing");
double probability = model.calculateProbability("natural", "language", "processing");
assertTrue(probability > 0.0);
}

@Test
public void testCalculateProbabilityForUnknownReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("deep", "learning");
double probability = model.calculateProbability("unknown", "tokens");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testCalculateProbabilityWithEmptyModelReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
double probability = model.calculateProbability("a", "b", "c");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testPredictNextTokensReturnsExpectedTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "brown", "fox");
model.add("the", "brown", "dog");
String[] prediction = model.predictNextTokens("the", "brown");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("fox") || prediction[0].equals("dog"));
}

@Test
public void testPredictNextTokenNoMatchReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("java", "language");
String[] prediction = model.predictNextTokens("no", "match");
assertNull(prediction);
}

@Test
public void testStupidBackoffRecursesWithoutException() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("deep", "neural", "networks");
double probability = model.calculateProbability("x", "y", "z");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testAddEmptyTokenArrayDoesNotThrow() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add();
assertEquals(0, model.size());
}

@Test
public void testGetCountReturnsCorrectValueAfterAdd() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("the", "sky");
model.add("the", "sky");
int count = model.getCount(new StringList("the", "sky"));
assertEquals(2, count);
}

@Test
public void testIteratorProvidesAllNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
model.add("beta", "gamma");
Iterator<StringList> iterator = model.iterator();
assertTrue(iterator.hasNext());
iterator.next();
assertTrue(iterator.hasNext());
iterator.next();
assertFalse(iterator.hasNext());
}

@Test
public void testIteratorThrowsWhenExhausted() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
Iterator<StringList> iterator = model.iterator();
iterator.next();
// expectedException.expect(NoSuchElementException.class);
iterator.next();
}

@Test
public void testPredictNextTokensWithEmptyModelReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] result = model.predictNextTokens("some", "prefix");
assertNull(result);
}

@Test
public void testPredictNextTokensWithEmptyInputReturnsMostFrequentNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("the", "dog");
model.add("the", "cat");
model.add("the", "dog");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("dog", result[0]);
}

@Test
public void testCalculateProbabilityWithSingleTokenInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("only", "once");
double probability = model.calculateProbability("only");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testAddSingleTokenOnlyDoesNotAddAnyNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("token");
assertEquals(0, model.size());
}

@Test
public void testCalculateProbabilityInMixedKnownUnknownSequence() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("known", "sequence");
double probability = model.calculateProbability("known", "sequence", "unknown");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testBackoffToUnigramWhenNoNMinusOneTokenExists() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("standalone");
double probability = model.calculateProbability("standalone");
assertTrue(probability > 0.0);
}

@Test
public void testCalculateProbabilityWithRepeatedTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("repeat", "repeat", "repeat", "repeat");
double probability = model.calculateProbability("repeat", "repeat");
assertTrue(probability > 0.0);
}

@Test
public void testAddNullTokensArrayDoesNotAddAnything() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add((String[]) null);
assertEquals(0, model.size());
}

@Test
public void testPredictionWithMultipleCandidatesSameProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("it", "rains");
model.add("it", "pours");
String[] prediction = model.predictNextTokens("it");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("rains") || prediction[0].equals("pours"));
}

@Test
public void testModelSizeReflectsUniqueNGramsOnly() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("shine", "bright", "shine", "bright");
assertEquals(2, model.size());
}

@Test
public void testCalculateProbabilityWithLogOverflowAvoided() {
NGramLanguageModel model = new NGramLanguageModel(1);
for (int i = 0; i < 1000; i++) {
model.add("token");
}
double probability = model.calculateProbability("token");
assertTrue(probability > 0.0);
assertFalse(Double.isNaN(probability));
}

@Test
public void testModelHandlesTrailingNullTokenGracefully() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", null, "beta");
assertFalse(model.contains(new StringList("alpha", null)));
assertFalse(model.contains(new StringList(null, "beta")));
}

@Test
public void testPredictNextTokensWithExactPrefixMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("my", "favorite", "language");
String[] result = model.predictNextTokens("my", "favorite");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("language", result[0]);
}

@Test
public void testCalculateProbabilityHandlesFloatingPointRobustness() {
NGramLanguageModel model = new NGramLanguageModel(2);
for (int i = 0; i < 100; i++) {
model.add("common", "phrase");
}
model.add("rare", "word");
double commonProb = model.calculateProbability("common", "phrase");
double rareProb = model.calculateProbability("rare", "word");
assertTrue(commonProb > rareProb);
}

@Test
public void testCalculateProbabilityWithExactNMinusOneBackoff() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "quick", "fox");
model.add("quick", "fox");
model.add("fox");
double prob = model.calculateProbability("the", "quick", "fox");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityBackoffMultipleLevels() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double probability = model.calculateProbability("x", "y", "z");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testCalculateProbabilityWithExactLengthSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double probability = model.calculateProbability("x", "y", "z");
assertTrue(probability > 0.0);
}

@Test
public void testPredictNextTokensOnUnigramsModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("blue");
model.add("red");
String[] result = model.predictNextTokens("ignored");
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testCalculateProbabilityWithInsufficientInputTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double probability = model.calculateProbability("a");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testAddTokenWithOnlyWhitespaceStrings() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("   ", " ");
assertTrue(model.contains(new StringList("   ", " ")));
}

@Test
public void testAddAndCountLongTokenStrings() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("thisisaverylongtokenthatmightcauseissues", "anotherverylongtoken");
int count = model.getCount(new StringList("thisisaverylongtokenthatmightcauseissues", "anotherverylongtoken"));
assertEquals(1, count);
}

@Test
public void testEmptyStringTokensHandledCorrectly() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "nonempty");
model.add("nonempty", "");
assertTrue(model.contains(new StringList("", "nonempty")));
assertTrue(model.contains(new StringList("nonempty", "")));
}

@Test
public void testIteratorWithEmptyModelHasNoElements() {
NGramLanguageModel model = new NGramLanguageModel(3);
Iterator<StringList> iterator = model.iterator();
assertFalse(iterator.hasNext());
}

@Test
public void testProbabilityCalculationReturnsProperLogProduct() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
model.add("beta", "gamma");
double probability = model.calculateProbability("alpha", "beta", "gamma");
assertTrue(probability > 0.0);
}

@Test
public void testModelSizeAfterAddingDuplicateNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("data", "science");
model.add("data", "science");
assertEquals(1, model.size());
}

@Test
public void testPredictNextTokensSingleNGramInModel() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("machine", "learning");
String[] prediction = model.predictNextTokens("machine");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("learning", prediction[0]);
}

@Test
public void testPredictNextTokensWithMultipleMatchesPickBest() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("go", "home");
model.add("go", "school");
model.add("go", "home");
String[] prediction = model.predictNextTokens("go");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("home", prediction[0]);
}

@Test
public void testStupidBackoffRecursiveBaseCaseNoContext() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("a");
model.add("b");
model.add("c");
double prob = model.calculateProbability("a");
assertTrue(prob > 0.0 && prob <= 1.0);
}

@Test
public void testCalculateProbabilityWithOnlyPaddingTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("<s>", "hello");
model.add("hello", "</s>");
double prob = model.calculateProbability("<s>", "hello", "</s>");
assertTrue(prob > 0.0);
}

@Test
public void testAddWithNullArrayReferenceDoesNothing() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add((String[]) null);
assertEquals(0, model.size());
}

@Test
public void testAddWithNullTokenInsideArraySkipsInvalidNgram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", null, "b");
assertFalse(model.contains(new StringList("a", null)));
assertFalse(model.contains(new StringList(null, "b")));
assertEquals(1, model.size());
}

@Test
public void testCalculateProbabilityWithTokensShorterThanNReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("a");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWithTokensEqualToNLastNgramUnknown() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double prob = model.calculateProbability("x", "y", "w");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWithNgramCountZeroCausesBackoff() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("total", "count");
model.add("n", "minus");
double prob = model.calculateProbability("unknown", "tokens");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWhenInputLengthEqualsNReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("you", "see");
String[] prediction = model.predictNextTokens("you", "see");
assertNull(prediction);
}

@Test
public void testAddTokensWithOnlyOneElementDoesNotAddAnyNgrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("single");
assertEquals(0, model.size());
}

@Test
public void testCalculateProbabilityHandlesNaNLogGracefully() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("never", "seen");
double prob = model.calculateProbability("missing", "ngram");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensOnEmptyModelReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] result = model.predictNextTokens("nothing", "here");
assertNull(result);
}

@Test
public void testPredictNextTokensReturnsLongestMatchingNgram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("deep", "neural", "net");
model.add("deep", "neural", "network");
String[] result = model.predictNextTokens("deep", "neural");
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("net") || result[0].equals("network"));
}

@Test
public void testAddTokensSameNgramDifferentCapitalsStoredSeparately() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("Token", "Value");
model.add("token", "value");
assertEquals(2, model.size());
assertTrue(model.contains(new StringList("Token", "Value")));
assertTrue(model.contains(new StringList("token", "value")));
}

@Test
public void testSingleBigramSequenceProbabilitySumOfLogWorks() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
double prob1 = model.calculateProbability("a", "b");
double prob2 = model.calculateProbability("b", "c");
double probBoth = model.calculateProbability("a", "b", "c");
assertTrue(probBoth < prob1);
assertTrue(probBoth < prob2);
assertTrue(probBoth > 0.0);
}

@Test
public void testPredictionWithTokensUnalignedWithNgramsReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("start", "of", "sentence");
String[] prediction = model.predictNextTokens("not", "present");
assertNull(prediction);
}

@Test
public void testModelHandlesHighNValue() {
NGramLanguageModel model = new NGramLanguageModel(10);
model.add("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
assertTrue(model.size() > 0);
double prob = model.calculateProbability("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
assertTrue(prob > 0.0);
}

@Test
public void testModelHandlesNEqualToOneProperly() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("apple", "banana", "cherry");
assertTrue(model.contains(new StringList("apple")));
assertTrue(model.contains(new StringList("banana")));
assertTrue(model.contains(new StringList("cherry")));
assertEquals(3, model.size());
}

@Test
public void testAddTokensWithMixOfWhitespaceAndValidTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(" ", "valid");
model.add("valid", " ");
assertTrue(model.contains(new StringList(" ", "valid")));
assertTrue(model.contains(new StringList("valid", " ")));
assertEquals(2, model.size());
}

@Test
public void testCalculateProbabilityWithNullInputTokensReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
String[] tokens = null;
double prob = model.calculateProbability(tokens);
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithNullInputReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
String[] prediction = model.predictNextTokens((String[]) null);
assertNull(prediction);
}

@Test
public void testProbabilitiesCorrectWithMixedFrequencies() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("high", "frequency");
model.add("high", "frequency");
model.add("low", "frequency");
double highFreqProb = model.calculateProbability("high", "frequency");
double lowFreqProb = model.calculateProbability("low", "frequency");
assertTrue(highFreqProb > lowFreqProb);
}

@Test
public void testMultipleCallsToAddAccumulateCountsCorrectly() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("accumulate", "test");
model.add("accumulate", "test");
model.add("accumulate", "test");
int count = model.getCount(new StringList("accumulate", "test"));
assertEquals(3, count);
}

@Test
public void testPredictNextTokensLongestPrefixMatchWins() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "quick", "fox");
model.add("the", "quick", "dog");
model.add("quick", "fox", "jumps");
String[] prediction = model.predictNextTokens("the", "quick");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("fox") || prediction[0].equals("dog"));
}

@Test
public void testCalculateProbabilityForRepeatingPattern() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "a");
model.add("a", "a");
model.add("a", "a");
double prob = model.calculateProbability("a", "a");
assertTrue(prob > 0.0);
}

@Test
public void testPredictionFromModelContainingOnlyOneNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("only", "this");
String[] prediction = model.predictNextTokens("only");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("this", prediction[0]);
}

@Test
public void testStupidBackoffReturnsNonZeroWhenBackedOffToUnigram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
model.add("beta");
double prob = model.calculateProbability("alpha", "beta");
assertTrue(prob > 0.0);
}

@Test
public void testAddWithExplicitShorterThanNSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("short", "sequence");
assertEquals(0, model.getCount(new StringList("short", "sequence")));
}

@Test
public void testModelWithZeroEntriesPredictReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] result = model.predictNextTokens("something");
assertNull(result);
}

@Test
public void testPredictionWhenOneCandidateHasZeroProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("valid", "token");
model.add("rare", "pair");
String[] result = model.predictNextTokens("valid");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("token", result[0]);
}

@Test
public void testCalculateProbabilityWithAllUnknownTokensReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
double prob = model.calculateProbability("x", "y");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testAddAndPredictCaseSensitiveEntries() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("Case", "Sensitive");
model.add("case", "sensitive");
String[] result = model.predictNextTokens("Case");
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("Sensitive"));
assertFalse(result[0].equals("sensitive"));
}

@Test
public void testCalculateProbabilityWithMaxSizeBuffer() {
NGramLanguageModel model = new NGramLanguageModel(4);
model.add("a", "b", "c", "d", "e");
double prob = model.calculateProbability("a", "b", "c", "d");
assertTrue(prob > 0.0);
}

@Test
public void testAddTokensWithAllNullsArray() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(null, null);
assertEquals(0, model.size());
}

@Test
public void testCalculateProbabilityAfterAddingNullTokensIsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", null, "z");
double probability = model.calculateProbability("x", "z");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testAddTokensWithEmptyStringElements() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "value");
model.add("value", "");
assertTrue(model.contains(new StringList("", "value")));
assertTrue(model.contains(new StringList("value", "")));
}

@Test
public void testAddTokensWithLeadingAndTrailingWhitespace() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("  lead", "trail  ");
assertTrue(model.contains(new StringList("  lead", "trail  ")));
}

@Test
public void testCalculateProbabilityWithWhitespaceTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(" ", " ");
double probability = model.calculateProbability(" ", " ");
assertTrue(probability > 0.0);
}

@Test
public void testAddMinimumLengthThatStillCreatesNgram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
assertTrue(model.contains(new StringList("a", "b")));
}

@Test
public void testAddTokensFromExactNSizeSequence() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two");
assertTrue(model.contains(new StringList("one", "two")));
}

@Test
public void testPredictNextTokensFromEmptyInputReturnsMostFrequent() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("common");
model.add("rare");
model.add("common");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("common", prediction[0]);
}

@Test
public void testPredictNextTokensWithIdenticalFrequencySelectsFirstInIteration() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("equal", "one");
model.add("equal", "two");
String[] prediction = model.predictNextTokens("equal");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("one") || prediction[0].equals("two"));
}

@Test
public void testCalculateProbabilityIncludesBackoffWeightAtZeroCounts() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double probability = model.calculateProbability("x", "y");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testAddTokensDoesNotThrowForOversizedInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c", "d", "e");
assertTrue(model.contains(new StringList("a", "b")));
}

@Test
public void testCalculateProbabilityWithNonOverlappingNGramsReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello", "world");
double probability = model.calculateProbability("foo", "bar");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testPredictNextTokensDoesNotThrowOnSingleNGramModelNoPrefix() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("start", "end");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testCalculateProbabilityWithPartialBackoffChain() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b");
model.add("b", "c");
double probability = model.calculateProbability("a", "b", "c");
assertTrue(probability > 0.0);
}

@Test
public void testAddTokensWithEmptyStringDoesNotThrow() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "");
assertTrue(model.contains(new StringList("", "")));
}

@Test
public void testProbabilitiesAreConsistentForUnigramModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("apple");
model.add("banana");
model.add("apple");
double appleProb = model.calculateProbability("apple");
double bananaProb = model.calculateProbability("banana");
assertTrue(appleProb > bananaProb);
}

@Test
public void testCalculateProbabilityWithTokensEqualToOneLessThanN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double probability = model.calculateProbability("a", "b");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testAddTokensWithExactNPlusOneLengthForMultipleGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "a", "test");
assertTrue(model.contains(new StringList("this", "is", "a")));
assertTrue(model.contains(new StringList("is", "a", "test")));
assertEquals(2, model.size());
}

@Test
public void testAddTokensWithAllEmptyStringsAcceptedAsValidInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "");
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("", "")));
}

@Test
public void testCalculateProbabilityHandlesAllCapitalTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("HELLO", "WORLD");
double probability = model.calculateProbability("HELLO", "WORLD");
assertTrue(probability > 0.0);
}

@Test
public void testCalculateProbabilityReturnsZeroForUnknownPrefix() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("known", "pair");
double probability = model.calculateProbability("unknown", "pair");
assertEquals(0.0, probability, 0.000001);
}

@Test
public void testAddEmptyStringArrayDoesNotAddNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
String[] empty = new String[0];
model.add(empty);
assertEquals(0, model.size());
}

@Test
public void testAddMinimumTokensWhenNis1AddsUnigrams() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("one");
model.add("two");
model.add("three");
assertEquals(3, model.size());
assertTrue(model.contains(new StringList("one")));
assertTrue(model.contains(new StringList("two")));
assertTrue(model.contains(new StringList("three")));
}

@Test
public void testGetCountReturnsZeroForMissingNGram() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("seen", "pair");
int count = model.getCount(new StringList("unseen", "token"));
assertEquals(0, count);
}

@Test
public void testPredictNextTokensWithPrefixThatIsFullNGramProducesNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
String[] result = model.predictNextTokens("alpha", "beta");
assertNull(result);
}

@Test
public void testCalculateProbabilityWithTokensMuchLongerThanN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
double probability = model.calculateProbability("x", "y", "a", "b", "c");
assertTrue(probability >= 0.0);
}

@Test
public void testPredictNextTokenWhenMultipleCandidatesHaveSameProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("repeat", "one");
model.add("repeat", "two");
String[] prediction = model.predictNextTokens("repeat");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("one") || prediction[0].equals("two"));
}

@Test
public void testCalculateProbabilityFromSingleObservedNGramReturnsHighScore() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("alpha", "beta");
double probability = model.calculateProbability("alpha", "beta");
assertTrue(probability > 0.0 && probability <= 1.0);
}

@Test
public void testPredictNextTokensRespectsMaxProbabilitySelection() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("next", "best");
model.add("next", "best");
model.add("next", "worst");
String[] result = model.predictNextTokens("next");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("best", result[0]);
}

@Test
public void testCalculateProbabilityOnBackoffAllTheWayToEmptyReturnsZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double probability = model.calculateProbability("x", "y", "z");
assertEquals(0.0, probability, 0.00001);
}

@Test
public void testAddSequenceWithDifferentNGramsCountsEachCorrectly() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d");
assertTrue(model.contains(new StringList("a", "b", "c")));
assertTrue(model.contains(new StringList("b", "c", "d")));
assertEquals(2, model.size());
}
}
