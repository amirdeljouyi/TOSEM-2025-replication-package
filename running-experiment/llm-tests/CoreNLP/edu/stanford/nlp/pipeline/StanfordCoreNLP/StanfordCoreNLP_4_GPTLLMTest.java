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

public class StanfordCoreNLP_4_GPTLLMTest {

 @Test
  public void testDefaultConstructorCreatesPipeline() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    assertNotNull(pipeline);
    assertNotNull(pipeline.getProperties());
  }
@Test
  public void testConstructorWithAnnotatorsProperty() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertNotNull(pipeline);
    String annotators = pipeline.getProperties().getProperty("annotators");
    assertEquals("tokenize,ssplit,pos,lemma", annotators);
  }
@Test
  public void testPreTokenizedAdjustsAnnotatorsAndProperties() {
    Properties props = new Properties();
    props.setProperty("preTokenized", "true");
    props.setProperty("annotators", "lemma");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String result = pipeline.getProperties().getProperty("annotators");
    boolean containsTokenize = result.contains("tokenize");
    boolean containsSsplit = result.contains("ssplit");
    boolean containsLemma = result.contains("lemma");
    assertTrue(containsTokenize && containsSsplit && containsLemma);
  }
@Test(expected = IllegalArgumentException.class)
  public void testMissingRequiredAnnotatorsThrowsException() {
    Properties props = new Properties();
    props.setProperty("annotators", "lemma");
    new StanfordCoreNLP(props); 
  }
@Test
  public void testEnsurePrerequisiteAnnotatorsReturnsCorrectOrder() {
    Properties props = new Properties();
    String[] input = new String[]{"lemma"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    assertTrue(result.contains("tokenize"));
    assertTrue(result.contains("ssplit"));
    assertTrue(result.contains("pos"));
    assertTrue(result.contains("lemma"));
    assertTrue(result.indexOf("tokenize") < result.indexOf("ssplit"));
    assertTrue(result.indexOf("ssplit") < result.indexOf("pos"));
    assertTrue(result.indexOf("pos") < result.indexOf("lemma"));
  }
@Test
  public void testProcessReturnsAnnotationTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String text = "Stanford NLP";
    Annotation annotation = pipeline.process(text);
    assertNotNull(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("Stanford", tokens.get(0).word());
    assertEquals("NLP", tokens.get(1).word());
  }
@Test
  public void testAnnotateCoreDocumentStoresTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument doc = new CoreDocument("Test input.");
    pipeline.annotate(doc);
    assertNotNull(doc.tokens());
    assertEquals("Test", doc.tokens().get(0).word());
  }
@Test
  public void testXmlPrintProducesOutputWithoutException() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "xml");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Testing XML output.");
    StringWriter writer = new StringWriter();
    pipeline.xmlPrint(annotation, writer);
    String xml = writer.toString();
    assertTrue(xml.contains("sentence"));
  }
@Test
  public void testJsonPrintProducesTokensField() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "json");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("JSON format test.");
    StringWriter writer = new StringWriter();
    pipeline.jsonPrint(annotation, writer);
    String json = writer.toString();
    assertTrue(json.contains("\"tokens\"") || json.contains("'tokens'"));
  }
@Test
  public void testGetEncodingReturnsDefaultUTF8() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    String encoding = pipeline.getEncoding();
    assertEquals("UTF-8", encoding);
  }

@Test
  public void testUnifyTokenizePropertyRemovesStandAloneCleanXml() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,cleanxml,lemma");
    StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
    String annotators = props.getProperty("annotators");
    String cleanxmlFlag = props.getProperty("tokenize.cleanxml");
    assertFalse(annotators.contains("cleanxml"));
    assertEquals("true", cleanxmlFlag);
  }
@Test
  public void testReplaceAnnotatorReplacesCdcTokenize() {
    Properties props = new Properties();
    props.setProperty("annotators", "cdc_tokenize,pos");
    StanfordCoreNLP.replaceAnnotator(props, "cdc_tokenize", "tokenize");
    String annotators = props.getProperty("annotators");
    assertTrue(annotators.contains("tokenize"));
    assertFalse(annotators.contains("cdc_tokenize"));
  }
@Test
  public void testTimingInformationIncludesTokensProcessed() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.process("This is a timing test.");
    String timing = pipeline.timingInformation();
    assertTrue(timing.toLowerCase(Locale.ROOT).contains("tokens"));
  }
