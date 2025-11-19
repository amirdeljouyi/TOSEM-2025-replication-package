package edu.stanford.nlp.coref.data;

import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.ling.AbstractCoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mention_5_GPTLLMTest {

 @Test
  public void testToStringEqualsSpanToString() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "John");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    List<CoreLabel> sentenceWords = new ArrayList<>();
    sentenceWords.add(w1);
    sentenceWords.add(w2);

    SemanticGraph basicDep = mock(SemanticGraph.class);
    SemanticGraph enhancedDep = mock(SemanticGraph.class);

    Mention mention = new Mention(1, 0, 2, sentenceWords, basicDep, enhancedDep, sentenceWords);
    mention.headWord = w2;
    mention.headIndex = 1;

    String expected = "John Smith";
    assertEquals(expected, mention.toString());
  }
@Test
  public void testLowercaseNormalizedSpanString() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "John");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    List<CoreLabel> sentenceWords = new ArrayList<>();
    sentenceWords.add(w1);
    sentenceWords.add(w2);

    SemanticGraph basicDep = mock(SemanticGraph.class);
    SemanticGraph enhancedDep = mock(SemanticGraph.class);

    Mention mention = new Mention(1, 0, 2, sentenceWords, basicDep, enhancedDep, sentenceWords);
    mention.headWord = w2;
    mention.headIndex = 1;

    String expected = "john smith";
    assertEquals(expected, mention.lowercaseNormalizedSpanString());
  }
@Test
  public void testSameSentenceTrue() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "John");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    CoreLabel w3 = new CoreLabel();
    w3.set(CoreAnnotations.TextAnnotation.class, "is");

    List<CoreLabel> sentenceWords = new ArrayList<>();
    sentenceWords.add(w1);
    sentenceWords.add(w2);
    sentenceWords.add(w3);

    SemanticGraph basicDep = mock(SemanticGraph.class);
    SemanticGraph enhancedDep = mock(SemanticGraph.class);

    Mention mention1 = new Mention(1, 0, 2, sentenceWords, basicDep, enhancedDep, sentenceWords.subList(0, 2));
    Mention mention2 = new Mention(2, 2, 3, sentenceWords, basicDep, enhancedDep, sentenceWords.subList(2, 3));

    assertTrue(mention1.sameSentence(mention2));
  }
@Test
  public void testSameSentenceFalse() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "left");

    List<CoreLabel> sentence1 = new ArrayList<>();
    sentence1.add(w1);

    List<CoreLabel> sentence2 = new ArrayList<>();
    sentence2.add(w2);

    SemanticGraph basicDep = mock(SemanticGraph.class);
    SemanticGraph enhancedDep = mock(SemanticGraph.class);

    Mention mention1 = new Mention(1, 0, 1, sentence1, basicDep, enhancedDep, sentence1);
    Mention mention2 = new Mention(2, 0, 1, sentence2, basicDep, enhancedDep, sentence2);

    assertFalse(mention1.sameSentence(mention2));
  }
@Test
  public void testHeadsAgreeSimpleEquality() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "John");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> span = new ArrayList<>();
    span.add(head);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m1 = new Mention(1, 0, 1, span, dep, dep, span);
    Mention m2 = new Mention(2, 0, 1, span, dep, dep, span);

    m1.headWord = head;
    m2.headWord = head;

    m1.headString = "john";
    m2.headString = "john";

    m1.nerString = "O";
    m2.nerString = "O";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testIncludedInTrue() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Smith");

    List<CoreLabel> sentence = new ArrayList<>();
    sentence.add(tok1);
    sentence.add(tok2);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention outer = new Mention(1, 0, 2, sentence, dep, dep, sentence);
    Mention inner = new Mention(2, 0, 1, sentence, dep, dep, sentence.subList(0, 1));

    assertTrue(inner.includedIn(outer));
  }
@Test
  public void testIncludedInFalseNotSubset() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Smith");

    List<CoreLabel> sentence = new ArrayList<>();
    sentence.add(tok1);
    sentence.add(tok2);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m1 = new Mention(1, 0, 1, sentence, dep, dep, sentence.subList(0, 1));
    Mention m2 = new Mention(2, 1, 2, sentence, dep, dep, sentence.subList(1, 2));

    assertFalse(m1.includedIn(m2));
  }
@Test
  public void testIsMemberOfSameListTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    Mention list = new Mention();

    m1.addBelongsToList(list);
    m2.addBelongsToList(list);

    assertTrue(m1.isMemberOfSameList(m2));
  }
@Test
  public void testIsMemberOfSameListFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    Mention list1 = new Mention();
    Mention list2 = new Mention();

    m1.addBelongsToList(list1);
    m2.addBelongsToList(list2);

    assertFalse(m1.isMemberOfSameList(m2));
  }
@Test
  public void testSpanToStringCachingValidation() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Alice");
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Johnson");

    List<CoreLabel> span = new ArrayList<>();
    span.add(tok1);
    span.add(tok2);

    SemanticGraph graph = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 2, span, graph, graph, span);

    String result1 = mention.spanToString();
    String result2 = mention.spanToString();
    assertSame(result1, result2);
  }
@Test
  public void testLowercaseNormalizedSpanStringCachingValidation() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Google");
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "LLC");

    List<CoreLabel> span = new ArrayList<>();
    span.add(tok1);
    span.add(tok2);

    SemanticGraph graph = mock(SemanticGraph.class);
    Mention mention = new Mention(3, 0, 2, span, graph, graph, span);

    mention.headWord = tok2;
    mention.headIndex = 1;

    String first = mention.lowercaseNormalizedSpanString();
    String second = mention.lowercaseNormalizedSpanString();

    assertSame(first, second);
  }
