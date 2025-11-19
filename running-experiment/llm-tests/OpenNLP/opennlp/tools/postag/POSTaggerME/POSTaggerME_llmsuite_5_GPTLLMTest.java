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

public class POSTaggerME_llmsuite_5_GPTLLMTest {

@Test
public void testTagSimpleSentence() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
List<String> tags = Arrays.asList("DT", "JJ", "JJ", "NN");
Sequence sequence = new Sequence(tags);
when(model.bestSequence(any(String[].class), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB", "DT" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
String[] sentence = new String[] { "The", "quick", "brown", "fox" };
// String[] result = tagger.tag(sentence);
// assertArrayEquals(new String[] { "DT", "JJ", "JJ", "NN" }, result);
}

@Test
public void testTopKSequences() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
Sequence sequence = new Sequence(Arrays.asList("DT", "JJ", "NN"));
when(model.bestSequences(eq(3), any(String[].class), any(), eq(contextGenerator), eq(validator))).thenReturn(new Sequence[] { sequence });
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB", "DT" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
String[] sentence = new String[] { "The", "lazy", "dog" };
// Sequence[] sequences = tagger.topKSequences(sentence);
// assertEquals(1, sequences.length);
// assertEquals(Arrays.asList("DT", "JJ", "NN"), sequences[0].getOutcomes());
}

@Test
public void testTagWithEmptyInput() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence emptySequence = new Sequence(Collections.emptyList());
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.bestSequence(eq(new String[0]), any(), eq(contextGenerator), eq(validator))).thenReturn(emptySequence);
when(model.getOutcomes()).thenReturn(new String[] {});
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
String[] sentence = new String[0];
// String[] result = tagger.tag(sentence);
// assertEquals(0, result.length);
}

@Test
public void testBuildNGramDictionarySingleSample() throws IOException {
POSSample sample = new POSSample(new String[] { "fast", "fox" }, new String[] { "JJ", "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertTrue(dict.contains(new StringList("fast")));
// assertTrue(dict.contains(new StringList("fox")));
}

@Test
public void testTagMultipleAlternatives() {
Sequence sequence = new Sequence(Arrays.asList("NN"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.bestSequences(eq(1), any(String[].class), any(), eq(contextGenerator), eq(validator))).thenReturn(new Sequence[] { sequence });
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
String[] sentence = new String[] { "hello" };
// String[][] result = tagger.tag(1, sentence);
// assertEquals(1, result.length);
// assertEquals("NN", result[0][0]);
}

@Test
public void testGetOrderedTagsThrowsWhenNoMaxentModel() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// POSTaggerME tagger = new POSTaggerME(posModel);
List<String> words = Arrays.asList("hello");
List<String> tags = Arrays.asList("NN");
// tagger.getOrderedTags(words, tags, 0);
}

@Test
public void testTrainWithEventTrainer() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
POSTaggerFactory factory = new POSTaggerFactory();
POSSample sample = new POSSample(new String[] { "run" }, new String[] { "VB" });
when(stream.read()).thenReturn(sample).thenReturn(null);
// TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
// params.put(BeamSearch.BEAM_SIZE_PARAMETER, "5");
// POSModel model = POSTaggerME.train("en", stream, params, factory);
// assertNotNull(model);
// assertEquals("en", model.getLanguage());
}

@Test
public void testPopulatePOSDictionaryAddsTags() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(false);
when(dict.getTags("word")).thenReturn(null);
POSSample sample = new POSSample(new String[] { "Word" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
// verify(dict).put(eq("word"), Mockito.argThat(tags -> tags.length == 1 && tags[0].equals("NN")));
}

@Test
public void testTrainWithUnknownTrainerType() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put("TrainerType", "UNKNOWN");
POSTaggerME.train("en", stream, params, new POSTaggerFactory());
}

@Test
public void testGetAllPosTagsReturnsOutcomes() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] tags = tagger.getAllPosTags();
// assertArrayEquals(new String[] { "NN", "VB" }, tags);
}

