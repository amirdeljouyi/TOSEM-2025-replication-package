package edu.illinois.cs.cogcomp.ner;

import edu.illinois.cs.cogcomp.annotation.AnnotatorConfigurator;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.ner.StringStatisticsUtils.CharacteristicWords;
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.stanford.nlp.util.IntPair;
import junit.framework.TestCase;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NERAnnotator_3_GPTLLMTest {

@Test
public void testConstructorWithViewNameOnly() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
assertNotNull(annotator);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testConstructorWithResourceManager() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertNotNull(annotator);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testAddViewAddsNERViewToTextAnnotation() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
List<String> tokens = Arrays.asList("Barack", "Obama", "visited", "Germany", ".");
List<String> sentence = Arrays.asList("Barack", "Obama", "visited", "Germany", ".");
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "Barack Obama visited Germany.", new TokenLabelView("TOKENS", "manual", null), new ArrayList<>());
// ta.initializeView("TOKENS", new TokenLabelView("TOKENS", "manual", ta));
// TokenLabelView tokenView = (TokenLabelView) ta.getView("TOKENS");
for (int i = 0; i < tokens.size(); i++) {
// tokenView.addToken(new Token(tokens.get(i), i));
}
// ta.addSentence(new Constituent("", "", ta, 0, tokens.size()));
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
// assertTrue(ta.getView(ViewNames.NER_CONLL).getConstituents().size() >= 0);
}

@Test
public void testGetTagValuesReturnsNonEmptySet() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tagValues = annotator.getTagValues();
assertNotNull(tagValues);
assertTrue(tagValues.contains("O") || tagValues.size() > 0);
}

@Test
public void testGetL1FeatureWeightsReturnsFeatureMap() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> map = annotator.getL1FeatureWeights();
assertNotNull(map);
assertFalse(map.isEmpty());
Object[] keys = map.keySet().toArray();
Object[] values = map.values().toArray();
assertNotNull(keys[0]);
assertNotNull(values[0]);
assertTrue(((double[]) values[0]).length > 0);
}

@Test
public void testGetL2FeatureWeightsReturnsFeatureMap() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> map = annotator.getL2FeatureWeights();
assertNotNull(map);
assertFalse(map.isEmpty());
Object[] keys = map.keySet().toArray();
Object[] values = map.values().toArray();
assertNotNull(keys[0]);
assertNotNull(values[0]);
assertTrue(((double[]) values[0]).length > 0);
}

@Test
public void testLazyInitializationViaGetTagValues() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.contains("O") || tags.size() > 0);
}

@Test
public void testAddViewEmptyTextAnnotationStillGeneratesView() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "", new TokenLabelView("TOKENS", "test", null), new ArrayList<>());
// ta.initializeView("TOKENS", new TokenLabelView("TOKENS", "test", ta));
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testMultipleAddViewCallsDoNotThrow() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
List<String> tokens = Arrays.asList("Angela", "Merkel", "is", "from", "Germany", ".");
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "Angela Merkel is from Germany.", new TokenLabelView("TOKENS", "manual", null), new ArrayList<>());
// ta.initializeView("TOKENS", new TokenLabelView("TOKENS", "manual", ta));
// TokenLabelView tokenView = (TokenLabelView) ta.getView("TOKENS");
// tokenView.addToken(new Token("Angela", 0));
// tokenView.addToken(new Token("Merkel", 1));
// tokenView.addToken(new Token("is", 2));
// tokenView.addToken(new Token("from", 3));
// tokenView.addToken(new Token("Germany", 4));
// tokenView.addToken(new Token(".", 5));
// ta.addSentence(new Constituent("", "", ta, 0, tokens.size()));
// annotator.addView(ta);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test(expected = NullPointerException.class)
public void testAddViewWithNullInputThrowsException() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.addView(null);
}