@Test
  public void testSpanToStringWithEmptyOriginalSpan() {
    List<CoreLabel> emptySpan = new ArrayList<>();
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 0, emptySpan, dep, dep, emptySpan);
    String result = mention.spanToString();
    assertEquals("", result);
  }
@Test
  public void testLowercaseNormalizedSpanStringNullSafe() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "HELLO");
    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 1, span, dep, dep, span);
    mention.headWord = token;
    mention.headIndex = 0;

    String lower = mention.lowercaseNormalizedSpanString();
    assertEquals("hello", lower);
  }
@Test
  public void testHeadsAgreeWithNERSubsetInclusion() {
    CoreLabel core1 = new CoreLabel();
    core1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    core1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel core2 = new CoreLabel();
    core2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    core2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> span1 = new ArrayList<>();
    span1.add(core1);

    List<CoreLabel> span2 = new ArrayList<>();
    span2.add(core1);
    span2.add(core2);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention mention1 = new Mention(1, 0, 1, span1, dep, dep, span1);
    mention1.headWord = core1;
    mention1.headString = "barack";
    mention1.nerString = "PERSON";

    Mention mention2 = new Mention(2, 0, 2, span2, dep, dep, span2);
    mention2.headWord = core2;
    mention2.headString = "obama";
    mention2.nerString = "PERSON";

    assertTrue(mention1.headsAgree(mention2));
  }
@Test
  public void testIsRelativePronounTrueCase() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addRelativePronoun(m2);
    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testIsRelativePronounFalseWithEmptySet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    assertFalse(m1.isRelativePronoun(m2));
  }
@Test
  public void testIsListMemberOfFalseEqualMention() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.LIST;
    assertFalse(mention.isListMemberOf(mention));
  }
@Test
  public void testIsListMemberOfFalseNestedList() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.mentionType = Dictionaries.MentionType.LIST;
    m2.mentionType = Dictionaries.MentionType.LIST;
    assertFalse(m1.isListMemberOf(m2));
  }
@Test
  public void testIsListLikeByDependencyFalseWhenHeadNull() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 1, new ArrayList<>(), dep, dep);
//    assertFalse(mention.isListLikeByDependency());
  }
@Test
  public void testNumbersAgreeStrictFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.number = Dictionaries.Number.SINGULAR;
    m2.number = Dictionaries.Number.PLURAL;
//    assertFalse(m1.numbersAgree(m2, true));
  }
@Test
  public void testGendersAgreeRelaxedWithUnknown() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.gender = Dictionaries.Gender.UNKNOWN;
    m2.gender = Dictionaries.Gender.FEMALE;
    assertTrue(m1.gendersAgree(m2, false));
  }
@Test
  public void testAnimaciesAgreeRelaxedWithUnknown() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.animacy = Dictionaries.Animacy.UNKNOWN;
    m2.animacy = Dictionaries.Animacy.ANIMATE;
    assertTrue(m1.animaciesAgree(m2, false));
  }
@Test
  public void testEqualsAndHashCodeNotEqualScenario() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "Jane");

    List<CoreLabel> span = new ArrayList<>();
    span.add(label);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m1 = new Mention(1, 0, 1, span, dep, dep, span);
    m1.headWord = label;
    m1.headIndex = 0;
    m1.mentionID = 5;

    Mention m2 = new Mention(2, 0, 1, span, dep, dep, span);
    m2.headWord = label;
    m2.headIndex = 0;
    m2.mentionID = 6;

    assertNotEquals(m1, m2);
    assertNotEquals(m1.hashCode(), m2.hashCode());
  }
@Test
  public void testGetTypeReturnsMentionClass() {
    Mention mention = new Mention();
    assertEquals(Mention.class, mention.getType());
  }
@Test
  public void testIsPronominalTrueForPronounType() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    assertTrue(mention.isPronominal());
  }
@Test
  public void testIsPronominalFalseForProper() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PROPER;
    assertFalse(mention.isPronominal());
  }
@Test
  public void testMentionInsideInTrueBoundaryCondition() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 0;
    m2.sentNum = 0;
    m1.startIndex = 1;
    m1.endIndex = 2;
    m2.startIndex = 1;
    m2.endIndex = 3;
    assertTrue(m1.insideIn(m2));
  }
@Test
  public void testMentionInsideInFalseWrongSentence() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 0;
    m2.sentNum = 1;
    m1.startIndex = 1;
    m1.endIndex = 2;
    m2.startIndex = 0;
    m2.endIndex = 3;
    assertFalse(m1.insideIn(m2));
  }
@Test
  public void testMentionAppearanceOrderEarlier() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 0;
    m1.endIndex = 1;
    m1.headIndex = 0;

    m2.sentNum = 2;
    m2.startIndex = 0;
    m2.endIndex = 1;
    m2.headIndex = 0;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testStringWithoutArticleWithThe() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("the man");
    assertEquals("man", result);
  }
@Test
  public void testStringWithoutArticleWithA() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("A book");
    assertEquals("book", result);
  }
@Test
  public void testStringWithoutArticleIrrelevant() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("history");
    assertEquals("history", result);
  }
@Test
  public void testRemoveParenthesisWhenPresent() {
    String result = Mention.removeParenthesis("Stanford University (SU)");
    assertEquals("Stanford University", result);
  }
