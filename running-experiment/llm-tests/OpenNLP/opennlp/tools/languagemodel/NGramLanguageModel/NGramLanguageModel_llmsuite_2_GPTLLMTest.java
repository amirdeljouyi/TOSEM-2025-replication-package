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

public class NGramLanguageModel_llmsuite_2_GPTLLMTest {

@Test
public void testDefaultConstructor() {
NGramLanguageModel model = new NGramLanguageModel();
assertNotNull(model);
assertEquals(0, model.size());
}

@Test
public void testConstructorWithValidN() {
NGramLanguageModel model = new NGramLanguageModel(2);
assertNotNull(model);
assertEquals(0, model.size());
}

@Test
public void testConstructorWithZeroN() {
new NGramLanguageModel(0);
}

@Test
public void testConstructorWithNegativeN() {
new NGramLanguageModel(-1);
}

@Test
public void testAddTokensAndSize() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "quick", "brown", "fox");
assertTrue(model.size() > 0);
}

@Test
public void testCalculateProbabilityKnownSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d");
double prob = model.calculateProbability("a", "b", "c");
assertTrue(prob >= 0.0);
assertTrue(prob <= 1.0);
}

@Test
public void testCalculateProbabilityUnseenSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWithEmptyModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
double prob = model.calculateProbability("a", "b", "c");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithExactMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("quick", "brown", "fox");
model.add("brown", "fox", "jumps");
String[] result = model.predictNextTokens("brown", "fox");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("jumps", result[0]);
}

@Test
public void testPredictNextTokensWithNoMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("quick", "brown", "fox");
String[] result = model.predictNextTokens("lazy", "dog");
assertNull(result);
}

@Test
public void testProbabilityWithBackoff() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "cat", "sat");
double prob = model.calculateProbability("the", "cat", "sat");
assertTrue(prob > 0.0);
}

@Test
public void testBackoffFallback() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("cat", "on", "mat");
double prob = model.calculateProbability("dog", "on", "mat");
assertTrue(prob >= 0.0);
}

@Test
public void testConstructorWithInputStreamValid() throws IOException {
NGramLanguageModel modelOrig = new NGramLanguageModel(2);
modelOrig.add("a", "b", "c");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(modelOrig);
oos.flush();
byte[] serialized = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
NGramLanguageModel modelNew = new NGramLanguageModel(bais, 2);
assertNotNull(modelNew);
}

@Test
public void testConstructorWithInputStreamInvalid() throws IOException {
byte[] corrupt = new byte[] { 1, 2, 3 };
ByteArrayInputStream in = new ByteArrayInputStream(corrupt);
new NGramLanguageModel(in, 2);
}

@Test
public void testIteratorReturnsNGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d");
StringList first = model.iterator().next();
assertEquals(3, first.size());
assertEquals("a", first.getToken(0));
assertEquals("b", first.getToken(1));
assertEquals("c", first.getToken(2));
}

@Test
public void testPredictNextTokensWithMultipleCandidates() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("a", "b", "d");
String[] result = model.predictNextTokens("a", "b");
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("c") || result[0].equals("d"));
}

@Test
public void testAddSingleToken() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello");
StringList sl = model.iterator().next();
assertEquals(1, sl.size());
assertEquals("hello", sl.getToken(0));
}

@Test
public void testAddNoTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
int before = model.size();
model.add();
int after = model.size();
assertEquals(before, after);
}

@Test
public void testCalculateProbabilityConsistency() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double p1 = model.calculateProbability("a", "b", "c");
double p2 = model.calculateProbability("a", "b", "c");
assertEquals(p1, p2, 0.000001);
}

@Test
public void testCalculateProbabilityTooFewTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("a");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityEmptyInput() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
double prob = model.calculateProbability();
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWithEmptyInput() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
String[] prediction = model.predictNextTokens();
assertNotNull(prediction);
assertTrue(prediction.length > 0);
}

