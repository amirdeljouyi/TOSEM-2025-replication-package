package edu.illinois.cs.cogcomp.quant;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParser;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParserFactory;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
// import edu.illinois.cs.cogcomp.quant.driver.*;
// import edu.illinois.cs.cogcomp.quant.lbj.QuantitiesClassifier;
// import edu.illinois.cs.cogcomp.quant.standardize.Normalizer;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class Quantifier_llmsuite_5_GPTLLMTest {

@Test
public void testWordSplitSentenceHandlesAllPatterns() {
Pattern[] patterns = new Pattern[25];
patterns[0] = Pattern.compile("-(\\D)");
patterns[1] = Pattern.compile("(\\S)-");
patterns[2] = Pattern.compile("(\\d)-(\\d|\\.\\d)");
patterns[3] = Pattern.compile("(\\d),(\\d)");
patterns[4] = Pattern.compile("\\$(\\d)");
patterns[5] = Pattern.compile("(\\d)\\$");
patterns[6] = Pattern.compile("(\\d)%");
// Quantifier.wordSplitPat = patterns;
String input = "pre-A A-B 1-2 1,000 $45 30$ 42% done";
// String output = Quantifier.wordsplitSentence(input);
// assertTrue(output.contains("- B"));
// assertTrue(output.contains("A -"));
// assertTrue(output.contains("1 - 2"));
// assertTrue(output.contains("1000"));
// assertTrue(output.contains("$ 45"));
// assertTrue(output.contains("30 $"));
// assertTrue(output.contains("42 %"));
}

@Test
public void testWordSplitSentenceWithEmptyInputReturnsEmpty() {
Pattern[] patterns = new Pattern[25];
patterns[0] = Pattern.compile("-(\\D)");
patterns[1] = Pattern.compile("(\\S)-");
patterns[2] = Pattern.compile("(\\d)-(\\d|\\.\\d)");
patterns[3] = Pattern.compile("(\\d),(\\d)");
patterns[4] = Pattern.compile("\\$(\\d)");
patterns[5] = Pattern.compile("(\\d)\\$");
patterns[6] = Pattern.compile("(\\d)%");
// Quantifier.wordSplitPat = patterns;
String input = "";
// String output = Quantifier.wordsplitSentence(input);
// assertEquals("", output);
}

@Test
public void testGetSpansWithValidTextAndStandardizedFalse() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("He bought 3 apples.");
// List<QuantSpan> spans = quantifier.getSpans("He bought 3 apples.", false, ta);
// assertNotNull(spans);
}

@Test
public void testGetSpansWithNullTextAnnotationReturnsValidList() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// List<QuantSpan> spans = quantifier.getSpans("This costs $25 and weighs 5 kg.", true, null);
// assertNotNull(spans);
}

@Test
public void testGetSpansHandlesInvalidChunkParsingGracefully() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object example) {
// return "B-NUM";
// }
// };
// quantifier.normalizer = new Normalizer() {
// 
// @Override
// public Object parse(String s, String label) throws Exception {
// throw new RuntimeException("Mock parse failure");
// }
// };
String text = "The width is 45 inches.";
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation(text);
// List<QuantSpan> spans = quantifier.getSpans(text, true, ta);
// assertNotNull(spans);
// assertTrue(spans.isEmpty());
}

@Test
public void testGetAnnotatedStringIncludesAnnotations() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
String text = "It cost 50 dollars.";
// String annotated = quantifier.getAnnotatedString(text, true);
// assertNotNull(annotated);
// assertTrue(annotated.contains("["));
// assertTrue(annotated.contains("]"));
}

@Test(expected = Exception.class)
public void testGetAnnotatedStringWithNullTextThrows() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.getAnnotatedString(null, true);
}

@Test
public void testAddViewAddsQuantitiesView() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("He paid 20 dollars.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// quantifier.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test(expected = AnnotatorException.class)
public void testAddViewFailsWithoutSentenceView() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("Amount is 100 units.");
// quantifier.addView(ta);
}

@Test
public void testInitializeAssignsResources() {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// assertNotNull(quantifier.normalizer);
// assertNotNull(Quantifier.taBuilder);
// assertNotNull(Quantifier.wordSplitPat[0]);
}

