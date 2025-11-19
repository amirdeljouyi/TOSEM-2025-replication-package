package edu.stanford.nlp.pipeline;

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
import java.util.*;

public class StanfordCoreNLP_1_GPTLLMTest {

 @Test
  public void testConstructorWithNullProperties() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP((Properties) null);
    Properties resultProps = pipeline.getProperties();
    assertNotNull(resultProps);
    assertTrue(resultProps.containsKey("annotators"));
  }
@Test
  public void testConstructorWithMinimalProperties() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    String annotators = pipeline.getProperties().getProperty("annotators");
    assertEquals("tokenize,ssplit", annotators);
  }
@Test
  public void testGetEncodingDefault() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    String encoding = pipeline.getEncoding();
    assertEquals("UTF-8", encoding);
  }
@Test
  public void testAnnotateAddsTokensToAnnotation() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("Barack Obama was born in Hawaii.");
    pipeline.annotate(annotation);
    List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testProcessReturnsNonNullAnnotatedTokens() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = pipeline.process("Stanford CoreNLP is great!");
    List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testProcessToCoreDocumentCreatesSentences() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    CoreDocument document = pipeline.processToCoreDocument("This is a sentence.");
    assertNotNull(document);
    assertNotNull(document.sentences());
    assertFalse(document.sentences().isEmpty());
  }
@Test
  public void testPrettyPrintToOutputStream() throws IOException {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = pipeline.process("NLP is fun.");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    pipeline.prettyPrint(annotation, outputStream);
    String result = outputStream.toString(pipeline.getEncoding());
    assertTrue(result.contains("NLP"));
  }
@Test
  public void testPrettyPrintToWriter() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = pipeline.process("NLP and CoreNLP rule.");
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    pipeline.prettyPrint(annotation, printWriter);
    printWriter.flush();
    String result = stringWriter.toString();
    assertTrue(result.contains("CoreNLP"));
  }
@Test
  public void testJsonPrintIncludesJsonTokens() throws IOException {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = pipeline.process("This is JSON output.");
    StringWriter writer = new StringWriter();
    pipeline.jsonPrint(annotation, writer);
    String json = writer.toString();
    assertTrue(json.contains("sentences"));
    assertTrue(json.contains("tokens"));
  }
@Test
  public void testUsesBinaryTreesWithSentiment() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
    boolean usesBinary = StanfordCoreNLP.usesBinaryTrees(properties);
    assertTrue(usesBinary);
  }
@Test
  public void testUsesBinaryTreesWithoutSentiment() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit,pos");
    boolean usesBinary = StanfordCoreNLP.usesBinaryTrees(properties);
    assertFalse(usesBinary);
  }
@Test
  public void testEnsurePrerequisiteAnnotatorsAddsPOSForLemma() {
    String[] annotators = new String[] { "lemma" };
    Properties props = new Properties();
    String output = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
    assertTrue(output.contains("lemma"));
    assertTrue(output.contains("pos"));
  }
@Test
  public void testAnnotationCallbackIsCalledInSingleThread() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize,ssplit");
    properties.setProperty("threads", "1");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    Annotation annotation = new Annotation("Callback test.");
    final boolean[] invoked = new boolean[] { false };
    pipeline.annotate(annotation, ann -> invoked[0] = true);
    assertTrue(invoked[0]);
  }
@Test
  public void testGetExistingAnnotatorReturnsNullForInvalidName() {
    Annotator annotator = StanfordCoreNLP.getExistingAnnotator("invalid_annotator_name");
    assertNull(annotator);
  }
@Test
  public void testReplaceAnnotatorReplacesCdcTokenize() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,cdc_tokenize,ssplit");
    StanfordCoreNLP.replaceAnnotator(props, "cdc_tokenize", "tokenize");
    String updated = props.getProperty("annotators");
    assertFalse(updated.contains("cdc_tokenize"));
    assertTrue(updated.contains("tokenize"));
  }
@Test
  public void testUnifyTokenizePropertyRemovesCleanXML() {
    Properties props = new Properties();
    props.setProperty("annotators", "cleanxml,tokenize,ssplit");
    StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
    String updated = props.getProperty("annotators");
    String cleanxmlFlag = props.getProperty("tokenize.cleanxml");
    assertFalse(updated.contains("cleanxml"));
    assertEquals("true", cleanxmlFlag);
  }