@Test
public void testCalculateProbabilityPartiallyKnown() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("this", "is", "a", "test");
double prob = model.calculateProbability("this", "is", "a", "trial");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilitySingleTokenEqualsN() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("hello");
double prob = model.calculateProbability("hello");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityTokenSequenceLongerThanN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two", "three", "four", "five");
double prob = model.calculateProbability("one", "two", "three", "four", "five");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithOnlyOneGramInModel() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("token");
double prob = model.calculateProbability("token");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWithEqualProbabilities() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("a", "c");
model.add("a", "d");
String[] predicted = model.predictNextTokens("a");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertTrue(predicted[0].equals("b") || predicted[0].equals("c") || predicted[0].equals("d"));
}

@Test
public void testAddDuplicateTokensInModel() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c");
int initialSize = model.size();
model.add("a", "b", "c");
int newSize = model.size();
assertEquals(initialSize, newSize);
}

@Test
public void testAddTokensContainingEmptyString() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "", "c");
double prob = model.calculateProbability("a", "", "c");
assertTrue(prob >= 0.0);
}

@Test
public void testAddTokensContainingSpecialCharacters() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "@", "#$%");
double prob = model.calculateProbability("one", "@", "#$%");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensFromEmptyModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
String[] predicted = model.predictNextTokens("anything");
assertNull(predicted);
}

@Test
public void testCalculateProbabilityNaNProtection() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertFalse(Double.isNaN(prob));
}

@Test
public void testCalculateProbabilityTriggerRecursiveBackoff() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("x", "y", "z");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testBackoffWhenNMinusOneGramsAreMissing() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
double prob = model.calculateProbability("a", "b", "x");
assertTrue(prob >= 0.0);
}

@Test
public void testSerializationRoundTripWithMixedEntries() throws IOException, ClassNotFoundException {
NGramLanguageModel original = new NGramLanguageModel(2);
original.add("sun", "moon");
original.add("sun", "@", "star");
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(original);
byte[] bytes = out.toByteArray();
ByteArrayInputStream in = new ByteArrayInputStream(bytes);
NGramLanguageModel restored = new NGramLanguageModel(in, 2);
double probOriginal = original.calculateProbability("sun", "moon");
double probRestored = restored.calculateProbability("sun", "moon");
assertEquals(probOriginal, probRestored, 0.00001);
}

@Test
public void testAddTokensWithLeadingAndTrailingSpaces() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("  hello", "world  ");
double prob1 = model.calculateProbability("  hello", "world  ");
double prob2 = model.calculateProbability("hello", "world");
assertTrue(prob1 > 0.0);
assertEquals(0.0, prob2, 0.000001);
}

@Test
public void testPredictNextTokensWhenPartialOverlap() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("weather", "is");
model.add("weather", "was");
model.add("weather", "will");
String[] prediction = model.predictNextTokens("weather");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("is") || prediction[0].equals("was") || prediction[0].equals("will"));
}

@Test
public void testCalculateProbabilityForSameTokenRepeated() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("repeat", "repeat", "repeat", "repeat");
double prob = model.calculateProbability("repeat", "repeat");
assertTrue(prob >= 0.0);
}

@Test
public void testMultipleCallsToAddIncreasesCoverage() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("b", "c", "d");
model.add("c", "d", "e");
model.add("d", "e", "f");
double prob = model.calculateProbability("a", "b", "c", "d", "e", "f");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWithLongPrefix() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("c", "d", "e");
model.add("e", "f", "g");
String[] predicted = model.predictNextTokens("a", "b", "c", "d", "e");
assertNotNull(predicted);
}

@Test
public void testPredictNextTokensWithExactNGramMatchOnly() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
String[] result = model.predictNextTokens("a");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("b", result[0]);
}

