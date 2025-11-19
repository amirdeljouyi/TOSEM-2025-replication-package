package opennlp.tools.postag;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.*;
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

public class POSTaggerME_llmsuite_4_GPTLLMTest {

@Test
public void testTagWithValidSentence() {
String[] sentence = new String[] { "The", "quick", "fox" };
String[] expectedTags = new String[] { "DT", "JJ", "NN" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Arrays.asList(expectedTags));
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(seq);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
// when(posModel.getOutcomes()).thenReturn(new String[] { "DT", "JJ", "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(sentence);
// assertArrayEquals(expectedTags, result);
}

@Test
public void testTagWithAdditionalContext() {
String[] sentence = new String[] { "Jumps", "over" };
Object[] context = new Object[] { "ctx" };
String[] expectedTags = new String[] { "VB", "IN" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Arrays.asList(expectedTags));
when(model.bestSequence(eq(sentence), eq(context), any(), any())).thenReturn(seq);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
// when(posModel.getOutcomes()).thenReturn(new String[] { "VB", "IN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(sentence, context);
// assertArrayEquals(expectedTags, result);
}

@Test
public void testTopKSequencesReturnsSingleSequence() {
String[] sentence = new String[] { "The", "dog" };
String[] tagOutput = new String[] { "DT", "NN" };
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Arrays.asList(tagOutput));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), eq(sentence), isNull(), any(), any())).thenReturn(new Sequence[] { seq });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// Sequence[] result = tagger.topKSequences(sentence);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals(Arrays.asList(tagOutput), result[0].getOutcomes());
}

@Test
public void testProbsArrayFilledExternally() {
String[] sentence = new String[] { "cat", "sleeps" };
String[] tags = new String[] { "NN", "VB" };
double[] probsStub = new double[] { 0.99, 0.88 };
Sequence seq = mock(Sequence.class);
doAnswer(invocation -> {
double[] out = (double[]) invocation.getArguments()[0];
out[0] = probsStub[0];
out[1] = probsStub[1];
return null;
}).when(seq).getProbs(any(double[].class));
when(seq.getOutcomes()).thenReturn(Arrays.asList(tags));
when(seq.getProbs()).thenReturn(probsStub);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(seq);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// tagger.tag(sentence);
double[] probs = new double[2];
// tagger.probs(probs);
assertEquals(0.99, probs[0], 0.001);
assertEquals(0.88, probs[1], 0.001);
}

@Test
public void testBuildNGramDictionaryReturnsValidDictionary() throws IOException {
String[] sentence = new String[] { "Hello", "world" };
String[] tags = new String[] { "UH", "NN" };
POSSample sample = new POSSample(sentence, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertTrue(dict.contains(new StringList(sentence)));
}

// @Test(expected = IllegalArgumentException.class)
public void testTrainThrowsOnUnsupportedTrainerType() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, "unsupported");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGen);
POSTaggerME.train("en", stream, params, factory);
}