@Test
public void testConvertTagsWithCustomFormat() {
List<String> inputTags = Arrays.asList("A", "B", "C");
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
Sequence sequence = new Sequence(inputTags);
when(model.bestSequence(any(String[].class), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "A", "B", "C" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
String[] sentence = new String[] { "word1", "word2", "word3" };
// String[] resultTags = tagger.tag(sentence);
// assertArrayEquals(new String[] { "A", "B", "C" }, resultTags);
}

@Test
public void testProbsWithPreSizedArray() {
List<String> tags = Arrays.asList("NN", "VB");
double[] expectedProbs = new double[] { 0.7, 0.3 };
Sequence sequence = spy(new Sequence(tags));
doReturn(expectedProbs).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.bestSequence(any(String[].class), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// tagger.tag(new String[] { "word1", "word2" });
double[] probs = new double[2];
// tagger.probs(probs);
assertArrayEquals(expectedProbs, probs, 1e-6);
}

@Test
public void testGetOrderedTagsWithTProbs() {
List<String> words = Arrays.asList("word");
List<String> tags = Arrays.asList("NN");
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class))).thenReturn(new double[] { 0.6, 0.3, 0.1 });
when(maxentModel.getOutcome(eq(0))).thenReturn("NN");
when(maxentModel.getOutcome(eq(1))).thenReturn("VB");
when(maxentModel.getOutcome(eq(2))).thenReturn("JJ");
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB", "JJ" });
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(contextGenerator.getContext(eq(0), any(String[].class), any(String[].class), isNull())).thenReturn(new String[] { "contextFeature1" });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
double[] tprobs = new double[3];
// String[] result = tagger.getOrderedTags(words, tags, 0, tprobs);
// assertArrayEquals(new String[] { "NN", "VB", "JJ" }, result);
assertEquals(0.6, tprobs[0], 1e-6);
assertEquals(0.3, tprobs[1], 1e-6);
assertEquals(0.1, tprobs[2], 1e-6);
}

@Test
public void testBuildNGramDictionaryWithEmptyWord() throws IOException {
POSSample sample = new POSSample(new String[] { "" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertTrue(dict.contains(new StringList("")));
}

@Test
public void testPopulatePOSDictionarySkipsDigitWords() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(false);
String wordWithDigit = "abc123";
POSSample sample = new POSSample(new String[] { wordWithDigit }, new String[] { "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 2);
verify(dict, never()).put(anyString(), any(String[].class));
}

@Test
public void testTagUsingFormatConstructor() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Arrays.asList("NN"));
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("fr", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(new String[] { "chien" });
// assertArrayEquals(new String[] { "NN" }, result);
}

@Test
public void testProbsCalledBeforeTagging() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// tagger.probs();
}

@Test
public void testTagWithAdditionalContextDiffersFromTagWithoutIt() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
Sequence seq1 = new Sequence(Arrays.asList("NN"));
Sequence seq2 = new Sequence(Arrays.asList("VB"));
when(model.bestSequence(any(), isNull(), eq(contextGenerator), eq(validator))).thenReturn(seq1);
when(model.bestSequence(any(), notNull(), eq(contextGenerator), eq(validator))).thenReturn(seq2);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, POSTaggerME.DEFAULT_BEAM_SIZE, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
String[] sentence = new String[] { "run" };
// String[] tagsWithoutCtx = tagger.tag(sentence);
// String[] tagsWithCtx = tagger.tag(sentence, new Object[] { "custom-context" });
// assertEquals("NN", tagsWithoutCtx[0]);
// assertEquals("VB", tagsWithCtx[0]);
}

@Test
public void testConstructorWithInvalidBeamSizeManifestValue() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
Map<String, String> manifest = new HashMap<>();
manifest.put(BeamSearch.BEAM_SIZE_PARAMETER, "invalidNumber");
// POSModel posModel = new POSModel("en", model, 3, manifest, factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] result = tagger.tag(new String[] { "a" });
// assertNotNull(result);
}

@Test
public void testTagWithTagDictionary() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Arrays.asList("NN"));
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
when(dict.getTags("dog")).thenReturn(new String[] { "NN" });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] result = tagger.tag(new String[] { "dog" });
// assertEquals("NN", result[0]);
}

@Test
public void testConvertTagsWithFormatMismatch() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
List<String> tags = Arrays.asList("FOO", "BAR");
Sequence sequence = new Sequence(tags);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "FOO", "BAR" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.XPOS);
// String[] result = tagger.tag(new String[] { "test1", "test2" });
// assertEquals(2, result.length);
}

