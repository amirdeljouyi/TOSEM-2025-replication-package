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

public class LlmStringComparator_llmsuite_3_GPTLLMTest {

@Test
public void testDetermineEntailmentBasic() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "The dog chased the cat.";
String hypothesis = "A dog chased a cat.";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailmentIdenticalStrings() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String sentence = "Apples are fruits.";
// EntailmentResult result = comparator.determineEntailment(sentence, sentence);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.9);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailmentWithEmptyInput() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "";
String hypothesis = "";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testAlignSentences() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "Cats sleep on mats.";
String hypothesis = "Felines sleep on rugs.";
// Alignment<String> alignment = comparator.alignSentences(text, hypothesis);
// assertNotNull(alignment);
// assertNotNull(alignment.getAlignments());
}

@Test
public void testAlignStringArraysBasic() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] text = new String[] { "The", "sky", "is", "blue" };
String[] hyp = new String[] { "Sky", "is", "blue" };
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertNotNull(alignment.getAlignments());
}

@Test
public void testAlignStringArraysEmpty() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] text = new String[] {};
String[] hyp = new String[] {};
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignments().isEmpty());
}

@Test
public void testAlignNEStringArrays() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] ne1 = new String[] { "Barack", "Obama" };
String[] ne2 = new String[] { "President", "Obama" };
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertNotNull(alignment.getAlignments());
}

@Test
public void testCompareStringsTypical() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "It is snowing heavily.";
String hypothesis = "There is heavy snowfall.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsCompletelyDifferent() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "I love pizza.";
String hypothesis = "The sun is very hot.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testScoreAlignmentSinglePair() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// Alignment<String> alignment = new Alignment<>();
// alignment.addAlignment(0, 0);
// EntailmentResult result = comparator.scoreAlignment(alignment);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareAnnotationWithNERView() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("source", "id1", "Barack Obama was president of the USA.");
// TextAnnotation ta2 = builder.createTextAnnotation("source", "id2", "Obama led the United States.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationNoNERView() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "ta1", "The Eiffel Tower is in Paris.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "ta2", "Paris has many tourist attractions.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test(expected = Exception.class)
public void testDetermineEntailmentNullInput() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// comparator.determineEntailment(null, null);
}

@Test
public void testConstructorWithCustomResourceManager() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// assertNotNull(comparator);
}

@Test
public void testConstructorWithCustomComparator() throws Exception {
Properties properties = new Properties();
// properties.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.6");
ResourceManager rm = new ResourceManager(properties);
// Comparator<String, EntailmentResult> customComparator = new WordComparator(new SimConfigurator().getConfig(rm));
// LlmStringComparator comparator = new LlmStringComparator(rm, customComparator);
// assertNotNull(comparator);
}

@Test
public void testSingleWordMatch() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "apple";
String hypothesis = "apple";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.9);
}

@Test
public void testSingleWordNoMatch() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "apple";
String hypothesis = "banana";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testWhitespaceOnlyString() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "   ";
String hypothesis = "   ";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test(expected = Exception.class)
public void testAlignStringArraysWithNullArrays() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// comparator.alignStringArrays(null, null);
}

@Test(expected = Exception.class)
public void testAlignNEStringArraysWithNull() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// comparator.alignNEStringArrays(null, new String[] { "entity" });
}

@Test
public void testMismatchedLengthArrays() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] text = new String[] { "The", "moon" };
String[] hyp = new String[] { "Moon", "is", "bright", "tonight" };
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertNotNull(alignment.getAlignments());
}

@Test
public void testCompareAnnotationWithOnlyNEViewOnOneSide() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "Albert Einstein was a physicist.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "Einstein developed relativity.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithPunctuationVariance() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "Hello, world!";
String hypothesis = "Hello world";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testThresholdBehaviorAtMinimum() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "0.0");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "Antarctica is cold.";
String hypothesis = "It's cold in Antarctica.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0);
}

@Test
public void testThresholdBehaviorAtMaximum() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "1.0");
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "Water is wet.";
String hypothesis = "Water is wet.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertEquals(1.0, score, 0.01);
}

