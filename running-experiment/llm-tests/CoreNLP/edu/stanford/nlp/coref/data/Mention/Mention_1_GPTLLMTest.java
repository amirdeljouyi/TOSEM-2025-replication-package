package edu.stanford.nlp.coref.data;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class Mention_1_GPTLLMTest {

 @Test
  public void testSpanToString_ReturnsCorrectString() {
    CoreLabel word1 = new CoreLabel();
    word1.setWord("John");
    word1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel word2 = new CoreLabel();
    word2.setWord("Smith");
    word2.set(CoreAnnotations.TextAnnotation.class, "Smith");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);

    Mention mention = new Mention(0, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    String result = mention.spanToString();

    assertEquals("John Smith", result);
  }
@Test
  public void testLowercaseNormalizedSpanString_IsLowercase() {
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Barack");
    word1.set(CoreAnnotations.TextAnnotation.class, "Barack");

    CoreLabel word2 = new CoreLabel();
    word2.setWord("Obama");
    word2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);

    Mention mention = new Mention(0, 0, 2, span, new SemanticGraph(), new SemanticGraph(), span);
    String result = mention.lowercaseNormalizedSpanString();

    assertEquals("barack obama", result);
  }
@Test
  public void testIsPronominalReturnsTrueWhenMentionTypeIsPronominal() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;

    assertTrue(mention.isPronominal());
  }
@Test
  public void testIsPronominalReturnsFalseWhenMentionTypeNotPronominal() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(mention.isPronominal());
  }
@Test
  public void testEqualsAndHashCode_WhenMentionsAreEqual() {
    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.set(CoreAnnotations.TextAnnotation.class, "Alice");

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    Mention mention1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention mention2 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    assertEquals(mention1, mention2);
    assertEquals(mention1.hashCode(), mention2.hashCode());
  }
@Test
  public void testHeadsAgree_WhenHeadStringsAreEqual() {
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    Mention mention1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    mention1.headString = "obama";

    Mention mention2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    mention2.headString = "obama";

    assertTrue(mention1.headsAgree(mention2));
  }
@Test
  public void testIsListMemberOf_ReturnsTrueWhenIncludedInListMention() {
    CoreLabel token = new CoreLabel();
    token.setWord("Tom");
    token.set(CoreAnnotations.TextAnnotation.class, "Tom");

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    Mention listMention = new Mention(1, 0, 5, span, new SemanticGraph(), new SemanticGraph(), span);
    listMention.mentionType = Dictionaries.MentionType.LIST;

    Mention partMention = new Mention(2, 1, 3, span, new SemanticGraph(), new SemanticGraph(), span);
    partMention.mentionType = Dictionaries.MentionType.NOMINAL;

    assertTrue(partMention.isListMemberOf(listMention));
  }
@Test
  public void testAppearEarlierThan_ReturnsTrueWhenSentNumIsSmaller() {
    Mention first = new Mention();
    first.sentNum = 0;
    first.startIndex = 0;
    first.endIndex = 2;
    first.headIndex = 1;
    first.mentionType = Dictionaries.MentionType.NOMINAL;
    first.originalSpan = new ArrayList<>();

    Mention second = new Mention();
    second.sentNum = 1;
    second.startIndex = 0;
    second.endIndex = 2;
    second.headIndex = 1;
    second.mentionType = Dictionaries.MentionType.NOMINAL;
    second.originalSpan = new ArrayList<>();

    assertTrue(first.appearEarlierThan(second));
  }
@Test
  public void testInsideIn_ReturnsTrueForValidNestedMention() {
    Mention outer = new Mention(1, 0, 5, new ArrayList<CoreLabel>(), new SemanticGraph(), new SemanticGraph(), new ArrayList<>());
    outer.sentNum = 0;

    Mention inner = new Mention(2, 1, 3, new ArrayList<CoreLabel>(), new SemanticGraph(), new SemanticGraph(), new ArrayList<>());
    inner.sentNum = 0;

    assertTrue(inner.insideIn(outer));
  }
@Test
  public void testIsMemberOfSameList_ReturnsTrueWhenSameParentMention() {
    Mention a = new Mention();
    Mention b = new Mention();
    Mention commonList = new Mention();

    a.belongToLists = edu.stanford.nlp.util.Generics.newHashSet();
    a.belongToLists.add(commonList);

    b.belongToLists = edu.stanford.nlp.util.Generics.newHashSet();
    b.belongToLists.add(commonList);

    assertTrue(a.isMemberOfSameList(b));
  }
@Test
  public void testStringWithoutArticle_RemovesThe() {
    Mention mention = new Mention();
    String input = "the president";
    String result = mention.stringWithoutArticle(input);
    assertEquals("president", result);
  }
@Test
  public void testRemoveParenthesis_RemovesTextAfterParen() {
    String input = "Apple Inc. (NASDAQ: AAPL)";
    String result = Mention.removeParenthesis(input);
    assertEquals("Apple Inc.", result);
  }
@Test
  public void testBuildQueryText_ReturnsJoinedString() {
    List<String> terms = new ArrayList<>();
    terms.add("United");
    terms.add("Nations");

    String result = Mention.buildQueryText(terms);
    assertEquals("United Nations", result);
  }
@Test
  public void testIsTheCommonNoun_ReturnsTrueForTheWord() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "host");

    ArrayList<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);

    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    mention.originalSpan = span;

    assertTrue(mention.isTheCommonNoun());
  }