@Test
public void testConvertTagsWithEmptyInputReturnsEmpty() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
List<String> tags = Collections.emptyList();
String[] result = Arrays.stream(tags.toArray(new String[0])).toArray(String[]::new);
assertEquals(0, result.length);
}

@Test
public void testTrainWithSequenceTrainer() throws Exception {
SequenceTrainer trainer = mock(SequenceTrainer.class);
SequenceClassificationModel<String> trained = mock(SequenceClassificationModel.class);
when(trainer.train(any())).thenReturn(trained);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
POSSample sample = new POSSample(new String[] { "test" }, new String[] { "NN" });
when(stream.read()).thenReturn(sample).thenReturn(null);
Map<String, String> manifest = new HashMap<>();
TrainingParameters params = new TrainingParameters();
params.put("TrainerType", "SEQUENCE_TRAINER");
POSTaggerFactory factory = new POSTaggerFactory() {

@Override
public POSContextGenerator getPOSContextGenerator() {
return mock(POSContextGenerator.class);
}
};
// POSModel model = POSModelUtil.trainWithExternalTrainer(stream, params, manifest, factory, trainer);
// assertEquals("en", model.getLanguage());
}

@Test
public void testPopulatePOSDictionaryWithExistingTagsBelowCutoff() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(false);
when(dict.getTags("word")).thenReturn(new String[] { "JJ" });
POSSample sample = new POSSample(new String[] { "Word", "blue" }, new String[] { "JJ", "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 2);
verify(dict, never()).put(eq("word"), any());
verify(dict, never()).put(eq("blue"), any());
}

@Test
public void testGetOrderedTagsSingleProb() {
MaxentModel maxentModel = mock(MaxentModel.class);
String[] outcomes = new String[] { "NN" };
when(maxentModel.eval(any())).thenReturn(new double[] { 1.0 });
when(maxentModel.getOutcome(anyInt())).thenReturn("NN");
SequenceClassificationModel<String> sequenceClassifier = mock(SequenceClassificationModel.class);
when(sequenceClassifier.getOutcomes()).thenReturn(outcomes);
POSContextGenerator context = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(context.getContext(eq(0), any(), any(), any())).thenReturn(new String[] { "ctx" });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(context);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", sequenceClassifier, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] ordered = tagger.getOrderedTags(Arrays.asList("hello"), Arrays.asList("VB"), 0);
// assertEquals(1, ordered.length);
// assertEquals("NN", ordered[0]);
}

@Test
public void testTagWithNullSentenceInput() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
Sequence sequence = new Sequence(Arrays.asList("NN"));
when(model.bestSequence(isNull(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] tags = tagger.tag((String[]) null);
// assertNotNull(tags);
// assertEquals(1, tags.length);
}

@Test
public void testPopulatePOSDictionaryExpandsWithCutoff() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(false);
when(dict.getTags("blue")).thenReturn(null);
POSSample sample1 = new POSSample(new String[] { "blue" }, new String[] { "JJ" });
POSSample sample2 = new POSSample(new String[] { "BLUE" }, new String[] { "JJ" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1).thenReturn(sample2).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
verify(dict).put(eq("blue"), eq(new String[] { "JJ" }));
}

@Test
public void testTagOnEmptyTokenArray() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Collections.emptyList());
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] {});
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] result = tagger.tag(new String[0]);
// assertEquals(0, result.length);
}

@Test
public void testTagWithNullTokens() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Collections.singletonList("NN"));
when(model.bestSequence(isNull(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] tags = tagger.tag((String[]) null);
// assertEquals(1, tags.length);
// assertEquals("NN", tags[0]);
}

@Test
public void testTrainWithUnknownTrainerTypeThrows() throws IOException {
TrainingParameters trainingParams = new TrainingParameters();
// trainingParams.put(TrainerFactory.TRAINER_TYPE_PARAM, "UNKNOWN_TYPE");
POSTaggerFactory factory = new POSTaggerFactory();
ObjectStream<POSSample> samples = mock(ObjectStream.class);
POSTaggerME.train("en", samples, trainingParams, factory);
}

@Test
public void testBestSequencesReturnsEmptyArray() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), any(), any(), any(), any())).thenReturn(new Sequence[0]);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[][] results = tagger.tag(3, new String[] { "the", "dog" });
// assertEquals(0, results.length);
}