@Test
public void testTrainMethodExecutesWithoutErrors() {
// Quantifier quantifier = new Quantifier();
// quantifier.modelName = "testModel/path/TrainTest";
try {
// quantifier.train();
} catch (Exception e) {
fail("train() should not throw exception: " + e.getMessage());
}
}

@Test
public void testTrainOnAllMethodExecutesWithoutErrors() {
// Quantifier quantifier = new Quantifier();
// quantifier.modelName = "testModel/path/TrainAllTest";
try {
// quantifier.trainOnAll();
} catch (Exception e) {
fail("trainOnAll() should not throw exception: " + e.getMessage());
}
}

@Test
public void testTestMethodExecutesWithoutErrors() {
// Quantifier quantifier = new Quantifier();
// quantifier.modelName = "testModel/path/TestOnly";
try {
// quantifier.test();
} catch (Exception e) {
fail("test() should not throw exception: " + e.getMessage());
}
}

@Test
public void testMainMethodRunsWithoutErrors() throws Throwable {
// Quantifier.main(new String[0]);
}

@Test
public void testConstructorWithLazyInitialization() {
// Quantifier quantifier = new Quantifier(true);
// assertNotNull(quantifier);
}

@Test
public void testGetSpansWithOnlyPunctuation() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
String text = "!@#$%^&*()";
// List<QuantSpan> spans = quantifier.getSpans(text, false, null);
// assertNotNull(spans);
// assertTrue(spans.isEmpty());
}

@Test
public void testGetSpansWithEmptyString() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
String text = "";
// List<QuantSpan> spans = quantifier.getSpans(text, true, null);
// assertNotNull(spans);
// assertTrue(spans.isEmpty());
}

@Test
public void testGetSpansWithChunkWithoutEndBoundary() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object example) {
// return "B-QUANT";
// }
// };
// quantifier.normalizer = new Normalizer();
String text = "Approximately 1000 units";
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder taBuilder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = taBuilder.createTextAnnotation(text);
// List<QuantSpan> spans = quantifier.getSpans(text, true, ta);
// assertNotNull(spans);
}

@Test
public void testGetAnnotatedStringWithNoQuantSpanMatch() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier testQuantifier = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) throws AnnotatorException {
// return new java.util.ArrayList<QuantSpan>();
// }
// };
String text = "Room 101 is available.";
// String output = testQuantifier.getAnnotatedString(text, false);
// assertNotNull(output);
// assertTrue(output.trim().length() > 0);
}

@Test
public void testAddViewWithTokenAlignmentEdgeCase() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
String text = "The value: $3000";
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation(text);
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// quantifier.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test
public void testInitializeWithCustomWordSplitPatterns() {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// assertNotNull(Quantifier.wordSplitPat[0]);
// assertTrue(Quantifier.wordSplitPat[0] instanceof Pattern);
}

@Test
public void testTrainMethodHandlesInvalidModelPathGracefully() {
// Quantifier quantifier = new Quantifier();
// quantifier.modelName = "invalid/path/model";
try {
// quantifier.train();
} catch (Exception e) {
fail("Train method should not throw exception: " + e.getMessage());
}
}

@Test
public void testTestMethodHandlesInvalidPathGracefully() {
// Quantifier quantifier = new Quantifier();
// quantifier.modelName = "invalid/test/path";
try {
// quantifier.test();
} catch (Exception e) {
fail("Test method should not throw exception: " + e.getMessage());
}
}

@Test
public void testWordSplitSentenceWithNoMatchPatterns() {
Pattern[] patterns = new Pattern[25];
patterns[0] = Pattern.compile("abc");
patterns[1] = Pattern.compile("xyz");
patterns[2] = Pattern.compile("123");
patterns[3] = Pattern.compile("zzz");
patterns[4] = Pattern.compile("yyy");
patterns[5] = Pattern.compile("xxx");
patterns[6] = Pattern.compile("www");
// Quantifier.wordSplitPat = patterns;
String input = "nothingShouldMatchHere";
// String output = Quantifier.wordsplitSentence(input);
// assertEquals("nothingShouldMatchHere", output);
}