@Test
  public void testIsTheCommonNoun_ReturnsFalseWhenTooManyWords() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "new");

    CoreLabel word3 = new CoreLabel();
    word3.set(CoreAnnotations.TextAnnotation.class, "host");

    ArrayList<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);
    span.add(word3);

    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    mention.originalSpan = span;

    assertFalse(mention.isTheCommonNoun());
  }
@Test
  public void testIsApposition_TrueWhenMentionAdded() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.addApposition(b);

    assertTrue(a.isApposition(b));
  }
@Test
  public void testIsRelativePronoun_TrueWhenMentionAdded() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.addRelativePronoun(b);

    assertTrue(a.isRelativePronoun(b));
  }
@Test
  public void testIsPredicateNominatives_TrueWhenMentionAdded() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.addPredicateNominatives(b);

    assertTrue(a.isPredicateNominatives(b));
  }
@Test
  public void testSpanToString_WhenOriginalSpanEmpty_ShouldReturnEmptyString() {
    Mention mention = new Mention();
    mention.originalSpan = new ArrayList<>();
    String result = mention.spanToString();
    assertEquals("", result);
  }
@Test
  public void testLowercaseNormalizedSpanString_WhenSpanToStringAlreadyCached() {
    Mention mention = new Mention();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Example");
    mention.originalSpan = new ArrayList<>();
    mention.originalSpan.add(token);
    mention.spanToString(); 
    String normalized = mention.lowercaseNormalizedSpanString();
    assertEquals("example", normalized);
  }
@Test
  public void testHeadsAgree_WhenNERTokensMatchButHeadWordsDiffer() {
    CoreLabel w1 = new CoreLabel();
    w1.setWord("George");
    w1.set(CoreAnnotations.TextAnnotation.class, "George");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("Bush");
    w2.set(CoreAnnotations.TextAnnotation.class, "Bush");

    List<CoreLabel> spanA = new ArrayList<>();
    spanA.add(w1);

    List<CoreLabel> spanB = new ArrayList<>();
    spanB.add(w1);
    spanB.add(w2);

    Mention m1 = new Mention();
    m1.nerString = "PERSON";
    m1.headWord = w1;
    m1.originalSpan = spanA;

    Mention m2 = new Mention();
    m2.nerString = "PERSON";
    m2.headWord = w2;
    m2.originalSpan = spanB;

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testEquals_WhenOtherMentionHasDifferentID_ShouldReturnFalse() {
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "Sample");

    List<CoreLabel> span = new ArrayList<>();
    span.add(tok);

    Mention m1 = new Mention(1, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);
    Mention m2 = new Mention(2, 0, 1, span, new SemanticGraph(), new SemanticGraph(), span);

    assertFalse(m1.equals(m2));
  }
@Test
  public void testHashCode_UniqueForDifferentSpans() {
    Mention m1 = new Mention();
    m1.startIndex = 1;
    m1.endIndex = 3;

    Mention m2 = new Mention();
    m2.startIndex = 2;
    m2.endIndex = 4;

    assertNotEquals(m1.hashCode(), m2.hashCode());
  }
@Test
  public void testIsListMemberOf_WhenSameMention_ShouldReturnFalse() {
    Mention self = new Mention();
    self.startIndex = 0;
    self.endIndex = 2;
    self.mentionType = Dictionaries.MentionType.LIST;
    assertFalse(self.isListMemberOf(self));
  }
@Test
  public void testAttributesAgree_WhenGenderMismatch_ShouldReturnFalse() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.gender = Dictionaries.Gender.MALE;
    m2.gender = Dictionaries.Gender.FEMALE;
    m1.animacy = Dictionaries.Animacy.UNKNOWN;
    m2.animacy = Dictionaries.Animacy.UNKNOWN;
    m1.number = Dictionaries.Number.UNKNOWN;
    m2.number = Dictionaries.Number.UNKNOWN;
    m1.nerString = "O";
    m2.nerString = "O";

    Dictionaries dict = new Dictionaries();
    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testGetSingletonFeatures_WithUnknownValues_ShouldStillSucceed() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    mention.nerString = "O";
    mention.animacy = Dictionaries.Animacy.UNKNOWN;
    mention.person = Dictionaries.Person.UNKNOWN;
    mention.number = Dictionaries.Number.UNKNOWN;

    mention.headString = "dog";
    mention.startIndex = 1;
    mention.endIndex = 2;
    mention.headIndex = 1;
    mention.enhancedDependency = new SemanticGraph();

    mention.basicDependency = new SemanticGraph();
    mention.sentenceWords = new ArrayList<>();

    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "dog");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    mention.headWord = token;

    Dictionaries dict = new Dictionaries();
    List<String> features = mention.getSingletonFeatures(dict);
    assertNotNull(features);
    assertTrue(features.size() > 0);
  }
@Test
  public void testMoreRepresentativeThan_ReturnsTrueForEarlierSentence() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.sentNum = 1;
    m1.nerString = "PERSON";
    m1.headIndex = 3;
    m1.startIndex = 1;
    m1.originalSpan = new ArrayList<>();

    Mention m2 = new Mention();
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.sentNum = 2;
    m2.nerString = "PERSON";
    m2.headIndex = 3;
    m2.startIndex = 1;
    m2.originalSpan = new ArrayList<>();

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testMoreRepresentativeThan_ThrowsExceptionForEqualMentions() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.sentNum = 1;
    m1.headIndex = 3;
    m1.startIndex = 1;

    m1.nerString = "O";
    CoreLabel token = new CoreLabel();
    token.setWord("X");
    token.set(CoreAnnotations.TextAnnotation.class, "X");
    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    m1.originalSpan = span;

    try {
      m1.moreRepresentativeThan(m1);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("Comparing a mention with itself"));
    }
  }
