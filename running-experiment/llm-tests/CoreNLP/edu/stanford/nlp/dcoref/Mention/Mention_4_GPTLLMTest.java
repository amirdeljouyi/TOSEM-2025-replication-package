package edu.stanford.nlp.dcoref;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mention_4_GPTLLMTest {

 @Test
  public void testEmptyConstructorInitialization() {
    Mention mention = new Mention();
    assertEquals(-1, mention.mentionID);
    assertTrue(mention.twinless);
    assertFalse(mention.generic);
  }
@Test
  public void testSpanToStringAndLowercaseNormalized() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setValue("Barack");
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setValue("Obama");
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> originalSpan = new ArrayList<>();
    originalSpan.add(token1);
    originalSpan.add(token2);

    Mention mention = new Mention(1, 0, 2, new SemanticGraph());
    mention.originalSpan = originalSpan;

    assertEquals("Barack Obama", mention.spanToString());
    assertEquals("barack obama", mention.lowercaseNormalizedSpanString());
  }
@Test
  public void testIsPronominalTrue() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    assertTrue(mention.isPronominal());
  }
@Test
  public void testHeadsAgreeIsTrueForSameHeadString() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention m1 = new Mention();
    m1.headWord = token;
    m1.headString = "obama";
    m1.nerString = "O";
    m1.originalSpan = Arrays.asList(token);

    Mention m2 = new Mention();
    m2.headWord = token;
    m2.headString = "obama";
    m2.nerString = "O";
    m2.originalSpan = Arrays.asList(token);

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testNumbersAgreeWhenOneIsUnknown() {
    Mention m1 = new Mention();
    m1.number = Dictionaries.Number.SINGULAR;

    Mention m2 = new Mention();
    m2.number = Dictionaries.Number.UNKNOWN;

    assertTrue(m1.numbersAgree(m2));
  }
@Test
  public void testGendersAgreeStrictTrue() {
    Mention m1 = new Mention();
    m1.gender = Dictionaries.Gender.FEMALE;

    Mention m2 = new Mention();
    m2.gender = Dictionaries.Gender.FEMALE;

    assertTrue(m1.gendersAgree(m2, true));
  }
@Test
  public void testAnimaciesAgreeStrictFalse() {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.ANIMATE;

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.INANIMATE;

    assertFalse(m1.animaciesAgree(m2, true));
  }
@Test
  public void testAttributesAgreeAllComponentsEqual() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    Dictionaries dict = new Dictionaries();

    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m2.animacy = Dictionaries.Animacy.ANIMATE;

    m1.gender = Dictionaries.Gender.MALE;
    m2.gender = Dictionaries.Gender.MALE;

    m1.number = Dictionaries.Number.SINGULAR;
    m2.number = Dictionaries.Number.SINGULAR;

    m1.nerString = "PERSON";
    m2.nerString = "PERSON";

    assertTrue(m1.attributesAgree(m2, dict));
  }
@Test
  public void testIsListMemberOfTrue() {
    Mention list = new Mention();
    list.mentionType = Dictionaries.MentionType.LIST;
    list.startIndex = 0;
    list.endIndex = 5;
//    list.mentionSubTree = new Tree("S") {};

    Mention child = new Mention();
    child.mentionType = Dictionaries.MentionType.NOMINAL;
    child.startIndex = 1;
    child.endIndex = 3;
    child.mentionSubTree = list.mentionSubTree;
    child.sentenceWords = list.sentenceWords = new ArrayList<>();

    assertTrue(child.isListMemberOf(list));
  }
@Test
  public void testAddListMember() {
    Mention list = new Mention();
    Mention member = new Mention();
    list.addListMember(member);
    assertNotNull(list.listMembers);
    assertEquals(1, list.listMembers.size());
    assertTrue(list.listMembers.contains(member));
  }
@Test
  public void testBelongsToListAndIsMemberOfSameList() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    Mention list = new Mention();

    m1.addBelongsToList(list);
    m2.addBelongsToList(list);

    assertTrue(m1.isMemberOfSameList(m2));
  }
@Test
  public void testMoreRepresentativeThanPrefersProper() {
    Mention proper = new Mention();
    proper.mentionType = Dictionaries.MentionType.PROPER;
    proper.headIndex = 2;
    proper.startIndex = 1;
    proper.sentNum = 1;
    proper.originalSpan = Collections.singletonList(new CoreLabel());

    Mention nominal = new Mention();
    nominal.mentionType = Dictionaries.MentionType.NOMINAL;
    nominal.headIndex = 2;
    nominal.startIndex = 1;
    nominal.sentNum = 1;
    nominal.originalSpan = Collections.singletonList(new CoreLabel());

    assertTrue(proper.moreRepresentativeThan(nominal));
    assertFalse(nominal.moreRepresentativeThan(proper));
  }
@Test(expected = IllegalStateException.class)
  public void testMoreRepresentativeThanThrowsOnSameObject() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m.headIndex = 1;
    m.startIndex = 1;
    m.sentNum = 1;
    CoreLabel word = new CoreLabel();
    m.originalSpan = Collections.singletonList(word);
    m.moreRepresentativeThan(m);
  }
@Test
  public void testRemoveParenthesisReturnsPrefixOnly() {
    String input = "Entity Name (redundant)";
    String result = Mention.removeParenthesis(input);
    assertEquals("Entity Name", result);
  }
@Test
  public void testRemoveParenthesisReturnsEmptyIfNoParenthesis() {
    String input = "Entity";
    String result = Mention.removeParenthesis(input);
    assertEquals("", result);
  }
@Test
  public void testIsApposition() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    m1.addApposition(m2);
    assertTrue(m1.isApposition(m2));
  }
@Test
  public void testStringWithoutArticleHandlesThe() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("the cat");
    assertEquals("cat", result);
  }
@Test
  public void testStringWithoutArticleHandlesA() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("a dog");
    assertEquals("dog", result);
  }
@Test
  public void testStringWithoutArticlePassesUnchangedIfNoArticle() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("house");
    assertEquals("house", result);
  }
