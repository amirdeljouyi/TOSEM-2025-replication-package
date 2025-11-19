public class Parse_wosr_3_GPTLLMTest { 

 @Test
  public void testConstructorAndBasicFields() {
    String text = "The quick brown fox.";
    Span span = new Span(0, 19);
    Parse parse = new Parse(text, span, "NP", 0.95, 0);

    assertEquals(text, parse.getText());
    assertEquals(span, parse.getSpan());
    assertEquals("NP", parse.getType());
    assertEquals(0.95, parse.getProb(), 0.0001);
    assertEquals(0, parse.getHeadIndex());
    assertEquals(parse, parse.getHead());
    assertNull(parse.getParent());
  }
@Test
  public void testSetAndGetType() {
    Parse parse = new Parse("Sample text", new Span(0, 5), "NP", 1.0, 0);
    parse.setType("VP");
    assertEquals("VP", parse.getType());
  }
@Test
  public void testInsertSingleChild() {
    String text = "The fox";
    Parse parent = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 3), "DT", 1.0, 0);

    parent.insert(child);
    Parse[] children = parent.getChildren();

    assertEquals(1, children.length);
    assertEquals(child, children[0]);
    assertEquals(parent, child.getParent());
  }
@Test(expected = IllegalArgumentException.class)
  public void testInsertOutsideSpanThrowsException() {
    String text = "Some text";
    Parse parent = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
    Parse child = new Parse(text, new Span(5, 8), "NN", 1.0, 0);
    parent.insert(child);
  }
@Test
  public void testCloneShallowCopy() {
    String text = "Hello world";
    Parse original = new Parse(text, new Span(0, 11), "S", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
    original.insert(child);
    original.setLabel("root");

    Parse clone = (Parse) original.clone();

    assertEquals(original.getSpan(), clone.getSpan());
    assertEquals(original.getType(), clone.getType());
    assertEquals(original.getCoveredText(), clone.getCoveredText());
    assertEquals("root", clone.getLabel());
    assertEquals(1, clone.getChildren().length);
    assertNotSame(original, clone);
  }
@Test
  public void testAddProb() {
    Parse parse = new Parse("Example", new Span(0, 7), "NP", 0.5, 0);
    parse.addProb(0.2);
    assertEquals(0.7, parse.getProb(), 0.0001);
  }
@Test
  public void testSetLabelAndGetLabel() {
    Parse parse = new Parse("Some text", new Span(0, 9), "NP", 1.0, 0);
    parse.setLabel("TEST_LABEL");
    assertEquals("TEST_LABEL", parse.getLabel());
  }
@Test
  public void testAddPreviousAndNextPunctuation() {
    String text = "( )";
    Parse p = new Parse(text, new Span(0, 3), "TOP", 1.0, 0);
    Parse punct = new Parse(text, new Span(0, 1), ".", 1.0, 0);

    p.addPreviousPunctuation(punct);
    Collection<Parse> prev = p.getPreviousPunctuationSet();
    assertTrue(prev.contains(punct));

    p.addNextPunctuation(punct);
    Collection<Parse> next = p.getNextPunctuationSet();
    assertTrue(next.contains(punct));
  }
@Test
  public void testSetAndGetParent() {
    String text = "ABC";
    Parse parent = new Parse(text, new Span(0, 3), "TOP", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 1), "DT", 1.0, 0);

    child.setParent(parent);
    assertEquals(parent, child.getParent());
  }
@Test
  public void testGetCoveredText() {
    String text = "OpenAI builds AI";
    Span span = new Span(7, 13);
    Parse parse = new Parse(text, span, "VP", 0.9, 0);
    assertEquals("builds", parse.getCoveredText());
  }
@Test
  public void testIsPosTagReturnsTrue() {
    String text = "Word";
    Parse parent = new Parse(text, new Span(0, 4), "NN", 0.9, 0);
    Parse token = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    parent.insert(token);
    assertTrue(parent.isPosTag());
  }
@Test
  public void testIsFlatReturnsTrue() {
    String text = "The dog";
    Parse parent = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
    Parse pos1 = new Parse(text, new Span(0, 3), "DT", 0.9, 0);
    Parse t1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    pos1.insert(t1);
    Parse pos2 = new Parse(text, new Span(4, 7), "NN", 0.9, 1);
    Parse t2 = new Parse(text, new Span(4, 7), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
    pos2.insert(t2);

    parent.insert(pos1);
    parent.insert(pos2);

    assertTrue(parent.isFlat());
  }
@Test
  public void testGetCommonParent() {
    String text = "John Smith";
    Parse root = new Parse(text, new Span(0, 10), "S", 1.0, 0);
    Parse np = new Parse(text, new Span(0, 10), "NP", 1.0, 0);
    Parse n1 = new Parse(text, new Span(0, 4), "NNP", 1.0, 0);
    Parse t1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    n1.insert(t1);
    Parse n2 = new Parse(text, new Span(5, 10), "NNP", 1.0, 1);
    Parse t2 = new Parse(text, new Span(5, 10), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
    n2.insert(t2);

    np.insert(n1);
    np.insert(n2);
    root.insert(np);

    Parse common = n1.getCommonParent(n2);
    assertEquals(np, common);
  }
@Test
  public void testParseEquality() {
    String text = "Token";
    Span span = new Span(0, 5);
    Parse p1 = new Parse(text, span, "NN", 1.0, 0);
    Parse p2 = new Parse(text, span, "NN", 1.0, 0);

    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
  }
@Test
  public void testCompareToGreater() {
    Parse p1 = new Parse("Example", new Span(0, 5), "NP", 0.8, 0);
    Parse p2 = new Parse("Example", new Span(0, 5), "NP", 0.5, 0);
    assertTrue(p1.compareTo(p2) < 0); 
  }
@Test
  public void testToStringPennTreebank() {
    String text = "Hi";
    Parse root = new Parse(text, new Span(0, 2), "S", 1.0, 0);
    Parse np = new Parse(text, new Span(0, 2), "NP", 1.0, 0);
    Parse word = new Parse(text, new Span(0, 2), "NN", 1.0, 0);
    Parse tok = new Parse(text, new Span(0, 2), AbstractBottomUpParser.TOK_NODE, 1.0, 0);

    word.insert(tok);
    np.insert(word);
    root.insert(np);

    String penn = root.toStringPennTreebank();
    assertNotNull(penn);
    assertTrue(penn.contains("NN"));
  } 
}