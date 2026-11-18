package gate.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import gate.Annotation;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.AnnotationSchema;
import gate.creole.ResourceInstantiationException;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.junit.Test;

public class CorpusAnnotationDiffPromptTest {

  @Test
  public void constructorProvidesEmptyDefaultsAndEmptyClassificationResults() {
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();

    assertNull(diff.getAnnotationSchema());
    assertNull(diff.getKeyCorpus());
    assertNull(diff.getKeyAnnotationSetName());
    assertNull(diff.getResponseAnnotationSetName());
    assertNull(diff.getResponseAnnotationSetNameFalsePoz());
    assertNull(diff.getAnnotationTypeForFalsePositive());
    assertNull(diff.getKeyFeatureNamesSet());
    assertFalse(diff.isTextMode());
    assertTrue(diff.getAnnotationsOfType(0).isEmpty());
    assertTrue(diff.getAnnotationsOfType(999).isEmpty());
    assertEquals(0.0, diff.getPrecisionStrict(), 0.0);
    assertEquals(0.0, diff.getRecallLenient(), 0.0);
    assertEquals(0.0, diff.getFMeasureAverage(), 0.0);
  }

  @Test
  public void ordinaryAndIntrospectedSettersRoundTripValues() throws Exception {
    Gate.init();
    Corpus corpus = Factory.newCorpus("key");
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    Set<String> features = new HashSet<String>(Arrays.asList("kind", "level"));
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();

    diff.setKeyCorpus(corpus);
    diff.setAnnotationSchema(schema);
    diff.setKeyAnnotationSetName("gold");
    diff.setResponseAnnotationSetName("system");
    diff.setResponseAnnotationSetNameFalsePoz("tokens");
    diff.setAnnotationTypeForFalsePositive("Token");
    diff.setKeyFeatureNamesSet(features);
    diff.setTextMode(Boolean.TRUE);

    assertSame(corpus, diff.getKeyCorpus());
    assertSame(schema, diff.getAnnotationSchema());
    assertEquals("gold", diff.getParameterValue("keyAnnotationSetName"));
    assertEquals("system", diff.getResponseAnnotationSetName());
    assertEquals("tokens", diff.getResponseAnnotationSetNameFalsePoz());
    assertEquals("Token", diff.getAnnotationTypeForFalsePositive());
    assertSame(features, diff.getKeyFeatureNamesSet());
    assertTrue(diff.isTextMode());

    diff.setParameterValue("keyAnnotationSetName", "revised-gold");
    assertEquals("revised-gold", diff.getKeyAnnotationSetName());
  }

  @Test
  public void bulkParameterSetterUpdatesIndependentProperties() throws Exception {
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    FeatureMap parameters = Factory.newFeatureMap();
    parameters.put("keyAnnotationSetName", "reference");
    parameters.put("responseAnnotationSetName", "candidate");
    parameters.put("annotationTypeForFalsePositive", "Sentence");

    diff.setParameterValues(parameters);

    assertEquals("reference", diff.getKeyAnnotationSetName());
    assertEquals("candidate", diff.getResponseAnnotationSetName());
    assertEquals("Sentence", diff.getAnnotationTypeForFalsePositive());
    assertFalse(diff.isTextMode());
  }

  @Test(expected = ResourceInstantiationException.class)
  public void initRejectsMissingSchemaBeforeOtherConfiguration() throws Exception {
    new CorpusAnnotationDiff().init();
  }

  @Test
  public void initRejectsMissingKeyCorpus() throws Exception {
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    diff.setAnnotationSchema(schema);

    try {
      diff.init();
    } catch(ResourceInstantiationException exception) {
      assertTrue(exception.getMessage().contains("key corpus"));
      return;
    }
    throw new AssertionError("Expected a missing-key-corpus failure");
  }

  @Test
  public void initRejectsEmptyResponseCorpus() throws Exception {
    Gate.init();
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(Factory.newDocument("abcdef"));
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);

