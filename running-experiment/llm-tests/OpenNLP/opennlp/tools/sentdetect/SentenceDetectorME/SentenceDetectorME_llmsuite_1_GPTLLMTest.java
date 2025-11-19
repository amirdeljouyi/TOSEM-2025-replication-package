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

public class SentenceDetectorME_llmsuite_1_GPTLLMTest {

@Test
public void testSingleSentenceDetection() {
CharSequence input = "This is a sentence.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(maxentModel);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(18));
when(contextGenerator.getContext(eq(input), eq(18))).thenReturn(new String[] { "test" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("This is a sentence.", sentences[0]);
double[] probabilities = detector.getSentenceProbabilities();
assertEquals(1, probabilities.length);
assertTrue(probabilities[0] >= 0.0 && probabilities[0] <= 1.0);
}

@Test
public void testMultipleSentencesDetection() {
CharSequence input = "This is one. This is two.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(maxentModel);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(13, 27));
when(contextGenerator.getContext(eq(input), eq(13))).thenReturn(new String[] { "ctx1" });
when(contextGenerator.getContext(eq(input), eq(27))).thenReturn(new String[] { "ctx2" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(2, sentences.length);
assertEquals("This is one.", sentences[0]);
assertEquals("This is two.", sentences[1]);
double[] probabilities = detector.getSentenceProbabilities();
assertEquals(2, probabilities.length);
assertTrue(probabilities[0] >= 0.0);
assertTrue(probabilities[1] >= 0.0);
}

@Test
public void testEmptyInputReturnsNoSentences() {
CharSequence input = "";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(maxentModel);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertNotNull(sentences);
assertEquals(0, sentences.length);
double[] probs = detector.getSentenceProbabilities();
assertNotNull(probs);
assertEquals(0, probs.length);
}

@Test
public void testOnlyWhitespaceInputReturnsNoSentences() {
CharSequence input = "     \t \n   ";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getMaxentModel()).thenReturn(maxentModel);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertNotNull(sentences);
assertEquals(0, sentences.length);
double[] probs = detector.getSentenceProbabilities();
assertNotNull(probs);
assertEquals(0, probs.length);
}

@Test
public void testIsAcceptableBreakTrueWithoutDictionary() {
MaxentModel maxentModel = mock(MaxentModel.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(mock(SentenceDetectorFactory.class));
when(model.getAbbreviations()).thenReturn(null);
when(model.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(model);
boolean result = detector.isAcceptableBreak("Example sentence.", 0, 16);
assertTrue(result);
}

@Test
public void testIsAcceptableBreakFalseDueToAbbreviation() {
StringList abbr = new StringList("Dr.");
Set<StringList> entries = new HashSet<>();
entries.add(abbr);
Dictionary dict = mock(Dictionary.class);
// when(dict.iterator()).thenReturn(entries.iterator());
MaxentModel maxentModel = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(maxentModel);
when(sentenceModel.getFactory()).thenReturn(mock(SentenceDetectorFactory.class));
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak("He was called Dr. Smith.", 0, 17);
assertFalse(result);
}

@Test
public void testTrainReturnsValidModel() throws IOException {
ObjectStream<SentenceSample> sampleStream = mock(ObjectStream.class);
SentenceDetectorFactory sdFactory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel trainedModel = mock(MaxentModel.class);
when(sdFactory.getSDContextGenerator()).thenReturn(contextGenerator);
when(sdFactory.getEndOfSentenceScanner()).thenReturn(scanner);
when(TrainerFactory.getEventTrainer(any(), anyMap())).thenReturn(trainer);
// when(trainer.train(any())).thenReturn(trainedModel);
TrainingParameters params = new TrainingParameters();
SentenceModel trained = SentenceDetectorME.train("en", sampleStream, sdFactory, params);
assertNotNull(trained);
assertEquals(trainedModel, trained.getMaxentModel());
}

@Test
public void testSentPosDetect_AllSpansWhitespaceOnly_Filtered() {
CharSequence input = "Sentence one.   Sentence two.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dictionary);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(13, 28));
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Sentence one.", spans[0].getCoveredText(input).toString());
assertEquals("Sentence two.", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_LeftOverWhitespaceAppendedAsSpan() {
CharSequence input = "End of sentence.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
List<Integer> positions = new ArrayList<>();
positions.add(16);
when(scanner.getPositions(input)).thenReturn(positions);
when(contextGen.getContext(eq(input), eq(16))).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("End of sentence.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_EOSFollowedByAnotherEOSIsSkipped() {
CharSequence input = "Wait... What?";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
List<Integer> eosPositions = Arrays.asList(4, 5, 6, 12);
when(scanner.getPositions(input)).thenReturn(eosPositions);
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Wait...", spans[0].getCoveredText(input).toString());
assertEquals("What?", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_SegmentIsWhitespaceOnly_ProbRemoved() {
CharSequence input = "Hi.   .Should ignore.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(2, 7, 22));
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hi.", spans[0].getCoveredText(input).toString());
assertEquals("Should ignore.", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_OnlyAbbreviationAndWhitespace_ReturnsNoSplit() {
CharSequence input = "e.g.   ";
StringList abbreviation = new StringList("e.g.");
Set<StringList> abbrevSet = new HashSet<>();
abbrevSet.add(abbreviation);
Dictionary dictionary = mock(Dictionary.class);
// when(dictionary.iterator()).thenReturn(abbrevSet.iterator());
MaxentModel maxentModel = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(4));
when(contextGen.getContext(eq(input), eq(4))).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("e.g.", sentences[0]);
}

@Test
public void testConstructorWithModelAndNullDictionary() {
MaxentModel maxentModel = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
when(model.getAbbreviations()).thenReturn(null);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(model);
assertNotNull(detector);
}

@Test
public void testSentPosDetect_WithTrailingWhitespace_OneSentence() {
CharSequence input = "Hello world.   ";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dictionary);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(11));
when(contextGenerator.getContext(eq(input), eq(11))).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Hello world.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_AbbreviationOverlappingWithCandidateSentenceEnd_IsRejected() {
CharSequence input = "See Dr. Robertson tomorrow.";
StringList abbreviation = new StringList("Dr.");
Set<StringList> abbreviations = new HashSet<>();
abbreviations.add(abbreviation);
Dictionary dict = mock(Dictionary.class);
// when(dict.iterator()).thenReturn(abbreviations.iterator());
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(8));
when(contextGenerator.getContext(eq(input), eq(8))).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("See Dr. Robertson tomorrow.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_DoubleSpaceAfterEOS_TokenEndFalse() {
CharSequence input = "Hello.  Goodbye.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(5));
when(contextGenerator.getContext(eq(input), eq(5))).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hello.", spans[0].getCoveredText(input).toString());
assertEquals("Goodbye.", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_EOSAtStartOfInput_IsIgnored() {
CharSequence input = ".Starts with period. Ends.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(0, 21, 27));
when(contextGenerator.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals(".Starts with period.", spans[0].getCoveredText(input).toString());
assertEquals("Ends.", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentDetect_AllWhiteSpaceTrimsToZero_ReturnsEmptyArray() {
CharSequence input = "   \n\t  \r ";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(model);
String[] sentences = detector.sentDetect(input);
assertNotNull(sentences);
assertEquals(0, sentences.length);
}

@Test
public void testGetSentenceProbabilities_AfterNoSplitDetection_Returns1d() {
CharSequence input = "Text with no punctuation";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(model);
detector.sentDetect(input);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(1.0, probs[0], 0.0001);
}

@Test
public void testSplitFollowedByWhitespace_ReturnsCorrectStart() {
CharSequence input = "Hi.  There.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
List<Integer> eosPositions = Arrays.asList(2, 9);
when(scanner.getPositions(input)).thenReturn(eosPositions);
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "c" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.0, 1.0 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hi.", spans[0].getCoveredText(input).toString());
assertEquals("There.", spans[1].getCoveredText(input).toString());
}

@Test
public void testSplitPredictionButStartEqualsEnd_IndexCheckAvoidsInvalidSpan() {
CharSequence input = "Hi.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
List<Integer> eosPositions = Collections.singletonList(2);
when(scanner.getPositions(input)).thenReturn(eosPositions);
when(contextGen.getContext(eq(input), eq(2))).thenReturn(new String[] { "c" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Hi.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSplitBeforeWhitespaceFollowedByEarlyEOS_IsSkipped() {
CharSequence input = "Hello! ! Goodbye.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
List<Integer> eosPositions = Arrays.asList(5, 7, 16);
when(scanner.getPositions(input)).thenReturn(eosPositions);
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hello!", spans[0].getCoveredText(input).toString());
assertEquals("Goodbye.", spans[1].getCoveredText(input).toString());
}

@Test
public void testModelReturnsNoSplit_NothingRecorded() {
CharSequence input = "Hello world?";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
List<Integer> eosPositions = Collections.singletonList(11);
when(scanner.getPositions(input)).thenReturn(eosPositions);
when(contextGen.getContext(eq(input), eq(11))).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 1.0, 0.0 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.NO_SPLIT);
SentenceDetectorME detector = new SentenceDetectorME(model);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("Hello world?", sentences[0]);
}

@Test
public void testAbbreviationAdjacentToPunctuation_StillSuppressesSplit() {
CharSequence input = "Meet with Prof. Smith.";
Dictionary dictionary = mock(Dictionary.class);
StringList abbreviation = new StringList("Prof.");
// when(dictionary.iterator()).thenReturn(Collections.singleton(abbreviation).iterator());
MaxentModel maxentModel = mock(MaxentModel.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getLanguage()).thenReturn("en");
// when(model.getAbbreviations()).thenReturn(dictionary);
when(model.getEosCharacters()).thenReturn(null);
when(model.useTokenEnd()).thenReturn(false);
Factory factory = mock(Factory.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(factory.createSentenceContextGenerator(eq("en"), anySet())).thenReturn(contextGen);
when(factory.createEndOfSentenceScanner(eq("en"))).thenReturn(scanner);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(11));
when(contextGen.getContext(eq(input), eq(11))).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model, factory);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("Meet with Prof. Smith.", sentences[0]);
}

@Test
public void testOnlyDelimitersAsInput_ReturnsEmptyArray() {
CharSequence input = ".!?";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(0, 1, 2));
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.5, 0.5 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.NO_SPLIT);
SentenceDetectorME detector = new SentenceDetectorME(model);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals(".!?", sentences[0]);
}

@Test
public void testSentPosDetect_ModelPredictsSplitButIsAcceptableBreakReturnsFalse() {
CharSequence input = "This is Dr. Evans.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
StringList abbreviation = new StringList("Dr.");
Set<StringList> set = new HashSet<>();
set.add(abbreviation);
Dictionary dict = mock(Dictionary.class);
// when(dict.iterator()).thenReturn(set.iterator());
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(10));
when(contextGen.getContext(eq(input), eq(10))).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("This is Dr. Evans.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_UseTokenEndTrue_AdjustsWhitespaceBoundary() {
CharSequence input = "Hi there.   How are you?";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.useTokenEnd()).thenReturn(true);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(8));
when(contextGen.getContext(eq(input), eq(8))).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hi there.", spans[0].getCoveredText(input).toString());
assertEquals("How are you?", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_NoEOSButNonWhitespace_ReturnsSingleSpan() {
CharSequence input = "Only one sentence here";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Only one sentence here", spans[0].getCoveredText(input).toString());
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(1.0, probs[0], 0.0001);
}

@Test
public void testSentPosDetect_NullAbbreviationDictionary_AllowsSplits() {
CharSequence input = "OK. Yes.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(2, 7));
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("OK.", spans[0].getCoveredText(input).toString());
assertEquals("Yes.", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_SpanIsTrimmedToEmpty_RemovedFromProbList() {
CharSequence input = "Sentence.  ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(8));
when(contextGen.getContext(eq(input), eq(8))).thenReturn(new String[] { "c" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Sentence.", spans[0].getCoveredText(input).toString());
}

@Test
public void testIsAcceptableBreak_TokenDoesNotOverlapCandidate_ContinueSearch() {
CharSequence input = "Visit Mr. Jones tomorrow.";
StringList abbreviation = new StringList("Mr.");
Set<StringList> abbrevs = new HashSet<>();
abbrevs.add(abbreviation);
Dictionary dictionary = mock(Dictionary.class);
// when(dictionary.iterator()).thenReturn(abbrevs.iterator());
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(mock(SDContextGenerator.class));
when(factory.getEndOfSentenceScanner()).thenReturn(mock(EndOfSentenceScanner.class));
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak(input, 0, 22);
assertTrue(result);
}

@Test
public void testSentDetect_SingleCharInputWithoutEOS_ReturnsWholeString() {
CharSequence input = "x";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertEquals(1, sentences.length);
assertEquals("x", sentences[0]);
}

@Test
public void testSentPosDetect_EOSAfterWhitespace_ReturnsProperSpan() {
CharSequence input = "This is a test.    ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(14));
when(contextGenerator.getContext(eq(input), eq(14))).thenReturn(new String[] { "context" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("This is a test.", spans[0].getCoveredText(input).toString());
}

@Test
public void testIsAcceptableBreak_TokenFullyAfterCandidateIndex_ReturnsTrue() {
CharSequence input = "Today is sunny. Tomorrow is cold.";
StringList abbr = new StringList("is");
Set<StringList> tokens = new HashSet<>();
tokens.add(abbr);
Dictionary dictionary = mock(Dictionary.class);
// when(dictionary.iterator()).thenReturn(tokens.iterator());
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.getFactory()).thenReturn(factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean accepted = detector.isAcceptableBreak("Today is sunny. Tomorrow is cold.", 0, 15);
assertTrue(accepted);
}

@Test
public void testIsAcceptableBreak_AbbreviationStartsBeforeButEndsAfterCandidate_ReturnsFalse() {
CharSequence input = "He got a Ph.D in physics.";
StringList abbr = new StringList("Ph.D");
Set<StringList> abbrs = new HashSet<>();
abbrs.add(abbr);
Dictionary dictionary = mock(Dictionary.class);
// when(dictionary.iterator()).thenReturn(abbrs.iterator());
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.getFactory()).thenReturn(factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean accepted = detector.isAcceptableBreak("He got a Ph.D in physics.", 0, 12);
assertFalse(accepted);
}

@Test
public void testSentPosDetect_MalformedEOSPositionIgnoredDueToIndexCheck() {
CharSequence input = "Alpha. Beta.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
List<Integer> eosList = Arrays.asList(5, 2, 10);
when(scanner.getPositions(input)).thenReturn(eosList);
when(contextGen.getContext(eq(input), eq(5))).thenReturn(new String[] { "ctx" });
when(contextGen.getContext(eq(input), eq(10))).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Alpha.", spans[0].getCoveredText(input).toString());
assertEquals("Beta.", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentDetect_InputWithWhitespaceOnlyReturnsEmptyArray() {
CharSequence input = "     ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] results = detector.sentDetect(input);
assertNotNull(results);
assertEquals(0, results.length);
}

@Test
public void testSentPosDetect_ModelReturnsLowConfidence_NoSplitTaken() {
CharSequence input = "Why not";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(7));
when(contextGen.getContext(eq(input), eq(7))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.8, 0.2 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.NO_SPLIT);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Why not", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_SentenceSpansIncludingOnlyNewlinesAreSkipped() {
CharSequence input = "Line1.\n\nLine2";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
List<Integer> eos = Arrays.asList(5, 7, 12);
when(scanner.getPositions(input)).thenReturn(eos);
when(contextGen.getContext(eq(input), anyInt())).thenReturn(new String[] { "c" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Line1.", spans[0].getCoveredText(input).toString());
assertEquals("Line2", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_EOSNextToNonWhitespace() {
CharSequence input = "Hi!What?";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(2, 7));
when(context.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hi!", spans[0].getCoveredText(input));
assertEquals("What?", spans[1].getCoveredText(input));
}

@Test
public void testSentPosDetect_MultipleEOSCloselySpaced_SkipsOverlap() {
CharSequence input = "Wait... Done.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
List<Integer> eosList = Arrays.asList(4, 5, 6, 12);
when(scanner.getPositions(input)).thenReturn(eosList);
when(context.getContext(eq(input), anyInt())).thenReturn(new String[] { "ctx" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.25, 0.75 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Wait...", spans[0].getCoveredText(input));
assertEquals("Done.", spans[1].getCoveredText(input));
}

@Test
public void testIsAcceptableBreak_MatchingTokenContainsExtraWhitespace_NoMatch() {
CharSequence input = "This is Dr . Smith.";
Dictionary dictionary = mock(Dictionary.class);
StringList abbreviation = new StringList("Dr.");
Set<StringList> tokens = new HashSet<>();
tokens.add(abbreviation);
// when(dictionary.iterator()).thenReturn(tokens.iterator());
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(mock(MaxentModel.class));
// when(model.getAbbreviations()).thenReturn(dictionary);
when(model.getFactory()).thenReturn(mock(SentenceDetectorFactory.class));
SentenceDetectorME detector = new SentenceDetectorME(model);
boolean result = detector.isAcceptableBreak(input, 0, 9);
assertTrue(result);
}

@Test
public void testSentPosDetect_SplitsIgnoredIfResultingSpanIsWhitespaceOnly() {
CharSequence input = "Hello.     . Bye.";
MaxentModel maxentModel = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
SentenceModel model = mock(SentenceModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
// when(model.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(5, 11, 16));
when(context.getContext(eq(input), anyInt())).thenReturn(new String[] { "sample" });
when(maxentModel.eval(any())).thenReturn(new double[] { 0.05, 0.95 });
when(maxentModel.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(maxentModel.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(model);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hello.", spans[0].getCoveredText(input));
assertEquals("Bye.", spans[1].getCoveredText(input));
}

@Test
public void testGetSentenceProbabilities_EmptyList_ReturnsEmptyArray() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(factory.getSDContextGenerator()).thenReturn(mock(SDContextGenerator.class));
when(factory.getEndOfSentenceScanner()).thenReturn(mock(EndOfSentenceScanner.class));
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
double[] probs = detector.getSentenceProbabilities();
assertNotNull(probs);
assertEquals(0, probs.length);
}

@Test
public void testSentDetect_WithSingleEOSButZeroLengthSpanTrimmed() {
CharSequence input = ". ";
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Dictionary dict = mock(Dictionary.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(0));
when(context.getContext(eq(input), eq(0))).thenReturn(new String[] { "c" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(input);
assertNotNull(sentences);
assertEquals(0, sentences.length);
}

@Test
public void testEOSAtLastChar_NoLeftoverSpan() {
CharSequence input = "Done.";
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Dictionary dict = mock(Dictionary.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(4));
when(context.getContext(eq(input), eq(4))).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.0, 1.0 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(any())).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Done.", spans[0].getCoveredText(input));
}
}
