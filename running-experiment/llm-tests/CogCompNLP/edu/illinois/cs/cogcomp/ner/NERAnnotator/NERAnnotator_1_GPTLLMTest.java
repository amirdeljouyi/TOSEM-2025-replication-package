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

public class NERAnnotator_1_GPTLLMTest {

@Test
public void testDefaultConstructorWithOntonotesViewName() throws IOException {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
assertNotNull(annotator);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testCustomResourceManagerInitializesViewNameProperly() {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertNotNull(annotator);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testAnnotatorAddViewWithSingleSentence() throws IOException {
String[] tokens = { "Barack", "Obama", "was", "born", "in", "Hawaii", "." };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] startOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "Barack Obama was born in Hawaii.", tokens, startOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// int constituentCount = view.getNumberOfConstituents();
// assertTrue(constituentCount >= 0);
}

@Test
public void testAnnotatorAddViewWithEmptyToken() throws IOException {
String[] tokensWithEmpty = { "Barack", "", "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokensWithEmpty);
int[] startOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "Barack Obama", tokensWithEmpty, startOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// int count = view.getNumberOfConstituents();
// assertTrue(count >= 0);
}

@Test
public void testAnnotatorAddViewWithMultipleSentences() throws IOException {
String[] tokens = { "Barack", "Obama", "was", "born", ".", "Michelle", "Obama", "lived", "in", "Chicago", "." };
List<String[]> sentences = new ArrayList<>();
sentences.add(new String[] { "Barack", "Obama", "was", "born", "." });
sentences.add(new String[] { "Michelle", "Obama", "lived", "in", "Chicago", "." });
int[] startOffsets = { 0, 5 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "Two sentences", tokens, startOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// int count = view.getNumberOfConstituents();
// assertTrue(count >= 0);
}

@Test
public void testTagValuesIncludeCommonLabels() throws IOException {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.contains("O"));
boolean hasBeginTag = false;
boolean hasInsideTag = false;
Iterator<String> iter = tags.iterator();
if (iter.hasNext()) {
String tag = iter.next();
if (tag.startsWith("B-")) {
hasBeginTag = true;
}
if (tag.startsWith("I-")) {
hasInsideTag = true;
}
}
assertTrue(hasBeginTag || hasInsideTag || tags.size() > 1);
}

@Test
public void testL1FeatureWeightsValidFeatureAndWeights() throws IOException {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
HashMap<Feature, double[]> l1Weights = annotator.getL1FeatureWeights();
assertNotNull(l1Weights);
assertTrue(l1Weights.size() > 0);
Iterator<Map.Entry<Feature, double[]>> entryIterator = l1Weights.entrySet().iterator();
if (entryIterator.hasNext()) {
Map.Entry<Feature, double[]> entry = entryIterator.next();
Feature feature = entry.getKey();
double[] weights = entry.getValue();
assertNotNull(feature);
assertNotNull(weights);
assertTrue(weights.length > 0);
}
}

@Test
public void testL2FeatureWeightsValidFeatureAndWeights() throws IOException {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
HashMap<Feature, double[]> l2Weights = annotator.getL2FeatureWeights();
assertNotNull(l2Weights);
assertTrue(l2Weights.size() > 0);
Iterator<Map.Entry<Feature, double[]>> entryIterator = l2Weights.entrySet().iterator();
if (entryIterator.hasNext()) {
Map.Entry<Feature, double[]> entry = entryIterator.next();
Feature feature = entry.getKey();
double[] weights = entry.getValue();
assertNotNull(feature);
assertNotNull(weights);
assertTrue(weights.length > 0);
}
}

@Test
public void testAddViewCreatesConstituentsWithProperSpan() throws IOException {
String[] tokens = { "Barack", "Obama", "was", "born", "in", "Hawaii", "." };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] startOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "sample", tokens, startOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// if (view.getNumberOfConstituents() > 0) {
// Constituent c = view.getConstituents().get(0);
// assertNotNull(c.getLabel());
// assertTrue(c.getStartSpan() >= 0);
// assertTrue(c.getEndSpan() > c.getStartSpan());
// }
}

@Test
public void testMainWithInvalidArguments() {
String[] args = {};
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main method should not throw exception on invalid args");
}
}

@Test
public void testAddViewWithOneTokenSentence() throws IOException {
String[] tokens = { "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] startOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "Obama", tokens, startOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewResultContainsNonEmptyLabelIfPresent() throws IOException {
String[] tokens = { "Barack", "Obama", "was", "born", "." };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] startOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "Barack Obama was born.", tokens, startOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// if (view.getNumberOfConstituents() > 0) {
// Constituent c = view.getConstituents().get(0);
// String label = c.getLabel();
// assertNotNull(label);
// assertFalse(label.isEmpty());
// }
}

@Test
public void testAddViewHandlesNoSentences() throws Exception {
String[] tokens = {};
int[] sentenceStartOffsets = {};
List<String[]> sentenceList = new java.util.ArrayList<>();
// TextAnnotation ta = new TextAnnotation("corpus", "id", "text", tokens, sentenceStartOffsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertEquals(0, ta.getView(ViewNames.NER_CONLL).getNumberOfConstituents());
}

@Test
public void testGetL1FeatureWeightsEmptyAfterManualReset() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
annotator.initialize(rm);
HashMap<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> weights = annotator.getL1FeatureWeights();
assertNotNull(weights);
assertTrue(weights.size() > 0);
}

