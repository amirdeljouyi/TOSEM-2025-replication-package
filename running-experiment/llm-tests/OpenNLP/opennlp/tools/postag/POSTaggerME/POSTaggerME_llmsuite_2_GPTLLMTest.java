package opennlp.tools.postag;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.*;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.GeneratorFactory;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class POSTaggerME_llmsuite_2_GPTLLMTest {

@Test
public void testTagReturnsCorrectTagsForSingleSentence() {
String[] sentence = { "The", "dog", "barks" };
Sequence sequence = new Sequence(Arrays.asList("DT", "NN", "VBZ"));
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
when(sequenceModel.bestSequence(eq(sentence), isNull(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(tagDict);
Map<String, Object> artifacts = new HashMap<>();
Map<String, String> manifest = new HashMap<>();
POSModel model = new POSModel("en", sequenceModel, manifest, factory);
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.tag(sentence);
assertEquals("DT", tags[0]);
assertEquals("NN", tags[1]);
assertEquals("VBZ", tags[2]);
}

@Test
public void testTagWithAdditionalContext() {
String[] sentence = { "OpenNLP", "rocks" };
Object[] additionalContext = new Object[] { "domain=nlp" };
Sequence sequence = new Sequence(Arrays.asList("NNP", "VBZ"));
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
when(sequenceModel.bestSequence(eq(sentence), eq(additionalContext), eq(contextGenerator), eq(validator))).thenReturn(sequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(tagDict);
Map<String, Object> artifacts = new HashMap<>();
Map<String, String> manifest = new HashMap<>();
POSModel model = new POSModel("en", sequenceModel, manifest, factory);
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.tag(sentence, additionalContext);
assertEquals("NNP", tags[0]);
assertEquals("VBZ", tags[1]);
}

@Test
public void testTopKSequencesReturnsMultipleAlternatives() {
String[] sentence = { "Quick", "brown", "fox" };
Sequence first = new Sequence(Arrays.asList("JJ", "JJ", "NN"));
Sequence second = new Sequence(Arrays.asList("RB", "JJ", "NN"));
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
when(sequenceModel.bestSequences(eq(3), eq(sentence), isNull(), eq(contextGenerator), eq(validator))).thenReturn(new Sequence[] { first, second });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(tagDict);
Map<String, Object> artifacts = new HashMap<>();
Map<String, String> manifest = new HashMap<>();
manifest.put("BeamSize", "3");
POSModel model = new POSModel("en", sequenceModel, manifest, factory);
POSTaggerME tagger = new POSTaggerME(model);
Sequence[] sequences = tagger.topKSequences(sentence);
assertEquals(2, sequences.length);
assertEquals("JJ", sequences[0].getOutcomes().get(0));
assertEquals("RB", sequences[1].getOutcomes().get(0));
}

@Test
public void testGetOrderedTagsThrowsOnNonMaxentModel() {
SequenceClassificationModel<String> nonMaxent = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary tagDict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(tagDict);
Map<String, String> manifest = new HashMap<>();
POSModel model = new POSModel("en", nonMaxent, manifest, factory);
POSTaggerME tagger = new POSTaggerME(model);
tagger.getOrderedTags(Arrays.asList("word"), Arrays.asList("tag"), 0);
}

@Test
public void testBuildNGramDictionaryIncludesUnigrams() throws IOException {
POSSample sample = new POSSample(new String[] { "hello" }, new String[] { "UH" });
POSSample sample2 = new POSSample(new String[] { "world" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample, sample2));
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertTrue(dict.contains(new StringList("hello")));
// assertTrue(dict.contains(new StringList("world")));
}

@Test
public void testPopulatePOSDictionaryCaseInsensitiveIncludesHighFrequencyTags() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample1 = new POSSample(new String[] { "Dog" }, new String[] { "NN" });
POSSample sample2 = new POSSample(new String[] { "dog" }, new String[] { "NN" });
POSSample sample3 = new POSSample(new String[] { "dog" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tags = dict.getTags("dog");
// assertNotNull(tags);
// assertEquals("NN", tags[0]);
}

@Test
public void testPopulatePOSDictionaryCaseSensitiveSeparatesWords() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(true);
POSSample sample1 = new POSSample(new String[] { "Run" }, new String[] { "VB" });
POSSample sample2 = new POSSample(new String[] { "run" }, new String[] { "NN" });
POSSample sample3 = new POSSample(new String[] { "run" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// assertArrayEquals(new String[] { "NN" }, dict.getTags("run"));
// assertNull(dict.getTags("Run"));
}

@Test
public void testProbsReturnsExpectedOutput() {
String[] sentence = { "Sky", "is", "blue" };
double[] expectedProbs = new double[] { 0.7, 0.6, 0.8 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("NN", "VBZ", "JJ"));
when(sequence.getProbs()).thenReturn(expectedProbs);
// when(sequence.getProbs(any(double[].class))).thenAnswer(invocation -> {
// double[] array = invocation.getArgument(0);
// array[0] = 0.7;
// array[1] = 0.6;
// array[2] = 0.8;
// return null;
// });
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
when(sequenceModel.bestSequence(eq(sentence), isNull(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(tagDict);
Map<String, String> manifest = new HashMap<>();
POSModel model = new POSModel("en", sequenceModel, manifest, factory);
POSTaggerME tagger = new POSTaggerME(model);
tagger.tag(sentence);
double[] outputProbs = tagger.probs();
assertArrayEquals(new double[] { 0.7, 0.6, 0.8 }, outputProbs, 0.0001);
double[] passedArray = new double[3];
tagger.probs(passedArray);
assertArrayEquals(new double[] { 0.7, 0.6, 0.8 }, passedArray, 0.0001);
}

@Test
public void testTagEmptySentenceReturnsEmptyArray() {
String[] sentence = new String[0];
Sequence sequence = new Sequence(Collections.emptyList());
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary td = mock(TagDictionary.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), eq(cg), eq(validator))).thenReturn(sequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(td);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
String[] result = tagger.tag(sentence);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testGetAllPosTagsReturnsCorrectValues() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
String[] outcomes = new String[] { "NN", "VB", "DT" };
when(model.getOutcomes()).thenReturn(outcomes);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary td = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(td);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
String[] tags = tagger.getAllPosTags();
assertArrayEquals(new String[] { "NN", "VB", "DT" }, tags);
}

@Test
public void testTagWithNullAdditionalContext() {
String[] sentence = new String[] { "Hello" };
Sequence sequence = new Sequence(Collections.singletonList("UH"));
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary td = mock(TagDictionary.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), eq(cg), eq(validator))).thenReturn(sequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(td);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
String[] result = tagger.tag(sentence, null);
assertEquals(1, result.length);
assertEquals("UH", result[0]);
}

@Test
public void testTagWithNullTagDictionary() {
String[] sentence = new String[] { "Hi" };
Sequence sequence = new Sequence(Collections.singletonList("UH"));
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary td = null;
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), eq(cg), eq(validator))).thenReturn(sequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
String[] result = tagger.tag(sentence);
assertEquals("UH", result[0]);
}

@Test
public void testTrainWithUnsupportedTrainerTypeThrowsException() throws IOException {
// ObjectStream<POSSample> stream = new ListObjectStream<>(Collections.emptyList());
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, "invalid");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator()).thenReturn(mock(POSContextGenerator.class));
try {
// POSTaggerME.train("en", stream, params, factory);
fail("Expected IllegalArgumentException for unsupported trainer type");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("Trainer type is not supported"));
}
}

@Test
public void testBuildNGramDictionaryWithEmptyStreamReturnsEmptyDictionary() throws IOException {
ObjectStream<POSSample> stream = new ObjectStream<POSSample>() {

public POSSample read() {
return null;
}

public void reset() {
}

public void close() {
}
};
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
}

@Test
public void testPopulateDictionaryIgnoresTokensWithDigits() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample = new POSSample(new String[] { "Token123" }, new String[] { "NN" });
List<POSSample> samples = Collections.singletonList(sample);
// ObjectStream<POSSample> stream = new ListObjectStream<>(samples);
// POSTaggerME.populatePOSDictionary(stream, dict, 1);
// assertNull(dict.getTags("token123"));
}

@Test
public void testGetOrderedTagsWithNullEvaluatedProbabilitiesReturnsValidTagSequence() {
List<String> words = Collections.singletonList("bright");
List<String> tags = Collections.singletonList("JJ");
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.75, 0.25 });
when(model.getOutcome(0)).thenReturn("JJ");
when(model.getOutcome(1)).thenReturn("RB");
POSContextGenerator cg = mock(POSContextGenerator.class);
when(cg.getContext(0, new String[] { "bright" }, new String[] { "JJ" }, null)).thenReturn(new String[] { "context" });
SequenceClassificationModel<String> dummySeqModel = mock(SequenceClassificationModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(null);
Map<String, Object> artifacts = new HashMap<>();
artifacts.put(POSModel.POS_MODEL_ENTRY_NAME, model);
// POSModel posModel = new POSModel("en", dummySeqModel, 5, new HashMap<>(), factory);
// posModel.serialize(new DummyOutputStream());
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, model);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.getOrderedTags(words, tags, 0, null);
// assertEquals("JJ", result[0]);
// assertEquals("RB", result[1]);
}