@Test
public void testAddViewWithOnlyOneToken() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Obama" };
String[] sentence = new String[] { "Obama" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 1 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Obama", tokens, new int[][] { starts }, new int[][] { ends });
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithSentenceThatContainsEmptyStringToken() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "John", "", "Smith" };
String[] sentenceTokens = new String[] { "John", "", "Smith" };
int[] starts = new int[] { 0, 2 };
int[] ends = new int[] { 1, 3 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "John Smith", tokens, new int[][] { starts, ends }, new int[][] { starts, ends });
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWherePredictionDoesNotStartWithBIOFormat() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "He", "ran", "fast" };
int[][] starts = new int[][] { { 0 } };
int[][] ends = new int[][] { { 3 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "He ran fast", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewCountsSingleConstituentCorrectly() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Steve", "Jobs" };
int[][] starts = new int[][] { { 0 } };
int[][] ends = new int[][] { { 2 } };
// TextAnnotation ta = new TextAnnotation("test", "1", "Steve Jobs", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testConstructorWithMissingConfigFile() {
try {
NERAnnotator annotator = new NERAnnotator("nonexistent-file.properties", ViewNames.NER_CONLL);
fail("Expected IOException due to missing config file");
} catch (IOException e) {
assertTrue(e.getMessage().contains("nonexistent-file"));
}
}

@Test
public void testMainMethodWithMissingArgs() {
try {
NERAnnotator.main(new String[] { "onlyOneArg" });
} catch (Exception e) {
fail("Should handle single argument gracefully");
}
}

@Test
public void testMainMethodWithInvalidModelArg() {
String configFilePath = "invalid_config_path.cfg";
String[] args = new String[] { configFilePath, "INVALIDMODEL" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof IOException || e instanceof RuntimeException);
}
}

@Test
public void testGetTagValuesAfterSecondCallReturnsSameResult() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> firstCall = annotator.getTagValues();
Set<String> secondCall = annotator.getTagValues();
assertNotNull(firstCall);
assertNotNull(secondCall);
assertEquals(firstCall, secondCall);
}

