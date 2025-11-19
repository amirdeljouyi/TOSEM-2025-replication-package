public class Parse_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorAndBasicAccessors() {
    String text = "Sample sentence.";
    Span span = new Span(0, 6);
    Parse parse = new Parse(text, span, "NP", 0.9, 0);

    assertEquals("NP", parse.getType());
    assertEquals(span, parse.getSpan());
    assertEquals(text, parse.getText());
    assertEquals(0.9, parse.getProb(), 0.0001);
    assertEquals(0, parse.getHeadIndex());
    assertEquals("Sample", parse.getCoveredText());
  }
@Test
  public void testSetAndGetLabel() {
    String text = "Label test";
    Span span = new Span(0, 4);
    Parse parse = new Parse(text, span, "VP", 0.8, 0);

    parse.setLabel("X");
    assertEquals("X", parse.getLabel());
  }
@Test
  public void testInsertAddsChild() {
    String text = "the cat";
    Parse parent = new Parse(text, new Span(0, 7), "NP", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 3), "DT", 1.0, 0);

    parent.insert(child);

    Parse[] children = parent.getChildren();
    assertEquals(1, children.length);
    assertEquals(child, children[0]);
    assertEquals(parent, child.getParent());
  }
@Test(expected = IllegalArgumentException.class)
  public void testInsertThrowsWhenSpanNotContained() {
    String text = "hello world";
    Parse parent = new Parse(text, new Span(0, 5), "ROOT", 1.0, 0);
    Parse invalidChild = new Parse(text, new Span(6, 11), "WRONG", 1.0, 0);

    parent.insert(invalidChild);
  }
@Test
  public void testAddAndGetPreviousPunctuation() {
    String text = "( )";
    Parse main = new Parse(text, new Span(2, 3), "NP", 1.0, 0);
    Parse punct = new Parse(text, new Span(0, 1), ".", 1.0, 0);

    main.addPreviousPunctuation(punct);
    Collection<Parse> puncts = main.getPreviousPunctuationSet();

    assertNotNull(puncts);
    assertTrue(puncts.contains(punct));
  }
@Test
  public void testAddAndGetNextPunctuation() {
    String text = "( )";
    Parse main = new Parse(text, new Span(0, 1), "NP", 1.0, 0);
    Parse punct = new Parse(text, new Span(2, 3), ".", 1.0, 0);

    main.addNextPunctuation(punct);
    Collection<Parse> puncts = main.getNextPunctuationSet();

    assertNotNull(puncts);
    assertTrue(puncts.contains(punct));
  }
@Test
  public void testCompleteReturnsTrue() {
    String text = "hello world";
    Parse parent = new Parse(text, new Span(0, 11), "S", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 5), "NP", 1.0, 0);
    parent.insert(child);

    assertTrue(parent.complete());
  }
@Test
  public void testCompleteReturnsFalse() {
    String text = "hi";
    Parse parent = new Parse(text, new Span(0, 2), "S", 1.0, 0);

    assertFalse(parent.complete());
  }
@Test
  public void testCloneMaintainsStructure() {
    String text = "cats meow";
    Parse original = new Parse(text, new Span(0, 9), "S", 1.0, 0);
    Parse np = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
    Parse vp = new Parse(text, new Span(5, 9), "VP", 1.0, 1);
    original.insert(np);
    original.insert(vp);
    original.setLabel("top");

    Parse cloned = (Parse) original.clone();

    assertEquals(original.getType(), cloned.getType());
    assertEquals(original.getSpan(), cloned.getSpan());
    assertEquals(2, cloned.getChildCount());
    assertEquals("top", cloned.getLabel());
  }
@Test
  public void testAddProbAccumulatesLogProb() {
    String text = "text";
    Parse p = new Parse(text, new Span(0, 4), "X", 0.1, 0);
    p.addProb(0.3);

    assertEquals(0.4, p.getProb(), 0.0001);
  }
@Test
  public void testSetChildReplacesChildWithLabel() {
    String text = "text";
    Parse parent = new Parse(text, new Span(0, 4), "P", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 4), "X", 1.0, 0);
    parent.insert(child);

    parent.setChild(0, "NEW");

    assertEquals("NEW", parent.getChildren()[0].getLabel());
  }
@Test
  public void testGetHeadAndHeadIndex() {
    String text = "text";
    Parse p = new Parse(text, new Span(0, 4), "X", 1.0, 7);
    assertEquals(p, p.getHead());
    assertEquals(7, p.getHeadIndex());
  }