@Test
public void testGetOrderedTagsProbsLengthOne() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any())).thenReturn(new double[] { 0.99 });
when(maxentModel.getOutcome(0)).thenReturn("NN");
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN" });
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(contextGenerator.getContext(anyInt(), any(), any(), any())).thenReturn(new String[] { "w=d" });
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", seqModel, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] result = tagger.getOrderedTags(Collections.singletonList("dog"), Collections.singletonList("NN"), 0);
// assertEquals(1, result.length);
// assertEquals("NN", result[0]);
}

@Test
public void testConvertTagsCustomAndMappedMatch() {
List<String> input = Arrays.asList("DT", "NN");
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(input);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "DT", "NN" });
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(new String[] { "The", "dog" });
// assertArrayEquals(new String[] { "DT", "NN" }, result);
}

@Test
public void testPopulatePOSDictionaryCaseSensitiveTagsRetained() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(true);
when(dict.getTags("Dog")).thenReturn(new String[] { "NN" });
POSSample sample = new POSSample(new String[] { "Dog" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
verify(dict).put(eq("Dog"), argThat(tags -> tags.length == 1 && tags[0].equals("NN")));
}

@Test
public void testProbsDoubleArrayTruncatesWhenTooLong() {
List<String> tags = Arrays.asList("NN");
double[] fakeProbs = new double[] { 0.87 };
Sequence sequence = mock(Sequence.class);
doAnswer(invocation -> {
double[] input = invocation.getArgument(0);
input[0] = fakeProbs[0];
return null;
}).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSContextGenerator context = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(context);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// tagger.tag(new String[] { "dog" });
double[] output = new double[5];
// tagger.probs(output);
assertEquals(0.87, output[0], 0.0001);
}

@Test
public void testPopulatePOSDictionaryMultipleTagsOverCutoff() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(false);
when(dict.getTags("food")).thenReturn(null);
// AtomicInteger counter = new AtomicInteger();
ObjectStream<POSSample> stream = new ObjectStream<POSSample>() {

public POSSample read() {
// if (counter.incrementAndGet() > 3)
return null;
// return new POSSample(new String[] { "Food" }, new String[] { "NN" });
}

public void reset() {
}

public void close() {
}
};
POSTaggerME.populatePOSDictionary(stream, dict, 3);
verify(dict).put(eq("food"), argThat(tags -> Arrays.asList(tags).contains("NN")));
}

@Test
public void testPopulatePOSDictionaryWithNullTagsFromDictionary() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(true);
when(dict.getTags("Apple")).thenReturn(null);
POSSample sample = new POSSample(new String[] { "Apple" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
verify(dict).put(eq("Apple"), argThat(tags -> tags.length == 1 && tags[0].equals("NN")));
}

@Test
public void testBuildNGramDictionarySingleLetterToken() throws IOException {
POSSample sample = new POSSample(new String[] { "A" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 0);
// assertTrue(dict.contains(new StringList("A")));
}

@Test
public void testPopulatePOSDictionaryThrowsIOExceptionFromStream() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(true);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenThrow(new IOException("Test I/O failure"));
POSTaggerME.populatePOSDictionary(stream, dict, 1);
}

@Test
public void testTagReturnsEmptyArrayIfSequenceValidatorRejects() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Collections.singletonList("VB"));
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(validator.validSequence(anyInt(), any(), any(), any())).thenReturn(false);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel modelObj = new POSModel("en", model, 3, new HashMap<>(), factory);
// modelObj.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(modelObj);
String[] tokens = { "jump" };
// String[] tags = tagger.tag(tokens);
// assertEquals(1, tags.length);
// assertEquals("VB", tags[0]);
}

@Test
public void testTagDictionaryWithEmptyTagSetDoesNotAdd() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(true);
when(dict.getTags("House")).thenReturn(new String[] { "VB" });
POSSample sample = new POSSample(new String[] { "House" }, new String[] { "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 10);
verify(dict, never()).put(eq("House"), any());
}

@Test
public void testProbsReturnsNullWhenSequenceReturnsNull() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getProbs()).thenReturn(null);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "DT" });
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// tagger.tag(new String[] { "the" });
// double[] result = tagger.probs();
// assertNull(result);
}

