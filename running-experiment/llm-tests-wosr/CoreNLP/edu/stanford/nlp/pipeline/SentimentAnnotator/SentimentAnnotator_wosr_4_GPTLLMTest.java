public class SentimentAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testSignatureWithDefaultProps() {
    Properties props = new Properties();
    String signature = SentimentAnnotator.signature("sentiment", props);
    assertTrue(signature.contains("sentiment.model:edu/stanford/nlp/models/sentiment/sentiment.ser.gz"));
    assertTrue(signature.contains("sentiment.nthreads:"));
    assertTrue(signature.contains("sentiment.maxtime:-1"));
  }
@Test
  public void testSignatureWithCustomProps() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", "custom/model/path.ser.gz");
    props.setProperty("sentiment.nthreads", "4");
    props.setProperty("sentiment.maxtime", "1000");
    String signature = SentimentAnnotator.signature("sentiment", props);
    assertTrue(signature.contains("sentiment.model:custom/model/path.ser.gz"));
    assertTrue(signature.contains("sentiment.nthreads:4"));
    assertTrue(signature.contains("sentiment.maxtime:1000"));
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorWithMissingModel() {
    Properties props = new Properties();
    props.setProperty("sentiment.model", null);
    new SentimentAnnotator("sentiment", props);
  }
@Test
  public void testRequirementsSatisfiedIsEmpty() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertNotNull(satisfied);
    assertTrue(satisfied.isEmpty());
  }
@Test
  public void testRequires() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertEquals(4, required.size());
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test(expected = AssertionError.class)
  public void testDoOneSentenceWithoutBinarizedTree() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");
    annotator.doOneSentence(annotation, sentence);
  }
@Test
  public void testDoOneSentenceSetsAnnotatedTreeAndSentimentClass() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree leaf1 = Tree.valueOf("(NN happy)");
    Tree leaf2 = Tree.valueOf("(NN dog)");
    Tree subtree = Tree.valueOf("(NP (NN happy) (NN dog))");
    subtree.setChildren(new Tree[]{leaf1, leaf2});
    subtree.setSpans();

    Tree binarizedTree = Tree.valueOf("(S (NP (NN happy) (NN dog)))");
    binarizedTree.setChildren(new Tree[]{subtree});
    binarizedTree.setSpans();

    Tree tree = Tree.valueOf("(S (NP (NN happy) (NN dog)))");
    tree.setChildren(new Tree[]{subtree});
    tree.setSpans();

    CoreMap sentence = new Annotation("happy dog sentence");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);

    Annotation annotation = new Annotation("happy dog");

    annotator.doOneSentence(annotation, sentence);

    Tree annotated = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertNotNull(annotated);
    String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(sentiment);
    assertTrue(Arrays.asList("Very negative", "Negative", "Neutral", "Positive", "Very positive").contains(sentiment));
  }
@Test
  public void testDoOneSentenceUsesSetSpansFromTree() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree word1 = Tree.valueOf("(NN nice)");
    Tree word2 = Tree.valueOf("(JJ cat)");
    word1.setSpans();
    word2.setSpans();

    Tree np = Tree.valueOf("(NP (NN nice) (JJ cat))");
    np.setChildren(new Tree[]{word1, word2});
    np.setSpans();

    Tree binarized = Tree.valueOf("(S (NP (NN nice) (JJ cat)))");
    binarized.setChildren(new Tree[]{np});
    binarized.setSpans();

    Tree original = Tree.valueOf("(S (NP (NN nice) (JJ cat)))");
    original.setChildren(new Tree[]{np.deepCopy()});
    original.setSpans();

    CoreLabel rootLabel = new CoreLabel();
    original.setLabel(rootLabel);

    CoreMap sentence = new Annotation("sentence");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, original);

    Annotation doc = new Annotation("test doc");

    annotator.doOneSentence(doc, sentence);

    Tree resultingTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    assertNotNull(resultingTree);

    String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    assertNotNull(sentiment);
  }
@Test(expected = IllegalStateException.class)
  public void testDoOneSentenceThrowsIfTreeHasSpanAnnotation() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);

    Tree binarized = Tree.valueOf("(S (NP (NN test)))");
    binarized.setSpans();

    Tree realTree = Tree.valueOf("(S (NP (NN test)))");
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(0, 1));
    realTree.setLabel(label);
    realTree.setSpans();

    CoreMap sentence = new Annotation("sentence");
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarized);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, realTree);

    Annotation doc = new Annotation("doc");

    annotator.doOneSentence(doc, sentence);
  }
@Test
  public void testNThreadsAndMaxTimeExtraction() {
    Properties props = new Properties();
    props.setProperty("sentiment.nthreads", "3");
    props.setProperty("sentiment.maxtime", "5678");
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    assertEquals(3, annotator.nThreads());
    assertEquals(5678, annotator.maxTime());
  }
@Test
  public void testDoOneFailedSentenceDoesNotThrow() {
    Properties props = new Properties();
    SentimentAnnotator annotator = new SentimentAnnotator("sentiment", props);
    Annotation annotation = new Annotation("text");
    CoreMap sentence = new Annotation("sentence");
    annotator.doOneFailedSentence(annotation, sentence); 
  } 
}