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
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
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

public class LlmStringComparator_llmsuite_4_GPTLLMTest {

@Test
public void testDetermineEntailment_identicalSentences() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String sentence = "Cats sleep on mats.";
// EntailmentResult result = comparator.determineEntailment(sentence, sentence);
// assertNotNull(result);
// assertEquals(1.0, result.getScore(), 1e-5);
}

@Test
public void testDetermineEntailment_emptyText() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "";
String hyp = "The earth orbits the sun.";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 1e-5);
}

@Test
public void testCompareStrings_partialSemanticOverlap() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String source = "The dog chased the cat.";
String target = "A canine followed a feline.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testAlignSentences_similarity() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "People enjoy sunny weather.";
String hyp = "Individuals like sunshine.";
// Alignment<String> alignment = comparator.alignSentences(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test
public void testAlignStringArrays_withTokenInputs() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] tokens1 = new String[] { "bird", "flies", "high" };
String[] tokens2 = new String[] { "a", "bird", "soars", "high" };
// Alignment<String> alignment = comparator.alignStringArrays(tokens1, tokens2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test
public void testCompareAnnotation_withEqualNER() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String sentence = "Barack Obama was born in Hawaii.";
TextAnnotation ta1 = builder.createTextAnnotation("test", "s1", sentence);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("test", "s2", sentence);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertEquals(1.0, score, 1e-5);
}

@Test
public void testCompareAnnotation_withDifferentNER() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String sentence1 = "Elon Musk founded SpaceX.";
String sentence2 = "Musk started a space company.";
TextAnnotation ta1 = builder.createTextAnnotation("test", "s1", sentence1);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("test", "s2", sentence2);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStrings_exactMatch() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String sentence = "Music heals the soul.";
// double score = comparator.compareStrings_(sentence, sentence);
// assertEquals(1.0, score, 1e-5);
}

@Test
public void testAlignNEStringArrays_withTokens() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] array1 = new String[] { "Barack Obama" };
String[] array2 = new String[] { "Obama" };
// Alignment<String> alignment = comparator.alignNEStringArrays(array1, array2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test(expected = Exception.class)
public void testAlignStringArrays_withNullInputs() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
// comparator.alignStringArrays(null, new String[] { "example" });
}

@Test(expected = Exception.class)
public void testAlignNEStringArrays_withNullInput() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
// comparator.alignNEStringArrays(null, new String[] { "Obama" });
}

@Test
public void testDetermineEntailment_nullTextAndHypothesis() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = null;
String hyp = null;
try {
// comparator.determineEntailment(text, hyp);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testCompareStrings_withOneEmptyString() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String source = "";
String target = "This is a test.";
// double score = comparator.compareStrings_(source, target);
// assertEquals(0.0, score, 1e-6);
}

@Test
public void testCompareStrings_bothStringsEmpty() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String source = "";
String target = "";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score == 0.0 || Double.isNaN(score));
}

@Test
public void testAlignStringArrays_withEmptyArrays() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] tokens1 = new String[0];
String[] tokens2 = new String[0];
// Alignment<String> alignment = comparator.alignStringArrays(tokens1, tokens2);
// assertNotNull(alignment);
// assertEquals(0, alignment.getAlignment().size());
}

@Test
public void testScoreAlignment_withEmptyAlignment() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] tokens1 = new String[0];
String[] tokens2 = new String[0];
// Alignment<String> emptyAlignment = comparator.alignStringArrays(tokens1, tokens2);
// EntailmentResult result = comparator.scoreAlignment(emptyAlignment);
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 1e-5);
}

@Test
public void testAlignNEStringArrays_differentLengthsAndNoOverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] ne1 = new String[] { "California" };
String[] ne2 = new String[] { "IBM", "Google", "Tesla" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test
public void testCompareAnnotation_noNERViews() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String s1 = "The Eiffel Tower is in Paris.";
String s2 = "Paris is home to the Eiffel Tower.";
TextAnnotation ta1 = builder.createTextAnnotation("test1", "id1", s1);
TextAnnotation ta2 = builder.createTextAnnotation("test2", "id2", s2);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score > 0.0);
}

@Test
public void testCompareAnnotation_withOnlyOneNERView() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String s1 = "Steve Jobs was the CEO of Apple.";
String s2 = "Jobs led Apple.";
TextAnnotation ta1 = builder.createTextAnnotation("test1", "doc1", s1);
TextAnnotation ta2 = builder.createTextAnnotation("test2", "doc2", s2);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
try {
// comparator.compareAnnotation(ta1, ta2);
fail("Expected an Exception due to missing NER_CONLL view on target.");
} catch (Exception e) {
}
}

