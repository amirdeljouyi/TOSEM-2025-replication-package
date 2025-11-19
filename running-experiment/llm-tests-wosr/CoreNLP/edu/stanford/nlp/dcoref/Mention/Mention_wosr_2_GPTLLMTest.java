public class Mention_wosr_2_GPTLLMTest { 

 @Test
  public void testSpanToStringReturnsOriginalSpanText() {
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    span.add(token1);
    span.add(token2);

    Mention mention = new Mention(1, 0, 2, new SemanticGraph(), span);

    String result = mention.spanToString();
    assertEquals("Barack Obama", result);
  }
@Test
  public void testLowercaseNormalizedSpanStringReturnsLowercase() {
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    span.add(token1);
    span.add(token2);

    Mention mention = new Mention(2, 0, 2, new SemanticGraph(), span);
    mention.spanToString(); 

    String result = mention.lowercaseNormalizedSpanString();
    assertEquals("new york", result);
  }
@Test
  public void testNerTokensReturnsNullWhenNerIsO() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    Mention mention = new Mention(3, 0, 1, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headIndex = 0;
    mention.startIndex = 0;
    mention.nerString = "O";

    assertNull(mention.nerTokens());
  }
@Test
  public void testNerTokensReturnsCorrectSingleTokenSpan() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.setNER("PERSON");

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    Mention mention = new Mention(4, 5, 6, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headIndex = 5;
    mention.startIndex = 5;
    mention.nerString = "PERSON";

    List<CoreLabel> tokens = mention.nerTokens();
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("Obama", tokens.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testIsMemberOfSameListReturnsTrueForSharedBelongList() {
    Mention list = new Mention();
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addBelongsToList(list);
    m2.addBelongsToList(list);

    assertTrue(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsMemberOfSameListReturnsFalseForDifferentLists() {
    Mention list1 = new Mention();
    Mention list2 = new Mention();
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addBelongsToList(list1);
    m2.addBelongsToList(list2);

    assertFalse(m1.isMemberOfSameList(m2));
  }
@Test
  public void testAppearEarlierThanComparesSentenceNumber() {
    Mention earlier = new Mention();
    earlier.sentNum = 1;
    earlier.startIndex = 2;
    earlier.endIndex = 3;
    earlier.headIndex = 2;

    Mention later = new Mention();
    later.sentNum = 2;
    later.startIndex = 1;
    later.endIndex = 2;
    later.headIndex = 1;

    assertTrue(earlier.appearEarlierThan(later));
    assertFalse(later.appearEarlierThan(earlier));
  }
@Test
  public void testIsAppositionReturnsTrueIfAppositionAdded() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addApposition(m2);

    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testIsPredicateNominativesReturnsTrueIfAdded() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addPredicateNominatives(m2);

    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testIsRelativePronounReturnsTrueIfAdded() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addRelativePronoun(m2);

    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testSameSentenceReturnsTrueIfSameSentenceReference() {
    List<CoreLabel> sentence = new ArrayList<>();
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "dog");
    sentence.add(tok);

    Mention m1 = new Mention();
    m1.sentenceWords = sentence;
    Mention m2 = new Mention();
    m2.sentenceWords = sentence;

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testSameSentenceReturnsFalseIfDifferentSentenceReference() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentenceWords = new ArrayList<>();
    m2.sentenceWords = new ArrayList<>();
    m2.sentenceWords.add(new CoreLabel());

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testInsideInReturnsTrueForNestedMentions() {
    Mention outer = new Mention();
    outer.sentNum = 1;
    outer.startIndex = 0;
    outer.endIndex = 10;

    Mention inner = new Mention();
    inner.sentNum = 1;
    inner.startIndex = 2;
    inner.endIndex = 5;

    assertTrue(inner.insideIn(outer));
  }
@Test
  public void testInsideInReturnsFalseForNonNestedMentions() {
    Mention m1 = new Mention();
    m1.sentNum = 0;
    m1.startIndex = 1;
    m1.endIndex = 3;

    Mention m2 = new Mention();
    m2.sentNum = 0;
    m2.startIndex = 5;
    m2.endIndex = 7;

    assertFalse(m2.insideIn(m1));
  }
@Test
  public void testRemoveParenthesisTruncatesAtFirstParenthesis() {
    String result = Mention.removeParenthesis("John (CEO)");
    assertEquals("John", result);
  }
@Test
  public void testRemoveParenthesisReturnsUnchangedTextWithoutParenthesis() {
    String result = Mention.removeParenthesis("Simple Text");
    assertEquals("", result);
  }
@Test
  public void testIsTheCommonNounRecognizesPatternProperly() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "dog");

    List<CoreLabel> span = Arrays.asList(tok1, tok2);

    Mention m = new Mention(1, 0, 2, null, span);
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    String str = m.spanToString();
    assertTrue(m.isTheCommonNoun());
  } 
}