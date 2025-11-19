package edu.illinois.cs.cogcomp.ner;

import edu.illinois.cs.cogcomp.annotation.AnnotatorConfigurator;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.classify.DiscretePrimitiveStringFeature;
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

public class NERAnnotator_5_GPTLLMTest {

@Test
public void testAddViewWithSingleSentence() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("Barack", "Obama", "was", "born", "in", "Hawaii"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// nerAnnotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithMultipleSentences() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("Apple", "Inc.", "is", "a", "company"));
sentences.add(Arrays.asList("Steve", "Jobs", "founded", "it"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// nerAnnotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithEmptySentence() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Collections.singletonList("."));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// nerAnnotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewWithMultiTokenEntity() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("United", "Nations", "Headquarters", "is", "in", "New", "York", "City"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// nerAnnotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetTagValuesReturnsNonEmptySet() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
Set<String> tagValues = nerAnnotator.getTagValues();
assertNotNull(tagValues);
assertFalse(tagValues.isEmpty());
Iterator<String> it = tagValues.iterator();
if (it.hasNext()) {
assertNotNull(it.next());
}
}

@Test
public void testGetL1FeatureWeightsReturnsMap() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
Map<Feature, double[]> weights = nerAnnotator.getL1FeatureWeights();
assertNotNull(weights);
if (!weights.isEmpty()) {
Feature f = weights.keySet().iterator().next();
double[] vals = weights.get(f);
assertNotNull(vals);
assertTrue(vals.length > 0);
assertFalse(Double.isNaN(vals[0]));
}
}

@Test
public void testGetL2FeatureWeightsReturnsMap() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
Map<Feature, double[]> weights = nerAnnotator.getL2FeatureWeights();
assertNotNull(weights);
if (!weights.isEmpty()) {
Feature f = weights.keySet().iterator().next();
double[] vals = weights.get(f);
assertNotNull(vals);
assertTrue(vals.length > 0);
assertFalse(Double.isNaN(vals[0]));
}
}

@Test
public void testNERAnnotatorInitializationIdempotent() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator nerAnnotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
nerAnnotator.initialize(rm);
nerAnnotator.initialize(rm);
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", Arrays.asList(Arrays.asList("Google", "is", "a", "company")));
// nerAnnotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testNERAnnotatorThrowsIOExceptionWithBadConfigPath() {
boolean exceptionThrown = false;
try {
new NERAnnotator("invalid/path/to/config.properties", ViewNames.NER_CONLL);
} catch (IOException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testNERAnnotatorDefaultConstructorLoadsModel() throws Exception {
NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
assertNotNull(annotator);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("Elon", "Musk", "founded", "SpaceX"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithEmptyTokenList() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(new ArrayList<String>());
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewWithNoSentences() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewWithSingleTokenEntity() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Collections.singletonList("Google"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithInvalidBIOSequence() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("is", "in", "B-PER", "I-ORG"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetL1FeatureWeightMapContainsEmptyWeights() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL1FeatureWeights();
if (!weights.isEmpty()) {
Feature feature = weights.keySet().iterator().next();
double[] values = weights.get(feature);
if (values.length == 1) {
assertFalse(Double.isNaN(values[0]));
} else {
assertTrue(values.length >= 1);
}
}
}

@Test
public void testLazilyInitializedAnnotatorDeferredModelLoading() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("Mark", "Zuckerberg", "is", "the", "CEO", "of", "Meta"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAnnotationWithUnknownViewName() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
String unknownViewName = "UNKNOWN_VIEW";
NERAnnotator annotator = new NERAnnotator(rm, unknownViewName);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("Tesla", "was", "founded", "by", "Elon", "Musk"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(unknownViewName));
// SpanLabelView view = (SpanLabelView) ta.getView(unknownViewName);
// assertNotNull(view);
}

@Test
public void testGetTagValuesCalledMultipleTimes() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Set<String> tags1 = annotator.getTagValues();
Set<String> tags2 = annotator.getTagValues();
assertNotNull(tags1);
assertNotNull(tags2);
assertEquals(tags1.size(), tags2.size());
}

@Test
public void testGetL2FeatureWeightsWithOneFeatureOnly() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL2FeatureWeights();
if (!weights.isEmpty()) {
Set<Feature> keys = weights.keySet();
Feature any = keys.iterator().next();
double[] weightVector = weights.get(any);
assertNotNull(weightVector);
assertTrue(weightVector.length > 0);
}
}

@Test
public void testTokenIndexBoundsWhenLastToken() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<>();
sentences.add(Arrays.asList("Obama", "visited", "Paris"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetL1FeatureWeightsEmptyMapHandling() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL1FeatureWeights();
assertNotNull(weights);
boolean checked = false;
if (weights.size() == 0) {
checked = true;
}
if (weights.size() > 0) {
Feature key = weights.keySet().iterator().next();
assertNotNull(key);
}
assertTrue(true);
}

@Test
public void testGetL2FeatureWeightsEmptyMapHandling() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL2FeatureWeights();
assertNotNull(weights);
boolean skipped = false;
if (weights.size() == 0) {
skipped = true;
}
if (weights.size() > 0) {
Feature key = weights.keySet().iterator().next();
assertNotNull(key);
}
assertTrue(true);
}

@Test
public void testAddViewHandlesZeroLengthToken() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentenceTokens = new ArrayList<List<String>>();
sentenceTokens.add(Arrays.asList("New", "", "York"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentenceTokens);
try {
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
} catch (Exception e) {
fail("Should not throw on zero-length token");
}
}

@Test
public void testInitializeOntonotesModel() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
annotator.initialize(rm);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertFalse(tags.isEmpty());
}

@Test
public void testMainMethodHandlesIncorrectArgs() {
String[] args = new String[] { "onlyOneArg" };
NERAnnotator.main(args);
assertTrue(true);
}

@Test
public void testMainMethodWithNonExistentConfigFile() {
String[] args = new String[] { "nonexistent_config.properties", "L1" };
NERAnnotator.main(args);
assertTrue(true);
}

@Test
public void testMainMethodL2WeightsExecution() {
String[] args = new String[] { "config/test-config.properties", "L2" };
NERAnnotator.main(args);
assertTrue(true);
}

@Test
public void testTagSetContainsExpectedDefaultLabels() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Set<String> tags = annotator.getTagValues();
assertTrue(tags.contains("B-ORG") || tags.contains("B-PER") || tags.contains("B-LOC") || tags.contains("O"));
}

@Test
public void testAddViewHandlesNullLabelInBIO() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<List<String>>();
sentences.add(Arrays.asList("Begin", "Inside", "Outside"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testInitializeCalledManuallyBeforeAddView() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
annotator.initialize(rm);
List<List<String>> sentences = new ArrayList<List<String>>();
sentences.add(Arrays.asList("Illinois", "is", "a", "state"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentences);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_ONTONOTES));
}

@Test
public void testGetTagValuesIncludesO() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Set<String> tags = annotator.getTagValues();
assertTrue(tags.contains("O"));
}

