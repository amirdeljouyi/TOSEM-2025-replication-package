public class Parse_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructorAndGetters() {
    String text = "Hello world";
    Span span = new Span(0, 11);
    Parse parse = new Parse(text, span, "TOP", 1.0, 0);

    assertEquals("TOP", parse.getType());
    assertEquals(text, parse.getText());
    assertEquals(span, parse.getSpan());
    assertEquals(1.0, parse.getProb(), 0.00001);
    assertEquals(parse, parse.getHead());
    assertEquals(0, parse.getHeadIndex());
  }
@Test
  public void testSetType() {
    Parse parse = new Parse("Sentence", new Span(0, 8), "TOP", 1.0, 0);
    parse.setType("S");
    assertEquals("S", parse.getType());
  }
@Test
  public void testSetAndGetLabel() {
    Parse parse = new Parse("Sentence", new Span(0, 8), "TOP", 1.0, 0);
    parse.setLabel("LABEL");
    assertEquals("LABEL", parse.getLabel());
  }
@Test
  public void testAddProb() {
    Parse parse = new Parse("abc", new Span(0, 3), "TOP", 0.5, 0);
    parse.addProb(0.3);
    assertEquals(0.8, parse.getProb(), 0.001);
  }
@Test
  public void testCloneSelf() {
    Parse original = new Parse("abc", new Span(0, 3), "TOP", 0.5, 0);
    Parse clone = (Parse) original.clone();

    assertNotSame(original, clone);
    assertEquals(original.getSpan(), clone.getSpan());
    assertEquals(original.getType(), clone.getType());
    assertEquals(original.getText(), clone.getText());
    assertEquals(original.getProb(), clone.getProb(), 0.00001);
  }
@Test
  public void testCloneWithChildren() {
    Parse parent = new Parse("text here", new Span(0, 9), "S", 1.0, 0);
    Parse child = new Parse("text here", new Span(0, 4), "NP", 0.9, 0);
    parent.insert(child);
    Parse clone = (Parse) parent.clone();

    assertEquals(1, clone.getChildren().length);
    assertNotSame(parent.getChildren()[0], clone.getChildren()[0]);
    assertEquals("NP", clone.getChildren()[0].getType());
  }
@Test
  public void testInsertAcceptableChild() {
    Parse parent = new Parse("this is it", new Span(0, 10), "TOP", 1.0, 0);
    Parse child = new Parse("this is it", new Span(0, 4), "NP", 0.9, 0);
    parent.insert(child);

    Parse[] children = parent.getChildren();
    assertEquals(1, children.length);
    assertEquals("NP", children[0].getType());
    assertEquals(parent, child.getParent());
  }
@Test(expected = IllegalArgumentException.class)
  public void testInsertOutsideThrowsException() {
    Parse parent = new Parse("this", new Span(0, 4), "TOP", 1.0, 0);
    Parse invalid = new Parse("this", new Span(5, 7), "X", 1.0, 0);
    parent.insert(invalid);
  }
@Test
  public void testAddPreviousAndNextPunctuation() {
    Parse text = new Parse("text", new Span(0, 4), "S", 1.0, 0);
    Parse punct1 = new Parse("text", new Span(0, 1), ".", 1.0, 0);
    Parse punct2 = new Parse("text", new Span(3, 4), ".", 1.0, 0);

    text.addPreviousPunctuation(punct1);
    text.addNextPunctuation(punct2);

    assertTrue(text.getPreviousPunctuationSet().contains(punct1));
    assertTrue(text.getNextPunctuationSet().contains(punct2));
  }
@Test
  public void testGetCoveredText() {
    Parse p = new Parse("Some sentence.", new Span(5, 13), "NP", 1.0, 0);
    assertEquals("sentence", p.getCoveredText());
  }
