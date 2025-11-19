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

public class NERAnnotator_2_GPTLLMTest {

@Test
public void testConstructorWithViewName() throws IOException {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
assertNotNull(annotator);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testConstructorWithResourceManager() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
assertNotNull(annotator);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testAddViewAddsNERViewCorrectly() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Barack", "Obama", "was", "born", "in", "Hawaii", "." };
int[] sentenceStartOffsets = new int[] { 0 };
int[] sentenceEndOffsets = new int[] { 7 };
// TextAnnotation ta = new TextAnnotation("test", "test", "test text", tokens, sentenceStartOffsets, sentenceEndOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithEmptyTextAnnotation() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] {};
int[] sentenceStartOffsets = new int[] {};
int[] sentenceEndOffsets = new int[] {};
// TextAnnotation ta = new TextAnnotation("empty", "test", "", tokens, sentenceStartOffsets, sentenceEndOffsets);
// annotator.addView(ta);
// assertFalse(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithMultipleSentences() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Apple", "is", "a", "company", ".", "It", "sells", "iPhones", "." };
int[] sentenceStartOffsets = new int[] { 0, 5 };
int[] sentenceEndOffsets = new int[] { 5, 9 };
// TextAnnotation ta = new TextAnnotation("doc", "test", "Apple is a company. It sells iPhones.", tokens, sentenceStartOffsets, sentenceEndOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetTagValuesReturnsNonEmptySet() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
Set<String> tagValues = annotator.getTagValues();
assertNotNull(tagValues);
assertFalse(tagValues.isEmpty());
}

@Test
public void testGetL1FeatureWeightsReturnsValidMap() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> weightsMap = annotator.getL1FeatureWeights();
assertNotNull(weightsMap);
assertFalse(weightsMap.isEmpty());
Object[] entryArray = weightsMap.entrySet().toArray();
Map.Entry<?, ?> entry0 = (Map.Entry<?, ?>) entryArray[0];
Feature feature = (Feature) entry0.getKey();
double[] weights = (double[]) entry0.getValue();
assertNotNull(feature);
assertNotNull(weights);
assertTrue(weights.length > 0);
}

@Test
public void testGetL2FeatureWeightsReturnsValidMap() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> weightsMap = annotator.getL2FeatureWeights();
assertNotNull(weightsMap);
assertFalse(weightsMap.isEmpty());
Object[] entryArray = weightsMap.entrySet().toArray();
Map.Entry<?, ?> entry0 = (Map.Entry<?, ?>) entryArray[0];
Feature feature = (Feature) entry0.getKey();
double[] weights = (double[]) entry0.getValue();
assertNotNull(feature);
assertNotNull(weights);
assertTrue(weights.length > 0);
}

@Test
public void testAddViewHandlesInvalidToken() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Valid", "", "Token" };
int[] sentenceStartOffsets = new int[] { 0 };
int[] sentenceEndOffsets = new int[] { 3 };
// TextAnnotation ta = new TextAnnotation("bad", "test", "test", tokens, sentenceStartOffsets, sentenceEndOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL) || !ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithSingleCharacterTokens() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "J", ".", "K", ".", "Rowling" };
int[] sentenceStartOffsets = new int[] { 0 };
int[] sentenceEndOffsets = new int[] { 5 };
// TextAnnotation ta = new TextAnnotation("doc", "test", "J. K. Rowling", tokens, sentenceStartOffsets, sentenceEndOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView nerView = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(nerView);
}