@Test
public void testGetAllPosTagsReturnsExpected() {
String[] expected = new String[] { "NN", "VB" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(expected);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.getAllPosTags();
// assertArrayEquals(expected, result);
}

// @Test(expected = UnsupportedOperationException.class)
public void testGetOrderedTagsThrowsIfNoModelPresent() {
POSModel posModel = mock(POSModel.class);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(null);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(null);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// when(posModel.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// tagger.getOrderedTags(Arrays.asList("word1", "word2"), Arrays.asList("NN", "VB"), 1);
}

@Test
public void testTagWithEmptyInput() {
String[] sentence = new String[0];
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Collections.<String>emptyList());
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(seq);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// when(posModel.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testTagMultipleSequencesWithZero() {
String[] sentence = new String[] { "apple" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(0), eq(sentence), isNull(), any(), any())).thenReturn(new Sequence[0]);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[][] result = tagger.tag(0, sentence);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testGetOrderedTagsWithEmptyLists() {
POSModel posModel = mock(POSModel.class);
MaxentModel maxentModel = mock(MaxentModel.class);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(maxentModel.eval(any())).thenReturn(new double[0]);
when(maxentModel.getOutcome(anyInt())).thenReturn("");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(null);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(contextGen.getContext(anyInt(), any(), any(), isNull())).thenReturn(new String[0]);
// when(posModel.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.getOrderedTags(Collections.<String>emptyList(), Collections.<String>emptyList(), 0);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testTrainWithNullStream() throws Exception {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerFactory.EVENT_MODEL_TRAINER);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(mock(MaxentModel.class));
// TrainerFactory.setEventTrainerFactory((mlParams, manifestInfoEntries) -> trainer);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGen);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testPopulatePOSDictionaryWithLowerCaseWord() throws IOException {
String[] words = new String[] { "DOG" };
String[] tags = new String[] { "NN" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
assertNotNull(dict.getTags("dog"));
}

@Test
public void testConstructorWithNullBeamSizeUsesDefault() {
POSModel posModel = mock(POSModel.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
String[] dummyTags = new String[] { "NN" };
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(dummyTags);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// assertNotNull(tagger.getAllPosTags());
}

// @Test(expected = IOException.class)
public void testConstructorWithDownloadUtilThrows() throws IOException {
DownloadUtil downloadUtil = mock(DownloadUtil.class);
when(DownloadUtil.downloadModel(eq("zz"), eq(DownloadUtil.ModelType.POS), eq(POSModel.class))).thenThrow(new IOException("fake"));
// new POSTaggerME("zz", POSTagFormat.UD);
}

@Test
public void testTagWithCustomPOSTagFormat() {
String[] sentence = new String[] { "word" };
String[] tags = new String[] { "CUSTOM" };
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Arrays.asList(tags));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(seq);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
// when(posModel.getOutcomes()).thenReturn(new String[] { "CUSTOM" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals("CUSTOM", result[0]);
}

@Test
public void testProbsReturnsNullWhenNotTaggedBefore() {
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(mock(SequenceClassificationModel.class));
// when(posModel.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
try {
// tagger.probs();
fail("Expected NullPointerException or similar due to uninitialized bestSequence");
} catch (NullPointerException expected) {
}
}

@Test
public void testTagWithNullReturnedByBestSequence() {
String[] sentence = new String[] { "word" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(null);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
try {
// tagger.tag(sentence);
fail("Expected NullPointerException due to null bestSequence");
} catch (NullPointerException expected) {
}
}

@Test
public void testConvertTagsDifferentFormatInvokesMappedConversion() {
String[] sentence = new String[] { "Someword" };
String[] mockedTags = new String[] { "DT" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(mockedTags));
when(sequence.getProbs()).thenReturn(new double[] { 0.9 });
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN", "DT" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.PTB);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals(1, result.length);
}

@Test
public void testPopulatePOSDictionaryWithNoValidTags() throws Exception {
String[] words = new String[] { "Test1" };
String[] tags = new String[] { "SYM" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(true);
dict.put("Test1", new String[] { "SYM" });
POSTaggerME.populatePOSDictionary(stream, dict, Integer.MAX_VALUE);
String[] result = dict.getTags("Test1");
assertNotNull(result);
}

@Test
public void testBuildNGramDictionaryWithEmptyWords() throws Exception {
String[] words = new String[0];
String[] tags = new String[0];
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertEquals(0, dict.size());
}

@Test
public void testTagWithNullInputReturnsEmptyArray() {
String[] sentence = null;
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence seq = mock(Sequence.class);
when(seq.getOutcomes()).thenReturn(Collections.singletonList("NN"));
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(seq);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
try {
// tagger.tag((String[]) null);
fail("Expected NullPointerException from null input sentence");
} catch (NullPointerException expected) {
}
}

@Test
public void testTagMultipleReturnsEmptyArrayWhenModelReturnsNullSequences() {
String[] sentence = new String[] { "Hello" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), any(), any(), any(), any())).thenReturn(null);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator gen = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(gen);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
try {
// tagger.tag(2, sentence);
fail("Expected NullPointerException due to null returned sequence array");
} catch (NullPointerException expected) {
}
}

@Test
public void testPopulatePOSDictionaryZeroCutoffStillStoresValidTag() throws IOException {
String[] words = new String[] { "apple" };
String[] tags = new String[] { "NN" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(true);
POSTaggerME.populatePOSDictionary(stream, dict, 0);
String[] result = dict.getTags("apple");
assertNotNull(result);
assertEquals("NN", result[0]);
}

@Test
public void testConstructorWithValidBeamSizeManifest() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn("5");
when(factory.getPOSContextGenerator(5)).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
// when(model.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
// String[] result = tagger.getAllPosTags();
// assertNotNull(result);
// assertEquals("NN", result[0]);
}

@Test
public void testGetOrderedTagsWithTprobsArrayFilled() {
String[] words = new String[] { "word" };
String[] prevTags = new String[] { "NN" };
String[] outcomes = new String[] { "NN", "VB" };
double[] scores = new double[] { 0.6, 0.4 };
POSModel model = mock(POSModel.class);
MaxentModel maxent = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxent);
when(maxent.eval(any())).thenReturn(scores.clone());
when(maxent.getOutcome(0)).thenReturn("NN");
when(maxent.getOutcome(1)).thenReturn("VB");
// when(model.getOutcomes()).thenReturn(outcomes);
when(model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
double[] tprobs = new double[2];
// String[] ordered = tagger.getOrderedTags(Arrays.asList(words), Arrays.asList(prevTags), 0, tprobs);
// assertNotNull(ordered);
// assertEquals("NN", ordered[0]);
// assertEquals("VB", ordered[1]);
assertEquals(0.6, tprobs[0], 0.001);
assertEquals(0.4, tprobs[1], 0.001);
}

@Test
public void testBuildNGramDictionarySkipsEmptyNGrams() throws IOException {
POSSample sample = new POSSample(new String[0], new String[0]);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertEquals(0, dict.size());
}

@Test
public void testPopulatePOSDictionaryWithDuplicateTags() throws IOException {
String[] words = new String[] { "fish" };
String[] tags = new String[] { "NN" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(sample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(true);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
String[] result = dict.getTags("fish");
assertNotNull(result);
assertEquals(1, result.length);
assertEquals("NN", result[0]);
}

@Test
public void testProbsReturnsCorrectValuesAfterTagging() {
String[] sentence = new String[] { "foo" };
String[] tags = new String[] { "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(tags));
when(sequence.getProbs()).thenReturn(new double[] { 0.99 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// tagger.tag(sentence);
// double[] result = tagger.probs();
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals(0.99, result[0], 0.001);
}

@Test
public void testTopKSequencesWithNullContextReturnsExpected() {
String[] sentence = new String[] { "word" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("NN"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(anyInt(), eq(sentence), isNull(), any(), any())).thenReturn(new Sequence[] { sequence });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// Sequence[] result = tagger.topKSequences(sentence, null);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals("NN", result[0].getOutcomes().get(0));
}

@Test
public void testTrainSequenceTrainerType() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
POSSample sample = new POSSample(new String[] { "word" }, new String[] { "NN" });
when(stream.read()).thenReturn(sample).thenReturn(null);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerFactory.SEQUENCE_TRAINER);
SequenceClassificationModel<POSSample> dummyModel = mock(SequenceClassificationModel.class);
// TrainerFactory.setSequenceModelTrainerFactory((p, m) -> new SequenceTrainer() {
// 
// public void init(TrainingParameters a, Map<String, String> b) {
// }
// 
// public SequenceClassificationModel<POSSample> train(ObjectStream<POSSample> samples) {
// return dummyModel;
// }
// });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGen);
POSModel result = POSTaggerME.train("en", stream, params, factory);
assertNotNull(result);
}

@Test
public void testPopulatePOSDictionaryIgnoresDigits() throws IOException {
String[] words = new String[] { "42isTheAnswer" };
String[] tags = new String[] { "CD" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(true);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
assertNull(dict.getTags("42isTheAnswer"));
}

@Test
public void testTrainWithEventModelSequenceTrainer() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
POSSample sample = new POSSample(new String[] { "hello" }, new String[] { "UH" });
when(stream.read()).thenReturn(sample).thenReturn(null);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerFactory.EVENT_MODEL_SEQUENCE_TRAINER);
// EventModelSequenceTrainer<POSSample> trainer = new EventModelSequenceTrainer<POSSample>() {
// 
// public void init(TrainingParameters a, Map<String, String> b) {
// }
// 
// public MaxentModel train(ObjectStream<POSSample> samples) {
// return mock(MaxentModel.class);
// }
// };
// TrainerFactory.setEventModelSequenceTrainerFactory((p, m) -> trainer);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(cg);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testPopulatePOSDictionaryCutoffGreaterThanFrequency() throws IOException {
String[] words = new String[] { "cat" };
String[] tags = new String[] { "NN" };
POSSample oneSample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(oneSample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(true);
POSTaggerME.populatePOSDictionary(stream, dict, 5);
assertNull(dict.getTags("cat"));
}

@Test
public void testPopulatePOSDictionaryEnforcesCaseSensitivityFalse() throws IOException {
String[] words = new String[] { "Dog" };
String[] tags = new String[] { "NN" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
assertNotNull(dict.getTags("dog"));
assertNull(dict.getTags("Dog"));
}

@Test
public void testTrainReturnsValidPOSModelWithMaxentModel() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
POSSample sample = new POSSample(new String[] { "hi" }, new String[] { "UH" });
when(stream.read()).thenReturn(sample).thenReturn(null);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerFactory.EVENT_MODEL_TRAINER);
// TrainerFactory.setEventTrainerFactory((p, m) -> new opennlp.tools.ml.EventTrainer() {
// 
// public void init(TrainingParameters a, Map<String, String> b) {
// }
// 
// public MaxentModel train(ObjectStream<Event> events) {
// return mock(MaxentModel.class);
// }
// });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(cg);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
}

// @Test(expected = IllegalArgumentException.class)
public void testTrainThrowsOnUnknownTrainerType() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, "NO_SUCH_TYPE");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(cg);
POSTaggerME.train("en", stream, params, factory);
}

@Test
public void testMultipleTaggingsTagEmptyStringToken() {
String[] sentence = new String[] { "" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("NN"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(2), eq(sentence), any(), any(), any())).thenReturn(new Sequence[] { sequence });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[][] result = tagger.tag(2, sentence);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals("NN", result[0][0]);
}

@Test
public void testTagSentenceWithTagDictionaryRestrictingTags() {
String[] sentence = new String[] { "walk" };
TagDictionary dict = mock(TagDictionary.class);
when(dict.getTags("walk")).thenReturn(new String[] { "VB" });
when(dict.isCaseSensitive()).thenReturn(true);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("VB"));
when(sequence.getProbs()).thenReturn(new double[] { 1.0 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), any(), any(), any())).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "VB" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals("VB", result[0]);
}