@Test
public void testCompareStringsSpecialCharacters() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "The price is $5.99!";
String hypothesis = "It costs 5.99 dollars.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0);
}

@Test
public void testCompareAnnotationIdenticalNERContent() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("set", "1", "Microsoft released Windows 11.");
// TextAnnotation ta2 = builder.createTextAnnotation("set", "2", "Windows 11 was released by Microsoft.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithHtmlContent() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "<p>This is a paragraph.</p>";
String hypothesis = "This is a paragraph.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStringsWithNewlinesAndTabs() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "Java\tis\nawesome.";
String hypothesis = "Java is awesome.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotationWithOnlyNamedEntities() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "Einstein Newton Tesla");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "Newton Einstein Tesla");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationWithEmptyNERViews() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "The quick brown fox.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "A fast dark-colored fox.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// View emptyView1 = new View(ViewNames.NER_CONLL, "dummy", ta1, 1.0);
// View emptyView2 = new View(ViewNames.NER_CONLL, "dummy", ta2, 1.0);
// ta1.addView(ViewNames.NER_CONLL, emptyView1);
// ta2.addView(ViewNames.NER_CONLL, emptyView2);
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationWithNoTokensRemainingAfterNERFiltering() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "IBM IBM IBM");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "IBM IBM IBM");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotationMismatchedSentenceTokenCount() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "ta1", "Only one sentence.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "ta2", "Sentence one. Sentence two.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailmentWithMixedCaseTokens() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "HELLO world";
String hypothesis = "hello WORLD";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailmentWithSpecialSymbols() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "@user #topic !important";
String hypothesis = "user topic important";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test(expected = Exception.class)
public void testDetermineEntailmentWithNullStringArrayInput() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// comparator.determineEntailment((String[]) null, (String[]) null);
}

@Test
public void testAlignmentOfCompletelyDifferentStrings() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] textToks = { "apple", "banana", "cherry" };
String[] hypToks = { "sun", "moon", "stars" };
// Alignment<String> alignment = comparator.alignStringArrays(textToks, hypToks);
// assertNotNull(alignment);
// assertNotNull(alignment.getAlignments());
}

@Test
public void testDetermineEntailmentWithHyphenatedWords() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "E-mail is the modern way to communicate.";
String hypothesis = "Email is used for communication today.";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testCompareStringsWithUnicodeCharacters() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "The café serves crème brûlée.";
String hypothesis = "A cafe offers cream brulee.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotationWithOverlappingNamedEntities() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "United Nations Headquarters is in New York.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "UN HQ is located in NYC.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test(expected = Exception.class)
public void testCompareAnnotationWithNullTextAnnotation() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// comparator.compareAnnotation(null, null);
}

@Test
public void testDetermineEntailmentWithWhitespaceAndControlChars() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "This\tis\na test\u0000!";
String hypothesis = "This is a test!";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStringsWithMultipleRepeatedTokens() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "fire fire fire fire fire";
String hypothesis = "big fire";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignmentWithSingleEmptyHypothesisToken() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] textTokens = new String[] { "The", "earth", "rotates" };
String[] hypothesisTokens = new String[] { "" };
// Alignment<String> alignment = comparator.alignStringArrays(textTokens, hypothesisTokens);
// assertNotNull(alignment);
}

@Test
public void testAlignmentWithSingleEmptyTextToken() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] textTokens = new String[] { "" };
String[] hypothesisTokens = new String[] { "The", "earth" };
// Alignment<String> alignment = comparator.alignStringArrays(textTokens, hypothesisTokens);
// assertNotNull(alignment);
}