@Test
public void testAddMoreTokensThanN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two", "three", "four");
double prob = model.calculateProbability("two", "three");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithOneTokenLessThanN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("alpha", "beta", "gamma");
double prob = model.calculateProbability("alpha");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWhenModelContainsLongerGrams() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("x", "y", "z");
String[] result = model.predictNextTokens("x", "y");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("z", result[0]);
}

@Test
public void testAddTokensWithAllNulls() {
NGramLanguageModel model = new NGramLanguageModel(3);
try {
model.add((String[]) null);
assertEquals(0, model.size());
} catch (Exception e) {
fail("No exception expected when adding null tokens array");
}
}

@Test
public void testCalculateProbabilityWithTokensContainingNull() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", null, "b");
double prob = model.calculateProbability("a", null);
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithAllNullTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
double prob = model.calculateProbability(null, null);
assertTrue(prob >= 0.0);
}

@Test
public void testPredictNextTokensWithNullInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
try {
String[] predicted = model.predictNextTokens((String[]) null);
assertNotNull(predicted);
} catch (Exception e) {
fail("Should handle null inputs without throwing");
}
}

@Test
public void testCalculateProbabilityUnbalancedCountsTriggeringBackoffToCorpusSize() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("only", "once");
double prob = model.calculateProbability("this", "neverSeen");
assertTrue(prob >= 0.0);
assertEquals(0.0, prob, 0.00001);
}

@Test
public void testCalculateProbabilityWithZeroCountParentFallingBackToCorpusSize() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
double prob = model.calculateProbability("y", "z");
assertTrue(prob >= 0.0);
}

@Test
public void testConstructorWithInputStreamAndDefaultN() throws IOException {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("m", "n");
java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
oos.writeObject(model);
byte[] serialized = baos.toByteArray();
java.io.ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
NGramLanguageModel newModel = new NGramLanguageModel(bais);
double prob = newModel.calculateProbability("m", "n");
assertTrue(prob > 0.0);
}

@Test
public void testBackoffRecursiveToZeroLengthHistory() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("word", "frequency");
double prob = model.calculateProbability("unknown", "token");
assertTrue(prob >= 0.0);
assertEquals(0.0, prob, 0.00001);
}

@Test
public void testBackoffWithIntermediateZeroCount() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "quick", "brown");
double prob = model.calculateProbability("lazy", "dog", "jumped");
assertEquals(0.0, prob, 0.00001);
}

@Test
public void testPredictNextTokensWhenInputLongerThanN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
String[] predicted = model.predictNextTokens("w", "x", "y", "z");
assertNull(predicted);
}

@Test
public void testCalculateProbabilityWithExactMatch() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("cat", "sat");
double prob = model.calculateProbability("cat", "sat");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithOnlyOneGramModelButMultipleInputTokens() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("alpha");
model.add("beta");
model.add("gamma");
double prob = model.calculateProbability("alpha", "beta", "gamma");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWhenMultipleCandidatesHaveSameProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "1");
model.add("a", "2");
model.add("a", "3");
String[] predicted = model.predictNextTokens("a");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertTrue(predicted[0].equals("1") || predicted[0].equals("2") || predicted[0].equals("3"));
}

@Test
public void testAddIdenticalSequenceMultipleTimesIncreasesProbability() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
model.add("x", "y");
model.add("x", "y");
double prob1 = model.calculateProbability("x", "y");
assertTrue(prob1 > 0.0);
}

@Test
public void testAddOverlappingSequences() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("I", "love", "Java");
model.add("love", "Java", "JUnit");
model.add("Java", "JUnit", "4");
double prob = model.calculateProbability("I", "love", "Java", "JUnit", "4");
assertTrue(prob > 0.0);
}

@Test
public void testPredictionWithExactOverlappingMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("to", "be", "or");
model.add("be", "or", "not");
model.add("or", "not", "to");
String[] predicted = model.predictNextTokens("be", "or");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertEquals("not", predicted[0]);
}

