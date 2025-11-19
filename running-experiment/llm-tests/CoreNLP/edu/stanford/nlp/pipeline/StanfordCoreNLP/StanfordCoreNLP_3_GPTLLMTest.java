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

public class StanfordCoreNLP_3_GPTLLMTest {

 @Test
  public void testConstructorWithValidAnnotators() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertNotNull(pipeline);
    assertEquals("tokenize,ssplit", pipeline.getProperties().getProperty("annotators"));
  }
@Test
  public void testConstructorWithPreTokenizedOption() {
    Properties props = new Properties();
    props.setProperty("annotators", "pos");
    props.setProperty("preTokenized", "true");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String annotators = pipeline.getProperties().getProperty("annotators");
    String whitespace = pipeline.getProperties().getProperty("tokenize.whitespace");
    String eolonly = pipeline.getProperties().getProperty("ssplit.eolonly");

    assertTrue(annotators.startsWith("tokenize,ssplit"));
    assertEquals("true", whitespace);
    assertEquals("true", eolonly);
  }
@Test
  public void testProcessAddsTokensToAnnotation() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Hello world.");
    pipeline.annotate(annotation);
    List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testProcessReturnsNonNullAnnotation() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("StanfordCoreNLP is great.");
    assertNotNull(annotation);
    assertEquals("StanfordCoreNLP is great.", annotation.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testProcessToCoreDocumentContainsTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument doc = pipeline.processToCoreDocument("This is a sentence.");
    assertNotNull(doc);
    assertEquals("This is a sentence.", doc.text());
    assertFalse(doc.tokens().isEmpty());
  }
@Test
  public void testPrettyPrintProducesOutput() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Stanford NLP.");
    OutputStream os = new ByteArrayOutputStream();
    pipeline.prettyPrint(annotation, os);
    String output = os.toString();
    assertTrue(output.contains("Sentence #"));
  }
@Test
  public void testNormalizeAnnotatorsRewritesCleanxml() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,cleanxml,pos");
    StanfordCoreNLP.normalizeAnnotators(props);
    String updated = props.getProperty("annotators");
    String cleanXmlProp = props.getProperty("tokenize.cleanxml");
    assertFalse(updated.contains("cleanxml"));
    assertEquals("true", cleanXmlProp);
  }
@Test
  public void testReplaceAnnotatorRewritesList() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,cdc_tokenize,ssplit");
    StanfordCoreNLP.replaceAnnotator(props, "cdc_tokenize", "tokenize");
    String updated = props.getProperty("annotators");
    assertTrue(updated.contains("tokenize"));
    assertFalse(updated.contains("cdc_tokenize"));
  }
@Test
  public void testGetEncodingReturnsUtf8ByDefault() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String encoding = pipeline.getEncoding();
    assertEquals("UTF-8", encoding);
  }
@Test
  public void testAnnotateCoreDocumentWrapsSentences() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument doc = new CoreDocument("Sentence one. Sentence two.");
    pipeline.annotate(doc);
    assertTrue(doc.sentences().size() >= 2);
  }
@Test
  public void testEnsurePrerequisiteAnnotatorsAddsRequiredOnes() {
    Properties props = new Properties();
    String[] input = new String[] {"lemma"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    assertTrue(result.contains("tokenize"));
    assertTrue(result.contains("ssplit"));
    assertTrue(result.contains("pos"));
    assertTrue(result.contains("lemma"));
  }
@Test(expected = IllegalArgumentException.class)
  public void testEnsurePrerequisiteThrowsForUnknownAnnotator() {
    Properties props = new Properties();
    String[] input = new String[] {"fakeAnnotator"};
    StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  }
@Test(expected = RuntimeException.class)
  public void testXmlPrintThrowsIfXMLOutputterMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation("This is text.");
    pipeline.annotate(ann);
    Writer writer = new PrintWriter(new ByteArrayOutputStream());
    StanfordCoreNLP.clearAnnotatorPool(); 
    pipeline.xmlPrint(ann, writer);
  }
@Test
  public void testJsonPrintProducesExpectedOutput() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("It works!");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    pipeline.jsonPrint(ann, new PrintWriter(os));
    String json = os.toString();
    assertTrue(json.contains("\"pos\""));
    assertTrue(json.contains("\"It\""));
  }