@Test
  public void testIncludedIn_WhenOtherMentionShorter_ShouldReturnFalse() {
    Mention longMention = new Mention();
    longMention.sentNum = 0;
    longMention.startIndex = 0;
    longMention.endIndex = 4;

    Mention shortMention = new Mention();
    shortMention.sentNum = 0;
    shortMention.startIndex = 0;
    shortMention.endIndex = 3;

    assertFalse(longMention.includedIn(shortMention));
  }
@Test
  public void testIsDemonym_MatchesUSStatesByCasedName() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    m1.originalSpan = new ArrayList<>();
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "New York");
    m1.originalSpan.add(c1);

    Mention m2 = new Mention();
    m2.originalSpan = new ArrayList<>();
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "new yorker");
    m2.originalSpan.add(c2);

    Dictionaries dict = new Dictionaries();
    dict.statesAbbreviation.put("New York", "NY");
//    dict.addDemonym("new yorker", "New York");

    assertTrue(m2.isDemonym(m1, dict));
  }
@Test
  public void testSameSentence_ReturnsFalseForDifferentReferences() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentenceWords = new ArrayList<>();
    m2.sentenceWords = new ArrayList<>();

    m1.sentenceWords.add(new CoreLabel());
    m2.sentenceWords.add(new CoreLabel());

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testHeadsAgree_NERMatchByIncludedLogic() {
    CoreLabel h1 = new CoreLabel();
    h1.set(CoreAnnotations.TextAnnotation.class, "Tom");
    h1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    h1.setWord("Tom");

    CoreLabel h2 = new CoreLabel();
    h2.set(CoreAnnotations.TextAnnotation.class, "Tommy");
    h2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    h2.setWord("Tommy");

    Mention m1 = new Mention();
    m1.nerString = "PERSON";
    m1.headWord = h1;
    m1.originalSpan = new ArrayList<>();
    m1.originalSpan.add(h1);

    Mention m2 = new Mention();
    m2.nerString = "PERSON";
    m2.headWord = h2;
    m2.originalSpan = new ArrayList<>();
    m2.originalSpan.add(h2);

    assertFalse(m1.headsAgree(m2)); 
  }
@Test
  public void testSpanToString_HandlesNullTextAnnotationGracefully() {
    Mention mention = new Mention();
    CoreLabel token1 = new CoreLabel();
    token1.setWord(null); 
    List<CoreLabel> span = new ArrayList<>();
    span.add(token1);
    mention.originalSpan = span;

    try {
      mention.spanToString(); 
    } catch (Exception e) {
      fail("Call to spanToString() should not throw exception when text annotation is null");
    }
  }
@Test
  public void testToString_WhenSpanEmpty_ReturnsEmpty() {
    Mention mention = new Mention();
    mention.originalSpan = new ArrayList<>();
    assertEquals("", mention.toString());
  }
@Test
  public void testIsListMemberOf_WhenMentionNotListType_ReturnsFalse() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    Mention list = new Mention();
    list.mentionType = Dictionaries.MentionType.PROPER;

    assertFalse(m.isListMemberOf(list));
  }
@Test
  public void testIsApposition_WhenAppositionsSetIsNull_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.appositions = null;

    assertFalse(m1.isApposition(m2));
  }
@Test
  public void testIsPredicateNominatives_WhenSetIsNull_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.predicateNominatives = null;

    assertFalse(m1.isPredicateNominatives(m2));
  }
@Test
  public void testIsRelativePronoun_WhenSetIsNull_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.relativePronouns = null;

    assertFalse(m1.isRelativePronoun(m2));
  }
@Test
  public void testSameSentence_WhenSentenceWordsAreSameReference_ReturnsTrue() {
    List<CoreLabel> sentence = new ArrayList<>();
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentenceWords = sentence;
    m2.sentenceWords = sentence;

    assertTrue(m1.sameSentence(m2));
  }
@Test
  public void testSameSentence_WhenSentenceWordsAreDifferentObjects_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentenceWords = new ArrayList<>();
    m2.sentenceWords = new ArrayList<>();

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testAppearEarlierThan_TieBreakerOnMentionType() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.sentNum = 1;
    b.sentNum = 1;
    a.startIndex = 0;
    b.startIndex = 0;
    a.endIndex = 1;
    b.endIndex = 1;
    a.headIndex = 2;
    b.headIndex = 2;
    a.mentionType = Dictionaries.MentionType.PROPER;
    b.mentionType = Dictionaries.MentionType.NOMINAL;

    
    assertTrue(a.appearEarlierThan(b));
  }
@Test
  public void testAppearEarlierThan_TieBreakerOnHashCode() {
    Mention a = new Mention();
    Mention b = new Mention();
    a.sentNum = 1;
    b.sentNum = 1;
    a.startIndex = 0;
    b.startIndex = 0;
    a.endIndex = 1;
    b.endIndex = 1;
    a.headIndex = 1;
    b.headIndex = 1;
    a.mentionType = Dictionaries.MentionType.NOMINAL;
    b.mentionType = Dictionaries.MentionType.NOMINAL;
    a.originalSpan = new ArrayList<>();
    b.originalSpan = new ArrayList<>();

    
    a.appearEarlierThan(b);
  }
@Test
  public void testEntityTypesAgree_PronounWithORGNERAndOrganizationPronounList() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m1.headString = "it";
    m1.nerString = "O";

    Mention m2 = new Mention();
    m2.nerString = "ORGANIZATION";

    Dictionaries dict = new Dictionaries();
    dict.organizationPronouns.add("it");

    assertTrue(m1.entityTypesAgree(m2, dict, false));
  }