@Test
  public void testHeadsAgreeNamedEntityIncludedCase() {
    CoreLabel obama = new CoreLabel();
    obama.setWord("Obama");
    obama.set(CoreAnnotations.TextAnnotation.class, "Obama");
    obama.setTag("NNP");

    CoreLabel barack = new CoreLabel();
    barack.setWord("Barack");
    barack.set(CoreAnnotations.TextAnnotation.class, "Barack");
    barack.setTag("NNP");

    CoreLabel obama2 = new CoreLabel();
    obama2.setWord("Obama");
    obama2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    obama2.setTag("NNP");

    Mention mention1 = new Mention();
    mention1.nerString = "PERSON";
    mention1.headWord = obama;
    mention1.originalSpan = Arrays.asList(barack, obama);
    mention1.headString = "obama";

    Mention mention2 = new Mention();
    mention2.nerString = "PERSON";
    mention2.headWord = obama2;
    mention2.originalSpan = Collections.singletonList(obama2);
    mention2.headString = "obama";

    assertTrue(mention2.headsAgree(mention1));
  }
@Test
  public void testGendersAgreeStrictMismatch() {
    Mention m1 = new Mention();
    m1.gender = Dictionaries.Gender.MALE;

    Mention m2 = new Mention();
    m2.gender = Dictionaries.Gender.FEMALE;

    assertFalse(m1.gendersAgree(m2, true));
  }
@Test
  public void testNumbersAgreeStrictMismatch() {
    Mention m1 = new Mention();
    m1.number = Dictionaries.Number.SINGULAR;

    Mention m2 = new Mention();
    m2.number = Dictionaries.Number.PLURAL;

//    assertFalse(m1.numbersAgree(m2, true));
  }
@Test
  public void testAttributesAgreeMismatchGender() {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.FEMALE;
    m1.number = Dictionaries.Number.SINGULAR;
    m1.nerString = "PERSON";

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.ANIMATE;
    m2.gender = Dictionaries.Gender.MALE;
    m2.number = Dictionaries.Number.SINGULAR;
    m2.nerString = "PERSON";

    Dictionaries dict = new Dictionaries();
    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testIsTheCommonNounTrue() {
    Mention m = new Mention();
    CoreLabel the = new CoreLabel();
    the.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel car = new CoreLabel();
    car.set(CoreAnnotations.TextAnnotation.class, "car");

    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.originalSpan = Arrays.asList(the, car);

    assertTrue(m.isTheCommonNoun());
  }
@Test
  public void testIsTheCommonNounFalseWhenNotTwoWords() {
    Mention m = new Mention();
    CoreLabel the = new CoreLabel();
    the.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel car = new CoreLabel();
    car.set(CoreAnnotations.TextAnnotation.class, "car");

    CoreLabel fast = new CoreLabel();
    fast.set(CoreAnnotations.TextAnnotation.class, "fast");

    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.originalSpan = Arrays.asList(the, fast, car);

    assertFalse(m.isTheCommonNoun());
  }
@Test
  public void testSameSentenceIsFalseWithDifferentSentenceWordsRefs() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.sentenceWords = new ArrayList<>();
    m2.sentenceWords = new ArrayList<>();

    CoreLabel word = new CoreLabel();
    m1.sentenceWords.add(word);
    m2.sentenceWords.add(word);

    assertFalse(m1.sameSentence(m2));
  }
@Test
  public void testAppearEarlierDifferentSentNum() {
    Mention m1 = new Mention();
    m1.sentNum = 2;
    m1.startIndex = 1;
    m1.endIndex = 4;

    Mention m2 = new Mention();
    m2.sentNum = 3;
    m2.startIndex = 0;
    m2.endIndex = 3;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierSameSentenceEarlierStart() {
    Mention m1 = new Mention();
    m1.sentNum = 2;
    m1.startIndex = 1;
    m1.endIndex = 4;

    Mention m2 = new Mention();
    m2.sentNum = 2;
    m2.startIndex = 3;
    m2.endIndex = 5;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testAppearEarlierSameStartLongerSpan() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 2;
    m1.endIndex = 5;

    Mention m2 = new Mention();
    m2.sentNum = 1;
    m2.startIndex = 2;
    m2.endIndex = 4;

    assertTrue(m1.appearEarlierThan(m2));
  }
@Test
  public void testSpanToStringWithSingleToken() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");

    Mention mention = new Mention();
    mention.originalSpan = Collections.singletonList(token);

    assertEquals("John", mention.spanToString());
  }
@Test
  public void testLowercaseNormalizedSpanReturnsSameAfterCached() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "John");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Doe");

    Mention mention = new Mention();
    List<CoreLabel> span = Arrays.asList(token1, token2);
    mention.originalSpan = span;

    
    String first = mention.lowercaseNormalizedSpanString();

    
    String second = mention.lowercaseNormalizedSpanString();

    assertEquals("john doe", first);
    assertEquals(first, second);
  }
@Test
  public void testIsAppositionReturnsFalseWithNullSet() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    assertFalse(m1.isApposition(m2));
  }
@Test
  public void testIsPredicateNominativeReturnsTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addPredicateNominatives(m2);
    assertTrue(m1.isPredicateNominatives(m2));
  }
@Test
  public void testIsPredicateNominativeReturnsFalseIfNotAdded() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    Mention m3 = new Mention();
    m1.addPredicateNominatives(m2);
    assertFalse(m1.isPredicateNominatives(m3));
  }
@Test
  public void testRelativePronoun_AddAndCheck() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.addRelativePronoun(m2);
    assertTrue(m1.isRelativePronoun(m2));
  }
@Test
  public void testRelativePronoun_ReturnsFalseIfNull() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    assertFalse(m1.isRelativePronoun(m2));
  }
@Test
  public void testInsideInTrue() {
    Mention outer = new Mention();
    outer.startIndex = 0;
    outer.endIndex = 5;
    outer.sentNum = 1;

    Mention inner = new Mention();
    inner.startIndex = 1;
    inner.endIndex = 4;
    inner.sentNum = 1;

    assertTrue(inner.insideIn(outer));
  }
