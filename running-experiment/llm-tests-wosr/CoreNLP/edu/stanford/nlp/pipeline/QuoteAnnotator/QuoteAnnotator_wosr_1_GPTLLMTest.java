public class QuoteAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testDefaultConstructorWithProperties() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    assertFalse(annotator.USE_SINGLE);
    assertEquals(-1, annotator.MAX_LENGTH);
    assertFalse(annotator.ASCII_QUOTES);
    assertFalse(annotator.SMART_QUOTES);
    assertTrue(annotator.ATTRIBUTE_QUOTES);
    assertNotNull(annotator.quoteAttributionAnnotator);
  }
@Test
  public void testConstructorWithSingleQuotesEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.singleQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
    assertTrue(annotator.USE_SINGLE);
  }
@Test
  public void testConstructorWithAsciiQuotesEnabled() {
    Properties props = new Properties();
    props.setProperty("quote.asciiQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props, false);
    assertTrue(annotator.ASCII_QUOTES);
  }
@Test
  public void testGetQuotesWithSimpleDoubleQuotes() {
    QuoteAnnotator annotator = new QuoteAnnotator(new Properties());
    String text = "She said, \"Hello world.\"";
    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result = annotator.getQuotes(text);
    assertEquals(1, result.first().size());
    Pair<Integer, Integer> quote = result.first().get(0);
    assertEquals(Integer.valueOf(10), quote.first());
    assertEquals(Integer.valueOf(24), quote.second());
  }
@Test
  public void testGetQuotesWithNestedQuotes() {
    Properties props = new Properties();
    props.setProperty("quote.allowEmbeddedSame", "true");
    QuoteAnnotator annotator = new QuoteAnnotator(props);
    String text = "He said, \"She replied, 'Yes.'\"";
    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result = annotator.getQuotes(text);
    assertEquals(2, result.first().size());
  }
@Test
  public void testGetQuotesWithUnclosedQuotesExtraction() {
    Properties props = new Properties();
    props.setProperty("quote.extractUnclosedQuotes", "true");
    QuoteAnnotator annotator = new QuoteAnnotator(props);
    String text = "It's incomplete: 'He never said.";
    Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> result = annotator.getQuotes(text);
    assertEquals(0, result.first().size());
    assertEquals(1, result.second().size());
  }
@Test
  public void testReplaceUnicodeQuotes() {
    String unicodeText = "He said, “Hello.”";
    String replaced = QuoteAnnotator.replaceUnicode(unicodeText);
    assertTrue(replaced.contains("\"Hello.\""));
  }
@Test
  public void testQuoteComparatorOrdersByStartCharOffset() {
    CoreMap first = new TypesafeMap();
    first.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    CoreMap second = new TypesafeMap();
    second.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    Comparator<CoreMap> comparator = QuoteAnnotator.getQuoteComparator();
    int result = comparator.compare(first, second);
    assertTrue(result > 0);
  }
@Test
  public void testMakeQuoteAnnotation() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setOriginalText("Hello");
    token1.setBeginPosition(1);
    token1.setEndPosition(6);
    tokens.add(token1);
    Annotation quote = QuoteAnnotator.makeQuote("Hello", 1, 6, tokens, 0, 0, 0, "doc-1");
    assertEquals("Hello", quote.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(1), quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(6), quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("doc-1", quote.get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(tokens, quote.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testIsAQuoteMapStarterReturnsTrueIfStartMatches() {
    Map<String, List<Pair<Integer, Integer>>> quoteMap = new HashMap<>();
    List<Pair<Integer, Integer>> quotes = new ArrayList<>();
    quotes.add(new Pair<>(10, 20));
    quoteMap.put("\"", quotes);
    boolean result = QuoteAnnotator.isAQuoteMapStarter(10, quoteMap);
    assertTrue(result);
  }
@Test
  public void testIsSingleQuote() {
    assertTrue(QuoteAnnotator.isSingleQuote("'"));
    assertFalse(QuoteAnnotator.isSingleQuote("\""));
  }
@Test
  public void testIsWhitespaceOrPunct() {
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct(" "));
    assertTrue(QuoteAnnotator.isWhitespaceOrPunct(","));
    assertFalse(QuoteAnnotator.isWhitespaceOrPunct("a"));
  }
@Test
  public void testSingleQuoteStartAtBeginning() {
    String text = "'hello'";
    boolean result = QuoteAnnotator.isSingleQuoteStart(text, 0);
    assertTrue(result);
  }
@Test
  public void testSingleQuoteEndAtEnding() {
    String text = "'hello'";
    boolean result = QuoteAnnotator.isSingleQuoteEnd(text, 6);
    assertTrue(result);
  }
@Test
  public void testDoubleQuoteEndWithWhitespace() {
    String text = "\"hello\" ";
    boolean result = QuoteAnnotator.isDoubleQuoteEnd(text, 6);
    assertTrue(result);
  }
@Test
  public void testRequirementsIncludesTextAndTokensAnnotations() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> requirements = annotator.requires();
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContainsQuotationAnnotation() {
    Properties props = new Properties();
    QuoteAnnotator annotator = new QuoteAnnotator("quote", props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.QuotationsAnnotation.class));
  }
@Test
  public void testXmlFreeTextWithTokenOffsets() {
    Annotation annotation = new Annotation("Hello <tag>world</tag>!");
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    t1.setOriginalText("Hello");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    t2.setOriginalText("world");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    String clean = QuoteAnnotator.xmlFreeText("Hello <tag>world</tag>!", annotation);
    assertNotNull(clean);
    assertTrue(clean.contains("Hello"));
    assertTrue(clean.length() == "Hello <tag>world</tag>!".length());
  }
@Test
  public void testGatherQuotesRecursiveEmbedding() {
    TypesafeMap outer = new TypesafeMap();
    TypesafeMap inner = new TypesafeMap();
    inner.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    List<CoreMap> innerList = new ArrayList<>();
    innerList.add(inner);
    outer.set(CoreAnnotations.QuotationsAnnotation.class, innerList);
    List<CoreMap> gathered = QuoteAnnotator.gatherQuotes(outer);
    assertEquals(1, gathered.size());
    assertEquals(Integer.valueOf(15), gathered.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
  } 
}