    try {
      diff.init();
    } catch(ResourceInstantiationException exception) {
      assertTrue(exception.getMessage().contains("response corpus"));
      return;
    }
    throw new AssertionError("Expected an empty-response-corpus failure");
  }

  @Test
  public void exactAnnotationProducesPerfectScoresAndCorrectClassification() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("John works here");
    Document responseDocument = Factory.newDocument("John works here");
    keyDocument.setName("same-document");
    responseDocument.setName("same-document");
    FeatureMap keyFeatures = Factory.newFeatureMap();
    keyFeatures.put("kind", "person");
    FeatureMap responseFeatures = Factory.newFeatureMap();
    responseFeatures.put("kind", "person");
    keyDocument.getAnnotations().add(0L, 4L, "Entity", keyFeatures);
    responseDocument.getAnnotations().add(0L, 4L, "Entity", responseFeatures);
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setTextMode(Boolean.TRUE);

    assertSame(diff, diff.init());

    assertEquals(1.0, diff.getPrecisionStrict(), 0.0);
    assertEquals(1.0, diff.getPrecisionLenient(), 0.0);
    assertEquals(1.0, diff.getRecallStrict(), 0.0);
    assertEquals(1.0, diff.getRecallLenient(), 0.0);
    assertEquals(1.0, diff.getFMeasureStrict(), 0.0);
    assertEquals(1.0, diff.getFMeasureAverage(), 0.0);
    Set<Annotation> correct = diff.getAnnotationsOfType(1);
    assertEquals(1, correct.size());
    assertTrue(correct.contains(responseDocument.getAnnotations().get("Entity").iterator().next()));
    assertTrue(diff.getAnnotationsOfType(2).isEmpty());
    assertTrue(diff.getAnnotationsOfType(3).isEmpty());
    assertTrue(diff.getAnnotationsOfType(4).isEmpty());
  }

  @Test
  public void overlappingAnnotationIsLenientlyCorrectButNotStrictlyCorrect() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdefghij");
    Document responseDocument = Factory.newDocument("abcdefghij");
    keyDocument.setName("overlap");
    responseDocument.setName("overlap");
    keyDocument.getAnnotations().add(1L, 6L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(3L, 8L, "Entity", Factory.newFeatureMap());
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(0.0, diff.getPrecisionStrict(), 0.0);
    assertEquals(1.0, diff.getPrecisionLenient(), 0.0);
    assertEquals(0.5, diff.getPrecisionAverage(), 0.0);
    assertEquals(0.0, diff.getRecallStrict(), 0.0);
    assertEquals(1.0, diff.getRecallLenient(), 0.0);
    assertEquals(0.0, diff.getFMeasureStrict(), 0.0);
    assertEquals(1.0, diff.getFMeasureLenient(), 0.0);
    assertEquals(1, diff.getAnnotationsOfType(2).size());
    assertEquals(1, diff.getAnnotationsOfType(0).size());
  }

  @Test
  public void mixedAnnotationsExerciseAllFourOutcomeCategories() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdefghijklmnopqrst");
    Document responseDocument = Factory.newDocument("abcdefghijklmnopqrst");
    keyDocument.setName("mixed");
    responseDocument.setName("mixed");
    keyDocument.getAnnotations().add(0L, 3L, "Entity", Factory.newFeatureMap());
    keyDocument.getAnnotations().add(5L, 9L, "Entity", Factory.newFeatureMap());
    keyDocument.getAnnotations().add(11L, 13L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(0L, 3L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(6L, 10L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(15L, 18L, "Entity", Factory.newFeatureMap());
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(1, diff.getAnnotationsOfType(1).size());
    assertEquals(1, diff.getAnnotationsOfType(2).size());
    assertEquals(1, diff.getAnnotationsOfType(3).size());
    assertEquals(1, diff.getAnnotationsOfType(4).size());
    assertEquals(1.0 / 3.0, diff.getPrecisionStrict(), 0.0000001);
    assertEquals(2.0 / 3.0, diff.getPrecisionLenient(), 0.0000001);
    assertEquals(0.5, diff.getPrecisionAverage(), 0.0000001);
    assertEquals(1.0 / 3.0, diff.getRecallStrict(), 0.0000001);
    assertEquals(2.0 / 3.0, diff.getRecallLenient(), 0.0000001);
    assertEquals(0.5, diff.getFMeasureAverage(), 0.0000001);
  }

  @Test
  public void coextensiveAnnotationsWithSignificantFeatureMismatchAreMissingAndSpurious() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdefghij");
    Document responseDocument = Factory.newDocument("abcdefghij");
    keyDocument.setName("feature-mismatch");
    responseDocument.setName("feature-mismatch");
    FeatureMap keyFeatures = Factory.newFeatureMap();
    keyFeatures.put("kind", "person");
    FeatureMap responseFeatures = Factory.newFeatureMap();
    responseFeatures.put("kind", "place");
    keyDocument.getAnnotations().add(1L, 5L, "Entity", keyFeatures);
    responseDocument.getAnnotations().add(1L, 5L, "Entity", responseFeatures);
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setKeyFeatureNamesSet(Collections.singleton("kind"));
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(0, diff.getAnnotationsOfType(1).size());
    assertEquals(1, diff.getAnnotationsOfType(3).size());
    assertEquals(1, diff.getAnnotationsOfType(4).size());
    assertEquals(0.0, diff.getPrecisionLenient(), 0.0);
    assertEquals(0.0, diff.getRecallLenient(), 0.0);
    assertEquals(0.0, diff.getFMeasureAverage(), 0.0);
  }

  @Test
  public void featureSelectionIgnoresUnselectedFeatureDifferences() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdefghij");
    Document responseDocument = Factory.newDocument("abcdefghij");
    keyDocument.setName("selected-feature");
    responseDocument.setName("selected-feature");
    FeatureMap keyFeatures = Factory.newFeatureMap();
    keyFeatures.put("kind", "person");
    keyFeatures.put("confidence", Double.valueOf(1.0));
    FeatureMap responseFeatures = Factory.newFeatureMap();
    responseFeatures.put("kind", "person");
    responseFeatures.put("confidence", Double.valueOf(0.2));
    keyDocument.getAnnotations().add(1L, 5L, "Entity", keyFeatures);
    responseDocument.getAnnotations().add(1L, 5L, "Entity", responseFeatures);
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setKeyFeatureNamesSet(Collections.singleton("kind"));
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(1, diff.getAnnotationsOfType(1).size());
    assertEquals(1.0, diff.getPrecisionStrict(), 0.0);
    assertEquals(1.0, diff.getRecallStrict(), 0.0);
  }

  @Test
  public void namedAnnotationSetsAreUsedInsteadOfDistractingDefaultSets() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdefghij");
    Document responseDocument = Factory.newDocument("abcdefghij");
    keyDocument.setName("named-sets");
    responseDocument.setName("named-sets");
    keyDocument.getAnnotations().add(0L, 2L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(7L, 9L, "Entity", Factory.newFeatureMap());
    keyDocument.getAnnotations("gold").add(2L, 5L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations("system").add(2L, 5L, "Entity", Factory.newFeatureMap());
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setKeyAnnotationSetName("gold");
    diff.setResponseAnnotationSetName("system");
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(1, diff.getAnnotationsOfType(1).size());
    assertTrue(diff.getAnnotationsOfType(3).isEmpty());
    assertTrue(diff.getAnnotationsOfType(4).isEmpty());
    assertEquals(1.0, diff.getPrecisionAverage(), 0.0);
  }

  @Test
  public void falsePositiveMetricsUseTokenCountFromNamedSet() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdefghijklmnopqrst");
    Document responseDocument = Factory.newDocument("abcdefghijklmnopqrst");
    keyDocument.setName("false-positive");
    responseDocument.setName("false-positive");
    keyDocument.getAnnotations().add(1L, 6L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(2L, 7L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(10L, 12L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations("lexical").add(0L, 4L, "Token", Factory.newFeatureMap());
    responseDocument.getAnnotations("lexical").add(5L, 9L, "Token", Factory.newFeatureMap());
    responseDocument.getAnnotations("lexical").add(10L, 14L, "Token", Factory.newFeatureMap());
    responseDocument.getAnnotations("lexical").add(15L, 19L, "Token", Factory.newFeatureMap());
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setAnnotationTypeForFalsePositive("Token");
    diff.setResponseAnnotationSetNameFalsePoz("lexical");
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(0.5, diff.getFalsePositiveStrict(), 0.0);
    assertEquals(0.25, diff.getFalsePositiveLenient(), 0.0);
    assertEquals(0.375, diff.getFalsePositiveAverage(), 0.0);
  }

  @Test
  public void resultsAccumulateAcrossMultipleMatchedDocumentPairs() throws Exception {
    Gate.init();
    Document keyOne = Factory.newDocument("abcdef");
    Document keyTwo = Factory.newDocument("uvwxyz");
    Document responseOne = Factory.newDocument("abcdef");
    Document responseTwo = Factory.newDocument("uvwxyz");
    keyOne.setName("one");
    responseOne.setName("one");
    keyTwo.setName("two");
    responseTwo.setName("two");
    keyOne.setSourceUrl(new URL("file:/documents/one"));
    responseOne.setSourceUrl(new URL("file:/documents/one"));
    keyTwo.setSourceUrl(new URL("file:/documents/two"));
    responseTwo.setSourceUrl(new URL("file:/documents/two"));
    keyOne.getAnnotations().add(0L, 3L, "Entity", Factory.newFeatureMap());
    responseOne.getAnnotations().add(0L, 3L, "Entity", Factory.newFeatureMap());
    keyTwo.getAnnotations().add(3L, 6L, "Entity", Factory.newFeatureMap());
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyOne);
    key.add(keyTwo);
    response.add(responseTwo);
    response.add(responseOne);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(1, diff.getAnnotationsOfType(1).size());
    assertEquals(1, diff.getAnnotationsOfType(4).size());
    assertEquals(1.0, diff.getPrecisionStrict(), 0.0);
    assertEquals(0.5, diff.getRecallStrict(), 0.0);
    assertEquals(2.0 / 3.0, diff.getFMeasureStrict(), 0.0000001);
  }

  @Test
  public void emptyAnnotationSetsProduceFiniteZeroMetrics() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdef");
    Document responseDocument = Factory.newDocument("abcdef");
    keyDocument.setName("empty");
    responseDocument.setName("empty");
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);
    diff.setTextMode(Boolean.TRUE);
    diff.init();

    assertEquals(0.0, diff.getPrecisionStrict(), 0.0);
    assertEquals(0.0, diff.getPrecisionLenient(), 0.0);
    assertEquals(0.0, diff.getRecallStrict(), 0.0);
    assertEquals(0.0, diff.getRecallLenient(), 0.0);
    assertEquals(0.0, diff.getFMeasureStrict(), 0.0);
    assertEquals(0.0, diff.getFalsePositiveAverage(), 0.0);
  }

  @Test
  public void scrollableContractUsesPreferredSizeAndTenPixelMargins() {
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setPreferredSize(new Dimension(320, 180));
    Rectangle visible = new Rectangle(0, 0, 100, 70);

    assertEquals(new Dimension(320, 180), diff.getPreferredScrollableViewportSize());
    assertEquals(10, diff.getScrollableUnitIncrement(visible, SwingConstants.HORIZONTAL, 1));
    assertEquals(10, diff.getScrollableUnitIncrement(visible, SwingConstants.VERTICAL, -1));
    assertEquals(90, diff.getScrollableBlockIncrement(visible, SwingConstants.HORIZONTAL, 1));
    assertEquals(60, diff.getScrollableBlockIncrement(visible, SwingConstants.VERTICAL, -1));
    assertFalse(diff.getScrollableTracksViewportWidth());
    assertFalse(diff.getScrollableTracksViewportHeight());
  }

  @Test
  public void graphicalModeEventuallyBuildsItsComponentHierarchy() throws Exception {
    Gate.init();
    Document keyDocument = Factory.newDocument("abcdef");
    Document responseDocument = Factory.newDocument("abcdef");
    keyDocument.setName("gui");
    responseDocument.setName("gui");
    keyDocument.getAnnotations().add(0L, 3L, "Entity", Factory.newFeatureMap());
    responseDocument.getAnnotations().add(0L, 3L, "Entity", Factory.newFeatureMap());
    Corpus key = Factory.newCorpus("key");
    Corpus response = Factory.newCorpus("response");
    key.add(keyDocument);
    response.add(responseDocument);
    AnnotationSchema schema = new AnnotationSchema();
    schema.setAnnotationName("Entity");
    CorpusAnnotationDiff diff = new CorpusAnnotationDiff();
    diff.setAnnotationSchema(schema);
    diff.setKeyCorpus(key);
    diff.setResponseCorpus(response);

    diff.init();
    SwingUtilities.invokeAndWait(() -> { });

    assertTrue(diff.getComponentCount() >= 3);
    assertEquals(1.0, diff.getPrecisionStrict(), 0.0);
  }
}
