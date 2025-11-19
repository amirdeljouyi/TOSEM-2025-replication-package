public class PTBTokenizer_wosr_3_GPTLLMTest { 

 @Test
  public void testNewPTBTokenizerReturnsWordTokens() {
    Reader reader = new StringReader("Hello, world!");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    List<Word> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals(4, tokens.size()); 
    assertEquals("Hello", tokens.get(0).word());
    assertEquals(",", tokens.get(1).word());
    assertEquals("world", tokens.get(2).word());
    assertEquals("!", tokens.get(3).word());
  }
@Test
  public void testCoreLabelNewlineTokenizationEnabled() {
    Reader reader = new StringReader("First line.\nSecond line.");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    boolean foundNewlineToken = false;
    for (CoreLabel token : tokens) {
      if (AbstractTokenizer.NEWLINE_TOKEN.equals(token.word())) {
        foundNewlineToken = true;
        break;
      }
    }

    assertTrue("Expected to find newline token", foundNewlineToken);
  }
@Test
  public void testCoreLabelWithInvertibleOption() {
    Reader reader = new StringReader("Example sentence.");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    CoreLabel token = tokens.get(0);
    assertNotNull(token.get(CoreAnnotations.OriginalTextAnnotation.class));
    assertNotNull(token.get(CoreAnnotations.BeforeAnnotation.class));
    assertNotNull(token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testLegacyConstructorTokenizationWithEscapingSuppressed() {
    Reader reader = new StringReader("(Hello)");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, false, false, true, new CoreLabelTokenFactory());
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("Expected opening parenthesis without normalization", "(", tokens.get(0).word());
    assertEquals("Hello", tokens.get(1).word());
    assertEquals(")", tokens.get(2).word());
  }
@Test
  public void testFactoryReturnsTokenizer() {
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("invertible,true");
    Reader reader = new StringReader("Tokenizer factory test.");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals(4, tokens.size()); 
    assertEquals("Tokenizer", tokens.get(0).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testPtbToken2Text() {
    String text = "-LRB- hello -RRB-";
    String result = PTBTokenizer.ptbToken2Text(text);
    assertEquals("( hello )", result);
  }
@Test
  public void testPtb2TextOnListOfWords() {
    List<String> ptbWords = new ArrayList<>();
    ptbWords.add("hello");
    ptbWords.add(",");
    ptbWords.add("world");
    ptbWords.add("!");

    String result = PTBTokenizer.ptb2Text(ptbWords);
    assertEquals("hello, world!", result);
  }
@Test
  public void testPtb2TextOnLabelList() {
    List<HasWord> tokens = new ArrayList<>();
    tokens.add(new Word("hello"));
    tokens.add(new Word(","));
    tokens.add(new Word("world"));
    tokens.add(new Word("!"));

    String result = PTBTokenizer.labelList2Text(tokens);
    assertEquals("hello, world!", result);
  }
@Test
  public void testSplitHyphenatedOption() {
    Reader reader = new StringReader("well-educated");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("well", tokens.get(0).word());
    assertEquals("-", tokens.get(1).word());
    assertEquals("educated", tokens.get(2).word());
  }
@Test
  public void testSplitForwardSlashOption() {
    Reader reader = new StringReader("and/or");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=true");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("and", tokens.get(0).word());
    assertEquals("/", tokens.get(1).word());
    assertEquals("or", tokens.get(2).word());
  }
@Test
  public void testStrictTreebank3Option() {
    Reader reader = new StringReader("U.K.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictTreebank3=true");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals(2, tokens.size());
    assertEquals("U.K.", tokens.get(0).word());
    assertEquals(".", tokens.get(1).word());
  }
@Test
  public void testTokenizeWithEscapeForwardSlashAsteriskOption() {
    Reader reader = new StringReader("/*comment*/");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "escapeForwardSlashAsterisk=true");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("\\/", tokens.get(0).word());
    assertEquals("\\*", tokens.get(1).word());
  }
@Test
  public void testTokenWithNormalizeCurrency() {
    Reader reader = new StringReader("€100 and £50");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("$", tokens.get(0).word());
    assertEquals("100", tokens.get(1).word());
    assertEquals("and", tokens.get(2).word());
    assertEquals("#", tokens.get(3).word());
    assertEquals("50", tokens.get(4).word());
  }
@Test
  public void testTokenizationOfEllipsesOptionPtb3() {
    Reader reader = new StringReader("Wait...");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("Wait", tokens.get(0).word());
    assertEquals("...", tokens.get(1).word());
  }
@Test
  public void testDashesOptionPtb3() {
    Reader reader = new StringReader("dash -- test");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "dashes=ptb3");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("dash", tokens.get(0).word());
    assertEquals("--", tokens.get(1).word());
    assertEquals("test", tokens.get(2).word());
  }
@Test
  public void testUntokenizableNoneDelete() {
    Reader reader = new StringReader("\u0000abc"); 
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=noneDelete");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    for (CoreLabel token : tokens) {
      assertFalse("Token should not contain null character", token.word().contains("\u0000"));
    }
  }
@Test
  public void testQuoteNormalizationPtb3Latex() {
    Reader reader = new StringReader("\"quote\"");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=latex");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals("``", tokens.get(0).word());
    assertEquals("quote", tokens.get(1).word());
    assertEquals("''", tokens.get(2).word());
  } 
}