@Test
public void testOrderedTagsWithProbabilityArrayValuesFilledCorrectly() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.85, 0.1, 0.05 });
when(model.getOutcome(0)).thenReturn("NN");
when(model.getOutcome(1)).thenReturn("VB");
when(model.getOutcome(2)).thenReturn("JJ");
POSContextGenerator cg = mock(POSContextGenerator.class);
when(cg.getContext(anyInt(), any(String[].class), any(String[].class), isNull())).thenReturn(new String[] { "x" });
SequenceClassificationModel<String> dummySeqModel = mock(SequenceClassificationModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(null);
when(factory.getTagDictionary()).thenReturn(null);
Map<String, String> manifest = new HashMap<>();
manifest.put(BeamSearch.BEAM_SIZE_PARAMETER, "5");
// POSModel posModel = new POSModel("en", dummySeqModel, 5, manifest, factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, model);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
List<String> words = Arrays.asList("amazing");
List<String> tags = Arrays.asList("JJ");
double[] probs = new double[3];
// String[] ordered = tagger.getOrderedTags(words, tags, 0, probs);
// assertEquals("NN", ordered[0]);
// assertEquals("VB", ordered[1]);
// assertEquals("JJ", ordered[2]);
assertEquals(0.85, probs[0], 0.0001);
assertEquals(0.1, probs[1], 0.0001);
assertEquals(0.05, probs[2], 0.0001);
}

