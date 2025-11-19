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
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class StanfordCoreNLP_2_GPTLLMTest {

 @Test
  public void testPipelineConstructionWithProperties() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    assertNotNull(pipeline);
    assertNotNull(pipeline.getProperties());
    assertEquals("tokenize,ssplit,pos,lemma", pipeline.getProperties().getProperty("annotators"));

    StanfordCoreNLP.clearAnnotatorPool();
  }
@Test
  public void testAnnotateSimpleTextReturnsTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Stanford University is great.");
    pipeline.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
    assertFalse(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());

    StanfordCoreNLP.clearAnnotatorPool();
  }
@Test
  public void testPreTokenizedAdjustsAnnotatorsProperly() {
    Properties props = new Properties();
    props.setProperty("annotators", "pos,lemma");
    props.setProperty("preTokenized", "true");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    String adjusted = pipeline.getProperties().getProperty("annotators");
    assertTrue(adjusted.startsWith("tokenize,ssplit,"));
    assertEquals("true", pipeline.getProperties().getProperty("tokenize.whitespace"));

    StanfordCoreNLP.clearAnnotatorPool();
  }
@Test
  public void testEnsurePrerequisiteAddsPosForLemma() {
    String[] annotators = new String[]{"lemma"};
    Properties props = new Properties();
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);

    assertTrue(result.contains("pos"));
    assertTrue(result.contains("tokenize"));
    assertTrue(result.contains("ssplit"));
  }

@Test
  public void testReplaceCDCAnnotator() {
    Properties props = new Properties();
    props.setProperty("annotators", "cdc_tokenize,pos,lemma");

    StanfordCoreNLP.replaceAnnotator(props, "cdc_tokenize", "tokenize");

    assertEquals("tokenize,pos,lemma", props.getProperty("annotators"));
  }
@Test
  public void testUnifyTokenizeAndCleanXmlProperty() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,cleanxml");

    StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");

    assertEquals("tokenize", props.getProperty("annotators"));
    assertEquals("true", props.getProperty("tokenize.cleanxml"));
  }
@Test
  public void testUsesBinaryTreesTrueWhenSentimentPresent() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,sentiment");

    boolean result = StanfordCoreNLP.usesBinaryTrees(props);

    assertTrue(result);
  }
@Test
  public void testUsesBinaryTreesFalseWhenSentimentAbsent() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");

    boolean result = StanfordCoreNLP.usesBinaryTrees(props);

    assertFalse(result);
  }
@Test
  public void testAnnotatorSignatureEqualityAndHashcode() {
    StanfordCoreNLP.AnnotatorSignature sig1 = new StanfordCoreNLP.AnnotatorSignature("pos", "123");
    StanfordCoreNLP.AnnotatorSignature sig2 = new StanfordCoreNLP.AnnotatorSignature("pos", "123");

    assertEquals(sig1, sig2);
    assertEquals(sig1.hashCode(), sig2.hashCode());
  }
@Test
  public void testJsonPrintIncludesInputText() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Stanford is great.");
    pipeline.annotate(annotation);

    StringWriter writer = new StringWriter();
    pipeline.jsonPrint(annotation, writer);
    String output = writer.toString();

    assertTrue(output.contains("Stanford"));
    assertTrue(output.contains("great"));

    StanfordCoreNLP.clearAnnotatorPool();
  }
@Test
  public void testInvalidAnnotatorThrowsIllegalArgumentException() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,foobar");

    try {
      new StanfordCoreNLP(props);
      fail("Should throw IllegalArgumentException for unknown annotator");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("unknown annotator"));
    }

    StanfordCoreNLP.clearAnnotatorPool();
  }
@Test
  public void testCircularDependencyThrowsException() {
    Properties props = new Properties();
    String[] input = new String[]{"x"};

    try {
      StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
      fail("Circular dependency must raise IllegalStateException");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("circular"));
    }

    Annotator.DEFAULT_REQUIREMENTS.remove("x");
    Annotator.DEFAULT_REQUIREMENTS.remove("y");
  }
