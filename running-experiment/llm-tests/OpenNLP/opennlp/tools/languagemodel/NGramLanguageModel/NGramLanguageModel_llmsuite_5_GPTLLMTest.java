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
import opennlp.tools.ngram.NGramModel;
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

public class NGramLanguageModel_llmsuite_5_GPTLLMTest {

@Test
public void testDefaultConstructorSetsDefaultN() {
NGramLanguageModel model = new NGramLanguageModel();
assertNotNull(model);
}

@Test
public void testConstructorWithValidN() {
NGramLanguageModel model = new NGramLanguageModel(2);
assertNotNull(model);
}

@Test
public void testConstructorWithInvalidN() {
new NGramLanguageModel(0);
}

@Test
public void testConstructorFromInputStreamValid() throws IOException {
NGramModel base = new NGramModel();
base.add(new StringList("A", "B", "C"), 1, 3);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
oos.writeObject(base);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
NGramLanguageModel model = new NGramLanguageModel(bin, 3);
assertNotNull(model);
assertTrue(model.contains(new StringList("A", "B", "C")));
}

@Test
public void testConstructorFromInputStreamWithInvalidN() throws IOException {
ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
new NGramLanguageModel(in, 0);
}

@Test
public void testAddTokensCreatesCorrectNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "quick", "brown", "fox");
assertTrue(model.contains(new StringList("the", "quick", "brown")));
assertTrue(model.contains(new StringList("quick", "brown", "fox")));
}

@Test
public void testAddTokensWithLengthLessThanN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("only", "two");
assertEquals(0, model.size());
}

@Test
public void testCalculateProbabilityOfKnownNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "good");
double prob = model.calculateProbability("this", "is", "good");
assertTrue(prob > 0.0);
assertTrue(prob <= 1.0);
}

@Test
public void testCalculateProbabilityReturnsZeroForEmptyModel() {
NGramLanguageModel model = new NGramLanguageModel(2);
double prob = model.calculateProbability("nothing", "here");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testCalculateProbabilityWithBackoffPartialMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("alpha", "beta", "gamma");
double prob = model.calculateProbability("alpha", "beta", "delta");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityOfUnseenSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0);
assertTrue(prob <= 1.0);
}

@Test
public void testCalculateProbabilityAvoidNaN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("m", "n", "o");
double prob = model.calculateProbability("x", "y", "z");
assertFalse(Double.isNaN(prob));
}

@Test
public void testPredictNextTokensSingleMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("simple", "test", "case");
String[] next = model.predictNextTokens("simple", "test");
assertNotNull(next);
assertEquals(1, next.length);
assertEquals("case", next[0]);
}

@Test
public void testPredictNextTokensWithNoMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] result = model.predictNextTokens("no", "tokens");
assertNull(result);
}

@Test
public void testPredictNextTokensWithMultipleCandidates() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("new", "york", "city");
model.add("new", "york", "jets");
String[] result = model.predictNextTokens("new", "york");
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("city") || result[0].equals("jets"));
}

@Test
public void testBackoffToBigramReturnsValidProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "two");
double prob = model.calculateProbability("zero", "one", "two");
assertTrue(prob >= 0.0);
}

@Test
public void testBackoffToUnigramProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello");
double prob = model.calculateProbability("a", "b", "hello");
assertTrue(prob >= 0.0);
}

@Test
public void testProbabilityStableAfterAddingUnrelatedSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("to", "be", "or");
double prob1 = model.calculateProbability("to", "be", "or");
model.add("foo", "bar", "baz");
double prob2 = model.calculateProbability("to", "be", "or");
assertEquals(prob1, prob2, 0.0001);
}

@Test
public void testAddSingleTokenDoesNotAddNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("only");
assertEquals(0, model.size());
}

@Test
public void testAddExactNTokensFormsSingleNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "two", "three");
assertTrue(model.contains(new StringList("one", "two", "three")));
assertEquals(1, model.size());
}