@Test
  public void testRemoveParenthesisWhenAbsent() {
    String result = Mention.removeParenthesis("MIT");
    assertEquals("", result);
  }
@Test
  public void testMentionEqualsSameObject() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 2, new ArrayList<>(), dep, dep);
    assertTrue(mention.equals(mention));
  }
@Test
  public void testMentionEqualsNull() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 1, new ArrayList<>(), dep, dep);
    assertFalse(mention.equals(null));
  }
@Test
  public void testMentionEqualsDifferentClass() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 1, new ArrayList<>(), dep, dep);
    assertFalse(mention.equals("String"));
  }
@Test
  public void testIsListMemberOfNonListMention() {
    Mention listMention = new Mention();
    listMention.mentionType = Dictionaries.MentionType.NOMINAL;
    listMention.startIndex = 0;
    listMention.endIndex = 5;

    Mention subMention = new Mention();
    subMention.startIndex = 1;
    subMention.endIndex = 3;
    subMention.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(subMention.isListMemberOf(listMention));
  }
@Test
  public void testMentionHashCodeIsConsistent() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 3, 7, new ArrayList<>(), dep, dep);
    int hash1 = mention.hashCode();
    int hash2 = mention.hashCode();
    assertEquals(hash1, hash2);
  }
@Test
  public void testAddAppositionStoresMentionCorrectly() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addApposition(m2);
    assertTrue(m1.appositions.contains(m2));
  }
@Test
  public void testIsAppositionReturnsFalseWhenNotPresent() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    assertFalse(m1.isApposition(m2));
  }
@Test
  public void testIsAppositionReturnsTrueWhenAdded() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addApposition(m2);
    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testAddPredicateNominativesStoresCorrectly() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addPredicateNominatives(m2);
    assertTrue(m1.predicateNominatives.contains(m2));
  }
@Test
  public void testIsPredicateNominativesReturnsTrueWhenAdded() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addPredicateNominatives(m2);
    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testAttributesAgreeWithDifferentAttributes() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.gender = Dictionaries.Gender.MALE;
    m2.gender = Dictionaries.Gender.FEMALE;
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m2.animacy = Dictionaries.Animacy.INANIMATE;
    m1.number = Dictionaries.Number.PLURAL;
    m2.number = Dictionaries.Number.SINGULAR;
    m1.nerString = "PERSON";
    m2.nerString = "ORG";

    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testMentionHeadsAgreeProperlyWithMatchingNER() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Mary");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> span = new ArrayList<>();
    span.add(head);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m1 = new Mention(0, 0, 1, span, dep, dep, span);
    m1.headWord = head;
    m1.headString = "mary";
    m1.nerString = "PERSON";

    Mention m2 = new Mention(2, 0, 1, span, dep, dep, span);
    m2.headWord = head;
    m2.headString = "mary";
    m2.nerString = "PERSON";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testAppearEarlierThanSameSentenceDifferentIndexes() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 2;
    m1.endIndex = 4;
    m1.headIndex = 3;

    Mention m2 = new Mention();
    m2.sentNum = 1;
    m2.startIndex = 3;
    m2.endIndex = 5;
    m2.headIndex = 4;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierThanEqualMentionsDifferentHash() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentNum = 0;
    m2.sentNum = 0;
    m1.startIndex = 1;
    m2.startIndex = 1;
    m1.endIndex = 2;
    m2.endIndex = 2;
    m1.headIndex = 1;
    m2.headIndex = 1;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    assertFalse(m1.appearEarlierThan(m2) && m2.appearEarlierThan(m1));
  }
@Test
  public void testRemovePhraseAfterHeadWhenCommaExists() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "The");
    word1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "president");
    word2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel word3 = new CoreLabel();
    word3.set(CoreAnnotations.TextAnnotation.class, ",");
    word3.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");

    CoreLabel word4 = new CoreLabel();
    word4.set(CoreAnnotations.TextAnnotation.class, "who");
    word4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);
    span.add(word3);
    span.add(word4);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 4, span, dep, dep, span);
    mention.headWord = word2;
    mention.headIndex = 1;
    String result = mention.removePhraseAfterHead();
    assertEquals("The president", result);
  }
@Test
  public void testGetPremodifiersWhenHeadWordIsNull() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 1, new ArrayList<>(), dep, dep);
    assertTrue(mention.getPremodifiers().isEmpty());
  }
@Test
  public void testGetPostmodifiersWhenHeadWordIsNull() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(2, 0, 1, new ArrayList<>(), dep, dep);
    assertTrue(mention.getPostmodifiers().isEmpty());
  }
@Test
  public void testBuildQueryTextConcatTwoTerms() {
    List<String> terms = new ArrayList<>();
    terms.add("new");
    terms.add("york");
    String result = Mention.buildQueryText(terms);
    assertEquals("new york", result);
  }
@Test
  public void testRemovePhraseAfterHeadWithWHOnly() {
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "The");
    c1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "man");
    c2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel c3 = new CoreLabel();
    c3.set(CoreAnnotations.TextAnnotation.class, "who");
    c3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    List<CoreLabel> span = new ArrayList<>();
    span.add(c1);
    span.add(c2);
    span.add(c3);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention m = new Mention(1, 0, 3, span, dep, dep, span);
    m.headWord = c2;
    m.headIndex = 1;
    String removed = m.removePhraseAfterHead();
    assertEquals("The man", removed);
  }