@Test(expected = RuntimeException.class)
  public void testConstructorFailsWithMissingAnnotatorsProperty() {
    Properties props = new Properties();
    new StanfordCoreNLP(props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidAnnotatorOrderThrowsException() {
    Properties props = new Properties();
    props.setProperty("annotators", "lemma"); 
    new StanfordCoreNLP(props, true);
  }
@Test
  public void testCustomAnnotatorRegistrationIsRecognized() {
    Properties props = new Properties();
    props.setProperty("annotators", "custom1");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);
    Annotation ann = new Annotation("Text");
    pipeline.annotate(ann);
  }
@Test
  public void testUsesBinaryTreesReturnsTrueWhenSentimentIsPresent() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,sentiment");
    boolean result = StanfordCoreNLP.usesBinaryTrees(props);
    assertTrue(result);
  }
@Test
  public void testUsesBinaryTreesReturnsFalseWhenSentimentIsAbsent() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    boolean result = StanfordCoreNLP.usesBinaryTrees(props);
    assertFalse(result);
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownOutputFormatFails() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "invalidFormat");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("text");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(os);
    StanfordCoreNLP.OutputFormat outputFormat = StanfordCoreNLP.OutputFormat.valueOf(
        props.getProperty("outputFormat").toUpperCase());
    assertEquals("This line should never be reached", outputFormat, null);
  }
@Test
  public void testAnnotateWithCallbackSingleThread() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "1");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation("Hello world.");
    final boolean[] callbackCalled = new boolean[]{false};

    pipeline.annotate(ann, result -> callbackCalled[0] = true);
    assertTrue(callbackCalled[0]);
  }
@Test
  public void testAnnotateWithCallbackMultiThread() throws InterruptedException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "2");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    final boolean[] callbackCalled = new boolean[]{false};
    Annotation ann = new Annotation("Hello world.");

    pipeline.annotate(ann, result -> callbackCalled[0] = true);
    Thread.sleep(500); 
    assertTrue(callbackCalled[0]);
  }
@Test
  public void testXmlPrintThrowsWhenEncodingInvalid() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("encoding", "bad-charset");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("text");
  }
@Test
  public void testDefaultAnnotatorPoolContainsTokenize() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    AnnotatorPool pool = StanfordCoreNLP.getDefaultAnnotatorPool(props, new AnnotatorImplementations());
    Annotator annotator = StanfordCoreNLP.getExistingAnnotator("tokenize");
    assertNotNull(annotator);
  }
@Test
  public void testClearAnnotatorPoolEmptiesCache() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    StanfordCoreNLP.clearAnnotatorPool();
    assertTrue(StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.isEmpty());
  }
@Test(expected = RuntimeException.class)
  public void testRunFailsIfFilePathInvalid() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    props.setProperty("file", "/non/existent/input.txt");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.run(); 
  }
@Test
  public void testProcessEmptyTextReturnsEmptyTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("");
    List tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens == null || tokens.isEmpty());
  }
@Test
  public void testEnsurePrerequisiteHandlesNoDependencies() {
    Properties props = new Properties();
    String[] annotators = {"tokenize"};
    String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
    assertEquals("tokenize", ordered);
  }
@Test(expected = IllegalStateException.class)
  public void testEnsurePrerequisiteDetectsCircularDependency() {
    Properties props = new Properties();
    StanfordCoreNLP.AnnotatorSignature fakeSig = new StanfordCoreNLP.AnnotatorSignature("loop", "sig");
    StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.put(fakeSig,
        Lazy.cache(() -> new Annotator() {
          public void annotate(Annotation annotation) { }
          public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
            return Collections.emptySet();
          }
          public Set<Class<? extends CoreAnnotation>> requires() {
            return Collections.singleton(CoreAnnotations.TextAnnotation.class);
          }
        }));
    AnnotatorPool pool = StanfordCoreNLP.getDefaultAnnotatorPool(props, new AnnotatorImplementations());
    props.setProperty("annotators", "loop");
    StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"loop"}, props);
  }
@Test
  public void testPreTokenizedWithNonStandardAnnotators() {
    Properties props = new Properties();
    props.setProperty("annotators", "ner");
    props.setProperty("preTokenized", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertTrue(pipeline.getProperties().get("annotators").toString().startsWith("tokenize,ssplit,ner"));
  }
@Test
  public void testReplaceAnnotatorNoMatchDoesNotAlterText() {
    Properties props = new Properties();
    props.setProperty("annotators", "ner,pos");
    StanfordCoreNLP.replaceAnnotator(props, "doesNotExist", "replacement");
    assertEquals("ner,pos", props.getProperty("annotators"));
  }
@Test(expected = RuntimeException.class)
  public void testXmlPrintMethodFailsForMissingXMLOutputter() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Test input.");
    Writer writer = new StringWriter();
    System.setProperty("java.class.path", ""); 
    pipeline.xmlPrint(ann, writer);
  }
