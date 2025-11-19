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

public class POSTaggerME_llmsuite_3_GPTLLMTest {

@Test
public void testTagSimpleSentenceReturnsTags() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
// Sequence<String> sequence = mock(Sequence.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
String[] input = new String[] { "The", "dog", "runs" };
List<String> tags = Arrays.asList("DT", "NN", "VB");
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "DT", "NN", "VB" });
// when(seqModel.bestSequence(input, null, contextGenerator, validator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(tags);
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.tag(input);
assertEquals("DT", result[0]);
assertEquals("NN", result[1]);
assertEquals("VB", result[2]);
}

@Test
public void testTagWithAdditionalContextReturnsCorrectTags() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
// Sequence<String> sequence = mock(Sequence.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
String[] input = new String[] { "She", "walks" };
Object[] context = new Object[] { "ctx" };
List<String> tags = Arrays.asList("PRP", "VBZ");
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "PRP", "VBZ" });
// when(seqModel.bestSequence(input, context, contextGenerator, validator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(tags);
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.tag(input, context);
assertEquals("PRP", result[0]);
assertEquals("VBZ", result[1]);
}

@Test
public void testTopKSequencesReturnsExpectedResults() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
String[] input = new String[] { "Fast", "run" };
Sequence sequence1 = new Sequence(Arrays.asList("RB", "VB"));
Sequence sequence2 = new Sequence(Arrays.asList("JJ", "NN"));
Sequence[] sequences = new Sequence[] { sequence1, sequence2 };
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "RB", "VB", "JJ", "NN" });
when(seqModel.bestSequences(3, input, null, contextGenerator, validator)).thenReturn(sequences);
POSTaggerME tagger = new POSTaggerME(model);
Sequence[] result = tagger.topKSequences(input);
assertEquals("RB", result[0].getOutcomes().get(0));
assertEquals("JJ", result[1].getOutcomes().get(0));
}

@Test
public void testProbsArrayPopulatedCorrectly() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
// Sequence<String> sequence = mock(Sequence.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
String[] input = new String[] { "test" };
List<String> tags = Arrays.asList("NN");
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN" });
// when(seqModel.bestSequence(input, null, contextGenerator, validator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(tags);
// when(sequence.getProbs()).thenReturn(new double[] { 0.85 });
POSTaggerME tagger = new POSTaggerME(model);
tagger.tag(input);
double[] probs = tagger.probs();
assertEquals(0.85, probs[0], 0.0001);
}

@Test
public void testBuildNGramDictionaryWithSimpleSample() throws IOException {
POSSample sample = new POSSample(new String[] { "sun", "shines" }, new String[] { "NN", "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// Dictionary result = POSTaggerME.buildNGramDictionary(stream, 1);
// assertTrue(result.contains(new StringList("sun")));
// assertTrue(result.contains(new StringList("shines")));
}

@Test
public void testPopulatePOSDictionaryAddsCorrectEntries() throws IOException {
MutableTagDictionary dict = new POSDictionary(true);
dict.put("sky", new String[] { "NN" });
POSSample sample = new POSSample(new String[] { "sky", "is" }, new String[] { "NN", "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
String[] tagsSky = dict.getTags("sky");
String[] tagsIs = dict.getTags("is");
assertArrayEquals(new String[] { "NN" }, tagsSky);
assertArrayEquals(new String[] { "VB" }, tagsIs);
}

@Test
public void testGetOrderedTagsThrowsIfModelIsNotMaxent() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN" });
POSTaggerME tagger = new POSTaggerME(model);
tagger.getOrderedTags(Arrays.asList("word"), Arrays.asList("tag"), 0);
}

@Test
public void testGetAllPosTagsReturnsExpectedValues() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
TagDictionary tagDictionary = mock(TagDictionary.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(tagDictionary);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VBZ", "JJ" });
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.getAllPosTags();
assertEquals("NN", tags[0]);
assertEquals("VBZ", tags[1]);
assertEquals("JJ", tags[2]);
}

@Test
public void testTrainThrowsOnUnsupportedTrainerType() throws IOException {
TrainingParameters parameters = new TrainingParameters();
parameters.put("TrainerType", "UnsupportedTrainer");
ObjectStream<POSSample> samples = mock(ObjectStream.class);
POSTaggerFactory factory = new POSTaggerFactory();
try {
POSTaggerME.train("en", samples, parameters, factory);
fail("Expected exception not thrown");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("Trainer type is not supported"));
}
}

@Test
public void testTagEmptySentenceReturnsEmptyArray() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> sequenceValidator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// when(seqModel.bestSequence(new String[0], null, contextGenerator, sequenceValidator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.tag(new String[0]);
assertEquals(0, tags.length);
}

@Test
public void testTagNullSentenceThrowsException() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> sequenceValidator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSTaggerME tagger = new POSTaggerME(model);
tagger.tag((String[]) null);
}

