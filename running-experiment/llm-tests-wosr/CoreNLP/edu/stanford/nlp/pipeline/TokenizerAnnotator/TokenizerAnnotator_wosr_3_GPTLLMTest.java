public class TokenizerAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testEnglishTokenizerAnnotateWithSimpleSentence() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Hello world.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello world.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());

    assertEquals("Hello", tokens.get(0).word());
    assertEquals("world", tokens.get(1).word());
    assertEquals(".", tokens.get(2).word());

    assertEquals((Integer) 0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertFalse(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testWhitespaceTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("A\nB C");
    annotation.set(CoreAnnotations.TextAnnotation.class, "A\nB C");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());

    assertEquals("A", tokens.get(0).word());
    assertEquals("B", tokens.get(1).word());
    assertEquals("C", tokens.get(2).word());

    assertFalse(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testDefaultConstructor() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Annotation annotation = new Annotation("Stanford CoreNLP is great.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Stanford CoreNLP is great.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(6, tokens.size());

    assertEquals("Stanford", tokens.get(0).word());
    assertEquals("CoreNLP", tokens.get(1).word());
    assertEquals("is", tokens.get(2).word());
    assertEquals("great", tokens.get(3).word());
    assertEquals(".", tokens.get(5).word());
  }
@Test
  public void testAdjustFinalTokenRemovesTrailingSpace() {
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    token2.set(CoreAnnotations.AfterAnnotation.class, "  ");

    tokens.add(token1);
    tokens.add(token2);

    TokenizerAnnotator.adjustFinalToken(tokens);

    assertEquals(" ", token1.get(CoreAnnotations.AfterAnnotation.class));
    assertEquals(" ", token2.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test(expected = IllegalArgumentException.class)
  public void testAdjustFinalTokenThrowsExceptionOnNonSpaceFinalChar() {
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.setWord("data");
    token.set(CoreAnnotations.AfterAnnotation.class, "\n");
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);
  }
@Test
  public void testEmptyInputProducesNoTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithoutTextAnnotationThrows() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);
  }
@Test
  public void testMultipleSpacesAreHandledCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("This  is    spaced");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This  is    spaced");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());

    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("spaced", tokens.get(2).word());
  }
@Test
  public void testNewlineAnnotatedProperly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("hello\nworld");
    annotation.set(CoreAnnotations.TextAnnotation.class, "hello\nworld");
    annotator.annotate(annotation);

    List<CoreLabel> labels = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(labels);
    assertTrue(labels.stream().anyMatch(label -> label.get(CoreAnnotations.IsNewlineAnnotation.class)));
  }
@Test
  public void testWhitespaceTokenizerWithEolProperty() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Token1\nToken2");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Token1\nToken2");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());

    boolean newlineFound = false;
    for (CoreLabel label : tokens) {
      if (label.get(CoreAnnotations.IsNewlineAnnotation.class) != null &&
          label.get(CoreAnnotations.IsNewlineAnnotation.class)) {
        newlineFound = true;
      }
    }
    assertTrue(newlineFound);
  }
@Test
  public void testAdjustFinalTokenOnEmptyList() {
    List<CoreLabel> emptyList = new ArrayList<>();
    TokenizerAnnotator.adjustFinalToken(emptyList);
    assertTrue(emptyList.isEmpty());
  }
@Test
  public void testAnnotatorRequirements() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
    assertNotNull(required);
    assertTrue(required.isEmpty());

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertNotNull(satisfied);
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testAnnotationWithCodepointProcessorEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("abc 123");
    annotation.set(CoreAnnotations.TextAnnotation.class, "abc 123");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
    for (CoreLabel token : tokens) {
      assertNotNull(token.get(CoreAnnotations.ValueAnnotation.class));
    }
  }
@Test
  public void testMultipleAnnotationRunsOverwrite() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("This is a sentence.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is a sentence.");
    annotator.annotate(annotation);
    List<CoreLabel> firstRun = annotation.get(CoreAnnotations.TokensAnnotation.class);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Another one.");
    annotator.annotate(annotation);
    List<CoreLabel> secondRun = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotSame(firstRun, secondRun);
    assertEquals("Another", secondRun.get(0).word());
    assertEquals(".", secondRun.get(2).word());
  } 
}