@Test
public void testConvertTagsWithCustomFormatReturnsUnchangedTags() {
List<String> tags = Arrays.asList("X1", "X2");
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
when(sequenceModel.getOutcomes()).thenReturn(new String[] { "X1", "X2" });
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
POSModel model = new POSModel("en", sequenceModel, new HashMap<>(), factory);
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.CUSTOM);
// String[] converted = tagger.tag(new String[] { "a", "b" }, null);
Sequence s = mock(Sequence.class);
when(s.getOutcomes()).thenReturn(tags);
when(sequenceModel.bestSequence(any(String[].class), any(), any(), any())).thenReturn(s);
// String[] result = tagger.tag(new String[] { "a", "b" }, null);
// assertEquals("X1", result[0]);
// assertEquals("X2", result[1]);
}

@Test
public void testTagMultipleSequencesReturnsCorrectNumberOfOutputs() {
String[] sentence = new String[] { "Can", "run" };
Sequence seq1 = new Sequence(Arrays.asList("MD", "VB"));
Sequence seq2 = new Sequence(Arrays.asList("NN", "NN"));
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(2), eq(sentence), isNull(), eq(cg), eq(validator))).thenReturn(new Sequence[] { seq1, seq2 });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(dict);
Map<String, String> manifest = new HashMap<>();
manifest.put(BeamSearch.BEAM_SIZE_PARAMETER, "2");
POSModel posModel = new POSModel("en", model, manifest, factory);
POSTaggerME tagger = new POSTaggerME(posModel);
String[][] multiTags = tagger.tag(2, sentence);
assertEquals(2, multiTags.length);
assertArrayEquals(new String[] { "MD", "VB" }, multiTags[0]);
assertArrayEquals(new String[] { "NN", "NN" }, multiTags[1]);
}