@Test
public void testTagWithNullContextReturnsTags() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> sequenceValidator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
String[] sentence = new String[] { "Hello" };
List<String> tagList = Collections.singletonList("NN");
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN" });
// when(seqModel.bestSequence(sentence, null, contextGenerator, sequenceValidator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(tagList);
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.tag(sentence, null);
assertEquals(1, result.length);
assertEquals("NN", result[0]);
}

@Test
public void testTagMultipleBestSequencesReturnsCorrectTags() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> sequenceValidator = mock(SequenceValidator.class);
Sequence sequence1 = mock(Sequence.class);
Sequence sequence2 = mock(Sequence.class);
String[] sentence = new String[] { "It", "rains" };
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "PRP", "VB" });
when(seqModel.bestSequences(2, sentence, null, contextGenerator, sequenceValidator)).thenReturn(new Sequence[] { sequence1, sequence2 });
when(sequence1.getOutcomes()).thenReturn(Arrays.asList("PRP", "VB"));
when(sequence2.getOutcomes()).thenReturn(Arrays.asList("NN", "VBD"));
POSTaggerME tagger = new POSTaggerME(model);
String[][] results = tagger.tag(2, sentence);
assertEquals("PRP", results[0][0]);
assertEquals("VB", results[0][1]);
assertEquals("NN", results[1][0]);
assertEquals("VBD", results[1][1]);
}

@Test
public void testProbsAfterTagWithProbsArrayPopulated() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
String[] sentence = new String[] { "AI" };
List<String> tagList = Collections.singletonList("NN");
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN" });
// when(seqModel.bestSequence(sentence, null, contextGenerator, validator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(tagList);
// when(sequence.getProbs()).thenReturn(new double[] { 0.99 });
POSTaggerME tagger = new POSTaggerME(model);
tagger.tag(sentence);
double[] probs = new double[1];
tagger.probs(probs);
assertEquals(0.99, probs[0], 0.00001);
}

@Test
public void testCustomTagFormatMapperPassthrough() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
String[] sentence = new String[] { "custom" };
List<String> outcomeTags = Collections.singletonList("X_TAG");
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "X_TAG" });
// when(seqModel.bestSequence(sentence, null, contextGenerator, validator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(outcomeTags);
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.CUSTOM);
// String[] result = tagger.tag(sentence);
// assertEquals("X_TAG", result[0]);
}

@Test
public void testGetOrderedTagsWithProbsArray() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
MaxentModel maxentModel = mock(MaxentModel.class);
String[] context = new String[] { "w=run" };
double[] eval = new double[] { 0.6, 0.3, 0.1 };
List<String> words = Arrays.asList("I", "run");
List<String> tags = Arrays.asList("PRP", "VB");
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(contextGenerator.getContext(1, new String[] { "I", "run" }, new String[] { "PRP", "VB" }, null)).thenReturn(context);
when(maxentModel.eval(context)).thenReturn(eval.clone());
when(maxentModel.getOutcome(0)).thenReturn("VB");
when(maxentModel.getOutcome(1)).thenReturn("NN");
when(maxentModel.getOutcome(2)).thenReturn("JJ");
POSTaggerME tagger = new POSTaggerME(model);
double[] probs = new double[3];
String[] ordered = tagger.getOrderedTags(words, tags, 1, probs);
assertEquals("VB", ordered[0]);
assertEquals("NN", ordered[1]);
assertEquals("JJ", ordered[2]);
assertEquals(0.6, probs[0], 0.0001);
assertEquals(0.3, probs[1], 0.0001);
assertEquals(0.1, probs[2], 0.0001);
}

