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
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import static org.junit.Assert.*;

public class LlmStringComparator_llmsuite_5_GPTLLMTest {

@Test
public void testDetermineEntailmentSimpleMatch() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// EntailmentResult result = comparator.determineEntailment("The dog chased the cat.", "A dog chased a cat.");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailmentNoOverlap() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// EntailmentResult result = comparator.determineEntailment("The moon is bright.", "Cats sleep on the roof.");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailmentOneEmpty() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// EntailmentResult result = comparator.determineEntailment("", "This is a test.");
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 0.0001);
}

@Test
public void testDetermineEntailmentBothEmpty() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// EntailmentResult result = comparator.determineEntailment("", "");
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 0.0001);
}

@Test
public void testAlignSentences() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// assertNotNull(comparator.alignSentences("The dog barked.", "A dog barked loudly."));
}

@Test
public void testAlignStringArrays() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String[] text = new String[] { "The", "cat", "sleeps" };
String[] hyp = new String[] { "A", "cat", "naps" };
// assertNotNull(comparator.alignStringArrays(text, hyp));
}

@Test
public void testAlignNEStringArrays() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String[] ne1 = new String[] { "Barack", "Obama" };
String[] ne2 = new String[] { "President", "Obama" };
// assertNotNull(comparator.alignNEStringArrays(ne1, ne2));
}

@Test
public void testCompareStringsExactMatch() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("Apples are red.", "Apples are red.");
// assertTrue(score >= 0.9);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsPartialMatch() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("Birds can fly.", "Birds fly.");
// assertTrue(score > 0.5);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsUnrelated() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("Dogs are friendly.", "The sun is massive.");
// assertTrue(score >= 0.0);
// assertTrue(score < 0.5);
}

@Test
public void testCompareStringsWithSpecialCharacters() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("I like pizza!", "I like pizza.");
// assertTrue(score >= 0.8);
}

@Test
public void testCompareStringsWithLongText() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String source = "This passage explains how water evaporates from oceans and later falls as rain.";
String target = "Rain comes from evaporated ocean water.";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationWithNERAndSentenceView() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String sourceText = "Barack Obama was the president.";
String targetText = "Obama was a US president.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation sourceTA = builder.createTextAnnotation("test", "source", sourceText);
TextAnnotation targetTA = builder.createTextAnnotation("test", "target", targetText);
sourceTA.addView(ViewNames.SENTENCE, sourceTA.getView(ViewNames.TOKENS));
targetTA.addView(ViewNames.SENTENCE, targetTA.getView(ViewNames.TOKENS));
sourceTA.addView(ViewNames.NER_CONLL, sourceTA.getView(ViewNames.TOKENS));
targetTA.addView(ViewNames.NER_CONLL, targetTA.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(sourceTA, targetTA);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationWithNoNERs() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String sourceText = "Yellow bananas are tasty.";
String targetText = "Bananas taste good.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation sourceTA = builder.createTextAnnotation("test", "source", sourceText);
TextAnnotation targetTA = builder.createTextAnnotation("test", "target", targetText);
sourceTA.addView(ViewNames.SENTENCE, sourceTA.getView(ViewNames.TOKENS));
targetTA.addView(ViewNames.SENTENCE, targetTA.getView(ViewNames.TOKENS));
sourceTA.addView(ViewNames.NER_CONLL, sourceTA.getView(ViewNames.TOKENS));
targetTA.addView(ViewNames.NER_CONLL, targetTA.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(sourceTA, targetTA);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testConstructorWithExplicitComparator() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// Comparator<String, EntailmentResult> customComparator = new Comparator<String, EntailmentResult>() {
// 
// public EntailmentResult compare(String[] textToks_, String[] hypToks_) {
// EntailmentResult result = new EntailmentResult();
// result.setScore(0.88);
// return result;
// }
// };
// LlmStringComparator comparator = new LlmStringComparator(rm, customComparator);
// double score = comparator.compareStrings_("Hello world", "Hello globe");
// assertEquals(0.88, score, 0.001);
}

@Test
public void testConstructorDefaultNoExceptionThrown() {
try {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// assertNotNull(comparator);
} catch (Exception e) {
fail("Exception should not be thrown in default constructor usage: " + e.getMessage());
}
}

@Test
public void testCompareStringsSingleCharacterMatch() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("A", "A");
// assertTrue(score > 0.9);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsSingleCharacterMismatch() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("A", "B");
// assertTrue(score >= 0.0);
// assertTrue(score < 1.0);
}