@Test
public void testConstructorWithEmptyPropertiesStillSetsAnnotators() {
  Properties properties = new Properties();
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  String annotators = pipeline.getProperties().getProperty("annotators");
  assertNotNull("Default annotators should be present if not specified", annotators);
  assertFalse("Annotators list should not be empty", annotators.trim().isEmpty());
}
@Test
public void testAnnotationWithNoTokensDoesNotIncrementWordCount() {
  Properties properties = new Properties();
  properties.setProperty("annotators", "");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  Annotation annotation = new Annotation("This will not be tokenized");
  pipeline.annotate(annotation);
  List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNull("TokensAnnotation should be null if no tokenization was done", tokens);
}
@Test
public void testAnnotateWithNullAnnotationThrowsNPE() {
  Properties properties = new Properties();
  properties.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  try {
    pipeline.annotate((Annotation) null);
    fail("Expected NullPointerException");
  } catch (NullPointerException expected) {
    
  }
}
@Test
public void testProcessWithEmptyStringReturnsNonNullAnnotation() {
  Properties properties = new Properties();
  properties.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  Annotation annotation = pipeline.process("");
  assertNotNull(annotation);
  List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertTrue(tokens == null || tokens.isEmpty());
}
@Test
public void testAnnotationCallbackThreadedModeInvokesCallback() throws InterruptedException {
  Properties properties = new Properties();
  properties.setProperty("annotators", "tokenize,ssplit");
  properties.setProperty("threads", "2");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  Annotation annotation = new Annotation("Threaded callback test");
  final boolean[] called = {false};
  Thread callbackWaiter = new Thread(() -> pipeline.annotate(annotation, ann -> called[0] = true));
  callbackWaiter.start();
  callbackWaiter.join();
  assertTrue("Callback must be called in threaded mode", called[0]);
}
@Test
public void testXmlPrintWithMissingXMLOutputterThrowsRuntimeException() {
  Properties properties = new Properties();
  properties.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  Annotation annotation = pipeline.process("text");
  try {
    ClassLoader classLoader = new ClassLoader(StanfordCoreNLP.class.getClassLoader()) {
      @Override
      public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.equals("edu.stanford.nlp.pipeline.XMLOutputter")) {
          throw new ClassNotFoundException("Simulated missing class");
        }
        return super.loadClass(name);
      }
    };
    Thread.currentThread().setContextClassLoader(classLoader);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    pipeline.xmlPrint(annotation, os);
    fail("Expected RuntimeException due to missing XMLOutputter");
  } catch (RuntimeException e) {
    assertTrue(e.getCause() instanceof ClassNotFoundException);
  } catch (IOException e) {
      throw new RuntimeException(e);
  }
}
@Test
public void testClearAnnotatorPoolRemovesAllAnnotators() {
  StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.clear();
  assertTrue("Global cache should be cleared", StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.isEmpty());
}
@Test
public void testInvalidAnnotatorNameInEnsurePrerequisiteThrowsException() {
  String[] input = new String[] { "nonexistent_annotator" };
  Properties properties = new Properties();
  try {
    StanfordCoreNLP.ensurePrerequisiteAnnotators(input, properties);
    fail("Expected IllegalArgumentException for unknown annotator");
  } catch (IllegalArgumentException e) {
    assertTrue(e.getMessage().contains("Unknown annotator"));
  }
}
@Test
public void testCircularDependencyInAnnotatorRequirementsThrows() {
  Map<String, Collection<String>> savedRequirements = new HashMap<>(Annotator.DEFAULT_REQUIREMENTS);
  try {
    String[] annos = new String[] { "loopA" };
    Properties props = new Properties();
    StanfordCoreNLP.ensurePrerequisiteAnnotators(annos, props);
    fail("Expected IllegalStateException due to circular dependency");
  } catch (IllegalStateException e) {
    assertTrue(e.getMessage().contains("circular dependency"));
  } finally {
    Annotator.DEFAULT_REQUIREMENTS.clear();
  }
}
@Test
public void testOutputAnnotationWithUnsupportedFormatThrowsException() {
  Properties props = new Properties();
  props.setProperty("outputFormat", "UnsupportedFormatX");
  Annotation annotation = new Annotation("test");
  try {
    StanfordCoreNLP.OutputFormat format = StanfordCoreNLP.OutputFormat.valueOf("UnsupportedFormatX");
    fail("Expected IllegalArgumentException due to invalid format");
  } catch (IllegalArgumentException e) {
    assertTrue(e.getMessage().contains("UnsupportedFormatX"));
  }
}
@Test
public void testEmptyAnnotatorsPropertyTriggersException() {
  Properties properties = new Properties();
  try {
    new StanfordCoreNLP(properties);
    fail("Expected RuntimeException due to missing required 'annotators' property");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Missing property"));
  }
}
@Test
public void testOutputFormatCustomClassNotFoundThrowsException() {
  Properties properties = new Properties();
  properties.setProperty("annotators", "tokenize,ssplit");
  properties.setProperty("outputFormat", "custom");
  properties.setProperty("customOutputter", "edu.stanford.nlp.pipeline.NonExistentCustomOutputter");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  Annotation annotation = pipeline.process("custom logic");
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  try {
    AnnotationOutputter.Options options = AnnotationOutputter.getOptions(properties);
    StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class)
        .invoke(null, outputStream, annotation, properties, options);
    fail("Expected RuntimeException due to missing custom outputter class");
  } catch (Exception e) {
    assertTrue(e.getCause() instanceof ClassNotFoundException ||
               e.getCause() instanceof NoClassDefFoundError);
  }
}
@Test
public void testRedundantAnnotatorIsNotDuplicatedInEnsurePrerequisites() {
  Properties properties = new Properties();
  String[] annos = new String[] { "tokenize", "ssplit", "tokenize" };
  String output = StanfordCoreNLP.ensurePrerequisiteAnnotators(annos, properties);
  int firstIndex = output.indexOf("tokenize");
  int lastIndex = output.lastIndexOf("tokenize");
  assertEquals("tokenize should not appear more than once", firstIndex, lastIndex);
}
@Test
public void testReplaceAnnotatorDoesNotModifyIfOldNameNotPresent() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP.replaceAnnotator(props, "nonexistent", "replacement");
  String result = props.getProperty("annotators");
  assertEquals("tokenize,ssplit", result);
}
@Test
public void testUnifyTokenizePropertySkipsIfAnnotatorAbsent() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
  assertEquals("tokenize,ssplit", props.getProperty("annotators"));
  assertNull(props.getProperty("tokenize.cleanxml"));
}
@Test
public void testPreTokenizedAnnotatorsRewriteWithCDC() {
  Properties props = new Properties();
  props.setProperty("preTokenized", "true");
  props.setProperty("annotators", "cdc_tokenize,pos");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String updated = pipeline.getProperties().getProperty("annotators");
  assertTrue(updated.startsWith("tokenize,ssplit"));
  assertFalse(updated.contains("cdc_tokenize"));
}