@Test
public void testEmptyTextAnnotationProcessesWithoutException() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("");
  pipeline.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testNullTextProcessesWithoutThrowing() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation((String) null);
  pipeline.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testDefaultConstructorLoadsClasspathProperties() {
  StanfordCoreNLP pipeline = new StanfordCoreNLP();
  assertNotNull(pipeline.getProperties());
  assertTrue(pipeline.getProperties().containsKey("annotators"));

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test(expected = IllegalArgumentException.class)
public void testConstructorEnforceRequirementsInvalidDependency() {
  Properties props = new Properties();
  props.setProperty("annotators", "lemma");

  new StanfordCoreNLP(props, true);
}
@Test
public void testConstructorEnforceRequirementsFalseSkipsValidation() {
  Properties props = new Properties();
  props.setProperty("annotators", "lemma");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);
  assertNotNull(pipeline);

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testAnnotationFailureIsCapturedInAsyncThread() throws InterruptedException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("threads", "2");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

  Annotation badAnnotation = new Annotation("fail text");
  badAnnotation.set(CoreAnnotations.TokensAnnotation.class, null);

  final List<Throwable> caught = new ArrayList<>();
  pipeline.annotate(badAnnotation, (Annotation result) -> {
    Throwable t = result.get(CoreAnnotations.ExceptionAnnotation.class);
    if (t != null) {
      caught.add(t);
    }
  });

  Thread.sleep(300); 

  assertFalse("Exception should be attached to annotation", caught.isEmpty());

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testMissingXMLOutputterClassTriggersRuntimeException() {
  try {
    Class<?> old = Class.forName("edu.stanford.nlp.pipeline.XMLOutputter");
    System.err.println("XMLOutputter class exists. Skipping test.");
    return;
  } catch (Exception ignored) {}

  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Sample!");

  try {
    pipeline.xmlPrint(annotation, new PrintWriter(new StringWriter()));
    fail("Expected RuntimeException when XMLOutputter class is missing");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("edu.stanford.nlp.pipeline.XMLOutputter"));
  } catch (IOException e) {
      throw new RuntimeException(e);
  }

    StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testRedundantAnnotatorsOrderingDoesNotDuplicate() {
  Properties props = new Properties();
  props.setProperty("annotators", "pos,pos,lemma");

  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"pos", "pos", "lemma"}, props);

  assertEquals(1, result.chars().filter(ch -> ch == ',').count() + 1);
}
@Test
public void testClearAnnotatorPoolEmptiesCache() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  assertFalse(StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.isEmpty());

  StanfordCoreNLP.clearAnnotatorPool();
  assertTrue(StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.isEmpty());
}
@Test
public void testSerializedOutputFormatWithoutSerializerIsSkippedWithoutCrash() throws IOException {
  ByteArrayOutputStream output = new ByteArrayOutputStream();
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "serialized");
  props.setProperty("serializer", "edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Stanford is great!");
  pipeline.annotate(annotation);

  AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);
  StanfordCoreNLP.OutputFormat outputFormat = StanfordCoreNLP.OutputFormat.SERIALIZED;

  StanfordCoreNLP.createOutputter(props, options).accept(annotation, output);

  assertTrue(output.toByteArray().length > 0);

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testEmptyAnnotatorsThrowsException() {
  Properties props = new Properties();
  

  try {
    new StanfordCoreNLP(props);
    fail("Expected RuntimeException due to missing 'annotators' property");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Missing property"));
  }
}
@Test
public void testInvalidCustomAnnotatorClassCausesException() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,invalid");
  props.setProperty("customAnnotatorClass.invalid", "non.existent.ClassName");

  try {
    new StanfordCoreNLP(props);
    fail("Expected RuntimeException due to invalid custom annotator class name");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("non.existent.ClassName"));
  }
}
@Test
public void testEncodingFallbackDefaultsToUTF8() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String encoding = pipeline.getEncoding();

  assertEquals("UTF-8", encoding);

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testPipelineWithWhitespaceOnlyText() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("   ");
  pipeline.annotate(annotation);

  List<?> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(tokens);
  assertEquals(0, tokens.size());

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testEnsurePrerequisiteRemovesDepIfParsePresent() {
  Properties props = new Properties();
  String[] annotators = new String[] { "depparse", "parse" };

  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);

  assertTrue(result.contains("parse"));
  assertFalse(result.contains("depparse"));
}
@Test
public void testEnsureCorefBeforeOpenIE() {
  Properties props = new Properties();
  String[] annotators = new String[] { "openie", "coref" };

  String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
  int corefIndex = ordered.indexOf("coref");
  int openieIndex = ordered.indexOf("openie");

  assertTrue(corefIndex < openieIndex);
}
@Test
public void testEnsureRegexNERAfterNER() {
  Properties props = new Properties();
  String[] annotators = new String[] { "regexner", "ner" };

  String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);

  int nerIndex = ordered.indexOf("ner");
  int regexnerIndex = ordered.indexOf("regexner");

  assertTrue(nerIndex < regexnerIndex);
}
@Test
public void testOutputFormatCustomThrowsIfClassMissing() {
  ByteArrayOutputStream output = new ByteArrayOutputStream();

  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "custom");
  props.setProperty("customOutputter", "fake.MissingOutputter");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("text");
  pipeline.annotate(annotation);

  StanfordCoreNLP.OutputFormat fmt = StanfordCoreNLP.OutputFormat.CUSTOM;
  AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);

  try {
    java.lang.reflect.Method outputMethod = StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
    outputMethod.setAccessible(true);
    outputMethod.invoke(null, output, annotation, props, options);
    fail("Expected exception due to missing customOutputter class");
  } catch (Exception e) {
    assertTrue(e.getCause().getMessage().contains("fake.MissingOutputter"));
  }

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testOutputFormatTextWritesExpectedOutput() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
  ByteArrayOutputStream output = new ByteArrayOutputStream();

  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "text");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Stanford is cool.");
  pipeline.annotate(annotation);

  StanfordCoreNLP.OutputFormat fmt = StanfordCoreNLP.OutputFormat.TEXT;
  AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);

  java.lang.reflect.Method m = StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
  m.setAccessible(true);
  m.invoke(null, output, annotation, props, options);

  String result = output.toString();
  assertTrue(result.contains("Stanford"));

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testInvalidOutputFormatThrowsOnProcessFiles() throws Exception {
  File dummyInput = File.createTempFile("text", ".txt");
  try (FileWriter writer = new FileWriter(dummyInput)) {
    writer.write("Stanford NLP");
  }

  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "INVALID_FORMAT");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

  Collection<File> inputs = new ArrayList<>();
  inputs.add(dummyInput);

  try {
    pipeline.processFiles(null, inputs, 1, false, Optional.empty());
    fail("Expected IllegalArgumentException due to invalid output format");
  } catch (IllegalArgumentException e) {
    assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("unknown output format"));
  } finally {
    dummyInput.delete();
  }

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testProcessToCoreDocumentReturnsWrappedAnnotation() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  CoreDocument document = pipeline.processToCoreDocument("Hello world.");

  assertNotNull(document);
  assertNotNull(document.annotation());

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testConstructorLoadsDefaultAnnotatorPool() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props, true, null);
  assertNotNull(pipeline.pool);

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testUnifyTokenizePropertyWithNoCommaAtEndRemovesTrailing() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,cleanxml");

  StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
  String updated = props.getProperty("annotators");

  assertFalse(updated.contains("cleanxml"));
  assertTrue(props.getProperty("tokenize.cleanxml").equals("true"));
}
@Test
public void testGetExistingAnnotatorReturnsNullForUnknown() {
  Annotator annotator = StanfordCoreNLP.getExistingAnnotator("non_existing_name");
  assertNull(annotator);
}
@Test
public void testFileListKeyBackfillsFilelistAlias() {
  Properties props = new Properties();
  props.setProperty("fileList", "some_input.txt");
  props.setProperty("annotators", "tokenize");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String result = pipeline.getProperties().getProperty("filelist");

  assertEquals("some_input.txt", result);
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testNoChangeIfAnnotatorListAlreadyBeginsWithTokenizeSsplit() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,pos");
  props.setProperty("preTokenized", "true");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String annotators = pipeline.getProperties().getProperty("annotators");

  assertEquals("tokenize,ssplit,pos", annotators);
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testCleanXmlIsAbsorbedWithUnifyPropertyCall() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,cleanxml");

  StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
  assertEquals("true", props.getProperty("tokenize.cleanxml"));
  assertEquals("tokenize", props.getProperty("annotators"));
}
@Test
public void testUsesBinaryTreesIsFalseByDefaultWhenNoSentiment() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  boolean result = StanfordCoreNLP.usesBinaryTrees(props);
  assertFalse(result);
}
@Test
public void testUsesBinaryTreesReturnsTrueWithSentimentAnnotator() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,sentiment");

  boolean result = StanfordCoreNLP.usesBinaryTrees(props);
  assertTrue(result);
}
@Test
public void testCreateOutputterReturnsValidBiConsumer() throws IOException {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "text");

  Annotation annotation = new Annotation("Some text.");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  pipeline.annotate(annotation);

  ByteArrayOutputStream output = new ByteArrayOutputStream();
  StanfordCoreNLP.createOutputter(props, AnnotationOutputter.getOptions(props)).accept(annotation, output);

  String result = output.toString("UTF-8");
  assertTrue(result.contains("Some"));

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testOutputFormatEnumHasAllExpectedValues() {
  Set<String> expected = new HashSet<String>();
  expected.add("TEXT");
  expected.add("TAGGED");
  expected.add("XML");
  expected.add("JSON");
  expected.add("CONLL");
  expected.add("CONLLU");
  expected.add("INLINEXML");
  expected.add("SERIALIZED");
  expected.add("CUSTOM");

  boolean allPresent = true;

  for (StanfordCoreNLP.OutputFormat format : StanfordCoreNLP.OutputFormat.values()) {
    if (!expected.contains(format.name())) {
      allPresent = false;
    }
  }

  assertTrue(allPresent);
}
@Test
public void testDefaultOutputFormatIsText() {
  assertEquals("text", StanfordCoreNLP.DEFAULT_OUTPUT_FORMAT);
}
@Test
public void testAnnotatorSignatureToStringFormat() {
  StanfordCoreNLP.AnnotatorSignature sig = new StanfordCoreNLP.AnnotatorSignature("ner", "hash123");
  String str = sig.toString();
  assertTrue(str.contains("name='ner'"));
  assertTrue(str.contains("signature='hash123'"));
}
@Test
public void testEqualityReflexivityOfAnnotatorSignature() {
  StanfordCoreNLP.AnnotatorSignature sig = new StanfordCoreNLP.AnnotatorSignature("pos", "abc");
  assertTrue(sig.equals(sig));
}
@Test
public void testEqualityReturnsFalseForDifferentClassType() {
  StanfordCoreNLP.AnnotatorSignature sig = new StanfordCoreNLP.AnnotatorSignature("pos", "abc");
  assertFalse(sig.equals("string"));
}
@Test
public void testEnsurePrerequisiteAddsTokenizeIfMissingButRequired() {
  Properties props = new Properties();
  String[] annotators = new String[] { "ner" };

  String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
  assertTrue(result.contains("tokenize"));
}
@Test
public void testEnsurePrerequisiteAddsMwtRequirementIfUsed() {
  Properties props = new Properties();
  String[] annotators = new String[] { "mwt" };

  String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
  assertTrue(ordered.contains("tokenize"));
  assertTrue(ordered.contains("ssplit"));
}
@Test
public void testProcessReturnsAnnotationWithNonNullFields() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,pos");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

  Annotation anno = pipeline.process("NLP is awesome.");
  List<?> tokens = anno.get(CoreAnnotations.TokensAnnotation.class);

  assertNotNull(tokens);
  assertFalse(tokens.isEmpty());
}
@Test
public void testThreadedAnnotateRunsSafelyOnSingleThread() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("threads", "1");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

  Annotation annotation = new Annotation("Thread test.");
  final boolean[] called = new boolean[1];

  pipeline.annotate(annotation, ann -> called[0] = true);

  assertTrue(called[0]);
}
@Test
public void testExceptionAnnotationIsOnlySetOnFailure() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Valid input.");

  pipeline.annotate(annotation);
  Throwable exception = annotation.get(CoreAnnotations.ExceptionAnnotation.class);

  assertNull(exception);
}
@Test
public void testXmlPrintThrowsWhenXMLOutputterClassMissing() throws Exception {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = pipeline.process("Hello.");

  ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
  ClassLoader emptyClassLoader = new ClassLoader(originalClassLoader) {
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
      if (name.equals("edu.stanford.nlp.pipeline.XMLOutputter")) {
        throw new ClassNotFoundException(name);
      }
      return super.loadClass(name);
    }
  };

  Thread.currentThread().setContextClassLoader(emptyClassLoader);

  try {
    pipeline.xmlPrint(annotation, new ByteArrayOutputStream());
    fail("Expected RuntimeException due to missing XMLOutputter");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("edu.stanford.nlp.pipeline.XMLOutputter"));
  } finally {
    Thread.currentThread().setContextClassLoader(originalClassLoader);
  }
}
@Test
public void testJsonPrintOnAnnotationWithTokens() throws Exception {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Stanford CoreNLP.");
  pipeline.annotate(annotation);

  StringWriter writer = new StringWriter();
  pipeline.jsonPrint(annotation, writer);
  String output = writer.toString();

  assertNotNull(output);
  assertTrue(output.contains("Stanford"));
  assertTrue(output.contains("CoreNLP"));
}
@Test
public void testConllOutputSkipsWithShortInput() throws Exception {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("X.");
  pipeline.annotate(annotation);

  StringWriter writer = new StringWriter();
  pipeline.conllPrint(annotation, writer);

  String out = writer.toString();
  assertTrue(out.contains("X"));
}
@Test
public void testConstructorWithNullPropertiesFallsBackToClasspathDefaults() {
  StanfordCoreNLP pipeline = new StanfordCoreNLP((Properties) null);
  Properties loadedProps = pipeline.getProperties();
  assertNotNull(loadedProps.getProperty("annotators"));
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testConstructorEnforcesTasksWithMissingRequiredDependency() {
  Properties props = new Properties();
  props.setProperty("annotators", "parse"); 

  try {
    new StanfordCoreNLP(props, true);
    fail("Expected IllegalArgumentException due to unsatisfied dependency");
  } catch (IllegalArgumentException e) {
    assertTrue(e.getMessage().contains("requires annotation"));
  } finally {
    StanfordCoreNLP.clearAnnotatorPool();
  }
}

@Test
public void testGlobalCacheStoresDifferentSignatureSeparately() {
  Properties props1 = new Properties();
  props1.setProperty("annotators", "tokenize");
  AnnotatorPool pool1 = StanfordCoreNLP.getDefaultAnnotatorPool(props1, new AnnotatorImplementations());

  Properties props2 = new Properties();
  props2.setProperty("annotators", "tokenize");
  props2.setProperty("tokenize.whitespace", "true");
  AnnotatorPool pool2 = StanfordCoreNLP.getDefaultAnnotatorPool(props2, new AnnotatorImplementations());

  assertNotSame(pool1, pool2); 
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testRegisterCustomAnnotatorWithOverlappingNameDoesNotCrash() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,mytokenize");
  props.setProperty("customAnnotatorClass.mytokenize", "edu.stanford.nlp.pipeline.TokenizerAnnotator");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotator a = StanfordCoreNLP.getExistingAnnotator("mytokenize");

  assertNotNull(a);
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testReplaceAnnotatorNotFoundResultsInNoChange() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,pos,lemma");

  StanfordCoreNLP.replaceAnnotator(props, "invalid_annotator", "ner");

  assertEquals("tokenize,pos,lemma", props.getProperty("annotators")); 
}
@Test
public void testCreateOutputterWithTaggedFormatProducesTaggedOutput() throws Exception {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,pos");
  props.setProperty("outputFormat", "tagged");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("The CPU is fast.");
  pipeline.annotate(annotation);

  ByteArrayOutputStream os = new ByteArrayOutputStream();
  StanfordCoreNLP.OutputFormat format = StanfordCoreNLP.OutputFormat.valueOf(props.getProperty("outputFormat", "text").toUpperCase());
  AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);
  java.lang.reflect.Method outputMethod = StanfordCoreNLP.class.getDeclaredMethod(
    "outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
  outputMethod.setAccessible(true);
  outputMethod.invoke(null, os, annotation, props, options);

  String result = os.toString("UTF-8");
  assertTrue(result.contains("_"));
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testEmptyAnnotatorsPropertyTriggersClasspathFallbackLogic() {
  Properties props = new Properties();
  

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String annotators = pipeline.getProperties().getProperty("annotators");

  assertNotNull(annotators);
  assertTrue(annotators.contains("tokenize"));
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testWhitespaceAnnotatorsListHandledGracefully() {
  Properties props = new Properties();
  props.setProperty("annotators", "  tokenize ,   ssplit ");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  String normalized = pipeline.getProperties().getProperty("annotators");

  assertTrue(normalized.contains("tokenize"));
  assertTrue(normalized.contains("ssplit"));
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test(expected = RuntimeException.class)
public void testCustomAnnotatorClassThrowsIfNotAssignableToAnnotator() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,fake");
  props.setProperty("customAnnotatorClass.fake", "java.lang.String");

  new StanfordCoreNLP(props);
}
@Test
public void testMatchingAnnotatorSignatureAgainstNullObjectReturnsFalse() {
  StanfordCoreNLP.AnnotatorSignature sig = new StanfordCoreNLP.AnnotatorSignature("x", "123");
  assertFalse(sig.equals(null));
}
@Test
public void testMatchingAnnotatorSignatureWithDifferentNamesReturnsFalse() {
  StanfordCoreNLP.AnnotatorSignature sig1 = new StanfordCoreNLP.AnnotatorSignature("ner", "a");
  StanfordCoreNLP.AnnotatorSignature sig2 = new StanfordCoreNLP.AnnotatorSignature("pos", "a");
  assertFalse(sig1.equals(sig2));
}
@Test
public void testMatchingAnnotatorSignatureWithDifferentSignaturesReturnsFalse() {
  StanfordCoreNLP.AnnotatorSignature sig1 = new StanfordCoreNLP.AnnotatorSignature("pos", "aaa");
  StanfordCoreNLP.AnnotatorSignature sig2 = new StanfordCoreNLP.AnnotatorSignature("pos", "bbb");
  assertFalse(sig1.equals(sig2));
}
@Test
public void testUnifiedTokenizePropertyHandlesCommaDelimitedSuffix() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,cleanxml,pos");

  StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");

  assertEquals("true", props.getProperty("tokenize.cleanxml"));
  assertEquals("tokenize,pos", props.getProperty("annotators"));
}
@Test
public void testIncorrectThreadValueFallsBackToOneThread() {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize");
  props.setProperty("threads", "four"); 

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("test");
  pipeline.annotate(annotation);

  assertNotNull(annotation);
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testEmptyAnnotatorsResultsInRuntimeException() {
  Properties props = new Properties();
  props.setProperty("annotators", "");

  try {
    new StanfordCoreNLP(props);
    fail("Expected RuntimeException due to empty annotator spec");
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Missing property"));
  }
}
@Test
public void testJsonPrintHandlesEmptyAnnotationGracefully() throws Exception {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("");

  pipeline.annotate(annotation);
  StringWriter writer = new StringWriter();

  pipeline.jsonPrint(annotation, writer);
  String output = writer.toString();

  assertTrue(output.contains("sentences"));
  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testSerializedOutputFormatThrowsIfSerializerInvalid() throws Exception {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");
  props.setProperty("outputFormat", "serialized");
  props.setProperty("serializer", "com.fake.Serializer");

  Annotation annotation = new Annotation("Hello serialized world.");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  pipeline.annotate(annotation);

  AnnotationOutputter.Options options = AnnotationOutputter.getOptions(props);
  ByteArrayOutputStream output = new ByteArrayOutputStream();

  try {
    java.lang.reflect.Method outputMethod = StanfordCoreNLP.class.getDeclaredMethod("outputAnnotation", OutputStream.class, Annotation.class, Properties.class, AnnotationOutputter.Options.class);
    outputMethod.setAccessible(true);
    outputMethod.invoke(null, output, annotation, props, options);
    fail("Expected exception due to invalid serializer class");
  } catch (Exception e) {
    assertTrue(e.getCause().getMessage().contains("com.fake.Serializer"));
  }

  StanfordCoreNLP.clearAnnotatorPool();
}
@Test
public void testXMLOutputFailsGracefullyIfMethodMissing() throws Exception {
  Properties props = new Properties();
  props.setProperty("annotators", "tokenize,ssplit");

  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  Annotation annotation = new Annotation("Text for xml output.");

  pipeline.annotate(annotation);

  try {
    Class<?> clazz = Class.forName("edu.stanford.nlp.pipeline.XMLOutputter");
    java.lang.reflect.Method method = clazz.getDeclaredMethod("xmlPrint", Annotation.class, OutputStream.class, StanfordCoreNLP.class);
    method.setAccessible(false); 

    pipeline.xmlPrint(annotation, new ByteArrayOutputStream());
    fail("Expected RuntimeException due to inaccessible XMLOutputter method");
  } catch (RuntimeException e) {
    assertTrue(e.getCause() instanceof IllegalAccessException);
  }

  StanfordCoreNLP.clearAnnotatorPool();
} 
}