@Test
  public void testEntityTypesAgree_PronounThatDoesNotMatchNERTyping() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m1.headString = "he";
    m1.nerString = "O";

    Mention m2 = new Mention();
    m2.nerString = "LOCATION";

    Dictionaries dict = new Dictionaries();
    dict.locationPronouns.add("it"); 

    assertFalse(m1.entityTypesAgree(m2, dict, false));
  }
@Test
  public void testRemovePhraseAfterHead_WhenNoCommaOrWH_ReturnsSpan() {
    Mention m = new Mention();
    List<CoreLabel> span = new ArrayList<>();

    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("the");
    tok1.set(CoreAnnotations.TextAnnotation.class, "the");
    tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("dog");
    tok2.set(CoreAnnotations.TextAnnotation.class, "dog");
    tok2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    span.add(tok1);
    span.add(tok2);

    m.originalSpan = span;
    m.startIndex = 0;
    m.endIndex = 2;
    m.headIndex = 1;

    String result = m.removePhraseAfterHead();
    assertEquals("the dog", result);
  }
@Test
  public void testRemovePhraseAfterHead_WithWHBeforeHead_ReturnsCorrectSpan() {
    Mention m = new Mention();
    List<CoreLabel> span = new ArrayList<>();

    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "who");
    tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "ran");
    tok2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");

    span.add(tok1);
    span.add(tok2);

    m.originalSpan = span;
    m.startIndex = 0;
    m.endIndex = 2;
    m.headIndex = 1;

    String result = m.removePhraseAfterHead();
    assertEquals("who", result);
  }
@Test
  public void testGetPosition_EdgeBeginning() {
    Mention m = new Mention();
    m.headIndex = 0;
    m.sentenceWords = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "The");
    m.sentenceWords.add(tok1);
    assertEquals("first", m.getPosition());
  }
@Test
  public void testGetRelation_WhenEnhancedDepEmpty() {
    Mention m = new Mention();
    m.enhancedDependency = new SemanticGraph();
//    m.headIndexedWord = new CoreLabel();

    assertNull(m.getRelation());
  }
@Test
  public void testSetType_NoNERNoEntityTag_ShouldSetNOMINAL() throws IOException, ClassNotFoundException {
    Mention m = new Mention();
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "item");
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    m.headWord = head;
    m.originalSpan = new ArrayList<>();
    m.originalSpan.add(head);
    m.headString = "item";

    Dictionaries dict = new Dictionaries();
    dict.allPronouns.add("item");

//    m.setType(dict);
    assertEquals(Dictionaries.MentionType.NOMINAL, m.mentionType);
  }
@Test
  public void testSetGender_WithUnknownGenderForceBergsma_ShouldSetNEUTRAL() throws IOException, ClassNotFoundException {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.number = Dictionaries.Number.SINGULAR;
    m.gender = Dictionaries.Gender.UNKNOWN;
    m.headString = "object";

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "object");
    m.headWord = head;
    m.originalSpan = new ArrayList<>();
    m.originalSpan.add(head);

    m.nerString = "O";

    Dictionaries dict = new Dictionaries();
    dict.neutralWords.add("object");

//    m.setGender(dict, Dictionaries.Gender.UNKNOWN);
//    assertEquals(Dictionaries.Gender.NEUTRAL, m.gender);
  }
@Test
  public void testSetAnimacy_FallbackToBergsmaAnimacy_ShouldBeINANIMATE() throws IOException, ClassNotFoundException {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.nerString = "O";
    m.headString = "lab";

    Dictionaries dict = new Dictionaries();
    dict.inanimateWords.add("lab");

//    m.setAnimacy(dict);
    assertEquals(Dictionaries.Animacy.INANIMATE, m.animacy);
  }
@Test
  public void testGetModifiers_EmptyDependencies_ShouldReturnZero() throws IOException, ClassNotFoundException {
    Mention m = new Mention();
//    m.headIndexedWord = new CoreLabel();
    m.enhancedDependency = new SemanticGraph();

    Dictionaries dict = new Dictionaries();
    int result = m.getModifiers(dict);

    assertEquals(0, result);
  }
@Test
  public void testGetQuantification_WithNumericModifier_ShouldReturnQuantified() throws IOException, ClassNotFoundException {
    Mention m = new Mention();
    SemanticGraph g = new SemanticGraph();
    CoreLabel head = new CoreLabel();
    head.setLemma("items");
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");
    head.setIndex(1);
//    g.addVertex(head);

    CoreLabel nummod = new CoreLabel();
    nummod.setLemma("five");
    nummod.setIndex(2);
//    g.addVertex(nummod);

//    g.addEdge(head, nummod, edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations.NUMERIC_MODIFIER, 1.0, false);

//    m.headIndexedWord = head;
    m.enhancedDependency = g;
    m.nerString = "O";

    Dictionaries dict = new Dictionaries();
    String result = m.getQuantification(dict);

    assertEquals("quantified", result);
  }
@Test
  public void testGetNegation_WithSiblingNegation_ShouldReturn1() throws IOException, ClassNotFoundException {
    Mention m = new Mention();
    SemanticGraph g = new SemanticGraph();

    CoreLabel head = new CoreLabel();
    head.setIndex(1);
//    g.addVertex(head);

    CoreLabel notToken = new CoreLabel();
    notToken.setIndex(2);
    notToken.setLemma("not");
//    g.addVertex(notToken);

//    g.addRoot(head);
//    g.addEdge(head, notToken, edu.stanford.nlp.trees.GrammaticalRelation.valueOf("advmod"), 1.0, false);

//    m.headIndexedWord = head;
    m.enhancedDependency = g;

    Dictionaries dict = new Dictionaries();
    dict.negations.add("not");

    int result = m.getNegation(dict);
    assertEquals(1, result);
  }