@Test
public void testMultipleAddsIncreaseCount() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("a", "b", "c");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithEmptyTokenArray() {
NGramLanguageModel model = new NGramLanguageModel(3);
double prob = model.calculateProbability();
assertEquals(0.0, prob, 0.0);
}

@Test
public void testAddNullTokensArrayThrowsNullPointerException() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add((String[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testAddArrayWithNullTokenValue() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add("a", null, "b");
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictNextTokensWithEmptyTokenArrayReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
String[] prediction = model.predictNextTokens();
assertNull(prediction);
}

@Test
public void testPredictNextTokensWithExactMatchLengthOfN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "test");
String[] prediction = model.predictNextTokens("this", "is", "test");
assertNull(prediction);
}

@Test
public void testPredictNextTokensPrefixMatchButLowerProbabilityBackoff() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "two", "three");
model.add("one", "two", "four");
model.add("one", "two", "four");
String[] prediction = model.predictNextTokens("one", "two");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("four", prediction[0]);
}

@Test
public void testPredictNextTokensWhenModelHasOnlyUnigrams() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("apple");
model.add("banana");
model.add("grape");
String[] prediction = model.predictNextTokens("apple");
assertNotNull(prediction);
assertEquals(1, prediction.length);
}

@Test
public void testCalculateProbabilityWhenBackoffChainHitsNoMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
double prob = model.calculateProbability("utterly", "random", "tokens");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testModelSizeAfterMultipleDistinctNGramsAdded() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d");
model.add("x", "y", "z");
model.add("1", "2", "3", "4");
assertEquals(5, model.size());
}

@Test
public void testModelAddTokenWithDuplicateConsecutiveTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("repeat", "repeat", "repeat");
assertTrue(model.contains(new StringList("repeat", "repeat")));
}

@Test
public void testCalculateProbabilityOfUnigramOnlyModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("alpha");
model.add("beta");
model.add("alpha");
double prob = model.calculateProbability("alpha");
assertTrue(prob > 0.0);
}

@Test
public void testConstructorInputStreamIOException() {
byte[] invalidSerializedData = new byte[] { 0x00, 0x01, 0x02, 0x03 };
ByteArrayInputStream corruptedStream = new ByteArrayInputStream(invalidSerializedData);
try {
new NGramLanguageModel(corruptedStream, 3);
fail("Expected IOException");
} catch (IOException expected) {
}
}

@Test
public void testPredictNextWhenAllCandidateSequencesProduceZeroProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
String[] prediction = model.predictNextTokens("x", "y");
assertNull(prediction);
}

@Test
public void testCalculateProbabilityWithRepeatedTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("yes", "yes", "yes");
double prob = model.calculateProbability("yes", "yes", "yes");
assertTrue(prob > 0.0);
}

@Test
public void testAddEmptyTokenArray() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add();
assertEquals(0, model.size());
}

@Test
public void testCalculateProbabilityWithLessThanRequiredTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("a", "b");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testCalculateProbabilityWithExactlyOneTokenAndUnigramModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("apple", "banana", "carrot");
double prob = model.calculateProbability("banana");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWithNullInputArray() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("this", "is", "fine");
try {
model.predictNextTokens((String[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testCalculateProbabilityOfTokenContainingWhitespace() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello world", "test");
double prob = model.calculateProbability("hello world", "test");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithAllUnknownNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
double prob = model.calculateProbability("a", "b", "c", "d");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testAddIdenticalSequenceMultipleTimesAndGetProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("foo", "bar", "baz");
model.add("foo", "bar", "baz");
model.add("foo", "bar", "baz");
double prob = model.calculateProbability("foo", "bar", "baz");
assertTrue(prob > 0.0);
}

@Test
public void testBackoffToEmptyHistoryReturnsSmoothedProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("intro", "word");
double prob = model.calculateProbability("missing", "entry");
assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testMissingNMinusOneTokenPathHandledGracefully() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0);
}

@Test
public void testProbabilityOfDifferentOrdersWhenSomeAvailableSomeMissing() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("I", "am", "testing");
model.add("testing", "is", "fun");
double prob = model.calculateProbability("I", "am", "testing", "is", "fun");
assertTrue(prob > 0.0);
}