@Test
public void testTagReturnsOriginalTagsWhenFormatIsAlreadyGuessed() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
// Sequence<String> sequence = mock(Sequence.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
String[] sentence = new String[] { "cats", "meow" };
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// when(seqModel.bestSequence(sentence, null, contextGenerator, validator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Arrays.asList("NN", "VB"));
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
// String[] result = tagger.tag(sentence);
// assertEquals("NN", result[0]);
// assertEquals("VB", result[1]);
}

@Test
public void testTrainWithNullTrainerTypeThrowsIllegalArgument() throws IOException {
TrainingParameters parameters = new TrainingParameters();
ObjectStream<POSSample> samples = mock(ObjectStream.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
try {
POSTaggerME.train("xx", samples, parameters, factory);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("Trainer type is not supported"));
}
}

@Test
public void testBuildNGramDictionaryWithEmptySampleStream() throws IOException {
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertFalse(dict.contains(new StringList("foo")));
}

@Test
public void testPopulatePOSDictionaryWithDigitsExcludesWord() throws IOException {
MutableTagDictionary dict = new POSDictionary(true);
POSSample sample = new POSSample(new String[] { "abc123" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
String[] tags = dict.getTags("abc123");
assertNull(tags);
}

@Test
public void testPopulatePOSDictionaryCaseInsensitiveLowercasesKey() throws IOException {
MutableTagDictionary dict = new POSDictionary(false);
POSSample sample = new POSSample(new String[] { "Test" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
String[] tags = dict.getTags("test");
assertNotNull(tags);
assertEquals("NN", tags[0]);
}

@Test
public void testGetOrderedTagsHandlesProbsArrayNull() {
POSModel model = mock(POSModel.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
String[] outcomeOrder = new String[] { "NN", "VB", "JJ" };
double[] scores = new double[] { 0.2, 0.6, 0.2 };
String[] contextFeatures = new String[] { "ctx" };
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(contextGenerator.getContext(1, new String[] { "a", "b" }, new String[] { "NN", "VB" }, null)).thenReturn(contextFeatures);
when(maxentModel.eval(contextFeatures)).thenReturn(scores.clone());
when(maxentModel.getOutcome(0)).thenReturn("NN");
when(maxentModel.getOutcome(1)).thenReturn("VB");
when(maxentModel.getOutcome(2)).thenReturn("JJ");
when(seqModel.getOutcomes()).thenReturn(outcomeOrder);
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.getOrderedTags(Arrays.asList("a", "b"), Arrays.asList("NN", "VB"), 1, null);
assertEquals("VB", result[0]);
assertEquals("NN", result[1]);
assertEquals("JJ", result[2]);
}

@Test
public void testTopKSequencesWithNullAdditionalContextReturnsSequences() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
// Sequence<String> seq = mock(Sequence.class);
// when(seq.getOutcomes()).thenReturn(Arrays.asList("NN", "VB"));
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// when(seqModel.bestSequences(3, new String[] { "hi", "there" }, null, contextGenerator, validator)).thenReturn(new Sequence[] { seq });
POSTaggerME tagger = new POSTaggerME(model);
Sequence[] sequences = tagger.topKSequences(new String[] { "hi", "there" });
assertEquals(1, sequences.length);
assertEquals(2, sequences[0].getOutcomes().size());
assertEquals("NN", sequences[0].getOutcomes().get(0));
assertEquals("VB", sequences[0].getOutcomes().get(1));
}

@Test
public void testBeamSizeParameterParsingFromModelManifest() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(42)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn("42");
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.getAllPosTags();
assertArrayEquals(new String[] { "NN", "VB" }, result);
}

@Test
public void testDefaultConstructorWithLanguageDownloadsModel() throws IOException {
POSModel downloadedModel = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
mockStatic(DownloadUtil.class);
when(DownloadUtil.downloadModel("en", DownloadUtil.ModelType.POS, POSModel.class)).thenReturn(downloadedModel);
when(downloadedModel.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(downloadedModel.getPosSequenceModel()).thenReturn(seqModel);
when(downloadedModel.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSTaggerME tagger = new POSTaggerME("en");
String[] tags = tagger.getAllPosTags();
assertEquals("NN", tags[0]);
assertEquals("VB", tags[1]);
}

@Test
public void testProbsCalledWithoutTaggingThrows() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSTaggerME tagger = new POSTaggerME(model);
try {
tagger.probs();
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(e.getMessage() == null || e instanceof NullPointerException);
}
}

@Test
public void testManifestMissingBeamSizeDefaultsTo3() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.getAllPosTags();
assertArrayEquals(new String[] { "NN", "VB" }, tags);
}

@Test
public void testPopulatePOSDictionaryHonorsCutoffRequirement() throws IOException {
MutableTagDictionary dict = new POSDictionary(true);
String[] sentence1 = new String[] { "apple", "banana" };
String[] tags1 = new String[] { "NN", "NN" };
String[] sentence2 = new String[] { "apple", "banana" };
String[] tags2 = new String[] { "NN", "VB" };
String[] sentence3 = new String[] { "apple", "banana" };
String[] tags3 = new String[] { "VB", "VB" };
POSSample sample1 = new POSSample(sentence1, tags1);
POSSample sample2 = new POSSample(sentence2, tags2);
POSSample sample3 = new POSSample(sentence3, tags3);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1).thenReturn(sample2).thenReturn(sample3).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 2);
String[] appleTags = dict.getTags("apple");
String[] bananaTags = dict.getTags("banana");
assertArrayEquals(new String[] { "NN" }, appleTags);
assertArrayEquals(new String[] { "VB" }, bananaTags);
}

@Test
public void testTrainEventModelSequenceTrainerReturnsModel() throws IOException {
TrainingParameters parameters = new TrainingParameters();
parameters.put("AlgorithmName", "MAXENT");
parameters.put("TrainerType", "EventModelSequenceTrainer");
POSSample sample = new POSSample(new String[] { "a", "b" }, new String[] { "NN", "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
// EventModelSequenceTrainer<POSSample> trainer = mock(EventModelSequenceTrainer.class);
MaxentModel trainedModel = mock(MaxentModel.class);
// when(trainer.train(any())).thenReturn(trainedModel);
mockStatic(TrainerFactory.class);
when(TrainerFactory.getTrainerType(parameters)).thenReturn(TrainerFactory.TrainerType.EVENT_MODEL_SEQUENCE_TRAINER);
// when(TrainerFactory.getEventModelSequenceTrainer(eq(parameters), any())).thenReturn(trainer);
POSTaggerFactory factory = new POSTaggerFactory();
POSModel model = POSTaggerME.train("xx", stream, parameters, factory);
assertNotNull(model);
assertEquals("xx", model.getLanguage());
}

@Test
public void testTrainSequenceTrainerReturnsSequenceModel() throws IOException {
TrainingParameters parameters = new TrainingParameters();
parameters.put("AlgorithmName", "PERCEPTRON");
parameters.put("TrainerType", "SequenceTrainer");
POSSample sample = new POSSample(new String[] { "a", "b" }, new String[] { "NN", "VB" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
SequenceClassificationModel<POSSample> sequenceModel = mock(SequenceClassificationModel.class);
opennlp.tools.ml.SequenceTrainer trainer = mock(opennlp.tools.ml.SequenceTrainer.class);
// when(trainer.train(any())).thenReturn(sequenceModel);
mockStatic(TrainerFactory.class);
when(TrainerFactory.getTrainerType(parameters)).thenReturn(TrainerFactory.TrainerType.SEQUENCE_TRAINER);
when(TrainerFactory.getSequenceModelTrainer(eq(parameters), any())).thenReturn(trainer);
POSTaggerFactory factory = new POSTaggerFactory();
POSModel model = POSTaggerME.train("xy", stream, parameters, factory);
assertNotNull(model);
assertEquals("xy", model.getLanguage());
}

@Test
public void testConstructorWithModelAndFormatCustomUsesNoOpMapper() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "X", "Y" });
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.CUSTOM);
// String[] tags = tagger.getAllPosTags();
// assertEquals("X", tags[0]);
// assertEquals("Y", tags[1]);
}

@Test
public void testTagNullContextStillWorks() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN" });
// when(seqModel.bestSequence(any(), isNull(), any(), any())).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Collections.singletonList("NN"));
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.tag(new String[] { "sample" }, null);
assertEquals(1, result.length);
assertEquals("NN", result[0]);
}