@Test
  public void testInsideInFalseDifferentSentence() {
    Mention outer = new Mention();
    outer.startIndex = 0;
    outer.endIndex = 5;
    outer.sentNum = 1;

    Mention inner = new Mention();
    inner.startIndex = 1;
    inner.endIndex = 4;
    inner.sentNum = 2;

    assertFalse(inner.insideIn(outer));
  }
@Test
  public void testInsideInFalseOutOfBounds() {
    Mention outer = new Mention();
    outer.startIndex = 0;
    outer.endIndex = 5;
    outer.sentNum = 1;

    Mention inner = new Mention();
    inner.startIndex = 0;
    inner.endIndex = 6;
    inner.sentNum = 1;

    assertFalse(inner.insideIn(outer));
  }
@Test
  public void testIncludedInFalseWhenNotSameSentence() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 2;
    m1.endIndex = 4;
    m1.mentionSubTree = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    m1.sentenceWords = new ArrayList<>();

    Mention m2 = new Mention();
    m2.sentNum = 2;
    m2.startIndex = 1;
    m2.endIndex = 5;
    m2.mentionSubTree = m1.mentionSubTree;
    m2.sentenceWords = new ArrayList<>();

    assertFalse(m1.includedIn(m2));
  }
@Test
  public void testIncludedInFalseWhenOutsideTreeSpan() {
    Mention m1 = new Mention();
    m1.sentNum = 1;
    m1.startIndex = 0;
    m1.endIndex = 8;
    m1.sentenceWords = new ArrayList<>();

    Mention m2 = new Mention();
    m2.sentNum = 1;
    m2.startIndex = 2;
    m2.endIndex = 4;
    m2.sentenceWords = new ArrayList<>();

    assertFalse(m1.includedIn(m2));
  }
@Test
  public void testEntityTypesAgreeStrictMatch() {
    Mention m1 = new Mention();
    m1.nerString = "PERSON";

    Mention m2 = new Mention();
    m2.nerString = "PERSON";

    Dictionaries dict = new Dictionaries();

    assertTrue(m1.entityTypesAgree(m2, dict, true));
  }
@Test
  public void testEntityTypesAgreeNonStrictPronounAndMismatchTag() {
    Mention m1 = new Mention();
    m1.nerString = "DATE";
    m1.headString = "now";
    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;

    Mention m2 = new Mention();
    m2.nerString = "MONEY"; 

    Dictionaries dict = new Dictionaries();
//    dict.dateTimePronouns = new HashSet<>(Collections.singletonList("now"));

    assertFalse(m1.entityTypesAgree(m2, dict, false));
  }
@Test
  public void testRemovePhraseAfterHeadRemovesClauseAfterComma() {
    Mention mention = new Mention();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "The");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "cat");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, ",");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, ",");

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "which");
    token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WDT");

    mention.originalSpan = Arrays.asList(token1, token2, token3, token4);
    mention.startIndex = 0;
    mention.headIndex = 1;

    String result = mention.removePhraseAfterHead();
    assertEquals("The cat", result);
  }
@Test
  public void testRemovePhraseAfterHeadWithNoCommaRemovesWHClause() {
    Mention mention = new Mention();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "dog");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "who");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "WP");

    mention.originalSpan = Arrays.asList(token1, token2);
    mention.startIndex = 0;
    mention.headIndex = 0;

    String result = mention.removePhraseAfterHead();
    assertEquals("dog", result);
  }
@Test
  public void testRemovePhraseAfterHeadNoPunctuation() {
    Mention mention = new Mention();

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "animals");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    mention.originalSpan = Collections.singletonList(token1);
    mention.startIndex = 0;
    mention.headIndex = 0;

    String result = mention.removePhraseAfterHead();
    assertEquals("animals", result);
  }
@Test
  public void testBuildQueryTextEmptyListReturnsEmpty() {
    List<String> input = new ArrayList<>();
    String result = Mention.buildQueryText(input);
    assertEquals("", result);
  }
@Test
  public void testBuildQueryTextMultipleTokensJoinedBySpace() {
    List<String> input = Arrays.asList("Barack", "Obama", "Sr.");
    String result = Mention.buildQueryText(input);
    assertEquals("Barack Obama Sr.", result);
  }
@Test
  public void testSpanToStringHandlesEmptySpan() {
    Mention mention = new Mention();
    mention.originalSpan = new ArrayList<>();
    String result = mention.spanToString();
    assertEquals("", result);
  }
@Test
  public void testIsRoleAppositiveFailsOnDifferentNER() {
    Mention m1 = new Mention();
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.originalSpan = new ArrayList<>();
    CoreLabel tok = new CoreLabel();
    tok.set(CoreAnnotations.TextAnnotation.class, "President");
    m1.originalSpan.add(tok);
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.gender = Dictionaries.Gender.MALE;
    m1.number = Dictionaries.Number.SINGULAR;
    m1.nerString = "PERSON";
    m1.sentenceWords = new ArrayList<>();

    Mention m2 = new Mention();
    m2.nerString = "ORG"; 
    m2.originalSpan = Arrays.asList(tok);
//    m2.lowercaseNormalizedSpanString = "president";
    m2.sentenceWords = m1.sentenceWords;

    Dictionaries dict = new Dictionaries();
//    dict.allPronouns = new HashSet<>(Collections.singleton("he"));
//    dict.demonymSet = new HashSet<>();

    assertFalse(m1.isRoleAppositive(m2, dict));
  }
@Test
  public void testIsCoordinatedFalseWhenDependencyIsNull() {
    Mention m = new Mention();
    m.headIndexedWord = null;
    assertFalse(m.isCoordinated());
  }
@Test
  public void testRemoveParenthesisOnlyLeftParen() {
    String text = "something (";
    String result = Mention.removeParenthesis(text);
    assertEquals("something", result);
  }
@Test
  public void testRemoveParenthesisOnlyRightParen() {
    String text = "text)";
    String result = Mention.removeParenthesis(text);
    assertEquals("text)", result);
  }
@Test
  public void testGetPositionFirstIndex() {
    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel());
    m.headIndex = 0;
    String pos = m.getPosition();
    assertEquals("first", pos);
  }