@Test
public void testAddOverlappingNGramsDoesNotOverwrite() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d", "e");
StringList ngram1 = new StringList("a", "b", "c");
StringList ngram2 = new StringList("b", "c", "d");
StringList ngram3 = new StringList("c", "d", "e");
assertTrue(model.contains(ngram1));
assertTrue(model.contains(ngram2));
assertTrue(model.contains(ngram3));
assertEquals(3, model.size());
}

@Test
public void testInputStreamConstructorWithEmptyStreamFails() {
byte[] emptyData = new byte[0];
ByteArrayInputStream stream = new ByteArrayInputStream(emptyData);
try {
new NGramLanguageModel(stream);
fail("Expected IOException");
} catch (IOException expected) {
}
}

@Test
public void testSingleUnigramTokenDoesNotCrashProbability() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("unique");
double prob = model.calculateProbability("unique");
assertTrue(prob > 0.0);
}

@Test
public void testMultipleUnigramSequencesReturnsHighestProbabilityToken() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("orange");
model.add("orange");
model.add("banana");
String[] predicted = model.predictNextTokens();
assertNotNull(predicted);
assertEquals("orange", predicted[0]);
}

@Test
public void testCalculateProbabilityWhereBackoffGetsZeroProbabilityAndRecursesToUnseen() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("x", "y", "z");
double prob = model.calculateProbability("new", "sequence", "test");
assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testDeserializeLargeModelFromStream() throws IOException {
opennlp.tools.ngram.NGramModel base = new opennlp.tools.ngram.NGramModel();
base.add(new StringList("alpha", "beta", "gamma"), 3, 3);
base.add(new StringList("beta", "gamma", "delta"), 3, 3);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(base);
oos.close();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramLanguageModel model = new NGramLanguageModel(in);
double prob = model.calculateProbability("alpha", "beta", "gamma");
assertTrue(prob > 0.0);
String[] predicted = model.predictNextTokens("alpha", "beta");
assertNotNull(predicted);
}

@Test
public void testCalculateProbabilityWithAllTokensUnseenAndNoBackoffPath() {
NGramLanguageModel model = new NGramLanguageModel(3);
double prob = model.calculateProbability("unseen1", "unseen2", "unseen3");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testCalculateProbabilityWithMultipleValidNGramsProbabilityProductNotZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c");
model.add("b", "c", "d");
double prob = model.calculateProbability("a", "b", "c", "d");
assertTrue(prob > 0.0);
}

@Test
public void testAddSequenceContainingEmptyStringTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "", "b", "");
assertTrue(model.contains(new StringList("a", "", "b")));
assertTrue(model.contains(new StringList("", "b", "")));
}

@Test
public void testCalculateProbabilityWithEmptyStringTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "", "b");
double prob = model.calculateProbability("a", "", "b");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWithUnigramModelAndMultipleCandidates() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("one");
model.add("one");
model.add("two");
model.add("three");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("one", prediction[0]);
}

@Test
public void testPredictNextTokensWhenNGramHasZeroCountDueToBackoffToEmptySequence() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
String[] prediction = model.predictNextTokens("a", "b");
assertNull(prediction);
}

@Test
public void testBackoffWhenNMinusOneTokenExistsButHasZeroCount() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0);
}

@Test
public void testLargeNValueAddShortSequence() {
NGramLanguageModel model = new NGramLanguageModel(5);
model.add("short", "tokens");
assertEquals(0, model.size());
}

@Test
public void testBackoffToEmptyListHandledWithoutException() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
double prob = model.calculateProbability("z", "q");
assertTrue(prob >= 0.0);
}