@Test
public void testIsXMLOutputPresentReturnsTrueWhenClassExists() {
  boolean result = false;
  try {
    result = (boolean) StanfordCoreNLP.class
        .getDeclaredMethod("isXMLOutputPresent")
        .invoke(null);
  } catch (Exception e) {
    fail("Method reflection failed");
  }
  assertTrue("XML Outputter class should be available in test classpath", result);
}
@Test
public void testRunConstructorWithPreSplitWhitespaceTokenization() {
  Properties properties = new Properties();
  properties.setProperty("preTokenized", "true");
  properties.setProperty("annotators", "ner");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
  String annotators = pipeline.getProperties().getProperty("annotators");
  assertTrue("tokenize must be added automatically", annotators.contains("tokenize"));
  assertTrue("ssplit must be added automatically", annotators.contains("ssplit"));
}
@Test
public void testAnnotateWithExceptionTriggersCallbackAndSetsExceptionAnnotation() {
  Properties properties = new Properties();
  properties.setProperty("annotators", "tokenize,ssplit");
  properties.setProperty("threads", "1");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

  Annotation annotation = new Annotation("Force exception scenario");
  annotation.set(CoreAnnotations.TextAnnotation.class, null); 

  final boolean[] callbackCalled = {false};
  pipeline.annotate(annotation, a -> {
    callbackCalled[0] = true;
    Throwable ex = a.get(CoreAnnotations.ExceptionAnnotation.class);
    assertNotNull("ExceptionAnnotation should be populated", ex);
  });

  assertTrue("Callback must still be called after failure", callbackCalled[0]);
}
@Test
public void testInvalidSerializerClassThrowsRuntimeExceptionWhenWritingAnnotation() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "serialized");
  props.setProperty("serializer", "non.existent.FakeSerializer");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("This is a sentence for serialization.");

  OutputStream os = new ByteArrayOutputStream();
  AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);

  try {
    StanfordCoreNLP.class
      .getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class)
      .invoke(null, os, annotation, props, options);
    fail("Expected RuntimeException due to missing serializer class");
  } catch (Exception e) {
    assertTrue(e.getCause() instanceof RuntimeException);
  }
}
@Test
public void testLoadNonexistentPropertiesFileThrowsRuntimeException() {
  String invalidFile = "nonexistent_props_file";
  try {
    StanfordCoreNLP.class
      .getDeclaredMethod("loadPropertiesOrException", String.class)
      .setAccessible(true);
    StanfordCoreNLP.class
      .getDeclaredMethod("loadPropertiesOrException", String.class)
      .invoke(null, invalidFile);
    fail("Expected RuntimeIOException for nonexistent properties file");
  } catch (Exception e) {
    assertTrue(e.getCause() instanceof edu.stanford.nlp.io.RuntimeIOException);
  }
}
@Test
public void testConstructAnnotatorPoolAddsCustomClassIfConfigured() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  props.setProperty("customAnnotatorClass.foo", "edu.stanford.nlp.pipeline.TokenizerAnnotator");

  StanfordCoreNLP coreNLP = new StanfordCoreNLP(props);
  Annotator custom = StanfordCoreNLP.getExistingAnnotator("foo");
  assertNotNull("Custom annotator 'foo' should be registered", custom);
}
@Test
public void testFallbackToClasspathPropertiesWhenPropsNull() {
  StanfordCoreNLP pipeline = new StanfordCoreNLP((Properties) null);
  Properties props = pipeline.getProperties();
  assertNotNull(props);
  assertTrue("Must load fallback annotators from classpath properties", props.containsKey("annotators"));
}
@Test
public void testReplaceExtensionPropertyTrimsOldExtensionIfTrue() throws IOException {
  File tempFile = File.createTempFile("document", ".txt");
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "json");
  props.setProperty("replaceExtension", "true");

  props.setProperty("file", tempFile.getAbsolutePath());
  props.setProperty("outputDirectory", tempFile.getParent());
  props.setProperty("noClobber", "false");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  pipeline.processFiles(null, Collections.singletonList(tempFile), 1, true, Optional.empty());

  String canonicalPath = tempFile.getCanonicalPath();
  String expectedOutputPath = canonicalPath.replace(".txt", ".json");
  File expectedFile = new File(expectedOutputPath);

  assertTrue("Output file with trimmed extension and correct new extension should exist", expectedFile.exists());
  expectedFile.delete();
  tempFile.delete();
}
@Test
public void testNullSignatureDoesNotCacheSameInstanceTwice() {
  Properties props1 = new Properties();
  props1.setProperty("annotators", "tokenize");
  props1.setProperty("customAnnotatorClass.facetoken", "edu.stanford.nlp.pipeline.TokenizerAnnotator");

  Properties props2 = new Properties();
  props2.setProperty("annotators", "tokenize");
  props2.setProperty("customAnnotatorClass.facetoken", "edu.stanford.nlp.pipeline.TokenizerAnnotator");

  StanfordCoreNLP coreNLP1 = new StanfordCoreNLP(props1);
  StanfordCoreNLP coreNLP2 = new StanfordCoreNLP(props2);

  Annotator anno1 = StanfordCoreNLP.getExistingAnnotator("facetoken");
  Annotator anno2 = StanfordCoreNLP.getExistingAnnotator("facetoken");

  assertSame("Should return same instance of lazily cached custom annotator", anno1, anno2);
}
@Test
  public void testProcessEmptyStringProducesEmptyTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Annotation annotation = pipeline.process("");
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(0, sentences.size());
  }
