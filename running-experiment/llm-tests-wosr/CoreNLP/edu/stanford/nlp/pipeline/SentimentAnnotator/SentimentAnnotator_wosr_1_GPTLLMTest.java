public class SentimentAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructorWithDefaultProperties() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertNotNull(annotator);
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullModelPath() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", null);
    new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testRequirementsSatisfiedIsEmpty() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> reqs = annotator.requirementsSatisfied();
    assertNotNull(reqs);
    assertTrue(reqs.isEmpty());
  }
@Test
  public void testRequiresContainsExpectedAnnotations() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> req = annotator.requires();
    assertTrue(req.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(req.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(req.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(req.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testSignatureGeneratesExpectedContent() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "path/to/model");
    props.setProperty("sentiment.nthreads", "3");
    props.setProperty("sentiment.maxtime", "1000");

    String sig = SentimentAnnotator.signature("sentiment", props);
    assertTrue(sig.contains("sentiment.model:path/to/model"));
    assertTrue(sig.contains("sentiment.nthreads:3"));
    assertTrue(sig.contains("sentiment.maxtime:1000"));
  }
@Test
  public void testNThreadsAndMaxTimeFromProperties() {
    Properties props = new Properties();
    props.setProperty("nthreads", "4");
    props.setProperty("sentiment.nthreads", "2");
    props.setProperty("sentiment.maxtime", "500");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(2, annotator.nThreads());
    assertEquals(500L, annotator.maxTime());
  }
@Test(expected = AssertionError.class)
  public void testDoOneSentenceWithMissingBinarizedTreeThrows() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Annotation ann = new Annotation("test");
    CoreMap sentence = new Annotation("sentence");
    annotator.doOneSentence(ann, sentence);
  }
@Test
  public void testDoOneSentenceWithMinimalValidInput() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation ann = new Annotation("Test annotation");

    CoreMap sentence = new Annotation("Test sentence");
    Tree leaf = Tree.valueOf("(ROOT (S (NP (DT The) (NN cat)) (VP (VBZ sits))))");

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, leaf.deepCopy());
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, leaf.deepCopy());

    annotator.doOneSentence(ann, sentence);

    assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
    String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(sentimentClass);
  }
@Test
  public void testDoOneSentenceStoresSentimentInTreeTokens() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation ann = new Annotation("Test");

    Tree inputTree = Tree.valueOf("(ROOT (S (NP (NN Cats)) (VP (VBP jump))))");

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, inputTree.deepCopy());

    Tree treeWithLabels = inputTree.deepCopy();
    LabelFactory lf = new CoreLabelFactory();
    for (Tree node : treeWithLabels) {
      node.setLabel(lf.newLabel("X"));
    }
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, treeWithLabels);

    annotator.doOneSentence(ann, sentence);

    Tree annotatedTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertNotNull(annotatedTree);
    String sentimentClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(sentimentClass);
    Tree labeledTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    for (Tree node : labeledTree) {
      if (node.isLeaf()) {
        continue;
      }
      CoreLabel label = (CoreLabel) node.label();
      assertTrue(label.containsKey(SentimentCoreAnnotations.SentimentClass.class));
      assertFalse(label.containsKey(CoreAnnotations.SpanAnnotation.class));
    }
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Annotation annotation = new Annotation("test");
    CoreMap sentence = new Annotation("failure-case");

    try {
      annotator.doOneFailedSentence(annotation, sentence);
    } catch (Exception e) {
      fail("doOneFailedSentence should not throw any exceptions");
    }
  }
@Test
  public void testTreeWithoutTreeAnnotationProcessesCorrectly() {
    Properties props = new Properties();

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree binarizedTree = Tree.valueOf("(ROOT (NP (NN Hello)))");

    CoreMap sentence = new Annotation("Only binarized");

    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree.deepCopy());

    Annotation annotation = new Annotation("test");

    annotator.doOneSentence(annotation, sentence);

    Tree resultTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertNotNull(resultTree);
    assertNotNull(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testTreeWithSpanAnnotationThrowsIllegalStateException() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree tree = Tree.valueOf("(ROOT (NP (NN Dog)))").deepCopy();
    LabelFactory lf = new CoreLabelFactory();
    for (Tree node : tree) {
      CoreLabel cl = (CoreLabel) lf.newLabel("X");
      cl.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
      node.setLabel(cl);
    }

    Tree binarized = tree.deepCopy();

    CoreMap sentence = new Annotation("sentence");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    try {
      annotator.doOneSentence(new Annotation("test"), sentence);
      fail("Expected IllegalStateException due to SpanAnnotation");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("SpanAnnotation"));
    }
  } 
}