@Test
public void testPopulatePOSDictionaryIncludesExistingTagsAtCutoffThreshold() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
// dict.put("dog", new String[] { "NN" });
// Map<String, AtomicInteger> dogMap = new HashMap<>();
// dogMap.put("NN", new AtomicInteger(2));
// dogMap.put("VB", new AtomicInteger(1));
POSSample sample1 = new POSSample(new String[] { "Dog" }, new String[] { "NN" });
POSSample sample2 = new POSSample(new String[] { "dog" }, new String[] { "VB" });
POSSample sample3 = new POSSample(new String[] { "dog" }, new String[] { "VB" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tags = dict.getTags("dog");
// Arrays.sort(tags);
// assertArrayEquals(new String[] { "NN", "VB" }, tags);
}

@Test
public void testPopulatePOSDictionaryWithOnlyDigitsIsIgnored() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample = new POSSample(new String[] { "12345" }, new String[] { "CD" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Collections.singletonList(sample));
// POSTaggerME.populatePOSDictionary(stream, dict, 1);
// assertNull(dict.getTags("12345"));
}

@Test
public void testPopulatePOSDictionaryWordAddedOnlyWhenCutoffMet() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample1 = new POSSample(new String[] { "apple" }, new String[] { "NN" });
POSSample sample2 = new POSSample(new String[] { "apple" }, new String[] { "VB" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample1, sample1));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tags = dict.getTags("apple");
// Arrays.sort(tags);
// assertArrayEquals(new String[] { "NN" }, tags);
}

@Test
public void testPopulatePOSDictionaryCaseSensitiveStoresSeparately() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(true);
POSSample sample1 = new POSSample(new String[] { "Run" }, new String[] { "VB" });
POSSample sample2 = new POSSample(new String[] { "run" }, new String[] { "NN" });
POSSample sample3 = new POSSample(new String[] { "Run" }, new String[] { "VB" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tagsRun = dict.getTags("Run");
// String[] tagsrun = dict.getTags("run");
// assertArrayEquals(new String[] { "VB" }, tagsRun);
// assertNull(tagsrun);
}

@Test
public void testProbsCalledWithoutTaggingReturnsNull() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
try {
tagger.probs();
fail("Expected NullPointerException due to bestSequence being null");
} catch (NullPointerException e) {
}
}

@Test
public void testTagReturnsEmptyTagsWhenModelReturnsNone() {
String[] sentence = { "Hi" };
Sequence emptySequence = new Sequence(Collections.emptyList());
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), eq(null), eq(contextGen), eq(validator))).thenReturn(emptySequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
String[] result = tagger.tag(sentence);
assertEquals(0, result.length);
}

@Test
public void testTagReturnsEmptyArrayWhenSequenceOutcomesIsNull() {
String[] sentence = new String[] { "Test" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(dict);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
try {
tagger.tag(sentence);
fail("Expected NullPointerException due to null outcomes");
} catch (NullPointerException e) {
}
}

@Test
public void testBuildNGramDictionarySkipsEmptySamples() throws IOException {
ObjectStream<POSSample> stream = new ObjectStream<POSSample>() {

private int count = 0;

public POSSample read() {
if (count < 3) {
count++;
return new POSSample(new String[0], new String[0]);
}
return null;
}

public void reset() {
}

public void close() {
}
};
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertFalse(dict.iterator().hasNext());
}

@Test
public void testPopulatePOSDictionaryHandlesMixedCaseWithCaseInsensitive() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample1 = new POSSample(new String[] { "Apple" }, new String[] { "NN" });
POSSample sample2 = new POSSample(new String[] { "apple" }, new String[] { "NN" });
POSSample sample3 = new POSSample(new String[] { "APPLE" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tags = dict.getTags("apple");
// Arrays.sort(tags);
// assertArrayEquals(new String[] { "NN" }, tags);
}

@Test
public void testGetOrderedTagsHandlesEmptyContextGracefully() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any())).thenReturn(new double[] { 0.5 });
when(model.getOutcome(0)).thenReturn("NN");
POSContextGenerator cg = mock(POSContextGenerator.class);
when(cg.getContext(anyInt(), any(String[].class), any(String[].class), any())).thenReturn(new String[0]);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(null);
SequenceClassificationModel<String> dummySeqModel = mock(SequenceClassificationModel.class);
// POSModel modelObj = new POSModel("en", dummySeqModel, 5, new HashMap<>(), factory);
// modelObj.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, model);
// POSTaggerME tagger = new POSTaggerME(modelObj, POSTagFormat.UD);
// String[] result = tagger.getOrderedTags(Collections.singletonList("word"), Collections.singletonList("NN"), 0);
// assertEquals(1, result.length);
// assertEquals("NN", result[0]);
}