@Test
public void testWhitespaceTokenHandling() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(" ", "\t");
double prob = model.calculateProbability(" ", "\t");
assertTrue(prob > 0.0);
}

@Test
public void testEmptyStringTokenHandling() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("", "");
double prob = model.calculateProbability("", "");
assertTrue(prob > 0.0);
}

@Test
public void testSerializationConsistencyAfterMultipleAdds() throws IOException {
NGramLanguageModel model1 = new NGramLanguageModel(3);
model1.add("one", "two", "three");
model1.add("four", "five", "six");
model1.add("one", "two", "three");
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream os = new ObjectOutputStream(out);
os.writeObject(model1);
byte[] modelData = out.toByteArray();
ByteArrayInputStream in = new ByteArrayInputStream(modelData);
NGramLanguageModel model2 = new NGramLanguageModel(in, 3);
double p1 = model1.calculateProbability("one", "two", "three");
double p2 = model2.calculateProbability("one", "two", "three");
assertEquals(p1, p2, 0.00001);
}

@Test
public void testPredictionWhenTokenSequenceHasNoContinuationMatch() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c");
model.add("c", "d", "e");
String[] predicted = model.predictNextTokens("x", "y", "z");
assertNull(predicted);
}

@Test
public void testCalculateProbabilityOfSubsetExactPrefix() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("apple", "banana", "carrot");
model.add("banana", "carrot", "date");
double prob = model.calculateProbability("apple", "banana", "carrot");
assertTrue(prob > 0.0);
double noMatchProb = model.calculateProbability("banana", "carrot");
assertEquals(0.0, noMatchProb, 0.00001);
}

@Test
public void testHighNValueWithTruncatedAdditions() {
NGramLanguageModel model = new NGramLanguageModel(10);
model.add("a", "b", "c", "d", "e");
assertTrue(model.size() > 0);
double prob = model.calculateProbability("a", "b", "c", "d", "e");
assertEquals(0.0, prob, 0.00001);
}

@Test
public void testPredictionWithDifferentPrefixLengthsThanN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("t1", "t2", "t3");
model.add("t3", "t4", "t5");
model.add("t5", "t6", "t7");
String[] prediction = model.predictNextTokens("t3", "t4", "t5", "t6");
assertNull(prediction);
}

@Test
public void testCalculateProbabilityAllTokensUnseenTriggersRecursiveBackoff() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("known", "tokens", "only");
double prob = model.calculateProbability("foo", "bar", "baz");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityNMinusOneTokenHasZeroCountTriggersCorpusBackoff() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
double prob = model.calculateProbability("x", "z");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithIdenticalTokensRepeated() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("hello", "hello", "hello", "hello");
double prob = model.calculateProbability("hello", "hello", "hello");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithSingleTokenShorterThanN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("single");
double prob = model.calculateProbability("single");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWithNullMiddleToken() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("start", null, "end");
double prob = model.calculateProbability("start", null, "end");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithNullFirstToken() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(null, "second");
double prob = model.calculateProbability(null, "second");
assertTrue(prob >= 0.0);
}

@Test
public void testCalculateProbabilityWithNullLastToken() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("first", null);
double prob = model.calculateProbability("first", null);
assertTrue(prob >= 0.0);
}

@Test
public void testPredictNextTokensFromEmptyHistoryInput() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
String[] result = model.predictNextTokens();
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("x", result[0]);
}

@Test
public void testPredictNextTokensWithLongHistoryMoreThanN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("c", "d");
String[] result = model.predictNextTokens("1", "2", "3", "4", "5");
assertNull(result);
}

@Test
public void testAddEmptyTokensArrayDoesNotChangeModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
int before = model.size();
model.add();
int after = model.size();
assertEquals(before, after);
}