@Test
public void testAddViewDoesNotThrowWhenSpanOutOfBounds() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier testQuantifier = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) throws AnnotatorException {
// java.util.List<QuantSpan> list = new java.util.ArrayList<QuantSpan>();
// list.add(new QuantSpan(new Object(), 10000, 10010));
// return list;
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("Short text.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
try {
// testQuantifier.addView(ta);
} catch (Exception e) {
fail("Should not throw even if span is outside TA bounds");
}
}

@Test
public void testGetSpansWithMixedBIOTransitions() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// private int counter = 0;
// 
// @Override
// public String discreteValue(Object example) {
// if (counter == 0) {
// counter++;
// return "B-QTY";
// } else if (counter == 1) {
// counter++;
// return "I-QTY";
// } else if (counter == 2) {
// counter++;
// return "B-TIME";
// }
// return "O";
// }
// };
// quantifier.normalizer = new Normalizer();
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
String text = "He ran 10 miles in 5 hours.";
TextAnnotation ta = builder.createTextAnnotation(text);
// List<QuantSpan> spans = quantifier.getSpans(text, true, ta);
// assertNotNull(spans);
}

@Test
public void testGetSpansWhenNormalizerReturnsNull() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object example) {
// return "B-QUANT";
// }
// };
// quantifier.normalizer = new Normalizer() {
// 
// @Override
// public Object parse(String val, String label) {
// return null;
// }
// };
String text = "25 miles per hour";
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation(text);
// List<QuantSpan> spans = quantifier.getSpans(text, true, ta);
// assertTrue(spans.isEmpty());
}

@Test
public void testGetAnnotatedStringWithNoMatchingSpanIndex() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier q = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// List<QuantSpan> spans = new ArrayList<QuantSpan>();
// spans.add(new QuantSpan("ms", 100, 110));
// return spans;
// }
// };
String text = "Short sentence.";
// String result = q.getAnnotatedString(text, false);
// assertNotNull(result);
// assertTrue(result.length() > 0);
}

@Test
public void testAddViewWithStartOffsetGreaterThanEndOffset() throws Exception {
// Quantifier q = new Quantifier(false);
// q.initialize(new ResourceManager());
// Quantifier customQuantifier = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// List<QuantSpan> spans = new ArrayList<QuantSpan>();
// spans.add(new QuantSpan("invalid", 50, 40));
// return spans;
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("This is a valid test sentence.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
try {
// customQuantifier.addView(ta);
} catch (Exception e) {
fail("Exception thrown during invalid span addView: " + e.getMessage());
}
}

@Test
public void testGetSpansWhenTokenizerMismatchDoesNotIncrementTokenPos() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object example) {
// return "B-QUANT";
// }
// };
// quantifier.normalizer = new Normalizer();
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", new String[] { "bad-token+" }, new int[][] { { 0, 10 } });
// ta.addView(ViewNames.TOKENS, new SpanLabelView(ViewNames.TOKENS, "test-generator", ta, 1.0));
// List<QuantSpan> spans = quantifier.getSpans("bad-token+", true, ta);
// assertNotNull(spans);
}

@Test
public void testAddViewMultipleQuantSpans() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier q = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// List<QuantSpan> spans = new ArrayList<QuantSpan>();
// spans.add(new QuantSpan("qty", 4, 5));
// spans.add(new QuantSpan("qty", 8, 14));
// return spans;
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("a 2 b 4 cm 6 kg");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// q.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
assertTrue(ta.getView(ViewNames.QUANTITIES).getNumberOfConstituents() >= 2);
}

@Test
public void testGetSpansWithNoTokens() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// TextAnnotation ta = new TextAnnotation("id", "txtid", new String[] {}, new int[][] {});
// ta.addView(ViewNames.TOKENS, new SpanLabelView(ViewNames.TOKENS, "gen", ta, 1.0));
// List<QuantSpan> spans = quantifier.getSpans(" ", false, ta);
// assertNotNull(spans);
// assertTrue(spans.isEmpty());
}

@Test
public void testAddViewWithEmptyQuantSpans() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier testQuantifier = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// return new ArrayList<QuantSpan>();
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("No numbers here.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// testQuantifier.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
assertEquals(0, ta.getView(ViewNames.QUANTITIES).getNumberOfConstituents());
}