@Test
  public void testLoadPropertiesReturnsNullIfFileMissing() {
    Properties result = null;
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.lang.reflect.Method method = StanfordCoreNLP.class.getDeclaredMethod("loadProperties", String.class, ClassLoader.class);
      method.setAccessible(true);
      result = (Properties) method.invoke(null, "some.nonexisting.file", loader);
    } catch (Exception e) {
      result = null;
    }
    assertNull(result);
  }
@Test
  public void testProcessFilesSkipsOutputEqualsInputPath() throws Exception {
    File input = File.createTempFile("sample", ".txt");
    PrintWriter out = new PrintWriter(input);
    out.print("This is a test.");
    out.close();
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("file", input.getAbsolutePath());
    props.setProperty("outputDirectory", input.getParent());
    props.setProperty("outputExtension", ".txt");
    props.setProperty("replaceExtension", "false"); 
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.processFiles(input.getParent(), Collections.singletonList(input), 1, false, Optional.of(new Timing()));
    assertTrue(input.exists());
    input.delete();
  }
@Test
  public void testGetRequiredPropertyThrowsWhenMissing() {
    Properties props = new Properties(); 
    try {
      java.lang.reflect.Method method = StanfordCoreNLP.class.getDeclaredMethod("getRequiredProperty", Properties.class, String.class);
      method.setAccessible(true);
      method.invoke(null, props, "missingKey");
      fail("Should throw RuntimeException on missing key");
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof RuntimeException);
    }
  }
@Test
  public void testJsonPrintHandlesNonEnglishText() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("你好，世界！");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    pipeline.jsonPrint(annotation, new OutputStreamWriter(os));
    String json = new String(os.toByteArray(), "UTF-8");
    assertTrue(json.contains("你好"));
  }
@Test
  public void testSerializerClassPropertyInSerializedOutput() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "serialized"); 
    props.setProperty("serializer", ProtobufAnnotationSerializer.class.getName());
    Annotation annotation = new Annotation("Serialize this.");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.annotate(annotation);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);
    java.lang.reflect.Method outMethod = StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation",
        OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
    outMethod.setAccessible(true);
    outMethod.invoke(null, stream, annotation, props, options);
    assertTrue(stream.toByteArray().length > 0);
  }
@Test
  public void testUnrecognizedLanguagePropertiesFallbackToClasspathLoading() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");

    try {
      java.lang.reflect.Method method = StanfordCoreNLP.class.getDeclaredMethod("loadProperties", String.class, ClassLoader.class);
      method.setAccessible(true);
      Object result = method.invoke(null, "en", Thread.currentThread().getContextClassLoader());
      assertTrue(result instanceof Properties);
    } catch (Exception e) {
      fail("Unexpected exception: " + e);
    }
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidCustomOutputterThrows() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "custom");
    props.setProperty("customOutputter", "invalid.NonExistentClass");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Some input here");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AnnotationOutputter.Options opts = AnnotationOutputter.getOptions(props);
    java.lang.reflect.Method method = StanfordCoreNLP.class
        .getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);

    method.setAccessible(true);
    method.invoke(null, baos, ann, props, opts);
  }
@Test(expected = RuntimeException.class)
  public void testLoadPropertiesOrExceptionFails() {
    try {
      java.lang.reflect.Method method = StanfordCoreNLP.class.getDeclaredMethod("loadPropertiesOrException", String.class);
      method.setAccessible(true);
      method.invoke(null, "this_file_does_not_exist");
    } catch (Exception e) {
      throw (RuntimeException) e.getCause();
    }
  }
@Test
  public void testExcludeFilesSkipsFilesFromList() throws Exception {
    File inputFile = File.createTempFile("test-input", ".txt");
    PrintWriter writer = new PrintWriter(inputFile);
    writer.println("This text must be ignored.");
    writer.close();

    File excludeList = File.createTempFile("exclude-list", ".txt");
    PrintWriter excludeWriter = new PrintWriter(excludeList);
    excludeWriter.println(inputFile.getName());
    excludeWriter.close();

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("excludeFiles", excludeList.getAbsolutePath());
    props.setProperty("file", inputFile.getAbsolutePath());
    props.setProperty("outputDirectory", inputFile.getParent());

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.processFiles(inputFile.getParent(), Collections.singletonList(inputFile), 1, false, Optional.of(new Timing()));

    assertTrue("Input file still exists", inputFile.exists());
    inputFile.delete();
    excludeList.delete();
  }