@Test
public void testAddTokenArrayContainingNullElement() {
NGramLanguageModel model = new NGramLanguageModel(2);
try {
model.add("hello", null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictNextTokensOnEmptyModelWithNoInput() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] prediction = model.predictNextTokens();
assertNull(prediction);
}

@Test
public void testAddTokensWithTrailingNullTokenThrowsException() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add("a", "b", null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testModelContainsOnlyExpectedNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two", "three");
assertTrue(model.contains(new StringList("one", "two")));
assertTrue(model.contains(new StringList("two", "three")));
assertFalse(model.contains(new StringList("three", "four")));
}

@Test
public void testBackoffChainStopsGracefullyOnNullIntermediateTokenList() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("x");
double prob = model.calculateProbability("z", "unknown");
assertTrue(prob >= 0.0);
}

@Test
public void testPredictionFromModelTrainedOnExactNLengthSequencesOnly() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("u", "v", "w");
String[] prediction = model.predictNextTokens("u", "v");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("w", prediction[0]);
}

@Test
public void testCorruptStreamFailsDeserializationGracefully() {
byte[] badData = new byte[] { 0x00, 0x01, 0x7F };
ByteArrayInputStream input = new ByteArrayInputStream(badData);
try {
new NGramLanguageModel(input, 3);
fail("Expected IOException");
} catch (IOException expected) {
}
}

@Test
public void testUnigramPredictionReturnsMostFrequentToken() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("x");
model.add("x");
model.add("x");
model.add("y");
model.add("z");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals("x", prediction[0]);
}

@Test
public void testCalculateProbabilityOfBackoffReturningZero() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("u", "v");
double prob = model.calculateProbability("x", "y");
assertTrue(prob >= 0.0);
}

@Test
public void testAddNullTokenArray() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add((String[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testAddTokenArrayWithAllNulls() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add(null, null, null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testCalculateProbabilityWithNullTokenArray() {
NGramLanguageModel model = new NGramLanguageModel(2);
try {
model.calculateProbability((String[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictNextTokensWithNullArgumentArray() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.predictNextTokens((String[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testAddTokensWithEmptyString() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "", "b");
assertTrue(model.contains(new StringList("a", "")));
assertTrue(model.contains(new StringList("", "b")));
}

@Test
public void testAddTokensMatchingExactlyN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("x", "y", "z")));
}

@Test
public void testPredictNextTokensWithAllTokensUnseen() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("alpha", "beta", "gamma");
String[] result = model.predictNextTokens("x", "y", "z");
assertNull(result);
}

@Test
public void testCalculateProbabilityBackoffRecursiveToEmpty() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testUnigramPredictionFallbackWhenNoTokensPassed() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("apple");
model.add("apple");
model.add("banana");
String[] tokens = model.predictNextTokens();
assertNotNull(tokens);
assertEquals(1, tokens.length);
assertEquals("apple", tokens[0]);
}

@Test
public void testProbabilityWhenNMinusOneTokenHasZeroCount() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
StringList invalid = new StringList("x", "y", "z");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testStupidBackoffRecursiveDepthReached() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("i", "j", "k");
assertTrue(prob >= 0.0);
}

@Test
public void testDeserializeFromStreamWithValidData() throws IOException {
opennlp.tools.ngram.NGramModel base = new opennlp.tools.ngram.NGramModel();
base.add(new StringList("x", "y", "z"), 1, 3);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(base);
oos.close();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramLanguageModel model = new NGramLanguageModel(in, 3);
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob > 0.0);
}

@Test
public void testDeserializeFromStreamWithBrokenDataThrowsIOException() {
byte[] corruptedData = new byte[] { 0x00, 0x01, 0x42 };
ByteArrayInputStream in = new ByteArrayInputStream(corruptedData);
try {
new NGramLanguageModel(in, 3);
fail("Expected IOException");
} catch (IOException expected) {
}
}

@Test
public void testZeroProbabilityWhenModelHasOnlyOneValidSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("d", "e", "f");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testAddSingleTokenRepeatedlyWithUnigramModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("x");
model.add("x");
model.add("x");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("x", prediction[0]);
}

@Test
public void testCalculateProbabilityWithIdenticalTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("word", "word", "word");
double prob = model.calculateProbability("word", "word", "word");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityEntireInputIsWhitespace() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(" ", " ");
double prob = model.calculateProbability(" ", " ");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensUsingLongestMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("b", "c", "d");
String[] predicted = model.predictNextTokens("b", "c");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertEquals("d", predicted[0]);
}

@Test
public void testAddWithEmptyTokenArrayProducesNoNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add();
assertEquals(0, model.size());
}

