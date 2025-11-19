public class StanfordCoreNLP_wosr_2_GPTLLMTest { 

 @Test
  public void testDefaultConstructor() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    assertNotNull(pipeline);
    assertNotNull(pipeline.getProperties());
  }
@Test
  public void testConstructorWithNullProperties() {
    Properties props = null;
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertNotNull(pipeline);
    assertNotNull(pipeline.getProperties());
  }
@Test
  public void testCustomAnnotatorsPreservedFromProps() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertEquals("tokenize,ssplit", pipeline.getProperties().getProperty("annotators"));
  }
@Test
  public void testPreTokenizedAdjustsAnnotators() {
    Properties props = new Properties();
    props.setProperty("annotators", "pos");
    props.setProperty("preTokenized", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String annotators = pipeline.getProperties().getProperty("annotators");
    assertTrue(annotators.contains("tokenize"));
    assertTrue(annotators.contains("ssplit"));
    assertTrue(annotators.contains("pos"));
  }
@Test
  public void testNormalizeAnnotatorsReplacesCDCWithTokenize() {
    Properties props = new Properties();
    props.setProperty("annotators", "cdc_tokenize,pos");
    StanfordCoreNLP.normalizeAnnotators(props);
    assertEquals("tokenize,pos", props.getProperty("annotators"));
  }
@Test
  public void testEnsurePrerequisiteAnnotatorsAddsMissingDependencies() {
    Properties props = new Properties();
    String cleaned = StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"lemma"}, props);
    assertTrue(cleaned.contains("tokenize"));
    assertTrue(cleaned.contains("ssplit"));
    assertTrue(cleaned.contains("pos"));
    assertTrue(cleaned.contains("lemma"));
  }
@Test
  public void testUsesBinaryTreesForSentiment() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
    boolean uses = StanfordCoreNLP.usesBinaryTrees(props);
    assertTrue(uses);
  }
@Test
  public void testUsesBinaryTreesWithoutSentimentIsFalse() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    boolean uses = StanfordCoreNLP.usesBinaryTrees(props);
    assertFalse(uses);
  }
@Test
  public void testProcessCreatesAnnotation() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("This is a test.");
    assertNotNull(annotation);
    assertTrue(annotation.containsKey(edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testPrettyPrintOutput() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Testing pretty print.");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter w = new PrintWriter(os);
    pipeline.prettyPrint(ann, w);
    w.flush();
    String output = os.toString("UTF-8");
    assertTrue(output.contains("Testing"));
  }
@Test
  public void testJsonPrint() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Testing JSON output.");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    pipeline.jsonPrint(ann, new PrintWriter(os));
    String output = os.toString("UTF-8");
    assertTrue(output.contains("tokens"));
  }
@Test
  public void testXmlPrintViaReflection() throws Exception {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation ann = pipeline.process("Testing XML output.");
    Class<?> cls = Class.forName("edu.stanford.nlp.pipeline.XMLOutputter");
    Method method = cls.getMethod("xmlPrint", Annotation.class, java.io.OutputStream.class, StanfordCoreNLP.class);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    method.invoke(null, ann, os, pipeline);
    assertTrue(os.toString("UTF-8").contains("<"));
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidAnnotatorNameThrows() {
    Properties props = new Properties();
    StanfordCoreNLP.ensurePrerequisiteAnnotators(new String[]{"invalid_annotator"}, props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownOutputFormatThrows() {
    Method method = null;
    try {
      method = StanfordCoreNLP.class.getDeclaredMethod("getDefaultExtension", OutputFormat.class);
      method.setAccessible(true);
      method.invoke(null, (Object) null); 
    } catch (Exception e) {
      Throwable cause = e.getCause();
      if (cause instanceof IllegalArgumentException) {
        throw (IllegalArgumentException) cause;
      }
      fail("Unexpected exception: " + e);
    }
  }
@Test
  public void testOutputFormatExtensions() throws Exception {
    assertEquals(".xml", invokeGetDefaultExtension(OutputFormat.XML));
    assertEquals(".json", invokeGetDefaultExtension(OutputFormat.JSON));
    assertEquals(".conll", invokeGetDefaultExtension(OutputFormat.CONLL));
    assertEquals(".conllu", invokeGetDefaultExtension(OutputFormat.CONLLU));
    assertEquals(".out", invokeGetDefaultExtension(OutputFormat.TEXT));
    assertEquals(".tag", invokeGetDefaultExtension(OutputFormat.TAGGED));
    assertEquals(".inxml", invokeGetDefaultExtension(OutputFormat.INLINEXML));
    assertEquals(".ser.gz", invokeGetDefaultExtension(OutputFormat.SERIALIZED));
    assertEquals(".out", invokeGetDefaultExtension(OutputFormat.CUSTOM));
  }
@Test
  public void testGetEncodingDefault() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    assertEquals("UTF-8", pipeline.getEncoding());
  }
@Test
  public void testConstructorMergesWithClasspathIfNoAnnotatorsSet() {
    Properties props = new Properties();
    props.setProperty("threads", "1");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertNotNull(pipeline.getProperties().getProperty("annotators"));
  }
@Test
  public void testConstructorHandlesFileListSynonym() {
    Properties props = new Properties();
    props.setProperty("fileList", "input.txt");
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    String filelist = pipeline.getProperties().getProperty("filelist");
    assertEquals("input.txt", filelist);
  } 
}