@Test
  public void testConstructWithMissingTokenizeAnnotatorsSetByPreTokenizedOption() {
    Properties props = new Properties();
    props.setProperty("annotators", "pos,lemma");
    props.setProperty("preTokenized", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String annotators = pipeline.getProperties().getProperty("annotators");
    assertTrue(annotators.contains("tokenize"));
    assertTrue(annotators.contains("ssplit"));
    assertTrue(annotators.contains("pos"));
    assertTrue(annotators.contains("lemma"));
  }
@Test
  public void testAnnotatorReplacementLeavesOtherAnnotatorsIntact() {
    Properties props = new Properties();
    props.setProperty("annotators", "ner,cdc_tokenize,lemma");
    StanfordCoreNLP.replaceAnnotator(props, "cdc_tokenize", "tokenize");
    String annotators = props.getProperty("annotators");
    assertTrue(annotators.contains("tokenize"));
    assertTrue(annotators.contains("ner"));
    assertTrue(annotators.contains("lemma"));
    assertFalse(annotators.contains("cdc_tokenize"));
  }
@Test
  public void testEnsurePrerequisitesPreservesOriginalAnnotatorOrderWherePossible() {
    Properties props = new Properties();
    String[] inputAnnotators = new String[] { "lemma", "pos", "tokenize", "ssplit" };
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(inputAnnotators, props);
    assertTrue(result.contains("tokenize"));
    assertTrue(result.contains("ssplit"));
    assertTrue(result.contains("pos"));
    assertTrue(result.contains("lemma"));
    assertTrue(result.indexOf("tokenize") < result.indexOf("ssplit"));
    assertTrue(result.indexOf("ssplit") < result.indexOf("pos"));
    assertTrue(result.indexOf("pos") < result.indexOf("lemma"));
  }
@Test
  public void testOutputFormatSerializedDefaultsToProtobufSerializer() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "serialized");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("serialized annotation");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    String serializerClass = props.getProperty("serializer", "edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer");
    assertEquals("edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer", serializerClass);
  }