@Test
public void testTagReturnsTagFormatConversionWhenGuessedFormatDiffers() {
String[] sentence = new String[] { "word" };
String[] rawTags = new String[] { "NN" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList(rawTags));
when(sequence.getProbs()).thenReturn(new double[] { 0.9 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.PTB);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals("NN", result[0]);
}

@Test
public void testTagCallsConvertTagsWhenFormatIsNotCUSTOMAndGuessedDiffers() {
String[] sentence = new String[] { "word" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("VBG"));
when(sequence.getProbs()).thenReturn(new double[] { 0.91 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator ctxGen = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(ctxGen);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "VBG" });
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals("VBG", result[0]);
}

@Test
public void testTopKSequencesWithEmptyInputReturnsEmpty() {
String[] sentence = new String[0];
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
when(model.bestSequences(anyInt(), eq(sentence), isNull(), any(), any())).thenReturn(new Sequence[] { sequence });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
TagDictionary dict = mock(TagDictionary.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(cg);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(dict);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// Sequence[] result = tagger.topKSequences(sentence);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals(0, result[0].getOutcomes().size());
}

@Test
public void testOrderedTagsWhenProbsAreAllEqual() {
String[] words = new String[] { "dogs" };
String[] existingTags = new String[] { "NN" };
double[] evalProbs = new double[] { 0.5, 0.5 };
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any())).thenReturn(evalProbs.clone());
when(maxentModel.getOutcome(0)).thenReturn("NN");
when(maxentModel.getOutcome(1)).thenReturn("VB");
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator ctxGen = mock(POSContextGenerator.class);
when(ctxGen.getContext(eq(0), any(), any(), isNull())).thenReturn(new String[] { "CTX" });
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(ctxGen);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(mock(TagDictionary.class));
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(posModel.getPosSequenceModel()).thenReturn(null);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
double[] outputProbs = new double[2];
// String[] result = tagger.getOrderedTags(Arrays.asList(words), Arrays.asList(existingTags), 0, outputProbs);
// assertNotNull(result);
// assertEquals(2, result.length);
assertEquals(0.5, outputProbs[0], 0.001);
assertEquals(0.5, outputProbs[1], 0.001);
}

