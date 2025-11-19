public class Parse_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructorWithHeadIndex() {
    String text = "The quick brown fox.";
    Span span = new Span(0, 19);
    Parse parse = new Parse(text, span, "NP", 1.0, 2);

    assertEquals(text, parse.getText());
    assertEquals(span, parse.getSpan());
    assertEquals("NP", parse.getType());
    assertEquals(1.0, parse.getProb(), 0.0001);
    assertEquals(2, parse.getHeadIndex());
  }
@Test
  public void testConstructorWithHeadParse() {
    String text = "The quick brown fox.";
    Span span = new Span(0, 19);
    Parse head = new Parse(text, span, "NP", 1.0, 3);
    Parse parse = new Parse(text, span, "NP", 0.5, head);

    assertEquals(head, parse.getHead());
    assertEquals(3, parse.getHeadIndex());
  }
@Test
  public void testCloneCreatesDeepCopy() {
    String text = "Hello world";
    Span span = new Span(0, 5);
    Parse original = new Parse(text, span, "NP", 0.9, 0);
    original.setLabel("L1");
    original.addProb(0.1);
    original.setType("NN");

    Parse clone = (Parse) original.clone();

    assertNotSame(original, clone);
    assertEquals(original.getSpan(), clone.getSpan());
    assertEquals(original.getText(), clone.getText());
    assertEquals(original.getType(), clone.getType());
    assertEquals(original.getProb(), clone.getProb(), 0.0001);
    assertEquals(original.getLabel(), clone.getLabel());
  }
@Test
  public void testInsertWithContainedSpan() {
    String text = "The quick brown fox";
    Parse parent = new Parse(text, new Span(0, 19), "NP", 1.0, 0);
    Parse child = new Parse(text, new Span(4, 9), "JJ", 1.0, 1);

    parent.insert(child);

    Parse[] children = parent.getChildren();
    assertEquals(1, children.length);
    assertEquals(child, children[0]);
    assertEquals(parent, child.getParent());
  }
@Test(expected = IllegalArgumentException.class)
  public void testInsertWithInvalidSpan() {
    String text = "sample";
    Parse parent = new Parse(text, new Span(0, 6), "S", 1.0, 0);
    Parse child = new Parse(text, new Span(7, 10), "NP", 1.0, 0);

    parent.insert(child);
  }
@Test
  public void testSetAndGetType() {
    Parse parse = new Parse("text", new Span(0, 4), "S", 1.0, 0);
    parse.setType("NP");
    assertEquals("NP", parse.getType());
  }
@Test
  public void testGetCoveredText() {
    String text = "John walks";
    Parse parse = new Parse(text, new Span(0, 4), "NN", 0.5, 0);
    assertEquals("John", parse.getCoveredText());
  }
@Test
  public void testAddPreviousPunctuation() {
    String text = "sample";
    Parse base = new Parse(text, new Span(0, 6), "NN", 1.0, 0);
    Parse punct = new Parse(text, new Span(5, 6), ",", 1.0, 0);

    base.addPreviousPunctuation(punct);
    Collection<Parse> punctuationSet = base.getPreviousPunctuationSet();

    assertNotNull(punctuationSet);
    assertTrue(punctuationSet.contains(punct));
  }
@Test
  public void testAddNextPunctuation() {
    String text = "sample.";
    Parse base = new Parse(text, new Span(0, 6), "NN", 1.0, 0);
    Parse punct = new Parse(text, new Span(6, 7), ".", 1.0, 0);

    base.addNextPunctuation(punct);
    Collection<Parse> punctuationSet = base.getNextPunctuationSet();

    assertNotNull(punctuationSet);
    assertTrue(punctuationSet.contains(punct));
  }
@Test
  public void testSetNextPrevPunctuationDirectly() {
    String text = "sample!";
    Parse parse = new Parse(text, new Span(0, 6), "NN", 1.0, 0);
    Parse punct = new Parse(text, new Span(6, 7), "!", 1.0, 0);

    Collection<Parse> nextSet = Collections.singleton(punct);
    Collection<Parse> prevSet = Collections.singleton(punct);

    parse.setNextPunctuation(nextSet);
    parse.setPrevPunctuation(prevSet);

    assertEquals(nextSet, parse.getNextPunctuationSet());
    assertEquals(prevSet, parse.getPreviousPunctuationSet());
  }
@Test
  public void testCompleteReturnsTrueIfSinglePart() {
    Parse p = new Parse("test", new Span(0, 4), "S", 1.0, 0);
    Parse child = new Parse("test", new Span(0, 4), "NP", 1.0, 0);

    p.insert(child);

    assertTrue(p.complete());
  }