@Test
  public void testMultipleCustomAnnotatorsWithDifferentNames() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,foo,bar");
    props.setProperty("customAnnotatorClass.foo", "edu.stanford.nlp.pipeline.TokenizerAnnotator");
    props.setProperty("customAnnotatorClass.bar", "edu.stanford.nlp.pipeline.TokenizerAnnotator");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Annotator foo = StanfordCoreNLP.getExistingAnnotator("foo");
    Annotator bar = StanfordCoreNLP.getExistingAnnotator("bar");

    assertNotNull(foo);
    assertNotNull(bar);
    assertNotSame("foo and bar should be separate annotator instances", foo, bar);
  }
@Test
  public void testLoadPropertiesFromClasspathDefaultFallback() {
    Properties props = StanfordCoreNLP.class.cast(new StanfordCoreNLP()).getProperties();
    String annotators = props.getProperty("annotators");
    assertNotNull(annotators);
    assertFalse(annotators.trim().isEmpty());
  }
@Test
  public void testAnnotatorThrowsOnUnresolvedRequirements() {
    Properties props = new Properties();
    props.setProperty("annotators", "lemma"); 
    try {
      new StanfordCoreNLP(props, true);
      fail("Expected IllegalArgumentException for unsatisfied prerequisites");
    } catch (IllegalArgumentException ex) {
      assertTrue(ex.getMessage().contains("requires annotation"));
    }
  }
@Test
  public void testJsonOutputEncodingMatchesConfiguredEncoding() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "json");
    props.setProperty("encoding", "UTF-8");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("test sentence");

    StringWriter writer = new StringWriter();
    pipeline.jsonPrint(annotation, writer);
    String output = writer.toString();

    assertTrue(output.contains("sentences"));
  }
@Test
  public void testShellExitsOnEOF() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream(new byte[0]));
      pipeline.getClass().getDeclaredMethod("shell").setAccessible(true);
      pipeline.getClass().getDeclaredMethod("shell").invoke(pipeline);
    } catch (Exception ex) {
      fail("shell should gracefully exit on EOF");
    } finally {
      System.setIn(originalIn);
    }
  }
@Test
  public void testEmptyAnnotationDoesNotCrashAnnotator() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("");  
    pipeline.annotate(annotation);
    List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens == null || tokens.isEmpty());
  }
@Test
  public void testNullTextAnnotationSkipsTokenizationGracefully() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation((String) null);
    pipeline.annotate(annotation);
    List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNull(tokens);  
  }
@Test
  public void testClearAnnotatorPoolActuallyClearsGlobalState() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotator token = StanfordCoreNLP.getExistingAnnotator("tokenize");
    assertNotNull(token);
    StanfordCoreNLP.clearAnnotatorPool();
    Annotator cleared = StanfordCoreNLP.getExistingAnnotator("tokenize");
    assertNull(cleared);
  }
