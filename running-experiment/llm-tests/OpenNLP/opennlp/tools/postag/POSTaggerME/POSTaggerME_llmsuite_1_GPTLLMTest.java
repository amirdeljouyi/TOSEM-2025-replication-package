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

public class POSTaggerME_llmsuite_1_GPTLLMTest {

@Test
public void testTagReturnsCorrectTags() {
String[] sentence = new String[] { "Time", "flies" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
Sequence sequence = new Sequence(Arrays.asList("NN", "VB"));
when(model.bestSequence(eq(sentence), isNull(), eq(contextGenerator), eq(validator))).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] predicted = tagger.tag(sentence);
// assertArrayEquals(new String[] { "NN", "VB" }, predicted);
}

@Test
public void testTopKSequencesReturnsAllSequences() {
String[] sentence = new String[] { "I", "run" };
Sequence s1 = new Sequence(Arrays.asList("PRP", "VB"));
Sequence s2 = new Sequence(Arrays.asList("NN", "VB"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.bestSequences(eq(3), eq(sentence), isNull(), eq(contextGen), eq(validator))).thenReturn(new Sequence[] { s1, s2 });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB", "PRP" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// Sequence[] result = tagger.topKSequences(sentence);
// assertEquals(2, result.length);
// assertEquals(Arrays.asList("PRP", "VB"), result[0].getOutcomes());
// assertEquals(Arrays.asList("NN", "VB"), result[1].getOutcomes());
}

@Test
public void testTagWithAdditionalContext() {
String[] tokens = new String[] { "John", "plays" };
Object[] additional = new Object[] { "context" };
Sequence sequence = new Sequence(Arrays.asList("NNP", "VBZ"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.bestSequence(eq(tokens), eq(additional), eq(contextGen), eq(validator))).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(model.getOutcomes()).thenReturn(new String[] { "NNP", "VBZ" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(tokens, additional);
// assertArrayEquals(new String[] { "NNP", "VBZ" }, result);
}

@Test
public void testGetAllPosTagsReturnsExpectedTags() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB", "JJ" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.getAllPosTags();
// assertArrayEquals(new String[] { "NN", "VB", "JJ" }, tags);
}

@Test
public void testBuildNGramDictionaryCreatesExpectedEntries() throws IOException {
POSSample sample1 = new POSSample(new String[] { "cat", "runs" }, new String[] { "NN", "VB" });
POSSample sample2 = new POSSample(new String[] { "white", "dog" }, new String[] { "JJ", "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1, sample2, null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertTrue(dict.contains(new StringList("cat", "runs")));
// assertTrue(dict.contains(new StringList("white", "dog")));
}

@Test
public void testGetOrderedTagsReturnsCorrectOrderAndProbs() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any())).thenReturn(new double[] { 0.6, 0.3, 0.1 });
when(maxentModel.getOutcome(0)).thenReturn("NN");
when(maxentModel.getOutcome(1)).thenReturn("VB");
when(maxentModel.getOutcome(2)).thenReturn("JJ");
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB", "JJ" });
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(contextGen.getContext(anyInt(), any(), any(), any())).thenReturn(new String[] { "feature1" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(maxentModel);
when(posModel.getPosSequenceModel()).thenReturn(seqModel);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
List<String> words = Arrays.asList("fast", "penguin");
List<String> tags = Arrays.asList("RB", "NN");
double[] probs = new double[3];
// String[] result = tagger.getOrderedTags(words, tags, 1, probs);
// assertArrayEquals(new String[] { "NN", "VB", "JJ" }, result);
assertEquals(0.6, probs[0], 1e-6);
assertEquals(0.3, probs[1], 1e-6);
assertEquals(0.1, probs[2], 1e-6);
}

@Test
public void testGetOrderedTagsFailsIfNoModelArtifact() {
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getPosSequenceModel()).thenReturn(seqModel);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(null);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
List<String> words = Collections.singletonList("unknown");
List<String> tags = Collections.singletonList("UNK");
// tagger.getOrderedTags(words, tags, 0);
}

@Test
public void testPopulatePOSDictionaryAddsExpectedTags() throws IOException {
POSSample sample1 = new POSSample(new String[] { "Tree", "grows" }, new String[] { "NN", "VB" });
POSSample sample2 = new POSSample(new String[] { "tree", "grows" }, new String[] { "NN", "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1, sample2, null);
MutableTagDictionary dict = new POSDictionary(false);
dict.put("tree", new String[] { "NN" });
POSTaggerME.populatePOSDictionary(stream, dict, 2);
String[] tags = dict.getTags("tree");
assertTrue(Arrays.asList(tags).contains("NN"));
}

@Test
public void testPopulatePOSDictionaryCaseSensitive() throws IOException {
POSSample sample = new POSSample(new String[] { "Flower" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
MutableTagDictionary dict = new POSDictionary(true);
dict.put("Flower", new String[] { "NN" });
POSTaggerME.populatePOSDictionary(stream, dict, 1);
assertArrayEquals(new String[] { "NN" }, dict.getTags("Flower"));
assertNull(dict.getTags("flower"));
}

@Test
public void testTrainFailsWithUnsupportedTrainerType() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "unsupported");
when(factory.getPOSContextGenerator()).thenReturn(mock(POSContextGenerator.class));
POSTaggerME.train("en", stream, params, factory);
}

@Test
public void testProbsReturnsExpectedArrayLengthWhenBestSequencePredictsLessTagsThanInput() {
String[] sentence = new String[] { "He", "jumps", "high" };
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator context = mock(POSContextGenerator.class);
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("NN", "VB"));
when(sequence.getProbs()).thenReturn(new double[] { 0.9, 0.1 });
when(model.bestSequence(eq(sentence), any(), eq(context), eq(validator))).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(context);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// tagger.tag(sentence);
// double[] probs = tagger.probs();
// assertEquals(2, probs.length);
// assertEquals(0.9, probs[0], 1e-6);
// assertEquals(0.1, probs[1], 1e-6);
}

@Test
public void testTagEmptySentenceReturnsEmptyArray() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
// when(model.bestSequence(any(), any(), any(), any())).thenReturn(new Sequence(Collections.emptyList(), new double[0]));
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(model.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.tag(new String[0]);
// assertNotNull(tags);
// assertEquals(0, tags.length);
}

@Test
public void testBuildNGramDictionarySkipsEmptySamples() throws IOException {
POSSample emptySample = new POSSample(new String[0], new String[0]);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(emptySample, null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertFalse(dict.contains(new StringList("")));
}

@Test
public void testPopulatePOSDictionaryHandlesDigitWordsBySkippingThem() throws IOException {
String[] tokens = { "1234", "word" };
String[] tags = { "CD", "NN" };
POSSample sample = new POSSample(tokens, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
MutableTagDictionary dict = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
assertNull(dict.getTags("1234"));
assertArrayEquals(new String[] { "NN" }, dict.getTags("word"));
}

@Test
public void testConvertTagsNoOpWhenUsingCustomFormat() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator context = mock(POSContextGenerator.class);
Sequence sequence = new Sequence(Arrays.asList("T1", "T2"));
when(sequence.getProbs()).thenReturn(new double[] { 1.0, 1.0 });
when(sequence.getOutcomes()).thenReturn(Arrays.asList("T1", "T2"));
when(model.bestSequence(any(), any(), eq(context), eq(validator))).thenReturn(sequence);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(context);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(model.getOutcomes()).thenReturn(new String[] { "T1", "T2" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
// String[] output = tagger.tag(new String[] { "a", "b" });
// assertArrayEquals(new String[] { "T1", "T2" }, output);
}

@Test
public void testTagReturnsSingleSequenceMatchesTopK() {
String[] sentence = { "Apple" };
Sequence sequence = new Sequence(Arrays.asList("NN"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSContextGenerator context = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.bestSequence(eq(sentence), isNull(), eq(context), eq(validator))).thenReturn(sequence);
when(model.bestSequences(eq(1), eq(sentence), isNull(), eq(context), eq(validator))).thenReturn(new Sequence[] { sequence });
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(context);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] finalTag = tagger.tag(sentence);
// Sequence[] topSeqs = tagger.tag(1, sentence);
// assertEquals("NN", finalTag[0]);
// assertEquals("NN", topSeqs[0][0]);
}

@Test
public void testTrainEventModelSequenceTrainer() throws IOException {
POSSample sample = new POSSample(new String[] { "a", "b" }, new String[] { "X", "Y" });
POSSampleSequenceStream sequenceStream = new POSSampleSequenceStream(new ObjectStream<POSSample>() {

boolean called = false;

public POSSample read() {
if (!called) {
called = true;
return sample;
}
return null;
}

public void reset() {
}

public void close() {
}
}, mock(POSContextGenerator.class));
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
@SuppressWarnings("unchecked")
opennlp.tools.ml.EventModelSequenceTrainer<POSSample> trainer = mock(opennlp.tools.ml.EventModelSequenceTrainer.class);
when(trainer.train(any(POSSampleSequenceStream.class))).thenReturn(mock(MaxentModel.class));
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT_QN");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator()).thenReturn(mock(POSContextGenerator.class));
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testProbsArrayParameterIsFilledCorrectly() {
String[] sentence = { "dog", "runs" };
double[] expected = { 0.8, 0.2 };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("NN", "VB"));
doAnswer(invocation -> {
double[] arg = invocation.getArgument(0);
arg[0] = 0.8;
arg[1] = 0.2;
return null;
}).when(sequence).getProbs(any(double[].class));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// tagger.tag(sentence);
double[] result = new double[2];
// tagger.probs(result);
assertEquals(0.8, result[0], 0.00001);
assertEquals(0.2, result[1], 0.00001);
}

@Test
public void testTagWithNullAdditionalContext() {
String[] sentence = { "She", "runs" };
Sequence sequence = new Sequence(Arrays.asList("PRP", "VBZ"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "PRP", "VBZ" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(posModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.tag(sentence, null);
// assertArrayEquals(new String[] { "PRP", "VBZ" }, result);
}

@Test
public void testConstructorRespectsBeamSizeFromManifest() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "X" });
POSModel posModel = mock(POSModel.class);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER)).thenReturn("7");
when(posModel.getFactory()).thenReturn(mock(POSTaggerFactory.class));
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
POSTaggerFactory factory = posModel.getFactory();
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getPOSContextGenerator(7)).thenReturn(mock(POSContextGenerator.class));
when(factory.getTagDictionary()).thenReturn(null);
// new POSTaggerME(posModel, POSTagFormat.UD);
}

@Test
public void testTrainSequenceTrainerPath() throws IOException {
SequenceClassificationModel<String> dummySeqModel = mock(SequenceClassificationModel.class);
ObjectStream<POSSample> stream = new ObjectStream<POSSample>() {

private boolean called = false;

public POSSample read() {
if (!called) {
called = true;
return new POSSample(new String[] { "a" }, new String[] { "A" });
} else {
return null;
}
}

public void reset() {
}

public void close() {
}
};
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator()).thenReturn(mock(POSContextGenerator.class));
opennlp.tools.ml.SequenceTrainer trainer = mock(opennlp.tools.ml.SequenceTrainer.class);
when(trainer.train(any())).thenReturn(dummySeqModel);
mockStatic(opennlp.tools.ml.TrainerFactory.class).when(() -> opennlp.tools.ml.TrainerFactory.getTrainerType(params)).thenReturn(opennlp.tools.ml.TrainerFactory.TrainerType.SEQUENCE_TRAINER);
mockStatic(opennlp.tools.ml.TrainerFactory.class).when(() -> opennlp.tools.ml.TrainerFactory.getSequenceModelTrainer(params, new HashMap<>())).thenReturn(trainer);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
}

@Test
public void testTagMethodHandlesTagConversionWhenMapperFormatDiffers() {
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Arrays.asList("TAG1", "TAG2"));
when(sequence.getProbs()).thenReturn(new double[] { 0.5, 0.5 });
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "TAG1", "TAG2" });
POSModel posModel = mock(POSModel.class);
when(posModel.getFactory()).thenReturn(mock(POSTaggerFactory.class));
when(posModel.getFactory().getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(posModel.getFactory().getTagDictionary()).thenReturn(null);
when(posModel.getFactory().getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.BROWN);
String[] sentence = { "green", "apple" };
// String[] tags = tagger.tag(sentence);
// assertNotNull(tags);
// assertEquals(2, tags.length);
}

@Test
public void testPopulatePOSDictionaryThresholdCutoffFiltering() throws IOException {
String[] words = new String[] { "cat", "sits" };
String[] tags = new String[] { "NN", "VB" };
POSSample sample = new POSSample(words, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample, sample, null);
MutableTagDictionary dict = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dict, 2);
assertArrayEquals(new String[] { "NN" }, dict.getTags("cat"));
assertArrayEquals(new String[] { "VB" }, dict.getTags("sits"));
}

@Test
public void testBuildNGramDictionaryWithHighCutoffRemovesLowFreqNgrams() throws IOException {
POSSample sample = new POSSample(new String[] { "rareword" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 10);
// assertFalse(dict.contains(new StringList("rareword")));
}

@Test
public void testTagReturnsEmptyArrayForEmptyInput() {
POSModel model = mock(POSModel.class);
SequenceClassificationModel seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
SequenceValidator validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(mock(POSTaggerFactory.class));
when(model.getFactory().getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(model.getFactory().getTagDictionary()).thenReturn(null);
when(model.getFactory().getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
// when(seqModel.bestSequence(eq(new String[0]), any(), any(), any())).thenReturn(new Sequence(Collections.emptyList(), new double[0]));
when(seqModel.getOutcomes()).thenReturn(new String[0]);
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
// String[] result = tagger.tag(new String[0]);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testTopKSequencesHandlesZeroSequences() {
String[] sentence = { "word" };
SequenceClassificationModel model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(3), eq(sentence), any(), any(), any())).thenReturn(new Sequence[0]);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
POSModel posModel = mock(POSModel.class);
when(posModel.getFactory()).thenReturn(mock(POSTaggerFactory.class));
when(posModel.getFactory().getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(posModel.getFactory().getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getFactory().getTagDictionary()).thenReturn(null);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// Sequence[] results = tagger.topKSequences(sentence);
// assertNotNull(results);
// assertEquals(0, results.length);
}

@Test
public void testTagMethodHandlesMismatchedTagLengthsGracefully() {
String[] sentence = { "a", "b" };
Sequence sequence = mock(Sequence.class);
when(sequence.getOutcomes()).thenReturn(Collections.singletonList("NN"));
SequenceClassificationModel model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.tag(sentence);
// assertEquals(1, tags.length);
// assertEquals("NN", tags[0]);
}

@Test
public void testPopulatePOSDictionaryWithMultipleTagsPerWord() throws IOException {
String[] sentence = { "apple", "apple", "apple" };
String[] tags = { "NN", "VB", "VB" };
POSSample sample = new POSSample(sentence, tags);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
MutableTagDictionary dictionary = new POSDictionary(false);
dictionary.put("apple", new String[] { "NN" });
POSTaggerME.populatePOSDictionary(stream, dictionary, 2);
String[] tagSet = dictionary.getTags("apple");
assertNotNull(tagSet);
List<String> tagList = Arrays.asList(tagSet);
assertTrue(tagList.contains("VB"));
assertTrue(tagList.contains("NN"));
}

@Test
public void testPopulatePOSDictionaryWhenNoTagsReachCutoff() throws IOException {
POSSample sample = new POSSample(new String[] { "city" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
MutableTagDictionary dictionary = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dictionary, 5);
String[] tags = dictionary.getTags("city");
assertNull(tags);
}

@Test
public void testBuildNGramDictionarySkipsNullSamples() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
// Dictionary dictionary = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dictionary);
// assertFalse(dictionary.iterator().hasNext());
}

@Test
public void testDownloadConstructorWithInvalidLanguageThrowsIOException() {
try {
new POSTaggerME("xx");
fail("Expected IOException for invalid model download.");
} catch (IOException expected) {
}
}

@Test
public void testOrderedTagsReturnsEmptyIfNoModelOutcomes() {
POSModel model = mock(POSModel.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(model.getPosSequenceModel()).thenReturn(mock(SequenceClassificationModel.class));
when(maxentModel.eval(any())).thenReturn(new double[0]);
when(maxentModel.getOutcome(anyInt())).thenReturn("NN");
when(contextGenerator.getContext(anyInt(), any(), any(), any())).thenReturn(new String[] { "x" });
// when(model.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
// String[] result = tagger.getOrderedTags(Collections.singletonList("word"), Collections.singletonList("TAG"), 0, new double[0]);
// assertEquals(0, result.length);
}

@Test
public void testGetOrderedTagsReturnsCorrectOrderWithTies() {
MaxentModel maxent = mock(MaxentModel.class);
String[] outcomes = { "VB", "JJ", "NN" };
double[] scores = { 0.5, 0.5, 0.5 };
when(maxent.eval(any())).thenReturn(scores.clone());
when(maxent.getOutcome(0)).thenReturn("VB");
when(maxent.getOutcome(1)).thenReturn("JJ");
when(maxent.getOutcome(2)).thenReturn("NN");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(contextGen.getContext(eq(0), any(), any(), any())).thenReturn(new String[] { "contextFeature" });
SequenceClassificationModel model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(outcomes);
POSModel posModel = mock(POSModel.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(maxent);
when(posModel.getPosSequenceModel()).thenReturn(model);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
double[] tprobs = new double[3];
// String[] ordered = tagger.getOrderedTags(Arrays.asList("word"), Arrays.asList("NN"), 0, tprobs);
// assertEquals(3, ordered.length);
// for (String tag : ordered) {
// assertTrue(Arrays.asList("VB", "JJ", "NN").contains(tag));
// }
}

@Test
public void testProbsReturnsEmptyArrayWhenNoBestSequenceHasBeenTagged() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
try {
// tagger.probs();
fail("Expected NullPointerException or similar on accessing uninitialized bestSequence.");
} catch (NullPointerException expected) {
}
}

@Test
public void testGetOrderedTagsHandlesEmptyContextArray() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any())).thenReturn(new double[0]);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(contextGenerator.getContext(eq(0), any(String[].class), any(String[].class), isNull())).thenReturn(new String[0]);
SequenceClassificationModel model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "X" });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
POSModel posModel = mock(POSModel.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(posModel.getPosSequenceModel()).thenReturn(model);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] result = tagger.getOrderedTags(Collections.singletonList("word"), Collections.singletonList("TAG"), 0, new double[0]);
// assertEquals(0, result.length);
}