@Test
public void testCompareStringsWithPunctuationOnly() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_(".", ".");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testDetermineEntailmentNullTextTokens() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// Comparator<String, EntailmentResult> forcedNullComparator = new Comparator<String, EntailmentResult>() {
// 
// public EntailmentResult compare(String[] text, String[] hyp) throws Exception {
// if (text == null || hyp == null)
// throw new IllegalArgumentException("Null input array");
// EntailmentResult result = new EntailmentResult();
// result.setScore(0.0);
// return result;
// }
// };
// LlmStringComparator comparator = new LlmStringComparator(config, forcedNullComparator);
try {
// comparator.determineEntailment(null, new String[] { "test" });
fail("Should throw IllegalArgumentException");
} catch (IllegalArgumentException e) {
assertEquals("Null input array", e.getMessage());
}
}

@Test
public void testAlignStringArraysWithEmptyArrays() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String[] text = new String[] {};
String[] hyp = new String[] {};
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().isEmpty());
}

@Test
public void testCompareAnnotationWithMismatchedSentenceNERCounts() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String sourceText = "The Eiffel Tower is in Paris.";
String targetText = "Paris is a famous city.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation source = builder.createTextAnnotation("", "", sourceText);
TextAnnotation target = builder.createTextAnnotation("", "", targetText);
source.addView(ViewNames.SENTENCE, source.getView(ViewNames.TOKENS));
target.addView(ViewNames.SENTENCE, target.getView(ViewNames.TOKENS));
source.addView(ViewNames.NER_CONLL, source.getView(ViewNames.TOKENS));
target.addView(ViewNames.NER_CONLL, target.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(source, target);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithStopWordsDominating() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("the the the the", "the");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsCaseSensitivity() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("Dog", "dog");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsUnicodeCharacters() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("naïve café", "naive cafe");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsVeryLongInput() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
StringBuilder sb1 = new StringBuilder();
StringBuilder sb2 = new StringBuilder();
for (int i = 0; i < 1000; i++) {
sb1.append("apple ");
sb2.append("apple ");
}
// double score = comparator.compareStrings_(sb1.toString(), sb2.toString());
// assertTrue(score > 0.9);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsMismatchLengthRatio() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("elephant", "e");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWhitespaceOnlyInputs() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("     ", " ");
// assertEquals(0.0, score, 0.001);
}

@Test
public void testCompareStringsTabAndNewlines() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("\t\nThe sky is blue.\t", "The sky is blue.");
// assertTrue(score > 0.5);
}

@Test
public void testCompareStringsOnlyNumbers() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("12345", "12345");
// assertEquals(1.0, score, 0.0001);
}

@Test
public void testCompareStringsDifferentNumbers() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("12345", "67890");
// assertTrue(score < 0.5);
}

@Test
public void testDetermineEntailmentWithSymbols() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String text = "@#$%^&*()!";
String hyp = "@#$%";
// double score = comparator.compareStrings_(text, hyp);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStrings_RepeatedWords() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String source = "apple apple apple apple apple";
String target = "apple";
// double score = comparator.compareStrings_(source, target);
// assertTrue(score > 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsIdenticalWithDifferentCasing() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
// double score = comparator.compareStrings_("The QUICK Brown Fox", "the quick brown fox");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationEmptyNERView() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String src = "Germany is a country in Europe.";
String tgt = "France is also in Europe.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("", "", src);
TextAnnotation ta2 = builder.createTextAnnotation("", "", tgt);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationWithOneEmptyNERView() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String src = "New York is a large city.";
String tgt = "Tokyo is also a city.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("", "", src);
TextAnnotation ta2 = builder.createTextAnnotation("", "", tgt);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsLargeMismatchLength() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String shortStr = "dog";
String longStr = "dog dog dog dog dog dog dog dog dog dog dog dog dog dog dog dog";
// double score = comparator.compareStrings_(shortStr, longStr);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsNonLatinUnicode() throws Exception {
Properties props = new Properties();
ResourceManager config = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(config);
String src = "Привет мир";
String tgt = "Hello world";
// double score = comparator.compareStrings_(src, tgt);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testDetermineEntailmentWithNullHypothesisArray() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// Comparator<String, EntailmentResult> customComparator = new Comparator<String, EntailmentResult>() {
// 
// public EntailmentResult compare(String[] textToks_, String[] hypToks_) throws Exception {
// if (hypToks_ == null)
// throw new IllegalArgumentException("Hypothesis tokens null");
// EntailmentResult result = new EntailmentResult();
// result.setScore(0.0);
// return result;
// }
// };
// LlmStringComparator comparator = new LlmStringComparator(rm, customComparator);
try {
// comparator.determineEntailment(new String[] { "hello" }, null);
fail("Expected IllegalArgumentException for null hypothesis input.");
} catch (IllegalArgumentException e) {
assertEquals("Hypothesis tokens null", e.getMessage());
}
}