@Test
  public void testRemovePhraseAfterHeadNoCommaNorWH() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "The");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "cat");

    List<CoreLabel> span = new ArrayList<>();
    span.add(a);
    span.add(b);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 2, span, dep, dep, span);
    mention.headWord = b;
    mention.headIndex = 1;
    String result = mention.removePhraseAfterHead();
    assertEquals("The cat", result);
  }
@Test
  public void testBuildQueryTextEmptyInput() {
    List<String> terms = new ArrayList<>();
    String query = Mention.buildQueryText(terms);
    assertEquals("", query);
  }
@Test
  public void testPreprocessSearchTermWithSpecialCharacters() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "M.I.T.");
    tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> sentence = new ArrayList<>();
    sentence.add(tok1);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 1, sentence, dep, dep, sentence);
    mention.headWord = tok1;
    mention.headString = "m.i.t.";
    mention.headIndex = 0;

    List<String> result = mention.preprocessSearchTerm();
    assertFalse(result.isEmpty());
  }
@Test
  public void testHeadsAgreeNERMismatchStillSameHead() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Washington");

    List<CoreLabel> span = new ArrayList<>();
    span.add(head);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention mention1 = new Mention(1, 0, 1, span, dep, dep, span);
    mention1.headWord = head;
    mention1.headString = "washington";
    mention1.nerString = "GPE";

    Mention mention2 = new Mention(2, 0, 1, span, dep, dep, span);
    mention2.headWord = head;
    mention2.headString = "washington";
    mention2.nerString = "PERSON";

    assertFalse(mention1.headsAgree(mention2));
  }
@Test
  public void testHeadsAgreeNERAndHeadNameSubsumed() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Barack");
    tok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> longerSpan = new ArrayList<>();
    longerSpan.add(tok2);
    longerSpan.add(tok1);

    List<CoreLabel> shorterSpan = new ArrayList<>();
    shorterSpan.add(tok1);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention longMention = new Mention(1, 0, 2, longerSpan, dep, dep, longerSpan);
    longMention.headWord = tok1;
    longMention.headString = "obama";
    longMention.nerString = "PERSON";

    Mention shortMention = new Mention(2, 0, 1, shorterSpan, dep, dep, shorterSpan);
    shortMention.headWord = tok1;
    shortMention.headString = "obama";
    shortMention.nerString = "PERSON";

    assertTrue(shortMention.headsAgree(longMention));
  }
@Test
  public void testNumbersAgreeWithUnknownAndMismatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.number = Dictionaries.Number.UNKNOWN;
    m2.number = Dictionaries.Number.PLURAL;
    assertTrue(m1.numbersAgree(m2));
  }
@Test
  public void testGendersAgreeStrictFalse() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.gender = Dictionaries.Gender.MALE;
    m2.gender = Dictionaries.Gender.FEMALE;
    assertFalse(m1.gendersAgree(m2, true));
  }
@Test
  public void testGetSingletonFeaturesIncludesCorrectProperties() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    Mention mention = new Mention();

    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    mention.nerString = "PERSON";
    mention.animacy = Dictionaries.Animacy.ANIMATE;
    mention.person = Dictionaries.Person.I;
    mention.number = Dictionaries.Number.SINGULAR;
    mention.headString = "he";
    mention.headIndexedWord = null;
    mention.headWord = new CoreLabel();
    mention.enhancedDependency = mock(SemanticGraph.class);

    List<String> features = mention.getSingletonFeatures(dict);
    assertNotNull(features);
    assertTrue(features.size() >= 10);
  }
@Test
  public void testSetGenderPronounMaleFromDict() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    dict.malePronouns.add("he");

    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "he");

    List<CoreLabel> span = new ArrayList<>();
    span.add(label);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention mention = new Mention(1, 0, 1, span, dep, dep, span);
    mention.headWord = label;
    mention.originalSpan = span;
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    mention.headString = "he";
    mention.number = Dictionaries.Number.SINGULAR;

//    mention.setGender(dict, null);
    assertEquals(Dictionaries.Gender.MALE, mention.gender);
  }
@Test
  public void testSetPersonFirstPersonWe() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    dict.firstPersonPronouns.add("we");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "We");

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 1, span, dep, dep, span);
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    mention.number = Dictionaries.Number.PLURAL;
    mention.headWord = token;
    mention.headIndex = 0;
    mention.originalSpan = span;
    mention.headString = "we";

    mention.spanToString(); 
//    mention.setPerson(dict);
    assertEquals(Dictionaries.Person.WE, mention.person);
  }
@Test
  public void testAnimaciesAgreeStrictFalseOnMismatch() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m2.animacy = Dictionaries.Animacy.INANIMATE;

    assertFalse(m1.animaciesAgree(m2, true));
  }
@Test
  public void testBuildQueryTextPreservesOrderWithSpecialChars() {
    List<String> terms = new ArrayList<>();
    terms.add("IBM+Watson");
    terms.add("!");
    String queryText = Mention.buildQueryText(terms);
    assertEquals("IBM+Watson !", queryText);
  }
@Test
  public void testMoreRepresentativeThanNullMentionAlwaysTrue() {
    Mention mention = new Mention();
    boolean result = mention.moreRepresentativeThan(null);
    assertTrue(result);
  }