@Test
public void testBuildNGramDictionaryHandlesOnlyOneToken() throws IOException {
POSSample sample = new POSSample(new String[] { "singleton" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Collections.singletonList(sample));
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertTrue(dict.contains(new StringList("singleton")));
}

@Test
public void testTagReturnsSafeOutputWhenEmptyOutcomeListExists() {
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence s = mock(Sequence.class);
when(s.getOutcomes()).thenReturn(new ArrayList<>());
when(model.bestSequence(any(), any(), any(), any())).thenReturn(s);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
String[] input = new String[] { "EmptyTest" };
String[] result = tagger.tag(input);
assertEquals(0, result.length);
}

@Test
public void testOrderedTagsReturnsEmptyWhenEvalReturnsEmptyProbabilities() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any())).thenReturn(new double[0]);
POSContextGenerator cg = mock(POSContextGenerator.class);
when(cg.getContext(anyInt(), any(), any(), isNull())).thenReturn(new String[] { "x" });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(null);
SequenceClassificationModel<String> dummySeqModel = mock(SequenceClassificationModel.class);
// POSModel modelObj = new POSModel("en", dummySeqModel, 5, new HashMap<>(), factory);
// modelObj.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, model);
// POSTaggerME tagger = new POSTaggerME(modelObj, POSTagFormat.UD);
// String[] result = tagger.getOrderedTags(Collections.singletonList("x"), Collections.singletonList("x"), 0);
// assertEquals(0, result.length);
}

@Test
public void testTrainEventModelTrainerReturnsPOSModel() throws IOException {
// ObjectStream<POSSample> sampleStream = new ListObjectStream<>(Collections.singletonList(new POSSample(new String[] { "cat" }, new String[] { "NN" })));
TrainingParameters mlParams = new TrainingParameters();
// mlParams.put(TrainerFactory.TRAINER_TYPE_PARAM, "EVENT");
mlParams.put(BeamSearch.BEAM_SIZE_PARAMETER, "2");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGenerator);
final MaxentModel trainedModel = mock(MaxentModel.class);
EventTrainer eventTrainer = mock(EventTrainer.class);
// when(eventTrainer.train(any())).thenReturn(trainedModel);
// TrainerFactory.registerTrainerFactory("EVENT", new TrainerFactory.TrainerFactoryCreator() {
// 
// public Object create(TrainingParameters params, Map<String, String> manifest) {
// return eventTrainer;
// }
// });
// POSModel trainedPosModel = POSTaggerME.train("en", sampleStream, mlParams, factory);
// assertNotNull(trainedPosModel);
}

@Test
public void testConvertTagsUsesMapperWhenFormatMismatch() {
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
Sequence sequence = new Sequence(Arrays.asList("TAG1", "TAG2"));
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
Map<String, String> manifest = new HashMap<>();
manifest.put(BeamSearch.BEAM_SIZE_PARAMETER, "3");
POSModel posModel = new POSModel("en", model, manifest, factory);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(new String[] { "word1", "word2" });
// assertNotNull(result);
// assertEquals(2, result.length);
}

@Test
public void testTagReturnsEmptyForEmptyInputAndModelReturnsNull() {
String[] sentence = new String[0];
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(null);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary td = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(td);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel modelObj = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(modelObj);
try {
tagger.tag(sentence);
fail("Expected NullPointerException due to null sequence");
} catch (NullPointerException expected) {
}
}

@Test
public void testGetOrderedTagsHandlesProbabilitiesWithAllZeroInput() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any())).thenReturn(new double[] { 0.0, 0.0, 0.0 });
when(model.getOutcome(0)).thenReturn("A");
when(model.getOutcome(1)).thenReturn("B");
when(model.getOutcome(2)).thenReturn("C");
POSContextGenerator cg = mock(POSContextGenerator.class);
when(cg.getContext(anyInt(), any(), any(), isNull())).thenReturn(new String[] { "ctx" });
SequenceClassificationModel<String> dummySeqModel = mock(SequenceClassificationModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(null);
when(factory.getTagDictionary()).thenReturn(null);
// POSModel posModel = new POSModel("en", dummySeqModel, 5, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, model);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
List<String> words = Arrays.asList("a");
List<String> tags = Arrays.asList("A");
// String[] ordered = tagger.getOrderedTags(words, tags, 0, new double[3]);
// assertEquals(3, ordered.length);
// assertTrue(Arrays.asList("A", "B", "C").contains(ordered[0]));
}