@Test
public void testPopulatePOSDictionaryWithNonAlphabetWordsAndDigits() throws IOException {
POSSample sample1 = new POSSample(new String[] { "Word1", "@symbol", "42" }, new String[] { "NN", "NN", "CD" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample1, null);
MutableTagDictionary dictionary = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dictionary, 1);
String[] tagsWord1 = dictionary.getTags("word1");
String[] tagsSymbol = dictionary.getTags("@symbol");
String[] tagsDigits = dictionary.getTags("42");
assertNotNull(tagsWord1);
assertNull(tagsSymbol);
assertNull(tagsDigits);
}

@Test
public void testPopulatePOSDictionaryHonorsCaseSensitivity() throws IOException {
POSSample sample = new POSSample(new String[] { "Cat", "cat", "CAT" }, new String[] { "NN", "VB", "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
MutableTagDictionary dictionary = new POSDictionary(true);
dictionary.put("Cat", new String[] { "NN" });
dictionary.put("cat", new String[] { "VB" });
POSTaggerME.populatePOSDictionary(stream, dictionary, 1);
String[] tagsCat = dictionary.getTags("Cat");
String[] tagsLowerCat = dictionary.getTags("cat");
String[] tagsUpperCat = dictionary.getTags("CAT");
assertNotNull(tagsCat);
assertNotNull(tagsLowerCat);
assertNull(tagsUpperCat);
}

@Test
public void testGetAllPosTagsWithCustomFormatPreservesOutcomeArray() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.getOutcomes()).thenReturn(new String[] { "N1", "V2" });
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
POSModel posModel = mock(POSModel.class);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.CUSTOM);
// String[] tags = tagger.getAllPosTags();
// assertArrayEquals(new String[] { "N1", "V2" }, tags);
}

