public class SentimentAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testSignatureWithDefaults() {
    Properties props = new Properties();
    String sig = SentimentAnnotator.signature("sentiment", props);
    assertTrue(sig.contains("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
    assertTrue(sig.contains("sentiment.nthreads:"));
    assertTrue(sig.contains("sentiment.maxtime:-1"));
  }
@Test
  public void testSignatureWithOverrides() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "custom-model.bin");
    props.setProperty("sentiment.nthreads", "5");
    props.setProperty("sentiment.maxtime", "10000");
    String sig = SentimentAnnotator.signature("sentiment", props);
    assertTrue(sig.contains("sentiment.model:custom-model.bin"));
    assertTrue(sig.contains("sentiment.nthreads:5"));
    assertTrue(sig.contains("sentiment.maxtime:10000"));
  }
@Test
  public void testConstructionWithDefaultModel() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(1, annotator.nThreads());
    assertEquals(-1, annotator.maxTime());
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructionWithNullModel() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "");
    new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testRequirementsDeclared() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertTrue(annotator.requires().contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(annotator.requires().contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(annotator.requires().contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(annotator.requires().contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedEmpty() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertTrue(annotator.requirementsSatisfied().isEmpty());
  }
@Test
  public void testDoOneSentenceWithValidTree() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    CoreMap sentence = new TypesafeMap.Default();
    Annotation annotation = new Annotation("Test annotation");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
    Tree binarized = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), java.util.Arrays.asList(leaf));
    binarized.label().setValue("root");
    binarized.indexLeaves();

    Tree deepCopyBinarized = binarized.deepCopy();
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, deepCopyBinarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, binarized.deepCopy());

    annotator.doOneSentence(annotation, sentence);
    assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
    assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
    Tree outputTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertTrue(outputTree.size() >= 1);
  }
@Test(expected = AssertionError.class)
  public void testDoOneSentenceWithoutBinarizedTree() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    CoreMap sentence = new TypesafeMap.Default();
    Annotation annotation = new Annotation("Test");
    annotator.doOneSentence(annotation, sentence);
  }
@Test(expected = IllegalStateException.class)
  public void testDoOneSentenceWithTreeHavingSpanAnnotation() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    CoreMap sentence = new TypesafeMap.Default();
    Annotation annotation = new Annotation("Test annotation");

    Tree leaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
    Tree binarized = new LabeledScoredTreeFactory().newTreeNode(new CoreLabel(), java.util.Arrays.asList(leaf));
    binarized.label().setValue("root");
    binarized.indexLeaves();

    Tree treeWithSpan = binarized.deepCopy();
    treeWithSpan.label().setValue("root");
    ((CoreLabel) treeWithSpan.label()).set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized.deepCopy());
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, treeWithSpan);

    annotator.doOneSentence(annotation, sentence);
  }
@Test
  public void testSetSpansAndMappingsAreAttachedToTree() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    CoreMap sentence = new TypesafeMap.Default();
    Annotation annotation = new Annotation("Test");

    CoreLabel rootLabel = new CoreLabel();
    Tree leaf = new LabeledScoredTreeFactory().newLeaf(new CoreLabel());
    Tree root = new LabeledScoredTreeFactory().newTreeNode(rootLabel, java.util.Arrays.asList(leaf));
    root.indexLeaves();

    Tree binarized = root.deepCopy();
    Tree tree = root.deepCopy();

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized.deepCopy());
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    annotator.doOneSentence(annotation, sentence);

    Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertNotNull(sentimentTree);

    for (Tree t : tree) {
      CoreLabel label = (CoreLabel) t.label();
      if (label != null) {
        String sent = label.get(SentimentCoreAnnotations.SentimentClass.class);
        assertNotNull(sent);
      }
    }
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Annotation annotation = new Annotation("Dummy");
    CoreMap map = new TypesafeMap.Default();
    annotator.doOneFailedSentence(annotation, map); 
  } 
}