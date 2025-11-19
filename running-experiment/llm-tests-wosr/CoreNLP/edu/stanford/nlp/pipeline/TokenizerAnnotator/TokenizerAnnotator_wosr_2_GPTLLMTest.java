public class TokenizerAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testDefaultEnglishTokenization() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("This is a test.");

    annotation.set(CoreAnnotations.TextAnnotation.class, "This is a test.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("a", tokens.get(2).word());
    assertEquals("test", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());

    assertEquals((Integer) 0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testWhitespaceTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    annotation.set(CoreAnnotations.TextAnnotation.class, "This is another test");
    Annotation annotation = new Annotation("This is another test");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is another test");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("another", tokens.get(2).word());
    assertEquals("test", tokens.get(3).word());
  }
@Test
  public void testFrenchTokenizerExplicit() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Bonjour le monde.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Bonjour le monde.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("Bonjour", tokens.get(0).word());
    assertEquals("le", tokens.get(1).word());
    assertEquals("monde", tokens.get(2).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testSpanishTokenizerExplicit() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Hola mundo.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hola mundo.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("Hola", tokens.get(0).word());
    assertEquals("mundo", tokens.get(1).word());
    assertEquals(".", tokens.get(2).word());
  }
@Test
  public void testAdjustFinalTokenRemovesTrailingSpace() {
    CoreLabel label1 = new CoreLabel();
    label1.setWord("Hello");
    label1.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel label2 = new CoreLabel();
    label2.setWord("world");
    label2.set(CoreAnnotations.AfterAnnotation.class, "  ");

    List<CoreLabel> tokens = List.of(label1, label2);
    TokenizerAnnotator.adjustFinalToken(tokens);

    assertEquals("world", tokens.get(1).word());
    assertEquals(" ", tokens.get(1).get(CoreAnnotations.AfterAnnotation.class));
  }
@Test(expected = IllegalArgumentException.class)
  public void testAdjustFinalTokenThrowsOnInvalidTrailingChar() {
    CoreLabel label = new CoreLabel();
    label.setWord("word");
    label.set(CoreAnnotations.AfterAnnotation.class, "\t"); 

    List<CoreLabel> tokens = List.of(label);
    TokenizerAnnotator.adjustFinalToken(tokens);
  }
@Test
  public void testWhitespaceCharacterTokenizationWithEndOfLine() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Hello\nworld");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello\nworld");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("Hello", tokens.get(0).word());
    assertEquals("world", tokens.get(1).word());
  }
@Test
  public void testPostProcessorIntegrationWithCodepoint() {
    Properties props = new Properties();
    props.setProperty("tokenize.codepoint", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Hi.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hi.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testVerboseLoggingFlagTrue() {
    Properties props = new Properties();
    props.setProperty("tokenize.verbose", "true");
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Logging test.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Logging test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
  }
@Test
  public void testUnspecifiedTokenizerDefaultsToEnglish() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx"); 

    boolean caught = false;
    try {
      new TokenizerAnnotator(props);
    } catch (IllegalArgumentException e) {
      caught = true;
    }

    assertTrue(caught);
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownTokenizerClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "UnknownTokenizer");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test
  public void testEmptyTextInputAnnotation() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
  }
@Test(expected = RuntimeException.class)
  public void testMissingTextAnnotationThrowsException() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("This will fail");
    annotator.annotate(annotation); 
  }
@Test
  public void testSentenceAnnotationRemoved() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Hello world.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello world.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new Object()); 

    annotator.annotate(annotation);
    assertFalse(annotation.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testNewlineTokenSetIsNewlineAnnotation() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord(AbstractTokenizer.NEWLINE_TOKEN);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("word");

    List<CoreLabel> tokens = List.of(token1, token2);
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    try {
      java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod("setNewlineStatus", List.class);
      method.setAccessible(true);
      method.invoke(null, tokens);
    } catch (Exception e) {
      fail("Reflection failed during test");
    }

    assertTrue(token1.get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(token2.get(CoreAnnotations.IsNewlineAnnotation.class));
  } 
}