@Test
public void testAddWithWhitespaceOnlyTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(" ", " ");
assertTrue(model.contains(new StringList(" ", " ")));
assertEquals(1, model.size());
}

@Test
public void testCalculateProbabilityWithOnlyUnigramsInModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("x");
double prob = model.calculateProbability("x");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityOfUppercaseIdenticalToLowercaseWhenCaseNotNormalized() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("Hello", "world");
double probUpper = model.calculateProbability("HELLO", "WORLD");
double probOriginal = model.calculateProbability("Hello", "world");
assertTrue(probOriginal > 0.0);
assertEquals(0.0, probUpper, 0.0);
}

@Test
public void testCalculateProbabilityWithExcessTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("this", "is", "a", "test");
double prob = model.calculateProbability("this", "is", "a", "test", "sequence");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensFromMiddleOfSequenceProducesNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "a", "test");
String[] prediction = model.predictNextTokens("is", "a");
assertNull(prediction);
}

@Test
public void testPredictionFromLongerPrefixThanNMinusOneReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
String[] prediction = model.predictNextTokens("a", "b", "c", "d");
assertNull(prediction);
}

@Test
public void testStupidBackoffReturnsAdjustedValueWhenNMinusOneNotFound() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "c");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityTransitioningAcrossKnownAndUnknownNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "two", "three");
model.add("three", "four", "five");
double prob = model.calculateProbability("one", "two", "three", "four", "unknown");
assertTrue(prob >= 0.0);
}

@Test
public void testProbabilityCalculationAvoidsNaNWhenModelAlmostEmpty() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("token1", "token2", "token3");
double prob = model.calculateProbability("unknown1", "unknown2", "token3");
assertFalse(Double.isNaN(prob));
}

@Test
public void testConstructorWithInputStreamLoadsMultipleNGrams() throws IOException {
opennlp.tools.ngram.NGramModel base = new opennlp.tools.ngram.NGramModel();
base.add(new StringList("a", "b", "c"), 1, 3);
base.add(new StringList("b", "c", "d"), 1, 3);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(base);
oos.close();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramLanguageModel model = new NGramLanguageModel(in, 3);
assertTrue(model.contains(new StringList("a", "b", "c")));
assertTrue(model.contains(new StringList("b", "c", "d")));
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob > 0.0);
}

@Test
public void testConstructorWithZeroNValueThrowsExceptionBeforeLoadingStream() {
ByteArrayInputStream dummyStream = new ByteArrayInputStream(new byte[0]);
try {
new NGramLanguageModel(dummyStream, 0);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException expected) {
} catch (IOException e) {
fail("Unexpected IOException thrown");
}
}

@Test
public void testCountConsistencyAfterRepeatedAdds() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("dog", "runs", "fast");
model.add("dog", "runs", "fast");
model.add("dog", "runs", "fast");
double prob = model.calculateProbability("dog", "runs", "fast");
assertTrue(prob > 0.0);
}

@Test
public void testBackoffReachesBaseCaseAtUnigram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("deep", "chain", "end");
double prob = model.calculateProbability("x", "y", "z");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testPredictNextTokensWhenModelOnlyHasOneValidPrediction() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("java", "unit", "test");
String[] prediction = model.predictNextTokens("java", "unit");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertEquals("test", prediction[0]);
}

@Test
public void testAddTokensContainingSpecialCharacters() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("(", ")");
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("(", ")")));
double prob = model.calculateProbability("(", ")");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityOfSingleUnigramWithOnlyOneToken() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("token");
double prob = model.calculateProbability("token");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensReturnsNullForEmptyModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] result = model.predictNextTokens("any", "input");
assertNull(result);
}

