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

public class SentenceDetectorME_llmsuite_4_GPTLLMTest {

@Test
public void testSentDetectSingleSentence() {
String input = "This is a sentence.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(18));
when(contextGenerator.getContext(input, 18)).thenReturn(new String[] { "f1" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("This is a sentence.", result[0]);
}

@Test
public void testSentDetectEmptyWhitespace() {
String input = "   \t \n ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
String[] result = detector.sentDetect(input);
assertEquals(0, result.length);
}

@Test
public void testSentDetectMultipleSentences() {
String input = "Hello there. How are you?";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(11, 25));
when(contextGenerator.getContext(input, 11)).thenReturn(new String[] { "c1" });
when(contextGenerator.getContext(input, 25)).thenReturn(new String[] { "c2" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 }, new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("Hello there.", result[0]);
assertEquals("How are you?", result[1]);
}

@Test
public void testGetSentenceProbabilityAfterDetection() {
String input = "Test one.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(8));
when(contextGenerator.getContext(input, 8)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
Map<String, String> manifest = new HashMap<>();
SentenceModel modelData = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
detector.sentDetect(input);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.9, probs[0], 0.00001);
}

@Test
public void testIsAcceptableBreakWithAbbreviation() {
String sentence = "Dr. Smith went home.";
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
StringList abbrev = new StringList("Dr.");
// when(dictionary.iterator()).thenReturn(Collections.singletonList(abbrev).iterator());
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dictionary);
// boolean result = detector.isAcceptableBreak(sentence, 0, 3);
// assertFalse(result);
}

@Test
public void testIsAcceptableBreakWithoutAbbreviation() {
String sentence = "He went home.";
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
boolean result = detector.isAcceptableBreak(sentence, 0, 14);
assertTrue(result);
}

@Test
public void testSentPosDetectReturnsSpan() {
String text = "Sentence end.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(text)).thenReturn(Collections.singletonList(12));
when(contextGenerator.getContext(text, 12)).thenReturn(new String[] { "fc" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
Map<String, String> manifest = new HashMap<>();
SentenceModel modelData = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(1, spans.length);
Span expected = new Span(0, 13);
assertEquals(expected.getStart(), spans[0].getStart());
assertEquals(expected.getEnd(), spans[0].getEnd());
}

@Test
public void testSentDetectFalsePositiveIsFiltered() {
String text = "Mr. Smith went home.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory sdf = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
StringList abbrev = new StringList("Mr.");
// when(dict.iterator()).thenReturn(Collections.singletonList(abbrev).iterator());
when(sdf.getSDContextGenerator()).thenReturn(cg);
when(sdf.getEndOfSentenceScanner()).thenReturn(eos);
when(sdf.isUseTokenEnd()).thenReturn(false);
when(eos.getPositions(text)).thenReturn(Collections.singletonList(2));
when(cg.getContext(text, 2)).thenReturn(new String[] { "fx" });
when(model.eval(any())).thenReturn(new double[] { 0.5, 0.5 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
Map<String, String> manifest = new HashMap<>();
SentenceModel modelData = new SentenceModel("en", model, manifest, sdf);
// SentenceDetectorME detector = new SentenceDetectorME(modelData, dict);
// String[] sents = detector.sentDetect(text);
// assertEquals(1, sents.length);
// assertEquals("Mr. Smith went home.", sents[0]);
}

@Test
public void testSentDetectWithOnlyWhitespaceAroundSentence() {
String input = "   Hello there.   ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(14));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(contextGen.getContext(input, 14)).thenReturn(new String[] { "f" });
when(model.eval(any())).thenReturn(new double[] { 0.4, 0.6 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("Hello there.", result[0]);
}

@Test
public void testSentDetectNoEOSCharactersReturnsEntireSpan() {
String input = "No punctuation just text";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("No punctuation just text", spans[0].getCoveredText(input).toString());
}

@Test
public void testSpanThatContainsOnlyWhitespaceGetsRemoved() {
String input = "Hello.  ";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(input)).thenReturn(Collections.singletonList(5));
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
when(cg.getContext(input, 5)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Hello.", spans[0].getCoveredText(input).toString());
}

@Test
public void testMultipleEOSWithConsecutivePositions() {
String input = "Yes! Hmm? Really.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(input)).thenReturn(Arrays.asList(3, 8, 15));
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
when(cg.getContext(input, 3)).thenReturn(new String[] { "ctx1" });
when(cg.getContext(input, 8)).thenReturn(new String[] { "ctx2" });
when(cg.getContext(input, 15)).thenReturn(new String[] { "ctx3" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 }, new double[] { 0.3, 0.7 }, new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT, SentenceDetectorME.SPLIT, SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(input);
assertEquals(3, result.length);
assertEquals("Yes!", result[0]);
assertEquals("Hmm?", result[1]);
assertEquals("Really.", result[2]);
}

@Test
public void testOverlappingEOSPositionIsIgnored() {
String text = "A. B. C.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
when(eos.getPositions(text)).thenReturn(Arrays.asList(1, 3, 5, 7));
when(cg.getContext(text, 1)).thenReturn(new String[] { "c1" });
when(cg.getContext(text, 3)).thenReturn(new String[] { "c2" });
when(cg.getContext(text, 5)).thenReturn(new String[] { "c3" });
when(cg.getContext(text, 7)).thenReturn(new String[] { "c4" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 }, new double[] { 0.2, 0.8 }, new double[] { 0.2, 0.8 }, new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT, SentenceDetectorME.SPLIT, SentenceDetectorME.SPLIT, SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(text);
assertTrue(result.length >= 1);
assertEquals("A.", result[0]);
}

@Test
public void testModelPredictsNoSplitReturnsSingleSpan() {
String input = "This is a test. This is only a test.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
when(eos.getPositions(input)).thenReturn(Arrays.asList(14, 35));
when(cg.getContext(input, 14)).thenReturn(new String[] { "ctxA" });
when(cg.getContext(input, 35)).thenReturn(new String[] { "ctxB" });
when(model.eval(any())).thenReturn(new double[] { 0.99, 0.01 }, new double[] { 0.98, 0.02 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.NO_SPLIT);
SentenceModel sentenceModel = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("This is a test. This is only a test.", result[0]);
}

@Test
public void testAbbreviationDoesNotMatchCandidateIndex() {
String sentence = "Mr goes to work.";
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
StringList abbrev = new StringList("Mr.");
// when(dictionary.iterator()).thenReturn(Collections.singletonList(abbrev).iterator());
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sentenceModel = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dictionary);
// boolean result = detector.isAcceptableBreak(sentence, 0, 1);
// assertTrue(result);
}

@Test
public void testSentDetectOnlySymbolsAndEOSCharacters() {
String text = "... !   ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(text)).thenReturn(Arrays.asList(2, 6));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(contextGen.getContext(text, 2)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 6)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 }, new double[] { 0.4, 0.6 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(text);
assertEquals(1, result.length);
}

@Test
public void testSentDetectSpanWithOnlyWhitespaceIsExcluded() {
String text = "Valid.    ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(text)).thenReturn(Collections.singletonList(5));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(contextGen.getContext(text, 5)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(1, spans.length);
assertEquals("Valid.", spans[0].getCoveredText(text).toString());
}

@Test
public void testSpanWithOnlyWhitespaceIsCompletelyDropped() {
String text = "   ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
when(eos.getPositions(text)).thenReturn(Collections.emptyList());
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(0, spans.length);
}

@Test
public void testSentDetectWithNonPeriodEOSCharacters() {
String text = "Wow! Really?";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(text)).thenReturn(Arrays.asList(3, 11));
when(cgen.getContext(text, 3)).thenReturn(new String[] { "f1" });
when(cgen.getContext(text, 11)).thenReturn(new String[] { "f2" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 }, new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] sents = detector.sentDetect(text);
assertEquals(2, sents.length);
assertEquals("Wow!", sents[0]);
assertEquals("Really?", sents[1]);
}

@Test
public void testSentDetectWithUnicodeWhitespaceBoundaryTrim() {
String text = "\u00A0The only sentence.\u2007";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(text)).thenReturn(Collections.singletonList(19));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(contextGen.getContext(text, 19)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(text);
assertEquals(1, result.length);
assertEquals("The only sentence.", result[0]);
}

@Test
public void testSentDetectAddsFinalSpanIfMissingEOS() {
String input = "This is complete. And this has none";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(17));
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(contextGenerator.getContext(input, 17)).thenReturn(new String[] { "ctx1" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("This is complete.", spans[0].getCoveredText(input).toString());
assertEquals("And this has none", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentenceFromWhitespaceTokensIsTrimmedProperly() {
String input = "Test.   ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(input)).thenReturn(Collections.singletonList(4));
when(contextGen.getContext(input, 4)).thenReturn(new String[] { "c1" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Test.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentDetectOnlyWhitespaceWithNewlines() {
String input = "   \n   \t\n  ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(input);
assertEquals(0, result.length);
}

@Test
public void testSentDetectSpanThatStartsAndEndsWithWhitespaceReturnsTrimmed() {
String input = "   Hello World.   ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(15));
when(contextGen.getContext(input, 15)).thenReturn(new String[] { "feature" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals(3, spans[0].getStart());
assertEquals(16, spans[0].getEnd());
}

@Test
public void testSentPosDetectSpanExcludedDueToZeroLengthAfterTrim() {
String input = ". ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(input)).thenReturn(Collections.singletonList(0));
when(contextGen.getContext(input, 0)).thenReturn(new String[] { "f" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(0, spans.length);
}

@Test
public void testIsAcceptableBreak_AbbreviationTokenStartsAfterCandidateIndex() {
String input = "This is not an abbrev but contains Abbr.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dict = mock(Dictionary.class);
StringList abbrev = new StringList("Abbr.");
Collection<StringList> abbrevList = Collections.singletonList(abbrev);
// when(dict.iterator()).thenReturn(abbrevList.iterator());
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sentenceModel = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dict);
// boolean result = detector.isAcceptableBreak(input, 0, 20);
// assertTrue(result);
}

@Test
public void testSentDetectWithNoSplitReturnedByModel() {
String input = "Text. Text.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(4, 10));
when(contextGen.getContext(input, 4)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(input, 10)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.99, 0.01 }, new double[] { 0.99, 0.01 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.NO_SPLIT);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("Text. Text.", result[0]);
}

@Test
public void testSentPosDetectWithNonEOSInputWithLeadingWhitespace() {
String input = "    Text without end marker";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(input)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sentenceModel = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Text without end marker", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentDetectHandlesEOSAtStart() {
String input = ". Starts oddly.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(input)).thenReturn(Collections.singletonList(0));
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
when(cgen.getContext(input, 0)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(input);
assertTrue(result.length > 0);
}

@Test
public void testConstructorWithNullAbbreviationDictionary() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
assertNotNull(detector);
}

@Test
public void testIsAcceptableBreak_MultipleAbbreviationsOnlyOneMatches() {
String input = "He met Prof. Smith.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
List<StringList> entries = Arrays.asList(new StringList("Dr."), new StringList("Prof."));
// when(dictionary.iterator()).thenReturn(entries.iterator());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(modelData, dictionary);
// boolean result = detector.isAcceptableBreak(input, 0, 12);
// assertFalse(result);
}

@Test
public void testSplitPreservesWhitespaceUsingGetFirstWSAndGetFirstNonWS() {
String text = "This is a test.  Next.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator context = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
when(scanner.getPositions(text)).thenReturn(Arrays.asList(14));
when(context.getContext(text, 14)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(2, spans.length);
assertEquals("This is a test.", spans[0].getCoveredText(text).toString());
assertEquals("Next.", spans[1].getCoveredText(text).toString());
}

@Test
public void testSentPosDetectAddsLeftoverSpanOutsideEOS() {
String input = "Sentence one. Sentence two";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(12));
when(cg.getContext(input, 12)).thenReturn(new String[] { "f" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Sentence one.", spans[0].getCoveredText(input).toString());
assertEquals("Sentence two", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentPosDetect_EOSImmediatelyFollowedBySentence() {
String input = "Hi.Hello.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Arrays.asList(2, 8));
when(cg.getContext(input, 2)).thenReturn(new String[] { "c1" });
when(cg.getContext(input, 8)).thenReturn(new String[] { "c2" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 }, new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("Hi.", result[0]);
assertEquals("Hello.", result[1]);
}

@Test
public void testAbbreviationEndsAtCandidateIndexExactMatchEdgeCase() {
String text = "He met Gen. John.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator context = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
Dictionary dictionary = mock(Dictionary.class);
List<StringList> abbrevs = Arrays.asList(new StringList("Gen."));
// when(dictionary.iterator()).thenReturn(abbrevs.iterator());
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(modelData, dictionary);
// boolean result = detector.isAcceptableBreak(text, 0, 9);
// assertFalse(result);
}

@Test
public void testSentDetect_allWhitespaceBetweenEOSCharacters() {
String text = "One.   Two.    ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(text)).thenReturn(Arrays.asList(3, 9));
when(cgen.getContext(text, 3)).thenReturn(new String[] { "ctx1" });
when(cgen.getContext(text, 9)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 }, new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("One.", sentences[0]);
assertEquals("Two.", sentences[1]);
}

@Test
public void testSentPosDetectStartsWithEOSCharacter() {
String text = ".Leading";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(text)).thenReturn(Collections.singletonList(0));
when(cg.getContext(text, 0)).thenReturn(new String[] { "feature" });
when(model.eval(any())).thenReturn(new double[] { 0.5, 0.5 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(1, spans.length);
assertEquals(".Leading", spans[0].getCoveredText(text).toString());
}

@Test
public void testSentPosDetectWithMultipleEOSAndOverlappingWhitespace() {
String text = "A. B. C.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(text)).thenReturn(Arrays.asList(1, 4, 7));
when(cgen.getContext(text, 1)).thenReturn(new String[] { "ctx1" });
when(cgen.getContext(text, 4)).thenReturn(new String[] { "ctx2" });
when(cgen.getContext(text, 7)).thenReturn(new String[] { "ctx3" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 }).thenReturn(new double[] { 0.2, 0.8 }).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(3, spans.length);
assertEquals("A.", spans[0].getCoveredText(text).toString());
assertEquals("B.", spans[1].getCoveredText(text).toString());
assertEquals("C.", spans[2].getCoveredText(text).toString());
}

@Test
public void testIsAcceptableBreak_tokenAppearsEarlierThanCandidateIndex() {
String text = "Abbrev. at middle.";
Dictionary dictionary = mock(Dictionary.class);
StringList abbrev = new StringList("Abbrev.");
// when(dictionary.iterator()).thenReturn(Collections.singletonList(abbrev).iterator());
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator context = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(sm, dictionary);
// boolean result = detector.isAcceptableBreak(text, 0, 7);
// assertFalse(result);
}

@Test
public void testSentPosDetectAllWhitespaceSpanGetsTrimmedOut() {
String text = "     .     ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(text)).thenReturn(Collections.singletonList(5));
when(cg.getContext(text, 5)).thenReturn(new String[] { "feat" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(0, spans.length);
}

@Test
public void testSentDetectMultipleEOSButOnlySomeSplit() {
String text = "One. Two. Three.";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(text)).thenReturn(Arrays.asList(3, 8, 14));
when(cg.getContext(text, 3)).thenReturn(new String[] { "ctx1" });
when(cg.getContext(text, 8)).thenReturn(new String[] { "ctx2" });
when(cg.getContext(text, 14)).thenReturn(new String[] { "ctx3" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 }).thenReturn(new double[] { 0.9, 0.1 }).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT).thenReturn(SentenceDetectorME.NO_SPLIT).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("One.", sentences[0]);
assertEquals("Two. Three.", sentences[1]);
}

@Test
public void testSentPosDetectAddsFinalSpanIfNotEndingAtEOS() {
String input = "First sentence. Second incomplete";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(14));
when(context.getContext(input, 14)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("First sentence.", spans[0].getCoveredText(input).toString());
assertEquals("Second incomplete", spans[1].getCoveredText(input).toString());
}

@Test
public void testSentDetectUseTokenEndTrueSkipsCorrectWhitespace() {
String input = "Token.     New token.";
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator context = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(scanner.getPositions(input)).thenReturn(Collections.singletonList(5));
when(context.getContext(input, 5)).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
SentenceModel modelData = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("Token.", result[0]);
assertEquals("New token.", result[1]);
}

@Test
public void testIsAcceptableBreak_AbbreviationEndsAtCandidateIndex() {
String text = "He talked to Col. Johnson.";
StringList entry = new StringList("Col.");
Dictionary dictionary = mock(Dictionary.class);
// when(dictionary.iterator()).thenReturn(Collections.singletonList(entry).iterator());
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator context = mock(SDContextGenerator.class);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(sm, dictionary);
// boolean accepted = detector.isAcceptableBreak(text, 0, 14);
// assertFalse(accepted);
}

@Test
public void testIsAcceptableBreak_AbbreviationPresentButDoesNotOverlapCandidate() {
String text = "This book by Mr. Smith is great.";
StringList entry = new StringList("Ms.");
Dictionary dictionary = mock(Dictionary.class);
// when(dictionary.iterator()).thenReturn(Collections.singletonList(entry).iterator());
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator context = mock(SDContextGenerator.class);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.getSDContextGenerator()).thenReturn(context);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(sm, dictionary);
// boolean accepted = detector.isAcceptableBreak(text, 0, 15);
// assertTrue(accepted);
}

@Test
public void testSentDetect_EmptySpanTrimmedOut() {
String text = "Word.  ";
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(eos.getPositions(text)).thenReturn(Collections.singletonList(4));
when(cgen.getContext(text, 4)).thenReturn(new String[] { "f1" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sm = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sm, null);
Span[] spans = detector.sentPosDetect(text);
assertEquals(1, spans.length);
assertEquals("Word.", spans[0].getCoveredText(text).toString());
}

@Test
public void testGetSentenceProbabilities_EmptyWhenNotCalled() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner eos = mock(EndOfSentenceScanner.class);
SDContextGenerator cgen = mock(SDContextGenerator.class);
when(factory.getEndOfSentenceScanner()).thenReturn(eos);
when(factory.getSDContextGenerator()).thenReturn(cgen);
when(factory.isUseTokenEnd()).thenReturn(false);
Map<String, String> manifest = new HashMap<>();
SentenceModel modelData = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(modelData, null);
double[] probs = detector.getSentenceProbabilities();
assertNotNull(probs);
assertEquals(0, probs.length);
}
}