@Test
public void testCompareAnnotationWithNERViewButEmptySentenceView() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "Albert Einstein won the Nobel Prize.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "Einstein received a prize.");
// View emptySentenceView1 = new View(ViewNames.SENTENCE, "fake", ta1, 1.0);
// View emptySentenceView2 = new View(ViewNames.SENTENCE, "fake", ta2, 1.0);
// View nerView1 = ta1.getView(ViewNames.TOKENS);
// View nerView2 = ta2.getView(ViewNames.TOKENS);
// ta1.addView(ViewNames.SENTENCE, emptySentenceView1);
// ta2.addView(ViewNames.SENTENCE, emptySentenceView2);
// ta1.addView(ViewNames.NER_CONLL, nerView1);
// ta2.addView(ViewNames.NER_CONLL, nerView2);
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailmentWithStopWordOnlyInput() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "the and but or";
String hypothesis = "and the";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStringsWithNumericQuantities() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "The package weighs 5.5 kilograms.";
String hypothesis = "It weighs five kilograms.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignmentWithLeadingTrailingWhitespace() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "   apple pie  ";
String hypothesis = "apple pie";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStringsWithOnlyDelimiters() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "....,,,";
String hypothesis = ";;;;";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStringsWithEmojiSymbols() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "I love 🍕 and 🍔!";
String hypothesis = "Pizza and burgers are great.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test(expected = Exception.class)
public void testDetermineEntailmentWithNullHypothesis() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "Valid sentence.";
String hypothesis = null;
// comparator.determineEntailment(text, hypothesis);
}

@Test
public void testCompareAnnotationWithSentenceOnlyNoNER() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "This is a test.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "This is not a drill.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
}

@Test
public void testCompareAnnotationWithNERButNoEntities() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "id1", "Running is healthy.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "id2", "Exercising daily helps health.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// View emptyNER1 = new View(ViewNames.NER_CONLL, "ner", ta1, 1.0);
// View emptyNER2 = new View(ViewNames.NER_CONLL, "ner", ta2, 1.0);
// ta1.addView(ViewNames.NER_CONLL, emptyNER1);
// ta2.addView(ViewNames.NER_CONLL, emptyNER2);
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
}

@Test
public void testCompareStringsWithLongSentenceAndReverseOrder() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "one two three four five six seven eight nine ten";
String hypothesis = "ten nine eight seven six five four three two one";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotationWithDuplicateEntities() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("source", "ta1", "Obama Obama Obama.");
// TextAnnotation ta2 = builder.createTextAnnotation("source", "ta2", "Obama Obama.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testConstructorWithMissingThresholdDefaults() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// EntailmentResult result = comparator.determineEntailment("Birds fly.", "Birds can fly.");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testDetermineEntailmentWithStopWordsOnlyHypothesis() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "He is a great leader.";
String hypothesis = "is a the";
// EntailmentResult result = comparator.determineEntailment(text, hypothesis);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStringsWithPunctuationHeavyContent() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "Wait!!! What??? Really?!?!";
String hypothesis = "You are surprised.";
// double score = comparator.compareStrings_(text, hypothesis);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testAlignNEStringArraysWithEmptyInputs() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] ne1 = new String[0];
String[] ne2 = new String[0];
// assertNotNull(comparator.alignNEStringArrays(ne1, ne2));
}

@Test
public void testScoreAlignmentWithNoAlignments() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// EntailmentResult result = comparator.scoreAlignment(new edu.illinois.cs.cogcomp.mrcs.dataStructures.Alignment<>());
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testDetermineEntailmentWithNullTokensInsideArray() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] textTokens = new String[] { "The", null, "dog" };
String[] hypTokens = new String[] { "dog" };
// EntailmentResult result = comparator.determineEntailment(textTokens, hypTokens);
// assertNotNull(result);
}

@Test
public void testCompareAnnotation_ZeroNEROverlap_TriggersSentenceScoreOnly() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("test", "s1", "Barack Obama lives in Washington.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "s2", "Angela Merkel resides in Berlin.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotation_WithOnlyStopWordsAfterNERRemoval() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("t", "1", "Mr. John Smith and Mr. White at the museum.");
// TextAnnotation ta2 = builder.createTextAnnotation("t", "2", "Ms. Jane Doe and Ms. Black lived near it.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
}

@Test
public void testCompareStringsContainsOnlyStopwords() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "to be or not to be";
String hyp = "to be";
// double score = comparator.compareStrings_(text, hyp);
// assertTrue(score >= 0.0);
}