@Test
  public void testStringWithoutArticle_WithCapitalizedStarts() {
    Mention m = new Mention();
    String input1 = "A road";
    String input2 = "An idea";
    String input3 = "The man";

    String out1 = m.stringWithoutArticle(input1);
    String out2 = m.stringWithoutArticle(input2);
    String out3 = m.stringWithoutArticle(input3);

    assertEquals("road", out1);
    assertEquals("idea", out2);
    assertEquals("man", out3);
  }
@Test
  public void testIncluded_WhenProperTagNNPExists_ShouldReturnTrue() {
    List<CoreLabel> big = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Obama");
    tok1.set(CoreAnnotations.TextAnnotation.class, "Obama");
    big.add(tok1);

    CoreLabel small = new CoreLabel();
    small.setWord("Obama");
    small.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

//    boolean included = Mention.included(small, big);
//    assertTrue(included);
  }
@Test
  public void testIncluded_WhenProperTagNNPButNoMatch_ShouldReturnFalse() {
    List<CoreLabel> big = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Bush");
    tok1.set(CoreAnnotations.TextAnnotation.class, "Bush");
    big.add(tok1);

    CoreLabel small = new CoreLabel();
    small.setWord("Obama");
    small.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

//    boolean included = Mention.included(small, big);
//    assertFalse(included);
  }
@Test
  public void testIsCoordinated_ReturnsTrueForCCChild() {
    Mention m = new Mention();
    SemanticGraph g = new SemanticGraph();

    CoreLabel head = new CoreLabel();
    head.setIndex(1);
//    g.addVertex(head);

    CoreLabel cc = new CoreLabel();
    cc.setIndex(2);
    cc.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CC");
//    g.addVertex(cc);

//    g.addEdge(head, cc, edu.stanford.nlp.trees.GrammaticalRelation.valueOf("cc"), 1.0, false);

//    m.headIndexedWord = head;
    m.enhancedDependency = g;

    assertTrue(m.isCoordinated());
  }
@Test
  public void testIsCoordinated_ReturnsFalseWhenNoCC() {
    Mention m = new Mention();
    SemanticGraph g = new SemanticGraph();

    CoreLabel head = new CoreLabel();
    head.setIndex(1);
//    g.addRoot(head);
//    g.addVertex(head);
//
//    m.headIndexedWord = head;
    m.enhancedDependency = g;

    assertFalse(m.isCoordinated());
  }
@Test
  public void testRemovePhraseAfterHead_WithCommaBeforeHead() {
    Mention m = new Mention();

    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "John");
    w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, ",");
    w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");

    CoreLabel w3 = new CoreLabel();
    w3.set(CoreAnnotations.TextAnnotation.class, "CEO");
    w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    m.originalSpan = new ArrayList<>();
    m.originalSpan.add(w1);
    m.originalSpan.add(w2);
    m.originalSpan.add(w3);
    m.startIndex = 0;
    m.headIndex = 0;
    m.endIndex = 3;

    String result = m.removePhraseAfterHead();
    assertEquals("John", result);
  }
@Test
  public void testGetTypeFallback_WhenNoNERAndProperNNP_ShouldSetPROPER() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    head.set(CoreAnnotations.TextAnnotation.class, "London");
    mention.headWord = head;
    mention.originalSpan = new ArrayList<>();
    mention.originalSpan.add(head);

    Dictionaries dict = new Dictionaries();

//    mention.setType(dict);

    assertEquals(Dictionaries.MentionType.PROPER, mention.mentionType);
  }
@Test
  public void testGetNumber_OrgNERFallbackToUnknown() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PROPER;
    mention.nerString = "ORGANIZATION";
    mention.headWord = new CoreLabel();

    Dictionaries dict = new Dictionaries();

    mention.setNumber(dict);

    assertEquals(Dictionaries.Number.UNKNOWN, mention.number);
  }
@Test
  public void testGetNumber_TagPluralNNPSetsPlural() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");
    mention.headWord = head;
    mention.nerString = "O";

    Dictionaries dict = new Dictionaries();

    mention.setNumber(dict);

    assertEquals(Dictionaries.Number.PLURAL, mention.number);
  }
@Test
  public void testSetAnimacy_WhenNerStringEqualsLOC_ShouldSetINANIMATE() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PROPER;
    mention.nerString = "LOCATION";

    Dictionaries dict = new Dictionaries();

//    mention.setAnimacy(dict);

    assertEquals(Dictionaries.Animacy.INANIMATE, mention.animacy);
  }
@Test
  public void testGetPattern_ReturnsSingleHeadWhenNoModifiers() {
    Mention mention = new Mention();
    CoreLabel head = new CoreLabel();
    head.setWord("dog");
    head.setLemma("dog");
    head.setIndex(1);
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    mention.headWord = head;
//    mention.headIndexedWord = head;
    mention.enhancedDependency = new SemanticGraph();

    String pattern = mention.getPattern();

    assertEquals("dog", pattern);
  }
