public class TokenizerAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testEnglishTokenizerBasicSentence() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("The quick brown fox jumps over the lazy dog.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(10, tokens.size()); 
    assertEquals("The", tokens.get(0).word());
    assertEquals(".", tokens.get(9).word());
  }
@Test
  public void testEnglishTokenizerHandlesWhitespace() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("This   sentence\tcontains\nwhitespace.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("This", tokens.get(0).word());
    assertEquals("whitespace", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
  }
@Test
  public void testAdjustFinalTokenTrimsTrailingSpace() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.set(CoreAnnotations.AfterAnnotation.class, "  ");

    List<CoreLabel> tokens = java.util.Arrays.asList(token1, token2);
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals(" ", token1.get(CoreAnnotations.AfterAnnotation.class));
    assertEquals(" ", token2.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test(expected = IllegalArgumentException.class)
  public void testAdjustFinalTokenInvalidEndingCharacter() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord(".");
    token1.set(CoreAnnotations.AfterAnnotation.class, "\t");

    List<CoreLabel> tokens = java.util.Arrays.asList(token1);
    TokenizerAnnotator.adjustFinalToken(tokens);
  }
@Test
  public void testWhitespaceTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("one two  three\nfour");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("one", tokens.get(0).word());
    assertEquals("four", tokens.get(3).word());
  }
@Test
  public void testUnknownTokenizerLanguageFallbackToDefault() {
    Properties props = new Properties();
    TokenizerAnnotator.TokenizerType defaultType = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertNotNull(defaultType);
    assertEquals(TokenizerAnnotator.TokenizerType.English, defaultType);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidTokenizerClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "InvalidTokenizer");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidTokenizerLanguageThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test
  public void testFrenchTokenizerInitializesCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    assertNotNull(annotator.getTokenizer(new StringReader("")));
  }
@Test
  public void testSpanishTokenizerInitializesCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    assertNotNull(annotator.getTokenizer(new StringReader("")));
  }
@Test
  public void testTokenBeginAndEndAnnotations() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("token1");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("token2");

    List<CoreLabel> tokens = java.util.Arrays.asList(token1, token2);
    TokenizerAnnotator.adjustFinalToken(tokens);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    annotator.annotate(annotation);

    assertEquals((Integer)0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer)1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals((Integer)1, tokens.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer)2, tokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testSetNewlineStatusWithNewline() {
    CoreLabel newlineToken = new CoreLabel();
    newlineToken.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    CoreLabel normalToken = new CoreLabel();
    normalToken.setWord("Hello");

    List<CoreLabel> tokens = java.util.Arrays.asList(newlineToken, normalToken);
    
    TokenizerAnnotator.adjustFinalToken(tokens);
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, AbstractTokenizer.NEWLINE_TOKEN + " Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    TokenizerAnnotator annotator = new TokenizerAnnotator();
    annotator.annotate(annotation);

    assertTrue(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testEnglishTokenizerWithPunctuation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Hi! How are you? Great.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(9, tokens.size());
    assertEquals("Hi", tokens.get(0).word());
    assertEquals("!", tokens.get(1).word());
    assertEquals("Great", tokens.get(7).word());
    assertEquals(".", tokens.get(8).word());
  } 
}