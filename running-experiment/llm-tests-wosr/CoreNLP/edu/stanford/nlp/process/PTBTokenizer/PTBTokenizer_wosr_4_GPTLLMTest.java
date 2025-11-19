public class PTBTokenizer_wosr_4_GPTLLMTest { 

 @Test
  public void testBasicWordTokenization() {
    StringReader reader = new StringReader("This is a test.");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    List<Word> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals(5, tokens.size());
    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("a", tokens.get(2).word());
    assertEquals("test", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
  }
@Test
  public void testCoreLabelBasicTokenization() {
    StringReader reader = new StringReader("U.S.A. is an abbreviation.");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals(6, tokens.size());
    assertEquals("U.S.A.", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("an", tokens.get(2).word());
    assertEquals("abbreviation", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
    assertEquals(null, tokens.get(5).get(CoreAnnotations.OriginalTextAnnotation.class));
  }
@Test
  public void testNewlineTokenizationEnabled() {
    String input = "Line one.\nLine two.";
    StringReader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertTrue(tokens.contains(AbstractTokenizer.NEWLINE_TOKEN));
    assertEquals(6, tokens.size());
    assertEquals("Line", tokens.get(0));
    assertEquals("one", tokens.get(1));
    assertEquals(".", tokens.get(2));
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(3));
    assertEquals("Line", tokens.get(4));
    assertEquals("two", tokens.get(5));
  }
@Test
  public void testNewlineTokenizationDisabled() {
    String input = "Line one.\nLine two.";
    StringReader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertFalse(tokens.contains(AbstractTokenizer.NEWLINE_TOKEN));
    assertEquals(5, tokens.size());
    assertEquals("Line", tokens.get(0));
    assertEquals("one", tokens.get(1));
    assertEquals(".", tokens.get(2));
    assertEquals("Line", tokens.get(3));
    assertEquals("two", tokens.get(4));
  }
@Test
  public void testTokenizerWithNoPTBTransformations() {
    StringReader reader = new StringReader("(Hello) & Goodbye ©");
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(reader, new WordTokenFactory(), "ptb3Escaping=false");
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertEquals(5, tokens.size());
    assertEquals("(", tokens.get(0));
    assertEquals("Hello", tokens.get(1));
    assertEquals(")", tokens.get(2));
    assertEquals("&", tokens.get(3));
    assertEquals("Goodbye", tokens.get(4));
  }
@Test
  public void testPTBTokenToTextConversion() {
    String escaped = "-LRB- test -RRB-";
    String result = PTBTokenizer.ptb2Text(escaped);
    assertEquals("(test)", result);
  }
@Test
  public void testPTBTokenToTextListConversion() {
    List<String> tokens = new ArrayList<>();
    tokens.add("The");
    tokens.add("-LRB-");
    tokens.add("test");
    tokens.add("-RRB-");
    tokens.add("case");
    String converted = PTBTokenizer.ptb2Text(tokens);
    assertEquals("The (test)case", converted);
  }
@Test
  public void testLabelList2TextConversion() {
    List<HasWord> words = new ArrayList<>();
    words.add(new Word("The"));
    words.add(new Word("-LRB-"));
    words.add(new Word("example"));
    words.add(new Word("-RRB-"));
    String text = PTBTokenizer.labelList2Text(words);
    assertEquals("The (example)", text);
  }
@Test
  public void testWordTokenizerFactory() {
    PTBTokenizer.PTBTokenizerFactory<Word> factory = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory();
    PTBTokenizer<Word> tokenizer = (PTBTokenizer<Word>) factory.getTokenizer(new StringReader("Tokenize this line."));
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertEquals(4, tokens.size());
    assertEquals("Tokenize", tokens.get(0));
    assertEquals("this", tokens.get(1));
    assertEquals("line", tokens.get(2));
    assertEquals(".", tokens.get(3));
  }
@Test
  public void testCoreLabelFactoryWithOptions() {
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("invertible");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(new StringReader("Test."));
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertEquals(2, tokens.size());
    assertEquals("Test", tokens.get(0));
    assertEquals(".", tokens.get(1));
  }
@Test
  public void testTokenizingQuotationMarks() {
    String input = "\"Hello, world!\"";
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(new StringReader(input), new CoreLabelTokenFactory(), "quotes=ascii");
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertEquals(5, tokens.size());
    assertEquals("\"", tokens.get(0));
    assertEquals("Hello", tokens.get(1));
    assertEquals(",", tokens.get(2));
    assertEquals("world", tokens.get(3));
    assertEquals("!", tokens.get(4));
  }
@Test
  public void testHandlingEllipsesTransformation() {
    StringReader reader = new StringReader("Wait... what?");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");
    List<CoreLabel> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next());
    }

    assertEquals(4, tokens.size());
    assertEquals("Wait", tokens.get(0).word());
    assertEquals("...", tokens.get(1).word());
    assertEquals("what", tokens.get(2).word());
    assertEquals("?", tokens.get(3).word());
  }
@Test
  public void testDashesTransformationWithOption() {
    String input = "Well--this is awkward.";
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(new StringReader(input), new CoreLabelTokenFactory(), "dashes=ptb3");
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertEquals(5, tokens.size());
    assertEquals("Well", tokens.get(0));
    assertEquals("--", tokens.get(1));
    assertEquals("this", tokens.get(2));
    assertEquals("is", tokens.get(3));
    assertEquals("awkward", tokens.get(4));
  }
@Test
  public void testEscapeForwardSlashAsteriskOption() {
    String input = "/ * token";
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(new StringReader(input), new WordTokenFactory(), "escapeForwardSlashAsterisk=true");
    List<String> tokens = new ArrayList<>();
    while (tokenizer.hasNext()) {
      tokens.add(tokenizer.next().word());
    }

    assertEquals(3, tokens.size());
    assertEquals("\\/", tokens.get(0));
    assertEquals("\\*", tokens.get(1));
    assertEquals("token", tokens.get(2));
  } 
}