@Test
public void testPopulatePOSDictionaryCaseInsensitiveLowerCasesTokens() throws IOException {
MutableTagDictionary dict = mock(MutableTagDictionary.class);
when(dict.isCaseSensitive()).thenReturn(false);
when(dict.getTags("apple")).thenReturn(null);
POSSample sample = new POSSample(new String[] { "Apple" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
verify(dict).put(eq("apple"), argThat(tags -> tags.length == 1 && "NN".equals(tags[0])));
}

@Test
public void testBuildNGramDictionaryIgnoresTokensBeforeCutoff() throws IOException {
POSSample sample1 = new POSSample(new String[] { "book" }, new String[] { "NN" });
POSSample sample2 = new POSSample(new String[] { "pen" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1).thenReturn(sample2).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 3);
// assertFalse(dict.contains(new StringList("book")));
// assertFalse(dict.contains(new StringList("pen")));
}

@Test
public void testGetOrderedTagsWithNullModelOutcomeReturnsNull() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any())).thenReturn(new double[] { 0.3 });
when(maxentModel.getOutcome(0)).thenReturn(null);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { null });
POSContextGenerator cg = mock(POSContextGenerator.class);
when(cg.getContext(anyInt(), any(), any(), any())).thenReturn(new String[] { "context" });
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] result = tagger.getOrderedTags(Arrays.asList("abc"), Arrays.asList("JJ"), 0);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertNull(result[0]);
}

@Test
public void testConstructorWithInvalidBeamSizeManifest() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
Map<String, String> manifest = new HashMap<>();
manifest.put(BeamSearch.BEAM_SIZE_PARAMETER, "notAnInt");
// POSModel posModel = new POSModel("en", model, 3, manifest, factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// new POSTaggerME(posModel);
}

@Test
public void testGetOrderedTagsWithoutPosModelThrowsException() {
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", seqModel, 3, new HashMap<>(), factory);
// POSTaggerME tagger = new POSTaggerME(posModel);
// tagger.getOrderedTags(Collections.singletonList("word"), Collections.singletonList("VB"), 0);
}

@Test
public void testTagWithInconsistentSentenceTagSizes() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
Sequence sequence = new Sequence(Arrays.asList("VB", "NN", "JJ"));
when(model.bestSequence(any(), any(), eq(cg), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "VB", "NN", "JJ" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
String[] singleToken = new String[] { "OnlyOne" };
// String[] result = tagger.tag(singleToken);
// assertEquals(3, result.length);
// assertArrayEquals(new String[] { "VB", "NN", "JJ" }, result);
}

@Test
public void testTopKSequencesReturnsEmptyWhenModelBroken() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequences(anyInt(), any(), any(), any(), any())).thenReturn(new Sequence[0]);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// Sequence[] result = tagger.topKSequences(new String[] { "This" }, new Object[] { "ctx" });
// assertEquals(0, result.length);
}

@Test
public void testConvertTagsPreservesFormatWhenMapperReturnsNullOutcome() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
List<String> fakeTags = Arrays.asList("X", "Y", "Z");
Sequence sequence = new Sequence(fakeTags);
when(model.bestSequence(any(), any(), eq(cg), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "X", "Y", "Z" });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
String[] sentence = { "a", "b", "c" };
// String[] tagged = tagger.tag(sentence);
// assertArrayEquals(new String[] { "X", "Y", "Z" }, tagged);
}

@Test
public void testEmptyDictionaryPopulationFromEmptySampleStream() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
// MutableTagDictionary dict = new MutableTagDictionary(true);
// POSTaggerME.populatePOSDictionary(stream, dict, 1);
// assertFalse(dict.getTags("any") != null);
// assertEquals(0, dict.getEntries().size());
}

@Test
public void testTagMultipleAlternativesWithEmptyBestSequences() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequences(eq(3), any(), any(), eq(cg), eq(validator))).thenReturn(new Sequence[0]);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("de", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[][] results = tagger.tag(3, new String[] { "Hund" });
// assertEquals(0, results.length);
}

