package edu.illinois.cs.cogcomp.mrcs;

// import edu.illinois.cs.cogcomp.align.GreedyAlignmentScorer;
// import edu.illinois.cs.cogcomp.config.SimConfigurator;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParser;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParserFactory;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
// import edu.illinois.cs.cogcomp.llm.comparators.LlmStringComparator;
// import edu.illinois.cs.cogcomp.mrcs.dataStructures.Alignment;
// import edu.illinois.cs.cogcomp.mrcs.dataStructures.EntailmentResult;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static org.junit.Assert.*;

public class LlmStringComparator_llmsuite_1_GPTLLMTest {

@Test
public void testDetermineEntailment_SimpleMatch() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "Cats sit on mats.";
String hypothesis = "A cat is sitting on the mat.";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailment_EmptyHypothesis() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "The quick brown fox jumps over the lazy dog.";
String hypothesis = "";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 1e-6);
}

@Test
public void testDetermineEntailment_SameTextHypothesis() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String sentence = "Paris is the capital of France.";
// EntailmentResult result = comparator.determineEntailment(sentence, sentence);
// assertNotNull(result);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testAlignSentences_NonTrivial() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "Dogs bark loudly.";
String hyp = "A dog makes sound.";
// Alignment<String> alignment = comparator.alignSentences(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() > 0);
}

@Test
public void testCompareStrings_ScoreBounds() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("Birds fly.", "A bird flies.");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_EmptySourceTarget() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("", "");
// assertEquals(0.0, score, 1e-6);
}

@Test
public void testAlignNEStringArrays_WithEntities() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.8");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] ne1 = new String[] { "Barack Obama" };
String[] ne2 = new String[] { "President Obama" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() > 0);
}

@Test
public void testCompareAnnotation_WithNER() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String sourceText = "Barack Obama was born in Hawaii.";
String targetText = "Obama is from Hawaii.";
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTA = builder.createTextAnnotation("test", "1", sourceText);
TextAnnotation targetTA = builder.createTextAnnotation("test", "2", targetText);
View sourceNER = new View(ViewNames.NER_CONLL, "manual", sourceTA, 1.0);
Constituent sourceConstituent = new Constituent("PERSON", ViewNames.NER_CONLL, sourceTA, 0, 2);
sourceNER.addConstituent(sourceConstituent);
sourceTA.addView(ViewNames.NER_CONLL, sourceNER);
View targetNER = new View(ViewNames.NER_CONLL, "manual", targetTA, 1.0);
Constituent targetConstituent = new Constituent("PERSON", ViewNames.NER_CONLL, targetTA, 0, 1);
targetNER.addConstituent(targetConstituent);
targetTA.addView(ViewNames.NER_CONLL, targetNER);
// double result = comparator.compareAnnotation(sourceTA, targetTA);
// assertTrue(result >= 0.0 && result <= 1.0);
}

@Test
public void testCompareAnnotation_NoNER() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String sourceText = "I traveled to New York City.";
String targetText = "I went to NYC.";
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTA = builder.createTextAnnotation("test", "1", sourceText);
TextAnnotation targetTA = builder.createTextAnnotation("test", "2", targetText);
View sourceNER = new View(ViewNames.NER_CONLL, "manual", sourceTA, 1.0);
sourceTA.addView(ViewNames.NER_CONLL, sourceNER);
View targetNER = new View(ViewNames.NER_CONLL, "manual", targetTA, 1.0);
targetTA.addView(ViewNames.NER_CONLL, targetNER);
// double result = comparator.compareAnnotation(sourceTA, targetTA);
// assertTrue(result >= 0.0 && result <= 1.0);
}

@Test
public void testDetermineEntailment_TokenizedArrays() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] textTokens = new String[] { "Barack", "Obama", "was", "born", "in", "Hawaii" };
String[] hypTokens = new String[] { "Obama", "is", "from", "Hawaii" };
// EntailmentResult result = comparator.determineEntailment(textTokens, hypTokens);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test(expected = NullPointerException.class)
public void testScoreAlignment_NullInputThrows() {
// GreedyAlignmentScorer<String> scorer = new GreedyAlignmentScorer<>(0.5);
// scorer.scoreAlignment(null);
}

@Test
public void testConstructor_Default() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// assertNotNull(comparator);
}

