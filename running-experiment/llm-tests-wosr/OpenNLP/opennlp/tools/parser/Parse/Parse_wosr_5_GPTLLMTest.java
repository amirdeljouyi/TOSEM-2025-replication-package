public class Parse_wosr_5_GPTLLMTest { 

 @Test
  public void testConstructorAndBasicAccessors() {
    String text = "Hello world!";
    Span span = new Span(0, 5);
    Parse parse = new Parse(text, span, "NP", 0.9, 0);

    assertEquals("Hello world!", parse.getText());
    assertEquals("NP", parse.getType());
    assertEquals(0.9, parse.getProb(), 0.0001);
    assertEquals(span, parse.getSpan());
    assertEquals("Hello", parse.getCoveredText());
  }
@Test
  public void testCloneMaintainsStructure() {
    String text = "Some text";
    Span span = new Span(0, 9);
    Parse original = new Parse(text, span, "S", 1.0, 0);
    Parse token = new Parse(text, new Span(0, 4), "TOK", 1.0, 0);
    original.insert(token);

    Parse clone = (Parse) original.clone();

    assertEquals(original.getText(), clone.getText());
    assertEquals(original.getType(), clone.getType());
    assertEquals(original.getSpan(), clone.getSpan());
    assertNotSame(original, clone);
    assertEquals(1, clone.getChildCount());
    assertEquals("TOK", clone.getChildren()[0].getType());
  }
@Test
  public void testInsertSingleChild() {
    String text = "Hello world.";
    Parse parent = new Parse(text, new Span(0, 12), "S", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 5), "NP", 1.0, 0);

    parent.insert(child);

    assertEquals(1, parent.getChildCount());
    assertEquals("NP", parent.getChildren()[0].getType());
    assertEquals(parent, child.getParent());
  }
@Test(expected = IllegalArgumentException.class)
  public void testInsertOutsideSpanThrowsException() {
    String text = "Test sentence.";
    Parse root = new Parse(text, new Span(0, 5), "S", 1.0, 0);
    Parse invalid = new Parse(text, new Span(6, 10), "NP", 1.0, 0);

    root.insert(invalid);
  }
@Test
  public void testAddPreviousAndNextPunctuation() {
    String text = "This is a test.";
    Parse main = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
    Parse punctBefore = new Parse(text, new Span(0, 1), ".", 1.0, 0);
    Parse punctAfter = new Parse(text, new Span(13, 14), ".", 1.0, 0);

    main.addPreviousPunctuation(punctBefore);
    main.addNextPunctuation(punctAfter);

    assertTrue(main.getPreviousPunctuationSet().contains(punctBefore));
    assertTrue(main.getNextPunctuationSet().contains(punctAfter));
  }
@Test
  public void testGetTagSequenceProbBaseCase() {
    String text = "Today";
    Parse tag = new Parse(text, new Span(0, 5), "NN", 0.2, 0);
    Parse token = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 0.2, 0);
    tag.insert(token);

    double logProb = tag.getTagSequenceProb();
    assertEquals(Math.log(0.2), logProb, 0.0001);
  }
@Test
  public void testUpdateSpanAndHeadIndex() {
    String text = "John Smith went home.";
    Parse parent = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
    Parse child1 = new Parse(text, new Span(0, 10), "NP", 1.0, 0);
    Parse child2 = new Parse(text, new Span(11, 20), "VP", 1.0, 1);

    parent.insert(child1);
    parent.insert(child2);
    parent.updateSpan();

    assertEquals(new Span(0, 20), parent.getSpan());
    assertEquals(2, parent.getChildCount());
  }
@Test
  public void testEqualityAndHashCode() {
    String text = "Same sentence.";
    Span span = new Span(0, 5);
    Parse p1 = new Parse(text, span, "NP", 0.5, 0);
    Parse p2 = new Parse(text, span, "NP", 0.5, 0);

    assertTrue(p1.equals(p2));
    assertEquals(p1.hashCode(), p2.hashCode());
  }
@Test
  public void testToStringPennTreebank() {
    String text = "Test";
    Parse word = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
    Parse token = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    word.insert(token);

    String treebank = word.toStringPennTreebank();

    assertTrue(treebank.startsWith("(NN "));
    assertTrue(treebank.endsWith(")"));
  }
@Test
  public void testFlatStructure() {
    String text = "flat test";
    Parse root = new Parse(text, new Span(0, 10), "S", 1.0, 0);
    Parse pos = new Parse(text, new Span(0, 5), "NN", 1.0, 0);
    Parse token = new Parse(text, new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    pos.insert(token);
    root.insert(pos);

    assertTrue(root.isFlat());
  }
@Test
  public void testGetCommonParent() {
    String text = "Test test";
    Parse root = new Parse(text, new Span(0, 9), "S", 1.0, 0);
    Parse child1 = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
    Parse child2 = new Parse(text, new Span(5, 9), "VP", 1.0, 0);
    root.insert(child1);
    root.insert(child2);

    Parse common = child1.getCommonParent(child2);
    assertEquals(root, common);
  }
@Test
  public void testGetAndSetDerivation() {
    String text = "Derive this.";
    Parse parse = new Parse(text, new Span(0, text.length()), "S", 1.0, 0);
    StringBuffer derivation = new StringBuffer("D1>D2");

    parse.setDerivation(derivation);

    assertEquals("D1>D2", parse.getDerivation().toString());
  }
@Test
  public void testAddProb() {
    String text = "test";
    Parse parse = new Parse(text, new Span(0, 4), "NP", 0.5, 0);

    parse.addProb(0.3);

    assertEquals(0.8, parse.getProb(), 0.00001);
  }
@Test
  public void testIsPosTagTrue() {
    String text = "one";
    Parse posTag = new Parse(text, new Span(0, 3), "NN", 1.0, 0);
    Parse tok = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    posTag.insert(tok);

    assertTrue(posTag.isPosTag());
  }
@Test
  public void testGetChildrenReturnsCopy() {
    String text = "copy test";
    Parse parent = new Parse(text, new Span(0, 10), "S", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
    parent.insert(child);

    Parse[] children = parent.getChildren();

    assertEquals(1, children.length);
    assertEquals("NP", children[0].getType());
  } 
}