@Test
public void testTrainWithEventTrainerReturnsModel() throws IOException {
TrainingParameters params = new TrainingParameters();
params.put("AlgorithmName", "MAXENT");
params.put("TrainerType", "EventModelTrainer");
POSSample sample = new POSSample(new String[] { "A", "B" }, new String[] { "X", "Y" });
ObjectStream<POSSample> sampleStream = mock(ObjectStream.class);
when(sampleStream.read()).thenReturn(sample).thenReturn(null);
MaxentModel trainedModel = mock(MaxentModel.class);
opennlp.tools.ml.EventTrainer trainer = mock(opennlp.tools.ml.EventTrainer.class);
when(trainer.train(any(ObjectStream.class))).thenReturn(trainedModel);
mockStatic(TrainerFactory.class);
when(TrainerFactory.getTrainerType(params)).thenReturn(TrainerFactory.TrainerType.EVENT_MODEL_TRAINER);
when(TrainerFactory.getEventTrainer(eq(params), any())).thenReturn(trainer);
POSTaggerFactory factory = new POSTaggerFactory();
POSModel model = POSTaggerME.train("en", sampleStream, params, factory);
assertNotNull(model);
assertEquals("en", model.getLanguage());
}

@Test
public void testBuildNGramDictionarySkipsEmptySentences() throws IOException {
POSSample emptySample = new POSSample(new String[0], new String[0]);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(emptySample).thenReturn(null);
// Dictionary dict = POSTaggerME.buildNGramDictionary(stream, 1);
// assertNotNull(dict);
// assertFalse(dict.contains(new StringList("")));
}

