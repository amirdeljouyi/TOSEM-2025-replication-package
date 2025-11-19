public class QuoteAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testAsciiQuoteExtractionSimple() {
    Properties props = new Properties();
    props.setProperty("quote.asciiQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "He said, \"Hello world.\"";
    Annotation annotation = new Annotation(text);
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "He");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "said");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, ",");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ",");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "\"");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    tokens.add(token4);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    tokens.add(token5);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.TextAnnotation.class, "world");
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "world");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
    tokens.add(token6);

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.TextAnnotation.class, ".");
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 21);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);
    tokens.add(token7);

    CoreLabel token8 = new CoreLabel();
    token8.set(CoreAnnotations.TextAnnotation.class, "\"");
    token8.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
    tokens.add(token8);

    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    Assert.assertNotNull(quotes);
    Assert.assertEquals(1, quotes.size());

    CoreMap quote = quotes.get(0);
    Assert.assertEquals(9, (int) quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    Assert.assertEquals(23, (int) quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    Assert.assertEquals("\"Hello world.\"", quote.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testSmartUnicodeQuoteRecognition() {
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "He said, “Hello again.”";
    Annotation annotation = new Annotation(text);
    List<CoreLabel> tokens = new ArrayList<>();

    int pos = 0;
    for (String word : Arrays.asList("He", "said", ",", "“", "Hello", "again", ".", "”")) {
      CoreLabel token = new CoreLabel();
      token.set(CoreAnnotations.OriginalTextAnnotation.class, word);
      token.set(CoreAnnotations.TextAnnotation.class, word);
      token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, pos);
      pos += word.length();
      token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, pos);
      tokens.add(token);
      pos += 1;
    }

    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    Assert.assertNotNull(quotes);
    Assert.assertEquals(1, quotes.size());

    CoreMap quote = quotes.get(0);
    String expectedQuote = "“Hello again.”";
    Assert.assertEquals(expectedQuote, quote.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testUnclosedQuoteExtractionEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "He said, \"This is a test.";
    List<CoreLabel> tokens = new ArrayList<>();

    int start = 0;
    for (String word : Arrays.asList("He", "said", ",", "\"", "This", "is", "a", "test", ".")) {
      CoreLabel tok = new CoreLabel();
      tok.set(CoreAnnotations.TextAnnotation.class, word);
      tok.set(CoreAnnotations.OriginalTextAnnotation.class, word);
      tok.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, start);
      start += word.length();
      tok.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, start);
      tokens.add(tok);
      start += 1;
    }

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    Assert.assertNotNull(unclosed);
    Assert.assertTrue(unclosed.size() > 0);
    for (CoreMap uq : unclosed) {
      int begin = uq.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
      int end = uq.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
      Assert.assertTrue(end > begin);
      String substr = text.substring(begin, end);
      Assert.assertTrue(substr.startsWith("\""));
    }
  }
@Test
  public void testSingleQuoteConfigTrue() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "'Quoted text' is useful.";
    List<CoreLabel> tokens = new ArrayList<>();
    int offset = 0;

    for (String word : Arrays.asList("'", "Quoted", "text", "'", "is", "useful", ".")) {
      CoreLabel tok = new CoreLabel();
      tok.set(CoreAnnotations.TextAnnotation.class, word);
      tok.set(CoreAnnotations.OriginalTextAnnotation.class, word);
      tok.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, offset);
      offset += word.length();
      tok.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, offset);
      tokens.add(tok);
      offset += 1;
    }

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    Assert.assertNotNull(quotes);
    Assert.assertEquals(1, quotes.size());
    CoreMap quote = quotes.get(0);
    Assert.assertEquals("'Quoted text'", quote.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNoQuotesFound() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "This is a sentence without any quotes.";
    List<CoreLabel> tokens = new ArrayList<>();
    int offset = 0;
    for (String word : Arrays.asList("This", "is", "a", "sentence", "without", "any", "quotes", ".")) {
      CoreLabel tok = new CoreLabel();
      tok.set(CoreAnnotations.TextAnnotation.class, word);
      tok.set(CoreAnnotations.OriginalTextAnnotation.class, word);
      tok.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, offset);
      offset += word.length();
      tok.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, offset);
      tokens.add(tok);
      offset += 1;
    }

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    Assert.assertTrue(quotes == null || quotes.isEmpty());
  }
@Test
  public void testAsciiQuotesReplaceUnicode() {
    String input = "“Hello”, he said.";
    String replaced = QuoteAnnotator.replaceUnicode(input);
    Assert.assertFalse(replaced.contains("“"));
    Assert.assertFalse(replaced.contains("”"));
    Assert.assertTrue(replaced.contains("\"Hello\""));
  }
@Test
  public void testDirectedQuoteRecognitionWithEmbeddingDisabled() {
    Properties props = new Properties();
    props.setProperty("quote.allowEmbeddedSame", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    String text = "他说：「她说：『你好』。」";

    List<CoreLabel> tokens = new ArrayList<>();
    int offset = 0;
    for (String word : Arrays.asList("他", "说", "：", "「", "她", "说", "：", "『", "你", "好", "』", "。", "」")) {
      CoreLabel tok = new CoreLabel();
      tok.set(CoreAnnotations.TextAnnotation.class, word);
      tok.set(CoreAnnotations.OriginalTextAnnotation.class, word);
      tok.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, offset);
      offset += word.length();
      tok.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, offset);
      tokens.add(tok);
    }

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    Assert.assertNotNull(quotes);
    Assert.assertEquals(1, quotes.size());
    CoreMap quote = quotes.get(0);
    Assert.assertEquals("「她说：『你好』。」", quote.get(CoreAnnotations.TextAnnotation.class));
  } 
}