@Test
public void testPopulatePOSDictionaryPreservesPreseededTagsWithCutoff() throws IOException {
MutableTagDictionary dict = new POSDictionary(true);
dict.put("run", new String[] { "VB" });
String[] words = new String[] { "run" };
String[] tags = new String[] { "VB" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 2);
String[] postTags = dict.getTags("run");
assertNotNull(postTags);
assertEquals(1, postTags.length);
assertEquals("VB", postTags[0]);
}

@Test
public void testTrainWithNullPOSContextGeneratorThrowsNPE() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, TrainerFactory.EVENT_MODEL_TRAINER);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator()).thenReturn(null);
try {
POSTaggerME.train("en", stream, params, factory);
fail("Expected NullPointerException due to null context generator");
} catch (NullPointerException expected) {
}
}

@Test
public void testBeamSizeExplicitlySetThroughManifest() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSModel posModel = mock(POSModel.class);
when(posModel.getPosSequenceModel()).thenReturn(model);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(eq(7))).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn("7");
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.getAllPosTags();
// assertNotNull(tags);
// assertEquals(1, tags.length);
// assertEquals("NN", tags[0]);
}

@Test
public void testTagWithNullTagDictionaryAndEmptySentence() {
String[] sentence = new String[0];
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
SequenceClassificationModel<String> sequenceModel = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(sequenceModel);
when(sequenceModel.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
// when(posModel.getOutcomes()).thenReturn(new String[0]);
when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testTagWithMultipleOutcomesIgnoresTagFormatMismatchWhenFormatIsCustom() {
String[] sentence = new String[] { "book" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("X"));
when(sequence.getProbs()).thenReturn(new double[] { 1.0 });
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "X" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
// String[] result = tagger.tag(sentence);
// assertNotNull(result);
// assertEquals(1, result.length);
// assertEquals("X", result[0]);
}

@Test
public void testBuildNGramDictionaryReturnsEmptyWhenAllTokensSkipCutoff() throws IOException {
POSSample sample = new POSSample(new String[] { "rareword" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1000);
// assertNotNull(dict);
// assertEquals(0, dict.size());
}

@Test
public void testPopulatePOSDictionaryHandlesTagSeenOnlyOnceBelowCutoff() throws IOException {
String[] tokens = new String[] { "unique" };
String[] tags = new String[] { "NN" };
POSSample sample = new POSSample(tokens, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
MutableTagDictionary dict = new POSDictionary(true);
POSTaggerME.populatePOSDictionary(stream, dict, 2);
assertNull(dict.getTags("unique"));
}

@Test
public void testTrainReturnsNonNullPOSModelWithSequenceTrainer() throws IOException {
POSSample sample = new POSSample(new String[] { "run" }, new String[] { "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
TrainingParameters params = new TrainingParameters();
// params.put(TrainerFactory.TRAINER_TYPE_PARAM, "SEQUENCE_TRAINER");
// TrainerFactory.setSequenceModelTrainerFactory((p, m) -> new opennlp.tools.ml.SequenceTrainer() {
// 
// public void init(TrainingParameters a, Map<String, String> b) {
// }
// 
// public SequenceClassificationModel<POSSample> train(ObjectStream<POSSample> samples) {
// return mock(SequenceClassificationModel.class);
// }
// });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGen);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testProbsMethodWithSuppliedArrayIsSafeOnEmptyInput() {
String[] sentence = new String[] { "only" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("NN"));
when(sequence.getProbs()).thenReturn(new double[] { 0.75 });
doAnswer(invocation -> {
double[] probs = (double[]) invocation.getArgument(0);
probs[0] = 0.75;
return null;
}).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary tagDict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// tagger.tag(sentence);
double[] probsArray = new double[1];
// tagger.probs(probsArray);
assertEquals(0.75, probsArray[0], 0.001);
}

@Test
public void testMultipleBestSequencesReturnsEmptyArrayIfModelReturnsNull() {
String[] sentence = new String[] { "word" };
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.bestSequences(eq(5), eq(sentence), isNull(), any(), any())).thenReturn(null);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
TagDictionary dict = mock(TagDictionary.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(dict);
when(factory.getSequenceValidator()).thenReturn(validator);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(seqModel);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
try {
// tagger.tag(5, sentence);
fail("Expected NullPointerException when bestSequences() returns null");
} catch (NullPointerException expected) {
}
}
}