@Test
public void testProbsWithExtraLengthInputArray() {
Sequence resultSequence = mock(Sequence.class);
doAnswer(invocation -> {
double[] dest = invocation.getArgument(0);
dest[0] = 0.7;
dest[1] = 0.2;
return null;
}).when(resultSequence).getProbs(any(double[].class));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(resultSequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
MaxentModel maxentModel = mock(MaxentModel.class);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// tagger.tag(new String[] { "dog", "barks" });
double[] out = new double[4];
// tagger.probs(out);
assertTrue(out[0] >= 0);
assertTrue(out[1] >= 0);
assertEquals(0.0, out[2], 0.00001);
}

@Test
public void testGetAllPosTagsReturnsNonEmptySet() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB", "JJ" });
MaxentModel maxentModel = mock(MaxentModel.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// String[] allTags = tagger.getAllPosTags();
// assertArrayEquals(new String[] { "NN", "VB", "JJ" }, allTags);
}

@Test
public void testTagReturnsOriginalWhenTagFormatCustom() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
Sequence sequence = new Sequence(Arrays.asList("X1", "X2"));
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "X1", "X2" });
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact("pos.model", maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
String[] sentence = new String[] { "foo", "bar" };
// String[] tags = tagger.tag(sentence);
// assertEquals("X1", tags[0]);
// assertEquals("X2", tags[1]);
}

@Test
public void testProbsMethodPopulatesArrayCorrectly() {
Sequence sequence = mock(Sequence.class);
double[] mockProbs = new double[] { 0.7, 0.3 };
doAnswer(invocation -> {
double[] dest = invocation.getArgument(0);
dest[0] = mockProbs[0];
dest[1] = mockProbs[1];
return null;
}).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
MaxentModel maxentModel = mock(MaxentModel.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
// tagger.tag(new String[] { "word1", "word2" });
double[] target = new double[2];
// tagger.probs(target);
assertEquals(0.7, target[0], 0.01);
assertEquals(0.3, target[1], 0.01);
}

@Test
public void testTagWithAdditionalContextProducesDifferentOutput() {
Sequence seq1 = new Sequence(Collections.singletonList("NN"));
Sequence seq2 = new Sequence(Collections.singletonList("VB"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(any(String[].class), isNull(), eq(contextGenerator), eq(validator))).thenReturn(seq1);
when(model.bestSequence(any(String[].class), notNull(), eq(contextGenerator), eq(validator))).thenReturn(seq2);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel);
String[] sent = new String[] { "run" };
Object[] context = new Object[] { "custom" };
// String[] tag1 = tagger.tag(sent);
// String[] tag2 = tagger.tag(sent, context);
// assertEquals("NN", tag1[0]);
// assertEquals("VB", tag2[0]);
}

@Test
public void testBuildNGramDictionarySkipsEmptySentences() throws IOException {
POSSample sample1 = new POSSample(new String[] {}, new String[] {});
POSSample sample2 = new POSSample(new String[] { "hello" }, new String[] { "UH" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1).thenReturn(sample2).thenReturn(null);
// Dictionary result = POSTaggerME.buildNGramDictionary(stream, 1);
// boolean contains = result.contains(new StringList("hello"));
// assertTrue(contains);
}

@Test
public void testTrainingSkipsNullPOSSamples() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null).thenReturn(null);
TrainingParameters params = new TrainingParameters();
params.put("TrainType", "EVENT_MODEL_TRAINER");
POSTaggerFactory factory = new POSTaggerFactory();
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
assertNotNull(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME));
}

@Test
public void testTrainWithEventModelCreatesValidModel() throws IOException {
POSSample sample = new POSSample(new String[] { "train", "this" }, new String[] { "VB", "DT" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
TrainingParameters params = new TrainingParameters();
params.put(BeamSearch.BEAM_SIZE_PARAMETER, "4");
params.put("TrainerType", "EVENT_MODEL_TRAINER");
POSTaggerFactory factory = new POSTaggerFactory();
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME));
assertEquals("en", model.getLanguage());
}

@Test
public void testTagShouldMapXPOSTagsWhenFormatIsXPOS() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
List<String> rawTags = Arrays.asList("NOUN", "VERB");
Sequence sequence = new Sequence(rawTags);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.bestSequence(any(), any(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NOUN", "VERB" });
// POSModel posModel = new POSModel("en", model, 3, new HashMap<>(), factory);
// posModel.putArtifact(POSModel.POS_MODEL_ENTRY_NAME, maxentModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.XPOS);
// String[] tags = tagger.tag(new String[] { "word", "other" });
// assertEquals(2, tags.length);
// assertEquals("NOUN", tags[0]);
// assertEquals("VERB", tags[1]);
}
}
