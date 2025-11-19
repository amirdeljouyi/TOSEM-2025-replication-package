public class Mention_wosr_4_GPTLLMTest { 

 @Test
  public void testDefaultConstructor() {
    Mention mention = new Mention();
    assertNotNull(mention);
    assertEquals(-1, mention.mentionID);
  }
@Test
  public void testSpanToStringWithSimpleTokens() {
    Mention mention = new Mention();
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);
    mention.originalSpan = span;

    String result = mention.spanToString();
    assertEquals("Barack Obama", result);
  }
@Test
  public void testSpanToStringMemoization() {
    Mention mention = new Mention();
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "University");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);
    mention.originalSpan = span;

    String firstCall = mention.spanToString();
    assertEquals("Stanford University", firstCall);

    word1.set(CoreAnnotations.TextAnnotation.class, "Changed");

    String secondCall = mention.spanToString();
    assertEquals("Stanford University", secondCall); 
  }
@Test
  public void testLowercaseNormalizedSpanString() {
    Mention mention = new Mention();
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "NASA");

    mention.originalSpan = Collections.singletonList(word);
    mention.spanToString();
    String result = mention.lowercaseNormalizedSpanString();

    assertEquals("nasa", result);
  }
@Test
  public void testIsPronominalTrue() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    assertTrue(mention.isPronominal());
  }
@Test
  public void testIsPronominalFalse() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    assertFalse(mention.isPronominal());
  }
@Test
  public void testToStringDelegatesToSpanToString() {
    Mention mention = new Mention();
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "President");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word);
    mention.originalSpan = span;

    String text = mention.toString();
    assertEquals("President", text);
  }
@Test
  public void testIncludedInTrue() {
    Mention outer = new Mention();
    Mention inner = new Mention();

    outer.sentenceWords = new ArrayList<>();
    inner.sentenceWords = outer.sentenceWords;

    Tree root = new Tree("NP");
    Tree innerTree = new Tree("NN");
    root.setChildren(new Tree[]{innerTree});
    outer.mentionSubTree = root;
    inner.mentionSubTree = innerTree;

    outer.startIndex = 0;
    outer.endIndex = 3;
    inner.startIndex = 1;
    inner.endIndex = 2;

    assertTrue(inner.includedIn(outer));
  }
@Test
  public void testIncludedInFalseDifferentSentence() {
    Mention a = new Mention();
    Mention b = new Mention();

    a.sentenceWords = new ArrayList<>();
    b.sentenceWords = new ArrayList<>();

    assertFalse(a.includedIn(b));
  }
@Test
  public void testIsListMemberOfTrue() {
    Mention list = new Mention();
    Mention member = new Mention();

    list.mentionType = Dictionaries.MentionType.LIST;
    member.mentionType = Dictionaries.MentionType.NOMINAL;
    list.sentenceWords = new ArrayList<>();
    member.sentenceWords = list.sentenceWords;

    Tree root = new Tree("NP");
    Tree memberTree = new Tree("NN");
    root.setChildren(new Tree[]{memberTree});
    list.mentionSubTree = root;
    member.mentionSubTree = memberTree;

    list.startIndex = 0;
    list.endIndex = 5;
    member.startIndex = 1;
    member.endIndex = 4;

    assertTrue(member.isListMemberOf(list));
  }
@Test
  public void testIsListMemberOfFalseWhenEqual() {
    Mention mention = new Mention();
    assertFalse(mention.isListMemberOf(mention));
  }
@Test
  public void testAddListMember() {
    Mention list = new Mention();
    Mention member = new Mention();
    list.addListMember(member);
    assertNotNull(list.listMembers);
    assertTrue(list.listMembers.contains(member));
  }
@Test
  public void testAddBelongsToList() {
    Mention member = new Mention();
    Mention list = new Mention();
    member.addBelongsToList(list);
    assertNotNull(member.belongToLists);
    assertTrue(member.belongToLists.contains(list));
  }
@Test
  public void testIsMemberOfSameListTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    Mention list = new Mention();

    m1.belongToLists = new HashSet<>();
    m2.belongToLists = new HashSet<>();
    m1.belongToLists.add(list);
    m2.belongToLists.add(list);

    assertTrue(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsMemberOfSameListFalse() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.belongToLists = new HashSet<>();
    b.belongToLists = new HashSet<>();

    Mention listA = new Mention();
    Mention listB = new Mention();
    a.belongToLists.add(listA);
    b.belongToLists.add(listB);

    assertFalse(a.isMemberOfSameList(b));
  }
@Test
  public void testAddApposition() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addApposition(m2);
    assertNotNull(m1.appositions);
    assertTrue(m1.appositions.contains(m2));
  }
@Test
  public void testIsApposition() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addApposition(m2);
    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testIsAppositionFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    assertFalse(m1.isApposition(m2));
  }
@Test
  public void testAddPredicateNominatives() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.addPredicateNominatives(b);
    assertNotNull(a.predicateNominatives);
    assertTrue(a.predicateNominatives.contains(b));
  }
@Test
  public void testIsPredicateNominativesTrue() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.addPredicateNominatives(b);
    assertTrue(a.isPredicateNominatives(b));
  }
@Test
  public void testIsPredicateNominativesFalse() {
    Mention a = new Mention();
    Mention b = new Mention();
    assertFalse(a.isPredicateNominatives(b));
  }
@Test
  public void testAppearEarlierThanTrueDifferentSentence() {
    Mention earlier = new Mention();
    earlier.sentNum = 1;
    earlier.startIndex = 2;

    Mention later = new Mention();
    later.sentNum = 2;
    later.startIndex = 0;

    assertTrue(earlier.appearEarlierThan(later));
  }
@Test
  public void testAppearEarlierThanTrueSameSentence() {
    Mention earlier = new Mention();
    earlier.sentNum = 1;
    earlier.startIndex = 1;
    earlier.endIndex = 2;
    earlier.headIndex = 3;

    Mention later = new Mention();
    later.sentNum = 1;
    later.startIndex = 2;
    later.endIndex = 3;
    later.headIndex = 4;

    assertTrue(earlier.appearEarlierThan(later));
  }
@Test
  public void testMoreRepresentativeThanTrueHigherType() {
    Mention nominal = new Mention();
    nominal.mentionType = Dictionaries.MentionType.NOMINAL;

    Mention pronoun = new Mention();
    pronoun.mentionType = Dictionaries.MentionType.PRONOMINAL;

    assertTrue(nominal.moreRepresentativeThan(pronoun));
  }
@Test
  public void testSameSentenceTrue() {
    Mention a = new Mention();
    Mention b = new Mention();
    List<CoreLabel> sentence = new ArrayList<>();
    a.sentenceWords = sentence;
    b.sentenceWords = sentence;
    assertTrue(a.sameSentence(b));
  }
@Test
  public void testSameSentenceFalse() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.sentenceWords = new ArrayList<>();
    b.sentenceWords = new ArrayList<>();
    assertFalse(a.sameSentence(b));
  } 
}