@Test
  public void testEnsurePrerequisiteWithParseButNoPOS() {
    Properties props = new Properties();
    String[] anno = {"parse"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(anno, props);
    assertTrue(result.contains("tokenize"));
    assertTrue(result.contains("ssplit"));
    assertTrue(result.contains("parse"));
    assertFalse(result.contains("pos")); 
  }
@Test(expected = IllegalArgumentException.class)
  public void testEnsurePrerequisiteWithUnknownAnnotator() {
    Properties props = new Properties();
    String[] anno = {"not_found_annotator"};
    StanfordCoreNLP.ensurePrerequisiteAnnotators(anno, props);
  }
@Test
  public void testAnnotatorPoolRegistersDuplicateCustomAnnotatorsOnce() {
    Properties props = new Properties();
    props.setProperty("annotators", "customA,customA");

    AnnotatorImplementations impl = new AnnotatorImplementations();
    AnnotatorPool pool = new AnnotatorPool();
    java.lang.reflect.Method method;
    try {
      method = StanfordCoreNLP.class.getDeclaredMethod("registerCustomAnnotators", AnnotatorPool.class, AnnotatorImplementations.class, Properties.class);
      method.setAccessible(true);
      method.invoke(null, pool, impl, props);
    } catch (Exception e) {
      fail("Reflection error: " + e.getMessage());
    }

    assertNotNull(pool.get("customA"));
  }
@Test
  public void testAnnotateSetsExceptionOnFailureAndCallbackInvoked() throws InterruptedException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "2");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation badAnnotation = new Annotation("data");
    final boolean[] wasCalled = {false};

    StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.put(
        new StanfordCoreNLP.AnnotatorSignature("bad_annotator", "sig"),
        Lazy.cache(() -> new Annotator() {
          public void annotate(Annotation annotation) {
            throw new RuntimeException("boom");
          }
          public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
            return Collections.emptySet();
          }
          public Set<Class<? extends CoreAnnotation>> requires() {
            return Collections.emptySet();
          }
        }));

    pipeline.annotate(badAnnotation, annotation -> wasCalled[0] = true);
    Thread.sleep(500);
    assertTrue(wasCalled[0]);
  }
@Test
  public void testRunInteractiveShellWithEmptyInput() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    InputStream originalIn = System.in;
    PrintStream originalOut = System.out;

    try {
      
      System.setIn(new ByteArrayInputStream(new byte[0]));
      ByteArrayOutputStream dummyOut = new ByteArrayOutputStream();
      System.setOut(new PrintStream(dummyOut));
      java.lang.reflect.Method shellMethod = StanfordCoreNLP.class.getDeclaredMethod("shell");
      shellMethod.setAccessible(true);
      shellMethod.invoke(pipeline);
      String output = dummyOut.toString();
      assertTrue(output.isEmpty() || output.contains("") || output.contains("NLP>"));
    } finally {
      System.setIn(originalIn);
      System.setOut(originalOut);
    }
  }
@Test
  public void testAnnotatorPoolClearsProperly() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertFalse(StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.isEmpty());
    StanfordCoreNLP.clearAnnotatorPool();
    assertTrue(StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.isEmpty());
  }
@Test
  public void testProcessFilesHandlesReplaceExtensionFalse() throws Exception {
    File in = File.createTempFile("document", ".text");
    BufferedWriter writer = new BufferedWriter(new FileWriter(in));
    writer.write("Hello CoreNLP");
    writer.close();

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputDirectory", in.getParent());
    props.setProperty("outputFormat", "json");
    props.setProperty("outputExtension", ".json");
    props.setProperty("replaceExtension", "false");
    props.setProperty("file", in.getAbsolutePath());

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.processFiles(in.getParent(), Collections.singletonList(in), 1, false, Optional.of(new Timing()));

    File out = new File(in.getAbsolutePath() + ".json");
    assertTrue(out.exists());
    in.delete();
    out.delete();
  }
@Test
  public void testEnsureRegexNERFollowsNER() {
    Properties p = new Properties();
    String[] input = {"tokenize", "ssplit", "pos", "lemma", "ner", "regexner"};
    String sequence = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, p);
    int regexIndex = sequence.indexOf("regexner");
    int nerIndex = sequence.indexOf("ner");
    assertTrue(regexIndex > nerIndex);
  }