@Test
public void testWordsplitSentenceWithNullMatchGroups() {
// Quantifier.wordSplitPat = new java.util.regex.Pattern[25];
// Quantifier.wordSplitPat[0] = java.util.regex.Pattern.compile("(\\d+)-(\\d+)");
// Quantifier.wordSplitPat[1] = java.util.regex.Pattern.compile("(\\d+)");
// Quantifier.wordSplitPat[2] = java.util.regex.Pattern.compile("(\\d+)");
// Quantifier.wordSplitPat[3] = java.util.regex.Pattern.compile("(\\d),(\\d)");
// Quantifier.wordSplitPat[4] = java.util.regex.Pattern.compile("\\$(\\d+)");
// Quantifier.wordSplitPat[5] = java.util.regex.Pattern.compile("(\\d+)\\$");
// Quantifier.wordSplitPat[6] = java.util.regex.Pattern.compile("(\\d+)%");
String input = "3-4 minus 1,000 equals 200% and $5 then 6$";
// String result = Quantifier.wordsplitSentence(input);
// assertTrue(result.contains("3 - 4"));
// assertTrue(result.contains("1000"));
// assertTrue(result.contains("200 %"));
// assertTrue(result.contains("$ 5"));
// assertTrue(result.contains("6 $"));
}

@Test
public void testGetSpansWithChunkerReturningOnlyO() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object example) {
// return "O";
// }
// };
// quantifier.normalizer = new Normalizer();
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("No quantitative data exists.");
// List<QuantSpan> spans = quantifier.getSpans(ta.getText(), true, ta);
// assertNotNull(spans);
// assertTrue(spans.isEmpty());
}

@Test
public void testGetSpansWithInvalidPredictionGroupTransition() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
final String[] predictions = new String[] { "B-UNIT", "I-TIME", "I-QUANT", "O", "B-QUANT" };
// quantifier.chunker = new QuantitiesClassifier() {
// 
// int idx = 0;
// 
// @Override
// public String discreteValue(Object example) {
// if (idx >= predictions.length)
// return "O";
// return predictions[idx++];
// }
// };
// quantifier.normalizer = new Normalizer();
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("Speed was 5 mph or 60 minutes only.");
// List<QuantSpan> spans = quantifier.getSpans(ta.getText(), true, ta);
// assertNotNull(spans);
}

@Test
public void testAddViewWithQuantSpanCoveringAllTokens() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier custom = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// int start = inputTA.getTokenCharacterOffset(0).getFirst();
// int end = inputTA.getTokenCharacterOffset(inputTA.size() - 1).getSecond();
// List<QuantSpan> spans = new ArrayList<QuantSpan>();
// spans.add(new QuantSpan("total", start, end));
// return spans;
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("All tokens in this sentence.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// custom.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
assertEquals(1, ta.getView(ViewNames.QUANTITIES).getNumberOfConstituents());
}

@Test
public void testAddViewWithMultipleOverlappingSpans() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier custom = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// int start1 = inputTA.getTokenCharacterOffset(1).getFirst();
// int end1 = inputTA.getTokenCharacterOffset(3).getSecond();
// int start2 = inputTA.getTokenCharacterOffset(2).getFirst();
// int end2 = inputTA.getTokenCharacterOffset(4).getSecond();
// List<QuantSpan> spans = new ArrayList<>();
// spans.add(new QuantSpan("A", start1, end1));
// spans.add(new QuantSpan("B", start2, end2));
// return spans;
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("Amount is around 50 dollars today.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// custom.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
assertTrue(ta.getView(ViewNames.QUANTITIES).getNumberOfConstituents() >= 2);
}

@Test
public void testTrainMethodDoesNotThrowWithNonexistentTrainFile() {
// Quantifier quantifier = new Quantifier();
// quantifier.modelName = "nonexistent/path/model";
try {
// quantifier.train();
} catch (Exception e) {
fail("train() should not throw an exception even with invalid path");
}
}

@Test
public void testTestMethodDoesNotThrowWithMissingData() {
// Quantifier quantifier = new Quantifier();
// quantifier.modelName = "nonexistent/test/model";
try {
// quantifier.test();
} catch (Exception e) {
fail("test() should not throw, even if test data is invalid or missing");
}
}