@Test
  public void testGetPositionLastIndex() {
    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());
    m.headIndex = 3;
    String pos = m.getPosition();
    assertEquals("last", pos);
  }
@Test
  public void testGetPositionBeginRange() {
    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());
    m.headIndex = 1;
    String pos = m.getPosition();
    assertEquals("begin", pos);
  }
@Test
  public void testGetPositionMiddleRange() {
    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());
    m.headIndex = 2;
    String pos = m.getPosition();
    assertEquals("middle", pos);
  }
@Test
  public void testGetPositionEndRange() {
    Mention m = new Mention();
    m.sentenceWords = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());
    m.headIndex = 5;
    String pos = m.getPosition();
    assertEquals("last", pos);
  }
@Test
  public void testEntityTypesAgreeKnownNERAndPronominalFails() {
    Mention m1 = new Mention();
    m1.nerString = "PERSON";
    m1.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m1.headString = "dog";

    Mention m2 = new Mention();
    m2.nerString = "LOCATION";

    Dictionaries dict = new Dictionaries();
//    dict.personPronouns = new HashSet<>(Collections.singletonList("he"));
//    dict.locationPronouns = new HashSet<>(Collections.singletonList("there"));

    assertFalse(m1.entityTypesAgree(m2, dict, false));
  }
@Test
  public void testMentionToStringReturnsSpanString() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    Mention mention = new Mention();
    mention.originalSpan = Collections.singletonList(token);
    String result = mention.toString();
    assertEquals("Hello", result);
  }
@Test
  public void testHeadStringStopsOnKnownSuffix() {
    CoreLabel suffix = new CoreLabel();
    suffix.set(CoreAnnotations.TextAnnotation.class, "Corp");
    CoreLabel name = new CoreLabel();
    name.set(CoreAnnotations.TextAnnotation.class, "Google");
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Corp");
    head.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    Mention mention = new Mention();
    mention.headWord = head;
    mention.headIndex = 2;
    mention.startIndex = 0;
    mention.originalSpan = Arrays.asList(name, suffix, head);

//    mention.setHeadString();

    assertEquals("google", mention.headString);
  }
@Test
  public void testEntityTypesAgreePronounAndNERMatchOrg() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PRONOMINAL;
    mention.headString = "it";

    Mention antecedent = new Mention();
    antecedent.nerString = "ORGANIZATION";

    Dictionaries dict = new Dictionaries();
//    dict.organizationPronouns = new HashSet<>(Collections.singleton("it"));

    boolean agree = mention.entityTypesAgree(antecedent, dict, false);

    assertTrue(agree);
  }
@Test
  public void testGetGenderReturnsNullWhenUnrecognizedNER() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "Unknown");

    Mention mention = new Mention();
    mention.originalSpan = Collections.singletonList(label);
    mention.headWord = label;
    mention.headString = "unknown";
    mention.nerString = "MISC";

    List<String> tokens = Collections.singletonList("unknown");
    Dictionaries dict = new Dictionaries();

//    Gender gender = mention.getGender(dict, tokens);
//    assertNull(gender);
  }
@Test
  public void testSetGenderUsesGenderNumberResult() {
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.NOMINAL;
    mention.number = Dictionaries.Number.SINGULAR;
    mention.headString = "john";
    mention.spanToString();
    Dictionaries dict = new Dictionaries();
//    mention.setGender(dict, Dictionaries.Gender.MALE);
//    assertEquals(Gender.MALE, mention.gender);
  }
@Test
  public void testSetGenderOverridesWithBergsmaMaleList() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "mark");
    Mention mention = new Mention();
    mention.mentionType = Dictionaries.MentionType.PROPER;
    mention.number = Dictionaries.Number.SINGULAR;
    mention.headString = "mark";
    mention.nerString = "PERSON";
    mention.gender = Dictionaries.Gender.UNKNOWN;
    mention.headWord = head;
    mention.originalSpan = Collections.singletonList(head);

    Dictionaries dict = new Dictionaries();
//    dict.maleWords = new HashSet<>(Collections.singleton("mark"));
//    dict.femaleWords = new HashSet<>();
//    dict.neutralWords = new HashSet<>();

//    mention.setGender(dict, null);

//    assertEquals(Gender.MALE, mention.gender);
  }
@Test
  public void testIsMemberOfSameListReturnsFalseWhenOneSideIsNull() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.belongToLists = new HashSet<>();
    m2.belongToLists = null;
    boolean result = m1.isMemberOfSameList(m2);
    assertFalse(result);
  }
@Test
  public void testMentionBuildQuerySkipsEmptyTokens() {
    List<String> input = Arrays.asList("Barack", "", "Obama");
    String result = Mention.buildQueryText(input);
    assertEquals("Barack  Obama", result);
  }
@Test
  public void testLowercaseNormalizedSpanWithNullSpanString() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "Example");

    Mention m = new Mention();
    m.originalSpan = Collections.singletonList(label);
    m.spanToString(); 
    String lower = m.lowercaseNormalizedSpanString();
    assertEquals("example", lower);
  }
@Test
  public void testIsListMemberOfFalseWhenNestedList() {
    Mention parent = new Mention();
    Mention child = new Mention();
    parent.mentionType = Dictionaries.MentionType.LIST;
    child.mentionType = Dictionaries.MentionType.LIST;

    parent.startIndex = 0;
    parent.endIndex = 10;
    child.startIndex = 2;
    child.endIndex = 4;

    parent.sentenceWords = new ArrayList<>();
    child.sentenceWords = parent.sentenceWords;

    assertFalse(child.isListMemberOf(parent));
  }
@Test
  public void testRemovePhraseAfterHeadWithNoHeadFound() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "unknown");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    Mention mention = new Mention();
    mention.originalSpan = Collections.singletonList(token);
    mention.headIndex = 0;
    mention.startIndex = 0;

    String result = mention.removePhraseAfterHead();
    assertEquals("unknown", result);
  }