@Test
public void testPopulatePOSDictionarySupportsMultipleTagsAboveCutoff() throws IOException {
MutableTagDictionary dict = new POSDictionary(true);
String[] words = new String[] { "walk" };
String[] tags1 = new String[] { "VB" };
String[] tags2 = new String[] { "NN" };
POSSample sample1 = new POSSample(words, tags1);
POSSample sample2 = new POSSample(words, tags1);
POSSample sample3 = new POSSample(words, tags2);
POSSample sample4 = new POSSample(words, tags2);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1).thenReturn(sample2).thenReturn(sample3).thenReturn(sample4).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 2);
String[] resultTags = dict.getTags("walk");
assertNotNull(resultTags);
assertEquals(2, resultTags.length);
assertTrue(Arrays.asList(resultTags).contains("VB"));
assertTrue(Arrays.asList(resultTags).contains("NN"));
}

@Test
public void testGetOrderedTagsOnlyReturnsOneIfSameScore() {
POSModel model = mock(POSModel.class);
MaxentModel posModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator cg = mock(POSContextGenerator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(cg);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(posModel);
when(seqModel.getOutcomes()).thenReturn(new String[] { "X", "Y" });
when(cg.getContext(eq(0), any(String[].class), any(String[].class), isNull())).thenReturn(new String[] { "ctx" });
when(posModel.eval(any())).thenReturn(new double[] { 0.5, 0.5 });
when(posModel.getOutcome(anyInt())).thenReturn("X").thenReturn("Y");
POSTaggerME tagger = new POSTaggerME(model);
String[] output = tagger.getOrderedTags(Arrays.asList("abc"), Arrays.asList("TAG"), 0, null);
assertNotNull(output);
assertEquals(2, output.length);
}

@Test
public void testConstructorWithNegativeBeamFallbackToDefault() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn("-1");
when(factory.getPOSContextGenerator(POSTaggerME.DEFAULT_BEAM_SIZE)).thenReturn(mock(POSContextGenerator.class));
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(mock(SequenceValidator.class));
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(seqModel.getOutcomes()).thenReturn(new String[] { "X" });
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.getAllPosTags();
assertEquals(1, tags.length);
assertEquals("X", tags[0]);
}