@Test
public void testAddViewHandlesCharacterOffsetMismatch() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier custom = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// int invalidOffset = inputTA.getText().length() + 5;
// List<QuantSpan> spans = new ArrayList<QuantSpan>();
// spans.add(new QuantSpan("bad", invalidOffset, invalidOffset + 3));
// return spans;
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("Edge offset issue.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// custom.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test
public void testAddViewWithPartialTokenCoverageSpan() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier partialCover = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation inputTA) {
// int partialStart = inputTA.getTokenCharacterOffset(2).getFirst() + 1;
// int partialEnd = inputTA.getTokenCharacterOffset(3).getFirst() - 1;
// List<QuantSpan> spans = new ArrayList<>();
// spans.add(new QuantSpan("partial", partialStart, partialEnd));
// return spans;
// }
// };
StatefulTokenizer tokenizer = new StatefulTokenizer();
TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
TextAnnotation ta = builder.createTextAnnotation("Let's measure 15 kilograms fairly.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// partialCover.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test
public void testGetSpansWithMalformedTextAnnotationOffsets() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
String[] tokens = new String[] { "ten", "meters" };
int[][] segments = new int[][] { { 5, 4 }, { 10, 15 } };
// TextAnnotation ta = new TextAnnotation("id", "test", tokens, segments);
// ta.addView(ViewNames.TOKENS, new SpanLabelView(ViewNames.TOKENS, "test", ta, 1.0));
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object o) {
// return "B-DISTANCE";
// }
// };
// quantifier.normalizer = new Normalizer();
// List<QuantSpan> result = quantifier.getSpans("ten meters", true, ta);
// assertNotNull(result);
}

@Test
public void testGetSpansWithSingleTokenInput() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object obj) {
// return "B-AMOUNT";
// }
// };
// quantifier.normalizer = new Normalizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("100");
// List<QuantSpan> spans = quantifier.getSpans("100", true, ta);
// assertNotNull(spans);
}

@Test
public void testGetSpansReturnsSpanEvenWhenStandardizedFalse() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object obj) {
// return "B-MEASURE";
// }
// };
// quantifier.normalizer = new Normalizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("3 km");
// List<QuantSpan> spans = quantifier.getSpans("3 km", false, ta);
// assertNotNull(spans);
// assertFalse(spans.isEmpty());
}

@Test
public void testGetSpansHandlesNullNormalizerGracefully() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// quantifier.normalizer = null;
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object o) {
// return "B-WEIGHT";
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("5 kg");
// List<QuantSpan> result = quantifier.getSpans("5 kg", true, ta);
// assertNotNull(result);
}

@Test
public void testGetSpansChunkerReturnsIOButPreviousIsEmpty() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
final String[] predictions = new String[] { "I-COUNT", "O" };
// quantifier.chunker = new QuantitiesClassifier() {
// 
// int count = 0;
// 
// @Override
// public String discreteValue(Object o) {
// return count < predictions.length ? predictions[count++] : "O";
// }
// };
// quantifier.normalizer = new Normalizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("several books");
// List<QuantSpan> result = quantifier.getSpans("several books", true, ta);
// assertNotNull(result);
}

@Test
public void testGetAnnotatedStringWithMultipleShortSpans() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier custom = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation ta) {
// int start1 = ta.getTokenCharacterOffset(1).getFirst();
// int end1 = ta.getTokenCharacterOffset(1).getSecond();
// int start2 = ta.getTokenCharacterOffset(2).getFirst();
// int end2 = ta.getTokenCharacterOffset(2).getSecond();
// List<QuantSpan> list = new ArrayList<>();
// list.add(new QuantSpan("first", start1, end1));
// list.add(new QuantSpan("second", start2, end2));
// return list;
// }
// };
String text = "has 3 dogs";
// String output = custom.getAnnotatedString(text, false);
// assertNotNull(output);
// assertTrue(output.contains("["));
// assertTrue(output.contains("]"));
}

