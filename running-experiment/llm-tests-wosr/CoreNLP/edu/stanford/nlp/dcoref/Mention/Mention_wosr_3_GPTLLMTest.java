public class Mention_wosr_3_GPTLLMTest { 

 @Test
  public void testSpanToString_populatesCorrectlyOnce() {
    CoreLabel label1 = new CoreLabel();
    label1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel label2 = new CoreLabel();
    label2.set(CoreAnnotations.TextAnnotation.class, "Smith");

    List<CoreLabel> span = Arrays.asList(label1, label2);

    Mention mention = new Mention(1, 0, 2, new SemanticGraph(), span);

    String spanStr = mention.spanToString();
    assertEquals("John Smith", spanStr);

    
    String repeatedCall = mention.spanToString();
    assertSame(spanStr, repeatedCall);
  }
@Test
  public void testLowercaseNormalizedSpanString_initializesLazily() {
    CoreLabel label1 = new CoreLabel();
    label1.set(CoreAnnotations.TextAnnotation.class, "New");

    CoreLabel label2 = new CoreLabel();
    label2.set(CoreAnnotations.TextAnnotation.class, "York");

    List<CoreLabel> span = Arrays.asList(label1, label2);
    Mention mention = new Mention(2, 0, 2, new SemanticGraph(), span);

    mention.spanToString(); 
    String lc = mention.lowercaseNormalizedSpanString();
    assertEquals("new york", lc);
  }
@Test
  public void testNerTokens_returnsNullOnEmptyOrOTag() {
    Mention mention = new Mention();
    mention.nerString = "O";
    assertNull(mention.nerTokens());

    mention.nerString = null;
    assertNull(mention.nerTokens());
  }
@Test
  public void testNerTokens_returnsMatchingSpan() {
    List<CoreLabel> span = new ArrayList<>();

    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");
    tok1.setNER("PERSON");
    span.add(tok1);

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Doe");
    tok2.setNER("PERSON");
    span.add(tok2);

    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "Ltd");
    tok3.setNER("ORG");
    span.add(tok3);

    Mention mention = new Mention(3, 0, 3, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.nerString = "PERSON";

    List<CoreLabel> nerToks = mention.nerTokens();
    assertNotNull(nerToks);
    assertEquals(2, nerToks.size());
    assertEquals("John", nerToks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Doe", nerToks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNerName_returnsNullIfNEREmpty() {
    Mention mention = new Mention();
    mention.nerString = "O";
    assertNull(mention.nerName());

    mention.nerString = null;
    assertNull(mention.nerName());
  }
@Test
  public void testNerName_returnsStringIfNERPresent() {
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    c1.setNER("PERSON");
    span.add(c1);

    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    c2.setNER("PERSON");
    span.add(c2);

    Mention mention = new Mention(4, 0, 2, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.nerString = "PERSON";

    List<CoreLabel> nerToks = mention.nerTokens();
    assertEquals(2, nerToks.size());

    String result = mention.nerName();
    assertEquals("Barack Obama", result);
  }
@Test
  public void testGetMentionString_returnsTokensUpToHead() {
    List<CoreLabel> span = new ArrayList<>();

    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "President");
    span.add(w1);

    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Barack");
    span.add(w2);

    CoreLabel w3 = new CoreLabel();
    w3.set(CoreAnnotations.TextAnnotation.class, "Obama");
    span.add(w3);

    Mention mention = new Mention(5, 0, 3, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headWord = w2;

    List<String> mentionStr = mention.getMentionString();
    assertEquals(Arrays.asList("president", "barack"), mentionStr);
  }
@Test
  public void testSameSentenceReturnsTrueForSharedSentenceWords() {
    List<CoreLabel> shared = new ArrayList<>();
    CoreLabel l = new CoreLabel();
    l.set(CoreAnnotations.TextAnnotation.class, "Hello");
    shared.add(l);

    Mention m1 = new Mention();
    m1.sentenceWords = shared;

    Mention m2 = new Mention();
    m2.sentenceWords = shared;

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testSameSentenceReturnsFalseForDifferentReferences() {
    Mention m1 = new Mention();
    m1.sentenceWords = new ArrayList<>();

    Mention m2 = new Mention();
    m2.sentenceWords = new ArrayList<>();

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testToStringDelegatesToSpanToString() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention mention = new Mention(6, 0, 1, new SemanticGraph(), Arrays.asList(token));
    String expected = "Obama";
    assertEquals(expected, mention.toString());
  }
@Test
  public void testIsPronominalReturnsTrueForPronounMentionType() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;

    assertTrue(mention.isPronominal());
  }
@Test
  public void testIsPronominalReturnsFalseForNonPronounMentionType() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PROPER;

    assertFalse(mention.isPronominal());
  }
@Test
  public void testIsListMemberOf_returnsFalseIfSameMention() {
    Mention mention = new Mention();
    assertFalse(mention.isListMemberOf(mention));
  }
@Test
  public void testIsListMemberOf_returnsFalseForNonListMention() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.NOMINAL;

    Mention m2 = new Mention();
    m2.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(m1.isListMemberOf(m2));
  }
@Test
  public void testIsTheCommonNoun_positiveCase() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "cat");

    Mention mention = new Mention(7, 0, 2, new SemanticGraph(), Arrays.asList(token));
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    mention.originalSpan = Arrays.asList(token);
    mention.headIndex = 0;
    mention.startIndex = 0;
    mention.spanString = "the cat";

    assertTrue(mention.isTheCommonNoun());
  }
@Test
  public void testIsTheCommonNoun_negativeTooLong() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "big");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "dog");

    Mention mention = new Mention(8, 0, 3, new SemanticGraph(), Arrays.asList(token1, token2, token3));
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    mention.originalSpan = Arrays.asList(token1, token2, token3);
    mention.spanString = "the big dog";

    assertFalse(mention.isTheCommonNoun());
  } 
}