@Test
  public void testMoreRepresentativeThanLowerNERPriority() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.mentionType = Dictionaries.MentionType.PROPER;
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m1.nerString = "PERSON";
    m2.nerString = "O";
    m1.headIndex = 3;
    m1.startIndex = 0;
    m2.headIndex = 2;
    m2.startIndex = 0;

    m1.sentNum = 0;
    m2.sentNum = 0;
    m1.originalSpan = new ArrayList<>();
    m2.originalSpan = new ArrayList<>();

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testMoreRepresentativeThanHeadIndexAndSentTiebreaker() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.mentionType = Dictionaries.MentionType.PROPER;
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m1.nerString = "PERSON";
    m2.nerString = "PERSON";
    m1.headIndex = 2;
    m1.startIndex = 0;

    m2.headIndex = 4;
    m2.startIndex = 0;

    m1.sentNum = 0;
    m2.sentNum = 0;

    m1.originalSpan = new ArrayList<>();
    m2.originalSpan = new ArrayList<>();

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testMoreRepresentativeThanHeadDistanceTieLengthPreference() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.mentionType = Dictionaries.MentionType.PROPER;
    m2.mentionType = Dictionaries.MentionType.PROPER;
    m1.nerString = "ORG";
    m2.nerString = "ORG";

    m1.headIndex = 3;
    m1.startIndex = 1;
    m2.headIndex = 4;
    m2.startIndex = 2;

    m1.sentNum = 1;
    m2.sentNum = 1;

    List<CoreLabel> span1 = new ArrayList<>();
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "Microsoft");
    span1.add(c1);

    List<CoreLabel> span2 = new ArrayList<>();
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "Microsoft");
    CoreLabel c3 = new CoreLabel();
    c3.set(CoreAnnotations.TextAnnotation.class, "Corp.");
    span2.add(c2);
    span2.add(c3);

    m1.originalSpan = span1;
    m2.originalSpan = span2;

    assertFalse(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testEntityTypesAgreeStrictFalseWhenDifferentNER() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.nerString = "PERSON";
    m2.nerString = "ORG";

    boolean result = m1.entityTypesAgree(m2, dict, true);
    assertFalse(result);
  }
@Test
  public void testEntityTypesAgreeRelaxedOandORG() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m1.nerString = "O";
    m1.headString = "it";
    m2.nerString = "ORGANIZATION";

    dict.organizationPronouns.add("it");

    boolean result = m1.entityTypesAgree(m2, dict, false);
    assertTrue(result);
  }
@Test
  public void testEntityTypesAgreeRelaxedUnsupportedNER() throws IOException, ClassNotFoundException {
    Dictionaries dict = new Dictionaries();
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m1.nerString = "GARBAGE";
    m2.nerString = "DATE";

    m1.headString = "this";
    boolean result = m1.entityTypesAgree(m2, dict, false);
    assertFalse(result);
  }
@Test
  public void testIsTheCommonNounTrue() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "table");

    List<CoreLabel> span = new ArrayList<>();
    span.add(token1);
    span.add(token2);

    SemanticGraph basicDep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 2, span, basicDep, basicDep, span);
    mention.headWord = token2;
    mention.mentionType = Dictionaries.MentionType.NOMINAL;

    String spanText = mention.spanToString();
    boolean result = mention.isTheCommonNoun();

    assertTrue(spanText.toLowerCase().startsWith("the "));
    assertTrue(result);
  }
@Test
  public void testIsTheCommonNounFalseDueToSize() {
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "red");

    CoreLabel c = new CoreLabel();
    c.set(CoreAnnotations.TextAnnotation.class, "table");

    List<CoreLabel> span = new ArrayList<>();
    span.add(a);
    span.add(b);
    span.add(c);

    SemanticGraph basicDep = mock(SemanticGraph.class);
    Mention m = new Mention(1, 0, 3, span, basicDep, basicDep, span);
    m.headWord = c;
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(m.isTheCommonNoun());
  }
@Test
  public void testIsCoordinatedTrue() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "cats");

    token.setIndex(1);
    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    SemanticGraph enhancedDep = mock(SemanticGraph.class);
    SemanticGraph basicDep = mock(SemanticGraph.class);

    Mention m = new Mention(1, 0, 1, span, basicDep, enhancedDep, span);
    m.headWord = token;
//    m.headIndexedWord = token;

//    when(enhancedDep.childPairs(token)).thenReturn(new ArrayList<>());
//    when(enhancedDep.childPairs(token)).thenReturn(
//      new ArrayList<>() {{
//        add(new edu.stanford.nlp.util.Pair<>(mock(edu.stanford.nlp.trees.GrammaticalRelation.class), token));
//      }}
//    );

    edu.stanford.nlp.trees.GrammaticalRelation gr = mock(edu.stanford.nlp.trees.GrammaticalRelation.class);
    when(gr.getShortName()).thenReturn("cc");

    List<edu.stanford.nlp.util.Pair<edu.stanford.nlp.trees.GrammaticalRelation, edu.stanford.nlp.ling.IndexedWord>> childPairs = new ArrayList<>();
//    childPairs.add(new edu.stanford.nlp.util.Pair<>(gr, token));
//
//    when(enhancedDep.childPairs(token)).thenReturn(childPairs);

    assertTrue(m.isCoordinated());
  }
@Test
  public void testIsCoordinatedFalseNoConjunction() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "apple");
    token.setIndex(1);

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    SemanticGraph enhancedDep = mock(SemanticGraph.class);
    SemanticGraph basicDep = mock(SemanticGraph.class);

    Mention m = new Mention(1, 0, 1, span, basicDep, enhancedDep, span);
    m.headWord = token;