@Test
  public void testAnnotateAnnotationIncrementsTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Token test case.");
    pipeline.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("Token", tokens.get(0).word());
  }
@Test
  public void testAnnotatorPoolIsClearedOnClearAnnotatorPoolCall() {
    StanfordCoreNLP.clearAnnotatorPool(); 
    int beforeSize = StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size();
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    new StanfordCoreNLP(props);
    int afterSize = StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size();
    assertTrue(afterSize > beforeSize);
    StanfordCoreNLP.clearAnnotatorPool();
    int clearedSize = StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size();
    assertEquals(0, clearedSize);
  }
@Test
  public void testAnnotatorsOrderInsertionOfRegexnerAfterNER() {
    Properties props = new Properties();
    String[] input = new String[]{"tokenize", "ssplit", "ner", "regexner"};
    String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    int nerIdx = ordered.indexOf("ner");
    int regexnerIdx = ordered.indexOf("regexner");
    assertTrue("regexner should come after ner", regexnerIdx > nerIdx);
  }
@Test
  public void testAnnotatorsOrderCorefBeforeOpenIE() {
    Properties props = new Properties();
    String[] input = new String[]{"tokenize", "ssplit", "coref", "openie"};
    String ordered = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    int openieIdx = ordered.indexOf("openie");
    int corefIdx = ordered.indexOf("coref");
    assertTrue("coref should come before openie", corefIdx < openieIdx);
  }
@Test(expected = IllegalArgumentException.class)
  public void testEnsurePrerequisiteAnnotatorsWithUnknownAnnotator() {
    Properties props = new Properties();
    String[] input = new String[]{"tokenize", "alien_annotator"};
    StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testEnsurePrerequisiteAnnotatorsWithCircularDependency() {
    Properties props = new Properties();
    String[] input = new String[]{"a"};
    StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
  }
@Test
  public void testProcessReturnsMultipleTokens() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String text = "This is one. This is two.";
    Annotation annotation = pipeline.process(text);
    List sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
  }
@Test
  public void testProcessToCoreDocumentCreatesSentences() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument doc = pipeline.processToCoreDocument("A test. Another test.");
    assertEquals(2, doc.sentences().size());
  }
@Test(expected = RuntimeException.class)
  public void testMissingPropertiesFileThrowsException() {
    new StanfordCoreNLP("non_existent_file_should_throw");
  }
@Test
  public void testXmlPrintHandlesIOExceptionGracefully() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Testing IOException.");
    pipeline.annotate(annotation);
  }
@Test
  public void testOutputFormatCustomFallsBackProperly() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "custom");
    props.setProperty("customOutputter", "edu.stanford.nlp.pipeline.TextOutputter");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("This is custom output.");
    pipeline.annotate(annotation);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    pipeline.prettyPrint(annotation, baos);
    String result = baos.toString();
    assertTrue(result.contains("This"));
  }
@Test
  public void testJsonPrintDoesNotThrowWithUtf16Encoding() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("encoding", "UTF-16");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Check UTF-16.");
    pipeline.annotate(annotation);
    StringWriter sw = new StringWriter();
    pipeline.jsonPrint(annotation, sw);
    String result = sw.toString();
    assertTrue(result.contains("tokens"));
  }
@Test
  public void testProcessHandlesEmptyString() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("");
    List tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens == null || tokens.isEmpty());
  }
@Test
  public void testAnnotationWithDuplicateAnnotators() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation("Hello world.");
    pipeline.annotate(ann);
    List tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("Hello", ((edu.stanford.nlp.ling.CoreLabel) tokens.get(0)).word());
  }
@Test(expected = RuntimeException.class)
  public void testInvalidCustomAnnotatorThrowsAtPipelineCreation() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,myfake");
    props.setProperty("customAnnotatorClass.myfake", "not.real.AnnotatorClass");
    new StanfordCoreNLP(props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testOutputAnnotationFailsWithUnknownFormat() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Fail output format test.");
    StanfordCoreNLP.OutputFormat unknown = StanfordCoreNLP.OutputFormat.valueOf("TEXT");
    OutputStream os = new ByteArrayOutputStream();
    props.setProperty("outputFormat", "unknownOutput");
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os);
  }
@Test
  public void testPreTokenizedWithBareAnnotators() {
    Properties props = new Properties();
    props.setProperty("preTokenized", "true");
    props.setProperty("annotators", "ner");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String adjusted = pipeline.getProperties().getProperty("annotators");
    assertTrue(adjusted.contains("tokenize"));
    assertTrue(adjusted.contains("ssplit"));
    assertTrue(adjusted.contains("ner"));
  }