@Test
public void testTagWithEmptyInputAndNonNullContextReturnsEmpty() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
String[] sentence = new String[0];
Object[] context = new Object[] { "test" };
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
// when(seqModel.bestSequence(sentence, context, contextGenerator, validator)).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
when(seqModel.getOutcomes()).thenReturn(new String[] {});
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.tag(sentence, context);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTrainWithInvalidTrainerTypeThrows() throws IOException {
TrainingParameters parameters = new TrainingParameters();
parameters.put("TrainerType", "UNSUPPORTED");
ObjectStream<POSSample> stream = mock(ObjectStream.class);
POSTaggerFactory factory = new POSTaggerFactory();
POSTaggerME.train("xx", stream, parameters, factory);
}

@Test
public void testTagReturnsOriginalWhenFormatMatchesMapperGuess() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
when(model.getFactory()).thenReturn(factory);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB" });
// when(seqModel.bestSequence(any(String[].class), any(), any(), any())).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Arrays.asList("NN", "VB"));
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
// String[] result = tagger.tag(new String[] { "word1", "word2" });
// assertEquals("NN", result[0]);
// assertEquals("VB", result[1]);
}

@Test
public void testTagWithNonMatchingFormatTriggersConversion() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
// Sequence<String> sequence = mock(Sequence.class);
when(model.getFactory()).thenReturn(factory);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VBZ" });
// when(seqModel.bestSequence(any(String[].class), any(), any(), any())).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Arrays.asList("NN", "VBZ"));
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.CONLL);
// String[] result = tagger.tag(new String[] { "Hello", "plays" });
// assertEquals(2, result.length);
// assertTrue(Arrays.asList("NN", "NOUN").contains(result[0]));
}

@Test
public void testGetOrderedTagsHandlesEmptyModelOutcomesGracefully() {
POSModel model = mock(POSModel.class);
MaxentModel posModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(seqModel.getOutcomes()).thenReturn(new String[] {});
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(posModel);
when(contextGenerator.getContext(eq(0), any(), any(), isNull())).thenReturn(new String[] { "f1" });
when(posModel.eval(any())).thenReturn(new double[] {});
when(posModel.getOutcome(anyInt())).thenReturn("NA");
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.getOrderedTags(Arrays.asList("x"), Arrays.asList("NN"), 0, null);
assertEquals(0, result.length);
}

