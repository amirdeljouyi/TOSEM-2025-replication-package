public class TokenizerAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testEnglishDefaultConstructor() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    List<CoreLabel> tokens = tokenizerAnnotator.getTokenizer(new StringReader("This is a test.")).tokenize();
    assertEquals(5, tokens.size());
    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("a", tokens.get(2).word());
    assertEquals("test", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
  }
@Test
  public void testEnglishWithVerboseFlag() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(true);
    List<CoreLabel> tokens = tokenizerAnnotator.getTokenizer(new StringReader("Example sentence.")).tokenize();
    assertEquals(3, tokens.size());
    assertEquals("Example", tokens.get(0).word());
    assertEquals("sentence", tokens.get(1).word());
    assertEquals(".", tokens.get(2).word());
  }
@Test
  public void testEnglishWithLanguageString() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(true, "en", null);
    List<CoreLabel> tokens = tokenizerAnnotator.getTokenizer(new StringReader("Language test!")).tokenize();
    assertEquals(3, tokens.size());
    assertEquals("Language", tokens.get(0).word());
    assertEquals("test", tokens.get(1).word());
    assertEquals("!", tokens.get(2).word());
  }
@Test
  public void testAnnotateSimpleSentence() {
    Properties props = new Properties();
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Stanford is great.");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("Stanford", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("great", tokens.get(2).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testWhitespaceTokenizerOption() {
    Properties props = PropertiesUtils.asProperties("tokenize.whitespace", "true");
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Tokenize   based on    whitespace.");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("Tokenize", tokens.get(0).word());
    assertEquals("based", tokens.get(1).word());
    assertEquals("on", tokens.get(2).word());
    assertEquals("whitespace.", tokens.get(3).word());
  }
@Test
  public void testTokensHaveTokenBeginAndEnd() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    List<CoreLabel> tokens = tokenizerAnnotator.getTokenizer(new StringReader("A B C")).tokenize();
    Annotation annotation = new Annotation("A B C");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> annotatedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    for (int i = 0; i < annotatedTokens.size(); i++) {
      assertEquals(i, (int) annotatedTokens.get(i).get(CoreAnnotations.TokenBeginAnnotation.class));
      assertEquals(i + 1, (int) annotatedTokens.get(i).get(CoreAnnotations.TokenEndAnnotation.class));
    }
  }
@Test
  public void testEmptyTextAnnotationResultsInException() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    Annotation annotation = new Annotation("");
    try {
      tokenizerAnnotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
      assertEquals(0, tokens.size());
    } catch (Exception e) {
      fail("Should handle empty text gracefully.");
    }
  }
@Test
  public void testAdjustFinalTokenRemovesTrailingSpace() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    List<CoreLabel> tokens = tokenizerAnnotator.getTokenizer(new StringReader("last.")).tokenize();
    tokens.get(tokens.size() - 1).set(CoreAnnotations.AfterAnnotation.class, " ");
    TokenizerAnnotator.adjustFinalToken(tokens);
    String after = tokens.get(tokens.size() - 1).get(CoreAnnotations.AfterAnnotation.class);
    assertNotEquals(" ", after);
    assertEquals("", after);
  }
@Test
  public void testAdjustFinalTokenWithNoAfterDoesNothing() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    List<CoreLabel> tokens = tokenizerAnnotator.getTokenizer(new StringReader("end")).tokenize();
    TokenizerAnnotator.adjustFinalToken(tokens);
    String after = tokens.get(tokens.size() - 1).get(CoreAnnotations.AfterAnnotation.class);
    assertTrue(after == null || after.isEmpty());
  }
@Test
  public void testInitFactoryWithWhitespaceTokenizer() {
    Properties props = PropertiesUtils.asProperties("tokenize.whitespace", "true");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Whitespace;
    TokenizerFactory<CoreLabel> factory = TokenizerAnnotator.initFactory(type, props, null);
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("abc def\nghi"));
    List<CoreLabel> tokens = tokenizer.tokenize();
    assertEquals(3, tokens.size());
    assertEquals("abc", tokens.get(0).word());
    assertEquals("def", tokens.get(1).word());
    assertEquals("ghi", tokens.get(2).word());
  }
@Test
  public void testTokensHaveIsNewlineAnnotation() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    List<CoreLabel> tokens = tokenizerAnnotator.getTokenizer(new StringReader("foo " + AbstractTokenizer.NEWLINE_TOKEN + " bar")).tokenize();
    Annotation annotation = new Annotation("foo\nbar");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> annotatedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean foundNewline = false;
    for (CoreLabel token : annotatedTokens) {
      if (token.word().equals(AbstractTokenizer.NEWLINE_TOKEN)) {
        assertTrue(token.get(CoreAnnotations.IsNewlineAnnotation.class));
        foundNewline = true;
      }
    }
    assertTrue(foundNewline);
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownLanguageInTokenizerType() {
    Properties props = PropertiesUtils.asProperties("tokenize.language", "unknownLang");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownClassInTokenizerType() {
    Properties props = PropertiesUtils.asProperties("tokenize.class", "UnknownClass");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test
  public void testTokenizerSatisfiesExpectedRequirements() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    Set<Class<? extends CoreAnnotation>> satisfied = tokenizerAnnotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testTokenizerRequiresNothing() {
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    Set<Class<? extends CoreAnnotation>> required = tokenizerAnnotator.requires();
    assertNotNull(required);
    assertTrue(required.isEmpty());
  }
@Test
  public void testPostProcessorReflectionFailureThrowsException() {
    Properties props = PropertiesUtils.asProperties("tokenize.postProcessor", "non.existent.ClassName");
    try {
      new TokenizerAnnotator(false, props);
      fail("Expected RuntimeException due to failed class loading");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("failed with"));
    }
  } 
}