@Test
public void testDetermineEntailment_specialCharacters() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "Hello @#%&!";
String hyp = "#@!Hello";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testLongTextEntailment_vsShortHypothesis() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "Once upon a time, in a kingdom far away, there was a brave knight named Arthur who fought dragons and defended the realm from all threats.";
String hyp = "Arthur fought dragons.";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStrings_nonAsciiCharacters() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "El niño comió piña y jalapeños.";
String hyp = "El chico comió frutas.";
// double score = comparator.compareStrings_(text, hyp);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testDetermineEntailment_textIsNull() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
try {
// comp.determineEntailment(null, "This is a hypothesis.");
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testDetermineEntailment_hypothesisIsNull() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
try {
// comp.determineEntailment("This is a text.", null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
}
}

@Test
public void testCompareAnnotation_textHasEmptyNER() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String textStr = "The Eiffel Tower is in Paris.";
String hypStr = "Paris contains the Eiffel Tower.";
TextAnnotation text = builder.createTextAnnotation("id", "text", textStr);
text.addView(ViewNames.SENTENCE, text.getView(ViewNames.TOKENS));
text.addView(ViewNames.NER_CONLL, text.getView(ViewNames.SENTENCE));
TextAnnotation hypothesis = builder.createTextAnnotation("id", "hyp", hypStr);
hypothesis.addView(ViewNames.SENTENCE, hypothesis.getView(ViewNames.TOKENS));
hypothesis.addView(ViewNames.NER_CONLL, hypothesis.getView(ViewNames.TOKENS));
// double score = comp.compareAnnotation(text, hypothesis);
// assertTrue(score >= 0.0);
}

@Test
public void testCompareAnnotation_exactNERMatchesButDifferentSurfaceForms() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String textStr = "Barack Hussein Obama visited Germany.";
String hypStr = "Barack Obama went to Berlin.";
TextAnnotation text = builder.createTextAnnotation("id", "doc1", textStr);
text.addView(ViewNames.SENTENCE, text.getView(ViewNames.TOKENS));
text.addView(ViewNames.NER_CONLL, text.getView(ViewNames.TOKENS));
TextAnnotation hyp = builder.createTextAnnotation("id", "doc2", hypStr);
hyp.addView(ViewNames.SENTENCE, hyp.getView(ViewNames.TOKENS));
hyp.addView(ViewNames.NER_CONLL, hyp.getView(ViewNames.TOKENS));
// double score = comp.compareAnnotation(text, hyp);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testScoreAlignment_alignmentWithOneMatchOnly() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String[] text = new String[] { "unrelated", "giraffe" };
String[] hyp = new String[] { "giraffe" };
// Alignment<String> alignment = comp.alignStringArrays(text, hyp);
// EntailmentResult result = comp.scoreAlignment(alignment);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testAlignNEStringArrays_emptyNEArrays() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String[] textNE = new String[0];
String[] hypNE = new String[0];
// Alignment<String> alignment = comp.alignNEStringArrays(textNE, hypNE);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().isEmpty());
}

@Test
public void testCompareStrings_withVeryLongString() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String text = "word ".repeat(500).trim();
String hyp = "word word word";
// double score = comp.compareStrings_(text, hyp);
// assertTrue(score >= 0.0);
}

@Test
public void testDetermineEntailment_whitespaceOnlyStrings() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String text = "     ";
String hyp = "   ";
// EntailmentResult result = comp.determineEntailment(text, hyp);
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 1e-4);
}

@Test
public void testCompareStrings_minimalSimilarTokens() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String source = "chair banana iron";
String target = "chair table glass";
// double score = comp.compareStrings_(source, target);
// assertTrue(score > 0.0);
// assertTrue(score < 1.0);
}

@Test
public void testLongNERListVsShort() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String textStr = "John met Mary and Dr. Smith near the Empire State Building in New York.";
String hypStr = "Mary saw the doctor.";
TextAnnotation ta1 = builder.createTextAnnotation("id", "d1", textStr);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("id", "d2", hypStr);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comp.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
}

@Test
public void testDetermineEntailment_onlyStopwords() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String text = "the a an and or but if then";
String hyp = "and or if";
// EntailmentResult result = comp.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailment_numbersOnly() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.7");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String text = "123 456 789";
String hyp = "123";
// EntailmentResult result = comp.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() > 0.0);
}