@Test
  public void testCompleteReturnsFalseIfMultipleParts() {
    Parse p = new Parse("text", new Span(0, 6), "S", 1.0, 0);
    Parse c1 = new Parse("text", new Span(0, 3), "NP", 1.0, 0);
    Parse c2 = new Parse("text", new Span(4, 6), "VP", 1.0, 1);

    p.insert(c1);
    p.insert(c2);

    assertFalse(p.complete());
  }
@Test
  public void testToStringReturnsCoveredText() {
    String text = "walk";
    Parse parse = new Parse("walk fast", new Span(0, 4), "VB", 0.8, 0);
    assertEquals(text, parse.toString());
  }
@Test
  public void testIsPosTagTrueCondition() {
    Parse p = new Parse("cat", new Span(0, 3), "NN", 1.0, 0);
    Parse tok = new Parse("cat", new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    p.insert(tok);

    assertTrue(p.isPosTag());
  }
@Test
  public void testIsFlatTrueCondition() {
    String text = "dogs bark";
    Parse p = new Parse(text, new Span(0, 9), "S", 1.0, 0);
    Parse n1 = new Parse(text, new Span(0, 4), "NN", 1.0, 0);
    Parse t1 = new Parse(text, new Span(0, 4), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    n1.insert(t1);
    Parse n2 = new Parse(text, new Span(5, 9), "VB", 1.0, 1);
    Parse t2 = new Parse(text, new Span(5, 9), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
    n2.insert(t2);
    p.insert(n1);
    p.insert(n2);

    assertTrue(p.isFlat());
  }
@Test
  public void testGetTagNodesReturnsCorrectTags() {
    String text = "We run";
    Parse top = new Parse(text, new Span(0, 6), "S", 1.0, 0);
    Parse np = new Parse(text, new Span(0, 2), "PRP", 1.0, 0);
    Parse vp = new Parse(text, new Span(3, 6), "VB", 1.0, 1);
    Parse word1 = new Parse(text, new Span(0, 2), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    Parse word2 = new Parse(text, new Span(3, 6), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
    np.insert(word1);
    vp.insert(word2);
    top.insert(np);
    top.insert(vp);

    Parse[] tags = top.getTagNodes();
    assertEquals(2, tags.length);
    assertEquals("PRP", tags[0].getType());
    assertEquals("VB", tags[1].getType());
  }
@Test
  public void testGetTokenNodesReturnsTokens() {
    String text = "See you";
    Parse top = new Parse(text, new Span(0, 7), "S", 1.0, 0);
    Parse np1 = new Parse(text, new Span(0, 3), "VB", 1.0, 0);
    Parse np2 = new Parse(text, new Span(4, 7), "PRP", 1.0, 1);
    Parse tok1 = new Parse(text, new Span(0, 3), AbstractBottomUpParser.TOK_NODE, 1.0, 0);
    Parse tok2 = new Parse(text, new Span(4, 7), AbstractBottomUpParser.TOK_NODE, 1.0, 1);
    np1.insert(tok1);
    np2.insert(tok2);
    top.insert(np1);
    top.insert(np2);

    Parse[] tokens = top.getTokenNodes();
    assertEquals(2, tokens.length);
    assertEquals("See", tokens[0].getCoveredText());
    assertEquals("you", tokens[1].getCoveredText());
  }
@Test
  public void testGetCommonParentReturnsCorrectParent() {
    String text = "I see you";
    Parse top = new Parse(text, new Span(0, 9), "S", 1.0, 0);
    Parse np1 = new Parse(text, new Span(0, 1), "PRP", 1.0, 0);
    Parse np2 = new Parse(text, new Span(6, 9), "PRP", 1.0, 1);
    top.insert(np1);
    top.insert(np2);

    assertEquals(top, np1.getCommonParent(np2));
  }
@Test
  public void testGetDerivationAndSetDerivation() {
    String text = "Hello";
    Parse p = new Parse(text, new Span(0, 5), "UH", 1.0, 0);

    StringBuffer sb = new StringBuffer("derive:");
    p.setDerivation(sb);

    assertEquals("derive:", p.getDerivation().toString());
  }
@Test
  public void testIsChunkFlag() {
    String text = "word";
    Parse parse = new Parse(text, new Span(0, 4), "NN", 1.0, 0);

    parse.isChunk(true);
    assertTrue(parse.isChunk());

    parse.isChunk(false);
    assertFalse(parse.isChunk());
  } 
}