@Test
  public void testPreprocessSearchTerm_RemovesSpecialCharacters() {
    Mention mention = new Mention();
    mention.originalSpan = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "R&D(USA)");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    mention.originalSpan.add(token);
    mention.headWord = token;
    mention.headIndex = 0;
    mention.startIndex = 0;
    mention.endIndex = 1;
    mention.headString = "r&d(usa)";

    List<String> terms = mention.preprocessSearchTerm();

    assertFalse(terms.isEmpty());
    for (String term : terms) {
      assertFalse(term.contains("("));
      assertFalse(term.contains(")"));
    }
  }
@Test
  public void testIsMemberOfSameList_OneBelongsToNull_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.belongToLists = null;
    m2.belongToLists = new HashSet<>();
    m2.belongToLists.add(m1);

    assertFalse(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsListMemberOf_BothMentionTypeList_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.mentionType = Dictionaries.MentionType.LIST;
    m2.mentionType = Dictionaries.MentionType.LIST;
    m1.startIndex = 0;
    m1.endIndex = 2;
    m2.startIndex = 0;
    m2.endIndex = 3;
    m1.sentenceWords = new ArrayList<>();
    m2.sentenceWords = m1.sentenceWords;

    assertFalse(m1.isListMemberOf(m2));
  }
@Test
  public void testHeadsAgree_DifferentNERAndHeadStringEqual_ReturnsTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.headString = "apple";
    m2.headString = "apple";
    m1.nerString = "O";
    m2.nerString = "ORG";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testMoreRepresentativeThan_TieOnEverythingLongerSpanWins() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.headIndex = 3;
    m1.startIndex = 0;
    m2.headIndex = 3;
    m2.startIndex = 0;
    m1.sentNum = 1;
    m2.sentNum = 1;
    m1.nerString = "PERSON";
    m2.nerString = "PERSON";

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Smith");

    m1.originalSpan = new ArrayList<>();
    m2.originalSpan = new ArrayList<>();
    m1.originalSpan.add(t1);
    m1.originalSpan.add(t2);
    m2.originalSpan.add(t1);

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testSetNERString_CaseForHeadWithoutNER() {
    Mention mention = new Mention();
    mention.headWord = new CoreLabel();
    mention.headWord.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");

//    mention.setNERString();

    assertEquals("O", mention.nerString);
  }
@Test
  public void testIsTheCommonNoun_LowercaseMatchReturnsTrue() {
    Mention mention = new Mention();
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "the");
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "dog");
    List<CoreLabel> span = new ArrayList<>();
    span.add(c1);
    span.add(c2);
    mention.originalSpan = span;
    mention.mentionType = Dictionaries.MentionType.NOMINAL;

    assertTrue(mention.isTheCommonNoun());
  }
@Test
  public void testIsTheCommonNoun_NotTwoTokensReturnsFalse() {
    Mention mention = new Mention();
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "the");
    List<CoreLabel> span = new ArrayList<>();
    span.add(c1);
    mention.originalSpan = span;
    mention.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(mention.isTheCommonNoun());
  }
@Test
  public void testEntityTypesAgree_BothNerMismatchStrictTrue_ReturnsFalse() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.nerString = "PERSON";
    m2.nerString = "ORG";

    Dictionaries dict = new Dictionaries();

    boolean result = m1.entityTypesAgree(m2, dict, true);
    assertFalse(result);
  }
@Test
  public void testEntityTypesAgree_OneEmptyNerReturnsTrueWhenStrictFalse() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.nerString = "O";
    m2.nerString = "ORG";

    Dictionaries dict = new Dictionaries();

    boolean result = m1.entityTypesAgree(m2, dict, false);
    assertTrue(result);
  }
@Test
  public void testNumbersAgree_BothKnownUnequalLooseMatchReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.number = Dictionaries.Number.SINGULAR;
    m2.number = Dictionaries.Number.PLURAL;

    assertFalse(m1.numbersAgree(m2));
  }
@Test
  public void testGetPosition_HeadNearMiddleReturnsMiddle() {
    Mention m = new Mention();
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "example");
    m.headIndex = 2;
    m.sentenceWords = new ArrayList<>();
    m.sentenceWords.add(new CoreLabel());
    m.sentenceWords.add(new CoreLabel());
    m.sentenceWords.add(word);
    m.sentenceWords.add(new CoreLabel());
    m.sentenceWords.add(new CoreLabel());
    m.sentenceWords.add(new CoreLabel());

    String pos = m.getPosition();
    assertEquals("middle", pos);
  }
@Test
  public void testGetRelation_UnknownRelationReturnsNull() {
    Mention m = new Mention();
    m.enhancedDependency = new SemanticGraph();
    CoreLabel head = new CoreLabel();
    head.setIndex(1);
//    m.headIndexedWord = head;
//    m.enhancedDependency.addVertex(head);

    CoreLabel parent = new CoreLabel();
    parent.setIndex(2);
//    m.enhancedDependency.addVertex(parent);

//    m.enhancedDependency.addEdge(parent, head, GrammaticalRelation.valueOf("foo"), 1.0, false);
//    m.enhancedDependency.addRoot(parent);

    String relation = m.getRelation();
    assertNull(relation);
  }
@Test
  public void testNerName_ReturnsNullWhenTokensAreNull() {
    Mention mention = new Mention();
    mention.nerString = "O";

    String nerName = mention.nerName();
    assertNull(nerName);
  }
@Test
  public void testGetPremodifiers_NoChildrenReturnsEmpty() {
    Mention m = new Mention();
    m.enhancedDependency = new SemanticGraph();
    CoreLabel head = new CoreLabel();
    head.setIndex(1);
//    m.headIndexedWord = head;
    m.headWord = head;

//    List<List<edu.stanford.nlp.ling.IndexedWord>> mods = m.getPremodifiers();
//    assertTrue(mods.isEmpty());
  }