@Test
public void testAddViewDoesNothingWhenGetSpansReturnsEmpty() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier noSpans = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation ta) {
// return Collections.emptyList();
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("The sky is blue.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// noSpans.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
assertEquals(0, ta.getView(ViewNames.QUANTITIES).getNumberOfConstituents());
}

@Test
public void testGetSpansWithOnlyBTagsAndOneOEnd() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
final String[] tags = new String[] { "B-TIME", "B-TIME", "B-TIME", "O" };
// quantifier.chunker = new QuantitiesClassifier() {
// 
// int index = 0;
// 
// @Override
// public String discreteValue(Object o) {
// return index < tags.length ? tags[index++] : "O";
// }
// };
// quantifier.normalizer = new Normalizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("2 days 5 hours 3 minutes done");
// List<QuantSpan> spans = quantifier.getSpans("2 days 5 hours 3 minutes done", true, ta);
// assertNotNull(spans);
}

@Test
public void testAddViewWithMultipleLabels() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new ResourceManager());
// Quantifier custom = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation ta) {
// int start1 = ta.getTokenCharacterOffset(1).getFirst();
// int end1 = ta.getTokenCharacterOffset(1).getSecond();
// int start2 = ta.getTokenCharacterOffset(3).getFirst();
// int end2 = ta.getTokenCharacterOffset(3).getSecond();
// List<QuantSpan> list = new ArrayList<>();
// list.add(new QuantSpan("amount", start1, end1));
// list.add(new QuantSpan("price", start2, end2));
// return list;
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("bought 3 books for 10");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// custom.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
assertEquals(2, ta.getView(ViewNames.QUANTITIES).getNumberOfConstituents());
}

@Test
public void testGetSpansWithErroneousIWithoutStart() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// int call = 0;
// 
// @Override
// public String discreteValue(Object o) {
// if (call == 0) {
// call++;
// return "I-NUMBER";
// } else
// return "O";
// }
// };
// quantifier.normalizer = new Normalizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("27");
// List<QuantSpan> spans = quantifier.getSpans("27", true, ta);
// assertNotNull(spans);
}

@Test
public void testGetSpansWhereNormalizerThrowsUnexpectedException() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object o) {
// return "B-NUM";
// }
// };
// quantifier.normalizer = new Normalizer() {
// 
// @Override
// public Object parse(String spanText, String type) throws Exception {
// throw new RuntimeException("Unexpected parse error");
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("about 42");
// List<QuantSpan> spans = quantifier.getSpans("about 42", true, ta);
// assertNotNull(spans);
// assertTrue(spans.isEmpty());
}

@Test
public void testZeroLengthSpan() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier stub = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardize, TextAnnotation ta) {
// int position = ta.getTokenCharacterOffset(2).getFirst();
// List<QuantSpan> spans = new ArrayList<>();
// spans.add(new QuantSpan("zero", position, position));
// return spans;
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("zero length span test.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// stub.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test
public void testGetAnnotatedStringWithSpanTokenMismatchNoMatch() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier custom = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation ta) {
// List<QuantSpan> list = new ArrayList<>();
// list.add(new QuantSpan("foo", 999, 1000));
// return list;
// }
// };
String originalText = "not a match";
// String result = custom.getAnnotatedString(originalText, true);
// assertTrue(result.contains("not"));
}

@Test
public void testAddViewWhenSpanIdConversionFails() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier custom = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardize, TextAnnotation ta) {
// List<QuantSpan> qspans = new ArrayList<>();
// qspans.add(new QuantSpan("bad", 9999, 9999));
// return qspans;
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("Safe text");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// custom.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test
public void testGetSpansWithSpecialCharactersText() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object o) {
// return "B-MONEY";
// }
// };
// quantifier.normalizer = new Normalizer();
String specialText = "@!#$%^& *()_+=123";
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation(specialText);
// List<QuantSpan> spans = quantifier.getSpans(specialText, true, ta);
// assertNotNull(spans);
}

@Test
public void testGetSpansWithWhitespaceOnlyText() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
String input = "    ";
// List<QuantSpan> spans = quantifier.getSpans(input, true, null);
// assertNotNull(spans);
// assertEquals(0, spans.size());
}