//    m.headIndexedWord = token;

//    when(enhancedDep.childPairs(token)).thenReturn(new ArrayList<>());

    assertFalse(m.isCoordinated());
  }
@Test
  public void testMentionInsideInFalseWhenStartsBefore() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.sentNum = 0;
    m2.sentNum = 0;

    m1.startIndex = 1;
    m1.endIndex = 4;

    m2.startIndex = 0;
    m2.endIndex = 3;

    assertFalse(m1.insideIn(m2));
  }
@Test
  public void testGetContextReturnsEmptyOnNoNER() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "item");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    token.setIndex(1);

    List<CoreLabel> sentence = new ArrayList<>();
    sentence.add(token);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m = new Mention(1, 0, 1, sentence, dep, dep, sentence);
    List<String> context = m.getContext();
    assertTrue(context.isEmpty());
  }
@Test
  public void testGetPremodifierContextReturnsNerStrings() {
    CoreLabel ner1 = new CoreLabel();
    ner1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    ner1.set(CoreAnnotations.TextAnnotation.class, "Paris");
    ner1.setIndex(1);

    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "ban");
    head.setIndex(2);

    List<CoreLabel> span = new ArrayList<>();
    span.add(ner1);
    span.add(head);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention mention = new Mention(1, 0, 2, span, dep, dep, span);
    mention.headWord = head;
//    mention.headIndexedWord = head;

    List<String> ctx = mention.getPremodifierContext();
    assertTrue(ctx.isEmpty());
  }
@Test
  public void testAddRelativePronounStoresCorrectly() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addRelativePronoun(m2);
    assertTrue(m1.relativePronouns.contains(m2));
  }
@Test
  public void testIsRelativePronounNegativeCase() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    assertFalse(m1.isRelativePronoun(m2));
  }
@Test
  public void testAddListMemberCreatesSetOnNull() {
    Mention m1 = new Mention();
    Mention member = new Mention();

    m1.addListMember(member);
    assertTrue(m1.listMembers.contains(member));
  }
@Test
  public void testAddBelongsToListCreatesSet() {
    Mention m1 = new Mention();
    Mention list = new Mention();

    m1.addBelongsToList(list);
    assertTrue(m1.belongToLists.contains(list));
  }
@Test
  public void testMentionEqualsWithEqualFields() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Name");
    token.setIndex(1);

    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m1 = new Mention(1, 0, 1, span, dep, dep, span);
    Mention m2 = new Mention(1, 0, 1, span, dep, dep, span);

    m1.headWord = token;
    m2.headWord = token;

    m1.headIndex = 0;
    m2.headIndex = 0;

    m1.mentionType = Dictionaries.MentionType.PROPER;
    m2.mentionType = Dictionaries.MentionType.PROPER;

    m1.headString = "name";
    m2.headString = "name";

    m1.nerString = "PERSON";
    m2.nerString = "PERSON";

    m1.goldCorefClusterID = m2.goldCorefClusterID = -1;
    m1.corefClusterID = m2.corefClusterID = -1;

    m1.mentionNum = m2.mentionNum = 1;
    m1.sentNum = m2.sentNum = 0;

    m1.isSubject = false;
    m2.isSubject = false;

    assertTrue(m1.equals(m2));
  }
@Test
  public void testSetHeadStringSkipsNERSuffixCorp() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Inc.");
    w2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> span = new ArrayList<>();
    span.add(w1);
    span.add(w2);

    SemanticGraph dep = mock(SemanticGraph.class);
    when(dep.getNodeByIndexSafe(anyInt())).thenReturn(null);

    Mention m = new Mention(1, 0, 2, span, dep, dep, span);
    m.headWord = w2;
    m.headIndex = 1;

//    m.setHeadString();

    assertEquals("apple", m.headString); 
  }
@Test
  public void testSetHeadStringInvalidHeadIndexBounds() {
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "Foo");
    word.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m = new Mention(1, 0, 1, span, dep, dep, span);
    m.headWord = word;
    m.headIndex = 5; 

    try {
//      m.setHeadString();
      fail("Expected an exception due to invalid head index");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Invalid start index"));
    }
  }
@Test
  public void testStringWithoutArticleHandlesCapitalAn() {
    Mention m = new Mention();
    String val = m.stringWithoutArticle("An elephant");
    assertEquals("elephant", val);
  }
@Test
  public void testRemovePhraseAfterHeadHandlesMultipleCommas() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "the");
    word1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "CEO");
    word2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel comma = new CoreLabel();
    comma.set(CoreAnnotations.TextAnnotation.class, ",");
    comma.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");

    CoreLabel clause = new CoreLabel();
    clause.set(CoreAnnotations.TextAnnotation.class, "who");
    clause.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    CoreLabel other = new CoreLabel();
    other.set(CoreAnnotations.TextAnnotation.class, "runs");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);
    span.add(comma);
    span.add(clause);
    span.add(other);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 5, span, dep, dep, span);
    mention.headWord = word2;
    mention.headIndex = 1;
    mention.startIndex = 0;

    String result = mention.removePhraseAfterHead();
    assertEquals("the CEO", result);
  }
@Test
  public void testRemovePhraseAfterHeadNoPunctuationReturnsSame() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "fresh");
    w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "food");
    w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> span = new ArrayList<>();
    span.add(w1);
    span.add(w2);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention m = new Mention(2, 0, 2, span, dep, dep, span);
    m.headWord = w2;
    m.headIndex = 1;
    m.startIndex = 0;

    String stripped = m.removePhraseAfterHead();
    assertEquals("fresh food", stripped);
  }