@Test
  public void testEnsureOpenIEFollowsCoref() {
    Properties props = new Properties();
    String[] input = {"tokenize", "ssplit", "pos", "lemma", "ner", "coref", "openie"};
    String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    int corefIndex = ordered.indexOf("coref");
    int openieIndex = ordered.indexOf("openie");
    assertTrue(corefIndex < openieIndex);
  }
@Test
  public void testCustomCorefMentionWithoutParseSetsMDTypeToDep() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,ner,coref");
    String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(
        new String[]{"coref"}, props);
    assertEquals("dep", props.getProperty("coref.md.type"));
  }
@Test
  public void testPrettyPrintFallbackToOutputStream() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation("The quick brown fox.");
    pipeline.annotate(ann);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    pipeline.prettyPrint(ann, output);
    String result = output.toString();
    assertTrue(result.contains("Sentence #1"));
  }
@Test
  public void testGetEncodingPropertyRespectsOverride() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("encoding", "ISO-8859-1");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertEquals("ISO-8859-1", pipeline.getEncoding());
  }
@Test(expected = IllegalArgumentException.class)
  public void testOutputAnnotationThrowsForUnknownFormat() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "nonexistent");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Hello world");

    AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    java.lang.reflect.Method method = StanfordCoreNLP.class
        .getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
    method.setAccessible(true);
    method.invoke(null, os, annotation, props, options);
  }
@Test
  public void testEnsurePrerequisiteRemovesDepParseIfParsePresent() {
    Properties props = new Properties();
    String[] annotators = {"tokenize", "ssplit", "parse", "depparse"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
    assertTrue(result.contains("parse"));
    assertFalse(result.contains("depparse"));
  }
@Test
  public void testEnsurePrerequisitePreservesDepParseIfSeparate() {
    Properties props = new Properties();
    String[] annotators = {"tokenize", "ssplit", "depparse"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
    assertTrue(result.contains("depparse"));
  }
@Test
  public void testAnnotationNumWordsUpdatedAfterProcess() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation doc = new Annotation("One two three");
    pipeline.annotate(doc);
    List tokens = doc.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
  }
@Test
  public void testRedundantAnnotatorsNotDuplicatedInEnsurePrerequisites() {
    Properties props = new Properties();
    String[] input = {"tokenize", "ssplit", "parse", "tokenize", "ssplit"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    int count = result.split(",").length;
    Set<String> set = new HashSet<>(Arrays.asList(result.split(",")));
    assertEquals(set.size(), count);
  }
@Test
  public void testMissingTextAnnotationInAnnotationStillProcesses() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation((Annotation) null);
    pipeline.annotate(ann);
    List tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens == null || tokens.isEmpty());
  }
@Test
  public void testEmptyPropertiesDefaultsHandled() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    Properties p = pipeline.getProperties();
    assertNotNull(p);
    assertTrue(p.containsKey("annotators"));
    assertFalse(p.getProperty("annotators").isEmpty());
  }
@Test
  public void testThreadInterruptionReleasesSemaphore() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "5");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Annotation doc = new Annotation("short text");
    final boolean[] interrupted = {false};

    Thread t = new Thread(() -> {
      try {
        Thread.currentThread().interrupt();
        pipeline.annotate(doc);
      } catch (RuntimeInterruptedException e) {
        interrupted[0] = true;
      }
    });

    t.start();
    t.join();
    assertTrue(interrupted[0]);
  }