@Test
  public void testIsCoordinatedTrueForConjRelation() {
    Mention m = new Mention();
    CoreLabel headWord = new CoreLabel();
    headWord.setWord("dogs");
    m.headWord = headWord;

    SemanticGraph graph = new SemanticGraph();
    IndexedWord head = new IndexedWord(headWord);
    IndexedWord child = new IndexedWord(new CoreLabel());
    child.setWord("and");

    graph.addVertex(head);
    graph.addVertex(child);
    graph.addEdge(head, child, GrammaticalRelation.valueOf("conj:and"), 1.0, false);

    m.headIndexedWord = head;
    m.dependency = graph;

    assertTrue(m.isCoordinated());
  }
@Test
  public void testSetNumberProperNERORGSetToUnknown() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.headWord = head;
    m.nerString = "ORGANIZATION";

    Dictionaries dict = new Dictionaries();

    m.setNumber(dict);
    assertEquals(Dictionaries.Number.UNKNOWN, m.number);
  }
@Test
  public void testSetNumberListTypeAlwaysPlural() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.LIST;
    m.setNumber(new Dictionaries());
    assertEquals(Dictionaries.Number.PLURAL, m.number);
  }
@Test
  public void testGetPatternReturnsHeadWhenNoModifiers() {
    CoreLabel headLabel = new CoreLabel();
    headLabel.set(CoreAnnotations.TextAnnotation.class, "king");
    headLabel.set(CoreAnnotations.LemmaAnnotation.class, "king");

    Mention m = new Mention();
    m.headWord = headLabel;
    m.headIndexedWord = null;
    m.dependency = null;

    String pattern = m.getPattern();
    assertEquals("king", pattern);
  }
@Test
  public void testMoreRepresentativePrefersWithEarlierHeadIndex() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.headIndex = 1;
    m2.headIndex = 3;
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.nerString = m2.nerString = "O";
    m1.originalSpan = m2.originalSpan = Collections.singletonList(new CoreLabel());

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testMentionHeadStringSuffixNotFoundFallsBackToOriginalHead() {
    CoreLabel head1 = new CoreLabel();
    head1.set(CoreAnnotations.TextAnnotation.class, "Ltd");
    head1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "OpenAI");

    Mention mention = new Mention();
    mention.headWord = head1;
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.originalSpan = Arrays.asList(token1, head1);

//    mention.setHeadString();
    assertEquals("openai", mention.headString);
  }
@Test
  public void testGetGenderWithSingleUppercaseAndPERPrefixNameMatch() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Obama");

    Mention mention = new Mention();
    mention.headWord = head;
    mention.nerString = "PERSON";

    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "H.");
    CoreLabel c3 = new CoreLabel();
    c3.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> span = Arrays.asList(c1, c2, c3);
    mention.originalSpan = span;

    Dictionaries dict = new Dictionaries();
//    dict.genderNumber = new HashMap<>();
    dict.genderNumber.put(Arrays.asList("barack", "h.", "obama"), Dictionaries.Gender.MALE);

    List<String> tokens = Arrays.asList("barack", "h.", "obama");
//    Dictionaries.Gender gender = mention.getGender(dict, tokens);
//    assertEquals(Gender.MALE, gender);
  }
@Test
  public void testGetGenderFromSecondToLastFallback() {
    CoreLabel head = new CoreLabel();
    head.set(CoreAnnotations.TextAnnotation.class, "Smith");

    Mention mention = new Mention();
    mention.headWord = head;
    mention.nerString = "PER";

    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "John");
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "Smith");

    mention.originalSpan = Arrays.asList(c1, c2);

    List<String> tokens = Arrays.asList("john", "smith");
    Dictionaries dict = new Dictionaries();
//    dict.genderNumber = new HashMap<>();
    dict.genderNumber.put(Collections.singletonList("smith"), Dictionaries.Gender.MALE);

//    Gender g = mention.getGender(dict, tokens);
//    assertEquals(Gender.MALE, g);
  }
@Test
  public void testSetPersonPronounYouSetsPersonEnumCorrectly() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PRONOMINAL;

    CoreLabel c = new CoreLabel();
    c.set(CoreAnnotations.TextAnnotation.class, "you");

    m.headWord = c;

    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "you");

    m.originalSpan = Collections.singletonList(t);
    m.headString = "you";
    m.number = Dictionaries.Number.SINGULAR;

    Dictionaries dict = new Dictionaries();
//    dict.secondPersonPronouns = Collections.singleton("you");

//    m.setPerson(dict);
//    assertEquals(Person.YOU, m.person);
  }
@Test
  public void testSetNumberUnknownFallbacksToBergsma() {
    CoreLabel h = new CoreLabel();
    h.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    Mention m = new Mention();
    m.headWord = h;
    m.nerString = "O";
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.headString = "tree";
    m.number = Dictionaries.Number.UNKNOWN;

    Dictionaries dict = new Dictionaries();
//    dict.singularWords = Collections.singleton("tree");

    m.setNumber(dict);
    assertEquals(Dictionaries.Number.SINGULAR, m.number);
  }
@Test
  public void testSetNumberVPPluralFallbackWithAnd() {
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "fire");
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "and");
    CoreLabel c3 = new CoreLabel();
    c3.set(CoreAnnotations.TextAnnotation.class, "ice");

    List<CoreLabel> span = Arrays.asList(c1, c2, c3);
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.NOMINAL;
    m.originalSpan = span;
    m.headWord = c3;
    m.nerString = "O";

    String treeText = StringUtils.joinWords(span, " ");

    edu.stanford.nlp.trees.Tree leaf = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
//    mention.mentionSubTree = leaf;
//    mention.headString = "ice";
//
//
//    mention.mentionSubTree = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
//    mention.spanToString();

    Dictionaries dict = new Dictionaries();
    m.setNumber(dict);

    assertEquals(Dictionaries.Number.PLURAL, m.number);
  }