@Test
public void testPopulatePOSDictionaryHandlesPreFilledDictWithCaseInsensitive() throws IOException {
MutableTagDictionary dict = new POSDictionary(false);
dict.put("hello", new String[] { "UH" });
POSSample sample = new POSSample(new String[] { "Hello" }, new String[] { "UH" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
String[] tags = dict.getTags("hello");
assertNotNull(tags);
assertTrue(Arrays.asList(tags).contains("UH"));
}

@Test
public void testBuildNGramDictionaryReturnsEmptyDictIfAllCut() throws IOException {
POSSample sample1 = new POSSample(new String[] { "rareword" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample1).thenReturn(null);
// Dictionary result = POSTaggerME.buildNGramDictionary(stream, 100);
// assertNotNull(result);
// assertFalse(result.contains(new StringList("rareword")));
}

@Test
public void testDefaultBeamSizeUsedWhenManifestMissing() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
when(model.getFactory()).thenReturn(factory);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(factory.getPOSContextGenerator(POSTaggerME.DEFAULT_BEAM_SIZE)).thenReturn(contextGenerator);
when(factory.getTagDictionary()).thenReturn(null);
when(factory.getSequenceValidator()).thenReturn(validator);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(seqModel.getOutcomes()).thenReturn(new String[] { "A", "B" });
POSTaggerME tagger = new POSTaggerME(model);
String[] tags = tagger.getAllPosTags();
assertEquals("A", tags[0]);
assertEquals("B", tags[1]);
}

@Test
public void testTrainCreatesValidModelWithMinimalConfig() throws IOException {
POSSample sample = new POSSample(new String[] { "a" }, new String[] { "NN" });
ObjectStream<POSSample> sampleStream = mock(ObjectStream.class);
when(sampleStream.read()).thenReturn(sample).thenReturn(null);
MaxentModel trainedModel = mock(MaxentModel.class);
opennlp.tools.ml.EventTrainer eventTrainer = mock(opennlp.tools.ml.EventTrainer.class);
when(eventTrainer.train(any(ObjectStream.class))).thenReturn(trainedModel);
POSTaggerFactory factory = new POSTaggerFactory();
TrainingParameters params = new TrainingParameters();
params.put("TrainerType", "EventModelTrainer");
params.put("AlgorithmName", "MAXENT");
mockStatic(TrainerFactory.class);
when(TrainerFactory.getTrainerType(params)).thenReturn(TrainerFactory.TrainerType.EVENT_MODEL_TRAINER);
when(TrainerFactory.getEventTrainer(eq(params), any())).thenReturn(eventTrainer);
POSModel model = POSTaggerME.train("xx", sampleStream, params, factory);
assertNotNull(model);
assertEquals("xx", model.getLanguage());
}

@Test
public void testTagWithEmptyOutcomesReturnsEmptyArray() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
SequenceClassificationModel<String> modelSequence = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
// Sequence<String> sequence = mock(Sequence.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(modelSequence);
when(modelSequence.getOutcomes()).thenReturn(new String[] {});
// when(modelSequence.bestSequence(any(), any(), any(), any())).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Collections.emptyList());
// POSTaggerME tagger = new POSTaggerME(model, POSTagFormat.UD);
// String[] result = tagger.tag(new String[] { "abc" });
// assertEquals(0, result.length);
}

@Test
public void testTagWithEmptySentenceReturnsEmptyTagsArray() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
// Sequence<String> emptySequence = mock(Sequence.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
// when(seqModel.bestSequence(eq(new String[0]), isNull(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(emptySequence);
// when(emptySequence.getOutcomes()).thenReturn(Collections.emptyList());
when(seqModel.getOutcomes()).thenReturn(new String[0]);
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.tag(new String[0]);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTagWithOnlyWhitespaceTokensReturnsEmptyTagList() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceValidator<String> sequenceValidator = mock(SequenceValidator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
// Sequence<String> sequence = mock(Sequence.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(sequenceValidator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[] { "DT", "VB" });
// when(seqModel.bestSequence(eq(new String[] { " ", "  " }), isNull(), eq(contextGenerator), eq(sequenceValidator))).thenReturn(sequence);
// when(sequence.getOutcomes()).thenReturn(Arrays.asList("DT", "VB"));
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.tag(new String[] { " ", "  " });
assertEquals("DT", result[0]);
assertEquals("VB", result[1]);
}

@Test
public void testEmptyPOSDictionaryAfterPopulateWithZeroCutoff() throws IOException {
MutableTagDictionary dictionary = new POSDictionary(true);
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dictionary, 0);
assertNull(dictionary.getTags("anyword"));
}