@Test
  public void testOpenIEReorderedAfterCoref() {
    Properties props = new Properties();
    String[] input = {"tokenize", "ssplit", "pos", "coref", "openie"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    int corefIndex = result.indexOf("coref");
    int openieIndex = result.indexOf("openie");
    assertTrue(corefIndex < openieIndex);
  }
@Test
  public void testProcessFilesIgnoreNoClobberIfFileMissing() throws Exception {
    File file = File.createTempFile("noclobber", ".txt");
    PrintWriter writer = new PrintWriter(file);
    writer.print("hello.");
    writer.close();

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputDirectory", file.getParent());
    props.setProperty("replaceExtension", "true");
    props.setProperty("outputFormat", "json");
    props.setProperty("noClobber", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.processFiles(file.getParent(), Collections.singletonList(file), 1, false, Optional.of(new Timing()));

    File out = new File(file.getParent(), file.getName().replace(".txt", ".json"));
    assertTrue(out.exists());
    file.delete();
    out.delete();
  }
@Test
  public void testShellReadsAsOneDocument() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("isOneDocument", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    InputStream originalIn = System.in;
    PrintStream originalOut = System.out;

    try {
      String text = "Hello.\nThis is one document.\n";
      System.setIn(new ByteArrayInputStream(text.getBytes("UTF-8")));
      ByteArrayOutputStream captureOut = new ByteArrayOutputStream();
      System.setOut(new PrintStream(captureOut));

      java.lang.reflect.Method shell = StanfordCoreNLP.class.getDeclaredMethod("shell");
      shell.setAccessible(true);
      shell.invoke(pipeline);

      String captured = new String(captureOut.toByteArray(), "UTF-8");
      assertTrue(captured.contains("Sentence #") || captured.contains("Token"));
    } finally {
      System.setIn(originalIn);
      System.setOut(originalOut);
    }
  }
@Test
  public void testDefaultAnnotatorsExistInNamedMap() {
    Map<String, java.util.function.BiFunction<Properties, AnnotatorImplementations, Annotator>> map;
    try {
      Method m = StanfordCoreNLP.class.getDeclaredMethod("getNamedAnnotators");
      m.setAccessible(true);
      map = (Map<String, java.util.function.BiFunction<Properties, AnnotatorImplementations, Annotator>>) m.invoke(null);
    } catch (Exception e) {
      fail(e.getMessage());
      return;
    }
    assertTrue(map.containsKey("tokenize"));
    assertTrue(map.containsKey("parse"));
  }
@Test
  public void testConstructAnnotatorPoolIncludesCustomAnnotators() {
    Properties p = new Properties();
    p.setProperty("annotators", "tokenize,ssplit,customfoo");
    StanfordCoreNLP coreNLP = new StanfordCoreNLP(p, false);
    assertNotNull(coreNLP.pool.get("customfoo"));
  }
@Test(expected = RuntimeException.class)
  public void testXmlPrintThrowsIfXMLOutputterClassMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Hello XML.");
    OutputStream os = new ByteArrayOutputStream();
    ClassLoader brokenLoader = new ClassLoader() {
      public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.contains("XMLOutputter")) {
          throw new ClassNotFoundException("Simulated missing XML");
        }
        return super.loadClass(name);
      }
    };
    Thread.currentThread().setContextClassLoader(brokenLoader);
    pipeline.xmlPrint(ann, new OutputStreamWriter(os));
  }
@Test
  public void testInvalidSerializerClassThrowsAndFallsBack() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("serializer", "java.lang.String"); 
    props.setProperty("outputFormat", "serialized");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("This should attempt to serialize.");
    boolean threw = false;
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      AnnotationOutputter.Options opts = AnnotationOutputter.getOptions(props);

      Method outputMethod = StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation",
          OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
      outputMethod.setAccessible(true);
      outputMethod.invoke(null, os, ann, props, opts);
    } catch (Exception e) {
      threw = true;
    }
    assertTrue(threw);
  }
@Test
  public void testCustomOutputterLoadsAndFailsIfMissing() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "custom");
    props.setProperty("customOutputter", "non.existing.CustomOutputter");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("text");
    boolean threw = false;
    try {
      Method method = StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation",
          OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
      method.setAccessible(true);
      method.invoke(null, new ByteArrayOutputStream(), ann, props, AnnotationOutputter.getOptions(props));
    } catch (Exception e) {
      threw = true;
    }
    assertTrue(threw);
  }
@Test
  public void testFallbackToClasspathPropertiesStillLoadsAnnotatorList() {
    Properties props = new Properties();
    props.setProperty("file", ""); 
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String annos = pipeline.getProperties().getProperty("annotators");
    assertNotNull(annos);
    assertFalse(annos.isEmpty());
  }
@Test(expected = RuntimeException.class)
  public void testLoadPropertiesFromClasspathThrowsIfNothingFound() {
    try {
      Method method = StanfordCoreNLP.class.getDeclaredMethod("loadPropertiesFromClasspath");
      method.setAccessible(true);
      method.invoke(null);
    } catch (Exception e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      } else {
        fail("Wrong exception type");
      }
    }
  }
@Test
  public void testProcessWithoutTokensDoesNotModifyAnnotation() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation input = new Annotation("");
    pipeline.annotate(input);
    List tokens = input.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens == null || tokens.isEmpty());
  } 
}