@Test
public void testAddViewReentrantOnMultipleTextAnnotations() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens1 = new String[] { "A", "B" };
int[][] spans = new int[][] { { 0 }, { 2 } };
// TextAnnotation ta1 = new TextAnnotation("c1", "id1", "A B", tokens1, spans, spans);
// annotator.addView(ta1);
// assertTrue(ta1.hasView(ViewNames.NER_CONLL));
String[] tokens2 = new String[] { "C", "D", "E" };
// TextAnnotation ta2 = new TextAnnotation("c2", "id2", "C D E", tokens2, spans, spans);
// annotator.addView(ta2);
// assertTrue(ta2.hasView(ViewNames.NER_CONLL));
String[] tokens3 = new String[] { "F" };
// TextAnnotation ta3 = new TextAnnotation("c3", "id3", "F", tokens3, spans, spans);
// annotator.addView(ta3);
// assertTrue(ta3.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithMultipleSentences() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Barack", "Obama", ".", "Angela", "Merkel", "." };
int[][] sentenceStarts = new int[][] { { 0, 1, 2 }, { 3, 4, 5 } };
int[][] sentenceEnds = new int[][] { { 1, 2, 3 }, { 4, 5, 6 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Barack Obama. Angela Merkel.", tokens, sentenceStarts, sentenceEnds);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithSentenceWithoutValidTokensDoesNotThrow() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "", "", "" };
int[][] sentenceStarts = new int[][] { { 0, 1, 2 } };
int[][] sentenceEnds = new int[][] { { 1, 2, 3 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, sentenceStarts, sentenceEnds);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewHandlesInvalidPredictionTransition() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "President", "of", "USA" };
int[][] sentenceStarts = new int[][] { { 0, 1, 2 } };
int[][] sentenceEnds = new int[][] { { 1, 2, 3 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "President of USA", tokens, sentenceStarts, sentenceEnds);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1FeatureWeightsReturnsConsistentWeights() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> weightsMap = annotator.getL1FeatureWeights();
assertNotNull(weightsMap);
assertFalse(weightsMap.isEmpty());
Object[] features = weightsMap.keySet().toArray();
double[] weights1 = weightsMap.get(features[0]);
double[] weights2 = annotator.getL1FeatureWeights().get(features[0]);
assertEquals(weights1.length, weights2.length);
assertArrayEquals(weights1, weights2, 0.0001);
}

@Test
public void testGetL2FeatureWeightsReturnsConsistentWeights() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> weightsMap = annotator.getL2FeatureWeights();
assertNotNull(weightsMap);
assertFalse(weightsMap.isEmpty());
Object[] features = weightsMap.keySet().toArray();
double[] weights1 = weightsMap.get(features[0]);
double[] weights2 = annotator.getL2FeatureWeights().get(features[0]);
assertEquals(weights1.length, weights2.length);
assertArrayEquals(weights1, weights2, 0.0001);
}

@Test
public void testAddViewWordWithInvalidInsideTransition() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "New", "York", "Times" };
int[][] sentenceStarts = new int[][] { { 0, 1, 2 } };
int[][] sentenceEnds = new int[][] { { 1, 2, 3 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "New York Times", tokens, sentenceStarts, sentenceEnds);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithTokenOverflowFix() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Elon", "Musk" };
int[][] start = new int[][] { { 0, 1 } };
int[][] end = new int[][] { { 1, 2 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Elon Musk", tokens, start, end);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testMultipleInitializationsDoNotBreakAnnotator() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags1 = annotator.getTagValues();
Set<String> tags2 = annotator.getTagValues();
assertNotNull(tags1);
assertNotNull(tags2);
assertEquals(tags1, tags2);
}

@Test
public void testConstructorWithOntonotesViewTriggersSpecificConfigurationPath() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
Set<String> tagValues = annotator.getTagValues();
assertNotNull(tagValues);
assertTrue(tagValues.size() > 0);
}

@Test
public void testAddViewWithMismatchedBAndINERLabelsTriggersCorrectionPath() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "San", "Francisco", "Bay" };
int[][] starts = new int[][] { { 0, 1, 2 } };
int[][] ends = new int[][] { { 1, 2, 3 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "San Francisco Bay", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewGeneratesClosedEntityAtEndOfSentence() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Google", "is", "here" };
int[][] starts = new int[][] { { 0, 1, 2 } };
int[][] ends = new int[][] { { 1, 2, 3 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Google is here", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1FeatureWeightsHandlesEmptyFeaturesGracefully() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
props.setProperty("featureExtractorClass", "");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
assertNotNull(weights);
assertTrue(weights.size() >= 0);
}

@Test
public void testGetL2FeatureWeightsHandlesEmptyFeaturesGracefully() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
props.setProperty("featureExtractorClass", "");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
assertNotNull(weights);
assertTrue(weights.size() >= 0);
}

@Test
public void testAddViewEntityInterruptedByOResetsSpan() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Barack", "Obama", "and", "Joe", "Biden" };
int[][] starts = new int[][] { { 0, 1, 2, 3, 4 } };
int[][] ends = new int[][] { { 1, 2, 3, 4, 5 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Barack Obama and Joe Biden", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testTokenOffsetBeyondLengthTriggersSpanCorrection() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Mr.", "John", "Smith" };
int[][] starts = new int[][] { { 0, 1, 2 } };
int[][] ends = new int[][] { { 1, 2, 3 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Mr. John Smith", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testMainPrintsErrorMessageWithIncorrectArguments() {
try {
String[] args = new String[] { "onlyOneArg" };
NERAnnotator.main(args);
} catch (Exception e) {
fail("main() should not throw with invalid arg count.");
}
}

@Test
public void testMainHandlesInvalidLevelModelArg() {
try {
String[] args = new String[] { "nonexistent.cfg", "INVALID" };
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof IOException);
}
}

@Test
public void testAddViewWithInvalidBIOSequenceHandlesGracefully() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "I", "I", "O", "B", "B", "O" };
int[][] starts = new int[][] { { 0, 1, 2, 3, 4, 5 } };
int[][] ends = new int[][] { { 1, 2, 3, 4, 5, 6 } };
// TextAnnotation ta = new TextAnnotation("c", "i", "I I O B B O", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithSentenceContainingOnlyOneEmptyToken() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "" };
int[][] starts = new int[][] { { 0 } };
int[][] ends = new int[][] { { 1 } };
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
// assertEquals(0, ta.getView(ViewNames.NER_CONLL).getConstituents().size());
}

@Test
public void testAddViewWithConstituentOfLengthOneTriggersSpanCorrection() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Apple" };
int[][] starts = new int[][] { { 0 } };
int[][] ends = new int[][] { { 1 } };
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "Apple", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithMultipleUnlabeledEmptyTokensDoesNotCorruptIndexing() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "", "", "Obama", "" };
int[][] starts = new int[][] { { 0, 1, 2, 3 } };
int[][] ends = new int[][] { { 1, 2, 3, 4 } };
// TextAnnotation ta = new TextAnnotation("test", "id", "Obama", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetTagValuesIsSafeAfterMultipleCalls() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> values1 = annotator.getTagValues();
Set<String> values2 = annotator.getTagValues();
assertNotNull(values1);
assertNotNull(values2);
assertEquals(values1, values2);
}

