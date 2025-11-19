public class Mention_wosr_5_GPTLLMTest { 

 @Test
  public void testIsPronominalTrueWhenMentionTypeIsPronominal() {
    Mention mention = new Mention();
    mention.mentionType = MentionType.PRONOMINAL;

    assertTrue(mention.isPronominal());
  }
@Test
  public void testIsPronominalFalseWhenMentionTypeIsNominal() {
    Mention mention = new Mention();
    mention.mentionType = MentionType.NOMINAL;

    assertFalse(mention.isPronominal());
  }
@Test
  public void testSpanToStringBuildsAndCachesCorrectString() {
    Mention mention = new Mention();
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    span.add(token1);
    span.add(token2);
    mention.originalSpan = span;

    String result = mention.spanToString();

    assertEquals("Barack Obama", result);

    
    String cachedResult = mention.spanToString();
    assertSame(result, cachedResult);
  }
@Test
  public void testLowercaseNormalizedSpanStringReturnsLowercase() {
    Mention mention = new Mention();
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "University");
    span.add(token1);
    span.add(token2);
    mention.originalSpan = span;

    mention.spanToString(); 
    String normalized = mention.lowercaseNormalizedSpanString();

    assertEquals("stanford university", normalized);
  }
@Test
  public void testNerTokensReturnsCorrectSpan() {
    Mention mention = new Mention();
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    t1.setNER("PERSON");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    t2.setNER("PERSON");
    span.add(t1);
    span.add(t2);
    mention.originalSpan = span;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.nerString = "PERSON";

    List<CoreLabel> tokens = mention.nerTokens();

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("Barack", tokens.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Obama", tokens.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNerTokensReturnsNullWhenNerIsO() {
    Mention mention = new Mention();
    mention.nerString = "O";
    List<CoreLabel> tokens = mention.nerTokens();

    assertNull(tokens);
  }
@Test
  public void testNerNameReturnsCombinedNERText() {
    Mention mention = new Mention();
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    w1.setNER("PERSON");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    w2.setNER("PERSON");
    span.add(w1);
    span.add(w2);
    mention.originalSpan = span;
    mention.startIndex = 0;
    mention.headIndex = 1;
    mention.nerString = "PERSON";

    String result = mention.nerName();

    assertEquals("Barack Obama", result);
  }
@Test
  public void testNerNameReturnsNullIfNerTokensAreNull() {
    Mention mention = new Mention();
    mention.nerString = "O";

    String result = mention.nerName();

    assertNull(result);
  }
@Test
  public void testIncludedInTrueWhenMentionIsSubtreeOfParameter() {
    Tree parentTree = Tree.valueOf("(NP (DT The) (NN president) (NN Obama))");
    Tree childTree = parentTree.getChild(1); 

    Mention m = new Mention();
    m.mentionSubTree = parentTree;

    Mention mention = new Mention();
    mention.mentionSubTree = childTree;
    mention.startIndex = 1;
    mention.endIndex = 2;
    mention.sentenceWords = Collections.emptyList();

    m.startIndex = 0;
    m.endIndex = 3;
    m.sentenceWords = Collections.emptyList();

    assertTrue(mention.includedIn(m));
  }
@Test
  public void testIncludedInFalseWhenDifferentSentences() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentNum = 1;
    m2.sentNum = 2;

    assertFalse(m1.includedIn(m2));
  }