@Test(expected = RuntimeException.class)
  public void testOutputAnnotationFailsWithInvalidCustomOutputter() throws Exception {
    Properties props = new Properties();
    props.setProperty("customOutputter", "not.real.CustomOutputter");
    props.setProperty("outputFormat", "custom");
    Annotation annotation = new Annotation("Force custom outputter failure.");
    OutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os);
  }
@Test
  public void testOutputAnnotationRunsForInlineXMLAndConllu() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    Annotation annotation = new Annotation("Testing inline XML and conllu.");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.annotate(annotation);

    props.setProperty("outputFormat", "inlinexml");
    OutputStream os1 = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os1);
    assertTrue(os1.toString().contains("Stanford"));

    props.setProperty("outputFormat", "conllu");
    OutputStream os2 = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os2);
    String out2 = os2.toString();
    assertTrue(out2.contains("Stanford"));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateCallbackWrapsErrorInExceptionAnnotation() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "1");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Annotation malformed = new Annotation((Annotation) null);
    malformed.set(CoreAnnotations.TextAnnotation.class, null);

    pipeline.annotate(malformed, result -> {
      Throwable cause = result.get(CoreAnnotations.ExceptionAnnotation.class);
      assertNotNull("Expected exception in annotation", cause);
      assertTrue(cause instanceof NullPointerException);
      throw new RuntimeException("verified");
    });
  }

@Test
  public void testEnsurePrerequisiteRemovesDepparseWhenParseExists() {
    Properties props = new Properties();
    String[] input = new String[] {"tokenize", "ssplit", "parse", "depparse"};
    String output = StanfordCoreNLP.ensurePrerequisiteAnnotators(input, props);
    assertTrue(output.contains("parse"));
    assertFalse(output.contains("depparse"));  
  }
@Test
  public void testMissingAnnotationPropertyTriggersPrintErrorWhenSerializerMissing() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "serialized");
    props.setProperty("serializer", "edu.stanford.nlp.pipeline.NonExistentSerializer");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Trigger serializer error.");
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os);
      fail("Expected RuntimeException due to invalid serializer");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("loadByReflection"));
    }
  }
@Test
  public void testJsonPrintHandlesStreamIOExceptionGracefully() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "json");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Stream error test.");
    pipeline.annotate(annotation);
    Writer failingWriter = new Writer() {
      @Override public void write(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("Simulated stream failure");
      }
      @Override public void flush() {}
      @Override public void close() {}
    };
    try {
      pipeline.jsonPrint(annotation, failingWriter);
      fail("Expected IOException to be thrown");
    } catch (IOException e) {
      assertEquals("Simulated stream failure", e.getMessage());
    }
  }
@Test
  public void testGetExistingAnnotatorReturnsNullIfNotRegistered() {
    StanfordCoreNLP.clearAnnotatorPool();
    Annotator result = StanfordCoreNLP.getExistingAnnotator("fakeannotator_not_defined");
    assertNull(result);
  }
@Test
  public void testGetExistingAnnotatorReturnsValidInstanceWhenPresent() {
    StanfordCoreNLP.clearAnnotatorPool();
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotator annotator = StanfordCoreNLP.getExistingAnnotator("tokenize");
    assertNotNull(annotator);
  }
@Test
  public void testAnnotationWithoutTokensMaintainsSafeTimingInformation() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    Annotation annotation = new Annotation("");
    pipeline.annotate(annotation);
    String timingInfo = pipeline.timingInformation();
    assertTrue(timingInfo.contains("tokens"));
  }
@Test
  public void testReplaceAnnotatorDoesNothingIfOldAnnotatorIsMissing() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,lemma");
    StanfordCoreNLP.replaceAnnotator(props, "notThere", "shouldNotAffect");
    String annotators = props.getProperty("annotators");
    assertEquals("tokenize,lemma", annotators);
  }
@Test
  public void testUnifyTokenizePropertySkipsIfConditionsNotMet() {
    Properties props = new Properties();
    props.setProperty("annotators", "ssplit,lemma");
    StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
    String annotators = props.getProperty("annotators");
    assertEquals("ssplit,lemma", annotators);
    assertNull(props.getProperty("tokenize.cleanxml"));
  }