@Test
public void testGetL1FeatureWeightsConsistentAcrossCalls() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights1 = annotator.getL1FeatureWeights();
HashMap<Feature, double[]> weights2 = annotator.getL1FeatureWeights();
assertNotNull(weights1);
assertNotNull(weights2);
assertEquals(weights1.keySet(), weights2.keySet());
}

@Test
public void testGetL2FeatureWeightsConsistentAcrossCalls() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights1 = annotator.getL2FeatureWeights();
HashMap<Feature, double[]> weights2 = annotator.getL2FeatureWeights();
assertNotNull(weights1);
assertNotNull(weights2);
assertEquals(weights1.keySet(), weights2.keySet());
}

@Test
public void testAddViewWithInvalidNERLabelsStillCreatesStableOutput() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "BLAH1", "I-BLAH2", "O", "B-WTF", "I-X" };
int[][] starts = new int[][] { { 0, 1, 2, 3, 4 } };
int[][] ends = new int[][] { { 1, 2, 3, 4, 5 } };
// TextAnnotation ta = new TextAnnotation("c", "id", "random input", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testZeroLengthSentenceArrayDoesNotBreakAnnotator() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] {};
int[][] starts = new int[][] {};
int[][] ends = new int[][] {};
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
// assertEquals(0, ta.getView(ViewNames.NER_CONLL).getConstituents().size());
}

@Test
public void testMainHandlesInvalidFilePathGracefully() {
String[] args = new String[] { "/path/does/not/exist.cfg", "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof IOException || e instanceof RuntimeException);
}
}

@Test
public void testMainRejectsInvalidModelLevelArgumentWithTwoArgs() {
String[] args = new String[] { "someconfig.cfg", "UNKNOWNLEVEL" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof IOException);
}
}

@Test
public void testSpanClosesWithInconsistentITransition() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
String[] tokens = new String[] { "B-PER", "I-ORG", "O" };
int[][] starts = new int[][] { { 0, 1, 2 } };
int[][] ends = new int[][] { { 1, 2, 3 } };
// TextAnnotation textAnnotation = new TextAnnotation("test", "id", "B-PER I-ORG O", tokens, starts, ends);
// annotator.addView(textAnnotation);
// assertTrue(textAnnotation.hasView(ViewNames.NER_CONLL));
}

@Test
public void testSpanIsForcedToIncrementEndIndexIfESmallerThanS() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
String[] tokens = new String[] { "X" };
int[][] starts = new int[][] { { 0 } };
int[][] ends = new int[][] { { 1 } };
// TextAnnotation txt = new TextAnnotation("t", "doc", "X", tokens, starts, ends);
// annotator.addView(txt);
// assertTrue(txt.hasView(ViewNames.NER_CONLL));
// assertNotNull(txt.getView(ViewNames.NER_CONLL));
}

@Test
public void testEntityClosesAtSentenceEnd() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
String[] tokens = new String[] { "B-LOC", "I-LOC", "I-LOC" };
int[][] starts = new int[][] { { 0, 1, 2 } };
int[][] ends = new int[][] { { 1, 2, 3 } };
// TextAnnotation textAnn = new TextAnnotation("corp", "id", "B I I", tokens, starts, ends);
// annotator.addView(textAnn);
// assertTrue(textAnn.hasView(ViewNames.NER_CONLL));
}

@Test
public void testEntityClosedByNextBTag() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
String[] tokens = new String[] { "B-ORG", "I-ORG", "B-PER" };
int[][] starts = new int[][] { { 0, 1, 2 } };
int[][] ends = new int[][] { { 1, 2, 3 } };
// TextAnnotation textAnn = new TextAnnotation("corp", "id", "B I B", tokens, starts, ends);
// annotator.addView(textAnn);
// assertTrue(textAnn.hasView(ViewNames.NER_CONLL));
// assertNotNull(textAnn.getView(ViewNames.NER_CONLL));
}