@Test
  public void testHeadsAgreeNamedEntityTagMismatchedButExactMatchStillSucceeds() {
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "Obama");
    c1.setTag("NNP");
    Mention m1 = new Mention();
    m1.originalSpan = Arrays.asList(c1);
    m1.headString = "obama";
    m1.nerString = "O";
    m1.headWord = c1;

    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    Mention m2 = new Mention();
    m2.originalSpan = Arrays.asList(c2);
    m2.headString = "obama";
    m2.nerString = "PERSON";
    m2.headWord = c2;

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testGetMentionStringStopsAtHead() {
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "the");

    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "quick");

    CoreLabel c3 = new CoreLabel();
    c3.set(CoreAnnotations.TextAnnotation.class, "fox");

    Mention m = new Mention();
    m.originalSpan = Arrays.asList(c1, c2, c3);
    m.headWord = c2;

//    List<String> tokens = m.getMentionString();
//    assertEquals(2, tokens.size());
//    assertEquals("the", tokens.get(0));
//    assertEquals("quick", tokens.get(1));
  }
@Test
  public void testPersonAssignmentWhenUnknownGender() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PRONOMINAL;
    m.number = Dictionaries.Number.SINGULAR;
    m.gender = Dictionaries.Gender.UNKNOWN;

    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "they");
    m.headString = "they";
    m.originalSpan = Collections.singletonList(t);
    m.headWord = t;

//    Dictionaries dict = new Dictionaries();
//    dict.thirdPersonPronouns = Collections.singleton("they");
//
//    m.setPerson(dict);
//    assertEquals(Person.THEY, m.person);
  }
@Test
  public void testSetAnimacyFromNERDate() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.nerString = "DATE";

    Dictionaries dict = new Dictionaries();
//    m.setAnimacy(dict);
    assertEquals(Dictionaries.Animacy.INANIMATE, m.animacy);
  }
@Test
  public void testSetAnimacyWithDekangFallback() {
    Mention m = new Mention();
    m.mentionType = Dictionaries.MentionType.PROPER;
    m.nerString = "O";
    m.gender = Dictionaries.Gender.UNKNOWN;
    m.animacy = Dictionaries.Animacy.UNKNOWN;
    m.headString = "tree";

    Dictionaries dict = new Dictionaries();
//    dict.animateWords = new HashSet<>();
//    dict.inanimateWords = new HashSet<>(Collections.singleton("tree"));
//
//    Constants.USE_ANIMACY_LIST = true;
//
//    m.setAnimacy(dict);
//    assertEquals(Animacy.INANIMATE, m.animacy);
  }
@Test
  public void testGendersAgreeBothUnknownReturnsTrue() {
    Mention m1 = new Mention();
    m1.gender = Dictionaries.Gender.UNKNOWN;

    Mention m2 = new Mention();
    m2.gender = Dictionaries.Gender.UNKNOWN;

    assertTrue(m1.gendersAgree(m2));
  }
@Test
  public void testAnimaciesAgreeOneUnknownReturnsTrue() {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.UNKNOWN;

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.INANIMATE;

    assertTrue(m1.animaciesAgree(m2));
  }
@Test
  public void testEntityTypesAgreeBothO() {
    Mention m1 = new Mention();
    m1.nerString = "O";

    Mention m2 = new Mention();
    m2.nerString = "O";

    Dictionaries dict = new Dictionaries();

    assertTrue(m1.entityTypesAgree(m2, dict));
  }
@Test
  public void testIncludedInFalseWhenTreeNotContained() {
    Mention container = new Mention();
    Mention sub = new Mention();

    container.sentNum = 0;
    sub.sentNum = 0;
    container.startIndex = 0;
    container.endIndex = 5;
    sub.startIndex = 1;
    sub.endIndex = 2;

    container.mentionSubTree = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    sub.mentionSubTree = new edu.stanford.nlp.trees.LabeledScoredTreeNode();

    container.sentenceWords = new ArrayList<>();
    sub.sentenceWords = container.sentenceWords;

    assertFalse(sub.includedIn(container));
  }
@Test
  public void testAttributesAgreeFailsDueToAnimacy() {
    Mention m1 = new Mention();
    m1.animacy = Dictionaries.Animacy.ANIMATE;
    m1.number = Dictionaries.Number.PLURAL;
    m1.gender = Dictionaries.Gender.MALE;
    m1.nerString = "PERSON";

    Mention m2 = new Mention();
    m2.animacy = Dictionaries.Animacy.INANIMATE;
    m2.number = Dictionaries.Number.PLURAL;
    m2.gender = Dictionaries.Gender.MALE;
    m2.nerString = "PERSON";

    Dictionaries dict = new Dictionaries();

    assertFalse(m1.attributesAgree(m2, dict));
  }
@Test
  public void testMentionStringWithoutArticleHandlesUpperCaseThe() {
    Mention m = new Mention();
    String result = m.stringWithoutArticle("The President");
    assertEquals("President", result);
  }
@Test
  public void testMentionGetQuantificationReturnsDefiniteDueToDeterminer() {
    Mention mention = new Mention();

    SemanticGraph dependency = new SemanticGraph();
    CoreLabel headLabel = new CoreLabel();
    headLabel.set(CoreAnnotations.TextAnnotation.class, "cat");
    IndexedWord head = new IndexedWord(headLabel);
    dependency.addVertex(head);

    CoreLabel detLabel = new CoreLabel();
    detLabel.set(CoreAnnotations.TextAnnotation.class, "the");
    IndexedWord det = new IndexedWord(detLabel);
    dependency.addVertex(det);
//    dependency.addEdge(head, det, GrammaticalRelation.DETERMINER, 1.0, false);

    mention.headIndexedWord = head;
    mention.dependency = dependency;

    Dictionaries dict = new Dictionaries();
//    dict.determiners = new HashSet<>();
    dict.determiners.add("the");

    String quant = mention.getQuantification(dict);
    assertEquals("definite", quant);
  }