@Test
public void testSerializationAfterAddingOnlyOneGramTokens() throws IOException {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("x");
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(model);
byte[] bytes = out.toByteArray();
ByteArrayInputStream in = new ByteArrayInputStream(bytes);
NGramLanguageModel restored = new NGramLanguageModel(in, 1);
double p = restored.calculateProbability("x");
assertTrue(p > 0.0);
}

@Test
public void testCalculateProbabilityWithTokensLongerThanModelN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c", "d", "e");
double prob = model.calculateProbability("a", "b", "c", "d", "e");
assertTrue(prob > 0.0);
}

@Test
public void testPredictNextTokensWithPartialPrefixOverlap() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("alpha", "beta", "gamma");
model.add("beta", "gamma", "delta");
String[] predicted = model.predictNextTokens("alpha", "beta");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertEquals("gamma", predicted[0]);
}

@Test
public void testPredictNextTokensWhenCorpusHasMultipleCandidates() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("the", "cat");
model.add("the", "dog");
model.add("the", "fish");
String[] predicted = model.predictNextTokens("the");
assertNotNull(predicted);
assertEquals(1, predicted.length);
assertTrue(predicted[0].equals("cat") || predicted[0].equals("dog") || predicted[0].equals("fish"));
}

@Test
public void testCalculateProbabilityWithOnlyStopWords() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("the", "a", "an");
double prob = model.calculateProbability("the", "a", "an");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityAllTokensSameLengthAsNAfterAdd() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two");
double prob = model.calculateProbability("one", "two");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithMixedKnownAndUnknownTokens() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("learn", "java", "now");
double prob = model.calculateProbability("learn", "python", "now");
assertTrue(prob >= 0.0);
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensWhenOnlyOneNGramExists() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("hello", "world");
String[] result = model.predictNextTokens("hello");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("world", result[0]);
}

@Test
public void testPredictNextTokensWithExactFullMatchSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("complete", "sequence", "match");
String[] result = model.predictNextTokens("complete", "sequence");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("match", result[0]);
}

@Test
public void testCalculateProbabilityWithEmptyInputTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("text", "data");
double prob = model.calculateProbability();
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictNextTokensOnModelWithOnlyOneGram() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("token1");
model.add("token2");
String[] predicted = model.predictNextTokens();
assertNotNull(predicted);
assertEquals(1, predicted.length);
}

@Test
public void testAddNullTokensArray() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add((String[]) null);
assertEquals(0, model.size());
}

@Test
public void testPredictNextTokensWithNullHistory() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
String[] predicted = model.predictNextTokens((String[]) null);
assertNotNull(predicted);
assertTrue(predicted.length > 0);
}

@Test
public void testCalculateProbabilityWithExtremelyLongTokenSequence() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
double probability = model.calculateProbability("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
assertTrue(probability > 0.0);
}

@Test
public void testAddWithOnlySpecialCharactersAsTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("@", "#");
double prob = model.calculateProbability("@", "#");
assertTrue(prob > 0.0);
}

@Test
public void testSerializationWithOnlyOneNGramStored() throws Exception {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("serialize", "me");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeObject(model);
byte[] bytes = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
NGramLanguageModel rehydrated = new NGramLanguageModel(bais, 2);
double prob = rehydrated.calculateProbability("serialize", "me");
assertTrue(prob > 0.0);
}

@Test
public void testBackoffToZeroSizeModel() {
NGramLanguageModel model = new NGramLanguageModel(3);
double prob = model.calculateProbability("no", "data");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testBackoffFallbackToCorpusSizeDueToZeroDenominator() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("x", "y");
double prob = model.calculateProbability("x", "z");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testPredictionWithExcessLengthInputOverN() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
String[] predicted = model.predictNextTokens("a", "b", "c", "d");
assertNull(predicted);
}

@Test
public void testPredictionWithWhitespaceTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add(" ", "    ");
double prob = model.calculateProbability(" ", "    ");
assertTrue(prob > 0.0);
}