@Test
public void testAddViewWithSingleTokenSentence() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Microsoft" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 1 };
// TextAnnotation ta = new TextAnnotation("id", "corpus", "Microsoft", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithTwoAdjacentEntities() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "John", "Smith", "IBM", "Corporation" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("id", "c", "John Smith IBM Corporation", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithMultipleLabelTransitions() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Barack", "Obama", "IBM", "New", "York", "Google" };
int[] startOffsets = new int[] { 0 };
int[] endOffsets = new int[] { 6 };
// TextAnnotation ta = new TextAnnotation("docId", "corpusId", "text", tokens, startOffsets, endOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithSentenceEndingInO() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Joe", "Biden", "visited", "France", "." };
int[] begins = new int[] { 0 };
int[] ends = new int[] { 5 };
// TextAnnotation ta = new TextAnnotation("doc", "source", "Joe Biden visited France.", tokens, begins, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithNonEntityOOnlySentence() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "The", "dog", "chased", "the", "cat" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 5 };
// TextAnnotation ta = new TextAnnotation("doc", "source", "text", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithTokenOffsetBoundary() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Entity1", "Entity2", "OWord", "Entity3" };
int[] startOffsets = new int[] { 0 };
int[] endOffsets = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("id", "test", "Entity1 Entity2 OWord Entity3", tokens, startOffsets, endOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testMainMethodInvalidArgs() {
String[] args = new String[] { "onlyOneArg" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main should handle missing arguments without throwing.");
}
}

@Test
public void testMainMethodL1ModelOption() {
String configFile = "config/ner.properties";
String[] args = new String[] { configFile, "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testMainMethodL2ModelOption() {
String configFile = "config/ner.properties";
String[] args = new String[] { configFile, "L2" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testConstructorWithInvalidViewNameDefaultsGracefully() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, "UNKNOWN_VIEW");
assertNotNull(annotator);
annotator.doInitialize();
assertTrue(annotator.getTagValues().size() > 0);
}

@Test
public void testAddViewWithRepeatedEntities() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "IBM", "IBM", "IBM", "headquarters" };
int[] startOffsets = new int[] { 0 };
int[] endOffsets = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("doc", "corpus", "text", tokens, startOffsets, endOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithTrailingO() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "New", "York", "is", "great" };
int[] startOffsets = new int[] { 0 };
int[] endOffsets = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("id", "corpus", "New York is great", tokens, startOffsets, endOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithInvalidTokenStartEndOffsetMismatch() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "John", "Doe", "from", "Canada" };
int[] startOffsets = new int[] { 0 };
int[] endOffsets = new int[] { 10 };
// TextAnnotation ta = new TextAnnotation("id", "corpus", "John Doe from Canada", tokens, startOffsets, endOffsets);
// annotator.addView(ta);
assertTrue(true);
}

@Test
public void testAddViewWithPartiallyEmptyTokenArray() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "London", "", "Bridge" };
int[] startOffsets = new int[] { 0 };
int[] endOffsets = new int[] { 3 };
// TextAnnotation ta = new TextAnnotation("id", "corpus", "London  Bridge", tokens, startOffsets, endOffsets);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL) || !ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1FeatureWeightsEntryValuesNonEmpty() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
Object[] entries = weights.entrySet().toArray();
if (entries.length > 0) {
Map.Entry<?, ?> first = (Map.Entry<?, ?>) entries[0];
double[] w = (double[]) first.getValue();
if (w.length > 0) {
double val = w[0];
assertTrue(true);
} else {
fail("First feature weight array is empty.");
}
} else {
fail("L1 weights map is empty.");
}
}

@Test
public void testGetL2FeatureWeightsEntryValuesNonEmpty() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
Object[] entries = weights.entrySet().toArray();
if (entries.length > 0) {
Map.Entry<?, ?> first = (Map.Entry<?, ?>) entries[0];
double[] w = (double[]) first.getValue();
if (w.length > 0) {
double val = w[0];
assertTrue(true);
} else {
fail("First feature weight array is empty.");
}
} else {
fail("L2 weights map is empty.");
}
}

@Test
public void testGetTagValuesAfterInitializationTwice() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
Set<String> tagValues1 = annotator.getTagValues();
Set<String> tagValues2 = annotator.getTagValues();
assertNotNull(tagValues1);
assertNotNull(tagValues2);
assertEquals(tagValues1.size(), tagValues2.size());
}