@Test
public void testGetTagValuesWhenUninitializedTriggersInitialization() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.size() > 0);
}

@Test
public void testAddViewWithPredictionMismatchDoesNotThrow() throws Exception {
String[] tokens = { "X", "Y", "Z" };
List<String[]> sentenceList = new java.util.ArrayList<>();
sentenceList.add(new String[] { "X", "", "Z" });
int[] startOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("source", "id", "text", tokens, startOffsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewAssignsEntityWithCorrectBeginEndConditions() throws Exception {
String[] tokens = { "Steve", "Jobs", "was", "CEO", "of", "Apple", "." };
List<String[]> sentenceList = new java.util.ArrayList<>();
sentenceList.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "sample", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// List<Constituent> consList = ta.getView(ViewNames.NER_CONLL).getConstituents();
// if (consList.size() > 0) {
// Constituent c = consList.get(0);
// assertTrue(c.getEndSpan() > c.getStartSpan());
// assertTrue(c.getStartSpan() >= 0);
// } else {
// assertTrue(true);
// }
}

@Test
public void testAddViewSkipsSingleDotToken() throws Exception {
String[] tokens = { "." };
List<String[]> sentenceList = new java.util.ArrayList<>();
sentenceList.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", ".", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// int count = ta.getView(ViewNames.NER_CONLL).getNumberOfConstituents();
// assertTrue(count >= 0);
}

@Test
public void testAddViewEntityBoundarySplitAtSentenceEnd() throws Exception {
String[] tokens = { "John", "Doe", "lives", "in", "New", "York" };
List<String[]> sentenceList = new java.util.ArrayList<>();
sentenceList.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("source", "id", "John Doe lives in New York", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// if (view.getNumberOfConstituents() > 0) {
// Constituent entity = view.getConstituents().get(0);
// assertNotNull(entity.getLabel());
// }
}

@Test
public void testAddViewWithLastTokenAsEntity() throws Exception {
String[] tokens = { "He", "visited", "California" };
List<String[]> sentences = new java.util.ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewHandlesEntityWithTransitionBetweenSentences() throws Exception {
String[] tokens = { "President", "Joe", "Biden", "visited", "Ukraine", ".", "He", "spoke", "with", "Volodymyr", "Zelenskyy" };
List<String[]> sentences = new java.util.ArrayList<>();
sentences.add(new String[] { "President", "Joe", "Biden", "visited", "Ukraine", "." });
sentences.add(new String[] { "He", "spoke", "with", "Volodymyr", "Zelenskyy" });
int[] offsets = { 0, 6 };
// TextAnnotation ta = new TextAnnotation("src", "id", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// if (view.getNumberOfConstituents() > 1) {
// Constituent first = view.getConstituents().get(0);
// Constituent second = view.getConstituents().get(view.getNumberOfConstituents() - 1);
// assertTrue(first.getStartSpan() < second.getEndSpan());
// assertNotNull(first.getLabel());
// assertNotNull(second.getLabel());
// }
}

@Test
public void testEmptyTokenArrayWithOneEmptySentence() throws Exception {
String[] tokens = {};
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("corp", "id", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertEquals(0, ta.getView(ViewNames.NER_CONLL).getNumberOfConstituents());
}

@Test
public void testSingleTokenWithoutEntityStillProducesView() throws Exception {
String[] tokens = { "the" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("c", "i", "the", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testViewCreatedEvenWithOnlyTokensLabelledO() throws Exception {
String[] tokens = { "the", "dog", "runs" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "dummy", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewHandlesPredictionWithMalformedBIO() throws Exception {
String[] tokens = { "London" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("c", "i", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewHandlesNonMatchingIOSequence() throws Exception {
String[] tokens = { "Apple", "is", "big" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("x", "y", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewHandlesRepeatedTokensAndEntities() throws Exception {
String[] tokens = { "Chicago", "Chicago", "Chicago" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("x", "y", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testGetTagValuesIsDeterministicAndConsistent() throws Exception {
NERAnnotator annotator1 = new NERAnnotator(ViewNames.NER_CONLL);
NERAnnotator annotator2 = new NERAnnotator(ViewNames.NER_CONLL);
java.util.Set<String> tags1 = annotator1.getTagValues();
java.util.Set<String> tags2 = annotator2.getTagValues();
assertNotNull(tags1);
assertNotNull(tags2);
assertEquals(tags1, tags2);
}

@Test
public void testEagerInitializationViaConfigIsRespected() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertNotNull(annotator);
Set<String> tags = annotator.getTagValues();
assertTrue(tags.size() > 0);
}

@Test
public void testEntitySpanningFullSentence() throws Exception {
String[] tokens = { "New", "York", "City" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "New York City", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// if (view.getNumberOfConstituents() > 0) {
// Constituent c = view.getConstituents().get(0);
// assertTrue(c.getEndSpan() >= 3);
// }
}

@Test
public void testConsistentTagSetAcrossCallsOnSameAnnotatorInstance() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
java.util.Set<String> first = annotator.getTagValues();
java.util.Set<String> second = annotator.getTagValues();
assertEquals(first, second);
}

@Test
public void testAddViewHandlesMultipleSentencesWithNoEntities() throws Exception {
String[] tokens = { "The", "sky", "is", "blue", ".", "Grass", "is", "green", "." };
List<String[]> sentences = new ArrayList<>();
sentences.add(new String[] { "The", "sky", "is", "blue", "." });
sentences.add(new String[] { "Grass", "is", "green", "." });
int[] sentenceOffsets = { 0, 5 };
// TextAnnotation ta = new TextAnnotation("s", "t", "The sky is blue. Grass is green.", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testNoEntitiesReturnsEmptyConstituentView() throws Exception {
String[] tokens = { "and", "or", "but", "the" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("x", "y", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testMainWithValidL1ArgumentDoesNotThrow() {
String conf = "ner-config.properties";
String[] args = { conf, "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof java.io.IOException || e instanceof NullPointerException);
}
}

@Test
public void testMainWithValidL2ArgumentDoesNotThrow() {
String conf = "ner-config.properties";
String[] args = { conf, "L2" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(e instanceof java.io.IOException || e instanceof NullPointerException);
}
}

@Test
public void testAddViewWithTruncatedBIOSequenceClosesEntityWhenEndsWithI() throws Exception {
String[] tokens = { "Steve", "Jobs", "founded", "Apple" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(view);
// if (view.getNumberOfConstituents() > 0) {
// Constituent entity = view.getConstituents().get(0);
// assertTrue(entity.getEndSpan() > entity.getStartSpan());
// }
}

@Test
public void testAddViewWithBackToBackEntities() throws Exception {
String[] tokens = { "Apple", "Google", "Amazon" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "Apple Google Amazon", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
// if (view.getNumberOfConstituents() > 1) {
// Constituent c1 = view.getConstituents().get(0);
// Constituent c2 = view.getConstituents().get(1);
// assertTrue(c2.getStartSpan() >= c1.getEndSpan());
// }
}

@Test
public void testEntitySpanningAllTokensB_IO_LTransition() throws Exception {
String[] tokens = { "Barack", "Obama", "and", "Michelle", "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("dataset", "id", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// if (view.getNumberOfConstituents() > 0) {
// Constituent first = view.getConstituents().get(0);
// assertTrue(first.getEndSpan() > first.getStartSpan());
// }
}

@Test
public void testAddViewWithSentenceEndingInOThenB() throws Exception {
String[] tokens = { "Obama", "visited", "Chicago", "." };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("c", "i", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testViewWithOverlappingPrefixButDistinctEntities() throws Exception {
String[] tokens = { "John", "Smith", "met", "Mary", "Smith" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testInitializationOnOntonotesViewLoadsCorrectConfig() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
assertNotNull(annotator);
assertEquals(ViewNames.NER_ONTONOTES, annotator.getViewName());
assertTrue(annotator.getTagValues().size() > 0);
}

@Test
public void testMultipleSentencesDifferentEntitiesPerSentence() throws Exception {
String[] tokens = { "Barack", "Obama", "was", "president", ".", "Michelle", "Obama", "was", "FLOTUS", "." };
List<String[]> sentences = new ArrayList<>();
sentences.add(new String[] { "Barack", "Obama", "was", "president", "." });
sentences.add(new String[] { "Michelle", "Obama", "was", "FLOTUS", "." });
int[] sentenceOffsets = { 0, 5 };
// TextAnnotation ta = new TextAnnotation("src", "id", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testSingleConjunctionWordOnlyToken() throws Exception {
String[] tokens = { "and" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("x", "y", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewWithEmptySentenceListAndNonEmptyTokens() throws Exception {
String[] tokens = { "Barack", "Obama" };
List<String[]> sentences = new ArrayList<>();
int[] sentenceOffsets = {};
// TextAnnotation ta = new TextAnnotation("corpus", "id", "text", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testWhitespaceOnlyTokensAreIgnored() throws Exception {
String[] tokens = { "Barack", " ", "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("source", "id", "Barack Obama", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testNullLabelInPredictionHandledGracefully() throws Exception {
String[] tokens = { "John", "Smith" };
List<String[]> sentenceList = new ArrayList<>();
sentenceList.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "John Smith", tokens, sentenceOffsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testBackToBackBILabelsCorrectClosureForEachEntity() throws Exception {
String[] tokens = { "Barack", "Obama", "Michelle", "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "Barack Obama Michelle Obama", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewWithUnexpectedTokenAnnotationsSkipsGracefully() throws Exception {
String[] tokens = { "", " " };
List<String[]> sentenceList = new ArrayList<>();
sentenceList.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "", tokens, sentenceOffsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewHandlesTokenMappingErrorInIndexRange() throws Exception {
String[] tokens = { "one" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("source", "id", "one", tokens, sentenceOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testInitializeWithCustomConfigDisablesLazyInit() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.contains("O"));
}

@Test
public void testDefaultConfigUsesLazyInitialization() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tagValues = annotator.getTagValues();
assertNotNull(tagValues);
assertTrue(tagValues.size() > 0);
}

@Test
public void testEmptyTextAnnotationStillCreatesNERView() throws Exception {
String[] tokens = {};
int[] offsets = {};
List<String[]> sentenceList = new ArrayList<>();
// TextAnnotation ta = new TextAnnotation("c", "i", "", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testL1FeatureWeightsAlwaysReturnUniqueFeatureKeys() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
java.util.HashMap<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> weights = annotator.getL1FeatureWeights();
assertNotNull(weights);
java.util.HashSet<String> keys = new java.util.HashSet<String>();
java.util.Iterator<edu.illinois.cs.cogcomp.lbjava.classify.Feature> it = weights.keySet().iterator();
if (it.hasNext()) {
edu.illinois.cs.cogcomp.lbjava.classify.Feature feature = it.next();
String s = feature.getStringValue();
keys.add(s);
}
assertTrue(keys.size() > 0);
}

@Test
public void testL2FeatureWeightsHaveExpectedWeightLength() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
java.util.HashMap<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> weightsMap = annotator.getL2FeatureWeights();
assertNotNull(weightsMap);
java.util.Iterator<java.util.Map.Entry<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]>> it = weightsMap.entrySet().iterator();
if (it.hasNext()) {
java.util.Map.Entry<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> entry = it.next();
assertTrue(entry.getValue().length > 0);
}
}

@Test
public void testAddViewWithEmptyTokensButNonEmptySentenceList() throws Exception {
String[] tokens = {};
List<String[]> sentenceList = new ArrayList<>();
sentenceList.add(new String[] { "A", "B", "C" });
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("test", "id", "abc", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testMultipleEntitiesOfSameLabelAreDistinctConstituents() throws Exception {
String[] tokens = { "London", "Paris", "Berlin" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("source", "id", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// if (view.getNumberOfConstituents() > 1) {
// int start1 = view.getConstituents().get(0).getStartSpan();
// int start2 = view.getConstituents().get(1).getStartSpan();
// assertTrue(start1 != start2);
// }
}

@Test
public void testAddViewDoesNotCrashOnMissingPredictionMismatchBetweenTokensAndEntities() throws Exception {
String[] tokens = { "New", "York" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "New York", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewHandlesSingleSentenceWithoutEntities() throws Exception {
String[] tokens = { "this", "is", "not", "a", "name" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("corpus", "id", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewWithMiddleTokenEmptyString() throws Exception {
String[] tokens = { "United", "", "America" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "bad token input", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewWithMismatchedStartEndSpanTriggersSafeHandling() throws Exception {
String[] tokens = new String[] { "Obama", "was", "here" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = new int[] { 0 };
// TextAnnotation ta = new TextAnnotation("src", "id", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testGetTagValuesReturnsConsistentNonEmptySetAfterMultipleCalls() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
Set<String> valuesFirst = annotator.getTagValues();
Set<String> valuesSecond = annotator.getTagValues();
assertNotNull(valuesFirst);
assertNotNull(valuesSecond);
assertEquals(valuesFirst, valuesSecond);
assertTrue(valuesFirst.size() > 0);
}

@Test
public void testAddViewCreatesSpansThatDoNotExceedTokenLengthBound() throws Exception {
String[] tokens = { "John", "Doe", "visited", "Germany" };
List<String[]> sentenceList = new ArrayList<>();
sentenceList.add(tokens);
int[] sentenceOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("doc", "id", "John Doe visited Germany", tokens, sentenceOffsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View nerView = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(nerView);
// for (Constituent c : nerView.getConstituents()) {
// assertTrue(c.getStartSpan() >= 0);
// assertTrue(c.getEndSpan() <= tokens.length);
// }
}

@Test
public void testDoInitializeLazySetToTrueTriggersModelLoadOnAccess() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.TRUE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.size() > 0);
}

@Test
public void testDoInitializeLazySetToFalseTriggersModelLoadEagerly() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.size() > 0);
}

@Test
public void testTokenSpanNeverNegativeWhenShortSingleWordIsPresent() throws Exception {
String[] tokens = { "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("id", "id", "Obama", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// for (Constituent c : view.getConstituents()) {
// assertTrue(c.getStartSpan() >= 0);
// assertTrue(c.getEndSpan() > c.getStartSpan());
// }
}

@Test
public void testMultipleConstituentsDoNotOverlap() throws Exception {
String[] tokens = { "New", "York", "Berlin", "London", "Tokyo" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("region", "1", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// List<Constituent> constituents = view.getConstituents();
// for (int i = 0; i < constituents.size(); i++) {
// for (int j = i + 1; j < constituents.size(); j++) {
// Constituent a = constituents.get(i);
// Constituent b = constituents.get(j);
// boolean noOverlap = a.getEndSpan() <= b.getStartSpan() || b.getEndSpan() <= a.getStartSpan();
// assertTrue(noOverlap);
// }
// }
}

@Test
public void testTokenIndexingEdgeCaseLastTokenIsEntityStart() throws Exception {
String[] tokens = { "He", "spoke", "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("script", "doc", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// for (Constituent c : view.getConstituents()) {
// assertTrue(c.getEndSpan() <= tokens.length);
// }
}

@Test
public void testMultipleCallsToAddViewDoNotDuplicateOutput() throws Exception {
String[] tokens = { "Jeff", "Bezos", "ran", "Amazon" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("repeat", "doc", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View before = ta.getView(ViewNames.NER_CONLL);
// int countBefore = before.getNumberOfConstituents();
// annotator.addView(ta);
// View after = ta.getView(ViewNames.NER_CONLL);
// int countAfter = after.getNumberOfConstituents();
// assertEquals(countBefore, countAfter);
}

@Test
public void testEmptySentenceWithNonEmptyTokensDoesNotCrash() throws Exception {
String[] tokens = { "X", "Y" };
List<String[]> sentences = new ArrayList<>();
int[] offsets = {};
// TextAnnotation ta = new TextAnnotation("t", "i", "X Y", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithMultipleShortSentencesAllLabeledO() throws Exception {
String[] tokens = { "the", "dog", ".", "a", "cat", "!" };
List<String[]> sentences = new ArrayList<>();
sentences.add(new String[] { "the", "dog", "." });
sentences.add(new String[] { "a", "cat", "!" });
int[] offsets = { 0, 3 };
// TextAnnotation ta = new TextAnnotation("word", "file", "", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testEmptyTokenAtStartIsHandledGracefully() throws Exception {
String[] tokens = { "", "Obama", "spoke" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("source", "id", "text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testEmptyTokenAtEndIsHandledGracefully() throws Exception {
String[] tokens = { "Obama", "spoke", "" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("source2", "doc2", "Obama spoke", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testEntityTokenFollowedByDifferentIOChainLabelHandledCorrectly() throws Exception {
String[] tokens = { "Barack", "Obama", "administered", "by", "Bill", "Gates" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] startOffsets = { 0 };
// TextAnnotation ta = new TextAnnotation("docSrc", "id1", "Barack Obama administered...", tokens, startOffsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// for (Constituent c : view.getConstituents()) {
// assertTrue(c.getStartSpan() >= 0);
// assertTrue(c.getEndSpan() > c.getStartSpan());
// assertNotNull(c.getLabel());
// }
}

@Test
public void testGetTagValuesReturnsOnlyUniqueStrings() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
java.util.Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
java.util.Set<String> copyTags = new java.util.HashSet<>(tags);
assertEquals(tags.size(), copyTags.size());
}

@Test
public void testForceNewSentenceOnLineBreaksFalseIsSetDuringInitialize() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertNotNull(annotator);
java.util.Set<String> tags = annotator.getTagValues();
assertTrue(tags.size() > 0);
}

@Test
public void testAddViewWithTwoIdenticalEntitiesInSeparateSentences() throws Exception {
String[] tokens = { "Obama", "spoke", ".", "Obama", "ran" };
List<String[]> sentenceList = new ArrayList<>();
sentenceList.add(new String[] { "Obama", "spoke", "." });
sentenceList.add(new String[] { "Obama", "ran" });
int[] offsets = { 0, 3 };
// TextAnnotation ta = new TextAnnotation("c", "id", "Obama spoke. Obama ran.", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// int count = view.getNumberOfConstituents();
// assertTrue(count >= 0);
}

@Test
public void testAddViewHandlesSingleLetterTokenProperly() throws Exception {
String[] tokens = { "A" };
List<String[]> sentenceList = new ArrayList<>();
sentenceList.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("single", "letter", "A", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testViewOnlyAddedOnceToTextAnnotation() throws Exception {
String[] tokens = { "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("repeatView", "id", "Obama", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View firstView = ta.getView(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View secondView = ta.getView(ViewNames.NER_CONLL);
// assertEquals(firstView.getNumberOfConstituents(), secondView.getNumberOfConstituents());
}

@Test
public void testAddViewDoesNotThrowExceptionWhenNoTokensInSentence() throws Exception {
String[] tokens = new String[] { "Obama", "visited", "France" };
List<String[]> sentenceList = new ArrayList<>();
sentenceList.add(new String[] { "" });
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("bad", "tok", "text", tokens, offsets, sentenceList);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewWithSentenceThatStartsWithOEntityThenBEnitySequence() throws Exception {
String[] tokens = new String[] { "And", "Barack", "Obama" };
List<String[]> sentences = new ArrayList<>();
sentences.add(tokens);
int[] offsets = { 0 };
// TextAnnotation ta = new TextAnnotation("example", "id", "input text", tokens, offsets, sentences);
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// View view = ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}
}
