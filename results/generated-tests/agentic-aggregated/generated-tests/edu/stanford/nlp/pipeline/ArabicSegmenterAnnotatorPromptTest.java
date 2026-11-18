package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class ArabicSegmenterAnnotatorPromptTest {

  @Test
  public void annotatesSingleAnnotationWithOffsetsAndOriginalText() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Annotation annotation = new Annotation("الكتاب");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(6, tokens.size());
    assertEquals("ا", tokens.get(0).word());
    assertEquals("ا", tokens.get(0).value());
    assertEquals("ا", tokens.get(0).originalText());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(1, tokens.get(0).endPosition());
    assertEquals("ب", tokens.get(5).word());
    assertEquals(5, tokens.get(5).beginPosition());
    assertEquals(6, tokens.get(5).endPosition());
    Files.deleteIfExists(model);
  }

  @Test
  public void annotatesEverySentenceAndLeavesDocumentTokensUntouched() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("segment.model", model.toString());
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("segment", properties);
    Annotation document = new Annotation("ignored document text");
    Annotation firstSentence = new Annotation("اب");
    Annotation secondSentence = new Annotation("مدرسة");
    List<CoreMap> sentences = Arrays.asList(firstSentence, secondSentence);
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    CoreLabel sentinel = new CoreLabel();
    sentinel.setWord("sentinel");
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(sentinel));

    annotator.annotate(document);

    assertEquals(2, firstSentence.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("ا", firstSentence.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
    assertEquals(5, secondSentence.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("ة", secondSentence.get(CoreAnnotations.TokensAnnotation.class).get(4).word());
    assertSame(sentinel, document.get(CoreAnnotations.TokensAnnotation.class).get(0));
    Files.deleteIfExists(model);
  }

  @Test
  public void emptySentenceListPreventsFallbackToDocumentText() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Annotation document = new Annotation("كتاب");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(document);

    assertNull(document.get(CoreAnnotations.TokensAnnotation.class));
    Files.deleteIfExists(model);
  }

  @Test
  public void newlineEnabledInsertsNewlineTokenAndAdjustsFollowingOffsets() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Annotation annotation = new Annotation("ا\nب");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("ا", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).value());
    assertEquals("\n", tokens.get(1).originalText());
    assertEquals(1, tokens.get(1).beginPosition());
    assertEquals(2, tokens.get(1).endPosition());
    assertEquals("ب", tokens.get(2).word());
    assertEquals(2, tokens.get(2).beginPosition());
    assertEquals(3, tokens.get(2).endPosition());
    Files.deleteIfExists(model);
  }

  @Test
  public void consecutiveNewlinesCollapseToOneUnlessTwoModeIsSelected() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Annotation annotation = new Annotation("ا\n\r\n\u2028ب");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals("\n", tokens.get(1).originalText());
    assertEquals("ب", tokens.get(2).word());
    assertEquals(5, tokens.get(2).beginPosition());
    assertEquals(6, tokens.get(2).endPosition());
    Files.deleteIfExists(model);
  }

  @Test
  public void twoNewlineModeRetainsExactlyTwoConsecutiveNewlines() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    properties.setProperty("ssplit.newlineIsSentenceBreak", "two");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Annotation annotation = new Annotation("ا\n\r\n\u2028ب");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("\n", tokens.get(1).originalText());
    assertEquals(1, tokens.get(1).beginPosition());
    assertEquals("\r\n", tokens.get(2).originalText());
    assertEquals(2, tokens.get(2).beginPosition());
    assertEquals(4, tokens.get(2).endPosition());
    assertEquals("ب", tokens.get(3).word());
    assertEquals(5, tokens.get(3).beginPosition());
    Files.deleteIfExists(model);
  }

  @Test
  public void textBetweenNewlinesResetsConsecutiveNewlineTracking() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Annotation annotation = new Annotation("ا\nب\nت");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals("ب", tokens.get(2).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(3).word());
    assertEquals(3, tokens.get(3).beginPosition());
    assertEquals("ت", tokens.get(4).word());
    assertEquals(4, tokens.get(4).beginPosition());
    Files.deleteIfExists(model);
  }

  @Test
  public void legacyEolOnlyPropertyAlsoEnablesNewlineTokens() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    properties.setProperty("ssplit.eolonly", "true");
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Annotation annotation = new Annotation("\r\n");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals("\r\n", tokens.get(0).originalText());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(2, tokens.get(0).endPosition());
    Files.deleteIfExists(model);
  }

  @Test
  public void requirementsSatisfiedReturnsTheDocumentedAnnotationTypes() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);
    Set<Class<? extends CoreAnnotation>> expected = new HashSet<>(Arrays.asList(
        CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.TokensAnnotation.class,
        CoreAnnotations.CharacterOffsetBeginAnnotation.class,
        CoreAnnotations.CharacterOffsetEndAnnotation.class,
        CoreAnnotations.BeforeAnnotation.class,
        CoreAnnotations.AfterAnnotation.class,
        CoreAnnotations.TokenBeginAnnotation.class,
        CoreAnnotations.TokenEndAnnotation.class,
        CoreAnnotations.PositionAnnotation.class,
        CoreAnnotations.IndexAnnotation.class,
        CoreAnnotations.OriginalTextAnnotation.class,
        CoreAnnotations.ValueAnnotation.class));

    Set<Class<? extends CoreAnnotation>> actual = annotator.requirementsSatisfied();

    assertEquals(expected, actual);
    actual.clear();
    assertEquals(12, annotator.requirementsSatisfied().size());
    Files.deleteIfExists(model);
  }

  @Test
  public void requiresIsEmptyAndCannotBeMutated() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    Properties properties = new Properties();
    properties.setProperty("arabic.model", model.toString());
    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator("arabic", properties);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.isEmpty());
    try {
      requirements.add(CoreAnnotations.TextAnnotation.class);
      fail("The empty requirement set should be immutable");
    } catch (UnsupportedOperationException expected) {
      assertTrue(requirements.isEmpty());
    }
    Files.deleteIfExists(model);
  }

  @Test
  public void stringAndVerboseConstructorUsesSuppliedModel() throws Exception {
    Path model = Files.createTempFile("arabic-segmenter", ".ser.gz");
    Properties trainingProperties = new Properties();
    trainingProperties.setProperty("featureFactory", "edu.stanford.nlp.international.arabic.process.StartAndEndArabicSegmenterFeatureFactory");
    trainingProperties.setProperty("maxIterations", "1");
    trainingProperties.setProperty("useQN", "false");
    trainingProperties.setProperty("useInPlaceSGD", "true");
    trainingProperties.setProperty("backgroundSymbol", "NOSEG");
    CRFClassifier<CoreLabel> classifier = new CRFClassifier<>(trainingProperties);
    CoreLabel trainingDatum = new CoreLabel();
    trainingDatum.setWord(".");
    trainingDatum.setIndex(0);
    trainingDatum.set(CoreAnnotations.TextAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.CharAnnotation.class, ".");
    trainingDatum.set(CoreAnnotations.AnswerAnnotation.class, "NOSEG");
    trainingDatum.set(CoreAnnotations.GoldAnswerAnnotation.class, "NOSEG");
    trainingDatum.setBeginPosition(0);
    trainingDatum.setEndPosition(1);
    classifier.train(Collections.singletonList(Collections.singletonList(trainingDatum)), null);
    classifier.serializeClassifier(model.toString());

    ArabicSegmenterAnnotator annotator = new ArabicSegmenterAnnotator(model.toString(), true);
    Annotation annotation = new Annotation("اب");

    annotator.annotate(annotation);

    assertEquals(2, annotation.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("ب", annotation.get(CoreAnnotations.TokensAnnotation.class).get(1).originalText());
    Files.deleteIfExists(model);
  }

  @Test
  public void missingNamedModelPropertyIsRejectedBeforeAnnotation() {
    Properties properties = new Properties();
    properties.setProperty("other.model", "not-used");

    try {
      new ArabicSegmenterAnnotator("arabic", properties);
      fail("A named model property is mandatory");
    } catch (RuntimeException expected) {
      assertEquals("Expected a property arabic.model", expected.getMessage());
    }
  }

  @Test
  public void nonexistentModelPathIsReportedAsRuntimeFailure() {
    Properties properties = new Properties();
    properties.setProperty("arabic.model", "model-that-does-not-exist.ser.gz");

    try {
      new ArabicSegmenterAnnotator("arabic", properties);
      fail("Loading a nonexistent model should fail");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("model-that-does-not-exist.ser.gz"));
    }
  }

  @Test
  public void annotatorSpecificTokenizerOptionsAreForwarded() {
    Properties properties = new Properties();
    properties.setProperty("arabic.model", "loading-is-never-reached.ser.gz");
    properties.setProperty("arabic.orthoOptions", "removeSegMarker");

    try {
      new ArabicSegmenterAnnotator("arabic", properties);
      fail("The Arabic segmenter forbids removeSegMarker");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("removeSegMarker"));
    }
  }
}