@Test
public void testCompareAnnotationWithOnlyOneSENTENCEView() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
// TextAnnotation ta1 = builder.createTextAnnotation("test", "ta1", "Hello New York.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "ta2", "Goodbye Berlin.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
}

@Test
public void testInitializeWithInvalidThresholdValue() throws Exception {
Properties props = new Properties();
// props.setProperty(SimConfigurator.LLM_ENTAILMENT_THRESHOLD.key, "not_a_number");
// LlmStringComparator comparator = null;
try {
ResourceManager rm = new ResourceManager(props);
// comparator = new LlmStringComparator(rm);
} catch (Exception e) {
assertTrue(e instanceof NumberFormatException || e instanceof IllegalArgumentException);
}
// assertNull(comparator);
}

@Test
public void testCompareAnnotationWithNoMatchedNamedEntities() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
// TextAnnotation ta1 = builder.createTextAnnotation("t", "1", "Chris went to Japan.");
// TextAnnotation ta2 = builder.createTextAnnotation("t", "2", "Linda traveled to Italy.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailmentWithLongSentence() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
StringBuilder longText = new StringBuilder();
StringBuilder longHyp = new StringBuilder();
for (int i = 0; i < 100; i++) {
longText.append("This is a sentence segment ").append(i).append(". ");
}
for (int i = 0; i < 50; i++) {
longHyp.append("sentence segment ").append(i).append(". ");
}
// EntailmentResult result = comparator.determineEntailment(longText.toString(), longHyp.toString());
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0);
}

@Test
public void testAlignStringArraysWithMismatchedCasing() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] textTokens = new String[] { "United", "States", "Of", "America" };
String[] hypTokens = new String[] { "united", "states", "of", "america" };
// Alignment<String> alignment = comparator.alignStringArrays(textTokens, hypTokens);
// assertNotNull(alignment);
}

@Test
public void testCompareStringsWithRepetitiveNamedEntities() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "John is in New York. John works in New York. John loves New York.";
String hyp = "John from New York loves working.";
// double score = comparator.compareStrings_(text, hyp);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotationWithNERViewHavingNonOverlappingNERItems() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// IllinoisTokenizer tokenizer = new IllinoisTokenizer();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
// TextAnnotation ta1 = builder.createTextAnnotation("domain", "id1", "Barack Obama visited England.");
// TextAnnotation ta2 = builder.createTextAnnotation("domain", "id2", "Angela Merkel went to Germany.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareAnnotationWithWhitespaceOnlySentenceAfterNERRemoval() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
// TextAnnotation ta1 = builder.createTextAnnotation("test", "ta1", "Obama Obama Obama");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "ta2", "Obama");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testCompareStringsUsingSpecialCharacters() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "C@t$ & D0g$ play!!";
String hyp = "Cats and dogs are playful.";
// double score = comparator.compareStrings_(text, hyp);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test(expected = Exception.class)
public void testCompareAnnotationWithNullNERView() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
// TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
// TextAnnotation ta1 = builder.createTextAnnotation("test", "ta1", "Google is a tech company.");
// TextAnnotation ta2 = builder.createTextAnnotation("test", "ta2", "Amazon sells books.");
// ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
// ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
// comparator.compareAnnotation(ta1, ta2);
}

@Test
public void testCompareStringsWithRepeatedStopwordsOnly() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String text = "the the the the the";
String hyp = "the the";
// double score = comparator.compareStrings_(text, hyp);
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testConstructorWithMissingThresholdKey() throws Exception {
Properties properties = new Properties();
ResourceManager rm = new ResourceManager(properties);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("Coffee is hot.", "Hot coffee is served.");
// assertTrue(score >= 0.0 && score <= 1.0);
}

@Test
public void testDetermineEntailmentWithEmptyTokenArrays() throws Exception {
// LlmStringComparator comparator = new LlmStringComparator();
String[] textToks = new String[0];
String[] hypToks = new String[0];
// EntailmentResult result = comparator.determineEntailment(textToks, hypToks);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}
}