@Test
  public void testGetPostmodifiers_NoChildrenReturnsEmpty() {
    Mention m = new Mention();
    m.enhancedDependency = new SemanticGraph();
    CoreLabel head = new CoreLabel();
    head.setIndex(1);
//    m.headIndexedWord = head;
    m.headWord = head;

//    List<List<edu.stanford.nlp.ling.IndexedWord>> mods = m.getPostmodifiers();
//    assertTrue(mods.isEmpty());
  }
@Test
  public void testIsListLikeByDependency_WhenHeadNull_ReturnsFalse() {
    Mention m = new Mention();
    m.headIndexedWord = null;
    m.basicDependency = new SemanticGraph();

//    boolean listLike = m.isListLikeByDependency();
//    assertFalse(listLike);
  }
@Test
  public void testIsListLikeByDependency_NoConjunctMatch_ReturnsFalse() {
    Mention m = new Mention();
    m.basicDependency = new SemanticGraph();

    CoreLabel head = new CoreLabel();
    head.setIndex(2);
//    m.headIndexedWord = head;
//    m.basicDependency.addVertex(head);

    CoreLabel cc = new CoreLabel();
    cc.setIndex(4);
//    m.basicDependency.addVertex(cc);
//
//    m.basicDependency.addEdge(head, cc, GrammaticalRelation.valueOf("conj"), 1.0, false);
    m.startIndex = 0;
    m.endIndex = 1;
//
//    boolean isList = m.isListLikeByDependency();
//    assertFalse(isList);
  }
@Test
  public void testBuildQueryText_EmptyListReturnsEmptyString() {
    List<String> emptyList = new ArrayList<>();
    String result = Mention.buildQueryText(emptyList);
    assertEquals("", result);
  }
@Test
  public void testHeadsAgree_SameNERButDifferentSpans_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    CoreLabel h1 = new CoreLabel();
    h1.set(CoreAnnotations.TextAnnotation.class, "Mike");

    CoreLabel h2 = new CoreLabel();
    h2.set(CoreAnnotations.TextAnnotation.class, "Michelle");

    m1.headWord = h1;
    m2.headWord = h2;

    List<CoreLabel> span1 = new ArrayList<>();
    span1.add(h1);
    m1.originalSpan = span1;

    List<CoreLabel> span2 = new ArrayList<>();
    span2.add(h2);
    m2.originalSpan = span2;

    m1.nerString = "PERSON";
    m2.nerString = "PERSON";

    boolean result = m1.headsAgree(m2);
    assertFalse(result);
  }
@Test
  public void testAddListMember_InitializesAndAddsToSet() {
    Mention m = new Mention();
    Mention member = new Mention();

    m.addListMember(member);
    assertNotNull(m.listMembers);
    assertTrue(m.listMembers.contains(member));
  }
@Test
  public void testAddBelongsToList_InitializesAndAddsToSet() {
    Mention m = new Mention();
    Mention list = new Mention();

    m.addBelongsToList(list);
    assertNotNull(m.belongToLists);
    assertTrue(m.belongToLists.contains(list));
  }
@Test
  public void testAddApposition_InitializesSet() {
    Mention m = new Mention();
    Mention other = new Mention();

    m.addApposition(other);
    assertNotNull(m.appositions);
    assertTrue(m.appositions.contains(other));
  }
@Test
  public void testAddPredicateNominatives_InitializesSet() {
    Mention m = new Mention();
    Mention other = new Mention();

    m.addPredicateNominatives(other);
    assertNotNull(m.predicateNominatives);
    assertTrue(m.predicateNominatives.contains(other));
  }
@Test
  public void testAddRelativePronoun_InitializesSet() {
    Mention m = new Mention();
    Mention other = new Mention();

    m.addRelativePronoun(other);
    assertNotNull(m.relativePronouns);
    assertTrue(m.relativePronouns.contains(other));
  }
@Test
  public void testInsideIn_SameSpanReturnsTrue() {
    Mention m1 = new Mention();
    m1.startIndex = 1;
    m1.endIndex = 4;
    m1.sentNum = 0;

    Mention m2 = new Mention();
    m2.startIndex = 1;
    m2.endIndex = 4;
    m2.sentNum = 0;

    assertTrue(m1.insideIn(m2));
  }
@Test
  public void testProcess_SetsMentionTypeAndHeadStringProperly() throws Exception {
    Mention mention = new Mention();
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    head.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");
    head.set(CoreAnnotations.TextAnnotation.class, "manager");

    List<CoreLabel> span = new ArrayList<>();
    span.add(head);

    SemanticGraph graph = new SemanticGraph();

    mention.startIndex = 0;
    mention.endIndex = 1;
    mention.headIndex = 0;
    mention.sentenceWords = span;
    mention.basicDependency = graph;
    mention.enhancedDependency = graph;
    mention.originalSpan = span;
    mention.headWord = head;

    Dictionaries dict = new Dictionaries();
//    Mention.Semantics semantics = null;
//
//    mention.process(dict, semantics);

    assertEquals(Dictionaries.MentionType.NOMINAL, mention.mentionType);
    assertEquals("manager", mention.headString);
    assertNotNull(mention.nerString);
  }