@Test
  public void testSameSentenceTrueWhenSameObject() {
    List<CoreLabel> words = new ArrayList<>();
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentenceWords = words;
    m2.sentenceWords = words;

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testSameSentenceFalseWhenDifferentSentenceObjects() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentenceWords = new ArrayList<>();
    m2.sentenceWords = new ArrayList<>();

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testIsListMemberOfTrueWhenIncludedAndOtherIsList() {
    Mention listMention = new Mention();
    listMention.mentionType = MentionType.LIST;
    listMention.sentenceWords = Collections.emptyList();
    listMention.startIndex = 0;
    listMention.endIndex = 4;

    Mention child = new Mention();
    child.mentionType = MentionType.NOMINAL;
    child.sentenceWords = Collections.emptyList();
    child.startIndex = 1;
    child.endIndex = 2;

    child.mentionSubTree = Tree.valueOf("(NP item)");
    listMention.mentionSubTree = Tree.valueOf("(NP (NP item1) (, ,) (NP item2))");

    assertTrue(child.isListMemberOf(listMention));
  }
@Test
  public void testIsListMemberOfFalseWhenNotIncluded() {
    Mention parent = new Mention();
    parent.mentionType = MentionType.LIST;
    parent.startIndex = 0;
    parent.endIndex = 2;
    parent.sentenceWords = Collections.emptyList();
    parent.mentionSubTree = Tree.valueOf("(NP list)");

    Mention child = new Mention();
    child.mentionType = MentionType.NOMINAL;
    child.startIndex = 3;
    child.endIndex = 4;
    child.sentenceWords = Collections.emptyList();
    child.mentionSubTree = Tree.valueOf("(NP item)");

    assertFalse(child.isListMemberOf(parent));
  }
@Test
  public void testAddListMemberAndBelongsToListWorks() {
    Mention list = new Mention();
    Mention item = new Mention();

    list.addListMember(item);
    item.addBelongsToList(list);

    assertTrue(list.listMembers.contains(item));
    assertTrue(item.belongToLists.contains(list));
  }
@Test
  public void testIsMemberOfSameListTrueWhenBothReferToSameList() {
    Mention list = new Mention();
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.belongToLists = new HashSet<>(Collections.singletonList(list));
    m2.belongToLists = new HashSet<>(Collections.singletonList(list));

    assertTrue(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsMemberOfSameListFalseWhenListsDoNotOverlap() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.belongToLists = new HashSet<>(Collections.singletonList(new Mention()));
    m2.belongToLists = new HashSet<>(Collections.singletonList(new Mention()));

    assertFalse(m1.isMemberOfSameList(m2));
  }
@Test
  public void testAddAppositionAndIsAppositionTrue() {
    Mention main = new Mention();
    Mention app = new Mention();

    main.addApposition(app);

    assertTrue(main.isApposition(app));
  }
@Test
  public void testIsAppositionFalseWhenAppositionNotAdded() {
    Mention main = new Mention();
    Mention app = new Mention();

    assertFalse(main.isApposition(app));
  }
@Test
  public void testAddAndIsPredicateNominativesWork() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addPredicateNominatives(m2);

    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testAddAndIsRelativePronounWork() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addRelativePronoun(m2);

    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testAppearEarlierThanTrueWhenEarlierSentence() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 0;
    m2.sentNum = 1;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierThanFalseWhenLaterSentence() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 2;
    m2.sentNum = 1;

    assertFalse(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierThanTrueWhenSameSentenceButEarlierStartIndex() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 0;
    m2.sentNum = 0;
    m1.startIndex = 3;
    m2.startIndex = 4;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testStringWithoutArticleRemovesDefiniteArticle() {
    Mention mention = new Mention();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "President");
    mention.originalSpan = Arrays.asList(token1, token2);

    String result = mention.stringWithoutArticle(null);

    assertEquals("President", result);
  }
@Test
  public void testIsTheCommonNounTrue() {
    Mention mention = new Mention();
    mention.mentionType = MentionType.NOMINAL;

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "the");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "car");

    mention.originalSpan = Arrays.asList(token1, token2);

    mention.spanToString(); 

    assertTrue(mention.isTheCommonNoun());
  }
@Test
  public void testIsTheCommonNounFalseWhenNotStartingWithThe() {
    Mention mention = new Mention();
    mention.mentionType = MentionType.NOMINAL;

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "somebody");

    mention.originalSpan = Collections.singletonList(token);

    mention.spanToString();

    assertFalse(mention.isTheCommonNoun());
  } 
}