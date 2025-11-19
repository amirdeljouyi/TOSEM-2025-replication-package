public class QuoteAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testAsciiQuoteExtractionWithDoubleQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.asciiQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "He said, \"This is a quote\".";

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ",");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

    CoreLabel token8 = new CoreLabel();
    token8.set(CoreAnnotations.OriginalTextAnnotation.class, "quote");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);

    CoreLabel token9 = new CoreLabel();
    token9.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 25);
    token9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);

    CoreLabel token10 = new CoreLabel();
    token10.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token10.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 26);
    token10.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

    List<CoreLabel> tokens = Arrays.asList(
        token1, token2, token3, token4, token5,
        token6, token7, token8, token9, token10
    );

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc1");

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(1, quotes.size());

    CoreMap quote = quotes.get(0);
    assertEquals("\"This is a quote\"", text.substring(
        quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
        quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)
    ));
  }
@Test
  public void testQuoteAnnotatorWithSingleQuotesDisabled() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "false");
    props.setProperty("quote.asciiQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);

    String text = "'This should not be recognized as a quote'.";

    CoreLabel qt1 = new CoreLabel();
    qt1.set(CoreAnnotations.OriginalTextAnnotation.class, "'");
    qt1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    qt1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreLabel qt2 = new CoreLabel();
    qt2.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    qt2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    qt2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel qt3 = new CoreLabel();
    qt3.set(CoreAnnotations.OriginalTextAnnotation.class, "should");
    qt3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    qt3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel qt4 = new CoreLabel();
    qt4.set(CoreAnnotations.OriginalTextAnnotation.class, "not");
    qt4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    qt4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreLabel qt5 = new CoreLabel();
    qt5.set(CoreAnnotations.OriginalTextAnnotation.class, "be");
    qt5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    qt5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

    CoreLabel qt6 = new CoreLabel();
    qt6.set(CoreAnnotations.OriginalTextAnnotation.class, "recognized");
    qt6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    qt6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);

    CoreLabel qt7 = new CoreLabel();
    qt7.set(CoreAnnotations.OriginalTextAnnotation.class, "as");
    qt7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 31);
    qt7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);

    CoreLabel qt8 = new CoreLabel();
    qt8.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
    qt8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 34);
    qt8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 35);

    CoreLabel qt9 = new CoreLabel();
    qt9.set(CoreAnnotations.OriginalTextAnnotation.class, "quote");
    qt9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 36);
    qt9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 41);

    CoreLabel qt10 = new CoreLabel();
    qt10.set(CoreAnnotations.OriginalTextAnnotation.class, "'");
    qt10.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 41);
    qt10.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 42);

    CoreLabel qt11 = new CoreLabel();
    qt11.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    qt11.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 42);
    qt11.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 43);

    List<CoreLabel> tokens = Arrays.asList(qt1, qt2, qt3, qt4, qt5, qt6, qt7, qt8, qt9, qt10, qt11);

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 43);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc2");

    annotator.annotate(annotation);

    List<CoreMap> quotes = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
    assertNotNull(quotes);
    assertEquals(0, quotes.size());
  }
@Test
  public void testReplaceUnicodeQuotes() {
    String raw = "He said, “Hello world.”";
    String converted = QuoteAnnotator.replaceUnicode(raw);
    assertTrue(converted.contains("\"Hello world.\""));
  }
@Test
  public void testDirectedQuotesMapUnmodifiable() {
    try {
      QuoteAnnotator.DIRECTED_QUOTES.put("A", "B");
      fail("DIRECTED_QUOTES map should be unmodifiable");
    } catch (UnsupportedOperationException e) {
      
    }
  }
@Test
  public void testConstructorWithMixedProperties() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    props.setProperty("quote.maxLength", "512");
    props.setProperty("quote.asciiQuotes", "true");
    props.setProperty("quote.allowEmbeddedSame", "true");
    props.setProperty("quote.extractUnclosedQuotes", "true");
    props.setProperty("quote.attributeQuotes", "false");

    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);

    assertTrue(annotator.USE_SINGLE);
    assertEquals(512, annotator.MAX_LENGTH);
    assertTrue(annotator.ASCII_QUOTES);
    assertTrue(annotator.ALLOW_EMBEDDED_SAME);
    assertTrue(annotator.EXTRACT_UNCLOSED);
    assertFalse(annotator.ATTRIBUTE_QUOTES);
    assertNull(annotator.quoteAttributionAnnotator);
  } 
}