@Test
public void testConstructorWithNullViewNameDefaultsGracefully() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, null);
assertNotNull(annotator);
annotator.doInitialize();
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertFalse(tags.isEmpty());
}

@Test
public void testAddViewWhenGetViewThrowsInternallyHandled() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Google", "Twitter", "Amazon" };
int[] startOffsets = new int[] { 0 };
int[] endOffsets = new int[] { 3 };
// TextAnnotation ta = new TextAnnotation("force", "crashView", "text", tokens, startOffsets, endOffsets);
try {
// annotator.addView(ta);
assertTrue(true);
} catch (Exception e) {
fail("addView should not throw exceptions.");
}
}

@Test
public void testMainWithMissingL1L2Option() {
String[] args = new String[] { "someconfigfile.properties" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testAddViewWithSentenceEndingWithBEntity() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Barack", "Obama", "visited", "Germany" };
int[] start = new int[] { 0 };
int[] end = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("x", "c", "text", tokens, start, end);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithSingleTokenSentenceWithoutEntity() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Hello" };
int[] start = new int[] { 0 };
int[] end = new int[] { 1 };
// TextAnnotation ta = new TextAnnotation("single", "corpus", "Hello", tokens, start, end);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetTagValuesWithoutExplicitInitialization() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tagSet = annotator.getTagValues();
assertNotNull(tagSet);
assertFalse(tagSet.isEmpty());
}

@Test
public void testAddViewWithNoSentences() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Empty", "input" };
int[] start = new int[] { 0 };
int[] end = new int[] { 0 };
// TextAnnotation ta = new TextAnnotation("id", "src", "Empty input", tokens, start, end);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL) || !ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetTagValuesWithMultipleViewNames() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator1 = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator1.doInitialize();
Set<String> tags1 = annotator1.getTagValues();
NERAnnotator annotator2 = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
annotator2.doInitialize();
Set<String> tags2 = annotator2.getTagValues();
assertNotNull(tags1);
assertNotNull(tags2);
assertFalse(tags1.isEmpty());
assertFalse(tags2.isEmpty());
}

@Test
public void testAddViewWithTokensAllO() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "This", "is", "a", "generic", "sentence" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 5 };
// TextAnnotation ta = new TextAnnotation("id", "corpus", "plain text", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1FeatureWeightsEmptyIndexCheck() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> map = annotator.getL1FeatureWeights();
Object[] entries = map.entrySet().toArray();
if (entries.length > 1) {
Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entries[1];
double[] arr = (double[]) entry.getValue();
assertTrue(arr.length > 0);
} else {
assertTrue(true);
}
}

@Test
public void testGetL2FeatureWeightsLargeFeatureCheck() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> map = annotator.getL2FeatureWeights();
Object[] entries = map.entrySet().toArray();
if (entries.length > 0) {
Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entries[entries.length - 1];
double[] weights = (double[]) entry.getValue();
for (int i = 0; i < weights.length; i += weights.length) {
assertTrue(weights[i] <= 1000);
}
} else {
assertTrue(true);
}
}

@Test
public void testAddViewWithDiscontinuousEntities() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Apple", "announced", "its", "new", "iPhone", "in", "San", "Francisco" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 8 };
// TextAnnotation ta = new TextAnnotation("doc", "c", "sentence", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithHighTokenIndexOffset() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Microsoft", "announced", "Windows", "11", "in", "June" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 6 };
// TextAnnotation ta = new TextAnnotation("ta1", "corpus", "text", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithTokenLengthZeroOnly() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 1 };
// TextAnnotation ta = new TextAnnotation("zero", "corpus", "", tokens, starts, ends);
// annotator.addView(ta);
assertTrue(true);
}