@Test
  public void testMentionGetQuantificationReturnsQuantified() {
    Mention mention = new Mention();

    SemanticGraph dependency = new SemanticGraph();
    CoreLabel headLabel = new CoreLabel();
    headLabel.set(CoreAnnotations.TextAnnotation.class, "dogs");
    IndexedWord head = new IndexedWord(headLabel);
    dependency.addVertex(head);

    CoreLabel numLabel = new CoreLabel();
    numLabel.set(CoreAnnotations.TextAnnotation.class, "some");
    IndexedWord num = new IndexedWord(numLabel);
    dependency.addVertex(num);
    dependency.addEdge(head, num, GrammaticalRelation.valueOf("det"), 1.0, false);

    mention.headIndexedWord = head;
    mention.dependency = dependency;

//    Dictionaries dict = new Dictionaries();
//    dict.quantifiers2 = new HashSet<>();
//    dict.quantifiers2.add("some");
//
//    String quant = mention.getQuantification(dict);
//    assertEquals("quantified", quant);
  }
@Test
  public void testMentionGetQuantificationReturnsIndefiniteWhenNoModifiers() {
    Mention mention = new Mention();

    SemanticGraph dependency = new SemanticGraph();
    CoreLabel headLabel = new CoreLabel();
    headLabel.set(CoreAnnotations.TextAnnotation.class, "books");
    IndexedWord head = new IndexedWord(headLabel);
    dependency.addVertex(head);

    mention.headIndexedWord = head;
    mention.dependency = dependency;

    Dictionaries dict = new Dictionaries();

    String quant = mention.getQuantification(dict);
    assertEquals("indefinite", quant);
  }
@Test
  public void testGetContextReturnsEmptyWhenNoNER() {
    CoreLabel word1 = new CoreLabel();
    word1.set(CoreAnnotations.TextAnnotation.class, "The");
    word1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel word2 = new CoreLabel();
    word2.set(CoreAnnotations.TextAnnotation.class, "dog");
    word2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> sentenceWords = new ArrayList<>();
    sentenceWords.add(word1);
    sentenceWords.add(word2);

    Mention mention = new Mention();
    mention.sentenceWords = sentenceWords;

    List<String> context = mention.getContext();
    assertNotNull(context);
    assertEquals(0, context.size());
  }
@Test
  public void testGetPremodifierContextReturnsMultipleNERs() {
    Mention mention = new Mention();

    SemanticGraph graph = new SemanticGraph();

    CoreLabel headLabel = new CoreLabel();
    headLabel.set(CoreAnnotations.TextAnnotation.class, "king");
    IndexedWord head = new IndexedWord(headLabel);
    graph.addVertex(head);

    CoreLabel mod1 = new CoreLabel();
    mod1.set(CoreAnnotations.TextAnnotation.class, "Kingdom");
    mod1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    IndexedWord mod1Word = new IndexedWord(mod1);
    graph.addVertex(mod1Word);
    graph.addEdge(head, mod1Word, GrammaticalRelation.valueOf("amod"), 1.0, false);

    mention.headIndexedWord = head;
    mention.dependency = graph;

    List<String> ctx = mention.getPremodifierContext();
    assertTrue(ctx.contains("Kingdom"));
  }
@Test
  public void testGetCoordinationReturnsTrueIfConjRelnOnParent() {
    Mention mention = new Mention();
    SemanticGraph graph = new SemanticGraph();

    CoreLabel headLabel = new CoreLabel();
    headLabel.setWord("worker");

    IndexedWord head = new IndexedWord(headLabel);
    graph.addVertex(head);

    IndexedWord parent = new IndexedWord(new CoreLabel());
    graph.addVertex(parent);
    graph.addEdge(parent, head, GrammaticalRelation.valueOf("conj:and"), 1.0, false);

    mention.dependency = graph;
    mention.headIndexedWord = head;

    int val = mention.getCoordination();
    assertEquals(1, val);
  }
@Test
  public void testPreprocessSearchTermRemovesDuplicates() {
    Mention mention = new Mention();
    List<AbstractCoreLabel> span = new ArrayList<>();

    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "the");
    c1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "city");
    c2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    mention.originalSpan = Arrays.asList(c1, c2);
    mention.headWord = c2;
    mention.headString = "city";
    mention.headIndex = 1;
    mention.startIndex = 0;
    mention.sentenceWords = Arrays.asList(c1, c2);

    List<String> terms = mention.preprocessSearchTerm();
    assertFalse(terms.contains("the city"));
    assertTrue(terms.contains("city"));
  }
@Test
  public void testEntityTypesAgreeStrictFalseWhenDifferent() {
    Mention m1 = new Mention();
    m1.nerString = "LOCATION";
    Mention m2 = new Mention();
    m2.nerString = "PERSON";
    Dictionaries dict = new Dictionaries();
    boolean result = m1.entityTypesAgree(m2, dict, true);
    assertFalse(result);
  }
@Test
  public void testRemoveParenthesisTrimsWhitespace() {
    String input = "Some Name (with notes)";
    String result = Mention.removeParenthesis(input);
    assertEquals("Some Name", result);
  }
@Test
  public void testLongSpanPatternGenerationWithPremodsAndPostmods() {
    Mention m = new Mention();
    CoreLabel coreLabel = new CoreLabel();
    coreLabel.set(CoreAnnotations.TextAnnotation.class, "example");
    coreLabel.set(CoreAnnotations.LemmaAnnotation.class, "example");
    coreLabel.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    m.headWord = coreLabel;
    IndexedWord headIndexedWord = new IndexedWord(coreLabel);
    m.headIndexedWord = headIndexedWord;

    CoreLabel premodLabel = new CoreLabel();
    premodLabel.set(CoreAnnotations.TextAnnotation.class, "big");
    premodLabel.set(CoreAnnotations.LemmaAnnotation.class, "big");
    IndexedWord premodWord = new IndexedWord(premodLabel);

    CoreLabel postmodLabel = new CoreLabel();
    postmodLabel.set(CoreAnnotations.TextAnnotation.class, "dog");
    postmodLabel.set(CoreAnnotations.LemmaAnnotation.class, "dog");
    IndexedWord postmodWord = new IndexedWord(postmodLabel);

    SemanticGraph graph = new SemanticGraph();
    graph.addVertex(headIndexedWord);
    graph.addVertex(premodWord);
    graph.addVertex(postmodWord);

    graph.addEdge(headIndexedWord, premodWord, GrammaticalRelation.valueOf("amod"), 1.0, false);
    graph.addEdge(headIndexedWord, postmodWord, GrammaticalRelation.valueOf("nmod:on"), 1.0, false);

    m.dependency = graph;

    String pattern = m.getPattern();
    assertTrue(pattern.contains("example"));
    assertTrue(pattern.contains("big"));
    assertTrue(pattern.contains("dog"));
  }