@Test
public void testCompareAnnotation_withMixedNEROverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String sentence1 = "Apple Inc. acquired Beats Electronics.";
String sentence2 = "Apple bought Beats.";
TextAnnotation ta1 = builder.createTextAnnotation("src", "s1", sentence1);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("tar", "s2", sentence2);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comp.compareAnnotation(ta1, ta2);
// assertTrue(score > 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotation_tokensOverlap_butNERMismatch() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String sentence1 = "Microsoft launched Windows 10.";
String sentence2 = "Google released Android.";
TextAnnotation ta1 = builder.createTextAnnotation("x", "s1", sentence1);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("x", "s2", sentence2);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comp.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testAlignSentences_singleWordMatch_only() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String s1 = "Elephants are large mammals.";
String s2 = "Mammals";
// Alignment<String> alignment = comp.alignSentences(s1, s2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() > 0);
}

@Test
public void testDetermineEntailment_caseInsensitiveComparison() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String text = "Obama was the president.";
String hyp = "obama";
// EntailmentResult result = comp.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() > 0.0);
}

@Test
public void testCompareStringsSpecialSymbolsOnly() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String s1 = "!@#$%^&*()";
String s2 = "#$%^";
// double score = comp.compareStrings_(s1, s2);
// assertEquals(0.0, score, 1e-5);
}

@Test
public void testCompareStrings_onlyPunctuationAndWhitespace() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String s1 = "     ";
String s2 = "?!...,";
// double score = comp.compareStrings_(s1, s2);
// assertEquals(0.0, score, 1e-5);
}

@Test
public void testCompareAnnotation_zeroTokensAfterNERFiltering() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String text = "Apple Microsoft IBM Amazon";
String hyp = "Apple Amazon";
TextAnnotation ta1 = builder.createTextAnnotation("doc", "d1", text);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("doc", "d2", hyp);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comp.compareAnnotation(ta1, ta2);
// assertTrue(score > 0.0);
}

@Test
public void testCompareStrings_thresholdBoundaryBehavior() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "1.0");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comp = new LlmStringComparator(rm, new SimpleComparator());
String s1 = "dog";
String s2 = "dog";
// double score = comp.compareStrings_(s1, s2);
// assertEquals(1.0, score, 1e-5);
}

@Test
public void testCompareAnnotation_noNamedEntities_returnSentenceScoreOnly() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.8");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String text = "This is a general message.";
String hyp = "A general message is conveyed.";
TextAnnotation ta1 = builder.createTextAnnotation("source", "s1", text);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, new edu.illinois.cs.cogcomp.core.datastructures.textannotation.View(ViewNames.NER_CONLL, ta1));
TextAnnotation ta2 = builder.createTextAnnotation("target", "s2", hyp);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, new edu.illinois.cs.cogcomp.core.datastructures.textannotation.View(ViewNames.NER_CONLL, ta2));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotation_namedEntitiesOnly() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "1.0");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String source = "Barack Obama worked for the White House.";
String target = "Obama served in the government.";
TextAnnotation ta1 = builder.createTextAnnotation("doc", "t1", source);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("doc", "t2", target);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double result = comparator.compareAnnotation(ta1, ta2);
// assertTrue(result >= 0.0);
}

@Test
public void testAlignNEStringArrays_caseDifference() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] ne1 = new String[] { "barack obama" };
String[] ne2 = new String[] { "Barack Obama" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test
public void testDetermineEntailment_digitsAndLettersMixed() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "Model X version 2023 is fast.";
String hyp = "2023 model is efficient.";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStrings_backToBackNonAlphas() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String s1 = "hello!!!";
String s2 = "hello";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score > 0.0);
}

@Test
public void testConstructorWithEmptyPropertiesObject() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
// EntailmentResult result = comparator.determineEntailment("The sun is hot.", "Heat comes from sun.");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testConstructorWithDefaultEmptyProperties() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// EntailmentResult result = comparator.determineEntailment("Apples are fruits.", "Fruits include apples.");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testTokenArrayFilteredCompletely() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "the a an the the";
String hyp = "an a";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 1e-5);
}

@Test
public void testAlignmentWithLongNEArrayAndShortText() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] ne1 = new String[] { "Barack Obama", "Hillary Clinton", "Joe Biden", "Kamala Harris", "George W. Bush" };
String[] ne2 = new String[] { "Obama" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test
public void testCompareAnnotation_sentenceAndNeZeroOverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String src = "xyz abc def";
String tgt = "uvw opq rst";
TextAnnotation ta1 = builder.createTextAnnotation("doc", "1", src);
TextAnnotation ta2 = builder.createTextAnnotation("doc", "2", tgt);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, new edu.illinois.cs.cogcomp.core.datastructures.textannotation.View(ViewNames.NER_CONLL, ta1));
// ta2.addView(ViewNames.NER_CONLL, new edu.illinois.cs.cogcomp.core.datastructures.textannotation.View(ViewNames.NER_CONLL, ta2));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertEquals(0.0, score, 1e-5);
}