@Test
public void testAddTokensWithNumericStrings() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("1", "2", "3", "4");
assertTrue(model.contains(new StringList("1", "2", "3")));
assertTrue(model.contains(new StringList("2", "3", "4")));
}

@Test
public void testPredictNextTokensWhenOnlyNMinusOneMatchExists() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b");
model.add("a", "b");
model.add("b", "c", "d");
String[] prediction = model.predictNextTokens("a", "b");
assertEquals(null, prediction);
}

@Test
public void testAddTokensExactSizeNDoesNotSlide() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("apple", "banana", "cherry");
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("apple", "banana", "cherry")));
}

@Test
public void testCalculateProbabilityWithNoMatchingPrefixAtAll() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d", "e");
double prob = model.calculateProbability("x", "y", "z");
assertTrue(prob >= 0.0);
}

@Test
public void testBackoffWhenAllRecursivePathsReturnZero() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("token", "token", "token");
double prob = model.calculateProbability("a", "b", "c");
assertEquals(0.0, prob, 0.0);
}

@Test
public void testStupidBackoffReturnsUnigramWhenNMinusOneIsNull() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("root");
double prob = model.calculateProbability("root");
assertTrue(prob > 0.0);
}

@Test
public void testInputStreamConstructorThrowsIOExceptionOnCorruptStream() {
byte[] invalidBytes = new byte[] { 0x00, 0x01, 0x02 };
ByteArrayInputStream input = new ByteArrayInputStream(invalidBytes);
try {
new NGramLanguageModel(input, 2);
fail("Expected IOException");
} catch (IOException expected) {
}
}

@Test
public void testProbabilityDoesNotThrowWhenModelContainsOnlyOneNGram() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob >= 0.0);
}

@Test
public void testUnigramPredictionSelectsMostFrequentToken() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("a");
model.add("a");
model.add("b");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals("a", result[0]);
}

@Test
public void testAddTokensContainingOnlyNumbers() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("123", "456", "789");
assertTrue(model.contains(new StringList("123", "456")));
assertTrue(model.contains(new StringList("456", "789")));
}

@Test
public void testAddTokensContainingNonAsciiCharacters() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("café", "bøok");
assertTrue(model.contains(new StringList("café", "bøok")));
}

@Test
public void testAddTokensWithMixedCasingDoesNotNormalize() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("Hello", "World");
assertTrue(model.contains(new StringList("Hello", "World")));
assertFalse(model.contains(new StringList("hello", "world")));
}

@Test
public void testAddTokensAndCalculateProbabilityOfExactMatch() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob > 0.0);
}

@Test
public void testAddWithAllEmptyStrings() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "", "", "", "");
assertTrue(model.contains(new StringList("", "")));
}

@Test
public void testEmptyModelPredictReturnsNull() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] result = model.predictNextTokens("nothing", "here");
assertNull(result);
}

@Test
public void testCalculateProbabilityUnseenBigramWhenUnigramExists() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello");
double prob = model.calculateProbability("hello", "world");
assertTrue(prob >= 0.0 && prob <= 1.0);
}

@Test
public void testPredictWithEmptyInputOnPopulatedModelReturnsMostFrequent() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("a");
model.add("a");
model.add("b");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("a", result[0]);
}

@Test
public void testPredictNotNullWhenValidPrefixExists() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("quick", "brown", "fox");
String[] result = model.predictNextTokens("quick", "brown");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("fox", result[0]);
}

@Test
public void testPredictNextTokensOnlyOneCandidateMatch() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("do", "re");
model.add("re", "mi");
String[] result = model.predictNextTokens("re");
assertNotNull(result);
assertEquals("mi", result[0]);
}

@Test
public void testAddWithExactNSizeAndSlideOffEnd() {
NGramLanguageModel model = new NGramLanguageModel(4);
model.add("one", "two", "three", "four");
assertEquals(1, model.size());
}

@Test
public void testProbabilityAfterMultipleSlidingWindowAdds() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("i", "j", "k", "l");
double prob = model.calculateProbability("j", "k");
assertTrue(prob > 0.0);
}
}
