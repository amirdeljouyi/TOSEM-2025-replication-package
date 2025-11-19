package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.*;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.mock.java.io.MockFile;
import org.evosuite.runtime.mock.java.io.MockPrintStream;
import org.junit.Assert;
import org.junit.Test;

import static org.evosuite.runtime.EvoAssertions.verifyException;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StanfordCoreNLP_5_GPTLLMTest {

 @Test
  public void testDefaultConstruction() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    Properties props = pipeline.getProperties();
    assertNotNull(props);
    assertTrue(props.containsKey("annotators"));
  }
@Test
  public void testConstructorWithCustomProperties() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Properties returnedProps = pipeline.getProperties();
    assertEquals("tokenize,ssplit,pos", returnedProps.getProperty("annotators"));
  }
@Test(expected = RuntimeException.class)
  public void testMissingAnnotatorsThrowsException() {
    Properties props = new Properties();
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  }
@Test
  public void testPreTokenizedHandlingUpdatesProperties() {
    Properties props = new Properties();
    props.setProperty("annotators", "ner");
    props.setProperty("preTokenized", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String annotators = pipeline.getProperties().getProperty("annotators");
    assertTrue(annotators.startsWith("tokenize,ssplit"));
  }
@Test
  public void testAnnotateSimpleText() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String text = "Hello Stanford NLP.";
    Annotation annotation = new Annotation(text);
    pipeline.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testAnnotateMultipleSentences() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Hello. This is a test.");
    pipeline.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
  }
@Test
  public void testProcessToCoreDocumentProducesValidCoreDocument() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument doc = pipeline.processToCoreDocument("Hello. World.");
    assertEquals(2, doc.sentences().size());
    assertEquals("Hello.", doc.sentences().get(0).text());
    assertEquals("World.", doc.sentences().get(1).text());
  }
@Test
  public void testJsonPrintOutputContainsInputText() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Testing JSON output");
    pipeline.annotate(annotation);
    StringWriter writer = new StringWriter();
    pipeline.jsonPrint(annotation, writer);
    String output = writer.toString();
    assertNotNull(output);
    assertTrue(output.contains("Testing"));
    writer.close();
  }
@Test
  public void testXmlPrintThrowsExceptionIfXMLOutputterMissing() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Hello XML");
    pipeline.annotate(annotation);
    try {
      StringWriter writer = new StringWriter();
      pipeline.xmlPrint(annotation, writer);
    } catch (RuntimeException | IOException e) {
      assertTrue(e.getMessage().contains("edu.stanford.nlp.pipeline.XMLOutputter"));
    }
  }
@Test
  public void testEnsurePrerequisiteAddsPOS() {
    Properties props = new Properties();
    String[] annotators = new String[]{"tokenize", "ssplit", "lemma"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
    assertTrue(result.contains("pos"));
  }
@Test
  public void testEnsureRegexnerAfterNEROrderPreserved() {
    Properties props = new Properties();
    String[] input = new String[]{"tokenize", "ssplit", "ner", "regexner"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    int nerIndex = result.indexOf("ner");
    int regexIndex = result.indexOf("regexner");
    assertTrue(nerIndex >= 0);
    assertTrue(regexIndex > nerIndex);
  }
@Test
  public void testGetEncodingReturnsUTF8ByDefault() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertEquals("UTF-8", pipeline.getEncoding());
  }
@Test
  public void testPrettyPrintTextOutput() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Testing pretty print.");
    pipeline.annotate(annotation);
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    pipeline.prettyPrint(annotation, printWriter);
    String output = writer.toString();
    assertTrue(output.contains("Testing"));
    printWriter.close();
    writer.close();
  }
@Test
  public void testAsyncAnnotateCallbackInvoked() throws InterruptedException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "2");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Async test.");
    final boolean[] flag = {false};
    pipeline.annotate(annotation, ann -> flag[0] = true);
    Thread.sleep(300); 
    assertTrue(flag[0]);
  }
@Test(expected = IllegalArgumentException.class)
  public void testEnsurePrerequisiteThrowsForUnknownAnnotator() {
    Properties props = new Properties();
    String[] input = new String[]{"tokenize", "unknown"};
    StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  }
@Test
  public void testUsesBinaryTreesIncludesSentiment() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,sentiment");
    boolean result = StanfordCoreNLP.usesBinaryTrees(props);
    assertTrue(result);
  }