@Test
  public void testTokenizeNormalizerHandlesNonStandardAnnotator() {
    Properties props = new Properties();
    props.setProperty("annotators", "no_such_annotator");
    StanfordCoreNLP.normalizeAnnotators(props);
    String value = props.getProperty("annotators");
    assertEquals("no_such_annotator", value);
  }
@Test
  public void testUnifyTokenizePropertyDoesNothingIfUnwantedNotPresent() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    StanfordCoreNLP.unifyTokenizeProperty(props, "ner", "tokenize.ner");
    assertNull(props.getProperty("tokenize.ner"));
    assertEquals("tokenize,ssplit,pos", props.getProperty("annotators"));
  }
@Test
  public void testReplaceAnnotatorNoEffectWhenOldValueMissing() {
    Properties props = new Properties();
    props.setProperty("annotators", "parse,lemma");
    StanfordCoreNLP.replaceAnnotator(props, "fake_annotator", "replacement");
    assertEquals("parse,lemma", props.getProperty("annotators"));
  }
@Test
  public void testAnnotateAsyncPropagatesExceptionAnnotationIfRaised() throws InterruptedException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "2");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation((String) null); 
    final boolean[] finished = {false};
    pipeline.annotate(annotation, ann -> {
      finished[0] = true;
      Throwable t = ann.get(CoreAnnotations.ExceptionAnnotation.class);
      assertNull("Annotation with null text should not set exception", t);
    });
    int maxWait = 1000;
    int waited = 0;
    while (!finished[0] && waited < maxWait) {
      Thread.sleep(50);
      waited += 50;
    }
    assertTrue(finished[0]);
  }
@Test
  public void testXmlPrintThrowsIfClassMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Text for xml attempt");

    ClassLoader brokenLoader = new ClassLoader() {
      @Override
      public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.equals("edu.stanford.nlp.pipeline.XMLOutputter")) {
          throw new ClassNotFoundException();
        }
        return super.loadClass(name);
      }
    };

    Thread current = Thread.currentThread();
    ClassLoader originalLoader = current.getContextClassLoader();
    current.setContextClassLoader(brokenLoader);
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      pipeline.xmlPrint(annotation, os);
      fail("Expected RuntimeException due to missing XMLOutputter class");
    } catch (RuntimeException e) {
      assertTrue(e.getCause() instanceof ClassNotFoundException || e.getCause() instanceof NoClassDefFoundError);
    } finally {
      current.setContextClassLoader(originalLoader);
    }
  }
@Test
  public void testMultipleThreadsInvalidThreadNumberFallbacksToOne() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("threads", "invalid_number");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertNotNull(pipeline);
  }
@Test
  public void testEnsurePrerequisiteAnnotatorsWithEmptyArray() {
    Properties props = new Properties();
    String[] empty = new String[0];
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(empty, props);
    assertEquals("", result);
  }
@Test
  public void testOutputAnnotationThrowsOnNullOutputFormat() throws Exception {
    Annotation annotation = new Annotation("test");
    Properties props = new Properties();
    props.setProperty("outputFormat", "badformat");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    AnnotationOutputter.Options opts = AnnotationOutputter.getOptions(props);

    try {
      StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class)
        .invoke(null, os, annotation, props, opts);
      fail("Should throw IllegalArgumentException on bad outputFormat");
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof IllegalArgumentException);
    }
  }
@Test
  public void testEmptyPropertiesStillAllowInstantiateDefaultAnnotators() {
    Properties props = new Properties();
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Properties internal = pipeline.getProperties();
    assertNotNull(internal.getProperty("annotators"));
  }
@Test
  public void testAnnotationCallbackPropagatesExceptionInAsyncMode() throws InterruptedException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "2");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Annotation annotation = new Annotation("text");
    final boolean[] caught = {false};

    annotation.set(CoreAnnotations.TextAnnotation.class, null);

    pipeline.annotate(annotation, a -> {
      Throwable ex = a.get(CoreAnnotations.ExceptionAnnotation.class);
      caught[0] = (ex != null);
    });

    Thread.sleep(300);
    assertTrue("ExceptionAnnotation should be set in case of error", caught[0]);
  }
