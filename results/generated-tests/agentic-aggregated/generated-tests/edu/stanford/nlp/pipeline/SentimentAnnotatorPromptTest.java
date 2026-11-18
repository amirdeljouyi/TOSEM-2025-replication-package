package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.RNNOptions;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentModel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class SentimentAnnotatorPromptTest {

  @Test
  public void signatureUsesAllDefaults() {
    Properties properties = new Properties();

    String signature = SentimentAnnotator.signature("sentiment", properties);

    assertEquals("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"
        + "sentiment.nthreads:sentiment.maxtime:-1", signature);
  }

  @Test
  public void signatureUsesGlobalThreadFallback() {
    Properties properties = new Properties();
    properties.setProperty("nthreads", "7");

    assertEquals("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"
        + "sentiment.nthreads:7sentiment.maxtime:-1",
        SentimentAnnotator.signature("sentiment", properties));
  }

  @Test
  public void signaturePrefersAnnotatorSpecificThreadCount() {
    Properties properties = new Properties();
    properties.setProperty("nthreads", "7");
    properties.setProperty("custom.nthreads", "3");

    assertEquals("custom.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"
        + "custom.nthreads:3custom.maxtime:-1",
        SentimentAnnotator.signature("custom", properties));
  }

  @Test
  public void signatureIncludesCustomModelAndMaximumTimeVerbatim() {
    Properties properties = new Properties();
    properties.setProperty("opinion.model", "/models/a model.ser.gz");
    properties.setProperty("opinion.nthreads", "02");
    properties.setProperty("opinion.maxtime", "4500");

    assertEquals("opinion.model:/models/a model.ser.gz"
        + "opinion.nthreads:02opinion.maxtime:4500",
        SentimentAnnotator.signature("opinion", properties));
  }

  @Test
  public void signaturePreservesExplicitEmptyValues() {
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", "");
    properties.setProperty("sentiment.nthreads", "");
    properties.setProperty("sentiment.maxtime", "");

    assertEquals("sentiment.model:sentiment.nthreads:sentiment.maxtime:",
        SentimentAnnotator.signature("sentiment", properties));
  }

  @Test(expected = NullPointerException.class)
  public void signatureRejectsNullProperties() {
    SentimentAnnotator.signature("sentiment", null);
  }

  @Test
  public void requiresReturnsFourElementsAndIsUnmodifiable() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "3"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    Tree trainingTree = Tree.valueOf("(ROOT (JJ fine))");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(trainingTree));
    Path modelPath = Files.createTempFile("sentiment-requires-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertEquals(4, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(requirements.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CategoryAnnotation.class));
    try {
      requirements.clear();
      fail("Requirements must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(4, requirements.size());
    }
  }

  @Test
  public void requirementsSatisfiedIsEmptyAndUnmodifiable() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (JJ okay))")));
    Path modelPath = Files.createTempFile("sentiment-satisfied-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.isEmpty());
    try {
      satisfied.add(CoreAnnotations.TextAnnotation.class);
      fail("The empty result must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertTrue(satisfied.isEmpty());
    }
  }

  @Test(expected = RuntimeIOException.class)
  public void constructorRejectsMissingModelFile() {
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", "/definitely/missing/model-for-test.ser.gz");

    new SentimentAnnotator("sentiment", properties);
  }

  @Test
  public void constructorAppliesThreadAndTimePrecedence() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (NN test))")));
    Path modelPath = Files.createTempFile("sentiment-config-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties defaultProperties = new Properties();
    defaultProperties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator defaults = new SentimentAnnotator("sentiment", defaultProperties);
    Properties globalProperties = new Properties();
    globalProperties.setProperty("sentiment.model", modelPath.toString());
    globalProperties.setProperty("nthreads", "5");
    SentimentAnnotator global = new SentimentAnnotator("sentiment", globalProperties);
    Properties specificProperties = new Properties();
    specificProperties.setProperty("sentiment.model", modelPath.toString());
    specificProperties.setProperty("nthreads", "5");
    specificProperties.setProperty("sentiment.nthreads", "2");
    specificProperties.setProperty("sentiment.maxtime", "1234");
    SentimentAnnotator specific = new SentimentAnnotator("sentiment", specificProperties);

    assertEquals(1, defaults.nThreads());
    assertEquals(-1L, defaults.maxTime());
    assertEquals(5, global.nThreads());
    assertEquals(-1L, global.maxTime());
    assertEquals(2, specific.nThreads());
    assertEquals(1234L, specific.maxTime());
  }

  @Test(expected = IllegalArgumentException.class)
  public void annotateRejectsDocumentWithoutSentenceAnnotation() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (NN test))")));
    Path modelPath = Files.createTempFile("sentiment-no-sentences-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);

    annotator.annotate(new Annotation("document without sentence maps"));
  }

  @Test
  public void annotateAcceptsAnEmptySentenceList() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (NN test))")));
    Path modelPath = Files.createTempFile("sentiment-empty-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);
    Annotation document = new Annotation("empty");
    List<CoreMap> sentences = Collections.emptyList();
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(document);

    assertSame(sentences, document.get(CoreAnnotations.SentencesAnnotation.class));
  }

  @Test(expected = AssertionError.class)
  public void sentenceProcessingRequiresBinarizedTree() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (NN test))")));
    Path modelPath = Files.createTempFile("sentiment-no-tree-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);

    annotator.doOneSentence(new Annotation("doc"), new Annotation("sentence"));
  }

  @Test
  public void failedSentenceCallbackLeavesSentenceUntouched() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "2"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (NN test))")));
    Path modelPath = Files.createTempFile("sentiment-failed-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);
    Annotation sentence = new Annotation("still here");

    annotator.doOneFailedSentence(new Annotation("doc"), sentence);

    assertEquals("still here", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertFalse(sentence.containsKey(SentimentCoreAnnotations.SentimentClass.class));
    assertFalse(sentence.containsKey(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
  }

  @Test
  public void annotateScoresBinaryTreeAndSetsSentenceOutputs() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-randomSeed", "42"}, 0);
    options.setOption(new String[]{"-numHid", "3"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    Tree trainingTree = Tree.valueOf("(ROOT (JJ great) (NN movie))");
    SentimentModel model = new SentimentModel(options, Collections.singletonList(trainingTree));
    Path modelPath = Files.createTempFile("sentiment-binary-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);
    Annotation sentence = new Annotation("great movie");
    Tree binarized = Tree.valueOf("(ROOT (JJ great) (NN movie))");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    Annotation document = new Annotation("great movie");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    Tree scored = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertNotNull(scored);
    assertNotSame(binarized, scored);
    assertEquals("(ROOT (JJ great) (NN movie))", scored.toString());
    assertTrue(RNNCoreAnnotations.getPredictedClass(scored) >= 0);
    assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
    assertFalse(sentence.get(SentimentCoreAnnotations.SentimentClass.class).isEmpty());
    assertEquals(-1, RNNCoreAnnotations.getPredictedClass(binarized));
  }

  @Test
  public void unaryChainsAreCollapsedBeforeScoring() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-randomSeed", "7"}, 0);
    options.setOption(new String[]{"-numHid", "3"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (JJ excellent))")));
    Path modelPath = Files.createTempFile("sentiment-unary-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);
    Tree binarized = Tree.valueOf("(ROOT (A (B (JJ excellent))))");
    Annotation sentence = new Annotation("excellent");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);

    annotator.doOneSentence(new Annotation("doc"), sentence);

    Tree scored = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertEquals("(ROOT excellent)", scored.toString());
    assertEquals("(ROOT (A (B (JJ excellent))))", binarized.toString());
    assertTrue(RNNCoreAnnotations.getPredictedClass(scored) >= 0);
  }

  @Test
  public void sentimentIsMappedOntoMatchingSpansOfOriginalTree() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-randomSeed", "99"}, 0);
    options.setOption(new String[]{"-numHid", "3"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (JJ good) (NN film))")));
    Path modelPath = Files.createTempFile("sentiment-map-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);
    Tree binarized = Tree.valueOf("(ROOT (JJ good) (NN film))");
    Tree original = Tree.valueOf("(ROOT (NP (JJ good) (NN film)))");
    Annotation sentence = new Annotation("good film");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

    annotator.doOneSentence(new Annotation("doc"), sentence);

    String sentenceSentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    assertEquals(sentenceSentiment,
        ((CoreLabel) original.label()).get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(((CoreLabel) original.firstChild().firstChild().label())
        .get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(((CoreLabel) original.firstChild().lastChild().label())
        .get(SentimentCoreAnnotations.SentimentClass.class));
    assertFalse(((CoreLabel) original.label()).containsKey(CoreAnnotations.SpanAnnotation.class));
    assertFalse(((CoreLabel) original.firstChild().label())
        .containsKey(CoreAnnotations.SpanAnnotation.class));
  }

  @Test(expected = IllegalStateException.class)
  public void preexistingSpanOnOriginalTreeIsRejected() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-numHid", "3"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options,
        Collections.singletonList(Tree.valueOf("(ROOT (JJ good) (NN film))")));
    Path modelPath = Files.createTempFile("sentiment-span-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);
    Tree original = Tree.valueOf("(ROOT (JJ good) (NN film))");
    original.setSpans();
    Annotation sentence = new Annotation("good film");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class,
        Tree.valueOf("(ROOT (JJ good) (NN film))"));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

    annotator.doOneSentence(new Annotation("doc"), sentence);
  }

  @Test
  public void multithreadedAnnotateProcessesTwoIndependentSentences() throws Exception {
    RNNOptions options = new RNNOptions();
    options.setOption(new String[]{"-randomSeed", "123"}, 0);
    options.setOption(new String[]{"-numHid", "3"}, 0);
    options.setOption(new String[]{"-nouseTensors"}, 0);
    SentimentModel model = new SentimentModel(options, Arrays.asList(
        Tree.valueOf("(ROOT (JJ good) (NN film))"),
        Tree.valueOf("(ROOT (JJ bad) (NN movie))")));
    Path modelPath = Files.createTempFile("sentiment-parallel-", ".ser");
    modelPath.toFile().deleteOnExit();
    model.saveSerialized(modelPath.toString());
    Properties properties = new Properties();
    properties.setProperty("sentiment.model", modelPath.toString());
    properties.setProperty("sentiment.nthreads", "2");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", properties);
    Annotation first = new Annotation("good film");
    first.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class,
        Tree.valueOf("(ROOT (JJ good) (NN film))"));
    Annotation second = new Annotation("bad movie");
    second.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class,
        Tree.valueOf("(ROOT (JJ bad) (NN movie))"));
    Annotation document = new Annotation("two sentences");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(first, second));

    annotator.annotate(document);

    assertNotNull(first.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
    assertNotNull(second.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
    assertNotNull(first.get(SentimentCoreAnnotations.SentimentClass.class));
    assertNotNull(second.get(SentimentCoreAnnotations.SentimentClass.class));
  }
}