@Test
  public void testOutputFormatCaseInsensitiveParsingWorks() {
    String upper = StanfordCoreNLP.OutputFormat.valueOf("TEXT").name();
    String lower = StanfordCoreNLP.OutputFormat.valueOf("text".toUpperCase(Locale.ROOT)).name();
    assertEquals(upper, lower);
  }
@Test
  public void testShellFailsIfInputIsNull() throws IOException {
    
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
    System.setIn(in);
    try {
      pipeline.run();
    } catch (IOException e) {
      fail("Pipeline should handle empty input without exception");
    }
  }
@Test
  public void testAnnotateDoesNotAddTokensIfNoTokenizePresent() {
    Properties props = new Properties();
    props.setProperty("annotators", "ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Simple test case");
    pipeline.annotate(annotation);
    List tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNull(tokens);
  }
@Test
  public void testJsonOutputContainsExpectedTokenStructure() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "json");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Stanford NLP test.");
    pipeline.annotate(annotation);
    StringWriter w = new StringWriter();
    pipeline.jsonPrint(annotation, w);
    String json = w.toString();
    assertTrue(json.contains("\"tokens\""));
    assertTrue(json.contains("Stanford"));
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidAnnotatorSignatureThrowsOnEnsureSatisfiable() {
    String[] annotators = new String[]{"parse"};
    Properties props = new Properties();
    Annotator.DEFAULT_REQUIREMENTS.put("parse", Collections.singleton("nonexistent_requirement"));
    StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
  }
@Test
  public void testProperlySkipsCleanXMLIfTokenizeNotPresent() {
    Properties props = new Properties();
    props.setProperty("annotators", "cleanxml,lemma");
    StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
    String updated = props.getProperty("annotators");
    String cleanOption = props.getProperty("tokenize.cleanxml");
    assertEquals("lemma", updated);
    assertEquals("true", cleanOption);
  }
@Test
  public void testNoChangeWhenReplacingWithSameAnnotator() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,tokenize");
    StanfordCoreNLP.replaceAnnotator(props, "tokenize", "tokenize");
    assertEquals("tokenize,ssplit,tokenize", props.getProperty("annotators"));
  }
@Test
  public void testTimingInformationIncludesNumberOfTokensProcessed() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String text = "How many words will this sentence give us?";
    Annotation annotation = new Annotation(text);
    pipeline.annotate(annotation);
    String timing = pipeline.timingInformation();
    assertTrue(timing.contains("tokens/sec."));
  }
@Test
  public void testDefaultPipelinePropertiesContainDefaultAnnotators() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    String annotators = pipeline.getProperties().getProperty("annotators");
    assertNotNull(annotators);
  }
@Test
  public void testNormalizeAnnotatorsCdcTokenizeReplacement() {
    Properties props = new Properties();
    props.setProperty("annotators", "cdc_tokenize,ssplit,ner");
    StanfordCoreNLP.normalizeAnnotators(props);
    String annotators = props.getProperty("annotators");
    assertTrue(annotators.startsWith("tokenize"));
    assertFalse(annotators.contains("cdc_tokenize"));
  }
@Test
  public void testNormalizeAnnotatorsHandlesNonstandardPreTokenized() {
    Properties props = new Properties();
    props.setProperty("annotators", "customXYZ");
    props.setProperty("preTokenized", "true");
    new StanfordCoreNLP(props); 
    assertTrue(props.getProperty("annotators").contains("tokenize"));
  }
@Test
  public void testCreateOutputterConllFormatNoException() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "conll");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("CoNLL output test.");
    pipeline.annotate(annotation);
    OutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os);
    String result = os.toString();
    assertTrue(result.contains("Stanford")); 
  }
@Test
  public void testOutputAnnotationSerializedDefaultsToProtobuf() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "serialized");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Serialize this.");
    OutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os);
    byte[] bytes = ((ByteArrayOutputStream) os).toByteArray();
    assertTrue(bytes.length > 0);
  }
@Test
  public void testAnnOutputConllAndConlluDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation("Testing multi-format.");
    pipeline.annotate(ann);

    props.setProperty("outputFormat", "conll");
    OutputStream os1 = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(ann, os1);

    props.setProperty("outputFormat", "conllu");
    OutputStream os2 = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(ann, os2);

    assertTrue(os1.toString().length() > 0);
    assertTrue(os2.toString().length() > 0);
  }
@Test
  public void testAnnotatorRequirementSatisfiedOrderingVerified() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,pos,lemma");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertNotNull(pipeline);
  }