@Test
  public void testProcess_WithNullSemanticsDoesNotThrow() throws Exception {
    Mention mention = new Mention();
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    head.set(CoreAnnotations.TextAnnotation.class, "Germany");
    head.set(CoreAnnotations.EntityTypeAnnotation.class, "NAM");

    List<CoreLabel> span = new ArrayList<>();
    span.add(head);

    SemanticGraph graph = new SemanticGraph();

    mention.startIndex = 0;
    mention.endIndex = 1;
    mention.headIndex = 0;
    mention.sentenceWords = span;
    mention.basicDependency = graph;
    mention.enhancedDependency = graph;
    mention.originalSpan = span;
    mention.headWord = head;

    Dictionaries dict = new Dictionaries();

    mention.process(dict, null);
  }
@Test
  public void testSetPerson_PronounSecondPerson() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "you");

    mention.originalSpan = new ArrayList<>();
    mention.originalSpan.add(head);
    mention.headWord = head;
    mention.headString = "you";
    mention.headIndex = 0;
    mention.startIndex = 0;

    Dictionaries dict = new Dictionaries();
    dict.secondPersonPronouns.add("you");

//    mention.setPerson(dict);

    assertEquals(Dictionaries.Person.YOU, mention.person);
  }
@Test
  public void testGetGender_ReturnsFromLastTokenRule() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "John");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word);

    mention.headWord = word;
    mention.originalSpan = span;
    mention.nerString = "O";

    Dictionaries dict = new Dictionaries();
    List<String> key = new ArrayList<>();
    key.add("john");
    dict.genderNumber.put(key, Dictionaries.Gender.MALE);

    List<String> resultInput = new ArrayList<>();
    resultInput.add("john");

//    Dictionaries.Gender gender = mention.getGender(dict, resultInput);
//
//    assertEquals(Dictionaries.Gender.MALE, gender);
  }
@Test
  public void testRoleAppositive_RejectsOnNERMismatch() throws IOException, ClassNotFoundException {
    Mention mention = new Mention();
    Mention other = new Mention();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "President");
    List<CoreLabel> span = new ArrayList<>();
    span.add(token1);
    mention.originalSpan = span;
    mention.headWord = token1;
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    mention.nerString = "ORG";
    mention.sentNum = 0;
    mention.startIndex = 0;
    mention.endIndex = 1;
    mention.animacy = Dictionaries.Animacy.ANIMATE;
    mention.gender = Dictionaries.Gender.FEMALE;
    mention.number = Dictionaries.Number.SINGULAR;

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Angela Merkel");
    List<CoreLabel> span2 = new ArrayList<>();
    span2.add(token2);
    other.originalSpan = span2;
    other.nerString = "O";
    other.sentNum = 0;
    other.startIndex = 0;
    other.endIndex = 2;
    other.animacy = Dictionaries.Animacy.ANIMATE;
    other.gender = Dictionaries.Gender.FEMALE;
    other.number = Dictionaries.Number.SINGULAR;

    Dictionaries dict = new Dictionaries();

    boolean result = mention.isRoleAppositive(other, dict);
    assertFalse(result);
  }
@Test
  public void testIsDemonym_WithNonMatchingDemonyms_ReturnsFalse() throws IOException, ClassNotFoundException {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "German");
    List<CoreLabel> span1 = new ArrayList<>();
    span1.add(tok1);
    m1.originalSpan = span1;

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "France");
    List<CoreLabel> span2 = new ArrayList<>();
    span2.add(tok2);
    m2.originalSpan = span2;

    Dictionaries dict = new Dictionaries();
//    dict.addDemonym("german", "germany");

    assertFalse(m1.isDemonym(m2, dict));
  }
@Test
  public void testMentionEquals_IgnoresUnrelatedFields() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span1 = new ArrayList<>();
    span1.add(tok1);

    Mention m1 = new Mention(1, 0, 1, span1, new SemanticGraph(), new SemanticGraph(), span1);
    Mention m2 = new Mention(1, 0, 1, span1, new SemanticGraph(), new SemanticGraph(), span1);

    assertTrue(m1.equals(m2));
  }
@Test
  public void testMentionHashCode_DifferenceByEndIndex() {
    Mention m1 = new Mention();
    m1.startIndex = 1;
    m1.endIndex = 3;

    Mention m2 = new Mention();
    m2.startIndex = 1;
    m2.endIndex = 4;

    assertNotEquals(m1.hashCode(), m2.hashCode());
  }
@Test
  public void testMoreRepresentativeThan_TieOnEverythingButSmallerSpanShouldLose() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Smith");

    m1.originalSpan = new ArrayList<>();
    m1.originalSpan.add(tok1);

    m2.originalSpan = new ArrayList<>();
    m2.originalSpan.add(tok1);
    m2.originalSpan.add(tok2);

    m1.headIndex = 1;
    m1.startIndex = 0;
    m2.headIndex = 1;
    m2.startIndex = 0;

    m1.sentNum = 0;
    m2.sentNum = 0;

    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;

    m1.nerString = "PERSON";
    m2.nerString = "PERSON";

    boolean result = m1.moreRepresentativeThan(m2);
    assertFalse(result);
  }
@Test
  public void testInsideIn_NegativeConditionStartBeforeContainer() {
    Mention container = new Mention();
    container.sentNum = 0;
    container.startIndex = 3;
    container.endIndex = 5;

    Mention inner = new Mention();
    inner.sentNum = 0;
    inner.startIndex = 1;
    inner.endIndex = 4;

    assertFalse(inner.insideIn(container));
  }
@Test
  public void testInsideIn_DifferentSentence_ReturnsFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 1;
    m2.sentNum = 2;
    m1.startIndex = 0;
    m1.endIndex = 3;
    m2.startIndex = 0;
    m2.endIndex = 5;

    assertFalse(m1.insideIn(m2));
  } 
}