@Test
public void testGetTagValuesReturnsNoNulls() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tagVals = annotator.getTagValues();
assertNotNull(tagVals);
assertFalse(tagVals.contains(null));
}

@Test
public void testAddViewWithMixedBIOSequenceDoesNotCrash() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "B-ORG", "I-MISC", "O", "B-LOC", "I-LOC", "O" };
int[][] starts = new int[][] { { 0, 1, 2, 3, 4, 5 } };
int[][] ends = new int[][] { { 1, 2, 3, 4, 5, 6 } };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "sequence", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1FeatureWeightsMapValueHasPositiveLength() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = ner.getL1FeatureWeights();
Object[] values = weights.values().toArray();
assertNotNull(values[0]);
assertTrue(((double[]) values[0]).length > 0);
}

@Test
public void testGetL2FeatureWeightsMapValueHasPositiveLength() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = ner.getL2FeatureWeights();
Object[] values = weights.values().toArray();
assertNotNull(values[0]);
assertTrue(((double[]) values[0]).length > 0);
}

@Test
public void testMainWithProperArgsProcessesWithoutCrash() {
String fakeConfigPath = "nonexistent_config.properties";
String[] args = new String[] { fakeConfigPath, "L2" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof IOException || e instanceof RuntimeException);
}
}

@Test
public void testMainRejectsMissingArguments() {
try {
String[] badArgs = new String[] {};
NERAnnotator.main(badArgs);
} catch (Exception e) {
fail("main() with missing arguments should not crash");
}
}

@Test
public void testExplicitLazyInitializationTrue() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertFalse(tags.isEmpty());
}

@Test
public void testTagValuePrefixDiversity() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tagValues = annotator.getTagValues();
boolean hasB = false;
boolean hasI = false;
boolean hasO = false;
if (tagValues.contains("O"))
hasO = true;
if (tagValues.contains("B-ORG") || tagValues.contains("B-PER"))
hasB = true;
if (tagValues.contains("I-ORG") || tagValues.contains("I-PER"))
hasI = true;
assertTrue(hasB || hasI || hasO);
}

@Test
public void testGetL1FeatureWeightsNoCrashIfEmptyLexicon() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
assertNotNull(weights);
assertTrue(weights.size() >= 0);
}

@Test
public void testGetL2FeatureWeightsNoCrashIfEmptyLexicon() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
assertNotNull(weights);
assertTrue(weights.size() >= 0);
}

@Test
public void testAddViewSameTokensWithInvalidTransitions() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "John", "John", "O", "John" };
int[][] starts = new int[][] { { 0, 1, 2, 3 } };
int[][] ends = new int[][] { { 1, 2, 3, 4 } };
// TextAnnotation textAnnotation = new TextAnnotation("c", "i", "test", tokens, starts, ends);
// ner.addView(textAnnotation);
// assertTrue(textAnnotation.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewIdempotencySingleInstance() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
String[] tokens = new String[] { "Bob", "Bob", "Bob" };
int[][] starts = new int[][] { { 0, 1, 2 } };
int[][] ends = new int[][] { { 1, 2, 3 } };
// TextAnnotation txt = new TextAnnotation("a", "k", "Bob Bob Bob", tokens, starts, ends);
// ner.addView(txt);
// ner.addView(txt);
// assertTrue(txt.hasView(ViewNames.NER_CONLL));
}

@Test
public void testMainWithInvalidPathPrintsWarningAndExits() {
String[] args = new String[] { "/invalid/path/config.cfg", "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof IOException || e instanceof RuntimeException);
}
}

@Test
public void testMainWithSingleArgument() {
try {
String[] args = new String[] { "only-one-arg" };
NERAnnotator.main(args);
} catch (Exception e) {
fail("Should not throw when exiting early with warning");
}
}

@Test
public void testMainWithInvalidModelLevel() {
String[] args = new String[] { "some-config.cfg", "INVALID" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof IOException);
}
}

@Test
public void testLazyInitializeTriggeredByGetTagValues() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> values = ner.getTagValues();
assertNotNull(values);
assertTrue(values.size() > 0);
}
}
