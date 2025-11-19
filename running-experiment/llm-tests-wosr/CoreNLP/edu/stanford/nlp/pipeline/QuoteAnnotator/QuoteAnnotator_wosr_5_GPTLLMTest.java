public class QuoteAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testConstructor_DefaultProperties() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    assertFalse(annotator.USE_SINGLE);
    assertEquals(-1, annotator.MAX_LENGTH);
    assertFalse(annotator.ASCII_QUOTES);
    assertFalse(annotator.ALLOW_EMBEDDED_SAME);
    assertFalse(annotator.SMART_QUOTES);
    assertFalse(annotator.EXTRACT_UNCLOSED);
    assertTrue(annotator.ATTRIBUTE_QUOTES);
    assertNotNull(annotator.quoteAttributionAnnotator);
  }
@Test
  public void testConstructor_CustomProperties() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.maxLength", "50");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    assertTrue(annotator.USE_SINGLE);
    assertEquals(50, annotator.MAX_LENGTH);
    assertTrue(annotator.ASCII_QUOTES);
    assertTrue(annotator.ALLOW_EMBEDDED_SAME);
    assertTrue(annotator.SMART_QUOTES);
    assertTrue(annotator.EXTRACT_UNCLOSED);
    assertFalse(annotator.ATTRIBUTE_QUOTES);
    assertNull(annotator.quoteAttributionAnnotator);
  }
@Test
  public void testReplaceUnicode() {
    String unicode = "\u201Ctest\u201D \u2018single\u2019";
    String replaced = QuoteAnnotator.replaceUnicode(unicode);
    assertTrue(replaced.contains("\"test\""));
    assertTrue(replaced.contains("'single'"));
  }
@Test
  public void testMakeQuote() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("Hello");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setOriginalText("World");
    token2.setBeginPosition(6);
    token2.setEndPosition(11);
    tokens.add(token2);

    Annotation quote = QuoteAnnotator.makeQuote("Hello World", 0, 11, tokens, 0, 0, 0, "doc123");

    assertEquals((Integer)0, quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals((Integer)11, quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("doc123", quote.get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(2, quote.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals((Integer)0, quote.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer)1, quote.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals((Integer)0, quote.get(CoreAnnotations.SentenceBeginAnnotation.class));
    assertEquals((Integer)0, quote.get(CoreAnnotations.SentenceEndAnnotation.class));
  }
@Test
  public void testGetQuotes_SimpleAscii() {
    Properties props = new Properties();
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("John said, \"Hello world.\"");

    assertEquals(1, result.first().size());
    Pair<Integer, Integer> quoted = result.first().get(0);
    assertEquals(Integer.valueOf(11), quoted.first());
    assertEquals(Integer.valueOf(25), quoted.second());
  }
@Test
  public void testGetQuotes_UnclosedQuote_ExtractFalse() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "false");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("He began to say \"this will never end");

    assertEquals(0, result.first().size());
    assertEquals(0, result.second().size());
  }
@Test
  public void testGetQuotes_UnclosedQuote_ExtractTrue() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("She said, \"this is quoted but unclosed");

    assertEquals(0, result.first().size());
    assertEquals(1, result.second().size());
    assertEquals(Integer.valueOf(10), result.second().get(0).first());
  }
@Test
  public void testRecursiveQuotes_EmbeddedQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.attributeQuotes", "false");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result =
        annotator.getQuotes("\"He said, 'Hi there!' and left.\"");

    assertEquals(2, result.first().size());
    assertEquals((Integer)0, result.first().get(0).first());
    assertEquals((Integer)32, result.first().get(0).second());
    assertEquals((Integer)10, result.first().get(1).first());
    assertEquals((Integer)21, result.first().get(1).second());
  }
@Test
  public void testXmlFreeText_RemovesNonTokenText() {
    Annotation annotation = new Annotation("Some <xml>text</xml>");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("text");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    tokens.add(token1);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    String result = QuoteAnnotator.xmlFreeText("Some <xml>text</xml>", annotation);

    assertEquals("            text     ", result);
  }
@Test
  public void testGetQuoteComparator_SortsByBeginOffset() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(token1);
    quotes.add(token2);

    quotes.sort(QuoteAnnotator.getQuoteComparator());

    assertEquals((Integer)5, quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals((Integer)10, quotes.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
  }
@Test
  public void testIsWhitespaceOrPunct() {
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct(" "));
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct("!"));
    assertFalse(QuoteAnnotator.isWhitespaceOrPunct("a"));
  }
@Test
  public void testIsSingleQuote() {
    assertTrue(QuoteAnnotator.isSingleQuote("'"));
    assertFalse(QuoteAnnotator.isSingleQuote("\""));
  }
@Test
  public void testGatherQuotes_WithEmbeddedQuotes() {
    Annotation topQuote = new Annotation("\"Hello 'world'!\"");
    topQuote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    topQuote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    Annotation embedded = new Annotation("'world'");
    embedded.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    embedded.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    topQuote.set(CoreAnnotations.QuotationsAnnotation.class, Arrays.asList(embedded));

    List<CoreMap> result = QuoteAnnotator.gatherQuotes(topQuote);

    assertEquals(1, result.size());
    assertEquals("'world'", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  } 
}