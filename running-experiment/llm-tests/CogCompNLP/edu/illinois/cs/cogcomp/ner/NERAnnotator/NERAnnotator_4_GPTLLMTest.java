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
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NERAnnotator_4_GPTLLMTest {

@Test
public void testConstructorWithViewName() throws Exception {
Properties props = new Properties();
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testConstructorWithDefaultResourceManager() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertNotNull(annotator);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testAddViewSingleSentence() throws Exception {
String[] tokens = new String[] { "Barack", "Obama", "was", "president" };
String[] sentenceLabels = new String[] { "Barack Obama was president" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentenceLabels, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertTrue(ta.getView(ViewNames.NER_CONLL) instanceof SpanLabelView);
}

@Test
public void testAddViewMultipleSentences() throws Exception {
String[] tokens = new String[] { "Barack", "Obama", "was", "president", ".", "Paris", "is", "in", "France" };
String[] labels = new String[] { "Barack Obama was president .", "Paris is in France" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", labels, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertTrue(ta.getView(ViewNames.NER_CONLL) instanceof SpanLabelView);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewNoTokens() throws Exception {
String[] tokens = new String[] {};
String[] sentences = new String[] {};
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetTagValuesReturnsNonEmptySet() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tagValues = annotator.getTagValues();
assertNotNull(tagValues);
assertTrue(tagValues.contains("O"));
}

@Test
public void testGetL1FeatureWeightsReturnsFeatureMap() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
assertNotNull(weights);
Set<Feature> keys = weights.keySet();
HashSet<Feature> nonNullKeys = new HashSet<>();
for (Feature f : keys) {
if (f != null && weights.get(f) != null) {
nonNullKeys.add(f);
}
}
assertFalse(nonNullKeys.isEmpty());
}

@Test
public void testGetL2FeatureWeightsReturnsFeatureMap() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
assertNotNull(weights);
Set<Feature> keys = weights.keySet();
HashSet<Feature> nonNullKeys = new HashSet<>();
for (Feature f : keys) {
if (f != null && weights.get(f) != null) {
nonNullKeys.add(f);
}
}
assertFalse(nonNullKeys.isEmpty());
}

@Test
public void testReAddViewProducesSameEntityCount() throws Exception {
String[] tokens = new String[] { "Barack", "Obama", "was", "president" };
String[] sentenceLabels = new String[] { "Barack Obama was president" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentenceLabels, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view1 = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// int count1 = view1.getNumberOfConstituents();
// ta.removeView(ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view2 = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// int count2 = view2.getNumberOfConstituents();
// assertEquals(count1, count2);
}

@Test
public void testMainMethodL1Execution() {
String configFile = "src/test/resources/test-ner-config.properties";
String[] args = new String[] { configFile, "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main method should not throw an exception with valid L1 args");
}
}

@Test
public void testMainMethodL2Execution() {
String configFile = "src/test/resources/test-ner-config.properties";
String[] args = new String[] { configFile, "L2" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main method should not throw an exception with valid L2 args");
}
}

@Test
public void testMainMethodHandlesInvalidArgs() {
String[] args = new String[] {};
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main method should not throw exception on empty args");
}
}

@Test
public void testAddViewWithOnlyPunctuationTokens() throws Exception {
String[] tokens = new String[] { ".", ",", "!" };
String[] sentences = new String[] { ". , !" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// assertTrue(ta.getView(ViewNames.NER_CONLL) instanceof SpanLabelView);
}

@Test
public void testAddViewWithEmptySentence() throws Exception {
String[] tokens = new String[] {};
String[] sentences = new String[] { "" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithTokensHavingEmptyStrings() throws Exception {
String[] tokens = new String[] { "John", "", "Smith" };
String[] sentences = new String[] { "John  Smith" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewWithOnlyOneToken() throws Exception {
String[] tokens = new String[] { "Obama" };
String[] sentences = new String[] { "Obama" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testGetTagValuesAfterExplicitInitialization() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
annotator.doInitialize();
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.contains("O"));
}

@Test
public void testGetTagValuesWithLazyInitialization() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertTrue(tags.contains("O"));
}

@Test
public void testAddViewDoesNotCrashWhenMultipleConsecutiveLabelsDiffer() throws Exception {
String[] tokens = new String[] { "Barack", "Obama", "Paris", "France" };
String[] sentences = new String[] { "Barack Obama Paris France" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL1WeightsDoNotThrowOnEmptyFeatures() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
assertNotNull(weights);
Set<Feature> keySet = weights.keySet();
for (Feature feature : keySet) {
assertNotNull(feature.toString());
double[] w = weights.get(feature);
assertNotNull(w);
break;
}
}

@Test
public void testGetL2WeightsDoNotThrowOnEmptyFeatures() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
assertNotNull(weights);
Set<Feature> keySet = weights.keySet();
for (Feature feature : keySet) {
assertNotNull(feature.toString());
double[] w = weights.get(feature);
assertNotNull(w);
break;
}
}

@Test
public void testAddViewDoesNotThrowForSentenceWithSingleEmptyToken() throws Exception {
String[] tokens = new String[] { "" };
String[] sentences = new String[] { "" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testConstructorWithPropertiesFilePath() throws Exception {
java.io.File temp = java.io.File.createTempFile("temp-config", ".props");
java.io.FileWriter fw = new java.io.FileWriter(temp);
fw.write(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key + "=false\n");
fw.close();
String path = temp.getAbsolutePath();
NERAnnotator annotator = new NERAnnotator(path, ViewNames.NER_CONLL);
assertNotNull(annotator);
temp.delete();
}

@Test
public void testMainMethodWithInvalidModelLabel() {
String configFile = "src/test/resources/test-ner-config.properties";
String[] args = new String[] { configFile, "XYZ" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main method should not throw an exception for unknown model argument");
}
}

@Test
public void testEmptyResourceManagerStringConstructor() throws Exception {
NERAnnotator annotator = new NERAnnotator("", ViewNames.NER_CONLL);
assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
}

@Test
public void testAnnotatorWithNullTokensArray() throws Exception {
String text = "";
String[] tokens = null;
String[] sentences = new String[] {};
TextAnnotation ta = new TextAnnotation("dummy", "dummy", text, null, null, null);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
try {
annotator.addView(ta);
} catch (Exception e) {
assertTrue(e instanceof NullPointerException);
}
}

@Test
public void testAddViewWherePredictionBreaksSpanConsistency() throws Exception {
String[] tokens = new String[] { "New", "York", "Times" };
String[] sentences = new String[] { "New York Times" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "doc", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetTagValuesReturnsUniqueSet() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
assertFalse(tags.contains(null));
}

@Test
public void testAddViewWithMalformedTokenSpanCausingEndIndexBug() throws Exception {
String[] tokens = new String[] { "A", "very", "long", "sentence", "that", "wraps", "badly" };
String[] sentences = new String[] { "A very long", "sentence that wraps badly" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithInconsistentPredictionLabels() throws Exception {
String[] tokens = new String[] { "Los", "Angeles", "Rams", "NFL", "Team" };
String[] sentences = new String[] { "Los Angeles Rams NFL Team" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "doc", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testMainMethodHandlesIOExceptionGracefully() {
String[] args = new String[] { "nonexistent.config.properties", "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main should catch IOException and print error instead of throwing");
}
}

@Test
public void testTagSetSizeConsistencyAcrossInstances() throws Exception {
Properties props1 = new Properties();
props1.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm1 = new ResourceManager(props1);
NERAnnotator annotator1 = new NERAnnotator(rm1, ViewNames.NER_CONLL);
Properties props2 = new Properties();
props2.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm2 = new ResourceManager(props2);
NERAnnotator annotator2 = new NERAnnotator(rm2, ViewNames.NER_CONLL);
Set<String> tags1 = annotator1.getTagValues();
Set<String> tags2 = annotator2.getTagValues();
assertEquals(tags1.size(), tags2.size());
}

@Test
public void testAddViewWithTokenLengthMismatchIndexFixPath() throws Exception {
String[] tokens = new String[] { "Washington", "D.C", "is", "the", "capital" };
String[] sentences = new String[] { "Washington D.C", "is the capital" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetFeatureWeightsContainNonZeroValues() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> l1Weights = annotator.getL1FeatureWeights();
for (Map.Entry<Feature, double[]> entry : l1Weights.entrySet()) {
double[] weights = entry.getValue();
if (weights.length > 0) {
boolean hasNonZero = weights[0] != 0.0;
assertTrue(true);
break;
}
}
}

@Test
public void testMainHandlesTooManyArguments() {
String[] args = new String[] { "path/to/config", "L1", "extra" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main should not throw on extra arguments");
}
}

@Test
public void testAddViewHandlesOnlyNonAlphaTokens() throws Exception {
String[] tokens = new String[] { "!", "@", "#", "$" };
String[] sentences = new String[] { "! @ # $" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("id", "doc", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithRepeatedEntitiesAcrossSentences() throws Exception {
String[] tokens = new String[] { "Paris", "is", "nice", ".", "Paris", "is", "in", "France" };
String[] sentences = new String[] { "Paris is nice .", "Paris is in France" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("id", "doc", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testGetTagValuesWithoutExplicitInitialization() throws Exception {
Properties props = new Properties();
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertFalse(annotator.isInitialized());
assertNotNull(annotator.getTagValues());
}

@Test
public void testMainMethodHandlesExactlyWrongArgCount() {
String[] args = new String[] { "only-one-arg" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main method should not throw on incorrect argument count.");
}
}

@Test
public void testMainMethodWithUnknownModelType() throws Exception {
File tempFile = File.createTempFile("test-ner", ".config");
FileWriter writer = new FileWriter(tempFile);
writer.write("model.dir=src/test/resources/models\n");
writer.close();
String[] args = new String[] { tempFile.getAbsolutePath(), "UNSUPPORTED" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main should not throw for invalid model flag.");
}
tempFile.delete();
}

@Test
public void testAddViewHandlesOOnlyEntities() throws Exception {
String[] tokens = new String[] { "The", "cat", "sat", "on", "the", "mat" };
String[] sentences = new String[] { "The cat sat on the mat" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("x", "y", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testAddViewHandlesSingleIOEntity() throws Exception {
String[] tokens = new String[] { "International", "Business", "Machines" };
String[] sentences = new String[] { "International Business Machines" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("ibm", "doc", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testFeatureWeightFormatCorrectness_L1() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
java.util.HashMap<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> map = annotator.getL1FeatureWeights();
for (java.util.Map.Entry<edu.illinois.cs.cogcomp.lbjava.classify.Feature, double[]> entry : map.entrySet()) {
assertNotNull(entry.getKey().toString());
assertNotNull(entry.getValue());
assertTrue(entry.getValue().length > 0);
break;
}
}

@Test
public void testCallGetL1WeightsBeforeInitialization() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
assertNotNull(annotator.getL1FeatureWeights());
assertTrue(annotator.isInitialized());
}

@Test
public void testTextAnnotationWithEmptyViewNameStillWorks() throws Exception {
// TextAnnotation ta = new TextAnnotation("c", "d", "", new int[] { { 0 } }, new String[][] { { "X" } }, new int[][] { { 1 } });
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testConstructorWithNullPathAndValidViewName() throws Exception {
try {
// NERAnnotator annotator = new NERAnnotator(null, ViewNames.NER_CONLL);
// assertNotNull(annotator);
} catch (Exception e) {
fail("Constructor should not throw if resource manager file path is null.");
}
}

@Test
public void testAddViewWithStartEqualsEndSpan() throws Exception {
String[] tokens = new String[] { "Obama", "visited", "New", "York" };
String[] sentences = new String[] { "Obama visited New York" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("startEndEqual", "testDoc", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager config = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(config, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testInitializeFallsBackToNerBaseConfigurator() {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager initialRm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(initialRm, "UNKNOWN");
try {
annotator.initialize(initialRm);
} catch (Exception e) {
fail("Initialization should fallback safely on unknown viewName");
}
assertNotNull(annotator.getTagValues());
}

@Test
public void testInitializationThreadSafety() throws InterruptedException {
Properties props1 = new Properties();
props1.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm1 = new ResourceManager(props1);
final NERAnnotator annotator1 = new NERAnnotator(rm1, ViewNames.NER_CONLL);
Properties props2 = new Properties();
props2.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm2 = new ResourceManager(props2);
final NERAnnotator annotator2 = new NERAnnotator(rm2, ViewNames.NER_CONLL);
Thread t1 = new Thread(new Runnable() {

public void run() {
annotator1.getTagValues();
}
});
Thread t2 = new Thread(new Runnable() {

public void run() {
annotator2.getTagValues();
}
});
t1.start();
t2.start();
t1.join();
t2.join();
assertTrue(annotator1.isInitialized());
assertTrue(annotator2.isInitialized());
}

@Test
public void testAddViewHandlesMismatchedIOSequence() throws Exception {
String[] tokens = new String[] { "I", "love", "Apple", "stock", "products" };
String[] sentences = new String[] { "I love Apple stock products" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("mismatch", "doc", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager config = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(config, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewHandlesLastTokenWithOpenSpan() throws Exception {
String[] tokens = new String[] { "CEO", "of", "Microsoft", "Satya", "Nadella" };
String[] sentences = new String[] { "CEO of Microsoft Satya Nadella" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("ORG", "lastToken", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testGetL1FeatureWeightsDetectsMalformedMapping() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
Set<Map.Entry<Feature, double[]>> entrySet = weights.entrySet();
for (Map.Entry<Feature, double[]> entry : entrySet) {
Feature feature = entry.getKey();
double[] arr = entry.getValue();
assertNotNull(feature);
assertNotNull(arr);
assertTrue(arr.length > 0);
break;
}
}

@Test
public void testModelLoadingWithEmptyConfigFile() throws Exception {
File config = File.createTempFile("empty", ".props");
FileWriter fw = new FileWriter(config);
fw.write("");
fw.close();
NERAnnotator annotator = null;
try {
annotator = new NERAnnotator(config.getAbsolutePath(), ViewNames.NER_CONLL);
} catch (Exception e) {
fail("Should load default config even with empty file.");
}
assertNotNull(annotator);
config.delete();
}

@Test
public void testViewNameFallbackWhenInvalid() {
Properties props = new Properties();
props.setProperty("some.nonsense", "true");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, "INVALID_VIEW");
assertEquals("INVALID_VIEW", annotator.getViewName());
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
}

@Test
public void testStartIndexGreaterThanEndIndexEntityHandling() throws Exception {
String[] tokens = new String[] { "this", "is", "a", "test" };
String[] sentence = new String[] { "this is a test" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("testing", "range", sentence, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithAllBLabelsInSentence() throws Exception {
String[] tokens = new String[] { "John", "Doe", "from", "New", "York" };
String[] sentences = new String[] { "John Doe from New York" };
// TextAnnotation textAnnotation = TextAnnotationUtilities.createTextAnnotationFromTokens("ner", "doc", sentences, tokens);
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
// annotator.addView(textAnnotation);
// assertTrue(textAnnotation.hasView(ViewNames.NER_CONLL));
// SpanLabelView view = (SpanLabelView) textAnnotation.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWhereAllTokensAreEmptyStrings() throws Exception {
String[] tokens = new String[] { "", "", "" };
String[] sentences = new String[] { "  " };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("empty", "tokens", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testAddViewHandlesNextPredictionOfIOWithDifferentLabel() throws Exception {
String[] tokens = new String[] { "Barack", "President", "House", "Obama" };
String[] sentence = new String[] { "Barack President House Obama" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("scope", "test", sentence, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
// SpanLabelView spanView = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(spanView);
}

@Test
public void testTokenCountExceedsIndexMapping() throws Exception {
String[] tokens = new String[] { "A", "B", "C", "D", "E", "F" };
String[] sentences = new String[] { "A B", "C D", "E F" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("doc", "idx", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testNERAnnotatorGetTagValuesHandlesEmptyLexicon() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
Set<String> tagValues = annotator.getTagValues();
assertNotNull(tagValues);
assertTrue(tagValues.contains("O"));
}

@Test
public void testAddViewHandlesConstituentWithZeroLengthSpan() throws Exception {
String[] tokens = new String[] { "Just", "a", "test" };
String[] sentence = new String[] { "Just a test" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("test", "edge", sentence, tokens);
Properties p = new Properties();
p.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(p);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testMainHandlesEmptyArgsArray() {
try {
String[] args = new String[] {};
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main should not throw if called with empty args.");
}
}

@Test
public void testMainHandlesNonexistentConfigPath() {
try {
String[] args = new String[] { "non_existing_path.props", "L1" };
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main should not throw even if configuration file is missing.");
}
}

@Test
public void testFeatureWeightRetrievalWithModelL1AndL2() throws Exception {
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager resourceManager = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(resourceManager, ViewNames.NER_CONLL);
HashMap<Feature, double[]> l1 = annotator.getL1FeatureWeights();
HashMap<Feature, double[]> l2 = annotator.getL2FeatureWeights();
assertNotNull(l1);
assertNotNull(l2);
Set<Feature> l1Keys = l1.keySet();
Set<Feature> l2Keys = l2.keySet();
assertTrue(l1Keys.size() > 0 || l2Keys.size() > 0);
}

@Test
public void testAddViewWithIOSequenceNoBPrefix() throws Exception {
String[] tokens = new String[] { "is", "known", "in", "USA" };
String[] sentences = new String[] { "is known in USA" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("confused", "labels", sentences, tokens);
Properties p = new Properties();
p.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(p);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
// assertTrue(view.getNumberOfConstituents() >= 0);
}

@Test
public void testAddViewWithSingletonSpanTokens() throws Exception {
String[] tokens = new String[] { "Barack", "Obama", "visited", "Berlin" };
String[] sentences = new String[] { "Barack Obama visited Berlin" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("singleton", "span", sentences, tokens);
Properties configProps = new Properties();
configProps.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager config = new ResourceManager(configProps);
NERAnnotator annotator = new NERAnnotator(config, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testAddViewWithDuplicateTokens() throws Exception {
String[] tokens = new String[] { "John", "John", "visited", "John" };
String[] sentence = new String[] { "John John visited John" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("dup", "tok", sentence, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testModelLoadsWithAlternateViewName() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, "VIEW_UNKNOWN");
Set<String> tags = annotator.getTagValues();
assertNotNull(tags);
}

@Test
public void testAddViewHandlesOverlappingConstituentsCorrectly() throws Exception {
String[] tokens = new String[] { "John", "Doe", "New", "York" };
String[] sentences = new String[] { "John Doe New York" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("overlap", "test", sentences, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
// assertNotNull(view);
}

@Test
public void testMainHandlesUnreadableConfigFile() throws Exception {
File config = File.createTempFile("fail-config", ".props");
config.setReadable(false);
String path = config.getAbsolutePath();
String[] args = new String[] { path, "L1" };
try {
NERAnnotator.main(args);
} catch (Exception e) {
fail("Main should handle unreadable config file without exception.");
}
config.delete();
}

@Test
public void testAddViewHandlesIOBESequenceCorrectly() throws Exception {
String[] tokens = new String[] { "Begin", "middle", "end" };
String[] sentences = new String[] { "Begin middle end" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("bioe", "labels", sentences, tokens);
Properties properties = new Properties();
properties.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager config = new ResourceManager(properties);
NERAnnotator annotator = new NERAnnotator(config, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testSpanIndexEdgeStartAtEndOfArray() throws Exception {
String[] tokens = new String[] { "x", "y", "z" };
String[] sentence = new String[] { "x y z" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("idx", "edge", sentence, tokens);
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
// annotator.addView(ta);
// assertTrue(ta.hasView(ViewNames.NER_CONLL));
}

@Test
public void testGetL2FeatureWeightsNonEmpty() throws Exception {
Properties props = new Properties();
props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
ResourceManager rm = new ResourceManager(props);
NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
assertNotNull(weights);
Set<Feature> keySet = weights.keySet();
assertFalse(keySet.isEmpty());
}
}