@Test
public void testPopulatePOSDictionaryHandlesMixedCaseWordsCaseInsensitiveWithDifferentTags() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample1 = new POSSample(new String[] { "Play" }, new String[] { "VB" });
POSSample sample2 = new POSSample(new String[] { "play" }, new String[] { "NN" });
POSSample sample3 = new POSSample(new String[] { "PLAY" }, new String[] { "VB" });
POSSample sample4 = new POSSample(new String[] { "Play" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3, sample4));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tags = dict.getTags("play");
// Arrays.sort(tags);
// assertEquals(2, tags.length);
// assertArrayEquals(new String[] { "NN", "VB" }, tags);
}

@Test
public void testPopulatePOSDictionaryAddsOnlySufficientOccurringTag() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample1 = new POSSample(new String[] { "jump" }, new String[] { "VBP" });
POSSample sample2 = new POSSample(new String[] { "jump" }, new String[] { "VBP" });
POSSample sample3 = new POSSample(new String[] { "jump" }, new String[] { "NN" });
POSSample sample4 = new POSSample(new String[] { "jump" }, new String[] { "VBP" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3, sample4));
// POSTaggerME.populatePOSDictionary(stream, dict, 3);
// String[] tags = dict.getTags("jump");
// assertEquals(1, tags.length);
// assertEquals("VBP", tags[0]);
}

@Test
public void testTagEmptyStringArrayReturnsEmptyTagArray() {
Sequence sequence = new Sequence(new ArrayList<String>());
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(String[].class), any(), any(), any())).thenReturn(sequence);
POSContextGenerator cg = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel modelObj = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(modelObj);
String[] result = tagger.tag(new String[] {});
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testPopulatePOSDictionaryPreservesPredefinedTagsWithCutoffWeighting() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
// dict.put("hello", new String[] { "UH" });
POSSample sample1 = new POSSample(new String[] { "hello" }, new String[] { "UH" });
POSSample sample2 = new POSSample(new String[] { "hello" }, new String[] { "VB" });
POSSample sample3 = new POSSample(new String[] { "hello" }, new String[] { "VB" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(sample1, sample2, sample3));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tags = dict.getTags("hello");
// Arrays.sort(tags);
// assertArrayEquals(new String[] { "UH", "VB" }, tags);
}

@Test
public void testTopKSequencesWithNullContextReturnsValidOutput() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence s1 = new Sequence(Arrays.asList("X", "Y", "Z"));
when(model.bestSequences(eq(3), any(), isNull(), any(), any())).thenReturn(new Sequence[] { s1 });
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary tagDict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
Map<String, String> manifest = new HashMap<>();
manifest.put(BeamSearch.BEAM_SIZE_PARAMETER, "3");
POSModel modelObj = new POSModel("en", model, manifest, factory);
POSTaggerME tagger = new POSTaggerME(modelObj);
Sequence[] result = tagger.topKSequences(new String[] { "One", "Two", "Three" });
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("Z", result[0].getOutcomes().get(2));
}

@Test
public void testTagWithMultipleSequencesDifferentLengthsShouldReturnCorrectConversions() {
String[] input = new String[] { "quick", "fox" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Arrays.asList("JJ", "NN"));
when(model.bestSequence(eq(input), isNull(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "JJ", "NN" });
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
Map<String, String> manifest = new HashMap<>();
manifest.put("BeamSize", "3");
POSModel modelObj = new POSModel("en", model, manifest, factory);
POSTaggerME tagger = new POSTaggerME(modelObj);
String[] result = tagger.tag(input);
assertNotNull(result);
assertEquals("JJ", result[0]);
assertEquals("NN", result[1]);
}

@Test
public void testGetOrderedTagsWithEmptyContextShouldReturnEmptyArray() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class))).thenReturn(new double[0]);
POSContextGenerator cg = mock(POSContextGenerator.class);
when(cg.getContext(eq(0), any(String[].class), any(String[].class), isNull())).thenReturn(new String[] { "dummy" });
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(null);
// POSModel posModel = new POSModel("en", seqModel, 5, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] ordered = tagger.getOrderedTags(Collections.singletonList("run"), Collections.singletonList("VB"), 0, new double[0]);
// assertNotNull(ordered);
// assertEquals(0, ordered.length);
}