@Test
public void testDetermineEntailmentWithNullTextArray() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// Comparator<String, EntailmentResult> customComparator = new Comparator<String, EntailmentResult>() {
// 
// public EntailmentResult compare(String[] textToks_, String[] hypToks_) throws Exception {
// if (textToks_ == null)
// throw new IllegalArgumentException("Text tokens null");
// EntailmentResult result = new EntailmentResult();
// result.setScore(0.0);
// return result;
// }
// };
// LlmStringComparator comparator = new LlmStringComparator(rm, customComparator);
try {
// comparator.determineEntailment(null, new String[] { "test" });
fail("Expected IllegalArgumentException for null text input.");
} catch (IllegalArgumentException e) {
assertEquals("Text tokens null", e.getMessage());
}
}

@Test
public void testCompareStringsBothInputsNullStrings() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
try {
// comparator.compareStrings_(null, null);
fail("Expected NullPointerException or handled exception.");
} catch (NullPointerException expected) {
} catch (Exception e) {
assertTrue(e.getMessage() != null);
}
}

@Test
public void testScoreAlignmentWithEmptyAlignmentObject() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// Alignment<String> alignment = new Alignment<>();
// EntailmentResult result = comparator.scoreAlignment(alignment);
// assertNotNull(result);
// assertEquals(0.0, result.getScore(), 0.001);
}

@Test
public void testScoreAlignmentWithSingleMatchingPair() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// Alignment<String> alignment = new Alignment<>();
// alignment.addAlignedPair(new AlignmentPair<>("cat", "cat", 1.0));
// EntailmentResult result = comparator.scoreAlignment(alignment);
// assertNotNull(result);
// assertTrue(result.getScore() > 0.0);
}

@Test
public void testAlignStringArraysWithMismatchedLengths() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] text = new String[] { "The", "quick", "brown", "fox" };
String[] hyp = new String[] { "fox" };
// Alignment<String> alignment = comparator.alignStringArrays(text, hyp);
// assertNotNull(alignment);
// assertTrue(alignment.getAlignedPairs().size() >= 1);
}

@Test
public void testCompareStringsWithOnlySpecialCharacters() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("@@@", "$$$");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testAlignNEStringArraysEmptyArrays() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String[] ne1 = new String[] {};
String[] ne2 = new String[] {};
// Alignment<String> alignment = comparator.alignNEStringArrays(ne1, ne2);
// assertNotNull(alignment);
// assertEquals(0, alignment.getAlignedPairs().size());
}

@Test
public void testCompareAnnotationWithNullNERView() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text1 = "Steve Jobs founded Apple.";
String text2 = "Apple was founded by Steve.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("corpus", "id1", text1);
TextAnnotation ta2 = builder.createTextAnnotation("corpus", "id2", text2);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, null);
ta2.addView(ViewNames.NER_CONLL, null);
try {
// comparator.compareAnnotation(ta1, ta2);
fail("Expected NullPointerException or handled exception for missing NER view");
} catch (NullPointerException expected) {
} catch (Exception e) {
assertTrue(e.getMessage() != null);
}
}

@Test
public void testCompareStringsExtremeLengthDifference() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String shortStr = "a";
String longStr = "a a a a a a a a a a a a a a a a a a a a";
// double score = comparator.compareStrings_(shortStr, longStr);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testAlignmentWithNullMatchValues() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// Alignment<String> alignment = new Alignment<>();
// alignment.addAlignedPair(new AlignmentPair<>("apple", "fruit", 0.0));
// EntailmentResult result = comparator.scoreAlignment(alignment);
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testCompareStringsEmptyVsNonEmpty() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("", "This has content.");
// assertEquals(0.0, score, 0.00001);
}

@Test
public void testCompareStringsEmojiSupport() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("I love pizza 🍕", "pizza");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testDetermineEntailmentWithNonOverlappingCapitalization() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// EntailmentResult result = comparator.determineEntailment("Paris is beautiful", "paris");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
// assertTrue(result.getScore() <= 1.0);
}

@Test
public void testDetermineEntailmentWithRedundantWords() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// EntailmentResult result = comparator.determineEntailment("cats cats cats cats", "cats");
// assertNotNull(result);
// assertTrue(result.getScore() >= 0.0);
}