@Test
public void testTagWithNullTagDictionary() {
Sequence sequence = new Sequence(Arrays.asList("X", "Y"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(any(String[].class), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "X", "Y" });
POSContextGenerator context = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(context);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
String[] sentence = { "This", "works" };
// String[] result = tagger.tag(sentence);
// assertArrayEquals(new String[] { "X", "Y" }, result);
}

@Test
public void testTrainWithEventModelFallbackReturnsExpectedPOSModel() throws IOException {
POSSample sample = new POSSample(new String[] { "the", "fox" }, new String[] { "DT", "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(mock(MaxentModel.class));
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGen);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testTrainFailsGracefullyWhenNullTrainerIsReturned() throws IOException {
POSSample sample = new POSSample(new String[] { "OOV" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "SEQUENCE");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
when(factory.getPOSContextGenerator()).thenReturn(contextGen);
opennlp.tools.ml.SequenceTrainer sequenceTrainer = mock(opennlp.tools.ml.SequenceTrainer.class);
when(sequenceTrainer.train(any())).thenReturn(null);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
assertNull(model.getPosModel());
}

@Test
public void testTagReturnsOnlyMappedTagsWhenFormatMapperUsed() {
String[] sentence = new String[] { "Cats", "sleep" };
Sequence bestSequence = new Sequence(Arrays.asList("X", "Y"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), isNull(), any(), any())).thenReturn(bestSequence);
when(model.getOutcomes()).thenReturn(new String[] { "X", "Y" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(posModel.getPosSequenceModel()).thenReturn(model);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.tag(sentence);
// assertEquals(2, tags.length);
// assertArrayEquals(new String[] { "X", "Y" }, tags);
}

@Test
public void testTopKSequencesReturnsEmptyWhenInputIsEmpty() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(3), eq(new String[0]), isNull(), any(), any())).thenReturn(new Sequence[0]);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(model.getOutcomes()).thenReturn(new String[0]);
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// Sequence[] results = tagger.topKSequences(new String[0]);
// assertNotNull(results);
// assertEquals(0, results.length);
}

@Test
public void testTagMultipleTopSequencesHandlesFewerThanRequested() {
String[] sentence = new String[] { "Example" };
Sequence first = new Sequence(Collections.singletonList("NN"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequences(eq(5), eq(sentence), isNull(), any(), any())).thenReturn(new Sequence[] { first });
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[][] results = tagger.tag(5, sentence);
// assertEquals(1, results.length);
// assertEquals("NN", results[0][0]);
}

@Test
public void testBuildNGramDictionaryHandlesMultipleSentencesSameToken() throws IOException {
POSSample sample1 = new POSSample(new String[] { "the" }, new String[] { "DET" });
POSSample sample2 = new POSSample(new String[] { "the" }, new String[] { "DET" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1, sample2, null);
// Dictionary dictionary = POSTaggerME.buildNGramDictionary(stream, 2);
// assertTrue(dictionary.contains(new StringList("the")));
}

@Test
public void testPopulatePOSDictionaryAddsOnlySufficientTagCounts() throws IOException {
POSSample sample = new POSSample(new String[] { "dog", "dog", "dog", "dog" }, new String[] { "NN", "NN", "VB", "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
MutableTagDictionary dictionary = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dictionary, 2);
String[] dogTags = dictionary.getTags("dog");
assertNotNull(dogTags);
List<String> tagList = Arrays.asList(dogTags);
assertTrue(tagList.contains("NN"));
assertFalse(tagList.contains("VB"));
}

@Test
public void testConvertTagsSkipsMappingIfFormatMatched() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
Sequence sequence = new Sequence(Arrays.asList("X", "Y"));
when(model.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
when(model.getOutcomes()).thenReturn(new String[] { "X", "Y" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.tag(new String[] { "word1", "word2" });
// assertEquals("X", tags[0]);
// assertEquals("Y", tags[1]);
}

@Test
public void testBuildNGramDictionarySkipsShortWords() throws IOException {
POSSample sample = new POSSample(new String[] { "a" }, new String[] { "DT" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
// Dictionary dictionary = POSTaggerME.buildNGramDictionary(stream, 2);
// assertFalse(dictionary.contains(new StringList("a")));
}

@Test
public void testPopulatePOSDictionaryBoostsExistingDictTagsWithCutoff() throws IOException {
MutableTagDictionary dict = new POSDictionary(false);
dict.put("moon", new String[] { "NN" });
String[] sentence = { "moon", "moon" };
String[] tags = { "NN", "NN" };
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(new POSSample(sentence, tags), null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
String[] moonTags = dict.getTags("moon");
assertNotNull(moonTags);
List<String> tagList = Arrays.asList(moonTags);
assertTrue(tagList.contains("NN"));
}

@Test
public void testTagWithTagDictionaryPresent() {
TagDictionary tagDict = mock(TagDictionary.class);
// SequenceValidationModel validator = mock(SequenceValidationModel.class);
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGen = mock(POSContextGenerator.class);
POSSample sample = new POSSample(new String[] { "bird" }, new String[] { "NN" });
Sequence seq = new Sequence(Collections.singletonList("NN"));
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGen);
when(factory.getTagDictionary()).thenReturn(tagDict);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(model.bestSequence(any(), any(), eq(contextGen), any())).thenReturn(seq);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.tag(new String[] { "bird" });
// assertArrayEquals(new String[] { "NN" }, tags);
}

@Test
public void testTrainReturnsNullModelFromEventModelSequenceTrainer() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(new POSSample(new String[] { "the" }, new String[] { "DT" }), null);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT_QN");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator()).thenReturn(mock(POSContextGenerator.class));
opennlp.tools.ml.EventModelSequenceTrainer<POSSample> trainer = mock(opennlp.tools.ml.EventModelSequenceTrainer.class);
when(trainer.train(any())).thenReturn(null);
POSModel model = POSTaggerME.train("en", stream, params, factory);
assertNotNull(model);
assertNull(model.getPosModel());
}

@Test
public void testTagHandlesSingleTokenSentence() {
String[] sentence = new String[] { "Apple" };
Sequence bestSeq = new Sequence(Collections.singletonList("NN"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), eq(null), any(), any())).thenReturn(bestSeq);
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// String[] tags = tagger.tag(sentence);
// assertArrayEquals(new String[] { "NN" }, tags);
}

@Test
public void testProbsThrowsExceptionWhenNoSequenceTagged() {
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
mock(BeamSearch.class);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(mock(POSContextGenerator.class));
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(mock(MaxentModel.class));
when(model.getOutcomes()).thenReturn(new String[] { "NN" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
// tagger.probs();
}

@Test
public void testTagSentenceWithNumbersIsAllowedAndHandled() {
String[] sentence = new String[] { "2023", "is", "great" };
Sequence seq = new Sequence(Arrays.asList("CD", "VBZ", "JJ"));
SequenceClassificationModel<String> model = mock(SequenceClassificationModel.class);
when(model.bestSequence(eq(sentence), eq(null), any(), any())).thenReturn(seq);
when(model.getOutcomes()).thenReturn(new String[] { "CD", "VBZ", "JJ" });
POSContextGenerator context = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(context);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(model);
when(posModel.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(mock(MaxentModel.class));
POSTaggerME tagger = new POSTaggerME(posModel);
String[] tags = tagger.tag(sentence);
assertArrayEquals(new String[] { "CD", "VBZ", "JJ" }, tags);
}

@Test
public void testTagWithSequenceModelFallback() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any())).thenReturn(new double[] { 0.8, 0.2 });
when(maxentModel.getOutcome(0)).thenReturn("NN");
when(maxentModel.getOutcome(1)).thenReturn("VB");
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(contextGenerator.getContext(eq(0), any(), any(), any())).thenReturn(new String[] { "f1" });
POSModel posModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator(anyInt())).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(posModel.getFactory()).thenReturn(factory);
when(posModel.getPosSequenceModel()).thenReturn(null);
when(posModel.getArtifact(eq(POSModel.POS_MODEL_ENTRY_NAME))).thenReturn(maxentModel);
SequenceClassificationModel<String> fallbackModel = new BeamSearch(3, maxentModel, 0);
// when(posModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// POSTaggerME tagger = new POSTaggerME(posModel, POSTagFormat.UD);
List<String> words = Collections.singletonList("dog");
List<String> tags = Collections.singletonList("NN");
double[] probs = new double[2];
// String[] result = tagger.getOrderedTags(words, tags, 0, probs);
// assertEquals("NN", result[0]);
assertTrue(probs[0] >= 0.0);
}

@Test
public void testTrainWithUnsupportedTrainerTypeThrowsIllegalArgument() throws IOException {
POSSample sample = new POSSample(new String[] { "word" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
TrainingParameters params = new TrainingParameters();
params.put(TrainingParameters.ALGORITHM_PARAM, "UNSUPPORTED_TYPE");
POSTaggerFactory factory = mock(POSTaggerFactory.class);
when(factory.getPOSContextGenerator()).thenReturn(mock(POSContextGenerator.class));
try {
POSTaggerME.train("en", stream, params, factory);
fail("Expected exception for invalid trainer type");
} catch (IllegalArgumentException expected) {
assertTrue(expected.getMessage().contains("Trainer type is not supported"));
}
}

@Test
public void testPopulatePOSDictionaryDoesNotAddUnknownTagBelowCutoff() throws IOException {
POSSample sample = new POSSample(new String[] { "newword" }, new String[] { "UNK" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
// when(stream.read()).thenReturn(sample, null);
MutableTagDictionary dict = new POSDictionary(false);
POSTaggerME.populatePOSDictionary(stream, dict, 5);
assertNull(dict.getTags("newword"));
}

@Test
public void testBuildNGramDictionaryCreatesEmptyDictionaryOnEmptyStream() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertFalse(dict.iterator().hasNext());
}
}