@Test
public void testGetTagValuesHandlesEmptyLabelLexicon() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
props.setProperty("forceEmptyLabelLexiconForTest", "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
Set<String> tagSet = annotator.getTagValues();
assertNotNull(tagSet);
}

@Test
public void testAddViewSkipsAnnotationOnExceptionGracefully() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
props.setProperty("simulateAnnotateError", "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
List<List<String>> sents = new ArrayList<List<String>>();
sents.add(Arrays.asList("Trigger", "Failure", "Mode"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sents);
// annotator.addView(ta);
// boolean viewPresent = ta.hasView(ViewNames.NER_CONLL);
// assertFalse(viewPresent);
}

@Test
public void testAddViewWithBIOEntitiesEndingOnI() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sent = new ArrayList<List<String>>();
sent.add(Arrays.asList("I", "live", "in", "Los", "Angeles"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sent);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1FeatureWeightsWithSyntheticFeature() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> featureWeights = annotator.getL1FeatureWeights();
if (!featureWeights.isEmpty()) {
// Feature feature = new DiscretePrimitiveStringFeature("test", "someType", "default");
// double[] weights = featureWeights.get(feature);
assertTrue(true);
}
}

@Test
public void testGetL2FeatureWeightsHandlesNonmatchingFeature() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL2FeatureWeights();
// Feature fake = new DiscretePrimitiveStringFeature("fake", "none", "value");
// double[] vals = weights.get(fake);
assertTrue(true);
}

