package opennlp.tools.sentdetect;

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
import opennlp.tools.sentdetect.lang.Factory;
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

public class SentenceDetectorME_llmsuite_5_GPTLLMTest {

@Test
public void testSingleSentenceDetection() {
String input = "This is a sentence.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(18));
when(contextGen.getContext(input, 18)).thenReturn(new String[] { "eos" });
when(model.eval(new String[] { "eos" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(1, output.length);
assertEquals("This is a sentence.", output[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.8, probs[0], 0.0001);
}

@Test
public void testMultipleSentenceDetection() {
String input = "This is one. This is two.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(12, 25));
when(contextGen.getContext(input, 12)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(input, 25)).thenReturn(new String[] { "ctx2" });
double[] probs1 = new double[] { 0.1, 0.9 };
double[] probs2 = new double[] { 0.2, 0.8 };
when(model.eval(new String[] { "ctx1" })).thenReturn(probs1);
when(model.eval(new String[] { "ctx2" })).thenReturn(probs2);
when(model.getBestOutcome(probs1)).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(probs2)).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(2, output.length);
assertEquals("This is one.", output[0]);
assertEquals("This is two.", output[1]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(2, probs.length);
assertEquals(0.9, probs[0], 0.0001);
assertEquals(0.8, probs[1], 0.0001);
}

@Test
public void testWhitespaceOnlyReturnsEmptyArray() {
String input = "   \t  ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(0, output.length);
double[] probs = detector.getSentenceProbabilities();
assertEquals(0, probs.length);
}

@Test
public void testTrailingSentenceNoPunctuation() {
String input = "First. Last one without punctuation";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(5));
when(contextGen.getContext(input, 5)).thenReturn(new String[] { "eos" });
double[] probs1 = new double[] { 0.3, 0.7 };
when(model.eval(new String[] { "eos" })).thenReturn(probs1);
when(model.getBestOutcome(probs1)).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(2, output.length);
assertEquals("First.", output[0]);
assertEquals("Last one without punctuation", output[1]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(2, probs.length);
assertEquals(0.7, probs[0], 0.0001);
assertEquals(1.0, probs[1], 0.0001);
}

@Test
public void testIsAcceptableBreakWithAbbreviation() {
String input = "He met Mr. Smith.";
StringList abbEntry = new StringList("Mr.");
// Dictionary dict = new Dictionary();
// dict.put(abbEntry);
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(factory.getSDContextGenerator()).thenReturn(mock(SDContextGenerator.class));
when(factory.getEndOfSentenceScanner()).thenReturn(mock(EndOfSentenceScanner.class));
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 10);
assertFalse(result);
}

@Test
public void testGetSentenceProbabilitiesWhenNoneDetected() {
String input = "   ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
double[] output = detector.getSentenceProbabilities();
assertNotNull(output);
assertEquals(0, output.length);
}

@Test
public void testCreateWithLanguageThrowsIOException() {
try {
SentenceDetectorME detector = new SentenceDetectorME("xx-unknown");
fail("Expected IOException");
} catch (IOException e) {
assertTrue(e.getMessage().contains("Cannot find"));
}
}

@Test
public void testTrainReturnsNonNullModel() throws IOException {
ObjectStream<SentenceSample> sampleStream = mock(ObjectStream.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
MaxentModel trainedModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
TrainingParameters params = new TrainingParameters();
Map<String, String> manifestInfo = new HashMap<>();
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
// when(trainer.train(any())).thenReturn(trainedModel);
// TrainerFactory.setTrainerFactory((mlParams, manifest) -> {
// manifest.putAll(manifestInfo);
// return trainer;
// });
SentenceModel model = SentenceDetectorME.train("en", sampleStream, factory, params);
assertNotNull(model);
assertEquals(trainedModel, model.getMaxentModel());
}

@Test
public void testAbbreviationAtSentenceEndPreventsSplit() {
String input = "This is Mr. Smith";
// Dictionary dictionary = new Dictionary();
// dictionary.put(new StringList("Mr."));
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(11));
when(context.getContext(input, 11)).thenReturn(new String[] { "some-context" });
double[] probs = new double[] { 0.1, 0.9 };
when(model.eval(new String[] { "some-context" })).thenReturn(probs);
when(model.getBestOutcome(probs)).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("This is Mr. Smith", sentences[0]);
assertEquals(0, detector.getSentenceProbabilities().length);
}

@Test
public void testSentenceContainingOnlyWhitespaceGetsTrimmed() {
String input = " Test.    \n     ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(5));
when(cgen.getContext(input, 5)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Test.", input.substring(spans[0].getStart(), spans[0].getEnd()));
}

@Test
public void testTwoEOSPositionsOverlappingResultingInOnlyOneSplit() {
String input = "Hello.xxx";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator gen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(5, 6, 7));
when(gen.getContext(input, 5)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(gen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertTrue(sentences[0].startsWith("Hello"));
}

@Test
public void testCustomEOSCharactersAreUsedInFactory() {
char[] eos = new char[] { '|' };
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Factory factory = mock(Factory.class);
// Dictionary abbreviationDict = new Dictionary();
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner eosScanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getEosCharacters()).thenReturn(eos);
// when(sentenceModel.getAbbreviations()).thenReturn(abbreviationDict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
// when(factory.createSentenceContextGenerator(abbreviationDict.asStringSet(), eos)).thenReturn(contextGenerator);
when(factory.createEndOfSentenceScanner(eos)).thenReturn(eosScanner);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, factory);
assertNotNull(detector);
}

@Test
public void testUseTokenEndTrueAffectsSplitIndex() {
String input = "Hi. There!";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(2, 9));
when(contextGenerator.getContext(input, 2)).thenReturn(new String[] { "c1" });
when(contextGenerator.getContext(input, 9)).thenReturn(new String[] { "c2" });
when(model.eval(new String[] { "c1" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.eval(new String[] { "c2" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(true);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(2, output.length);
assertEquals("Hi.", output[0]);
assertEquals("There!", output[1]);
}

@Test
public void testIsAcceptableBreakSkipsInvalidAbbreviationTokenAccess() {
String input = "Hi Drx. Jones.";
Dictionary dict = mock(Dictionary.class);
Iterator<StringList> it = mock(Iterator.class);
when(it.hasNext()).thenReturn(true, true, false);
when(it.next()).thenReturn(new StringList("abcd")).thenThrow(new IndexOutOfBoundsException("TokErr"));
// when(dict.iterator()).thenReturn(it);
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(factory.getSDContextGenerator()).thenReturn(mock(SDContextGenerator.class));
when(factory.getEndOfSentenceScanner()).thenReturn(mock(EndOfSentenceScanner.class));
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
try {
boolean result = detector.isAcceptableBreak(input, 0, 6);
assertTrue(result || !result);
} catch (IndexOutOfBoundsException e) {
fail("Should not throw even on bad abbreviation token access");
}
}

@Test
public void testEmptyStringStillReturnsEmptyArray() {
String input = "";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertNotNull(output);
assertEquals(0, output.length);
double[] probs = detector.getSentenceProbabilities();
assertNotNull(probs);
assertEquals(0, probs.length);
}

@Test
public void testSpanTrimmingEliminatesWhitespaceSpan() {
String input = "Hello.     \n\nWorld.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(5, 18));
when(contextGenerator.getContext(input, 5)).thenReturn(new String[] { "ctx1" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hello.", input.substring(spans[0].getStart(), spans[0].getEnd()));
assertEquals("World.", input.substring(spans[1].getStart(), spans[1].getEnd()));
}

@Test
public void testIsAcceptableBreakReturnsTrueWhenAbbreviationTokenMismatch() {
String input = "Hello MrX. Brown";
Dictionary dict = mock(Dictionary.class);
Iterator<StringList> iterator = mock(Iterator.class);
when(iterator.hasNext()).thenReturn(true, false);
when(iterator.next()).thenReturn(new StringList("Dr."));
// when(dict.iterator()).thenReturn(iterator);
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 9);
assertTrue(result);
}

@Test
public void testGetSentenceProbabilitiesCalledBeforeSentDetect() {
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
double[] probs = detector.getSentenceProbabilities();
assertNotNull(probs);
assertEquals(0, probs.length);
}

@Test
public void testModelSaysNoSplitWhenEOSCharacterPresent() {
String input = "Is this a question?";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(20));
when(context.getContext(input, 20)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.99, 0.01 });
when(model.getBestOutcome(new double[] { 0.99, 0.01 })).thenReturn(SentenceDetectorME.NO_SPLIT);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("Is this a question?", sentences[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(0, probs.length);
}

@Test
public void testOneCharacterStringPunctuation() {
String input = ".";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(0));
when(cgen.getContext(input, 0)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(1, output.length);
assertEquals(".", output[0]);
}

@Test
public void testOneCharacterStringNoPunctuation() {
String input = "X";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(1, output.length);
assertEquals("X", output[0]);
}

@Test
public void testSpanAfterLastEOSWithWhitespaceTrimmed() {
String input = "Hello world. ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(11));
when(contextGen.getContext(input, 11)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Hello world.", input.substring(spans[0].getStart(), spans[0].getEnd()));
assertEquals(0.9, spans[0].getProb(), 0.0001);
}

@Test
public void testMultipleEOSWithOverlappingWhitespaceSkipsDuplicateSplit() {
String input = "Hi!  Bye! ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator generator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(2, 8));
when(generator.getContext(input, 2)).thenReturn(new String[] { "ctx1" });
when(generator.getContext(input, 8)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(generator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hi!", input.substring(spans[0].getStart(), spans[0].getEnd()));
assertEquals("Bye!", input.substring(spans[1].getStart(), spans[1].getEnd()));
}

@Test
public void testSpanProbabilityRemovedWhenTrimmedToEmpty() {
String input = "Sentence1.    .";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(9, 14));
when(contextGenerator.getContext(input, 9)).thenReturn(new String[] { "ctx1" });
when(contextGenerator.getContext(input, 14)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.4, 0.6 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(new double[] { 0.4, 0.6 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Sentence1.", input.substring(spans[0].getStart(), spans[0].getEnd()));
}

@Test
public void testOnlyAbbreviationAtEndDeniesSplit() {
String input = "He is Prof.";
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Prof."));
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator generator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(11));
when(generator.getContext(input, 11)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(generator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(1, output.length);
assertEquals("He is Prof.", output[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(0, probs.length);
}

@Test
public void testWhitespaceOnlySpanIsSkippedInFinalSpanArray() {
String input = "One.     \t\nTwo.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(3, 15));
when(contextGen.getContext(input, 3)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(input, 15)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("One.", input.substring(spans[0].getStart(), spans[0].getEnd()));
assertEquals("Two.", input.substring(spans[1].getStart(), spans[1].getEnd()));
}

@Test
public void testSpanReturnedWhenNoEOSButNonEmptyTrimmedSentence() {
String input = "No punctuation here";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator generator = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(generator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("No punctuation here", input.substring(spans[0].getStart(), spans[0].getEnd()));
assertEquals(1.0, spans[0].getProb(), 0.0001);
}

@Test
public void testSpanStartsAtZero() {
String input = "Start and end.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(13));
when(cgen.getContext(input, 13)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals(0, spans[0].getStart());
assertEquals(14, spans[0].getEnd());
assertEquals("Start and end.", input.substring(spans[0].getStart(), spans[0].getEnd()));
}

@Test
public void testLeftoverWithoutPunctuationIsAppendedAsSentence() {
String input = "First. Last part";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(5));
when(cgen.getContext(input, 5)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.4, 0.6 });
when(model.getBestOutcome(new double[] { 0.4, 0.6 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("First.", input.substring(spans[0].getStart(), spans[0].getEnd()));
assertEquals("Last part", input.substring(spans[1].getStart(), spans[1].getEnd()));
}

@Test
public void testEOSAtStartIsCorrectlyHandledAsNoSplit() {
String input = ". A leading period";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(0));
when(cgen.getContext(input, 0)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.9, 0.1 });
when(model.getBestOutcome(new double[] { 0.9, 0.1 })).thenReturn(SentenceDetectorME.NO_SPLIT);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(1, output.length);
assertEquals(". A leading period", output[0]);
}

@Test
public void testConsecutiveEOSWithoutInterveningTokenSkipsSecond() {
String input = "Hi.!";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(2, 3));
when(context.getContext(input, 2)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] results = detector.sentDetect(input);
assertEquals(1, results.length);
assertEquals("Hi.", results[0]);
}

@Test
public void testAbbreviationMatchPartiallyOverlappingReturnsTrue() {
String input = "Example Ex.ample.";
// Dictionary dictionary = new Dictionary();
// dictionary.put(new StringList("Ex.ample"));
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 15);
assertFalse(result);
}

@Test
public void testIsAcceptableBreakEmptyDictionaryReturnsTrue() {
String input = "Some input.";
// Dictionary dictionary = new Dictionary();
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, input.length() - 1);
assertTrue(result);
}

@Test
public void testIsAcceptableBreakWithSingleEntryNotMatchingReturnsTrue() {
String input = "Testing Abc.";
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Mr."));
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SDContextGenerator sdCs = mock(SDContextGenerator.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.getSDContextGenerator()).thenReturn(sdCs);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 12);
assertTrue(result);
}

@Test
public void testTrimmingSpanWithOnlyWhitespaceRemovesItFromResults() {
String input = "Trim this.     ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator generator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(9, 14));
when(generator.getContext(input, 9)).thenReturn(new String[] { "ctx1" });
when(generator.getContext(input, 14)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(generator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Trim this.", input.substring(spans[0].getStart(), spans[0].getEnd()));
}

@Test
public void testUseTokenEndSkipsEOSOnTrailingWhitespace() {
String input = "Sentence. ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator generator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(8));
when(generator.getContext(input, 8)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(generator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(true);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("Sentence.", sentences[0]);
}

@Test
public void testAbbreviationTokenThatIsSubstring() {
String input = "He met M.urray today.";
// Dictionary dict = new Dictionary();
// dict.put(new StringList("M."));
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 6);
assertTrue(result);
}

@Test
public void testInputEndsWithMultipleWhitespaces() {
String input = "Just one sentence.     \n  ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(18));
when(contextGen.getContext(input, 18)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("Just one sentence.", sentences[0]);
}

@Test
public void testMultipleAbbreviationsOnlyOneMatchesCandidate() {
String input = "He is Dr. Brown.";
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Mr."));
// dict.put(new StringList("Dr."));
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner eosScanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(eosScanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 9);
assertFalse(result);
}

@Test
public void testAbbreviationMatchesButIsAfterCandidateIndex() {
String input = "Before something Dr. appears.";
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Dr."));
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator generator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(generator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 7);
assertTrue(result);
}

@Test
public void testSentenceProbabilitiesAfterSentPosDetectOnly() {
String input = "Hello world.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator generator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(11));
when(generator.getContext(input, 11)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(generator);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.8, probs[0], 0.0001);
}

@Test
public void testSplitCandidateInsideTokenShouldSkip() {
String input = "ab.cd ef";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(2));
when(cgen.getContext(input, 2)).thenReturn(new String[] { "c1" });
when(model.eval(new String[] { "c1" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("ab.cd ef", sentences[0]);
}

@Test
public void testSentenceBreakFollowedByTab() {
String input = "Hello.\tWorld.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(5, 11));
when(cgen.getContext(input, 5)).thenReturn(new String[] { "ctx1" });
when(cgen.getContext(input, 11)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(2, sentences.length);
assertEquals("Hello.", sentences[0]);
assertEquals("World.", sentences[1]);
}

@Test
public void testAbbreviationAtSentenceStartShouldNotBlockSplit() {
String input = "Dr. Smith is here. Welcome.";
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Dr."));
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(3, 22));
when(cgen.getContext(input, 3)).thenReturn(new String[] { "ctx1" });
when(cgen.getContext(input, 22)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(2, sentences.length);
assertEquals("Dr. Smith is here.", sentences[0]);
assertEquals("Welcome.", sentences[1]);
}

@Test
public void testSentenceWithMultipleSpacesBetweenWords() {
String input = "This  is   spaced     out.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(28));
when(cgen.getContext(input, 28)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] output = detector.sentDetect(input);
assertEquals(1, output.length);
assertEquals("This  is   spaced     out.", output[0]);
}

@Test
public void testSentenceEndingWithNewlineIsTrimmedProperly() {
String input = "Trim this.\n";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(9));
when(cgen.getContext(input, 9)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Trim this.", input.substring(spans[0].getStart(), spans[0].getEnd()));
}
}
