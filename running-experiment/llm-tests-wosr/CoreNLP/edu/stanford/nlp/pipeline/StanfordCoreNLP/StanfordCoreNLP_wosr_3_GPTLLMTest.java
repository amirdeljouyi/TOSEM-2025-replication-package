public class StanfordCoreNLP_wosr_3_GPTLLMTest { 

 @Test
  public void testDefaultConstructorLoadsProperties() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    assertNotNull(pipeline.getProperties());
    assertTrue(pipeline.getProperties().containsKey("annotators"));
  }
@Test
  public void testConstructorWithMinimalAnnotators() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertNotNull(pipeline);
    assertEquals("tokenize,ssplit,pos,lemma", props.getProperty("annotators"));
  }
@Test
  public void testPreTokenizedAnnotatorsAdjustment() {
    Properties props = new Properties();
    props.setProperty("annotators", "ner");
    props.setProperty("preTokenized", "true");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    assertTrue(pipeline.getProperties().getProperty("annotators").startsWith("tokenize,ssplit,ner"));
    assertEquals("true", pipeline.getProperties().getProperty("tokenize.whitespace"));
    assertEquals("true", pipeline.getProperties().getProperty("ssplit.eolonly"));
  }
@Test
  public void testNormalizeAnnotatorsUnifiesCleanXml() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,cleanxml,pos");
    StanfordCoreNLP.normalizeAnnotators(props);
    assertTrue(props.getProperty("annotators").contains("tokenize"));
    assertFalse(props.getProperty("annotators").contains("cleanxml"));
    assertEquals("true", props.getProperty("tokenize.cleanxml"));
  }
@Test
  public void testNormalizeAnnotatorsReplacesSsplit() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,lemma");
    StanfordCoreNLP.normalizeAnnotators(props);
    assertTrue(props.getProperty("annotators").contains("tokenize"));
    assertFalse(props.getProperty("annotators").contains("ssplit"));
  }
@Test
  public void testReplaceAnnotatorSubstitution() {
    Properties props = new Properties();
    props.setProperty("annotators", "cdc_tokenize,lemma");
    StanfordCoreNLP.replaceAnnotator(props, "cdc_tokenize", "tokenize");
    assertEquals("tokenize,lemma", props.getProperty("annotators"));
  }
@Test
  public void testUnifyTokenizePropertyRemovesCleanxmlAndSetsFlag() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,cleanxml,pos");
    StanfordCoreNLP.unifyTokenizeProperty(props, "cleanxml", "tokenize.cleanxml");
    assertEquals("true", props.getProperty("tokenize.cleanxml"));
    assertFalse(props.getProperty("annotators").contains("cleanxml"));
  }
@Test
  public void testEnsurePrerequisiteAnnotatorsSortsProperly() {
    Properties props = new Properties();
    String[] annotators = new String[] { "ner" };
    String result = StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
    assertTrue(result.contains("tokenize"));
    assertTrue(result.contains("ssplit"));
    assertTrue(result.contains("pos"));
    assertTrue(result.contains("ner"));
    assertTrue(result.indexOf("tokenize") < result.indexOf("ssplit"));
    assertTrue(result.indexOf("ssplit") < result.indexOf("pos"));
    assertTrue(result.indexOf("pos") < result.indexOf("ner"));
  }
@Test(expected = IllegalArgumentException.class)
  public void testEnsurePrerequisiteAnnotatorsWithUnknownAnnotatorFails() {
    Properties props = new Properties();
    String[] annotators = new String[] { "unknown_annotator" };
    StanfordCoreNLP.ensurePrerequisiteAnnotators(annotators, props);
  }
@Test
  public void testProcessReturnsAnnotatedText() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("This is a sentence.");
    assertNotNull(annotation);
    assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).size() > 0);
  }
@Test
  public void testJsonPrintProducesOutput() throws IOException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,pos");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("Stanford University is located in California.");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    pipeline.jsonPrint(annotation, new OutputStreamWriter(out));
    String json = out.toString("UTF-8");
    assertTrue(json.contains("Stanford"));
    assertTrue(json.contains("California"));
  }
@Test
  public void testPrettyPrintDoesNotThrow() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = pipeline.process("A quick brown fox.");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(out);
    pipeline.prettyPrint(annotation, pw);
    pw.flush();
    String output = out.toString();
    assertTrue(output.contains("Sentence #1"));
  }
@Test
  public void testAnnotateCallbackSingleThreaded() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "1");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation = new Annotation("This is a test.");
    pipeline.annotate(annotation, ann -> {
      assertTrue(ann.get(CoreAnnotations.SentencesAnnotation.class).size() == 1);
    });
  }
@Test
  public void testAnnotateCallbackMultiThreaded() throws InterruptedException {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("threads", "2");
    final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    Annotation annotation = new Annotation("Thread one.");
    final boolean[] callbackRan = new boolean[1];
    pipeline.annotate(annotation, ann -> {
      callbackRan[0] = true;
      assertEquals(1, ann.get(CoreAnnotations.SentencesAnnotation.class).size());
    });

    int attempts = 0;
    while (!callbackRan[0] && attempts < 100) {
      Thread.sleep(50);
      attempts++;
    }
    assertTrue("Callback did not complete in time", callbackRan[0]);
  }
@Test
  public void testProcessToCoreDocumentReturnsSentences() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument doc = pipeline.processToCoreDocument("Hello world. I love NLP.");
    assertNotNull(doc.sentences());
    assertEquals(2, doc.sentences().size());
  }
@Test
  public void testUsesBinaryTreesTrueWhenSentiment() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit,sentiment");
    boolean result = StanfordCoreNLP.usesBinaryTrees(props);
    assertTrue(result);
  }
@Test
  public void testUsesBinaryTreesFalseWhenNoSentiment() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    boolean result = StanfordCoreNLP.usesBinaryTrees(props);
    assertFalse(result);
  }
@Test
  public void testGetEncodingReturnsUTF8ByDefault() {
    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    assertEquals("UTF-8", pipeline.getEncoding());
  }
@Test
  public void testToStringOfAnnotatorSignature() {
    StanfordCoreNLP.AnnotatorSignature sig = new StanfordCoreNLP.AnnotatorSignature("pos", "v1");
    String str = sig.toString();
    assertTrue(str.contains("pos"));
    assertTrue(str.contains("v1"));
  }
@Test
  public void testGlobalAnnotatorCacheStoresInstance() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline1 = new StanfordCoreNLP(props);
    Annotator a1 = StanfordCoreNLP.getExistingAnnotator("tokenize");
    assertNotNull(a1);
  } 
}