@Test
  public void testIsPosTagTrue() {
    Parse parent = new Parse("tag word", new Span(0, 8), "NN", 0.9, 0);
    Parse token = new Parse("tag word", new Span(4, 8), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    parent.insert(token);
    assertTrue(parent.isPosTag());
  }
@Test
  public void testIsFlatTrue() {
    Parse parent = new Parse("tiles go", new Span(0, 8), "VP", 0.9, 0);
    Parse token1 = new Parse("tiles go", new Span(0, 5), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    Parse pos1 = new Parse("tiles go", new Span(0, 5), "VB", 1.0, 0);
    pos1.insert(token1);

    Parse token2 = new Parse("tiles go", new Span(6, 8), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
    Parse pos2 = new Parse("tiles go", new Span(6, 8), "IN", 1.0, 1);
    pos2.insert(token2);

    parent.insert(pos1);
    parent.insert(pos2);

    assertTrue(parent.isFlat());
  }
@Test
  public void testGetChildrenReturnsCorrectArray() {
    Parse p = new Parse("abc def", new Span(0, 7), "S", 1.0, 0);
    Parse c1 = new Parse("abc def", new Span(0, 3), "NP", 1.0, 0);
    Parse c2 = new Parse("abc def", new Span(4, 7), "VP", 1.0, 1);

    p.insert(c1);
    p.insert(c2);

    Parse[] children = p.getChildren();
    assertEquals(2, children.length);
    assertEquals("NP", children[0].getType());
    assertEquals("VP", children[1].getType());
  }
@Test
  public void testGetHeadAndHeadIndex() {
    Parse token = new Parse("unit", new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 3);
    Parse pos = new Parse("unit", new Span(0, 4), "NN", 1.0, token);
    pos.insert(token);

    assertEquals(token, pos.getHead());
    assertEquals(3, pos.getHeadIndex());
  }
@Test
  public void testCompareToMethod() {
    Parse p1 = new Parse("text", new Span(0, 4), "S", 0.9, 0);
    Parse p2 = new Parse("text", new Span(0, 4), "S", 0.5, 0);
    assertTrue(p1.compareTo(p2) < 0); 
  }
@Test
  public void testSetAndGetDerivation() {
    Parse p = new Parse("hi", new Span(0, 2), "S", 1.0, 0);
    StringBuffer sb = new StringBuffer("foo");
    p.setDerivation(sb);
    assertEquals("foo", p.getDerivation().toString());
  }
@Test
  public void testGetCommonParent() {
    Parse root = new Parse("abc def", new Span(0, 7), "TOP", 1.0, 0);
    Parse left = new Parse("abc def", new Span(0, 3), "NP", 0.9, 0);
    Parse right = new Parse("abc def", new Span(4, 7), "VP", 0.9, 1);

    root.insert(left);
    root.insert(right);

    Parse leftChild = new Parse("abc def", new Span(0, 3), "DT", 0.9, 0);
    left.insert(leftChild);
    Parse rightChild = new Parse("abc def", new Span(4, 7), "VB", 0.9, 1);
    right.insert(rightChild);

    Parse common = leftChild.getCommonParent(rightChild);
    assertEquals(root, common);
  }
@Test
  public void testToStringReturnsCoveredText() {
    Parse parse = new Parse("This is something", new Span(5, 7), "VP", 1.0, 0);
    assertEquals("is", parse.toString());
  }
@Test
  public void testCompleteTrueWhenOneChild() {
    Parse p = new Parse("aaa", new Span(0, 3), "S", 1.0, 0);
    Parse child = new Parse("aaa", new Span(0, 3), "NP", 1.0, 0);
    p.insert(child);

    assertTrue(p.complete());
  }
@Test
  public void testCompleteFalseWhenNoChildren() {
    Parse p = new Parse("aaa", new Span(0, 3), "S", 1.0, 0);
    assertFalse(p.complete());
  }
@Test
  public void testEqualsAndHashCode() {
    Parse p1 = new Parse("data", new Span(0, 4), "S", 1.0, 0);
    Parse p2 = new Parse("data", new Span(0, 4), "S", 1.0, 0);

    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
  }
@Test
  public void testSetAndGetChunk() {
    Parse p = new Parse("data", new Span(0, 4), "NP", 1.0, 0);
    p.isChunk(true);
    assertTrue(p.isChunk());
  } 
}