package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class StanfordCoreNLPPromptTest {

  @Test
  public void constructorRetainsExplicitPropertiesAndCopiesFileListAlias() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    properties.setProperty("fileList", "documents.txt");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

    assertSame(properties, pipeline.getProperties());
    assertEquals("documents.txt", pipeline.getProperties().getProperty("filelist"));
    assertEquals("UTF-8", pipeline.getEncoding());
  }

  @Test
  public void getEncodingReturnsConfiguredEncoding() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    properties.setProperty("encoding", "UTF-16LE");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties, false);

    assertEquals("UTF-16LE", pipeline.getEncoding());
  }

  @Test
  public void normalizeAnnotatorsAbsorbsLegacyStagesIntoTokenizer() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,cleanxml,ssplit,cdc_tokenize");

    StanfordCoreNLP.normalizeAnnotators(properties);

    assertEquals("tokenize,tokenize", properties.getProperty("annotators"));
    assertEquals("true", properties.getProperty("tokenize.cleanxml"));
  }

  @Test
  public void normalizeAnnotatorsLeavesUnrelatedListUntouched() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "docdate,quote");

    StanfordCoreNLP.normalizeAnnotators(properties);

    assertEquals("docdate,quote", properties.getProperty("annotators"));
    assertNull(properties.getProperty("tokenize.cleanxml"));
  }

  @Test
  public void replaceAnnotatorReplacesEveryLegacyOccurrence() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "old,oldish,old");

    StanfordCoreNLP.replaceAnnotator(properties, "old", "new");

    assertEquals("new,newish,new", properties.getProperty("annotators"));
  }

  @Test
  public void unifyTokenizePropertyRemovesMiddleAnnotatorAndSetsOption() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,legacy,pos");

    StanfordCoreNLP.unifyTokenizeProperty(properties, "legacy", "tokenize.legacy");

    assertEquals("tokenize,pos", properties.getProperty("annotators"));
    assertEquals("true", properties.getProperty("tokenize.legacy"));
  }

  @Test
  public void unifyTokenizePropertyRemovesTrailingAnnotator() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,legacy");

    StanfordCoreNLP.unifyTokenizeProperty(properties, "legacy", null);

    assertEquals("tokenize", properties.getProperty("annotators"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void unifyTokenizePropertyRejectsSingleAmbiguousAnnotator() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize");

    StanfordCoreNLP.unifyTokenizeProperty(properties, "tokenize", null);
  }

  @Test
  public void ensurePrerequisitesForLemmaAddsTokenizerAndTaggerInOrder() {
    Properties properties = new Properties();

    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"lemma"}, properties);

    assertEquals("tokenize,pos,lemma", result);
    assertTrue(properties.isEmpty());
  }

  @Test
  public void ensurePrerequisitesForParseUsesParserForPos() {
    Properties properties = new Properties();

    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"parse"}, properties);

    assertEquals("tokenize,parse", result);
  }

  @Test
  public void ensurePrerequisitesPlacesRegexNerImmediatelyAfterNer() {
    Properties properties = new Properties();

    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(
        new String[]{"regexner", "ner"}, properties);

    assertEquals("tokenize,pos,lemma,ner,regexner", result);
  }

  @Test
  public void ensurePrerequisitesForCorefSelectsDependencyMentionDetection() {
    Properties properties = new Properties();

    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"coref"}, properties);

    assertEquals("tokenize,pos,lemma,ner,depparse,coref", result);
    assertEquals("dep", properties.getProperty("coref.md.type"));
  }

  @Test
  public void ensurePrerequisitesDoesNotOverwriteExplicitMentionDetectionMode() {
    Properties properties = new Properties();
    properties.setProperty("coref.md.type", "rule");

    StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"coref.mention"}, properties);

    assertEquals("rule", properties.getProperty("coref.md.type"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void ensurePrerequisitesRejectsUnknownAnnotator() {
    StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"not-a-real-annotator"}, new Properties());
  }

  @Test
  public void ensurePrerequisitesAcceptsNoAnnotators() {
    assertEquals("", StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[0], new Properties()));
  }

  @Test
  public void usesBinaryTreesOnlyWhenSentimentIsAConfiguredStage() {
    Properties sentiment = new Properties();
    sentiment.setProperty("annotators", "tokenize, sentiment");
    Properties substringOnly = new Properties();
    substringOnly.setProperty("annotators", "sentimental");

    assertTrue(StanfordCoreNLP.usesBinaryTrees(sentiment));
    assertFalse(StanfordCoreNLP.usesBinaryTrees(substringOnly));
    assertFalse(StanfordCoreNLP.usesBinaryTrees(new Properties()));
  }

  @Test
  public void processWithEmptyPipelinePreservesTextAndReturnsFreshAnnotations() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

    Annotation first = pipeline.process("Café has text.");
    Annotation second = pipeline.process("Café has text.");

    assertEquals("Café has text.", first.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Café has text.", second.toString());
    assertNotSame(first, second);
  }

  @Test
  public void processToCoreDocumentWrapsTheProcessedAnnotation() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

    CoreDocument document = pipeline.processToCoreDocument("A tiny document.");

    assertEquals("A tiny document.", document.text());
    assertEquals("A tiny document.", document.annotation().toString());
    assertNull(document.tokens());
  }

  @Test
  public void annotateCoreDocumentPreservesIdentityAndText() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    CoreDocument document = new CoreDocument("Wrapped text");
    Annotation underlying = document.annotation();

    pipeline.annotate(document);

    assertSame(underlying, document.annotation());
    assertEquals("Wrapped text", document.text());
  }

  @Test
  public void synchronousCallbackReceivesSameAnnotatedInstance() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("callback");
    AtomicReference<Annotation> delivered = new AtomicReference<>();

    pipeline.annotate(annotation, delivered::set);

    assertSame(annotation, delivered.get());
  }

  @Test
  public void threadedCallbackCompletesAndReceivesSameInstance() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    properties.setProperty("threads", "2");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("asynchronous callback");
    AtomicReference<Annotation> delivered = new AtomicReference<>();
    CountDownLatch completed = new CountDownLatch(1);

    pipeline.annotate(annotation, result -> {
      delivered.set(result);
      completed.countDown();
    });

    assertTrue(completed.await(5, TimeUnit.SECONDS));
    assertSame(annotation, delivered.get());
    assertNull(annotation.get(CoreAnnotations.ExceptionAnnotation.class));
  }

  @Test
  public void timingInformationCountsTokensAddedToAnnotation() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("two tokens");
    annotation.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(new CoreLabel(), new CoreLabel()));

    pipeline.annotate(annotation);

    assertTrue(pipeline.timingInformation().contains("for 2 tokens at"));
    assertTrue(pipeline.timingInformation().contains("tokens/sec"));
  }

  @Test
  public void prettyPrintSupportsOutputStreamAndWriterEntryPoints() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("Visible text");
    CoreLabel token = new CoreLabel();
    token.setWord("Visible");
    token.setOriginalText("Visible");
    token.setBeginPosition(0);
    token.setEndPosition(7);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    StringWriter characters = new StringWriter();
    PrintWriter printWriter = new PrintWriter(characters);

    pipeline.prettyPrint(annotation, bytes);
    pipeline.prettyPrint(annotation, printWriter);
    printWriter.flush();

    assertTrue(new String(bytes.toByteArray(), StandardCharsets.UTF_8).contains("Visible text"));
    assertTrue(characters.toString().contains("Visible text"));
  }

  @Test
  public void jsonPrintUsesConfiguredEncodingAndFlushesWriter() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    properties.setProperty("encoding", "UTF-8");
    properties.setProperty("output.includeText", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("naïve JSON");
    StringWriter writer = new StringWriter();

    pipeline.jsonPrint(annotation, writer);

    assertTrue(writer.toString().startsWith("{"));
    assertTrue(writer.toString().contains("naïve JSON"));
  }

  @Test(expected = NoClassDefFoundError.class)
  public void xmlPrintPropagatesMissingOptionalXmlDependency() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("XML & text");
    pipeline.xmlPrint(annotation, new StringWriter());
  }

  @Test
  public void createOutputterDispatchesToJsonOutputterCaseInsensitively() {
    Properties properties = new Properties();
    properties.setProperty("outputFormat", "json");
    properties.setProperty("output.includeText", "true");
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    StanfordCoreNLP.createOutputter(properties, new AnnotationOutputter.Options(properties))
        .accept(new Annotation("from consumer"), output);

    String json = new String(output.toByteArray(), StandardCharsets.UTF_8);
    assertTrue(json.startsWith("{"));
    assertTrue(json.contains("from consumer"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createOutputterRejectsUnknownFormatWhenInvoked() {
    Properties properties = new Properties();
    properties.setProperty("outputFormat", "made-up");

    StanfordCoreNLP.createOutputter(properties, new AnnotationOutputter.Options())
        .accept(new Annotation("text"), new ByteArrayOutputStream());
  }

  @Test
  public void annotatorSignatureHasValueSemanticsAndReadableText() {
    StanfordCoreNLP.AnnotatorSignature first =
        new StanfordCoreNLP.AnnotatorSignature("tokenize", "language:en");
    StanfordCoreNLP.AnnotatorSignature equal =
        new StanfordCoreNLP.AnnotatorSignature("tokenize", "language:en");
    StanfordCoreNLP.AnnotatorSignature different =
        new StanfordCoreNLP.AnnotatorSignature("tokenize", "language:fr");

    assertEquals(first, equal);
    assertEquals(first.hashCode(), equal.hashCode());
    assertNotEquals(first, different);
    assertNotEquals(first, null);
    assertNotEquals(first, "tokenize");
    assertTrue(first.toString().contains("name='tokenize'"));
    assertTrue(first.toString().contains("signature='language:en'"));
  }

  @Test
  public void conllPrintHandlesAnnotationWithoutTokens() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    StringWriter writer = new StringWriter();

    pipeline.conllPrint(new Annotation("no tokens"), writer);

    assertEquals("", writer.toString());
  }

  @Test
  public void processFilesReadsAnnotatesAndWritesJsonToConfiguredDirectory() throws Exception {
    Path inputDirectory = Files.createTempDirectory("corenlp-input-");
    Path outputDirectory = Files.createTempDirectory("corenlp-output-");
    Path input = inputDirectory.resolve("document.txt");
    Files.write(input, "file café".getBytes(StandardCharsets.UTF_8));
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    properties.setProperty("encoding", "UTF-8");
    properties.setProperty("outputFormat", "json");
    properties.setProperty("output.includeText", "true");
    properties.setProperty("outputDirectory", outputDirectory.toString());
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

    pipeline.processFiles(Collections.singletonList(input.toFile()), false, Optional.empty());

    Path output = outputDirectory.resolve("document.txt.json");
    String generated = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
    assertTrue(generated.startsWith("{"));
    assertTrue(generated.contains("file café"));
    Files.delete(output);
    Files.delete(input);
    Files.delete(outputDirectory);
    Files.delete(inputDirectory);
  }

  @Test
  public void processFilesHonorsNoClobberForExistingOutput() throws Exception {
    Path inputDirectory = Files.createTempDirectory("corenlp-input-");
    Path outputDirectory = Files.createTempDirectory("corenlp-output-");
    Path input = inputDirectory.resolve("document.txt");
    Path output = outputDirectory.resolve("document.txt.json");
    Files.write(input, "new content".getBytes(StandardCharsets.UTF_8));
    Files.write(output, "keep me".getBytes(StandardCharsets.UTF_8));
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    properties.setProperty("outputFormat", "json");
    properties.setProperty("outputDirectory", outputDirectory.toString());
    properties.setProperty("noClobber", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

    pipeline.processFiles(Collections.singletonList(input.toFile()), 1, false, Optional.empty());

    assertEquals("keep me", new String(Files.readAllBytes(output), StandardCharsets.UTF_8));
    Files.delete(output);
    Files.delete(input);
    Files.delete(outputDirectory);
    Files.delete(inputDirectory);
  }
}