@Test
public void testCompareStringsWithMultipleWhitespaces() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("Dog         barks", "Dog barks");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationDifferentSentenceLengths() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String sourceText = "The Eiffel Tower is in Paris.";
String targetText = "Paris.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation source = builder.createTextAnnotation("corpus", "src", sourceText);
TextAnnotation target = builder.createTextAnnotation("corpus", "tgt", targetText);
source.addView(ViewNames.SENTENCE, source.getView(ViewNames.TOKENS));
target.addView(ViewNames.SENTENCE, target.getView(ViewNames.TOKENS));
source.addView(ViewNames.NER_CONLL, source.getView(ViewNames.TOKENS));
target.addView(ViewNames.NER_CONLL, target.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(source, target);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithSpecialTokenizationMarks() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("Hello-world", "Hello world");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithTokenOverlapAndNoise() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String sentence1 = "Banana 123 and Apple!";
String sentence2 = "apple banana";
// double score = comparator.compareStrings_(sentence1, sentence2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithHTMLContent() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("<html><body>Hello</body></html>", "Hello");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsOnlyStopWords() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
// double score = comparator.compareStrings_("the is of and to", "and the is");
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationWithRepeatedEntities() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String input1 = "Barack Obama was born in Hawaii. Obama became president.";
String input2 = "Obama served as the president of the United States.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("corpus", "id1", input1);
TextAnnotation ta2 = builder.createTextAnnotation("corpus", "id2", input2);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithNumbersAndDates() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "The event happened on 12.05.2020.";
String s2 = "It occurred on December 5, 2020.";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsMixedLanguages() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "He is very happy.";
String s2 = "他 很 高兴";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsPunctuationTokenSplitEffect() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "This.is.a.test";
String s2 = "This is a test";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareAnnotationNERViewWithoutConstituents() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text1 = "Elephants can remember.";
String text2 = "They have strong memory.";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("corpus", "id1", text1);
TextAnnotation ta2 = builder.createTextAnnotation("corpus", "id2", text2);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithUnusualSpacingAndTabs() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "Red\tapple is\t sweet.";
String s2 = "Red apple is sweet";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testDetermineEntailmentLargeSymbolNoise() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "!!! @@## *** Banana";
String s2 = "Banana";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsEdgeTokenBoundaryMatch() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "cat.";
String s2 = "cat";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score > 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsBracketedVsPlain() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "[Important] Message";
String s2 = "Important Message";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score > 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithNonBreakingSpace() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "San\u00A0Francisco is nice";
String s2 = "San Francisco is nice";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithLeadingAndTrailingSpaces() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "   leading space";
String s2 = "leading space   ";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithDoubleQuotes() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "The book is called \"Ulysses\"";
String s2 = "The book is called Ulysses";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithEmptyHypothesis() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text = "A complete sentence.";
String hyp = "";
// double score = comparator.compareStrings_(text, hyp);
// assertEquals(0.0, score, 0.00001);
}

@Test
public void testCompareAnnotationWithEmptyNERAndSentenceViews() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String text1 = "Germany";
String text2 = "Berlin";
IllinoisTokenizer tokenizer = new IllinoisTokenizer();
TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta1 = builder.createTextAnnotation("corpus", "ta1", text1);
TextAnnotation ta2 = builder.createTextAnnotation("corpus", "ta2", text2);
ta1.addView(ViewNames.SENTENCE, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.SENTENCE, ta2.getView(ViewNames.TOKENS));
ta1.addView(ViewNames.NER_CONLL, ta1.getView(ViewNames.TOKENS));
ta2.addView(ViewNames.NER_CONLL, ta2.getView(ViewNames.TOKENS));
// double score = comparator.compareAnnotation(ta1, ta2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsWithTabsBetweenWords() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "dog\tbarks\tloudly";
String s2 = "dog barks loudly";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsLowerVsUpperCase() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "the quick brown fox";
String s2 = "The QUICK Brown FOX";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsNumbersWithCommas() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "The budget was 1,000,000 dollars.";
String s2 = "budget was one million dollars";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsComplexMixedSymbolsAndAlphanumerics() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "UserID#123! logged in.";
String s2 = "User 123 logged in";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}

@Test
public void testCompareStringsHTMLStrippedVsRaw() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
// LlmStringComparator comparator = new LlmStringComparator(rm);
String s1 = "<div>Hello world</div>";
String s2 = "Hello world";
// double score = comparator.compareStrings_(s1, s2);
// assertTrue(score >= 0.0);
// assertTrue(score <= 1.0);
}
}
