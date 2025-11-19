public class Mention_wosr_1_GPTLLMTest { 

 @Test
  public void testSpanToString_basicSpan() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);

    Mention mention = new Mention(1, 0, 2, new SemanticGraph(), span);
    String spanStr = mention.spanToString();
    assertEquals("Barack Obama", spanStr);
  }
@Test
  public void testLowercaseNormalizedSpanString() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "President");

    List<CoreLabel> span = Arrays.asList(word1, word2);
    Mention mention = new Mention(1, 0, 2, new SemanticGraph(), span);
    assertEquals("the president", mention.lowercaseNormalizedSpanString());
  }
@Test
  public void testNerTokens_whenMatchingNERSpan() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    word1.setNER("PERSON");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    word2.setNER("PERSON");

    List<CoreLabel> span = Arrays.asList(word1, word2);

    Mention mention = new Mention(2, 0, 2, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.nerString = "PERSON";

    List<CoreLabel> nerTokens = mention.nerTokens();
    assertNotNull(nerTokens);
    assertEquals(2, nerTokens.size());
    assertEquals("Barack", nerTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Obama", nerTokens.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNerTokens_whenNoNERTag() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "the");
    word1.setNER("O");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "book");
    word2.setNER("O");

    List<CoreLabel> span = Arrays.asList(word1, word2);

    Mention mention = new Mention(3, 0, 2, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.nerString = "O";

    List<CoreLabel> nerTokens = mention.nerTokens();
    assertNull(nerTokens);
  }
@Test
  public void testNerName_withNER() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "New");
    word1.setNER("LOCATION");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "York");
    word2.setNER("LOCATION");

    List<CoreLabel> span = Arrays.asList(word1, word2);

    Mention mention = new Mention(4, 0, 2, new SemanticGraph(), span);
    mention.originalSpan = span;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.nerString = "LOCATION";

    String nerName = mention.nerName();
    assertEquals("New York", nerName);
  }
@Test
  public void testToStringDelegatesToSpanToString() {
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "test");

    List<CoreLabel> span = Collections.singletonList(word);
    Mention mention = new Mention(5, 0, 1, new SemanticGraph(), span);
    String str = mention.toString();
    assertEquals("test", str);
  }
@Test
  public void testIsPronominal_trueWhenTypeIsPronominal() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    assertTrue(mention.isPronominal());
  }
@Test
  public void testIsPronominal_falseWhenTypeIsNotPronominal() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PROPER;
    assertFalse(mention.isPronominal());
  }
@Test
  public void testIsListMemberOf_trueWhenNestedProperly() {
    Mention listMention = new Mention();
    listMention.mentionType = Dictionaries.MentionType.LIST;
    listMention.startIndex = 2;
    listMention.endIndex = 5;
    listMention.mentionSubTree = Tree.valueOf("(NP (NP item1) (CC and) (NP item2))");

    Mention inner = new Mention();
    inner.mentionType = Dictionaries.MentionType.NOMINAL;
    inner.startIndex = 2;
    inner.endIndex = 3;
    inner.mentionSubTree = Tree.valueOf("(NP item1)");

    listMention.sentenceWords = new ArrayList<>();
    inner.sentenceWords = listMention.sentenceWords;

    assertTrue(inner.isListMemberOf(listMention));
  }
@Test
  public void testIsListMemberOf_falseWhenNotIncluded() {
    Mention outer = new Mention();
    outer.mentionType = Dictionaries.MentionType.LIST;
    outer.startIndex = 0;
    outer.endIndex = 3;

    Mention inner = new Mention();
    inner.mentionType = Dictionaries.MentionType.LIST;
    inner.startIndex = 0;
    inner.endIndex = 3;

    assertFalse(inner.isListMemberOf(outer));
  }
@Test
  public void testIsApposition_trueWhenMentionInSet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.appositions = new HashSet<>();
    m1.appositions.add(m2);

    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testIsApposition_falseWhenMentionNotInSet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.appositions = new HashSet<>();

    assertFalse(m1.isApposition(m2));
  }
@Test
  public void testIsPredicateNominatives_trueWhenMentionInSet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.predicateNominatives = new HashSet<>();
    m1.predicateNominatives.add(m2);

    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testIsPredicateNominatives_falseWhenMentionNotInSet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.predicateNominatives = new HashSet<>();

    assertFalse(m1.isPredicateNominatives(m2));
  }
@Test
  public void testIsRelativePronoun_trueWhenInSet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.relativePronouns = new HashSet<>();
    m1.relativePronouns.add(m2);

    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testIsRelativePronoun_falseWhenNotInSet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.relativePronouns = new HashSet<>();

    assertFalse(m1.isRelativePronoun(m2));
  }
@Test
  public void testSameSentence_true() {
    List<CoreLabel> dummySentence = new ArrayList<>();
    Mention m1 = new Mention();
    m1.sentenceWords = dummySentence;

    Mention m2 = new Mention();
    m2.sentenceWords = dummySentence;

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testSameSentence_false() {
    Mention m1 = new Mention();
    m1.sentenceWords = new ArrayList<>();

    Mention m2 = new Mention();
    m2.sentenceWords = new ArrayList<>();

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testAppearEarlierThan_sentOrder() {
    Mention earlier = new Mention();
    earlier.sentNum = 1;

    Mention later = new Mention();
    later.sentNum = 2;

    assertTrue(earlier.appearEarlierThan(later));
  }
@Test
  public void testAppearEarlierThan_startIndexOrder() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 1;

    Mention m2 = new Mention();
    m2.sentNum = 1;
    m2.startIndex = 2;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierThan_falseWhenLaterStartIndex() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 2;

    Mention m2 = new Mention();
    m2.sentNum = 1;
    m2.startIndex = 1;

    assertFalse(m1.appearEarlierThan(m2));
  } 
}