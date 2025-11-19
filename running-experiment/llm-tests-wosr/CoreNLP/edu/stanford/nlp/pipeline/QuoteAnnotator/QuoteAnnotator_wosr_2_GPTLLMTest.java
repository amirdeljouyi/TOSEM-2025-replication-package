public class QuoteAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testAsciiDoubleQuotesOnly() {
    String text = "He said, \"This is a quote.\"";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");
    props.setProperty("quote.asciiQuotes", "true");

    Annotation annotation = new Annotation(text);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token1.set(CoreAnnotations.TextAnnotation.class, "He");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token2.set(CoreAnnotations.TextAnnotation.class, "said");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ",");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token3.set(CoreAnnotations.TextAnnotation.class, ",");

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    token4.set(CoreAnnotations.TextAnnotation.class, "\"");

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    token5.set(CoreAnnotations.TextAnnotation.class, "This");

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    token6.set(CoreAnnotations.TextAnnotation.class, "is");

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    token7.set(CoreAnnotations.TextAnnotation.class, "a");

    CoreLabel token8 = new CoreLabel();
    token8.set(CoreAnnotations.OriginalTextAnnotation.class, "quote");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    token8.set(CoreAnnotations.TextAnnotation.class, "quote");

    CoreLabel token9 = new CoreLabel();
    token9.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 25);
    token9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);
    token9.set(CoreAnnotations.TextAnnotation.class, ".");

    CoreLabel token10 = new CoreLabel();
    token10.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token10.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 26);
    token10.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);
    token10.set(CoreAnnotations.TextAnnotation.class, "\"");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6,
            token7, token8, token9, token10);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    QuoteAnnotator quoteAnnotator = new QuoteAnnotator(props);
    quoteAnnotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap quoteSpan = quotes.get(0);
    assertEquals(text.indexOf("\"This"), (int) quoteSpan.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(text.indexOf(".\"") + 2, (int) quoteSpan.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    assertEquals(0, quoteSpan.get(CoreAnnotations.QuotationIndexAnnotation.class).intValue());
    List<CoreLabel> quoteTokens = quoteSpan.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(quoteTokens);
    assertEquals("This", quoteTokens.get(0).originalText());
  }
@Test
  public void testUnclosedQuotationExtractionEnabled() {
    String text = "She said, \"This is not closed.";
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "She");
    token1.set(CoreAnnotations.TextAnnotation.class, "She");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
    token2.set(CoreAnnotations.TextAnnotation.class, "said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ",");
    token3.set(CoreAnnotations.TextAnnotation.class, ",");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token4.set(CoreAnnotations.TextAnnotation.class, "\"");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    token5.set(CoreAnnotations.TextAnnotation.class, "This");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    QuoteAnnotator quoteAnnotator = new QuoteAnnotator(props);
    quoteAnnotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(0, quotes.size());

    List<CoreMap> unclosed = annotation.get(CoreAnnotations.UnclosedQuotationsAnnotation.class);
    assertNotNull(unclosed);
    assertEquals(1, unclosed.size());

    CoreMap q = unclosed.get(0);
    assertTrue(q.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) < q.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals(0, q.get(CoreAnnotations.QuotationIndexAnnotation.class).intValue());
  }
@Test
  public void testSmartQuotesEnabledPrioritizesUnicode() {
    String text = "He wrote, “This is ‘amazing’!” and left.";
    Properties props = new Properties();
    props.setProperty("quote.smartQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");
    props.setProperty("quote.extractUnclosedQuotes", "true");

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    t1.set(CoreAnnotations.TextAnnotation.class, "He");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "wrote");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    t2.set(CoreAnnotations.TextAnnotation.class, "wrote");

    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, ",");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    t3.set(CoreAnnotations.TextAnnotation.class, ",");

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    QuoteAnnotator quoteAnnotator = new QuoteAnnotator(props);
    quoteAnnotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap mainQuote = quotes.get(0);
    assertTrue(mainQuote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) < mainQuote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    List<CoreMap> innerQuotes = mainQuote.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(innerQuotes);
    assertEquals(1, innerQuotes.size());
  }
@Test
  public void testAsciiSingleQuotesEnabled() {
    String text = "He replied, 'Absolutely not.'";
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");

    List<CoreLabel> tokens = new ArrayList<>();
    int idx = 0;
    for (String word : new String[]{"He", "replied", ",", "'", "Absolutely", "not", ".", "'"}) {
      CoreLabel token = new CoreLabel();
      token.set(CoreAnnotations.OriginalTextAnnotation.class, word);
      token.set(CoreAnnotations.TextAnnotation.class, word);
      token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, idx);
      idx += word.length();
      token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, idx);
      idx++; 
      tokens.add(token);
    }

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation sentence = new Annotation(text);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    QuoteAnnotator quoteAnnotator = new QuoteAnnotator(props);
    quoteAnnotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());
    CoreMap quote = quotes.get(0);
    assertTrue(quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class) - quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) > 1);
  } 
}