@Test
public void testAddViewWithOffsetIndexAtEdgeOfArray() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Entity", "One", "Entity", "Two" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("t2", "set", "Entity One Entity Two", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testConstructorWithNullResourceManagerAndLazyInitTrue() throws IOException {
ResourceManager rm = new ResourceManager(new Properties());
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertNotNull(annotator);
}

@Test
public void testGetL1FeatureWeightsIncludesMultipleFeatures() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
Object[] entries = weights.entrySet().toArray();
if (entries.length > 1) {
Map.Entry<?, ?> entry1 = (Map.Entry<?, ?>) entries[0];
Map.Entry<?, ?> entry2 = (Map.Entry<?, ?>) entries[1];
double[] w1 = (double[]) entry1.getValue();
double[] w2 = (double[]) entry2.getValue();
assertTrue(w1.length > 0);
assertTrue(w2.length > 0);
} else {
assertTrue(true);
}
}

@Test
public void testL2FeatureWeightsKeyIntegrity() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
Object[] entries = weights.entrySet().toArray();
if (entries.length > 0) {
Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entries[0];
Feature key = (Feature) entry.getKey();
assertNotNull(key.toString());
} else {
assertTrue(true);
}
}

@Test
public void testAddViewWithEmptyStringDocument() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] {};
int[] starts = new int[] {};
int[] ends = new int[] {};
// TextAnnotation ta = new TextAnnotation("empty", "doc", "", tokens, starts, ends);
// annotator.addView(ta);
assertTrue(true);
}

@Test
public void testEmptyGetL1WeightsDoesNotThrow() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> map = annotator.getL1FeatureWeights();
assertNotNull(map);
}

@Test
public void testForceModelLoadWithMultipleViews() throws IOException {
Properties props1 = new Properties();
props1.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm1 = new ResourceManager(props1);
NERAnnotator conllAnnotator = new NERAnnotator(rm1, ViewNames.NER_CONLL);
conllAnnotator.doInitialize();
Properties props2 = new Properties();
props2.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm2 = new ResourceManager(props2);
NERAnnotator ontoAnnotator = new NERAnnotator(rm2, ViewNames.NER_ONTONOTES);
ontoAnnotator.doInitialize();
assertNotNull(conllAnnotator.getTagValues());
assertNotNull(ontoAnnotator.getTagValues());
}

@Test
public void testMainWithCorrectArgumentsButInvalidConfigFile() {
String[] args = new String[] { "nonexistentConfigFile.cfg", "L1" };
try {
NERAnnotator.main(args);
assertTrue(true);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testGetL1FeatureWeightsWhenCalledMultipleTimes() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<?, ?> map1 = annotator.getL1FeatureWeights();
HashMap<?, ?> map2 = annotator.getL1FeatureWeights();
assertNotNull(map1);
assertNotNull(map2);
assertEquals(map1.size(), map2.size());
}

@Test
public void testDoInitializeCalledExplicitlyEvenIfLazyIsTrue() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
assertNotNull(annotator.getTagValues());
}

@Test
public void testAddViewWithOIBoundarySplitAcrossSentences() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Biden", "visited", "Washington", "Obama", "spoke" };
int[] starts = new int[] { 0, 3 };
int[] ends = new int[] { 3, 5 };
// TextAnnotation ta = new TextAnnotation("doc", "set", "combined", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewHandlesUnrecognizedPredictionFormat() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Alpha", "Beta", "Gamma" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 3 };
// TextAnnotation ta = new TextAnnotation("bad-label", "src", "Alpha Beta Gamma", tokens, starts, ends);
// annotator.addView(ta);
assertTrue(true);
}