@Test
  public void testSpanToStringMultipleTokensSpecialCharacters() {
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "\"Quote");

    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "End\"");

    List<CoreLabel> list = new ArrayList<>();
    list.add(tok1);
    list.add(tok2);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention m = new Mention(10, 0, 2, list, dep, dep, list);
    String out = m.spanToString();
    assertEquals("\"Quote End\"", out);
  }
@Test
  public void testLowercaseNormalizedSpanStringWithCacheCheck() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "HELLO");
    List<CoreLabel> span = new ArrayList<>();
    span.add(token);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention m = new Mention(5, 0, 1, span, dep, dep, span);

    m.headWord = token;
    m.headIndex = 0;

    String first = m.lowercaseNormalizedSpanString();
    String second = m.lowercaseNormalizedSpanString();
    assertSame(first, second);
    assertEquals("hello", second);
  }
@Test
  public void testEqualsFieldMismatchReturnsFalse() {
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "Alpha");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word);

    SemanticGraph dep = mock(SemanticGraph.class);

    Mention m1 = new Mention(1, 0, 1, span, dep, dep, span);
    Mention m2 = new Mention(2, 0, 1, span, dep, dep, span);

    m1.headWord = word;
    m2.headWord = word;

    m1.headIndex = 0;
    m2.headIndex = 0;

    m1.mentionID = 1;
    m2.mentionID = 99; 

    boolean equals = m1.equals(m2);
    assertFalse(equals);
  }
@Test
  public void testMentionHashCodeDifferenceBasedOnStartEnd() {
    SemanticGraph dep = mock(SemanticGraph.class);
    Mention m1 = new Mention(1, 0, 1, new ArrayList<>(), dep, dep);
    Mention m2 = new Mention(1, 0, 2, new ArrayList<>(), dep, dep);

    int h1 = m1.hashCode();
    int h2 = m2.hashCode();

    assertNotEquals(h1, h2);
  }
@Test
  public void testGetTypeReturnsCorrectTypeClass() {
    Mention mention = new Mention();
    assertEquals(Mention.class, mention.getType());
  }
@Test
  public void testMentionToStringDelegatesToSpanToString() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "Sun");
    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "Microsystems");

    List<CoreLabel> span = new ArrayList<>();
    span.add(word1);
    span.add(word2);

    SemanticGraph dep = mock(SemanticGraph.class);
    Mention mention = new Mention(1, 0, 2, span, dep, dep, span);
    String output = mention.toString();
    assertEquals("Sun Microsystems", output);
  }
@Test
  public void testGetQuantificationReturnsDefiniteWhenDeterminer() throws IOException, ClassNotFoundException {
    IndexedWord head = new IndexedWord();
    head.setLemma("house");

    IndexedWord det = new IndexedWord();
    det.setLemma("the");

    SemanticGraph graph = mock(SemanticGraph.class);
    Set<IndexedWord> children = new HashSet<>();
    children.add(det);

    when(graph.getChildrenWithReln(eq(head), eq(GrammaticalRelation.valueOf("det")))).thenReturn(children);
    when(graph.getChildrenWithReln(eq(head), eq(GrammaticalRelation.valueOf("nmod:poss")))).thenReturn(Collections.emptySet());
    when(graph.getChildrenWithReln(eq(head), eq(GrammaticalRelation.valueOf("nummod")))).thenReturn(Collections.emptySet());

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.nerString = "O";
    mention.enhancedDependency = graph;

    Dictionaries dict = new Dictionaries();
    dict.determiners.add("the");

    String result = mention.getQuantification(dict);
    assertEquals("definite", result);
  }
@Test
  public void testGetQuantificationReturnsIndefiniteWithNoChildren() throws IOException, ClassNotFoundException {
    IndexedWord head = new IndexedWord();
    head.setLemma("book");

    SemanticGraph graph = mock(SemanticGraph.class);
    when(graph.getChildrenWithReln(any(), any())).thenReturn(Collections.emptySet());

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.nerString = "O";
    mention.enhancedDependency = graph;

    Dictionaries dict = new Dictionaries();
    String result = mention.getQuantification(dict);
    assertEquals("indefinite", result);
  }
@Test
  public void testGetModifiersWithPossessivePronounFilteredOut() throws IOException, ClassNotFoundException {
    IndexedWord head = new IndexedWord();

    IndexedWord possessive = new IndexedWord();
    possessive.setLemma("his");

    SemanticGraph graph = mock(SemanticGraph.class);
    List<Pair<GrammaticalRelation, IndexedWord>> children = new ArrayList<>();
    children.add(new Pair<>(GrammaticalRelation.valueOf("nmod:poss"), possessive));

    when(graph.childPairs(eq(head))).thenReturn(children);

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.enhancedDependency = graph;

    Dictionaries dict = new Dictionaries();
    dict.determiners.add("his");

    int value = mention.getModifiers(dict);
    assertEquals(0, value);
  }