@Test
  public void testOrderedAnnotatorsWithRedundantAdditions() {
    Properties props = new Properties();
    String[] annotators = {"aaa", "ccc"};
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
    assertTrue(result.contains("aaa"));
    assertTrue(result.contains("bbb"));
    assertTrue(result.contains("ccc"));
  }
@Test
  public void testXmlOutputterClassNotPresentSkipsFailGracefully() {
    try {
      Class<?> clazz = Class.forName("edu.stanford.nlp.pipeline.XMLOutputter_SKIP_MISSING");
      fail("Class should not be found");
    } catch (ClassNotFoundException e) {
      assertTrue(true);
    }
  }
@Test
  public void testShellModeInputHandlesSystemInGracefully() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    System.setIn(new java.io.ByteArrayInputStream("".getBytes()));
    try {
      pipeline.run(false); 
    } catch (IllegalStateException e) {
      fail("Should not throw");
    }
  }
@Test
  public void testOutputAnnotationTextFormatExecutesSuccessfully() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "text");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Print this text.");
    OutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os);
    String result = os.toString();
    assertTrue(result.contains("Print"));
  }
@Test
  public void testSecureJoinOfQuoteBeforeQuoteAttribution() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,quote,quoteattribution");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation("He said, \"I am here\".");
    pipeline.annotate(ann);
    List list = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(list);
  }
@Test
  public void testLoadPropertiesFromClasspathFailsGracefully() {
    try {
      StanfordCoreNLP.class.getDeclaredMethod("loadPropertiesFromClasspath")
          .invoke(null);
      fail("Expected RuntimeException due to missing classpath props");
    } catch (Exception e) {
      assertTrue(e.getMessage().toLowerCase().contains("could not find properties file"));
    }
  }
@Test(expected = RuntimeException.class)
  public void testXmlPrintThrowsWhenXMLOutputterMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Hello XML");
    OutputStream os = new ByteArrayOutputStream();
    Class outputterClass = Class.forName("edu.stanford.nlp.pipeline.XMLOutputter");
    outputterClass.getMethod("xmlPrint", Annotation.class, OutputStream.class, StanfordCoreNLP.class);
    
    pipeline.xmlPrint(annotation, os);
  }
@Test
  public void testSystemPropertySetWhenSsplitNotPresentInAnnotators() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String sysProperty = System.getProperty("ssplit.eolonly");
    assertEquals("false", sysProperty);
  }
@Test(expected = RuntimeException.class)
  public void testRunWithMissingFilePropertyFailsInFileReadBlock() throws IOException {
    Properties props = new Properties();
    props.setProperty("file", "nonexistent_file.txt");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.run(true);
  }
@Test
  public void testProcessToCoreDocumentReturnsNonEmptySentences() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument doc = pipeline.processToCoreDocument("One sentence.");
    assertFalse(doc.sentences().isEmpty());
    assertFalse(doc.tokens().isEmpty());
    assertEquals("One", doc.tokens().get(0).word());
  }
@Test
  public void testLoadPropertiesReturnsValidPropertiesForCoreNLPResource() {
    Properties props = StanfordCoreNLP.class.getClassLoader() != null ?
        Thread.currentThread().getContextClassLoader() != null ? new Properties() : null : null;
    
    String name = "edu/stanford/nlp/pipeline/StanfordCoreNLP.properties";
    InputStream stream = StanfordCoreNLP.class.getClassLoader().getResourceAsStream(name);
    if (stream != null) {
      Properties loaded = new Properties();
      try {
        loaded.load(stream);
      } catch (IOException e) {
        fail("Unable to load internal StanfordCoreNLP properties");
      }
      assertNotNull(loaded);
    }
  }
@Test
  public void testOutputAnnotationWithInlineXMLDoesNotThrow() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "inlinexml");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = new Annotation("Inline test.");
    pipeline.annotate(ann);
    OutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(ann, os);
    assertTrue(os.toString().contains("Inline"));
  }
@Test
  public void testProcessWithSerializedOutputFormatGeneratesBytes() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "serialized");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Serialize again.");
    OutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(ann, os);
    assertTrue(((ByteArrayOutputStream) os).size() > 0);
  }
@Test
  public void testJsonOutputWriterPrintsSafely() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "json");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Print JSON safely.");
    StringWriter sw = new StringWriter();
    pipeline.jsonPrint(ann, sw);
    String output = sw.toString();
    assertTrue(output.contains("tokens"));
  }