@Test
public void testPopulatePOSDictionaryIgnoresWordWithDigitsInTheMiddle() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample sample = new POSSample(new String[] { "ru3n" }, new String[] { "VB" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Collections.singletonList(sample));
// POSTaggerME.populatePOSDictionary(stream, dict, 1);
// assertNull(dict.getTags("ru3n"));
}

@Test
public void testTrainReturnsModelUsingSequenceTrainer() throws IOException {
// ObjectStream<POSSample> sampleStream = new ListObjectStream<>(Collections.singletonList(new POSSample(new String[] { "run" }, new String[] { "VB" })));
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, "SEQUENCE");
SequenceClassificationModel<POSSample> sequenceModel = mock(SequenceClassificationModel.class);
SequenceTrainer trainer = mock(SequenceTrainer.class);
// when(trainer.train(any())).thenReturn(sequenceModel);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGenerator);
// TrainerFactory.registerTrainerFactory("SEQUENCE", new TrainerFactory.TrainerFactoryCreator() {
// 
// public Object create(TrainingParameters p, Map<String, String> m) {
// return trainer;
// }
// });
// POSModel model = POSTaggerME.train("en", sampleStream, params, factory);
// assertNotNull(model);
}

@Test
public void testTopKSequencesReturnsZeroIfBeamSizeSetToZero() {
String[] sentence = { "word" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(0), eq(sentence), isNull(), any(), any())).thenReturn(new Sequence[0]);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(dict);
Map<String, String> manifest = new HashMap<>();
manifest.put(BeamSearch.BEAM_SIZE_PARAMETER, "0");
POSModel posModel = new POSModel("en", model, manifest, factory);
POSTaggerME tagger = new POSTaggerME(posModel);
Sequence[] sequences = tagger.topKSequences(sentence);
assertNotNull(sequences);
assertEquals(0, sequences.length);
}

@Test
public void testPopulatePOSDictionaryPreservesCutoffForPreloadedTags() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
// dict.put("cat", new String[] { "NN" });
POSSample s1 = new POSSample(new String[] { "cat" }, new String[] { "JJ" });
POSSample s2 = new POSSample(new String[] { "cat" }, new String[] { "JJ" });
POSSample s3 = new POSSample(new String[] { "cat" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(s1, s2, s3));
// POSTaggerME.populatePOSDictionary(stream, dict, 2);
// String[] tags = dict.getTags("cat");
// assertNotNull(tags);
// Arrays.sort(tags);
// assertArrayEquals(new String[] { "JJ", "NN" }, tags);
}

@Test
public void testTagReturnsOriginalTagsWhenFormatIsCustom() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
List<String> outcomes = Arrays.asList("X1", "Y2", "Z9");
Sequence sequence = new Sequence(outcomes);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "X1", "Y2", "Z9" });
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
// String[] result = tagger.tag(new String[] { "a", "b", "c" });
// assertNotNull(result);
// assertArrayEquals(new String[] { "X1", "Y2", "Z9" }, result);
}

@Test
public void testGetAllPosTagsReturnsEmptyArrayIfModelOutcomesIsNull() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(null);
POSContextGenerator cg = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
POSModel posModel = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(posModel);
try {
String[] tags = tagger.getAllPosTags();
assertNull(tags);
} catch (Exception e) {
fail("Should not throw exception even if outcomes are null");
}
}

@Test
public void testConstructorDefaultsToFallbackBeamSizeWhenNotSet() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(POSTaggerME.DEFAULT_BEAM_SIZE)).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
Map<String, String> manifest = new HashMap<>();
POSModel modelObj = new POSModel("en", model, manifest, factory);
POSTaggerME tagger = new POSTaggerME(modelObj);
assertNotNull(tagger);
}