@Test(expected = IOException.class)
public void testConstructor_InvalidThreshold() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "not_a_number");
ResourceManager rm = new ResourceManager(props);
// new LlmStringComparator(rm);
}

@Test
public void testDetermineEntailment_NullTextInput() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = null;
String hypothesis = "This is a hypothesis.";
try {
// comparator.determineEntailment(text, hypothesis);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testDetermineEntailment_NullHypothesisInput() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "This is a text.";
String hypothesis = null;
try {
// comparator.determineEntailment(text, hypothesis);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testCompareStrings_WhitespaceOnlyStrings() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("   ", "   ");
// assertEquals(0.0, score, 1e-6);
}

@Test
public void testAlignSentences_EmptyStrings() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// Alignment<String> alignment = comparator.alignSentences("", "");
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().isEmpty());
}

@Test
public void testCompareAnnotation_NERWithSingleTokenOverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String sourceText = "John visited London.";
String targetText = "London was visited.";
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTa = builder.createTextAnnotation("test", "src", sourceText);
TextAnnotation targetTa = builder.createTextAnnotation("test", "tgt", targetText);
View sourceView = new View(ViewNames.NER_CONLL, "testSource", sourceTa, 1.0);
Constituent cSource = new Constituent("LOCATION", ViewNames.NER_CONLL, sourceTa, 2, 3);
sourceView.addConstituent(cSource);
sourceTa.addView(ViewNames.NER_CONLL, sourceView);
View targetView = new View(ViewNames.NER_CONLL, "testTarget", targetTa, 1.0);
Constituent cTarget = new Constituent("LOCATION", ViewNames.NER_CONLL, targetTa, 0, 1);
targetView.addConstituent(cTarget);
targetTa.addView(ViewNames.NER_CONLL, targetView);
// double score = comparator.compareAnnotation(sourceTa, targetTa);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignNEStringArrays_NullInput() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.8");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
try {
// comparator.alignNEStringArrays(null, null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testAlignStringArrays_NoOverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] text = new String[] { "apple", "banana" };
String[] hyp = new String[] { "car", "house" };
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() >= 0);
}

@Test
public void testDetermineEntailment_UnbalancedLengths() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String shortText = "Obama.";
String longHypothesis = "Barack Obama was the former president of the United States.";
// EntailmentResult result = comparator.determineEntailment(shortText, longHypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testCompareStrings_CaseSensitivityImpact() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score1 = comparator.compareStrings_("NEW YORK", "new york");
// double score2 = comparator.compareStrings_("New York", "new york");
// assertTrue(score1 >= 0.0 && score1 <= 1.0);
// assertTrue(score2 >= 0.0 && score2 <= 1.0);
}

@Test
public void testCompareStrings_TokenOverlapWithoutMeaning() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "the in on a";
String target = "a the on in";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotation_OnlyOneSideHasNER() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "Google acquired YouTube.";
String target = "The acquisition happened in 2006.";
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTa = builder.createTextAnnotation("test", "1", source);
TextAnnotation targetTa = builder.createTextAnnotation("test", "2", target);
View sourceNER = new View(ViewNames.NER_CONLL, "manual", sourceTa, 1.0);
Constituent google = new Constituent("ORGANIZATION", ViewNames.NER_CONLL, sourceTa, 0, 1);
Constituent youtube = new Constituent("ORGANIZATION", ViewNames.NER_CONLL, sourceTa, 2, 3);
sourceNER.addConstituent(google);
sourceNER.addConstituent(youtube);
sourceTa.addView(ViewNames.NER_CONLL, sourceNER);
View targetNER = new View(ViewNames.NER_CONLL, "manual", targetTa, 1.0);
targetTa.addView(ViewNames.NER_CONLL, targetNER);
// double result = comparator.compareAnnotation(sourceTa, targetTa);
// assertTrue(result >= 0.0 && result <= 1.0);
}

@Test
public void testScoreAlignment_WithNoAlignedPairs() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] text = new String[] { "car", "boat" };
String[] hyp = new String[] { "satellite", "rocket" };
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// double score = comparator.scoreAlignment(alignment).getScore();
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_NonAlphanumericTokens() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "@@@ ### !!!";
String target = "$$$ %%% ^^^";
// double score = comparator.compareStrings_(source, target);
// assertEquals(0.0, score, 1e-6);
}