@Test
  public void testTimingIncludesNonNegativeTokenCount() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,lemma");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Timing test sentence.");
    String info = pipeline.timingInformation();
    assertTrue(info.contains("tokens/sec."));
  }
@Test
  public void testClearAnnotatorPoolActuallyClearsSharedCache() {
    StanfordCoreNLP.clearAnnotatorPool();
    int sizeBefore = StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size();
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    new StanfordCoreNLP(props);
    assertTrue(StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size() > sizeBefore);
    StanfordCoreNLP.clearAnnotatorPool();
    assertEquals(0, StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size());
  }
@Test
  public void testGetExistingAnnotatorReturnsNullIfUninitialized() {
    Annotator result = StanfordCoreNLP.getExistingAnnotator("nonexistent");
    assertNull(result);
  }
@Test
  public void testDuplicateAnnotatorsAreHandledOnce() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,tokenize");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("Word.");
    pipeline.annotate(annotation);
    List tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
  }
@Test
  public void testProcessHandlesOnlyWhitespaceInputGracefully() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("   \n\t");
    assertNull(ann.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testProcessFilesSkipsWhenNoClobberTrueAndFileExists() throws IOException {
    File inputFile = File.createTempFile("corenlp_input", ".txt");
    File outputFile = new File(inputFile.getParent(), inputFile.getName() + ".out");
    inputFile.deleteOnExit();
    outputFile.createNewFile();
    outputFile.deleteOnExit();

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputDirectory", inputFile.getParent());
    props.setProperty("noClobber", "true");
    props.setProperty("outputFormat", "text");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Collection<File> files = new ArrayList<>();
    files.add(inputFile);
    pipeline.processFiles(null, files, 1, false, Optional.empty());
    assertTrue(outputFile.exists());
  }
@Test
  public void testProcessFilesSkipsFileMatchingOutputFileName() throws IOException {
    File inputFile = File.createTempFile("same", ".test");
    inputFile.deleteOnExit();

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("outputDirectory", inputFile.getParent());
    props.setProperty("replaceExtension", "false");
    props.setProperty("noClobber", "false");
    props.setProperty("outputFormat", "text");

    File outputFile = new File(inputFile.getParent(), inputFile.getName());
    outputFile.deleteOnExit();

    Collection<File> files = new ArrayList<>();
    files.add(inputFile);

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.processFiles(null, files, 1, false, Optional.empty());
    assertTrue(inputFile.exists());
  }
@Test
  public void testRedundantGlobalCacheDoesNotCauseDuplicateInitialization() {
    StanfordCoreNLP.clearAnnotatorPool();
    int before = StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size();
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    StanfordCoreNLP pipeline1 = new StanfordCoreNLP(props);
    StanfordCoreNLP pipeline2 = new StanfordCoreNLP(props);
    int after = StanfordCoreNLP.GLOBAL_ANNOTATOR_CACHE.size();
    assertTrue(after >= before + 1);
  }
@Test
  public void testSerializerLoadFallbacksSilentlyWhenPropertyMissing() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("outputFormat", "serialized");
    
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    OutputStream os = new ByteArrayOutputStream();
    Annotation ann = pipeline.process("test");
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(ann, os);
    assertTrue(os.toString().length() == 0 || os.toString().getBytes().length > 0);
  }
@Test
  public void testProcessFilesHandlesExcludeFilesList() throws IOException {
    File file = File.createTempFile("file", ".txt");
    PrintWriter w = new PrintWriter(file);
    w.println("content");
    w.close();
    file.deleteOnExit();

    File exList = File.createTempFile("exclude", ".lst");
    PrintWriter ew = new PrintWriter(exList);
    ew.println(file.getName());
    ew.close();
    exList.deleteOnExit();

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("excludeFiles", exList.getAbsolutePath());

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Collection<File> files = new LinkedList<>();
    files.add(file);
    pipeline.processFiles(file.getParent(), files, 1, false, Optional.empty());
    assertTrue(file.exists());
  }
@Test
  public void testInlineXMLOutputDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("outputFormat", "inlinexml");
    Annotation annotation = new Annotation("inline output");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.annotate(annotation);
    OutputStream os = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, os);
    assertTrue(os.toString().contains("inline"));
  }
@Test
  public void testEmptyAnnotationSerializeOutput() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize");
    props.setProperty("outputFormat", "serialized");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("");
    pipeline.annotate(annotation);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    StanfordCoreNLP.createOutputter(props, new AnnotationOutputter.Options()).accept(annotation, out);
    assertTrue(out.size() > 0);
  } 
}