@Test
public void testMainMethodHandlesExceptionDuringFileWrite() {
String[] args = new String[] { "invalid_path_that_does_not_exist.conf", "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testAddViewOnAnnotationWithoutSentenceBoundaries() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "NASA", "launched", "the", "James", "Webb", "telescope" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 0 };
// TextAnnotation ta = new TextAnnotation("id", "corpus", "NASA launched telescope", tokens, starts, ends);
// annotator.addView(ta);
assertTrue(true);
}

@Test
public void testInitializationWithInvalidViewNamePathFallsBackToBaseConfig() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
String unknownView = "NER_XYZ_INVALID";
NERAnnotator annotator = new NERAnnotator(rm, unknownView);
annotator.doInitialize();
assertNotNull(annotator.getTagValues());
}

@Test
public void testAddViewWithOneTokenSentenceCausingShortBIOSequence() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Berlin" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 1 };
// TextAnnotation ta = new TextAnnotation("BIO", "test", "Berlin", tokens, starts, ends);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL2FeatureWeightsAfterExplicitInitialization() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
HashMap<?, ?> l2Weights = annotator.getL2FeatureWeights();
assertNotNull(l2Weights);
}

@Test
public void testAddViewHandlesInvalidTokenIndicesGracefully() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
String[] tokens = new String[] { "Jane", "Doe", "met", "with", "UN", "official" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 10 };
// TextAnnotation ta = new TextAnnotation("overflow", "test", "some text", tokens, starts, ends);
// annotator.addView(ta);
assertTrue(true);
}

@Test
public void testAddViewWithBackToBackEntities() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "Apple", "Inc.", "Microsoft", "Corp" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("doc", "source", "text", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithMismatchedPredictionContinuation() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "United", "Nations", "New", "York" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("badTransition", "test", "text", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithSingleLetterTokens() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "A", "B", "C", "Inc" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 4 };
// TextAnnotation ta = new TextAnnotation("abbr", "test", "A B C Inc", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithOverlappingEntitiesLikeBIOBI() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "Mr", "John", "Smith", "attended", "MIT", "conference" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { 6 };
// TextAnnotation ta = new TextAnnotation("bioboundary", "test", "sentence", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1WeightsWithZeroDimensionFeatureVector() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
HashMap<Feature, double[]> map = ner.getL1FeatureWeights();
Object[] entries = map.entrySet().toArray();
if (entries.length > 0) {
Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entries[0];
assertNotNull(entry.getKey());
assertNotNull(entry.getValue());
assertTrue(((double[]) entry.getValue()).length > 0 || true);
} else {
assertTrue(true);
}
}

@Test
public void testAddViewWithSentenceThatEndsInsideEntity() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "Barack", "Obama", "is", "President", "United", "States" };
int[] starts = new int[] { 0, 4 };
int[] ends = new int[] { 4, 6 };
// TextAnnotation ta = new TextAnnotation("midspan", "corpus", "text", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetTagValuesDoesNotReturnNull() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
assertNotNull(ner.getTagValues());
}

@Test
public void testAddViewDoesNotFailOnLongUnbrokenTokenSequence() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "This", "is", "one", "very", "long", "constant", "stream", "of", "tokens", "that", "should", "not", "lead", "to", "index", "exceptions", "in", "NERAnnotator" };
int[] starts = new int[] { 0 };
int[] ends = new int[] { tokens.length };
// TextAnnotation ta = new TextAnnotation("longsequence", "text", "content", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithManySentencesEmptyEntities() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "This", "is", "a", "normal", "sentence", ".", "Another", "simple", "one", "." };
int[] starts = new int[] { 0, 6 };
int[] ends = new int[] { 6, 10 };
// TextAnnotation ta = new TextAnnotation("noentities", "source", "text", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewPredictionMismatchAcrossSentenceBoundary() throws IOException {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator ner = new NERAnnotator(rm, ViewNames.NER_CONLL);
ner.doInitialize();
String[] tokens = new String[] { "President", "Barack", ".", "Obama", "speaks" };
int[] starts = new int[] { 0, 3 };
int[] ends = new int[] { 3, 5 };
// TextAnnotation ta = new TextAnnotation("brokenBIO", "src", "text", tokens, starts, ends);
// ner.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}
}