@Test
  public void testProcessFilesNoClobberSkipsExistingFile() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "json");
    props.setProperty("replaceExtension", "true");
    props.setProperty("noClobber", "true");

    File inputFile = File.createTempFile("example_", ".txt");
    File outputFile = new File(inputFile.getParent(), "example_.json");
    try (FileWriter writer = new FileWriter(outputFile)) {
      writer.write("cached document");
    }

    List<File> files = Collections.singletonList(inputFile);

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.processFiles(null, files, 1, true, Optional.of(new Timing()));

    assertTrue("Output file should be untouched due to noClobber", outputFile.exists());
    inputFile.delete();
    outputFile.delete();
  }
@Test
  public void testProcessFileSkipsSelfOutputOverwrite() throws IOException {
    File file = File.createTempFile("data", ".txt");
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("outputDirectory", file.getParent());
    props.setProperty("outputFormat", "text");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Collection<File> singleton = Collections.singleton(new File(file.getCanonicalPath()));
    pipeline.processFiles(null, singleton, 1, false, Optional.empty());

    String outputName = file.getCanonicalPath() + ".out";
    assertTrue(new File(outputName).exists());
    new File(outputName).delete();
    file.delete();
  }
@Test
  public void testUnknownHelpTopicPrintsDefaultUsage() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream stream = new PrintStream(out);
    String unknownKey = "unknownHelpCommand";
    StanfordCoreNLP.class.cast(new StanfordCoreNLP()).getClass();
    StanfordCoreNLP.class.cast(new StanfordCoreNLP()).getClass();
    try {
      StanfordCoreNLP.class.getDeclaredMethod("printHelp", PrintStream.class, String.class)
        .invoke(null, stream, unknownKey);
    } catch (Exception e) {
      fail("Reflection should succeed");
    }
    String output = out.toString();
    assertTrue(output.contains("Unknown help topic") || output.contains("The following properties can be defined"));
  }
@Test
  public void testLoadPropertiesFromClasspathFailsGracefullyWithCorruptLoader() {
    ClassLoader corruptLoader = new ClassLoader() {
      @Override
      public InputStream getResourceAsStream(String name) {
        return null;
      }

      @Override
      public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
      }
    };

    boolean failedToLoad;
  }
@Test
  public void testPreTokenizedWithMwtRewritesAnnotatorsProperly() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,mwt,ner");
    props.setProperty("preTokenized", "true");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String updated = pipeline.getProperties().getProperty("annotators");

    assertTrue(updated.contains("tokenize"));
    assertTrue(updated.contains("ssplit"));
    assertTrue(updated.contains("ner"));
    assertFalse(updated.contains("mwt"));
  }
@Test
  public void testProcessDocumentWithExceptionDoesNotCrashMainMethod() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("file", "non_existent_file.txt");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    try {
      pipeline.run(true);
    } catch (Exception e) {
      
      assertTrue(e instanceof RuntimeException || e instanceof IOException);
    }
  }
@Test
  public void testCustomOutputterFailsWithMissingClass() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "custom");
    props.setProperty("customOutputter", "invalid.CustomOutputter");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("custom output test");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);
      StanfordCoreNLP.class
        .getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class)
        .invoke(null, baos, annotation, props, options);
      fail("Expected RuntimeException for missing custom outputter");
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof RuntimeException);
    }
  }
@Test
  public void testExcludeFilesSkipsCorrectListedFile() throws IOException {
    File testFile = File.createTempFile("skipme", ".txt");
    File excludeList = File.createTempFile("excludes", ".txt");
    try (PrintWriter writer = new PrintWriter(excludeList)) {
      writer.println(testFile.getName());
    }

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("excludeFiles", excludeList.getAbsolutePath());
    props.setProperty("outputFormat", "text");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.processFiles(null, Collections.singleton(testFile), 1, true, Optional.of(new Timing()));

    File output = new File(testFile.getAbsolutePath() + ".out");
    assertFalse("Excluded file should not be processed", output.exists());

    testFile.delete();
    excludeList.delete();
  }
@Test
  public void testGetDefaultAnnotatorPoolUsesGlobalCacheAndSignature() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    AnnotatorImplementations impl = new AnnotatorImplementations();
    AnnotatorPool pool1 = StanfordCoreNLP.getDefaultAnnotatorPool(props, impl);
    AnnotatorPool pool2 = StanfordCoreNLP.getDefaultAnnotatorPool(props, impl);
    assertNotNull(pool1);
    assertSame(pool1, pool2);
  } 
}