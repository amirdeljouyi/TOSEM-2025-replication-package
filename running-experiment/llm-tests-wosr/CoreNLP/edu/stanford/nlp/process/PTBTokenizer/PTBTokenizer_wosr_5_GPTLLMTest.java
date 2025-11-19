public class PTBTokenizer_wosr_5_GPTLLMTest { 

 @Test
  public void testSimpleWordTokenization() {
    String input = "Hello, world!";
    Reader reader = new StringReader(input);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    assertTrue(tokenizer.hasNext());
    Word first = tokenizer.next();
    assertEquals("Hello", first.word());

    assertTrue(tokenizer.hasNext());
    Word comma = tokenizer.next();
    assertEquals(",", comma.word());

    assertTrue(tokenizer.hasNext());
    Word second = tokenizer.next();
    assertEquals("world", second.word());

    assertTrue(tokenizer.hasNext());
    Word exclamation = tokenizer.next();
    assertEquals("!", exclamation.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testCoreLabelTokenizationWithInvertible() {
    String input = "Hello (again)! ";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

    CoreLabel first = tokenizer.next();
    assertEquals("Hello", first.word());
    assertEquals("Hello", first.get(CoreAnnotations.ValueAnnotation.class));
    assertEquals("Hello", first.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Hello", first.originalText());

    CoreLabel second = tokenizer.next();
    assertEquals("-LRB-", second.word());

    CoreLabel third = tokenizer.next();
    assertEquals("again", third.word());

    CoreLabel fourth = tokenizer.next();
    assertEquals("-RRB-", fourth.word());

    CoreLabel fifth = tokenizer.next();
    assertEquals("!", fifth.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNewlineAsToken() {
    String input = "Hi\nThere";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

    CoreLabel first = tokenizer.next();
    assertEquals("Hi", first.word());

    CoreLabel newline = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), newline.word());

    CoreLabel third = tokenizer.next();
    assertEquals("There", third.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNoPtb3Escaping() {
    String input = "(Hello) & Goodbye";
    Reader reader = new StringReader(input);
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(reader, new WordTokenFactory(), "ptb3Escaping=false");

    Word first = tokenizer.next();
    assertEquals("(", first.word());

    Word second = tokenizer.next();
    assertEquals("Hello", second.word());

    Word third = tokenizer.next();
    assertEquals(")", third.word());

    Word fourth = tokenizer.next();
    assertEquals("&", fourth.word());

    Word fifth = tokenizer.next();
    assertEquals("Goodbye", fifth.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testPtbToken2TextWithBracketConversion() {
    String input = "-LRB- Example -RRB-";
    String result = PTBTokenizer.ptbToken2Text(input);
    assertEquals("( Example )", result);
  }
@Test
  public void testPtb2TextListOfWords() {
    List<String> tokens = new ArrayList<>();
    tokens.add("Hello");
    tokens.add(",");
    tokens.add("-LRB-");
    tokens.add("world");
    tokens.add("-RRB-");
    String result = PTBTokenizer.ptb2Text(tokens);
    assertEquals("Hello, (world)", result);
  }
@Test
  public void testPtb2TextListOfHasWords() {
    List<HasWord> tokens = new ArrayList<>();
    tokens.add(new Word("This"));
    tokens.add(new Word("is"));
    tokens.add(new Word("-LRB-"));
    tokens.add(new Word("a"));
    tokens.add(new Word("-RRB-"));
    tokens.add(new Word("test"));
    String result = PTBTokenizer.labelList2Text(tokens);
    assertEquals("This is (a) test", result);
  }
@Test
  public void testTokenizerFactoryCoreLabelWithOptions() {
    Reader reader = new StringReader("US$ 12.5");
    PTBTokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("normalizeCurrency=true");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel token1 = tokenizer.next();
    assertEquals("US", token1.word());

    CoreLabel token2 = tokenizer.next();
    assertEquals("$", token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("12.5", token3.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerFactoryWordWithCustomOption() {
    Reader reader = new StringReader("\"quoted\" word");
    PTBTokenizerFactory<Word> factory = PTBTokenizer.factory(new WordTokenFactory(), "quotes=ascii");
    PTBTokenizer<Word> tokenizer = (PTBTokenizer<Word>) factory.getTokenizer(reader);

    Word quoteStart = tokenizer.next();
    assertEquals("\"", quoteStart.word());

    Word word = tokenizer.next();
    assertEquals("quoted", word.word());

    Word quoteEnd = tokenizer.next();
    assertEquals("\"", quoteEnd.word());

    Word last = tokenizer.next();
    assertEquals("word", last.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEmptyInputTokenizer() {
    Reader reader = new StringReader("");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerWithEllipsis() {
    Reader reader = new StringReader("Wait...");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");

    CoreLabel token1 = tokenizer.next();
    assertEquals("Wait", token1.word());

    CoreLabel token2 = tokenizer.next();
    assertEquals("...", token2.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNormalizeFractions() {
    Reader reader = new StringReader("½ and ¼");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeFractions=true");

    CoreLabel token1 = tokenizer.next();
    assertEquals("1/2", token1.word());

    CoreLabel token2 = tokenizer.next();
    assertEquals("and", token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("1/4", token3.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSplitForwardSlashOptionOn() {
    Reader reader = new StringReader("black/white");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=true");

    CoreLabel token1 = tokenizer.next();
    assertEquals("black", token1.word());

    CoreLabel token2 = tokenizer.next();
    assertEquals("/", token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("white", token3.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSplitHyphenatedOptionOn() {
    Reader reader = new StringReader("frog-lipped");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

    CoreLabel token1 = tokenizer.next();
    assertEquals("frog", token1.word());

    CoreLabel token2 = tokenizer.next();
    assertEquals("-", token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("lipped", token3.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUntokenizableOptionNoneKeep() {
    Reader reader = new StringReader("valid © invalid");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=noneKeep");

    CoreLabel token1 = tokenizer.next();
    assertEquals("valid", token1.word());

    CoreLabel token2 = tokenizer.next();
    assertEquals("©", token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("invalid", token3.word());

    assertFalse(tokenizer.hasNext());
  } 
}