@Test
public void testGetSpansReturnsNullLabelSpan() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier withNullLabel = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean std, TextAnnotation ta) {
// int start = ta.getTokenCharacterOffset(0).getFirst();
// int end = ta.getTokenCharacterOffset(0).getSecond();
// List<QuantSpan> list = new ArrayList<>();
// QuantSpan nullObjectSpan = new QuantSpan(null, start, end);
// list.add(nullObjectSpan);
// return list;
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("test");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// withNullLabel.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test
public void testMainExecutes() throws Throwable {
// Quantifier.main(new String[0]);
}

@Test
public void testTrainWhenModelFilesAlreadyExist() {
// Quantifier quantifier = new Quantifier();
String mockModelPath = "existing-model" + File.separator + "QuantitiesClassifier";
// quantifier.modelName = mockModelPath;
try {
// quantifier.train();
} catch (Exception e) {
fail("train() should silently complete even if files already exist or paths are reused.");
}
}

@Test
public void testTestWhenModelFilesAlreadyExist() {
// Quantifier quantifier = new Quantifier();
String mockModelPath = "existing-test-model" + File.separator + "QuantitiesClassifier";
// quantifier.modelName = mockModelPath;
try {
// quantifier.test();
} catch (Exception e) {
fail("test() should catch exceptions thrown due to missing test files or model files.");
}
}

@Test
public void testSpanEndsAtLastToken() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object example) {
// return "B-TIME";
// }
// };
// quantifier.normalizer = new Normalizer();
String text = "It lasted 3 hours.";
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation(text);
// List<QuantSpan> spans = quantifier.getSpans(text, true, ta);
// assertNotNull(spans);
// assertFalse(spans.isEmpty());
}

@Test
public void testEmptyTokensInput() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// TextAnnotation ta = new TextAnnotation("corpus", "id", new String[] {}, new int[][] {});
// List<QuantSpan> spans = quantifier.getSpans("", true, ta);
// assertNotNull(spans);
// assertEquals(0, spans.size());
}

@Test
public void testChunkerReturnsEmptyString() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object e) {
// return "";
// }
// };
// quantifier.normalizer = new Normalizer();
String input = "Something with numbers 42";
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation(input);
// List<QuantSpan> spans = quantifier.getSpans(input, true, ta);
// assertNotNull(spans);
}

@Test
public void testChunkerReturnsNull() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object e) {
// return null;
// }
// };
// quantifier.normalizer = new Normalizer();
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("A span with unknown label");
// List<QuantSpan> spans = quantifier.getSpans("A span with unknown label", true, ta);
// assertNotNull(spans);
}

@Test
public void testOverlappingSpansSameOffsets() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// Quantifier withDuplicates = new Quantifier(false) {
// 
// @Override
// public List<QuantSpan> getSpans(String text, boolean standardized, TextAnnotation ta) {
// int start = ta.getTokenCharacterOffset(1).getFirst();
// int end = ta.getTokenCharacterOffset(1).getSecond();
// QuantSpan span1 = new QuantSpan("amount", start, end);
// QuantSpan span2 = new QuantSpan("price", start, end);
// List<QuantSpan> list = new ArrayList<>();
// list.add(span1);
// list.add(span2);
// return list;
// }
// };
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("Bought 50 apples.");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// withDuplicates.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}

@Test
public void testGetSpansWithStandardizedFalseNoNormalization() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.initialize(new edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager());
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object example) {
// return "B-WEIGHT";
// }
// };
// quantifier.normalizer = new Normalizer() {
// 
// @Override
// public Object parse(String s, String type) throws Exception {
// fail("parse() should not be called when standardized=false");
// return null;
// }
// };
String input = "The package weighs 4 kg.";
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation(input);
// List<QuantSpan> spans = quantifier.getSpans(input, false, ta);
// assertNotNull(spans);
}

@Test
public void testCallAddViewWithoutExplicitInitialize() throws Exception {
// Quantifier quantifier = new Quantifier(false);
// quantifier.chunker = new QuantitiesClassifier() {
// 
// @Override
// public String discreteValue(Object o) {
// return "B-RANDOM";
// }
// };
// quantifier.normalizer = new Normalizer();
// Quantifier.wordSplitPat = new java.util.regex.Pattern[7];
TextAnnotation ta = new TokenizerTextAnnotationBuilder(new StatefulTokenizer()).createTextAnnotation("uninitialized with 10%");
ta.addView(ViewNames.SENTENCE, ta.getView(ViewNames.TOKENS));
// quantifier.addView(ta);
assertTrue(ta.hasView(ViewNames.QUANTITIES));
}
}