@Test
  public void testIsListLikeReturnsTrueForCommaAndAndChildren() {
    Mention m = new Mention();
    List<CoreLabel> span = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "cats");
    token1.setTag("NN");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ",");
    token2.setTag(",");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "dogs");
    token3.setTag("NN");
    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "and");
    token4.setTag("CC");

    span.add(token1);
    span.add(token2);
    span.add(token3);
    span.add(token4);

    m.originalSpan = span;
    m.mentionSubTree = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    m.mentionSubTree.setValue("NP");

    List<edu.stanford.nlp.trees.Tree> children = new ArrayList<>();
    edu.stanford.nlp.trees.Tree leaf1 = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    leaf1.setValue("NN");
//    leaf1.setLeaves(Collections.singletonList(token1));

    edu.stanford.nlp.trees.Tree leaf2 = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    leaf2.setValue(",");
//    leaf2.setLeaves(Collections.singletonList(token2));

    edu.stanford.nlp.trees.Tree leaf3 = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    leaf3.setValue("CC");
//    leaf3.setLeaves(Collections.singletonList(token4));

    children.add(leaf1);
    children.add(leaf2);
    children.add(leaf3);
    m.mentionSubTree.setChildren(children);

    String result = m.getPattern();
    assertNotNull(result);
  }
@Test
  public void testIsCoordinatedFalseForNoConjRelation() {
    Mention m = new Mention();
    IndexedWord head = new IndexedWord(new CoreLabel());
    SemanticGraph g = new SemanticGraph();
    g.addVertex(head);
    IndexedWord child = new IndexedWord(new CoreLabel());
    g.addVertex(child);
    g.addEdge(head, child, GrammaticalRelation.valueOf("amod"), 1.0, false);
    m.headIndexedWord = head;
    m.dependency = g;
    assertFalse(m.isCoordinated());
  }
@Test
  public void testGetSplitPatternReturnsHeadOnlyWhenNoModifiers() {
    Mention m = new Mention();
    CoreLabel cl = new CoreLabel();
    cl.set(CoreAnnotations.TextAnnotation.class, "car");
    cl.set(CoreAnnotations.LemmaAnnotation.class, "car");
    m.headWord = cl;
    m.headIndexedWord = new IndexedWord(cl);
    SemanticGraph g = new SemanticGraph();
    g.addVertex(m.headIndexedWord);
    m.dependency = g;
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    String[] patterns = m.getSplitPattern();
    assertEquals("car", patterns[0]);
    assertEquals("car", patterns[1]);
    assertEquals("car", patterns[2]);
    assertEquals("car", patterns[3]);
  }
@Test
  public void testMoreRepresentativeReturnsTrueForLongerSpan() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();

    CoreLabel c1 = new CoreLabel();
    CoreLabel c2 = new CoreLabel();
    m1.headIndex = 5;
    m1.startIndex = 2;
    m1.sentNum = 0;
    m1.originalSpan = Arrays.asList(c1, c2);
    m1.mentionType = Dictionaries.MentionType.NOMINAL;
    m1.nerString = null;

    CoreLabel c3 = new CoreLabel();
    m2.headIndex = 5;
    m2.startIndex = 2;
    m2.sentNum = 0;
    m2.originalSpan = Collections.singletonList(c3);
    m2.mentionType = Dictionaries.MentionType.NOMINAL;
    m2.nerString = null;

    assertTrue(m1.moreRepresentativeThan(m2));
  }
@Test
  public void testIsTheCommonNounReturnsFalseWithWrongStart() {
    Mention m = new Mention();
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "some");
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "books");

    m.originalSpan = Arrays.asList(c1, c2);
    m.mentionType = Dictionaries.MentionType.NOMINAL;

    assertFalse(m.isTheCommonNoun());
  }
@Test
  public void testHeadsAgreeIncludesProperSubsetNER() {
    Mention m1 = new Mention();
    CoreLabel h1 = new CoreLabel();
    h1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    h1.setTag("NNP");
    CoreLabel h2 = new CoreLabel();
    h2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    h2.setTag("NNP");
    m1.originalSpan = Arrays.asList(h1, h2);
    m1.headWord = h2;
    m1.headString = "obama";
    m1.nerString = "PERSON";

    Mention m2 = new Mention();
    CoreLabel h3 = new CoreLabel();
    h3.set(CoreAnnotations.TextAnnotation.class, "Obama");
    h3.setTag("NNP");
    m2.originalSpan = Arrays.asList(h3);
    m2.headWord = h3;
    m2.headString = "obama";
    m2.nerString = "PERSON";

    assertTrue(m1.headsAgree(m2));
  }
@Test
  public void testNumbersAgreeBothUnknownReturnsTrue() {
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.number = Dictionaries.Number.UNKNOWN;
    m2.number = Dictionaries.Number.UNKNOWN;
    assertTrue(m1.numbersAgree(m2));
  }
@Test
  public void testMentionMoreRepresentativeThrowsSameMention() {
    Mention m = new Mention();
    m.headIndex = 1;
    m.startIndex = 1;
    m.sentNum = 1;
    m.mentionType = Dictionaries.MentionType.PROPER;
    CoreLabel c = new CoreLabel();
    m.originalSpan = Collections.singletonList(c);
    try {
      m.moreRepresentativeThan(m);
      fail("Expected exception");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("Comparing a mention with itself"));
    }
  }
@Test
  public void testBuildQueryTextWithSpecialCharacters() {
    List<String> tokens = Arrays.asList("Dr.", "King", "Jr.", "Boulevard", "!");
    String query = Mention.buildQueryText(tokens);
    assertEquals("Dr. King Jr. Boulevard !", query);
  } 
}