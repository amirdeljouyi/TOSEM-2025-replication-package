public class SentimentAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testConstructorWithValidProperties() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
    props.setProperty("sentiment.nthreads", "4");
    props.setProperty("sentiment.maxtime", "5000");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    assertNotNull(annotator);
    assertEquals(4, annotator.nThreads());
    assertEquals(5000, annotator.maxTime());
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorWithMissingModelThrowsException() {
    Properties props = new Properties();
    props.remove("sentiment.model");

    new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testRequirementsSatisfiedIsEmpty() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertNotNull(satisfied);
    assertTrue(satisfied.isEmpty());
  }
@Test
  public void testRequiresNotEmpty() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testSignatureGeneratesExpectedString() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "my/custom/model.gz");
    props.setProperty("sentiment.nthreads", "8");
    props.setProperty("sentiment.maxtime", "10000");

    String signature = SentimentAnnotator.signature("sentiment", props);

    assertTrue(signature.contains("sentiment.model:my/custom/model.gz"));
    assertTrue(signature.contains("sentiment.nthreads:8"));
    assertTrue(signature.contains("sentiment.maxtime:10000"));
  }
@Test
  public void testDoOneSentenceWithMinimalValidSentence() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("test");

    CoreLabel rootLabel = new CoreLabel();
    Tree tree = new LabeledScoredTreeFactory().newLeaf(rootLabel);
    tree = new LabeledScoredTreeFactory().newTreeNode(rootLabel, Collections.singletonList(tree));
    tree.setSpans();
    for (Tree t : tree) {
      t.label().setValue("test");
    }

    CoreMap sentence = new Annotation("test sentence");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, tree.deepCopy());
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree.deepCopy());

    annotator.doOneSentence(annotation, sentence);

    Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertNotNull(annotatedTree);

    String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(sentimentClass);
  }
@Test(expected = AssertionError.class)
  public void testDoOneSentenceWithNoBinarizedTreeThrowsAssertionError() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("text");
    annotator.doOneSentence(annotation, sentence);
  }
@Test
  public void testDoOneSentenceWithNullOriginalTreeNoException() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("text");

    CoreLabel rootLabel = new CoreLabel();
    Tree binTree = new LabeledScoredTreeFactory().newLeaf(rootLabel);
    binTree = new LabeledScoredTreeFactory().newTreeNode(rootLabel, Collections.singletonList(binTree));
    binTree.setSpans();

    CoreMap sentence = new Annotation("sentence without TreeAnnotation");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);

    annotator.doOneSentence(annotation, sentence);

    assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
    assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test(expected = IllegalStateException.class)
  public void testDoOneSentenceWhenOriginalTreeContainsSpanAnnotationThrowsException() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("text");

    CoreLabel rootLabel = new CoreLabel();
    rootLabel.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
    Tree binTree = new LabeledScoredTreeFactory().newLeaf(rootLabel);
    binTree = new LabeledScoredTreeFactory().newTreeNode(rootLabel, Collections.singletonList(binTree));

    CoreLabel treeLabel = new CoreLabel();
    treeLabel.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
    Tree originalTree = new LabeledScoredTreeFactory().newLeaf(treeLabel);
    originalTree = new LabeledScoredTreeFactory().newTreeNode(treeLabel, Collections.singletonList(originalTree));

    CoreMap sentence = new Annotation("conflict test");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

    annotator.doOneSentence(annotation, sentence);
  }
@Test
  public void testNThreadsDefaultIfUnspecified() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    assertEquals(1, annotator.nThreads());
  }
@Test
  public void testMaxTimeDefaultIfUnspecified() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    assertEquals(-1, annotator.maxTime());
  }
@Test
  public void testSetSentimentClassOnConstituentTree() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("text");

    CoreLabel spanLabel = new CoreLabel();
    Tree binTree = new LabeledScoredTreeFactory().newLeaf(spanLabel);
    binTree = new LabeledScoredTreeFactory().newTreeNode(spanLabel, Collections.singletonList(binTree));
    binTree.setSpans();

    CoreLabel treeLabel = new CoreLabel();
    Tree originalTree = new LabeledScoredTreeFactory().newLeaf(treeLabel);
    originalTree = new LabeledScoredTreeFactory().newTreeNode(treeLabel, Collections.singletonList(originalTree));
    originalTree.setSpans();

    CoreMap sentence = new Annotation("text");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, originalTree);

    annotator.doOneSentence(annotation, sentence);

    boolean foundSentimentClass = false;
    for (Tree t : originalTree) {
      CoreLabel label = (CoreLabel) t.label();
      if (label.containsKey(SentimentCoreAnnotations.SentimentClass.class)) {
        foundSentimentClass = true;
        break;
      }
    }

    assertTrue(foundSentimentClass);
  } 
}