@Test
  public void testIsPosTagAndIsFlatReturnsCorrectValues() {
    String text = "tag";
    Parse posNode = new Parse(text, new Span(0, 3), "NN", 0.9, 0);
    Parse token = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    posNode.insert(token);

    assertTrue(posNode.isPosTag());

    Parse flatNode = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
    flatNode.insert(posNode);

    assertTrue(flatNode.isFlat());
  }
@Test
  public void testGetTagNodesReturnsCorrectTags() {
    String text = "word";
    Parse root = new Parse(text, new Span(0, 4), "S", 1.0, 0);
    Parse pos = new Parse(text, new Span(0, 4), "NN", 0.8, 0);
    Parse tok = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    pos.insert(tok);
    root.insert(pos);

    Parse[] tags = root.getTagNodes();
    assertEquals(1, tags.length);
    assertEquals("NN", tags[0].getType());
  }
@Test
  public void testGetTokenNodesReturnsCorrectTokens() {
    String text = "word";
    Parse root = new Parse(text, new Span(0, 4), "S", 1.0, 0);
    Parse pos = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
    Parse tok = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    pos.insert(tok);
    root.insert(pos);

    Parse[] tokens = root.getTokenNodes();
    assertEquals(1, tokens.length);
    assertEquals(AbstractBottomUpParser.TOK_NODE, tokens[0].getType());
  }
@Test
  public void testGetCommonParentSameNodeReturnParent() {
    String text = "word";
    Parse parent = new Parse(text, new Span(0, 4), "S", 1.0, 0);
    Parse child = new Parse(text, new Span(0, 4), "NP", 1.0, 0);
    parent.insert(child);

    assertEquals(parent, child.getCommonParent(child));
  }
@Test
  public void testGetCommonParentDifferentNodes() {
    String text = "Joe went";
    Parse root = new Parse(text, new Span(0, 8), "S", 1.0, 0);
    Parse p1 = new Parse(text, new Span(0, 3), "NP", 1.0, 0);
    Parse p2 = new Parse(text, new Span(4, 8), "VP", 1.0, 1);
    root.insert(p1);
    root.insert(p2);

    assertEquals(root, p1.getCommonParent(p2));
  }
@Test
  public void testEqualsAndHashCode() {
    String text = "word";
    Span span = new Span(0, 4);
    Parse p1 = new Parse(text, span, "NP", 1.0, 0);
    Parse p2 = new Parse(text, span, "NP", 1.0, 0);

    assertTrue(p1.equals(p2));
    assertEquals(p1.hashCode(), p2.hashCode());
  }
@Test
  public void testToStringReturnsCoveredText() {
    String text = "parsed";
    Span span = new Span(0, 6);
    Parse p = new Parse(text, span, "X", 0.5, 0);

    assertEquals("parsed", p.toString());
  }
@Test
  public void testCompareToOrdering() {
    String text = "abc";
    Parse p1 = new Parse(text, new Span(0, 1), "A", 0.9, 0);
    Parse p2 = new Parse(text, new Span(1, 2), "B", 0.5, 0);

    assertTrue(p1.compareTo(p2) < 0);
    assertTrue(p2.compareTo(p1) > 0);
  }
@Test
  public void testSetAndGetDerivation() {
    String text = "text";
    Parse parse = new Parse(text, new Span(0, 4), "NP", 1.0, 0);

    StringBuffer sb = new StringBuffer("derivation");
    parse.setDerivation(sb);

    assertEquals("derivation", parse.getDerivation().toString());
  }
@Test
  public void testSetAndCheckIsChunk() {
    String text = "sample";
    Span span = new Span(0, 6);
    Parse parse = new Parse(text, span, "X", 1.0, 0);

    parse.isChunk(true);
    assertTrue(parse.isChunk());

    parse.isChunk(false);
    assertFalse(parse.isChunk());
  }
@Test
  public void testIndexOfReturnsChildIndex() {
    String text = "abc";
    Parse parent = new Parse(text, new Span(0, 3), "ROOT", 1.0, 0);
    Parse child1 = new Parse(text, new Span(0, 1), "A", 1.0, 0);
    Parse child2 = new Parse(text, new Span(1, 3), "B", 1.0, 0);
    parent.insert(child1);
    parent.insert(child2);

    assertEquals(0, parent.indexOf(child1));
    assertEquals(1, parent.indexOf(child2));
  } 
}