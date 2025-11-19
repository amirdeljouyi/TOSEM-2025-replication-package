public class PTBTokenizer_wosr_2_GPTLLMTest { 

 @Test
  public void testSimpleTokenization() {
    String text = "This is a test.";
    Reader reader = new StringReader(text);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    assertTrue(tokenizer.hasNext());
    assertEquals("This", tokenizer.next().word());
    assertTrue(tokenizer.hasNext());
    assertEquals("is", tokenizer.next().word());
    assertTrue(tokenizer.hasNext());
    assertEquals("a", tokenizer.next().word());
    assertTrue(tokenizer.hasNext());
    assertEquals("test", tokenizer.next().word());
    assertTrue(tokenizer.hasNext());
    assertEquals(".", tokenizer.next().word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testCoreLabelTokenizationWithNL() {
    String text = "Hello\nWorld";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

    assertTrue(tokenizer.hasNext());
    assertEquals("Hello", tokenizer.next().word());
    assertTrue(tokenizer.hasNext());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokenizer.next().word());
    assertTrue(tokenizer.hasNext());
    assertEquals("World", tokenizer.next().word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testCoreLabelTokenizationInvertible() {
    String text = "Hello, Stanford!";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

    CoreLabel token1 = tokenizer.next();
    assertEquals("Hello", token1.word());
    assertNotNull(token1.get(CoreAnnotations.TextAnnotation.class));
    assertNotNull(token1.get(CoreAnnotations.OriginalTextAnnotation.class));

    CoreLabel token2 = tokenizer.next();
    assertEquals(",", token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("Stanford", token3.word());

    CoreLabel token4 = tokenizer.next();
    assertEquals("!", token4.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNewlineTokenStaticMethod() {
    String newlineToken = PTBTokenizer.getNewlineToken();
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, newlineToken);
  }
@Test
  public void testPtb2TextRecovery() {
    String ptbText = "-LRB- Hello World ! -RRB-";
    String expected = "(Hello World!)";
    String actual = PTBTokenizer.ptb2Text(ptbText).trim();
    assertEquals(expected, actual);
  }
@Test
  public void testPtbToken2TextSimple() {
    String ptb = "-LRB-";
    String expected = "(";
    assertEquals(expected, PTBTokenizer.ptbToken2Text(ptb));
  }
@Test
  public void testPtb2TextFromList() {
    List<String> ptbWords = Arrays.asList("John", "said", ":", "\"", "Yes", ".", "\"");
    String joined = StringUtils.join(ptbWords);
    String expected = "John said: \"Yes.\"";
    String recovered = PTBTokenizer.ptb2Text(ptbWords);
    assertEquals(expected, recovered);
  }
@Test
  public void testLabelList2Text() {
    List<HasWord> tokens = new ArrayList<>();
    tokens.add(new Word("-LRB-"));
    tokens.add(new Word("Example"));
    tokens.add(new Word("-RRB-"));

    String expected = "(Example)";
    String result = PTBTokenizer.labelList2Text(tokens);
    assertEquals(expected, result);
  }
@Test
  public void testFactoryCreatesWordTokenizer() {
    TokenizerFactory<Word> factory = PTBTokenizer.factory();
    assertNotNull(factory);
    Reader reader = new StringReader("Tokenize me.");
    Tokenizer<Word> tokenizer = factory.getTokenizer(reader);
    assertNotNull(tokenizer);
    assertEquals("Tokenize", tokenizer.next().word());
  }
@Test
  public void testCoreLabelFactoryProducesExpectedTokens() {
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("invertible");
    Reader reader = new StringReader("Test sentence -LRB- with parens -RRB-.");
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(reader);

    CoreLabel token1 = tokenizer.next();
    assertEquals("Test", token1.word());
    CoreLabel token2 = tokenizer.next();
    assertEquals("sentence", token2.word());
    CoreLabel token3 = tokenizer.next();
    assertEquals("(", PTBTokenizer.ptbToken2Text(token3.word()));
  }
@Test
  public void testCoreLabelFactoryDefaultOptions() {
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory();
    assertNotNull(factory);
    Reader reader = new StringReader("Apple's stock rose.");
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(reader);

    CoreLabel first = tokenizer.next();
    assertEquals("Apple", first.word());

    CoreLabel second = tokenizer.next();
    assertEquals("'s", second.word());

    CoreLabel third = tokenizer.next();
    assertEquals("stock", third.word());

    CoreLabel fourth = tokenizer.next();
    assertEquals("rose", fourth.word());

    CoreLabel fifth = tokenizer.next();
    assertEquals(".", fifth.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testPTBTokenizerFactoryGetTokenizerWithExtraOptions() {
    PTBTokenizer.PTBTokenizerFactory<Word> factory = new PTBTokenizer.PTBTokenizerFactory<>(new WordTokenFactory(), "invertible=false");
    assertNotNull(factory);
    Reader reader = new StringReader("Extra option test.");
    Tokenizer<Word> tokenizer = factory.getTokenizer(reader, "americanize=false");

    assertEquals("Extra", tokenizer.next().word());
  }
@Test
  public void testTokenizerFactorySetOptions() {
    PTBTokenizer.PTBTokenizerFactory<Word> factory = new PTBTokenizer.PTBTokenizerFactory<>(new WordTokenFactory(), "");
    factory.setOptions("invertible=true,tokenizeNLs");
    Reader reader = new StringReader("Testing\nupdates");
    Tokenizer<Word> tokenizer = factory.getTokenizer(reader);

    assertEquals("Testing", tokenizer.next().word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokenizer.next().word());
    assertEquals("updates", tokenizer.next().word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEmptyInputReturnsNoTokens() {
    Reader reader = new StringReader("");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUnicodeQuotesNormalization() {
    Reader reader = new StringReader("“quote”");
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(reader, new WordTokenFactory(), "quotes=ascii");
    assertEquals("\"", tokenizer.next().word());
    assertEquals("quote", tokenizer.next().word());
    assertEquals("\"", tokenizer.next().word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEllipsesNormalizationPTB3() {
    Reader reader = new StringReader("Wait... okay.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");
    assertEquals("Wait", tokenizer.next().word());
    assertEquals("...", tokenizer.next().word());
    assertEquals("okay", tokenizer.next().word());
    assertEquals(".", tokenizer.next().word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testDashesNormalizationUnicode() {
    Reader reader = new StringReader("long---dash");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "dashes=ptb3");
    assertEquals("long", tokenizer.next().word());
    assertEquals("--", tokenizer.next().word());
    assertEquals("-", tokenizer.next().word());
    assertEquals("dash", tokenizer.next().word());
    assertFalse(tokenizer.hasNext());
  } 
}