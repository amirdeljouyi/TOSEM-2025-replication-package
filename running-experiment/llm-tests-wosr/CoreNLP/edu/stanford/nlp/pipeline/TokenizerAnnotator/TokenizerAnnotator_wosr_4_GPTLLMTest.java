public class TokenizerAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testEnglishDefaultTokenizerCreatesTokens() {
    Annotation annotation = new Annotation("The quick brown fox.");
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("The", tokens.get(0).word());
    assertEquals("quick", tokens.get(1).word());
    assertEquals("brown", tokens.get(2).word());
    assertEquals("fox", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
  }
@Test
  public void testWhitespaceTokenizerCreatesTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Hello Stanford CoreNLP");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("Hello", tokens.get(0).word());
    assertEquals("Stanford", tokens.get(1).word());
    assertEquals("CoreNLP", tokens.get(2).word());
  }
@Test
  public void testSpanishTokenizerCreatesTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("¡Hola amigo!");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3); 
  }
@Test
  public void testFrenchTokenizerCreatesTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr");
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("C'est la vie.");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 4);
  }
@Test
  public void testInvalidTokenizerClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "UnknownTokenizer");
    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected IllegalArgumentException due to unknown tokenizer class");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.class property"));
    }
  }
@Test
  public void testInvalidTokenizerLanguageThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx");
    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected IllegalArgumentException due to unknown tokenizer language");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language property"));
    }
  }
@Test
  public void testAdjustFinalTokenModifiesTrailingSpace() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    token2.set(CoreAnnotations.AfterAnnotation.class, " ");
    List<CoreLabel> tokens = java.util.Arrays.asList(token1, token2);
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", token2.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAdjustFinalTokenWithNoAfterAnnotationDoesNothing() {
    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    List<CoreLabel> tokens = java.util.Collections.singletonList(token);
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertNull(token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testEmptyInputReturnsEmptyTokens() {
    Annotation annotation = new Annotation("");
    TokenizerAnnotator tokenizer = new TokenizerAnnotator();
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testTokenBeginAndEndAreSetCorrectly() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hi");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("there");
    List<CoreLabel> tokens = java.util.Arrays.asList(token1, token2);
    edu.stanford.nlp.pipeline.TokenizerAnnotator.setTokenBeginTokenEnd(tokens);
    assertEquals(0, (int) token1.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(1, (int) token1.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(1, (int) token2.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(2, (int) token2.get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testSetNewlineStatusDetectsNewlineToken() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("hi");
    List<CoreLabel> tokens = java.util.Arrays.asList(token1, token2);
    edu.stanford.nlp.pipeline.TokenizerAnnotator.setNewlineStatus(tokens);
    assertTrue(token1.get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(token2.get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testInitFactoryWithEnglish() {
    Properties props = new Properties();
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.English;
    TokenizerFactory<CoreLabel> factory = edu.stanford.nlp.pipeline.TokenizerAnnotator.initFactory(type, props, null);
    assertNotNull(factory);
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("Hello."));
    List<CoreLabel> tokens = tokenizer.tokenize();
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testInitFactoryWithWhitespace() {
    Properties props = new Properties();
    props.setProperty("tokenize.keepeol", "true");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Whitespace;
    TokenizerFactory<CoreLabel> factory = edu.stanford.nlp.pipeline.TokenizerAnnotator.initFactory(type, props, null);
    assertNotNull(factory);
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("TokenA\nTokenB"));
    List<CoreLabel> tokens = tokenizer.tokenize();
    assertEquals(2, tokens.size());
  }
@Test
  public void testUnspecifiedTokenizerDefaultsToPTB() {
    Properties props = new Properties();
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Unspecified;
    TokenizerFactory<CoreLabel> factory = edu.stanford.nlp.pipeline.TokenizerAnnotator.initFactory(type, props, null);
    assertNotNull(factory);
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("word1 word2."));
    List<CoreLabel> tokens = tokenizer.tokenize();
    assertEquals(3, tokens.size());
  }
@Test
  public void testAnnotationWithoutTextThrowsException() {
    Annotation annotation = new Annotation();
    TokenizerAnnotator tokenizer = new TokenizerAnnotator();
    try {
      tokenizer.annotate(annotation);
      fail("Should throw RuntimeException due to missing text");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Tokenizer unable to find text in annotation"));
    }
  } 
}