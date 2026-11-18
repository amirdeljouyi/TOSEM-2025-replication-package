package edu.stanford.nlp.sentiment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class SentimentPipelinePromptTest {

  @Test
  public void textInputProducesOneAnnotationPerSentence() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline", ".txt");
    Files.write(input, "Cats purr. Dogs bark!".getBytes(StandardCharsets.UTF_8));
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP tokenizer = new StanfordCoreNLP(properties);

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        tokenizer, SentimentPipeline.Input.TEXT, input.toString(), false);

    assertEquals(2, annotations.size());
    assertEquals("Cats purr.", annotations.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Dogs bark!", annotations.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(1, annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).size());
    assertEquals(1, annotations.get(1).get(CoreAnnotations.SentencesAnnotation.class).size());
    assertSame(annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0),
        annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0));
    Files.delete(input);
  }

  @Test
  public void textInputRetainsTokensAndSentenceMetadataFromTokenizer() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-metadata", ".txt");
    Files.write(input, "A small test.".getBytes(StandardCharsets.UTF_8));
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP tokenizer = new StanfordCoreNLP(properties);

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        tokenizer, SentimentPipeline.Input.TEXT, input.toString(), true);
    CoreMap sentence = annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0);

    assertEquals("A small test.", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(4, sentence.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("A", sentence.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
    assertEquals("small", sentence.get(CoreAnnotations.TokensAnnotation.class).get(1).word());
    assertEquals("test", sentence.get(CoreAnnotations.TokensAnnotation.class).get(2).word());
    assertEquals(".", sentence.get(CoreAnnotations.TokensAnnotation.class).get(3).word());
    assertSame(sentence, annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0));
    Files.delete(input);
  }

  @Test
  public void textInputSupportsUnicodeAndMultipleCallsWithSameTokenizer() throws Exception {
    Path first = Files.createTempFile("sentiment-pipeline-unicode", ".txt");
    Path second = Files.createTempFile("sentiment-pipeline-reuse", ".txt");
    Files.write(first, "Café is lovely.".getBytes(StandardCharsets.UTF_8));
    Files.write(second, "Awful weather.".getBytes(StandardCharsets.UTF_8));
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP tokenizer = new StanfordCoreNLP(properties);

    List<Annotation> positive = SentimentPipeline.getAnnotations(
        tokenizer, SentimentPipeline.Input.TEXT, first.toString(), false);
    List<Annotation> negative = SentimentPipeline.getAnnotations(
        tokenizer, SentimentPipeline.Input.TEXT, second.toString(), false);

    assertEquals(1, positive.size());
    assertEquals(1, negative.size());
    assertEquals("Café is lovely.", positive.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Awful weather.", negative.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertFalse(positive.get(0) == negative.get(0));
    Files.delete(first);
    Files.delete(second);
  }

  @Test
  public void emptyTextProducesNoSentenceAnnotations() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-empty", ".txt");
    Files.write(input, new byte[0]);
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP tokenizer = new StanfordCoreNLP(properties);

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        tokenizer, SentimentPipeline.Input.TEXT, input.toString(), false);

    assertNotNull(annotations);
    assertTrue(annotations.isEmpty());
    Files.delete(input);
  }

  @Test
  public void textInputRequiresTokenizer() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-null-tokenizer", ".txt");
    Files.write(input, "Some text.".getBytes(StandardCharsets.UTF_8));

    assertThrows(NullPointerException.class, () -> SentimentPipeline.getAnnotations(
        null, SentimentPipeline.Input.TEXT, input.toString(), false));
    Files.delete(input);
  }

  @Test
  public void missingTextFilePropagatesRuntimeIOException() {
    String missing = new java.io.File("missing-sentiment-input-314159.txt").getAbsolutePath();
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP tokenizer = new StanfordCoreNLP(properties);

    assertThrows(RuntimeIOException.class, () -> SentimentPipeline.getAnnotations(
        tokenizer, SentimentPipeline.Input.TEXT, missing, false));
  }

  @Test
  public void treeInputCreatesAnnotationWithOriginalTreeAndYieldText() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-tree", ".txt");
    Files.write(input,
        "(ROOT (NP (NN Cats)) (VP (VBP purr)))\n".getBytes(StandardCharsets.UTF_8));

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        null, SentimentPipeline.Input.TREES, input.toString(), false);
    CoreMap sentence = annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0);
    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

    assertEquals(1, annotations.size());
    assertEquals("Cats purr", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertNotNull(tree);
    assertEquals("ROOT", tree.value());
    assertEquals("(ROOT (NP (NN Cats)) (VP (VBP purr)))", tree.toString());
    assertSame(sentence, annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0));
    Files.delete(input);
  }

  @Test
  public void treeInputCreatesSeparateAnnotationsForMultipleTrees() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-trees", ".txt");
    Files.write(input, ("(S (NN First))\n" +
        "(S (ADJP (JJ Very) (JJ good)))\n").getBytes(StandardCharsets.UTF_8));

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        null, SentimentPipeline.Input.TREES, input.toString(), false);

    assertEquals(2, annotations.size());
    assertEquals("First", annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Very good", annotations.get(1).get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TextAnnotation.class));
    assertFalse(annotations.get(0) == annotations.get(1));
    Files.delete(input);
  }

  @Test
  public void emptyTreeFileProducesEmptyList() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-empty-trees", ".txt");

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        null, SentimentPipeline.Input.TREES, input.toString(), false);

    assertNotNull(annotations);
    assertTrue(annotations.isEmpty());
    Files.delete(input);
  }

  @Test
  public void filteringDropsUnknownRootAndAttachesGoldLabels() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-filter", ".txt");
    Files.write(input, ("(-1 (0 Uncertain) (0 film))\n" +
        "(3 (1 Good) (4 movie))\n").getBytes(StandardCharsets.UTF_8));

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        null, SentimentPipeline.Input.TREES, input.toString(), true);
    CoreMap sentence = annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0);
    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

    assertEquals(1, annotations.size());
    assertEquals("Good movie", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(3, RNNCoreAnnotations.getGoldClass(tree));
    assertEquals(1, RNNCoreAnnotations.getGoldClass(tree.firstChild()));
    assertEquals(4, RNNCoreAnnotations.getGoldClass(tree.lastChild()));
    Files.delete(input);
  }

  @Test
  public void filteringKeepsAllKnownSentimentExtremes() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-known", ".txt");
    Files.write(input, ("(0 (0 Bad))\n" +
        "(4 (4 Great))\n").getBytes(StandardCharsets.UTF_8));

    List<Annotation> annotations = SentimentPipeline.getAnnotations(
        null, SentimentPipeline.Input.TREES, input.toString(), true);
    Tree first = annotations.get(0).get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(TreeCoreAnnotations.TreeAnnotation.class);
    Tree second = annotations.get(1).get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(TreeCoreAnnotations.TreeAnnotation.class);

    assertEquals(2, annotations.size());
    assertEquals(0, RNNCoreAnnotations.getGoldClass(first));
    assertEquals(4, RNNCoreAnnotations.getGoldClass(second));
    Files.delete(input);
  }

  @Test
  public void filteringRejectsNonNumericSentimentLabels() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-invalid-label", ".txt");
    Files.write(input, "(positive (token word))\n".getBytes(StandardCharsets.UTF_8));

    assertThrows(NumberFormatException.class, () -> SentimentPipeline.getAnnotations(
        null, SentimentPipeline.Input.TREES, input.toString(), true));
    Files.delete(input);
  }

  @Test
  public void nullInputFormatIsRejected() throws Exception {
    Path input = Files.createTempFile("sentiment-pipeline-null-format", ".txt");

    assertThrows(NullPointerException.class, () -> SentimentPipeline.getAnnotations(
        null, null, input.toString(), false));
    Files.delete(input);
  }

  @Test
  public void mainRejectsInvalidInputFormatBeforePipelineConstruction() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> SentimentPipeline.main(new String[] {"-input", "documents", "-file", "x"}));

    assertTrue(exception.getMessage().contains("DOCUMENTS"));
  }

  @Test
  public void mainRejectsInvalidOutputFormatBeforePipelineConstruction() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> SentimentPipeline.main(new String[] {"-output", "json", "-file", "x"}));

    assertTrue(exception.getMessage().contains("JSON"));
  }

  @Test
  public void mainOptionNamesAreCaseInsensitiveButEnumValueStillValidated() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> SentimentPipeline.main(new String[] {"-InPuT", "not-a-format", "-FiLe", "x"}));

    assertTrue(exception.getMessage().contains("NOT-A-FORMAT"));
  }

  @Test
  public void mainRejectsMissingValueForInputOption() {
    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> SentimentPipeline.main(new String[] {"-input"}));
  }

  @Test
  public void mainRejectsMissingValueForOutputOption() {
    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> SentimentPipeline.main(new String[] {"-output"}));
  }

  @Test
  public void mainRejectsUnknownArgumentAndReportsFollowingToken() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> SentimentPipeline.main(new String[] {"-mystery", "payload"}));

    assertEquals("Unknown argument payload", exception.getMessage());
  }
}
