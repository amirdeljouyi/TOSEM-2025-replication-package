public class SentimentAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testRequirementsSatisfiedReturnsEmptySet() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummyPath");
    SentimentModel dummyModel = mock(SentimentModel.class);
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = dummyModel;
      }
    };
    Assert.assertTrue(annotator.requirementsSatisfied().isEmpty());
  }
@Test
  public void testRequiresContainsExpectedDependencies() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummyPath");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = mock(SentimentModel.class);
      }
    };

    Assert.assertTrue(annotator.requires().contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    Assert.assertTrue(annotator.requires().contains(TreeCoreAnnotations.TreeAnnotation.class));
    Assert.assertTrue(annotator.requires().contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    Assert.assertTrue(annotator.requires().contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testSignatureReturnsExpectedFormat() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "customModel.ser.gz");
    props.setProperty("sentiment.nthreads", "4");
    props.setProperty("sentiment.maxtime", "1000");

    String actual = SentimentAnnotator.signature("sentiment", props);
    Assert.assertTrue(actual.contains("sentiment.model:customModel.ser.gz"));
    Assert.assertTrue(actual.contains("sentiment.nthreads:4"));
    Assert.assertTrue(actual.contains("sentiment.maxtime:1000"));
  }
@Test
  public void testConstructorSetsModelPathAndThreads() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "dummyPath.ser.gz");
    props.setProperty("sentiment.nthreads", "3");
    props.setProperty("sentiment.maxtime", "9000");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = mock(SentimentModel.class);
      }
    };

    Assert.assertEquals(3, annotator.nThreads());
    Assert.assertEquals(9000L, annotator.maxTime());
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorThrowsWhenModelMissing() {
    Properties props = new Properties();
    props.remove("sentiment.model");

    new SentimentAnnotator("sentiment", props);
  }
@Test(expected = AssertionError.class)
  public void testDoOneSentenceThrowsWhenNoBinarizedTree() {
    Properties props = new Properties();
    props.put("sentiment.model", "dummyPath");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = mock(SentimentModel.class);
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(null);
    annotator.doOneSentence(null, sentence);
  }
@Test
  public void testDoOneSentenceAnnotatesCollapsedUnaryTree() {
    Properties props = new Properties();
    props.put("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree binarizedTree = tf.newTreeNode(new CoreLabel(), List.of(
        tf.newLeaf(new CoreLabel()),
        tf.newLeaf(new CoreLabel())
    ));
    binarizedTree.setSpans();

    SentimentModel model = mock(SentimentModel.class);
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = model;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    doAnswer(invocation -> {
      Tree tree = invocation.getArgument(1);
      Assert.assertNotNull(tree);
      return null;
    }).when(sentence).set(eq(SentimentCoreAnnotations.SentimentAnnotatedTree.class), any(Tree.class));
    doAnswer(invocation -> null).when(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), anyString());

    annotator.doOneSentence(null, sentence);

    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentAnnotatedTree.class), any(Tree.class));
    verify(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), any(String.class));
  }
@Test
  public void testDoOneSentenceSentimentPropagationToTree() {
    Properties props = new Properties();
    props.put("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();

    CoreLabel rootLabel = new CoreLabel();
    Tree tree = tf.newTreeNode(rootLabel, List.of());
    tree.setSpans();

    CoreLabel binarizedLabel = new CoreLabel();
    Tree binarizedTree = tf.newTreeNode(binarizedLabel, List.of(
        tf.newLeaf(new CoreLabel())
    ));
    binarizedTree.setSpans();

    SentimentModel model = mock(SentimentModel.class);

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = model;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);
    doNothing().when(sentence).set(eq(SentimentCoreAnnotations.SentimentAnnotatedTree.class), any(Tree.class));
    doNothing().when(sentence).set(eq(SentimentCoreAnnotations.SentimentClass.class), any(String.class));

    annotator.doOneSentence(null, sentence);

    Assert.assertNull(rootLabel.get(CoreAnnotations.SpanAnnotation.class));
    Assert.assertNotNull(rootLabel.get(SentimentCoreAnnotations.SentimentClass.class));
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() {
    Properties props = new Properties();
    props.put("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = mock(SentimentModel.class);
      }
    };
    CoreMap sentence = mock(CoreMap.class);
    annotator.doOneFailedSentence(null, sentence);
    
    Assert.assertTrue(true);
  }
@Test(expected = IllegalStateException.class)
  public void testDoOneSentenceThrowsWhenSpanAnnotationExists() {
    Properties props = new Properties();
    props.put("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");

    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();

    CoreLabel labelWithSpan = new CoreLabel();
    labelWithSpan.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));

    Tree tree = tf.newTreeNode(labelWithSpan, List.of());
    tree.setSpans();

    Tree binarizedTree = tf.newTreeNode(new CoreLabel(), List.of(
        tf.newLeaf(new CoreLabel())
    ));
    binarizedTree.setSpans();

    SentimentModel model = mock(SentimentModel.class);

    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props) {
      {
        this.model = model;
      }
    };

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(TreeCoreAnnotations.BinarizedTreeAnnotation.class)).thenReturn(binarizedTree);
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(tree);

    annotator.doOneSentence(null, sentence);
  } 
}