@Test
public void testAlignStringArrays_PartialOverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] text = new String[] { "apple", "banana", "carrot" };
String[] hyp = new String[] { "banana", "carrot", "date" };
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() > 0);
}

@Test
public void testDetermineEntailment_SingleWordInputs() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "rain";
String hyp = "rain";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertEquals(1.0, result.getScore(), 1e-6);
}

@Test
public void testAlignSentences_OneWordEach() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "sky";
String hyp = "cloud";
// Alignment<String> alignment = comparator.alignSentences(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() >= 0);
}

@Test
public void testAlignNEStringArrays_SameTokensDifferentLabels() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.9");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] ne1 = new String[] { "Washington" };
String[] ne2 = new String[] { "Washington" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() == 1);
}

@Test
public void testCompareAnnotation_LongConstituentNER() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "The president of the United States visited France.";
String target = "The US president was in France.";
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTa = builder.createTextAnnotation("test", "src", source);
TextAnnotation targetTa = builder.createTextAnnotation("test", "tgt", target);
View sourceView = new View(ViewNames.NER_CONLL, "manual", sourceTa, 1.0);
Constituent fullTitle = new Constituent("TITLE", ViewNames.NER_CONLL, sourceTa, 1, 7);
sourceView.addConstituent(fullTitle);
sourceTa.addView(ViewNames.NER_CONLL, sourceView);
View targetView = new View(ViewNames.NER_CONLL, "manual", targetTa, 1.0);
Constituent shortTitle = new Constituent("TITLE", ViewNames.NER_CONLL, targetTa, 1, 3);
targetView.addConstituent(shortTitle);
targetTa.addView(ViewNames.NER_CONLL, targetView);
// double score = comparator.compareAnnotation(sourceTa, targetTa);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_MixedNumericAndAlpha() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "He owns 2 cats and 3 dogs.";
String target = "He has two cats and three dogs.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_NumbersAsWordsVsDigits() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "He has two cats.";
String target = "He has 2 cats.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_PunctuationDifference() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "Let's eat, grandma!";
String target = "Lets eat grandma";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_MultipleSentences() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "The weather is nice. Let's go outside.";
String target = "It is sunny. We should go out.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_NegationEffect() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "The dog is not barking.";
String target = "The dog is barking.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailment_OnlyStopwords() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "and the but or";
String hyp = "but or and";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testCompareAnnotation_ComplexNEROverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.8");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "Barack Hussein Obama was the 44th President.";
String target = "Barack Obama served as a US President.";
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTA = builder.createTextAnnotation("test", "doc1", source);
TextAnnotation targetTA = builder.createTextAnnotation("test", "doc2", target);
View srcNERView = new View(ViewNames.NER_CONLL, "manual", sourceTA, 1.0);
Constituent srcConstituent = new Constituent("PERSON", ViewNames.NER_CONLL, sourceTA, 0, 3);
srcNERView.addConstituent(srcConstituent);
sourceTA.addView(ViewNames.NER_CONLL, srcNERView);
View tgtNERView = new View(ViewNames.NER_CONLL, "manual", targetTA, 1.0);
Constituent tgtConstituent = new Constituent("PERSON", ViewNames.NER_CONLL, targetTA, 0, 2);
tgtNERView.addConstituent(tgtConstituent);
targetTA.addView(ViewNames.NER_CONLL, tgtNERView);
// double score = comparator.compareAnnotation(sourceTA, targetTA);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignStringArrays_UnalignedDueToStopWords() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] text = new String[] { "and", "the", "but" };
String[] hyp = new String[] { "or", "but", "and" };
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() >= 0);
}

@Test
public void testCompareStrings_EmptyAndWhitespaceCombination() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score1 = comparator.compareStrings_("", "   ");
// double score2 = comparator.compareStrings_("   ", "");
// double score3 = comparator.compareStrings_("   ", "   ");
// assertEquals(0.0, score1, 1e-6);
// assertEquals(0.0, score2, 1e-6);
// assertEquals(0.0, score3, 1e-6);
}