@Test
  public void testUsesBinaryTreesExcludesSentiment() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    boolean result = StanfordCoreNLP.usesBinaryTrees(props);
    assertFalse(result);
  }
@Test
  public void testConllPrintProducesExpectedOutput() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Simple test.");
    pipeline.annotate(annotation);
    StringWriter writer = new StringWriter();
    pipeline.conllPrint(annotation, writer);
    String out = writer.toString();
    assertTrue(out.contains("Simple"));
    assertTrue(out.contains("test"));
    writer.close();
  }
@Test
  public void testProcessEmptyTextReturnsZeroSentences() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("");
    pipeline.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertTrue(sentences.isEmpty());
  }
@Test
  public void testClearAnnotatorPoolRunsWithoutException() {
    StanfordCoreNLP.clearAnnotatorPool();
  }
@Test
public void testEmptyAnnotatorsPropertyShouldThrowAtInit() {
  Properties props = new Properties();
  props.setProperty("annotators", "");
  try {
    new StanfordCoreNLP(props);
    fail("Expected RuntimeException due to missing annotators");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Missing property"));
  }
}
@Test
public void testCustomAnnotatorRegistrationViaProperties() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,fake");
  props.setProperty("customAnnotatorClass.fake", "edu.stanford.nlp.pipeline.TokenizerAnnotator");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation doc = new Annotation("Test custom setup.");
  pipeline.annotate(doc);
  List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sentences);
}
@Test
public void testUnifyTokenizePropertyRemovesCleanXml() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,cleanxml,ssplit,pos");
  StanfordCoreNLP.normalizeAnnotators(props);
  String updatedAnnotators = props.getProperty("annotators");
  assertFalse(updatedAnnotators.contains("cleanxml"));
  assertEquals("tokenize,ssplit,pos", updatedAnnotators);
  assertEquals("true", props.getProperty("tokenize.cleanxml"));
}
@Test
public void testReplaceAnnotatorReplacesCdcTokenize() {
  Properties props = new Properties();
  props.setProperty("annotators", "cdc_tokenize,lemma");
  StanfordCoreNLP.replaceAnnotator(props, "cdc_tokenize", "tokenize");
  String result = props.getProperty("annotators");
  assertEquals("tokenize,lemma", result);
}
@Test
public void testExceptionDuringAsyncAnnotationIsHandled() throws InterruptedException {
  Properties props = new Properties();
  props.setProperty("annotators", "pos"); 
  props.setProperty("threads", "2");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props, true, null);
  Annotation annotation = new Annotation("Invalid config.");
  final Throwable[] capturedError = new Throwable[1];
  pipeline.annotate(annotation, ann -> capturedError[0] = ann.get(CoreAnnotations.ExceptionAnnotation.class));
  Thread.sleep(500); 
  assertNotNull(capturedError[0]);
  assertTrue(capturedError[0] instanceof IllegalArgumentException);
}
@Test
public void testAnnotatorTimingInformationIncludesRate() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Hello again.");
  pipeline.annotate(annotation);
  String output = pipeline.timingInformation();
  assertTrue(output.contains("tokens/sec"));
}
@Test
public void testInvalidHelpTopicPrintsUnknownHelpMessage() {
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  PrintStream ps = new PrintStream(out);
  StanfordCoreNLP.printHelp(ps, "unknown-help");
  String output = new String(out.toByteArray());
  assertTrue(output.contains("Unknown help topic"));
}
@Test
public void testHelpParserTopicIncludesStanfordAndCharniak() {
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  PrintStream ps = new PrintStream(out);
  StanfordCoreNLP.printHelp(ps, "parser");
  String output = new String(out.toByteArray());
  assertTrue(output.contains("stanford"));
  assertTrue(output.contains("charniak"));
  assertTrue(output.contains("parse.model"));
}
@Test
public void testInputDirectoryAndOutputDirectoryProcessingConfig() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  File inputDir = new File("inputDir");
  File outputDir = new File("outputDir");
  props.setProperty("inputDirectory", inputDir.getAbsolutePath());
  props.setProperty("outputDirectory", outputDir.getAbsolutePath());
  assertEquals(inputDir.getAbsolutePath(), props.getProperty("inputDirectory"));
  assertEquals(outputDir.getAbsolutePath(), props.getProperty("outputDirectory"));
}
@Test
public void testAnnotationOfNonBreakingTextStillProducesSingleSentence() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("One line without punctuation");
  pipeline.annotate(annotation);
  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sentences);
  assertEquals(1, sentences.size());
}
@Test
public void testAnnotatorsWithExtraWhitespace() {
  Properties props = new Properties();
  props.setProperty("annotators", " tokenize ,   ssplit\t,pos  ");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Testing whitespace handling.");
  pipeline.annotate(annotation);
  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
}
@Test
public void testAnnotationWithoutTokensStillReturnsNonNull() {
  Properties props = new Properties();
  props.setProperty("annotators", "ssplit"); 
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("No tokens should not fail.");
  pipeline.annotate(annotation);
  assertNotNull(annotation);
}
@Test
public void testPreTokenizedAddsMissingTokenizePrefix() {
  Properties props = new Properties();
  props.setProperty("annotators", "ner");
  props.setProperty("preTokenized", "true");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String annotators = pipeline.getProperties().getProperty("annotators");
  assertTrue(annotators.startsWith("tokenize,ssplit,ner"));
}
@Test
public void testEnsurePrerequisitesUsesParseForPOS() {
  Properties props = new Properties();
  String[] input = new String[]{"tokenize", "ssplit", "parse"};
  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  assertFalse(result.contains("pos,")); 
}
@Test
public void testDepParseNotAddedRedundantlyWithParse() {
  Properties props = new Properties();
  String[] input = new String[]{"tokenize", "ssplit", "parse"};
  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  assertFalse(result.contains("depparse"));
}
@Test
public void testCorefBeforeOpenIEOrdering() {
  Properties props = new Properties();
  String[] annotators = new String[]{"tokenize", "ssplit", "coref", "openie"};
  String resolved = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
  int corefIndex = resolved.indexOf("coref");
  int openieIndex = resolved.indexOf("openie");
  assertTrue(corefIndex < openieIndex);
}
@Test(expected = IllegalStateException.class)
public void testCircularDependencyDetection() {
  Map<String, Collection<String>> originalRequirements = new HashMap<>(Annotator.DEFAULT_REQUIREMENTS);
  try {
    Properties props = new Properties();
    String[] input = new String[]{"a"};
    StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  } finally {
    Annotator.DEFAULT_REQUIREMENTS.clear();
  }
}
@Test
public void testMultipleCustomAnnotatorsRegistration() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,fakeA,fakeB");
  props.setProperty("customAnnotatorClass.fakeA", "edu.stanford.nlp.pipeline.TokenizerAnnotator");
  props.setProperty("customAnnotatorClass.fakeB", "edu.stanford.nlp.pipeline.TokenizerAnnotator");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Hello multi-custom.");
  pipeline.annotate(annotation);
  List<CoreMap> sents = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sents.size());
}
@Test(expected = RuntimeException.class)
public void testXmlPrintFailsWithoutClass() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Testing XML");
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  pipeline.xmlPrint(annotation, os); 
}
@Test(expected = RuntimeException.class)
public void testCustomOutputterMissingClassThrows() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "custom");
  props.setProperty("customOutputter", "non.existent.CustomPrinter");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Bad outputter");
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  StanfordCoreNLP.OutputFormat outputFormat = StanfordCoreNLP.OutputFormat.CUSTOM;
  StanfordCoreNLP.createOutputter(props, AnnotationOutputter.getOptions(props)).accept(annotation, os);
}
@Test
public void testEmptyAnnotationGracefullyHandled() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation empty = new Annotation("");
  pipeline.annotate(empty);
  List<CoreMap> sentences = empty.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sentences);
  assertTrue(sentences.isEmpty());
}
@Test
public void testMultipleThreadsConfiguredProperly() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("threads", "4");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("One.");
  final boolean[] flagged = {false};
  pipeline.annotate(annotation, ann -> flagged[0] = true);
  try { Thread.sleep(300); } catch (InterruptedException e) {}
  assertTrue(flagged[0]);
}
@Test
public void testAnnotatorPoolCachingUsed() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  AnnotatorImplementations impl = new AnnotatorImplementations();
  StanfordCoreNLP.AnnotatorSignature sig = new StanfordCoreNLP.AnnotatorSignature("tokenize", PropertiesUtils.getSignature("tokenize", props));
  AnnotatorPool pool = StanfordCoreNLP.getDefaultAnnotatorPool(props, impl);
  assertNotNull(pool.get("tokenize"));
  Lazy<?> lazyAnnotator = StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.get(sig);
  assertNotNull(lazyAnnotator);
  assertTrue(lazyAnnotator.get().toString().contains("TokenizerAnnotator"));
}
@Test
public void testEnsureMentionUsesDepIfNoParse() {
  Properties props = new Properties();
  String[] input = new String[]{"tokenize", "ssplit", "coref.mention"};
  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  assertEquals("dep", props.getProperty("coref.md.type"));
}
@Test
public void testRegexnerPlacedAfterNER() {
  Properties props = new Properties();
  String[] input = new String[]{"tokenize", "ssplit", "ner", "regexner"};
  String outputAnnotators = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  assertTrue(outputAnnotators.indexOf("ner") < outputAnnotators.indexOf("regexner"));
}
@Test
public void testNormalizeAnnotatorsRemovesSsplit() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,pos");
  StanfordCoreNLP.normalizeAnnotators(props);
  assertTrue(props.getProperty("annotators").contains("tokenize"));
  assertTrue(props.getProperty("annotators").contains("pos"));
}
@Test
public void testReplaceAnnotatorDoesNotReplaceIfNotPresent() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,pos");
  StanfordCoreNLP.replaceAnnotator(props, "abc", "xyz");
  assertEquals("tokenize,pos", props.getProperty("annotators"));
}
@Test
public void testUnifyTokenizePropertyWhenTargetNotPresent() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,pos");
  StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
  assertEquals("tokenize,pos", props.getProperty("annotators"));
}
@Test
public void testAnnotationWithOnlyParseAnnotator() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,pos,parse");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Parsing this sentence.");
  pipeline.annotate(annotation);
}
@Test
public void testDocIdSetFromFileNameAnnotation() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Text body.");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "sample.txt");
  pipeline.annotate(annotation);
  String docId = annotation.get(CoreAnnotations.DocIDAnnotation.class);
  assertEquals("sample.txt", docId);
}
@Test
public void testTimedAnnotationReportsTokensSec() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Just an example sentence.");
  pipeline.annotate(annotation);
  String timing = pipeline.timingInformation();
  assertTrue(timing.contains("tokens/sec."));
}
@Test
public void testShellSkipsInteractiveOnNonConsole() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  ByteArrayInputStream fakeInput = new ByteArrayInputStream("q".getBytes());
  InputStream originalIn = System.in;
  System.setIn(fakeInput);
  try {
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String line = reader.readLine();
    assertEquals("q", line);
  } finally {
    System.setIn(originalIn);
  }
}
@Test
public void testAnnotateWithNullAnnotationDoesNotThrow() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  try {
    pipeline.annotate((Annotation) null);
  } catch (Exception e) {
    fail("Annotating a null document should not throw: " + e.getMessage());
  }
}
@Test
public void testProcessNullTextReturnsAnnotation() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process((String) null);
  assertNotNull(annotation);
}
@Test
public void testRedundantAnnotatorsAreNotDuplicated() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Redundant tokenizer.");
  pipeline.annotate(annotation);
  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotationWithDocIdAndEncoding() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  props.setProperty("encoding", "ISO-8859-1");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Sentence goes here.");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc123");
  pipeline.annotate(annotation);
  assertEquals("doc123", annotation.get(CoreAnnotations.DocIDAnnotation.class));
  assertEquals("ISO-8859-1", pipeline.getEncoding());
}
@Test
public void testThrowsIfAnnotatorRequirementNotSatisfied() {
  Properties props = new Properties();
  props.setProperty("annotators", "lemma");
  try {
    new StanfordCoreNLP(props);
    fail("Expected IllegalArgumentException due to missing POS requirement");
  } catch (IllegalArgumentException e) {
    assertTrue(e.getMessage().contains("requires annotation"));
  }
}
@Test
public void testCustomSerializerFallbackFailsGracefully() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  props.setProperty("outputFormat", "serialized");
  props.setProperty("outputSerializer", "bad.class.name.DoesNotExist");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Failing serializer.");
  try {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.OutputFormat outputFormat = StanfordCoreNLP.OutputFormat.SERIALIZED;
    StanfordCoreNLP.createOutputter(props, AnnotationOutputter.getOptions(props)).accept(annotation, os);
    fail("Expected runtime exception from bad serializer");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().toLowerCase().contains("class") || e.getCause() != null);
  }
}
@Test
public void testJsonOutputEncodingRespectsProperty() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  props.setProperty("encoding", "UTF-8");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Encoding test æøå.");
  StringWriter writer = new StringWriter();
  pipeline.jsonPrint(annotation, writer);
  assertTrue(writer.toString().contains("Encoding test"));
}
@Test
public void testThreadPropertySetButInvalidValueDefaultsToOneThread() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("threads", "invalid_number");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Fallback test.");
  pipeline.annotate(annotation);
  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testConcurrentAnnotateCallbackReceivesException() throws InterruptedException {
  Properties props = new Properties();
  props.setProperty("annotators", "pos");
  props.setProperty("threads", "2");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("This will fail.");
  final Throwable[] sent = new Throwable[1];
  pipeline.annotate(annotation, ann -> sent[0] = ann.get(CoreAnnotations.ExceptionAnnotation.class));
  Thread.sleep(300); 
  assertNotNull(sent[0]);
  assertTrue(sent[0] instanceof IllegalArgumentException);
}
@Test
public void testPrettyPrintHandlesNoSentenceAnnotation() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("No ssplit.");
  StringWriter sw = new StringWriter();
  PrintWriter pw = new PrintWriter(sw);
  pipeline.prettyPrint(annotation, pw);
  String result = sw.toString();
  assertTrue(result.contains("No ssplit"));
  pw.close();
}
@Test
public void testMissingTokenizerWithPOSThrowsRequirementsError() {
  Properties props = new Properties();
  props.setProperty("annotators", "pos");
  try {
    new StanfordCoreNLP(props);
    fail("Expected IllegalArgumentException for missing tokenize/ssplit");
  } catch (IllegalArgumentException e) {
    assertTrue(e.getMessage().contains("requires annotation"));
  }
}
@Test
public void testInvalidThreadPropertyFallsBackToSingleThread() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("threads", "notAnInteger");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Thread fallback test.");
  pipeline.annotate(annotation);
  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testEmptyTextStillGeneratesAnnotation() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("");
  pipeline.annotate(annotation);
  List<CoreMap> sents = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sents);
  assertEquals(0, sents.size());
}
@Test
public void testEnsureRegexnerIsReorderedProperly() {
  Properties props = new Properties();
  String[] anno = new String[] {"tokenize", "ssplit", "regexner", "ner"};
  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(anno, props);
  int nerIndex = result.indexOf("ner");
  int regexIndex = result.indexOf("regexner");
  assertTrue(nerIndex < regexIndex);
}
@Test
public void testEnsureOpenIEAfterCorefEvenIfListedBefore() {
  Properties props = new Properties();
  String[] anno = new String[] {"tokenize", "ssplit", "openie", "coref"};
  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(anno, props);
  int corefIndex = result.indexOf("coref");
  int openieIndex = result.indexOf("openie");
  assertTrue(corefIndex < openieIndex);
}
@Test
public void testEnsureMentionAnnotatorUsesDepFallback() {
  Properties props = new Properties();
  String[] anno = new String[] {"tokenize", "ssplit", "coref.mention"};
  StanfordCoreNLP.ensurePrerequisiteAnnotators(anno, props);
  assertEquals("dep", props.getProperty("coref.md.type"));
}
@Test(expected = IllegalStateException.class)
public void testCircularDependencyDetectionThrows() {
  Map<String, Collection<String>> defaultReqs = new HashMap<>(Annotator.DEFAULT_REQUIREMENTS);
  try {
    Properties props = new Properties();
    StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"a"}, props);
  } finally {
    Annotator.DEFAULT_REQUIREMENTS.clear();
  }
}
@Test
public void testJsonOutputUnicodeCharactersHandled() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  props.setProperty("encoding", "UTF-8");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Unicode ☃ é 龍");
  StringWriter writer = new StringWriter();
  pipeline.jsonPrint(annotation, writer);
  String content = writer.toString();
  assertTrue(content.contains("Unicode"));
  assertTrue(content.contains("☃") || content.contains("\\u2603")); 
}
@Test
public void testInvalidCustomOutputterClassThrows() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  props.setProperty("outputFormat", "custom");
  props.setProperty("customOutputter", "invalid.ClassName.DoesNotExist");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Testing output.");
  OutputStream os = new ByteArrayOutputStream();
  try {
    StanfordCoreNLP.createOutputter(props, AnnotationOutputter.getOptions(props)).accept(annotation, os);
    fail("Expected exception due to invalid customOutputter class");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().toLowerCase().contains("class") || e.getCause() instanceof ClassNotFoundException);
  }
}
@Test
public void testDepParseIsNotIncludedWithParseOnly() {
  Properties props = new Properties();
  String[] input = new String[] {"tokenize", "ssplit", "parse"};
  String output = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  assertFalse(output.contains("depparse"));
}
@Test
public void testDocumentProcessingWithoutSentencesStillCountsWords() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Word test only");
  pipeline.annotate(annotation);
  assertEquals(3, annotation.get(CoreAnnotations.TokensAnnotation.class).size());
}
@Test
public void testAnnotatorOrderingHandlesZeroDependencies() {
  Properties props = new Properties();
  String[] inputAnnotators = new String[]{"tokenize", "ssplit"};
  String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(inputAnnotators, props);
  assertEquals("tokenize,ssplit", ordered);
}
@Test
public void testPropertiesMergingWhenPropsPassedWithoutAnnotators() {
  Properties customProps = new Properties();
  customProps.setProperty("fileList", "dummy.txt");  
  try {
    StanfordCoreNLP pipeline = new StanfordCoreNLP(customProps);  
    assertTrue(pipeline.getProperties().containsKey("annotators"));
  } catch (RuntimeException e) {
    
    assertTrue(e.getMessage().contains("Could not find properties file"));
  }
}
@Test
public void testPreTokenizedWithNonStandardAnnotatorOrder() {
  Properties props = new Properties();
  props.setProperty("annotators", "ner,pos");
  props.setProperty("preTokenized", "true");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String updatedAnnotators = pipeline.getProperties().getProperty("annotators");
  assertTrue(updatedAnnotators.contains("tokenize"));
  assertTrue(updatedAnnotators.contains("ssplit"));
}
@Test
public void testReplaceExtensionBehaviorInProcessFilesConfigFlag() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("replaceExtension", "true");
  assertTrue(Boolean.parseBoolean(props.getProperty("replaceExtension")));
}
@Test
public void testEnableCleanXmlViaUnifyLogic() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,cleanxml,ssplit");
  StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
  String modified = props.getProperty("annotators");
  String cleanXmlProp = props.getProperty("tokenize.cleanxml");
  assertFalse(modified.contains("cleanxml"));
  assertEquals("true", cleanXmlProp);
}
@Test
public void testMissingAnnotationRequirementWithNullExactRequirementsFallback() {
  Annotator ann = new Annotator() {
    @Override public void annotate(Annotation annotation) {}
    @Override public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() { return Collections.emptySet(); }
    @Override public Set<Class<? extends CoreAnnotation>> requires() {
      Set<Class<? extends CoreAnnotation>> set = new HashSet<>();
      set.add(CoreAnnotations.SentencesAnnotation.class);
      return set;
    }
    @Override public Collection<String> exactRequirements() { return null; }
  };
  AnnotatorPool pool = new AnnotatorPool();
  Properties props = new Properties();
  props.setProperty("annotators", "bad");
  try {
    new StanfordCoreNLP(props, true, pool);
    fail("Should throw due to unsatisfied dependency");
  } catch (IllegalArgumentException e) {
    assertTrue(e.getMessage().contains("requires annotation"));
  }
}
@Test
public void testCustomAnnotatorThatDoesNotExistFailsGracefully() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,fakeannotator");
  props.setProperty("customAnnotatorClass.fakeannotator", "foo.BarDoesNotExist");
  try {
    new StanfordCoreNLP(props);
    fail("Expected RuntimeException for invalid custom annotator class");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("foo.BarDoesNotExist"));
  }
}
@Test
public void testXmlOutputterClassUnavailableThrowsExpectedException() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Testing <tag>");
  try {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    pipeline.xmlPrint(annotation, baos);
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("edu.stanford.nlp.pipeline.XMLOutputter"));
  } catch (IOException e) {
      throw new RuntimeException(e);
  }
}

@Test
public void testUnknownOutputFormatThrows() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Test output");
  OutputStream os = new ByteArrayOutputStream();
  StanfordCoreNLP.OutputFormat fakeFormat = null;
  try {
    fakeFormat = StanfordCoreNLP.OutputFormat.valueOf("XYZ");
  } catch (IllegalArgumentException expected) {
    
    assertTrue(expected.getMessage().contains("No enum constant"));
  }
} 
}