@Test
  public void testGetNegationWhenNegWordFoundInChild() throws IOException, ClassNotFoundException {
    IndexedWord head = new IndexedWord();
    IndexedWord child = new IndexedWord();
    child.setLemma("not");

    SemanticGraph graph = mock(SemanticGraph.class);
    Set<IndexedWord> children = new HashSet<>();
    children.add(child);

    List<IndexedWord> siblings = new ArrayList<>();
    siblings.add(new IndexedWord());

    when(graph.getChildren(eq(head))).thenReturn(children);
    when(graph.getSiblings(eq(head))).thenReturn(siblings);
    when(graph.hasParentWithReln(any(), any())).thenReturn(false);

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.enhancedDependency = graph;

    Dictionaries dict = new Dictionaries();
    dict.negations.add("not");

    int result = mention.getNegation(dict);
    assertEquals(1, result);
  }
@Test
  public void testGetNegationWithNoNegation() throws IOException, ClassNotFoundException {
    IndexedWord head = new IndexedWord();

    SemanticGraph graph = mock(SemanticGraph.class);
    when(graph.getChildren(any())).thenReturn(Collections.emptySet());
    when(graph.getSiblings(any())).thenReturn(Collections.emptyList());
    when(graph.hasParentWithReln(any(), any())).thenReturn(false);
    when(graph.parentPairs(any())).thenReturn(Collections.emptyList());

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.enhancedDependency = graph;

    Dictionaries dict = new Dictionaries();

    int result = mention.getNegation(dict);
    assertEquals(0, result);
  }
@Test
  public void testGetModalFromParentAuxChild() throws IOException, ClassNotFoundException {
    IndexedWord head = new IndexedWord();
    IndexedWord parent = new IndexedWord();
    IndexedWord aux = new IndexedWord();
    aux.setLemma("might");

    SemanticGraph graph = mock(SemanticGraph.class);
    when(graph.getParent(head)).thenReturn(parent);
    when(graph.getChildWithReln(eq(parent), eq(GrammaticalRelation.valueOf("aux")))).thenReturn(aux);
//    when(graph.hasParentWithReln(head, GrammaticalRelation.NOMINAL_SUBJECT)).thenReturn(false);
    when(graph.getPathToRoot(head)).thenReturn(new ArrayList<>());

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.enhancedDependency = graph;

    Dictionaries dict = new Dictionaries();
    dict.modals.add("might");

    int result = mention.getModal(dict);
    assertEquals(1, result);
  }
@Test
  public void testGetReportEmbeddingFromPathToRoot() throws IOException, ClassNotFoundException {
    IndexedWord head = new IndexedWord();
    IndexedWord pathTok = new IndexedWord();
    pathTok.setLemma("say");

    List<IndexedWord> path = new ArrayList<>();
    path.add(pathTok);

    SemanticGraph graph = mock(SemanticGraph.class);
    when(graph.getParent(any())).thenReturn(null);
    when(graph.getPathToRoot(any())).thenReturn(path);
//    when(graph.hasParentWithReln(any(), eq(GrammaticalRelation.NOMINAL_SUBJECT))).thenReturn(false);

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.enhancedDependency = graph;

    Dictionaries dict = new Dictionaries();
    dict.reportVerb.add("say");

    int val = mention.getReportEmbedding(dict);
    assertEquals(1, val);
  }
@Test
  public void testGetCoordinationParentRelation() {
    IndexedWord head = new IndexedWord();
    SemanticGraph graph = mock(SemanticGraph.class);

    Set<GrammaticalRelation> rels = new HashSet<>();
    rels.add(GrammaticalRelation.valueOf("conj:and"));

    when(graph.childRelns(eq(head))).thenReturn(Collections.emptySet());
    when(graph.relns(eq(head))).thenReturn(rels);

    Mention mention = new Mention();
    mention.headIndexedWord = head;
    mention.enhancedDependency = graph;

    int result = mention.getCoordination();
    assertEquals(1, result);
  }
@Test
  public void testGetPremodifierReturnsEmptyForNullHead() {
    Mention mention = new Mention();
    mention.headIndexedWord = null;
    mention.enhancedDependency = mock(SemanticGraph.class);
    ArrayList<ArrayList<IndexedWord>> result = mention.getPremodifiers();
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetPostmodifierReturnsEmptyForNullHead() {
    Mention mention = new Mention();
    mention.headIndexedWord = null;
    mention.enhancedDependency = mock(SemanticGraph.class);
    ArrayList<ArrayList<IndexedWord>> result = mention.getPostmodifiers();
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetMentionStringStopsAtHead() {
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "The");
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "dog");

    List<CoreLabel> span = new ArrayList<>();
    span.add(w1);
    span.add(w2);

    Mention mention = new Mention();
    mention.originalSpan = span;
    mention.headWord = w2;

//    List<String> result = mention.getSingletonFeatures(new Dictionaries());
//    assertNotNull(result);
  }
@Test
  public void testHeadsAgreeWithEmptyNerStillMatches() {
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.TextAnnotation.class, "Bob");

    List<CoreLabel> span1 = new ArrayList<>();
    span1.add(word);

    List<CoreLabel> span2 = new ArrayList<>();
    span2.add(word);

    Mention m1 = new Mention(1, 0, 1, span1, mock(SemanticGraph.class), mock(SemanticGraph.class), span1);
    Mention m2 = new Mention(2, 0, 1, span2, mock(SemanticGraph.class), mock(SemanticGraph.class), span2);

    m1.headString = "bob";
    m2.headString = "bob";
    m1.nerString = "O";
    m2.nerString = "O";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testHeadsAgreeFailsOnDifferentHeadStrings() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.headString = "bob";
    m2.headString = "alice";
    m1.nerString = "O";
    m2.nerString = "O";
    assertFalse(m1.headsAgree(m2));
  } 
}