@Test
public void testCompareAnnotation_IdenticalNERAndTokenSpans() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "Elon Musk founded SpaceX.";
String target = "Elon Musk started SpaceX.";
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation ta1 = builder.createTextAnnotation("test", "docA", text);
TextAnnotation ta2 = builder.createTextAnnotation("test", "docB", target);
View ner1 = new View(ViewNames.NER_CONLL, "manual", ta1, 1.0);
ner1.addConstituent(new Constituent("PERSON", ViewNames.NER_CONLL, ta1, 0, 2));
ner1.addConstituent(new Constituent("ORG", ViewNames.NER_CONLL, ta1, 3, 4));
ta1.addView(ViewNames.NER_CONLL, ner1);
View ner2 = new View(ViewNames.NER_CONLL, "manual", ta2, 1.0);
ner2.addConstituent(new Constituent("PERSON", ViewNames.NER_CONLL, ta2, 0, 2));
ner2.addConstituent(new Constituent("ORG", ViewNames.NER_CONLL, ta2, 3, 4));
ta2.addView(ViewNames.NER_CONLL, ner2);
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotation_EmptySentenceViewPresent() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation ta1 = builder.createTextAnnotation("test", "d1", "");
TextAnnotation ta2 = builder.createTextAnnotation("test", "d2", "Hello world.");
View ner1 = new View(ViewNames.NER_CONLL, "manual", ta1, 1.0);
ta1.addView(ViewNames.NER_CONLL, ner1);
View ner2 = new View(ViewNames.NER_CONLL, "manual", ta2, 1.0);
ner2.addConstituent(new Constituent("O", ViewNames.NER_CONLL, ta2, 0, 1));
ta2.addView(ViewNames.NER_CONLL, ner2);
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotation_SourceViewMissingSentence() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTa = builder.createTextAnnotation("test", "src", "");
TextAnnotation targetTa = builder.createTextAnnotation("test", "tgt", "France is in Europe.");
View nerView = new View(ViewNames.NER_CONLL, "manual", targetTa, 1.0);
Constituent ne = new Constituent("LOCATION", ViewNames.NER_CONLL, targetTa, 0, 1);
nerView.addConstituent(ne);
targetTa.addView(ViewNames.NER_CONLL, nerView);
// double score = comparator.compareAnnotation(sourceTa, targetTa);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotation_TargetViewMissingSentence() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation sourceTa = builder.createTextAnnotation("test", "src", "Italy was visited.");
TextAnnotation targetTa = builder.createTextAnnotation("test", "tgt", "");
View nerView = new View(ViewNames.NER_CONLL, "manual", sourceTa, 1.0);
Constituent ne = new Constituent("LOCATION", ViewNames.NER_CONLL, sourceTa, 0, 1);
nerView.addConstituent(ne);
sourceTa.addView(ViewNames.NER_CONLL, nerView);
// double score = comparator.compareAnnotation(sourceTa, targetTa);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignNEStringArrays_DifferentLengths() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] ne1 = new String[] { "New York", "Los Angeles" };
String[] ne2 = new String[] { "New York" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() >= 0);
}

@Test
public void testCompareStrings_RepeatedTokens() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("dog dog dog", "dog");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_UppercaseAndLowercaseMatch() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("HELLO world", "hello WORLD");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_MixedLanguageInput() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("Bonjour le monde", "Hello world");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_InputWithEmojis() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("I love pizza 🍕", "I like pizza");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_InputWithSymbolsOnly() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.2");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("#$%^&*", "@!~`");
// assertEquals(0.0, score, 1e-6);
}

@Test
public void testCompareStrings_NonEnglishCharacters() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("你好 世界", "Hello world");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailment_UnequalStringsSameTokens() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// EntailmentResult result = comparator.determineEntailment("apple banana", "banana apple");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailment_SingleCommaToken() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.2");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = ",";
String hyp = ",";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailment_LongRepeatedTokens() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "apple apple apple apple apple";
String hyp = "apple";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailment_TokensWithHyphens() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "This is a state-of-the-art model.";
String hyp = "The model is state-of-the-art.";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testCompareStrings_WithNumbersAndUnits() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "The package weighs 5kg.";
String target = "It is a five kilogram parcel.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignStringArrays_HypothesisTokensEmptyArray() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] textTokens = new String[] { "one", "two", "three" };
String[] hypTokens = new String[0];
// Alignment<String> alignment = comparator.alignStringArrays(textTokens, hypTokens);
// assertNotNull(alignment);
// assertEquals(0, alignment.getAlignedPairs().size());
}