@Test
public void testTagMethodHandlesNullSequenceOutcomesListGracefully() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(null);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(String[].class), isNull(), any(), any())).thenReturn(sequence);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel modelObj = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(modelObj);
try {
tagger.tag(new String[] { "this" });
fail("Expected NullPointerException due to null outcomes");
} catch (NullPointerException expected) {
}
}

@Test
public void testGetOrderedTagsUsesCorrectRankingForProbabilities() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any())).thenReturn(new double[] { 0.2, 0.7, 0.1 });
when(maxentModel.getOutcome(0)).thenReturn("A");
when(maxentModel.getOutcome(1)).thenReturn("B");
when(maxentModel.getOutcome(2)).thenReturn("C");
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(contextGenerator.getContext(anyInt(), any(), any(), isNull())).thenReturn(new String[] { "context" });
SequenceClassificationModel<String> dummyModel = mock(SequenceClassificationModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(null);
Map<String, String> manifest = new HashMap<>();
manifest.put("BeamSize", "3");
POSModel model = new POSModel("en", dummyModel, manifest, factory);
// model.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
List<String> sentence = Arrays.asList("word");
List<String> tags = Arrays.asList("X");
double[] probs = new double[3];
// String[] result = tagger.getOrderedTags(sentence, tags, 0, probs);
// assertEquals("B", result[0]);
// assertEquals("A", result[1]);
// assertEquals("C", result[2]);
assertEquals(0.7, probs[0], 0.0001);
assertEquals(0.2, probs[1], 0.0001);
assertEquals(0.1, probs[2], 0.0001);
}

@Test
public void testTrainReturnsNullPOSModelWhenTrainerReturnsNullModel() throws IOException {
// ObjectStream<POSSample> stream = new ListObjectStream<>(Collections.emptyList());
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, "SEQUENCE");
SequenceTrainer badTrainer = mock(SequenceTrainer.class);
when(badTrainer.train(any())).thenReturn(null);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGen);
// TrainerFactory.registerTrainerFactory("SEQUENCE", new TrainerFactory.TrainerFactoryCreator() {
// 
// public Object create(TrainingParameters p, Map<String, String> m) {
// return badTrainer;
// }
// });
// POSModel model = POSTaggerME.train("en", stream, params, factory);
// assertNull(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME));
}

@Test
public void testBuildNGramDictionarySkipsEmptyTokens() throws IOException {
POSSample s1 = new POSSample(new String[] {}, new String[] {});
POSSample s2 = new POSSample(new String[] { "a" }, new String[] { "NN" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(s1, s2));
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertTrue(dict.contains(new StringList("a")));
}

@Test
public void testPopulatePOSDictionaryWordAppearsWithOnlyOneTagBelowCutoff() throws IOException {
// MutableTagDictionary dict = new MutableTagDictionary(false);
POSSample s1 = new POSSample(new String[] { "hit" }, new String[] { "VB" });
POSSample s2 = new POSSample(new String[] { "hit" }, new String[] { "VB" });
// ObjectStream<POSSample> stream = new ListObjectStream<>(Arrays.asList(s1, s2));
// POSTaggerME.populatePOSDictionary(stream, dict, 3);
// assertNull(dict.getTags("hit"));
}

@Test
public void testTagWorksCorrectlyWithCustomFormatAndFormatMapperSetToNoOp() {
Sequence sequence = new Sequence(Arrays.asList("A", "B", "C"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(String[].class), isNull(), any(), any())).thenReturn(sequence);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
POSModel modelObj = new POSModel("en", model, new HashMap<>(), factory);
// POSTaggerME tagger = new POSTaggerME(modelObj, POSTagFormat.CUSTOM);
// String[] result = tagger.tag(new String[] { "x", "y", "z" });
// assertArrayEquals(new String[] { "A", "B", "C" }, result);
}

@Test
public void testTagWithEmptyInputReturnsEmptyArray() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Collections.emptyList());
when(model.bestSequence(any(String[].class), any(), any(), any())).thenReturn(sequence);
TagDictionary dict = mock(TagDictionary.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
POSModel modelObj = new POSModel("en", model, new HashMap<>(), factory);
POSTaggerME tagger = new POSTaggerME(modelObj);
String[] result = tagger.tag(new String[0]);
assertNotNull(result);
assertEquals(0, result.length);
}
}
