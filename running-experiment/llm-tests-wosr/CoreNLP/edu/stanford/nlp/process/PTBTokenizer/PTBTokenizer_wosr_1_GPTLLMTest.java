public class PTBTokenizer_wosr_1_GPTLLMTest { 

 @Test
  public void testSimpleTokenizationDefaultOptions() {
    Reader reader = new StringReader("John went to the store.");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    assertTrue(tokenizer.hasNext());
    assertEquals("John", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("went", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("to", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("the", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("store", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(".", tokenizer.next().word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizationWithNewlinesAsTokens() {
    String input = "Hello\nWorld\nHere we go.";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);

    assertTrue(tokenizer.hasNext());
    assertEquals("Hello", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("World", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("Here", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("we", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("go", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(".", tokenizer.next().word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizationCustomOptionsNoEscaping() {
    Reader reader = new StringReader("(Testing) brackets & quotes.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

    assertTrue(tokenizer.hasNext());
    assertEquals("(", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("Testing", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(")", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("brackets", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("&", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("quotes", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(".", tokenizer.next().word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testPtbtokFactoryInvertibleOption() {
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("invertible");
    Reader reader = new StringReader("Test this sentence.");
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(reader);

    assertTrue(tokenizer.hasNext());
    CoreLabel first = tokenizer.next();
    assertEquals("Test", first.word());
    assertNotNull(first.get(CoreAnnotations.OriginalTextAnnotation.class));
    assertNotNull(first.get(CoreAnnotations.BeforeAnnotation.class));
    assertNotNull(first.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testNewlineTokenLiteral() {
    String expected = AbstractTokenizer.NEWLINE_TOKEN;
    String actual = PTBTokenizer.getNewlineToken();
    assertEquals(expected, actual);
  }
@Test
  public void testPtbtokToPlainTextConversion() {
    String ptbText = "-LRB- This is a test -RRB- .";
    String restored = PTBTokenizer.ptb2Text(ptbText);
    assertEquals("( This is a test ) .", restored);
  }
@Test
  public void testPtbtokTokenToPlainText() {
    String ptbText = "-RRB-";
    String restored = PTBTokenizer.ptbToken2Text(ptbText);
    assertEquals(")", restored);
  }
@Test
  public void testLabelList2TextRoundTrip() {
    Reader reader = new StringReader("She said , \"Hello !\"");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    assertTrue(tokenizer.hasNext());
    Word w1 = tokenizer.next();
    assertTrue(tokenizer.hasNext());
    Word w2 = tokenizer.next();
    assertTrue(tokenizer.hasNext());
    Word w3 = tokenizer.next();
    assertTrue(tokenizer.hasNext());
    Word w4 = tokenizer.next();
    assertTrue(tokenizer.hasNext());
    Word w5 = tokenizer.next();
    assertTrue(tokenizer.hasNext());
    Word w6 = tokenizer.next();

    List<Word> words = List.of(w1, w2, w3, w4, w5, w6);
    String output = PTBTokenizer.labelList2Text(words);
    assertEquals("She said, \"Hello!\"", output);
  }
@Test
  public void testPtbtokPtbsSingleQuoteConversion() {
    String ptbText = "He said , `` hi '' .";
    String restored = PTBTokenizer.ptb2Text(ptbText);
    assertEquals("He said, \"hi\".", restored);
  }
@Test
  public void testFactoryReturnsCorrectTokenizer() {
    TokenizerFactory<Word> factory = PTBTokenizer.factory();
    Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader("A simple test."));

    assertTrue(tokenizer.hasNext());
    assertEquals("A", tokenizer.next().word());
  }
@Test
  public void testEmptyInputYieldsNoTokens() {
    Reader reader = new StringReader("");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNumericAndPunctuationTokens() {
    Reader reader = new StringReader("100.5 is 50.5% of 201.0 .");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    assertTrue(tokenizer.hasNext());
    assertEquals("100.5", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("is", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("50.5", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("%", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("of", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("201.0", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(".", tokenizer.next().word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testOptionsIncludeTokenizeNLs() {
    Reader reader = new StringReader("A\nB");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true,tokenizeNLs");

    assertTrue(tokenizer.hasNext());
    assertEquals("A", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("B", tokenizer.next().word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSpecialCharactersTokenization() {
    Reader reader = new StringReader("a/b = c*d");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    assertTrue(tokenizer.hasNext());
    assertEquals("a", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("/", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("b", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("=", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("c", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("*", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("d", tokenizer.next().word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUnicodeCharacterProcessing() {
    Reader reader = new StringReader("Café – résumé … voilà !");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

    assertTrue(tokenizer.hasNext());
    assertEquals("Café", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    CoreLabel dashLabel = tokenizer.next();
    assertEquals("–", dashLabel.word());

    assertTrue(tokenizer.hasNext());
    assertEquals("résumé", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    CoreLabel ellipsis = tokenizer.next();
    assertEquals("…", ellipsis.word());

    assertTrue(tokenizer.hasNext());
    assertEquals("voilà", tokenizer.next().word());

    assertTrue(tokenizer.hasNext());
    assertEquals("!", tokenizer.next().word());

    assertFalse(tokenizer.hasNext());
  } 
}