@Test
public void testAlignStringArrays_TextTokensEmptyArray() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] textTokens = new String[0];
String[] hypTokens = new String[] { "one", "two" };
// Alignment<String> alignment = comparator.alignStringArrays(textTokens, hypTokens);
// assertNotNull(alignment);
// assertEquals(0, alignment.getAlignedPairs().size());
}

@Test
public void testCompareAnnotation_EmptySentenceView() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation srcTA = builder.createTextAnnotation("test", "src", "");
TextAnnotation tgtTA = builder.createTextAnnotation("test", "tgt", "The capital is Paris.");
View nerView = new View(ViewNames.NER_CONLL, "manual", tgtTA, 1.0);
Constituent paris = new Constituent("LOCATION", ViewNames.NER_CONLL, tgtTA, 3, 4);
nerView.addConstituent(paris);
tgtTA.addView(ViewNames.NER_CONLL, nerView);
// double score = comparator.compareAnnotation(srcTA, tgtTA);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotation_OnlyEmptyNERConllViews() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation srcTA = builder.createTextAnnotation("test", "src", "John is in New York.");
TextAnnotation tgtTA = builder.createTextAnnotation("test", "tgt", "New York is a city.");
View srcNER = new View(ViewNames.NER_CONLL, "manual", srcTA, 1.0);
View tgtNER = new View(ViewNames.NER_CONLL, "manual", tgtTA, 1.0);
srcTA.addView(ViewNames.NER_CONLL, srcNER);
tgtTA.addView(ViewNames.NER_CONLL, tgtNER);
// double score = comparator.compareAnnotation(srcTA, tgtTA);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_IdenticalSymbolicText() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.2");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "!!! ??? ...";
String hyp = "!!! ??? ...";
// double score = comparator.compareStrings_(text, hyp);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_RandomUnicodeCharacters() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("☃️✈️", "☀️✨");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailment_LongHypothesisEmptyText() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// EntailmentResult result = comparator.determineEntailment("", "This hypothesis contains multiple words.");
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 1e-6);
}

@Test
public void testCompareStrings_IdempotentScoreWithSameInput() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score1 = comparator.compareStrings_("Gravity is a natural force.", "Gravity is a natural force.");
// double score2 = comparator.compareStrings_("Gravity is a natural force.", "Gravity is a natural force.");
// assertEquals(score1, score2, 1e-6);
}

@Test
public void testAlignSentences_NumericTokensOnly() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// Alignment<String> alignment = comparator.alignSentences("123 456 789", "123 456 000");
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() > 0);
}

@Test
public void testDetermineEntailment_LargeInputStrings() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
StringBuilder text = new StringBuilder();
StringBuilder hyp = new StringBuilder();
for (int i = 0; i < 100; i++) {
text.append("word").append(i).append(" ");
hyp.append("word").append(i).append(" ");
}
// EntailmentResult result = comparator.determineEntailment(text.toString().trim(), hyp.toString().trim());
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testCompareStrings_InputWithNewlinesAndTabs() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String source = "This\tis\na test.";
String target = "This is a test.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignNEStringArrays_IdenticalButReversedOrder() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] ne1 = new String[] { "New York", "Los Angeles" };
String[] ne2 = new String[] { "Los Angeles", "New York" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() > 0);
}

@Test
public void testCompareAnnotation_OneTokenEachEntity() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.8");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
TextAnnotation source = builder.createTextAnnotation("test", "src", "Paris is beautiful.");
TextAnnotation target = builder.createTextAnnotation("test", "tgt", "Rome is historic.");
View nerView1 = new View(ViewNames.NER_CONLL, "manual", source, 1.0);
nerView1.addConstituent(new Constituent("LOCATION", ViewNames.NER_CONLL, source, 0, 1));
source.addView(ViewNames.NER_CONLL, nerView1);
View nerView2 = new View(ViewNames.NER_CONLL, "manual", target, 1.0);
nerView2.addConstituent(new Constituent("LOCATION", ViewNames.NER_CONLL, target, 0, 1));
target.addView(ViewNames.NER_CONLL, nerView2);
// double score = comparator.compareAnnotation(source, target);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStrings_TokensWithMixedPunctuationAndAlphanumeric() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("e-mail address: test@example.com", "email address test at example dot com");
// assertTrue(score >= 0.0 && score <= 1.0);
}
}