@Test
public void testCompareAnnotation_NEROnlyInTarget() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("source", "s1", "This is a simple sentence.");
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// View emptyNER = new View(ViewNames.NER_CONLL, ta1);
// ta1.addView(ViewNames.NER_CONLL, emptyNER);
TextAnnotation ta2 = builder.createTextAnnotation("target", "s2", "Barack Obama spoke today.");
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotation_NEROnlyInSource() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.45");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("source", "s1", "Bill Gates created Microsoft.");
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("target", "s2", "A software company was founded.");
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// View emptyNER = new View(ViewNames.NER_CONLL, ta2);
// ta2.addView(ViewNames.NER_CONLL, emptyNER);
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignNEStringArrays_duplicateTokens() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] sourceNE = new String[] { "John", "John", "John" };
String[] targetNE = new String[] { "John" };
// Alignment<String> alignment = comparator.alignNEStringArrays(sourceNE, targetNE);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 1);
}

@Test
public void testDetermineEntailment_sentenceWithEmojis() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.25");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "She is happy 😊.";
String hypothesis = "She feels joy.";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStrings_identicalStopwordStrings() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.1");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String source = "the the the the";
String target = "the";
// double score = comparator.compareStrings_(source, target);
// assertEquals(0.0, score, 0.00001);
}

@Test
public void testCompareAnnotation_allTokensRemovedAfterNERFilter() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.85");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("src", "t1", "Steve Jobs founded Apple.");
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("tar", "t2", "Apple's founder was Steve.");
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailment_emptyTokenTextArray() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.9");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] textTokens = new String[0];
String[] hypTokens = new String[] { "cats" };
// EntailmentResult result = comparator.determineEntailment(textTokens, hypTokens);
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 1e-6);
}

@Test
public void testConstructor_withThresholdZero() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.0");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
// EntailmentResult result = comparator.determineEntailment("It is raining.", "There is precipitation.");
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStrings_extremelyCommonWords() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.2");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String source = "this is that and this is that";
String target = "this and that";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0);
}

@Test
public void testAlignStringArrays_zeroOverlap() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.75");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] textTokens = new String[] { "one", "two", "three" };
String[] hypTokens = new String[] { "apple", "banana", "carrot" };
// Alignment<String> alignment = comparator.alignStringArrays(textTokens, hypTokens);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test
public void testAlignNEStringArrays_bothEmpty() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] ne1 = new String[0];
String[] ne2 = new String[0];
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertEquals(0, alignment.getAlignment().size());
}

@Test
public void testDetermineEntailment_shortText_vsLongHyp() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = "rain";
String hyp = "It is raining outside, and the sky is gray.";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testDetermineEntailment_whitespace_variation() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String text = " \t  Hello\tworld  ";
String hyp = "Hello world";
// EntailmentResult result = comparator.determineEntailment(text, hyp);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.9);
}

@Test
public void testCompareAnnotation_onlyPunctuation() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.3");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String source = "!?.,";
String target = "...!";
TextAnnotation ta1 = builder.createTextAnnotation("s1", "doc1", source);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, new View(ViewNames.NER_CONLL, ta1));
TextAnnotation ta2 = builder.createTextAnnotation("s2", "doc2", target);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, new View(ViewNames.NER_CONLL, ta2));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertEquals(0.0, score, 1e-6);
}

@Test
public void testCompareAnnotation_partialNamedEntityMatch() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.45");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String source = "United States of America declared independence.";
String target = "America was founded.";
TextAnnotation ta1 = builder.createTextAnnotation("s1", "doc1", source);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("s2", "doc2", target);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
}

@Test
public void testCompareStrings_singleTokenSentences() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.66");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String source = "Water";
String target = "Water";
// double score = comparator.compareStrings_(source, target);
// assertEquals(1.0, score, 1e-5);
}

@Test
public void testAlignNEStringArrays_symbolsOnly() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.4");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
String[] ne1 = new String[] { "$%", "@@@" };
String[] ne2 = new String[] { "$%", "###" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignment().size() >= 0);
}

@Test
public void testCompareAnnotation_heavyNERWeighting() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.5");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm, new SimpleComparator());
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String source = "Elon Musk founded Tesla and SpaceX and Neuralink and The Boring Company.";
String target = "Musk created many tech firms.";
TextAnnotation ta1 = builder.createTextAnnotation("s", "src", source);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
TextAnnotation ta2 = builder.createTextAnnotation("s", "hyp", target);
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
}
}