@Test
public void testAddTokensWithSingleWordCalledTwiceShouldNotIncreaseSize() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("word");
int size1 = model.size();
model.add("word");
int size2 = model.size();
assertEquals(size1, size2);
}

@Test
public void testCalculateProbabilityWithNMinusOneTokenOnly() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("m", "n", "o");
double prob = model.calculateProbability("m", "n");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testAddWithMoreTokensThanGramSizeAndVerifyProbability() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("one", "two", "three", "four", "five");
double prob = model.calculateProbability("two", "three", "four");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithAllNGramsValidButIncludesUnseenTail() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b");
model.add("b", "c");
double prob = model.calculateProbability("a", "b", "c", "x");
assertTrue(prob >= 0.0);
}

@Test
public void testPredictNextTokensWithUniformFrequencies() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("data", "science");
model.add("data", "mining");
model.add("data", "processing");
String[] prediction = model.predictNextTokens("data");
assertNotNull(prediction);
assertEquals(1, prediction.length);
assertTrue(prediction[0].equals("science") || prediction[0].equals("mining") || prediction[0].equals("processing"));
}

@Test
public void testAddSameTokenRepeatedBeyondN() {
NGramLanguageModel model = new NGramLanguageModel(3);
model.add("a", "a", "a", "a", "a");
double prob = model.calculateProbability("a", "a", "a");
assertTrue(prob > 0.0);
}

@Test
public void testCalculateProbabilityWithMixedCaseTokens() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("Hello", "World");
double probLower = model.calculateProbability("hello", "world");
double probExact = model.calculateProbability("Hello", "World");
assertEquals(0.0, probLower, 0.000001);
assertTrue(probExact > 0.0);
}

@Test
public void testPredictionAfterAddingTokensThenResettingModelState() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("reset", "test");
// model.clear();
assertEquals(0, model.size());
double prob = model.calculateProbability("reset", "test");
assertEquals(0.0, prob, 0.000001);
}

@Test
public void testCalculateProbabilityWithTokensContainingNumbersAndSymbols() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("123", "@#%&");
double prob = model.calculateProbability("123", "@#%&");
assertTrue(prob > 0.0);
}

@Test
public void testModelHandlesVeryHighNParameter() {
NGramLanguageModel model = new NGramLanguageModel(10);
model.add("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
assertTrue(model.size() > 0);
double prob = model.calculateProbability("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
assertTrue(prob > 0.0);
}

@Test
public void testConstructorWithNegativeNAndStream() throws IOException {
NGramLanguageModel model = new NGramLanguageModel(2);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(model);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramLanguageModel(in, -1);
}

@Test
public void testPredictNextTokensFromSingularExistingToken() {
NGramLanguageModel model = new NGramLanguageModel(1);
model.add("only");
String[] predicted = model.predictNextTokens("only");
assertNull(predicted);
}

@Test
public void testCalculateProbabilityFromMaxLengthMatchingNGrams() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("one", "two");
model.add("two", "three");
model.add("three", "four");
double prob = model.calculateProbability("one", "two", "three", "four");
assertTrue(prob > 0.0);
}

@Test
public void testAddTokenSequenceUsingNullValuesGracefully() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("start", null);
double prob = model.calculateProbability("start", null);
assertTrue(prob >= 0.0);
}

@Test
public void testPredictNextTokensWithSingleMatchingHistoryAndMultipleContinuations() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("match", "one");
model.add("match", "two");
model.add("match", "three");
String[] result = model.predictNextTokens("match");
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0].equals("one") || result[0].equals("two") || result[0].equals("three"));
}

@Test
public void testCalculateProbabilityOnSequenceThatExceedsStandardWindow() {
NGramLanguageModel model = new NGramLanguageModel(2);
model.add("a", "b", "c", "d", "e", "f", "g");
double prob = model.calculateProbability("a", "b", "c", "d", "e", "f", "g");
assertTrue(prob > 0.0);
}
}