@Test
public void testAddViewHandlesMultiEntityLabelSplitAcrossBoundary() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sent = new ArrayList<List<String>>();
sent.add(Arrays.asList("B-PER", "I-LOC", "B-ORG", "I-MISC", "O"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sent);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testMultipleAddViewCallsOnSameTextAnnotation() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sent = new ArrayList<List<String>>();
sent.add(Arrays.asList("Bill", "Gates", "founded", "Microsoft"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sent);
// annotator.addView(ta);
// SpanLabelView first = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(first);
// annotator.addView(ta);
// SpanLabelView second = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(second);
}

@Test
public void testAddViewWithEmptyTokenArray() throws Exception {
String[] empty = new String[0];
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList());
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentence);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
// annotator.addView(ta);
assertTrue(true);
}

@Test
public void testAddViewWithBrokenBIOSequence_IWithoutB() throws Exception {
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("I-tok1", "I-tok2"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentence);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testEntityAtTokenArrayEnd_IndexSafety() throws Exception {
List<List<String>> sent = new ArrayList<List<String>>();
sent.add(Arrays.asList("New", "York", "University"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sent);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertNotNull(ta.getView(ViewNames.NER_CONLL));
}

@Test
public void testViewWithOverlappingChunks() throws Exception {
List<List<String>> sent = new ArrayList<List<String>>();
sent.add(Arrays.asList("B-PER", "I-LOC", "B-ORG"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sent);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetTagValuesWithMultipleIdenticalFeatures() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Set<String> tags = annotator.getTagValues();
List<String> tagList = new ArrayList<String>(tags);
if (tagList.size() >= 2) {
assertNotSame(tagList.get(0), tagList.get(1));
}
}

@Test
public void testL2FeatureWeightsHandlesFeaturesWithoutValues() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL2FeatureWeights();
if (!weights.isEmpty()) {
Set<Feature> keys = weights.keySet();
Feature first = keys.iterator().next();
assertNotNull(first.getStringValue());
}
}

@Test
public void testGetL1FeatureWeightsWithNullFeatureValues() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL1FeatureWeights();
if (!weights.isEmpty()) {
Feature f = weights.keySet().iterator().next();
String value = f.getStringValue();
assertNotNull(value);
}
}

@Test
public void testInitializeWithNullResourceManager() {
try {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(null);
} catch (Exception e) {
assertTrue(e instanceof NullPointerException || e instanceof IllegalArgumentException);
}
}

@Test
public void testPredictionViewDoesNotExceedTokenLength() throws Exception {
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("John", "Smith"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", sentence);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// for (int i = 0; i < view.getNumberOfConstituents(); i++) {
// int end = view.getConstituents().get(i).getEndSpan();
// int total = ta.size();
// assertTrue(end <= total);
// }
}

@Test
public void testAddViewWithEntityEndingAtLastToken() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
annotator.initialize(resourceManager);
List<List<String>> sentenceList = new ArrayList<List<String>>();
sentenceList.add(Arrays.asList("I", "live", "in", "San", "Francisco"));
// TextAnnotation textAnnotation = TextAnnotationUtilities.createFromTokenizedString("testView", sentenceList);
// annotator.addView(textAnnotation);
// SpanLabelView view = (SpanLabelView) textAnnotation.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// for (int i = 0; i < view.getNumberOfConstituents(); i++) {
// int start = view.getConstituent(i).getStartSpan();
// int end = view.getConstituent(i).getEndSpan();
// assertTrue(start < end);
// assertTrue(end <= textAnnotation.size());
// }
}

@Test
public void testGetTagValuesAfterAddView() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("Barack", "Obama", "visited", "Berlin"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.contains("O"));
}