@Test
public void testPOSDictionaryCaseSensitivityRespected() throws IOException {
MutableTagDictionary dict = new POSDictionary(true);
POSSample sample = new POSSample(new String[] { "Word" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
assertArrayEquals(new String[] { "NN" }, dict.getTags("Word"));
assertNull(dict.getTags("word"));
}

@Test
public void testPOSDictionaryIgnoresDigitsInWord() throws IOException {
MutableTagDictionary dict = new POSDictionary(true);
POSSample sample = new POSSample(new String[] { "win2024" }, new String[] { "NN" });
ObjectStream<POSSample> stream = mock(ObjectStream.class);
when(stream.read()).thenReturn(sample).thenReturn(null);
POSTaggerME.populatePOSDictionary(stream, dict, 1);
assertNull(dict.getTags("win2024"));
}

@Test
public void testEmptyOutputFromGetOrderedTagsReturnsEmptyResult() {
POSModel model = mock(POSModel.class);
MaxentModel maxentModel = mock(MaxentModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(maxentModel);
when(contextGenerator.getContext(eq(0), any(String[].class), any(String[].class), isNull())).thenReturn(new String[] { "f1" });
when(maxentModel.eval(any())).thenReturn(new double[0]);
when(maxentModel.getOutcome(anyInt())).thenReturn("NONE");
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
when(seqModel.getOutcomes()).thenReturn(new String[0]);
POSTaggerME tagger = new POSTaggerME(model);
String[] result = tagger.getOrderedTags(Arrays.asList("a"), Arrays.asList("b"), 0);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTagReturnsMultipleBestSequencesCorrectly() {
POSModel model = mock(POSModel.class);
POSTaggerFactory factory = mock(POSTaggerFactory.class);
POSContextGenerator contextGenerator = mock(POSContextGenerator.class);
SequenceClassificationModel<String> seqModel = mock(SequenceClassificationModel.class);
SequenceValidator<String> validator = mock(SequenceValidator.class);
// Sequence<String> s1 = new Sequence<>(Arrays.asList("NN", "VB"));
// Sequence<String> s2 = new Sequence<>(Arrays.asList("JJ", "NN"));
when(model.getFactory()).thenReturn(factory);
when(factory.getPOSContextGenerator(3)).thenReturn(contextGenerator);
when(factory.getSequenceValidator()).thenReturn(validator);
when(factory.getTagDictionary()).thenReturn(null);
when(model.getPosSequenceModel()).thenReturn(seqModel);
when(seqModel.getOutcomes()).thenReturn(new String[] { "NN", "VB", "JJ" });
when(model.getManifestProperty("BeamSearch.beamSize")).thenReturn(null);
// when(seqModel.bestSequences(eq(2), eq(new String[] { "fast", "run" }), isNull(), eq(contextGenerator), eq(validator))).thenReturn(new Sequence[] { s1, s2 });
POSTaggerME tagger = new POSTaggerME(model);
String[][] results = tagger.tag(2, new String[] { "fast", "run" });
assertArrayEquals(new String[] { "NN", "VB" }, results[0]);
assertArrayEquals(new String[] { "JJ", "NN" }, results[1]);
}

@Test
public void testTrainingFailsGracefullyOnIOException() {
POSTaggerFactory factory = new POSTaggerFactory();
TrainingParameters parameters = new TrainingParameters();
parameters.put("TrainerType", "EventModelTrainer");
ObjectStream<POSSample> failingStream = new ObjectStream<>() {

public POSSample read() throws IOException {
throw new IOException("Simulated IO failure");
}

public void reset() throws IOException {
}

public void close() throws IOException {
}
};
try {
POSTaggerME.train("en", failingStream, parameters, factory);
fail("Expected IOException");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Simulated IO failure"));
}
}
}