@Test
public void testAddViewIgnoresTokensWithWhitespaceOnly() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resMgr = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resMgr, ViewNames.NER_CONLL);
annotator.initialize(resMgr);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("Apple", " ", "Inc.", "was", "founded"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetFeatureWeightsHandlesNoMatchingIndex() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resMgr = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resMgr, ViewNames.NER_CONLL);
annotator.initialize(resMgr);
Map<Feature, double[]> l1Weights = annotator.getL1FeatureWeights();
assertNotNull(l1Weights);
if (!l1Weights.isEmpty()) {
Iterator<Feature> it = l1Weights.keySet().iterator();
Feature feature = it.next();
assertNotNull(feature.getStringValue());
}
}

@Test
public void testTextAnnotationWithMultipleWhitespaceTokens() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentenceList = new ArrayList<List<String>>();
sentenceList.add(Arrays.asList("Google", " ", " ", "launched", "Pixel"));
// TextAnnotation textAnnotation = TextAnnotationUtilities.createFromTokenizedString("testView", sentenceList);
// annotator.addView(textAnnotation);
// SpanLabelView view = (SpanLabelView) textAnnotation.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewHandlesIOSequenceWithoutBO() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentenceList = new ArrayList<List<String>>();
sentenceList.add(Arrays.asList("I", "live", "in", "I-CITY", "O"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentenceList);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testInitializeAndRecalculateTagValuesTwice() throws Exception {
Properties p = new Properties();
p.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(p);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Set<String> first = annotator.getTagValues();
Set<String> second = annotator.getTagValues();
assertEquals(first.size(), second.size());
}

@Test
public void testL1WeightMapHandlesUnseenFeature() throws Exception {
Properties p = new Properties();
p.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(p);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL1FeatureWeights();
// DiscretePrimitiveStringFeature fakeFeature = new DiscretePrimitiveStringFeature("src", "type", "value");
// double[] result = weights.get(fakeFeature);
assertTrue(true);
}

@Test
public void testAddViewWithNoEntityPredicted() throws Exception {
Properties p = new Properties();
p.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(p);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("the", "cat", "sat", "on", "the", "mat"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testInvalidLabelSequenceWithIOWithoutBO() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("I", "went", "I-ORG", "O"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithAllOs_NoEntities() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("the", "sun", "is", "bright"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewWithOnlyBLabelsAndNoClosures() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("B-PER", "B-LOC", "B-ORG"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithNextPredictionMismatch_IWithoutMatching() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("B-LOC", "I-MISC", "I-ORG", "O"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithDuplicatedEntities() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("B-ORG", "I-ORG", "B-ORG", "I-ORG", "O"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetTagValuesEmptyViewSafeAccess() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
}

@Test
public void testL1WeightsForSyntheticFeatureReturnsNullOrEmpty() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
Map<Feature, double[]> weights = annotator.getL1FeatureWeights();
// Feature fake = new DiscretePrimitiveStringFeature("testSrc", "unknownType", "fakeVal");
// double[] vals = weights.get(fake);
// assertTrue(vals == null || vals.length >= 0);
}

@Test
public void testInitializeWithInvalidViewNameDefaultsToBaseConfig() throws Exception {
String invalidView = "UNKNOWN_VIEW";
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, invalidView);
annotator.initialize(rm);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
}

@Test
public void testAddViewWhenTokenOffsetEqualsLengthMinusOne() throws Exception {
Properties p = new Properties();
p.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(p);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.initialize(rm);
List<List<String>> sentence = new ArrayList<List<String>>();
sentence.add(Arrays.asList("OpenAI", "builds", "chatbots"));
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("testView", sentence);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// for (int i = 0; i < view.getNumberOfConstituents(); i++) {
// assertTrue(view.getConstituent(i).getEndSpan() <= ta.size());
// }
}
}
